/**
 *	Echo Speaks Device (Hubitat ONLY)
 *
 *  Copyright 2018, 2019, 2020, 2021 Anthony Santilli
 *  Code Contributions by @nh.schottfam
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

 /* TODO: 
    Use a call to the parent to get the token and compare it with the current field variable and update the variable if they are different
*/

import groovy.transform.Field

// STATICALLY DEFINED VARIABLES
@Field static final String devVersionFLD  = "4.0.2.0"
@Field static final String appModifiedFLD = "2021-01-25"
@Field static final String branchFLD      = "master"
@Field static final String platformFLD    = "Hubitat"
@Field static final Boolean betaFLD       = false
@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sTRUE          = 'true'
@Field static final String sFALSE         = 'false'
@Field static final String sAPPJSON       = 'application/json'

// IN-MEMORY VARIABLES (Cleared only on HUB REBOOT or CODE UPDATES)
@Field volatile static Map<String,Map> historyMapFLD = [:]
@Field volatile static Map<String,Map> cookieDataFLD = [:]

static String devVersion()  { return devVersionFLD }
static Boolean isWS()       { return false }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/drivers/echo-speaks-device.groovy") {
        // capability "Audio Mute" // Not Compatible with Hubitat
        capability "Audio Notification"
        // capability "Audio Track Data" // Not Compatible with Hubitat
        capability "Audio Volume"
        capability "Music Player"
        capability "Notification"
        capability "Refresh"
        capability "Sensor"
        capability "Speech Synthesis"

        attribute "alarmVolume", "number"
        // attribute "alexaNotifications", "JSON_OBJECT"
        attribute "alexaPlaylists", "JSON_OBJECT"
        // attribute "alexaGuardStatus", "string"
        attribute "alexaWakeWord", "string"
        attribute "btDeviceConnected", "string"
        attribute "btDevicesPaired", "JSON_OBJECT"
        attribute "currentAlbum", "string"
        attribute "currentStation", "string"
        attribute "deviceFamily", "string"
        attribute "deviceSerial", "string"

        attribute "deviceStatus", "string"
        attribute "deviceStyle", "string"
        attribute "deviceType", "string"
        attribute "doNotDisturb", "string"
        attribute "firmwareVer", "string"
        attribute "followUpMode", "string"
        attribute "lastCmdSentDt", "string"
        attribute "lastSpeakCmd", "string"
        attribute "lastAnnouncement", "string"
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
        // attribute "wifiNetwork", "string"
        attribute "wasLastSpokenToDevice", "string"
	    
	attribute "audioTrackData", "JSON_OBJECT" // To support SharpTools.io Album Art feature

/* // these are part of audioNotification
        command "playText", ["STRING"] //This command is deprecated in ST but will work
        command "playTextAndRestore"
        command "playTextAndResume"
        command "playTrack", ["STRING"]
        command "playTrackAndResume"
        command "playTrackAndRestore"
*/
        command "replayText"
        command "doNotDisturbOn"
        command "doNotDisturbOff"
        // command "followUpModeOn"
        // command "followUpModeOff"
        command "setAlarmVolume", [[name: "Alarm Volume*", type: "NUMBER", description: "Sets the devices Alarm notification volume"]]
        command "resetQueue"
        command "playWeather", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playSingASong", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playFlashBrief", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playFunFact", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playGoodNews", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playTraffic", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playJoke", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playSoundByName", [[name: "Sound Name", type: "STRING", description: "Sound object name"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playTellStory", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodbye", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodNight", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayBirthday", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayCompliment", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodMorning", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayWelcomeHome", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        // command "playCannedRandomTts", ["string", "number", "number"]
        // command "playCannedTts", ["string", "string", "number", "number"]
        command "playAnnouncement", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playAnnouncement", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playAnnouncementAll", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"]]
        command "playCalendarToday", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playCalendarTomorrow", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playCalendarNext", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "stopAllDevices"
        command "searchMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."], [name: "Music Provider*", type: "ENUM", constraints: ["AMAZON_MUSIC", "APPLE_MUSIC", "TUNEIN", "PANDORA", "SIRIUSXM", "SPOTIFY", "I_HEART_RADIO"], description: "Select One of these Music Providers to use."], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchAmazonMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchAppleMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchPandora", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchIheart", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchSiriusXm", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchSpotify", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        // command "searchTidal", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "searchTuneIn", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sendAlexaAppNotification", [ [name: "Notification Message*", type: "STRING", description: ""]]
        command "executeSequenceCommand", [[name: "Sequence Message Text*", type: "STRING", description: ""]]
        command "executeRoutineId", [[name: "Routine ID*", type: "STRING", description: ""]]
        command "createAlarm", [[name: "Alarm Label*", type: "STRING", description: "This is the title of the alarm"], [name: "Date*",type: "STRING", description: "Date (2021-01-05 | YYYY-MM-DD)"], [name: "Time*", type: "STRING", description: "Time (18:10 | HH:MM)"]]
        command "createReminder", [[name: "Reminder Label*", type: "STRING", description: "This is the title of the reminder"], [name: "Date*", type: "STRING", description: "Date (2021-01-05 | YYYY-MM-DD)"], [name: "Time*", type: "STRING", description: "Time (18:10 | HH:MM)"]]
        // command "createReminderNew", ["string", "string", "string", "string", "string"]
        command "removeNotification", [[name: "Notification ID to Remove*", type: "STRING", description: ""]]
        // command "removeAllNotificationsByType", ["string"]
        command "setWakeWord", [[name: "New Wake Word*", type: "STRING", description: ""]]
        command "renameDevice", [[name: "New Device Name*", type: "STRING", description: ""]]
        command "storeCurrentVolume"
        command "restoreLastVolume"
        command "togglePlayback"
        command "setVolumeAndSpeak", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""]]
        command "setVolumeSpeakAndRestore", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""],[name: "Restore Volume*", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "volumeUp"
        command "volumeDown"
        command "speechTest"
        command "speak", [[name: "Message to Speak*", type: "STRING", description: ""]]
        command "sendTestAnnouncement"
        command "sendTestAnnouncementAll"
        command "getDeviceActivity"
        command "getBluetoothDevices"
        command "connectBluetooth", [[name: "Bluetooth Device Label*", type: "STRING", description: ""]]
        command "disconnectBluetooth"
        command "removeBluetooth", [[name: "Bluetooth Device Label*", type: "STRING", description: ""]]
        command "sendAnnouncementToDevices", ["string", "string", "string", "number", "number"]
        command "voiceCmdAsText", [[name: "Voice Command as Text*", type: "STRING", description: ""]]
    }

    preferences {
        section("Preferences") {
            input "logInfo", "bool", title: "Show Info Logs?",  required: false, defaultValue: true
            input "logWarn", "bool", title: "Show Warning Logs?", required: false, defaultValue: true
            input "logError", "bool", title: "Show Error Logs?",  required: false, defaultValue: true
            input "logDebug", "bool", title: "Show Debug Logs?", description: "Only leave on when required", required: false, defaultValue: false
            input "logTrace", "bool", title: "Show Detailed Logs?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false
            input "ignoreTimeoutErrors", "bool", required: false, title: "Don't show errors in the logs for request timeouts?", description: sBLANK, defaultValue: true

            input "disableQueue", "bool", required: false, title: "Don't Allow Queuing?", defaultValue: false
            input "disableTextTransform", "bool", required: false, title: "Disable Text Transform?", description: "This will attempt to convert items in text like temp units and directions like `WSW` to west southwest", defaultValue: false
            input "sendDevNotifAsAnnouncement", "bool", required: false, title: "Send Device Notifications as Announcements?", description: sBLANK, defaultValue: false
// maxVolume not used
//            input "maxVolume", "number", required: false, title: "Set Max Volume for this device", description: "There will be a delay of 30-60 seconds in getting the current volume level"
            input "ttsWordDelay", "number", required: true, title: "Speech queue delay (per 14 characters)", description: "Default - 2 second delay per every 14 characters.", defaultValue: 2
            input "autoResetQueue", "number", required: false, title: "Auto reset queue (xx seconds) after last speak command", description: "This will reset the queue 180 seconds after last message sent.", defaultValue: 180
        }
    }
}

def installed() {
    logInfo("${device?.displayName} Executing Installed...")
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "deviceStatus", value: "stopped_echo_gen1")
    sendEvent(name: "trackDescription", value: sBLANK)
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    sendEvent(name: "wasLastSpokenToDevice", value: sFALSE)
    sendEvent(name: "doNotDisturb", value: sFALSE)
    sendEvent(name: "onlineStatus", value: "online")
    sendEvent(name: "followUpMode", value: sFALSE)
    sendEvent(name: "alarmVolume", value: 0)
    sendEvent(name: "alexaWakeWord", value: "ALEXA")
    sendEvent(name: "mediaSource", value: sBLANK)
    state.doNotDisturb = false
    initialize()
    runIn(20, "postInstall")
}

def updated() {
    logInfo("${device?.displayName} Executing Updated()")
    initialize()
}

def initialize() {
    logInfo("${device?.displayName} Executing initialize()")
//    sendEvent(name: "DeviceWatch-DeviceStatus", value: "online", display: false, displayed: false)
//    sendEvent(name: "DeviceWatch-Enroll", value: new groovy.json.JsonOutput().toJson([protocol: "cloud", scheme:"untracked"]), display: false, displayed: false)
    unschedule()
    state.refreshScheduled = false
    resetQueue()
    stateCleanup()
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return }
    schedDataRefresh(true)
    parent?.updChildSocketStatus()
    if(advLogsActive()) { runIn(1800, "logsOff") }
    refreshData(true)
    //TODO: Have the queue validated based on the last time it was processed and have it cleanup if it's been too long
}

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace) }
public void logsOff() {
    device.updateSetting("logDebug",[value:sFALSE,type:"bool"])
    device.updateSetting("logTrace",[value:sFALSE,type:"bool"])
    log.debug "Disabling debug logs"
}

def postInstall() {
    if(device?.currentState('level') == 0) { setLevel(30) }
    if(device?.currentState('alarmVolume') == 0) { setAlarmVolume(30) }
}

public triggerInitialize() { runIn(3, "initialize") }
String getEchoDeviceType() { return (String)state.deviceType ?: sNULL }
String getEchoSerial() { return (String)state.serialNumber ?: sNULL }

String getHealthStatus(Boolean lower=false) {
    String res = device?.getStatus()
    if(lower) { return res?.toLowerCase() }
    return res
}

String getShortDevName(){
    return device?.displayName?.replace("Echo - ", sBLANK)
}

public void setAuthState(Boolean authenticated) {
    state.authValid = authenticated
    if(!authenticated && (Boolean)state.refreshScheduled) {
        removeCookies()
    }
}

public void updateCookies(Map cookies) {
    logWarn("Cookies Update by Parent.  Re-Initializing Device in 5 Seconds...")
    state.cookie = cookies
    cookieDataFLD = [:]
    setAuthState(true)
    runIn(5, "initialize")
}

public void removeCookies(Boolean isParent=false) {
    if(state.cookie != null || (Boolean)state.authValid != false || (Boolean)state.refreshScheduled) {
        logWarn("Cookie Authentication Cleared by ${isParent ? "Parent" : "Device"} | Scheduled Refreshes also cancelled!")
        if((Boolean)state.refreshScheduled) { unschedule("refreshData"); state.refreshScheduled = false }
        state.cookie = null
        cookieDataFLD = [:]
        state.authValid = false
    }
}

Boolean isAuthOk(Boolean noLogs=false) {
    if(!(Boolean)state.authValid) {
        if((Boolean)state.refreshScheduled) { unschedule("refreshData"); state.refreshScheduled = false }
        if(state.cookie != null) {
            if(!noLogs) { logWarn("Echo Speaks Authentication is no longer valid... Please login again and commands will be allowed again!!!", true) }
            state.remove("cookie")
            cookieDataFLD = [:]
        }
        return false
    } else { return true }
}

Boolean isCommandTypeAllowed(String type, Boolean noLogs=false) {
    Boolean isOnline = (device?.currentValue("onlineStatus") == "online")
    if(!isOnline) { if(!noLogs) { logWarn("Commands NOT Allowed! Device is currently (OFFLINE) | Type: (${type})", true) }; return false }
    if(!isAuthOk(noLogs)) { return false }
    if(!getAmazonDomain()) { if(!noLogs) { logWarn("amazonDomain State Value Missing: ${getAmazonDomain()}", true) }; return false }
    if(!state.cookie || !state.cookie.cookie || !state.cookie.csrf) { if(!noLogs) { logWarn("Amazon Cookie State Values Missing: ${state.cookie}", true) }; setAuthState(false); return false }
    if(!(String)state.serialNumber) { if(!noLogs) { logWarn("SerialNumber State Value Missing: ${(String)state.serialNumber}", true) }; return false }
    if(!(String)state.deviceType) { if(!noLogs) { logWarn("DeviceType State Value Missing: ${(String)state.deviceType}", true) }; return false }
    if(!(String)state.deviceOwnerCustomerId) { if(!noLogs) { logWarn("OwnerCustomerId State Value Missing: ${(String)state.deviceOwnerCustomerId}", true) }; return false }
    if(state.isSupportedDevice == false) { logWarn("You are using an Unsupported/Unknown Device all restrictions have been removed for testing! If commands function please report device info to developer", true); return true }
    if(!type) { if(!noLogs) { logWarn("Invalid Permissions Type Received: ${type}", true) }; return false }
    if(state.permissions == null) { if(!noLogs) { logWarn("Permissions State Object Missing: ${state.permissions}", true) }; return false }
    if(device?.currentValue("doNotDisturb") == sTRUE && (!(type in ["volumeControl", "alarms", "reminders", "doNotDisturb", "wakeWord", "bluetoothControl", "mediaPlayer"]))) { if(!noLogs) { logWarn("All Voice Output Blocked... Do Not Disturb is ON", true) }; return false }
    if(state.permissions.containsKey(type) && state.permissions[type] == true) { return true }
    else {
        String warnMsg = sNULL
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
                warnMsg = "OOPS... Flash Briefs and Good News are NOT Supported by this Device!!!"
                break
        }
        if(warnMsg && !noLogs) { logWarn(warnMsg, true) }
        return false
    }
}

