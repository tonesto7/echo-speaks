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
String devVersion() { return "0.0.1"}
String devModified() { return "2018-08-31"}
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }

metadata {
    definition (name: "Echo Speaks Device", namespace: "tonesto7", author: "Anthony Santilli") {
        capability "Sensor"
        capability "Music Player"
        capability "Speech Synthesis"
        
        attribute "lastUpdated", "string"
        attribute "firmwareVer", "string"
    }

    preferences { 
        input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
    }

    tiles (scale: 2) {
        multiAttributeTile(name:"genericMulti", type:"generic", width:6, height:4) {
            tileAttribute("device.power", key: "PRIMARY_CONTROL") {
                attributeState "power", label: '${currentValue}W', unit: "W",
                        foregroundColor: "#000000",
                        backgroundColors:[
                            [value: 1, color: "#00cc00"], //Light Green
                            [value: 2000, color: "#79b821"], //Darker Green
                            [value: 3000, color: "#ffa81e"], //Orange
                            [value: 4000, color: "#FFF600"], //Yellow
                            [value: 5000, color: "#fb1b42"] //Bright Red
                        ]
            }
            tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
                attributeState "lastUpdated", label:'Last Updated:\n${currentValue}'
            }
        }
        valueTile("power", "device.power", decoration: "flat", width: 1, height: 1) {
            state "power", label:'${currentValue} W', unit: "W", icon: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/sense_monitor.png",
                backgroundColors:[
                    [value: 0, color: "#ffffff"],
                    [value: 1, color: "#00a0dc"]
                ]
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
        valueTile("phase1Voltage", "device.phase1Voltage", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("phase1Voltage", label:'Phase 1:\n${currentValue}V', unit: "V")
        }
        valueTile("phase2Voltage", "device.phase2Voltage", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("phase2Voltage", label:'Phase 2:\n${currentValue}V', unit: "V")
        }
        valueTile("phase1Usage", "device.phase1Usage", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("phase1Usage", label:'Phase 1 Usage:\n${currentValue}W', unit: "W")
        }
        valueTile("phase2Usage", "device.phase2Usage", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("phase2Usage", label:'Phase 2 Usage:\n${currentValue}W', unit: "W")
        }
        valueTile("cycleHz", "device.cycleHz", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("cycleHz", label:'Cycle Hz:\n${currentValue}hz', unit: "HZ")
        }
        valueTile("wifi_ssid", "device.wifi_ssid", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("wifi_ssid", label:'WiFi SSID:\n${currentValue}')
        }
        valueTile("wifi_signal", "device.wifi_signal", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("wifi_signal", label:'WiFi Signal:\n${currentValue}', unit: "dBm")
        }
        valueTile("networkDetection", "device.networkDetection", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
            state("networkDetection", label:'Network Detection:\n${currentValue}')
        }
        valueTile("detectionsPending", "device.detectionsPending", height: 2, width: 4, inactiveLabel: false, decoration: "flat") {
            state("detectionsPending", label:'Pending Device Detections:\n${currentValue}')
        }
        main(["power"])
        details(["genericMulti", "dtCreated", "phase1Voltage", "phase2Voltage", "phase1Usage", "phase2Usage", "cycleHz", "wifi_ssid", "wifi_signal", "networkDetection", "detectionsPending", "firmwareVer"])
    }
}

