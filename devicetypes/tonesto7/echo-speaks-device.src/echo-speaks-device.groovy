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
include 'asynchttp_v1'
String devVersion() { return "1.0.2"}
String devModified() { return "2018-11-01"}
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli", ocfResourceType: "x.com.st.mediaplayer") {
        capability "Sensor"
        capability "Refresh"
        capability "Music Player"
        capability "Notification"
        capability "Speech Synthesis"

        attribute "lastUpdated", "string"
        attribute "deviceStatus", "string"
        attribute "deviceStyle", "string"
        attribute "doNotDisturb", "boolean"
        attribute "firmwareVer", "string"
        attribute "onlineStatus", "string"
        attribute "currentStation", "string"
        attribute "currentAlbum", "string"
        command "sendTestTts"
        command "doNotDisturbOn"
        command "doNotDisturbOff"
        // command "setVolumeAndSpeak", ["number", "string"]
        command "resetQueue"
    }

    preferences {
        input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
        input "disableQueue", "bool", required: false, title: "Don't Allow Queuing?", defaultValue: false
    }

    tiles (scale: 2) {
        multiAttributeTile(name: "mediaMulti", type:"mediaPlayer", width:6, height:4) {
            tileAttribute("device.status", key: "PRIMARY_CONTROL") {
                attributeState("paused", label:"Paused")
                attributeState("playing", label:"Playing")
                attributeState("stopped", label:"Stopped", defaultState: true)
            }
            tileAttribute("device.status", key: "MEDIA_STATUS") {
                attributeState("paused", label:"Paused", action:"music Player.play", nextState: "playing")
                attributeState("playing", label:"Playing", action:"music Player.pause", nextState: "paused")
                attributeState("stopped", label:"Stopped", action:"music Player.play", nextState: "playing", defaultState: true)
            }
            tileAttribute("device.status", key: "PREVIOUS_TRACK") {
                attributeState("status", action:"music Player.previousTrack", defaultState: true)
            }
            tileAttribute("device.status", key: "NEXT_TRACK") {
                attributeState("status", action:"music Player.nextTrack", defaultState: true)
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState("level", action:"music Player.setLevel", defaultState: true)
            }
            tileAttribute ("device.mute", key: "MEDIA_MUTED") {
                attributeState("unmuted", action:"music Player.mute", nextState: "muted")
                attributeState("muted", action:"music Player.unmute", nextState: "unmuted", defaultState: true)
            }
            tileAttribute("device.trackDescription", key: "MARQUEE") {
                attributeState("trackDescription", label:"${currentValue}", defaultState: true)
            }
        }
        standardTile("deviceStatus", "device.deviceStatus", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("paused_echo_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen1.png")

            state("paused_echo_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_gen2.png")

            state("paused_echo_dot_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_dot_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_dot_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen1.png")

            state("paused_echo_dot_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_dot_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_dot_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen2.png")

            state("paused_echo_dot_gen3", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen3.png", backgroundColor: "#cccccc")
            state("playing_echo_dot_gen3", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen3.png", backgroundColor: "#00a0dc")
            state("stopped_echo_dot_gen3", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_dot_gen3.png")

            state("paused_echo_spot_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_spot_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_spot_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen1.png")

            state("paused_echo_spot_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_spot_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_spot_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_spot_gen2.png")

            state("paused_echo_show_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_show_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_show_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png")

            state("paused_echo_show_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_show_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_show_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png")

            state("paused_amazon_tablet", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png", backgroundColor: "#cccccc")
            state("playing_amazon_tablet", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png", backgroundColor: "#00a0dc")
            state("stopped_amazon_tablet", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png")
        }
        valueTile("blank1x1", "device.blank", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("blank2x1", "device.blank", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("blank1x1", label:'')
        }
        valueTile("firmwareVer", "device.firmwareVer", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Firmware:\n${currentValue}')
        }
        valueTile("deviceStyle", "device.deviceStyle", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("deviceStyle", label:'Device Style:\n${currentValue}')
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
        standardTile("resetQueue", "resetQueue", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Reset Queue', action: 'resetQueue')
        }
        standardTile("doNotDisturb", "device.doNotDisturb", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state "true", label: 'DnD: ON', action: "doNotDisturbOff", nextState: "false"
            state "false", label: 'DnD: OFF', action: "doNotDisturbOn", nextState: "true"
        }
        main(["deviceStatus"])
        details(["mediaMulti", "currentAlbum", "currentStation", "dtCreated", "deviceFamily", "firmwareVer", "doNotDisturb", "deviceStyle", "deviceImage", "sendTest", "resetQueue"])
    }
}

def installed() {
    log.trace "${device?.displayName} Executing Installed..."
    sendEvent(name: "level", value: 0)
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "deviceStatus", value: "stopped_echo_gen1")
    sendEvent(name: "trackDescription", value: "")
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
    resetQueue()
}

String getHealthStatus(lower=false) {
	String res = device?.getStatus()
	if(lower) { return res?.toString()?.toLowerCase() }
	return res as String
}

def getShortDevName(){
    return device?.displayName?.replace("Echo - ", "")
}

Map getDeviceStyle(String family, String type) {
    switch(family) {
        case "KNIGHT":
            switch (type) {
                case "A1NL4BVLQ4L3N3":
                    return [name: "Echo Show", image: "echo_show_gen1"]
                case "AWZZ5CVHX2CD":
                    return [name: "Echo Show", image: "echo_show_gen2"]
                default:
                    return [name: "Echo Show", image: "echo_show_gen1"]
            }
        case "ECHO":
            switch (type) {
                case "AB72C64C86AW2":
                    return [name: "Echo", image: "echo_gen1"]
                case "AKNO1N0KSFN8L":
                    return [name: "Echo Dot", image: "echo_dot_gen1"]
                case "A3S5BH2HU6VAYF":
                    return [name: "Echo Dot (Gen2)", image: "echo_dot_gen2"]
                default:
                    return [name: "Echo", image: "echo_gen2"]
            }
        case "ROOK":
            switch (type) {
                case "A10A33FOX2NUBK":
                    return [name: "Echo Spot", image: "echo_spot_gen1"]
                default:
                    return [name: "Echo Spot", image: "echo_spot_gen1"]
            }
        case "TABLET":
            switch(type) {
                default:
                return [name: "Kindle Tablet", image: "amazon_tablet"]
            }
        default:
            return [name: "Echo", image: "echo_dot_gen2"]
    }
}

public updateDeviceStatus(Map devData) {
    try {
        String devName = getShortDevName()
        if(devData?.size()) {
            // log.debug "deviceFamily: ${devData?.deviceFamily} | deviceType: ${devData?.deviceType}"  // UNCOMMENT to identify unidentified devices
            // devData?.each { k,v ->
            //     if(!(k in ["playerState", "capabilities", "deviceAccountId"])) {
            //         log.debug("$k: $v")
            //     }
            // }
            // devData?.playerState?.each { k,v ->
            //     if(!(k in ["mainArt", "mediaId", "miniArt", "hint", "template", "upNextItems", "queueId", "miniInfoText", "provider"])) {
            //         logger("debug", "$k: $v")
            //     }
            // }
            Map deviceStyle = getDeviceStyle(devData?.deviceFamily as String, devData?.deviceType as String)
            state?.deviceImage = deviceStyle?.image as String
            if(isStateChange(device, "deviceStyle", deviceStyle?.name?.toString())) {
                sendEvent(name: "deviceStyle", value: deviceStyle?.name?.toString(), descriptionText: "Device Style is ${deviceStyle?.name}", display: true, displayed: true)
            }
            // logger("info", "deviceStyle (${devData?.deviceFamily}): ${devData?.deviceType} | Desc: ${deviceStyle?.name}")
            state?.serialNumber = devData?.serialNumber
            state?.deviceType = devData?.deviceType
            state?.deviceOwnerCustomerId = devData?.deviceOwnerCustomerId
            // log.debug "dndEnabled: ${devData?.dndEnabled}"
            String firmwareVer = devData?.softwareVersion ?: "Not Set"
            if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
                sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), descriptionText: "Firmware Version is ${firmwareVer}", display: true, displayed: true)
            }
            if(isStateChange(device, "doNotDisturb", (devData?.dndEnabled == true)?.toString())) {
                sendEvent(name: "doNotDisturb", value: (devData?.dndEnabled == true)?.toString(), descriptionText: "Do Not Disturb Enabled ${(devData?.dndEnabled == true)}", display: true, displayed: true)
            }
            String devFamily = devData?.deviceFamily ?: ""
            if(isStateChange(device, "deviceFamily", devFamily?.toString())) {
                sendEvent(name: "deviceFamily", value: devFamily?.toString(), descriptionText: "Echo Device Family is ${devFamily}", display: true, displayed: true)
            }
            if(devData?.playerState?.size()) {
                Map sData = devData?.playerState
                String playState = sData?.state == 'PLAYING' ? "playing" : "stopped"
                String deviceStatus = "${playState}_${deviceStyle?.image}"
                // log.debug "deviceStatus: ${deviceStatus}"
                if(isStateChange(device, "status", playState?.toString()) || isStateChange(device, "deviceStatus", deviceStatus?.toString())) {
                    sendEvent(name: "status", value: playState?.toString(), descriptionText: "Player Status is ${playState}", display: true, displayed: true)
                    sendEvent(name: "deviceStatus", value: deviceStatus?.toString(), display: false, displayed: false)
                }
                if(sData?.infoText) {
                    // infoText: [multiLineMode:false, subText1:The Sixteen & Harry Christophers, subText2:Renaissance Classical Station, title:Veni sancte Spiritus]
                    if(sData?.infoText.title) {
                        String title = sData?.infoText.title ?: ""
                        if(isStateChange(device, "trackDescription", title?.toString())) {
                            sendEvent(name: "trackDescription", value: title?.toString(), descriptionText: "Track Description is ${title}", display: true, displayed: true)
                        }

                        String subText1 = sData?.infoText.subText1 ?: "Idle"
                        if(isStateChange(device, "currentAlbum", subText1?.toString())) {
                            sendEvent(name: "currentAlbum", value: subText1?.toString(), descriptionText: "Album is ${subText1}", display: true, displayed: true)
                        }
                        String subText2 = sData?.infoText.subText2 ?: "Idle"
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
                            sendEvent(name: "mute", value: muteState, descriptionText: "Mute State is ${muteState}", display: true, displayed: true)
                        }
                    }
                }
            } else {
                def deviceStatus = "stopped_echo_gen1"
                if(deviceStyle?.image) { deviceStatus = "stopped_${deviceStyle?.image}" }
                // log.debug "deviceStatus: ${deviceStatus}"
                if(isStateChange(device, "deviceStatus", deviceStatus?.toString())) {
                    sendEvent(name: "deviceStatus", value: deviceStatus?.toString(), display: false, displayed: false)
                }
            }
        }
        setOnlineStatus((devData?.online != false))
        sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false , displayed: false)
    } catch(ex) {
        log.error "updateDeviceStatus Error: ", ex
    }
}

public updateServiceInfo(String svcHost, useHeroku=false) {
    state?.serviceHost = svcHost
    state?.useHeroku = useHeroku
}

public resetServiceInfo() {
    logger("trace", "resetServiceInfo() received...")
    resetQueue()
    ["serviceHost", "useHeroku", ""]?.each { item->
        state?.remove(item)
    }
}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
    if(isStateChange(device, "DeviceWatch-DeviceStatus", onlStatus?.toString()) || isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        sendEvent(name: "DeviceWatch-DeviceStatus", value: onlStatus?.toString(), displayed: false, isStateChange: true)
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), displayed: true, isStateChange: true)
    }
}