Boolean permissionOk(String type) {
    if(type && state?.permissions?.containsKey(type) && state?.permissions[type] == true) { return true }
    return false
}

void updateDeviceStatus(Map devData) {
    Boolean isOnline = false
    if(devData.size()) {
        isOnline = (devData.online != false)
        // log.debug "isOnline: ${isOnline}"
        // log.debug "deviceFamily: ${devData?.deviceFamily} | deviceType: ${devData?.deviceType}"  // UNCOMMENT to identify unidentified devices

        // NOTE: These allow you to log all device data items
        // devData?.each { k,v ->
        //     if(!(k in ["playerState", "capabilities", "deviceAccountId"])) {
        //         log.debug("$k: $v")
        //     }
        // }
        state.isSupportedDevice = (devData.unsupported != true)
        state.remove('isEchoDevice') //        state.isEchoDevice = (devData?.permissionMap?.isEchoDevice == true)
        state.serialNumber = (String)devData.serialNumber
        state.deviceType = (String)devData.deviceType
        state.deviceOwnerCustomerId = (String)devData.deviceOwnerCustomerId
        state.deviceAccountId = (String)devData?.deviceAccountId
        state.softwareVersion = devData?.softwareVersion
        // state?.mainAccountCommsId = devData?.mainAccountCommsId ?: null
        // log.debug "mainAccountCommsId: ${state?.mainAccountCommsId}"
        if(!state.cookie) {
            state.cookie = devData?.cookie
            cookieDataFLD = [:]
        }
        state.authValid = (devData?.authValid == true)
        state.amazonDomain = (String)devData?.amazonDomain
        state.regionLocale = (String)devData?.regionLocale
        Map permissions = state.permissions ?: [:]
        devData.permissionMap?.each {String k,v -> permissions[k] = v }
        state.permissions = permissions
        state.hasClusterMembers = devData?.hasClusterMembers
        state.isWhaDevice = (devData?.permissionMap?.isMultiroomDevice == true)
        // log.trace "hasClusterMembers: ${ state?.hasClusterMembers}"
        // log.trace "permissions: ${state?.permissions}"

        Boolean chg=false

        List permissionList = permissions?.findAll { it?.value == true }?.collect { it?.key }
        if(isStateChange(device, "permissions", permissionList?.toString())) {
            sendEvent(name: "permissions", value: permissionList, display: false, displayed: false)
            chg=true
        }

        Map deviceStyle = devData?.deviceStyle
        state.deviceStyle = devData?.deviceStyle
        // logInfo("deviceStyle (${devData?.deviceFamily}): ${devData?.deviceType} | Desc: ${deviceStyle?.name}")
        state.deviceImage = deviceStyle?.image as String
        if(isStateChange(device, "deviceStyle", deviceStyle?.name?.toString())) {
            sendEvent(name: "deviceStyle", value: deviceStyle?.name?.toString(), descriptionText: "Device Style is ${deviceStyle?.name}", display: true, displayed: true)
            chg=true
        }

        String firmwareVer = devData?.softwareVersion ?: "Not Set"
        if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
            sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
            chg=true
        }

        String devFamily = devData?.deviceFamily ?: sBLANK
        if(isStateChange(device, "deviceFamily", devFamily)) {
            sendEvent(name: "deviceFamily", value: devFamily, descriptionText: "Echo Device Family is ${devFamily}", display: true, displayed: true)
            chg=true
        }

        if(isStateChange(device, "deviceSerial", devData?.serialNumber?.toString())) {
            sendEvent(name: "deviceSerial", value: devData?.serialNumber?.toString(), descriptionText: "Echo Device SerialNumber is ${devData?.serialNumber}", display: true, displayed: true)
            chg=true
        }

        String devType = devData?.deviceType ?: sBLANK
        if(isStateChange(device, "deviceType", devType?.toString())) {
            sendEvent(name: "deviceType", value: devType?.toString(), display: false, displayed: false)
            chg=true
        }

        Map musicProviders = devData?.musicProviders ?: [:]
        String lItems = musicProviders?.collect{ it?.value }?.sort()?.join(", ")
        if(isStateChange(device, "supportedMusic", lItems?.toString())) {
            sendEvent(name: "supportedMusic", value: lItems?.toString(), display: false, displayed: false)
            chg=true
        }
        // if(devData?.guardStatus) { updGuardStatus(devData?.guardStatus) }
        if(!isOnline) {
            sendEvent(name: "mute", value: "unmuted")
            sendEvent(name: "status", value: "stopped")
            sendEvent(name: "deviceStatus", value: "stopped_${state.deviceStyle?.image}")
            sendEvent(name: "trackDescription", value: sBLANK)
        } else { if(chg) { state.fullRefreshOk = true; triggerDataRrsh('updateDeviceStatus') }}
    }
    setOnlineStatus(isOnline)
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false, displayed: false)
    schedDataRefresh()
}

public void updSocketStatus(Boolean active) {
    if(!active) { schedDataRefresh(true) }
    state.websocketActive = active
}

void websocketUpdEvt(List triggers) {
    logTrace("websocketEvt: $triggers")
    if((Boolean) state.isWhaDevice) { return }
    if(triggers?.size()) {
        triggers?.each { k->
            switch(k) {
                case "all":
                    state.fullRefreshOk = true
                    runIn(2, "refreshData1")
                    break
                case "media":
                    runIn(2, "getPlaybackState")
                    break
                case "queue":
                    runIn(4, "getPlaylists")
                case "notif":
                    // runIn(2, "getNotifications")
                    break
                case "bluetooth":
                    runIn(20, "getBluetoothDevices")
                    break
                case "notification":
                    // runIn(2, "getNotifications")
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
        }
    }
}

void refresh() {
    logTrace("refresh()")
    parent?.childInitiatedRefresh()
    triggerDataRrsh('refresh')
//    refreshData(true)
}

private void triggerDataRrsh(String src, Boolean parentRefresh=false) {
    logTrace("triggerDataRrsh $src $parentRefresh")
    runIn(6, parentRefresh ? "refresh" : "refreshData1")
}

void refreshData1() {
    refreshData()
}

public schedDataRefresh(Boolean frc=false) {
    if(frc || !(Boolean)state.refreshScheduled) {
        runEvery30Minutes("refreshData")
        state.refreshScheduled = true
    }
}

void refreshData(Boolean full=false) {
    logTrace("refreshData($full)...")
    Boolean wsActive = (Boolean)state.websocketActive
    Boolean isWHA = (Boolean)state.isWhaDevice
    Boolean mfull = (Boolean)state.fullRefreshOk

//    Boolean isEchoDev = (state.isEchoDevice == true)
    if(device?.currentValue("onlineStatus") != "online") {
        logTrace("Skipping Device Data Refresh... Device is OFFLINE... (Offline Status Updated Every 10 Minutes)")
        return
    }
    if(!isAuthOk()) {return}
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return }
    // logTrace("permissions: ${state?.permissions}")
    if(state.permissions?.mediaPlayer == true && (full || mfull || !wsActive)) {
        getPlaybackState()
        if(!isWHA) { getPlaylists() }
    }
    if(!isWHA) {
        if (full || mfull) {
            // if(isEchoDev) { getWifiDetails() }
            getDeviceSettings()
        }
        if(state.permissions?.doNotDisturb == true) { getDoNotDisturb() }
        if(!wsActive || full || mfull) {
            getDeviceActivity()
        }
        if(!mfull && full) state.fullRefreshOk = true
        if((Boolean)state.fullRefreshOk || full) runIn(3, "refreshStage2")
    } else { state.fullRefreshOk = false }
}

private refreshStage2() {
    // log.trace("refreshStage2()...")
    Boolean wsActive = (Boolean)state.websocketActive
    Boolean full = (Boolean)state.fullRefreshOk
    state.fullRefreshOk = false

    if(state.permissions?.wakeWord && full) {
        getWakeWord()
        getAvailableWakeWords()
    }
    if((state.permissions?.alarms == true) || (state.permissions?.reminders == true)) {
        if(state?.permissions?.alarms == true) { getAlarmVolume() }
        // getNotifications()
    }

    if(state.permissions?.bluetoothControl && (!wsActive || full)) {
        getBluetoothDevices()
    }
    // updGuardStatus()
}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
//    if(isStateChange(device, "DeviceWatch-DeviceStatus", onlStatus?.toString())) {
//        sendEvent(name: "DeviceWatch-DeviceStatus", value: onlStatus?.toString(), display: false, displayed: false)
//    }
    if(isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        logDebug("OnlineStatus has changed to (${onlStatus})")
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), display: true, displayed: true)
    }
}

private getPlaybackState(Boolean isGroupResponse=false) {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/np/player",
        query: [ deviceSerialNumber: (String)state.serialNumber, deviceType: (String)state.deviceType, screenWidth: 2560, _: now() ],
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map playerInfo = [:]
    try {
        logTrace('getPlaybackState')
//private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
        httpGet(params) { response->
            Map sData = response?.data ?: [:]
            playerInfo = sData?.playerInfo ?: [:]
        }
    } catch (ex) {
        respExceptionHandler(ex, "getPlaybackState", false, true)
        return
    }
    playbackStateHandler(playerInfo, isGroupResponse)
}

