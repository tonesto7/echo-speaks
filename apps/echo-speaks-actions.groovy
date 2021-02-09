/**
 *  Echo Speaks Actions (Hubitat)
 *
 *  Copyright 2018, 2019, 2020, 2021 Anthony Santilli
 *  Code Contributions by @nh.schottfam
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
 * ---------------------------------------------
    TODO: Custom Reports for multiple builtin in routine items. Reports for home status like temp, contact, alarm status.
 */

import groovy.transform.Field

@Field static final String appVersionFLD  = "4.0.7.0"
@Field static final String appModifiedFLD = "2021-02-08"
@Field static final String branchFLD      = "master"
@Field static final String platformFLD    = "Hubitat"
@Field static final Boolean betaFLD       = false
@Field static final Boolean devModeFLD    = false

@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sBULLET        = '\u2022'
@Field static final String sBULLETINV     = '\u25E6'
@Field static final String okSymFLD       = "\u2713"
@Field static final String notOkSymFLD    = "\u2715"
@Field static final String sFALSE         = 'false'
@Field static final String sTRUE          = 'true'
@Field static final String sBOOL          = 'bool'
@Field static final String sENUM          = 'enum'
@Field static final String sNUMBER        = 'number'
@Field static final String sTEXT          = 'text'
@Field static final String sTIME          = 'time'
@Field static final String sMODE          = 'mode'
@Field static final String sCOMPLT        = 'complete'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sTTM           = 'Tap to modify...'
@Field static final String sTTC           = 'Tap to configure...'
@Field static final String sTTCR          = 'Tap to configure (Required)'
@Field static final String sTTP           = 'Tap to proceed...'
//@Field static final String sTTS           = 'Tap to select...'
//@Field static final String sSETTINGS      = 'settings'
//@Field static final String sRESET         = 'reset'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sSWITCH        = 'switch'
@Field static final String sCHKBOX        = 'checkbox'
@Field static final String sCOMMAND       = 'command'
@Field static final String sANY           = 'any'
@Field static final String sBETWEEN       = 'between'
@Field static final String sBELOW         = 'below'
@Field static final String sABOVE         = 'above'
@Field static final String sEQUALS        = 'equals'

static String appVersion()  { return appVersionFLD }

definition(
    name: "Echo Speaks - Actions",
    namespace: "tonesto7",
    author: "Anthony Santilli",
    description: "DO NOT INSTALL FROM MARKETPLACE\n\nAllows you to create echo device actions based on device/location events in your SmartThings/Hubitat home.",
    category: "My Apps",
    parent: "tonesto7:Echo Speaks",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png",
    importUrl  : "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/apps/echo-speaks-actions.groovy")

preferences {
    page(name: "startPage")
    page(name: "uhOhPage")
    page(name: "codeUpdatePage")
    page(name: "mainPage")
    page(name: "prefsPage")
    page(name: "triggersPage")
    page(name: "conditionsPage")
    page(name: "condTimePage")
    page(name: "actionsPage")
    page(name: "actionTiersPage")
    page(name: "actionTiersConfigPage")
    page(name: "actTrigTasksPage")
    page(name: "actTierStartTasksPage")
    page(name: "actTierStopTasksPage")
    page(name: "actNotifPage")
    page(name: "actNotifTimePage")
    page(name: "actionHistoryPage")
    page(name: "searchTuneInResultsPage")
    page(name: "uninstallPage")
    page(name: "namePage")
}

def startPage() {
    if(parent != null) {
        if(!(Boolean)state.isInstalled && !(Boolean)parent?.childInstallOk()) { return uhOhPage() }
        else {
//            state.isParent = false
            List aa = settings.act_EchoDevices
            List devIt = aa.collect { it ? it.toInteger():null }
            app.updateSetting( "act_EchoDeviceList", [type: "capability", value: devIt?.unique()]) // this won't take effect until next execution
            return (minVersionFailed()) ? codeUpdatePage() : mainPage() }
    } else { return uhOhPage() }
}

def codeUpdatePage () {
    return dynamicPage(name: "codeUpdatePage", title: "Update is Required", install: false, uninstall: false) {
        section() { paragraph "Looks like your Action App needs an update\n\nPlease make sure all app and device code is updated to the most current version\n\nOnce updated your actions will resume normal operation.", required: true, state: null }
    }
}

def uhOhPage () {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            paragraph "HOUSTON WE HAVE A PROBLEM!\n\nEcho Speaks - Actions can't be directly installed from the Marketplace.\n\nPlease use the Echo Speaks SmartApp to configure them.", required: true,
            state: null
        }
    }
}

def appInfoSect()	{
    String instDt = state.dateInstalled ? fmtTime(state.dateInstalled, "MMM dd '@' h:mm a", true) : sNULL
    section() { href "empty", title: pTS("${app?.name}", getAppImg("es_actions")), description: "${instDt ? "Installed: ${instDt}\n" : sBLANK}Version: ${appVersionFLD}"  }
}

List cleanedTriggerList() {
    List newList = []
    settings.triggerTypes?.each { String tr ->
        newList.push(tr.split("::")[0] as String)
    }
    return newList.unique()
}

String selTriggerTypes(type) {
    return settings.triggerTypes?.findAll { it?.startsWith(type as String) }?.collect { it?.toString()?.split("::")[1] }?.join(", ")
}

private Map buildTriggerEnum() {
//    List enumOpts = []
    Map<String,Map> buildItems = [:]
    buildItems["Date/Time"] = ["scheduled":"Scheduled Time"]?.sort{ it?.key }
    buildItems["Location"] = [(sMODE):"Modes", "pistonExecuted":"Pistons"]?.sort{ it?.key }
    if(!settings.enableWebCoRE) {
        buildItems.Location.remove("pistonExecuted")
    }
    buildItems["Sensor Devices"] = ["contact":"Contacts | Doors | Windows", "battery":"Battery Level", "motion":"Motion", "illuminance": "Illuminance/Lux", "presence":"Presence", "temperature":"Temperature", "humidity":"Humidity", "water":"Water", "power":"Power", "acceleration":"Accelorometers"]?.sort{ it?.value }
    buildItems["Actionable Devices"] = ["lock":"Locks", "securityKeypad":"Keypads", "switch":"Switches/Outlets", "level":"Dimmers/Level", "door":"Garage Door Openers", "valve":"Valves", "shade":"Window Shades"]?.sort{ it?.value }
    buildItems["Thermostat Devices"] = ["coolingSetpoint":"Thermostat Cooling Setpoint", "heatingSetpoint":"Thermostat Heating Setpoint", "thermostatTemperature":"Thermostat Ambient Temp", "thermostatOperatingState":"Thermostat Operating State", "thermostatMode":"Thermostat Mode", "thermostatFanMode":"Thermostat Fan Mode"]?.sort{ it?.value }
    buildItems["Button Devices"] = ["pushed":"Button (Pushable)", "released":"Button (Releasable)", "held":"Button (Holdable)", "doubleTapped":"Button (Double Tapable)"]?.sort{ it?.value }
    buildItems["Safety & Security"] = ["alarm": "${getAlarmSystemName()}", "smoke":"Fire/Smoke", "carbon":"Carbon Monoxide", "guard":"Alexa Guard"]?.sort{ it?.value }
    if(!parent?.guardAutoConfigured()) { buildItems["Safety & Security"]?.remove("guard") }
    return buildItems.collectEntries { it?.value }?.sort { it?.value }
}

private static Map buildActTypeEnum() {
//    List enumOpts = []
    Map<String, Map> buildItems = [:]
    buildItems["Speech"] = ["speak":"Speak", "announcement":"Announcement", "speak_tiered":"Speak (Tiered)", "announcement_tiered":"Announcement (Tiered)"]?.sort{ it?.key }
    buildItems["Built-in Sounds"] = ["sounds":"Play a Sound"]?.sort{ it?.key }
    buildItems["Built-in Responses"] = ["weather":"Weather Report", "builtin":"Birthday, Compliments, Facts, Jokes, News, Stories, Traffic, and more...", "calendar":"Read Calendar Events"]?.sort{ it?.key }
    buildItems["Media/Playback"] = ["music":"Play Music/Playlists", "playback":"Playback/Volume Control"]?.sort{ it?.key }
    buildItems["Alarms/Reminders"] = ["alarm":"Create Alarm", "reminder":"Create Reminder"]?.sort{ it?.key }
    buildItems["Devices Settings"] = ["wakeword":"Change Wake Word", "dnd":"Set Do Not Disturb", "bluetooth":"Bluetooth Control"]?.sort{ it?.key }
    buildItems["Custom"] = ["voicecmd":"Execute a voice command","sequence":"Execute Sequence", "alexaroutine": "Execute Alexa Routine(s)"]?.sort{ it?.key }
    return buildItems.collectEntries { it?.value }?.sort { it?.value }
}

