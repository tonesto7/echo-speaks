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
String appVersion()	 { return "3.1.1.0" }
String appModified()  { return "2019-10-02" }
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
    page(name: "codeUpdatePage")
    page(name: "mainPage", install: false, uninstall: false)
    page(name: "prefsPage")
    page(name: "conditionsPage")
    page(name: "condTimePage")
    page(name: "zoneNotifPage")
    page(name: "zoneNotifTimePage")
    page(name: "uninstallPage")
    page(name: "namePage")
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
    Boolean newInstall = (state?.isInstalled != true)
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: (newInstall == true), install: !newInstall) {
        appInfoSect()
        Boolean paused = isPaused()
        Boolean dup = (state?.dupPendingSetup == true)
        if(dup) {
            section() {
                paragraph pTS("This Zone was just created from an existing zone.  Please review the settings and save to activate...", getAppImg("pause_orange", true), false, "red"), required: true, state: null, image: getAppImg("pause_orange")
            }
        }
        if(paused) {
            section() {
                paragraph pTS("This Action is currently in a paused state...\nTo edit the please un-pause", getAppImg("pause_orange", true), false, "red"), required: true, state: null, image: getAppImg("pause_orange")
            }
        } else {
            Boolean condConf = conditionsConfigured()
            section(sTS("Zone Configuration:")) {
                href "conditionsPage", title: inTS("Zone Activation Conditions\n(Optional)", getAppImg("conditions", true)), description: getConditionsDesc(), required: true, state: (condConf ? "complete": null), image: getAppImg("conditions")
                if(condConf) {
                    echoDevicesInputByPerm("announce")
                }
            }

            if(settings?.zone_EchoDevices) {
                section(sTS("Activation Delay:")) {
                    input "zone_delay", "number", title: inTS("Delay Activation in Seconds\n(Optional)", getAppImg("delay_time", true)), required: false, submitOnChange: true, image: getAppImg("delay_time")
                }
            }
        }
        section(sTS("Preferences")) {
            if(!paused) {
                href "prefsPage", title: inTS("Debug/Preferences", getAppImg("settings", true)), description: "", image: getAppImg("settings")
            }
            if(state?.isInstalled) {
                input "zonePause", "bool", title: inTS("Pause Action?", getAppImg("pause_orange", true)), defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange")
                if(zonePause) { unsubscribe() }
            }
        }
        if(state?.isInstalled) {
            section(sTS("Name this Zone:")) {
                input "appLbl", "text", title: inTS("Action Name", getAppImg("name_tag", true)), description: "", required:true, submitOnChange: true, image: getAppImg("name_tag")
            }
            section(sTS("Remove Zone:")) {
                href "uninstallPage", title: inTS("Remove this Action", getAppImg("uninstall", true)), description: "Tap to Remove...", image: getAppImg("uninstall")
            }
            section(sTS("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                def issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28BUG%29+"
                def featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BFeature+Request%5D"
                href url: featUrl, style: "external", required: false, title: inTS("New Feature Request", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
                href url: issueUrl, style: "external", required: false, title: inTS("Report an Issue", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
            }
        }
    }
}

private echoDevicesInputByPerm(type) {
    List echoDevs = parent?.getChildDevicesByCap(type as String)
    // section(sTS("Alexa Devices in this zone:")) {
        if(echoDevs?.size()) {
            def eDevsMap = echoDevs?.collectEntries { [(it?.getId()): [label: it?.getLabel(), lsd: (it?.currentWasLastSpokenToDevice?.toString() == "true")]] }?.sort { a,b -> b?.value?.lsd <=> a?.value?.lsd ?: a?.value?.label <=> b?.value?.label }
            input "zone_EchoDevices", "enum", title: inTS("Echo Devices in Zone", getAppImg("echo_gen1", true)), description: "Select the devices", options: eDevsMap?.collectEntries { [(it?.key): "${it?.value?.label}${(it?.value?.lsd == true) ? "\n(Last Spoken To)" : ""}"] }, multiple: true, required: true, submitOnChange: true, image: getAppImg("echo_gen1")
        } else { paragraph "No devices were found with support for ($type)"}
    // }
}

def prefsPage() {
    return dynamicPage(name: "prefsPage", install: false, uninstall: false) {
        section(sTS("Logging:")) {
            input "logInfo", "bool", title: inTS("Show Info Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logWarn", "bool", title: inTS("Show Warning Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logError", "bool", title: inTS("Show Error Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logDebug", "bool", title: inTS("Show Debug Logs?", getAppImg("debug", true)), description: "Only leave on when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug")
            input "logTrace", "bool", title: inTS("Show Detailed Logs?", getAppImg("debug", true)), description: "Only Enabled when asked by the developer", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug")
        }
        section(sTS("Other:")) {
            input "clrEvtHistory", "bool", title: inTS("Clear Device Event History?", getAppImg("reset", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("reset")
            if(clrEvtHistory) { clearEvtHistory() }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section(sTS("Name this Automation:")) {
            input "appLbl", "text", title: inTS("Name this Zone", getAppImg("name_tag", true)), description: "", required:true, submitOnChange: true, image: getAppImg("name_tag")
        }
    }
}

/******************************************************************************
    CONDITIONS SELECTION PAGE
******************************************************************************/

def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: "", nextPage: "mainPage", install: false, uninstall: false) {
        section() {
            paragraph pTS("Notice:\nAll selected conditions must pass before this zone will be marked active.", null, false, "#2784D9"), state: "complete"
        }
        section(sTS("Time/Date")) {
            href "condTimePage", title: inTS("Time Schedule", getAppImg("clock", true)), description: getTimeCondDesc(false), state: (timeCondConfigured() ? "complete" : null), image: getAppImg("clock")
            input "cond_days", "enum", title: inTS("Days of the week", getAppImg("day_calendar", true)), multiple: true, required: false, submitOnChange: true, options: weekDaysEnum(), image: getAppImg("day_calendar")
            input "cond_months", "enum", title: inTS("Months of the year", getAppImg("day_calendar", true)), multiple: true, required: false, submitOnChange: true, options: monthEnum(), image: getAppImg("day_calendar")
        }

        section (sTS("Location Conditions")) {
            input "cond_mode", "mode", title: inTS("Location Mode is...", getAppImg("mode", true)), multiple: true, required: false, submitOnChange: true, image: getAppImg("mode")
            input "cond_alarm", "enum", title: inTS("${getAlarmSystemName()} is...", getAppImg("alarm_home", true)), options: getAlarmTrigOpts(), multiple: false, required: false, submitOnChange: true, image: getAppImg("alarm_home")
        }

        condNonNumSect("switch", "switch", "Switches/Outlets Conditions", "Switches/Outlets", ["on","off"], "are", "switch")

        condNonNumSect("motion", "motionSensor", "Motion Conditions", "Motion Sensors", ["active", "inactive"], "are", "motion")

        condNonNumSect("presence", "presenceSensor", "Presence Conditions", "Presence Sensors", ["present", "not present"], "are", "presence")

        condNonNumSect("contact", "contactSensor", "Door, Window, Contact Sensors Conditions", "Contact Sensors",  ["open","closed"], "are", "contact")

        condNonNumSect("lock", "lock", "Lock Conditions", "Smart Locks", ["locked", "unlocked"], "are", "lock")

        condNonNumSect("door", "garageDoorControl", "Garage Door Conditions", "Garage Doors", ["open", "closed"], "are", "garage_door")

        condNumValSect("temperature", "temperatureMeasurement", "Temperature Conditions", "Temperature Sensors", "Temperature", "temperature", true)

        condNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Conditions", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity", true)

        condNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Conditions", "Illuminance Sensors", "Lux Level (%)", "illuminance", true)

        condNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level (%)", "speed_knob", true)

        condNonNumSect("water", "waterSensor", "Water Sensors", "Water Sensors", ["wet", "dry"], "are", "water")

        condNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power", true)

        condNonNumSect("shade", "windowShades", "Window Shades", "Window Shades", ["open", "closed"], "are", "shade")

        condNonNumSect("valve", "valve", "Valves", "Valves", ["open", "closed"], "are", "valve")

        condNumValSect("battery", "battery", "Battery Level Conditions", "Batteries", "Level (%)", "battery", true)
    }
}

def condNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, String image) {
    section (sTS(sectStr), hideWhenEmpty: true) {
        input "cond_${inType}", "capability.${capType}", title: inTS(devTitle, getAppImg(image, true)), multiple: true, submitOnChange: true, required:false, image: getAppImg(image)
        if (settings?."cond_${inType}") {
            input "cond_${inType}_cmd", "enum", title: inTS("${cmdTitle}...", getAppImg("command", true)), options: cmdOpts, multiple: false, required: true, submitOnChange: true, image: getAppImg("command")
            if (settings?."cond_${inType}_cmd" && settings?."cond_${inType}"?.size() > 1) {
                input "cond_${inType}_all", "bool", title: inTS("ALL ${devTitle} must be (${settings?."cond_${inType}_cmd"})?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
            }
        }
    }
}

def condNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, hideable= false) {
    section (sTS(sectStr), hideable: hideable, hideWhenEmpty: true) {
        input "cond_${inType}", "capability.${capType}", title: inTS(devTitle, getAppImg(image, true)), multiple: true, submitOnChange: true, required: false, image: getAppImg(image)
        if(settings?."cond_${inType}") {
            input "cond_${inType}_cmd", "enum", title: inTS("${cmdTitle} is...", getAppImg("command", true)), options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
            if (settings?."cond_${inType}_cmd") {
                if (settings?."cond_${inType}_cmd" in ["between", "below"]) {
                    input "cond_${inType}_low", "number", title: inTS("a ${settings?."cond_${inType}_cmd" == "between" ? "Low " : ""}${cmdTitle} of...", getAppImg("low", true)), required: true, submitOnChange: true, image: getAppImg("low")
                }
                if (settings?."cond_${inType}_cmd" in ["between", "above"]) {
                    input "cond_${inType}_high", "number", title: inTS("${settings?."cond_${inType}_cmd" == "between" ? "and a high " : "a "}${cmdTitle} of...", getAppImg("high", true)), required: true, submitOnChange: true, image: getAppImg("high")
                }
                if (settings?."cond_${inType}_cmd" == "equals") {
                    input "cond_${inType}_equal", "number", title: inTS("a ${cmdTitle} of...", getAppImg("equal", true)), required: true, submitOnChange: true, image: getAppImg("equal")
                }
                if (settings?."cond_${inType}"?.size() > 1) {
                    input "cond_${inType}_all", "bool", title: inTS("Require ALL devices to be (${settings?."cond_${inType}_cmd"}) values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                    if(!settings?."cond_${inType}_all") {
                        input "cond_${inType}_avg", "bool", title: inTS("Use the average of all selected device values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                    }
                }
            }
        }
    }
}

def condTimePage() {
    return dynamicPage(name:"condTimePage", title: "", install: false, uninstall: false) {
        Boolean timeReq = (settings["cond_time_start"] || settings["cond_time_stop"])
        section(sTS("Start Time:")) {
            input "cond_time_start_type", "enum", title: inTS("Starting at...", getAppImg("start_time", true)), options: ["time":"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true, image: getAppImg("start_time")
            if(cond_time_start_type  == "time") {
                input "cond_time_start", "time", title: inTS("Start time", getAppImg("start_time", true)), required: timeReq, submitOnChange: true, image: getAppImg("start_time")
            } else if(cond_time_start_type in ["sunrise", "sunrise"]) {
                input "cond_time_start_offset", "number", range: "*..*", title: inTS("Offset in minutes (+/-)", getAppImg("start_time", true)), required: false, submitOnChange: true, image: getAppImg("threshold")
            }
        }
        section(sTS("Stop Time:")) {
            input "cond_time_stop_type", "enum", title: inTS("Stopping at...", getAppImg("start_time", true)), options: ["time":"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true, image: getAppImg("stop_time")
            if(cond_time_stop_type == "time") {
                input "cond_time_stop", "time", title: inTS("Stop time", getAppImg("start_time", true)), required: timeReq, submitOnChange: true, image: getAppImg("stop_time")
            } else if(cond_time_stop_type in ["sunrise", "sunrise"]) {
                input "cond_time_stop_offset", "number", range: "*..*", title: inTS("Offset in minutes (+/-)", getAppImg("start_time", true)), required: false, submitOnChange: true, image: getAppImg("threshold")
            }
        }
    }
}

def zoneNotifPage() {
    return dynamicPage(name: "zoneNotifPage", title: "Zone Notifications", install: false, uninstall: false) {
        section (sTS("Message Customization:")) {
            if(customMsgRequired() && !settings?.notif_use_custom) { settingUpdate("notif_use_custom", "true", "bool") }
            paragraph pTS("When using speak and announcements you can leave this off and a notification will be sent with speech text.  For other zone types a custom message is required", null, false, "gray")
            input "notif_use_custom", "bool", title: inTS("Send a custom notification...", getAppImg("question", true)), required: false, defaultValue: customMsgRequired(), submitOnChange: true, image: getAppImg("question")
            if(settings?.notif_use_custom) {
                input "notif_custom_message", "text", title: inTS("Enter custom message...", getAppImg("text", true)), required: true, submitOnChange: true, image: getAppImg("text")
            }
        }

        section (sTS("Push Messages:")) {
            input "notif_send_push", "bool", title: inTS("Send Push Notifications...", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
        }
        section (sTS("Text Messages:"), hideWhenEmpty: true) {
            paragraph pTS("To send to multiple numbers separate the number by a comma\n\nE.g. 8045551122,8046663344", getAppImg("info", true), false, "gray")
            paragraph pTS("SMS Support will soon be removed from Hubitat and SmartThings (UK)", getAppImg("info", true), false, "gray")
            input "notif_sms_numbers", "text", title: inTS("Send SMS Text to...", getAppImg("sms_phone", true)), required: false, submitOnChange: true, image: getAppImg("sms_phone")
        }
        section (sTS("Notification Devices:")) {
            input "notif_devs", "capability.notification", title: inTS("Send to Notification devices?", getAppImg("notification", true)), required: false, multiple: true, submitOnChange: true, image: getAppImg("notification")
        }
        section (sTS("Alexa Mobile Notification:")) {
            paragraph pTS("This will send a push notification the Alexa Mobile app.", null, false, "gray")
            input "notif_alexa_mobile", "bool", title: inTS("Send message to Alexa App?", getAppImg("notification", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("notification")
        }
        if(isST()) {
            section(sTS("Pushover Support:")) {
                input "notif_pushover", "bool", title: inTS("Use Pushover Integration", getAppImg("pushover_icon", true)), required: false, submitOnChange: true, image: getAppImg("pushover")
                if(settings?.notif_pushover == true) {
                    def poDevices = parent?.getPushoverDevices()
                    if(!poDevices) {
                        parent?.pushover_init()
                        paragraph pTS("If this is the first time enabling Pushover than leave this page and come back if the devices list is empty", null, false, "#2784D9"), state: "complete"
                    } else {
                        input "notif_pushover_devices", "enum", title: inTS("Select Pushover Devices", getAppImg("select_icon", true)), description: "Tap to select", groupedOptions: poDevices, multiple: true, required: false, submitOnChange: true, image: getAppImg("select_icon")
                        if(settings?.notif_pushover_devices) {
                            def t0 = [(-2):"Lowest", (-1):"Low", 0:"Normal", 1:"High", 2:"Emergency"]
                            input "notif_pushover_priority", "enum", title: inTS("Notification Priority (Optional)", getAppImg("priority", true)), description: "Tap to select", defaultValue: 0, required: false, multiple: false, submitOnChange: true, options: t0, image: getAppImg("priority")
                            input "notif_pushover_sound", "enum", title: inTS("Notification Sound (Optional)", getAppImg("sound", true)), description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: parent?.getPushoverSounds(), image: getAppImg("sound")
                        }
                    }
                }
            }
        }
        if(isActNotifConfigured()) {
            section(sTS("Notification Restrictions:")) {
                def nsd = getNotifSchedDesc()
                href "zoneNotifTimePage", title: inTS("Notification Restrictions", getAppImg("restriction", true)), description: (nsd ? "${nsd}\nTap to modify..." : "Tap to configure"), state: (nsd ? "complete" : null), image: getAppImg("restriction")
            }
            if(!state?.notif_message_tested) {
                def actDevices = settings?.notif_alexa_mobile ? parent?.getDevicesFromList(settings?.zone_EchoDevices) : []
                def aMsgDev = actDevices?.size() && settings?.notif_alexa_mobile ? actDevices[0] : null
                if(sendNotifMsg("Info", "Zone Notification Test Successful. Notifications Enabled for ${app?.getLabel()}", aMsgDev, true)) { state?.notif_message_tested = true }
            }
        } else { state?.notif_message_tested = false }
    }
}

def zoneNotifTimePage() {
    return dynamicPage(name:"zoneNotifTimePage", title: "", install: false, uninstall: false) {
        def pre = "notif"
        Boolean timeReq = (settings["${pre}_time_start"] || settings["${pre}_time_stop"])
        section(sTS("Start Time:")) {
            input "${pre}_time_start_type", "enum", title: inTS("Starting at...", getAppImg("start_time", true)), options: ["time":"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true, image: getAppImg("start_time")
            if(settings?."${pre}_time_start_type" == "time") {
                input "${pre}_time_start", "time", title: inTS("Start time", getAppImg("start_time", true)), required: timeReq, submitOnChange: true, image: getAppImg("start_time")
            } else if(settings?."${pre}_time_start_type" in ["sunrise", "sunrise"]) {
                input "${pre}_time_start_offset", "number", range: "*..*", title: inTS("Offset in minutes (+/-)", getAppImg("start_time", true)), required: false, submitOnChange: true, image: getAppImg("threshold")
            }
        }
        section(sTS("Stop Time:")) {
            input "${pre}_time_stop_type", "enum", title: inTS("Stopping at...", getAppImg("start_time", true)), options: ["time":"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true, image: getAppImg("stop_time")
            if(settings?."${pre}_time_stop_type" == "time") {
                input "${pre}_time_stop", "time", title: inTS("Stop time", getAppImg("start_time", true)), required: timeReq, submitOnChange: true, image: getAppImg("stop_time")
            } else if(settings?."${pre}_time_stop_type" in ["sunrise", "sunrise"]) {
                input "${pre}_time_stop_offset", "number", range: "*..*", title: inTS("Offset in minutes (+/-)", getAppImg("start_time", true)), required: false, submitOnChange: true, image: getAppImg("threshold")
            }
        }
        section(sTS("Quiet Days:")) {
            input "${pre}_days", "enum", title: inTS("Only on these days of the week", getAppImg("day_calendar", true)), multiple: true, required: false, image: getAppImg("day_calendar"), options: weekDaysEnum()
        }
        section(sTS("Quiet Modes:")) {
            input "${pre}_modes", "mode", title: inTS("When these Modes are Active", getAppImg("mode", true)), multiple: true, submitOnChange: true, required: false, image: getAppImg("mode")
        }
    }
}


//********************************************
//              CORE APP METHODS
//********************************************

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
}

def initialize() {
    if(state?.dupPendingSetup == false && settings?.duplicateFlag == true) {
        settingUpdate("duplicateFlag", "false", "bool")
    } else if(settings?.duplicateFlag == true && state?.dupPendingSetup != false) {
        String newLbl = app?.getLabel() + app?.getLabel()?.toString()?.contains("(Dup)") ? "" : " (Dup)"
        app?.updateLabel(newLbl)
        state?.dupPendingSetup = true
        def dupState = parent?.getDupZoneStateData()
        if(dupState?.size()) {
            dupState?.each {k,v-> state[k] = v }
            parent?.clearDuplicationItems()
        }
        logInfo("Duplicated Zone has been created... Please open Zone and configure to complete setup...")
        return
    }
    unsubscribe()
    state?.isInstalled = true
    updAppLabel()
    runIn(3, "zoneCleanup")
    runIn(7, "subscribeToEvts")
    parent?.updZoneActiveStatus([id: app?.getId(), name: app?.getLabel(), active: false])
    updConfigStatusMap()
}

private updAppLabel() {
    String newLbl = "${settings?.appLbl}${isPaused() ? " | (\u23F8)" : ""}"?.replaceAll(/(Dup)/, "").replaceAll("\\s"," ")
    if(settings?.appLbl && app?.getLabel() != newLbl) { app?.updateLabel(newLbl) }
}

public guardEventHandler(guardState) {
    if(!state?.alexaGuardState || state?.alexaGuardState != guardState) {
        state?.alexaGuardState = guardState
        def evt = [name: "guard", displayName: "Alexa Guard", value: state?.alexaGuardState, date: new Date(), deviceId: null]
        logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)})")
        if(state?.handleGuardEvents) {
            executeAction(evt, false, "guardEventHandler", false, false)
        }
    }
}

private updConfigStatusMap() {
    Map sMap = atomicState?.configStatusMap ?: [:]
    sMap?.conditions = conditionsConfigured()
    sMap?.devices = devicesConfigured()
    atomicState?.configStatusMap = sMap
}

Boolean devicesConfigured() { return (settings?.zone_EchoDevices) }
private getConfStatusItem(item) { return (atomicState?.configStatusMap?.containsKey(item) && atomicState?.configStatusMap[item] == true) }

private zoneCleanup() {
    // State Cleanup
    List items = []
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    //Cleans up unused action setting items
    List setItems = []
    List setIgn = ["zone_EchoDevices"]
    setItems?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
}

public triggerInitialize() { runIn(3, "initialize") }

public updatePauseState(Boolean pause) {
    if(settings?.actionPause != pause) {
        logDebug("Received Request to Update Pause State to (${pause})")
        settingUpdate("zonePause", "${pause}", "bool")
        runIn(4, "updated")
    }
}


private condItemSet(String key) { return (settings?.containsKey("cond_${key}") && settings["cond_${key}"]) }

private subscribeToEvts() {
    if(checkMinVersion()) { logError("CODE UPDATE required to RESUME operation.  No events will be monitored.", true); return; }
    if(isPaused()) { logWarn("Zone is PAUSED... No Events will be subscribed to or scheduled....", true); return; }
    //SCHEDULING
    if (settings?.cond_time_start_type) {
        if(settings?.cond_time_start_type in ["sunrise", "sunset"]) {
            if (settings?.cond_time_start_type == "sunset") { subscribe(location, "sunsetTime", zoneEvtHandler) }
            if (settings?.cond_time_start_type == "sunrise") { subscribe(location, "sunriseTime", zoneEvtHandler) }
        }
        if(settings?.cond_time_start_type == "time") {
            if(settings?.cond_time_start) { schedule(settings?.cond_time_start, zoneEvtHandler) }
            if(settings?.cond_time_stop) { schedule(settings?.cond_time_stop, zoneEvtHandler) }
        }
    }

    // Location Alarm Events
    if(settings?.cond_alarm) { subscribe(location, !isST() ? "hsmStatus" : "alarmSystemStatus", zoneEvtHandler) }

    state?.handleGuardEvents = false

    // Location Mode Events
    if(settings?.cond_mode)         { subscribe(location, "mode", zoneEvtHandler) }

    // ENVIRONMENTAL Sensors
    if(settings?.cond_presence)     { subscribe(cond_presence, "presence", zoneEvtHandler) }

    // Motion Sensors
    if(settings?.cond_motion)       { subscribe(cond_motion, "motion", zoneEvtHandler) }

    // Water Sensors
    if(settings?.cond_water)        { subscribe(settings?.cond_water, "water", zoneEvtHandler) }

    // Humidity Sensors
    if(settings?.cond_humidity)     { subscribe(settings?.cond_humidity, "humidity", zoneEvtHandler) }

    // Temperature Sensors
    if(settings?.cond_temperature)  { subscribe(settings?.cond_temperature, "temperature", zoneEvtHandler) }

    // Illuminance Sensors
    if(settings?.cond_illuminance)  { subscribe(settings?.cond_illuminance, "illuminance", zoneEvtHandler) }

    // Power Meters
    if(settings?.cond_power)        { subscribe(cond_power, "power", zoneEvtHandler) }

    // Locks
    if(settings?.cond_lock)         { subscribe(settings?.cond_lock, "lock", zoneEvtHandler) }

    // Window Shades
    if(settings?.cond_shade)        { subscribe(settings?.cond_shade, "windowShade", zoneEvtHandler) }

    // Valves
    if(settings?.cond_valve)        { subscribe(settings?.cond_valve, "valve", zoneEvtHandler) }

    // Garage Door Openers
    if(settings?.cond_door)         { subscribe(settings?.cond_door, "garageDoorControl", zoneEvtHandler) }

    //Contact Sensors
    if(settings?.cond_contact)      { subscribe(settings?.cond_contact, "contact", zoneEvtHandler) }

    // Outlets, Switches
    if(settings?.cond_switch)       { subscribe(cond_switch, "switch", zoneEvtHandler) }

    // Batteries
    if(settings?.cond_battery)      { subscribe(settings?.cond_battery, "battery", zoneEvtHandler) }

    // Dimmers/Level
    if(settings?.cond_level)        { subscribe(settings?.cond_level, "level", zoneEvtHandler) }
}

private attributeConvert(String attr) {
    Map atts = ["door":"garageDoorControl", "carbon":"carbonMonoxide", "shade":"windowShade"]
    return (atts?.containsKey(attr)) ? atts[attr] : attr
}

private getDevEvtHandlerName(String type) {
    return (type && settings?."cond_${type}_after") ? "devAfterEvtHandler" : "deviceEvtHandler"
}


/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
Boolean timeCondOk() {
    def startTime = null
    def stopTime = null
    def now = new Date()
    def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
    if(settings?.cond_time_start_type && settings?.cond_time_stop_type) {
        if(settings?.cond_time_start_type == "sunset") { startTime = sun?.sunset }
        else if(settings?.cond_time_start_type == "sunrise") { startTime = sun?.sunrise }
        else if(settings?.cond_time_start_type == "time" && settings?.cond_time_start) { startTime = settings?.cond_time_start }

        if(settings?.cond_time_stop_type == "sunset") { stopTime = sun?.sunset }
        else if(settings?.cond_time_stop_type == "sunrise") { stopTime = sun?.sunrise }
        else if(settings?.cond_time_stop_type == "time" && settings?.cond_time_stop) { stopTime = settings?.cond_time_stop }
    } else { return true }
    if(startTime && stopTime) {
        if(!isST()) {
            startTime = toDateTime(startTime)
            stopTime = toDateTime(stopTime)
        }
        return timeOfDayIsBetween(startTime, stopTime, new Date(), location?.timeZone)
    } else { return true }
}

Boolean dateCondOk() {
    Boolean dOk = settings?.cond_days ? (isDayOfWeek(settings?.cond_days)) : true
    Boolean mOk = settings?.cond_months ? (isMonthOfYear(settings?.cond_months)) : true
    return (dOk && mOk)
}

Boolean locationCondOk() {
    Boolean mOk = settings?.cond_mode ? (isInMode(settings?.cond_mode)) : true
    Boolean aOk = settings?.cond_alarm ? (isInAlarmMode(settings?.cond_alarm)) : true
    logDebug("locationCondOk | modeOk: $mOk | alarmOk: $aOk")
    return (mOk && aOk)
}

Boolean checkDeviceCondOk(type) {
    List devs = settings?."cond_${type}" ?: null
    String cmdVal = settings?."cond_${type}_cmd" ?: null
    Boolean all = settings?."cond_${type}_all"
    if( !(type && devs && cmdVal) ) { return true }
    return all ? allDevCapValsEqual(devs, type, cmdVal) : anyDevCapValsEqual(devs, type, cmdVal)
}

Boolean checkDeviceNumCondOk(type) {
    List devs = settings?."cond_${type}" ?: null
    String cmd = settings?."cond_${type}_cmd" ?: null
    Double dcl = settings?."cond_${type}_low" ?: null
    Double dch = settings?."cond_${type}_high" ?: null
    Double dce = settings?."cond_${type}_equal" ?: null
    Boolean dca = settings?."cond_${type}_all" ?: false
    if( !(type && devs && cmd) ) { return true }

    switch(cmd) {
        case "equals":
            if(dce) {
                if(dca) { return allDevCapNumValsEqual(devs, type, dce) }
                else { return anyDevCapNumValEqual(devs, type, dce) }
            }
            return true
            break
        case "between":
            if(dcl && dch) {
                if(dca) { return allDevCapNumValsBetween(devs, type, dcl, dch) }
                else { return anyDevCapNumValBetween(devs, type, dcl, dch) }
            }
            return true
            break
        case "above":
            if(dch) {
                if(dca) { return allDevCapNumValsAbove(devs, type, dch) }
                else { return anyDevCapNumValAbove(devs, type, dch) }
            }
            return true
            break
        case "below":
            if(dcl) {
                if(dca) { return allDevCapNumValsBelow(devs, type, dcl) }
                else { return anyDevCapNumValBelow(devs, type, dcl) }
            }
            return true
            break
    }
}

Boolean deviceCondOk() {
    Boolean swDevOk = checkDeviceCondOk("switch")
    Boolean motDevOk = checkDeviceCondOk("motion")
    Boolean presDevOk = checkDeviceCondOk("presence")
    Boolean conDevOk = checkDeviceCondOk("contact")
    Boolean lockDevOk = checkDeviceCondOk("lock")
    Boolean garDevOk = checkDeviceCondOk("door")
    Boolean shadeDevOk = checkDeviceCondOk("shade")
    Boolean valveDevOk = checkDeviceCondOk("valve")
    Boolean tempDevOk = checkDeviceNumCondOk("temperature")
    Boolean humDevOk = checkDeviceNumCondOk("humidity")
    Boolean illDevOk = checkDeviceNumCondOk("illuminance")
    Boolean levelDevOk = checkDeviceNumCondOk("level")
    Boolean powerDevOk = checkDeviceNumCondOk("illuminance")
    Boolean battDevOk = checkDeviceNumCondOk("battery")
    logDebug("checkDeviceCondOk | switchOk: $swDevOk | motionOk: $motDevOk | presenceOk: $presDevOk | contactOk: $conDevOk | lockOk: $lockDevOk | garageOk: $garDevOk")
    return (swDevOk && motDevOk && presDevOk && conDevOk && lockDevOk && garDevOk && valveDevOk && shadeDevOk && tempDevOk && humDevOk && battDevOk && illDevOk && levelDevOk && powerDevOk)
}

def allConditionsOk() {
    List blocks = []
    if(!timeCondOk())        { blocks?.push("time") }
    if(!dateCondOk())        { blocks?.push("date") }
    if(!locationCondOk())    { blocks?.push("location") }
    if(!deviceCondOk())      { blocks?.push("device") }
    logDebug("Action Conditions Check | Blocks: ${blocks}")
    return [ok: (blocks?.size() == 0), blocks: blocks]
}

Boolean devCondConfigured(type) {
    return (settings?."cond_${type}" && settings?."cond_${type}_cmd")
}

Boolean devNumCondConfigured(type) {
    return (settings?."cond_${type}_cmd" && (settings?."cond_${type}_low" || settings?."cond_${type}_low" || settings?."trig_${type}_equal"))
}

Boolean timeCondConfigured() {
    Boolean startTime = (settings?.cond_time_start_type in ["sunrise", "sunset"] || (settings?.cond_time_start_type == "time" && settings?.cond_time_start))
    Boolean stopTime = (settings?.cond_time_stop_type in ["sunrise", "sunset"] || (settings?.cond_time_stop_type == "time" && settings?.cond_time_stop))
    return (startTime && stopTime)
}

Boolean dateCondConfigured() {
    Boolean days = (settings?.cond_days)
    Boolean months = (settings?.cond_months)
    return (days || months)
}

Boolean locationCondConfigured() {
    Boolean mode = (settings?.cond_mode)
    Boolean alarm = (settings?.cond_alarm)
    return (mode || alarm)
}

Boolean deviceCondConfigured() {
    Boolean swDev = devCondConfigured("switch")
    Boolean motDev = devCondConfigured("motion")
    Boolean presDev = devCondConfigured("presence")
    Boolean conDev = devCondConfigured("contact")
    Boolean lockDev = devCondConfigured("lock")
    Boolean garDev = devCondConfigured("door")
    Boolean shadeDev = devCondConfigured("shade")
    Boolean valveDev = devCondConfigured("valve")
    Boolean tempDev = devCondConfigured("temperature")
    Boolean humDev = devCondConfigured("humidity")
    Boolean illDev = devCondConfigured("illuminance")
    Boolean levelDev = devCondConfigured("level")
    Boolean powerDev = devCondConfigured("illuminance")
    Boolean battDev = devCondConfigured("battery")
    return (swDev || motDev || presDev || conDev || lockDev || garDev || shadeDev || valveDev || tempDev || humDev || illDev || levelDev || powerDev || battDev)
}

Boolean conditionsConfigured() {
    return (timeCondConfigured() || dateCondConfigured() || locationCondConfigured() || deviceCondConfigured())
}

/***********************************************************************************************************
    EVENT HANDLER FUNCTIONS
************************************************************************************************************/

def zoneEvtHandler(evt) {
    Map condOk = allConditionsOk()
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms | Zone Conditions: (${condOk?.ok ? okSym() : notOkSym()})${condOk?.blocks?.size() ? " Blocks: ${condOk?.blocks}" : ""}")
    if(settings?.zone_delay) {
        runIn(settings?.zone_delay as Integer, "zoneActivationEvt", [data: [active: condOk, recheck: true]])
    } else { zoneActivationEvt([data: [active: condOk, recheck: false]]) }
}

def zoneActivationEvt(data) {
    Boolean active = (data?.active == true)
    if(data?.recheck == true) { active = (allConditionsOk()?.ok == true) }
    if(state?.zoneConditionsOk != active) {
        log.debug("Updating Zone Status to (${active ? "Active" : "Inactive"})... ${app?.getLabel()}")
        state?.zoneConditionsOk = active
        parent?.updZoneActiveStatus([id: app?.getId(), name: app?.getLabel(), active: active])
    }
}


/******************************************
|   Restriction validators
*******************************************/

Double getDevValueAvg(devs, attr) {
    List vals = devs?.findAll { it?."current${attr?.capitalize()}"?.isNumber() }?.collect { it?."current${attr?.capitalize()}" as Double }
    return vals?.size() ? (vals?.sum()/vals?.size())?.round(1) as Double : null
}

String getCurrentMode() {
    return location?.mode
}

List getLocationModes(Boolean sorted=false) {
    List modes = location?.modes*.name
    // log.debug "modes: ${modes}"
    return (sorted) ? modes?.sort() : modes
}

List getLocationRoutines() {
    return (isST()) ? location.helloHome?.getPhrases()*.label?.sort() : []
}

List getClosedContacts(sensors) {
    return sensors?.findAll { it?.currentContact == "closed" } ?: null
}

List getOpenContacts(sensors) {
    return sensors?.findAll { it?.currentContact == "open" } ?: null
}

List getDryWaterSensors(sensors) {
    return sensors?.findAll { it?.currentWater == "dry" } ?: null
}

List getWetWaterSensors(sensors) {
    return sensors?.findAll { it?.currentWater == "wet" } ?: null
}

List getPresentSensors(sensors) {
    return sensors?.findAll { it?.currentPresence == "present" } ?: null
}

List getAwaySensors(sensors) {
    return sensors?.findAll { it?.currentPresence != "present" } ?: null
}

Boolean isContactOpen(sensors) {
    if(sensors) { sensors?.each { if(sensors?.currentSwitch == "open") { return true } } }
    return false
}

Boolean isSwitchOn(devs) {
    if(devs) { devs?.each { if(it?.currentSwitch == "on") { return true } } }
    return false
}

Boolean isSensorPresent(sensors) {
    if(sensors) { sensors?.each { if(it?.currentPresence == "present") { return true } } }
    return false
}

Boolean isSomebodyHome(sensors) {
    if(sensors) { return (sensors?.findAll { it?.currentPresence == "present" }?.size() > 0) }
    return false
}

Boolean isIlluminanceBelow(sensors, val) {
    if(sensors) { return (sensors?.findAll { it?.currentIlluminance?.integer() < val }?.size() > 0) }
    return false
}

Boolean isIlluminanceAbove(sensors, val) {
    if(sensors) { return (sensors?.findAll { it?.currentIlluminance?.integer() > val }?.size() > 0) }
    return false
}

Boolean isWaterWet(sensors) {
    if(sensors) { return (sensors?.findAll { it?.currentWater == "wet" }?.size() > 0) }
    return false
}

Boolean isInMode(modes) {
    return (modes) ? (getCurrentMode() in modes) : false
}

Boolean isInAlarmMode(modes) {
    return (modes) ? (getAlarmSystemStatus() in modes) : false
}

Boolean areAllDevsSame(List devs, String attr, val) {
    if(devs && attr && val) { return (devs?.findAll { it?.currentValue(attr) == val as String }?.size() == devs?.size()) }
    return false
}

Boolean allDevCapValsEqual(List devs, String cap, val) {
    if(devs) { return (devs?.findAll { it?."current${cap?.capitalize()}" == val }?.size() == devs?.size()) }
    return false
}

Boolean anyDevCapValsEqual(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}" == val }?.size() >= 1) : false
}

Boolean anyDevCapNumValAbove(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() > val?.toDouble() }?.size() >= 1) : false
}
Boolean anyDevCapNumValBelow(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() < val?.toDouble() }?.size() >= 1) : false
}
Boolean anyDevCapNumValBetween(List devs, String cap, low, high) {
    return (devs && cap && low && high) ? (devs?.findAll { ( (it?."current${cap?.capitalize()}"?.toDouble() > low?.toDouble()) && (it?."current${cap?.capitalize()}"?.toDouble() < high?.toDouble()) ) }?.size() >= 1) : false
}
Boolean anyDevCapNumValEqual(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() == val?.toDouble() }?.size() >= 1) : false
}

Boolean allDevCapNumValsAbove(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() > val?.toDouble() }?.size() == devs?.size()) : false
}
Boolean allDevCapNumValsBelow(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() < val?.toDouble() }?.size() == devs?.size()) : false
}
Boolean allDevCapNumValsBetween(List devs, String cap, low, high) {
    return (devs && cap && low && high) ? (devs?.findAll { ( (it?."current${cap?.capitalize()}"?.toDouble() > low?.toDouble()) && (it?."current${cap?.capitalize()}"?.toDouble() < high?.toDouble()) ) }?.size() == devs?.size()) : false
}
Boolean allDevCapNumValsEqual(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() == val?.toDouble() }?.size() == devs?.size()) : false
}

