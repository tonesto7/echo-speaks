/**
 *	Echo Speaks Zone Device (Hubitat ONLY)
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
@Field static final String devVersionFLD  = "4.0.9.3"
@Field static final String appModifiedFLD = "2021-03-13"
@Field static final String branchFLD      = "master"
@Field static final String platformFLD    = "Hubitat"
@Field static final Boolean betaFLD       = false
@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sLINEBR        = '<br>'
@Field static final String sTRUE          = 'true'
@Field static final String sFALSE         = 'false'
@Field static final String sMEDIUM        = 'medium'
@Field static final String sSMALL         = 'small'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sAPPJSON       = 'application/json'

// IN-MEMORY VARIABLES (Cleared only on HUB REBOOT or CODE UPDATES)
@Field volatile static Map<String,Map> historyMapFLD = [:]
@Field volatile static Map<String,Map> cookieDataFLD = [:]

static String devVersion()  { return devVersionFLD }
static Boolean isWS()       { return false }
static Boolean isZone()     { return true }

metadata {
    definition (name: "Echo Speaks Zone Device", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/drivers/echo-speaks-zone-device.groovy") {
        capability "Audio Notification"
        capability "Audio Volume"
        capability "Music Player"
        capability "Notification"
        capability "Refresh"
        capability "Sensor"
        capability "Speech Synthesis"

        attribute "alarmVolume", "number"
        attribute "alexaPlaylists", "JSON_OBJECT"
        attribute "alexaGuardStatus", "string"
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
        attribute "wasLastSpokenToDevice", "string"
	    
	    attribute "audioTrackData", "JSON_OBJECT" // To support SharpTools.io Album Art feature
        command "replayText"
if(!isZone()) {
        command "doNotDisturbOn"
        command "doNotDisturbOff"
}
        command "setAlarmVolume", [[name: "Alarm Volume*", type: "NUMBER", description: "Sets the devices Alarm notification volume"]]
        command "playWeather", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playSingASong", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playFlashBrief", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playFunFact", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playGoodNews", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playTraffic", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playJoke", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playSoundByName", [[name: "Sound Name", type: "STRING", description: "Sound object name"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "playTellStory", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodbye", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodNight", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayBirthday", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayCompliment", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayGoodMorning", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "sayWelcomeHome", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]

        command "playAnnouncement", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playAnnouncementAll", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"]]
if(!isZone()) {
}
        command "playCalendarToday", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playCalendarTomorrow", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playCalendarNext", [[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "stopAllDevices"

if(!isZone()) {
        command "searchMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."], [name: "Music Provider*", type: "ENUM", constraints: ["AMAZON_MUSIC", "APPLE_MUSIC", "TUNEIN", "PANDORA", "SIRIUSXM", "SPOTIFY", "I_HEART_RADIO", "CLOUDPLAYER"], description: "Select One of these Music Providers to use."], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restore volume after playing"]]
        command "searchAmazonMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchAppleMusic", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchPandora", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchIheart", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchSiriusXm", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchSpotify", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        // command "searchTidal", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
        command "searchTuneIn", [[name: "Music Search Phrase*", type: "STRING", description: "Enter the artist, song, playlist, etc."],[name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing"]]
}
        command "sendAlexaAppNotification", [ [name: "Notification Message*", type: "STRING", description: ""]]
if(!isZone()) {
        command "executeSequenceCommand", [[name: "Sequence Message Text*", type: "STRING", description: ""]]
        command "executeRoutineId", [[name: "Routine ID*", type: "STRING", description: ""]]
        command "createAlarm", [[name: "Alarm Label*", type: "STRING", description: "This is the title of the alarm"], [name: "Date*",type: "STRING", description: "Date (2021-01-05 | YYYY-MM-DD)"], [name: "Time*", type: "STRING", description: "Time (18:10 | HH:MM)"]]
        command "createReminder", [[name: "Reminder Label*", type: "STRING", description: "This is the title of the reminder"], [name: "Date*", type: "STRING", description: "Date (2021-01-05 | YYYY-MM-DD)"], [name: "Time*", type: "STRING", description: "Time (18:10 | HH:MM)"]]
        // command "createReminderNew", ["string", "string", "string", "string", "string"]
        command "removeNotification", [[name: "Notification ID to Remove*", type: "STRING", description: ""]]
        // command "removeAllNotificationsByType", ["string"]
        command "setWakeWord", [[name: "New Wake Word*", type: "STRING", description: ""]]
        command "renameDevice", [[name: "New Device Name*", type: "STRING", description: ""]]
}
        command "storeCurrentVolume"
        command "restoreLastVolume"
        command "togglePlayback"
        command "setVolumeAndSpeak", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""]]
        command "setVolumeSpeakAndRestore", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
//        command "volumeUp"
//        command "volumeDown"
        command "speechTest"
//        command "speak", [[name: "Message to Speak*", type: "STRING", description: ""], volume, voice]
        command "sendTestAnnouncement"
        command "sendTestAnnouncementAll"
if(!isZone()) {
        command "getDeviceActivity"
        command "getBluetoothDevices"
        command "connectBluetooth", [[name: "Bluetooth Device Label", type: "STRING", description: ""]]
        command "disconnectBluetooth"
        command "removeBluetooth", [[name: "Bluetooth Device Label*", type: "STRING", description: ""]]
}
        command "sendAnnouncementToDevices", ["string", "string", "object", "number", "number"]
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
            input "sendDevNotifAsAnnouncement", "bool", required: false, title: "Send Device Notifications as Announcements?", description: sBLANK, defaultValue: false
            // maxVolume not used
            // input "maxVolume", "number", required: false, title: "Set Max Volume for this device", description: "There will be a delay of 30-60 seconds in getting the current volume level"
        }
    }
}

def installed() {
    logInfo("${device?.displayName} Executing Installed...")
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "deviceStatus", value: "stopped_echo_gen1")
    sendEvent(name: "trackDescription", value: "Not Set")
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    sendEvent(name: "wasLastSpokenToDevice", value: sFALSE)
    sendEvent(name: "doNotDisturb", value: sFALSE)
    sendEvent(name: "onlineStatus", value: "online")
    sendEvent(name: "followUpMode", value: sFALSE)
    sendEvent(name: "alarmVolume", value: 0)
    sendEvent(name: "alexaWakeWord", value: "ALEXA")
    sendEvent(name: "mediaSource", value: "Not Set")
//    state.doNotDisturb = false
    initialize()
    runIn(20, "postInstall")
}

def updated() {
    logInfo("${device?.displayName} Executing Updated()")
    initialize()
}

def initialize() {
    logInfo("${device?.displayName} Executing initialize()")
    unschedule()
    state.refreshScheduled = false
    resetQueue()
    stateCleanup()
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return }
    schedDataRefresh(true)
    if(advLogsActive()) { runIn(1800, "logsOff") }
    state.websocketActive = isZone() ? parent?.relayGetWWebSocketStatus() : parent?.getWWebSocketStatus()
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
String getEchoOwner() { return (String)state.deviceOwnerCustomerId ?: sNULL }
String getEchoAccountId() { return (String)state.deviceAccountId ?: sNULL }

Map getEchoDevInfo(String cmd) {
    if(isCommandTypeAllowed(cmd)) {
	return [deviceTypeId: getEchoDeviceType(), deviceSerialNumber: getEchoSerial(), deviceOwnerCustomerId: getEchoOwner(), deviceAccountId: getEchoAccountId(), dni: device.deviceNetworkId ]
    }
   return null
}

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
    if(isZone()) return true

    if(!type) { if(!noLogs) { logWarn("Invalid Permissions Type Received: ${type}", true) }; return false }

    if(!isAuthOk(noLogs)) { return false }
    if(!getAmazonDomain()) { if(!noLogs) { logWarn("amazonDomain State Value Missing: ${getAmazonDomain()}", true) }; return false }
    if(!state.cookie || !state.cookie.cookie || !state.cookie.csrf) { if(!noLogs) { logWarn("Amazon Cookie State Values Missing: ${state.cookie}", true) }; setAuthState(false); return false }
    if(!(String)state.serialNumber) { if(!noLogs) { logWarn("SerialNumber State Value Missing: ${(String)state.serialNumber}", true) }; return false }
    if(!(String)state.deviceType) { if(!noLogs) { logWarn("DeviceType State Value Missing: ${(String)state.deviceType}", true) }; return false }
    if(!(String)state.deviceOwnerCustomerId) { if(!noLogs) { logWarn("OwnerCustomerId State Value Missing: ${(String)state.deviceOwnerCustomerId}", true) }; return false }

    Boolean isOnline = (device?.currentValue("onlineStatus") == "online")
    if(!isOnline) {
        if(!noLogs) { logWarn("Commands NOT Allowed! Device is currently (OFFLINE) | Type: (${type})", true) }
        triggerDataRrsh("found offline", true)
        return false
    }

    if(state.isSupportedDevice == false) { logWarn("You are using an Unsupported/Unknown Device all restrictions have been removed for testing! If commands function please report device info to developer", true); return true }
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
            case "cloudPlayer":
                warnMsg = "OOPS... Cloudplayer is NOT Supported by this Device!!!"
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
    if(isZone()) return true
    if(type && state?.permissions?.containsKey(type) && state?.permissions[type] == true) { return true }
    return false
}

void updateDeviceStatus(Map devData) {
    Boolean isOnline = false
    if(devData.size()) {
        isOnline = (Boolean)devData.online
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

        String devSerial = (String)devData.serialNumber
        state.serialNumber = devSerial

        String devType = devData.deviceType ?: sBLANK
        state.deviceType = devType

        state.deviceOwnerCustomerId = (String)devData.deviceOwnerCustomerId
        state.deviceAccountId = (String)devData.deviceAccountId

        String firmwareVer = devData.softwareVersion ?: "Not Set"
        state.softwareVersion = firmwareVer

        // state?.mainAccountCommsId = devData.mainAccountCommsId ?: null
        // log.debug "mainAccountCommsId: ${state.mainAccountCommsId}"
        if(!state.cookie) {
            state.cookie = devData.cookie
            cookieDataFLD = [:]
        }
        state.authValid = (devData.authValid == true)
        state.amazonDomain = (String)devData.amazonDomain
        state.regionLocale = (String)devData.regionLocale

        Map permissions = state.permissions ?: [:]
        devData.permissionMap?.each {String k,v -> permissions[k] = v }
        state.permissions = permissions
        state.hasClusterMembers = devData.hasClusterMembers
        state.isWhaDevice = (devData.permissionMap?.isMultiroomDevice == true)
        // log.trace "hasClusterMembers: ${state.hasClusterMembers}"
        // log.trace "permissions: ${state.permissions}"

        Boolean chg=false

        List permissionList = permissions?.findAll { it?.value == true }?.collect { it?.key }
        if(isStateChange(device, "permissions", permissionList?.toString())) {
            sendEvent(name: "permissions", value: permissionList, display: false, displayed: false)
            chg=true
        }

        Map deviceStyle = (Map)devData.deviceStyle
        state.deviceStyle = deviceStyle

        String devFamily = devData.deviceFamily ?: sBLANK
        String devName = (String)deviceStyle?.name

        // logInfo("deviceStyle (${devFamily}): ${devType} | Desc: ${devName}")

        state.remove('deviceImage') //        state.deviceImage = (String)deviceStyle?.image

        if(isStateChange(device, "deviceStyle", devName)) {
            sendEvent(name: "deviceStyle", value: devName, descriptionText: "Device Style is ${devName}", display: true, displayed: true)
            chg=true
        }
        if(isStateChange(device, "firmwareVer", firmwareVer)) {
            sendEvent(name: "firmwareVer", value: firmwareVer, descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
            chg=true
        }
        if(isStateChange(device, "deviceFamily", devFamily)) {
            sendEvent(name: "deviceFamily", value: devFamily, descriptionText: "Echo Device Family is ${devFamily}", display: true, displayed: true)
            chg=true
        }
        if(isStateChange(device, "deviceSerial", devSerial)) {
            sendEvent(name: "deviceSerial", value: devSerial, descriptionText: "Echo Device SerialNumber is ${devSerial}", display: true, displayed: true)
            chg=true
        }
        if(isStateChange(device, "deviceType", devType)) {
            sendEvent(name: "deviceType", value: devType, display: false, displayed: false)
            chg=true
        }

        Map musicProviders = (Map)devData.musicProviders ?: [:]
        String lItems = ""
        musicProviders.each { String k, String v ->
            if(v.size() > 0) lItems = lItems + (lItems.size() > 0 ? ", "+v : v)
        }
//log.warn "musicProviders is $musicProviders   lItems is $lItems"
        if(isStateChange(device, "supportedMusic", lItems)) {
            sendEvent(name: "supportedMusic", value: lItems, display: false, displayed: false)
            chg=true
        }
        // if(devData.guardStatus) { updGuardStatus(devData.guardStatus) }
        if(!isOnline) {
            sendEvent(name: "mute", value: "unmuted")
            sendEvent(name: "status", value: "stopped")
            sendEvent(name: "deviceStatus", value: "stopped_${state.deviceStyle?.image}")
            sendEvent(name: "trackDescription", value: "Not Set")
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

void websocketUpdEvt(List<String> triggers) {
    logTrace("websocketEvt: $triggers")
    if((Boolean) state.isWhaDevice) { return }
    if(triggers?.size()) {
        triggers.each { String k->
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
    if(isZone()) parent?.relayChildInitiatedRefresh() else parent?.childInitiatedRefresh()
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

private void triggerDataRrshF(String src) {
    logTrace("triggerDataRrsh $src $parentRefresh")
    runIn(6, "refreshData2")
}

void refreshData2() {
    refreshData(true)
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

private void refreshStage2() {
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

public void setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
//    if(isStateChange(device, "DeviceWatch-DeviceStatus", onlStatus?.toString())) {
//        sendEvent(name: "DeviceWatch-DeviceStatus", value: onlStatus?.toString(), display: false, displayed: false)
//    }
    if(isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        logDebug("OnlineStatus has changed to (${onlStatus})")
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), display: true, displayed: true)
    }
}

private void getPlaybackState(Boolean isGroupResponse=false) {
    if(isZone()) return
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/np/player",
        query: [ deviceSerialNumber: getEchoSerial(), deviceType: getEchoDeviceType(), screenWidth: 2560, _: now() ],
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map playerInfo = [:]
    try {
        logTrace('getPlaybackState')
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
                updateLevel(level, null)
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
        if(isZone()) parent?.relaySendPlaybackStateToClusterMembers((String)state.serialNumber, playerInfo) else parent?.sendPlaybackStateToClusterMembers((String)state.serialNumber, playerInfo)
    }
}

private getAlarmVolume() {
    if(isZone()) return
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
    if(isZone()) return
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
    if(isZone()) return
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
    if(isZone()) return
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/wake-words-locale",
        query: [ cached: true, _: new Date().getTime(), deviceSerialNumber: getEchoSerial(), deviceType: getEchoDeviceType(), softwareVersion: device.currentValue('firmwareVer') ],
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
    Map btData = !isZone() ? parent?.getBluetoothData((String)state.serialNumber) : [:]
    btData = btData ?: [:]
    String curConnName = btData.curConnName ?: 'None Connected' //(String)null
    Map btObjs = (Map)btData?.btObjs ?: [:]
    // logDebug("Current Bluetooth Device: ${curConnName} | Bluetooth Objects: ${btObjs}")
    state.bluetoothObjs = btObjs
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
    Boolean guardSup = (Boolean)state.permissions?.guardSupported
    String t0 = guardSup ? (val ?: (!isZone() ? parent?.getAlexaGuardStatus() : parent?.relayGetAlexaGuardStatus() ) ) : "Not Supported"
    String gState = t0 ?: "Unknown"
    if(guardSup) {
        //TODO: Guard is location based ?  ie we may be seeing multiple locations, each with different Guard status?
        // get something from the parent to check this device or location's guard status
        // we may really need a new virtual device at each location that is that location's guard status if guard is present at that location
    }
    if(isStateChange(device, "alexaGuardStatus", gState)) {
        sendEvent(name: "alexaGuardStatus", value: gState, display: false, displayed: false)
        logDebug("Alexa Guard Status: (${gState})")
    }
}

private String getBtAddrByAddrOrName(String btNameOrAddr) {
    Map btObj = state.bluetoothObjs
    String curBtAddr = btObj?.find { it?.value?.friendlyName == btNameOrAddr || it?.value?.address == btNameOrAddr }?.key ?: sNULL
    // logDebug("curBtAddr: ${curBtAddr}")
    return curBtAddr
}

private String getBtFirst() {
    Map btObj = state.bluetoothObjs
    String curBtAddr = btObj?.keySet() ? (String)btObj.keySet()[0] : sNULL
    // logDebug("curBtAddr: ${curBtAddr}")
    return curBtAddr
}

private getDoNotDisturb() {
    if(isZone()) return
    Boolean dndEnabled = (Boolean)parent?.getDndEnabled((String)state.serialNumber)
//    logTrace("getDoNotDisturb: $dndEnabled")
//    state.doNotDisturb = dndEnabled
    if(isStateChange(device, "doNotDisturb", dndEnabled?.toString())) {
        logDebug("Do Not Disturb: (${(dndEnabled)})")
        sendEvent(name: "doNotDisturb", value: dndEnabled?.toString(), descriptionText: "Do Not Disturb Enabled ${dndEnabled}", display: true, displayed: true)
    }
}

private getPlaylists() {
    if(isZone()) return
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/cloudplayer/playlists",
        query: [ deviceSerialNumber: getEchoSerial(), deviceType: getEchoDeviceType(), mediaOwnerCustomerId: getEchoOwner(), screenWidth: 2560 ],
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

private List getNotifications(String type="Reminder", all=false) {
    if(isZone()) return null
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
                if(all) s.push("OFF")
                List items = sData.notifications ? sData.notifications.findAll { (it.status in s) && (it.type == type) && it?.deviceSerialNumber == (String)state.serialNumber } : []
                items?.each { item->
                    Map li = [:]
                    item.keySet()?.each { String key-> if(key in ['id', 'reminderLabel', 'originalDate', 'originalTime', 'deviceSerialNumber', 'type', 'remainingDuration', 'status']) { li[key] = item[key] } }
                    newList?.push(li)
                }
            }
            // log.trace "notifications: $newList"
            return newList
        }
    } catch (ex) {
        respExceptionHandler(ex, "getNotifications")
    }
    return null
}

private getDeviceActivity() {
    if(isZone()) return
    try {
        Map actData = parent?.getDeviceActivity((String)state.serialNumber)
        actData = actData ?: null
        Boolean wasLastDevice = (actData != null && (String)actData?.serialNumber == (String)state.serialNumber)
        if(actData != null && wasLastDevice) {
            String spTx = (String)actData.spokenText
            if(spTx) {
                if(isStateChange(device, "lastSpokenToTime", (String)actData.lastSpokenDt)) {
                    sendEvent(name: "lastSpokenToTime", value: (String)actData.lastSpokenDt, display: false, displayed: false)

                    logDebug("wasLastSpokenToDevice: ${wasLastDevice}")
                    sendEvent(name: "wasLastSpokenToDevice", value: wasLastDevice.toString(), display: false, displayed: false, isStateChange: true)

                    logDebug("lastVoiceActivity: ${spTx}")
                    sendEvent(name: "lastVoiceActivity", value: spTx, display: false, displayed: false, isStateChange: true)
                }
            }
        } else if(isStateChange(device, "wasLastSpokenToDevice", wasLastDevice.toString())) {
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
    String myId = parent.getId()
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
    if(isZone()) return
    String t0 = sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/np/command",
        headers: getCookieMap(),
        query: [ deviceSerialNumber: getEchoSerial(), deviceType: getEchoDeviceType() ],
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
                triggerDataRrshF("sendAmazonCommand $method bluetooth")
            } else if(cmdD.startsWith("renameDevice")) { triggerDataRrshF("sendAmazonCommand $method rename") }
        }
        logDebug("sendAmazonCommand | Status: (${rStatus})${rData != null ? " | Response: ${rData}" : sBLANK} | ${cmdD} was Successfully Sent!!!")
        return rData?.id ?: sNULL
    } catch (ex) {
        respExceptionHandler(ex, "${otherData?.cmdDesc}", true)
    }
    return sNULL
}

private Map getDeviceData(){
    return [deviceType: getEchoDeviceType(), serialNumber: getEchoSerial(), owner: getEchoOwner(), account: getEchoAccountId(), dni: device.deviceNetworkId]
//Map getEchoDevInfo(cmd) {
//	return [deviceTypeId: getEchoDeviceType(), deviceSerialNumber: getEchoSerial(), deviceOwnerCustomerId: getEchoOwner(), deviceAccountId: getEchoAccountId(), dni: device.deviceNetworkId ]
}

private void sendSequenceCommand(String type, String command, value=null, String callback=sNULL) {
    // logTrace("sendSequenceCommand($type) | command: $command | value: $value")
    if(isZone()) 
        parent.relaySeqCommand(type, command, value, getDeviceData(), device.deviceNetworkId, callback)
    else
        parent.queueSequenceCommand(type, command, value, getDeviceData(), device.deviceNetworkId, callback)
}

private void sendMultiSequenceCommand(List commands, String srcDesc, Boolean parallel=false, String callback=sNULL) {
    if(isZone())
        parent.relayMultiSeqCommand(commands, srcDesc, parallel, null, device.deviceNetworkId, callback)
    else {
        parent.queueMultiSequenceCommand(commands, srcDesc, parallel, null, device.deviceNetworkId, callback)
    }
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
    if(isZone()) {
        parent.zoneCmdHandler([value: 'playback', jsonData: [zones:[parent.id.toString()], cmd:'play', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
        return
    }
    if(isCommandTypeAllowed("mediaPlayer")) {
        sendAmazonBasicCommand("PlayCommand")
        if(isStateChange(device, "status", "playing")) {
            sendEvent(name: "status", value: "playing", descriptionText: "Player Status is playing", display: true, displayed: true)
            // log.debug "deviceStatus: playing_${state.deviceStyle?.image}"
            sendEvent(name: "deviceStatus", value: "playing_${state.deviceStyle?.image}", display: false, displayed: false)
        }
        return
    }
    logWarn("Uh-Oh... The play Command is NOT Supported by this Device!!!")
}

// capability audioNotification
def playTrack(String uri, volume=null) {
    if(isCommandTypeAllowed("mediaPlayer")) {
        logDebug("playTrack($uri, $volume) | Attempting to play on CLOUDPLAYER...")
        String nuri = uriTrackParser(uri)
        doSearchMusicCmd(nuri, "CLOUDPLAYER", volume)
        return
    }
    logWarn("Uh-Oh... The playTrack($uri, $volume) Command is NOT Supported by this Device!!!")
}

// capability musicPlayer
/*def playTrack(String uri) {
    if(isCommandTypeAllowed("TTS")) {
        String tts = uriTrackParser(uri)
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
    if(isZone()) {
        parent.zoneCmdHandler([value: 'playback', jsonData: [zones:[parent.id.toString()], cmd:'pause', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
    } else {
        if(isCommandTypeAllowed("mediaPlayer")) {
            sendAmazonBasicCommand("PauseCommand")
            if(isStateChange(device, "status", "stopped")) {
                sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
                // log.debug "deviceStatus: stopped_${state.deviceStyle?.image}"
                sendEvent(name: "deviceStatus", value: "stopped_${state.deviceStyle?.image}", display: false, displayed: false)
            }
            return
        }
        logWarn("Uh-Oh... The pause Command is NOT Supported by this Device!!!")
    }
}

// capability musicPlayer
def stop() {
    logTrace("stop() command received...")
    pause()
}

def togglePlayback() {
    logTrace("togglePlayback() command received...")
    if(isZone()) {
        parent.zoneCmdHandler([value: 'playback', jsonData: [zones:[parent.id.toString()], cmd:'togglePlayback', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
    } else {
        if(isCommandTypeAllowed("mediaPlayer")) {
            def isPlaying = (device?.currentValue('status') == "playing")
            if(isPlaying) {
                stop()
            } else {
                play()
            }
        }
    }
}

def stopAllDevices() {
    if(isZone()) {
        logWarn("Uh-Oh... The stopAllDevices() Command is NOT Supported by this Device!!!", true)
        return
    }
    sendSequenceCommand("StopAllDevicesCommand", "stopalldevices")
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
        Integer t0= device?.currentValue("level")?.toInteger()
        if( (t0 && t0 != 0) ) state.muteLevel = t0
        if(isStateChange(device, "mute", "muted")) {
            sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
        }
        if(isZone()) {
            parent.zoneCmdHandler([value: 'mute', jsonData: [zones:[parent.id.toString()], cmd:'mute', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
            updateLevel(0, null)
        } else {
            setLevel(0)
        }
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
            if(isZone()) {
                parent.zoneCmdHandler([value: 'unmute', jsonData: [zones:[parent.id.toString()], cmd:'unmute', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
                updateLevel(state.muteLevel, null)
            } else {
                setLevel(state.muteLevel)
            }
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
        if(isZone()) {
            parent.zoneCmdHandler([value: 'volume', jsonData: [zones:[parent.id.toString()], cmd:'setLevel', message: sNULL, changeVol:level, restoreVol:null, delay:0]])
        } else {
            if(level != device?.currentValue('level')) {
                sendSequenceCommand("VolumeCommand", "volume", level)
            }
        }
        updateLevel(level.toInteger(), null)
    }
}

def setAlarmVolume(vol) {
    logTrace("setAlarmVolume($vol) command received...")
    if(isZone()) {
        parent.zoneCmdHandler([value: 'alarmvolume', jsonData: [zones:[parent.id.toString()], cmd:'setAlarmVolume', message: sNULL, changeVol:vol, restoreVol:null, delay:0]])
    } else {
        if(isCommandTypeAllowed("alarms") && vol>=0 && vol<=100) {
            String t0 = sendAmazonCommand("PUT", [
                uri: getAmazonUrl(),
                path: "/api/device-notification-state/${(String)state.deviceType}/${state?.softwareVersion}/${(String)state.serialNumber}",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                body: [
                    deviceSerialNumber: getEchoSerial(),
                    deviceType: getEchoDeviceType(),
                    softwareVersion: device?.currentValue('firmwareVer'),
                    volumeLevel: vol
                ]
            ], [cmdDesc: "AlarmVolume"])
            sendEvent(name: "alarmVolume", value: vol, display: true, displayed: true)
        }
    }
}

// capability audioVolume
def setVolume(vol) {
    if(vol) { setLevel(vol.toInteger()) }
}

// capability audioVolume
def volumeUp() {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'volume', jsonData: [zones:[parent.id.toString()], cmd:'volumeUp', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
    } else {
        def t0 = device?.currentValue('level')
        def curVol = t0 ?: 1
        if(curVol >= 0 && curVol <= 95) { setVolume(curVol.toInteger()+5) }
        else if(t0 > 95) setVolume(100)
    }
}

// capability audioVolume
def volumeDown() {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'volume', jsonData: [zones:[parent.id.toString()], cmd:'volumeDown', message: sNULL, changeVol:null, restoreVol:null, delay:0]])
    } else {
        def t0 = device?.currentValue('level')
        def curVol = t0 ?: 1
        if(curVol >= 5) { setVolume(curVol.toInteger()-5) }
        else if(t0 > 0) setVolume(0)
    }
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
        if(!isZone()) {
            String t0 = sendAmazonCommand("PUT", [
                uri: getAmazonUrl(),
                path: "/api/dnd/status",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                body: [
                    deviceSerialNumber: getEchoSerial(),
                    deviceType: getEchoDeviceType(),
                    enabled: val
                ]
            ], [cmdDesc: "SetDoNotDisturb${val ? "On" : "Off"}"])
            sendEvent(name: "doNotDisturb", value: val.toString(), descriptionText: "Do Not Disturb Enabled ${val}", display: true, displayed: true)
            parent?.getDoNotDisturb()
        }
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
                deviceSerialNumber: getEchoSerial(),
                deviceType: getEchoDeviceType(),
                deviceAccountId: (String)state.deviceAccountId,
                goldfishEnabled: val
            ]
        ], [cmdDesc: "setFollowUpMode${val ? "On" : "Off"}"])
    }
}

def deviceNotification(String msg) {
    logTrace("deviceNotification(msg: $msg) command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with deviceNotification($msg) command", true); return }
        // logTrace("deviceNotification(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
        if((Boolean)settings.sendDevNotifAsAnnouncement) { playAnnouncement(msg) } else { speak(msg) }
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
    if(msg) {
        if(volume != null && permissionOk("volumeControl")) {
            state.newVolume = volume?.toInteger()
            if(restVolume != null) {
                state.oldVolume = restVolume as Integer
            } else {
                state.oldVolume = null // clear out any junk
                Boolean stored = mstoreCurrentVolume()
            }
        }
        speak(msg)
    }
}

def storeCurrentVolume() {
    Boolean a= mstoreCurrentVolume(true)
}

Boolean mstoreCurrentVolume(Boolean user=false) {
    Integer t0 = device?.currentValue("level")
    Integer curVol = t0 //  ?: 1
    String msg = "storeCurrentVolume($user, $curVol)"
    if(curVol != null) {
        if(user) state.svVolume = curVol
        else state.oldVolume = curVol
        logTrace(msg+" received...")
        return true
    }
    logWarn(msg+" failed...", true)
    return false
}

public restoreLastVolume() {
    Integer lastVol = state.svVolume
    String msg = "restoreLastVolume($lastVol)"
    if(lastVol && permissionOk("volumeControl")) {
        logTrace(msg+" received...")
        setVolume(lastVol as Integer)
    } else { logWarn(msg+" previous value not found...", true) }
}

void seqHelper_a(String cmd, String val, String cmdType, volume, restoreVolume) {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'builtin', jsonData: [zones:[parent.id.toString()], cmd:cmdType, message: sNULL, changeVol:volume, restoreVol:restoreVolume, delay:0]])
        updateLevel(restoreVolume, volume)
    } else {
        if(volume != null) {
            List seqs = [[command: "volume", value: volume, deviceData: getDeviceData()], [command: cmd, cmdType: cmdType, value: val, deviceData: getDeviceData()]]
            if(restoreVolume != null) { seqs.push([command: "volume", value: restoreVolume, deviceData: getDeviceData()]) }
            sendMultiSequenceCommand(seqs, cmdType)
            updateLevel(restoreVolume, volume)
        } else { sendSequenceCommand(cmdType, cmd, val) }
    }
}

void seqHelper_c(String val, String cmdType, volume, restoreVolume){
    seqHelper_a("cannedtts_random", val, cmdType, volume, restoreVolume)
}

def sayWelcomeHome(volume=null, restoreVolume=null) {
    seqHelper_c("iamhome", "sayWelcomeHome", volume, restoreVolume)
}

def sayCompliment(volume=null, restoreVolume=null) {
    seqHelper_c("compliments", "sayCompliment", volume, restoreVolume)
}

def sayBirthday(volume=null, restoreVolume=null) {
    seqHelper_c("birthday", "sayBirthday", volume, restoreVolume)
}

def sayGoodNight(volume=null, restoreVolume=null) {
    seqHelper_c("goodnight", "sayGoodNight", volume, restoreVolume)
}

def sayGoodMorning(volume=null, restoreVolume=null) {
    seqHelper_c("goodmorning", "sayGoodMorning", volume, restoreVolume)
}

def sayGoodbye(volume=null, restoreVolume=null) {
    seqHelper_c("goodbye", "sayGoodBye", volume, restoreVolume)
}

def executeRoutineId(String rId) {
    def execDt = now()
    logTrace("executeRoutineId($rId) command received...")
    if(!rId) { logWarn("No Routine ID sent with executeRoutineId($rId) command", true); return }
    if(parent?.executeRoutineById(rId)) {
        logDebug("Executed Alexa Routine | Process Time: (${(now()-execDt)}ms) | RoutineId: ${rId}")
    }
}

void seqHelper_s(String cmd, String cmdType, volume, restoreVolume){
    if(isZone()) {
        parent.zoneCmdHandler([value: 'builtin', jsonData: [zones:[parent.id.toString()], cmd:cmdType, message: sNULL, changeVol:volume, restoreVol:restoreVolume, delay:0]])
        updateLevel(restoreVolume, volume)
    } else {
        if(volume != null) {
            List seqs = [[command: "volume", value: volume, deviceData: getDeviceData()], [command: cmd, cmdType: cmdType, deviceData: getDeviceData()]]
            if(restoreVolume != null) { seqs.push([command: "volume", value: restoreVolume, deviceData: getDeviceData()]) }
            sendMultiSequenceCommand(seqs, cmdType)
            updateLevel(restoreVolume, volume)
        } else { sendSequenceCommand(cmdType, cmd) }
    }
}

def playWeather(volume=null, restoreVolume=null) {
    seqHelper_s("weather", "playWeather", volume, restoreVolume)
}

def playTraffic(volume=null, restoreVolume=null) {
    seqHelper_s("traffic", "playTraffic", volume, restoreVolume)
}

def playSingASong(volume=null, restoreVolume=null) {
    seqHelper_s("singasong", "playSingASong", volume, restoreVolume)
}

def playFlashBrief(volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("flashBriefing")) {
        seqHelper_s("flashbriefing", "playFlashBrief", volume, restoreVolume)
    }
}

def playGoodNews(volume=null, restoreVolume=null) {
    if(isCommandTypeAllowed("flashBriefing")) {
        seqHelper_s("goodnews", "playGoodNews", volume, restoreVolume)
    }
}

def playTellStory(volume=null, restoreVolume=null) {
    seqHelper_s("tellstory", "playTellStory", volume, restoreVolume)
}

def playFunFact(volume=null, restoreVolume=null) {
    seqHelper_s("funfact", "playFunFact", volume, restoreVolume)
}

def playJoke(volume=null, restoreVolume=null) {
    seqHelper_s("joke", "playJoke", volume, restoreVolume)
}

def playCalendarToday(volume=null, restoreVolume=null) {
    seqHelper_s("calendartoday", "playCalendarToday", volume, restoreVolume)
}

def playCalendarTomorrow(volume=null, restoreVolume=null) {
    seqHelper_s("calendartomorrow", "playCalendarTomorrow", volume, restoreVolume)
}

def playCalendarNext(volume=null, restoreVolume=null) {
    seqHelper_s("calendarnext", "playCalendarNext", volume, restoreVolume)
}

def playCannedRandomTts(String type, volume=null, restoreVolume=null) {
    seqHelper_c(type, "playCannedRandomTts($type)", volume, restoreVolume)
}

def playSoundByName(String name, volume=null, restoreVolume=null) {
    log.debug "sound name: ${name}"
    seqHelper_a("sound", name, "playSoundByName($name)", volume, restoreVolume)
}

def playAnnouncement(String msg, volume=null, restoreVolume=null) {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'announcement', jsonData: [zones:[parent.id.toString()], cmd:'playAnnouncement', message: msg, title: sNULL, changeVol:volume, restoreVol:restoreVolume, delay:0]])
        finishAnnounce(msg, volume, restoreVolume)
    } else {
        if(isCommandTypeAllowed("announce")) {
            seqHelper_a("announcement", msg, "playAnnouncement", volume, restoreVolume)
            finishAnnounce(msg, volume, restoreVolume)
        }
    }
}

def finishAnnounce(String msg, volume, restoreVolume){ 
    sendEvent(name: "lastAnnouncement", value: msg, display: false, displayed: false, isStateChange:true)
    updateLevel(restoreVolume, volume)
}

def playAnnouncement(String msg, String title, volume=null, restoreVolume=null) {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'announcement', jsonData: [zones:[parent.id.toString()], cmd:'playAnnouncement', message: msg, title: title, changeVol:volume, restoreVol:restoreVolume, delay:0]])
        String newMsg= "${title ? "${title}::" : sBLANK}${msg}".toString()
        finishAnnounce(newMsg, volume, restoreVolume)
    } else {
        String newMsg= "${title ? "${title}::" : sBLANK}${msg}".toString()
        playAnnouncement(newMsg, volume, restoreVolume)
    }
}

def sendAnnouncementToDevices(String msg, String title=sNULL, List devObj, volume=null, restoreVolume=null) {
    // log.debug "sendAnnouncementToDevices(msg: $msg, title: $title, devObj: $devObj, volume: $volume, restoreVolume: $restoreVolume)"
    if(isCommandTypeAllowed("announce") && devObj) {
        String devJson = new groovy.json.JsonOutput().toJson(devObj)
// TODO check if message is too big
        String newmsg = "${title ?: "Echo Speaks"}::${msg}::${devJson}"
        // log.debug "sendAnnouncementToDevices | msg: ${newmsg}"
        if(volume != null) {
            List mainSeq = []
            devObj.each { dev-> mainSeq.push([command: "volume", value: volume, deviceData: getDeviceData()]) }
            mainSeq.push([command: "announcement_devices", value: newmsg, cmdType: 'playAnnouncement', deviceData: getDeviceData()])

            //sendMultiSequenceCommand(mainSeq, "sendAnnouncementToDevices-VolumeSet",true)
            sendMultiSequenceCommand(mainSeq, "sendAnnouncementToDevices-VolumeSet")

            if(restoreVolume!=null) {
                List amainSeq = []
                devObj.each { dev-> amainSeq.push([command: "volume", value: restoreVolume, deviceData: getDeviceData()]) }
                sendMultiSequenceCommand(amainSeq, "sendAnnouncementToDevices-VolumeRestore")
            }
            updateLevel(restoreVolume, volume)
            // log.debug "mainSeq: $mainSeq"
        } else { sendSequenceCommand("sendAnnouncementToDevices", "announcement_devices", newmsg) }
    }
}

def voiceCmdAsText(String cmd) {
    // log.trace "voiceCmdAsText($cmd)"
    if(cmd) {
        if(isZone()) {
            parent.zoneCmdHandler([value: 'voicecmd', jsonData: [zones:[parent.id.toString()], cmd:'voiceCmdAsText', message: cmd, title: sNULL, changeVol:null, restoreVol:null, delay:0]])
        } else {
            sendSequenceCommand("voiceCmdAsText", "voicecmdtxt", cmd)
        }
    }
}

public playAnnouncementAll(String msg, String title=sNULL) {
    if(isZone()) {
        parent.zoneCmdHandler([value: 'announcement', jsonData: [zones:[parent.id.toString()], cmd:'playAnnouncementAll', message: msg, title: sNULL, changeVol:null, restoreVol:null, delay:0]])
    } else {
    // if(isCommandTypeAllowed("announce")) {
        msg = title ? title+"::"+msg : msg
        sendSequenceCommand("playAnnouncementAll", "announcementall", msg)
    // }
    }
    msg = title ? title+"::"+msg : msg
    finishAnnounce(msg, null, null)
}

def searchMusic(String searchPhrase, String providerId, volume=null, sleepSeconds=null) {
    logDebug("searchMusic(${searchPhrase}, ${providerId})")
    if(isZone()) { return }
    if(isCommandTypeAllowed("mediaPlayer")) {
        String a = getCommandTypeForProvider(providerId)
        if(isCommandTypeAllowed(a) || a == "CLOUDPLAYER") {
            doSearchMusicCmd(searchPhrase, providerId, volume, sleepSeconds)
            return
        }
    }
    logWarn("searchMusic not supported for ${providerId}", true)
}

static String getCommandTypeForProvider(String providerId) {
    String commandType = providerId
    // logDebug("getCommandTypeForProvider(${providerId})")
    switch (providerId) {
        case "CLOUDPLAYER":
            break
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

        //doSearchMusicCmd(nuri, "CLOUDPLAYER", volume)
private void doSearchMusicCmd(String searchPhrase, String musicProvId, volume=null, sleepSeconds=null) {
    if((String)state.serialNumber && searchPhrase && musicProvId) { 
        playMusicProvider(searchPhrase, musicProvId, volume, sleepSeconds)
    } else { logWarn("doSearchMusicCmd Error | You are missing one of the following... SerialNumber: ${(String)state.serialNumber} | searchPhrase: ${searchPhrase} | musicProvider: ${musicProvId}", true) }
}

private Map validateMusicSearch(String searchPhrase, String providerId, sleepSeconds=null) {
    Map validObj = [
        type: "Alexa.Music.PlaySearchPhrase",
        operationPayload: [
            deviceType: getEchoDeviceType(),
            deviceSerialNumber: getEchoSerial(),
            customerId: getEchoOwner(),
            locale: ((String)state.regionLocale ?: "en-US"),
            musicProviderId: providerId,
            searchPhrase: searchPhrase
        ]
    ]
    if(sleepSeconds) { validObj.operationPayload.waitTimeInSeconds = sleepSeconds }
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

private Map getMusicSearchObj(String searchPhrase, String providerId, sleepSeconds=null) {
    if (searchPhrase == sBLANK) { logError("getMusicSearchObj Searchphrase empty"); return null }
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

private void playMusicProvider(String searchPhrase, String providerId, volume=null, sleepSeconds=null) {
    logTrace("playMusicProvider() command received... | searchPhrase: $searchPhrase | providerId: $providerId | sleepSeconds: $sleepSeconds")
    Map validObj = getMusicSearchObj(searchPhrase, providerId, sleepSeconds)
    if(!validObj) { return }
//    Map seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": validObj]
//    seqJson?.startNode["@type"] = "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode"
    List seqList = []
    if(volume) seqList.push([command: "volume", value: volume])
     seqList.push([command: validObj])
     sendMultiSequenceCommand(seqList, "playMusicProvider(${providerId})", true)
//    } else { sendSequenceCommand("playMusicProvider(${providerId})", seqJson, null) }
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
                deviceSerialNumber: getEchoSerial(),
                deviceType: getEchoDeviceType(),
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
            label: alarmLbl?.replaceAll(" ", sBLANK),
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
                label: remLbl,
                date: remDate,
                time: remTime,
                type: "Reminder"
            ])
        } else { logWarn("createReminder is Missing the Required (id) Parameter!!!", true) }
    }
}

def createReminderNew(String remLbl, String remDate, String remTime, String recurType=sNULL, String recurOpt=sNULL) {
    logTrace("createReminderNew($remLbl, $remDate, $remTime, $recurType, $recurOpt) command received...")
    if(isCommandTypeAllowed("alarms")) {
        if(remLbl && remDate && remTime) {
            createNotification("Reminder", [
                cmdType: "CreateReminder",
                label: remLbl,
                date: remDate,
                time: remTime,
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
        List items = getNotifications(type, true)
        if(items?.size()) {
            items.each { item->
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
        state.remove("createdNotifications")
    }
}

private static String generateNotificationKey(String id) {
    return id?.toString()?.replaceAll(" ", sBLANK)
}

//TODO: CreateReminderInXMinutes()
//TODO: RemoveAllReminders() //Remove all Reminders for this device
//TODO: RemoveAllAlarms() //Remove all Alarms for this device
//TODO: Add Recurrence Options to Alarms and Reminders

private createNotification(String type, Map opts) {
    logTrace("createdNotification params: ${opts}")
    String notifKey = generateNotificationKey((String)opts.label)
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
    def alarmDate = isoFormat.parse("${(String)opts.date}T${(String)opts.time}")
    Long alarmTime = (Long)alarmDate.getTime()
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
            deviceSerialNumber: getEchoSerial(),
            deviceType: getEchoDeviceType(),
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
    Map rule = transormRecurString((String)opts?.recur_type, (String)opts?.recur_opt, (String)opts?.time, (String)opts?.date)
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
private transormRecurString(String type, String opt, String tm, String dt) {
    logTrace("transormRecurString(type: ${type}, opt: ${opt}, time: ${tm}, date: ${dt})")
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
            rd?.nextTriggerTimes = ["${parseFmtDt("HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "${time[0]}:${time[1]}")}"]
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

def renameDevice(String newName) {
    logTrace("renameDevice($newName) command received...")
    if(!(String)state.deviceAccountId) { logError("renameDevice Failed because deviceAccountId is not found..."); return }
    String t0 = sendAmazonCommand("PUT", [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device/${(String)state.serialNumber}",
        headers: getCookieMap(true),
        contentType: sAPPJSON,
        timeout: 20,
        body: [
            serialNumber: getEchoSerial(),
            deviceType: getEchoDeviceType(),
            deviceAccountId: (String)state.deviceAccountId,
            accountName: newName
        ]
    ], [cmdDesc: "renameDevice(${newName})"])
}

def connectBluetooth(String btNameOrAddr) {
    logTrace("connectBluetooth(${btNameOrAddr}) command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String curBtAddr = btNameOrAddr ? getBtAddrByAddrOrName(btNameOrAddr) : getBtFirst()
        if(curBtAddr) {
            String t0 = sendAmazonCommand("POST", [
                uri: getAmazonUrl(),
                path: "/api/bluetooth/pair-sink/${(String)state.deviceType}/${(String)state.serialNumber}",
                headers: getCookieMap(true),
                contentType: sAPPJSON,
                timeout: 20,
                body: [ bluetoothDeviceAddress: curBtAddr ]
            ], [cmdDesc: "connectBluetooth($btNameOrAddr)"])
// above command will refresh bt data in 6-10 seconds
//            sendEvent(name: "btDeviceConnected", value: btNameOrAddr, display: true, displayed: true)
        } else { logError("ConnectBluetooth Error: Unable to find the connected bluetooth device address...") }
    }
}

def disconnectBluetooth() {
    logTrace("disconnectBluetooth() command received...")
    if(isCommandTypeAllowed("bluetoothControl")) {
        String devC = device?.currentValue("btDeviceConnected")
        String curBtAddr = devC != 'None Connected' ? getBtAddrByAddrOrName(devC) : sNULL
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
        String curBtAddr = btNameOrAddr ? getBtAddrByAddrOrName(btNameOrAddr) : sNULL
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
    sendSequenceCommand("AlexaAppNotification", "pushnotification", text)
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
    if(isCommandTypeAllowed("mediaPlayer")) {
        logDebug("playTrackAndResume($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
        String nuri = uriTrackParser(uri)
        doSearchMusicCmd(nuri, "CLOUDPLAYER", volume)
        return
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
    if(isCommandTypeAllowed("mediaPlayer")) {
        logDebug("playTrackAndRestore($uri, $volume) | Attempting to parse out message from trackUri.  This might not work in all scenarios...")
        String nuri = uriTrackParser(uri)
        doSearchMusicCmd(nuri, "CLOUDPLAYER", volume)
        return
    }
    logWarn("Uh-Oh... The playTrackAndRestore(uri: $uri, volume: $volume) Command is NOT Supported by this Device!!!")
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
        String tts = uriTrackParser(uri)
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
String uriTrackParser(String uri) {
    // Thanks @fkrlaframboise for this idea.  It never for one second occurred to me to parse out the trackUri...
    // log.debug "uri: $uri"
    if (uri?.toString()?.contains("/")) {
        Integer sInd = uri?.lastIndexOf("/") + 1
        uri = uri?.substring(sInd, uri?.size())?.toLowerCase()?.replaceAll("_", " ")?.replace(".mp3", sBLANK)
        logDebug("uriTrackParser | tts: $uri")
        return uri
    }
    return uri
}

void speechTest(String ttsMsg=sNULL) {
    List<String> items = [
        """<speak><voice name="Joey">Presenting, <break time="500ms"/>Alfred!</voice><audio src="soundbank://soundlibrary/musical/amzn_sfx_trumpet_bugle_03"/></speak>""",
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

void speak(String msg, Integer volume=null, String awsPollyVoiceName = sNULL) {
    logTrace("speak() command received...")
    if(isCommandTypeAllowed("TTS")) {
        if(!msg) { logWarn("No Message sent with speak($msg) command", true) }
        else {
            def newvol = volume ?: (state.newVolume ?: null)
            def restvol = state.oldVolume ?: null

            if(isZone()) {
                parent.zoneCmdHandler([value: 'speak', jsonData: [zones:[parent.id.toString()], cmd:'speak', message: msg, changeVol: newvol, restoreVol: restvol, delay:0]])
                String t0 = getDtNow()
                String lastMsg = msg ?: "Nothing to Show Here..."
                sendEvent(name: "lastSpeakCmd", value: lastMsg, descriptionText: "Last Text Spoken: ${lastMsg}", display: true, displayed: true, isStateChange:true)
                sendEvent(name: "lastCmdSentDt", value: t0, descriptionText: "Last Command Timestamp: ${t0}", display: false, displayed: false)
                updateLevel(restvol, newvol)
                logSpeech(msg, 200, sNULL)
            } else {
                speechCmd([cmdDesc: "SpeakCommand", message: msg, newVolume: newvol, oldVolume: restvol, cmdDt: now()])
            }
        }
    } else {
        logWarn("Uh-Oh... The speak($msg) Command is NOT Supported by this Device!!!")
    }
    state.newVolume = null
    state.oldVolume = null
}

void updateLevel(oldvolume, newvolume) {
    if(oldvolume != null || newvolume != null) {
        sendEvent(name: "level", value: (oldvolume != null  ? oldvolume.toInteger() : newvolume.toInteger()), display: true, displayed: true)
        sendEvent(name: "volume", value: (oldvolume != null  ? oldvolume.toInteger() : newvolume.toInteger()), display: true, displayed: true)
    }
}

private void speechCmd(Map cmdMap=[:], Boolean isQueueCmd=true) {

    if(!cmdMap) { logError("speechCmd | Error | cmdMap is missing"); return }
    String healthStatus = getHealthStatus()
    if(!(healthStatus in ["ACTIVE", "ONLINE"])) { logWarn("speechCmd Ignored... Device is current in OFFLINE State", true); return }

    if(settings.logTrace){
        String tr = "speechCmd (${cmdMap.cmdDesc}) | Msg: ${cmdMap.message}"
        tr += cmdMap.newVolume ? " | SetVolume: (${cmdMap.newVolume})" :sBLANK
        tr += cmdMap.oldVolume ? " | Restore Volume: (${cmdMap.oldVolume})" :sBLANK
        tr += cmdMap.msgDelay  ? " | Expected runtime: (${(Integer)cmdMap.msgDelay})" :sBLANK
        tr += cmdMap.cmdDt     ? " | CmdDt: (${cmdMap.cmdDt})" :sBLANK
        logTrace("${tr}")
    }

//    Integer msgLen = ((String)cmdMap.message)?.length()
    Random random = new Random()
    Integer randCmdId = random.nextInt(300)
    cmdMap["cmdId"] = randCmdId
    cmdMap = cmdMap + getDeviceData()
//    cmdMap["serialNumber"] = getEchoSerial()
//    cmdMap["deviceType"] = getEchoDeviceType()
//    cmdMap["owner"] = getEchoOwner()

    parent.sendSpeak(cmdMap, getDeviceData(), device.deviceNetworkId, "finishSendSpeak")
}

def sendTestAnnouncement() {
    playAnnouncement("Echo Speaks announcement test on ${device?.label?.replace("Echo - ", sBLANK)}")
}

def sendTestAnnouncementAll() {
    playAnnouncementAll("Echo Speaks announcement test on All devices")
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
            seqList.each {
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
                        if(!isCommandTypeAllowed((String)seqItemsAvailFLD.musicAlt[cmd])) { logError("Current Music Sequence command ($cmd) not allowed... "); return }
                        if (!valObj || valObj[0] == sBLANK) { logError("Play Music Sequence it Searchphrase empty"); return }
                        Map validObj = getMusicSearchObj(valObj[0], provID, valObj[1] ?: null)
                        if(!validObj) { return }
                        seqItems?.push([command: validObj])
                    } else {
                        if(li.size() == 1) {
                            seqItems.push([command: cmd, deviceData: getDeviceData()])
                        } else if(li.size() == 2) {
                            seqItems.push([command: cmd, value: li[1]?.trim(), deviceData: getDeviceData()])
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

/*Integer getRecheckDelay(Integer msgLen=null, Boolean addRandom=false) {
    if(!msgLen) { return 30 }
    Integer twd = ttsWordDelay ? ttsWordDelay?.toInteger() : 2
    Integer v = (msgLen <= 14 ? 1 : (msgLen / 14)) * twd
    Integer res=v
    Integer randomInt
    if(addRandom){
        def random = new Random()
        randomInt = random?.nextInt(5) //Was using 7
        res=v + randomInt
    }
    logTrace("getRecheckDelay($msgLen) | res:$res | twd: $twd | delay: $v ${addRandom ? '+ '+randomInt.toString() : sBLANK}")
    return res //+2
} */


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
    "lastQueueMsg", "lastTtsMsg",
    "lastTtsCmdDt",
    "q_blocked",
    "q_cmdCycleCnt",
    "q_lastCheckDt",
    "q_loopChkCnt",
    "q_speakingNow",
    "q_cmdWorking",
    "q_firstCmdFlag",
    "q_recheckScheduled",
    "q_cmdIndexNum",
    "q_curMsgLen",
    "q_lastTtsCmdDelay",
    "q_lastTtsMsg",
    "q_lastMsg",
    "doNotDisturb"
]

