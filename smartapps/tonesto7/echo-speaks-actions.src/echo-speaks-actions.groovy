/**
 *  Echo Speaks Actions
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

import groovy.json.*
import java.text.SimpleDateFormat

String appVersion()  { return "3.1.0.1" }
String appModified() { return "2019-09-30" }
String appAuthor()   { return "Anthony S." }
Boolean isBeta()     { return false }
Boolean isST()       { return (getPlatform() == "SmartThings") }

// TODO: Finish the button trigger logic
definition(
    name: "Echo Speaks - Actions",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllow you to create echo device actions based on Events in your SmartThings home",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    importUrl  : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/smartapps/tonesto7/echo-speaks-actions.src/echo-speaks-actions.groovy",
    pausable: true)

preferences {
    page(name: "startPage")
    page(name: "uhOhPage")
    page(name: "codeUpdatePage")
    page(name: "mainPage", install: false, uninstall: false)
    page(name: "prefsPage")
    page(name: "triggersPage")
    page(name: "conditionsPage")
    page(name: "condTimePage")
    page(name: "actionsPage")
    page(name: "actNotifPage")
    page(name: "actNotifTimePage")
    page(name: "searchTuneInResultsPage")
    page(name: "uninstallPage")
    page(name: "namePage")
}

def startPage() {
    if(parent != null) {
        if(!state?.isInstalled && parent?.childInstallOk() != true) { uhOhPage() }
        else {
            state?.isParent = false
            if(checkMinVersion()) { codeUpdatePage() }
            else { mainPage() }
        }
    } else { uhOhPage() }
}

def codeUpdatePage () {
    return dynamicPage(name: "codeUpdatePage", title: "Update is Required", install: false, uninstall: false) {
        section() { paragraph "Looks like your Action App needs an update\n\nPlease make sure all app and device code is updated to the most current version\n\nOnce updated your actions will resume normal operation.", required: true, state: null, image: getAppImg("exclude") }
    }
}

def uhOhPage () {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            paragraph "HOUSTON WE HAVE A PROBLEM!\n\nEcho Speaks - Actions can't be directly installed from the Marketplace.\n\nPlease use the Echo Speaks SmartApp to configure them.", required: true,
            state: null, image: getAppImg("exclude")
        }
        if(isST()) { remove("Remove this invalid Action", "WARNING!!!", "This is a BAD install of an Action SHOULD be removed") }
    }
}

def appInfoSect(sect=true)	{
    section() { href "empty", title: pTS("${app?.name}", getAppImg("es_actions", true)), description: "(V${appVersion()})", image: getAppImg("es_actions") }
}

List cleanedTriggerList() {
    List newList = []
    settings?.triggerTypes?.each {
        newList?.push(it?.toString()?.split("::")[0] as String)
    }
    return newList?.unique()
}

String selTriggerTypes(type) {
    return settings?.triggerTypes?.findAll { it?.startsWith(type as String) }?.collect { it?.toString()?.split("::")[1] }?.join(", ")
}

private def buildTriggerEnum() {
    List enumOpts = []
    Map buildItems = [:]
    buildItems["Date/Time"] = ["scheduled":"Scheduled Time"]?.sort{ it?.key }
    buildItems["Location"] = ["mode":"Modes", "routineExecuted":"Routines"]?.sort{ it?.key }
    if(!isST()) {
        buildItems?.Location?.remove("routineExecuted")
        //TODO: Once I can find a reliable method to list the scenes and subscribe to events on Hubitat I will re-activate
        // buildItems?.Location?.scene = "Scenes"
    }
    // buildItems["Weather Events"] = ["Weather":"Weather"]
    buildItems["Safety & Security"] = ["alarm": "${getAlarmSystemName()}", "smoke":"Fire/Smoke", "carbon":"Carbon Monoxide", "guard":"Alexa Guard"]?.sort{ it?.key }
    if(!parent?.guardAutoConfigured()) { buildItems["Safety & Security"]?.remove("guard") }
    buildItems["Actionable Devices"] = ["lock":"Locks", "switch":"Outlets/Switches", "level":"Dimmers/Level", "door":"Garage Door Openers", "valve":"Valves", "shade":"Window Shades", "thermostat":"Thermostat"]?.sort{ it?.key }
    buildItems["Sensor Devices"] = ["contact":"Contacts | Doors | Windows", "battery":"Battery Level", "motion":"Motion", "illuminance": "Illuminance/Lux", "presence":"Presence", "temperature":"Temperature", "humidity":"Humidity", "water":"Water", "power":"Power"]?.sort{ it?.key }
    if(isST()) {
        buildItems?.each { key, val-> addInputGrp(enumOpts, key, val) }
        // log.debug "enumOpts: $enumOpts"
        return enumOpts
    } else { return buildItems?.collectEntries { it?.value } }
}

def mainPage() {
    Boolean newInstall = (state?.isInstalled != true)
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: (newInstall == true), install: !newInstall) {
        appInfoSect()
        Boolean paused = isPaused()
        Boolean dup = (state?.dupPendingSetup == true)
        if(dup) {
            section() {
                paragraph pTS("This Action was just created from an existing action.  Please review the settings and save to activate...", getAppImg("pause_orange", true), false, "red"), required: true, state: null, image: getAppImg("pause_orange")
            }
        }
        if(paused) {
            section() {
                paragraph pTS("This Action is currently in a paused state...\nTo edit the please un-pause", getAppImg("pause_orange", true), false, "red"), required: true, state: null, image: getAppImg("pause_orange")
            }
        } else {
            Boolean trigConf = triggersConfigured()
            Boolean condConf = conditionsConfigured()
            Boolean actConf = executionConfigured()
            section(sTS("Configuration: Part 1")) {
                Map actionOpts = [
                    "speak":"Speak (SSML Supported)", "announcement":"Announcement (SSML Supported)", "sequence":"Execute Sequence", "weather":"Weather Report", "playback":"Playback Control",
                    "builtin":"Sing, Jokes, Story, etc.", "music":"Play Music", "calendar":"Calendar Events", "alarm":"Create Alarm", "reminder":"Create Reminder", "dnd":"Do Not Disturb",
                    "bluetooth":"Bluetooth Control", "wakeword":"Wake Word", "alexaroutine": "Execute Alexa Routine(s)"
                ]
                input "actionType", "enum", title: inTS("Action Type", getAppImg("list", true)), description: "", options: actionOpts, multiple: false, required: true, submitOnChange: true, image: getAppImg("list")
            }
            section (sTS("Configuration: Part 2")) {
                if(settings?.actionType) {
                    href "triggersPage", title: inTS("Action Triggers", getAppImg("trigger", true)), description: getTriggersDesc(), state: (trigConf ? "complete" : ""), required: true, image: getAppImg("trigger")
                } else { paragraph pTS("These options will be shown once the action type is configured.", getAppImg("info", true)) }
            }
            section(sTS("Configuration: Part 3")) {
                if(settings?.actionType && trigConf) {
                    href "conditionsPage", title: inTS("Condition/Restrictions\n(Optional)", getAppImg("conditions", true)), description: getConditionsDesc(), state: (condConf ? "complete": ""), image: getAppImg("conditions")
                } else { paragraph pTS("These options will be shown once the triggers are configured.", getAppImg("info", true)) }
            }
            section(sTS("Configuration: Part 4")) {
                if(settings?.actionType && trigConf) {
                    href "actionsPage", title: inTS("Execution Config", getAppImg("es_actions", true)), description: getActionDesc(), state: (actConf ? "complete" : ""), required: true, image: getAppImg("es_actions")
                } else { paragraph pTS("These options will be shown once the triggers are configured.", getAppImg("info", true)) }
            }
            if(settings?.actionType && trigConf && actConf) {
                section(sTS("Notifications:")) {
                    def t0 = getAppNotifDesc()
                    href "actNotifPage", title: inTS("Send Notifications", getAppImg("notification2", true)), description: (t0 ? "${t0}\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("notification2")
                }
            }
        }

        section(sTS("Preferences")) {
            if(!paused) {
                href "prefsPage", title: inTS("Debug/Preferences", getAppImg("settings", true)), description: "", image: getAppImg("settings")
            }
            if(state?.isInstalled) {
                input "actionPause", "bool", title: inTS("Pause Action?", getAppImg("pause_orange", true)), defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange")
                if(actionPause) { unsubscribe() }
                if(!paused) {
                    input "actTestRun", "bool", title: inTS("Test this action?", getAppImg("testing", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("testing")
                    if(actTestRun) { executeActTest() }
                }
            }
        }
        if(state?.isInstalled) {
            section(sTS("Name this Action:")) {
                input "appLbl", "text", title: inTS("Action Name", getAppImg("name_tag", true)), description: "", required:true, submitOnChange: true, image: getAppImg("name_tag")
            }
            section(sTS("Remove Action:")) {
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
    return dynamicPage(name: "namePage", install: true, uninstall: true) {
        section(sTS("Name this Automation:")) {
            input "appLbl", "text", title: inTS("Label this Action", getAppImg("name_tag", true)), description: "", required:true, submitOnChange: true, image: getAppImg("name_tag")
        }
    }
}

def triggersPage() {
    return dynamicPage(name: "triggersPage", nextPage: "mainPage", uninstall: false, install: false) {
        List stRoutines = getLocationRoutines() ?: []
        Boolean showSpeakEvtVars = false
        section (sTS("Select Capabilities")) {
            if(isST()) {
                input "triggerEvents", "enum", title: "Select Trigger Event(s)", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getAppImg("trigger")
            } else {
                input "triggerEvents", "enum", title: inTS("Select Trigger Event(s)", getAppImg("trigger", true)), options: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true
            }
        }
        if (settings?.triggerEvents?.size()) {
            Integer trigItemCnt = 0
            Integer trigEvtCnt = settings?.triggerEvents?.size()
            if(!(settings?.triggerEvents in ["Scheduled", "Weather"])) { showSpeakEvtVars = true }
            if (valTrigEvt("scheduled")) {
                section(sTS("Time Based Events"), hideable: true) {
                    if(!settings?.trig_scheduled_time) {
                        input "trig_scheduled_sunState", "enum", title: inTS("Sunrise or Sunset...", getAppImg("sunrise", true)), options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true, image: getAppImg("sunrise")
                        if(settings?.trig_scheduled_sunState) {
                            input "trig_scheduled_sunState_offset", "number", range: "*..*", title: inTS("Offset event this number of minutes (+/-)", getAppImg(settings?.trig_scheduled_sunState?.toString()?.toLowerCase(), true)),
                                    required: true, image: getPublicImg(settings?.trig_scheduled_sunState?.toString()?.toLowerCase() + "")
                        }
                    }
                    if(!settings?.trig_scheduled_sunState) {
                        input "trig_scheduled_time", "time", title: inTS("Time of Day?", getAppImg("clock", true)), required: false, submitOnChange: true, image: getPublicImg("clock")
                        if(settings?.trig_scheduled_time || settings?.trig_scheduled_sunState) {
                            input "trig_scheduled_recurrence", "enum", title: inTS("Recurrence?", getAppImg("day_calendar", true)), description: "(Optional)", multiple: false, required: false, submitOnChange: true, options: ["Once", "Daily", "Weekly", "Monthly"], defaultValue: "Once", image: getAppImg("day_calendar")
                            Boolean dayReq = (settings?.trig_scheduled_recurrence in ["Weekly", "Monthly"])
                            Boolean weekReq = (settings?.trig_scheduled_recurrence in ["Weekly", "Monthly"])
                            Boolean monReq = (settings?.trig_scheduled_recurrence in ["Monthly"])
                            if(settings?.trig_scheduled_recurrence) {
                                input "trig_scheduled_days", "enum", title: inTS("Day(s) of the week", getAppImg("day_calendar", true)), description: (!dayReq ? "(Optional)" : ""), multiple: true, required: dayReq, submitOnChange: true, options: weekDaysEnum(), image: getAppImg("day_calendar")
                                input "trig_scheduled_weeks", "enum", title: inTS("Weeks(s) of the month", getAppImg("day_calendar", true)), description: (!weekReq ? "(Optional)" : ""), multiple: true, required: weekReq, submitOnChange: true, options: ["1", "2", "3", "4", "5"], image: getAppImg("day_calendar")
                                input "trig_scheduled_months", "enum", title: inTS("Month(s) of the year", getAppImg("day_calendar", true)), description: (!monReq ? "(Optional)" : ""), multiple: true, required: monReq, submitOnChange: true, options: monthEnum(), image: getAppImg("day_calendar")
                            }
                        }
                    }
                }
            }

            if (valTrigEvt("alarm")) {
                section (sTS("${getAlarmSystemName()} (${getAlarmSystemName(true)}) Events"), hideable: true) {
                    input "trig_alarm", "enum", title: inTS("${getAlarmSystemName()} Modes", getAppImg("alarm_home", true)), options: getAlarmTrigOpts(), multiple: true, required: true, submitOnChange: true, image: getAppImg("alarm_home")
                    if(trig_alarm) {
                        triggerVariableDesc("alarm", false, trigItemCnt++)
                    }
                    // if("alerts" in trig_alarm) {
                    //     input "trig_alarm_alerts_clear", "bool", title: "Send the update when Alerts are cleared.", required: false, defaultValue: false, submitOnChange: true
                    // }
                }
            }

            if (valTrigEvt("guard")) {
                section (sTS("Alexa Guard Events"), hideable: true) {
                    input "trig_guard", "enum", title: inTS("Alexa Guard Modes", getAppImg("alarm_home", true)), options: ["ARMED_STAY", "ARMED_AWAY", "any"], multiple: true, required: true, submitOnChange: true, image: getAppImg("alarm_home")
                    if(trig_alarm) {
                        triggerVariableDesc("guard", false, trigItemCnt++)
                    }
                }
            }

            if (valTrigEvt("mode")) {
                section (sTS("Mode Events"), hideable: true) {
                    input "trig_mode", "mode", title: inTS("Location Modes", getAppImg("mode", true)), multiple: true, required: true, submitOnChange: true, image: getAppImg("mode")
                    if(settings?.trig_mode) {
                        input "trig_mode_once", "bool", title: inTS("Only alert once a day?\n(per mode)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                        triggerVariableDesc("mode", false, trigItemCnt++)
                    }
                }
            }

            if(valTrigEvt("routineExecuted")) {
                section(sTS("Routine Events"), hideable: true) {
                    input "trig_routineExecuted", "enum", title: inTS("Routines", getAppImg("routine", true)), options: stRoutines, multiple: true, required: true, submitOnChange: true, image: getAppImg("routine")
                    if(settings?.trig_routineExecuted) {
                        input "trig_routineExecuted_once", "bool", title: inTS("Only alert once a day?\n(per routine)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                        triggerVariableDesc("routineExecuted", false, trigItemCnt++)
                    }
                }
            }

            if(valTrigEvt("scene")) {
                section(sTS("Scene Events"), hideable: true) {
                    input "trig_scene", "device.sceneActivator", title: inTS("Scene Devices", getAppImg("routine", true)), multiple: true, required: true, submitOnChange: true, image: getAppImg("routine")
                    if(settings?.trig_scene) {
                        input "trig_scene_once", "bool", title: inTS("Only alert once a day?\n(per scene)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                        triggerVariableDesc("scene", false, trigItemCnt++)
                    }
                }
            }

            if (valTrigEvt("switch")) {
                trigNonNumSect("switch", "switch", "Switches", "Switches", ["on", "off", "any"], "are turned", ["on", "off"], "switch", trigItemCnt++)
            }

            if (valTrigEvt("level")) {
                trigNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level is", "speed_knob", trigItemCnt++)
            }

            if (valTrigEvt("battery")) {
                trigNumValSect("battery", "battery", "Battery Level", "Batteries", "Level is", "speed_knob", trigItemCnt++)
            }

            if (valTrigEvt("motion")) {
                trigNonNumSect("motion", "motionSensor", "Motion Sensors", "Motion Sensors", ["active", "inactive", "any"], "become", ["active", "inactive"], "motion", trigItemCnt++)
            }

            if (valTrigEvt("presence")) {
                trigNonNumSect("presence", "presenceSensor", "Presence Sensors", "Presence Sensors", ["present", "not present", "any"], "changes to", ["present", "not present"], "presence", trigItemCnt++)
            }

            if (valTrigEvt("contact")) {
                trigNonNumSect("contact", "contactSensor", "Contacts, Doors, Windows", "Contacts, Doors, Windows", ["open", "closed", "any"], "changes to", ["open", "closed"], "contact", trigItemCnt++)
            }

            if (valTrigEvt("door")) {
                trigNonNumSect("door", "garageDoorControl", "Garage Door Openers", "Garage Doors", ["open", "closed", "opening", "closing", "any"], "changes to", ["open", "closed"], "garage_door", trigItemCnt++)
            }

            if (valTrigEvt("lock")) {
                //TODO: Add lock code triggers
                trigNonNumSect("lock", "lock", "Locks", "Smart Locks", ["locked", "unlocked", "any"], "changes to", ["locked", "unlocked"], "lock", trigItemCnt++)
            }

            if (valTrigEvt("temperature")) {
                trigNumValSect("temperature", "temperatureMeasurement", "Temperature Sensor", "Temperature Sensors", "Temperature", "temperature", trigItemCnt++)
            }

            if (valTrigEvt("humidity")) {
                trigNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Sensors", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity", trigItemCnt++)
            }

            if (valTrigEvt("water") in settings?.triggerEvents) {
                trigNonNumSect("water", "waterSensor", "Water Sensors", "Water/Moisture Sensors", ["wet", "dry", "any"], "changes to", ["wet", "dry"], "water", trigItemCnt++)
            }

            if (valTrigEvt("power")) {
                trigNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power", trigItemCnt++)
            }

            if (valTrigEvt("carbon")) {
                section (sTS("Carbon Monoxide Events"), hideable: true) {
                    input "trig_carbonMonoxide", "capability.carbonMonoxideDetector", title: inTS("Carbon Monoxide Sensors", getAppImg("co", true)), required: !(settings?.trig_smoke), multiple: true, submitOnChange: true, image: getAppImg("co")
                    if (settings?.trig_carbonMonoxide) {
                        input "trig_carbonMonoxide_cmd", "enum", title: inTS("changes to?", getAppImg("command", true)), options: ["detected", "clear", "any"], required: false, submitOnChange: true, image: getAppImg("command")
                        if(settings?.trig_carbonMonoxide_cmd) {
                            if (settings?.trig_carbonMonoxide?.size() > 1 && settings?.trig_carbonMonoxide_cmd != "any") {
                                input "trig_carbonMonoxide_all", "bool", title: inTS("Require ALL Smoke Detectors to be (${settings?.trig_carbonMonoxide_cmd})?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                            }
                            triggerVariableDesc("carbonMonoxide", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("smoke")) {
                section (sTS("Smoke Events"), hideable: true) {
                    input "trig_smoke", "capability.smokeDetector", title: inTS("Smoke Detectors", getAppImg("smoke", true)), required: !(settings?.trig_carbonMonoxide), multiple: true, submitOnChange: true, image: getAppImg("smoke")
                    if (settings?.trig_smoke) {
                        input "trig_smoke_cmd", "enum", title: inTS("changes to?", getAppImg("command", true)), options: ["detected", "clear", "any"], required: false, submitOnChange: true, image: getAppImg("command")
                        if(settings?.trig_smoke_cmd) {
                            if (settings?.trig_smoke?.size() > 1 && settings?.trig_smoke_cmd != "any") {
                                input "trig_smoke_all", "bool", title: inTS("Require ALL Smoke Detectors to be (${settings?.trig_smoke_cmd})?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                            }
                            triggerVariableDesc("smoke", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("illuminance")) {
                trigNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Events", "Illuminance Sensors", "Lux Level (%)", "illuminance", trigItemCnt++)
            }

            if (valTrigEvt("shade")) {
                trigNonNumSect("shade", "windowShades", "Window Shades", "Window Shades", ["open", "closed", "opening", "closing", "any"], "changes to", ["open", "closed"], "window_shade", trigItemCnt++)
            }

            if (valTrigEvt("valve")) {
                trigNonNumSect("valve", "valve", "Valves", "Valves", ["open", "closed", "any"], "changes to", ["open", "closed"], "valve", trigItemCnt++)
            }

            if (valTrigEvt("thermostat")) {
                section (sTS("Thermostat Events"), hideable: true) {
                    input "trig_thermostat", "capability.thermostat", title: inTS("Thermostat", getAppImg("thermostat", true)), multiple: true, required: true, submitOnChange: true, image: getAppImg("thermostat")
                    if (settings?.trig_thermostat) {
                        input "trig_thermostat_cmd", "enum", title: inTS("Thermostat Event is...", getAppImg("command", true)), options: ["ambient":"Ambient Change", "setpoint":"Setpoint Change", "mode":"Mode Change", "operatingstate":"Operating State Change"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
                        if (settings?.trig_thermostat_cmd) {
                            if (settings?.trig_thermostat_cmd == "setpoint") {
                                input "trig_thermostat_setpoint_type", "enum", title: inTS("SetPoint type is...", getAppImg("command", true)), options: ["cooling", "heating", "any"], required: false, submitOnChange: true, image: getAppImg("command")
                                if(settings?.trig_thermostat_setpoint_type) {
                                    input "trig_thermostat_setpoint_cmd", "enum", title: inTS("Setpoint temp is...", getAppImg("command", true)), options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
                                    if (settings?.trig_thermostat_setpoint_cmd) {
                                        if (settings?.trig_thermostat_setpoint_cmd in ["between", "below"]) {
                                            input "trig_thermostat_setpoint_low", "number", title: inTS("a ${trig_thermostat_setpoint_cmd == "between" ? "Low " : ""}Setpoint temp of..."), required: true, submitOnChange: true
                                        }
                                        if (settings?.trig_thermostat_setpoint_cmd in ["between", "above"]) {
                                            input "trig_thermostat_setpoint_high", "number", title: inTS("${trig_thermostat_setpoint_cmd == "between" ? "and a high " : "a "}Setpoint temp of..."), required: true, submitOnChange: true
                                        }
                                        if (settings?.trig_thermostat_setpoint_cmd == "equals") {
                                            input "trig_thermostat_setpoint_equal", "number", title: inTS("a Setpoint temp of..."), required: true, submitOnChange: true
                                        }
                                    }
                                }
                            }
                            if(settings?.trig_thermostat_cmd == "ambient") {
                                input "trig_thermostat_ambient_cmd", "enum", title: inTS("Ambient Temp is...", getAppImg("command", true)), options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
                                if (settings?.trig_thermostat_ambient_cmd) {
                                    if (settings?.trig_thermostat_ambient_cmd in ["between", "below"]) {
                                        input "trig_thermostat_ambient_low", "number", title: inTS("a ${trig_thermostat_ambient_cmd == "between" ? "Low " : ""}Ambient Temp of..."), required: true, submitOnChange: true
                                    }
                                    if (settings?.trig_thermostat_ambient_cmd in ["between", "above"]) {
                                        input "trig_thermostat_ambient_high", "number", title: inTS("${trig_thermostat_ambient_cmd == "between" ? "and a high " : "a "}Ambient Temp of..."), required: true, submitOnChange: true
                                    }
                                    if (settings?.trig_thermostat_ambient_cmd == "equals") {
                                        input "trig_thermostat_ambient_equal", "number", title: inTS("a Ambient Temp of..."), required: true, submitOnChange: true
                                    }
                                }
                            }
                            if (settings?.trig_thermostat_cmd == "mode") {
                                input "trig_thermostat_mode_cmd", "enum", title: inTS("Hvac Mode changes to?", getAppImg("command", true)), options: ["auto", "cool", " heat", "emergency heat", "off", "every mode"], required: true, submitOnChange: true, image: getAppImg("command")
                            }
                            if (settings?.trig_thermostat_cmd == "operatingstate") {
                                input "trig_thermostat_state_cmd", "enum", title: inTS("Operating State changes to?", getAppImg("command", true)), options: ["cooling", "heating", "idle", "every state"], required: true, submitOnChange: true, image: getAppImg("command")
                            }
                            input "trig_thermostat_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                            input "trig_thermostat_wait", "number", title: inTS("Wait between each alert", getAppImg("delay_time", true)), required: false, defaultValue: 120, submitOnChange: true, image: getAppImg("delay_time")
                            triggerVariableDesc("thermostat", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("weather")) {
                section(sTS("Weather Events"), hideable: true) {
                    paragraph pTS("Weather Events are not configured to take actions yet.", getAppImg("weather")), state: null, image: getAppImg("weather")
                    //TODO: Buildout weather alerts
                    input "trig_weather_cmd", "enum", title: inTS("Weather Alerts", getAppImg("command", true)), required: true, multiple: true, submitOnChange: true, image: getAppImg("command"),
                        options: [
                            "TOR":	"Tornado Warning",
                            "TOW":	"Tornado Watch",
                            "WRN":	"Severe Thunderstorm Warning",
                            "SEW":	"Severe Thunderstorm Watch",
                            "WIN":	"Winter Weather Advisory",
                            "FLO":	"Flood Warning",
                            "WND":	"High Wind Advisoryt",
                            "HEA":	"Heat Advisory",
                            "FOG":	"Dense Fog Advisory",
                            "FIR":	"Fire Weather Advisory",
                            "VOL":	"Volcanic Activity Statement",
                            "HWW":	"Hurricane Wind Warning"
                        ]
                }
            }
            if(triggersConfigured()) {
                section("") {
                    paragraph pTS("You're all done with this step.  Press Done/Save", getAppImg("done", true)), state: "complete", image: getAppImg("done")
                }
            }
        }
        state?.showSpeakEvtVars = showSpeakEvtVars
    }
}

def trigNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, cmdAfterOpts, String image, Integer trigItemCnt) {
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: inTS(devTitle, getAppImg(image, true)), multiple: true, required: true, submitOnChange: true, image: getAppImg(image)
        if (settings?."trig_${inType}") {
            input "trig_${inType}_cmd", "enum", title: inTS("${cmdTitle}...", getAppImg("command", true)), options: cmdOpts, multiple: false, required: true, submitOnChange: true, image: getAppImg("command")
            if(settings?."trig_${inType}_cmd") {
                if (settings?."trig_${inType}"?.size() > 1 && settings?."trig_${inType}_cmd" != "any") {
                    input "trig_${inType}_all", "bool", title: inTS("Require ALL ${devTitle} to be (${settings?."trig_${inType}_cmd"})?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                }
                if(settings?."trig_${inType}_cmd" in cmdAfterOpts) {
                    input "trig_${inType}_after", "number", title: inTS("Only after (${settings?."trig_${inType}_cmd"}) for (xx) seconds?", getAppImg("delay_time", true)), required: false, defaultValue: null, submitOnChange: true, image: getAppImg("delay_time")
                    if(settings?."trig_${inType}_after") {
                        input "trig_${inType}_after_repeat", "number", title: inTS("Repeat every (xx) seconds until it's not ${settings?."trig_${inType}_cmd"}?", getAppImg("delay_time", true)), required: false, defaultValue: null, submitOnChange: true, image: getAppImg("delay_time")
                    }
                }
                if(!settings?."trig_${inType}_after") {
                    input "trig_${inType}_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                    input "trig_${inType}_wait", "number", title: inTS("Wait between each report (xx) seconds\n(Optional)", getAppImg("delay_time", true)), required: false, defaultValue: null, submitOnChange: true, image: getAppImg("delay_time")
                }
                triggerVariableDesc(inType, true, trigItemCnt)
            }
        }
    }
}

def trigNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, Integer trigItemCnt) {
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", tite: inTS(devTitle, getAppImg(image, true)), multiple: true, submitOnChange: true, required: true, image: getAppImg(image)
        if(settings?."trig_${inType}") {
            input "trig_${inType}_cmd", "enum", title: inTS("${cmdTitle} is...", getAppImg("command", true)), options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
            if (settings?."trig_${inType}_cmd") {
                if (settings?."trig_${inType}_cmd" in ["between", "below"]) {
                    input "trig_${inType}_low", "number", title: inTS("a ${settings?."trig_${inType}_cmd" == "between" ? "Low " : ""}${cmdTitle} of...", getAppImg("low", true)), required: true, submitOnChange: true, image: getAppImg("low")
                }
                if (settings?."trig_${inType}_cmd" in ["between", "above"]) {
                    input "trig_${inType}_high", "number", title: inTS("${settings?."trig_${inType}_cmd" == "between" ? "and a high " : "a "}${cmdTitle} of...", getAppImg("high", true)), required: true, submitOnChange: true, image: getAppImg("high")
                }
                if (settings?."trig_${inType}_cmd" == "equals") {
                    input "trig_${inType}_equal", "number", title: inTS("a ${cmdTitle} of...", getAppImg("equal", true)), required: true, submitOnChange: true, image: getAppImg("equal")
                }
                if (settings?."trig_${inType}"?.size() > 1) {
                    input "trig_${inType}_all", "bool", title: inTS("Require ALL devices to be (${settings?."trig_${inType}_cmd"}) values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                    if(!settings?."trig_${inType}_all") {
                        input "trig_${inType}_avg", "bool", title: inTS("Use the average of all selected device values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                    }
                }
                input "trig_${inType}_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                input "trig_${inType}_wait", "number", title: inTS("Wait between each report", getAppImg("delay_time", true)), required: false, defaultValue: 120, submitOnChange: true, image: getAppImg("delay_time")
                triggerVariableDesc(inType, false, trigItemCnt)
            }
        }
    }
}

Boolean scheduleTriggers() {
    return (settings?.trig_scheduled_time || settings?.trig_scheduled_sunState)
}

Boolean locationTriggers() {
    return (settings?.trig_mode || settings?.trig_alarm || settings?.trig_routineExecuted || settings?.trig_scene || settings?.trig_guard)
}

Boolean deviceTriggers() {
    return (settings?.trig_Buttons || (settings?.trig_shade && settings?.trig_shade_cmd) || (settings?.trig_door && settings?.trig_door_cmd) || (settings?.trig_valve && settings?.trig_valve_cmd) ||
            (settings?.trig_switch && settings?.trig_switch_cmd) || (settings?.trig_level && settings?.trig_level_cmd) || (settings?.trig_lock && settings?.trig_lock_cmd) ||
            (settings?.trig_battery && settings?.trig_battery_cmd) || (thermostatTriggers())
    )
}

Boolean thermostatTriggers() {
    if (settings?.trig_thermostat && settings?.trig_thermostat_cmd) {
        switch(settings?.trig_thermostat_cmd) {
            case "setpoint":
                return (settings?.trig_thermostat_setpoint_cmd && (trig_thermostat_setpoint_low || trig_thermostat_setpoint_high || trig_thermostat_setpoint_equal))
            case "mode":
                return (settings?.trig_thermostat_mode_cmd)
            case "operatingstate":
                return (settings?.trig_thermostat_state_cmd)
            case "ambient":
                return (settings?.trig_thermostat_ambient_cmd && (trig_thermostat_ambient_low || trig_thermostat_ambient_high || trig_thermostat_ambient_equal))
        }
    }
    return false
}

Boolean sensorTriggers() {
    return (
        (settings?.trig_temperature && settings?.trig_temperature_cmd) || (settings?.trig_carbonMonoxide && settings?.trig_carbonMonoxide_cmd) || (settings?.trig_humidity && settings?.trig_humidity_cmd) ||
        (settings?.trig_water && settings?.trig_water_cmd) || (settings?.trig_smoke && settings?.trig_smoke_cmd) || (settings?.trig_presence && settings?.trig_presence_cmd) || (settings?.trig_motion && settings?.trig_motion_cmd) ||
        (settings?.trig_contact && settings?.trig_contact_cmd) || (settings?.trig_power && settings?.trig_power_cmd) || (settings?.trig_illuminance && settings?.trig_illuminance_low && settings?.trig_illuminance_high)
    )
}

Boolean weatherTriggers() {
    return (settings?.trig_Weather || settings?.myWeather || settings?.myWeatherAlert)
}

Boolean triggersConfigured() {
    Boolean sched = scheduleTriggers()
    Boolean loc = locationTriggers()
    Boolean dev = deviceTriggers()
    Boolean sen = sensorTriggers()
    Boolean weath = weatherTriggers()
    // log.debug "sched: $sched | loc: $loc | dev: $dev | sen: $sen | weath: $weath"
    return (sched || loc || dev || sen || weath)
}

/******************************************************************************
    CONDITIONS SELECTION PAGE
******************************************************************************/

