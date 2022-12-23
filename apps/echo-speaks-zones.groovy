/**
 *  Echo Speaks - Zones (Hubitat)
 *
 *  Copyright 2018, 2019, 2020, 2021, 2022, 2023 Anthony Santilli
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
 */
//file:noinspection GroovyUnusedAssignment
//file:noinspection unused
//file:noinspection GroovySillyAssignment
//file:noinspection GrMethodMayBeStatic

import groovy.json.JsonOutput
import groovy.transform.Field

import java.text.SimpleDateFormat
import java.util.concurrent.Semaphore

//************************************************
//*               STATIC VARIABLES               *
//************************************************
@Field static final String appVersionFLD  = '4.2.2.0'
@Field static final String appModifiedFLD = '2022-12-22'
@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sBULLET        = '\u2022'
//@Field static final String sBULLETINV     = '\u25E6'
//@Field static final String sSQUARE        = '\u29C8'
//@Field static final String sPLUS          = '\u002B'
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
@Field static final String sTEXT          = 'text'
@Field static final String sTIME          = 'time'
@Field static final String sMODE          = 'mode'
//@Field static final String sCOMPLT        = 'complete'
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
//@Field static final String sTTCR          = 'Tap to configure (Required)'
//@Field static final String sTTP           = 'Tap to proceed...'
//@Field static final String sTTS           = 'Tap to select...'
@Field static final String sSETTINGS      = 'settings'
@Field static final String sRESET         = 'reset'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sSWITCH        = 'switch'
@Field static final String sCHKBOX        = 'checkbox'
@Field static final String sCOMMAND       = 'command'
@Field static final String zoneHistFLD    = 'zoneHistory'
@Field static final List<String> lSUNRISESET   = ["sunrise", "sunset"]

//************************************************
//*          IN-MEMORY ONLY VARIABLES            *
//* (Cleared only on HUB REBOOT or CODE UPDATES) *
//************************************************
@Field volatile static Map<String,Map> historyMapFLD = [:]
// @Field volatile static String gitBranchFLD = null

static String appVersion()  { return appVersionFLD }
static String appVersionDt()  { return appModifiedFLD }

definition(
    name                : "Echo Speaks - Zones",
    namespace           : "tonesto7",
    author              : "Anthony Santilli",
    description         : "DO NOT INSTALL FROM MARKETPLACE\n\nAllows you to create virtual broadcast zones based on your echo devices using device/location events to active the zone.",
    category            : "My Apps",
    parent              : "tonesto7:Echo Speaks",
    iconUrl             : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    iconX2Url           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    iconX3Url           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/es_groups.png",
    importUrl           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/apps/echo-speaks-zones.groovy",
    documentationLink   : documentationUrl(),
    videoLink           : videoUrl()
)

preferences {
    page(name: "startPage")
    page(name: "uhOhPage")
    page(name: "codeUpdatePage")
    page(name: "mainPage")
    page(name: "prefsPage")
    page(name: "conditionsPage")
    page(name: "condTimePage")
    page(name: "zoneNotifPage")
    page(name: "zoneNotifTimePage")
    page(name: "zoneHistoryPage")
    page(name: "uninstallPage")
    page(name: "namePage")
}

def startPage() {
    if(parent != null) {
        if(!(Boolean)state.isInstalled && !(Boolean)parent?.childInstallOk()) { return uhOhPage() }
        else {
            updDeviceInputs()
            return (minVersionFailed()) ? codeUpdatePage() : mainPage()
        }
    } else { return uhOhPage() }
}

def codeUpdatePage () {
    return dynamicPage(name: "codeUpdatePage", title: "Update is Required", install: false, uninstall: false) {
        section() { paragraph spanSmBld("Looks like your Zone App needs an update<br><br>Please make sure all app and device code is updated to the most current version<br><br>Once updated your zones will resume normal operation.", sCLRRED) }
    }
}

def uhOhPage () {
    return dynamicPage(name: "uhOhPage", title: "This install Method is Not Allowed", install: false, uninstall: true) {
        section() {
            paragraph spanBld("HOUSTON WE HAVE A PROBLEM!<br><br>Echo Speaks - Zones can't be directly installed from the Marketplace.<br><br>Please use the Echo Speaks SmartApp to configure them.", sCLRRED)
        }
    }
}

def appInfoSect()	{
    String instDt = state.dateInstalled ? fmtTime((String)state.dateInstalled, "MMM dd '@' h:mm a", true) : sNULL
    String str = spanBld((String)app.name, "black", "es_groups")
    str += (app.label && (String)app.label != (String)app.name) ? spanBldBr(" (${app.label.replace(" (Z)", sBLANK)})") : sLINEBR
    str += spanSmBld("Version: ") + spanSmBr(appVersionFLD)
    str += instDt ? spanSmBld("Installed: ") + spanSmBr(instDt) : sBLANK
    section() { paragraph divSm(str, sCLRGRY) }
}

