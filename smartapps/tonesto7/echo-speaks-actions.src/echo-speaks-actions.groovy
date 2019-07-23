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

String appVersion()	 { return "2.8.0" }
String appModified() { return "2019-07-22" }
String appAuthor()	 { return "Anthony S." }
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
    page(name: "mainPage")
    page(name: "uhOhPage")
    page(name: "namePage")
    page(name: "triggersPage")
    page(name: "conditionsPage")
    page(name: "notifPrefPage")
    page(name: "actionsPage")
    page(name: "searchTuneInResultsPage")
    page(name: "timePage")
    page(name: "dateTimePage")
    page(name: "quietRestrictPage")
    page(name: "uninstallPage")
    page(name: "sequencePage")
}

def startPage() {
    if(parent != null) {
        if(!state?.isInstalled && parent?.childInstallOk() != true) {
            uhOhPage()
        } else {
            state?.isParent = false
            mainPage()
        }
    } else { uhOhPage() }
}

def uhOhPage () {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            paragraph "HOUSTON WE HAVE A PROBLEM!\n\nEcho Speaks - Groups can't be directly installed from the Marketplace.\n\nPlease use the Echo Speaks SmartApp to configure them.", required: true,
            state: null, image: getAppImg("exclude")
        }
        if(isST()) { remove("Remove this bad Group", "WARNING!!!", "BAD Group SHOULD be removed") }
    }
}

def appInfoSect(sect=true)	{
    def str = "App: v${appVersion()}"
    section() {
        href "empty", title: pTS("${app?.name}", getAppImg("es_actions", true)), description: str, image: getAppImg("es_actions")
    }
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

private List buildTriggerEnum() {
    List enumOpts = []
    Map buildItems = [:]
    buildItems["Date/Time"] = ["Scheduled": "Scheduled Time"]?.sort{ it?.key }
    buildItems["Location"] = ["Modes":"Modes", "Routines":"Routines"]?.sort{ it?.key }
    // buildItems["Weather Events"] = ["Weather":"Weather"]
    buildItems["Safety & Security"] = ["AlarmSystem": "${getAlarmSystemName()}", "CO2 & Smoke":"CO\u00B2 & Smoke"]?.sort{ it?.key }
    buildItems["Actionable Devices"] = ["Locks":"Locks", "Dimmers, Outlets, Switches":"Dimmers, Outlets, Switches", "Garage Door Openers":"Garage Door Openers", "Valves":"Valves", "Window Shades":"Window Shades", "Buttons":"Buttons"]?.sort{ it?.key }
    // buildItems["Sensor Device"] = ["Acceleration":"Acceleration", "Contacts, Doors, Windows":"Contacts, Doors, Windows", "Motion":"Motion", "Presence":"Presence", "Temperature":"Temperature", "Humidity":"Humidity", "Water":"Water", "Power":"Power"]?.sort{ it?.key }
    buildItems["Sensor Device"] = ["Contacts, Doors, Windows":"Contacts, Doors, Windows", "Motion":"Motion", "Presence":"Presence", "Temperature":"Temperature", "Humidity":"Humidity", "Water":"Water", "Power":"Power"]?.sort{ it?.key }
    if(isST()) {
        buildItems?.each { key, val-> addInputGrp(enumOpts, key, val) }
        // log.debug "enumOpts: $enumOpts"
        return enumOpts
    } else {
        //TODO: FIX HUBITAT TRIGGER Loading Section
        def newOpts = buildItems?.collectEntries { it?.value }
        log.debug "newOpts: $newOpts"
        return newOpts as Map
    }

}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        if(!isPaused()) {
            Boolean trigConf = triggersConfigured()
            Boolean actConf = actionsConfigured()
            section ("Configuration: Part 1") {
                href "triggersPage", title: "Action Triggers", description: getTriggersDesc(), state: (trigConf ? "complete" : ""), image: getPublicImg("trigger")
            }

            section("Configuration: Part 2:") {
                if(trigConf) {
                    def condDef = settings?.findAll { it?.key?.startsWith("cond_") }?.findAll { it?.value }
                    href "conditionsPage", title: "Condition/Restrictions\n(Optional)", description: (condDef?.size() ? "(${condDef?.size()}) Conditions Configured\n\ntap to modify..." : "tap to configure..."), state: (condDef?.size() ? "complete": ""), image: getPublicImg("evaluate")
                } else { paragraph "More Options will be shown once triggers are configured" }
            }
            section("Configuration: Part 3") {
                if(trigConf) {
                    href "actionsPage", title: "Actions Tasks", description: getActionDesc(), state: (actConf ? "complete" : ""), image: getPublicImg("adhoc")
                } else { paragraph "More Options will be shown once triggers are configured" }
            }

            section("Preferences") {
                input (name: "appDebug", type: "bool", title: "Show Debug Logs in the IDE?", description: "Only enable when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug"))
            }
        } else {
            paragraph "This Action is currently in a paused state...  To edit the configuration please un-pause", required: true, state: null, image: getPublicImg("issue")
        }


        if(state?.isInstalled) {
            section ("Place this action on hold:") {
                input "actionPause", "bool", title: "Pause this Actions from Running?", defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange")
            }
            section("Remove Broadcast Group:") {
                href "uninstallPage", title: "Remove this Group", description: "Tap to Remove...", image: getAppImg("uninstall")
            }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: true) {
        section("Name this Automation:") {
            input "appLbl", "text", title:"Group Name", description: "", required:true, submitOnChange: true, image: getPublicImg("name_tag")
        }
    }
}

