/**
 *	Echo Speaks Device (Hubitat ONLY)
 *
 *  Copyright 2018, 2019, 2020 Anthony Santilli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

String devVersion()  { return "3.6.0.0"}
String devModified() { return "2020-03-02" }
Boolean isBeta()     { return false }
Boolean isST()       { return (getPlatform() == "SmartThings") }
Boolean isWS()       { return false }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/drivers/echo-speaks-device.groovy") {
        // capability "Audio Mute" // Not Compatible with Hubitat
        capability "Audio Notification"
        // capability "Audio Track Data" // To support SharpTools.io Album Art feature
        capability "Audio Volume"
        capability "Music Player"
        capability "Notification"
        capability "Refresh"
        capability "Sensor"
        capability "Speech Synthesis"

        attribute "alarmVolume", "number"
        attribute "alexaNotifications", "JSON_OBJECT"
        attribute "alexaPlaylists", "JSON_OBJECT"
        // attribute "alexaGuardStatus", "string"
        attribute "alexaWakeWord", "string"
        attribute "btDeviceConnected", "string"
        attribute "btDevicesPaired", "JSON_OBJECT"
        attribute "currentAlbum", "string"
        attribute "currentStation", "string"
        attribute "deviceFamily", "string"
        attribute "deviceStatus", "string"
        attribute "deviceStyle", "string"
        attribute "deviceType", "string"
        attribute "doNotDisturb", "string"
        attribute "firmwareVer", "string"
        attribute "followUpMode", "string"
        attribute "lastCmdSentDt", "string"
        attribute "lastSpeakCmd", "string"
        attribute "lastSpokenToTime", "number"
        attribute "lastVoiceActivity", "string"
        attribute "lastUpdated", "string"
        attribute "mediaSource", "string"
        attribute "onlineStatus", "string"
        attribute "permissions", "string"
        attribute "supportedMusic", "string"
        attribute "trackImage", "string"
        attribute "trackImageHtml", "string"

        attribute "volume", "number"
        attribute "wakeWords", "enum"
        attribute "wifiNetwork", "string"
        attribute "wasLastSpokenToDevice", "string"

        command "playText", ["string"] //This command is deprecated in ST but will work
        command "playTextAndResume"
        command "playTrackAndResume"
        command "playTrackAndRestore"
        command "playTextAndRestore"
        command "replayText"
        command "doNotDisturbOn"
        command "doNotDisturbOff"
        // command "followUpModeOn"
        // command "followUpModeOff"
        command "setAlarmVolume", ["number"]
        command "resetQueue"
        command "playWeather", ["number", "number"]
        command "playSingASong", ["number", "number"]
        command "playFlashBrief", ["number", "number"]
        command "playFunFact", ["number", "number"]
        command "playTraffic", ["number", "number"]
        command "playJoke", ["number", "number"]
        command "playSoundByName", ["string", "number", "number"]
        command "playTellStory", ["number", "number"]
        command "sayGoodbye", ["number", "number"]
        command "sayGoodNight", ["number", "number"]
        command "sayBirthday", ["number", "number"]
        command "sayCompliment", ["number", "number"]
        command "sayGoodMorning", ["number", "number"]
        command "sayWelcomeHome", ["number", "number"]
        // command "playCannedRandomTts", ["string", "number", "number"]
        // command "playCannedTts", ["string", "string", "number", "number"]
        command "playAnnouncement", ["string", "number", "number"]
        command "playAnnouncement", ["string", "string", "number", "number"]
        command "playAnnouncementAll", ["string", "string"]
        command "playCalendarToday", ["number", "number"]
        command "playCalendarTomorrow", ["number", "number"]
        command "playCalendarNext", ["number", "number"]
        command "stopAllDevices"
        command "searchMusic", ["string", "string", "number", "number"]
        command "searchAmazonMusic", ["string", "number", "number"]
        command "searchAppleMusic", ["string", "number", "number"]
        command "searchPandora", ["string", "number", "number"]
        command "searchIheart", ["string", "number", "number"]
        command "searchSiriusXm", ["string", "number", "number"]
        command "searchSpotify", ["string", "number", "number"]
        // command "searchTidal", ["string", "number", "number"]
        command "searchTuneIn", ["string", "number", "number"]
        command "sendAlexaAppNotification", ["string"]
        command "executeSequenceCommand", ["string"]
        command "executeRoutineId", ["string"]
        command "createAlarm", ["string", "string", "string"]
        command "createReminder", ["string", "string", "string"]
        command "removeNotification", ["string"]
        command "setWakeWord", ["string"]
        command "renameDevice", ["string"]
        command "storeCurrentVolume"
        command "restoreLastVolume"
        command "togglePlayback"
        command "setVolumeAndSpeak", ["number", "string"]
        command "setVolumeSpeakAndRestore", ["number", "string", "number"]
        command "volumeUp"
        command "volumeDown"
        command "speechTest"
        command "sendTestAnnouncement"
        command "sendTestAnnouncementAll"
        command "getDeviceActivity"
        command "getBluetoothDevices"
        command "connectBluetooth", ["string"]
        command "disconnectBluetooth"
        command "removeBluetooth", ["string"]
        command "sendAnnouncementToDevices", ["string", "string", "string", "number", "number"]
    }

    preferences {
        section("Preferences") {
            input "logInfo", "bool", title: "Show Info Logs?",  required: false, defaultValue: true
            input "logWarn", "bool", title: "Show Warning Logs?", required: false, defaultValue: true
            input "logError", "bool", title: "Show Error Logs?",  required: false, defaultValue: true
            input "logDebug", "bool", title: "Show Debug Logs?", description: "Only leave on when required", required: false, defaultValue: false
            input "logTrace", "bool", title: "Show Detailed Logs?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false
            input "ignoreTimeoutErrors", "bool", required: false, title: "Don't show errors in the logs for request timeouts?", description: "", defaultValue: true

            input "disableQueue", "bool", required: false, title: "Don't Allow Queuing?", defaultValue: false
            input "disableTextTransform", "bool", required: false, title: "Disable Text Transform?", description: "This will attempt to convert items in text like temp units and directions like `WSW` to west southwest", defaultValue: false
            input "sendDevNotifAsAnnouncement", "bool", required: false, title: "Send Device Notifications as Announcements?", description: "", defaultValue: false
            input "maxVolume", "number", required: false, title: "Set Max Volume for this device", description: "There will be a delay of 30-60 seconds in getting the current volume level"
            input "ttsWordDelay", "number", required: true, title: "Speech queue delay (per character)", description: "Currently there is a 2 second delay per every 14 characters.", defaultValue: 2
            input "autoResetQueue", "number", required: false, title: "Auto reset queue (xx seconds) after last speak command", description: "This will reset the queue 3 minutes after last message sent.", defaultValue: 180
        }
    }
}

def installed() {
    logInfo("${device?.displayName} Executing Installed...")
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "deviceStatus", value: "stopped_echo_gen1")
    sendEvent(name: "trackDescription", value: "")
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    sendEvent(name: "doNotDisturb", value: false)
    sendEvent(name: "onlineStatus", value: "online")
    sendEvent(name: "followUpMode", value: false)
    sendEvent(name: "alarmVolume", value: 0)
    sendEvent(name: "alexaWakeWord", value: "ALEXA")
    sendEvent(name: "mediaSource", value: "")
    state?.doNotDisturb = false
    initialize()
    runIn(20, "postInstall")
}

def updated() {
    logTrace("${device?.displayName} Executing Updated()")
    state?.fullRefreshOk = true
    initialize()
}

def initialize() {
    state?.useLogDevice = (parent?.hasLogDevice() == true)
    logInfo("${device?.displayName} Executing initialize()")
    sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
    sendEvent(name: "DeviceWatch-Enroll", value: new groovy.json.JsonOutput().toJson([protocol: "cloud", scheme:"untracked"]), displayed: false)
    resetQueue()
    stateCleanup()
    if(checkMinVersion()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return; }
    schedDataRefresh(true)
    refreshData(true)
    //TODO: Add Queue cleanup task to schedule.  If q_speakingNow != true
    //TODO: Have the queue validated based on the last time it was processed and have it cleanup if it's been too long
}

def postInstall() {
    if(device?.currentState('level') == 0) { setLevel(30) }
    if(device?.currentState('alarmVolume') == 0) { setAlarmVolume(30) }
}

public triggerInitialize() { runIn(3, "initialize") }
String getEchoDeviceType() { return state?.deviceType ?: null }
String getEchoSerial() { return state?.serialNumber ?: null }

String getHealthStatus(lower=false) {
    String res = device?.getStatus()
    if(lower) { return res?.toString()?.toLowerCase() }
    return res as String
}

def getShortDevName(){
    return device?.displayName?.replace("Echo - ", "")
}

public setAuthState(authenticated) {
    state?.authValid = (authenticated == true)
    if(authenticated != true && state?.refreshScheduled) {
        removeCookies()
    }
}

public updateCookies(cookies) {
    logWarn("Cookies Update by Parent.  Re-Initializing Device in 5 Seconds...")
    state?.cookie = cookies
    state?.authValid = true
    runIn(5, "initialize")
}

public removeCookies(isParent=false) {
    logWarn("Cookie Authentication Cleared by ${isParent ? "Parent" : "Device"} | Scheduled Refreshes also cancelled!")
    unschedule("refreshData")
    state?.cookie = null
    state?.authValid = false
    state?.refreshScheduled = false
}

Boolean isAuthOk(noLogs=false) {
    if(state?.authValid != true) {
        if(state?.refreshScheduled) { unschedule("refreshData"); state?.refreshScheduled = false; }
        if(state?.cookie != null) {
            if(!noLogs) { logWarn("Echo Speaks Authentication is no longer valid... Please login again and commands will be allowed again!!!", true) }
            state?.remove("cookie")
        }
        return false
    } else { return true }
}

Boolean isCommandTypeAllowed(String type, noLogs=false) {
    Boolean isOnline = (device?.currentValue("onlineStatus") == "online")
    if(!isOnline) { if(!noLogs) { logWarn("Commands NOT Allowed! Device is currently (OFFLINE) | Type: (${type})", true) }; return false; }
    if(!isAuthOk(noLogs)) { return false }
    if(!getAmazonDomain()) { if(!noLogs) { logWarn("amazonDomain State Value Missing: ${getAmazonDomain()}", true) }; return false }
    if(!state?.cookie || !state?.cookie?.cookie || !state?.cookie?.csrf) { if(!noLogs) { logWarn("Amazon Cookie State Values Missing: ${state?.cookie}", true) }; setAuthState(false); return false }
    if(!state?.serialNumber) { if(!noLogs) { logWarn("SerialNumber State Value Missing: ${state?.serialNumber}", true) }; return false }
    if(!state?.deviceType) { if(!noLogs) { logWarn("DeviceType State Value Missing: ${state?.deviceType}", true) }; return false }
    if(!state?.deviceOwnerCustomerId) { if(!noLogs) { logWarn("OwnerCustomerId State Value Missing: ${state?.deviceOwnerCustomerId}", true) }; return false; }
    if(state?.isSupportedDevice == false) { logWarn("You are using an Unsupported/Unknown Device all restrictions have been removed for testing! If commands function please report device info to developer", true); return true; }
    if(!type || state?.permissions == null) { if(!noLogs) { logWarn("Permissions State Object Missing: ${state?.permissions}", true) }; return false }
    if(device?.currentValue("doNotDisturb") == "true" && (!(type in ["volumeControl", "alarms", "reminders", "doNotDisturb", "wakeWord", "bluetoothControl", "mediaPlayer"]))) { if(!noLogs) { logWarn("All Voice Output Blocked... Do Not Disturb is ON", true) }; return false }
    if(state?.permissions?.containsKey(type) && state?.permissions[type] == true) { return true }
    else {
        String warnMsg = null
        switch(type) {
            case "TTS":
                warnMsg = "OOPS... Text to Speech is NOT Supported by this Device!!!"
                break
            case "announce":
                warnMsg = "OOPS... Announcements are NOT Supported by this Device!!!"
                break
            case "followUpMode":
                warnMsg = "OOPS... Follow-Up Mode is NOT Supported by this Device!!!"
                break
            case "mediaPlayer":
                warnMsg = "OOPS... Media Player Controls are NOT Supported by this Device!!!"
                break
            case "volumeControl":
                warnMsg = "OOPS... Volume Control is NOT Supported by this Device!!!"
                break
            case "bluetoothControl":
                warnMsg = "OOPS... Bluetooth Control is NOT Supported by this Device!!!"
                break
            case "alarms":
                warnMsg = "OOPS... Alarm Notification are NOT Supported by this Device!!!"
                break
            case "reminders":
                warnMsg = "OOPS... Reminders Notifications are NOT Supported by this Device!!!"
                break
            case "doNotDisturb":
                warnMsg = "OOPS... Do Not Disturb Control is NOT Supported by this Device!!!"
                break
            case "wakeWord":
                warnMsg = "OOPS... Alexa Wake Word Control is NOT Supported by this Device!!!"
                break
            case "amazonMusic":
                warnMsg = "OOPS... Amazon Music is NOT Supported by this Device!!!"
                break
            case "appleMusic":
                warnMsg = "OOPS... Apple Music is NOT Supported by this Device!!!"
                break
            case "tuneInRadio":
                warnMsg = "OOPS... Tune-In Radio is NOT Supported by this Device!!!"
                break
            case "iHeartRadio":
                warnMsg = "OOPS... iHeart Radio is NOT Supported by this Device!!!"
                break
            case "pandoraRadio":
                warnMsg = "OOPS... Pandora Radio is NOT Supported by this Device!!!"
                break
            case "siriusXm":
                warnMsg = "OOPS... Sirius XM Radio is NOT Supported by this Device!!!"
                break
            // case "tidal":
            //     warnMsg = "OOPS... Tidal Music is NOT Supported by this Device!!!"
            //     break
            case "spotify":
                warnMsg = "OOPS... Spotify is NOT Supported by this Device!!!"
                break
            case "flashBriefing":
                warnMsg = "OOPS... Flash Briefs are NOT Supported by this Device!!!"
                break
        }
        if(warnMsg && !noLogs) { logWarn(warnMsg, true) }
        return false
    }
}

Boolean permissionOk(type) {
    if(type && state?.permissions?.containsKey(type) && state?.permissions[type] == true) { return true }
    return false
}

void updateDeviceStatus(Map devData) {
    Boolean isOnline = false
    if(devData?.size()) {
        isOnline = (devData?.online != false)
        // log.debug "isOnline: ${isOnline}"
        // log.debug "deviceFamily: ${devData?.deviceFamily} | deviceType: ${devData?.deviceType}"  // UNCOMMENT to identify unidentified devices

        // NOTE: These allow you to log all device data items
        // devData?.each { k,v ->
        //     if(!(k in ["playerState", "capabilities", "deviceAccountId"])) {
        //         log.debug("$k: $v")
        //     }
        // }
        state?.isSupportedDevice = (devData?.unsupported != true)
        state?.isEchoDevice = (devData?.isEchoDevice == true)
        state?.serialNumber = devData?.serialNumber
        state?.deviceType = devData?.deviceType
        state?.deviceOwnerCustomerId = devData?.deviceOwnerCustomerId
        state?.deviceAccountId = devData?.deviceAccountId
        state?.softwareVersion = devData?.softwareVersion
        // state?.mainAccountCommsId = devData?.mainAccountCommsId ?: null
        // log.debug "mainAccountCommsId: ${state?.mainAccountCommsId}"
        state?.cookie = devData?.cookie
        state?.authValid = (devData?.authValid == true)
        state?.amazonDomain = devData?.amazonDomain
        state?.regionLocale = devData?.regionLocale
        Map permissions = state?.permissions ?: [:]
        devData?.permissionMap?.each {k,v -> permissions[k] = v }
        state?.permissions = permissions
        state?.hasClusterMembers = devData?.hasClusterMembers
        state?.isWhaDevice = (devData?.permissionMap?.isMultiroomDevice == true)
        // log.trace "hasClusterMembers: ${ state?.hasClusterMembers}"
        // log.trace "permissions: ${state?.permissions}"
        List permissionList = permissions?.findAll { it?.value == true }?.collect { it?.key }
        if(isStateChange(device, "permissions", permissionList?.toString())) {
            sendEvent(name: "permissions", value: permissionList, display: false, displayed: false)
        }
        Map deviceStyle = devData?.deviceStyle
        state?.deviceStyle = devData?.deviceStyle
        // logInfo("deviceStyle (${devData?.deviceFamily}): ${devData?.deviceType} | Desc: ${deviceStyle?.name}")
        state?.deviceImage = deviceStyle?.image as String
        if(isStateChange(device, "deviceStyle", deviceStyle?.name?.toString())) {
            sendEvent(name: "deviceStyle", value: deviceStyle?.name?.toString(), descriptionText: "Device Style is ${deviceStyle?.name}", display: true, displayed: true)
        }

        String firmwareVer = devData?.softwareVersion ?: "Not Set"
        if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
            sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
        }

        String devFamily = devData?.deviceFamily ?: ""
        if(isStateChange(device, "deviceFamily", devFamily?.toString())) {
            sendEvent(name: "deviceFamily", value: devFamily?.toString(), descriptionText: "Echo Device Family is ${devFamily}", display: true, displayed: true)
        }

        String devType = devData?.deviceType ?: ""
        if(isStateChange(device, "deviceType", devType?.toString())) {
            sendEvent(name: "deviceType", value: devType?.toString(), display: false, displayed: false)
        }

        Map musicProviders = devData?.musicProviders ?: [:]
        String lItems = musicProviders?.collect{ it?.value }?.sort()?.join(", ")
        if(isStateChange(device, "supportedMusic", lItems?.toString())) {
            sendEvent(name: "supportedMusic", value: lItems?.toString(), display: false, displayed: false)
        }
        // if(devData?.guardStatus) { updGuardStatus(devData?.guardStatus) }
        if(!isOnline) {
            sendEvent(name: "mute", value: "unmuted")
            sendEvent(name: "status", value: "stopped")
            sendEvent(name: "deviceStatus", value: "stopped_${state?.deviceStyle?.image}")
            sendEvent(name: "trackDescription", value: "")
        } else { state?.fullRefreshOk = true; triggerDataRrsh(); }
    }
    setOnlineStatus(isOnline)
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false, displayed: false)
    schedDataRefresh()
}

public updSocketStatus(active) {
    if(active != true) { schedDataRefresh(true) }
    state?.websocketActive = active
}

void websocketUpdEvt(triggers) {
    logDebug("websocketEvt: $triggers")
    if(state?.isWhaDevice) { return }
    if(triggers?.size()) {
        triggers?.each { k->
            switch(k) {
                case "all":
                    state?.fullRefreshOk = true
                    runIn(2, "refreshData")
                    break
                case "media":
                    runIn(2, "getPlaybackState")
                    break
                case "queue":
                    runIn(4, "getPlaylists")
                case "notif":
                    runIn(2, "getNotifications")
                    break
                case "bluetooth":
                    runIn(2, "getBluetoothDevices")
                    break
                case "notification":
                    runIn(2, "getNotifications")
                    break
                case "online":
                    setOnlineStatus(true)
                    break
                case "offline":
                    setOnlineStatus(false)
                    break
                case "activity":
                    runIn(2, "getDeviceActivity")
                    break

            }
            //TODO: BUILD A DATA REFRESH QUEUE System
        }
    }
}

void refresh() {
    logTrace("refresh()")
    parent?.childInitiatedRefresh()
    refreshData(true)
}

private triggerDataRrsh(parentRefresh=false) {
    runIn(4, parentRefresh ? "refresh" : "refreshData")
}

public schedDataRefresh(frc) {
    if(frc || state?.refreshScheduled != true) {
        runEvery1Minute("refreshData")
        state?.refreshScheduled = true
    }
}

private refreshData(full=false) {
    // logTrace("trace", "refreshData()...")
    Boolean wsActive = (state?.websocketActive == true)
    Boolean isWHA = (state?.isWhaDevice == true)
    Boolean isEchoDev = (state?.isEchoDevice == true)
    if(device?.currentValue("onlineStatus") != "online") {
        logTrace("Skipping Device Data Refresh... Device is OFFLINE... (Offline Status Updated Every 10 Minutes)")
        return
    }
    if(!isAuthOk()) {return}
    if(checkMinVersion()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return; }
    // logTrace("permissions: ${state?.permissions}")
    if(state?.permissions?.mediaPlayer == true && (full || state?.fullRefreshOk || !wsActive)) {
        getPlaybackState()
        if(!isWHA) { getPlaylists() }
    }
    if(!isWHA) {
        if (full || state?.fullRefreshOk) {
            if(isEchoDev) { getWifiDetails() }
            getDeviceSettings()
        }
        if(state?.permissions?.doNotDisturb == true) { getDoNotDisturb() }
        getDeviceActivity()
        runIn(3, "refreshStage2")
    } else { state?.fullRefreshOk = false }
}

private refreshStage2() {
    Boolean wsActive = (state?.websocketActive == true)
    if(state?.permissions?.wakeWord) {
        getWakeWord()
        getAvailableWakeWords()
    }
    if((state?.permissions?.alarms == true) || (state?.permissions?.reminders == true)) {
        if(state?.permissions?.alarms == true) { getAlarmVolume() }
        getNotifications()
    }

    if(state?.permissions?.bluetoothControl && !wsActive) {
        getBluetoothDevices()
    }
    state?.fullRefreshOk = false
    // updGuardStatus()
}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
    if(isStateChange(device, "DeviceWatch-DeviceStatus", onlStatus?.toString())) {
        sendEvent(name: "DeviceWatch-DeviceStatus", value: onlStatus?.toString(), displayed: false, isStateChange: true)
    }
    if(isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        logDebug("OnlineStatus has changed to (${onlStatus})")
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), displayed: true, isStateChange: true)
    }
}

private getPlaybackState(isGroupResponse=false) {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/np/player",
        query: [ deviceSerialNumber: state?.serialNumber, deviceType: state?.deviceType, screenWidth: 2560, _: now() ],
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal() ],
        contentType: "application/json"
    ]
    Map playerInfo = [:]
    try {
        httpGet(params) { response->
            Map sData = response?.data ?: [:]
            playerInfo = sData?.playerInfo ?: [:]
        }
    } catch (ex) {
        respExceptionHandler(ex, "getPlaybackState", false, true)
    }
    playbackStateHandler(playerInfo)
}

def playbackStateHandler(playerInfo, isGroupResponse=false) {
    // log.debug "playerInfo: ${playerInfo}"
    Boolean isPlayStateChange = false
    Boolean isMediaInfoChange = false
    if (state?.isGroupPlaying && !isGroupResponse) {
        logDebug("ignoring getPlaybackState because group is playing here")
        return
    }
    // logTrace("getPlaybackState: ${playerInfo}")
    String playState = playerInfo?.state == 'PLAYING' ? "playing" : "stopped"
    String deviceStatus = "${playState}_${state?.deviceStyle?.image}"
    // log.debug "deviceStatus: ${deviceStatus}"
    if(isStateChange(device, "status", playState?.toString()) || isStateChange(device, "deviceStatus", deviceStatus?.toString())) {
        logTrace("Status Changed to ${playState}")
        isPlayStateChange = true
        if (isGroupResponse) {
            state?.isGroupPlaying = (playerInfo?.state == 'PLAYING')
        }
        sendEvent(name: "status", value: playState?.toString(), descriptionText: "Player Status is ${playState}", display: true, displayed: true)
        sendEvent(name: "deviceStatus", value: deviceStatus?.toString(), display: false, displayed: false)
    }
    //Track Title
    String title = playerInfo?.infoText?.title ?: ""
    if(isStateChange(device, "trackDescription", title?.toString())) {
        isMediaInfoChange = true
        sendEvent(name: "trackDescription", value: title?.toString(), descriptionText: "Track Description is ${title}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText1 = playerInfo?.infoText?.subText1 ?: "Idle"
    if(isStateChange(device, "currentAlbum", subText1?.toString())) {
        isMediaInfoChange = true
        sendEvent(name: "currentAlbum", value: subText1?.toString(), descriptionText: "Album is ${subText1}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText2 = playerInfo?.infoText?.subText2 ?: "Idle"
    if(isStateChange(device, "currentStation", subText2?.toString())) {
        isMediaInfoChange = true
        sendEvent(name: "currentStation", value: subText2?.toString(), descriptionText: "Station is ${subText2}", display: true, displayed: true)
    }

    //Track Art Image
    String trackImg = (playerInfo && playerInfo?.mainArt && playerInfo?.mainArt?.url) ? playerInfo?.mainArt?.url : ""
    if(isStateChange(device, "trackImage", trackImg?.toString())) {
        isMediaInfoChange = true
        sendEvent(name: "trackImage", value: trackImg?.toString(), descriptionText: "Track Image is ${trackImg}", display: false, displayed: false)
        sendEvent(name: "trackImageHtml", value: """<img src="${trackImg?.toString()}"/>""", display: false, displayed: false)
    }

    //Media Source Provider
    String mediaSource = playerInfo?.provider?.providerName ?: ""
    if(isStateChange(device, "mediaSource", mediaSource?.toString())) {
        isMediaInfoChange = true
        sendEvent(name: "mediaSource", value: mediaSource?.toString(), descriptionText: "Media Source is ${mediaSource}", display: true, displayed: true)
    }

    //Update Audio Track Data
    if (isMediaInfoChange){
        Map trackData = [:]
        if(playerInfo?.infoText?.title) { trackData?.title = playerInfo?.infoText?.title }
        if(playerInfo?.infoText?.subText1) { trackData?.artist = playerInfo?.infoText?.subText1 }
        //To avoid media source provider being used as album (ex: Apple Music), only inject `album` if subText2 and providerName are different
        if(playerInfo?.infoText?.subText2 && playerInfo?.provider?.providerName!=playerInfo?.infoText?.subText2) { trackData?.album = playerInfo?.infoText?.subText2 }
        if(playerInfo?.mainArt?.url) { trackData?.albumArtUrl = playerInfo?.mainArt?.url }
        if(playerInfo?.provider?.providerName) { trackData?.mediaSource = playerInfo?.provider?.providerName }
        //log.debug(trackData)
        sendEvent(name: "audioTrackData", value: new groovy.json.JsonOutput().toJson(trackData), display: false, displayed: false)
    }

    // Group response data never has valida data for volume
    if(!isGroupResponse && playerInfo?.volume) {
        if(playerInfo?.volume?.volume != null) {
            Integer level = playerInfo?.volume?.volume
            if(level < 0) { level = 0 }
            if(level > 100) { level = 100 }
            if(isStateChange(device, "level", level?.toString()) || isStateChange(device, "volume", level?.toString())) {
                logDebug("Volume Level Set to ${level}%")
                sendEvent(name: "level", value: level, display: false, displayed: false)
                sendEvent(name: "volume", value: level, display: false, displayed: false)
            }
        }
        if(playerInfo?.volume?.muted != null) {
            String muteState = (playerInfo?.volume?.muted == true) ? "muted" : "unmuted"
            if(isStateChange(device, "mute", muteState?.toString())) {
                logDebug("Mute Changed to ${muteState}")
                sendEvent(name: "mute", value: muteState, descriptionText: "Volume has been ${muteState}", display: true, displayed: true)
            }
        }
    }
    // Update cluster (unless we remain paused)
    if (state?.hasClusterMembers && (playerInfo?.state == 'PLAYING' || isPlayStateChange)) {
        parent?.sendPlaybackStateToClusterMembers(state?.serialNumber, playerInfo)
    }
}

private getAlarmVolume() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/device-notification-state/${state?.deviceType}/${device.currentValue("firmwareVer") as String}/${state.serialNumber}",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        query: [_: new Date().getTime()],
        contentType: "application/json",
    ]
    try {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // logTrace("getAlarmVolume: $sData")
            if(sData && isStateChange(device, "alarmVolume", (sData?.volumeLevel ?: 0)?.toString())) {
                logDebug("Alarm Volume Changed to ${(sData?.volumeLevel ?: 0)}")
                sendEvent(name: "alarmVolume", value: (sData?.volumeLevel ?: 0), display: false, displayed: false)
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getAlarmVolume")
    }
}

private getWakeWord() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/wake-word",
        query: [cached: true, _: new Date().getTime()],
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
    ]
    try {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // log.debug "sData: $sData"
            if(sData && sData?.wakeWords) {
                def wakeWord = sData?.wakeWords?.find { it?.deviceSerialNumber == state?.serialNumber } ?: null
                // logTrace("getWakeWord: ${wakeWord?.wakeWord}")
                if(isStateChange(device, "alexaWakeWord", wakeWord?.wakeWord?.toString())) {
                    logDebug("Wake Word Changed to ${(wakeWord?.wakeWord)}")
                    sendEvent(name: "alexaWakeWord", value: wakeWord?.wakeWord, display: false, displayed: false)
                }
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getWakeWord")
    }
}

private getWifiDetails() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/device-wifi-details",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        query: [ cached: true, _: new Date().getTime(), deviceSerialNumber: state?.serialNumber, deviceType: state?.deviceType ],
        contentType: "application/json",
    ]
    try {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // log.debug "sData: $sData"
            if(sData && sData?.wakeWords) {
                def wakeWord = sData?.wakeWords?.find { it?.deviceSerialNumber == state?.serialNumber } ?: null
                // logTrace("getWakeWord: ${wakeWord?.wakeWord}")
                if(isStateChange(device, "alexaWakeWord", wakeWord?.wakeWord?.toString())) {
                    logDebug("Wake Word Changed to ${(wakeWord?.wakeWord)}")
                    sendEvent(name: "alexaWakeWord", value: wakeWord?.wakeWord, display: false, displayed: false)
                }
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getWifiDetails")
    }
}

private getDeviceSettings() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/device-preferences",
        query: [cached: true, _: new Date().getTime()],
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
    ]
    try {
        httpGet(params) { response->
            Map sData = response?.data ?: null
            // log.debug "sData: $sData"
            def devData = sData?.devicePreferences?.find { it?.deviceSerialNumber == state?.serialNumber } ?: null
            state?.devicePreferences = devData ?: [:]
            // log.debug "devData: $devData"
            def fupMode = (devData?.goldfishEnabled == true)
            if(isStateChange(device, "followUpMode", fupMode?.toString())) {
                logDebug("FollowUp Mode Changed to ${(fupMode)}")
                sendEvent(name: "followUpMode", value: fupMode, display: false, displayed: false)
            }
            // logTrace("getDeviceSettingsHandler: ${sData}")
        }
    } catch (ex) {
        respExceptionHandler(ex, "getDeviceSettings")
    }
}

private getAvailableWakeWords() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/wake-words-locale",
        query: [ cached: true, _: new Date().getTime(), deviceSerialNumber: state?.serialNumber, deviceType: state?.deviceType, softwareVersion: device.currentValue('firmwareVer') ],
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
    ]
    try {
        httpGet(params) { response->
            Map sData = response?.data ?: null
            // log.debug "sData: $sData"
            String wakeWords = (sData && sData?.wakeWords) ? sData?.wakeWords?.join(",") : null
            logTrace("getAvailableWakeWords: ${wakeWords}")
            if(isStateChange(device, "wakeWords", wakeWords?.toString())) {
                sendEvent(name: "wakeWords", value: wakeWords, display: false, displayed: false)
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getAvailableWakeWords")
    }
}

def getBluetoothDevices() {
    Map btData = parent?.getBluetoothData(state?.serialNumber) ?: [:]
    String curConnName = btData?.curConnName ?: null
    Map btObjs = btData?.btObjs ?: [:]
    // logDebug("Current Bluetooth Device: ${curConnName} | Bluetooth Objects: ${btObjs}")
    state?.bluetoothObjs = btObjs
    String pairedNames = (btData && btData?.pairedNames) ? btData?.pairedNames?.join(",") : null
    // if(isStateChange(device, "btDeviceConnected", curConnName?.toString())) {
        // log.info "Bluetooth Device Connected: (${curConnName})"
        sendEvent(name: "btDeviceConnected", value: curConnName?.toString(), descriptionText: "Bluetooth Device Connected (${curConnName})", display: true, displayed: true)
    // }

    if(isStateChange(device, "btDevicesPaired", pairedNames?.toString())) {
        logDebug("Paired Bluetooth Devices: ${pairedNames}")
        sendEvent(name: "btDevicesPaired", value: pairedNames, descriptionText: "Paired Bluetooth Devices: ${pairedNames}", display: true, displayed: true)
    }
}

def updGuardStatus(val=null) {
    //TODO: Update this because it's not working
    String gState = val ?: (state?.permissions?.guardSupported ? (parent?.getAlexaGuardStatus() ?: "Unknown") : "Not Supported")
    if(isStateChange(device, "alexaGuardStatus", gState?.toString())) {
        sendEvent(name: "alexaGuardStatus", value: gState, display: false, displayed: false)
        logDebug("Alexa Guard Status: (${gState})")
    }
}

private String getBtAddrByAddrOrName(String btNameOrAddr) {
    Map btObj = state?.bluetoothObjs
    String curBtAddr = btObj?.find { it?.value?.friendlyName == btNameOrAddr || it?.value?.address == btNameOrAddr }?.key ?: null
    // logDebug("curBtAddr: ${curBtAddr}")
    return curBtAddr
}

private getDoNotDisturb() {
    Boolean dndEnabled = (parent?.getDndEnabled(state?.serialNumber) == true)
    logTrace("getDoNotDisturb: $dndEnabled")
    state?.doNotDisturb = dndEnabled
    if(isStateChange(device, "doNotDisturb", (dndEnabled == true)?.toString())) {
        logDebug("Do Not Disturb: (${(dndEnabled == true)})")
        sendEvent(name: "doNotDisturb", value: (dndEnabled == true)?.toString(), descriptionText: "Do Not Disturb Enabled ${(dndEnabled == true)}", display: true, displayed: true)
    }
}

private getPlaylists() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/cloudplayer/playlists",
        query: [ deviceSerialNumber: state?.serialNumber, deviceType: state?.deviceType, mediaOwnerCustomerId: state?.deviceOwnerCustomerId, screenWidth: 2560 ],
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1"],
        contentType: "application/json"
    ]
    try {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // logTrace("getPlaylistsHandler: ${sData}")
            Map playlists = sData ? sData?.playlists : "{}"
            if(isStateChange(device, "alexaPlaylists", playlists?.toString())) {
                // log.trace "Alexa Playlists Changed to ${playlists}"
                sendEvent(name: "alexaPlaylists", value: playlists, display: false, displayed: false)
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getPlaylists")
    }
}

private getNotifications(type="Reminder", all=false) {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/notifications",
        query: [cached: true],
        headers: [Cookie: getCookieVal(), csrf: getCsrfVal()],
        contentType: "application/json"
    ]
    try {
        httpGet(params) { response->
            List newList = []
            def sData = response?.data ?: null
            if(sData?.size()) {
                List s = ["ON"]
                if(all) s?.push("OFF")
                List items = sData?.notifications ? sData?.notifications?.findAll { (it?.status in s) && (it?.type == type) && it?.deviceSerialNumber == state?.serialNumber } : []
                items?.each { item->
                    Map li = [:]
                    item?.keySet()?.each { key-> if(key in ['id', 'reminderLabel', 'originalDate', 'originalTime', 'deviceSerialNumber', 'type', 'remainingDuration']) { li[key] = item[key] } }
                    newList?.push(li)
                }
            }
            if(isStateChange(device, "alexaNotifications", newList?.toString())) {
                sendEvent(name: "alexaNotifications", value: newList, display: false, displayed: false)
            }
            // log.trace "notifications: $newList"
            return newList
        }
    } catch (ex) {
        respExceptionHandler(ex, "getNotifications")
        return null
    }
}

private getDeviceActivity() {
    try {
        def aData = parent?.getActivityData(state?.serialNumber) ?: null
        Boolean wasLastDevice = (aData?.lastSpokenTo == true)
        if (aData != null) {
            if(isStateChange(device, "lastVoiceActivity", aData?.spokenText?.toString())) {
                sendEvent(name: "lastVoiceActivity", value: aData?.spokenText?.toString(), display: false, displayed: false)
            }
            if(isStateChange(device, "lastSpokenToTime", aData?.lastSpokenDt?.toString())) {
                sendEvent(name: "lastSpokenToTime", value: aData?.lastSpokenDt?.toString(), display: false, displayed: false)
            }
        }
        if(isStateChange(device, "wasLastSpokenToDevice", wasLastDevice?.toString())) {
            logDebug("wasLastSpokenToDevice: ${wasLastDevice}")
            sendEvent(name: "wasLastSpokenToDevice", value: wasLastDevice, display: false, displayed: false)
        }
    } catch (ex) {
        logError("updDeviceActivity Error: ${ex.message}")
    }
}

String getCookieVal() { return (state?.cookie && state?.cookie?.cookie) ? state?.cookie?.cookie as String : null }
String getCsrfVal() { return (state?.cookie && state?.cookie?.csrf) ? state?.cookie?.csrf as String : null }

/*******************************************************************
            Amazon Command Logic
*******************************************************************/

