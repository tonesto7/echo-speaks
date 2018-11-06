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
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
import java.text.SimpleDateFormat
include 'asynchttp_v1'

String platform() { return "SmartThings" }
String appVersion()	 { return "1.0.8" }
String appModified() { return "2018-11-06"} 
String appAuthor()	 { return "Anthony Santilli" }
Boolean isST() { return (platform() == "SmartThings") }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }
Map minVersions() { //These define the minimum versions of code this app will work with.
    return [echoDevice: 104, server: 105]
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
    page(name: "newSetupPage")
    page(name: "devicePage")
    page(name: "notifPrefPage")
    page(name: "servPrefPage")
    page(name: "setNotificationTimePage")
    page(name: "uninstallPage")
}

def appInfoSect(sect=true)	{
    def str = ""
    str += "${app?.name}"
    str += "\nAuthor: ${appAuthor()}"
    str += "\nVersion: ${appVersion()}"
    section() { paragraph str, image: getAppImg("echo_speaks.2x.png") }
}

def mainPage() {
    def tokenOk = getAccessToken()
    checkVersionData(true)
    Boolean newInstall = !state?.isInstalled
    
    if(state?.resumeConfig) {
        return servPrefPage()
    } else {
        return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "servPrefPage"), uninstall: false, install: !newInstall) {
            appInfoSect()
            if(!tokenOk) {
                paragraph title: "Uh OH!!!", "Oauth Has NOT BEEN ENABLED. Please Remove this app and try again after it after enabling OAUTH"
                return
            }
            if(!newInstall) {
                section("Echo Service:") {
                    def t0 = getServiceConfDesc()
                    href "servPrefPage", title: "Echo Service\nSettings", description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("settings.png")
                }
            }
            section("Device Preferences:") {
                if(!newInstall) {
                    List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : ""}" }?.sort()
                    paragraph title: "Discovered Devices:", "${devs?.size() ? devs?.join("\n") : "No Devices Available"}", state: "complete"
                }
                input "autoCreateDevices", "bool", title: "Auto Create New Devices?", description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("devices.png")
                input "createTablets", "bool", title: "Create Devices for Tablets?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("amazon_tablet.png")
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
            section ("Application Logs") {
                input (name: "appDebug", type: "bool", title: "Show Debug Logs in the IDE?", description: "Only leave on when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug.png"))
                if(appDebug) {
                    input (name: "appTrace", type: "bool", title: "Show Detailed Trace Logs in the IDE?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug.png"))
                }
            }
            if(!newInstall) {
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
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/settings", style: "external", required: false, title: "Heroku App Settings", description: "Tap to proceed", image: getAppImg("heroku.png")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/webhooks", style: "external", required: false, title: "Heroku App Webhooks", description: "Tap to proceed", image: getAppImg("heroku.png")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/logs", style: "external", required: false, title: "Heroku App Logs", description: "Tap to proceed", image: getAppImg("heroku.png")
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
                    input (name: "misPollNotifyWaitVal", type: "enum", title: "Time Past the Missed Checkin?", required: false, defaultValue: 900, metadata: [values:notifValEnum()], submitOnChange: true, image: getAppImg("delay_time.png"))
                    if(settings?.misPollNotifyWaitVal) { pollWait = settings?.misPollNotifyWaitVal as Integer }
                    
                    def misPollNotifyMsgWaitValDesc = settings?.misPollNotifyMsgWaitVal ?: "Default: 1 Hour"
                    input (name: "misPollNotifyMsgWaitVal", type: "enum", title: "Send Reminder After?", required: false, defaultValue: 3600, metadata: [values:notifValEnum()], submitOnChange: true, image: getAppImg("reminder.png"))
                    if(settings?.misPollNotifyMsgWaitVal) { pollMsgWait = settings?.misPollNotifyMsgWaitVal as Integer }
                }
            }
            section("Code Update Alerts:") {
                input (name: "sendAppUpdateMsg", type: "bool", title: "Send for Updates...", defaultValue: true, submitOnChange: true, image: getAppImg("update.png"))
                if(settings?.sendAppUpdateMsg) {
                    def updNotifyWaitValDesc = settings?.updNotifyWaitVal ?: "Default: 2 Hours"
                    input (name: "updNotifyWaitVal", type: "enum", title: "Send Reminders After?", required: false, defaultValue: 7200, metadata: [values:notifValEnum()], submitOnChange: true, image: getAppImg("reminder.png"))
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
    state?.isInstalled = true
    sendInstallNotif(true)
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    if(!state?.isInstalled) { state?.isInstalled = true }
    sendInstallNotif()
    unsubscribe()
    initialize()
}

def uninstalled() {
    log.warn "uninstalling app and devices"
}

void settingUpdate(name, value, type=null) {
    if(name && type) {
        app?.updateSetting("$name", [type: "$type", value: value])
    }
    else if (name && type == null){ app?.updateSetting(name.toString(), value) }
}

mappings {
    path("/receiveData") { action: [POST: "processData"] }
    path("/config") { action: [GET: "renderConfig"]  }
    path("/cookie") { action: [GET: "getCookie", POST: "storeCookie", DELETE: "clearCookie"] }
}

def initialize() {
    // listen to LAN incoming messages
    def tokenOk = getAccessToken()
    if(app?.getLabel() != "Echo Speaks") { app?.updateLabel("Echo Speaks") }
    
    runEvery5Minutes("notificationCheck") // This task checks for missed polls and app updates
    subscribe(app, onAppTouch)
    if(!settings?.useHeroku && settings?.stHub) { subscribe(location, null, lanEventHandler, [filterEvents:false]) }
    resetQueue()
    stateCleanup()
    updCodeVerMap()
    if(!settings?.useHeroku) {
        runIn(5, "echoServiceUpdate", [overwrite: true])
    }
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
                        <a href="https://heroku.com/deploy?template=https://github.com/tonesto7/echo-speaks-server${getEnvParamsStr()}">
                            <img src="https://www.herokucdn.com/deploy/button.svg" alt="Deploy">
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </body>
    <script>
        \$("#copyName").on("click", function () {
            console.log('click')
            \$(this).select();
        });
        \$('#generateEmail').click(function() {
            \$("#generateEmail").attr("href", "mailto:example@email.com?subject=Echo Speaks URL Info&body=${getAppEndpointUrl("receiveData")}").attr("target", "_blank");
        });
    </script>
    """
    render contentType: "text/html", data: html
}

String getEnvParamsStr() {
    Map envParams = [:]
    envParams['smartThingsUrl'] = "${getAppEndpointUrl("receiveData")}"
    envParams['useHeroku'] = settings?.useHeroku == true ? "true" : "false"
    envParams['serviceDebug'] = settings?.serviceDebug == true ? "true" : "false"
    envParams['amazonDomain'] = settings?.amazonDomain as String
    envParams['refreshSeconds'] = settings?.refreshSeconds as String
    envParams['hostUrl'] = "${getRandAppName()}.herokuapp.com"
    envParams['HEROKU_APP_NAME'] = "${getRandAppName()}"

    String envs = ""
    envParams?.each { k, v-> envs += "&env[${k}]=${v}" }
    return envs
}

private checkIfCodeUpdated() {
    if(state?.codeVersions && state?.codeVersions?.mainApp != appVersion()) {
        log.info "Code Version Change! Re-Initializing SmartApp in 5 seconds..."
        state?.pollBlocked = true
        runIn(5, "updated", [overwrite: false])
        return true
    }
    state?.pollBlocked = false
    return false
}

private stateCleanup() {
    List items = ["availableDevices", "lastMsgDt", "consecutiveCmdCnt"]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    state?.isRateLimiting = false 
    state?.pollBlocked = false
    state?.resumeConfig = false
    state?.heartbeatScheduled = false
}

def onAppTouch(evt) {
    // log.trace "appTouch..."
    app?.getChildDevices(true)?.each { cDev->
        cDev?.resetQueue()
    }
}

private resetQueue() {
    Map cmdQueue = state?.findAll { it?.key?.toString()?.startsWith("cmdQueueItem_") }
    cmdQueue?.each { cmdKey, cmdData ->
        state?.remove(cmdKey)
    }
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
    if(!state?.generatedHerokuName) { state?.generatedHerokuName = "${app?.name?.toString().replaceAll(" ", "-")}-${randomString(6)}"?.toLowerCase() }
    return state?.generatedHerokuName as String
}

def lanEventHandler(evt) {
    // log.trace "lanStreamEvtHandler..."
    def msg = parseLanMessage(evt?.description)
    Map headerMap = msg?.headers
    logger("trace", "lanEventHandler... | headers: ${headerMap}", true)
    try {
        Map msgData = [:]
        if (headerMap?.size()) {
            if (headerMap?.evtSource && headerMap?.evtSource == "Echo_Speaks") {
                if (msg?.body != null) {
                    def slurper = new groovy.json.JsonSlurper()
                    msgData = slurper?.parseText(msg?.body as String)
                    logger("debug", "msgData: $msgData", true)
                    if(headerMap?.evtType) { 
                        switch(headerMap?.evtType) {
                            case "sendStatusData":
                                receiveEventData(msgData, "Local")
                                break
                        }
                    }
                }
            }
        }
    } catch (ex) {
        log.error "lanEventHandler Exception:", ex
    }
}

def processData() {
    // log.trace "processData() | Data: ${request.JSON}"
    Map data = request?.JSON as Map
    if(data) {
        if(data && (data?.echoDevices || data?.serviceInfo)) {
            receiveEventData(data, "Cloud")
        } else {
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
    }
    render contentType: 'text/html', data: "status received...ok", status: 200
}

def getCookie() {
    log.trace "getCookie() Request Received..."
    Map resp = state?.cookie ?: [:]
    def json = new groovy.json.JsonOutput().toJson(resp)
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
}

def clearCookie() {
    logger("trace", "clearCookie()")
    settingUpdate("resetCookies", "false", "bool")
    state?.remove('cookie')
    unschedule("cloudServiceHeartbeat")
}

def scheduleHeartbeat() {
    log.info "Scheduling CloudHeartbeat Check for Every 5 minutes..."
    unschedule("cloudServiceHeartbeat")
    state?.heartbeatScheduled = true
    runEvery5Minutes('cloudServiceHeartbeat')
}

def sendOneHeartbeat() { cloudServiceHeartbeat() }

def cloudServiceHeartbeat() {
    // log.trace "cloud keep alive heartbeat"
    try {
        httpGet([uri: "https://${getRandAppName()}.herokuapp.com/heartbeat", contentType: 'application/json']) { resp->
            if(resp && resp?.data && resp?.data?.result) {
                log.info "CloudHeartBeat Successful"
            } else { log.warn "No CloudHeartbeat Response Received... Is the App still available?" }
        }
    } catch(ex) {
        log.error "cloudServiceHeartbeat Exception: ", ex
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
            Boolean onHeroku = (evtData?.useHeroku == true)
            state?.serviceConfigured = true
            // log.debug "onHeroku: ${evtData?.useHeroku} | cloudUrl: ${evtData?.cloudUrl}"
            state?.onHeroku = onHeroku
            state?.cloudUrl = (onHeroku && evtData?.cloudUrl) ? evtData?.cloudUrl : null
            if(onHeroku && !state?.heartbeatScheduled) {
                scheduleHeartbeat()
            }
            
            //Check for minimum versions before processing
            Boolean updRequired = false
            List updRequiredItems = []
            ["server":"Echo Speaks Server", "echoDevice":"Echo Virtual Device"]?.each { k,v->
                Map codeVers = state?.codeVersions
                if(codeVers && codeVers[k as String] && (versionStr2Int(codeVers[k as String]) < minVersions()[k as String])) { 
                    updRequired = true
                    updRequiredItems?.push("$v")
                }
            }
            
            if (evtData?.echoDevices?.size()) {
                log.debug "Device Data Received for (${evtData?.echoDevices?.size()}) Echo Devices${src ? " [$src]" : ""}"
                Map echoDeviceMap = [:]
                List curDevFamily = []
                Integer cnt = 0
                evtData?.echoDevices?.each { echoKey, echoValue->
                    logger("debug", "echoDevice | $echoKey | ${echoValue}", true)
                    logger("debug", "echoDevice | ${echoValue?.accountName}", false)
                    echoDeviceMap[echoKey] = [name: echoValue?.accountName, online: echoValue?.online]
                    if(echoValue?.serialNumber in ignoreTheseDevs) { 
                        logger("warn", "skipping ${echoValue?.accountName} because it is in the do not use list...")
                        return 
                    }
                    if(!settings?.createTablets && echoValue?.deviceFamily == "TABLET") {
                        // logger("warn", "skipping ${echoValue?.accountName} because Tablets are not enabled...")
                        return 
                    }
                    String dni = [app?.id, "echoSpeaks", echoKey].join('|')
                    def childDevice = getChildDevice(dni)
                    String devLabel = "Echo - " + echoValue?.accountName
                    String childHandlerName = "Echo Speaks Device"
                    String hubId = settings?.stHub?.getId()
                    echoValue["deviceStyle"] = getDeviceStyle(echoValue?.deviceFamily as String, echoValue?.deviceType as String)
                    if(!updRequired) {
                        if (!childDevice) {
                            try{
                                log.debug "Creating NEW Echo Speaks Device!!! | Device Label: ($devLabel)"
                                childDevice = addChildDevice("tonesto7", childHandlerName, dni, hubId, [name: childHandlerName, label: devLabel, completedSetup: true])
                            } catch(physicalgraph.app.exception.UnknownDeviceTypeException ex) {
                                log.error "AddDevice Error! ", ex
                            }
                        } else {
                            //Check and see if name needs a refresh
                            if (settings?.autoRenameDevices != false && childDevice?.name != childHandlerName || childDevice?.label != devLabel) {
                                log.debug ("Updating device name (old label was " + childDevice?.label + " | old name was " + childDevice?.name + " new hotness: " + devLabel)
                                childDevice?.name = childHandlerName
                                childDevice?.label = devLabel
                            }
                        }
                        // logger("info", "Sending Device Data Update to ${devLabel} | Last Updated (${getLastDevicePollSec()}sec ago)")
                        childDevice?.updateDeviceStatus(echoValue)
                        childDevice?.updateServiceInfo(getServiceHostInfo(), onHeroku)
                    }
                    modCodeVerMap("echoDevice", childDevice?.devVersion()) // Update device versions in codeVersion state Map
                    curDevFamily.push(echoValue?.deviceStyle?.name)
                    state?.lastDevDataUpd = getDtNow()
                }
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
                if(sendSetUpd && !onHeroku) { echoServiceUpdate() }
            }
            if(updRequired) {
                log.error "CODE UPDATES REQUIRED: Echo Speaks Integration will not function until the following items are ALL Updated ${updRequiredItems}..."
                appUpdateNotify()
            }
        }
    } catch(ex) {
        log.error "receiveEventData Error:", ex
    }
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

private echoServiceUpdate() {
    // log.trace("echoServiceUpdate")
    String host = getServiceHostInfo()
    String smartThingsHubIp = settings?.stHub?.getLocalIP()
    if(!host) { return }
    
    logger("trace", "echoServiceUpdate host: ${host}")
    try {
        def hubAction = new physicalgraph.device.HubAction(
            method: "POST",
            headers: [
                "HOST": host,
                "smartThingsHubIp": "${smartThingsHubIp}",
                "refreshSeconds": settings?.refreshSeconds
            ],
            path: "/updateSettings",
            body: ""
        )
        sendHubCommand(hubAction)
    }
    catch (Exception e) {
        log.error "echoServiceUpdate HubAction Exception, $hubAction", ex
    }
}

public checkIsRateLimiting() {
    return (state?.isRateLimiting == true)
}

private incCmdCnt() {
    state?.consecutiveCmdCnt = state?.consecutiveCmdCnt ? (state?.consecutiveCmdCnt?.toInteger() + 1) : 1
}

public rateLimitTracking(device) {
    // log.trace "rateLimitTracking(${device?.getDisplayName()})"
    Integer conCmdCnt = state?.consecutiveCmdCnt ? state?.consecutiveCmdCnt+1 : 1
    state?.consecutiveCmdCnt = conCmdCnt
    log.debug "consecutiveCmdCnt: ${conCmdCnt}"
    if(conCmdCnt > 5) {
        log.warn "Rate Limiting Active! Clearing Limit in 4 seconds..."
        state?.isRateLimiting = true
        runIn(4, "clearRateLimit", [overwrite: true])
        return true
    } else {
        runIn(4, "clearRateLimit", [overwrite: true])
        return false
    }
}

private clearRateLimit(devUpd = true) {
    log.trace "clearRateLimit(devUpd: $devUpd)"
    if(devUpd && state?.isRateLimiting) {
        state?.isRateLimiting = false
        app?.getChildDevices(true)?.each { cDev->
            cDev?.checkQueue()
        }
    }
    state?.isRateLimiting = false
}

public getDeviceStyle(String family, String type) {
    switch(family) {
        case "KNIGHT":
            switch (type) {
                case "A1NL4BVLQ4L3N3":
                    return [name: "Echo Show (Gen1)", image: "echo_show_gen1"]
                case "AWZZ5CVHX2CD":
                    return [name: "Echo Show (Gen2)", image: "echo_show_gen2"]
                default:
                    return [name: "Echo Show (Gen1)", image: "echo_show_gen1"]
            }
        case "ECHO":
            switch (type) {
                case "AB72C64C86AW2":
                    return [name: "Echo (Gen1)", image: "echo_gen1"]
                case "AKNO1N0KSFN8L":
                    return [name: "Echo Dot (Gen1)", image: "echo_dot_gen1"]
                case "A3S5BH2HU6VAYF":
                    return [name: "Echo Dot (Gen2)", image: "echo_dot_gen2"]
                default:
                    return [name: "Echo (Gen1)", image: "echo_gen2"]
            }
        case "ROOK":
            switch (type) {
                case "A10A33FOX2NUBK":
                    return [name: "Echo Spot (Gen1)", image: "echo_spot_gen1"]
                default:
                    return [name: "Echo Spot (Gen1)", image: "echo_spot_gen1"]
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

/******************************************
|    Notification Functions
*******************************************/
Map notifValEnum(allowCust = true) {
    Map items = [
        300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    if(allowCust) { items[100000] = "Custom" }
    return items
}

private notificationCheck() {
    // logger("trace", "notificationCheck")
    updCodeVerMap()
    checkVersionData()
    if(!getOk2Notify()) { return }
    missPollNotify((settings?.sendMissedPollMsg == true), (state?.misPollNotifyMsgWaitVal ?: 3600))
    appUpdateNotify()
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
            str += !echoDevUpd ? "" : "\nEcho Virtual Device: v${state?.appData?.versions?.echoDevice?.ver?.toString()}"
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
        uri:  "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/appData.json",
        contentType: 'application/json'
    ]
    try {
        httpGet(params) { resp->
            if(resp?.data) {
                log.info "Getting Latest Version Data from appData.json File"
                state?.appData = resp?.data
                state?.lastVerUpdDt = getDtNow()
            }
        }
    } catch(ex) {
        log.error "getConfigData Exception: ", ex
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
    if(state?.onHeroku) {
        str += (state?.generatedHerokuName) ? "${str != "" ? "\n" : ""}Heroku Info:" : ""
        str += (state?.generatedHerokuName) ? "${str != "" ? "\n" : ""} • App Name: ${state?.generatedHerokuName}" : ""
        str += (settings?.amazonDomain) ? "${str != "" ? "\n" : ""} • Amazon Domain : (${settings?.amazonDomain})" : ""
        str += (settings?.refreshSeconds) ? "${str != "" ? "\n" : ""} • Refresh Seconds : (${settings?.refreshSeconds}sec)" : ""
    } else {
        str += (settings?.stHub) ? "${str != "" ? "\n\n" : ""}Hub Info:" : ""
        str += (settings?.stHub) ? "${str != "" ? "\n" : ""} • IP: ${settings?.stHub?.getLocalIP()}" : ""
        str += (settings?.refreshSeconds) ? "\n\nServer Push Settings:" : ""
        str += (settings?.refreshSeconds) ? "${str != "" ? "\n" : ""} • Refresh Seconds : (${settings?.refreshSeconds}sec)" : ""
    }
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

private sendInstallNotif(inst=false) {
    String url = inst ? "https://hooks.slack.com/services/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" : "https://hooks.slack.com/services/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"
    if(inst && state?.appData && state?.appData?.settings?.installNotif == false) { return }
    if(!inst && state?.appData && state?.appData?.settings?.updateNotif == false) { return }
    Map res = [:]
    def str = ""
    def swVer = state?.codeVersions
    str += "\n ────── ${location?.id} ──────"
    str += "\n • DateTime: (${getDtNow()})"
    str += "\n • TimeZone: [${location?.timeZone?.ID?.toString()}]"
    str += "\n • Amazon Domain: (${settings?.amazonDomain})"
    str += "\n ────────── Version Info ──────────"
    str += swVer?.server != null ? "\n • Server: (${swVer?.server}) [${state?.onHeroku ? "Cloud" : "Local"}]" : ""
    str += "\n • SmartApp: (${appVersion()})"
    str += swVer?.echoDevice != null ? "\n • Device: (${swVer?.echoDevice})" : ""
    def ch = app?.getChildDevices(true)?.size() ?: 0
    if (ch >= 1) {
        str += "\n ──────── Device Info ────────"
        if(state?.deviceStyleCnts?.size()) {
            state?.deviceStyleCnts?.each { k,v-> str += "\n • ${k}: (${(v ?: 0)})" }
        } else { str += "\n • Echo Devices: (${(ch ?: 0)})" }
        str += "\n ────────────────────────"
    }
    res["username"] = "Echo Speaks Instance ${inst ? "Installed" : "Updated"}"
    res["channel"] = inst ? "#new_installs" : "#updated_installs"
    res["text"] = str
    def json = new groovy.json.JsonOutput().toJson(res)
    sendInstallData(url, json, "", "post", "${inst ? "App Install" : "App Update"} Notif")
}

private sendInstallData(url, data, pathVal, cmdType=null, type=null) {
    // logger("trace", "sendInstallData(${data}, ${pathVal}, $cmdType, $type")
    def result = false
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    def params = [ uri: url, body: json.toString() ]
    def typeDesc = type ? "${type}" : "Install Data"
    def respData
    try {
        if(!cmdType || cmdType == "post") {
            httpPostJson(params)
            result = true
        }
    }
    catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            log.error("sendInstallData: 'HttpResponseException': ${ex?.message}")
        }
        else { log.error "sendInstallData: ([$data, $pathVal, $cmdType, $type]) Exception:", ex }
    }
    return result
}

String randomString(Integer len) {
    def pool = ['a'..'z',0..9].flatten()
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

def debugStatus() { return !settings?.appDebug ? "Off" : "On" }
def deviceDebugStatus() { return !settings?.childDebug ? "Off" : "On" }
def isAppDebug() { return !settings?.appDebug ? false : true }
def isChildDebug() { return !settings?.childDebug ? false : true }

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