private void stateCleanup() {
    state.newVolume = null
    state.oldVolume = null
//    if(state.lastVolume) { state?.oldVolume = state?.lastVolume }
    clnItemsFLD.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
}

void resetQueue(String src=sBLANK) {
    logTrace("resetQueue($src)")
    Map cmdQueue = ((Map<String,Object>)state).findAll { it?.key?.toString()?.startsWith("qItem_") }
    cmdQueue.each { cmdKey, cmdData -> state.remove(cmdKey) }
    unschedule("queueCheck")
    unschedule("checkQueue")
/*    state.q_blocked = false
    state.q_cmdCycleCnt = null */
    state.newVolume = null
/*    state.q_lastCheckDt = sNULL
    state.q_loopChkCnt = null
    state.q_speakingNow = false
    state.q_cmdWorking = false
    state.q_firstCmdFlag = false
    state.q_recheckScheduled = false
    state.q_cmdIndexNum = null
    state.q_curMsgLen = null
    state.q_lastTtsCmdDelay = null
    state.q_lastTtsMsg = null
    state.q_lastMsg = null */
}

String getAmazonDomain() { return (String)state.amazonDomain ?: (String)parent?.settings?.amazonDomain } // does this work for parent call on HE?
String getAmazonUrl() {return "https://alexa.${getAmazonDomain()}".toString() }
private void queueCheck(data) {
    return
}

