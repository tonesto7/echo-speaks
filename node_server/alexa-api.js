const request = require('request');
const Browser = require('zombie');
const Nightmare = require('nightmare');
const nightmare = Nightmare({
    show: false
});
const dateFormat = require('dateformat');

var zombieSession = undefined;

var loginZombie = async function(userName, password, alexa_url, confirmCode = undefined, callback) {

    let browser = new Browser({
        site: alexa_url
    });
    let captchaSrc = undefined;
    try {
        // browser.debug();
        await browser.visit('/');
        await browser.fill('email', userName);
        await browser.fill('password', password);
        await browser.check('rememberMe');
        await browser.pressButton('#auth-signin-button #signInSubmit');
        await browser.wait();
        const captcha = browser.querySelector('#auth-captcha-image');
        console.log(captcha.src);
        if (captcha.src !== undefined) {
            captchaSrc = encodeURI(captcha.src);
        }
        // const saveAuth = await browser.querySelector('label input[type="checkbox" name="rememberMe"]');
        // console.log('saveAuth: ', saveAuth);

        // await browser.fire('#auth-signin-button', 'click');
        // await browser.document.forms[0].submit();
        // console.log(await browser.html());
        // const captcha = browser.querySelector('#auth-captcha-image');
        // console.log(captcha.src);
        // const cookies = await browser.saveCookies()
        // console.log('logs: ', cookies);
        // console.log(await browser.visit('/api/devices-v2/device?cached=false'));
    } catch (error) {
        console.log('loginZombie: ' + error.message);
        callback(error, 'There was an error: ' + error.message, undefined);
    } finally {
        if (captchaSrc) {
            zombieSession = browser;
            callback(null, 'Confirmation-Required', { captcha: captchaSrc })
        } else {
            browser.destroy();
            callback(null, 'Logged in', {})
        };
    }
};


var login = function(userName, password, alexa_url, callback) {
    var devicesArray = [];
    var cookiesArray = [];
    var deviceSerialNumber;
    var deviceType;
    var deviceOwnerCustomerId;
    var strCookies = '';
    var csrf = '';
    var config = {};

    nightmare
        .goto(alexa_url)
        .type('#ap_email', userName)
        .type('#ap_password', password)
        .wait('label input[type="checkbox" name="rememberMe"]')
        .evaluate(function() {
            let checkbox = document.querySelector('label input[type="checkbox" name="rememberMe"]');
            if (checkbox) {
                console.log('checkbox: ', checkbox);
                checkbox.click();
                console.log('checkbox_after: ', checkbox);
            }
        })
        .click('#signInSubmit')
        .wait(1000)
        .goto(alexa_url + '/api/devices-v2/device')
        .wait()
        .evaluate(function() {
            return document.body.innerText;
        })
        .then(function(result) {
            devicesArray = JSON.parse(result);
        })
        .then(function() {
            return nightmare
                .cookies.get({
                    url: null
                })
                .end()
                .then(cookies => {
                    cookiesArray = cookies;
                });
        })
        .then(function() {
            cookiesArray.forEach(function(cookie) {
                strCookies += cookie.name + '=' + cookie.value + '; ';
                if (cookie.name === 'csrf') {
                    csrf = cookie.value;
                }
            });
            config.devicesArray = devicesArray;
            config.cookies = strCookies;
            config.csrf = csrf;
            config.deviceSerialNumber = deviceSerialNumber;
            config.deviceType = deviceType;
            config.deviceOwnerCustomerId = deviceOwnerCustomerId;
            config.alexaURL = alexa_url;
            callback(null, 'Logged in', config);
        })
        .catch(function(error) {
            callback(error, 'There was an error', null);
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

exports.login = login;
exports.loginZombie = loginZombie;
exports.setReminder = setReminder;
exports.setTTS = setTTS;
exports.setMedia = setMedia;
exports.getDevices = getDevices;
exports.getState = getState;
exports.getBluetoothDevices = getBluetoothDevices;
exports.setBluetoothDevice = setBluetoothDevice;
exports.disconnectBluetoothDevice = disconnectBluetoothDevice;