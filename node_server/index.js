const appVer = '0.2.0';
const alexa_api = require('./alexa-api');
const reqPromise = require("request-promise");
const logger = require('./logger');
const express = require('express');
const bodyParser = require('body-parser');
const os = require('os');
const editJsonFile = require("edit-json-file", {
    autosave: true
});
const homedir = require('os').homedir();
const dataFolder = homedir + '/.echo-speaks'
const configFile = editJsonFile(dataFolder + '/es_config.json');
const fs = require('fs');
const webApp = express();
const urlencodedParser = bodyParser.urlencoded({
    extended: false
});

// These the config variables
var configData = {};
var scheduledUpdatesActive = false;
var savedConfig = {};
var command = {};
var serviceStartTime = Date.now(); //Returns time in millis
var eventCount = 0;
var echoDevices = {};

function initLogsConfig() {
    logger.debug('homedir: ' + homedir);
    // Create the log directory if it does not exist
    if (!fs.existsSync(dataFolder)) {
        fs.mkdirSync(dataFolder);
    }
    if (!fs.existsSync(dataFolder + '/logs')) {
        fs.mkdirSync(dataFolder + '/logs');
    }
}

function loadConfig() {
    configData = configFile.get() || {};
    // console.log(configData);
    configData.serverPort = configData.serverPort || 8091;
    configData.refreshSeconds = configData.refreshSeconds || 60;
    configFile.save();
}

initLogsConfig()
loadConfig();

function startWebConfig() {
    webApp.listen(configData.serverPort, function() {
        logger.info('Echo Speaks Config Service (v' + appVer + ') is Running at (IP: ' + getIPAddress() + ' | Port: ' + configData.serverPort + ') | ProcessId: ' + process.pid);
        if (!configCheckOk()) {
            logger.warn('** Configurations Settings Missing... Please visit http://' + getIPAddress() + ':' + configData.serverPort + '/configWeb to configure settings...');
        }
    });
    webApp.use(function(req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        next();
    });
    webApp.get('/configWeb', function(req, res) {
        res.sendFile(__dirname + '/public/index.html');
    });
    webApp.get('/configData', function(req, res) {
        res.send(JSON.stringify(configData));
    });
    webApp.post('/configSave', function(req, res) {
        if (req.headers.user) {
            configFile.set('user', req.headers.user);
        };
        if (req.headers.password) {
            configFile.set('password', req.headers.password);
        };
        if (req.headers.smartthingshubip) {
            configFile.set('smartThingsHubIP', req.headers.smartthingshubip);
        };
        if (req.headers.url) {
            configFile.set('url', req.headers.url);
        };
        if (req.headers.refreshseconds) {
            configFile.set('refreshSeconds', parseInt(req.headers.refreshseconds));
        };
        // console.log('configData(set): ', configData);
        if (req.headers.user.length && req.headers.password.length && req.headers.smartthingshubip.length && req.headers.url.length && req.headers.refreshseconds.length) {
            configFile.save();
            loadConfig();
            res.send('done');
            if (configCheckOk()) {
                tsLogger("** Settings File Updated via Web Config **");
                if (!scheduledUpdatesActive) {
                    // startWebServer()
                    // startWebServerTest()
                }
            }
        } else {
            res.send('failed');
        }
    });
}