private sendAmazonBasicCommand(String cmdType) {
    sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/np/command",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal()],
        query: [ deviceSerialNumber: state?.serialNumber, deviceType: state?.deviceType ],
        contentType: "application/json",
        body: [type: cmdType]
    ], [cmdDesc: cmdType])
}

private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
    if(method && callbackHandler && params) {
        String m = method?.toString()?.toLowerCase()
        if(isST()) {
            include 'asynchttp_v1'
            asynchttp_v1."${m}"(callbackHandler, params, otherData)
        } else { "asynchttp${m?.capitalize()}"("${callbackHandler}", params, otherData) }
    }
}

private String sendAmazonCommand(String method, Map params, Map otherData=null) {
    try {
        def rData = null
        def rStatus = null
        switch(method) {
            case "POST":
                httpPostJson(params) { response->
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
            case "PUT":
                if(params?.body) { params?.body = new groovy.json.JsonOutput().toJson(params?.body) }
                httpPutJson(params) { response->
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
            case "DELETE":
                httpDelete(params) { response->
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
        }
        if (otherData?.cmdDesc?.startsWith("connectBluetooth") || otherData?.cmdDesc?.startsWith("disconnectBluetooth") || otherData?.cmdDesc?.startsWith("removeBluetooth")) {
            triggerDataRrsh()
        } else if(otherData?.cmdDesc?.startsWith("renameDevice")) { triggerDataRrsh(true) }
        logDebug("sendAmazonCommand | Status: (${rStatus})${rData != null ? " | Response: ${rData}" : ""} | ${otherData?.cmdDesc} was Successfully Sent!!!")
        return rData?.id || null
    } catch (ex) {
        respExceptionHandler(ex, "${otherData?.cmdDesc}", true)
    }
}

private sendSequenceCommand(type, command, value) {
    // logTrace("sendSequenceCommand($type) | command: $command | value: $value")
    Map seqObj = sequenceBuilder(command, value)
    sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/behaviors/preview",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
        body: new groovy.json.JsonOutput().toJson(seqObj)
    ], [cmdDesc: "SequenceCommand (${type})"])
}

private sendMultiSequenceCommand(commands, String srcDesc, Boolean parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem->
        if(cmdItem?.command instanceof Map) {
            nodeList?.push(cmdItem?.command)
        } else { nodeList?.push(createSequenceNode(cmdItem?.command, cmdItem?.value, cmdItem?.devType ?: null, cmdItem?.devSerial ?: null)) }
    }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    sendSequenceCommand("${srcDesc} | MultiSequence: ${parallel ? "Parallel" : "Sequential"}", seqJson, null)
}

