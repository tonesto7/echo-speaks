/**
 *  Echo Speaks SmartApp
 *
 *  Copyright 2018 Anthony Santilli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import groovy.json.*
import java.text.SimpleDateFormat
include 'asynchttp_v1'

String platform() { return "SmartThings" }
String appVersion()	 { return "2.0.0" }
String appModified() { return "2018-12-02" } 
String appAuthor()	 { return "Anthony Santilli" }
Boolean isST() { return (platform() == "SmartThings") }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }
Map minVersions() { //These define the minimum versions of code this app will work with.
    return [echoDevice: 200, server: 200]
}

definition(
    name: "Echo Speaks",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "Allow you to create virtual echo devices and send tts to them in SmartThings",
    category: "My Apps",
    iconUrl: getAppImg("echo_speaks.1x.png"),
    iconX2Url: getAppImg("echo_speaks.2x.png"),
    iconX3Url: getAppImg("echo_speaks.3x.png"),
    pausable: true,
    oauth: true)

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

public getDeviceStyle(String family, String type) {
    switch(type) {
        //ECHOS - SPEAKERS\\
	    case "A38949IHXHRQ5P": return [name: "Echo Tap", image: "echo_gen", allowTTS: true]
        case "AB72C64C86AW2" : return [name: "Echo (Gen1)", image: "echo_gen1", allowTTS: true]
        case "A7WXQPH584YP"  : return [name: "Echo (Gen2)", image: "echo_gen2", allowTTS: true]
        case "A2M35JJZWCQOMZ": return [name: "Echo Plus (Gen1)", image: "echo_plus_gen1", allowTTS: true]
        case "A18O6U1UQFJ0XK": return [name: "Echo Plus (Gen2)", image: "echo_plus_gen2", allowTTS: true]
        case "A3SSG6GR8UU7SN": return [name: "Echo Sub", image: "echo_sub_gen1", allowTTS: true]
        case "A38EHHIB10L47V": return [name: "Echo Dot (Gen1)", image: "echo_dot_gen1", allowTTS: true]
        case "AKNO1N0KSFN8L" : return [name: "Echo Dot (Gen1)", image: "echo_dot_gen1", allowTTS: true]
        case "A3S5BH2HU6VAYF": return [name: "Echo Dot (Gen2)", image: "echo_dot_gen2", allowTTS: true]
        case "A32DOYMUN6DTXA": return [name: "Echo Dot (Gen3)", image: "echo_dot_gen3", allowTTS: true]
        //ECHOS - SCREENS\\
        case "A10A33FOX2NUBK": return [name: "Echo Spot", image: "echo_spot_gen1", allowTTS: true]
        case "A1NL4BVLQ4L3N3": return [name: "Echo Show (Gen1)", image: "echo_show_gen1", allowTTS: true]
        case "AWZZ5CVHX2CD"  : return [name: "Echo Show (Gen2)", image: "echo_show_gen2", allowTTS: true]
        //FIRE TVs\\
	    case "A12GXV8XMS007S": return [name: "Fire TV (Gen1)", image: "firetv_gen1", allowTTS: true]
        case "A2E0SNTXJVT7WK": return [name: "Fire TV (Gen2)", image: "firetv_gen2", allowTTS: true]
        case "A2GFL5ZMWNE0PX": return [name: "Fire TV (Gen3)", image: "firetv_gen3", allowTTS: true]
        case "ADVBD696BHNV5" : return [name: "Fire TV Stick (Gen1)", image: "firetv_stick_gen1", allowTTS: true]
        case "A2LWARUGJLBYEW": return [name: "Fire TV Stick (Gen2)", image: "firetv_stick_gen2", allowTTS: true]
        case "AKPGW064GI9HE" : return [name: "Fire TV Stick 4K (Gen3)", image: "firetv_stick_gen3", allowTTS: true] 
        case "A3HF4YRA2L7XGC": return [name: "Fire TV Cube", image: "firetv_cube", allowTTS: true]
        //TABLETS\\
        case "A2M4YX06LWP8WI": return [name: "Fire Tablet", image: "amazon_tablet", allowTTS: true]
        case "A1J16TEDOYCZTN": return [name: "Fire Tablet", image: "amazon_tablet", allowTTS: true]
        case "A2M4YX06LWP8WI": return [name: "Fire Tablet 7 (Gen5)", image: "amazon_tablet", allowTTS: true]
        case "A3R9S4ZZECZ6YL": return [name: "Fire Tablet HD 10", image: "tablet_hd10", allowTTS: true]
        //MULTIROOM\\
        case "A3C9PE6TNYLTCH": return [name: "Multiroom", image: "echo_wha", allowTTS: false]
        //SONOS\\
        case "A15ERDAKK5HQQG": return [name: "Sonos", image: "sonos_generic", allowTTS: false]
        case "A2OSP3UA4VC85F": return [name: "Sonos", image: "sonos_generic", allowTTS: false]
        case "A3NPD82ABCPIDP": return [name: "Sonos Beam", image: "sonos_beam", allowTTS: true]
        //OTHER\\
        case "A18BI6KPKDOEI4": return [name: "Ecobee4", image: "ecobee4", allowTTS: true]
        case "A1N9SW0I0LUX5Y": return [name: "Dash Wand", image: "dash_wand", allowTTS: false]
        default: return [name: "Echo Unknown $type", image: "unknown", allowTTS: false]
    }
}

def appInfoSect(sect=true)	{
    def str = ""
    str += "${app?.name}"
    str += "\nAuthor: ${appAuthor()}"
    str += "\nVersion: ${appVersion()}"
    section() { 
        href "changeLogPage", title: "", description: str, image: getAppImg("echo_speaks.2x.png")
        if(state?.customerName) {
            paragraph "Hello, ${state?.customerName}"
        }
    }
}

def mainPage() {
    def tokenOk = getAccessToken()
    checkVersionData(true)
    Boolean newInstall = !state?.isInstalled
    
    if(state?.resumeConfig) {
        return servPrefPage()
    } else if(showChgLogOk()) { 
        return changeLogPage() 
    } else {
        return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "servPrefPage"), uninstall: newInstall, install: !newInstall) {
            appInfoSect()
            if(!tokenOk) {
                paragraph title: "Uh OH!!!", "Oauth Has NOT BEEN ENABLED. Please Remove this app and try again after it after enabling OAUTH"
                return
            }
            if(!newInstall) {
                section("Alexa Login Service:") {
                    def t0 = getServiceConfDesc()
                    href "servPrefPage", title: "Login Service\nSettings", description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("settings.png")
                }
            }
            
            section("Device Preferences:") {
                if(!newInstall) {
                    List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : ""}" }?.sort()
                    if(devs?.size()) {
                        href "deviceListPage", title: "Discovered Devices:", description: "${devs?.join("\n")}\n\nTap to view details...", state: "complete"
                    } else { paragraph title: "Discovered Devices:", "No Devices Available", state: "complete" }
                }
                input "autoCreateDevices", "bool", title: "Auto Create New Devices?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("devices.png")
                input "createTablets", "bool", title: "Create Devices for Tablets?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("amazon_tablet.png")
                input "createWHA", "bool", title: "Create Multiroom Devices?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("echo_wha.png")
                input "createOtherDevices", "bool", title: "Create Other Alexa Enabled Devices?", description: "FireTV (Cube, Stick), Sonos, etc.", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("devices.png")
                input "autoRenameDevices", "bool", title: "Rename Devices to Match Amazon Echo Name?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("name_tag.png")

                if(newInstall) {
                    paragraph title:"Notice:", "Device filtering options will be available once app install is complete.", required: true, state: null
                } else {
                    Map devs = getDeviceList(true, false)
                    input "echoDeviceFilter", "enum", title: "Don't Use these Devices", description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true, image: getAppImg("exclude.png")
                    paragraph title:"Notice:", "Any Echo devices created by this app will require manual removal, or uninstall the app to remove all devices!\nTo prevent an unwanted device from reinstalling after removal make sure to add it to the Don't use input before removing."
                }
            }
            
            section("Notifications:") {
                def t0 = getAppNotifConfDesc()
                href "notifPrefPage", title: "App and Device\nNotifications", description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("notification2.png")
            }
            
            section ("Application Preferences") {
                href "settingsPage", title: "Manage Logging, and Metrics", description: "Tap to modify...", image: getAppImg("settings.png")
            }
            
            section ("Broadcasts (Experimental)") {
                href "broadcastTestPage", title: "Broadcast Test Page", description: "Tap to modify...", image: getAppImg("settings.png")
            }
            if(!newInstall) {
                if(!state?.shownDevSharePage) {
                    showDevSharePrefs()
                }
                section("Donations:") {
                    href url: textDonateLink(), style:"external", required: false, title:"Donations", description:"Tap to open browser", image: getAppImg("donate.png")
                }
                section("Remove Everything:") {
                    href "uninstallPage", title: "Uninstall this App", description: "Tap to Remove...", image: getAppImg("uninstall.png")
                }
            }
        }
    }
}

def broadcastTestPage() {
    return dynamicPage(name: "broadcastTestPage", uninstall: false, install: false) {
        section("") {
            Map devs = getDeviceList(true, false)
            input "broadcastDevices", "enum", title: "Select Devices to Test the Broadcast", description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            input "broadcastVolume", "number", title: "Broadcast at this volume", description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
            input "broadcastMessage", "text", title: "Message to broadcast", defaultValue: "This is a test of the Echo speaks broadcast system!!!", required: true, submitOnChange: true
            input "broadcastParallel", "bool", title: "Execute commands in Parallel?", description: "", required: false, defaultValue: true, submitOnChange: true
        }
        if(settings?.broadcastDevices) {
            section() {
                input "performBroadcast", "bool", title: "Perform the Broadcast?", description: "", required: false, defaultValue: false, submitOnChange: true
                if(performBroadcast) {
                    executeBroadcast()
                    
                }
            }
        }
    }
}

private executeBroadcast() {
    String testMsg = settings?.broadcastMessage
    Map eDevs = state?.echoDeviceMap
    List seqItems = []
    Integer bcVol = settings?.broadcastVolume
    def selectedDevs = settings?.broadcastDevices
    selectedDevs?.each { dev->
        seqItems?.push([command: "volume", value: bcVol, serial: dev, type: eDevs[dev]?.type])
    }
    selectedDevs?.each { dev->
        seqItems?.push([command: "speak", value: testMsg, serial: dev, type: eDevs[dev]?.type])
    }
    selectedDevs?.each { dev->
        seqItems?.push([command: "volume", value: 20, serial: dev, type: eDevs[dev]?.type])
    }
    sendMultiSequenceCommand(seqItems, settings?.broadcastParallel)
    settingUpdate("performBroadcast", "false", "bool")
}

Map sequenceBuilder(cmd, val) {
    def seqJson = null
    if (cmd instanceof Map) {
        seqJson = cmd?.sequence ?: cmd
    } else { seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": createSequenceNode(cmd, val)] }
    Map seqObj = ["behaviorId": seqJson?.sequenceId ? cmd?.automationId : "PREVIEW", "sequenceJson": seqJson?.encodeAsJson() as String, "status": "ENABLED"]
    return seqObj
}

Map multiSequenceBuilder(commands, parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList?.push(createSequenceNode(cmdItem?.serial, cmdItem?.type, cmdItem?.command, cmdItem?.value)) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    Map seqObj = sequenceBuilder(seqJson, null)
    return seqObj
}

Map createSequenceNode(serialNumber, deviceType, command, value) {
    try {
        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            "operationPayload": [
                "deviceType": deviceType,
                "deviceSerialNumber": serialNumber,
                "locale": (settings?.regionLocale ?: "en-US"),
                "customerId": state?.deviceOwnerCustomerId
            ]
        ]
        switch (command) {
            case "weather":
                seqNode?.type = "Alexa.Weather.Play"
                break
            case "traffic":
                seqNode?.type = "Alexa.Traffic.Play"
                break
            case "flashbriefing":
                seqNode?.type = "Alexa.FlashBriefing.Play"
                break
            case "goodmorning":
                seqNode?.type = "Alexa.GoodMorning.Play"
                break
            case "singasong":
                seqNode?.type = "Alexa.SingASong.Play"
                break
            case "tellstory":
                seqNode?.type = "Alexa.TellStory.Play"
                break
            case "playsearch":
                seqNode?.type = "Alexa.Music.PlaySearchPhrase"
                break
            case "volume":
                seqNode?.type = "Alexa.DeviceControls.Volume"
                seqNode?.operationPayload?.value = value;
                break
            case "speak":
                seqNode?.type = "Alexa.Speak"
                seqNode?.operationPayload?.textToSpeak = value as String
                break
            default:
                return
        }
        // log.debug "seqNode: $seqNode"
        return seqNode
    } catch (ex) {
        log.error "createSequenceNode Exception: $ex"
        return [:]
    }
}

private sendAmazonCommand(String method, Map params, Map otherData) {
    asynchttp_v1."${method?.toString()?.toLowerCase()}"(amazonCommandResp, params, otherData)
}

def amazonCommandResp(response, data) {
    if(response?.hasError()) {
        log.error "amazonCommandResp error: ${response?.getErrorMessage()}"
    } else {
        def resp = response?.data ? response?.getJson() : null
        // logger("warn", "amazonCommandResp | Status: (${response?.getStatus()}) | Response: ${resp} | PassThru-Data: ${data}")
        if(response?.getStatus() == 200) {
            log.trace "amazonCommandResp | Status: (${response?.getStatus()}) | Response: ${resp} | (${data?.cmdDesc}) was Successfully Sent!!!"
        }
    }
}

private sendSequenceCommand(type, command, value) {
    // logger("trace", "sendSequenceCommand($type) | command: $command | value: $value")
    Map seqObj = sequenceBuilder(command, value)
    sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/behaviors/preview",
        headers: ["Cookie": state?.cookie?.cookie, "csrf": state?.cookie?.csrf],
        requestContentType: "application/json",
        contentType: "application/json",
        body: seqObj
    ], [cmdDesc: "SequenceCommand (${type})"])
}

private sendMultiSequenceCommand(commands, parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList?.push(createSequenceNode(cmdItem?.serial, cmdItem?.type, cmdItem?.command, cmdItem?.value)) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    sendSequenceCommand("MultiSequence", seqJson, null)
}

def settingsPage() {
    return dynamicPage(name: "settingsPage", uninstall: false, install: false) {
        section("Logging:") {
            input (name: "appDebug", type: "bool", title: "Show Debug Logs in the IDE?", description: "Only leave on when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug.png"))
            if(settings?.appDebug) {
                input (name: "appTrace", type: "bool", title: "Show Detailed Trace Logs in the IDE?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug.png"))
            }
        }
        showDevSharePrefs()
        section("App Change Details:") {
			href "changeLogPage", title: "View App Revision History", description: "Tap to view", image: getAppImg("change_log.png")
		}
    }
}

def deviceListPage() {
    return dynamicPage(name: "deviceListPage", install: false) {
        Map devMap = state?.echoDeviceMap
        // log.debug "devMap: $devMap"
        section() {
            state?.echoDeviceMap?.sort { it?.value?.name }?.each { k,v->
                String str = "Name: ${v?.name}"
                str += "\nStyle: ${v?.style?.name}" 
                str += "\nFamily: ${v?.family}" 
                str += "\nType: ${v?.type}"
                str += "\nMusic Player: ${v?.mediaPlayer?.toString()?.capitalize()}"
                str += "\nVolume Control: ${v?.volumeSupport?.toString()?.capitalize()}"
                str += "\nText-to-Speech: ${v?.ttsSupport?.toString()?.capitalize()}"
                str += "\nStatus: ${v?.online ? "Online" : "Offline"}"
                
                paragraph str, state: "complete", image: getAppImg("${v?.style?.image}.png")
            }
        }
    }
}

def showDevSharePrefs() {
    section("Share Data with Developer:") {
        paragraph title: "What is this used for?", "These options send non-user identifiable information and error data to diagnose catch trending issues."
        input ("optOutMetrics", "bool", title: "Do Not Share Data?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("analytics.png"))
        if(settings?.optOutMetrics != true) {
            href url: getAppEndpointUrl("renderMetricData"), style:"embedded", title:"View the Data shared with Developer", description: "Tap to view Data", required:false, image: getAppImg("view.png")
        }
    }
    state?.shownDevSharePage = true
}

Map getDeviceList(isInputEnum=false, hideDefaults=true) {
    Map devMap = [:]
    Map availDevs = state?.echoDeviceMap ?: [:]
    availDevs?.each { key, val->
        if(hideDefaults) {
            if(!(key?.toString() in ["nothing here"])) {
                devMap[key] = val
            }
        } else { devMap[key] = val }
    }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

def servPrefPage() {
    Boolean newInstall = !state?.isInstalled
    Boolean resumeConf = !state?.resumeConfig
    return dynamicPage(name: "servPrefPage", install: (newInstall || resumeConf)) {
        Map amazonDomainOpts = [
            "amazon.com":"Amazon.com",
            "amazon.ca":"Amazon.ca",
            "amazon.co.uk":"amazon.co.uk",
            "amazon.de":"Amazon.de",
        ]
        List localeOpts = ["en-US", "en-CA", "de-DE", "en-GB"]
        Boolean herokuOn = (settings?.useHeroku == true)
        Boolean hubOn = (settings?.stHub != null)
        Boolean hasChild = (app.getChildDevices(true)?.size())
        if(newInstall) {
            section("") {
                input "useHeroku", "bool", title: "Will you be deploying to Heroku Cloud?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("heroku.png")
                if(useHeroku) {
                    paragraph "Please complete the install and return to the Echo Speaks SmartApp to resume deployment and configuration of the service", required: true, state: null
                    state?.resumeConfig = true
                } else {
                    paragraph "Please Configure these Options before completing the App install", state: "complete"
                    state?.resumeConfig = false
                }
            }
            showDevSharePrefs()
        }
        if(!newInstall) {
            state?.resumeConfig = false
            if(!hasChild || !state?.serviceConfigured) {
                section("Cloud Service Hosting:") {
                    input "useHeroku", "bool", title: "Use Heroku Cloud to Host Service?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("heroku.png")
                }
            }
            if(state?.nodeServiceInfo) {
                section() {
                    paragraph title: "${settings?.useHeroku && state?.onHeroku ? "Heroku" : "Service"} Info:", getServInfoDesc(), state: "complete"
                }
            }
            if(settings?.useHeroku) {
                section("Service Preferences", hideable: true, hidden: state?.onHeroku) {
                    input (name: "amazonDomain", type: "enum", title: "Select your Amazon Domain?", description: "", required: true, defaultValue: "amazon.com", options: amazonDomainOpts, submitOnChange: true, image: getPublicImg("amazon_orange.png"))
                    input (name: "regionLocale", type: "enum", title: "Select your Locale?", description: "", required: true, defaultValue: "en-US", options: localeOpts, submitOnChange: true, image: getPublicImg("web.png"))
                    input (name: "refreshSeconds", type: "number", title: "Poll Amazon for Device Status (in Seconds)", description: "in Seconds...", required: false, defaultValue: 60, submitOnChange: true, image: getAppImg("delay_time.png"))
                }
                if(!state?.onHeroku) {
                    section("Deploy the Service:") {
                        if(settings?.amazonDomain && settings?.refreshSeconds) {
                            href url: getAppEndpointUrl("config"), style: "external", required: false, title: "Begin Heroku Setup", description: "Tap to proceed", state: "complete", image: getPublicImg("upload.png")
                        }
                    }
                }
            }
        }
        if((newInstall && !useHeroku) || !newInstall) {
            if(!hasChild) {
                section("Hub Selection:") {
                    input(name: "stHub", type: "hub", title: "Select Local Hub", description: "This is mainly used for when the service runs on local network.", required: true, submitOnChange: true, image: getAppImg("hub.png"))
                }
            }
            if(settings?.stHub && !settings?.useHeroku) {
                section("Service Preferences", hideable: true, hidden: !newInstall) {
                    input (name: "amazonDomain", type: "enum", title: "Select your Amazon Domain?", description: "", required: true, defaultValue: "amazon.com", options: amazonDomainOpts, submitOnChange: true, image: getPublicImg("amazon_orange.png"))
                    input (name: "refreshSeconds", type: "number", title: "Poll Amazon for Device Status (in Seconds)", description: "in Seconds...", required: false, defaultValue: 60, submitOnChange: true, image: getAppImg("delay_time.png"))
                    if(!newInstall && settings?.stHub && !settings?.useHeroku) {
                        paragraph title: "Notice", "These changes will be applied on the next server data refresh."
                    }
                }
            }
        }
        if(!newInstall) {
            if(settings?.useHeroku && state?.onHeroku) {
                section("Cloud App Management:") {
                    href url: "https://${getRandAppName()}.herokuapp.com/config", style: "external", required: false, title: "Service Config Page", description: "Tap to proceed", image: getPublicImg("web.png")
                    href url: "https://${getRandAppName()}.herokuapp.com/manualCookie", style: "external", required: false, title: "Manual Cookie Page", description: "Tap to proceed", image: getPublicImg("web.png")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/settings", style: "external", required: false, title: "Heroku App Settings", description: "Tap to proceed", image: getAppImg("heroku.png")
                    // href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/webhooks", style: "external", required: false, title: "Heroku App Webhooks", description: "Tap to proceed", image: getAppImg("heroku.png")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/logs", style: "external", required: false, title: "Heroku App Logs", description: "Tap to proceed", image: getAppImg("heroku.png")
                    // href url: "https://${getRandAppName()}.herokuapp.com/skippedDevices", style: "external", required: false, title: "View Ignored Devices", description: "Tap to proceed", image: getPublicImg("web.png")
                }
            }
            
            section("Reset Options:", hideable:true, hidden: true) {
                input "resetService", "bool", title: "Reset Service Data?", description: "This will clear all traces of the current service info and allow you to redeploy or reconfigure a new instance.\nLeave the page and come back after toggling.", 
                    required: false, defaultValue: false, submitOnChange: true, image: getPublicImg("reset.png")
                input "resetCookies", "bool", title: "Clear Stored Cookie Data?", description: "This will clear all stored cookie data.", required: false, defaultValue: false, submitOnChange: true, image: getPublicImg("reset.png")
                if(settings?.resetService == true) { clearCloudConfig() }
                if(settings?.resetCookies == true) { clearCookie() }
            }
        }
    }
}

def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
        Integer pollWait = 900
        Integer pollMsgWait = 3600
        Integer updNotifyWait = 7200
        section("") {
            paragraph title: "Notice:", "The settings configure here are used by both the App and the Devices.", state: "complete"
        }
        section("Push Messages:") {
            input "usePush", "bool", title: "Send Push Notitifications\n(Optional)", required: false, submitOnChange: true, defaultValue: false, image: getAppImg("notification.png")
        }
        section("SMS Text Messaging:") {
            paragraph "To send to multiple numbers separate the number by a comma\nE.g. 8045551122,8046663344"
            input "smsNumbers", "text", title: "Send SMS to Text to...\n(Optional)", required: false, submitOnChange: true, image: getAppImg("sms_phone.png")
        }
        section("Pushover Support:") {
            input ("pushoverEnabled", "bool", title: "Use Pushover Integration", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("pushover.png"))
            if(settings?.pushoverEnabled == true) {
                if(state?.isInstalled) {
                    if(!state?.pushoverManager) {
                        paragraph "If this is the first time enabling Pushover than leave this page and come back if the devices list is empty"
                        pushover_init()
                    } else {
                        input "pushoverDevices", "enum", title: "Select Pushover Devices", description: "Tap to select", groupedOptions: getPushoverDevices(), multiple: true, required: false, submitOnChange: true
                        if(settings?.pushoverDevices) {
                            def t0 = ["-2":"Lowest", "-1":"Low", "0":"Normal", "1":"High", "2":"Emergency"]
                            input "pushoverPriority", "enum", title: "Notification Priority (Optional)", description: "Tap to select", defaultValue: "0", required: false, multiple: false, submitOnChange: true, options: t0
                            input "pushoverSound", "enum", title: "Notification Sound (Optional)", description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: getPushoverSounds()
                        }
                    }
                } else { paragraph "New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the SmartApps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", state: "complete" }
            }
        }
        if(settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) {
            if((settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) && !state?.pushTested && state?.pushoverManager) {
                if(sendMsg("Info", "Push Notification Test Successful. Notifications Enabled for ${app?.label}", true)) {
                    state.pushTested = true
                }
            }
            section("Notification Restrictions:") {
                def t1 = getNotifSchedDesc()
                href "setNotificationTimePage", title: "Notification Restrictions", description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("restriction.png")
            }
            section("Missed Poll Alerts:") {
                input (name: "sendMissedPollMsg", type: "bool", title: "Send Missed Checkin Alerts?", defaultValue: true, submitOnChange: true, image: getAppImg("late.png"))
                if(settings?.sendMissedPollMsg) {
                    def misPollNotifyWaitValDesc = settings?.misPollNotifyWaitVal ?: "Default: 15 Minutes"
                    input (name: "misPollNotifyWaitVal", type: "enum", title: "Time Past the Missed Checkin?", required: false, defaultValue: 900, options: notifValEnum(), submitOnChange: true, image: getAppImg("delay_time.png"))
                    if(settings?.misPollNotifyWaitVal) { pollWait = settings?.misPollNotifyWaitVal as Integer }
                    
                    def misPollNotifyMsgWaitValDesc = settings?.misPollNotifyMsgWaitVal ?: "Default: 1 Hour"
                    input (name: "misPollNotifyMsgWaitVal", type: "enum", title: "Send Reminder After?", required: false, defaultValue: 3600, options: notifValEnum(), submitOnChange: true, image: getAppImg("reminder.png"))
                    if(settings?.misPollNotifyMsgWaitVal) { pollMsgWait = settings?.misPollNotifyMsgWaitVal as Integer }
                }
            }
            section("Code Update Alerts:") {
                input (name: "sendAppUpdateMsg", type: "bool", title: "Send for Updates...", defaultValue: true, submitOnChange: true, image: getAppImg("update.png"))
                if(settings?.sendAppUpdateMsg) {
                    def updNotifyWaitValDesc = settings?.updNotifyWaitVal ?: "Default: 12 Hours"
                    input (name: "updNotifyWaitVal", type: "enum", title: "Send Reminders After?", required: false, defaultValue: 43200, options: notifValEnum(), submitOnChange: true, image: getAppImg("reminder.png"))
                    if(settings?.updNotifyWaitVal) { updNotifyWait = settings?.updNotifyWaitVal as Integer }
                }
            }
        } else { state.pushTested = false }
        state.misPollNotifyWaitVal = pollWait
        state.misPollNotifyMsgWaitVal = pollMsgWait
        state.updNotifyWaitVal = updNotifyWait
    }
}

def setNotificationTimePage() {
    dynamicPage(name: "setNotificationTimePage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
        Boolean timeReq = (settings["qStartTime"] || settings["qStopTime"]) ? true : false
        section() {
            input "qStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time.png")
            if(settings["qStartInput"] == "A specific time") {
                input "qStartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time.png")
            }
            input "qStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time.png")
            if(settings?."qStopInput" == "A specific time") {
                input "qStopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time.png")
            }
            input "quietDays", "enum", title: "Only on these days of the week", multiple: true, required: false, image: getAppImg("day_calendar.png"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            input "quietModes", "mode", title: "When these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode.png")
        }
    }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section("") {
            paragraph "This will uninstall the App and All Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove."
        }
        remove("Remove ${app?.label} and Devices!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis App and Devices will be removed")
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    state?.installData = [initVer: appVersion(), dt: getDtNow().toString(), updatedDt: "Not Set", sentMetrics: false, shownChgLog: true]
    state?.isInstalled = true
    sendInstallData()
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    if(!state?.isInstalled) { state?.isInstalled = true }
    if(!state?.installData) { state?.installData = [initVer: appVersion(), dt: getDtNow().toString(), updatedDt: getDtNow().toString(), sentMetrics: false] }
    unschedule()
    initialize()
}

def initialize() {
    // getAccessToken()
    if(app?.getLabel() != "Echo Speaks") { app?.updateLabel("Echo Speaks") }
    subscribe(app, onAppTouch)
    // if(!settings?.useHeroku && settings?.stHub) { subscribe(location, null, lanEventHandler, [filterEvents:false]) }
    if(!state?.resumeConfig) {
        runEvery5Minutes("healthCheck") // This task checks for missed polls, app updates, code version changes, and cloud service health
        updCodeVerMap()
        stateCleanup()
        runEvery15Minutes("dataRefresh") //This will reload the device list from Amazon
        validateCookie(true)
        runIn(4, "dataRefresh")
    }
}

def uninstalled() {
    log.warn "uninstalling app and devices"
    unschedule()
    if(settings?.optOutMetrics != true) {
        if(removeInstallData()) { state?.appGuid = null }
    }
}

void settingUpdate(name, value, type=null) {
    if(name && type) {
        app?.updateSetting("$name", [type: "$type", value: value])
    }
    else if (name && type == null){ app?.updateSetting(name.toString(), value) }
}

mappings {
    path("/renderMetricData") { action: [GET: "renderMetricData"] }
    path("/receiveData") { action: [POST: "processData"] }
    path("/config") { action: [GET: "renderConfig"]  }
    path("/cookie") { action: [GET: "getCookie", POST: "storeCookie", DELETE: "clearCookie"] }
}

def clearCloudConfig() {
    settingUpdate("resetService", "false", "bool")
    unschedule("cloudServiceHeartbeat")
    List remItems = ["generatedHerokuName", "useHeroku", "onHeroku", "nodeServiceInfo", "serviceConfigured"]
    remItems?.each { rem-> 
        state?.remove(rem as String)
    }
    app.getChildDevices(true)?.each { dev-> dev?.resetServiceInfo() }
    state?.resumeConfig = true
}

String getEnvParamsStr() {
    Map envParams = [:]
    envParams["smartThingsUrl"] = "${getAppEndpointUrl("receiveData")}"
    envParams["useHeroku"] = (settings?.useHeroku == true) ? "true" : "false"
    envParams["serviceDebug"] = (settings?.serviceDebug == true) ? "true" : "false"
    envParams["serviceTrace"] = (settings?.serviceTrace == true) ? "true" : "false"
    envParams["amazonDomain"] = settings?.amazonDomain as String
    envParams["refreshSeconds"] = settings?.refreshSeconds as String
    envParams["hostUrl"] = "${getRandAppName()}.herokuapp.com"
    // envParams["HEROKU_APP_NAME"] = "${getRandAppName()}"
    String envs = ""
    envParams?.each { k, v-> envs += "&env[${k}]=${v}" }
    return envs
}

private checkIfCodeUpdated() {
    if(state?.codeVersions && state?.codeVersions?.mainApp != appVersion()) {
        log.info "Code Version Change! Re-Initializing SmartApp in 5 seconds..."
        state?.pollBlocked = true
        Map iData = atomicState?.installData
        iData["updatedDt"] = getDtNow().toString()
        iData["shownChgLog"] = false
        atomicState?.installData = iData
        runIn(5, "updated", [overwrite: false])
        return true
    }
    state?.pollBlocked = false
    return false
}

private stateCleanup() {
    List items = ["availableDevices", "lastMsgDt", "consecutiveCmdCnt", "isRateLimiting", "versionData", "heartbeatScheduled", "serviceAuthenticated", ]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    state?.pollBlocked = false
    state?.resumeConfig = false
    
}

def onAppTouch(evt) {
    // log.trace "appTouch..."
    updated()
    // validateCookie()
    // apiHealthCheck()
    //resetQueues()
    //dataRefresh()
}

private resetQueues() {
    app.getChildDevices(true)?.each { it?.resetQueue() }
}

private updCodeVerMap() {
    Map cv = state?.codeVersions ?: [:]
    cv["mainApp"] = appVersion()
    state?.codeVersions = cv
}

private modCodeVerMap(key, val) {
    Map cv = state?.codeVersions ?: [:]
    cv["$key"] = val
    state?.codeVersions = cv
}

String getRandAppName() {
    if(!state?.generatedHerokuName) { state?.generatedHerokuName = "${app?.name?.toString().replaceAll(" ", "-")}-${randomString(8)}"?.toLowerCase() }
    return state?.generatedHerokuName as String
}

def processData() {
    // log.trace "processData() | Data: ${request.JSON}"
    Map data = request?.JSON as Map
    if(data) {
        if(data?.resource == "dyno" && data?.version == "application/vnd.heroku+json; version=3" && data?.action) {
            log.debug "Heroku Event (${data?.action}): App ${data?.data?.state}"
            if(data?.data && data?.data?.state == "down") {
                runIn(60, "sendOneHeartbeat", [overwrite: true])
                scheduleHeartbeat()
            }
            if(data?.data?.app?.name) {
                if(state?.generatedHerokuName != data?.data?.app?.name) { state?.generatedHerokuName = data?.data?.app?.name }
                    if(state?.cloudUrl != "https://${data?.data?.app?.name}.herokuapp.com") {
                    log.info "Heroku CloudURL change required | Old: ${state?.cloudUrl} | new: https://${data?.data?.app?.name}.herokuapp.com"
                    state?.cloudUrl = "https://${data?.data?.app?.name}.herokuapp.com"
                    app?.getChildDevices(true)?.each { cDev?.updateServiceInfo(state?.cloudUrl, state?.onHeroku) }
                    }
            }
        } else { log.debug "data: $data" }
    }
    def json = new groovy.json.JsonOutput().toJson([message: "success", version: appVersion()])
    render contentType: "application/json", data: json, status: 200
}

def getCookie() {
    log.trace "getCookie() Request Received..."
    Map resp = state?.cookie ?: [:]
    def json = new groovy.json.JsonOutput().toJson(resp)
    incrementCntByKey("getCookieCnt")
    render contentType: "application/json", data: json
}

def storeCookie() {
    log.trace "storeCookie Request Received..."
    if(request?.JSON && request?.JSON?.cookie && request?.JSON?.csrf) {
        Map obj = [:]
        obj?.cookie = request?.JSON?.cookie as String ?: null
        obj?.csrf = request?.JSON?.csrf as String ?: null
        state?.cookie = obj
    }
    if(state?.cookie?.cookie && state?.cookie?.csrf) {
        log.info "Cookie Has been Updated... Re-Initializing SmartApp and to restart polling..."
        runIn(5, "initialize", [overwrite: true])
    }
}

def clearCookie() {
    logger("trace", "clearCookie()")
    settingUpdate("resetCookies", "false", "bool")
    state?.remove("cookie")
    unschedule("getEchoDevices")
    log.warn "Cookie has been cleared... Device Refresh has been suspended..."
}

private authEvtHandler(isAuth) {
    state?.authValid = (isAuth == true)
    if(isAuth == false && !state?.noAuthActive) {
        noAuthReminder()
        sendMsg("${app.name} Amazon Login Issue", "Amazon Cookie Has Expired or is Missing!!! Please login again using the Heroku Web Config page...")
        runEvery1Hour("noAuthReminder")
        state?.noAuthActive = true
        app?.getChildDevices(true)?.each { it?.setAuthState(valid) }
    } else {
        if(state?.noAuthActive) { 
            unschedule("noAuthReminder")
            state?.noAuthActive = false
        }
    }
}

Boolean isAuthValid() {
    if(state?.authValid != true) {
        log.warn "Echo Speaks Authentication is no longer valid... Please login again and commands will be allowed again!!!"
        // state?.remove("cookie")
        return false
    } 
    return true
}

private validateCookie(frc=false) {
    if(!frc || (getLastCookieChkSec() <= 1800)) { return }
    try {
        def params = [uri: getAmazonUrl(), path: "/api/bootstrap", query: ["version": 0], headers: ["Cookie": state?.cookie?.cookie as String, "csrf": state?.cookie?.csrf as String], contentType: "application/json"]
        asynchttp_v1.get(cookieValidResp, params, [execDt: now()])
    } catch(ex) {
        incrementCntByKey("err_app_cookieValidCnt")
        log.error "validateCookie() Exception:", ex
    }
}

private apiHealthCheck(frc=false) {
    // if(!frc || (getLastApiChkSec() <= 1800)) { return }
    try {
        def params = [uri: getAmazonUrl(), path: "/api/ping", query: ["_": ""], headers: ["Cookie": state?.cookie?.cookie as String, "csrf": state?.cookie?.csrf as String], contentType: "plain/text"]
        httpGet(params) { resp->
            log.debug "API Health Check Resp: (${resp?.getData()})"
            return (resp?.getData().toString() == "healthy")
        }
    } catch(ex) {
        incrementCntByKey("err_app_apiHealthCnt")
        log.error "apiHealthCheck() Exception:", ex
    }
}

def cookieValidResp(response, data) { 
    try {
        Map aData = response?.json?.authentication ?: [:]
        Boolean valid = false
        if (aData) {
            if(aData?.customerId) { state?.deviceOwnerCustomerId = aData?.customerId }
            if(aData?.customerName) { state?.customerName = aData?.customerName }
            valid = (resp?.data?.authentication?.authenticated != false)
        }
        state?.lastCookieChkDt = getDtNow()
        def execTime = data?.execDt ? (now()-data?.execDt) : 0
        log.debug "Cookie Validation: (${valid}) | Process Time: (${execTime}ms)"
        authEvtHandler(valid)
    } catch (ex) {
        log.error "cookieValidResp Exception", ex
    }
}

private noAuthReminder() {
    log.warn "Amazon Cookie Has Expired or is Missing!!! Please login again using the Heroku Web Config page..."
}

private dataRefresh() {
    // validateCookie()
    getEchoDevices()
}

private makeSyncronousReq(params, method="get", src, showLogs=false) {
    try {
        "http${method?.toString()?.toLowerCase()?.capitalize()}"(params) { resp ->
            if(resp?.data) {
                // log.debug "status: ${resp?.status}"
                if(showLogs) { log.debug "makeSyncronousReq(Src: $src) | Status: ${resp?.status}: ${resp?.data}" }
                return resp?.data ?: null
            } else {
                return null
            }
        }
    } catch (ex) {
        if(ex instanceof  groovyx.net.http.ResponseParseException) {
            log.error "There was an errow while parsing the response: ", ex
        } else { log.error "makeSyncronousReq(Method: ${method}, Src: ${src}) exception", ex }
        return null
    }
}

private getEchoDevices() {
    if(!isAuthValid()) { return }
    def params = [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device",
        query: [ cached: true ],
        headers: [
            "Cookie": state?.cookie?.cookie as String, 
            "csrf": state?.cookie?.csrf as String
        ],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    asynchttp_v1.get(echoDevicesResponse, params, [execDt: now()])
}

def echoDevicesResponse(response, data) { 
    List ignoreTypes = ["A1DL2DVDQVK3Q", "A21Z3CGI8UIP0F", "A2825NDLA7WDZV", "A2IVLV5VM2W81", "A2TF17PFR55MTB", "A1X7HJX9QL16M5", "A2T0P32DY3F7VB", "A3H674413M2EKB", "AILBSA2LNTOYL", "A38BPK7OW001EX"]
    List removeKeys = ["appDeviceList", "charging", "macAddress", "deviceTypeFriendlyName", "registrationId", "remainingBatteryLevel", "postalCode", "language"]
    try {
        // log.debug "json response is: ${response.json}"
        List eDevData = response?.json?.devices ?: []
        Map echoDevices = [:]
        
        if(eDevData?.size()) {
            eDevData?.each { eDevice->
                String serialNumber = eDevice?.serialNumber;
                if (!(eDevice?.deviceType in ignoreTypes) && !eDevice?.accountName?.contains("Alexa App")) {
                    removeKeys?.each { rk->
                        eDevice?.remove(rk as String)
                    }
                    if (eDevice?.deviceOwnerCustomerId != null) {
                        state?.deviceOwnerCustomerId = eDevice?.deviceOwnerCustomerId
                    }
                    echoDevices[serialNumber] = eDevice;
                }
            }
        }
        // log.debug "echoDevices: ${echoDevices}"
        receiveEventData([echoDevices: echoDevices, execDt: data?.execDt], "Groovy")
    } catch (ex) {
        log.error "echoDevicesResponse Exception", ex
    }
}

def receiveEventData(Map evtData, String src) {
    try {
        if(checkIfCodeUpdated()) { 
            log.warn "Possible Code Version Update Detected... Device Updates will occur on next cycle."
            return 0 
        }
        
        logger("trace", "evtData(Keys): ${evtData?.keySet()}", true)
        if (evtData?.keySet()?.size()) {
            List ignoreTheseDevs = settings?.echoDeviceFilter ?: []
            Boolean onHeroku = true
            state?.serviceConfigured = true
            state?.onHeroku = onHeroku
            state?.cloudUrl = (onHeroku && evtData?.cloudUrl) ? evtData?.cloudUrl : null
            //Check for minimum versions before processing
            Boolean updRequired = false
            List updRequiredItems = []
            ["server":"Echo Speaks Server", "echoDevice":"Echo Speaks Device"]?.each { k,v->
                Map codeVers = state?.codeVersions
                if(codeVers && codeVers[k as String] && (versionStr2Int(codeVers[k as String]) < minVersions()[k as String])) { 
                    updRequired = true
                    updRequiredItems?.push("$v")
                }
            }
            
            if (evtData?.echoDevices?.size()) {
                def execTime = evtData?.execDt ? (now()-evtData?.execDt) : 0
                log.debug "Device Data Received for (${evtData?.echoDevices?.size()}) Echo Devices${!onHeroku && src ? " [$src]" : ""} | Took: (${execTime}ms) | Last Refreshed: (${(getLastDevicePollSec()/60).toFloat()?.round(1)} minutes)"
                Map echoDeviceMap = [:]
                List curDevFamily = []
                Integer cnt = 0
                evtData?.echoDevices?.each { echoKey, echoValue->
                    logger("debug", "echoDevice | $echoKey | ${echoValue}", true)
                    logger("debug", "echoDevice | ${echoValue?.accountName}", false)
                    Boolean familyAllowed = deviceFamilyAllowed(echoValue?.deviceFamily as String)
                    if(!familyAllowed) { return }
                    echoValue["authValid"] = (state?.authValid == true)
                    echoValue["amazonDomain"] = (settings?.amazonDomain ?: "amazon.com")
                    echoValue["regionLocale"] = (settings?.regionLocale ?: "en-US")
                    echoValue["cookie"] = state?.cookie
                    echoValue["deviceStyle"] = getDeviceStyle(echoValue?.deviceFamily as String, echoValue?.deviceType as String)
        
                    Boolean allowTTS = (echoValue?.deviceStyle?.allowTTS == true)
                    Boolean volumeSupport = (echoValue?.capabilities.contains("VOLUME_SETTING"))
                    Map permissions = [:]
                    permissions["TTS"] = allowTTS
                    permissions["volumeControl"] = (echoValue?.capabilities.contains("VOLUME_SETTING"))
                    permissions["mediaPlayer"] = (echoValue?.capabilities?.contains("AUDIO_PLAYER") || echoValue?.capabilities?.contains("AMAZON_MUSIC") || echoValue?.capabilities?.contains("TUNE_IN") || echoValue?.capabilities?.contains("PANDORA") || echoValue?.capabilities?.contains("I_HEART_RADIO") || echoValue?.capabilities?.contains("SPOTIFY"))
                    permissions["amazonMusic"] = (echoValue?.capabilities.contains("AMAZON_MUSIC"))
                    permissions["tuneInRadio"] = (echoValue?.capabilities.contains("TUNE_IN"))
                    permissions["iHeartRadio"] = (echoValue?.capabilities.contains("I_HEART_RADIO"))
                    permissions["pandoraRadio"] = (echoValue?.capabilities.contains("PANDORA"))
                    permissions["spotify"] = (echoValue?.capabilities.contains("SPOTIFY"))
                    permissions["isMultiroomDevice"] = (echoValue?.clusterMembers && echoValue?.clusterMembers?.size() > 0) ?: false;
                    permissions["isMultiroomMember"] = (echoValue?.parentClusters && echoValue?.parentClusters?.size() > 0) ?: false;
                    permissions["alarms"] = (echoValue?.capabilities.contains("TIMERS_AND_ALARMS"))
                    permissions["reminders"] = (echoValue?.capabilities.contains("REMINDERS"))
                    permissions["doNotDisturb"] = (echoValue?.capabilities?.contains("SLEEP"))
                    permissions["wakeWord"] = (echoValue?.capabilities?.contains("FAR_FIELD_WAKE_WORD"))
                    permissions["flashBriefing"] = (echoValue?.capabilities?.contains("FLASH_BRIEFING"))
                    permissions["microphone"] = (echoValue?.capabilities?.contains("MICROPHONE"))
                    permissions["connectedHome"] = (echoValue?.capabilities?.contains("SUPPORTS_CONNECTED_HOME"))
                    echoValue["permissionMap"] = permissions
                    if(permissions?.mediaPlayer != true && allowTTS != true && (!(echoValue?.deviceFamily in ["ROOK", "ECHO", "KNIGHT"]))) {
                        log.warn "IGNORED Device | Name: ${echoValue?.accountName} | Permissions: $permissions"
                        logger("warn", "Ignoring Device: ${echoValue?.deviceStyle?.name} because it does not support Playback Control or TTS!!!") 
                        return
                    }
                    echoDeviceMap[echoKey] = [name: echoValue?.accountName, online: echoValue?.online, family: echoValue?.deviceFamily, style: echoValue?.deviceStyle, type: echoValue?.deviceType, mediaPlayer: (permissions?.mediaPlayer == true), ttsSupport: allowTTS, volumeSupport: volumeSupport]
                
                    if(echoValue?.serialNumber in ignoreTheseDevs) { 
                        logger("warn", "skipping ${echoValue?.accountName} because it is in the do not use list...")
                        return 
                    }
                    
                    String dni = [app?.id, "echoSpeaks", echoKey].join("|")
                    def childDevice = getChildDevice(dni)
                    String devLabel = "Echo - ${echoValue?.accountName}${echoValue?.deviceFamily == "WHA" ? " (WHA)" : ""}"
                    String childHandlerName = "Echo Speaks Device"
                    String hubId = settings?.stHub?.getId()
                    
                    if (!childDevice) {
                        // log.debug "childDevice not found | autoCreateDevices: ${settings?.autoCreateDevices}"
                        if(settings?.autoCreateDevices != false) {
                            try{
                                log.debug "Creating NEW Echo Speaks Device!!! | Device Label: ($devLabel)"
                                childDevice = addChildDevice("tonesto7", childHandlerName, dni, hubId, [name: childHandlerName, label: devLabel, completedSetup: true])
                            } catch(physicalgraph.app.exception.UnknownDeviceTypeException ex) {
                                log.error "AddDevice Error! ", ex
                            }
                        }
                    } else {
                        //Check and see if name needs a refresh
                        if (settings?.autoRenameDevices != false && childDevice?.name != childHandlerName || childDevice?.label != devLabel) {
                            log.debug ("Updating device name (old label was " + childDevice?.label + " | old name was " + childDevice?.name + " new hotness: " + devLabel)
                            childDevice?.name = childHandlerName
                            childDevice?.label = devLabel
                        }
                        // logger("info", "Sending Device Data Update to ${devLabel} | Last Updated (${getLastDevicePollSec()}sec ago)")
                        childDevice?.updateDeviceStatus(echoValue)
                        childDevice?.updateServiceInfo(getServiceHostInfo(), onHeroku)
                        modCodeVerMap("echoDevice", childDevice?.devVersion()) // Update device versions in codeVersion state Map
                    }
                    
                    curDevFamily.push(echoValue?.deviceStyle?.name)
                }
                state?.lastDevDataUpd = getDtNow()
                state?.echoDeviceMap = echoDeviceMap
                state?.deviceStyleCnts = curDevFamily?.countBy { it }
            } else {
                log.warn "No Echo Device Data Sent... This may be the first transmission from the service after it started up!"
            }
            if(evtData?.serviceInfo) {
                Map srvcInfo = evtData?.serviceInfo
                state?.nodeServiceInfo = srvcInfo
                Boolean sendSetUpd = false
                if(srvcInfo?.config && srvcInfo?.config?.size() && !onHeroku) {
                    srvcInfo?.config?.each { k,v->
                        if(settings?.containsKey(k as String)) {
                            if(settings[k as String] != v) { 
                                sendSetUpd = true 
                                log.debug "config($k) | Service: $v | App: ${settings[k as String]} | sendUpdate: ${sendSetUpd}"
                            }
                        }
                    }
                }
                modCodeVerMap("server", srvcInfo?.version)
                // if(sendSetUpd && !onHeroku) { echoServiceUpdate() }
            }
            if(updRequired) {
                log.warn "CODE UPDATES REQUIRED: Echo Speaks Integration may not function until the following items are ALL Updated ${updRequiredItems}..."
                appUpdateNotify()
            }
            if(state?.installData?.sentMetrics != true) { runIn(900, "sendInstallData", [overwrite: false]) }
        }
    } catch(ex) {
        log.error "receiveEventData Error:", ex
        incrementCntByKey("appErrorCnt")
    }
}

Boolean deviceFamilyAllowed(String family) {
    if(family in ["ROOK", "KNIGHT", "ECHO"]) { return true }
    if(settings?.createTablets == true && family == "TABLET") { return true }
    if(settings?.createWHA == true && family == "WHA") { return true }
    if(settings?.createOtherDevices == true && !(family in ["DASH_WAND"])) { return true }
    return false
}

public getServiceHostInfo() {
    if(settings?.useHeroku) {
        return (state?.onHeroku && state?.cloudUrl) ? state?.cloudUrl : null
    } else {
        String ip = state?.nodeServiceInfo?.ip
        String port = state?.nodeServiceInfo?.port
        return ip && port ? "${ip}:${port}" : null
    }
}

// private echoServiceUpdate() {
//     // log.trace("echoServiceUpdate")
//     String host = getServiceHostInfo()
//     String smartThingsHubIp = settings?.stHub?.getLocalIP()
//     if(!host) { return }
    
//     logger("trace", "echoServiceUpdate host: ${host}")
//     try {
//         def hubAction = new physicalgraph.device.HubAction(
//             method: "POST",
//             headers: [
//                 "HOST": host,
//                 "smartThingsHubIp": "${smartThingsHubIp}",
//                 "refreshSeconds": settings?.refreshSeconds
//             ],
//             path: "/updateSettings",
//             body: ""
//         )
//         sendHubCommand(hubAction)
//     }
//     catch (Exception e) {
//         incrementCntByKey("appErrorCnt")
//         log.error "echoServiceUpdate HubAction Exception, $hubAction", ex
//     }
// }

/******************************************
|    Notification Functions
*******************************************/
String getAmazonDomain() { return settings?.amazonDomain as String }
String getAmazonUrl() {return "https://alexa.${settings?.amazonDomain as String}"}