def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: "", nextPage: "mainPage", install: false, uninstall: false) {
        section() {
            paragraph pTS("Notice:\nAll selected conditions must pass before this action will execute", null, false, "#2784D9"), state: "complete"
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

        condNumValSect("battery", "battery", "Battery Level Conditions", "Batteries", "Level (%)", "battery", true)
    }
}

def condNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, String image) {
    section (sTS(sectStr)) {
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
    section (sTS(sectStr), hideable: hideable) {
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

private Map devsSupportVolume(devs) {
    List noSupport = []
    List supported = []
    if(devs instanceof List && devs?.size()) {
        devs?.each { dev->
            if(dev?.hasAttribute("permissions") && dev?.currentPermissions?.toString()?.contains("volumeControl")) {
                supported?.push(dev?.label)
            } else { noSupport?.push(dev?.label) }
        }
    }
    return [s:supported, n:noSupport]
}

def actionVariableDesc(actType, hideUserTxt=false) {
    Map txtItems = customTxtItems()
    if(!txtItems?.size() && state?.showSpeakEvtVars && !settings?."act_${actType}_txt") {
        String str = "NOTICE:\nYou can choose to leave the text field empty and generic text will be generated for each event type or define responses for each trigger under Step 2."
        paragraph pTS(str, getAppImg("info", true), false, "#2784D9"), required: true, state: "complete", image: getAppImg("info")
    }
    if(!hideUserTxt) {
        if(txtItems?.size()) {
            String str = "NOTICE: (Custom Text Defined)"
            txtItems?.each { i->
                i?.value?.each { i2-> str += "\n \u2022 ${i?.key?.toString()?.capitalize()} ${i2?.key?.toString()?.capitalize()}: (${i2?.value?.size()} Responses)" }
            }
            paragraph pTS(str, null, true, "#2784D9"), state: "complete"
            paragraph pTS("NOTICE:\nEntering text in the input below will override the user defined text for each trigger type under Step 2.", null, true, "red"), required: true, state: null
        }
    }
}

def triggerVariableDesc(inType, showRepInputs=false, itemCnt=0) {
    if(settings?.actionType in ["speak", "announcement"]) {
        String str = "Description:\nYou have 3 response options:\n"
        str += " \u2022 1. Leave the text empty below and text will be generated for each ${inType} trigger event.\n"
        str += " \u2022 2. Wait till Step 4 and define a single response for any trigger selected here.\n"
        str += " \u2022 3. Use the reponse builder below and create custom responses for each trigger type. (Supports randomization when multiple responses are configured)\n\n"
        str += "Custom Text is only used when Speech or Announcement action type is selected in Step 4."
        paragraph pTS(str, getAppImg("info", true), false, "#2784D9"), required: true, state: "complete", image: getAppImg("info")
        //Custom Text Options
        href url: parent?.getTextEditorPath(app?.id, "trig_${inType}_txt"), style: (isST() ? "embedded" : "external"), required: false, title: "Custom ${inType?.capitalize()} Responses\n(Optional)", state: (settings?."trig_${inType}_txt" ? "complete" : ''),
                description: settings?."trig_${inType}_txt" ?: "Open Response Designer...", image: getAppImg("text")
        if(showRepInputs) {
            if(settings?."trig_${inType}_after_repeat") {
                //Custom Repeat Text Options
                paragraph pTS("Description:\nAdd custom responses for the ${inType} events that are repeated.", getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info")
                href url: parent?.getTextEditorPath(app?.id, "trig_${inType}_after_repeat_txt"), style: (isST() ? "embedded" : "external"), title: inTS("Custom ${inType?.capitalize()} Repeat Responses\n(Optional)", getAppImg("text", true)),
                        description: settings?."trig_${inType}_after_repeat_txt" ?: "Open Response Designer...", state: (settings?."trig_${inType}_after_repeat_txt" ? "complete" : '') , submitOnChange: true, required: false, image: getAppImg("text")
            }
        }
    }
}

String actionTypeDesc() {
    Map descs = [
        speak: "Speak any message choose on your Echo Devices",
        announcement: "Plays a brief tone and speaks the message you define. If you select multiple devices it will be a synchronized broadcast.",
        sequence: "Sequences are a custom command where you can string different alexa actions which are sent to Amazon as a single command.  The command is then processed by amazon sequentially or in parallel.",
        weather: "Plays a very basic weather report.",
        playback: "Allows you to control the media playback state of your Echo devices.",
        builtin: "Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.",
        music: "Allows playback of various Songs/Radio using any connected music provider",
        calendar: "This will read out events in your calendar (Requires accounts to be configured in the alexa app. Must not have PIN.)",
        alarm: "This will allow you to create Alexa alarm clock notifications.",
        reminder: "This will allow you to create Alexa reminder notifications.",
        dnd: "This will allow you to enable/disable Do Not Disturb mode",
        alexaroutine: "This will allow you to run your configured Alexa Routines",
        wakeword: "This will allow you to change the Wake Word of your Echo devices.",
        bluetooth: "This will allow you to connect or disconnect bluetooth devices paired to your echo devices."
    ]
    return descs?.containsKey(settings?.actionType) ? descs[settings?.actionType] as String : "No Description Found..."
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", title: "", nextPage: "mainPage", install: false, uninstall: false) {
        Boolean done = false
        Map actionExecMap = [configured: false]
        if(settings?.actionType) {
            actionExecMap?.actionType = actionType
            actionExecMap?.config = [:]
            List devices = parent?.getDevicesFromList(settings?.act_EchoDevices)
            section(sTS("Action Type:")) {
                paragraph pTS("${settings?.actionType}", null, false, "#2678D9")
            }
            switch(actionType) {
                case "speak":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Type Config:"), hideable: true) {
                            actionVariableDesc(actionType)
                            href url: parent?.getTextEditorPath(app?.id, "act_speak_txt"), style: (isST() ? "embedded" : "external"), required: false, title: inTS("Defaul Action Reponse\n(Optional)", getAppImg("text", true)), state: (settings?."act_speak_txt" ? "complete" : ""),
                                    description: settings?."act_speak_txt" ?: "Open Response Designer...", image: getAppImg("text")
                        }
                        actionVolumeInputs(devices)
                        // log.debug "showSpeakEvtVars: ${state?.showSpeakEvtVars} | txt: ${settings?.act_speak_txt} | userDefinedTxt: ${hasUserDefinedTxt()}"
                        actionExecMap?.config?.speak = [text: settings?.act_speak_txt, evtText: ((state?.showSpeakEvtVars && !settings?.act_speak_txt) || hasUserDefinedTxt())]
                        if(state?.showSpeakEvtVars || act_speak_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "announcement":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("announce")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Type Config:")) {
                            actionVariableDesc(actionType)
                            href url: parent?.getTextEditorPath(app?.id, "act_announcement_txt"), style: (isST() ? "embedded" : "external"), required: false, title: inTS("Default Action Reponse\n(Optional)", getAppImg("text", true)), state: (settings?."act_announcement_txt" ? "complete" : ""),
                                    description: settings?."act_announcement_txt" ?: "Open Response Designer...", image: getAppImg("text")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap?.config?.announcement = [text: settings?.act_announcement_txt, evtText: ((state?.showSpeakEvtVars && !settings?.act_speak_txt) || hasUserDefinedTxt())]
                        if(settings?.act_EchoDevices?.size() > 1) {
                            List devObj = []
                            devices?.each { devObj?.push([deviceTypeId: it?.currentValue("deviceType"), deviceSerialNumber: it?.deviceNetworkId?.toString()?.tokenize("|")[2]]) }
                            // log.debug "devObj: $devObj"
                            actionExecMap?.config?.announcement?.deviceObjs = devObj
                        }
                        if(state?.showSpeakEvtVars || act_announcement_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sequence":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Sequence Options Legend:"), hideable: true, hidden: false) {
                            String str1 = "Sequence Options:"
                            seqItemsAvail()?.other?.sort()?.each { k, v->
                                str1 += "${bulletItem(str1, "${k}${v != null ? "::${v}" : ""}")}"
                            }
                            String str4 = "DoNotDisturb Options:"
                            seqItemsAvail()?.dnd?.sort()?.each { k, v->
                                str4 += "${bulletItem(str4, "${k}${v != null ? "::${v}" : ""}")}"
                            }
                            String str2 = "Music Options:"
                            seqItemsAvail()?.music?.sort()?.each { k, v->
                                str2 += "${bulletItem(str2, "${k}${v != null ? "::${v}" : ""}")}"
                            }
                            String str3 = "Canned TTS Options:"
                            seqItemsAvail()?.speech?.sort()?.each { k, v->
                                def newV = v
                                if(v instanceof List) { newV = ""; v?.sort()?.each { newV += "     ${dashItem(newV, "${it}", true)}"; } }
                                str3 += "${bulletItem(str3, "${k}${newV != null ? "::${newV}" : ""}")}"
                            }
                            paragraph str1, state: "complete"
                            // paragraph str4, state: "complete"
                            paragraph str2, state: "complete"
                            paragraph str3, state: "complete"
                            paragraph pTS("Enter the command in a format exactly like this:\nvolume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30\n\nEach command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.", null, false, , false, "violet"), state: "complete"
                        }
                        section(sTS("Action Type Config:")) {
                            input "act_sequence_txt", "text", title: inTS("Enter sequence text", getAppImg("text", true)), submitOnChange: true, required: false, image: getAppImg("text")
                        }
                        actionExecMap?.config?.sequence = [text: settings?.act_sequence_txt]
                        if(settings?.act_sequence_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "weather":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        actionVolumeInputs(devices)
                        done = true
                        actionExecMap?.config?.weather = [cmd: "playWeather"]
                    } else { done = false }
                    break

                case "playback":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        Map playbackOpts = [
                            "pause":"Pause", "stop":"Stop", "play":"Play", "nextTrack":"Next Track", "previousTrack":"Previous Track",
                            "mute":"Mute", "volume":"Volume"
                        ]
                        section(sTS("Playback Config:")) {
                            input "act_playback_cmd", "enum", title: inTS("Select Playback Action", getAppImg("command", true)), description: "", options: playbackOpts, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionExecMap?.config?.playback = [cmd: settings?.act_playback_cmd]
                        if(settings?.act_playback_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "builtin":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        Map builtinOpts = [
                            "playSingASong":"Sing a Song", "playFlashBrief":"Flash Briefing", "playFunFact": "Fun Fact", "playTraffic": "Traffic", "playJoke":"Joke",
                            "playTellStory":"Tell Story", "sayGoodbye": "Say Goodbye", "sayGoodNight": "Say Goodnight", "sayBirthday": "Happy Birthday",
                            "sayCompliment": "Give Compliment", "sayGoodMorning": "Good Morning", "sayWelcomeHome": "Welcome Home"
                        ]
                        section(sTS("BuiltIn Speech Config:")) {
                            input "act_builtin_cmd", "enum", title: inTS("Select Builtin Speech Type", getAppImg("command", true)), description: "", options: builtinOpts, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap?.config?.builtin = [cmd: settings?.act_builtin_cmd]
                        if(settings?.act_builtin_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "music":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        List musicProvs = devices[0]?.hasAttribute("supportedMusic") ? devices[0]?.currentValue("supportedMusic")?.split(",")?.collect { "${it?.toString()?.trim()}"} : []
                        logDebug("Music Providers: ${musicProvs}")
                        if(musicProvs) {
                            section(sTS("Music Providers:")) {
                                input "act_music_provider", "enum", title: inTS("Select Music Provider", getAppImg("music", true)), description: "", options: musicProvs, multiple: false, required: true, submitOnChange: true, image: getAppImg("music")
                            }
                            if(settings?.act_music_provider) {
                                if(settings?.act_music_provider == "TuneIn") {
                                    section(sTS("TuneIn Search Results:")) {
                                        paragraph "Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", state: "complete"
                                        input "tuneinSearchQuery", "text", title: inTS("Enter search phrase for TuneIn", getAppImg("tunein", true)), defaultValue: null, required: false, submitOnChange: true, image: getAppImg("tunein")
                                        if(settings?.tuneinSearchQuery) {
                                            href "searchTuneInResultsPage", title: inTS("View search results!", getAppImg("search", true)), description: "Tap to proceed...", image: getAppImg("search")
                                        }
                                    }
                                }
                                section(sTS("Action Type Config:")) {
                                    input "act_music_txt", "text", title: inTS("Enter Music Search text", getAppImg("text", true)), submitOnChange: true, required: false, image: getAppImg("text")
                                }
                                actionVolumeInputs(devices)
                            }
                        }
                        actionExecMap?.config?.music = [cmd: "searchMusic", provider: settings?.act_music_provider, search: settings?.act_music_txt]
                        if(settings?.act_music_provider && settings?.act_music_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "calendar":
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Type Config:")) {
                            input "act_calendar_cmd", "enum", title: inTS("Select Calendar Action", getAppImg("command", true)), description: "", options: ["playCalendarToday":"Today", "playCalendarTomorrow":"Tomorrow", "playCalendarNext":"Next Events"],
                                    required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap?.config?.calendar = [cmd: settings?.act_calendar_cmd]
                        if(act_calendar_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alarm":
                    //TODO: Offer to remove alarm after event.
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("alarms")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Type Config:")) {
                            input "act_alarm_label", "text", title: inTS("Alarm Label", getAppImg("name_tag", true)), submitOnChange: true, required: true, image: getAppImg("name_tag")
                            input "act_alarm_date", "text", title: inTS("Alarm Date\n(yyyy-mm-dd)", getAppImg("day_calendar", true)), submitOnChange: true, required: true, image: getAppImg("day_calendar")
                            input "act_alarm_time", "time", title: inTS("Alarm Time", getAppImg("clock", true)), submitOnChange: true, required: true, image: getPublicImg("clock")
                            // input "act_alarm_remove", "bool", title: "Remove Alarm when done", defaultValue: true, submitOnChange: true, required: false, image: getPublicImg("question")
                        }
                        actionVolumeInputs(devices, true)
                        actionExecMap?.config?.alarm = [cmd: "createAlarm", label: settings?.act_alarm_label, date: settings?.act_alarm_date, time: settings?.act_alarm_time, remove: settings?.act_alarm_remove]
                        if(act_alarm_label && act_alarm_date && act_alarm_time) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "reminder":
                    //TODO: Offer to remove reminder after event.
                    section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                    echoDevicesInputByPerm("reminders")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Type Config:")) {
                            input "act_reminder_label", "text", title: inTS("Reminder Label", getAppImg("name_tag", true)), submitOnChange: true, required: true, image: getAppImg("name_tag")
                            input "act_reminder_date", "text", title: inTS("Reminder Date\n(yyyy-mm-dd)", getAppImg("day_calendar", true)), submitOnChange: true, required: true, image: getAppImg("day_calendar")
                            input "act_reminder_time", "time", title: inTS("Reminder Time", getAppImg("clock", true)), submitOnChange: true, required: true, image: getPublicImg("clock")
                            // input "act_reminder_remove", "bool", title: "Remove Reminder when done", defaultValue: true, submitOnChange: true, required: false, image: getPublicImg("question")
                        }
                        actionVolumeInputs(devices, true)
                        actionExecMap?.config?.reminder = [cmd: "createReminder", label: settings?.act_reminder_label, date: settings?.act_reminder_date, time: settings?.act_reminder_time, remove: settings?.act_reminder_remove]
                        if(act_reminder_label && act_reminder_date && act_reminder_time) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "dnd":
                    echoDevicesInputByPerm("doNotDisturb")
                    if(settings?.act_EchoDevices) {
                        Map dndOpts = ["doNotDisturbOn":"Enable", "doNotDisturbOff":"Disable"]
                        section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                        section(sTS("Action Type Config:")) {
                            input "act_dnd_cmd", "enum", title: inTS("Select Do Not Disturb Action", getAppImg("command", true)), description: "", options: dndOpts, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionExecMap?.config?.dnd = [cmd: settings?.act_dnd_cmd]
                        if(settings?.act_dnd_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alexaroutine":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                        def routinesAvail = parent?.getAlexaRoutines(null, true) ?: [:]
                        logDebug("routinesAvail: $routinesAvail")
                        section(sTS("Action Type Config:")) {
                            input "act_alexaroutine_cmd", "enum", title: inTS("Select Alexa Routine", getAppImg("command", true)), description: "", options: routinesAvail, multiple: false, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionExecMap?.config?.alexaroutine = [cmd: "executeRoutineId", routineId: settings?.act_alexaroutine_cmd]
                        if(settings?.act_alexaroutine_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "wakeword":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings?.act_EchoDevices) {
                        Integer devsCnt = settings?.act_EchoDevices?.size() ?: 0
                        List devsObj = []
                        section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                        if(devsCnt >= 1) {
                            List wakeWords = devices[0]?.hasAttribute("wakeWords") ? devices[0]?.currentValue("wakeWords")?.replaceAll('"', "")?.split(",") : []
                            // logDebug("WakeWords: ${wakeWords}")
                            devices?.each { cDev->
                                section(sTS("${cDev?.getLabel()}:")) {
                                    if(wakeWords?.size()) {
                                        paragraph "Current Wake Word: ${cDev?.hasAttribute("alexaWakeWord") ? cDev?.currentValue("alexaWakeWord") : "Unknown"}"
                                        input "act_wakeword_device_${cDev?.id}", "enum", title: inTS("New Wake Word", getAppImg("list", true)), description: "", options: wakeWords, required: true, submitOnChange: true, image: getAppImg("list")
                                        devsObj?.push([device: cDev?.id, wakeword: settings?."act_wakeword_device_${cDev?.id}", cmd: "setWakeWord"])
                                    } else { paragraph "Oops...\nNo Wake Words have been found!  Please Remove the device from selection.", state: null, required: true }
                                }
                            }
                        }
                        actionExecMap?.config?.wakeword = [ devices: devsObj]
                        def aCnt = settings?.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }
                        // log.debug "aCnt: ${aCnt} | devsCnt: ${devsCnt}"
                        if(settings?.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }?.size() == devsCnt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "bluetooth":
                    echoDevicesInputByPerm("bluetoothControl")
                    if(settings?.act_EchoDevices) {
                        Integer devsCnt = settings?.act_EchoDevices?.size() ?: 0
                        List devsObj = []
                        section(sTS("Action Description:")) { paragraph pTS(actionTypeDesc(), getAppImg("info", true), false, "#2784D9"), state: "complete", image: getAppImg("info") }
                        if(devsCnt >= 1) {
                            devices?.each { cDev->
                                List btDevs = cDev?.hasAttribute("btDevicesPaired") ? cDev?.currentValue("btDevicesPaired")?.split(",") : []
                                // log.debug "btDevs: $btDevs"
                                section(sTS("${cDev?.getLabel()}:")) {
                                    if(btDevs?.size()) {
                                        input "act_bluetooth_device_${cDev?.id}", "enum", title: inTS("BT device to use", getAppImg("bluetooth", true)), description: "", options: btDevs, required: true, submitOnChange: true, image: getAppImg("bluetooth")
                                        input "act_bluetooth_action_${cDev?.id}", "enum", title: inTS("BT action to take", getAppImg("command", true)), description: "", options: ["connectBluetooth":"connect", "disconnectBluetooth":"disconnect"], required: true, submitOnChange: true, image: getAppImg("command")
                                        devsObj?.push([device: cDev?.id, btDevice: settings?."act_bluetooth_device_${cDev?.id}", cmd: settings?."act_bluetooth_action_${cDev?.id}"])
                                    } else { paragraph "Oops...\nNo Bluetooth devices are paired to this Echo Device!  Please Remove the device from selection.", state: null, required: true }
                                }
                            }
                        }
                        actionExecMap?.config?.bluetooth = [devices: devsObj]
                        if(settings?.findAll { it?.key?.startsWith("act_bluetooth_device_") && it?.value }?.size() == devsCnt &&
                            settings?.findAll { it?.key?.startsWith("act_bluetooth_action_") && it?.value }?.size() == devsCnt) { done = true } else { done = false }
                    } else { done = false }
                    break
            }
            if(done) {
                section(sTS("Delay Config:")) {
                    input "act_delay", "number", title: inTS("Delay Action in Seconds\n(Optional)", getAppImg("delay_time", true)), required: false, submitOnChange: true, image: getAppImg("delay_time")
                    paragraph "This does not work on Hubitat yet..."
                }
                section(sTS("Control Devices:")) {
                    input "act_switches_on", "capability.switch", title: inTS("Turn on these Switches\n(Optional)", getAppImg("switch", true)), multiple: true, required: false, submitOnChange: true, image: getAppImg("switch")
                    input "act_switches_off", "capability.switch", title: inTS("Turn off these Switches\n(Optional)", getAppImg("switch", true)), multiple: true, required: false, submitOnChange: true, image: getAppImg("switch")
                }
                actionSimulationSect()
                section("") {
                    paragraph pTS("You're all done with this step.  Press Done/Save", getAppImg("done", true)), state: "complete", image: getAppImg("done")
                }
                actionExecMap?.config?.volume = [change: settings?.act_volume_change, restore: settings?.act_volume_restore, alarm: settings?.act_alarm_volume]

                actionExecMap?.delay = settings?.act_delay
                actionExecMap?.configured = true
                updConfigStatusMap()
                //TODO: Add Cleanup of non selected inputs
            } else { actionExecMap = [configured: false] }
        }
        atomicState?.actionExecMap = (done && actionExecMap?.configured == true) ? actionExecMap : [configured: false]
        logDebug("actionExecMap: ${atomicState?.actionExecMap}")
    }
}

Boolean isActDevContConfigured() {
    return (settings?.act_switches_off || settings?.act_switches_on)
}

def actionSimulationSect() {
    section(sTS("Simulate Action")) {
        paragraph pTS("Toggle this to execute the action and see the results.\nWhen global text is not defined, this will generate a random event based on your trigger selections.", getAppImg("info", true), false, "#2784D9"), image: getAppImg("info")
        input "actTestRun", "bool", title: inTS("Test this action?", getAppImg("testing", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("testing")
        if(actTestRun) { executeActTest() }
    }
}

Boolean customMsgRequired() { return ((settings?.actionType in ["speak", "announcement"]) != true) }
Boolean customMsgConfigured() { return (settings?.notif_use_custom && settings?.notif_custom_message) }

def actNotifPage() {
    return dynamicPage(name: "actNotifPage", title: "Action Notifications", install: false, uninstall: false) {
        section (sTS("Message Customization:")) {
            if(customMsgRequired() && !settings?.notif_use_custom) { settingUpdate("notif_use_custom", "true", "bool") }
            paragraph pTS("When using speak and announcements you can leave this off and a notification will be sent with speech text.  For other action types a custom message is required", null, false, "gray")
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
                href "actNotifTimePage", title: inTS("Notification Restrictions", getAppImg("restriction", true)), description: (nsd ? "${nsd}\nTap to modify..." : "Tap to configure"), state: (nsd ? "complete" : null), image: getAppImg("restriction")
            }
            if(!state?.notif_message_tested) {
                def actDevices = settings?.notif_alexa_mobile ? parent?.getDevicesFromList(settings?.act_EchoDevices) : []
                def aMsgDev = actDevices?.size() && settings?.notif_alexa_mobile ? actDevices[0] : null
                if(sendNotifMsg("Info", "Action Notification Test Successful. Notifications Enabled for ${app?.getLabel()}", aMsgDev, true)) { state?.notif_message_tested = true }
            }
        } else { state?.notif_message_tested = false }
    }
}

def actNotifTimePage() {
    return dynamicPage(name:"actNotifTimePage", title: "", install: false, uninstall: false) {
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

def ssmlInfoSection() {
    String ssmlTestUrl = "https://topvoiceapps.com/ssml"
    String ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
    String ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
    String ssmlSpeechConsUrl = "https://developer.amazon.com/docs/custom-skills/speechcon-reference-interjections-english-us.html"
    section(sTS("SSML Documentation:"), hideable: true, hidden: true) {
        paragraph title: "What is SSML?", pTS("SSML allows for changes in tone, speed, voice, emphasis. As well as using MP3, and access to the Sound Library", null, false, "#2784D9"), state: "complete", image: getAppImg("info")
        href url: ssmlDocsUrl, style: "external", required: false, title: inTS("Amazon SSML Docs", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
        href url: ssmlSoundsUrl, style: "external", required: false, title: inTS("Amazon Sound Library", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
        href url: ssmlSpeechConsUrl, style: "external", required: false, title: inTS("Amazon SpeechCons", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
        href url: ssmlTestUrl, style: "external", required: false, title: inTS("SSML Designer and Tester", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
    }
}

def cleanupDevSettings(prefix) {
    List cDevs = settings?.act_EchoDevices
    List sets = settings?.findAll { it?.key?.startsWith(prefix) }?.collect { it?.key as String }
    // log.debug "cDevs: $cDevs | sets: $sets"
    List rem = []
    if(sets?.size()) {
        if(cDevs?.size()) {
            cDevs?.each {
                if(!sets?.contains("${prefix}${it}")) {
                    rem?.push("${prefix}${it}")
                }
            }
        } else { rem = rem + sets }
    }
    // log.debug "rem: $rem"
    // rem?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
}

Map customTxtItems() {
    Map items = [:]
    settings?.triggerEvents?.each { tr->
        if(settings?."trig_${tr}_txt") { if(!items[tr]) { items[tr] = [:]; }; items[tr]?.event = settings?."trig_${tr}_txt"?.toString()?.tokenize(";"); }
        if(settings?."trig_${tr}_after_repeat_txt") { if(!items[tr]) { items[tr] = [:]; };  items[tr]?.repeat = settings?."trig_${tr}_after_repeat_txt"?.toString()?.tokenize(";"); }
    }
    return items
}

Boolean hasUserDefinedTxt() {
    List items = []
    settings?.triggerEvents?.each {
        if(settings?."trig_${it}_txt") { return true }
        if(settings?."trig_${it}_after_repeat_txt") { return true }
    }
    return false
}

def updateActionExecMap(data) {
    // logTrace( "updateActionExecMap...")
    atomicState?.actionExecMap = (data && data?.configured == true) ? data : [configured: false]
    // log.debug "actionExecMap: ${state?.actionExecMap}"
}

Boolean executionConfigured() {
    // Boolean type = (settings?.actionType)
    Boolean opts = (state?.actionExecMap && state?.actionExecMap?.configured == true)
    Boolean devs = (settings?.act_EchoDevices)
    // log.debug "Options: $opts | devs: $devs"
    return (opts || devs)
}

private echoDevicesInputByPerm(type) {
    List echoDevs = parent?.getChildDevicesByCap(type as String)
    section(sTS("Alexa Devices:")) {
        if(echoDevs?.size()) {
            def eDevsMap = echoDevs?.collectEntries { [(it?.getId()): [label: it?.getLabel(), lsd: (it?.currentWasLastSpokenToDevice?.toString() == "true")]] }?.sort { a,b -> b?.value?.lsd <=> a?.value?.lsd ?: a?.value?.label <=> b?.value?.label }
            input "act_EchoDevices", "enum", title: inTS("Echo Speaks Device(s) to Use", getAppImg("echo_gen1", true)), description: "Select the devices", options: eDevsMap?.collectEntries { [(it?.key): "${it?.value?.label}${(it?.value?.lsd == true) ? "\n(Last Spoken To)" : ""}"] }, multiple: true, required: true, submitOnChange: true, image: getAppImg("echo_gen1")
        } else { paragraph "No devices were found with support for ($type)"}
    }
}

private actionVolumeInputs(devices, showAlrmVol=false) {
    if(showAlrmVol) {
        section(sTS("Volume Options:")) {
            input "act_alarm_volume", "number", title: inTS("Alarm Volume\n(Optional)", getAppImg("speed_knob", true)), range: "0..100", required: false, submitOnChange: true, image: getAppImg("speed_knob")
        }
    } else {
        if(devices && settings?.actionType in ["speak", "announcement", "weather", "builtin", "music", "calendar"]) {
            Map volMap = devsSupportVolume(devices)
            section(sTS("Volume Options:")) {
                if(volMap?.n?.size() > 0 && volMap?.n?.size() < devices?.size()) { paragraph "Some of the selected devices do not support volume control" }
                else if(devices?.size() == volMap?.n?.size()) { paragraph "Some of the selected devices do not support volume control"; return; }
                input "act_volume_change", "number", title: inTS("Volume Level\n(Optional)", getAppImg("speed_knob", true)), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true, image: getAppImg("speed_knob")
                input "act_volume_restore", "number", title: inTS("Restore Volume\n(Optional)", getAppImg("speed_knob", true)), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true, image: getAppImg("speed_knob")
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

def uninstallPage() {
    return dynamicPage(name: "uninstallPage", title: "Uninstall", install: false , uninstall: true) {
        section("") { paragraph "This will uninstall the App and All Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove." }
        if(isST()) { remove("Remove ${app?.label} and Devices!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis App and Devices will be removed") }
    }
}

Boolean wordInString(String findStr, String fullStr) {
    List parts = fullStr?.split(" ")?.collect { it?.toString()?.toLowerCase() }
    return (findStr in parts)
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    unschedule()
    state?.dupPendingSetup = false
    initialize()
}

def initialize() {
    if(state?.dupPendingSetup == false && settings?.duplicateFlag == true) {
        settingUpdate("duplicateFlag", "false", "bool")
    } else if(settings?.duplicateFlag == true && state?.dupPendingSetup != false) {
        String newLbl = app?.getLabel() + app?.getLabel()?.toString()?.contains("(Dup)") ? "" : " (Dup)"
        app?.updateLabel(newLbl)
        state?.dupPendingSetup = true
        def dupState = parent?.getDupActionStateData()
        if(dupState?.size()) {
            dupState?.each {k,v-> state[k] = v }
            parent?.clearDuplicationItems()
        }
        logInfo("Duplicated Action has been created... Please open action and configure to complete setup...")
        return
    }
    unsubscribe()
    state?.isInstalled = true
    updAppLabel()
    runIn(3, "actionCleanup")
    runIn(7, "subscribeToEvts")
    updConfigStatusMap()
}

private updAppLabel() {
    String newLbl = "${settings?.appLbl}${isPaused() ? " | (Paused)" : ""}"
    newLbl = newLbl?.replaceAll(/(Dup)/, "").replaceAll("\\s"," ")
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
    sMap?.triggers = triggersConfigured()
    sMap?.conditions = conditionsConfigured()
    sMap?.actions = executionConfigured()
    atomicState?.configStatusMap = sMap
}

private getConfStatusItem(item) {
    return (state?.configStatusMap?.containsKey(item) && state?.configStatusMap[item] == true)
}

private actionCleanup() {
    // State Cleanup
    List items = ["afterEvtMap", "afterEvtChkSchedMap", "afterCheckActiveScheduleId", "afterEvtChkSchedId"]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    //Cleans up unused action setting items
    List setItems = ["act_set_volume", "act_restore_volume"]
    List setIgn = ["act_delay", "act_volume_change", "act_volume_restore", "act_EchoDevices"]
    if(settings?.actionType) { settings?.each { si-> if(si?.key?.startsWith("act_") && !si?.key?.startsWith("act_${settings?.actionType}") && !(si?.key in setIgn)) { setItems?.push(si?.key as String) } } }
    // if(settings?.actionType in ["bluetooth", "wakeword"]) { cleanupDevSettings("act_${settings?.actionType}_device_") }
    // TODO: Cleanup unselected trigger types
    settings?.each { si-> if(si?.key?.startsWith("broadcast") || si?.key?.startsWith("musicTest") || si?.key?.startsWith("announce") || si?.key?.startsWith("sequence") || si?.key?.startsWith("speechTest")) { setItems?.push(si?.key as String) } }
    // Performs the Setting Removal
    setItems = setItems +  ["tuneinSearchQuery", "performBroadcast", "performMusicTest", "usePush", "smsNumbers", "pushoverSound", "pushoverDevices", "pushoverEnabled", "pushoverPriority", "alexaMobileMsg", "appDebug"]
    setItems?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
}

public triggerInitialize() {
    runIn(3, "initialize")
}

public updatePauseState(Boolean pause) {
    if(settings?.actionPause != pause) {
        logDebug("Received Request to Update Pause State to (${pause})")
        settingUpdate("actionPause", "${pause}", "bool")
        runIn(4, "updated")
    }
}

Boolean isPaused() {
    return (settings?.actionPause == true)
}

private valTrigEvt(key) {
    return (key in settings?.triggerEvents)
}

def scheduleTrigEvt() {
    Map dateMap = getDateMap()
    Map sTrigMap = atomicState?.schedTrigMap ?: [:]
    String recur = settings?.trig_scheduled_recurrence
    def days = settings?.trig_scheduled_days
    def weeks = settings?.trig_scheduled_weeks
    def months = settings?.trig_scheduled_months
    Boolean dOk = settings?.trig_scheduled_days ? (isDayOfWeek(days)) : true
    Boolean wOk = (recur && weeks && recur in ["Weekly"]) ? (dateMap?.wm in weeks && sTrigMap?.lastRun?.wm != dateMap?.wm) : true
    Boolean mOk = (recur && months && recur in ["Weekly", "Monthly"]) ? (dateMap?.m in months && sTrigMap?.lastRun?.m != dateMap?.m) : true
    // Boolean yOk = (recur && recur in ["Yearly"]) ? (sTrigMap?.lastRun?.y != dateMap?.y) : true
    logDebug("scheduleTrigEvt | dayOk: $dOk | weekOk: $wOk | monthOk: $mOk")
    if(dOk && wOk && mOk) {
        sTripMap?.lastRun = dateMap
        atomicState?.schedTrigMap = sTrigMap
        Date dt = new Date()
        executeAction([name: "Schedule", displayName: "Scheduled Trigger", value: time2Str(dt?.toString()), date: dt, deviceId: null], false, "scheduleTrigEvt", false, false)
    }
}

private subscribeToEvts() {
    if(checkMinVersion()) { logError("CODE UPDATE required to RESUME operation.  No events will be monitored.", true); return; }
    if(isPaused()) { logWarn("Action is PAUSED... No Events will be subscribed to or scheduled....", true); return; }
    //SCHEDULING
    if (valTrigEvt("scheduled") && (settings?.trig_scheduled_time || settings?.trig_scheduled_sunState)) {
        if(settings?.trig_scheduled_sunState) {
            if (settings?.trig_scheduled_sunState == "Sunset") { subscribe(location, "sunsetTime", sunsetTimeHandler) }
            if (settings?.trig_scheduled_sunState == "Sunrise") { subscribe(location, "sunriseTime", sunriseTimeHandler) }
        }
        if(settings?.trig_scheduled_recurrence) {
            if(settings?.trig_scheduled_recurrence == "Once") {
                runOnce(settings?.trig_scheduled_time, "scheduleTrigEvt")
            } else {
                schedule(settings?.trig_scheduled_time, "scheduleTrigEvt")
            }
        }
    }

    // Location Alarm Events
    if(valTrigEvt("alarm")) {
        if(settings?.trig_alarm) { subscribe(location, !isST() ? "hsmStatus" : "alarmSystemStatus", alarmEvtHandler) }
        if(!isST() && settings?.trig_alarm == "Alerts") { subscribe(location, "hsmAlert", alarmEvtHandler) } // Only on Hubitat
    }

    state?.handleGuardEvents = valTrigEvt("guard")

    // Location Mode Events
    if(valTrigEvt("mode") && settings?.trig_mode) { subscribe(location, "mode", modeEvtHandler) }

    // Routines (ST Only)
    if(valTrigEvt("routineExecuted") && settings?.trig_routineExecuted) { subscribe(location, "routineExecuted", routineEvtHandler) }

    // Scene (Hubitat Only)
    if(valTrigEvt("scene") && settings?.trig_scene) { subscribe(settings?.trig_scene, "switch", sceneEvtHandler) }

    // ENVIRONMENTAL Sensors
    if(valTrigEvt("presence") && settings?.trig_presence) { subscribe(trig_presence, "presence", getDevEvtHandlerName("presence")) }

    // Motion Sensors
    if(valTrigEvt("motion") && settings?.trig_motion) { subscribe(trig_motion, "motion", getDevEvtHandlerName("motion")) }

    // Water Sensors
    if(valTrigEvt("water") && settings?.trig_water) { subscribe(settings?.trig_water, "water", getDevEvtHandlerName("water")) }

    // Humidity Sensors
    if(valTrigEvt("humidity") && settings?.trig_humidity) { subscribe(settings?.trig_humidity, "humidity", deviceEvtHandler) }

    // Temperature Sensors
    if(valTrigEvt("temperature") && settings?.trig_temperature) { subscribe(settings?.trig_temperature, "temperature", deviceEvtHandler) }

    // Illuminance Sensors
    if(valTrigEvt("illuminance") && settings?.trig_illuminance) { subscribe(settings?.trig_illuminance, "illuminance", deviceEvtHandler) }

    // Power Meters
    if(valTrigEvt("power") && settings?.trig_power) { subscribe(trig_power, "power", deviceEvtHandler) }

    // Locks
    if(valTrigEvt("lock") && settings?.trig_lock) { subscribe(settings?.trig_lock, "lock", getDevEvtHandlerName("lock")) }

    // Window Shades
    if(valTrigEvt("shade") && settings?.trig_shade) { subscribe(settings?.trig_shade, "windowShade", getDevEvtHandlerName("shade")) }

    // Valves
    if(valTrigEvt("valve") && settings?.trig_valve) { subscribe(settings?.trig_valve, "valve", getDevEvtHandlerName("valve")) }

    // Smoke/CO2
    if(valTrigEvt("carbon") || valTrigEvt("smoke")) {
        if(settings?.trig_carbonMonoxide)   { subscribe(settings?.trig_carbonMonoxide, "carbonMonoxide", deviceEvtHandler) }
        if(settings?.trig_smoke)            { subscribe(settings?.trig_smoke, "smoke", deviceEvtHandler) }
    }

    // Garage Door Openers
    if(valTrigEvt("door") && settings?.trig_door) { subscribe(settings?.trig_door, "garageDoorControl", getDevEvtHandlerName("door")) }

    //Keypads
    if(valTrigEvt("keypad") && settings?.trig_Keypads) { subscribe(settings?.trig_Keypads, "codeEntered", deviceEvtHandler) }

    //Contact Sensors
    if (valTrigEvt("contact")) {
        if(settings?.trig_contact) { subscribe(settings?.trig_contact, "contact", getDevEvtHandlerName("contact")) }
    }

    // Outlets, Switches
    if (valTrigEvt("switch")) {
        if(settings?.trig_switch) { subscribe(trig_switch, "switch", getDevEvtHandlerName("switch")) }
    }

    // Batteries
    if (valTrigEvt("battery")) {
        if(settings?.trig_battery)    { subscribe(settings?.trig_battery, "battery", deviceEvtHandler) }
    }

    // Dimmers/Level
    if (valTrigEvt("level")) {
        if(settings?.trig_level)    { subscribe(settings?.trig_level, "level", deviceEvtHandler) }
    }

    // Thermostats
    if (valTrigEvt("thermostat")) {
        if(settings?.trig_thermostat) { subscribe(settings?.trig_thermostat, "thermostat", deviceEvtHandler) }
    }

    // Weather
    if (valTrigEvt("weather")) {
        if(settings?.trig_weather && settings?.trig_weather_cmd) { runEvery1Minute("weatherCheckHandler") }
    }

    // settings?.triggerEvents?.each {
    //     if(settings?."trig_${it}_after") {
    //         runEvery1Minute("afterEvtCheckWatcher")
    //         return
    //     }
    // }
}

private attributeConvert(String attr) {
    Map atts = ["door":"garageDoorControl", "carbon":"carbonMonoxide", "shade":"windowShade"]
    return (atts?.containsKey(attr)) ? atts[attr] : attr
}

private getDevEvtHandlerName(String type) {
    return (type && settings?."trig_${type}_after") ? "devAfterEvtHandler" : "deviceEvtHandler"
}


/***********************************************************************************************************
    EVENT HANDLER FUNCTIONS
************************************************************************************************************/
def sunriseTimeHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    executeAction(evt, false, "sunriseTimeHandler", false, false)
}

def sunsetTimeHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    executeAction(evt, false, "sunsetTimeHandler", false, false)
}

def alarmEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    Boolean useAlerts = (settings?.trig_alarm == "Alerts")
    switch(evt?.name) {
        case "hsmStatus":
        case "alarmSystemStatus":
            // def inc = (isST() && useAlerts) ? getShmIncidents() : null
            executeAction(evt, false, "alarmEvtHandler", false, false)
            break
        case "hsmAlert":
            executeAction(evt, false, "alarmEvtHandler", false, false)
            break
    }
}

Integer getLastAfterEvtCheck() { return !state?.lastAfterEvtCheck ? 10000000 : GetTimeDiffSeconds(state?.lastAfterEvtCheck, "getLastAfterEvtCheck").toInteger() }

def afterEvtCheckWatcher() {
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    Map aSchedMap = atomicState?.afterEvtChkSchedMap ?: null
    if((aEvtMap?.size() == 0 && aSchedMap && aSchedMap?.id) || (aEvtMap?.size() && getLastAfterEvtCheck() > 240)) {
        runIn(2, "afterEvtCheckHandler")
    }
}

def devAfterEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    Boolean ok = true
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    Boolean aftWatSched = state?.afterEvtCheckWatcherSched ?: false
    def evtDt = parseDate(evt?.date?.toString())
    String dc = settings?."trig_${evt?.name}_cmd" ?: null
    Integer dcaf = settings?."trig_${evt?.name}_after" ?: null
    Integer dcafr = settings?."trig_${evt?.name}_after_repeat" ?: null
    String eid = "${evt?.deviceId}_${evt?.name}"
    Boolean schedChk = (dc && dcaf && evt?.value == dc)
    logTrace( "Device Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms | SchedCheck: (${schedChk})")
    if(aEvtMap?.containsKey(eid)) {
        if(dcaf && !schedChk) {
            aEvtMap?.remove(eid)
            log.warn "Removing ${evt?.displayName} from AfterEvtCheckMap | Reason: (${evt?.name?.toUpperCase()}) no longer has the state of (${dc}) | Remaining Items: (${aEvtMap?.size()})"
        }
    }
    ok = schedChk
    if(ok) { aEvtMap["${evt?.deviceId}_${evt?.name}"] =
        [ dt: evt?.date?.toString(), deviceId: evt?.deviceId as String, displayName: evt?.displayName, name: evt?.name, value: evt?.value, triggerState: dc, wait: dcaf ?: null, isRepeat: false, repeatWait: dcafr ?: null ]
    }
    atomicState?.afterEvtMap = aEvtMap
    if(ok) {
        runIn(2, "afterEvtCheckHandler")
        if(!aftWatSched) {
            state?.afterEvtCheckWatcherSched = true
            runEvery5Minutes("afterEvtCheckWatcher")
        }
    }
}

def afterEvtCheckHandler() {
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    if(aEvtMap?.size()) {
        // Collects all of the evt items and stores there wait values as a list
        Integer timeLeft = null
        Integer lowWait = aEvtMap?.findAll { it?.value?.wait != null }?.collect { it?.value?.wait }?.min()
        Integer lowLeft = aEvtMap?.findAll { it?.value?.wait == lowWait }?.collect { it?.value?.timeLeft} ?.min()
        def nextItem = aEvtMap?.find { it?.value?.wait == lowWait && it?.value?.timeLeft == lowLeft }
        def nextVal = nextItem?.value ?: null
        def nextId = (nextVal?.deviceId && nextVal?.name) ? "${nextVal?.deviceId}_${nextVal?.name}" : null
        // log.debug "nextVal: $nextVal"
        if(nextVal) {
            def prevDt = nextVal?.repeat && nextVal?.repeatDt ? parseDate(nextVal?.repeatDt?.toString()) : parseDate(nextVal?.dt?.toString())
            def fullDt = parseDate(nextVal?.dt?.toString())
            def devs = settings?."trig_${nextVal?.name}" ?: null
            Boolean isRepeat = nextVal?.isRepeat ?: false
            Boolean hasRepeat = (settings?."trig_${nextVal?.name}_after_repeat" != null)
            if(prevDt) {
                def timeNow = new Date()?.getTime()
                Integer evtElap = (int) ((long)(timeNow - prevDt?.getTime())/1000)
                Integer fullElap = (int) ((long)(timeNow - fullDt?.getTime())/1000)
                Integer reqDur = (nextVal?.isRepeat && nextVal?.repeatWait) ? nextVal?.repeatWait : nextVal?.wait ?: null
                timeLeft = (reqDur - evtElap)
                aEvtMap[nextItem?.key]?.timeLeft = timeLeft
                // log.warn "After Debug | TimeLeft: ${timeLeft} | LastCheck: ${evtElap} | EvtDuration: ${fullElap} | RequiredDur: ${reqDur} | AfterWait: ${nextVal?.wait} | RepeatWait: ${nextVal?.repeatWait} | isRepeat: ${nextVal?.isRepeat}"
                if(timeLeft <= 4 && nextVal?.deviceId && nextVal?.name) {
                    timeLeft = reqDur
                    Boolean skipEvt = (nextVal?.triggerState && nextVal?.deviceId && nextVal?.name && devs) ? !devCapValEqual(devs, nextVal?.deviceId as String, nextVal?.name, nextVal?.triggerState) : true
                    aEvtMap[nextItem?.key]?.timeLeft = timeLeft
                    if(!skipEvt) {
                        if(hasRepeat) {
                            log.warn "Last Repeat ${nextVal?.displayName?.toString()?.capitalize()} (${nextVal?.name}) Event | TimeLeft: ${timeLeft} | LastCheck: ${evtElap} | EvtDuration: ${fullElap} | Required: ${reqDur}"
                            aEvtMap[nextItem?.key]?.repeatDt = formatDt(new Date())
                            aEvtMap[nextItem?.key]?.isRepeat = true
                            deviceEvtHandler([date: parseDate(nextVal?.repeatDt?.toString()), deviceId: nextVal?.deviceId as String, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value, totalDur: fullElap], true, isRepeat)
                        } else {
                            aEvtMap?.remove(nextId)
                            log.warn "Wait Threshold (${reqDur} sec) Reached for ${nextVal?.displayName} (${nextVal?.name?.toString()?.capitalize()}) | TriggerState: (${nextVal?.triggerState}) | EvtDuration: ${fullElap}"
                            deviceEvtHandler([date: parseDate(nextVal?.dt?.toString()), deviceId: nextVal?.deviceId as String, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value], true)
                        }
                    } else {
                        aEvtMap?.remove(nextId)
                        logInfo("${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) state is already ${nextVal?.triggerState} | Skipping Actions...")
                    }
                }
            }
        }
        // log.debug "nextId: $nextId | timeLeft: ${timeLeft}"
        runIn(2, "scheduleAfterCheck", [data: [val: timeLeft, id: nextId, repeat: isRepeat]])
        atomicState?.afterEvtMap = aEvtMap
        // logTrace( "afterEvtCheckHandler Remaining Items: (${aEvtMap?.size()})")
    } else { clearAfterCheckSchedule() }
    state?.lastAfterEvtCheck = getDtNow()
}

def deviceEvtHandler(evt, aftEvt=false, aftRepEvt=false) {
    def evtDelay = now() - evt?.date?.getTime()
    Boolean evtOk = false
    Boolean evtAd = false
    List d = settings?."trig_${evt?.name}"
    String dc = settings?."trig_${evt?.name}_cmd"
    Boolean dca = (settings?."trig_${evt?.name}_all" == true)
    Boolean dcavg = (!dca && settings?."trig_${evt?.name}_avg" == true)
    Boolean dco = (!settings?."trig_${evt?.name}_after" && settings?."trig_${evt?.name}_once" == true)
    Integer dcw = (!settings?."trig_${evt?.name}_after" && settings?."trig_${evt?.name}_wait") ? settings?."trig_${evt?.name}_wait" : null
    logTrace( "Device Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms${aftEvt ? " | (AfterEvt)" : ""}")
    Boolean devEvtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)
    switch(evt?.name) {
        case "switch":
        case "lock":
        case "door":
        case "smoke":
        case "carbonMonoxide":
        case "windowShade":
        case "presence":
        case "contact":
        case "motion":
        case "water":
        case "valve":
            if(d?.size() && dc) {
                if(dc == "any") { evtOk = true; }
                else {
                    if(dca && (allDevCapValsEqual(d, dc, evt?.value))) { evtOk = true; evtAd = true; }
                    else if(evt?.value == dc) { evtOk=true }
                }
            }
            break
        case "humidity":
        case "temperature":
        case "power":
        case "illuminance":
        case "level":
        case "battery":
            Double dcl = settings?."trig_${evt?.name}_low"
            Double dch = settings?."trig_${evt?.name}_high"
            Double dce = settings?."trig_${evt?.name}_equal"
            Map valChk = deviceEvtProcNumValue(evt, d, dc, dcl, dch, dce, dca, dcavg)
            evtOk = valChk?.evtOk
            evtAd = valChk?.evtAd
            break
    }
    if(evtOk && devEvtWaitOk) {
        executeAction(evt, false, "deviceEvtHandler(${evt?.name})", aftRepEvt, evtAd)
    }
}

Map deviceEvtProcNumValue(evt, List devs = null, String cmd = null, Double dcl = null, Double dch = null, Double dce = null, Boolean dca = false, Boolean dcavg=false) {
    Boolean evtOk = false
    Boolean evtAd = false
    // log.debug "deviceEvtProcNumValue | cmd: ${cmd} | low: ${dcl} | high: ${dch} | equal: ${dce} | all: ${dca}"
    if(devs?.size() && cmd && evt?.value?.isNumber()) {
        Double evtValue = (dcavg ? getDevValueAvg(devs, evt?.name) : evt?.value) as Double
        switch(cmd) {
            case "equals":
                if(!dca && dce && dce?.toDouble() == evtValue) {
                    evtOk=true
                } else if(dca && dce && allDevCapNumValsEqual(devs, evt?.name, dce)) { evtOk=true; evtAd=true; }
                break
            case "between":
                if(!dca && dcl && dch && (evtValue in (dcl..dch))) {
                    evtOk=true
                } else if(dca && dcl && dch && allDevCapNumValsBetween(devs, evt?.name, dcl, dch)) { evtOk=true; evtAd=true; }
                break
            case "above":
                if(!dca && dch && (evtValue > dch)) {
                    evtOk=true
                } else if(dca && dch && allDevCapNumValsAbove(devs, evt?.name, dch)) { evtOk=true; evtAd=true; }
                break
            case "below":
                if(dcl && (evtValue < dcl)) {
                    evtOk=true
                } else if(dca && dcl && allDevCapNumValsBelow(devs, evt?.name, dcl)) { evtOk=true; evtAd=true; }
                break
        }
    }
    return [evtOk: evtOk, evtAd: evtAd]
}

def thermostatEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    Boolean evtOk = false
    Boolean evtAd = false
    List d = settings?."trig_${evt?.name}"
    String dc = settings?."trig_${evt?.name}_cmd"
    Boolean dca = (settings?."trig_${evt?.name}_all" == true)
    Boolean dco = (!settings?."trig_${evt?.name}_after" && settings?."trig_${evt?.name}_once" == true)
    Integer dcw = (!settings?."trig_${evt?.name}_after" && settings?."trig_${evt?.name}_wait") ? settings?."trig_${evt?.name}_wait" : null
    logTrace( "Thermostat Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    Boolean devEvtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)

    if(d?.size() && dc) {
        switch(dc) {
            case "setpoint":
                switch(evt?.name) {
                    case "coolSetpoint":
                    case "heatSetpoint":
                        if(dsc == "any" || (dsc == "cooling" && evt?.name == "coolSetpoint") || (dsc == "heating" && evt?.name == "heatSetpoint")) {
                            String dsc = settings?.trig_thermostat_setpoint_cmd ?: null
                            Double dcl = settings?.trig_thermostat_setpoint_low
                            Double dch = settings?.trig_thermostat_setpoint_high
                            Double dce = settings?.trig_thermostat_setpoint_equal
                            Map valChk = deviceEvtProcNumValue(evt, d, dsc, dcl, dch, dce, null)
                            evtOk = valChk?.evtOk
                            evtAd = valChk?.evtAd
                        }
                        break
                }
                break

            case "ambient":
                if(evt?.name == "temperature") {
                    String dac = settings?.trig_thermostat_ambient_cmd ?: null
                    Double dcl = settings?.trig_thermostat_ambient_low
                    Double dch = settings?.trig_thermostat_ambient_high
                    Double dce = settings?.trig_thermostat_ambient_equal
                    Map valChk = deviceEvtProcNumValue(evt, d, dac, dcl, dch, dce, null)
                    evtOk = valChk?.evtOk
                    evtAd = valChk?.evtAd
                }
                break

            case "mode":
            case "operatingstate":
            case "fanmode":
                if(evt?.name == "thermostatMode") {
                    String dmc = settings?.trig_thermostat_fanmode_cmd ?: null
                    if(dmc == "any" || evt?.value == dmc) { evtOk=true }
                }

                if(evt?.name == "thermostatOperatingState") {
                    String doc = settings?.trig_thermostat_state_cmd ?: null
                    if(doc == "any" || evt?.value == doc) { evtOk=true }
                }

                if(evt?.name == "thermostatFanMode") {
                    String dfc = settings?.trig_thermostat_mode_cmd ?: null
                    if(dfc == "any" || evt?.value == dfc) { evtOk=true }
                }
                break
        }
    }
    if(evtOk && devEvtWaitOk) {
        executeAction(evt, false, "thermostatEvtHandler(${evt?.name})", false, evtAd)
    }
}

String evtValueCleanup(val) {
    // log.debug "val(in): ${val}"
    val = (val?.isNumber() && val?.toString()?.endsWith(".0")) ? val?.toDouble()?.round(0) : val
    // log.debug "val(out): ${val}"
    return val
}

private clearEvtHistory() {
    settingUpdate("clrEvtHistory", "false", "bool")
    atomicState?.valEvtHistory = null
}

Boolean evtWaitRestrictionOk(evt, Boolean once, Integer wait) {
    Boolean ok = true
    Map evtHistMap = atomicState?.valEvtHistory ?: [:]
    def evtDt = parseDate(evt?.date?.toString())
    // TODO: Look into moving away from the histMap and use statesSince()
    // def evtDev = settings?."trig_${evt?.name}"?.find { it?.id == evt?.deviceId }
    // List sts = evtDev.statesBetween(evt?.name, new Date() - 1, new Date())
    // sts?.each {
    //     log.debug "${it?.date} | ${it?.name} | ${it?.value}"
    // }

    // log.debug "prevDt: ${evtHistMap["${evt?.deviceId}_${evt?.name}"]?.date ? parseDate(evtHistMap["${evt?.deviceId}_${evt?.name}"]?.dt as String) : null} | evtDt: ${evtDt}"
    if(evtHistMap?.containsKey("${evt?.deviceId}_${evt?.name}") && evtHistMap["${evt?.deviceId}_${evt?.name}"]?.dt) {
        // log.debug "prevDt: ${evtHistMap["${evt?.deviceId}_${evt?.name}"]?.dt as String}"
        def prevDt = parseDate(evtHistMap["${evt?.deviceId}_${evt?.name}"]?.dt?.toString())
        if(prevDt && evtDt) {
            def dur = (int) ((long)(evtDt?.getTime() - prevDt?.getTime())/1000)
            def waitOk = ( (wait && dur) && (wait < dur));
            def dayOk = !once || (once && !isDateToday(prevDt))
            logDebug("Last ${evt?.name?.toString()?.capitalize()} Event for Device Occurred: (${dur} sec ago) | Desired Wait: (${wait} sec) - Status: (${waitOk ? "OK" : "Block"}) | OnceDaily: (${once}) - Status: (${dayOk ? "OK" : "Block"})")
            ok = (waitOk && dayOk)
        }
    }
    if(ok) { evtHistMap["${evt?.deviceId}_${evt?.name}"] = [dt: evt?.date?.toString(), value: evt?.value, name: evt?.name as String] }
    // log.debug "evtWaitRestrictionOk: $ok"
    atomicState?.valEvtHistory = evtHistMap
    return ok
}

String getAttrPostfix(attr) {
    switch(attr) {
        case "humidity":
        case "level":
        case "battery":
            return " percent"
        case "temperature":
            return " degrees"
        case "illuminance":
            return " lux"
        case "power":
            return " watts"
        default:
            return ""
    }
}

def routineEvtHandler(evt) {
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    executeAction(evt, false, "routineEvtHandler", false, false)
}

def sceneEvtHandler(evt) {
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    executeAction(evt, false, "sceneEvtHandler", false, false)
}

def modeEvtHandler(evt) {
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    executeAction(evt, false, "modeEvtHandler", false, false)
}

def locationEvtHandler(evt) {
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    executeAction(evt, false, "locationEvtHandler", false, false)
}

def weatherCheckHandler() {
    // def alerts = getTwcAlerts()
    // log.debug "alerts: ${alerts}"

    // def alerts = getTwcAlerts()
    // log.debug "alerts: ${alerts}"

    // executeAction(evt, false, "locationEvtHandler", false, false)
}

def scheduleAfterCheck(data) {
    Integer val = data?.val ? (data?.val < 2 ? 2 : data?.val) : 60
    String id = data?.id ?: null
    Boolean rep = (data?.repeat == true)
    Map aSchedMap = atomicState?.afterEvtChkSchedMap ?: null
    if(aSchedMap && aSchedMap?.id && id && aSchedMap?.id == id) {
        // log.debug "Active Schedule Id (${aSchedMap?.id}) is the same as the requested schedule ${id}."
    }
    runIn(val, "afterEvtCheckHandler")
    atomicState?.afterEvtChkSchedMap = [id: id, dur: val, dt: getDtNow()]
    logDebug("Schedule After Event Check${rep ? " (Repeat)" : ""} for (${val} seconds) | Id: ${id}")
}

private clearAfterCheckSchedule() {
    unschedule("afterEvtCheckHandler")
    logDebug("Clearing After Event Check Schedule...")
    atomicState?.afterEvtChkSchedMap = null
    if(state?.afterEvtCheckWatcherSched) {
        state?.afterEvtCheckWatcherSched = false
        unschedule("afterEvtCheckWatcher")
    }
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
    Boolean tempDevOk = checkDeviceNumCondOk("temperature")
    Boolean humDevOk = checkDeviceNumCondOk("humidity")
    Boolean illDevOk = checkDeviceNumCondOk("illuminance")
    Boolean battDevOk = checkDeviceNumCondOk("battery")
    logDebug("checkDeviceCondOk | switchOk: $swDevOk | motionOk: $motDevOk | presenceOk: $presDevOk | contactOk: $conDevOk | lockOk: $lockDevOk | garageOk: $garDevOk")
    return (swDevOk && motDevOk && presDevOk && conDevOk && lockDevOk && garDevOk && tempDevOk && humDevOk && battDevOk && illDevOk)
}

def allConditionsOk() {
    def timeOk = timeCondOk()
    def dateOk = dateCondOk()
    def locOk = locationCondOk()
    def devOk = deviceCondOk()
    logDebug("Action Conditions Check | Time: ($timeOk) | Date: ($dateOk) | Location: ($locOk) | Devices: ($devOk)")
    return (timeOk && dateOk && locOk && devOk)
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
    Boolean tempDev = devCondConfigured("temperature")
    Boolean humDev = devCondConfigured("humidity")
    Boolean illDev = devCondConfigured("illuminance")
    Boolean battDev = devCondConfigured("battery")
    return (swDev || motDev || presDev || conDev || lockDev || garDev || tempDev || humDev || illDev || battDev)
}

Boolean conditionsConfigured() {
    return (timeCondConfigured() || dateCondConfigured() || locationCondConfigured() || deviceCondConfigured())
}


/***********************************************************************************************************
    ACTION EXECUTION
************************************************************************************************************/

private executeActTest() {
    settingUpdate("actTestRun", "false", "bool")
    Map evt = [name: "contact", displayName: "some test device", value: "open", date: new Date()]
    if(settings?.actionType in ["speak", "announcement"]) {
        evt = getRandomTrigEvt()
    }
    executeAction(evt, true, "executeActTest", false, false)
}

Map getRandomTrigEvt() {
    List noDevTrigs = ["mode", "routine", "schedule", "scene", "hsmStatus", "alarmSystemStatus", "alarm"]
    Boolean useDev = (!(trig in noDevOpts))
    Map evt = [:]
    String trig = getRandomItem(settings?.triggerEvents?.collect { it as String })
    List trigItems = settings?."trig_${trig}" ?: null
    def randItem = trigItems?.size() ? getRandomItem(trigItems) : null
    def trigItem = randItem ? (randItem instanceof String ? [displayName: null, id: null] : (trigItems?.size() ? trigItems?.find { it?.id == randItem?.id } : [displayName: null, id: null])) : null
    // log.debug "trigItem: ${trigItem} | ${trigItem?.displayName} | ${trigItem?.id} | Evt: ${evt}"
    Map attVal = [
        "switch": getRandomItem(["on", "off"]),
        door: getRandomItem(["open", "closed", "opening", "closing"]),
        contact: getRandomItem(["open", "closed"]),
        lock: getRandomItem(["locked", "unlocked"]),
        water: getRandomItem(["wet", "dry"]),
        presence: getRandomItem(["present", "not present"]),
        motion: getRandomItem(["active", "inactive"]),
        valve: getRandomItem(["open", "closed"]),
        shade: getRandomItem(["open", "closed"]),
        temperature: getRandomItem(30..80),
        illuminance: getRandomItem(1..100),
        humidity: getRandomItem(1..100),
        battery: getRandomItem(1..100),
        power: getRandomItem(100..3000),
        thermostat: getRandomItem(["cooling is "]),
        mode: getRandomItem(location?.modes),
        alarm: getRandomItem(getAlarmTrigOpts()?.collect {it?.value as String}),
        guard: getRandomItem(["ARMED_AWAY", "ARMED_STAY"]),
        routineExecuted: isST() ? getRandomItem(getLocationRoutines()) : null
    ]
    if(attVal?.containsKey(trig)) { evt = [name: trig, displayName: trigItem?.displayName ?: "", value: attVal[trig], date: new Date(), deviceId: trigItem?.id ?: null] }
    // log.debug "getRandomTrigEvt | trig: ${trig} | Evt: ${evt}"
    return evt
}

String convEvtType(type) {
    Map typeConv = [
        "routineExecuted": "Routine",
        "alarmSystemStatus": "Alarm system",
        "hsmStatus": "Alarm system"
    ]
    return (type && typeConv?.containsKey(type)) ? typeConv[type] : type
}

String decodeVariables(evt, str) {
    if(evt && str) {
        // log.debug "str: ${str} | vars: ${(str =~ /%[a-z]+%/)}"
        if(str?.contains("%type%") && str?.contains("%name%")) {
            str = (str?.contains("%type%") && evt?.name) ? str?.replaceAll("%type%", !evt?.displayName?.toLowerCase()?.contains(evt?.name) ? convEvtType(evt?.name) : "") : str
            str = (str?.contains("%name%")) ? str?.replaceAll("%name%", evt?.displayName) : str
        } else {
            str = (str?.contains("%type%") && evt?.name) ? str?.replaceAll("%type%", convEvtType(evt?.name)) : str
            str = (str?.contains("%name%")) ? str?.replaceAll("%name%", evt?.displayName) : str
        }
        str = (str?.contains("%unit%") && evt?.name) ? str?.replaceAll("%unit%", getAttrPostfix(evt?.name)) : str
        str = (str?.contains("%value%") && evt?.value) ? str?.replaceAll("%value%", evt?.value?.isNumber() ? evtValueCleanup(evt?.value) : evt?.value) : str
        str = (str?.contains("%duration%") && evt?.totalDur) ? str?.replaceAll("%duration%", "${evt?.totalDur} seconds ago") : str
    }
    str = (str?.contains("%date%")) ? str?.replaceAll("%date%", convToDate(evt?.date ?: new Date())) : str
    str = (str?.contains("%time%")) ? str?.replaceAll("%time%", convToTime(evt?.date ?: new Date())) : str
    str = (str?.contains("%datetime%")) ? str?.replaceAll("%datetime%", convToDateTime(evt?.date ?: new Date())) : str
    return str
}

String getResponseItem(evt, evtAd=false, isRepeat=false, testMode=false) {
    // log.debug "getResponseItem | EvtName: ${evt?.name} | EvtDisplayName: ${evt?.displayName} | EvtValue: ${evt?.value} | AllDevsResp: ${evtAd} | Repeat: ${isRepeat} | TestMode: ${testMode}"
    String glbText = settings?."act_${settings?.actionType}_txt" ?: null
    if(glbText) {
        List eTxtItems = glbText ? glbText?.toString()?.tokenize(";") : []
        return decodeVariables(evt, getRandomItem(eTxtItems))
    } else {
        List devs = settings?."trig_${evt?.name}" ?: []
        String dc = settings?."trig_${evt?.name}_cmd"
        Boolean dca = (settings?."trig_${evt?.name}_all" == true)
        String dct = settings?."trig_${evt?.name}_txt" ?: null
        String dcart = settings?."trig_${evt?.name}_after_repeat" && settings?."trig_${evt?.name}_after_repeat_txt" ? settings?."trig_${evt?.name}_after_repeat_txt" : null
        List eTxtItems = dct ? dct?.toString()?.tokenize(";") : []
        List rTxtItems = dcart ? dcart?.toString()?.tokenize(";") : []
        List testItems = eTxtItems + rTxtItems
        if(testMode && testItems?.size()) {
            return  decodeVariables(evt, getRandomItem(testItems))
        } else if(!testMode && isRepeat && rTxtItems?.size()) {
            return  decodeVariables(evt, getRandomItem(rTxtItems))
        } else if(!testMode && eTxtItems?.size()) {
            return  decodeVariables(evt, getRandomItem(eTxtItems))
        } else {
            String postfix = getAttrPostfix(evt?.name) ?: ""
            switch(evt?.name) {
                case "thermostatMode":
                case "thermostatFanMode":
                case "thermostatOperatingState":
                case "coolSetpoint":
                case "heatSetpoint":
                    return "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} ${evt?.name} is ${evt?.value} ${postfix}"
                case "mode":
                    return  "The location mode is now set to ${evt?.value}"
                case "routine":
                    return  "The ${evt?.value} routine was just executed!."
                case "scene":
                    return  "The ${evt?.value} scene was just executed!."
                case "alarm":
                case "hsmStatus":
                case "alarmSystemStatus":
                    return "The ${getAlarmSystemName()} is now set to ${evt?.value}"
                case "guard":
                    return "Alexa Guard is now set to ${evt?.value}"
                case "hsmAlert":
                    return "A ${getAlarmSystemName()} ${evt?.displayName} alert with ${evt?.value} has occurred."
                case "sunriseTime":
                case "sunsetTime":
                    return "The ${getAlarmSystemName()} is now set to ${evt?.value}"
                case "schedule":
                    return "Your scheduled event has occurred at ${evt?.value}"
                default:
                    if(evtAd && devs?.size()>1) {
                        return "All ${devs?.size()}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} devices are ${evt?.value}"
                    } else {
                        return "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value} ${postfix}"
                    }
                    break
            }
        }
    }
    return "Invalid Text Received... Please verify Action configuration..."
}

private executeAction(evt = null, testMode=false, src=null, allDevsResp=false, isRptAct=false) {
    def startTime = now()
    logTrace( "executeAction${src ? "($src)" : ""}${testMode ? " | [TestMode]" : ""}${allDevsResp ? " | [AllDevsResp]" : ""}${isRptAct ? " | [RepeatEvt]" : ""}")
    if(isPaused()) { logWarn("Action is PAUSED... Skipping Action Execution...", true); return; }
    Boolean condOk = allConditionsOk()
    Boolean actOk = getConfStatusItem("actions")
    Boolean isST = isST()
    Map actMap = state?.actionExecMap ?: null
    List actDevices = parent?.getDevicesFromList(settings?.act_EchoDevices) ?: []
    String actMsgTxt = null
    String actType = settings?.actionType
    if(actOk && actType) {
        def alexaMsgDev = actDevices?.size() && settings?.notif_alexa_mobile ? actDevices[0] : null
        if(!condOk) { logWarn("Skipping Execution because set conditions have not been met", true); return; }
        if(!actMap || !actMap?.size()) { logError("executeAction Error | The ActionExecutionMap is not found or is empty", true); return; }
        if(!actDevices?.size()) { logError("executeAction Error | Echo Device List not found or is empty", true); return; }
        if(!actMap?.actionType) { logError("executeAction Error | The ActionType is missing or is empty", true); return; }
        Map actConf = actMap?.config
        Integer actDelay = actMap?.delay ?: 0
        Integer actDelayMs = actMap?.delay ? (actMap?.delay*1000) : 0
        Integer changeVol = actMap?.config?.volume?.change as Integer ?: null
        Integer restoreVol = actMap?.config?.volume?.restore as Integer ?: null
        Integer alarmVol = actMap?.config?.volume?.alarm ?: null

        switch(actType) {
            case "speak":
            case "announcement":
                if(actConf[actType]) {
                    String txt = getResponseItem(evt, allDevsResp, isRptAct, testMode) ?: null
                    // log.debug "txt: $txt"
                    if(!txt) { txt = "Invalid Text Received... Please verify Action configuration..." }
                    actMsgTxt = txt
                    if(actType == "speak") {
                        //Speak Command Logic
                        if(changeVol || restoreVol) {
                            actDevices?.each { dev-> dev?.setVolumeSpeakAndRestore(changeVol, txt, restoreVol) }
                        } else {
                            actDevices?.each { dev-> dev?.speak(txt) }
                        }
                        logDebug("Sending Speak Command: (${txt}) to ${actDevices}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}")
                    }
                    else if (actType == "announcement") {
                        //Announcement Command Logic
                        if(actDevices?.size() > 1 && actConf[actType]?.deviceObjs && actConf[actType]?.deviceObjs?.size()) {
                            //NOTE: Only sends command to first device in the list | We send the list of devices to announce one and then Amazon does all the processing
                            def devJson = new JsonOutput().toJson(actConf[actType]?.deviceObjs)
                            if(isST) {
                                actDevices[0]?.sendAnnouncementToDevices(txt, (app?.getLabel() ?: "Echo Speaks Action"), devJson, changeVol, restoreVol, [delay: actDelayMs])
                            } else { actDevices[0]?.sendAnnouncementToDevices(txt, (app?.getLabel() ?: "Echo Speaks Action"), devJson, changeVol, restoreVol) }
                            logDebug("Sending Announcement Command: (${txt}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}")
                        } else {
                            actDevices?.each { dev->
                                if(isST) {
                                    dev?.playAnnouncement(txt, (app?.getLabel() ?: "Echo Speaks Action"), changeVol, restoreVol, [delay: actDelayMs])
                                } else { dev?.playAnnouncement(txt, (app?.getLabel() ?: "Echo Speaks Action"), changeVol, restoreVol) }
                            }
                            logDebug("Sending Announcement Command: (${txt}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}")
                        }
                    }
                }
                break

            case "sequence":
                if(actConf[actType] && actConf[actType]?.text) {
                    actDevices?.each { dev->
                        if(isST) {
                            dev?.executeSequenceCommand(actConf[actType]?.text as String, [delay: actDelayMs])
                        } else { dev?.executeSequenceCommand(actConf[actType]?.text as String) }
                    }
                    logDebug("Sending Sequence Command: (${actConf[actType]?.text}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}")
                }
                break

            case "playback":
            case "dnd":
                if(actConf[actType] && actConf[actType]?.cmd) {
                    actDevices?.each { dev->
                        if(isST) {
                            dev?."${actConf[actType]?.cmd}"([delay: actDelayMs])
                        } else {
                            dev?."${actConf[actType]?.cmd}"()
                        }
                    }
                    logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}")
                }
                break

            case "builtin":
            case "calendar":
            case "weather":
                if(actConf[actType] && actConf[actType]?.cmd) {
                    if(changeVol || restoreVol) {
                        actDevices?.each { dev->
                            if(isST) {
                                dev?."${actConf[actType]?.cmd}"(changeVolume, restoreVol, [delay: actDelayMs])
                            } else { dev?."${actConf[actType]?.cmd}"(changeVolume, restoreVol) }
                        }
                        logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}")
                    } else {
                        actDevices?.each { dev->
                            if(isST) {
                                dev?."${actConf[actType]?.cmd}"([delay: actDelayMs])
                            } else { dev?."${actConf[actType]?.cmd}"() }
                        }
                        logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}")
                    }
                }
                break

            case "alarm":
            case "reminder":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.label && actConf[actType]?.date && actConf[actType]?.time) {
                    actDevices?.each { dev->
                        if(isST) {
                            dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time, [delay: actDelayMs])
                        } else { dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time) }
                    }
                    logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices} | Label: ${actConf[actType]?.label} | Date: ${actConf[actType]?.date} | Time: ${actConf[actType]?.time}")
                }
                break

            case "music":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.provider && actConf[actType]?.search) {
                    actDevices?.each { dev->
                        if(isST) {
                            dev?."${actConf[actType]?.cmd}"(actConf[actType]?.search, convMusicProvider(actConf[actType]?.provider), changeVol, restoreVol, [delay: actDelayMs])
                        } else { dev?."${actConf[actType]?.cmd}"(actConf[actType]?.search, convMusicProvider(actConf[actType]?.provider), changeVol, restoreVol) }
                    }
                    logDebug("Sending ${actType?.toString()?.capitalize()} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}")
                }
                break

            case "alexaroutine":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.routineId) {
                    actDevices[0]?."${actConf[actType]?.cmd}"(actConf[actType]?.routineId as String)
                    logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) | RoutineId: ${actConf[actType]?.routineId} to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}")
                }
                break

            case "wakeword":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id == d?.device }
                        if(aDev) {
                            if(isST) {
                                aDev?."${d?.cmd}"(d?.wakeword, [delay: actDelayMs])
                            } else { aDev?."${d?.cmd}"(d?.wakeword) }
                            logDebug("Sending WakeWord: (${d?.wakeword}) | Command: (${d?.cmd}) to ${aDev}${actDelay ? " | Delay: (${actDelay})" : ""}")
                        }
                    }
                }
                break

            case "bluetooth":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id == d?.device }
                        if(aDev) {
                            if(d?.cmd == "disconnectBluetooth") {
                                if(isST) {
                                    aDev?."${d?.cmd}"([delay: actDelayMs])
                                } else { aDev?."${d?.cmd}"() }
                            } else {
                                if(isST) {
                                    aDev?."${d?.cmd}"(d?.btDevice, [delay: actDelayMs])
                                } else { aDev?."${d?.cmd}"(d?.btDevice) }
                            }
                            logDebug("Sending ${d?.cmd} | Bluetooth Device: ${d?.btDevice} to ${aDev}${actDelay ? " | Delay: (${actDelay})" : ""}")
                        }
                    }
                }
                break
        }
        if(isActNotifConfigured()) {
            Boolean ok2SendNotif = true
            if(customMsgConfigured()) { actMsgTxt = settings?.notif_custom_message; }
            if(customMsgRequired() && !customMsgConfigured()) { ok2SendNotif = false }
            if(ok2SendNotif && actMsgTxt) {
                if(sendNotifMsg(app?.getLabel() as String, actMsgTxt as String, alexaMsgDev, false)) { logDebug("Sent Action Notification...") }
            }
        }
        if(isActDevContConfigured()) {
            if(settings?.act_switches_off) settings?.act_switches_off?.off()
            if(settings?.act_switches_on) settings?.act_switches_on?.on()
        }
    }
    logDebug("ExecuteAction Finished | ProcessTime: (${now()-startTime}ms)")
}