def triggersPage() {
    return dynamicPage(name: "triggersPage", uninstall: false, install: false) {
        def stRoutines = isST() ? location.helloHome?.getPhrases()*.label.sort() : []
        section ("Select Capabilities") {
            if(isST()) {
                input "triggerEvents", "enum", title: "Select Trigger Event(s)", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getPublicImg("trigger")
            } else {
                input "triggerEvents", "enum", title: "Select Trigger Event(s)", options: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getPublicImg("trigger")
            }
        }
        if (settings?.triggerEvents?.size()) {
            if ("Scheduled" in settings?.triggerEvents) {
                section("Time Based Events", hideable: true) {
                    if(!settings?.trig_ScheduleTime) {
                        input "trig_SunState", "enum", title: "Sunrise or Sunset...", options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true, image: getPublicImg("sun")
                        if(settings?.trigSunState) {
                            input "offset", "number", range: "*..*", title: "Offset event this number of minutes (+/-)", required: true, image: getPublicImg(settings?.trig_SunState?.toString()?.toLowerCase() + "")
                        }
                    }
                    if(!settings?.trig_SunState) {
                        input "trig_ScheduleTime", "time", title: "Time of Day?", required: false, submitOnChange: true, image: getPublicImg("clock")
                        if(settings?.trig_ScheduleTime) {
                            input "trig_ScheduleDays", "enum", title: "Days of the week", description: "(Optional)", multiple: true, required: false, submitOnChange: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getPublicImg("day_calendar2")
                        }
                    }
                }
            }

            if ("AlarmSystem" in settings?.triggerEvents) {
                section ("${getAlarmSystemName()} (${getAlarmSystemName(true)}) Events", hideable: true) {
                    input "trig_Alarm", "enum", title: "${getAlarmSystemName()} Modes", options: getAlarmTrigOpts(), multiple: true, required: true, submitOnChange: true, image: getPublicImg("alarm_home")
                    if("alerts" in trig_Alarm) {
                        input "trig_AlarmAlertsClear", "bool", title: "Send the update when Alerts are cleared.", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("Modes" in settings?.triggerEvents) {
                section ("Mode Events", hideable: true) {
                    def actions = location.helloHome?.getPhrases()*.label.sort()
                    input "trig_Modes", "mode", title: "Location Modes", multiple: true, required: true, submitOnChange: true, image: getPublicImg("mode")
                }
            }

            if("Routines" in settings?.triggerEvents) {
                if ("Routines" in settings?.triggerEvents) {
                    section("Routine Events", hideable: true) {
                        input "trig_Routines", "enum", title: "Routines", options: stRoutines, multiple: true, required: true, submitOnChange: true, image: getPublicImg("routine")
                    }
                }
            }

            if ("Weather" in settings?.triggerEvents) {
                section ("Weather Events", hideable: true) {
                    paragraph "Weather Events are not configured to take actions yet.", state: null, image: getPublicImg("weather")
                    // input "trig_WeatherAlert", "enum", title: "Weather Alerts", required: false, multiple: true, submitOnChange: true, image: getAppImg("weather"),
                    //         options: [
                    //             "TOR":	"Tornado Warning",
                    //             "TOW":	"Tornado Watch",
                    //             "WRN":	"Severe Thunderstorm Warning",
                    //             "SEW":	"Severe Thunderstorm Watch",
                    //             "WIN":	"Winter Weather Advisory",
                    //             "FLO":	"Flood Warning",
                    //             "WND":	"High Wind Advisoryt",
                    //             "HEA":	"Heat Advisory",
                    //             "FOG":	"Dense Fog Advisory",
                    //             "FIR":	"Fire Weather Advisory",
                    //             "VOL":	"Volcanic Activity Statement",
                    //             "HWW":	"Hurricane Wind Warning"
                    //         ]
                    // input "trig_WeatherHourly", "enum", title: "Hourly Weather Forecast Updates", required: false, multiple: false, submitOnChange: true, options: ["Weather Condition Changes", "Chance of Precipitation Changes", "Wind Speed Changes", "Humidity Changes", "Any Weather Updates"], image: "blank"
                    // input "trig_WeatherEvents", "enum", title: "Weather Elements", required: false, multiple: false, submitOnChange: true, options: ["Chance of Precipitation (in/mm)", "Wind Gust (MPH/kPH)", "Humidity (%)", "Temperature (F/C)"], image: "blank"
                    // if (settings?.trig_WeatherEvents) {
                    //     input "trig_WeatherEventsCond", "enum", title: "Notify when Weather Element changes...", options: ["above", "below"], required: false, submitOnChange: true, image: getAppImg("trigger")
                    // }
                    // if (settings?.trig_WeatherEventsCond) {
                    //     input "trig_WeatherThreshold", "decimal", title: "Weather Variable Threshold...", required: false, submitOnChange: true, image: getAppImg("trigger")
                    //     if (settings?.trig_WeatherThreshold) {
                    //         input "trig_WeatherCheckSched", "enum", title: "How Often to Check for Weather Changes...", required: true, multiple: false, submitOnChange: true, image: getPublicImg("day_calendar2"),
                    //             options: [
                    //                 "runEvery1Minute": "Every Minute",
                    //                 "runEvery5Minutes": "Every 5 Minutes",
                    //                 "runEvery10Minutes": "Every 10 Minutes",
                    //                 "runEvery15Minutes": "Every 15 Minutes",
                    //                 "runEvery30Minutes": "Every 30 Minutes",
                    //                 "runEvery1Hour": "Every Hour",
                    //                 "runEvery3Hours": "Every 3 Hours"
                    //             ]
                    //     }
                    // }
                }
            }

            if ("Dimmers, Outlets, Switches" in settings?.triggerEvents) {
                section ("Dimmers, Outlets, Switches", hideable: true) {
                    input "trig_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required: !(settings?.trig_Dimmer?.size() || settings?.trig_Outlet?.size()), image: getPublicImg("switch")
                    if (settings?.trig_Switch) {
                        input "trig_SwitchCmd", "enum", title: "are turned...", options:["on", "off", "any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Switch?.size() > 1 && settings?.trig_SwitchCmd && settings?.trig_SwitchCmd != "any") {
                            input "trig_SwitchAll", "bool", title: "Require ALL Switches to be (${settings?.trig_SwitchCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                    input "trig_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: !(settings?.trig_Switch?.size() || settings?.trig_Outlet?.size()), image: getPublicImg("speed_knob")
                    if (settings?.trig_Dimmer) {
                        input "trig_DimmersCmd", "enum", title: "turn...", options:["on": "on", "off": "off", "gt": "to greater than", "lt": "to less than", "gte": "to greater than or equal to", "lte": "to less than or equal to", "eq": "to being equal to"], multiple: false, required: false, submitOnChange: true
                        if (settings?.trig_DimmerCmd in ["greater", "lessThan", "equal"]) {
                            input "trig_DimmerLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                        }
                    }
                    input "trig_Outlet", "capability.outlet", title: "Outlets", multiple: true, submitOnChange: true, required: !(settings?.trig_Switch?.size() || settings?.trig_Dimmer?.size()), image: getPublicImg("outlet")
                    if (settings?.trig_Outlet) {
                        input "trig_OutletCmd", "enum", title: "are turned...", options:["on", "off", "any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Outlet?.size() > 1 && settings?.trig_OutletCmd && settings?.trig_OutletCmd != "any") {
                            input "trig_OutletAll", "bool", title: "Require ALL Outlets to be (${settings?.trig_OutletCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Motion" in settings?.triggerEvents) {
                section ("Motion Sensors", hideable: true) {
                    input "trig_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("motion")
                    if (settings?.trig_Motion) {
                        input "trig_MotionCmd", "enum", title: "become...", options: ["active", "inactive", "any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Motion?.size() > 1 && settings?.trig_MotionCmd && settings?.trig_MotionCmd != "any") {
                            input "trig_MotionAll", "bool", title: "Require ALL Motion Sensors to be (${settings?.trig_MotionCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Presence" in settings?.triggerEvents) {
                section ("Presence Events", hideable: true) {
                    input "trig_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("presence")
                    if (settings?.trig_Presence) {
                        input "trig_PresenceCmd", "enum", title: "have...", options: ["present":"Arrived", "not present":"Departed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Presence?.size() > 1 && settings?.trig_PresenceCmd && settings?.trig_PresenceCmd != "any") {
                            input "trig_PresenceAll", "bool", title: "Require ALL Presence Sensors to be (${settings?.trig_PresenceCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Contacts, Doors, Windows" in settings?.triggerEvents) {
                section ("Contacts, Doors, Windows", hideable: true) {
                    input "trig_ContactDoor", "capability.contactSensor", title: "Doors", multiple: true, required: !(settings?.trig_ContactWindow?.size() || settings?.trig_Contact?.size()), submitOnChange: true, image: getPublicImg("door_open")
                    if (settings?.trig_ContactDoor) {
                        input "trig_ContactDoorCmd", "enum", title: "changes to?", options: ["open":"opened", "closed":"closed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactDoor?.size() > 1 && settings?.trig_ContactDoorCmd && settings?.trig_ContactDoorCmd != "any") {
                            input "trig_ContactDoorAll", "bool", title: "Require ALL Doors to be (${settings?.trig_ContactDoorCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }

                    input "trig_ContactWindow", "capability.contactSensor", title: "Windows", multiple: true, required: !(settings?.trig_ContactDoor?.size() || settings?.trig_Contact?.size()), submitOnChange: true, image: getPublicImg("window")
                    if (settings?.trig_ContactWindow) {
                        input "trig_ContactWindowCmd", "enum", title: "changes to?", options: ["open":"opened", "closed":"closed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactWindow?.size() > 1 && settings?.trig_ContactWindowCmd && settings?.trig_ContactWindowCmd != "any") {
                            input "trig_ContactWindowAll", "bool", title: "Require ALL Windows to be (${settings?.trig_ContactWindowCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }

                    input "trig_Contact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: !(settings?.trig_ContactDoor?.size() || settings?.trig_ContactWindow?.size()), submitOnChange: true, image: getPublicImg("contact")
                    if (settings?.trig_Contact) {
                        input "trig_ContactCmd", "enum", title: "changes to?", options: ["open":"opened", "closed":"closed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Contact?.size() > 1 && settings?.trig_ContactCmd && settings?.trig_ContactCmd != "any") {
                            input "trig_ContactAll", "bool", title: "Require ALL Contact to be (${settings?.trig_ContactCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Garage Door Openers" in settings?.triggerEvents) {
                section ("Garage Door Openers", hideable: true) {
                    input "trig_Garages", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("garage_door_open")
                    if (settings?.trig_Garages) {
                        input "trig_GaragesCmd", "enum", title: "change to?", options: ["open":"opened", "close":"closed", "opening":"opening", "closing":"closing", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Garages?.size() > 1 && trig_GaragesCmd && (trig_GaragesCmd == "open" || trig_GaragesCmd == "close")) {
                            input "trig_GaragesAll", "bool", title: "Require ALL Garages to be (${settings?.trig_GaragesCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Locks" in settings?.triggerEvents) {
                section ("Locks", hideable: true) {
                    input "trig_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: true, submitOnChange: true, image: getPublicImg("lock")
                    if (settings?.trig_Locks) {
                        input "trig_LocksCmd", "enum", title: "changes to?", options: ["locked", "unlocked", "any"], multiple: false, required: true, submitOnChange:true
                        if (settings?.trig_Locks?.size() > 1 && settings?.trig_LocksCmd && settings?.trig_LocksCmd != "any") {
                            input "trig_LocksAll", "bool", title: "Require ALL Locks to be (${settings?.trig_LocksCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            // if ("Keypads" in settings?.triggerEvents) {
            //     section ("Keypads", hideable: true) {
            //         input "trig_Keypads", "capability.lockCodes", title: "Select Keypads", multiple: true, required: true, submitOnChange: true, image: getPublicImg("door_control")
            //         if (settings?.trig_Keypads) {
            //             input "trig_KeyCode", "number", title: "Code (4 digits)", required: true, submitOnChange: true
            //             input "trig_KeyButton", "enum", title: "Which button?", options: ["on":"On", "off":"Off", "partial":"Partial", "panic":"Panic"], multiple: false, required: true, submitOnChange: true
            //         }
            //     }
            // }

            if ("Temperature" in settings?.triggerEvents) {
                section ("Temperature Sensor Events", hideable: true) {
                    input "trig_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: true, multiple: true, submitOnChange: true, image: getPublicImg("temperature")
                    input "trig_TempCond", "enum", title: "Temperature is...", options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true
                    if (settings?.trig_TempCond) {
                        if (settings?.trig_TempCond in ["between", "below"]) {
                            input "trig_tempLow", "number", title: "a ${trig_TempCond == "between" ? "Low " : ""}Temperature of...", required: true, submitOnChange: true
                        }
                        if (settings?.trig_TempCond in ["between", "above"]) {
                            input "trig_tempHigh", "number", title: "${trig_TempCond == "between" ? "and a high " : "a "}Temperature of...", required: true, submitOnChange: true
                        }
                        if (settings?.trig_TempCond == "equals") {
                            input "trig_tempEquals", "number", title: "a Temperature of...", required: true, submitOnChange: true
                        }
                        input "trig_TempOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("Humidity" in settings?.triggerEvents) {
                section ("Humidity Sensor Events", hideable: true) {
                    input "trig_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: true, multiple: true, submitOnChange: true, image: getPublicImg("humidity")
                    if (settings?.trig_Humidity) {
                        input "trig_HumidityCond", "enum", title: "Relative Humidity (%) is...", options: ["above", "below", "equals"], required: false, submitOnChange: true
                        if(settings?.trig_HumidityCond) {
                            input "trig_HumidityLevel", "number", title: "Relative Humidity (%)", required: true, description: "percent", submitOnChange: true
                            input "trig_HumidityOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            // if ("Acceleration" in settings?.triggerEvents) {
            //     section ("Acceleration Sensor Events", hideable: true) {
            //         input "trig_Acceleration", "capability.accelerationSensor", title: "Acceleration Sensors", required: true, multiple: true, submitOnChange: true, image: getPublicImg("humidity")
            //         if (settings?.trig_Acceleration) {
            //             input "trig_AccelerationCond", "enum", title: "Relative Humidity (%) is...", options: ["active", "inactive", "any"], required: false, submitOnChange: true
            //             if (settings?.trig_Acceleration?.size() > 1 && settings?.trig_AccelerationCmd && settings?.trig_AccelerationCmd != "any") {
            //                 input "trig_AccelerationAll", "bool", title: "Require ALL Acceleration Sensors to be (${settings?.trig_AccelerationCmd})?", required: false, defaultValue: false, submitOnChange: true
            //             }
            //         }
            //     }
            // }

            if ("Water" in settings?.triggerEvents) {
                section ("Water Sensor Events", hideable: true) {
                    input "trig_Water", "capability.waterSensor", title: "Water/Moisture Sensors", required: true, multiple: true, submitOnChange: true, image: getPublicImg("water")
                    if (settings?.trig_Water) {
                        input "trig_WaterCmd", "enum", title: "changes to?", options: ["wet", "dry", "any"], required: false, submitOnChange: true
                        if (settings?.trig_Water?.size() > 1 && settings?.trig_WaterCmd && settings?.trig_WaterCmd != "any") {
                            input "trig_WaterAll", "bool", title: "Require ALL Water Sensors to be (${settings?.trig_WaterCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Power" in settings?.triggerEvents) {
                section ("Power Events", hideable: true) {
                    input "trig_Power", "capability.powerMeter", title: "Power Meters", required: true, multiple: true, submitOnChange: true, image: getPublicImg("power")
                    input "trig_PowerCond", "enum", title: "Power Level (W) is...", options: ["between", "below", "above", "equals"], required: true, multiple: false, submitOnChange: true
                    if (settings?.trig_PowerCond) {
                        if (settings?.trig_PowerCond in ["between", "below"]) {
                            input "trig_PowerLow", "number", title: "a ${trig_PowerCond == "between" ? "Low " : ""}Power Level (W) of...", required: true, submitOnChange: true
                        }
                        if (settings?.trig_PowerCond in ["between", "above"]) {
                            input "trig_PowerHigh", "number", title: "${trig_PowerCond == "between" ? "and a high " : "a "}Power Level (W) of...", required: true, submitOnChange: true
                        }
                        if (settings?.trig_PowerCond == "equals") {
                            input "trig_PowerEquals", "number", title: "a Power Level (W) of...", required: true, submitOnChange: true
                        }
                        input "trig_PowerOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("CO2 & Smoke" in settings?.triggerEvents) {
                section ("CO\u00B2 Events", hideable: true) {
                    input "trig_CO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO\u00B2)", required: !(settings?.trig_Smoke), multiple: true, submitOnChange: true, image: getPublicImg("co2_warn_status")
                    if (settings?.trig_CO2) {
                        input "trig_CO2Cmd", "enum", title: "changes to?", options: ["above", "below", "equals"], required: false, submitOnChange: true
                        if (settings?.trig_CO2Cmd) {
                            input "trig_CO2Level", "number", title: "CO\u00B2 Level...", required: true, description: "number", submitOnChange: true
                            input "trig_CO2Once", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
                section ("Smoke Events", hideable: true) {
                    input "trig_Smoke", "capability.smokeDetector", title: "Smoke Detectors", required: !(settings?.trig_CO2), multiple: true, submitOnChange: true
                    if (settings?.trig_Smoke) {
                        input "trig_SmokeCmd", "enum", title: "changes to?", options: ["detected", "clear", "any"], required: false, submitOnChange: true
                        if (settings?.trig_Smoke?.size() > 1 && settings?.trig_SmokeCmd && settings?.trig_SmokeCmd != "any") {
                            input "trig_SmokeAll", "bool", title: "Require ALL Smoke Detectors to be (${settings?.trig_SmokeCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Illuminance" in settings?.triggerEvents) {
                section ("Illuminance Events", hideable: true) {
                    input "trig_Illuminance", "capability.illuminanceMeasurement", title: "Lux Level", required: true, submitOnChange: true
                    if (settings?.trig_Illuminance) {
                        input "trig_IlluminanceLow", "number", title: "A low lux level of...", required: true, submitOnChange: true
                        input "trig_IlluminanceHigh", "number", title: "and a high lux level of...", required: true, submitOnChange: true
                        input "trig_IlluminanceOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("Window Shades" in settings?.triggerEvents) {
                section ("Window Shades", hideable: true) {
                    input "trig_Shades", "capability.windowShades", title: "Window Shades", multiple: true, required: true, submitOnChange: true, image: getPublicImg("window_shade")
                    if (settings?.trig_Shades) {
                        input "trig_ShadesCmd", "enum", title: "changes to?", options:["open":"opened", "close":"closed", "any":"any"], multiple: false, required: true, submitOnChange:true
                        if (settings?.trig_Shades?.size() > 1 && settings?.trig_ShadesCmd && settings?.trig_ShadesCmd != "any") {
                            input "trig_ShadesAll", "bool", title: "Require ALL Window Shades to be (${settings?.trig_ShadesCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Valves" in settings?.triggerEvents) {
                section ("Valves", hideable: true) {
                    input "trig_Valves", "capability.valve", title: "Valves", multiple: true, required: true, submitOnChange: true, image: getPublicImg("valve")
                    if (settings?.trig_Valves) {
                        input "trig_ValvesCmd", "enum", title: "changes to?", options:["open":"opened", "close":"closed", "any":"any"], multiple: false, required: true, submitOnChange:true
                        if (settings?.trig_Valves?.size() > 1 && settings?.trig_ValvesCmd && settings?.trig_ValvesCmd != "any") {
                            input "trig_ValvesAll", "bool", title: "Require ALL Valves to be (${settings?.trig_ValvesCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }
        }
    }
}

Boolean scheduleTriggers() {
	return (settings?.trig_ScheduleTime || settings?.trig_SunState)
}

Boolean locationTriggers() {
	return (settings?.trig_Modes || settings?.trig_Alarm || settings?.trig_Routines)
}

Boolean deviceTriggers() {
	return (settings?.trig_Buttons || (settings?.trig_Shades && settings?.trig_ShadesCmd) || (settings?.trig_Garages && settings?.trig_GaragesCmd) || (settings?.trig_Valves && settings?.trig_ValvesCmd) ||
            (settings?.trig_Switch && settings?.trig_SwitchCmd) || (settings?.trig_Dimmer && settings?.trig_DimmerCmd) || (settings?.trig_Outlet && settings?.trig_OutletCmd) || (settings?.trig_Locks && settings?.trig_LocksCmd))
}

Boolean sensorTriggers() {
    return (
        (settings?.trig_Temperature && settings?.trig_TempCond) || (settings?.trig_CO2 && settings?.trig_CO2Cmd) || (settings?.trig_Humidity && settings?.trig_HumidityCond) ||
        (settings?.trig_ContactWindow && settings?.trig_ContactWindowCmd) || (settings?.trig_ContactDoor && settings?.trig_ContactDoorCmd) || (settings?.trig_Water && settings?.trig_WaterCmd) ||
        (settings?.trig_Smoke && settings?.trig_SmokeCmd) || (settings?.trig_Presence && settings?.trig_PresenceCmd) || (settings?.trig_Motion && settings?.trig_MotionCmd) ||
        (settings?.trig_Contact && settings?.trig_ContactCmd) || (settings?.trig_Power && settings?.trig_PowerCond) || (settings?.trig_Illuminance && settings?.trig_IlluminanceLow && settings?.trig_IlluminanceHigh)
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
        section("Time of Day") {
            href "timePage", title: "Time Schedule", description: "", state: "", image: getPublicImg("clock")
        }

        section ("Location Based Conditions") {
            input "cond_Mode", "mode", title: "Location Mode is...", multiple: true, required: false, submitOnChange: true, image: getPublicImg("mode")
            input "cond_Alarm", "enum", title: "${getAlarmSystemName()} is...", options: ["away":"Armed Away","stay":"Armed Home","off":"Disarmed"], multiple: false, required: false, submitOnChange: true, image: getPublicImg("alarm_home")
            input "cond_Days", "enum", title: "Days of the week", multiple: true, required: false, submitOnChange: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getPublicImg("day_calendar")
        }

        section ("Switch and Dimmer Conditions") {
            input "cond_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false, image: getPublicImg("switch")
            if (settings?.cond_Switch) {
                input "cond_Switch_state", "enum", title: "are...", options:["on":"On","off":"Off"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Switch?.size() > 1 && settings?.cond_Switch_state) {
                    input "cond_Switch_allreq", "bool", title: "ALL Switches must be (${settings?.cond_Switch_state})?", required: false, defaultValue: false, submitOnChange: true
                }
            }

            // input "cond_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false, image: getPublicImg("speed_knob")
            // if (settings?.cond_Dimmer) {
            //     input "cond_Dimmer_state", "enum", title: "is...", options:["greater":"greater than","lessThan":"less than","equal":"equal to"], multiple: false, required: false, submitOnChange: true
            //     if (settings?.cond_Dimmer_state in ["greater", "lessThan", "equal"]) {
            //         input "cond_Dimmer_level", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
            //         if (settings?.cond_Dimmer?.size() > 1 && settings?.cond_Dimmer_state && settings?.cond_Dimmer_level) {
            //             input "cond_Dimmmer_allreq", "bool", title: "ALL Dimmers must be (${settings?.cond_Dimmer_state} ${settings?.cond_Dimmer_level}%)?", required: false, defaultValue: false, submitOnChange: true
            //         }
            //     }
            // }
        }
        section ("Motion and Presence Conditions") {
            input "cond_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("motion")
            if (settings?.cond_Motion) {
                input "cond_Motion_state", "enum", title: "are...", options: ["active":"active", "inactive":"inactive"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Motion?.size() > 1 && settings?.cond_Motion_state) {
                    input "cond_Motion_allreq", "bool", title: "ALL Motion Sensors must be (${settings?.cond_Motion_state})?"
                }
            }

            input "cond_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("presence")
            if (settings?.cond_Presence) {
                input "cond_Presence_state", "enum", title: "are...", options: ["present":"Present","not present":"Not Present"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Presence?.size() > 1 && settings?.cond_Presence_state) {
                    input "cond_Presence_allreq", "bool", title: "Presence Sensors must be (${settings?.cond_Presence_state})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Door, Window, Contact Sensors Conditions") {
            input "cond_Contact", "capability.contactSensor", title: "Contact Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("contact")
            if (settings?.cond_Contact) {
                input "cond_Contact_state", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Contact?.size() > 1 && settings?.cond_Contact_state) {
                    input "cond_Contact_allreq", "bool", title: "ALL Contacts must be (${settings?.cond_Contact_state})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }

        section ("Garage Door and Lock Conditions") {
            input "cond_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true, image: getPublicImg("lock")
            if (settings?.cond_Locks) {
                input "cond_Locks_state", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
                if (settings?.cond_Locks?.size() > 1 && settings?.cond_Locks_state) {
                    input "cond_Locks_allreq", "bool", title: "ALL Locks must be (${settings?.cond_Locks_state})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_Garages", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("garage_door_open")
            if (settings?.cond_Garages) {
                input "cond_Garages_state", "enum", title: "are...", options:["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Garages?.size() > 1 && settings?.cond_Garages_state) {
                    input "cond_Garages_allreq", "bool", title: "ALL Garages must be (${settings?.cond_Garages_state})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }

        // section ("Environmental Conditions") {
        //     input "cond_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true, image: getPublicImg("humidity")
        //     if (settings?.cond_Humidity) {
        //         input "cond_Humidity_level", "enum", title: "Only when the Humidity is...", options: ["above", "below", "equal"], required: false, submitOnChange: true
        //         if (settings?.cond_Humidity_level) {
        //             input "cond_Humidity_percent", "number", title: "this level...", required: true, description: "percent", submitOnChange: true
        //         }
        //         if (settings?.cond_Humidity_percent && settings?.cond_Humidity_level != "equal") {
        //             input "cond_Humidity_stop", "number", title: "...but not ${settings?.cond_Humidity_level} this percentage", required: false, description: "humidity"
        //         }
        //     }
        //     input "cond_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true, image: getPublicImg("temperature")
        //     if (settings?.cond_Temperature) {
        //         input "cond_Temperature_level", "enum", title: "When the temperature is...", options: ["above", "below", "equal"], required: false, submitOnChange: true
        //         if (settings?.cond_Temperature_level) {
        //             input "cond_Temperature_degrees", "number", title: "Temperature...", required: true, description: "degrees", submitOnChange: true
        //         }
        //         if (settings?.cond_Temperature_degrees && settings?.cond_Temperature_level != "equal") {
        //             input "cond_Temperature_stop", "number", title: "...but not ${settings?.cond_Temperature_level} this temperature", required: false, description: "degrees"
        //         }
        //     }
        // }
    }
}

private Map devsSupportVolume(devs) {
    List noSupport = []
    List supported = []
    if(devs) {
        devs?.each { dev->
            if(dev?.hasAttribute("permissions") && dev?.currentPermissions?.toString()?.contains("volumeControl")) {
                supported?.push(dev?.label)
            } else { noSupport?.push(dev?.label) }
        }
    }
    return [s:supported, n:noSupport]
}

def broadcastGroupsSect() {
    section("Group Devices:") {
        input "act_SendToBrdGrp", "bool", title: "Send to an Echo Speaks Broadcast Group?", description: "This is ONLY for sending a Speech message to all devices in the group", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("es_groups")
        if(act_SendToBrdGrp) {
            Map brdCastGrps = parent?.getBroadcastGrps()
            state?.brdCastGrps = brdCastGrps
            Map groups = brdCastGrps?.collectEntries { [(it?.key): it?.value?.name] }
            input "act_BroadcastGrps", "enum", title: "Select the broadcast Group", options: groups, required: true, multiple: false, submitOnChange: true, image: getAppImg("es_groups")
        }
    }
}

private executeAction(frc=false, src=null) {
    def startTime = now()
    log.trace "executeAction${src ? "($src)" : ""}${frc ? " | [Forced]" : ""}..."
    Boolean condOk = conditionValid()
    Boolean actConf = actionsConfigured()
    String actType = settings?.actionType
    Integer waitDelay = settings?.act_delay ?: 0
    if(actConf && actType) {
        if(!condOk) { log.warn "Skipping Execution because set conditions have not been met"; return; }
        switch(actType) {
            case "speak":
                if(settings?.act_set_volume || settings?.act_restore_volume) {
                    settings?.act_EchoDevicesList?.setVolumeSpeakAndRestore(settings?.act_set_volume, settings?.act_speak_txt, settings?.act_restore_volume)
                } else {
                    settings?.act_EchoDevicesList?.speak(settings?.act_speak_txt)
                }
                logger("debug", "Sending Speak Command: (${settings?.act_speak_txt}) to ${settings?.act_EchoDevicesList} | Volume: ${settings?.act_set_volume} | Restore Volume: ${settings?.act_restore_volume}")
                break
            case "announcement":
                if(settings?.act_EchoDevicesList?.size() > 1) {
                    List devObj = []
                    devObj << settings?.act_EchoDevicesList?.collectEntries { [deviceTypeId: it?.currentValue("deviceType"), deviceSerialNumber: it?.deviceNetworkId?.toString()?.tokenize("|")[2]] }
                    log.debug "devObj: $devObj"
                    settings?.act_EchoDevicesList?.sendAnnouncementToDevices(settings?.act_announcement_txt, null, devObj?.toString(), settings?.act_set_volume ?: null, settings?.act_restore_volume ?: null)
                    logger("debug", "Sending Announcement Command: (${settings?.act_announcement_txt}) to ${settings?.act_EchoDevicesList} | Volume: ${settings?.act_set_volume} | Restore Volume: ${settings?.act_restore_volume}")
                } else {
                    settings?.act_EchoDevicesList?.playAnnouncement(settings?.act_announcement_txt, null, settings?.act_set_volume ?: null, settings?.act_restore_volume ?: null)
                    logger("debug", "Sending Announcement Command: (${settings?.act_announcement_txt}) to ${settings?.act_EchoDevicesList} | Volume: ${settings?.act_set_volume} | Restore Volume: ${settings?.act_restore_volume}")
                }

                break
        }
    }


    log.debug "executeAction finished... | ProcessTime: (${now()-startTime}ms)"
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", title: (settings?.actionType ? "Action | (${settings?.actionType})" : "Actions to perform..."), install: false, uninstall: false) {
        Boolean done = false
        Map actionExecMap = [:]
        Map actionOpts = [
            "speak":"Speak", "announcement":"Announcement", "sequence":"Execute Sequence", "weather":"Weather Report", "playback":"Playback Control",
            "builtin":"Sing, Jokes, Story, etc.", "music":"Play Music", "calendar":"Calendar Events", "alarm":"Create Alarm", "reminder":"Create Reminder", "dnd":"Do Not Disturb",
            "bluetooth":"Bluetooth Control", "wakeword":"Wake Word"
        ]
        section("Configure Actions to Take:", hideable: true, hidden: (settings?.act_EchoDevicesList?.size())) {
            input "actionType", "enum", title: "Actions Type", description: "", options: actionOpts, multiple: false, required: true, submitOnChange: true, image: getPublicImg("adhoc")
        }

        if(actionType) {
            actionExecMap?.actionType = actionType
            switch(actionType) {
                case "speak":
                    String ssmlTestUrl = "https://topvoiceapps.com/ssml"
                    String ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
                    String ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {

                        section("SSML Info:") {
                            paragraph title: "What is SSML?", "SSML allows for changes in tone, speed, voice, emphasis. As well as using MP3, and access to the Sound Library", state: "complete"
                            href url: ssmlDocsUrl, style: "external", required: false, title: "Amazon SSML Docs", description: "Tap to open browser", image: getPublicImg("web")
                            href url: ssmlSoundsUrl, style: "external", required: false, title: "Amazon Sound Library", description: "Tap to open browser", image: getPublicImg("web")
                            href url: ssmlTestUrl, style: "external", required: false, title: "SSML Designer and Tester", description: "Tap to open browser", image: getPublicImg("web")
                        }
                        section("Speech Tips:") {
                            paragraph "To make beep tones use: 'wop' (each wop is one beep)"
                        }
                        section("Action Config:") {
                            input "act_speak_txt", "text", title: "Enter Text/SSML", description: "If entering SSML make sure to include <speak></speak>", submitOnChange: true, required: false, image: getAppImg("speak")
                        }
                        actionVolumeInputs()
                        actionExecMap?.config = [text: settings?.act_speak_txt, volume: [change: settings?.act_set_volume, restore: settings?.act_restore_volume]]
                        if(act_speak_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "announcement":
                    section("Action Description:") {
                        paragraph "Plays a brief tone and speaks the message you define.", state: "complete"
                    }
                    echoDevicesInputByPerm("announce")
                    if(settings?.act_EchoDevices) {
                        if(settings?.act_EchoDevices?.size() > 1) {
                            List devObj = []
                            devObj << settings?.act_EchoDevicesList?.collectEntries { [deviceTypeId: it?.currentValue("deviceType"), deviceSerialNumber: it?.deviceNetworkId?.toString()?.tokenize("|")[2]] }
                            log.debug "devObj: $devObj"
                            actionType?.config?.deviceObjs = devObj
                        }
                        section("Action Config:") {
                            input "act_announcement_txt", "text", title: "Enter Text to announce", submitOnChange: true, required: false, image: getAppImg("announcement")
                        }
                        actionVolumeInputs()
                        actionExecMap?.config = [text: settings?.act_announcement_txt, volume: [change: settings?.act_set_volume, restore: settings?.act_restore_volume]]
                        if(act_announcement_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sequence":
                    section("Action Description:") {
                        paragraph "Sequences are a custom command where you can string different alexa actions which are sent to Amazon as a single command.  The command is then processed by amazon sequentially or in parallel.", state: "complete"
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
                        section("Action Config:") {
                            input "act_sequence_txt", "text", title: "Enter sequence text", submitOnChange: true, required: false, image: getAppImg("sequence")
                        }
                        if(settings?.act_sequence_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "weather":
                    section("Action Description:") {
                        paragraph "Plays a very basic weather report.", state: "complete"
                    }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        actionVolumeInputs()
                        done = true
                    } else { done = false }
                    break

                case "playback":
                    section("Action Description:") {
                        paragraph "Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.", state: "complete"
                    }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        Map playbackOpts = [
                            "pause":"Pause", "stop":"Stop", "play": "Play", "next": "Next Track", "prev":"Previous Track",
                            "mute":"Mute"
                        ]
                        section("Playback Config:") {
                            input "act_playback_type", "enum", title: "Select Playback Action", description: "", options: playbackOpts, required: true, submitOnChange: true, image: getPublicImg("playback")
                        }
                        if(settings?.act_playback_type) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "builtin":
                    section("Action Description:") {
                        paragraph "Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.", state: "complete"
                    }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevicesList) {
                        Map builtinOpts = [
                            "sing":"Sing a Song", "flashbrief":"Flash Briefing", "funfact": "Fun Fact", "traffic": "Traffic", "joke":"Joke",
                            "story":"Tell Story", "goodbye": "Say Goodbye", "goodnight": "Say Goodnight", "birthday": "Happy Birthday",
                            "compliment": "Give Compliment", "goodmorning": "Good Morning", "welcomehome": "Welcome Home"
                        ]
                        section("BuiltIn Speech Config:") {
                            input "act_builtin_type", "enum", title: "Select Builtin Speech Type", description: "", options: builtinOpts, required: true, submitOnChange: true, image: getPublicImg("builtin")
                        }
                        actionVolumeInputs()
                        if(settings?.act_builtin_type && (settings?.act_set_volume || settings?.act_restore_volume)) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "music":
                    section("Action Description:") {
                        paragraph "Allow playback of various Songs/Radio using any connected music provider", state: "complete"
                    }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings?.act_EchoDevices) {
                        List musicProvs = settings?.act_EchoDevicesList[0]?.hasAttribute("supportedMusic") ? settings?.act_EchoDevicesList[0]?.currentValue("supportedMusic")?.split(",")?.collect { "${it?.toString()?.trim()}"} : []
                        log.debug "Music Providers: ${musicProvs}"
                        musicProvs?.each {
                            log.debug "${it}"
                        }
                        if(musicProvs) {
                            section("Music Providers:") {
                                input "act_music_provider", "enum", title: "Select Music Provider", description: "", options: musicProvs, multiple: false, required: true, submitOnChange: true, image: getPublicImg("music")
                            }
                            if(settings?.act_music_provider) {
                                if(settings?.act_music_provider == "TuneIn") {
                                    section(sTS("TuneIn Search Results:")) {
                                        paragraph "Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", state: "complete"
                                        input "tuneinSearchQuery", "text", title: inTS("Enter search phrase for TuneIn", getAppImg("tunein", true)), defaultValue: null, required: false, submitOnChange: true, image: getAppImg("tunein")
                                        if(settings?.tuneinSearchQuery) {
                                            href "searchTuneInResultsPage", title: inTS("View search results!", getAppImg("search2", true)), description: "Tap to proceed...", image: getAppImg("search2")
                                        }
                                    }
                                }
                                section("Action Config:") {
                                    input "act_music_txt", "text", title: "Enter Music Search text", submitOnChange: true, required: false, image: getAppImg("music")
                                }
                                actionVolumeInputs()
                            }
                        }
                        if(settings?.act_music_provider && settings?.act_music_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "calendar":
                    section("Action Description:") {
                        paragraph "This will read out events in your calendar (Requires accounts to be configured in the alexa app. Must not have PIN.)", state: "complete"
                    }
                    echoDevicesInputByPerm("TTS")
                    if(settings?.act_EchoDevices) {
                        section("Action Config:") {
                            input "act_calendar_type", "enum", title: "Select Calendar Action", description: "", options: ["Today", "Tomorrow", "Next Events"], required: true, submitOnChange: true, image: getPublicImg("day_calendar")
                        }
                        actionVolumeInputs()
                        if(act_calendar_type) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alarm":
                    //TODO: Offer to remove alarm after event.
                    section("Action Description:") {
                        paragraph "This will allow you to alexa alarms based on triggers", state: "complete"
                    }
                    echoDevicesInputByPerm("alarms")
                    if(settings?.act_EchoDevices) {
                        section("Action Config:") {
                            input "act_alarm_label", "text", title: "Alarm Label", submitOnChange: true, required: true, image: getAppImg("name_tag")
                            input "act_alarm_date", "text", title: "Alarm Date\n(yyyy-mm-dd)", submitOnChange: true, required: true, image: getAppImg("day_calendar")
                            input "act_alarm_time", "time", title: "Alarm Time", submitOnChange: true, required: true, image: getPublicImg("clock")
                        }
                        actionVolumeInputs(true)
                        if(act_alarm_label && act_alarm_date && act_alarm_time) { done = true } else { done = false }
                    } else { done = false }

                    break

                case "reminder":
                    //TODO: Offer to remove reminder after event.
                    section("Action Description:") {
                        paragraph "This will allow you to alexa reminders based on triggers", state: "complete"
                    }
                    echoDevicesInputByPerm("reminders")
                    if(settings?.act_EchoDevices) {
                        section("Action Config:") {
                            input "act_reminder_label", "text", title: "Reminder Label", submitOnChange: true, required: true, image: getAppImg("name_tag")
                            input "act_reminder_date", "text", title: "Reminder Date\n(yyyy-mm-dd)", submitOnChange: true, required: true, image: getAppImg("day_calendar")
                            input "act_reminder_time", "time", title: "Reminder Time", submitOnChange: true, required: true, image: getPublicImg("clock")
                        }
                        actionVolumeInputs(true)
                        if(act_reminder_label && act_reminder_date && act_reminder_time) { done = true } else { done = false }
                    } else { done = false }

                    break
                case "dnd":
                    echoDevicesInputByPerm("doNotDisturb")
                    if(settings?.act_EchoDevices) {
                        Map dndOpts = ["enable":"Enable", "disable":"Disable"]
                        section("Action Description:") {
                            paragraph "This will allow you to enable/disable Do Not Disturb based on triggers", state: "complete"
                        }
                        section("Action Config:") {
                            input "act_dnd_type", "enum", title: "Select Do Not Disturb Action", description: "", options: dndOpts, required: true, submitOnChange: true, image: getPublicImg("donotdisturb")
                        }
                        if(settings?.act_dnd_type) { done = true } else { done = false }
                    } else { done = false }

                    break
                case "wakeword":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings?.act_EchoDevices) {
                        Integer devsCnt = settings?.act_EchoDevices?.size() ?: 0
                        section("Action Description:") {
                            paragraph "This will allow you to the Wake Word based on triggers", state: "complete"
                        }
                        if(devsCnt >= 1) {
                            List wakeWords = settings?.act_EchoDevicesList[0]?.hasAttribute("wakeWords") ? settings?.act_EchoDevicesList[0]?.currentValue("wakeWords")?.replaceAll('"', "")?.split(",") : []
                            // log.debug "WakeWords: ${wakeWords}"
                            Integer iCnt = 0
                            settings?.act_EchoDevicesList?.each { cDev->
                                section("${cDev?.getLabel()}:") {
                                    if(wakeWords?.size()) {
                                        paragraph "Current Wake Word: ${settings?.act_EchoDevicesList[iCnt]?.hasAttribute("alexaWakeWord") ? settings?.act_EchoDevicesList[iCnt]?.currentValue("alexaWakeWord") : "Unknown"}"
                                        input "act_wakeword_device_${iCnt}", "enum", title: "New Wake Word", description: "", options: wakeWords, required: true, submitOnChange: true, image: getAppImg("wake_words")
                                    } else { paragraph "Oops...\nNo Wake Words have been found!  Please Remove the device from selection.", state: null, required: true }
                                }
                                iCnt++
                            }
                        }
                        if(settings?.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }?.size() == devsCnt) { done = true } else { done = false }
                    } else { done = false }
                    break
                case "bluetooth":
                    echoDevicesInputByPerm("bluetoothControl")
                    if(settings?.act_EchoDevices) {
                        Integer devsCnt = settings?.act_EchoDevices?.size() ?: 0
                        section("Action Description:") {
                            paragraph "This will allow you to connect or disconnect bluetooth based on triggers", state: "complete"
                        }
                        if(devsCnt >= 1) {
                            Integer iCnt = 0
                            settings?.act_EchoDevicesList?.each { cDev->
                                List btDevs = settings?.act_EchoDevicesList[iCnt]?.hasAttribute("btDevicesPaired") ? settings?.act_EchoDevicesList[iCnt]?.currentValue("btDevicesPaired")?.split(",") : []
                                // log.debug "btDevs: $btDevs"
                                section("${cDev?.getLabel()}:") {
                                    if(btDevs?.size()) {
                                        input "act_bluetooth_device_${iCnt}", "enum", title: "BT device to use", description: "", options: btDevs, required: true, submitOnChange: true, image: getAppImg("bluetooth")
                                        input "act_bluetooth_action_${iCnt}", "enum", title: "BT action to take", description: "", options: ["connect", "disconnect"], required: true, submitOnChange: true, image: getAppImg("bluetooth")
                                    } else { paragraph "Oops...\nNo Bluetooth devices are paired to this Echo Device!  Please Remove the device from selection.", state: null, required: true }
                                }
                                iCnt++
                            }
                        }
                        if(settings?.findAll { it?.key?.startsWith("act_bluetooth_device_") && it?.value }?.size() == devsCnt &&
                            settings?.findAll { it?.key?.startsWith("act_bluetooth_action_") && it?.value }?.size() == devsCnt) { done = true } else { done = false }
                    } else { done = false }
                    break
            }
            if(done) {
                section("Delay Config:") {
                    input "act_delay", "number", title: "Delay Action in Seconds\n(Optional)", required: false, submitOnChange: true, image: getAppImg("delay_time")
                }
                section("Simulate Action") {
                    paragraph "Run the test to see if the action is what you what to occur"
                    input "actTestRun", "bool", title: "Test this action?", description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("testing")
                    if(actTestRun) { executeActTest() }
                }
                section("") {
                    paragraph "You're all done with this step.  Press Done", state: "complete"
                }
                actionExecMap?.devices = settings?.act_EchoDevicesList
                state?.actionExecMap = actionExecMap
            }
        }
        // section(sTS("Notifications:")) {
        //     def t0 = getAppNotifConfDesc()
        //     href "notifPrefPage", title: "Notifications", description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("notification2")
        // }
        state?.act_configured = done
        log.debug "actionExecMap: ${state?.actionExecMap}"
    }
}

Boolean actionsConfigured() {
    Boolean type = (settings?.actionType)
    Boolean opts = (state?.act_configured == true)
    Boolean devs = (settings?.act_EchoDevicesList)
    // log.debug "type: $type | Options: $opts | devs: $devs"
	return (type || opts || devs)
}

private echoDevicesInputByPerm(type) {
    List echoDevs = parent?.getChildDevicesByCap(type as String)
    section("Alexa Devices: ") {
        if(echoDevs?.size()) {
            input "act_EchoDevices", "enum", title: "Echo Speaks Device(s) to Use", description: "Select the devices", options: echoDevs?.collectEntries { [(it?.getId()): it?.getLabel()] }?.sort { it?.value }, multiple: true, required: true, submitOnChange: true, image: getAppImg("echo_gen1")
        } else { paragraph "No devices were found with support for ($type)"}
        updMainEchoDeviceInput(settings?.act_EchoDevices ?: [])
    }
}

private updMainEchoDeviceInput(devs) {
    if(devs?.size()) { settingUpdate("act_EchoDevicesList", devs as List, "device.echoSpeaksDevice") }
}

private actionVolumeInputs(showAlrmVol=false) {
    if(showAlrmVol) {
        section("Volume Options:") {
            input "act_alarm_volume", "number", title: "Alarm Volume\n(Optional)", range: "0..100", required: false, submitOnChange: true, image: getPublicImg("speed_knob")
        }
    } else {
        if(settings?.act_EchoDevicesList && settings?.actionType in ["speak", "announcement", "weather", "builtin", "music", "calendar"]) {
            Map volMap = devsSupportVolume(settings?.act_EchoDevicesList)
            section("Volume Options:") {
                if(volMap?.n?.size() > 0 && volMap?.n?.size() < settings?.act_EchoDevicesList?.size()) { paragraph "Some of the selected devices do not support volume control" }
                else if(settings?.act_EchoDevicesList?.size() == volMap?.n?.size()) { paragraph "Some of the selected devices do not support volume control"; return; }
                input "act_set_volume", "number", title: "Volume Level\n(Optional)", range: "0..100", required: false, submitOnChange: true, image: getPublicImg("speed_knob")
                input "act_restore_volume", "number", title: "Restore Volume\n(Optional)", range: "0..100", required: false, submitOnChange: true, image: getPublicImg("speed_knob")
            }
        }
    }
}

def timePage() {
    return dynamicPage(name:"timePage", title: "", uninstall: false) {
        section("Start...") {
            input "startingX", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingX in [null, "A specific time"]) {
                input "starting", "time", title: "Start time", required: false, submitOnChange: true
            } else {
                if(startingX == "Sunrise") {
                    input "startSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                } else if(startingX == "Sunset") {
                    input "startSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                }
            }
        }
        section("Stop...") {
            input "endingX", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingX in [null, "A specific time"]) {
                input "ending", "time", title: "End time", required: false, submitOnChange: true
            } else {
                if(endingX == "Sunrise") {
                    input "endSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                } else if(endingX == "Sunset") {
                    input "endSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                }
            }
        }
    }
}

def uninstallPage() {
    return dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        remove("Remove this Group!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis group will be removed")
    }
}

Boolean wordInString(String findStr, String fullStr) {
    List parts = fullStr?.split(" ")?.collect { it?.toString()?.toLowerCase() }
    return (findStr in parts)
}

def dateTimePage() {
    return dynamicPage(name: "dateTimePage", title: "Configure Date/Time Triggers", uninstall: false) {
        Boolean timeReq = (settings["qTrigStartTime"] || settings["qTrigStopTime"]) ? true : false
        section() {
            input "qTrigStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time")
            if(settings["qTrigStartInput"] == "A specific time") {
                input "qTrigStartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time")
            }
            input "qTrigStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time")
            if(settings?."qTrigStopInput" == "A specific time") {
                input "qTrigStopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time")
            }
            input "triggerOnlyDays", "enum", title: "Only on these days of the week", multiple: true, required: false, image: getAppImg("day_calendar"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            // input "quietModes", "mode", title: "When these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode")
        }
    }
}

def quietRestrictPage() {
    return dynamicPage(name: "quietRestrictPage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
        Boolean timeReq = (settings["qStartTime"] || settings["qStopTime"]) ? true : false
        section() {
            input "qStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time")
            if(settings["qStartInput"] == "A specific time") {
                input "qStartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time")
            }
            input "qStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time")
            if(settings?."qStopInput" == "A specific time") {
                input "qStopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time")
            }
            input "quietDays", "enum", title: "Only on these days of the week", multiple: true, required: false, image: getAppImg("day_calendar"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            input "quietModes", "mode", title: "When these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode")
        }
    }
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
    state?.isInstalled = true
    state?.setupComplete = true
    if(settings?.appLbl && app?.getLabel() != "${settings?.appLbl} (Action)") { app?.updateLabel("${settings?.appLbl} (Action)") }
    // TODO: Cleanup unselected trigger types
    runIn(5, "subscribeToEvts")
    appCleanup()
}

private appCleanup() {
    List items = []
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    // Settings Cleanup
    List setItems = ["tuneinSearchQuery", "performBroadcast", "performMusicTest"]
    settings?.each { si-> if(si?.key?.startsWith("broadcast") || si?.key?.startsWith("musicTest") || si?.key?.startsWith("announce") || si?.key?.startsWith("sequence") || si?.key?.startsWith("speechTest")) { setItems?.push(si?.key as String) } }
    setItems?.each { sI->
        if(settings?.containsKey(sI as String)) { settingRemove(sI as String) }
    }
}

public triggerInitialize() {
    runIn(3, "initialize")
}

Boolean isPaused() {
    return (settings?.actionPause == true)
}

private valTrigEvt(key) {
    return (key in settings?.triggerEvents)
}

private subscribeToEvts() {
    //SCHEDULING
    if (valTrigEvt("Scheduled") && settings?.trig_ScheduleTime) { schedule(settings?.trig_ScheduleTime, "scheduleTrigEvt") }
    // TODO: Add sunset evts
    // if (settings?.trig_SunState == "Sunset") {
    //     subscribe(location, "sunsetTime", sunsetTimeHandler)
    //     sunsetTimeHandler(location.currentValue("sunsetTime"))
    // }
    // if (settings?.trig_SunState == "Sunrise") {
    //     subscribe(location, "sunriseTime", sunriseTimeHandler)
    //     sunriseTimeHandler(location.currentValue("sunriseTime"))
    // }

    // Location Events
    if(valTrigEvt("AlarmSystem") && settings?.trig_Alarm) {
        subscribe(location, !isST() ? "hsmStatus" : "alarmSystemStatus", alarmEvtHandler)
        if(!isST() && settings?.trig_Alarm == "Alerts") { subscribe(location, "hsmAlert", alarmEvtHandler) }
    }
    if(valTrigEvt("Modes") && settings?.trig_Modes) { subscribe(location, "mode", modeEvtHandler) }
    if("Routines" in settings?.triggerEvents && settings?.trig_Routines) { subscribe(location, "routineExecuted", routineEvtHandler) }

    // ENVIRONMENTAL Sensors
    if("Presence" in settings?.triggerEvents) {
        if(settings?.trig_Presence)         { subscribe(trig_Presence, "presence", triggerEvtHandler) }
    }
    if("Motion" in settings?.triggerEvents) {
        if(settings?.trig_Motion)           { subscribe(trig_Motion, "motion", triggerEvtHandler) }
    }

    if("Water" in settings?.triggerEvents) {
        if(settings?.trig_Water)            { subscribe(settings?.trig_Water, "water", triggerEvtHandler) }
    }

    if("Humidity" in settings?.triggerEvents) {
        if(settings?.trig_Humidity)         { subscribe(settings?.trig_Humidity, "humidity", triggerEvtHandler) }
    }

    if("Temperature" in settings?.triggerEvents) {
        if(settings?.trig_Temperature)      { subscribe(settings?.trig_Temperature, "temperature", triggerEvtHandler) }
    }

    if("Illuminance" in settings?.triggerEvents) {
        if(settings?.trig_Illuminance)      { subscribe(settings?.trig_Illuminance, "illuminance", triggerEvtHandler) }
    }

    //Power
    if("Power" in settings?.triggerEvents) {
        if(settings?.trig_Power) { subscribe(trig_Power, "power", triggerEvtHandler) }
    }

    // Locks
    if("Locks" in settings?.triggerEvents) {
        if(settings?.trig_Locks) { subscribe(settings?.trig_Locks, "lock", triggerEvtHandler) }
    }

    if("Window Shades" in settings?.triggerEvents) {
        if(settings?.trig_Shades) { subscribe(settings?.trig_Shades, "windowShade", triggerEvtHandler) }
    }

    if("Valves" in settings?.triggerEvents) {
        if(settings?.trig_Valves) { subscribe(settings?.trig_Valves, "valve", triggerEvtHandler) }
    }

    if("CO2 & Smoke" in settings?.triggerEvents) {
        if(settings?.trig_CO2)          { subscribe(settings?.trig_CO2, "carbonDioxide", triggerEvtHandler) }
        if(settings?.trig_Smoke)        { subscribe(settings?.trig_Smoke, "smoke", triggerEvtHandler) }
    }

    // Garage Door Openers
    if("Garage Door Openers" in settings?.triggerEvents) {
        if(settings?.trig_Garages)       { subscribe(settings?.trig_Garages, "garageDoorControl", triggerEvtHandler) }
    }

    //Keypads
    if("Keypads" in settings?.triggerEvents) {
        if(settings?.trig_Keypads)       { subscribe(settings?.trig_Keypads, "codeEntered", triggerEvtHandler) }
    }

    //Contacts
    if ("Contacts, Doors, Windows" in settings?.triggerEvents) {
        if(settings?.trig_ContactDoor)   { subscribe(trig_ContactDoor, "contact", triggerEvtHandler) }
        if(settings?.trig_ContactWindow) { subscribe(trig_ContactWindow, "contact", triggerEvtHandler) }
        if(settings?.trig_Contact)       { subscribe(trig_Contact, "contact", triggerEvtHandler) }
    }

    // Dimmers, Outlets, Switches
    if ("Dimmers, Outlets, Switches" in settings?.triggerEvents) {
        if(settings?.trig_Switch)        { subscribe(trig_Switch, "switch", triggerEvtHandler) }
        if(settings?.trig_Outlet)        { subscribe(trig_Outlet, "outlet", triggerEvtHandler) }
        if(settings?.trig_Dimmer) {
            subscribe(settings?.trig_Dimmer, "switch", triggerEvtHandler)
            subscribe(settings?.trig_Dimmer, "level", triggerEvtHandler)
        }
    }
}

// EVENT HANDLER FUNCTIONS
def alarmEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    Boolean useAlerts = (settings?.trig_Alarm == "Alerts")
    switch(evt?.name) {
        case "hsmStatus":
        case "alarmSystemStatus":
            if(isST() && useAlerts) {
                def inc = getShmIncidents()
                if(inc) {

                }
            }
            break
        case "hsmAlert":

            break


    }
}

def routineEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
}

def modeEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
}

def locationEvtHandler(evt) {
	def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")

}

def triggerEvtHandler(evt) {
	def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
}

def scheduleTrigEvt() {
    if(isDayOfWeek()) {
        // Execute Action Plan
    }
}


/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
Boolean timeCondOk() {
    // *TODO: Add timepage condition logic
    return true
}

Boolean locationCondOk() {
    Boolean mOk = settings?.cond_Mode ? (isInMode(settings?.cond_Mode)) : true
    Boolean sOk = settings?.cond_Alarm ? (isInAlarmMode(settings?.cond_Alarm)) : true
    Boolean dOk = settings?.cond_Days ? (isDayOfWeek(settings?.cond_Days)) : true
    log.debug "locationCondOk | modeOk: $mOk | shmOk: $sOk | daysOk: $dOk"
    return (mOk && sOk && dOk)
}

Boolean deviceCondOk(type) {
    def devs = settings?."cond_${type}" ?: null
    def stateVal = settings?."cond_${type}_state" ?: null
    if( !(type && devs && stateVal) ) { return true }
    return settings?."cond_${type}_allreq" ? allDevEqCapVal(devs, type, stateVal) : anyDevEqCapVal(devs, type, stateVal)
}

Boolean allDevEqCapVal(devs, cap, val) {
    if(devs) { return (devs?.findAll { it?."current${cap}" == val }?.size() > devs?.size()) }
    return false
}

Boolean anyDevEqCapVal(devs, cap, val) {
    if(devs) { return (devs?.findAll { it?."current${cap}" == val }?.size() > 0) }
    return false
}

Boolean deviceCondOk() {
    Boolean swDevOk = deviceCondOk("Switch")
    Boolean motDevOk = deviceCondOk("Motion")
    Boolean presDevOk = deviceCondOk("Presence")
    Boolean conDevOk = deviceCondOk("Contact")
    Boolean lockDevOk = deviceCondOk("Locks")
    Boolean garDevOk = deviceCondOk("Garages")
    log.debug "deviceCondOk | switchOk: $swDevOk | motionOk: $motDevOk | presenceOk: $presDevOk | contactOk: $conDevOk | lockOk: $lockDevOk | garageOk: $garDevOk"
    return (swDevOk && motDevOk && presDevOk && conDevOk && lockDevOk && garDevOk)
}

def conditionValid() {
    log.trace "Checking that all conditions are ok."
    def timeOk = timeCondOk()
    def locOk = locationCondOk()
    def devOk = deviceCondOk()
    log.debug "conditionHandler | timeOk: $timeOk | locationOk: $locOk | deviceOk: $devOk"
    return (timeOk && locOk && devOk)
}

/***********************************************************************************************************
    ACTION EXECUTION
************************************************************************************************************/

private executeActTest() {
    settingUpdate("actTestRun", "false", "bool")
    executeAction(true, "executeActTest")
}



/***********************************************************************************************************
   HELPER UTILITES
************************************************************************************************************/

void settingUpdate(name, value, type=null) {
    if(name && type) { app?.updateSetting("$name", [type: "$type", value: value]) }
    else if (name && type == null) { app?.updateSetting(name.toString(), value) }
}

private stateCleanup() {
    List items = []
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
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
Map weekDaysEnum() {
    return ["SUN": "Sunday", "MON": "Monday", "TUE": "Tuesday", "WED": "Wednesday", "THU": "Thursday", "FRI": "Friday", "SAT": "Saturday"]
}

Map monthEnum() {
    return ["1": "January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"]
}

Map getAlarmTrigOpts() {
    return ["away":"Armed Away","stay":"Armed Home","off":"Disarmed", "alerts": "Alerts"]
}

def getShmIncidents() {
    def incidentThreshold = now() - 604800000
    return location.activeIncidents.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}

String getShmStatus() {
    switch (location.currentState("alarmSystemStatus")?.value) { case 'off': return 'Disarmed' case 'stay': return 'Armed/Stay' case 'away': return 'Armed/Away' }
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

/******************************************
|   Restriction validators
*******************************************/

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
    if(modes) { return (location?.mode?.toString() in mode) }
    return false
}

Boolean isInAlarmMode(modes) {
    if(!modes) return false
    return (getAlarmSystemStatus() in modes)
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

String getTriggersDesc() {
    return triggersConfigured() ? "Triggers:\n${settings?.triggerEvents?.collect { " • ${it}" }?.join("\n")}\n\ntap to modify..." : "tap to configure..."
}

String getActionDesc() {
    return actionsConfigured() ? "Actions:\n • ${settings?.actionType}\n\ntap to modify..." : "tap to configure..."
}

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
    str += (state?.generatedHerokuName) ? "${str != "" ? "\n" : ""} • Name: ${state?.generatedHerokuName}" : ""
    str += (settings?.amazonDomain) ? "${str != "" ? "\n" : ""} • Domain : (${settings?.amazonDomain})" : ""
    // str += (settings?.refreshSeconds) ? "${str != "" ? "\n" : ""} • Refresh Seconds : (${settings?.refreshSeconds}sec)" : ""
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
String sTS(String t, String i = null) { return isST() ? t : """<h3>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", " ")}</h3>""" }
String inTS(String t, String i = null) { return isST() ? t : """${i ? """<img src="${i}" width="42"> """ : ""} <u>${t?.replaceAll("\\n", " ")}</u>""" }
String pTS(String t, String i = null) { return isST() ? t : """<b>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", " ")}</b>""" }
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
