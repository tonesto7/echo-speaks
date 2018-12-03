/**
 *  Echo Speaks Companion
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
 
import groovy.json.*
import java.text.SimpleDateFormat
include 'asynchttp_v1'

String platform() { return "SmartThings" }
String appVersion()	 { return "2.1.1" }
String appModified() { return "2018-12-02" } 
String appAuthor()	 { return "Anthony Santilli" }
Boolean isST() { return (platform() == "SmartThings") }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }
Map minVersions() { //These define the minimum versions of code this app will work with.
    return [echoDevice: 201, server: 201]
}
definition(
    name: "Echo Speaks Companion",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "Allow you to create virtual echo devices and send tts to them in SmartThings",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: getAppImg("echo_speaks.1x.png"),
    iconX2Url: getAppImg("echo_speaks.2x.png"),
    iconX3Url: getAppImg("echo_speaks.3x.png"),
    pausable: true)

preferences {
    page(name: "mainPage")
    page(name: "settingsPage")
    page(name: "newSetupPage")
    page(name: "devicePage")
    page(name: "deviceListPage")
    page(name: "changeLogPage")
    page(name: "notifPrefPage")
    page(name: "servPrefPage")
    page(name: "broadcastTestPage")
    page(name: "setNotificationTimePage")
    page(name: "uninstallPage")
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

// TODO: implement event handlers