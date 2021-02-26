/**
 *	Echo Speaks Zone Device (Hubitat ONLY)
 *
 *  Copyright 2021 Anthony Santilli
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
@Field static final String devVersionFLD  = "4.0.7.0"
@Field static final String appModifiedFLD = "2021-02-26"
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
    definition (name: "Echo Speaks Zone Child", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/drivers/echo-speaks-zone-device.groovy") {
        
        capability "Audio Notification"
        capability "Audio Volume"
        capability "Notification"
        capability "Refresh"
        capability "Sensor"
        capability "Speech Synthesis"
        capability "Switch"
        
        attribute "lastCmdSentDt", "string"
        attribute "lastSpeakCmd", "string"
        attribute "lastAnnouncement", "string"
        attribute "lastUpdated", "string"
        attribute "onlineStatus", "string"
        attribute "volume", "number"

        command "replayText"
        command "playAnnouncement", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"], [name: "Set Volume", type: "NUMBER", description: "Sets the volume before playing the message"],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
        command "playAnnouncementAll", [[name: "Message to Announcement*", type: "STRING", description: "Message to announce"],[name: "Announcement Title", type: "STRING", description: "This displays a title above message on devices with display"]]
        command "stopAllDevices"
        command "setVolumeAndSpeak", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""]]
        command "setVolumeSpeakAndRestore", [[name: "Volume*", type: "NUMBER", description: "Sets the volume before playing the message"], [name: "Message to Speak*", type: "STRING", description: ""],[name: "Restore Volume", type: "NUMBER", description: "Restores the volume after playing the message"]]
//        command "volumeUp"
//        command "volumeDown"
        command "speechTest"
        command "sendTestAnnouncement"
    }

    preferences {
        section("Preferences") {
            input "logInfo", "bool", title: "Show Info Logs?",  required: false, defaultValue: true
            input "logWarn", "bool", title: "Show Warning Logs?", required: false, defaultValue: true
            input "logError", "bool", title: "Show Error Logs?",  required: false, defaultValue: true
            input "logDebug", "bool", title: "Show Debug Logs?", description: "Only leave on when required", required: false, defaultValue: false
            input "logTrace", "bool", title: "Show Detailed Logs?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false
        }
    }
}

def installed() {
    logInfo("${device?.displayName} Executing Installed...")
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    initialize()
}

def updated() {
    logInfo("${device?.displayName} Executing Updated()")
    initialize()
}

def initialize() {
    logInfo("${device?.displayName} Executing initialize()")
    unschedule()
    state.refreshScheduled = false
    stateCleanup()
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return }
    schedDataRefresh(true)
    if(advLogsActive()) { runIn(1800, "logsOff") }
    refreshData(true)
}

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace) }
public void logsOff() {
    device.updateSetting("logDebug",[value:sFALSE,type:"bool"])
    device.updateSetting("logTrace",[value:sFALSE,type:"bool"])
    log.debug "Disabling debug logs"
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

void updateDeviceStatus(Map devData) {
    Boolean isOnline = true
    setOnlineStatus(isOnline)
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false, displayed: false)
    schedDataRefresh()
}

void refresh() {
    logTrace("refresh()")
    refreshData()
}

void refreshData() {
    refreshData()
}

public schedDataRefresh(Boolean frc=false) {
    if(frc || !(Boolean)state.refreshScheduled) {
        runEvery30Minutes("refreshData")
        state.refreshScheduled = true
    }
}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
    if(isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        logDebug("OnlineStatus has changed to (${onlStatus})")
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), display: true, displayed: true)
    }
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
def pause() {
    logTrace("pause() command received...")
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

def stopAllDevices() {
    sendSequenceCommand("StopAllDevicesCommand", "stopalldevices")
}

// capability musicPlayer, audioVolume
def mute() {
    logTrace("mute() command received...")
    def t0= device?.currentValue("level")?.toInteger()
    if( (!state.muteLevel && t0) ) state.muteLevel = t0
    if(isStateChange(device, "mute", "muted")) {
        sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
    }
    setLevel(0)
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
            //parent.sendSpeak(cmdMap, device.deviceNetworkId, "finishSendSpeak")
            sendEvent(name: "level", value: level.toInteger(), display: true, displayed: true)
            sendEvent(name: "volume", value: level.toInteger(), display: true, displayed: true)
        }
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
                Boolean stored = mstoreCurrentVolume()
            }
        }
        speak(msg)
    }
}

void seqHelper_a(String cmd, String val, String cmdType, volume, restoreVolume) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: cmd, cmdType: cmdType, value: val]]
        if(restoreVolume != null) { seqs.push([command: "volume", value: restoreVolume]) }
        parent.relayMultiSeqCommand(seqs, cmdType)
    } else { parent.relaySeqCommand(cmdType, cmd, val) }
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

void seqHelper_s(String cmd, String cmdType, volume, restoreVolume){
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: cmd, cmdType: cmdType]]
        if(restoreVolume != null) { seqs.push([command: "volume", value: restoreVolume]) }
        parent.relayMultiSeqCommand(seqs, cmdType)
    } else { parent.relaySeqCommand(cmdType, cmd) }
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
    seqHelper_s("flashbriefing", "playFlashBrief", volume, restoreVolume)
}

def playGoodNews(volume=null, restoreVolume=null) {
    seqHelper_s("goodnews", "playGoodNews", volume, restoreVolume)
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
    seqHelper_a("announcement", msg, "playAnnouncement", volume, restoreVolume)
    finishAnnounce(msg)
}

def finishAnnounce(String msg){ 
    sendEvent(name: "lastAnnouncement", value: msg, display: false, displayed: false)
}

/*void seqHelper_a(String cmd, String val, String cmdType, volume, restoreVolume) {
    if(volume != null) {
        List seqs = [[command: "volume", value: volume], [command: cmd, cmdType: cmdType, value: val]]
        if(restoreVolume != null) { seqs.push([command: "volume", value: restoreVolume]) }
        sendMultiSequenceCommand(seqs, cmdType)
    } else { sendSequenceCommand(cmdType, cmd, val) } */

