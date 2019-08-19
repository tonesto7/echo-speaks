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

String appVersion()  { return "3.0.0" }
String appModified()  { return "2019-08-18" }
String appAuthor()   { return "Anthony S." }
Boolean isBeta()     { return false }
Boolean isST()       { return (getPlatform() == "SmartThings") }

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
    page(name: "codeUpdatePage")
    page(name: "mainPage")
    page(name: "uhOhPage")
    page(name: "namePage")
    page(name: "triggersPage")
    page(name: "conditionsPage")
    page(name: "notifPrefPage")
    page(name: "actionsPage")
    page(name: "prefsPage")
    page(name: "searchTuneInResultsPage")
    page(name: "condTimePage")
    page(name: "uninstallPage")
    page(name: "sequencePage")
}

def startPage() {
    if(parent != null) {
        if(!state?.isInstalled && parent?.childInstallOk() != true) {
            uhOhPage()
        } else {
            state?.isParent = false
            if(checkMinVersion()) {
                codeUpdatePage()
            } else { mainPage() }
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
    buildItems["Safety & Security"] = ["alarm": "${getAlarmSystemName()}", "smoke":"Fire/Smoke", "carbon":"Carbon Monoxide"]?.sort{ it?.key }
    buildItems["Actionable Devices"] = ["lock":"Locks", "switch":"Outlets/Switches", "level":"Dimmers/Level", "door":"Garage Door Openers", "valve":"Valves", "shade":"Window Shades", "button":"Buttons", "thermostat":"Thermostat"]?.sort{ it?.key }
    buildItems["Sensor Device"] = ["contact":"Contacts, Doors, Windows", "battery":"Battery Level", "motion":"Motion", "presence":"Presence", "temperature":"Temperature", "humidity":"Humidity", "water":"Water", "power":"Power"]?.sort{ it?.key }
    if(isST()) {
        buildItems?.each { key, val-> addInputGrp(enumOpts, key, val) }
        // log.debug "enumOpts: $enumOpts"
        return enumOpts
    } else { return buildItems?.collectEntries { it?.value } }
}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        Boolean paused = isPaused()
        if(!paused) {
            Boolean trigConf = triggersConfigured()
            Boolean condConf = conditionsConfigured()
            Boolean actConf = actionsConfigured()
            section (sTS("Configuration: Part 1")) {
                href "triggersPage", title: inTS("Action Triggers", getAppImg("trigger", true)), description: getTriggersDesc(), state: (trigConf ? "complete" : ""), image: getAppImg("trigger")
            }

            section(sTS("Configuration: Part 2")) {
                if(trigConf) {
                    href "conditionsPage", title: inTS("Condition/Restrictions\n(Optional)", getAppImg("conditions", true)), description: getConditionsDesc(), state: (condConf ? "complete": ""), image: getAppImg("conditions")
                } else { paragraph pTS("More Options will be shown once triggers are configured", getAppImg("info", true)) }
            }
            section(sTS("Configuration: Part 3")) {
                if(trigConf) {
                    href "actionsPage", title: inTS("Actions Tasks", getAppImg("es_actions", true)), description: getActionDesc(), state: (actConf ? "complete" : ""), image: getAppImg("es_actions")
                } else { paragraph pTS("More Options will be shown once triggers are configured", getAppImg("info", true)) }
            }
            section(sTS("Preferences")) {
                href "prefsPage", title: inTS("Debug/Preferences", getAppImg("settings", true)), description: "", image: getAppImg("settings")
            }
        } else {
            section() {
                paragraph pTS("This Action is currently in a paused state...  To edit the configuration please un-pause", getAppImg("pause_orange", true)), required: true, state: null, image: getAppImg("pause_orange")
            }
        }

        if(state?.isInstalled) {
            section(sTS("Place this action on hold:")) {
                input "actionPause", "bool", title: inTS("Pause Action?", getAppImg("pause_orange", true)), defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange")
            }
            section(sTS("Name this Action:")) {
                input "appLbl", "text", title: inTS("Action Name", getAppImg("name_tag", true)), description: "", required:true, submitOnChange: true, image: getAppImg("name_tag")
            }
            section(sTS("Remove Action:")) {
                href "uninstallPage", title: inTS("Remove this Action", getAppImg("uninstall", true)), description: "Tap to Remove...", image: getAppImg("uninstall")
            }
        }
    }
}