def respExceptionHandler(ex, String mName, clearOn401=false, ignNullMsg=false) {
    if(ex instanceof groovyx.net.http.HttpResponseException ) {
        Integer sCode = ex?.getResponse()?.getStatus()
        def respData = ex?.getResponse()?.getData()
        def errMsg = ex?.getMessage()
        if(sCode == 401) {
            // logError("${mName} | Amazon Authentication is no longer valid | Msg: ${errMsg}")
            if(clearOn401) { setAuthState(false) }
        } else if (sCode == 400) {
            switch(errMsg) {
                case "Bad Request":
                    if(respData && respData?.message == null && ignNullMsg) {
                        // Ignoring Null message
                    } else {
                        if (respData && respData?.message?.startsWith("Music metadata")) {
                            // Ignoring metadata error message
                        } else if(respData && respData?.message?.startsWith("Unknown device type in request")) {
                            // Ignoring Unknown device type in request
                        } else if(respData && respData?.message?.startsWith("device not connected")) {
                            // Ignoring device not connect error
                        } else { logError("${mName} | Status: ($sCode) | Message: ${errMsg} | Data: ${respData}") }
                    }
                    break
                case "Rate Exceeded":
                    logError("${mName} | Amazon is currently rate-limiting your requests | Msg: ${errMsg}")
                    break
                default:
                    if(respData && respData?.message == null && ignNullMsg) {
                        // Ignoring Null message
                    } else {
                        logError("${mName} | 400 Error | Msg: ${errMsg}")
                    }
                    break
            }
        } else if(sCode == 429) {
            logWarn("${mName} | Too Many Requests Made to Amazon | Msg: ${errMsg}")
        } else if(sCode == 200) {
            if(errMsg != "OK") { logError("${mName} Response Exception | Status: (${sCode}) | Msg: ${errMsg}") }
        } else {
            logError("${mName} Response Exception | Status: (${sCode}) | Msg: ${errMsg}")
        }
    } else if(ex instanceof java.net.SocketTimeoutException) {
        if(settings?.ignoreTimeoutErrors == true) logError("${mName} | Response Socket Timeout (Possibly an Amazon Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.UnknownHostException) {
        logError("${mName} | HostName Not Found | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof org.apache.http.conn.ConnectTimeoutException) {
        if(settings?.ignoreTimeoutErrors == true) logError("${mName} | Request Timeout (Possibly an Amazon/Internet Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.NoRouteToHostException) {
        logError("${mName} | No Route to Connection (Possibly a Local Internet Issue) | Msg: ${ex}")
    } else if(ex instanceof javax.net.ssl.SSLHandshakeException) {
        if(settings?.ignoreTimeoutErrors == true) logError("${mName} | Remote Connection Closed (Possibly an Amazon/Internet Issue) | Msg: ${ex}")
    } else { logError("${mName} Exception: ${ex}") }
}

def searchTest() {
    searchMusic("thriller", "AMAZON_MUSIC")
}
/*******************************************************************
            Device Command FUNCTIONS
*******************************************************************/

def play() {
    logTrace("play() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PlayCommand")
        if(isStateChange(device, "status", "playing")) {
            sendEvent(name: "status", value: "playing", descriptionText: "Player Status is playing", display: true, displayed: true)
            // log.debug "deviceStatus: playing_${state?.deviceStyle?.image}"
            sendEvent(name: "deviceStatus", value: "playing_${state?.deviceStyle?.image}", display: false, displayed: false)
        }
        triggerDataRrsh()
    }
}

def playTrack(uri) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrack($uri) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            speak(tts as String)
        } else {
            logWarn("Uh-Oh... The playTrack($uri) Command is NOT Supported by this Device!!!")
        }
    }
}

def pause() {
    logTrace("pause() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PauseCommand")
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
            // log.debug "deviceStatus: stopped_${state?.deviceStyle?.image}"
            sendEvent(name: "deviceStatus", value: "stopped_${state?.deviceStyle?.image}", display: false, displayed: false)
        }
        triggerDataRrsh()
    }
}

def stop() {
    logTrace("stop() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PauseCommand")
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
        }
        triggerDataRrsh()
    }
}

def togglePlayback() {
    logTrace("togglePlayback() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        def isPlaying = (device?.currentValue('status') == "playing")
        if(isPlaying) {
            stop()
        } else {
            play()
        }
    }
}

def stopAllDevices() {
    doSequenceCmd("StopAllDevicesCommand", "stopalldevices")
    triggerDataRrsh()
}

def previousTrack() {
    logTrace("previousTrack() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PreviousCommand")
        triggerDataRrsh()
    }
}

def nextTrack() {
    logTrace("nextTrack() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("NextCommand")
        triggerDataRrsh()
    }
}

def mute() {
    logTrace("mute() command received...")
    if(isCommandTypeAllowed("volumeControl")) {
        state.muteLevel = device?.currentValue("level")?.toInteger()
        if(isStateChange(device, "mute", "muted")) {
            sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
        }
        setLevel(0)
    }
}

def repeat() {
    logTrace("repeat() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("RepeatCommand")
        triggerDataRrsh()
    }
}

def shuffle() {
    logTrace("shuffle() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("ShuffleCommand")
        triggerDataRrsh()
    }
}

def unmute() {
    logTrace("unmute() command received...")
    if(isCommandTypeAllowed("volumeControl")) {
        if(state?.muteLevel) {
            setLevel(state?.muteLevel)
            state?.muteLevel = null
            if(isStateChange(device, "mute", "unmuted")) {
                sendEvent(name: "mute", value: "unmuted", descriptionText: "Mute is set to unmuted", display: true, displayed: true)
            }
        }
    }
}

def setMute(muteState) {
    if(muteState) { (muteState == "muted") ? mute() : unmute() }
}

def setLevel(level) {
    logTrace("setVolume($level) command received...")
    if(isCommandTypeAllowed("volumeControl") && level>=0 && level<=100) {
        if(level != device?.currentValue('level')) {
            sendSequenceCommand("VolumeCommand", "volume", level)
            sendEvent(name: "level", value: level?.toInteger(), display: false, displayed: false)
            sendEvent(name: "volume", value: level?.toInteger(), display: false, displayed: false)
        }
    }
}

def setAlarmVolume(vol) {
    logTrace("setAlarmVolume($vol) command received...")
    if(isCommandTypeAllowed("alarms") && vol>=0 && vol<=100) {
        sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/device-notification-state/${state?.deviceType}/${state?.softwareVersion}/${state?.serialNumber}",
            headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
            contentType: "application/json",
            body: [
                deviceSerialNumber: state?.serialNumber,
                deviceType: state?.deviceType,
                softwareVersion: device?.currentValue('firmwareVer'),
                volumeLevel: vol
            ]
        ], [cmdDesc: "AlarmVolume"])
        sendEvent(name: "alarmVolume", value: vol, display: false, displayed: false)
    }
}

def setVolume(vol) {
    if(vol) { setLevel(vol?.toInteger()) }
}

def volumeUp() {
    def curVol = (device?.currentValue('level') ?: 1)
    if(curVol >= 0 && curVol < 100) { setVolume(curVol?.toInteger()+5) }
}

def volumeDown() {
    def curVol = (device?.currentValue('level') ?: 0)
    if(curVol > 0) { setVolume(curVol?.toInteger()-5) }
}

def setTrack(String uri, metaData="") {
    logWarn("Uh-Oh... The setTrack(uri: $uri, meta: $meta) Command is NOT Supported by this Device!!!", true)
}

def resumeTrack() {
    logWarn("Uh-Oh... The resumeTrack() Command is NOT Supported by this Device!!!", true)
}

def restoreTrack() {
    logWarn("Uh-Oh... The restoreTrack() Command is NOT Supported by this Device!!!", true)
}

def doNotDisturbOff() {
    setDoNotDisturb(false)
}

def doNotDisturbOn() {
    setDoNotDisturb(true)
}

def followUpModeOff() {
    setFollowUpMode(false)
}

def followUpModeOn() {
    setFollowUpMode(true)
}