def play() {
    logger("trace", "play() command received...")
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
    logger("trace", "pause() command received...")
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
    logger("trace", "stop() command received...")
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
    logger("trace", "previousTrack() command received...")
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
    logger("trace", "nextTrack() command received...")
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
    logger("trace", "mute() command received...")
    if(state?.serialNumber) {
        state.muteLevel = device?.currentState("level")?.integerValue
        if(isStateChange(device, "mute", "muted")) {
            sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
        }
        setLevel(0)
    } else { log.warn "mute() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def unmute() {
    logger("trace", "unmute() command received...")
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
    logger("trace", "setVolume($level) command received...")
    if(state?.serialNumber && level>=0 && level<=100) {
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
    logger("warn", "setTrack(uri: $uri, meta: $meta) | Not Supported Yet!!!")
}

def resumeTrack() {
    logger("warn", "resumeTrack() | Not Supported Yet!!!")
}

def restoreTrack() {
    logger("warn", "restoreTrack() | Not Supported Yet!!!")
}

def playURL(theURL) {
    logger("warn", "playURL() | Not Supported Yet!!!")
    // log.debug "Executing 'playURL'"
    // sendCommand("url=$theURL")
}

public doNotDisturbOff() {
    logger("trace", "doNotDisturbOff() command received...")
    setDoNotDisturb(false)
}

public doNotDisturbOn() {
    logger("trace", "doNotDisturbOn() command received...")
    setDoNotDisturb(true)
}

public setDoNotDisturb(Boolean val) {
    logger("trace", "setDoNotDisturb() command received...")
    echoServiceCmd("cmd", [
        deviceSerialNumber: state?.serialNumber,
        deviceType: state?.deviceType,
        deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
        cmdType: "SetDnd",
        cmdValObj: [enabled: (val==true)].encodeAsJson()
    ])
}


public deviceNotification(String msg) {
    if(!msg) { log.warn "No Message sent with deviceNotification($msg) command" }
    log.trace "deviceNotification(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
    speak(msg as String)
}

public speak(String msg) {
    if(!msg) { log.warn "No Message sent with speak($msg) command" }
    // log.trace "speak(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
    if(msg != null && state?.serialNumber) {
        echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            message: msg,
            cmdType: "SendTTS"
        ])
    } else { log.warn "speak Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Message: ${msg}" }
}