Map notifValEnum(allowCust = true) {
    Map items = [
        300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    if(allowCust) { items[100000] = "Custom" }
    return items
}

private healthCheck() {
    // logger("trace", "healthCheck")
    updCodeVerMap()
    checkVersionData()
    validateCookie()
    if(!getOk2Notify()) { return }
    missPollNotify((settings?.sendMissedPollMsg == true), (state?.misPollNotifyMsgWaitVal ?: 3600))
    appUpdateNotify()
    // cloudHeartbeatCheck()
}

private missPollNotify(Boolean on, Integer wait) {
    logger("debug", "missPollNotify() | on: ($on) | wait: ($wait) | getLastDevicePollSec: (${getLastDevicePollSec()}) | misPollNotifyWaitVal: (${state?.misPollNotifyWaitVal}) | getLastMisPollMsgSec: (${getLastMisPollMsgSec()})")
    if(!on || !wait || !(getLastDevicePollSec() > (state?.misPollNotifyWaitVal ?: 900))) { return }
    if(!(getLastMisPollMsgSec() > wait.toInteger())) {
        return
    } else {
        def msg = "\nThe app has not received any device data from Echo Speaks service in the last (${getLastDevicePollSec()}) seconds.\nSomething must be wrong with the node server."
        log.warn "${msg.toString().replaceAll("\n", " ")}"
        if(sendMsg("${app.name} Data Refresh Issue", msg)) {
            state?.lastMisPollMsgDt = getDtNow()
        }
        app.getChildDevices(true)?.each { cd-> cd?.sendEvent(name: "DeviceWatch-DeviceStatus", value: "offline", displayed: true, isStateChange: true) }
    }
}

private appUpdateNotify() {
    Boolean on = (settings?.sendAppUpdateMsg != false)
    Boolean appUpd = isAppUpdateAvail()
    Boolean echoDevUpd = isEchoDevUpdateAvail()
    Boolean servUpd = isServerUpdateAvail()
    logger("debug", "appUpdateNotify() | on: (${on}) | appUpd: (${appUpd}) | echoDevUpd: (${echoDevUpd}) | servUpd: (${servUpd}) | getLastUpdMsgSec: ${getLastUpdMsgSec()} | state?.updNotifyWaitVal: ${state?.updNotifyWaitVal}")
    if(getLastUpdMsgSec() > state?.updNotifyWaitVal.toInteger()) {
        if(appUpd || echoDevUpd || servUpd) {
            def str = ""
            str += !appUpd ? "" : "\nEcho Speaks App: v${state?.appData?.versions?.mainApp?.ver?.toString()}"
            str += !echoDevUpd ? "" : "\nEcho Speaks Device: v${state?.appData?.versions?.echoDevice?.ver?.toString()}"
            str += !servUpd ? "" : "\n${state?.onHeroku ? "Heroku Service" : "Node Service"}: v${state?.appData?.versions?.server?.ver?.toString()}"
            sendMsg("Info", "Echo Speaks Update(s) are Available:${str}...\n\nPlease visit the IDE to Update your code...")
            state?.lastUpdMsgDt = getDtNow()
        }
    }
}

Boolean pushStatus() { return (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || settings?.pushoverEnabled) ? ((settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) ? "Push Enabled" : "Enabled") : null }
Integer getLastMsgSec() { return !state?.lastMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastMsgDt, "getLastMsgSec").toInteger() }
Integer getLastUpdMsgSec() { return !state?.lastUpdMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastUpdMsgDt, "getLastUpdMsgSec").toInteger() }
Integer getLastMisPollMsgSec() { return !state?.lastMisPollMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastMisPollMsgDt, "getLastMisPollMsgSec").toInteger() }
Integer getLastVerUpdSec() { return !state?.lastVerUpdDt ? 100000 : GetTimeDiffSeconds(state?.lastVerUpdDt, "getLastVerUpdSec").toInteger() }
Integer getLastDevicePollSec() { return !state?.lastDevDataUpd ? 840 : GetTimeDiffSeconds(state?.lastDevDataUpd, "getLastDevicePollSec").toInteger() }
Integer getLastCookieChkSec() { return !state?.lastCookieChkDt ? 3600 : GetTimeDiffSeconds(state?.lastCookieChkDt, "getLastCookieChkSec").toInteger() }
Boolean getOk2Notify() { 
    Boolean smsOk = (settings?.smsNumbers?.toString()?.length()>=10)
    Boolean pushOk = settings?.usePush
    Boolean pushOver = (settings?.pushoverEnabled && settings?.pushoverDevices)
    Boolean daysOk = quietDaysOk(settings?.quietDays)
    Boolean timeOk = quietTimeOk()
    Boolean modesOk = quietModesOk(settings?.quietModes)
    logger("debug", "getOk2Notify() | smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
    if(!(smsOk || pushOk || pushOver)) { return false }
    if(!(daysOk && modesOk && timeOk)) { return false }
    return true
}
Boolean quietModesOk(List modes) { return (modes && location?.mode?.toString() in modes) ? false : true }
Boolean quietTimeOk() {
    def strtTime = null
    def stopTime = null
    def now = new Date()
    def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
    if(settings?.qStartTime && settings?.qStopTime) {
        if(settings?.qStartInput == "sunset") { strtTime = sun?.sunset }
        else if(settings?.qStartInput == "sunrise") { strtTime = sun?.sunrise }
        else if(settings?.qStartInput == "A specific time" && settings?.qStartTime) { strtTime = settings?.qStartTime }

        if(settings?.qStopInput == "sunset") { stopTime = sun?.sunset }
        else if(settings?.qStopInput == "sunrise") { stopTime = sun?.sunrise }
        else if(settings?.qStopInput == "A specific time" && settings?.qStopTime) { stopTime = settings?.qStopTime }
    } else { return true }
    if(strtTime && stopTime) {
        return timeOfDayIsBetween(strtTime, stopTime, new Date(), location.timeZone) ? false : true
    } else { return true }
}

Boolean quietDaysOk(days) {
    if(days) {
        def dayFmt = new SimpleDateFormat("EEEE")
        if(location?.timeZone) { dayFmt?.setTimeZone(location?.timeZone) }
        return days?.contains(dayFmt?.format(new Date())) ? false : true
    }
    return true
}

// Sends the notifications based on app settings
public sendMsg(String msgTitle, String msg, Boolean showEvt=true, Map pushoverMap=null, sms=null, push=null) { 
    logger("trace", "sendMsg() | msgTitle: ${msgTitle}, msg: ${msg}, showEvt: ${showEvt}")
    String sentstr = "Push"
    Boolean sent = false
    try {
        String newMsg = "${msgTitle}: ${msg}"
        String flatMsg = newMsg.toString().replaceAll("\n", " ")
        if(!getOk2Notify()) {
            log.info "sendMsg: Message Skipped During Quiet Time ($flatMsg)"
            if(showEvt) { sendNotificationEvent(newMsg) }
        } else {
            if(push || settings?.usePush) {
                sentstr = "Push Message"
                if(showEvt) {
                    sendPush(newMsg)	// sends push and notification feed
                } else {
                    sendPushMessage(newMsg)	// sends push
                }
                sent = true
            }
            if(settings?.pushoverEnabled && settings?.pushoverDevices) {
                sentstr = "Pushover Message"
                Map msgObj = [:]
                msgObj = pushoverMap ?: [title: msgTitle, message: msg, priority: (settings?.pushoverPriority?:0)]
                if(settings?.pushoverSound) { msgObj?.sound = settings?.pushoverSound }
                buildPushMessage(settings?.pushoverDevices, msgObj, true)
                sent = true
            }
            String smsPhones = sms ? sms.toString() : (settings?.smsNumbers?.toString() ?: null)
            if(smsPhones) {
                List phones = smsPhones?.toString()?.split("\\,")
                for (phone in phones) {
                    String t0 = newMsg.take(140)
                    if(showEvt) {
                        sendSms(phone?.trim(), t0)	// send SMS and notification feed
                    } else {
                        sendSmsMessage(phone?.trim(), t0)	// send SMS
                    }
                    
                }
                sentstr = "Text Message to Phone [${phones}]"
                sent = true
            }
            if(sent) {
                state?.lastMsg = flatMsg
                state?.lastMsgDt = getDtNow()
                logger("debug", "sendMsg: Sent ${sentstr} (${flatMsg})")
            }
        }
    } catch (ex) {
        incrementCntByKey("appErrorCnt")
        log.error "sendMsg $sentstr Exception:", ex
    }
    return sent
}
String textDonateLink() { return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HWBN4LB9NMHZ4" }
String getAppEndpointUrl(subPath)   { return isST() ? "${apiServerUrl("/api/smartapps/installations/${app.id}${subPath ? "/${subPath}" : ""}?access_token=${state.accessToken}")}" : "${getApiServerUrl()}/${getHubUID()}/apps/${app?.id}${subPath ? "/${subPath}" : ""}?access_token=${state?.accessToken}" }
String getLocalEndpointUrl(subPath) { return "${getLocalApiServerUrl()}/apps/${app?.id}${subPath ? "/${subPath}" : ""}?access_token=${state?.accessToken}" }
//PushOver-Manager Input Generation Functions
private getPushoverSounds(){return (Map) state?.pushoverManager?.sounds?:[:]}
private getPushoverDevices(){List opts=[];Map pmd=state?.pushoverManager?:[:];pmd?.apps?.each{k,v->if(v&&v?.devices&&v?.appId){Map dm=[:];v?.devices?.sort{}?.each{i->dm["${i}_${v?.appId}"]=i};addInputGrp(opts,v?.appName,dm);}};return opts;}
private inputOptGrp(List groups,String title){def group=[values:[],order:groups?.size()];group?.title=title?:"";groups<<group;return groups;}
private addInputValues(List groups,String key,String value){def lg=groups[-1];lg["values"]<<[key:key,value:value,order:lg["values"]?.size()];return groups;}
private listToMap(List original){original.inject([:]){r,v->r[v]=v;return r;}}
private addInputGrp(List groups,String title,values){if(values instanceof List){values=listToMap(values)};values.inject(inputOptGrp(groups,title)){r,k,v->return addInputValues(r,k,v)};return groups;}
private addInputGrp(values){addInputGrp([],null,values)}
//PushOver-Manager Location Event Subscription Events, Polling, and Handlers
public pushover_init(){subscribe(location,"pushoverManager",pushover_handler);pushover_poll()}
public pushover_cleanup(){state?.remove("pushoverManager");unsubscribe("pushoverManager");}
public pushover_poll(){sendLocationEvent(name:"pushoverManagerCmd",value:"poll",data:[empty:true],isStateChange:true,descriptionText:"Sending Poll Event to Pushover-Manager")}
public pushover_msg(List devs,Map data){if(devs&&data){sendLocationEvent(name:"pushoverManagerMsg",value:"sendMsg",data:data,isStateChange:true,descriptionText:"Sending Message to Pushover Devices: ${devs}");}}
public pushover_handler(evt){Map pmd=state?.pushoverManager?:[:];switch(evt?.value){case"refresh":def ed = evt?.jsonData;String id = ed?.appId;Map pA = pmd?.apps?.size() ? pmd?.apps : [:];if(id){pA[id]=pA?."${id}"instanceof Map?pA[id]:[:];pA[id]?.devices=ed?.devices?:[];pA[id]?.appName=ed?.appName;pA[id]?.appId=id;pmd?.apps = pA;};pmd?.sounds=ed?.sounds;break;case "reset":pmd=[:];break;};state?.pushoverManager=pmd;}
//Builds Map Message object to send to Pushover Manager
private buildPushMessage(List devices,Map msgData,timeStamp=false){if(!devices||!msgData){return};Map data=[:];data?.appId=app?.getId();data.devices=devices;data?.msgData=msgData;if(timeStamp){data?.msgData?.timeStamp=new Date().getTime()};pushover_msg(devices,data);}

/******************************************
|       Changelog Logic
******************************************/
String changeLogData() { return getWebData([uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/changelog.txt", contentType: "text/plain; charset=UTF-8"], "changelog") }
Boolean showChgLogOk() { return (state?.isInstalled && state?.installData?.shownChgLog != true) }
def changeLogPage() {
    def execTime = now()
    return dynamicPage(name: "changeLogPage", title: "", nextPage: "mainPage", install: false) {
        section() {
            paragraph title: "What's New in this Release...", "", state: "complete", image: getAppImg("whats_new_icon.png")
            paragraph changeLogData()
        }
        Map iData = atomicState?.installData
        iData["shownChgLog"] = true
        atomicState?.installData = iData
    }
}

/******************************************
|    METRIC Logic
******************************************/
String getFbMetricsUrl() { return state?.appData?.settings?.database?.metricsUrl ?: "https://echo-speaks-metrics.firebaseio.com/" }
Integer getLastMetricUpdSec() { return !state?.lastMetricUpdDt ? 100000 : GetTimeDiffSeconds(state?.lastMetricUpdDt, "getLastMetricUpdSec").toInteger() }
Boolean metricsOk() { return true; }// (settings?.optOutMetrics != true && state?.appData?.settings?.sendMetrics != false) }
private generateGuid() { if(!state?.appGuid) { state?.appGuid = UUID?.randomUUID().toString() } }
private sendInstallData() { if(metricsOk()) { sendFirebaseData(getFbMetricsUrl(), createMetricsDataJson(), "clients/${state?.appGuid}.json", null, "heartbeat") } }
private removeInstallData() { return removeFirebaseData("clients/${state?.appGuid}.json") }
private sendFirebaseData(url, data, pathVal, cmdType=null, type=null) {
    logger("trace", "sendFirebaseData(${data}, ${pathVal}, $cmdType, $type", true)
    return queueFirebaseData(url, data, pathVal, cmdType, type)
}
def queueFirebaseData(url, data, pathVal, cmdType=null, type=null) {
    logger("trace", "queueFirebaseData(${data}, ${pathVal}, $cmdType, $type", true)
    Boolean result = false
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    Map params = [uri: "${url}/${pathVal}", body: json.toString()]
    String typeDesc = type ? type as String : "Data"
    try {
        if(!cmdType || cmdType == "put") {
            asynchttp_v1.put(processFirebaseResponse, params, [type: typeDesc])
            result = true
        } else if (cmdType == "post") {
            asynchttp_v1.post(processFirebaseResponse, params, [type: typeDesc])
            result = true
        } else { log.debug "queueFirebaseData UNKNOWN cmdType: ${cmdType}" }

    } catch(ex) { log.error "queueFirebaseData (type: $typeDesc) Exception:", ex }
    return result
}

def removeFirebaseData(pathVal) {
    logger("trace", "removeFirebaseData(${pathVal})", true)
    Boolean result = true
    try {
        httpDelete(uri: "${getFbMetricsUrl()}/${pathVal}") { resp ->
            logger("debug", "Remove Firebase | resp: ${resp?.status}")
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.ResponseParseException) {
            logger("error", "removeFirebaseData: Response: ${ex?.message}")
        } else {
            logger("error", "removeFirebaseData: Response: ${ex?.message}")
            result = false
        }
    }
    return result
}

def processFirebaseResponse(resp, data) {
    logger("trace", "processFirebaseResponse(${data?.type})", true)
    Boolean result = false
    String typeDesc = data?.type as String
    try {
        if(resp?.status == 200) {
            logger("info", "processFirebaseResponse: ${typeDesc} Data Sent SUCCESSFULLY")
            if(typeDesc?.toString() == "heartbeat") { state?.lastMetricUpdDt = getDtNow() }
            def iData = atomicState?.installData ?: [:]
            iData["sentMetrics"] = true
            atomicState?.installData = iData
            result = true
        }
        else if(resp?.status == 400) { log.error "processFirebaseResponse: 'Bad Request': ${resp?.status}" }
        else { log.warn "processFirebaseResponse: 'Unexpected' Response: ${resp?.status}" }
        if(resp?.hasError()) { log.error "processFirebaseResponse: errorData: ${resp?.errorData} | errorMessage: ${resp?.errorMessage}" }
    } catch(ex) {
        log.error "processFirebaseResponse (type: $typeDesc) Exception:", ex
    }
}

def renderMetricData() {
    try {
        def json = new groovy.json.JsonOutput().prettyPrint(createMetricsDataJson())
        render contentType: "application/json", data: json
    } catch (ex) { log.error "renderMetricData Exception:", ex }
}

private createMetricsDataJson(rendAsMap=false) {
    try {
        generateGuid()
        Map swVer = state?.codeVersions
        Map deviceUsageMap = [:]
        Map deviceErrorMap = [:]
        app?.getChildDevices(true)?.each { d-> 
            Map obj = d?.getDeviceMetrics()
            if(obj?.usage?.size()) {
                obj?.usage?.each { k,v->
                    deviceUsageMap[k as String] = (deviceUsageMap[k as String] ? deviceUsageMap[k as String] + v : v)
                }
            }
            if(obj?.errors?.size()) {
                obj?.errors?.each { k,v->
                    deviceErrorMap[k as String] = (deviceErrorMap[k as String] ? deviceErrorMap[k as String] + v : v)
                }
            }
        }
        def dataObj = [
            guid: state?.appGuid,
            datetime: getDtNow()?.toString(),
            installDt: state?.installData?.dt, 
            updatedDt: state?.installData?.updatedDt,
            timeZone: location?.timeZone?.ID?.toString(),
            stateUsage: "${stateSizePerc()}%",
            amazonDomain: settings?.amazonDomain,
            serverPlatform: state?.onHeroku ? "Cloud" : "Local",
            versions: [app: appVersion(), server: swVer?.server ?: "N/A", device: swVer?.echoDevice ?: "N/A"],
            counts: [
                deviceStyleCnts: state?.deviceStyleCnts ?: [:],
                appHeartbeatCnt: state?.appHeartbeatCnt ?: 0,
                getCookieCnt: state?.getCookieCnt ?: 0,
                appErrorCnt: state?.appErrorCnt ?: 0,
                deviceErrors: deviceErrorMap ?: [:],
                deviceUsage: deviceUsageMap ?: [:]
            ]
        ]
        def json = new groovy.json.JsonOutput().toJson(dataObj)
        return json
    } catch (ex) {
        log.error "createMetricsDataJson: Exception:", ex
    }
}

private incrementCntByKey(String key) {
    long evtCnt = state?."${key}" ?: 0
    // evtCnt = evtCnt?.toLong()+1
    evtCnt++
    logger("trace", "${key?.toString()?.capitalize()}: $evtCnt", true)
    state?."${key}" = evtCnt?.toLong()
}

/******************************************
|    APP/DEVICE Version Functions
*******************************************/
Boolean isCodeUpdateAvailable(String newVer, String curVer, String type) {
    Boolean result = false
    def latestVer
    if(newVer && curVer) {
        List versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b ->
                List verA = a?.tokenize('.')
                List verB = b?.tokenize('.')
                Integer commonIndices = Math.min(verA?.size(), verB?.size())
                for (int i = 0; i < commonIndices; ++i) {
                    //log.debug "comparing $numA and $numB"
                    if(verA[i]?.toInteger() != verB[i]?.toInteger()) {
                        return verA[i]?.toInteger() <=> verB[i]?.toInteger()
                    }
                }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer) ? true : false
        }
    }
    // logger("trace", "isCodeUpdateAvailable | type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result")
    return result
}

Boolean isAppUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.mainApp && isCodeUpdateAvailable(state?.appData?.versions?.mainApp?.ver, state?.codeVersions?.mainApp, "app")) { return true }
    return false
}

Boolean isEchoDevUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.echoDevice && isCodeUpdateAvailable(state?.appData?.versions?.echoDevice?.ver, state?.codeVersions?.echoDevice, "dev")) { return true }
    return false
}