Map getInputData(inName) {
    String desc = null
    String title = null
    String template = null
    String vDesc = "<li>Example: %name% %type% is now %value% would translate into The garage door contact is now open</li>"
    vDesc += "<li>To make beep sounds use: <b>wop, wop, wop</b> (equals 3 beeps)</li>"
    switch(inName) {
        case "act_speak_txt":
            title = "Global Action Speech Event"
            desc = "<li>Add custom responses to use when this action is executed.</li>"
            template = '%name% %type% is now %value% %unit%'
            break
        case "act_announcement_txt":
            title = "Global Announcement Speech Event"
            desc = "<li>Add custom responses to use when this action is executed.</li>"
            template = '%name% %type% is now %value% %unit%'
            break
        default:
            if(inName?.startsWith("trig_")) {
                def i = inName?.tokenize("_")
                if(i?.contains("repeat")) {
                    title = "(${i[1]?.toString()?.capitalize()}) Trigger Repeat Events"
                    desc = "<li>Add custom responses for ${i[1]?.toString()?.capitalize()} events which have to be repeated.</li>${vDesc}"
                    template = '%name% %type% is now %value% %unit%'
                } else {
                    title = "(${i[1]?.toString()?.capitalize()}) Trigger Events"
                    desc = "<li>Add custom responses for ${i[1]?.toString()?.capitalize()} trigger events.</li>${vDesc}"
                    template = '%name% %type% is now %value% %unit%'
                }
            }
            break
    }
    Map o = [
        val: settings?."${inName}"?.toString() ?: null,
        desc: """<ul class="pl-3" style="list-style-type: bullet;">${desc}</ul>""",
        title: title,
        template: template
    ]
    return o
}