def getRandomItem(items) {
    def list = new ArrayList<String>();
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()));
}

def sendTestTts(ttsMsg) {
    log.trace "sendTestTts"
    List items = ["Testing Testing 1, 2, 3", "Yay!, I'm Alive... Hopefully you can hear me speaking?", "Being able to make me say whatever you want is the coolest thing since sliced bread!",
        "I said a hip hop, Hippie to the hippie," +
        "The hip, hip a hop, and you don't stop, a rock it out," +
        "Bubba to the bang bang boogie, boobie to the boogie" +
        "To the rhythm of the boogie the beat," +
        "Now, what you hear is not a test I'm rappin' to the beat", "This is how we do it!. It's Friday night, and I feel alright. The party is here on the West side. So I reach for my 40 and I turn it up. Designated driver take the keys to my truck, Hit the shore 'cause I'm faded, Honeys in the street say, Monty, yo we made it!. It feels so good in my hood tonight,  The summertime skirts and the guys in Kani.",
        "Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Heroes in a half-shell Turtle power!...   They're the world's most fearsome fighting team (We're really hip!), They're heroes in a half-shell and they're green (Hey - get a grip!), When the evil Shredder attacks!!!, These Turtle boys don't cut him no slack!."
    ]
    if(!ttsMsg) { ttsMsg = getRandomItem(items) }
    speak(ttsMsg as String)
}

