/**
 *	Echo Speaks Device
 *
 *  Copyright 2018 Anthony Santilli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import java.text.SimpleDateFormat
String devVersion() { return "0.6.0"}
String devModified() { return "2018-09-10"}
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli") {
        capability "Sensor"
        capability "Refresh"
        capability "Music Player"
        capability "Notification"
        capability "Speech Synthesis"
        
        attribute "lastUpdated", "string"
        attribute "deviceFamily", "string"
        attribute "firmwareVer", "string"
        attribute "onlineStatus", "string"
        attribute "currentStation", "string"
        attribute "currentAlbum", "string"
        command "sendTestTts"
    }

    preferences { 
        input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
    }

    tiles (scale: 2) {
        multiAttributeTile(name: "mediaMulti", type:"mediaPlayer", width:6, height:4) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused")
				attributeState("playing", label:"Playing")
				attributeState("stopped", label:"Stopped")
			}
			tileAttribute("device.status", key: "MEDIA_STATUS") {
				attributeState("paused", label:"Paused", action:"music Player.play", nextState: "playing")
				attributeState("playing", label:"Playing", action:"music Player.pause", nextState: "paused")
				attributeState("stopped", label:"Stopped", action:"music Player.play", nextState: "playing")
			}
			tileAttribute("device.status", key: "PREVIOUS_TRACK") {
				attributeState("status", action:"music Player.previousTrack", defaultState: true)
			}
			tileAttribute("device.status", key: "NEXT_TRACK") {
				attributeState("status", action:"music Player.nextTrack", defaultState: true)
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState("level", action:"music Player.setLevel")
			}
			tileAttribute ("device.mute", key: "MEDIA_MUTED") {
				attributeState("unmuted", action:"music Player.mute", nextState: "muted")
				attributeState("muted", action:"music Player.unmute", nextState: "unmuted")
			}
			tileAttribute("device.trackDescription", key: "MARQUEE") {
				attributeState("trackDescription", label:"${currentValue}", defaultState: true)
			}
		}
        standardTile("deviceStatus", "device.status", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("paused", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png", backgroundColor: "#cccccc")
            state("playing", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png", backgroundColor: "#00a0dc")
            state("stopped", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png")
            state("unavailable", label: "Unavailable", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png", backgroundColor: "#F22000")
        }
        valueTile("blank1x1", "device.blank", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("blank2x1", "device.blank", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("firmwareVer", "device.firmwareVer", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("firmwareVer", label:'Firmware:\n${currentValue}')
        }
        valueTile("deviceFamily", "device.deviceFamily", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Device Family:\n${currentValue}')
        }
        valueTile("currentStation", "device.currentStation", height: 1, width: 3, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Station:\n${currentValue}')
        }
        valueTile("currentAlbum", "device.currentAlbum", height: 1, width: 3, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Album:\n${currentValue}')
        }
        standardTile("sendTest", "sendTest", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Send Test TTS', action: 'sendTestTts')
        }
        valueTile("status", "device.onlineStatus", height: 1, width: 2, decoration: "flat") {
            state("default", label: '${currentValue}', icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png")
        }
        main(["deviceStatus"])
        details(["mediaMulti","currentAlbum", "currentStation", "dtCreated", "deviceFamily", "firmwareVer", "sendTest"])
    }
}

def parse(description) {
	// No parsing will happen
}

def installed() {
	log.trace "${device?.displayName} Executing Installed..."

    state.tracks = []
    state.currentTrack = 0
	sendEvent(name: "level", value: 0)
	sendEvent(name: "mute", value: "unmuted")
	sendEvent(name: "status", value: "stopped")
	initialize()
}

def updated() {
	log.trace "${device?.displayName} Executing Updated..."
	initialize()
}

def initialize() {
	log.trace "${device?.displayName} Executing initialize"
 	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def getShortDevName(){
    return device?.displayName?.replace("Echo - ", "")
}

def updateDeviceStatus(Map devData) {
    String devName = getShortDevName()
    if(devData?.size()) {
        // devData?.each { k,v ->
        //     if(!(k in ["playerState", "capabilities", "deviceAccountId"])) {
        //         logger("debug", "$k: $v")
        //     }
        // }
        devData?.playerState?.each { k,v ->
            if(!(k in ["mainArt", "mediaId", "miniArt", "hint", "template", "upNextItems", "queueId", "miniInfoText", "provider"])) {
                logger("debug", "$k: $v")
            }
        }
        state?.serialNumber = devData?.serialNumber
        state?.serialNumber = devData?.serialNumber
        state?.deviceType = devData?.deviceType
        state?.deviceOwnerCustomerId = devData?.deviceOwnerCustomerId
        String firmwareVer = devData?.softwareVersion ?: "Not Set"
        if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
            sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
        }
        log.debug "deviceFamily: ${devData?.deviceFamily}"
        String devFamily = devData?.deviceFamily ?: ""
        if(isStateChange(device, "deviceFamily", devFamily?.toString())) {
            sendEvent(name: "deviceFamily", value: devFamily?.toString(), descriptionText: "Echo Device Family is ${devFamily}", display: true, displayed: true)
        }
        if(devData?.playerState?.size()) {
            Map sData = devData?.playerState
            String playState = sData?.state == 'PLAYING' ? "playing" : "stopped"
            log.debug "playState: $playState"
            if(isStateChange(device, "status", playState?.toString())) {
                sendEvent(name: "status", value: playState?.toString(), descriptionText: "Player Status is ${playState}", display: true, displayed: true)
            }
            if(sData?.infoText) {
                // infoText: [multiLineMode:false, subText1:The Sixteen & Harry Christophers, subText2:Renaissance Classical Station, title:Veni sancte Spiritus]
                if(sData?.infoText.title) {
                    String title = sData?.infoText.title ?: ""
                    if(isStateChange(device, "trackDescription", title?.toString())) {
                        sendEvent(name: "trackDescription", value: title?.toString(), descriptionText: "Track Description is ${title}", display: true, displayed: true)
                    }

                    String subText1 = sData?.infoText.subText1 ?: ""
                    if(isStateChange(device, "currentAlbum", subText1?.toString())) {
                        sendEvent(name: "currentAlbum", value: subText1?.toString(), descriptionText: "Album is ${subText1}", display: true, displayed: true)
                    }
                    String subText2 = sData?.infoText.subText2 ?: ""
                    if(isStateChange(device, "currentStation", subText2?.toString())) {
                        sendEvent(name: "currentStation", value: subText2?.toString(), descriptionText: "Station is ${subText2}", display: true, displayed: true)
                    }
                }
            }
            if(sData?.volume) {
                if(sData?.volume?.volume) {
                    Integer level = sData?.volume?.volume
                    if(level < 0) { level = 0 }
                    if(level > 100) { level = 100 }
                    if(isStateChange(device, "level", level?.toString())) {
                        sendEvent(name: "level", value: level, descriptionText: "Volume Level set to ${level}", display: true, displayed: true)
                    }
                }
                if(sData?.volume?.muted) {
                    String muteState = sData?.volume?.muted == true ? "muted" : "unmuted"
                    if(isStateChange(device, "mute", muteState?.toString())) {
                        sendEvent(name: "mute", value: muteState, descriptionText: "Mute State is ${muteState?.capitialize()}", display: true, displayed: true)
                    }
                }
            }
        }
    }
    
    setOnlineStatus((devData?.online != false))
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false , displayed: false)
}

def updateServiceInfo(String svcHost) {
    if(svcHost) { state?.serviceHost = svcHost }
}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
    if(isStateChange(device, "DeviceWatch-DeviceStatus", onlStatus?.toString()) || isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        sendEvent(name: "DeviceWatch-DeviceStatus", value: onlStatus?.toString(), displayed: false, isStateChange: true)
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), displayed: true, isStateChange: true)
    }
}

def deviceNotification(String msg) {
    log.trace "deviceNotification(msg: $msg)"
    if(msg) { sendTtsMsg(msg) }
}

def speak(String msg) {
    log.trace "speak($msg)"
    if(msg) { sendTtsMsg(msg) }
}

def play() {
    log.debug "play() command received..."
    if(state?.serialNumber) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "PlayCommand"
        ])
        if(isStateChange(device, "status", "playing")) {
            sendEvent(name: "status", value: "playing", descriptionText: "Player Status is playing", display: true, displayed: true)
        }
	} else { log.warn "play() Command Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def pause() {
    log.debug "pause() command received..."
    if(state?.serialNumber) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "PauseCommand"
        ])
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
        }
	} else { log.warn "pause() Command Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
	// sendEvent(name: "trackDescription", value: state.tracks[state.currentTrack])
}

def stop() {
    log.debug "stop() command received..."
    if(state?.serialNumber) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "StopCommand"
        ])
        if(isStateChange(device, "status", "stopped")) {
            sendEvent(name: "status", value: "stopped", descriptionText: "Player Status is stopped", display: true, displayed: true)
        }
	} else { log.warn "stop() Command Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def previousTrack() {
	log.debug "previousTrack() command received..."
    if(state?.serialNumber) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "PreviousCommand"
        ])
	} else { log.warn "previousTrack() Command Error | You are missing a SerialNumber: ${state?.serialNumber}" }
}

def nextTrack() {
    log.debug "nextTrack() command received..."
    if(state?.serialNumber) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "NextCommand"
        ])
	} else { log.warn "nextTrack() Command Error | You are missing a SerialNumber: ${state?.serialNumber}" }
}

def mute() {
    log.debug "mute() command received..."
    if(state?.serialNumber) {
        state.muteLevel = device?.currentState("level")?.integerValue
        if(isStateChange(device, "mute", "muted")) {
            sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
        }
        setLevel(0)
	} else { log.warn "mute() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def unmute() {
    log.debug "unmute() command received..."
    if(state?.serialNumber) {
		if(state?.muteLevel) {
            setLevel(state?.muteLevel)
            state?.muteLevel = null
            if(isStateChange(device, "mute", "unmuted")) {
                sendEvent(name: "mute", value: "unmuted", descriptionText: "Mute is set to unmuted", display: true, displayed: true)
            }
        }
	} else { log.warn "unmute() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def setLevel(level) {
    log.debug "setVolume($level) command received..."
    if(state?.serialNumber && level) {
		echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "VolumeLevelCommand", 
            cmdValObj: [volumeLevel: level?.toInteger()].encodeAsJson()
        ])
        if(isStateChange(device, "level", level?.toString())) {
            sendEvent(name: "level", value: level, descriptionText: "Volume Level set to ${level}", display: true, displayed: true)
        }
	} else { log.warn "setLevel() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Level: ${level}" }
}

def setTrack(String uri, metaData="") {
    log.debug "setLevel($level) | Not Supported Yet!!!"
	// log.debug "Executing 'setTrack'"
    // sendCommand("track=$uri")
}

def resumeTrack() {
    log.debug "resumeTrack() | Not Supported Yet!!!"
	// log.debug "Executing 'resumeTrack'"
	// TODO: handle 'resumeTrack' command
}

def restoreTrack() {
    log.debug "restoreTrack() | Not Supported Yet!!!"
	// log.debug "Executing 'restoreTrack'"
	// TODO: handle 'restoreTrack' command
}

def playTrackAtVolume(String uri, volume) {
    log.debug "playTrackAtVolume() | Not Supported Yet!!!"
    // log.trace "playTrackAtVolume($uri, $volume)"
	// sendCommand("playTrack&track=${uri}&volume=${volume}")
}

def playTrackAndResume(uri, duration, volume=null) {
    log.debug "playTrackAndResume() | Not Supported Yet!!!"
    // log.debug "playTrackAndResume($uri, $duration, $volume)"
	// def cmd = "playTrack&track=${uri}&resume"
	// if (volume) {
	// 	cmd += "&volume=${volume}"
    // }
    // sendCommand(cmd)
}

def playTextAndResume(text, volume=null) {
    log.debug "playTextAndResume($text, $volume)"
    // def sound = textToSpeech(text)
    // playTrackAndResume(sound.uri, (sound.duration as Integer) + 1, volume)
    if(text) { sendTtsMsg(text, volume) }
}

def playTrackAndRestore(uri, duration, volume=null) {
    log.debug "playTrackAndRestore() | Not Supported Yet!!!"
    // log.debug "playTrackAndResume($uri, $duration, $volume)"
	// def cmd = "playTrack&track=${uri}&restore"
	// if (volume) {
	// 	cmd += "&volume=${volume}"
    // }
    // sendCommand(cmd)
}

def playTextAndRestore(text, volume=null) {
    log.debug "playTextAndResume($text, $volume)"
	// def sound = textToSpeech(text)
	// playTrackAndRestore(sound.uri, (sound.duration as Integer) + 1, volume)
    if(text) { sendTtsMsg(text, volume) }
}

def playURL(theURL) {
    log.debug "playURL() | Not Supported Yet!!!"
	// log.debug "Executing 'playURL'"
    // sendCommand("url=$theURL")
}

def playSoundAndTrack(soundUri, duration, trackData, volume=null) {
    log.debug "playSoundAndTrack() | Not Supported Yet!!!"
	// log.debug "playSoundAndTrack($uri, $duration, $trackData, $volume)"
	// def cmd = "playTrack&track=${soundUri}&playlist=${trackData.station}"
	// if (volume) {
	// 	cmd += "&volume=${volume}"
    // }
    // sendCommand(cmd)
}

def sendTestTts(ttsMsg) {
    log.trace "sendTestTts"
    if(!ttsMsg) { ttsMsg = "Testing Testing 1, 2, 3"}
	sendTtsMsg(ttsMsg, 20)
}

public sendTtsMsg(String msg, Integer volume=null) {
    if(state?.serialNumber && msg) {
        if(volume) { 
            setLevel(volume)
        }
		echoServiceCmd("tts", [deviceSerialNumber: state?.serialNumber, tts: msg])
	} else { log.warn "sendTtsMsg Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Message: ${msg}" }
}

private echoServiceCmd(type, headers={}, body = null) {
	// log.trace("echoServiceCmd(type: $type, headers: $headers, body: $body)")
	String host = state?.serviceHost
	if(!host) { return }
	logger("trace", "echoServiceCmd($type) | headers: ${headers} | body: $body | host: ${host}")
	try {
		String path = ""
		Map headerMap = ["HOST": host]
		switch(type) {
			case "tts":
				path = "/alexa-tts"
				break
			case "deviceState":
				path = "/alexa-getState"
				break
            case "cmd":
				path = "/alexa-command"
				break
		}
		headers?.each { k,v-> headerMap[k] = v }
		def hubAction = new physicalgraph.device.HubAction(
			method: "POST",
			headers: headerMap,
			path: path,
			body: body ?: ""
		)
		sendHubCommand(hubAction)
	}
	catch (Exception ex) {
		log.error "echoServiceCmd HubAction Exception, $hubAction", ex
	}
}

/*****************************************************
                HELPER FUNCTIONS
******************************************************/
def formatDt(dt, String tzFmt=("MM/d/yyyy hh:mm:ss a")) {
	def tf = new SimpleDateFormat(tzFmt); tf.setTimeZone(location.timeZone);
    return tf.format(dt)
}

def parseDt(dt, dtFmt) {
    return Date.parse(dtFmt, dt)
}

Boolean ok2Notify() {
    return (parent?.getOk2Notify())
}

private logger(type, msg) {
	if(type && msg && settings?.showLogs) {
		log."${type}" "${msg}"
	}
}
