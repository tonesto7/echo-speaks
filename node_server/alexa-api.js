const request = require('request');
const alexaCookie = require('alexa-cookie2');
const dateFormat = require('dateformat');

const alexaOptions = { // options is optional at all
    logger: console.log, // optional: Logger instance to get (debug) logs
    amazonPage: 'amazon.com', // optional: possible to use with different countries, default is 'amazon.de'
    alexaServiceHost: 'pitangui.amazon.com', // optional: possible to use with different countries, default is 'layla.amazon.de'
    acceptLanguage: 'en-US', // optional: webpage language, should match to amazon-Page, default is 'de-DE'
    userAgent: '...', // optional: own userAgent to use for all request, overwrites default one
    setupProxy: true, // optional: should the library setup a proxy to get cookie when automatic way did not worked? Default false!
    proxyOwnIp: '127.0.0.1', // required if proxy enabled: provide own IP or hostname to later access the proxy. needed to setup all rewriting and proxy stuff internally
    proxyPort: 3456, // optional: use this port for the proxy, default is 0 means random port is selected
    proxyListenBind: '0.0.0.0', // optional: set this to bind the proxy to a special IP, default is '0.0.0.0'
    proxyLogLevel: 'info' // optional: Loglevel of Proxy, default 'warn'
};

var login = function(userName, password, alexa_url, callback) {
    var devicesArray = [];
    var deviceSerialNumber;
    var deviceType;
    var deviceOwnerCustomerId;
    var config = {};

    alexaCookie.generateAlexaCookie(userName, password, alexaOptions, function(err, result) {
        if (err) {
            console.log('error: ', err);
            callback(error, 'There was an error', null);
        } else {
            // IMPORTANT: can be called multiple times!! As soon as a new cookie is fetched or an error happened. Consider that!
            console.log('cookie: ' + result.cookie || undefined);
            console.log('csrf: ' + result.csrf || undefined);
            if (result && result.csrf) {
                alexaCookie.stopProxyServer();
                config.devicesArray = devicesArray;
                config.cookies = result.strCookies;
                config.csrf = result.csrf;
                config.deviceSerialNumber = deviceSerialNumber;
                config.deviceType = deviceType;
                config.deviceOwnerCustomerId = deviceOwnerCustomerId;
                config.alexaURL = alexa_url;
                callback(null, 'Logged in', config);
            } else {
                callback(true, 'There was an error getting authentication', null);
            }
        }
    });
};

var setReminder = function(message, datetime, deviceSerialNumber, config, callback) {
    var now = new Date();
    var createdDate = now.getTime();
    var addSeconds = new Date(createdDate + 1 * 60000); // one minute afer the current time
    var alarmTime = addSeconds.getTime();
    if (datetime) {
        var datetimeDate = new Date(dateFormat(datetime));
        alarmTime = datetimeDate.getTime();
    }
    var originalTime = dateFormat(alarmTime, 'HH:MM:00.000');
    var originalDate = dateFormat(alarmTime, 'yyyy-mm-dd');
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });

    request({
        method: 'PUT',
        url: config.alexaURL + '/api/notifications/createReminder',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        },
        json: {
            type: 'Reminder',
            status: 'ON',
            alarmTime: alarmTime,
            originalTime: originalTime,
            originalDate: originalDate,
            timeZoneId: null,
            reminderIndex: null,
            sound: null,
            deviceSerialNumber: device.deviceSerialNumber,
            deviceType: device.deviceType,
            recurringPattern: '',
            reminderLabel: message,
            isSaveInFlight: true,
            id: 'createReminder',
            isRecurring: false,
            createdDate: createdDate
        }
    }, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "status": "success"
            });
        } else {
            callback(error, {
                "status": "failure"
            });
        }
    });
};

