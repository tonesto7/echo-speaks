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

@Field static final String appVersionFLD  = '4.1.8.0'
@Field static final String appModifiedFLD = '2021-06-07'
@Field static final String platformFLD    = 'Hubitat'
@Field static final String branchFLD      = 'master'
//@Field static final Boolean betaFLD       = false
@Field static final Boolean devModeFLD    = false
@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sBULLET        = '\u2022'
@Field static final String sBULLETINV     = '\u25E6'
//@Field static final String sSQUARE        = '\u29C8'
@Field static final String sPLUS          = '\u002B'
//@Field static final String sRIGHTARR      = '\u02C3'
@Field static final String okSymFLD       = "\u2713"
@Field static final String notOkSymFLD    = "\u2715"
@Field static final String sPAUSESymFLD   = "\u275A\u275A"
@Field static final String sLINEBR        = '<br>'
@Field static final String sFALSE         = 'false'
@Field static final String sTRUE          = 'true'
@Field static final String sBOOL          = 'bool'
@Field static final String sENUM          = 'enum'
@Field static final String sNUMBER        = 'number'
@Field static final String sRESET         = 'reset'
@Field static final String sTEXT          = 'text'
@Field static final String sTIME          = 'time'
@Field static final String sMODE          = 'mode'
@Field static final String sCOMPLT        = 'complete'
@Field static final String sMEDIUM        = 'medium'
@Field static final String sSMALL         = 'small'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
//@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sTTM           = 'Tap to modify...'
@Field static final String sTTC           = 'Tap to configure...'
@Field static final String sTTCR          = 'Tap to configure (Required)'
@Field static final String sTTP           = 'Tap to proceed...'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sCHKBOX        = 'checkbox'
@Field static final String sCOMMAND       = 'command'
@Field static final String sANY           = 'any'
@Field static final String sARE           = 'are'
@Field static final String sBETWEEN       = 'between'
@Field static final String sNBETWEEN      = 'not_between'
@Field static final String sBELOW         = 'below'
@Field static final String sABOVE         = 'above'
@Field static final String sEQUALS        = 'equals'
@Field static final String sANN           = 'announcement'
@Field static final String sSPEAK         = 'speak'
@Field static final String sSPEAKP        = 'speak_parallel'
@Field static final String sSPEAKT        = 'speak_tiered'
@Field static final String sWEATH         = 'weather'
@Field static final String sCSUNRISE      = 'Sunrise'
@Field static final String sCSUNSET       = 'Sunset'
@Field static final String sSTTIME        = 'start_time'
@Field static final String sSPDKNB        = 'speed_knob'
@Field static final String sSWITCH        = 'switch'
@Field static final String sTEMP          = 'temperature'
@Field static final String sTHERMTEMP     = 'thermostatTemperature'
@Field static final String sCOOLSP        = 'coolingSetpoint'
@Field static final String sHEATSP        = 'heatingSetpoint'
@Field static final String sMOTION        = 'motion'
@Field static final String sLEVEL         = 'level'
@Field static final String sBATT          = 'battery'
@Field static final String sTHERM         = 'thermostat'
@Field static final String sHUMID         = 'humidity'
@Field static final String sKEYPAD        = 'keypad'
@Field static final String sLOCK          = 'lock'
@Field static final String sCONTACT       = 'contact'
@Field static final String sWATER         = 'water'
@Field static final String sPOWER         = 'power'
@Field static final String sVALVE         = 'valve'
@Field static final String sCHGTO         = 'changes to'
@Field static final String sTTS           = 'TTS'
@Field static final String sQUES          = 'question'
@Field static final String sDELAYT        = 'delay_time'
@Field static final String sEQ            = 'equal'
@Field static final String sPUSHED        = 'pushed'
@Field static final String sRELEASED      = 'released'
@Field static final String sHELD          = 'held'
@Field static final String sDBLTAP        = 'doubleTapped'
@Field static final String sALRMSYSST     = 'alarmSystemStatus'
@Field static final String sPISTNEXEC     = 'pistonExecuted'
@Field static final List<String> lONOFF        = ['on', 'off']
@Field static final List<String> lANY          = ['any']
@Field static final List<String> lOPNCLS       = ['open', 'closed']
@Field static final List<String> lACTINACT     = ['active', 'inactive']
@Field static final List<String> lSUNRISESET   = ['sunrise', 'sunset']
@Field static final List<String> lWETDRY       = ['wet', 'dry']
@Field static final List<String> lLOCKUNL      = ['locked', 'unlocked']
@Field static final List<String> lDETECTCLR    = ['detected', 'clear', 'tested']
@Field static final List<String> lPRES         = ['present', 'not present']
@Field static final List<String> lSEC          = ['disarmed', 'armed home', 'armed away', 'unknown']

static String appVersion()  { return appVersionFLD }

definition(
    name: 'Echo Speaks - Actions',
    namespace: 'tonesto7',
    author: 'Anthony Santilli',
    description: 'DO NOT INSTALL FROM MARKETPLACE\n\nAllows you to create echo device actions based on device/location events in your home.',
    category: 'My Apps',
    parent: 'tonesto7:Echo Speaks',
    iconUrl: 'https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png',
    iconX2Url: 'https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png',
    iconX3Url: 'https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_actions.png',
    importUrl  : 'https://raw.githubusercontent.com/tonesto7/echo-speaks/master/apps/echo-speaks-actions.groovy')

preferences {
    page(name: 'startPage')
    page(name: 'uhOhPage')
    page(name: 'codeUpdatePage')
    page(name: 'mainPage')
    page(name: 'prefsPage')
    page(name: 'triggersPage')
    page(name: 'conditionsPage')
    page(name: 'condTimePage')
    page(name: 'actionsPage')
    page(name: 'actionTiersPage')
    page(name: 'actionTiersConfigPage')
    page(name: 'actTrigTasksPage')
    page(name: 'actTierStartTasksPage')
    page(name: 'actTierStopTasksPage')
    page(name: 'actNotifPage')
    page(name: 'actNotifTimePage')
    page(name: 'actionHistoryPage')
    page(name: 'searchTuneInResultsPage')
    page(name: 'uninstallPage')
    page(name: 'namePage')
}

def startPage() {
    if(parent != null) {
        if(!(Boolean)state.isInstalled && !(Boolean)parent?.childInstallOk()) { return uhOhPage() }
        else {
            updDeviceInputs()
            return (minVersionFailed()) ? codeUpdatePage() : mainPage() }
    } else { return uhOhPage() }
}

def codeUpdatePage() {
    return dynamicPage(name: "codeUpdatePage", title: "Update is Required", install: false, uninstall: false) {
        section() { paragraph spanSmBld("Looks like your Action App needs an update<br><br>Please make sure all app and device code is updated to the most current version<br><br>Once updated your actions will resume normal operation.", sCLRRED) }
    }
}

def uhOhPage() {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            paragraph spanBld("HOUSTON WE HAVE A PROBLEM!<br><br>Echo Speaks - Actions can't be directly installed from the Marketplace.<br><br>Please use the Echo Speaks SmartApp to configure them.", sCLRRED)
        }
    }
}

def appInfoSect() {
    String instDt = state.dateInstalled ? fmtTime((String)state.dateInstalled, "MMM dd '@' h:mm a", true) : sNULL
    String str = spanBldBr((String)app.name, "black", "es_actions") + spanSmBld("Version: ") + spanSmBr(appVersionFLD)
    str += instDt ? spanSmBld("Installed: ") + spanSmBr(instDt) : sBLANK
    section() { paragraph divSm(str, sCLRGRY) }
}
/*
List cleanedTriggerList() {
    List newList = []
    settings.triggerTypes?.each { String tr ->
        newList.push(tr.split("::")[0] as String)
    }
    return newList.unique()
}

String selTriggerTypes(type) {
    return settings.triggerTypes?.findAll { it?.startsWith(type as String) }?.collect { it?.toString()?.split("::")[1] }?.join(", ")
} */

private Map buildTriggerEnum() {
    Map<String,Map> buildItems = [:]
    buildItems["Date/Time"] = ["scheduled":"Scheduled Time"]?.sort{ it?.key }
    buildItems["Location"] = [(sMODE):"Modes", (sPISTNEXEC):"Pistons"]?.sort{ it?.key }
    if(!settings.enableWebCoRE) {
        buildItems.Location.remove(sPISTNEXEC)
    }
    buildItems["Sensor Devices"] = [(sCONTACT):"Contacts | Doors | Windows", (sBATT):"Battery Level", (sMOTION):"Motion", "illuminance": "Illuminance/Lux", "presence":"Presence", (sTEMP):"Temperature", (sHUMID):"Humidity", (sWATER):"Water", (sPOWER):"Power", "acceleration":"Accelerometers"]?.sort{ it?.value }
    buildItems["Actionable Devices"] = [(sLOCK):"Locks", "securityKeypad":"Keypads", (sSWITCH):"Switches/Outlets", (sLEVEL):"Dimmers/Level", "door":"Garage Door Openers", (sVALVE):"Valves", "windowShade":"Window Shades"]?.sort{ it?.value }
    buildItems["Thermostat Devices"] = [(sCOOLSP):"Thermostat Cooling Setpoint", (sHEATSP):"Thermostat Heating Setpoint", (sTHERMTEMP):"Thermostat Ambient Temp", "thermostatOperatingState":"Thermostat Operating State", "thermostatMode":"Thermostat Mode", "thermostatFanMode":"Thermostat Fan Mode"]?.sort{ it?.value }
    buildItems["Button Devices"] = [(sPUSHED):"Button (Pushable)", (sRELEASED):"Button (Releasable)", (sHELD):"Button (Holdable)", (sDBLTAP):"Button (Double Tapable)"]?.sort{ it?.value }
// TODO siren (capability.alarm, attr alarm - ENUM ["strobe", "off", "both", "siren"]
    buildItems["Safety & Security"] = [(sALRMSYSST): "${getAlarmSystemName()}", "smoke":"Fire/Smoke", "carbonMonoxide":"Carbon Monoxide", "guard":"Alexa Guard"]?.sort{ it?.value }
    if(!parent?.guardAutoConfigured()) { buildItems["Safety & Security"]?.remove("guard") }
    return buildItems.collectEntries { it?.value }?.sort { it?.value }
}

private static Map buildActTypeEnum() {
    Map<String, Map> buildItems = [:]
    buildItems["Speech"] = [(sSPEAK):"Speak", (sANN):"Announcement", (sSPEAKP):"Speak (Parallel)", (sSPEAKT):"Speak (Tiered)", "speak_parallel_tiered":"Speak Parallel (Tiered)", "announcement_tiered":"Announcement (Tiered)"]?.sort{ it?.key }
    buildItems["Built-in Sounds"] = ["sounds":"Play a Sound"]?.sort{ it?.key }
    buildItems["Built-in Responses"] = [(sWEATH):"Weather Report", "builtin":"Birthday, Compliments, Facts, Jokes, News, Stories, Traffic, and more...", "calendar":"Read Calendar Events"]?.sort{ it?.key }
    buildItems["Media/Playback"] = ["music":"Play Music/Playlists", "playback":"Playback/Volume Control"]?.sort{ it?.key }
    buildItems["Alarms/Reminders"] = ["alarm":"Create Alarm", "reminder":"Create Reminder"]?.sort{ it?.key }
    buildItems["Devices Settings"] = ["wakeword":"Change Wake Word", "dnd":"Set Do Not Disturb", "bluetooth":"Bluetooth Control"]?.sort{ it?.key }
    buildItems["Custom"] = ["voicecmd":"Execute a voice command","sequence":"Execute Sequence", "alexaroutine": "Execute Alexa Routine(s)"]?.sort{ it?.key }
    return buildItems.collectEntries { it?.value }?.sort { it?.value }
}

def mainPage() {
    Boolean newInstall = !(Boolean)state.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? sBLANK : "namePage"), uninstall: newInstall, install: !newInstall) {
        Boolean dup = ((Boolean)settings.duplicateFlag && (Boolean)state.dupPendingSetup)
        if(dup) {
            state.dupOpenedByUser = true
            section() { paragraph spanBld("This Action was created from an existing action.<br><br>Please review the settings and save to activate...<br>${state.badMode ?: sBLANK}", sCLRORG, "pause_orange") }
        }

        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
            webCoRE_poll()
        }
        appInfoSect()
        Boolean paused = isPaused()
        Boolean trigConf
        //Boolean condConf
        Boolean actConf
        Boolean allOk

        if(paused) {
            section() {
                paragraph spanSmBld("This Action is currently in a paused state...<br>To edit the please un-pause", sCLRRED, "pause_orange")
            }
        } else {
            section() { paragraph divSm(getOverallDesc(), sCLRGRY) }

            if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", sARE, sENUM) }
            trigConf = triggersConfigured()
            //condConf = conditionsConfigured()
            actConf = executionConfigured()
            allOk = (Boolean) ((String)settings.actionType && trigConf && actConf)
            section(sectHead("Configuration: Part 1")) {
                input "actionType", sENUM, title: inTS1("Action Type", "list"), description: sBLANK, options: buildActTypeEnum(), multiple: false, required: true, submitOnChange: true
            }
            if (newInstall) {
                section(sectHead("Configuration: Part 2")) {
                    paragraph spanBld("Further Options will be configured once you save this automation.<br>Please save and return to complete", sNULL, getAppImg("info"))
                }
            } else {
                section (sectHead("Configuration: Part 2")) {
                    if((String)settings.actionType) {
                        href "triggersPage", title: inTS1("Action Triggers", "trigger"), description: spanSm(getTriggersDesc(), sCLR4D9)
                    } else { paragraph spanBld("These options will be shown once the action type is configured.", sNULL, getAppImg("info")) }
                }
                section(sectHead("Configuration: Part 3")) {
                    if((String)settings.actionType && trigConf) {
                        href "conditionsPage", title: inTS1("Conditions/Restrictions", "conditions") + optPrefix(), description: divSm(getConditionsDesc(true), sCLR4D9)
                    } else { paragraph spanBld("These options will be shown once the triggers are configured.", sNULL, getAppImg("info")) }
                }
                section(sectHead("Configuration: Part 4")) {
                    if((String)settings.actionType && trigConf) {
                        href "actionsPage", title: inTS1("Execution Config", "es_actions"), description: spanSm(getActionDesc(true), sCLR4D9)
                    } else { paragraph spanBld("These options will be shown once the triggers are configured.", sNULL, getAppImg("info")) }
                }
                if(allOk) {
                    section(sectHead("Notifications:")) {
                        String t0 = getAppNotifDesc()
                        href "actNotifPage", title: inTS1("Send Notifications", "notification2"), description: t0 ? divSm(t0 + inputFooter(sTTM), sCLR4D9) : inactFoot(sTTC)
                    }

                    getTierStatusSection()

                    section(sectHead("Action History")) {
                        href "actionHistoryPage", title: inTS1("View Action History", "tasks"), description: sBLANK
                    }
                }
            }
        }

        section(sectHead("Preferences")) {
            href "prefsPage", title: inTS1("Debug/Preferences", "settings"), description: sBLANK
            if(!newInstall) {
                input "actionPause", sBOOL, title: inTS1("Pause Action?", "pause_orange"), defaultValue: false, submitOnChange: true
                if((Boolean)settings.actionPause) { unsubscribe() }
                else {
                    input "actTestRun", sBOOL, title: inTS1("Test this action?", "testing"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                    if((Boolean)settings.actTestRun) { executeActTest() }
                }
            }
        }

        if(!newInstall) {
            section(sectHead("Name this Action:")) {
                input "appLbl", sTEXT, title: inTS1("Action Name", "name_tag"), description: sBLANK, required:true, submitOnChange: true
            }
            section(sectHead("Remove Action:")) {
                href "uninstallPage", title: inTS1("Remove this Action", "uninstall"), description: inputFooter("Tap to Remove...", sCLRGRY, true)
            }
            if(allOk) {
                section(sectHead("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                    String issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28ACTIONS+BUG%29+&projects=echo-speaks%2F6"
                    String featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BActions+Feature+Request%5D&projects=echo-speaks%2F6"
                    href url: featUrl, style: sEXTNRL, required: false, title: inTS1("New Feature Request", "www"), description: inactFoot("Tap to open browser")
                    href url: issueUrl, style: sEXTNRL, required: false, title: inTS1("Report an Issue", "www"), description: inactFoot("Tap to open browser")
                }
            }
        }
    }
}

def prefsPage() {
    return dynamicPage(name: "prefsPage", install: false, uninstall: false) {
        section(sectHead("Logging:")) {
            input "logInfo",  sBOOL, title: inTS1("Show Info Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logWarn",  sBOOL, title: inTS1("Show Warning Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logError", sBOOL, title: inTS1("Show Error Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logDebug", sBOOL, title: inTS1("Show Debug Logs?", sDEBUG), required: false, defaultValue: false, submitOnChange: true
            input "logTrace", sBOOL, title: inTS1("Show Detailed Logs?", sDEBUG), required: false, defaultValue: false, submitOnChange: true
        }
        if((Boolean)state.isInstalled) {
            if(advLogsActive()) { logsEnabled() }
            section(sectHead("Other:")) {
                input "clrEvtHistory", sBOOL, title: inTS1("Clear Device Event History?", "reset"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.clrEvtHistory) { clearEvtHistory() }
            }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section(sectHead("Name this Automation:")) {
            input "appLbl", sTEXT, title: inTS1("Label this Action", "name_tag"), description: sBLANK, required:true, submitOnChange: true
        }
    }
}

def actionHistoryPage() {
    return dynamicPage(name: "actionHistoryPage", title: "Action History", install: false, uninstall: false) {
        section() {
            getActionHistory()
        }
        if( ((List)getMemStoreItem("actionHistory")).size() ) {
            section(sBLANK) {
                input "clearActionHistory", sBOOL, title: inTS1("Clear Action History?", sRESET), description: spanSm("Clears Stored Action History.", sCLRGRY), defaultValue: false, submitOnChange: true
                if((Boolean)settings.clearActionHistory) {
                    settingUpdate("clearActionHistory", sFALSE, sBOOL)
                    clearActHistory()
                }
            }
        }
    }
}
/*
// TODO: Add flag to check for the old schedule settings and pause the action, and notify the user.
private scheduleConvert() {
    if(settings.trig_scheduled_time || settings.trig_scheduled_sunState && !settings.trig_scheduled_type) {
        if(settings.trig_scheduled_sunState) { settingUpdate("trig_scheduled_type", "${settings.trig_scheduled_sunState}", sENUM); settingRemove("trig_scheduled_sunState") }
        else if(settings.trig_scheduled_time && settings.trig_scheduled_recurrence) {
            if(settings.trig_scheduled_recurrence == "Once") { settingUpdate("trig_scheduled_type", "One-Time", sENUM) }
            if(settings.trig_scheduled_recurrence in ["Daily", "Weekly", "Monthly"]) { settingUpdate("trig_scheduled_type", "Recurring", sENUM) }
        }
    }
} */

def triggersPage() {
    return dynamicPage(name: "triggersPage", nextPage: "mainPage", title: "Action Triggers", uninstall: false, install: false) {
//        Boolean isTierAct = isTierAction()
        String a = getTriggersDesc(false, false)
        if(a) {
            section() {
                paragraph spanSm(a, sCLR4D9)
            }
        }
        section (sectHead("Enable webCoRE Integration:")) {
            input "enableWebCoRE", sBOOL, title: inTS1("Enable webCoRE Integration", webCore_icon()), required: false, defaultValue: false, submitOnChange: true
        }
        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
            webCoRE_poll()
        }
        Boolean showSpeakEvtVars = false
        section (sectHead("Select Capabilities")) {
            input "triggerEvents", sENUM, title: inTS1("Select Trigger Event(s)", "trigger"), options: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true
        }
        Integer trigEvtCnt = ((List)settings.triggerEvents)?.size()
        if (trigEvtCnt) {
            Boolean fnd = false
            //if( "scheduled" in (List<String>)settings.triggerEvents ||
            //       sWEATH in (List<String>)settings.triggerEvents) { fnd = true }
            /*((List<String>)settings.triggerEvents)?.each { String tr ->
                if(tr in ["scheduled", sWEATH]) { fnd = true }
            } */
            //if(!((List)settings.triggerEvents in ["scheduled", sWEATH])) { showSpeakEvtVars = true }
            if(!fnd) { showSpeakEvtVars = true }

            if (valTrigEvt("scheduled")) {
                section(sectHead("Time Based Events"), hideable: true) {
                    List schedTypes = ["One-Time", "Recurring", sCSUNRISE, sCSUNSET]
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
                                                input "trig_scheduled_weekdays", sENUM, title: inTS1("Only of these Days of the Week?", "day_calendar") + optPrefix(), description: sBLANK, multiple: true, required: false, submitOnChange: true, options: daysOfWeekMap()
                                                break

                                            case "Weekly":
                                                input "trig_scheduled_weekdays", sENUM, title: inTS1("Days of the Week?", "day_calendar") + optPrefix(), description: sBLANK, multiple: true, required: true, submitOnChange: true, options: daysOfWeekMap()
                                                input "trig_scheduled_weeks", sENUM, title: inTS1("Only these Weeks on the Month?", "day_calendar") + optPrefix(), description: sBLANK, multiple: true, required: false, submitOnChange: true, options: weeksOfMonthMap()
                                                input "trig_scheduled_months", sENUM, title: inTS1("Only on these Months?", "day_calendar") + optPrefix(), description: sBLANK, multiple: true, required: false, submitOnChange: true, options: monthMap()
                                                break

                                            case "Monthly":
                                                input "trig_scheduled_daynums", sENUM, title: inTS1("Days of the Month?", "day_calendar"), description: (!settings.trig_scheduled_weeks ? optPrefix(): sBLANK), multiple: true, required: (!settings.trig_scheduled_weeks), submitOnChange: true, options: (1..31)?.collect { it as String }
                                                if(!settings.trig_scheduled_daynums) {
                                                    input "trig_scheduled_weeks", sENUM, title: inTS1("Weeks of the Month?", "day_calendar"), description: (!settings.trig_scheduled_daynums ? optPrefix(): sBLANK), multiple: true, required: (!settings.trig_scheduled_daynums), submitOnChange: true, options: weeksOfMonthMap()
                                                }
                                                input "trig_scheduled_months", sENUM, title: inTS1("Only on these Months?", "day_calendar"), description: optPrefix(), multiple: true, required: false, submitOnChange: true, options: monthMap()
                                                break
                                        }
                                    }
                                }
                                break
                            case sCSUNRISE:
                            case sCSUNSET:
                                input "trig_scheduled_sunState_offset", sNUMBER, range: "*..*", title: inTS1("Offset ${schedType} this number of minutes (+/-)", schedType?.toLowerCase()), required: false
                                break
                        }
                        triggerMsgInput("scheduled")
                    }
                }
            }

            if (valTrigEvt(sALRMSYSST)) {
                section (sectHead("${getAlarmSystemName()} (${getAlarmSystemName(true)}) Events"), hideable: true) {
                    String inT = sALRMSYSST
                    input "trig_${inT}", sENUM, title: inTS1("${getAlarmSystemName()} Modes", "alarm_home"), options: getAlarmTrigOpts(), multiple: true, required: true, submitOnChange: true
                    if("alerts" in (List)settings.trig_alarmSystemStatus) {
                        input "trig_${inT}_events", sENUM, title: inTS1("${getAlarmSystemName()} Alert Events", "alarm_home"), options: getAlarmSystemAlertOptions(), multiple: true, required: true, submitOnChange: true
                    }
                    if((List)settings.trig_alarmSystemStatus) {
                        input "trig_${inT}_once", sBOOL, title: inTS1("Only alert once a day? (per type: mode)", sQUES), required: false, defaultValue: false, submitOnChange: true
                        input "trig_${inT}_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)", sDELAYT) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                        triggerMsgInput(sALRMSYSST)
                    }
                }
            }

            if (valTrigEvt("guard")) {
                section (sectHead("Alexa Guard Events"), hideable: true) {
                    input "trig_guard", sENUM, title: inTS1("Alexa Guard Modes", "alarm_home"), options: ["ARMED_STAY", "ARMED_AWAY", sANY], multiple: true, required: true, submitOnChange: true
                    if(settings.trig_guard) {
                        // input "trig_guard_once", sBOOL, title: inTS1("Only alert once a day?\n(per mode)", sQUES), required: false, defaultValue: false, submitOnChange: true
                        // input "trig_guard_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)\n(Optional)", sDELAYT), required: false, defaultValue: null, submitOnChange: true
                        triggerMsgInput("guard")
                    }
                }
            }

            if (valTrigEvt(sMODE)) {
                section (sectHead("Mode Events"), hideable: true) {
                    input "trig_mode", sMODE, title: inTS1("Location Modes", sMODE), multiple: true, required: true, submitOnChange: true
                    if((List)settings.trig_mode) {
                        input "trig_mode_once", sBOOL, title: inTS1("Only alert once a day? (per type: mode)", sQUES), required: false, defaultValue: false, submitOnChange: true
                        input "trig_mode_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)", sDELAYT) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                        triggerMsgInput(sMODE)
                    }
                }
            }

            if(valTrigEvt(sPISTNEXEC)) {
                section(sectHead("webCoRE Piston Executed Events"), hideable: true) {
                    String inT = sPISTNEXEC
                    input "trig_${inT}", sENUM, title: inTS1("Pistons", webCore_icon()), options: webCoRE_list(), multiple: true, required: true, submitOnChange: true
                    if(settings."trig_${inT}") {
                        paragraph pTS("webCoRE settings must be enabled to send events for Piston Execution (not enabled by default in webCoRE)", sNULL, false, sCLRGRY)
                        input "trig_${inT}_once", sBOOL, title: inTS1("Only alert once a day?\n(per type: piston)", sQUES), required: false, defaultValue: false, submitOnChange: true
                        input "trig_${inT}_wait", sNUMBER, title: inTS1("Wait between each report (in seconds)", sDELAYT) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                        triggerMsgInput(sPISTNEXEC)
                    }
                }
            }

            if (valTrigEvt(sSWITCH)) {
                trigNonNumSect(sSWITCH, sSWITCH, "Switches", "Switches", lONOFF+lANY, "are turned", lONOFF, sSWITCH)
            }

            if (valTrigEvt(sLEVEL)) {
                trigNumValSect(sLEVEL, "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level is", sSPDKNB)
            }

            if (valTrigEvt(sBATT)) {
                trigNumValSect(sBATT, sBATT, "Battery Level", "Batteries", "Level is", sSPDKNB)
            }

            if (valTrigEvt(sMOTION)) {
                trigNonNumSect(sMOTION, "motionSensor", "Motion Sensors", "Motion Sensors", lACTINACT+lANY, "become", lACTINACT, sMOTION)
            }

            if (valTrigEvt("presence")) {
                trigNonNumSect("presence", "presenceSensor", "Presence Sensors", "Presence Sensors", lPRES +lANY, sCHGTO, lPRES, "presence")
            }

            if (valTrigEvt(sCONTACT)) {
                trigNonNumSect(sCONTACT, "contactSensor", "Contacts, Doors, Windows", "Contacts, Doors, Windows", lOPNCLS+lANY, sCHGTO, lOPNCLS, sCONTACT)
            }

            if (valTrigEvt("acceleration")) {
                trigNonNumSect("acceleration", "accelerationSensor", "Accelerometers", "Accelerometers", lACTINACT+lANY, sCHGTO, lACTINACT, "acceleration")
            }

            if (valTrigEvt("door")) {
                trigNonNumSect("door", "garageDoorControl", "Garage Door Openers", "Garage Doors", lOPNCLS+["opening", "closing", sANY], sCHGTO, lOPNCLS, "garage_door")
            }

            if (valTrigEvt(sLOCK)) {
                trigNonNumSect(sLOCK, sLOCK, "Locks", "Smart Locks", lLOCKUNL + lANY, sCHGTO, lLOCKUNL, sLOCK, (!!(List)settings.trig_lock_Codes), (((String)settings.trig_lock && (String)settings.trig_lock_cmd in ["unlocked", sANY])  ? this.&handleCodeSect : this.&dummy), "Unlocked" )
            }

            if (valTrigEvt("securityKeypad")) {
                trigNonNumSect("securityKeypad", "securityKeypad", "Security Keypad", "Security Keypad", lSEC + lANY, sCHGTO, lSEC, sKEYPAD, (!!(List)settings.trig_securityKeypad_Codes), (((String)settings.trig_securityKeypad && (String)settings.trig_securityKeypad_cmd in ["disarmed", sANY]) ? this.&handleCodeSect : this.&dummy), "Keypad Disarmed" )
            }

            if (valTrigEvt(sPUSHED)) {
                trigButtonSect(sPUSHED, "pushableButton", "Button Pushed Events", "Pushable Buttons", "button_push", sPUSHED)
            }

            if (valTrigEvt(sRELEASED)) {
                trigButtonSect(sRELEASED, "releasableButton", "Button Released Events", "Releasable Buttons", "button_released", sRELEASED)
            }

            if (valTrigEvt(sHELD)) {
                trigButtonSect(sHELD, "holdableButton", "Button Held Events", "Holdable Buttons", "button_held", sHELD)
            }

            if (valTrigEvt(sDBLTAP)) {
                trigButtonSect(sDBLTAP, "doubleTapableButton", "Button Double Tap Events", "Double Tap Buttons", "button_double", sDBLTAP)
            }

            if (valTrigEvt(sTEMP)) {
                trigNumValSect(sTEMP, "temperatureMeasurement", "Temperature Sensor", "Temperature Sensors", "Temperature", sTEMP)
            }

            if (valTrigEvt(sHUMID)) {
                trigNumValSect(sHUMID, "relativeHumidityMeasurement", "Humidity Sensors", "Relative Humidity Sensors", "Relative Humidity (%)", sHUMID)
            }

            if (valTrigEvt(sWATER)) {
                trigNonNumSect(sWATER, "waterSensor", "Water Sensors", "Water/Moisture Sensors", lWETDRY + lANY, sCHGTO, lWETDRY, sWATER)
            }

            if (valTrigEvt(sPOWER)) {
                trigNumValSect(sPOWER, "powerMeter", "Power Events", "Power Meters", "Power Level (W)", sPOWER)
            }

            if (valTrigEvt("carbonMonoxide")) {
                trigNonNumSect("carbonMonoxide", "carbonMonoxideDetector", "Carbon Monoxide Events", "Carbon Monoxide Detectors", lDETECTCLR+lANY, sCHGTO, lDETECTCLR, "co")
            }

            if (valTrigEvt("smoke")) {
                trigNonNumSect("smoke", "smokeDetector", "Smoke Detector Events", "Smoke Detectors", lDETECTCLR+lANY, sCHGTO, lDETECTCLR, "smoke")
            }

            if (valTrigEvt("illuminance")) {
                trigNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Events", "Illuminance Sensors", "Lux Level (%)", "illuminance")
            }

            if (valTrigEvt("windowShade")) {
                trigNonNumSect("windowShade", "windowShade", "Window Shades", "Window Shades", lOPNCLS+["opening", "closing", sANY], sCHGTO, lOPNCLS, "shade")
            }

            if (valTrigEvt(sVALVE)) {
                trigNonNumSect(sVALVE, sVALVE, "Valves", "Valves", lOPNCLS+lANY, sCHGTO, lOPNCLS, sVALVE)
            }

            if (valTrigEvt(sCOOLSP)) {
                trigNumValSect(sCOOLSP, sTHERM, "Thermostat Cooling Setpoint Events", "Thermostat (Cooling Setpoint)", "Setpoint temp", sTHERM)
            }

            if (valTrigEvt(sHEATSP)) {
                trigNumValSect(sHEATSP, sTHERM, "Thermostat Heating Setpoint Events", "Thermostat (Heating Setpoint)", "Setpoint temp", sTHERM)
            }

            if (valTrigEvt(sTHERMTEMP)) {
                trigNumValSect(sTHERMTEMP, sTHERM, "Thermostat Ambient Temperature Events", "Thermostat (Ambient Temperature)", "Ambient Temp", sTHERM)
            }

            if (valTrigEvt("thermostatOperatingState")) {
                trigNonNumSect("thermostatOperatingState", sTHERM, "Thermostat Operating State Events", "Thermostat (Operating State)", getThermOperStOpts()+lANY, sCHGTO, getThermOperStOpts(), sTHERM)
            }

            if (valTrigEvt("thermostatMode")) {
                trigNonNumSect("thermostatMode", sTHERM, "Thermostat Mode Events", "Thermostat (Mode)", getThermModeOpts()+lANY, sCHGTO, getThermModeOpts(), sTHERM)
            }

            if (valTrigEvt("thermostatFanMode")) {
                trigNonNumSect("thermostatFanMode", sTHERM, "Thermostat Fan Mode Events", "Thermostat (Fan Mode)", getThermFanOpts()+lANY, sCHGTO, getThermFanOpts(), sTHERM)
            }

            if(triggersConfigured()) {
                section(sBLANK) {
                    paragraph spanMdBldBr("Trigger Configuration Complete", sNULL, getAppImg("done")) + spanSm("Press <b>Next</b> to Return to Main Page")
                }
            }
        }
        state.showSpeakEvtVars = showSpeakEvtVars
    }
}