Boolean devCapValEqual(List devs, String devId, String cap, val) {
    if(devs) { return (devs?.find { it?."current${cap?.capitalize()}" == val }) }
    return false
}

String getAlarmSystemName(abbr=false) {
    return isST() ? (abbr ? "SHM" : "Smart Home Monitor") : (abbr ? "HSM" : "Hubitat Safety Monitor")
}
/******************************************
|    Time and Date Conversion Functions
*******************************************/
def formatDt(dt, tzChg=true) {
    def tf = new java.text.SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone(location?.timeZone) } }
    return tf?.format(dt)
}

def dateTimeFmt(dt, fmt) {
    def tf = new java.text.SimpleDateFormat(fmt)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(dt)
}

def convToTime(dt) {
    def newDt = dateTimeFmt(dt, "h:mm a")
    if(newDt?.toString()?.contains(":00 ")) { newDt?.toString()?.replaceAll(":00 ", " ") }
    return newDt
}

def convToDate(dt) {
    def newDt = dateTimeFmt(dt, "EEE, MMM d")
    return newDt
}

def convToDateTime(dt) {
    def t = dateTimeFmt(dt, "h:mm a")
    def d = dateTimeFmt(dt, "EEE, MMM d")
    return "$d, $t"
}

Date parseDate(dt) { return Date.parse("E MMM dd HH:mm:ss z yyyy", dt?.toString()) }
Boolean isDateToday(Date dt) { return (dt && dt?.clearTime().compareTo(new Date()?.clearTime()) >= 0) }
String strCapitalize(str) { return str ? str?.toString().capitalize() : null }
String pluralizeStr(obj) { return (obj?.size() > 1) ? "(s)" : "" }

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
    def tf = new java.text.SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    return tf.format(tm)
}