Boolean isServerUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.server && isCodeUpdateAvailable(state?.appData?.versions?.server?.ver, state?.codeVersions?.server, "server")) { return true }
    return false
}

Integer versionStr2Int(str) { return str ? str.toString()?.replaceAll("\\.", "")?.toInteger() : null }

private checkVersionData(now = false) { //This reads a JSON file from GitHub with version numbers
    if (now || !state?.appData || (getLastVerUpdSec() > (3600*6))) {
        if(now && (getLastVerUpdSec() < 300)) { return }
        getConfigData()
    }
}

private getConfigData() {
    def params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/appData.json",
        contentType: "application/json"
    ]
    def data = getWebData(params, "appData", false)
    if(data) {
        log.info "Getting Latest Version Data from appData.json File"
        state?.appData = data
        state?.lastVerUpdDt = getDtNow()
    }
    if(state?.isInstalled) {
        if(getLastMetricUpdSec() > (3600*24)) { runIn(30, "sendInstallData", [overwrite: true]) }
    }
}

private getWebData(params, desc, text=true) {
    try {
        // log.trace("getWebData: ${desc} data")
        httpGet(params) { resp ->
            if(resp?.data) {
                if(text) { return resp?.data?.text.toString() } 
                return resp?.data
            }
        }
    }
    catch (ex) {
        incrementCntByKey("appErrorCnt")
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            log.warn("${desc} file not found")
        } else { log.error "getWebData(params: $params, desc: $desc, text: $text) Exception:", ex }
        return "${label} info not found"
    }
}