def setDoNotDisturb(Boolean val) {
    logTrace("setDoNotDisturb($val) command received...")
    if(isCommandTypeAllowed("doNotDisturb")) {
        sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/dnd/status",
            headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
            contentType: "application/json",
            body: [
                deviceSerialNumber: state?.serialNumber,
                deviceType: state?.deviceType,
                enabled: (val==true)
            ]
        ], [cmdDesc: "SetDoNotDisturb${val ? "On" : "Off"}"])
        sendEvent(name: "doNotDisturb", value: (val == true)?.toString(), descriptionText: "Do Not Disturb Enabled ${(val == true)}", display: true, displayed: true)
        parent?.getDoNotDisturb()
    }
}

def setFollowUpMode(Boolean val) {
    logTrace("setFollowUpMode($val) command received...")
    if(state?.devicePreferences == null || !state?.devicePreferences?.size()) { return }
    if(!state?.deviceAccountId) { logError("renameDevice Failed because deviceAccountId is not found..."); return; }
    if(isCommandTypeAllowed("followUpMode")) {
        sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/device-preferences/${state?.serialNumber}",
            headers: [ Cookie: getCookieVal(), csrf: getCsrfVal()],
            contentType: "application/json",
            body: [
                deviceSerialNumber: state?.serialNumber,
                deviceType: state?.deviceType,
                deviceAccountId: state?.deviceAccountId,
                goldfishEnabled: (val==true)
            ]
        ], [cmdDesc: "setFollowUpMode${val ? "On" : "Off"}"])
    }
}

def deviceNotification(String msg) {
    logTrace("deviceNotification(msg: $msg) command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with deviceNotification($msg) command", true); return; }
        // logTrace("deviceNotification(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
        if(settings?.sendDevNotifAsAnnouncement == true) { playAnnouncement(msg as String) } else { speak(msg as String) }
    }
}

def setVolumeAndSpeak(volume, String msg) {
    logTrace("setVolumeAndSpeak(volume: $volume, msg: $msg) command received...")
    if(volume != null && permissionOk("volumeControl")) {
        state?.newVolume = volume
    }
    speak(msg)
}

def setVolumeSpeakAndRestore(volume, String msg, restVolume=null) {
    logTrace("setVolumeSpeakAndRestore(volume: $volume, msg: $msg, restVolume) command received...")
    if(msg) {
        if(volume != null && permissionOk("volumeControl")) {
            state?.newVolume = volume?.toInteger()
            if(restVolume != null) {
                state?.oldVolume = restVolume as Integer
            } else {
                storeCurrentVolume()
            }
        }
        speak(msg)
    }
}

def storeCurrentVolume() {
    Integer curVol = device?.currentValue("level") ?: 1
    logTrace("storeCurrentVolume(${curVol}) command received...")
    if(curVol != null) { state?.oldVolume = curVol as Integer }
}

private restoreLastVolume() {
    Integer lastVol = state?.oldVolume
    logTrace("restoreLastVolume(${lastVol}) command received...")
    if(lastVol && permissionOk("volumeControl")) {
        setVolume(lastVol as Integer)
        sendEvent(name: "level", value: lastVol, display: false, displayed: false)
        sendEvent(name: "volume", value: lastVol, display: false, displayed: false)
    } else { logWarn("Unable to restore Last Volume!!! restoreVolume State Value not found...", true) }
}

def sayWelcomeHome(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "iamhome"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayWelcomeHome")
    } else { doSequenceCmd("sayWelcomeHome", "cannedtts_random", "iamhome") }
}

def sayCompliment(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "compliments"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayCompliment")
    } else { doSequenceCmd("sayCompliment", "cannedtts_random", "compliments") }
}

def sayBirthday(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "birthday"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayBirthday")
    } else { doSequenceCmd("sayBirthday", "cannedtts_random", "birthday") }
}

def sayGoodNight(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "goodnight"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayGoodNight")
    } else { doSequenceCmd("sayGoodNight", "cannedtts_random", "goodnight") }
}

def sayGoodMorning(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "goodmorning"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayGoodMorning")
    } else { doSequenceCmd("sayGoodMorning", "cannedtts_random", "goodmorning") }
}

def sayGoodbye(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: "goodbye"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "sayGoodbye")
    } else { doSequenceCmd("sayGoodbye", "cannedtts_random", "goodbye") }
}

def executeRoutineId(String rId) {
    def execDt = now()
    logTrace("executeRoutineId($rId) command received...")
    if(!rId) { logWarn("No Routine ID sent with executeRoutineId($rId) command", true) }
    if(parent?.executeRoutineById(rId as String)) {
        logDebug("Executed Alexa Routine | Process Time: (${(now()-execDt)}ms) | RoutineId: ${rId}")
    }
}

def playWeather(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "weather"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playWeather")
    } else { doSequenceCmd("playWeather", "weather") }
}

def playTraffic(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "traffic"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playTraffic")
    } else { doSequenceCmd("playTraffic", "traffic") }
}

def playSingASong(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "singasong"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playSingASong")
    } else { doSequenceCmd("playSingASong", "singasong") }
}

def playFlashBrief(volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("flashBriefing")) {
        if(volume != null) {
            List seqs = [[command: "volume", value: volume], [command: "flashbriefing"]]
            if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
            sendMultiSequenceCommand(seqs, "playFlashBrief")
        } else { doSequenceCmd("playFlashBrief", "flashbriefing") }
    }
}

def playTellStory(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "tellstory"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playTellStory")
    } else { doSequenceCmd("playTellStory", "tellstory") }
}

def playFunFact(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "funfact"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playFunFact")
    } else { doSequenceCmd("playFunFact", "funfact") }
}

def playJoke(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "joke"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playJoke")
    } else { doSequenceCmd("playJoke", "joke") }
}

def playCalendarToday(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "calendartoday"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playCalendarToday")
    } else { doSequenceCmd("playCalendarToday", "calendartoday") }
}

def playCalendarTomorrow(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "calendartomorrow"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playCalendarTomorrow")
    } else { doSequenceCmd("playCalendarTomorrow", "calendartomorrow") }
}

def playCalendarNext(volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "calendarnext"]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playCalendarNext")
    } else { doSequenceCmd("playCalendarNext", "calendarnext") }
}

def playCannedRandomTts(String type, volume=null, restoreVolume=null) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "cannedtts_random", value: type]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playCannedRandomTts($type)")
    } else { doSequenceCmd("playCannedRandomTts($type)", "cannedtts_random", type) }
}

def playSoundByName(String name, volume=null, restoreVolume=null) {
    log.debug "sound name: ${name}"
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: "sound", value: name]]
        if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, "playSoundByName($name)")
    } else { doSequenceCmd("playSoundByName($name)", "sound", name) }
}

def playAnnouncement(String msg, volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("announce")) {
        if(volume != null) {
            List seqs = [[command: "volume", value: volume], [command: "announcement", value: msg]]
            if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
            sendMultiSequenceCommand(seqs, "playAnnouncement")
        } else { doSequenceCmd("playAnnouncement", "announcement", msg) }
    }
}

def playAnnouncement(String msg, String title, volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("announce")) {
        msg = "${title ? "${title}::" : ""}${msg}"
        if(volume != null) {
            List seqs = [[command: "volume", value: volume], [command: "announcement", value: msg]]
            if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
            sendMultiSequenceCommand(seqs, "playAnnouncement")
        } else { doSequenceCmd("playAnnouncement", "announcement", msg) }
    }
}

def sendAnnouncementToDevices(String msg, String title=null, devObj, volume=null, restoreVolume=null) {
    // log.debug "sendAnnouncementToDevices(msg: $msg, title: $title, devObj: devObj, volume: $volume, restoreVolume: $restoreVolume)"
    if(isCommandTypeAllowed("announce") && devObj) {
        def devJson = new groovy.json.JsonOutput().toJson(devObj)
        msg = "${title ?: "Echo Speaks"}::${msg}::${devJson?.toString()}"
        // log.debug "sendAnnouncementToDevices | msg: ${msg}"
        if(volume || restoreVolume) {
            List mainSeq = []
            if(volume) { devObj?.each { dev-> mainSeq?.push([command: "volume", value: volume, devType: dev?.deviceTypeId, devSerial: dev?.deviceSerialNumber]) } }
            mainSeq?.push([command: "announcement_devices", value: msg])
            if(restoreVolume) { devObj?.each { dev-> mainSeq?.push([command: "volume", value: restoreVolume, devType: dev?.deviceTypeId, devSerial: dev?.deviceSerialNumber]) } }
            // log.debug "mainSeq: $mainSeq"
            sendMultiSequenceCommand(mainSeq, "sendAnnouncementToDevices")
        } else { doSequenceCmd("sendAnnouncementToDevices", "announcement_devices", msg) }
    }
}

def playAnnouncementAll(String msg, String title=null) {
    // if(isCommandTypeAllowed("announce")) {bvxdsa
        doSequenceCmd("AnnouncementAll", "announcementall", msg)
    // }
}

def searchMusic(String searchPhrase, String providerId, volume=null, sleepSeconds=null) {
    // logTrace("searchMusic(${searchPhrase}, ${providerId})")
    if(isCommandTypeAllowed(getCommandTypeForProvider(providerId))) {
        doSearchMusicCmd(searchPhrase, providerId, volume, sleepSeconds)
    } else { logWarn("searchMusic not supported for ${providerId}", true) }
}

String getCommandTypeForProvider(String providerId) {
    def commandType = providerId
    switch (providerId) {
        case "AMAZON_MUSIC":
            commandType = "amazonMusic"
            break
        case "APPLE_MUSIC":
            commandType = "appleMusic"
            break
        case "TUNEIN":
            commandType = "tuneInRadio"
            break
        case "PANDORA":
            commandType = "pandoraRadio"
            break
        case "SIRIUSXM":
            commandType = "siriusXm"
            break
        case "SPOTIFY":
            commandType = "spotify"
            break
        // case "TIDAL":
        //     commandType = "tidal"
        //     break
        case "I_HEART_RADIO":
            commandType = "iHeartRadio"
            break
    }
    return commandType
}

def searchAmazonMusic(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("amazonMusic")) {
        doSearchMusicCmd(searchPhrase, "AMAZON_MUSIC", volume, sleepSeconds)
    }
}

def searchAppleMusic(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("appleMusic")) {
        doSearchMusicCmd(searchPhrase, "APPLE_MUSIC", volume, sleepSeconds)
    }
}

def searchTuneIn(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("tuneInRadio")) {
        doSearchMusicCmd(searchPhrase, "TUNEIN", volume, sleepSeconds)
    }
}

def searchPandora(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("pandoraRadio")) {
        doSearchMusicCmd(searchPhrase, "PANDORA", volume, sleepSeconds)
    }
}

def searchSiriusXm(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("siriusXm")) {
        doSearchMusicCmd(searchPhrase, "SIRIUSXM", volume, sleepSeconds)
    }
}

def searchSpotify(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("spotify")) {
        doSearchMusicCmd(searchPhrase, "SPOTIFY", volume, sleepSeconds)
    }
}

// def searchTidal(String searchPhrase, volume=null, sleepSeconds=null) {
//     if(isCommandTypeAllowed("tidal")) {
//         doSearchMusicCmd(searchPhrase, "TIDAL", volume, sleepSeconds)
//     }
// }

def searchIheart(String searchPhrase, volume=null, sleepSeconds=null) {
    if(isCommandTypeAllowed("iHeartRadio")) {
        doSearchMusicCmd(searchPhrase, "I_HEART_RADIO", volume, sleepSeconds)
    }
}

private doSequenceCmd(cmdType, seqCmd, seqVal="") {
    if(state?.serialNumber) {
        logDebug("Sending (${cmdType}) | Command: ${seqCmd} | Value: ${seqVal}")
        sendSequenceCommand(cmdType, seqCmd, seqVal)
    } else { logWarn("doSequenceCmd Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}", true) }
}

private doSearchMusicCmd(searchPhrase, musicProvId, volume=null, sleepSeconds=null) {
    if(state?.serialNumber && searchPhrase && musicProvId) {
        playMusicProvider(searchPhrase, musicProvId, volume, sleepSeconds)
    } else { logWarn("doSearchMusicCmd Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} | searchPhrase: ${searchPhrase} | musicProvider: ${musicProvId}", true) }
}

private Map validateMusicSearch(searchPhrase, providerId, sleepSeconds=null) {
    Map validObj = [
        type: "Alexa.Music.PlaySearchPhrase",
        operationPayload: [
            deviceType: state?.deviceType,
            deviceSerialNumber: state?.serialNumber,
            customerId: state?.deviceOwnerCustomerId,
            locale: (state?.regionLocale ?: "en-US"),
            musicProviderId: providerId,
            searchPhrase: searchPhrase
        ]
    ]
    if(sleepSeconds) { validObj?.operationPayload?.waitTimeInSeconds = sleepSeconds }
    validObj?.operationPayload = new groovy.json.JsonOutput().toJson(validObj?.operationPayload)
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/operation/validate",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
        body: new groovy.json.JsonOutput().toJson(validObj)
    ]
    Map result = null
    try {
        httpPost(params) { resp->
            Map rData = resp?.data ?: null
            if(resp?.status == 200) {
                if (rData?.result != "VALID") {
                    logError("Amazon the Music Search Request as Invalid | MusicProvider: [${providerId}] | Search Phrase: (${searchPhrase})")
                    result = null
                } else { result = rData }
            } else { logError("validateMusicSearch Request failed with status: (${resp?.status}) | MusicProvider: [${providerId}] | Search Phrase: (${searchPhrase})") }
        }
    } catch (ex) {
        respExceptionHandler(ex, "validateMusicSearch")
    }
    return result
}

private getMusicSearchObj(String searchPhrase, String providerId, sleepSeconds=null) {
    if (searchPhrase == "") { logError("getMusicSearchObj Searchphrase empty"); return; }
    Map validObj = [type: "Alexa.Music.PlaySearchPhrase", "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode"]
    Map validResp = validateMusicSearch(searchPhrase, providerId, sleepSeconds)
    if(validResp && validResp?.operationPayload) {
        validObj?.operationPayload = validResp?.operationPayload
    } else {
        logError("Something went wrong with the Music Search | MusicProvider: [${providerId}] | Search Phrase: (${searchPhrase})")
        validObj = null
    }
    return validObj
}

private playMusicProvider(searchPhrase, providerId, volume=null, sleepSeconds=null) {
    logTrace("playMusicProvider() command received... | searchPhrase: $searchPhrase | providerId: $providerId | sleepSeconds: $sleepSeconds")
    Map validObj = getMusicSearchObj(searchPhrase, providerId, sleepSeconds)
    if(!validObj) { return }
    Map seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": validObj]
        seqJson?.startNode["@type"] = "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode"
    if(volume) {
        sendMultiSequenceCommand([ [command: "volume", value: volume], [command: validObj] ], "playMusicProvider(${providerId})", true)
    } else { sendSequenceCommand("playMusicProvider(${providerId})", seqJson, null) }
}

def setWakeWord(String newWord) {
    logTrace("setWakeWord($newWord) command received...")
    String oldWord = device?.currentValue('alexaWakeWord')
    def wwList = device?.currentValue('wakeWords') ?: []
    logDebug("newWord: $newWord | oldWord: $oldWord | wwList: $wwList (${wwList?.contains(newWord.toString()?.toUpperCase())})")
    if(oldWord && newWord && wwList && wwList?.contains(newWord.toString()?.toUpperCase())) {
        sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/wake-word/${state?.serialNumber}",
            headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
            contentType: "application/json",
            body: [
                active: true,
                deviceSerialNumber: state?.serialNumber,
                deviceType: state?.deviceType,
                displayName: oldWord,
                midFieldState: null,
                wakeWord: newWord
            ]
        ], [cmdDesc: "SetWakeWord(${newWord})"])
        sendEvent(name: "alexaWakeWord", value: newWord?.toString()?.toUpperCase(), display: true, displayed: true)
    } else { logWarn("setWakeWord is Missing a Required Parameter!!!", true) }
}

