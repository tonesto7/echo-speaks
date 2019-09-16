/**
 *  Echo Speaks - Groups
 *
 *  Copyright 2018, 2019 Anthony Santilli
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

String appVersion()	 { return "3.0.1.1" }
String appModified()  { return "2019-09-15" }
String appAuthor()	 { return "Anthony S." }
Boolean isBeta()     { return true }
Boolean isST()       { return (getPlatform() == "SmartThings") }

definition(
    name: "Echo Speaks - Zones",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllow creation of virtual broadcast groups based on your echo devices",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    importUrl  : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/smartapps/tonesto7/echo-speaks-zones.src/echo-speaks-zones.groovy",
    pausable: true)

preferences {
    page(name: "startPage")
    page(name: "uhOhPage")
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
    if(settings?.appLbl && app?.getLabel() != "${settings?.appLbl} (Group)") { app?.updateLabel("${settings?.appLbl} (Group)") }
    getBroadcastGroupData()
    log.debug "Group Data: ${state?.groupDataMap}"
}

def startPage() {
    if(parent) {
        if(!state?.isInstalled && parent?.childInstallOk() != true) {
            uhOhPage()
        } else {
            state?.isParent = false
            mainPage()
        }
    } else {
        uhOhPage()
    }
}

def appInfoSect(sect=true)	{
    def str = "App: v${appVersion()}"
    section() {
        href "empty", title: pTS("${app?.name}", getAppImg("es_groups", true)), description: str, image: getAppImg("es_groups")
    }
}

def uhOhPage () {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            def str = "HOUSTON WE HAVE A PROBLEM!\n\nEcho Speaks - Groups can't be directly installed from the Marketplace.\n\nPlease use the Echo Speaks SmartApp to configure them."
            paragraph str, required: true, state: null, image: getAppImg("exclude")
        }
        if(isST()) { remove("Remove this bad Group", "WARNING!!!", "BAD Group SHOULD be removed") }
    }
}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        state?.echoDeviceMap = parent?.state?.echoDeviceMap
        section(sTS("Device Preferences:")) {
            Map devs = getDeviceList(true, false)
            input "echoGroupDevices", "enum", title: inTS("Devices in Group", getAppImg("devices", true)), description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true, image: getAppImg("devices")
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section(sTS("Name this Group:")) {
            input "appLbl", "text", title: inTS("Group Name", getAppImg("name_tag", true)), description: "", required: true, submitOnChange: true, image: getPublicImg("name_tag")
        }
    }
}

String getAppImg(String imgName, frc=false) { return (frc || isST()) ? "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/icons/${imgName}.png" : "" }
String getPublicImg(String imgName) { return isST() ? "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" : "" }
String sTS(String t, String i = null) { return isST() ? t : """<h3>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", "<br>")}</h3>""" }
String pTS(String t, String i = null, bold=true, color=null) { return isST() ? t : "${color ? """<div style="color: $color;">""" : ""}${bold ? "<b>" : ""}${i ? """<img src="${i}" width="42"> """ : ""}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : ""}${color ? "</div>" : ""}" }
String inTS(String t, String i = null, color=null) { return isST() ? t : """${color ? """<div style="color: $color;">""" : ""}${i ? """<img src="${i}" width="42"> """ : ""} <u>${t?.replaceAll("\\n", " ")}</u>${color ? "</div>" : ""}""" }

String bulletItem(String inStr, String strVal) { return "${inStr == "" ? "" : "\n"} \u2022 ${strVal}" }
String dashItem(String inStr, String strVal, newLine=false) { return "${(inStr == "" && !newLine) ? "" : "\n"} - ${strVal}" }

Integer stateSize() {
    def j = new groovy.json.JsonOutput().toJson(state)
    return j?.toString().length()
}
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0) }

private addToLogHistory(String logKey, msg, Integer max=10) {
    Boolean ssOk = (stateSizePerc() > 70)
    List eData = atomicState[logKey as String] ?: []
    eData.push([dt: getDtNow(), message: msg])
    if(!ssOk || eData?.size() > max) { eData = eData?.drop( (eData?.size()-max)+1 ) }
    atomicState[logKey as String] = eData
}
private logDebug(msg) { if(settings?.logDebug == true) { log.debug "Actions (v${appVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info " Actions (v${appVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace "Actions (v${appVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { log.warn " Actions (v${appVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, 15); } }
private logError(msg) { if(settings?.logError != false) { log.error "Actions (v${appVersion()}) | ${msg}"; }; addToLogHistory("errorHistory", msg, 15); }

Map getLogHistory() {
    return [ warnings: atomicState?.warnHistory ?: [], errors: atomicState?.errorHistory ?: [] ]
}

Map getDeviceList(isInputEnum=false, onlyTTS=false) {
    Map devMap = [:]
    Map availDevs = state?.echoDeviceMap ?: [:]
    availDevs?.each { key, val->
        if(onlyTTS && val?.ttsSupport != true) { return }
        devMap[key] = val
    }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

Map getAllDevices(isInputEnum=false) {
    Map devMap = [:]
    Map availDevs = state?.allEchoDevices ?: [:]
    availDevs?.each { key, val-> devMap[key] = val }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

private getPlatform() {
    def p = "SmartThings"
    if(state?.hubPlatform == null) {
        try { [dummy: "dummyVal"]?.encodeAsJson(); } catch (e) { p = "Hubitat" }
        // p = (location?.hubs[0]?.id?.toString()?.length() > 5) ? "SmartThings" : "Hubitat"
        state?.hubPlatform = p
        log.debug "hubPlatform: (${state?.hubPlatform})"
    }
    return state?.hubPlatform
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
