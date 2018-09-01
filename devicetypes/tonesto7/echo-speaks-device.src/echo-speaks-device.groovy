/**
 *	Echo Speaks Device
 *
 *	Author: Anthony Santilli
 ***************************
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
String devVersion() { return "0.1.0"}
String devModified() { return "2018-09-01"}
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli") {
        capability "Sensor"
        capability "Refresh"
        capability "Music Player"
        capability "Notification"
        capability "Speech Synthesis"
        
        attribute "lastUpdated", "string"
        attribute "firmwareVer", "string"
        attribute "onlineStatus", "string"

        command "sendTestTts"
    }

    preferences { 
        input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
    }

    tiles (scale: 2) {
        multiAttributeTile(name: "mediaMulti", type:"mediaPlayer", width:6, height:4) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused",)
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
        valueTile("blank1x1", "device.blank", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("blank2x1", "device.blank", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("firmwareVer", "device.firmwareVer", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("firmwareVer", label:'Firmware:\n${currentValue}')
        }
        standardTile("sendTest", "sendTest", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Send Test TTS', action: 'sendTestTts')
        }
        valueTile("status", "device.onlineStatus", height: 1, width: 2, decoration: "flat") {
            state("default", label: '${currentValue}', icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_device.png")
        }
        main(["status"])
        details(["mediaMulti", "dtCreated", "firmwareVer", "sendTest"])
    }
}

def parse(description) {
	// No parsing will happen
}

def installed() {
	log.trace "${device?.displayName} Executing Installed..."

    state.tracks = [
		"Gangnam Style (강남스타일)\nPSY\nPsy 6 (Six Rules), Part 1",
		"Careless Whisper\nWham!\nMake It Big",
		"Never Gonna Give You Up\nRick Astley\nWhenever You Need Somebody",
		"Shake It Off\nTaylor Swift\n1989",
		"Ironic\nAlanis Morissette\nJagged Little Pill",
		"Hotline Bling\nDrake\nHotline Bling - Single"
	]
    state.currentTrack = 0
	sendEvent(name: "level", value: 72)
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

def updateDeviceStatus(Map echoDevice) {
    String devName = getShortDevName()
    echoDevice?.each { k,v ->
        logger("debug", "$k: $v")
    }
    state?.serialNumber = echoDevice?.serialNumber
    if(echoDevice?.size()) {
        state?.serialNumber = echoDevice?.serialNumber
        String firmwareVer = echoDevice?.softwareVersion ?: "Not Set"
        if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
            sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
        }
    }
    setOnlineStatus((echoDevice?.online != false))
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false , displayed: false)
}

public setOnlineStatus(Boolean isOnline) {
    if(isStateChange(device, "DeviceWatch-DeviceStatus", (isOnline ? "online" : "offline")) || isStateChange(device, "onlineStatus", (isOnline ? "online" : "offline"))) {
        sendEvent(name: "DeviceWatch-DeviceStatus", value: (isOnline ? "online" : "offline"), displayed: true, isStateChange: true)
        sendEvent(name: "onlineStatus", value: (isOnline ? "online" : "offline"), displayed: true, isStateChange: true)
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
    log.debug "play() | Not Supported Yet!!!"
	// sendEvent(name: "status", value: "playing")
	// sendEvent(name: "trackDescription", value: state.tracks[state.currentTrack])
}

def pause() {
    log.debug "pause() | Not Supported Yet!!!"
	// sendEvent(name: "status", value: "paused")
	// sendEvent(name: "trackDescription", value: state.tracks[state.currentTrack])
}

def stop() {
    log.debug "stop() | Not Supported Yet!!!"
	// sendEvent(name: "status", value: "stopped")
}

def previousTrack() {
    log.debug "previousTrack() | Not Supported Yet!!!"
	// state.currentTrack = state.currentTrack - 1
	// if (state.currentTrack < 0) {
	// 	state.currentTrack = state.tracks.size()-1
    // }
	// sendEvent(name: "trackDescription", value: state.tracks[state.currentTrack])
}

def nextTrack() {
    log.debug "nextTrack() | Not Supported Yet!!!"
	// state.currentTrack = state.currentTrack + 1
	// if (state.currentTrack == state.tracks.size()) {
	// 	state.currentTrack = 0
    // }
	// sendEvent(name: "trackDescription", value: state.tracks[state.currentTrack])
}

def mute() {
    log.debug "mute() | Not Supported Yet!!!"
	// sendEvent(name: "mute", value: "muted")
}

def unmute() {
    log.debug "unmute() | Not Supported Yet!!!"
	// sendEvent(name: "mute", value: "unmuted")
}

def setLevel(level) {
    log.debug "setLevel($level) | Not Supported Yet!!!"
	// sendEvent(name: "level", value: level)
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
    if(text) { sendTtsMsg(text) }
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
    if(text) { sendTtsMsg(text) }
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
	sendTtsMsg(ttsMsg)
}

public sendTtsMsg(String msg) {
    if(state?.serialNumber && msg) {
		parent?.echoServiceCmd("tts", [deviceSerialNumber: state?.serialNumber, tts: msg])
	} else { log.warn "sendTtsMsg Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Message: ${msg}" }
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