void playbackStateHandler(Map playerInfo, Boolean isGroupResponse=false) {
    // log.debug "playerInfo: ${playerInfo}"
    Boolean isPlayStateChange = false
    Boolean isMediaInfoChange = false
    if (state.isGroupPlaying && !isGroupResponse) {
        logDebug("ignoring getPlaybackState because group is playing here")
        return
    }
    // logTrace("getPlaybackState: ${playerInfo}")
    String playState = playerInfo.state == 'PLAYING' ? "playing" : "stopped"
    String deviceStatus = "${playState}_${state.deviceStyle?.image}".toString()
    // log.debug "deviceStatus: ${deviceStatus}"
    if(isStateChange(device, "status", playState) || isStateChange(device, "deviceStatus", deviceStatus)) {
        logTrace("Status Changed to ${playState}")
        isPlayStateChange = true
        if (isGroupResponse) {
            state.isGroupPlaying = (playerInfo.state == 'PLAYING')
        }
        sendEvent(name: "status", value: playState, descriptionText: "Player Status is ${playState}", display: true, displayed: true)
        sendEvent(name: "deviceStatus", value: deviceStatus, display: false, displayed: false)
    }
    //Track Title
    String title = playerInfo.infoText?.title ?: "Not Set"
    if(isStateChange(device, "trackDescription", title)) {
        isMediaInfoChange = true
        sendEvent(name: "trackDescription", value: title, descriptionText: "Track Description is ${title}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText1 = playerInfo.infoText?.subText1 ?: "Idle"
    if(isStateChange(device, "currentAlbum", subText1)) {
        isMediaInfoChange = true
        sendEvent(name: "currentAlbum", value: subText1, descriptionText: "Album is ${subText1}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText2 = playerInfo.infoText?.subText2 ?: "Idle"
    if(isStateChange(device, "currentStation", subText2)) {
        isMediaInfoChange = true
        sendEvent(name: "currentStation", value: subText2, descriptionText: "Station is ${subText2}", display: true, displayed: true)
    }

    //Track Art Image
    String trackImg = playerInfo.mainArt?.url ?: "Not Set"
    if(isStateChange(device, "trackImage", trackImg)) {
        isMediaInfoChange = true
        sendEvent(name: "trackImage", value: trackImg, descriptionText: "Track Image is ${trackImg}", display: false, displayed: false)
        sendEvent(name: "trackImageHtml", value: """<img src="${trackImg?.toString()}"/>""", display: false, displayed: false)
    }

    //Media Source Provider
    String mediaSource = playerInfo.provider?.providerName ?: "Not Set"
    if(isStateChange(device, "mediaSource", mediaSource)) {
        isMediaInfoChange = true
        sendEvent(name: "mediaSource", value: mediaSource, descriptionText: "Media Source is ${mediaSource}", display: true, displayed: true)
    }

    //Update Audio Track Data
    if (isMediaInfoChange){
        Map trackData = [:]
        if(playerInfo.infoText?.title) { trackData.title = playerInfo.infoText?.title }
        if(playerInfo.infoText?.subText1) { trackData.artist = playerInfo.infoText?.subText1 }
        //To avoid media source provider being used as album (ex: Apple Music), only inject `album` if subText2 and providerName are different
        if(playerInfo.infoText?.subText2 && playerInfo?.provider?.providerName!=playerInfo?.infoText?.subText2) { trackData.album = playerInfo.infoText?.subText2 }
        if(playerInfo.mainArt?.url) { trackData.albumArtUrl = playerInfo.mainArt?.url }
        if(playerInfo.provider?.providerName) { trackData.mediaSource = playerInfo.provider?.providerName }
        //log.debug(trackData)
        sendEvent(name: "trackData", value: new groovy.json.JsonOutput().toJson(trackData), display: false, displayed: false)
// non-standard:
        sendEvent(name: "audioTrackData", value: new groovy.json.JsonOutput().toJson(trackData), display: false, displayed: false)
    }

    //NOTE: Group response data never has valid data for volume
    if(!isGroupResponse && playerInfo.volume) {
        if(playerInfo?.volume?.volume != null) {
            Integer level = playerInfo.volume?.volume
            if(level < 0) { level = 0 }
            if(level > 100) { level = 100 }
            if(isStateChange(device, "level", level.toString()) || isStateChange(device, "volume", level.toString())) {
                logDebug("Volume Level Set to ${level}%")
                sendEvent(name: "level", value: level, display: false, displayed: false)
                sendEvent(name: "volume", value: level, display: false, displayed: false)
            }
        }
        if(playerInfo.volume?.muted != null) {
            String muteState = (playerInfo.volume?.muted == true) ? "muted" : "unmuted"
            if(isStateChange(device, "mute", muteState)) {
                logDebug("Mute Changed to ${muteState}")
                sendEvent(name: "mute", value: muteState, descriptionText: "Volume has been ${muteState}", display: true, displayed: true)
            }
        }
    }
    // Update cluster (unless we remain paused)
    if ((Boolean)state.hasClusterMembers && (playerInfo.state == 'PLAYING' || isPlayStateChange)) {
        parent?.sendPlaybackStateToClusterMembers((String)state.serialNumber, playerInfo)
    }
}

private getAlarmVolume() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/device-notification-state/${(String)state.deviceType}/${device.currentValue("firmwareVer") as String}/${(String)state.serialNumber}",
        headers: getCookieMap(true),
        query: [_: new Date().getTime()],
        contentType: sAPPJSON,
        timeout: 20
    ]
    try {
        logTrace('getAlarmVolume')
//private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // logTrace("getAlarmVolume: $sData")
            if(sData && isStateChange(device, "alarmVolume", (sData?.volumeLevel ?: 0)?.toString())) {
                logDebug("Alarm Volume Changed to ${(sData?.volumeLevel ?: 0)}")
                sendEvent(name: "alarmVolume", value: (sData?.volumeLevel ?: 0), display: true, displayed: true)
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
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    try {
        logTrace('getWakeWord')
//private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
        httpGet(params) { response->
            def sData = response?.data ?: null
            // log.debug "sData: $sData"
            if(sData && sData?.wakeWords) {
                def t0 = sData?.wakeWords?.find { it?.deviceSerialNumber == (String)state.serialNumber }
                def wakeWord = t0 ?: null
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

private getDeviceSettings() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/device-preferences",
        query: [cached: true, _: new Date().getTime()],
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    try {
        logTrace('getDeviceSettings')
//private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
        httpGet(params) { response->
            Map sData = response?.data ?: null
            // log.debug "sData: $sData"
            def t0 = sData?.devicePreferences?.find { it?.deviceSerialNumber == (String)state.serialNumber }
            def devData = t0 ?: null
            state.devicePreferences = devData ?: [:]
            // log.debug "devData: $devData"
            Boolean fupMode = (devData?.goldfishEnabled == true)
            if(isStateChange(device, "followUpMode", fupMode.toString())) {
                logDebug("FollowUp Mode Changed to ${(fupMode)}")
                sendEvent(name: "followUpMode", value: fupMode.toString(), display: false, displayed: false)
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
        query: [ cached: true, _: new Date().getTime(), deviceSerialNumber: (String)state.serialNumber, deviceType: (String)state.deviceType, softwareVersion: device.currentValue('firmwareVer') ],
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    try {
        logTrace('getAvailableWakeWords')
        httpGet(params) { response->
            Map sData = response?.data ?: null
            // log.debug "sData: $sData"
            String wakeWords = (sData && sData?.wakeWords) ? sData?.wakeWords?.join(",") : "None Found" //(String)null
            if(isStateChange(device, "wakeWords", wakeWords)) {
                logDebug("getAvailableWakeWords: ${wakeWords}")
                sendEvent(name: "wakeWords", value: wakeWords, display: false, displayed: false)
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getAvailableWakeWords")
    }
}

void getBluetoothDevices() {
    Map btData = parent?.getBluetoothData((String)state.serialNumber)
    btData = btData ?: [:]
    String curConnName = btData.curConnName ?: 'None Connected' //(String)null
    Map btObjs = (Map)btData?.btObjs ?: [:]
    // logDebug("Current Bluetooth Device: ${curConnName} | Bluetooth Objects: ${btObjs}")
    state.bluetoothObjs = btObjs
//    String pairedNames = (btData.pairedNames) ? btData.pairedNames.join(",") : (String)null
    Map btMap = [:]
    btMap.names = (btData.pairedNames && btData.pairedNames.size()) ? btData.pairedNames.collect { it as String } : []
    String btPairedJson = new groovy.json.JsonOutput().toJson(btMap)
    if(isStateChange(device, "btDevicesPaired", btPairedJson)) {
        logDebug("Paired Bluetooth Devices: ${btPairedJson}")
        sendEvent(name: "btDevicesPaired", value: btPairedJson, descriptionText: "Paired Bluetooth Devices: ${btPairedJson}", display: true, displayed: true)
    }

    if(isStateChange(device, "btDeviceConnected", curConnName)) {
        // log.info "Bluetooth Device Connected: (${curConnName})"
        sendEvent(name: "btDeviceConnected", value: curConnName, descriptionText: "Bluetooth Device Connected (${curConnName})", display: true, displayed: true)
    }
}

void updGuardStatus(String val=sNULL) {
    //TODO: Update this because it's not working
    String t0 = val ?: (state?.permissions?.guardSupported ? parent?.getAlexaGuardStatus() : sNULL)
    String gState = val ?: (state?.permissions?.guardSupported ? (t0 ?: "Unknown") : "Not Supported")
    if(isStateChange(device, "alexaGuardStatus", gState)) {
        sendEvent(name: "alexaGuardStatus", value: gState, display: false, displayed: false)
        logDebug("Alexa Guard Status: (${gState})")
    }
}

private String getBtAddrByAddrOrName(String btNameOrAddr) {
    Map btObj = state?.bluetoothObjs
    String curBtAddr = btObj?.find { it?.value?.friendlyName == btNameOrAddr || it?.value?.address == btNameOrAddr }?.key ?: sNULL
    // logDebug("curBtAddr: ${curBtAddr}")
    return curBtAddr
}

private getDoNotDisturb() {
    Boolean dndEnabled = (Boolean)parent?.getDndEnabled((String)state.serialNumber)
//    logTrace("getDoNotDisturb: $dndEnabled")
    state.doNotDisturb = dndEnabled
    if(isStateChange(device, "doNotDisturb", dndEnabled?.toString())) {
        logDebug("Do Not Disturb: (${(dndEnabled)})")
        sendEvent(name: "doNotDisturb", value: dndEnabled?.toString(), descriptionText: "Do Not Disturb Enabled ${dndEnabled}", display: true, displayed: true)
    }
}

private getPlaylists() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/cloudplayer/playlists",
        query: [ deviceSerialNumber: (String)state.serialNumber, deviceType: (String)state.deviceType, mediaOwnerCustomerId: (String)state.deviceOwnerCustomerId, screenWidth: 2560 ],
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    try {
        logTrace('getPlaylists')
        httpGet(params) { response->
            def sData = response?.data ?: null
            // logTrace("getPlaylistsHandler: ${sData}")
            def playlist = sData ? sData?.playlists : [:]
            String playlistJson = new groovy.json.JsonOutput().toJson(playlist)
            if(isStateChange(device, "alexaPlaylists", playlistJson) && playlistJson.length() < 1024) {
                logDebug("Alexa Playlists Changed to ${playlistJson}")
                sendEvent(name: "alexaPlaylists", value: playlistJson, display: false, displayed: false)
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
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    try {
        logTrace('getNotifications')
        httpGet(params) { response->
            List newList = []
            def sData = response?.data ?: null
            if(sData?.size()) {
                List s = ["ON"]
                if(all) s?.push("OFF")
                List items = sData?.notifications ? sData?.notifications?.findAll { (it?.status in s) && (it?.type == type) && it?.deviceSerialNumber == (String)state.serialNumber } : []
                items?.each { item->
                    Map li = [:]
                    item?.keySet()?.each { String key-> if(key in ['id', 'reminderLabel', 'originalDate', 'originalTime', 'deviceSerialNumber', 'type', 'remainingDuration', 'status']) { li[key] = item[key] } }
                    newList?.push(li)
                }
            }
            // if(isStateChange(device, "alexaNotifications", newList?.toString())) {
            //     sendEvent(name: "alexaNotifications", value: newList, display: false, displayed: false)
            // }
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
        Map actData = parent?.getDeviceActivity((String) state.serialNumber)
        actData = actData ?: null
        Boolean wasLastDevice = (actData != null && (String) actData?.serialNumber == (String) state.serialNumber)
        if(actData != null && (Boolean) wasLastDevice) {
            if(isStateChange(device, "lastVoiceActivity", (String) actData?.spokenText)) {
                logDebug("lastVoiceActivity: ${actData?.spokenText}")
                sendEvent(name: "lastVoiceActivity", value: (String) actData?.spokenText, display: false, displayed: false)
            }
            if(isStateChange(device, "lastSpokenToTime", (String) actData?.lastSpokenDt)) {
                sendEvent(name: "lastSpokenToTime", value: (String) actData?.lastSpokenDt, display: false, displayed: false)
            }
        }
        if(isStateChange(device, "wasLastSpokenToDevice", wasLastDevice.toString())) {
            logDebug("wasLastSpokenToDevice: ${wasLastDevice}")
            sendEvent(name: "wasLastSpokenToDevice", value: wasLastDevice.toString(), display: false, displayed: false)
        }
    } catch (ex) {
        logError("updDeviceActivity Error: ${ex.message}")
    }
}

Map getCookieMap(Boolean extra=false) {
    Map cook = [cookie: getCookieVal(), csrf: getCsrfVal()]
    if(extra) return cook + [Connection: "keep-alive", DNT: "1"]
    return cook
}

String getCookieVal() {
    String myId=parent.getId() //device.getId()
    Map cookieData = cookieDataFLD[myId]
    if(cookieData && cookieData.cookie) { return (String)cookieData.cookie.cookie }
    else { 
        try {
            if (cookieDataFLD[myId] == null) { cookieDataFLD[myId] = [:];  cookieDataFLD = cookieDataFLD }
            cookieData = state.cookie
            if (cookieData && cookieData.cookie) { cookieDataFLD[myId].cookie = cookieData;  cookieDataFLD = cookieDataFLD }
            else return sNULL
        } catch (ex) {
            cookieData = state.cookie
        }
        return (String)cookieData.cookie
    }
}

String getCsrfVal() {
    String myId=parent.getId() //device.getId()
    Map cookieData = cookieDataFLD[myId]
    if(cookieData && cookieData.cookie) { return (String)cookieData.cookie.csrf }
    else {
        try {
            if (cookieDataFLD[myId] == null) { cookieDataFLD[myId] = [:];  cookieDataFLD = cookieDataFLD }
            cookieData = state.cookie
            if (cookieData && cookieData.cookie) { cookieDataFLD[myId].cookie = cookieData;  cookieDataFLD = cookieDataFLD }
            else return sNULL
        } catch (ex) {
            cookieData = state.cookie
        }
        return (String)cookieData.csrf
    }
}

/*******************************************************************
            Amazon Command Logic
*******************************************************************/

private void sendAmazonBasicCommand(String cmdType) {
    String t0 = sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/np/command",
        headers: getCookieMap(),
        query: [ deviceSerialNumber: (String)state.serialNumber, deviceType: (String)state.deviceType ],
        contentType: sAPPJSON,
        body: [type: cmdType],
        timeout: 20
    ], [cmdDesc: cmdType])
    triggerDataRrsh(cmdType)
}

private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
    if(method && callbackHandler && params) {
        String m = method?.toString()?.toLowerCase()
        "asynchttp${m?.capitalize()}"("${callbackHandler}", params, otherData)
    } else { logError("execAsyncCmd Error | Missing a required parameter") }
}

private String sendAmazonCommand(String method, Map params, Map otherData=null) {
    try {
        def rData = null
        def rStatus = null
        logTrace("sendAmazonCommand($method, $params")
        switch(method) {
            case "POST":
//private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
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
        String cmdD = (String)otherData?.cmdDesc
        if (cmdD) {
            if(cmdD.startsWith("connectBluetooth") || cmdD.startsWith("disconnectBluetooth") || cmdD.startsWith("removeBluetooth")) {
                triggerDataRrsh("sendAmazonCommand $method bluetooth")
            } else if(cmdD.startsWith("renameDevice")) { triggerDataRrsh("sendAmazonCommand $method rename", true) }
        }
        logDebug("sendAmazonCommand | Status: (${rStatus})${rData != null ? " | Response: ${rData}" : sBLANK} | ${cmdD} was Successfully Sent!!!")
        return rData?.id ?: sNULL
    } catch (ex) {
        respExceptionHandler(ex, "${otherData?.cmdDesc}", true)
    }
    return sNULL
}

private void sendSequenceCommand(type, command, value) {
    // logTrace("sendSequenceCommand($type) | command: $command | value: $value")
    Map seqObj = sequenceBuilder(command, value)
    String t0 = sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/behaviors/preview",
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        body: new groovy.json.JsonOutput().toJson(seqObj),
        timeout: 20
    ], [cmdDesc: "SequenceCommand (${type})"])
}

private void sendMultiSequenceCommand(commands, String srcDesc, Boolean parallel=false) {
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

void respExceptionHandler(ex, String mName, Boolean clearOn401=false, Boolean ignNullMsg=false) {
    if(ex instanceof groovyx.net.http.HttpResponseException ) {
        Integer sCode = ex?.getResponse()?.getStatus()
        def respData = ex?.getResponse()?.getData()
        String errMsg = ex?.getMessage()
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
        if(settings?.ignoreTimeoutErrors == false) logError("${mName} | Response Socket Timeout (Possibly an Amazon Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.UnknownHostException) {
        logError("${mName} | HostName Not Found | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof org.apache.http.conn.ConnectTimeoutException) {
        if(settings?.ignoreTimeoutErrors == false) logError("${mName} | Request Timeout (Possibly an Amazon/Internet Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.NoRouteToHostException) {
        logError("${mName} | No Route to Connection (Possibly a Local Internet Issue) | Msg: ${ex}")
    } else if(ex instanceof javax.net.ssl.SSLHandshakeException) {
        if(settings?.ignoreTimeoutErrors == false) logError("${mName} | Remote Connection Closed (Possibly an Amazon/Internet Issue) | Msg: ${ex}")
    } else { logError("${mName} Exception: ${ex}") }
}

def searchTest() {
    searchMusic("thriller", "AMAZON_MUSIC")
}
/*******************************************************************
            Device Command FUNCTIONS
*******************************************************************/

// capability musicPlayer
def play() {
    logTrace("play() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PlayCommand")
        if(isStateChange(device, "status", "playing")) {
            sendEvent(name: "status", value: "playing", descriptionText: "Player Status is playing", display: true, displayed: true)
            // log.debug "deviceStatus: playing_${state?.deviceStyle?.image}"
            sendEvent(name: "deviceStatus", value: "playing_${state?.deviceStyle?.image}", display: false, displayed: false)
        }
        return
    }
    logWarn("Uh-Oh... The play Command is NOT Supported by this Device!!!")
}

// capability audioNotification
def playTrack(String uri, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrack($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            if(volume) {
                setVolumeAndSpeak(volume, tts)
            } else { speak(tts) }
            return
        }
    }
    logWarn("Uh-Oh... The playTrack($uri, $volume) Command is NOT Supported by this Device!!!")
}

// capability musicPlayer
/*def playTrack(String uri) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrack($uri) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            speak(tts)
            return
        }
    }
    logWarn("Uh-Oh... The playTrack($uri) Command is NOT Supported by this Device!!!")
} */

// capability musicPlayer
def pause() {
    logTrace("pause() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PauseCommand")
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
            // log.debug "deviceStatus: stopped_${state?.deviceStyle?.image}"
            sendEvent(name: "deviceStatus", value: "stopped_${state?.deviceStyle?.image}", display: false, displayed: false)
        }
        return
    }
    logWarn("Uh-Oh... The pause Command is NOT Supported by this Device!!!")
}

// capability musicPlayer
def stop() {
    logTrace("stop() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PauseCommand")
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
        }
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
    triggerDataRrsh('stopAllDevices')
}

// capability musicPlayer
def previousTrack() {
    logTrace("previousTrack() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PreviousCommand")
    }
}

// capability musicPlayer
def nextTrack() {
    logTrace("nextTrack() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("NextCommand")
    }
}

// capability musicPlayer, audioVolume
def mute() {
    logTrace("mute() command received...")
    if(isCommandTypeAllowed("volumeControl")) {
        def t0= device?.currentValue("level")?.toInteger()
        if( (!state.muteLevel && t0) ) state.muteLevel = t0
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
    }
}

def shuffle() {
    logTrace("shuffle() command received...")
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("ShuffleCommand")
    }
}

// capability musicPlayer, audioVolume
def unmute() {
    logTrace("unmute() command received...")
    if(isCommandTypeAllowed("volumeControl")) {
        if(state.muteLevel) {
            setLevel(state.muteLevel)
            state.muteLevel = null
            if(isStateChange(device, "mute", "unmuted")) {
                sendEvent(name: "mute", value: "unmuted", descriptionText: "Mute is set to unmuted", display: true, displayed: true)
            }
        } else logTrace("no previous volume level found")
    }
}

def setMute(muteState) {
    if(muteState) { (muteState == "muted") ? mute() : unmute() }
}

// capability musicPlayer
def setLevel(level) {
    logTrace("setVolume($level) command received...")
    if(isCommandTypeAllowed("volumeControl") && level>=0 && level<=100) {
        if(level != device?.currentValue('level')) {
            sendSequenceCommand("VolumeCommand", "volume", level)
            sendEvent(name: "level", value: level?.toInteger(), display: true, displayed: true)
            sendEvent(name: "volume", value: level?.toInteger(), display: true, displayed: true)
        }
    }
}

def setAlarmVolume(vol) {
    logTrace("setAlarmVolume($vol) command received...")
    if(isCommandTypeAllowed("alarms") && vol>=0 && vol<=100) {
        String t0 = sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/device-notification-state/${(String)state.deviceType}/${state?.softwareVersion}/${(String)state.serialNumber}",
            headers: getCookieMap(true),
            contentType: sAPPJSON,
            body: [
                deviceSerialNumber: (String)state.serialNumber,
                deviceType: (String)state.deviceType,
                softwareVersion: device?.currentValue('firmwareVer'),
                volumeLevel: vol
            ]
        ], [cmdDesc: "AlarmVolume"])
        sendEvent(name: "alarmVolume", value: vol, display: true, displayed: true)
    }
}

// capability audioVolume
def setVolume(vol) {
    if(vol) { setLevel(vol.toInteger()) }
}

// capability audioVolume
def volumeUp() {
    def t0 = device?.currentValue('level')
    def curVol = t0 ?: 1
    if(curVol >= 0 && curVol <= 95) { setVolume(curVol.toInteger()+5) }
    else if(t0 > 95) setVolume(100)
}

// capability audioVolume
def volumeDown() {
    def t0 = device?.currentValue('level')
    def curVol = t0 ?: 1
    if(curVol >= 5) { setVolume(curVol.toInteger()-5) }
    else if(t0 > 0) setVolume(0)
}

// capability musicPlayer
def setTrack(String uri, String metaData=sBLANK) {
    logWarn("Uh-Oh... The setTrack(uri: $uri, meta: $metaData) Command is NOT Supported by this Device!!!", true)
}

// capability musicPlayer
def resumeTrack(uri) {
    logWarn("Uh-Oh... The resumeTrack() Command is NOT Supported by this Device!!!", true)
}

// capability musicPlayer
def restoreTrack(uri) {
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
        String t0 = sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/dnd/status",
            headers: getCookieMap(true),
            contentType: sAPPJSON,
            body: [
                deviceSerialNumber: (String)state.serialNumber,
                deviceType: (String)state.deviceType,
                enabled: (val==true)
            ]
        ], [cmdDesc: "SetDoNotDisturb${val ? "On" : "Off"}"])
        sendEvent(name: "doNotDisturb", value: (val == true)?.toString(), descriptionText: "Do Not Disturb Enabled ${(val == true)}", display: true, displayed: true)
        parent?.getDoNotDisturb()
    }
}

def setFollowUpMode(Boolean val) {
    logTrace("setFollowUpMode($val) command received...")
    if(state.devicePreferences == null || !state.devicePreferences?.size()) { return }
    if(!(String)state.deviceAccountId) { logError("setFollowUpMode Failed because deviceAccountId is not found..."); return }
    if(isCommandTypeAllowed("followUpMode")) {
        String t0 = sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/device-preferences/${(String)state.serialNumber}",
            headers: getCookieMap(),
            contentType: sAPPJSON,
            body: [
                deviceSerialNumber: (String)state.serialNumber,
                deviceType: (String)state.deviceType,
                deviceAccountId: (String)state.deviceAccountId,
                goldfishEnabled: (val==true)
            ]
        ], [cmdDesc: "setFollowUpMode${val ? "On" : "Off"}"])
    }
}

def deviceNotification(String msg) {
    logTrace("deviceNotification(msg: $msg) command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with deviceNotification($msg) command", true); return }
        // logTrace("deviceNotification(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
        if(settings?.sendDevNotifAsAnnouncement == true) { playAnnouncement(msg) } else { speak(msg) }
    }
}