def mainPage() {
    Boolean newInstall = (!(Boolean)state.isInstalled)
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? sBLANK : "namePage"), uninstall: newInstall, install: !newInstall) {
        Boolean dup = (settings.duplicateFlag == true && state.dupPendingSetup == true)
        if(dup) {
            state.dupOpenedByUser = true
            section() { paragraph spanWrapImg("This Action was created from an existing action.<br><br>Please review the settings and save to activate...<br>${state.badMode ?: sBLANK}", sCLRORG, true, getAppImg("pause_orange")) }
        }

        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
        }
        appInfoSect()
        Boolean paused = isPaused()
        Boolean trigConf
        Boolean condConf
        Boolean actConf
        Boolean allOk

        if(paused) {
            section() {
                paragraph pTS("This Action is currently in a paused state...\nTo edit the please un-pause", getAppImg("pause_orange"), false, sCLRRED), required: true, state: null
            }
        } else {
            if(settings.cond_mode && !settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
            trigConf = triggersConfigured()
            condConf = conditionsConfigured()
            actConf = executionConfigured()
            allOk = (Boolean) ((String)settings.actionType && trigConf && actConf)
            section(sTS("Configuration: Part 1")) {
                input "actionType", sENUM, title: inTS1("Action Type", "list"), description: sBLANK, options: buildActTypeEnum(), multiple: false, required: true, submitOnChange: true
            }
            if (newInstall) {
                section("Configuration: Part 2") {
                    paragraph pTS("Further Options will be configured once you save this automation.  Please save and return to complete", getAppImg("info"))
                }
            } else {
                section (sTS("Configuration: Part 2")) {
                    if((String)settings.actionType) {
                        href "triggersPage", title: inTS1("Action Triggers", "trigger"), description: getTriggersDesc(), state: (trigConf ? sCOMPLT : sBLANK), required: true
                    } else { paragraph pTS("These options will be shown once the action type is configured.", getAppImg("info")) }
                }
                section(sTS("Configuration: Part 3")) {
                    if((String)settings.actionType && trigConf) {
                        href "conditionsPage", title: inTS1("Condition/Restrictions\n(Optional)", "conditions"), description: getConditionsDesc(), state: (condConf ? sCOMPLT: sBLANK)
                    } else { paragraph pTS("These options will be shown once the triggers are configured.", getAppImg("info")) }
                }
                section(sTS("Configuration: Part 4")) {
                    if((String)settings.actionType && trigConf) {
                        href "actionsPage", title: inTS1("Execution Config", "es_actions"), description: getActionDesc(), state: (actConf ? sCOMPLT : sBLANK), required: true
                    } else { paragraph pTS("These options will be shown once the triggers are configured.", getAppImg("info")) }
                }
                if(allOk) {
                    section(sTS("Notifications:")) {
                        String t0 = getAppNotifDesc()
                        href "actNotifPage", title: inTS1("Send Notifications", "notification2"), description: (t0 ? "${t0}\n\n"+sTTM : sTTC), state: (t0 ? sCOMPLT : null)
                    }
                    // getTierStatusSection()

                    section(sTS("Action History")) {
                        href "actionHistoryPage", title: inTS1("View Action History", "tasks"), description: sBLANK
                    }
                }
            }
        }

        section(sTS("Preferences")) {
            href "prefsPage", title: inTS1("Debug/Preferences", "settings"), description: sBLANK
            if(!newInstall) {
                input "actionPause", sBOOL, title: inTS1("Pause Action?", "pause_orange"), defaultValue: false, submitOnChange: true
                if((Boolean)settings.actionPause) { unsubscribe() }
                else {
                    input "actTestRun", sBOOL, title: inTS1("Test this action?", "testing"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                    if(actTestRun) { executeActTest() }
                }
            }
        }

        if(!newInstall) {
            section(sTS("Name this Action:")) {
                input "appLbl", sTEXT, title: inTS1("Action Name", "name_tag"), description: sBLANK, required:true, submitOnChange: true
            }
            section(sTS("Remove Action:")) {
                href "uninstallPage", title: inTS1("Remove this Action", "uninstall"), description: "Tap to Remove..."
            }
            if(allOk) {
                section(sTS("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                    String issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28ACTIONS+BUG%29+&projects=echo-speaks%2F6"
                    String featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BActions+Feature+Request%5D&projects=echo-speaks%2F6"
                    href url: featUrl, style: sEXTNRL, required: false, title: inTS1("New Feature Request", "www"), description: "Tap to open browser"
                    href url: issueUrl, style: sEXTNRL, required: false, title: inTS1("Report an Issue", "www"), description: "Tap to open browser"
                }
            }
        }
    }
}

def prefsPage() {
    return dynamicPage(name: "prefsPage", install: false, uninstall: false) {
        section(sTS("Logging:")) {
            input "logInfo",  sBOOL, title: inTS1("Show Info Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logWarn",  sBOOL, title: inTS1("Show Warning Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logError", sBOOL, title: inTS1("Show Error Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logDebug", sBOOL, title: inTS1("Show Debug Logs?", sDEBUG), description: "Auto disables after 6 hours", required: false, defaultValue: false, submitOnChange: true
            input "logTrace", sBOOL, title: inTS1("Show Detailed Logs?", sDEBUG), description: "Only enable when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
        }
        if((Boolean)state.isInstalled) {
            if(advLogsActive()) { logsEnabled() }
            section(sTS("Other:")) {
                input "clrEvtHistory", sBOOL, title: inTS1("Clear Device Event History?", "reset"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if(clrEvtHistory) { clearEvtHistory() }
            }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section(sTS("Name this Automation:")) {
            input "appLbl", sTEXT, title: inTS1("Label this Action", "name_tag"), description: sBLANK, required:true, submitOnChange: true
        }
    }
}

def actionHistoryPage() {
    return dynamicPage(name: "actionHistoryPage", install: false, uninstall: false) {
        section() {
            getActionHistory()
        }
        if( ((List)getMemStoreItem("actionHistory")).size() ) {
            section(sBLANK) {
                input "clearActionHistory", sBOOL, title: inTS1("Clear Action History?", "reset"), description: "Clears Stored Action History.", defaultValue: false, submitOnChange: true
                //private List getMemStoreItem(String key){
                if(settings.clearActionHistory) {
                    settingUpdate("clearActionHistory", sFALSE, sBOOL)
                    clearActHistory()
                }
            }
        }
    }
}

// TODO: Add flag to check for the old schedule settings and pause the action, and notifiy the user.
private scheduleConvert() {
    if(settings.trig_scheduled_time || settings.trig_scheduled_sunState && !settings.trig_scheduled_type) {
        if(settings.trig_scheduled_sunState) { settingUpdate("trig_scheduled_type", "${settings.trig_scheduled_sunState}", sENUM); settingRemove("trig_scheduled_sunState") }
        else if(settings.trig_scheduled_time && settings.trig_scheduled_recurrence) {
            if(settings.trig_scheduled_recurrence == "Once") { settingUpdate("trig_scheduled_type", "One-Time", sENUM) }
            if(settings.trig_scheduled_recurrence in ["Daily", "Weekly", "Monthly"]) { settingUpdate("trig_scheduled_type", "Recurring", sENUM) }
        }
    }
}

def triggersPage() {
    return dynamicPage(name: "triggersPage", nextPage: "mainPage", uninstall: false, install: false) {
//        Boolean isTierAct = isTierAction()
        String a = getTriggersDesc(false, false)
        if(a) {
            section() {
                paragraph pTS(a, sNULL, false, sCLR4D9), state: sCOMPLT
            }
        }
        section (sTS("Enable webCoRE Integration:")) {
            input "enableWebCoRE", sBOOL, title: inTS("Enable webCoRE Integration", webCore_icon()), required: false, defaultValue: false, submitOnChange: true
        }
        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
        }
        Boolean showSpeakEvtVars = false
        section (sTS("Select Capabilities")) {
            input "triggerEvents", sENUM, title: inTS1("Select Trigger Event(s)", "trigger"), options: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true
        }
        Integer trigEvtCnt = settings.triggerEvents?.size()
        if (trigEvtCnt) {
            Integer trigItemCnt = 0
            if(!(settings.triggerEvents in ["scheduled", "weather"])) { showSpeakEvtVars = true }

            if (valTrigEvt("scheduled")) {
                section(sTS("Time Based Events"), hideable: true) {
                    List schedTypes = ["One-Time", "Recurring", "Sunrise", "Sunset"]
                    input "trig_scheduled_type", sENUM, title: inTS1("Schedule Type?", sCHKBOX), options: schedTypes, multiple: false, required: true, submitOnChange: true
                    String schedType = (String)settings.trig_scheduled_type
                    if(schedType) {
                        switch(schedType) {
                            case "One-Time":
                            case "Recurring":
                                input "trig_scheduled_time", sTIME, title: inTS1("Trigger Time?", "clock"), required: false, submitOnChange: true
                                if(settings.trig_scheduled_time && schedType == "Recurring") {
                                    List recurOpts = ["Daily", "Weekly", "Monthly"] // "Yearly"
                                    input "trig_scheduled_recurrence", sENUM, title: inTS1("Recurrence?", "day_calendar"), description: sBLANK, multiple: false, required: true, submitOnChange: true, options: recurOpts, defaultValue: "Once"
                                    // TODO: Build out the scheduling some more with quick items like below
                                    /*
                                        At 6 pm on the last day of every month: (0 0 18 L * ?)
                                        At 6 pm on the 3rd to last day of every month: (0 0 18 L-3 * ?)
                                        At 10:30 am on the last Thursday of every month: (0 30 10 ? * 5L)
                                        At 6 pm on the last Friday of every month during the years 2015, 2016 and 2017: (0 0 18 ? * 6L 2015-2017)
                                        At 10 am on the third Monday of every month: (0 0 10 ? * 2#3)
                                        At 12 am midnight on every day for five days starting on the 10th day of the month: (0 0 0 10/5 * ?)
                                    */
                                    String schedRecur = (String)settings.trig_scheduled_recurrence
                                    if(schedRecur) {
                                        switch(schedRecur) {
                                            case "Daily":
                                                input "trig_scheduled_weekdays", sENUM, title: inTS1("Only of these Days of the Week?", "day_calendar"), description: "(Optional)", multiple: true, required: false, submitOnChange: true, options: daysOfWeekMap()
                                                break

                                            case "Weekly":
                                                input "trig_scheduled_weekdays", sENUM, title: inTS1("Days of the Week?", "day_calendar"), description: sBLANK, multiple: true, required: true, submitOnChange: true, options: daysOfWeekMap()
                                                input "trig_scheduled_weeks", sENUM, title: inTS1("Only these Weeks on the Month?", "day_calendar"), description: "(Optional)", multiple: true, required: false, submitOnChange: true, options: weeksOfMonthMap()
                                                input "trig_scheduled_months", sENUM, title: inTS1("Only on these Months?", "day_calendar"), description: "(Optional)", multiple: true, required: false, submitOnChange: true, options: monthMap()
                                                break

                                            case "Monthly":
                                                input "trig_scheduled_daynums", sENUM, title: inTS1("Days of the Month?", "day_calendar"), description: (!settings.trig_scheduled_weeks ? "(Optional)" : sBLANK), multiple: true, required: (!settings.trig_scheduled_weeks), submitOnChange: true, options: (1..31)?.collect { it as String }
                                                if(!settings.trig_scheduled_daynums) {
                                                    input "trig_scheduled_weeks", sENUM, title: inTS1("Weeks of the Month?", "day_calendar"), description: (!settings.trig_scheduled_daynums ? "(Optional)" : sBLANK), multiple: true, required: (!settings.trig_scheduled_daynums), submitOnChange: true, options: weeksOfMonthMap()
                                                }
                                                input "trig_scheduled_months", sENUM, title: inTS1("Only on these Months?", "day_calendar"), description: "(Optional)", multiple: true, required: false, submitOnChange: true, options: monthMap()
                                                break
                                        }
                                    }
                                }
                                break
                            case "Sunrise":
                            case "Sunset":
                                input "trig_scheduled_sunState_offset", sNUMBER, range: "*..*", title: inTS1("Offset ${schedType} this number of minutes (+/-)", schedType?.toLowerCase()), required: false
                                break
                        }
                    }
                }
            }

            if (valTrigEvt("alarm")) {
                section (sTS("${getAlarmSystemName()} (${getAlarmSystemName(true)}) Events"), hideable: true) {
                    input "trig_alarm", sENUM, title: inTS1("${getAlarmSystemName()} Modes", "alarm_home"), options: getAlarmTrigOpts(), multiple: true, required: true, submitOnChange: true
                    if(!("alerts" in settings.trig_alarm)) {
                        input "trig_alarm_events", sENUM, title: inTS1("${getAlarmSystemName()} Alert Events", "alarm_home"), options: getAlarmSystemAlertOptions(), multiple: true, required: true, submitOnChange: true
                    }
                    if(settings.trig_alarm) {
                        // input "trig_alarm_once", sBOOL, title: inTS1("Only alert once a day?\n(per mode)", "question"), required: false, defaultValue: false, submitOnChange: true
                        // input "trig_alarm_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                        triggerVariableDesc("alarm", false, trigItemCnt++)
                    }
                }
            }

            if (valTrigEvt("guard")) {
                section (sTS("Alexa Guard Events"), hideable: true) {
                    input "trig_guard", sENUM, title: inTS1("Alexa Guard Modes", "alarm_home"), options: ["ARMED_STAY", "ARMED_AWAY", sANY], multiple: true, required: true, submitOnChange: true
                    if(settings.trig_guard) {
                        // input "trig_guard_once", sBOOL, title: inTS1("Only alert once a day?\n(per mode)", "question"), required: false, defaultValue: false, submitOnChange: true
                        // input "trig_guard_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                        triggerVariableDesc("guard", false, trigItemCnt++)
                    }
                }
            }

            if (valTrigEvt(sMODE)) {
                section (sTS("Mode Events"), hideable: true) {
                    input "trig_mode", sMODE, title: inTS1("Location Modes", sMODE), multiple: true, required: true, submitOnChange: true
                    if(settings.trig_mode) {
                        input "trig_mode_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: mode)", "question"), required: false, defaultValue: false, submitOnChange: true
                        input "trig_mode_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                        triggerVariableDesc(sMODE, false, trigItemCnt++)
                    }
                }
            }

            if(valTrigEvt("pistonExecuted")) {
                section(sTS("webCoRE Piston Executed Events"), hideable: true) {
                    input "trig_pistonExecuted", sENUM, title: inTS("Pistons", webCore_icon()), options: webCoRE_list('name'), multiple: true, required: true, submitOnChange: true
                    if(settings.trig_pistonExecuted) {
                        paragraph pTS("webCoRE settings must be enabled to send events for Piston Execution (not enabled by default in webCoRE)", sNULL, false, sCLRGRY)
                        input "trig_pistonExecuted_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: piston)", "question"), required: false, defaultValue: false, submitOnChange: true
                        input "trig_pistonExecuted_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                        triggerVariableDesc("pistonExecuted", false, trigItemCnt++)
                    }
                }
            }

            if (valTrigEvt(sSWITCH)) {
                trigNonNumSect(sSWITCH, sSWITCH, "Switches", "Switches", lONOFF+lANY, "are turned", lONOFF, sSWITCH, trigItemCnt++)
            }

            if (valTrigEvt("level")) {
                trigNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level is", "speed_knob", trigItemCnt++)
            }

            if (valTrigEvt("battery")) {
                trigNumValSect("battery", "battery", "Battery Level", "Batteries", "Level is", "speed_knob", trigItemCnt++)
            }

            if (valTrigEvt("motion")) {
                trigNonNumSect("motion", "motionSensor", "Motion Sensors", "Motion Sensors", lACTINACT+[sANY], "become", lACTINACT, "motion", trigItemCnt++)
            }

            if (valTrigEvt("presence")) {
                trigNonNumSect("presence", "presenceSensor", "Presence Sensors", "Presence Sensors", ["present", "not present", sANY], "changes to", ["present", "not present"], "presence", trigItemCnt++)
            }

            if (valTrigEvt("contact")) {
                trigNonNumSect("contact", "contactSensor", "Contacts, Doors, Windows", "Contacts, Doors, Windows", lOPNCLS+[sANY], "changes to", lOPNCLS, "contact", trigItemCnt++)
            }

            if (valTrigEvt("acceleration")) {
                trigNonNumSect("acceleration", "accelerationSensor", "Accelorometers", "Accelorometers", lACTINACT+[sANY], "changes to", lACTINACT, "acceleration", trigItemCnt++)
            }

            if (valTrigEvt("door")) {
                trigNonNumSect("door", "garageDoorControl", "Garage Door Openers", "Garage Doors", lOPNCLS+["opening", "closing", sANY], "changes to", lOPNCLS, "garage_door", trigItemCnt++)
            }

            if (valTrigEvt("lock")) {
                trigNonNumSect("lock", "lock", "Locks", "Smart Locks", ["locked", "unlocked", sANY], "changes to", ["locked", "unlocked"], "lock", trigItemCnt++, (settings.trig_lock_Codes), ((settings.trig_lock && settings.trig_lock_cmd in ["unlocked", sANY])  ? this.&handleCodeSect : this.&dummy), "Unlocked" )
            }

            if (valTrigEvt("securityKeypad")) {
                trigNonNumSect("securityKeypad", "securityKeypad", "Security Keypad", "Security Keypad", ["disarmed", "armed home", "armed away", "unknown", sANY], "changes to", ["disarmed", "armed home", "armed away", "unknown"], "lock", trigItemCnt++, (settings.trig_securityKeypad_Codes), ((settings.trig_securityKeypad && settings.trig_securityKeypad_cmd in ["disarmed", sANY]) ? this.&handleCodeSect : this.&dummy), "Keypad Disarmed" )
            }

            if (valTrigEvt("pushed")) {
                section (sTS("Button Pushed Events"), hideable: true) {
                    input "trig_pushed", "capability.pushableButton", title: inTS1("Pushable Buttons", "button"), required: true, multiple: true, submitOnChange: true
                    if (settings.trig_pushed) {
                        settingUpdate("trig_pushed_cmd", "pushed", sENUM)
                        //input "trig_pushed_cmd", sENUM, title: inTS1("Pushed changes", sCOMMAND), options: ["pushed"], required: true, multiple: false, defaultValue: "pushed", submitOnChange: true
                        input "trig_pushed_nums", sENUM, title: inTS1("button numbers?", sCOMMAND), options: 1..8, required: true, multiple: true,  submitOnChange: true
                        if(settings.trig_pushed_nums) {
                            triggerVariableDesc("pushed", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("released")) {
                section (sTS("Button Released Events"), hideable: true) {
                    input "trig_released", "capability.releasableButton", title: inTS1("Releasable Buttons", "button"), required: true, multiple: true, submitOnChange: true
                    if (settings.trig_released) {
                        settingUpdate("trig_released_cmd", "released", sENUM)
                        //input "trig_released_cmd", sENUM, title: inTS1("Released changes", sCOMMAND), options: ["released"], required: true, multiple: false, defaultValue: "released", submitOnChange: true
                        input "trig_released_nums", sENUM, title: inTS1("button numbers?", sCOMMAND), options: 1..8, required: true, multiple: true, submitOnChange: true
                        if(settings.trig_released_nums) {
                            triggerVariableDesc("released", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("held")) {
                section (sTS("Button Held Events"), hideable: true) {
                    input "trig_held", "capability.holdableButton", title: inTS1("Holdable Buttons", "button"), required: true, multiple: true, submitOnChange: true
                    if (settings.trig_held) {
                        settingUpdate("trig_held_cmd", "held", sENUM)
                        //input "trig_held_cmd", sENUM, title: inTS1("Held changes", sCOMMAND), options: ["held"], required: true, multiple: false, defaultValue: "held", submitOnChange: true
                        input "trig_held_nums", sENUM, title: inTS1("button numbers?", sCOMMAND), options: 1..8, required: true,  multiple: true, submitOnChange: true
                        if(settings.trig_held_nums) {
                            triggerVariableDesc("held", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("doubleTapped")) {
                section (sTS("Button Double Tap Events"), hideable: true) {
                    input "trig_doubleTapped", "capability.doubleTapableButton", title: inTS1("Double Tap Buttons", "button"), required: true, multiple: true, submitOnChange: true
                    if (settings.trig_doubleTapped) {
                        settingUpdate("trig_doubleTapped_cmd", "doubleTapped", sENUM)
                        //input "trig_doubleTapped_cmd", sENUM, title: inTS1("Double Tapped changes", sCOMMAND), options: ["doubleTapped"], required: true, multiple: false, defaultValue: "doubleTapped", submitOnChange: true
                        input "trig_doubleTapped_nums", sENUM, title: inTS1("button numbers?", sCOMMAND), options: 1..8, required: true, multiple: true, submitOnChange: true
                        if(settings.trig_doubleTapped_nums) {
                            triggerVariableDesc("doubleTapped", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("temperature")) {
                trigNumValSect("temperature", "temperatureMeasurement", "Temperature Sensor", "Temperature Sensors", "Temperature", "temperature", trigItemCnt++)
            }

            if (valTrigEvt("humidity")) {
                trigNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Sensors", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity", trigItemCnt++)
            }

            if (valTrigEvt("water")) {
                trigNonNumSect("water", "waterSensor", "Water Sensors", "Water/Moisture Sensors", ["wet", "dry", sANY], "changes to", ["wet", "dry"], "water", trigItemCnt++)
            }

            if (valTrigEvt("power")) {
                trigNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power", trigItemCnt++)
            }

            if (valTrigEvt("carbon")) {
                section (sTS("Carbon Monoxide Events"), hideable: true) {
                    input "trig_carbonMonoxide", "capability.carbonMonoxideDetector", title: inTS1("Carbon Monoxide Sensors", "co"), required: !(settings.trig_smoke), multiple: true, submitOnChange: true
                    if (settings.trig_carbonMonoxide) {
                        input "trig_carbonMonoxide_cmd", sENUM, title: inTS1("changes to?", sCOMMAND), options: ["detected", "clear", sANY], required: true, submitOnChange: true
                        if(settings.trig_carbonMonoxide_cmd) {
                            if (settings.trig_carbonMonoxide?.size() > 1 && settings.trig_carbonMonoxide_cmd != sANY) {
                                input "trig_carbonMonoxide_all", sBOOL, title: inTS1("Require ALL Smoke Detectors to be (${settings.trig_carbonMonoxide_cmd})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                            }
                            triggerVariableDesc("carbonMonoxide", false, trigItemCnt++)
                        }
                    }
                }
            }

            if (valTrigEvt("smoke")) {
                section (sTS("Smoke Events"), hideable: true) {
                    input "trig_smoke", "capability.smokeDetector", title: inTS1("Smoke Detectors", "smoke"), required: !(settings.trig_carbonMonoxide), multiple: true, submitOnChange: true
                    if (settings.trig_smoke) {
                        input "trig_smoke_cmd", sENUM, title: inTS1("changes to?", sCOMMAND), options: ["detected", "clear", sANY], required: true, submitOnChange: true
                        if(settings.trig_smoke_cmd) {
                            if (settings.trig_smoke?.size() > 1 && settings.trig_smoke_cmd != sANY) {
                                input "trig_smoke_all", sBOOL, title: inTS1("Require ALL Smoke Detectors to be (${settings.trig_smoke_cmd})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
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
                trigNonNumSect("shade", "windowShade", "Window Shades", "Window Shades", lOPNCLS+["opening", "closing", sANY], "changes to", lOPNCLS, "shade", trigItemCnt++)
            }

            if (valTrigEvt("valve")) {
                trigNonNumSect("valve", "valve", "Valves", "Valves", lOPNCLS+[sANY], "changes to", lOPNCLS, "valve", trigItemCnt++)
            }

            if (valTrigEvt("coolingSetpoint")) {
                trigNumValSect("coolingSetpoint", "thermostat", "Thermostat Cooling Setpoint Events", "Thermostat (Cooling Setpoint)", "Setpoint temp", "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Cooling Setpoint Events"), hideable: true) {
                    input "trig_coolingSetpoint", "capability.thermostat", title: inTS1("Thermostat (Cooling Setpoint)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_coolingSetpoint) {
                        input "trig_coolingSetpoint_cmd", sENUM, title: spanWrapImg("Setpoint temp is...", sNULL, "small", sCOMMAND, true), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
                        if (settings.trig_coolingSetpoint_cmd) {
                            if (settings.trig_coolingSetpoint_cmd in [sBETWEEN, sBELOW]) {
                                input "trig_coolingSetpoint_low", sNUMBER, title: spanWrapImg("${trig_coolingSetpoint_cmd == sBETWEEN ? "Between a Low" : "a"} Setpoint temp of...", sNULL, "small", "low", true), required: true, submitOnChange: true
                                if(settings.trig_coolingSetpoint_low) done=true
                            }
                            if (settings.trig_coolingSetpoint_cmd in [sBETWEEN, sABOVE]) {
                                input "trig_coolingSetpoint_high", sNUMBER, title: spanWrapImg("${trig_coolingSetpoint_cmd == sBETWEEN ? "and a high" : "a"} Setpoint temp of...", sNULL, "small", "high", true), required: true, submitOnChange: true
                                if(settings.trig_coolingSetpoint_high) done=true
                            }
                            if (settings.trig_coolingSetpoint_cmd == sEQUALS) {
                                input "trig_coolingSetpoint_equal", sNUMBER, title: spanWrapImg("a Setpoint temp of...", sNULL, "small", "equal", true), required: true, submitOnChange: true
                                if(settings.trig_coolingSetpoint_equal) done=true
                            }
                            if(done) {
                                input "trig_coolingSetpoint_once", sBOOL, title: spanWrapImg("Only alert once a day?\n(per type: thermostat)", sNULL, "small", "question", true), required: false, defaultValue: false, submitOnChange: true
                                input "trig_coolingSetpoint_wait", sNUMBER, title: spanWrapImg("Wait between each report (in seconds)\n(Optional)", sNULL, "small", "delay_time", true), required: false, defaultValue: null, submitOnChange: true
                                triggerVariableDesc("coolingSetpoint", false, trigItemCnt++)
                            }
                        }
                    }
                } */
            }

            if (valTrigEvt("heatingSetpoint")) {
                trigNumValSect("heatingSetpoint", "thermostat", "Thermostat Heating Setpoint Events", "Thermostat (HeatingSetpoint)", "Setpoint temp", "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Heating Setpoint Events"), hideable: true) {
                    input "trig_heatingSetpoint", "capability.thermostat", title: inTS1("Thermostat (Heating Setpoint)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_heatingSetpoint) {
                        input "trig_heatingSetpoint_cmd", sENUM, title: inTS1("Setpoint temp is...", sCOMMAND), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
                        if (settings.trig_heatingSetpoint_cmd) {
                            if (settings.trig_heatingSetpoint_cmd in [sBETWEEN, sBELOW]) {
                                input "trig_heatingSetpoint_low", sNUMBER, title: inTS1("${trig_heatingSetpoint_cmd == sBETWEEN ? "Between a Low" : "a"} Setpoint temp of...", "low"), required: true, submitOnChange: true
                                if(settings.trig_heatingSetpoint_low) done=true
                            }
                            if (settings.trig_heatingSetpoint_cmd in [sBETWEEN, sABOVE]) {
                                input "trig_heatingSetpoint_high", sNUMBER, title: inTS1("${trig_heatingSetpoint_cmd == sBETWEEN ? "and a high" : "a"} Setpoint temp of...", "high"), required: true, submitOnChange: true
                                if(settings.trig_heatingSetpoint_high) done=true
                            }
                            if (settings.trig_heatingSetpoint_cmd == sEQUALS) {
                                input "trig_heatingSetpoint_equal", sNUMBER, title: inTS1("a Setpoint temp of...", "equal"), required: true, submitOnChange: true
                                if(settings.trig_heatingSetpoint_equal) done=true
                            }
                            if(done) {
                                input "trig_heatingSetpoint_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: thermostat)", "question"), required: false, defaultValue: false, submitOnChange: true
                                input "trig_heatingSetpoint_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                                triggerVariableDesc("heatingSetpoint", false, trigItemCnt++)
                            }
                        }
                    }
                } */
            }

            if (valTrigEvt("thermostatTemperature")) {
                trigNumValSect("thermostatTemperature", "thermostat", "Thermostat Ambient Temperature Events", "Thermostat (Ambient Temperature)", "Ambient Temp", "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Ambient Temp Events"), hideable: true) {
                    input "trig_thermostatTemperature", "capability.thermostat", title: inTS1("Thermostat (Ambient Temperature)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_thermostatTemperature) {
                        input "trig_thermostatTemperature_cmd", sENUM, title: inTS1("Ambient Temp is...", sCOMMAND), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
                        if (settings.trig_thermostatTemperature_cmd) {
                            if (settings.trig_thermostatTemperature_cmd in [sBETWEEN, sBELOW]) {
                                input "trig_thermostatTemperature_low", sNUMBER, title: inTS1("a ${trig_thermostatTemperature_cmd == sBETWEEN ? "Low " : sBLANK}Ambient Temp of...", "low"), required: true, submitOnChange: true
                                if(settings.trig_thermostatTemperature_low) done=true
                            }
                            if (settings.trig_thermostatTemperature_cmd in [sBETWEEN, sABOVE]) {
                                input "trig_thermostatTemperature_high", sNUMBER, title: inTS1("${trig_thermostatTemperature_cmd == sBETWEEN ? "and a high " : "a "}Ambient Temp of...", "high"), required: true, submitOnChange: true
                                if(settings.trig_thermostatTemperature_high) done=true
                            }
                            if (settings.trig_thermostatTemperature_cmd == sEQUALS) {
                                input "trig_thermostatTemperature_equal", sNUMBER, title: inTS1("a Ambient Temp of...", "equal"), required: true, submitOnChange: true
                                if(settings.trig_thermostatTemperature_equal) done=true
                            }
                            if(done) {
                                input "trig_thermostatTemperature_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: thermostat)", "question"), required: false, defaultValue: false, submitOnChange: true
                                input "trig_thermostatTemperature_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                                triggerVariableDesc("thermostatTemperature", false, trigItemCnt++)
                            }
                        }
                    }
                } */
            }

            if (valTrigEvt("thermostatOperatingState")) {
                trigNonNumSect("thermostatOperatingState", "thermostat", "Thermostat Operating State Events", "Thermostat (Operating State)", getThermOperStOpts()+[sANY], "changes to", getThermOperStOpts(), "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Operating State Events"), hideable: true) {
                    input "trig_thermostatOperatingState", "capability.thermostat", title: inTS1("Thermostat (Operating State)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_thermostatOperatingState) {
                        input "trig_thermostatOperatingState_cmd", sENUM, title: inTS1("Operating State changes to?", sCOMMAND), options: getThermOperStOpts()+[sANY], required: true, submitOnChange: true
                        if(settings.trig_thermostatOperatingState_cmd) done=true
                        if(done) {
                            input "trig_thermostatOperatingState_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: thermostat)", "question"), required: false, defaultValue: false, submitOnChange: true
                            input "trig_thermostatOperatingState_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                            triggerVariableDesc("thermostatOperatingState", false, trigItemCnt++)
                        }
                    }
                }*/
            }

            if (valTrigEvt("thermostatMode")) {
                trigNonNumSect("thermostatMode", "thermostat", "Thermostat Mode Events", "Thermostat (Mode)", getThermModeOpts()+[sANY], "changes to", getThermModeOpts(), "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Mode Events"), hideable: true) {
                    input "trig_thermostatMode", "capability.thermostat", title: inTS1("Thermostat (Mode)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_thermostatMode) {
                        input "trig_thermostatMode_cmd", sENUM, title: inTS1("HVAC Mode changes to?", sCOMMAND), options: getThermModeOpts()+[sANY], required: true, submitOnChange: true
                        if(settings.trig_thermostatMode_cmd) done=true
                        if(done) {
                            input "trig_thermostatMode_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: thermostat)", "question"), required: false, defaultValue: false, submitOnChange: true
                            input "trig_thermostatMode_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                            triggerVariableDesc("thermostatMode", false, trigItemCnt++)
                        }
                    }
                } */
            }

            if (valTrigEvt("thermostatFanMode")) {
                trigNonNumSect("thermostatFanMode", "thermostat", "Thermostat Fan Mode Events", "Thermostat (Fan Mode)", ["on", "circulate", "auto"]+[sANY], "changes to", ["on", "circulate", "auto"], "thermostat", trigItemCnt++)
/*
                Boolean done = false
                section (sTS("Thermostat Fan Mode Events"), hideable: true) {
                    input "trig_thermostatFanMode", "capability.thermostat", title: inTS1("Thermostat(Fan Mode)", "thermostat"), multiple: true, required: true, submitOnChange: true
                    if (settings.trig_thermostatFanMode) {
                        input "trig_thermostatFanMode_cmd", sENUM, title: inTS1("Fan Mode changes to?", sCOMMAND), options: ["on", "circulate", "auto", sANY], required: true, submitOnChange: true
                        if(settings.trig_thermostatFanMode_cmd) done=true
                        if(done) {
                            input "trig_thermostatFanMode_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: thermostat)", "question"), required: false, defaultValue: false, submitOnChange: true
                            input "trig_thermostatFanMode_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                            triggerVariableDesc("thermostatFanMode", false, trigItemCnt++)
                        }
                    }
                } */
            }

            if(triggersConfigured()) {
                section(sBLANK) {
                    paragraph pTS(spanWrap("Step Complete", sNULL, "medium", true), getAppImg("done"))
                    paragraph spanWrap("Press <b>Next</b> to Return to Main Page", sNULL, "small")
                }
            }
        }
        state.showSpeakEvtVars = showSpeakEvtVars
    }
}

def handleCodeSect(String typ, String lbl) {
    Map<String, Map> lockCodes = getCodes((List)settings."trig_${typ}")
    log.debug "lockCodes: ${lockCodes}"
    if(lockCodes) {
//        section (sTS("Filter ${lbl} Code Events"), hideable: true) {
            Map codeOpts = lockCodes.collectEntries { [((String)it.key): it.value?.name ? "Name: "+(String)it.value.name : "Code Number ${(String)it.key}: (${(String)it.value?.code})"] }
            input "trig_${typ}_Codes", sENUM, title: inTS1("Filter ${lbl} codes...", sCOMMAND), options: codeOpts, multiple: true, required: false, submitOnChange: true
//        }
    }
}

Map<String,Map> getCodes(List devs, String code=sNULL) {
    // lockCodes are:
    // ["<codeNumber>":["code":"<pinCode>", "name":"<display name for code>"],"<codeNumber>":["code":"<pinCode>", "name":"<display name for code>"]]
    Map<String,Map> result = [:]
    try {
        String lockCodes = devs && devs.size() == 1 ? devs[0]?.currentValue("lockCodes") : (code ?: sNULL)
        if (lockCodes) {
            //decrypt codes if they're encrypted
            if (lockCodes[0] == "{") result = parseJson(lockCodes)
            else result = parseJson(decrypt(lockCodes))
            log.debug "lockCodes: ${result}"
        }
    } catch(ex) { logError("getCodes error", true, ex) }
    return result
}

def dummy(a,b) {}

def trigNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, cmdAfterOpts, String image, Integer trigItemCnt, Boolean devReq=true, Closure extraMeth=this.&dummy, String extraStr=sNULL) {
    Boolean done = false
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, required: devReq, submitOnChange: true
        if (settings."trig_${inType}") {
            input "trig_${inType}_cmd", sENUM, title: inTS1("${cmdTitle}...", sCOMMAND), options: cmdOpts, multiple: false, required: true, submitOnChange: true
            if(settings."trig_${inType}_cmd") {
                done=true
                if (settings."trig_${inType}"?.size() > 1 && settings."trig_${inType}_cmd" != sANY) {
                    input "trig_${inType}_all", sBOOL, title: inTS1("Require ALL ${devTitle} to be (${settings."trig_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                }
                extraMeth(inType, extraStr)

                if(!isTierAction() && settings."trig_${inType}_cmd" in cmdAfterOpts) {
                    input "trig_${inType}_after", sNUMBER, title: inTS1("Only after (${settings."trig_${inType}_cmd"}) for (xx) seconds?", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                    if(settings."trig_${inType}_after") {
                        input "trig_${inType}_after_repeat", sNUMBER, title: inTS1("Repeat every (xx) seconds until it's not ${settings."trig_${inType}_cmd"}?", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                        if(settings."trig_${inType}_after_repeat") {
                            input "trig_${inType}_after_repeat_cnt", sNUMBER, title: inTS1("Only repeat this many times? (Optional)", "question"), required: false, defaultValue: null, submitOnChange: true
                        }
                    }
                }
                if(!settings."trig_${inType}_after") {
                    input "trig_${inType}_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: ${inType})", "question"), required: false, defaultValue: false, submitOnChange: true
                    input "trig_${inType}_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", "delay_time"), required: false, defaultValue: null, submitOnChange: true
                }
                triggerVariableDesc(inType, true, trigItemCnt)
            }
        }
    }
}

def trigNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, Integer trigItemCnt, Boolean devReq=true) {
    Boolean done = false
    section (sTS(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", tite: inTS1(devTitle, image), multiple: true, submitOnChange: true, required: devReq
        if(settings."trig_${inType}") {
            input "trig_${inType}_cmd", sENUM, title: inTS1("${cmdTitle} is...", sCOMMAND), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
            if (settings."trig_${inType}_cmd") {
                if (settings."trig_${inType}_cmd" in [sBETWEEN, sBELOW]) {
                    input "trig_${inType}_low", sNUMBER, title: inTS1("a ${settings."trig_${inType}_cmd" == sBETWEEN ? "Low " : sBLANK}${cmdTitle} of...", "low"), required: true, submitOnChange: true
                }
                if(settings."trig_${inType}_low" && (settings."trig_${inType}_cmd" in [sBELOW]) ) done=true
                if (settings."trig_${inType}_cmd" in [sBETWEEN, sABOVE]) {
                    input "trig_${inType}_high", sNUMBER, title: inTS1("${settings."trig_${inType}_cmd" == sBETWEEN ? "and a high " : "a "}${cmdTitle} of...", "high"), required: true, submitOnChange: true
                }
                if(settings."trig_${inType}_high" && (settings."trig_${inType}_cmd" in [sABOVE])) done=true
                if(settings."trig_${inType}_low" && settings."trig_${inType}_high" && (settings."trig_${inType}_cmd" in [sBETWEEN])) done=true

                if (settings."trig_${inType}_cmd" == sEQUALS) {
                    input "trig_${inType}_equal", sNUMBER, title: inTS1("a ${cmdTitle} of...", "equal"), required: true, submitOnChange: true
                }
                if(settings."trig_${inType}_equal") done=true
                if(done) {
                    if (settings."trig_${inType}"?.size() > 1) {
                        input "trig_${inType}_all", sBOOL, title: inTS1("Require ALL devices to be (${settings."trig_${inType}_cmd"}) values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                        if(!settings."trig_${inType}_all") {
                            input "trig_${inType}_avg", sBOOL, title: inTS1("Use the average of all selected device values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                        }
                    }
                    input "trig_${inType}_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: ${inType})", "question"), required: false, defaultValue: false, submitOnChange: true
                    input "trig_${inType}_wait", sNUMBER, title: inTS1("Wait between each report", "delay_time"), required: false, defaultValue: 120, submitOnChange: true
                    triggerVariableDesc(inType, false, trigItemCnt)
                }
            }
        }
    }
}

Boolean locationTriggers() {
    return (
        (valTrigEvt(sMODE) && settings.trig_mode) || (valTrigEvt("alarm") && settings.trig_alarm) ||
        (valTrigEvt("pistonExecuted") && settings.trig_pistonExecuted) ||
        (valTrigEvt("guard") && settings.trig_guard)
    )
}

Boolean deviceTriggers() {
//ERS
    return (
        (settings.trig_shade && settings.trig_shade_cmd) || (settings.trig_door && settings.trig_door_cmd) || (settings.trig_valve && settings.trig_valve_cmd) ||
        (settings.trig_switch && settings.trig_switch_cmd) || (settings.trig_level && settings.trig_level_cmd) || (settings.trig_lock && settings.trig_lock_cmd) ||
        (settings.trig_securityKeypad && settings.trig_securityKeypad_cmd) ||
        (settings.trig_battery && settings.trig_battery_cmd) ||
        (settings.trig_button && settings.trig_button_cmd) ||
        (settings.trig_pushed && settings.trig_pushed_cmd && settings.trig_pushed_nums) ||
        (settings.trig_released && settings.trig_released_cmd && settings.trig_released_nums) ||
        (settings.trig_held && settings.trig_held_cmd && settings.trig_held_nums) ||
        (settings.trig_doubleTapped && settings.trig_doubleTapped_cmd && settings.trig_doubleTapped_nums) ||
        (thermostatTriggers())
    )
}

Boolean thermostatTriggers() {
    List okList = []
    ["coolingSetpoint", "heatingSetpoint", "thermostatTemperature"].each { String att-> // Thermostat number value validation
        if(valTrigEvt(att)) { okList.push((Boolean)(settings."trig_${att}" && settings."trig_${att}_cmd" && (settings."trig_${att}_low" || settings."trig_${att}_high" || settings."trig_${att}_equal"))) }
    }
    ["thermostatMode", "thermostatOperatingState", "thermostatFanMode"].each { String att-> // Thermostat non number validation
        if(valTrigEvt(att)) { okList.push((Boolean)(settings."trig_${att}" && settings."trig_${att}_cmd")) }
    }
    // log.debug "thermostatTriggers | okList: ${okList} | Every: ${(okList.every { it == true })}"
    return (okList.size()) ? (okList.every { it == true }) : false
}

Boolean sensorTriggers() {
    return (
        (settings.trig_temperature && settings.trig_temperature_cmd) || (settings.trig_carbonMonoxide && settings.trig_carbonMonoxide_cmd) || (settings.trig_humidity && settings.trig_humidity_cmd) ||
        (settings.trig_water && settings.trig_water_cmd) || (settings.trig_smoke && settings.trig_smoke_cmd) || (settings.trig_presence && settings.trig_presence_cmd) || (settings.trig_motion && settings.trig_motion_cmd) ||
        (settings.trig_contact && settings.trig_contact_cmd) || (settings.trig_power && settings.trig_power_cmd) || (settings.trig_illuminance && settings.trig_illuminance_low && settings.trig_illuminance_high) ||
        (settings.trig_acceleration && settings.trig_acceleration_cmd)
    )
}

Boolean triggersConfigured() {
    Boolean sched = schedulesConfigured()
    Boolean loc = locationTriggers()
    Boolean dev = deviceTriggers()
    Boolean sen = sensorTriggers()
    // log.debug "sched: $sched | loc: $loc | dev: $dev | sen: $sen"
    return (sched || loc || dev || sen)
}

/******************************************************************************
    CONDITIONS SELECTION PAGE
******************************************************************************/

def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: sBLANK, nextPage: "mainPage", install: false, uninstall: false) {
        String a = getConditionsDesc(false)
        if(a) {
            section() {
                paragraph pTS(a, sNULL, false, sCLR4D9), state: sCOMPLT
            }
        }
        Boolean multiConds = multipleConditions()
        section() {
            if(multiConds) {
                input "cond_require_all", sBOOL, title: inTS1("Require All Selected Conditions to Pass Before Activating Zone?", sCHKBOX), required: false, defaultValue: true, submitOnChange: true
            }
            paragraph pTS("Notice:\n${reqAllCond() ? "All selected conditions must pass before for this action to operate." : "Any condition will allow this action to operate."}", sNULL, false, sCLR4D9), state: sCOMPLT
        }
        section(sTS("Time/Date")) {
            // input "test_time", sTIME, title: "Trigger Time?", required: false, submitOnChange: true
            href "condTimePage", title: inTS1("Time Schedule", "clock"), description: getTimeCondDesc(false), state: (timeCondConfigured() ? sCOMPLT : null)
            input "cond_days", sENUM, title: inTS1("Days of the week", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: weekDaysEnum()
            input "cond_months", sENUM, title: inTS1("Months of the year", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: monthEnum()
        }
        section (sTS("Mode Conditions")) {
            input "cond_mode", sMODE, title: inTS1("Location Modes...", sMODE), multiple: true, required: false, submitOnChange: true
            if(settings.cond_mode) {
                input "cond_mode_cmd", sENUM, title: inTS1("are...", sCOMMAND), options: ["not":"Not in these modes", "are":"In these Modes"], required: true, multiple: false, submitOnChange: true
            }
        }
        section (sTS("Alarm Conditions")) {
            input "cond_alarm", sENUM, title: inTS1("${getAlarmSystemName()} is...", "alarm_home"), options: getAlarmTrigOpts(), multiple: true, required: false, submitOnChange: true
        }

        condNonNumSect(sSWITCH, sSWITCH, "Switches/Outlets Conditions", "Switches/Outlets", lONOFF, "are", sSWITCH)

        condNonNumSect("motion", "motionSensor", "Motion Conditions", "Motion Sensors", lACTINACT, "are", "motion")

        condNonNumSect("presence", "presenceSensor", "Presence Conditions", "Presence Sensors", ["present", "not present"], "are", "presence")

        condNonNumSect("contact", "contactSensor", "Door, Window, Contact Sensors Conditions", "Contact Sensors", lOPNCLS, "are", "contact")

        condNonNumSect("acceleration", "accelerationSensor", "Accelorometer Conditions", "Accelorometer Sensors", lACTINACT, "are", "acceleration")

        condNonNumSect("lock", "lock", "Lock Conditions", "Smart Locks", ["locked", "unlocked"], "are", "lock")

        condNonNumSect("securityKeypad", "securityKeypad", "Security Keypad Conditions", "Security Kepads", ["disarmed", "armed home", "armed away"], "are", "lock")

        condNonNumSect("door", "garageDoorControl", "Garage Door Conditions", "Garage Doors", lOPNCLS, "are", "garage_door")

        condNumValSect("temperature", "temperatureMeasurement", "Temperature Conditions", "Temperature Sensors", "Temperature", "temperature")

        condNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Conditions", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity")

        condNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Conditions", "Illuminance Sensors", "Lux Level (%)", "illuminance")

        condNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level (%)", "speed_knob")

        condNonNumSect("water", "waterSensor", "Water Sensors", "Water Sensors", ["wet", "dry"], "are", "water")

        condNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power")

        condNonNumSect("shade", "windowShade", "Window Shades", "Window Shades", lOPNCLS, "are", "shade")

        condNonNumSect("valve", "valve", "Valves", "Valves", lOPNCLS, "are", "valve")

        condNumValSect("battery", "battery", "Battery Level Conditions", "Batteries", "Level (%)", "battery")

        section (sTS("Thermostat Modes")) { // these allow multiple: true for _cmd
            String inType = "thermostatMode"
            String devTitle = "Thermostat Mode"
            List cmdOpts = getThermModeOpts()
            input "cond_${inType}", "capability.thermostat", title: inTS1("Thermostats", "thermostat"), multiple: true, submitOnChange: true, required:false, hideWhenEmpty: true
            if (settings."cond_${inType}") {
                input "cond_${inType}_cmd", sENUM, title: inTS1("${devTitle} is...", sCOMMAND), options: cmdOpts, multiple: true, required: true, submitOnChange: true
                if (settings."cond_${inType}_cmd".size() == 1 && settings."cond_${inType}"?.size() > 1) {
                    input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                } else settingUpdate("cond_${inType}_all", sFALSE, sBOOL)
            }
        }

        section (sTS("Thermostat Operating States")) {
            String inType = "thermostatOperatingState"
            String devTitle = "Thermostat Operating State"
            List cmdOpts = getThermOperStOpts()
            input "cond_${inType}", "capability.thermostat", title: inTS1("Thermostats", "thermostat"), multiple: true, submitOnChange: true, required:false, hideWhenEmpty: true
            if (settings."cond_${inType}") {
                input "cond_${inType}_cmd", sENUM, title: inTS1("${devTitle} is...", sCOMMAND), options: cmdOpts, multiple: true, required: true, submitOnChange: true
                if (settings."cond_${inType}_cmd".size() == 1 && settings."cond_${inType}"?.size() > 1) {
                    input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                } else settingUpdate("cond_${inType}_all", sFALSE, sBOOL)
            }
        }

        condNumValSect("coolingSetpoint", "thermostat", "Cooling Setpoint", "Thermostats", "Temperature", "temperature")
        condNumValSect("heatingSetpoint", "thermostat", "Heating Setpoint", "Thermostats", "Temperature", "temperature")
    }
}

static List getThermModeOpts() {
    return ["auto", "cool", " heat", "emergency heat", "off"]
}

static List getThermOperStOpts() {
    return ["cooling", "heating", "idle"]
}

def condNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, String image) {
    section (sTS(sectStr), hideWhenEmpty: true) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required:false, hideWhenEmpty: true
        if (settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle}...", sCOMMAND), options: cmdOpts, multiple: false, required: true, submitOnChange: true
            if (settings."cond_${inType}_cmd" && settings."cond_${inType}"?.size() > 1) {
                input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
        }
    }
}

def condNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, Boolean hideable= false) {
    section (sTS(sectStr), hideWhenEmpty: true) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required: false, hideWhenEmpty: true
        if(settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle} is...", sCOMMAND), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
            if (settings."cond_${inType}_cmd") {
                if (settings."cond_${inType}_cmd" in [sBETWEEN, sBELOW]) {
                    input "cond_${inType}_low", sNUMBER, title: inTS1("a ${settings."cond_${inType}_cmd" == sBETWEEN ? "Low " : sBLANK}${cmdTitle} of...", "low"), required: true, submitOnChange: true
                }
                if (settings."cond_${inType}_cmd" in [sBETWEEN, sABOVE]) {
                    input "cond_${inType}_high", sNUMBER, title: inTS1("${settings."cond_${inType}_cmd" == sBETWEEN ? "and a high " : "a "}${cmdTitle} of...", "high"), required: true, submitOnChange: true
                }
                if (settings."cond_${inType}_cmd" == sEQUALS) {
                    input "cond_${inType}_equal", sNUMBER, title: inTS1("a ${cmdTitle} of...", "equal"), required: true, submitOnChange: true
                }
                if (settings."cond_${inType}"?.size() > 1) {
                    input "cond_${inType}_all", sBOOL, title: inTS1("Require ALL devices to be (${settings."cond_${inType}_cmd"}) values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                    if(!settings."cond_${inType}_all") {
                        input "cond_${inType}_avg", sBOOL, title: inTS1("Use the average of all selected device values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
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
        devs.each { dev->
            if(dev?.hasAttribute("permissions") && dev?.currentPermissions?.toString()?.contains("volumeControl")) {
                supported.push(dev.label)
            } else { noSupport.push(dev.label) }
        }
    }
    return [s:supported, n:noSupport]
}

def actVariableDesc(String actType, Boolean hideUserTxt=false) {
    Map txtItems = customTxtItems()
    if(!isTierAction()) {
        if(!txtItems?.size() && state.showSpeakEvtVars && !settings."act_${actType}_txt") {
            String str = "NOTICE:\nYou can choose to leave the response field empty and generic text will be generated for each event type, or return to Step 2 and define responses for each trigger."
            paragraph pTS(str, getAppImg("info"), false, sCLR4D9), required: true, state: sCOMPLT
        }
        if(!hideUserTxt) {
            if(txtItems?.size()) {
                String str = "NOTICE: (Custom Text Defined)"
                txtItems?.each { i->
                    i?.value?.each { i2-> str += "\n${sBULLET} ${i?.key?.toString()?.capitalize()} ${i2?.key?.toString()?.capitalize()}: (${i2?.value?.size()} Responses)" }
                }
                paragraph pTS(str, sNULL, true, sCLR4D9), state: sCOMPLT
                paragraph pTS("WARNING:\nEntering text below will override the text you defined for the trigger types under Step 2.", sNULL, true, sCLRRED), required: true, state: null
            }
        }
    }
}

def triggerVariableDesc(String inType, Boolean showRepInputs=false, Integer itemCnt=0) {
    if((String)settings.actionType in ["speak", "announcement"]) {
        String str = spanWrapSm("Description:", sNULL, true, true)
        str += spanWrapSm("You have 3 response options:",  sNULL, false, true)
        str += spanWrapSm(" ${sBULLET} Option 1. Leave the text empty below and text will be generated for each ${inType} trigger event.", sNULL, false, true)
        str += spanWrapSm(" ${sBULLET} Option 2. Wait till Step 4 and define a single global response for all triggers selected here.", sNULL, false, true)
        str += spanWrapSm(" ${sBULLET} Option 3. Use the response builder below and create custom responses for each individual trigger type. (Supports randomization when multiple responses are configured)")
        // str += "Custom Text is only used when Speech or Announcement action type is selected in Step 4."
        paragraph spanWrapImg(str, sCLR4D9, "small", "info")
        //Custom Text Options
        href url: parent?.getTextEditorPath(app?.id as String, "trig_${inType}_txt"), style: sEXTNRL, required: false, title: spanWrapSm("Custom ${inType?.capitalize()} Responses<br>(Optional)"), 
                    description: settings."trig_${inType}_txt" ? spanWrapSm(settings."trig_${inType}_txt", sCLR4D9) : spanWrapSm("Open Response Designer...", sCLRGRY)
        if(showRepInputs) {
            if(settings."trig_${inType}_after_repeat") {
                //Custom Repeat Text Options
                paragraph pTS("Description:\nAdd custom responses for the ${inType} events that are repeated.", getAppImg("info"), false, sCLR4D9), state: sCOMPLT
                href url: parent?.getTextEditorPath(app?.id as String, "trig_${inType}_after_repeat_txt"), style: sEXTNRL, title: inTS1("Custom ${inType?.capitalize()} Repeat Responses\n(Optional)", sTEXT),
                        description: settings."trig_${inType}_after_repeat_txt" ?: "Open Response Designer...", state: (settings."trig_${inType}_after_repeat_txt" ? sCOMPLT : '') , submitOnChange: true, required: false
            }
        }
    }
}

@Field static final Map<String, String> descsFLD = [
    speak: "Speak any message you choose on you're Echo Devices.",
    announcement: "Plays a brief tone and speaks the message you define. If you select multiple devices it will be a synchronized broadcast.",
    speak_tiered: "Allows you to create tiered responses.  Each tier can have a different delay before the next message is spoken/announced.",
    announcement_tiered: "Allows you to create tiered responses.  Each tier can have a different delay before the next message is spoken/announced. Plays a brief tone and speaks the message you define. If you select multiple devices it will be a synchronized broadcast.",
    sequence: "Sequences are a custom command where you can string different alexa actions which are sent to Amazon as a single command.  The command is then processed by amazon sequentially or in parallel.",
    weather: "Plays a very basic weather report.",
    playback: "Allows you to control the media playback state or volume level of your Echo devices.",
    builtin: "Builtin items are things like Sing a Song, Tell a Joke, Say Goodnight, etc.",
    sounds: "Plays a selected amazon sound item.",
    voicecmd: "Executes a text based command just as if it was spoken to the device",
    music: "Allows playback of various Songs/Radio using any connected music provider",
    calendar: "This will read out events in your calendar (Requires accounts to be configured in the alexa app. Must not have PIN.)",
    alarm: "This will allow you to create Alexa alarm clock notifications.",
    reminder: "This will allow you to create Alexa reminder notifications.",
    dnd: "This will allow you to enable/disable Do Not Disturb mode",
    alexaroutine: "This will allow you to run your configured Alexa Routines",
    wakeword: "This will allow you to change the Wake Word of your Echo devices.",
    bluetooth: "This will allow you to connect or disconnect bluetooth devices paired to your echo devices."
]

String actionTypeDesc() {
    String a = (String)settings.actionType
    return descsFLD.containsKey(a) ? (String)descsFLD[a] : "No Description Found..."
}

def actionTiersPage() {
    return dynamicPage(name: "actionTiersPage", title: sBLANK, install: false, uninstall: false) {
        section() {
            input "act_tier_cnt", sNUMBER, title: inTS1("Number of Tiers", "equal"), required: true, submitOnChange: true
        }
        Integer tierCnt = (Integer)settings.act_tier_cnt
        if(tierCnt) {
            (1..tierCnt)?.each { Integer ti->
                section(sTS("Tier Item (${ti}) Config:")) {
                    if(ti > 1) {
                        input "act_tier_item_${ti}_delay", sNUMBER, title: inTS1("Delay after Tier ${ti-1}\n(seconds)", "equal"), defaultValue: (ti == 1 ? 0 : null), required: true, submitOnChange: true
                    }
                    if(ti==1 || settings."act_tier_item_${ti}_delay") {
                        href url: parent?.getTextEditorPath(app?.id as String, "act_tier_item_${ti}_txt"), style: sEXTNRL, required: true, title: inTS1("Tier Item ${ti} Response", sTEXT), state: (settings."act_tier_item_${ti}_txt" ? sCOMPLT : sBLANK),
                                    description: settings."act_tier_item_${ti}_txt" ?: "Open Response Designer..."
                    }
                    input "act_tier_item_${ti}_volume_change", sNUMBER, title: inTS1("Tier Item Volume", "speed_knob"), defaultValue: null, required: false, submitOnChange: true
                    input "act_tier_item_${ti}_volume_restore", sNUMBER, title: inTS1("Tier Item Volume Restore", "speed_knob"), defaultValue: null, required: false, submitOnChange: true
                }
            }
            if(isTierActConfigured()) {
                section(sBLANK) {
                    paragraph pTS("You are all done configuring tier responses.\n\nPress Next/Done/Save to go back", getAppImg("done")), state: sCOMPLT
                }
            }
        }
    }
}

String getTierRespDesc() {
    Map tierMap = getTierMap() ?: [:]
    String str = sBLANK
    str += tierMap?.size() ? "Tiered Responses: (${tierMap?.size()})" : sBLANK
    tierMap?.each { k,v->
        if(settings."act_tier_item_${k}_txt") {
            str += (settings."act_tier_item_${k}_delay") ? "\n ${sBULLET} Tier ${k} delay: (${settings."act_tier_item_${k}_delay"} sec)" : sBLANK
            str += (settings."act_tier_item_${k}_volume_change") ? "\n ${sBULLET} Tier ${k} volume: (${settings."act_tier_item_${k}_volume_change"})" : sBLANK
            str += (settings."act_tier_item_${k}_volume_restore") ? "\n ${sBULLET} Tier ${k} restore: (${settings."act_tier_item_${k}_volume_restore"})" : sBLANK
        }
    }
    return str != sBLANK ? str : sNULL
}

Boolean isTierAction() {
    return ((String)settings.actionType in ["speak_tiered", "announcement_tiered"])
}

Boolean isTierActConfigured() {
    if(!isTierAction()) { return false }
    Integer cnt = (Integer)settings.act_tier_cnt
    List tierKeys = settings.findAll { it?.key?.startsWith("act_tier_item_") && it?.key?.endsWith("_txt") }?.collect { it?.key as String }
    return (tierKeys?.size() == cnt)
}

Map getTierMap() {
    Map exec = [:]
    Integer cnt = (Integer)settings.act_tier_cnt
    if(isTierActConfigured() && cnt) {
        List tiers = (1..cnt)
        tiers?.each { t->
            exec[t as Integer] = [
                message: settings["act_tier_item_${t}_txt"],
                delay: settings["act_tier_item_${t+1}_delay"],
                volume: [
                    change: settings["act_tier_item_${t}_volume_change"],
                    restore: settings["act_tier_item_${t}_volume_restore"]
                ]
            ]
        }
    }
    return (exec?.size()) ? exec : null
}

void tierItemCleanup() {
    List<String> rem = []
    Boolean isTierAct = isTierAction()
    Integer tierCnt = (Integer)settings.act_tier_cnt
    List tierKeys = settings.findAll { it?.key?.startsWith("act_tier_item_") }?.collect { it.key as String }
    List tierIds = isTierAct && tierCnt ? (1..tierCnt) : []
    // if(!isTierAct() || !tierCnt) { return }
    tierKeys?.each { String k->
        List id = k?.tokenize("_") ?: []
        if(!isTierAct || (id?.size() && id?.size() < 4) || !id[3]?.toString()?.isNumber() || !(id[3]?.toInteger() in tierIds)) { rem.push(k) }
    }
    if(rem.size()) { logDebug("tierItemCleanup | Removing: ${rem}"); rem.each { settingRemove(it) } }
}

def actTextOrTiersInput(String type) {
    if(isTierAction()) {
        String tDesc = getTierRespDesc()
        href "actionTiersPage", title: inTS1("Create Tiered Responses?", sTEXT, (tDesc ? sCLR4D9 : sCLRRED)), description: (tDesc ? "${tDesc}\n\n"+sTTM : sTTC), required: true, state: (tDesc ? sCOMPLT : null)
        input "act_tier_stop_on_clear", sBOOL, title: inTS1("Stop responses when trigger is cleared?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
    } else {
        String textUrl = parent?.getTextEditorPath(app?.id as String, type)
        href url: textUrl, style: sEXTNRL, required: false, title: inTS1("Default Action Response\n(Optional)", sTEXT), state: (settings."${type}" ? sCOMPLT : sBLANK),
                description: settings."${type}" ?: "Open Response Designer..."
    }
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", title: sBLANK, nextPage: "mainPage", install: false, uninstall: false) {
        String a = getActionDesc(false)
        if(a) {
            section() {
                paragraph pTS(a, sNULL, false, sCLR4D9), state: sCOMPLT
            }
        }
        Boolean done = false
        Map actionExecMap = [configured: false]
        String myactionType = (String)settings.actionType
        if(myactionType) {
            actionExecMap.actionType = myactionType
            actionExecMap.config = [:]
            List devices = parent?.getDevicesFromList(settings.act_EchoDevices)
            String actTypeDesc = "[${myactionType.tokenize("_")?.collect { it?.capitalize() }?.join(sSPACE)}]\n\n${actionTypeDesc()}"
            Boolean isTierAct = isTierAction()
            switch(myactionType) {
                case "speak":
                case "speak_tiered":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sTS("Action Type Config:"), hideable: true) {
                            actVariableDesc(myactionType)
                            actTextOrTiersInput("act_speak_txt")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config[myactionType] = [text: settings.act_speak_txt, evtText: ((state.showSpeakEvtVars && !settings.act_speak_txt) || hasUserDefinedTxt()), tiers: getTierMap()]
                        done = state.showSpeakEvtVars || settings.act_speak_txt || (isTierAct && isTierActConfigured())
                    } else { done = false }
                    break

                case "announcement":
                case "announcement_tiered":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("announce")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sTS("Action Type Config:")) {
                            actVariableDesc(myactionType)
                            actTextOrTiersInput("act_announcement_txt")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config[myactionType] = [text: settings.act_announcement_txt, evtText: ((state.showSpeakEvtVars && !settings.act_speak_txt) || hasUserDefinedTxt()), tiers: getTierMap()]
                        if(settings.act_EchoDevices?.size() > 1) {
                            List devObj = []
                            devices?.each { devObj?.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String]) }
                            // log.debug "devObj: $devObj"
                            actionExecMap.config[myactionType].deviceObjs = devObj
                        }
                        done = state.showSpeakEvtVars || settings.act_announcement_txt || (isTierAct && isTierActConfigured())
                    } else { done = false }
                    break

                case "voicecmd":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info", true), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices) {
                        section(sTS("Action Type Config:")) {
                            input "act_voicecmd_txt", sTEXT, title: inTS1("Enter voice command text", sTEXT), submitOnChange: true, required: false
                        }
                        actionExecMap.config.voicecmd = [text: settings.act_voicecmd_txt]
                        if(settings.act_voicecmd_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sequence":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map seqItemsAvail = parent?.seqItemsAvail()
                        section(sTS("Sequence Options Legend:"), hideable: true, hidden: false) {
                            String str1 = "Sequence Options:"
                            seqItemsAvail?.other?.sort()?.each { k, v->
                                str1 += "${bulletItem(str1, "${k}${v != null ? "::${v}" : sBLANK}")}"
                            }
                            String str4 = "DoNotDisturb Options:"
                            seqItemsAvail?.dnd?.sort()?.each { k, v->
                                str4 += "${bulletItem(str4, "${k}${v != null ? "::${v}" : sBLANK}")}"
                            }
                            String str2 = "Music Options:"
                            seqItemsAvail?.music?.sort()?.each { k, v->
                                str2 += "${bulletItem(str2, "${k}${v != null ? "::${v}" : sBLANK}")}"
                            }
                            String str3 = "Canned TTS Options:"
                            seqItemsAvail?.speech?.sort()?.each { k, v->
                                def newV = v
                                if(v instanceof List) { newV = sBLANK; v?.sort()?.each { newV += "     ${dashItem(newV, "${it}", true)}" } }
                                str3 += "${bulletItem(str3, "${k}${newV != null ? "::${newV}" : sBLANK}")}"
                            }
                            paragraph str1, state: sCOMPLT
                            // paragraph str4, state: sCOMPLT
                            paragraph str2, state: sCOMPLT
                            paragraph str3, state: sCOMPLT
                            paragraph pTS("Enter the command in a format exactly like this:\nvolume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30\n\nEach command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.", sNULL, false, "violet"), state: sCOMPLT
                        }
                        section(sTS("Action Type Config:")) {
                            input "act_sequence_txt", sTEXT, title: inTS1("Enter sequence text", sTEXT), submitOnChange: true, required: false
                        }
                        actionExecMap.config.sequence = [text: settings.act_sequence_txt]
                        if(settings.act_sequence_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "weather":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        actionVolumeInputs(devices)
                        done = true
                        actionExecMap.config.weather = [cmd: "playWeather"]
                    } else { done = false }
                    break

                case "playback":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map playbackOpts = [
                            "pause":"Pause", "stop":"Stop", "play":"Play", "nextTrack":"Next Track", "previousTrack":"Previous Track",
                            "mute":"Mute", "volume":"Volume"
                        ]
                        section(sTS("Playback Config:")) {
                            input "act_playback_cmd", sENUM, title: inTS1("Select Playback Action", sCOMMAND), description: sBLANK, options: playbackOpts, required: true, submitOnChange: true
                        }
                        if(settings.act_playback_cmd == "volume") { actionVolumeInputs(devices, true) }
                        actionExecMap.config?.playback = [cmd: settings.act_playback_cmd]
                        if(settings.act_playback_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sounds":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sTS("BuiltIn Sounds Config:")) {
                            input "act_sounds_cmd", sENUM, title: inTS1("Select Sound Type", sCOMMAND), description: sBLANK, options: parent?.getAvailableSounds()?.collect { it?.key as String }, required: true, submitOnChange: true
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config.sounds = [cmd: "playSoundByName", name: settings.act_sounds_cmd]
                        done = (settings.act_sounds_cmd != null)
                    } else { done = false }
                    break

                case "builtin":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map builtinOpts = [
                            "playSingASong": "Sing a Song", "playFlashBrief": "Flash Briefing (News)", "playGoodNews": "Good News Only", "playFunFact": "Fun Fact", "playTraffic": "Traffic", "playJoke": "Joke",
                            "playTellStory": "Tell Story", "sayGoodbye": "Say Goodbye", "sayGoodNight": "Say Goodnight", "sayBirthday": "Happy Birthday",
                            "sayCompliment": "Give Compliment", "sayGoodMorning": "Good Morning", "sayWelcomeHome": "Welcome Home"
                        ]
                        section(sTS("BuiltIn Speech Config:")) {
                            input "act_builtin_cmd", sENUM, title: inTS1("Select Builtin Speech Type", sCOMMAND), description: sBLANK, options: builtinOpts, required: true, submitOnChange: true
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config.builtin = [cmd: settings.act_builtin_cmd]
                        if(settings.act_builtin_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "music":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        List musicProvs = devices[0]?.hasAttribute("supportedMusic") ? devices[0]?.currentValue("supportedMusic")?.split(",")?.collect { "${it?.toString()?.trim()}"} : []
                        logDebug("Music Providers: ${musicProvs}")
                        if(musicProvs) {
                            section(sTS("Music Providers:")) {
                                input "act_music_provider", sENUM, title: inTS1("Select Music Provider", "music"), description: sBLANK, options: musicProvs, multiple: false, required: true, submitOnChange: true
                            }
                            if(settings.act_music_provider) {
                                if(settings.act_music_provider == "TuneIn") {
                                    section(sTS("TuneIn Search Results:")) {
                                        paragraph "Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", state: sCOMPLT
                                        input "tuneinSearchQuery", sTEXT, title: inTS1("Enter search phrase for TuneIn", "tunein"), defaultValue: null, required: false, submitOnChange: true
                                        if(settings.tuneinSearchQuery) {
                                            href "searchTuneInResultsPage", title: inTS1("View search results!", "search"), description: sTTP
                                        }
                                    }
                                }
                                section(sTS("Action Type Config:")) {
                                    input "act_music_txt", sTEXT, title: inTS1("Enter Music Search text", sTEXT), submitOnChange: true, required: false
                                }
                                actionVolumeInputs(devices)
                            }
                        }
                        actionExecMap.config.music = [cmd: "searchMusic", provider: settings.act_music_provider, search: settings.act_music_txt]
                        done = settings.act_music_provider && settings.act_music_txt
                    } else { done = false }
                    break

                case "calendar":
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("TTS")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sTS("Action Type Config:")) {
                            input "act_calendar_cmd", sENUM, title: inTS1("Select Calendar Action", sCOMMAND), description: sBLANK, options: ["playCalendarToday":"Today", "playCalendarTomorrow":"Tomorrow", "playCalendarNext":"Next Events"],
                                    required: true, submitOnChange: true
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config.calendar = [cmd: settings.act_calendar_cmd]
                        if(act_calendar_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alarm":
                    //TODO: Offer to remove alarm after event.
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("alarms")
                    if(settings.act_EchoDevices) {
//                        Map repeatOpts = ["everyday":"Everyday", "weekends":"Weekends", "weekdays":"Weekdays", "daysofweek":"Days of the Week", "everyxdays":"Every Nth Day"]
                        String rptType = null
                        def rptTypeOpts = null
                        section(sTS("Action Type Config:")) {
                            input "act_alarm_label", sTEXT, title: inTS1("Alarm Label", "name_tag"), submitOnChange: true, required: true
                            input "act_alarm_date", sTEXT, title: inTS1("Alarm Date\n(yyyy-mm-dd)", "day_calendar"), submitOnChange: true, required: true
                            input "act_alarm_time", sTIME, title: inTS1("Alarm Time", "clock"), submitOnChange: true, required: true
                            // if(act_alarm_label && act_alarm_date && act_alarm_time) {
                            //     input "act_alarm_rt", sENUM, title: inTS1("Repeat (Optional)", sCOMMAND), description: sBLANK, options: repeatOpts, required: true, submitOnChange: true
                            //     if(settings."act_alarm_rt") {
                            //         rptType = settings.act_alarm_rt
                            //         if(settings."act_alarm_rt" == "daysofweek") {
                            //             input "act_alarm_rt_wd", sENUM, title: inTS1("Weekday", sCHKBOX), description: sBLANK, options: weekDaysAbrvEnum(), multiple: true, required: true, submitOnChange: true
                            //             if(settings.act_alarm_rt_wd) rptTypeOpts = settings.act_alarm_rt_wd
                            //         }
                            //         if(settings."act_alarm_rt" == "everyxdays") {
                            //             input "act_alarm_rt_ed", sNUMBER, title: inTS1("Every X Days", sCHKBOX), description: sBLANK, range: "1..31", required: true, submitOnChange: true
                            //             if(settings.act_alarm_rt_ed) rptTypeOpts = settings.act_alarm_rt_ed
                            //         }
                            //     }
                            // }
                            // input "act_alarm_remove", sBOOL, title: "Remove Alarm when done", defaultValue: true, submitOnChange: true, required: false
                        }
                        actionVolumeInputs(devices, false, true)
                        def newTime = settings.act_alarm_time ? parseFmtDt("yyyy-MM-dd'T'HH:mm:ss.SSSZ", 'HH:mm', settings.act_alarm_time) : null
                        actionExecMap.config.alarm = [cmd: "createAlarm", label: settings.act_alarm_label, date: settings.act_alarm_date, time: newTime, recur: [type: rptType, opts: rptTypeOpts], remove: settings.act_alarm_remove]
                        done = act_alarm_label && act_alarm_date && act_alarm_time
                    } else { done = false }
                    break

                case "reminder":
                    //TODO: Offer to remove reminder after event.
                    section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                    echoDevicesInputByPerm("reminders")
                    if(settings.act_EchoDevices) {
//                        Map repeatOpts = ["everyday":"Everyday", "weekends":"Weekends", "weekdays":"Weekdays", "daysofweek":"Days of the Week", "everyxdays":"Every Nth Day"]
                        String rptType = null
                        def rptTypeOpts = null
                        section(sTS("Action Type Config:")) {
                            input "act_reminder_label", sTEXT, title: inTS1("Reminder Label", "name_tag"), submitOnChange: true, required: true
                            input "act_reminder_date", sTEXT, title: inTS1("Reminder Date\n(yyyy-mm-dd)", "day_calendar"), submitOnChange: true, required: true
                            input "act_reminder_time", sTIME, title: inTS1("Reminder Time", "clock"), submitOnChange: true, required: true
                            // if(act_reminder_label && act_reminder_date && act_reminder_time) {
                            //     input "act_reminder_rt", sENUM, title: inTS1("Repeat (Optional)", sCOMMAND), description: sBLANK, options: repeatOpts, required: true, submitOnChange: true
                            //     if(settings."act_reminder_rt") {
                            //         rptType = settings.act_reminder_rt
                            //         if(settings."act_reminder_rt" == "daysofweek") {
                            //             input "act_reminder_rt_wd", sENUM, title: inTS1("Weekday", sCHKBOX), description: sBLANK, options: weekDaysAbrvEnum(), multiple: true, required: true, submitOnChange: true
                            //             if(settings.act_reminder_rt_wd) rptTypeOpts = settings.act_reminder_rt_wd
                            //         }
                            //         if(settings."act_reminder_rt" && settings."act_reminder_rt" == "everyxdays") {
                            //             input "act_reminder_rt_ed", sNUMBER, title: inTS1("Every X Days (1-31)", sCHKBOX), description: sBLANK, range: "1..31", required: true, submitOnChange: true
                            //             if(settings.act_reminder_rt_ed) rptTypeOpts = settings.act_reminder_rt_ed
                            //         }
                            //     }
                            // }
                            // input "act_reminder_remove", sBOOL, title: "Remove Reminder when done", defaultValue: true, submitOnChange: true, required: false
                        }
                        actionVolumeInputs(devices, false, true)
                        def newTime = settings.act_reminder_time ? parseFmtDt("yyyy-MM-dd'T'HH:mm:ss.SSSZ", 'HH:mm', settings.act_reminder_time) : null
                        actionExecMap.config.reminder = [cmd: "createReminderNew", label: settings.act_reminder_label, date: settings.act_reminder_date, time: newTime, recur: [type: rptType, opts: rptTypeOpts], remove: settings.act_reminder_remove]
                        done = act_reminder_label && act_reminder_date && act_reminder_time
                    } else { done = false }
                    break

                case "dnd":
                    echoDevicesInputByPerm("doNotDisturb")
                    if(settings.act_EchoDevices) {
                        Map dndOpts = ["doNotDisturbOn":"Enable", "doNotDisturbOff":"Disable"]
                        section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                        section(sTS("Action Type Config:")) {
                            input "act_dnd_cmd", sENUM, title: inTS1("Select Do Not Disturb Action", sCOMMAND), description: sBLANK, options: dndOpts, required: true, submitOnChange: true
                        }
                        actionExecMap.config.dnd = [cmd: settings.act_dnd_cmd]
                        if(settings.act_dnd_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alexaroutine":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings.act_EchoDevices) {
                        section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                        Map t0 = parent?.getAlexaRoutines()
                        Map routinesAvail = t0 ?: [:]
                        // logDebug("routinesAvail: $routinesAvail")
                        section(sTS("Action Type Config:")) {
                            input "act_alexaroutine_cmd", sENUM, title: inTS1("Select Alexa Routine", sCOMMAND), description: sBLANK, options: routinesAvail, multiple: false, required: true, submitOnChange: true
                        }
                        actionExecMap.config.alexaroutine = [cmd: "executeRoutineId", routineId: settings.act_alexaroutine_cmd]
                        if(settings.act_alexaroutine_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "wakeword":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings.act_EchoDevices) {
                        Integer devsCnt = settings.act_EchoDevices.size() ?: 0
                        List devsObj = []
                        section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                        if(devsCnt >= 1) {
                            List wakeWords = devices[0]?.hasAttribute("wakeWords") ? devices[0]?.currentValue("wakeWords")?.replaceAll('"', sBLANK)?.split(",") : []
                            // logDebug("WakeWords: ${wakeWords}")
                            devices?.each { cDev->
                                section(sTS("${cDev?.getLabel()}:")) {
                                    if(wakeWords?.size()) {
                                        paragraph "Current Wake Word: ${cDev?.hasAttribute("alexaWakeWord") ? cDev?.currentValue("alexaWakeWord") : "Unknown"}"
                                        input "act_wakeword_device_${cDev?.id}", sENUM, title: inTS1("New Wake Word", "list"), description: sBLANK, options: wakeWords, required: true, submitOnChange: true
                                        devsObj.push([device: cDev?.id as String, wakeword: settings."act_wakeword_device_${cDev?.id}", cmd: "setWakeWord"])
                                    } else { paragraph "Oops...\nNo Wake Words have been found!  Please Remove the device from selection.", state: null, required: true }
                                }
                            }
                        }
                        actionExecMap.config.wakeword = [ devices: devsObj]
                        // def aCnt = settings.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }
                        // log.debug "aCnt: ${aCnt} | devsCnt: ${devsCnt}"
                        done = settings.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }?.size() == devsCnt
                    } else { done = false }
                    break

                case "bluetooth":
                    echoDevicesInputByPerm("bluetoothControl")
                    if(settings.act_EchoDevices) {
                        Integer devsCnt = settings.act_EchoDevices?.size() ?: 0
                        List devsObj = []
                        section(sTS("Action Description:")) { paragraph pTS(actTypeDesc, getAppImg("info"), false, sCLR4D9), state: sCOMPLT }
                        if(devsCnt >= 1) {
                            devices?.each { cDev->
                                def btData = cDev?.hasAttribute("btDevicesPaired") ? cDev?.currentValue("btDevicesPaired") : null
                                List btDevs = (btData) ? parseJson(btData)?.names : []
                                // log.debug "btDevs: $btDevs"
                                section(sTS("${cDev?.getLabel()}:")) {
                                    if(btDevs?.size()) {
                                        input "act_bluetooth_device_${cDev?.id}", sENUM, title: inTS1("BT device to use", "bluetooth"), description: sBLANK, options: btDevs, required: true, submitOnChange: true
                                        input "act_bluetooth_action_${cDev?.id}", sENUM, title: inTS1("BT action to take", sCOMMAND), description: sBLANK, options: ["connectBluetooth":"connect", "disconnectBluetooth":"disconnect"], required: true, submitOnChange: true
                                        devsObj?.push([device: cDev?.id as String, btDevice: settings."act_bluetooth_device_${cDev?.id}", cmd: settings."act_bluetooth_action_${cDev?.id}"])
                                    } else { paragraph "Oops...\nNo Bluetooth devices are paired to this Echo Device!  Please Remove the device from selection.", state: null, required: true }
                                }
                            }
                        }
                        actionExecMap.config.bluetooth = [devices: devsObj]
                        done = settings.findAll { it?.key?.startsWith("act_bluetooth_device_") && it?.value }?.size() == devsCnt &&
                                settings.findAll { it?.key?.startsWith("act_bluetooth_action_") && it?.value }?.size() == devsCnt
                    } else { done = false }
                    break
                default:
                    paragraph pTS("Unknown Action Type Defined...", getAppImg("error"), true, sCLRRED), required: true, state: null
                    break
            }
            if(done) {
                section(sTS("Delay Config:")) {
                    input "act_delay", sNUMBER, title: inTS1("Delay Action in Seconds\n(Optional)", "delay_time"), required: false, submitOnChange: true
                }
                if(isTierAct && (Integer)settings.act_tier_cnt > 1) {
                    section(sTS("Tier Action Start Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tiered Tasks to Perform on Tier Start?", "tasks"), description: actTaskDesc("act_tier_start_", true), params:[type: "act_tier_start_"], state: (actTaskDesc("act_tier_start_") ? sCOMPLT : null)
                    }
                    section(sTS("Tier Action Stop Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tiered Tasks to Perform on Tier Stop?", "tasks"), description: actTaskDesc("act_tier_stop_", true), params:[type: "act_tier_stop_"], state: (actTaskDesc("act_tier_stop_") ? sCOMPLT : null)
                    }
                } else {
                    section(sTS("Action Triggered Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tasks to Perform when Triggered?", "tasks"), description: actTaskDesc("act_", true), params:[type: "act_"], state: (actTaskDesc("act_") ? sCOMPLT : null)
                    }
                }
                actionSimulationSect()
                section(sBLANK) {
                    paragraph pTS("You are all done with this step.\n\nPress Next/Done/Save to go back", getAppImg("done")), state: sCOMPLT
                }
                actionExecMap.config.volume = [change: settings.act_volume_change, restore: settings.act_volume_restore, alarm: settings.act_alarm_volume]

                actionExecMap.delay = settings.act_delay
                actionExecMap.configured = true
                actionCleanup()
                //TODO: Check Cleanup of non selected inputs
            } else { actionExecMap = [configured: false] }
        }
        Map t1 = (done && (Boolean)actionExecMap.configured) ? actionExecMap : [configured: false]
        state.actionExecMap = t1
        updConfigStatusMap()
        logDebug("actionExecMap: ${t1}")
    }
}

def actTrigTasksPage(params) {
    String t = params?.type
    if(t) {
        atomicState.curPageParams = params
    } else { t = atomicState.curPageParams?.type }
    return dynamicPage(name: "actTrigTasksPage", title: sBLANK, install: false, uninstall: false) {
        Map dMap = [:]
        section() {
            switch(t) {
                case "act_":
                    dMap = [def: sBLANK, delay: "tasks"]
                    paragraph pTS("These tasks will be performed when the action is triggered.\n(Delay is optional)", sNULL, false, sCLR4D9), state: sCOMPLT
                    break
                case "act_tier_start_":
                    dMap = [def: " with Tier start", delay: "Tier Start tasks"]
                    paragraph pTS("These tasks will be performed with when the first tier of action is triggered.\n(Delay is optional)", sNULL, false, sCLR4D9), state: sCOMPLT
                    break
                case "act_tier_stop_":
                    dMap = [def: " with Tier stop", delay: "Tier Stop tasks"]
                    paragraph pTS("These tasks will be performed with when the last tier of action is triggered.\n(Delay is optional)", sNULL, false, sCLR4D9), state: sCOMPLT
                    break
            }
        }
        if(!settings.enableWebCoRE) {
            section (sTS("Enable webCoRE Integration:")) {
                input "enableWebCoRE", sBOOL, title: inTS("Enable webCoRE Integration", webCore_icon()), required: false, defaultValue: false, submitOnChange: true
            }
        }
        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
        }
        section(sTS("Control Devices:")) {
            input "${t}switches_on", "capability.switch", title: inTS1("Turn ON these Switches${dMap?.def}\n(Optional)", sSWITCH), multiple: true, required: false, submitOnChange: true
            input "${t}switches_off", "capability.switch", title: inTS1("Turn OFF these Switches${dMap?.def}\n(Optional)", sSWITCH), multiple: true, required: false, submitOnChange: true
        }

        section(sTS("Control Lights:")) {
            input "${t}lights", "capability.switch", title: inTS1("Turn ON these Lights${dMap?.def}\n(Optional)", "light"), multiple: true, required: false, submitOnChange: true
            if(settings."${t}lights") {
                List lights = settings."${t}lights"
                if(lights?.any { i-> (i?.hasCommand("setColor")) } && !lights?.every { i-> (i?.hasCommand("setColor")) }) {
                    paragraph pTS("Not all selected devices support color. So color options are hidden.", sNULL, true, sCLRRED), state: null, required: true
                    settingRemove("${t}lights_color".toString())
                    settingRemove("${t}lights_color_delay".toString())
                } else {
                    input "${t}lights_color", sENUM, title: inTS1("To this color?\n(Optional)", sCOMMAND), multiple: false, options: colorSettingsListFLD?.name, required: false, submitOnChange: true
                    if(settings."${t}lights_color") {
                        input "${t}lights_color_delay", sNUMBER, title: inTS1("Restore original light state after (x) seconds?\n(Optional)", "delay"), required: false, submitOnChange: true
                    }
                }
                if(lights?.any { i-> (i?.hasCommand("setLevel")) } && !lights?.every { i-> (i?.hasCommand("setLevel")) }) {
                    paragraph pTS("Not all selected devices support level. So level option is hidden.", sNULL, true, sCLRRED), state: null, required: true
                    settingRemove("${t}lights_level".toString())
                } else { input "${t}lights_level", sENUM, title: inTS1("At this level?\n(Optional)", "speed_knob"), options: dimmerLevelEnum(), required: false, submitOnChange: true }
            }
        }

        section(sTS("Control Locks:")) {
            input "${t}locks_lock", "capability.lock", title: inTS1("Lock these Locks${dMap?.def}\n(Optional)", "lock"), multiple: true, required: false, submitOnChange: true
            input "${t}locks_unlock", "capability.lock", title: inTS1("Unlock these Locks${dMap?.def}\n(Optional)", "lock"), multiple: true, required: false, submitOnChange: true
        }

        section(sTS("Control Keypads:")) {
            input "${t}securityKeypads_disarm", "capability.securityKeypad", title: inTS1("Disarm these Keypads${dMap?.def}\n(Optional)", "lock"), multiple: true, required: false, submitOnChange: true
            input "${t}securityKeypads_armHome", "capability.securityKeypad", title: inTS1("Arm Home these Keypads${dMap?.def}\n(Optional)", "lock"), multiple: true, required: false, submitOnChange: true
            input "${t}securityKeypads_armAway", "capability.securityKeypad", title: inTS1("Arm Away these Keypads${dMap?.def}\n(Optional)", "lock"), multiple: true, required: false, submitOnChange: true
        }

        section(sTS("Control Doors:")) {
            input "${t}doors_close", "capability.garageDoorControl", title: inTS1("Close these Garage Doors${dMap?.def}\n(Optional)", "garage_door"), multiple: true, required: false, submitOnChange: true
            input "${t}doors_open", "capability.garageDoorControl", title: inTS1("Open these Garage Doors${dMap?.def}\n(Optional)", "garage_door"), multiple: true, required: false, submitOnChange: true
        }

        section(sTS("Control Siren:")) {
            input "${t}sirens", "capability.alarm", title: inTS1("Activate these Sirens${dMap?.def}\n(Optional)", "siren"), multiple: true, required: false, submitOnChange: true
            if(settings."${t}sirens") {
                input "${t}siren_cmd", sENUM, title: inTS1("Alarm action to take${dMap?.def}\n(Optional)", sCOMMAND), options: ["both": "Siren & Stobe", "strobe":"Strobe Only", "siren":"Siren Only"], multiple: false, required: true, submitOnChange: true
                input "${t}siren_time", sNUMBER, title: inTS1("Stop after (x) seconds...", "delay"), required: true, submitOnChange: true
            }
        }
        section(sTS("Location Actions:")) {
            input "${t}mode_run", sENUM, title: inTS1("Set Location Mode${dMap?.def}\n(Optional)", sMODE), options: getLocationModes(true), multiple: false, required: false, submitOnChange: true
            input "${t}alarm_run", sENUM, title: inTS1("Set ${getAlarmSystemName()} mode${dMap?.def}\n(Optional)", "alarm_home"), options: getAlarmSystemStatusActions(), multiple: false, required: false, submitOnChange: true

            if(settings.enableWebCoRE) {
                input "${t}piston_run", sENUM, title: inTS("Execute a piston${dMap?.def}\n(Optional)", webCore_icon()), options: webCoRE_list('name'), multiple: false, required: false, submitOnChange: true
            }
        }

        if(actTasksConfiguredByType(t)) {
            section("Delay before running Tasks: ") {
                input "${t}tasks_delay", sNUMBER, title: inTS1("Delay running ${dMap?.delay} in Seconds\n(Optional)", "delay_time"), required: false, submitOnChange: true
            }
        }
    }
}

Boolean actTasksConfiguredByType(String pType) {
    String p = pType
    return (
            (String)settings."${p}mode_run" || (String)settings."${p}alarm_run" /* || settings."${p}routine_run"*/ || settings."${p}switches_off" || settings."${p}switches_on" ||
                (settings.enableWebCoRE && (String)settings."${p}piston_run") ||
                settings."${p}lights" || settings."${p}locks_lock" || settings."${p}locks_unlock" || settings."${p}sirens" || settings."${p}doors_close" || settings."${p}doors_open" ||
                settings."${p}securityKeypads_disarm" || settings."${p}securityKeypads_armHome" || settings."${p}securityKeypads_armAway"
    )
}

private executeTaskCommands(data) {
    String p = data?.type ?: sNULL

    if((String)settings."${p}mode_run") { setLocationMode((String)settings."${p}mode_run") }
    if((String)settings."${p}alarm_run") { sendLocationEvent(name: "hsmSetArm", value: (String)settings."${p}alarm_run") }
    if(settings.enableWebCoRE && (String)settings."${p}piston_run") { webCoRE_execute((String)settings."${p}piston_run") }

    if(settings."${p}switches_off") { settings."${p}switches_off"*.off() }
    if(settings."${p}switches_on") { settings."${p}switches_on"*.on() }
    if(settings."${p}locks_lock") { settings."${p}locks_lock"*.lock() }
    if(settings."${p}locks_unlock") { settings."${p}locks_unlock"*.unlock() }
    if(settings."${p}securityKeypads_disarm") { settings."${p}securityKeypads_disarm"*.disarm() }
    if(settings."${p}securityKeypads_armHome") { settings."${p}securityKeypads_armHome"*.armHome() }
    if(settings."${p}securityKeypads_armAway") { settings."${p}securityKeypads_armAway"*.armAway() }
    if(settings."${p}doors_close") { settings."${p}doors_close"*.close() }
    if(settings."${p}doors_open") { settings."${p}doors_open"*.open() }
    if(settings."${p}sirens" && (String)settings."${p}siren_cmd") {
        String cmd= (String)settings."${p}siren_cmd"
        settings."${p}sirens"*."${cmd}"()
        if(settings."${p}siren_time") runIn(settings."${p}siren_time", postTaskCommands, [data:[type: p]])
    }
    if(settings."${p}lights") {
        if(settings."${p}lights_color_delay") { captureLightState(settings."${p}lights") }
        settings."${p}lights"*.on()
        if(settings."${p}lights_level") { settings."${p}lights"*.setLevel(getColorName(settings."${p}lights_level")) }
        if(settings."${p}lights_color") { settings."${p}lights"*.setColor(getColorName(settings."${p}lights_color", settings."${p}lights_level")) }
        if(settings."${p}lights_color_delay") runIn(settings."${p}lights_color_delay", restoreLights, [data:[type: p]])
    }
}

String actTaskDesc(String t, Boolean isInpt=false) {
    String str = sBLANK
    if(actTasksConfiguredByType(t)) {
        switch(t) {
            case "act_":
                str += "${isInpt ? sBLANK : "\n\n"}Triggered Tasks:"
                break
            case "act_tier_start_":
                str += "${isInpt ? sBLANK : "\n\n"}Tiered Start Tasks:"
                break
            case "act_tier_stop_":
                str += "${isInpt ? sBLANK : "\n\n"}Tiered Stop Tasks:"
                break
        }
        String aStr = "\n \u2022 "
        str += settings."${t}switches_on" ? aStr+"Switches On: (${settings."${t}switches_on"?.size()})" : sBLANK
        str += settings."${t}switches_off" ? aStr+"Switches Off: (${settings."${t}switches_off"?.size()})" : sBLANK
        str += settings."${t}lights" ? aStr+"Lights: (${settings."${t}lights"?.size()})" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_level" ? "\n    - Level: (${settings."${t}lights_level"}%)" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_color" ? "\n    - Color: (${settings."${t}lights_color"})" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_color" && settings."${t}lights_color_delay" ? "\n    - Restore After: (${settings."${t}lights_color_delay"} sec.)" : sBLANK
        str += settings."${t}locks_unlock" ? aStr+"Locks Unlock: (${settings."${t}locks_unlock"?.size()})" : sBLANK
        str += settings."${t}locks_lock" ? aStr+"Locks Lock: (${settings."${t}locks_lock"?.size()})" : sBLANK
        str += settings."${t}securityKeypads_disarm" ? aStr+"KeyPads Disarm: (${settings."${t}securityKeypads_disarm".size()})" : sBLANK
        str += settings."${t}securityKeypads_armHome" ? aStr+"KeyPads Arm Home: (${settings."${t}securityKeypads_armHome".size()})" : sBLANK
        str += settings."${t}securityKeypads_armAway" ? aStr+"KeyPads Arm Away: (${settings."${t}securityKeypads_armAway".size()})" : sBLANK
        str += settings."${t}doors_open" ? aStr+"Garages Open: (${settings."${t}doors_open"?.size()})" : sBLANK
        str += settings."${t}doors_close" ? aStr+"Garages Close: (${settings."${t}doors_close"?.size()})" : sBLANK
        str += settings."${t}sirens" ? aStr+"Sirens On: (${settings."${t}sirens"?.size()})${settings."${t}sirens_delay" ? "(${settings."${t}sirens_delay"} sec)" : sBLANK}" : sBLANK

        str += (String)settings."${t}mode_run" ? aStr+"Set Mode:\n \u2022 ${(String)settings."${t}mode_run"}" : sBLANK
        str += (String)settings."${t}alarm_run" ? aStr+"Set Alarm:\n \u2022 ${(String)settings."${t}alarm_run"}" : sBLANK
//        str += settings."${t}routine_run" ? aStr+"Execute Routine:\n    - ${getRoutineById(settings."${t}routine_run")?.label}" : sBLANK
        str += (settings.enableWebCoRE && (String)settings."${t}piston_run") ? aStr+"Execute webCoRE Piston:\n    - " + getPistonById((String)settings."${t}piston_run") : sBLANK
    }
    return str != sBLANK ? (isInpt ? "${str}\n\n"+sTTM : str) : (isInpt ? "On trigger control devices, set mode, set alarm state, execute WebCore Pistons\n\n"+sTTC : sNULL)
}

private flashLights(data) {
    // log.debug "data: ${data}"
    String p = data?.type
    if(!p) return
    def devs = settings."${p}lights"
    if(devs) {
        // log.debug "devs: $devs"
        if(data.cycle <= data.cycles ) {
            logDebug("state: ${data.state} | color1Map: ${data.color1Map} | color2Map: ${data.color2Map}")
            if(data.state == "off" || (data.color1Map && data.color2Map && data.state == data.color2Map)) {
                if(data.color1Map) {
                    data.state = data.color1Map
                    devs*.setColor(data.color1Map)
                } else {
                    data.state = "on"
                }
                devs*.on()
                runIn(1, "flashLights", [data: data])
            } else {
                if(data.color2Map) {
                    data.state = data.color2Map
                    devs*.setColor(data.color2Map)
                } else {
                    data.state = "off"; devs*.off()
                }
                data.cycle = data.cycle + 1
                runIn(1, "flashLights", [data: data])
            }
        } else {
            logDebug("restoring state")
            restoreLightState(settings."${p}lights")
        }
    }
}

private restoreLights(data) {
    if(data?.type && settings."${data.type}lights") { restoreLightState(settings."${data.type}lights") }
}

Boolean isActDevContConfigured() {
    return isTierAction() ? (settings.act_tier_start_switches_off || settings.act_tier_start_switches_on || settings.act_tier_stop_switches_off || settings.act_tier_stop_switches_on) : (settings.act_switches_off || settings.act_switches_on)
}

def actionSimulationSect() {
    section(sTS("Simulate Action")) {
        paragraph pTS("Toggle this to execute the action and see the results.\nWhen global text is not defined, this will generate a random event based on your trigger selections.${act_EchoZones ? "\nTesting with zones requires you to save the app and come back in to test." : sBLANK}", getAppImg("info"), false, sCLR4D9)
        input "actTestRun", sBOOL, title: inTS1("Test this action?", "testing"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        if(actTestRun) { executeActTest() }
    }
}

Boolean customMsgRequired() { return (!((String)settings.actionType in ["speak", "announcement"])) }
Boolean customMsgConfigured() { return (settings.notif_use_custom && settings.notif_custom_message) }

def actNotifPage() {
    return dynamicPage(name: "actNotifPage", title: "Action Notifications", install: false, uninstall: false) {
                        //href "actNotifPage", title: inTS1("Send Notifications", "notification2"), description: (t0 ? "${t0}\n\n"+sTTM : sTTC), state: (t0 ? sCOMPLT : null)
        String a = getAppNotifDesc()
         if(!a) a= "Notifications not enabled"
         section() {
             paragraph pTS(a, sNULL, false, sCLR4D9), state: sCOMPLT
         }

        section (sTS("Notification Devices:")) {
            input "notif_devs", "capability.notification", title: inTS1("Send to Notification devices?", "notification"), required: false, multiple: true, submitOnChange: true
        }
        section (sTS("Alexa Mobile Notification:")) {
            paragraph pTS("This will send a push notification the Alexa Mobile app.", sNULL, false, sCLRGRY)
            input "notif_alexa_mobile", sBOOL, title: inTS1("Send message to Alexa App?", "notification"), required: false, defaultValue: false, submitOnChange: true
        }

        if((List)settings.notif_devs || (Boolean)settings.notif_alexa_mobile) {
            section (sTS("Message Customization:")) {
                Boolean custMsgReq = customMsgRequired()
                if(custMsgReq) {
                    paragraph pTS("The selected action (${(String)settings.actionType}) requires a custom notification message if notifications are enabled.", sNULL, false, sCLRGRY)
                    if(!settings.notif_use_custom) { settingUpdate("notif_use_custom", sTRUE, sBOOL) }
                } else {
                    paragraph pTS("When using speak or announcement actions custom notification is optional and a notification will be sent with speech text.", sNULL, false, sCLRGRY)
                }
                input "notif_use_custom", sBOOL, title: inTS1("Send a custom notification...", "question"), required: false, defaultValue: false, submitOnChange: true
                if(settings.notif_use_custom || custMsgReq) {
                    input "notif_custom_message", sTEXT, title: inTS1("Enter custom message...", sTEXT), required: custMsgReq, submitOnChange: true
                }
            }
        } else {
            List sets = settings.findAll { it?.key?.startsWith("notif_") }?.collect { it?.key as String }
            sets?.each { String sI-> settingRemove(sI) }
        }

        if(isActNotifConfigured()) {
            section(sTS("Notification Restrictions:")) {
                String nsd = getNotifSchedDesc()
                href "actNotifTimePage", title: inTS1("Quiet Restrictions", "restriction"), description: (nsd ? "${nsd}\n\n"+sTTM : sTTC), state: (nsd ? sCOMPLT : null)
            }
            if(!(Boolean)state.notif_message_tested) {
                def actDevices = (Boolean)settings.notif_alexa_mobile ? parent?.getDevicesFromList(settings.act_EchoDevices) : []
                def aMsgDev = actDevices?.size() && (Boolean)settings.notif_alexa_mobile ? actDevices[0] : null
                if(sendNotifMsg("Info", "Action Notification Test Successful. Notifications Enabled for ${app?.getLabel()}", aMsgDev, true)) { state.notif_message_tested = true }
            }
        } else state.remove("notif_message_tested")
    }
}

def actNotifTimePage() {
    return dynamicPage(name:"actNotifTimePage", title: sBLANK, install: false, uninstall: false) {
        String a = getNotifSchedDesc()
         if(a) {
             section() {
                 paragraph pTS("Restrictions Status:\n"+a, sNULL, false, sCLR4D9), state: sCOMPLT
                 paragraph pTS("Notice:\nAll selected restrictions  must be inactive for notifications to be sent.", sNULL, false, sCLR4D9), state: sCOMPLT
             }
         }
        String pre = "notif"
        Boolean timeReq = (settings["${pre}_time_start"] || settings["${pre}_time_stop"])
        section(sTS("Quiet Start Time:")) {
            input "${pre}_time_start_type", sENUM, title: inTS1("Starting at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings."${pre}_time_start_type" == sTIME) {
                input "${pre}_time_start", sTIME, title: inTS1("Start time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_start_type" in lSUNRISESET) {
                input "${pre}_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
        section(sTS("Quiet Stop Time:")) {
            input "${pre}_time_stop_type", sENUM, title: inTS1("Stopping at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings."${pre}_time_stop_type" == sTIME) {
                input "${pre}_time_stop", sTIME, title: inTS1("Stop time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_stop_type" in lSUNRISESET) {
                input "${pre}_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
        section(sTS("Allowed Days:")) {
            input "${pre}_days", sENUM, title: inTS1("Only on these week days", "day_calendar"), multiple: true, required: false, options: weekDaysEnum()
        }
        section(sTS("Allowed Modes:")) {
            input "${pre}_modes", sMODE, title: inTS1("Only in these Modes", sMODE), multiple: true, submitOnChange: true, required: false
        }
    }
}

def ssmlInfoSection() {
    String ssmlTestUrl = "https://topvoiceapps.com/ssml"
    String ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
    String ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
    String ssmlSpeechConsUrl = "https://developer.amazon.com/docs/custom-skills/speechcon-reference-interjections-english-us.html"
    section(sTS("SSML Documentation:"), hideable: true, hidden: true) {
        paragraph title: "What is SSML?", pTS("SSML allows for changes in tone, speed, voice, emphasis. As well as using MP3, and access to the Sound Library", sNULL, false, sCLR4D9), state: sCOMPLT
        href url: ssmlDocsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon SSML Docs", "www"), description: "Tap to open browser"
        href url: ssmlSoundsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon Sound Library", "www"), description: "Tap to open browser"
        href url: ssmlSpeechConsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon SpeechCons", "www"), description: "Tap to open browser"
        href url: ssmlTestUrl, style: sEXTNRL, required: false, title: inTS1("SSML Designer and Tester", "www"), description: "Tap to open browser"
    }
}
/*
private void cleanupDevSettings(prefix) {
    List cDevs = settings.act_EchoDevices
    List sets = settings.findAll { it?.key?.startsWith(prefix) }?.collect { it?.key as String }
    log.debug "cDevs: $cDevs | sets: $sets"
    List rem = []
    if(sets?.size()) {
        if(cDevs?.size()) {
            cDevs.each {
                if(!sets?.contains("${prefix}${it}")) {
                    rem.push("${prefix}${it}")
                }
            }
        } else { rem = rem + sets }
    }
    log.debug "rem: $rem"
    // rem?.each { sI-> if(settings.containsKey(sI as String)) { settingRemove(sI as String) } }
} */

Map customTxtItems() {
    Map items = [:]
    settings.triggerEvents?.each { String tr->
        if(settings."trig_${tr}_txt") { if(!items[tr]) { items[tr] = [:] }; items[tr].event = settings."trig_${tr}_txt"?.toString()?.tokenize(";") }
        if(settings."trig_${tr}_after_repeat_txt") { if(!items[tr]) { items[tr] = [:] };  items[tr].repeat = settings."trig_${tr}_after_repeat_txt"?.toString()?.tokenize(";") }
    }
    return items
}

Boolean hasRepeatTriggers() {
    Map items = [:]
    settings.triggerEvents?.each { String tr->
        if(settings."trig_${tr}_after_repeat_txt") { if(!items[tr]) { items[tr] = [:] };  items[tr].repeat = settings."trig_${tr}_after_repeat_txt"?.toString()?.tokenize(";") }
    }
    return (items.size() > 0)
}

Boolean hasUserDefinedTxt() {
    Boolean fnd = false
    settings.triggerEvents?.find { String tr ->
        if(settings."trig_${tr}_txt") { fnd = true }
        if(settings."trig_${tr}_after_repeat_txt") { fnd = true }
        if(fnd) return true
    }
    return fnd
}

Boolean executionConfigured() {
    Boolean opts = (state.actionExecMap && state.actionExecMap.configured == true)
    Boolean devs = (settings.act_EchoDevices || settings.act_EchoZones)
    return (opts && devs)
}
/*
private getLastEchoSpokenTo() {
    def a = parent?.getChildDevicesByCap("TTS")?.find { (it?.currentWasLastSpokenToDevice?.toString() == sTRUE) }
    return a ?: null
}
*/
private echoDevicesInputByPerm(String type) {
    List echoDevs = parent?.getChildDevicesByCap(type)
    Boolean capOk = (type in ["TTS", "announce"])
    Boolean zonesOk = ((String)settings.actionType in ["speak", "speak_tiered", "announcement", "announcement_tiered", "voicecmd", "sequence", "weather", "calendar", "music", "sounds", "builtin"])
    Map echoZones = (capOk && zonesOk) ? getZones() : [:]
    section(sTS("${echoZones?.size() ? "Zones & " : sBLANK}Alexa Devices:")) {
//    section(sTS("Alexa Devices${echoZones?.size() ? " & Zones" : sBLANK}:")) {
        if(echoZones?.size()) {
            if(!settings.act_EchoZones) { paragraph pTS("Zones are used to direct the speech output based on the conditions in the zone (Motion, presence, etc).\nWhen both Zones and Echo devices are selected, the zone will take priority over the echo device setting.", sNULL, false) }
            input "act_EchoZones", sENUM, title: inTS1("Zone(s) to Use", "es_groups"), description: "Select the Zone(s)", options: echoZones?.collectEntries { [(it?.key): it?.value?.name as String] }, multiple: true, required: (!settings.act_EchoDevices), submitOnChange: true
        }
        if(settings.act_EchoZones?.size() && echoDevs?.size() && !settings.act_EchoDevices?.size()) {
            paragraph pTS("There may be scenarios when none of your zones are active at the triggered action execution.\nYou have the option to select echo devices to use when no zones are available.", sNULL, false, sCLR4D9)
        }
        if(echoDevs?.size()) {
            Boolean devsOpt = (settings.act_EchoZones?.size())
            def eDevsMap = echoDevs?.collectEntries { [(it?.getId()): [label: it?.getLabel(), lsd: (it?.currentWasLastSpokenToDevice?.toString() == sTRUE)]] }?.sort { a,b -> b?.value?.lsd <=> a?.value?.lsd ?: a?.value?.label <=> b?.value?.label }
            input "act_EchoDevices", sENUM, title: inTS1("Echo Speaks Devices${devsOpt ? "\n(Optional backup)" : sBLANK}", "echo_gen1"), description: (devsOpt ? "These devices are used when all zones are inactive." : "Select your devices"), options: eDevsMap?.collectEntries { [(it?.key): "${it?.value?.label}${(it?.value?.lsd == true) ? "\n(Last Spoken To)" : sBLANK}"] }, multiple: true, required: (!settings.act_EchoZones), submitOnChange: true
            List aa = settings.act_EchoDevices
            List devIt = aa.collect { it ? it.toInteger():null }
            app.updateSetting( "act_EchoDeviceList", [type: "capability", value: devIt?.unique()]) // this won't take effect until next execution
        } else { paragraph pTS("No devices were found with support for ($type)", sNULL, true, sCLRRED) }
    }
    //updateZoneSubscriptions()
}

private actionVolumeInputs(devices, Boolean showVolOnly=false, Boolean showAlrmVol=false) {
    if(showAlrmVol) {
        section(sTS("Volume Options:")) {
            input "act_alarm_volume", sNUMBER, title: inTS1("Alarm Volume\n(Optional)", "speed_knob"), range: "0..100", required: false, submitOnChange: true
        }
    } else {
        if((devices || settings.act_EchoZones) && (String)settings.actionType in ["speak", "announcement", "weather", "sounds", "builtin", "music", "calendar", "playback"]) {
            Map volMap = devsSupportVolume(devices)
            Integer volMapSiz = volMap?.n?.size()
            Integer devSiz = devices?.size()
            section(sTS("Volume Options:")) {
                if(volMapSiz > 0 && volMapSiz < devSiz) { paragraph "Some of the selected devices do not support volume control" }
                else if(devSiz == volMapSiz) { paragraph "Some of the selected devices do not support volume control"; return }
                input "act_volume_change", sNUMBER, title: inTS1("Volume Level\n(Optional)", "speed_knob"), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
                if(!showVolOnly) { input "act_volume_restore", sNUMBER, title: inTS1("Restore Volume\n(Optional)", "speed_knob"), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true }
            }
        }
    }
}

def condTimePage() {
    return dynamicPage(name:"condTimePage", title: sBLANK, install: false, uninstall: false) {
        Boolean timeReq = (settings["cond_time_start"] || settings["cond_time_stop"])
        section(sTS("Condition Start Time:")) {
            input "cond_time_start_type", sENUM, title: inTS1("Starting at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings.cond_time_start_type  == sTIME) {
                input "cond_time_start", sTIME, title: inTS1("Start time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings.cond_time_start_type in lSUNRISESET) {
                input "cond_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
        section(sTS("Condition Stop Time:")) {
            input "cond_time_stop_type", sENUM, title: inTS1("Stopping at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings.cond_time_stop_type == sTIME) {
                input "cond_time_stop", sTIME, title: inTS1("Stop time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings.cond_time_stop_type in lSUNRISESET) {
                input "cond_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
    }
}

def uninstallPage() {
    return dynamicPage(name: "uninstallPage", title: "Uninstall", install: false , uninstall: true) {
        section(sBLANK) { paragraph "This will delete this Echo Speaks Action." }
    }
}

static Boolean wordInString(String findStr, String fullStr) {
    List parts = fullStr?.split(sSPACE)?.collect { it?.toString()?.toLowerCase() }
    return (findStr in parts)
}

def installed() {
    logInfo("Installed Event Received...")
    state.dateInstalled = getDtNow()
    if((Boolean)settings.duplicateFlag && !(Boolean)state.dupPendingSetup) {
        Boolean maybeDup = app?.getLabel()?.toString()?.contains(" (Dup)")
        state.dupPendingSetup = true
        runIn(2, "processDuplication")
        if(maybeDup) logInfo("installed found maybe a dup... ${settings.duplicateFlag}")
    } else {
        if(!(Boolean)state.dupPendingSetup) initialize()
    }
}

@Field static final String dupMSGFLD = "This action is duplicated and has not had configuration completed... Please open action and configure to complete setup..."

def updated() {
    logInfo("Updated Event Received...")
    Boolean maybeDup = app?.getLabel()?.toString()?.contains(" (Dup)")
    if((Boolean)settings.duplicateFlag) {
        if((Boolean)state.dupOpenedByUser) { state.dupPendingSetup = false }
        if((Boolean)state.dupPendingSetup){
            logInfo(dupMSGFLD)
            return
        }
        logInfo("removing duplicate status")
        settingRemove('duplicateFlag'); settingRemove('duplicateSrcId')
        state.remove('dupOpenedByUser'); state.remove('dupPendingSetup'); state.remove('badMode')
    }
    initialize()
}

def initialize() {
    logInfo("Initialize Event Received...")
    if((Boolean)settings.duplicateFlag && (Boolean)state.dupPendingSetup){
        logInfo(dupMSGFLD)
        return
    }
    unsubscribe()
    unschedule()
    state.isInstalled = true
    state.afterEvtCheckWatcherSched = false
    atomicState.tierSchedActive = false
    updAppLabel()
    if(advLogsActive()) { logsEnabled() }
    runIn(3, "actionCleanup")
    if(!isPaused(true)) {
        runIn(7, "subscribeToEvts")
        runEvery1Hour("healthCheck")
        if(settings.enableWebCoRE){
            remTsVal(sLASTWU)
            webCoRE_init()
        }
        updConfigStatusMap()
        resumeTierJobs()
    }
}

private void processDuplication() {
    String newLbl = "${app?.getLabel()}${app?.getLabel()?.toString()?.contains(" (Dup)") ? "" : " (Dup)"}"
    app?.updateLabel(newLbl)
    state.dupPendingSetup = true

    String dupSrcId = settings.duplicateSrcId ? (String)settings.duplicateSrcId : sNULL
    Map dupData = parent?.getChildDupeData("actions", dupSrcId)
    log.debug "dupData: ${dupData}"
    if(dupData && dupData.state?.size()) {
        dupData.state.each { String k,v-> state[k] = v }
    }

    if(dupData && dupData.settings?.size()) {
        dupData.settings.each { String k, Map v->
           if((String)v.type in [sENUM]) settingRemove(k)

           if((String)v.type in [sMODE]){
              String msg = "Found mode settings $k is type $v.type value is ${v.value}, this setting needs to be updated to work properly"
              logWarn(msg)
              state.badMode=msg

              settingRemove(k)
              List modeIt= v.value?.collect { String vit ->
                  location.getModes()?.find { (String)it.name == vit ? it.toString() : null }
              }
//              log.warn "new settings $k is $modeIt"
              if(modeIt) app.updateSetting( k, [type: sMODE, value: modeIt]) // this won't take effect until next execution

           } else settingUpdate(k, (v.value != null ? v.value : null), (String)v.type)
        }
    }
    parent.childAppDuplicationFinished("actions", dupSrcId)
    logInfo("Duplicated Action has been created... Please open action and configure to complete setup...")
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

/*
private void updateZoneSubscriptions() {
    if(settings.act_EchoZones) {
        if(state.zoneEvtsActive != true) {
            subscribe(location, "es3ZoneState", zoneStateHandler)
            subscribe(location, "es3ZoneRemoved", zoneRemovedHandler)
            state.zoneEvtsActive = true
            runIn(6, requestZoneRefresh)
        }
    }
}
*/
String getActionName() { return (String)settings.appLbl }

private void updAppLabel() {
    String newLbl = "${settings.appLbl} (A${isPaused(true) ? " \u275A\u275A" : sBLANK})".replaceAll(/ (Dup)/, sBLANK).replaceAll("\\s"," ")
    if(settings.appLbl && app?.getLabel() != newLbl) { app?.updateLabel(newLbl) }
}

private void updConfigStatusMap() {
    //ERS
    Map sMap = [:]
    sMap.triggers = triggersConfigured()
    sMap.conditions = conditionsConfigured()
    sMap.actions = executionConfigured()
    sMap.tiers = isTierActConfigured()
    state.configStatusMap = sMap
}

Boolean getConfStatusItem(String item) {
    //ERS
    switch(item) {
       case "tiers": return isTierActConfigured()
       case "actions": return executionConfigured()
    }
    return (state.configStatusMap?.containsKey(item) && state.configStatusMap[item] == true)
}

private void actionCleanup() {
    stateMapMigration()
    // State Cleanup
    //ERS
    // keep actionExecMap configStatusMap schedTrigMap
    List items = ["afterEvtMap", "afterEvtChkSchedMap", "actTierState", "tierSchedActive", "zoneStatusMap"]
    updMemStoreItem("afterEvtMap", [:])
    updMemStoreItem("afterEvtChkSchedMap", [:])
    updMemStoreItem("actTierState", [:])
    items.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
    //Cleans up unused action setting items
    List setItems = []
    List setIgn = ["act_delay", "act_volume_change", "act_volume_restore", "act_tier_cnt", "act_switches_off", "act_switches_on", "act_piston_run", "act_mode_run", "act_alarm_run"]
    if(settings.act_EchoZones) { setIgn.push("act_EchoZones"); Map a = getActiveZones() } // to fill in FLD
    else if(settings.act_EchoDevices) { setIgn.push("act_EchoDevices"); setIgn.push("act_EchoDeviceList") }

    tierItemCleanup()
    if((String)settings.actionType) {
        Boolean isTierAct = isTierAction()
        //ERS
//  isTierAct      return ((String)settings.actionType in ["speak_tiered", "announcement_tiered"])
//                case "act_tier_start_":
//                case "act_tier_stop_":
//        if(isTierAct) {
        ["act_lights", "act_locks", "act_securityKeypads", "act_doors", "act_sirens"]?.each { String it -> settings.each { sI -> if(sI.key.startsWith(it)) { isTierAct ? setItems.push(sI.key as String) : setIgn.push(sI.key as String) } } }
//        }
        ["act_tier_start_", "act_tier_stop_"]?.each { String it -> settings.each { sI -> if(sI.key.startsWith(it)) { isTierAct ? setIgn.push(sI.key as String) : setItems.push(sI.key as String) } } }
        settings.each { si->
            if(!(si.key in setIgn) && si.key.startsWith("act_") && !si.key.startsWith("act_${(String)settings.actionType}") && (!isTierAct && si.key.startsWith("act_tier_item_"))) { setItems.push(si?.key as String) }
        }
    }

    // Cleanup Unused Trigger Types...
    if(settings.triggerEvents) {
        List trigKeys = settings.findAll { it?.key?.startsWith("trig_") && !(it?.key?.tokenize("_")[1] in settings.triggerEvents) }?.keySet()?.collect { it?.tokenize("_")[1] as String }?.unique()
        // log.debug "trigKeys: $trigKeys"
        if(trigKeys?.size()) {
            trigKeys?.each { tk-> setItems.push("trig_${tk}"); ["wait", "all", "cmd", "once", "after", "txt", "nums"]?.each { ei-> setItems.push("trig_${tk}_${ei}") } }
        }
    }

    // Cleanup Unused Schedule Trigger Items
    setItems = setItems + ["trig_scheduled_sunState"]
    if(!settings.trig_scheduled_type) {
        setItems = setItems + ["trig_scheduled_daynums", "trig_scheduled_months", "trig_scheduled_type", "trig_scheduled_recurrence", "trig_scheduled_time", "trig_scheduled_weekdays", "trig_scheduled_weeks"]
    } else {
        switch(settings.trig_scheduled_type) {
            case "One-Time":
            case "Sunrise":
            case "Sunset":
                setItems = setItems + ["trig_scheduled_daynums", "trig_scheduled_months", "trig_scheduled_recurrence", "trig_scheduled_weekdays", "trig_scheduled_weeks"]
                if(settings.trig_scheduled_type in ["Sunset", "Sunrise"]) { setItems.push("trig_scheduled_time") }
                else setItems = setItems + ["trig_scheduled_sunState_offset"]
                break
            case "Recurring":
                switch(settings.trig_scheduled_recurrence) {
                    case "Daily":
                        setItems = setItems + ["trig_scheduled_daynums", "trig_scheduled_months", "trig_scheduled_weeks"]
                        break
                    case "Weekly":
                        setItems.push("trig_scheduled_daynums")
                        break
                    case "Monthly":
                        setItems.push("trig_scheduled_weekdays")
                        if(settings.trig_scheduled_daynums && settings.trig_scheduled_weeks) { setItems.push("trig_scheduled_weeks") }
                        break
                }
                break
        }
    }

    settings.each { si-> if(si?.key?.startsWith("broadcast") || si?.key?.startsWith("musicTest") || si?.key?.startsWith("announce") || si?.key?.startsWith("sequence") || si?.key?.startsWith("speechTest")) { setItems.push(si?.key as String) } }
    if(state.webCoRE) { state.remove("webCoRE") }
    if(!settings.enableWebCoRE) { setItems.push("webCorePistons"); setItems.push("act_piston_run") }
    setItems = setItems + ["tuneinSearchQuery", "usePush", "smsNumbers", "pushoverSound", "pushoverDevices", "pushoverEnabled", "pushoverPriority", "alexaMobileMsg", "appDebug"]
    // Performs the Setting Removal
    // log.debug "setItems: $setItems"
    setItems.unique()?.each { String sI-> if(settings.containsKey(sI)) { settingRemove(sI) } }
}

Boolean isPaused(Boolean chkAll = false) { return (Boolean)settings.actionPause && (chkAll ? !(state.dupPendingSetup == true) : true) }

public void triggerInitialize() { runIn(3, "initialize") }
private Boolean valTrigEvt(String key) { return (key in settings.triggerEvents) }

public void updatePauseState(Boolean pause) {
    if((Boolean)settings.actionPause != pause) {
        logDebug("Received Request to Update Pause State to (${pause})")
        settingUpdate("actionPause", "${pause}", sBOOL)
        runIn(4, "updated")
    }
}

private healthCheck() {
    logTrace("healthCheck")
    if(advLogsActive()) { logsDisable() }
    if(settings.enableWebCoRE) webCoRE_poll()
}

String cronBuilder() {
    /****
        Cron Expression Format: (<second> <minute> <hour> <day-of-month> <month> <day-of-week> <?year>)

        * (all) – it is used to specify that event should happen for every time unit. For example, “*” in the <minute> field – means “for every minute”
        ? (any) – it is utilized in the <day-of-month> and <day-of -week> fields to denote the arbitrary value – neglect the field value. For example, if we want to fire a script at “5th of every month” irrespective of what the day of the week falls on that date, then we specify a “?” in the <day-of-week> field
        – (range) – it is used to determine the value range. For example, “10-11” in <hour> field means “10th and 11th hours”
        , (values) – it is used to specify multiple values. For example, “MON, WED, FRI” in <day-of-week> field means on the days “Monday, Wednesday, and Friday”
        / (increments) – it is used to specify the incremental values. For example, a “5/15” in the <minute> field, means at “5, 20, 35 and 50 minutes of an hour”
        L (last) – it has different meanings when used in various fields. For example, if it's applied in the <day-of-month> field, then it means last day of the month, i.e. “31st for January” and so on as per the calendar month. It can be used with an offset value, like “L-3“, which denotes the “third to last day of the calendar month”. In the <day-of-week>, it specifies the “last day of a week”. It can also be used with another value in <day-of-week>, like “6L“, which denotes the “last Friday”
        W (weekday) – it is used to specify the weekday (Monday to Friday) nearest to a given day of the month. For example, if we specify “10W” in the <day-of-month> field, then it means the “weekday near to 10th of that month”. So if “10th” is a Saturday, then the job will be triggered on “9th”, and if “10th” is a Sunday, then it will trigger on “11th”. If you specify “1W” in the <day-of-month> and if “1st” is Saturday, then the job will be triggered on “3rd” which is Monday, and it will not jump back to the previous month
        # – it is used to specify the “N-th” occurrence of a weekday of the month, for example, “3rd Friday of the month” can be indicated as “6#3“

        At 12:00 pm (noon) every day during the year 2017:  (0 0 12 * * ? 2017)

        Every 5 minutes starting at 1 pm and ending on 1:55 pm and then starting at 6 pm and ending at 6:55 pm, every day: (0 0/5 13,18 * * ?)

        Every minute starting at 1 pm and ending on 1:05 pm, every day: (0 0-5 13 * * ?)

        At 1:15 pm and 1:45 pm every Tuesday in the month of June: (0 15,45 13 ? 6 Tue)

        At 9:30 am every Monday, Tuesday, Wednesday, Thursday, and Friday: (0 30 9 ? * MON-FRI)

        At 9:30 am on 15th day of every month: (0 30 9 15 * ?)

        At 6 pm on the last day of every month: (0 0 18 L * ?)

        At 6 pm on the 3rd to last day of every month: (0 0 18 L-3 * ?)

        At 10:30 am on the last Thursday of every month: (0 30 10 ? * 5L)

        At 6 pm on the last Friday of every month during the years 2015, 2016 and 2017: (0 0 18 ? * 6L 2015-2017)

        At 10 am on the third Monday of every month: (0 0 10 ? * 2#3)

        At 12 am midnight on every day for five days starting on the 10th day of the month: (0 0 0 10/5 * ?)
    ****/
    String cron = sNULL
    //List schedTypes = ["One-Time", "Recurring", "Sunrise", "Sunset"]
    String schedType = (String)settings.trig_scheduled_type
    Boolean recur = (schedType == 'Recurring')
    def time = settings.trig_scheduled_time ?: null
    List dayNums = recur && settings.trig_scheduled_daynums ? settings.trig_scheduled_daynums?.collect { it as Integer }?.sort() : null
    List weekNums = recur && settings.trig_scheduled_weeks ? settings.trig_scheduled_weeks?.collect { it as Integer }?.sort() : null
    List monthNums = recur && settings.trig_scheduled_months ? settings.trig_scheduled_months?.collect { it as Integer }?.sort() : null
    if(time) {
        String hour = fmtTime(time, "HH") ?: "0"
        String minute = fmtTime(time, "mm") ?: "0"
        String second = "0" //fmtTime(time, "mm") ?: "0"
        String daysOfWeek = settings.trig_scheduled_weekdays ? settings.trig_scheduled_weekdays?.join(",") : sNULL
        String daysOfMonth = dayNums?.size() ? (dayNums?.size() > 1 ? "${dayNums?.first()}-${dayNums?.last()}" : dayNums[0]) : sNULL
        String weeks = (weekNums && !dayNums) ? weekNums?.join(",") : sNULL
        String months = monthNums ? monthNums?.sort()?.join(",") : sNULL
        // log.debug "hour: ${hour} | m: ${minute} | s: ${second} | daysOfWeek: ${daysOfWeek} | daysOfMonth: ${daysOfMonth} | weeks: ${weeks} | months: ${months}"
        if(hour || minute || second) {
            List cItems = []
            cItems.push(second ?: "*")
            cItems.push(minute ?: "0")
            cItems.push(hour ?: "0")
            cItems.push(daysOfMonth ?: (!daysOfWeek ? "*" : "?"))
            cItems.push(months ?: "*")
            cItems.push(daysOfWeek ? daysOfWeek?.toString()?.replaceAll("\"", sBLANK) : "?")
            if(year) { cItems.push(" ${year}") }
            cron = cItems.join(" ")
        }
    }
    logDebug("cronBuilder | Cron: ${cron}")
    return cron
}

Boolean schedulesConfigured() {
    if(settings.trig_scheduled_type in ["Sunrise", "Sunset"]) { return true }
    else if(settings.trig_scheduled_type == "One-Time" && settings.trig_scheduled_time) { return true }
    else if(settings.trig_scheduled_type == "Recurring" && settings.trig_scheduled_time && settings.trig_scheduled_recurrence) {
        if(settings.trig_scheduled_recurrence == "Daily") { return true }
        else if (settings.trig_scheduled_recurrence == "Weekly" && settings.trig_scheduled_weekdays) { return true }
        else if (settings.trig_scheduled_recurrence == "Monthly" && (settings.trig_scheduled_daynums || settings.trig_scheduled_weeks)) { return true }
    }
    return false
}

void scheduleSunriseSet() {
    if(isPaused(true)) { logWarn("Action is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    def sun = getSunriseAndSunset()
    Long ltim = settings.trig_scheduled_type in ["Sunrise"] ? sun.sunrise.time : sun.sunset.time
    Long offset = (settings.trig_scheduled_sunState_offset ?: 0L) * 60000L // minutes
    Long t = now()
    Long n = ltim+offset
    if(t > n) { logDebug("Not scheduling sunrise, sunset - already past today"); return }
    Long secs = Math.round((n - t)/1000.0D) + 1L
    runIn(secs, scheduleTrigEvt)
    Date tt = new Date(n)
    logDebug("Setting Schedule for ${epochToTime(tt)} in $secs's")
}

void subscribeToEvts() {
    state.handleGuardEvents = false
    if(minVersionFailed ()) { logError("CODE UPDATE required to RESUME operation.  No events will be monitored.", true); return }
    if(isPaused(true)) { logWarn("Action is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    settings.triggerEvents?.each { String te->
        if(te == "scheduled" || settings."trig_${te}") {
            switch (te) {
                case "scheduled":
                    // Scheduled Trigger Events
                    if (schedulesConfigured()) {
                        if(settings.trig_scheduled_type in ["Sunrise", "Sunset"]) {
                            scheduleSunriseSet()
                            schedule('29 0 0 1/1 * ? * ', scheduleSunriseSet)  // run at 00:00:24 every day
                        }
                        else if(settings.trig_scheduled_type in ["One-Time", "Recurring"] && settings.trig_scheduled_time) { schedule(cronBuilder(), "scheduleTrigEvt") }
                    }
                    break
                case "guard":
                    // Alexa Guard Status Events
                    if(settings.trig_guard) state.handleGuardEvents = true
                    break
                case "alarm":
                    // Location Alarm Events
                    subscribe(location, "hsmStatus", alarmEvtHandler)
    // ["armedAway":"Armed Away", "armingAway":"Arming Away Pending exit delay","armedHome":"Armed Home","armingHome":"Arming Home pending exit delay", "armedNight":"Armed Night", "armingNight":"Arming Night pending exit delay","disarm":"Disarmed", "allDisarmed":"All Disarmed","alerts":"Alerts"]
                    if("alerts" in settings.trig_alarm) { subscribe(location, "hsmAlert", alarmEvtHandler) } // Only on Hubitat
                    break
                case sMODE:
                    // Location Mode Events
                    if(settings.cond_mode && !settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
                    subscribe(location, sMODE, modeEvtHandler)
                    break
                case "pistonExecuted":
                    break
/*                case "coolingSetpoint":
                    // Thermostat Events
                    subscribe(settings."trig_${te}", "coolingSetpoint", thermostatEvtHandler)
                    break
                case "heatingSetpoint":
                    subscribe(settings."trig_${te}", "heatingSetpoint", thermostatEvtHandler)
                    break
                case "thermostatOperatingState":
                    subscribe(settings."trig_${te}", "thermostatOperatingState", thermostatEvtHandler)
                    break
                case "thermostatMode":
                    subscribe(settings."trig_${te}", "thermostatMode", thermostatEvtHandler)
                    break
                case "thermostatFanMode":
                    subscribe(settings."trig_${te}", "thermostatFanMode", thermostatEvtHandler)
                    break */
                case "thermostatTemperature":
                    if (settings."trig_${te}_cmd") subscribe(settings."trig_${te}", "temperature", getThermEvtHandlerName(te))
                    break
                default:
                    // Handles Remaining Device Events
                    subscribe(settings."trig_${te}", attributeConvert(te), getDevEvtHandlerName(te))
                    break
            }
        }
    }
}

static String attributeConvert(String attr) {
    Map atts = ["door":"garageDoorControl", "shade":"windowShade"]
    return (atts.containsKey(attr)) ? atts[attr] : attr
}

private getDevEvtHandlerName(String type) {
    // if(isTierAction()) { return "deviceTierEvtHandler" }
    return (type && settings."trig_${type}_after") ? "devAfterEvtHandler" : "deviceEvtHandler"
}

private getThermEvtHandlerName(String type) {
    // if(isTierAction()) { return "deviceTierEvtHandler" }
    return (type && settings."trig_${type}_after") ? "devAfterThermEvtHandler" : "thermostatEvtHandler"
}

@Field volatile static Map<String,Map> zoneStatusMapFLD = [:]

def zoneStateHandler(evt) {
    // This is here as placeholder to prevent flooding the logs with errors after upgrading to v4.0
}

def zoneRemovedHandler(evt) {
    // This is here as placeholder to prevent flooding the logs with errors after upgrading to v4.0
}

// private requestZoneRefresh() {
//    zoneStatusMapFLD =  [:]
//    sendLocationEvent(name: "es3ZoneRefresh", value: "sendStatus", data: [sendStatus: true], isStateChange: true, display: false, displayed: false)
// }

//updateZoneSubscriptions()

public updZones(Map zoneMap) {
    zoneStatusMapFLD = zoneMap
    zoneStatusMapFLD = zoneStatusMapFLD
}

public Map getZones() {
    Map a = zoneStatusMapFLD
    if(!a) {
        a = parent.getZones()
    }
    String i = 'initialized'
    if(a.containsKey(i))a.remove(i)
    return a
}

public Map getActiveZones() {
    Map t0 = getZones()
    Map zones = t0 ?: [:]
    return zones.size() ? zones.findAll { it?.value?.active == true  && !it?.value?.paused } : [:]
}

public List getActiveZoneNames() {
    Map t0 = getActiveZones()
    Map zones = t0 ?: [:]
    return zones.size() ? zones.collect { (String)it?.value?.name } : []
}

/***********************************************************************************************************
    EVENT HANDLER FUNCTIONS
************************************************************************************************************/

def scheduleTest() {
    scheduleTrigEvt([date: new Date(), name: "test", value: "Stest", displayName: "Schedule Test"])
}

def scheduleTrigEvt(evt=null) {
    if(!evt) {
        Date adate = new Date()
        String dt = dateTimeFmt(adate, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        evt = [name: "Schedule", displayName: "Scheduled Trigger", value: fmtTime(dt), date: adate, deviceId: null]
    }
    Long evtDelay = now() - ((Date)evt.date).getTime()
    logTrace( "${(String)evt.name} Event | Device: ${(String)evt.displayName} | Value: (${strCapitalize(evt.value)}) with a delay of ${evtDelay}ms")
    if (!schedulesConfigured()) { return }
    //List schedTypes = ["One-Time", "Recurring", "Sunrise", "Sunset"]
    String schedType = (String)settings.trig_scheduled_type
    Boolean recur = schedType == 'Recurring'
    Map dateMap = getDateMap()
    // log.debug "dateMap: $dateMap"
    //List recurOpts = ["Daily", "Weekly", "Monthly"] // "Yearly"
    String srecur = recur ? settings.trig_scheduled_recurrence : sNULL
    List days = recur ? settings.trig_scheduled_weekdays : null
    List daynums = recur ? settings.trig_scheduled_daynums : null
    List weeks = recur ? settings.trig_scheduled_weeks : null
    List months = recur ? settings.trig_scheduled_months : null

    //ERS
    Boolean aa = getTheLock(sHMLF, "scheduleTrigEvt")
    // log.trace "lock wait: ${aa}"
//    Map t0 = atomicState.schedTrigMap
//    Map sTrigMap = t0 ?: [:]
    Map sTrigMap = (Map)getMemStoreItem("schedTrigMap", [:])
    if(!sTrigMap) sTrigMap = state.schedTrigMap ?: [:]

    Boolean wdOk = (days && srecur in ["Daily", "Weekly"]) ? (dateMap.dayNameShort in days && sTrigMap?.lastRun?.dayName != dateMap.dayNameShort) : true
    Boolean mdOk = daynums ? (dateMap.day in daynums && sTrigMap?.lastRun?.day != dateMap.day) : true
    Boolean wOk = (weeks && srecur in ["Weekly"]) ? (dateMap.week in weeks && sTrigMap?.lastRun?.week != dateMap.week) : true
    Boolean mOk = (months && srecur in ["Weekly", "Monthly"]) ? (dateMap.month in months && sTrigMap?.lastRun?.month != dateMap.month) : true
    // Boolean yOk = (sTrigMap.lastRun && srecur in ["Yearly"]) ? (sTrigMap?.lastRun?.year != dateMap.year) : true
    if(wdOk && mdOk && wOk && mOk) {
        sTrigMap.lastRun = dateMap
        updMemStoreItem("schedTrigMap", sTrigMap)
        state.schedTrigMap = sTrigMap
//        atomicState.schedTrigMap = sTrigMap
        releaseTheLock(sHMLF)
        executeAction(evt, false, "scheduleTrigEvt", false, false)
    } else {
        releaseTheLock(sHMLF)
        logDebug("scheduleTrigEvt | dayOfWeekOk: $wdOk | dayOfMonthOk: $mdOk | weekOk: $wOk | monthOk: $mOk")
    }
}

def alarmEvtHandler(evt) {
    Long evtDelay = now() - evt?.date?.getTime()
    String eN = (String)evt?.name
    def eV = evt?.value
    logTrace( "${eN} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(eV)}) with a delay of ${evtDelay}ms")
    if(!settings.trig_alarm) return
    // Boolean dco = (settings.trig_alarm_once == true)
    // Integer dcw = settings.trig_alarm_wait ?: null
    // Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk([date: evt?.date, deviceId: "alarm", value: evt?.value, name: evt?.name, displayName: evt?.displayName], dco, dcw) : true)
    // if(!evtWaitOk) { return }
    Boolean ok2Run = true
    switch(eN) {
        case "hsmStatus":
        case "alarmSystemStatus":
            if(!(eV in settings.trig_alarm)) ok2Run = false
            break
        case "hsmAlert":
            if (!("alerts" in settings.trig_alarm)) ok2Run = false
            if (!(eV in settings.trig_alarm_events)) ok2Run = false
            break
        default:
            ok2Run = false
    }
    if(ok2Run) {
        if(getConfStatusItem("tiers")) {
            processTierTrigEvt(evt, true)
        } else { executeAction(evt, false, "alarmEvtHandler(${evt?.name})", false, false) }
    } else {
        logDebug("alarmEvtHandler | Skipping event $eN  value: $eV,  did not match ${settings.trig_alarm}")
    }
}

public guardEventHandler(String guardState) {
//    state.alexaGuardState = guardState
    def evt = [name: "guard", displayName: "Alexa Guard", value: guardState, date: new Date(), deviceId: null]
    logTrace( "${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)})")
    if((Boolean)state.handleGuardEvents && settings.trig_guard && (sANY in (List)settings.trig_guard || guardState in (List)settings.trig_guard)) {
//        if((Boolean)state.handleGuardEvents) {
            // Boolean dco = (settings.trig_guard_once == true)
            // Integer dcw = settings.trig_guard_wait ?: null
            // Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)
            // if(!evtWaitOk) { return }
            executeAction(evt, false, "guardEventHandler", false, false)
//        }
    }
}

def eventCompletion(evt, String dId, Boolean dco, Integer dcw, String meth, evtVal, String evtDis) {
    Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk([date: evt?.date, deviceId: dId, value: evtVal, name: evt?.name, displayName: evtDis], dco, dcw) : true)
    if(!evtWaitOk) { return }
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, true)
    } else { executeAction(evt, false, meth, false, false) }
}

def webcoreEvtHandler(evt) {
    String disN = evt?.jsonData?.name
    String pId = evt?.jsonData?.id
    logTrace( "${evt?.name?.toUpperCase()} Event | Piston: ${disN} | pistonId: ${pId} | with a delay of ${now() - evt?.date?.getTime()}ms")
    if(pId in settings.trig_pistonExecuted) {
        Boolean dco = (settings.trig_pistonExecuted_once == true)
        Integer dcw = settings.trig_pistonExecuted_wait ?: null
        eventCompletion(evt, "pistonExecuted", dco, dcw, "webcoreEvtHandler", disN, disN)
/*        Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk([date: evt?.date, deviceId: "pistonExecuted", value: disN, name: evt?.name, displayName: disN], dco, dcw) : true)
        if(!evtWaitOk) { return }
        if(getConfStatusItem("tiers")) {
            processTierTrigEvt(evt, true)
        } else { executeAction(evt, false, "webcoreEvtHandler", false, false) } */
    }
}

def sceneEvtHandler(evt) {
    logTrace( "${evt?.name?.toUpperCase()} Event | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    Boolean dco = (settings.trig_scene_once == true)
    Integer dcw = settings.trig_scene_wait ?: null
    eventCompletion(evt, "scene", dco, dcw, "sceneEvtHandler", evt?.value, (String)evt?.displayName)
/*    Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk([date: evt?.date, deviceId: "scene", value: evt?.value, name: evt?.name, displayName: evt?.displayName], dco, dcw) : true)
    if(!evtWaitOk) { return }
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, true)
    } else { executeAction(evt, false, "sceneEvtHandler", false, false) } */
}

def modeEvtHandler(evt) {
    logTrace("${evt?.name?.toUpperCase()} Event | Mode: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    if(evt?.value in settings.trig_mode) {
        Boolean dco = (settings.trig_mode_once == true)
        Integer dcw = settings.trig_mode_wait ?: null
        eventCompletion(evt, sMODE, dco, dcw, "modeEvtHandler", evt?.value, (String)evt?.displayName)
/*        Boolean evtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk([date: evt?.date, deviceId: sMODE, value: evt?.value, name: evt?.name, displayName: evt?.displayName], dco, dcw) : true)
        if(!evtWaitOk) { return }
        if(getConfStatusItem("tiers")) {
            processTierTrigEvt(evt, true)
        } else { executeAction(evt, false, "modeEvtHandler", false, false) } */
    }
}

Integer getLastAfterEvtCheck() { return getLastTsValSecs("lastAfterEvtCheck") }

void afterEvtCheckWatcher() {
    //ERS
    Boolean aa = getTheLock(sHMLF, "afterEvtCheckWatcher")
    // log.trace "lock wait: ${aa}"

    Map aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if(!aEvtMap) aEvtMap = (Map)state.afterEvtMap ?: [:]

    Map aSchedMap = (Map)getMemStoreItem("afterEvtChkSchedMap", null)
    if(!aSchedMap) aSchedMap = (Map)state.afterEvtChkSchedMap ?: null

    if((aEvtMap.size() == 0 && aSchedMap?.id) || (aEvtMap.size() && getLastAfterEvtCheck() > 240)) {
        runIn(2, "afterEvtCheckHandler")
    }
    releaseTheLock(sHMLF)
}

void devAfterThermEvtHandler(evt) {
    String a = evt.name
    if(a == "temperature") a = "thermostatTemperature"
    evt.name = a
    devAfterEvtHandler(evt)
}

void devAfterEvtHandler(evt) {
    Long evtDelay = now() - evt?.date?.getTime()
    String evntName = evt?.name
    String dc = settings."trig_${evntName}_cmd" ?: null
    Integer dcaf = settings."trig_${evntName}_after" ?: null
    Integer dcafr = settings."trig_${evntName}_after_repeat" ?: null
    Integer dcafrc = settings."trig_${evntName}_after_repeat_cnt" ?: null
    String eid = "${evt?.deviceId}_${evntName}"
    Boolean schedChk = (dc && dcaf && evt?.value == dc)
    logTrace( "Device Event | ${evntName?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms | SchedCheck: (${schedChk})")
    Boolean rem = false

    //ERS
    Boolean aa = getTheLock(sHMLF, "scheduleTrigEvt")
    // log.trace "lock wait: ${aa}"
    Map aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if(!aEvtMap) aEvtMap = (Map)state.afterEvtMap ?: [:]

    if(aEvtMap.containsKey(eid) && dcaf && !schedChk) {
        aEvtMap.remove(eid)
        rem = true
    }
    Boolean ok = schedChk
    if(ok) { aEvtMap[eid] = [
            dt: evt?.date?.toString(),
            deviceId: evt?.deviceId as String,
            displayName: evt?.displayName,
            name: evt?.name,
            value: evt?.value,
            type: evt?.type,
            data: evt?.data,
            triggerState: dc,
            wait: dcaf ?: null,
            timeLeft: dcaf ?: null,
            isRepeat: false,
            repeatWait: dcafr ?: null,
            repeatCnt: 0,
            repeatCntMax: dcafrc ]
    }
    state.afterEvtMap = aEvtMap
    updMemStoreItem("afterEvtMap", aEvtMap)
    releaseTheLock(sHMLF)

    if(rem) logDebug("Removing ${evt?.displayName} from AfterEvtCheckMap | Reason: (${evt?.name?.toUpperCase()}) no longer has the state of (${dc}) | Remaining Items: (${aEvtMap?.size()})")

    if(ok) {
        runIn(2, "afterEvtCheckHandler")
        logTrace( "Scheduled afterEvent in 2 seconds")
        if(!(Boolean)state.afterEvtCheckWatcherSched) {
            state.afterEvtCheckWatcherSched = true
            runEvery5Minutes("afterEvtCheckWatcher")
        }
    }
}

void afterEvtCheckHandler() {
    //ERS
    Boolean aa = getTheLock(sHMLF, "afterEvtCheckHandler")
    // log.trace "lock wait: ${aa}"
    Map<String,Map> aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if(!aEvtMap) aEvtMap = (Map)state.afterEvtMap ?: [:]
    Boolean hasLock = true

    if(aEvtMap.size()) {
        // Collects all of the evt items and stores their wait values as a list
        Integer timeLeft = null
        Integer lowWait = aEvtMap.findAll {it -> it?.value?.wait != null }?.collect { it?.value?.wait }?.min()
        Integer lowLeft = aEvtMap.findAll {it -> it?.value?.wait == lowWait }?.collect { it?.value?.timeLeft} ?.min()
        def nextItem = aEvtMap.find {it -> it?.value?.wait == lowWait && (!it?.value?.timeLeft || it?.value?.timeLeft == lowLeft) }
        //ERS TODO
        //log.debug "nextItem: $nextItem"
        Map nextVal = (Map)nextItem?.value ?: null
        //log.debug "nextVal: $nextVal"
        String nextId = (nextVal?.deviceId && nextVal?.name) ? "${nextVal.deviceId}_${nextVal.name}" : sNULL
        //log.debug "nextId: $nextId    key: ${nextItem?.key}"
        if(nextVal && nextId && aEvtMap[nextId]) {
            Date fullDt = parseDate((String)nextVal.dt)
            Date prevDt = (Boolean)nextVal.isRepeat && nextVal.repeatDt ? parseDate((String)nextVal.repeatDt) : fullDt
            Integer repeatCnt = (nextVal.repeatCnt >= 0) ? nextVal.repeatCnt + 1 : 1
            Integer repeatCntMax = (Integer)nextVal.repeatCntMax ?: null
            Boolean isRepeat = (Boolean)nextVal.isRepeat ?: false
            Boolean hasRepeat = (settings."trig_${nextVal.name}_after_repeat" != null)
            if(prevDt) {
                Long timeNow = new Date().getTime()
                Integer evtElap = Math.round((timeNow - prevDt.getTime())/1000L).toInteger()
                Integer fullElap = Math.round((timeNow - fullDt.getTime())/1000L).toInteger()
                Integer reqDur = ((Boolean)nextVal.isRepeat && nextVal.repeatWait) ? (Integer)nextVal.repeatWait : (Integer)nextVal.wait ?: null
                timeLeft = (reqDur - evtElap)
                aEvtMap[nextId].timeLeft = timeLeft
                aEvtMap[nextId].repeatCnt = repeatCnt

                updMemStoreItem("afterEvtMap", aEvtMap)
                state.afterEvtMap = aEvtMap

                logDebug("afterEvtCheckHandler  | TimeLeft: ${timeLeft}(<=1 ${(timeLeft <= 1)}) | LastCheck: ${evtElap} | EvtDuration: ${fullElap} | RequiredDur: ${reqDur} | AfterWait: ${nextVal?.wait} | RepeatWait: ${nextVal?.repeatWait} | isRepeat: ${nextVal?.isRepeat} | RepeatCnt: ${repeatCnt} | RepeatCntMax: ${repeatCntMax}")
                if(timeLeft <= 1 && nextVal.deviceId && nextVal.name) {
                    timeLeft = reqDur
                    // log.debug "reqDur: $reqDur | evtElap: ${evtElap} | timeLeft: $timeLeft"
                    def devs = settings."trig_${nextVal.name}" ?: null
                    Boolean skipEvt = (nextVal.triggerState && nextVal.deviceId && nextVal.name && devs) ?
                            !devCapValEqual(devs, nextVal.deviceId as String, (String)nextVal.name, nextVal.triggerState) : true
                    Boolean skipEvtCnt = (repeatCntMax && (repeatCnt > repeatCntMax))
                    aEvtMap[nextId]?.timeLeft = timeLeft
                    if(!skipEvt && !skipEvtCnt) {
                        if(hasRepeat) {
                            // log.warn "Last Repeat ${nextVal?.displayName?.toString()?.capitalize()} (${nextVal?.name}) Event | TimeLeft: ${timeLeft} | LastCheck: ${evtElap} | EvtDuration: ${fullElap} | Required: ${reqDur}"
                            aEvtMap[nextId].repeatDt = formatDt(new Date())
                            aEvtMap[nextId].isRepeat = true
                            updMemStoreItem("afterEvtMap", aEvtMap)
                            releaseTheLock(sHMLF)
                            state.afterEvtMap = aEvtMap
                            hasLock = false
                            Map<String,Object> tt=[date: parseDate(nextVal?.repeatDt?.toString()), deviceId: nextVal?.deviceId as String, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value, type: nextVal?.type, data: nextVal?.data, totalDur: fullElap]
                            deviceEvtHandler(tt, true, isRepeat)
                        } else {
                            aEvtMap.remove(nextId)
                            updMemStoreItem("afterEvtMap", aEvtMap)
                            releaseTheLock(sHMLF)
                            state.afterEvtMap = aEvtMap
                            hasLock = false
                            logDebug("Wait Threshold (${reqDur} sec) Reached for ${nextVal?.displayName} (${nextVal?.name?.toString()?.capitalize()}) | Issuing held event | TriggerState: (${nextVal?.triggerState}) | EvtDuration: ${fullElap}")
                            Map<String,Object> tt = [date: parseDate(nextVal?.dt?.toString()), deviceId: nextVal?.deviceId as String, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value, type: nextVal?.type, data: nextVal?.data]
                            deviceEvtHandler(tt, true)
                        }
                    } else {
                        aEvtMap.remove(nextId)
                        updMemStoreItem("afterEvtMap", aEvtMap)
                        if(!skipEvt && skipEvtCnt) {
                            logDebug("${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) has repeated ${repeatCntMax} times | Skipping Action Repeat...")
                        } else {
                            logDebug("${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) state is already ${nextVal?.triggerState} | Skipping Action...")
                        }
                    }
                }
            }
        }
        // log.debug "nextId: $nextId | timeLeft: ${timeLeft}"
        if(hasLock) releaseTheLock(sHMLF)
        runIn(1, "scheduleAfterCheck", [data: [val: timeLeft, id: nextId, repeat: isRepeat]])
        logTrace( "afterEvtCheckHandler scheduleAfterCheck in 1 second")
        // logTrace( "afterEvtCheckHandler Remaining Items: (${aEvtMap?.size()})")
    } else {
        releaseTheLock(sHMLF)
        clearAfterCheckSchedule()
    }
    updTsVal("lastAfterEvtCheck")
}

def thermostatEvtHandler(evt) {
    String a = evt.name
    if(a == "temperature") a = "thermostatTemperature"
    evt.name = a
    deviceEvtHandler(evt)
/*
    Long evtDelay = now() - ((Date)evt?.date)?.getTime()
    Boolean evtOk = false
    Boolean evtAd = false
    String a = evt.name
    if(a == "temperature") a = "thermostatTemperature"
    evt.name = a
    List d = settings."trig_${a}"
    String dc = settings."trig_${a}_cmd"
    Boolean dco = settings."trig_${a}_once"
    Integer dcw = settings."trig_${a}_wait" ? settings."trig_${a}_wait" : null
    logTrace( "${a} Event | ${a.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
//    log.debug "d: $d | dc: $dc | dco: $dco | dcw: $dcw"
    Boolean devEvtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)
    if(d?.size() && dc) {
        switch(a) {
            case "coolingSetpoint":
            case "heatingSetpoint":
            case "thermostatTemperature":
                String dsc = settings."trig_${a}_cmd" ?: sNULL
                Double dcl = settings."trig_${a}_low"
                Double dch = settings."trig_${a}_high"
                Double dce = settings."trig_${a}_equal"
                Map valChk = deviceEvtProcNumValue(evt, d, dsc, dcl, dch, dce, null)
                evtOk = valChk.evtOk
                evtAd = valChk.evtAd
                break

            case "thermostatFanMode":
                String dfc = settings."trig_${a}_cmd" ?: sNULL
                if(dfc == sANY || evt.value == dfc) { evtOk=true }
                break

            case "thermostatOperatingState":
                String doc = settings."trig_${a}_cmd" ?: sNULL
                if(doc == sANY || evt.value == doc) { evtOk=true }
                break

            case "thermostatMode":
                String dmc = settings."trig_${a}_cmd" ?: sNULL
                if(dmc ==sANY || evt.value == dmc) { evtOk=true }
                break
        }
    }
    Boolean execOk = (evtOk && devEvtWaitOk)
    // log.debug "execOk: $execOk"
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, execOk)
    } else if (execOk) { executeAction(evt, false, "thermostatEvtHandler(${a})", false, evtAd) } */
}

def deviceEvtHandler(evt, Boolean aftEvt=false, Boolean aftRepEvt=false) {
    Long evtDelay = now() - (Long)((Date)evt.date).getTime()
    Boolean evtOk = false
    Boolean evtAd = false
    String evntName = (String)evt.name
    List d = settings."trig_${evntName}"
    String dc = settings."trig_${evntName}_cmd"
    Boolean dca = (settings."trig_${evntName}_all" == true)
    Boolean dcavg = (!dca && settings."trig_${evntName}_avg" == true)
    Boolean dco = (!settings."trig_${evntName}_after" && settings."trig_${evntName}_once" == true)
    Integer dcw = (!settings."trig_${evntName}_after" && settings."trig_${evntName}_wait") ? settings."trig_${evntName}_wait" : null
    Boolean devEvtWaitOk = ((dco || dcw) ? evtWaitRestrictionOk(evt, dco, dcw) : true)
    String extra = sBLANK
    switch(evntName) {
        case sSWITCH:
        case "lock":
        case "securityKeypad":
        case "door":
        case "smoke":
        case "carbonMonoxide":
        case "windowShade":
        case "presence":
        case "contact":
        case "acceleration":
        case "motion":
        case "water":
        case "valve":
        case "button":
        case "thermostatFanMode":
        case "thermostatOperatingState":
        case "thermostatMode":
            if(d?.size() && dc) {
                if(dc == sANY) { evtOk = true }
                else {
                    if(dca && (allDevCapValsEqual(d, dc, evt?.value))) { evtOk = true; evtAd = true }
                    else if(evt?.value == dc) { evtOk=true }
                }
            }
            if(evtOk && evntName in ["lock", "securityKeypad"] && (String)evt.value in ["disarmed", "unlocked"]) {
                List dcn = settings."trig_${evntName}_Codes"
                if(dcn) {
                    String theCode = evt?.data
                    Map data
                    if(theCode) {
                        data = getCodes(null, theCode)
//                        if(theCode[0] == "{") data = parseJson(theCode)
//                        else data = parseJson(decrypt(theCode))
                    }
//                    log.debug "converted $theCode to ${data}  starting with ${evt?.data}"
                    List<String> theCList = data?.collect { (String)it.key }
//                    log.debug "found code $theCList"
                    if(!theCList || !(theCList[0] in dcn)) {
                        evtOk = false
                        extra = " FILTER REMOVED the received code (${theCode}), code did not match trig_${evntName}_Codes (${dcn})"
                    }
                }
            }
            break
        case "pushed":
        case "released":
        case "held":
        case "doubleTapped":
            def dcn = settings."trig_${evntName}_nums"
            if(d?.size() && dc && dcn && dcn?.size() > 0) {
                if(dcn?.contains(evt?.value)) { evtOk = true }
            }
//log.debug "deviceEvtHandler: dcn: $dcn | d: $d | dc: $dc | dco: $dco | dcw: $dcw | evt.value: ${evt?.value}"
            break
        case "coolingSetpoint":
        case "heatingSetpoint":
        case "thermostatTemperature":
        case "humidity":
        case "temperature":
        case "power":
        case "illuminance":
        case "level":
        case "battery":
            Double dcl = settings."trig_${evntName}_low"
            Double dch = settings."trig_${evntName}_high"
            Double dce = settings."trig_${evntName}_equal"
            Map valChk = deviceEvtProcNumValue(evt, d, dc, dcl, dch, dce, dca, dcavg)
            evtOk = valChk.evtOk
            evtAd = valChk.evtAd
            break
    }
    Boolean execOk = (evtOk && devEvtWaitOk)
    logTrace("Device Event | ${evntName.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms${aftEvt ? " | (aftEvt)" : sBLANK} ${extra ?: sBLANK}")
    logDebug("deviceEvtHandler | execOk: ${execOk} | evtOk :${evtOk} | devEvtWaitOk: ${devEvtWaitOk} | evtAd: $evtAd | aftRepEvt: ${aftRepEvt}${extra}")
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, execOk)
    } else if (execOk) { executeAction(evt, false, "deviceEvtHandler(${evntName})", evtAd, aftRepEvt) }
}

private processTierTrigEvt(evt, Boolean evtOk) {
    logDebug("processTierTrigEvt | Name: ${evt?.name} | Value: ${evt?.value} | EvtOk: ${evtOk}")
    //ERS
    Boolean aa = getTheLock(sHMLF, "processTierTrigEvt")
    // log.trace "lock wait: ${aa}"
    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = state.actTierState ?: [:]

    if (evtOk) {
        if(aTierSt.size()) {
            releaseTheLock(sHMLF)
            logDebug("processTierTrigEvt  found tier state | Name: ${evt?.name} | Value: ${evt?.value} | EvtOk: ${evtOk}")
            return
        }
        releaseTheLock(sHMLF)
        tierEvtHandler(evt)

    } else if(!evtOk && settings.act_tier_stop_on_clear == true) {
        //def tierConf = atomicState.actTierState?.evt
        def tierConf = aTierSt.evt
        if(tierConf?.size() && tierConf?.name == evt?.name && tierConf?.deviceId == evt?.deviceId) {
            updMemStoreItem("actTierState", [:])
            state.actTierState = [:]
//            atomicState.actTierState = [:]
            releaseTheLock(sHMLF)
            logDebug("Tier Trigger no longer valid... Clearing TierState and Schedule...")
            unschedule("tierEvtHandler")
            atomicState.tierSchedActive = false
            updTsVal("lastTierRespStopDt")
        }
    } else {
        releaseTheLock(sHMLF)
        logDebug("processTierTrigEvt no action | Name: ${evt?.name} | Value: ${evt?.value} | EvtOk: ${evtOk}")
    }
}

def getTierStatusSection() {
    String str = sBLANK
    if(isTierAction()) {
        //ERS
        Map lTierMap = getTierMap()
        Boolean tsa = (Boolean)atomicState.tierSchedActive == true

        Boolean aa = getTheLock(sHMLF, "processTierTrigEvt")
        // log.trace "lock wait: ${aa}"
        Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
        if(!aTierSt) aTierSt = state.actTierState ?: [:]

//        Map tS = atomicState.actTierState
        Map tS = aTierSt
        str += "Tier Size: ${lTierMap?.size()}\n"
        str += "Schedule Active: ${tsa}\n"
        str += tS?.cycle ? "Tier Cycle: ${tS?.cycle}\n" : sBLANK
        str += tS?.schedDelay ? "Next Delay: ${tS?.schedDelay}\n" : sBLANK
        str += tS?.lastMsg ? "Is Last Cycle: ${tS?.lastMsg == true}\n" : sBLANK

        releaseTheLock(sHMLF)

        str += getTsVal("lastTierRespStartDt") ? "Last Tier Start: ${getTsVal("lastTierRespStartDt")}\n" : sBLANK
        str += getTsVal("lastTierRespStopDt") ? "Last Tier Stop: ${getTsVal("lastTierRespStopDt")}\n" : sBLANK
        section("Tier Response Status: ") {
            paragraph pTS(str, sNULL, false, sCLR4D9)
        }
    }
}

private void resumeTierJobs() {
    //ERS
    Boolean aa = getTheLock(sHMLF, "processTierTrigEvt")
    // log.trace "lock wait: ${aa}"
    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = state.actTierState ?: [:]

    //if(atomicState.actTierState?.size() && (Boolean)atomicState.tierSchedActive) {
    if(aTierSt?.size()) {
        releaseTheLock(sHMLF)
        Boolean tsa = (Boolean)atomicState.tierSchedActive == true
        if(tsa) {
            tierEvtHandler()
        }
        return
    }
    releaseTheLock(sHMLF)
}

private tierEvtHandler(evt=null) {
    if(!evt) {
        Date adate = new Date()
        String dt = dateTimeFmt(adate, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        evt = [name: "Tiered Schedule", displayName: "Scheduled Tiered Trigger", value: fmtTime(dt), date: adate, deviceId: null]
    }
    Map t0 = getTierMap()
    Map tierMap = t0 ?: [:]
    //ERS
    Boolean aa = getTheLock(sHMLF, "processTierTrigEvt")
    // log.trace "lock wait: ${aa}"
    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = state.actTierState ?: [:]

//    t0 = atomicState.actTierState
    Map tierState = aTierSt
    // TODO
    log.debug "tierState: ${tierState}"
    log.debug "tierMap: ${tierMap}"
    if(tierMap.size()) {
        Map newEvt = tierState.evt ?: [name: evt?.name, displayName: evt?.displayName, value: evt?.value, unit: evt?.unit, deviceId: evt?.deviceId, date: evt?.date]
        Integer curPass = (tierState.cycle && tierState.cycle.toString()?.isNumber()) ? tierState.cycle.toInteger()+1 : 1
        if(curPass <= tierMap.size()) {
            tierState.cycle = curPass
            tierState.schedDelay = tierMap[curPass]?.delay ?: null
            tierState.message = tierMap[curPass]?.message ?: null
            tierState.volume = [:]
            if(tierMap[curPass]?.volume?.change) tierState.volume.change = tierMap[curPass]?.volume?.change ?: null
            if(tierMap[curPass]?.volume?.restore) tierState.volume.restore = tierMap[curPass]?.volume?.restore ?: null
            tierState.evt = newEvt
            tierState.lastMsg = (curPass+1 > tierMap.size())

            logTrace("tierSize: (${tierMap.size()}) | cycle: ${tierState.cycle} | curPass: (${curPass}) | nextPass: ${curPass+1} | schedDelay: (${tierState.schedDelay}) | Message: (${tierState.message}) | LastMsg: (${tierState.lastMsg})")

            updMemStoreItem("actTierState", tierState)
            state.actTierState = tierState
//            atomicState.actTierState = tierState
            releaseTheLock(sHMLF)

            if(curPass == 1) { updTsVal("lastTierRespStartDt"); remTsVal("lastTierRespStopDt") }

            tierSchedHandler([sched: true, tierState: tierState])
        } else {
            logDebug("Tier Cycle has completed... Clearing TierState...")
            //atomicState.actTierState = [:]
            updMemStoreItem("actTierState", [:])
            state.actTierState = [:]
            releaseTheLock(sHMLF)
            atomicState.tierSchedActive = false
            updTsVal("lastTierRespStopDt")
        }
    } else releaseTheLock(sHMLF)
}

private void tierSchedHandler(data) {
    if(data && data.tierState?.size() && data.tierState?.message) {
        // log.debug "tierSchedHandler(${data})"
        Map evt = data.tierState.evt
        executeAction(evt, false, "tierSchedHandler", false, false, [msg: data?.tierState?.message as String, volume: data?.tierState?.volume, isFirst: (data?.tierState?.cycle == 1), isLast: (data?.tierState?.lastMsg == true)])
        if(data?.sched) {
            if(data.tierState.schedDelay && data.tierState.lastMsg == false) {
                logDebug("Scheduling Next Tier Message for (${data.tierState?.schedDelay} seconds)")
                runIn(data.tierState.schedDelay, "tierEvtHandler")
            } else {
                logDebug("Scheduling cleanup for (5 seconds) as this was the last message")
                runIn(5, "tierEvtHandler")
            }
            //ERS
            atomicState.tierSchedActive = true
        }
    }
}

Map deviceEvtProcNumValue(evt, List devs=null, String cmd=sNULL, Double dcl=null, Double dch=null, Double dce=null, Boolean dca=false, Boolean dcavg=false) {
    Boolean evtOk = false
    Boolean evtAd = false
    log.debug "deviceEvtProcNumValue | cmd: ${cmd} | low: ${dcl} | high: ${dch} | equal: ${dce} | all: ${dca}"
    if(devs?.size() && cmd && evt?.value?.toString()?.isNumber()) {
        Double evtValue = (dcavg ? getDevValueAvg(devs, evt?.name) : evt?.value) as Double
        log.debug "evtValue: ${evtValue}"
        switch(cmd) {
            case sEQUALS:
                if(!dca && dce && dce?.toDouble() == evtValue) {
                    evtOk=true
                } else if(dca && dce && allDevCapNumValsEqual(devs, evt?.name, dce)) { evtOk=true; evtAd=true }
                break
            case sBETWEEN:
                if(!dca && dcl && dch && (evtValue in (dcl..dch))) {
                    evtOk=true
                } else if(dca && dcl && dch && allDevCapNumValsBetween(devs, evt?.name, dcl, dch)) { evtOk=true; evtAd=true }
                break
            case sABOVE:
                if(!dca && dch && (evtValue > dch)) {
                    evtOk=true
                } else if(dca && dch && allDevCapNumValsAbove(devs, evt?.name, dch)) { evtOk=true; evtAd=true }
                break
            case sBELOW:
                if(dcl && (evtValue < dcl)) {
                    evtOk=true
                } else if(dca && dcl && allDevCapNumValsBelow(devs, evt?.name, dcl)) { evtOk=true; evtAd=true }
                break
        }
    }
    return [evtOk: evtOk, evtAd: evtAd]
}

static String evtValueCleanup(val) {
    // log.debug "val(in): ${val}"
    val = (val?.toString()?.isNumber() && val?.toString()?.endsWith(".0")) ? val?.toDouble()?.round(0) : val
    // log.debug "val(out): ${val}"
    return val?.toString()
}

private clearEvtHistory() {
    settingUpdate("clrEvtHistory", sFALSE, sBOOL)
    //ERS
    Boolean aa = getTheLock(sHMLF, "clearEvtHistory")
    // log.trace "lock wait: ${aa}"
    updMemStoreItem("valEvtHistory", [:])
    state.valEvtHistory = [:]
    releaseTheLock(sHMLF)
}

Boolean evtWaitRestrictionOk(evt, Boolean once, Integer wait) {
    Boolean ok = true
    //ERS
    Long dur
    Boolean waitOk
    Boolean dayOk
    String n = (String)evt?.name
    String msg = "Last ${n?.capitalize()} Event for Device"
    Date evtDt = (Date)evt?.date // parseDate(evt?.date?.toString())

    Boolean aa = getTheLock(sHMLF, "evtWaitRestrictionOk")
    // log.trace "lock wait: ${aa}"
//    Map t0 = atomicState.valEvtHistory
//    Map evtHistMap = t0 ?: [:]
    Map evtHistMap = (Map)getMemStoreItem("valEvtHistory", [:])
    if(!evtHistMap) evtHistMap = state.valEvtHistory ?: [:]
    // log.debug "prevDt: ${evtHistMap[n]?.dt ? parseDate(evtHistMap[n]?.dt as String) : null} | evtDt: ${evtDt}"
    if(evtHistMap.containsKey(n) && evtHistMap[n]?.dt) {
        Date prevDt = parseDate(evtHistMap[n].dt)
        // log.debug "prevDt: ${prevDt.toString()}"
        if(prevDt && evtDt) {
            dur = Math.round(((Long)evtDt.getTime() - (Long)prevDt.getTime())/1000.0D)
            waitOk = ( (wait && dur) && (wait < dur))
            dayOk = !once || (once && !isDateToday(prevDt))
            ok = (waitOk && dayOk)
            msg += " Occurred: (${dur} sec ago) | Desired Wait: (${wait} sec) - ($waitOk && $dayOk)"
        }
    } else msg = "No "+msg
    if(ok) {
        evtHistMap[n] = [dt: evt?.date?.toString(), value: evt?.value, name: n]
        updMemStoreItem("valEvtHistory", evtHistMap)
        state.valEvtHistory = evtHistMap
    }
    // log.debug "evtWaitRestrictionOk: $ok"
    releaseTheLock(sHMLF)
    msg += " Status: (${ok ? okSymFLD : notOkSymFLD}) | OnceDaily: (${once})"
    logDebug(msg)
    return ok
}

static String getAttrPostfix(String attr) {
    switch(attr) {
        case "humidity":
        case "level":
        case "battery":
            return " percent"
        case "thermostatTemperature":
        case "temperature":
            return " degrees"
        case "illuminance":
            return " lux"
        case "power":
            return " watts"
        default:
            return sBLANK
    }
}

def scheduleAfterCheck(data) {
    Integer val = data?.val ? (data.val < 2 ? 2 : data.val-4) : 60
    String id = data?.id?.toString() ?: null
    Boolean rep = (data?.repeat == true)
    //ERS
    Boolean aa = getTheLock(sHMLF, "scheduleafterCheck")
    // log.trace "lock wait: ${aa}"
    Map aSchedMap = (Map)getMemStoreItem("afterEvtChkSchedMap", null)
    if(!aSchedMap) aSchedMap = (Map)state.afterEvtChkSchedMap ?: null
//    Map t0 = atomicState.afterEvtChkSchedMap
//    Map aSchedMap = t0 ?: null
    if(aSchedMap && aSchedMap?.id?.toString() && id && aSchedMap?.id?.toString() == id) {
        // log.debug "Active Schedule Id (${aSchedMap?.id}) is the same as the requested schedule ${id}."
    }
    runIn(val, "afterEvtCheckHandler")
    //atomicState.afterEvtChkSchedMap = [id: id, dur: val, dt: getDtNow()]
    Map a = [id: id, dur: val, dt: getDtNow()]
    state.afterEvtChkSchedMap = a
    updMemStoreItem("afterEvtChkSchedMap", a)
    releaseTheLock(sHMLF)
    logDebug("Schedule After Event Check${rep ? " (Repeat)" : sBLANK} in (${val} seconds) | Id: ${id}")
}

private clearAfterCheckSchedule() {
    unschedule("afterEvtCheckHandler")
    state.afterEvtCheckWatcherSched = false
    logDebug("Clearing After Event Check Schedule...")
    //ERS
    Boolean aa = getTheLock(sHMLF, "clearAfterCheckSchedule")
    // log.trace "lock wait: ${aa}"
    Map aSchedMap = (Map)getMemStoreItem("afterEvtChkSchedMap", null)
    if(!aSchedMap) aSchedMap = (Map)state.afterEvtChkSchedMap ?: null
    updMemStoreItem("afterEvtChkSchedMap", null)
//    atomicState.afterEvtChkSchedMap = null
    state.afterEvtChkSchedMap = null
    releaseTheLock(sHMLF)
}

/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
Boolean reqAllCond() {
    Boolean mult = multipleConditions()
    return ( !mult || (mult && (Boolean)settings.cond_require_all))
}

Boolean timeCondOk() {
    Date startTime
    Date stopTime
    Date now = new Date()
    String startType = settings.cond_time_start_type
    String stopType = settings.cond_time_stop_type
    if(startType && stopType) {
        startTime = startType == 'time' && settings.cond_time_start ? toDateTime(settings.cond_time_start) : null
        stopTime = stopType == 'time' && settings.cond_time_stop ? toDateTime(settings.cond_time_stop) : null

        if(startType in lSUNRISESET || stopType in lSUNRISESET) {
            def sun = getSunriseAndSunset()
            Long lsunset = sun.sunset.time
            Long lsunrise = sun.sunrise.time
            Long startoffset = settings.cond_time_start_offset ? settings.cond_time_start_offset*1000L : 0L
            Long stopoffset = settings.cond_time_stop_offset ? settings.cond_time_stop_offset*1000L : 0L
            if(startType in lSUNRISESET) {
                Long startl = (startType == 'sunrise' ? lsunrise : lsunset) + startoffset
                startTime = new Date(startl)
            }
            if(stopType in lSUNRISESET) {
                Long stopl = (stopType == 'sunrise' ? lsunrise : lsunset) + stopoffset
                stopTime = new Date(stopl)
            }
        }

        if(startTime && stopTime) {
            Boolean not = startTime.getTime() > stopTime.getTime()
            Boolean isBtwn = timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, location?.timeZone)
            isBtwn = not ? !isBtwn : isBtwn
            state.startTime = formatDt(startTime)
            state.stopTime = formatDt(stopTime)
            logTrace("TimeCheck ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not? startTime:stopTime})")
            return isBtwn
        }
    }
    logTrace("TimeCheck | (null)")
    state.startTime = sNULL
    state.stopTime = sNULL
    return null
}

Boolean dateCondOk() {
    Boolean result = null
    Boolean dOk
    Boolean mOk
    if(!(settings.cond_days == null && settings.cond_months == null)) {
        Boolean reqAll = reqAllCond()
        dOk = settings.cond_days ? (isDayOfWeek(settings.cond_days)) : reqAll // true
        mOk = settings.cond_months ? (isMonthOfYear(settings.cond_months)) : reqAll //true
        result = reqAll ? (mOk && dOk) : (mOk || dOk)
    }
//    logTrace("dateConditions | $result | monthOk: $mOk | daysOk: $dOk")
    return result
}

Boolean locationCondOk() {
    Boolean result = null
    Boolean mOk
    Boolean aOk
    if(!(settings.cond_mode == null && settings.cond_mode_cmd == null && settings.cond_alarm == null)) {
        Boolean reqAll = reqAllCond()
        mOk = (settings.cond_mode /*&& settings.cond_mode_cmd*/) ? (isInMode(settings.cond_mode, (settings.cond_mode_cmd == "not"))) : reqAll //true
        aOk = settings.cond_alarm ? isInAlarmMode(settings.cond_alarm) : reqAll //true
        result = reqAll ? (mOk && aOk) : (mOk || aOk)
    }
//    logTrace("locationConditions | $result | modeOk: $mOk | alarmOk: $aOk")
    return result
}

Boolean checkDeviceCondOk(String type) {
    List devs = settings."cond_${type}" ?: null
    def cmdVal = settings."cond_${type}_cmd" ?: null  // list or string
    Boolean all = (settings."cond_${type}_all" == true)
    if( !(type && devs && cmdVal) ) { return true }
    return all ? allDevCapValsEqual(devs, type, cmdVal) : anyDevCapValsEqual(devs, type, cmdVal)
}

Boolean checkDeviceNumCondOk(String type) {
    List devs = settings."cond_${type}" ?: null
    String cmd = settings."cond_${type}_cmd" ?: null
    Double dcl = settings."cond_${type}_low" ?: null
    Double dch = settings."cond_${type}_high" ?: null
    Double dce = settings."cond_${type}_equal" ?: null
    Boolean dca = (settings."cond_${type}_all" == true) ?: false
    if( !(type && devs && cmd) ) { return true }

    switch(cmd) {
        case sEQUALS:
            if(dce) {
                if(dca) { return allDevCapNumValsEqual(devs, type, dce) }
                else { return anyDevCapNumValEqual(devs, type, dce) }
            }
            break
        case sBETWEEN:
            if(dcl && dch) {
                if(dca) { return allDevCapNumValsBetween(devs, type, dcl, dch) }
                else { return anyDevCapNumValBetween(devs, type, dcl, dch) }
            }
            break
        case sABOVE:
            if(dch) {
                if(dca) { return allDevCapNumValsAbove(devs, type, dch) }
                else { return anyDevCapNumValAbove(devs, type, dch) }
            }
            break
        case sBELOW:
            if(dcl) {
                if(dca) { return allDevCapNumValsBelow(devs, type, dcl) }
                else { return anyDevCapNumValBelow(devs, type, dcl) }
            }
            break
    }
    return true
}

Boolean deviceCondOk() {
    List skipped = []
    List passed = []
    List failed = []
    [sSWITCH, "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "door", "shade", "valve", "water", "thermostatMode", "thermostatOperatingState"  ]?.each { String i->
        if(!settings."cond_${i}") { skipped.push(i); return }
        checkDeviceCondOk(i) ? passed.push(i) : failed.push(i)
    }
    ["temperature", "humidity", "illuminance", "level", "power", "battery", "coolingSetpoint", "heatingSetpoint"]?.each { String i->
        if(!settings."cond_${i}") { skipped.push(i); return }
        checkDeviceNumCondOk(i) ? passed.push(i) : failed.push(i)
    }
    Integer cndSize = (passed.size() + failed.size())
    Boolean result = null
    if(cndSize != 0) result = reqAllCond() ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
//    logTrace("DeviceCondOk | ${result} | Found: (${(passed?.size() + failed?.size())}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return result
}

Map conditionStatus() {
    List failed = []
    List passed = []
    List skipped = []
    [sTIME, "date", "location", "device"]?.each { String i->
        def s = "${i}CondOk"()
        if(s == null) { skipped.push(i); return }
        s ? passed.push(i) : failed.push(i)
    }
    Integer cndSize = (passed.size() + failed.size())
    Boolean reqAll = reqAllCond()
    Boolean ok = reqAll ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
    if(cndSize == 0) ok = true
    logTrace("ConditionsStatus | ok: $ok | RequireAll: ${reqAll} | Found: (${cndSize}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return [ok: ok, passed: passed, blocks: failed]
}

Boolean devCondConfigured(String type) {
    return (settings."cond_${type}" && settings."cond_${type}_cmd")
}

Boolean devNumCondConfigured(String type) {
    return (settings."cond_${type}_cmd" && (settings."cond_${type}_low" || settings."cond_${type}_high" || settings."trig_${type}_equal"))
}

Boolean timeCondConfigured() {
    Boolean startTime = (settings.cond_time_start_type in lSUNRISESET || (settings.cond_time_start_type == sTIME && settings.cond_time_start))
    Boolean stopTime = (settings.cond_time_stop_type in lSUNRISESET || (settings.cond_time_stop_type == sTIME && settings.cond_time_stop))
    return (startTime && stopTime)
}

Boolean dateCondConfigured() {
    Boolean days = (settings.cond_days)
    Boolean months = (settings.cond_months)
    return (days || months)
}

Boolean locationModeConfigured() {
    if(settings.cond_mode && !settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
    return (settings.cond_mode && settings.cond_mode_cmd)
}

Boolean locationAlarmConfigured() {
    return (settings.cond_alarm)
}

Boolean deviceCondConfigured() {
    return (deviceCondCount() > 0)
}

Integer deviceCondCount() {
    List devConds = [sSWITCH, "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "door", "shade", "valve", "temperature", "humidity", "illuminance", "level", "power", "battery", "water", "thermostatMode", "thermostatOperatingState", "coolingSetpoint", "heatingSetpoint", "coolingSetpoint", "heatingSetpoint" ]
    List items = []
    devConds.each { String dc-> if(devCondConfigured(dc)) { items.push(dc) } }
    return items.size()
}

Boolean conditionsConfigured() {
    return (timeCondConfigured() || dateCondConfigured() || locationModeConfigured() || locationAlarmConfigured() || deviceCondConfigured())
}

Boolean multipleConditions() {
    Integer cnt = 0
    if(timeCondConfigured()) cnt++
    if(dateCondConfigured()) cnt++
    if(locationModeConfigured()) cnt++
    if(locationAlarmConfigured()) cnt++
    cnt = cnt + deviceCondCount()
    return (cnt>1)
}

/***********************************************************************************************************
    ACTION EXECUTION
************************************************************************************************************/

private executeActTest() {
    settingUpdate("actTestRun", sFALSE, sBOOL)
    Map evt = [name: "contact", displayName: "some test device", value: "open", date: new Date()]
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, true) // evt was null
    } else {
        if((String)settings.actionType in ["speak", "announcement", "weather", "builtin", "calendar"]) {
            Map aevt = getRandomTrigEvt()
            if(!aevt) log.warn "no random event"
            else evt = aevt
        }
        executeAction(evt, true, "executeActTest", false, false)
    }
}

@Field static final List<String> lONOFF        = ["on", "off"]
@Field static final List<String> lANY          = ["any"]
@Field static final List<String> lOPNCLS       = ["open", "closed"]
@Field static final List<String> lACTINACT     = ["active", "inactive"]
@Field static final List<String> lSUNRISESET   = ["sunrise", "sunset"]

Map getRandomTrigEvt() {
    String trig = getRandomItem((List)settings.triggerEvents)
    List trigItems = settings."trig_${trig}" ?: null
    def randItem = trigItems?.size() ? getRandomItem(trigItems) : null
    def trigItem = randItem ? (randItem instanceof String ? [displayName: null, id: null] :
            (trigItems?.size() ? trigItems?.find { it?.id?.toString() == randItem?.id?.toString() } : [displayName: null, id: null])) : null
    if(devModeFLD) log.debug("trig: ${trig} | trigItem: ${trigItem} | ${trigItem?.displayName} | ${trigItem?.id} | Evt: ${evt}")
    Boolean isC = (getTemperatureScale()=="C")
    Map attVal = [
        sSWITCH: getRandomItem(lONOFF),
        door: getRandomItem(lOPNCLS+["opening", "closing"]),
        contact: getRandomItem(lOPNCLS),
        acceleration: getRandomItem(lACTINACT),
        lock: getRandomItem(["locked", "unlocked", "unlocked with timeout", "unknown"]),
        securityKeypad: getRandomItem(["disarmed", "armed home", "armed away", "unknown"]),
        water: getRandomItem(["wet", "dry"]),
        presence: getRandomItem(["present", "not present"]),
        motion: getRandomItem(lACTINACT),
        valve: getRandomItem(lOPNCLS),
        shade: getRandomItem(lOPNCLS),
        pushed: getRandomItem(["pushed"]),
        released: getRandomItem(["released"]),
        held: getRandomItem(["held"]),
        doubleTapped: getRandomItem(["doubleTapped"]),
        smoke: getRandomItem(["detected", "clear"]),
        carbonMonoxide: getRandomItem(["detected", "clear"]),
        temperature: isC ? getRandomItem(-1..29) : getRandomItem(30..80),
        illuminance: getRandomItem(1..100),
        humidity: getRandomItem(1..100),
        battery: getRandomItem(1..100),
        power: getRandomItem(100..3000),
        mode: getRandomItem(location?.modes),
        alarm: getRandomItem(getAlarmTrigOpts()?.collect {it?.value as String}),
        guard: getRandomItem(["ARMED_AWAY", "ARMED_STAY"]),
        thermostatTemperature: isC ? getRandomItem(10..33) : getRandomItem(50..90),
        coolingSetpoint: isC ? getRandomItem(10..33) : getRandomItem(50..90),
        heatingSetpoint: isC ? getRandomItem(10..33) : getRandomItem(50..90),
        thermostatMode: getRandomItem(getThermModeOpts()),
        thermostatFanMode: getRandomItem(["on", "circulate", "auto"]),
        thermostatOperatingState: getRandomItem(getThermOperStOpts()),
    ]
    if(settings.enableWebCoRE) attVal.pistonExecuted = getRandomItem(getLocationPistons())
    Map evt = [:]
    if(attVal.containsKey(trig)) {
        evt = [name: trig, displayName: trigItem?.displayName ?: sBLANK,
               value: attVal[trig], date: new Date(),
               deviceId: trigItem?.id?.toString() ?: null]
    }
    if(devModeFLD) log.debug "getRandomTrigEvt | trig: ${trig} | Evt: ${evt}"
    return evt
}

static String convEvtType(String type) {
    Map typeConv = [
        "pistonExecuted": "Piston",
        "alarmSystemStatus": "Alarm system",
        "hsmStatus": "Alarm system",
        "hsmAlert": "Alarm system"
    ]
    return (type && typeConv.containsKey(type)) ? typeConv[type] : type
}

String decodeVariables(evt, String str) {
    if(!str) return str
    if(evt) {
        // log.debug "str: ${str} | vars: ${(str =~ /%[a-z]+%/)}"
        if(str.contains("%type%") && (String)evt.name) {
            if(str.contains("%name%")) {
                str = str.replaceAll("%type%", !(String)evt.displayName?.toLowerCase()?.contains((String)evt.name) ? convEvtType((String)evt.name) : sBLANK)
            } else {
                str = str.replaceAll("%type%", convEvtType((String)evt.name))
            }
        }
        str = (str.contains("%name%")) ? str.replaceAll("%name%", (String)evt.displayName) : str

        str = (str.contains("%unit%") && (String)evt.name) ? str.replaceAll("%unit%", getAttrPostfix((String)evt.name)) : str
        str = (str.contains("%value%") && evt.value) ? str.replaceAll("%value%", evt.value?.toString()?.isNumber() ? evtValueCleanup(evt?.value) : evt?.value) : str
        if(!(evt instanceof com.hubitat.hub.domain.Event) && evt.totalDur) {
            Integer mins = durationToMinutes(evt.totalDur)
            str = (str.contains("%duration%")) ? str.replaceAll("%duration%", "${evt.totalDur} second${evt.totalDur > 1 ? "s" : sBLANK} ago") : str
            str = (str.contains("%duration_min%")) ? str.replaceAll("%duration_min%", "${mins} minute${mins > 1 ? "s" : sBLANK} ago") : str
            str = (str.contains("%durationmin%")) ? str.replaceAll("%durationmin%", "${mins} minute${mins > 1 ? "s" : sBLANK} ago") : str
            str = (str.contains("%durationval%")) ? str.replaceAll("%durationval%", "${evt.totalDur} second${evt.totalDur > 1 ? "s" : sBLANK}") : str
            str = (str.contains("%durationvalmin%")) ? str.replaceAll("%durationvalmin%", "${mins} minute${mins > 1 ? "s" : sBLANK}") : str
        }
    }
    Date adate = (Date)evt.date ?: new Date()
    str = (str.contains("%date%")) ? str.replaceAll("%date%", convToDate(adate)) : str
    str = (str.contains("%time%")) ? str.replaceAll("%time%", convToTime(adate)) : str
    str = (str.contains("%datetime%")) ? str.replaceAll("%datetime%", convToDateTime(adate)) : str
    return str
}

static Integer durationToMinutes(dur) {
    if(dur && dur>=60) return (dur/60)?.toInteger()
    return 0 // dur?.toInteger()
}

static Integer durationToHours(dur) {
    if(dur && dur>= (60*60)) return (dur / 60 / 60)?.toInteger()
    return 0 // dur?.toInteger()
}

private List decamelizeStr(String str) {
    return (List) str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
}

String getResponseItem(evt, String tierMsg=sNULL, Boolean evtAd=false, Boolean isRepeat=false, Boolean testMode=false) {
    // log.debug "getResponseItem | EvtName: ${evt?.name} | EvtDisplayName: ${evt?.displayName} | EvtValue: ${evt?.value} | AllDevsResp: ${evtAd} | Repeat: ${isRepeat} | TestMode: ${testMode}"
    String glbText = (String)settings."act_${(String)settings.actionType}_txt" ?: sNULL
    if(glbText) {
        List eTxtItems = glbText.tokenize(";")
        return decodeVariables(evt, getRandomItem(eTxtItems))
    } else if(tierMsg) {
        List eTxtItems = tierMsg?.tokenize(";")
        return decodeVariables(evt, getRandomItem(eTxtItems))
    } else {
        String evntName = evt?.name
        List devs = settings."trig_${evntName}" ?: []
        String dc = (String)settings."trig_${evntName}_cmd"
        Boolean dca = (settings."trig_${evntName}_all" == true)
        String dct = settings."trig_${evntName}_txt" ?: sNULL
        String dcart = settings."trig_${evntName}_after_repeat" && settings."trig_${evntName}_after_repeat_txt" ? settings."trig_${evntName}_after_repeat_txt" : sNULL
        List eTxtItems = dct ? dct.tokenize(";") : []
        List rTxtItems = dcart ? dcart.tokenize(";") : []
        List testItems = eTxtItems + rTxtItems
        if(testMode && testItems.size()) {
            return  decodeVariables(evt, getRandomItem(testItems))
        } else if(!testMode && isRepeat && rTxtItems?.size()) {
            return  decodeVariables(evt, getRandomItem(rTxtItems))
        } else if(!testMode && eTxtItems?.size()) {
            return  decodeVariables(evt, getRandomItem(eTxtItems))
        } else {
            switch(evntName) {
                case sMODE:
                    return  "The location mode is now set to ${evt?.value}"
                case "pistonExecuted":
                    return  "The ${evt?.displayName} piston was just executed!."
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
                case "smoke":
                case "carbonMonoxide":
                    return "${evntName} is ${evt?.value} on ${evt?.displayName}!"
                case "pushed":
                case "held":
                case "released":
                case "doubleTapped":
                    return "Button ${evt?.value} was ${evntName == "doubleTapped" ? "double tapped" : evntName} on ${evt?.displayName}"

                case "thermostatTemperature":
                    evntName = "temperature"
                case "coolingSetpoint":
                case "heatingSetpoint":
                case "thermostatMode":
                case "thermostatFanMode":
                case "thermostatOperatingState":
                default:
                    String t0 = getAttrPostfix(evntName)
                    String postfix = t0 ?: sBLANK
                    if(evtAd && devs?.size()>1) {
                        return "All ${devs?.size()}${!evt?.displayName?.toLowerCase()?.contains(evntName) ? " ${evntName}" : sBLANK} devices are ${evt?.value} ${postfix}"
                    } else {
                        return "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(evntName) ? " ${evntName}" : sBLANK} is ${evt?.value} ${postfix}"
                    }
                    break
            }
        }
    }
    return "Invalid Text Received... Please verify Action configuration..."
}

public getActionHistory(Boolean asObj=false) {
    List eHist = (List)getMemStoreItem("actionHistory")
    List<String> output = []
    if(eHist.size()) {
        eHist.each { Map h->
            String str = sBLANK
            str += "Trigger: [${h?.evtName}]"
            str += "\nDevice: [${h?.evtDevice}]"
            str += "\nCondition Status: ${h?.active ? "Passed" : "Failed"}"
            str += "\nConditions Passed: ${h?.passed}"
            str += "\nConditions Blocks: ${h?.blocks}"
            str += h?.test ? "\nTest Mode: true" : sBLANK
            str += h?.isTier ? "\nTest Cmd: true" : sBLANK
            str += h?.isRepeat ? "\nRepeat: true" : sBLANK
            str += "\nSource: ${h?.src}"
            str += "\nDateTime: ${h?.dt}"
            output.push(str)
        }
    } else { output.push("No History Items Found...") }
    if(!asObj) {
        output.each { String i-> paragraph pTS(i) }
    } else {
        return output
    }
}

private addToActHistory(evt, data, Integer max=10) {
    Boolean ssOk = true // (stateSizePerc() <= 70)

    Boolean aa = getTheLock(sHMLF, "addToActHistory")
    // log.trace "lock wait: ${aa}"

    List eData = (List)getMemStoreItem("actionHistory")
    if(eData == null)eData = []
    eData.push([
        dt: getDtNow(),
        active: (data?.status?.ok == true),
        evtName: evt?.name,
        evtDevice: evt?.displayName,
        blocks: data?.status?.blocks,
        passed: data?.status?.passed,
        test: data?.test,
        isTierCmd: data?.isTier,
        isRepeat: data?.isRepeat,
        src: data?.src
    ])
    Integer lsiz=eData.size()
    if(!ssOk || lsiz > max) { eData = eData.drop( (lsiz-max) ) }
    // log.debug "actionHistory Size: ${eData?.size()}"
    updMemStoreItem("actionHistory", eData)

    releaseTheLock(sHMLF)
}

void clearActHistory(){
    Boolean aa = getTheLock(sHMLF, "clearActHistory")
    // log.trace "lock wait: ${aa}"
    updMemStoreItem("actionHistory", [])

    releaseTheLock(sHMLF)

}

private void executeAction(evt = null, Boolean testMode=false, String src=sNULL, Boolean allDevsResp=false, Boolean isRptAct=false, Map tierData=null) {
    Long startTime = now()
    logTrace( "executeAction${src ? "($src)" : sBLANK}${testMode ? " | [TestMode]" : sBLANK}${allDevsResp ? " | [AllDevsResp]" : sBLANK}${isRptAct ? " | [RepeatEvt]" : sBLANK}")
    if(isPaused(true)) { logWarn("Action is PAUSED... Skipping Action Execution...", true); return }
    Map condStatus = conditionStatus()
    // log.debug "condStatus: ${condStatus}"
    addToActHistory(evt, [status: condStatus, test: testMode, src: src, isRepeat: isRptAct, isTier: (tierData != null)] )
    Map actMap = state.actionExecMap

    List actDevices = settings.act_EchoDevices ? parent?.getDevicesFromList(settings.act_EchoDevices) : []
    Integer actDevSiz = actDevices.size()

    Map activeZones = settings.act_EchoZones ? getActiveZones() : [:]
    activeZones = activeZones.size() ? activeZones.findAll { it?.key in settings.act_EchoZones } : [:]
    Integer actZonesSiz = activeZones.size()

//  log.debug "activeZones: $activeZones"

    String actMsgTxt = sNULL
    String actType = (String)settings.actionType
    Boolean firstTierMsg = (tierData && tierData.isFirst == true)
    Boolean lastTierMsg = (tierData && tierData.isLast == true)
    Boolean actOk = getConfStatusItem("actions")
    if(actOk && actType) {
        def alexaMsgDev = actDevSiz && (Boolean)settings.notif_alexa_mobile ? actDevices[0] : null
        if(!(Boolean)condStatus.ok) { logWarn("executeAction | Skipping execution because ${condStatus.blocks} conditions have not been met", true); return }
        if(!actMap || !actMap?.size()) { logError("executeAction Error | The ActionExecutionMap is not found or is empty", true); return }
        if(settings.act_EchoZones && actZonesSiz == 0 && actDevSiz == 0) { logWarn("executeAction | No Active Zones Available and No Alternate Echo Devices Selected.", true); return }
        if(actDevSiz == 0 && !settings.act_EchoZones) { logError("executeAction Error | Echo Device List not found or is empty", true); return }
        if(!actMap.actionType) { logError("executeAction Error | The ActionType is missing or is empty", true); return }
        Map actConf = (Map)actMap.config
        Integer actDelay = (Integer)actMap.delay ?: 0
        Integer actDelayMs = actDelay*1000
        Integer changeVol = actConf?.volume?.change as Integer ?: null
        Integer restoreVol = actConf?.volume?.restore as Integer ?: null
        Integer alarmVol = actConf?.volume?.alarm ?: null
        switch(actType) {
            case "speak":
            case "speak_tiered":
            case "announcement":
            case "announcement_tiered":
                if(actConf[actType]) {
                    if(tierData?.volume && tierData?.volume?.change || tierData?.volume?.restore) {
                        if(tierData?.volume?.change) changeVol = tierData?.volume?.change
                        if(tierData?.volume?.restore) restoreVol = tierData?.volume?.restore
                    }
                    String txt = getResponseItem(evt, (String)tierData?.msg, allDevsResp, isRptAct, testMode) ?: sNULL
                    // log.debug "txt: $txt"
                    if(!txt) { txt = "Invalid Text Received... Please verify Action configuration..." }
                    actMsgTxt = txt

                    if(actZonesSiz) {
                        String mCmd = actType.replaceAll("_tiered", sBLANK)
                        sendLocationEvent(name: "es3ZoneCmd", value: mCmd, data:[ zones: activeZones.collect { it?.key as String }, cmd: mCmd, title: getActionName(), message: txt, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending Speech Text: (${txt}) to Zones (${activeZones.collect { it?.value?.name }})${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")

                    } else {
                        if(actType in ["speak", "speak_tiered"]) {
                            //Speak Command Logic
                            if(actDevSiz) {
                                if(changeVol!=null || restoreVol!=null) {
                                    actDevices.each { dev-> dev?.setVolumeSpeakAndRestore(changeVol, txt, restoreVol) }
                                } else {
                                    actDevices.each { dev-> dev?.speak(txt) }
                                }
                                logDebug("Sending Speech Text: (${txt}) to ${actDevices}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                            }
                        } else if (actType in ["announcement", "announcement_tiered"]) {
                            //Announcement Command Logic
                            String bn = getActionName()
                            bn = bn ?: "Echo Speaks Action"
                            if(actDevSiz > 1 && actConf[actType]?.deviceObjs && actConf[actType]?.deviceObjs?.size()) {
                                //NOTE: Only sends command to first device in the list | We send the list of devices to announce one and then Amazon does all the processing
                                //def devJson = new groovy.json.JsonOutput().toJson(actConf[actType]?.deviceObjs)
                                actDevices[0]?.sendAnnouncementToDevices(txt, bn, actConf[actType]?.deviceObjs, changeVol, restoreVol)
                            } else {
                                actDevices?.each { dev->
                                    dev?.playAnnouncement(txt, bn, changeVol, restoreVol)
                                }
                            }
                            logDebug("Sending Announcement Command: (${txt}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                        }
                    }
                }
                break
            case "voicecmd":
            case "sequence":
                String mAct="VoiceCmdasText"
                String mMeth="voiceCmdAsText"
                if(actType == "sequence") {
                    mAct="Sequence"
                    mMeth="executeSequenceCommand"
                }
                String mText= actConf[actType] ? (String)actConf[actType].text : sNULL
                if(mText != sNULL) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actType, message: mText, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${mAct} Command: (${mText}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            dev?."${mMeth}"(mText)
                        }
                        logDebug("Sending ${mAct} Command: (${mText}) to devices (${actDevices})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    }
                }
                break
/*
            case "sequence":
                if(actConf[actType] && actConf[actType].text) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actType, message: actConf[actType]?.text, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending Sequence Command: (${txt}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            dev?.executeSequenceCommand(actConf[actType].text as String)
                        }
                        logDebug("Sending Sequence Command to Zones: (${actConf[actType].text}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    }
                }
                break
*/
            case "playback":
            case "dnd":
                String mCmd= actConf[actType] ? (String)actConf[actType].cmd : sNULL
                if(mCmd != sNULL) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[zones: activeZones.collect { it?.key as String }, cmd: mCmd, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${mCmd}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            if(mCmd != "volume") dev?."${mCmd}"()
                            else if(mCmd == "volume" && changeVol != null) dev?.setVolume(changeVol)
                        }
                        logDebug("Sending ${actType.capitalize()} Command: (${mCmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol != null  ? " | Volume: ${changeVol}" : sBLANK}")
                    }
                }
                break

            case "builtin":
            case "calendar":
            case "weather":
                if(actConf[actType] && actConf[actType]?.cmd) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actConf[actType]?.cmd, message: actConf[actType]?.text, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType]?.cmd}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            dev?."${actConf[actType]?.cmd}"(changeVol, restoreVol)
                        }
                        logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    }
                }
                break

            case "sounds":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.name) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actConf[actType]?.cmd, message: actConf[actType]?.name, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType]?.cmd} | Name: ${actConf[actType]?.name}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            dev?."${actConf[actType]?.cmd}"(actConf[actType]?.name, changeVol, restoreVol)
                        }
                        logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType]?.cmd} | Name: ${actConf[actType]?.name}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    }
                }
                break

            case "alarm":
            case "reminder":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.label && actConf[actType]?.date && actConf[actType]?.time) {
                    actDevices?.each { dev->
                        dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time)
                        // dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time, actConf[actType]?.recur?.type, actConf[actType]?.recur?.opt)
                    }
                    logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices} | Label: ${actConf[actType]?.label} | Date: ${actConf[actType]?.date} | Time: ${actConf[actType]?.time}")
                }
                break

            case "music":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.provider && actConf[actType]?.search) {
                    log.debug "musicProvider: ${actConf[actType]?.provider} | ${convMusicProvider(actConf[actType]?.provider)}"
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actType, search: actConf[actType]?.search, provider: convMusicProvider(actConf[actType]?.provider), changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: true, displayed: true)
                        logDebug("Sending ${actType.capitalize()} Command: (${txt}) to Zones (${activeZones.collect { it?.value?.name }} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    } else if(actDevSiz) {
                        actDevices.each { dev->
                            dev?."${actConf[actType]?.cmd}"(actConf[actType]?.search, convMusicProvider(actConf[actType]?.provider), changeVol, restoreVol)
                        }
                        logDebug("Sending ${actType.capitalize()} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    }
                }
                break

            case "alexaroutine":
                if(actConf[actType] && actConf[actType].cmd && actConf[actType].routineId) {
                    actDevices[0]?."${actConf[actType].cmd}"((String)actConf[actType].routineId)
                    logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType].cmd}) | RoutineId: ${actConf[actType].routineId} to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                }
                break

            case "wakeword":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id?.toString() == d?.device?.toString() }
                        if(aDev) {
                            aDev?."${d?.cmd}"(d?.wakeword)
                            logDebug("Sending WakeWord: (${d?.wakeword}) | Command: (${d?.cmd}) to ${aDev}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                        }
                    }
                }
                break

            case "bluetooth":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        def aDev = actDevices?.find { it?.id?.toString() == d?.device?.toString() }
                        if(aDev) {
                            if(d?.cmd == "disconnectBluetooth") {
                                aDev?."${d?.cmd}"()
                            } else {
                                aDev?."${d?.cmd}"(d?.btDevice)
                            }
                            logDebug("Sending ${d?.cmd} | Bluetooth Device: ${d?.btDevice} to ${aDev}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                        }
                    }
                }
                break
        }
        if(isActNotifConfigured()) {
            Boolean ok2SendNotif = true
            if(customMsgConfigured()) { actMsgTxt = settings.notif_custom_message }
            if(customMsgRequired() && !customMsgConfigured()) { ok2SendNotif = false }
            if(ok2SendNotif && actMsgTxt) {
                if(sendNotifMsg(getActionName(), actMsgTxt, alexaMsgDev, false)) { logTrace("Sent Action Notification...") }
            }
        }
        if(tierData?.size() && (Integer)settings.act_tier_cnt > 1) {
            log.debug "firstTierMsg: ${firstTierMsg} | lastTierMsg: ${lastTierMsg}"
            if(firstTierMsg) {
                Integer del = settings.act_tier_start_delay
                if(del) {
                    logTrace( "scheduled executeTaskCommands in $del seconds - start delay")
                    runIn(del, "executeTaskCommands", [data:[type: "act_tier_start_"]])
                } else { executeTaskCommands([type:"act_tier_start_"]) }
            }
            if(lastTierMsg) {
                Integer del = settings.act_tier_stop_delay
                if(del) {
                    logTrace( "scheduled executeTaskCommands in $del seconds - stop delay")
                    runIn(del, "executeTaskCommands", [data:[type: "act_tier_stop_"]])
                } else { executeTaskCommands([type:"act_tier_stop_"]) }
            }
        } else {
            Integer del = settings.act_tasks_delay
            if(del) {
                logTrace( "scheduled executeTaskCommands in $del seconds - action tasks delay")
                runIn(del, "executeTaskCommands", [data:[type: "act_"]])
            } else { executeTaskCommands([type: "act_"]) }
        }
    }
    logDebug("ExecuteAction Finished | ProcessTime: (${now()-startTime}ms)")
}

private postTaskCommands(data) {
    if(data?.type && settings."${data.type}sirens" && (String)settings."${data.type}siren_cmd") { settings."${data.type}sirens"*.off() }
}

Map getInputData(String inName) {
    // TODO
    log.debug "getInputData inName: $inName"
    String desc = sNULL
    String title = sNULL
    String template = sNULL
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
            else if(inName?.startsWith("act_tier_item_") && inName?.endsWith("_txt")) {
                def i = inName?.tokenize("_")
                title = "Tier Response (${i[3]})"
                desc = "<li>Add custom responses to use when this action is executed.</li>"
                template = "Custom tier ${i[3]} message here."
            }
            break
    }
    Map o = [
        val: settings."${inName}" ? settings."${inName}"?.toString()?.replaceAll("\'", sBLANK) : null,
        desc: """<ul class="pl-3" style="list-style-type: bullet;">${desc}</ul>""",
        title: title,
        template: template,
        version: appVersionFLD
    ]
    return o
}

def updateTxtEntry(obj) {
    // log.debug "updateTxtEntry | Obj: $obj"
    if(obj?.name && obj?.type) {
        settingUpdate("${obj?.name}", obj?.val ?: sBLANK, obj?.type as String)
        return true
    }
    return false
}

public getSettingInputVal(String inName) {
    // log.debug "getSettingInputVal: ${inName}"
    return settings."${inName}" ?: null
}


/***********************************************************************************************************
   HELPER UTILITES
************************************************************************************************************/

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace) }
public void logsEnabled() { if(advLogsActive() && !getTsVal("logsEnabled")) { logTrace("enabling logging timer"); updTsVal("logsEnabled") } }
public void logsDisable() {
    if(advLogsActive()) {
        Integer dtSec = getLastTsValSecs("logsEnabled", null)
        if(dtSec && (dtSec > 3600*6)) {
            settingUpdate("logDebug", sFALSE, sBOOL)
            settingUpdate("logTrace", sFALSE, sBOOL)
            remTsVal("logsEnabled")
            log.debug "Disabling debug logs"
        }
    }
}

@Field volatile static Map<String,Map> tsDtMapFLD=[:]

private void updTsVal(String key, String dt=sNULL) {
    String appId=app.getId()
    Map data=tsDtMapFLD[appId] ?: [:]
    if(!data) data = state.tsDtMap ?: [:]
    if(key) { data[key] = dt ?: getDtNow() }
    tsDtMapFLD[appId]=data
    tsDtMapFLD=tsDtMapFLD

    state.tsDtMap = data
}

private void remTsVal(key) {
    String appId=app.getId()
    Map data=tsDtMapFLD[appId] ?: [:]
    if(!data) data = state.tsDtMap ?: [:]
    if(key) {
        if(key instanceof List) {
                key.each { String k->
                    if(data.containsKey(k)) { data.remove(k) }
                }
        } else if(data.containsKey((String)key)) { data.remove((String)key) }
    }
    tsDtMapFLD[appId]=data
    tsDtMapFLD=tsDtMapFLD

    state.tsDtMap = data
}

String getTsVal(String key) {
    String appId=app.getId()
    Map tsMap=tsDtMapFLD[appId]
    if(!tsMap) tsMap = state.tsDtMap ?: [:]
    if(key && tsMap && tsMap[key]) { return (String)tsMap[key] }
    return sNULL
}

Integer getLastTsValSecs(String val, Integer nullVal=1000000) {
    return (val && getTsVal(val)) ? GetTimeDiffSeconds(getTsVal(val)).toInteger() : nullVal
}

private void updAppFlag(String key, val) {
    //ERS
    def t0 = atomicState.appFlagsMap
    def data = t0 ?: [:]
    if(key) { data[key] = val }
    atomicState.appFlagsMap = data
}

private remAppFlag(key) {
    //ERS
    def t0 = atomicState.appFlagsMap
    def data = t0 ?: [:]
    if(key) {
        if(key instanceof List) {
            key?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
        } else { if(data.containsKey((String)key)) { data.remove((String)key) } }
        atomicState.appFlagsMap = data
    }
}

Boolean getAppFlag(val) {
    //ERS
    def aMap = atomicState.appFlagsMap
    if(val && aMap && aMap[val]) { return aMap[val] }
    return false
}

private void stateMapMigration() {
    //Timestamp State Migrations
    Map tsItems = ["lastAfterEvtCheck":"lastAfterEvtCheck", "lastNotifMsgDt":"lastNotifMsgDt"]
    tsItems?.each { String k, String v-> if(state.containsKey(k)) { updTsVal(v, state[k]); state.remove(k) } }

    //App Flag Migrations
    Map flagItems = [:]
    flagItems?.each { String k, String v-> if(state.containsKey(k)) { updAppFlag(v, state[k]); state.remove(k) } }
    updAppFlag("stateMapConverted", true)
}

void settingUpdate(String name, value, String type=sNULL) {
    if(name && type) { app?.updateSetting(name, [type: type, value: value]) }
    else if (name && type == sNULL) { app?.updateSetting(name, value) }
}

void settingRemove(String name) {
    logTrace("settingRemove($name)...")
    if(name && settings.containsKey(name)) { app?.removeSetting(name) }
}

static List weekDaysEnum() { return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"] }
static Map weekDaysAbrvEnum() { return ["MO":"Monday", "TU":"Tuesday", "WE":"Wednesday", "TH":"Thursday", "FR":"Friday", "SA":"Saturday", "SU":"Sunday"] }
static List monthEnum() { return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"] }
static Map daysOfWeekMap() { return ["MON":"Monday", "TUE":"Tuesday", "WED":"Wednesday", "THU":"Thursday", "FRI":"Friday", "SAT":"Saturday", "SUN":"Sunday"] }
static Map weeksOfMonthMap() { return ["1":"1st Week", "2":"2nd Week", "3":"3rd Week", "4":"4th Week", "5":"5th Week"] }
static Map monthMap() { return ["1":"January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"] }

static Map getAlarmTrigOpts() {
    return ["armedAway":"Armed Away", "armingAway":"Arming Away Pending exit delay","armedHome":"Armed Home","armingHome":"Arming Home pending exit delay", "armedNight":"Armed Night", "armingNight":"Arming Night pending exit delay","disarmed":"Disarmed", "allDisarmed":"All Disarmed","alerts":"Alerts"]
}

private static Map getAlarmSystemAlertOptions(){
    return [
        intrusion:              "Intrusion Away",
        "intrusion-home":       "Intrusion Home",
        "intrusion-night":      "Intrusion Night",
        smoke:                  "Smoke",
        water:                  "Water",
        rule:                   "Rule",
        cancel:                 "Alerts cancelled",
        arming:                 "Arming failure"
    ]
}

private static Map getAlarmSystemStatusActions(){
    return [
        armAway:                "Arm Away",
        armHome:                "Arm Home",
        armNight:               "Arm Night",
        disarm:                 "Disarm",
        armRules:               "Arm Monitor Rules",
        disarmRules:            "Disarm Monitor Rules",
        disarmAll:              "Disarm All",
        armAll:                 "Arm All",
        cancelAlerts:           "Cancel Alerts"
    ]
}

/*
def getShmIncidents() {
    Long incidentThreshold = now() - 604800000L
    return location.activeIncidents.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}*/

public Map getActionMetrics() {
    Map out = [:]
    out.version = appVersionFLD
    out.type = (String)settings.actionType ?: sNULL
    out.delay = settings.actDelay ?: 0
    out.triggers = settings.triggerEvents ?: []
    out.echoZones = settings.act_EchoZones ?: []
    out.echoDevices = settings.act_EchoDevices ?: []
    out.usingCustomText = (customTxtItems()?.size())
    Integer tDevCnt = 0
    settings.triggerEvents?.each { if(settings."trig_${it}" && settings."trig_${it}"?.size()) { tDevCnt = tDevCnt+settings."trig_${it}"?.size() } }
    out.triggerDeviceCnt = tDevCnt
    return out
}

// String pushStatus() { return (settings.notif_sms_numbers?.toString()?.length()>=10 || settings.notif_send_push || settings.notif_pushover) ? ((settings.notif_send_push || (settings.notif_pushover && settings.notif_pushover_devices)) ? "Push Enabled" : "Enabled") : sNULL }

Integer getLastNotifMsgSec() { return getLastTsValSec("lastNotifMsgDt") }
//Integer getLastChildInitRefreshSec() { return getLastTsValSec("lastChildInitRefreshDt", 3600) }
//Integer getLastNotifMsgSec() { return !state.lastNotifMsgDt ? 100000 : GetTimeDiffSeconds(state.lastNotifMsgDt, "getLastMsgSec").toInteger() }

Boolean getOk2Notify() {
    Boolean smsOk // = (settings.notif_sms_numbers?.toString()?.length()>=10)
    Boolean pushOk // = (settings.notif_send_push)
    Boolean pushOver // = (settings.notif_pushover && settings.notif_pushover_devices)
    Boolean alexaMsg = ((Boolean)settings.notif_alexa_mobile)
    Boolean notifDevsOk = (((List)settings.notif_devs)?.size())
    Boolean daysOk = settings.notif_days ? (isDayOfWeek(settings.notif_days)) : true
    Boolean timeOk = notifTimeOk()
    Boolean modesOk = settings.notif_modes ? (isInMode(settings.notif_modes)) : true
    Boolean result = true
    if(!(smsOk || pushOk || alexaMsg || notifDevsOk || pushOver)) { result = false }
    if(!(daysOk && modesOk && timeOk)) { result = false }
    logDebug("getOk2Notify() RESULT: $result | notifDevs: $notifDevs |smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver | alexaMsg: $alexaMsg || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
    return result
}

Boolean notifTimeOk() {
    Date startTime
    Date stopTime
    String startType = settings.notif_time_start_type
    String stopType = settings.notif_time_stop_type
    if(startType && stopType) {
        startTime = startType == 'time' && settings.notif_time_start ? toDateTime(settings.notif_time_start) : null
        stopTime = stopType == 'time' && settings.notif_time_stop ? toDateTime(settings.notif_time_stop) : null
    } else { return true }

    Date now = new Date()
    if(startType in lSUNRISESET || stopType in lSUNRISESET) {
        def sun = getSunriseAndSunset()
        Long lsunset = sun.sunset.time
        Long lsunrise = sun.sunrise.time
        Long startoffset = settings.notif_time_start_offset ? settings.notif_time_start_offset*1000L : 0L
        Long stopoffset = settings.notif_time_stop_offset ? settings.notif_time_stop_offset*1000L : 0L
        if(startType in lSUNRISESET) {
            Long startl = (startType == 'sunrise' ? lsunrise : lsunset) + startoffset
            startTime = new Date(startl)
        }
        if(stopType in lSUNRISESET) {
            Long stopl = (stopType == 'sunrise' ? lsunrise : lsunset) + stopoffset
            stopTime = new Date(stopl)
        }
    }

    if(startTime && stopTime) {
        Boolean not = startTime.getTime() > stopTime.getTime()
        Boolean isBtwn = timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, location?.timeZone) ? false : true
        isBtwn = not ? !isBtwn : isBtwn
        logTrace("NotifTimeOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not ? startTime:stopTime})")
        return isBtwn
    } else { return true }
}

// Sends the notifications based on app settings
public sendNotifMsg(String msgTitle, String msg, alexaDev=null, Boolean showEvt=true) {
    logTrace("sendNotifMsg() | msgTitle: ${msgTitle}, msg: ${msg}, $alexaDev, showEvt: ${showEvt}")
    List sentSrc = [] // ["Push"]
    Boolean sent = false
    try {
        String newMsg = msgTitle+': '+msg
        String flatMsg = newMsg.replaceAll("\n", sSPACE)
        if(!getOk2Notify()) {
            logInfo( "sendNotifMsg: Notification not configured or Message Skipped During Quiet Time ($flatMsg)")
//            if(showEvt) { sendNotificationEvent(newMsg) }
        } else {
            if((List)settings.notif_devs) {
                sentSrc.push("Notification Devices")
                ((List)settings.notif_devs)?.each { it?.deviceNotification(newMsg) }
                sent = true
            }
            if((Boolean)settings.notif_alexa_mobile && alexaDev) {
                alexaDev?.sendAlexaAppNotification(newMsg)
                sentSrc.push("Alexa Mobile App")
                sent = true
            }
            if(sent) {
                state.lastNotificationMsg = flatMsg
                updTsVal("lastNotifMsgDt")
                logDebug("sendNotifMsg: Sent ${sentSrc} (${flatMsg})")
            }
        }
    } catch (ex) {
        logError("sendNotifMsg $sentSrc Exception: ${ex}", false, ex)
    }
    return sent
}

Boolean isActNotifConfigured() {
    if(customMsgRequired() && !(settings.notif_use_custom && settings.notif_custom_message)) { return false }
    return ((List)settings.notif_devs || (Boolean)settings.notif_alexa_mobile)
}

/*
//PushOver-Manager Input Generation Functions
private getPushoverSounds(){return (Map) state?.pushoverManager?.sounds?:[:]}
private getPushoverDevices(){List opts=[];Map pmd=state.pushoverManager?:[:];pmd?.apps?.each{k,v->if(v&&v?.devices&&v?.appId){Map dm=[:];v?.devices?.sort{}?.each{i->dm["${i}_${v?.appId}"]=i};addInputGrp(opts,v?.appName,dm)}};return opts}
private inputOptGrp(List groups,String title){def group=[values:[],order:groups?.size()];group?.title=title?:sBLANK;groups<<group;return groups}
private addInputValues(List groups,String key,String value){def lg=groups[-1];lg["values"]<<[key:key,value:value,order:lg["values"]?.size()];return groups}
private listToMap(List original){original.inject([:]){r,v->r[v]=v;return r}}
private addInputGrp(List groups,String title,values){if(values instanceof List){values=listToMap(values)};values.inject(inputOptGrp(groups,title)){r,k,v->return addInputValues(r,k,v)};return groups}
private addInputGrp(values){addInputGrp([],null,values)}
//PushOver-Manager Location Event Subscription Events, Polling, and Handlers
public pushover_init(){subscribe(location,"pushoverManager",pushover_handler);pushover_poll()}
public pushover_cleanup(){state.remove("pushoverManager");unsubscribe("pushoverManager")}
public pushover_poll(){sendLocationEvent(name:"pushoverManagerCmd",value:"poll",data:[empty:true],isStateChange:true,descriptionText:"Sending Poll Event to Pushover-Manager")}
public pushover_msg(List devs,Map data){if(devs&&data){sendLocationEvent(name:"pushoverManagerMsg",value:"sendMsg",data:data,isStateChange:true,descriptionText:"Sending Message to Pushover Devices: ${devs}")}}
public pushover_handler(evt){Map pmd=state.pushoverManager?:[:];switch(evt?.value){case"refresh":def ed = evt?.jsonData;String id = ed?.appId;Map pA = pmd?.apps?.size() ? pmd?.apps : [:];if(id){pA[id]=pA?."${id}"instanceof Map?pA[id]:[:];pA[id]?.devices=ed?.devices?:[];pA[id]?.appName=ed?.appName;pA[id]?.appId=id;pmd?.apps = pA};pmd?.sounds=ed?.sounds;break;case "reset":pmd=[:];break;};state.pushoverManager=pmd;}
//Builds Map Message object to send to Pushover Manager
private buildPushMessage(List devices,Map msgData,timeStamp=false){if(!devices||!msgData){return};Map data=[:];data?.appId=app?.getId();data.devices=devices;data?.msgData=msgData;if(timeStamp){data?.msgData?.timeStamp=new Date().getTime()};pushover_msg(devices,data);} */

Integer versionStr2Int(String str) { return str ? str.toString()?.replaceAll("\\.", sBLANK)?.toInteger() : null }
Boolean minVersionFailed() {
    try {
        Integer t0 = parent?.minVersions()["actionApp"]
        Integer minDevVer = t0 ?: null
        if(minDevVer != null && versionStr2Int(appVersionFLD) < minDevVer) { return true }
        else { return false }
    } catch (e) {
        return false
    }
}


/************************************************************************************************************
        webCoRE Integration
************************************************************************************************************/
private static String webCoRE_handle(){return'webCoRE'}

private webCoRE_init(pistonExecutedCbk=null){
    subscribe(location,webCoRE_handle(),webCoRE_handler);
//    if(pistonExecutedCbk)subscribe(location,"${webCoRE_handle()}.pistonExecuted",webCoRE_handler);
    if(!webCoREFLD) {
        webCoREFLD = [:] + [cbk:true] // pistonExecutedCbk]
        webCoRE_poll(true)
    }
}

@Field static final String sLASTWU = 'lastwebCoREUpdDt'
@Field volatile static Map<String,Map> webCoREFLD = [:]

private webCoRE_poll(Boolean anow=false){
    Long rUpd = webCoREFLD?.updated
    if(rUpd && (now() > (rUpd+300000L))) {
        Date aa = new Date(rUpd)
        updTsVal(sLASTWU, formatDt(aa))
    }
    Integer lastUpd = getLastTsValSecs(sLASTWU)
    if (!webCoREFLD || (lastUpd > (3600*24)) || (anow && lastUpd > 300)) {
        sendLocationEvent([name: "${webCoRE_handle()}.poll",value:'poll',isStateChange:true,displayed:false])
        updTsVal(sLASTWU)
    }
}

public webCoRE_execute(String pistonIdOrName,Map data=[:]) {
    String i = pistonIdOrName
    if(i){sendLocationEvent([name:i,value:app.label,isStateChange:true,displayed:false,data:data])}
}

public List webCoRE_list(String mode){
    return (List)webCoREFLD?.pistons?.sort {it?.name}?.collect { [(it?.id): it?.aname?.replaceAll("<[^>]*>", sBLANK)] }
}

public getPistonByName(String pistonIdOrName) {
    String i=(webCoREFLD?.pistons ?: []).find{(it.name==pistonIdOrName)||(it.id==pistonIdOrName)}?.id;
}

String getPistonById(String rId) {
    Map a = webCoRE_list('name')?.find { it.containsKey(rId) }
    String aaa = (String)a?."${rId}"
    return aaa ?: "Refresh to display piston name..."
}

public  webCoRE_handler(evt){
    switch(evt.value){
      case 'pistonList':
        List p=webCoREFLD?.pistons ?: [];
        Map d=evt.jsonData?:[:];
        if(d.id && d.pistons && (d.pistons instanceof List)){
            p.removeAll{it.iid==d.id};
            p+=d.pistons.collect{[iid:d.id]+it}.sort{it.name};
            def a = webCoREFLD?.cbk

            Boolean aa = getTheLock(sHMLF, "webCoRE_Handler")
            webCoREFLD = [cbk: a, updated: now(), pistons: p]
            releaseTheLock(sHMLF)

            updTsVal(sLASTWU)
        }
        break;
      case 'pistonExecuted':
        if(valTrigEvt("pistonExecuted") && settings.trig_pistonExecuted) {
            webcoreEvtHandler(evt)
        }
        break
    }
}

public static String webCore_icon(){ return "https://raw.githubusercontent.com/ady624/webCoRE/master/resources/icons/app-CoRE.png" }
/******************************************
|   Restriction validators
*******************************************/

Double getDevValueAvg(devs, attr) {
    List vals = devs?.findAll { it?."current${attr?.capitalize()}" != null && it?."current${attr?.capitalize()}" != 0 }?.collect { it?."current${attr?.capitalize()}" as Double }
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

List<String> getLocationPistons() {
    List aa = (webCoREFLD?.pistons ?: []).findAll { it.id }.collect { (String)it.id }
    return aa ?: []
}

def getModeById(mId) {
    return location?.getModes()?.find{it?.id == mId}
}

Boolean isInMode(modes, Boolean not=false) {
    if(modes instanceof String){
        modes = modes.toList()
        log.warn("Found bad mode settings $modes, this setting needs to be updated to work properly")
    }
    return (modes) ? (not ? (!(getCurrentMode() in modes)) : (getCurrentMode() in modes)) : false
}

Boolean isInAlarmMode(List modes) {
    String a = location?.hsmStatus ?: "disarmed"
    //return (modes) ? (parent?.getAlarmSystemStatus() in modes) : false
    return (modes) ? (a in modes) : false
}

Boolean areAllDevsSame(List devs, String attr, val) {
    if(devs && attr && val) { return (devs?.findAll { it?.currentValue(attr) == val as String }?.size() == devs?.size()) }
    return false
}

Boolean allDevCapValsEqual(List devs, String cap, val) {
    if(devs) {
        if(val instanceof List) return (devs.findAll { it?."current${cap?.capitalize()}" in val }?.size() == devs?.size())
        else return (devs.findAll { it?."current${cap?.capitalize()}" == val }?.size() == devs?.size())
    }
    return false
}

Boolean anyDevCapValsEqual(List devs, String cap, val) {
    if(devs && cap && val) {
        if(val instanceof List) return (devs.findAll { it?."current${cap?.capitalize()}" in val }?.size() >= 1)
        else return (devs.findAll { it?."current${cap?.capitalize()}" == val }?.size() >= 1)
    }
    return false
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
    if(devs) { return (devs.find { it?."current${cap?.capitalize()}" == val }) }
    return false
}

String getAlarmSystemName(Boolean abbr=false) {
    return (abbr ? "HSM" : "Hubitat Safety Monitor")
}
/******************************************
|    Time and Date Conversion Functions
*******************************************/
String formatDt(Date dt, Boolean tzChg=true) {
    return dateTimeFmt(dt, "E MMM dd HH:mm:ss z yyyy", tzChg)
}

String dateTimeFmt(Date dt, String fmt, Boolean tzChg=true) {
//    if(!(dt instanceof Date)) { try { dt = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", dt?.toString()) } catch(e) { dt = Date.parse("E MMM dd HH:mm:ss z yyyy", dt?.toString()) } }
    def tf = new java.text.SimpleDateFormat(fmt)
    if(tzChg && location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return (String)tf.format(dt)
}

String convToTime(Date dt) {
    String newDt = dateTimeFmt(dt, "h:mm a")
    if(newDt?.contains(":00 ")) { newDt?.replaceAll(":00 ", sSPACE) }
    return newDt
}

String convToDate(Date dt) {
    String newDt = dateTimeFmt(dt, "EEE, MMM d")
    return newDt
}

String convToDateTime(Date dt) {
    String t = dateTimeFmt(dt, "h:mm a")
    String d = dateTimeFmt(dt, "EEE, MMM d")
    return d+', '+t
}

static Date parseDate(dt) { return Date.parse("E MMM dd HH:mm:ss z yyyy", dt?.toString()) }

static Boolean isDateToday(Date dt) { return (dt && dt.clearTime().compareTo(new Date().clearTime()) >= 0) }

static String strCapitalize(str) { return str ? str.toString().capitalize() : sNULL }

static String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }
/*
String parseDt(String pFormat, String dt, Boolean tzFmt=true) {
    Date newDt = Date.parse(pFormat, dt)
    return formatDt(newDt, tzFmt)
} */

String parseFmtDt(String parseFmt, String newFmt, dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    return dateTimeFmt(newDt, newFmt)
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now)
}

String epochToTime(Date dt) {
    return dateTimeFmt(dt, "h:mm a")
}
/*
String time2Str(String time, String fmt="h:mm a") {
    if(time) {
        Date t = timeToday(time, location?.timeZone)
        return dateTimeFmt(t, fmt)
    }
    return sNULL
} */

String fmtTime(t, String fmt="h:mm a", Boolean altFmt=false) {
    if(!t) return sNULL
    Date dt = new Date().parse(altFmt ? "E MMM dd HH:mm:ss z yyyy" : "yyyy-MM-dd'T'HH:mm:ss.SSSZ", t.toString())
    return dateTimeFmt(dt, fmt)
}

Long GetTimeDiffSeconds(String lastDate, String sender=sNULL) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        Date lastDt = parseDate(lastDate) // Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        Long start = lastDt.getTime()
        Long stop = now()
        Long diff = (stop - start) / 1000L
        return diff.abs()
    }
    catch (ex) {
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : sBLANK}lastDate: $lastDate): ${ex?.message}", false, ex)
        return 10000L
    }
}

String getDateByFmt(String fmt, Date dt=null) {
    def df = new java.text.SimpleDateFormat(fmt)
    df?.setTimeZone(location?.timeZone)
    return (String)df?.format(dt ?: new Date())
}

Map getDateMap() {
    Map m = [:]
    m.dayOfYear =    getDateByFmt("DD")
    m.dayNameShort = getDateByFmt("EEE").toUpperCase()
    m.dayName =      getDateByFmt("EEEE")
    m.day =          getDateByFmt("d")
    m.week =         getDateByFmt("W")
    m.weekOfYear =   getDateByFmt("w")
    m.monthName =    getDateByFmt("MMMMM")
    m.month =        getDateByFmt("MM")
    m.year =         getDateByFmt("yyyy")
    m.hour =         getDateByFmt("hh")
    m.minute =       getDateByFmt("mm")
    m.second =       getDateByFmt("ss")
    return m
}

Boolean isDayOfWeek(List opts) {
    def df = new java.text.SimpleDateFormat("EEEE")
    df?.setTimeZone(location?.timeZone)
    String day = df?.format(new Date())
    return opts?.contains(day)
}

Boolean isMonthOfYear(List opts) {
    Map dtMap = getDateMap()
    return ( opts?.contains((String)dtMap.monthName) )
}

Boolean isTimeBetween(String startTime, String stopTime, Date curTime= new Date()) {
    if(!startTime && !stopTime) { return true }
    Date st = toDateTime(startTime)
    Date et = toDateTime(stopTime)
    return timeOfDayIsBetween(st, et, curTime, location?.timeZone)
}

/******************************************
|   App Input Description Functions
*******************************************/

static Map dimmerLevelEnum() { return [100:"Set Level to 100%", 90:"Set Level to 90%", 80:"Set Level to 80%", 70:"Set Level to 70%", 60:"Set Level to 60%", 50:"Set Level to 50%", 40:"Set Level to 40%", 30:"Set Level to 30%", 20:"Set Level to 20%", 10:"Set Level to 10%"] }

String unitStr(type) {
    switch(type) {
        case "temp":
            return "\u00b0${getTemperatureScale() ?: "F"}"
        case "humidity":
            return "%"
        default:
            return sBLANK
    }
}

String getAppNotifDesc(Boolean hide=false) {
    String str = sBLANK
    if(isActNotifConfigured()) {
        Boolean ok = getOk2Notify()
        str += hide ? sBLANK : "Send allowed: (${ok ? okSymFLD : notOkSymFLD})\n"
        str += ((List)settings.notif_devs) ? " \u2022 Notification Device${pluralizeStr((List)settings.notif_devs)} (${((List)settings.notif_devs).size()})\n" : sBLANK
        str += (Boolean)settings.notif_alexa_mobile ? " \u2022 Alexa Mobile App\n" : sBLANK
        String res = getNotifSchedDesc(true)
        str += res ?: sBLANK
    }
    return str != sBLANK ? str : sNULL
}

List getQuietDays() {
    List<String> allDays = weekDaysEnum()
    List curDays = settings.notif_days ?: []
    return allDays.findAll { (!curDays?.contains(it as String)) }
}

String getNotifSchedDesc(Boolean min=false) {
    String startType = settings.cond_time_start_type
    Date startTime
    String stopType = settings.cond_time_stop_type
    Date stopTime
    List dayInput = settings.notif_days
    List modeInput = settings.notif_modes
    String str = sBLANK

    if(startType && stopType) {
        startTime = startType == 'time' && settings.notif_time_start ? toDateTime(settings.notif_time_start) : null
        stopTime = stopType == 'time' && settings.notif_time_stop ? toDateTime(settings.notif_time_stop) : null
    }
    if(startType in lSUNRISESET || stopType in lSUNRISESET) {
        def sun = getSunriseAndSunset()
        Long lsunset = sun.sunset.time
        Long lsunrise = sun.sunrise.time
        Long startoffset = settings.notif_time_start_offset ? settings.notif_time_start_offset*1000L : 0L
        Long stopoffset = settings.notif_time_stop_offset ? settings.notif_time_stop_offset*1000L : 0L
        if(startType in lSUNRISESET) {
            Long startl = (startType == 'sunrise' ? lsunrise : lsunset) + startoffset
            startTime = new Date(startl)
        }
        if(stopType in lSUNRISESET) {
            Long stopl = (stopType == 'sunrise' ? lsunrise : lsunset) + stopoffset
            stopTime = new Date(stopl)
        }
    }
    Boolean timeOk = notifTimeOk()
    Boolean daysOk = dayInput ? (isDayOfWeek(dayInput)) : true
    Boolean modesOk = modeInput ? (isInMode(modeInput)) : true
    Boolean rest = !(daysOk && modesOk && timeOk)
    String startLbl = startTime ? epochToTime(startTime) : sBLANK
    String stopLbl = stopTime ? epochToTime(stopTime) : sBLANK
    str += (startLbl && stopLbl) ? "   ${sBULLET} Restricted Times: ${startLbl} - ${stopLbl} (${!timeOk ? okSymFLD : notOkSymFLD})" : sBLANK
    List qDays = getQuietDays()
    String a = " (${!daysOk ? okSymFLD : notOkSymFLD})"
    str += dayInput && qDays ? "${(startLbl || stopLbl) ? "\n" : sBLANK}   ${sBULLET} Restricted Day${pluralizeStr(qDays, false)}:${min ? " (${qDays?.size()} selected)" : " ${qDays?.join(", ")}"}${a}" : sBLANK
    a = " (${!modesOk ? okSymFLD : notOkSymFLD})"
    str += modeInput ? "${(startLbl || stopLbl || qDays) ? "\n" : sBLANK}   ${sBULLET} Allowed Mode${pluralizeStr(modeInput, false)}:${min ? " (${modeInput?.size()} selected)" : " ${modeInput?.join(",")}"}${a}" : sBLANK
    str = str ? " ${sBULLET} Restrictions Active: ${getOkOrNotSymHTML(rest)}" + addLineBr() + str : sBLANK
    return (str != sBLANK) ? str : sNULL
}

String getTriggersDesc(Boolean hideDesc=false, Boolean addFoot=true) {
    Boolean confd = triggersConfigured()
    List setItem = settings.triggerEvents
    String sPre = "trig_"
    if(confd && setItem?.size()) {
        if(!hideDesc) {
            String str = spanWrapSm("Triggers${!addFoot ? " for "+(String)buildActTypeEnum()."${(String)settings.actionType}" : sBLANK}:", sNULL, true, true)
            setItem?.each { String evt->
                String adder = sBLANK
                switch(evt) {
                    case "scheduled":
                        String schedTyp = settings."${sPre}${evt}_type" ? settings."${sPre}${evt}_type" : sNULL
                        str += spanWrapSm(" ${sBULLET} ${evt?.capitalize()}${settings."${sPre}${evt}_type" ? " (${settings."${sPre}${evt}_type"})" : ""}", sNULL, false, true)
                        if(schedTyp == "Recurring") {
                            str += settings."${sPre}${evt}_recurrence"     ? "    ${sBULLETINV} Recurrence: (${settings."${sPre}${evt}_recurrence"})\n"      : sBLANK
                            str += settings."${sPre}${evt}_time"     ? "    ${sBULLETINV} Time: (${fmtTime(settings."${sPre}${evt}_time")})\n"      : sBLANK
                            str += settings."${sPre}${evt}_weekdays"     ? "    ${sBULLETINV} Week Days: (${settings."${sPre}${evt}_weekdays"?.join(",")})\n"      : sBLANK
                            str += settings."${sPre}${evt}_daynums"     ? "    ${sBULLETINV} Days of Month: (${settings."${sPre}${evt}_daynums"?.size()})\n"      : sBLANK
                            str += settings."${sPre}${evt}_weeks"    ? "    ${sBULLETINV} Weeks of Month: (${settings."${sPre}${evt}_weeks"?.join(",")})\n" : sBLANK
                            str += settings."${sPre}${evt}_months"   ? "    ${sBULLETINV} Months: (${settings."${sPre}${evt}_months"?.join(",")})\n"  : sBLANK
                        }
                        if(schedTyp == "One-Time") {
                            str += settings."${sPre}${evt}_time"     ? "    ${sBULLETINV} Time: (${fmtTime(settings."${sPre}${evt}_time")})\n"      : sBLANK
                        }
                        if(schedTyp in ["Sunrise", "Sunset"]) {
                            str += settings."${sPre}${evt}_sunState_offset"     ? "    ${sBULLETINV} Offset: (${settings."${sPre}${evt}_sunState_offset"})\n"      : sBLANK
                        }
                        break
                    case "alarm":
                        str += " \u2022 ${evt?.capitalize()} (${getAlarmSystemName(true)})${settings."${sPre}${evt}" ? " (${settings."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings."${sPre}${evt}_once" ? "    ${sBULLETINV} Once a Day: (${settings."${sPre}${evt}_once"})\n" : sBLANK
                        break
                    case "pistonExecuted":
//                    case "routineExecuted":
                    case sMODE:
                    case "scene":
                        str += " \u2022 ${evt == "pistonExecuted" ? "Piston" : evt?.capitalize()}${settings."${sPre}${evt}" ? " (${settings."${sPre}${evt}"?.size()} Selected)" : ""}\n"
                        str += settings."${sPre}${evt}_once" ? "    ${sBULLETINV} Once a Day: (${settings."${sPre}${evt}_once"})\n" : sBLANK
                        break
                    case "pushed":
                    case "released":
                    case "held":
                    case "doubleTapped":
                        adder = "Button "
                    default:
                        str += spanWrapSm(" ${sBULLET} ${adder}${evt?.capitalize()}${settings."${sPre}${evt}" ? " (${settings."${sPre}${evt}"?.size()} Selected)" : ""}", sNULL, false, true)
                        def subStr = sBLANK
                        if(settings."${sPre}${evt}_cmd" in [sABOVE, sBELOW, "equal", sBETWEEN]) {
                            if (settings."${sPre}${evt}_cmd" == sBETWEEN) {
                                str += settings."${sPre}${evt}_cmd"  ? "    ${sBULLETINV} ${settings."${sPre}${evt}_cmd"}: (${settings."${sPre}${evt}_low"} - ${settings."${sPre}${evt}_high"})\n" : sBLANK
                            } else {
                                str += (settings."${sPre}${evt}_cmd" == sABOVE && settings."${sPre}${evt}_high")     ? "    ${sBULLETINV} Above: (${settings."${sPre}${evt}_high"})\n" : sBLANK
                                str += (settings."${sPre}${evt}_cmd" == sBELOW && settings."${sPre}${evt}_low")      ? "    ${sBULLETINV} Below: (${settings."${sPre}${evt}_low"})\n" : sBLANK
                                str += (settings."${sPre}${evt}_cmd" == "equal" && settings."${sPre}${evt}_equal")    ? "    ${sBULLETINV} Equals: (${settings."${sPre}${evt}_equal"})\n" : sBLANK
                            }
                        } else {
                            str += settings."${sPre}${evt}_cmd"  ? "    ${sBULLETINV} Trigger State: (${settings."${sPre}${evt}_cmd"})\n" : sBLANK
                        }
                        str += settings."${sPre}${evt}_nums"              ? "    ${sBULLETINV} Button Numbers: ${settings."${sPre}${evt}_nums"}\n" : sBLANK
                        str += settings."${sPre}${evt}_after"              ? "    ${sBULLETINV} Only After: (${settings."${sPre}${evt}_after"} sec)\n" : sBLANK
                        str += settings."${sPre}${evt}_after_repeat"       ? "    ${sBULLETINV} Repeat Every: (${settings."${sPre}${evt}_after_repeat"} sec)\n" : sBLANK
                        str += settings."${sPre}${evt}_after_repeat_cnt"   ? "    ${sBULLETINV} Repeat Count: (${settings."${sPre}${evt}_after_repeat_cnt"})\n" : sBLANK
                        str += (settings."${sPre}${evt}_all" == true)      ? "    ${sBULLETINV} Require All: (${settings."${sPre}${evt}_all"})\n" : sBLANK
                        str += settings."${sPre}${evt}_once"               ? "    ${sBULLETINV} Once a Day: (${settings."${sPre}${evt}_once"})\n" : sBLANK
                        str += settings."${sPre}${evt}_wait"               ? "    ${sBULLETINV} Wait: (${settings."${sPre}${evt}_wait"})\n" : sBLANK
                        str += (settings."${sPre}${evt}_txt" || settings."${sPre}${evt}_after_repeat_txt") ? "    ${sBULLETINV} Custom Responses:\n" : sBLANK
                        str += settings."${sPre}${evt}_txt"                ? "       \u02C3 Events: (${settings."${sPre}${evt}_txt"?.toString()?.tokenize(";")?.size()} Items)\n" : sBLANK
                        str += settings."${sPre}${evt}_after_repeat_txt"   ? "       \u02C3 Repeats: (${settings."${sPre}${evt}_after_repeat_txt"?.toString()?.tokenize(";")?.size()} Items)\n" : sBLANK
                        break
                }
            }
            str += addFoot ? "\n"+sTTM : sBLANK
            return str
        } else { return addFoot ? sTTM : sBLANK }
    } else {
        return addFoot ? spanWrap("Tap to configure (Required!)", sCLRRED, "small") : sBLANK
    }
}

String getConditionsDesc(Boolean addFoot=true) {
    Boolean confd = conditionsConfigured()
//    def time = null
    String sPre = "cond_"
    if(confd) {
        String str = "Conditions Allow Operation: (${(Boolean)conditionStatus().ok ? okSymFLD : notOkSymFLD})\n"
        str += reqAllCond() ?  " \u2022 All Conditions Required\n" : " \u2022 Any Condition Allowed\n"
        if(timeCondConfigured()) {
            str += " • Time Between Allowed: (${timeCondOk() ? okSymFLD : notOkSymFLD})\n"
            str += "    - ${getTimeCondDesc(false)}\n"
        }
        if(dateCondConfigured()) {
            str += " • Date:\n"
            str += settings.cond_days      ? "    - Days Allowed: (${isDayOfWeek(settings.cond_days) ? okSymFLD : notOkSymFLD})\n" : sBLANK
            str += settings.cond_months    ? "    - Months Allowed: (${isMonthOfYear(settings.cond_months) ? okSymFLD : notOkSymFLD})\n"  : sBLANK
        }
        if(settings.cond_alarm || (settings.cond_mode /*&& settings.cond_mode_cmd*/)) {
            str += " • Location: (${locationCondOk() ? okSymFLD : notOkSymFLD})\n"
            str += settings.cond_alarm ? "    - Alarm Modes Allowed: (${isInAlarmMode(settings.cond_alarm) ? okSymFLD : notOkSymFLD})\n" : sBLANK
            Boolean not = settings.cond_mode_cmd == "not"
            str += settings.cond_mode ? "    - Allowed Modes (${not ? "not in" : "in"}): (${(isInMode(settings.cond_mode, not)) ? okSymFLD : notOkSymFLD})\n" : sBLANK
        }

        if(deviceCondConfigured()) {
            [sSWITCH, "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "battery", "humidity", "temperature", "illuminance", "shade", "door", "level", "valve", "water", "power", "thermostatMode", "thermostatOperatingState", "coolingSetpoint", "heatingSetpoint", "thermostatTemperature" ]?.each { String evt->
                if(devCondConfigured(evt)) {
                    Boolean condOk = false
                    if(evt in [sSWITCH, "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "shade", "door", "valve", "water", "thermostatMode", "thermostatOperatingState" ]) { condOk = checkDeviceCondOk(evt) }
                    else if(evt in ["battery", "temperature", "illuminance", "level", "power", "humidity", "coolingSetpoint", "heatingSetpoint", "thermostatTemperature"]) { condOk = checkDeviceNumCondOk(evt) }

                    str += settings."${sPre}${evt}"     ? " • ${evt?.capitalize()} (${settings."${sPre}${evt}"?.size()}) (${condOk ? okSymFLD : notOkSymFLD})\n" : sBLANK
                    def cmd = settings."${sPre}${evt}_cmd" ?: null
                    if(cmd in [sBETWEEN, sBELOW, sABOVE, sEQUALS]) {
                        def cmdLow = settings."${sPre}${evt}_low" ?: null
                        def cmdHigh = settings."${sPre}${evt}_high" ?: null
                        def cmdEq = settings."${sPre}${evt}_equal" ?: null
                        str += (cmd == sEQUALS && cmdEq) ? "    - Value: ( =${cmdEq}${attUnit(evt)})${settings."cond_${inType}_avg" ? "(Avg)" : ""}\n" : sBLANK
                        str += (cmd == sBETWEEN && cmdLow && cmdHigh) ? "    - Value: (${cmdLow-cmdHigh}${attUnit(evt)})${settings."cond_${inType}_avg" ? "(Avg)" : ""}\n" : sBLANK
                        str += (cmd == sABOVE && cmdHigh) ? "    - Value: ( >${cmdHigh}${attUnit(evt)})${settings."cond_${inType}_avg" ? "(Avg)" : ""}\n" : sBLANK
                        str += (cmd == sBELOW && cmdLow) ? "    - Value: ( <${cmdLow}${attUnit(evt)})${settings."cond_${inType}_avg" ? "(Avg)" : ""}\n" : sBLANK
                    } else {
                        str += cmd ? "    - Value: (${cmd})${settings."cond_${inType}_avg" ? "(Avg)" : ""}\n" : sBLANK
                    }
                    str += (settings."${sPre}${evt}_all" == true) ? "    - Require All: (${settings."${sPre}${evt}_all"})\n" : sBLANK
                }
            }
        }
        str += addFoot ? inputFooter(sTTM, sCLRGRY) : sBLANK
        return str
    } else {
        return addFoot ? inputFooter(sTTC, sCLRGRY) : sBLANK
    }
}

static String attUnit(String attr) {
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
            return sBLANK
    }
}

Map getZoneStatus() {
    def echoZones = settings.act_EchoZones ?: []
    def res = [:]
    if(echoZones.size()) {
        def allZones = getZones()
        echoZones.each { k -> if(allZones?.containsKey(k)) { res[k] = allZones[k] } }
    }
    return res
}

String getActionDesc(Boolean addFoot=true) {
    Boolean confd = executionConfigured()
//    String sPre = "act_"
    String str = sBLANK
    str += addFoot ? spanWrap("Action:", sNULL, sNULL, true, true) : sBLANK
    str += addFoot ? spanWrap(" ${sBULLET} "+(String)buildActTypeEnum()."${(String)settings.actionType}", sNULL, sNULL, false, true) : sBLANK
    if((String)settings.actionType && confd) {
        Boolean isTierAct = isTierAction()
        def eDevs = parent?.getDevicesFromList(settings.act_EchoDevices)
        Map zones = getZoneStatus()
        String tierDesc = isTierAct ? getTierRespDesc() : sNULL
        String tierStart = isTierAct ? actTaskDesc("act_tier_start_") : sNULL
        String tierStop = isTierAct ? actTaskDesc("act_tier_stop_") : sNULL
        str += zones?.size() ? "Echo Zones:\n${zones?.collect { " ${sBULLET} ${it?.value?.name} (${it?.value?.active == true ? "Active" : "Inactive"})" }?.join("<br>")}\n${eDevs?.size() ? "\n": ""}" : sBLANK
        str += eDevs?.size() ? "Alexa Devices:${zones?.size() ? " (Inactive Zone default)" : ""}\n${eDevs?.collect { " \u2022 ${it?.displayName?.toString()?.replace("Echo - ", sBLANK)}" }?.join("\n")}\n" : sBLANK
        str += tierDesc ? "\n${tierDesc}${tierStart || tierStop ? sBLANK : "\n"}" : sBLANK
        str += tierStart ? tierStart+"\n" : sBLANK
        str += tierStop ? tierStop+"\n" : sBLANK
        str += settings.act_volume_change != null ? "New Volume: (${settings.act_volume_change})\n" : sBLANK
        str += settings.act_volume_restore != null ? "Restore Volume: (${settings.act_volume_restore})\n" : sBLANK
        str += settings.act_delay ? "Delay: (${settings.act_delay})\n" : sBLANK
        str += (String)settings.actionType in ["speak", "announcement", "speak_tiered", "announcement_tiered"] && settings."act_${(String)settings.actionType}_txt" ? "Using Default Response: (True)\n" : sBLANK
        String trigTasks = !isTierAct ? actTaskDesc("act_") : sNULL
        str += trigTasks ? trigTasks : sBLANK
        str += addFoot ? "\n\n"+sTTM : sBLANK
        // return divWrap(str.replaceAll("\n\n\n", "\n\n"), sCLR4D9, "small")
    }
    str += !confd && addFoot ? spanWrap("Tap to configure (Required!)", sCLRRED, "small") : sBLANK
    return divWrap(str.replaceAll("\n\n\n", "\n\n"), sCLR4D9, "small")
}

String getTimeCondDesc(Boolean addPre=true) {
    Date startTime
    Date stopTime
    String startType = settings.cond_time_start_type
    String stopType = settings.cond_time_stop_type
    if(startType && stopType) {
        startTime = startType == 'time' && settings.cond_time_start ? toDateTime(settings.cond_time_start) : null
        stopTime = stopType == 'time'  && settings.cond_time_stop ? toDateTime(settings.cond_time_stop) : null
    }

    String startLbl1 = sBLANK
    String stopLbl1 = sBLANK
    if(startType in lSUNRISESET || stopType in lSUNRISESET) {
        def sun = getSunriseAndSunset()
        Long lsunset = sun.sunset.time
        Long lsunrise = sun.sunrise.time
        Long startoffset = settings.cond_time_start_offset ? settings.cond_time_start_offset*1000L : 0L
        Long stopoffset = settings.cond_time_stop_offset ? settings.cond_time_stop_offset*1000L : 0L
        if(startType in lSUNRISESET) {
            Long startl = (startType == 'sunrise' ? lsunrise : lsunset) + startoffset
            startTime = new Date(startl)
            startLbl1 = startType.capitalize() + sSPACE + "${startoffset ? "with offset " : sBLANK}"
        }
        if(stopType in lSUNRISESET) {
            Long stopl = (stopType == 'sunrise' ? lsunrise : lsunset) + stopoffset
            stopTime = new Date(stopl)
            stopLbl1 = stopType.capitalize() + sSPACE + "${stopoffset ? "with offset " : sBLANK}"
        }
    }
    String startLbl = startTime ? epochToTime(startTime) : sBLANK
    String stopLbl = stopTime ? epochToTime(stopTime) : sBLANK

    return startLbl && stopLbl ? "${addPre ? "Time Condition:\n" : sBLANK}(${startLbl1}${startLbl} - ${stopLbl1}${stopLbl})" : sTTC
}

static String getInputToStringDesc(inpt, addSpace = null) {
    Integer cnt = 0
    String str = sBLANK
    if(inpt) {
        inpt.sort().each { item ->
            cnt = cnt+1
            str += item ? (((cnt < 1) || (inpt?.size() > 1)) ? "\n      ${item}" : "${addSpace ? "      " : sBLANK}${item}") : sBLANK
        }
    }
    //log.debug "str: $str"
    return (str != sBLANK) ? str : sNULL
}

String randomString(Integer len) {
    def pool = ["a".."z", 0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    // logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}

def getRandomItem(items) {
    def list = new ArrayList<String>()
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()))
}

Boolean showChgLogOk() { return ((Boolean)state.isInstalled && !(Boolean)state.shownChgLog) }

static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/icons/${imgName}.png" }

static String getPublicImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" }

static String sTS(String t, String i = sNULL, Boolean bold=false) { return """<h3>${i ? """<img src="${i}" width="42"> """ : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>""" }
/* """ */

static String s3TS(String t, String st, String i = sNULL, String c=sCLR4D9) { return """<h3 style="color:${c};font-weight: bold">${i ? """<img src="${i}" width="42"> """ : sBLANK} ${t?.replaceAll("\n", "<br>")}</h3>${st ? "${st}" : sBLANK}""" }
/* """ */

static String pTS(String t, String i = sNULL, Boolean bold=true, String color=sNULL) { return "${color ? """<div style="color: $color;">""" : ""}${bold ? "<b>" : ""}${i ? """<img src="${i}" width="42"> """ : ""}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : ""}${color ? "</div>" : ""}" }
/* """ */

static String inputFooter(str, color=sCLR4D9, noBr=false) {
    return "${noBr ? sBLANK : "<br>"}<div style='color: ${color}; font-size: small;font-weight: bold;'>${str}</div>"
}

static String inTS1(String t, String i = sNULL, String color=sNULL, Boolean under=true) { return inTS(t, getAppImg(i), color, under) }
static String inTS(String t, String i = sNULL, String color=sNULL, Boolean under=true) { return """${color ? """<div style="color: $color;">""" : ""}${i ? """<img src="${i}" width="42"> """ : ""} ${under ? "<u>" : ""}${t?.replaceAll("\n", " ")}${under ? "</u>" : ""}${color ? "</div>" : ""}""" }
/* """ */

static String htmlLine(String color=sCLR4D9) { return "<hr style='background-color:${color}; height: 1px; border: 0;'>" }

@Field static final String sLNBRK       = '<br>'
@Field static final String sHFONTSM     = 'font-size: small;'
@Field static final String sHFONTLG     = 'font-size: large;'
@Field static final String sHFONTBLD    = 'font-weight: bold;'


String lineBr(Boolean show=true) {
    return (String) show ? sLINEBR : sBLANK
}

String spanWrap(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) {
    return (String) str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLNBRK : sBLANK}" : sBLANK
}

String spanWrapSm(String str, String clr=sNULL, Boolean bld=false, Boolean br=false) {
    return (String) str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sHFONTSM}${bld ? sHFONTBLD : sBLANK}'" : sBLANK}>${str}</span>${br ? sLNBRK : sBLANK}" : sBLANK
}

String spanWrapImg(String str, String clr=sNULL, String sz=sNULL, String img=sNULL, Boolean bld=false, Boolean br=false) {
    return (String) str ? (img ? "<span><img src='${getAppImg(img)}' width='42'> </span>" : sBLANK) + spanWrap(str, clr, sz, bld, br) : sBLANK
}

static String divWrap(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) {
    return str ? "<div ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</div>${br ? sLNBRK : sBLANK}" : sBLANK
}

static String divWrapSm(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) {
    return str ? "<div ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? sHFONTSM : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</div>${br ? sLNBRK : sBLANK}" : sBLANK
}

static String divWrapImg(String str, String clr=sNULL, String sz=sNULL, String img=sNULL, Boolean bld=false, Boolean br=false) {
    str += img ? "<span><img src='${getAppImg(img)}' width='42'> </span>" : sBLANK
    str += img ? "<span>" + str + "</span>" : sBLANK
    return str ? divWrap(str, clr, sz, bld, br) : sBLANK
}

static String getOkOrNotSymHTML(Boolean ok) {
    return ok ? "<span style='color: #43d843;'>(${okSymFLD})</span>" : "<span style='color: #cc2d3b;'>(${notOkSymFLD})</span>"
}

def appFooter() {
	section() {
		paragraph htmlLine("orange")
		paragraph """<div style='color:orange;text-align:center'>Echo Speaks<br><a href='${textDonateLink()}' target="_blank"><img width="120" height="120" src="https://raw.githubusercontent.com/tonesto7/homebridge-hubitat-tonesto7/master/images/donation_qr.png"></a><br><br>Please consider donating if you find this integration useful.</div>"""
	}
}

static String bulletItem(String inStr, String strVal) { return "${inStr == sBLANK ? sBLANK : "\n"} \u2022 ${strVal}" }
static String dashItem(String inStr, String strVal, Boolean newLine=false) { return "${(inStr == sBLANK && !newLine) ? sBLANK : "\n"} - ${strVal}" }

Integer stateSize() {
    String j = new groovy.json.JsonOutput().toJson(state)
    return j.length()
}

Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0) }
/*
String debugStatus() { return !settings.appDebug ? "Off" : "On" }
String deviceDebugStatus() { return !settings.childDebug ? "Off" : "On" }
Boolean isAppDebug() { return (settings.appDebug == true) }
Boolean isChildDebug() { return (settings.childDebug == true) }

String getAppDebugDesc() {
    String str = sBLANK
    str += isAppDebug() ? "App Debug: (${debugStatus()})" : sBLANK
    str += isChildDebug() && str != sBLANK ? "\n" : sBLANK
    str += isChildDebug() ? "Device Debug: (${deviceDebugStatus()})" : sBLANK
    return (str != sBLANK) ? str : sNULL
}*/

private addToLogHistory(String logKey, String data, Integer max=10) {
    Boolean ssOk = true // (stateSizePerc() > 70)

    Boolean aa = getTheLock(sHMLF, "addToHistory(${logKey})")
    // log.trace "lock wait: ${aa}"

    List<Map> eData = (List<Map>)getMemStoreItem(logKey)
    if(!(eData.find { it?.data == data })) {
        eData.push([dt: getDtNow(), data: data])
        Integer lsiz = eData.size()
        if (!ssOk || lsiz > max) {
            eData = eData.drop((lsiz - max))
        }
        updMemStoreItem(logKey, eData)
    }
    releaseTheLock(sHMLF)
}

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug addHead(msg) } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info sSPACE+addHead(msg) } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace addHead(msg) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn sSPACE+addHead(msg) }; if(!noHist) { addToLogHistory("warnHistory", msg, 15) } }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error addHead(msg)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (e) {
        }
        if(a) log.error addHead(a)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, 15) }
}

static String addHead(String msg) {
    return "Action (v"+appVersionFLD+") | "+msg
}

Map getLogHistory() {
    Boolean aa = getTheLock(sHMLF, "getLogHistory")
    // log.trace "lock wait: ${aa}"

    List warn = (List)getMemStoreItem("warnHistory")
//    warn = warn ?: []
    List errs = (List)getMemStoreItem("errorHistory")
//    errs = errs ?: []

    releaseTheLock(sHMLF)

    return [ warnings: []+warn, errors: []+errs ]
}

private void clearLogHistory() {
//    String appId = app.getId()

    Boolean aa = getTheLock(sHMLF, "clearLogHistory")
    // log.trace "lock wait: ${aa}"

    updMemStoreItem("warnHistory", [])
    updMemStoreItem("errorHistory", [])

    releaseTheLock(sHMLF)
}

@Field volatile static Map<String,Map> historyMapFLD = [:]
// FIELD VARIABLE FUNCTIONS
private void updMemStoreItem(String key, val) {
    String appId = app.getId()
    Map<String, Object> memStore = historyMapFLD[appId] ?: [:]
    memStore[key] = val
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD
    // log.debug("updMemStoreItem(${key}): ${memStore[key]}")
}

private getMemStoreItem(String key, defVal=[]){
    String appId = app.getId()
    Map<String, Object> memStore = historyMapFLD[appId] ?: [:]
    return memStore[key] ?: defVal
}

// Memory Barrier
@Field static java.util.concurrent.Semaphore theMBLockFLD=new java.util.concurrent.Semaphore(0)
static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}

@Field static final String sHMLF = 'theHistMapLockFLD'
@Field static java.util.concurrent.Semaphore histMapLockFLD = new java.util.concurrent.Semaphore(1)

private Integer getSemaNum(String name) {
	if(name==sHMLF) return 0
    log.warn "unrecognized lock name..."
    return 0
	// Integer stripes=22
	// if(name.isNumber()) return name.toInteger()%stripes
	// Integer hash=smear(name.hashCode())
	// return Math.abs(hash)%stripes
    // log.info "sema $name # $sema"
}
java.util.concurrent.Semaphore getSema(Integer snum){
	switch(snum) {
		case 0: return histMapLockFLD
		default: log.error "bad hash result $snum"
			return null
	}
}

@Field volatile static Map<String,Long> lockTimesFLD = [:]
@Field volatile static Map<String,String> lockHolderFLD = [:]

Boolean getTheLock(String qname, String meth=sNULL, Boolean longWait=false) {
    Long waitT = longWait ? 1000L : 60L
    Boolean wait = false
    Integer semaNum = getSemaNum(qname)
    String semaSNum = semaNum.toString()
    def sema = getSema(semaNum)
    while(!((Boolean)sema.tryAcquire())) {
        // did not get the lock
        Long timeL = lockTimesFLD[semaSNum]
        if(timeL == null){
            timeL = now()
            lockTimesFLD[semaSNum] = timeL
            lockTimesFLD = lockTimesFLD
        }
        if(devModeFLD) log.warn "waiting for ${qname} ${semaSNum} lock access, $meth, long: $longWait, holder: ${(String)lockHolderFLD[semaSNum]}"
        pauseExecution(waitT)
        wait = true
        if((now() - timeL) > 30000L) {
            releaseTheLock(qname)
            if(devModeFLD) log.warn "overriding lock $meth"
        }
    }
    lockTimesFLD[semaSNum] = now()
    lockTimesFLD = lockTimesFLD
    lockHolderFLD[semaSNum] = "${app.getId()} ${meth}".toString()
    lockHolderFLD = lockHolderFLD
    return wait
}

void releaseTheLock(String qname){
    Integer semaNum=getSemaNum(qname)
    String semaSNum=semaNum.toString()
    def sema=getSema(semaNum)
    lockTimesFLD[semaSNum]=null
    lockTimesFLD=lockTimesFLD
    lockHolderFLD[semaSNum]=sNULL
    lockHolderFLD=lockHolderFLD
    sema.release()
}

static String convMusicProvider(String prov) {
    switch (prov) {
        case "My Library":
            return "CLOUDPLAYER"
        case "Amazon Music":
            return "AMAZON_MUSIC"
        case "Apple Music":
            return "APPLE_MUSIC"
        case "TuneIn":
            return "TUNEIN"
        case "Pandora":
            return "PANDORA"
        case "SiriusXM":
            return "SIRIUSXM"
        case "Spotify":
            return "SPOTIFY"
        case "Tidal":
            return "TIDAL"
        case "iHeartRadio":
            return "I_HEART_RADIO"
    }
}

/************************************************
            SEQUENCE TEST LOGIC
*************************************************/

def searchTuneInResultsPage() {
    return dynamicPage(name: "searchTuneInResultsPage", uninstall: false, install: false) {
        Map results = parent.executeTuneInSearch((String)settings.tuneinSearchQuery)
        section(sTS("Search Results: (Query: ${(String)settings.tuneinSearchQuery})")) {
            if(results?.browseList && results?.browseList?.size()) {
                results?.browseList?.eachWithIndex { item, i->
                    if(i < 25) {
                        if(item?.browseList != null && item?.browseList?.size()) {
                            item?.browseList?.eachWithIndex { item2, i2->
                                String str = sBLANK
                                str += "ContentType: (${item2?.contentType})"
                                str += "\nId: (${item2?.id})"
                                str += "\nDescription: ${item2?.description}"
                                href "searchTuneInResultsPage", title: pTS(item2?.name?.take(75), item2?.image), description: str, required: true, state: (!item2?.name?.contains("Not Supported") ? sCOMPLT : sNULL)
                            }
                        } else {
                            String str = sBLANK
                            str += "ContentType: (${item?.contentType})"
                            str += "\nId: (${item?.id})"
                            str += "\nDescription: ${item?.description}"
                            href "searchTuneInResultsPage", title: pTS(item?.name?.take(75), item?.image), description: str, required: true, state: (!item?.name?.contains("Not Supported") ? sCOMPLT : null)
                        }
                    }
                }
            } else { paragraph "No Results found..." }
        }
    }
}

private static getColorName(desiredColor, level=null) {
    String desC = desiredColor?.toLowerCase()
    for (color in colorSettingsListFLD) {
        if (color.name?.toLowerCase() == desC) {
            Integer hue = Math.round((Integer)color.h / 3.6)
            level = level ?: color.l
            return [hue: hue, saturation: color.s, level: level]
        }
    }
}

private captureLightState(devs) {
    Map sMap = [:]
    if(devs) {
        devs.each { dev->
            String dId = dev?.id
            sMap[dId] = [:]
            if(dev?.hasAttribute(sSWITCH)) { sMap[dId]?.switch = dev?.currentSwitch }
            if(dev?.hasAttribute("level")) { sMap[dId]?.level = dev?.currentLevel }
            if(dev?.hasAttribute("hue")) { sMap[dId]?.hue = dev?.currentHue }
            if(dev?.hasAttribute("saturation")) { sMap[dId]?.saturation = dev?.currentSaturation }
            if(dev?.hasAttribute("colorTemperature")) { sMap[dId]?.colorTemperature = dev?.currentColorTemperature }
            if(dev?.hasAttribute("color")) { sMap[dId]?.color = dev?.currentColor }
        }
    }
    //ERS
    atomicState.light_restore_map = sMap
    log.debug "sMap: $sMap"
}

private restoreLightState(devs) {
    //ERS
    Map sMap = atomicState.light_restore_map
    if(devs && sMap?.size()) {
        devs.each { dev->
            if(sMap.containsKey(dev.id)) {
                if(sMap?.level) {
                    if(sMap.saturation && sMap.hue) { dev?.setColor([h: sMap.hue, s: sMap.saturation, l: sMap.level]) }
                    else { dev?.setLevel(sMap.level) }
                }
                if(sMap.colorTemperature) { dev?.setColorTemperature(sMap.colorTemperature) }
                if(sMap.switch) { dev?."${sMap.switch}"() }
            }
        }
    }
    state.remove("light_restore_map")
}

@Field static final List colorSettingsListFLD = [
    [name: "Soft White", rgb: "#B6DA7C", h: 83, s: 44, l: 67],              [name: "Warm White", rgb: "#DAF17E",	h: 51, s: 20, l: 100],      [name: "Very Warm White", rgb: "#DAF17E", h: 51, s: 60, l: 51],
    [name: "Daylight White", rgb: "#CEF4FD", h: 191, s: 9, l: 90],          [name: "Daylight", rgb: "#CEF4FD", h: 191, s: 9, l: 90],            [name: "Cool White", rgb: "#F3F6F7", h: 187, s: 19, l: 96],
    [name: "White", rgb: "#FFFFFF", h: 0, s: 0, l: 100],                    [name: "Alice Blue", rgb: "#F0F8FF", h: 208, s: 100, l: 97],        [name: "Antique White", rgb: "#FAEBD7", h: 34, s: 78, l: 91],
    [name: "Aqua", rgb: "#00FFFF", h: 180, s: 100, l: 50],                  [name: "Aquamarine", rgb: "#7FFFD4", h: 160, s: 100, l: 75],        [name: "Azure", rgb: "#F0FFFF", h: 180, s: 100, l: 97],
    [name: "Beige", rgb: "#F5F5DC", h: 60, s: 56, l: 91],                   [name: "Bisque", rgb: "#FFE4C4", h: 33, s: 100, l: 88],             [name: "Blanched Almond", rgb: "#FFEBCD", h: 36, s: 100, l: 90],
    [name: "Blue", rgb: "#0000FF", h: 240, s: 100, l: 50],                  [name: "Blue Violet", rgb: "#8A2BE2", h: 271, s: 76, l: 53],        [name: "Brown", rgb: "#A52A2A", h: 0, s: 59, l: 41],
    [name: "Burly Wood", rgb: "#DEB887", h: 34, s: 57, l: 70],              [name: "Cadet Blue", rgb: "#5F9EA0", h: 182, s: 25, l: 50],         [name: "Chartreuse", rgb: "#7FFF00", h: 90, s: 100, l: 50],
    [name: "Chocolate", rgb: "#D2691E", h: 25, s: 75, l: 47],               [name: "Coral", rgb: "#FF7F50", h: 16, s: 100, l: 66],              [name: "Corn Flower Blue", rgb: "#6495ED", h: 219, s: 79, l: 66],
    [name: "Corn Silk", rgb: "#FFF8DC", h: 48, s: 100, l: 93],              [name: "Crimson", rgb: "#DC143C", h: 348, s: 83, l: 58],            [name: "Cyan", rgb: "#00FFFF", h: 180, s: 100, l: 50],
    [name: "Dark Blue", rgb: "#00008B", h: 240, s: 100, l: 27],             [name: "Dark Cyan", rgb: "#008B8B", h: 180, s: 100, l: 27],         [name: "Dark Golden Rod", rgb: "#B8860B", h: 43, s: 89, l: 38],
    [name: "Dark Gray", rgb: "#A9A9A9", h: 0, s: 0, l: 66],                 [name: "Dark Green", rgb: "#006400", h: 120, s: 100, l: 20],        [name: "Dark Khaki", rgb: "#BDB76B", h: 56, s: 38, l: 58],
    [name: "Dark Magenta", rgb: "#8B008B", h: 300, s: 100, l: 27],          [name: "Dark Olive Green", 	rgb: "#556B2F", h: 82, s: 39, l: 30],   [name: "Dark Orange", rgb: "#FF8C00", h: 33, s: 100, l: 50],
    [name: "Dark Orchid", rgb: "#9932CC", h: 280, s: 61, l: 50],            [name: "Dark Red", rgb: "#8B0000", h: 0, s: 100, l: 27],            [name: "Dark Salmon", rgb: "#E9967A", h: 15, s: 72, l: 70],
    [name: "Dark Sea Green", rgb: "#8FBC8F", h: 120, s: 25, l: 65],         [name: "Dark Slate Blue", rgb: "#483D8B", h: 248, s: 39, l: 39],    [name: "Dark Slate Gray", rgb: "#2F4F4F", h: 180, s: 25, l: 25],
    [name: "Dark Turquoise", rgb: "#00CED1", h: 181, s: 100, l: 41],        [name: "Dark Violet", rgb: "#9400D3", h: 282, s: 100, l: 41],       [name: "Deep Pink", rgb: "#FF1493", h: 328, s: 100, l: 54],
    [name: "Deep Sky Blue", rgb: "#00BFFF", h: 195, s: 100, l: 50],         [name: "Dim Gray", rgb: "#696969", h: 0, s: 0, l: 41],              [name: "Dodger Blue", rgb: "#1E90FF", h: 210, s: 100, l: 56],
    [name: "Fire Brick", rgb: "#B22222", h: 0, s: 68, l: 42],               [name: "Floral White", rgb: "#FFFAF0", h: 40, s: 100, l: 97],       [name: "Forest Green", rgb: "#228B22", h: 120, s: 61, l: 34],
    [name: "Fuchsia", rgb: "#FF00FF", h: 300, s: 100, l: 50],               [name: "Gainsboro", rgb: "#DCDCDC", h: 0, s: 0, l: 86],             [name: "Ghost White", rgb: "#F8F8FF", h: 240, s: 100, l: 99],
    [name: "Gold", rgb: "#FFD700", h: 51, s: 100, l: 50],                   [name: "Golden Rod", rgb: "#DAA520", h: 43, s: 74, l: 49],          [name: "Gray", rgb: "#808080", h: 0, s: 0, l: 50],
    [name: "Green", rgb: "#008000", h: 120, s: 100, l: 25],                 [name: "Green Yellow", rgb: "#ADFF2F", h: 84, s: 100, l: 59],       [name: "Honeydew", rgb: "#F0FFF0", h: 120, s: 100, l: 97],
    [name: "Hot Pink", rgb: "#FF69B4", h: 330, s: 100, l: 71],              [name: "Indian Red", rgb: "#CD5C5C", h: 0, s: 53, l: 58],           [name: "Indigo", rgb: "#4B0082", h: 275, s: 100, l: 25],
    [name: "Ivory", rgb: "#FFFFF0", h: 60, s: 100, l: 97],                  [name: "Khaki", rgb: "#F0E68C", h: 54, s: 77, l: 75],               [name: "Lavender", rgb: "#E6E6FA", h: 240, s: 67, l: 94],
    [name: "Lavender Blush", rgb: "#FFF0F5", h: 340, s: 100, l: 97],        [name: "Lawn Green", rgb: "#7CFC00", h: 90, s: 100, l: 49],         [name: "Lemon Chiffon", rgb: "#FFFACD", h: 54, s: 100, l: 90],
    [name: "Light Blue", rgb: "#ADD8E6", h: 195, s: 53, l: 79],             [name: "Light Coral", rgb: "#F08080", h: 0, s: 79, l: 72],          [name: "Light Cyan", rgb: "#E0FFFF", h: 180, s: 100, l: 94],
    [name: "Light Golden Rod Yellow", rgb: "#FAFAD2", h: 60, s: 80, l: 90], [name: "Light Gray", rgb: "#D3D3D3", h: 0, s: 0, l: 83],            [name: "Light Green", rgb: "#90EE90", h: 120, s: 73, l: 75],
    [name: "Light Pink", rgb: "#FFB6C1", h: 351, s: 100, l: 86],            [name: "Light Salmon", rgb: "#FFA07A", h: 17, s: 100, l: 74],       [name: "Light Sea Green", 	rgb: "#20B2AA", h: 177, s: 70, l: 41],
    [name: "Light Sky Blue", 	rgb: "#87CEFA", h: 203, s: 92, l: 75],      [name: "Light Slate Gray", 	rgb: "#778899", h: 210, s: 14, l: 53],  [name: "Light Steel Blue", 	rgb: "#B0C4DE", h: 214, s: 41, l: 78],
    [name: "Light Yellow", rgb: "#FFFFE0", h: 60, s: 100, l: 94],           [name: "Lime", rgb: "#00FF00", h: 120, s: 100, l: 50],              [name: "Lime Green", rgb: "#32CD32", h: 120, s: 61, l: 50],
    [name: "Linen", rgb: "#FAF0E6", h: 30, s: 67, l: 94],                   [name: "Maroon", rgb: "#800000", h: 0, s: 100, l: 25],              [name: "Medium Aquamarine", rgb: "#66CDAA", h: 160, s: 51, l: 60],
    [name: "Medium Blue", rgb: "#0000CD", h: 240, s: 100, l: 40],           [name: "Medium Orchid", rgb: "#BA55D3", h: 288, s: 59, l: 58],      [name: "Medium Purple", rgb: "#9370DB", h: 260, s: 60, l: 65],
    [name: "Medium Sea Green", 	rgb: "#3CB371", h: 147, s: 50, l: 47],      [name: "Medium Slate Blue", rgb: "#7B68EE", h: 249, s: 80, l: 67],  [name: "Medium Spring Green", rgb: "#00FA9A", h: 157, s: 100, l: 49],
    [name: "Medium Turquoise", 	rgb: "#48D1CC", h: 178, s: 60, l: 55],      [name: "Medium Violet Red", rgb: "#C71585", h: 322, s: 81, l: 43],  [name: "Midnight Blue", rgb: "#191970", h: 240, s: 64, l: 27],
    [name: "Mint Cream", rgb: "#F5FFFA", h: 150, s: 100, l: 98],            [name: "Misty Rose", rgb: "#FFE4E1", h: 6, s: 100, l: 94],          [name: "Moccasin", rgb: "#FFE4B5", h: 38, s: 100, l: 85],
    [name: "Navajo White", rgb: "#FFDEAD", h: 36, s: 100, l: 84],           [name: "Navy", rgb: "#000080", h: 240, s: 100, l: 25],              [name: "Old Lace", rgb: "#FDF5E6", h: 39, s: 85, l: 95],
    [name: "Olive", rgb: "#808000", h: 60, s: 100, l: 25],                  [name: "Olive Drab", rgb: "#6B8E23", h: 80, s: 60, l: 35],          [name: "Orange", rgb: "#FFA500", h: 39, s: 100, l: 50],
    [name: "Orange Red", rgb: "#FF4500", h: 16, s: 100, l: 50],             [name: "Orchid", rgb: "#DA70D6", h: 302, s: 59, l: 65],             [name: "Pale Golden Rod", rgb: "#EEE8AA", h: 55, s: 67, l: 80],
    [name: "Pale Green", rgb: "#98FB98", h: 120, s: 93, l: 79],             [name: "Pale Turquoise", rgb: "#AFEEEE", h: 180, s: 65, l: 81],     [name: "Pale Violet Red", rgb: "#DB7093", h: 340, s: 60, l: 65],
    [name: "Papaya Whip", rgb: "#FFEFD5", h: 37, s: 100, l: 92],            [name: "Peach Puff", rgb: "#FFDAB9", h: 28, s: 100, l: 86],         [name: "Peru", rgb: "#CD853F", h: 30, s: 59, l: 53],
    [name: "Pink", rgb: "#FFC0CB", h: 350, s: 100, l: 88],                  [name: "Plum", rgb: "#DDA0DD", h: 300, s: 47, l: 75],               [name: "Powder Blue", rgb: "#B0E0E6", h: 187, s: 52, l: 80],
    [name: "Purple", rgb: "#800080", h: 300, s: 100, l: 25],                [name: "Red", rgb: "#FF0000", h: 0, s: 100, l: 50],                 [name: "Rosy Brown", rgb: "#BC8F8F", h: 0, s: 25, l: 65],
    [name: "Royal Blue", rgb: "#4169E1", h: 225, s: 73, l: 57],             [name: "Saddle Brown", rgb: "#8B4513", h: 25, s: 76, l: 31],        [name: "Salmon", rgb: "#FA8072", h: 6, s: 93, l: 71],
    [name: "Sandy Brown", rgb: "#F4A460", h: 28, s: 87, l: 67],             [name: "Sea Green", rgb: "#2E8B57", h: 146, s: 50, l: 36],          [name: "Sea Shell", rgb: "#FFF5EE", h: 25, s: 100, l: 97],
    [name: "Sienna", rgb: "#A0522D", h: 19, s: 56, l: 40],                  [name: "Silver", rgb: "#C0C0C0", h: 0, s: 0, l: 75],                [name: "Sky Blue", rgb: "#87CEEB", h: 197, s: 71, l: 73],
    [name: "Slate Blue", rgb: "#6A5ACD", h: 248, s: 53, l: 58],             [name: "Slate Gray", rgb: "#708090", h: 210, s: 13, l: 50],         [name: "Snow", rgb: "#FFFAFA", h: 0, s: 100, l: 99],
    [name: "Spring Green", rgb: "#00FF7F", h: 150, s: 100, l: 50],          [name: "Steel Blue", rgb: "#4682B4", h: 207, s: 44, l: 49],         [name: "Tan", rgb: "#D2B48C", h: 34, s: 44, l: 69],
    [name: "Teal", rgb: "#008080", h: 180, s: 100, l: 25],                  [name: "Thistle", rgb: "#D8BFD8", h: 300, s: 24, l: 80],            [name: "Tomato", rgb: "#FF6347", h: 9, s: 100, l: 64],
    [name: "Turquoise", rgb: "#40E0D0", h: 174, s: 72, l: 56],              [name: "Violet", rgb: "#EE82EE", h: 300, s: 76, l: 72],             [name: "Wheat", rgb: "#F5DEB3", h: 39, s: 77, l: 83],
    [name: "White Smoke", rgb: "#F5F5F5", h: 0, s: 0, l: 96],               [name: "Yellow", rgb: "#FFFF00", h: 60, s: 100, l: 50],             [name: "Yellow Green", rgb: "#9ACD32", h: 80, s: 61, l: 50]
]

//*******************************************************************
//    CLONE CHILD LOGIC
//*******************************************************************
public Map getSettingsAndStateMap() {
    Map typeObj = parent?.getAppDuplTypes()
    Map setObjs = [:]
    if(typeObj) {
        ((Map<String, List<String>>)typeObj.stat).each { sk, sv->
            sv?.each { svi-> if(settings.containsKey(svi)) { setObjs[svi] = [type: sk, value: settings[svi] ] } }
        }
        ((Map<String, List<String>>)typeObj.ends).each { ek, ev->
            ev?.each { evi->
                settings.findAll { it?.key?.endsWith(evi) }?.each { String fk, fv->
                    def vv = settings[fk] // fv
                   if(ek==sTIME) vv = dateTimeFmt(toDateTime(vv), "HH:mm")
                    setObjs[fk] = [type: ek, value: vv]
                }
            }
        }
        ((Map<String,String>)typeObj.caps).each { ck, cv->
            settings.findAll { it.key.endsWith(ck) }?.each { String fk, fv->
                setObjs[fk] = [type: "capability", value: (fv instanceof List ? fv?.collect { it?.id?.toString() } : it?.id?.toString ) ] } //.toString().toList()
        }
        ((Map<String, String>)typeObj.dev).each { dk, dv->
            settings.findAll { it.key.endsWith(dk) }?.each { String fk, fv->
                setObjs[fk] = [type: "device", value: (fv instanceof List ? fv.collect { it?.id?.toString() } : it?.id?.toString() ) ] } //.toString().toList()
        }
    }
    Map data = [:]
    String newlbl = app?.getLabel()?.toString()?.replace(" (A \u275A\u275A)", sBLANK)
    data.label = newlbl?.replace(" (A)", sBLANK)
    data.settings = setObjs

    List stateSkip = [
        /* "isInstalled", "isParent", */
        "lastNotifMsgDt", "lastNotificationMsg", "setupComplete", "valEvtHistory", "warnHistory", "errorHistory",
        "appData", "actionHistory", "authValidHistory", "deviceRefreshInProgress", "noticeData", "installData", "herokuName", "zoneHistory",
        // actions
        "tierSchedActive",
        // zones
        "zoneConditionsOk", "configStatusMap", "tsDtMap", "dateInstalled", "handleGuardEvents", "startTime", "stopTime", "alexaGuardState", "appFlagsMap",

        "dupPendingSetup", "dupOpenedByUser"

    ]
    data.state = state?.findAll { !(it?.key in stateSkip) }
    return data
}