/******************************************
|    Time and Date Conversion Functions
*******************************************/
def formatDt(dt, tzChg=true) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone(location?.timeZone) } }
    return tf?.format(dt)
}

String strCapitalize(str) { return str ? str?.toString().capitalize() : null }
String isPluralString(obj) { return (obj?.size() > 1) ? "(s)" : "" }

def parseDt(pFormat, dt, tzFmt=true) {
    def result
    def newDt = Date.parse("$pFormat", dt)
    result = formatDt(newDt, tzFmt)
    //log.debug "parseDt Result: $result"
    return result
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

def epochToTime(tm) {
    def tf = new SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    return tf.format(tm)
}

def time2Str(time) {
    if(time) {
        def t = timeToday(time, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f?.setTimeZone(location?.timeZone ?: timeZone(time))
        return f?.format(t)
    }
}

def GetTimeDiffSeconds(lastDate, sender=null) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        def now = new Date()
        def lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        def start = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt)).getTime()
        def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(now)).getTime()
        def diff = (int) (long) (stop - start) / 1000
        return diff
    }
    catch (ex) {
        log.error "GetTimeDiffSeconds Exception: (${sender ? "$sender | " : ""}lastDate: $lastDate):", ex
        return 10000
    }
}

/******************************************
|   App Input Description Functions
*******************************************/
String getAppNotifConfDesc() {
    String str = ""
    if(pushStatus()) {
        def ap = getAppNotifDesc()
        def nd = getNotifSchedDesc()
        str += (settings?.usePush) ? "${str != "" ? "\n" : ""}Sending via: (Push)" : ""
        str += (settings?.pushoverEnabled) ? "${str != "" ? "\n" : ""}Pushover: (Enabled)" : ""
        str += (settings?.pushoverEnabled && settings?.pushoverPriority) ? "${str != "" ? "\n" : ""} • Priority: (${settings?.pushoverPriority})" : ""
        str += (settings?.pushoverEnabled && settings?.pushoverSound) ? "${str != "" ? "\n" : ""} • Sound: (${settings?.pushoverSound})" : ""
        str += (settings?.phone) ? "${str != "" ? "\n" : ""}Sending via: (SMS)" : ""
        str += (ap) ? "${str != "" ? "\n\n" : ""}Enabled Alerts:\n${ap}" : ""
        str += (ap && nd) ? "${str != "" ? "\n" : ""}\nAlert Restrictions:\n${nd}" : ""
    }
    return str != "" ? str : null
}

