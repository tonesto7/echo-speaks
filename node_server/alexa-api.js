const request = require('request');
const logger = require('./logger');
const alexaCookie = require('./alexa-cookie/alexa-cookie');
const dateFormat = require('dateformat');
const editJsonFile = require("edit-json-file", {
    autosave: true
});
const dataFolder = require('os').homedir() + '/.echo-speaks';
const sessionFile = editJsonFile(dataFolder + '/session.json');

var alexaUrl = 'https://alexa.amazon.com';
var sessionData = sessionFile.get() || {};
sessionFile.save();

var clearSession = function() {
    sessionFile.unset('csrf');
    sessionFile.unset('cookie');
    sessionFile.save();
};

var alexaLogin = function(username, password, alexaOptions, callback) {
    var devicesArray = [];
    var deviceSerialNumber;
    var deviceType;
    var deviceOwnerCustomerId;
    var config = {};

    if (sessionData.csrf && sessionData.cookie) {
        config.devicesArray = devicesArray;
        config.cookies = sessionData.cookie;
        config.csrf = sessionData.csrf;
        config.deviceSerialNumber = deviceSerialNumber;
        config.deviceType = deviceType;
        config.deviceOwnerCustomerId = deviceOwnerCustomerId;
        config.alexaURL = alexaOptions.amazonPage;
        callback(null, 'Logged in', config);
    } else {
        alexaCookie.generateAlexaCookie(username, password, alexaOptions, function(err, result) {
            if (err && (err.message.startsWith('Login unsuccessful') || err.message.startsWith('Amazon-Login-Error:'))) {
                logger.debug('Please complete Amazon login by going here: (http://' + alexaOptions.proxyOwnIp + ':' + alexaOptions.serverPort + '/config)');
            } else if (err && !result) {
                logger.error('generateAlexaCookie: ' + err.message);
                callback(err, 'There was an error', null);
            } else if (result) {
                alexaUrl = 'https://alexa.' + alexaOptions.amazonPage;
                // IMPORTANT: can be called multiple times!! As soon as a new cookie is fetched or an error happened. Consider that!
                logger.debug('cookie: ' + result.cookie || undefined);
                logger.debug('csrf: ' + result.csrf || undefined);
                if (result && result.csrf && result.cookie) {
                    // alexaCookie.stopProxyServer();
                    if (sessionData['csrf'] === undefined || sessionData['csrf'] !== result.csrf) {
                        sessionFile.set('csrf', result.csrf);
                        sessionData['csrf'] = result.csrf;
                    }
                    if (sessionData['cookie'] === undefined || sessionData['cookie'] !== result.cookie) {
                        sessionFile.set('cookie', result.cookie);
                        sessionData['cookie'] = result.cookie;
                    }
                    sessionFile.save();
                    config.devicesArray = devicesArray;
                    config.cookies = sessionData.cookie;
                    config.csrf = sessionData.csrf;
                    config.deviceSerialNumber = deviceSerialNumber;
                    config.deviceType = deviceType;
                    config.deviceOwnerCustomerId = deviceOwnerCustomerId;
                    config.alexaURL = alexaOptions.amazonPage;
                    callback(null, 'Logged in', config);
                } else {
                    callback(true, 'There was an error getting authentication', null);
                    clearSession();
                }
            }
        });
    }
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
        url: alexaUrl + '/api/notifications/createReminder',
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
        url: alexaUrl + '/api/behaviors/preview',
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
        url: alexaUrl + '/api/np/command?deviceSerialNumber=' +
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
        url: alexaUrl + '/api/devices-v2/device',
        headers: {
            'Cookie': config.cookies,
            'csrf': config.csrf
        }
    }, function(error, response, body) {
        if (!error && response.statusCode === 200) {
            try {
                config.devicesArray = JSON.parse(body);
            } catch (e) {
                logger.error('getDevices Error: ' + e.message);
                config.devicesArray = [];
            }
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
        url: alexaUrl + '/api/np/player?deviceSerialNumber=' + device.deviceSerialNumber + '&deviceType=' + device.deviceType + '&screenWidth=2560',
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
        url: alexaUrl + '/api/bluetooth?cached=false',
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
        url: alexaUrl + '/api/bluetooth/pair-sink/' + device.deviceType + '/' + device.deviceSerialNumber,
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
        url: alexaUrl + '/api/bluetooth/disconnect-sink/' + device.deviceType + '/' + device.deviceSerialNumber,
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

exports.alexaLogin = alexaLogin;
exports.clearSession = clearSession;
exports.setReminder = setReminder;
exports.setTTS = setTTS;
exports.setMedia = setMedia;
exports.getDevices = getDevices;
exports.getState = getState;
exports.getBluetoothDevices = getBluetoothDevices;
exports.setBluetoothDevice = setBluetoothDevice;
exports.disconnectBluetoothDevice = disconnectBluetoothDevice;