def mainPage() {
    Boolean newInstall = !(Boolean)state.isInstalled
    return dynamicPage(name: "mainPage", nextPage: (!newInstall ? sBLANK : "namePage"), uninstall: newInstall, install: !newInstall) {
        Boolean dup = ((Boolean)settings.duplicateFlag == true && (Boolean)state.dupPendingSetup)
        if(dup) {
            state.dupOpenedByUser = true
            section() { paragraph spanBld("This Zone was created from an existing zone.<br><br>Please review the settings and save to activate...<br>${state.badMode ?: sBLANK}", sCLRORG, "pause_orange") }
        }
        appInfoSect()

        Boolean paused = isPaused()
        if(paused) {
            section() {
                paragraph spanSmBlr("This Zone is currently disabled...<br>To edit the please re-enable it.", sCLRRED, "pause_orange")
            }
        } else {
            section() { paragraph divSm(getOverallDesc(), sCLRGRY) }

            section(sectHead("Zone Devices:")) {
                echoDevicesInputByPerm('announce')
            }
            if(settings.zone_EchoDevices) {
                if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
                Boolean condConf = conditionsConfigured()
                section(sectHead("Zone Configuration:")) {
                    href "conditionsPage", title: inTS1("Zone Activation Conditions", "conditions") + optPrefix(), description: divSm(getConditionsDesc(true), sCLR4D9)
                }

                if(condConf && settings.zone_EchoDevices) {
                    section(sectHead("Condition Delays:")) {
                        input "zone_active_delay", sNUMBER, title: inTS1("Delay Activation (In Seconds)", "delay_time") + optPrefix(), defaultValue: null, required: false, submitOnChange: true
                        input "zone_inactive_delay", sNUMBER, title: inTS1("Delay Deactivation (In Seconds)", "delay_time") + optPrefix(), defaultValue: null, required: false, submitOnChange: true
                    }
                    section(sectHead("Control Switches on Zone Active (Optional):")) {
                        input "zone_active_switches_on", "capability.switch", title: inTS1("Turn on Switches when Zone Active", sSWITCH) + optPrefix(), description: inputFooter(sTTC, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
                        input "zone_active_switches_off", "capability.switch", title: inTS1("Turn off Switches when Zone Active", sSWITCH) + optPrefix(), description: inputFooter(sTTC, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
                    }
                    section(sectHead("Control Switches on Zone Inactive (Optional):")) {
                        input "zone_inactive_switches_on", "capability.switch", title: inTS1("Turn on Switches when Zone Inactive", sSWITCH) + optPrefix(), description: inputFooter(sTTC, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
                        input "zone_inactive_switches_off", "capability.switch", title: inTS1("Turn off Switches when Zone Inactive", sSWITCH) + optPrefix(), description: inputFooter(sTTC, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
                    }
                    section(sectHead("Notifications:")) {
                        def t0 = getAppNotifDesc()
                        href "zoneNotifPage", title: inTS1("Send Notifications", "notification2"), description: t0 ? divSm(t0 + inputFooter(sTTM), sCLR4D9) : inputFooter(sTTC, sNULL, true)
                    }
                }
            }
            updConfigStatusMap()
        }

        section(sectHead("Zone History")) {
            href "zoneHistoryPage", title: inTS1("View Zone History", "tasks"), description: sBLANK
        }

        section(sectHead("Preferences")) {
            href "prefsPage", title: inTS1("Logging Preferences", sSETTINGS), description: sBLANK
            if((Boolean)state.isInstalled) {
                input "zonePause", sBOOL, title: inTS1("Disable Zone?", "pause_orange"), defaultValue: false, submitOnChange: true
                input "createZoneDevice", sBOOL, title: inTS1("Create a Virtual Device for this Zone?", "question"), defaultValue: false, submitOnChange: true
                input "forceAnnounce", sBOOL, title: inTS1("Convert Zone speak commands to announcements?", "question"), defaultValue: true, submitOnChange: true
                if((Boolean)settings.zonePause) { unsubscribe() }
            }
        }
        //log.debug "myZoneStatus: ${myZoneStatus()}"
        if((Boolean) state.isInstalled) {
            section(sectHead("Name this Zone:")) {
                input "appLbl", sTEXT, title: inTS1("Zone Name", "name_tag"), description: sBLANK, required:true, submitOnChange: true
            }
            section(sectHead("Remove Zone:")) {
                href "uninstallPage", title: inTS1("Remove this Zone", "uninstall"), description: inputFooter("Tap to Remove...", sCLRGRY, true)
            }
            section(sectHead("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                String issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28ZONES+BUG%29+&projects=echo-speaks%2F6"
                String featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BZones+Feature+Request%5D&projects=echo-speaks%2F6"
                href url: featUrl, style: sEXTNRL, required: false, title: inTS1("New Feature Request", "www"), description: spanSm("Tap to open browser", sCLRGRY)
                href url: issueUrl, style: sEXTNRL, required: false, title: inTS1("Report an Issue", "www"), description: spanSm("Tap to open browser", sCLRGRY)
            }
        }
    }
}

private echoDevicesInputByPerm(String type) {
    List echoDevs = parent?.getChildDevicesByCap(type)
    if(echoDevs?.size()) {
        Map eDevsMap = echoDevs?.collectEntries { [(it.getId()): [label: (String)it.getLabel(), lsd: (it.currentWasLastSpokenToDevice?.toString() == sTRUE)]] }?.sort { a,b -> b?.value?.lsd <=> a?.value?.lsd ?: a?.value?.label <=> b?.value?.label }
        Map moptions =  eDevsMap?.collectEntries { [(it.key.toString()): "${it?.value?.label}${(it?.value?.lsd == true) ? " (Last Spoken To)" : sBLANK}".toString()] }
        input "zone_EchoDevices", sENUM, title: inTS1("Echo Devices in Zone", "echo_gen1"), description: spanSm("Select the devices", sCLRGRY), options: moptions, multiple: true, required: true, submitOnChange: true

        // updDeviceInputs()
    } else { paragraph spanSmBld("No devices were found with support for ($type)", sCLRRED) }
}

def zoneHistoryPage() {
    return dynamicPage(name: "zoneHistoryPage", title: "Zone History", install: false, uninstall: false) {
        section() {
            getZoneHistory()
        }
        List eData = (List)getMemStoreItem(zoneHistFLD) ?: []
        if(eData.size()) {
            section(sBLANK) {
                input "clearZoneHistory", sBOOL, title: inTS1("Clear Zone History?", sRESET), description: spanSm("Clears Stored Zone History.", sCLRGRY), defaultValue: false, submitOnChange: true
                if(settings.clearZoneHistory) { settingUpdate("clearZoneHistory", sFALSE, sBOOL); updMemStoreItem(zoneHistFLD, []) }
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
            input "logDebug", sBOOL, title: inTS1("Show Debug Logs?", sDEBUG), description: spanSm("Auto disables after 6 hours", sCLRGRY), required: false, defaultValue: false, submitOnChange: true
            input "logTrace", sBOOL, title: inTS1("Show Detailed Logs?", sDEBUG), description: spanSm("Only enable when asked to (Auto disables after 6 hours)", sCLRGRY), required: false, defaultValue: false, submitOnChange: true
        }
        if((Boolean)state.isInstalled) {
            if(advLogsActive()) { logsEnabled() }
        }
    }
}

def namePage() {
    return dynamicPage(name: "namePage", install: true, uninstall: false) {
        section(sectHead("Name the Zone:")) {
            input "appLbl", sTEXT, title: inTS1("Zone Name", "name_tag"), description: sBLANK, required:true, submitOnChange: true
        }
    }
}

def uninstallPage() {
    return dynamicPage(name: "uninstallPage", title: "Uninstall", install: false , uninstall: true) {
        section(sBLANK) { paragraph spanSmBld("This will delete this Echo Speaks Zone.", sCLRORG) }
    }
}

private void updDeviceInputs() {
    // log.trace "updDeviceInputs..."
    List aa = (List)settings.zone_EchoDevices
    List devIds
    Boolean updList = false
    try {
        updList = (aa.size() && aa[0].id != null)
        devIds = aa.collect { it?.id?.toString() }
        // log.debug "updList(try): ${devIds.unique()}"
    } catch (ignored) {
        // log.debug "ex: $ex"
        devIds = aa.collect { it?.toString() }
    }
    if(updList && devIds) {
        logDebug("updList: $devIds")
        app.updateSetting( "zone_EchoDevices", [type: "enum", value: devIds.unique()])
    }
    if(devIds) { app.updateSetting( "zone_EchoDeviceList", [type: "capability", value: devIds.unique()]) } // this won't take effect until next execution
}

/******************************************************************************
    CONDITIONS SELECTION PAGE
******************************************************************************/

def conditionsPage() {
    return dynamicPage(name: "conditionsPage", title: "Zone Activation Conditions", nextPage: "mainPage", install: false, uninstall: false) {
        String desc = getConditionsDesc(false)
        if(!desc) { desc= "No Conditions/Restrictions Set" }
        section() { paragraph divSm(desc, sCLR4D9) }

        Boolean multiConds = multipleConditions()
        section() {
            if(multiConds) {
                input "cond_require_all", sBOOL, title: inTS1("Require All Selected Conditions to Pass Before Activating Zone?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
            paragraph spanSmBldBr("Notice:", sCLR4D9) + spanSm(reqAllCond() ? "All selected conditions must pass before this zone is active." : "Any condition will allow this zone to become active.", sCLR4D9)
        }
        section(sectHead("Time/Date Restrictions")) {
            href "condTimePage", title: inTS1("Time Schedule", "clock"), description: spanSm(getTimeCondDesc(false), sCLR4D9)
            input "cond_days", sENUM, title: inTS1("Days of the week", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: weekDaysEnum()
            input "cond_months", sENUM, title: inTS1("Months of the year", "day_calendar"), multiple: true, required: false, submitOnChange: true, options: monthEnum()
        }

        section (sectHead("Mode Conditions")) {
            input "cond_mode", sMODE, title: inTS1("Location Modes...", sMODE), multiple: true, required: false, submitOnChange: true
            if((List)settings.cond_mode) {
                input "cond_mode_cmd", sENUM, title: inTS1("are...", sCOMMAND), options: ["not":"not in these modes", "are":"In these Modes"], required: true, multiple: false, submitOnChange: true
                if((List)settings.cond_mode && (String)settings.cond_mode_cmd) {
                    input "cond_mode_db", sBOOL, title: inTS1("Deactivate Zone immediately when Mode condition no longer passes?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                }
            }
        }

        section (sectHead("Alarm Conditions")) {
            input "cond_alarm", sENUM, title: inTS1("${getAlarmSystemName()} is...", "alarm_home"), options: getAlarmTrigOpts(), multiple: true, required: false, submitOnChange: true
            if((List)settings.cond_alarm) {
                input "cond_alarm_db", sBOOL, title: inTS1("Deactivate Zone immediately when Alarm condition no longer passes?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
        }

        condNonNumSect(sSWITCH, sSWITCH, "Switches/Outlets Conditions", "Switches/Outlets", ["on","off"], "are", sSWITCH)

        condNonNumSect("contact", "contactSensor", "Door, Window, Contact Sensors Conditions", "Contact Sensors",  ["open","closed"], "are", "contact")

        condNonNumSect("motion", "motionSensor", "Motion Conditions", "Motion Sensors", ["active", "inactive"], "are", "motion")

        condNonNumSect("presence", "presenceSensor", "Presence Conditions", "Presence Sensors", ["present", "not present"], "are", "presence")

        condNonNumSect("acceleration", "accelerationSensor", "Accelerometer Conditions", "Accelerometer Sensors", ["active","inactive"], "are", "acceleration")

        condNumValSect("battery", "battery", "Battery Level Conditions", "Batteries", "Level (%)", "battery")

        condNumValSect("level", "switchLevel", "Dimmers/Levels", "Dimmers/Levels", "Level (%)", "speed_knob")

        condNonNumSect("door", "garageDoorControl", "Garage Door Conditions", "Garage Doors", ["open", "closed"], "are", "garage_door")

        condNumValSect("illuminance", "illuminanceMeasurement", "Illuminance Conditions", "Illuminance Sensors", "Lux Level (%)", "illuminance")

        condNumValSect("power", "powerMeter", "Power Events", "Power Meters", "Power Level (W)", "power")

        condNumValSect("humidity", "relativeHumidityMeasurement", "Humidity Conditions", "Relative Humidity Sensors", "Relative Humidity (%)", "humidity")

        condNonNumSect("securityKeypad", "securityKeypad", "Security Keypad Conditions", "Security Kepads", ["disarmed", "armed home", "armed away"], "are", "keypad")

        condNonNumSect("lock", "lock", "Lock Conditions", "Smart Locks", ["locked", "unlocked"], "are", "lock")

        condNumValSect("temperature", "temperatureMeasurement", "Temperature Conditions", "Temperature Sensors", "Temperature", "temperature")

        condNonNumSect("water", "waterSensor", "Water Sensors", "Water Sensors", ["wet", "dry"], "are", "water")

        condNonNumSect("windowShade", "windowShade", "Window Shades", "Window Shades", ["open", "closed"], "are", "shade")

        condNonNumSect("valve", "valve", "Valves", "Valves", ["open", "closed"], "are", "valve")
    }
}

def condNonNumSect(String inType, String capType, String sectStr, String devTitle, cmdOpts, String cmdTitle, String image) {
    section(sectHead(sectStr) /*, hideWhenEmpty: true */) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required:false //, hideWhenEmpty: true
        if (settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle}...", sCOMMAND), options: cmdOpts, multiple: false, required: true, submitOnChange: true
            if ((String)settings."cond_${inType}_cmd" && ((List)settings."cond_${inType}")?.size() > 1) {
                input "cond_${inType}_all", sBOOL, title: inTS1("ALL ${devTitle} must be (${(String)settings."cond_${inType}_cmd"})?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
            input "cond_${inType}_db", sBOOL, title: inTS1("Deactivate Zone immediately when ${cmdTitle} condition no longer passes?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
        }
    }
}

@SuppressWarnings('unused')
def condNumValSect(String inType, String capType, String sectStr, String devTitle, String cmdTitle, String image, hideable= false) {
    section(sectHead(sectStr) /*, hideWhenEmpty: true */) {
        input "cond_${inType}", "capability.${capType}", title: inTS1(devTitle, image), multiple: true, submitOnChange: true, required: false //, hideWhenEmpty: true
        if(settings."cond_${inType}") {
            input "cond_${inType}_cmd", sENUM, title: inTS1("${cmdTitle} is...", sCOMMAND), options: [sBETWEEN, sBELOW, sABOVE, sEQUALS], required: true, multiple: false, submitOnChange: true
            if ((String)settings."cond_${inType}_cmd") {
                if ((String)settings."cond_${inType}_cmd" in [sBETWEEN, sBELOW]) {
                    input "cond_${inType}_low", sNUMBER, title: inTS1("a ${(String)settings."cond_${inType}_cmd" == sBETWEEN ? "Low " : sBLANK}${cmdTitle} of...", "low"), required: true, submitOnChange: true
                }
                if ((String)settings."cond_${inType}_cmd" in [sBETWEEN, sABOVE]) {
                    input "cond_${inType}_high", sNUMBER, title: inTS1("${(String)settings."cond_${inType}_cmd" == sBETWEEN ? "and a high " : "a "}${cmdTitle} of...", "high"), required: true, submitOnChange: true
                }
                if ((String)settings."cond_${inType}_cmd" == sEQUALS) {
                    input "cond_${inType}_equal", sNUMBER, title: inTS1("a ${cmdTitle} of...", "equal"), required: true, submitOnChange: true
                }
                if (settings."cond_${inType}"?.size() > 1) {
                    input "cond_${inType}_all", sBOOL, title: inTS1("Require ALL devices to be (${(String)settings."cond_${inType}_cmd"}) values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                    if(!(Boolean)settings."cond_${inType}_all") {
                        input "cond_${inType}_avg", sBOOL, title: inTS1("Use the average of all selected device values?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
                    }
                }
                input "cond_${inType}_db", sBOOL, title: inTS1("Deactivate Zone immediately when ${cmdTitle} condition no longer passes?", sCHKBOX), required: false, defaultValue: false, submitOnChange: true
            }
        }
    }
}

def condTimePage() {
    return dynamicPage(name:"condTimePage", title: sBLANK, install: false, uninstall: false) {
        Boolean timeReq = (settings["cond_time_start"] || settings["cond_time_stop"])
        section(sectHead("Start Time:")) {
            input "cond_time_start_type", sENUM, title: inTS1("Starting at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(cond_time_start_type  == sTIME) {
                input "cond_time_start", sTIME, title: inTS1("Start time", "start_time"), required: timeReq, submitOnChange: true
            } else if(cond_time_start_type in lSUNRISESET) {
                input "cond_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
        section(sectHead("Stop Time:")) {
            input "cond_time_stop_type", sENUM, title: inTS1("Stopping at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(cond_time_stop_type == sTIME) {
                input "cond_time_stop", sTIME, title: inTS1("Stop time", "start_time"), required: timeReq, submitOnChange: true
            } else if(cond_time_stop_type in lSUNRISESET) {
                input "cond_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
    }
}

def zoneNotifPage() {
    return dynamicPage(name: "zoneNotifPage", title: "Zone Notifications", install: false, uninstall: false) {
        String a = getAppNotifDesc()
        if(!a) a = "Notifications not enabled"
        section() { paragraph divSm(a, sCLR4D9) }

        section (sectHead("Notification Devices:")) {
            input "notif_devs", "capability.notification", title: inTS1("Send to Notification devices?", "notification"), description: ((!settings?.notif_devs) ? inactFoot(sTTC) : sBLANK), required: false, multiple: true, submitOnChange: true
        }
        section (sectHead("Alexa Mobile Notification:")) {
            paragraph spanSmBldBr("Description:", sCLRGRY) + spanSmBld("This will send a push notification the Alexa Mobile app.", sCLRGRY)
            input "notif_alexa_mobile", sBOOL, title: inTS1("Send message to Alexa App?", "notification"), required: false, defaultValue: false, submitOnChange: true
        }

        Boolean active = ((List)settings.notif_devs || (Boolean)settings.notif_alexa_mobile)
        if(active) {
            section (sectHead("Message Customization:")) {
                paragraph spanSm("Configure either an Active or Inactive message to complete notification configuration.", sCLR4D9)
                Boolean req = !((String)settings.notif_active_message || (String)settings.notif_inactive_message)
                input "notif_active_message", sTEXT, title: inTS1("Active Message?", sTEXT), required: req, submitOnChange: true
                input "notif_inactive_message", sTEXT, title: inTS1("Inactive Message?", sTEXT), required: req, submitOnChange: true
            }
        } else {
            List<String> sets = settings.findAll { it.key.startsWith("notif_") }?.collect { (String)it.key }
            sets?.each { String sI-> settingRemove(sI) }
        }

        if(isZoneNotifConfigured()) {
            section(sectHead("Notification Restrictions:")) {
                String nsd = getNotifSchedDesc()
                href "zoneNotifTimePage", title: inTS1("Quiet Restrictions", "restriction"), description: (nsd ? divSm(spanSmBr(nsd) + inputFooter(sTTM), sCLR4D9) : inactFoot(sTTC))
            }
            if(!(Boolean)state.notif_message_tested) {
                List actDevices = (Boolean)settings.notif_alexa_mobile ? parent?.getDevicesFromList(settings.zone_EchoDevices) : []
                def aMsgDev = actDevices?.size() && (Boolean)settings.notif_alexa_mobile ? actDevices[0] : null
                if(sendNotifMsg("Info", "Zone Notification Test Successful. Notifications Enabled for ${app?.getLabel()}", aMsgDev, true)) { state.notif_message_tested = true }
            }
        } else { state.remove("notif_message_tested") }
    }
}

def zoneNotifTimePage() {
    return dynamicPage(name:"zoneNotifTimePage", title: sBLANK, install: false, uninstall: false) {
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
            input "${pre}_time_start_type", sENUM, title: inTS1("Starting at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings."${pre}_time_start_type" == sTIME) {
                input "${pre}_time_start", sTIME, title: inTS1("Start time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_start_type" in lSUNRISESET) {
                input "${pre}_time_start_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
            }
        }
        section(sectHead("Quiet Stop Time:")) {
            input "${pre}_time_stop_type", sENUM, title: inTS1("Stopping at...", "start_time"), options: [(sTIME):"Time of Day", "sunrise":"Sunrise", "sunset":"Sunset"], required: false , submitOnChange: true
            if(settings."${pre}_time_stop_type" == sTIME) {
                input "${pre}_time_stop", sTIME, title: inTS1("Stop time", "start_time"), required: timeReq, submitOnChange: true
            } else if(settings."${pre}_time_stop_type" in lSUNRISESET) {
                input "${pre}_time_stop_offset", sNUMBER, range: "*..*", title: inTS1("Offset in minutes (+/-)", "start_time"), required: false, submitOnChange: true
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


//********************************************
//              CORE APP METHODS
//********************************************

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

@Field static final String dupMSGFLD = "This zone is duplicated and has not had configuration completed... Please open zone and configure to complete setup..."

def updated() {
    logInfo("Updated Event Received...")
    Boolean maybeDup = app?.getLabel()?.toString()?.contains(" (Dup)")
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
    unsubscribe()
    unschedule()
    state.isInstalled = true
    updAppLabel()
    if(advLogsActive()) { logsEnabled() }
    runIn(3, "zoneCleanup")
    if(!isPaused(true)) {
        runIn(7, "subscribeToEvts")
        runEvery1Hour("healthCheck")
        updConfigStatusMap()
    }

    handleZoneDevice()
    checkZoneStatus([name: "initialize", displayName: "initialize"])
}

void handleZoneDevice() {
    String dni = [app?.id, "echoSpeaks-Zone-Dev"].join("|")
    def childDevice = getChildDevice(dni)
    String devLabel = "EchoZone - ${app.getLabel().replace(" (Z)", sBLANK)}"
    String childHandlerName = "Echo Speaks Zone Device"
    if (!childDevice && (Boolean)settings.createZoneDevice) {
        // log.debug "childDevice not found | autoCreateDevices: ${settings.autoCreateDevices}"
        try{
            logInfo("Creating NEW Echo Speaks Zone Device!!! | Device Label: ($devLabel)")
            childDevice = addChildDevice("tonesto7", childHandlerName, dni, null, [name: childHandlerName, label: devLabel, settings:[isZone: true], completedSetup: true])
        } catch(ex) {
            logError("AddDevice Error! | ${ex}", false, ex)
        }
    }
    if (childDevice && !(Boolean)settings.createZoneDevice) {
        try {
            deleteChildDevice(childDevice.deviceNetworkId)
        } catch(e) {
            logError("RemoveDevice Error! } ${e}", false, e)
        }
    }
}

List getEzDevice() {
    return ((List)getChildDevices())?.findAll { (Boolean)it?.isZone() }
}

void updateChildZoneState(Boolean zoneActive, Boolean active) {
    if(zoneActive != null) getEzDevice()?.each { it?.updStatus(zoneActive) }
    if(active != null) getEzDevice()?.each { it?.updSocketStatus(active) }
}

/*******************************************************************
            To Device from parent
*******************************************************************/
@SuppressWarnings('unused')
String relayDevVersion() {
    String a
    a = sNULL
    getEzDevice().each { a = it.devVersion() }
    return a
}

@SuppressWarnings('unused')
void relayUpdChildSocketStatus(Boolean active) {
     updateChildZoneState(null, active)
}

@SuppressWarnings('unused')
void relayUpdateCookies(Map cookies, Boolean doInit){
    getEzDevice().each { it.updateCookies(cookies, doInit) }
}

@SuppressWarnings('unused')
void relayRemoveCookies(Boolean isParent) {
    getEzDevice().each { it.removeCookies(isParent) }
}

@SuppressWarnings('unused')
void relayEnableDebugLog() { getEzDevice().each { it.enableDebugLog() } }
@SuppressWarnings('unused')
void relayDisableDebugLog() { getEzDevice().each { it.disableDebugLog() } }
@SuppressWarnings('unused')
void relayEnableTraceLog() { getEzDevice().each { it.enableTraceLog() } }
@SuppressWarnings('unused')
void relayDisableTraceLog() { getEzDevice().each { it.disableTraceLog() } }
@SuppressWarnings('unused')
void relayLogsOff() { getEzDevice().each { it.logsOff() } }
@SuppressWarnings('unused')
Map relayGetLogHistory() { Map a; a=null; getEzDevice().each { a = it.getLogHistory() }; return a ?: [:] }
@SuppressWarnings('unused')
void relayClearLogHistory() { getEzDevice().each { it.clearLogHistory() } }
@SuppressWarnings('unused')
void relayFinishAnnouncement(String msg, LinkedHashMap vmap) { getEzDevice().each { it.finishAnnounce(msg, vmap.vol, vmap.restvol) } }
@SuppressWarnings('unused')
void relayFinishSpeak(Map resp, Integer statucode, Map data) { getEzDevice().each { it.finishSendSpeakZ(resp, statuscode, data) } }
@SuppressWarnings('unused')
void relaySetOnline(Boolean onl) { getEzDevice().each { it.setOnlineStatus(onl) } }


/*******************************************************************
            To Parent from Device Command FUNCTIONS
*******************************************************************/
Boolean zoneOnline() {
    if(isPaused(true) || !isActive()) {
        logTrace("zone offline or inactive; relay failed")
        return false
    }
    return true
}

Map relayMinVersions() {
    parent.minVersions()
}

@SuppressWarnings('unused')
void relayMultiSeqCommand(List<Map> commands, String srcDesc, Boolean parallel=false, Map cmdMap=[:], String device=sNULL, String callback=sNULL) {
    if(zoneOnline()) parent.queueMultiSequenceCommand(commands, srcDesc, parallel, cmdMap, device, callback)
}

@SuppressWarnings('unused')
void relaySeqCommand(String type, String command, value=null,  Map deviceData=[:], String device=sNULL, String callback=sNULL) {
    if(zoneOnline()) parent.queueSequenceCommand(type, command, value, deviceData, device, callback)
}

@SuppressWarnings('unused')
void relayAnnounceZone(String zoneId, String msg, Boolean parallel, Boolean ignoreDoNotDisturb) {
    if(zoneOnline()) parent.sendZoneAnnounce(zoneId, msg, parallel, ignoreDoNotDisturb)
}

@SuppressWarnings('unused')
void relaySpeakZone(String zoneId, String msg, Boolean parallel, Boolean ignoreDoNotDisturb) {
    if(zoneOnline()) parent.sendZoneSpeak(zoneId, msg, parallel, ignoreDoNotDisturb)
}

@SuppressWarnings('unused')
void relayNopCommand() {
    if(zoneOnline()) parent.queueNopCommand()
}

@SuppressWarnings('unused')
private Boolean relayGetWWebSocketStatus() {
     parent.getWWebSocketStatus()
}

@SuppressWarnings('unused')
void relayChildInitiatedRefresh() {
     parent.childInitiatedRefresh()
}

String relayGetAlexaGuardStatus() {
    parent.getAlexaGuardStatus()
}
/*
Boolean RelayGetDndEnabled(String serial) {
    parent.getDndEnabled(serial)
} */

@SuppressWarnings('unused')
private void processDuplication() {
    String al = (String)app?.getLabel()
    String newLbl = "${al}${al?.contains(" (Dup)") ? sBLANK : " (Dup)"}"
    app?.updateLabel(newLbl)
    state.dupPendingSetup = true

    String dupSrcId = settings.duplicateSrcId ? (String)settings.duplicateSrcId : sNULL
    Map dupData = parent?.getChildDupeData("zones", dupSrcId)
    log.debug "dupData: ${dupData}"
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

    parent.childAppDuplicationFinished("zones", dupSrcId)
    sendZoneStatus()
    subscribe(location, "es3ZoneRefresh", zoneRefreshHandler)
    logInfo("Duplicated Zone has been created... Please open zone and configure to complete setup...")
}

def uninstalled() {
    sendZoneRemoved()
}

String getZoneName() { return (String)settings.appLbl }

private void updAppLabel() {
    String newLbl = "${(String)settings.appLbl} (Z${isPaused(true) ? " ${sPAUSESymFLD}" : sBLANK})".replaceAll(/ (Dup)/, sBLANK).replaceAll("\\s"," ")
    if((String)settings.appLbl && (String)app?.getLabel() != newLbl) { app?.updateLabel(newLbl); sendZoneStatus() }
}

private void updConfigStatusMap() {
    Map sMap = (Map)state.configStatusMap
    sMap = sMap ?: [:]
    sMap.conditions = conditionsConfigured()
    sMap.devices = devicesConfigured()
    state.configStatusMap = sMap
}

Boolean devicesConfigured() { return (settings.zone_EchoDevices?.size() > 0) }

//private Boolean getConfStatusItem(String item) { Map sMap = state.configStatusMap; return (sMap?.containsKey(item) && sMap[item] == true) }

@SuppressWarnings('unused')
private void zoneCleanup() {
    // State Cleanup
    List<String> items = []
    items.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
    //Cleans up unused Zone setting items
    List<String> setItems = ["zone_delay"]
    setItems.each { String sI-> if(settings.containsKey(sI)) { settingRemove(sI) } }
}

public void triggerInitialize() { runIn(3, "initialize") }

public void updatePauseState(Boolean pause) {
    if((Boolean)settings.zonePause != pause) {
        logDebug("Received Request to Update Pause State to (${pause})")
        settingUpdate("zonePause", "${pause}", sBOOL)
        runIn(4, "updated")
    }
}

@SuppressWarnings('unused')
private healthCheck() {
    // logTrace("healthCheck", true)
    checkZoneStatus([name: "healthCheck", displayName: "healthCheck"])
    if(advLogsActive()) { logsDisable() }
}

//private condItemSet(String key) { return (settings.containsKey("cond_${key}") && settings["cond_${key}"]) }

@SuppressWarnings('GroovyVariableNotAssigned')
void scheduleCondition() {
    if(isPaused(true)) { logWarn("Zone is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    Boolean tC = timeCondConfigured()
    Boolean dC = dateCondConfigured()
    if (tC || dC) {
        String msg = 'scheduleCondition: '
        Date startTime
        Date stopTime
        if (tC) {
                Boolean timeOk = timeCondOk() // this updates state variables
                startTime = (String)state.startTime ? parseDate((String)state.startTime) : null
                stopTime = (String)state.stopTime ? parseDate((String)state.stopTime) : null
        }
        if(!startTime && !stopTime && dC){
                startTime = timeToday('00:00', mTZ())
                stopTime = timeTodayAfter('23:59', '00:00', mTZ())
        }
        if(startTime && stopTime){
            Long t = wnow()
            Long lstart = startTime.getTime()
            Long lstop = stopTime.getTime()

            Boolean not = lstart > lstop
            Boolean isStart = true
            Long nextEvtT = 0L
            if(!not) {
                if(t<lstart) {
                    nextEvtT= lstart - t
                } else if(t<lstop){
                        nextEvtT = lstop - t
                        isStart = false
                }
            } else {
                if(t<lstop){
                    nextEvtT = lstop - t
                    isStart = false
                } else if(t<lstart) {
                    nextEvtT= lstart - t
                }
            }

            String msg1 = " schedule Start: $lstart Stop: $lstop now: $t  not: $not  isStart: $isStart nextEvtT: $nextEvtT"
            if(nextEvtT > 0L) {
                Long tt = Math.round((nextEvtT)/1000.0D) + 1L
                tt=(tt<1L ? 1L:tt)
                if(isStart) runIn(tt, "zoneTimeStartCondHandler")
                else  runIn(tt, "zoneTimeStopCondHandler")
                Date ttt = isStart ? startTime: stopTime
                msg += "Setting Schedule for ${isStart ? "Start Condition" : "Stop Condition"} in $tt's at ${epochToTime(ttt)}"
                msg += msg1
            } else msg += "Nothing to"+msg1
        } else log.warn "Strange - no start ($startTime) or stop ($stopTime) time: $tC  date: $dC"
        logTrace(msg)
    }
}

void subscribeToEvts() {
//    state.handleGuardEvents = false
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No events will be monitored.", true); return }
    if(isPaused(true)) { logWarn("Zone is PAUSED... No Events will be subscribed to or scheduled....", true); return }
    List attItems = [sMODE, "alarm"] + lDATTSTR + lDATTNUM

    //SCHEDULING
    if(timeCondConfigured() || dateCondConfigured()) {
        scheduleCondition()
        schedule('33 0 0 1/1 * ? * ', scheduleCondition)  // run at 00:00:33 every day
// in case we have been stopped a while...
        Map condStat = conditionStatus()
        if((Boolean)condStat.ok) zoneTimeStartCondHandler()
        else zoneTimeStopCondHandler()
    }

    attItems.each { String si->
        if((List)settings."cond_${si}") {
            switch(si) {
                case "alarm":
                    subscribe(location, "hsmStatus", zoneEvtHandler)
                    break
                case sMODE:
                    if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
                    subscribe(location, si, zoneEvtHandler)
                    break
                default:
                    subscribe((List)settings."cond_${si}", si, zoneEvtHandler)
                    break
            }
        }
    }
    // Subscribes to Zone Location Command from Other Echo Speaks apps.
    subscribe(location, "es3ZoneCmd", zoneCmdHandler)
    subscribe(location, "es3ZoneRefresh", zoneRefreshHandler)
    subscribe(location, "systemStart", zoneStartHandler)
}

/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
// Boolean reqAllCond() { Boolean a = multipleConditions(); return (!a || (a && (Boolean)settings.cond_require_all) ); }
Boolean reqAllCond() {
    Boolean mult = multipleConditions()
    return ( mult && (Boolean)settings.cond_require_all )
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
            Boolean isBtwn = timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, mTZ())
            isBtwn = not ? !isBtwn : isBtwn
            state.startTime =  formatDt(startTime)
            state.stopTime =  formatDt(stopTime)
            logTrace("timeCondOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not? startTime:stopTime})")
            return isBtwn
        }
    }
    logTrace("timeCondOk | (null)")
    state.startTime = sNULL
    state.stopTime = sNULL
    return null
}

@SuppressWarnings('GroovyVariableNotAssigned')
Boolean dateCondOk() {
    Boolean result = null
    Boolean dOk
    Boolean mOk
    if(((List)settings.cond_days || (List)settings.cond_months) ) {
        Boolean reqAll = reqAllCond()
        dOk = (List)settings.cond_days ? (isDayOfWeek((List)settings.cond_days)) : reqAll // true
        mOk = (List)settings.cond_months ? (isMonthOfYear((List)settings.cond_months)) : reqAll //true
        result = reqAll ? (mOk && dOk) : (mOk || dOk)
    }
    logTrace("dateCondOk | $result | monthOk: $mOk | daysOk: $dOk")
    return result
}

@SuppressWarnings('GroovyVariableNotAssigned')
Boolean locationCondOk() {
    Boolean result = null
    Boolean mOk
    Boolean aOk
    if((List)settings.cond_mode || (String)settings.cond_mode_cmd || (List)settings.cond_alarm) {
        Boolean reqAll = reqAllCond()
        mOk = ((List)settings.cond_mode /*&& (String)settings.cond_mode_cmd*/) ? (isInMode((List)settings.cond_mode, ((String)settings.cond_mode_cmd == "not"))) : reqAll //true
        aOk = (List)settings.cond_alarm ? isInAlarmMode((List)settings.cond_alarm) : reqAll //true
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
    String cmd = (String)settings."cond_${att}_cmd" ?: sNULL
    Double dcl = settings."cond_${att}_low" ?: null
    Double dch = settings."cond_${att}_high" ?: null
    Double dce = settings."cond_${att}_equal" ?: null
    Boolean dca = ((Boolean)settings."cond_${att}_all" == true) ?: false
    if( !(att && devs && cmd) ) { return true }

    switch(cmd) {
        case "equals":
            if(dce) {
                if(dca) { return allDevAttNumValsEqual(devs, att, dce) }
                else { return anyDevAttNumValEqual(devs, att, dce) }
            }
            return true
            break
        case "between":
            if(dcl && dch) {
                if(dca) { return allDevAttNumValsBetween(devs, att, dcl, dch) }
                else { return anyDevAttNumValBetween(devs, att, dcl, dch) }
            }
            return true
            break
        case "above":
            if(dch) {
                if(dca) { return allDevAttNumValsAbove(devs, att, dch) }
                else { return anyDevAttNumValAbove(devs, att, dch) }
            }
            return true
            break
        case "below":
            if(dcl) {
                if(dca) { return allDevAttNumValsBelow(devs, att, dcl) }
                else { return anyDevAttNumValBelow(devs, att, dcl) }
            }
            return true
            break
    }
    return false
}

@Field static List<String> lDATTSTR = ["switch", "motion", "presence", "contact", "acceleration", "lock", "securityKeypad", "windowShade", "door", "valve", "water" ]
@Field static List<String> lDATTNUM = ["battery", "temperature", "illuminance", "level", "power", "humidity"]

private Boolean isConditionOk(String att) {
    if(lDATTSTR.contains(att)) {
        if(!settings."cond_${att}") { return true }
        return checkDeviceCondOk(att)
    } else if(lDATTNUM.contains(att)) {
        if(!settings."cond_${att}") { return true }
        return checkDeviceNumCondOk(att)
    } else if (att == sMODE) {
        return ((List)settings.cond_mode /*&& (String)settings.cond_mode_cmd*/) ? (isInMode((List)settings.cond_mode, ((String)settings.cond_mode_cmd == "not"))) : true
    } else if (["hsmStatus", "alarmSystemStatus"].contains(att)) {
        return (List)settings.cond_alarm ? isInAlarmMode((List)settings.cond_alarm) : true
    } else {
        return true
    }
}

Boolean deviceCondOk() {
    List<String> skipped = []
    List<String> passed = []
    List<String> failed = []
    lDATTSTR.each { String i->
        if(!settings."cond_${i}") { skipped.push(i); return }
        checkDeviceCondOk(i) ? passed.push(i) : failed.push(i)
    }
    lDATTNUM.each { String i->
        if(!settings."cond_${i}") { skipped.push(i); return }
        checkDeviceNumCondOk(i) ? passed.push(i) : failed.push(i)
    }
    Integer cndSize = (passed.size() + failed.size())
    Boolean result = null
    if(cndSize != 0) result = reqAllCond() ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
    logTrace("DeviceCondOk | ${result} | Found: (${(passed?.size() + failed?.size())}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return result
}

Map conditionStatus() {
    Boolean reqAll = reqAllCond()
    List<String> failed = []
    List<String> passed = []
    List<String> skipped = []
    Boolean ok = true
    if((Boolean)state.dupPendingSetup) ok = false
    if(!settings.zone_EchoDevices) ok = false
    Integer cndSize=(Integer)null
    if(ok) {
        [sTIME, "date", "location", "device"].each { i->
            Boolean s = "${i}CondOk"()
            if(s == null) { skipped.push(i); return }
            s ? passed.push(i) : failed.push(i)
        }
        cndSize = passed.size() + failed.size()
        ok = reqAll ? (cndSize == passed.size()) : (cndSize > 0 && passed.size() >= 1)
        if(cndSize == 0) ok = true
    }
    logTrace("conditionStatus | ok: $ok | RequireAll: ${reqAll} | Found: (${cndSize}) | Skipped: $skipped | Passed: $passed | Failed: $failed")
    return [ok: ok, passed: passed, blocks: failed]
}

Boolean devCondConfigured(String att) {
    return ((List)settings."cond_${att}" && (String)settings."cond_${att}_cmd")
}

Boolean devNumCondConfigured(String att) {
    return ((String)settings."cond_${att}_cmd" && (settings."cond_${att}_low" || settings."cond_${att}_high" || settings."trig_${att}_equal"))
}

Boolean timeCondConfigured() {
    Boolean startTime = ((String)settings.cond_time_start_type in lSUNRISESET || ((String)settings.cond_time_start_type == sTIME && settings.cond_time_start))
    Boolean stopTime = ((String)settings.cond_time_stop_type in lSUNRISESET || ((String)settings.cond_time_stop_type == sTIME && settings.cond_time_stop))
    return (startTime && stopTime)
}

Boolean dateCondConfigured() {
    Boolean days = ((List)settings.cond_days)
    Boolean months = ((List)settings.cond_months)
    return (days || months)
}

Boolean locationCondConfigured() {
    if((List)settings.cond_mode && !(String)settings.cond_mode_cmd) { settingUpdate("cond_mode_cmd", "are", sENUM) }
    Boolean mode = ((List)settings.cond_mode && (String)settings.cond_mode_cmd)
    Boolean alarm = ((List)settings.cond_alarm)
    return (mode || alarm)
}

Boolean deviceCondConfigured() {
    return (deviceCondCount() > 0)
}

Integer deviceCondCount() {
    List<String> devConds = lDATTSTR + lDATTNUM
    List items = []
    devConds.each { String dc-> if(devCondConfigured(dc)) { items.push(dc) } }
    return items.size()
}

Boolean conditionsConfigured() {
    return (timeCondConfigured() || dateCondConfigured() || locationCondConfigured() || deviceCondConfigured())
}

Boolean multipleConditions() {
    Integer cnt = 0
    if(timeCondConfigured()) cnt++
    if(dateCondConfigured()) cnt++
    if(locationCondConfigured()) cnt++
    cnt = cnt + deviceCondCount()
    return (cnt>1)
}

/***********************************************************************************************************
    EVENT HANDLER FUNCTIONS
************************************************************************************************************/

def zoneEvtHandler(evt) {
    logTrace( "${(String)evt?.name} Event | Device: ${(String)evt?.displayName} | Value: (${strCapitalize(evt?.value?.toString())}) with a delay of ${wnow() - ((Date)evt?.date)?.getTime()}ms")
    checkZoneStatus(evt)
    scheduleCondition()
}

void zoneTimeStartCondHandler() {
    Map evt = [date: new Date(), name: "Time", displayName: "Condition Start Time", value: wnow()]
    zoneEvtHandler(evt)
}

void zoneTimeStopCondHandler() {
    Map evt = [date: new Date(), name: "Time", displayName: "Condition Stop Time", value: wnow()]
    zoneEvtHandler(evt)
}

def zoneStartHandler(evt) {
    zoneEvtHandler(evt)
// match states incase we were down
    sendZoneStatus()
}

private void addToZoneHistory(Map evt, Map condStatus, Integer max=10) {
    Boolean ssOk = true //(stateSizePerc() <= 70)
    List eData = getMemStoreItem(zoneHistFLD) ?: []
    eData.push([dt: getDtNow(), active: (condStatus.ok == true), evtName: evt.name, evtDevice: evt.displayName, blocks: condStatus.blocks, passed: condStatus.passed])
    Integer lsiz = eData.size()
    if(!ssOk || lsiz > max) { eData = eData.drop( (lsiz-max)+1 ) }
    updMemStoreItem(zoneHistFLD, eData)
}

void checkZoneStatus(evt) {
    Map condStatus = conditionStatus()
    Boolean active = (Boolean)condStatus.ok
    String delayType = active ? "active" : "inactive"
    String nm = (String)evt?.name
    String msg1 = " | Call from ${nm} / ${(String)evt?.displayName}".toString()
    if((Boolean)state.zoneConditionsOk == active) { logTrace("checkZoneStatus: Zone: ${delayType} | No changes${msg1}"); return }
    Boolean bypassDelay = false
    Map data = [active: active, recheck: false, evtData: [name: nm, displayName: (String)evt?.displayName], condStatus: condStatus]
    Integer delay = settings."zone_${delayType}_delay" ?: null
    if(!active && settings."cond_${nm}_db" == true) { bypassDelay = !isConditionOk(nm) }
    String msg = !bypassDelay && delay ? "in (${delay} sec)" : (bypassDelay ? "Bypassing Inactive Delay for (${nm}) Event..." : sBLANK)
    logTrace("calling updateZoneStatus [${delayType}] "+msg+msg1)
    if(!bypassDelay && delay) {
        runIn(delay, "updateZoneStatus", [data: data])
    } else {
        updateZoneStatus(data)
    }
}

public Boolean isZoneDeviceEnabled() {
    return (Boolean)settings.createZoneDevice
}

Map myZoneStatus() {
    Map zoneDevs = getZoneDevices()
    // log.debug "zoneDevs: $zoneDevs"
    return [name: (String)app?.getLabel(), active: isActive(), paused: isPaused(true), id: app?.getId(), createZoneDevice: isZoneDeviceEnabled(), zoneDevices: (List)zoneDevs.devIds ?: []]
}

void sendZoneStatus() {
    Map zs = myZoneStatus()
    sendLocationEvent(name: "es3ZoneState", value: app?.getId(), data: zs, isStateChange: true, display: false, displayed: false)
    Boolean onl = (Boolean)zs.active && !(Boolean)zs.paused
    relaySetOnline(onl)
}

void sendZoneRemoved() {
    sendLocationEvent(name: "es3ZoneRemoved", value: app?.getId(), data:[name: (String)app?.getLabel()], isStateChange: true, display: false, displayed: false)
}

void updateZoneStatus(Map data) {
    Boolean active = (Boolean)data.active
    Map condStatus = (Map)data.condStatus
    if((Boolean)data.recheck) {
        condStatus = conditionStatus()
        active = (Boolean)condStatus.ok
    }
    if(state.zoneConditionsOk != active) {
        logInfo("Setting Zone (${getZoneName()}) Status to (${active ? "Active" : "Inactive"})")
        state.zoneConditionsOk = active
        addToZoneHistory((Map)data.evtData, condStatus)
        sendZoneStatus()
        if(isZoneNotifConfigured()) {
            Boolean ok2Send = true
            String msgTxt = active ? ((String)settings.notif_active_message ?: sNULL) : ((String)settings.notif_inactive_message ?: sNULL)
            if(ok2Send && msgTxt) {
                Map zoneDevices = getZoneDevices()
                def alexaMsgDev = ((List)zoneDevices.devices)?.size() && (Boolean)settings.notif_alexa_mobile ? ((List)zoneDevices.devices)[0] : null
                if(sendNotifMsg(getZoneName(), msgTxt, alexaMsgDev, false)) { logTrace("Sent Zone Notification...") }
            }
        }
        if(active) {
            if(settings.zone_active_switches_off) settings.zone_active_switches_off*.off()
            if(settings.zone_active_switches_on) settings.zone_active_switches_on*.on()
        } else {
            if(settings.zone_inactive_switches_off) settings.zone_inactive_switches_off*.off()
            if(settings.zone_inactive_switches_on) settings.zone_inactive_switches_on*.on()
        }
    } else logDebug("no change to Zone (${getZoneName()}) Status (${active ? "Active" : "Inactive"})")
}

public getZoneHistory(Boolean asObj=false) {
    List<Map> zHist = (List)getMemStoreItem(zoneHistFLD) ?: []
    List<String> output = []
    if(zHist?.size()) {
        zHist.sort { a,b-> b.dt <=> a.dt }.each { h->
            List<Map> hList = []
            hList.push([name: "Trigger:", val: h?.evtName])
            hList.push([name: "Device:", val: h?.evtDevice])
            // hList.push([name: "Value:", val: h?.evtValue])
            hList.push([name: "Zone Status:", val: (h?.active ? "Activate" : "Deactivate")])
            hList.push([name: "Conditions Passed:", val: h?.passed])
            hList.push([name: "Conditions Blocks:", val: h?.blocks])
            hList.push([name: "DateTime:", val: h?.dt])
            if(hList.size()) {
                output.push(spanSm(kvListToHtmlTable(hList, sCLR4D9), sCLRGRY))
            }
        }
    } else { output.push("No History Items Found...") }
    if(!asObj) {
        output.each { i-> paragraph spanSm(i) }
    } else { return output }
}

private static String kvListToHtmlTable(List<Map> tabList, String color=sCLRGRY) {
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

Map getZoneDevices(String icmd=sNULL, Boolean ignoreDoNotDisturb=false) {
    // updDeviceInputs()
    String cmd=icmd
    List devObj = []
    List devIds = []
    if(cmd==sNULL) cmd='announce'
    List devices = parent?.getDevicesFromList(settings.zone_EchoDevices)
    //devices?.each { devObj?.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String]); devIds.push(it?.getId()); }
    devices?.each {
        Map devInfo = it?.getEchoDevInfo(cmd, ignoreDoNotDisturb)
        if(devInfo) {
            devObj?.push(devInfo)
            devIds.push(it?.getId())
        } else { 
            logDebug("Uh Oh. did not get devinfo") 
        }
        // devObj?.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String])
        // devIds.push(it?.getId())
    }
    return [devices: devices, devObj: devObj, devIds: devIds]//, jsonStr: new groovy.json.JsonOutput().toJson(devObj)]
}

public zoneRefreshHandler(evt) {
    String cmd = evt?.value
    // Map data = evt?.jsonData
    switch(cmd) {
        case "checkStatus":
            checkZoneStatus([name: "zoneRefresh", displayName: "zoneRefresh"])
            scheduleCondition()
            break
        case "sendStatus":
            sendZoneStatus()
            break
    }
}

/*
 * handle commands sent to a zone
 * caller could be actions (a broadcast location event) (actions calls parent for speak or announce to combine across zones)
 *   or a zone device handler (calling its parent)
 */

@SuppressWarnings('GroovyFallthrough')
public zoneCmdHandler(evt, Boolean chldDev=false, Boolean ignoreDoNotDisturb=false) {
    // log.warn "zoneCmdHandler $evt"
    String cmd = evt?.value
    Map data = evt?.jsonData
    String appId = app?.getId() as String

    // log.warn "zoneCmdHandler cmd: $cmd data: $data appId: $appId"
    if(cmd && data && appId && data.zones && data.zones.contains(appId) && data.cmd) { // is the broadcast for me?
        if(isPaused(true) || !isActive()) {
            logInfo( "zoneCmdHandler: zone is paused or inactive Skipping $evt")
            return
        }

        // log.trace "zoneCmdHandler | Cmd: $cmd | Data: $data"
        if(cmd == 'speak'/*  && zoneDevs?.size() >= 2 */ ) {
            Boolean fA = true
            if(settings.forceAnnounce != null) fA = (Boolean)settings.forceAnnounce
            if(fA) cmd = "announcement"
        }

        Map zoneDevMap = getZoneDevices(cmd == 'speak' ? 'TTS' : 'announce', ignoreDoNotDisturb)
        List zoneDevs = (List)zoneDevMap.devices

        if(data.zoneVolumes && data.zoneVolumes?.size() && data.zoneVolumes[appId]) {
            Map zVol = (Map)data.zoneVolumes[appId]
            // log.debug "zoneVolume: ${zVol}"
            data.changeVol = zVol.change ?: data.changeVol
            data.restoreVol = zVol.restore ?: data.restoreVol
        }
        Integer delay = (Integer)data.delay // ?: null

        // log.warn "zoneCmdHandler cmd: $cmd data: $data zoneDevMap: $zoneDevMap zoneDevs: $zoneDevs"
        if(cmd in [ 'speak', 'announcement', 'voicecmd', 'sequence'] && !data.message) { logWarning("Zone Command Message is missing", true); return }

        String tmsg = (String)data.message
        String msgp1 = " to Zone (${getZoneName()})"
        String msgv1 = "${data.changeVol!=null ? " | Volume: ${data.changeVol}" : sBLANK}${data.restoreVol!=null ? " | Restore Volume: ${data.restoreVol}" : sBLANK}"
        String msgd1 = "${delay ? " | Delay: (${delay})" : sBLANK}"
        Boolean doFin = false
        switch(cmd) {
            case "speak":
                logDebug("Sending Speak Command: (${tmsg})${msgp1}${msgv1}${msgd1}")
                if(data.changeVol!=null) {
                    zoneDevs?.each { dev->
                        dev?.setVolumeSpeakAndRestore(data.changeVol, tmsg, null)
                    }
                    /* call zone vdevice with finishSendSpeakZ */
                    if(!chldDev) relayFinishSpeak([:], 200, [message: tmsg, oldVolume: data.restoreVol, newVolume: data.changeVol])
                } else {
                    zoneDevs?.each { dev->
                        dev?.speak(tmsg)
                    }
                }
                if(data.restoreVol!=null) {
                    zoneDevs?.each { dev->
                        dev?.setLevel(data.restoreVol)
                    }
                }
                break
            case "announcement":
                if(zoneDevs?.size() > 0 && (List)zoneDevMap.devObj) {
                    logDebug("Sending Announcement Command: (${tmsg})${msgp1}${msgv1}${msgd1}")
                    List<String> valS = tmsg.contains("::") ? (List<String>)tmsg.split("::") : [sNULL, tmsg]
                    String mymsg = valS[1]
                    String mtitle = data.title ?: (valS[0] ?: getZoneName())
                    //NOTE: Only sends command to first device in the list | We send the list of devices to announce one and then Amazon does all the processing
                    zoneDevs[0]?.sendAnnouncementToDevices(mymsg, mtitle, (List)zoneDevMap.devObj, data.changeVol, data.restoreVol)
                    /* call zone vdevice with finishAnnounce */
                    if(!chldDev) relayFinishAnnouncement(mtitle+'::'+mymsg, [vol: (Integer)data.changeVol, restvol: (Integer)data.restoreVol])
                }
                break

            case "voicecmd":
                logDebug("Sending VoiceCmdAsText Command: (${tmsg})${msgp1}${msgd1}")
                zoneDevs?.each { dev->
                    dev?.voiceCmdAsText(tmsg)
                }
                break
            case "sequence":
                logDebug("Sending Sequence Command: (${tmsg})${msgp1}${msgd1}")
                zoneDevs?.each { dev->
                    dev?.executeSequenceCommand(tmsg)
                }
                break

            case "alarmvolume":
            case "volume":
                logDebug("Sending ${data.cmd?.capitalize()} Command${msgp1}${msgv1}${msgd1}")
                if(data.changeVol != null) {
                    zoneDevs?.each { dev->
                        dev?."${data.cmd}"(data.changeVol)
                    }
                    /* call zone vdevice with updateLevel */
                    if(!chldDev) getEzDevice().each { it.updateLevel(null, data.changeVol) }
                }
                break

            case "playback":
            case "dnd":
            case "mute":
            case "unmute":
                logDebug("Sending ${data.cmd?.capitalize()} Command${msgp1}${msgd1}")
                zoneDevs?.each { dev->
                    if(data.cmd) { dev?."${data.cmd}"() }
                }
                    /* if(!chldDev) todo need to call zone vdevice with mute/unmute updates */
                   // getEzDevice().each { it.updateCookies(cookies) }
                break

            case "builtin":
            case "calendar":
            case "weather":
                logDebug("Sending ${data.cmd?.capitalize()} Command${msgp1}${msgv1}${msgd1}")
                zoneDevs?.each { dev->
                    if(data.cmd) { dev?."${data.cmd}"(data.changeVol, data.restoreVol) }
                }
                doFin=true
                break
            case "sounds":
                logDebug("Sending ${data.cmd?.capitalize()} | Name: ${tmsg} Command${msgp1}${msgv1}${msgd1}")
                zoneDevs?.each { dev->
                    dev?."${data.cmd}"(tmsg, data.changeVol, data.restoreVol)
                }
                doFin=true
                break
            case "music":
                logDebug("Sending ${data.cmd?.capitalize()} Command${msgp1} | Provider: ${data.provider} | Search: ${data.search}${msgd1}${msgv1}")
                zoneDevs?.each { dev ->
                    dev?."${data.cmd}"(data.search, data.provider, data.changeVol, data.restoreVol)
                }
                doFin=true
                break
        }
        /* call zone vdevice with updateLevel */
        if(doFin && !chldDev) getEzDevice().each { it.updateLevel(data.restoreVol, data.changeVol) }
    }
}
/******************************************
|   Restriction validators
*******************************************/

@SuppressWarnings('GroovyFallthrough')
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
/*
Double getDevValueAvg(devs, attr) {
    List vals = devs?.findAll { it?.currentValue(attr)?.isNumber() }?.collect { it?.currentValue(attr).toDouble() }
    return vals?.size() ? (vals?.sum()/vals?.size())?.round(1) as Double : null
} */

String getCurrentMode() {
    return (String)location?.mode
}

List getLocationModes(Boolean sorted=false) {
    List modes = location?.modes*.name
    // log.debug "modes: ${modes}"
    return (sorted) ? modes?.sort() : modes
}

Boolean isInMode(List modes, Boolean not=false) {
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
/*
Boolean areAllDevsSame(List devs, String attr, val) {
    if(devs && attr) { return (devs?.findAll { it?.currentValue(attr) == val as String }?.size() == devs?.size()) }
    return false
} */

Boolean allDevAttValsEqual(List devs, String att, val) {
    if(devs && att) {
        if(val instanceof List) return (devs.findAll { it?.currentValue(att) in val }?.size() == devs.size())
        else return (devs.findAll { it?.currentValue(att) == val }?.size() == devs.size())
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
Boolean anyDevAttNumValBetween(List devs, String att, Double low, Double high) {
    return (devs && att && (low < high)) ? (devs?.findAll {
        Double t = it?.currentValue(att)?.toDouble()
        ( (t >= low) && (t <= high) ) }?.size() >= 1) : false
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
Boolean allDevAttNumValsBetween(List devs, String att, Double low, Double high) {
    return (devs && att && (low < high)) ? (devs?.findAll {
        Double t = it?.currentValue(att)?.toDouble()
        ( (t >= low) && (t <= high) ) }?.size() == devs?.size()) : false
}
Boolean allDevAttNumValsEqual(List devs, String att, Double val) {
    return (devs && att) ? (devs?.findAll { it?.currentValue(att)?.toDouble() == val }?.size() == devs?.size()) : false
}
/*
Boolean devAttValEqual(List devs, String devId, String att, val) {
    if(devs && att) { return (devs.find { it?.currentValue(att) == val }) }
    return false
} */

static String getAlarmSystemName(Boolean abbr=false) {
    return (abbr ? "HSM" : "Hubitat Safety Monitor")
}

public Map getZoneMetrics() {
    Map out = [:]
    out.version = appVersionFLD
    out.activeDelay = settings.zone_active_delay ?: 0
    out.inactiveDelay = settings.zone_inactive_delay ?: 0
    out.zoneDevices = settings.zone_EchoDevices ?: []
    out.activeSwitchesOnCnt = settings.zone_active_switches_on ?: []
    out.activeSwitchesOffCnt = settings.zone_active_switches_off ?: []
    out.inactiveSwitchesOnCnt = settings.zone_inactive_switches_on ?: []
    out.inactiveSwitchesOffCnt = settings.zone_inactive_switches_off ?: []
    return out
}

/******************************************
|    Time and Date Conversion Functions
*******************************************/
private static TimeZone mTZ(){ return TimeZone.getDefault() } // (TimeZone)location.timeZone

static String formatDt(Date dt, Boolean tzChg=true) {
    return dateTimeFmt(dt, "E MMM dd HH:mm:ss z yyyy", tzChg)
}

static String dateTimeFmt(Date dt, String fmt, Boolean tzChg=true) {
//    if(!(dt instanceof Date)) { try { dt = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", dt?.toString()) } catch(e) { dt = Date.parse("E MMM dd HH:mm:ss z yyyy", dt?.toString()) } }
    def tf = new SimpleDateFormat(fmt)
    if(tzChg && mTZ()) { tf.setTimeZone(mTZ()) }
    return (String)tf.format(dt)
}

static String convToTime(Date dt) {
    String newDt = dateTimeFmt(dt, "h:mm a")
    if(newDt?.contains(":00 ")) { newDt?.toString()?.replaceAll(":00 ", sSPACE) }
    return newDt
}

static Date parseDate(String dt) { return Date.parse("E MMM dd HH:mm:ss z yyyy", dt) }

//static Boolean isDateToday(Date dt) { return (dt && dt.clearTime().compareTo(new Date().clearTime()) >= 0) }
static Boolean isDateToday(Date dt) { return (dt && dt.clearTime() >= new Date().clearTime()) }

static String strCapitalize(String str) { return str ? str.toString().capitalize() : sNULL }

static String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }

static String getDtNow() {
    Date now = new Date()
    return formatDt(now)
}

static String epochToTime(Date dt) {
    return dateTimeFmt(dt, "h:mm a")
}

static String fmtTime(t, String fmt="h:mm a", Boolean altFmt=false) {
    if(!t) return sNULL
    Date dt = new Date().parse(altFmt ? "E MMM dd HH:mm:ss z yyyy" : "yyyy-MM-dd'T'HH:mm:ss.SSSZ", t.toString())
    return dateTimeFmt(dt, fmt)
}

Long GetTimeDiffSeconds(String lastDate, String sender=sNULL) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        Date lastDt = parseDate(lastDate) // Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        Long start = lastDt.getTime()
        Long stop = wnow()
        Long diff = Math.round((stop - start) / 1000L)
        return diff.abs()
    }
    catch (ex) {
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : sBLANK}lastDate: $lastDate): ${ex?.message}", false, ex)
        return 10000L
    }
}

static String getWeekDay() {
    def df = new SimpleDateFormat("EEEE")
    df.setTimeZone(mTZ())
    return (String)df.format(new Date())
}
/*
String getWeekMonth() {
    def df = new java.text.SimpleDateFormat("W")
    df.setTimeZone(location?.timeZone)
    return (String)df.format(new Date())
}

String getDay() {
    def df = new java.text.SimpleDateFormat("D")
    df.setTimeZone(location?.timeZone)
    return (String)df.format(new Date())
}

String getYear() {
    def df = new java.text.SimpleDateFormat("yyyy")
    df.setTimeZone(location?.timeZone)
    return (String)df.format(new Date())
}
*/
static String getMonth() {
    def df = new SimpleDateFormat("MMMMM")
    df.setTimeZone(mTZ())
    return (String)df.format(new Date())
}
/*
String getWeekYear() {
    def df = new java.text.SimpleDateFormat("w")
    df.setTimeZone(location?.timeZone)
    return (String)df.format(new Date())
} */
/*
Map getDateMap() {
    return [d: getWeekDay(), dm: getDay(), wm: getWeekMonth(), wy: getWeekYear(), m: getMonth(), y: getYear() ]
} */

static Boolean isDayOfWeek(List opts) {
    String day = getWeekDay()
    return ( opts?.contains(day) )
}

static Boolean isMonthOfYear(List opts) {
    String mon = getMonth()
    return ( opts?.contains(mon) )
}

Boolean isTimeOfDay(String startTime, String stopTime) {
    if(!startTime && !stopTime) { return true }
    Date st = toDateTime(startTime)
    Date et = toDateTime(stopTime)
    return timeOfDayIsBetween(st, et, new Date(), mTZ())
}

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

public void enableDebugLog() { settingUpdate("logDebug", sTRUE, sBOOL); logInfo("Debug Logs Enabled From Main App...") }
public void disableDebugLog() { settingUpdate("logDebug", sFALSE, sBOOL); logInfo("Debug Logs Disabled From Main App...") }
public void enableTraceLog() { settingUpdate("logTrace", sTRUE, sBOOL); logInfo("Trace Logs Enabled From Main App...") }
public void disableTraceLog() { settingUpdate("logTrace", sFALSE, sBOOL); logInfo("Trace Logs Disabled From Main App...") }

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
            List<String> aa = (List<String>)key
            aa.each { String k->
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

private void updAppFlag(String key, Boolean val) {
    Map data = atomicState?.appFlagsMap ?: [:]
    if(key) { data[key] = val }
    atomicState.appFlagsMap = data
}

@SuppressWarnings('unused')
private void remAppFlag(key) {
    Map data = atomicState?.appFlagsMap ?: [:]
    if(key) {
        if(key instanceof List) {
            List<String> aa = (List<String>)key
            aa.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
        } else { if(data.containsKey(key)) { data.remove(key) } }
        atomicState.appFlagsMap = data
    }
}

Boolean getAppFlag(String val) {
    Map aMap = atomicState?.appFlagsMap
    if(val && aMap && aMap[val]) { return (Boolean)aMap[val] }
    return false
}

@SuppressWarnings('unused')
private stateMapMigration() {
    //Timestamp State Migrations
    Map<String, String> tsItems = [:]
    tsItems?.each { k, v-> if(state.containsKey(k)) { updTsVal(v, (String)state[k]); state.remove(k) } }

    //App Flag Migrations
    Map<String,String> flagItems = [:]
    flagItems?.each { k, v-> if(state.containsKey(k)) { updAppFlag(v, (Boolean)state[k]); state.remove(k) } }
    updAppFlag("stateMapConverted", true)
}

/******************************************
|   App Input Description Functions
*******************************************/
/*
String unitStr(String att) {
    switch(att) {
        case "temp":
            return "\u00b0${getTemperatureScale() ?: "F"}"
        case "humidity":
        case "battery":
            return "%"
        default:
            return sBLANK
    }
} */

String getAppNotifDesc(Boolean hide=false) {
    String str = sBLANK
    if(isZoneNotifConfigured()) {
        Boolean ok = getOk2Notify()
        str += hide ? sBLANK : spanSmBr(strUnder("Send allowed: ") +  getOkOrNotSymHTML(ok))
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
    return allDays?.findAll { (!curDays?.contains(it)) }
}

@SuppressWarnings('GroovyVariableNotAssigned')
String getNotifSchedDesc(Boolean min=false) {
    String startType = settings.notif_time_start_type
    Date startTime
    String stopType = settings.notif_time_stop_type
    Date stopTime
    List dayInput = (List)settings.notif_days
    List modeInput = (List)settings.notif_modes
    String str = sBLANK

    if(startType && stopType) {
        startTime = startType == sTIME && settings.notif_time_start ? toDateTime(settings.notif_time_start) : null
        stopTime = stopType == sTIME && settings.notif_time_stop ? toDateTime(settings.notif_time_stop) : null
    }
    if(startType in lSUNRISESET || stopType in lSUNRISESET) {
        Map sun = getSunriseAndSunset()
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
    str += (startLbl && stopLbl) ? spanSmBr("     ${sBULLET} Restricted Times: ${startLbl} - ${stopLbl} " + getOkOrNotSymHTML(!timeOk)) : sBLANK
    List qDays = getQuietDays()
    str += dayInput && qDays ? spanSmBr("     ${sBULLET} Restricted Day${pluralizeStr(qDays, false)}: (${qDays?.join(", ")}) " + getOkOrNotSymHTML(!daysOk)) : sBLANK
    str += modeInput ? spanSm("     ${sBULLET} Allowed Mode${pluralizeStr(modeInput, false)}: (${modeInput?.join(", ")}) " + getOkOrNotSymHTML(!modesOk)) : sBLANK
    str = str ? spanSmBr("  ${sBULLET} Restrictions Active: " + getOkOrNotSymHTML(rest)) + spanSm(str) : sBLANK
    return (str != sBLANK) ? divSm(str, sCLR4D9) : sNULL
}

String getOverallDesc() {
    Map condStatus = conditionStatus()
    String str = spanSmBld("Zone is ") + spanSmBr( ((Boolean)condStatus.ok ? "Active " : "Inactive ") + getOkOrNotSymHTML((Boolean)condStatus.ok))
    return str
}

String getConditionsDesc(Boolean addFoot=true) {
    Boolean confd = conditionsConfigured()
    String sPre = "cond_"
    String str = sBLANK
    if(confd) {
        str = getOverallDesc()
        str += spanSmBr(" ${sBULLET} " + spanSmBld("${reqAllCond() ? "All Conditions Required" : "Any Condition Allowed"}"))
        if((Boolean)timeCondConfigured()) {
            str += spanSmBr(" ${sBULLET} Time Between Allowed: " + getOkOrNotSymHTML(timeCondOk()))
            str += spanSmBr("    - ${getTimeCondDesc(false)}")
        }
        if((Boolean)dateCondConfigured()) {
            str += spanSmBr(" ${sBULLET} Date:")
            str += (List)settings.cond_days    ? spanSmBr("    - Days Allowed: " + getOkOrNotSymHTML(isDayOfWeek((List)settings.cond_days))) : sBLANK
            str += (List)settings.cond_months  ? spanSmBr("    - Months Allowed: " + getOkOrNotSymHTML(isMonthOfYear((List)settings.cond_months)))  : sBLANK
        }
        if((List)settings.cond_alarm || (List)settings.cond_mode) {
            str += spanSmBr(" ${sBULLET} Location: " + getOkOrNotSymHTML(locationCondOk()))
            String a = location?.hsmStatus ?: "disarmed"
            str += (List)settings.cond_alarm ? spanSmBr("    - Alarm Mode ${a} in: ${(List)settings.cond_alarm} " + getOkOrNotSymHTML(isInAlarmMode((List)settings.cond_alarm))) : sBLANK
            Boolean not = ((String)settings.cond_mode_cmd == "not")
            str += settings.cond_mode ? spanSmBr("    - Mode ${getCurrentMode()} (${not ? "not in" : "in"}): ${(List)settings.cond_mode} " + getOkOrNotSymHTML(isInMode((List)settings.cond_mode, not))) : sBLANK
        }
         if(deviceCondConfigured()) {
             List<String> devConds = lDATTSTR + lDATTNUM
             devConds.each { String evt->
                if(devCondConfigured(evt)) {
                    Boolean condOk = false
                    if(evt in lDATTSTR) { condOk = checkDeviceCondOk(evt) }
                    else if(evt in lDATTNUM) { condOk = checkDeviceNumCondOk(evt) }

                    List devs = settings."${sPre}${evt}" ?: null
                    if(devs){
                        List myV = []
                        if(!addFoot) devs.each { dev -> myV.push(dev?.currentValue(evt)) }
                        str += spanSmBr(" ${sBULLET} ${evt?.capitalize()} (${settings."${sPre}${evt}"?.size()}) ${!addFoot ? myV : sBLANK} " + getOkOrNotSymHTML(condOk))
                    }

                    String a = "    - Desired Value: "
                    String cmd = (String)settings."${sPre}${evt}_cmd" ?: sNULL
                    if(cmd in [sBETWEEN, sBELOW, sABOVE, sEQUALS]) {
                        def cmdLow = settings."${sPre}${evt}_low" ?: null
                        def cmdHigh = settings."${sPre}${evt}_high" ?: null
                        def cmdEq = settings."${sPre}${evt}_equal" ?: null
                        str += (cmd == sEQUALS && cmdEq) ? spanSmBr(a+"( =${cmdEq}${attUnit(evt)})" + (settings."cond_${inType}_avg" ? "(Avg)" : sBLANK)) : sBLANK
                        str += (cmd == sBETWEEN && cmdLow && cmdHigh) ? spanSmBr(a+"(${cmdLow-cmdHigh}${attUnit(evt)})" + (settings."cond_${inType}_avg" ? "(Avg)" : sBLANK)) : sBLANK
                        str += (cmd == sABOVE && cmdHigh) ? spanSmBr(a+"( >${cmdHigh}${attUnit(evt)})" + (settings."cond_${inType}_avg" ? "(Avg)" : sBLANK)) : sBLANK
                        str += (cmd == sBELOW && cmdLow) ? spanSmBr(a+"( <${cmdLow}${attUnit(evt)})" + (settings."cond_${inType}_avg" ? "(Avg)" : sBLANK)) : sBLANK
                    } else {
                        str += cmd ? spanSmBr(a+"(${cmd})" + (settings."cond_${inType}_avg" ? "(Avg)" : sBLANK)) : sBLANK
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

String getZoneDesc() {
    if(devicesConfigured() && conditionsConfigured()) {
        List eDevs = parent?.getDevicesFromList(settings.zone_EchoDevices)?.collect { it?.displayName as String }
        String str = eDevs?.size() ? "Echo Devices in Zone:\n${eDevs?.join("\n")}\n" : sBLANK
        str += settings.zone_active_delay ? bulletItem(sBLANK, "Activate Delay: (${settings.zone_active_delay})\n)") : sBLANK
        str += settings.zone_inactive_delay ? bulletItem(sBLANK, "Deactivate Delay: (${settings.zone_inactive_delay})\n") : sBLANK
        str += "\n"+sTTM
        return str
    } else {
        return sTTC
    }
}

@SuppressWarnings('GroovyVariableNotAssigned')
String getTimeCondDesc(Boolean addPre=true) {
    Date startTime
    Date stopTime
    String startType = settings.cond_time_start_type
    String stopType = settings.cond_time_stop_type
    if(startType && stopType) {
        startTime = startType == 'time' && settings.cond_time_start ? toDateTime(settings.cond_time_start) : null
        stopTime = stopType == 'time' && settings.cond_time_stop ? toDateTime(settings.cond_time_stop) : null
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
            str += item ? (((cnt < 1) || (inpt.size() > 1)) ? "\n      ${item}" : "${addSpace ? "      " : sBLANK}${item}") : sBLANK
        }
    }
    //log.debug "str: $str"
    return str != sBLANK ? str : sNULL
}

static String randomString(Integer len) {
    def pool = ["a".."z",0..9].flatten()
    Random rand = new Random(wnow())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    // logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}

/*static def getRandomItem(List items) {
    List list = [] //new ArrayList<String>()
    items?.each { list.add(it) }
    return list?.get(new Random().nextInt(list?.size()))
} */

/***********************************************************************************************************
    HELPER FUNCTIONS
************************************************************************************************************/
void settingUpdate(String name, value, String type=sNULL) {
    if(name && type) { app?.updateSetting(name, [type: type, value: value]) }
    else if (name) { app?.updateSetting(name, value) }
}

void settingRemove(String name) {
    logTrace("settingRemove($name)...")
    if(name && settings.containsKey(name)) { app?.removeSetting(name) }
}

static List<String> weekDaysEnum() { return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"] }

static Map<String,String> daysOfWeekMap() { return ["MON":"Monday", "TUE":"Tuesday", "WED":"Wednesday", "THU":"Thursday", "FRI":"Friday", "SAT":"Saturday", "SUN":"Sunday"] }

static List<String> monthEnum() { return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"] }

static Map<String,String> monthMap() { return ["1":"January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"] }

static Map<String,String> getAlarmTrigOpts() {
    return ["armedAway":"Armed Away", "armingAway":"Arming Away Pending exit delay","armedHome":"Armed Home","armingHome":"Arming Home pending exit delay", "armedNight":"Armed Night", "armingNight":"Arming Night pending exit delay","disarmed":"Disarmed", "allDisarmed":"All Disarmed","alerts":"Alerts"]
}

Integer getLastNotifMsgSec() { return !state.lastNotifMsgDt ? 100000 : GetTimeDiffSeconds((String)state.lastNotifMsgDt, "getLastMsgSec").toInteger() }
Integer getLastChildInitRefreshSec() { return !state.lastChildInitRefreshDt ? 3600 : GetTimeDiffSeconds((String)state.lastChildInitRefreshDt, "getLastChildInitRefreshSec").toInteger() }

Boolean getOk2Notify() {
    Boolean smsOk = false // = (settings.notif_sms_numbers?.toString()?.length()>=10)
    Boolean pushOk = false // = settings.notif_send_push
    Boolean pushOver = false // = (settings.notif_pushover && settings.notif_pushover_devices)
    Boolean alexaMsg = (Boolean)settings.notif_alexa_mobile
    Boolean notifDevsOk = (((List)settings.notif_devs)?.size())
    Boolean daysOk = (List)settings.notif_days ? (isDayOfWeek((List)settings.notif_days)) : true
    Boolean timeOk = notifTimeOk()
    Boolean modesOk = (List)settings.notif_modes ? (isInMode((List)settings.notif_modes)) : true
    Boolean result
    result = (smsOk || pushOk || alexaMsg || notifDevsOk || pushOver)
    if(!(daysOk && modesOk && timeOk)) { result = false }
    logDebug("getOk2Notify() RESULT: $result | notifDevsOk: $notifDevsOk | smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver | alexaMsg: $alexaMsg || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
    return result
}

Boolean notifTimeOk() {
    Date startTime
    Date stopTime
    // these are quiet time start/stop
    String startType = settings.notif_time_start_type
    String stopType = settings.notif_time_stop_type
    if(startType && stopType) {
        startTime = startType == sTIME && settings.notif_time_start ? toDateTime(settings.notif_time_start) : null
        stopTime = stopType == sTIME && settings.notif_time_stop ? toDateTime(settings.notif_time_stop) : null
    } else { return true }

    Date now = new Date()
    if(startType in lSUNRISESET || stopType in lSUNRISESET) {
        Map sun = getSunriseAndSunset()
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
        Boolean isBtwn = !timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, mTZ())
        isBtwn = not ? !isBtwn : isBtwn
        logTrace("NotifTimeOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not ? startTime:stopTime})")
        return isBtwn
    } else { return true }
}

// Sends the notifications based on app settings
public Boolean sendNotifMsg(String msgTitle, String msg, alexaDev=null, Boolean showEvt=true) {
    logTrace("sendNotifMsg() | msgTitle: ${msgTitle}, msg: ${msg}, $alexaDev, showEvt: ${showEvt}")
    List sentSrc = [] // ["Push"]
    Boolean sent = false
    try {
        String newMsg = "${msgTitle}: ${msg}"
        String flatMsg = newMsg.toString().replaceAll("\n", sSPACE)
        if(!getOk2Notify()) {
            logInfo( "sendNotifMsg: Notification not configured or Message Skipped During Quiet Time ($flatMsg)")
            //if(showEvt) { sendNotificationEvent(newMsg) }
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
        logError("sendNotifMsg $sentstr Exception: ${ex}", false, ex)
    }
    return sent
}

Boolean isZoneNotifConfigured() {
    return ((String)settings.notif_active_message || (String)settings.notif_inactive_message) &&
        ((List)settings.notif_devs || (Boolean)settings.notif_alexa_mobile)

}

static Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }

Boolean minVersionFailed() {
    try {
        Integer minDevVer = (Integer)parent?.minVersions()["zoneApp"]
        return minDevVer != null && versionStr2Int(appVersionFLD) < minDevVer
    } catch (ignored) {
        return false
    }
}

// public String gitBranch() { 
//     if(gitBranchFLD == sNULL) { gitBranchFLD = (String) parent?.gitBranch() }
//     return (String)gitBranchFLD
// }

Boolean isActive() {
    Boolean st = (Boolean)state.zoneConditionsOk
    return st != null ? st : (Boolean)conditionStatus().ok
}

Boolean isPaused(Boolean chkAll = false) { return (Boolean)settings.zonePause && (chkAll ? !((Boolean)state.dupPendingSetup == true) : true) }

static String documentationUrl() { return "https://tonesto7.github.io/echo-speaks-docs" }
static String videoUrl() { return "https://www.youtube.com/watch?v=wQPPlTFaGb4&ab_channel=SimplySmart123%E2%9C%85" }

static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/${imgName}.png" }

static String getPublicImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" }

static String sTS(String t, String i = sNULL, Boolean bold=false) { return "<h3>${i ? "<img src='${i}' width='42'> " : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>" }

static String s3TS(String t, String st, String i = sNULL, String c=sCLR4D9) { return "<h3 style='color:${c};font-weight: bold;'>${i ? "<img src='${i}' width='42'> " : sBLANK} ${t?.replaceAll("\n", "<br>")}</h3>${st ? "${st}" : sBLANK}" }

static String pTS(String t, String i = sNULL, Boolean bold=true, String color=sNULL) { return "${color ? "<div style='color: $color;'>" : sBLANK}${bold ? "<b>" : sBLANK}${i ? "<img src='${i}' width='42'> " : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}${color ? "</div>" : sBLANK}" }

@SuppressWarnings('unused')
static String inTS1(String str, String img = sNULL, String clr=sNULL, Boolean und=true) { return spanSmBldUnd(str, clr, img) }
static String inTS(String str, String img = sNULL, String clr=sNULL, Boolean und=true) { return divSm(strUnder(str.replaceAll("\n", sSPACE).replaceAll("<br>", sSPACE), und), clr, img) }

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
static String bulletItem(String inStr, String strVal) { return "${inStr == sBLANK ? sBLANK : "\n"}"+sSPACE+sBULLET+sSPACE+strVal }
static String dashItem(String inStr, String strVal, Boolean newLine=false) { return "${(inStr == sBLANK && !newLine) ? sBLANK : "\n"} - "+strVal }

Integer stateSize() {
    String j = new JsonOutput().toJson((Map)state)
    return j.length()
}
Integer stateSizePerc() { return (Integer)(((stateSize() / 100000)*100).toDouble().round(0)) }

private addToLogHistory(String logKey, String data, Integer max=10) {
    Boolean ssOk = true //(stateSizePerc() > 70)
    List eData = getMemStoreItem(logKey)
    if(eData.find { it?.data == data }) { return }
    eData.push([dt: getDtNow(), data: data])
    Integer lsiz = eData.size()
    if(!ssOk || lsiz > max) { eData = eData?.drop( (lsiz-max) ) }
    updMemStoreItem(logKey, eData)
}

public Map getLogConfigs() {
    return [
        info: (Boolean) settings.logInfo,
        warn: (Boolean) settings.logWarn,
        error: (Boolean) settings.logError,
        debug: (Boolean) settings.logDebug,
        trace: (Boolean) settings.logTrace,
    ]
}

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
    return span("Zone (v" + appVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

@SuppressWarnings('unused')
private Map getLogHistory() {
    List warn = getMemStoreItem("warnHistory")
    List errs = getMemStoreItem("errorHistory")
    return [ warnings: []+warn, errors: []+errs ]
}

@SuppressWarnings('unused')
private void clearHistory()  { historyMapFLD = [:]; mb() }

// FIELD VARIABLE FUNCTIONS
private void updMemStoreItem(String key, List val) {
    String appId = app.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    memStore[key] = val
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD
    // log.debug("updMemStoreItem(${key}): ${memStore[key]}")
}

private List getMemStoreItem(String key){
    String appId = app.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    return (List)memStore[key] ?: []
}
// Memory Barrier
@Field static Semaphore theMBLockFLD=new Semaphore(0)

static void mb(/*String meth=sNULL*/){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
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
                    if(ek==sTIME) vv = dateTimeFmt((Date)toDateTime(vv), "HH:mm")
                    setObjs[fk] = [type: ek, value: vv]
                }
            }
        }
        ((Map<String,String>)typeObj.caps).each { ck, cv->
            settings.findAll { it.key.endsWith(ck) }?.each { String fk, fv->
                setObjs[fk] = [type: "capability", value: (fv instanceof List ? fv?.collect { it?.id?.toString() } : fv?.id?.toString ) ] } //.toString().toList()
        }
        ((Map<String, String>)typeObj.dev).each { dk, dv->
            settings.findAll { it.key.endsWith(dk) }?.each { String fk, fv->
                setObjs[fk] = [type: "device", value: (fv instanceof List ? fv.collect { it?.id?.toString() } : fv?.id?.toString() ) ] } //.toString().toList()
        }
    }
    Map data = [:]
    String newlbl = ((String)app?.getLabel())?.replace(" (Z ${sPAUSESymFLD})", sBLANK)
    data.label = newlbl?.replace(" (Z)", sBLANK)
    data.settings = setObjs

    List stskip = [
        // "isInstalled", "isParent",
        "lastNotifMsgDt", "lastNotificationMsg", "setupComplete", "valEvtHistory", "warnHistory", "errorHistory",
        "appData", "actionHistory", "authValidHistory", "deviceRefreshInProgress", "noticeData", "installData", "herokuName", "zoneHistory",
        // actions
        "tierSchedActive",
        // zones
        "zoneConditionsOk", "configStatusMap", "tsDtMap", "dateInstalled", "handleGuardEvents", "startTime", "stopTime", "alexaGuardState", "appFlagsMap",
        "dupPendingSetup", "dupOpenedByUser"
    ]
    data.state = state?.findAll { !((String)it?.key in stskip) }
    return data
}

private Long wnow(){ return (Long)now() }