var setTTS = function(message, deviceSerialNumber, config, callback) {
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });
    request({
        method: 'POST',
        url: config.alexaURL + '/api/behaviors/preview',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        },
        json: {
            "behaviorId": "PREVIEW",
            "sequenceJson": "{\"@type\":\"com.amazon.alexa.behaviors.model.Sequence\", \
        \"startNode\":{\"@type\":\"com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode\", \
        \"type\":\"Alexa.Speak\",\"operationPayload\":{\"deviceType\":\"" + device.deviceType + "\", \
        \"deviceSerialNumber\":\"" + device.deviceSerialNumber + "\",\"locale\":\"en-US\", \
        \"customerId\":\"" + device.deviceOwnerCustomerId + "\", \"textToSpeak\": \"" + message + "\"}}}",
            "status": "ENABLED"
        }
    }, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "status": "success"
            });
        } else {
            callback(error, {
                "status": "failure"
            });
        }
    });
};

var setMedia = function(command, deviceSerialNumber, config, callback) {
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });
    request({
        method: 'POST',
        url: config.alexaURL + '/api/np/command?deviceSerialNumber=' +
            device.deviceSerialNumber + '&deviceType=' + device.deviceType,
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        },
        json: command
    }, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "status": "success"
            });
        } else {
            callback(error, response);
        }
    });
};

var getDevices = function(config, callback) {
    request({
        method: 'GET',
        url: config.alexaURL + '/api/devices-v2/device',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        }
    }, function(error, response, body) {
        if (!error && response.statusCode === 200) {
            config.devicesArray = JSON.parse(body);
            callback(null, config.devicesArray);
        } else {
            callback(error, response);
        }
    });
};

var getState = function(deviceSerialNumber, config, callback) {
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });
    request({
        method: 'GET',
        url: config.alexaURL + '/api/np/player?deviceSerialNumber=' + device.deviceSerialNumber + '&deviceType=' + device.deviceType + '&screenWidth=2560',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        }
    }, function(error, response, body) {
        if (!error && response.statusCode === 200) {
            callback(null, JSON.parse(body));
        } else {
            callback(error, response);
        }
    });
};


var getBluetoothDevices = function(config, callback) {
    request({
        method: 'GET',
        url: config.alexaURL + '/api/bluetooth?cached=false',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        }
    }, function(error, response, body) {
        if (!error && response.statusCode === 200) {
            callback(null, JSON.parse(body));
        } else {
            callback(error, response);
        }
    });
};

var setBluetoothDevice = function(mac, deviceSerialNumber, config, callback) {
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });
    request({
        method: 'POST',
        url: config.alexaURL + '/api/bluetooth/pair-sink/' + device.deviceType + '/' + device.deviceSerialNumber,
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        },
        json: {
            bluetoothDeviceAddress: mac
        }
    }, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "message": "success"
            });
        } else {
            callback(error, response);
        }
    });
};

var disconnectBluetoothDevice = function(deviceSerialNumber, config, callback) {
    var device = {};
    config.devicesArray.devices.forEach(function(dev) {
        if (dev.serialNumber === deviceSerialNumber) {
            device.deviceSerialNumber = dev.serialNumber;
            device.deviceType = dev.deviceType;
            device.deviceOwnerCustomerId = dev.deviceOwnerCustomerId;
        }
    });
    request({
        method: 'POST',
        url: config.alexaURL + '/api/bluetooth/disconnect-sink/' + device.deviceType + '/' + device.deviceSerialNumber,
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        },
    }, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "message": "success"
            });
        } else {
            callback(error, response);
        }
    });
};

// function verifyLogin() {
//     String response = makeRequestAndReturnString(alexaServer + "/api/bootstrap?version=0");
//     Boolean result = response.contains("\"authenticated\":true");
//     if (result) {
//         verifyTime = new Date();
//         if (loginTime == null) {
//             loginTime = verifyTime;
//         }
//     }
//     return result;
// }

exports.login = login;
exports.setReminder = setReminder;
exports.setTTS = setTTS;
exports.setMedia = setMedia;
exports.getDevices = getDevices;
exports.getState = getState;
exports.getBluetoothDevices = getBluetoothDevices;
exports.setBluetoothDevice = setBluetoothDevice;
exports.disconnectBluetoothDevice = disconnectBluetoothDevice;