def handleCodeSect(String typ, String lbl) {
    Map<String, Map> lockCodes = getCodes((List)settings."trig_${typ}")
    //log.debug "lockCodes: ${lockCodes}"
    if(lockCodes) {
        Map codeOpts = lockCodes.collectEntries { [((String)it.key): it.value?.name ? "Name: "+ (String)it.value.name : "Code Number ${(String)it.key}: (${(String)it.value?.code})"] }
        input "trig_${typ}_Codes", sENUM, title: inTS1("Filter ${lbl} codes...", sCOMMAND), options: codeOpts, multiple: true, required: false, submitOnChange: true
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
            if(devModeFLD) log.debug "lockCodes: ${result}"
        }
    } catch(ex) { logError("getCodes error", true, ex) }
    return result
}

@SuppressWarnings('unused')
def dummy(a,b) {}

def trigNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, cmdAfterOpts, String image, Boolean devReq=true, Closure extraMeth=this.&dummy, String extraStr=sNULL) {
    //Boolean done = false
    section (sectHead(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: spanSmBld(devTitle, sNULL, image), multiple: true, required: devReq, submitOnChange: true
        if (settings."trig_${inType}") {
            input "trig_${inType}_cmd", sENUM, title: spanSmBld("${cmdTitle}...", sNULL, sCOMMAND), options: cmdOpts, multiple: false, required: true, submitOnChange: true
            if((String)settings."trig_${inType}_cmd") {
                //done=true
                if (settings."trig_${inType}"?.size() > 1 && (String)settings."trig_${inType}_cmd" != sANY) {
                    input "trig_${inType}_all", sBOOL, title: spanSmBld("Require ALL ${devTitle} to be (${settings."trig_${inType}_cmd"})?", sNULL, sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                }
                extraMeth(inType, extraStr)

                if(!isTierAction() && (String)settings."trig_${inType}_cmd" in cmdAfterOpts) {
//                    if(!(Boolean)settings."trig_${inType}_once" && !(Integer)settings."trig_${inType}_wait") {
                    input "trig_${inType}_after", sNUMBER, title: spanSmBld("Only after (${settings."trig_${inType}_cmd"}) for (xx) (0..7200) seconds?", sNULL, sDELAYT), required: false, defaultValue: null, submitOnChange: true
                    Integer aft = (Integer)settings."trig_${inType}_after"
                    if(aft != null) {
                        if(aft < 0 || aft > 7200) settingUpdate("trig_${inType}_after",10)
                        input "trig_${inType}_after_repeat", sNUMBER, title: spanSmBld("Repeat every (xx) (10..7200) seconds until it's not ${settings."trig_${inType}_cmd"}?", sNULL, sDELAYT), required: false, defaultValue: null, submitOnChange: true
                        Integer aftR = (Integer)settings."trig_${inType}_after_repeat"
                        if(aftR != null) {
                            input "trig_${inType}_after_repeat_cnt", sNUMBER, title: spanSmBld("Only repeat this many times (2..20000)?", sNULL, sQUES) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                            if(aftR < 10 || aftR > 7200) settingUpdate("trig_${inType}_after_repeat",10)
                            triggerMsgInput(inType)
                        }
                        Integer aftRC = (Integer)settings."trig_${inType}_after_repeat_cnt"
                        if(aftRC != null)
                            if(aftRC < 2 || aftRC > 20000) settingUpdate("trig_${inType}_after_repeat_cnt",120)
//                        settingRemove("trig_${inType}_once")
//                        settingRemove("trig_${inType}_wait")

                    } else {
                        settingRemove("trig_${inType}_after_repeat")
                        settingRemove("trig_${inType}_after_repeat_cnt")
                        settingRemove("trig_${inType}_after_repeat_txt")
                    }
                }
//                }
 //               if((Integer)settings."trig_${inType}_after" == null) {
                    input "trig_${inType}_once", sBOOL, title: spanSmBld("Only alert once a day?", sNULL, sQUES) + optPrefix(), required: false, defaultValue: false, submitOnChange: true
                    input "trig_${inType}_wait", sNUMBER, title: spanSmBld("Wait between each report (in seconds)", sNULL, sDELAYT) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                    triggerMsgInput(inType)
  //              }
            }
        }
    }
}

def trigButtonSect(String inType, String capType, String sectStr, String devTitle, String image, String cmd){
    section (sectHead(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: spanSmBld(devTitle, sNULL, image), required: true, multiple: true, submitOnChange: true
        if (settings."trig_${inType}") {
            settingUpdate("trig_${inType}_cmd", cmd, sENUM)
            //input "trig_${inType}_cmd", sENUM, title: inTS1("Pushed changes", sCOMMAND), options: [sPUSHED], required: true, multiple: false, defaultValue: sPUSHED, submitOnChange: true
            input "trig_${inType}_nums", sENUM, title: inTS1("button numbers?", sCOMMAND), options: 1..8, required: true, multiple: true,  submitOnChange: true
            if(settings."trig_${inType}_nums") {
                triggerMsgInput(cmd)
            }
        }
    }
}

def trigNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, Boolean devReq=true) {
    Boolean done = false
    section (sectHead(sectStr), hideable: true) {
        input "trig_${inType}", "capability.${capType}", title: spanSmBld(devTitle, sNULL, image), multiple: true, submitOnChange: true, required: devReq
        if(settings."trig_${inType}") {
            input "trig_${inType}_cmd", sENUM, title: spanSmBld("${cmdTitle} is...", sNULL, sCOMMAND), options: numOpts(), required: true, multiple: false, submitOnChange: true
            if (settings."trig_${inType}_cmd") {
                if ((String)settings."trig_${inType}_cmd" in [sNBETWEEN, sBETWEEN, sBELOW]) {
                    String snot = (String)settings."trig_${inType}_cmd" in [sNBETWEEN] ? "Not " : sBLANK
                    input "trig_${inType}_low", sNUMBER, title: spanSmBld(((String)settings."trig_${inType}_cmd" in [sNBETWEEN, sBETWEEN] ? "${snot}Between a Low" : "a low") + " ${cmdTitle} of...", sNULL, "low"), required: true, submitOnChange: true
                }
                if(settings."trig_${inType}_low"!=null && ((String)settings."trig_${inType}_cmd" in [sBELOW]) ) done=true

                if ((String)settings."trig_${inType}_cmd" in [sNBETWEEN, sBETWEEN, sABOVE]) {
                    input "trig_${inType}_high", sNUMBER, title: spanSmBld(((String)settings."trig_${inType}_cmd" in [sNBETWEEN, sBETWEEN] ? "and a High of..." : "a High") + " ${cmdTitle} of...", sNULL, "high"), required: true, submitOnChange: true
                }
                if(settings."trig_${inType}_high"!=null && ((String)settings."trig_${inType}_cmd" in [sABOVE])) done=true

                if(settings."trig_${inType}_low"!=null && settings."trig_${inType}_high"!=null && ((String)settings."trig_${inType}_cmd" in [sNBETWEEN, sBETWEEN])) done=true

                if ((String)settings."trig_${inType}_cmd" == sEQUALS) {
                    input "trig_${inType}_equal", sNUMBER, title: spanSmBld("a ${cmdTitle} of...", sNULL, sEQ), required: true, submitOnChange: true
                    if(settings."trig_${inType}_equal"!=null) done=true
                }
                if(done) {
                    if (settings."trig_${inType}"?.size() > 1) {
                        input "trig_${inType}_all", sBOOL, title: spanSmBld("Require ALL devices to be (${settings."trig_${inType}_cmd"}) values?", sNULL, sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                        if(!(Boolean)settings."trig_${inType}_all") {
                            input "trig_${inType}_avg", sBOOL, title: spanSmBld("Use the average of all selected device values?", sNULL, sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                        }
                    }
//                    if(!(Boolean)settings."trig_${inType}_once" && !(Integer)settings."trig_${inType}_wait") {
                    input "trig_${inType}_after", sNUMBER, title: spanSmBld("Only after (${settings."trig_${inType}_cmd"}) for (xx) (0..7200) seconds?", sNULL, sDELAYT), required: false, defaultValue: null, submitOnChange: true
                    Integer aft = (Integer)settings."trig_${inType}_after"
                    if(aft != null) {
                        if(aft < 0 || aft > 7200) settingUpdate("trig_${inType}_after",10)
                        input "trig_${inType}_after_repeat", sNUMBER, title: spanSmBld("Repeat every (xx) (10..7200) seconds until it's not ${settings."trig_${inType}_cmd"}?", sNULL, sDELAYT), required: false, defaultValue: null, submitOnChange: true
                        Integer aftR = (Integer)settings."trig_${inType}_after_repeat"
                        if(aftR != null) {
                            input "trig_${inType}_after_repeat_cnt", sNUMBER, title: spanSmBld("Only repeat this many times (2..20000)?", sNULL, sQUES) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                            if(aftR < 10 || aftR > 7200) settingUpdate("trig_${inType}_after_repeat",10)
//                                triggerMsgInput(inType)
                        }
                        Integer aftRC = (Integer)settings."trig_${inType}_after_repeat_cnt"
                        if(aftRC != null)
                            if(aftRC < 2 || aftRC > 20000) settingUpdate("trig_${inType}_after_repeat_cnt",120)
//                            settingRemove("trig_${inType}_once")
//                            settingRemove("trig_${inType}_wait")

                    } else {
                        settingRemove("trig_${inType}_after_repeat")
                        settingRemove("trig_${inType}_after_repeat_cnt")
                        settingRemove("trig_${inType}_after_repeat_txt")
                    }
//                    }
//                    if((Integer)settings."trig_${inType}_after" == null) {
                    input "trig_${inType}_once", sBOOL, title: spanSmBld("Only alert once a day? (per type: ${inType})", sNULL, sQUES) + optPrefix(), required: false, defaultValue: false, submitOnChange: true
                    input "trig_${inType}_wait", sNUMBER, title: spanSmBld("Wait between each report (in seconds)?", sNULL, sQUES) + optPrefix(), required: false, defaultValue: null, submitOnChange: true
                    triggerMsgInput(inType)
//                    }
                }
            }
        }
    }
}

def triggerMsgInput(String inType /*, Boolean showRepInputs=false, Integer itemCnt=0 */) {
    if((String)settings.actionType in [sSPEAK, sSPEAKP, sANN]) {
        String str = spanSmBldBr("Response Options", sCLR4D9)
        str += spanSmBr("Available Options:")
        str += spanSmBr("   ${sBULLET} ${strUnder("1")}: Leave the text empty below and text will be generated for each ${inType} trigger event.")
        str += spanSmBr("   ${sBULLET} ${strUnder("2")}: Wait till the Execution config step and define a single global response for all triggers selected.")
        str += spanSmBr("   ${sBULLET} ${strUnder("3")}: Use the response builder below and create custom responses for each individual trigger type. (Supports randomization when multiple responses are configured)")
        paragraph divSm(str, sCLRGRY, "info")
        //Custom Text Options
        href url: parent?.getTextEditorPath(app?.id as String, "trig_${inType}_txt"), style: sEXTNRL, required: false, title: inTS1("Custom ${inType?.capitalize()} Responses", sTEXT) + optPrefix(),
                description: ((String)settings."trig_${inType}_txt" ? spanSm((String)settings."trig_${inType}_txt", sCLR4D9) : sBLANK) + ' ' + spanSm("Open Response Designer...", sCLRGRY)
    //    if(showRepInputs) {
            if((Integer)settings."trig_${inType}_after_repeat") {
                //Custom Repeat Text Options
                paragraph pTS("Description:\nAdd custom responses for the ${inType} events that are repeated.", getAppImg("info"), false, sCLR4D9)
                href url: parent?.getTextEditorPath(app?.id as String, "trig_${inType}_after_repeat_txt"), style: sEXTNRL, title: spanSm("Custom ${inType?.capitalize()} Repeat Responses", sNULL, sTEXT) + optPrefix(),
                        description: (String)settings."trig_${inType}_after_repeat_txt" ?: "Open Response Designer...", submitOnChange: true
            }
     //   }
    }
}

Boolean locationTriggers() {
    return  (valTrigEvt(sMODE) && (List)settings.trig_mode) || (valTrigEvt(sALRMSYSST) && (List)settings.trig_alarmSystemStatus) ||
        (valTrigEvt(sPISTNEXEC) && settings.trig_pistonExecuted) ||
        (valTrigEvt("guard") && settings.trig_guard)
}

Boolean deviceTriggers() {
    return (settings.trig_windowShade && settings.trig_windowShade_cmd) || (settings.trig_door && settings.trig_door_cmd) || (settings.trig_valve && settings.trig_valve_cmd) ||
        (settings.trig_switch && settings.trig_switch_cmd) || (settings.trig_level && settings.trig_level_cmd) || (settings.trig_lock && settings.trig_lock_cmd) ||
        (settings.trig_securityKeypad && settings.trig_securityKeypad_cmd) ||
        (settings.trig_button && settings.trig_button_cmd) ||
        (settings.trig_pushed && settings.trig_pushed_cmd && settings.trig_pushed_nums) ||
        (settings.trig_released && settings.trig_released_cmd && settings.trig_released_nums) ||
        (settings.trig_held && settings.trig_held_cmd && settings.trig_held_nums) ||
        (settings.trig_doubleTapped && settings.trig_doubleTapped_cmd && settings.trig_doubleTapped_nums) ||
        (thermostatTriggers())
}

Boolean thermostatTriggers() {
    List okList = []
    [sCOOLSP, sHEATSP, sTHERMTEMP].each { String att-> // Thermostat number value validation
        if(valTrigEvt(att)) { okList.push((Boolean)(settings."trig_${att}" && settings."trig_${att}_cmd" && (settings."trig_${att}_low"!=null || settings."trig_${att}_high"!=null || settings."trig_${att}_equal"!=null))) }
    }
    ["thermostatMode", "thermostatOperatingState", "thermostatFanMode"].each { String att-> // Thermostat non number validation
        if(valTrigEvt(att)) { okList.push((Boolean)(settings."trig_${att}" && settings."trig_${att}_cmd")) }
    }
    // log.debug "thermostatTriggers | okList: ${okList} | Every: ${(okList.every { it == true })}"
    return (okList.size()) ? (okList.every { it == true }) : false
}

Boolean sensorTriggers() {
    return (settings.trig_temperature && settings.trig_temperature_cmd) || (settings.trig_carbonMonoxide && settings.trig_carbonMonoxide_cmd) || (settings.trig_humidity && settings.trig_humidity_cmd) ||
        (settings.trig_battery && settings.trig_battery_cmd) ||
        (settings.trig_water && settings.trig_water_cmd) || (settings.trig_smoke && settings.trig_smoke_cmd) || (settings.trig_presence && settings.trig_presence_cmd) || (settings.trig_motion && settings.trig_motion_cmd) ||
        (settings.trig_contact && settings.trig_contact_cmd) || (settings.trig_power && settings.trig_power_cmd) || (settings.trig_illuminance && settings.trig_illuminance_low!=null && settings.trig_illuminance_high!=null) ||
        (settings.trig_acceleration && settings.trig_acceleration_cmd)
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
    return dynamicPage(name: "conditionsPage", nextPage: "mainPage", title: "Conditions/Restrictions", install: false, uninstall: false) {
        String a = getConditionsDesc(false)
        if(a) {
            section() { paragraph spanSm(a, sCLR4D9) }
        }
        Boolean multiConds = multipleConditions()
        section() {
            if(multiConds) {
                input "cond_require_all", sBOOL, title: inTS1("Require All Selected Conditions to Pass Before Activating Zone?", sCHKBOX), required: false, defaultValue: true, submitOnChange: true
            }
            paragraph spanSmBldBr("Notice:", sCLR4D9) + spanSm(reqAllCond() ? "All selected conditions must pass before for this action to operate." : "Any condition will allow this action to operate.", sCLR4D9)
        }
        section(sectHead("Time/Date")) {
            href "condTimePage", title: inTS1("Time Schedule", "clock"), description: spanSm(getTimeCondDesc(false), sCLR4D9)
            input "cond_days", sENUM, title: inTS1("Days of the week", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: weekDaysEnum()
            input "cond_months", sENUM, title: inTS1("Months of the year", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: monthEnum()
        }
        section (sectHead("Mode Conditions")) {
            input "cond_mode", sMODE, title: inTS1("Location Modes...", sMODE), multiple: true, required: false, submitOnChange: true
            if((List)settings.cond_mode) {
                input "cond_mode_cmd", sENUM, title: inTS1("are...", sCOMMAND), options: ["not":"Not in these modes", (sARE):"In these Modes"], required: true, multiple: false, submitOnChange: true
            }
        }
        section (sectHead("Alarm Conditions")) {
            input "cond_alarmSystemStatus", sENUM, title: inTS1("${getAlarmSystemName()} is...", "alarm_home"), options: getAlarmTrigOpts(), multiple: true, required: false, submitOnChange: true
        }

        condNonNumSect(sSWITCH, sSWITCH, "Switches/Outlets Conditions", "Switches/Outlets", lONOFF, sARE, sSWITCH)

        condNonNumSect(sMOTION, "motionSensor", "Motion Conditions", "Motion Sensors", lACTINACT, sARE, sMOTION)

        condNonNumSect("presence", "presenceSensor", "Presence Conditions", "Presence Sensors", lPRES, sARE, "presence")

        condNonNumSect(sCONTACT, "contactSensor", "Door, Window, Contact Sensors Conditions", "Contact Sensors", lOPNCLS, sARE, sCONTACT)

        condNonNumSect("acceleration", "accelerationSensor", "Accelerometer Conditions", "Accelerometer Sensors", lACTINACT, sARE, "acceleration")

        condNonNumSect(sLOCK, sLOCK, "Lock Conditions", "Smart Locks", lLOCKUNL, sARE, sLOCK)

        condNonNumSect("securityKeypad", "securityKeypad", "Security Keypad Conditions", "Security Kepads", lSEC, sARE, sKEYPAD)

        condNonNumSect("door", "garageDoorControl", "Garage Door Conditions", "Garage Doors", lOPNCLS, sARE, "garage_door")

        condNumValSect(sTEMP, "temperatureMeasurement", "Temperature Conditions", "Temperature Sensors", "Temperature", sTEMP)

        condNumValSect(sHUMID, "relativeHumidityMeasurement", "Humidity Conditions", "Relative Humidity Sensors", "Relative Humidity (%)", sHUMID)

        condNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Conditions", "Illuminance Sensors", "Lux Level (%)", "illuminance")

        condNumValSect(sLEVEL, "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level (%)", sSPDKNB)

        condNonNumSect(sWATER, "waterSensor", "Water Sensors", "Water Sensors", lWETDRY, sARE, sWATER)

        condNumValSect(sPOWER, "powerMeter", "Power Events", "Power Meters", "Power Level (W)", sPOWER)

        condNonNumSect("windowShade", "windowShade", "Window Shades", "Window Shades", lOPNCLS, sARE, "shade")

        condNonNumSect(sVALVE, sVALVE, "Valves", "Valves", lOPNCLS, sARE, sVALVE)

        condNumValSect(sBATT, sBATT, "Battery Level Conditions", "Batteries", "Level (%)", sBATT)

        condNonNumSectM("thermostatMode", sTHERM, "Thermostat Modes", "Thermostat Mode", getThermModeOpts(), sTHERM)

        condNonNumSectM("thermostatOperatingState", sTHERM, "Thermostat Operating States", "Thermostat Operating State", getThermOperStOpts(), sTHERM)

        condNonNumSectM("thermostatFanMode", sTHERM, "Thermostat Fan Modes", "Thermostat Fan Mode", getThermFanOpts(), sTHERM)

        condNumValSect(sCOOLSP, sTHERM, "Cooling Setpoint", "Thermostat Cooling SetPoint", "Temperature", sTEMP)

        condNumValSect(sHEATSP, sTHERM, "Heating Setpoint", "Thermostat Heating SetPoint", "Temperature", sTEMP)
    }
}

static List<String> getThermModeOpts() {
    return ["auto", "cool", " heat", "emergency heat", "off"]
}

static List<String> getThermOperStOpts() {
    return ["cooling", "heating", "idle", "fan only"]
}

static List<String> getThermFanOpts() {
    return ["on", "circulate", "auto"]
}

// allows multiple:true for _cmd
def condNonNumSectM(String inType, String capType, String sectStr, String devTitle, List cmdOpts, String image) {
    section (sectHead(sectStr)) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required:false
        if (settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${devTitle} is...", sCOMMAND), options: cmdOpts, multiple: true, required: true, submitOnChange: true
            if (((List)settings."cond_${inType}")?.size() > 1 && settings."cond_${inType}_cmd"?.size() == 1) {
                input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            } else settingUpdate("cond_${inType}_all", sFALSE, sBOOL)
        }
    }
}

def condNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, String image) {
    section (sectHead(sectStr)) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required:false
        if (settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle}...", sCOMMAND), options: cmdOpts, multiple: false, required: true, submitOnChange: true
            if ((String)settings."cond_${inType}_cmd" && settings."cond_${inType}"?.size() > 1) {
                input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
        }
    }
}

static List<String> numOpts() {
    return [sBETWEEN, sNBETWEEN, sBELOW, sABOVE, sEQUALS]
}

def condNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image) {
    section (sectHead(sectStr)) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required: false
        if((List)settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle} is...", sCOMMAND), options: numOpts(), required: true, multiple: false, submitOnChange: true
            String c_cmd = (String)settings."cond_${inType}_cmd"
            if (c_cmd) {
                if (c_cmd in [sNBETWEEN, sBETWEEN, sBELOW]) {
                    input "cond_${inType}_low", sNUMBER, title: inTS1("a ${c_cmd in [sNBETWEEN, sBETWEEN] ? "Low " : sBLANK}${cmdTitle} of...", "low"), required: true, submitOnChange: true
                }
                if (c_cmd in [sNBETWEEN, sBETWEEN, sABOVE]) {
                    input "cond_${inType}_high", sNUMBER, title: inTS1("${c_cmd in [sNBETWEEN, sBETWEEN] ? "and a high " : "a "}${cmdTitle} of...", "high"), required: true, submitOnChange: true
                }
                if (c_cmd == sEQUALS) {
                    input "cond_${inType}_equal", sNUMBER, title: inTS1("a ${cmdTitle} of...", sEQ), required: true, submitOnChange: true
                }
                if (((List)settings."cond_${inType}")?.size() > 1) {
                    input "cond_${inType}_all", sBOOL, title: inTS1("Require ALL devices to be (${c_cmd}) values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                    if(!(Boolean)settings."cond_${inType}_all") {
                        input "cond_${inType}_avg", sBOOL, title: inTS1("Use the average of all selected device values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                    }
                }
            }
        }
    }
}

private Map devsSupportVolume(List devs) {
    // this will never return a zone virtual device
    List<String> noSupport = []
    List<String> supported = []
    if(devs?.size()) {
        devs.each { dev->
            if(dev?.hasAttribute("permissions") && dev?.currentPermissions?.toString()?.contains("volumeControl")) {
                supported.push((String)dev.label)
            } else { noSupport.push((String)dev.label) }
        }
    }
    return [s:supported, n:noSupport]
}

def actVariableDesc(String actType, Boolean hideUserTxt=false) {
    Map<String,Map<String,List>> txtItems = customTxtItems()
    if(!isTierAction()) {
        if(!txtItems?.size() && state.showSpeakEvtVars && !(String)settings."act_${actType}_txt") {
            String str = "NOTICE:<br>You can choose to leave the response field empty and generic text will be generated for each event type, or return to Step 2 and define responses for each trigger."
            paragraph spanSm(str, sCLR4D9, getAppImg("info"))
        }
        if(!hideUserTxt) {
            if(txtItems?.size()) {
                String str = "NOTICE: (Custom Text Defined)"
                txtItems?.each { i->
                    i?.value?.each { i2-> str += lineBr() + "${sBULLET} ${i?.key?.capitalize()} ${i2?.key?.capitalize()}: (${i2?.value?.size()} Response${pluralizeStr(i2?.value)})" }
                }
                paragraph spanSmBld(str, sCLR4D9)
                paragraph spanSmBld("WARNING:<br>Entering text below will override the text you defined for the trigger types under Step 2.", sCLRRED)
            }
        }
    }
}

@Field static final Map<String, String> descsFLD = [
    speak: "Speak any message on your Echo Devices.",
    speak_parallel: "Speak any message parallel on your Echo Devices.",
    announcement: "Plays a brief tone and speaks the message you define. If you select multiple devices it will be a synchronized broadcast.",
    speak_tiered: "Allows you to create tiered responses.  Each tier can have a different delay before the next message is spoken.",
    speak_parallel_tiered: "Allows you to create parallel tiered responses.  Each tier can have a different delay before the next message is spoken.",
    announcement_tiered: "Allows you to create tiered responses.  Each tier can have a different delay before the next message is announced. Plays a brief tone and announces the message you define. If you select multiple devices it will be a synchronized broadcast.",
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
            input "act_tier_cnt", sNUMBER, title: inTS1("How many Tiers?", sEQ), required: true, submitOnChange: true
        }
        Integer tierCnt = (Integer)settings.act_tier_cnt
        if(tierCnt) {
            (1..tierCnt)?.each { Integer ti->
                section(sectHead("Tier Item (${ti}) Config:")) {
                    if(ti > 1) {
                        input "act_tier_item_${ti}_delay", sNUMBER, title: inTS1("Delay after Tier ${ti-1}\n(seconds)", sEQ), defaultValue: (ti == 1 ? 0 : null), required: true, submitOnChange: true
                    }
                    if(ti==1 || settings."act_tier_item_${ti}_delay") {
                        String inTxt = (String)settings."act_tier_item_${ti}_txt"
                        href url: parent?.getTextEditorPath(app?.id as String, "act_tier_item_${ti}_txt"), style: sEXTNRL, required: true, title: inTS1("Tier Item ${ti} Response", sTEXT) + (!inTxt ? spanSm(" (Required)", sCLRRED) : sBLANK),
                                description: inTxt ? spanSm(inTxt, sCLR4D9) : inactFoot("Open Response Designer...")
                    }
                    input "act_tier_item_${ti}_volume_change", sNUMBER, title: inTS1("Tier Item Volume", sSPDKNB) + optPrefix(), defaultValue: null, required: false, submitOnChange: true
                    input "act_tier_item_${ti}_volume_restore", sNUMBER, title: inTS1("Tier Item Volume Restore", sSPDKNB) + optPrefix(), defaultValue: null, required: false, submitOnChange: true
                }
            }
            if(isTierActConfigured()) {
                section(sBLANK) {
                    paragraph spanMdBldBr("Tier Configuration Complete", sNULL, getAppImg("done")) + spanSm("Press <b>Next</b> to Return to Main Page")
                }
            }
        }
    }
}