def setVolumeAndSpeak(volume, String msg) {
    logTrace("setVolumeAndSpeak(volume: $volume, msg: $msg) command received...")
    if(volume != null && permissionOk("volumeControl")) {
        state.newVolume = volume
        state.oldVolume = null // does not put old value back
    }
    speak(msg)
}

def setVolumeSpeakAndRestore(volume, String msg, restVolume=null) {
    logTrace("setVolumeSpeakAndRestore(volume: $volume, msg: $msg, $restVolume) command received...")
    Boolean volChg = false
    Boolean stored = false
    if(msg) {
        if(volume != null && permissionOk("volumeControl")) {
            state.newVolume = volume?.toInteger()
            if(restVolume != null) {
                state.oldVolume = restVolume as Integer
                stored = true
            } else {
                stored = mstoreCurrentVolume()
            }
            volChg = true
        }
        speak(msg)
        if(volChg && stored) state.oldVolume = null 
    }
}

def storeCurrentVolume() {
    Boolean a= mstoreCurrentVolume()
}

Boolean mstoreCurrentVolume() {
    Integer t0 = device?.currentValue("level")
    Integer curVol = t0 //  ?: 1
    if(curVol != null) {
        state.oldVolume = curVol
        logTrace("storeCurrentVolume(${curVol}) command received...")
        return true
    }
    logTrace("storeCurrentVolume(${curVol}) command failed...")
    return false
}

public restoreLastVolume() {
    Integer lastVol = state.oldVolume
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

def playGoodNews(volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("flashBriefing")) {
        if(volume != null) {
            List seqs = [[command: "volume", value: volume], [command: "goodnews"]]
            if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
            sendMultiSequenceCommand(seqs, "playGoodNews")
        } else { doSequenceCmd("playGoodNews", "goodnews") }
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
        sendEvent(name: "lastAnnouncement", value: msg, display: false, displayed: false)
    }
}