void processCmdQueue() {
}

def testMultiCmd() {
    sendMultiSequenceCommand(
        [
           [command: "volume", value: 60, deviceData: getDeviceData()], 
           [command: "speak", value: "super duper test message 1, 2, 3", deviceData: getDeviceData()],
           [command: "volume", value: 30, deviceData: getDeviceData()]
        ], "testMultiCmd")
}

def finishSendSpeak(Map resp, Integer statusCode, Map data){
    postCmdProcess(resp, statusCode, data)
}

private void postCmdProcess(Map resp, Integer statusCode, Map data) {
    if(data && data.deviceId && (data.deviceId == device?.getDeviceNetworkId())) {
        String respMsg = resp?.message ?: sNULL
        String respMsgLow = respMsg ? respMsg?.toLowerCase() : sNULL
        if(statusCode == 200) {
            Long execTime = (Long)data.cmdDt ? (now()-(Long)data.cmdDt) : 0L
            if ((Boolean)settings.logDebug) {
                String pi = data.cmdDesc ?: "Command"
                pi += data.isSSML ? " (SSML)" :sBLANK
                pi += " Sent"
                pi += " | (${data.message})"
                pi += data.msgLen ? " | Length: (${data.msgLen}) " :sBLANK
                pi += data.msgDelay ? " | Expected Runtime: (${(Integer)data.msgDelay} sec)" :sBLANK
                pi += execTime ? " | Execution Time: (${execTime}ms)" : sBLANK
                pi += data.amznReqId ? " | Amazon Request ID: ${data.amznReqId}" :sBLANK
                logDebug(pi)
            }

            String lastMsg = (String)data?.message ?: "Nothing to Show Here..."
            if((String)data?.cmdDesc == "SpeakCommand") {
                String t0 = getDtNow()
                sendEvent(name: "lastSpeakCmd", value: lastMsg, descriptionText: "Last Text Spoken: ${lastMsg}", display: true, displayed: true, isStateChange:true)
                sendEvent(name: "lastCmdSentDt", value: t0, descriptionText: "Last Command Timestamp: ${t0}", display: false, displayed: false)
                updateLevel((Integer)data.oldVolume, (Integer)data.newVolume)
                logSpeech(lastMsg, statusCode, sNULL)
            }
        } else if((statusCode?.toInteger() in [400, 429]) && respMsgLow && (respMsgLow in ["rate exceeded", "too many requests"])) {
            switch(respMsgLow) {
                case "rate exceeded":
                    Integer rDelay = 3
                    logWarn("You've been rate-limited by Amazon for sending too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    break
                case "too many requests":
                    Integer rDelay = 5
                    logWarn("You've sent too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    break
            }
            logSpeech((String)data?.message, statusCode, respMsg)
        } else {
            logError("postCmdProcess Error | status: ${statusCode} | Msg: ${respMsg}")
            logSpeech((String)data?.message, statusCode, respMsg)
        }
    }
}

/*****************************************************
                HELPER FUNCTIONS
******************************************************/
//static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/icons/$imgName" }
static Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }
Boolean minVersionFailed() {
    try {
        Integer t0 = isZone() ? ((Map<String,Integer>)parent?.relayMinVersions())["zoneEchoDevice"] : ((Map<String,Integer>)parent?.minVersions())["echoDevice"]
        Integer minDevVer = t0 ?: null
        return minDevVer != null && versionStr2Int(devVersionFLD) < minDevVer
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
    return tf.format(new Date())
}

String  formatDt(Date dt, Boolean mdy = true) {
    String formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
    def tf = new java.text.SimpleDateFormat(formatVal)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(dt)
}

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

String parseFmtDt(String parseFmt, String newFmt, String dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(newDt)
}

Boolean ok2Notify() {
    return (Boolean)parent?.getOk2Notify()
}

private void logSpeech(String msg, Integer status, String error=sNULL) {
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

public Map getLogConfigs() {
    return [
        info: (Boolean) settings.logInfo,
        warn: (Boolean) settings.logWarn,
        error: (Boolean) settings.logError,
        debug: (Boolean) settings.logDebug,
        trace: (Boolean) settings.logTrace,
    ]
}

public void enableDebugLog() { device.updateSetting("logDebug",[value:sTRUE,type:"bool"]); logInfo("Debug Logs Enabled From Main App...") }
public void disableDebugLog() { device.updateSetting("logDebug",[value:sFALSE,type:"bool"]); logInfo("Debug Logs Disabled From Main App...") }
public void enableTraceLog() { device.updateSetting("logTrace",[value:sTRUE,type:"bool"]); logInfo("Trace Logs Enabled From Main App...") }
public void disableTraceLog() { device.updateSetting("logTrace",[value:sFALSE,type:"bool"]); logInfo("Trace Logs Disabled From Main App...") }

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn logPrefix(sSPACE + msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15) } }
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error logPrefix(msg, sCLRRED)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (e) {
        }
        if(a) log.error logPrefix(a, sCLRRED)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, null, 15) }
}

static String logPrefix(String msg, String color = sNULL) {
    return span("Echo (v" + devVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

Map getLogHistory() {
    return [ warnings: getMemStoreItem("warnHistory") ?: [], errors: getMemStoreItem("errorHistory") ?: [], speech: getMemStoreItem("speechHistory") ?: [] ]
}

public void clearLogHistory() {
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

static String getObjType(obj) {
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