def installed() {
	log.trace "${device?.displayName} Executing Installed..."
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

def updateDeviceStatus(Map echoDevice){
    String devName = getShortDevName()
    echoDevice?.monitorData?.each { k,v ->
        logger("debug", "$k: $v")
    }
    
    Float currentPower = echoDevice?.usage?.isNumber() ? echoDevice?.usage as Float : 0.0
    Float oldPower = device.currentState("power")?.floatValue ?: -1
    if (oldPower != currentPower) {
        def usageChange = (currentPower - oldPower).abs()
        if (isStateChange(device, "power", currentPower?.toString())) {
            logger("debug", "Updating usage from $oldPower to $currentPower")
            sendEvent(name: "power", value: currentPower, units: "W", display: true, displayed: true, isStateChange: true)
        }
    }
    // log.debug "usage: ${echoDevice?.usage} | currentPower: $currentPower | oldPower: ${oldPower}"
    
    if(echoDevice?.monitorData) {
        String firmwareVer = echoDevice?.monitorData?.version ?: "Not Set"
        if(isStateChange(device, "firmwareVer", firmwareVer?.toString())) {
            sendEvent(name: "firmwareVer", value: firmwareVer?.toString(), display: true, displayed: true)
        }
        logger("debug", "voltage: ${echoDevice?.monitorData?.voltage}")
        String volt1 = echoDevice?.monitorData?.voltage && echoDevice?.monitorData?.voltage[0] ? echoDevice?.monitorData?.voltage[0] : "Not Set"
        if(isStateChange(device, "phase1Voltage", volt1?.toString())) {
            sendEvent(name: "phase1Voltage", value: volt1?.toString(), display: true, displayed: true)
        }

        String volt2 = echoDevice?.monitorData?.voltage && echoDevice?.monitorData?.voltage[1] ? echoDevice?.monitorData?.voltage[1] : "Not Set"
        if(isStateChange(device, "phase2Voltage", volt2?.toString())) {
            sendEvent(name: "phase2Voltage", value: volt2?.toString(), display: true, displayed: true)
        }

        String phaseUse1 = echoDevice?.monitorData?.phaseUsage && echoDevice?.monitorData?.phaseUsage[0] ? echoDevice?.monitorData?.phaseUsage[0] : "Not Set"
        if(isStateChange(device, "phase1Usage", phaseUse1?.toString())) {
            sendEvent(name: "phase1Usage", value: phaseUse1?.toString(), display: true, displayed: true)
        }

        String phaseUse2 = echoDevice?.monitorData?.phaseUsage && echoDevice?.monitorData?.phaseUsage[1] ? echoDevice?.monitorData?.phaseUsage[1] : "Not Set"
        if(isStateChange(device, "phase2Usage", phaseUse2?.toString())) {
            sendEvent(name: "phase2Usage", value: phaseUse2?.toString(), display: true, displayed: true)
        }
        String hz = echoDevice?.monitorData?.hz ?: "Not Set"
        if(isStateChange(device, "cycleHz", hz?.toString())) {
            sendEvent(name: "cycleHz", value: hz?.toString(), display: true, displayed: true)
        }
        String ssid = echoDevice?.monitorData?.wifi_ssid ?: "Not Set"
        if(isStateChange(device, "cyclwifi_ssideHz", ssid?.toString())) {
            sendEvent(name: "wifi_ssid", value: ssid?.toString(), display: true, displayed: true)
        }
        String signal = echoDevice?.monitorData?.wifi_signal ?: "Not Set"
        if(isStateChange(device, "wifi_signal", signal?.toString())) {
            sendEvent(name: "wifi_signal", value: signal?.toString(), display: true, displayed: true)
        }
        String netDetect = (echoDevice?.monitorData?.ndt_enabled == true) ? "Enabled" : "Disabled"
        if(isStateChange(device, "networkDetection", netDetect?.toString())) {
            sendEvent(name: "networkDetection", value: netDetect?.toString(), display: true, displayed: true)
        }

        String pending = (echoDevice?.monitorData?.detectionsPending?.size()) ? echoDevice?.monitorData?.detectionsPending?.collect { "${it?.name} (${it?.progress}%)"}?.join("\n") : "Nothing Pending..."
        logger("debug", "pending: $pending")
        if(isStateChange(device, "detectionsPending", pending?.toString())) {
            sendEvent(name: "detectionsPending", value: pending?.toString(), display: true, displayed: true)
        }
    }
    setOnlineStatus((echoDevice?.monitorData?.online != false))
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false , displayed: false)
}

public setOnlineStatus(Boolean isOnline) {
    if(isStateChange(device, "DeviceWatch-DeviceStatus", (isOnline ? "online" : "offline"))) {
        sendEvent(name: "DeviceWatch-DeviceStatus", value: (isOnline ? "online" : "offline"), displayed: true, isStateChange: true)
    }
}

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