def time2Str(time) {
    if(time) {
        def t = timeToday(time as String, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
        return f?.format(t)
    }
}

def fmtTime(t, altFmt=false) {
    if(!t) return null
    def dt = new Date().parse(altFmt ? "E MMM dd HH:mm:ss z yyyy" : "yyyy-MM-dd'T'HH:mm:ss.SSSZ", t?.toString())
    def tf = new java.text.SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    return tf?.format(dt)
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
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : ""}lastDate: $lastDate): ${ex?.message}")
        return 10000
    }
}

def getWeekDay() {
    def df = new java.text.SimpleDateFormat("EEEE")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

def getWeekMonth() {
    def df = new java.text.SimpleDateFormat("W")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

def getDay() {
    def df = new java.text.SimpleDateFormat("D")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

def getYear() {
    def df = new java.text.SimpleDateFormat("yyyy")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

def getMonth() {
    def df = new java.text.SimpleDateFormat("MMMMM")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

def getWeekYear() {
    def df = new java.text.SimpleDateFormat("w")
    df.setTimeZone(location?.timeZone)
    return df.format(new Date())
}

Map getDateMap() {
    return [d: getWeekDay(), dm: getDay(), wm: getWeekMonth(), wy: getWeekYear(), m: getMonth(), y: getYear() ]
}

Boolean isDayOfWeek(opts) {
    def df = new java.text.SimpleDateFormat("EEEE")
    df.setTimeZone(location?.timeZone)
    def day = df.format(new Date())
    return ( opts?.contains(day) )
}

Boolean isTimeOfDay(startTime, stopTime) {
    if(!startTime && !stopTime) { return true }
    if(!isST()) { startTime = toDateTime(startTime); stopTime = toDateTime(stopTime); }
    return timeOfDayIsBetween(startTime, stopTime, new Date(), location.timeZone)
}

/******************************************
|   App Input Description Functions
*******************************************/

String unitStr(type) {
    switch(type) {
        case "temp":
            return "\u00b0${getTemperatureScale() ?: "F"}"
        case "humidity":
            return "%"
        default:
            return ""
    }
}

String getAppNotifDesc(hide=false) {
    String str = ""
    if(isActNotifConfigured()) {
        str += hide ? "" : "Send To:\n"
        str += settings?.notif_sms_numbers ? " \u2022 (${settings?.notif_sms_numbers?.tokenize(",")?.size()} SMS Numbers)\n" : ""
        str += settings?.notif_send_push ? " \u2022 (Push Message)\n" : ""
        str += (settings?.notif_pushover && settings?.notif_pushover_devices?.size()) ? " \u2022 Pushover Device${pluralizeStr(settings?.notif_pushover_devices)} (${settings?.notif_pushover_devices?.size()})\n" : ""
        str += settings?.notif_alexa_mobile ? " \u2022 Alexa Mobile App\n" : ""
        str += getNotifSchedDesc() ? " \u2022 Restrictions: (${getOk2Notify() ? "${okSym()}" : "${notOkSym()}"})\n" : ""
    }
    return str != "" ? str : null
}

String getNotifSchedDesc() {
    def sun = getSunriseAndSunset()
    def startInput = settings?.notif_time_start_type
    def startTime = settings?.notif_time_start
    def stopInput = settings?.notif_time_stop_type
    def stopTime = settings?.notif_time_stop
    def dayInput = settings?.notif_days
    def modeInput = settings?.notif_modes
    def str = ""
    def startLbl = ( (startInput == "Sunrise" || startInput == "Sunset") ? ( (startInput == "Sunset") ? epochToTime(sun?.sunset?.time) : epochToTime(sun?.sunrise?.time) ) : (startTime ? time2Str(startTime) : "") )
    def stopLbl = ( (stopInput == "Sunrise" || stopInput == "Sunset") ? ( (stopInput == "Sunset") ? epochToTime(sun?.sunset?.time) : epochToTime(sun?.sunrise?.time) ) : (stopTime ? time2Str(stopTime) : "") )
    str += (startLbl && stopLbl) ? " • Silent Time: ${startLbl} - ${stopLbl}" : ""
    def days = getInputToStringDesc(dayInput)
    def modes = getInputToStringDesc(modeInput)
    str += days ? "${(startLbl || stopLbl) ? "\n" : ""} • Silent Day${pluralizeStr(dayInput)}: ${days}" : ""
    str += modes ? "${(startLbl || stopLbl || days) ? "\n" : ""} • Silent Mode${pluralizeStr(modeInput)}: ${modes}" : ""
    return (str != "") ? "${str}" : null
}

String getConditionsDesc() {
    Boolean confd = conditionsConfigured()
    def time = null
    String sPre = "cond_"
    if(confd) {
        String str = "Conditions: (${(allConditionsOk()?.ok == true) ? "${okSym()}" : "${notOkSym()}"})\n"
        if(timeCondConfigured()) {
            str += " • Time Between: (${timeCondOk() ? "${okSym()}" : "${notOkSym()}"})\n"
            str += "    - ${getTimeCondDesc(false)}\n"
        }
        if(dateCondConfigured()) {
            str += " • Date:\n"
            str += settings?.cond_days      ? "    - Days: (${(isDayOfWeek(settings?.cond_days)) ? "${okSym()}" : "${notOkSym()}"})\n" : ""
            str += settings?.cond_months    ? "    - Months: (${(isMonthOfYear(settings?.cond_months)) ? "${okSym()}" : "${notOkSym()}"})\n"  : ""
        }
        if(settings?.cond_alarm || settings?.cond_mode) {
            str += " • Location: (${locationCondOk() ? "${okSym()}" : "${notOkSym()}"})\n"
            str += settings?.cond_alarm ? "    - Alarm Modes: (${(isInAlarmMode(settings?.cond_alarm)) ? "${okSym()}" : "${notOkSym()}"})\n" : ""
            // str += settings?.cond_alarm ? "    - Current Alarm: (${getAlarmSystemStatus()})\n" : ""
            str += settings?.cond_mode ? "    - Location Modes: (${(isInMode(settings?.cond_mode)) ? "${okSym()}" : "${notOkSym()}"})\n" : ""
            // str += settings?.cond_mode ? "    - Current Mode: (${location?.mode})\n" : ""
        }
        if(deviceCondConfigured()) {
            // str += " • Devices: (${deviceCondOk() ? "${okSym()}" : "${notOkSym()}"})\n"
            ["switch", "motion", "presence", "contact", "lock", "door"]?.each { evt->
                if(devCondConfigured(evt)) {
                    str += settings?."${sPre}${evt}"     ? " • ${evt?.capitalize()} (${settings?."${sPre}${evt}"?.size()}) (${checkDeviceCondOk(evt) ? "${okSym()}" : "${notOkSym()}"})\n" : ""
                    str += settings?."${sPre}${evt}_cmd" ? "    - Value: (${settings?."${sPre}${evt}_cmd"})\n" : ""
                    str += settings?."${sPre}${evt}_all" ? "    - Require All: (${settings?."${sPre}${evt}_all"})\n" : ""
                }
            }
        }
        str += "\ntap to modify..."
        return str
    } else {
        return "tap to configure..."
    }
}

String getZoneDesc() {
    if(devicesConfigured() && conditionsConfigured()) {
        List eDevs = parent?.getDevicesFromList(settings?.zone_EchoDevices)?.collect { it?.displayName as String }
        String str = eDevs?.size() ? "Echo Devices in Zone:\n${eDevs?.join("\n")}\n" : ""
        str += settings?.zone_delay ? " • Delay: (${settings?.zone_delay})\n" : ""
        str += "\nTap to modify..."
        return str
    } else {
        return "tap to configure..."
    }
}

String getTimeCondDesc(addPre=true) {
    def sun = getSunriseAndSunset()
    def sunsetTime = epochToTime(sun?.sunset?.time)
    def sunriseTime = epochToTime(sun?.sunrise?.time)
    def startType = settings?.cond_time_start_type
    def startTime = settings?.cond_time_start ? fmtTime(settings?.cond_time_start) : null
    def stopType = settings?.cond_time_stop_type
    def stopTime = settings?.cond_time_stop ? fmtTime(settings?.cond_time_stop) : null
    String startLbl = (
        (startType in ["Sunset", "Sunrise"]) ?
        ((startType == "Sunset") ? sunsetTime : sunriseTime) :
        startTime
    )
    def stopLbl = (
        (stopType in ["Sunrise", "Sunset"]) ?
        ((stopType == "Sunset") ? sunsetTime : sunriseTime) :
        stopTime
    )
    return ((startLbl && startLbl != "") && (stopLbl && stopLbl != "")) ? "${addPre ? "Time Condition:\n" : ""}(${startLbl} - ${stopLbl})" : "tap to configure..."
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
    // logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}

def getRandomItem(items) {
    def list = new ArrayList<String>();
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()));
}

/***********************************************************************************************************
    HELPER FUNCTIONS
************************************************************************************************************/
void settingUpdate(name, value, type=null) {
    if(name && type) { app?.updateSetting("$name", [type: "$type", value: value]) }
    else if (name && type == null) { app?.updateSetting(name.toString(), value) }
}

void settingRemove(String name) {
    logTrace("settingRemove($name)...")
    if(name && settings?.containsKey(name as String)) { isST() ? app?.deleteSetting(name as String) : app?.removeSetting(name as String) }
}

Map notifValEnum(allowCust = true) {
    Map items = [
        300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    if(allowCust) { items[100000] = "Custom" }
    return items
}

def fanTimeSecEnum() {
    def vals = [
        60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes"
    ]
    return vals
}

def longTimeSecEnum() {
    def vals = [
        0:"Off", 60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours", 10:"10 Seconds(Testing)"
    ]
    return vals
}

def shortTimeEnum() {
    def vals = [
        1:"1 Second", 2:"2 Seconds", 3:"3 Seconds", 4:"4 Seconds", 5:"5 Seconds", 6:"6 Seconds", 7:"7 Seconds",
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds", 15:"15 Seconds", 30:"30 Seconds", 60:"60 Seconds"
    ]
    return vals
}
List weekDaysEnum() {
    return ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
}

List monthEnum() {
    return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
}

Map getAlarmTrigOpts() {
    if(isST()) { return ["away":"Armed Away","stay":"Armed Home","off":"Disarmed"] }
    return ["armAway":"Armed Away","armHome":"Armed Home","disarm":"Disarmed", "alerts":"Alerts"]
}

def getShmIncidents() {
    def incidentThreshold = now() - 604800000
    return location.activeIncidents.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}

String getAlarmSystemStatus() {
    if(isST()) {
        def cur = location.currentState("alarmSystemStatus")?.value
        def inc = getShmIncidents()
        if(inc != null && inc?.size()) { cur = 'alarm_active' }
        return cur ?: "disarmed"
    } else { return location?.hsmStatus ?: "disarmed" }
}
Boolean pushStatus() { return (settings?.notif_sms_numbers?.toString()?.length()>=10 || settings?.notif_send_push || settings?.notif_pushover) ? ((settings?.notif_send_push || (settings?.notif_pushover && settings?.notif_pushover_devices)) ? "Push Enabled" : "Enabled") : null }
Integer getLastNotifMsgSec() { return !state?.lastActNotifMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastActNotifMsgDt, "getLastMsgSec").toInteger() }
Integer getLastChildInitRefreshSec() { return !state?.lastChildInitRefreshDt ? 3600 : GetTimeDiffSeconds(state?.lastChildInitRefreshDt, "getLastChildInitRefreshSec").toInteger() }
Boolean getOk2Notify() {
    Boolean smsOk = (settings?.notif_sms_numbers?.toString()?.length()>=10)
    Boolean pushOk = settings?.notif_send_push
    Boolean alexaMsg = (settings?.notif_alexa_mobile)
    Boolean pushOver = (settings?.notif_pushover && settings?.notif_pushover_devices)
    Boolean notifDevsOk = (settings?.notif_devs?.size())
    Boolean daysOk = settings?.notif_days ? (isDayOfWeek(settings?.notif_days)) : true
    Boolean timeOk = notifTimeOk()
    Boolean modesOk = settings?.notif_mode ? (isInMode(settings?.notif_mode)) : true
    logDebug("getOk2Notify() | smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver || alexaMsg: $alexaMsg || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
    if(!(smsOk || pushOk || alexaMsg || notifDevsOk || pushOver)) { return false }
    if(!(daysOk && modesOk && timeOk)) { return false }
    return true
}

Boolean notifTimeOk() {
    def startTime = null
    def stopTime = null
    def now = new Date()
    def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
    if(settings?.notif_time_start_type && settings?.notif_time_stop_type) {
        if(settings?.notif_time_start_type == "sunset") { startTime = sun?.sunset }
        else if(settings?.notif_time_start_type == "sunrise") { startTime = sun?.sunrise }
        else if(settings?.notif_time_start_type == "time" && settings?.notif_time_start) { startTime = settings?.notif_time_start }

        if(settings?.notif_time_stop_type == "sunset") { stopTime = sun?.sunset }
        else if(settings?.notif_time_stop_type == "sunrise") { stopTime = sun?.sunrise }
        else if(settings?.notif_time_stop_type == "time" && settings?.notif_time_stop) { stopTime = settings?.notif_time_stop }
    } else { return true }
    if(startTime && stopTime) {
        if(!isST()) {
            startTime = toDateTime(startTime)
            stopTime = toDateTime(stopTime)
        }
        return timeOfDayIsBetween(startTime, stopTime, new Date(), location?.timeZone)
    } else { return true }
}

// Sends the notifications based on app settings
public sendNotifMsg(String msgTitle, String msg, alexaDev=null, Boolean showEvt=true) {
    logTrace("sendNotifMsg() | msgTitle: ${msgTitle}, msg: ${msg}, $alexaDev, showEvt: ${showEvt}")
    List sentSrc = ["Push"]
    Boolean sent = false
    try {
        String newMsg = "${msgTitle}: ${msg}"
        String flatMsg = newMsg.toString().replaceAll("\n", " ")
        if(!getOk2Notify()) {
            logInfo( "sendNotifMsg: Message Skipped During Quiet Time ($flatMsg)")
            if(showEvt) { sendNotificationEvent(newMsg) }
        } else {
            if(settings?.notif_send_push) {
                sendSrc?.push("Push Message")
                if(showEvt) {
                    sendPush(newMsg)	// sends push and notification feed
                } else { sendPushMessage(newMsg) } // sends push
                sent = true
            }
            if(settings?.notif_pushover && settings?.notif_pushover_devices) {
                sentSrc?.push("Pushover Message")
                Map msgObj = [title: msgTitle, message: msg, priority: (settings?.notif_pushover_priority?:0)]
                if(settings?.notif_pushover_sound) { msgObj?.sound = settings?.notif_pushover_sound }
                buildPushMessage(settings?.notif_pushover_devices, msgObj, true)
                sent = true
            }
            String smsPhones = settings?.notif_sms_numbers?.toString() ?: null
            if(smsPhones) {
                List phones = smsPhones?.toString()?.tokenize(",")
                for (phone in phones) {
                    String t0 = newMsg.take(140)
                    if(showEvt) {
                        sendSms(phone?.trim(), t0)	// send SMS and notification feed
                    } else { sendSmsMessage(phone?.trim(), t0) } // send SMS
                }
                sentSrc?.push("SMS Message to [${phones}]")
                sent = true
            }
            if(settings?.notif_devs) {
                sentSrc?.push("Notification Devices")
                settings?.notif_devs?.each { it?.deviceNotification(msg as String) }
                sent = true
            }
            if(settings?.notif_alexa_mobile && alexaDev) {
                alexaDev?.sendAlexaAppNotification(msg)
                sentSrc?.push("Alexa Mobile App")
                sent = true
            }
            if(sent) {
                state?.lastActionNotificationMsg = flatMsg
                state?.lastActNotifMsgDt = getDtNow()
                logDebug("sendNotifMsg: Sent ${sendSrc} (${flatMsg})")
            }
        }
    } catch (ex) {
        logError("sendNotifMsg $sentstr Exception: ${ex}")
    }
    return sent
}

Boolean isActNotifConfigured() {
    if(customMsgRequired() && (!settings?.notif_use_custom || settings?.notif_custom_message)) { return false }
    return (settings?.notif_sms_numbers?.toString()?.length()>=10 || settings?.notif_send_push || settings?.notif_devs || settings?.notif_alexa_mobile || (isST() && settings?.notif_pushover && settings?.notif_pushover_devices))
}

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
Integer versionStr2Int(str) { return str ? str.toString()?.replaceAll("\\.", "")?.toInteger() : null }
Boolean checkMinVersion() { return (versionStr2Int(appVersion()) < parent?.minVersions()["actionApp"]) }
Boolean isPaused() { return (settings?.zonePause == true) }

String okSym() { return "\u2713" }
String notOkSym() { return "\u2715" }
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
private logDebug(msg) { if(settings?.logDebug == true) { log.debug "Zone (v${appVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info " Zone (v${appVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace "Zone (v${appVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { log.warn " Zone (v${appVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, 15); } }
private logError(msg) { if(settings?.logError != false) { log.error "Zone (v${appVersion()}) | ${msg}"; }; addToLogHistory("errorHistory", msg, 15); }

Map getLogHistory() {
    return [ warnings: atomicState?.warnHistory ?: [], errors: atomicState?.errorHistory ?: [] ]
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

//*******************************************************************
//    CLONE CHILD LOGIC
//*******************************************************************
public getDuplSettingData() {
    Map typeObj = [
        s: [
            bool: ["notif_pushover", "notif_alexa_mobile", "logInfo", "logWarn", "logError", "logDebug", "logTrace"],
            enum: [ "triggerEvents", "act_EchoDevices", "actionType", "cond_alarm", "cond_months", "cond_days", "notif_pushover_devices", "notif_pushover_priority", "notif_pushover_sound", "trig_alarm", "trig_guard" ],
            mode: ["cond_mode", "trig_mode"],
            number: [],
            text: ["appLbl"]
        ],
        e: [
            bool: ["_all", "_avg", "_once", "_send_push", "_use_custom"],
            enum: ["_cmd", "_type", "_time_start_type", "cond_time_stop_type", "_routineExecuted", "_scheduled_sunState", "_scheduled_recurrence", "_scheduled_days", "_scheduled_weeks", "_scheduled_months"],
            number: ["_wait", "_low", "_high", "_equal", "_delay", "_volume", "_scheduled_sunState_offset", "_after", "_after_repeat"],
            text: ["_txt", "_sms_numbers"],
            time: ["_time_start", "_time_stop", "_scheduled_time"]
        ],
        caps: [
            _battery: "battery",
            _contact: "contactSensor",
            _door: "garageDoorControl",
            _temperature: "temperatureMeasurement",
            _illuminance: "illuminanceMeasurement",
            _humidity: "relativeHumidityMeasurement",
            _motion: "motionSensor",
            _level: "switchLevel",
            _presence: "presenceSensor",
            _switch: "switch",
            _power: "powerMeter",
            _shade: "windowShades",
            _water: "waterSensor",
            _valve: "valve",
            _thermostat: "thermostat",
            _carbonMonoxide: "carbonMonoxideDetector",
            _smoke: "smokeDetector",
            _lock: "lock"
        ],
        dev: [
            _scene: "sceneActivator"
        ]
    ]
    Map setObjs = [:]
    typeObj?.s?.each { sk,sv->
        sv?.each { svi-> if(settings?.containsKey(svi)) { setObjs[svi] = [type: sk as String, value: settings[svi] ] } }
    }
    typeObj?.e?.each { ek,ev->
        ev?.each { evi-> settings?.findAll { it?.key?.endsWith(evi) }?.each { fk, fv-> setObjs[fk] = [type: ek as String, value: fv] } }
    }
    typeObj?.caps?.each { ck,cv->
        settings?.findAll { it?.key?.endsWith(ck) }?.each { fk, fv-> setObjs[fk] = [type: "capability.${cv}" as String, value: fv?.collect { it?.id as String }] }
    }
    typeObj?.dev?.each { dk,dv->
        settings?.findAll { it?.key?.endsWith(dk) }?.each { fk, fv-> setObjs[fk] = [type: "device.${dv}" as String, value: fv] }
    }
    Map data = [:]
    data?.label = app?.getLabel()?.toString()?.replace(" | (\u23F8)", "")
    data?.settings = setObjs
    return data
}

public getDuplStateData() {
    List stskip = ["isInstalled", "isParent", "lastActNotifMsgDt", "lastActionNotificationMsg", "setupComplete", "valEvtHistory", "warnHistory", "errorHistory"]
    return state?.findAll { !(it?.key in stskip) }
}