String getNotifSchedDesc() {
    def sun = getSunriseAndSunset()
    def startInput = settings?.qStartInput
    def startTime = settings?.qStartTime
    def stopInput = settings?.qStopInput
    def stopTime = settings?.qStopTime
    def dayInput = settings?.quietDays
    def modeInput = settings?.quietModes
    def notifDesc = ""
    def getNotifTimeStartLbl = ( (startInput == "Sunrise" || startInput == "Sunset") ? ( (startInput == "Sunset") ? epochToTime(sun?.sunset?.time) : epochToTime(sun?.sunrise?.time) ) : (startTime ? time2Str(startTime) : "") )
    def getNotifTimeStopLbl = ( (stopInput == "Sunrise" || stopInput == "Sunset") ? ( (stopInput == "Sunset") ? epochToTime(sun?.sunset?.time) : epochToTime(sun?.sunrise?.time) ) : (stopTime ? time2Str(stopTime) : "") )
    notifDesc += (getNotifTimeStartLbl && getNotifTimeStopLbl) ? " • Silent Time: ${getNotifTimeStartLbl} - ${getNotifTimeStopLbl}" : ""
    def days = getInputToStringDesc(dayInput)
    def modes = getInputToStringDesc(modeInput)
    notifDesc += days ? "${(getNotifTimeStartLbl || getNotifTimeStopLbl) ? "\n" : ""} • Silent Day${isPluralString(dayInput)}: ${days}" : ""
    notifDesc += modes ? "${(getNotifTimeStartLbl || getNotifTimeStopLbl || days) ? "\n" : ""} • Silent Mode${isPluralString(modeInput)}: ${modes}" : ""
    return (notifDesc != "") ? "${notifDesc}" : null
}

