const appVer = '0.0.1';
const alexa_api = require('./alexa-api');
const reqPromise = require("request-promise");
const logger = require('./logging');
const express = require('express');
const bodyParser = require('body-parser');
const os = require('os');
const configFile = require('./config-file');
const fs = require('fs');
const logDir = 'logs';
const app = express();

// Create the log directory if it does not exist
if (!fs.existsSync(logDir)) {
    fs.mkdirSync(logDir);
}

const user = configFile.user;
const password = configFile.password;
const url = configFile.url;

const serverPort = configFile.serverPort || 8091;
var savedConfig = {};
var command = {};
var urlencodedParser = bodyParser.urlencoded({
    extended: false
});
var smartThingsHubIP = configFile.smartThingsHubIP;
var serviceStartTime = Date.now(); //Returns time in millis
var refreshSeconds = 60;
var eventCount = 0;
var echoDevices = {};
var echoDeviceStates = {}

async function buildEchoDeviceMap(eDevData) {
    // console.log('eDevData: ', eDevData);
    let promises = [];
    try {
        let removeKeys = ['appDeviceList', 'charging', 'clusterMembers', 'essid', 'macAddress', 'parentClusters', 'deviceTypeFriendlyName', 'registrationId', 'remainingBatteryLevel', 'postalCode', 'language'];
        for (const dev in eDevData) {
            if (eDevData[dev].deviceFamily === 'ECHO' || eDevData[dev].deviceFamily === 'KNIGHT') {
                for (const item in removeKeys) {
                    delete eDevData[dev][removeKeys[item]];
                }
                echoDevices[eDevData[dev].serialNumber] = eDevData[dev];
                let devState = await getDeviceStateInfo(eDevData[dev].serialNumber);
                echoDevices[eDevData[dev].serialNumber].playerState = devState;
            }
        }
    } catch (err) {
        console.log('buildEchoDeviceMap ERROR:', err);
    }
}

function getDeviceStateInfo(deviceId) {
    return new Promise(resolve => {
        alexa_api.getState(deviceId, savedConfig, function(err, resp) {
            resolve(resp.playerInfo || {});
        });
    });
}