def prefsPage() {
    return dynamicPage(name: "prefsPage", install: false, uninstall: false) {
        section(sTS("Debug")) {
            input "appDebug", "bool", title: inTS("Show Debug Logs in the IDE?", getAppImg("debug", true)), description: "Only enable when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug")
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
    return dynamicPage(name: "triggersPage", uninstall: false, install: false) {
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
            Integer trigEvtCnt = settings?.triggerEvents?.size()
            if(!(settings?.triggerEvents in ["Scheduled", "Weather"])) { showSpeakEvtVars = true }
            if (valTrigEvt("scheduled")) {
                section(sTS("Time Based Events"), hideable: true) {
                    if(!settings?.trig_scheduled_time) {
                        input "trig_scheduled_sunState", "enum", title: inTS("Sunrise or Sunset...", getAppImg("sun", true)), options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true, image: getPublicImg("sun")
                        if(settings?.trig_scheduled_sunState) {
                            input "trig_scheduled_sunState_offset", "number", range: "*..*", title: inTS("Offset event this number of minutes (+/-)", getAppImg(settings?.trig_scheduled_sunState?.toString()?.toLowerCase(), true)),
                                    required: true, image: getPublicImg(settings?.trig_scheduled_sunState?.toString()?.toLowerCase() + "")
                        }
                    }
                    if(!settings?.trig_scheduled_sunState) {
                        input "trig_scheduled_time", "time", title: inTS("Time of Day?", getAppImg("clock", true)), required: false, submitOnChange: true, image: getPublicImg("clock")
                        if(settings?.trig_scheduled_time || settings?.trig_scheduled_sunState) {
                            input "trig_scheduled_recurrence", "enum", title: inTS("Recurrence?", getAppImg("day_calendar2", true)), description: "(Optional)", multiple: false, required: false, submitOnChange: true, options: ["Once", "Daily", "Weekly", "Monthly"], defaultValue: "Once", image: getPublicImg("day_calendar2")
                            Boolean dayReq = (settings?.trig_scheduled_recurrence in ["Weekly", "Monthly"])
                            Boolean weekReq = (settings?.trig_scheduled_recurrence in ["Weekly", "Monthly"])
                            Boolean monReq = (settings?.trig_scheduled_recurrence in ["Monthly"])
                            if(settings?.trig_scheduled_recurrence) {
                                input "trig_scheduled_days", "enum", title: inTS("Day(s) of the week", getAppImg("day_calendar2", true)), description: (!dayReq ? "(Optional)" : ""), multiple: true, required: dayReq, submitOnChange: true, options: weekDaysEnum(), image: getPublicImg("day_calendar2")
                                input "trig_scheduled_weeks", "enum", title: inTS("Weeks(s) of the month", getAppImg("day_calendar2", true)), description: (!weekReq ? "(Optional)" : ""), multiple: true, required: weekReq, submitOnChange: true, options: ["1", "2", "3", "4", "5"], image: getPublicImg("day_calendar2")
                                input "trig_scheduled_months", "enum", title: inTS("Month(s) of the year", getAppImg("day_calendar2", true)), description: (!monReq ? "(Optional)" : ""), multiple: true, required: monReq, submitOnChange: true, options: monthEnum(), image: getPublicImg("day_calendar2")
                            }
                        }
                    }
                }
            }

            if (valTrigEvt("alarm")) {
                section (sTS("${getAlarmSystemName()} (${getAlarmSystemName(true)}) Events"), hideable: true) {
                    input "trig_alarm", "enum", title: inTS("${getAlarmSystemName()} Modes", getAppImg("alarm_home", true)), options: getAlarmTrigOpts(), multiple: true, required: true, submitOnChange: true, image: getAppImg("alarm_home")
                    // if("alerts" in trig_alarm) {
                    //     input "trig_alarm_alerts_clear", "bool", title: "Send the update when Alerts are cleared.", required: false, defaultValue: false, submitOnChange: true
                    // }
                }
            }

            if (valTrigEvt("mode")) {
                section (sTS("Mode Events"), hideable: true) {
                    input "trig_mode", "mode", title: inTS("Location Modes", getAppImg("mode", true)), multiple: true, required: true, submitOnChange: true, image: getAppImg("mode")
                    if(settings?.trig_mode) {
                        input "trig_mode_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                    }
                }
            }

            if(valTrigEvt("routineExecuted")) {
                section(sTS("Routine Events"), hideable: true) {
                    input "trig_routineExecuted", "enum", title: inTS("Routines", getAppImg("routine", true)), options: stRoutines, multiple: true, required: true, submitOnChange: true, image: getAppImg("routine")
                    if(settings?.trig_routineExecuted) {
                        input "trig_routineExecuted_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                    }
                }
            }

            if(valTrigEvt("scene")) {
                section(sTS("Scene Events"), hideable: true) {
                    input "trig_scene", "device.sceneActivator", title: inTS("Scene Devices", getAppImg("routine", true)), multiple: true, required: true, submitOnChange: true, image: getAppImg("routine")
                    if(settings?.trig_scene) {
                        input "trig_scene_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                    }
                }
            }

            if (valTrigEvt("switch")) {
                trigNonNumSect("switch", "switch", "Switches", "Switches", ["on", "off", "any"], "are turned", ["on", "off"], "switch")
            }

            if (valTrigEvt("level")) {
                trigNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level is", "speed_knob")
            }

            if (valTrigEvt("battery")) {
                trigNumValSect("battery", "battery", "Battery Level", "Batteries", "Level is", "speed_knob")
            }

            if (valTrigEvt("motion")) {
                trigNonNumSect("motion", "motionSensor", "Motion Sensors", "Motion Sensors", ["active", "inactive", "any"], "become", ["active", "inactive"], "motion")
            }

            if (valTrigEvt("presence")) {
                trigNonNumSect("presence", "presenceSensor", "Presence Sensors", "Presence Sensors", ["present", "not present", "any"], "changes to", ["present", "not present"], "presence")
            }

            if (valTrigEvt("contact")) {
                trigNonNumSect("contact", "contactSensor", "Contacts, Doors, Windows", "Contacts, Doors, Windows", ["open", "closed", "any"], "changes to", ["open", "closed"], "contact")
            }

            if (valTrigEvt("door")) {
                trigNonNumSect("door", "garageDoorControl", "Garage Door Openers", "Garage Doors", ["open", "closed", "opening", "closing", "any"], "changes to", ["open", "closed"], "garage_door")
            }

            if (valTrigEvt("lock")) {
                trigNonNumSect("lock", "lock", "Locks", "Smart Locks", ["locked", "unlocked", "any"], "changes to", ["locked", "unlocked"], "lock")
            }

            if (valTrigEvt("temperature")) {
                trigNumValSect("temperature", "temperatureMeasurement", "Temperature Sensor", "Temperature Sensors", "Temperature", "temperature")
            }

            if (valTrigEvt("humidity")) {
                trigNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Sensors", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity")
            }

            if (valTrigEvt("water") in settings?.triggerEvents) {
                trigNonNumSect("water", "waterSensor", "Water Sensors", "Water/Moisture Sensors", ["wet", "dry", "any"], "changes to", ["wet", "dry"], "water")
            }

            if (valTrigEvt("power")) {
                trigNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power")
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
                            //Custom Text Options
                            paragraph "Description:\nYou can set custom responses for each carbon monoxide event.\nNotice: These are only used if Speech or Announcement action are selected.\nFYI: To allow multiple random responses just separate each response with a ;"
                            input "trig_carbon_txt", "text", title: inTS("Custom Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
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
                            //Custom Text Options
                            paragraph "Description:\nYou can set custom responses for each smoke event.\nNotice: These are only used if Speech or Announcement action are selected.\nFYI: To allow multiple random responses just separate each response with a ;"
                            input "trig_smoke_txt", "text", title: inTS("Custom Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
                        }
                    }
                }
            }

            if (valTrigEvt("illuminance")) {
                trigNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Events", "Illuminance Sensors", "Lux Level (%)", "illuminance")
            }

            if (valTrigEvt("shade")) {
                trigNonNumSect("shade", "windowShades", "Window Shades", "Window Shades", ["open", "closed", "opening", "closing", "any"], "changes to", ["open", "closed"], "window_shade")
            }

            if (valTrigEvt("valve")) {
                trigNonNumSect("valve", "valve", "Valves", "Valves", ["open", "closed", "any"], "changes to", ["open", "closed"], "valve")
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
                            //Custom Text Options
                            paragraph "Description:\nYou can set custom responses for each thermostat event.\nNotice: These are only used if Speech or Announcement action are selected.\nFYI: To allow multiple random responses just separate each response with a ;"
                            input "trig_thermostat_txt", "text", title: inTS("Custom Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
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
                    // if(trig_weather_cmd) {
                    //     input "trig_weather_hourly", "enum", title: inTS("Hourly Forecast Updates", getAppImg("command", true)), required: false, multiple: false, submitOnChange: true, image: getAppImg("command"),
                    //             options: ["conditions":"Weather Condition Changes", "rain":"Chance of Precipitation Changes", "wind":"Wind Speed Changes", "humidit":"Humidity Changes", "any":"Any Weather Updates"]
                    //     if(!settings?.trig_weather_hourly) {
                    //         input "trig_weather_events", "enum", title: inTS("Weather Elements", getAppImg("command", true)), required: false, multiple: false, submitOnChange: true, options: ["Chance of Precipitation (in/mm)", "Wind Gust (MPH/kPH)", "Humidity (%)", "Temperature (F/C)"], image: getAppImg("command")
                    //         if (settings?.trig_WeatherEvents) {
                    //             input "trig_weather_events_cmd", "enum", title: inTS("Notify when Weather Element changes...", getAppImg("command", true)), options: ["above", "below"], required: false, submitOnChange: true, image: getAppImg("command")
                    //         }
                    //         if (settings?.trig_WeatherEventsCond) {
                    //             input "trig_WeatherThreshold", "decimal", title: inTS("Weather Variable Threshold...", getAppImg("command", true)), required: false, submitOnChange: true, image: getAppImg("command")
                    //             if (settings?.trig_WeatherThreshold) {
                    //                 input "trig_WeatherCheckSched", "enum", title: inTS("How Often to Check for Weather Changes...", getAppImg("command", true)), required: true, multiple: false, submitOnChange: true, image: getAppImg("command"),
                    //                     options: [
                    //                         "runEvery1Minute": "Every Minute",
                    //                         "runEvery5Minutes": "Every 5 Minutes",
                    //                         "runEvery10Minutes": "Every 10 Minutes",
                    //                         "runEvery15Minutes": "Every 15 Minutes",
                    //                         "runEvery30Minutes": "Every 30 Minutes",
                    //                         "runEvery1Hour": "Every Hour",
                    //                         "runEvery3Hours": "Every 3 Hours"
                    //                     ]
                    //             }
                    //         }
                    //     }
                    // }
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

def trigNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, cmdAfterOpts, String image) {
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: inTS(devTitle, getAppImg(image, true)), multiple: true, required: true, submitOnChange: true, image: getAppImg(image)
        if (settings?."trig_${inType}") {
            input "trig_${inType}_cmd", "enum", title: inTS("${cmdTitle}...", getAppImg("command", true)), options: cmdOpts, multiple: false, required: true, submitOnChange: true, image: getAppImg("command")
            if(settings?."trig_${inType}_cmd") {
                if (settings?."trig_${inType}"?.size() > 1 && settings?."trig_${inType}_cmd" != "any") {
                    input "trig_${inType}_all", "bool", title: inTS("Require ALL ${devTitle} to be (${settings?."trig_${inType}_cmd"})?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                }
                if(settings?."trig_${inType}_cmd" in cmdAfterOpts) {
                    input "trig_${inType}_after", "number", title: inTS("Only trigger after (${settings?."trig_${inType}_cmd"}) for (xx) minutes?", getAppImg("delay_time", true)), required: false, defaultValue: null, submitOnChange: true, image: getAppImg("delay_time")
                    if(settings?."trig_${inType}_after") {
                        input "trig_${inType}_after_repeat", "number", title: inTS("Repeat trigger every (xx) seconds until not ${settings?."trig_${inType}_cmd"}?", getAppImg("delay_time", true)), required: false, defaultValue: null, submitOnChange: true, image: getAppImg("delay_time")
                    }
                }
                input "trig_${inType}_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                input "trig_${inType}_wait", "number", title: inTS("Wait between each report (xx) seconds\n(Optional)", getAppImg("delay_time", true)), required: false, defaultValue: 0, submitOnChange: true, image: getAppImg("delay_time")
                //Custom Text Options
                paragraph "Description:\nYou can set custom responses for each ${inType} event.\n\nNotice:\nThese are only used if Speech or Announcement action are selected.\n\nFYI:\nTo allow multiple random responses just separate each response with a ;"
                input "trig_${inType}_txt", "text", title: inTS("Custom ${inType?.capitalize()} Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
                if(settings?."trig_${inType}_after_repeat") {
                    //Custom Repeat Text Options
                    paragraph "Description:\nAdd custom responses for the ${inType} events that are repeated.\nTo allow multiple random responses just separate each response with a ;"
                    input "trig_${inType}_after_repeat_txt", "text", title: inTS("Custom ${inType?.capitalize()} Repeat Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
                }
            }
        }
    }
}

def trigNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image) {
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: inTS(devTitle, getAppImg(image, true)), multiple: true, submitOnChange: true, required: true, image: getAppImg(image)
        if(settings?."trig_${inType}") {
            input "trig_${inType}_cmd", "enum", title: inTS("${cmdTitle} is...", getAppImg("command", true)), options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true, image: getAppImg("command")
            if (settings?."trig_${inType}_cmd") {
                if (settings?."trig_${inType}_cmd" in ["between", "below"]) {
                    input "trig_${inType}_low", "number", title: inTS("a ${settings?."trig_${inType}_cmd" == "between" ? "Low " : ""}${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?."trig_${inType}_cmd" in ["between", "above"]) {
                    input "trig_${inType}_high", "number", title: inTS("${settings?."trig_${inType}_cmd" == "between" ? "and a high " : "a "}${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?."trig_${inType}_cmd" == "equals") {
                    input "trig_${inType}_equal", "number", title: inTS("a ${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?.trig_level?.size() > 1) {
                    input "trig_${inType}_all", "bool", title: inTS("Require ALL devices to be (${settings?."trig_${inType}_cmd"}) values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
                }
                input "trig_${inType}_once", "bool", title: inTS("Only alert once a day?\n(per device)", getAppImg("question", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("question")
                input "trig_${inType}_wait", "number", title: inTS("Wait between each report", getAppImg("delay_time", true)), required: false, defaultValue: 120, submitOnChange: true, image: getAppImg("delay_time")
                //Custom Text Options
                paragraph "Description:\nYou can set custom responses for each ${inType} event.\n\nNotice:\nThese are only used if Speech or Announcement action are selected.\n\nFYI:\nTo allow multiple random responses just separate each response with a ;"
                input "trig_${inType}_txt", "text", title: inTS("Custom ${inType?.capitalize()} Text/SSML Response\n(Optional)", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
            }
        }
    }
}

Boolean scheduleTriggers() {
    return (settings?.trig_scheduled_time || settings?.trig_scheduled_sunState)
}

Boolean locationTriggers() {
    return (settings?.trig_mode || settings?.trig_alarm || settings?.trig_routineExecuted || settings?.trig_scene)
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
    return dynamicPage(name: "conditionsPage", title: "Only when these device, location conditions are True...", install: false, uninstall: false) {
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
                    input "cond_${inType}_low", "number", title: inTS("a ${settings?."cond_${inType}_cmd" == "between" ? "Low " : ""}${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?."cond_${inType}_cmd" in ["between", "above"]) {
                    input "cond_${inType}_high", "number", title: inTS("${settings?."cond_${inType}_cmd" == "between" ? "and a high " : "a "}${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?."cond_${inType}_cmd" == "equals") {
                    input "cond_${inType}_equal", "number", title: inTS("a ${cmdTitle} of..."), required: true, submitOnChange: true
                }
                if (settings?.cond_level?.size() > 1) {
                    input "cond_${inType}_all", "bool", title: inTS("Require ALL devices to be (${settings?."cond_${inType}_cmd"}) values?", getAppImg("checkbox", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("checkbox")
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

def actionsPage() {
    return dynamicPage(name: "actionsPage", title: (settings?.actionType ? "Action | (${settings?.actionType})" : "Actions to perform..."), install: false, uninstall: false) {
        Boolean done = false
        Map actionExecMap = [configured: false]
        Map actionOpts = [
            "speak":"Speak (SSML Supported)", "announcement":"Announcement (SSML Supported)", "sequence":"Execute Sequence", "weather":"Weather Report", "playback":"Playback Control",
            "builtin":"Sing, Jokes, Story, etc.", "music":"Play Music", "calendar":"Calendar Events", "alarm":"Create Alarm", "reminder":"Create Reminder", "dnd":"Do Not Disturb",
            "bluetooth":"Bluetooth Control", "wakeword":"Wake Word", "alexaroutine": "Execute Alexa Routine(s)"
        ]
        section(sTS("Configure Actions to Take:"), hideable: true, hidden: (settings?.act_EchoDevices?.size())) {
            input "actionType", "enum", title: inTS("Actions Type", getAppImg("list", true)), description: "", options: actionOpts, multiple: false, required: true, submitOnChange: true, image: getAppImg("list")
        }

        if(actionType) {
            actionExecMap?.actionType = actionType
            actionExecMap?.config = [:]
            List devices = parent?.getDevicesFromList(settings?.act_EchoDevices)
            switch(actionType) {
                case "speak":
                    String ssmlTestUrl = "https://topvoiceapps.com/ssml"
                    String ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
                    String ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
                    String ssmlSpeechConsUrl = "https://developer.amazon.com/docs/custom-skills/speechcon-reference-interjections-english-us.html"
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section(sTS("SSML Info:"), hideable: true, hidden: true) {
                            paragraph title: "What is SSML?", pTS("SSML allows for changes in tone, speed, voice, emphasis. As well as using MP3, and access to the Sound Library", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                            href url: ssmlDocsUrl, style: "external", required: false, title: inTS("Amazon SSML Docs", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
                            href url: ssmlSoundsUrl, style: "external", required: false, title: inTS("Amazon Sound Library", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
                            href url: ssmlSpeechConsUrl, style: "external", required: false, title: inTS("Amazon SpeechCons", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
                            href url: ssmlTestUrl, style: "external", required: false, title: inTS("SSML Designer and Tester", getAppImg("www", true)), description: "Tap to open browser", image: getAppImg("www")
                        }
                        section(sTS("Speech Tips:")) {
                            paragraph pTS("To make beep tones use: 'wop, wop, wop' (equals 3 beeps)", getAppImg("info", true)), image: getAppImg("info")
                        }

                        section(sTS("Action Config:")) {
                            variableDesc()
                            input "act_speak_txt", "text", title: inTS("Enter Text/SSML", getAppImg("text", true)), description: "Enter Text to Speak", submitOnChange: true, required: false, image: getAppImg("text")
                            paragraph pTS("Reminder\nIf entering SSML be sure to wrap the text in <speak></speak>", getAppImg("info", true))
                        }
                        actionVolumeInputs(devices)
                        actionExecMap?.config?.speak = [text: settings?.act_speak_txt, evtText: ((state?.showSpeakEvtVars && !settings?.act_speak_txt) || hasUserDefinedTxt())]
                        if(state?.showSpeakEvtVars || act_speak_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "announcement":
                    section(sTS("Action Description:")) {
                        paragraph pTS("Plays a brief tone and speaks the message you define. If you select multiple devices it will be a synchronized broadcast.", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("announce")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Config:")) {
                            variableDesc()
                            input "act_announcement_txt", "text", title: inTS("Enter Text to announce", getAppImg("text", true)), submitOnChange: true, required: false, image: getAppImg("text")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap?.config?.announcement = [text: settings?.act_announcement_txt, evtText: ((state?.showSpeakEvtVars && !settings?.act_speak_txt) || hasUserDefinedTxt())]
                        if(settings?.act_EchoDevices?.size() > 1) {
                            List devObj = []
                            devices?.each { devObj?.push([deviceTypeId: it?.currentValue("deviceType"), deviceSerialNumber: it?.deviceNetworkId?.toString()?.tokenize("|")[2]]) }
                            log.debug "devObj: $devObj"
                            actionExecMap?.config?.announcement?.deviceObjs = devObj
                        }
                        if(state?.showSpeakEvtVars || act_announcement_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sequence":
                    section(sTS("Action Description:")) {
                        paragraph pTS("Sequences are a custom command where you can string different alexa actions which are sent to Amazon as a single command.  The command is then processed by amazon sequentially or in parallel.", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
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
                            paragraph "Enter the command in a format exactly like this:\nvolume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30\n\nEach command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.", state: "complete"
                        }
                        section(sTS("Action Config:")) {
                            input "act_sequence_txt", "text", title: inTS("Enter sequence text", getAppImg("text", true)), submitOnChange: true, required: false, image: getAppImg("text")
                        }
                        actionExecMap?.config?.sequence = [text: settings?.act_sequence_txt]
                        if(settings?.act_sequence_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "weather":
                    section(sTS("Action Description:")) {
                        paragraph pTS("Plays a very basic weather report.", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        actionVolumeInputs(devices)
                        done = true
                        actionExecMap?.config?.weather = [cmd: "playWeather"]
                    } else { done = false }
                    break

                case "playback":
                    section(sTS("Action Description:")) {
                        paragraph pTS("Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        Map playbackOpts = [
                            "pause":"Pause", "stop":"Stop", "play": "Play", "nextTrack": "Next Track", "previousTrack":"Previous Track",
                            "mute":"Mute"
                        ]
                        section(sTS("Playback Config:")) {
                            input "act_playback_cmd", "enum", title: inTS("Select Playback Action", getAppImg("command", true)), description: "", options: playbackOpts, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionExecMap?.config?.playback = [cmd: settings?.act_playback_cmd]
                        if(settings?.act_playback_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "builtin":
                    section(sTS("Action Description:")) {
                        paragraph pTS("Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
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
                    section(sTS("Action Description:")) {
                        paragraph pTS("Allow playback of various Songs/Radio using any connected music provider", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        List musicProvs = devices[0]?.hasAttribute("supportedMusic") ? devices[0]?.currentValue("supportedMusic")?.split(",")?.collect { "${it?.toString()?.trim()}"} : []
                        log.debug "Music Providers: ${musicProvs}"
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
                                section(sTS("Action Config:")) {
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
                    section(sTS("Action Description:")) {
                        paragraph pTS("This will read out events in your calendar (Requires accounts to be configured in the alexa app. Must not have PIN.)", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Config:")) {
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
                    section(sTS("Action Description:")) {
                        paragraph pTS("This will allow you to alexa alarms based on triggers", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("alarms")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Config:")) {
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
                    section(sTS("Action Description:")) {
                        paragraph pTS("This will allow you to alexa reminders based on triggers", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                    }
                    echoDevicesInputByPerm("reminders")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Config:")) {
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
                        section(sTS("Action Description:")) {
                            paragraph pTS("This will allow you to enable/disable Do Not Disturb based on triggers", getAppImg("info", true)), state: "complete"
                        }
                        section(sTS("Action Config:")) {
                            input "act_dnd_cmd", "enum", title: inTS("Select Do Not Disturb Action", getAppImg("command", true)), description: "", options: dndOpts, required: true, submitOnChange: true, image: getAppImg("command")
                        }
                        actionExecMap?.config?.dnd = [cmd: settings?.act_dnd_cmd]
                        if(settings?.act_dnd_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alexaroutine":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings?.act_EchoDevices) {
                        section(sTS("Action Description:")) {
                            paragraph pTS("This will Allow you trigger any Alexa Routines (Those with voice triggers only)", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                        }
                        def routinesAvail = parent?.getAlexaRoutines(null, true) ?: [:]
                        log.debug "routinesAvail: $routinesAvail"
                        section(sTS("Action Config:")) {
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
                        section(sTS("Action Description:")) {
                            paragraph pTS("This will allow you to change the Wake Word of your Echo's based on triggers", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                        }
                        if(devsCnt >= 1) {
                            List wakeWords = devices[0]?.hasAttribute("wakeWords") ? devices[0]?.currentValue("wakeWords")?.replaceAll('"', "")?.split(",") : []
                            // log.debug "WakeWords: ${wakeWords}"
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
                        log.debug "aCnt: ${aCnt} | devsCnt: ${devsCnt}"
                        if(settings?.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }?.size() == devsCnt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "bluetooth":
                    echoDevicesInputByPerm("bluetoothControl")
                    if(settings?.act_EchoDevices) {
                        Integer devsCnt = settings?.act_EchoDevices?.size() ?: 0
                        List devsObj = []
                        section(sTS("Action Description:")) {
                            paragraph pTS("This will allow you to connect or disconnect bluetooth based on triggers", getAppImg("info", true)), state: "complete", image: getAppImg("info")
                        }
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
                }
                section(sTS("Simulate Action")) {
                    //TODO: Add event generator based on trigger items selected.
                    //TODO: Use custom text for test
                    paragraph pTS("Perform a test of this action to see the results", getAppImg("info", true)), image: getAppImg("info")
                    input "actTestRun", "bool", title: inTS("Test this action?", getAppImg("testing", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("testing")
                    if(actTestRun) { executeActTest() }
                }
                section("") {
                    paragraph pTS("You're all done with this step.  Press Done/Save", getAppImg("done", true)), state: "complete", image: getAppImg("done")
                }
                actionExecMap?.config?.volume = [change: settings?.act_volume_change, restore: settings?.act_volume_restore, alarm: settings?.act_alarm_volume]
                // devices = parent?.getDevicesFromList(settings?.act_EchoDevices)
                actionExecMap?.delay = settings?.act_delay
                actionExecMap?.configured = true

                //TODO: Add Cleanup of non selected inputs
            } else { actionExecMap = [configured: false] }
        }
        atomicState?.actionExecMap = (done && actionExecMap?.configured == true) ? actionExecMap : [configured: false]
        log.debug "actionExecMap: ${atomicState?.actionExecMap}"

    }
}

def cleanupDevSettings(prefix) {
    List cDevs = settings?.act_EchoDevices
    List sets = settings?.findAll { it?.key?.startsWith(prefix) }?.collect { it?.key as String }
    log.debug "cDevs: $cDevs | sets: $sets"
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
    log.debug "rem: $rem"
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

def variableDesc(hideUserTxt=false) {
    Map txtItems = customTxtItems()
    if(state?.showSpeakEvtVars) {
        paragraph pTS("You are using device/location triggers.\nYou can choose to leave the text empty and text will be generated for each event.")
        String str = "You can also use variables with your text"
        str += "\n • %type% = Event Type"
        str += "\n • %value% = Event Value"
        str += "\n • %name% = Event Device"
        str += "\n • %date% = Event Date"
        str += "\n • %time% = Event Time"
        str += "\n • %datetime% = Event Date/Time"
        str += "\nContact example: %name% has been %open%"
        paragraph str, state: "complete"
    }
    if(!hideUserTxt) {
        Map txtItems = customTxtItems()
        if(txtItems?.size()) {
            String str = "<Custom Trigger Text Defined>"
            txtItems?.each { i->
                i?.value?.each { i2->
                    str += "\n \u2022 ${i?.key?.toString()?.capitalize()} ${i2?.key?.toString()?.capitalize()} Items:"
                    if(i2?.value?.size()) { i2?.value?.each { i3-> str += "\n   - ${i3}" } }
                }
                str += "\n"
            }
            paragraph str
            paragraph pTS("Notice:\nEntering text on the Actions Page will override the user defined text for each trigger type.", getAppImg("info", true)), image: getAppImg("info"), state: "complete"
        }
    }
}

def updateActionExecMap(data) {
    // log.trace "updateActionExecMap..."
    atomicState?.actionExecMap = (data && data?.configured == true) ? data : [configured: false]
    // log.debug "actionExecMap: ${state?.actionExecMap}"
}

Boolean actionsConfigured() {
    Boolean type = (settings?.actionType)
    Boolean opts = (state?.actionExecMap && state?.actionExecMap?.configured == true)
    Boolean devs = (settings?.act_EchoDevices)
    // log.debug "type: $type | Options: $opts | devs: $devs"
    return (type || opts || devs)
}

private echoDevicesInputByPerm(type) {
    List echoDevs = parent?.getChildDevicesByCap(type as String)
    section(sTS("Alexa Devices:")) {
        if(echoDevs?.size()) {
            input "act_EchoDevices", "enum", title: inTS("Echo Speaks Device(s) to Use", getAppImg("echo_gen1", true)), description: "Select the devices", options: echoDevs?.collectEntries { [(it?.getId()): it?.getLabel()] }?.sort { it?.value }, multiple: true, required: true, submitOnChange: true, image: getAppImg("echo_gen1")
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
                input "act_volume_change", "number", title: inTS("Volume Level\n(Optional)", getAppImg("speed_knob", true)), range: "0..100", required: false, submitOnChange: true, image: getAppImg("speed_knob")
                input "act_volume_restore", "number", title: inTS("Restore Volume\n(Optional)", getAppImg("speed_knob", true)), range: "0..100", required: false, submitOnChange: true, image: getAppImg("speed_knob")
            }
        }
    }
}

def condTimePage() {
    return dynamicPage(name:"condTimePage", title: "", uninstall: false) {
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
    return dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section("") { paragraph "This will uninstall the App and All Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove." }
        if(isST()) { remove("Remove ${app?.label} and Devices!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis App and Devices will be removed") }
    }
}

Boolean wordInString(String findStr, String fullStr) {
    List parts = fullStr?.split(" ")?.collect { it?.toString()?.toLowerCase() }
    return (findStr in parts)
}

def notifPrefPage() {
    return dynamicPage(name: "notifPrefPage", title: "Notifications", uninstall: false) {
        section ("Push Messages:") {
            input "usePush", "bool", title: "Send Push Notifications...", required: false, defaultValue: false, submitOnChange: true
            input "pushTimeStamp", "bool", title: "Add timestamp to Push Messages...", required: false, defaultValue: false, submitOnChange: true
        }
        section ("Text Messages:", hideWhenEmpty: true) {
            paragraph "To send to multiple numbers separate the number by a comma\nE.g. 8045551122,8046663344"
            input "smsNumbers", "text", title: "Send SMS Text to...", required: false, submitOnChange: true, image: getAppImg("sms_phone")
        }
        section ("Alexa Mobile Notification:") {
            paragraph "This will send a push notification the Alexa Mobile app."
            input "alexaMobileMsg", "text", title: "Send this message to Alexa App", required: false, submitOnChange: true, image: getAppImg("sms_phone")
        }
        section("Pushover Support:") {
            input ("pushoverEnabled", "bool", title: "Use Pushover Integration", required: false, submitOnChange: true, image: getAppImg("pushover_icon"))
            if(settings?.pushoverEnabled == true) {
                def poDevices = parent?.getPushoverDevices()
                if(!poDevices) {
                    parent?.pushover_init()
                    paragraph "If this is the first time enabling Pushover than leave this page and come back if the devices list is empty"
                } else {
                    input "pushoverDevices", "enum", title: "Select Pushover Devices", description: "Tap to select", groupedOptions: poDevices, multiple: true, required: false, submitOnChange: true, image: getAppImg("select_icon")
                    if(settings?.pushoverDevices) {
                        def t0 = [(-2):"Lowest", (-1):"Low", 0:"Normal", 1:"High", 2:"Emergency"]
                        input "pushoverPriority", "enum", title: "Notification Priority (Optional)", description: "Tap to select", defaultValue: 0, required: false, multiple: false, submitOnChange: true, options: t0, image: getAppImg("priority")
                        input "pushoverSound", "enum", title: "Notification Sound (Optional)", description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: parent?.getPushoverSounds(), image: getAppImg("sound")
                    }
                }
                // } else { paragraph "New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the SmartApps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", state: "complete" }
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
                href "setNotificationTimePage", title: "Notification Restrictions", description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("restriction")
            }
        } else { state.pushTested = false }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    unschedule()
    initialize()
}

def initialize() {
    unsubscribe()
    state?.isInstalled = true
    state?.setupComplete = true
    updAppLabel()
    runIn(3, "actionCleanup")
    runIn(7, "subscribeToEvts")
    updConfigStatusMap()
    appCleanup()
}

private updAppLabel() {
    String newLbl = "${settings?.appLbl} (Action)${(settings?.actionPause == true) ? " | (Paused)" : ""}"
    if(settings?.appLbl && app?.getLabel() != newLbl) { app?.updateLabel(newLbl) }
}

private updConfigStatusMap() {
    Map sMap = atomicState?.configStatusMap ?: [:]
    sMap?.triggers = triggersConfigured()
    sMap?.conditions = conditionsConfigured()
    sMap?.actions = actionsConfigured()
    atomicState?.configStatusMap = sMap
}

private getConfStatusItem(item) {
    return (state?.configStatusMap?.containsKey(item) && state?.configStatusMap[item] == true)
}

private appCleanup() {
    // State Cleanup
    List items = ["afterEvtMap", "afterEvtChkSchedMap", "afterCheckActiveScheduleId", "afterEvtChkSchedId"]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    // Settings Cleanup
    List setItems = ["tuneinSearchQuery", "performBroadcast", "performMusicTest"]
    settings?.each { si-> if(si?.key?.startsWith("broadcast") || si?.key?.startsWith("musicTest") || si?.key?.startsWith("announce") || si?.key?.startsWith("sequence") || si?.key?.startsWith("speechTest")) { setItems?.push(si?.key as String) } }
    // Performs the Setting Removal
    setItems?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
}

private actionCleanup() {
    //Cleans up unused action setting items
    List setItems = ["act_set_volume", "act_restore_volume"]
    List setIgn = ["act_delay", "act_volume_change", "act_volume_restore", "act_EchoDevices"]
    if(settings?.actionType) { settings?.each { si-> if(si?.key?.startsWith("act_") && !si?.key?.startsWith("act_${settings?.actionType}") && !(si?.key in setIgn)) { setItems?.push(si?.key as String) } } }
    // if(settings?.actionType in ["bluetooth", "wakeword"]) { cleanupDevSettings("act_${settings?.actionType}_device_") }
    // TODO: Cleanup unselected trigger types
    // Performs the Setting Removal
    setItems?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
}

public triggerInitialize() {
    runIn(3, "initialize")
}

public updatePauseState(Boolean pause) {
    if(settings?.actionPause != pause) {
        log.debug "Received Request to Update Pause State to (${pause})"
        settingUpdate("actionPause", "${pause}", "bool")
        runIn(4, updated())
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
    log.debug "scheduleTrigEvt | dayOk: $dOk | weekOk: $wOk | monthOk: $mOk"
    if(dOk && wOk && mOk) {
        sTripMap?.lastRun = dateMap
        atomicState?.schedTrigMap = sTrigMap
        executeAction(evt, false, null, "scheduleTrigEvt")
    }
}

private subscribeToEvts() {
    if(checkMinVersion()) { log.error "CODE UPDATE required to RESUME operation.  No events will be monitored."; return; }
    if(isPaused()) { log.warn "Action is PAUSED... No Events will be subscribed to or scheduled...." }
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

    settings?.triggerEvents?.each {
        if(settings?."trig_${it}_after") {
            runEvery1Minute("afterEvtCheckWatcher")
            return
        }
    }
}

private attributeConvert(String attr) {
    Map atts = ["door":"garageDoorControl", "carbon":"carbonMonoxide", "shade":"windowShade"]
    return (atts?.containsKey(attr)) ? atts[attr] : attr
}

private getDevEvtHandlerName(String type) {
    return (type && settings?."trig_${type}_after") ? "devAfterEvtHandler" : "deviceEvtHandler"
}


// EVENT HANDLER FUNCTIONS
def sunriseTimeHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    String custText = null
    executeAction(evt, false, custText, "sunriseTimeHandler")
}

def sunsetTimeHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    String custText = null
    executeAction(evt, false, custText, "sunsetTimeHandler")
}

def alarmEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = null
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    Boolean useAlerts = (settings?.trig_alarm == "Alerts")
    switch(evt?.name) {
        case "hsmStatus":
        case "alarmSystemStatus":
            def inc = (isST() && useAlerts) ? getShmIncidents() : null
            custText = "The ${getAlarmSystemName()} is now set to ${evt?.value}"
            executeAction(evt, false, custText, "alarmEvtHandler")
            break
        case "hsmAlert":
            custText = "A ${getAlarmSystemName()} ${evt?.displayName} alert with ${evt?.value} has occurred."
            executeAction(evt, false, custText, "alarmEvtHandler")
            break
    }
}
Integer getLastAfterEvtCheck() { return !state?.lastAfterEvtCheck ? 10000000 : GetTimeDiffSeconds(state?.lastAfterEvtCheck, "getLastAfterEvtCheck").toInteger() }


def afterEvtCheckWatcher() {
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    Map aSchedMap = atomicState?.afterEvtChkSchedMap ?: null
    if((aEvtMap?.size() == 0 && aSchedMap && aSchedMap?.id) || (aEvtMap?.size() && getLastAfterEvtCheck() > 240000)) {
        runIn(2, "afterEvtCheckHandler")
    }
}

def devAfterEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    Boolean ok = true
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    def evtDt = parseDate(evt?.date?.toString())
    String dc = settings?."trig_${evt?.name}_cmd" ?: null
    Integer dcaf = settings?."trig_${evt?.name}_after" ?: null
    Integer dcafr = settings?."trig_${evt?.name}_after_repeat" ?: null
    String eid = "${evt?.deviceId}_${evt?.name}"
    Boolean schedChk = (dc && dcaf && evt?.value == dc)
    log.trace "Device Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms | SchedCheck: (${schedChk})"
    if(aEvtMap?.containsKey(eid)) {
        if(dcaf && !schedChk) {
            aEvtMap?.remove(eid)
            log.warn "Removing ${evt?.displayName} from AfterEvtCheckMap | Reason: (${evt?.name?.toUpperCase()}) has the Desired State of (${dc}) | Remaining Items: (${aEvtMap?.size()})"
        }
    }
    ok = schedChk
    if(ok) { aEvtMap["${evt?.deviceId}_${evt?.name}"] =
        [
            dt: evt?.date?.toString(), deviceId: evt?.deviceId, displayName: evt?.displayName, name: evt?.name, value: evt?.value, triggerState: dc,
            wait: dcaf ?: null, repeat: false, repeatWait: dcafr ?: null
        ]
    }
    atomicState?.afterEvtMap = aEvtMap
    if(ok) { runIn(2, "afterEvtCheckHandler") }
}

def afterEvtCheckHandler() {
    Map aEvtMap = atomicState?.afterEvtMap ?: [:]
    if(aEvtMap?.size()) {
        // Collects all of the evt items and stores there wait values as a list
        Integer timeLeft = null
        Boolean ok2Sched = true
        Boolean isRepeat = false
        Integer lowWait = aEvtMap?.findAll { it?.value?.wait != null }?.collect { it?.value?.wait }?.min()
        Integer lowLeft = aEvtMap?.findAll { it?.value?.wait == lowWait }?.collect { it?.value?.timeLeft} ?.min()
        def nextItem = aEvtMap?.find { it?.value?.wait == lowWait && it?.value?.timeLeft == lowLeft }
        def nextVal = nextItem?.value ?: null
        def nextId = (nextVal?.deviceId && nextVal?.name) ? "${nextVal?.deviceId}_${nextVal?.name}" : null
        // log.debug "nextVal: $nextVal"
        if(nextVal) {
            def prevDt = nextVal?.repeat && nextVal?.repeatDt ? parseDate(nextVal?.repeatDt?.toString()) : parseDate(nextVal?.dt?.toString())
            def devs = settings?."trig_${nextVal?.name}" ?: null
            Boolean repeat = (settings?."trig_${nextVal?.name}_after_repeat" != null)
            if(prevDt) {
                def evtElap = (int) ((long)(new Date()?.getTime() - prevDt?.getTime())/1000)
                def reqDur = (nextVal?.repeat && nextVal?.repeatWait) ? nextVal?.repeatWait : nextVal?.wait ?: null
                timeLeft = (reqDur - evtElap)

                aEvtMap[nextItem?.key]?.timeLeft = timeLeft
                // log.info "Last ${nextVal?.displayName?.toString()?.capitalize()} (${nextVal?.name}) Event | TimeLeft: ${timeLeft} | Duration: ${evtElap} | Required: ${reqDur}"
                if(timeLeft) {
                    if(timeLeft <= 0 && nextVal?.deviceId && nextVal?.name) {
                        Boolean skipEvt = (nextVal?.triggerState && nextVal?.deviceId && nextVal?.name && devs) ? !devCapValEqual(devs, nextVal?.deviceId, nextVal?.name, nextVal?.triggerState) : true
                        if(!skipEvt) {
                            if(repeat) {
                                log.info "Last ${nextVal?.displayName?.toString()?.capitalize()} (${nextVal?.name}) Event | TimeLeft: ${timeLeft} | Duration: ${evtElap} | Required: ${reqDur}"
                                aEvtMap[nextItem?.key]?.repeatDt = formatDt(new Date())
                                aEvtMap[nextItem?.key]?.repeat = repeat
                                isRepeat = true
                                deviceEvtHandler([date: parseDate(nextVal?.repeatDt?.toString()), deviceId: nextVal?.deviceId, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value], true, true)
                            } else {
                                aEvtMap?.remove(nextId)
                                log.warn "${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) Reached the (${nextVal?.triggerState}) Threshold for (${reqDur} seconds)"
                                deviceEvtHandler([date: parseDate(nextVal?.dt?.toString()), deviceId: nextVal?.deviceId, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value], true)
                            }
                        } else {
                            aEvtMap?.remove(nextId)
                            log.info "${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) state is already ${nextVal?.triggerState} | Skipping Actions..."
                        }
                    }
                }

            }
        }
        if(ok2Sched) {
            log.debug "nextId: $nextId | timeLeft: ${timeLeft}"
            runIn(2, "scheduleAfterCheck", [data: [val:timeLeft, id:nextId, repeat: isRepeat]])
        }
        atomicState?.afterEvtMap = aEvtMap
        log.trace "afterEvtCheckHandler Remaining Items: (${aEvtMap?.size()})"
    } else { clearAfterCheckSchedule() }
    state?.lastAfterEvtCheck = getDtNow()
}

def deviceEvtHandler(evt, aftEvt=false, aftRepEvt=false) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = null
    Boolean evtOk = false
    List d = settings?."trig_${evt?.name}"
    String dc = settings?."trig_${evt?.name}_cmd"
    Boolean dco = (settings?."trig_${evt?.name}_once" == true)
    Boolean dca = (settings?."trig_${evt?.name}_all" == true)
    Integer dcw = settings?."trig_${evt?.name}_wait" ?: null
    String dct = settings?."trig_${evt?.name}_txt" ?: null
    String dcart = settings?."trig_${evt?.name}_after_repeat_txt" ?: null
    List evtTxtItems = dct ? dct?.toString()?.tokenize(";") : null
    List repeatTxtItems = dcart ? dcart?.toString()?.tokenize(";") : null
    log.trace "Device Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms${aftEvt ? " | (AfterEvt)" : ""}"
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
                if(dc == "any") {
                    evtOk = true
                    if(aftRepEvt) {
                        custText = (repeatTxtItems?.size()) ? decodeVariables(evt, getRandomItem(repeatTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value}"
                    } else {
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value}"
                    }
                } else {
                    if(dca && (allDevEqCapVal(d, dc, evt?.value))) {
                        evtOk = true
                        if(d?.size() > 1) {
                            if(aftRepEvt) {
                                custText = (repeatTxtItems?.size()) ? decodeVariables(evt, getRandomItem(repeatTxtItems)) : "All ${d?.size()}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} devices are ${evt?.value}"
                            } else {
                                custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "All ${d?.size()}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} devices are ${evt?.value}"
                            }
                        } else {
                            if(aftRepEvt) {
                                custText = (repeatTxtItems?.size()) ? decodeVariables(evt, getRandomItem(repeatTxtItems)) : "All ${d?.size()}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} devices are ${evt?.value}"
                            } else {
                                custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value}"
                            }
                        }
                    } else {
                        if(evt?.value == dc) {
                            evtOk=true
                            if(aftRepEvt) {
                                custText = (repeatTxtItems?.size()) ? decodeVariables(evt, getRandomItem(repeatTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value}"
                            } else {
                                custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${evt?.value}"
                            }
                        }
                    }
                }
            }
            break

        case "humidity":
        case "temperature":
        case "power":
        case "illuminance":
        case "level":
            Double dcl = settings?."trig_${evt?.name}_low"
            Double dch = settings?."trig_${evt?.name}_high"
            Double dce = settings?."trig_${evt?.name}_equal"
            Map valChk = deviceEvtProcNumValue(evt, d, dc, dcl, dch, dce, dca)
            evtOk = valChk?.evtOk
            custText = valChk?.custText
            break
    }
    if(evtOk && devEvtWaitOk) {
        executeAction(evt, false, custText, "deviceEvtHandler(${evt?.name})")
    }
}

Map deviceEvtProcNumValue(evt, List devs = null, String cmd = null, Double dcl = null, Double dch = null, Double dce = null, Boolean dca = false) {
    String custText = null
    Boolean evtOk = false
    // log.debug "deviceEvtProcNumValue | cmd: ${cmd} | low: ${dcl} | high: ${dch} | equal: ${dce} | all: ${dca}"
    if(devs?.size() && cmd && evt?.value?.isNumber()) {
        String postfix = getAttrPostfix(evt?.name)
        def v = evtValueCleanup(evt?.value)
        String dct = settings?."trig_${evt?.name}_txt" ?: null
        List evtTxtItems = dct ? dct?.toString()?.tokenize(";") : null
        switch(cmd) {
            case "equals":
                if(!dca && dce && dce?.toDouble() == evt?.value?.toDouble()) {
                    evtOk=true
                } else if(dca && dce && allDevCapValsEqual(devs, evt?.name, dce)) { evtOk=true }
                break
            case "between":
                if(!dca && dcl && dch && (evt?.value?.toDouble() in (dcl..dch))) {
                    evtOk=true
                } else if(dca && dcl && dch && allDevCapValsBetween(devs, evt?.name, dcl, dch)) { evtOk=true }
                break
            case "above":
                if(!dca && dch && (evt?.value?.toDouble() > dch)) {
                    evtOk=true
                } else if(dca && dch && allDevCapValsAbove(devs, evt?.name, dch)) { evtOk=true }
                break
            case "below":
                if(dcl && (evt?.value?.toDouble() < dcl)) {
                    evtOk=true
                } else if(dca && dcl && allDevCapValsBelow(devs, evt?.name, dcl)) { evtOk=true }
                break
        }
        if(evtOk) {
            if(dca) {
                custText = "All ${devs?.size()} ${evt?.name} devices are now ${v} ${postfix}"
            } else {
                switch(evt?.name) {
                    case "thermostatFanMode":
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Fan Mode is ${v} ${postfix}"
                        break
                    case "thermostatMode":
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Mode is ${v} ${postfix}"
                        break
                    case "coolSetpoint":
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Cool Setpoint is ${v} ${postfix}"
                        break
                    case "heatSetpoint":
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Heat Setpoint is ${v} ${postfix}"
                        break
                    case "thermostatOperatingState":
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Operating State is ${v} ${postfix}"
                        break
                    default:
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} is ${v} ${postfix}"
                        break
                }
            }
        }
    }
    return [evtOk: evtOk, custText: custText]
}

def thermostatEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = null
    Boolean evtOk = false
    List d = settings?."trig_${evt?.name}"
    String dc = settings?."trig_${evt?.name}_cmd"
    Boolean dco = (settings?."trig_${evt?.name}_once" == true)
    Integer dcw = settings?."trig_${evt?.name}_wait" ?: null
    log.trace "Thermostat Event | ${evt?.name?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    Boolean devEvtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)
    String dct = settings?."trig_${evt?.name}_txt" ?: null
    List evtTxtItems = dct ? dct?.toString()?.tokenize(";") : null

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
                            custText = valChk?.custText
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
                    custText = valChk?.custText
                }
                break

            case "mode":
            case "operatingstate":
            case "fanmode":
                if(evt?.name == "thermostatMode") {
                    String dmc = settings?.trig_thermostat_fanmode_cmd ?: null
                    if(dmc == "any" || evt?.value == dmc) {
                        evtOk=true
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Mode is ${evt?.value}"
                    }
                }

                if(evt?.name == "thermostatOperatingState") {
                    String doc = settings?.trig_thermostat_state_cmd ?: null
                    if(doc == "any" || evt?.value == doc) {
                        evtOk=true
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Operating State is ${evt?.value}"
                    }
                }

                if(evt?.name == "thermostatFanMode") {
                    String dfc = settings?.trig_thermostat_mode_cmd ?: null
                    if(dfc == "any" || evt?.value == dfc) {
                        evtOk=true
                        custText = (evtTxtItems?.size()) ? decodeVariables(evt, getRandomItem(evtTxtItems)) : "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evt?.name) ? " ${evt?.name}" : ""} Fan Mode is ${evt?.value}"
                    }
                }
                break
        }
    }
    if(evtOk && devEvtWaitOk) {
        executeAction(evt, false, custText, "thermostatEvtHandler(${evt?.name})")
    }
}

String evtValueCleanup(val) {
    return (val?.toString()?.endsWith(".0")) ? val?.toString()?.substring(0, val?.toString()?.length() - 2) : val
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
            log.info "Last ${evt?.name?.toString()?.capitalize()} Event for Device Occurred: (${dur} sec ago) | Desired Wait: (${wait} sec) - Status: (${waitOk ? "OK" : "Block"}) | OnceDaily: (${once}) - Status: (${dayOk ? "OK" : "Block"})"
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
    def evtDelay = now() - evt?.date?.getTime()
    String custText = "The ${evt?.displayName} routine was just executed!."
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    executeAction(evt, false, custText, "routineEvtHandler")
}

def sceneEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = "The ${evt?.displayName} scene was just activated!."
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    executeAction(evt, false, custText, "sceneEvtHandler")
}

def modeEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = "The location mode is now set to ${evt?.value}"
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    executeAction(evt, false, "The location mode is now set to ${evt?.value}", "modeEvtHandler")
}

def locationEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
    String custText = null
    log.trace "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms"
    executeAction(evt, false, custText, "locationEvtHandler")
}

def weatherCheckHandler() {
    // def alerts = getTwcAlerts()
    // log.debug "alerts: ${alerts}"

    // def alerts = getTwcAlerts()
    // log.debug "alerts: ${alerts}"

    // executeAction(evt, false, custText, "locationEvtHandler")
}

def scheduleAfterCheck(data) {
    Integer val = data?.val ? (data?.val < 2 ? 2 : data?.val) : 60
    String id = data?.id ?: null
    Boolean rep = (data?.repeat == true)
    Map aSchedMap = atomicState?.afterEvtChkSchedMap ?: null
    if(aSchedMap && aSchedMap?.id && id && aSchedMap?.id == id) {
        log.debug "Active Schedule Id (${aSchedMap?.id}) is the same as the requested schedule ${id}."
    }
    runIn(val, "afterEvtCheckHandler")
    atomicState?.afterEvtChkSchedMap = [id: id, dur: val, dt: getDtNow()]
    log.debug "Schedule After Event Check${rep ? " (Repeat)" : ""} for (${val} seconds) | Id: ${id}"
}

private clearAfterCheckSchedule() {
    log.error "Clearing After Event Check Schedule..."
    atomicState?.afterEvtChkSchedMap = null
    unschedule("afterEvtCheckHandler")
    // unschedule("afterEvtCheckWatcher")
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
    // log.debug "locationCondOk | modeOk: $mOk | alarmOk: $aOk"
    return (mOk && aOk)
}

Boolean checkDeviceCondOk(type) {
    def devs = settings?."cond_${type}" ?: null
    def cmdVal = settings?."cond_${type}_cmd" ?: null
    if( !(type && devs && cmdVal) ) { return true }
    return settings?."cond_${type}_all" ? allDevEqCapVal(devs, type, cmdVal) : anyDevCapValEqual(devs, type, cmdVal)
}

Boolean checkDeviceNumCondOk(type) {
    List devs = settings?."cond_${type}" ?: null
    String cmd = settings?."cond_${type}_cmd" ?: null
    Double cdv = settings?."cond_${type}"
    Double dcl = settings?."cond_${type}_low"
    Double dch = settings?."cond_${type}_high"
    Double dce = settings?."cond_${type}_equal"
    Double dca = settings?."cond_${type}_all"
    if( !(type && devs && cmd) ) { return true }

    switch(cmd) {
        case "equals":
            if(dce) {
                if(dca) { return allDevCapValsEqual(devs, type, dce) }
                else { return anyDevCapValEqual(devs, type, dce) }
            }
            return true
            break
        case "between":
            if(dcl && dch) {
                if(dca) { return allDevCapValsBetween(devs, type, dcl, dch) }
                else { return anyDevCapValBetween(devs, type, dcl, dch) }
            }
            return true
            break
        case "above":
            if(dch) {
                if(dca) { return allDevCapValsAbove(devs, type, dch) }
                else { return anyDevCapValAbove(devs, type, dch) }
            }
            return true
            break
        case "below":
            if(dcl) {
                if(dca) { return allDevCapValsBelow(devs, type, dcl) }
                else { return anyDevCapValBelow(devs, type, dcl) }
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
    // log.debug "checkDeviceCondOk | switchOk: $swDevOk | motionOk: $motDevOk | presenceOk: $presDevOk | contactOk: $conDevOk | lockOk: $lockDevOk | garageOk: $garDevOk"
    return (swDevOk && motDevOk && presDevOk && conDevOk && lockDevOk && garDevOk && tempDevOk && humDevOk)
}

def allConditionsOk() {
    def timeOk = timeCondOk()
    def dateOk = dateCondOk()
    def locOk = locationCondOk()
    def devOk = deviceCondOk()
    log.debug "Action Conditions Check | Time: ($timeOk) | Date: ($dateOk) | Location: ($locOk) | Devices: ($devOk)"
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
    executeAction([name: "contact", displayName: "Front Door", value: "open", date: new Date()], true, null, "executeActTest")
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
        if(str?.contains("%type%") && str?.contains("%name%")) {
            str = (str?.contains("%type%") && evt?.name) ? str?.replaceAll("%type%", !evt?.displayName?.toLowerCase()?.contains(evt?.name) ? convEvtType(evt?.name) : "") : str
            str = (str?.contains("%name%") && evt?.displayName) ? str?.replaceAll("%name%", evt?.displayName) : str
        } else {
            str = (str?.contains("%type%") && evt?.name) ? str?.replaceAll("%type%", convEvtType(evt?.name)) : str
            str = (str?.contains("%name%") && evt?.displayName) ? str?.replaceAll("%name%", evt?.displayName) : str
        }
        str = (str?.contains("%value%") && evt?.value) ? str?.replaceAll("%value%", evt?.value) : str
        str = (str?.contains("%date%") && evt?.date) ? str?.replaceAll("%date%", convToDate(evt?.date)) : str
        str = (str?.contains("%time%") && evt?.date) ? str?.replaceAll("%time%", convToTime(evt?.date)) : str
        str = (str?.contains("%datetime%") && evt?.date) ? str?.replaceAll("%datetime%", convToDateTime(evt?.date)) : str
    }
    return str
}

private executeAction(evt = null, frc=false, custText=null, src=null) {
    def startTime = now()
    log.trace "executeAction${src ? "($src)" : ""}${frc ? " | [Forced]" : ""}..."
    if(isPaused()) { log.warn "Action is PAUSED... Skipping Action Execution..."; return; }
    Boolean condOk = allConditionsOk()
    Boolean actOk = getConfStatusItem("actions")
    Map actMap = state?.actionExecMap ?: null
    def actDevices = parent?.getDevicesFromList(settings?.act_EchoDevices)
    String actType = settings?.actionType
    if(actOk && actType) {
        if(!condOk) { log.warn "Skipping Execution because set conditions have not been met"; return; }
        if(!actMap || !actMap?.size()) { log.error "executeAction Error | The ActionExecutionMap is not found or is empty"; return; }
        if(!actDevices?.size()) { log.error "executeAction Error | No Echo Device List is not found or is empty"; return; }
        if(!actMap?.actionType) { log.error "executeAction Error | The ActionType is not found or is empty"; return; }
        Map actConf = actMap?.config
        Integer actDelay = actMap?.delay ?: 0
        Integer actDelayMs = actMap?.delay ? (actMap?.delay*1000) : 0
        Integer changeVol = actMap?.config?.volume?.change as Integer ?: null
        Integer restoreVol = actMap?.config?.volume?.restore as Integer ?: null
        Integer alarmVol = actMap?.config?.volume?.alarm ?: null

        switch(actType) {
            //Speak Command Logic
            case "speak":
                if(actConf[actType]) {
                    String txt = null
                    if(actConf[actType]?.text) {
                        txt = evt ? (decodeVariables(evt, actConf[actType]?.text)) : actConf[actType]?.text
                    } else {
                        if(evt && custText && actConf[actType]?.evtText) { txt = custText }
                        else { txt = "Invalid Text Received... Please verify Action configuration..." }
                    }
                    if(changeVol || restoreVol) {
                        actDevices?.each { dev-> dev?.setVolumeSpeakAndRestore(changeVol, txt, restoreVol) }
                    } else {
                        actDevices?.each { dev-> dev?.speak(txt) }
                    }
                    log.debug "Sending Speak Command: (${txt}) to ${actDevices}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}"
                }
                break

            //Announcement Command Logic
            case "announcement":
                if(actConf[actType] && actConf[actType]?.text) {
                    String txt = null
                    if(actConf[actType]?.text) {
                        txt = evt ? (decodeVariables(evt, actConf[actType]?.text)) : actConf[actType]?.text
                    } else {
                        if(evt && custText && actConf[actType]?.evtText) { txt = custText }
                        else { txt = "Invalid Text Received... Please verify Action configuration..." }
                    }
                    if(actDevices?.size() > 1 && actConf[actType]?.deviceObjs && actConf[actType]?.deviceObjs?.size()) {
                        //NOTE: Only sends command to first device in the list | We send the list of devices to announce one and then Amazon does all the processing
                        def devJson = new groovy.json.JsonOutput().toJson(actConf[actType]?.deviceObjs)
                        actDevices[0]?.sendAnnouncementToDevices(txt, (app?.getLabel() ?: "Echo Speaks Action"), devJson, changeVol, restoreVol, [delay: actDelayMs])
                        log.debug "Sending Announcement Command: (${txt}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}"
                    } else {
                        actDevices?.each { dev-> dev?.playAnnouncement(txt, (app?.getLabel() ?: "Echo Speaks Action"), changeVol, restoreVol, [delay: actDelayMs]) }
                        log.debug "Sending Announcement Command: (${txt}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}"
                    }
                }
                break

            case "sequence":
                if(actConf[actType] && actConf[actType]?.text) {
                    actDevices?.each { dev-> dev?.executeSequenceCommand(actConf[actType]?.text as String, [delay: actDelayMs]) }
                    log.debug "Sending Sequence Command: (${actConf[actType]?.text}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}"
                }
                break

            case "playback":
            case "dnd":
                if(actConf[actType] && actConf[actType]?.cmd) {
                    actDevices?.each { dev-> dev?."${actConf[actType]?.cmd}"([delay: actDelayMs]) }
                    log.debug "Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}"
                }
                break

            case "builtin":
            case "calendar":
            case "weather":
                if(actConf[actType] && actConf[actType]?.cmd) {
                    if(changeVol || restoreVol) {
                        actDevices?.each { dev-> dev?."${actConf[actType]?.cmd}"(changeVolume, restoreVol, [delay: actDelayMs]) }
                        log.debug "Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}"
                    } else {
                        actDevices?.each { dev-> dev?."${actConf[actType]?.cmd}"([delay: actDelayMs]) }
                        log.debug "Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}"
                    }
                }
                break

            case "alarm":
            case "reminder":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.label && actConf[actType]?.date && actConf[actType]?.time) {
                    actDevices?.each { dev-> dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time, [delay: actDelayMs]) }
                    log.debug "Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices} | Label: ${actConf[actType]?.label} | Date: ${actConf[actType]?.date} | Time: ${actConf[actType]?.time}"
                }
                break

            case "music":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.provider && actConf[actType]?.search) {
                    actDevices?.each { dev-> dev?."${actConf[actType]?.cmd}"(actConf[actType]?.search, convMusicProvider(actConf[actType]?.provider), changeVol, restoreVol, [delay: actDelayMs]) }
                    log.debug "Sending ${actType?.toString()?.capitalize()} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}${changeVol ? " | Volume: ${changeVol}" : ""}${restoreVol ? " | Restore Volume: ${restoreVol}" : ""}"
                }
                break

            case "alexaroutine":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.routineId) {
                    actDevices[0]?."${actConf[actType]?.cmd}"(actConf[actType]?.routineId as String)
                    log.debug "Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) | RoutineId: ${actConf[actType]?.routineId} to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : ""}"
                }
                break

            case "wakeword":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id == d?.device }
                        aDev?."${d?.cmd}"(d?.wakeword, [delay: actDelayMs])
                        log.debug "Sending WakeWord: (${d?.wakeword}) | Command: (${d?.cmd}) to ${aDev}${actDelay ? " | Delay: (${actDelay})" : ""}"
                    }
                }
                break

            case "bluetooth":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id == d?.device }
                        if(d?.cmd == "disconnectBluetooth") {
                            aDev?."${d?.cmd}"([delay: actDelayMs])
                        } else { aDev?."${d?.cmd}"(d?.btDevice, [delay: actDelayMs]) }
                        log.debug "Sending ${d?.cmd} | Bluetooth Device: ${d?.btDevice} to ${aDev}${actDelay ? " | Delay: (${actDelay})" : ""}"
                    }
                }
                break
        }
    }

    log.trace "ExecuteAction Finished | ProcessTime: (${now()-startTime}ms)"
}


/***********************************************************************************************************************
    WEATHER ALERTS
***********************************************************************************************************************/
def mGetWeatherAlerts() {
    def data = [:]
    try {
        def weather = getTwcAlerts()
        def type = weather?.alerts?.type[0]
        def alert = weather?.alerts?.description[0]
        def expire = weather?.alerts?.expires[0]
        def typeOk = settings?.myWeatherAlert?.find {a -> a == type}
        if (typeOk) {
            if (expire != null) { expire = expire?.replaceAll(~/ EST /, " ")?.replaceAll(~/ CST /, " ")?.replaceAll(~/ MST /, " ")?.replaceAll(~/ PST /, " ") }
            if (alert != null) {
                result = alert  + " is in effect for your area, that expires at " + expire
                if (state?.weatherAlert == null) {
                    state?.weatherAlert = result
                    state?.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
                    data = [value: result, name: "weather alert", device:"weather"]
                    alertsHandler(data)
                } else {
                    if (state?.showDebug) { log.debug "new weather alert = ${alert}, expire = ${expire}" }
                    def newAlert = result != state?.weatherAlert ? true : false
                    if (newAlert == true) {
                        state?.weatherAlert = result
                        state?.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
                        data = [value: result, name: "weather alert", device:"weather"]
                        alertsHandler(data)
                    }
                }
            }
        } else if (firstTime == true) {
            data = [value: result, name: "weather alert", device:"weather"]
            alertsHandler(data)
        }
    } catch (Throwable t) {
        log.error t
        return result
    }
}





/***********************************************************************************************************
   HELPER UTILITES
************************************************************************************************************/

void settingUpdate(name, value, type=null) {
    if(name && type) { app?.updateSetting("$name", [type: "$type", value: value]) }
    else if (name && type == null) { app?.updateSetting(name.toString(), value) }
}

void settingRemove(String name) {
    logger("trace", "settingRemove($name)...")
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

Boolean pushStatus() { return (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || settings?.pushoverEnabled) ? ((settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) ? "Push Enabled" : "Enabled") : null }
Integer getLastMsgSec() { return !state?.lastMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastMsgDt, "getLastMsgSec").toInteger() }
Integer getLastUpdMsgSec() { return !state?.lastUpdMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastUpdMsgDt, "getLastUpdMsgSec").toInteger() }
Integer getLastMisPollMsgSec() { return !state?.lastMisPollMsgDt ? 100000 : GetTimeDiffSeconds(state?.lastMisPollMsgDt, "getLastMisPollMsgSec").toInteger() }
Integer getLastVerUpdSec() { return !state?.lastVerUpdDt ? 100000 : GetTimeDiffSeconds(state?.lastVerUpdDt, "getLastVerUpdSec").toInteger() }
Integer getLastDevicePollSec() { return !state?.lastDevDataUpd ? 840 : GetTimeDiffSeconds(state?.lastDevDataUpd, "getLastDevicePollSec").toInteger() }
Integer getLastCookieChkSec() { return !state?.lastCookieChkDt ? 3600 : GetTimeDiffSeconds(state?.lastCookieChkDt, "getLastCookieChkSec").toInteger() }
Integer getLastChildInitRefreshSec() { return !state?.lastChildInitRefreshDt ? 3600 : GetTimeDiffSeconds(state?.lastChildInitRefreshDt, "getLastChildInitRefreshSec").toInteger() }
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

String getCurrentMode() {
    return location?.mode
}

List getLocationModes(Boolean sorted=false) {
    List modes = location?.modes*.name
    log.debug "modes: ${modes}"
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

Boolean allDevEqCapVal(List devs, String cap, val) {
    if(devs) { return (devs?.findAll { it?."current${cap?.capitalize()}" == val }?.size() == devs?.size()) }
    return false
}

Boolean anyDevCapValAbove(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() > val?.toDouble() }?.size() >= 1) : false
}
Boolean anyDevCapValBelow(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() < val?.toDouble() }?.size() >= 1) : false
}
Boolean anyDevCapValBetween(List devs, String cap, low, high) {
    return (devs && cap && low && high) ? (devs?.findAll { ( (it?."current${cap?.capitalize()}"?.toDouble() > low?.toDouble()) && (it?."current${cap?.capitalize()}"?.toDouble() < high?.toDouble()) ) }?.size() >= 1) : false
}
Boolean anyDevCapValEqual(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() == val?.toDouble() }?.size() >= 1) : false
}

Boolean allDevCapValsAbove(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() > val?.toDouble() }?.size() == devs?.size()) : false
}
Boolean allDevCapValsBelow(List devs, String cap, val) {
    return (devs && cap && val) ? (devs?.findAll { it?."current${cap?.capitalize()}"?.toDouble() < val?.toDouble() }?.size() == devs?.size()) : false
}
Boolean allDevCapValsBetween(List devs, String cap, low, high) {
    return (devs && cap && low && high) ? (devs?.findAll { ( (it?."current${cap?.capitalize()}"?.toDouble() > low?.toDouble()) && (it?."current${cap?.capitalize()}"?.toDouble() < high?.toDouble()) ) }?.size() == devs?.size()) : false
}
Boolean allDevCapValsEqual(List devs, String cap, val) {
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
        log.error "GetTimeDiffSeconds Exception: (${sender ? "$sender | " : ""}lastDate: $lastDate):", ex
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
                        str += settings?."${sPre}${evt}_time"     ? "   \u25E6 Time: (${fmtTime(settings?."${sPre}${evt}_time")})\n"      : ""
                        str += settings?."${sPre}${evt}_sunState" ? "   \u25E6 SunState: (${settings?."${sPre}${evt}_sunState"})\n"       : ""
                        str += settings?."${sPre}${evt}_days"     ? "   \u25E6 (${settings?."${sPre}${evt}_days"?.size()}) Days\n"      : ""
                        str += settings?."${sPre}${evt}_weeks"    ? "   \u25E6 (${settings?."${sPre}${evt}_weeks"?.size()}) Weeks\n"    : ""
                        str += settings?."${sPre}${evt}_months"   ? "   \u25E6 (${settings?."${sPre}${evt}_months"?.size()}) Months\n"  : ""
                        break
                    case "alarm":
                        str += " \u2022 ${evt?.capitalize()} (${getAlarmSystemName(true)})${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings?."${sPre}${evt}_once" ? "   \u25E6 Once a Day: (${settings?."${sPre}${evt}_once"})\n" : ""
                        break
                    case "routineExecuted":
                    case "mode":
                    case "scene":
                        str += " \u2022 ${evt == "routineExecuted" ? "Routines" : evt?.capitalize()}${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings?."${sPre}${evt}_once" ? "   \u25E6 Once a Day: (${settings?."${sPre}${evt}_once"})\n" : ""
                        break
                    default:
                        str += " \u2022 ${evt?.capitalize()}${settings?."${sPre}${evt}" ? " (${settings?."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        def subStr = ""
                        if(settings?."${sPre}${evt}_cmd" in ["above", "below", "equal", "between"]) {
                            if (settings?."${sPre}${evt}_cmd" == "between") {
                                str += settings?."${sPre}${evt}_cmd"  ? "   \u25E6 ${settings?."${sPre}${evt}_cmd"}: (${settings?."${sPre}${evt}_low"} - ${settings?."${sPre}${evt}_high"})\n" : ""
                            } else {
                                str += (settings?."${sPre}${evt}_cmd" == "above" && settings?."${sPre}${evt}_high")     ? "   \u25E6 Above: (${settings?."${sPre}${evt}_high"})\n" : ""
                                str += (settings?."${sPre}${evt}_cmd" == "below" && settings?."${sPre}${evt}_low")      ? "   \u25E6 Below: (${settings?."${sPre}${evt}_low"})\n" : ""
                                str += (settings?."${sPre}${evt}_cmd" == "equal" && settings?."${sPre}${evt}_equal")    ? "   \u25E6 Equals: (${settings?."${sPre}${evt}_equal"})\n" : ""
                            }
                        } else {
                            str += settings?."${sPre}${evt}_cmd"  ? "    \u25E6 Trigger State: (${settings?."${sPre}${evt}_cmd"})\n" : ""
                        }
                        str += settings?."${sPre}${evt}_after"              ? "    \u25E6 Only After: (${settings?."${sPre}${evt}_after"} min)\n" : ""
                        str += settings?."${sPre}${evt}_after_repeat"       ? "    \u25E6 Repeat Every: (${settings?."${sPre}${evt}_after_repeat"} min)\n" : ""
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
    Boolean confd = actionsConfigured()
    def time = null
    String sPre = "act_"
    if(confd) {
        String str = "Actions:${settings?.act_EchoDevices ? " (${settings?.act_EchoDevices?.size()} Device${settings?.act_EchoDevices?.size() > 1 ? "s" : ""})" : ""}\n"
        str += " • ${settings?.actionType?.capitalize()}\n"
        // str += settings?."act_${settings?.actionType}_cmd" ? " • Cmd: (${settings?."act_${settings?.actionType}_cmd"})\n" : ""
        str += settings?.act_volume_change ? " • Set Volume: (${settings?.act_volume_change})\n" : ""
        str += settings?.act_volume_restore ? " • Restore Volume: (${settings?.act_volume_restore})\n" : ""
        str += settings?.act_delay ? " • Delay: (${settings?.act_delay})\n" : ""
        str += "\ntap to modify..."
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
    log.debug "randomString: ${randChars?.join()}"
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
        log.debug "hubPlatform: (${state?.hubPlatform})"
    }
    return state?.hubPlatform
}

String getAppImg(String imgName, frc=false) { return (frc || isST()) ? "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/icons/${imgName}.png" : "" }
String getPublicImg(String imgName) { return isST() ? "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" : "" }
String sTS(String t, String i = null) { return isST() ? t : """<h3>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", "<br> ")}</h3>""" }
String inTS(String t, String i = null) { return isST() ? t : """${i ? """<img src="${i}" width="42"> """ : ""} <u>${t?.replaceAll("\\n", " ")}</u>""" }
String pTS(String t, String i = null) { return isST() ? t : """<b>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", "<br> ")}</b>""" }
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

private logger(type, msg, traceOnly=false) {
    if (traceOnly && !settings?.appTrace) { return }
    if(type && msg && settings?.appDebug) {
        log."${type}" "${msg}"
    }
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


/***********************************************************************************************************************
    CUSTOM WEATHER VARIABLES
***********************************************************************************************************************/
private getWeatherVar(eTxt) {
    def result
    // weather variables
    def weatherToday = mGetWeatherVar("today")
    def weatherTonight = mGetWeatherVar("tonight")
    def weatherTomorrow = mGetWeatherVar("tomorrow")
    def tHigh = mGetWeatherVar("high")
    def tLow = mGetWeatherVar("low")
    def tUV = mGetWeatherElements("uv")
    def tPrecip = mGetWeatherElements("precip")
    def tHum = mGetWeatherElements("hum")
    def tCond = mGetWeatherElements("cond")
    def tWind = mGetWeatherElements("wind")
    def tSunset = mGetWeatherElements("set")
    def tSunrise = mGetWeatherElements("rise")
    def tTemp = mGetWeatherElements("current")
    //def tWind = mGetWeatherElements("moonphase")

    result = eTxt.replace("&today", "${weatherToday}")?.replace("&tonight", "${weatherTonight}")?.replace("&tomorrow", "${weatherTomorrow}")
    if (result) { result = result.replace("&high", "${tHigh}")?.replace("&low", "${tLow}")?.replace("&wind", "${tWind}")?.replace("&uv", "${tUV}")?.replace("&precipitation", "${tPrecip}") }
    if (result) { result = result.replace("&humidity", "${tHum}")?.replace("&conditions", "${tCond}")?.replace("&set", "${tSunset}")?.replace("&rise", "${tSunrise}")?.replace("&current", "${tTemp}") }

    return result
}
/***********************************************************************************************************************
    WEATHER TRIGGERS
***********************************************************************************************************************/
def mGetWeatherTrigger() {
    def data = [:]
    def myTrigger
    def process = false
    try {
        if (getMetric() == false) {
            def cWeather = getWeatherFeature("conditions", state?.wZipCode)
            def cTempF = cWeather?.current_observation?.temp_f.toDouble()
            int tempF = cTempF as Integer
            def cRelativeHum = cWeather?.current_observation?.relative_humidity
            cRelativeHum = cRelativeHum?.replaceAll("%", "")
            int humid = cRelativeHum as Integer
            def cWindGustM = cWeather?.current_observation?.wind_gust_mph?.toDouble()
            int wind = cWindGustM as Integer
            def cPrecipIn = cWeather?.current_observation?.precip_1hr_in?.toDouble()
            double precip = cPrecipIn //as double
                precip = 1 + precip //precip
            if (state?.showDebug) { log.debug "current triggers: precipitation = $precip, humidity = $humid, wind = $wind, temp = $tempF" }
            myTrigger = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? precip : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? wind : settings?.myWeatherTriggers == "Humidity (%)" ? humid : settings?.myWeatherTriggers == "Temperature (F/C)" ? tempF : null
        } else {
            def cWeather = getWeatherFeature("conditions", state?.wZipCode)
            def cTempC = cWeather?.current_observation?.temp_c?.toDouble()
                int tempC = cTempC as Integer
            def cRelativeHum = cWeather?.current_observation?.relative_humidity
                cRelativeHum = cRelativeHum?.replaceAll("%", "")
                int humid = cRelativeHum as Integer
            def cWindGustK = cWeather?.current_observation?.wind_gust_kph?.toDouble()
                int windC = cWindGustK as Integer
            def cPrecipM = cWeather?.current_observation?.precip_1hr_metric?.toDouble()
                double  precipC = cPrecipM as double

            myTrigger = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? precipC : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? windC : settings?.myWeatherTriggers == "Humidity (%)" ? humid : settings?.myWeatherTriggers == "Temperature (F/C)" ? tempC : null
        }
        def myTriggerName = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? "Precipitation" : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? "Wind Gusts" : settings?.myWeatherTriggers == "Humidity (%)" ? "Humidity" : settings?.myWeatherTriggers == "Temperature (F/C)" ? "Temperature" : null
        if (settings?.myWeatherTriggersS == "above" && state.cycleOnA == false) {
            def var = myTrigger > settings?.myWeatherThreshold
            if (state?.showDebug) { log.debug  " myTrigger = $myTrigger, myWeatherThreshold = ${settings?.myWeatherThreshold}, myWeatherTriggersS = ${settings?.myWeatherTriggersS}, var = $var" }
            if (myTrigger > settings?.myWeatherThreshold) {
                process = true
                state.cycleOnA = process
                state.cycleOnB = false
            }
        }
        if (settings?.myWeatherTriggersS == "below" && state.cycleOnB == false) {
            def var = myTrigger <= settings?.myWeatherThreshold
            if (state?.showDebug) { log.debug  " myTrigger = $myTrigger, myWeatherThreshold = ${settings?.myWeatherThreshold} myWeatherTriggersS = ${settings?.myWeatherTriggersS}, var = $var" }
            if (myTrigger <= settings?.myWeatherThreshold) {
                process = true
                state.cycleOnA = false
                state.cycleOnB = process
            }
        }
        if (process == true) {
            //data = [value:"${myTrigger}", name:"${settings?.myWeatherTriggers}", device:"${settings?.myWeatherTriggers}"] 4/5/17 Bobby
            data = [value:"${myTrigger}", name:"weather", device:"${myTriggerName}"]
            alertsHandler(data)
        }
    } catch (Throwable t) {
        log.error t
        return result
    }
}

/***********************************************************************************************************************
    HOURLY FORECAST
***********************************************************************************************************************/
def mGetCurrentWeather() {
    def weatherData = [:]
    def data = [:]
       def result
    try {
        //hourly updates
        def cWeather = getWeatherFeature("hourly", state?.wZipCode)
        def cWeatherCondition = cWeather?.hourly_forecast[0]?.condition
        def cWeatherPrecipitation = cWeather?.hourly_forecast[0]?.pop + " percent"
        def cWeatherWind = cWeather?.hourly_forecast[0]?.wspd?.english + " miles per hour"
        def cWeatherWindC = cWeather?.hourly_forecast[0]?.wspd?.metric + " kilometers per hour"
        if (getMetric() == true) { cWeatherWind = cWeatherWindC }
        def cWeatherHum = cWeather?.hourly_forecast[0]?.humidity + " percent"
        def cWeatherUpdate = cWeather?.hourly_forecast[0]?.FCTTIME?.civil
        //past hour's data
        def pastWeather = state.lastWeather
        //current forecast
            weatherData.wCond = cWeatherCondition
            weatherData.wWind = cWeatherWind
            weatherData.wHum = cWeatherHum
            weatherData.wPrecip = cWeatherPrecipitation
        //last weather update
        def lastUpdated = new Date(now()).format("h:mm aa", location.timeZone)
        if (settings?.myWeather) {
            if (pastWeather == null) {
                state.lastWeather = weatherData
                state.lastWeatherCheck = lastUpdated
                result = "hourly weather forcast notification has been activated at " + lastUpdated + " You will now receive hourly weather updates, only if the forecast data changes"
                data = [value: result, name: "weather alert", device: "weather"]
                alertsHandler(data)
            } else {
                def wUpdate = pastWeather.wCond != cWeatherCondition ? "current weather condition" : pastWeather.wWind != cWeatherWind ? "wind intensity" : pastWeather.wHum != cWeatherHum ? "humidity" : pastWeather.wPrecip != cWeatherPrecipitation ? "chance of precipitation" : null
                def wChange = wUpdate == "current weather condition" ? cWeatherCondition : wUpdate == "wind intensity" ? cWeatherWind  : wUpdate == "humidity" ? cWeatherHum : wUpdate == "chance of precipitation" ? cWeatherPrecipitation : null
                //something has changed
                if (wUpdate != null) {
                    // saving update to state
                    state.lastWeather = weatherData
                    state.lastWeatherCheck = lastUpdated
                    if (settings?.myWeather == "Any Weather Updates") {
                        def condChanged = pastWeather.wCond != cWeatherCondition
                        def windChanged = pastWeather.wWind != cWeatherWind
                        def humChanged = pastWeather.wHum != cWeatherHum
                        def precChanged = pastWeather.wPrecip != cWeatherPrecipitation
                        if (condChanged) {
                            result = "The hourly weather forecast has been updated. The weather condition has been changed to "  + cWeatherCondition
                        }
                        if (windChanged) {
                            if (result) { result = result +  ", the wind intensity to "  + cWeatherWind }
                            else { result = "The hourly weather forecast has been updated. The wind intensity has been changed to "  + cWeatherWind }
                        }
                        if (humChanged) {
                            if (result) {result = result +  ", the humidity to "  + cWeatherHum }
                            else { result = "The hourly weather forecast has been updated. The humidity has been changed to "  + cWeatherHum }
                        }
                        if (precChanged) {
                            if (result) {result = result + ", the chance of rain to "  + cWeatherPrecipitation }
                            else { result = "The hourly weather forecast has been updated. The chance of rain has been changed to "  + cWeatherPrecipitation }
                        }
                        data = [value: result, name: "weather alert", device: "weather"]
                        alertsHandler(data)
                    }
                    else {
                        if (settings?.myWeather == "Weather Condition Changes" && wUpdate ==  "current weather condition") {
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value: result, name: "weather alert", device: "weather"]
                            alertsHandler(data)
                        }
                        else if (settings?.myWeather == "Chance of Precipitation Changes" && wUpdate ==  "chance of precipitation") {
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value: result, name: "weather alert", device: "weather"]
                            alertsHandler(data)
                        }
                        else if (settings?.myWeather == "Wind Speed Changes" && wUpdate == "wind intensity") {
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value: result, name: "weather alert", device: "weather"]
                            alertsHandler(data)
                        }
                        else if (settings?.myWeather == "Humidity Changes" && wUpdate == "humidity") {
                            result = "The " + wUpdate + " has been updated to " + wChange
                            data = [value: result, name: "weather alert", device: "weather"]
                            alertsHandler(data)
                        }
                    }
                }
            }
        }
    } catch (Throwable t) {
        log.error t
        return result
    }
}


/***********************************************************************************************************************
    WEATHER ELEMENTS
***********************************************************************************************************************/
def mGetWeatherElements(element) {
    state.pTryAgain = false
    def result ="Current weather is not available at the moment, please try again later"
       try {
        //hourly updates
        def cWeather = getWeatherFeature("hourly", state?.wZipCode)
        def cWeatherCondition = cWeather?.hourly_forecast[0]?.condition
        def cWeatherPrecipitation = cWeather?.hourly_forecast[0]?.pop + " percent"
        def cWeatherWind = cWeather?.hourly_forecast[0]?.wspd?.english + " miles per hour"
        def cWeatherHum = cWeather?.hourly_forecast[0]?.humidity + " percent"
        def cWeatherUpdate = cWeather?.hourly_forecast[0]?.FCTTIME?.civil //forecast last updated time E.G "11:00 AM",
        //current conditions
        def condWeather = getWeatherFeature("conditions", state?.wZipCode)
        def condTodayUV = condWeather?.current_observation?.UV
          def currentT = condWeather?.current_observation?.temp_f
            int currentNow = currentT
        //forecast
        def forecastT = getWeatherFeature("forecast", state?.wZipCode)
        def fToday = forecastT?.forecast?.simpleforecast?.forecastday[0]
        def high = fToday?.high?.fahrenheit?.toInteger()
               int highNow = high
        def low = fToday?.low?.fahrenheit?.toInteger()
            int lowNow = low
        //sunset, sunrise, moon, tide
        def s = getWeatherFeature("astronomy", state?.wZipCode)
        def sunriseHour = s?.moon_phase?.sunrise?.hour
        def sunriseTime = s?.moon_phase?.sunrise?.minute
        def sunrise = sunriseHour + ":" + sunriseTime
            Date date = Date.parse("HH:mm",sunrise)
            def sunriseNow = date.format( "h:mm aa" )
        def sunsetHour = s?.moon_phase?.sunset?.hour
        def sunsetTime = s?.moon_phase?.sunset?.minute
        def sunset = sunsetHour + ":" + sunsetTime
            date = Date.parse("HH:mm", sunset)
            def sunsetNow = date.format( "h:mm aa" )
            if (getMetric() == true) {
                def cWeatherWindC = cWeather?.hourly_forecast[0]?.wspd?.metric + " kilometers per hour"
                    cWeatherWind = cWeatherWindC
                def currentTc = condWeather?.current_observation?.temp_c
                    currentNow = currentTc
                def highC = fToday?.high?.celsius
                    highNow = currentTc
                def lowC = fToday?.low?.celsius
                    lowNow = currentTc
            }
        if (state?.showDebug) { log.debug "cWeatherUpdate = ${cWeatherUpdate}, cWeatherCondition = ${cWeatherCondition}, cWeatherPrecipitation = ${cWeatherPrecipitation}, cWeatherWind = ${cWeatherWind},  cWeatherHum = ${cWeatherHum}, cWeatherHum = ${condTodayUV}" }
            if		(element == "precip" ) { result = cWeatherPrecipitation }
            else if	(element == "wind") { result = cWeatherWind }
            else if	(element == "uv") { result = condTodayUV }
            else if	(element == "hum") { result = cWeatherHum }
            else if	(element == "cond") { result = cWeatherCondition }
            else if	(element == "current") { result = currentNow }
            else if	(element == "rise") { result = sunriseNow }
            else if	(element == "set") { result = sunsetNow }
             else if	(element == "high") { result = highNow }
            else if	(element == "low") { result = lowNow }

            return result
    } catch (Throwable t) {
        log.error t
        state.pTryAgain = true
        return result
    }
}
/***********************************************************************************************************************
    WEATHER TEMPS
***********************************************************************************************************************/
def private mGetWeatherVar(var) {
    state.pTryAgain = false
    def result
    try {
        def weather = getWeatherFeature("forecast", state?.wZipCode)
        def sTodayWeather = weather?.forecast?.simpleforecast?.forecastday[0]
        if (var =="high") { result = sTodayWeather?.high?.fahrenheit }
        if (var == "low") { result = sTodayWeather?.low?.fahrenheit }
        if (var =="today") { result = 	weather?.forecast?.txt_forecast?.forecastday[0]?.fcttext }
        if (var =="tonight") { result = weather?.forecast?.txt_forecast?.forecastday[1]?.fcttext }
        if (var =="tomorrow") { result = weather?.forecast?.txt_forecast?.forecastday[2]?.fcttext }

        if (getMetric() == true) {
            if (var =="high") { result = weather?.forecast?.simpleforecast?.forecastday[0]?.high?.celsius }
            if (var == "low") { result = weather?.forecast?.simpleforecast?.forecastday[0]?.low?.celsius }
            if (var =="today") { result = 	weather?.forecast?.txt_forecast?.forecastday[0]?.fcttext_metric }
            if (var =="tonight") { result = weather?.forecast?.txt_forecast?.forecastday[1]?.fcttext_metric }
            if (var =="tomorrow") { result = weather?.forecast?.txt_forecast?.forecastday[2]?.fcttext_metric }
            result = result?.toString()
            result = result?.replaceAll(/([0-9]+)C/,'$1 degrees')
        }
        result = result?.toString()
        result = result?.replaceAll(/([0-9]+)F/,'$1 degrees')?.replaceAll(~/mph/, " miles per hour")
        // clean up wind direction (South)
        result = result?.replaceAll(~/ SSW /, " South-southwest ")?.replaceAll(~/ SSE /, " South-southeast ")?.replaceAll(~/ SE /, " Southeast ")?.replaceAll(~/ SW /, " Southwest ")
        // clean up wind direction (North)
        result = result?.replaceAll(~/ NNW /, " North-northwest ")?.replaceAll(~/ NNE /, " North-northeast ")?.replaceAll(~/ NE /, " Northeast ")?.replaceAll(~/ NW /, " Northwest ")
        // clean up wind direction (West)
        result = result?.replaceAll(~/ WNW /, " West-northwest ")?.replaceAll(~/ WSW /, " West-southwest ")
        // clean up wind direction (East)
        result = result?.replaceAll(~/ ENE /, " East-northeast ")?.replaceAll(~/ ESE /, " East-southeast ")

        return result
    } catch (Throwable t) {
        log.error t
        state.pTryAgain = true
        return result
    }
}