String getServiceConfDesc() {
    String str = ""
    str += (state?.generatedHerokuName) ? "${str != "" ? "\n" : ""}Heroku Info:" : ""
    str += (state?.generatedHerokuName) ? "${str != "" ? "\n" : ""} • App Name: ${state?.generatedHerokuName}" : ""
    str += (settings?.amazonDomain) ? "${str != "" ? "\n" : ""} • Amazon Domain : (${settings?.amazonDomain})" : ""
    str += (settings?.refreshSeconds) ? "${str != "" ? "\n" : ""} • Refresh Seconds : (${settings?.refreshSeconds}sec)" : ""
    // str += (settings?.stHub) ? "${str != "" ? "\n\n" : ""}Hub Info:" : ""
    // str += (settings?.stHub) ? "${str != "" ? "\n" : ""} • IP: ${settings?.stHub?.getLocalIP()}" : ""
    // str += (settings?.refreshSeconds) ? "\n\nServer Push Settings:" : ""
    // str += (settings?.refreshSeconds) ? "${str != "" ? "\n" : ""} • Refresh Seconds : (${settings?.refreshSeconds}sec)" : ""
    return str != "" ? str : null
}

String getAppNotifDesc() {
    def str = ""
    str += settings?.sendMissedPollMsg != false ? "${str != "" ? "\n" : ""} • Missed Poll Alerts" : ""
    str += settings?.sendAppUpdateMsg != false ? "${str != "" ? "\n" : ""} • Code Updates" : ""
    return str != "" ? str : null
}