function startWebServer() {
    alexa_api.login(user, password, url, function(error, response, config) {
        savedConfig = config;
        // console.log('error:', error);
        console.log('response: ', response);
        // console.log('config: ', config);
        if (config.devicesArray) {
            buildEchoDeviceMap(config.devicesArray.devices)
                .then(function(devOk) {
                    if (Object.keys(echoDevices).length) {
                        // console.log(echoDevices);
                        sendDeviceDataToST(echoDevices);
                        tsLogger("** Device Data Refresh Scheduled for Every (" + refreshSeconds + ' sec) **');
                        setInterval(scheduledDataUpdates, refreshSeconds * 1000);
                    }
                })
                .catch(function(err) {
                    console.log(err);
                });
        }
        app.listen(serverPort, function() {
            tsLogger('Echo Manager Service (v' + appVer + ') is Running at (IP: ' + getIPAddress() + ' | Port: ' + serverPort + ') | ProcessId: ' + process.pid);
        });
    });

    app.post('/alexa-tts', urlencodedParser, function(req, res) {
        let hubAct = (req.headers.tts !== undefined || req.headers.deviceserialnumber !== undefined);
        let tts = req.body.tts || req.headers.tts;
        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.deviceserialnumber;
        tsLogger('++ Received a Send TTS Request for Device: ' + deviceSerialNumber + ' | Message: ' + tts + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
        alexa_api.setTTS(tts, deviceSerialNumber, savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-getDevices', urlencodedParser, function(req, res) {
        console.log('++ Received a getDevices Request...  ++');
        alexa_api.getDevices(savedConfig, function(error, response) {
            buildEchoDeviceMap(response.devices)
                .then(function(devOk) {
                    res.send(echoDevices);
                })
                .catch(function(err) {
                    res.send(null);
                });
        });
    });

    app.post('/alexa-getState', urlencodedParser, function(req, res) {
        let hubAct = (req.headers.deviceserialnumber != undefined);
        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.echodeviceid;
        console.log('++ Received a Device State Request for Device: ' + deviceSerialNumber + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
        alexa_api.getState(deviceSerialNumber, savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-getActivities', urlencodedParser, function(req, res) {
        console.log('got request for getActivities');
        alexa_api.getActivities(savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-setMedia', urlencodedParser, function(req, res) {
        var volume = req.body.volume;
        var deviceSerialNumber = req.body.deviceSerialNumber;
        if (volume) {
            command = {
                type: 'VolumeLevelCommand',
                volumeLevel: parseInt(volume)
            };
        } else {
            command = {
                type: req.body.command
            };
        }
        console.log('got set media message with command: ' + command + ' for device: ' + deviceSerialNumber);
        alexa_api.setMedia(command, deviceSerialNumber, savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-setBluetooth', urlencodedParser, function(req, res) {
        var mac = req.body.mac;
        var deviceSerialNumber = req.body.deviceSerialNumber;
        console.log('got set bluetooth  message with mac: ' + mac + ' for device: ' + deviceSerialNumber);
        alexa_api.setBluetoothDevice(mac, deviceSerialNumber, savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-getBluetooth', urlencodedParser, function(req, res) {
        console.log('got get bluetootha message');
        alexa_api.getBluetoothDevices(savedConfig, function(error, response) {
            res.send(response);
        });
    });

    app.post('/alexa-disconnectBluetooth', urlencodedParser, function(req, res) {
        var deviceSerialNumber = req.body.deviceSerialNumber;
        console.log('got set bluetooth disconnect for device: ' + deviceSerialNumber);
        alexa_api.disconnectBluetoothDevice(deviceSerialNumber, savedConfig, function(error, response) {
            res.send(response);
        });
    });

    //Returns Status of Service
    app.post('/sendStatusUpdate', urlencodedParser, function(req, res) {
        tsLogger('++ SmartThings is Requesting Device Data Update... | PID: ' + process.pid + ' ++');
        res.send(0);
        sendStatusUpdateToST(alexa_api);
    });

    app.post('/updateSettings', function(req, res) {
        tsLogger('** Settings Update Received from SmartThings **');
        if (req.headers.refreshseconds !== undefined && parseInt(req.headers.refreshseconds) !== refreshSeconds) {
            tsLogger('++ Changed Setting (refreshSeconds) | New Value: (' + req.headers.refreshseconds + ') | Old Value: (' + refreshSeconds + ') ++');
            refreshSeconds = parseInt(req.headers.refreshseconds);
            clearInterval(scheduledDataUpdates);
            tsLogger("** Device Data Refresh Schedule Changed to Every (" + refreshSeconds + ' sec) **');
            setInterval(scheduledDataUpdates, refreshSeconds * 1000);
        }
        if (req.headers.smartthingshubip !== undefined && req.headers.smartthingshubip !== smartThingsHubIP) {
            tsLogger('++ Changed Setting (smartThingsHubIP) | New Value: (' + req.headers.smartthingshubip + ') | Old Value: (' + smartThingsHubIP + ') ++');
            smartThingsHubIP = req.headers.smartthingshubip;
        }
    });

    process.stdin.resume(); //so the program will not close instantly
    //do something when app is closing
    process.on('exit', exitHandler.bind(null, {
        exit: true
    }));
}

function sendDeviceDataToST(eDevData) {
    buildEchoDeviceMap(eDevData)
        .then(function(devOk) {
            let options = {
                method: 'POST',
                uri: 'http://' + smartThingsHubIP + ':39500/event',
                headers: {
                    'evtSource': 'Echo_Speaks',
                    'evtType': 'sendStatusData'
                },
                body: {
                    'echoDevices': echoDevices,
                    'timestamp': Date.now(),
                    'serviceInfo': {
                        'version': appVer,
                        'sessionEvts': eventCount,
                        'startupDt': getServiceUptime(),
                        'ip': getIPAddress(),
                        'port': serverPort,
                        // 'hostInfo': getHostInfo(),
                        'config': {
                            'refreshSeconds': refreshSeconds,
                            'smartThingsHubIP': smartThingsHubIP
                        }
                    }
                },
                json: true
            };
            reqPromise(options)
                .then(function() {
                    eventCount++;
                    tsLogger('** Sent Echo Speaks Data to SmartThings Hub Successfully! **');
                })
                .catch(function(err) {
                    console.log("ERROR: Unable to connect to SmartThings Hub: " + err.message);
                });
        })
        .catch(function(err) {
            console.log(err);
        });
}

function sendStatusUpdateToST(self) {
    self.getDevices(savedConfig, function(error, response) {
        buildEchoDeviceMap(response.devices)
            .then(function(devOk) {
                let options = {
                    method: 'POST',
                    uri: 'http://' + smartThingsHubIP + ':39500/event',
                    headers: {
                        'evtSource': 'Echo_Speaks',
                        'evtType': 'sendStatusData'
                    },
                    body: {
                        'echoDevices': echoDevices,
                        'timestamp': Date.now(),
                        'serviceInfo': {
                            'version': appVer,
                            'sessionEvts': eventCount,
                            'startupDt': getServiceUptime(),
                            'ip': getIPAddress(),
                            'port': serverPort,
                            // 'hostInfo': getHostInfo(),
                            'config': {
                                'refreshSeconds': refreshSeconds,
                                'smartThingsHubIP': smartThingsHubIP
                            }
                        }
                    },
                    json: true
                };
                // console.log('echoDevices (statUpd):', echoDevices);
                reqPromise(options)
                    .then(function() {
                        eventCount++;
                        tsLogger('** Sent Echo Speaks Data to SmartThings Hub Successfully! **');
                    })
                    .catch(function(err) {
                        console.log("ERROR: Unable to connect to SmartThings Hub: " + err.message);
                    });
            })
            .catch(function(err) {
                console.log(err);
            });
    });
}

function tsLogger(msg) {
    let dt = new Date().toLocaleString();
    console.log(dt + ' | ' + msg);
}

function exitHandler(options, err) {
    console.log('exitHandler: (PID: ' + process.pid + ')', options, err);
    clearInterval(scheduledDataUpdates);
    if (options.cleanup) {
        tsLogger('exitHandler: ', 'ClosedByUserConsole');
    } else if (err) {
        tsLogger('exitHandler error', err);
        if (options.exit) process.exit(1);
    }
    process.exit();
}

function getIPAddress() {
    var interfaces = os.networkInterfaces();
    for (var devName in interfaces) {
        var iface = interfaces[devName];
        for (var i = 0; i < iface.length; i++) {
            var alias = iface[i];
            if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal)
                return alias.address;
        }
    }
    return '0.0.0.0';
}

function getServiceUptime() {
    var now = Date.now();
    var diff = (now - serviceStartTime) / 1000;
    //logger.debug("diff: "+ diff);
    return getHostUptimeStr(diff);
}

function getHostUptimeStr(time) {
    var years = Math.floor(time / 31536000);
    time -= years * 31536000;
    var months = Math.floor(time / 31536000);
    time -= months * 2592000;
    var days = Math.floor(time / 86400);
    time -= days * 86400;
    var hours = Math.floor(time / 3600);
    time -= hours * 3600;
    var minutes = Math.floor(time / 60);
    time -= minutes * 60;
    var seconds = parseInt(time % 60, 10);
    return {
        'y': years,
        'mn': months,
        'd': days,
        'h': hours,
        'm': minutes,
        's': seconds
    };
}

function scheduledDataUpdates() {
    sendStatusUpdateToST(alexa_api);
}

tsLogger('** Echo Manager Service is Starting Up!  Takes about 10-15 seconds before it\'s available... **');
startWebServer();