def playAnnouncement(String msg, String title, volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("announce")) {
        msg = "${title ? "${title}::" : sBLANK}${msg}"
        if(volume != null) {
            List seqs = [[command: "volume", value: volume], [command: "announcement", value: msg]]
            if(restoreVolume != null) { seqs?.push([command: "volume", value: restoreVolume]) }
            sendMultiSequenceCommand(seqs, "playAnnouncement")
        } else { doSequenceCmd("playAnnouncement", "announcement", msg) }
        sendEvent(name: "lastAnnouncement", value: msg, display: false, displayed: false)
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

def voiceCmdAsText(String cmd) {
    // log.trace "voiceCmdAsText($cmd)"
    if(cmd) {
        doSequenceCmd("voiceCmdAsText", "voicecmdtxt", cmd)
    }
}

public playAnnouncementAll(String msg, String title=sNULL) {
    // if(isCommandTypeAllowed("announce")) {bvxdsa
        doSequenceCmd("AnnouncementAll", "announcementall", msg)
    // }
}

def searchMusic(String searchPhrase, String providerId, volume=null, sleepSeconds=null) {
    logDebug("searchMusic(${searchPhrase}, ${providerId})")
    if(isCommandTypeAllowed(getCommandTypeForProvider(providerId))) {
        doSearchMusicCmd(searchPhrase, providerId, volume, sleepSeconds)
    } else { logWarn("searchMusic not supported for ${providerId}", true) }
}

static String getCommandTypeForProvider(String providerId) {
    String commandType = providerId
    // logDebug("getCommandTypeForProvider(${providerId})")
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

private void doSequenceCmd(String cmdType, String seqCmd, String seqVal=sBLANK) {
    if((String)state.serialNumber) {
        logDebug("Sending (${cmdType}) | Command: ${seqCmd} | Value: ${seqVal}")
        sendSequenceCommand(cmdType, seqCmd, seqVal)
    } else { logWarn("doSequenceCmd Error | You are missing one of the following... SerialNumber: ${(String)state.serialNumber}", true) }
}

private void doSearchMusicCmd(String searchPhrase, String musicProvId, volume=null, sleepSeconds=null) {
    if((String)state.serialNumber && searchPhrase && musicProvId) {
        playMusicProvider(searchPhrase, musicProvId, volume, sleepSeconds)
    } else { logWarn("doSearchMusicCmd Error | You are missing one of the following... SerialNumber: ${(String)state.serialNumber} | searchPhrase: ${searchPhrase} | musicProvider: ${musicProvId}", true) }
}

private Map validateMusicSearch(String searchPhrase, String providerId, sleepSeconds=null) {
    Map validObj = [
        type: "Alexa.Music.PlaySearchPhrase",
        operationPayload: [
            deviceType: (String)state.deviceType,
            deviceSerialNumber: (String)state.serialNumber,
            customerId: (String)state.deviceOwnerCustomerId,
            locale: ((String)state.regionLocale ?: "en-US"),
            musicProviderId: providerId,
            searchPhrase: searchPhrase
        ]
    ]
    if(sleepSeconds) { validObj?.operationPayload?.waitTimeInSeconds = sleepSeconds }
    validObj.operationPayload = new groovy.json.JsonOutput().toJson(validObj?.operationPayload)
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/operation/validate",
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20,
        body: new groovy.json.JsonOutput().toJson(validObj)
    ]
    Map result = null
    try {
        logTrace("validateMusicSearch")
        httpPost(params) { resp->
            Map rData = resp?.data ?: null
            if(resp?.status == 200) {
                if (rData?.result != "VALID") {
                    logError("Amazon the Music Search Request as Invalid | MusicProvider: [${providerId}] | Search Phrase: (${searchPhrase})")
                } else { result = rData }
            } else { logError("validateMusicSearch Request failed with status: (${resp?.status}) | MusicProvider: [${providerId}] | Search Phrase: (${searchPhrase})") }
        }
    } catch (ex) {
        respExceptionHandler(ex, "validateMusicSearch")
    }
    return result
}

private getMusicSearchObj(String searchPhrase, String providerId, sleepSeconds=null) {
    if (searchPhrase == sBLANK) { logError("getMusicSearchObj Searchphrase empty"); return }
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

private playMusicProvider(String searchPhrase, String providerId, volume=null, sleepSeconds=null) {
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
    def t0 = device?.currentValue('wakeWords')
    def wwList = t0 ?: []
    logDebug("newWord: $newWord | oldWord: $oldWord | wwList: $wwList (${wwList?.contains(newWord.toString()?.toUpperCase())})")
    if(oldWord && newWord && wwList && wwList?.contains(newWord.toString()?.toUpperCase())) {
        String t1 = sendAmazonCommand("PUT", [
            uri: getAmazonUrl(),
            path: "/api/wake-word/${(String)state.serialNumber}",
            headers: getCookieMap(true),
            contentType: sAPPJSON,
            timeout: 20,
            body: [
                active: true,
                deviceSerialNumber: (String)state.serialNumber,
                deviceType: (String)state.deviceType,
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
            label: alarmLbl?.toString()?.replaceAll(" ", sBLANK),
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
            logDebug("Found ID translation ${id}=${translatedID}")
            if (translatedID) {
                String t0 = sendAmazonCommand("DELETE", [
                    uri: getAmazonUrl(),
                    path: "/api/notifications/${translatedID}",
                    headers: getCookieMap(true),
                    contentType: sAPPJSON,
                    timeout: 20,
                    body: []
                ], [cmdDesc: "RemoveNotification"])

                state.createdNotifications[id] = null
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
                    String t0 = sendAmazonCommand("DELETE", [
                        uri: getAmazonUrl(),
                        path: "/api/notifications/${item?.id}",
                        headers: getCookieMap(true),
                        contentType: sAPPJSON,
                        timeout: 20,
                        body: []
                    ], [cmdDesc: "RemoveNotification"])
                } else { logWarn("removeAllNotificationByType($type) Unable to Find ID for ${item?.id}", true) }
            }
        }// else { logWarn("removeAllNotificationByType($type) is Missing the Required (id) Parameter!!!", true) }
        state?.remove("createdNotifications")
    }
}

private static String generateNotificationKey(String id) {
    return id?.toString()?.replaceAll(" ", sBLANK)
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
            logWarn("createNotification found existing notification named ${notifKey}=${translatedID}, removing that first")
            removeNotification(notifKey)
        }
    }
    def now = new Date()
    def createdDate = now.getTime()

    def isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
    isoFormat.setTimeZone(location.timeZone)
    def alarmDate = isoFormat.parse("${opts.date}T${opts.time}")
    Long alarmTime = alarmDate.getTime()
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/notifications/create${type}",
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20,
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
            deviceSerialNumber: (String)state.serialNumber,
            deviceType: (String)state.deviceType,
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
    logDebug("rule: $rule")
    params?.body?.rRuleData = rule?.data ?: null
    params?.body?.recurringPattern = rule?.pattern ?: null
    logDebug("params: ${params?.body}")
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
    logTrace("transormRecurString(type: ${type}, opt: ${opt}, time: ${tm},date: ${dt})")
    Map rd = null
    String rp = null
    if(!type) return [data: rd, pattern: rp]
    List time = tm?.tokenize(':')
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
    if(!(String)state.deviceAccountId) { logError("renameDevice Failed because deviceAccountId is not found..."); return }
    String t0 = sendAmazonCommand("PUT", [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device/${(String)state.serialNumber}",
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20,
        body: [
            serialNumber: (String)state.serialNumber,
            deviceType: (String)state.deviceType,
            deviceAccountId: (String)state.deviceAccountId,
            accountName: newName
        ]
    ], [cmdDesc: "renameDevice(${newName})"])
}

def connectBluetooth(String btNameOrAddr) {
    logTrace("connectBluetooth(${btName}) command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String curBtAddr = getBtAddrByAddrOrName(btNameOrAddr as String)
        if(curBtAddr) {
            String t0 = sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/pair-sink/${(String)state.deviceType}/${(String)state.serialNumber}",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                timeout: 20,
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
            String t0 = sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/disconnect-sink/${(String)state.deviceType}/${(String)state.serialNumber}",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                timeout: 20,
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
            String t0 = sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/unpair-sink/${(String)state.deviceType}/${(String)state.serialNumber}",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                timeout: 20,
                body: [ bluetoothDeviceAddress: curBtAddr, bluetoothDeviceClass: "OTHER" ]
            ], [cmdDesc: "removeBluetooth(${btNameOrAddr})"])
        } else { logError("RemoveBluetooth Error: Unable to find the connected bluetooth device address...") }
    }
}

def sendAlexaAppNotification(String text) {
    // log.debug "sendAlexaAppNotification(${text})"
    doSequenceCmd("AlexaAppNotification", "pushnotification", text)
}

String getRandomItem(List<String>items) {
    def list = new ArrayList<String>()
    items?.each { list.add(it) }
    return list.get(new Random().nextInt(list.size()))
}

def replayText() {
    logTrace("replayText() command received...")
    String lastText = device?.currentValue("lastSpeakCmd")?.toString()
    if(lastText) { speak(lastText) } else { log.warn "Last Text was not found" }
}

// capability audioNotification
def playText(String msg, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        if (msg) {
            logDebug("playText($msg, $volume)")
            if(volume) {
                setVolumeAndSpeak(volume, msg)
            } else { speak(msg) }
            return
        }
    }
    logWarn("Uh-Oh... The playText($msg, $volume) Command is NOT Supported by this Device!!!")
}

// capability musicPlayer
/*def playText(String msg) {
    logTrace("playText(msg: $msg) command received...")
    speak(msg)
} */

// capability audioNotification
//def playTrackAndResume(String uri, duration, volume=null) {
def playTrackAndResume(String uri, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrackAndResume($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            if(volume) {
                Integer restVolume // = device?.currentValue("level")?.toInteger()
                setVolumeSpeakAndRestore(volume as Integer, tts, restVolume)
            } else { speak(tts) }
            return
        }
    }
    logWarn("Uh-Oh... The playTrackAndResume($uri, $volume) Command is NOT Supported by this Device!!!")
}

// capability audioNotification
def playTextAndResume(String text, volume=null) {
    logTrace("The playTextAndResume(text: $text, volume: $volume) command received...")
    if (volume != null) {
        def restVolume // = device?.currentValue("level")?.toInteger()
        setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
    } else { speak(text as String) }
}

//def playTrackAndRestore(String uri, duration, volume=null) {
def playTrackAndRestore(String uri, volume=null) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playTrackAndRestore($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            if(volume) {
                Integer restVolume // = device?.currentValue("level")?.toInteger()
                setVolumeSpeakAndRestore(volume as Integer, tts, restVolume)
            } else { speak(tts) }
            return
        }
    }
    logWarn("Uh-Oh... The playTrackAndRestore(uri: $uri, duration: $duration, volume: $volume) Command is NOT Supported by this Device!!!")
}

// capability audioNotification
def playTextAndRestore(String text, volume=null) {
    logTrace("The playTextAndRestore($text, $volume) command received...")
    if (volume != null) {
        def restVolume // = device?.currentValue("level")?.toInteger()
        setVolumeSpeakAndRestore(volume as Integer, text as String, restVolume as Integer)
    } else { speak(text as String) }
}
/* // this is not a command
def playURL(uri) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriSpeechParser(uri)
        if (tts) {
            logDebug("playURL($uri) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
            speak(tts as String)
            return
        }
    }
    logWarn("Uh-Oh... The playUrl($uri) Command is NOT Supported by this Device!!!")
}

def playSoundAndTrack(soundUri, duration, trackData, volume=null) {
    logWarn("Uh-Oh... The playSoundAndTrack(soundUri: $soundUri, duration: $duration, trackData: $trackData, volume: $volume) Command is NOT Supported by this Device!!!", true)
}
*/
String uriSpeechParser(String uri) {
    // Thanks @fkrlaframboise for this idea.  It never for one second occurred to me to parse out the trackUri...
    // log.debug "uri: $uri"
    if (uri?.toString()?.contains("/")) {
        Integer sInd = uri?.lastIndexOf("/") + 1
        uri = uri?.substring(sInd, uri?.size())?.toLowerCase()?.replaceAll("_", " ")?.replace(".mp3", sBLANK)
        logDebug("uriSpeechParser | tts: $uri")
        return uri
    }
    return sNULL
}

void speechTest(String ttsMsg=sNULL) {
    List<String> items = [
        """<speak><say-as interpret-as="interjection">abracadabra!</say-as>, You asked me to speak and I did!.</speak>""",
        """<speak><say-as interpret-as="interjection">pew pew</say-as>, <say-as interpret-as="interjection">guess what</say-as>? I'm pretty awesome.</speak>""",
        """<speak><say-as interpret-as="interjection">knock knock</say-as>., Please let me in. It's your favorite assistant...</speak>""",
        """<speak><voice name="Ivy">This is Ivy. Testing Testing, 1, 2, 3</voice></speak>""",
        """<speak><say-as interpret-as="interjection">yay</say-as>, I'm Alive... Hopefully you can hear me speaking?</speak>""",
        """<speak>Hi, I am Alexa. <voice name="Matthew">Hi, I am Matthew.</voice><voice name="Kendra">Hi, I am Kendra.</voice> <voice name="Joanna">Hi, I am Joanna.</voice><voice name="Kimberly">Hi, I am Kimberly.</voice> <voice name="Ivy">Hi, I am Ivy.</voice><voice name="Joey">, and I am Joey.</voice> Don't we make a great team?</speak>""",
        "Testing Testing, 1, 2, 3.",
        "Everybody have fun tonight. Everybody have fun tonight. Everybody Wang Chung tonight. Everybody have fun.",
        "Being able to make me say whatever you want is the coolest thing since sliced bread!",
        "I said a hip hop, Hippie to the hippie, The hip, hip a hop, and you don't stop, a rock it out, Bubba to the bang bang boogie, boobie to the boogie To the rhythm of the boogie the beat, Now, what you hear is not a test, I'm rappin' to the beat",
        "This is how we do it!. It's Friday night, and I feel alright. The party is here on the West side. So I reach for my 40 and I turn it up. Designated driver take the keys to my truck, Hit the shore 'cause I'm faded, Honeys in the street say, Monty, yo we made it!. It feels so good in my hood tonight, The summertime skirts and the guys in Khannye.",
        "Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Heroes in a half-shell Turtle power!... They're the world's most fearsome fighting team (We're really hip!), They're heroes in a half-shell and they're green (Hey - get a grip!), When the evil Shredder attacks!!!, These Turtle boys don't cut him no slack!."
    ]
    if(!ttsMsg) { ttsMsg = getRandomItem(items) }
    speak(ttsMsg)
}

void speak(String msg) {
    logTrace("speak() command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with speak($msg) command", true) }
        // msg = cleanString(msg, true)
        speechCmd([cmdDesc: "SpeakCommand", message: msg, newVolume: (state.newVolume ?: null), oldVolume: (state.oldVolume ?: null), cmdDt: now()])
        return
    }
    logWarn("Uh-Oh... The speak($msg) Command is NOT Supported by this Device!!!")
}

String cleanString(String str, Boolean frcTrans=false) {
    if(!str) { return sNULL }
    //Cleans up characters from message
    str.replaceAll(~/[^a-zA-Z0-9-?%°., ]+/, sBLANK)?.replaceAll(/\s\s+/, " ")
    str = textTransform(str, frcTrans)
    // log.debug "cleanString: $str"
    return str
}

