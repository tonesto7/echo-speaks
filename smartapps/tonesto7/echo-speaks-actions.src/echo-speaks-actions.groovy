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

String appVersion()	 { return "2.0.7" }
String appModified() { return "2018-12-13" }
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
    /* TODO:

        -TIME TRIGGERS-
            Time of Day
            Days of week
            Months (?)

        -DEVICE TRIGGERS-
            Presence (Present/NotPresent) (Stays?)
            Water (Wet/Dry)
            Contacts (Opened/Closed) (Stays?)
            Motion (Active/Inactive) (Stays?)
            Valves (Opened/Closed) (Stays?)
            Switches (On/Off) (Stays?)
            Garages (Opened/Closed) (Stays?)
            Locks (Locked/Unlocked) (Stays?)
            Buttons
            Temperature (Above/Below/Equals)
            Power (Above/Below/Equals)
            Humidity (Above/Below/Equals)
            WindowShades (Open/Closed)
            Thermostats(?)
            Smoke Detectors (?)
            Carbon Dioxide (?)

        -LOCATION TRIGGERS-
            Modes
            SHM
            Routines
            Sunrise/Sunset

        -WEATHER TRIGGERS- (MAYBE)
            FORCAST hot words
    */

    /* TODO:
        -ACTIONS-
            PlsyWeather
            PlayTraffic
            FlashBriefing
    */

    List enumOpts = []
    Map buildItems = [:]
    buildItems["Date/Time"] = ["Scheduled": "Scheduled"]?.sort{ it?.key }
    buildItems["Location"] = ["Modes":"Modes", "Routines":"Routines"]?.sort{ it?.key }
    buildItems["Safety & Security"] = ["Smart Home Monitor": "Smart Home Monitor", "CO2 & Smoke":"CO\u00B2 & Smoke"]?.sort{ it?.key }
    buildItems["Actionable Devices"] = [ "Locks":"Locks", "Outlets, Switches, Dimmers":"Outlets, Switches, Dimmers", "Garage Door Openers":"Garage Door Openers", "Valves":"Valves", "Window Shades":"Window Shades", "Buttons":"Buttons"]?.sort{ it?.key }
    buildItems["Sensor Device"] = ["Doors, Windows, Contacts":"Doors, Windows, Contacts", "Motion":"Motion", "Presence":"Presence", "Temperature":"Temperature", "Humidity":"Humidity", "Water":"Water", "Power":"Power"]?.sort{ it?.key }

    buildItems?.each { key, val-> addInputGrp(enumOpts, key, val) }
    return enumOpts
}

private String inputDesc(type) {
    def selItems = selTriggerTypes(type) ?: null
    return settings[type]?.size() ? "${selItems ? "Events: [${selItems}]\n" : ""}(${settings[type]?.size()}) Selected\n\ntap to modify" : "tap to select"
}


def mainPage() {
    Boolean newInstall = !state?.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? "" : "namePage"), uninstall: newInstall, install: !newInstall) {
        appInfoSect()
        if(!settings?.actionPause) {
            // section("Triggers:", hideable: true, hidden: (settings?.triggerTypes?.size())) {
                // input "triggerTypes", "enum", title: "Select Triggers (Multiple Allowed)", description: "Tap to select", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getAppImg("trigger.png")
            // }
            section ("Action Configuration") {
                    href "triggersPage", title: "Configure Triggers", description: ""
                    href "conditionsPage", title: "Configure Conditions", description: ""
                    href "actionsPage", title: "Configure Actions", description: ""
                    // href "pSend", title: "Audio, Push, SMS messages, and Reports", description: ""
                }
        } else {
            paragraph "This Action is currently in a paused state...  To edit the configuration please un-pause", required: true, state: null, image: getAppImg("notice.png")
        }


        if(state?.isInstalled) {
            section ("Place this action on hold:") {
                   input "actionPause", "bool", title: "Pause this Actions from Running?", defaultValue: false, submitOnChange: true, image: getAppImg("pause.png")
            }
            section("Remove Broadcast Group:") {
                href "uninstallPage", title: "Remove this Group", description: "Tap to Remove...", image: getAppImg("uninstall.png")
            }
        }
    }
}