def createAlarm(String alarmLbl, String alarmDate, String alarmTime) {
    logTrace("createAlarm($alarmLbl, $alarmDate, $alarmTime) command received...")
    if(alarmLbl && alarmDate && alarmTime) {
        createNotification("Alarm", [
            cmdType: "CreateAlarm",
            label: alarmLbl?.toString()?.replaceAll(" ", ""),
            date: alarmDate,
            time: alarmTime,
            type: "Alarm"
        ])
    } else { logWarn("createAlarm is Missing a Required Parameter!!!", true) }
}

def createReminder(String remLbl, String remDate, String remTime) {
    logTrace("createReminder($remLbl, $remDate, $remTime) command received...")
    if(isCommandTypeAllowed("alarms")) {
        if(remLbl && remDate && remTime) {
            createNotification("Reminder", [
                cmdType: "CreateReminder",
                label: remLbl?.toString(),
                date: remDate?.toString(),
                time: remTime?.toString(),
                type: "Reminder"
            ])
        } else { logWarn("createReminder is Missing the Required (id) Parameter!!!", true) }
    }
}

def createReminderNew(String remLbl, String remDate, String remTime, String recurType=null, recurOpt=null) {
    logTrace("createReminderNew($remLbl, $remDate, $remTime, $recurType, $recurOpt) command received...")
    if(isCommandTypeAllowed("alarms")) {
        if(remLbl && remDate && remTime) {
            createNotification("Reminder", [
                cmdType: "CreateReminder",
                label: remLbl?.toString(),
                date: remDate?.toString(),
                time: remTime?.toString(),
                type: "Reminder",
                recur_type: recurType,
                recur_opt: recurOpt
            ])
        } else { logWarn("createReminder is Missing the Required (id) Parameter!!!", true) }
    }
}

def removeNotification(String id) {
    id = generateNotificationKey(id)
    logTrace("removeNotification($id) command received...")
    if(isCommandTypeAllowed("alarms") || isCommandTypeAllowed("reminders", true)) {
        if(id) {
            String translatedID = state?.createdNotifications == null ? null : state?.createdNotifications[id]
            if (translatedID) {
                sendAmazonCommand("DELETE", [
                    uri: getAmazonUrl(),
                    path: "/api/notifications/${id}",
                    headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
                    contentType: "application/json",
                    body: []
                ], [cmdDesc: "RemoveNotification"])
            } else { logWarn("removeNotification Unable to Find Translated ID for ${id}", true) }
        } else { logWarn("removeNotification is Missing the Required (id) Parameter!!!", true) }
    }
}

def removeAllNotificationsByType(String type) {
    logTrace("removeAllNotificationsByType($id) command received...")
    if(isCommandTypeAllowed("alarms") || isCommandTypeAllowed("reminders", true)) {
        def items = getNotifications(type, true)
        if(items?.size()) {
            items?.each { item->
                if (item?.id) {
                    sendAmazonCommand("DELETE", [
                        uri: getAmazonUrl(),
                        path: "/api/notifications/${item?.id}",
                        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
                        contentType: "application/json",
                        body: []
                    ], [cmdDesc: "RemoveNotification"])
                } else { logWarn("removeAllNotificationByType($type) Unable to Find ID for ${item?.id}", true) }
            }
        }// else { logWarn("removeAllNotificationByType($type) is Missing the Required (id) Parameter!!!", true) }
        state?.remove("createdNotifications")
    }
}

private String generateNotificationKey(id) {
    return id?.toString()?.replaceAll(" ", "")
}

//TODO: CreateReminderInXMinutes()
//TODO: RemoveAllReminders() //Remove all Reminders for this device
//TODO: RemoveAllAlarms() //Remove all Alarms for this device
//TODO: Add Recurrence Options to Alarms and Reminders

private createNotification(type, opts) {
    log.trace "createdNotification params: ${opts}"
    String notifKey = generateNotificationKey(opts?.label)
    if (notifKey) {
        String translatedID = state?.createdNotifications == null ? null : state?.createdNotifications[notifKey]
        if (translatedID) {
            logWarn("createNotification found existing notification named ${notifKey}, removing that first")
            removeNotification(notifKey)
        }
    }
    def now = new Date()
    def createdDate = now.getTime()

    def isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
    isoFormat.setTimeZone(location.timeZone)
    def alarmDate = isoFormat.parse("${opts.date}T${opts.time}")
    def alarmTime = alarmDate.getTime()
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/notifications/create${type}",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
        body: [
            type: type,
            status: "ON",
            alarmTime: alarmTime,
            createdDate: createdDate,
            originalTime: type != "Timer" ? "${opts?.time}:00.000" : null,
            originalDate: type != "Timer" ? opts?.date : null,
            timeZoneId: null,
            reminderIndex: null,
            sound: null,
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            timeZoneId: null,
            alarmLabel: type == "Alarm" ? opts?.label : null,
            reminderLabel: type == "Reminder" ? opts?.label : null,
            reminderSubLabel: "Echo Speaks",
            timerLabel: type == "Timer" ? opts?.label : null,
            skillInfo: null,
            isSaveInFlight: type != "Timer" ? true : null,
            id: "create${type}",
            isRecurring: false,
            remainingDuration: type != "Timer" ? 0 : opts?.timerDuration
        ]
    ]
    Map rule = transormRecurString(opts?.recur_type, opts?.recur_opt, opts?.time, opts?.date)
    log.debug "rule: $rule"
    params?.body?.rRuleData = rule?.data ?: null
    params?.body?.recurringPattern = rule?.pattern ?: null
    log.debug "params: ${params?.body}"
    String id = sendAmazonCommand("PUT", params, [cmdDesc: "Create${type}"])
    if (notifKey) {
        if (state?.containsKey("createdNotifications")) {
            state?.createdNotifications[notifKey] = id
        } else { state.createdNotifications = [notifKey: id] }
    }
}

// For simple recurring, all that is needed is the "recurringPattern":
// once per day: "P1D"
// weekly on one day: "XXXX-WXX-3" => Weds
// weekdays: "XXXX-WD"
// weekends: "XXXX-WE"
private transormRecurString(type, opt, tm, dt) {
    log.debug "transormRecurString(type: ${type}, opt: ${opt}, time: ${tm},date: ${dt})"
    Map rd = null
    String rp = null
    if(!type) return [data: rd, pattern: rp]
    def time = tm?.tokenize(':')
    switch(type) {
        case "everyday":
            rd = [:]
            rd?.byMonthDays = null
            rd?.byWeekDays = null
            rd?.flexibleRecurringPatternType = "ONCE_A_DAY"
            rd?.frequency = null
            rd?.intervals = [1]
            rd?.nextTriggerTimes = null
            rd?.notificationTimes = ["${time[0]}:${time[1]}:00.000"]
            rd?.recurEndDate = null
            rd?.recurEndTime = null
            rd?.recurStartDate = null
            rd?.recurStartTime = null
            rd?.recurrenceRules = ["FREQ=DAILY;BYHOUR=${time[0]};BYMINUTE=${time[1]};BYSECOND=0;INTERVAL=1;"]
            rp = "P1D"
            return [data: rd, pattern: rp]
            /* Repeat Everyday @x:xx time
                "rRuleData": {
                    "byMonthDays": null,
                    "byWeekDays": null,
                    "flexibleRecurringPatternType": "ONCE_A_DAY",
                    "frequency": null,
                    "intervals": [
                        1
                    ],
                    "nextTriggerTimes": null,
                    "notificationTimes": [
                        "22:59:00.000"
                    ],
                    "recurEndDate": null,
                    "recurEndTime": null,
                    "recurStartDate": null,
                    "recurStartTime": null,
                    "recurrenceRules": [
                        "FREQ=DAILY;BYHOUR=22;BYMINUTE=59;BYSECOND=0;INTERVAL=1;"
                    ]
                },
                "recurrenceEligibility": false,
                "recurringPattern": "P1D"
            */
            break

        case "weekdays":
            rd = [:]
            rd?.byMonthDays = null
            rd?.byWeekDays = ["MO", "TU", "WE", "TH", "FR"]
            rd?.flexibleRecurringPatternType = "X_TIMES_A_WEEK"
            rd?.frequency = null
            rd?.intervals = [1]
            rd?.nextTriggerTimes = null
            rd?.notificationTimes = []
            rd?.recurEndDate = null
            rd?.recurEndTime = null
            rd?.recurStartDate = null
            rd?.recurStartTime = null
            rd?.recurrenceRules = []
            rd?.byWeekDays?.each { d->
                rd?.notificationTimes?.push("${time[0]}:${time[1]}:00.000")
                rd?.recurrenceRules?.push("FREQ=WEEKLY;BYDAY=${d};BYHOUR=${time[0]};BYMINUTE=${time[1]};BYSECOND=0;INTERVAL=1;")
            }
            rp = "XXXX-WD"
            log.debug "weekdays: ${rd}"
            return [data: rd, pattern: rp]
            /*
                Repeat Every Weekday @ x:xx
                "rRuleData": {
                    "byMonthDays": null,
                    "byWeekDays": [
                        "MO",
                        "TU",
                        "WE",
                        "TH",
                        "FR"
                    ],
                    "flexibleRecurringPatternType": "X_TIMES_A_WEEK",
                    "frequency": null,
                    "intervals": [
                        1
                    ],
                    "nextTriggerTimes": null,
                    "notificationTimes": [
                        "23:01:00.000",
                        "23:01:00.000",
                        "23:01:00.000",
                        "23:01:00.000",
                        "23:01:00.000"
                    ],
                    "recurEndDate": null,
                    "recurEndTime": null,
                    "recurStartDate": null,
                    "recurStartTime": null,
                    "recurrenceRules": [
                        "FREQ=WEEKLY;BYDAY=MO;BYHOUR=23;BYMINUTE=1;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=TU;BYHOUR=23;BYMINUTE=1;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=WE;BYHOUR=23;BYMINUTE=1;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=TH;BYHOUR=23;BYMINUTE=1;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=FR;BYHOUR=23;BYMINUTE=1;BYSECOND=0;INTERVAL=1;"
                    ]
                },
                "recurrenceEligibility": false,
                "recurringPattern": "XXXX-WD",
            */
            break

        case "weekends":
            rd = [:]
            rd?.byMonthDays = null
            rd?.byWeekDays = ["SA", "SU"]
            rd?.flexibleRecurringPatternType = "X_TIMES_A_WEEK"
            rd?.frequency = null
            rd?.intervals = [1]
            rd?.nextTriggerTimes = null
            rd?.notificationTimes = []
            rd?.recurEndDate = null
            rd?.recurEndTime = null
            rd?.recurStartDate = null
            rd?.recurStartTime = null
            rd?.recurrenceRules = []
            rd?.byWeekDays?.each { d->
                rd?.notificationTimes?.push("${time[0]}:${time[1]}:00.000")
                rd?.recurrenceRules?.push("FREQ=WEEKLY;BYDAY=${d};BYHOUR=${time[0]};BYMINUTE=${time[1]};BYSECOND=0;INTERVAL=1;")
            }
            rp = "XXXX-WE"
            return [data: rd, pattern: rp]
            /*
                Repeat on Weekends @x:xx
                "rRuleData": {
                    "byMonthDays": null,
                    "byWeekDays": [
                        "SA",
                        "SU"
                    ],
                    "flexibleRecurringPatternType": "X_TIMES_A_WEEK",
                    "frequency": null,
                    "intervals": [
                        1
                    ],
                    "nextTriggerTimes": null,
                    "notificationTimes": [
                        "23:02:00.000",
                        "23:02:00.000"
                    ],
                    "recurEndDate": null,
                    "recurEndTime": null,
                    "recurStartDate": null,
                    "recurStartTime": null,
                    "recurrenceRules": [
                        "FREQ=WEEKLY;BYDAY=SA;BYHOUR=23;BYMINUTE=2;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=SU;BYHOUR=23;BYMINUTE=2;BYSECOND=0;INTERVAL=1;"
                    ]
                },
                "recurrenceEligibility": false,
                "recurringPattern": "XXXX-WE",
            */
            break

        case "daysofweek":
            rd = [:]
            rd?.byMonthDays = null
            rd?.byWeekDays = opt
            rd?.flexibleRecurringPatternType = "X_TIMES_A_WEEK"
            rd?.frequency = null
            rd?.intervals = []
            rd?.nextTriggerTimes = ["${parseFmtDt("HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", time)}"]
            rd?.notificationTimes = ["${dt}T${time[0]}:${time[1]}:00.000-05:00"]
            rd?.recurEndDate = null
            rd?.recurEndTime = null
            rd?.recurStartDate = null
            rd?.recurStartTime = null
            rd?.recurrenceRules = []
            rd?.byWeekDays?.each { d->
                rd?.intervals?.push(1)
                rd?.notificationTimes?.push("${time[0]}:${time[1]}:00.000")
                rd?.recurrenceRules?.push("FREQ=WEEKLY;BYDAY=${d};BYHOUR=${time[0]};BYMINUTE=${time[1]};BYSECOND=0;INTERVAL=1;")
            }
            rp = null
            return [data: rd, pattern: rp]
            /* Repeat on these day every week
                "rRuleData": {
                    "byMonthDays": [],
                    "byWeekDays": [
                        "TU",
                        "WE",
                        "TH"
                    ],
                    "flexibleRecurringPatternType": "X_TIMES_A_WEEK",
                    "frequency": null,
                    "intervals": [
                        1,
                        1,
                        1
                    ],
                    "nextTriggerTimes": [
                        "2020-01-21T23:00:00.000-05:00"
                    ],
                    "notificationTimes": [
                        "23:00:00.000",
                        "23:00:00.000",
                        "23:00:00.000"
                    ],
                    "recurEndDate": null,
                    "recurEndTime": null,
                    "recurStartDate": null,
                    "recurStartTime": null,
                    "recurrenceRules": [
                        "FREQ=WEEKLY;BYDAY=TU;BYHOUR=23;BYMINUTE=0;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=WE;BYHOUR=23;BYMINUTE=0;BYSECOND=0;INTERVAL=1;",
                        "FREQ=WEEKLY;BYDAY=TH;BYHOUR=23;BYMINUTE=0;BYSECOND=0;INTERVAL=1;"
                    ]
                },
                "recurrenceEligibility": false,
                "recurringPattern": null,
            */
            break

        case "everyxdays":
            rd = [:]
            rd?.byMonthDays = null
            rd?.byWeekDays = opt
            rd?.flexibleRecurringPatternType = "EVERY_X_DAYS"
            rd?.frequency = null
            rd?.intervals = []
            rd?.intervals?.push(recurOpt)
            rd?.nextTriggerTimes = []
            rd?.notificationTimes = ["${dt}T${time[0]}:${time[1]}:00.000-05:00"]
            rd?.recurEndDate = null
            rd?.recurEndTime = null
            rd?.recurStartDate = dt
            rd?.recurStartTime = "${time[0]}:${time[1]}:00.000"
            rd?.recurrenceRules = ["FREQ=DAILY;BYHOUR=${time[0]};BYMINUTE=${time[1]};BYSECOND=0;INTERVAL=${recurOpt};"]
            rp = null
            return [data: rd, pattern: rp]
            /*
                Repeat every 7th day of the month
                "rRuleData": {
                    "byMonthDays": [],
                    "byWeekDays": [],
                    "flexibleRecurringPatternType": "EVERY_X_DAYS",
                    "frequency": null,
                    "intervals": [
                        7
                    ],
                    "nextTriggerTimes": [
                        "2020-01-21T23:00:00.000-05:00"
                    ],
                    "notificationTimes": [
                        "23:00:00.000"
                    ],
                    "recurEndDate": null,
                    "recurEndTime": null,
                    "recurStartDate": "2020-01-21",
                    "recurStartTime": "00:00:00.000",
                    "recurrenceRules": [
                        "FREQ=DAILY;BYHOUR=23;BYMINUTE=0;BYSECOND=0;INTERVAL=7;"
                    ]
                },
                "recurringPattern": null,
            */
            break
    }
}