String getServInfoDesc() {
    Map rData = state?.nodeServiceInfo	
    String str = ""
    String dtstr = ""
    if(rData?.startupDt) {
        def dt = rData?.startupDt
        dtstr += dt?.y ? "${dt?.y}yr${dt?.y > 1 ? "s" : ""}, " : ""
        dtstr += dt?.mn ? "${dt?.mn}mon${dt?.mn > 1 ? "s" : ""}, " : ""
        dtstr += dt?.d ? "${dt?.d}day${dt?.d > 1 ? "s" : ""}, " : ""
        dtstr += dt?.h ? "${dt?.h}hr${dt?.h > 1 ? "s" : ""} " : ""
        dtstr += dt?.m ? "${dt?.m}min${dt?.m > 1 ? "s" : ""} " : ""
        dtstr += dt?.s ? "${dt?.s}sec" : ""
    }
    if(settings?.useHeroku && state?.onHeroku) {
        str += " ├ App Name: (${state?.generatedHerokuName})\n"
    }
    str += " ├ IP: (${rData?.ip})"
    str += "\n ├ Port: (${rData?.port})"
    str += "\n ├ Version: (v${rData?.version})"
    str += "\n ${dtstr != "" ? "├" : "└"} Session Events: (${rData?.sessionEvts})"
    str += dtstr != "" ? "\n └ Uptime: ${dtstr.length() > 20 ? "\n     └ ${dtstr}" : "${dtstr}"}" : ""
    return str != "" ? str : null
}

