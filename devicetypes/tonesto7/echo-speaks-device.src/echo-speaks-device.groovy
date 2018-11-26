/**
 *	Echo Speaks Device
 *
 *  Copyright 2018 Anthony Santilli
 *  M
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
String devVersion() { return "1.4.0"}
String devModified() { return "2018-11-26" }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli", mnmn:"SmartThings", vid:"generic-music-player") {
        capability "Sensor"
        capability "Refresh"
        capability "Audio Mute"
        capability "Audio Volume"
        capability "Music Player"
        capability "Notification"
        capability "Speech Synthesis"

        attribute "lastUpdated", "string"
        attribute "deviceStatus", "string"
        attribute "deviceType", "string"
        attribute "deviceStyle", "string"
        attribute "doNotDisturb", "string"
        attribute "firmwareVer", "string"
        attribute "onlineStatus", "string"
        attribute "currentStation", "string"
        attribute "currentAlbum", "string"
        attribute "lastSpeakCmd", "string"
        attribute "lastCmdSentDt", "string"
        attribute "trackImage", "string"
        attribute "alarmVolume", "number"
        attribute "alexaWakeWord", "string"
        attribute "wakeWords", "enum"
        attribute "alexaPlaylists", "JSON_OBJECT"
        attribute "alexaNotifications", "JSON_OBJECT"
        attribute "alexaMusicProviders", "JSON_OBJECT"
        attribute "volumeSupported", "string"
        attribute "ttsSupported", "string"
        attribute "musicSupported", "string"
        command "sendTestTts"
        command "replayText"
        command "doNotDisturbOn"
        command "doNotDisturbOff"
        command "setVolumeAndSpeak"
        command "setAlarmVolume"
        command "resetQueue"
        command "playWeather"
        command "playSingASong"
        command "playFlashBrief"
        command "playGoodMorning"
        command "playTraffic"
        command "playTellStory"
        command "searchMusic"
        command "searchAmazonMusic"
        command "searchPandora"
        command "searchIheart"
        command "searchSpotify"
        command "searchTuneIn"
        command "createAlarm"
        command "createReminder"
        command "removeNotification"
        command "setWakeWord"
        command "storeCurrentVolume"
        command "restoreLastVolume"
        command "setVolumeSpeakAndRestore"
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

            state("paused_echo_plus_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_plus_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_plus_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen1.png")

            state("paused_echo_plus_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_plus_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_plus_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_plus_gen2.png")

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

            state("paused_echo_show_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_show_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_show_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen1.png")

            state("paused_echo_show_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png", backgroundColor: "#cccccc")
            state("playing_echo_show_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_echo_show_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_show_gen2.png")

            state("paused_amazon_tablet", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png", backgroundColor: "#cccccc")
            state("playing_amazon_tablet", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png", backgroundColor: "#00a0dc")
            state("stopped_amazon_tablet", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/amazon_tablet.png")

            state("paused_echo_sub_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_sub_gen1.png", backgroundColor: "#cccccc")
            state("playing_echo_sub_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_sub_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_echo_sub_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_sub_gen1.png")

            state("paused_firetv_cube", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_cube.png", backgroundColor: "#cccccc")
            state("playing_firetv_cube", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_cube.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_cube", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_cube.png")

            state("paused_firetv_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen1.png", backgroundColor: "#cccccc")
            state("playing_firetv_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen1.png")

            state("paused_firetv_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen2.png", backgroundColor: "#cccccc")
            state("playing_firetv_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen2.png")

            state("paused_firetv_gen3", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen3.png", backgroundColor: "#cccccc")
            state("playing_firetv_gen3", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen3.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_gen3", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_gen3.png")

            state("paused_tablet_hd10", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/tablet_hd10.png", backgroundColor: "#cccccc")
            state("playing_tablet_hd10", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/tablet_hd10.png", backgroundColor: "#00a0dc")
            state("stopped_tablet_hd10", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/tablet_hd10.png")

            state("paused_firetv_stick_gen1", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen1.png", backgroundColor: "#cccccc")
            state("playing_firetv_stick_gen1", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen1.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_stick_gen1", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen1.png")

            state("paused_firetv_stick_gen2", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen2.png", backgroundColor: "#cccccc")
            state("playing_firetv_stick_gen2", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen2.png", backgroundColor: "#00a0dc")
            state("stopped_firetv_stick_gen2", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/firetv_stick_gen2.png")

            state("paused_echo_wha", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_wha.png", backgroundColor: "#cccccc")
            state("playing_echo_wha", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_wha.png", backgroundColor: "#00a0dc")
            state("stopped_echo_wha", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_wha.png")

            state("paused_sonos_generic", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_generic.png", backgroundColor: "#cccccc")
            state("playing_sonos_generic", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_generic.png", backgroundColor: "#00a0dc")
            state("stopped_sonos_generic", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_generic.png")

            state("paused_sonos_beam", label:"Paused", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_beam.png", backgroundColor: "#cccccc")
            state("playing_sonos_beam", label:"Playing", action:"music Player.pause", nextState: "paused", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_beam.png", backgroundColor: "#00a0dc")
            state("stopped_sonos_beam", label:"Stopped", action:"music Player.play", nextState: "playing", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sonos_beam.png")
        }
        valueTile("blank1x1", "device.blank", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state("default", label:'')
        }
        valueTile("blank2x1", "device.blank", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("default", label:'')
        }
        valueTile("blank2x2", "device.blank", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state("default", label:'')
        }
        valueTile("alarmVolume", "device.alarmVolume", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("alarmVolume", label:'Alarm Volume:\n${currentValue}%')
        }
        valueTile("volumeSupported", "device.volumeSupported", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("volumeSupported", label:'Volume Supported:\n${currentValue}')
        }
        valueTile("ttsSupported", "device.ttsSupported", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("ttsSupported", label:'TTS Supported:\n${currentValue}')
        }
        valueTile("deviceStyle", "device.deviceStyle", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("deviceStyle", label:'Device Style:\n${currentValue}')
        }
        valueTile("onlineStatus", "device.onlineStatus", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("onlineStatus", label:'Online Status:\n${currentValue}')
        }
        valueTile("currentStation", "device.currentStation", height: 1, width: 3, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Station:\n${currentValue}')
        }
        valueTile("currentAlbum", "device.currentAlbum", height: 1, width: 3, inactiveLabel: false, decoration: "flat") {
            state("default", label:'Album:\n${currentValue}')
        }
        valueTile("lastSpeakCmd", "device.lastSpeakCmd", height: 2, width: 6, inactiveLabel: false, decoration: "flat") {
            state("lastSpeakCmd", label:'Last Text Sent:\n${currentValue}')
        }
        valueTile("lastCmdSentDt", "device.lastCmdSentDt", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state("lastCmdSentDt", label:'Last Text Sent:\n${currentValue}')
        }
        valueTile("alexaWakeWord", "device.alexaWakeWord", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("alexaWakeWord", label:'Wake Word:\n${currentValue}')
        }
        standardTile("sendTest", "sendTest", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Send Test TTS', action: 'sendTestTts')
        }
        standardTile("playWeather", "playWeather", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Weather Report', action: 'playWeather')
        }
        standardTile("playSingASong", "playSingASong", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Sing-A-Song', action: 'playSingASong')
        }
        standardTile("playFlashBrief", "playFlashBrief", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Flash Briefing', action: 'playFlashBrief')
        }
        standardTile("playGoodMorning", "playGoodMorning", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Good Morning', action: 'playGoodMorning')
        }
        standardTile("playTraffic", "playTraffic", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Traffic', action: 'playTraffic')
        }
        standardTile("playTellStory", "playTellStory", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Tell-a-Story', action: 'playTellStory')
        }
        standardTile("searchTest", "searchTest", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Search Test', action: 'searchTest')
        }
        standardTile("resetQueue", "resetQueue", height: 1, width: 2, decoration: "flat") {
            state("default", label:'Reset Queue', action: 'resetQueue')
        }
        standardTile("doNotDisturb", "device.doNotDisturb", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state "true", label: 'DnD: ON', action: "doNotDisturbOff", nextState: "false"
            state "false", label: 'DnD: OFF', action: "doNotDisturbOn", nextState: "true"
        }
        main(["deviceStatus"])
        details([
            "mediaMulti", "currentAlbum", "currentStation", "dtCreated", "deviceFamily", "deviceStyle", "onlineStatus", "alarmVolume", "volumeSupported", "alexaWakeWord", "ttsSupported",
            "playWeather", "playSingASong", "playFlashBrief", "playGoodMorning", "playTraffic", "playTellStory", "sendTest", "doNotDisturb", "resetQueue", 
            "lastSpeakCmd", "blank2x2", "lastCmdSentDt", "blank2x2"])
    }
    
    preferences {
        section("Preferences") {
            input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
            input "disableQueue", "bool", required: false, title: "Don't Allow Queuing?", defaultValue: false
            input "restoreVolDelay", "number", required: false, title: "Restore Volume Delay (Seconds)", defaultValue: 10
        }
    }
}

def installed() {
    log.trace "${device?.displayName} Executing Installed..."
    sendEvent(name: "level", value: 0)
    sendEvent(name: "volume", value: 0)
    sendEvent(name: "alarmVolume", value: 0)
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "deviceStatus", value: "stopped_echo_gen1")
    sendEvent(name: "trackDescription", value: "")
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    sendEvent(name: "onlineStatus", value: "online")
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

void updateDeviceStatus(Map devData) {
    try {
        String devName = getShortDevName()
        if(devData?.size()) {
            // log.debug "deviceFamily: ${devData?.deviceFamily} | deviceType: ${devData?.deviceType}"  // UNCOMMENT to identify unidentified devices
            
            // NOTE: These allow you to log all device data items
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
            state?.serialNumber = devData?.serialNumber
            state?.deviceType = devData?.deviceType
            state?.deviceOwnerCustomerId = devData?.deviceOwnerCustomerId
            state?.cookie = devData?.cookie
            state?.amazonDomain = devData?.amazonDomain
            state?.permissions = devData?.permissionMap
            log.debug "permissions: ${state?.permissions}"
            Boolean canPlayMusic = (state?.permissions?.canPlayMusic == true)
            Boolean ttsSupported = (state?.permissions?.ttsSupport == true)
            Boolean volumeSupported = (state?.permissions?.volumeControl == true)
            Boolean alarmsSupported = (state?.permissions?.allowAlarms == true)
            Boolean remindersSupported = (state?.permissions?.allowReminders == true)
            state?.allowAlarms = (devData?.allowAlarms == true)
            state?.allowReminders = (devData?.allowReminders == true)

            if(isStateChange(device, "volumeSupported", volumeSupported?.toString())) {
                sendEvent(name: "volumeSupported", value: volumeSupported, display: false, displayed: false)
            }
            if(isStateChange(device, "musicSupported", canPlayMusic?.toString())) {
                sendEvent(name: "musicSupported", value: canPlayMusic, display: false, displayed: false)
            }
            if(isStateChange(device, "ttsSupported", ttsSupported?.toString())) {
                sendEvent(name: "ttsSupported", value: ttsSupported, display: false, displayed: false)
            }
            state?.serviceAuthenticated = (devData?.serviceAuthenticated == true)
            Map deviceStyle = devData?.deviceStyle
            state?.deviceStyle = devData?.deviceStyle
            // logger("info", "deviceStyle (${devData?.deviceFamily}): ${devData?.deviceType} | Desc: ${deviceStyle?.name}")
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

            if(devFamily != "WHA") { 
                getDndStatus()
                getDeviceState()
                getWakeWord() 
                getAvailableWakeWords()
                
                // getMusicProviders()
                // getPlaylists()

                //getNotifications()
            }
            if(alarmsSupported || remindersSupported) { 
                getAlarmVolume() 
                
            }

            

            // def alarms = devData?.notifications
            // // if(alarms?.size()) { delete alarms["deviceSerialNumber"] }
            // if(isStateChange(device, "alexaNotifications", alarms?.toString())) {
            //     sendEvent(name: "alexaNotifications", value: alarms, display: false, displayed: false)
            // }
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

private getDeviceState() {
    try {
        asynchttp_v1.get(updateDeviceState, [
            uri: "https://alexa.${state?.amazonDomain}",
            path: "/api/np/player",
            query: [
                deviceSerialNumber: state?.serialNumber,
                deviceType: state?.deviceType,
                screenWidth: 2560
            ],
            headers: [
                "Cookie": state?.cookie?.cookie as String, 
                "csrf": state?.cookie?.csrf as String
            ],
            requestContentType: "application/json",
            contentType: "application/json",
        ])
    } catch (ex) {
        log.error "getDeviceState ERROR: ", ex
    }
}

def updateDeviceState(response, data) {
    def sData = [:]
    if (response.hasError()) {
        log.debug "updateDeviceState error parsing json - raw error data is $response.errorData"
    } else { 
        sData = response?.json
        sData = sData?.playerInfo ?: [:]
    }
    // log.trace "updateDeviceState: ${sData}"
    String playState = sData?.state == 'PLAYING' ? "playing" : "stopped"
    String deviceStatus = "${playState}_${state?.deviceStyle?.image}"
    // log.debug "deviceStatus: ${deviceStatus}"
    if(isStateChange(device, "status", playState?.toString()) || isStateChange(device, "deviceStatus", deviceStatus?.toString())) {
        sendEvent(name: "status", value: playState?.toString(), descriptionText: "Player Status is ${playState}", display: true, displayed: true)
        sendEvent(name: "deviceStatus", value: deviceStatus?.toString(), display: false, displayed: false)
    }
    //Track Title
    String title = sData?.infoText?.title ?: ""
    if(isStateChange(device, "trackDescription", title?.toString())) {
        sendEvent(name: "trackDescription", value: title?.toString(), descriptionText: "Track Description is ${title}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText1 = sData?.infoText?.subText1 ?: "Idle"
    if(isStateChange(device, "currentAlbum", subText1?.toString())) {
        sendEvent(name: "currentAlbum", value: subText1?.toString(), descriptionText: "Album is ${subText1}", display: true, displayed: true)
    }
    //Track Sub-Text2
    String subText2 = sData?.infoText?.subText2 ?: "Idle"
    if(isStateChange(device, "currentStation", subText2?.toString())) {
        sendEvent(name: "currentStation", value: subText2?.toString(), descriptionText: "Station is ${subText2}", display: true, displayed: true)
    }

    //Track Art Imager
    String trackImg = sData?.mainArt?.url ?: ""
    if(isStateChange(device, "trackImage", trackImg?.toString())) {
        sendEvent(name: "trackImage", value: trackImg?.toString(), descriptionText: "Track Image is ${trackImg}", display: false, displayed: false)
    }
    
    if(sData?.volume) {
        if(sData?.volume?.volume) {
            Integer level = sData?.volume?.volume
            if(level < 0) { level = 0 }
            if(level > 100) { level = 100 }
            if(isStateChange(device, "level", level?.toString()) || isStateChange(device, "volume", level?.toString())) {
                sendEvent(name: "level", value: level, display: false, displayed: false)
                sendEvent(name: "volume", value: level, display: false, displayed: false)
            }
        }
        if(sData?.volume?.muted) {
            String muteState = sData?.volume?.muted == true ? "muted" : "unmuted"
            if(isStateChange(device, "mute", muteState?.toString())) {
                sendEvent(name: "mute", value: muteState, descriptionText: "Mute State is ${muteState}", display: true, displayed: true)
            }
        }
    }
}

private getAlarmVolume() {
    Map params = [
        uri: "https://alexa.${state?.amazonDomain}",
        path: "/api/device-notification-state/${state?.deviceType}/${device.currentState("firmwareVer")?.stringValue}/${state.serialNumber}",
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    asynchttp_v1.get(updateAlarmVolume, params)
}

def updateAlarmVolume(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updateAlarmVolume error parsing json - raw error data is $response.errorData"
    }
    // log.debug "sData: $sData"
    if(isStateChange(device, "alarmVolume", (sData?.alarmVolume ?: 0)?.toString())) {
        sendEvent(name: "alarmVolume", value: sData?.alarmVolume, display: false, displayed: false)
    }
}

private getWakeWord() {
    Map params = [
        uri: "https://alexa.${state?.amazonDomain}",
        path: "/api/wake-word",
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    asynchttp_v1.get(updateWakeWord, params)
}

def updateWakeWord(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updateWakeWord error parsing json - raw error data is $response.errorData"
    }
    // log.debug "sData: $sData"
    def wakeWord = sData?.wakeWords?.find { it?.deviceSerialNumber == state?.serialNumber } ?: null
    // log.trace "updateWakeWord: ${wakeWord?.wakeWord}"
    if(isStateChange(device, "alexaWakeWord", wakeWord?.wakeWord?.toString())) {
        sendEvent(name: "alexaWakeWord", value: wakeWord?.wakeWord, display: false, displayed: false)
    }
}

private getAvailableWakeWords() {
    Map params = [
        uri: "https://alexa.${state?.amazonDomain}",
        path: "/api/wake-words-locale",
        query: [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            softwareVersion: device.currentValue('firmwareVer')
        ],
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    asynchttp_v1.get(updateAvailableWakeWords, params)
}

def updateAvailableWakeWords(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updateAvailableWakeWords error parsing json - raw error data is $response.errorData"
    } 
    def wakeWords = sData?.wakeWords ?: []
    // log.trace "updateAvailableWakeWords: ${wakeWords}"
    if(isStateChange(device, "wakeWords", wakeWords?.toString())) {
        sendEvent(name: "wakeWords", value: wakeWords, display: false, displayed: false)
    }
}

private getDndStatus() {
    try {
        Map params = [
            uri: "https://alexa.${state?.amazonDomain}",
            path: "/api/dnd/device-status-list",
            headers: [
                "Cookie": state?.cookie?.cookie as String, 
                "csrf": state?.cookie?.csrf as String
            ],
            requestContentType: "application/json",
            contentType: "application/json",
        ]
        asynchttp_v1.get(updateDndStatus, params)
    } catch(ex) {
        log.error "getDndStatus ERROR: ", ex
    }
}

def updateDndStatus(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updateDndStatus error parsing json - raw error data is $response.errorData"
    } 
    def dndData = sData?.doNotDisturbDeviceStatusList?.size() ? sData?.doNotDisturbDeviceStatusList?.find { it?.deviceSerialNumber == state?.serialNumber } : [:]
    // log.debug "dndData: $dndData"
    if(isStateChange(device, "doNotDisturb", (dndData?.enabled == true)?.toString())) {
        log.debug "Do Not Disturb: ${(dndData?.enabled == true)}"
        sendEvent(name: "doNotDisturb", value: (dndData?.enabled == true)?.toString(), descriptionText: "Do Not Disturb Enabled ${(dndData?.enabled == true)}", display: true, displayed: true)
    }
}

private getPlaylists() {
    asynchttp_v1.get(updatePlaylists,[
        uri: "https://alexa.${state?.amazonDomain}",
        path: "/api/cloudplayer/playlists",
        query: [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            mediaOwnerCustomerId: state?.deviceOwnerCustomerId,
            screenWidth: 2560
        ],
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json",
    ])
}

def updatePlaylists(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updatePlaylists error parsing json - raw error data is $response.errorData"
    } 
    // log.trace "updatePlaylists: ${sData}"
    Map playlists = sData?.playlists ?: [:]
    if(isStateChange(device, "alexaPlaylists", playlists?.toString())) {
        sendEvent(name: "alexaPlaylists", value: playlists, display: false, displayed: false)
    }
}

private getMusicProviders() {
    asynchttp_v1.get(updateMusicProviders, [
        uri: "https://alexa.${state?.amazonDomain}",
        path: "/api/behaviors/entities",
        query: [ skillId: "amzn1.ask.1p.music" ],
        headers: [
            "Routines-Version": '1.1.210292',
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json"
    ])
}

def updateMusicProviders(response, data) {
    def sData = response?.json
    if (response.hasError()) {
        log.debug "updateMusicProviders error parsing json - raw error data is $response.errorData"
    } 
    log.trace "updateMusicProviders: ${sData}"
    Map items = [:]
    if(sData && resp?.size()) {
        sData?.findAll { it?.availability == "AVAILABLE" }?.each { item->
            items[item?.id] = item?.displayName
        }
    }
    Map musicProviders = items?.musicProviders ? (items?.musicProviders instanceof List ? items?.musicProviders[0] : items?.musicProviders) : [:]
    if(isStateChange(device, "alexaMusicProviders", musicProviders?.toString())) {
        sendEvent(name: "alexaMusicProviders", value: musicProviders, display: false, displayed: false)
    }
}

private getNotifications() {
    def resp = makeSyncronousReq([
        uri: "https://alexa.${state?.amazonDomain}", 
        path: "/api/notifications",
        query: [cached: true ],
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json"
    ], "GET", "Notifications", true)
    List newList = []
    if(resp) {
        List items = resp?.notifications ? resp?.notifications?.findAll { it?.status == "ON" } : []
        items?.each { item->
            Map li = [:]
            item?.keySet().each { key->
                if(key in ['id', 'reminderLabel', 'originalDate', 'originalTime', 'deviceSerialNumber', 'type', 'remainingDuration']) {
                    li[key] = item[key]
                }
            }
            newList?.push(li)
        }
    }
    return newList
}

// let sendSequenceCommand = function(device, command, value, config, callback) {
//     if (typeof value === 'function') {
//         callback = value;
//         value = null;
//     }
//     let seqCommandObj;
//     if (typeof command === 'object') {
//         seqCommandObj = command.sequence || command;
//     } else {
//         seqCommandObj = {
//             '@type': 'com.amazon.alexa.behaviors.model.Sequence',
//             'startNode': createSequenceNode(device, command, value)
//         };
//     }

//     const reqObj = {
//         'behaviorId': seqCommandObj.sequenceId ? command.automationId : 'PREVIEW',
//         'sequenceJson': JSON.stringify(seqCommandObj),
//         'status': 'ENABLED'
//     };
//     request({
//         method: 'POST',
//         url: alexaUrl + '/api/behaviors/preview',
//         headers: {
//             'Cookie': config.cookies,
//             'csrf': config.csrf
//         },
//         json: reqObj
//     }, function(error, response) {
//         if (!error && response.statusCode === 200) {
//             callback(null, {
//                 "message": response
//             });
//         } else {
//             callback(error, response);
//         }
//     });
// };

// let sequenceJsonBuilder = function(serial, devType, custId, cmdKey, cmdVal) {
//     let device = {
//         deviceSerialNumber: serial,
//         deviceType: devType,
//         deviceOwnerCustomerId: custId,
//         locale: 'en-US'
//     };
//     const reqObj = {
//         'behaviorId': 'PREVIEW',
//         'sequenceJson': JSON.stringify({
//             '@type': 'com.amazon.alexa.behaviors.model.Sequence',
//             'startNode': createSequenceNode(device, cmdKey, cmdVal)
//         }),
//         'status': 'ENABLED'
//     };
//     return reqObj;
// };


// private createSequenceNode(device, command, value, callback) {
//     Map seqNode = [
//         '@type': 'com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode',
//         'operationPayload': [
//             'deviceType': device.deviceType,
//             'deviceSerialNumber': device.deviceSerialNumber,
//             'locale': device.locale,
//             'customerId': device.deviceOwnerCustomerId
//         ]
//     ];
//     switch (command) {
//         case 'weather':
//             seqNode?.type = 'Alexa.Weather.Play';
//             break;
//         case 'traffic':
//             seqNode?.type = 'Alexa.Traffic.Play';
//             break;
//         case 'flashbriefing':
//             seqNode?.type = 'Alexa.FlashBriefing.Play';
//             break;
//         case 'goodmorning':
//             seqNode?.type = 'Alexa.GoodMorning.Play';
//             break;
//         case 'singasong':
//             seqNode?.type = 'Alexa.SingASong.Play';
//             break;
//         case 'tellstory':
//             seqNode?.type = 'Alexa.TellStory.Play';
//             break;
//         case 'playsearch':
//             seqNode?.type = 'Alexa.Music.PlaySearchPhrase';
//             break;
//         case 'volume':
//             seqNode?.type = 'Alexa.DeviceControls.Volume';
//             value = ~~value;
//             // if (value < 0 || value > 100) {
//             //     return callback(new Error('Volume needs to be between 0 and 100'));
//             // }
//             seqNode.operationPayload.value = value;
//             break;
//         case 'speak':
//             seqNode?.type = 'Alexa.Speak';
//             if (!(value instanceof String)) value = value as String;
//             if (value.length === 0) {
//                 return callback && callback(new Error('Can not speak empty string', null));
//             }
//             // if (value.length > 250) {
//             //     return callback && callback(new Error('text too long, limit are 250 characters', null));
//             // }
//             seqNode.operationPayload.textToSpeak = value;
//             break;
//         default:
//             return;
//     }
//     return seqNode;
// };

def play() {
    logger("trace", "play() command received...")
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
    if(state?.serialNumber) {
        echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "PlayCommand"
        ])
        incrementCntByKey("use_cnt_playCmd")
        if(isStateChange(device, "status", "playing")) {
            sendEvent(name: "status", value: "playing", descriptionText: "Player Status is playing", display: true, displayed: true)
        }
    } else { log.warn "play() Command Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def playTrack(track) {
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
    logger("warn", "playTrack() | Not Supported Yet!!!")
}

def pause() {
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
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
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
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
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
    if(state?.serialNumber) {
        echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "PreviousCommand"
        ])
        incrementCntByKey("use_cnt_prevTrackCmd")
    } else { log.warn "previousTrack() Command Error | You are missing a SerialNumber: ${state?.serialNumber}" }
}

def nextTrack() {
    logger("trace", "nextTrack() command received...")
    if(!state?.permissions?.canPlayMusic) { log.warn "This Device Does NOT Support Media Playback Control!!!"; return; }
    if(state?.serialNumber) {
        echoServiceCmd("cmd", [
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            cmdType: "NextCommand"
        ])
        incrementCntByKey("use_cnt_nextTrackCmd")
    } else { log.warn "nextTrack() Command Error | You are missing a SerialNumber: ${state?.serialNumber}" }
}

def mute() {
    logger("trace", "mute() command received...")
    if(!state?.permissions?.volumeControl) { log.warn "This Device Does NOT Support Volume Control!!!"; return; }
    if(state?.serialNumber) {
        state.muteLevel = device?.currentState("level")?.integerValue
        incrementCntByKey("use_cnt_muteCmd")
        if(isStateChange(device, "mute", "muted")) {
            sendEvent(name: "mute", value: "muted", descriptionText: "Mute is set to muted", display: true, displayed: true)
        }
        setLevel(0)
    } else { log.warn "mute() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def unmute() {
    logger("trace", "unmute() command received...")
    if(!state?.permissions?.volumeControl) { log.warn "This Device Does NOT Support Volume Control!!!"; return; }
    if(state?.serialNumber) {
        if(state?.muteLevel) {
            setLevel(state?.muteLevel)
            state?.muteLevel = null
            incrementCntByKey("use_cnt_unmuteCmd")
            if(isStateChange(device, "mute", "unmuted")) {
                sendEvent(name: "mute", value: "unmuted", descriptionText: "Mute is set to unmuted", display: true, displayed: true)
            }
        }
    } else { log.warn "unmute() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

def setMute(muteState) {
    if(muteState) {
        if(muteState == "muted") { 
            mute() 
        } else {
            unmute()
        }
    }
}

def setLevel(level) {
    logger("trace", "setVolume($level) command received...")
    if(!state?.permissions?.volumeControl) { log.warn "This Device Does NOT Support Volume Control!!!"; return; }
    if(state?.serialNumber && level>=0 && level<=100) {
        if(volume != device?.currentState('level')?.integerValue) {
            doSequenceCmd("VolumeCommand", "volume", level)
            incrementCntByKey("use_cnt_volumeCmd")
            sendEvent(name: "level", value: level, display: false, displayed: false)
            sendEvent(name: "volume", value: level, display: false, displayed: false)
        }
    } else { log.warn "setLevel() Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Level: ${level}" }
}

def setAlarmVolume(volume) {
    logger("trace", "setAlarmVolume($level) command received...")
    if(volume) {
        echoServiceCmd("cmd", [
            cmdType: "AlarmVolume",
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            softwareVersion: device?.currentValue('firmwareVer'),
            volumeLevel: volume
        ])
        incrementCntByKey("use_cnt_alarmVolumeCmd")
        sendEvent(name: "alarmVolume", value: volume, display: false, displayed: false)
    }
}

def setVolume(volume) {
    setLevel(volume)
}

def volumeUp() {
    Integer curVol = device?.currentValue('level')
    if(curVol < 100) { setVolume(curVol+1) }
}

def volumeDown() {
    Integer curVol = device?.currentValue('level')
    if(curVol > 0) { setVolume(curVol-1) }
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
}

def doNotDisturbOff() {
    setDoNotDisturb(false)
}

def doNotDisturbOn() {
    setDoNotDisturb(true)
}

def setDoNotDisturb(Boolean val) {
    logger("trace", "setDoNotDisturb($val) command received...")
    echoServiceCmd("cmd", [
        cmdType: "SetDoNotDisturb${val ? "On" : "Off"}",
        deviceSerialNumber: state?.serialNumber,
        deviceType: state?.deviceType,
        deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
        cmdValObj: [enabled: (val==true)].encodeAsJson()
    ])
    if(val) {
        incrementCntByKey("use_cnt_dndCmdOn")
    } else { incrementCntByKey("use_cnt_dndCmdOff") }
}

def deviceNotification(String msg) {
    if(!msg) { log.warn "No Message sent with deviceNotification($msg) command" }
    log.trace "deviceNotification(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
    incrementCntByKey("use_cnt_devNotif")
    speak(msg as String)
}

def setVolumeAndSpeak(Integer volume, String msg) {
    if(!state?.permissions?.ttsSupported) { log.warn "This Device Does NOT Support Text to Speech!!!"; return; }
    if(volume) { setVolume(volume) }
    incrementCntByKey("use_cnt_setVolSpeak")
    speak(msg)
}

def setVolumeSpeakAndRestore(Integer volume, String msg) {
    if(!state?.permissions?.ttsSupported) { log.warn "This Device Does NOT Support Text to Speech!!!"; return; }
    if(msg) {
        Integer restoreDelay = getRecheckDelay(msg?.toString()?.length())
        if(volume) { 
            storeLastVolume()
            setVolume(volume) 
            incrementCntByKey("use_cnt_setVolSpeakRestore")
        }
        speak(msg)
        if(volume && restoreDelay) { 
            log.debug "Scheduling Volume (${state?.lastVolume}) Restore in ${(restoreDelay + (settings?.restoreLastVolume ?: 10)?.toInteger())} seconds"
            runIn((settings?.restoreLastVolume ?: 10), "restoreLastVolume")
        }
    }
}

private storeLastVolume() {
    Integer curVol = device?.currentState('volume')?.integerValue
    if(curVol) { state?.lastVolume = curVol }
}

private restoreLastVolume() {
    if(state?.lastVolume) { setVolume(state?.lastVolume) }
}

def speak(String msg) {
    if(!state?.permissions?.ttsSupported) { log.warn "This Device Does NOT Support Text to Speech!!!"; return; }
    if(!msg) { log.warn "No Message sent with speak($msg) command" }
    // log.trace "speak(${msg?.toString()?.length() > 200 ? msg?.take(200)?.trim() +"..." : msg})"
    if(msg != null && state?.serialNumber) {
        doSequenceCmd("SpeakCommand", "speak", msg as String)
        incrementCntByKey("use_cnt_speak")
    } else { log.warn "speak Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} or Message: ${msg}" }
}

def playWeather() {
    doSequenceCmd("WeatherCommand", "weather")
    incrementCntByKey("use_cnt_playWeather")
}

def playTraffic() {
    doSequenceCmd("TrafficCommand", "traffic")
    incrementCntByKey("use_cnt_playTraffic")
}

def playSingASong() {
    doSequenceCmd("SingCommand", "singasong")
    incrementCntByKey("use_cnt_playSong")
}

def playFlashBrief() {
    doSequenceCmd("FlashCommand", "flashbriefing")
    incrementCntByKey("use_cnt_playBrief")
}

def playGoodMorning() {
    doSequenceCmd("GoodMorningCommand", "goodmorning")
    incrementCntByKey("use_cnt_playGoodMorning")
}

def playTellStory() {
    doSequenceCmd("StoryCommand", "tellstory")
    incrementCntByKey("use_cnt_playStory")
}

def searchMusic(String searchPhrase, String providerId) {
    doSearchMusicCmd(searchPhrase, providerId)
}

def searchAmazonMusic(String searchPhrase) {
    if(state?.permissions?.allowAmazonMusic == false) { log.warn "device does not support AMAZON MUSIC"; return }
    doSearchMusicCmd(searchPhrase, "AMAZON_MUSIC")
    incrementCntByKey("use_cnt_searchAmazon")
}

def searchTuneIn(String searchPhrase) {
    if(state?.permissions?.allowTuneIn == false) { log.warn "device does not support TUNE_IN"; return }
    doSearchMusicCmd(searchPhrase, "TUNE_IN")
    incrementCntByKey("use_cnt_searchTuneIn")
}

def searchPandora(String searchPhrase) {
    if(state?.permissions?.allowPandora == false) { log.warn "device does not support PANDORA"; return }
    doSearchMusicCmd(searchPhrase, "PANDORA")
    incrementCntByKey("use_cnt_searchPandora")
}

def searchSpotify(String searchPhrase) {
    if(state?.permissions?.allowSpotify == false) { log.warn "device does not support SPOTIFY"; return }
    doSearchMusicCmd(searchPhrase, "SPOTIFY")
    incrementCntByKey("use_cnt_searchSpotify")
}

def searchIheart(String searchPhrase) {
    if(state?.permissions?.allowIheart == false) { log.warn "device does not support I_HEART_RADIO"; return }
    doSearchMusicCmd(searchPhrase, "I_HEART_RADIO")
    incrementCntByKey("use_cnt_searchIheart")
}

private doSequenceCmd(cmdType, seqCmd, seqVal="") {
    if(state?.serialNumber) {
        def headers = [
            cmdType: "ExecuteSequence",
            cmdDesc: cmdType,
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            seqCmdKey: seqCmd,
            seqCmdVal: seqVal
        ]
        if(seqCmd == "speak") { headers.message = seqVal }
        echoServiceCmd("cmd", headers)
    } else { log.warn "doSequenceCmd Error | You are missing one of the following... SerialNumber: ${state?.serialNumber}" }
}

private doSearchMusicCmd(searchPhrase, musicProvId) {
    if(state?.permissions?.canPlayMusic == false) { log.warn "This device does not support music playback" }
    if(state?.serialNumber && searchPhrase && musicProvId) {
        echoServiceCmd("musicSearch", [
            cmdType: "MusicSearch",
            cmdDesc: "MusicSearch(${musicProvId})",
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            deviceOwnerCustomerId: state?.deviceOwnerCustomerId,
            searchPhrase: searchPhrase,
            providerId: musicProvId
        ])
        incrementCntByKey("use_cnt_searchMusic")
    } else { log.warn "doSearchMusicCmd Error | You are missing one of the following... SerialNumber: ${state?.serialNumber} | searchPhrase: ${searchPhrase} | musicProvider: ${musicProvId}" }
}

def setWakeWord(String newWord) {
    logger("trace", "setWakeWord($newWord) command received...")
    String oldWord = device?.currentValue('alexaWakeWord')
    def wwList = device?.currentValue('wakeWords') ?: []
    log.debug "newWord: $newWord | oldWord: $oldWord | wwList: $wwList (${wwList?.contains(newWord.toString()?.toUpperCase())})"
    if(oldWord && newWord && wwList && wwList?.contains(newWord.toString()?.toUpperCase())) {
        echoServiceCmd("wakeword", [
            cmdType: "SetWakeWord",
            oldWord: oldWord,
            newWord: newWord
        ])
        incrementCntByKey("use_cnt_setWakeWord")
        sendEvent(name: "alexaWakeWord", value: newWord?.toString()?.toUpperCase(), display: true, displayed: true)
    } else { log.warn "setWakeWord is Missing a Required Parameter!!!" }
}

def createAlarm(String alarmLbl, String alarmDate, String alarmTime) {
    logger("trace", "createAlarm($alarmLbl, $alarmDate, $alarmTime) command received...")
    if(alarmLbl && alarmDate && alarmTime) {
        echoServiceCmd("notification", [
            cmdType: "CreateAlarm",
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            label: alarmLbl?.toString()?.replaceAll(" ", ""),
            date: alarmDate,
            time: alarmTime,
            type: "Alarm"
        ])
        incrementCntByKey("use_cnt_createAlarm")
    } else { log.warn "createAlarm is Missing a Required Parameter!!!" }
}

def createReminder(String remLbl, String remDate, String remTime) {
    logger("trace", "createReminder($remLbl, $remDate, $remTime) command received...")
    if(remLbl && remDate && remTime) {
        echoServiceCmd("notification", [
            cmdType: "CreateReminder",
            deviceSerialNumber: state?.serialNumber,
            deviceType: state?.deviceType,
            label: remLbl?.toString()?.replaceAll(" ", ""),
            date: remDate,
            time: remTime,
            type: "Reminder"
        ])
    } else { log.warn "createReminder is Missing a Required Parameter!!!" }
}

def removeNotification(String id) {
    logger("trace", "removeNotification($id) command received...")
    if(id) {
        echoServiceCmd("rem_notification", [
            cmdType: "RemoveNotification",
            id: id
        ])
    } else { log.warn "removeNotification is Missing a Required Parameter!!!" }
}

def getRandomItem(items) {
    def list = new ArrayList<String>();
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()));
}

def replayText() {
    logger("trace", "replayText() command received...")
    String lastText = device?.currentState("lastSpeakCmd")?.stringValue
    if(lastText) { speak(lastText) } else { log.warn "Last Text was not found" }
}

def sendTestTts(ttsMsg) {
    if(!state?.permissions?.ttsSupported) { log.warn "This Device Does NOT Support Text to Speech!!!"; return; }
    log.trace "sendTestTts"
    List items = [
        "Testing Testing 1, 2, 3", 
        "Yay!, I'm Alive... Hopefully you can hear me speaking?", 
        "Being able to make me say whatever you want is the coolest thing since sliced bread!",
        "I said a hip hop, Hippie to the hippie, The hip, hip a hop, and you don't stop, a rock it out, Bubba to the bang bang boogie, boobie to the boogie To the rhythm of the boogie the beat, Now, what you hear is not a test, I'm rappin' to the beat", 
        "This is how we do it!. It's Friday night, and I feel alright. The party is here on the West side. So I reach for my 40 and I turn it up. Designated driver take the keys to my truck, Hit the shore 'cause I'm faded, Honeys in the street say, Monty, yo we made it!. It feels so good in my hood tonight, The summertime skirts and the guys in Khannye.",
        "Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Teenage Mutant Ninja Turtles, Heroes in a half-shell Turtle power!... They're the world's most fearsome fighting team (We're really hip!), They're heroes in a half-shell and they're green (Hey - get a grip!), When the evil Shredder attacks!!!, These Turtle boys don't cut him no slack!."
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
        String endSep = "└─────────────────────────────"
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

def resetQueue(showLog=true) {
    if(showLog) { log.trace "resetQueue()" }
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }
    cmdQueue?.each { cmdKey, cmdData ->
        state?.remove(cmdKey)
    }
    unschedule("checkQueue")
    state?.qCmdCycleCnt = null
    state?.speakingNow = false
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
        // log.debug "getAdjCmdDelay | reqDelay: $reqDelay | elap: $elap | res: ${res+3}"
        return res < 3 ? 3 : res+3
    } 
    return 5
}

private echoServiceCmd(type, headers={}, body = null, isQueueCmd=false) {
    if(!isQueueCmd) { log.trace "echoServiceCmd($type, ${headers?.cmdType}, $isQueueCmd)" }
    if(state?.serviceAuthenticated != true) { 
        log.warn "Echo Speaks service is no longer authenticated... Please login again and commands will resume as they should!!!" 
        return
    }
    String host = state?.serviceHost
    Map queryMap = [:]
    List logItems = []
    String healthStatus = getHealthStatus()
    if(!host || !type || !headers || !(healthStatus in ["ACTIVE", "ONLINE"])) {
        if(!host || !type || !headers) { log.error "echoServiceCmd | Error${!host ? " | host is missing" : ""}${!type ? " | type is missing" : ""}${!headers ? " | headers are missing" : ""} " }
        if(!(healthStatus in ["ACTIVE", "ONLINE"])) { log.warn "Command Ignored... Device is current in OFFLINE State" }
        return
    }
    Boolean isTTS = (type == "cmd" && headers?.seqCmdKey == "speak")
    Integer lastTtsCmdSec = getLastTtsCmdSec()
    if(!settings?.disableQueue && isTTS) {
        logItems?.push("│ Last TTS Sent: (${lastTtsCmdSec} seconds) ")
        Integer ml = headers?.message?.toString()?.length()
        Boolean isFirstCmd = (state?.firstCmdFlag != true)
        if(isFirstCmd) {
            logItems?.push("│ First Command: (${isFirstCmd})")
            state?.firstCmdFlag = true
        }
        
        Boolean sendToQueue = (lastTtsCmdSec < 3 || (state?.speakingNow && !isFirstCmd && !isQueueCmd))
        // log.warn "sendToQueue: $sendToQueue | isQueueCmd: $isQueueCmd | lastTtsCmdSec: $lastTtsCmdSec | isFirstCmd: ${(state?.firstCmdFlag != true)} | speakingNow: ${state?.speakingNow} | getRecheckDelay: ${getRecheckDelay(state?.curMsgLen)}"
        if(sendToQueue) {
            headers['msgDelay'] = getRecheckDelay(ml)
            if(!isQueueCmd) {
                queueEchoCmd(type, headers, body, isFirstCmd)
            }
            return
        }
    }
    try {
        state?.speakingNow = (isTTS == true)
        String path = ""
        String uriCmd = "POST"
        Map headerMap = [HOST: host, deviceId: device?.getDeviceNetworkId()]
        switch(type) {
            case "cmd":
                path = "/alexa-command"
                if(headers?.volumeLevel) { logItems?.push("│ Alarm Volume: (${headers?.volumeLevel})") }
                break
            case "musicSearch":
                if(headers?.searchPhrase) { logItems?.push("│ Search Phrase: (${headers?.searchPhrase})") }
                if(headers?.providerId) { logItems?.push("│ Music Provider: (${headers?.providerId})") }
                path = "/musicSearch"
                break
            case "notification":
                if(headers?.date) { logItems?.push("│ Date: (${headers?.date})") }
                if(headers?.time) { logItems?.push("│ Time: (${headers?.time})") }
                if(headers?.label) { logItems?.push("│ Label: (${headers?.label})") }
                if(headers?.type) { logItems?.push("│ Type: (${headers?.type})") }
                queryMap?.serialNumber = headers?.deviceSerialNumber
                queryMap?.deviceType = headers?.deviceType
                queryMap?.label = headers?.label
                queryMap?.type = headers?.type
                queryMap?.time = headers?.time
                queryMap?.date = headers?.date
                path = "/createNotification"
                break
            case "wakeword":
                if(headers?.oldWord) { logItems?.push("│ Old Wake Word: (${headers?.oldWord})") }
                if(headers?.newWord) { logItems?.push("│ New Wake Word: (${headers?.newWord})") }
                queryMap?.serialNumber = state?.serialNumber
                queryMap?.oldWord = headers?.oldWord
                queryMap?.newWord = headers?.newWord
                uriCmd = "PUT"
                path = "/setWakeWord"
                break
            case "rem_notification":
                if(headers?.id) { logItems?.push("│ Notification ID: (${headers?.id})") }
                uriCmd = "GET"
                queryMap?.id = headers?.id
                path = "/removeNotification"
                break
        }
        headers?.each { k,v-> headerMap[k] = v }
        def result = new physicalgraph.device.HubAction([
                method: uriCmd,
                headers: headerMap,
                path: path,
                query: queryMap,
                body: body ?: ""
            ],
            null,
            [callback: cmdCallBackHandler]
        )
        Integer qSize = getQueueSize()
        if(isTTS) { logItems?.push("│ Queue Items: (${qSize>=1 ? qSize-1 : 0}) │ Working: (${state?.cmdQueueWorking})") }
        if(body) { logItems?.push("│ Body: ${body}") }
        if(isTTS && headers?.message) {
            Integer ml = headers?.message?.toString()?.length()
            Integer rcv = getRecheckDelay(ml)
            state?.curMsgLen = ml
            state?.lastTtsCmdDelay = rcv
            
            schedQueueCheck(rcv, true, null, "echoServiceCmd(${state?.useHeroku ? "sendCloudCommand" : "sendHubCommand"})")
            logItems?.push("│ Rechecking: (${state?.lastTtsCmdDelay} seconds)")
            logItems?.push("│ Message(${ml} char): ${headers?.message?.take(190)?.trim()}${ml > 190 ? "..." : ""}")
            state?.lastTtsMsg = headers.message
            state?.lastTtsCmdDt = getDtNow()
        }
        if(headers?.seqCmdVal && !isTTS) { logItems?.push("│ Value: (${headers?.seqCmdVal})") }
        if(headers?.seqCmdKey) { logItems?.push("│ Action: (${headers?.seqCmdKey})") }
        if(headers?.cmdType) { logItems?.push("│ Command: (${headers?.cmdType})") }
        if(state?.useHeroku == true) {
            try {
                headerMap.remove("HOST")
                Map params = [
                    uri: host,
                    headers: headerMap,
                    path: path,
                    query: queryMap,
                    body: body ?: [:]
                ]
                asynchttp_v1."${uriCmd?.toLowerCase()}"('asyncCommandHandler', params, [queueKey: headerMap?.queueKey ?: null])
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
        if(state?.useHeroku) {
            incrementCntByKey("err_cloud_command")
        } else { incrementCntByKey("err_hub_command") }
    }
}

void cmdCallBackHandler(physicalgraph.device.HubResponse hubResponse) {
    def resp = hubResponse?.json
    postCmdProcess(resp, resp?.statusCode, false)
}

def asyncCommandHandler(response, data) {
    Map resp = response?.json ?: null
    Integer statusCode = response?.status
    postCmdProcess(resp, statusCode, true)
}

private postCmdProcess(resp, statusCode, isAsync=false) {
    if(resp && resp?.deviceId && (resp?.deviceId == device?.getDeviceNetworkId())) {
        // log.debug "command resp was: ${resp} | statusCode: ${statusCode}"
        if(statusCode == 200) {
            log.info "${resp?.cmdDesc ? "${resp?.cmdDesc}" : "Command"} Sent Successfully${resp?.queueKey ? " | queueKey: ${resp?.queueKey}" : ""}${resp?.msgDelay ? " | msgDelay: ${resp?.msgDelay}" : ""} | ${isAsync ? "(Cloud)" : "(LAN)"}"
            if(resp?.cmdDesc && resp?.cmdDesc == "SpeakCommand") {
                String lastMsg = state?.lastTtsMsg as String ?: "Nothing to Show Here..."
                sendEvent(name: "lastSpeakCmd", value: "${lastMsg}", descriptionText: "Last Speech text: ${lastMsg}", display: true, displayed: true)
                sendEvent(name: "lastCmdSentDt", value: "${state?.lastTtsCmdDt}", descriptionText: "Last Command Timestamp: ${state?.lastTtsCmdDt}", display: false, displayed: false)
            }
            if(resp?.queueKey) {
                state?.remove(resp?.queueKey as String)
            }
            if(resp?.cmdDesc && resp?.cmdDesc == "SpeakCommand") {
                schedQueueCheck(getAdjCmdDelay(getLastTtsCmdSec(), state?.lastTtsCmdDelay), true, null, "postCmdProcess(adjDelay) | ${isAsync ? "(Cloud)" : "(LAN)"}")
            }
            return
        } else if(statusCode == 400 && resp?.message && resp?.message == "Rate exceeded") {
            log.warn "You are being Rate-Limited by Amazon... | A retry will occue in 2 seconds"
            state?.recheckScheduled = true
            runIn(3, "checkQueue", [overwrite: true, data:[rateLimited: true, delay: (resp?.msgDelay ?: getRecheckDelay(state?.curMsgLen))]])
            return
        } else {
            log.error "postCmdProcess Error | status: ${statusCode} | message: ${resp?.message} | ${isAsync ? "(Cloud)" : "(LAN)"}"
            if(state?.useHeroku) {
                incrementCntByKey("err_cloud_commandPost")
            } else { incrementCntByKey("err_hub_commandPost") }
            resetQueue()
            return
        }
    }
}

private incrementCntByKey(String key) {
	long evtCnt = state?."${key}" ?: 0
	// evtCnt = evtCnt?.toLong()+1
	evtCnt++
	// logger("trace", "${key?.toString()?.capitalize()}: $evtCnt")
	state?."${key}" = evtCnt?.toLong()
}

public Map getDeviceMetrics() {
    Map out = [:]
    def cntItems = state?.findAll { it?.key?.startsWith("use_") }
    def errItems = state?.findAll { it?.key?.startsWith("err_") }
    if(cntItems?.size()) {
        out['usage'] = [:]
        cntItems?.each { k,v -> out?.usage[k?.toString()?.replace("use_", "") as String] = v as Integer ?: 0 }
    }
    if(errItems?.size()) {
        out['errors'] = [:]
        errItems?.each { k,v -> out?.errors[k?.toString()?.replace("err_", "") as String] = v as Integer ?: 0 }
    }
    return out
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