def playAnnouncement(String msg, String title, volume=null, restoreVolume=null) {
    String newMsg= "${title ? "${title}::" : sBLANK}${msg}".toString()
    sendAnnouncementToDevices(newMsg, title, volume, restoreVolume)
}

def sendAnnouncementToDevices(String msg, String title=sNULL, volume=null, restoreVolume=null) {
    // log.debug "sendAnnouncementToDevices(msg: $msg, title: $title, devObj: $devObj, volume: $volume, restoreVolume: $restoreVolume)"
    List devObj = state.zoneDevObj
    String devJson = new groovy.json.JsonOutput().toJson(devObj)
    String newmsg = "${title ?: "Echo Speaks"}::${msg}::${devJson}"
    // log.debug "sendAnnouncementToDevices | msg: ${newmsg}"
    if(volume != null) {
        List mainSeq = []
        devObj.each { dev-> mainSeq.push([command: "volume", value: volume, devType: dev.deviceTypeId, devSerial: dev.deviceSerialNumber]) }
        mainSeq.push([command: "announcement_devices", value: newmsg, cmdType: 'playAnnouncement'])
        parent.relayMultiSeqCommand(mainSeq, "sendAnnouncementToDevices-VolumeSet")
        if(restoreVolume!=null) {
            List amainSeq = []
            devObj.each { dev-> amainSeq.push([command: "volume", value: restoreVolume, devType: dev.deviceTypeId, devSerial: dev.deviceSerialNumber]) }
            parent.relayMultiSeqCommand(amainSeq, "sendAnnouncementToDevices-VolumeRestore")
        }
        // log.debug "mainSeq: $mainSeq"
    } else { parent.relaySeqCommand("sendAnnouncementToDevices", "announcement_devices", newmsg) }
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
    if (msg) {
        logDebug("playText($msg, $volume)")
        if(volume) {
            setVolumeAndSpeak(volume, msg)
        } else { speak(msg) }
        return
    }
    logWarn("Uh-Oh... The playText($msg, $volume) Command is NOT Supported by this Device!!!")
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
    if(!msg) { logWarn("No Message sent with speak($msg) command", true); return }
    speechCmd([cmdDesc: "SpeakCommand", message: msg, newVolume: (state.newVolume ?: null), oldVolume: (state.oldVolume ?: null), cmdDt: now()])
    return
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

    Integer msgLen = ((String)cmdMap.message)?.length()
    Random random = new Random()
    Integer randCmdId = random.nextInt(300)
    cmdMap["cmdId"] = randCmdId
    cmdMap["serialNumber"] = (String)state.serialNumber
    cmdMap["deviceType"] = (String)state.deviceType

    parent.relaySpeak(cmdMap, device.deviceNetworkId, "finishSendSpeak")
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


/*******************************************************************
            Speech Queue Logic
*******************************************************************/

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
    "q_lastMsg"
]

