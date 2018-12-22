/**
 *  Echo Speaks Actions
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

String appVersion()	 { return "2.0.8" }
String appModified() { return "2018-12-17" }
String appAuthor()	 { return "Anthony S." }
String getAppImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/$imgName" }
String getPublicImg(imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/$imgName" }

definition(
    name: "Echo Speaks - Actions",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllow you to create echo device actions based on Events in your SmartThings home",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: getAppImg("es_actions.png"),
    iconX2Url: getAppImg("es_actions.png"),
    iconX3Url: getAppImg("es_actions.png"),
    pausable: true)

preferences {
    page(name: "startPage")
    page(name: "mainPage")
    page(name: "uhOhPage")
    page(name: "namePage")
    page(name: "triggersPage")
    page(name: "conditionsPage")
    page(name: "actionsPage")
    page(name: "timePage")
    page(name: "dateTimePage")
    page(name: "quietRestrictPage")
    page(name: "uninstallPage")
}

def startPage() {
    if(parent) {
        if(!state?.isInstalled && parent?.state?.childInstallOkFlag != true) {
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
            state: null, image: getAppImg("exclude.png")
        }
        remove("Remove this bad Group", "WARNING!!!", "BAD Group SHOULD be removed")
    }
}

def appInfoSect(sect=true)	{
    def str = ""
    str += "${app?.name}"
    str += "\nAuthor: ${appAuthor()}"
    str += "\nVersion: ${appVersion()}"
    section() {
        paragraph str, image: getAppImg("es_actions.png")
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
    buildItems["Date/Time"] = ["Scheduled": "Scheduled"]?.sort{ it?.key }
    buildItems["Location"] = ["Modes":"Modes", "Routines":"Routines"]?.sort{ it?.key }
    buildItems["Weather Events"] = ["Weather":"Weather"]
    buildItems["Safety & Security"] = ["Smart Home Monitor": "Smart Home Monitor", "CO2 & Smoke":"CO\u00B2 & Smoke"]?.sort{ it?.key }
    buildItems["Actionable Devices"] = ["Locks":"Locks", "Dimmers, Outlets, Switches":"Dimmers, Outlets, Switches", "Garage Door Openers":"Garage Door Openers", "Valves":"Valves", "Window Shades":"Window Shades", "Buttons":"Buttons"]?.sort{ it?.key }
    buildItems["Sensor Device"] = ["Acceleration":"Acceleration", "Contacts, Doors, Windows":"Contacts, Doors, Windows", "Motion":"Motion", "Presence":"Presence", "Temperature":"Temperature", "Humidity":"Humidity", "Water":"Water", "Power":"Power"]?.sort{ it?.key }
    buildItems?.each { key, val-> addInputGrp(enumOpts, key, val) }
    return enumOpts
}

def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        if(!settings?.actionPause) {
            section ("") {
                def trigsDef = settings?.findAll { it?.key?.startsWith("trig_") }
                href "triggersPage", title: "Configure Events to Start this action...", description: (trigsDef?.size() ? "Triggers have been configured\n\ntap to modify..." : "tap to configure..."), state: (trigsDef?.size() ? "complete": ""), image: getPublicImg("trigger.png")
            }
            section("") {
                def condDef = settings?.findAll { it?.key?.startsWith("cond_") }
                href "conditionsPage", title: "(Optional)\nConditions", description: (condDef?.size() ? "Conditions have been configured\n\ntap to modify..." : "tap to configure any restrictions..."), state: (condDef?.size() ? "complete": ""), image: getPublicImg("evaluate.png")
            }
            section("") {
                href "actionsPage", title: "Actions to Perform", description: "tap to configure...", image: getPublicImg("adhoc.png")
            }
            section("Preferences") {
                input (name: "appDebug", type: "bool", title: "Show Debug Logs in the IDE?", description: "Only leave on when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug.png"))
            }
        } else {
            paragraph "This Action is currently in a paused state...  To edit the configuration please un-pause", required: true, state: null, image: getPublicImg("issue.png")
        }


        if(state?.isInstalled) {
            section ("Place this action on hold:") {
                   input "actionPause", "bool", title: "Pause this Actions from Running?", defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange.png")
            }
            section("Remove Broadcast Group:") {
                href "uninstallPage", title: "Remove this Group", description: "Tap to Remove...", image: getAppImg("uninstall.png")
            }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: true) {
        section("Name this Automation:") {
            input "appLbl", "text", title:"Group Name", description: "", required:true, submitOnChange: true, image: getPublicImg("name_tag.png")
        }
    }
}

def triggersPage() {
    return dynamicPage(name: "triggersPage", uninstall: false, install: false) {
        def stRoutines = location.helloHome?.getPhrases()*.label.sort()
        section ("Select Capabilities") {
            input "triggerEvents", "enum", title: "Select Trigger Events", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getPublicImg("trigger.png")
        }
        if (settings?.triggerEvents?.size()) {
            if ("Scheduled" in settings?.triggerEvents) {
                section("Time Based Events", hideable: true) {
                    input "trig_SunState", "enum", title: "Sunrise or Sunset...", options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true, image: getPublicImg("sun.png")
                    if(settings?.trigSunState) {
                        input "offset", "number", range: "*..*", title: "Offset event this number of minutes (+/-)", required: true, image: getPublicImg(settings?.trig_SunState?.toString()?.toLowerCase() + ".png")
                    }
                    input "trig_Schedule", "enum", title: "Date/Time Schedule", options: ["One Time", "Recurring"], required: false, submitOnChange: true, image: getPublicImg("day_calendar2.png")
                }

                if(trig_Schedule == "One Time") {
                    section("On Future Time & Date...", hideable: true) {
                        input "trig_xFutureTime", "time", title: "Time of Day?", required: true, submitOnChange: true
                        input "trig_xFutureDay", "number", title: "This Day number? (1-31)", range: "1..31", description: "Example: (${new Date(now()).format("dd")})", required: false, submitOnChange: true, image: getAppImg("")
                        if(settings?.trig_xFutureDay) {
                            input "trig_xFutureMonth", "enum", title: "This Month?", description: "Example: (${new Date(now()).format("MMMM")})", options: monthEnum(), multiple: false, required: true, submitOnChange: true, image: getAppImg("")
                            if(settings?.trig_xFutureMonth) {
                                input "trig_xFutureYear", "number", title: "This Year?", range: "2017..2020", description: "Example: (${new Date(now()).format("yyyy")})", required: true,  submitOnChange: true, image: getAppImg("")
                            }
                        }
                    }
                }

                if(trig_Schedule == "Recurring") {
                    section("Recurring Schedule", hideable:true) {
                        input "trig_frequency", "enum", title: "Select frequency", submitOnChange: true, required: true, options: ["Minutes", "Hourly", "Daily", "Weekly", "Monthly", "Yearly"]
                        if(settings?.trig_frequency == "Minutes") {
                            input "trig_xMinutes", "number", title: "Every (XX) minute(s) - maximum 60", range: "1..59", submitOnChange: true, required: true
                        }
                        if(settings?.trig_frequency == "Hourly") {
                            input "trig_xHours", "number", title: "Every (XX) hour(s) - maximum 24", range: "1..23", submitOnChange: true, required: true
                        }
                        if(settings?.trig_frequency == "Daily") {
                            if (!settings?.trig_xDaysWeekDay) {
                                input "trig_xDays", "number", title: "Every (XX) day(s) - maximum 31", range: "1..31", submitOnChange: true, required: (!settings?.trig_xDaysWeekDay)
                            }
                            input "trig_xDaysWeekDay", "bool", title: "OR Every Week Day (MON-FRI)", required: (!settings?.trig_xDays), defaultValue: false, submitOnChange: true
                            if(settings?.trig_xDays || settings?.trig_xDaysWeekDay) {
                                input "trig_xDaysStarting", "time", title: "starting at time...", submitOnChange: true, required: true
                            }
                        }
                        if(settings?.trig_frequency == "Weekly") {
                            input "trig_xWeeks", "enum", title: "Every selected day(s) of the week", submitOnChange: true, required: true, multiple: true, options: weekDaysEnum()
                            if(settings?.trig_xWeeks) {
                                input "trig_xWeeksStarting", "time", title: "starting at time...", submitOnChange: true, required: true
                            }
                        }
                        if(settings?.trig_frequency == "Monthly") {
                            input "trig_xMonths", "number", title: "Every X month(s) - maximum 12", range: "1..12", submitOnChange: true, required: true
                            if(settings?.trig_xMonths) {
                                input "trig_xMonthsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
                                input "trig_xMonthsStarting", "time", title: "starting at time...", submitOnChange: true, required: true
                            }
                        }
                        if(settings?.trig_frequency == "Yearly") {
                            input "trig_xYears", "enum", title: "Every selected month of the year", submitOnChange: true, required: true, multiple: false, options: monthEnum()
                            if(settings?.trig_xYears) {
                                input "trig_xYearsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
                                input "trig_xYearsStarting", "time", title: "starting at time...", submitOnChange: true, required: true
                            }
                        }
                    }
                }
            }

            if ("Smart Home Monitor" in settings?.triggerEvents) {
                section ("Smart Home Monitor (SHM) Events", hideable: true) {
                    input "trig_SHM", "enum", title: "Smart Home Monitor", options:["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed", "alerts": "Alerts"], multiple: true, required: true, submitOnChange: true, image: getPublicImg("alarm_home.png")
                    if("alerts" in trig_SHM) {
                        input "trig_SHMAlertsClear", "bool", title: "Send the update when Alerts are cleared.", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("Modes" in settings?.triggerEvents) {
                section ("Mode Events", hideable: true) {
                    def actions = location.helloHome?.getPhrases()*.label.sort()
                    input "trig_Modes", "mode", title: "Location Modes", multiple: true, required: true, submitOnChange: true, image: getPublicImg("mode.png")
                }
            }

            if("Routines" in settings?.triggerEvents) {
                if ("Routines" in settings?.triggerEvents) {
                    section("Routine Events", hideable: true) {
                        input "trig_Routine", "enum", title: "Routines", options: stRoutines, multiple: true, required: true, submitOnChange: true, image: getPublicImg("routine.png")
                    }
                }
            }
            
            if ("Weather" in settings?.triggerEvents) {
                section ("Weather Events", hideable: true) {
                    paragraph "Weather Events are not configured to take actions yet.", state: null, image: getPublicImg("weather.png")
                    // input "trig_WeatherAlert", "enum", title: "Weather Alerts", required: false, multiple: true, submitOnChange: true, image: getAppImg("weather.png"),
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
                    // input "trig_WeatherHourly", "enum", title: "Hourly Weather Forecast Updates", required: false, multiple: false, submitOnChange: true, options: ["Weather Condition Changes", "Chance of Precipitation Changes", "Wind Speed Changes", "Humidity Changes", "Any Weather Updates"], image: "blank.png"
                    // input "trig_WeatherEvents", "enum", title: "Weather Elements", required: false, multiple: false, submitOnChange: true, options: ["Chance of Precipitation (in/mm)", "Wind Gust (MPH/kPH)", "Humidity (%)", "Temperature (F/C)"], image: "blank.png"
                    // if (settings?.trig_WeatherEvents) {
                    //     input "trig_WeatherEventsCond", "enum", title: "Notify when Weather Element changes...", options: ["above", "below"], required: false, submitOnChange: true, image: getAppImg("trigger.png")
                    // }
                    // if (settings?.trig_WeatherEventsCond) {
                    //     input "trig_WeatherThreshold", "decimal", title: "Weather Variable Threshold...", required: false, submitOnChange: true, image: getAppImg("trigger.png")
                    //     if (settings?.trig_WeatherThreshold) {
                    //         input "trig_WeatherCheckSched", "enum", title: "How Often to Check for Weather Changes...", required: true, multiple: false, submitOnChange: true, image: getPublicImg("day_calendar2.png"),
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
                    input "trig_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required: !(settings?.trig_Dimmer?.size() || settings?.trig_Outlet?.size()), image: getPublicImg("switch.png")
                    if (settings?.trig_Switch) {
                        input "trig_SwitchCmd", "enum", title: "are turned...", options:["on", "off", "any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Switch?.size() > 1 && settings?.trig_SwitchCmd && settings?.trig_SwitchCmd != "any") {
                            input "trig_SwitchAll", "bool", title: "Require ALL Switches to be (${settings?.trig_SwitchCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                    input "trig_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: !(settings?.trig_Switch?.size() || settings?.trig_Outlet?.size()), image: getPublicImg("speed_knob.png")
                    if (settings?.trig_Dimmer) {
                        input "trig_DimmersCmd", "enum", title: "turn...", options:["on": "on", "off": "off", "gt": "to greater than", "lt": "to less than", "gte": "to greater than or equal to", "lte": "to less than or equal to", "eq": "to being equal to"], multiple: false, required: false, submitOnChange: true
                        if (settings?.trig_DimmerCmd in ["greater", "lessThan", "equal"]) {
                            input "trig_DimmerLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                        }
                    }
                    input "trig_Outlet", "capability.outlet", title: "Outlets", multiple: true, submitOnChange: true, required: !(settings?.trig_Switch?.size() || settings?.trig_Dimmer?.size()), image: getPublicImg("outlet.png")
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
                    input "trig_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("motion.png")
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
                    input "trig_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("presence.png")
                    if (settings?.trig_Presence) {
                        input "trig_PresenceCmd", "enum", title: "have...", options: ["present":"Arrived","not present":"Departed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Presence?.size() > 1 && settings?.trig_PresenceCmd && settings?.trig_PresenceCmd != "any") {
                            input "trig_PresenceAll", "bool", title: "Require ALL Presence Sensors to be (${settings?.trig_PresenceCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }
            
            if ("Contacts, Doors, Windows" in settings?.triggerEvents) {
                section ("Contacts, Doors, Windows", hideable: true) {
                    input "trig_ContactDoor", "capability.contactSensor", title: "Doors", multiple: true, required: !(settings?.trig_ContactWindow?.size() || settings?.trig_Contact?.size()), submitOnChange: true, image: getPublicImg("door_open.png")
                    if (settings?.trig_ContactDoor) {
                        input "trig_ContactDoorCmd", "enum", title: "changes to?", options: ["open":"opened", "closed":"closed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactDoor?.size() > 1 && settings?.trig_ContactDoorCmd && settings?.trig_ContactDoorCmd != "any") {
                            input "trig_ContactDoorAll", "bool", title: "Require ALL Doors to be (${settings?.trig_ContactDoorCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                
                    input "trig_ContactWindow", "capability.contactSensor", title: "Windows", multiple: true, required: !(settings?.trig_ContactDoor?.size() || settings?.trig_Contact?.size()), submitOnChange: true, image: getPublicImg("window.png")
                    if (settings?.trig_ContactWindow) {
                        input "trig_ContactWindowCmd", "enum", title: "changes to?", options: ["open":"opened", "closed":"closed", "any":"any"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactWindow?.size() > 1 && settings?.trig_ContactWindowCmd && settings?.trig_ContactWindowCmd != "any") {
                            input "trig_ContactWindowAll", "bool", title: "Require ALL Windows to be (${settings?.trig_ContactWindowCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                
                    input "trig_Contact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: !(settings?.trig_ContactDoor?.size() || settings?.trig_ContactWindow?.size()), submitOnChange: true, image: getPublicImg("contact.png")
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
                    input "trig_Garages", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: true, submitOnChange: true, image: getPublicImg("garage_door_open.png")
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
                    input "trig_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: true, submitOnChange: true, image: getPublicImg("lock.png")
                    if (settings?.trig_Locks) {
                        input "trig_LocksCmd", "enum", title: "changes to?", options: ["locked", "unlocked", "any"], multiple: false, required: true, submitOnChange:true
                        if (settings?.trig_Locks?.size() > 1 && settings?.trig_LocksCmd && settings?.trig_LocksCmd != "any") {
                            input "trig_LocksAll", "bool", title: "Require ALL Locks to be (${settings?.trig_LocksCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Keypads" in settings?.triggerEvents) {
                section ("Keypads", hideable: true) {
                    input "trig_Keypads", "capability.lockCodes", title: "Select Keypads", multiple: true, required: true, submitOnChange: true, image: getPublicImg("door_control.png")
                    if (settings?.trig_Keypads) {
                        input "trig_KeyCode", "number", title: "Code (4 digits)", required: true, submitOnChange: true
                        input "trig_KeyButton", "enum", title: "Which button?", options: ["on":"On", "off":"Off", "partial":"Partial", "panic":"Panic"], multiple: false, required: true, submitOnChange: true
                    }
                }
            }

            if ("Temperature" in settings?.triggerEvents) {
                section ("Temperature Sensor Events", hideable: true) {
                    input "trig_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: true, multiple: true, submitOnChange: true, image: getPublicImg("temperature.png")
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
                    input "trig_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: true, multiple: true, submitOnChange: true, image: getPublicImg("humidity.png")
                    if (settings?.trig_Humidity) {
                        input "trig_HumidityCond", "enum", title: "Relative Humidity (%) is...", options: ["above", "below", "equals"], required: false, submitOnChange: true
                        if(settings?.trig_HumidityCond) {
                            input "trig_HumidityLevel", "number", title: "Relative Humidity (%)", required: true, description: "percent", submitOnChange: true
                            input "trig_HumidityOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Acceleration" in settings?.triggerEvents) {
                section ("Acceleration Sensor Events", hideable: true) {
                    input "trig_Acceleration", "capability.accelerationSensor", title: "Acceleration Sensors", required: true, multiple: true, submitOnChange: true, image: getPublicImg("humidity.png")
                    if (settings?.trig_Acceleration) {
                        input "trig_AccelerationCond", "enum", title: "Relative Humidity (%) is...", options: ["active", "inactive", "any"], required: false, submitOnChange: true
                        if (settings?.trig_Acceleration?.size() > 1 && settings?.trig_AccelerationCmd && settings?.trig_AccelerationCmd != "any") {
                            input "trig_AccelerationAll", "bool", title: "Require ALL Acceleration Sensors to be (${settings?.trig_AccelerationCmd})?", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Water" in settings?.triggerEvents) {
                section ("Water Sensor Events", hideable: true) {
                    input "trig_Water", "capability.waterSensor", title: "Water/Moisture Sensors", required: true, multiple: true, submitOnChange: true, image: getPublicImg("water.png")
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
                    input "trig_Power", "capability.powerMeter", title: "Power Meters", required: true, multiple: true, submitOnChange: true, image: getPublicImg("power.png")
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
                    input "trig_CO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO\u00B2)", required: !(settings?.trig_Smoke), multiple: true, submitOnChange: true, image: getPublicImg("co2_warn_status.png")
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
                    input "trig_Shades", "capability.windowShades", title: "Window Shades", multiple: true, required: true, submitOnChange: true, image: getPublicImg("window_shade.png")
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
                    input "trig_Valves", "capability.valve", title: "Valves", multiple: true, required: true, submitOnChange: true, image: getPublicImg("valve.png")
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

def scheduleTriggers() {
	return ((settings?.frequency && (settings?.xMinutes || settings?.xHours || settings?.xDays || settings?.xMonths || settings?.xWeeks || settings?.xYears)) || settings?.xFutureTime || settings?.xFutureDay)
}

def locationTriggers() {
	return (settings?.myMode || settings?.mySHM || settings?.myRoutine || settings?.mySunState)
}

def deviceTriggers() {
	return (settings?.myButton || settings?.myShades || settings?.myGarage || settings?.myValve || settings?.mySwitch || settings?.myLocks || settings?.myTstat)
}

def sensorTriggers() {
	return (settings?.myTemperature || settings?.myCO2 || settings?.myCO || settings?.myAcceleration || settings?.myHumidity || settings?.myWindow || settings?.myDoor || settings?.mySound  || settings?.myWater ||
			settings?.mySmoke || settings?.myPresence || settings?.myMotion || settings?.myContact || settings?.myPower)
}

def weatherTriggers() {
	return (settings?.myWeatherTriggers || settings?.myWeather || settings?.myWeatherAlert)
}

def triggersConfigured() {
	return (scheduleTriggers() || locationTriggers() || deviceTriggers() || sensorTriggers() || weatherTriggers()) ? "Configured" : "Tap to Configure"
}

/******************************************************************************
    CONDITIONS SELECTION PAGE
******************************************************************************/
def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: "Only when these are true...", install: false, uninstall: false) {
        section("Time of Day") {
            href "timePage", title: "Time Schedule", description: "", state: "", image: getPublicImg("clock.png")
        }

        section ("Location Based Conditions") {
            input "cond_Mode", "mode", title: "Location Mode is...", multiple: true, required: false, submitOnChange: true, image: getPublicImg("mode.png")
            input "cond_SHM", "enum", title: "Smart Home Monitor is...", options: ["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed"], multiple: false, required: false, submitOnChange: true, image: getPublicImg("alarm_home.png")
            input "cond_Days", "enum", title: "Days of the week", multiple: true, required: false, submitOnChange: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getPublicImg("day_calendar.png")
        }

        section ("Switch and Dimmer Conditions") {
            input "cond_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false, image: getPublicImg("switch.png")
            if (settings?.cond_Switch) {
                input "cond_SwitchCmd", "enum", title: "are...", options:["on":"On","off":"Off"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Switch?.size() > 1) {
                    input "cond_SwitchAll", "bool", title: "Require ALL Switches to be (${settings?.cond_SwitchCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false, image: getPublicImg("speed_knob.png")
            if (settings?.cond_Dimmer) {
                input "cond_DimmerCmd", "enum", title: "is...", options:["greater":"greater than","lessThan":"less than","equal":"equal to"], multiple: false, required: false, submitOnChange: true
                if (settings?.cond_DimmerCmd in ["greater", "lessThan", "equal"]) {
                    input "cond_DimmerLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                    if (settings?.cond_Dimmer?.size() > 1 && settings?.cond_DimmerCmd && settings?.cond_DimmerLvl) {
                        input "cond_DimmmerAll", "bool", title: "Require ALL Dimmers to be (${settings?.cond_DimmerCmd} ${settings?.cond_DimmerLvl}%)?", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }
        }
        section ("Motion and Presence Conditions") {
            input "cond_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("motion.png")
            if (settings?.cond_Motion) {
                input "cond_MotionCmd", "enum", title: "are...", options: ["active":"active", "inactive":"inactive"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Motion?.size() > 1 && settings?.cond_MotionCmd) {
                    input "cond_MotionAll", "bool", title: "Require ALL Motion Sensors to be (${settings?.cond_MotionCmd})?"
                }
            }
            input "cond_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("presence.png")
            if (settings?.cond_Presence) {
                input "cond_PresenceCmd", "enum", title: "are...", options: ["present":"Present","not present":"Not Present"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Presence?.size() > 1 && settings?.cond_PresenceCmd) {
                    input "cond_PresenceAll", "bool", title: "Require ALL Presence Sensors to be (${settings?.cond_PresenceCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Door, Window, Contact Sensors Conditions") {
            input "cond_ContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("door_open.png")
            if (settings?.cond_ContactDoor) {
                input "cond_ContactDoorCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_ContactDoor?.size() > 1 && settings?.cond_ContactDoorCmd) {
                    input "cond_ContactDoorAll", "bool", title: "Require ALL Doors to be (${settings?.cond_ContactDoorCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_ContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true, image: getPublicImg("window.png")
                if (settings?.cond_ContactWindow) {
                input "cond_ContactWindowCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_ContactWindow?.size() > 1 && settings?.cond_ContactWindowCmd) {
                    input "cond_ContactWindowAll", "bool", title: "Require ALL Windows to be (${settings?.cond_ContactWindowCmd})?", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            input "cond_Contact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("contact.png")
            if (settings?.cond_Contact) {
                input "cond_ContactCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Contact?.size() > 1 && settings?.cond_ContactCmd) {
                    input "cond_ContactAll", "bool", title: "Require ALL Contacts to be (${settings?.cond_ContactCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }

        section ("Garage Door and Lock Conditions") {
            input "cond_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true, image: getPublicImg("lock.png")
            if (settings?.cond_Locks) {
                input "cond_LocksCmd", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
                if (settings?.cond_Locks?.size() > 1 && settings?.cond_LocksCmd) {
                    input "cond_LocksAll", "bool", title: "Require ALL Locks to be (${settings?.cond_LocksCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_Garages", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true, image: getPublicImg("garage_door_open.png")
            if (settings?.cond_Garages) {
                input "cond_GaragesCmd", "enum", title: "are...", options:["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Garages?.size() > 1 && settings?.cond_Garages) {
                    input "cond_GaragesAll", "bool", title: "Require ALL Garages to be (${settings?.cond_GaragesCmd})?", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        
        section ("Environmental Conditions") {
            input "cond_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true, image: getPublicImg("humidity.png")
            if (settings?.cond_Humidity) {
                input "cond_HumidityLevel", "enum", title: "Only when the Humidity is...", options: ["above", "below", "equal"], required: false, submitOnChange: true
                if (settings?.cond_HumidityLevel) {
                    input "cond_HumidityPercent", "number", title: "this level...", required: true, description: "percent", submitOnChange: true
                }
                if (settings?.cond_HumidityPercent && settings?.cond_HumidityLevel != "equal") {
                    input "cond_HumidityStop", "number", title: "...but not ${settings?.cond_HumidityLevel} this percentage", required: false, description: "humidity"
                }
            }
            input "cond_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true, image: getPublicImg("temperature.png")
            if (settings?.cTemperature) {
                input "cond_TemperatureLevel", "enum", title: "When the temperature is...", options: ["above", "below", "equal"], required: false, submitOnChange: true
                if (settings?.cond_TemperatureLevel) {
                    input "cond_TemperatureDegrees", "number", title: "Temperature...", required: true, description: "degrees", submitOnChange: true
                }
                if (settings?.cond_TemperatureDegrees && settings?.cond_TemperatureLevel != "equal") {
                    input "cond_TemperatureStop", "number", title: "...but not ${settings?.cond_TemperatureLevel} this temperature", required: false, description: "degrees"
                }
            }
        }
    }
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", title: "Actions to perform...", install: false, uninstall: false) {
        section("Output Devices:") {
            input "act_SendToBrdGrp", "bool", title: "Send to an Echo Speaks Broadcast Group?", description: "This is ONLY for sending a Speech message to all devices in the group", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("es_groups.png")
            if(act_SendToBrdGrp) {
                Map brdCastGrps = parent?.getBroadcastGrps()
                state?.brdCastGrps = brdCastGrps
                Map groups = brdCastGrps?.collectEntries { [(it?.key): it?.value?.name] }
                input "act_BroadcastGrps", "enum", title: "Select the broadcast Group", options: groups, required: true, multiple: false, submitOnChange: true, image: getAppImg("es_groups.png")
            } else {
                input "act_EchoDevices", "device.echoSpeaksDevice", title: "Echo Speaks Device to Use", description: "Select the devices", multiple: true, required: true, submitOnChange: true, image: getAppImg("echo_gen1.png")
            }
        }
        if(act_SendToBrdGrp && act_BroadcastGrps) {
            
        } else {
            if (settings?.triggerEvents?.size()) {
                if ("Scheduled" in settings?.triggerEvents) {

                }
            }
        
            section("Configure Actions to Take:") {

            }

            // section ("Push Messages:") {
            //     input "usePush", "bool", title: "Send Push Notifications...", required: false, defaultValue: false, submitOnChange: true
            //     input "pushTimeStamp", "bool", title: "Add timestamp to Push Messages...", required: false, defaultValue: false, submitOnChange: true
    		// }
            // section ("Text Messages:", hideWhenEmpty: true) {
            //     paragraph "To send to multiple numbers separate the number by a comma\nE.g. 8045551122,8046663344"
            //     input "smsNumbers", "text", title: "Send SMS Text to...", required: false, submitOnChange: true, image: getAppImg("sms_phone.png")
            // }
            // section("Pushover Support:") {
            //     input ("pushoverEnabled", "bool", title: "Use Pushover Integration", required: false, submitOnChange: true, image: getAppImg("pushover_icon.png"))
            //     if(settings?.pushoverEnabled == true) {
            //         def poDevices = parent?.getPushoverDevices()
            //         if(!poDevices) {
            //             parent?.pushover_init()
            //             paragraph "If this is the first time enabling Pushover than leave this page and come back if the devices list is empty"
            //         } else {
            //             input "pushoverDevices", "enum", title: "Select Pushover Devices", description: "Tap to select", groupedOptions: poDevices, multiple: true, required: false, submitOnChange: true, image: getAppImg("select_icon.png")
            //             if(settings?.pushoverDevices) {
            //                 def t0 = [(-2):"Lowest", (-1):"Low", 0:"Normal", 1:"High", 2:"Emergency"]
            //                 input "pushoverPriority", "enum", title: "Notification Priority (Optional)", description: "Tap to select", defaultValue: 0, required: false, multiple: false, submitOnChange: true, options: t0, image: getAppImg("priority.png")
            //                 input "pushoverSound", "enum", title: "Notification Sound (Optional)", description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: parent?.getPushoverSounds(), image: getAppImg("sound.png")
            //             }
            //         }
            //         // } else { paragraph "New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the SmartApps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", state: "complete" }
            //     }
            // }
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
            input "qTrigStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time.png")
            if(settings["qTrigStartInput"] == "A specific time") {
                input "qTrigStartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time.png")
            }
            input "qTrigStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time.png")
            if(settings?."qTrigStopInput" == "A specific time") {
                input "qTrigStopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time.png")
            }
            input "triggerOnlyDays", "enum", title: "Only on these days of the week", multiple: true, required: false, image: getAppImg("day_calendar.png"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            // input "quietModes", "mode", title: "When these Modes are Active", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode.png")
        }
    }
}

def quietRestrictPage() {
    return dynamicPage(name: "quietRestrictPage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
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
}

private subscribeToEvts() {
    //SCHEDULING
    
    // if (settings?.trig_frequency)           { cronHandler(frequency) }
    // if (settings?.trig_xFutureTime)         { oneTimeHandler() }
    // if (settings?.trig_SunState == "Sunset") {
    //     subscribe(location, "sunsetTime", sunsetTimeHandler)
    //     sunsetTimeHandler(location.currentValue("sunsetTime"))
    // }
    // if (settings?.trig_SunState == "Sunrise") {
    //     subscribe(location, "sunriseTime", sunriseTimeHandler)
    //     sunriseTimeHandler(location.currentValue("sunriseTime"))
    // }

    // Location Events
    if(("Smart Home Monitor" in settings?.triggerEvents) || ("Modes" in settings?.triggerEvents) || ("Routines" in settings?.triggerEvents)) {
        if(settings?.trig_shm || settings?.trig_Modes || settings?.trig_Routine) { subscribe(location, locationEvtHandler) }
    }

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
    // if(tWind) { subscribe(tWind, "WindStrength", windHandler) }
    // if(tRain) { subscribe(tRain, "rain", rainHandler) }
    
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
        if(settings?.trig_Garages)      { subscribe(settings?.trig_Garages, "garageDoorControl", triggerEvtHandler) }
    }
    
    //Keypads
    if("Keypads" in settings?.triggerEvents) {
        if(settings?.trig_Keypads)          { subscribe(settings?.trig_Keypads, "codeEntered", triggerEvtHandler) }    
    }
    
    //Contacts
    if ("Contacts, Doors, Windows" in settings?.triggerEvents) {
        if(settings?.trig_ContactDoor) { subscribe(trig_ContactDoor, "contact", triggerEvtHandler) }
        if(settings?.trig_ContactWindow) { subscribe(trig_ContactWindow, "contact", triggerEvtHandler) }
        if(settings?.trig_Contact) { subscribe(trig_Contact, "contact", triggerEvtHandler) }
    }
    
    // Dimmers, Outlets, Switches
    if ("Dimmers, Outlets, Switches" in settings?.triggerEvents) {
        if(settings?.trig_Switch) { subscribe(trig_Switch, "switch", triggerEvtHandler) }
        if(settings?.trig_Outlet) { subscribe(trig_Outlet, "outlet", triggerEvtHandler) }
        if(settings?.trig_Dimmer)		{ 
            subscribe(settings?.trig_Dimmer, "switch", triggerEvtHandler)
            subscribe(settings?.trig_Dimmer, "level", triggerEvtHandler)
        }
    }
}  

def locationEvtHandler(evt) {
	def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")

}

def triggerEvtHandler(evt) {
	def evtDelay = now() - evt?.date?.getTime()
	logger("trace", "${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
}


/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
def conditionHandler(evt) {
    if (parent.debug) log.info "Checking that all conditions are ok."
    def result
    def cSwitchOk = false
    def cDimOk = false
    def cHumOk = false
    def cTempOk = false
    def cSHMOk = false
    def cModeOk = false
    def cMotionOk = false
    def cPresenceOk = false
    def cDoorOk = false
    def cWindowOk = false
    def cContactOk = false
    def cDaysOk = false
    def cPendAll = false
    def timeOk = false
    def cGarageOk = false
    def cLocksOk = false
    def devList = []

    // SWITCHES
    if (cSwitch == null) { cSwitchOk = true }
    if (cSwitch) {
    if (parent.trace) log.trace "Conditions: Switches events method activated"
        def cSwitchSize = cSwitch?.size()
        cSwitch.each { deviceName ->
            def status = deviceName.currentValue("switch")
            if (status == "${cSwitchCmd}"){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cSwitchAll) {
            if (devList?.size() > 0) { 
                cSwitchOk = true  
            }
        }        
        if(cSwitchAll) {
            if (devListSize == cSwitchSize) { 
                cSwitchOk = true 
            }
        }
        if (cSwitchOk == false) log.warn "Switches Conditions Handler failed"
    }

    // HUMIDITY
    if (cHumidity == null) {cHumOk = true }
    if (cHumidity) {
    if (parent.trace) log.trace "Conditions: Humidity events method activated"
        int cHumidityStopVal = cHumidityStop == null ? 0 : cHumidityStop as int
            cHumidity.each { deviceName ->
                def status = deviceName.currentValue("humidity")
                if (cHumidityLevel == "above") {
                    cHumidityStopVal = cHumidityStopVal == 0 ? 999 :  cHumidityStopVal as int
                        if (status >= cHumidityPercent && status <= cHumidityStopVal) {
                            cHumOk = true
                        }
                }
                if (cHumidityLevel == "below") {
                    if (status <= cHumidityPercent && status >= cHumidityStopVal) {
                        cHumOk = true
                    }
                }    
            }
            if (cHumOk == false) log.warn "Humidity Conditions Handler failed"
    }

    // TEMPERATURE
    if (cTemperature == null) {cTempOk = true }
    if (cTemperature) {
    if (parent.trace) log.trace "Conditions: Temperature events method activated"
        int cTemperatureStopVal = cTemperatureStop == null ? 0 : cTemperatureStop as int
            cTemperature.each { deviceName ->
                def status = deviceName.currentValue("temperature")
                if (cTemperatureLevel == "above") {
                    cTemperatureStopVal = cTemperatureStopVal == 0 ? 999 :  cTemperatureStopVal as int
                        if (status >= cTemperatureDegrees && status <= cTemperatureStopVal) {
                            cTempOk = true
                        }
                }
                if (cTemperatureLevel == "below") {
                    if (status <= cTemperatureDegrees && status >= cTemperatureStopVal) {
                        cTempOk = true
                    }
                }    
            }
            if (cTempOk == false) log.warn "Temperature Conditions Handler failed"
    }	

    // DIMMERS
    if (cDim == null) { cDimOk = true }
    if (cDim) {
    if (parent.trace) log.trace "Conditions: Dimmers events method activated"
        cDim.each {deviceD ->
            def currLevel = deviceD.latestValue("level")
            if (cDimCmd == "greater") {
                if ("${currLevel}" > "${cDimLvl}") { 
                    def cDimSize = cDim?.size()
                    cDim.each { deviceName ->
                        def status = deviceName.currentValue("level")
                        if (status > cDimLvl){ 
                            String device  = (String) deviceName
                            devList += device
                        }
                    }
                }        
            }
            if (cDimCmd == "lessThan") {
                if ("${currLevel}" < "${cDimLvl}") { 
                    def cDimSize = cDim?.size()
                    cDim.each { deviceName ->
                        def status = deviceName.currentValue("level")
                        if (status < cDimLvl){ 
                            String device  = (String) deviceName
                            devList += device
                        }
                    }
                }        
            }
            if (cDimCmd == "equal") {
                if ("${currLevel}" == "${cDimLvl}") { 
                    def cDimSize = cDim?.size()
                    cDim.each { deviceName ->
                        def status = deviceName.currentValue("level")
                        if (status == cDimLvl){ 
                            String device  = (String) deviceName
                            devList += device
                        }
                    }
                }        
            }
            def devListSize = devList?.size()
            if(!cDimAll) {
                if (devList?.size() > 0) { 
                    cDimOk = true  
                }
            }        
            if(cDimAll) {
                if (devListSize == cDimSize) { 
                    cDimOk = true 
                }
            }
        }
        if (cDimOk == false) log.warn "Dimmers Conditions Handler failed"
    }

    // DAYS OF THE WEEK
    if (cDays == null) { cDaysOk = true }
    if (cDays) {
        if (parent.trace) log.trace "Conditions: Days of the Week events method activated"
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        if (cDaysOk == false) log.warn "Days Conditions Handler failed"
        result = cDays.contains(day)
    }

    // SMART HOME MONITOR
    if (cSHM == null) { cSHMOk = true }
    if (cSHM) {
        if (parent.trace) log.trace "Conditions: SHM events method activated"
        def currentSHM = location.currentState("alarmSystemStatus")?.value
        if (cSHM == currentSHM) {
            cSHMOk = true
        }
        if (cSHMOk == false) log.warn "SHM Conditions Handler failed"
    }    

    // LOCATION MODE
    if (cMode == null) { cModeOk = true }
    if (cMode) {
    if (parent.trace) log.trace "Conditions: Mode events method activated"
        cModeOk = !cMode || cMode?.contains(location.mode)
        if (cModeOk == false) log.warn "Mode Conditions Handler failed"
    }

    // MOTION
    if (cMotion == null) { cMotionOk = true }
    if (cMotion) {
    if (parent.trace) log.trace "Conditions: Motion events method activated"
        def cMotionSize = cMotion?.size()
        cMotion.each { deviceName ->
            def status = deviceName.currentValue("motion")
            if (status == "${cMotionCmd}"){ 
                String device  = (String) deviceName
                devList += device
             }   
        }
        def devListSize = devList?.size()
        if(!cMotionAll) {
            if (devList?.size() > 0) { 
                cMotionOk = true  
            }
        }        
        if(cMotionAll) {
            if (devListSize == cMotionSize) { 
                cMotionOk = true 
            }
        }
        if (cMotionOk == false) log.warn "Motion Conditions Handler failed"
    }

    // PRESENCE
    if (cPresence == null) { cPresenceOk = true }
    if (cPresence) {
    if (parent.trace) log.trace "Conditions: Presence events method activated"
        def cPresenceSize = cPresence.size()
        cPresence.each { deviceName ->
            def status = deviceName.currentValue("presence")
            if (status == cPresenceCmd){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cPresenceAll) {
            if (devList?.size() > 0) { 
                cPresenceOk = true  
            }
        }        
        if(cPresenceAll) {
            if (devListSize == cPresenceSize) { 
                cPresenceOk = true 
            }
        }
        if (cPresenceOk == false) log.warn "Presence Conditions Handler failed"
    }

    // CONTACT SENSORS
    if (cContact == null) { cContactOk = true }
    if (cContact) {
    if (parent.trace) log.trace "Conditions: Contacts events method activated"
        def cContactSize = cContact?.size()
        cContact.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "${cContactCmd}"){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cContactAll) {
            if (devList?.size() > 0) { 
                cContactOk = true  
            }
        }        
        if(cContactAll) {
            if (devListSize == cContactSize) { 
                cContactOk = true 
            }
        }
        if (cContactOk == false) log.warn "Contacts Conditions Handler failed"
    }

    // DOOR CONTACT SENSORS
    if (cContactDoor == null) { cDoorOk = true }
    if (cContactDoor) {
    if (parent.trace) log.trace "Conditions: Door Contacts events method activated"
        def cContactDoorSize = cContactDoor?.size()
        cContactDoor.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "${cContactDoorCmd}"){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cContactDoorAll) {
            if (devList?.size() > 0) { 
                cDoorOk = true  
            }
        }        
        if(cContactDoorAll) {
            if (devListSize == cContactDoorSize) { 
                cDoorOk = true 
            }
        }
        if (cDoorOk == false) log.warn "Door Contacts Conditions Handler failed"
    }

    // WINDOW CONTACT SENSORS
    if (cContactWindow == null) { cWindowOk = true }
    if (cContactWindow) {
    if (parent.trace) log.trace "Conditions: Window Contacts events method activated"
        def cContactWindowSize = cContactWindow?.size()
        cContactWindow.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == cContactWindowCmd){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cContactWindowAll) {
            if (devList?.size() > 0) { 
                cWindowOk = true  
            }
        }        
        if(cContactWindowAll) {
            if (devListSize == cContactWindowSize) { 
                cWindowOk = true 
            }
        }
        if (cWindowOk == false) log.warn "Window Contacts Conditions Handler failed"
    }

    // GARAGE DOORS
    if (cGarage == null) { cGarageOk = true }
    if (cGarage) {
    if (parent.trace) log.trace "Conditions: Garage Doors events method activated"
        cGarage.each { deviceName ->
            def status = deviceName.currentValue("door")
            if (status == "${cGarageCmd}"){
            cGarageOk = true
            }
            if (cGarageOk == false) log.warn "Garage Conditions Handler failed"
        }
    }    
    // LOCKS
    if (cLocks == null) { cLocksOk = true }
    if (cLocks) {
    if (parent.trace) log.trace "Conditions: Locks events method activated"
        cLocks.each { deviceName ->
            def status = deviceName.currentValue("lock")
            if (status == "${cLocksCmd}"){
            cLocksOk = true
            }
            if (cLocksOk == false) log.warn "Locks Conditions Handler failed"
        }
    }    


    if (cLocksOk==true && cGarageOk==true && cTempOk==true && cHumOk==true && cSHMOk==true && cDimOk==true && cSwitchOk==true && cModeOk==true && 
        cMotionOk==true && cPresenceOk==true && cDoorOk==true && cWindowOk==true && cContactOk==true && cDaysOk==true){ // && getTimeOk(evt)==true) { 
        result = true
    }
    if (result == true) {
        if (parent.debug) log.warn "Conditions Handler ==> All Conditions have been met"
    } else {
        log.warn "Conditions Handler ==>  \n" +
        "*************************************************************************** \n" +
        "**** cLocksOk=$cLocksOk, cGarageOk=$cGarageOk, cTempOk=$cTempOk 		 \n" +
        "**** cHumOk=$cHumOk, SHM=$cSHMOk, cDim=$cDimOk, cSwitchOk=$cSwitchOk 	 \n" + 
        "**** cModeOk=$cModeOk, cMotionOk=$cMotionOk, cPresenceOk=$cPresenceOk 	 \n" +
        "**** cDoorOk=$cDoorOk,	cWindowOk=$cWindowOk, cContactOk=$cContactOk 	 \n" +
        "**** cDaysOk=$cDaysOk, getTimeOk=" + getTimeOk(evt) +					 "\n" +
        "***************************************************************************"
    }
    return result
}



void settingUpdate(name, value, type=null) {
    if(name && type) {
        app?.updateSetting("$name", [type: "$type", value: value])
    }
    else if (name && type == null) { app?.updateSetting(name.toString(), value) }
}

private stateCleanup() {
    List items = ["availableDevices", "lastMsgDt", "consecutiveCmdCnt", "isRateLimiting", "versionData", "heartbeatScheduled", "serviceAuthenticated", ]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    state?.pollBlocked = false
    state?.resumeConfig = false
    state?.deviceRefreshInProgress = false
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

def getShmIncidents() {
    //Thanks Adrian
    def incidentThreshold = now() - 604800000
    return location.activeIncidents.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}

def getShmStatus() {
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

Boolean isInMode(modes) {
    if(modes) { return (location?.mode?.toString() in mode) }
    return false
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