String getTierRespDesc() {
    Map tierMap = getTierMap() ?: [:]
    String str = sBLANK
    str += tierMap?.size() ? spanSmBr("Tiered Responses: (${tierMap?.size()})") : sBLANK
    tierMap?.each { k,v->
        if((String)settings."act_tier_item_${k}_txt") {
            str += (settings."act_tier_item_${k}_delay") ? spanSmBr(" ${sBULLET} Tier ${k} delay: (${settings."act_tier_item_${k}_delay"} sec)") : sBLANK
            str += (settings."act_tier_item_${k}_volume_change") ? spanSmBr(" ${sBULLET} Tier ${k} volume: (${settings."act_tier_item_${k}_volume_change"})") : sBLANK
            str += (settings."act_tier_item_${k}_volume_restore") ? spanSmBr(" ${sBULLET} Tier ${k} restore: (${settings."act_tier_item_${k}_volume_restore"})") : sBLANK
        }
    }
    return str != sBLANK ? str : sBLANK
}

Boolean isTierAction() {
    return ((String)settings.actionType in [sSPEAKT, "speak_parallel_tiered", "announcement_tiered"])
}

Boolean isTierActConfigured() {
    if(!isTierAction()) { return false }
    Integer cnt = (Integer)settings.act_tier_cnt
    List tierKeys = settings.findAll { it?.key?.startsWith("act_tier_item_") && it?.key?.endsWith("_txt") }?.collect { (String)it?.key }
    return (tierKeys?.size() == cnt)
}

Map getTierMap() {
    Map exec = [:]
    Integer cnt = (Integer)settings.act_tier_cnt
    if(isTierActConfigured() && cnt) {
        List tiers = (1..cnt)
        tiers?.each { Integer t->
            exec[t] = [
                message: (String)settings["act_tier_item_${t}_txt"],
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
    List<String> tierKeys = settings.findAll { it?.key?.startsWith("act_tier_item_") }?.collect { it.key as String }
    List<Integer> tierIds = isTierAct && tierCnt ? (1..tierCnt) : []
    // if(!isTierAct() || !tierCnt) { return }
    tierKeys?.each { String k->
        List<String> id = k?.tokenize("_") ?: []
        if(!isTierAct || (id?.size() && id?.size() < 4) || !id[3]?.isNumber() || !(id[3]?.toInteger() in tierIds)) { rem.push(k) }
    }
    if(rem.size()) { logDebug("tierItemCleanup | Removing: ${rem}"); rem.each { settingRemove(it) } }
}

def actTextOrTiersInput(String type, Boolean req=false) {
    if(isTierAction()) {
        String tDesc = getTierRespDesc()
        href "actionTiersPage", title: inTS1("Create Tiered Responses?", sTEXT, (tDesc ? sCLR4D9 : sCLRRED)), description: (tDesc ? divSm(tDesc + inputFooter(sTTM), sCLR4D9) : inputFooter(sTTC, sCLRGRY))
        input "act_tier_stop_on_clear", sBOOL, title: inTS1("Stop responses when trigger is cleared?"), required: false, defaultValue: false, submitOnChange: true
    } else {
        String textUrl = parent?.getTextEditorPath(app?.id as String, type)
        String addr = !req ? optPrefix() : sBLANK
        href url: textUrl, style: sEXTNRL, title: inTS1("Default Action Response", sTEXT) + addr, description: (String)settings."${type}" ? spanSm((String)settings."${type}", sCLR4D9) : inputFooter("Open Response Designer...", sCLRGRY, true)
    }
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", nextPage: "mainPage", title: "Action Execution Configuration", install: false, uninstall: false) {
        String a = getActionDesc(false)
        if(a) {
            section() {
                paragraph spanSm(a, sCLR4D9)
            }
        }
        Boolean done = false
        Map actionExecMap = [configured: false]
        String myactionType = (String)settings.actionType
        if(myactionType) {
            actionExecMap.actionType = myactionType
            actionExecMap.config = [:]
            List devices = parent?.getDevicesFromList(settings.act_EchoDevices)
            String actTypeDesc = spanBldBr("[${myactionType.tokenize("_")?.collect { it?.capitalize() }?.join(sSPACE)}]", sCLR4D9, "info") + spanSm(actionTypeDesc(), sCLRGRY)
            Boolean isTierAct = isTierAction()
            switch(myactionType) {
                case sSPEAK:
                case sSPEAKP:
                case sSPEAKT:
                case "speak_parallel_tiered":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sectHead("Action Type Config:")) {
                            actVariableDesc(myactionType)
                            actTextOrTiersInput("act_speak_txt")
                        }
                        if(!(myactionType in [sSPEAKP, "speak_parallel_tiered"])) actionVolumeInputs(devices)
                        actionExecMap.config[myactionType] = [text: (String)settings.act_speak_txt, evtText: ((state.showSpeakEvtVars && !(String)settings.act_speak_txt) || hasUserDefinedTxt()), tiers: getTierMap()]
                        done = state.showSpeakEvtVars || (String)settings.act_speak_txt || (isTierAct && isTierActConfigured())
                    } else { done = false }
                    break

                case sANN:
                case "announcement_tiered":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm("announce")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sectHead("Action Type Config:")) {
                            actVariableDesc(myactionType)
                            actTextOrTiersInput("act_announcement_txt")
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config[myactionType] = [text: (String)settings.act_announcement_txt, evtText: ((state.showSpeakEvtVars && !(String)settings.act_announcement_txt) || hasUserDefinedTxt()), tiers: getTierMap()]
                        if(settings.act_EchoDevices?.size() > 1) {
                            List devObj = []
                            devices?.each { devObj?.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String]) }
                            // log.debug "devObj: $devObj"
                            actionExecMap.config[myactionType].deviceObjs = devObj
                        }
                        done = state.showSpeakEvtVars || (String)settings.act_announcement_txt || (isTierAct && isTierActConfigured())
                    } else { done = false }
                    break

                case "voicecmd":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices) {
                        section(sectHead("Action Type Config:")) {
                            input "act_voicecmd_txt", sTEXT, title: inTS1("Enter voice command text", sTEXT), submitOnChange: true, required: false
                        }
                        actionExecMap.config.voicecmd = [text: (String)settings.act_voicecmd_txt, cmd: "voiceCmdAsText"]
                        if((String)settings.act_voicecmd_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sequence":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map seqItemsAvail = parent?.seqItemsAvail()
                        section(sectHead("Sequence Options Legend:"), hideable: true, hidden: false) {
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
                            paragraph spanSm(str1, sCLR4D9)
                            // paragraph spanSm(str4, sCLR4D9)
                            paragraph spanSm(str2, sCLR4D9)
                            paragraph spanSm(str3, sCLR4D9)
                            paragraph spanSm("Enter the command in a format exactly like this:<br>volume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30<br><br>Each command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.", "violet")
                        }
                        section(sectHead("Action Type Config:")) {
                            input "act_sequence_txt", sTEXT, title: inTS1("Enter sequence text", sTEXT), submitOnChange: true, required: false
                        }
                        actionExecMap.config.sequence = [text: (String)settings.act_sequence_txt, cmd: "executeSequenceCommand"]
                        if((String)settings.act_sequence_txt) { done = true } else { done = false }
                    } else { done = false }
                    break

                case sWEATH:
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        actionVolumeInputs(devices)
                        done = true
                        actionExecMap.config.weather = [cmd: "playWeather"]
                    } else { done = false }
                    break

                case "playback":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map playbackOpts = [
                            "pause":"Pause", "stop":"Stop", "play":"Play", "nextTrack":"Next Track", "previousTrack":"Previous Track",
                            "mute":"Mute", "volume":"Volume"
                        ]
                        section(sectHead("Playback Config:")) {
                            input "act_playback_cmd", sENUM, title: inTS1("Select Playback Action", sCOMMAND), description: sBLANK, options: playbackOpts, required: true, submitOnChange: true
                        }
                        if(settings.act_playback_cmd == "volume") { actionVolumeInputs(devices, true) }
                        actionExecMap.config?.playback = [cmd: settings.act_playback_cmd]
                        if(settings.act_playback_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "sounds":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sectHead("BuiltIn Sounds Config:")) {
                            input "act_sounds_cmd", sENUM, title: inTS1("Select Sound Type", sCOMMAND), description: sBLANK, options: parent?.getAvailableSounds()?.collect { it?.key as String }, required: true, submitOnChange: true
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config.sounds = [cmd: "playSoundByName", name: settings.act_sounds_cmd]
                        done = (settings.act_sounds_cmd != null)
                    } else { done = false }
                    break

                case "builtin":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        Map builtinOpts = [
                            "playSingASong": "Sing a Song", "playFlashBrief": "Flash Briefing (News)", "playGoodNews": "Good News Only", "playFunFact": "Fun Fact", "playTraffic": "Traffic", "playJoke": "Joke",
                            "playTellStory": "Tell Story", "sayGoodbye": "Say Goodbye", "sayGoodNight": "Say Goodnight", "sayBirthday": "Happy Birthday",
                            "sayCompliment": "Give Compliment", "sayGoodMorning": "Good Morning", "sayWelcomeHome": "Welcome Home"
                        ]
                        section(sectHead("BuiltIn Speech Config:")) {
                            input "act_builtin_cmd", sENUM, title: inTS1("Select Builtin Speech Type", sCOMMAND), description: sBLANK, options: builtinOpts, required: true, submitOnChange: true
                        }
                        actionVolumeInputs(devices)
                        actionExecMap.config.builtin = [cmd: settings.act_builtin_cmd]
                        if(settings.act_builtin_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "music":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm("mediaPlayer")
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        List musicProvs = devices[0]?.hasAttribute("supportedMusic") ? devices[0]?.currentValue("supportedMusic")?.split(",")?.collect { "${it?.toString()?.trim()}"} : []
                        logDebug("Music Providers: ${musicProvs}")
                        if(musicProvs) {
                            section(sectHead("Music Providers:")) {
                                input "act_music_provider", sENUM, title: inTS1("Select Music Provider", "music"), description: sBLANK, options: musicProvs, multiple: false, required: true, submitOnChange: true
                            }
                            if(settings.act_music_provider) {
                                if(settings.act_music_provider == "TuneIn") {
                                    section(sectHead("TuneIn Search Results:")) {
                                        paragraph spanSm("Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.")
                                        input "tuneinSearchQuery", sTEXT, title: inTS1("Enter search phrase for TuneIn", "tunein"), defaultValue: null, required: false, submitOnChange: true
                                        if(settings.tuneinSearchQuery) {
                                            href "searchTuneInResultsPage", title: inTS1("View search results!", "search"), description: sTTP
                                        }
                                    }
                                }
                                section(sectHead("Action Type Config:")) {
                                    input "act_music_txt", sTEXT, title: inTS1("Enter Music Search text", sTEXT), submitOnChange: true, required: false
                                }
                                actionVolumeInputs(devices)
                            }
                        }
                        actionExecMap.config.music = [cmd: "searchMusic", provider: settings.act_music_provider, search: (String)settings.act_music_txt]
                        done = settings.act_music_provider && (String)settings.act_music_txt
                    } else { done = false }
                    break

                case "calendar":
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm(sTTS)
                    if(settings.act_EchoDevices || settings.act_EchoZones) {
                        section(sectHead("Action Type Config:")) {
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
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm("alarms")
                    if(settings.act_EchoDevices) {
//                        Map repeatOpts = ["everyday":"Everyday", "weekends":"Weekends", "weekdays":"Weekdays", "daysofweek":"Days of the Week", "everyxdays":"Every Nth Day"]
                        String rptType = null
                        def rptTypeOpts = null
                        section(sectHead("Action Type Config:")) {
                            input "act_alarm_label", sTEXT, title: inTS1("Alarm Label", "name_tag"), submitOnChange: true, required: true
                            input "act_alarm_date", sTEXT, title: inTS1("Alarm Date\n(yyyy-mm-dd)", "day_calendar"), submitOnChange: true, required: true
                            input "act_alarm_time", sTIME, title: inTS1("Alarm Time", "clock"), submitOnChange: true, required: true
                            // if(act_alarm_label && act_alarm_date && act_alarm_time) {
                            //     input "act_alarm_rt", sENUM, title: inTS1("Repeat (Optional)", sCOMMAND), description: sBLANK, options: repeatOpts, required: true, submitOnChange: true
                            //     if(settings."act_alarm_rt") {
                            //         rptType = settings.act_alarm_rt
                            //         if(settings.act_alarm_rt == "daysofweek") {
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
                    section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                    echoDevicesInputByPerm("reminders")
                    if(settings.act_EchoDevices) {
//                        Map repeatOpts = ["everyday":"Everyday", "weekends":"Weekends", "weekdays":"Weekdays", "daysofweek":"Days of the Week", "everyxdays":"Every Nth Day"]
                        String rptType = null
                        def rptTypeOpts = null
                        section(sectHead("Action Type Config:")) {
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
                        section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                        section(sectHead("Action Type Config:")) {
                            input "act_dnd_cmd", sENUM, title: inTS1("Select Do Not Disturb Action", sCOMMAND), description: sBLANK, options: dndOpts, required: true, submitOnChange: true
                        }
                        actionExecMap.config.dnd = [cmd: settings.act_dnd_cmd]
                        if(settings.act_dnd_cmd) { done = true } else { done = false }
                    } else { done = false }
                    break

                case "alexaroutine":
                    echoDevicesInputByPerm("wakeWord")
                    if(settings.act_EchoDevices) {
                        section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                        Map t0 = parent?.getAlexaRoutines()
                        Map routinesAvail = t0 ?: [:]
                        // logDebug("routinesAvail: $routinesAvail")
                        section(sectHead("Action Type Config:")) {
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
                        section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                        if(devsCnt >= 1) {
                            List wakeWords = devices[0]?.hasAttribute("wakeWords") ? devices[0]?.currentValue("wakeWords")?.replaceAll('"', sBLANK)?.split(",") : []
                            // logDebug("WakeWords: ${wakeWords}")
                            devices?.each { cDev->
                                section(sectHead("${cDev?.getLabel()}:")) {
                                    if(wakeWords?.size()) {
                                        paragraph "Current Wake Word: ${cDev?.hasAttribute("alexaWakeWord") ? cDev?.currentValue("alexaWakeWord") : "Unknown"}"
                                        input "act_wakeword_device_${cDev?.id}", sENUM, title: inTS1("New Wake Word", "list"), description: sBLANK, options: wakeWords, required: true, submitOnChange: true
                                        devsObj.push([device: cDev?.id as String, wakeword: settings."act_wakeword_device_${cDev?.id}", cmd: "setWakeWord"])
                                    } else { paragraph spanSmBld("Oops...<br>No Wake Words have been found!<br><br>Please Remove the device from selection.", sCLRRED) }
                                }
                            }
                        }
                        actionExecMap.config.wakeword = [ devices: devsObj]
                        done = settings.findAll { it?.key?.startsWith("act_wakeword_device_") && it?.value }?.size() == devsCnt
                    } else { done = false }
                    break

                case "bluetooth":
                    echoDevicesInputByPerm("bluetoothControl")
                    if(settings.act_EchoDevices) {
                        Integer devsCnt = settings.act_EchoDevices?.size() ?: 0
                        List devsObj = []
                        section(sectHead("Action Description:")) { paragraph spanSm(actTypeDesc, sCLR4D9) }
                        if(devsCnt >= 1) {
                            devices?.each { cDev->
                                def btData = cDev?.hasAttribute("btDevicesPaired") ? cDev?.currentValue("btDevicesPaired") : null
                                List btDevs = (btData) ? parseJson(btData)?.names : []
                                // log.debug "btDevs: $btDevs"
                                section(sectHead("${cDev?.getLabel()}:")) {
                                    if(btDevs?.size()) {
                                        input "act_bluetooth_device_${cDev?.id}", sENUM, title: inTS1("BT device to use", "bluetooth"), description: sBLANK, options: btDevs, required: true, submitOnChange: true
                                        input "act_bluetooth_action_${cDev?.id}", sENUM, title: inTS1("BT action to take", sCOMMAND), description: sBLANK, options: ["connectBluetooth":"connect", "disconnectBluetooth":"disconnect"], required: true, submitOnChange: true
                                        devsObj?.push([device: cDev?.id as String, btDevice: settings."act_bluetooth_device_${cDev?.id}", cmd: settings."act_bluetooth_action_${cDev?.id}"])
                                    } else { paragraph spanSmBldBr("Oops...") + spanSm("No Bluetooth devices are paired to this Echo Device!<br>Please Remove the device from selection.", sCLRRED) }
                                }
                            }
                        }
                        actionExecMap.config.bluetooth = [devices: devsObj]
                        done = settings.findAll { it?.key?.startsWith("act_bluetooth_device_") && it?.value }?.size() == devsCnt &&
                                settings.findAll { it?.key?.startsWith("act_bluetooth_action_") && it?.value }?.size() == devsCnt
                    } else { done = false }
                    break
                default:
                    paragraph spanSmBld("Unknown Action Type Defined...", sCLRRED, getAppImg("error"))
                    break
            }
            if(done) {
                section(sectHead("Delay Config:")) {
                    input "act_delay", sNUMBER, title: inTS1("Delay Action in seconds", sDELAYT) + optPrefix(), required: false, submitOnChange: true
                }
                if(isTierAct && (Integer)settings.act_tier_cnt > 1) {
                    section(sectHead("Tier Action Start Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tiered Tasks to Perform on Tier Start?", "tasks"), description: spanSm(actTaskDesc("act_tier_start_", true), sCLR4D9), params:[type: "act_tier_start_"]
                    }
                    section(sectHead("Tier Action Stop Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tiered Tasks to Perform on Tier Stop?", "tasks"), description: spanSm(actTaskDesc("act_tier_stop_", true), sCLR4D9), params:[type: "act_tier_stop_"]
                    }
                } else {
                    section(sectHead("Action Triggered Tasks:")) {
                        href "actTrigTasksPage", title: inTS1("Tasks to Perform when Triggered?", "tasks"), description: spanSm(actTaskDesc("act_", true), sCLR4D9), params:[type: "act_"]
                    }
                }
                actionSimulationSect()
                section(sBLANK) {
                    paragraph spanMdBldBr("Execution Configuration Complete", sNULL, getAppImg("done")) + spanSm("Press <b>Next</b> to Return to Main Page")
                }
                actionExecMap.config.volume = [change: settings.act_volume_change, restore: settings.act_volume_restore, alarm: settings.act_alarm_volume]
                actionExecMap.config.zoneVolume = getZoneVolumeMap()
                // log.debug "zoneVolume: ${actionExecMap.config.zoneVolume}"
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
                    paragraph spanSm("These tasks will be performed when the action is triggered.<br>(Delay is optional)", sCLR4D9)
                    break
                case "act_tier_start_":
                    dMap = [def: " with Tier start", delay: "Tier Start tasks"]
                    paragraph spanSm("These tasks will be performed with when the first tier of action is triggered.<br>(Delay is optional)", sCLR4D9)
                    break
                case "act_tier_stop_":
                    dMap = [def: " with Tier stop", delay: "Tier Stop tasks"]
                    paragraph spanSm("These tasks will be performed with when the last tier of action is triggered.<br>(Delay is optional)", sCLR4D9)
                    break
                default:
                    logWarn("bad actTaskDesc t: ${t}", true)
            }
        }

        if(!settings.enableWebCoRE) {
            section (sectHead("Enable webCoRE Integration:")) {
                input "enableWebCoRE", sBOOL, title: inTS("Enable webCoRE Integration", webCore_icon()), required: false, defaultValue: false, submitOnChange: true
            }
        }
        if(settings.enableWebCoRE) {
            if(!webCoREFLD) webCoRE_init()
            webCoRE_poll()
        }
        section(sectHead("Control Devices:")) {
            input "${t}switches_on", "capability.switch", title: inTS1("Turn ON these Switches${dMap?.def}", sSWITCH) + optPrefix(), multiple: true, required: false, submitOnChange: true
            input "${t}switches_off", "capability.switch", title: inTS1("Turn OFF these Switches${dMap?.def}", sSWITCH) + optPrefix(), multiple: true, required: false, submitOnChange: true
        }

        section(sectHead("Control Lights:")) {
            input "${t}lights", "capability.switch", title: inTS1("Turn ON these Lights${dMap?.def}", "light") + optPrefix(), multiple: true, required: false, submitOnChange: true
            if((List)settings."${t}lights") {
                List lights = (List)settings."${t}lights"
                if(lights?.any { i-> (i?.hasCommand("setColor")) } && !lights?.every { i-> (i?.hasCommand("setColor")) }) {
                    paragraph spanSmBld("Not all selected devices support color. So color options are hidden.", sCLRRED)
                    settingRemove("${t}lights_color".toString())
                    settingRemove("${t}lights_color_delay".toString())
                } else {
                    input "${t}lights_color", sENUM, title: inTS1("To this color?", sCOMMAND) + optPrefix(), multiple: false, options: colorSettingsListFLD?.name, required: false, submitOnChange: true
                    if(settings."${t}lights_color") {
                        input "${t}lights_color_delay", sNUMBER, title: inTS1("Restore original light state after (x) seconds?", "delay") + optPrefix(), required: false, submitOnChange: true
                    }
                }
                if(lights?.any { i-> (i?.hasCommand("setLevel")) } && !lights?.every { i-> (i?.hasCommand("setLevel")) }) {
                    paragraph spanSmBld("Not all selected devices support level. So level option is hidden.", sCLRRED)
                    settingRemove("${t}lights_level".toString())
                } else { input "${t}lights_level", sENUM, title: inTS1("At this level?", sSPDKNB) + optPrefix(), options: dimmerLevelEnum(), required: false, submitOnChange: true }
            }
        }

        section(sectHead("Control Locks:")) {
            input "${t}locks_lock", "capability.lock", title: inTS1("Lock these Locks${dMap?.def}", sLOCK) + optPrefix(), multiple: true, required: false, submitOnChange: true
            input "${t}locks_unlock", "capability.lock", title: inTS1("Unlock these Locks${dMap?.def}", sLOCK) + optPrefix(), multiple: true, required: false, submitOnChange: true
        }

        section(sectHead("Control Keypads:")) {
            input "${t}securityKeypads_disarm", "capability.securityKeypad", title: inTS1("Disarm these Keypads${dMap?.def}", sLOCK) + optPrefix(), multiple: true, required: false, submitOnChange: true
            input "${t}securityKeypads_armHome", "capability.securityKeypad", title: inTS1("Arm Home these Keypads${dMap?.def}", sLOCK) + optPrefix(), multiple: true, required: false, submitOnChange: true
            input "${t}securityKeypads_armAway", "capability.securityKeypad", title: inTS1("Arm Away these Keypads${dMap?.def}", sLOCK) + optPrefix(), multiple: true, required: false, submitOnChange: true
        }

        section(sectHead("Control Doors:")) {
            input "${t}doors_close", "capability.garageDoorControl", title: inTS1("Close these Garage Doors${dMap?.def}", "garage_door") + optPrefix(), multiple: true, required: false, submitOnChange: true
            input "${t}doors_open", "capability.garageDoorControl", title: inTS1("Open these Garage Doors${dMap?.def}", "garage_door") + optPrefix(), multiple: true, required: false, submitOnChange: true
        }

        section(sectHead("Control Siren:")) {
            input "${t}sirens", "capability.alarm", title: inTS1("Activate these Sirens${dMap?.def}", "siren") + optPrefix(), multiple: true, required: false, submitOnChange: true
            if(settings."${t}sirens") {
                input "${t}siren_cmd", sENUM, title: inTS1("Alarm action to take${dMap?.def}", sCOMMAND) + optPrefix(), options: ["both": "Siren & Stobe", "strobe":"Strobe Only", "siren":"Siren Only"], multiple: false, required: true, submitOnChange: true
                input "${t}siren_time", sNUMBER, title: inTS1("Stop after (x) seconds...", "delay"), required: true, submitOnChange: true
            }
        }
        section(sectHead("Location Actions:")) {
            input "${t}mode_run", sENUM, title: inTS1("Set Location Mode${dMap?.def}", sMODE) + optPrefix(), options: getLocationModes(true), multiple: false, required: false, submitOnChange: true
            input "${t}alarm_run", sENUM, title: inTS1("Set ${getAlarmSystemName()} mode${dMap?.def}", "alarm_home") + optPrefix(), options: getAlarmSystemStatusActions(), multiple: false, required: false, submitOnChange: true

            if(settings.enableWebCoRE) {
                input "${t}piston_run", sENUM, title: inTS("Execute a piston${dMap?.def}", webCore_icon()) + optPrefix(), options: webCoRE_list(), multiple: false, required: false, submitOnChange: true
            }
        }

        if(actTasksConfiguredByType(t)) {
            section("Delay before running Tasks: ") {
                input "${t}tasks_delay", sNUMBER, title: inTS1("Delay running ${dMap?.delay} in seconds", sDELAYT) + optPrefix(), required: false, submitOnChange: true
            }
        }
    }
}

Boolean actTasksConfiguredByType(String pType) {
    String p = pType
    return (String)settings."${p}mode_run" || (String)settings."${p}alarm_run" || settings."${p}switches_off" || settings."${p}switches_on" ||
        (settings.enableWebCoRE && (String)settings."${p}piston_run") ||
        settings."${p}lights" || settings."${p}locks_lock" || settings."${p}locks_unlock" || settings."${p}sirens" || settings."${p}doors_close" || settings."${p}doors_open" ||
        settings."${p}securityKeypads_disarm" || settings."${p}securityKeypads_armHome" || settings."${p}securityKeypads_armAway"
}

private executeTaskCommands(data) {
    String p = data?.type ?: sNULL
    logTrace("executeTaskCommands ${p} ${actTaskDesc(p)}")

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
        if(settings."${p}lights_color_delay") { captureLightState((List)settings."${p}lights") }
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
        String aStr = "\n ${sBULLET} "
        str += settings."${t}switches_on" ? aStr + "Switches On: (${settings."${t}switches_on"?.size()})" : sBLANK
        str += settings."${t}switches_off" ? aStr + "Switches Off: (${settings."${t}switches_off"?.size()})" : sBLANK
        str += settings."${t}lights" ? aStr + "Lights: (${settings."${t}lights"?.size()})" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_level" ? "\n    - Level: (${settings."${t}lights_level"}%)" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_color" ? "\n    - Color: (${settings."${t}lights_color"})" : sBLANK
        str += settings."${t}lights" && settings."${t}lights_color" && settings."${t}lights_color_delay" ? "\n    - Restore After: (${settings."${t}lights_color_delay"} sec.)" : sBLANK
        str += settings."${t}locks_unlock" ? aStr + "Locks Unlock: (${settings."${t}locks_unlock"?.size()})" : sBLANK
        str += settings."${t}locks_lock" ? aStr + "Locks Lock: (${settings."${t}locks_lock"?.size()})" : sBLANK
        str += settings."${t}securityKeypads_disarm" ? aStr + "KeyPads Disarm: (${settings."${t}securityKeypads_disarm".size()})" : sBLANK
        str += settings."${t}securityKeypads_armHome" ? aStr + "KeyPads Arm Home: (${settings."${t}securityKeypads_armHome".size()})" : sBLANK
        str += settings."${t}securityKeypads_armAway" ? aStr + "KeyPads Arm Away: (${settings."${t}securityKeypads_armAway".size()})" : sBLANK
        str += settings."${t}doors_open" ? aStr + "Garages Open: (${settings."${t}doors_open"?.size()})" : sBLANK
        str += settings."${t}doors_close" ? aStr + "Garages Close: (${settings."${t}doors_close"?.size()})" : sBLANK
        str += settings."${t}sirens" ? aStr + "Sirens On: (${settings."${t}sirens"?.size()})${settings."${t}sirens_delay" ? "(${settings."${t}sirens_delay"} sec)" : sBLANK}" : sBLANK

        str += (String)settings."${t}mode_run" ? aStr + "Set Mode:\n \u2022 ${(String)settings."${t}mode_run"}" : sBLANK
        str += (String)settings."${t}alarm_run" ? aStr + "Set Alarm:\n \u2022 ${(String)settings."${t}alarm_run"}" : sBLANK
//        str += settings."${t}routine_run" ? aStr+"Execute Routine:\n    - ${getRoutineById(settings."${t}routine_run")?.label}" : sBLANK
        str += (settings.enableWebCoRE && (String)settings."${t}piston_run") ? aStr + "Execute webCoRE Piston:\n    - " + getPistonById((String)settings."${t}piston_run") : sBLANK
    }
    return str != sBLANK ? (isInpt ? divSm(spanSmBr(str) + inputFooter(sTTM), sCLR4D9) : str) : (isInpt ? spanSm("On trigger control devices, set mode, set alarm state, execute WebCore Pistons", sCLRGRY) + inactFoot(sTTC) : sNULL)
}

@SuppressWarnings('unused')
private flashLights(data) {
    // log.debug "data: ${data}"
    String p = data?.type
    if(!p) return
    List devs = (List)settings."${p}lights"
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
            restoreLightState((List)settings."${p}lights")
        }
    }
}

@SuppressWarnings('unused')
private restoreLights(data) {
    String p = data?.type ?: sNULL
    if(p && settings."${p}lights") { restoreLightState((List)settings."${p}lights") }
}

Boolean isActDevContConfigured() {
    return isTierAction() ? (settings.act_tier_start_switches_off || settings.act_tier_start_switches_on || settings.act_tier_stop_switches_off || settings.act_tier_stop_switches_on) : (settings.act_switches_off || settings.act_switches_on)
}