String getInputToStringDesc(inpt, addSpace = null) {
    Integer cnt = 0
    String str = ""
    if(inpt) {
        inpt.sort().each { item ->
            cnt = cnt+1
            str += item ? (((cnt < 1) || (inpt?.size() > 1)) ? "\n      ${item}" : "${addSpace ? "      " : ""}${item}") : ""
        }
    }
    //log.debug "str: $str"
    return (str != "") ? "${str}" : null
}

String randomString(Integer len) {
    def pool = ["a".."z",0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    log.debug "randomString: ${randChars?.join()}"
    return randChars.join()
}

def getAccessToken() {
    try {
        if(!state?.accessToken) { state?.accessToken = createAccessToken() }
        else { return true }
    }
    catch (ex) {
        // sendPush("Error: OAuth is not Enabled for ${appName()}!. Please click remove and Enable Oauth under the SmartApp App Settings in the IDE")
        log.error "getAccessToken Exception", ex
        return false
    }
}

def renderConfig() {
    String title = "Echo Speaks"
    String html = """<head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="${title}">
        <meta name="author" content="Anthony S.">
        <meta http-equiv="cleartype" content="on">
        <meta name="MobileOptimized" content="320">
        <meta name="HandheldFriendly" content="True">
        <meta name="apple-mobile-web-app-capable" content="yes">
        <title>${title}</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.5.13/css/mdb.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet">
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.4/umd/popper.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
        <style>
            .btn-rounded {
                border-radius: 50px!important;
            }
            span img {
                width: 48px;
                height: auto;
            }
            span p {
                display: block;
            }
            .all-copy p {  
                -webkit-user-select: all;
                -moz-user-select: all;
                -ms-user-select: all;
                user-select: all;
            }
            .nameContainer {
                border-radius: 18px;
                color: rgba(255,255,255,1);
                font-size: 1.5rem;
                background: #666;
                -webkit-box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3) ;
                box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3) ;
                text-shadow: 1px 1px 1px rgba(0,0,0,0.2) ;
            }
        </style>
    <head>
    <body>
        <div style="margin: 0 auto; max-width: 500px;">
            <form class="p-1">
                <div class="my-3 text-center">
                    <span>
                        <img src="${getAppImg("echo_speaks.1x.png")}"/>
                        <p class="h4 text-center">Echo Speaks</p>
                    </span>
                </div>
                <hr>
                <div class="w-100 mb-3">
                    <div class="my-2 text-center">
                        <h5>1. Copy the following Name and use it when asked by Heroku</h5>
                        <div class="all-copy nameContainer mx-5 mb-2 p-1">
                          <p id="copyName" class="m-0 p-0">${getRandAppName()?.toString().trim()}</p>
                        </div>
                    </div>
                    <div class="my-2 text-center">
                        <h5>2. Tap Button to deploy to Heroku</h5>
                        <a href="https://heroku.com/deploy?template=https://github.com/tonesto7/echo-speaks-server/tree/dev${getEnvParamsStr()}">
                            <img src="https://www.herokucdn.com/deploy/button.svg" alt="Deploy">
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </body>
    <script>
        \$("#copyName").on("click", function () {
            console.log("click")
            \$(this).select();
        });
        \$('#generateEmail').click(function() {
            \$("#generateEmail").attr("href", "mailto:example@email.com?subject=Echo Speaks URL Info&body=${getAppEndpointUrl("receiveData")}").attr("target", "_blank");
        });
    </script>
    """
    render contentType: "text/html", data: html
}
Integer stateSize() {
	def j = new groovy.json.JsonOutput().toJson(state)
	return j?.toString().length()
}
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0) }
String debugStatus() { return !settings?.appDebug ? "Off" : "On" }
String deviceDebugStatus() { return !settings?.childDebug ? "Off" : "On" }
Boolean isAppDebug() { return (settings?.appDebug == true) }
Boolean isChildDebug() { return (settings?.childDebug == true) }

String getAppDebugDesc() {
    def str = ""
    str += isAppDebug() ? "App Debug: (${debugStatus()})" : ""
    str += isChildDebug() && str != "" ? "\n" : ""
    str += isChildDebug() ? "Device Debug: (${deviceDebugStatus()})" : ""
    return (str != "") ? "${str}" : null
}

private logger(type, msg, traceOnly=false) {
    if (traceOnly && !settings?.appTrace) { return }
    if(type && msg && settings?.appDebug) {
        log."${type}" "${msg}"
    }
}
