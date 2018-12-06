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

String appVersion()	 { return "2.0.5" }
String appModified() { return "2018-12-06" } 
String appAuthor()	 { return "Anthony Santilli" }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }

definition(
    name: "Echo Speaks - Actions",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllow you to create echo device actions based on Events in your SmartThings home",
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

def appInfoSect(sect=true)	{
    def str = ""
    str += "${app?.name}"
    str += "\nAuthor: ${appAuthor()}"
    str += "\nVersion: ${appVersion()}"
    section() { 
        paragraph str, image: getAppImg("echo_speaks.2x.png")
    }
}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    // if(showChgLogOk()) { 
    //     return changeLogPage() 
    // } else {
        return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "servPrefPage"), uninstall: newInstall, install: !newInstall) {
            appInfoSect()
            
            section("Device Preferences:") {
                // Map devs = getDeviceList(true, false)
                // input "echoDeviceFilter", "enum", title: "Don't Use these Devices", description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true, image: getAppImg("exclude.png")
                // paragraph title:"Notice:", "Any Echo devices created by this app will require manual removal, or uninstall the app to remove all devices!\nTo prevent an unwanted device from reinstalling after removal make sure to add it to the Don't use input before removing."
            }
            section("Remove Broadcast Group:") {
                href "uninstallPage", title: "Remove this Group", description: "Tap to Remove...", image: getAppImg("uninstall.png")
            }
        }
    // }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        remove("Remove this Group!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis group will be removed")
    }
}