def actionSimulationSect() {
    section(sectHead("Simulate Action")) {
        String str = spanSmBldBr("Test this action and see the results.", "black", "testing")
        str += spanSmBldBr("NOTE: ") + spanSmBr("  ${sBULLET} When global text is not defined, this will generate a random event based on your trigger selections.")
        str += settings.act_EchoZones ? spanSm("  ${sBULLET} Testing with zones requires you to save the app and come back in to test.") : sBLANK
        paragraph spanSm(str, sCLRGRY)
        input "actTestRun", sBOOL, title: inTS1("Test this action?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        if((Boolean)settings.actTestRun) { executeActTest() }
    }
}

Boolean customMsgRequired() { return (!((String)settings.actionType in [sSPEAK, sSPEAKP, sANN])) }
Boolean customMsgConfigured() { return (settings.notif_use_custom && settings.notif_custom_message) }

def actNotifPage() {
    return dynamicPage(name: "actNotifPage", title: "Action Notifications", install: false, uninstall: false) {
        String a = getAppNotifDesc()
        String b = spanSmBldBr("Notification Overview:", sCLR4D9)
        b += (!a) ? "  ${sBULLET} Notifications not enabled" : a
        section() {
            paragraph spanSm(b, sCLR4D9)
        }

        section (sectHead("Notification Devices:")) {
            input "notif_devs", "capability.notification", title: inTS1("Send to Notification devices?", "notification"), description: ((!settings?.notif_devs) ? inactFoot(sTTC) : sBLANK), required: false, multiple: true, submitOnChange: true
        }
        section (sectHead("Alexa Mobile Notification:")) {
            paragraph spanSmBldBr("Description:", sCLRGRY) + spanSmBld("This will send a push notification the Alexa Mobile app.", sCLRGRY)
            input "notif_alexa_mobile", sBOOL, title: inTS1("Send message to Alexa App?", "notification"), required: false, defaultValue: false, submitOnChange: true
        }

        if((List)settings.notif_devs || (Boolean)settings.notif_alexa_mobile) {
            section (sectHead("Message Customization:")) {
                Boolean custMsgReq = customMsgRequired()
                if(custMsgReq) {
                    paragraph pTS("The selected action (${(String)settings.actionType}) requires a custom notification message if notifications are enabled.", sNULL, false, sCLRGRY)
                    if(!settings.notif_use_custom) { settingUpdate("notif_use_custom", sTRUE, sBOOL) }
                } else {
                    paragraph pTS("When using speak or announcement actions custom notification is optional and a notification will be sent with speech text.", sNULL, false, sCLRGRY)
                }
                input "notif_use_custom", sBOOL, title: inTS1("Send a custom notification...", sQUES), required: false, defaultValue: false, submitOnChange: true
                if(settings.notif_use_custom || custMsgReq) {
                    input "notif_custom_message", sTEXT, title: inTS1("Enter custom message...", sTEXT), required: custMsgReq, submitOnChange: true
                }
            }
        } else {
            List sets = settings.findAll { it?.key?.startsWith("notif_") }?.collect { it?.key as String }
            sets?.each { String sI-> settingRemove(sI) }
        }

        if(isActNotifConfigured()) {
            section(sectHead("Notification Restrictions:")) {
                String nsd = getNotifSchedDesc()
                href "actNotifTimePage", title: inTS1("Quiet Restrictions", "restriction"), description: (nsd ? divSm(spanSmBr(nsd) + inputFooter(sTTM), sCLR4D9) : inactFoot(sTTC))
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
        // String a = getNotifSchedDesc()
        //  if(a) {
        //      section() {
        //          paragraph spanSmBldBr("Restrictions Status:", sCLR4D9) + spanSm(a, sCLR4D9)
        //          paragraph spanSmBldBr("NOTICE: All selected restrictions must be ${strUnder("INACTIVE")} for notifications to be sent.", sCLRORG)
        //          paragraph htmlLine()
        //      }
        //  }
        String pre = "notif"
        Boolean timeReq = (settings["${pre}_time_start"] || settings["${pre}_time_stop"])
        section(sectHead("Quiet Start Time:")) {
            input "${pre}_time_start_type", sENUM, title: inTS1("Starting at...", sSTTIME), options: [(sTIME):"Time of Day", "sunrise":sCSUNRISE, "sunset":sCSUNSET], required: false , submitOnChange: true
            if(settings."${pre}_time_start_type" == sTIME) {
                input "${pre}_time_start", sTIME, title: inTS1("Start time", sSTTIME), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_start_type" in lSUNRISESET) {
                input "${pre}_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", sSTTIME), required: false, submitOnChange: true
            }
        }
        section(sectHead("Quiet Stop Time:")) {
            input "${pre}_time_stop_type", sENUM, title: inTS1("Stopping at...", sSTTIME), options: [(sTIME):"Time of Day", "sunrise":sCSUNRISE, "sunset":sCSUNSET], required: false , submitOnChange: true
            if(settings."${pre}_time_stop_type" == sTIME) {
                input "${pre}_time_stop", sTIME, title: inTS1("Stop time", sSTTIME), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_stop_type" in lSUNRISESET) {
                input "${pre}_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", sSTTIME), required: false, submitOnChange: true
            }
        }
        section(sectHead("Allowed Days:")) {
            input "${pre}_days", sENUM, title: inTS1("Only on these week days", "day_calendar"), multiple: true, required: false, options: weekDaysEnum()
        }
        section(sectHead("Allowed Modes:")) {
            input "${pre}_modes", sMODE, title: inTS1("Only in these Modes", sMODE), multiple: true, submitOnChange: true, required: false
        }
    }
}

def ssmlInfoSection() {
    String ssmlTestUrl = "https://topvoiceapps.com/ssml"
    String ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
    String ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
    String ssmlSpeechConsUrl = "https://developer.amazon.com/docs/custom-skills/speechcon-reference-interjections-english-us.html"
    section(sectHead("SSML Documentation:"), hideable: true, hidden: true) {
        paragraph spanSmBldBr("What is SSML?", sCLR4D9) + spanSm("SSML allows for changes in tone, speed, voice, emphasis. As well as using MP3, and access to the Sound Library", sCLR4D9)
        href url: ssmlDocsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon SSML Docs", "www"), description: inactFoot("Tap to open browser")
        href url: ssmlSoundsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon Sound Library", "www"), description: inactFoot("Tap to open browser")
        href url: ssmlSpeechConsUrl, style: sEXTNRL, required: false, title: inTS1("Amazon SpeechCons", "www"), description: inactFoot("Tap to open browser")
        href url: ssmlTestUrl, style: sEXTNRL, required: false, title: inTS1("SSML Designer and Tester", "www"), description: inactFoot("Tap to open browser")
    }
}

Map<String,Map<String,List>> customTxtItems() {
    Map<String,Map<String,List>> items = [:]
    ((List<String>)settings.triggerEvents)?.each { String tr->
        String a=(String)settings."trig_${tr}_txt"
        if(a) { if(!items[tr]) { items[tr] = [:] as Map<String, List> }; items[tr].event = a.tokenize(";") }
        a = (String)settings."trig_${tr}_after_repeat_txt"
        if(a) { if(!items[tr]) { items[tr] = [:] as Map<String, List> }; items[tr].repeat = a.tokenize(";") }
    }
    return items
}

Boolean hasRepeatTriggers() {
    Map<String, Map<String,List>> items = [:]
    ((List<String>)settings.triggerEvents)?.each { String tr->
        String a=(String)settings."trig_${tr}_after_repeat_txt"
        if(a) { if(!items[tr]) { items[tr] = [:] as Map<String, List> }; items[tr].repeat = a.tokenize(";") }
    }
    return (items.size() > 0)
}

Boolean hasUserDefinedTxt() {
    Boolean fnd = false
    ((List<String>)settings.triggerEvents)?.each { String tr ->
        if((String)settings."trig_${tr}_txt") { fnd = true }
        if((String)settings."trig_${tr}_after_repeat_txt") { fnd = true }
        if(fnd) return true
    }
    return fnd
}

Boolean executionConfigured() {
    Boolean opts = (state.actionExecMap && state.actionExecMap.configured == true)
    Boolean devs = (settings.act_EchoDevices || settings.act_EchoZones)
    return (opts && devs)
}

private echoDevicesInputByPerm(String type) {
    List echoDevs = parent?.getChildDevicesByCap(type)
    Boolean capOk = (type in [sTTS, "announce"])
    Boolean zonesOk = ((String)settings.actionType in [sSPEAK, sSPEAKP, sSPEAKT, "speak_parallel_tiered", sANN, "announcement_tiered", "voicecmd", "sequence", sWEATH, "calendar", "music", "sounds", "builtin"])
    Map echoZones = (capOk && zonesOk) ? getZones() : [:]
    section(sectHead("${echoZones?.size() ? "Zones & " : sBLANK}Alexa Devices:")) {
        if(echoZones?.size()) {
            if(!settings.act_EchoZones) { paragraph spanSmBldBr("What are Zones?") + spanSm("Zones are used to direct the speech output based on the conditions in the zone (Motion, presence, etc).<br>When both Zones and Echo devices are selected, the zone will take priority over the echo device setting.") }
            input "act_EchoZones", sENUM, title: inTS1("Zone(s) to Use", "es_groups"), description: spanSm("Select the Zone(s)", sCLRGRY), options: echoZones?.collectEntries { [(it?.key): it?.value?.name?.replace(" (Z)", sBLANK) as String] }, multiple: true, required: (!settings.act_EchoDevices), submitOnChange: true
        }
        if(settings.act_EchoZones?.size() && echoDevs?.size() && !settings.act_EchoDevices?.size()) {
            paragraph spanSm("There may be scenarios when none of your zones are active at the triggered action execution.\nYou have the option to select echo devices to use when no zones are available.", sCLR4D9)
        }
        if(echoDevs?.size()) {
            Boolean devsOpt = (settings.act_EchoZones?.size())
            def eDevsMap = echoDevs?.collectEntries { [(it?.getId()): [label: it?.getLabel(), lsd: (it?.currentWasLastSpokenToDevice?.toString() == sTRUE)]] }?.sort { a,b -> b?.value?.lsd <=> a?.value?.lsd ?: a?.value?.label <=> b?.value?.label }
            input "act_EchoDevices", sENUM, title: inTS1("Echo Speaks Devices", "echo_gen1") + (devsOpt ? spanSm(" (Optional Zone Backup)", "violet") : sBLANK), description: spanSm(devsOpt ? "These devices are used when all zones are inactive." : "Select your devices", sCLRGRY), options: eDevsMap?.collectEntries { [(it?.key): "${it?.value?.label}${(it?.value?.lsd == true) ? "\n(Last Spoken To)" : sBLANK}"] }, multiple: true, required: (!settings.act_EchoZones), submitOnChange: true
            updDeviceInputs()
        } else { paragraph spanSmBld("No devices were found with support for ($type)", sCLRRED) }
    }
}

private actionVolumeInputs(List devices, Boolean showVolOnly=false, Boolean showAlrmVol=false) {
    if(showAlrmVol) {
        section(sectHead("Volume Options:")) {
            input "act_alarm_volume", sNUMBER, title: inTS1("Alarm Volume (0% - 100%)", sSPDKNB) + optPrefix(), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
        }
    } else {
        if((devices || settings.act_EchoZones) && (String)settings.actionType in [sSPEAK, sSPEAKP, sANN, sWEATH, "calendar", "music", "sounds", "builtin", "playback"]) {
            Map volMap = devsSupportVolume(devices)
            Integer volMapSiz = volMap?.n?.size()
            Integer devSiz = devices?.size()
            if(settings.act_EchoZones?.size() > 1) {
                section(sectHead("(Per Zone) Volume Options:"), hideable: true) {
                    input "act_EchoZones_vol_per_zone", sBOOL, title: inTS1("Set Per Zone Volume?", sQUES), defaultValue: false, submitOnChange: true
                    if((Boolean)settings.act_EchoZones_vol_per_zone) {
                        Map<String, Map> echoZones = (Map<String,Map>)((Map<String,Map>)getZones() ?: [:]).findAll { it.key in settings.act_EchoZones }
                        paragraph htmlLine(sCLR4D9)
                        echoZones.each { String znId, Map znData->
                            paragraph spanBld("Zone: ${znData.name}")
                            input "act_EchoZones_${znId}_volume_change", sNUMBER, title: inTS1("Volume Level (0% - 100%)", sSPDKNB) + optPrefix(), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
                            if(!showVolOnly) {
                                input "act_EchoZones_${znId}_volume_restore", sNUMBER, title: inTS1("Restore Volume (0% - 100%)", sSPDKNB) + optPrefix(), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
                            }
                        }
                    }
                }
            }
            section(sectHead("Volume Options:")) {
                if(devSiz == volMapSiz) { paragraph spanSmBld("None of the selected devices support volume control", sCLRORG) }
                else {
                    if(volMapSiz > 0 && volMapSiz < devSiz) { paragraph spanSmBld("NOTICE:<br>Some of the selected devices do not support volume control", sCLRORG) }
                    input "act_volume_change", sNUMBER, title: inTS1("Volume Level (0% - 100%)", sSPDKNB) + optPrefix(), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
                    if(!showVolOnly) {
                        input "act_volume_restore", sNUMBER, title: inTS1("Restore Volume (0% - 100%)", sSPDKNB) + optPrefix(), description: "(0% - 100%)", range: "0..100", required: false, submitOnChange: true
                    }
                }
            }
        }
    }
}

private Map getZoneVolumeMap() {
    Map znMap = [:]
    if((Boolean)settings.act_EchoZones_vol_per_zone) {
        Map<String,Map> echoZones = (Map<String,Map>)((Map<String,Map>)getZones() ?: [:]).findAll { it.key in settings.act_EchoZones }
        echoZones.each { String zId, Map zData ->
            Map aa = [:]
            aa.change = settings."act_EchoZones_${zId}_volume_change" ? settings."act_EchoZones_${zId}_volume_change" : null
            aa.restore = settings."act_EchoZones_${zId}_volume_restore" ? settings."act_EchoZones_${zId}_volume_restore" : null
            if(aa.keySet().size()) { znMap[zId] = aa }
        }
    }
    return znMap
}

def condTimePage() {
    return dynamicPage(name:"condTimePage", title: sBLANK, install: false, uninstall: false) {
        Boolean timeReq = (settings["cond_time_start"] || settings["cond_time_stop"])
        section(sectHead("Condition Start Time:")) {
            input "cond_time_start_type", sENUM, title: inTS1("Starting at...", sSTTIME), options: [(sTIME):"Time of Day", "sunrise":sCSUNRISE, "sunset":sCSUNSET], required: false , submitOnChange: true
            if(settings.cond_time_start_type  == sTIME) {
                input "cond_time_start", sTIME, title: inTS1("Start time", sSTTIME), required: timeReq, submitOnChange: true
            } else if((String)settings.cond_time_start_type in lSUNRISESET) {
                input "cond_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", sSTTIME), required: false, submitOnChange: true
            }
        }
        section(sectHead("Condition Stop Time:")) {
            input "cond_time_stop_type", sENUM, title: inTS1("Stopping at...", sSTTIME), options: [(sTIME):"Time of Day", "sunrise":sCSUNRISE, "sunset":sCSUNSET], required: false , submitOnChange: true
            if(settings.cond_time_stop_type == sTIME) {
                input "cond_time_stop", sTIME, title: inTS1("Stop time", sSTTIME), required: timeReq, submitOnChange: true
            } else if((String)settings.cond_time_stop_type in lSUNRISESET) {
                input "cond_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", sSTTIME), required: false, submitOnChange: true
            }
        }
    }
}

def uninstallPage() {
    return dynamicPage(name: "uninstallPage", title: "Uninstall", install: false , uninstall: true) {
        section(sBLANK) { paragraph spanSmBld("This will delete this Echo Speaks Action.", sCLRORG) }
    }
}

private void updDeviceInputs() {
    // log.trace "updDeviceInputs..."
    List aa = (List)settings.act_EchoDevices
    List devIds
    Boolean updList = false
    try {
        updList = (aa.size() && aa[0].id != null)
        devIds = aa.collect { it?.id?.toString() }
        // log.debug "updList(try): ${devIds.unique()}"
    } catch (ignored) {
        devIds = aa.collect { it?.toString() }
    }
    if(updList && devIds) {
        if(devModeFLD) log.debug "updList: $devIds"
        app.updateSetting( "act_EchoDevices", [type: "enum", value: devIds.unique()])
    }
    if(devIds) { app.updateSetting( "act_EchoDeviceList", [type: "capability", value: devIds.unique()]) } // this won't take effect until next execution
}

static Boolean wordInString(String findStr, String fullStr) {
    List<String> parts = fullStr?.split(sSPACE)?.collect { String it -> it?.toLowerCase() }
    return (findStr in parts)
}

def installed() {
    logInfo("Installed Event Received...")
    state.dateInstalled = getDtNow()
    if((Boolean)settings.duplicateFlag && !(Boolean)state.dupPendingSetup) {
        Boolean maybeDup = ((String)app?.getLabel())?.contains(" (Dup)")
        state.dupPendingSetup = true
        runIn(2, "processDuplication")
        if(maybeDup) logInfo("installed found maybe a dup... ${(Boolean)settings.duplicateFlag}")
    } else {
        if(!(Boolean)state.dupPendingSetup) initialize()
    }
}

@Field static final String dupMSGFLD = "This action is duplicated and has not had configuration completed... Please open action and configure to complete setup..."