def triggersPage() {
    return dynamicPage(name: "triggersPage", uninstall: false, install: false) {
        def stRoutines = location.helloHome?.getPhrases()*.label.sort()
        section ("Select Capabilities") {
            input "triggerEvents", "enum", title: "Select Trigger Types to configure...", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true
        }
        if (settings?.triggerEvents?.size()) {
            if ("Scheduled" in settings?.triggerEvents) {
                section("Time Events", hideable: true) {
                    input "trig_SunState", "enum", title: "Sunrise or Sunset...", options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true
                    if(settings?.trigSunState) {
                        input "offset", "number", range: "*..*", title: "Offset event this number of minutes (+/-)", required: true
                    }
                    input "trig_Schedule", "enum", title: "Date/Time Schedule", submitOnChange: true, required: false, options: ["One Time", "Recurring"]
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
                    input "trig_SHM", "enum", title: "Smart Home Monitor", options:["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed", "alerts": "Alerts"], multiple: true, required: true, submitOnChange: true
                }
            }

            if ("Modes" in settings?.triggerEvents) {
                section ("Mode Events", hideable: true) {
                    input "trig_Mode", "mode", title: "Location Modes", multiple: true, required: true, submitOnChange: true
                }
            }

            if("Routines" in settings?.triggerEvents) {
                if ("Routines" in settings?.triggerEvents) {
                    section("Routine Events", hideable: true) {
                        input "trig_Routine", "enum", title: "Routines", options: stRoutines, multiple: true, required: true
                    }
                }
            }

            if ("Outlets, Switches, Dimmers" in settings?.triggerEvents) {
                section ("Switches", hideable: true) {
                    input "trig_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required: false
                    if (settings?.trig_Switch) {
                        input "trig_SwitchCmd", "enum", title: "are turned...", options:["on": "on","off": "off"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Switch?.size() > 1) {
                            input "trig_SwitchAll", "bool", title: "ALL switches to be ${settings?.trig_SwitchCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
                section ("Dimmers", hideable: true) {
                    input "trig_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false
                    if (settings?.trig_Dimmer) {
                        input "trig_DimmersCmd", "enum", title: "turn...", options:["on": "on","off": "off","greater": "to greater than","lessThan": "to less than","equal": "to being equal to"], multiple: false, required: false, submitOnChange: true
                        if (settings?.trig_DimmerCmd in ["greater", "lessThan", "equal"]) {
                            input "trig_DimmerLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                        }
                    }
                }
                section ("Outlets", hideable: true) {
                    input "trig_Outlet", "capability.outlet", title: "Outlets", multiple: true, submitOnChange: true, required:false
                    if (settings?.trig_Outlet) {
                        input "trig_OutletCmd", "enum", title: "are turned...", options:["on": "on","off": "off"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Outlet?.size() > 1) {
                            input "trig_OutletAll", "bool", title: "ALL switches to be ${settings?.trig_OutletCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Motion" in settings?.triggerEvents) {
                section ("Motion Sensors", hideable: true) {
                    input "trig_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Motion) {
                        input "trig_MotionCmd", "enum", title: "become...", options: ["active": "active", "inactive": "inactive"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Motion?.size() > 1) {
                            input "trig_MotionAll", "bool", title: "ALL motion sensors to be ${settings?.trig_MotionCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Presence" in settings?.triggerEvents) {
                section ("Presence Events", hideable: true) {
                    input "trig_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Presence) {
                        input "trig_PresenceCmd", "enum", title: "have...", options: ["present":"Arrived","not present":"Departed"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Presence?.size() > 1) {
                            input "trig_PresenceAll", "bool", title: "ALL presence sensors to be ${settings?.trig_PresenceCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }
            if ("Garage Door Openers" in settings?.triggerEvents) {
                section ("Garage Door Openers", hideable: true) {
                    input "trig_Garage", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Garage) {
                        input "trig_GarageCmd", "enum", title: "are...", options:["open":"opened", "close":"closed", "opening":"opening", "closing":"closing"], multiple: false, required: true, submitOnChange: true
                    }
                }
            }
            if ("Doors, Windows, & Contacts" in settings?.triggerEvents) {
                section ("Doors", hideable: true) {
                    input "trig_ContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_ContactDoor) {
                        input "trig_ContactDoorCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactDoor?.size() > 1) {
                            input "trig_ContactDoorAll", "bool", title: "ALLdoors to be ${settings?.trig_ContactDoorCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
                section ("Windows", hideable: true) {
                    input "trig_ContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_ContactWindow) {
                        input "trig_ContactWindowCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_ContactWindow?.size() > 1) {
                            input "trig_ContactWindowAll", "bool", title: "ALL windows to be ${settings?.trig_ContactWindowCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
                section ("Contact Sensors", hideable: true) {
                    input "trig_Contact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Contact) {
                        input "trig_ContactCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                        if (settings?.trig_Contact?.size() > 1) {
                            input "trig_ContactAll", "bool", title: "ALL contact sensors to be ${settings?.trig_ContactCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }
            
            if ("Locks" in settings?.triggerEvents) {
                section ("Locks", hideable: true) {
                    input "trig_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Locks) {
                        input "trig_LocksCmd", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
                        if (settings?.trig_Locks?.size() > 1) {
                            input "trig_LocksAll", "bool", title: "ALL locks to be ${settings?.trig_LocksCmd ?: ""}.", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
            }

            if ("Keypads" in settings?.triggerEvents) {
                section ("Keypads", hideable: true) {
                    input "trig_Keypads", "capability.lockCodes", title: "Select Keypads", multiple: true, required: false, submitOnChange: true
                    if (settings?.trig_Keypads) {
                        input "trig_KeyCode", "number", title: "Code (4 digits)", required: true, submitOnChange: true
                        input "trig_KeyButton", "enum", title: "Which button?", options: ["on":"On", "off":"Off", "partial":"Partial", "panic":"Panic"], multiple: false, required: true, submitOnChange: true
                    }
                }
            }

            if ("Temperature" in settings?.triggerEvents) {
                section ("Temperature Sensor Events", hideable: true) {
                    input "trig_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true, image: getAppImg("")
                    input "trig_TempCond", "enum", title: "Temperature is...", options: ["between","below","above"], required: true, multiple: false, submitOnChange: true
                    if (settings?.trig_TempCond) {
                        if (settings?.trig_TempCond in ["between", "below"]) {
                            input "trig_tempLow", "number", title: "a ${trig_TempCond == "between" ? "Low " : ""}Temperature of...", required: true, submitOnChange: true
                        }
                        if (settings?.trig_TempCond in ["between", "above"]) {
                            input "trig_tempHigh", "number", title: "${trig_TempCond == "between" ? "and a high " : "a "}Temperature of...", required: true, submitOnChange: true
                        }
                        input "tempOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }

            if ("Humidity" in settings?.triggerEvents) {
                section ("Humidity Sensor Events", hideable: true) {
                    input "trig_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true
                    if (settings?.trig_tHumidity) {
                        input "trig_HumidityLevel", "enum", title: "Activate when Relative Humidity is...", options: ["above", "below"], required: false, submitOnChange: true
                        input "trig_HumidityPercent", "number", title: "Relative Humidity Level...", required: true, description: "percent", submitOnChange: true
                        input "trig_HumidityOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }
            if ("Water" in settings?.triggerEvents) {
                section ("Water Sensor Events", hideable: true) {
                    input "trig_tWater", "capability.waterSensor", title: "Water/Moisture Sensors", required: false, multiple: true, submitOnChange: true
                    if (settings?.trig_tWater) {
                        input "trig_WaterState", "enum", title: "Activate when state changes to...", options: ["wet", "dry", "both"], required: false
                    }
                }
            }
            if ("CO2 & Smoke" in settings?.triggerEvents) {
                section ("CO\u00B2 Events", hideable: true) {
                    input "trig_CO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO\u00B2)", required: false, multiple: true, submitOnChange: true
                    if (settings?.trig_CO2) {
                        input "trig_CO2State", "enum", title: "Activate when CO\u00B2 is...", options: ["above", "below"], required: false, submitOnChange: true
                        if (settings?.trig_CO2State) {
                            input "trig_CO2Level", "number", title: "CO\u00B2 Level...", required: true, description: "number", submitOnChange: true
                            input "trig_CO2Once", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                }
                section ("Smoke Events", hideable: true) {
                    input "trig_Smoke", "capability.smokeDetector", title: "Smoke Detectors", required: false, multiple: true, submitOnChange: true
                    if (settings?.trig_Smoke) {
                        input "trig_SmokeState", "enum", title: "Activate when smoke is...", options: ["detected", "clear", "both"], required: false
                    }
                }
            }
            if ("Illuminance" in settings?.triggerEvents) {
                section ("Illuminance Events", hideable: true) {
                    input "trig_illuminance", "capability.illuminanceMeasurement", title: "Lux Level", required: false, submitOnChange: true
                    if (settings?.trig_illuminance) {
                        input "trig_illuminanceLow", "number", title: "A low lux level of...", required: true, submitOnChange: true
                        input "trig_illuminanceHigh", "number", title: "and a high lux level of...", required: true, submitOnChange: true
                        input "trig_illuminanceOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }
            // input "tFeelsLike", "capability.relativeHumidityMeasurement", title: "How hot/cold it 'Feels'", required: false, multiple: true, submitOnChange: true
            // input "tWind", "capability.sensor", title: "Wind Speed", multiple: true, required: false, submitOnChange: true
            // if (tWind) {
            //     input "tWindLevel", "enum", title: "Activate when Wind Speed is...", options: ["above", "below"], required: false, submitOnChange: true
            //     input "tWindSpeed", "number", title: "Wind Speed Level...", required: true, description: "mph", submitOnChange: true
            //     input "windOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
            // }
            // input "tRain", "capability.sensor", title: "Rain Accumulation", multiple: true, required: false, submitOnChange: true
            // if (tRain) {
            //     input "tRainAction", "enum", title: "Activate when the Rain...", options: ["begins", "ends", "begins and ends"], required: false, defaultValue: true, submitOnChange: true
            //     if (tRainAction == "begins" || tRainAction == "begins and ends") {
            //         input "rainStartMsg", "text", title: "Send this message when it begins to rain"
            //     }
            //     if (tRainAction == "ends" || tRainAction == "begins and ends") {
            //         input "rainStopMsg", "text", title: "Send this message when it stops raining"
            //     }
            // }
        }
    }
}

/******************************************************************************
	CONDITIONS SELECTION PAGE
******************************************************************************/
def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: "Execute this Action when...", install: false, uninstall: false) {
        section ("Location Based Conditions") {
            input "cond_Mode", "mode", title: "Location Mode is...", multiple: true, required: false, submitOnChange: true
        	input "cond_SHM", "enum", title: "Smart Home Monitor is...", options:["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed"], multiple: false, required: false, submitOnChange: true
            input "cond_Days", "enum", title: "Days of the week", multiple: true, required: false, submitOnChange: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            href "timePage", title: "Time Schedule", description: "", state: ""
        }
        section ("Switch and Dimmer Conditions") {
            input "cond_Switch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false
            if (settings?.cond_Switch) {
                input "cond_SwitchCmd", "enum", title: "are...", options:["on":"On","off":"Off"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Switch?.size() > 1) {
                    input "cond_SwitchAll", "bool", title: "Activate this toggle if you want ALL of the switches to be $cond_SwitchCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_Dimmer", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false
            if (settings?.cond_Dimmer) {
                input "cond_DimmerCmd", "enum", title: "is...", options:["greater":"greater than","lessThan":"less than","equal":"equal to"], multiple: false, required: false, submitOnChange: true
                if (settings?.cond_DimmerCmd in ["greater", "lessThan", "equal"]) {
                    input "cond_DimmerLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                    if (settings?.cond_Dimmer?.size() > 1) {
                        input "cond_DimmmerAll", "bool", title: "Activate this toggle if you want ALL of the dimmers for this condition.", required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }
        }
        section ("Motion and Presence Conditions") {
            input "cond_Motion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_Motion) {
                input "cond_MotionCmd", "enum", title: "are...", options: ["active":"active", "inactive":"inactive"], multiple: false, required: true, submitOnChange: true
            	if (settings?.cond_Motion?.size() > 1) {
                	input "cond_MotionAll", "bool", title: "Activate this toggle if you want ALL of the Motion Sensors to be ${settings?.cond_MotionCmd ?: ""} as a condition."
                }
            }
        	input "cond_Presence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_Presence) {
                input "cond_PresenceCmd", "enum", title: "are...", options: ["present":"Present","not present":"Not Present"], multiple: false, required: true, submitOnChange: true
                if (settings?.cond_Presence?.size() > 1) {
                    input "cond_PresenceAll", "bool", title: "Activate this toggle if you want ALL of the Presence Sensors to be ${settings?.cPresenceCmd ?: ""} as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Door, Window, and other Contact Sensor Conditions") {
            input "cond_ContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_ContactDoor) {
                input "cond_ContactDoorCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (settings?.cond_ContactDoor?.size() > 1) {
                	input "cond_ContactDoorAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be ${settings?.cContactDoorCmd ?: ""} as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
            input "cond_ContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true
            	if (settings?.cond_ContactWindow) {
                input "cond_ContactWindowCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (settings?.cond_ContactWindow?.size() > 1) {
                	input "cond_ContactWindowAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be ${settings?.cContactWindowCmd ?: ""} as a condition.", required: false, defaultValue: false, submitOnChange: true
                    }
            	}
            input "cond_Contact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_Contact) {
                input "cond_ContactCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (settings?.cond_Contact?.size() > 1) {
                	input "cond_ContactAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be ${settings?.cContactCmd ?: ""} as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
		section ("Garage Door and Lock Conditions") {
            input "cond_Locks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_Locks) {
                input "cond_LocksCmd", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
            }
            input "cond_Garage", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true
            if (settings?.cond_Garage) {
                input "cond_GarageCmd", "enum", title: "are...", options:["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
        	}
        }
        section ("Environmental Conditions") {
        	input "cond_Humidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true
            if (settings?.cond_Humidity) {
                input "cond_HumidityLevel", "enum", title: "Only when the Humidity is...", options: ["above", "below"], required: false, submitOnChange: true
                if (settings?.cond_HumidityLevel) {
                    input "cond_HumidityPercent", "number", title: "this level...", required: true, description: "percent", submitOnChange: true
                }
                if (settings?.cond_HumidityPercent) {
                    input "cond_HumidityStop", "number", title: "...but not ${settings?.cond_HumidityLevel} this percentage", required: false, description: "humidity"
                }
            }
            input "cond_Temperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true
            if (settings?.cTemperature) {
                input "cond_TemperatureLevel", "enum", title: "When the temperature is...", options: ["above", "below"], required: false, submitOnChange: true
                if (settings?.cond_TemperatureLevel) {
                    input "cond_TemperatureDegrees", "number", title: "Temperature...", required: true, description: "degrees", submitOnChange: true
                }
                if (settings?.cond_TemperatureDegrees) {
                    input "cond_TemperatureStop", "number", title: "...but not ${settings?.cond_TemperatureLevel} this temperature", required: false, description: "degrees"
                }
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
    // TODO: Cleanup unselected trigger types
    // runIn(5, "subscribeToEvts")
}

private subscribeToEvts() {
    //LOCATION & SCHEDULING
    if(settings?.trig_shm) {
        subscribe(location, "alarmSystemStatus",shmModeChange)
    }
    if (settings?.trig_Modes)               { subscribe(location, processModeChange) }
    if (settings?.trig_Routine)             { subscribe(location, "routineExecuted", routineHandler) }
    if (settings?.trig_frequency)           { cronHandler(frequency) }
    if (settings?.trig_xFutureTime)         { oneTimeHandler() }
    if (mySunState == "Sunset") {
    subscribe(location, "sunsetTime", sunsetTimeHandler)
	sunsetTimeHandler(location.currentValue("sunsetTime"))
    }
    if (mySunState == "Sunrise") {
	subscribe(location, "sunriseTime", sunriseTimeHandler)
	sunriseTimeHandler(location.currentValue("sunriseTime"))
    }
    // DIMMERS
	if(tDimCmd == "on")							{ subscribe(tDim, "switch.on", processActions) } 
    if(tDimCmd == "off")						{ subscribe(tDim, "switch.off", processActions) }
    if(tDimCmd == "greater")					{ subscribe(tDim, "level", processActions) }
    if(tDimCmd == "lessThan")					{ subscribe(tDim, "level", processActions) }
    if(tDimCmd == "equal")						{ subscribe(tDim, "level", processActions) }

	// ENVIRONMENTAL
    if(tHumidity)								{ subscribe(tHumidity, "humidity", humidityHandler) }
    if(tTemperature)							{ subscribe(tTemperature, "temperature", tempHandler) } 
    if(tLux)									{ subscribe(tLux, "illuminance", luxHandler) }
    if(tWind)									{ subscribe(tWind, "WindStrength", windHandler) }
    if(tRain)									{ subscribe(tRain, "rain", rainHandler) }
    if(tLocks) 									{ subscribe(tLocks, "lock", locksTrigger) }
	if(myCO2) 									{ subscribe(myCO2, "carbonDioxide", CO2Handler) }
    if(tWater) {    
		if (tWaterStatus == "wet")				subscribe(tWater, "water.wet", processActions)
    	if (tWaterStatus == "dry")				subscribe(tWater, "water.dry", processActions)
    	if (tWaterStatus == "both")				subscribe(tWater, "water", processActions) }
	if(tSmoke) {    
		if (tSmokeStatus == "detected")			subscribe(tSmoke, "smoke.detected", processActions)
		if (tSmokeStatus == "clear")			subscribe(tSmoke, "smoke.clear", processActions)
		if (tSmokeStatus == "both")				subscribe(tSmoke, "smoke", processActions) }

	// MISC EVENTS
	if(tGarageCmd=="open") 						{ subscribe(tGarage, "contact.open", processActions) }
    if(tGarageCmd=="close") 					{ subscribe(tGarage, "contact.closed", processActions) } 
    if(tGarageCmd=="opening") 					{ subscribe(tGarage, "door.opening", processActions) }
    if(tGarageCmd=="closing") 					{ subscribe(tGarage, "door.closing", processActions) }

	if(tKeypads) 								{ subscribe(tKeypads, "codeEntered", codeEntryHandler) }    

    subscribe(tPresence, "presence", routingMethod)
    subscribe(tMotion, "motion", routingMethod)   
    subscribe(tContact, "contact", routingMethod)
    subscribe(tSwitch, "switch", routingMethod)
    subscribe(tLocks, "lock", routingMethod)
    subscribe(tContactWindow, "contact", routingMethod)
    subscribe(tContactDoor, "contact", routingMethod) 
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