private String textTransform(String str, Boolean force=false) {
    if(!force && settings?.disableTextTransform == true) { return str }
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
            } else { seqCmds?.push([command: (isSSML ? "ssml": "speak"), value: strArr?.join(" ")]); strArr = []; strArr?.push(wd as String) }
            if(wd == msgItems?.last()) { seqCmds?.push([command: (isSSML ? "ssml": "speak"), value: strArr?.join(" ")]) }
        }
    }
    // log.debug "seqCmds: $seqCmds"
    return seqCmds
}

def sendTestAnnouncement() {
    playAnnouncement("Echo Speaks announcement test on ${device?.label?.replace("Echo - ", sBLANK)}")
}

def sendTestAnnouncementAll() {
    playAnnouncementAll("Echo Speaks Announcement Test on All devices")
}

def sendTestAlexaMsg() {
    sendAlexaAppNotification("Test Alexa Notification from ${device?.displayName}")
}

@Field static final Map seqItemsAvailFLD = [
        other: [
            "weather":null, "traffic":null, "flashbriefing":null, "goodnews":null, "goodmorning":null, "goodnight":null, "cleanup":null,
            "singasong":null, "tellstory":null, "funfact":null, "joke":null, "playsearch":null, "calendartoday":null,
            "calendartomorrow":null, "calendarnext":null, "stop":null, "stopalldevices":null,
            "dnd_duration": "2H30M", "dnd_time": "00:30", "dnd_all_duration": "2H30M", "dnd_all_time": "00:30",
            "cannedtts_random": ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"],
            "sound": "message",
            "date": null, "time": null,
            "wait": "value (seconds)", "volume": "value (0-100)", "speak": "message", "announcement": "message",
            "announcementall": "message", "pushnotification": "message", "email": null, "voicecmdtxt": "voice command as text"
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

def executeSequenceCommand(String seqStr) {
    if(seqStr) {
        List seqList = seqStr?.split(",,")
        // log.debug "seqList: ${seqList}"
        List seqItems = []
        if(seqList?.size()) {
            seqList?.each {
                List li = it?.toString()?.split("::")
                // log.debug "li: $li"
                if(li.size()) {
                    String cmd = li[0]?.trim()?.toString()?.toLowerCase() as String
                    Boolean isValidCmd = (seqItemsAvailFLD.other?.containsKey(cmd) || (seqItemsAvailFLD.music?.containsKey(cmd)) || (seqItemsAvailFLD.dnd?.containsKey(cmd)))
                    Boolean isMusicCmd = (seqItemsAvailFLD.music?.containsKey(cmd) && !seqItemsAvailFLD.other?.containsKey(cmd) && !seqItemsAvailFLD.dnd?.containsKey(cmd))
                    // log.debug "cmd: $cmd | isValidCmd: $isValidCmd | isMusicCmd: $isMusicCmd"

                    if(!isValidCmd) { logError("executeSequenceCommand command ($cmd) is not a valid sequence command!!!"); return }

                    if(isMusicCmd) {
                        List valObj = (li[1]?.trim()?.toString()?.contains("::")) ? li[1]?.trim()?.split("::") : [li[1]?.trim() as String]
                        String provID = seqItemsAvailFLD.music[cmd]
                        if(!isCommandTypeAllowed(seqItemsAvailFLD.musicAlt[cmd])) { logError("Current Music Sequence command ($cmd) not allowed... "); return }
                        if (!valObj || valObj[0] == sBLANK) { logError("Play Music Sequence it Searchphrase empty"); return }
                        Map validObj = getMusicSearchObj(valObj[0], provID, valObj[1] ?: null)
                        if(!validObj) { return }
                        seqItems?.push([command: validObj])
                    } else {
                        if(li.size() == 1) {
                            seqItems.push([command: cmd])
                        } else if(li.size() == 2) {
                            seqItems.push([command: cmd, value: li[1]?.trim()])
                        }
                    }
                }
            }
        }
        logDebug("executeSequenceCommand Items: $seqItems | seqStr: ${seqStr}")
        if(seqItems.size()) {
            sendMultiSequenceCommand(seqItems, "executeSequenceCommand")
        }
    }
}

/*******************************************************************
            Speech Queue Logic
*******************************************************************/

Integer getRecheckDelay(Integer msgLen=null, Boolean addRandom=false) {
    def random = new Random()
    Integer randomInt = random?.nextInt(5) //Was using 7
    Integer twd = ttsWordDelay ? ttsWordDelay?.toInteger() : 2
    if(!msgLen) { return 30 }
    def v = (msgLen <= 14 ? twd : (msgLen / 14)) as Integer
    // logTrace("getRecheckDelay($msgLen) | delay: $v + $randomInt")
    return addRandom ? (v + randomInt) : v+2
}

Integer getLastTtsCmdSec() { return !state.lastTtsCmdDt ? 1000 : GetTimeDiffSeconds((String)state.lastTtsCmdDt).toInteger() }
Integer getLastQueueCheckSec() { return !state.q_lastCheckDt ? 1000 : GetTimeDiffSeconds((String)state.q_lastCheckDt).toInteger() }
Integer getCmdExecutionSec(String timeVal) { return !timeVal ? null : GetTimeDiffSeconds(timeVal).toInteger() }

private Integer getQueueSize() {
    Map cmdQueue = ((Map<String,Object>)state).findAll { it?.key?.toString()?.startsWith("qItem_") }
    Integer t0 = cmdQueue.size()
    return (t0 ?: 0)
}

private String getQueueSizeStr() {
    Integer size = getQueueSize()
    return "($size) Item${size>1 || size==0 ? "s" : sBLANK}"
}

private void processLogItems(String t, List ll, Boolean es=false, Boolean ee=true) {
    if(t && ll?.size() && settings?.logDebug) {
        if(ee) { "log${t?.capitalize()}"(" ") }
        "log${t?.capitalize()}"("└─────────────────────────────")
        ll?.each { "log${t?.capitalize()}"(it) }
        if(es) { "log${t?.capitalize()}"(" ") }
    }
}

@Field static final List<String> clnItemsFLD = [
        "qBlocked", "qCmdCycleCnt", "useThisVolume", "lastVolume", "lastQueueCheckDt", "loopChkCnt", "speakingNow",
        "cmdQueueWorking", "firstCmdFlag", "recheckScheduled", "cmdQIndexNum", "curMsgLen", "lastTtsCmdDelay",
        "lastQueueMsg", "lastTtsMsg"
]

private void stateCleanup() {
    if(state.lastVolume) { state?.oldVolume = state?.lastVolume }
    clnItemsFLD.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
}

void resetQueue(String src=sBLANK) {
    logTrace("resetQueue($src)")
    Map cmdQueue = ((Map<String,Object>)state).findAll { it?.key?.toString()?.startsWith("qItem_") }
    cmdQueue.each { cmdKey, cmdData -> state?.remove(cmdKey) }
    unschedule("queueCheck")
    unschedule("checkQueue")
    state.q_blocked = false
    state.q_cmdCycleCnt = null
    state.newVolume = null
    state.q_lastCheckDt = sNULL
    state.q_loopChkCnt = null
    state.q_speakingNow = false
    state.q_cmdWorking = false
    state.q_firstCmdFlag = false
    state.q_recheckScheduled = false
    state.q_cmdIndexNum = null
    state.q_curMsgLen = null
    state.q_lastTtsCmdDelay = null
    state.q_lastTtsMsg = null
    state.q_lastMsg = null
}

Integer getNextQueueIndex() { return state.q_cmdIndexNum ? state?.q_cmdIndexNum+1 : 1 }
Integer getCurrentQueueIndex() { return state.q_cmdIndexNum ?: 1 }
String getAmazonDomain() { return (String)state.amazonDomain ?: (String)parent?.settings?.amazonDomain } // does this work for parent call on HE?
String getAmazonUrl() {return "https://alexa.${getAmazonDomain()}".toString() }
Map getQueueItems() { return ((Map<String,Object>)state).findAll { it?.key?.toString()?.startsWith("qItem_") } }

private void queueCheckSchedHealth() {
    Integer cmdCnt = state?.q_cmdCycleCnt
    Integer lastChk = getLastQueueCheckSec()
    Integer qSize = getQueueSize()
    logDebug("queueCheckSchedHealth | Qsize: ${qSize} | LastChk: ${lastChk}")
    if(qSize >= 2 && lastChk > 120) {
        schedQueueCheck(4, true, null, "queueCheck(missed schedule)")
        logDebug("queueCheck | Scheduling Queue Check for (4 sec) | Possible Lost Recheck Schedule")
    }
}

private void schedQueueCheck(Integer delay=30, Boolean overwrite=true, data=null, String src) {
    Map opts = [:]
    opts["overwrite"] = overwrite
    if(data) { opts["data"] = data }
    runIn(delay, "queueCheck", opts)
    state.q_recheckScheduled = true
    logDebug("Scheduled Queue Check for (${delay}sec) | Overwrite: (${overwrite}) | q_recheckScheduled: (${state?.q_recheckScheduled}) | Source: (${src})")
}

            //queueEchoCmd("Speak", msgLen, headers, body, isFirstCmd)
public void queueEchoCmd(String type, Integer msgLen, Map headers, body=null, Boolean firstRun=false) {
    Integer qSize = getQueueSize()
    if((Boolean)state.q_blocked == true) { log.warn "│ Queue Temporarily Blocked (${qSize} Items): | Working: (${state?.q_cmdWorking}) | Recheck: (${state?.q_recheckScheduled})"; return }
    List logItems = []
    Map dupItems = state?.findAll { it?.key?.toString()?.startsWith("qItem_") && it?.value?.type == type && it?.value?.headers && it?.value?.headers?.message == headers.message }
    logItems.push("│ Queue Active: (${state?.q_cmdWorking}) | Recheck: (${state?.q_recheckScheduled}) ")
    if(dupItems?.size()) {
        if(headers.message) { logItems.push("│ Message(${msgLen} char): ${headers.message?.take(190)?.trim()}${msgLen > 190 ? "..." : sBLANK}") }
        logItems.push("│ Ignoring (${headers.cmdType}) Command... It Already Exists in QUEUE!!!")
        logItems.push("┌────────── Echo Queue Warning ──────────")
        processLogItems("warn", logItems, true, true)
        return
    }
    Integer qIndNum = getNextQueueIndex()
    // log.debug "qIndexNum: $qIndNum"
    state.q_cmdIndexNum = qIndNum
    headers.qId = qIndNum
    state."qItem_${qIndNum}" = [type: type, headers: headers, body: body, newVolume: (headers.newVolume ?: null), oldVolume: (headers.oldVolume ?: null)]
    state.newVolume = null
    state.oldVolume = null
    if(headers.volume)  {  logItems.push("│ Volume (${headers.volume})") }
    if(headers.message) {  logItems.push("│ Message(Len: ${headers.message?.toString()?.length()}): ${headers.message?.take(200)?.trim()}${headers.message?.toString()?.length() > 200 ? "..." : sBLANK}") }
    if(headers.cmdType) {  logItems.push("│ CmdType: (${headers.cmdType})") }
                            logItems.push("┌───── Added Echo Queue Item (${state.q_cmdIndexNum}) ─────")
    // queueCheckSchedHealth()
    if(!firstRun) {
        processLogItems("trace", logItems, false, true)
    }
}

private void queueCheck(data) {
    // log.debug "queueCheck | ${data}"
    Integer qSize = getQueueSize()
    Boolean qEmpty = (qSize == 0)
    state.q_lastCheckDt = getDtNow()
    if(!qEmpty) {
        if(qSize && qSize >= 10) {
            state.q_blocked = true
            if (qSize < 20) {
                logWarn("queueCheck | Queue Item Count (${qSize}) is filling up... Blocking Queue Additions Until Queue Size Drops below 10!!!", true)
                schedQueueCheck(delay, true, null, "queueCheck(filling)")
            } else {
                logWarn("queueCheck | Queue Item Count (${qSize}) is abnormally high... Resetting Queue", true)
                resetQueue("queueCheck | High Item Count")
                return
            }
        } else { state.q_blocked = false }
        if(data && data?.rateLimited == true) {
            Integer delay = data?.delay as Integer ?: getRecheckDelay(state.q_curMsgLen)
            schedQueueCheck(delay, true, null, "queueCheck(rate-limit)")
            logDebug("queueCheck | Scheduling Queue Check for (${delay} sec) | Recheck for RateLimiting")
        }
        processCmdQueue()
        return
    } else {
        logDebug("queueCheck | Nothing in the Queue | Performing Queue Reset...")
        resetQueue("queueCheck | Queue Empty")
        return
    }
}

void processCmdQueue() {
    state.q_cmdWorking = true
    Integer q_cmdCycleCnt = state.q_cmdCycleCnt
    state.q_cmdCycleCnt = q_cmdCycleCnt ? q_cmdCycleCnt+1 : 1
    Map cmdQueue = getQueueItems()
    if(cmdQueue?.size()) {
        state.q_recheckScheduled = false
        def cmdKey = cmdQueue.keySet()?.sort(false) { it.tokenize('_')[-1] as Integer }?.first()
        Map cmdData = state[cmdKey as String]
        // logDebug("processCmdQueue | Key: ${cmdKey} | Queue Items: (${getQueueItems()})")
        cmdData.headers["queueKey"] = cmdKey
        Integer q_loopChkCnt = state.q_loopChkCnt ?: 0
        if(state.q_lastTtsMsg == cmdData.headers?.message && (getLastTtsCmdSec() <= 10)) { state.q_loopChkCnt = (q_loopChkCnt >= 1) ? q_loopChkCnt+1 : 1 }
        // log.debug "q_loopChkCnt: ${state.q_loopChkCnt}"
        if(state.q_loopChkCnt && (state.q_loopChkCnt > 4) && (getLastTtsCmdSec() <= 10)) {
            state.remove(cmdKey as String)
            logWarn("processCmdQueue | Possible loop detected... Last message was the same as message sent <10 seconds ago. This message will be removed from the queue")
            schedQueueCheck(2, true, null, "processCmdQueue(removed duplicate)")
            state.q_cmdWorking = false
        } else {
            state.q_lastMsg = cmdData?.headers?.message
            speechCmd(cmdData?.headers, true)
        }
    } else { state.q_cmdWorking = false }
}

Integer getAdjCmdDelay(Integer elap, reqDelay) {
    if((elap >= 0) && (reqDelay >= 0)) {
        Integer res = (elap - reqDelay)?.abs()
        logTrace("getAdjCmdDelay | reqDelay: $reqDelay | elap: $elap | del: ${res < 3 ? 3 : res+3}")
        return res < 3 ? 3 : res+3
    }
    return reqDelay //del
}

def testMultiCmd() {
    sendMultiSequenceCommand([[command: "volume", value: 60], [command: "speak", value: "super duper test message 1, 2, 3"], [command: "volume", value: 30]], "testMultiCmd")
}

private void speechCmd(headers=[:], Boolean isQueueCmd=false) {
    // if(isQueueCmd) log.warn "QueueBlocked: ${state.q_blocked} | cycleCnt: ${state.q_cmdCycleCnt} | isQCmd: ${isQueueCmd}"
    state.q_speakingNow = true
    String tr = "speechCmd (${headers.cmdDesc}) | Msg: ${headers.message}"
    tr += headers.newVolume ? " | SetVolume: (${headers.newVolume})" :sBLANK
    tr += headers.oldVolume ? " | Restore Volume: (${headers.oldVolume})" :sBLANK
    tr += headers.msgDelay  ? " | RecheckSeconds: (${headers.msgDelay})" :sBLANK
    tr += headers.queueKey  ? " | QueueItem: [${headers.queueKey}]" :sBLANK
    tr += headers.cmdDt     ? " | CmdDt: (${headers.cmdDt})" :sBLANK
    logTrace("${tr}")

    def random = new Random()
    def randCmdId = random?.nextInt(300)

    Map queryMap = [:]
    List logItems = []
    String healthStatus = getHealthStatus()
    if(!headers || !(healthStatus in ["ACTIVE", "ONLINE"])) {
        if(!headers) { logError("speechCmd | Error${!headers ? " | headers are missing" : sBLANK} ") }
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
    if(!settings.disableQueue) {
        logItems.push("│ Last TTS Sent: (${lastTtsCmdSec} seconds) ")

        Boolean isFirstCmd = (state.q_firstCmdFlag != true)
        if(isFirstCmd) {
            logItems?.push("│ First Command: (${isFirstCmd})")
            headers["queueKey"] = "qItem_1"
            state.q_firstCmdFlag = true
        }
        Boolean sendToQueue = (isFirstCmd || (lastTtsCmdSec < 3) || (!isQueueCmd && state.q_speakingNow == true))
        if(!isQueueCmd) { logItems?.push("│ SentToQueue: (${sendToQueue})") }
        // log.warn "speechCmd - QUEUE DEBUG | sendToQueue: (${sendToQueue?.toString()?.capitalize()}) | isQueueCmd: (${isQueueCmd?.toString()?.capitalize()}) | QueueSize: (${getQueueSize()}) | lastTtsCmdSec: [${lastTtsCmdSec}] | isFirstCmd: (${isFirstCmd?.toString()?.capitalize()}) | q_speakingNow: (${state.q_speakingNow?.toString()?.capitalize()}) | RecheckDelay: [${recheckDelay}]"
        if(sendToQueue) {
            queueEchoCmd("Speak", msgLen, headers, body, isFirstCmd)
            runIn((settings?.autoResetQueue ?: 180), "resetQueue")
            if(!isFirstCmd) {
                // log.debug "lastTtsCmdSec: ${getLastTtsCmdSec()} | Last Delay: ${state.q_lastTtsCmdDelay}"
                if(state.q_lastTtsCmdDelay && getLastTtsCmdSec() > state.q_lastTtsCmdDelay + 15) schedQueueCheck(3, true, null, "speechCmd(QueueStuck)")
                return
            }
        }
    }
    try {
        Map headerMap = getCookieMap()
        headers.each { k,v-> headerMap[k] = v }
        Integer qSize = getQueueSize()
        logItems.push("│ Queue Items: (${qSize>=1 ? qSize-1 : 0}) │ Working: (${state?.q_cmdWorking})")

        if(headers.message) {
            state.q_curMsgLen = msgLen
            state.q_lastTtsCmdDelay = recheckDelay
            schedQueueCheck(recheckDelay, true, null, "speechCmd(sendCloudCommand)")
            logItems.push("│ Rechecking: (${recheckDelay} seconds)")
            logItems.push("│ Message(${msgLen} char): ${headers?.message?.take(190)?.trim()}${msgLen > 190 ? "..." : sBLANK}")
            state.q_lastTtsMsg = headers.message
            // state?.lastTtsCmdDt = getDtNow()
        }
        if(headerMap.oldVolume) {logItems.push("│ Restore Volume: (${headerMap.oldVolume}%)") }
        if(headerMap.newVolume) {logItems.push("│ New Volume: (${headerMap.newVolume}%)") }
        logItems.push("│ Current Volume: (${device?.currentValue("volume")}%)")
        Boolean isSSML = (headers?.message?.toString()?.startsWith("<speak>") && headers?.message?.toString()?.endsWith("</speak>"))
        logItems.push("│ Command: (SpeakCommand)${isSSML ? " | (SSML)" : sBLANK}")
        try {
            String bodyObj = sNULL
            List seqCmds = []
            if(headerMap.newVolume) { seqCmds.push([command: "volume", value: headerMap.newVolume]) }
            seqCmds = seqCmds + msgSeqBuilder(headerMap?.message)
            if(headerMap.oldVolume) { seqCmds.push([command: "volume", value: headerMap.oldVolume]) }
            bodyObj = new groovy.json.JsonOutput().toJson(multiSequenceBuilder(seqCmds))

            Map params = [
                uri: getAmazonUrl(),
                path: "/api/behaviors/preview",
                headers: headerMap,
                contentType: sAPPJSON,
                timeout: 20,
                body: bodyObj
            ]
            Map extData = [
                cmdDt:(headerMap.cmdDt ?: null), queueKey: (headerMap.queueKey ?: null), cmdDesc: (headerMap.cmdDesc ?: null), msgLen: msgLen, isSSML: isSSML, deviceId: device?.getDeviceNetworkId(), msgDelay: (headerMap.msgDelay ?: null),
                message: (headerMap.message ? headerMap.message : null), newVolume: (headerMap.newVolume ?: null), oldVolume: (headerMap.oldVolume ?: null), cmdId: (headerMap.cmdId ?: null),
                qId: (headerMap.qId ?: null)
            ]
            httpPost(params) { response->
                def sData = response?.data ?: null
                extData["amznReqId"] = response?.headers["x-amz-rid"] ?: null
                postCmdProcess(sData, response?.status, extData)
            }
        } catch (ex) {
            respExceptionHandler(ex, "speechCmd")
        }
        logItems?.push("┌─────── Echo Command ${isQueueCmd && !settings?.disableQueue ? " (From Queue) " : sBLANK} ────────")
        processLogItems("debug", logItems)
    } catch (ex) {
        logError("speechCmd Exception: ${ex}")
    }
}

private postCmdProcess(resp, statusCode, Map data) {
    if(data && data?.deviceId && (data?.deviceId == device?.getDeviceNetworkId())) {
        String respMsg = resp?.message ?: null
        String respMsgLow = resp?.message ? resp?.message?.toString()?.toLowerCase() : null
        if(statusCode == 200) {
            Long execTime = data?.cmdDt ? (now()-data?.cmdDt) : 0L
            if(data?.queueKey) {
                logDebug("Command Completed | Removing Queue Item: ${data?.queueKey}")
                state.remove(data?.queueKey as String)
            }
            String pi = data?.cmdDesc ?: "Command"
            pi += data?.isSSML ? " (SSML)" :sBLANK
            pi += " Sent"
            pi += " | (${data?.message})"
            pi += logDebug && !logInfo && data?.msgLen ? " | Length: (${data?.msgLen}) " :sBLANK
            pi += data?.msgDelay ? " | Runtime: (${data?.msgDelay} sec)" :sBLANK
            pi += logDebug && data?.amznReqId ? " | Amazon Request ID: ${data?.amznReqId}" :sBLANK
            pi += logDebug && data?.qId ? " | QueueID: (${data?.qId})" :sBLANK
            pi += " | QueueItems: (${getQueueSize()})"
            // pi += " | Time to Execute Msg: (${execTime}ms)"
            logInfo(pi)

            if(data?.cmdDesc && data.cmdDesc == "SpeakCommand" && data?.message) {
                state.lastTtsCmdDt = getDtNow()
                String lastMsg = data?.message as String ?: "Nothing to Show Here..."
                sendEvent(name: "lastSpeakCmd", value: "${lastMsg}", descriptionText: "Last Text Spoken: ${lastMsg}", display: true, displayed: true)
                sendEvent(name: "lastCmdSentDt", value: "${state.lastTtsCmdDt}", descriptionText: "Last Command Timestamp: ${state.lastTtsCmdDt}", display: false, displayed: false)
                if(data?.oldVolume || data?.newVolume) {
                    sendEvent(name: "level", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: true, displayed: true)
                    sendEvent(name: "volume", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: true, displayed: true)
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
            resetQueue("postCmdProcess | Error")
            return
        }
    }
}

/*****************************************************
                HELPER FUNCTIONS
******************************************************/
String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/icons/$imgName" }
Integer versionStr2Int(String str) { return str ? str.toString()?.replaceAll("\\.", sBLANK)?.toInteger() : null }
Boolean minVersionFailed() {
    try {
        Integer t0 = ((Map<String,Integer>)parent?.minVersions())["echoDevice"]
        Integer minDevVer = t0 ?: null
        if(minDevVer != null && versionStr2Int(devVersionFLD) < minDevVer) { return true }
        else { return false }
    } catch (e) { 
        return false
    }
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now, false)
}

String getIsoDtNow() {
    def tf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(new Date());
}

String  formatDt(Date dt, Boolean mdy = true) {
    String formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
    def tf = new java.text.SimpleDateFormat(formatVal)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(dt)
}

//Integer getCmdExecutionSec(String timeVal) { return !timeVal ? null : GetTimeDiffSeconds(timeVal).toInteger() }
Long GetTimeDiffSeconds(String strtDate, String stpDate=sNULL) {
    if((strtDate && !stpDate) || (strtDate && stpDate)) {
        Date now = new Date()
        String stopVal = stpDate ? stpDate : formatDt(now, false)
        Long start = Date.parse("E MMM dd HH:mm:ss z yyyy", strtDate).getTime()
        Long stop = Date.parse("E MMM dd HH:mm:ss z yyyy", stopVal).getTime()
        Long diff =  ((stop - start) / 1000L)
        return diff
    } else { return null }
}

Date parseDt(String dt, String dtFmt) {
    return Date.parse(dtFmt, dt)
}

String parseFmtDt(String parseFmt, String newFmt, String dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(newDt)
}

Boolean ok2Notify() {
    return (Boolean)parent?.getOk2Notify()
}

private void logSpeech(String msg, status, String error=sNULL) {
    Map o = [:]
    if(status) o.code = status
    if(error) o.error = error
    addToLogHistory("speechHistory", msg, o, 5)
}
private Integer stateSize() { String j = new groovy.json.JsonOutput().toJson(state); return j.length() }
private Integer stateSizePerc() { return (Integer) (((stateSize() / 100000)*100).toDouble().round(0)) }

private void addToLogHistory(String logKey, String msg, statusData, Integer max=10) {
    Boolean ssOk = true //(stateSizePerc() <= 70)
    String appId = device.getId()

    Map memStore = historyMapFLD[appId] ?: [:]
    List eData = (List)memStore[logKey] ?: []
    if(eData?.find { it?.message == msg }) { return }
    if(status) { eData.push([dt: getDtNow(), message: msg, status: statusData]) }
    else { eData.push([dt: getDtNow(), message: msg]) }
    Integer lsiz=eData.size()
    if(!ssOk || lsiz > max) { eData = eData.drop( (lsiz-max) ) }
    updMemStoreItem(logKey, eData)
}

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug addHead(msg) } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info " "+addHead(msg) } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace addHead(msg) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn " "+addHead(msg) }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15); } }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error addHead(msg)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (e) {
        }
        if(a) log.error addHead(a)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, null, 15) }
}