def updated() {
    logInfo("Updated Event Received...")
    Boolean maybeDup = ((String)app?.getLabel())?.contains(" (Dup)")
    if(maybeDup) logInfo("updated found maybe a dup... ${(Boolean)settings.duplicateFlag}")
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
    updDeviceInputs()

// TODO go away at some point
// convert old alarm / hsm settings to new;  as attribute 'alarm' belongs to capability.alarm TODO this will go away if we offer alarm attribute
    if(settings.trig_alarm && !settings.trig_alarmSystemStatus){
        settingUpdate("trig_alarmSystemStatus", settings.trig_alarm, sENUM)
        if(settings.trig_alarm_events && !settings.trig_alarmSystemStatus_events){
            settingUpdate("trig_alarmSystemStatus_events", settings.trig_alarm_events, sENUM)
        }
        settingRemove("trig_alarm")
        settingRemove("trig_alarm_events")

        List<String> tl = [sNUMBER, sBOOL, sENUM, sBOOL, sNUMBER, sNUMBER, sNUMBER, sTEXT, sTEXT]
        Integer i = 0
        ["wait", "all", "cmd", "once", "after", "after_repeat", "after_repeat_cnt", "txt", "after_repeat_txt" ]?.each { ei->
            if(settings."trig_alarm_${ei}") settingUpdate("trig_alarmSystemStatus_${ei}".toString(), settings."trig_alarm_${ei}", tl[i])
            settingRemove("trig_alarm_${ei}".toString())
            i++
        }
    }
    if(settings.cond_alarm && !settings.cond_alarmSystemStatus){
        settingUpdate("cond_alarmSystemStatus", settings.cond_alarm, sENUM)
        settingRemove("cond_alarm")
    }
// convert carbon to attribute
    if(valTrigEvt('carbon')) {
        List<String> a = (List<String>)settings.triggerEvents
        a.remove(a.indexOf('carbon'))
        a.push('carbonMonoxide')
        settingUpdate("triggerEvents", a, sENUM)
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

@SuppressWarnings('unused')
private void processDuplication() {
    String al = (String)app?.getLabel()
    String newLbl = "${al}${al?.contains(" (Dup)") ? "" : " (Dup)"}"
    app?.updateLabel(newLbl)
    state.dupPendingSetup = true

    String dupSrcId = settings.duplicateSrcId ? (String)settings.duplicateSrcId : sNULL
    Map dupData = parent?.getChildDupeData("actions", dupSrcId)
    if(devModeFLD) log.debug "dupData: ${dupData}"
    if(dupData && dupData.state?.size()) {
        dupData.state.each { String k,v-> state[k] = v }
    }

    if(dupData && dupData.settings?.size()) {
        dupData.settings.each { String k, Map v->
            if((String)v.type in [sENUM, sMODE]) {
                settingRemove(k)
                settingUpdate(k, (v.value != null ? v.value : null), (String)v.type)
            }
        }
    }

    parent.childAppDuplicationFinished("actions", dupSrcId)
    logInfo("Duplicated Action has been created... Please open action and configure to complete setup...")
}

static String getObjType(obj) {
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

String getActionName() { return (String)settings.appLbl }

private void updAppLabel() {
    String newLbl = "${(String)settings.appLbl} (A${isPaused(true) ? " ${sPAUSESymFLD}" : sBLANK})".replaceAll(/ (Dup)/, sBLANK).replaceAll("\\s"," ")
    if((String)settings.appLbl && (String)app?.getLabel() != newLbl) { app?.updateLabel(newLbl) }
}

private void updConfigStatusMap() {
    Map sMap = [:]
    sMap.triggers = triggersConfigured()
    sMap.conditions = conditionsConfigured()
    sMap.actions = executionConfigured()
    sMap.tiers = isTierActConfigured()
    state.configStatusMap = sMap
}

Boolean getConfStatusItem(String item) {
    switch(item) {
       case "tiers": return isTierActConfigured()
       case "actions": return executionConfigured()
    }
    return (state.configStatusMap?.containsKey(item) && state.configStatusMap[item] == true)
}

private void actionCleanup() {
    stateMapMigration()
    // State Cleanup

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
        ["act_lights", "act_locks", "act_securityKeypads", "act_doors", "act_sirens"]?.each { String it -> settings.each { sI -> if(sI.key.startsWith(it)) { isTierAct ? setItems.push(sI.key as String) : setIgn.push(sI.key as String) } } }
        ["act_tier_start_", "act_tier_stop_"]?.each { String it -> settings.each { sI -> if(sI.key.startsWith(it)) { isTierAct ? setIgn.push(sI.key as String) : setItems.push(sI.key as String) } } }
        settings.each { si->
            if(!(si.key in setIgn) && si.key.startsWith("act_") && !si.key.startsWith("act_${(String)settings.actionType}") && (!isTierAct && si.key.startsWith("act_tier_item_"))) { setItems.push(si?.key as String) }
        }
    }

    // Cleanup Unused Condition settings...
    List<String> condKeys = settings.findAll { it?.key?.startsWith("cond_")  }?.keySet()?.collect { (String)((List)it?.tokenize("_"))[1] }?.unique()
    if(condKeys?.size()) {
        condKeys.each { String ck->
            if(!settings."cond_${ck}") {
                setItems.push("cond_${ck}")
                ["cmd", "all", "low", "high", "equal", "avg", "nums"]?.each { ei->
                    setItems.push("cond_${ck}_${ei}")
                }
            }
        }
    }

        // Cleanup Unused Trigger Types...
    if((List)settings.triggerEvents) {
        List<String> trigKeys = settings.findAll { it?.key?.startsWith("trig_") && !((String)((List)it?.key?.tokenize("_"))[1] in (List)settings.triggerEvents) }?.keySet()?.collect { (String)((List)it?.tokenize("_"))[1] }?.unique()
        // log.debug "trigKeys: $trigKeys"
        if(trigKeys?.size()) {
            trigKeys.each { String tk-> setItems.push("trig_${tk}"); ["events", "wait", "all", "avg", "cmd", "low", "high", "equal", "once", "after", "txt", "nums", "after_repeat", "after_repeat_cnt", "after_repeat_txt", "Codes"]?.each { ei-> setItems.push("trig_${tk}_${ei}") } }
        }
    }

    // Cleanup Unused Schedule Trigger Items
    setItems = setItems + ["trig_scheduled_sunState"]
    if(!settings.trig_scheduled_type) {
        setItems = setItems + ["trig_scheduled_daynums", "trig_scheduled_months", "trig_scheduled_type", "trig_scheduled_recurrence", "trig_scheduled_time", "trig_scheduled_weekdays", "trig_scheduled_weeks"]
    } else {
        switch(settings.trig_scheduled_type) {
            case "One-Time":
            case sCSUNRISE:
            case sCSUNSET:
                setItems = setItems + ["trig_scheduled_daynums", "trig_scheduled_months", "trig_scheduled_recurrence", "trig_scheduled_weekdays", "trig_scheduled_weeks"]
                if((String)settings.trig_scheduled_type in [sCSUNSET, sCSUNRISE]) { setItems.push("trig_scheduled_time") }
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

Boolean isPaused(Boolean chkAll = false) { return (Boolean)settings.actionPause && (chkAll ? !((Boolean)state.dupPendingSetup == true) : true) }

public void triggerInitialize() { runIn(3, "initialize") }

private Boolean valTrigEvt(String key) { return (key in (List<String>)settings.triggerEvents) }

public void updatePauseState(Boolean pause) {
    if((Boolean)settings.actionPause != pause) {
        logDebug("Received Request to Update Pause State to (${pause})")
        settingUpdate("actionPause", "${pause}", sBOOL)
        runIn(4, "updated")
    }
}

@SuppressWarnings('unused')
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
    //List schedTypes = ["One-Time", "Recurring", sCSUNRISE, sCSUNSET]
    String schedType = (String)settings.trig_scheduled_type
    Boolean recur = (schedType == "Recurring")
    def time = settings.trig_scheduled_time ?: null
    List dayNums = recur && settings.trig_scheduled_daynums ? settings.trig_scheduled_daynums?.collect { it as Integer }?.sort() : null
//    List weekNums = recur && settings.trig_scheduled_weeks ? settings.trig_scheduled_weeks?.collect { it as Integer }?.sort() : null
    List monthNums = recur && settings.trig_scheduled_months ? settings.trig_scheduled_months?.collect { it as Integer }?.sort() : null
    if(time) {
        String hour = fmtTime(time, "HH") ?: "0"
        String minute = fmtTime(time, "mm") ?: "0"
        String second = "0" //fmtTime(time, "mm") ?: "0"
        String daysOfWeek = settings.trig_scheduled_weekdays ? ((List)settings.trig_scheduled_weekdays)?.join(",") : sNULL
        String daysOfMonth = dayNums?.size() ? (dayNums?.size() > 1 ? "${dayNums?.first()}-${dayNums?.last()}" : dayNums[0]) : sNULL
        //String weeks = (weekNums && !dayNums) ? weekNums?.join(",") : sNULL
        String months = monthNums ? monthNums?.sort()?.join(",") : sNULL
        // log.debug "hour: ${hour} | m: ${minute} | s: ${second} | daysOfWeek: ${daysOfWeek} | daysOfMonth: ${daysOfMonth} | weeks: ${weeks} | months: ${months}"
        if(hour || minute || second) {
            List cItems = []
            cItems.push(second ?: "*")
            cItems.push(minute ?: "0")
            cItems.push(hour ?: "0")
            cItems.push(daysOfMonth ?: (!daysOfWeek ? "*" : "?"))
            cItems.push(months ?: "*")
            cItems.push(daysOfWeek ? daysOfWeek?.replaceAll("\"", sBLANK) : "?")
            if(year) { cItems.push(" ${year}") }
            cron = cItems.join(" ")
        }
    }
    logDebug("cronBuilder | Cron: ${cron}")
    return cron
}

Boolean schedulesConfigured() {
    if((String)settings.trig_scheduled_type in [sCSUNRISE, sCSUNSET]) { return true }
    else if((String)settings.trig_scheduled_type == "One-Time" && settings.trig_scheduled_time) { return true }
    else if((String)settings.trig_scheduled_type == "Recurring" && settings.trig_scheduled_time && settings.trig_scheduled_recurrence) {
        if((String)settings.trig_scheduled_recurrence == "Daily") { return true }
        else if ((String)settings.trig_scheduled_recurrence == "Weekly" && (List)settings.trig_scheduled_weekdays) { return true }
        else if ((String)settings.trig_scheduled_recurrence == "Monthly" && (settings.trig_scheduled_daynums || settings.trig_scheduled_weeks)) { return true }
    }
    return false
}

void scheduleSunriseSet() {
    if(isPaused(true)) { logWarn("Action is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    def sun = getSunriseAndSunset()
    Long ltim = (String)settings.trig_scheduled_type in [sCSUNRISE] ? sun.sunrise.time : sun.sunset.time
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
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No events will be monitored.", true); return }
    if(isPaused(true)) { logWarn("Action is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    ((List<String>)settings.triggerEvents)?.each { String te->
        if(te == "scheduled" || settings."trig_${te}") {
            switch (te) {
                case "scheduled":
                    // Scheduled Trigger Events
                    if (schedulesConfigured()) {
                        if((String)settings.trig_scheduled_type in [sCSUNRISE, sCSUNSET]) {
                            scheduleSunriseSet()
                            schedule('29 0 0 1/1 * ? * ', scheduleSunriseSet)  // run at 00:00:29 every day
                        }
                        else if((String)settings.trig_scheduled_type in ["One-Time", "Recurring"] && settings.trig_scheduled_time) { schedule(cronBuilder(), "scheduleTrigEvt") }
                    }
                    break
                case "guard":
                    // Alexa Guard Status Events
                    if(settings.trig_guard) state.handleGuardEvents = true
                    break
                case sALRMSYSST:
                    // Location Alarm Events
                    subscribe(location, "hsmStatus", alarmEvtHandler)
                    if("alerts" in (List)settings.trig_alarmSystemStatus) { subscribe(location, "hsmAlert", alarmEvtHandler) } // Only on Hubitat
                    break
                case sMODE:
                    // Location Mode Events
                    if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", sARE, sENUM) }
                    subscribe(location, sMODE, modeEvtHandler)
                    break
                case sPISTNEXEC:
                    break
                case sTHERMTEMP:
                    if (settings."trig_${te}_cmd") subscribe(settings."trig_${te}", sTEMP, getThermEvtHandlerName(te))
                    break
                default:
                    // Handles Remaining Device Events
                    subscribe(settings."trig_${te}", te, getDevEvtHandlerName(te))
                    break
            }
        }
    }
}

private getDevEvtHandlerName(String type) {
    // if(isTierAction()) { return "deviceTierEvtHandler" }
    return (type && (Integer)settings."trig_${type}_after"!=null) ? "devAfterEvtHandler" : "deviceEvtHandler"
}

private getThermEvtHandlerName(String type) {
    // if(isTierAction()) { return "deviceTierEvtHandler" }
    return (type && (Integer)settings."trig_${type}_after"!=null) ? "devAfterThermEvtHandler" : "thermostatEvtHandler"
}

private executeActTest() {
    logTrace("executeActTest STARTING")
    try {
        if((String)settings.actionType in [sSPEAK, sSPEAKP, sANN, sWEATH, "builtin", "calendar"]) {
            Map evt = getRandomTrigEvt()
            if(!evt) logWarn("no random event found")
            else {
                String te = (String)evt.name

                switch (te) {
                    case "scheduled":
                        // Scheduled Trigger Events
                        scheduleTrigEvt([date: new Date(), name: "test", value: "Stest", displayName: "Schedule Test"])
                        break
                    case "webCoRE": // sPISTNEXEC:
                        webCoRE_handler(evt)
                        break
                    case sTHERMTEMP:
                        String hand = getThermEvtHandlerName(te)
                        evt.name = sTEMP
                        "${hand}"(evt)
                        break
                    case "guard":
                        // Alexa Guard Status Events
                        guardEventHandler((String)evt.value)
                        break
                    case sALRMSYSST:
                        // Location Alarm Events
                        alarmEvtHandler(evt)
                        // two cases hsmStatus (above) and hsmAlert TODO
                        //if("alerts" in (List)settings.trig_alarmSystemStatus) { subscribe(location, "hsmAlert", alarmEvtHandler) } // Only on Hubitat
                        break
                    case sMODE:
                        // Location Mode Events
                        modeEvtHandler(evt)
                        break
                    default:
                        String hand = getDevEvtHandlerName(te)
                        // Handles Remaining Device Events
                        "${hand}"(evt)
                        break
                }
            }
//        }
//        if(!fnd) executeAction(evt, true, "executeActTest", false, false)
        } else logWarn("TEST of Action type ${settings.actionType} SKIPPED")
    } catch (ignored) {}
    settingUpdate("actTestRun", sFALSE, sBOOL)
    logTrace("executeActTest ENDING")
}

Map getRandomTrigEvt() {
    String trig = (String)getRandomItem((List)settings.triggerEvents)
    List trigItems = settings."trig_${trig}" ?: null
    def randItem = trigItems?.size() ? getRandomItem(trigItems) : null
    def trigItem = randItem ? (randItem instanceof String ? [displayName: null, id: null] :
            (trigItems?.size() ? trigItems?.find { it?.id?.toString() == randItem?.id?.toString() } : [displayName: null, id: null])) : null
    if(devModeFLD) log.debug("trig: ${trig} | trigItem: ${trigItem} | ${trigItem?.displayName} | ${trigItem?.id} | trigItems: ${trigItems}")
    Boolean isC = (getTemperatureScale()=="C")
    Map attVal = [
        (sSWITCH): getRandomItem(lONOFF),
        door: getRandomItem(lOPNCLS+["opening", "closing"]),
        (sCONTACT): getRandomItem(lOPNCLS),
        acceleration: getRandomItem(lACTINACT),
        (sLOCK): getRandomItem(lLOCKUNL +["unlocked with timeout"]),
        securityKeypad: getRandomItem(lSEC),
        (sWATER): getRandomItem(lWETDRY),
        presence: getRandomItem(lPRES),
        (sMOTION): getRandomItem(lACTINACT),
        (sVALVE): getRandomItem(lOPNCLS),
        windowShade: getRandomItem(lOPNCLS+["opening", "closing"]),
        (sPUSHED): getRandomItem([sPUSHED]),
        (sRELEASED): getRandomItem([sRELEASED]),
        (sHELD): getRandomItem([sHELD]),
        (sDBLTAP): getRandomItem([sDBLTAP]),
        smoke: getRandomItem(lDETECTCLR),
        carbonMonoxide: getRandomItem(lDETECTCLR),
        (sTEMP): isC ? getRandomItem(-1..29) : getRandomItem(30..80),
        illuminance: getRandomItem(1..100),
        (sHUMID): getRandomItem(1..100),
        (sLEVEL): getRandomItem(1..100),
        (sBATT): getRandomItem(1..100),
        (sPOWER): getRandomItem(100..3000),
        (sMODE): getRandomItem((List)location?.modes),
        alarmSystemStatus: getRandomItem(getAlarmTrigOpts()?.collect {(String)it.key}),
        guard: getRandomItem(["ARMED_AWAY", "ARMED_STAY"]),
        (sTHERMTEMP): isC ? getRandomItem(10..33) : getRandomItem(50..90),
        (sCOOLSP): isC ? getRandomItem(10..33) : getRandomItem(50..90),
        (sHEATSP): isC ? getRandomItem(10..33) : getRandomItem(50..90),
        thermostatMode: getRandomItem(getThermModeOpts()),
        thermostatFanMode: getRandomItem(getThermFanOpts()),
        thermostatOperatingState: getRandomItem(getThermOperStOpts()),
    ]
    Map evt = [ date: new Date(), deviceId: trigItem?.id?.toString() ?: null ]
    if(settings.enableWebCoRE && trig == sPISTNEXEC) {
        attVal.webCoRE = sPISTNEXEC
        trig = 'webCoRE'
        trigItem.displayName = 'webcore piston executed'
        String id = getRandomItem(getLocationPistons())
        evt = evt + [ jsonData: [id: id, name: getPistonById(id) ] ]
    }
    if(attVal.containsKey(trig)) {
        evt = evt + [ name: trig, displayName: trigItem?.displayName ?: sBLANK, value: attVal[trig]]
    } else evt = null
    if(devModeFLD) log.debug "getRandomTrigEvt | trig: ${trig} | Evt: ${evt}"
    return evt
}

@Field volatile static Map<String,Map> zoneStatusMapFLD = [:]

@SuppressWarnings('unused')
def zoneStateHandler(evt) {
    // TODO This is here as placeholder to prevent flooding the logs with errors after upgrading to v4.0
}

@SuppressWarnings('unused')
def zoneRemovedHandler(evt) {
    // TODO This is here as placeholder to prevent flooding the logs with errors after upgrading to v4.0
}

public updZones(Map zoneMap) {
    String myId=app.getId()
    zoneStatusMapFLD[myId] = zoneMap
    zoneStatusMapFLD = zoneStatusMapFLD
}

public Map getZones(Boolean noCache = false) {
    String myId=app.getId()
    Map a = zoneStatusMapFLD[myId]
    if(noCache || !a) {
        a = parent.getZones(true)
    }
    String i = 'initialized'
    if(a.containsKey(i))a.remove(i)
    return a
}
/*
private Map getZoneState(Integer znId) {
    Map zones = getZones()
    if(zones) {
        return zones[znId]
    }
    return null
} */

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

def scheduleTrigEvt(evt=null) {
    if(!evt) {
        Date adate = new Date()
        String dt = dateTimeFmt(adate, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        evt = [name: "scheduled", displayName: "Scheduled Trigger", value: fmtTime(dt), date: adate, deviceId: null]
    }
    Long evtDelay = now() - ((Date)evt.date).getTime()
    logTrace("${(String)evt.name} Event | Device: ${(String)evt.displayName} | Value: (${strCapitalize(evt.value)}) with a delay of ${evtDelay}ms")
    if (!schedulesConfigured()) { return }
    String schedType = (String)settings.trig_scheduled_type
    Boolean recur = schedType == "Recurring"
    Map dateMap = getDateMap()
    // log.debug "dateMap: $dateMap"
    String srecur = recur ? (String)settings.trig_scheduled_recurrence : sNULL
    List days = recur ? (List)settings.trig_scheduled_weekdays : null
    List daynums = recur ? (List)settings.trig_scheduled_daynums : null
    List weeks = recur ? (List)settings.trig_scheduled_weeks : null
    List months = recur ? (List)settings.trig_scheduled_months : null


    getTheLock(sHMLF, "scheduleTrigEvt")

    Map sTrigMap = (Map)getMemStoreItem("schedTrigMap", [:])
    if(!sTrigMap) sTrigMap = (Map)state.schedTrigMap ?: [:]

    Boolean wdOk = (days && srecur in ["Daily", "Weekly"]) ? (dateMap.dayNameShort in days && sTrigMap?.lastRun?.dayName != dateMap.dayNameShort) : true
    Boolean mdOk = daynums ? (dateMap.day in daynums && sTrigMap?.lastRun?.day != dateMap.day) : true
    Boolean wOk = (weeks && srecur in ["Weekly"]) ? (dateMap.week in weeks && sTrigMap?.lastRun?.week != dateMap.week) : true
    Boolean mOk = (months && srecur in ["Weekly", "Monthly"]) ? (dateMap.month in months && sTrigMap?.lastRun?.month != dateMap.month) : true
    if(wdOk && mdOk && wOk && mOk) {
        sTrigMap.lastRun = dateMap
        updMemStoreItem("schedTrigMap", sTrigMap)
        state.schedTrigMap = sTrigMap

        releaseTheLock(sHMLF)
        executeAction(evt, false, "scheduleTrigEvt", false, false)
    } else {
        releaseTheLock(sHMLF)
        logDebug("scheduleTrigEvt | SKIPPING | dayOfWeekOk: $wdOk | dayOfMonthOk: $mdOk | weekOk: $wOk | monthOk: $mOk")
    }
}

def alarmEvtHandler(evt) {
    Long evtDelay = now() - evt?.date?.getTime()
    String eN = (String)evt?.name
    def eV = evt?.value
    logTrace("${eN} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(eV)}) with a delay of ${evtDelay}ms")
    String inT = "trig_${sALRMSYSST}"
    List lT = (List)settings."${inT}"
    List lE = (List)settings."${inT}_events"
    Boolean ok2Run = !!(lT)
    if(ok2Run) {
        switch(eN) {
            case "hsmStatus":
            case sALRMSYSST:
                if(!(eV in lT)) ok2Run = false
                break
            case "hsmAlert":
                if (!("alerts" in lT && eV in lE)) ok2Run = false
                break
            default:
                ok2Run = false
        }
    }
    if(ok2Run) {
        Boolean dco = ((Boolean)settings."${inT}_once" == true)
        Integer dcw = (Integer)settings."${inT}_wait"!=null ? (Integer)settings."${inT}_wait" : null
        eventCompletion(evt, sALRMSYSST, dco, dcw, "alarmEvtHandler(${eN})", eV, (String)evt?.displayName)
    } else {
        logDebug("alarmEvtHandler | Skipping event ${eN}  value: ${eV}, did not match ${lT} ${lE}")
    }
}

public guardEventHandler(String guardState) {
    def evt = [name: "guard", displayName: "Alexa Guard", value: guardState, date: new Date(), deviceId: null]
    logTrace("${evt?.name} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)})")
    if((Boolean)state.handleGuardEvents && settings.trig_guard && (sANY in (List)settings.trig_guard || guardState in (List)settings.trig_guard)) {
        executeAction(evt, false, "guardEventHandler", false, false)
    }
}

def eventCompletion(evt, String dId, Boolean dco, Integer dcw, String meth, evtVal, String evtDis) {
    Boolean evtWaitOk = ((dco || dcw!=null) ? evtWaitRestrictionOk([date: evt?.date, deviceId: dId, value: evtVal, name: evt?.name, displayName: evtDis], dco, dcw) : true)
    if(!evtWaitOk) { return }
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, true)
    } else { executeAction(evt, false, meth, false, false) }
}

def webcoreEvtHandler(evt) {
    String eN = (String)evt?.name
    def eV = evt?.value
    String disN = evt?.jsonData?.name
    String pId = evt?.jsonData?.id
    List lT = (List)settings."${inT}"
    logTrace("${evt?.name?.toUpperCase()} Event | Piston: ${disN} | pistonId: ${pId} | with a delay of ${now() - evt?.date?.getTime()}ms")
    String inT = "trig_${sPISTNEXEC}"
    if(pId in lT) {
        Boolean dco = ((Boolean)settings."${inT}_once" == true)
        Integer dcw = (Integer)settings."${inT}_wait"!=null ? (Integer)settings."${inT}_wait" : null
        eventCompletion(evt, sPISTNEXEC, dco, dcw, "webcoreEvtHandler", disN, disN)
    } else {
        logTrace("webcoreEvtHandler | Skipping event ${eN}  value: ${eV}, ${pId} did not match ${lT}")
    }
}
// TODO not in use
/*
def sceneEvtHandler(evt) {
    logTrace("${evt?.name?.toUpperCase()} Event | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    Boolean dco = ((Boolean)settings.trig_scene_once == true)
    Integer dcw = (Integer)settings.trig_scene_wait ?: null
    eventCompletion(evt, "scene", dco, dcw, "sceneEvtHandler", evt?.value, (String)evt?.displayName)
}*/

def modeEvtHandler(evt) {
    logTrace("${evt?.name?.toUpperCase()} Event | Mode: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    String eN = (String)evt?.name
    def eV = evt?.value
    String inT = "trig_mode"
    List lT = (List)settings."${inT}"
    if(eV in lT) {
        Boolean dco = ((Boolean)settings."${inT}_once" == true)
        Integer dcw = (Integer)settings."${inT}_wait"!=null ? (Integer)settings."${inT}_wait" : null
        eventCompletion(evt, sMODE, dco, dcw, "modeEvtHandler", eV, (String)evt?.displayName)
    } else {
        logTrace("modeEvtHandler | Skipping event ${eN}  value: ${eV}, did not match ${lT}")
    }
}

void devAfterThermEvtHandler(evt) {
    evt.name = (String)evt.name == sTEMP ? sTHERMTEMP : (String)evt.name
    devAfterEvtHandler(evt)
}

void devAfterEvtHandler(evt) {
    Long evtT = (Long)((Date)evt.date).getTime()
    Long now = now()
    Long evtDelay = now - evtT
    String eN = (String)evt?.name
    def eV = evt?.value
    String dc = settings."trig_${eN}_cmd" ?: null
    Integer dcaf = (Integer)settings."trig_${eN}_after"!=null ? (Integer)settings."trig_${eN}_after" : null
    Integer dcafr = (Integer)settings."trig_${eN}_after_repeat" ?: null
    Integer dcafrc = (Integer)settings."trig_${eN}_after_repeat_cnt" ?: null
    String eid = "${evt?.deviceId}_${eN}"
    Boolean okpt1 = (dc && dcaf!=null)
    Boolean okpt2 = (okpt1 && eV == dc)
    String msg = "Device Event After | "

    logTrace(msg+"${eN?.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(eV)}) with a delay of ${evtDelay}ms | SchedCheck: (${okpt1}, ${okpt2})")
    Boolean rem = false

    getTheLock(sHMLF, "scheduleTrigEvt")

    Map aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if(!aEvtMap) aEvtMap = (Map)state.afterEvtMap ?: [:]

    if(aEvtMap.containsKey(eid) && !okpt2) {
        aEvtMap.remove(eid)
        rem = true
    }
    if(okpt2) { aEvtMap[eid] = [
            dt: evt?.date?.toString(),
            deviceId: evt?.deviceId as String,
            displayName: evt?.displayName,
            name: eN,
            value: eV,
            type: evt?.type,
            data: evt?.data,
            triggerState: dc, // desired comparison value for non-numbers;   or comparison type for number
            nextT: now + (dcaf*1000L),
            wait: dcaf,
            //timeLeft: dcaf,
            isRepeat: false,
            repeatWait: dcafr ?: null,
            repeatCnt: 0,
            repeatCntMax: dcafrc ]
    }
    updMemStoreItem("afterEvtMap", aEvtMap)
    state.afterEvtMap = aEvtMap
    Integer sz = aEvtMap.size()

    releaseTheLock(sHMLF)

    if(rem) logDebug(msg+"Removing ${evt?.displayName} from AfterEvtCheckMap | Reason: (${eN?.toUpperCase()}) no longer has the state of (${dc}) | Remaining Items: (${sz})")

    if(okpt2) {
        Boolean doRun=true
        if (dcaf == 0) {
            logTrace(msg+"Running afterEvent")
            afterEvtCheckHandler()
            doRun = false
        }
        if(doRun) {
            Integer ssec = dcaf >= 0 ? dcaf : 2
            runIn(ssec, "afterEvtCheckHandler")
            logTrace(msg+"Scheduled afterEvent in ${ssec} seconds")
        }
        // TODO remove
        if(!(Boolean)state.afterEvtCheckWatcherSched) {
            state.afterEvtCheckWatcherSched = true
            logTrace(msg+"Scheduling afterEvtCheckWatcher...")
            runEvery5Minutes("afterEvtCheckWatcher")
        }
    } else {
        logDebug(msg+"Passing to deviceEvtHandler")
        deviceEvtHandler(evt, true, false)
    }
}

void afterEvtCheckHandler() {
    unschedule("afterEvtCheckHandler")
    updTsVal("lastAfterEvtCheck")
    String msg = "afterEvtCheckHandler  | "
    logTrace(msg)

    getTheLock(sHMLF, "afterEvtCheckHandler")

    Map<String, Map> aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if (!aEvtMap) aEvtMap = (Map) state.afterEvtMap ?: [:]
    Boolean hasLock = true

    Long now = (Long) now() + 750L // anything in next 750ms runs now
    Map newMap = aEvtMap.findAll { it -> it?.value?.nextT < now }
    List<Long> sortList = newMap.collect { (Long) it?.value?.nextT }?.sort()

    sortList?.each {
        if (!hasLock) {
            getTheLock(sHMLF, "afterEvtCheckHandler")

            aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
            if (!aEvtMap) aEvtMap = (Map) state.afterEvtMap ?: [:]
            hasLock = true
        }
        def nextItem = aEvtMap.find { eM -> (Long)eM?.value?.nextT == it }
        Map nextVal = (Map)nextItem?.value ?: null
        String eN = (String)nextVal.name
        String edisplayN = (String)nextVal.displayName
        String edId = (String)nextVal.deviceId
        String nextId = (edId && eN) ? "${edId}_${eN}" : sNULL
        Boolean remEvt
        Boolean runEvt = false
        String msg1 = "Evaluating ${edisplayN} | (${eN?.capitalize()}) | Original Event Time: ${nextVal.dt} | "
        String msg2
        if (nextVal && nextId && aEvtMap[nextId]) {
            def devs = settings."trig_${eN}" ?: null

            def newVal = null // could be number or string
            if (devs && eN) {
                def dev = devs.find { it?.deviceId?.toString() == edId }
                if (dev) newVal = dev?.currentValue(att)
                if (newVal != nextVal.value) msg1 = msg1 + "value changed old: ${nextVal.value} new: ${newVal} | "
            }
            Map<String, Object> evt = [
                date       : new Date(),
                deviceId   : edId,
                displayName: edisplayN,
                name       : eN,
                value      : newVal,// ?: nextVal.value,
                type       : nextVal.type,
                data       : nextVal.data
            ]

            Boolean skipEvt = true
            if(eN in [sCOOLSP, sHEATSP, sTHERMTEMP, sHUMID, sTEMP, sPOWER, "illuminance", sLEVEL, sBATT]) {
                String dc = settings."trig_${eN}_cmd" // desired number comparison
                Boolean dca = ((Boolean)settings."trig_${eN}_all" == true)
                Boolean dcavg = (!dca && (Boolean)settings."trig_${eN}_avg" == true)
                Double dcl = settings."trig_${eN}_low"
                Double dch = settings."trig_${eN}_high"
                Double dce = settings."trig_${eN}_equal"
                Map valChk = deviceEvtProcNumValue(evt, devs, dc, dcl, dch, dce, dca, dcavg)
                skipEvt = !(Boolean)valChk.evtOk
                //evtAd = valChk.evtAd
            } else {
                if (nextVal.triggerState && edId && eN && devs) {
                    //String en = eN
                    //en = en == sTHERMTEMP ? sTEMP : en
                    skipEvt = !devAttValEqual(devs, edId, eN, nextVal.triggerState)
                }
            }

            Boolean hasRepeat = ((Integer)settings."trig_${eN}_after_repeat" != null)
            Boolean isRepeat = (Boolean)nextVal.isRepeat ?: false

            if (!skipEvt) {
                runEvt = true

                if(hasRepeat) {
                    aEvtMap[nextId].nextT = now() + ((Integer)nextVal.repeatWait * 1000L)
                    updMemStoreItem("afterEvtMap", aEvtMap)
                    state.afterEvtMap = aEvtMap
                }

                if (isRepeat && hasRepeat) {
                    Integer repeatCnt = ((Integer)nextVal.repeatCnt >= 0) ? (Integer)nextVal.repeatCnt + 1 : 1
                    Integer repeatCntMax = (Integer)nextVal.repeatCntMax ?: null
                    remEvt = (repeatCntMax && (repeatCnt == repeatCntMax))
                    aEvtMap[nextId].repeatCnt = repeatCnt
                    updMemStoreItem("afterEvtMap", aEvtMap)
                    state.afterEvtMap = aEvtMap
                    msg2 = "Issuing Repeat Event | RepeatWait: ${nextVal?.repeatWait} | isRepeat: ${isRepeat} | RepeatCnt: ${repeatCnt} | RepeatCntMax: ${repeatCntMax}"
                    if (remEvt) msg2 = "has repeated ${repeatCntMax} times | " + msg2

                } else { // this is after
                    aEvtMap[nextId].isRepeat = true
                    updMemStoreItem("afterEvtMap", aEvtMap)
                    state.afterEvtMap = aEvtMap
                    remEvt = !hasRepeat
                    msg2 = "Wait Threshold (${nextVal.wait} sec) Reached for ${eDisplayN} (${eN?.capitalize()}) | Issuing held event | TriggerState: (${nextVal.triggerState}) | hasRepeat: ${hasRepeat} "
                }
            } else {
                remEvt = true
                msg2 = "state is no longer ${nextVal.triggerState} | Skipping Action... "
            }
            if (remEvt) {
                aEvtMap.remove(nextId)
                updMemStoreItem("afterEvtMap", aEvtMap)
                state.afterEvtMap = aEvtMap
                msg2 = msg2 + "Removed event"
            }

            releaseTheLock(sHMLF)
            hasLock = false

            logDebug(msg + msg1 + msg2)
            if (runEvt) {
                deviceEvtHandler(evt, true, isRepeat)
            }
        }
    }

    if (!hasLock) {
        getTheLock(sHMLF, "afterEvtCheckHandler")

        aEvtMap = (Map) getMemStoreItem("afterEvtMap", [:])
        if (!aEvtMap) aEvtMap = (Map) state.afterEvtMap ?: [:]
        hasLock = true
    }
    Integer sz = aEvtMap.size()
    if (sz > 0) {
        //newMap = aEvtMap.findAll { it -> it?.value?.nextT < now }
        sortList = aEvtmap.collect { (Long) it?.value?.nextT }?.sort()
        releaseTheLock(sHMLF)
        hasLock = false

        Long myL = sortList.size() > 0 ? sortList[0] : null
        if (myL != null) {
            Integer ssecs = myL - now()
            ssecs = ssecs > 1 ? Math.min(ssecs, 300).toInteger : 2
            runIn(ssecs, "afterEvtCheckHandler")
            logTrace(msg+"Scheduled afterEvent in ${ssec} seconds; afterEvtMap: ${sz}")
        } else logWarn(msg+"INCONSISTENT")
    } else {
        releaseTheLock(sHMLF)
        hasLock = false
        clearAfterCheckSchedule()
    }

    if (hasLock) releaseTheLock(sHMLF)
}
/*
void afterEvtCheckHandler() {

    getTheLock(sHMLF, "afterEvtCheckHandler")

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
            Boolean hasRepeat = ((Integer)settings."trig_${nextVal.name}_after_repeat" != null)
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
                    Boolean skipEvt = true
                    if(nextVal.triggerState && nextVal.deviceId && nextVal.name && devs) {
                        String en = (String)nextVal.name
                        en = en == sTHERMTEMP ? sTEMP : en
                        skipEvt = !devAttValEqual(devs, (String)nextVal.deviceId, en, nextVal.triggerState)
                    }
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
                            Map<String,Object> tt=[date: parseDate(nextVal.repeatDt?.toString()), deviceId: (String)nextVal.deviceId, displayName: nextVal?.displayName, name: nextVal?.name, value: nextVal?.value, type: nextVal?.type, data: nextVal?.data, totalDur: fullElap]
                            deviceEvtHandler(tt, true, isRepeat)
                        } else {
                            aEvtMap.remove(nextId)
                            updMemStoreItem("afterEvtMap", aEvtMap)
                            releaseTheLock(sHMLF)
                            state.afterEvtMap = aEvtMap
                            hasLock = false
                            logDebug("Wait Threshold (${reqDur} sec) Reached for ${nextVal.displayName} (${nextVal.name?.toString()?.capitalize()}) | Issuing held event | TriggerState: (${nextVal.triggerState}) | EvtDuration: ${fullElap}")
                            Map<String,Object> tt = [date: parseDate(nextVal.dt?.toString()), deviceId: (String)nextVal.deviceId, displayName: nextVal.displayName, name: nextVal.name, value: nextVal.value, type: nextVal.type, data: nextVal.data]
                            deviceEvtHandler(tt, true)
                        }
                    } else {
                        aEvtMap.remove(nextId)
                        updMemStoreItem("afterEvtMap", aEvtMap)
                        releaseTheLock(sHMLF)
                        state.afterEvtMap = aEvtMap
                        hasLock = false
                        String msg1 = "${nextVal?.displayName} | (${nextVal?.name?.toString()?.capitalize()}) "
                        if(!skipEvt && skipEvtCnt) {
                            logDebug(msg1 + "has repeated ${repeatCntMax} times | Skipping Action Repeat...")
                        } else {
                            logDebug(msg1 + "state is no longer ${nextVal.triggerState} | Skipping Action...")
                        }
                    }
                }
            }
        }
        // log.debug "nextId: $nextId | timeLeft: ${timeLeft}"
        if(hasLock) releaseTheLock(sHMLF)

//        runIn(1, "scheduleAfterCheck", [data: [val: timeLeft, id: nextId, repeat: isRepeat]])
//        logTrace("afterEvtCheckHandler scheduleAfterCheck in 1 second")
        // logTrace("afterEvtCheckHandler Remaining Items: (${aEvtMap?.size()})")
    } else {
        releaseTheLock(sHMLF)
        clearAfterCheckSchedule()
    }
    updTsVal("lastAfterEvtCheck")
}
*/
// TODO remove
Integer getLastAfterEvtCheck() { return getLastTsValSecs("lastAfterEvtCheck") }

void afterEvtCheckWatcher() {

    getTheLock(sHMLF, "afterEvtCheckWatcher")

    Map aEvtMap = (Map)getMemStoreItem("afterEvtMap", [:])
    if(!aEvtMap) aEvtMap = (Map)state.afterEvtMap ?: [:]

//    Map aSchedMap = (Map)getMemStoreItem("afterEvtChkSchedMap", null)
//    if(!aSchedMap) aSchedMap = (Map)state.afterEvtChkSchedMap ?: null

    //if((aEvtMap.size() == 0 && aSchedMap?.id) || (aEvtMap.size() && getLastAfterEvtCheck() > 240)) {
    releaseTheLock(sHMLF)

    if(aEvtMap.size() && getLastAfterEvtCheck() > 240) {
        runIn(2, "afterEvtCheckHandler")
        logDebug("afterEvtCheckWatcher scheduled afterEvtCheckHandler...")
    }
}
/*
def scheduleAfterCheck(data) {
    Integer val = data?.val ? (data.val < 2 ? 2 : data.val-4) : 60
    String id = data?.id?.toString() ?: null
    Boolean rep = (data?.repeat == true)

    getTheLock(sHMLF, "scheduleAfterCheck")

    Map aSchedMap = (Map)getMemStoreItem("afterEvtChkSchedMap", null)
    if(!aSchedMap) aSchedMap = (Map)state.afterEvtChkSchedMap ?: null

    runIn(val, "afterEvtCheckHandler")

    Map a = [id: id, dur: val, dt: getDtNow()]
    state.afterEvtChkSchedMap = a
    updMemStoreItem("afterEvtChkSchedMap", a)

    releaseTheLock(sHMLF)

    if(devModeFLD && aSchedMap && aSchedMap?.id?.toString() && id && aSchedMap?.id?.toString() == id) {
        log.debug "scheduleAfterCheck: Active Schedule Id (${aSchedMap?.id}) is the same as the requested schedule ${id}."
    }
    logDebug("Schedule After Event Check${rep ? " (Repeat)" : sBLANK} in (${val} seconds) | Id: ${id}")
}
*/
private clearAfterCheckSchedule() {
    unschedule("afterEvtCheckHandler")
    unschedule("afterEvtCheckWatcher") // TODO remove
    state.afterEvtCheckWatcherSched = false
    logTrace("Clearing After Event Check Schedule...")

    getTheLock(sHMLF, "clearAfterCheckSchedule")

    updMemStoreItem("afterEvtChkSchedMap", null)
    state.afterEvtChkSchedMap = null

    releaseTheLock(sHMLF)
}

void thermostatEvtHandler(evt) {
    evt.name = (String)evt.name == sTEMP ? sTHERMTEMP : (String)evt.name
    deviceEvtHandler(evt)
}

void deviceEvtHandler(evt, Boolean aftEvt=false, Boolean aftRepEvt=false) {
    Long evtDelay = now() - (Long)((Date)evt.date).getTime()
    Boolean evtOk = false
    Boolean evtAd = false
    String eN = (String)evt.name
    def eV = evt?.value
    List d = settings."trig_${eN}"
    String aftMsg = "${aftEvt ? " | (aftEvt)" : sBLANK}${aftRepEvt ? " | (aftRepEvt)" : sBLANK}"
    logTrace("Device Event | ${eN.toUpperCase()} | Name: ${evt?.displayName} | Value: (${strCapitalize(eV)}) with a delay of ${evtDelay}ms${aftMsg}")
    String dc = settings."trig_${eN}_cmd" // desired attribute value
    Boolean dca = ((Boolean)settings."trig_${eN}_all" == true)
    Boolean dcavg = (!dca && (Boolean)settings."trig_${eN}_avg" == true)
    //Boolean dco = ((Integer)settings."trig_${eN}_after"==null && (Boolean)settings."trig_${eN}_once" == true)
    //Integer dcw = ((Integer)settings."trig_${eN}_after"==null && (Integer)settings."trig_${eN}_wait") ? (Integer)settings."trig_${eN}_wait" : null
    Boolean dco = ((Boolean)settings."trig_${eN}_once" == true)
    Integer dcw = (Integer)settings."trig_${eN}_wait"!=null ? (Integer)settings."trig_${eN}_wait" : null
    String extra = sBLANK
    switch(eN) {
        case sSWITCH:
        case sLOCK:
        case "securityKeypad":
        case "door":
        case "smoke":
        case "carbonMonoxide":
        case "windowShade":
        case "presence":
        case sCONTACT:
        case "acceleration":
        case sMOTION:
        case sWATER:
        case sVALVE:
        case "thermostatFanMode":
        case "thermostatOperatingState":
        case "thermostatMode":
            if(d?.size() && dc) {
                if(dc == sANY) { evtOk = true }
                else {
                    if(dca && (allDevAttValsEqual(d, dc, (String)eV))) { evtOk = true; evtAd = true }
                    else if(eV == dc) { evtOk=true }
                }
            }
            if(evtOk && eN in [sLOCK, "securityKeypad"] && (String)evt.value in ["disarmed", "unlocked"]) {
                List dcn = settings."trig_${eN}_Codes"
                if(dcn) {
                    String theCode = evt?.data
                    Map data = null
                    if(theCode) data = getCodes(null, theCode)
//                    log.debug "converted $theCode to ${data}  starting with ${evt?.data}"
                    List<String> theCList = data?.collect { (String)it.key }
//                    log.debug "found code $theCList"
                    if(!theCList || !(theCList[0] in dcn)) {
                        evtOk = false
                        extra = " FILTER REMOVED the received code (${theCode}), code did not match trig_${eN}_Codes (${dcn})"
                    }
                }
            }
            break
        case sPUSHED:
        case sRELEASED:
        case sHELD:
        case sDBLTAP:
            def dcn = settings."trig_${eN}_nums"
            if(d?.size() && dc && dcn && dcn?.size() > 0) {
                if(dcn?.contains(eV)) { evtOk = true }
            }
//log.debug "deviceEvtHandler: dcn: $dcn | d: $d | dc: $dc | dco: $dco | dcw: $dcw | evt.value: ${eV}"
            break
        case sCOOLSP:
        case sHEATSP:
        case sTHERMTEMP:
        case sHUMID:
        case sTEMP:
        case sPOWER:
        case "illuminance":
        case sLEVEL:
        case sBATT:
            Double dcl = settings."trig_${eN}_low"
            Double dch = settings."trig_${eN}_high"
            Double dce = settings."trig_${eN}_equal"
            Map valChk = deviceEvtProcNumValue(evt, d, dc, dcl, dch, dce, dca, dcavg)
            evtOk = valChk.evtOk
            evtAd = valChk.evtAd
            break
        default:
            logDebug("deviceEvtHandler | unknown event ${eN}  value: ${eV}")
    }
    Boolean devEvtWaitOk = ((dco || dcw!=null) ? evtWaitRestrictionOk(evt, dco, dcw, aftRepEvt) : true)
    Boolean execOk = (evtOk && devEvtWaitOk)
    logDebug("deviceEvtHandler | execOk: ${execOk} | evtOk :${evtOk} | devEvtWaitOk: ${devEvtWaitOk} | event all devices evtAd: $evtAd${aftMsg}${extra}")
    //if(!devEvtWaitOk) { return }
    if(getConfStatusItem("tiers")) {
        processTierTrigEvt(evt, execOk)
    } else {
        if (execOk) { executeAction(evt, false, "deviceEvtHandler(${eN})", evtAd, aftRepEvt) }
        else logTrace("deviceEvtHandler | Skipping event ${eN}  value: ${eV}")
    }
}

private void processTierTrigEvt(evt, Boolean evtOk) {
    String meth = "processTierTrigEvt"
    String msg = " | Name: ${evt?.name} | Value: ${evt?.value} | EvtOk: ${evtOk}"
    logTrace(meth + msg)

    getTheLock(sHMLF, meth)

    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = (Map)state.actTierState ?: [:]

    if (evtOk) {
        if(aTierSt.size()) {
            msg = meth + " found already active tier state ${aTierSt}"+msg
        } else {
            releaseTheLock(sHMLF)
            logDebug(meth + " activating tier state" + msg)
            tierEvtHandler(evt)
            return
        }
    } else if(!evtOk && settings.act_tier_stop_on_clear == true) {
        def tierConf = aTierSt.evt
        if(tierConf?.size() && tierConf?.name == evt?.name && tierConf?.deviceId == evt?.deviceId) {
            updMemStoreItem("actTierState", [:])
            state.actTierState = [:]

            releaseTheLock(sHMLF)
            logDebug(meth + " Tier Trigger no longer valid... Clearing TierState and Schedule..."+msg)
            unschedule("tierEvtHandler")
            atomicState.tierSchedActive = false
            updTsVal("lastTierRespStopDt")
            return
        } else msg = meth + " Tier Trigger valid... exiting...${aTierSt}"+msg
    } else msg = meth + " no action ${aTierSt}"+msg

    releaseTheLock(sHMLF)
    logDebug(msg)
}

def getTierStatusSection() {
    String str = sBLANK
    if(isTierAction()) {
        Map lTierMap = getTierMap()
        Boolean tsa = (Boolean)atomicState.tierSchedActive == true

        getTheLock(sHMLF, "getTierStatusSection")

        Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
        if(!aTierSt) aTierSt = (Map)state.actTierState ?: [:]

        Map tS = aTierSt
        str += "Tier Size: ${lTierMap?.size()}\n"
        str += "Schedule Active: ${tsa}\n"
        str += tS?.cycle ? "Tier Cycle: ${tS?.cycle}\n" : sBLANK
        str += tS?.schedDelay ? "Next Delay: ${tS?.schedDelay}\n" : sBLANK
        str += tS?.lastMsg ? "Is Last Cycle: ${tS?.lastMsg == true}\n" : sBLANK

        releaseTheLock(sHMLF)

        String a = getTsVal("lastTierRespStartDt")
        str += a ? "Last Tier Start: ${a}\n" : sBLANK
        a = getTsVal("lastTierRespStopDt")
        str += a ? "Last Tier Stop: ${a}\n" : sBLANK
        section("Tier Response Status: ") {
            paragraph pTS(str, sNULL, false, sCLR4D9)
        }
    }
}

private void resumeTierJobs() {

    getTheLock(sHMLF, "resumeTierJobs")

    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = (Map)state.actTierState ?: [:]

    if(aTierSt?.size()) {
        releaseTheLock(sHMLF)
        if((Boolean)atomicState.tierSchedActive == true) {
            tierEvtHandler()
        }
        return
    }
    releaseTheLock(sHMLF)
}

private void tierEvtHandler(evt=null) {
    if(!evt) {
        Date adate = new Date()
        String dt = dateTimeFmt(adate, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        evt = [name: "Tiered Schedule", displayName: "Scheduled Tiered Trigger", value: fmtTime(dt), date: adate, deviceId: null]
    }
    logTrace("tierEvtHandler: received event ${evt?.name} ${evt?.value}")

    Map t0 = getTierMap()
    Map tierMap = t0 ?: [:]

    getTheLock(sHMLF, "tierEvtHandler")

    Map aTierSt = (Map)getMemStoreItem("actTierState", [:])
    if(!aTierSt) aTierSt = (Map)state.actTierState ?: [:]

    Map tierState = aTierSt
    // TODO
    //log.debug "tierState: ${tierState}"
    //log.debug "tierMap: ${tierMap}"
    if(tierMap.size()) {
        Map newEvt = (Map)tierState.evt ?: [name: evt?.name, displayName: evt?.displayName, value: evt?.value, unit: evt?.unit, deviceId: evt?.deviceId, date: evt?.date]
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

            updMemStoreItem("actTierState", tierState)
            state.actTierState = tierState

            releaseTheLock(sHMLF)

            logTrace("tierEvtHandler: tierSize: (${tierMap.size()}) | cycle: ${tierState.cycle} | curPass: (${curPass}) | nextPass: ${curPass+1} | schedDelay: (${tierState.schedDelay}) | Message: (${tierState.message}) | LastMsg: (${tierState.lastMsg})")

            if(curPass == 1) { updTsVal("lastTierRespStartDt"); remTsVal("lastTierRespStopDt") }

            tierSchedHandler([sched: true, tierState: tierState])
        } else {

            updMemStoreItem("actTierState", [:])
            state.actTierState = [:]
            releaseTheLock(sHMLF)
            atomicState.tierSchedActive = false
            updTsVal("lastTierRespStopDt")
            logDebug("tierEvtHandler: Tier Cycle has completed... Clearing TierState...")
        }
    } else releaseTheLock(sHMLF)
}

private void tierSchedHandler(data) {
    if(data && data.tierState?.size() && data.tierState?.message) {
        logTrace("tierSchedHandler(${data})")
        Map evt = data.tierState.evt
        executeAction(evt, false, "tierSchedHandler", false, false, [msg: data?.tierState?.message as String, volume: data?.tierState?.volume, isFirst: (data?.tierState?.cycle == 1), isLast: (data?.tierState?.lastMsg == true)])
        if(data?.sched) {
            if(data.tierState.schedDelay && data.tierState.lastMsg == false) {
                logDebug("tierSchedHandler: Scheduling Next Tier Message for (${data.tierState?.schedDelay} seconds)")
                runIn(data.tierState.schedDelay, "tierEvtHandler")
            } else {
                logDebug("tierSchedHandler: Scheduling cleanup for (5 seconds) as this was the last message")
                runIn(5, "tierEvtHandler")
            }
            atomicState.tierSchedActive = true
        }
    }
}

Map deviceEvtProcNumValue(evt, List devs=null, String cmd=sNULL, Double dcl=null, Double dch=null, Double dce=null, Boolean dca=false, Boolean dcavg=false) {
    Boolean evtOk = false
    Boolean evtAd = false
    String en = (String)evt.name
    String eV = evt.value?.toString()
    Double evtValue = evt.value?.toDouble()
    if(devs?.size() && cmd && eV?.isNumber()) {
        en = en == sTHERMTEMP ? sTEMP : en
        evtValue = dcavg ? getDevValueAvg(devs, en) : evtValue
        Boolean not=false
        switch(cmd) {
            case sEQUALS:
                if(dce) {
                    if(!dca && dce == evtValue) evtOk=true
                    else if(dca && allDevAttNumValsEqual(devs, en, dce)) { evtOk=true; evtAd=true }
                }
                break
            case sNBETWEEN:
                not=true
            case sBETWEEN:
                if(dcl && dch) {
                    if(!dca) {
                        evtOk = (evtValue in (dcl..dch))
                        if(not) evtOk=!evtOk
                    } else if(dca && allDevAttNumValsBetween(devs, en, dcl, dch, not)) { evtOk=true; evtAd=true }
                }
                break
            case sABOVE:
                if(dch) {
                    if(!dca && (evtValue > dch)) evtOk=true
                    else if(dca && allDevAttNumValsAbove(devs, en, dch)) { evtOk=true; evtAd=true }
                }
                break
            case sBELOW:
                if(dcl) {
                    if(!dca && (evtValue < dcl)) evtOk=true
                    else if(dca && allDevAttNumValsBelow(devs, en, dcl)) { evtOk=true; evtAd=true }
                }
                break
        }
    }
    Map result = [evtOk: evtOk, evtAd: evtAd]
    logDebug("deviceEvtProcNumValue | result: $result | evtName: ${evt.name} (${en}) | evtVal: $evtValue ($eV) | cmd: ${cmd} | low: ${dcl} | high: ${dch} | equal: ${dce} | all: ${dca} | avg: $dcavg")
    return result
}

static String evtValueCleanup(val) {
    // log.debug "val(in): ${val}"
    val = (val?.toString()?.isNumber() && val?.toString()?.endsWith(".0")) ? val?.toDouble()?.round(0) : val
    // log.debug "val(out): ${val}"
    return val?.toString()
}

private void clearEvtHistory() {
    settingUpdate("clrEvtHistory", sFALSE, sBOOL)

    getTheLock(sHMLF, "clearEvtHistory")

    updMemStoreItem("valEvtHistory", [:])
    state.valEvtHistory = [:]

    releaseTheLock(sHMLF)
}

private Boolean evtWaitRestrictionOk(evt, Boolean once, Integer wait, Boolean aftRepEvt=false) {
    Boolean ok = true
    Long dur
    Boolean waitOk = (wait!=null && wait >= 0)
    Boolean dayOk
    String n = (String)evt?.name
    String msg = "evtWaitRestrictionOk: Last ${n?.capitalize()} Event for Device | onceDaily ${once} | wait ${wait} | repeatEvt: ${aftRepEvt} | "
    Date evtDt = (Date)evt?.date

    getTheLock(sHMLF, "evtWaitRestrictionOk")

    Map evtHistMap = (Map)getMemStoreItem("valEvtHistory", [:])
    if(!evtHistMap) evtHistMap = (Map)state.valEvtHistory ?: [:]

    // log.debug "prevDt: ${evtHistMap[n]?.dt ? parseDate(evtHistMap[n]?.dt as String) : null} | evtDt: ${evtDt}"
    if(evtHistMap.containsKey(n) && evtHistMap[n]?.dt) {
        Date prevDt = parseDate(evtHistMap[n].dt)
        // log.debug "prevDt: ${prevDt.toString()}"
        if(prevDt && evtDt) {
            dur = Math.round(((Long)evtDt.getTime() - (Long)prevDt.getTime())/1000.0D)
            waitOk = waitOk ? (dur && (wait < dur)) : true
            dayOk = !once || (once && !isDateToday(prevDt))
            ok = (waitOk && dayOk)
            msg += "Occurred: (${dur} sec ago) | Desired Wait: (${wait} sec) - (waitOk $waitOk && dayOk $dayOk)"
        }
    } else msg += "No history found"
    // check if same and don't save?
    if(ok) {
        evtHistMap[n] = [dt: evt?.date?.toString(), value: evt?.value, name: n]
        updMemStoreItem("valEvtHistory", evtHistMap)
        state.valEvtHistory = evtHistMap
    }

    releaseTheLock(sHMLF)
    msg += " Status: (${ok ? okSymFLD : notOkSymFLD})"
    logDebug(msg)
    return ok
}

static String getAttrPostfix(String attr) {
    switch(attr) {
        case sHUMID:
        case sLEVEL:
        case sBATT:
            return " percent"
        case sTHERMTEMP:
        case sTEMP:
            return " degrees"
        case "illuminance":
            return " lux"
        case sPOWER:
            return " watts"
        default:
            return sBLANK
    }
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
    String startType = (String)settings.cond_time_start_type
    String stopType = (String)settings.cond_time_stop_type
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
            logTrace("timeCondOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not? startTime:stopTime})")
            return isBtwn
        }
    }
    logTrace("timeCondOk | (null)")
    state.startTime = sNULL
    state.stopTime = sNULL
    return null
}

Boolean dateCondOk() {
    Boolean result = null
    Boolean dOk
    Boolean mOk
    if((List)settings.cond_days || (List)settings.cond_months) {
        Boolean reqAll = reqAllCond()
        dOk = settings.cond_days ? (isDayOfWeek((List)settings.cond_days)) : reqAll // true
        mOk = settings.cond_months ? (isMonthOfYear((List)settings.cond_months)) : reqAll //true
        result = reqAll ? (mOk && dOk) : (mOk || dOk)
    }
    logTrace("dateCondOk | $result | monthOk: $mOk | daysOk: $dOk")
    return result
}

Boolean locationCondOk() {
    Boolean result = null
    Boolean mOk
    Boolean aOk
    if((List)settings.cond_mode || (String)settings.cond_mode_cmd || (List)settings.cond_alarmSystemStatus) {
        Boolean reqAll = reqAllCond()
        mOk = ((List)settings.cond_mode /*&& (String)settings.cond_mode_cmd*/) ? (isInMode((List)settings.cond_mode, ((String)settings.cond_mode_cmd == "not"))) : reqAll //true
        aOk = (List)settings.cond_alarmSystemStatus ? isInAlarmMode((List)settings.cond_alarmSystemStatus) : reqAll //true
        result = reqAll ? (mOk && aOk) : (mOk || aOk)
    }
    logTrace("locationCondOk | $result | modeOk: $mOk | alarmOk: $aOk")
    return result
}

Boolean checkDeviceCondOk(String att) {
    List devs = (List)settings."cond_${att}" ?: null
    def cmdVal = settings."cond_${att}_cmd" ?: null  // list or string
    Boolean all = ((Boolean)settings."cond_${att}_all" == true)
    if( !(att && devs && cmdVal) ) { return true }
    return all ? allDevAttValsEqual(devs, att, cmdVal) : anyDevAttValsEqual(devs, att, cmdVal)
}

Boolean checkDeviceNumCondOk(String att) {
    List devs = (List)settings."cond_${att}" ?: null
    String cmd = settings."cond_${att}_cmd" ?: null
    Double dcl = settings."cond_${att}_low"!=null ? settings."cond_${att}_low" : null
    Double dch = settings."cond_${att}_high"!=null ? settings."cond_${att}_high" : null
    Double dce = settings."cond_${att}_equal"!=null ? settings."cond_${att}_equal" : null
    Boolean dca = ((Boolean)settings."cond_${att}_all" == true) ?: false
    if( !(att && devs && cmd) ) { return true }
    Boolean not=false
    Boolean a = true
    switch(cmd) {
        case sEQUALS:
            if(dce) {
                a = dca ? allDevAttNumValsEqual(devs, att, dce) : anyDevAttNumValEqual(devs, att, dce)
            }
            break
        case sNBETWEEN:
            not=true
        case sBETWEEN:
            if(dcl && dch) {
                a = dca ? allDevAttNumValsBetween(devs, att, dcl, dch, not) : anyDevAttNumValBetween(devs, att, dcl, dch, not)
            }
            break
        case sABOVE:
            if(dch) {
                a = dca ? allDevAttNumValsAbove(devs, att, dch) : anyDevAttNumValAbove(devs, att, dch)
            }
            break
        case sBELOW:
            if(dcl) {
                a = dca ? allDevAttNumValsBelow(devs, att, dcl) : anyDevAttNumValBelow(devs, att, dcl)
            }
            break
    }
    return a
}

@Field static List<String> lDATTSTR = ["switch", "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "door", "windowShade", "valve", "water", "thermostatMode", "thermostatOperatingState", "thermostatFanMode"]
@Field static List<String> lDATTNUM = ["temperature", "humidity", "illuminance", "level", "power", "battery", "coolingSetpoint", "heatingSetpoint", "thermostatTemperature"]
// these are triggers only pushed released held doubleTapped smoke, carbonMonoxide

Boolean deviceCondOk() {
    List<String> skipped = []
    List<String> passed = []
    List<String> failed = []
    lDATTSTR.each { String i->
        if(!(List)settings."cond_${i}") { skipped.push(i); return }
        checkDeviceCondOk(i) ? passed.push(i) : failed.push(i)
    }
    lDATTNUM.each { String i->
        if(!(List)settings."cond_${i}") { skipped.push(i); return }
        checkDeviceNumCondOk(i) ? passed.push(i) : failed.push(i)
    }
    Integer cndSize = (passed.size() + failed.size())
    Boolean result = null
    if(cndSize != 0) result = reqAllCond() ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
    logTrace("deviceCondOk | ${result} | Found: (${(passed?.size() + failed?.size())}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return result
}

Map conditionStatus() {
    Boolean reqAll = reqAllCond()
    List<String> failed = []
    List<String> passed = []
    List<String> skipped = []
    Boolean ok = true
    if((Boolean)state.dupPendingSetup) ok = false
    Integer cndSize = (Integer)null
    if(ok) {
        [sTIME, "date", "location", "device"]?.each { String i->
            def s = "${i}CondOk"()
            if(s == null) { skipped.push(i); return }
            s ? passed.push(i) : failed.push(i)
        }
        cndSize = (passed.size() + failed.size())
        ok = reqAll ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
        if(cndSize == 0) ok = true
    }
    logTrace("conditionsStatus | ok: $ok | RequireAll: ${reqAll} | Found: (${cndSize}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return [ok: ok, passed: passed, blocks: failed]
}

Boolean devCondConfigured(String att) {
    return ((List)settings."cond_${att}" && settings."cond_${att}_cmd")
}

Boolean devNumCondConfigured(String att) {
    return (settings."cond_${att}_cmd" && (settings."cond_${att}_low"!=null || settings."cond_${att}_high"!=null || settings."trig_${att}_equal"!=null))
}

Boolean timeCondConfigured() {
    Boolean startTime = ((String)settings.cond_time_start_type in lSUNRISESET || ((String)settings.cond_time_start_type == sTIME && settings.cond_time_start))
    Boolean stopTime = ((String)settings.cond_time_stop_type in lSUNRISESET || ((String)settings.cond_time_stop_type == sTIME && settings.cond_time_stop))
    return (startTime && stopTime)
}

Boolean dateCondConfigured() {
    Boolean days = (settings.cond_days)
    Boolean months = (settings.cond_months)
    return (days || months)
}

Boolean locationModeConfigured() {
    if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", sARE, sENUM) }
    return ((List)settings.cond_mode && (String)settings.cond_mode_cmd)
}

Boolean locationAlarmConfigured() {
    return ((List)settings.cond_alarmSystemStatus)
}

Boolean deviceCondConfigured() {
    return (deviceCondCount() > 0)
}

Integer deviceCondCount() {
    List<String> devAtts = lDATTSTR + lDATTNUM
    List items = []
    devAtts.each { String dc-> if(devCondConfigured(dc)) { items.push(dc) } }
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

static String convEvtType(String type) {
    Map typeConv = [
        (sPISTNEXEC): "Piston",
        (sALRMSYSST): "Alarm system status",
        "hsmStatus": "Alarm system status",
        "hsmAlert": "Alarm system alert"
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
        str = (str.contains("%value%") && evt.value != null) ? str.replaceAll("%value%", evt.value?.toString()?.isNumber() ? evtValueCleanup(evt?.value) : evt?.value) : str
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
    return 0
}

static Integer durationToHours(dur) {
    if(dur && dur>= (60*60)) return (dur / 60 / 60)?.toInteger()
    return 0
}
/*
private List decamelizeStr(String str) {
    return (List) str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
} */

String getResponseItem(evt, String tierMsg=sNULL, Boolean evtAd=false, Boolean isRepeat=false, Boolean testMode=false) {
    String eN = (String)evt?.name
    def eV = evt?.value
    logTrace("getResponseItem | EvtName: ${eN} | EvtDisplayName: ${evt?.displayName} | EvtValue: ${eV} | AllDevsResp: ${evtAd} | Repeat: ${isRepeat} | TestMode: ${testMode}")
    String glbText = (String)settings."act_${(String)settings.actionType}_txt" ?: sNULL
    if(glbText) {
        List<String> eTxtItems = glbText.tokenize(";")
        return decodeVariables(evt, (String)getRandomItem(eTxtItems))
    } else if(tierMsg) {
        List<String> eTxtItems = tierMsg.tokenize(";")
        return decodeVariables(evt, (String)getRandomItem(eTxtItems))
    } else {
        List devs = settings."trig_${eN}" ?: []
        // String dc = (String)settings."trig_${eN}_cmd"
        // Boolean dca = ((Boolean)settings."trig_${eN}_all" == true)
        String dct = (String)settings."trig_${eN}_txt" ?: sNULL
        String dcart = (Integer)settings."trig_${eN}_after_repeat" && (String)settings."trig_${eN}_after_repeat_txt" ? (String)settings."trig_${eN}_after_repeat_txt" : sNULL
        List<String> eTxtItems = dct ? dct.tokenize(";") : []
        List<String> rTxtItems = dcart ? dcart.tokenize(";") : []
        List<String> testItems = eTxtItems + rTxtItems
        if(testMode && testItems.size()) {
            return  decodeVariables(evt, (String)getRandomItem(testItems))
        } else if(!testMode && isRepeat && rTxtItems?.size()) {
            return  decodeVariables(evt, (String)getRandomItem(rTxtItems))
        } else if(!testMode && eTxtItems?.size()) {
            return  decodeVariables(evt, (String)getRandomItem(eTxtItems))
        } else {
            switch(eN) {
                case sMODE:
                    return  "The location mode is now set to ${eV}"
                case sPISTNEXEC:
                    return  "The ${evt?.displayName} piston was just executed!."
                case "scene":
                    return  "The ${eV} scene was just executed!."
                case "hsmStatus":
                case sALRMSYSST:
                    return "The ${getAlarmSystemName()} is now set to ${eV}"
                case "guard":
                    return "Alexa Guard is now set to ${eV}"
                case "hsmAlert":
                    return "A ${getAlarmSystemName()} ${evt?.displayName} alert with ${eV} has occurred."
                case "sunriseTime":
                case "sunsetTime":
                    return "The ${getAlarmSystemName()} is now set to ${eV}"
                case "scheduled":
                    return "Your scheduled event has occurred at ${eV}"
                case sPUSHED:
                case sHELD:
                case sRELEASED:
                case sDBLTAP:
                    return "Button ${eV} was ${eN == sDBLTAP ? "double tapped" : eN} on ${evt?.displayName}"

                case sTHERMTEMP:
                    eN = sTEMP
                /*
                case sCOOLSP:
                case sHEATSP:
                case "thermostatMode":
                case "thermostatFanMode":
                case "thermostatOperatingState":
                case "smoke":
                case "carbonMonoxide":
                    return "${eN} is ${eV} on ${evt?.displayName}!"
                */
                default:
                    String t0 = getAttrPostfix(eN)
                    String postfix = t0 ?: sBLANK
                    if(evtAd && devs?.size()>1) {
                        return "All ${devs.size()}${!evt?.displayName?.toLowerCase()?.contains(eN) ? " ${eN}" : sBLANK} devices are ${eV} ${postfix}"
                    } else {
                        return "${evt?.displayName}${!evt?.displayName?.toLowerCase()?.contains(eN) ? " ${eN}" : sBLANK} is ${eV} ${postfix}"
                    }
                    break
            }
        }
    }
    return "Invalid Text Received... Please verify Action configuration..."
}

public getActionHistory(Boolean asObj=false) {
    List<Map> eHist = (List<Map>)getMemStoreItem("actionHistory")
    List<String> output = []
    if(eHist.size()) {
        eHist.each { Map h->
            List hList = []
            hList.push([name: "Trigger:", val: h?.evtName])
            hList.push([name: "Device:", val: h?.evtDevice])
            hList.push([name: "Condition Status:", val: (h?.active ? "Passed" : "Failed")])
            hList.push([name: "Conditions Passed:", val: h?.passed])
            hList.push([name: "Conditions Blocks:", val: h?.blocks])
            if(h?.test) hList.push([name: "Test Mode:", val: true])
            if(h?.isRepeat) hList.push([name: "Repeat:", val: true])
            hList.push([name: "Source:", val: h?.src])
            hList.push([name: "DateTime:", val: h?.dt])
            if(hList.size()) {
                output.push(spanSm(kvListToHtmlTable(hList, sCLR4D9), sCLRGRY))
            }
        }
    } else { output.push("No History Items Found...") }
    if(!asObj) {
        output.each { String i-> paragraph spanSm(i) }
    } else {
        return output
    }
}

private static String kvListToHtmlTable(List tabList, String color=sCLRGRY) {
    String str = sBLANK
    if(tabList?.size()) {
        str += "<table style='border: 1px solid ${color};border-collapse: collapse;'>"
        tabList.each { it->
            str += "<tr style='border: 1px solid ${color};'><td style='border: 1px solid ${color};padding: 0px 3px 0px 3px;'>${spanSmBld((String)it.name)}</td><td style='border: 1px solid ${color};padding: 0px 3px 0px 3px;'>${spanSmBr("${it.val}")}</td></tr>"
        }
        str += "</table>"
    }
    return str
}

private addToActHistory(evt, data, Integer max=10) {
    getTheLock(sHMLF, "addToActHistory")

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
    if(lsiz > max) { eData = eData.drop( (lsiz-max) ) }
    // log.debug "actionHistory Size: ${eData?.size()}"
    updMemStoreItem("actionHistory", eData)

    releaseTheLock(sHMLF)
}

void clearActHistory(){
    getTheLock(sHMLF, "clearActHistory")

    updMemStoreItem("actionHistory", [])

    releaseTheLock(sHMLF)

}

private void executeAction(evt = null, Boolean testMode=false, String src=sNULL, Boolean allDevsResp=false, Boolean isRptAct=false, Map tierData=null) {
    Long startTime = now()
    if((Boolean)settings.actTestRun) testMode = true
    logTrace("executeAction ${src ? '('+src+')' : sBLANK}${testMode ? " | [TestMode]" : sBLANK}${allDevsResp ? " | [AllDevsResp]" : sBLANK}${isRptAct ? " | [RepeatEvt]" : sBLANK}")
    if(isPaused(true)) { logWarn("Action is PAUSED... Skipping Action Execution...", true); return }
    Map condStatus = conditionStatus()
    // log.debug "condStatus: ${condStatus}"
    addToActHistory(evt, [status: condStatus, test: testMode, src: src, isRepeat: isRptAct, isTier: (tierData != null)] )
    Map actMap = (Map)state.actionExecMap

    Integer actDevSiz = settings.act_EchoDevices ? settings.act_EchoDevices.size() : 0
//    List actDevices  // = settings.act_EchoDevices ? parent?.getDevicesFromList(settings.act_EchoDevices) : []
//    Integer actDevSiz = actDevices.size()

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
//        def alexaMsgDev = actDevSiz && (Boolean)settings.notif_alexa_mobile ? actDevices[0] : null
        if(!(Boolean)condStatus.ok) { logWarn("executeAction | Skipping execution because ${condStatus.blocks} conditions have not been met", true); return }
        if(!actMap || !actMap?.size()) { logError("executeAction Error | The ActionExecutionMap is not found or is empty", true); return }
        if(settings.act_EchoZones && actZonesSiz == 0 && actDevSiz == 0) { logWarn("executeAction | No Active Zones Available and No Alternate Echo Devices Selected.", true); return }
        if(actDevSiz == 0 && !settings.act_EchoZones) { logError("executeAction Error | Echo Device List not found or is empty", true); return }
        if(!actMap.actionType) { logError("executeAction Error | The ActionType is missing or is empty", true); return }
        Map actConf = (Map)actMap.config
        Integer actDelay = (Integer)actMap.delay ?: 0
        Integer actDelayMs = actDelay*1000
        Integer changeVol = actConf?.volume?.change != null ? (Integer)actConf?.volume?.change : null
        Integer restoreVol = actConf?.volume?.restore != null ? (Integer)actConf?.volume?.restore : null
        //Integer alarmVol = actConf?.volume?.alarm ?: null
        Map zoneVolumeMap = (Map)actConf?.zoneVolume ?: null
        switch(actType) {
            case sSPEAK:
            case sSPEAKP:
            case sSPEAKT:
            case "speak_parallel_tiered":
            case sANN:
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

                    String mCmd = actType.replaceAll("_tiered", sBLANK)
                    if(actZonesSiz) {
                        parent?.sendZoneCmd([zones: activeZones.collect { (String)it?.key }, cmd: mCmd, title: getActionName(), message: txt, changeVol: changeVol, restoreVol: restoreVol, zoneVolumes: zoneVolumeMap, delay: actDelayMs])
                        logDebug("Sending ${mCmd} Command: (${txt}) to Zones via Parent (${activeZones.collect { it?.value?.name }.join(',')})${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")

                    } else {
                        if(actDevSiz) {
                            List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                            List devObjs = []

                            String cmd = mCmd in [sSPEAK, sSPEAKP] ? sTTS : "announce"
                            actDevices?.each {
                                Map devInfo = it?.getEchoDevInfo(cmd)
                                if(devInfo) {
                                    Boolean dnd = false //chkDnd ? getDndEnabled((String)devInfo.deviceSerialNumber) : false
                                    //if(!dnd) devObjs?.push([deviceTypeId: devInfo.deviceTypeId, deviceSerialNumber: devInfo.deviceSerialNumber, dni: devInfo.dni])
                                    if(!dnd) devObjs?.push(devInfo)
                                }
                            }
                            parent?.sendDevObjCmd(devObjs, mCmd, getActionName(), txt, changeVol, restoreVol)
                            logDebug("Sending ${mCmd} Command: (${txt}) to devices via Parent (${devObjs})${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                        }
                    }
                }
                break
            case "voicecmd":
            case "sequence":
                String mCmd= actConf[actType] ? (String)actConf[actType].cmd : sNULL
                String mText= actConf[actType] ? (String)actConf[actType].text : sNULL
                if(mText != sNULL) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { (String)it?.key }, cmd: actType, message: mText, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${mText}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    } else if(actDevSiz) {
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                        actDevices.each { dev->
                            dev?."${mCmd}"(mText)
                        }
                        logDebug("Sending ${mAct} Command: (${mText}) to devices (${actDevices})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    }
                }
                break
            case "playback":
            case "dnd":
                String mCmd= actConf[actType] ? (String)actConf[actType].cmd : sNULL
                if(mCmd != sNULL) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[zones: activeZones.collect { (String)it?.key }, cmd: mCmd, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${mCmd}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                    } else if(actDevSiz) {
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
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
            case sWEATH:
                if(actConf[actType] && actConf[actType]?.cmd) {
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { (String)it?.key }, cmd: actConf[actType]?.cmd, message: actConf[actType]?.text, changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: false, displayed: false)
                        logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType]?.cmd}) to Zones (${activeZones.collect { it?.value?.name }})${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    } else if(actDevSiz) {
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
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
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
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
                    List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                    actDevices?.each { dev->
                        dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time)
                        // dev?."${actConf[actType]?.cmd}"(actConf[actType]?.label, actConf[actType]?.date, actConf[actType]?.time, actConf[actType]?.recur?.type, actConf[actType]?.recur?.opt)
                    }
                    logDebug("Sending ${actType?.toString()?.capitalize()} Command: (${actConf[actType]?.cmd}) to ${actDevices} | Label: ${actConf[actType]?.label} | Date: ${actConf[actType]?.date} | Time: ${actConf[actType]?.time}")
                }
                break

            case "music":
                if(actConf[actType] && actConf[actType]?.cmd && actConf[actType]?.provider && actConf[actType]?.search) {
                    logDebug("musicProvider: ${actConf[actType]?.provider} | ${convMusicProvider((String)actConf[actType]?.provider)}")
                    if(actZonesSiz) {
                        sendLocationEvent(name: "es3ZoneCmd", value: actType, data:[ zones: activeZones.collect { it?.key as String }, cmd: actType, search: actConf[actType]?.search, provider: convMusicProvider((String)actConf[actType]?.provider), changeVol: changeVol, restoreVol: restoreVol, delay: actDelayMs], isStateChange: true, display: true, displayed: true)
                        logDebug("Sending ${actType.capitalize()} Command: (${txt}) to Zones (${activeZones.collect { it?.value?.name }} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd})${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    } else if(actDevSiz) {
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                        actDevices.each { dev->
                            dev?."${actConf[actType]?.cmd}"(actConf[actType]?.search, convMusicProvider((String)actConf[actType]?.provider), changeVol, restoreVol)
                        }
                        logDebug("Sending ${actType.capitalize()} | Provider: ${actConf[actType]?.provider} | Search: ${actConf[actType]?.search} | Command: (${actConf[actType]?.cmd}) to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}${changeVol!=null ? " | Volume: ${changeVol}" : sBLANK}${restoreVol!=null ? " | Restore Volume: ${restoreVol}" : sBLANK}")
                    }
                }
                break

            case "alexaroutine":
                if(actConf[actType] && actConf[actType].cmd && actConf[actType].routineId) {
                    List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                    actDevices[0]?."${actConf[actType].cmd}"((String)actConf[actType].routineId)
                    logDebug("Sending ${actType.capitalize()} Command: (${actConf[actType].cmd}) | RoutineId: ${actConf[actType].routineId} to ${actDevices}${actDelay ? " | Delay: (${actDelay})" : sBLANK}")
                }
                break

            case "wakeword":
                if(actConf[actType] && actConf[actType]?.devices && actConf[actType]?.devices?.size()) {
                    actConf[actType]?.devices?.each { d->
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
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
                        List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
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
                def alexaMsgDev = null
                if(actDevSiz && (Boolean)settings.notif_alexa_mobile){
                    List actDevices = parent?.getDevicesFromList(settings.act_EchoDevices)
                    alexaMsgDev = actDevices[0]
                }
                if(sendNotifMsg(getActionName(), actMsgTxt, alexaMsgDev, false)) { logTrace("Sent Action Notification...") }
            }
        }
        if(tierData?.size() && (Integer)settings.act_tier_cnt > 1) {
            log.debug "firstTierMsg: ${firstTierMsg} | lastTierMsg: ${lastTierMsg}"
            if(firstTierMsg) {
                Integer del = (Integer)settings.act_tier_start_delay
                if(del) {
                    logTrace("scheduled executeTaskCommands in $del seconds - start delay")
                    runIn(del, "executeTaskCommands", [data:[type: "act_tier_start_"]])
                } else { executeTaskCommands([type:"act_tier_start_"]) }
            }
            if(lastTierMsg) {
                Integer del = (Integer)settings.act_tier_stop_delay
                if(del) {
                    logTrace("scheduled executeTaskCommands in $del seconds - stop delay")
                    runIn(del, "executeTaskCommands", [data:[type: "act_tier_stop_"]])
                } else { executeTaskCommands([type:"act_tier_stop_"]) }
            }
        } else {
            Integer del = (Integer)settings.act_tasks_delay
            if(del) {
                logTrace("scheduled executeTaskCommands in $del seconds - action tasks delay")
                runIn(del, "executeTaskCommands", [data:[type: "act_"]])
            } else { executeTaskCommands([type: "act_"]) }
        }
    }
    logTrace("ExecuteAction Finished | ProcessTime: (${now()-startTime}ms)")
}

@SuppressWarnings('unused')
private postTaskCommands(data) {
    String p = data?.type ?: sNULL
    if(p && settings."${p}sirens" && (String)settings."${p}siren_cmd") { settings."${p}sirens"*.off() }
}


// parent calls for editing

public Map getInputData(String inName) {
    String desc = "<li>Add custom responses to use when this action is executed.</li>"
    String title
    String tmplt = '%name% %type% is now %value% %unit%'
    String vDesc = "<li>Example: %name% %type% is now %value% would translate into The garage door contact is now open</li><li>To make beep sounds use: <b>wop, wop, wop</b> (equals 3 beeps)</li>"
    switch(inName) {
        case "act_speak_txt":
            title = "Global Action Speech Event"
            break
        case "act_announcement_txt":
            title = "Global Announcement Speech Event"
            break
        default:
            if(inName?.startsWith("trig_")) {
                List<String> i = inName.tokenize("_")
                title = "(${i[1]?.capitalize()}) Trigger "
                desc = "<li>Add custom responses for ${i[1]?.capitalize()} "
                if(i.contains("repeat")) {
                    title += "Repeat "
                    desc += "events which have to be repeated"
                } else {
                    desc += "trigger events"
                }
                title += "Events"
                desc += ".</li>${vDesc}"
            }
            else if(inName?.startsWith("act_tier_item_") && inName?.endsWith("_txt")) {
                List<String> i = inName.tokenize("_")
                title = "Tier Response (${i[3]})"
                desc = "<li>Add custom responses to use when this action is executed.</li>"
                tmplt = "Custom tier ${i[3]} message here."
            } else {
                desc = sNULL
                title = sNULL
                tmplt = sNULL
            }
            break
    }
    Map o = [
        val: settings."${inName}" ? settings."${inName}"?.toString()?.replaceAll("\'", sBLANK) : null,
        desc: """<ul class="pl-3" style="list-style-type: bullet;">${desc}</ul>""",
        title: title,
        template: tmplt,
        version: appVersionFLD
    ]
    if(devModeFLD) log.debug "getInputData inName: $inName result: $o"
    return o
}

public Boolean updateTxtEntry(obj) {
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
    if(!data) data = (Map)state.tsDtMap ?: [:]
    if(key) { data[key] = dt ?: getDtNow() }
    tsDtMapFLD[appId]=data
    tsDtMapFLD=tsDtMapFLD

    state.tsDtMap = data
}

private void remTsVal(key) {
    String appId=app.getId()
    Map data=tsDtMapFLD[appId] ?: [:]
    if(!data) data = (Map)state.tsDtMap ?: [:]
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
    if(!tsMap) tsMap = (Map)state.tsDtMap ?: [:]
    if(key && tsMap && tsMap[key]) { return (String)tsMap[key] }
    return sNULL
}

Integer getLastTsValSecs(String val, Integer nullVal=1000000) {
    return (val && getTsVal(val)) ? GetTimeDiffSeconds(getTsVal(val)).toInteger() : nullVal
}

private void updAppFlag(String key, val) {
    Map t0 = atomicState.appFlagsMap
    Map data = t0 ?: [:]
    if(key) { data[key] = val }
    atomicState.appFlagsMap = data
}

@SuppressWarnings('unused')
private void remAppFlag(key) {
    Map t0 = atomicState.appFlagsMap
    Map data = t0 ?: [:]
    if(key) {
        if(key instanceof List) {
            key?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
        } else { if(data.containsKey((String)key)) { data.remove((String)key) } }
        atomicState.appFlagsMap = data
    }
}

Boolean getAppFlag(String val) {
    Map aMap = atomicState.appFlagsMap
    if(val && aMap && aMap[val]) { return aMap[val] }
    return false
}

private void stateMapMigration() {
    //Timestamp State Migrations
    Map<String,String> tsItems = ["lastAfterEvtCheck":"lastAfterEvtCheck", "lastNotifMsgDt":"lastNotifMsgDt"]
    tsItems.each { String k, String v-> if(state.containsKey(k)) { updTsVal(v, (String)state[k]); state.remove(k) } }

    //App Flag Migrations
    Map<String,String> flagItems = [:]
    flagItems.each { String k, String v-> if(state.containsKey(k)) { updAppFlag(v, state[k]); state.remove(k) } }
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

static List<String> weekDaysEnum() { return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"] }
static Map<String, String> weekDaysAbrvEnum() { return ["MO":"Monday", "TU":"Tuesday", "WE":"Wednesday", "TH":"Thursday", "FR":"Friday", "SA":"Saturday", "SU":"Sunday"] }
static List<String> monthEnum() { return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"] }
static Map<String, String> daysOfWeekMap() { return ["MON":"Monday", "TUE":"Tuesday", "WED":"Wednesday", "THU":"Thursday", "FRI":"Friday", "SAT":"Saturday", "SUN":"Sunday"] }
static Map<String, String> weeksOfMonthMap() { return ["1":"1st Week", "2":"2nd Week", "3":"3rd Week", "4":"4th Week", "5":"5th Week"] }
static Map<String, String> monthMap() { return ["1":"January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"] }

static Map<String, String> getAlarmTrigOpts() {
    return ["armedAway":"Armed Away", "armingAway":"Arming Away Pending exit delay","armedHome":"Armed Home","armingHome":"Arming Home pending exit delay", "armedNight":"Armed Night", "armingNight":"Arming Night pending exit delay","disarmed":"Disarmed", "allDisarmed":"All Disarmed","alerts":"Alerts"]
}

private static Map<String, String> getAlarmSystemAlertOptions(){
    return [
        intrusion        : "Intrusion Away",
        "intrusion-home" : "Intrusion Home",
        "intrusion-night": "Intrusion Night",
        smoke            : "Smoke",
        water            : "Water",
        rule             : "Rule",
        cancel           : "Alerts cancelled",
        arming           : "Arming failure"
    ]
}

private static Map<String, String> getAlarmSystemStatusActions(){
    return [
        armAway     : "Arm Away",
        armHome     : "Arm Home",
        armNight    : "Arm Night",
        disarm      : "Disarm",
        armRules    : "Arm Monitor Rules",
        disarmRules : "Disarm Monitor Rules",
        disarmAll   : "Disarm All",
        armAll      : "Arm All",
        cancelAlerts: "Cancel Alerts"
    ]
}

public Map getActionMetrics() {
    Map out = [:]
    out.version = appVersionFLD
    out.type = (String)settings.actionType ?: sNULL
    out.delay = settings.actDelay ?: 0
    out.triggers = (List)settings.triggerEvents ?: []
    out.echoZones = (List)settings.act_EchoZones ?: []
    out.echoDevices = (List)settings.act_EchoDevices ?: []
    out.usingCustomText = (customTxtItems()?.size())
    Integer tDevCnt = 0
    ((List<String>)settings.triggerEvents)?.each { if(settings."trig_${it}" && settings."trig_${it}"?.size()) { tDevCnt = tDevCnt+settings."trig_${it}"?.size() } }
    out.triggerDeviceCnt = tDevCnt
    return out
}


Integer getLastNotifMsgSec() { return getLastTsValSec("lastNotifMsgDt") }
//Integer getLastChildInitRefreshSec() { return getLastTsValSec("lastChildInitRefreshDt", 3600) }
//Integer getLastNotifMsgSec() { return !state.lastNotifMsgDt ? 100000 : GetTimeDiffSeconds(state.lastNotifMsgDt, "getLastMsgSec").toInteger() }

Boolean getOk2Notify() {
    Boolean smsOk // = (settings.notif_sms_numbers?.toString()?.length()>=10)
    Boolean pushOk // = (settings.notif_send_push)
    Boolean pushOver // = (settings.notif_pushover && settings.notif_pushover_devices)
    Boolean alexaMsg = ((Boolean)settings.notif_alexa_mobile)
    Boolean notifDevsOk = (((List)settings.notif_devs)?.size())
    Boolean daysOk = (List)settings.notif_days ? (isDayOfWeek((List)settings.notif_days)) : true
    Boolean timeOk = notifTimeOk()
    Boolean modesOk = (List)settings.notif_modes ? (isInMode((List)settings.notif_modes)) : true
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
Boolean sendNotifMsg(String msgTitle, String msg, alexaDev=null, Boolean showEvt=true) {
    logTrace("sendNotifMsg() | msgTitle: ${msgTitle}, msg: ${msg}, $alexaDev, showEvt: ${showEvt}")
    List sentSrc = [] // ["Push"]
    Boolean sent = false
    try {
        String newMsg = msgTitle+': '+msg
        String flatMsg = newMsg.replaceAll("\n", sSPACE)
        if(!getOk2Notify()) {
            logInfo( "sendNotifMsg: Notification not configured or Message Skipped During Quiet Time ($flatMsg)")
            // if(showEvt) { sendNotificationEvent(newMsg) }
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

static Integer versionStr2Int(String str) { return str ? str.toString()?.replaceAll("\\.", sBLANK)?.toInteger() : null }
Boolean minVersionFailed() {
    try {
        Integer t0 = (Integer)parent?.minVersions()["actionApp"]
        Integer minDevVer = t0 ?: null
        return minDevVer != null && versionStr2Int(appVersionFLD) < minDevVer
    } catch (ignored) {
        return false
    }
}


/************************************************************************************************************
        webCoRE Integration
************************************************************************************************************/
private static String webCoRE_handle(){return'webCoRE'}

private void webCoRE_init(pistonExecutedCbk=null){
    subscribe(location,webCoRE_handle(),webCoRE_handler)
//    if(pistonExecutedCbk)subscribe(location,"${webCoRE_handle()}.pistonExecuted",webCoRE_handler);
    if(!webCoREFLD) {
        webCoREFLD = [:] + [cbk:true] // pistonExecutedCbk]
    }
    webCoRE_poll(true)
}

@Field static final String sLASTWU = 'lastwebCoREUpdDt'
@Field volatile static Map<String,Object> webCoREFLD = [:]

private void webCoRE_poll(Boolean anow=false){
    Long rUpd = (Long)webCoREFLD.updated
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

private void webCoRE_execute(String pistonIdOrName,Map data=[:]) {
    String i = pistonIdOrName
    if(i){sendLocationEvent([name:i,value:app.label,isStateChange:true,displayed:false,data:data])}
}

private static List webCoRE_list(){
    List ret = ((List)webCoREFLD?.pistons)?.sort {it?.name}?.collect { [(it?.id): it?.aname?.replaceAll("<[^>]*>", sBLANK)] }
    return ret
}
/*
private static String getPistonByName(String pistonIdOrName) {
    String i=((List)webCoREFLD?.pistons ?: []).find{((String)it.name==pistonIdOrName)||((String)it.id==pistonIdOrName)}?.id
} */

private static String getPistonById(String rId) {
    Map a = webCoRE_list()?.find { it.containsKey(rId) }
    String aaa = (String)a?."${rId}"
    return aaa ?: "Refresh to display piston name..."
}

void webCoRE_handler(evt){
    logTrace("${evt?.name?.toUpperCase()} Event | Value: (${strCapitalize(evt?.value)}) with a delay of ${now() - evt?.date?.getTime()}ms")
    switch((String)evt.value){
      case 'pistonList':
        getTheLock(sHMLF, "webCoRE_Handler")
        Long rUpd = (Long)webCoREFLD.updated
        if(rUpd && (now() < (rUpd+1000L))) {
            releaseTheLock(sHMLF)
            break
        }
        List p=(List)webCoREFLD?.pistons ?: []
        Map d=evt.jsonData?:[:]
        if(d.id && d.pistons && (d.pistons instanceof List)){
            p.removeAll{it.iid==d.id}
            p+=d.pistons.collect{[iid:d.id]+it}.sort{it.name}
            Boolean a = (Boolean)webCoREFLD?.cbk

            webCoREFLD = [cbk: a, updated: now(), pistons: p]
            releaseTheLock(sHMLF)

            updTsVal(sLASTWU)
        } else releaseTheLock(sHMLF)
        break
      case sPISTNEXEC:
        if(valTrigEvt(sPISTNEXEC) && settings.trig_pistonExecuted) {
            webcoreEvtHandler(evt)
        }
        break
    }
}

static String webCore_icon(){ return "https://raw.githubusercontent.com/ady624/webCoRE/master/resources/icons/app-CoRE.png" }

/******************************************
|   Restriction validators
*******************************************/

Double getDevValueAvg(List devs, String attr) {
    List<Double> vals = devs?.findAll {
        Double t = it?.currentValue(attr)?.toDouble()
        (t != null /* && t != 0 */ ) }?.collect { t }
    return vals?.size() ? (vals.sum()/vals.size()).round(1).toDouble() : null
}

String getCurrentMode() {
    return location?.mode
}

List getLocationModes(Boolean sorted=false) {
    List modes = location?.modes*.name
    // log.debug "modes: ${modes}"
    return (sorted) ? modes?.sort() : modes
}

static List<String> getLocationPistons() {
    List aa = ((List)webCoREFLD?.pistons ?: []).findAll { it.id }.collect { (String)it.id }
    return aa ?: []
}
/*
private getModeById(mId) {
    return location?.getModes()?.find{it?.id == mId}
} */

Boolean isInMode(List modes, Boolean not=false) {
    Boolean fnd = false
    if(modes) {
        fnd = (getCurrentMode() in modes)
        fnd = (not ? !fnd : fnd)
    }
    return fnd
}

Boolean isInAlarmMode(List modes) {
    String a = location?.hsmStatus ?: "disarmed"
    //return (modes) ? (parent?.getAlarmSystemStatus() in modes) : false
    return (modes) ? (a in modes) : false
}
/*
Boolean areAllDevsSame(List devs, String attr, val) {
    if(devs && attr) { return (devs?.findAll { it?.currentValue(attr) == val as String }?.size() == devs?.size()) }
    return false
} */

Boolean allDevAttValsEqual(List devs, String att, val) {
    if(devs && att) {
        if(val instanceof List) return (devs.findAll { it?.currentValue(att) in val }?.size() == devs?.size())
        else return (devs.findAll { it?.currentValue(att) == val }?.size() == devs?.size())
    }
    return false
}

Boolean anyDevAttValsEqual(List devs, String att, val) {
    if(devs && att) {
        if(val instanceof List) return (devs.findAll { it?.currentValue(att) in (List)val }?.size() >= 1)
        else return (devs.findAll { it?.currentValue(att) == val }?.size() >= 1)
    }
    return false
}

Boolean anyDevAttNumValAbove(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() > val }?.size() >= 1) : false
}
Boolean anyDevAttNumValBelow(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() < val }?.size() >= 1) : false
}
Boolean anyDevAttNumValBetween(List devs, String att, Double low, Double high, Boolean not) {
    return (devs && att && (low < high)) ? (devs?.findAll {
        Double t = it?.currentValue(att)?.toDouble()
        if(not) ( (t < low) || (t > high) )
        else ( (t >= low) && (t <= high) ) }?.size() >= 1) : false
}
Boolean anyDevAttNumValEqual(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() == val }?.size() >= 1) : false
}

Boolean allDevAttNumValsAbove(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() > val }?.size() == devs?.size()) : false
}
Boolean allDevAttNumValsBelow(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() < val }?.size() == devs?.size()) : false
}
Boolean allDevAttNumValsBetween(List devs, String att, Double low, Double high, Boolean not) {
    return (devs && att && (low < high)) ? (devs?.findAll {
        Double t = it?.currentValue(att)?.toDouble()
        if(not) ( (t < low) || (t > high) )
        else ( (t >= low) && (t <= high) ) }?.size() == devs?.size()) : false
}
Boolean allDevAttNumValsEqual(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() == val }?.size() == devs?.size()) : false
}

Boolean devAttValEqual(List devs, String devId, String att, val) {
    if(devs && att) {
        def dev = devs.find { it?.deviceId?.toString() == devId }
        if(dev) return (dev?.currentValue(att) == val)
    }
    return false
}

static String getAlarmSystemName(Boolean abbr=false) {
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

//static Boolean isDateToday(Date dt) { return (dt && dt.clearTime().compareTo(new Date().clearTime()) >= 0) }
static Boolean isDateToday(Date dt) { return (dt && dt.clearTime() >= new Date().clearTime()) }

static String strCapitalize(str) { return str ? str.toString().capitalize() : sNULL }

static String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }

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

Map getDateMap() {
    Map m = [:]
    Date d = new Date()
    m.dayOfYear =    dateTimeFmt(d, "DD")
    m.dayNameShort = dateTimeFmt(d, "EEE").toUpperCase()
    m.dayName =      dateTimeFmt(d, "EEEE")
    m.day =          dateTimeFmt(d, "d")
    m.week =         dateTimeFmt(d, "W")
    m.weekOfYear =   dateTimeFmt(d, "w")
    m.monthName =    dateTimeFmt(d, "MMMMM")
    m.month =        dateTimeFmt(d, "MM")
    m.year =         dateTimeFmt(d, "yyyy")
    m.hour =         dateTimeFmt(d, "hh")
    m.minute =       dateTimeFmt(d, "mm")
    m.second =       dateTimeFmt(d, "ss")
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

/*
String unitStr(type) {
    switch(type) {
        case "temp":
            return "\u00b0${getTemperatureScale() ?: "F"}"
        case sHUMID:
            return "%"
        default:
            return sBLANK
    }
} */

String getAppNotifDesc(Boolean hide=false) {
    String str = sBLANK
    if(isActNotifConfigured()) {
        Boolean ok = getOk2Notify()
        str += hide ? sBLANK : spanSmBr(strUnder("Send allowed: ") + getOkOrNotSymHTML(ok))
        str += ((List)settings.notif_devs) ? spanSmBr("  ${sBULLET} Notification Device${pluralizeStr((List)settings.notif_devs)} (${((List)settings.notif_devs).size()})") : sBLANK
        str += (Boolean)settings.notif_alexa_mobile ? spanSmBr("  ${sBULLET} Alexa Mobile App") : sBLANK
        String res = getNotifSchedDesc(true)
        str += res ? spanSmBr(res) : sBLANK
    }
    return str != sBLANK ? str : sNULL
}

List getQuietDays() {
    List<String> allDays = weekDaysEnum()
    List curDays = (List)settings.notif_days ?: []
    return allDays.findAll { (!curDays?.contains(it)) }
}

String getNotifSchedDesc(Boolean min=false) {
    String startType = settings.notif_time_start_type
    Date startTime
    String stopType = settings.notif_time_stop_type
    Date stopTime
    List dayInput = (List)settings.notif_days
    List modeInput = (List)settings.notif_modes
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
    Boolean daysOk = dayInput ? isDayOfWeek(dayInput) : true
    Boolean modesOk = modeInput ? isInMode(modeInput) : true
    Boolean rest = !(daysOk && modesOk && timeOk)
    String startLbl = startTime ? epochToTime(startTime) : sBLANK
    String stopLbl = stopTime ? epochToTime(stopTime) : sBLANK
    str += (startLbl && stopLbl) ? spanSmBr("     ${sBULLET} Restricted Times: ${startLbl} - ${stopLbl} " + getOkOrNotSymHTML(timeOk)) : sBLANK
    List qDays = getQuietDays()
    str += dayInput && qDays ? spanSmBr("     ${sBULLET} Restricted Day${pluralizeStr(qDays, false)}: (${qDays?.join(", ")}) " + getOkOrNotSymHTML(!daysOk)) : sBLANK
    str += modeInput ? spanSm("     ${sBULLET} Allowed Mode${pluralizeStr(modeInput, false)}: (${modeInput?.join(", ")}) " + getOkOrNotSymHTML(!modesOk)) : sBLANK
    str = str ? spanSmBr("  ${sBULLET} Restrictions Active: " + getOkOrNotSymHTML(rest)) + spanSm(str) : sBLANK
    return (str != sBLANK) ? str : sNULL
}

String getTriggersDesc(Boolean hideDesc=false, Boolean addFoot=true) {
    Boolean confd = triggersConfigured()
    List<String> setItem = (List<String>)settings.triggerEvents
    String sPre = "trig_"
    if(confd && setItem?.size()) {
        if(!hideDesc) {
            String str = spanSmBldBr("Triggers${!addFoot ? " for ("+(String)buildActTypeEnum()."${(String)settings.actionType}" + ")" : sBLANK}:", sNULL)
            setItem.each { String evt->
                String adder = sBLANK
                List myL = (List)settings."${sPre}${evt}"
                switch(evt) {
                    case "scheduled":
                        String schedTyp = settings."${sPre}${evt}_type" ?: sNULL
                        str += spanSmBr(" ${sBULLET} ${strUnder(evt?.capitalize())}${schedTyp ? " (${schedTyp})" : ""}")
                        if(schedTyp == "Recurring") {
                            str += settings."${sPre}${evt}_recurrence"  ? spanSmBr("    ${sBULLETINV} Recurrence: (${settings."${sPre}${evt}_recurrence"})")            : sBLANK
                            str += settings."${sPre}${evt}_time"        ? spanSmBr("    ${sBULLETINV} Time: (${fmtTime(settings."${sPre}${evt}_time")})")               : sBLANK
                            str += settings."${sPre}${evt}_weekdays"    ? spanSmBr("    ${sBULLETINV} Week Days: (${settings."${sPre}${evt}_weekdays"?.join(",")})")    : sBLANK
                            str += settings."${sPre}${evt}_daynums"     ? spanSmBr("    ${sBULLETINV} Days of Month: (${settings."${sPre}${evt}_daynums"?.size()})")    : sBLANK
                            str += settings."${sPre}${evt}_weeks"       ? spanSmBr("    ${sBULLETINV} Weeks of Month: (${settings."${sPre}${evt}_weeks"?.join(",")})")  : sBLANK
                            str += settings."${sPre}${evt}_months"      ? spanSmBr("    ${sBULLETINV} Months: (${settings."${sPre}${evt}_months"?.join(",")})")         : sBLANK
                        }
                        if(schedTyp == "One-Time") {
                            str += settings."${sPre}${evt}_time"        ? spanSmBr("    ${sBULLETINV} Time: (${fmtTime(settings."${sPre}${evt}_time")}))")              : sBLANK
                        }
                        if(schedTyp in [sCSUNRISE, sCSUNSET]) {
                            str += settings."${sPre}${evt}_sunState_offset"     ? spanSmBr("    ${sBULLETINV} Offset: (${settings."${sPre}${evt}_sunState_offset"})")      : sBLANK
                        }
                        break
                    case sALRMSYSST:
                        str += spanSmBr(" ${sBULLET} ${strUnder(evt?.capitalize())} (${getAlarmSystemName(true)})" + myL ? " (${myL?.size()} Selected)" : sBLANK)
                        if ("alerts" in myL) str += (List)settings."${sPre}${evt}_events"  ? spanSmBr("    ${sBULLETINV} Alert Events: (${(List)settings."${sPre}${evt}_events"})") : sBLANK
                        str += (Boolean)settings."${sPre}${evt}_once" ? spanSmBr("    ${sBULLETINV} Once a Day: (${(Boolean)settings."${sPre}${evt}_once"})") : sBLANK
                        break
                    case sPISTNEXEC:
                    case sMODE:
//                    case "scene":
                        String typ = evt == sMODE ? "Mode" : "Piston"
                        str += myL    ? spanSmBr(" ${sBULLET} "+ strUnder(typ) + pluralizeStr(myL) + " (${myL?.size()})") : sBLANK
                        str += (Boolean)settings."${sPre}${evt}_once" ? spanSmBr("    ${sBULLETINV} Once a Day: (${(Boolean)settings."${sPre}${evt}_once"})") : sBLANK
                        break
                    case sPUSHED:
                    case sRELEASED:
                    case sHELD:
                    case sDBLTAP:
                        adder = "Button "
                    default:
                        str += spanSmBr(" ${sBULLET} ${adder}${strUnder(evt?.capitalize())}${myL ? " (${myL?.size()} Device"+pluralizeStr(myL)+')' : sBLANK}")
                        String t_cmd = (String)settings."${sPre}${evt}_cmd"
                        if(t_cmd in numOpts()) {
                            if (t_cmd in [sBETWEEN, sNBETWEEN]) {
                                str += spanSmBr("    ${sPLUS} Trigger Value ${t_cmd.capitalize()}: (${settings."${sPre}${evt}_low"} - ${settings."${sPre}${evt}_high"})")
                            } else {
                                str += (t_cmd == sABOVE && settings."${sPre}${evt}_high"!=null)    ? spanSmBr("    ${sPLUS} Trigger Value Above: (${settings."${sPre}${evt}_high"})")   : sBLANK
                                str += (t_cmd == sBELOW && settings."${sPre}${evt}_low"!=null)     ? spanSmBr("    ${sPLUS} Trigger Value Below: (${settings."${sPre}${evt}_low"})")    : sBLANK
                                str += (t_cmd == sEQUALS && settings."${sPre}${evt}_equal"!=null)  ? spanSmBr("    ${sPLUS} Trigger Value Equals: (${settings."${sPre}${evt}_equal"})") : sBLANK
                            }
                        } else {
                            str += t_cmd  ? spanSmBr("    ${sPLUS} Trigger State: (${t_cmd})") : sBLANK
                        }
                        str += settings."${sPre}${evt}_nums"               ? spanSmBr("    ${sPLUS} Button Numbers: ${settings."${sPre}${evt}_nums"}") : sBLANK
                        str += (Integer)settings."${sPre}${evt}_after"!=null        ? spanSmBr("    ${sPLUS} Only After: (${settings."${sPre}${evt}_after"} sec)") : sBLANK
                        str += (Integer)settings."${sPre}${evt}_after_repeat"       ? spanSmBr("    ${sPLUS} Repeat Every: (${settings."${sPre}${evt}_after_repeat"} sec)") : sBLANK
                        str += (Integer)settings."${sPre}${evt}_after_repeat_cnt"   ? spanSmBr("    ${sPLUS} Repeat Count: (${settings."${sPre}${evt}_after_repeat_cnt"})") : sBLANK
                        str += (Boolean)settings."${sPre}${evt}_all" == true        ? spanSmBr("    ${sPLUS} Require All: (${settings."${sPre}${evt}_all"})") : sBLANK
                        str += (Boolean)settings."${sPre}${evt}_once"               ? spanSmBr("    ${sPLUS} Once a Day: (${(Boolean)settings."${sPre}${evt}_once"})") : sBLANK
                        str += (Integer)settings."${sPre}${evt}_wait"!=null         ? spanSmBr("    ${sPLUS} Wait (Sec): (${(Integer)settings."${sPre}${evt}_wait"})") : sBLANK
                        str += ((String)settings."${sPre}${evt}_txt" || (String)settings."${sPre}${evt}_after_repeat_txt") ? spanSmBr("    ${sPLUS} Custom Responses:") : sBLANK
                        str += (String)settings."${sPre}${evt}_txt"                 ? spanSmBr("       ${sPLUS} Events: (${((String)settings."${sPre}${evt}_txt")?.tokenize(";")?.size()} Items)") : sBLANK
                        str += (String)settings."${sPre}${evt}_after_repeat_txt"    ? spanSmBr("       ${sPLUS} Repeats: (${((String)settings."${sPre}${evt}_after_repeat_txt")?.tokenize(";")?.size()} Items)") : sBLANK
                        break
                }
            }
            str += addFoot ? inputFooter(sTTM) : sBLANK
            return str
        } else { return addFoot ? inputFooter(sTTM) : sBLANK }
    } else {
        return addFoot ? inputFooter("Tap to configure (Required!)", sCLRRED, true) : sBLANK
    }
}

String getOverallDesc() {
    Map condStatus = conditionStatus()
    return spanSmBld("Action Condition Status: ") + spanSmBr( ((Boolean)condStatus.ok ? "Active " : "Inactive ") + getOkOrNotSymHTML((Boolean)condStatus.ok))
}

String getConditionsDesc(Boolean addFoot=true) {
    Boolean confd = conditionsConfigured()
    String sPre = "cond_"
    String str = sBLANK
    if(confd) {
        str = getOverallDesc()
        str += spanSmBr(" ${sBULLET} " + reqAllCond() ? "All Conditions Required" : "Any Condition Allowed")
        if(timeCondConfigured()) {
            str += spanSmBr(" ${sBULLET} Time Between Allowed: " + getOkOrNotSymHTML(timeCondOk()))
            str += spanSmBr("    - ${getTimeCondDesc(false)}")
        }
        if(dateCondConfigured()) {
            str += spanSmBr(" ${sBULLET} Date:")
            str += (List)settings.cond_days    ? spanSmBr("    - Days Allowed: ${(List)settings.cond_days} " + getOkOrNotSymHTML(isDayOfWeek((List)settings.cond_days))) : sBLANK
            str += (List)settings.cond_months  ? spanSmBr("    - Months Allowed: ${(List)settings.cond_months} " + getOkOrNotSymHTML(isMonthOfYear((List)settings.cond_months)))  : sBLANK
        }
        if((List)settings.cond_alarmSystemStatus || ((List)settings.cond_mode)) {
            str += spanSmBr(" ${sBULLET} Location: " + getOkOrNotSymHTML(locationCondOk()))
            String a = location?.hsmStatus ?: "disarmed"
            str += (List)settings.cond_alarmSystemStatus ? spanSmBr("    - Alarm Mode ${a} in: ${(List)settings.cond_alarmSystemStatus} " + getOkOrNotSymHTML(isInAlarmMode((List)settings.cond_alarmSystemStatus))) : sBLANK
            Boolean not = ((String)settings.cond_mode_cmd == "not")
            str += (List)settings.cond_mode ? spanSmBr("    - Mode ${getCurrentMode()} (${not ? "not in" : "in"}): ${(List)settings.cond_mode} " + getOkOrNotSymHTML(isInMode((List)settings.cond_mode, not))) : sBLANK
        }

        if(deviceCondConfigured()) {
            List<String> devConds = lDATTSTR + lDATTNUM
            devConds.each { String evt->
                if(devCondConfigured(evt)) {
                    Boolean condOk = false
                    //thermostatFanMode
                    if(evt in lDATTSTR) { condOk = checkDeviceCondOk(evt) }
                    else if(evt in lDATTNUM) { condOk = checkDeviceNumCondOk(evt) }

                    List devs = settings."${sPre}${evt}" ?: null
                    if(devs){
                        List myV = []
                        if(!addFoot) devs.each { dev -> myV.push(dev?.currentValue(evt)?.toString()) }
                        str += spanSmBr(" ${sBULLET} ${evt?.capitalize()} (${settings."${sPre}${evt}"?.size()}) ${!addFoot ? myV : sBLANK} " + getOkOrNotSymHTML(condOk))
                    }

                    String a = "    - Desired Value: "
                    String aG = (Boolean)settings."cond_${inType}_avg" ? "(Avg)" : sBLANK
                    String cmd = settings."${sPre}${evt}_cmd" ?: sNULL
                    if(cmd in numOpts()) {
                        def cmdLow = settings."${sPre}${evt}_low"!=null ? settings."${sPre}${evt}_low" : null
                        def cmdHigh = settings."${sPre}${evt}_high"!=null ? settings."${sPre}${evt}_high" : null
                        def cmdEq = settings."${sPre}${evt}_equal"!=null ?  settings."${sPre}${evt}_equal" : null
                        String aU = attUnit(evt) ')' + aG
                        str += (cmd == sEQUALS && cmdEq) ? spanSmBr(a+"( =${cmdEq}"+aU) : sBLANK
                        str += (cmd in [sBETWEEN, sNBETWEEN] && cmdLow && cmdHigh) ? spanSmBr(a+cmd.capitalize()+" (${cmdLow}-${cmdHigh}"+aU) : sBLANK
                        str += (cmd == sABOVE && cmdHigh) ? spanSmBr(a+"( >${cmdHigh}"+aU) : sBLANK
                        str += (cmd == sBELOW && cmdLow) ? spanSmBr(a+"( <${cmdLow}"+aU) : sBLANK
                    } else {
                        str += cmd ? spanSmBr(a+"(${cmd})" + aG) : sBLANK
                    }
                    str += ((Boolean)settings."${sPre}${evt}_all" == true) ? spanSmBr("    - Require All: (${settings."${sPre}${evt}_all"})") : sBLANK
                }
            }
        }
        str += addFoot ? inputFooter(sTTM) : sBLANK
    } else {
        str += addFoot ? inputFooter(sTTC, sCLRGRY, true) : sBLANK
    }
    return str
}

static String attUnit(String attr) {
    switch(attr) {
        case sHUMID:
        case sLEVEL:
        case sBATT:
            return " percent"
        case sTEMP:
            return " degrees"
        case "illuminance":
            return " lux"
        case sPOWER:
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

String getZoneVolDesc(zone, Map<String,Object>volMap) {
    String str = sBLANK
    String k = (String)zone.key
    str += spanSm(" ${sBULLET} ${zone?.value?.name} ${zone?.value?.active == true ? spanSm(" (Active)", sCLRGRN2) : spanSm(" (Inactive)", sCLRGRY)}")
    if((Boolean)settings.act_EchoZones_vol_per_zone && volMap && volMap[k]) {
        str += volMap[k].change ? lineBr() + spanSm("    - New Volume: ${volMap[k].change}") : sBLANK
        str += volMap[k].restore ? lineBr() + spanSm("    - Restore Volume: ${volMap[k].restore}") : sBLANK
    }
    return str
}

String getActionDesc(Boolean addFoot=true) {
    Boolean confd = executionConfigured()
    String str = sBLANK
    Map actMap = (Map)state.actionExecMap
    Map znVolMap = actMap && actMap.config && actMap.config.zoneVolume ? (Map)actMap.config.zoneVolume : null
    str += addFoot ? spanSmBr(strUnder("Action Type:")) : sBLANK
    str += addFoot ? spanSmBr(" ${sBULLET} " + (String)buildActTypeEnum()."${(String)settings.actionType}") + lineBr() : sBLANK
    if((String)settings.actionType && confd) {
        Boolean isTierAct = isTierAction()
        def eDevs = parent?.getDevicesFromList(settings.act_EchoDevices)
        Map zones = getZoneStatus()
        String tierDesc = isTierAct ? getTierRespDesc() : sNULL
        String tierStart = isTierAct ? actTaskDesc("act_tier_start_") : sNULL
        String tierStop = isTierAct ? actTaskDesc("act_tier_stop_") : sNULL
        str += zones?.size() ? spanSmBr(strUnder("Echo Zones:")) : sBLANK
        str += zones?.size() ? spanSmBr(zones?.collect { getZoneVolDesc(it, znVolMap) }?.join(sLINEBR)) + (eDevs?.size() ? sLINEBR : sBLANK) : sBLANK
        str += eDevs?.size() ? spanSm(strUnder("Alexa Devices:")) + (zones?.size() ? spanSm(" (Inactive Zone Default)", sCLRGRY) : sBLANK) + lineBr() + spanSmBr(eDevs?.collect { " ${sBULLET} ${it?.displayName?.toString()?.replace("Echo - ", sBLANK)}" }?.join(sLINEBR)) : sBLANK
        str += tierDesc ? sLINEBR + spanSm(tierDesc) + (tierStart || tierStop ? sBLANK : sLINEBR) : sBLANK
        str += tierStart ? spanSmBr(tierStart) : sBLANK
        str += tierStop ? spanSmBr(tierStop) : sBLANK
        str += settings.act_volume_change != null ? spanSmBr(" - New Volume: (${settings.act_volume_change})") : sBLANK
        str += settings.act_volume_restore != null ? spanSmBr(" - Restore Volume: (${settings.act_volume_restore})") : sBLANK
        str += settings.act_delay ? spanSmBr("Delay: (${settings.act_delay})") : sBLANK
        str += (String)settings.actionType in [sSPEAK, sSPEAKP, sANN, sSPEAKT, "speak_parallel_tiered", "announcement_tiered"] && (String)settings."act_${(String)settings.actionType}_txt" ? spanSmBr("Using Default Response: (True)") : sBLANK
        String trigTasks = !isTierAct ? actTaskDesc("act_") : sNULL
        str += trigTasks ? spanSm(trigTasks) : sBLANK
        str += addFoot ? inputFooter(sTTM) : sBLANK
    }
    str += !confd && addFoot ? inputFooter("Tap to configure (Required!)", sCLRRED, true) : sBLANK
    return divSm(str.replaceAll("\n\n\n", "\n\n"), sCLR4D9)
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
            startLbl1 = startType.capitalize() + sSPACE + startoffset ? "with offset " : sBLANK
        }
        if(stopType in lSUNRISESET) {
            Long stopl = (stopType == 'sunrise' ? lsunrise : lsunset) + stopoffset
            stopTime = new Date(stopl)
            stopLbl1 = stopType.capitalize() + sSPACE + stopoffset ? "with offset " : sBLANK
        }
    }
    String startLbl = startTime ? epochToTime(startTime) : sBLANK
    String stopLbl = stopTime ? epochToTime(stopTime) : sBLANK

    return startLbl && stopLbl ? "${addPre ? "Time Condition:\n" : sBLANK}(${startLbl1}${startLbl} - ${stopLbl1}${stopLbl})" : inactFoot(sTTC)
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

static String randomString(Integer len) {
    def pool = ["a".."z", 0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    // logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}

static def getRandomItem(List items) {
    def list = [] // new ArrayList<String>()
    items?.each { list.add(it) }
    return list.get(new Random().nextInt(list.size()))
}

Boolean showChgLogOk() { return ((Boolean)state.isInstalled && !(Boolean)state.shownChgLog) }

static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${branchFLD}/resources/icons/${imgName}.png" }

static String getPublicImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" }

static String sTS(String t, String i = sNULL, Boolean bold=false) { return "<h3>${i ? "<img src='${i}' width='42'> " : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>" }

static String s3TS(String t, String st, String i = sNULL, String c=sCLR4D9) { return "<h3 style='color:${c};font-weight: bold;'>${i ? "<img src='${i}' width='42'> " : sBLANK} ${t?.replaceAll("\n", "<br>")}</h3>${st ? "${st}" : sBLANK}" }

static String pTS(String t, String i = sNULL, Boolean bold=true, String color=sNULL) { return "${color ? "<div style='color: $color;'>" : sBLANK}${bold ? "<b>" : sBLANK}${i ? "<img src='${i}' width='42'> " : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : ""}${color ? "</div>" : ""}" }

static String inTS1(String str, String img = sNULL, String clr=sNULL) { return spanSmBldUnd(str, clr, img) }
static String inTS(String str, String img = sNULL, String clr=sNULL, Boolean und=true) { return divSm(strUnder(str?.replaceAll("\n", " ")?.replaceAll("<br>", " "), und), clr, img) }

// Root HTML Objects
static String sectHead(String str, String img = sNULL) { return str ? "<h3 style='margin-top:0;margin-bottom:0;'>" + spanImgStr(img) + span(str, "darkorange", sNULL, true) + "</h3>" + "<hr style='background-color:${sCLRGRY};font-style:italic;height:1px;border:0;margin-top:0;margin-bottom:0;'>" : sBLANK }
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }
static String div(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return str ? "<div ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</div>${br ? sLINEBR : sBLANK}" : sBLANK }
static String spanImgStr(String img=sNULL) { return img ? span("<img src='${(!img.startsWith("http://") && !img.startsWith("https://")) ? getAppImg(img) : img}' width='42'> ") : sBLANK }
static String divImgStr(String str, String img=sNULL) { return str ? div(img ? spanImg(img) + span(str) : str) : sBLANK }
static String strUnder(String str, Boolean showUnd=true) { return str ? (showUnd ? "<u>${str}</u>" : str) : sBLANK }
static String getOkOrNotSymHTML(Boolean ok) { return ok ? span("(${okSymFLD})", sCLRGRN2) : span("(${notOkSymFLD})", sCLRRED2) }
static String htmlLine(String color=sCLR4D9) { return "<hr style='background-color:${color};height:1px;border:0;margin-top:0;margin-bottom:0;'>" }
static String lineBr(Boolean show=true) { return show ? sLINEBR : sBLANK }
static String inputFooter(String str, String clr=sCLR4D9, Boolean noBr=false) { return str ? ((noBr ? sBLANK : lineBr()) + divSmBld(str, clr)) : sBLANK }
static String inactFoot(String str) { return str ? inputFooter(str, sCLRGRY, true) : sBLANK }
static String actFoot(String str) { return str ? inputFooter(str, sCLR4D9, false) : sBLANK }
static String optPrefix() { return spanSm(" (Optional)", "violet") }
//

// Custom versions of the root objects above
static String spanBld(String str, String clr=sNULL, String img=sNULL)      { return str ? spanImgStr(img) + span(str, clr, sNULL, true)             : sBLANK }
static String spanBldBr(String str, String clr=sNULL, String img=sNULL)    { return str ? spanImgStr(img) + span(str, clr, sNULL, true, true)       : sBLANK }
static String spanBr(String str, String clr=sNULL, String img=sNULL)       { return str ? spanImgStr(img) + span(str, clr, sNULL, false, true)      : sBLANK }
static String spanSm(String str, String clr=sNULL, String img=sNULL)       { return str ? spanImgStr(img) + span(str, clr, sSMALL)                 : sBLANK }
static String spanSmBr(String str, String clr=sNULL, String img=sNULL)     { return str ? spanImgStr(img) + span(str, clr, sSMALL, false, true)    : sBLANK }
static String spanSmBld(String str, String clr=sNULL, String img=sNULL)    { return str ? spanImgStr(img) + span(str, clr, sSMALL, true)           : sBLANK }
static String spanSmBldUnd(String str, String clr=sNULL, String img=sNULL) { return str ? spanImgStr(img) + span(strUnder(str), clr, sSMALL, true) : sBLANK }
static String spanSmBldBr(String str, String clr=sNULL, String img=sNULL)  { return str ? spanImgStr(img) + span(str, clr, sSMALL, true, true)     : sBLANK }
static String spanMd(String str, String clr=sNULL, String img=sNULL)       { return str ? spanImgStr(img) + span(str, clr, sMEDIUM)                : sBLANK }
static String spanMdBr(String str, String clr=sNULL, String img=sNULL)     { return str ? spanImgStr(img) + span(str, clr, sMEDIUM, false, true)   : sBLANK }
static String spanMdBld(String str, String clr=sNULL, String img=sNULL)    { return str ? spanImgStr(img) + span(str, clr, sMEDIUM, true)          : sBLANK }
static String spanMdBldBr(String str, String clr=sNULL, String img=sNULL)  { return str ? spanImgStr(img) + span(str, clr, sMEDIUM, true, true)    : sBLANK }


static String divBld(String str, String clr=sNULL, String img=sNULL)        { return str ? div(spanImgStr(img) + span(str), clr, sNULL, true, false)   : sBLANK }
static String divBldBr(String str, String clr=sNULL, String img=sNULL)      { return str ? div(spanImgStr(img) + span(str), clr, sNULL, true, true)    : sBLANK }
static String divBr(String str, String clr=sNULL, String img=sNULL)         { return str ? div(spanImgStr(img) + span(str), clr, sNULL, false, true)   : sBLANK }
static String divSm(String str, String clr=sNULL, String img=sNULL)         { return str ? div(spanImgStr(img) + span(str), clr, sSMALL)              : sBLANK }
static String divSmBr(String str, String clr=sNULL, String img=sNULL)       { return str ? div(spanImgStr(img) + span(str), clr, sSMALL, false, true) : sBLANK }
static String divSmBld(String str, String clr=sNULL, String img=sNULL)      { return str ? div(spanImgStr(img) + span(str), clr, sSMALL, true)        : sBLANK }
static String divSmBldBr(String str, String clr=sNULL, String img=sNULL)    { return str ? div(spanImgStr(img) + span(str), clr, sSMALL, true, true)  : sBLANK }

def appFooter() {
    section() {
        paragraph htmlLine("orange")
        paragraph "<div style='color:orange;text-align:center;'>Echo Speaks<br><a href='${textDonateLink()}' target='_blank'><img width=120' height='120' src='https://raw.githubusercontent.com/tonesto7/homebridge-hubitat-tonesto7/master/images/donation_qr.png'></a><br><br>Please consider donating if you find this integration useful.</div>"
    }
}

static String bulletItem(String inStr, String strVal) { return "${inStr == sBLANK ? sBLANK : "\n"} \u2022 ${strVal}" }
static String dashItem(String inStr, String strVal, Boolean newLine=false) { return "${(inStr == sBLANK && !newLine) ? sBLANK : "\n"} - ${strVal}" }

Integer stateSize() {
    String j = new groovy.json.JsonOutput().toJson((Map)state)
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

    getTheLock(sHMLF, "addToHistory(${logKey})")


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

public Map getLogConfigs() {
    return [
        info: (Boolean) settings.logInfo,
        warn: (Boolean) settings.logWarn,
        error: (Boolean) settings.logError,
        debug: (Boolean) settings.logDebug,
        trace: (Boolean) settings.logTrace
    ]
}

public void enableDebugLog() { settingUpdate("logDebug", sTRUE, sBOOL); logInfo("Debug Logs Enabled From Main App...") }
public void disableDebugLog() { settingUpdate("logDebug", sFALSE, sBOOL); logInfo("Debug Logs Disabled From Main App...") }
public void enableTraceLog() { settingUpdate("logTrace", sTRUE, sBOOL); logInfo("Trace Logs Enabled From Main App...") }
public void disableTraceLog() { settingUpdate("logTrace", sFALSE, sBOOL); logInfo("Trace Logs Disabled From Main App...") }

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info sSPACE + logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn sSPACE + logPrefix(msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, 15) } }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error logPrefix(msg, sCLRRED)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (ignored) {
        }
        if(a) log.error logPrefix(a, sCLRRED)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, 15) }
}

static String logPrefix(String msg, String color = sNULL) {
    return span("Action (v" + appVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

Map getLogHistory() {
    getTheLock(sHMLF, "getLogHistory")

    List warn = (List)getMemStoreItem("warnHistory")
    List errs = (List)getMemStoreItem("errorHistory")
    releaseTheLock(sHMLF)
    return [ warnings: []+warn, errors: []+errs ]
}

/*
private void clearLogHistory() {
    getTheLock(sHMLF, "clearLogHistory")

    updMemStoreItem("warnHistory", [])
    updMemStoreItem("errorHistory", [])
    releaseTheLock(sHMLF)
} */

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

/*
// Memory Barrier
@Field static java.util.concurrent.Semaphore theMBLockFLD=new java.util.concurrent.Semaphore(0)
static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
} */

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

void getTheLock(String qname, String meth=sNULL, Boolean longWait=false) {
    Long waitT = longWait ? 1000L : 60L
//    Boolean wait = false
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
//        wait = true
        if((now() - timeL) > 30000L) {
            releaseTheLock(qname)
            if(devModeFLD) log.warn "overriding lock $meth"
        }
    }
    lockTimesFLD[semaSNum] = (Long)now()
    lockTimesFLD = lockTimesFLD
    lockHolderFLD[semaSNum] = "${app.getId()} ${meth}".toString()
    lockHolderFLD = lockHolderFLD
//    return wait
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
        default: return sBLANK
    }
}

/************************************************
            SEQUENCE TEST LOGIC
*************************************************/

def searchTuneInResultsPage() {
    return dynamicPage(name: "searchTuneInResultsPage", uninstall: false, install: false) {
        Map results = parent.executeTuneInSearch((String)settings.tuneinSearchQuery)
        section(sectHead("Search Results: (Query: ${(String)settings.tuneinSearchQuery})")) {
            if(results?.browseList && results?.browseList?.size()) {
                results?.browseList?.eachWithIndex { item, i->
                    if(i < 25) {
                        if(item?.browseList != null && item?.browseList?.size()) {
                            item?.browseList?.eachWithIndex { item2, i2->
                                String str = sBLANK
                                str += "ContentType: (${item2?.contentType})"
                                str += "\nId: (${item2?.id})"
                                str += "\nDescription: ${item2?.description}"
                                href "searchTuneInResultsPage", title: pTS((String)item2?.name?.take(75), (String)item2?.image), description: str
                            }
                        } else {
                            String str = sBLANK
                            str += "ContentType: (${item?.contentType})"
                            str += "\nId: (${item?.id})"
                            str += "\nDescription: ${item?.description}"
                            href "searchTuneInResultsPage", title: pTS( (String)((String)item?.name)?.take(75), (String)item?.image), description: str
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
            Integer hue = Math.round((Integer)color.h / 3.6).toInteger()
            level = level ?: color.l
            return [hue: hue, saturation: color.s, level: level]
        }
    }
}

private captureLightState(List devs) {
    Map sMap = [:]
    if(devs) {
        devs.each { dev->
            String dId = dev?.id
            sMap[dId] = [:]
            [sSWITCH, sLEVEL, "hue", "saturation", "colorTemperature", "color"].each { String att ->
                if(dev?.hasAttribute(att)) { sMap[dId]."${att}" = dev?.currentValue(att) }
            }
        }
    }
    atomicState.light_restore_map = sMap
    if(devModeFLD) log.debug "captureLightState: sMap: $sMap"
}

private restoreLightState(List devs) {
    Map sMap = atomicState.light_restore_map
    if(devModeFLD) log.debug "restoreLightState: sMap: $sMap"
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
    [name: "Soft White", rgb: "#B6DA7C", h: 83, s: 44, l: 67],              [name: "Warm White", rgb: "#DAF17E", h: 51, s: 20, l: 100],      [name: "Very Warm White", rgb: "#DAF17E", h: 51, s: 60, l: 51],
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
    [name: "Dark Magenta", rgb: "#8B008B", h: 300, s: 100, l: 27],          [name: "Dark Olive Green",     rgb: "#556B2F", h: 82, s: 39, l: 30],   [name: "Dark Orange", rgb: "#FF8C00", h: 33, s: 100, l: 50],
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
    [name: "Light Pink", rgb: "#FFB6C1", h: 351, s: 100, l: 86],            [name: "Light Salmon", rgb: "#FFA07A", h: 17, s: 100, l: 74],       [name: "Light Sea Green", rgb: "#20B2AA", h: 177, s: 70, l: 41],
    [name: "Light Sky Blue", rgb: "#87CEFA", h: 203, s: 92, l: 75],         [name: "Light Slate Gray", rgb: "#778899", h: 210, s: 14, l: 53],   [name: "Light Steel Blue", rgb: "#B0C4DE", h: 214, s: 41, l: 78],
    [name: "Light Yellow", rgb: "#FFFFE0", h: 60, s: 100, l: 94],           [name: "Lime", rgb: "#00FF00", h: 120, s: 100, l: 50],              [name: "Lime Green", rgb: "#32CD32", h: 120, s: 61, l: 50],
    [name: "Linen", rgb: "#FAF0E6", h: 30, s: 67, l: 94],                   [name: "Maroon", rgb: "#800000", h: 0, s: 100, l: 25],              [name: "Medium Aquamarine", rgb: "#66CDAA", h: 160, s: 51, l: 60],
    [name: "Medium Blue", rgb: "#0000CD", h: 240, s: 100, l: 40],           [name: "Medium Orchid", rgb: "#BA55D3", h: 288, s: 59, l: 58],      [name: "Medium Purple", rgb: "#9370DB", h: 260, s: 60, l: 65],
    [name: "Medium Sea Green", rgb: "#3CB371", h: 147, s: 50, l: 47],       [name: "Medium Slate Blue", rgb: "#7B68EE", h: 249, s: 80, l: 67],  [name: "Medium Spring Green", rgb: "#00FA9A", h: 157, s: 100, l: 49],
    [name: "Medium Turquoise", rgb: "#48D1CC", h: 178, s: 60, l: 55],       [name: "Medium Violet Red", rgb: "#C71585", h: 322, s: 81, l: 43],  [name: "Midnight Blue", rgb: "#191970", h: 240, s: 64, l: 27],
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
                    def vv = settings[fk]
                    if(ek==sTIME) vv = dateTimeFmt((Date)toDateTime(vv), "HH:mm")
                    setObjs[fk] = [type: ek, value: vv]
                }
            }
        }
        ((Map<String,String>)typeObj.caps).each { ck, cv->
            settings.findAll { it.key.endsWith(ck) }?.each { String fk, fv-> setObjs[fk] = [type: "capability", value: (fv instanceof List ? fv?.collect { it?.id?.toString() } : it?.id?.toString ) ] }
        }
        ((Map<String, String>)typeObj.dev).each { dk, dv->
            settings.findAll { it.key.endsWith(dk) }?.each { String fk, fv-> setObjs[fk] = [type: "device", value: (fv instanceof List ? fv.collect { it?.id?.toString() } : it?.id?.toString() ) ] }
        }
    }
    Map data = [:]
    String newlbl = app?.getLabel()?.toString()?.replace(" (A ${sPAUSESymFLD})", sBLANK)
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
    data.state = state?.findAll { !((String)it?.key in stateSkip) }
    return data
}