def renameDevice(newName) {
    logTrace("renameDevice($newName) command received...")
    if(!state?.deviceAccountId) { logError("renameDevice Failed because deviceAccountId is not found..."); return; }
    sendAmazonCommand("PUT", [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device/${state?.serialNumber}",
        headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
        contentType: "application/json",
        body: [
            serialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceAccountId: state?.deviceAccountId,
            accountName: newName
        ]
    ], [cmdDesc: "renameDevice(${newName})"])
}

def connectBluetooth(String btNameOrAddr) {
    logTrace("connectBluetooth(${btName}) command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String curBtAddr = getBtAddrByAddrOrName(btNameOrAddr as String)
        if(curBtAddr) {
            sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/pair-sink/${state?.deviceType}/${state?.serialNumber}",
                headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
                contentType: "application/json",
                body: [ bluetoothDeviceAddress: curBtAddr ]
            ], [cmdDesc: "connectBluetooth($btNameOrAddr)"])
            sendEvent(name: "btDeviceConnected", value: btNameOrAddr, display: true, displayed: true)
        } else { logError("ConnectBluetooth Error: Unable to find the connected bluetooth device address...") }
    }
}

def disconnectBluetooth() {
    logTrace("disconnectBluetooth() command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String curBtAddr = getBtAddrByAddrOrName(device?.currentValue("btDeviceConnected") as String)
        if(curBtAddr) {
            sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/disconnect-sink/${state?.deviceType}/${state?.serialNumber}",
                headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
                contentType: "application/json",
                body: [ bluetoothDeviceAddress: curBtAddr ]
            ], [cmdDesc: "disconnectBluetooth"])
        } else { logError("DisconnectBluetooth Error: Unable to find the connected bluetooth device address...") }
    }
}

def removeBluetooth(String btNameOrAddr) {
    logTrace("removeBluetooth(${btNameOrAddr}) command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String curBtAddr = getBtAddrByAddrOrName(btNameOrAddr)
        if(curBtAddr) {
            sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/unpair-sink/${state?.deviceType}/${state?.serialNumber}",
                headers: [ Cookie: getCookieVal(), csrf: getCsrfVal(), Connection: "keep-alive", DNT: "1" ],
                contentType: "application/json",
                body: [ bluetoothDeviceAddress: curBtAddr, bluetoothDeviceClass: "OTHER" ]
            ], [cmdDesc: "removeBluetooth(${btNameOrAddr})"])
        } else { logError("RemoveBluetooth Error: Unable to find the connected bluetooth device address...") }
    }
}

def sendAlexaAppNotification(String text) {
    // log.debug "sendAlexaAppNotification(${text})"
    doSequenceCmd("AlexaAppNotification", "pushnotification", text)
}

def getRandomItem(items) {
    def list = new ArrayList<String>();
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()));
}

def replayText() {
    logTrace("replayText() command received...")
    String lastText = device?.currentValue("lastSpeakCmd")?.toString()
    if(lastText) { speak(lastText) } else { log.warn "Last Text was not found" }
}

def playText(String msg) {
    logTrace("playText(msg: $msg) command received...")
    speak(msg as String)
}

def playTrackAndResume(uri, duration, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrackAndResume($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            if(volume) {
                def restVolume = device?.currentValue("level")?.toInteger()
                setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
            } else { speak(tts as String) }
        } else {
            logWarn("Uh-Oh... The playTrackAndResume($uri, $volume) Command is NOT Supported by this Device!!!")
        }
    }
}

def playTextAndResume(text, volume=null) {
    logTrace("The playTextAndResume(text: $text, volume: $volume) command received...")
    def restVolume = device?.currentValue("level")?.toInteger()
    if (volume != null) {
        setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
    } else { speak(text as String) }
}

def playTrackAndRestore(uri, duration, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrackAndRestore($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            if(volume) {
                def restVolume = device?.currentValue("level")?.toInteger()
                setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
            } else { speak(tts as String) }
        } else {
            logWarn("Uh-Oh... The playTrackAndRestore(uri: $uri, duration: $duration, volume: $volume) Command is NOT Supported by this Device!!!")
        }
    }
}

def playTextAndRestore(text, volume=null) {
    logTrace("The playTextAndRestore($text, $volume) command received...")
    def restVolume = device?.currentValue("level")?.toInteger()
    if (volume != null) {
        setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
    } else { speak(text as String) }
}

def playURL(uri) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playURL($uri) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            speak(tts as String)
        } else {
            logWarn("Uh-Oh... The playUrl($uri) Command is NOT Supported by this Device!!!")
        }
    }
}

def playSoundAndTrack(soundUri, duration, trackData, volume=null) {
    logWarn("Uh-Oh... The playSoundAndTrack(soundUri: $soundUri, duration: $duration, trackData: $trackData, volume: $volume) Command is NOT Supported by this Device!!!", true)
}

String uriSpeechParser(uri) {
    // Thanks @fkrlaframboise for this idea.  It never for one second occurred to me to parse out the trackUri...
    if (uri?.toString()?.contains("/")) {
        Integer sInd = uri?.lastIndexOf("/") + 1
        uri = uri?.substring(sInd, uri?.size())?.toLowerCase()?.replace(".mp3", "")
        logDebug("uriSpeechParser | tts: $uri")
        return uri
    }
    return null
}

def speechTest(ttsMsg) {
    List items = [
        "Testing Testing 1, 2, 3",
        "Yay!, I'm Alive... Hopefully you can hear me speaking?",
        "Everybody have fun tonight. Everybody have fun tonight. Everybody Wang Chung tonight. Everybody have fun.",
        "Being able to make me say whatever you want is the coolest thing since sliced bread!",
        "I said a hip hop, Hippie to the hippie, The hip, hip a hop, and you don't stop, a rock it out, Bubba to the bang bang boogie, boobie to the boogie To the rhythm of the boogie the beat, Now, what you hear is not a test, I'm rappin' to the beat",
        "This is how we do it!. It's Friday night, and I feel alright. The party is here on the West side. So I reach for my 40 and I turn it up. Designated driver take the keys to my truck, Hit the shore 'cause I'm faded, Honeys in the street say, Monty, yo we made it!. It feels so good in my hood tonight, The summertime skirts and the guys in Khannye.",
        "Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Heroes in a half-shell Turtle power!... They're the world's most fearsome fighting team (We're really hip!), They're heroes in a half-shell and they're green (Hey - get a grip!), When the evil Shredder attacks!!!, These Turtle boys don't cut him no slack!."
    ]
    if(!ttsMsg) { ttsMsg = getRandomItem(items) }
    speak(ttsMsg as String)
}

def speak(String msg) {
    logTrace("speak() command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with speak($msg) command", true) }
        // msg = cleanString(msg, true)
        speechCmd([cmdDesc: "SpeakCommand", message: msg, newVolume: (state?.newVolume ?: null), oldVolume: (state?.oldVolume ?: null), cmdDt: now()])
    }
}

String cleanString(str, frcTrans=false) {
    if(!str) { return null }
    //Cleans up characters from message
    str?.replaceAll(~/[^a-zA-Z0-9-?%°., ]+/, "")?.replaceAll(/\s\s+/, " ")
    str = textTransform(str, frcTrans)
    // log.debug "cleanString: $str"
    return str
}

private String textTransform(String str, force=false) {
    if(!force && settings?.disableTextTransform == true) { return str; }
    // Converts F temp values to readable text "19F"
    str = str?.replaceAll(/([+-]?\d+)\s?([CcFf])/) { return "${it[0]?.toString()?.replaceAll("[-]", "minus ")?.replaceAll("[FfCc]", " degrees")}" }
    str = str?.replaceAll(/(\sWSW\s)/, " west southwest ")?.replaceAll(/(\sWNW\s)/, " west northwest ")?.replaceAll(/(\sESE\s)/, " east southeast ")?.replaceAll(/(\sENE\s)/, " east northeast ")
    str = str?.replaceAll(/(\sSSE\s)/, " south southeast ")?.replaceAll(/(\sSSW\s)/, " south southwest ")?.replaceAll(/(\sNNE\s)/, " north northeast ")?.replaceAll(/(\sNNW\s)/, " north northwest ")
    str = str?.replaceAll(/(\sNW\s)/, " northwest ")?.replaceAll(/(\sNE\s)/, " northeast ")?.replaceAll(/(\sSW\s)/, " southwest ")?.replaceAll(/(\sSE\s)/, " southeast ")
    str = str?.replaceAll(/(\sE\s)/," east ")?.replaceAll(/(\sS\s)/," south ")?.replaceAll(/(\sN\s)/," north ")?.replaceAll(/(\sW\s)/," west ")
    str = str?.replaceAll("%"," percent ")
    str = str?.replaceAll("°"," degree ")
    return str
}

Integer getStringLen(str) { return (str && str?.toString()?.length()) ? str?.toString()?.length() : 0 }

private List msgSeqBuilder(String str) {
    // log.debug "msgSeqBuilder: $str"
    List seqCmds = []
    List strArr = []
    Boolean isSSML = (str?.toString()?.startsWith("<speak>") && str?.toString()?.endsWith("</speak>"))
    if(str?.toString()?.length() < 450) {
        seqCmds?.push([command: (isSSML ? "ssml": "speak"), value: str as String])
    } else {
        List msgItems = str?.split()
        msgItems?.each { wd->
            if((getStringLen(strArr?.join(" ")) + wd?.length()) <= 430) {
                // log.debug "CurArrLen: ${(getStringLen(strArr?.join(" ")))} | CurStrLen: (${wd?.length()})"
                strArr?.push(wd as String)
            } else { seqCmds?.push([command: (isSSML ? "ssml": "speak"), value: strArr?.join(" ")]); strArr = []; strArr?.push(wd as String); }
            if(wd == msgItems?.last()) { seqCmds?.push([command: (isSSML ? "ssml": "speak"), value: strArr?.join(" ")]) }
        }
    }
    // log.debug "seqCmds: $seqCmds"
    return seqCmds
}

def sendTestAnnouncement() {
    playAnnouncement("Echo Speaks announcement test on ${device?.label?.replace("Echo - ", "")}")
}

def sendTestAnnouncementAll() {
    playAnnouncementAll("Echo Speaks Announcement Test on All devices")
}

def sendTestAlexaMsg() {
    sendAlexaAppNotification("Test Alexa Notification from ${device?.displayName}")
}

Map seqItemsAvail() {
    return [
        other: [
            "weather":null, "traffic":null, "flashbriefing":null, "goodmorning":null, "goodnight":null, "cleanup":null,
            "singasong":null, "tellstory":null, "funfact":null, "joke":null, "playsearch":null, "calendartoday":null,
            "calendartomorrow":null, "calendarnext":null, "stop":null, "stopalldevices":null,
            "dnd_duration": "2H30M", "dnd_time": "00:30", "dnd_all_duration": "2H30M", "dnd_all_time": "00:30",
            "dnd_duration":"2H30M", "dnd_time":"00:30",
            "cannedtts_random": ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"],
            "sound": "message",
            "wait": "value (seconds)", "volume": "value (0-100)", "speak": "message", "announcement": "message",
            "announcementall": "message", "pushnotification": "message", "email": null
        ],
        music: [
            "amazonmusic": "AMAZON_MUSIC", "applemusic": "APPLE_MUSIC", "iheartradio": "I_HEART_RADIO", "pandora": "PANDORA",
            "spotify": "SPOTIFY", "tunein": "TUNEIN", "cloudplayer": "CLOUDPLAYER"
        ],
        musicAlt: [
            "amazonmusic": "amazonMusic", "applemusic": "appleMusic", "iheartradio": "iHeartRadio", "pandora": "pandoraRadio",
            "spotify": "spotify", "tunein": "tuneInRadio", "cloudplayer": "cloudPlayer"
        ]
    ]
}

def executeSequenceCommand(String seqStr) {
    if(seqStr) {
        List seqList = seqStr?.split(",,")
        // log.debug "seqList: ${seqList}"
        List seqItems = []
        if(seqList?.size()) {
            seqList?.each {
                def li = it?.toString()?.split("::")
                // log.debug "li: $li"
                if(li?.size()) {
                    String cmd = li[0]?.trim()?.toString()?.toLowerCase() as String
                    Boolean isValidCmd = (seqItemsAvail()?.other?.containsKey(cmd) || (seqItemsAvail()?.music?.containsKey(cmd)) || (seqItemsAvail()?.dnd?.containsKey(cmd)))
                    Boolean isMusicCmd = (seqItemsAvail()?.music?.containsKey(cmd) && !seqItemsAvail()?.other?.containsKey(cmd) && !seqItemsAvail()?.dnd?.containsKey(cmd))
                    // log.debug "cmd: $cmd | isValidCmd: $isValidCmd | isMusicCmd: $isMusicCmd"

                    if(!isValidCmd) { logError("executeSequenceCommand command ($cmd) is not a valid sequence command!!!"); return; }

                    if(isMusicCmd) {
                        List valObj = (li[1]?.trim()?.toString()?.contains("::")) ? li[1]?.trim()?.split("::") : [li[1]?.trim() as String]
                        String provID = seqItemsAvail()?.music[cmd]
                        if(!isCommandTypeAllowed(seqItemsAvail()?.musicAlt[cmd])) { logError("Current Music Sequence command ($cmd) not allowed... "); return; }
                        if (!valObj || valObj[0] == "") { logError("Play Music Sequence it Searchphrase empty"); return; }
                        Map validObj = getMusicSearchObj(valObj[0], provID, valObj[1] ?: null)
                        if(!validObj) { return }
                        seqItems?.push([command: validObj])
                    } else {
                        if(li?.size() == 1) {
                            seqItems?.push([command: cmd])
                        } else if(li?.size() == 2) {
                            seqItems?.push([command: cmd, value: li[1]?.trim()])
                        }
                    }
                }
            }
        }
        logDebug("executeSequenceCommand Items: $seqItems | seqStr: ${seqStr}")
        if(seqItems?.size()) {
            sendMultiSequenceCommand(seqItems, "executeSequenceCommand")
        }
    }
}

/*******************************************************************
            Speech Queue Logic
*******************************************************************/

Integer getRecheckDelay(Integer msgLen=null, addRandom=false) {
    def random = new Random()
    Integer randomInt = random?.nextInt(5) //Was using 7
    Integer twd = ttsWordDelay ? ttsWordDelay?.toInteger() : 2
    if(!msgLen) { return 30 }
    def v = (msgLen <= 14 ? twd : (msgLen / 14)) as Integer
    // logTrace("getRecheckDelay($msgLen) | delay: $v + $randomInt")
    return addRandom ? (v + randomInt) : v+2
}

Integer getLastTtsCmdSec() { return !state?.lastTtsCmdDt ? 1000 : GetTimeDiffSeconds(state?.lastTtsCmdDt).toInteger() }
Integer getLastQueueCheckSec() { return !state?.q_lastCheckDt ? 1000 : GetTimeDiffSeconds(state?.q_lastCheckDt).toInteger() }
Integer getCmdExecutionSec(timeVal) { return !timeVal ? null : GetTimeDiffSeconds(timeVal).toInteger() }

private getQueueSize() {
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("qItem_") }
    return (cmdQueue?.size() ?: 0)
}

private getQueueSizeStr() {
    Integer size = getQueueSize()
    return "($size) Item${size>1 || size==0 ? "s" : ""}"
}

private processLogItems(String t, List ll, es=false, ee=true) {
    if(t && ll?.size() && settings?.logDebug) {
        if(ee) { "log${t?.capitalize()}"(" ") }
        "log${t?.capitalize()}"("└─────────────────────────────")
        ll?.each { "log${t?.capitalize()}"(it) }
        if(es) { "log${t?.capitalize()}"(" ") }
    }
}