private void stateCleanup() {
    state.newVolume = null
    state.oldVolume = null
//    if(state.lastVolume) { state?.oldVolume = state?.lastVolume }
    clnItemsFLD.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
}

String getAmazonDomain() { return (String)state.amazonDomain ?: (String)parent?.settings?.amazonDomain } // does this work for parent call on HE?
String getAmazonUrl() {return "https://alexa.${getAmazonDomain()}".toString() }
private void queueCheck(data) {
    return
}

def testMultiCmd() {
    sendMultiSequenceCommand([[command: "volume", value: 60], [command: "speak", value: "super duper test message 1, 2, 3"], [command: "volume", value: 30]], "testMultiCmd")
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
            if ((Boolean)settings.logInfo != false) {
                String pi = data.cmdDesc ?: "Command"
                pi += data.isSSML ? " (SSML)" :sBLANK
                pi += " Sent"
                pi += " | (${data.message})"
                pi += data.msgLen ? " | Length: (${data.msgLen}) " :sBLANK
                pi += data.msgDelay ? " | Expected Runtime: (${(Integer)data.msgDelay} sec)" :sBLANK
                pi += execTime ? " | Execution Time: (${execTime}ms)" : sBLANK
                pi += (Boolean)settings.logDebug && data.amznReqId ? " | Amazon Request ID: ${data.amznReqId}" :sBLANK
                logInfo(pi)
            }

            if(data?.cmdDesc && data.cmdDesc == "SpeakCommand" && data?.message) {
                state.lastTtsCmdDt = getDtNow()
                String lastMsg = (String)data?.message ?: "Nothing to Show Here..."
                sendEvent(name: "lastSpeakCmd", value: lastMsg, descriptionText: "Last Text Spoken: ${lastMsg}", display: true, displayed: true)
                sendEvent(name: "lastCmdSentDt", value: (String)state.lastTtsCmdDt, descriptionText: "Last Command Timestamp: ${(String)state.lastTtsCmdDt}", display: false, displayed: false)
                if(data?.oldVolume || data?.newVolume) {
                    sendEvent(name: "level", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: true, displayed: true)
                    sendEvent(name: "volume", value: (data?.oldVolume ?: data?.newVolume) as Integer, display: true, displayed: true)
                }
                logSpeech((String)data?.message, statusCode, sNULL)
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
        Integer t0 = ((Map<String,Integer>)parent?.minVersions())["echoDevice"]
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

public void enableDebugLog() { device.updateSetting("logDebug",[value:sTRUE,type:"bool"]); logInfo("Debug Logs Enabled From Main App..."); }
public void disableDebugLog() { device.updateSetting("logDebug",[value:sFALSE,type:"bool"]); logInfo("Debug Logs Disabled From Main App..."); }
public void enableTraceLog() { device.updateSetting("logTrace",[value:sTRUE,type:"bool"]); logInfo("Trace Logs Enabled From Main App..."); }
public void disableTraceLog() { device.updateSetting("logTrace",[value:sFALSE,type:"bool"]); logInfo("Trace Logs Disabled From Main App..."); }

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn logPrefix(sSPACE + msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15) } }
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return (String) str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }

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
    return span("Echo Zone (v" + devVersionFLD + ") | ", sCLRGRY) + span(msg, color)
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