def updateTxtEntry(obj) {
    // log.debug "updateTxtEntry | Obj: $obj"
    if(obj?.name && obj?.type) {
        settingUpdate("${obj?.name}", obj?.val ?: "", obj?.type as String)
        return true
    }
    return false
}

public getSettingInputVal(inName) {
    // log.debug "getSettingInputVal: ${inName}"
    return settings?."${inName}" ?: null
}


/***********************************************************************************************************
   HELPER UTILITES
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
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone(location?.timeZone) } }
    return tf?.format(dt)
}

def dateTimeFmt(dt, fmt) {
    def tf = new SimpleDateFormat(fmt)
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
    def tf = new SimpleDateFormat("h:mm a")
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
        str += getNotifSchedDesc() ? " \u2022 Restrictions: (${getOk2Notify() ? "OK" : "Blocked"})\n" : ""
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

String getTriggersDesc(hideDesc=false) {
    Boolean confd = triggersConfigured()
    List setItem = settings?.triggerEvents
    String sPre = "trig_"
    if(confd && setItem?.size()) {
        if(!hideDesc) {
            String str = "Triggers:\n"
            setItem?.each { evt->
                switch(evt as String) {
                    case "scheduled":
                        str += " \u2022 ${evt?.capitalize()}${settings?."${sPre}${evt}_recurrence" ? " (${settings?."${sPre}${evt}_recurrence"})" : ""}\n"
                        str += settings?."${sPre}${evt}_time"     ? "    \u25E6 Time: (${fmtTime(settings?."${sPre}${evt}_time")})\n"      : ""
                        str += settings?."${sPre}${evt}_sunState" ? "    \u25E6 SunState: (${settings?."${sPre}${evt}_sunState"})\n"       : ""
                        str += settings?."${sPre}${evt}_days"     ? "    \u25E6 (${settings?."${sPre}${evt}_days"?.size()}) Days\n"      : ""
                        str += settings?."${sPre}${evt}_weeks"    ? "    \u25E6 (${settings?."${sPre}${evt}_weeks"?.size()}) Weeks\n"    : ""
                        str += settings?."${sPre}${evt}_months"   ? "    \u25E6 (${settings?."${sPre}${evt}_months"?.size()}) Months\n"  : ""
                        break
                    case "alarm":
                        str += " \u2022 ${evt?.capitalize()} (${getAlarmSystemName(true)})${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings?."${sPre}${evt}_once" ? "    \u25E6 Once a Day: (${settings?."${sPre}${evt}_once"})\n" : ""
                        break
                    case "routineExecuted":
                    case "mode":
                    case "scene":
                        str += " \u2022 ${evt == "routineExecuted" ? "Routines" : evt?.capitalize()}${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings?."${sPre}${evt}_once" ? "    \u25E6 Once a Day: (${settings?."${sPre}${evt}_once"})\n" : ""
                        break
                    default:
                        str += " \u2022 ${evt?.capitalize()}${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        def subStr = ""
                        if(settings?."${sPre}${evt}_cmd" in ["above", "below", "equal", "between"]) {
                            if (settings?."${sPre}${evt}_cmd" == "between") {
                                str += settings?."${sPre}${evt}_cmd"  ? "    \u25E6 ${settings?."${sPre}${evt}_cmd"}: (${settings?."${sPre}${evt}_low"} - ${settings?."${sPre}${evt}_high"})\n" : ""
                            } else {
                                str += (settings?."${sPre}${evt}_cmd" == "above" && settings?."${sPre}${evt}_high")     ? "    \u25E6 Above: (${settings?."${sPre}${evt}_high"})\n" : ""
                                str += (settings?."${sPre}${evt}_cmd" == "below" && settings?."${sPre}${evt}_low")      ? "    \u25E6 Below: (${settings?."${sPre}${evt}_low"})\n" : ""
                                str += (settings?."${sPre}${evt}_cmd" == "equal" && settings?."${sPre}${evt}_equal")    ? "    \u25E6 Equals: (${settings?."${sPre}${evt}_equal"})\n" : ""
                            }
                        } else {
                            str += settings?."${sPre}${evt}_cmd"  ? "    \u25E6 Trigger State: (${settings?."${sPre}${evt}_cmd"})\n" : ""
                        }
                        str += settings?."${sPre}${evt}_after"              ? "    \u25E6 Only After: (${settings?."${sPre}${evt}_after"} sec)\n" : ""
                        str += settings?."${sPre}${evt}_after_repeat"       ? "    \u25E6 Repeat Every: (${settings?."${sPre}${evt}_after_repeat"} sec)\n" : ""
                        str += settings?."${sPre}${evt}_all"                ? "    \u25E6 Require All: (${settings?."${sPre}${evt}_all"})\n" : ""
                        str += settings?."${sPre}${evt}_once"               ? "    \u25E6 Once a Day: (${settings?."${sPre}${evt}_once"})\n" : ""
                        str += settings?."${sPre}${evt}_wait"               ? "    \u25E6 Wait: (${settings?."${sPre}${evt}_wait"})\n" : ""
                        str += (settings?."${sPre}${evt}_txt" || settings?."${sPre}${evt}_after_repeat_txt") ? "    \u25E6 Custom Responses:\n" : ""
                        str += settings?."${sPre}${evt}_txt"                ? "       \u02C3 Events: (${settings?."${sPre}${evt}_txt"?.toString()?.tokenize(";")?.size()} Items)\n" : ""
                        str += settings?."${sPre}${evt}_after_repeat_txt"   ? "       \u02C3 Repeats: (${settings?."${sPre}${evt}_after_repeat_txt"?.toString()?.tokenize(";")?.size()} Items)\n" : ""
                        break
                }
            }
            str += "\ntap to modify..."
            return str
        } else { return "tap to modify..." }
    } else {
        return "tap to configure..."
    }
}

String getConditionsDesc() {
    Boolean confd = conditionsConfigured()
    def time = null
    String sPre = "cond_"
    if(confd) {
        String str = "Conditions: (${allConditionsOk() ? "OK" : "Block"})\n"
        if(timeCondConfigured()) {
            str += " • Time Between: (${timeCondOk() ? "OK" : "Block"})\n"
            str += "    - ${getTimeCondDesc(false)}\n"
        }
        if(dateCondConfigured()) {
            str += " • Date:\n"
            str += settings?.cond_days      ? "    - Days: (${(isDayOfWeek(settings?.cond_days)) ? "OK" : "Block"})\n" : ""
            str += settings?.cond_months    ? "    - Months: (${(isMonthOfYear(settings?.cond_months)) ? "OK" : "Block"})\n"  : ""
        }
        if(settings?.cond_alarm || settings?.cond_mode) {
            str += " • Location: (${locationCondOk() ? "OK" : "Block"})\n"
            str += settings?.cond_alarm ? "    - Alarm Modes: (${(isInAlarmMode(settings?.cond_alarm)) ? "OK" : "Block"})\n" : ""
            // str += settings?.cond_alarm ? "    - Current Alarm: (${getAlarmSystemStatus()})\n" : ""
            str += settings?.cond_mode ? "    - Location Modes: (${(isInMode(settings?.cond_mode)) ? "OK" : "Block"})\n" : ""
            // str += settings?.cond_mode ? "    - Current Mode: (${location?.mode})\n" : ""
        }
        if(deviceCondConfigured()) {
            // str += " • Devices: (${deviceCondOk() ? "OK" : "Block"})\n"
            ["switch", "motion", "presence", "contact", "lock", "door"]?.each { evt->
                if(devCondConfigured(evt)) {
                    str += settings?."${sPre}${evt}"     ? " • ${evt?.capitalize()} (${settings?."${sPre}${evt}"?.size()}) (${checkDeviceCondOk(evt) ? "OK" : "Block"})\n" : ""
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

String getActionDesc() {
    Boolean confd = executionConfigured()
    def time = null
    String sPre = "act_"
    if(settings?.actionType && confd) {
        String str = ""
        def eDevs = parent?.getDevicesFromList(settings?.act_EchoDevices)
        str += eDevs?.size() ? "Alexa Devices used:\n${eDevs?.collect { " \u2022 ${it?.displayName?.toString()?.replace("Echo - ", "")}" }?.join("\n")}\n" : ""
        str += settings?.act_volume_change ? " • Set Volume: (${settings?.act_volume_change})\n" : ""
        str += settings?.act_volume_restore ? " • Restore Volume: (${settings?.act_volume_restore})\n" : ""
        str += settings?.act_delay ? " • Delay: (${settings?.act_delay})\n" : ""
        str += settings?."act_${settings?.actionType}_txt" ? " • Using Default Response: (True)\n" : ""
        str += "\nTap to modify..."
        return str
    } else {
        return "tap to configure..."
    }
    return confd ? "Actions:\n • ${settings?.actionType}\n\ntap to modify..." : "tap to configure..."
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

private getPlatform() {
    def p = "SmartThings"
    if(state?.hubPlatform == null) {
        try { [dummy: "dummyVal"]?.encodeAsJson(); } catch (e) { p = "Hubitat" }
        // p = (location?.hubs[0]?.id?.toString()?.length() > 5) ? "SmartThings" : "Hubitat"
        state?.hubPlatform = p
        // logDebug("hubPlatform: (${state?.hubPlatform})")
    }
    return state?.hubPlatform
}

Boolean showChgLogOk() { return (state?.isInstalled && state?.shownChgLog != true) }
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

private addToLogHistory(String logKey, msg, Integer max=10) {
    Boolean ssOk = (stateSizePerc() > 70)
    List eData = atomicState[logKey as String] ?: []
    if(eData?.find { it?.message == msg }) { return; }
    eData.push([dt: getDtNow(), message: msg])
    if(!ssOk || eData?.size() > max) { eData = eData?.drop( (eData?.size()-max) ) }
    atomicState[logKey as String] = eData
}
private logDebug(msg) { if(settings?.logDebug == true) { log.debug "Actions (v${appVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info " Actions (v${appVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace "Actions (v${appVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { log.warn " Actions (v${appVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, 15); } }
private logError(msg, noHist=false) { if(settings?.logError != false) { log.error "Actions (v${appVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("errorHistory", msg, 15); } }

Map getLogHistory() {
    return [ warnings: atomicState?.warnHistory ?: [], errors: atomicState?.errorHistory ?: [] ]
}
void clearLogHistory() {
    atomicState?.warnHistory = []
    atomicState?.errorHistory = []
}

String convMusicProvider(String prov) {
    switch (prov) {
        case "Amazon Music":
            return "AMAZON_MUSIC"
        case "Apple Music":
            return "APPLE_MUSIC"
        case "TuneIn":
            return "TUNEIN"
        case "Pandora":
            return "PANDORA"
        case "Sirius Xm":
            return "SIRIUSXM"
        case "Spotify":
            return "SPOTIFY"
        case "Tidal":
            return "TIDAL"
        case "iHeartRadio":
            return "I_HEART_RADIO"
    }
}

String getObjType(obj) {
    if(obj instanceof String) {return "String"}
    else if(obj instanceof GString) {return "GString"}
    else if(obj instanceof Map) {return "Map"}
    else if(obj instanceof List) {return "List"}
    else if(obj instanceof ArrayList) {return "ArrayList"}
    else if(obj instanceof Integer) {return "Integer"}
    else if(obj instanceof BigInteger) {return "BigInteger"}
    else if(obj instanceof Long) {return "Long"}
    else if(obj instanceof Boolean) {return "Boolean"}
    else if(obj instanceof BigDecimal) {return "BigDecimal"}
    else if(obj instanceof Float) {return "Float"}
    else if(obj instanceof Byte) {return "Byte"}
    else if(obj instanceof Date) {return "Date"}
    else { return "unknown"}
}
/************************************************
            SEQUENCE TEST LOGIC
*************************************************/