private stateCleanup() {
    if(state?.lastVolume) { state?.oldVolume = state?.lastVolume }
    List items = ["qBlocked", "qCmdCycleCnt", "useThisVolume", "lastVolume", "lastQueueCheckDt", "loopChkCnt", "speakingNow",
        "cmdQueueWorking", "firstCmdFlag", "recheckScheduled", "cmdQIndexNum", "curMsgLen", "lastTtsCmdDelay",
        "lastQueueMsg", "lastTtsMsg"
    ]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
}

def resetQueue() {
    logTrace("resetQueue()")
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("qItem_") }
    cmdQueue?.each { cmdKey, cmdData -> state?.remove(cmdKey) }
    unschedule("queueCheck")
    unschedule("checkQueue")
    state?.q_blocked = false
    state?.q_cmdCycleCnt = null
    state?.newVolume = null
    state?.q_lastCheckDt = null
    state?.q_loopChkCnt = null
    state?.q_speakingNow = false
    state?.q_cmdWorking = false
    state?.q_firstCmdFlag = false
    state?.q_recheckScheduled = false
    state?.q_cmdIndexNum = null
    state?.q_curMsgLen = null
    state?.q_lastTtsCmdDelay = null
    state?.q_lastTtsMsg = null
    state?.q_lastMsg = null
}

Integer getNextQueueIndex() { return state?.q_cmdIndexNum ? state?.q_cmdIndexNum+1 : 1 }
Integer getCurrentQueueIndex() { return state?.q_cmdIndexNum ?: 1 }
String getAmazonDomain() { return state?.amazonDomain ?: parent?.settings?.amazonDomain }
String getAmazonUrl() {return "https://alexa.${getAmazonDomain()}"}
Map getQueueItems() { return state?.findAll { it?.key?.toString()?.startsWith("qItem_") } }

private queueCheckSchedHealth() {
    Integer cmdCnt = state?.q_cmdCycleCnt
    Integer lastChk = getLastQueueCheckSec()
    Integer qSize = getQueueSize()
    logDebug("queueCheckSchedHealth | Qsize: ${qSize} | LastChk: ${lastChk}")
    if(qSize >= 2 && lastChk > 120) {
        schedQueueCheck(4, true, null, "queueCheck(missed schedule)")
        logDebug("queueCheck | Scheduling Queue Check for (4 sec) | Possible Lost Recheck Schedule")
    }
}

private schedQueueCheck(Integer delay=30, overwrite=true, data=null, src) {
    Map opts = [:]
    opts["overwrite"] = overwrite
    if(data) { opts["data"] = data }
    runIn(delay, "queueCheck", opts)
    state?.q_recheckScheduled = true
    // log.debug "Scheduled Queue Check for (${delay}sec) | Overwrite: (${overwrite}) | q_recheckScheduled: (${state?.q_recheckScheduled}) | Source: (${src})"
}

public queueEchoCmd(type, msgLen, headers, body=null, firstRun=false) {
    Integer qSize = getQueueSize()
    if(state?.q_blocked == true) { log.warn "│ Queue Temporarily Blocked (${qSize} Items): | Working: (${state?.q_cmdWorking}) | Recheck: (${state?.q_recheckScheduled})"; return; }
    List logItems = []
    Map dupItems = state?.findAll { it?.key?.toString()?.startsWith("qItem_") && it?.value?.type == type && it?.value?.headers && it?.value?.headers?.message == headers?.message }
    logItems?.push("│ Queue Active: (${state?.q_cmdWorking}) | Recheck: (${state?.q_recheckScheduled}) ")
    if(dupItems?.size()) {
        if(headers?.message) { logItems?.push("│ Message(${msgLen} char): ${headers?.message?.take(190)?.trim()}${msgLen > 190 ? "..." : ""}") }
        logItems?.push("│ Ignoring (${headers?.cmdType}) Command... It Already Exists in QUEUE!!!")
        logItems?.push("┌────────── Echo Queue Warning ──────────")
        processLogItems("warn", logItems, true, true)
        return
    }
    Integer qIndNum = getNextQueueIndex()
    // log.debug "qIndexNum: $qIndNum"
    state?.q_cmdIndexNum = qIndNum
    headers?.qId = qIndNum
    state?."qItem_${qIndNum}" = [type: type, headers: headers, body: body, newVolume: (headers?.newVolume ?: null), oldVolume: (headers?.oldVolume ?: null)]
    state?.newVolume = null
    state?.oldVolume = null
    if(headers?.volume)  {  logItems?.push("│ Volume (${headers?.volume})") }
    if(headers?.message) {  logItems?.push("│ Message(Len: ${headers?.message?.toString()?.length()}): ${headers?.message?.take(200)?.trim()}${headers?.message?.toString()?.length() > 200 ? "..." : ""}") }
    if(headers?.cmdType) {  logItems?.push("│ CmdType: (${headers?.cmdType})") }
                            logItems?.push("┌───── Added Echo Queue Item (${state?.q_cmdIndexNum}) ─────")
    // queueCheckSchedHealth()
    if(!firstRun) {
        processLogItems("trace", logItems, false, true)
    }
}

private queueCheck(data) {
    // log.debug "queueCheck | ${data}"
    Integer qSize = getQueueSize()
    Boolean qEmpty = (qSize == 0)
    state?.q_lastCheckDt = getDtNow()
    if(!qEmpty) {
        if(qSize && qSize >= 10) {
            state?.q_blocked = true
            if (qSize < 20) {
                logWarn("queueCheck | Queue Item Count (${qSize}) is filling up... Blocking Queue Additions Until Queue Size Drops below 10!!!", true)
                schedQueueCheck(delay, true, null, "queueCheck(filling)")
            } else {
                logWarn("queueCheck | Queue Item Count (${qSize}) is abnormally high... Resetting Queue", true)
                resetQueue()
                return
            }
        } else { state?.q_blocked = false }
        if(data && data?.rateLimited == true) {
            Integer delay = data?.delay as Integer ?: getRecheckDelay(state?.q_curMsgLen)
            schedQueueCheck(delay, true, null, "queueCheck(rate-limit)")
            logDebug("queueCheck | Scheduling Queue Check for (${delay} sec) | Recheck for RateLimiting")
        }
        processCmdQueue()
        return
    } else {
        logDebug("queueCheck | Nothing in the Queue | Performing Queue Reset...")
        resetQueue()
        return
    }
}

void processCmdQueue() {
    state?.q_cmdWorking = true
    Integer q_cmdCycleCnt = state?.q_cmdCycleCnt
    state?.q_cmdCycleCnt = q_cmdCycleCnt ? q_cmdCycleCnt+1 : 1
    Map cmdQueue = getQueueItems()
    if(cmdQueue?.size()) {
        state?.q_recheckScheduled = false
        def cmdKey = cmdQueue?.keySet()?.sort(false) { it.tokenize('_')[-1] as Integer }?.first()
        Map cmdData = state[cmdKey as String]
        // logDebug("processCmdQueue | Key: ${cmdKey} | Queue Items: (${getQueueItems()})")
        cmdData?.headers["queueKey"] = cmdKey
        Integer q_loopChkCnt = state?.q_loopChkCnt ?: 0
        if(state?.q_lastTtsMsg == cmdData?.headers?.message && (getLastTtsCmdSec() <= 10)) { state?.q_loopChkCnt = (q_loopChkCnt >= 1) ? q_loopChkCnt++ : 1 }
        // log.debug "q_loopChkCnt: ${state?.q_loopChkCnt}"
        if(state?.q_loopChkCnt && (state?.q_loopChkCnt > 4) && (getLastTtsCmdSec() <= 10)) {
            state?.remove(cmdKey as String)
            logWarn("processCmdQueue | Possible loop detected... Last message was the same as message sent <10 seconds ago. This message will be removed from the queue")
            schedQueueCheck(2, true, null, "processCmdQueue(removed duplicate)")
            state?.q_cmdWorking = false
        } else {
            state?.q_lastMsg = cmdData?.headers?.message
            speechCmd(cmdData?.headers, true)
        }
    } else { state?.q_cmdWorking = false }
}

Integer getAdjCmdDelay(elap, reqDelay) {
    if(elap && reqDelay) {
        Integer res = (elap - reqDelay)?.abs()
        // log.debug "getAdjCmdDelay | reqDelay: $reqDelay | elap: $elap | res: ${res+3}"
        return res < 3 ? 3 : res+3
    }
    return 5
}

def testMultiCmd() {
    sendMultiSequenceCommand([[command: "volume", value: 60], [command: "speak", value: "super duper test message 1, 2, 3"], [command: "volume", value: 30]], "testMultiCmd")
}

private speechCmd(headers=[:], isQueueCmd=false) {
    if(isQueueCmd) log.warn "Blocked: ${state?.q_blocked} | cycleCnt: ${state?.q_cmdCycleCnt} | isQCmd: ${isQueueCmd}"
    state?.q_speakingNow = true
    def tr = "speechCmd (${headers?.cmdDesc}) | Msg: ${headers?.message}"
    tr += headers?.newVolume ? " | SetVolume: (${headers?.newVolume})" : ""
    tr += headers?.oldVolume ? " | Restore Volume: (${headers?.oldVolume})" : ""
    tr += headers?.msgDelay  ? " | RecheckSeconds: (${headers?.msgDelay})" : ""
    tr += headers?.queueKey  ? " | QueueItem: [${headers?.queueKey}]" : ""
    tr += headers?.cmdDt     ? " | CmdDt: (${headers?.cmdDt})" : ""
    logTrace("${tr}")

    def random = new Random()
    def randCmdId = random?.nextInt(300)

    Map queryMap = [:]
    List logItems = []
    String healthStatus = getHealthStatus()
    if(!headers || !(healthStatus in ["ACTIVE", "ONLINE"])) {
        if(!headers) { logError("speechCmd | Error${!headers ? " | headers are missing" : ""} ") }
        if(!(healthStatus in ["ACTIVE", "ONLINE"])) { logWarn("Command Ignored... Device is current in OFFLINE State", true) }
        return
    }
    Boolean isTTS = true
    // headers?.message = cleanString(headers?.message)
    Integer lastTtsCmdSec = getLastTtsCmdSec()
    Integer msgLen = headers?.message?.toString()?.length()
    Integer recheckDelay = getRecheckDelay(msgLen)
    headers["msgDelay"] = recheckDelay
    headers["cmdId"] = randCmdId
    if(!settings?.disableQueue) {
        logItems?.push("│ Last TTS Sent: (${lastTtsCmdSec} seconds) ")

        Boolean isFirstCmd = (state?.q_firstCmdFlag != true)
        if(isFirstCmd) {
            logItems?.push("│ First Command: (${isFirstCmd})")
            headers["queueKey"] = "qItem_1"
            state?.q_firstCmdFlag = true
        }
        Boolean sendToQueue = (isFirstCmd || (lastTtsCmdSec < 3) || (!isQueueCmd && state?.q_speakingNow == true))
        if(!isQueueCmd) { logItems?.push("│ SentToQueue: (${sendToQueue})") }
        log.warn "speechCmd - QUEUE DEBUG | sendToQueue: (${sendToQueue?.toString()?.capitalize()}) | isQueueCmd: (${isQueueCmd?.toString()?.capitalize()})() | lastTtsCmdSec: [${lastTtsCmdSec}] | isFirstCmd: (${isFirstCmd?.toString()?.capitalize()}) | q_speakingNow: (${state?.q_speakingNow?.toString()?.capitalize()}) | RecheckDelay: [${recheckDelay}]"
        if(sendToQueue) {
            queueEchoCmd("Speak", msgLen, headers, body, isFirstCmd)
            runIn((settings?.autoResetQueue ?: 180), "resetQueue")
            if(!isFirstCmd) { return }
        }
    }
    try {
        Map headerMap = [cookie: getCookieVal(), csrf: getCsrfVal()]
        headers?.each { k,v-> headerMap[k] = v }
        Integer qSize = getQueueSize()
        logItems?.push("│ Queue Items: (${qSize>=1 ? qSize-1 : 0}) │ Working: (${state?.q_cmdWorking})")

        if(headers?.message) {
            state?.q_curMsgLen = msgLen
            state?.q_lastTtsCmdDelay = recheckDelay
            schedQueueCheck(recheckDelay, true, null, "speechCmd(sendCloudCommand)")
            logItems?.push("│ Rechecking: (${recheckDelay} seconds)")
            logItems?.push("│ Message(${msgLen} char): ${headers?.message?.take(190)?.trim()}${msgLen > 190 ? "..." : ""}")
            state?.q_lastTtsMsg = headers?.message
            // state?.lastTtsCmdDt = getDtNow()
        }
        if(headerMap?.oldVolume) {logItems?.push("│ Restore Volume: (${headerMap?.oldVolume}%)") }
        if(headerMap?.newVolume) {logItems?.push("│ New Volume: (${headerMap?.newVolume}%)") }
        logItems?.push("│ Current Volume: (${device?.currentValue("volume")}%)")
        Boolean isSSML = (headers?.message?.toString()?.startsWith("<speak>") && headers?.message?.toString()?.endsWith("</speak>"))
        logItems?.push("│ Command: (SpeakCommand)${isSSML ? " | (SSML)" : ""}")
        try {
            def bodyObj = null
            List seqCmds = []
            if(headerMap?.newVolume) { seqCmds?.push([command: "volume", value: headerMap?.newVolume]) }
            seqCmds = seqCmds + msgSeqBuilder(headerMap?.message)
            if(headerMap?.oldVolume) { seqCmds?.push([command: "volume", value: headerMap?.oldVolume]) }
            bodyObj = new groovy.json.JsonOutput().toJson(multiSequenceBuilder(seqCmds))

            Map params = [
                uri: getAmazonUrl(),
                path: "/api/behaviors/preview",
                headers: headerMap,
                contentType: "application/json",
                body: bodyObj
            ]
            Map extData = [
                cmdDt:(headerMap?.cmdDt ?: null), queueKey: (headerMap?.queueKey ?: null), cmdDesc: (headerMap?.cmdDesc ?: null), msgLen: msgLen, isSSML: isSSML, deviceId: device?.getDeviceNetworkId(), msgDelay: (headerMap?.msgDelay ?: null),
                message: (headerMap?.message ? (isST() && msgLen > 700 ? headerMap?.message?.take(700) : headerMap?.message) : null), newVolume: (headerMap?.newVolume ?: null), oldVolume: (headerMap?.oldVolume ?: null), cmdId: (headerMap?.cmdId ?: null),
                qId: (headerMap?.qId ?: null)
            ]
            httpPost(params) { response->
                def sData = response?.data ?: null
                extData["amznReqId"] = response?.headers["x-amz-rid"] ?: null
                postCmdProcess(sData, response?.status, extData)
            }
        } catch (ex) {
            respExceptionHandler(ex, "speechCmd")
        }
        logItems?.push("┌─────── Echo Command ${isQueueCmd && !settings?.disableQueue ? " (From Queue) " : ""} ────────")
        processLogItems("debug", logItems)
    } catch (ex) {
        logError("speechCmd Exception: ${ex}")
    }
}