String addHead(String msg) {
    return "Echo ("+devVersionFLD+") | "+msg
}

Map getLogHistory() {
    return [ warnings: getMemStoreItem("warnHistory") ?: [], errors: getMemStoreItem("errorHistory") ?: [], speech: getMemStoreItem("speechHistory") ?: [] ]
}
public clearLogHistory() {
    updMemStoreItem("warnHistory", [])
    updMemStoreItem("errorHistory",[])
    updMemStoreItem("speechHistory", [])
    mb()
}

void incrementCntByKey(String key) {
    Long evtCnt = state?."${key}"
    evtCnt = evtCnt != null ? evtCnt : 0
    evtCnt++
    state."${key}" = evtCnt
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
        cntItems?.each { String k,v -> out.usage[k.replace("use_", sBLANK) as String] = v as Integer ?: 0 }
    }
    if(errItems?.size()) {
        out["errors"] = [:]
        errItems?.each { String k,v -> out.errors[k.replace("err_", sBLANK) as String] = v as Integer ?: 0 }
    }
    return out
}

Map sequenceBuilder(cmd, val) {
    Map seqJson = null
    if (cmd instanceof Map) {
        seqJson = cmd?.sequence ?: cmd
    } else { seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": createSequenceNode(cmd, val)] }
    Map seqObj = [behaviorId: (seqJson?.sequenceId ? cmd?.automationId : "PREVIEW"), sequenceJson: new groovy.json.JsonOutput().toJson(seqJson) as String, status: "ENABLED"]
    return seqObj
}

Map multiSequenceBuilder(List commands, Boolean parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList.push(createSequenceNode(cmdItem?.command, cmdItem?.value)) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    Map seqObj = sequenceBuilder(seqJson, null)
    return seqObj
}

Map createSequenceNode(command, value, String devType=null, String devSerial=null) {
    try {
        Boolean remDevSpecifics = false
        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            "operationPayload": [
                "deviceType": devType ?: (String)state.deviceType,
                "deviceSerialNumber": devSerial ?: (String)state.serialNumber,
                "locale": ((String)state.regionLocale ?: "en-US"),
                "customerId": (String)state.deviceOwnerCustomerId
            ]
        ]
        switch (command?.toString()?.toLowerCase()) {
            case "weather":
                seqNode.type = "Alexa.Weather.Play"
                break
            case "traffic":
                seqNode.type = "Alexa.Traffic.Play"
                break
            case "flashbriefing":
                seqNode.type = "Alexa.FlashBriefing.Play"
                break
            case "goodmorning":
                seqNode.type = "Alexa.GoodMorning.Play"
                break
            case "goodnight":
                seqNode.type = "Alexa.GoodNight.Play"
                break
            case "cleanup":
                seqNode.type = "Alexa.CleanUp.Play"
                break
            case "singasong":
                seqNode.type = "Alexa.SingASong.Play"
                break
            case "tellstory":
                seqNode.type = "Alexa.TellStory.Play"
                break
            case "funfact":
                seqNode.type = "Alexa.FunFact.Play"
                break
            case "joke":
                seqNode.type = "Alexa.Joke.Play"
                break
            case "calendartomorrow":
                seqNode.type = "Alexa.Calendar.PlayTomorrow"
                break
            case "calendartoday":
                seqNode.type = "Alexa.Calendar.PlayToday"
                break
            case "calendarnext":
                seqNode.type = "Alexa.Calendar.PlayNext"
                break
            case "date":
                seqNode.type = "Alexa.Date.Play"
                seqNode.skillId = "amzn1.ask.1p.dateandtime"
                break
            case "time":
                seqNode.type = "Alexa.Time.Play"
                seqNode.skillId = "amzn1.ask.1p.dateandtime"
                break
            case "stop":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.Stop"
                seqNode.skillId = "amzn1.ask.1p.alexadevicecontrols"
                seqNode.operationPayload?.devices = [ [deviceType: (String)state.deviceType, deviceSerialNumber: (String)state.serialNumber] ]
                seqNode.operationPayload?.isAssociatedDevice = false
                break
            case "stopalldevices":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.Stop"
                seqNode.operationPayload?.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                seqNode.operationPayload?.isAssociatedDevice = false
                break
            case "cannedtts_random":
                List okVals = ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
                if(!(value in okVals)) { return null }
                seqNode.type = "Alexa.CannedTts.Speak"
                seqNode.operationPayload?.cannedTtsStringId = "alexa.cannedtts.speak.curatedtts-category-${value}/alexa.cannedtts.speak.curatedtts-random"
                break
            case "cannedtts":
                List okVals = ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
                if(!(value in okVals)) { return null }
                seqNode.type = "Alexa.CannedTts.Speak"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : [value as String, value as String]
                seqNode.operationPayload.cannedTtsStringId = "alexa.cannedtts.speak.curatedtts-category-${valObj[0]}/alexa.cannedtts.speak.curatedtts-${valObj[1]}"
                break
            case "sound":
                String sndName =sBLANK
                if(value?.startsWith("amzn_sfx_")) {
                    sndName = value
                } else {
                    Map sounds = (Map)parent?.getAvailableSounds()
                    if(!(sounds[value])) { return null }
                    sndName = sounds[value]
                }
                seqNode.type = "Alexa.Sound"
                seqNode.operationPayload?.soundStringId = sndName
                break
            case "wait":
                remDevSpecifics = true
                seqNode.operationPayload?.remove('customerId')
                seqNode.type = "Alexa.System.Wait"
                seqNode.operationPayload?.waitTimeInSeconds = value?.toInteger() ?: 5
                break
            case "volume":
                seqNode.type = "Alexa.DeviceControls.Volume"
                seqNode.operationPayload?.value = value
                break
            case "dnd_duration":
            case "dnd_time":
            case "dnd_all_duration":
            case "dnd_all_time":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.DoNotDisturb"
                seqNode.skillId = "amzn1.ask.1p.alexadevicecontrols"
                seqNode.operationPayload?.customerId = (String)state.deviceOwnerCustomerId
                if(command == "dnd_all_time" || command == "dnd_all_duration") {
                    seqNode.operationPayload?.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                }
                if(command == "dnd_time" || command == "dnd_duration") {
                    seqNode.operationPayload?.devices = [ [deviceAccountId: (String)state.deviceAccountId, deviceType: (String)state.deviceType, deviceSerialNumber: (String)state.serialNumber] ]
                }
                seqNode?.operationPayload?.action = "Enable"
                if(command == "dnd_time" || command == "dnd_all_time") {
                    seqNode.operationPayload?.until = "TIME#T${value}"
                } else if (command == "dnd_duration" || command == "dnd_all_duration") { seqNode?.operationPayload?.duration = "DURATION#PT${value}" }
                seqNode.operationPayload?.timeZoneId = "America/Detroit" //location?.timeZone?.ID ?: null
                break
            case "speak":
                seqNode.type = "Alexa.Speak"
                value = cleanString(value as String)
                seqNode.operationPayload?.textToSpeak = value as String
                break
            case "ssml":
            case "announcement":
            case "announcementall":
            case "announcement_devices":
                remDevSpecifics = true
                seqNode.type = "AlexaAnnouncement"
                seqNode.operationPayload?.expireAfter = "PT5S"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : ["Echo Speaks", value as String]
                // log.debug "valObj(size: ${valObj?.size()}): $valObj"
                // valObj[1] = valObj[1]?.toString()?.replace(/([^0-9]?[0-9]+)\.([0-9]+[^0-9])?/, "\$1,\$2")
                // log.debug "valObj[1]: ${valObj[1]}"
                seqNode.operationPayload?.content = [[ locale: ((String)state.regionLocale ?: "en-US"), display: [ title: valObj[0], body: valObj[1]?.toString().replaceAll(/<[^>]+>/, '') ], speak: [ type: (command == "ssml" ? "ssml" : "text"), value: valObj[1] as String ] ] ]
                seqNode.operationPayload?.target = [ customerId : (String)state.deviceOwnerCustomerId ]
                if(!(command in ["announcementall", "announcement_devices"])) {
                    seqNode?.operationPayload?.target?.devices = [ [ deviceTypeId: (String)state.deviceType, deviceSerialNumber: (String)state.serialNumber ] ]
                } else if(command == "announcement_devices" && valObj?.size() && valObj[2] != null) {
                    List devObjs = new groovy.json.JsonSlurper().parseText(valObj[2])
                    seqNode.operationPayload?.target?.devices = devObjs
                }
                break
            case "pushnotification":
                remDevSpecifics = true
                seqNode.type = "Alexa.Notifications.SendMobilePush"
                seqNode.skillId = "amzn1.ask.1p.alexanotifications"
                seqNode.operationPayload?.notificationMessage = value as String
                seqNode.operationPayload?.alexaUrl = "#v2/behaviors"
                seqNode.operationPayload?.title = "Echo Speaks"
                break
            case "email":
                seqNode.type = "Alexa.Operation.SkillConnections.Email.EmailSummary"
                seqNode.skillId = "amzn1.ask.1p.email"
                seqNode.operationPayload?.targetDevice = [deviceType: (String)state.deviceType, deviceSerialNumber: (String)state.serialNumber ]
                seqNode.operationPayload?.connectionRequest = [uri: "connection://AMAZON.Read.EmailSummary/amzn1.alexa-speechlet-client.DOMAIN:ALEXA_CONNECT", input: [:] ]
                seqNode.operationPayload?.remove('deviceType')
                seqNode.operationPayload?.remove('deviceSerialNumber')
                break
            case "goodnews":
                seqNode.type = "Alexa.GoodNews.Play"
                seqNode.skillId = "amzn1.ask.1p.goodnews"
                break
            case "voicecmdtxt":
                seqNode.type = "Alexa.TextCommand"
                seqNode.skillId = "amzn1.ask.1p.tellalexa"
                seqNode.operationPayload.text = value
                break
            default:
                return null
        }
        if(remDevSpecifics) {
            seqNode.operationPayload?.remove('deviceType')
            seqNode.operationPayload?.remove('deviceSerialNumber')
            seqNode.operationPayload?.remove('locale')
        }
        return seqNode
    } catch (ex) {
        logError("createSequenceNode Exception: ${ex}")
        return [:]
    }
}