Integer getRecheckDelay(Integer msgLen=null) {
    def random = new Random()
	Integer randomInt = random?.nextInt(5) //Was using 7 
    if(!msgLen) { return 30 }
    def v = (msgLen <= 14 ? 1 : (msgLen / 14)) as Integer
    // logger("trace", "getRecheckDelay($msgLen) | delay: $v + $randomInt")
    return v + randomInt
}

Integer getLastTtsCmdSec() { return !state?.lastTtsCmdDt ? 1000 : GetTimeDiffSeconds(state?.lastTtsCmdDt).toInteger() }

private getQueueSize() {
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }
    return (cmdQueue?.size() ?: 0)
}

private getQueueSizeStr() {
    Integer size = getQueueSize()
    return "($size) Item${size>1 || size==0 ? "s" : ""}"
}

private processLogItems(String logType, List logList, emptyStart=false, emptyEnd=true) {
    if(logType && logList?.size() && settings?.showLogs) {
        Integer maxStrLen = 0
        String endSep = "└──────────────────────────────────"
        if(emptyEnd) { logger(logType, " ") }
        logger(logType, endSep)
        logList?.each { l->
            logger(logType, l)
        }
        if(emptyStart) { logger(logType, " ") }
    }
}

private queueWatchDog() {
    checkQueue()
}

