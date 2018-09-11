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
        callback(null, 'Login Successful (Stored Session)', config);
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
                    alexaCookie.stopProxyServer();
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
                    callback(null, 'Login Successful', config);
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

var getDndStatus = function(_config, callback) {
    request({
        method: 'GET',
        url: alexaUrl + '/api/dnd/device-status-list',
        headers: {
            'Cookie': _config.cookies,
            'csrf': _config.csrf
        }
    }, function(error, response, body) {
        if (!error && response.statusCode === 200) {
            let items = [];
            try {
                let res = JSON.parse(body);
                if (Object.keys(res).length) {
                    if (res.doNotDisturbDeviceStatusList.length) {
                        items = res.doNotDisturbDeviceStatusList;
                    }
                }
            } catch (e) {
                logger.error('getDevices Error: ' + e.message);
            }
            callback(null, items);
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

var executeCommand = function(_cmdOpts, callback) {
    console.log(JSON.stringify(_cmdOpts));
    request(_cmdOpts, function(error, response) {
        if (!error && response.statusCode === 200) {
            callback(null, {
                "message": "success"
            });
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


/*

    // commands and states

    public List<Device> getDeviceList() throws IOException, URISyntaxException {
        String json = getDeviceListJson();
        JsonDevices devices = parseJson(json, JsonDevices.class);
        Device[] result = devices.devices;
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(result));
    }

    public String getDeviceListJson() throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(alexaServer + "/api/devices-v2/device?cached=false");
        return json;
    }

    public JsonPlayerState getPlayer(Device device) throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(alexaServer + "/api/np/player?deviceSerialNumber="
                + device.serialNumber + "&deviceType=" + device.deviceType + "&screenWidth=1440");
        JsonPlayerState playerState = parseJson(json, JsonPlayerState.class);
        return playerState;
    }

    public JsonMediaState getMediaState(Device device) throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(alexaServer + "/api/media/state?deviceSerialNumber="
                + device.serialNumber + "&deviceType=" + device.deviceType);
        JsonMediaState mediaState = parseJson(json, JsonMediaState.class);
        return mediaState;
    }

    public JsonBluetoothStates getBluetoothConnectionStates() {
        String json;
        try {
            json = makeRequestAndReturnString(alexaServer + "/api/bluetooth?cached=true");
        } catch (IOException | URISyntaxException e) {
            logger.debug("failed to get bluetooth state: {}", e.getMessage());
            return new JsonBluetoothStates();
        }
        JsonBluetoothStates bluetoothStates = parseJson(json, JsonBluetoothStates.class);
        return bluetoothStates;
    }

    public JsonPlaylists getPlaylists(Device device) throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(
                alexaServer + "/api/cloudplayer/playlists?deviceSerialNumber=" + device.serialNumber + "&deviceType="
                        + device.deviceType + "&mediaOwnerCustomerId=" + device.deviceOwnerCustomerId);
        JsonPlaylists playlists = parseJson(json, JsonPlaylists.class);
        return playlists;
    }

    public void command(Device device, String command) throws IOException, URISyntaxException {
        String url = alexaServer + "/api/np/command?deviceSerialNumber=" + device.serialNumber + "&deviceType="
                + device.deviceType;
        makeRequest("POST", url, command, true, true, null);
    }

    public void bluetooth(Device device, @Nullable String address) throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(address)) {
            // disconnect
            makeRequest("POST",
                    alexaServer + "/api/bluetooth/disconnect-sink/" + device.deviceType + "/" + device.serialNumber, "",
                    true, true, null);
        } else {
            makeRequest("POST",
                    alexaServer + "/api/bluetooth/pair-sink/" + device.deviceType + "/" + device.serialNumber,
                    "{\"bluetoothDeviceAddress\":\"" + address + "\"}", true, true, null);
        }
    }

    public void playRadio(Device device, @Nullable String stationId) throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(stationId)) {
            command(device, "{\"type\":\"PauseCommand\"}");
        } else {
            makeRequest("POST",
                    alexaServer + "/api/tunein/queue-and-play?deviceSerialNumber=" + device.serialNumber
                            + "&deviceType=" + device.deviceType + "&guideId=" + stationId
                            + "&contentType=station&callSign=&mediaOwnerCustomerId=" + device.deviceOwnerCustomerId,
                    "", true, true, null);
        }
    }

    public void playAmazonMusicTrack(Device device, @Nullable String trackId) throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(trackId)) {
            command(device, "{\"type\":\"PauseCommand\"}");
        } else {
            String command = "{\"trackId\":\"" + trackId + "\",\"playQueuePrime\":true}";
            makeRequest("POST",
                    alexaServer + "/api/cloudplayer/queue-and-play?deviceSerialNumber=" + device.serialNumber
                            + "&deviceType=" + device.deviceType + "&mediaOwnerCustomerId="
                            + device.deviceOwnerCustomerId + "&shuffle=false",
                    command, true, true, null);
        }
    }

    public void playAmazonMusicPlayList(Device device, @Nullable String playListId)
            throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(playListId)) {
            command(device, "{\"type\":\"PauseCommand\"}");
        } else {
            String command = "{\"playlistId\":\"" + playListId + "\",\"playQueuePrime\":true}";
            makeRequest("POST",
                    alexaServer + "/api/cloudplayer/queue-and-play?deviceSerialNumber=" + device.serialNumber
                            + "&deviceType=" + device.deviceType + "&mediaOwnerCustomerId="
                            + device.deviceOwnerCustomerId + "&shuffle=false",
                    command, true, true, null);
        }
    }

    public void textToSpeech(Device device, String text) throws IOException, URISyntaxException {
        Map<String, Object> parameters = new Hashtable<String, Object>();
        parameters.put("textToSpeak", text);
        executeSequenceCommand(device, "Alexa.Speak", parameters);
    }

    // commands: Alexa.Weather.Play, Alexa.Traffic.Play, Alexa.FlashBriefing.Play, Alexa.GoodMorning.Play,
    // Alexa.SingASong.Play, Alexa.TellStory.Play, Alexa.Speak (textToSpeach)
    public void executeSequenceCommand(Device device, String command, @Nullable Map<String, Object> parameters)
            throws IOException, URISyntaxException {
        JsonObject operationPayload = new JsonObject();
        operationPayload.addProperty("deviceType", device.deviceType);
        operationPayload.addProperty("deviceSerialNumber", device.serialNumber);
        operationPayload.addProperty("locale", "");
        operationPayload.addProperty("customerId", device.deviceOwnerCustomerId);
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (value instanceof String) {
                    operationPayload.addProperty(key, (String) value);
                } else if (value instanceof Number) {
                    operationPayload.addProperty(key, (Number) value);
                } else if (value instanceof Boolean) {
                    operationPayload.addProperty(key, (Boolean) value);
                } else if (value instanceof Character) {
                    operationPayload.addProperty(key, (Character) value);
                } else {
                    operationPayload.add(key, gson.toJsonTree(value));
                }
            }
        }

        JsonObject startNode = new JsonObject();
        startNode.addProperty("@type", "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode");
        startNode.addProperty("type", command);
        startNode.add("operationPayload", operationPayload);

        JsonObject sequenceJson = new JsonObject();
        sequenceJson.addProperty("@type", "com.amazon.alexa.behaviors.model.Sequence");
        sequenceJson.add("startNode", startNode);

        JsonStartRoutineRequest request = new JsonStartRoutineRequest();
        request.sequenceJson = gson.toJson(sequenceJson);
        String json = gson.toJson(request);

        makeRequest("POST", alexaServer + "/api/behaviors/preview", json, true, true, null);
    }

    public void startRoutine(Device device, String utterance) throws IOException, URISyntaxException {
        JsonAutomation found = null;
        String deviceLocale = "";
        for (JsonAutomation routine : getRoutines()) {
            Trigger[] triggers = routine.triggers;
            if (triggers != null && routine.sequence != null) {
                for (JsonAutomation.Trigger trigger : triggers) {
                    if (trigger == null) {
                        continue;
                    }
                    Payload payload = trigger.payload;
                    if (payload == null) {
                        continue;
                    }
                    if (StringUtils.equalsIgnoreCase(payload.utterance, utterance)) {
                        found = routine;
                        deviceLocale = payload.locale;
                        break;
                    }
                }
            }
        }
        if (found != null) {
            String sequenceJson = gson.toJson(found.sequence);

            JsonStartRoutineRequest request = new JsonStartRoutineRequest();
            request.behaviorId = found.automationId;

            // replace tokens

            // "deviceType":"ALEXA_CURRENT_DEVICE_TYPE"
            String deviceType = "\"deviceType\":\"ALEXA_CURRENT_DEVICE_TYPE\"";
            String newDeviceType = "\"deviceType\":\"" + device.deviceType + "\"";
            sequenceJson = sequenceJson.replace(deviceType.subSequence(0, deviceType.length()),
                    newDeviceType.subSequence(0, newDeviceType.length()));

            // "deviceSerialNumber":"ALEXA_CURRENT_DSN"
            String deviceSerial = "\"deviceSerialNumber\":\"ALEXA_CURRENT_DSN\"";
            String newDeviceSerial = "\"deviceSerialNumber\":\"" + device.serialNumber + "\"";
            sequenceJson = sequenceJson.replace(deviceSerial.subSequence(0, deviceSerial.length()),
                    newDeviceSerial.subSequence(0, newDeviceSerial.length()));

            // "customerId": "ALEXA_CUSTOMER_ID"
            String customerId = "\"customerId\":\"ALEXA_CUSTOMER_ID\"";
            String newCustomerId = "\"customerId\":\"" + device.deviceOwnerCustomerId + "\"";
            sequenceJson = sequenceJson.replace(customerId.subSequence(0, customerId.length()),
                    newCustomerId.subSequence(0, newCustomerId.length()));

            // "locale": "ALEXA_CURRENT_LOCALE"
            String locale = "\"locale\":\"ALEXA_CURRENT_LOCALE\"";
            String newlocale = StringUtils.isNotEmpty(deviceLocale) ? "\"locale\":\"" + deviceLocale + "\""
                    : "\"locale\":null";
            sequenceJson = sequenceJson.replace(locale.subSequence(0, locale.length()),
                    newlocale.subSequence(0, newlocale.length()));

            request.sequenceJson = sequenceJson;

            String requestJson = gson.toJson(request);
            makeRequest("POST", alexaServer + "/api/behaviors/preview", requestJson, true, true, null);
        } else {
            logger.warn("Routine {} not found", utterance);
        }
    }

    public JsonAutomation[] getRoutines() throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(alexaServer + "/api/behaviors/automations");
        JsonAutomation[] result = parseJson(json, JsonAutomation[].class);
        return result;
    }

    public JsonFeed[] getEnabledFlashBriefings() throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(alexaServer + "/api/content-skills/enabled-feeds");
        JsonEnabledFeeds result = parseJson(json, JsonEnabledFeeds.class);
        JsonFeed[] enabledFeeds = result.enabledFeeds;
        if (enabledFeeds != null) {
            return enabledFeeds;
        }
        return new JsonFeed[0];
    }

    public void setEnabledFlashBriefings(JsonFeed[] enabledFlashBriefing) throws IOException, URISyntaxException {
        JsonEnabledFeeds enabled = new JsonEnabledFeeds();
        enabled.enabledFeeds = enabledFlashBriefing;
        String json = gsonWithNullSerialization.toJson(enabled);
        makeRequest("POST", alexaServer + "/api/content-skills/enabled-feeds", json, true, true, null);
    }

    public JsonNotificationSound[] getNotificationSounds(Device device) throws IOException, URISyntaxException {
        String json = makeRequestAndReturnString(
                alexaServer + "/api/notification/sounds?deviceSerialNumber=" + device.serialNumber + "&deviceType="
                        + device.deviceType + "&softwareVersion=" + device.softwareVersion);
        JsonNotificationSounds result = parseJson(json, JsonNotificationSounds.class);
        JsonNotificationSound[] notificationSounds = result.notificationSounds;
        if (notificationSounds != null) {
            return notificationSounds;
        }
        return new JsonNotificationSound[0];
    }

    public JsonNotificationResponse notification(Device device, String type, @Nullable String label,
            @Nullable JsonNotificationSound sound) throws IOException, URISyntaxException {
        Date date = new Date(new Date().getTime());
        long createdDate = date.getTime();
        Date alarm = new Date(createdDate + 5000); // add 5 seconds, because amazon does not except calls for times in
                                                   // the past (compared with the server time)
        long alarmTime = alarm.getTime();

        JsonNotificationRequest request = new JsonNotificationRequest();
        request.type = type;
        request.deviceSerialNumber = device.serialNumber;
        request.deviceType = device.deviceType;
        request.createdDate = createdDate;
        request.alarmTime = alarmTime;
        request.reminderLabel = label;
        request.sound = sound;
        request.originalDate = new SimpleDateFormat("yyyy-MM-dd").format(alarm);
        request.originalTime = new SimpleDateFormat("HH:mm:ss.SSSS").format(alarm);
        request.type = type;
        request.id = "create" + type;

        String data = gsonWithNullSerialization.toJson(request);
        String response = makeRequestAndReturnString("PUT", alexaServer + "/api/notifications/createReminder", data,
                true, null);
        JsonNotificationResponse result = parseJson(response, JsonNotificationResponse.class);
        return result;
    }

    public void stopNotification(JsonNotificationResponse notification) throws IOException, URISyntaxException {
        makeRequestAndReturnString("DELETE", alexaServer + "/api/notifications/" + notification.id, null, true, null);
    }

    public JsonNotificationResponse getNotificationState(JsonNotificationResponse notification)
            throws IOException, URISyntaxException {
        String response = makeRequestAndReturnString("GET", alexaServer + "/api/notifications/" + notification.id, null,
                true, null);
        JsonNotificationResponse result = parseJson(response, JsonNotificationResponse.class);
        return result;
    }

    public List<JsonMusicProvider> getMusicProviders() {
        String response;
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Routines-Version", "1.1.201102");
            response = makeRequestAndReturnString("GET",
                    alexaServer + "/api/behaviors/entities?skillId=amzn1.ask.1p.music", null, true, headers);
        } catch (IOException | URISyntaxException e) {
            logger.warn("getMusicProviders fails: {}", e.getMessage());
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(response)) {
            return new ArrayList<>();
        }
        JsonMusicProvider[] result = parseJson(response, JsonMusicProvider[].class);
        return Arrays.asList(result);
    }

    public void playMusicVoiceCommand(Device device, String providerId, String voiceCommand)
            throws IOException, URISyntaxException {
        JsonPlaySearchPhraseOperationPayload payload = new JsonPlaySearchPhraseOperationPayload();
        payload.customerId = device.deviceOwnerCustomerId;
        payload.locale = "ALEXA_CURRENT_LOCALE";
        payload.musicProviderId = providerId;
        payload.searchPhrase = voiceCommand;

        String playloadString = gson.toJson(payload);

        JsonObject postValidataionJson = new JsonObject();

        postValidataionJson.addProperty("type", "Alexa.Music.PlaySearchPhrase");
        postValidataionJson.addProperty("operationPayload", playloadString);

        String postDataValidate = postValidataionJson.toString();

        String validateResultJson = makeRequestAndReturnString("POST",
                alexaServer + "/api/behaviors/operation/validate", postDataValidate, true, null);

        if (StringUtils.isNotEmpty(validateResultJson)) {
            JsonPlayValidationResult validationResult = parseJson(validateResultJson, JsonPlayValidationResult.class);
            JsonPlaySearchPhraseOperationPayload validatedOperationPayload = validationResult.operationPayload;
            if (validatedOperationPayload != null) {
                payload.sanitizedSearchPhrase = validatedOperationPayload.sanitizedSearchPhrase;
                payload.searchPhrase = validatedOperationPayload.searchPhrase;
            }
        }

        payload.locale = null;
        payload.deviceSerialNumber = device.serialNumber;
        payload.deviceType = device.deviceType;

        JsonObject sequenceJson = new JsonObject();
        sequenceJson.addProperty("@type", "com.amazon.alexa.behaviors.model.Sequence");
        JsonObject startNodeJson = new JsonObject();
        startNodeJson.addProperty("@type", "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode");
        startNodeJson.addProperty("type", "Alexa.Music.PlaySearchPhrase");
        startNodeJson.add("operationPayload", gson.toJsonTree(payload));
        sequenceJson.add("startNode", startNodeJson);

        JsonStartRoutineRequest startRoutineRequest = new JsonStartRoutineRequest();
        startRoutineRequest.sequenceJson = sequenceJson.toString();
        startRoutineRequest.status = null;

        String postData = gson.toJson(startRoutineRequest);
        makeRequest("POST", alexaServer + "/api/behaviors/preview", postData, true, true, null);
    }
}

*/

exports.alexaLogin = alexaLogin;
exports.clearSession = clearSession;
exports.setReminder = setReminder;
exports.setTTS = setTTS;
exports.setMedia = setMedia;
exports.getDevices = getDevices;
exports.getState = getState;
exports.getDndStatus = getDndStatus;
exports.executeCommand = executeCommand;
exports.getBluetoothDevices = getBluetoothDevices;
exports.setBluetoothDevice = setBluetoothDevice;
exports.disconnectBluetoothDevice = disconnectBluetoothDevice;