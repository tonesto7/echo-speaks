/**
 *  Echo Speaks - Groups
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
 
String appVersion()	 { return "2.0.5" }
String appModified() { return "2018-12-07" } 
String appAuthor()	 { return "Anthony S." }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }

definition(
    name: "Echo Speaks - Groups",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllow creation of virtual broadcast groups based on your echo devices",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: getAppImg("es_groups.png"),
    iconX2Url: getAppImg("es_groups.png"),
    iconX3Url: getAppImg("es_groups.png"),
    pausable: true)

preferences {
    page(name: "startPage")
    page(name: "mainPage")
    page(name: "namePage")
    page(name: "uninstallPage")
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
    state?.isInstalled = true
    getBroadcastGroupData()
	log.debug "Group Data: ${state?.groupDataMap}"
}

def startPage() {
	if(parent) {
		if(!state?.isInstalled && parent?.state?.childInstallOkFlag != true) {
			notAllowedPage()
		} else {
			state?.isParent = false
			mainPage()
		}
	} else {
		notAllowedPage()
	}
}

def appInfoSect(sect=true)	{
    def str = ""
    str += "${app?.name}"
    str += "\nAuthor: ${appAuthor()}"
    str += "\nVersion: ${appVersion()}"
    section() { 
        paragraph str, image: getAppImg("es_groups.png")
    }
}

def notAllowedPage () {
	return dynamicPage(name: "notAllowedPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
		section() {
			paragraph "HOUSTON WE HAVE A PROBLEM!\n\nEcho Speaks - Groups can't be directly installed from the Marketplace.\n\nPlease use the Echo Speaks SmartApp to configure them.", required: true,
			state: null, image: getAppImg("exclude.png")
		}
		remove("Remove this bad Group", "WARNING!!!", "BAD Group SHOULD be removed")
	}
}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        state?.echoDeviceMap = parent?.state?.echoDeviceMap
        section("Device Preferences:") {
            Map devs = getDeviceList(true, false)
            input "echoGroupDevices", "enum", title: "Devices in Group", description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true, image: getAppImg("devices.png")
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section("Name this Group:") {
            label title:"Group Name", required:true, submitOnChange: true
        }
    }
}

Map getDeviceList(isInputEnum=false, hideDefaults=true) {
    Map devMap = [:]
    Map availDevs = state?.echoDeviceMap ?: [:]
    availDevs?.findAll { it?.value?.family != "WHA" }?.each { key, val->
        if(hideDefaults) {
            if(!(key?.toString() in ["nothing here"])) {
                devMap[key] = val
            }
        } else { devMap[key] = val }
    }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

public getBroadcastGroupData(retMap=false) {
    Map eDev = state?.echoDeviceMap
    Map out = [:]
    List devs = []
    echoGroupDevices?.each { d-> devs?.push([serialNumber: d, type: eDev[d]?.type]) }
    out = [name: app?.getLabel(), devices: devs]
    state?.groupDataMap = out
    if(retMap) { return out }
}