private resetQueue(showLog=true) {
    if(showLog) { log.trace "resetQueue()" }
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }
    cmdQueue?.each { cmdKey, cmdData ->
        state?.remove(cmdKey)
    }
    unschedule("checkQueue")
    state?.qCmdCycleCnt = null
    state?.cmdQueueWorking = false
    state?.firstCmdFlag = false
    state?.recheckScheduled = false
    state?.cmdQIndexNum = null
    state?.curMsgLen = null
    state?.lastTtsCmdDelay = null
}

Integer getQueueIndex() {
    return state?.cmdQIndexNum ? state?.cmdQIndexNum+1 : 1
}

private getQueueItems() {
    return state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }
}

private schedQueueCheck(Integer delay, overwrite=true, data=null, src) {
    if(delay) {
        Map opts = [:]
        opts["overwrite"] = overwrite
        if(data) { opts["data"] = data }
        runIn(delay, "checkQueue", opts)
        state?.recheckScheduled = true
        log.debug "checkQueue Scheduled | Delay: ($delay) | Overwrite: $overwrite | recheckScheduled: ${state?.recheckScheduled} | Src: $src"
    }
}

public queueEchoCmd(type, headers, body=null, firstRun=false) {
    List logItems = []
    Map cmdItems = state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") && it?.value?.type == type && it?.value?.headers && it?.value?.headers?.message == headers?.message }
    logItems?.push("│ Queue Active: (${state?.cmdQueueWorking}) | Recheck: (${state?.recheckScheduled}) ")
    if(cmdItems?.size()) {
        if(headers?.message) {
            Integer ml = headers?.message?.toString()?.length()
            logItems?.push("│ Message(${ml} char): ${headers?.message?.take(190)?.trim()}${ml > 190 ? "..." : ""}")
        }
        logItems?.push("│ Ignoring (${headers?.cmdType}) Command... It Already Exists in QUEUE!!!")
        logItems?.push("┌────────── Echo Queue Warning ──────────")
        processLogItems("warn", logItems, true, true)
        return
    }
    state?.cmdQIndexNum = getQueueIndex()
    state?."cmdQueueItem_${state?.cmdQIndexNum}" = [type: type, headers: headers, body: body]
    if(headers?.message) {
        logItems?.push("│ Message(Len: ${headers?.message?.toString()?.length()}): ${headers?.message?.take(200)?.trim()}${headers?.message?.toString()?.length() > 200 ? "..." : ""}")
    }
    if(headers?.cmdType) { logItems?.push("│ CmdType: (${headers?.cmdType})") }
    logItems?.push("┌───── Added Echo Queue Item (${state?.cmdQIndexNum}) ─────")
    if(!firstRun) {
        processLogItems("trace", logItems, false, true) 
    }
}

private checkQueue(data) {
    // log.debug "checkQueue | ${data}"
    if(state?.qCmdCycleCnt && state?.qCmdCycleCnt?.toInteger() >= 10) {
        log.warn "checkQueue | Queue Cycle Count (${state?.qCmdCycleCnt}) is abnormally high... Resetting Queue"
        resetQueue(false)
        return
    }
    Boolean qEmpty = (getQueueSize() == 0)
    if(qEmpty) {
        log.trace "checkQueue | Nothing in the Queue... Resetting Queue"
        resetQueue(false)
        return
    }
    if(data && data?.rateLimited == true) {
        Integer delay = data?.delay as Integer ?: getRecheckDelay(state?.curMsgLen)
        schedQueueCheck(delay, true, null, "checkQueue(rate-limit)")
        log.debug "checkQueue | Scheduling Re-Check for ${delay} seconds...${(data && data?.rateLimited == true) ? " | Recheck for RateLimiting: true" : ""}"
    }
    processCmdQueue()
    return
}

private processCmdQueue() {
    state?.cmdQueueWorking = true
    state?.qCmdCycleCnt = state?.qCmdCycleCnt ? state?.qCmdCycleCnt+1 : 1
    Map cmdQueue = getQueueItems()
    if(cmdQueue?.size()) {
        state?.recheckScheduled = false
        def cmdKey = cmdQueue?.keySet()?.sort()?.first()
        Map cmdData = state[cmdKey as String]
        // logger("debug", "processCmdQueue | Key: ${cmdKey} | Queue Items: (${state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }?.size()})")
        cmdData?.headers['queueKey'] = cmdKey
        if(state?.lastTtsMsg && (cmdData?.headers?.message == state?.lastTtsMsg) && (getLastTtsCmdSec() < 10)) {
            state?.remove(cmdKey as String)
            log.trace "processCmdQueue | Possible loop detected... Last message the same as current sent less than 10 seconds ago. This message will be removed from the queue"
            schedQueueCheck(2, true, null, "processCmdQueue(removed duplicate)")
        } else {
            echoServiceCmd(cmdData?.type, cmdData?.headers, cmdData?.body, true)
        }
    }
    state?.cmdQueueWorking = false
}