private postCmdProcess(resp, statusCode, data) {
    if(data && data?.deviceId && (data?.deviceId == device?.getDeviceNetworkId())) {
        String respMsg = resp?.message ?: null
        String respMsgLow = resp?.message ? resp?.message?.toString()?.toLowerCase() : null
        if(statusCode == 200) {
            def execTime = data?.cmdDt ? (now()-data?.cmdDt) : 0
            if(data?.queueKey) {
                logDebug("Command Completed | Removing Queue Item: ${data?.queueKey}")
                state?.remove(data?.queueKey as String)
            }
            def pi = "${data?.cmdDesc ? "${data?.cmdDesc}" : "Command"}"
            pi += data?.isSSML ? " (SSML)" : ""
            pi += " Sent"
            pi += " | (${data?.message})"
            pi += logDebug && !logInfo && data?.msgLen ? " | Length: (${data?.msgLen}) " : ""
            pi += data?.msgDelay ? " | Runtime: (${data?.msgDelay} sec)" : ""
            pi += logDebug && data?.amznReqId ? " | Amazon Request ID: ${data?.amznReqId}" : ""
            pi += logDebug && data?.qId ? " | QueueID: (${data?.qId})" : ""
            pi += " | QueueItems: (${getQueueSize()})"
            pi += " | Execution Time: (${execTime}ms)"
            logInfo("${pi}")

            if(data?.cmdDesc && data?.cmdDesc == "SpeakCommand" && data?.message) {
                state?.lastTtsCmdDt = getDtNow()
                String lastMsg = data?.message as String ?: "Nothing to Show Here..."
                sendEvent(name: "lastSpeakCmd", value: "${lastMsg}", descriptionText: "Last Speech text: ${lastMsg}", display: true, displayed: true)
                sendEvent(name: "lastCmdSentDt", value: "${state?.lastTtsCmdDt}", descriptionText: "Last Command Timestamp: ${state?.lastTtsCmdDt}", display: false, displayed: false)
                if(data?.oldVolume || data?.newVolume) {
                    sendEvent(name: "level", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: false, displayed: false)
                    sendEvent(name: "volume", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: false, displayed: false)
                }
                schedQueueCheck(getAdjCmdDelay(getLastTtsCmdSec(), data?.msgDelay), true, null, "postCmdProcess(adjDelay)")
                logSpeech(data?.message, statusCode, null)
            }
            return
        } else if((statusCode?.toInteger() in [400, 429]) && respMsgLow && (respMsgLow in ["rate exceeded", "too many requests"])) {
            switch(respMsgLow) {
                case "rate exceeded":
                    Integer rDelay = 3
                    logWarn("You've been rate-limited by Amazon for sending too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    schedQueueCheck(rDelay, true, [rateLimited: true, delay: data?.msgDelay], "postCmdProcess(Rate-Limited)")
                    break
                case "too many requests":
                    Integer rDelay = 5
                    logWarn("You've sent too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    schedQueueCheck(rDelay, true, [rateLimited: false, delay: data?.msgDelay], "postCmdProcess(Too-Many-Requests)")
                    break
            }
            logSpeech(data?.message, statusCode, respMsg)
            return
        } else {
            logError("postCmdProcess Error | status: ${statusCode} | Msg: ${respMsg}")
            logSpeech(data?.message, statusCode, respMsg)
            resetQueue()
            return
        }
    }
}

/*****************************************************
                HELPER FUNCTIONS
******************************************************/
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/icons/$imgName" }
Integer versionStr2Int(str) { return str ? str.toString()?.replaceAll("\\.", "")?.toInteger() : null }
Boolean checkMinVersion() { return (versionStr2Int(devVersion()) < parent?.minVersions()["echoDevice"]) }
def getDtNow() {
    def now = new Date()
    return formatDt(now, false)
}

def getIsoDtNow() {
    def tf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(new Date());
}

def formatDt(dt, mdy = true) {
    def formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
    def tf = new java.text.SimpleDateFormat(formatVal)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(dt)
}

def GetTimeDiffSeconds(strtDate, stpDate=null) {
    if((strtDate && !stpDate) || (strtDate && stpDate)) {
        def now = new Date()
        def stopVal = stpDate ? stpDate.toString() : formatDt(now, false)
        def start = Date.parse("E MMM dd HH:mm:ss z yyyy", strtDate)?.getTime()
        def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", stopVal)?.getTime()
        def diff = (int) (long) (stop - start) / 1000
        return diff
    } else { return null }
}

def parseDt(dt, dtFmt) {
    return Date.parse(dtFmt, dt)
}

def parseFmtDt(parseFmt, newFmt, dt) {
    def newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(newDt)
}

Boolean ok2Notify() {
    return (parent?.getOk2Notify())
}

private logSpeech(msg, status, error=null) {
    Map o = [:]
    if(status) o?.code = status
    if(error) o?.error = error
    addToLogHistory("speechHistory", msg, o, 5)
}
Integer stateSize() { def j = new groovy.json.JsonOutput().toJson(state); return j?.toString().length(); }
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0); }
private addToLogHistory(String logKey, msg, statusData, Integer max=10) {
    Boolean ssOk = (stateSizePerc() > 70)
    List eData = state?.containsKey(logKey as String) ? state[logKey as String] : []
    if(eData?.find { it?.message == msg }) { return; }
    if(status) { eData.push([dt: getDtNow(), message: msg, status: statusData]) }
    else { eData.push([dt: getDtNow(), message: msg]) }
    if(!ssOK || eData?.size() > max) { eData = eData?.drop( (eData?.size()-max) ) }
    state[logKey as String] = eData
}
private logDebug(msg) { if(settings?.logDebug == true) { sendLog(msg, "debug"); log.debug "Echo (v${devVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { sendLog(msg, "info"); log.info " Echo (v${devVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { sendLog(msg, "trace"); log.trace "Echo (v${devVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { sendLog(msg, "warn"); log.warn " Echo (v${devVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15); } }
private logError(msg, noHist=false) { if(settings?.logError != false) { sendLog(msg, "error"); log.error "Echo (v${devVersion()}) | ${msg}"; }; if(noHist) { addToLogHistory("errorHistory", msg, null, 15); } }

public sendLog(msg, lvl) {
    if(state?.useLogDevice) {
        parent?.logToDevice(device?.displayName, "device", msg, devVersion(), lvl)
    }
}

Map getLogHistory() {
    return [ warnings: state?.warnHistory ?: [], errors: state?.errorHistory ?: [], speech: state?.speechHistory ?: [] ]
}
public clearLogHistory() {
    state?.warnHistory = []
    state?.errorHistory = []
    state?.speechHistory = []
}

private incrementCntByKey(String key) {
    long evtCnt = state?."${key}" ?: 0
    evtCnt++
    state?."${key}" = evtCnt?.toLong()
}

String getObjType(obj) {
    if(obj instanceof String) {return "String"}
    else if(obj instanceof GString) {return "GString"}
    else if(obj instanceof Map) {return "Map"}
    else if(obj instanceof LinkedHashMap) {return "LinkedHashMap"}
    else if(obj instanceof HashMap) {return "HashMap"}
    else if(obj instanceof List) {return "List"}
    else if(obj instanceof ArrayList) {return "ArrayList"}
    else if(obj instanceof Integer) {return "Integer"}
    else if(obj instanceof BigInteger) {return "BigInteger"}
    else if(obj instanceof Long) {return "Long"}
    else if(obj instanceof Boolean) {return "Boolean"}
    else if(obj instanceof BigDecimal) {return "BigDecimal"}
    else if(obj instanceof Float) {return "Float"}
    else if(obj instanceof Byte) {return "Byte"}
    else { return "unknown"}
}

public Map getDeviceMetrics() {
    Map out = [:]
    def cntItems = state?.findAll { it?.key?.startsWith("use_") }
    def errItems = state?.findAll { it?.key?.startsWith("err_") }
    if(cntItems?.size()) {
        out["usage"] = [:]
        cntItems?.each { k,v -> out?.usage[k?.toString()?.replace("use_", "") as String] = v as Integer ?: 0 }
    }
    if(errItems?.size()) {
        out["errors"] = [:]
        errItems?.each { k,v -> out?.errors[k?.toString()?.replace("err_", "") as String] = v as Integer ?: 0 }
    }
    return out
}

private getPlatform() {
    String p = "SmartThings"
    if(state?.hubPlatform == null) {
        try { [dummy: "dummyVal"]?.encodeAsJson(); } catch (e) { p = "Hubitat" }
        // if (location?.hubs[0]?.id?.toString()?.length() > 5) { p = "SmartThings" } else { p = "Hubitat" }
        state?.hubPlatform = p
        logDebug("hubPlatform: (${state?.hubPlatform})")
    }
    return state?.hubPlatform
}

Map sequenceBuilder(cmd, val) {
    def seqJson = null
    if (cmd instanceof Map) {
        seqJson = cmd?.sequence ?: cmd
    } else { seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": createSequenceNode(cmd, val)] }
    Map seqObj = [behaviorId: (seqJson?.sequenceId ? cmd?.automationId : "PREVIEW"), sequenceJson: new groovy.json.JsonOutput().toJson(seqJson) as String, status: "ENABLED"]
    return seqObj
}

Map multiSequenceBuilder(commands, parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList?.push(createSequenceNode(cmdItem?.command, cmdItem?.value)) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    Map seqObj = sequenceBuilder(seqJson, null)
    return seqObj
}

Map createSequenceNode(command, value, devType=null, devSerial=null) {
    try {
        Boolean remDevSpecifics = false
        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            "operationPayload": [
                "deviceType": devType ?: state?.deviceType,
                "deviceSerialNumber": devSerial ?: state?.serialNumber,
                "locale": (state?.regionLocale ?: "en-US"),
                "customerId": state?.deviceOwnerCustomerId
            ]
        ]
        switch (command?.toString()?.toLowerCase()) {
            case "weather":
                seqNode?.type = "Alexa.Weather.Play"
                break
            case "traffic":
                seqNode?.type = "Alexa.Traffic.Play"
                break
            case "flashbriefing":
                seqNode?.type = "Alexa.FlashBriefing.Play"
                break
            case "goodmorning":
                seqNode?.type = "Alexa.GoodMorning.Play"
                break
            case "goodnight":
                seqNode?.type = "Alexa.GoodNight.Play"
                break
            case "cleanup":
                seqNode?.type = "Alexa.CleanUp.Play"
                break
            case "singasong":
                seqNode?.type = "Alexa.SingASong.Play"
                break
            case "tellstory":
                seqNode?.type = "Alexa.TellStory.Play"
                break
            case "funfact":
                seqNode?.type = "Alexa.FunFact.Play"
                break
            case "joke":
                seqNode?.type = "Alexa.Joke.Play"
                break
            case "calendartomorrow":
                seqNode?.type = "Alexa.Calendar.PlayTomorrow"
                break
            case "calendartoday":
                seqNode?.type = "Alexa.Calendar.PlayToday"
                break
            case "calendarnext":
                seqNode?.type = "Alexa.Calendar.PlayNext"
                break
            case "stop":
                seqNode?.type = "Alexa.DeviceControls.Stop"
                break
            case "stopalldevices":
                remDevSpecifics = true
                seqNode?.type = "Alexa.DeviceControls.Stop"
                seqNode?.operationPayload?.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                seqNode?.operationPayload?.isAssociatedDevice = false
                break
            case "cannedtts_random":
                List okVals = ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
                if(!(value in okVals)) { return null }
                seqNode?.type = "Alexa.CannedTts.Speak"
                seqNode?.operationPayload?.cannedTtsStringId = "alexa.cannedtts.speak.curatedtts-category-${value}/alexa.cannedtts.speak.curatedtts-random"
                break
            case "cannedtts":
                List okVals = ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
                if(!(value in okVals)) { return null }
                seqNode?.type = "Alexa.CannedTts.Speak"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : [value as String, value as String]
                seqNode?.operationPayload?.cannedTtsStringId = "alexa.cannedtts.speak.curatedtts-category-${valObj[0]}/alexa.cannedtts.speak.curatedtts-${valObj[1]}"
                break
            case "sound":
                String sndName = ""
                if(value?.startsWith("amzn_sfx_")) {
                    sndName = value
                } else {
                    Map sounds = parent?.getAvailableSounds()
                    if(!(sounds[value])) { return null }
                    sndName = sounds[value]
                }
                seqNode?.type = "Alexa.Sound"
                seqNode?.operationPayload?.soundStringId = sndName
                break
            case "wait":
                remDevSpecifics = true
                seqNode?.operationPayload?.remove('customerId')
                seqNode?.type = "Alexa.System.Wait"
                seqNode?.operationPayload?.waitTimeInSeconds = value?.toInteger() ?: 5
                break
            case "volume":
                seqNode?.type = "Alexa.DeviceControls.Volume"
                seqNode?.operationPayload?.value = value;
                break
            case "dnd_duration":
            case "dnd_time":
            case "dnd_all_duration":
            case "dnd_all_time":
                remDevSpecifics = true
                seqNode?.type = "Alexa.DeviceControls.DoNotDisturb"
                seqNode?.skillId = "amzn1.ask.1p.alexadevicecontrols"
                seqNode?.operationPayload?.customerId = state?.deviceOwnerCustomerId
                if(command == "dnd_all_time" || command == "dnd_all_duration") {
                    seqNode?.operationPayload?.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                }
                if(command == "dnd_time" || command == "dnd_duration") {
                    seqNode?.operationPayload?.devices = [ [deviceAccountId: state?.deviceAccountId, deviceType: state?.deviceType, deviceSerialNumber: state?.serialNumber] ]
                }
                seqNode?.operationPayload?.action = "Enable"
                if(command == "dnd_time" || command == "dnd_all_time") {
                    seqNode?.operationPayload?.until = "TIME#T${value}"
                } else if (command == "dnd_duration" || command == "dnd_all_duration") { seqNode?.operationPayload?.duration = "DURATION#PT${value}" }
                seqNode?.operationPayload?.timeZoneId = "America/Detroit" //location?.timeZone?.ID ?: null
                break
            case "speak":
                seqNode?.type = "Alexa.Speak"
                value = cleanString(value as String)
                seqNode?.operationPayload?.textToSpeak = value as String
                break
            case "ssml":
            case "announcement":
            case "announcementall":
            case "announcement_devices":
                remDevSpecifics = true
                seqNode?.type = "AlexaAnnouncement"
                seqNode?.operationPayload?.expireAfter = "PT5S"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : ["Echo Speaks", value as String]
                // log.debug "valObj(size: ${valObj?.size()}): $valObj"
                // valObj[1] = valObj[1]?.toString()?.replace(/([^0-9]?[0-9]+)\.([0-9]+[^0-9])?/, "\$1,\$2")
                // log.debug "valObj[1]: ${valObj[1]}"
                seqNode?.operationPayload?.content = [[ locale: (state?.regionLocale ?: "en-US"), display: [ title: valObj[0], body: valObj[1]?.toString().replaceAll(/<[^>]+>/, '') ], speak: [ type: (command == "ssml" ? "ssml" : "text"), value: valObj[1] as String ] ] ]
                seqNode?.operationPayload?.target = [ customerId : state?.deviceOwnerCustomerId ]
                if(!(command in ["announcementall", "announcement_devices"])) {
                    seqNode?.operationPayload?.target?.devices = [ [ deviceTypeId: state?.deviceType, deviceSerialNumber: state?.serialNumber ] ]
                } else if(command == "announcement_devices" && valObj?.size() && valObj[2] != null) {
                    List devObjs = new groovy.json.JsonSlurper().parseText(valObj[2])
                    seqNode?.operationPayload?.target?.devices = devObjs
                }
                break
            case "pushnotification":
                remDevSpecifics = true
                seqNode?.type = "Alexa.Notifications.SendMobilePush"
                seqNode?.skillId = "amzn1.ask.1p.alexanotifications"
                seqNode?.operationPayload?.notificationMessage = value as String
                seqNode?.operationPayload?.alexaUrl = "#v2/behaviors"
                seqNode?.operationPayload?.title = "Echo Speaks"
                break
            case "email":
                seqNode?.type = "Alexa.Operation.SkillConnections.Email.EmailSummary"
                seqNode?.skillId = "amzn1.ask.1p.email"
                seqNode?.operationPayload?.targetDevice = [deviceType: state?.deviceType, deviceSerialNumber: state?.serialNumber ]
                seqNode?.operationPayload?.connectionRequest = [uri: "connection://AMAZON.Read.EmailSummary/amzn1.alexa-speechlet-client.DOMAIN:ALEXA_CONNECT", input: [:] ]
                seqNode?.operationPayload?.remove('deviceType')
                seqNode?.operationPayload?.remove('deviceSerialNumber')
                break
            default:
                return null
        }
        if(remDevSpecifics) {
            seqNode?.operationPayload?.remove('deviceType')
            seqNode?.operationPayload?.remove('deviceSerialNumber')
            seqNode?.operationPayload?.remove('locale')
        }
        return seqNode
    } catch (ex) {
        logError("createSequenceNode Exception: ${ex}")
        return [:]
    }
}