function startWebServerTest() {
    let loadWebApp = false;
    alexa_api.loginZombie(configData.user, configData.password, configData.url, configData.confirmCode, function(error, response, config) {
        logger.trace('Alexa Login Status: ', response);
        savedConfig = config;
        if (response === 'Logged in' && config.devicesArray) {
            buildEchoDeviceMap(config.devicesArray.devices)
                .then(function(devOk) {
                    // webApp.listen(configData.serverPort, function() {
                    logger.info('Echo Speaks Alexa API is Actively Running at (IP: ' + getIPAddress() + ' | Port: ' + configData.serverPort + ') | ProcessId: ' + process.pid);
                    // });
                    webApp.post('/alexa-tts', urlencodedParser, function(req, res) {
                        let hubAct = (req.headers.tts !== undefined || req.headers.deviceserialnumber !== undefined);
                        let tts = req.body.tts || req.headers.tts;
                        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.deviceserialnumber;
                        logger.info('++ Received a Send TTS Request for Device: ' + deviceSerialNumber + ' | Message: ' + tts + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
                        alexa_api.setTTS(tts, deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getDevices', urlencodedParser, function(req, res) {
                        logger.debug('++ Received a getDevices Request...  ++');
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

                    webApp.post('/alexa-getState', urlencodedParser, function(req, res) {
                        let hubAct = (req.headers.deviceserialnumber != undefined);
                        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.echodeviceid;
                        logger.debug('++ Received a Device State Request for Device: ' + deviceSerialNumber + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
                        alexa_api.getState(deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getActivities', urlencodedParser, function(req, res) {
                        logger.debug('got request for getActivities');
                        alexa_api.getActivities(savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-setMedia', urlencodedParser, function(req, res) {
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
                        logger.debug('got set media message with command: ' + command + ' for device: ' + deviceSerialNumber);
                        alexa_api.setMedia(command, deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-setBluetooth', urlencodedParser, function(req, res) {
                        var mac = req.body.mac;
                        var deviceSerialNumber = req.body.deviceSerialNumber;
                        logger.debug('got set bluetooth  message with mac: ' + mac + ' for device: ' + deviceSerialNumber);
                        alexa_api.setBluetoothDevice(mac, deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getBluetooth', urlencodedParser, function(req, res) {
                        logger.debug('got get bluetootha message');
                        alexa_api.getBluetoothDevices(savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-disconnectBluetooth', urlencodedParser, function(req, res) {
                        var deviceSerialNumber = req.body.deviceSerialNumber;
                        logger.debug('got set bluetooth disconnect for device: ' + deviceSerialNumber);
                        alexa_api.disconnectBluetoothDevice(deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    //Returns Status of Service
                    webApp.post('/sendStatusUpdate', urlencodedParser, function(req, res) {
                        logger.info('++ SmartThings is Requesting Device Data Update... | PID: ' + process.pid + ' ++');
                        res.send(0);
                        sendStatusUpdateToST(alexa_api);
                    });

                    webApp.post('/updateSettings', function(req, res) {
                        logger.info('** Settings Update Received from SmartThings **');
                        if (req.headers.refreshseconds !== undefined && parseInt(req.headers.refreshseconds) !== configData.refreshSeconds) {
                            logger.trace('++ Changed Setting (refreshSeconds) | New Value: (' + req.headers.refreshseconds + ') | Old Value: (' + configData.refreshSeconds + ') ++');
                            configData.refreshSeconds = parseInt(req.headers.refreshseconds);
                            configFile.set('refreshSeconds', parseInt(req.headers.refreshseconds));
                            clearInterval(scheduledDataUpdates);
                            logger.trace("** Device Data Refresh Schedule Changed to Every (" + configData.refreshSeconds + ' sec) **');
                            setInterval(scheduledDataUpdates, configData.refreshSeconds * 1000);
                        }
                        if (req.headers.smartthingshubip !== undefined && req.headers.smartthingshubip !== configData.smartThingsHubIP) {
                            logger.trace('++ Changed Setting (smartThingsHubIP) | New Value: (' + req.headers.smartthingshubip + ') | Old Value: (' + configData.smartThingsHubIP + ') ++');
                            configFile.set('smartThingsHubIP', req.headers.smartthingshubip);
                            configData.smartThingsHubIP = req.headers.smartthingshubip;
                        }
                        configFile.save();
                    });
                    if (Object.keys(echoDevices).length) {
                        // console.log(echoDevices);
                        sendDeviceDataToST(echoDevices);
                        logger.debug("** Device Data Refresh Scheduled for Every (" + configData.refreshSeconds + ' sec) **');
                        setInterval(scheduledDataUpdates, configData.refreshSeconds * 1000);
                        scheduledUpdatesActive = true;
                    }
                })
                .catch(function(err) {
                    console.log(err);
                });
        } else if (response === 'Confirmation-Required' && config.captcha !== undefined) {
            configData.captcha = config.captcha;
            logger.warn('** Login CAPTCHA Detected... Please visit http://' + getIPAddress() + ':' + configData.serverPort + '/configWeb to enter the info and complete the login!');
        }
    });
}

function startWebServer() {
    let loadWebApp = false;
    alexa_api.login(configData.user, configData.password, configData.url, function(error, response, config) {
        savedConfig = config;
        // console.log('error:', error);
        console.log('Alexa Login Status: ', response);
        // console.log('config: ', config);
        if (response === 'Logged in' && config.devicesArray) {
            buildEchoDeviceMap(config.devicesArray.devices)
                .then(function(devOk) {
                    // webApp.listen(configData.serverPort, function() {
                    tsLogger('Echo Speaks Alexa API is Actively Running at (IP: ' + getIPAddress() + ' | Port: ' + configData.serverPort + ') | ProcessId: ' + process.pid);
                    // });
                    webApp.post('/alexa-tts', urlencodedParser, function(req, res) {
                        let hubAct = (req.headers.tts !== undefined || req.headers.deviceserialnumber !== undefined);
                        let tts = req.body.tts || req.headers.tts;
                        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.deviceserialnumber;
                        tsLogger('++ Received a Send TTS Request for Device: ' + deviceSerialNumber + ' | Message: ' + tts + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
                        alexa_api.setTTS(tts, deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getDevices', urlencodedParser, function(req, res) {
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

                    webApp.post('/alexa-getState', urlencodedParser, function(req, res) {
                        let hubAct = (req.headers.deviceserialnumber != undefined);
                        let deviceSerialNumber = req.body.deviceSerialNumber || req.headers.echodeviceid;
                        console.log('++ Received a Device State Request for Device: ' + deviceSerialNumber + (hubAct ? ' | Source: (ST HubAction)' : '') + ' ++');
                        alexa_api.getState(deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getActivities', urlencodedParser, function(req, res) {
                        console.log('got request for getActivities');
                        alexa_api.getActivities(savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-setMedia', urlencodedParser, function(req, res) {
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

                    webApp.post('/alexa-setBluetooth', urlencodedParser, function(req, res) {
                        var mac = req.body.mac;
                        var deviceSerialNumber = req.body.deviceSerialNumber;
                        console.log('got set bluetooth  message with mac: ' + mac + ' for device: ' + deviceSerialNumber);
                        alexa_api.setBluetoothDevice(mac, deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-getBluetooth', urlencodedParser, function(req, res) {
                        console.log('got get bluetootha message');
                        alexa_api.getBluetoothDevices(savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    webApp.post('/alexa-disconnectBluetooth', urlencodedParser, function(req, res) {
                        var deviceSerialNumber = req.body.deviceSerialNumber;
                        console.log('got set bluetooth disconnect for device: ' + deviceSerialNumber);
                        alexa_api.disconnectBluetoothDevice(deviceSerialNumber, savedConfig, function(error, response) {
                            res.send(response);
                        });
                    });

                    //Returns Status of Service
                    webApp.post('/sendStatusUpdate', urlencodedParser, function(req, res) {
                        tsLogger('++ SmartThings is Requesting Device Data Update... | PID: ' + process.pid + ' ++');
                        res.send(0);
                        sendStatusUpdateToST(alexa_api);
                    });

                    webApp.post('/updateSettings', function(req, res) {
                        tsLogger('** Settings Update Received from SmartThings **');
                        if (req.headers.refreshseconds !== undefined && parseInt(req.headers.refreshseconds) !== configData.refreshSeconds) {
                            tsLogger('++ Changed Setting (refreshSeconds) | New Value: (' + req.headers.refreshseconds + ') | Old Value: (' + configData.refreshSeconds + ') ++');
                            configData.refreshSeconds = parseInt(req.headers.refreshseconds);
                            configFile.set('refreshSeconds', parseInt(req.headers.refreshseconds));
                            clearInterval(scheduledDataUpdates);
                            tsLogger("** Device Data Refresh Schedule Changed to Every (" + configData.refreshSeconds + ' sec) **');
                            setInterval(scheduledDataUpdates, configData.refreshSeconds * 1000);
                        }
                        if (req.headers.smartthingshubip !== undefined && req.headers.smartthingshubip !== configData.smartThingsHubIP) {
                            tsLogger('++ Changed Setting (smartThingsHubIP) | New Value: (' + req.headers.smartthingshubip + ') | Old Value: (' + configData.smartThingsHubIP + ') ++');
                            configFile.set('smartThingsHubIP', req.headers.smartthingshubip);
                            configData.smartThingsHubIP = req.headers.smartthingshubip;
                        }
                        configFile.save();
                    });
                    if (Object.keys(echoDevices).length) {
                        // console.log(echoDevices);
                        sendDeviceDataToST(echoDevices);
                        tsLogger("** Device Data Refresh Scheduled for Every (" + configData.refreshSeconds + ' sec) **');
                        setInterval(scheduledDataUpdates, configData.refreshSeconds * 1000);
                        scheduledUpdatesActive = true;
                    }
                })
                .catch(function(err) {
                    console.log(err);
                });
        }
    });
}

async function buildEchoDeviceMap(eDevData) {
    // console.log('eDevData: ', eDevData);
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
        logger.error('buildEchoDeviceMap ERROR:', err);
    }
}

function getDeviceStateInfo(deviceId) {
    return new Promise(resolve => {
        alexa_api.getState(deviceId, savedConfig, function(err, resp) {
            resolve(resp.playerInfo || {});
        });
    });
}

function sendDeviceDataToST(eDevData) {
    try {
        buildEchoDeviceMap(eDevData)
            .then(function(devOk) {
                let options = {
                    method: 'POST',
                    uri: 'http://' + configData.smartThingsHubIP + ':39500/event',
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
                            'port': configData.serverPort,
                            'config': {
                                'refreshSeconds': configData.refreshSeconds,
                                'smartThingsHubIP': configData.smartThingsHubIP
                            }
                        }
                    },
                    json: true
                };
                reqPromise(options)
                    .then(function() {
                        eventCount++;
                        logger.info('** Sent Echo Speaks Data to SmartThings Hub Successfully! **');
                    })
                    .catch(function(err) {
                        logger.error("ERROR: Unable to connect to SmartThings Hub: " + err.message);
                    });
            })
            .catch(function(err) {
                logger.error(err);
            });
    } catch (err) {
        logger.error('sendStatusUpdateToST Error: ', err.message);
    }
}

function sendStatusUpdateToST(self) {
    self.getDevices(savedConfig, function(error, response) {
        try {
            buildEchoDeviceMap(response.devices)
                .then(function(devOk) {
                    let options = {
                            method: 'POST',
                            uri: 'http://' + configData.smartThingsHubIP + ':39500/event',
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
                                    'port': configData.serverPort,
                                    'config': {
                                        'refreshSeconds': configData.refreshSeconds,
                                        'smartThingsHubIP': configData.smartThingsHubIP
                                    }
                                }
                            },
                            json: true
                        }
                        // console.log('echoDevices (statUpd):', echoDevices);
                    reqPromise(options)
                        .then(function() {
                            eventCount++;
                            logger.info('** Sent Echo Speaks Data to SmartThings Hub Successfully! **');
                        })
                        .catch(function(err) {
                            logger.error("ERROR: Unable to connect to SmartThings Hub: " + err.message);
                        });
                })
                .catch(function(err) {
                    logger.error('sendStatusUpdateToST Error: ', err);
                });
        } catch (err) {
            logger.error('sendStatusUpdateToST Error: ', err.message);
        }
    });
}

function scheduledDataUpdates() {
    sendStatusUpdateToST(alexa_api);
}

function configCheckOk() {
    return (configData.user === '' || configData.password === '' || configData.url === '') ? false : true
}

startWebConfig()
if (configCheckOk()) {
    logger.info('** Echo Speaks Web Service Starting Up! Takes about 10 seconds before it\'s available... **');
    // startWebServer();
    // startWebServerTest();
}

/*******************************************************************************
                            SYSTEM INFO FUNCTIONS
********************************************************************************/

function tsLogger(msg) {
    let dt = new Date().toLocaleString();
    console.log(dt + ' | ' + msg);
}

function getIPAddress() {
    let interfaces = os.networkInterfaces();
    for (const devName in interfaces) {
        let iface = interfaces[devName];
        for (var i = 0; i < iface.length; i++) {
            let alias = iface[i];
            if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal) {
                return alias.address;
            }
        }
    }
    return '0.0.0.0';
}

function getServiceUptime() {
    let now = Date.now();
    let diff = (now - serviceStartTime) / 1000;
    //logger.debug("diff: "+ diff);
    return getHostUptimeStr(diff);
}

function getHostUptimeStr(time) {
    let years = Math.floor(time / 31536000);
    time -= years * 31536000;
    let months = Math.floor(time / 31536000);
    time -= months * 2592000;
    let days = Math.floor(time / 86400);
    time -= days * 86400;
    let hours = Math.floor(time / 3600);
    time -= hours * 3600;
    let minutes = Math.floor(time / 60);
    time -= minutes * 60;
    let seconds = parseInt(time % 60, 10);
    return {
        'y': years,
        'mn': months,
        'd': days,
        'h': hours,
        'm': minutes,
        's': seconds
    };
}

/*******************************************************************************
                            PROCESS EXIT FUNCTIONS
********************************************************************************/
//so the program will not close instantly
process.stdin.resume();
//do something when app is closing
process.on('exit', exitHandler.bind(null, {
    cleanup: true
}));
//catches ctrl+c event
process.on('SIGINT', exitHandler.bind(null, {
    exit: true
}));
process.on('SIGUSR2', exitHandler.bind(null, {
    exit: true
}));
process.on('SIGHUP', exitHandler.bind(null, {
    exit: true
}));
process.on('SIGTERM', exitHandler.bind(null, {
    exit: true
}));
process.on('uncaughtException', exitHandler.bind(null, {
    exit: true
}));

function exitHandler(options, exitCode) {
    if (scheduledUpdatesActive) {
        clearInterval(scheduledDataUpdates);
    }
    if (options.cleanup) {
        console.log('clean');
    }
    if (exitCode || exitCode === 0) {
        console.log(exitCode);
    }
    if (options.exit) {
        process.exit();
    }
    console.log('graceful setting timeout for PID: ' + process.pid);
    setTimeout(function() {
        console.error("Could not close connections in time, forcefully shutting down");
        process.exit(1);
    }, 2 * 1000);
    // console.log('exitHandler: (PID: ' + process.pid + ')', options, err);
    // if (options.cleanup) {
    //     tsLogger('exitHandler: ClosedByUserConsole');
    // } else if (err) {
    //     tsLogger('exitHandler error' + err.message);
    //     if (options.exit) { process.exit(1); }
    // }
    // process.exit();
}