Integer getAdjCmdDelay(elap, reqDelay) {
    if(elap && reqDelay) {
        Integer res = (elap - reqDelay)?.abs()
        log.debug "getAdjCmdDelay | reqDelay: $reqDelay | elap: $elap | res: ${res+3}"
        return res < 3 ? 3 : res+3
    } 
    return 5
}

private echoServiceCmd(type, headers={}, body = null, isQueueCmd=false) {
    if(!isQueueCmd) { log.trace "echoServiceCmd($type, ${headers?.cmdType}, $isQueueCmd)" }
    String host = state?.serviceHost
    List logItems = []
    String healthStatus = getHealthStatus()
    if(!host || !type || !headers || !(healthStatus in ["ACTIVE", "ONLINE"])) {
        if(!host || !type || !headers) {
            log.error "echoServiceCmd | Error${!host ? " | host is missing" : ""}${!type ? " | type is missing" : ""}${!headers ? " | headers are missing" : ""} "
        }
        if(!(healthStatus in ["ACTIVE", "ONLINE"])) { log.warn "Command Ignored... Device is current in OFFLINE State" }
        return true 
    }
    Boolean isTTS = (type == "cmd" && headers?.cmdType == "SendTTS")
    Integer lastTtsCmdSec = getLastTtsCmdSec()
    Boolean sendTheCmd = true
    if(!settings?.disableQueue && isTTS) {
        logItems?.push("│ Last TTS Sent: (${lastTtsCmdSec} seconds) ")
        Integer ml = headers?.message?.toString()?.length()
        Boolean isFirstRunCmd = (state?.firstCmdFlag != true)
        Boolean sendToQueue = (isFirstRunCmd || (!isQueueCmd && getQueueSize() >= 1))
        // log.debug "sendToQueue: $sendToQueue | lastTtsCmdSec: $lastTtsCmdSec | getRecheckDelay: ${getRecheckDelay(state?.curMsgLen)}"
        if(sendToQueue) {
            if(!isQueueCmd) {
                headers['msgDelay'] = getRecheckDelay(ml)
                if(isFirstRunCmd) { 
                    logItems?.push("│ First Command: (${isFirstRunCmd})")
                    state?.firstCmdFlag = true
                }
                queueEchoCmd(type, headers, body, isFirstRunCmd)
            }
            if(isFirstRunCmd) { processCmdQueue() }
            sendTheCmd = false 
        }
    }
    if(sendTheCmd) {
        try {
            String path = ""
            Map headerMap = [HOST: host, deviceId: device?.getDeviceNetworkId()]
            switch(type) {
                case "cmd":
                    path = "/alexa-command"
                    break
            }
            headers?.each { k,v-> headerMap[k] = v }
            def result = new physicalgraph.device.HubAction([
                    method: "POST",
                    headers: headerMap,
                    path: path,
                    body: body ?: ""
                ],
                null,
                [callback: cmdCallBackHandler]
            )
            logItems?.push("│ Queue Items: (${(getQueueSize()-1).abs()}) │ Working: (${state?.cmdQueueWorking})")
            if(body) { logItems?.push("│ Body: ${body}") }
            if(headers?.message) {
                Integer ml = headers?.message?.toString()?.length()
                Integer rcv = getRecheckDelay(ml)
                state?.curMsgLen = ml
                state?.lastTtsCmdDelay = rcv
                
                schedQueueCheck(rcv, true, null, "echoServiceCmd(sendHubCommand)")
                logItems?.push("│ Rechecking: (${state?.lastTtsCmdDelay} seconds)")
                logItems?.push("│ Message(${ml} char): ${headers?.message?.take(190)?.trim()}${ml > 190 ? "..." : ""}")
                state?.lastTtsMsg = headers.message
                state?.lastTtsCmdDt = getDtNow()
            }
            if(headers?.cmdType) { logItems?.push("│ Command: (${headers?.cmdType})") }
            if(state?.useHeroku == true) {
                try {
                    headerMap.remove("HOST")
                    Map params = [
                        uri: host,
                        headers: headerMap,
                        path: path,
                        body: body ?: [:]
                    ]
                    // log.debug "params: $params"
                    asynchttp_v1.post('asyncCommandHandler', params, [queueKey: headerMap?.queueKey ?: null])
                } catch (e) {
                    log.debug "something went wrong: $e"
                }
            } else {
                sendHubCommand(result)
            }
            
            logItems?.push("┌─────── Echo Command ${isQueueCmd && !settings?.disableQueue ? " (From Queue) " : ""} ────────")
            processLogItems("debug", logItems)
        }
        catch (Exception ex) {
            log.error "echoServiceCmd HubAction Exception:", ex
        }
    }
}