Map seqItemsAvail() {
    return [
        other: [
            "weather":null, "traffic":null, "flashbriefing":null, "goodmorning":null, "goodnight":null, "cleanup":null,
            "singasong":null, "tellstory":null, "funfact":null, "joke":null, "playsearch":null, "calendartoday":null,
            "calendartomorrow":null, "calendarnext":null, "stop":null, "stopalldevices":null,
            "wait": "value (seconds)", "volume": "value (0-100)", "speak": "message", "announcement": "message",
            "announcementall": "message", "pushnotification": "message", "email": null
        ],
        // dnd: [
        //     "dnd_duration": "2H30M", "dnd_time": "00:30", "dnd_all_duration": "2H30M", "dnd_all_time": "00:30",
        //     "dnd_duration":"2H30M", "dnd_time":"00:30"
        // ],
        speech: [
            "cannedtts_random": ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
        ],
        music: [
            "amazonmusic": "search term", "applemusic": "search term", "iheartradio": "search term", "pandora": "search term",
            "spotify": "search term", "tunein": "search term", "cloudplayer": "search term"
        ]
    ]
}

def searchTuneInResultsPage() {
    return dynamicPage(name: "searchTuneInResultsPage", uninstall: false, install: false) {
        def results = executeTuneInSearch()
        Boolean onST = isST()
        section(sTS("Search Results: (Query: ${settings?.tuneinSearchQuery})")) {
            if(results?.browseList && results?.browseList?.size()) {
                results?.browseList?.eachWithIndex { item, i->
                    if(i < 25) {
                        if(item?.browseList != null && item?.browseList?.size()) {
                            item?.browseList?.eachWithIndex { item2, i2->
                                String str = ""
                                str += "ContentType: (${item2?.contentType})"
                                str += "\nId: (${item2?.id})"
                                str += "\nDescription: ${item2?.description}"
                                if(onST) {
                                    paragraph title: pTS(item2?.name?.take(75), (onST ? null : item2?.image)), str, required: true, state: (!item2?.name?.contains("Not Supported") ? "complete" : null), image: item2?.image ?: ""
                                } else { href "searchTuneInResultsPage", title: pTS(item2?.name?.take(75), (onST ? null : item2?.image)), description: str, required: true, state: (!item2?.name?.contains("Not Supported") ? "complete" : null), image: onST && item2?.image ? item2?.image : null }
                            }
                        } else {
                            String str = ""
                            str += "ContentType: (${item?.contentType})"
                            str += "\nId: (${item?.id})"
                            str += "\nDescription: ${item?.description}"
                            if(onST) {
                                paragraph title: pTS(item?.name?.take(75), (onST ? null : item?.image)), str, required: true, state: (!item?.name?.contains("Not Supported") ? "complete" : null), image: item?.image ?: ""
                            } else { href "searchTuneInResultsPage", title: pTS(item?.name?.take(75), (onST ? null : item?.image)), description: str, required: true, state: (!item?.name?.contains("Not Supported") ? "complete" : null), image: onST && item?.image ? item?.image : null }
                        }
                    }
                }
            } else { paragraph "No Results found..." }
        }
    }
}

//*******************************************************************
//    CLONE ACTION Logic
//*******************************************************************
public getActDuplSettingData() {
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
    data?.label = app?.getLabel()?.toString()?.replace(" | (Paused)", "")
    data?.settings = setObjs
    return data
}

public getActDuplStateData() {
    List stskip = ["isInstalled", "isParent", "lastActNotifMsgDt", "lastActionNotificationMsg", "setupComplete", "valEvtHistory", "warnHistory", "errorHistory"]
    return state?.findAll { !(it?.key in stskip) }
}