// FIELD VARIABLE FUNCTIONS
private void updMemStoreItem(String key, val) {
    String appId = device.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    memStore[key] = val
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD
    // log.debug("updMemStoreItem(${key}): ${memStore[key]}")
}

private List getMemStoreItem(String key){
    String appId = device.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    return (List)memStore[key] ?: []
}

// Memory Barrier
@Field static java.util.concurrent.Semaphore theMBLockFLD=new java.util.concurrent.Semaphore(0)

static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}
/*
@Field static final String sHMLF = 'theHistMapLockFLD'
@Field static java.util.concurrent.Semaphore histMapLockFLD = new java.util.concurrent.Semaphore(1)

private Integer getSemaNum(String name) {
    if(name == sHMLF) return 0 
    log.warn "unrecognized lock name..."
    return 0
	// Integer stripes=22
	// if(name.isNumber()) return name.toInteger()%stripes
	// Integer hash=smear(name.hashCode())
	// return Math.abs(hash)%stripes
    // log.info "sema $name # $sema"
}

java.util.concurrent.Semaphore getSema(Integer snum) {
	switch(snum) {
		case 0: return histMapLockFLD
		default: log.error "bad hash result $snum"
			return null
	}
}

@Field volatile static Map<String,Long> lockTimesFLD = [:]
@Field volatile static Map<String,String> lockHolderFLD = [:]

Boolean getTheLock(String qname, String meth=sNULL, Boolean longWait=false) {
    Long waitT = longWait ? 1000L : 60L
    Boolean wait = false
    Integer semaNum = getSemaNum(qname)
    String semaSNum = semaNum.toString()
    def sema = getSema(semaNum)
    while(!((Boolean)sema.tryAcquire())) {
        // did not get the lock
        Long timeL = lockTimesFLD[semaSNum]
        if(timeL == null){
            timeL = now()
            lockTimesFLD[semaSNum] = timeL
            lockTimesFLD = lockTimesFLD
        }
        if(devModeFLD) log.warn "waiting for ${qname} ${semaSNum} lock access, $meth, long: $longWait, holder: ${(String)lockHolderFLD[semaSNum]}"
        pauseExecution(waitT)
        wait = true
        if((now() - timeL) > 30000L) {
            releaseTheLock(qname)
            if(devModeFLD) log.warn "overriding lock $meth"
        }
    }
    lockTimesFLD[semaSNum] = now()
    lockTimesFLD = lockTimesFLD
    lockHolderFLD[semaSNum] = "${device.getId()} ${meth}".toString()
    lockHolderFLD = lockHolderFLD
    return wait
}

void releaseTheLock(String qname){
    Integer semaNum=getSemaNum(qname)
    String semaSNum=semaNum.toString()
    def sema=getSema(semaNum)
    lockTimesFLD[semaSNum]=null
    lockTimesFLD=lockTimesFLD
    lockHolderFLD[semaSNum]=(String)null
    lockHolderFLD=lockHolderFLD
    sema.release()
} */