void cmdCallBackHandler(physicalgraph.device.HubResponse hubResponse) {
    def resp = hubResponse?.json
    if(resp && resp?.deviceId && (resp?.deviceId == device?.getDeviceNetworkId())) {
        // log.debug "command resp was: ${resp}"
        if(resp?.statusCode == 200) {
            if(resp?.queueKey) {
                log.info "commands sent successfully | queueKey: ${resp?.queueKey} | msgDelay: ${resp?.msgDelay}"
                state?.remove(resp?.queueKey as String)
                schedQueueCheck(getAdjCmdDelay(getLastTtsCmdSec(), state?.lastTtsCmdDelay), true, null, "cmdCallBackHandler(adjDelay)")
            }
            return
        } else if(resp?.statusCode == 400 && resp?.message && resp?.message == "Rate exceeded") {
            log.warn "You are being Rate-Limited by Amazon... | A retry will occue in 2 seconds"
            state?.recheckScheduled = true
            runIn(3, "checkQueue", [overwrite: true, data:[rateLimited: true, delay: (resp?.msgDelay ?: getRecheckDelay(state?.curMsgLen))]])
            return
        } else {
            log.error "calledBackHandler Error | status: ${resp?.statusCode} | message: ${resp?.message}"
            resetQueue()
            return
        }
    }
}

def asyncCommandHandler(response, data) {
    Map resp = response?.json
    Integer statusCode = response?.status
    log.debug "resp: $resp"
    if(resp && resp?.deviceId && (resp?.deviceId == device?.getDeviceNetworkId())) {
        // log.debug "command resp was: ${resp}"
        if(statusCode == 200) {
            if(resp?.queueKey) {
                log.info "commands sent successfully | queueKey: ${resp?.queueKey} | msgDelay: ${resp?.msgDelay}"
                state?.remove(resp?.queueKey as String)
                schedQueueCheck(getAdjCmdDelay(getLastTtsCmdSec(), state?.lastTtsCmdDelay), true, null, "cmdCallBackHandler(adjDelay)")
            }
            return
        } else if(statusCode == 400 && resp?.message && resp?.message == "Rate exceeded") {
            log.warn "You are being Rate-Limited by Amazon... | A retry will occue in 2 seconds"
            state?.recheckScheduled = true
            runIn(3, "checkQueue", [overwrite: true, data:[rateLimited: true, delay: (resp?.msgDelay ?: getRecheckDelay(state?.curMsgLen))]])
            return
        } else {
            log.error "asyncCommandHandler Error | status: ${statusCode} | message: ${resp?.message}"
            resetQueue()
            return
        }
    }
}

/*****************************************************
                HELPER FUNCTIONS
******************************************************/
def getDtNow() {
	def now = new Date()
	return formatDt(now, false)
}

def formatDt(dt, mdy = true) {
	def formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
	def tf = new SimpleDateFormat(formatVal)
	if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
	return tf.format(dt)
}

def GetTimeDiffSeconds(strtDate, stpDate=null) {
	if((strtDate && !stpDate) || (strtDate && stpDate)) {
		def now = new Date()
		def stopVal = stpDate ? stpDate.toString() : formatDt(now, false)
		def start = Date.parse("E MMM dd HH:mm:ss z yyyy", strtDate).getTime()
		def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", stopVal).getTime()
		def diff = (int) (long) (stop - start) / 1000
		return diff
	} else { return null }
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
