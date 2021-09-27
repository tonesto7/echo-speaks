/**
 *  Echo Speaks App (Hubitat)
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
 */
//file:noinspection GroovyUnusedAssignment
//file:noinspection unused
//file:noinspection GroovySillyAssignment
//file:noinspection GrMethodMayBeStatic


import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.Field

import java.text.SimpleDateFormat
import java.util.concurrent.Semaphore

//************************************************
//*               STATIC VARIABLES               *
//************************************************
@Field static final String appVersionFLD  = '4.2.0.0'
@Field static final String appModifiedFLD = '2021-09-27'
@Field static final String gitBranchFLD   = 'master'
@Field static final String platformFLD    = 'Hubitat'
@Field static final Boolean devModeFLD    = false
@Field static final Map<String,Integer> minVersionsFLD = [echoDevice: 4200, wsDevice: 4200, actionApp: 4200, zoneApp: 4200, zoneEchoDevice: 4200, server: 270]  //These values define the minimum versions of code this app will work with.

@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sBULLET        = '\u2022'
@Field static final String sFRNFACE       = '\u2639'
@Field static final String okSymFLD       = "\u2713"
@Field static final String notOkSymFLD    = "\u2715"
@Field static final String sPAUSESymFLD   = "\u275A\u275A"
@Field static final String sLINEBR        = '<br>'
@Field static final String sFALSE         = 'false'
@Field static final String sTRUE          = 'true'
@Field static final String sBOOL          = 'bool'
@Field static final String sENUM          = 'enum'
@Field static final String sNUMBER        = 'number'
@Field static final String sTIME          = 'time'
@Field static final String sMODE          = 'mode'
@Field static final String sAPPJSON       = 'application/json'
@Field static final String sIN_IGNORE     = 'In Ignore Device Input'
@Field static final String sARM_AWAY      = 'ARMED_AWAY'
@Field static final String sARM_STAY      = 'ARMED_STAY'
@Field static final String sCOMPLT        = 'complete'
@Field static final String sMEDIUM        = 'medium'
@Field static final String sSMALL         = 'small'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sTTM           = 'Tap to modify...'
@Field static final String sTTC           = 'Tap to configure...'
@Field static final String sTTP           = 'Tap to proceed...'
@Field static final String sTTVD          = 'Tap to view details...'
@Field static final String sTTS           = 'Tap to select...'
@Field static final String sSETTINGS      = 'settings'
@Field static final String sUnknown       = 'Unknown'
@Field static final String sUNKNOWN       = 'unknown'
@Field static final String sRESET         = 'reset'
@Field static final String sHEROKU        = 'heroku'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sAMAZONORNG    = 'amazon_orange'
@Field static final String sDEVICES       = 'devices'
@Field static final String sSWITCH        = 'switch'
@Field static final String sASTR          = 'a'
@Field static final String sTSTR          = 't'
@Field static final List<String> lSUNRISESET   = ['sunrise', 'sunset']

//************************************************
//*          IN-MEMORY ONLY VARIABLES            *
//* (Cleared only on HUB REBOOT or CODE UPDATES) *
//************************************************
@Field volatile static Map<String, Map> historyMapFLD         = [:]
@Field volatile static Map<String, Map> cookieDataFLD         = [:]
@Field volatile static Map<String, Map> echoDeviceMapFLD      = [:]
@Field volatile static Map<String, Map> childDupMapFLD        = [:]
@Field volatile static Map<String,Map> workQMapFLD            = [:]
//@Field static Map<String,          Map> guardDataFLD        = [:]
@Field volatile static Map<String, Map> zoneStatusMapFLD      = [:]
@Field volatile static Map<String, Map> bluetoothDataFLD      = [:]
@Field volatile static Map<String, List> alexaRoutinesDataFLD = [:]
@Field volatile static Map<String, Map> dndDataFLD            = [:]
@Field volatile static Boolean guardArmPendingFLD             = false

definition(
    name                : "Echo Speaks",
    namespace           : "tonesto7",
    author              : "Anthony Santilli",
    description         : "Integrate your Amazon Echo devices into your Hubitat environment to create virtual Echo Devices. This allows you to speak text, make announcements, control media playback including volume, and many other Alexa features.",
    category            : "My Apps",
    iconUrl             : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.1x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    iconX2Url           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.2x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    iconX3Url           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.3x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    importUrl           : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/apps/echo-speaks.groovy",
    oauth               : true,
    singleInstance      : false,
    documentationLink   : documentationUrl(),
    videoLink           : videoUrl()
)

preferences {
    page(name: "startPage")
    page(name: "mainPage")
    page(name: "settingsPage")
    page(name: "devicePrefsPage")
    page(name: "deviceManagePage")
    page(name: "devCleanupPage")
    page(name: "newSetupPage")
    page(name: "authStatusPage")
    page(name: "actionsPage")
    page(name: "zonesPage")
    page(name: "devicePage")
    page(name: "deviceListPage")
    page(name: "unrecogDevicesPage")
    page(name: "changeLogPage")
    page(name: "notifPrefPage")
    page(name: "alexaGuardPage")
    page(name: "alexaGuardAutoPage")
    page(name: "servPrefPage")
    page(name: "musicSearchTestPage")
    page(name: "alexaRoutinesTestPage")
    page(name: "searchTuneInResultsPage")
    page(name: "deviceTestPage")
    page(name: "donationPage")
    page(name: "speechPage")
    page(name: "announcePage")
    page(name: "sequencePage")
    page(name: "viewZoneHistory")
    page(name: "viewActionHistory")
    page(name: "setNotificationTimePage")
    page(name: "actionDuplicationPage")
    page(name: "zoneDuplicationPage")
    page(name: "uninstallPage")
}

def startPage() {
    state.isParent = true
    checkVersionData(true)
    state.childInstallOkFlag = false
    if(!(Boolean)state.resumeConfig && (Boolean)state.isInstalled) { stateMigrationChk(); checkGuardSupport() }
    if((Boolean)state.resumeConfig || ((Boolean)state.isInstalled && !(Boolean)state.serviceConfigured)) { return servPrefPage() }
    else if(showChgLogOk()) { return changeLogPage() }
    else if(showDonationOk()) { return donationPage() }
    else { return mainPage() }
}

def mainPage() {
    Boolean tokenOk = getAccessToken()
    Boolean newInstall = !(Boolean)state.isInstalled

// force defaults
    if(settings.autoCreateDevices == null) settingUpdate("autoCreateDevices", sTRUE, sBOOL)
    if(settings.autoRenameDevices == null) settingUpdate("autoRenameDevices", sTRUE, sBOOL)
    if(settings.addEchoNamePrefix == null) settingUpdate("addEchoNamePrefix", sTRUE, sBOOL)
    if(settings.refreshCookieDays == null) settingUpdate("refreshCookieDays", 5, "number")
    if(settings.logInfo == null) settingUpdate("logInfo", sTRUE, sBOOL)
    if(settings.logWarn == null) settingUpdate("logWarn", sTRUE, sBOOL)
    if(settings.logError == null) settingUpdate("logError", sTRUE, sBOOL)
    if(settings.sendMissedPollMsg == null) {
        settingUpdate('sendMissedPollMsg', sTRUE, sBOOL)
        settingUpdate('misPollNotifyWaitVal', 2700)
        settingUpdate('misPollNotifyMsgWaitVal', 3600)
    }
    if(settings.sendCookieInvalidMsg == null) settingUpdate("sendCookieInvalidMsg", sTRUE, sBOOL)
    if(settings.sendAppUpdateMsg == null) settingUpdate("sendAppUpdateMsg", sTRUE, sBOOL)

    getEchoDevices(true)

    return dynamicPage(name: "mainPage", uninstall: false, install: true) {
        appInfoSect()
        if(!tokenOk) {
            section() { paragraph title: "Uh OH!!!", "Oauth Has NOT BEEN ENABLED. Please Remove this app and try again after it after enabling OAUTH" }; return
        }
        if(newInstall) {
            deviceDetectOpts()
        } else {
            section(sectHead("Alexa Guard:")) {
                if((Boolean) state.alexaGuardSupported) {
                    String gState = (String) state.alexaGuardState ? ((String) state.alexaGuardState == sARM_AWAY ? "Away" : "Home") : sUnknown
                    String gStateIcon = gState == sUnknown ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
                    String ad = spanSmBld("Alarm System Mode:", sCLR4D9) + spanSm(" (${gState})", (gState == sUnknown ? sCLRGRY : (gState == "Away" ? sCLRORG : sCLRGRN)))
                    ad += guardAutoConfigured() ? lineBr() + lineBr() + guardAutoDesc() : sBLANK
                    ad += inputFooter(sTTM)
                    href "alexaGuardPage", title: inTS1("Alexa Guard Control", gStateIcon), description: ad
                } else { paragraph divSm("Alexa Guard is not enabled or supported by any of your Echo Devices", sCLRGRY) }
            }

            section(sectHead("Alexa Devices:")) {
                if(!newInstall) {
                    List remDevs = getRemovableDevs()
                    if(remDevs?.size()) {
                        String rd = remDevs.sort().collect { spanSm(" ${sBULLET} ${it}") }.join("<br>")
                        href "devCleanupPage", title: inTS1("Removable Devices:"), description: divSm(rd, sCLRRED)
                    }
                    String devDesc = getDeviceList().collect { "${spanSm((String)it.value?.name)}${it.value?.online ? spanSm(" (Online)", sCLRGRN2) : sBLANK}${it.value?.supported == false ? spanSm(" ${sFRNFACE}", sCLRRED2) : sBLANK}" }.sort().join("<br>").toString()
                    String dd = devDesc ? divSm(devDesc, sCLR4D9) + inputFooter(sTTM) : inputFooter(sTTC, sCLRGRY)
                    href "deviceManagePage", title: inTS1("Manage Devices:", sDEVICES), description: dd
                } else { paragraph spanSmBld("Device Management will be displayed after install is complete", sCLRORG) }
            }

            section(sectHead("Companion Apps:")) {
                List zones = getZoneApps()
                List acts = getActionApps()
                href "zonesPage", title: inTS1("Manage Zones${zones?.size() ? " (${zones?.size()} ${zones?.size() > 1 ? "Zones" : "Zone"})" : sBLANK}", "es_groups"), description: getZoneDesc()
                href "actionsPage", title: inTS1("Manage Actions${acts?.size() ? " (${acts?.size()} ${acts?.size() > 1 ? "Actions" : "Action"})" : sBLANK}", "es_actions"), description: getActionsDesc()
            }

            section(sectHead("Alexa Login Service:")) {
                String ls = getLoginStatusDesc()
                href "authStatusPage", title: inTS1("Login Status | Cookie Service Management", sSETTINGS), description: (ls ? "${ls}${inputFooter(sTTM)}" : inputFooter(sTTC, sNULL, true))
            }
            if(!(Boolean)state.shownDevSharePage) { showDevSharePrefs() }
        }
        section(sectHead("Notifications:")) {
            String t0 = getAppNotifConfDesc()
            href "notifPrefPage", title: inTS1("Manage Notifications", "notification2"), description: (t0 ? "${t0}${inputFooter(sTTM)}" : inputFooter(sTTC, sNULL, true))
        }
        section(sectHead("Documentation & Settings:")) {
            href url: documentationUrl(), style: sEXTNRL, required: false, title: inTS1("View Documentation", "documentation"), description: inputFooter(sTTP, sCLRGRY, true)
            href "settingsPage", title: inTS1("Manage Logging, and Metrics", sSETTINGS), description: inputFooter(sTTM, sCLRGRY, true)
            href "changeLogPage", title: inTS1("View Change Logs", "change_log"), description: inputFooter(sTTVD, sCLRGRY, true)
        }

        if(!newInstall) {
            section(sectHead("Experimental Functions")) {
                href "deviceTestPage", title: inTS1("Device Testing", "testing"), description: spanSm("Test Speech, Announcements, and Sequences Builder", sCLRGRY) + inputFooter(sTTP, sCLRGRY)
                href "alexaRoutinesTestPage", title: inTS1("Alexa Routine Testing", "routine"), description: spanSm("View Routine Info and Test", sCLRGRY) + inputFooter(sTTP, sCLRGRY)
                href "musicSearchTestPage", title: inTS1("Music Search Tests", "music"), description: spanSm("Test music queries", sCLRGRY) + inputFooter(sTTP, sCLRGRY)
            }
            section(sectHead("Donations:")) {
                href url: textDonateUrl(), style: sEXTNRL, required: false, title: inTS1("Donations", "donate"), description: inputFooter("Tap to open browser", sCLRGRY, true)
            }
            section(sectHead("Remove Everything:")) {
                href "uninstallPage", title: inTS1("Uninstall this App", "uninstall"), description: inputFooter("Tap to Remove...", sCLRGRY, true)
            }
            section(sectHead("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                String issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28BUG%29+&projects=echo-speaks%2F6"
                String featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BFeature+Request%5D&projects=echo-speaks%2F6"
                href url: featUrl, style: sEXTNRL, required: false, title: inTS1("New Feature Request", "www"), description: inputFooter("Tap to open browser", sCLRGRY, true)
                href url: issueUrl, style: sEXTNRL, required: false, title: inTS1("Report an Issue", "www"), description: inputFooter("Tap to open browser", sCLRGRY, true)
            }
        } else {
            showDevSharePrefs()
            section(sectHead("Important Step:")) {
                paragraph spanSmBldBr("Notice", sCLRRED) + spanSmBr("Please complete the install (hit done below) and then return to the Echo Speaks App to resume deployment and configuration of the server.", sCLRRED)
                state.resumeConfig = true
            }
        }
        appFooter()
        state.ok2InstallActionFlag = false
        clearDuplicationItems()
    }
}

def authStatusPage() {
    return dynamicPage(name: "authStatusPage", install: false, nextPage: "mainPage", uninstall: false) {
        if((Boolean)state.authValid) {
            Integer lastChkSec = getLastTsValSecs("lastCookieRrshDt")
            Boolean pastDayChkOk = (lastChkSec > 86400)
            section(sectHead("Cookie Status:")) {
                Boolean cookieValid = validateCookie(true)
                Boolean chk1 = (state.cookieData && state.cookieData.localCookie)
                Boolean chk2 = (state.cookieData && state.cookieData.csrf  )
                Boolean chk3 = (lastChkSec < 432000)
                // Boolean chk4 = (cookieValid == true)
                // log.debug "cookieValid: ${cookieValid} | chk1: $chk1 | chk2: $chl2 | chk3: $chk3 | chk4: $chk4"
                String stat = spanSmBld("Auth Status:") + spanSmBr(getOkOrNotSymHTML(chk1 && chk2 && cookieValid))
                stat += spanSm(" ${sBULLET} Cookie:") + spanSmBr(getOkOrNotSymHTML(chk1))
                stat += spanSm(" ${sBULLET} CSRF Value:") + spanSmBr(getOkOrNotSymHTML(chk2))
                stat += lineBr()
                stat += spanSmBld("Cookie Refresh:") + spanSmBr(getOkOrNotSymHTML(chk3))
                stat += spanSm(" ${sBULLET} Last Refresh:") + spanSmBr(" (${seconds2Duration(getLastTsValSecs("lastCookieRrshDt"))})", (!chk3 ? sCLRRED : sNULL))
                stat += spanSm(" ${sBULLET} Next Refresh:") + spanSmBr(" (${nextCookieRefreshDur()})")
                paragraph divSm(stat, sCLR4D9)
            }

            section(sectHead("Cookie Tools: (Tap to show)", getAppImg("cookie")), hideable: true, hidden: true) {
                String ckDesc = pastDayChkOk ? "This will Refresh your Amazon Cookie." : "It's too soon to refresh your cookie.\nMinimum wait is 24 hours!!"
                input "refreshCookieDays", "number", title: inTS1("Auto refresh cookie every?\n(in days)", "day_calendar"), description: "in Days (1-5 max)", required: true, range: '1..5', defaultValue: 5, submitOnChange: true
                if(refreshCookieDays != null && refreshCookieDays < 1) { settingUpdate("refreshCookieDays", 1, "number") }
                if(refreshCookieDays != null && refreshCookieDays > 5) { settingUpdate("refreshCookieDays", 5, "number") }

                // Refreshes the cookie
                input "refreshCookie", sBOOL, title: inTS1("Manually refresh cookie?", sRESET), description: spanSm(ckDesc), required: true, defaultValue: false, submitOnChange: true
                paragraph pTS(ckDesc, sNULL, false, pastDayChkOk ? sNULL : sCLRRED)
                paragraph pTS("Notice:\nAfter manually refreshing the cookie leave this page and come back before the date will change.", sNULL, false, sCLR4D9)

                // Clears cookies for app and devices
                input "resetCookies", sBOOL, title: inTS1("Remove All Cookie Data?", sRESET), description: spanSm("Clear all stored cookie data from the app and devices."), required: false, defaultValue: false, submitOnChange: true
                paragraph pTS("Clear all stored cookie data from the app and devices.", sNULL, false, sCLRGRY)

                input "refreshDevCookies", sBOOL, title: inTS1("Resend Cookies to Devices?", sRESET), description: spanSm("Force devices to synchronize their stored cookies."), required: false, defaultValue: false, submitOnChange: true
                paragraph pTS("Force devices to synchronize their stored cookies.", sNULL, false, sCLRGRY)

                if((Boolean)settings.refreshCookie) { settingUpdate("refreshCookie", sFALSE, sBOOL); runIn(2, "runCookieRefresh") }
                if(settings.resetCookies) { clearCookieData("resetCookieToggle", false) }
                if((Boolean)settings.refreshDevCookies) { Boolean a = refreshDevCookies() }
            }
        }

        section(sectHead("Cookie Service Management")) {
            String t0 = getServiceConfDesc()
            href "servPrefPage", title: inTS1("Manage Cookie Login Service", sSETTINGS), description: (t0 ? divSm(t0, sCLR4D9) + inputFooter(sTTM, sCLR4D9) : inputFooter(sTTC, sCLRGRY, true))
        }
    }
}

def servPrefPage() {
    Boolean newInstall = !(Boolean)state.isInstalled
    Boolean resumeConf = (Boolean)state.resumeConfig
    return dynamicPage(name: "servPrefPage", install: (newInstall || resumeConf), nextPage: (!(newInstall || resumeConf) ? "mainPage" : sBLANK), uninstall: !(Boolean)state.serviceConfigured) {
        Boolean authValid = (Boolean)state.authValid

        if(settings.useHeroku == null) settingUpdate("useHeroku", sTRUE, sBOOL)
        if(settings.amazonDomain == null) settingUpdate("amazonDomain", "amazon.com", sENUM)
        if(settings.regionLocale == null) settingUpdate("regionLocale", "en-US", sENUM)

        if(!(Boolean)state.serviceConfigured) {
            section(sectHead("Cookie Server Deployment Option:")) {
                input "useHeroku", sBOOL, title: inTS1("Deploy server to Heroku?", sHEROKU), description: spanSm("Turn Off to allow local server deployment"), required: false, defaultValue: true, submitOnChange: true
                if((Boolean)settings.useHeroku == false) { paragraph spanSmBldBr("NOTICE:", sCLRRED) + spanSmBld("I highly recommend Heroku deployments for most users. Local Server deployments are something that can be very difficult for me to support remotely.", sCLRRED) }
            }
            section() { paragraph spanSmBld("To proceed with the server setup.<br>Tap on 'Begin Server Setup below", sCLR4D9) }
            srvcPrefOpts(true)
            section(sectHead("Deploy the Server:")) {
                href url: getAppEndpointUrl("config"), style: sEXTNRL, title: inTS1("Begin Server Setup", "upload"), description: inactFoot(sTTP)
            }
        } else {
            String myUrl = "${getServerHostURL()}/config"
            String t0 = getServiceConfDesc()
            if(!authValid) {
                section(sectHead("Authentication:")) {
                    paragraph spanSmBld("You still need to Login to Amazon to complete the setup", sCLRRED)
                    href url: myUrl, style: sEXTNRL, title: inTS1("Amazon Login Page", sAMAZONORNG), description: t0 + inputFooter(sTTP, sCLR4D9)
                }
            } else {
                Boolean oH = (Boolean)getServerItem("onHeroku")
                section(sectHead("Server Management:")) {
                    if(oH && (String)state.herokuName) { paragraph spanSmBr("Heroku Name:", sCLR4D9) + spanSmBld(" ${sBULLET} ${(String)state.herokuName}", sCLR4D9) }
                    href url: myUrl, style: sEXTNRL, title: inTS1("Amazon Login Page", sAMAZONORNG), description: t0 + inputFooter(sTTP, sCLR4D9)
                    if(oH) href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/settings", style: sEXTNRL, title: inTS1("Heroku App Settings", sHEROKU), description: inputFooter(sTTP, sCLR4D9)
                    if(oH) href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/logs", style: sEXTNRL, title: inTS1("Heroku App Logs", sHEROKU), description: inputFooter(sTTP, sCLR4D9)
                }
            }
            srvcPrefOpts()
        }
        section(sectHead("Reset Options (Tap to show):"), hideable: true, hidden: true) {
            input "resetService", sBOOL, title: inTS1("Reset Service Data?", sRESET), description: "This will clear all references to the current server and allow you to redeploy a new instance.\nLeave the page and come back after toggling.",
                required: false, defaultValue: false, submitOnChange: true
            paragraph pTS("This will clear all references to the current server and allow you to redeploy a new instance.\nLeave the page and come back after toggling.", sNULL, false, sCLRGRY)
            if(settings.resetService) { clearCloudConfig() }
        }
        state.resumeConfig = false
    }
}

def srvcPrefOpts(Boolean req=false) {
    section(sectHead("${req ? "Required " : sBLANK}Amazon Locale Settings"), hideable: false, hidden: false) {
        if(req) {
            input "amazonDomain", sENUM, title: inTS1("Select your Amazon Domain?", sAMAZONORNG), description: sBLANK, required: true, defaultValue: "amazon.com", options: amazonDomainOpts(), submitOnChange: true
            input "regionLocale", sENUM, title: inTS1("Select your Locale?", "www"), description: sBLANK, required: true, defaultValue: "en-US", options: localeOpts(), submitOnChange: true
        } else {
            String s = sBLANK
            s += settings.amazonDomain ? "Amazon Domain: (${settings.amazonDomain})" : sBLANK
            s += settings.regionLocale ? "\nLocale Region: (${settings.regionLocale})" : sBLANK
            paragraph spanSm(s, sCLR4D9, getAppImg(sAMAZONORNG))
        }
    }
}

def deviceManagePage() {
    return dynamicPage(name: "deviceManagePage", uninstall: false, install: false) {
        Boolean newInstall = !(Boolean)state.isInstalled
        section(sectHead("Alexa Devices:")) {
            if(!newInstall) {
                Map devs = getDeviceList()
                Map skDevs = ((Map)state.skippedDevices)?.findAll { (it?.value?.reason != sIN_IGNORE) }
                Map ignDevs = ((Map)state.skippedDevices)?.findAll { (it?.value?.reason == sIN_IGNORE) }
                if(devs.size()) {
                    String devDesc = devs.collect { "<span>${it.value?.name}</span>${it.value?.online ? "<span style='color: green;'> (Online)</span>" : sBLANK}${it.value?.supported == false ? "<span style='color: red;'> ${sFRNFACE}</span>" : sBLANK}" }?.sort()?.join("<br>")?.toString()
                    String dd = spanSmBr(devDesc) + inputFooter(sTTVD)
                    href "deviceListPage", title: inTS1("Installed Devices:"), description: divSm(dd, sCLR4D9)
                } else { paragraph spanSm("Discovered Devices:<br>No Devices Available", sCLRRED) }
                List remDevs = getRemovableDevs()
                if(remDevs?.size()) {
                    String rd = spanSm(remDevs.sort().collect { " ${sBULLET} ${it}" }.join("<br>"))
                    href "devCleanupPage", title: inTS1("Removable Devices:"), description: divSm(rd, sCLRGRY)
                }
                if(skDevs?.size()) {
                    String uDesc = "Unsupported: (${skDevs?.size()})"
                    uDesc += ignDevs?.size() ? "\nUser Ignored: (${ignDevs?.size()})" : sBLANK
                    uDesc += (Boolean)settings.bypassDeviceBlocks ? "\nBlock Bypass: (Active)" : sBLANK
                    href "unrecogDevicesPage", title: inTS1("Unused Devices:"), description: spanSmBr("${uDesc}", sCLRORG) + inputFooter(sTTVD)
                }
            }
            String devPrefDesc = devicePrefsDesc()
            href "devicePrefsPage", title: inTS1("Device Detection Preferences", sDEVICES), description: (devPrefDesc ? devPrefDesc + inputFooter(sTTM) : inputFooter(sTTC, sCLRGRY))
        }
    }
}

def alexaGuardPage() {
    return dynamicPage(name: "alexaGuardPage", uninstall: false, install: false) {
        String gState = (String)state.alexaGuardState ? ((String)state.alexaGuardState == sARM_AWAY ? "Away" : "Home") : sUnknown
        String gStateIcon = gState == sUnknown ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
        String gStateTitle = (gState == sUnknown || gState == "Home") ? "Set Guard to Armed?" : "Set Guard to Home?"
        section(sectHead("Alexa Guard Control")) {
            paragraph spanSm("Current Status:", sCLR4D9) + spanSm(" (${gState})", (gState == sUnknown ? sCLRGRY : (gState == "Away" ? sCLRORG : sCLRGRN)))
            input "alexaGuardAwayToggle", sBOOL, title: inTS1(gStateTitle, gStateIcon), defaultValue: false, submitOnChange: true
        }
        if(settings.alexaGuardAwayToggle != state.alexaGuardAwayToggle) {
            setGuardState(settings.alexaGuardAwayToggle == true ? sARM_AWAY : sARM_STAY)
        }
        state.alexaGuardAwayToggle = settings.alexaGuardAwayToggle
        section(sectHead("Automate Guard Control")) {
            String t0 = guardAutoDesc()
            href "alexaGuardAutoPage", title: inTS1("Automate Guard Changes", "alarm_disarm"), description: (t0 ? t0 + inputFooter(sTTM) : spanSm("Automate the control of Alexa using modes, HSM, and more.", sCLRGRY) + inputFooter(sTTC, sCLRGRY, true))
        }
    }
}

def alexaGuardAutoPage() {
    return dynamicPage(name: "alexaGuardAutoPage", uninstall: false, install: false) {
        String asn = getAlarmSystemName(true)
        List amo = getAlarmModes()
        Boolean alarmReq = (settings.guardAwayAlarm || settings.guardHomeAlarm)
        Boolean modeReq = (settings.guardAwayModes || settings.guardHomeModes)
        // Boolean swReq = (settings.guardAwaySw || settings.guardHomeSw)
        section(sectHead("Set Guard Using ${asn}")) {
            input "guardHomeAlarm", sENUM, title: inTS1("Home in ${asn} modes.", "alarm_home"), description: inputFooter(sTTS, sCLRGRY, true), options: amo, required: alarmReq, multiple: true, submitOnChange: true
            input "guardAwayAlarm", sENUM, title: inTS1("Away in ${asn} modes.", "alarm_away"), description: inputFooter(sTTS, sCLRGRY, true), options: amo, required: alarmReq, multiple: true, submitOnChange: true
        }

        section(sectHead("Set Guard Using Modes")) {
            input "guardHomeModes", "mode", title: inTS1("Home in these Modes?", "mode"), description: inputFooter(sTTS, sCLRGRY, true), required: modeReq, multiple: true, submitOnChange: true
            input "guardAwayModes", "mode", title: inTS1("Away in these Modes?", "mode"), description: inputFooter(sTTS, sCLRGRY, true), required: modeReq, multiple: true, submitOnChange: true
        }

        section(sectHead("Set Guard Using Switches:")) {
            input "guardHomeSwitch", "capability.switch", title: inTS1("Home when any of these are On?", sSWITCH), description: inputFooter(sTTS, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
            input "guardAwaySwitch", "capability.switch", title: inTS1("Away when any of these are On?", sSWITCH), description: inputFooter(sTTS, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
            input "guardFollowSwitch", "capability.switch", title: inTS1("Follow Switch State (ON = Away | OFF = HOME)?", sSWITCH), description: inputFooter(sTTS, sCLRGRY, true), multiple: false, required: false, submitOnChange: true
        }

        section(sectHead("Set Guard using Presence")) {
            input "guardAwayPresence", "capability.presenceSensor", title: inTS1("Away when these devices are All away?", "presence"), description: inputFooter(sTTS, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
        }
        if(guardAutoConfigured()) {
            section(sectHead("Delay:")) {
                input "guardAwayDelay", "number", title: inTS1("Delay before arming Away?\n(in seconds)", "delay_time"), description: "Enter number in seconds", required: false, defaultValue: 30, submitOnChange: true
            }
        }
        section(sectHead("Restrict Guard Changes (Optional):")) {
            input "guardRestrictOnSwitch", "capability.switch", title: inTS1("Only when these are On?", sSWITCH), description: inputFooter(sTTS, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
            input "guardRestrictOffSwitch", "capability.switch", title: inTS1("Only when these are Off?", sSWITCH), description: inputFooter(sTTS, sCLRGRY, true), multiple: true, required: false, submitOnChange: true
        }
    }
}

Boolean guardAutoConfigured() {
    return ((settings.guardAwayAlarm && settings.guardHomeAlarm) || (settings.guardAwayModes && settings.guardHomeModes) || settings.guardFollowSwitch || (settings.guardAwaySwitch && settings.guardHomeSwitch) || settings.guardAwayPresence)
}

String guardAutoDesc() {
    String str = sBLANK
    if(guardAutoConfigured()) {
        str += spanSmBldBr("Guard Triggers:")
        str += (settings.guardAwayAlarm && settings.guardHomeAlarm) ? spanSmBr(" ${sBULLET} Using ${getAlarmSystemName()}") : sBLANK
        str += settings.guardHomeModes ? spanSmBr(" ${sBULLET} Home Modes: (${settings.guardHomeModes?.size()})") : sBLANK
        str += settings.guardAwayModes ? spanSmBr(" ${sBULLET} Away Modes: (${settings.guardAwayModes?.size()})") : sBLANK
        str += settings.guardHomeSwitch ? spanSmBr(" ${sBULLET} Home Switches: (${settings.guardHomeSwitch?.size()})") : sBLANK
        str += settings.guardAwaySwitch ? spanSmBr(" ${sBULLET} Away Switches: (${settings.guardAwaySwitch?.size()})") : sBLANK
        str += settings.guardFollowSwitch ? spanSmBr(" ${sBULLET} Follow Switch: (${isSwitchOn(settings.guardFollowSwitch) ? "Armed Away" : "Disarmed"})") : sBLANK
        str += settings.guardAwayPresence ? spanSmBr(" ${sBULLET} Presence Home: (${settings.guardAwayPresence?.size()})") : sBLANK
    }
    return str != sBLANK ? divSm(str, sCLR4D9) : sBLANK
}

def guardTriggerEvtHandler(evt) {
    Long evtDelay = now() - (Long)evt.date.getTime()
    logDebug("${evt.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize((String)evt?.value)}) with a delay of ${evtDelay}ms")
    if(!guardRestrictOk()) {
        logDebug("guardTriggerEvtHandler | Skipping Guard Changes because Restriction are Active.")
        return
    }
    String newState = sNULL
    String curState = (String)state.alexaGuardState ?: sNULL
    switch((String)evt?.name) {
        case "mode":
            Boolean inAwayMode = isInMode((List)settings.guardAwayModes)
            Boolean inHomeMode = isInMode((List)settings.guardHomeModes)
            if(inAwayMode && inHomeMode) { logError("Guard Control Trigger can't act because same mode is in both Home and Away input"); return }
            if(inAwayMode && !inHomeMode) { newState = sARM_AWAY }
            if(!inAwayMode && inHomeMode) { newState = sARM_STAY }
            break
        case "switch":
            Boolean isFollowSwitch = (settings.guardFollowSwitch && settings.guardFollowSwitch.getId().toInteger() == evt.getDeviceId().toInteger())
            if(isFollowSwitch) {
                newState = isSwitchOn(settings.guardFollowSwitch) ? sARM_AWAY : sARM_STAY
            } else {
                Boolean inAwaySw = isSwitchOn((List)settings.guardAwaySwitch)
                Boolean inHomeSw = isSwitchOn((List)settings.guardHomeSwitch)
                if(inAwaySw && inHomeSw) { logError("Guard Control Trigger can't act because both switch groups are in both Home and Away input"); return }
                if(inAwaySw && !inHomeSw) { newState = sARM_AWAY }
                if(!inAwaySw && inHomeSw) { newState = sARM_STAY }
            }
            break
        case "presence":
            newState = isSomebodyHome((List)settings.guardAwayPresence) ? sARM_STAY : sARM_AWAY
            break
        case "alarmSystemStatus":
        case "hsmStatus":
            Boolean inAlarmHome = isInAlarmMode((List)settings.guardHomeAlarm)
            Boolean inAlarmAway = isInAlarmMode((List)settings.guardAwayAlarm)
            if(inAlarmAway && !inAlarmHome) { newState = sARM_AWAY }
            if(!inAlarmAway && inAlarmHome) { newState = sARM_STAY }
            break
    }
    if(guardArmPendingFLD && curState == sARM_STAY) {
        unschedule("setGuardAway")
        logInfo("New Guard State is Now STAY... Scheduled Arming Has Been Cancelled...")
        guardArmPendingFLD = false
        return
    }
    if(curState == newState) { logInfo("Skipping Guard Change... New Guard State is the same as current state: ($curState)") }
    if(newState && curState != newState) {
        if (newState == sARM_STAY) {
            unschedule("setGuardAway")
            logInfo("Setting Alexa Guard Mode to Home...")
            setGuardHome()
        }
        if(newState == sARM_AWAY) {
            if(settings.guardAwayDelay) {
                guardArmPendingFLD = true
                logWarn("Setting Alexa Guard Mode to Away in (${settings.guardAwayDelay} seconds)", true)
                runIn(settings.guardAwayDelay, "setGuardAway")
                
            }
            else { setGuardAway(); logWarn("Setting Alexa Guard Mode to Away...", true) }
        }
    }
}

Boolean guardRestrictOk() {
    Boolean onSwOk = settings.guardRestrictOnSwitch ? isSwitchOn((List)settings.guardRestrictOnSwitch) : true
    Boolean offSwOk = settings.guardRestrictOffSwitch ? !isSwitchOn((List)settings.guardRestrictOffSwitch) : true
    return (onSwOk && offSwOk)
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", nextPage: "mainPage", uninstall: false, install: false) {
        List actApps = getActionApps()
        List activeActions = actApps?.findAll { it?.isPaused() != true }
        List pausedActions = actApps?.findAll { it?.isPaused() == true }
        if(actApps) { /*Nothing to add here yet*/ }
        else { section(sBLANK) { paragraph pTS("You haven't created any Actions yet!\nTap Create New Action to get Started") } }
        section() {
            app(name: "actionApp", appName: actChildName(), namespace: "tonesto7", multiple: true, title: inTS1("Create New Action", "es_actions"))
            if(actApps?.size()) {
                input "actionDuplicateSelect", sENUM, title: inTS1("Duplicate Existing Action", "es_actions"), description: sTTS, options: actApps?.collectEntries { [(it?.id):it?.getLabel()] }, required: false, multiple: false, submitOnChange: true
                if(settings.actionDuplicateSelect) {
                    href "actionDuplicationPage", title: inTS1(spanSm("Create Duplicate Action?", sCLRGRY), "question"), description: inactFoot(sTTP)
                }
            }
        }
        if(actApps?.size()) {
            section (sectHead("Action History:")) {
                href "viewActionHistory", title: inTS1("View Action History", "tasks"), description: spanSm("(Grouped by Action)", sCLR4D9)
            }

            section (sectHead("Global Actions Management:"), hideable: true, hidden: true) {
                if(activeActions?.size()) {
                    input "pauseChildActions", sBOOL, title: inTS1("Pause all actions?", "pause_orange"), description: sBLANK, defaultValue: false, submitOnChange: true
                    if((Boolean)settings.pauseChildActions) { settingUpdate("pauseChildActions", sFALSE, sBOOL); runIn(3, "executeActionPause") }
                    paragraph spanSm("When pausing all Actions you can either restore all or open each action and manually unpause it.", sCLRGRY)
                }
                if(pausedActions?.size()) {
                    input "unpauseChildActions", sBOOL, title: inTS1("Restore all actions?", "pause_orange"), defaultValue: false, submitOnChange: true
                    if(settings.unpauseChildActions) { settingUpdate("unpauseChildActions", sFALSE, sBOOL); runIn(3, "executeActionUnpause") }
                }
                input "reinitChildActions", sBOOL, title: inTS1("Force Refresh all actions?", sRESET), defaultValue: false, submitOnChange: true
                if(settings.reinitChildActions) { settingUpdate("reinitChildActions", sFALSE, sBOOL); runIn(3, "executeActionUpdate") }
            }
        }
        state.childInstallOkFlag = true
        state.actionDuplicated = false
    }
}

def actionDuplicationPage() {
    return dynamicPage(name: "actionDuplicationPage", nextPage: "actionsPage", uninstall: false, install: false) {
        section() {
            if((Boolean)state.actionDuplicated) {
                paragraph spanSmBldBr("Action already duplicated...", sCLRRED) + spanSmBld("Return to action page and select it", sCLRRED)
            } else {
                def act = getActionApps()?.find { it?.id?.toString() == settings.actionDuplicateSelect?.toString() }
                if(act) {
                    Map actData = act.getSettingsAndStateMap() ?: [:]
                    String actId = (String)act.getId().toString()
                    if(actData.settings && actData.state) {
                        String myId=app.getId()
                        if(!childDupMapFLD[myId]) childDupMapFLD[myId] = [:]
                        if(!childDupMapFLD[myId].actions) childDupMapFLD[myId].actions = [:]
                        childDupMapFLD[myId].actions[actId] = actData
                        log.debug "Dup Data: ${childDupMapFLD[myId].actions[actId]}"
                    }
                    actData.settings["duplicateFlag"] = [type: sBOOL, value: true]
                    // actData?.settings["actionPause"] = [type: sBOOL, value: true]
                    actData.settings["duplicateSrcId"] = [type: "text", value: actId]
                    def a=addChildApp("tonesto7", actChildName(), "${actData.label} (Dup)", [settings: actData.settings])
                    paragraph spanSmBldBr("Action Duplicated...", sCLR4D9) + spanSmBld("<br>Return to Action Page and look for the App with '(Dup)' in the name...", sCLR4D9)
                    state.actionDuplicated = true
                } else { paragraph spanSmBld("Action not Found", sCLRRED) }
            }
        }
    }
}

def zoneDuplicationPage() {
    return dynamicPage(name: "zoneDuplicationPage", nextPage: "zonesPage", uninstall: false, install: false) {
        section() {
            if((Boolean)state.zoneDuplicated) {
                paragraph spanSmBldBr("Zone already duplicated...", sCLRRED) +  spanSmBld("<br>Return to zone page and select it", sCLRRED)
            } else {
                def zn = getZoneApps()?.find { it.id.toString() == settings.zoneDuplicateSelect?.toString() }
                if(zn) {
                    Map znData = zn?.getSettingsAndStateMap() ?: null
                    String znId = (String)zn.getId().toString()
                    if(znData.settings && znData.state) {
                        String myId=app.getId()
                        if(!childDupMapFLD[myId]) childDupMapFLD[myId] = [:]
                        if(!childDupMapFLD[myId].zones) childDupMapFLD[myId].zones = [:]
                        childDupMapFLD[myId].zones[znId] = znData
                        log.debug "Dup Data: ${childDupMapFLD[myId].zones[znId]}"
                    }
                    // log.debug "Dup Data: ${actData}"
                    znData.settings["duplicateFlag"] = [type: sBOOL, value: true]
                    // znData?.settings["zonePause"] = [type: sBOOL, value: true]
                    znData?.settings["duplicateSrcId"] = [type: "text", value: znId]
                    def a=addChildApp("tonesto7", zoneChildName(), "${znData?.label} (Dup)", [settings: znData.settings])
                    paragraph spanSmBldBr("Zone Duplicated...", sCLR4D9) + spanSmBld("<br>Return to Zone Page and look for the App with '(Dup)' in the name...", sCLR4D9)
                    state.zoneDuplicated = true
                } else { paragraph spanSmBld("Zone not Found", sCLRRED) }
            }
        }
    }
}

public Map getChildDupeData(String type, String childId) {
    String myId=app.getId()
    return (childDupMapFLD[myId] && childDupMapFLD[myId][type] && childDupMapFLD[myId][type][childId]) ? (Map)childDupMapFLD[myId][type][childId] : [:]
}

public void clearDuplicationItems() {
    state.actionDuplicated = false
    state.zoneDuplicated = false
    if(settings.actionDuplicateSelect) settingRemove("actionDuplicateSelect")
    if(settings.zoneDuplicateSelect) settingRemove("zoneDuplicateSelect")
    state.remove('actionDuplicated')
    state.remove('zoneDuplicated')
}

public void childAppDuplicationFinished(String type, String childId) {
    log.trace "childAppDuplicationFinished($type, $childId)"
//    Map data = [:]
     String myId=app.getId()
    if(childDupMapFLD[myId] && childDupMapFLD[myId][type] && childDupMapFLD[myId][type][childId]) {
        childDupMapFLD[myId][type].remove(childId)
    }
    clearDuplicationItems()
}

def zonesPage() {
    return dynamicPage(name: "zonesPage", nextPage: "mainPage", uninstall: false, install: false) {
        List zApps = getZoneApps()
        List activeZones = zApps?.findAll { it?.isPaused() != true }
        List pausedZones = zApps?.findAll { it?.isPaused() == true }
        if(zApps) { /*Nothing to add here yet*/ }
        else {
            section(sBLANK) { paragraph spanSmBld("You haven't created any Zones yet!<br>Tap Create New Zone to get Started", sCLRGRY) }
        }
        section() {
            app(name: "zoneApp", appName: zoneChildName(), namespace: "tonesto7", multiple: true, title: inTS1("Create New Zone", "es_groups"))
            if(zApps?.size()) {
                input "zoneDuplicateSelect", sENUM, title: inTS1("Duplicate Existing Zone", "es_groups"), description: sTTS, options: zApps?.collectEntries { [(it?.id):it?.getLabel()] }, required: false, multiple: false, submitOnChange: true
                if(settings.zoneDuplicateSelect) {
                    href "zoneDuplicationPage", title: inTS1(spanSm("Create Duplicate Zone?", sCLRGRY), "question"), description: inactFoot(sTTP)
                }
            }
        }
        if(zApps?.size()) {
            section (sectHead("Zone History:")) {
                href "viewZoneHistory", title: inTS1("View Zone History", "tasks"), description: spanSm("(Grouped by Zone)", sCLR4D9)
            }
        }
        section (sectHead("Zone Management:"), hideable: true, hidden: true) {
            if(activeZones?.size()) {
                input "pauseChildZones", sBOOL, title: inTS1("Pause all Zones?", "pause_orange"), description: "When pausing all Zones you can either restore all or open each zones and manually unpause it.", defaultValue: false, submitOnChange: true
                if(settings.pauseChildZones) { settingUpdate("pauseChildZones", sFALSE, sBOOL); runIn(3, "executeZonePause") }
                paragraph spanSm("When pausing all zones you can either restore all or open each zone and manually unpause it.", sCLRGRY)
            }
            if(pausedZones?.size()) {
                input "unpauseChildZone", sBOOL, title: inTS1("Restore all actions?", "pause_orange"), defaultValue: false, submitOnChange: true
                if(settings.unpauseChildZone) { settingUpdate("unpauseChildZone", sFALSE, sBOOL); runIn(3, "executeZoneUnpause") }
            }
            input "reinitChildZones", sBOOL, title: inTS1("Clear Zones Status and force a full status refresh for all zones?", sRESET), defaultValue: false, submitOnChange: true
            if(settings.reinitChildZones) { settingUpdate("reinitChildZones", sFALSE, sBOOL); runIn(3, "executeZoneUpdate") }
        }
        state.childInstallOkFlag = true
        state.zoneDuplicated = false
        updateZoneSubscriptions()
    }
}

def viewZoneHistory() {
    return dynamicPage(name: "viewZoneHistory", title: div("<h2>Zone Event History</h2>", sNULL, sNULL, true), uninstall: false, install: false) {
        List zApps = getZoneApps()
        zApps?.each { z->
            section(sectHead((String)z.getLabel())) {
                List<String> items = z.getZoneHistory(true)
                items = items ?: []
                items.each { String v->
                    paragraph spanSm(v)
                }
            }
        }
    }
}

def viewActionHistory() {
    return dynamicPage(name: "viewActionHistory", title: div("<h2>Action Event History</h2>", sNULL, sNULL, true), uninstall: false, install: false) {
        List actApps = getActionApps()
        actApps?.each { a->
            section(sectHead((String)a.getLabel())) {
                List<String> items = (List<String>)a.getActionHistory(true)
                items.each { String v->
                    paragraph spanSm(v)
                }
            }
        }
    }
}

void executeActionPause() {
    getActionApps()?.findAll { it?.isPaused() != true }?.each { it?.updatePauseState(true) }
}
void executeActionUnpause() {
    getActionApps()?.findAll { it?.isPaused() == true }?.each { it?.updatePauseState(false) }
}
void executeActionUpdate() {
    getActionApps()?.each { it?.updated() }
}
void executeZonePause() {
    getZoneApps()?.findAll { it?.isPaused() != true }?.each { it?.updatePauseState(true) }
}
void executeZoneUnpause() {
    getZoneApps()?.findAll { it?.isPaused() == true }?.each { it?.updatePauseState(false) }
}
void executeZoneUpdate() {
    getZoneApps()?.each { it?.updated() }
}

def devicePrefsPage() {
//    Boolean newInstall = !(Boolean)state.isInstalled
//    Boolean resumeConf = (Boolean)state.resumeConfig
    return dynamicPage(name: "devicePrefsPage", uninstall: false, install: false) {
        deviceDetectOpts()
        section(sectHead("Detection Override:")) {
            paragraph spanSmBldBr("Device not detected?", sCLRORG) + spanSm("Enabling this will allow you to override the developer block for unrecognized or uncontrollable devices.<br>This is useful for testing if a device supports certain features.", sCLRORG)
            input "bypassDeviceBlocks", sBOOL, title: inTS1("Override Blocks and Create Ignored Devices?"), required: false, defaultValue: false, submitOnChange: true
            paragraph spanSmBld("WARNING:", sCLRRED) + spanSm(" This will create devices for all remaining ignored devices", sCLRRED)
        }
        devCleanupSect()
//        if(!newInstall && !resumeConf) { state.refreshDeviceData = true }
    }
}

private deviceDetectOpts() {
//    Boolean newInstall = !(Boolean) state.isInstalled
//    Boolean resumeConf = (Boolean) state.resumeConfig
    section(sectHead("Device Detection Preferences")) {
        input "autoCreateDevices", sBOOL, title: inTS1("Auto Create New Devices?", sDEVICES), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        input "createTablets", sBOOL, title: inTS1("Create Devices for Tablets?", "amazon_tablet"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        input "createWHA", sBOOL, title: inTS1("Create Multiroom Devices?", "echo_wha"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        input "createOtherDevices", sBOOL, title: inTS1("Create Other Alexa Enabled Devices?", sDEVICES), description: "FireTV (Cube, Stick), Sonos, etc.", required: false, defaultValue: false, submitOnChange: true
        input "autoRenameDevices", sBOOL, title: inTS1("Rename Devices to Match Amazon Echo Name?", "name_tag"), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        input "addEchoNamePrefix", sBOOL, title: inTS1("Add 'Echo - ' Prefix to label?", "name_tag"), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        Map devs = getAllDevices(true)
        if(devs?.size()) {
            input "echoDeviceFilter", sENUM, title: inTS1("Don't Use these Devices", "exclude"), description: sTTS, options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            paragraph spanSmBldBr("Notice:", sCLR4D9) + spanSm("To prevent unwanted devices from reappearing after removal make sure to add the device to the Don't Use these Devices input above before removing.", sCLR4D9)
        }
    }
}

private devCleanupPage() {
    return dynamicPage(name: "devCleanupPage", uninstall: false, install: false) {
        devCleanupSect()
    }
}

private devCleanupSect() {
    if(state.isInstalled && !(Boolean)state.resumeConfig) {
        section(sectHead("Device Cleanup Options:")) {
            Map devs = getAllDevices(true)
            if(devs?.size()) {
                input "echoDeviceFilter", sENUM, title: inTS1("Don't Use these Devices", "exclude"), description: sTTS, options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            }
            List remDevs = getRemovableDevs()
            if(remDevs.size()) { paragraph spanSmBldBr("Removable Devices:", sCLRRED) + spanSm(remDevs.sort().collect { " ${sBULLET} ${it}" } ?.join("<br>"), sCLRGRY), required: true }
            paragraph spanSmBldBr("Notice:", sCLR4D9) + spanSm("Remember to add device to filter above to prevent recreation.<br>Also the cleanup process will fail if the devices are used in external apps/automations", sCLR4D9)
            input "cleanUpDevices", sBOOL, title: inTS1("Cleanup Unused Devices?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
            if((Boolean)settings.cleanUpDevices) { removeDevices() }
        }
    }
}

private List getRemovableDevs() {
    Map eDevs = getEchoDeviceMap()
    eDevs = eDevs  ?: [:]
    List cDevs = app?.getChildDevices()
    List remDevs = []
    String myId=app.getId()
    String nmS = 'echoSpeaks_websocket'
    nmS = myId+'|'+nmS

    cDevs?.each { cDev->
        if(cDev?.deviceNetworkId?.toString() == nmS) { return }
        List dni = cDev?.deviceNetworkId?.tokenize("|")
        if(eDevs.size() && dni[2] && !eDevs.containsKey(dni[2])) { remDevs.push(cDev?.getLabel() as String) }
    }
    return remDevs ?: []
}

private String devicePrefsDesc() {
    String str = sBLANK
    str += "Auto Create (${!(Boolean)settings.autoCreateDevices ? "Disabled" : "Enabled"})"
    if((Boolean)settings.autoCreateDevices) {
        str += (Boolean) settings.createTablets ? "<br><span> ${sBULLET} Tablets</span>" : sBLANK
        str += (Boolean) settings.createWHA ? "<br><span> ${sBULLET} WHA</span>" : sBLANK
        str += (Boolean) settings.createOtherDevices ? "<br><span> ${sBULLET} Other Devices</span>" : sBLANK
    }
    str += (Boolean) settings.autoRenameDevices ? "<br><span> ${sBULLET} Auto Rename</span>" : sBLANK
    str += (Boolean) settings.bypassDeviceBlocks ? "<br><span> ${sBULLET} Block Bypass: (Active)</span>" : sBLANK
    str = paraTS(null, str, null, [:], [s: 'small', c: sCLR4D9])
    return str != sBLANK ? str : sNULL
}

def settingsPage() {
    return dynamicPage(name: "settingsPage", uninstall: false, install: false) {
        section(sectHead("Logging:")) {
            input "logInfo", sBOOL, title: inTS1("Show Info Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logWarn", sBOOL, title: inTS1("Show Warning Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logError", sBOOL, title: inTS1("Show Error Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logDebug", sBOOL, title: inTS1("Show Debug Logs?", sDEBUG), description: "Auto disables after 6 hours", required: false, defaultValue: false, submitOnChange: true
            input "logTrace", sBOOL, title: inTS1("Show Detailed Logs?", sDEBUG), description: "Only enabled when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
        }

        // if(devModeFLD) {
        section(sectHead("Child Logging:")) {
            input "childAppLogDebug", sBOOL, title: inTS1("Enable Debug Logs for All Child Apps?", sDEBUG), description: "Auto disables after 6 hours", required: false, defaultValue: false, submitOnChange: true
            input "childAppLogTrace", sBOOL, title: inTS1("Enable Trace Logs for All Child Apps?", sDEBUG), description: "Only enabled when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
            input "childDeviceLogDebug", sBOOL, title: inTS1("Enable Debug Logs for All Child Devices?", sDEBUG), description: "Auto disables after 6 hours", required: false, defaultValue: false, submitOnChange: true
            input "childDeviceLogTrace", sBOOL, title: inTS1("Enable Trace Logs for All Child Devices?", sDEBUG), description: "Only enabled when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
            input "disableAllChildAdvLogs", sBOOL, title: inTS1("Disable All Advanced Logging on Child Apps/Devices?", sDEBUG), description: "Only enabled when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
            if((Boolean)settings.childAppLogDebug || (Boolean)settings.childAppLogTrace || (Boolean)settings.childDeviceLogDebug || (Boolean)settings.childDeviceLogTrace || (Boolean)settings.disableAllChildAdvLogs) { activateChildAdvLogs() }
        }
        // }

        if(advLogsActive()) { logsEnabled() }
        section(sectHead("Text Transforms:")) {
            input "disableTextTransform", sBOOL, title: inTS1("Disable Text Transform?", "question"), description: "This will disable attempts to convert items in speech text like temp units and directions like `WSW` to west southwest", required: false, defaultValue: false, submitOnChange: true
        }
        showDevSharePrefs()
        section(sectHead("Diagnostic Data:")) {
            paragraph pTS("If you are having trouble send a private message to the developer with a link to this page that is shown below.", sNULL, false, sCLRGRY)
            input "diagShareSensitveData", sBOOL, title: inTS1("Share Cookie Data?", "question"), required: false, defaultValue: false, submitOnChange: true
            href url: getAppEndpointUrl("diagData"), style: sEXTNRL, title: inTS1("Diagnostic Data"), description: spanSm("Tap to view", sCLRGRY)
        }
    }
}

def deviceListPage() {
    return dynamicPage(name: "deviceListPage", install: false) {
        section(sectHead("Discovered Devices:")) {
            paragraph spanSmBldBr("NOTICE: The test buttons on this page sometimes do not work.  Please use the testing section on the main page if you have issues!", sCLRRED2)   
            getEchoDeviceMap()?.sort { it?.value?.name }?.each { String k, Map v->
                String str = "<span>Status: (${v.online ? "Online" : "Offline"})</span>"
                str += "<br><span>Style: ${v.style?.n}</span>"
                str += "<br><span>Family: ${v.family}</span>"
                str += "<br><span>Type: ${v.type}</span>"
                str += "<br><span>Volume Control: (${v.volumeSupport?.toString()?.capitalize()})</span>"
                str += "<br><span>Announcements: (${v.announceSupport?.toString()?.capitalize()})</span>"
                str += "<br><span>Text-to-Speech: (${v.ttsSupport?.toString()?.capitalize()})</span>"
                str += "<br><span>Music Player: (${v.mediaPlayer?.toString()?.capitalize()})</span>"
                str += v.supported != true ? "<br><span>Unsupported Device: (True)</span>" : sBLANK
                str += (v.mediaPlayer == true && v.musicProviders) ? "<br><span>Music Providers: [${v.musicProviders}]</span>" : sBLANK
                paragraph paraTS((String)v.name, str, (String)v.style?.i, [c: 'black', b: true, u: true], [s: 'small', c: (v.online ? sCLR4D9 : sCLRGRY)])
                input "deviceSpeechTest::${k}", "button", title: spanSmBld("Test Speech", sCLRGRY), width: 4
                input "deviceAnnouncementTest::${k}", "button", title: spanSmBld("Test Announcement", sCLRGRY), width: 4
            }
        }
    }
}

def unrecogDevicesPage() {
    return dynamicPage(name: "unrecogDevicesPage", install: false) {
        Map<String, Map> skDevMap = (Map<String, Map>)state.skippedDevices ?: [:]
        Map<String, Map> ignDevs = skDevMap?.findAll { (it?.value?.reason == sIN_IGNORE) }
        Map<String, Map> unDevs = skDevMap?.findAll { (it?.value?.reason != sIN_IGNORE) }
        section(sectHead("Unrecognized/Unsupported Devices:")) {
            if(unDevs?.size()) {
                unDevs.sort { it?.value?.name }?.each { String k, Map v->
                    // log.debug "v: $v"
                    String str = "<span>Status: (${(Boolean)v.online ? "Online" : "Offline"})</span>"
                    str += "<br><span>Style: ${(String) v.desc}</span>"
                    str += "<br><span>Family: ${(String)v.family}</span>"
                    str += "<br><span>Type: ${(String)v.type}</span>"
                    str += "<br><span>Volume Control: (${v?.volume?.toString()?.capitalize()})</span>"
                    str += "<br><span>Text-to-Speech: (${v?.tts?.toString()?.capitalize()})</span>"
                    str += "<br><span>Music Player: (${v?.mediaPlayer?.toString()?.capitalize()})</span>"
                    str += "<br><span>Reason Ignored: (${v?.reason})</span>"
                    paragraph paraTS((String)v.name, str, (String)v.image, [c: 'black', b: true, u: true], [s: 'small', c: (v.online ? sCLR4D9 : sCLRGRY)])
                }
                input "bypassDeviceBlocks", sBOOL, title: inTS1("Override Blocks and Create Ignored Devices?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
            } else {
                paragraph pTS("No Uncognized Devices", sNULL, true)
            }
        }
        if(ignDevs?.size()) {
            section(sectHead("User Ignored Devices:")) {
                ignDevs.sort { it?.value?.name }?.each { k,v->
                    String str = spanSmBr("Status: (${v.online ? "Online" : "Offline"})")
                    str += spanSmBr("Style: ${(String)v.desc}")
                    str += spanSmBr("Family: ${(String)v.family}")
                    str += spanSmBr("Type: ${(String)v.type}")
                    str += spanSmBr("Volume Control: (${v?.volume?.toString()?.capitalize()})")
                    str += spanSmBr("Text-to-Speech: (${v?.tts?.toString()?.capitalize()})")
                    str += spanSmBr("Music Player: (${v?.mediaPlayer?.toString()?.capitalize()})")
                    str += spanSmBr("Reason Ignored: (${v?.reason})")
                    href "unrecogDevicesPage", title: inTS1((String)v.name, (String)v.image), description: divSm(str, sCLRGRY)
                }
            }
        }
    }
}

def showDevSharePrefs() {
    section(sectHead("Share Data with Developer:")) {
        paragraph  spanSmBldBr("What is this used for?", sCLRGRY) + spanSmBldBr("These options send non-user identifiable information and error data to diagnose catch trending issues.", sCLRGRY)
        input ("optOutMetrics", sBOOL, title: inTS1("Do Not Share Data?", "analytics"), required: false, defaultValue: false, submitOnChange: true)
        href url: getAppEndpointUrl("renderMetricData"), style: sEXTNRL, title: inTS1("View the Data shared with Developer", "view"), description: inputFooter("Tap to view Data", sCLRGRY, true)
    }
    if(!(Boolean)settings.optOutMetrics && (Boolean)state.isInstalled && (Boolean)state.serviceConfigured && !(Boolean)state.resumeConfig) {
        section() { input "sendMetricsNow", sBOOL, title: inTS1("Send Metrics Now?", sRESET), description: sBLANK, required: false, defaultValue: false, submitOnChange: true }
        if(sendMetricsNow) { sendInstallData() }
    }
    state.shownDevSharePage = true
}

Map getDeviceList(Boolean isInputEnum=false, List filters=[]) {
    Map devMap = [:]
    Map<String, Map> availDevs = getEchoDeviceMap() //state.echoDeviceMap
    availDevs = availDevs ?: [:]
    availDevs.each { String key, Map val->
        if(filters.size()) {
            if(filters.contains('tts') && val.ttsSupport != true) { return }
            if(filters.contains('announce') && val.ttsSupport != true && val.announceSupport != true) { return }
        }
        devMap[key] = val
    }
    return isInputEnum ? (devMap.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

Map getAllDevices(Boolean isInputEnum=false) {
    Map<String, Map> devMap = [:]
    Map<String, Map> availDevs = (Map<String,Map>)state.allEchoDevices ?: [:]
    availDevs?.each { String key, Map val-> devMap[key] = val }
    return isInputEnum ? (devMap.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
        section(sBLANK) {
            paragraph spanSmBldBr("Notice:", sCLR4D9) + spanSmBld("The settings configured here are used by both the App and the Devices.", sCLR4D9, getAppImg("info"))
        }
        section (sectHead("Notification Devices:")) {
            input "notif_devs", "capability.notification", title: inTS1("Send to Notification devices?", "notification"), required: false, multiple: true, submitOnChange: true
        }

//TODO REMOVE
        if(settings.smsNumbers) settingRemove('smsNumbers')
        if(settings.usePush) settingRemove('usePush')
        state.remove('pushoverManager')
        settingRemove('pushoverEnabled')
        settingRemove('pushoverDevices')
        settingRemove('pushoverPriority')
        settingRemove('pushoverSound')

        if((List)settings.notif_devs) {
            if(!state.pushTested) {
                if(sendMsg("Info", "Notification Test Successful. Notifications Enabled for ${app?.label}", true)) {
                    state.pushTested = true
                }
            }
            section(sectHead("Notification Restrictions:")) {
                String t1 = getNotifSchedDesc()
                href "setNotificationTimePage", title: inTS1("Quiet Restrictions", "restriction"), description: (t1 ? t1 + inputFooter(sTTM) : inputFooter(sTTC, sCLRGRY))
            }
            section(sectHead("Missed Poll Alerts:")) {
                input (name: "sendMissedPollMsg", type: sBOOL, title: inTS1("Send Missed Checkin Alerts?", "late"), defaultValue: true, submitOnChange: true)
                if((Boolean)settings.sendMissedPollMsg) {
                    input (name: "misPollNotifyWaitVal", type: sENUM, title: inTS1("Time Past the Missed Checkin?", "delay_time"), description: spanSm("Default: 45 Minutes"), required: false, defaultValue: 2700, options: notifValEnum(), submitOnChange: true)
                    input (name: "misPollNotifyMsgWaitVal", type: sENUM, title: inTS1("Send Reminder After?", "reminder"), description: spanSm("Default: 1 Hour"), required: false, defaultValue: 3600, options: notifValEnum(), submitOnChange: true)
                }
            }
            section(sectHead("Cookie Alerts:")) {
                input (name: "sendCookieRefreshMsg", type: sBOOL, title: inTS1("Send on Refreshed Cookie?", "cookie"), defaultValue: false, submitOnChange: true)
                input (name: "sendCookieInvalidMsg", type: sBOOL, title: inTS1("Send on Invalid Cookie?", "cookie"), defaultValue: true, submitOnChange: true)
            }
            section(sectHead("Code Update Alerts:")) {
                input "sendAppUpdateMsg", sBOOL, title: inTS1("Send for Updates...", "update"), defaultValue: true, submitOnChange: true
                if((Boolean)settings.sendAppUpdateMsg) {
                    input (name: "updNotifyWaitVal", type: sENUM, title: inTS1("Send Reminders After?", "reminder"), description: spanSm("Default: 12 Hours"), required: false, defaultValue: 43200, options: notifValEnum(), submitOnChange: true)
                }
            }
        } else { state.pushTested = false }
    }
}

def setNotificationTimePage() {
    settingRemove("qStartInput")
    settingRemove("qStopInput")
    settingRemove("quietDays")
    settingRemove("quietModes")

    dynamicPage(name: "setNotificationTimePage", title: "Restrict Notifications\nDuring these Times or to these Days or Modes", uninstall: false) {
        String a = getNotifSchedDesc()
        if(a) {
            section() {
                paragraph spanSmBldBr("Restrictions Status:", sCLR4D9) + spanSm(a, sCLR4D9)
                paragraph spanSmBldBr("NOTICE: All selected restrictions must be ${strUnder("INACTIVE")} for notifications to be sent.", sCLRORG)
                paragraph htmlLine()
            }
        }
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

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section(sBLANK) { paragraph spanSmBldBr("This will remove the app, all devices, all actions, all zones.<br>", sCLRRED) + spanSmBld("Please make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove.", sCLRRED) }
    }
}

static String bulletItem(String inStr, String strVal) { return "${inStr == sBLANK ? sBLANK : "\n"} "+ sBULLET + sSPACE + strVal }
static String dashItem(String inStr, String strVal, Boolean newLine=false) { return "${(inStr == sBLANK && !newLine) ? sBLANK : "\n"} - " + strVal }

def deviceTestPage() {
    return dynamicPage(name: "deviceTestPage", uninstall: false, install: false) {
        section(sBLANK) {
            href "speechPage", title: inTS1("Speech Test", "broadcast"), description: inputFooter(sTTC, sCLRGRY, true)
            href "announcePage", title: inTS1("Announcement Test","announcement"), description: inputFooter(sTTC, sCLRGRY, true)
            href "sequencePage", title: inTS1("Sequence Creator Test", "sequence"), description: inputFooter(sTTC, sCLRGRY, true)
        }
    }
}

def speechPage() {
    return dynamicPage(name: "speechPage", uninstall: false, install: false) {
        // if(state.mainMenu) return mainPage()
        section(sBLANK) {
            paragraph pTS("This feature has been known to have issues and may not work because it's not supported by all Alexa devices.  To test each device individually I suggest using the device interface and press Test Speech or Test Announcement")
            Map<String,String> devs = getDeviceList(true, [tts])
            input "test_speechDevices", sENUM, title: inTS1("Select Devices to Test the Speech"), description: inputFooter(sTTS, sCLRGRY, true), options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            if(((List) settings.test_speechDevices)?.size() >= 3) {
                paragraph spanSmBldBr("NOTICE:", sCLRRED) + spanSm("Amazon often rate limits when 3 or more device commands are sent at a time.<br>There may be a delay in the other devices but they should play the test after a few seconds", sCLRRED)
            }
            input "test_speechVolume", "number", title: inTS1("Speak at this volume (0% - 100%)"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
            input "test_speechRestVolume", "number", title: inTS1("Restore to this volume after (0% - 100%)"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
            input "test_speechMessage", "text", title: inTS1("Message to Speak"), defaultValue: "This is a speech test for your Echo speaks device!!!", required: true, submitOnChange: true
        }
        if((List)settings.test_speechDevices) {
            section() {
                input "test_speechRun", sBOOL, title: inTS1("Perform the Speech Test?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_speechRun) { executeSpeechTest() }
            }
        }
        // returnHomeBtn()
    }
}

def alexaRoutinesTestPage() {
    return dynamicPage(name: "alexaRoutinesTestPage", uninstall: false, install: false) {
        Map<String, String> rts = (Map<String,String>)getAlexaRoutines()
        section("Available Routines") {
            if(rts.size()) {
                rts.each { String rk, String rv->
                    String str = sBLANK
                    str += spanBldBr(rv)
                    str += spanSmBld("Routine ID: ") + spanSmBr(rk)
                    paragraph divSm(str, sCLR4D9)
                    input "executeRoutine::${rk}", "button", title: spanSmBld("Run Routine: ", sCLRGRY) + spanSm("(${rv})", sCLRGRY), width: 4
                    paragraph htmlLine()
                }
            } else {
                paragraph divSm("No Routine Data Found...", sCLRGRY)
            }
        }
    }
}

def returnHomeBtn() {
    section {
        paragraph htmlLine()
            input "btnMainMenu", "button", title: "Home Page", width: 3
    }
}

void appButtonHandler(String btn) {
    logDebug("appButton Event Received: $btn")
    switch (btn) {
        case "btnMainMenu":
            state.mainMenu = true
            break

        default:
            if(btn.contains("::")) {
                List items = btn.tokenize("::")
                if(items && items.size() > 1 && items[1]) {
                    String k = (String)items[0]
                    String v = (String)items[1]
                    switch(k) {
                        case "executeRoutine":
                            // log.debug "routine: ${rt[1]}"
                            executeRoutineTest(v)
                            break
                        case "deviceSpeechTest":
                            def childDev = getChildDeviceBySerial(v)
                            if(childDev && childDev?.hasCommand('speechTest')) {
                                logInfo("Sending SpeechTest Command to (${childDev.displayName})")
                                childDev?.speechTest()
                            } else {
                                logError("Speech Test device with Serial# (${v} was not located!!! or does not support speechTest()")
                            }
                            break
                        case "deviceAnnouncementTest":
                            def childDev = getChildDeviceBySerial(v)
                            if(childDev && childDev?.hasCommand('sendTestAnnouncement')) {
                                logInfo("Sending AnnouncementTest Command to (${childDev.displayName})")
                                childDev?.sendTestAnnouncement()
                            } else {
                                logError("Announcement Test device with Serial# (${v} was not located!!! or does not support sendTestAnnouncement()")
                            }
                            break
                    }
                }
            }
            break
        }
}

void executeRoutineTest(String rtId) {
    if(!(rtId && executeRoutineById(rtId)) ) {
        logError("Valid Routine ID not received for Routine Test!!!")
    }
}

def announcePage() {
    return dynamicPage(name: "announcePage", uninstall: false, install: false) {
        section(sBLANK) {
            paragraph pTS("This feature has known to have issues and may not work because it's not supported by all Alexa devices.  To test each device individually I suggest using the device interface and press Test Speech or Test Announcement")
            if(!settings.test_announceDevices) {
                input "test_announceAllDevices", sBOOL, title: inTS1("Test Announcement using All Supported Devices"), defaultValue: false, required: false, submitOnChange: true
            }
            if(!(Boolean)settings.test_announceAllDevices) {
                List devs = getChildDevicesByCap("announce") ?: []
                input "test_announceDevices", sENUM, title: inTS1("Select Devices to Test the Announcement"), description: sTTS, options: (devs?.collectEntries { [(it?.getId()): it?.getLabel() as String] }), multiple: true, required: false, submitOnChange: true
            }
            if((Boolean)settings.test_announceAllDevices || settings.test_announceDevices) {
                input "test_announceVolume", "number", title: inTS1("Announce at this volume"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
                input "test_announceRestVolume", "number", title: inTS1("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
                input "test_announceMessage", "text", title: inTS1("Message to announce"), defaultValue: "This is a test of the Echo speaks announcement system!!!", required: true, submitOnChange: true
            }
        }
        if(settings.test_announceDevices || (Boolean)settings.test_announceAllDevices) {
            section() {
                input "test_announceRun", sBOOL, title: inTS1("Perform the Announcement?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_announceRun) { executeAnnouncement() }
            }
        }
    }
}

@Field final Map<String,Map> seqItemsAvailFLD = [
    other: [
        "weather":sNULL, "traffic":sNULL, "flashbriefing":sNULL, "goodnews":sNULL, "goodmorning":sNULL, "goodnight":sNULL, "cleanup":sNULL,
        "singasong":sNULL, "tellstory":sNULL, "funfact":sNULL, "joke":sNULL, "playsearch":sNULL, "calendartoday":sNULL,
        "calendartomorrow":sNULL, "calendarnext":sNULL, "stop":sNULL, "stopalldevices":sNULL,
        "wait": "value (seconds)", "volume": "value (0-100)", "speak": "message", "announcement": "message",
        "announcementall": "message", "pushnotification": "message", "email": sNULL
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

public Map<String,Map> seqItemsAvail() {
    return seqItemsAvailFLD
}

def sequencePage() {
    return dynamicPage(name: "sequencePage", uninstall: false, install: false) {
        section(sectHead("Command Legend:"), hideable: true, hidden: true) {
            String str1 = "Sequence Options:"
            ((Map<String, String>)seqItemsAvailFLD.other).sort()?.each { String k, String v->
                str1 += "${bulletItem(str1, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            }
            // String str4 = "DoNotDisturb Options:"
            // seqItemsAvailFLD.dnd?.sort()?.each { String k, String v->
                // str4 += "${bulletItem(str4, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            // }
            String str2 = "Music Options:"
            ((Map<String, String>)seqItemsAvailFLD.music).sort()?.each { String k, String v->
                str2 += "${bulletItem(str2, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            }
            String str3 = "Canned TTS Options:"
            ((Map<String, Object>)seqItemsAvailFLD.speech).sort()?.each { String k, v->
                String newV
                if(v instanceof List) { newV = sBLANK; v?.sort()?.each { newV += "     ${dashItem(newV, "${it}", true)}" } }
                else newV=v
                str3 += "${bulletItem(str3, "${k}${newV != sNULL ? "::${newV}" : sBLANK}")}"
            }
            paragraph spanSm(str1, sCLR4D9)
            // paragraph spanSm(str4, sCLR4D9)
            paragraph spanSm(str2, sCLR4D9)
            paragraph spanSm(str3, sCLR4D9)
            paragraph spanSmBldBr("Enter the command in a format exactly like this:") + spanSmBr("volume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30") + spanSm("<br>Each command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.")
        }
        section(sectHead("Sequence Test Config:")) {
            input "test_sequenceDevice", "device.EchoSpeaksDevice", title: inTS1("Select Devices to Test Sequence Command"), description: inputFooter(sTTS, sCLRGRY, true), multiple: false, required: ((String)settings.test_sequenceString != sNULL), submitOnChange: true
            input "test_sequenceString", "text", title: inTS1("Sequence String to Use"), required: ((String)settings?.test_sequenceDevice != sNULL), submitOnChange: true
        }
        if(settings?.test_sequenceDevice && settings?.test_sequenceString) {
            section() {
                input "test_sequenceRun", sBOOL, title: inTS1("Perform the Sequence?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_sequenceRun) { executeSequence() }
            }
        }
    }
}

void executeSpeechTest() {
    settingUpdate("test_speechRun", sFALSE, sBOOL)
    String testMsg = (String)settings.test_speechMessage
    List<String> selectedDevs = (List<String>)settings.test_speechDevices
    selectedDevs?.each { String devSerial->
        def childDev = getChildDeviceBySerial(devSerial)
        if(childDev && childDev?.hasCommand('setVolumeSpeakAndRestore')) {
            childDev?.setVolumeSpeakAndRestore((Integer)settings.test_speechVolume, testMsg, (Integer)settings.test_speechRestVolume )
        } else {
            logError("Speech Test device with serial# (${devSerial} was not located!!! or does not support speakAndRestore")
        }
    }
}

void executeAnnouncement() {
    settingUpdate("test_announceRun", sFALSE, sBOOL)
    String testMsg = (String)settings.test_announceMessage
    List sDevs = (Boolean)settings.test_announceAllDevices ? getChildDevicesByCap("announce") : getDevicesFromList((List)settings.test_announceDevices)
    if(sDevs?.size()) {
        if(sDevs.size() > 1) {
            List<Map> devObj = []
            sDevs.each { devObj.push([deviceTypeId: (String)it?.getEchoDeviceType(), deviceSerialNumber: (String)it?.getEchoSerial()]) }
//            String devJson = new groovy.json.JsonOutput().toJson(devObj)
// send to first one which will have Amazon fan it out
            sDevs[0]?.sendAnnouncementToDevices(testMsg, "Echo Speaks Test", devObj, settings.test_announceVolume ?: null, settings.test_announceRestVolume ?: null)
        } else {
            sDevs[0]?.playAnnouncement(testMsg, "Echo Speaks Test", settings.test_announceVolume ?: null, settings.test_announceRestVolume ?: null)
        }
    }
}

void executeSequence() {
    settingUpdate("test_sequenceRun", sFALSE, sBOOL)
    String seqStr = settings.test_sequenceString
    if(settings.test_sequenceDevice?.hasCommand("executeSequenceCommand")) {
        settings.test_sequenceDevice?.executeSequenceCommand(seqStr)
    } else {
        logWarn("sequence test device doesn't support the executeSequenceCommand command...", true)
    }
}

Map executeTuneInSearch(String query) {
    if(!isAuthValid("executeTuneInSearch")) { return null }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/tunein/search",
        query: [ query: query, mediaOwnerCustomerId: state.deviceOwnerCustomerId ],
        headers: getReqHeaderMap(true),
        requestContentType: sAPPJSON,
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map results = [:]
    try {
        httpGet(params) { resp ->
            if(resp?.status != 200) logWarn("${resp?.status} $params")
            if(resp?.status == 200) {
                results = resp?.data ?: [:]
                updTsVal("lastSpokeToAmazon")
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "executeTuneInSearch")
    }
    return results
}

void executeMusicSearchTest() {
    settingUpdate("test_musicSearchRun", sFALSE, sBOOL)
    if(settings.test_musicDevice && (String)settings.test_musicProvider && (String)settings.test_musicQuery) {
        if(settings.test_musicDevice?.hasCommand("searchMusic")) {
            logDebug("Performing ${(String)settings.test_musicProvider} Search Test with Query: (${(String)settings.test_musicQuery}) on Device: (${settings.test_musicDevice})")
            settings.test_musicDevice.searchMusic((String)settings.test_musicQuery, (String)settings.test_musicProvider)
        } else { logError("The Device ${settings.test_musicDevice} does NOT support the searchMusic() command...") }
    }
}

def musicSearchTestPage() {
    return dynamicPage(name: "musicSearchTestPage", uninstall: false, install: false) {
        section(sectHead("Test a Music Search on Device:")) {
            paragraph spanSm("Use this to test the search you discovered above directly on a device.", sCLR4D9)
            Map testEnum = ["CLOUDPLAYER": "My Library", "AMAZON_MUSIC": "Amazon Music", "I_HEART_RADIO": "iHeartRadio", "PANDORA": "Pandora", "APPLE_MUSIC": "Apple Music", "TUNEIN": "TuneIn", "SIRIUSXM": "siriusXm", "SPOTIFY": "Spotify"]
            input "test_musicProvider", sENUM, title: inTS1("Select Music Provider to perform test", "music"), defaultValue: null, required: false, options: testEnum, multiple: false, submitOnChange: true
            if((String)settings.test_musicProvider) {
                input "test_musicQuery", "text", title: inTS1("Music Search term to test on Device", "search2"), defaultValue: null, required: false, submitOnChange: true
                if((String)settings.test_musicQuery) {
                    input "test_musicDevice", "device.EchoSpeaksDevice", title: inTS1("Select a Device to Test Music Search", "echo_speaks_3.1x"), description: inputFooter(sTTS, sCLRGRY, true), multiple: false, required: false, submitOnChange: true
                    if(settings.test_musicDevice) {
                        input "test_musicSearchRun", sBOOL, title: inTS1("Perform the Music Search Test?", "music"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                        if((Boolean)settings.test_musicSearchRun) { executeMusicSearchTest() }
                    }
                }
            }
        }
        section(sectHead("TuneIn Search Results:")) {
            paragraph spanSm("Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", sCLR4D9)
            input "test_tuneinSearchQuery", "text", title: inTS1("Enter search phrase for TuneIn", "tunein"), defaultValue: sNULL, required: false, submitOnChange: true
            if((String)settings.test_tuneinSearchQuery) {
                href "searchTuneInResultsPage", title: inTS1("View search results!", "search2"), description: inactFoot(sTTP)
            }
        }
    }
}

def searchTuneInResultsPage() {
    return dynamicPage(name: "searchTuneInResultsPage", uninstall: false, install: false) {
        Map results = executeTuneInSearch((String)settings.test_tuneinSearchQuery)
        section(sectHead("Search Results: (Query: ${(String)settings.test_tuneinSearchQuery})")) {
            if(results?.browseList && results?.browseList?.size()) {
                results?.browseList?.eachWithIndex { Map item, Integer i->
                    if(i < 25) {
                        if(item?.browseList != null && item?.browseList?.size()) {
                            item?.browseList?.eachWithIndex { Map item2, i2->
                                dumpBrowseItem(item2)
                            }
                        } else {
                            dumpBrowseItem(item)
                        }
                    }
                }
            } else { paragraph "No Results found..." }
        }
    }
}

def dumpBrowseItem(Map item) {
    String str = sBLANK
    str += "ContentType: (${item.contentType})"
    str += "\nId: (${item.id})"
    str += "\nDescription: ${item.description}"
    String a = (String)item.image ?: sNULL
    String b = ((String)item.name).take(75)
    Boolean c = !item.name?.contains("Not Supported")
    href "searchTuneInResultsPage", title: pTS(b, a, false), description: spanSm(str, c ? sCLR4D9 : sCLRRED)
}

private getChildDeviceBySerial(String serial) {
    List childDevs = app?.getChildDevices()
    def a = childDevs?.find { it?.deviceNetworkId?.tokenize("|")?.contains(serial) }
    return a ?: null
}
/*
public getChildDeviceByCap(String cap) {
    List childDevs = app?.getChildDevices()
    def a= childDevs?.find { it?.currentValue("permissions") && it?.currentValue("permissions")?.toString()?.contains(cap) }
    return a ?: null
}*/

public List getDevicesFromList(List ids) {
    List cDevs = app?.getChildDevices()
    List a = cDevs?.findAll { it?.id in ids }
    return a ?: null
}

public getDeviceFromId(String id) {
    List cDevs = app?.getChildDevices()
    def a = cDevs?.find { it?.id == id }
    return a ?: null
}

public List getChildDevicesByCap(String cap) {
    List childDevs = app?.getChildDevices()
    List a = childDevs?.findAll { it?.currentValue("permissions") && it?.currentValue("permissions")?.toString()?.contains(cap) }
    return a ?: null
}

def donationPage() {
    return dynamicPage(name: "donationPage", title: sBLANK, nextPage: "mainPage", install: false, uninstall: false) {
        section(sBLANK) {
            String str = sBLANK
            str += spanSmBldBr("Hello User,") + spanSmBr("Please forgive the interuption but it's been 30 days since you installed/updated this App and I wanted to present you with this one time reminder that donations are accepted (We do not require them).")
            str += spanSmBr("<br>If you have been enjoying the software and devices please remember that we have spent thousand's of hours of our spare time working on features and stability for those applications and devices.")
            str += spanSmBr("<br>If you have already donated, thank you very much for your support!")
            str += spanSmBr("<br>If you are just not interested in donating please ignore this message")
            str += spanSm("<br>Thanks again for using Echo Speaks")
            paragraph divSm(str, sCLRRED)
            href url: textDonateUrl(), style: sEXTNRL, required: false, title: inTS1("Donations", "donate"), description: inputFooter("Tap to open in browser", sCLRGRY, true)
        }
        updInstData("shownDonation", true)
    }
}

def installed() {
    logInfo("Installed Event Received...")
    state.installData = [initVer: appVersionFLD, dt: getDtNow(), updatedDt: "Not Set", showDonation: false, sentMetrics: false, shownChgLog: true]
    state.isInstalled = true
    sendInstallData()
    initialize()
}

def updated() {
    logInfo("Updated Event Received...")
    if(!(Boolean)state.isInstalled) { state.isInstalled = true }
    if(!state.installData) { state.installData = [initVer: appVersionFLD, dt: getDtNow(), updatedDt: getDtNow(), shownDonation: false, sentMetrics: false] }
    unsubscribe()
    state.clearCnt = 0
    state.zoneEvtsActive = false
    unschedule()
    stateMigrationChk()
    initialize()
}

def initialize() {
    logInfo("running initialize...")
    appCleanup()
    //if(app?.getLabel() != "Echo Speaks") { app?.updateLabel("Echo Speaks") }
    if((Boolean)settings.optOutMetrics && (String)state.appGuid) { if(removeInstallData()) { state.appGuid = sNULL; state.remove('appGuid') } }
    subscribe(location, "systemStart", startHandler)
    if(guardAutoConfigured()) {
        if(settings.guardAwayAlarm && settings.guardHomeAlarm) {
            subscribe(location, "hsmStatus", guardTriggerEvtHandler)
        }
        if(settings.guardAwayModes && settings.guardHomeModes) {
            subscribe(location, "mode", guardTriggerEvtHandler)
        }
        if(settings.guardFollowSwitch || (settings.guardAwaySwitch && settings.guardHomeSwitch)) {
            if(settings.guardFollowSwitch) subscribe(settings.guardFollowSwitch, sSWITCH, guardTriggerEvtHandler)
            if(settings.guardHomeSwitch) subscribe(settings.guardHomeSwitch, sSWITCH, guardTriggerEvtHandler)
            if(settings.guardAwaySwitch) subscribe(settings.guardAwaySwitch, sSWITCH, guardTriggerEvtHandler)
        }
        if(settings.guardAwayPresence) {
            subscribe(settings.guardAwayPresence, "presence", guardTriggerEvtHandler)
        }
    }
    if(!(Boolean)state.resumeConfig) {
        updChildVers()
        updateZoneSubscriptions()
        Boolean a=validateCookie(true)
        if(!(Boolean)state.noAuthActive) {
            //runEvery15Minutes("getOtherData") now part of healthCheck
            runEvery3Hours("getEchoDevices") //This will reload the device list from Amazon
            runIn(11, "postInitialize")
            remTsVal("donotdisturbDt")
            remTsVal("musicProviderUpdDt")

            getOtherData()

            Long newD = now() - 999000
            Date d = new Date(newD)
            updTsVal("lastDevDataUpdDt", formatDt(d)) // make sure not to cause a warning
           // remTsVal("lastDevDataUpdDt") // will force next one to gather EchoDevices
            getEchoDevices()

            if(advLogsActive()) { logsEnabled() }
            runIn(60, "workQB")

        } else { unschedule("getEchoDevices") /*; unschedule("getOtherData") */ }
    }
}

void startHandler(evt){
    logDebug('startHandler called')
    runIn(6, "chkRestartSocket")
}

void chkRestartSocket(){
    def dev= getSocketDevice()
    if(!(Boolean)dev?.isSocketActive()) { dev?.triggerInitialize() }
}

void stateMigrationChk() {
    if(!getAppFlag("stateMapConverted")) { stateMapMigration() }
}

void updateZoneSubscriptions() {
    if(state.zoneEvtsActive != true) {
        subscribe(location, "es3ZoneState", zoneStateHandler)
        subscribe(location, "es3ZoneRemoved", zoneRemovedHandler)
        state.zoneEvtsActive = true
        zoneStatusMapFLD = [:]
        checkZoneData()
        runIn(6, "requestZoneRefresh")
    }
}

void postInitialize() {
    logTrace("postInitialize")
    runEvery15Minutes("healthCheck") // This task checks for missed polls, app updates, code version changes, and cloud service health
    Boolean a = refreshDevCookies(false) // don't have children re-init as it is coming next
    reInitChildren()
}

def uninstalled() {
    log.warn "uninstalling app and devices"
    unschedule()
    if(!(Boolean)settings.optOutMetrics) { if(removeInstallData()) { state.appGuid = sNULL } }
    clearCloudConfig()
    clearCookieData("App Uninstalled", false)
    removeDevices(true)
}

void appCleanup() {
    logTrace("appCleanup")
    List items = [
        "availableDevices", "consecutiveCmdCnt", "isRateLimiting", "versionData", "heartbeatScheduled", "serviceAuthenticated", "cookie", "misPollNotifyWaitVal", "misPollNotifyMsgWaitVal",
        "updNotifyWaitVal", "lastDevActivity", "devSupMap", "tempDevSupData", "devTypeIgnoreData",
        "warnHistory", "errorHistory", "bluetoothData", "dndData", "zoneStatusMap", "guardData", "guardDataSrc", "guardDataOverMaxSize", "lastMsg"
    ]
    items.each { String si-> if(state.containsKey(si)) { state.remove(si)} }

    state.pollBlocked = false
    state.resumeConfig = false
    state.missPollRepair = false
    state.deviceRefreshInProgress = false

    // Settings Cleanup
    List<String> setItems = ["performBroadcast", "stHub", "cookieRefreshDays"]
    settings?.each { si-> ["music", "tunein", "announce", "perform", "broadcast", "sequence", "speech", "test_"].each { String swi-> if(si.key?.startsWith(swi)) { setItems.push(si?.key as String) } } }
    setItems.unique().sort().each { String sI-> if(settings?.containsKey(sI)) { settingRemove(sI) } }
    cleanUpdVerMap()
}

void wsEvtHandler(Map evt) {
    if(devModeFLD) logTrace("wsEvtHandler evt: ${evt}")
    if(evt && /* evt.id && */ (evt.attributes?.size() || evt.triggers?.size())) {
        List<String> trigs = (List<String>)evt.triggers
        Map<String,Object> atts = (Map<String,Object>)evt.attributes
        if("bluetooth" in trigs) { runIn(2, "getBluetoothRunIn") } // getBluetoothDevices(true)
        if("activity" in trigs) { runIn(1, "getDeviceActivityRunIn") } // Map a=getDeviceActivity(sNULL, true)
        if("notification" in trigs) { runIn(2, "getNotificationsRunIn") }
        if((Boolean)evt.all == true) {
            getEsDevices()?.each { eDev->
                atts.each { String k,v-> eDev.sendEvent(name: k, value: v, descriptionText: "ES wsEvt") }
                if(trigs.size()) { eDev.websocketUpdEvt(trigs) }
            }
        } else {
            def eDev = findEchoDevice((String)evt.id)
            if(eDev) {
                atts.each { String k,v-> eDev.sendEvent(name: k, value: v, descriptionText: "ES wsEvt") }
                if(trigs.size()) { eDev.websocketUpdEvt(trigs) }
            }
        }
    }
}

private findEchoDevice(String serial) {
    def a = getEsDevices()?.find { (String)it?.getEchoSerial() == serial }
    return a ?: null
}

Boolean getWWebSocketStatus(){
    return (Boolean)state.websocketActive
}

void webSocketStatus(Boolean active) {
    logTrace "webSocketStatus... | Active: ${active}"
    state.websocketActive = active
    if(active) {
        remTsVal('bluetoothUpdDt')
        remTsVal("donotdisturbDt")
    } // healthcheck will re-read
    runIn(6, "updChildSocketStatus")
}

void updChildSocketStatus() {
    Boolean active = (Boolean)state.websocketActive
    logTrace "updChildSocketStatus... | Active: ${active}"
    getEsDevices()?.each { it?.updSocketStatus(active) }
    getZoneApps()?.each { it?.relayUpdChildSocketStatus(active) }
    updTsVal("lastWebsocketUpdDt")
}

def zoneStateHandler(evt) {
    String id = evt?.value?.toString()
    Map data = evt?.jsonData
    logTrace("zoneStateHandler: ${id} | Data: $data")
    String myId=app.getId()
    checkZoneData()
    if(data && id) {
        Boolean aa = getTheLock(sHMLF, "zoneStateHandler")

        Map t0 = zoneStatusMapFLD[myId]
        Map zoneMap = t0 ?: [:]
        zoneMap[id] = [name: data?.name, active: data?.active, paused: data?.paused, zoneDevices: data.zoneDevices, t: now()]
        zoneStatusMapFLD[myId] = zoneMap
        zoneStatusMapFLD = zoneStatusMapFLD

        releaseTheLock(sHMLF)

        getActionApps()?.each { it?.updZones(zoneMap) }
    }
}

def zoneRemovedHandler(evt) {
    String id = evt?.value?.toString()
    Map data = evt?.jsonData
    logTrace("zone removed: ${id} | Data: $data")
    String myId=app.getId()
    if(data && id) {
        Boolean aa = getTheLock(sHMLF, "zoneRemoveHandler")
        Map t0 = zoneStatusMapFLD[myId]
        Map zoneMap = t0 ?: [:]
        zoneMap = zoneMap ?: [:]
        Boolean fnd=false
        if(zoneMap.containsKey(id)) {
            fnd = true
            zoneMap.remove(id)
            zoneStatusMapFLD[myId] = zoneMap
            zoneStatusMapFLD = zoneStatusMapFLD
        }
        releaseTheLock(sHMLF)
        if(fnd){
            getActionApps()?.each { it?.updZones(zoneMap) }
        }
    }
}

void requestZoneRefresh() {
    sendLocationEvent(name: "es3ZoneRefresh", value: "sendStatus", data: [sendStatus: true], isStateChange: true, display: false, displayed: false)
}

void checkZoneData() {
    String myId=app.getId()
    if(!zoneStatusMapFLD[myId]) {
        Boolean aa = getTheLock(sHMLF, "getZones")

        Map newField = [:]
        List zones = getZoneApps()
        zones?.each { 
            Map zoneMap = it?.myZoneStatus()
            String id = zoneMap.id
            newField[id] = [name: (String)zoneMap.name, active: (Boolean)zoneMap.active, paused: (Boolean)zoneMap.paused, zoneDevices: zoneMap.zoneDevices ?: null, t: now()]
        }
        newField.initialized = [a:true]
        zoneStatusMapFLD[myId] = newField
        zoneStatusMapFLD = zoneStatusMapFLD

        releaseTheLock(sHMLF)

        getActionApps()?.each { it?.updZones(zoneMap) }
    }
}

public Map getZones(Boolean chld=false) {
    String myId=app.getId()
    checkZoneData()
    Map a = zoneStatusMapFLD[myId]

    if(!chld) {
        String i = 'initialized'
        if(a.containsKey(i)) a.remove(i)
    }
    return a
}

Map getActiveZones() {
    Map zones = getZones()
    return zones.size() ? zones.findAll { (Boolean)it?.value?.active && !(Boolean)it?.value?.paused } : [:]
}

Map getInActiveZones() {
    Map zones = getZones()
    return zones.size() ? zones.findAll { !(Boolean)it?.value?.active || (Boolean)it?.value?.paused } : [:]
}

List<String> getActiveZoneNames() {
    Map t0 = getActiveZones()
    return t0.size() ? t0.collect { (String)it?.value?.name } : []
}

List<String> getInActiveZoneNames() {
    Map t0 = getInActiveZones()
    return t0.size() ? t0.collect { (String)it?.value?.name } : []
}


List getZoneApps() {
    return ((List)getAllChildApps())?.findAll { (String)it?.name == zoneChildName() }
}

def getZoneById(String id) {
    return getZoneApps()?.find { it?.id?.toString() == id }
}

List getActiveApps() {
    List acts = getActionApps()
    return acts.size() ? acts.findAll { !(Boolean)it?.isPaused() } : []
}

List getInActiveApps() {
    List acts = getActionApps()
    return acts.size() ? acts.findAll { (Boolean)it?.isPaused() } : []
}

List getMyANames(List acts) {
    acts = acts ?: []
    return acts.size() ? acts.findAll { it }.collect { (String)it?.getLabel() } : []
}

public List getActiveActionNames() {
    return getMyANames(getActiveApps())
}

public List getInActiveActionNames() {
    return getMyANames(getInActiveApps())
}

List getActionApps() {
    return ((List)getAllChildApps())?.findAll { it?.name == actChildName() }
}

List getEsDevices() {
    return ((List)getChildDevices())?.findAll { !(Boolean)it?.isWS() && !(Boolean)it?.isZone() }
}

def getSocketDevice() {
    String myId=app.getId()
    String nmS = 'echoSpeaks_websocket'
    return getChildDevice(myId+'|'+nmS)
}

mappings {
    path("/renderMetricData")           { action: [GET: "renderMetricData"] }
    path("/receiveData")                { action: [POST: "processData"] }
    path("/config")                     { action: [GET: "renderConfig"] }
    path("/textEditor/:cId/:inName")    { action: [GET: "renderTextEditPage", POST: "textEditProcessing"] }
    path("/cookie")                     { action: [GET: "getCookieData", POST: "storeCookieData", DELETE: "clearCookieD"] }
    path("/diagData")                   { action: [GET: "getDiagData"] }
    path("/diagCmds/:cmd")              { action: [GET: "execDiagCmds"] }
    path("/diagDataJson")               { action: [GET: "getDiagDataJson"] }
    path("/diagDataText")               { action: [GET: "getDiagDataText"] }
}

void clearCloudConfig() {
    logTrace("clearCloudConfig called...")
    settingUpdate("resetService", sFALSE, sBOOL)
    unschedule("cloudServiceHeartbeat")
    remServerItem(["onHeroku", "serverHost", "isLocal"])
    settingRemove("useHeroku")
    state.remove("herokuName")
    state.serviceConfigured = false
    state.resumeConfig = true
    clearCookieData("clearCloudConfig", false)
}

String getEnvParamsStr() {
    Map<String, String> envParams = [:]
    envParams["smartThingsUrl"] = getAppEndpointUrl("receiveData")
    envParams["appCallbackUrl"] = getAppEndpointUrl("receiveData")
    envParams["hubPlatform"] = platformFLD
    envParams["useHeroku"] = ((Boolean)settings.useHeroku).toString()
    envParams["serviceDebug"] = sFALSE
    envParams["serviceTrace"] = sFALSE
    envParams["amazonDomain"] = (String)settings.amazonDomain ?: "amazon.com"
    envParams["regionLocale"] = (String)settings.regionLocale ?: "en-US"
    envParams["hostUrl"] = "${getRandAppName()}.herokuapp.com".toString()
    String envs = sBLANK
    envParams.each { String k, String v-> envs += "&env[${k}]=${v}".toString() }
    return envs
}

Boolean checkIfCodeUpdated() {
    Boolean codeUpdated = false
    List chgs = []
    Map codeVerMap = (Map)state.codeVersions ?: [:]
    //if(devModeFLD) logTrace("Code versions: ${codeVerMap}")
    if(codeVerMap.mainApp != appVersionFLD) {
        checkVersionData(true)
        chgs.push("mainApp")
        state.pollBlocked = true
        updCodeVerMap("mainApp", appVersionFLD)
        Map iData = state.installData
        iData = iData ?: [:]
        iData["updatedDt"] = getDtNow()
        iData["shownChgLog"] = false
        if(iData?.shownDonation == null) {
            iData["shownDonation"] = false
        }
        state.installData = iData
        codeUpdated = true
    }
    List cDevs = getEsDevices()
    if(cDevs?.size()) {
        String ver = (String)cDevs[0]?.devVersion()
        if((String)codeVerMap.echoDevice != ver) {
            chgs.push("echoDevice")
            state.pollBlocked = true
            updCodeVerMap("echoDevice", ver)
            codeUpdated = true
        }
    }
    def wsDev = getSocketDevice()
    if(wsDev) {
        String ver = (String)wsDev?.devVersion()
        if((String)codeVerMap.wsDevice != ver) {
            chgs.push("wsDevice")
            updCodeVerMap("wsDevice", ver)
            codeUpdated = true
        }
    }
    List cApps = getActionApps()
    if(cApps?.size()) {
        String ver = (String)cApps[0]?.appVersion()
        if((String)codeVerMap.actionApp != ver) {
            chgs.push("actionApp")
            state.pollBlocked = true
            updCodeVerMap("actionApp", ver)
            codeUpdated = true
        }
    }
    List zApps = getZoneApps()
    if(zApps?.size()) {
        String ver = (String)zApps[0]?.appVersion()
        if((String)codeVerMap.zoneApp != ver) {
            chgs.push("zoneApp")
            state.pollBlocked = true
            // log.debug "zoneVer: ver"
            updCodeVerMap("zoneApp", ver)
            codeUpdated = true
        }
        ver = sNULL
        zApps.each { if(!ver) ver = it?.relayDevVersion() }
        if(ver && (String)codeVerMap.zoneEchoDevice != ver) {
            chgs.push("zoneEchoDevice")
            state.pollBlocked = true
            updCodeVerMap("zoneEchoDevice", ver)
            codeUpdated = true
        }
    }
    if(codeUpdated) {
        logInfo("Code Version Change Detected... | Re-Initializing App in 5 seconds | Changes: ${chgs}")
        runIn(5, "postCodeUpdated", [overwrite: false])
        return true
    } else {
        state.pollBlocked = false
        return false
    }
}

void postCodeUpdated() {
    updated()
    runIn(10, "sendInstallData", [overwrite: false])
}

void resetQueues() {
    getEsDevices()?.each { it?.resetQueue() }
}

void reInitChildren() {
    getEsDevices()?.each { it?.triggerInitialize() }
    getSocketDevice()?.triggerInitialize()
    runIn(11, "reInitChildActions")
}

void reInitChildActions() {
    getActionApps()?.each { it?.triggerInitialize() }
    runIn(3, "reInitChildZones")
}

void reInitChildZones() {
    getZoneApps()?.each { it?.triggerInitialize() }
}

def processData() {
    logTrace("processData() | Data: ${request.JSON}")
    Map data = request?.JSON as Map
    if(data) {
        if(data?.version) {
            updServerItem("onHeroku", (data?.onHeroku != false || (!data?.isLocal && (Boolean)settings.useHeroku)))
            updServerItem("isLocal", (data?.isLocal == true))
            updServerItem("serverHost", ((String)data?.serverUrl ?: sNULL))
            logTrace("processData Received | Version: ${data?.version} | onHeroku: ${data?.onHeroku} | serverUrl: ${data?.serverUrl}")
            updCodeVerMap("server", (String)data?.version)
            state.serviceConfigured = true
        } else { log.debug "data: $data" }
    }
    String json = new JsonOutput().toJson([message: "success", version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}

Boolean serverConfigured() {
    return ((Boolean)getServerItem("onHeroku") || (Boolean)getServerItem("isLocal"))
}

def getCookieData() {
    logTrace("getCookieData Request Received...")
    Map resp = state.cookieData ?: [:]
    String aa = getTsVal("lastCookieRrshDt")
    resp["refreshDt"] = aa ?: null
    String json = new JsonOutput().toJson(resp)
    incrementCntByKey("getCookieCnt")
    render contentType: sAPPJSON, data: json, status: 200
}

Map getCookieMap() {
    return [cookie: getCookieVal(), csrf: getCsrfVal()]
}

Map getReqHeaderMap(Boolean extra=false) {
    Map head = [
        "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
        Referer: "https://alexa.${state.cookieData?.amazonPage}/spa/index.html",
        Origin: "https://alexa.${state.cookieData?.amazonPage}",
        cookie: getCookieVal(),
        csrf: getCsrfVal(),
    ]
    if(extra) return head + [Connection: "keep-alive", DNT: "1"]
    return head
}

String getCookieVal() {
    String myId=app.getId()
    if(! (cookieDataFLD[myId]!=null && cookieDataFLD[myId].localCookie != null)) {
        Map cookieData = state.cookieData
        //if (cookieData && cookieData.localCookie && cookieData.csrf) cookieDataFLD[myId] = cookieData
        cookieDataFLD[myId] = cookieData
        cookieDataFLD = cookieDataFLD
    }
    return cookieDataFLD[myId]?.localCookie ? (String)cookieDataFLD[myId].localCookie : sNULL
}

String getCsrfVal() {
    String myId=app.getId()
    if(! (cookieDataFLD[myId]!=null && cookieDataFLD[myId].csrf != null)) {
        Map cookieData = state.cookieData
        //if (cookieData && cookieData?.localCookie && cookieData?.csrf) { cookieDataFLD[myId] = cookieData; }
        cookieDataFLD[myId] = cookieData
        cookieDataFLD = cookieDataFLD
    }
    return cookieDataFLD[myId]?.csrf ?  (String)cookieDataFLD[myId].csrf : sNULL
}

def storeCookieData() {
    logTrace("storeCookieData Request Received...")
    Map data = request?.JSON as Map
    Map cookieItems = [:]

    if(data) {
        if(data.cookieData) {
            logTrace("cookieData Received: ${request?.JSON?.cookieData?.keySet()}")

            data.cookieData.each { String k,v-> cookieItems[k] = v as String }
            state.cookieData = cookieItems
            String myId=app.getId()
            cookieDataFLD[myId] = cookieItems
            cookieDataFLD = cookieDataFLD
            state.clearCnt = 0
        }

        updServerItem("isLocal", (data.isLocal == true))
        updServerItem("onHeroku", (data.onHeroku != false || (!data?.isLocal && (Boolean)settings.useHeroku)))
        updServerItem("serverHost", ((String)data.serverUrl ?: sNULL))
        updCodeVerMap("server", (String)data.version)
        remTsVal(["lastCookieChkDt", "lastSpokeToAmazon"])
    }

    // log.debug "csrf: ${state.cookieData?.csrf}"
    Boolean a=validateCookie(true)
    if((Boolean)state.authValid && cookieItems.localCookie  && cookieItems.csrf ) {
        logInfo("Cookie data was updated | Reinitializing App... | in 10 seconds...")
        state.serviceConfigured = true
        updTsVal("lastCookieRrshDt")
        checkGuardSupport()
        runIn(10, "initialize", [overwrite: true])
    } else {
        logWarn("Cookie data was updated but found invalid...")
    }

// should be rendering a response?
    String json = new JsonOutput().toJson([message: "success", version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}

def clearCookieD() {
// deal with temporary problems
    Integer a = state.clearCnt
    a = a!= null ? a : 0
    a = a+1
    state.clearCnt = a
    if(a > 5) clearCookieData('webCall', false)
    else logTrace("skipping server call to clearCookieData()")
    String json = new JsonOutput().toJson([message: "success", version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}

def clearCookieData(String src=sNULL, Boolean callSelf=false) {
    logTrace("clearCookieData(${src ?: sBLANK}, $callSelf)")
    settingUpdate("resetCookies", sFALSE, sBOOL)
    if(!callSelf) authEvtHandler(false, "clearCookieData")
    state.remove("cookie")
    state.remove("cookieData")
    String myId=app.getId()
    cookieDataFLD[myId] = [:]
    cookieDataFLD = cookieDataFLD
    state.clearCnt = 0
    remTsVal(["lastCookieChkDt", "lastCookieRrshDt"])
    unschedule("getEchoDevices")
//    unschedule("getOtherData")
    logWarn("Cookie Data has been cleared and Device Data Refreshes have been suspended...")
    updateChildAuth(false)
}

Boolean refreshDevCookies(Boolean doInit=true) {
    logTrace("refreshDevCookies()")
    settingUpdate("refreshDevCookies", sFALSE, sBOOL)
    logDebug("Re-Syncing Cookie Data with Devices")
    Boolean isValid = ((Boolean)state.authValid && getCookieVal() && getCsrfVal())
    updateChildAuth(isValid, doInit)
    return isValid
}

void updateChildAuth(Boolean isValid, Boolean doInit=true) {
    Map cook = getCookieMap()
    ((List)getChildDevices())?.each { (isValid) ? it?.updateCookies(cook, doInit) : it?.removeCookies(true) }
    getZoneApps()?.each { (isValid) ? it?.relayUpdateCookies(cook, doInit) : it?.relayRemoveCookies(true) }
}

@Field volatile static Map<String,List> authValidMapFLD             = [:]

void authValidationEvent(Boolean valid, String src=sNULL) {
    Boolean isValid = valid && getCookieVal() && getCsrfVal()
    Integer listSize = 3
    String myId=app.getId()
    List eList = authValidMapFLD[myId]
    eList = eList ?: [true, true, true]
    eList.push(isValid)
    if(eList.size() > listSize) { eList = eList.drop( eList.size()-listSize ) }
    authValidMapFLD[myId]= eList
    authValidMapFLD[myId]= authValidMapFLD[myId]
    state.authValidHistory = eList
    if(eList.every { it == false }) {
        if(!(Boolean)state.noAuthActive) {
            logError("The last 3 Authentication Validations have failed | Clearing Stored Auth Data | Please login again using the Echo Speaks service...")
        }
        authEvtHandler(false, 'a_v_'+src)
    } else { authEvtHandler(true, 'a_v_'+src) }
}

void authEvtHandler(Boolean isAuth, String src=sNULL) {
    Boolean stC = ((Boolean)state.authValid != isAuth)
    logDebug "authEvtHandler(${isAuth},$src) stateChange: ${stC}"
    state.authValid = isAuth
    state.remove('authEvtClearReason')
    if(!isAuth && !(Boolean)state.noAuthActive) {
        state.noAuthActive = true
        clearCookieData('authHandler', true)
        noAuthReminder()
        if((Boolean)settings.sendCookieInvalidMsg && getLastTsValSecs("lastCookieInvalidMsgDt") > 28800) {
            String loc='Local Server'
            if((Boolean)getServerItem("onHeroku")) loc='Heroku'
            sendMsg("${app.name} Amazon Login Issue", "Amazon Cookie Has Expired or is Missing!!! Please login again using the ${loc} Web Config page...")
            updTsVal("lastCookieInvalidMsgDt")
        }
        logDebug("Scheduling noAuthReminder ${stC}")
        runEvery1Hour("noAuthReminder")
        state.authEvtClearReason = [dt: getDtNow(), src: src]
        updateChildAuth(isAuth)
    } else if(isAuth && (Boolean)state.noAuthActive) {
        unschedule("noAuthReminder")
        state.noAuthActive = false
        logDebug("Scheduling initialize for auth change ${stC}")
        runIn(10, "initialize", [overwrite: true])
    }
}

Boolean isAuthValid(String methodName) {
    if(!(Boolean)state.authValid) {
        logWarn("Echo Speaks Authentication is no longer valid... Please login again and commands will be allowed again!!! | Method: (${methodName})", true)
        return false
    }
    return true
}

void noAuthReminder() {
    String loc='Local Server'
    if((Boolean)getServerItem("onHeroku")) loc='Heroku'
    logWarn("Amazon Cookie Has Expired or is Missing!!! Please login again using the ${loc} Web Config page...")
}

static String toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v?.toString(), "utf-8").replaceAll("\\+", "%20")}" }?.sort()?.join("&")
}

String getServerHostURL() {
    String srvHost = (String)getServerItem("serverHost")
    return ((Boolean)getServerItem("isLocal") && srvHost) ? (srvHost ?: sNULL) : "https://${getRandAppName()}.herokuapp.com".toString()
}

Integer cookieRefreshSeconds() { return ((Integer)settings.refreshCookieDays ?: 5)*86400 as Integer }

void clearServerAuth() {
    logDebug("clearServerAuth: serverUrl: ${getServerHostURL()}")
    Map params = [ uri: getServerHostURL(), path: "/clearAuth", timeout: 20 ]
    Long execDt = now()
    httpGet(params) { resp->
        // log.debug "resp: ${resp.status} | data: ${resp?.data}"
        if(resp?.status != 200) logWarn("clearServerAuth: ${resp?.status} $params")
        if (resp?.status == 200) {
            logInfo("Clear Server Auth Completed... | Process Time: (${execDt ? (now()-execDt) : 0}ms)")
        }
    }
}

void wakeupServer(Boolean c=false, Boolean g=false, String src) {
    Map params = [
        uri: getServerHostURL(),
        path: "/wakeup",
        headers: [wakesrc: src],
        contentType: "text/plain",
        requestContentType: "text/plain",
        timeout: 20
    ]
//    if(!getCookieVal() || !getCsrfVal()) { logWarn("wakeupServer | Cookie or CSRF Missing... Skipping Wakeup"); return; }
    logTrace("wakeupServer $c $g $src")
    execAsyncCmd("post", "wakeupServerResp", params, [execDt: now(), refreshCookie: c, updateGuard: g, wakesrc: src])
}

void runCookieRefresh() {
    logTrace("runCookieRefresh")
    settingUpdate("refreshCookie", sFALSE, sBOOL)
    if(getLastTsValSecs("lastCookieRrshDt", 500000) < 86400) { logError("Cookie Refresh is blocked... | Last refresh was less than 24 hours ago.", true); return }
    wakeupServer(true, false, "runCookieRefresh")
}

def wakeupServerResp(response, data) {
    try {
        if(response?.status != 200) { logWarn("wakeupServerResp: ${response?.status}") }
        def rData = response?.data ?: null
        if(response?.status == 200) {
            updTsVal("lastServerWakeDt")
            if (rData && rData == "OK") {
                logDebug("$rData wakeupServer Completed... | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms) | Source: (${data?.wakesrc}) ${data}")
                if(data?.refreshCookie == true) { runIn(2, "cookieRefresh") }
                if(data?.updateGuard == true) { runIn(2, "checkGuardSupportFromServer") }
            } else {
                logWarn("wakeupServerResp: noData ${rData} ${data}")
            }
        }
    } catch(ex) {
        logError("wakeupServerResp Server may be down / unreachable")
        respExceptionHandler(ex, "wakeupServerResp", false, false)
    }
}

void cookieRefresh() {
    Map cookieData = state.cookieData ?: [:]
    if (!cookieData || !cookieData?.loginCookie || !cookieData?.refreshToken) {
        logError("cookieRefresh: Required Registration data is missing for Cookie Refresh ${cookieData}")
        return
    }
    Map params = [
        uri: getServerHostURL(),
        path: "/refreshCookie",
        contentType: sAPPJSON,
        timeout: 20
    ]
    logTrace("cookieRefresh")
    execAsyncCmd("get", "cookieRefreshResp", params, [execDt: now()])
}

def cookieRefreshResp(response, data) {
    String cMsg
    try {
        if(response?.status != 200) {
            cMsg = "Amazon Cookie Refresh FAILED ${response?.status}"
            logWarn(cMsg)
        } else {
            Map rData = response?.data ? parseJson(response?.data?.toString()) : null
            // log.debug "rData: $rData"
            if (rData && rData?.result && rData?.result?.size()) {
                logInfo("Amazon Cookie Refresh Completed | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms)")
                cMsg = "Amazon Cookie was Refreshed Successfully!!!"
                // log.debug "refreshAlexaCookie Response: ${rData?.result}"
            } else {
                logWarn("Amazon Cookie Refresh Completed with NO DATA ${rData} | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms)")
                cMsg = "Amazon Cookie was Completed with NO DATA"
            }
        }
    } catch(ex) {
        cMsg = "Amazon Cookie FAILED!!! ${ex?.getMessage()}"
        respExceptionHandler(ex, "cookieRefreshResp", false, false)
    }
    if((Boolean)settings.sendCookieRefreshMsg && getLastTsValSecs("lastCookieRfshMsgDt") > 15) {
        sendMsg("${app.name} Cookie Refresh", cMsg)
        updTsVal("lastCookieRfshMsgDt")
    }
}

Boolean validateCookie(Boolean frc=false) {
    Boolean valid = (Boolean)state.authValid
    Integer lastChk = getLastTsValSecs("lastCookieChkDt", 3600)
    Integer lastSpoke = getLastTsValSecs("lastSpokeToAmazon", 3600)
    Boolean cookieOk = getCookieVal() && getCsrfVal()
    if(!frc && valid && lastChk <= 1800 && cookieOk) { return valid }
    if(!frc && valid && lastSpoke <= 1800 && lastChk < 3600 && cookieOk) { return valid }
    if(frc && valid && lastChk <= 60 && cookieOk) { return valid }

    valid = false
    String meth='validateCookie'
    if(!cookieOk) {
        authValidationEvent(valid, meth)
        return valid
    }
    try {
        Long execDt = now()
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/bootstrap",
            query: ["version": 0],
            headers: getReqHeaderMap(true),
            contentType: sAPPJSON,
            timeout: 20,
        ]
        logTrace(meth)
        if(!frc) { execAsyncCmd("get", "validateCookieResp", params, [dt:execDt]) }
        else {
            httpGet(params) { resp->
                valid = validateCookieResp(resp, [dt:execDt])
            }
        }
    } catch(ex) {
        respExceptionHandler(ex, "validateCookie", true)
        incrementCntByKey("err_app_cookieValidCnt")
    }
    return valid
}

Boolean validateCookieResp(resp, data){
    try {
        String meth = 'validCookieResp'
        if(resp?.status != 200) { logWarn("${resp?.status} $meth"); return false }
        if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
        def t0 = resp?.data
        Map aData
        if( t0 instanceof String)  aData = parseJson(resp?.data)
        else aData =  resp?.data
        aData = aData ?: null
        aData = (Map)aData?.authentication ?: null
        if (aData) {
            // log.debug "aData: $aData"
            if(aData.customerId) { state.deviceOwnerCustomerId = aData.customerId }
            if(aData.customerName) { state.customerName = aData.customerName }
            Boolean valid = (aData.authenticated != false)
            authValidationEvent(valid, meth)
            updTsVal("lastCookieChkDt")
            logDebug("Cookie Validation: (${valid}) | Process Time: (${(now()-(Long)data.dt)}ms)")
            return true
       }
    } catch(ex) {
        respExceptionHandler(ex, "validCookieResp", true)
        incrementCntByKey("err_app_cookieValidCnt")
    }
    return false
}
/*
private getCustomerData(Boolean frc=false) {
    try {
        if(!frc && state.amazonCustomerData && getLastTsValSecs("lastCustDataUpdDt") < 3600) { return state.amazonCustomerData }
        if(!isAuthValid("getCustomerData")) { return null }
        Long execDt = now()
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/get-customer-pfm",
            query: ["_": execDt],
            headers: getReqHeaderMap(true),
            contentType: sAPPJSON,
            timeout: 20,
        ]
        logTrace("getCustomerData")
        httpGet(params) { resp->
            if(resp?.status != 200) logWarn("${resp?.status} $params")
            if(resp?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                Map pData = resp?.data ?: null
                if (pData) {
                    Map d = [:]
                    if(pData?.marketPlaceLocale) { d["marketPlaceLocale"] = pData?.marketPlaceLocale }
                    if(pData?.marketPlaceId) { d["marketPlaceId"] = pData?.marketPlaceId }
                    state.amazonCustomerData = d
                    updTsVal("lastCustDataUpdDt")
                    return state.amazonCustomerData
                }
            }
        }
    } catch(ex) {
        respExceptionHandler(ex, "getCustomerData", true)
        updTsVal("lastCustDataUpdDt")
    }
}
*/
/*
private List getAllDeviceVolumes(Boolean frc=false) {
    if(!isAuthValid("getAllDeviceVolumes")) { return [] }
    if(!frc && (List)state.deviceVolumes && getLastTsValSecs("deviceVolumeUpdDt") < 3600) { return (List)state.deviceVolumes }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/devices/deviceType/dsn/audio/v1/allDeviceVolumes",
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    List volumes = []
    try {
        logTrace("getAllDeviceVolumes")
        httpGet(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                Map rData = response?.data ?: [:]
                // log.debug "Device Volumes: ${rData.volumes}"
                state.deviceVolumes = rData && rData.volumes ? rData.volumes : []
                volumes = rData && (List)rData.volumes ? (List)rData.volumes : []
                updTsVal("deviceVolumeUpdDt")
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getAllDeviceVolumes", true)
    }
    return volumes
}
 */

// private getCustomerHistoryRecords(Integer maxRecordSize = 1, Boolean frc) {
//     if(!isAuthValid("getCustomerHistoryRecords")) { return [:] }
//     if(!frc && (Map)state.deviceVolumes && getLastTsValSecs("customerHistoryRecUpdDt") < 3600) { return (Map)state.deviceVolumes }
//     Map params = [
//         uri: getAmazonUrl(),
//         path: "/alexa-privacy/apd/rvh/customer-history-records",
//         query: [
//             startTime: (now() - 24 * 60 * 60 * 1000),
//             endTime: now(),
//             recordType: 'VOICE_HISTORY',
//             maxRecordSize: maxRecordSize
//         ],
//         headers: getReqHeaderMap(), //getCookieMap(),
//         contentType: sAPPJSON,
//         timeout: 20
//     ]
//     Map items = [:]
//     try {
//         logTrace("getCustomerHistoryRecords")
//         httpGet(params) { response ->
//             if(response?.status != 200) logWarn("${response?.status} $params")
//             if(response?.status == 200) {
//             updTsVal("lastSpokeToAmazon")
//             def result = response?.data
//             log.debug "result: $result"

//             List ret = [];
//             if (result.customerHistoryRecords) {
//                 for (List r = 0; r < result.customerHistoryRecords.size(); r++) {
//                     def res = result.customerHistoryRecords[r];
//                     def o = [
//                         data: res
//                     ];
//                     Map convParts = [:];
//                     if (res.voiceHistoryRecordItems && Array.isArray(res.voiceHistoryRecordItems)) {
//                         res.voiceHistoryRecordItems.each { item ->
//                             convParts[item.recordItemType] = convParts[item.recordItemType] || [];
//                             convParts[item.recordItemType].push(item);
//                         };
//                     }

//                     def recordKey = res.recordKey.split('#'); // A3NSX4MMJVG96V#1612297041815#A1RABVCI4QCIKC#G0911W0793360TLG

//                     o.deviceType = recordKey[2] || null;
//                     //o.deviceAccountId = res.sourceDeviceIds[i].deviceAccountId || null;
//                     o.creationTimestamp = res.timestamp || null;
//                     //o.activityStatus = res.activityStatus || null; // DISCARDED_NON_DEVICE_DIRECTED_INTENT, SUCCESS, FAIL, SYSTEM_ABANDONED

//                     o.deviceSerialNumber = recordKey[3];
//                     // if (!this.serialNumbers[o.deviceSerialNumber]) continue;
//                     // o.name = this.serialNumbers[o.deviceSerialNumber].accountName;
//                     // const dev = this.find(o.deviceSerialNumber);
//                     // let wakeWord = (dev && dev.wakeWord) ? dev.wakeWord : null;

//                     // if (convParts.CUSTOMER_TRANSCRIPT) {
//                     //     o.description = {'summary': ''};
//                     //     convParts.CUSTOMER_TRANSCRIPT.forEach(trans => {
//                     //         let text = trans.transcriptText;
//                     //         if (wakeWord && text.startsWith(wakeWord)) {
//                     //             text = text.substr(wakeWord.length).trim();
//                     //         }
//                     //         o.description.summary += text + ', ';
//                     //     });
//                     //     o.description.summary = o.description.summary.substring(0, -2).trim();
//                     // }
//                     // if (convParts.ALEXA_RESPONSE) {
//                     //     o.alexaResponse = '';
//                     //     convParts.ALEXA_RESPONSE.forEach(trans => o.alexaResponse += trans.transcriptText + ', ');
//                     //     o.alexaResponse = o.alexaResponse.substring(0, -2).trim();
//                     // }
//                     // if (!o.description || !o.description.summary.length) continue;
//                     // if (options.filter) {
//                     //     if (res.utteranceType === 'WAKE_WORD_ONLY') {
//                     //         continue;
//                     //     }

//                     //     switch (o.description.summary) {
//                     //         case 'stopp':
//                     //         case 'alexa':
//                     //         case 'echo':
//                     //         case 'computer':
//                     //         case 'amazon':
//                     //         case ',':
//                     //         case '':
//                     //             continue;
//                     //     }
//                     // }

//                     // if (o.description.summary || !options.filter) ret.push(o);
//                 }
//             }
//             }
//         }
//     } catch (ex) {
//         respExceptionHandler(ex, "getCustomerHistoryRecords", true)
//     }
//     return items
// }

/*
private userCommIds() {
    if(!isAuthValid("userCommIds")) { return }
    try {
        Map params = [
            uri: "https://alexa-comms-mobile-service.${getAmazonDomain()}",
            path: "/accounts",
            headers: getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20
        ]
        logTrace("userCommIds")
        httpGet(params) { response->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                List resp = response?.data ?: []
                Map accItems = (resp?.size()) ? resp?.findAll { it?.signedInUser?.toString() == sTRUE }?.collectEntries { [(it?.commsId as String): [firstName: it?.firstName as String, signedInUser: it?.signedInUser, isChild: it?.isChild]]} : [:]
                state.accountCommIds = accItems
                logDebug("Amazon User CommId's: (${accItems})")
            }
        }
    } catch(ex) {
        respExceptionHandler(ex, "userCommIds")
    }
}
*/
public void childInitiatedRefresh() {
    Integer lastRfsh = getLastTsValSecs("lastChildInitRefreshDt", 3600)?.abs()
    if(!(Boolean)state.deviceRefreshInProgress && lastRfsh > 40) {
        updTsVal("lastChildInitRefreshDt")
        logDebug("A Child Device is requesting a Device List Refresh...")
        getOtherData()
        runIn(3, "getEchoDevices1")
    } else {
        if(devModeFLD) logWarn("childInitiatedRefresh request ignored... Refresh already in progress or it's too soon to refresh again | Last Refresh: (${lastRfsh} seconds)")
    }
}

public updChildVers() {
    List cApps = getActionApps()
    List zApps = getZoneApps()
    List eDevs = getEsDevices()
    updCodeVerMap("actionApp", cApps?.size() ? cApps[0]?.appVersion() : null)
    updCodeVerMap("zoneApp", zApps?.size() ? zApps[0]?.appVersion() : null)
    updCodeVerMap("echoDevice", eDevs?.size() ? eDevs[0]?.devVersion() : null)
    def wDevs = getSocketDevice()
    updCodeVerMap("wsDevice", wDevs ? wDevs?.devVersion() : null)
}

Map getMusicProviders(Boolean frc=false) {
    if(!isAuthValid("getMusicProviders")) { return [:] }
    if(!frc && (Map)state.musicProviders && getLastTsValSecs("musicProviderUpdDt") < 7200) { return (Map)state.musicProviders }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/entities",
        query: [ skillId: "amzn1.ask.1p.music" ],
        headers: ["Routines-Version": "1.1.210292" ] + getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map items = [:]
    try {
        logTrace("getMusicProviders")
        httpGet(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                List<Map> rData = (List<Map>)response?.data ?: []
                if(rData.size()) {
                    rData.findAll { it?.availability == "AVAILABLE" && it?.id != "DEFAULT" }?.each { Map item->
                        items[item.id] = (String)item.displayName
                    }
                }
                // log.debug "Music Providers: ${items}"
                state.musicProviders = items
                updTsVal("musicProviderUpdDt")
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getMusicProviders", true)
    }
    return items
}

private getOtherData() {
    stateMigrationChk()
    getDoNotDisturb(true)
    getBluetoothDevices()
    Map aa = getMusicProviders()
    // def bb=getAllDeviceVolumes()
    // getCustomerData()
    // getAlexaSkills()
}

void getNotificationsRunIn(){
    getNotifications(true)
}

// Called by child devices
private List getNotificationList(Boolean frc) {
    getNotifications(frc)
    return (List)state.notifications
}

private void getNotifications(Boolean frc = false) {
    Integer lastU = getLastTsValSecs("notificationsUpdDt")
    if( (frc && lastU < 9)) { return }
    if( (!frc && (Boolean)state.websocketActive && state.notifications && lastU < 10800) ) { return }
    if(!isAuthValid("getNotifications")) { return }
    updTsVal("notificationsUpdDt")
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/notifications",
        query: [cached: true],
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    try {
        logTrace('getNotifications')
        def sData
        List newList = []
        httpGet(params) { response->
            sData = response?.data ?: null
            // log.trace "notifications: $sData"
        }
        if(sData?.size()) {
            Boolean all = true
            List s = ["ON"]
            if(all) s.push("OFF")
            List items = sData.notifications ? sData.notifications.findAll { it.status in s /* && (it.type == type) && it?.deviceSerialNumber == (String)state.serialNumber  */ } : []
            items?.each { item->
                Map li = [:]
                item.keySet()?.each { String key-> if(key in ['id', 'reminderLabel', 'createdDate', 'originalDate', 'originalTime', 'deviceSerialNumber', 'type', 'remainingDuration', 'remainingTime', 'status']) { li[key] = item[key] } }
                newList?.push(li)
            }
        }
        // log.trace "notifications: $newList"
        state.notifications=newList
    } catch (ex) {
        respExceptionHandler(ex, "getNotifications")
    }
}

void getBluetoothRunIn(){
    getBluetoothDevices(true)
}

void getBluetoothDevices(Boolean frc=false) {
    String myId=app.getId()
    Integer lastU = getLastTsValSecs("bluetoothUpdDt")
    if( (frc && lastU < 30)) { return }
    if( (!frc && (Boolean)state.websocketActive && bluetoothDataFLD[myId] && lastU < 10800) ) { return }
    if(!isAuthValid("getBluetoothDevices")) { return }
    updTsVal("bluetoothUpdDt")
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/bluetooth",
        query: [cached: true, _: new Date()?.getTime()],
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
    
    try {
        logTrace("getBluetoothDevices")
        if(!frc) { 
            execAsyncCmd("get", "getBluetoothResp", params, [:]) 
        } else {
            httpGet(params) { response ->
                getBluetoothResp(response, [:])
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getBluetoothDevices", true)
        if(!bluetoothDataFLD[myId]) { bluetoothDataFLD[myId] = [:] }
    }
}

void getBluetoothResp(resp, data) {
    try {
        String meth = 'getBluetoothResp'
        if(resp?.status != 200) logWarn("${resp?.status} $meth")
        if(resp?.status == 200) {
            updTsVal("lastSpokeToAmazon")
            def t0 = resp?.data
            Map btResp
            if (t0 instanceof String) { btResp = parseJson(resp?.data) }
            else { btResp = resp?.data }
            // log.debug "Bluetooth Items: ${btResp}"
            String myId=app.getId()
            bluetoothDataFLD[myId] = btResp
            bluetoothDataFLD=bluetoothDataFLD
            updTsVal("bluetoothUpdDt")
        }
    } catch(ex) {
        respExceptionHandler(ex, "getBluetoothResp", true)
        String myId=app.getId()
        if(!bluetoothDataFLD[myId]) { bluetoothDataFLD[myId] = [:] }
    }
}

Map getBluetoothData(String serialNumber) {
    if(!isAuthValid("getBluetoothData")) { return [btObjs: [:], pairedNames: [], curConnName: sBLANK] }
    String myId=app.getId()
    // logTrace("getBluetoothData: ${serialNumber}")
    String curConnName = sNULL
    Map btObjs = [:]
    getBluetoothDevices(true)
    Map btData = bluetoothDataFLD[myId]
    if(btData == null) {
        bluetoothDataFLD[myId] = [:]
        bluetoothDataFLD=bluetoothDataFLD
        btData = [:]
    }
    Map bluData = btData && btData.bluetoothStates?.size() ? ((List<Map>)btData.bluetoothStates)?.find { it?.deviceSerialNumber == serialNumber } : [:]
    if(bluData && bluData.size() && bluData.pairedDeviceList && bluData.pairedDeviceList?.size()) {
        def bData = bluData.pairedDeviceList.findAll { (it?.deviceClass != "GADGET") }
        bData?.findAll { it?.address != null }?.each {
            btObjs[it?.address as String] = it
            if(it?.connected == true) { curConnName = it?.friendlyName as String }
        }
    }
    List tob = btObjs?.findAll { it?.value?.friendlyName != null }?.collect { it?.value?.friendlyName?.toString()?.replaceAll("\ufffd", sBLANK) }
    return [btObjs: btObjs, pairedNames: tob ?: [], curConnName: curConnName?.replaceAll("\ufffd", sBLANK)]
}

@Field volatile static Map<String,Map> devActivityMapFLD = [:]

void getDeviceActivityRunIn() {
     Map a = getDeviceActivity(sNULL, true)
}

Map getDeviceActivity(String serialNum, Boolean frc=false) {
    if(!isAuthValid("getDeviceActivity")) { return null }
    try {
        Integer lastUpdSec = getLastTsValSecs("lastDevActChk")
        // log.debug "lastUpdSec: $lastUpdSec"
        if((frc && lastUpdSec > 3) || lastUpdSec >= 360) {
            updTsVal("lastDevActChk")
            Long execDt = now()
            Map params = [
                    uri: getAmazonUrl(),
                    path: "/api/activities",
                    query: [ size: 5, offset: 1 ],
                    headers: getReqHeaderMap(true),
                    contentType: sAPPJSON,
                    timeout: 20
            ]
            logTrace("getDeviceActivity($serialNum, $frc)")

            if(!serialNum) execAsyncCmd("get", "getLastActResp", params, [dt:execDt])
            else {
                httpGet(params) { response ->
                    getLastActResp(response, [dt: execDt])
                }
            }
        } else if (!serialNum) runIn(3, "getDeviceActivityRunIn")
        if(serialNum) {
            String appId=app.getId()
            Map lastActData = devActivityMapFLD[appId]
            lastActData = lastActData ?: null
            // log.debug "activityData(IN): $lastActData"
            if(lastActData && (String)lastActData.serialNumber == serialNum) {
                // log.debug "activityData(OUT): $lastActData"
                return lastActData
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getDeviceActivity")
        // log.error "getDeviceActivity error: ${ex.message}"
    }
    return null
}

def getLastActResp(resp, data){
    try {
        String meth = 'getLastActResp'
        if(resp?.status != 200) logWarn("${resp?.status} $meth")
        if(resp?.status == 200) {
            updTsVal("lastSpokeToAmazon")
            def t0 = resp?.data
            Map myResp
            if (t0 instanceof String) { myResp = parseJson(t0) }
            else { myResp = (Map)resp?.data }
            List<Map> act = (List<Map>)myResp?.activities
            if (myResp && act != null) {
                Map lastCommand = act.find {
                    (it?.domainAttributes == null || it?.domainAttributes?.startsWith("{")) &&
                            it?.activityStatus?.equals("SUCCESS") &&
                            (it?.utteranceId?.startsWith(it?.sourceDeviceIds?.deviceType) || it?.utteranceId?.startsWith("Vox:")) &&
                            it?.utteranceId?.contains(it?.sourceDeviceIds?.serialNumber)
                }
                if (lastCommand) {
                    Map lastDescription = (Map) new JsonSlurper().parseText((String)lastCommand.description)
                    def lastDevice = ((List)lastCommand.sourceDeviceIds)?.get(0)
                    Map lastActData = [ serialNumber: lastDevice?.serialNumber, spokenText: lastDescription?.summary, lastSpokenDt: lastCommand.creationTimestamp ]

                    String appId=app.getId()
                    devActivityMapFLD[appId] = lastActData
                    devActivityMapFLD = devActivityMapFLD
                }
            }
        }
        logDebug("getDeviceActivity: Process Time: (${(now()-(Long)data.dt)}ms)")
    } catch(ex) {
        respExceptionHandler(ex, "getLastActResp")
    }
}

void getDoNotDisturb(Boolean frc=true) {
    if(!isAuthValid("getDoNotDisturb")) { return }
    String myId=app.getId()
    Integer lastU = getLastTsValSecs("donotdisturbDt")
    if( (frc && lastU < 20)) { runIn(24, "getDoNotDisturb"); return }
    if( (!frc && (Boolean)state.websocketActive && dndDataFLD[myId] && lastU < 10800) ) { return }
    updTsVal("donotdisturbDt")
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/dnd/device-status-list",
        query: [_: now()],
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20
    ]
//    Map dndResp = [:]
//    String myId=app.getId()
    try {
        logTrace("getDoNotDisturb")
        execAsyncCmd("get", "DnDResp", params, [:])
    } catch (ex) {
        respExceptionHandler(ex, "getDoNotDisturb", true)
        if(!dndDataFLD[myId]) { dndDataFLD[myId] = [:] }
    }
}

void DnDResp(resp, data){
    try {
        String meth = 'DnDResp'
        if(resp?.status != 200) logWarn("${resp?.status} $meth")
        if(resp?.status == 200) {
            updTsVal("lastSpokeToAmazon")
            def t0 = resp?.data
            def dndResp
            if( t0 instanceof String)  dndResp = parseJson(resp?.data)
            else dndResp = resp?.data
//            log.debug "DoNotDisturb Data: ${dndResp}"
            String myId=app.getId()
            dndDataFLD[myId] = (Map)dndResp
            dndDataFLD=dndDataFLD
        }
    } catch(ex) {
        respExceptionHandler(ex, "DnDResp", true)
        String myId=app.getId()
        if(!dndDataFLD[myId]) { dndDataFLD[myId] = [:] }
    }
}

Boolean getDndEnabled(String serialNumber) {
    if(!isAuthValid("getDndEnabled")) { return false }
    String myId=app.getId()
    Map sData = dndDataFLD[myId]
    if(sData == null) {
        getDoNotDisturb()
        sData = dndDataFLD[myId]
        if(sData == null) {
            dndDataFLD[myId] = [:]
            dndDataFLD=dndDataFLD
            sData = [:]
        }
    }
    Map dndData = sData && sData.doNotDisturbDeviceStatusList?.size() ? ((List<Map>)sData.doNotDisturbDeviceStatusList)?.find { it?.deviceSerialNumber == serialNumber } : [:]
    return (dndData && dndData.enabled == true)
}

public Map getAlexaRoutines(String autoId=sNULL) {
    if(!isAuthValid("getAlexaRoutines")) { return [:] }

    String myId=app.getId()
    Integer lastU = getLastTsValSecs("alexaRoutinesUpdDt")
    List<Map> rtList = []
    Map rtResp = [:]

    if(alexaRoutinesDataFLD[myId] && ( (autoId && lastU < 90) || (!autoId && lastU < 180) )) { rtList = alexaRoutinesDataFLD[myId] }
    else {
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/behaviors/v2/automations",
            query: [ limit: 100 ],
            headers: getReqHeaderMap(true),
            contentType: sAPPJSON,
            timeout: 20
        ]

        try {
            logTrace("getAlexaRoutines($autoId)")
            httpGet(params) { response ->
                if(response?.status != 200) logWarn("${response?.status} $params")
                if(response?.status == 200) {
                    rtList = response?.data ?: []
                    updTsVal("lastSpokeToAmazon")
                    updTsVal("alexaRoutinesUpdDt")
                    alexaRoutinesDataFLD[myId] = rtList
                    alexaRoutinesDataFLD=alexaRoutinesDataFLD
                }
            }
        } catch (ex) {
            respExceptionHandler(ex, "getAlexaRoutines", true)
        }
    }

    if(!rtList && alexaRoutinesDataFLD[myId]) { rtList = alexaRoutinesDataFLD[myId] }

    // log.debug "alexaRoutines: $rtList"
    Map items = [:]
    Integer cnt = 1
    if(rtList.size()) {
        if(autoId) {
            rtResp = ((List<Map>)rtList).find { it?.automationId?.toString() == autoId } ?: [:]
            //log.debug "rtResp: ${rtResp}"
            return rtResp
        } else {
            ((List<Map>)rtList).findAll { it?.status == "ENABLED" }?.each { Map item ->
                String myK = item.automationId.toString()
                if(item.name != null) {
                    items[myK] = item.name.toString()
                } else {
                    if(item.triggers?.size()) {
                        item.triggers.each { trg->
                            if(trg.payload?.containsKey("utterance") && trg.payload?.utterance != null) {
                                items[myK] = (String)trg.payload.utterance
                            } else if(trg.type != null) {
                                // log.debug "trg: $trg"
                                String pt = trg.type.toString()
                                if(pt.toLowerCase().contains('guard')) {
                                    items[myK] = "Unlabeled Guard Routine ($cnt)"
                                    cnt++
                                } else {
                                    items[myK] = "Unlabeled Routine ($cnt)"
                                    cnt++
                                }
                            }
                             else {
                                items[myK] = "Unlabeled Routine ($cnt)"
                                cnt++
                            }
                        }
                    }
                }
            }
        }
        rtResp = items
    }

    //log.debug "routines: $rtResp"
    return rtResp
}

/*public getAlexaRoutineByNameOrID(String nameOrId) {
    // TODO: This doesn't work yet...
    Map routines = getAlexaRoutines()
    if(routines.size()) {
        Map match = routines.find { it.name == nameOrId || it.automationId == nameOrId }
        if(match) return match
    }
} */

Boolean executeRoutineById(String routineId) {
    Long execDt = now()
    Map routineData = getAlexaRoutines(routineId)
    //log.debug "routineData: ${routineData.sequence}"
    if(routineData && routineData.sequence) {
        //sendSequenceCommand("ExecuteRoutine", routineData, null)
        List seqList =  []
        seqList.push([command: routineData])
        queueMultiSequenceCommand(seqList, "ExecuteRoutine", false)
        String rtName = routineData && routineData.name ? routineData.name : sBLANK
        logDebug("Queued Alexa Routine | Process Time: (${(now()-execDt)}ms) | Label: ${rtName} | RoutineId: ${routineId}")
        return true
    } else {
        logError("No Routine Data Returned for ID: (${routineId})")
        return false
    }
}

void checkGuardSupport() {
//    Long execDt = now()
    Integer lastUpdSec = getLastTsValSecs("lastGuardSupChkDt")
    if(lastUpdSec < 125 ) {
        if (state.alexaGuardSupported) { getGuardState() }
        return
    }
    if(!isAuthValid("checkGuardSupport")) { return }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix",
        query: [ cached: true, _: new Date().getTime() ],
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    logTrace("checkGuardSupport")
    execAsyncCmd("get", "checkGuardSupportResponse", params, [execDt: now()])
}

void checkGuardSupportResponse(response, data) {
    // log.debug "checkGuardSupportResponse Resp Size(${response?.data?.toString()?.size()})"
    Boolean guardSupported = false
    try {
        if(response?.status != 200) logWarn("${response?.status} checkGuardSupportResponse")
        if(response?.status == 200) {
            updTsVal("lastSpokeToAmazon")
            Integer respLen = response?.data?.toString()?.length() ?: null
            Map resp = response?.data ? parseJson(response?.data?.toString()) : null
            if(resp && resp.networkDetail) {
                Map details = parseJson(resp.networkDetail as String)
                Map locDetails = details?.locationDetails?.locationDetails?.Default_Location?.amazonBridgeDetails?.amazonBridgeDetails["LambdaBridge_AAA/OnGuardSmartHomeBridgeService"] ?: null
                if(locDetails && locDetails.applianceDetails && locDetails.applianceDetails.applianceDetails) {
                    def guardKey = locDetails.applianceDetails.applianceDetails.find { it?.key?.startsWith("AAA_OnGuardSmartHomeBridgeService_") }
                    // TODO could there be multiple Guards?
                    def guardKeys = locDetails.applianceDetails.applianceDetails.findAll { it?.key?.startsWith("AAA_OnGuardSmartHomeBridgeService_") }
                    if(devModeFLD) logTrace("Guardkeys: ${guardKeys.size()}")
                    def guardData = locDetails.applianceDetails.applianceDetails[(String)guardKey?.key]
                    if(devModeFLD) logTrace("Guard: ${guardData}")
                    if(guardData?.modelName == "REDROCK_GUARD_PANEL") {
                        //TODO: we really need to match guardData to devices (and really locations)  ie guard can be on some devices/locations and not on others
                        state.alexaGuardData = [
                                entityId: guardData?.entityId,
                                applianceId: guardData?.applianceId,
                                friendlyName: guardData?.friendlyName,
                        ]
                        guardSupported = true
                    } // else { logDebug("checkGuardSupportResponse | No Guard Data Received Must Not Be Enabled...") }
                }
                state.alexaGuardSupported = guardSupported
                updTsVal("lastGuardSupChkDt")
                state.alexaGuardDataSrc = "app"
                if(guardSupported) getGuardState()
            } else { logError("checkGuardSupportResponse Error | No data received...") }
        }
    } catch (ex) {
        respExceptionHandler(ex, 'checkGuardSupportResponse', true)
    }
}

void checkGuardSupportFromServer() {
    if(!isAuthValid("checkGuardSupportFromServer")) { return }
    Map params = [
        uri: getServerHostURL(),
        path: "/agsData",
        requestContentType: sAPPJSON,
        contentType: sAPPJSON,
        timeout: 20,
    ]
    logTrace("checkGuardSupportFromServer")
    execAsyncCmd("get", "checkGuardSupportServerResponse", params, [execDt: now()])
}

void checkGuardSupportServerResponse(response, data) {
    Boolean guardSupported = false
    try {
        if(response?.status != 200) {
            logWarn("checkGuardSupportServerResp: ${response?.status}")
        } else {
            Map resp = response?.data ? parseJson(response?.data?.toString()) : null
            // log.debug "GuardSupport Server Response: ${resp}"
            if(resp) {
                if(resp.guardData) {
                    logDebug("AGS Server Resp: ${resp?.guardData}")
                    state.alexaGuardData = resp.guardData
                    guardSupported = true
                }
            } else { logError("checkGuardSupportServerResponse Error | No data received..."); return }
            state.alexaGuardSupported = guardSupported
            state.alexaGuardDataOverMaxSize = guardSupported
            state.alexaGuardDataSrc = "server"
            updTsVal("lastGuardSupChkDt")
            if(guardSupported) getGuardState()
        }
    } catch (ex) {
        respExceptionHandler(ex, "checkGuardSupportServerResponse", false, false)
    }
}

void getGuardState() {
    String meth = "getGuardState"
    if(!isAuthValid(meth)) { return }
    if(!(Boolean)state.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix/state",
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20,
        body: [ stateRequests: [ [entityId: state.alexaGuardData?.applianceId, entityType: "APPLIANCE" ] ] ]
    ]
    try {
        logTrace(meth)
        httpPostJson(params) { resp ->
            if(resp?.status != 200) logWarn("${resp?.status} "+meth)
            if(resp?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                Map respData = resp?.data ?: null

                if(devModeFLD) log.debug "GuardState resp: ${respData}"

                if(respData && respData.deviceStates && ((List)respData.deviceStates)[0] && ((List)respData.deviceStates)[0].capabilityStates) {
                    def guardStateData = parseJson(((List)respData.deviceStates)[0].capabilityStates as String)
                    if(devModeFLD) logTrace("guardState: ${guardStateData}")
                    String curState = (String)state.alexaGuardState ?: sNULL
                    state.alexaGuardState = ((List)guardStateData?.value)[0] ? (String)((List)guardStateData?.value)[0] : (String)guardStateData?.value
                    settingUpdate("alexaGuardAwayToggle", (((String)state.alexaGuardState == sARM_AWAY) ? sTRUE : sFALSE), sBOOL)
                    logDebug("Alexa Guard State: (${(String)state.alexaGuardState})")
                    if(curState != (String)state.alexaGuardState) updGuardActionTrig()
                    updTsVal("lastGuardStateChkDt")
                }
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, meth, true)
    }
}

void setGuardState(String guardState) {
    Long execTime = now()
    String meth = "setGuardState"
    if(!isAuthValid("setGuardState")) { return }
    if(!(Boolean)state.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return }
    guardState = guardStateConv(guardState)
    logDebug("setAlexaGuard($guardState)")
    try {
        String body = new JsonOutput()?.toJson([ controlRequests: [ [ entityId: state.alexaGuardData?.applianceId as String, entityType: "APPLIANCE", parameters: [action: "controlSecurityPanel", armState: guardState ] ] ] ])
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/phoenix/state",
            headers: getReqHeaderMap(),//getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20,
            body: body
        ]
        logTrace(meth)
        httpPutJson(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params $meth")
            if(response?.status == 200) {
                updTsVal("lastSpokeToAmazon")
                def resp = response?.data ?: null
                if(resp && !resp.errors?.size() && resp.controlResponses && ((List)resp.controlResponses)[0] && ((List)resp.controlResponses)[0].code && (String)((List)resp.controlResponses)[0].code == "SUCCESS") {
                    logInfo("Alexa Guard set to (${guardState}) Successfully | (${(now()-execTime)}ms)")
                    state.alexaGuardState = guardState
                    updTsVal("lastGuardStateUpdDt")
                    updGuardActionTrig()
                } else { logError("Failed to set Alexa Guard to (${guardState}) | Reason: ${resp?.errors ?: null}") }
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, meth, true)
    }
}

// private getAlexaSkills() {
//     Long execDt = now()
//     if(!isAuthValid("getAlexaSkills") || !getCustomerData()) { return } // state.amazonCustomerData
//     if(state.skillDataMap && getLastTsValSecs("skillDataUpdDt") < 3600) { return }
//     Map params = [
//         uri: "https://skills-store.${getAmazonDomain()}",
//         path: "/app/secure/your-skills-page?deviceType=app&ref-suffix=evt_sv_ub&pfm=${state.amazonCustomerData?.marketPlaceId}&cor=US&lang=en-us&_=${now()}",
//         headers: [
//             Accept: "application/vnd+amazon.uitoolkit+json;ns=1;fl=0",
//             Origin: getAmazonUrl()
//         ] + getReqHeaderMap(true),
//         contentType: sAPPJSON,
//         timeout: 20,
//     ]
//     try {
//         logTrace("getAlexaSkills")
//         httpGet(params) { response->
//             if(response?.status != 200) logWarn("${response?.status} $params")
//             if(response?.status == 200) {
//             updTsVal("lastSpokeToAmazon")
//             def respData = response?.data ?: null
//             log.debug "respData: $respData"
//             // log.debug respData[3]?.contents[3]?.contents?.products

//             // updTsVal("skillDataUpdDt")
//         }
//         }
//     } catch (ex) {
//         log.error "getAlexaSkills Exception: ${ex}"
//         // respExceptionHandler(ex, "getAlexaSkills", true)
//     }
// }

void respExceptionHandler(ex, String mName, Boolean ignOn401=false, Boolean toAmazon=true, Boolean ignNullMsg=false) {
    String toMsg = "Amazon"
    if(!toAmazon) { toMsg = "Echo Speaks Server" }
    String stackTr
    if(ex) {
        try {
            stackTr = getStackTrace(ex)
        } catch (ignored) {
        }
        if(stackTr) logError("${mName} | Stack Trace: "+stackTr)
    }
    if(ex instanceof groovyx.net.http.HttpResponseException ) {
        Integer sCode = ex?.getResponse()?.getStatus()
        String errMsg = ex?.getMessage()
        if(sCode == 401) {
            if(ignOn401) authValidationEvent(false, "${mName}_${sCode}")
        } else if (sCode in [400, 429]) {
            String respMsgLow = errMsg ? errMsg.toLowerCase() : sNULL
            if((sCode in [400, 429]) && respMsgLow) { // && (respMsgLow in ["rate exceeded", "too many requests"])) {
                switch(respMsgLow) {
                    case "rate exceeded":
                        logWarn("You've been rate-limited by Amazon for sending too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                        break
                    case "too many requests":
                        logError("${mName} | ${toMsg} is currently rate-limiting your requests | Msg: ${errMsg}")
                        break
                    case "bad request":
                        logError("${mName} | Improperly formatted request sent to ${toMsg} | Msg: ${errMsg}")
                        break
                    default:
                        logError("${mName} | 400 Error | Msg: ${errMsg}")
                        break
                }
            }
        } else {
            logError("${mName} | Response Exception | Status: (${sCode}) | Msg: ${errMsg}")
        }
    } else if(ex instanceof java.net.SocketTimeoutException) {
        logError("${mName} | Response Socket Timeout (Possibly an ${toMsg} Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof org.apache.http.conn.ConnectTimeoutException) {
        logError("${mName} | Request Timeout | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.UnknownHostException) {
        logError("${mName} | HostName Not Found (Possibly an ${toMsg}/Internet Issue) | Msg: ${ex?.getMessage()}")
    } else if(ex instanceof java.net.NoRouteToHostException) {
        logError("${mName} | No Route to Connection (Possibly a Local Internet Issue) | Msg: ${ex?.getMessage()}")
    } else { logError("${mName} Exception: ${ex}") }
}

static String guardStateConv(String gState) {
    switch(gState) {
        case "disarm":
        case "off":
        case "stay":
        case "home":
        case sARM_STAY:
            return sARM_STAY
        case "away":
        case sARM_AWAY:
            return sARM_AWAY
        default:
            return sARM_STAY
    }
}

String getAlexaGuardStatus() {
    return (String)state.alexaGuardState ?: sNULL
}

Boolean getAlexaGuardSupported() {
    return (Boolean)state.alexaGuardSupported
}

public void updGuardActionTrig() {
    List acts = getActionApps()
    if(acts?.size()) { acts?.each { aa-> aa?.guardEventHandler((String)state.alexaGuardState) } }
}

public setGuardHome() {
    setGuardState(sARM_STAY)
}

public setGuardAway() {
    setGuardState(sARM_AWAY)
    guardArmPendingFLD = false
}

Map isFamilyAllowed(String family) {
    Map famMap = getDeviceFamilyMap()
    if(family in famMap?.block) { return [ok: false, reason: "Family Blocked"] }
    if(family in famMap?.echo) { return [ok: true, reason: "Amazon Echos Allowed"] }
    if(family in famMap?.tablet) {
        if((Boolean)settings.createTablets) { return [ok: true, reason: "Tablets Enabled"] }
        return [ok: false, reason: "Tablets Not Enabled"]
    }
    if(family in famMap?.wha) {
        if((Boolean)settings.createWHA) { return [ok: true, reason: "WHA Enabled"] }
        return [ok: false, reason: "WHA Devices Not Enabled"]
    }
    if((Boolean)settings.createOtherDevices) {
        return [ok: true, reason: "Other Devices Enabled"]
    } else { return [ok: false, reason: "Other Devices Not Enabled"] }
//    return [ok: false, reason: "Unknown Reason"]
}

void getEchoDevices1() {
    getEchoDevices()
}

void getEchoDevices(Boolean lazy=false) {
    stateMigrationChk()
    if(!isAuthValid("getEchoDevices")) { return }
    String myId=app.getId()
    Integer lastUpd = getLastTsValSecs("lastDevDataUpdDt")
    if(echoDeviceMapFLD[myId] && (!lazy && lastUpd < 20) || (lazy && lastUpd <= 600)) { return }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device",
        query: [ cached: true, _: new Date().getTime() ],
        headers: getReqHeaderMap(true),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    state.deviceRefreshInProgress = true
//    state.refreshDeviceData = false
    logTrace("getEchoDevices")
    execAsyncCmd("get", "echoDevicesResponse", params, [execDt: now()])
}

void echoDevicesResponse(response, data) {
    List<String> ignoreTypes = getDeviceIgnoreData()
    List<String> removeKeys = ["appDeviceList", "charging", "macAddress", "deviceTypeFriendlyName", "registrationId", "remainingBatteryLevel", "postalCode", "language"]
    List<String> removeCaps = [
        "SUPPORTS_CONNECTED_HOME", "SUPPORTS_CONNECTED_HOME_ALL", "SUPPORTS_CONNECTED_HOME_CLOUD_ONLY", "ALLOW_LOG_UPLOAD", "FACTORY_RESET_DEVICE", "DIALOG_INTERFACE_VERSION",
        "SUPPORTS_SOFTWARE_VERSION", "REQUIRES_OOBE_FOR_SETUP", "DEREGISTER DEVICE", "PAIR_REMOTE", "SET_LOCALE", "DEREGISTER_FACTORY_RESET"
    ]
    try {
        if(response?.status != 200) logWarn("${response?.status} $data")
        if(response?.status == 200) {
            updTsVal("lastSpokeToAmazon")
            // log.debug "json response is: ${response.json}"
            state.deviceRefreshInProgress=false
            List eDevData = response?.json?.devices ?: []
            Map echoDevices = [:]
            if(eDevData.size()) {
                eDevData.each { eDevice->
                    if (!((String)eDevice.deviceType in ignoreTypes) && !((String)eDevice.accountName)?.startsWith("This Device")) {
                        removeKeys.each { rk-> eDevice.remove(rk) }
                        eDevice.capabilities = eDevice.capabilities?.findAll { !(it in removeCaps) }?.collect { it as String }
                        //if (eDevice.deviceOwnerCustomerId != null) { state.deviceOwnerCustomerId = eDevice.deviceOwnerCustomerId }
                        echoDevices[(String)eDevice.serialNumber] = eDevice
                    }
                }
            }
            // log.debug "echoDevices: ${echoDevices}"
            Map musicProvs = (Map)state.musicProviders ?: getMusicProviders(true)
            receiveEventData([echoDevices: echoDevices, musicProviders: musicProvs, execDt: data?.execDt], "Groovy")
        }

    } catch (ex) {
        respExceptionHandler(ex, "echoDevicesResponse")
    }
}

private static List<String> getDeviceIgnoreData() {
    Map<String,Map> dData = (Map<String,Map>)deviceSupportMapFLD.types
    if(dData.size()) {
        List o = dData.findAll { it.value.ig == true }?.collect { it.key }
        // log.debug "devTypeIgnoreData: ${o}"
        return o
    }
    return []
}

List getUnknownDevices() {
    List items = []
    ((List<Map>)state.unknownDevices)?.each { Map it->
        it.description = "What kind of device/model?(PLEASE UPDATE THIS)"
        if(items.size() < 5) items.push(it)
    }
    return items
}

void receiveEventData(Map evtData, String src) {
    try {
        if(checkIfCodeUpdated()) {
            logWarn("Possible Code Version Change Detected... Device Updates will occur on next cycle.")
            return
        }
        // log.debug "musicProviders: ${evtData?.musicProviders}"
        logTrace("evtData(Keys): ${evtData?.keySet()}")
        if (evtData?.keySet()?.size()) {
            //List ignoreTheseDevs = settings.echoDeviceFilter ?: []
            //Boolean onHeroku = ((Boolean)getServerItem("onHeroku") && !(Boolean)getServerItem("isLocal"))

            //Check for minimum versions before processing
            Map updReqMap = getMinVerUpdsRequired()
            Boolean updRequired = updReqMap?.updRequired
            List updRequiredItems = updReqMap?.updItems

            String myId=app.getId()
            String wsChildHandlerName = "Echo Speaks WS"
            String nmS = 'echoSpeaks_websocket'
            def oldWsDev = getChildDevice(nmS)
            if(oldWsDev) { deleteChildDevice(nmS) }
            nmS = myId+'|'+nmS
            def wsDevice = getChildDevice(nmS)
            if(!wsDevice) { def a=addChildDevice("tonesto7", wsChildHandlerName, nmS, null, [name: wsChildHandlerName, label: "Echo Speaks - WebSocket", completedSetup: true]) }
            updCodeVerMap("echoDeviceWs", (String)wsDevice?.devVersion())

            if (evtData?.echoDevices?.size()) {
                Long execTime = evtData?.execDt ? (now()-(Long)evtData.execDt) : 0L
                Map<String, Map> echoDeviceMap = [:]
                Map<String, Map> allEchoDevices = [:]
                Map<String, Map> skippedDevices = [:]
                List unknownDevices = []
                List curDevFamily = []
//                Integer cnt = 0
                String devAcctId = sNULL
                evtData?.echoDevices?.each { String echoKey, echoValue->
                    devAcctId = echoValue?.deviceAccountId
                    logTrace("echoDevice | $echoKey | ${echoValue}")
                    // logDebug("echoDevice | ${echoValue?.accountName}", false)
                    allEchoDevices[echoKey] = [name: echoValue?.accountName]
                    // log.debug "name: ${echoValue?.accountName}"
                    Map familyAllowed = isFamilyAllowed(echoValue?.deviceFamily as String)
                    Map deviceStyleData = getDeviceStyle(echoValue?.deviceFamily as String, echoValue?.deviceType as String)
                    // log.debug "deviceStyle: ${deviceStyleData}"
                    Boolean isBlocked = ((Boolean)deviceStyleData?.b || (String)familyAllowed?.reason == "Family Blocked")
                    Boolean isInIgnoreInput = (echoValue?.serialNumber in settings.echoDeviceFilter)
                    Boolean allowTTS = (deviceStyleData?.c && ((List)deviceStyleData.c)?.contains(sTSTR))
                    Boolean isMediaPlayer = (echoValue?.capabilities?.contains("AUDIO_PLAYER") || echoValue?.capabilities?.contains("AMAZON_MUSIC") || echoValue?.capabilities?.contains("TUNE_IN") || echoValue?.capabilities?.contains("PANDORA") || echoValue?.capabilities?.contains("I_HEART_RADIO") || echoValue?.capabilities?.contains("SPOTIFY"))
                    Boolean volumeSupport = (echoValue?.capabilities?.contains("VOLUME_SETTING"))
                    Boolean unsupportedDevice = ((!(Boolean)familyAllowed?.ok && (String)familyAllowed?.reason == "Unknown Reason") || isBlocked)
                    Boolean bypassBlock = ((Boolean)settings.bypassDeviceBlocks && !isInIgnoreInput)

                    if(!bypassBlock && (!(Boolean)familyAllowed?.ok || isBlocked || (!allowTTS && !isMediaPlayer) || isInIgnoreInput)) {
                        logTrace("familyAllowed(${echoValue?.deviceFamily}): ${(Boolean)familyAllowed?.ok} | Reason: ${(String)familyAllowed?.reason} | isBlocked: ${isBlocked} | deviceType: ${echoValue?.deviceType} | tts: ${allowTTS} | volume: ${volumeSupport} | mediaPlayer: ${isMediaPlayer}")
                        if(!skippedDevices.containsKey(echoValue?.serialNumber as String)) {
                            List reasons = []
                            if((Boolean)deviceStyleData?.b) {
                                reasons.push("Device Blocked by App Config")
                            } else if((String)familyAllowed?.reason == "Family Blocked") {
                                reasons.push("Family Blocked by App Config")
                            } else if (!(Boolean)familyAllowed?.ok) {
                                reasons.push((String)familyAllowed?.reason)
                            } else if(isInIgnoreInput) {
                                reasons.push(sIN_IGNORE)
                                logDebug("skipping ${echoValue?.accountName} because it is in the do not use list...")
                            } else {
                                if(!allowTTS) { reasons.push("No TTS") }
                                if(!isMediaPlayer) { reasons.push("No Media Controls") }
                            }
                            skippedDevices[echoValue?.serialNumber as String] = [
                                name: echoValue?.accountName, desc: (String)deviceStyleData?.n, image: (String)deviceStyleData?.i, family: echoValue?.deviceFamily,
                                type: echoValue?.deviceType, tts: allowTTS, volume: volumeSupport, mediaPlayer: isMediaPlayer, reason: reasons?.join(", "),
                                online: echoValue?.online
                            ]
                        }
                        return
                    }
                    // if(isBypassBlock && familyAllowed?.reason == "Family Blocked" || isBlocked == true) { return }

                    echoValue["unsupported"] = (unsupportedDevice)
                    echoValue["authValid"] = (Boolean)state.authValid
                    echoValue["amazonDomain"] = (settings.amazonDomain ?: "amazon.com")
                    echoValue["regionLocale"] = (settings.regionLocale ?: "en-US")
                    echoValue["cookie"] = getCookieMap()
                    echoValue["deviceAccountId"] = echoValue?.deviceAccountId as String ?: sNULL
                    echoValue["deviceStyle"] = deviceStyleData
                    // log.debug "deviceStyle: ${echoValue?.deviceStyle}"

                    Map<String, Object> permissions = [:]
                    permissions["TTS"] = allowTTS
                    permissions["announce"] = (deviceStyleData?.c && ((List)deviceStyleData.c)?.contains(sASTR))
                    permissions["volumeControl"] = volumeSupport
                    permissions["mediaPlayer"] = isMediaPlayer
                    permissions["amazonMusic"] = (echoValue.capabilities?.contains("AMAZON_MUSIC"))
                    permissions["tuneInRadio"] = (echoValue.capabilities?.contains("TUNE_IN"))
                    permissions["iHeartRadio"] = (echoValue.capabilities?.contains("I_HEART_RADIO"))
                    permissions["pandoraRadio"] = (echoValue.capabilities?.contains("PANDORA"))
                    permissions["appleMusic"] = (((Map)evtData.musicProviders).containsKey("APPLE_MUSIC"))
                    permissions["siriusXm"] = (((Map)evtData.musicProviders).containsKey("SIRIUSXM"))
                    // permissions["tidal"] = true
                    permissions["spotify"] = true //(echoValue?.capabilities.contains("SPOTIFY")) // Temporarily removed restriction check
                    permissions["isMultiroomDevice"] = (echoValue.clusterMembers && echoValue.clusterMembers?.size() > 0) ?: false
                    permissions["isMultiroomMember"] = (echoValue.parentClusters && echoValue.parentClusters?.size() > 0) ?: false
                    permissions["alarms"] = (echoValue.capabilities?.contains("TIMERS_AND_ALARMS"))
                    permissions["reminders"] = (echoValue.capabilities?.contains("REMINDERS"))
                    permissions["doNotDisturb"] = (echoValue.capabilities?.contains("SLEEP"))
                    permissions["wakeWord"] = (echoValue.capabilities?.contains("FAR_FIELD_WAKE_WORD"))
                    permissions["flashBriefing"] = (echoValue.capabilities?.contains("FLASH_BRIEFING"))
                    permissions["microphone"] = (echoValue.capabilities?.contains("MICROPHONE"))
                    permissions["followUpMode"] = (echoValue.capabilities?.contains("GOLDFISH"))
                    permissions["connectedHome"] = (echoValue.capabilities?.contains("SUPPORTS_CONNECTED_HOME"))
                    permissions["bluetoothControl"] = (echoValue.capabilities?.contains("PAIR_BT_SOURCE") || echoValue.capabilities?.contains("PAIR_BT_SINK"))
                    permissions["guardSupported"] = (echoValue.capabilities?.contains("TUPLE"))
                    permissions["isEchoDevice"] = (echoValue.deviceFamily in (List)deviceSupportMapFLD.families.echo)
                    echoValue["guardStatus"] = ((Boolean)state.alexaGuardSupported && (String)state.alexaGuardState) ? (String)state.alexaGuardState : ((Boolean)permissions.guardSupported ? sUnknown : "Not Supported")
                    echoValue["musicProviders"] = (Map)evtData.musicProviders
                    echoValue["permissionMap"] = permissions
                    echoValue["hasClusterMembers"] = (echoValue.clusterMembers && echoValue.clusterMembers?.size() > 0) ?: false

                    if(deviceStyleData?.n?.toString()?.toLowerCase()?.contains(sUNKNOWN)) {
                        unknownDevices.push([
                            name: echoValue.accountName,
                            family: echoValue.deviceFamily,
                            type: echoValue.deviceType,
                            permissions: permissions.findAll {it?.value == true}?.collect {it?.key as String}?.join(", ")?.toString()
                        ])
                    }
                    // echoValue["mainAccountCommsId"] = state.accountCommIds?.find { it?.value?.signedInUser == true && it?.value?.isChild == false }?.key as String ?: null
                    // logWarn("Device Permisions | Name: ${echoValue?.accountName} | $permissions")
                    echoDeviceMap[echoKey] = [
                        name: echoValue.accountName,
                        online: echoValue.online,
                        family: echoValue.deviceFamily,
                        serialNumber: echoKey,
                        style: echoValue.deviceStyle,
                        type: echoValue.deviceType,
                        mediaPlayer: isMediaPlayer,
                        announceSupport: (Boolean)permissions.announce,
                        ttsSupport: allowTTS,
                        volumeSupport: volumeSupport,
                        clusterMembers: echoValue.clusterMembers,
                        musicProviders: ((Map)evtData.musicProviders)?.collect{ it?.value }?.sort()?.join(", "),
                        supported: (!unsupportedDevice)
                    ]

                    String dni = [app?.id, "echoSpeaks", echoKey].join("|")
                    def childDevice = getChildDevice(dni)
                    String devLabel = "${(Boolean)settings.addEchoNamePrefix ? "Echo - " : sBLANK}${echoValue?.accountName}${echoValue?.deviceFamily == "WHA" ? " (WHA)" : sBLANK}"
                    String childHandlerName = "Echo Speaks Device"
                    Boolean autoRename = ((Boolean)settings.autoRenameDevices)
                    if (!childDevice) {
                        // log.debug "childDevice not found | autoCreateDevices: ${settings.autoCreateDevices}"
                        if((Boolean)settings.autoCreateDevices) {
                            try{
                                logInfo("Creating NEW Echo Speaks Device!!! | Device Label: ($devLabel)${((Boolean)settings.bypassDeviceBlocks && unsupportedDevice) ? " | (UNSUPPORTED DEVICE)" : sBLANK }")
                                childDevice = addChildDevice("tonesto7", childHandlerName, dni, null, [name: childHandlerName, label: devLabel, completedSetup: true])
                            } catch(ex) {
                                logError("AddDevice Error! | ${ex}", false, ex)
                            }
                            runIn(10, "updChildSocketStatus")
                        } else {
                            logInfo("Found NEW Echo Speaks Device, but not creating HE device due to settings | Device Label: ($devLabel)${((Boolean)settings.bypassDeviceBlocks && unsupportedDevice) ? " | (UNSUPPORTED DEVICE)" : sBLANK}")
                        }
                    }
                    if(childDevice) {
                        //Check and see if name needs a refresh
                        String curLbl = (String)childDevice?.getLabel()
                        if(autoRename && (String)childDevice?.name != childHandlerName) { childDevice.name = childHandlerName }
                        // log.debug "curLbl: ${curLbl} | newLbl: ${devLabel} | autoRename: ${autoRename}"
                        if(autoRename && (curLbl != devLabel)) {
                            logDebug("Amazon Device Name Change Detected... Updating Device Name to (${devLabel}) | Old Name: (${curLbl})")
                            childDevice?.setLabel(devLabel as String)
                        }
                        // logInfo("Sending Device Data Update to ${devLabel} | Last Updated (${getLastTsValSecs("lastDevDataUpdDt")}sec ago)")
                        childDevice?.updateDeviceStatus(echoValue)
                        updCodeVerMap("echoDevice", (String)childDevice?.devVersion()) // Update device versions in codeVersions state Map
                    }
                    curDevFamily.push((String)echoValue.deviceStyle?.n)
                }
                logDebug("Device Data Received and Updated for (${echoDeviceMap?.size()}) Alexa Devices | Took: (${execTime}ms) | Last Refreshed: (${(getLastTsValSecs("lastDevDataUpdDt")/60).toFloat()?.round(1)} minutes)")
                updTsVal("lastDevDataUpdDt")
                state.echoDeviceMap = echoDeviceMap
                echoDeviceMapFLD[myId] = echoDeviceMap
                echoDeviceMapFLD = echoDeviceMapFLD
                state.allEchoDevices = allEchoDevices
                state.skippedDevices = skippedDevices
                state.deviceStyleCnts = curDevFamily.countBy { it }
                state.unknownDevices = unknownDevices
            } else {
                log.warn "No Echo Device Data Sent... This may be the first transmission from the service after it started up!"
            }
            state.remove("tempDevSupData")
            if(updRequired) {
                logWarn("CODE UPDATES REQUIRED: Echo Speaks Integration may not function until the following items are ALL Updated ${updRequiredItems}...")
                appUpdateNotify()
            }
            if(!(Boolean)getInstData('sentMetrics')) { runIn(900, "sendInstallData", [overwrite: false]) }
        }
    } catch(ex) {
        logError("receiveEventData Error: ${ex}", false, ex)
        incrementCntByKey("appErrorCnt")
    }
}

Map<String,Map> getEchoDeviceMap(){
    String myId=app.getId()
    if(!echoDeviceMapFLD[myId]) {
        echoDeviceMapFLD[myId] = (Map)state.echoDeviceMap
        echoDeviceMapFLD = echoDeviceMapFLD
    }
    return echoDeviceMapFLD[myId]
}

public static Map minVersions() {
    return minVersionsFLD
}

private Map getMinVerUpdsRequired() {
    Boolean updRequired = false
    List updItems = []
    Map<String, String> codeItems = [server: "Echo Speaks Server", echoDevice: "Echo Speaks Device", wsDevice: "Echo Speaks Websocket", actionApp: "Echo Speaks Actions", zoneApp: "Echo Speaks Zones", zoneEchoDevice: "Echo Speaks Zone Device"]
    Map<String, String> codeVers = (Map<String, String>)state.codeVersions ?: [:]
    codeVers.each { String k,String v->
        if(codeItems.containsKey(k) && v != sNULL && (versionStr2Int(v) < minVersionsFLD[k])) { updRequired = true; updItems.push(codeItems[k]) }
    }
    return [updRequired: updRequired, updItems: updItems]
}

static Map getDeviceStyle(String family, String type) {
    Map typeData = (Map)deviceSupportMapFLD.types[type] ?: [:]
    if(typeData) {
        return typeData
    } else { return [n: "Echo Unknown "+type, i: sUNKNOWN ] }
}

public Map getDeviceFamilyMap() {
    if(!state.appData || !state.appData.deviceFamilies) { checkVersionData(true) }
    return (Map)state.appData?.deviceFamilies ?: (Map)deviceSupportMapFLD.families
}

List getDevicesFromSerialList(List<String> serialList) {
    //logTrace("getDevicesFromSerialList called with: ${serialList}")
    if (!serialList) {
       logDebug("Serial List is empty")
       return null
    }
    List devs = []
    serialList.each { String ser ->
        def d = findEchoDevice(ser)
        if(d) devs.push(d)
    }
    //log.debug "Device list: ${devs}"
    return devs
}

// This is called by the (WHA) device handler to send playback data to cluster members
public void sendPlaybackStateToClusterMembers(String whaKey, data) {
    //logTrace("sendPlaybackStateToClusterMembers: key: ${ whaKey}")
    try {
        Map echoDeviceMap = getEchoDeviceMap() //state.echoDeviceMap
        Map whaMap = echoDeviceMap[whaKey]
        List clusterMembers = (List)whaMap?.clusterMembers

        if (clusterMembers) {
            List clusterMemberDevices = getDevicesFromSerialList(clusterMembers)
            if(clusterMemberDevices) {
                clusterMemberDevices?.each { it?.playbackStateHandler(data, true) }
            }
        } else {
            // The lookup will fail during initial refresh because echoDeviceMap isn't available yet
            //log.debug "sendPlaybackStateToClusterMembers: no data found for ${whaKey} (first refresh?)"
        }
    } catch (ex) {
        log.error "sendPlaybackStateToClusterMembers Error: ${ex}"
    }
}

void removeDevices(Boolean all=false) {
    try {
        settingUpdate("cleanUpDevices", sFALSE, sBOOL)
        List<String> devList = getDeviceList(true)?.collect { (String)[app?.id, "echoSpeaks", it?.key].join("|") }
        List<String> items = ((List)app.getChildDevices())?.findResults { (all || (!all && !devList?.contains(it?.deviceNetworkId as String))) ? it?.deviceNetworkId as String : sNULL }
        logWarn("removeDevices(${all ? "all" : sBLANK}) | In Use: (${all ? 0 : devList.size()}) | Removing: (${items.size()})", true)
        if(items.size() > 0) {
            items.each {  String it -> deleteChildDevice(it) }
        }
    } catch (ex) { logError("Device Removal Failed: ${ex}", false, ex) }
}

void execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
    if(method && callbackHandler && params) {
        String m = method?.toString()?.toLowerCase()
        "asynchttp${m?.capitalize()}"("${callbackHandler}", params, otherData)
    } else { logError("execAsyncCmd Error | Missing a required parameter") }
}

void sendAmazonCommand(String method, Map params, Map otherData=null) {
    String meth = "sendAmazonCommand ${method} ${params} ${otherData?.cmdDesc}"
    try {
        def rData = null
        def rStatus = null
        logTrace(meth)
        switch(method) {
            case "POST":
                httpPostJson(params) { response->
                    rStatus = response?.status
                    if(rStatus == 200) { rData = response?.data ?: null }
                }
                break
            case "PUT":
                if(params?.body) { params?.body = new JsonOutput().toJson(params?.body) }
                httpPutJson(params) { response->
                    rStatus = response?.status
                    if(rStatus == 200) { rData = response?.data ?: null }
                }
                break
            case "DELETE":
                httpDelete(params) { response->
                    rStatus = response?.status
                    if(rStatus == 200) { rData = response?.data ?: null }
                }
                break
        }
        if(rStatus == 200) {
            updTsVal("lastSpokeToAmazon")
            logDebug("${meth} | Status: (${rStatus})${rData != null ? " | Response: ${rData}" : sBLANK} | ${otherData?.cmdDesc} was Successfully Sent!!!")
        } else logWarn("${meth} | Status: ${rStatus} FAILED")
    } catch (ex) {
        respExceptionHandler(ex, meth, true)
    }
}

/*
 * send speak command to one zone
 * caller is vdevice handler via relay from zone app; this will callback the actual device(s) with status
 */
void sendZoneSpeak(String zoneId, String msg, Boolean parallel=false, Boolean bypassDoNotDisturb=false) {
    List devObj = getZoneDevices([zoneId], "TTS", bypassDoNotDisturb)
    String myMsg = "sendZoneSpeak"
    devObj.each { dev ->
        Map cmdMap = [
            cmdDt: now(),
            cmdDesc: "SpeakCommand",
            message: msg,
            //msgLen: newmsg.length(),
            oldVolume: null,
            newVolume: null
        ] 
        Map deviceData = [
            serialNumber : dev.deviceSerialNumber,
            deviceType: dev.deviceTypeId,
            owner: dev.deviceOwnerCustomerId,
            account: dev.deviceAccountId
        ]

        queueMultiSequenceCommand(
            [ [command: 'sendspeak', value:msg, deviceData: deviceData] ],
               myMsg+" to device ${dev.dni}", parallel, cmdMap, (String)dev.dni, "finishSendSpeakZ")
    }
}

/*
 * send announce command to one zone
 * caller is vdevice handler via relay from zone app; this will callback the actual device(s) with status
 */
void sendZoneAnnounce(String zoneId, String msg, Boolean parallel=false, Boolean bypassDoNotDisturb=false) {
    List devObj = getZoneDevices([zoneId], "announce", bypassDoNotDisturb)
    String myMsg = "sendZoneAnnounce"
    devObj.each { dev ->
        Map deviceData = [
            serialNumber : dev.deviceSerialNumber,
            deviceType: dev.deviceTypeId,
            owner: dev.deviceOwnerCustomerId,
            account: dev.deviceAccountId
        ]

        queueMultiSequenceCommand(
            [ [command: 'announcement', value:msg, deviceData: deviceData] ],
               myMsg+" to device ${dev.dni}", parallel)
    }
    devObj.each { dev-> 
        def child = getChildDevice((String)dev.dni)
        child.finishAnnounce(msg, null, null)
    }
}

/*
 * send command to one or more zones (actually devices in one or more zones)
 * caller is actions;  operations are speak or announcement commands
 */
void sendZoneCmd(Map cmdData) {
    logTrace(span("sendZoneCmd | cmdData: $cmdData", "purple"))
    String myCmd = cmdData ? (String)cmdData.cmd : sNULL
    List znList = (List)cmdData.zones
    if(myCmd && znList && znList.size()) {
        List devObj = getZoneDevices(znList, myCmd in ['speak', 'speak_parallel'] ? "TTS" : "announce")

        String newmsg = (String)cmdData.message
        String title = (String)cmdData.title
        Integer volume = (Integer)cmdData.changeVol
        Integer restoreVolume = (Integer)cmdData.restoreVol
        sendDevObjCmd(devObj, myCmd, title, newmsg, volume, restoreVolume)

        /* need to call zone vdevice with updates to finishAnnouncement or finishSpeak */
        znList.each { znId ->
            def zn = getZoneApps()?.find { it.id.toString() == znId.toString() }
            if(zn) {
                if(myCmd == 'announcement') {
                    newmsg = "${title ?: "Echo Speaks"}::${newmsg}".toString()
                    zn.relayFinishAnnouncement(newmsg, [vol:volume, restvol:restoreVolume])
                }
                if(myCmd in ['speak', 'speak_parallel']) {
                    zn.relayFinishSpeak([:], 200, cmdData)
                }
            }
        }
    }
}

/*
 * send a speak or announcement command to a list of devices
 * caller is actions (here or via above) when there is a list of devices
 * this will callback the actual device(s) with status for attribute updates
 */
void sendDevObjCmd(List<Map> odevObj, String myCmd, String title, String newmsg, Integer volume, Integer restoreVolume){
	List<Map> devObj = odevObj.unique() // remove any duplicate devices
        String origMsg = newmsg
        if(devObj.size() == 0) {
            logWarn("sendDevObjCmd NO DEVICES | cmd: $myCmd | devObj: $devObj | msg: ${newmsg} title: $title | volume: $volume | restoreVolume: $restoreVolume")
            return
        }
    //noinspection GroovyFallthrough
    switch(myCmd) {
            case "announcement":
                String zoneDevJson = devObj.size() ? new JsonOutput().toJson(devObj) : sNULL
                newmsg = "${title ?: "Echo Speaks"}::${newmsg}::${zoneDevJson}"
            case "speak":
            case "speak_parallel":
                logDebug("sendDevObjCmd | cmd: $myCmd | devObj: $devObj | msg: ${newmsg} title: $title | volume: $volume | restoreVolume: $restoreVolume")
                String myMsg = "sendDevObjCmd ${myCmd}"
                if(volume != null) {
                    List mainSeqa = []
                    devObj.each { dev-> 
                        Map deviceData = [
                            serialNumber : dev.deviceSerialNumber,
                            deviceType: dev.deviceTypeId,
                            owner: dev.deviceOwnerCustomerId,
                            account: dev.deviceAccountId
                        ]
                        mainSeqa.push([command: "volume", value: volume, deviceData: deviceData])
                    }
                    queueMultiSequenceCommand(mainSeqa, myMsg+"-VolumeSet")
                }
                List mainSeq = []
                if(myCmd in ['speak', 'speak_parallel']) {
                    Boolean para = myCmd == 'speak_parallel'
                    devObj.each { dev->
                        //mainSeq.push([command: 'sendspeak', value:cmdData.message, devType: dev.deviceTypeId, devSerial: dev.deviceSerialNumber])
                        Map cmdMap = [
                            cmdDt: now(),
                            cmdDesc: !para ? "SpeakCommand" : "SpeakParallel",
                            message: newmsg,
                            msgLen: newmsg.length(),
                            oldVolume: restoreVolume,
                            newVolume: volume
                        ]
                        Map deviceData = [
                            serialNumber : dev.deviceSerialNumber,
                            deviceType: dev.deviceTypeId,
                            owner: dev.deviceOwnerCustomerId,
                            account: dev.deviceAccountId
                        ]

                        queueMultiSequenceCommand(
                            [ [command: 'sendspeak', value:newmsg, deviceData: deviceData] ],
                            myMsg+" to device ${dev.dni}", para, cmdMap, (String)dev.dni, "finishSendSpeakZ")
                    }
                    if(para) queueNopCommand()
                } else if (myCmd == 'announcement') {
                    Map myDev = devObj[0]
                    Map deviceData = [
                        serialNumber : myDev.deviceSerialNumber,
                        deviceType: myDev.deviceTypeId,
                        owner: myDev.deviceOwnerCustomerId,
                        account: myDev.deviceAccountId
                    ]
                    mainSeq.push([command: "announcement_devices", value: newmsg, deviceData: deviceData, cmdType: 'playAnnouncement'])
                    queueMultiSequenceCommand(mainSeq, myMsg)
                    devObj.each { dev-> 
                        def child = getChildDevice((String)dev.dni)
                        child.finishAnnounce(origMsg, volume, restoreVolume)
                    }
                }

                if(restoreVolume!=null) {
                    List amainSeq = []
                    devObj.each { dev->
                        Map deviceData = [
                            serialNumber : dev.deviceSerialNumber,
                            deviceType: dev.deviceTypeId,
                            owner: dev.deviceOwnerCustomerId,
                            account: dev.deviceAccountId
                        ]
                        amainSeq.push([command: "volume", value: restoreVolume, deviceData: deviceData])
                    }
                    queueMultiSequenceCommand(amainSeq, myMsg+"-VolumeRestore")
                }
//                log.debug "mainSeq: $mainSeq"

                break
        }
}

private List getZoneDevices(List znList, String cmd, Boolean chkDnd=false) {
    // logTrace("getZoneDevices | $znList")
    List devObjs = []
    if(znList && znList.size()) {
        znList.each { znId ->
            // log.debug "znId: $znId"
            Map znData = getZoneState(znId.toString())
            // log.trace "znData: $znData"
            if(znData && znData.zoneDevices) {
                List devices = getDevicesFromList((List)znData.zoneDevices)
                //devices?.each { devObjs?.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String]) }
                devices?.each {
                    Map devInfo = it?.getEchoDevInfo(cmd, true)  // ignores dnd setting in device
                    if(devInfo) {
                        Boolean dnd = chkDnd ? getDndEnabled((String)devInfo.deviceSerialNumber) : false
                        if(!dnd) devObjs?.push(devInfo)
//                        if(!dnd) devObjs?.push([deviceTypeId: devInfo.deviceTypeId, deviceSerialNumber: devInfo.deviceSerialNumber, deviceOwnerCustomerId: devInfo.deviceOwnerCustomerId, deviceAccountId: devInfo.deviceAccountId, dni: devInfo.dni])
                    }
                }
            }
        }
    }
    //return (String) (devObjs.size()) ? new groovy.json.JsonOutput().toJson(devObjs) : sNULL
    return devObjs
}

private Map getZoneState(String znId) {
    Map zones = getZones()
    if(zones) {
        return (Map)zones[znId]
    }
    return null
}

/*
 * called by drivers to queue speak (and possible volume changes)
 * will setup to call back the device handler when the command completes with status
 */

void sendSpeak(Map cmdMap, Map deviceData, String device, String callback, Boolean parallel=false){
    String nm = cmdMap.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    logTrace("sendSpeak cmdMap: $nm  callback: $callback,  device: $device")

/*    Map deviceData = [
        serialNumber : cmdMap.serialNumber,
        deviceType: cmdMap.deviceType,
        owner: cmdMap.owner,
        account: cmdMap.account
    ] */
//    Map st = [serialNumber: cmdMap.serialNumber, deviceType: cmdMap.deviceType]
    List<Map> seqCmds = []
    if(cmdMap.newVolume) { seqCmds.push([command: "volume", value: cmdMap.newVolume, deviceData: deviceData]) }
    seqCmds.push([command: 'sendspeak', value:cmdMap.message, deviceData:deviceData])
    if(cmdMap.oldVolume) { seqCmds.push([command: "volume", value: cmdMap.oldVolume, deviceData: deviceData]) }

    queueMultiSequenceCommand(seqCmds, "sendSpeak from $device", parallel, cmdMap, device, callback)
}

void queueNopCommand(){
    Map item= [
        t: 'nop',
        time: now(),
        device: null,
        callback: null
    ]
    addToQ(item)

}

void queueSequenceCommand(String type, String command, value, Map deviceData=[:], String device=sNULL, String callback=sNULL){
    Map item= [
        t: 'sequence',
        time: now(),
        type: type,
        command: command,
        value: value,
        deviceData: deviceData,
        device: device,
        callback: callback
    ]
    addToQ(item)
}

void queueMultiSequenceCommand(List<Map> commands, String srcDesc, Boolean parallel=false, Map cmdMap=[:], String device=sNULL, String callback=sNULL) {
//log.warn "commands: $commands   srcDesc: $srcDesc  parallel: $parallel  cmdMap: $cmdMap  device: $device"
// expand speak commands and handle ssml
    List<Map> newCmds = []
    List<Map> seqCmds = commands
    seqCmds?.each { cmdItem->
        // log.debug "cmdItem: $cmdItem"
        if(cmdItem.command instanceof String){
             if((String)cmdItem.command in ['sendspeak']){
                  Map deviceData = (Map)cmdItem.deviceData
//                  Map st = cmdItem.devType ? [serialNumber: cmdItem.devSerial, deviceType: cmdItem.devType] : deviceData
                  newCmds = newCmds + msgSeqBuilder((String)cmdItem.value, deviceData, 'sendSpeak')
             } else newCmds.push(cmdItem)
        } else newCmds.push(cmdItem)
    }
    Map item = [
        t: 'multi',
        time: now(),
        commands: newCmds,
        srcDesc: srcDesc,
        parallel: parallel,
//        deviceData: deviceData,
        cmdMap: cmdMap,
        device: device,
        callback: callback
    ]
    addToQ(item)
}

void addToQ(Map item) {
    String appId=app.getId()
    Boolean aa = getTheLock(sHMLF, "addToQ(${item})")
    // log.trace "lock wait: ${aa}"

    Map<String,List> memStore = historyMapFLD[appId] ?: [:]
    String k = 'cmdQ'
    List<Map> eData = (List)memStore[k] ?: []
    eData.push(item)
    Integer qsiz = eData.size()
    updMemStoreItem(k, eData)

    releaseTheLock(sHMLF)

    if(qsiz == 1) runInMillis(300L, "workQ")
    else runIn(24, "workQB")

    List<String> lmsg = []
    String t = item.t
    Boolean fir=true
    ['cmdMap', 'time', 'deviceData', 'device', 'callback', 'parallel', 'command', 'value', 'srcDesc', 'type'].each { String s ->
        def ss = item."${s}"
        if(ss) {
             if(fir) { fir=false; lmsg.push(spanSm("addToQ NEW COMMAND (${qsiz})", sCLRGRN2)) }
             String nm = ss.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
             lmsg.push("addToQ (${t}) | ${s}: ${nm}".toString())
        }
    }
    if(item.commands?.size()) {
        Integer cnt = 1
        item.commands.each { cmd -> 
            lmsg.push("addToQ (${item.t}) | Command(${cnt}): ${cmd}".toString())
            cnt++
        }
    }
    if((Boolean)settings.logDebug) lmsg.each { String msg -> log.debug(msg) }
}

void workQF() { workQ() }
void workQB() { workQ() }

void workQ() {
    logTrace "running workQ"
    String mmsg

    String appId=app.getId()
    Boolean aa = getTheLock(sHMLF, "addToQ(${item})")
    // log.trace "lock wait: ${aa}"
    Boolean locked = true

    Map myMap = workQMapFLD[appId] ?: [:]
    Boolean active = (Boolean)myMap.active
    if(active==null) { active = false;  myMap.active=active; workQMapFLD[appId]=myMap; workQMapFLD=workQMapFLD }
    // log.debug "active: $active myMap: $myMap"
    Long nextOk = (Long)myMap.nextOk ?: 0L
    if(nextOk < now()) nextOk = 0L

    Map<String,List> memStore = historyMapFLD[appId] ?: [:]
    String k = 'cmdQ'
    List<Map> eData = (List<Map>)memStore[k] ?: []

    Boolean fnd = (eData.size() > 0)

    // if we are not doing anything grab next item off queue and start it;
    if(!active && now() > nextOk && fnd) {

        List<String> lmsg = []
        Double msSum = 0.0D
        List seqList = []
        List activeD = []
        Map extData=[:]
        List extList = []

        Boolean oldParallel
        Boolean parallel = false

        String srcDesc
        Map seqObj
        Integer mdelay = 0

        // lets try to join commands in single request to Alexa
        while(eData.size()>0){

            Map item = (Map)eData[0]

            String t=(String)item.t
            // Long tLong=(Long)item.time
            Map cmdMap=[:]
            String device = (String)item.device
            String callback = (String)item.callback
            srcDesc = sNULL
            //log.debug "item: $item"

            if(t=='multi') {
                srcDesc = (String)item.srcDesc
                List<Map> seqCmds = (List<Map>)item.commands

                if(srcDesc == 'ExecuteRoutine'){
                    if(seqList.size() > 0) break // execute runs by itself
                    Map seqMap = (Map)seqCmds[0].command // already have a sequence map
                    seqObj = sequenceBuilder(seqMap, null, null)
                    if(oldParallel == null) oldParallel = parallel
                } else {

                    Boolean nparallel = item.parallel
                    parallel = nparallel != null ? nparallel : parallel
                    if(oldParallel == null) oldParallel = parallel
                    if(parallel != oldParallel) { break } // if parallel changes we are done this set of commands

                    cmdMap = (Map)item.cmdMap ?: [:]

                    //log.debug "seqCmds: $seqCmds"
                    seqList = seqList + multiSequenceListBuilder(seqCmds)
                    if(!parallel) mdelay = 0
                    seqCmds?.each { cmdItem->
                        //log.debug "cmdItem: $cmdItem"
                        if(cmdItem.command instanceof String){
                            String mcommand = cmdItem.command
                            String type=cmdItem?.cmdType ?: sBLANK
                            String tv  = cmdItem?.value?.toString()
                            Integer del = getMsgDur(mcommand, type, tv)
                            if(del) {
                                if(!parallel) mdelay += del
                                else mdelay = del > mdelay ? del : mdelay
                            }
                        }
                        if(mdelay) cmdMap.msgDelay= mdelay
                    }
                    //log.debug "seqList: ${seqList}"
                }
            }

            if(t=='sequence') {
                String type=item.type
                Map deviceData = (Map)item.deviceData
                srcDesc = type + "${device ? " from $device" : sBLANK}"

                Boolean nparallel = item.parallel
                parallel = nparallel != null ? nparallel : false
                if(oldParallel == null) oldParallel = parallel
                if(parallel != oldParallel) { break } // if parallel changes we are done this set of commands

                String command=(String)item.command

                def value = item.value

                if(command in ['announcement_devices'] ) {
                    if(seqList.size() > 0) break // this command runs alone so finish off what we are planning to do
                    seqObj = sequenceBuilder(command, value, deviceData)
                } else {
                    seqList = seqList + [createSequenceNode(command, value, deviceData)]
                }

                String tv  = value?.toString()
                Integer del = getMsgDur(command, type, tv)
                if(del) {
                    if(parallel) mdelay = del > mdelay ? del : mdelay
                }
                if(del && !cmdMap) cmdMap = [ msgDelay: del ]
            }

            Map titem = (Map)eData.remove(0) // pop the command as we are going to do it
            updMemStoreItem(k, eData)

            if(t=='nop') {
                    if(seqList.size() > 0) {
                        lmsg.push("workQ found nop, processing current list")
                        break // finish off what we are planning to do
                    }
                    lmsg.push("workQ found nop, skipping")
            } else {

                activeD.push(titem) // save what we are doing to an active list
                updMemStoreItem('active', activeD)

                lmsg.push("workQ adding ${srcDesc} | ${seqList ? "MultiSequence" : "Sequence"} ${seqList ? "${parallel ? ": Parallel" : ": Sequential"}" : sBLANK}".toString())

                Map t_extData =[:]
                if(device && callback) {
                    String nstr = cmdMap?.message?.toString()
                    nstr = nstr?.trim()
                    Boolean isSSML = (nstr?.toString()?.startsWith("<speak>") && nstr?.endsWith("</speak>"))
                    //if(isSSML) nstr = nstr[7..-9]
                    Integer msgLen = nstr?.length()
                    t_extData = [
                        cmdDt:(cmdMap.cmdDt ?: null),
                        cmdDesc: (cmdMap.cmdDesc ?: null),
                        msgLen: msgLen,
                        isSSML: isSSML,
                        deviceId: device,
                        callback: callback,
                        msgDelay: (cmdMap.msgDelay ?: null),
                        message: (cmdMap.message ? cmdMap.message : null),
                        newVolume: (cmdMap.newVolume ?: null),
                        oldVolume: (cmdMap.oldVolume ?: null),
                        cmdId: (cmdMap.cmdId ?: null),
                    ]
                }
                extList.push(t_extData)
                extData.extList = extList

                Double ms = ((cmdMap?.msgDelay ?: 0.5D) * 1000.0D)
                ms = Math.min(240000, Math.max(ms, 0))  // at least 0, max 240 seconds
                msSum = parallel ? ms : msSum + ms
                lmsg.push("workQ ms delay is $msSum".toString())

                if(seqObj) { break } // runs by itself
                // if(parallel) { break } // only run 1 parallel at a time in case they are changing the same thing again
            }
        }

        if(seqList.size() > 0 || seqObj) {

            Integer mymin = 3000 // min ms between Alexa commands

            msSum = Math.min(240000, Math.max(msSum, mymin))
            nextOk = (Long)now() + msSum.toLong()
            lmsg.push("workQ FINAL ms delay is $msSum".toString())
            active = true
            myMap.active=active
            myMap.nextOk = nextOk; workQMapFLD[appId]=myMap; workQMapFLD=workQMapFLD

            locked = false
            releaseTheLock(sHMLF)

            lmsg.each { String msg -> logDebug(msg) }

            extData = extData + [nextOk: nextOk]

            if(!seqObj) { //'ExecuteRoutine'
                Map seqMap = multiSequenceBuilder(seqList, oldParallel)
                seqObj = sequenceBuilder(seqMap, null, null)
            }
            
            Map params = [
                uri: getAmazonUrl(),
                path: "/api/behaviors/preview",
                headers: getReqHeaderMap(true),
                contentType: sAPPJSON,
                timeout: 20,
                body: new JsonOutput().toJson(seqObj)
            ]

              //String nm = params.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
              //log.trace spanSm("workQ params: $nm extData: $extData", sCLRGRN)

            try{
                execAsyncCmd("post", "finishWorkQ", params, extData)
            } catch (ex) {
                respExceptionHandler(ex, "workQ", true)
                finishWorkQ([status: 500, data: [:]], extData)
            }
        }
    }

    Long t0 = (Long)now()
    mmsg = "workQ active: ${active} work items fnd: ${fnd} now: ${t0} nextOk: ${nextOk}"
    if(!active && fnd) { // if we have more work to do
        t0 = (Long)now()
        Long ms = nextOk+200L - t0
        if(ms <= 0L) ms = 4000
        if(t0 < nextOk) { // if we are waiting between commands due to Alexa limits, schedule wakeup to resume
            runInMillis(ms, "workQF")
            mmsg = "workQ wakeup requested in $ms ms" + mmsg
        }
    }
    if(locked) releaseTheLock(sHMLF)
    if(mmsg) logDebug(mmsg)
}

// this does not handle SSML break commands
// https://developer.amazon.com/en-US/docs/alexa/custom-skills/speech-synthesis-markup-language-ssml-reference.html
Integer getMsgDur(String command, String type, String tv){
    Integer del = 0
    if(command in ['announcement_devices', 'announcement', 'announcementall'] || type in ['sendSpeak']) {
        String[] valObj = (tv?.contains("::")) ? tv.split("::") : ["Echo Speaks", tv]
        String nstr = valObj[1].trim()
        nstr = nstr.replaceAll(/\s\s+/, sSPACE)
        //String nm = nstr.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
        //log.debug "getMsgDur $nm"
        Boolean isSSML = (nstr?.startsWith("<speak>") && nstr?.endsWith("</speak>"))
        if(isSSML) nstr = nstr[7..-9]
        isSSML = (isSSML || command == 'ssml')
        String actMsg = isSSML ?  nstr?.replaceAll(/<[^>]+>/, sBLANK) : cleanString(nstr)
        //nm = actMsg.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
        //log.debug "getMsgDur1 $nm"
        Integer msgLen = actMsg?.length() ?: 0
        del = calcDelay(msgLen)
        if(devModeFLD) logTrace("getMsgDur res: $del | actMsg: ${actMsg} msgLen: $msgLen origLen: ${tv.length()} isSSML: ${isSSML} ($command, $type, $tv)")
    }
    else if(type.startsWith('play')) del = 18
    else if(type.startsWith('say')) del = 3
    //logTrace("getMsgDur res: $del ($command, $type, $tv)")
    return del
}

static Integer calcDelay(Integer msgLen=null, Boolean addRandom=false) {
    if(!msgLen) { return 30 }
    Integer twd = 2
    Integer v = (Integer)((msgLen <= 14 ? 1 : (msgLen / 14)) * twd)
    Integer res=v
    Integer randomInt
    if(addRandom){
        Random random = new Random()
        randomInt = random?.nextInt(5) //Was using 7
        res=v + randomInt
    }
//    logTrace("calcDelay($msgLen) | res:$res | twd: $twd | delay: $v ${addRandom ? '+ '+randomInt.toString() : sBLANK}")
    return res //+2
}

void finishWorkQ(response, extData){
    String meth = 'finishWorkQ'
    logTrace "running "+meth
    Integer statusCode
    def sData
    String respMsg
    try {
        statusCode = response?.status
        if(response.hasError()){
           respMsg = response.getErrorMessage()
        } else sData = response?.data
    } catch(ex) {
        respExceptionHandler(ex, "finishWorkQ", true)
    }

    Boolean retry=false
    if(statusCode == 200) updTsVal("lastSpokeToAmazon")
    else {
        logWarn("$meth | ${statusCode} | $respMsg  | $extData")
        String respMsgLow = respMsg ? respMsg.toLowerCase() : sNULL
        if((statusCode in [400, 429]) && respMsgLow && (respMsgLow in ["rate exceeded", "too many requests"])) {
            switch(respMsgLow) {
                case "rate exceeded":
                    logWarn("You've been rate-limited by Amazon for sending too many consectutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    retry=true
                    break
                case "too many requests":
                    logWarn("You've sent too many consecutive commands to your devices... | Device will retry again in ${rDelay} seconds", true)
                    retry=true
                    break
            }
        }
    }

    String appId=app.getId()
    Boolean aa = getTheLock(sHMLF, "addToQ(${item})")

    Map myMap = workQMapFLD[appId]
    Boolean active = false;  myMap.active=active; workQMapFLD[appId]=myMap; workQMapFLD=workQMapFLD

    Map<String,List> memStore = historyMapFLD[appId] ?: [:]
    String k = 'active'
    List<Map> activeD = (List<Map>)memStore[k] ?: []
    if(retry) {
        log.warn "wanted to retry but did not"
//        String kk = 'cmdQ'
//        List<Map> eData = (List<Map>)memStore[kk] ?: []
//        List<Map> newL = activeD + eData
//        activeD = newL
// TODO delays
        activeD = []
    } else {
        activeD = []
    }
    updMemStoreItem(k, activeD)

    releaseTheLock(sHMLF)

//    if(!retry) {
    extData?.extList?.each  { extItem ->
        if(extItem && (String)extItem.deviceId && (String)extItem.callback) {
            if(extItem != null && statusCode==200) extItem["amznReqId"] = response?.headers["x-amz-rid"] ?: null
            def child = getChildDevice((String)extItem.deviceId)
            child."${(String)extItem.callback}"(sData, statusCode, extItem)
        }
    }
//    }
    workQ()
}

Map sequenceBuilder(cmd, val, Map deviceData=[:]) {
//log.debug "sequenceBuilder: $cmd   val: $val"
    Map seqJson
    if (cmd instanceof Map) {
        seqJson = (Map)cmd?.sequence ?: (Map)cmd
    } else {
        seqJson = [
            "@type": "com.amazon.alexa.behaviors.model.Sequence",
            "startNode": createSequenceNode(cmd, val, deviceData)
        ]
    }
    Map seqObj = [
        behaviorId: (seqJson?.sequenceId ? cmd?.automationId : "PREVIEW"),
        sequenceJson: new JsonOutput().toJson(seqJson),
        status: "ENABLED"
    ]
    return seqObj
}

List multiSequenceListBuilder(List<Map>commands) {
    //log.debug "multiSequenceListBuilder commands: $commands"
    List nodeList = []
    commands?.each { cmdItem->
        //log.debug "multiSequenceListBuilder cmdItem: $cmdItem"
        if(cmdItem.command instanceof String){
            Map deviceData = (Map)cmdItem.deviceData
            nodeList.push(createSequenceNode((String)cmdItem.command, cmdItem.value, deviceData) )
        } else {
            nodeList.push(cmdItem.command)
        }
    }
    return nodeList
}

static Map multiSequenceBuilder(List nodeList, Boolean parallel=false) {
//log.debug "multiSequenceBuilder: $nodeList"
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    Map seqMap = [
       "sequence": [
           "@type": "com.amazon.alexa.behaviors.model.Sequence",
           "startNode": [
               "@type": "com.amazon.alexa.behaviors.model.${seqType}",
               "name": null,
               "nodesToExecute": nodeList
           ]
       ]
    ]
    return seqMap
}

static Integer getStringLen(String str) { return str?.length() ?: 0 }

private static List msgSeqBuilder(String str, Map deviceData, String cmdType) {
    //String nm = str.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    //log.debug "msgSeqBuilder: $nm"
    List seqCmds = []
    List strArr = []
    String nstr = str.trim()
    Boolean isSSML = (nstr.startsWith("<speak>") && nstr.endsWith("</speak>"))
    //if(isSSML) nstr = nstr[7..-9]
    str = nstr
    if(str.length() < 450) {
        seqCmds.push([command: (isSSML ? "ssml": "speak"), value: str, deviceData: deviceData, cmdType: cmdType])
    } else {
        List<String> msgItems = str.split()
        msgItems.each { String wd->
            // log.debug "CurArrLen: ${(getStringLen(strArr.join(" ")))} | CurStrLen: (${wd.length()})"
            if((getStringLen(strArr.join(sSPACE)) + wd.length()) > 430) {
                seqCmds.push([command: (isSSML ? "ssml": "speak"), value: strArr.join(sSPACE), deviceData: deviceData, cmdType: cmdType])
                strArr = []
            }
            strArr.push(wd)
            if(wd == msgItems.last()) { seqCmds.push([command: (isSSML ? "ssml": "speak"), value: strArr.join(sSPACE), deviceData: deviceData, cmdType: cmdType]) }
        }
    }
    // log.debug "seqCmds: $seqCmds"
    return seqCmds
}

String cleanString(String str, Boolean frcTrans=false) {
    if(!str) { return sNULL }
    //String nm = str.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    //log.debug "cleanString1: $nm"

    //Cleans up characters from message

// some folks try to use ssml without <speak> markers.  It sometimes works and sometimes does not - below makes it always fail as it removes some ssml markup ( / for example)
    //str = str.replaceAll(~/[^a-zA-Z0-9-?%°.,:&#;<>!\/ ]+/, sSPACE)?.replaceAll(/\s\s+/, sSPACE)
    str = str.replaceAll(/\s\s+/, sSPACE)

    str = textTransform(str, frcTrans)
    //nm = str.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    //log.debug "cleanString: $nm"
    return str
}

private String textTransform(String str, Boolean force=false) {
    if(!force && (Boolean)settings.disableTextTransform) { return str }
    // Converts F temp values to readable text "19F"
    str = str.replaceAll(/([+-]?\d+)\s?([CcFf])/) { return "${it[0]?.toString()?.replaceAll("[-]", "minus ")?.replaceAll("[FfCc]", " degrees")}" }
    str = str.replaceAll(/(\sWSW\s)/, " west southwest ")?.replaceAll(/(\sWNW\s)/, " west northwest ")?.replaceAll(/(\sESE\s)/, " east southeast ")?.replaceAll(/(\sENE\s)/, " east northeast ")
    str = str.replaceAll(/(\sSSE\s)/, " south southeast ")?.replaceAll(/(\sSSW\s)/, " south southwest ")?.replaceAll(/(\sNNE\s)/, " north northeast ")?.replaceAll(/(\sNNW\s)/, " north northwest ")
    str = str.replaceAll(/(\sNW\s)/, " northwest ")?.replaceAll(/(\sNE\s)/, " northeast ")?.replaceAll(/(\sSW\s)/, " southwest ")?.replaceAll(/(\sSE\s)/, " southeast ")
    str = str.replaceAll(/(\sE\s)/," east ")?.replaceAll(/(\sS\s)/," south ")?.replaceAll(/(\sN\s)/," north ")?.replaceAll(/(\sW\s)/," west ")
    str = str.replaceAll("%"," percent ")
    str = str.replaceAll("°"," degrees ")
    return str
}
/*
private String timeTransform(String str, Boolean force=false) {
    str = str.replaceAll(/^(?:(?:(?:0?[1-9]|1[0-2])(?::|\.)[0-5]\d(?:(?::|\.)[0-5]\d)?\s?[aApP][mM])|(?:(?:0?\d|1\d|2[0-3])(?::|\.)[0-5]\d(?:(?::|\.)[0-5]\d)?))$/) {
        log.debug "timeTransform: ${it[0]}"
        // return "${it[0]?.toString()?.replaceAll("[-]", "minus ")?.replaceAll("[FfCc]", " degrees")}" 
    }
    return str
}*/

Map createSequenceNode(String command, value, Map deviceData = [:]) {
    //log.debug "createSequenceNode: command: $command   "
    //String nm = value.toString().replaceAll('<', '&lt;').replaceAll('>', '&gt;')
    //log.debug "createSequenceNode: value:  $nm"
    //log.debug "createSequenceNode: deviceData:  $deviceData   "
    try {
        Boolean remDevSpecifics = false
        String deviceType = deviceData?.deviceType ?: sNULL
        String serialNumber = deviceData?.serialNumber ?: sNULL
        String accountId = deviceData?.account ?: sNULL
        String owner = deviceData?.owner ?: sNULL

        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            operationPayload: [
                deviceType: deviceType,
                deviceSerialNumber: serialNumber,
                locale: ((String)settings.regionLocale ?: "en-US"),
                customerId: owner //state.deviceOwnerCustomerId
            ]
        ]

        String lcmd = command.toLowerCase()
        switch (lcmd) {
            case "weather":
                seqNode.type = "Alexa.Weather.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "traffic":
                seqNode.type = "Alexa.Traffic.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "flashbriefing":
                seqNode.type = "Alexa.FlashBriefing.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "goodmorning":
                seqNode.type = "Alexa.GoodMorning.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "goodnight":
                seqNode.type = "Alexa.GoodNight.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "cleanup":
                seqNode.type = "Alexa.CleanUp.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "singasong":
                seqNode.type = "Alexa.SingASong.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "tellstory":
                seqNode.type = "Alexa.TellStory.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "funfact":
                seqNode.type = "Alexa.FunFact.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "joke":
                seqNode.type = "Alexa.Joke.Play"
                seqNode.skillId = "amzn1.ask.1p.saysomething"
                break
            case "calendartomorrow":
                seqNode.type = "Alexa.Calendar.PlayTomorrow"
                seqNode.skillId = "amzn1.ask.1p.calendar"
                break
            case "calendartoday":
                seqNode.type = "Alexa.Calendar.PlayToday"
                seqNode.skillId = "amzn1.ask.1p.calendar"
                break
            case "calendarnext":
                seqNode.type = "Alexa.Calendar.PlayNext"
                seqNode.skillId = "amzn1.ask.1p.calendar"
                break
            case "date":
                seqNode.type = "Alexa.Date.Play"
                seqNode.skillId = "amzn1.ask.1p.dateandtime"
                break
            case "time":
                seqNode.type = "Alexa.Time.Play"
                seqNode.skillId = "amzn1.ask.1p.dateandtime"
                break
            case "stop":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.Stop"
                seqNode.skillId = "amzn1.ask.1p.alexadevicecontrols"
                seqNode.operationPayload.devices = [ [deviceType: deviceType, deviceSerialNumber: serialNumber] ]
                seqNode.operationPayload.isAssociatedDevice = false
                break
            case "stopalldevices":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.Stop"
                seqNode.operationPayload.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                seqNode.operationPayload.isAssociatedDevice = false
                break
            case "cannedtts_random":
            case "cannedtts":
                List<String> okVals = (List<String>)seqItemsAvail().speech.cannedtts_random //["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"]
                String sval = value.toString()
                if(!(sval in okVals)) { return null }
                seqNode.type = "Alexa.CannedTts.Speak"
                String[] valObj = lcmd == 'cannedtts_random' ?  [sval, 'random'] : (sval?.contains("::") ? sval.split("::") : [sval, sval])
                seqNode.operationPayload.cannedTtsStringId = "alexa.cannedtts.speak.curatedtts-category-${valObj[0]}/alexa.cannedtts.speak.curatedtts-${valObj[1]}"
                break
            case "sound":
                String sndName
                if(value?.startsWith("amzn_sfx_")) {
                    sndName = value
                } else {
                    Map sounds = getAvailableSounds()
                    if(sounds[value]) { sndName = sounds[value] }
                    else { sndName = value }
                    //if(!(sounds[value])) { return null }
                    //sndName = sounds[value]
                }
                seqNode.type = "Alexa.Sound"
                seqNode.operationPayload.soundStringId = sndName
                break
            case "wait":
                remDevSpecifics = true
                seqNode.operationPayload?.remove('customerId')
                seqNode.type = "Alexa.System.Wait"
                seqNode.operationPayload.waitTimeInSeconds = value?.toInteger() ?: 5
                break

            case "dnd_duration":
            case "dnd_time":
            case "dnd_all_duration":
            case "dnd_all_time":
                remDevSpecifics = true
                seqNode.type = "Alexa.DeviceControls.DoNotDisturb"
                seqNode.skillId = "amzn1.ask.1p.alexadevicecontrols"
//                seqNode.operationPayload.customerId = (String)state.deviceOwnerCustomerId
                if(lcmd in ["dnd_all_time", "dnd_all_duration"]) {
                    seqNode.operationPayload.devices = [ [deviceType: "ALEXA_ALL_DEVICE_TYPE", deviceSerialNumber: "ALEXA_ALL_DSN"] ]
                }
                if(lcmd in ["dnd_time", "dnd_duration"]) {
                    seqNode.operationPayload.devices = [ [deviceAccountId: accountId /*(String)state.deviceAccountId*/, deviceType: deviceType, deviceSerialNumber: serialNumber] ]
                }
                seqNode.operationPayload.action = "Enable"
                if(lcmd in ["dnd_time","dnd_all_time"]) {
                    seqNode.operationPayload.until = "TIME#T${value}"
                } else if (lcmd in ["dnd_duration","dnd_all_duration"]) { seqNode?.operationPayload?.duration = "DURATION#PT${value}" }
//ERS
                seqNode.operationPayload.timeZoneId = "America/Detroit" //location?.timeZone?.ID ?: null
                break
            case "speak":
                seqNode.type = "Alexa.Speak"
                value = cleanString(value.toString())
                seqNode.operationPayload.textToSpeak = (String)value
                break
            case "volume":
                seqNode.type = "Alexa.DeviceControls.Volume"
                seqNode.skillId = "amzn1.ask.1p.alexadevicecontrols"
                seqNode.operationPayload.value = value
                break
            case "ssml":
            case "announcement":
            case "announcementall":
            case "announcement_devices":
                remDevSpecifics = true
                seqNode.type = "AlexaAnnouncement"
                seqNode.skillId = "amzn1.ask.1p.routines.messaging"
                seqNode.operationPayload.expireAfter = "PT5S"
                String[] valObj = (value?.toString()?.contains("::")) ? value.toString().split("::") : ["Echo Speaks", value.toString()]
                // log.debug "valObj(size: ${valObj?.size()}): $valObj"
                // valObj[1] = valObj[1]?.toString()?.replace(/([^0-9]?[0-9]+)\.([0-9]+[^0-9])?/, "\$1,\$2")
                // log.debug "valObj[1]: ${valObj[1]}"
                String nstr = valObj[1].trim()
                Boolean isSSML = (nstr?.startsWith("<speak>") && nstr?.endsWith("</speak>"))
                //if(isSSML) nstr = nstr[7..-9]
                String str = nstr
                String mtype = lcmd == "ssml" || isSSML ? "ssml" : "text"
                String mval = lcmd == "ssml" || isSSML ? str : cleanString(str)
                String mtitle = cleanString(lcmd == "ssml" || isSSML ?  str.replaceAll(/<[^>]+>/, sBLANK) : str)
                seqNode.operationPayload.content = [[
                                                            locale: ((String)settings.regionLocale ?: "en-US"),
                                                            display: [ title: cleanString(valObj[0]), body: mtitle ], //valObj[1].replaceAll(/<[^>]+>/, '') ],
                                                            speak: [ type: mtype, value: mval ] //(lcmd == "ssml" || isSSML ? "ssml" : "text"), value: valObj[1] ]
                                                    ]]
                seqNode.operationPayload.target = [ customerId: owner /* (String)state.deviceOwnerCustomerId */]
                if(!(lcmd in ["announcementall", "announcement_devices"])) {
                    seqNode.operationPayload.target.devices = [ [ deviceTypeId: deviceType, deviceSerialNumber: serialNumber ] ]
                } else if(lcmd == "announcement_devices" && valObj?.size() && valObj[2] != null) {
//                    log.debug spanSm("valObj: ${valObj}", sCLRGRN2)
                    List devObjs = new JsonSlurper().parseText(valObj[2])
                    seqNode.operationPayload.target.devices = devObjs
                }
                break

            case "pushnotification":
                remDevSpecifics = true
                seqNode.type = "Alexa.Notifications.SendMobilePush"
                seqNode.skillId = "amzn1.ask.1p.alexanotifications"
                seqNode.operationPayload.notificationMessage = value as String
                seqNode.operationPayload.alexaUrl = "#v2/behaviors"
                seqNode.operationPayload.title = "Echo Speaks"
                break
            case "email":
                seqNode.type = "Alexa.Operation.SkillConnections.Email.EmailSummary"
                seqNode.skillId = "amzn1.ask.1p.email"
                seqNode.operationPayload.targetDevice = [deviceType: deviceType, deviceSerialNumber: serialNumber ]
                seqNode.operationPayload.connectionRequest = [uri: "connection://AMAZON.Read.EmailSummary/amzn1.alexa-speechlet-client.DOMAIN:ALEXA_CONNECT", input: [:] ]
                seqNode.operationPayload.remove('deviceType')
                seqNode.operationPayload.remove('deviceSerialNumber')
                break
            case "goodnews":
                seqNode.type = "Alexa.GoodNews.Play"
                seqNode.skillId = "amzn1.ask.1p.goodnews"
                break
            case "voicecmdtxt":
                seqNode.type = "Alexa.TextCommand"
                seqNode.skillId = "amzn1.ask.1p.tellalexa"
                seqNode.operationPayload.text = value.toString()
                break

            default:
                return null
        }
        if(remDevSpecifics) {
            seqNode.operationPayload.remove('deviceType')
            seqNode.operationPayload.remove('deviceSerialNumber')
            seqNode.operationPayload.remove('locale')
        }
        // log.debug "seqNode: $seqNode"
        return seqNode
    } catch (ex) {
        logError("createSequenceNode Exception: ${ex}", false, ex)
    }
    return [:]
}

/******************************************
|    Notification Functions
*******************************************/
String getAmazonDomain() { return (String)settings.amazonDomain }
String getAmazonUrl() {return "https://alexa.${(String)settings.amazonDomain}"}

static Map notifValEnum(Boolean allowCust = true) {
    Map items = [
        300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    if(allowCust) { items[100000] = "Custom" }
    return items
}

@Field volatile static Map<String,Boolean> healthChkMapFLD = [:]

void healthCheck() {
    logTrace("healthCheck")
    String appId=app.getId()
    if(!healthChkMapFLD[appId]) {
        healthChkMapFLD[appId] = true
        healthChkMapFLD = healthChkMapFLD
    }

    if(!devModeFLD && advLogsActive()) { logsEnabled() }
    checkVersionData()
    if(checkIfCodeUpdated()) {
        logWarn("Code Version Change Detected... Health Check will occur on next cycle.")
        return
    }
    Boolean a=validateCookie()
    if(getLastTsValSecs("lastCookieRrshDt") > cookieRefreshSeconds()) {
        runCookieRefresh()
    } else if (getLastTsValSecs("lastGuardSupChkDt") > 43200) {
        checkGuardSupport()
    } else if(getLastTsValSecs("lastServerWakeDt") > 86400 && serverConfigured()) { wakeupServer(false, false, "healthCheck") }

    if(!(Boolean)state.noAuthActive) runIn(2, "getOtherData")
    chkRestartSocket()

    if((Boolean)state.isInstalled && getLastTsValSecs("lastMetricUpdDt") > (3600*24)) { runIn(30, "sendInstallData", [overwrite: true]) }
    if(advLogsActive()) { logsDisable() }
    appUpdateNotify()

    //if(!getOk2Notify()) { return }
    missPollNotify((Boolean)settings.sendMissedPollMsg, (settings.misPollNotifyMsgWaitVal as Integer ?: 3600))
}

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace || (Boolean)settings.childAppLogDebug || (Boolean)settings.childAppLogTrace || (Boolean)settings.childDeviceLogDebug || (Boolean)settings.childDeviceLogTrace) }
public void logsEnabled() { if(advLogsActive() && !getTsVal("logsEnabled")) { logTrace("enabling logging timer"); updTsVal("logsEnabled") } }
public void logsDisable() {
    if(advLogsActive()) {
        Integer dtSec = getLastTsValSecs("logsEnabled", null)
        if(dtSec && (dtSec > 3600*6)) {
            settingUpdate("logDebug", sFALSE, sBOOL)
            settingUpdate("logTrace", sFALSE, sBOOL)
            if((Boolean)settings.childAppLogDebug || (Boolean)settings.childAppLogTrace || (Boolean)settings.childDeviceLogDebug || (Boolean)settings.childDeviceLogTrace) {
                settingUpdate("childAppLogDebug", sFALSE, sBOOL)
                settingUpdate("childAppLogTrace", sFALSE, sBOOL)
                settingUpdate("childDeviceLogDebug", sFALSE, sBOOL)
                settingUpdate("childDeviceLogTrace", sFALSE, sBOOL)
                runIn(12, 'disableAdvChldLogs')
            }
            remTsVal("logsEnabled")
            log.debug "Disabling debug logs"
        }
    }
}

public void activateChildAdvLogs() {
    if((Boolean)settings.childAppLogDebug) {
        settingUpdate("childAppLogDebug", sFALSE, sBOOL)
        manAllZonesDbgLogs(true)
        manAllActsDbgLogs(true)
    }
    if((Boolean)settings.childAppLogTrace) {
        settingUpdate("childAppLogTrace", sFALSE, sBOOL)
        manAllZonesTrcLogs(true)
        manAllActsTrcLogs(true)
    }
    if((Boolean)settings.childDeviceLogDebug) {
        settingUpdate("childDeviceLogDebug", sFALSE, sBOOL)
        manAllEchosDbgLogs(true)
    }
    if((Boolean)settings.childDeviceLogTrace) {
        settingUpdate("childDeviceLogTrace", sFALSE, sBOOL)
        manAllEchosTrcLogs(true)
    }
    if((Boolean)settings.disableAllChildAdvLogs) {
        settingUpdate("disableAllChildAdvLogs", sFALSE, sBOOL)
        manAllZonesDbgLogs(false)
        manAllActsDbgLogs(false)
        manAllEchosDbgLogs(false)
        manAllEchosTrcLogs(false)
        manAllZonesTrcLogs(false)
        manAllActsTrcLogs(false)
    }
}

private void manAllZonesDbgLogs(Boolean enable=true) { getZoneApps()?.each { ca-> enable ? ca?.enableDebugLog() : ca?.disableDebugLog() } }
private void manAllZonesTrcLogs(Boolean enable=true) { getZoneApps()?.each { ca-> enable ? ca?.enableTraceLog() : ca?.disableTraceLog() } }
private void manAllActsDbgLogs(Boolean enable=true) { getActionApps()?.each { ca-> enable ? ca?.enableDebugLog() : ca?.disableDebugLog() } }
private void manAllActsTrcLogs(Boolean enable=true) { getActionApps()?.each { ca-> enable ? ca?.enableTraceLog() : ca?.disableTraceLog() } }
private void manAllEchosDbgLogs(Boolean enable=true) { ((List)getChildDevices())?.each { cd-> enable ? cd?.enableDebugLog() : cd?.disableDebugLog() }
                                                       getZoneApps()?.each { ca-> enable ? ca?.relayEnableDebugLog() : ca?.relayDisableDebugLog() } }
private void manAllEchosTrcLogs(Boolean enable=true) { ((List)getChildDevices())?.each { cd-> enable ? cd?.enableTraceLog() : cd?.disableTraceLog() }
                                                       getZoneApps()?.each { ca-> enable ? ca?.relayEnableTraceLog() : ca?.relayDisableTraceLog() } }


private void disableAdvChldLogs() {
    getActionApps()?.each { ca-> ca?.logsDisable() }
    getZoneApps()?.each { ca-> ca?.logsDisable() }
    getZoneApps()?.each { ca-> ca?.relayLogsOff() }
    ((List)getChildDevices())?.each { cd-> cd?.logsOff() }
}

void missPollNotify(Boolean on, Integer wait) {
    Integer lastDataUpd = getLastTsValSecs("lastDevDataUpdDt", 1000000)
    Integer lastMissPollM = getLastTsValSecs("lastMissedPollMsgDt")
    //if(devModeFLD) logTrace("missPollNotify() | on: ($on) | wait: ($wait) | getLastDevicePollSec: (${lastDataUpd}) | misPollNotifyWaitVal: (${settings.misPollNotifyWaitVal}) | getLastMisPollMsgSec: (${lastMissPollM})")
    if(lastDataUpd <= ((settings.misPollNotifyWaitVal as Integer ?: 2700)+10800)) {
        state.missPollRepair = false
    } else {
        if(lastDataUpd != 1000000) {
            String msg
            if((Boolean)state.authValid) {
                msg = "\nThe Echo Speaks app has NOT received any device data from Amazon in the last (${getLastTsValSecs("lastDevDataUpdDt")}) seconds.\nThere maybe an issue with network access."
            } else { msg = "\nThe Amazon login info has expired!\nPlease open the heroku amazon authentication page and login again to restore normal operation." }
            logWarn(msg.toString().replaceAll("\n", sSPACE))

            if(lastMissPollM < wait?.toInteger()) { on = false }
            if(on && sendMsg("${app.name} ${(Boolean)state.authValid ? "Data Refresh Issue" : "Amazon Login Issue"}", msg)) {
                updTsVal("lastMissedPollMsgDt")
            }
        }
        if(!(Boolean)state.missPollRepair) {
            if((Boolean)state.authValid){
                if(lastDataUpd == 1000000) logTrace("code reload or system restart, calling initialize")
                else logTrace("calling initialize to attempt recovery")
                state.missPollRepair = true
                initialize()
            }
        }
    }
}

@SuppressWarnings('GroovyVariableNotAssigned')
void appUpdateNotify() {
    Boolean appUpd = appUpdAvail()
    Boolean actUpd = actionUpdAvail()
    Boolean zoneUpd = zoneUpdAvail()
    Boolean zoneChildDevUpd = zoneChildDevUpdAvail()
    Boolean echoDevUpd = echoDevUpdAvail()
    Boolean socketUpd = socketUpdAvail()
    Boolean servUpd = serverUpdAvail()
    Boolean res=false
    if(appUpd || actUpd || zoneUpd || zoneChildDevUpd || echoDevUpd || socketUpd || servUpd) res=true

    Integer secs
    Integer updW
    Boolean on=false
    if(res) {
        on = ((Boolean)settings.sendAppUpdateMsg)
        updW = settings.updNotifyWaitVal?.toInteger()
        if(updW == null) { updW = 43200; settingUpdate("updNotifyWaitVal", "43200", sENUM) }
        secs=getLastTsValSecs("lastUpdMsgDt")
        if(secs > updW && on) {
            String str = sBLANK
            str += !appUpd ? sBLANK : "\nEcho Speaks App: v${state.appData?.versions?.mainApp?.ver?.toString()}"
            str += !actUpd ? sBLANK : "\nEcho Speaks Actions: v${state.appData?.versions?.actionApp?.ver?.toString()}"
            str += !zoneUpd ? sBLANK : "\nEcho Speaks Zones: v${state.appData?.versions?.zoneApp?.ver?.toString()}"
            str += !zoneChildDevUpd ? sBLANK : "\nEcho Speaks Zone Device: v${state.appData?.versions?.zoneChildDevice?.ver?.toString()}"
            str += !echoDevUpd ? sBLANK : "\nEcho Speaks Device: v${state.appData?.versions?.echoDevice?.ver?.toString()}"
            str += !socketUpd ? sBLANK : "\nEcho Speaks Socket: v${state.appData?.versions?.wsDevice?.ver?.toString()}"
            str += !servUpd ? sBLANK : "\n${((Boolean)getServerItem("onHeroku") == true) ? "Heroku Service" : "Node Service"}: v${state.appData?.versions?.server?.ver?.toString()}"
            sendMsg("Info", "Echo Speaks Update(s) are Available:${str}...\n\nPlease visit the IDE to Update your code...")
            updTsVal("lastUpdMsgDt")
        }
    }
    state.updateAvailable = res
    String msg="appUpdateNotify() RESULT: ${res} | on: (${on}) | appUpd: (${appUpd}) | actUpd: (${actUpd}) | zoneUpd: (${zoneUpd}) | echoDevUpd: (${echoDevUpd}) | echoZoneDevUpd (${zoneChildDevUpd}) | servUpd: (${servUpd}) | getLastUpdMsgSec: ${secs} | updNotifyWaitVal: ${updW}"
    if(res) logDebug(msg)
    //else if(devModeFLD) logTrace(msg)
}

private List codeUpdateItems(Boolean shrt=false) {
    Boolean appUpd = appUpdAvail()
    Boolean actUpd = actionUpdAvail()
    Boolean zoneUpd = zoneUpdAvail()
    Boolean zoneChildDevUpd = zoneChildDevUpdAvail()
    Boolean devUpd = echoDevUpdAvail()
    Boolean socketUpd = socketUpdAvail()
    Boolean servUpd = serverUpdAvail()
    List updItems = []
    if(appUpd || actUpd || zoneUpd || zoneChildDevUpd || devUpd || socketUpd || servUpd) {
        if(appUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}App: (v${state.appData?.versions?.mainApp?.ver?.toString()})")
        if(actUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Actions: (v${state.appData?.versions?.actionApp?.ver?.toString()})")
        if(zoneUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Zones: (v${state.appData?.versions?.zoneApp?.ver?.toString()})")
        if(zoneChildDevUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Zone Child Device: (v${state.appData?.versions?.zoneChildDevice?.ver?.toString()})")
        if(devUpd) updItems.push("${!shrt ? "\nEcho Speaks " : "ES "}Device: (v${state.appData?.versions?.echoDevice?.ver?.toString()})")
        if(socketUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Websocket: (v${state.appData?.versions?.wsDevice?.ver?.toString()})")
        if(servUpd) updItems.push("${!shrt ? "\n" : sBLANK}Server: (v${state.appData?.versions?.server?.ver?.toString()})")
    }
    return updItems
}

Boolean getOk2Notify() {
    Boolean smsOk = false
    Boolean pushOk = false
    Boolean pushOver = false
    Boolean notifDevsOk = (((List)settings.notif_devs)?.size() > 0)
    Boolean alexaMsg = ((Boolean)settings.notif_alexa_mobile)
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
        Boolean isBtwn = !timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, (TimeZone)location?.timeZone)
        isBtwn = not ? !isBtwn : isBtwn
        logTrace("NotifTimeOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT": sBLANK} between (${not ? stopTime:startTime} and ${not ? startTime:stopTime})")
        return isBtwn
    } else { return true }
}

// Sends the notifications based on app settings
public Boolean sendMsg(String msgTitle, String msg, Boolean showEvt=true, Map pushoverMap=null, sms=null, push=null) {
    logTrace("sendMsg() | msgTitle: ${msgTitle}, msg: ${msg}, showEvt: ${showEvt}")
    String sentstr = sBLANK
    Boolean sent = false
    try {
        String newMsg = msgTitle+": "+msg
        String flatMsg = newMsg.replaceAll("\n", sSPACE)
        if(!getOk2Notify()) {
            logInfo("sendMsg: Message Skipped Notification not configured or During Quiet Time ($flatMsg)")
        } else {
            if((List)settings.notif_devs) {
                sentstr = "Notification Devices"
                ((List)settings.notif_devs).each { it?.deviceNotification(newMsg) }
                sent = true
            }
            if(sent) {
                state.lastMsg = flatMsg
                updTsVal("lastMsgDt")
                logDebug("sendMsg: Sent ${sentstr} (${flatMsg})")
            }
        }
    } catch (ex) {
        incrementCntByKey("appErrorCnt")
        logError("sendMsg $sentstr Exception: ${ex}", false, ex)
    }
    return sent
}

Boolean childInstallOk() { return (Boolean)state.childInstallOkFlag }

public static String gitBranch() { return gitBranchFLD }

static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${gitBranchFLD}/resources/icons/${imgName}.png" }

static String getPublicImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" }

//static String sectTS(String t, String i = sNULL, Boolean bold=false) { return """<h3>${i ? """<img src="${i}" width="48"> """ : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>""" }

static String sectH3TS(String t, String st, String i = sNULL, String c=sCLR4D9) { return """<h3 style="color:${c};font-weight: bold">${i ? """<img src="${i}" width="48"> """ : sBLANK} ${t?.replaceAll("\\n", "<br>")}</h3>${st ?: sBLANK}""" }

public static String paraTS(String title = sNULL, String body = sNULL, String img = sNULL, Map tOpts=[s: 'normal', c: 'black', b: true, u:true], Map bOpts = [s:'normal', c: sNULL, b: false]) {
    String s = sBLANK
    s += title ? "<div style='${tOpts && (String)tOpts.c != sNULL ? "color: ${(String)tOpts.c};" : sBLANK}${tOpts && (String)tOpts.s != sNULL ? "font-size: ${(String)tOpts.s};" : sBLANK}${tOpts && (Boolean)tOpts.b ? "font-weight: bold;" : sBLANK}${tOpts && (Boolean)tOpts.u ? "text-decoration: underline;" : sBLANK}'>${img != sNULL ? """<img src=${getAppImg(img)} width="42"> """ : sBLANK}${title}</div>" : sBLANK
    s += body ? "<div style='${bOpts && (String)bOpts.c != sNULL ? "color: ${(String)bOpts.c};" : sBLANK}${bOpts && (String)bOpts.s != sNULL ? "font-size: ${(String)bOpts.s};" : sBLANK}${bOpts && (Boolean)bOpts.b ? "font-weight: bold;" : sBLANK}'>${body}</div>" : sBLANK
    return s
}

static String sectHead(String str, String img = sNULL) { return str ? "<h3 style='margin-top:0;margin-bottom:0;'>" + spanImgStr(img) + span(str, "darkorange", sNULL, true) + "</h3>" + "<hr style='background-color:${sCLRGRY};font-style:italic;height:1px;border:0;margin-top:0;margin-bottom:0;'>" : sBLANK }
static String sTS(String t, String i = sNULL, Boolean bold=false) { return "<h3>${i ? "<img src='${i}' width='42'> " : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>" }
static String s3TS(String t, String st, String i = sNULL, String c=sCLR4D9) { return "<h3 style='color:${c};font-weight: bold;'>${i ? "<img src='${i}' width='42'> " : sBLANK} ${t?.replaceAll("\n", "<br>")}</h3>${st ? "${st}" : sBLANK}" }
static String pTS(String t, String i = sNULL, Boolean bold=true, String color=sNULL) { return "${color ? "<div style='color: $color;'>" : sBLANK}${bold ? "<b>" : sBLANK}${i ? "<img src='${i}' width='42'> " : sBLANK}${t?.replaceAll("\n", "<br>")}${bold ? "</b>" : sBLANK}${color ? "</div>" : sBLANK}" }

static String inTS1(String str, String img = sNULL, String clr=sNULL, Boolean und=true) { return spanSmBldUnd(str, clr, img) }
static String inTS(String str, String img = sNULL, String clr=sNULL, Boolean und=true) { return divSm(strUnder(str?.replaceAll("\n", sSPACE)?.replaceAll("<br>", sSPACE), und), clr, img) }

// Root HTML Objects
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }
static String div(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return str ? "<div ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</div>${br ? sLINEBR : sBLANK}" : sBLANK }
static String spanImgStr(String img=sNULL) { return img ? span("<img src='${(!img.startsWith("http://") && !img.startsWith("https://")) ? getAppImg(img) : img}' width='42'> ") : sBLANK }
static String divImgStr(String str, String img=sNULL) { return str ? div(img ? spanImg(img) + span(str) : str) : sBLANK }
static String strUnder(String str, Boolean showUnd=true) { return str ? (showUnd ? "<u>${str}</u>" : str) : sBLANK }
static String getOkOrNotSymHTML(Boolean ok) { return ok ? span("(${okSymFLD})", sCLRGRN2) : span("(${notOkSymFLD})", sCLRRED2) }
static String htmlLine(String color=sCLR4D9) { return "<hr style='background-color:${color};height:1px;border:0;margin-top:0;margin-bottom:0;'>" }
static String lineBr(Boolean show=true) { return show ? sLINEBR : sBLANK }
static String inputFooter(String str, String clr=sCLR4D9, Boolean noBr=false) { return str ? lineBr(!noBr) + divSmBld(str, clr) : sBLANK }
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
        paragraph htmlLine()
        paragraph """<div style='color:${sCLR4D9};text-align:center;'>Echo Speaks<br><a href='${textDonateUrl()}' target="_blank"><img width="120" height="120" src="https://raw.githubusercontent.com/tonesto7/homebridge-hubitat-tonesto7/master/images/donation_qr.png"></a><br><br>Please consider donating if you find this integration useful.</div>"""
    }
}

static String actChildName(){ return "Echo Speaks - Actions" }
static String zoneChildName(){ return "Echo Speaks - Zones" }
//static String zoneChildDeviceName(){ return "Echo Speaks - Zones" }
static String documentationUrl() { return "https://tonesto7.github.io/echo-speaks-docs" }
static String videoUrl() { return "https://www.youtube.com/watch?v=wQPPlTFaGb4&ab_channel=SimplySmart123%E2%9C%85" }
static String textDonateUrl() { return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HWBN4LB9NMHZ4" }
def updateDocsInput() { href url: documentationUrl(), style: sEXTNRL, required: false, title: inTS1("View Documentation", "documentation"), description: inactFoot(sTTP) }

String getAppEndpointUrl(subPath)   { return "${getApiServerUrl()}/${getHubUID()}/apps/${app?.id}${subPath ? "/${subPath}" : sBLANK}?access_token=${state.accessToken}".toString() }

String getLocalEndpointUrl(subPath) { return "${getLocalApiServerUrl()}/apps/${app?.id}${subPath ? "/${subPath}" : sBLANK}?access_token=${state.accessToken}" }

/******************************************
|       Changelog Logic
******************************************/
Boolean showDonationOk() { return ((Boolean)state.isInstalled && !(Boolean)getInstData('shownDonation') && getDaysSinceUpdated() >= 30) }

Integer getDaysSinceUpdated() {
    String updDt = getInstData('updatedDt')
    updDt = updDt ?: sNULL
    if(updDt == sNULL || updDt == "Not Set") {
        updInstData("updatedDt", getDtNow())
        return 0
    } else {
        Date start = Date.parse("E MMM dd HH:mm:ss z yyyy", updDt)
        Date stop = new Date()
        if(start && stop) { return (stop - start) }
        return 0
    }
}

String changeLogData() {
    String txt = (String) getWebData([uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${gitBranchFLD}/CHANGELOG.html", contentType: "text/plain; charset=UTF-8", timeout: 20], "changelog", true)
    return txt?.toString()
}

Boolean showChgLogOk() { return ((Boolean) state.isInstalled && !((String) state.curAppVer == appVersionFLD && (Boolean) getInstData('shownChgLog')) ) }

def changeLogPage() {
    return dynamicPage(name: "changeLogPage", title: sBLANK, nextPage: "mainPage", install: false) {
        section(sectHead("Release Notes:", getAppImg("change_log"))) { paragraph "<span style='font-size: small;white-space: nowrap;'>${changeLogData()}</span>" }
        state.curAppVer = appVersionFLD
        updInstData("shownChgLog", true)
    }
}

/******************************************
|    METRIC Logic
******************************************/
String getFbMetricsUrl() { return state.appData?.settings?.database?.metricsUrl ?: "https://echo-speaks-metrics.firebaseio.com/" }
String getFbConfigUrl() { return state.appData?.settings?.database?.configUrl ?: "https://echospeaks-config.firebaseio.com/" }
Boolean metricsOk() { (!(Boolean)settings.optOutMetrics && state.appData?.settings?.sendMetrics) }
private generateGuid() { if(!(String)state.appGuid) { state.appGuid = UUID?.randomUUID()?.toString() } }
void sendInstallData() { settingUpdate("sendMetricsNow", sFALSE, sBOOL); if(metricsOk()) { Boolean aa=sendFirebaseData(getFbMetricsUrl(), "/clients/${(String)state.appGuid}.json", createMetricsDataJson(), "put", "heartbeat") } }
Boolean removeInstallData() { return removeFirebaseData("/clients/${(String)state.appGuid}.json") }
Boolean sendFirebaseData(String url, String path, String data, String cmdType=null, String type=null) { logTrace("sendFirebaseData(${path}, ${data}, $cmdType, $type"); return queueFirebaseData(url, path, data, cmdType, type) }

Boolean queueFirebaseData(String url, String path, String data, String cmdType=sNULL, String type=sNULL) {
    // logTrace("queueFirebaseData(${path}, ${data}, $cmdType, $type")
    Boolean result = false
    String json = new JsonOutput().prettyPrint(data)
    Map params = [uri: url, path: path, requestContentType: sAPPJSON, contentType: sAPPJSON, timeout: 20, body: json]
    String typeDesc = type ?: "Data"
    try {
        if(!cmdType || cmdType == "put") {
            execAsyncCmd(cmdType, "processFirebaseResponse", params, [type: typeDesc])
            result = true
        } else if (cmdType == "post") {
            execAsyncCmd(cmdType, "processFirebaseResponse", params, [type: typeDesc])
            result = true
        } else { logWarn("queueFirebaseData UNKNOWN cmdType: ${cmdType}") }

    } catch(ex) { logError("queueFirebaseData (type: $typeDesc) Exception: ${ex}", false, ex) }
    return result
}

Boolean removeFirebaseData(String pathVal) {
    logTrace("removeFirebaseData(${pathVal})")
    Boolean result = true
    try {
        httpDelete(uri: getFbMetricsUrl(), path: pathVal) { resp ->
            logDebug("Remove Firebase | resp: ${resp?.status}")
        }
    } catch (ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException ) {
            logError("removeFirebaseData Response Exception: ${ex}", false, ex)
        } else {
            logError("removeFirebaseData Exception: ${ex}", false, ex)
            result = false
        }
    }
    return result
}

void processFirebaseResponse(resp, Map data) {
    String mName="processFirebaseResponse"
    String typeDesc = (String)data.type
    logTrace(mName+"(${typeDesc})")
    try {
        if(resp?.status == 200) {
            logDebug(mName+": ${typeDesc} Data Sent SUCCESSFULLY")
            if(typeDesc == "heartbeat") { updTsVal("lastMetricUpdDt") }
            updInstData("sentMetrics", true)
        } else if(resp?.status == 400) {
            logError(mName+": 'Bad Request': ${resp?.status}")
        } else { logWarn(mName+": 'Unexpected' Response: ${resp?.status}") }
    } catch(ex) {
        logError(mName+" (type: $typeDesc) Exception: ${ex}", false, ex)
    }
}

def renderMetricData() {
    try {
        String jsonIn = createMetricsDataJson()
        String json = "Not Data Found"
        if(jsonIn) {
            json = new JsonOutput().prettyPrint(jsonIn)
        }
        render contentType: sAPPJSON, data: json, status: 200
    } catch (ex) { logError("renderMetricData Exception: ${ex}", false, ex) }
}

private Map getSkippedDevsAnon() {
    Map res = [:]
    Map<String, Map> sDevs = (Map<String,Map>)state.skippedDevices ?: [:]
    sDevs.each { k, v-> if(!res.containsKey((String)v?.type)) { res[(String)v?.type] = v } }
    return res
}

private String createMetricsDataJson() {
    try {
        generateGuid()
        Map swVer = (Map)state.codeVersions
        Map deviceUsageMap = [:]
        Map deviceErrorMap = [:]
        ((List)getChildDevices())?.each { d->
            Map obj = d?.getDeviceMetrics()
            if(obj?.usage?.size()) { obj?.usage?.each { String k, v-> deviceUsageMap[k] = (deviceUsageMap[k] ? deviceUsageMap[k] + v : v) } }
            if(obj?.errors?.size()) { obj?.errors?.each { String k, v-> deviceErrorMap[k] = (deviceErrorMap[k] ? deviceErrorMap[k] + v : v) } }
        }
        Map actData = [:]
        Integer actCnt = 0
        getActionApps()?.each { a-> actData[actCnt] = a?.getActionMetrics(); actCnt++ }
        Map zoneData = [:]
        Integer zoneCnt = 0
        getZoneApps()?.each { a-> zoneData[zoneCnt] = a?.getZoneMetrics(); zoneCnt++ }
        Map dataObj = [
            guid: (String)state.appGuid,
            datetime: getDtNow(),
            installDt: (String)getInstData('dt'),
            updatedDt: (String)getInstData('updatedDt'),
            timeZone: location?.timeZone?.ID?.toString(),
            hubPlatform: platformFLD,
            authValid: (Boolean)state.authValid,
            stateUsage: "${stateSizePerc()}%",
            amazonDomain: settings?.amazonDomain,
            serverPlatform: (Boolean)getServerItem("onHeroku") ? "Cloud" : "Local",
            versions: [app: appVersionFLD, server: (String)swVer?.server ?: "N/A", actions: (String)swVer?.actionApp ?: "N/A", zones: (String)swVer?.zoneApp ?: "N/A", device: (String)swVer?.echoDevice ?: "N/A", socket: (String)swVer?.wsDevice ?: "N/A"],
            detections: [skippedDevices: getSkippedDevsAnon()],
            actions: actData,
            zones: zoneData,
            counts: [
                deviceStyleCnts: (Map)state.deviceStyleCnts ?: [:],
                appHeartbeatCnt: state.appHeartbeatCnt ?: 0,
                getCookieCnt: state.getCookieCnt ?: 0,
                appErrorCnt: state.appErrorCnt ?: 0,
                deviceErrors: deviceErrorMap ?: [:],
                deviceUsage: deviceUsageMap ?: [:]
            ]
        ]
        
        String json = new JsonOutput().toJson(dataObj)
        // log.debug "dataObj: $dataObj"
        return json
    } catch (ex) {
        logError("createMetricsDataJson: Exception: ${ex}", false, ex)
    }
    return sNULL
}

void incrementCntByKey(String key) {
    Long evtCnt = (Long)state."${key}"
    evtCnt = evtCnt != null ? evtCnt : 0L
    evtCnt++
    // logTrace("${key?.toString()?.capitalize()}: $evtCnt", true)
    state."${key}" = evtCnt
}

// ******************************************
//      APP/DEVICE Version Functions
// ******************************************
static Boolean codeUpdIsAvail(String newVer, String curVer, String type) {
    Boolean result = false
    def latestVer
    if(newVer && curVer) {
        List versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b ->
                List verA = a?.tokenize('.'); List verB = b?.tokenize('.'); Integer commonIndices = Math.min(verA?.size(), verB?.size())
                for (Integer i = 0; i < commonIndices; ++i) { if(verA[i]?.toInteger() != verB[i]?.toInteger()) { return verA[i]?.toInteger() <=> verB[i]?.toInteger() } }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer)
        }
    }
    return result
}

Boolean appUpdAvail()           { return ((Map)state.appData?.versions && (String)state.codeVersions?.mainApp && codeUpdIsAvail((String)state.appData.versions?.mainApp?.ver, (String)state.codeVersions.mainApp, "main_app")) }
Boolean actionUpdAvail()        { return ((Map)state.appData?.versions && (String)state.codeVersions?.actionApp && codeUpdIsAvail((String)state.appData.versions?.actionApp?.ver, (String)state.codeVersions.actionApp, "action_app")) }
Boolean zoneUpdAvail()          { return ((Map)state.appData?.versions && (String)state.codeVersions?.zoneApp && codeUpdIsAvail((String)state.appData.versions?.zoneApp?.ver, (String)state.codeVersions.zoneApp, "zone_app")) }
Boolean zoneChildDevUpdAvail()  { return ((Map)state.appData?.versions && (String)state.codeVersions?.zoneEchoDevice && codeUpdIsAvail((String)state.appData.versions?.zoneChildDevice?.ver, (String)state.codeVersions.zoneEchoDevice, "zone_child_dev")) }
Boolean echoDevUpdAvail()       { return ((Map)state.appData?.versions && (String)state.codeVersions?.echoDevice && codeUpdIsAvail((String)state.appData.versions?.echoDevice?.ver, (String)state.codeVersions.echoDevice, "dev")) }
Boolean socketUpdAvail()        { return ((Map)state.appData?.versions && (String)state.codeVersions?.wsDevice && codeUpdIsAvail((String)state.appData.versions?.wsDevice?.ver, (String)state.codeVersions.wsDevice, "socket")) }
Boolean serverUpdAvail()        { return ((Map)state.appData?.versions && (String)state.codeVersions?.server && codeUpdIsAvail((String)state.appData.versions?.server?.ver, (String)state.codeVersions.server, "server")) }

static Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }

void checkVersionData(Boolean now = false) { //This reads a JSON file from GitHub with version numbers
    Integer lastUpd = getLastTsValSecs("lastAppDataUpdDt")
    if (now || !state.appData || (lastUpd > (3600*6))) {
        if(now && (lastUpd < 300)) { return }
        getConfigData()
        getNoticeData()
    }
}

void getConfigData() {
    Map params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${gitBranchFLD}/resources/appData.json",
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map data = (Map)getWebData(params, "appData", false)
    if(data) {
        state.appData = data
        updTsVal("lastAppDataUpdDt")
        logDebug("Successfully Retrieved (v${data?.appDataVer}) of AppData Content from GitHub Repo...")
    }
}

void getNoticeData() {
    Map params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${gitBranchFLD}/notices.json",
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map data = (Map)getWebData(params, "noticeData", false)
    if(data) {
        state.noticeData = data
        logDebug("Successfully Retrieved Developer Notices from GitHub Repo...")
    }
}

private getWebData(Map params, String desc, Boolean text=true) {
    try {
        // log.trace("getWebData: ${desc} data")
        httpGet(params) { resp ->
            if(resp?.status != 200) logWarn("${resp?.status} $params")
            if(resp?.data) {
                if(text) { return resp.data.text?.toString() }
                return resp.data
            }
        }
    } catch (ex) {
        incrementCntByKey("appErrorCnt")
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            logWarn("${desc} file not found")
        } else { logError("getWebData(params: $params, desc: $desc, text: $text) Exception: ${ex}", false, ex) }
        return "${desc} info not found"
    }
}


static Map getAvailableSounds() {
    return getAvailableSoundsFLD
}

// https://developer.amazon.com/en-US/docs/alexa/custom-skills/ask-soundlibrary.html
// TODO: https://m.media-amazon.com/images/G/01/mobile-apps/dex/ask-tech-docs/ask-soundlibrary._TTH_.json
// send this to speak command:   <audio src="soundbank://soundlibrary/sports/crowds/crowds_12"/>
@Field static final Map getAvailableSoundsFLD = [
        // Bells and Buzzer
        bells: "bell_02",
        buzzer: "buzzers_pistols_01",
        church_bell: "amzn_sfx_church_bell_1x_02",
        doorbell1: "amzn_sfx_doorbell_01",
        doorbell2: "amzn_sfx_doorbell_chime_01",
        doorbell3: "amzn_sfx_doorbell_chime_02",
        // Holidays
        xmas_bells: "christmas_05",
        halloween_door: "horror_10",
        // Misc
        air_horn: "air_horn_03",
        boing1: "boing_01",
        boing2: "boing_03",
        camera: "camera_01",
        squeaky_door: "squeaky_12",
        ticking_clock: "clock_01",
        trumpet: "amzn_sfx_trumpet_bugle_04",
        // Animals
        cat_meow: "amzn_sfx_cat_meow_1x_01",
        dog_bark: "amzn_sfx_dog_med_bark_1x_02",
        lion_roar: "amzn_sfx_lion_roar_02",
        rooster: "amzn_sfx_rooster_crow_01",
        wolf_howl: "amzn_sfx_wolf_howl_02",
        // Scifi
        aircraft: "futuristic_10",
        engines: "amzn_sfx_scifi_engines_on_02",
        red_alert: "amzn_sfx_scifi_alarm_04",
        shields: "amzn_sfx_scifi_sheilds_up_01",
        sirens: "amzn_sfx_scifi_alarm_01",
        zap: "zap_01",
        // Crowds
        applause: "amzn_sfx_crowd_applause_01",
        cheer: "amzn_sfx_large_crowd_cheer_01"
    ]

/******************************************
|    Diagnostic Data
*******************************************/

private getDiagDataJson(Boolean asString = false) {
    try {
        String myId=app.getId()
        updChildVers()
        List zoneDevs = []
        List echoDevs = getEsDevices()
        List actApps = getActionApps()
        List zoneApps = getZoneApps()
        def wsDev = getSocketDevice()
        List appWarnings = []
        List appErrors = []
        List devWarnings = []
        List devErrors = []
        List sockWarnings = []
        List sockErrors = []
        List devSpeech = []
        List actWarnings = []
        List actErrors = []
        List zoneWarnings = []
        List zoneErrors = []
        Map ah = getLogHistory()
        if(ah.warnings.size()) { appWarnings = appWarnings + ah.warnings }
        if(ah.errors.size()) { appErrors = appErrors + ah.errors }
        echoDevs?.each { dev->
            Map h = (Map)dev?.getLogHistory()
            if(h.warnings?.size()) { devWarnings = devWarnings + h.warnings }
            if(h.errors?.size()) { devErrors = devErrors + h.errors }
            if(h.speech?.size()) { devSpeech = devSpeech + h.speech }
        }
        if(wsDev) {
            Map h = (Map)dev?.getLogHistory()
            if(h?.warnings?.size()) { sockWarnings = sockWarnings + h?.warnings }
            if(h?.errors?.size()) { sockErrors = sockErrors + h?.errors }
        }
        actApps?.each { act->
            Map h = (Map)act?.getLogHistory()
            if(h?.warnings?.size()) { actWarnings = actWarnings + h?.warnings }
            if(h?.errors?.size()) { actErrors = actErrors + h?.errors }
        }
        zoneApps?.each { zn->
            Map h = (Map)zn?.getLogHistory()
            if(h?.warnings?.size()) { zoneWarnings = zoneWarnings + h?.warnings }
            if(h?.errors?.size()) { zoneErrors = zoneErrors + h?.errors }
            Map hh = (Map)zn?.relayGetLogHistory()
            if(hh) {
                zoneDevs.push(zn.getLabel())
                if(hh.warnings?.size()) { devWarnings = devWarnings + hh.warnings }
                if(hh.errors?.size()) { devErrors = devErrors + hh.errors }
                if(hh.speech?.size()) { devSpeech = devSpeech + hh.speech }
            }
        }
        Map output = [
            diagDt: getDtNow(),
            app: [
                version: appVersionFLD,
                installed: (String)getInstData('dt'),
                updated: (String)getInstData('updatedDt'),
                timeZone: location?.timeZone?.ID?.toString(),
                lastVersionUpdDt: getTsVal("lastAppDataUpdDt"),
                config: state.appData?.appDataVer ?: null,
                flags: [
                    pollBlocked: (Boolean)state.pollBlocked,
                    resumeConfig: (Boolean)state.resumeConfig,
                    serviceConfigured: (Boolean)state.serviceConfigured,
//                    refreshDeviceData: (Boolean)state.refreshDeviceData,
                    deviceRefreshInProgress: (Boolean)state.deviceRefreshInProgress,
                    noAuthActive: (Boolean)state.noAuthActive,
                    missPollRepair: (Boolean)state.missPollRepair,
                    pushTested: state.pushTested,
                    updateAvailable: (Boolean)state.updateAvailable,
                    devices: [
                        addEchoNamePrefix: (Boolean)settings.addEchoNamePrefix,
                        autoCreateDevices: (Boolean)settings.autoCreateDevices,
                        autoRenameDevices: (Boolean)settings.autoRenameDevices,
                        bypassDeviceBlocks: (Boolean)settings.bypassDeviceBlocks,
                        createOtherDevices: (Boolean)settings.createOtherDevices,
                        createTablets: (Boolean)settings.createTablets,
                        createWHA: (Boolean)settings.createWHA,
                        echoDeviceFilters: settings?.echoDeviceFilter?.size() ?: 0
                    ]
                ],
                stateUsage: "${stateSizePerc()}%",
                warnings: appWarnings ?: [],
                errors: appErrors ?: []
            ],
            actions: [
                version: state.codeVersions?.actionApp ?: null,
                count: actApps?.size() ?: 0,
                warnings: actWarnings ?: [],
                errors: actErrors ?: []
            ],
            zones: [
                version: state.codeVersions?.zoneApp ?: null,
                count: zoneApps?.size() ?: 0,
                warnings: zoneWarnings ?: [],
                errors: zoneErrors ?: []
            ],
            devices: [
                version: state.codeVersions?.echoDevice ?: null,
                count: echoDevs?.size() + zoneDevs.size() ?: 0,
                lastDataUpdDt: getTsVal("lastDevDataUpdDt"),
                models: (Map)state.deviceStyleCnts ?: [:],
                zoneDevs: zoneDevs,
                warnings: devWarnings ?: [],
                errors: devErrors ?: [],
                speech: devSpeech
            ],
            socket: [
                version: state.codeVersions?.wsDevice ?: null,
                warnings: sockWarnings ?: [],
                errors: sockErrors ?: [],
                active: state.websocketActive,
                lastStatusUpdDt: getTsVal("lastWebsocketUpdDt")
            ],
            hub: [
                platform: platformFLD,
                firmware: ((List)location?.hubs)[0]?.getFirmwareVersionString() ?: null,
                type: ((List)location?.hubs)[0]?.getType() ?: null
            ],
            authStatus: [
                cookieValidationState: (Boolean)state.authValid,
                cookieValidDate: getTsVal("lastCookieChkDt") ?: null,
                cookieValidDur: getTsVal("lastCookieChkDt") ? seconds2Duration(getLastTsValSecs("lastCookieChkDt")) : null,
                cookieValidHistory: (List)state.authValidHistory,
                cookieLastRefreshDate: getTsVal("lastCookieRrshDt") ?: null,
                cookieLastRefreshDur: getTsVal("lastCookieRrshDt") ? seconds2Duration(getLastTsValSecs("lastCookieRrshDt")) : null,
                cookieInvalidReason: (!(Boolean)state.authValid && state.authEvtClearReason) ? state.authEvtClearReason : sNULL,
                cookieRefreshDays: (Integer)settings.refreshCookieDays,
                cookieItems: [
                    hasLocalCookie: (state.cookieData && state.cookieData.localCookie),
                    hasCSRF: (state.cookieData && state.cookieData.csrf),
                    hasDeviceId: (state.cookieData && state.cookieData?.deviceId),
                    hasDeviceSerial: (state.cookieData && state.cookieData?.deviceSerial),
                    hasLoginCookie: (state.cookieData && state.cookieData?.loginCookie),
                    hasRefreshToken: (state.cookieData && state.cookieData?.refreshToken),
                    hasFrc: (state.cookieData && state.cookieData?.frc),
                    amazonPage: (state.cookieData && state.cookieData?.amazonPage) ? state.cookieData?.amazonPage : null,
                    refreshDt: (state.cookieData && state.cookieData?.refreshDt) ? state.cookieData?.refreshDt : null,
                    tokenDate: (state.cookieData && state.cookieData?.tokenDate) ? state.cookieData?.tokenDate : null,
                ],
                cookieData: (settings.diagShareSensitveData == true) ? state.cookieData ?: null : "Not Shared"
            ],
            alexaGuard: [
                supported: (Boolean)state.alexaGuardSupported,
                status: (String)state.alexaGuardState,
                dataSrc: (String)state.alexaGuardDataSrc,
                lastSupportCheck: getTsVal("lastGuardSupChkDt"),
                lastStateCheck: getTsVal("lastGuardStateChkDt"),
                lastStateUpd: getTsVal("lastGuardStateUpdDt"),
                stRespLimit: (Boolean)state.alexaGuardDataOverMaxSize
            ],
            server: [
                version: state.codeVersions?.server ?: null,
                amazonDomain: settings?.amazonDomain,
                amazonLocale: settings?.regionLocale,
                lastServerWakeDt: getTsVal("lastServerWakeDt"),
                lastServerWakeDur: getTsVal("lastServerWakeDt") ? seconds2Duration(getLastTsValSecs("lastServerWakeDt")) : null,
                serverPlatform: (Boolean)getServerItem("onHeroku") ? "Cloud" : "Local",
                hostUrl: getServerHostURL(),
                randomName: getRandAppName()
            ],
            versionChecks: [
                minVersionUpdates: getMinVerUpdsRequired(),
                updateItemsOther: codeUpdateItems()
            ],
            bluetoothData: bluetoothDataFLD[myId],
            dndData:  dndDataFLD[myId]
        ]
        String json = new JsonOutput().toJson(output)
        if(asString) {
            return json
        }
        render contentType: sAPPJSON, data: json, status: 200
    } catch (ex) {
        logError("getDiagData: Exception: ${ex}", false, ex)
        if(asString) { return sNULL }
        render contentType: sAPPJSON, data: [status: "failed", error: ex], status: 500
    }
}

def getDiagDataText() {
    String jsonIn = (String)getDiagDataJson(true)
    if(jsonIn) {
        String o = new JsonOutput().prettyPrint(jsonIn)
        render contentType: "text/plain", data: o, status: 200
    }
}

def getDiagData() {
    def ema = new String("dG9uZXN0bzdAZ21haWwuY29t"?.decodeBase64())
    String html = """
        <!DOCTYPE html>
        <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta http-equiv="x-ua-compatible" content="ie=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
                <meta name="description" content="${title}">
                <meta name="author" content="Anthony S.">
                <meta http-equiv="cleartype" content="on">
                <meta name="MobileOptimized" content="320">
                <meta name="HandheldFriendly" content="True">
                <title>Echo Speak Diagnostics</title>
                <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css">
                <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.14.0/css/mdb.min.css" rel="stylesheet">
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.4/umd/popper.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.4.1/js/bootstrap.min.js"></script>
                <script>
                    let cmdUrl = '${getAppEndpointUrl("diagCmds")}';
                </script>
                <style>
                    .bg-less-dark { background-color: #373c40 !important; color: #fff !important;}
                    .rounded-15 { border-radius: 15px !important; }
                    .rounded-5 { border-radius: 5px !important; }
                    .btn-matrix button { width: 135px; }
                    .btn-matrix > .btn:nth-child(Xn+X+1) { clear: left; margin-left: 0; }
                    .btn-matrix > .btn:nth-child(n+X+1) { margin-top: -1px; }
                    .btn-matrix > .btn:first-child { border-bottom-left-radius: 0; }
                    .btn-matrix > .btn:nth-child(X) { border-top-right-radius: 4px !important; }
                    .btn-matrix > .btn:nth-last-child(X) { border-bottom-left-radius: 4px !important; }
                    .btn-matrix > .btn:last-child { border-top-right-radius: 0; }
                    .btn-matrix { margin: 20px; flex-wrap: wrap;}
                    .valign-center { display: grid; vertical-align: middle; align-items: center;}
                </style>
            </head>
            <body class="bg-less-dark">
                <div class="container-fluid">
                    <div class="text-center">
                        <h3 class="mt-4 mb-0">Echo Speaks Diagnostics</h3>
                        <p>(v${appVersionFLD})</p>
                    </div>
                    <div class="text-center">
                        <h5 class="mt-4 mb-0">Diagnostic Data</h5>
                    </div>
                    <div class="px-0">
                        <div class="d-flex justify-content-center">
                            <div class="btn-group btn-matrix mt-1">
                                <button id="emailBtn" onclick="location.href='mailto:${ema?.toString()}?subject=Echo%20Speaks%20Diagnostics&body=${getAppEndpointUrl("diagData")}'" class="btn btn-sm btn-success rounded-15 p-2 my-2 mx-3" type="button"><div class="valign-center"><i class="fas fa-envelope fa-2x m-1"></i><span class="">Share with Developer</span></div></button>
                                <button id="asJsonBtn" onclick="location.href='${getAppEndpointUrl("diagDataJson")}'" class="btn btn-sm btn-info rounded-15 p-2 my-2 mx-3" type="button"><div class="valign-center"><i class="fas fa-code fa-2x m-1"></i><span>View Data as JSON</span></div></button>
                                <button id="asTextBtn" onclick="location.href='${getAppEndpointUrl("diagDataText")}'" class="btn btn-sm btn-dark rounded-15 p-2 my-2 mx-3" type="button"><div class="valign-center"><i class="fas fa-file-alt fa-2x m-1"></i><span>View Data as Text</span></div></button>
                            </div>
                        </div>
                    </div>
                    <div class="text-center">
                        <h5 class="mt-4 mb-0">Remote Commands</h5>
                    </div>
                    <div class="px-0">
                        <div class="d-flex justify-content-center">
                            <section class="btn-group btn-matrix mt-1">
                                <button id="wakeupServer" data-cmdtype="wakeupServer" class="btn btn-sm btn-outline-light rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-server fa-2x m-1"></i><span>Wakeup Server</span></div></button>
                                <button id="forceDeviceSync" data-cmdtype="forceDeviceSync" class="btn btn-sm btn-outline-light rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-sync fa-2x m-1"></i><span>Device Auth Sync</span></div></button>
                                <button id="execUpdate" data-cmdtype="execUpdate" class="btn btn-sm btn-outline-light rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-arrow-circle-up fa-2x m-1"></i><span>Execute Update()</span></div></button>
                                <button id="validateAuth" data-cmdtype="validateAuth" class="btn btn-sm btn-outline-warning rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-check fa-2x m-1"></i><span>Validate Auth</span></div></button>
                                <button id="clearLogs" data-cmdtype="clearLogs" class="btn btn-sm btn-outline-warning rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-broom fa-2x m-1"></i><span>Clear Logs</span></div></button>
                                <button id="cookieRefresh" data-cmdtype="cookieRefresh" class="btn btn-sm btn-outline-danger rounded-5 p-2 my-2 mx-3 cmd_btn" type="button"><div class="valign-center"><i class="fas fa-cookie-bite fa-2x m-1"></i><span>Refresh Cookie</span></div></button>
                            </section>
                        </div>
                    </div>
                    <div class="w-100" style="position: fixed; bottom: 0;">
                        <div class="form-group ml-0 mr-4">
                            <label for="exampleFormControlTextarea1">External URL (Click/Tap to Select)</label>
                            <textarea class="form-control z-depth-1" id="exampleFormControlTextarea1" onclick="this.focus();this.select()" rows="3" readonly>${getAppEndpointUrl("diagData")}</textarea>
                        </div>
                    </div>
                </div>
            </body>
            <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.14.0/js/mdb.min.js"></script>
            <script>
                \$('.cmd_btn').click(function() { console.log('cmd_btn type: ', \$(this).attr("data-cmdtype")); execCmd(\$(this).attr("data-cmdtype")); });
                function execCmd(cmd) { if(!cmd) return; \$.getJSON(cmdUrl.replace('diagCmds', `diagCmds/\${cmd}`), function(result){ console.log(result); }); }
            </script>
        </html>
    """
    render contentType: "text/html", data: html, status: 200
}
/* """ */

def execDiagCmds() {
    String dcmd = params?.cmd
    Boolean status = false
    // log.debug "dcmd: ${dcmd}"
    if(dcmd) {
        switch(dcmd) {
            case "clearLogs":
                clearDiagLogs()
                status = true
                break
            case "validateAuth":
                status = validateCookie(true)
                break
            case "wakeupServer":
                wakeupServer(false, false, "Diagnostic Command")
                status = true
                break
            case "cookieRefresh":
                runCookieRefresh()
                status = true
                break
            case "forceDeviceSync":
                status = refreshDevCookies()
                break
            case "execUpdate":
                updated()
                status = true
                break
        }
    }
    String json = new JsonOutput().toJson([message: (status ? "ok" : "failed"), command: dcmd, version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}


/******************************************
|    Time and Date Conversion Functions
*******************************************/
String formatDt(Date dt, Boolean tzChg=true) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone((TimeZone)location?.timeZone) } }
    return (String)tf.format(dt)
}

static String strCapitalize(String str) { return str ? str.toString().capitalize() : sNULL }
static String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }
static String pluralize(Integer itemVal, String str) { return (itemVal > 1) ? str+"s" : str }

String parseDt(String pFormat, String dt, Boolean tzFmt=true) {
    Date newDt = Date.parse(pFormat, dt)
    return formatDt(newDt, tzFmt)
}

String parseFmtDt(String parseFmt, String newFmt, dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new SimpleDateFormat(newFmt)
    if(location.timeZone) { tf.setTimeZone((TimeZone)location?.timeZone) }
    return (String)tf.format(newDt)
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now)
}

String epochToTime(Date tm) {
    def tf = new SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone((TimeZone)location?.timeZone) }
    return (String)tf.format(tm)
}

String time2Str(time) {
    if(time) {
        Date t = timeToday(time as String, (TimeZone)location?.timeZone)
        def f = new SimpleDateFormat("h:mm a")
        f.setTimeZone((TimeZone)location?.timeZone ?: timeZone(time))
        return (String)f.format(t)
    }
    return sNULL
}

Long GetTimeDiffSeconds(String lastDate, String sender=sNULL) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        Date lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        Long start = lastDt.getTime()
        Long stop = now()
        Long diff = (Long)((stop - start) / 1000L)
        return diff.abs()
    } catch (ex) {
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : sBLANK}lastDate: $lastDate): ${ex}", false, ex)
        return 10000L
    }
}

@SuppressWarnings('GroovyAssignabilityCheck')
static String seconds2Duration(Integer timeSec, Boolean postfix=true, Integer tk=2) {
    Integer years = Math.floor(timeSec / 31536000); timeSec -= years * 31536000
    Integer months = Math.floor(timeSec / 31536000); timeSec -= months * 2592000
    Integer days = Math.floor(timeSec / 86400); timeSec -= days * 86400
    Integer hours = Math.floor(timeSec / 3600); timeSec -= hours * 3600
    Integer minutes = Math.floor(timeSec / 60); timeSec -= minutes * 60
    Integer seconds = Integer.parseInt((timeSec % 60) as String, 10)
    Map d = [y: years, mn: months, d: days, h: hours, m: minutes, s: seconds]
    List l = []
    if(d.d > 0) { l.push("${d.d} ${pluralize(d.d, "day")}") }
    if(d.h > 0) { l.push("${d.h} ${pluralize(d.h, "hour")}") }
    if(d.m > 0) { l.push("${d.m} ${pluralize(d.m, "min")}") }
    if(d.s > 0) { l.push("${d.s} ${pluralize(d.s, "sec")}") }
    return l.size() ? "${l.take(tk ?: 2)?.join(", ")}${postfix ? " ago" : sBLANK}".toString() : "Not Sure"
}

String nextCookieRefreshDur() {
    Integer days = (Integer)settings.refreshCookieDays ?: 5
    String lastCookieRfsh = getTsVal("lastCookieRrshDt")
    if(!lastCookieRfsh) { return "Not Sure"}
    Date lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(Date.parse("E MMM dd HH:mm:ss z yyyy", lastCookieRfsh)))
    Date nextDt = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt + days))
    Integer diff = ( ((Long)nextDt.getTime() - now()) / 1000) as Integer
    String dur = seconds2Duration(diff, false, 3)
    // log.debug "now: ${now} | lastDt: ${lastDt} | nextDt: ${nextDt} | Days: $days | Wait: $diff | Dur: ${dur}"
    return dur
}

@Field static final List<String> weekDaysEnumFLD = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
static List<String> weekDaysEnum() {
    return weekDaysEnumFLD
}

@Field static final List<String> monthEnumFLD = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
static List<String> monthEnum() {
    return monthEnumFLD
}

/******************************************
|   App Helper Utilites
*******************************************/

void updInstData(String key, val) {
    Map iData = state.installData
    iData =  iData ?: [:]
    iData[key] = val
    state.installData = iData
}

private getInstData(String key) {
    Map iMap = state.installData
    if(key && iMap && iMap[key]) { return iMap[key] }
    return null
}

@Field static final List<String> svdTSValsFLD = ["lastCookieRrshDt", "lastServerWakeDt"]
@Field volatile static Map<String,Map> tsDtMapFLD=[:]

private void updTsVal(String key, String dt=sNULL) {
    String val = dt ?: getDtNow()
    if(key in svdTSValsFLD) { updServerItem(key, val); return }

    String appId=app.getId()
    Map data=tsDtMapFLD[appId] ?: [:]
    if(key) data[key]=val
    tsDtMapFLD[appId]=data
    tsDtMapFLD=tsDtMapFLD
}

private void remTsVal(key) {
    String appId=app.getId()
    Map data=tsDtMapFLD[appId] ?: [:]
    if(key) {
        if(key instanceof List) {
            List<String> aa = (List<String>)key
            aa.each { String k->
                if(data.containsKey(k)) { data.remove(k) }
                if(k in svdTSValsFLD) { remServerItem(k) }
            }
        } else {
            String sKey = (String)key
            if(data.containsKey(sKey)) { data.remove(sKey) }
            if(sKey in svdTSValsFLD) { remServerItem(sKey) }
        }
        tsDtMapFLD[appId]=data
        tsDtMapFLD=tsDtMapFLD
    }
}

private String getTsVal(String key) {
    if(key in svdTSValsFLD) {
        return (String)getServerItem(key)
    }
    String appId=app.getId()
    Map tsMap=tsDtMapFLD[appId]
    if(key && tsMap && tsMap[key]) { return (String)tsMap[key] }
    return sNULL
}

Integer getLastTsValSecs(String val, Integer nullVal=1000000) {
        return (val && getTsVal(val)) ? GetTimeDiffSeconds(getTsVal(val)).toInteger() : nullVal
}

@Field volatile static Map<String,Map> serverDataMapFLD=[:]

void updServerItem(String key, val) {
    Map data = atomicState?.serverDataMap
    data =  data ?: [:]
    if(key) {
        String appId=app.getId()
        data[key] = val
        atomicState.serverDataMap = data
        serverDataMapFLD[appId]= [:]
        serverDataMapFLD = serverDataMapFLD
    }
}

void remServerItem(key) {
    Map data = atomicState?.serverDataMap
    data =  data ?: [:]
    if(key) {
        if(key instanceof List) {
            List<String> aa = (List<String>)key
            aa?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
        } else { if(data.containsKey((String)key)) { data.remove((String)key) } }
        String appId=app.getId()
        atomicState?.serverDataMap = data
        serverDataMapFLD[appId]= [:]
        serverDataMapFLD = serverDataMapFLD
    }
}

def getServerItem(String key) {
    String appId=app.getId()
    Map fdata = serverDataMapFLD[appId]
    if(fdata == null) fdata = [:]
    if(key) {
        if(fdata[key] == null) {
            Map sMap = atomicState?.serverDataMap
            if(sMap && sMap[key]) {
                fdata[key]=sMap[key]
            }
        }
        return fdata[key]
    }
    return null
}

void updAppFlag(String key, Boolean val) {
    Map data = atomicState?.appFlagsMap
    data = data ?: [:]
    if(key) { data[key] = val }
    atomicState.appFlagsMap = data
}

void remAppFlag(key) {
    Map data = atomicState?.appFlagsMap
    data = data ?: [:]
    if(key) {
        if(key instanceof List) {
            List<String> aa = (List<String>)key
            aa?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
        } else { if(data.containsKey(key)) { data.remove(key) } }
        atomicState.appFlagsMap = data
    }
}

Boolean getAppFlag(String key) {
    def aMap = atomicState?.appFlagsMap
    if(key && aMap && aMap[key]) { return (Boolean)aMap[key] }
    return false
}

void stateMapMigration() {
    //Timestamp State Migrations
    Map tsItems = [
        "musicProviderUpdDt":"musicProviderUpdDt", "lastCookieChkDt":"lastCookieChkDt", "lastServerWakeDt":"lastServerWakeDt", "lastChildInitRefreshDt":"lastChildInitRefreshDt",
        /* "lastCookieRefresh":"lastCookieRrshDt", */ "lastVerUpdDt":"lastAppDataUpdDt", "lastGuardSupportCheck":"lastGuardSupChkDt", "lastGuardStateUpd":"lastGuardStateUpdDt",
        "lastGuardStateCheck":"lastGuardStateChkDt", "lastDevDataUpd":"lastDevDataUpdDt", "lastMetricUpdDt":"lastMetricUpdDt", "lastMisPollMsgDt":"lastMissedPollMsgDt",
        "lastUpdMsgDt":"lastUpdMsgDt", "lastMsgDt":"lastMsgDt"
    ]
    tsItems?.each { String k, String v-> if(state.containsKey(k)) { updTsVal(v, (String)state[k]); state.remove(k) } }

    //App Flag Migrations
    Map<String, String> flagItems = [:]
    flagItems?.each { String k, String v-> if(state.containsKey(k)) { updAppFlag(v, (Boolean)state[k]); state.remove(k) } }

    //Server Data Migrations
    Map servItems = ["onHeroku":"onHeroku", "serverHost":"serverHost", "isLocal":"isLocal", "lastCookieRefresh":"lastCookieRrshDt" ]
    servItems?.each { String k, String v-> if(state.containsKey(k)) { updServerItem(v, state[k]); state.remove(k) } }
    if(state.generatedHerokuName) { state.herokuName = state.generatedHerokuName; state.remove("generatedHerokuName") }
    updAppFlag("stateMapConverted", true)
}

void settingUpdate(String name, value, String type=sNULL) {
    if(name && type) {
        app.updateSetting(name, [type: type, value: value])
    }
    else if (name && !type){ app.updateSetting(name, value) }
}

void settingRemove(String name) {
    logTrace("settingRemove($name)...")
    if(name && settings.containsKey(name)) { app?.removeSetting(name) }
}

void updCodeVerMap(String key, String val) {
    Map cv = state.codeVersions
    cv = cv ?: [:]
    if(val && (!cv.containsKey(key) || (cv.containsKey(key) && cv[key] != val))) { cv[key] = val }
    if (cv.containsKey(key) && val == sNULL) { cv.remove(key) }
    state.codeVersions = cv
}

void cleanUpdVerMap() {
    Map<String, String> cv = (Map<String,String>)state.codeVersions
    cv = cv ?: [:]
    List<String> ri = ["groupApp"]
    cv.each { String k, String v-> if(v == null) ri.push(k) }
    ri.each { cv.remove(it) }
    state.codeVersions = cv
}

String getRandAppName() {
    if(!(String)state.herokuName && (!(Boolean)getServerItem("isLocal") && !(String)getServerItem("serverHost"))) { state.herokuName = "${app?.name?.toString()?.replaceAll(" ", "-")}-${randomString(8)}"?.toLowerCase() }
    return (String)state.herokuName
}

/******************************************
|   App Input Description Functions
*******************************************/
String getAppNotifConfDesc() {
    String str = sBLANK
    Integer notifDevs = ((List)settings.notif_devs)?.size()
    if(notifDevs) {
        Boolean ok = getOk2Notify()
        str += spanSmBld("Send Notifications Allowed: ") + getOkOrNotSymHTML(ok)
        String ap = getAppNotifDesc()
        String nd = getNotifSchedDesc(true)
        List t0 = (List)settings.notif_devs
        str += notifDevs ? lineBr() + spanSmBr(" ${sBULLET} Sending via: Notification Device${pluralizeStr(t0)} (${notifDevs})") : sBLANK
        str += (ap) ? lineBr(str != sBLANK) + spanSmBldBr("Enabled Alerts:") + ap : sBLANK
        str += (ap && nd) ? lineBr(str != sBLANK) + lineBr() + nd : sBLANK
    }
    return str != sBLANK ? divSm(str, sCLR4D9) : sNULL
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

String getServiceConfDesc() {
    String str = sBLANK
    str += ((String)state.herokuName && (Boolean)getServerItem("onHeroku")) ? "${spanSmBld("Heroku:")} ${spanSmBr("(Configured)")}" : sBLANK
    str += ((Boolean)state.serviceConfigured && (Boolean)getServerItem("isLocal")) ? "${spanSmBld("Local Server:")} ${spanSmBr("(Configured)")}" : sBLANK
    str += "${spanSmBld("Server:")} ${spanSmBr("(${getServerHostURL()})")}"
    str += (settings.amazonDomain) ? spanSmBld("Domain:") + spanSmBr(" (${settings?.amazonDomain})") : sBLANK
    return str != sBLANK ? divSm(str, sCLR4D9) : sNULL
}

String getLoginStatusDesc() {
    String str = sBLANK
    str += "${spanSm("Login Status:")} ${getOkOrNotSymHTML((Boolean)state.authValid)}"
    str += (getTsVal("lastCookieRrshDt")) ? "${lineBr()}${spanSm("Cookie Updated:")} ${spanSm("(${seconds2Duration(getLastTsValSecs("lastCookieRrshDt"))})")}" : sBLANK
    return divSm(str, sCLR4D9)
}

String getAppNotifDesc() {
    String str = sBLANK
    str += (Boolean)settings.sendMissedPollMsg ? bulletItem(str, "Missed Polls") : sBLANK
    str += (Boolean)settings.sendAppUpdateMsg ? bulletItem(str, "Code Updates") : sBLANK
    str += (Boolean)settings.sendCookieRefreshMsg ? bulletItem(str, "Cookie Refresh") : sBLANK
    str += (Boolean)settings.sendCookieInvalidMsg ? bulletItem(str, "Cookie Invalid") : sBLANK
    return str != sBLANK ? str : sNULL
}

String getActionsDesc() {
    List<String> actActs = getActiveActionNames()?.sort()?.collect { spanSm(" ${sBULLET} ${it.replace(' (A)', sBLANK)}") + spanSm(" (Active)", sCLRGRN2) }
    List<String> inactActs = getInActiveActionNames()?.sort()?.collect { spanSm(" ${sBULLET} ${it.replace(' (A ❚❚)', sBLANK)}") + spanSm(" (Paused)", sCLRORG) }
    List<String> acts = (actActs + inactActs).sort()
    Integer a = acts?.size()
    String str = sBLANK
    str += a ? divSm("${spanSmBldBr("Action Status:")}${spanSm(acts?.join("<br>"))}", sCLR4D9) : sBLANK
    str += a ? inputFooter(sTTM) : inputFooter("Tap to create actions using device/location events to perform advanced actions using your Alexa devices.", sCLRGRY)
    return str
}

String getZoneDesc() {
    List<String> actZones = getActiveZoneNames()?.sort()?.collect { spanSm(" ${sBULLET} ${it.replace(' (Z)', sBLANK)}") + spanSm(" (Active)", sCLRGRN2) }
    List<String> inActZones = getInActiveZoneNames()?.sort()
    List<String> iZones = inActZones.findAll { it.contains(" (Z)") }?.collect { spanSm(" ${sBULLET} ${it.replace(' (Z)', sBLANK)}") + spanSm(" (Inactive)", sCLRGRY) }
    List<String> pZones = inActZones.findAll { it.contains(" (Z ❚❚)") }?.collect { spanSm(" ${sBULLET} ${it.replace(' (Z ❚❚)', sBLANK)}") + spanSm(" (Paused)", sCLRORG) }
    List<String> zones = (actZones + iZones + pZones).sort()
    String str = sBLANK
    Integer a = zones.size()
    str += a ? divSm("${spanSmBldBr("Zone Status:")}${spanSm(zones?.join("<br>"))}", sCLR4D9) : sBLANK
    str += a ? inputFooter(sTTM) : inputFooter("Tap to create alexa device zones based on motion, presence, and other criteria.", sCLRGRY)
    return str
}

static String getInputToStringDesc(List inpt, Boolean addSpace=false) {
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

def appInfoSect() {
    Map codeVer = (Map)state.codeVersions ?: null
    String tStr = sBLANK
    Boolean isNote = false
    if(codeVer && (codeVer.server || codeVer.actionApp || codeVer.echoDevice)) {
        List<Map> verMap = []
        verMap.push([name: "App:", ver: "v${appVersionFLD}"])
        if((String)codeVer.echoDevice) verMap.push([name: "Device:", ver: "v${(String)codeVer.echoDevice}"])
        if((String)codeVer.actionApp) verMap.push([name: "Action:", ver: "v${(String)codeVer.actionApp}"])
        if((String)codeVer.zoneApp) verMap.push([name: "Zone:", ver: "v${(String)codeVer.zoneApp}"])
        if((String)codeVer.zoneEchoDevice) verMap.push([name: "Zone Device:", ver: "v${(String)codeVer.zoneEchoDevice}"])
        if((String)codeVer.wsDevice) verMap.push([name: "Socket:", ver: "v${(String)codeVer.wsDevice}"])
        if((String)codeVer.server) verMap.push([name: "Server:", ver: "v${(String)codeVer.server}"])
        if(verMap?.size()) {
            tStr += "<table style='border: 1px solid ${sCLRGRY};border-collapse: collapse;'>"
            verMap.each { it->
                tStr += "<tr style='border: 1px solid ${sCLRGRY};'><td style='border: 1px solid ${sCLRGRY};padding: 0px 3px 0px 3px;'>${spanSmBld((String)it.name)}</td><td style='border: 1px solid ${sCLRGRY};padding: 0px 3px 0px 3px;'>${spanSmBr("${(String)it.ver}")}</td></tr>"
            }
            tStr += "</table>"
        }
        tStr = spanSm(tStr, sCLRGRY)
    }

    section(sectH3TS((String)app?.name, tStr, getAppImg("echo_speaks_3.2x"), sCLR4D9)) {
        if(!(Boolean)state.isInstalled) {
            paragraph spanSmBld("--NEW Install--", sCLR4D9)
        } else {
            if(!state.noticeData) { getNoticeData() }
            Boolean showDocs = false
            Map minUpdMap = getMinVerUpdsRequired()
            List codeUpdItems = codeUpdateItems(true)
            List remDevs = getRemovableDevs()
            if((Boolean)minUpdMap?.updRequired && ((List)minUpdMap.updItems).size()>0) {
                isNote=true
                String str3 = spanSmBldBr("Updates Required:")
                ((List) minUpdMap.updItems).each { item-> str3 += spanSmBr("  ${sBULLET} ${item}") }
                str3 += lineBr() + spanSmBld("If you just updated the code please press Done/Next to let the app process the changes.")
                paragraph divSm(str3, sCLRRED)
                showDocs = true
            } else if(codeUpdItems?.size()) {
                isNote=true
                String str2 = spanSmBldBr("Code Updates Available:")
                codeUpdItems?.each { item-> str2 += spanSmBr("  ${sBULLET} ${item}") }
                paragraph divSm(str2, sCLRRED)
                showDocs = true
            }
            if(showDocs) { updateDocsInput() }
            if(!(Boolean) state.authValid && !(Boolean) state.resumeConfig) {
                isNote = true
                String str4 = spanSmBldBr("Login Issue:")
                str4 += spanSm("You are no longer logged in to Amazon.  Please complete the Authentication Process on the Server Login Page!")
                paragraph divSm(str4, sCLRORG)
            }
            if(state.noticeData && state.noticeData.notices && state.noticeData.notices?.size()) {
                isNote = true
                String str1 = sBLANK
                state.noticeData.notices.each { String item-> str1 += lineBr() + spanSmBr("  ${sBULLET} ${item}") }
                paragraph divSm(str1, sCLRRED)
            }
            if(remDevs?.size()) {
                isNote = true
                String str = spanSmBldBr("Device Removal:") + spanSm("(${remDevs?.size()}) devices can be removed")
                paragraph divSm(str, sCLRRED)
            }
            if(!isNote) { paragraph inputFooter("No Issues to Report", sCLRGRY, true) }
        }
        // paragraph htmlLine()
    }
    List unkDevs = getUnknownDevices()
    if(unkDevs?.size()) {
        section() {
            String title = "[DEVICE SUPPORT]"
            title += (unkDevs?.size() < 4) ? " | ${unkDevs.collect { it.type }.join(",")}" : "(${unkDevs.size()}) Devices"
            String body = "Requesting device support for the following device(s):\n" + unkDevs?.collect { d-> d?.collect { k,v-> "**${k}**: ${v}" }?.join("\n") }?.join("\n\n")?.toString()
            Map params = [ assignees: "tonesto7", labels: "add_device_support", title: title, body: body ]
            String featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?${UrlParamBuilder(params)}"
            href url: featUrl, style: sEXTNRL, required: false, title: inTS1(spanSmBr(unkDevs?.size() > 1 ? "Unknown Devices Found" : "Unknown Device Found", sCLRORG) + "Send the device info to the Developer on GitHub?", "info"), description: spanSm("Tap to open browser", sCLRGRY)
        }
    }
}

@SuppressWarnings('GrDeprecatedAPIUsage')
String UrlParamBuilder(Map<String,Object> items) {
    return items.collect { String k,v -> "${k}=${URLEncoder.encode(v.toString())}" }?.join("&")?.toString()
}

static def getRandomItem(items) {
    def list = new ArrayList<String>()
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()))
}

static String randomString(Integer len) {
    def pool = ["a".."z",0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
//    logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}

Boolean getAccessToken() {
    try {
        if(!state.accessToken) { state.accessToken = createAccessToken() }
        else { return true }
    } catch (ex) {
        logError("getAccessToken Exception: ${ex}")
        return false
    }
}

private getTextEditChild(id) {
    Long longId = id as Long
    def a = getChildAppById(longId)
    return a ?: null
}

def renderConfig() {
    String title = "Echo Speaks"
    Boolean heroku = (settings.useHeroku == null || (Boolean)settings.useHeroku != false)
    String oStr = !heroku ? """
        <div id="localServerDiv" class="w-100 mb-3">
            <div class="my-2 text-left">
                <p>Due to the complexity of node environments I will not be able to support local server setup</p>
                <h5>1. Install the node server</h5>
                <h5>2. Start the node server</h5>
                <h5>3. Open the servers web config page</h5>
                <h5>4. Copy the following URL and use it in the appCallbackUrl field of the Server Web Config Page</h5>
            </div>
            <div class="all-copy nameContainer mx-0 mb-2 p-1">
                <p id="copyCallback" class="m-0 p-0">${getAppEndpointUrl("receiveData") as String}</p>
            </div>
        </div>""" : """
        <div id="cloudServerDiv" class="w-100 mb-3">
            <div class="my-2 text-center">
                <h5>1. Copy the following Name and use it when asked by Heroku</h5>
                <div class="all-copy nameContainer mx-5 mb-2 p-1">
                    <p id="copyHeroku" class="m-0 p-0">${getRandAppName()?.trim()}</p>
                </div>
            </div>
            <div class="my-2 text-center">
                <h5>2. Tap Button to deploy to Heroku</h5>
                <a href="https://heroku.com/deploy?template=https://github.com/tonesto7/echo-speaks-server/tree/master${getEnvParamsStr()}">
                    <img src="https://www.herokucdn.com/deploy/button.svg" alt="Deploy">
                </a>
            </div>
        </div>"""

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
            .btn-rounded { border-radius: 50px!important; }
            span img { width: 48px; height: auto; }
            span p { display: block; }
            .all-copy p { -webkit-user-select: all; -moz-user-select: all; -ms-user-select: all; user-select: all; }
            .nameContainer { border-radius: 18px; color: rgba(255,255,255,1); font-size: 1.5rem; background: #666; -webkit-box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3); box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3); text-shadow: 1px 1px 1px rgba(0,0,0,0.2); }
        </style>
    <head>
    <body>
        <div style="margin: 0 auto; max-width: 600px;">
            <form class="p-1">
                <div class="my-3 text-center"><span><img src="${getAppImg("echo_speaks_3.1x")}"/><p class="h4 text-center">Echo Speaks</p></span></div>
                <hr>
                ${oStr}

            </form>
        </div>
    </body>
    <script>
        \$("#copyHeroku").on("click", function () {
            console.log("copyHerokuName Click...")
            \$(this).select();
        });
        \$("#copyCallback").on("click", function () {
            console.log("copyCallback Click...")
            \$(this).select();
        });
    </script>
    """
    render contentType: "text/html", data: html
}
/* """ */

def renderTextEditPage() {
    String actId = params?.cId
    String inName = params?.inName
    Map inData = [:]
    // log.debug "actId: $actId | inName: $inName"
    if(actId && inName) {
        def actApp = getTextEditChild(actId)
        if(actApp) { inData = actApp?.getInputData(inName) }
    }
    String html = """
        <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta http-equiv="x-ua-compatible" content="ie=edge">
                <title>Echo Speak Response Designer</title>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.9.0/css/all.min.css">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/mdbootstrap@4.8.10/css/mdb.min.css" integrity="sha256-iNGvtX88EOpA/u8LtFgUkDiIsoBSwI+GbErXYrSzpuA=" crossorigin="anonymous">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/codemirror.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/theme/material.min.css" rel="stylesheet">
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vanillatoasts@1.3.0/vanillatoasts.css" integrity="sha256-U06o/6s4HELYo2A3Gd7KPGQMojQiAxY9B8oE/hnM3KU=" crossorigin="anonymous">
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.4/umd/popper.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/js/bootstrap.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/codemirror.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/mode/simple.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/mode/xml/xml.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/hint/show-hint.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/hint/xml-hint.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/edit/closetag.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/edit/matchtags.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/mode/htmlmixed/htmlmixed.min.js"></script>
                <style>
                    form div { margin-bottom: 0.5em; margin: 0 auto; }
                    .form-control { font-size: 0.7rem; }
                    button.btn.btn-info.btn-block.my-4 { max-width: 200px; text-align: center; }
                    .ssml-buttons { font-size: .8rem; margin-bottom: 5px; text-decoration: none; }
                    .ssml-buttons, h3 { font-size: 0.9rem; text-decoration: underline; }
                    .ssml-buttons, input { font-size: 0.9rem; text-decoration: none; }
                    .ssml-button { font-weight: normal; margin-left: 5px; margin-bottom: 5px; cursor: pointer; border: 0.5px solid #b1a8a8; border-radius: 7px; padding: 3px; background-color: #EEE; -webkit-appearance: none; -moz-appearance: none }
                    .ssml-button:hover { background-color: #AAA }
                    .ssml-button:first-child { margin-left: 0 }
                    .no-submit { cursor: text }
                </style>
            </head>
            <body class="m-2">
                <div class="p-3"><button type="button" class="close" aria-label="Close" onclick="window.open('','_parent',''); window.close();"><span aria-hidden="true">×</span></button></div>
                <div style="float: left; clear:both;position: fixed; top: 0px;"><small>v${inData?.version}</small></div>
                <div class="w-100 pt-0">
                    <form>
                        <div class="container px-0">
                            <div class="text-center">
                                <p id="inputTitle" class="h5 mb-0">Text Field Entry</p>
                                <p class="mt-1 mb-2 text-center">Response Designer</p>
                            </div>
                            <div class="px-0">
                                <textarea id="editor"></textarea>
                                <div class="text-center blue-text"><small>Each line item represents a single response. Multiple lines will trigger a random selection.</small></div>
                                <div class="d-flex justify-content-center">
                                    <button id="clearBtn" style="border-radius: 50px !important;" class="btn btn-sm btn-outline-warning px-1 my-2 mx-3" type="button"><i class="fas fa-times-circle mr-1"></i>Clear All</button>
                                    <button id="newLineBtn" style="border-radius: 50px !important;" class="btn btn-sm btn-outline-info px-1 my-2 mx-3" type="button"><i class="fas fa-plus mr-1"></i>New Response</button>
                                    <button id="submitBtn" style="border-radius: 50px !important;" class="btn btn-sm btn-outline-success my-2" type="submit"><i class="fa fa-save mr-1"></i>Save Responses</button>
                                </div>
                            </div>
                            <div class="row mt-2 mx-auto">
                                <div class="px-2 col-12">
                                    <div class="card my-3 mx-0">
                                        <h5 class="card-header px-2 py-0"><i class="fas fa-info px-2 my-auto"></i>Builder Items</h5>
                                        <div class="card-body py-0 px-2 mx-0">
                                            <div class="text-center orange-text mt-0 mb-2">
                                                <small>Select a line item and tap on an item to insert or replace the item in the text.</small>
                                            </div>
                                            <div class="p-1 mx-auto">
                                                <div class="shortcuts-wrap" style="display: block;">
                                                    <div class="row">
                                                        <div class="col-12">
                                                            <div class="ssml-buttons">
                                                                <h3>Event Variables</h3>
                                                                <input class="ssml-button" type="button" unselectable="on" value="Type" data-ssml="evttype">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Value" data-ssml="evtvalue">
                                                                <input class="ssml-button" type="button" unselectable="on" value="DeviceName" data-ssml="evtname">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Unit" data-ssml="evtunit">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Date" data-ssml="evtdate">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Time" data-ssml="evttime">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Date/Time" data-ssml="evtdatetime">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Duration (Seconds)" data-ssml="evtduration">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Duration (Minutes)" data-ssml="evtdurationmin">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Duration Value (Seconds)" data-ssml="evtdurationval">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Duration Value (Minutes)" data-ssml="evtdurationvalmin">
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="my-3">
                                                    <small id="inputDesc">Description goes here.</small>
                                                </div>
                                            </div>
                                            <hr class="mb-1 mx-auto" style="background-color: #696969; width: 100%;">
                                            <div class="p-1 mx-auto">
                                                <h4>SSML Markup Items</h4>
                                                <div class="shortcuts-wrap" style="display: block;">
                                                    <div class="row">
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>BREAK</h3><input class="ssml-button" type="button" unselectable="on" value="50ms" data-ssml="break"><input class="ssml-button" type="button" unselectable="on" value="200ms" data-ssml="break"><input class="ssml-button"
                                                                    type="button" unselectable="on" value="500ms" data-ssml="break"><input class="ssml-button" type="button" unselectable="on" value="800ms" data-ssml="break"><input class="ssml-button" type="button" unselectable="on"
                                                                    value="1s" data-ssml="break"><input class="ssml-button" type="button" unselectable="on" value="2s" data-ssml="break">
                                                            </div>
                                                        </div>
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>EMPHASIS</h3><input class="ssml-button" type="button" unselectable="on" value="strong" data-ssml="emphasis"><input class="ssml-button" type="button" unselectable="on" value="reduced" data-ssml="emphasis">
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>PITCH</h3><input class="ssml-button" type="button" unselectable="on" value="x-low" data-ssml="pitch"><input class="ssml-button" type="button" unselectable="on" value="low" data-ssml="pitch"><input class="ssml-button"
                                                                    type="button" unselectable="on" value="medium" data-ssml="pitch"><input class="ssml-button" type="button" unselectable="on" value="high" data-ssml="pitch"><input class="ssml-button" type="button" unselectable="on"
                                                                    value="x-high" data-ssml="pitch">
                                                            </div>
                                                        </div>
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>RATE</h3><input class="ssml-button" type="button" unselectable="on" value="x-slow" data-ssml="rate"><input class="ssml-button" type="button" unselectable="on" value="slow" data-ssml="rate"><input class="ssml-button"
                                                                    type="button" unselectable="on" value="medium" data-ssml="rate"><input class="ssml-button" type="button" unselectable="on" value="fast" data-ssml="rate"><input class="ssml-button" type="button" unselectable="on"
                                                                    value="x-fast" data-ssml="rate">
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>VOLUME</h3><input class="ssml-button" type="button" unselectable="on" value="silent" data-ssml="volume"><input class="ssml-button" type="button" unselectable="on" value="x-soft" data-ssml="volume">
                                                                <input class="ssml-button" type="button" unselectable="on" value="soft" data-ssml="volume"><input class="ssml-button" type="button" unselectable="on" value="medium" data-ssml="volume"><input class="ssml-button"
                                                                    type="button" unselectable="on" value="loud" data-ssml="volume"><input class="ssml-button" type="button" unselectable="on" value="x-loud" data-ssml="volume">
                                                            </div>
                                                        </div>
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>WHISPER</h3><input class="ssml-button" type="button" unselectable="on" value="whisper" data-ssml="whisper">
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>VOICE</h3>
                                                                <select class="browser-default custom-select custom-select-sm mb-2" id="voices">
                                                                    <option value="Naja" class="x-option ember-view">Danish (F) - Naja</option>
                                                                    <option value="Mads" class="x-option ember-view">Danish (M) - Mads</option>
                                                                    <option value="Lotte" class="x-option ember-view">Dutch (F) - Lotte</option>
                                                                    <option value="Ruben" class="x-option ember-view">Dutch (M) - Ruben</option>
                                                                    <option value="Nicole" class="x-option ember-view">English, Australian (F) - Nicole</option>
                                                                    <option value="Russell" class="x-option ember-view">English, Australian (M) - Russell</option>
                                                                    <option value="Amy" class="x-option ember-view">English, British (F) - Amy</option>
                                                                    <option value="Emma" class="x-option ember-view">English, British (F) - Emma</option>
                                                                    <option value="Brian" class="x-option ember-view">English, British (M) - Brian</option>
                                                                    <option value="Raveena" class="x-option ember-view">English, Indian (F) - Raveena</option>
                                                                    <option value="Aditi" class="x-option ember-view">English, Indian (F) - Aditi</option>
                                                                    <option value="Ivy" class="x-option ember-view">English, US (F) - Ivy</option>
                                                                    <option value="Joanna" class="x-option ember-view">English, US (F) - Joanna</option>
                                                                    <option value="Kendra" class="x-option ember-view">English, US (F) - Kendra</option>
                                                                    <option value="Kimberly" class="x-option ember-view">English, US (F) - Kimberly</option>
                                                                    <option value="Salli" class="x-option ember-view">English, US (F) - Salli</option>
                                                                    <option value="Joey" class="x-option ember-view">English, US (M) - Joey</option>
                                                                    <option value="Justin" class="x-option ember-view">English, US (M) - Justin</option>
                                                                    <option value="Matthew" class="x-option ember-view">English, US (M) - Matthew</option>
                                                                    <option value="Geraint" class="x-option ember-view">English, Welsh (M) - Geraint</option>
                                                                    <option value="Celine" class="x-option ember-view">French (F) - Céline</option>
                                                                    <option value="Lea" class="x-option ember-view">French (F) - Léa</option>
                                                                    <option value="Mathieu" class="x-option ember-view">French (M) - Mathieu</option>
                                                                    <option value="Chantal" class="x-option ember-view">French, Canadian (F) - Chantal</option>
                                                                    <option value="Marlene" class="x-option ember-view">German (F) - Marlene</option>
                                                                    <option value="Vicki" class="x-option ember-view">German (F) - Vicki</option>
                                                                    <option value="Hans" class="x-option ember-view">German (M) - Hans</option>
                                                                    <option value="Aditi" class="x-option ember-view">Hindi (F) - Aditi</option>
                                                                    <option value="Dóra" class="x-option ember-view">Icelandic (F) - Dóra</option>
                                                                    <option value="Karl" class="x-option ember-view">Icelandic (M) - Karl</option>
                                                                    <option value="Carla" class="x-option ember-view">Italian (F) - Carla</option>
                                                                    <option value="Giorgio" class="x-option ember-view">Italian (M) - Giorgio</option>
                                                                    <option value="Takumi" class="x-option ember-view">Japanese (M) - Takumi</option>
                                                                    <option value="Mizuki" class="x-option ember-view">Japanese (F) - Mizuki</option>
                                                                    <option value="Seoyeon" class="x-option ember-view">Korean (F) - Seoyeon</option>
                                                                    <option value="Liv" class="x-option ember-view">Norwegian (F) - Liv</option>
                                                                    <option value="Ewa" class="x-option ember-view">Polish (F) - Ewa</option>
                                                                    <option value="Maja" class="x-option ember-view">Polish (F) - Maja</option>
                                                                    <option value="Jacek" class="x-option ember-view">Polish (M) - Jacek</option>
                                                                    <option value="Jan" class="x-option ember-view">Polish (M) - Jan</option>
                                                                    <option value="Vitoria" class="x-option ember-view">Portugese, Brazilian (F) - Vitória</option>
                                                                    <option value="Ricardo" class="x-option ember-view">Portugese, Brazilian (M) - Ricardo</option>
                                                                    <option value="Ines" class="x-option ember-view">Portugese, European (F) - Inês</option>
                                                                    <option value="Cristiano" class="x-option ember-view">Portugese, European (M) - Cristiano</option>
                                                                    <option value="Carmen" class="x-option ember-view">Romanian (F) - Carmen</option>
                                                                    <option value="Tatyana" class="x-option ember-view">Russian (F) - Tatyana</option>
                                                                    <option value="Maxim" class="x-option ember-view">Russian (M) - Maxim</option>
                                                                    <option value="Conchita" class="x-option ember-view">Spanish, European (F) - Conchita</option>
                                                                    <option value="Enrique" class="x-option ember-view">Spanish, European (M) - Enrique</option>
                                                                    <option value="Penélope" class="x-option ember-view">Spanish, US (F) - Penélope</option>
                                                                    <option value="Miguel" class="x-option ember-view">Spanish, US (M) - Miguel</option>
                                                                    <option value="Astrid" class="x-option ember-view">Swedish (F) - Astrid</option>
                                                                    <option value="Filiz" class="x-option ember-view">Turkish (F) - Filiz</option>
                                                                    <option value="Gwyneth" class="x-option ember-view">Welsh (F) - Gwyneth</option>
                                                                </select>
                                                                <input class="ssml-button" type="button" unselectable="on" value="Add Voice" data-ssml="voice">
                                                            </div>
                                                        </div>
                                                        <div class="col-6">
                                                            <div class="ssml-buttons">
                                                                <h3>SAY-AS</h3><input class="ssml-button" type="button" unselectable="on" value="number" data-ssml="say-as"><input class="ssml-button" type="button" unselectable="on" value="spell-out" data-ssml="say-as">
                                                                <input class="ssml-button" type="button" unselectable="on" value="ordinal" data-ssml="say-as"><input class="ssml-button" type="button" unselectable="on" value="digits" data-ssml="say-as"><input class="ssml-button"
                                                                    type="button" unselectable="on" value="date" data-ssml="say-as"><input class="ssml-button" type="button" unselectable="on" value="time" data-ssml="say-as"><input class="ssml-button" type="button" unselectable="on"
                                                                    value="speechcon" data-ssml="say-as">
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <hr class="mb-1 mx-auto" style="background-color: #696969; width: 100%;">
                                            <div class="text-center align-text-top blue-text"><small>Speak tags will automatically be added to lines where SSML is inserted.</small></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/mdbootstrap@4.8.10/js/mdb.min.js" integrity="sha256-wH71T2mMsoF6NEYmAPxpPvUbgALoVRlZRHlMlCQpOnk=" crossorigin="anonymous"></script>
                <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vanillatoasts@1.3.0/vanillatoasts.min.js"></script>
                <script>
                    let inName = '${inName}';
                    let actId = '${actId}'
                    let rootUrl = '${getAppEndpointUrl("textEditor/${actId}/${inName}")}';
                    let curText = '${inData?.val}';
                    curText = (curText == null || curText == 'null' || curText == '') ? '${inData?.template}' : curText
                    let curTitle = '${inData?.title}';
                    let curDesc = '${inData?.desc}';
                    let selectedLineNum = undefined;

                    // SSML Links
                    let ssmlTestUrl = "https://topvoiceapps.com/ssml"
                    let ssmlDocsUrl = "https://developer.amazon.com/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html"
                    let ssmlSoundsUrl = "https://developer.amazon.com/docs/custom-skills/ask-soundlibrary.html"
                    let ssmlSpeechConsUrl = "https://developer.amazon.com/docs/custom-skills/speechcon-reference-interjections-english-us.html"

                    function cleanEditorText(txt) {
                        txt = txt.split(';').filter(t => t.trim().length > 0).map(t => t.trim()).join(';');
                        txt = txt.endsWith(';') ? txt.replace(/;([^;]*)\$/, '\$1') : txt;
                        txt = txt.replace('%duration_min%', '%durationmin%');
                        return txt.replace(/  +/g, ' ').replace('> <', '><');//.replace("\'", '');
                    }

                    \$(document).ready(function() {
                        \$('#inputTitle').text(curTitle);
                        \$('#inputDesc').html(curDesc);
                        \$('#editor').val(cleanEditorText(curText));
                        CodeMirror.defineSimpleMode("simplemode", {
                            start: [{
                                regex: /<speak>|<\\/speak>/,
                                token: 'tag'
                            }, {
                                regex: /<voice[^>]+>|<\\/voice>/,
                                token: 'attribute'
                            }, {
                                regex: /<say-as[^>]+>|<\\/say-as>|<emphasis[^>]+>|<\\/emphasis>/,
                                token: 'string'
                            }, {
                                regex: /<prosody[^>]+>|<\\/prosody>/,
                                token: 'keyword'
                            }, {
                                regex: /%[a-z]+%/,
                                token: "variable-2"
                            }, {
                                regex: /<[^>]+>/,
                                token: 'variable'
                            }, {
                                regex: /=["']?((?:.(?!["']?\\s+(?:\\S+)=|[>"']))+.)["']?/,
                                token: 'value'
                            }, {
                                regex: /REPLACE_THIS_TEXT/,
                                token: "error"
                            }, {
                                regex: /0x[a-f\\d]+|[-+]?(?:\\.\\d+|\\d+\\.?\\d*)(?:e[-+]?\\d+)?/i,
                                token: "number"
                            }]
                        });

                        let editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
                            theme: 'material',
                            mode: 'simplemode',
                            lineNumbers: true,
                            lineSeparator: ';',
                            styleActiveLine: true,
                            showCursorWhenSelecting: true,
                            autoCloseTags: true,
                            lineWrapping: false,
                            autocorrect: false,
                            autocapitalize: false,
                            spellcheck: true,
                            styleActiveLine: {
                                nonEmpty: true
                            }
                        });
                        editor.markClean();

                        \$('#newLineBtn').click((e) => {
                            let doc = editor.getDoc();
                            let cursor = doc.getCursor();
                            let lineCnt = editor.lineCount();
                            doc.replaceRange(';', CodeMirror.Pos(lineCnt - 1));
                        });

                        \$('#clearBtn').click((e) => {
                            editor.setValue('');
                        });

                        function updateInfo(instance, changeObj) {
                            selectedLineNum = changeObj.to.line;
                            if (!editor.isClean()) {
                                \$('#submitBtn').removeClass('btn-outline-success').addClass('btn-success');
                            } else {
                                \$('#submitBtn').removeClass('btn-success').addClass('btn-outline-success');
                            }
                            // \$('#lineCnt').text('Response Cnt: ' + changeObj.to.line);
                        }

                        editor.on("change", updateInfo);
                        editor.on('beforeSelectionChange', (e) => {
                            //Handles selection changes
                        })

                        \$('form').submit(function(e) {
                            console.log('form submit...')
                            e.preventDefault();
                            let xmlhttp = new XMLHttpRequest();
                            xmlhttp.open("POST", rootUrl);
                            xmlhttp.onreadystatechange = () => {
                                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                                    // console.log(xmlhttp.responseText);
                                    let toast = VanillaToasts.create({
                                        title: 'Echo Speaks Actions',
                                        text: 'Responses saved successfully!',
                                        type: 'success',
                                        icon: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEsAAABLCAYAAAA4TnrqAAAbSUlEQVR42u2cd5QVRdrGf1XdfePcyYkZYchBgigICoqoi+gChjWjYMRFRdQ1x9U1oLK6qKBgFlE/I4iIrq6uYlgRUaJkmGGGyXluvt1d3x99h6CIJFe/7/Cc02fuubfi02+99dRbVQMHcAAHcAAHcAAHcAAH8GtA/NYN2AG6Cz09D+lPR7i8KCuBCjeRaKiCWOi3bt1/lyzhSeGcSAuzDxmGd/B5GHmdkKlZaCnpSF8a0u9H84HQQUjnQYGKgYpEUZFm7HATdrAOs2YzkSUf0zDvKYy2PUiUrvo/TpYvBf9RFxD+7HnSL3seo/Bg9DbdCJxqUHXl312uzkekSX9qtnR7c4ThzhKGkSE0mSJ0wyN0Q0OZtrATcbCCWGYTVrxOJaI1KtpSm6jY0HDipPMiC7+AeMl64mVraPnkf/D0GEjV07dhh5v/b5Dl++PNHPXeJBbd8AFGYU+8g9rS/PyLPv2gXu2lP6O39Ab6Cpenp9D19kKTOULKgJDSLaTQhQSEcCxLOC0UQiFQFqi4wA6BVYeySjFjq1QstMQONS4za0rWD7x5VOOK+XXESn6g7qUHCBx1MmWTx/8OyZKSwOjHSaz7Av9JN+Lq2pfIwtl+I797b5mSeZxw+44Vuqun0PUcIaUupIAkIdtIcYjZ+lkmv5diW7rt8ghhA0phmw3YibUkIp/bocZ/mbVl3+aNO7a+4Z11tHw1H1e7rpTedQFmS81vT1bKWQ8TXT6PtLOnkHlTH+ruXdhWyy46UXpTzhC663Ch6xlIuY0UFGDHBWYjyqpGWVUCuxpBvcBuRoqI0DRLYEuU7RaaDAhBhpAyR2gyX+hartS0TKFpXqTYRiYKlBnGSqxQ8fA7Zl35nLVjD1l98Oxiu/nzObjyiyi+9yLs5sb/Plme468m87opRL5cRPolh9P0zNcdtOwOo4U3dbQw3N2FlFLI1tRWAitehhVbjh3/FjOyVEWbN9rB2mqrZmNL02s3x46sqbFr1oNZA8RBukHPhtTO8LUQImf8wy53u55+PSM3W0tJayc9vt7C5eovDXdf4XJ1kJrmRUsSpxRY8TI7Gpxj1lU8v+b8Pt/3eGO1Wn1Wdw66/nFKJ1/13yMrdfxs7GANnoHnY25emKXndhsjfWl/Fi5Pd6RMFm4rrHixMiOfqVjL+3bjlm9jS94ty/nr/fHY0hasxi3YjeVYdaVYleuJbV6OHaxD1ZSSaKxE8wbQ2nRG86fi7tAbV2EXjLx26DmFGFkFuDtns+GUblruxCdyXXlFfbRA+gnS4x0mXe7uQtMMx5IVmPEKO9LycqK6dHrmHw/bUPv6Jxi5B1F885mE1y/79cjSux5N2vi5WJWraXzoSJF538Y/aIHcW4XLMwRNk0IAykooM/otifCrdkPZ/PoHhm7Ke6LWNkuXEF//Nc2zrsZ31AWEFryw11YdOPwEWhZ9SM6Fd+E/ZAieTodw5qhMXp66IN/VtvNQzRc4T7o9xwhdDwgpEMpGmdFVVrBpcnDhh//j7zs4ctypnXnjvGupfGXK/ifLfcwVFH06jdpJpZhblqUZRf2vFr6MiUJ3ZQkBYFumFfsqFgs9GWoMzZ8Wf68po3wxJ1UvIF3F9pqYn7RYgfigdOtXmj+VgutmEFr8MekjL8bXcyDNH7/s9fUaNERPyxovvf7hUte9CMC2Yna4+dVY6dq7U/ocUVz28DV4u/Sl+O6L9h9Z3j/eTmT+vWQ/HscsX95By+n4oPSkno6UEhRY8WJ/S+mM6d9e+93whi8i6D4docntirABaydFy+RjOTTssq0hBCuAKG2yEc99/5NEGSeeT/sHX6Lxw3n4+w6h6ZPXfIEBJ4zU07Ovlx7v4UiBQKGi4W8SdeV/yRrR9cuSe54GTaPk7kv2nSz3kMuJLXiS7OkKc8vKQ7TMomnS7R+MEKBsSyWC79SGgn9V/+7bGc39NIgK4NvtOq+AXkCfHxEigM3AUuBYwP8LTYkD1wAvUrBzsloR6H8cRfe9Tmj5V6QfP4rQ4q8L3UVdrtP8qeOErqcIASoRKzYbqq8+7o/t5s6542mElBT/7dKfLVPyCzC6H8+Rnz1B1jSFWbayr5bZ/jnhShnsyMREsx2s/lt83ecXPb3x4RVoni4gsoEvkXIciBtA3AziRuBTwAv4tnu8wHIQtwLRH/22sycd6AFAKLHLdrd8+wkrhmcTXb2YM/qC9Pm3lE+9/sZEXcV4FY+VAEiXu72RmTvjk3c3/qn/PeOwmuo56JoH95Isw0fqxH+x9MFqzLKlXWVm0XTh8h+GAKx4td1cNbHu1rb3Sn9286Wlr7VaEICFsjNAzQK1wHlodQrrgJeBf7N1WCobWAW8DqxJprOBb4CXgO/Y0UrB3tWI3YaKZ+7mDsON1VxP2pDTzKyR7V6OVxSPtaPh5SgQhjvfyCl4bO3cDScUXnsDZVNuIm3wSXtOVuDydzFLvsMsXZIlM9v/Xbj8AwEw41V2c9WV+bcc9GLqpW/YdqguKbd3gAVsAYqBMsDAGW5nAZOT5E12Oi/qgftAXAdcDNQCrwLnAo8CZwOzd4udncA246wbPxSroZq1F92Cr1v3BfHKzRfasfASAOFyFxq5Bf8Ifr+m96Gf1dPl0bl7Rpar37moYA3B2ddJ/aBDrxPuwCicoddkNVfckHlr2zc3j3sNZZs0Tx3x4+wCKZoQ3IngMhATgB+SVuIDPgD+ClQDYUADbgc1BcF6YBnwJHAy8AkwBHgCSLAP2rDk3nFIt4clg9Lwdu7xXby6bLwdj64DkC7PwUZW3n3B7z9PDy1fSMd7Z+4+WRkPvYL70LMIjH52mPCmXy6EdJx5uGFyw+3tX6667E0EkpanztxZdoWtslDMRPEpqH8ChwBrgZ5APjAWuB/wgErF8UXtAReOZdUA/YHU5N8KILQvZAGUz7iL3LMnsunGswkc3m1hoqb8RpVINCBA+lJG+A7uf8nACwcTWb8Co7Dol8lyHT6ayHtfEF/7UbpMybleaEY6KFSs5d1E2eLH02782kZ30/LMWbtql8IZihbOLBYCcoESIAK8CdyAY1lJ9bTVL6UAaTj+jeTfDJwJYfec1a4Ie/ZeAv2GUPX8TFac0vkdK9g4VdlKCalLPTVzwqLXl/XMOeNy2l//6C+Tde03L+PqfRRabo9RwvAdA6CseIXVVDFJb3t4s9amJy1PnryzrGJruULWIzgfOBohjgKmAWOAjcApwG1AlkNM6xJ7a/7MZNoXgREI3gYuBNzbavrFiXyXKHlgAlpKOj1mfauim1ZNs6Ohr0EhDFd7I6fNxctGdhDR4tU75NF3VtC0y2aj4sGAu9fJY5GagVKoSNOspnt6fOM9bTLxVR/8bBuAIHA0tv0UrVaglA0cDAwAnsGREefh+KQ6UPfjaKwCFA8B7XB0WT6wAsU4YBiOFa5PSIOiEd+QP2LfgiZBoO7TTznspuFVkfWbp0qPt5+QmkvzB07vNXvlDKG71kY2/kDtuzN3sISt8J07HXfvU1DhhqEyu/M7QjNSlRktt6p+OEm4A8usqjW0TB/1k4rVie3A8T+H4eihH796O/lo29VrYsUUmltPSEPZQgpNWVK3LTNJdGtalcwbVEJ8JxpKQifdvevRKAVUBqEyGBdSCAYUGir8I2mmgLowVDeV4rfWpnt7HDFPenyDUQqzseaalEPyHv0iextFPyFLP+RU2rw2m+DsmnulP/s2ENjRhldCr4+7QO9wtBl+63qwzZ8ji911KSHdh3/eGlJmqbMM2/6DEqJaIZoEKksolbWzggQK3TY1gdpdk1JAC45s8f5MGgFUm+H4o+26hM7WM3MfQUisUPP8+nkvnK7506Ib77xo52QFxr+LsuJ+V7fhc4Xbfxy2bdvBqotlRpsXVaiO+huyd7Odu0buCxFwHP6/gCXAFKAJyAFuBE7bD9VEgXHA0cBlu2ZVjMv3rfzGVdTzE2G4s1Q8Uhpet+x4oRnrfhjdD9iJg5eZRWjZnXLR9E4ASpl1Ktb8vVWzkaYZ+6P9O8CF4/BvwZEH03CG8M1AabKzcRwp0fCjvM1AJbC9mceA8mR6gDiCZTi+FByf17qnFsIRzc0AAvzWhv9sVIn4BgA0PUcPZHR2FWyTDz9x8NKXiUpE2iBkJgC2VWG3VJajFNb6z/c3WQJoEYigQnmBbjiqfi2Oj7o8Scam5N/bgFHA/GS6BmAo8DccgXsbzmrBwlH9F6DQcIbhomS6S4G2wJ1AY/KF3Q5Kc918ZVB9Vr8B1AAhpEd6fO3dbbKQvlTscPNOpIPLD5qRhZAehyyzxqpZF7Lqivc3USQ71UuhzhdCPA8cAVyXJNEHLATW46j9dsBdwGIcSzw82eF3gVfYFuWYgiOApwN1OJPESuBqoANwQjL9CuCOZJ1LASu2CpRtlaOUs8Ok6bneruDr0M0xpJ+8at2N0D0+sVXIqJBVs85UwVp+JUjgNqXUO8AVQAWCPyeJMnA02TAcnVUJfIQzrJYDb+NY3Hc4suQwnCXVIraJYil17W0hxBLgJBxHPwhHs92KszIYAAjVBAhatko+Xfe7ACO7DfAzOgu1/Uz0q+7DasC85PMkjqUMEIgzFKokWXlLMm2rr/GBcIH6A3AojvNuh2NRnwEP4Qytucn8lm1axyVJfRg4EuiS/BwE/g7cDnSWBogItE7EP+75T8kyYygrGkmGTTTAr2V31hH6rgNIeweFsyZ8EWeteDSwWCnVDuibfFlvgNKA93CG1ymg5uIssIPJ729LkmniWMoinInBSn43CDhWCHGaUur55At4B7gSCOCsP3WRCqqZlFZppywzHAcStRU7J0vFQ2Al6pSyowLNQGo5Wm4XP0pFfgWybKA3jg95ESdq2g+4AOjo/K6OwJmxBuE4/CJgKvA0zkQwHmd4dcKJTCzCCQO14KwvLwa6A0cppSYlybsQZ4h/kqzvUuApowuICq3AidUrlGlWRdZBpGTtzsmyww2g7EqUXQ8EkHobGcgvQHPVap0GYW34an+S1Trc+yUfxTbrb0iS2RO46Uf5uuMMo+3RK0nWjzFxu8/b7+XfsmMyYcfufyzFe8xFSclkx+xYuDheXocVbPoZsuqLUVaiSsvqtBHdXSSkniU8aX1lWptlqRe9TsPtB+0nngQCGhXqY5y3/GPFHsGZ/jvgOPR9jjb8bEOgUSnxjdbl6E7CcHUCAZZZbTU3rLfDwa0Jf0JWdOEL5L36Zig0u+Yr4fIdC1IK3TO8+emTXzE6HWduW6rtKxQKIgLxF1D3JaROvVe38kMRvX1ws1HpyTWL0zKUPw4piSjq15xoBJF+F7hqKj7Ku1ZoejaASsRXhFctKpO+1B1Y3QH+C1/C1W2Ys5DO6jRXaEZAmdFyq3LlicIdWG5Vr93pQnpPUTgzSsJWhHUPYza8wRMvnWXQMe84hNYP8AAWSpWQaH7/4X53Vt3X5zqMnaxJ9we8VOJJbEj39xz4rvT4jkoupK9NOSxvyhcZu1hIAwQum42KhwKuniNnC0/a8SiFHap5qOGWvJt8f/o78ZXzMNd8us+NVCcVgVIAblDX4gzHuThiMg0nnNwfmASsRwjE+5v3G0nS7eHMaITiNRDZVHqOkd3mBaFpbpWIbY6VbzxBGq41FU/dQ81cJ0Sz0wjapBmn4h16XouKNs/EthIIgfCmnZ92x6rD3QPGknrJW/upua3BUTUMyEaIB3DUdBmO6n4S+BgYQyK+30hqRYe/vcSyWYtoXPBprhZInyA0zY1SWKHg2ytO67m28d9ztxL1s2RdO2AMsaWfY1b9MFclwgsA0NwFMrXNzWbxVwGzejWBK+buXot+CdkZ4Kzv3kepBCS35sVWo/8KyMHl9uxPotrdNBUr2MSqMQOEp0P3K6THNwhAJWIliZotz/Z5b6PyJJc5uyQrsWgWKeccjavH8EY7WPuwshJNApCelJON9kdOaP77YKHiUVIumPnLrdolBNQ16jgxdieG6/ahRnbYPlEOjuDcb6I4d/Q1NH46h7yxl9B77oaRWiDjKiGlwLZts7nxiVPOOmRFzZtPUXLflb9MFkDNhPOJff8Woffu/FBFm6YrZYPQdOHLuDHjnuJzcx8/E5RFykWz9r7Vzsk+EyGeQcoteD0QDUI83vprJ2A08AFKWdjW3teVRN4FN1D9yhS6zviI4PI1h+vZbSYLw5WJAiscfD+y5vunZ8/8D56OPYhVlOyQd5fzceo1CxDuFOxgTbbefsAL0ps2QggBVqzSaq64Mvu29m9XXT4b4fLS9NhJ7CnUyPaOg7edo5HJXeY84ByccE0h8AKo2c4hUw0xf9NeE1V0x3NE1i2l3a1TCP+wvK+7sMOz0uvssKtYZHW8suRsIyt/mZ6WwaK+P6Vml1skzdNOQO94KHq7frV2fcl1Kh5eBIDuztfSCqbVP7jl/Obpf5LCk0rWw3VoHQbsWetNCyyb1pBIa7U4kYWXcdZ9Q0D0dEjdO+mgZ+fT+fEPkb4AnadPIbxu7WBXYcfnpcd/GIBKxCsStRV/ST2qx7Lvj8lkw7Wn7rScXe8nJaI0/+MEht2YhV7YZ43dVDZeJcJLUCA0V75MyZ2a9VDlLXZTZQArhuZLIeWcR/butZtbhW4EZ/f6S+ApHClxKQjPdoTuNnLPuYYxNRVo6dnUvPqI1vx52TmuvHazpMfXFwEqEa9K1FZe3WdUp/fLHp7GQVc/SP2/39kLsoDE6o+YO/wm6iZI9Pxu31m1Gy9W8eB/FAo0I02m5Nzt6n7csypU27vwg49xdRxE9v3r2IOOFQCnIhEIgfigNHlQrfVIMl8COqi2SU22W0jpfzy9/tVIysATWLIE7FBLm/YPzXlAz8x9Srjc7QHseGxzorbi8rNHFL3xn/tmoadlUfboTT9b5m7tVEY/fAjvqZOovVygF/T83qpeM0ZFm99G2QopNeFNO1PL6Tyn5bXKq5QZS1HxMK7+f9rdfgWAESQ3UNWo5C6R1kq2sHAW1K7dKcx/6BD6b1BknfxnUIJEXYUnXlp2urfbYW/raVnXo7sCKLCj4cXxys1jcka2n/3sXc+Bstl467m7LHu3t3Ujc27BM3QChZcLZGreBrPsu0vsUN09mPF6EAjD21EGcv6h5Xa+0DuoDyrUsLtFlwEaSvVCKTCTyt5Wji9TqmOS0PKf7URKGgXXPEbakNPJu/ReYpVg1lV4zIaaP6QN+dNLWmb+TOnxH4GQYJlxK9j4Umzz2rPc7bouWD3uDkCy8Y4LfrGhe7QHHv10Kiu7H4v0ZyK9GY11N+TebdUXX4gZczoipCakliVTYeuQcXvInPgm6eOeI/P690i/5GkA0i+cinKkQAjn3MMEnLCwQYofnKVPL+B64H2gAbltaKcc4cy+2WPupGjyh6QOG8uwz95EWWZuvKL4zPQTx7xq5LV9S/pTzxBS86EUdiyyJlFXMaFl4T/Ha2lZG0f0A6G72LQb50lhb2PGXi+pY2aiF/ZE2WZPPbfrR0J3t8FONFr1xSOFN/VLW0aJvnU3RQueo/b+5dTe0UdmXDc/NbH2i1DWzfcl4qtasBu3UFS+iIVZY1CftTseZzjGkgT6ceLlHyLFBx+b6fbZBefgLuqBq6AjelYBng7ZrOgnZIcnv842sgv6SLd3mPR4hkmXp6fQNNfWo92WWW1HWl5JVBY/mTe239rKGZ9i5LWl+M7zCC1fuNvd3qe4x0GfKyJfld8gU/MfElKiYs0fxVa+d5pwpYSkS8NuqMB3/CXEV33ZRm/TdaJ0+/6AbW7GjH6rrOhyoi0b7KbymuCmxcHQrIuiyDYGA8lD130kzDALqXrsyXmJz8eP4E0hRMZpEw3vIcf5jLx2WZo/vUh6PL2k291Pur2HSsPoJKTmQ2P7SwPldiz0jtVc//ymG09Z3OmRefama04i64wrKHvkmj3u796RZXhJvfRlVCKa5u41Yq7wpA4R2Mpq2jLR3aPt1OxRUH7fSmpv7yVyH1o/TKbl3ik8/sFCyu2vo8TAbBC2WYVtVqLMKpRVZwualBQRoWxbF0qTUrkRMiB0LVNoeo7U9XxhGPlC07KkJn1bb1RsLdeKKDO2UoVb3rWaamZvuuqYlV1fXWOHlizAVdCBDROO3xr5/K+QlTphPkZRP1S48Vgtu8M76EYAM1pqlS8/ASFXGx0OI7FpYa6e2+kq6Uu7QuiuTCQIbBuUElJqO7/opHb83HpDLHnpSSRvi+1wKQobgd2Iba5TsdAXdrjpX2Z16TcdrxpaW/bmBoKLPsLdrhvFd4/GqqncK5Jaoe9NpsSmrzjv8ZN4/RF9FFIPoBQkop/VPzJoLRGT7EnrjtULet0p3b6hztUUBYnYBjvS8Ci2WSdcnkOFy32w0I0iNC0HKVKElG6k0JzX13pUa7vzbbayFSqObYfBqlO2WYoZW40ZW6rM2FKrqWbtaeMHNrz/TxdmXQUL+wwmZfDJVD57FyoR3SeS9tqytKJDCZz3DFhmgV7Y+yPh8h6MsuJ2c8U51uZFnxidjpwo/JlXCd2Vs/VqSqx5jt245d7AyF7Lokstcs7XKBk72e3qMihd86flCMOdK3Q9W2haBpoMCE1zC4EmJDbKjgtlBXGspw4rXq2iwerEltUNZZPGhnt/DYnyCszaMho/eJHUY05n8y2nYkd+B5czc19UqAhY9ZvP1tIKX0LTDBUPrbAbN9+tZRw0Trr9w9CSc7wV26zC9ZMT6794wdX9mGDjU2PxDjyLyNev4T/yDPTCHmjpuWj+NKQ/DZnqQc8AmQKaF0iAHQXVAnaLiR1qREWasFvqSFSsJ7z4Ixr/+QKuwq7Et6zd7+TsM1kp58/A3LxI9586eab0pp+LABLhcjRNCt2d79xrtiwVC86zW6ruSb+46+KGRxegt+1D/eRhJDZ+u+sKXB5w+zB0F5adwI5HIBpN7vn+ttgjn+UadDFGl6EYHY7sIgzvEMBxKYavoHUpqBKxLSpc90ii5JtnXF2HNpePvQajfX/q7j9m9yqJRyEe3X+Rvv2IPVLwVtVaPAO6InxZw5FGIbDNNpVtq2jzfKuu5PT8vxQ8Iv3ZzcLwYlb8QNOMMb91P/cL9mgY+kfPQMVavN5Bl8yW3vThWyctM1apwnWPxTd8PqPH9LPrFwoh2ryqVMW5v69/G7Gv2KNhKDQd3H4NhNuZ2W1lhxo+M0sXT2p6dPhXgLXwqdEGYP5/IwqcUzK7D8PD0f98MF42Z2kDqIBdV/xWaM4tD4Rm37IBZ0jHQSX49bbaf1PszesXQBZSz8Y2TZxjPS04h2d/j355v2FvFLwCEthm64mXRhyifvu5/XcKgXMeYbeilwdwAAdwAAdwAL9r/C/e3US8+Q+buQAAAABJRU5ErkJggg==",
                                        timeout: 4500,
                                        callback: function() { this.hide() }
                                    });
                                }
                            }
                            xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                            // console.log(\$('#editor').val());
                            flattenSsml();
                            console.log('editor: ', editor.getValue());
                            xmlhttp.send(JSON.stringify({
                                val: editor.getValue(),
                                name: inName,
                                type: 'text'
                            }));
                            editor.markClean();
                            \$('#submitBtn').removeClass('btn-success').addClass('btn-outline-info');
                        });

                        function insertSsml(editor, str, sTags = true) {
                            let doc = editor.getDoc();
                            let cursor = doc.getCursor();
                            let line = cursor.line;
                            // console.log(`lineTxt (\${editor.getLine(line).length}): `, editor.getLine(line))
                            if (editor.getSelection().length > 0) {
                                editor.replaceSelection(cleanEditorText(` \${str}`));
                            } else {
                                doc.replaceRange(cleanEditorText(` \${str}`), {
                                    line: line,
                                    ch: cursor.ch
                                });
                            }
                            if (sTags) {
                                doc.replaceRange(editor.getLine(line).replace('<speak>', '').replace('</speak>', ''), {
                                    line: line,
                                    ch: 0
                                }, {
                                    line: line,
                                    ch: editor.getLine(line).length
                                });
                                doc.replaceRange('<speak>', {
                                    line: line,
                                    ch: 0
                                });
                                doc.replaceRange('</speak>', {
                                    line: line,
                                    ch: editor.getLine(line).length
                                });
                            }
                        }

                        function flattenSsml() {
                            let doc = editor.getDoc();
                            let mlInd = 0
                            let mlItems = [];
                            let ln = 0
                            doc.eachLine(line => {
                                if (line.text.trim().startsWith('<speak>') && !line.text.trim().endsWith('</speak>')) {
                                    mlItems[mlInd] = {
                                        s: ln,
                                        str: line.text.replace(';', '').trim()
                                    };
                                } else if (!line.text.trim().startsWith('<speak>') && line.text.trim().endsWith('</speak>')) {
                                    if (mlItems[mlInd] !== undefined && mlItems[mlInd].s !== undefined) {
                                        mlItems[mlInd].e = ln;
                                        mlItems[mlInd].str += line.text.replace(';', '').trim();
                                        mlItems[mlInd].el = line.text.length;
                                        mlInd++;
                                    }
                                } else {
                                    if (mlItems[mlInd] !== undefined) {
                                        mlItems[mlInd].str += line.text.replace(';', '').trim();
                                    }
                                }
                                ln++;
                            });
                            // console.log(mlItems);
                            if (mlItems.length) {
                                doc.replaceRange(mlItems[0].str, {
                                    line: mlItems[0].s,
                                    ch: 0
                                }, {
                                    line: mlItems[0].e,
                                    ch: mlItems[0].el
                                });
                                flattenSsml();
                            }
                            editor.setValue(cleanEditorText(editor.getValue()));
                        }

                        \$('.ssml-buttons input:not(.no-submit)').click(function() {
                            let ssml = \$(this).data('ssml');
                            let value = \$(this).val();
                            var selected = editor.getSelection();
                            var replace = (selected == '') ? 'REPLACE_THIS_TEXT' : selected;
                            switch (ssml) {
                                case 'break':
                                    insertSsml(editor, `<break time="\${value}"/>`);
                                    break;
                                case 'emphasis':
                                    insertSsml(editor, `<emphasis level="\${value}">\${replace}</emphasis>`);
                                    break;
                                case 'pitch':
                                case 'rate':
                                case 'volume':
                                    insertSsml(editor, `<prosody \${ssml}="\${value}">\${replace}</prosody>`);
                                    break;
                                case 'voice':
                                    insertSsml(editor, `<voice name="\${(\$('#voices').val() != '') ? \$('#voices').val() : 'Ivy'}">\${replace}</voice>`);
                                    break;
                                case 'audio':
                                    insertSsml(editor, `<audio src="\${(\$('#audio').val() != '') ? \$('#audio').val() : 'https://s3.amazonaws.com/ask-soundlibrary/transportation/amzn_sfx_car_accelerate_01.mp3'}"/>`);
                                    break;
                                case 'sub':
                                    insertSsml(editor, `<sub alias="\${(\$('#alias').val() != '') ? \$('#alias').val() : 'magnesium'}">\${replace}</sub>`);
                                    break;
                                case 'say-as':
                                    if (selected == '') {
                                        switch (value) {
                                            case 'number':
                                                replace = '1234';
                                                break;
                                            case 'characters':
                                                replace = 'TEST';
                                                break;
                                            case 'spell-out':
                                                replace = 'TEST';
                                                break;
                                            case 'cardinal':
                                                replace = '1234';
                                                break;
                                            case 'ordinal':
                                                replace = '5';
                                                break;
                                            case 'digits':
                                                replace = '1234';
                                                break;
                                            case 'fraction':
                                                replace = '1/5';
                                                break;
                                            case 'unit':
                                                replace = 'USD10';
                                                break;
                                            case 'date':
                                                replace = '01012019';
                                                break;
                                            case 'time':
                                                // replace = '1'21"';
                                                break;
                                            case 'telephone':
                                                replace = '(541) 754-3010';
                                                break;
                                            case 'address':
                                                replace = '711-2880 Nulla St., Mankato, Mississippi, 96522';
                                                break;
                                            case 'expletive':
                                                replace = 'bad word';
                                                break;
                                            case 'interjection':
                                                replace = 'boing';
                                                break;
                                            case 'speechcon':
                                                replace = 'boing';
                                                break;
                                            default:
                                                replace = 'REPLACE THIS TEXT';
                                        }
                                    }
                                    insertSsml(editor, `<say-as interpret-as="\${value}">\${replace}</say-as>`)
                                    break;
                                case 'whisper':
                                    insertSsml(editor, `<amazon:effect name="whispered">\${replace}</amazon:effect>`);
                                    break;
                                case 'evttype':
                                    insertSsml(editor, '%type%', false);
                                    break;
                                case 'evtvalue':
                                    insertSsml(editor, '%value%', false);
                                    break;
                                case 'evtname':
                                    insertSsml(editor, '%name%', false);
                                    break;
                                case 'evtdate':
                                    insertSsml(editor, '%date%', false);
                                    break;
                                case 'evtunit':
                                    insertSsml(editor, '%unit%', false);
                                    break;
                                case 'evttime':
                                    insertSsml(editor, '%time%', false);
                                    break;
                                case 'evtdatetime':
                                    insertSsml(editor, '%datetime%', false);
                                    break;
                                case 'evtduration':
                                    insertSsml(editor, '%duration%', false);
                                    break;
                                case 'evtdurationmin':
                                    insertSsml(editor, '%durationmin%', false);
                                    break;
                                case 'evtdurationval':
                                    insertSsml(editor, '%durationval%', false);
                                    break;
                                case 'evtdurationvalmin':
                                    insertSsml(editor, '%durationvalmin%', false);
                                    break;
                                default:
                                    break;
                            }
                            return false
                        });
                    });
                </script>
            </body>
        </html>
    """
/* """ */
    render contentType: "text/html", data: html
}

def textEditProcessing() {
    String actId = params?.cId
    // String inName = params?.inName
    // log.debug "POST | actId: $actId | inName: $inName"
    def resp = request?.JSON ?: null
    def actApp = getTextEditChild(actId)
    Boolean status = (actApp && actApp?.updateTxtEntry(resp))
    String json = new JsonOutput().toJson([message: (status ? "success" : "failed"), version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}

def getSettingVal(String inName) {
    String actId = params?.cId
    // log.debug "GetSettingVals | actId: $actId"
    def actApp = getTextEditChild(actId)
    def value = null
    if(actApp) { value = actApp?.getSettingInputVal(inName) }
    return value
}

String getTextEditorPath(String cId, String inName) {
    return getAppEndpointUrl("textEditor/${cId}/${inName}")
}

@Field static final List amazonDomainsFLD = ["amazon.com", "amazon.ca", "amazon.co.uk", "amazon.com.au", "amazon.de", "amazon.it", "amazon.com.br", "amazon.com.mx"]
@Field static final List localesFLD       = ["en-US", "en-CA", "de-DE", "en-GB", "it-IT", "en-AU", "pt-BR", "es-MX", "es-UY"]

private List amazonDomainOpts() { return (state.appData && state.appData?.amazonDomains?.size()) ? state.appData.amazonDomains : amazonDomainsFLD }
private List localeOpts() { return (state.appData && state.appData?.locales?.size()) ? state.appData.locales : localesFLD }

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
    else { return sUNKNOWN}
}
/*
Boolean isContactOpen(sensors) {
    if(sensors) { sensors.each { if(sensors?.currentValue("contact") == "open") { return true } } }
    return false
} */

Boolean isSwitchOn(devs) {
    if(devs instanceof List) { devs.each { if(it?.currentValue("switch") == "on") { return true } } }
    else if(devs) if(devs?.currentValue("switch") == "on") { return true }
    return false
}
/*
Boolean isSensorPresent(List sensors) {
    if(sensors) { sensors.each { if(it?.currentValue("presence") == "present") { return true } } }
    return false
} */

Boolean isSomebodyHome(List sensors) {
    if(sensors) { return (sensors.findAll { it?.currentValue("presence") == "present" }.size() > 0) }
    return false
}

Boolean isDayOfWeek(List opts) {
    SimpleDateFormat df = new SimpleDateFormat("EEEE")
    df?.setTimeZone((TimeZone)location?.timeZone)
    String day = df?.format(new Date())
    return opts?.contains(day)
}

Boolean isInMode(List modes) {
    return (location?.mode?.toString() in modes)
}

Boolean isInAlarmMode(List modes) {
    if(!modes) return false
    return (getAlarmSystemStatus() in modes)
}

static String getAlarmSystemName(Boolean abbr=false) {
    return (abbr ? "HSM" : "Hubitat Safety Monitor")
}

static List getAlarmModes() {
    return ["armedAway", "armingAway", "armedHome", "armingHome", "armedNight", "armingNight", "disarmed", "allDisarmed"]
}

String getAlarmSystemStatus() {
    return location?.hsmStatus ?: "disarmed"
}

// This is incomplete (and currently unused)
void setAlarmSystemMode(String mode) {
    switch(mode) {
        case "armAway":
        case "away":
            mode = "armAway"
            break
        case "armHome":
        case "stay":
            mode = "armHome"
            break
        case "armNight":
        case "night":
            mode = "armNight"
            break
        case "disarm":
        case "off":
            mode = "disarm"
            break
    }
    logInfo("Setting the ${getAlarmSystemName()} Mode to (${mode})...")
    sendLocationEvent(name: "hsmSetArm", value: mode.toString())
}

Integer stateSize() { String j = new JsonOutput().toJson((Map)state); return j.length() }
Integer stateSizePerc() { return (Integer) ((stateSize() / 100000)*100).toDouble().round(0) }

List logLevels() {
    List lItems = ["logInfo", "logWarn", "logDebug", "logError", "logTrace"]
    return settings?.findAll { it.key in lItems && it?.value == true }?.collect { it.key }
}

String getAppDebugDesc() {
    List ll = logLevels()
    String str = sBLANK
    str += ll?.size() ? "App Log Levels: (${ll?.join(", ")})" : sBLANK
    return (str != sBLANK) ? str : sNULL
}

void addToLogHistory(String logKey, String msg, Integer max=10) {
    Boolean ssOk = true // (stateSizePerc() <= 70)
    String appId=app.getId()

    Boolean aa = getTheLock(sHMLF, "addToHistory(${logKey})")
    // log.trace "lock wait: ${aa}"

    Map<String,List> memStore = historyMapFLD[appId] ?: [:]
    List<Map> eData = (List)memStore[logKey] ?: []
    if(eData.find { it?.message == msg }) {
        releaseTheLock(sHMLF)
        return
    }
    eData.push([dt: getDtNow(), gt: now(), message: msg])
    Integer lsiz=eData.size()
    if(!ssOk || lsiz > max) { eData = eData.drop( (lsiz-max) ) }
    updMemStoreItem(logKey, eData)

    releaseTheLock(sHMLF)
}

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo) { log.info sSPACE + logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn) { log.warn sSPACE + logPrefix(msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, 15) } }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError) {
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
    return span("EchoApp (v" + appVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

void clearDiagLogs(String type="all") {
    // log.debug "clearDiagLogs($type)"
    if(type=="all") {
        clearHistory()
        getActionApps()?.each { ca-> ca?.clearLogHistory() }
        ((List)getChildDevices())?.each { cd-> cd?.clearLogHistory() }
        getZoneApps()?.each { ca -> ca?.relayClearLogHistory() }
    }
}

Map getLogHistory() {
    Boolean aa = getTheLock(sHMLF, "getLogHistory")
    // log.trace "lock wait: ${aa}"

    List warn = getMemStoreItem("warnHistory")
    List errs = getMemStoreItem("errorHistory")

    releaseTheLock(sHMLF)

    return [ warnings: []+warn, errors: []+errs ]
}

private void clearLogHistory() {
    String appId = app.getId()

    Boolean aa = getTheLock(sHMLF, "clearLogHistory")
    // log.trace "lock wait: ${aa}"

    Map memStore = historyMapFLD[appId] ?: [:]
    memStore["warnHistory"] = []
    memStore["errorHistory"] = []
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD

    releaseTheLock(sHMLF)
}

private void clearHistory() {
    String appId = app.getId()
    Boolean aa = getTheLock(sHMLF, "clearHistory")
    // log.trace "lock wait: ${aa}"

    historyMapFLD[appId] = [:]
    historyMapFLD = historyMapFLD

    releaseTheLock(sHMLF)
}

// FIELD VARIABLE FUNCTIONS
private void updMemStoreItem(String key, List val) {
    String appId = app.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    memStore[key] = val
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD
    // log.debug("updMemStoreItem(${key}): ${memStore[key]}")
}

private List<Map> getMemStoreItem(String key){
    String appId = app.getId()
    Map<String, List> memStore = historyMapFLD[appId] ?: [:]
    return (List)memStore[key] ?: []
}

// Memory Barrier
@Field static Semaphore theMBLockFLD=new Semaphore(0)

static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}

@Field static final String sHMLF = 'theHistMapLockFLD'
@Field static Semaphore histMapLockFLD = new Semaphore(1)

private Integer getSemaNum(String name) {
    if(name == sHMLF) return 0
    log.warn "unrecognized lock name..."
    return 0
    // Integer stripes=22
    // if(name.isNumber()) return name.toInteger()%stripes
    // Integer hash=smear(name.hashCode())
    // return Math.abs(hash)%stripes
    // log.info "sema $name # $sema"
}

Semaphore getSema(Integer snum) {
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
    Semaphore sema = getSema(semaNum)
    while(!((Boolean)sema.tryAcquire())) {
        // did not get the lock
        Long timeL = lockTimesFLD[semaSNum]
        if(timeL == null){
            timeL = now()
            lockTimesFLD[semaSNum] = timeL
            lockTimesFLD = lockTimesFLD
        }
        //if(devModeFLD) log.warn "waiting for ${qname} ${semaSNum} lock access, $meth, long: $longWait, holder: ${(String)lockHolderFLD[semaSNum]}"
        pauseExecution(waitT)
        wait = true
        if((now() - timeL) > 30000L) {
            releaseTheLock(qname)
            if(devModeFLD) log.warn "overriding lock $meth"
        }
    }
    lockTimesFLD[semaSNum] = (Long)now()
    lockTimesFLD = lockTimesFLD
    lockHolderFLD[semaSNum] = "${app.getId()} ${meth}".toString()
    lockHolderFLD = lockHolderFLD
    return wait
}

void releaseTheLock(String qname){
    Integer semaNum=getSemaNum(qname)
    String semaSNum=semaNum.toString()
    Semaphore sema=getSema(semaNum)
    lockTimesFLD[semaSNum]=null
    lockTimesFLD=lockTimesFLD
    lockHolderFLD[semaSNum]=sNULL
    lockHolderFLD=lockHolderFLD
    sema.release()
}

public static Map getAppDuplTypes() { return appDuplicationTypesMapFLD }

@Field static final Map appDuplicationTypesMapFLD = [
    stat: [
        bool: ["notif_pushover", "notif_alexa_mobile", "logInfo", "logWarn", "logError", "logDebug", "logTrace", "enableWebCoRE"],
        enum: ["triggerEvents", "act_EchoZones", "actionType", "cond_alarmSystemStatus", "cond_months", "trig_alarmSystemStatus", "trig_guard"],
        mode: ["cond_mode", "trig_mode"],
        number: [],
        text: ["appLbl"]
    ],
    ends: [
        bool: ["_all", "_avg", "_once", "_send_push", "_use_custom", "_stop_on_clear", "_db", "Pause", "_vol_per_zone", "_ign_empty_type"],
        enum: ["_cmd", "_type", "_routineExecuted",
               "_EchoDevices",
               "_scheduled_sunState", "_scheduled_recurrence", "_scheduled_days", "_scheduled_weeks", "_scheduled_weekdays", "_scheduled_months", "_scheduled_daynums", "_scheduled_type",
               "_routine_run", "_mode_run", "_piston_run", "_alarm_run", "_rt", "_rt_wd", "_nums", "_Codes", "_pistonExecuted", "_days", "_months", "_alarmSystemStatus_events"],
        number: ["_wait", "_low", "_high", "_equal", "_delay", "_cnt", "_volume", "_offset", "_after", "_after_repeat", "_rt_ed", "_volume_change", "_volume_restore", "_leveln"],
        text: ["_txt", "_sms_numbers", "_label", "_date", "_message", "_colort"],
        mode: ["_modes"],
        time: ["_time_start", "_time_stop", "_time", "_scheduled_time"]
    ],
    caps: [
        _devs: "notification",
        _acceleration: "accelerationSensor",
        _battery: "battery",
        _carbonMonoxide: "carbonMonoxideDetector",
//        _color: "colorControl",
        _contact: "contactSensor",
        _door: "garageDoorControl",
        _temperature: "temperatureMeasurement",
        _illuminance: "illuminanceMeasurement",
        _humidity: "relativeHumidityMeasurement",
        _motion: "motionSensor",
        _level: "switchLevel",
//        _button: "button",
        _lock: "lock",
        _pushed: "pushableButton",
        _held: "holdableButton",
        _released: "releasableButton",
        _doubleTapped: "doubleTapableButton",
        _power: "powerMeter",
        _presence: "presenceSensor",
        _securityKeypad: "securityKeypad",
        _smoke: "smokeDetector",
        _switch: "switch",
        _thermostatOperatingState: "thermostat",
        _thermostatMode: "thermostat",
        _thermostatFanMode: "thermostat",
        _thermostatTemperature: "thermostat",
        _thermostatHeatingSetpoint: "thermostat",
        _thermostatCoolingSetpoint: "thermostat",
        _windowShade: "windowShades",
        _water: "waterSensor",
        _valve: "valve",
// these are for action commands inputs
        _doors_open: "garageDoorControl",
        _doors_close: "garageDoorControl",
//        _lock: "lock",   ALREADY COVERED ABOVE
        _unlock: "lock",
        _disarm: "securityKeypad",
        _armHome: "securityKeypad",
        _armAway: "securityKeypad",
        _sirens: "alarm",
        _switches_off: "switch",
        _switches_on: "switch",
        _lights: "switch",
        _EchoDeviceList: ""
    ],
    dev: [
        _scene: "sceneActivator",
        // _EchoDevices: "EchoSpeaksDevice",
        // _EchoDeviceList: "EchoSpeaksDevice"
    ]
]

@Field static final Map deviceSupportMapFLD = [
    types: [
        //  c: "a" == announce, "t" == TTS
        // Amazon Devices
        "A3C9PE6TNYLTCH" : [ i: "echo_wha", n: "Multiroom" ],

        // Amazon FireTV's
        "A12GXV8XMS007S" : [ c: [ "a", "t" ], i: "firetv_gen1", n: "Fire TV (Gen1)" ],
        "A2E0SNTXJVT7WK" : [ c: [ "a", "t" ], i: "firetv_gen2", n: "Fire TV (Gen2)" ],
        "A2GFL5ZMWNE0PX" : [ c: [ "a", "t" ], i: "firetv_gen3", n: "Fire TV (Gen3)" ],
        "AKPGW064GI9HE"  : [ c: [ "a", "t" ], i: "firetv_stick_gen1", n: "Fire TV Stick 4K (Gen3)" ],
        "AN630UQPG2CA4"  : [ c: [ "a", "t" ], i: "firetv_toshiba", n: "Fire TV (Toshiba)" ],
        "A2JKHJ0PX4J3L3" : [ c: [ "a", "t" ], i: "firetv_cube", n: "Fire TV Cube (Gen2)" ],
        "A265XOI9586NML" : [ c: [ "a", "t" ], i: "firetv_stick_gen1", n: "Fire TV Stick" ],
        "A2LWARUGJLBYEW" : [ c: [ "a", "t" ], i: "firetv_stick_gen1", n: "Fire TV Stick (Gen2)" ],
        "A1F8D55J0FWDTN" : [ c: [ "a", "t" ], i: "toshiba_firetv", n: "Fire TV (Toshiba)" ],
        "AP4RS91ZQ0OOI"  : [ c: [ "a", "t" ], i: "toshiba_firetv", n: "Fire TV (Toshiba)" ],
        "AFF5OAL5E3DIU"  : [ c: [ "a", "t" ], i: "toshiba_firetv", n: "Fire TV" ],
        "A3HF4YRA2L7XGC" : [ c: [ "a", "t" ], i: "firetv_cube", n: "Fire TV Cube" ],
        "AFF50AL5E3DIU"  : [ c: [ "a", "t" ], i: "insignia_firetv",  n: "Fire TV (Insignia)" ],
        "ADVBD696BHNV5"  : [ c: [ "a", "t" ], i: "firetv_stick_gen1", n: "Fire TV Stick (Gen1)" ],
        "A1P7E7V3FCZKU6" : [ c: [ "a", "t" ], i: "firetv_gen3", n: "Fire TV (Gen3)" ],
        
        // Amazon Tablets
        "A1Q7QCGNMXAKYW" : [ c: [ "t", "a" ], i: "amazon_tablet", n: "Generic Tablet" ],
        "A1J16TEDOYCZTN" : [ c: [ "a", "t" ], i: "amazon_tablet", n: "Fire Tablet" ],
        "A2M4YX06LWP8WI" : [ c: [ "a", "t" ], i: "amazon_tablet", n: "Fire Tablet" ],
        "A1C66CX2XD756O" : [ c: [ "a", "t" ], i: "amazon_tablet", n: "Fire Tablet HD" ],
        "A3L0T0VL9A921N" : [ c: [ "a", "t" ], i: "tablet_hd10", n: "Fire Tablet HD 8" ],
        "AVU7CPPF2ZRAS"  : [ c: [ "a", "t" ], i: "tablet_hd10", n: "Fire Tablet HD 8" ],
        "A38EHHIB10L47V" : [ c: [ "a", "t" ], i: "tablet_hd10", n: "Fire Tablet HD 8" ],
        "A3R9S4ZZECZ6YL" : [ c: [ "a", "t" ], i: "tablet_hd10", n: "Fire Tablet HD 10" ],

        // Amazon Echo's
        "AB72C64C86AW2"  : [ c: [ "a", "t" ], i: "echo_gen1", n: "Echo (Gen1)" ],
        "A7WXQPH584YP"   : [ c: [ "a", "t" ], i: "echo_gen2", n: "Echo (Gen2)" ],
        "A3FX4UWTP28V1P" : [ c: [ "a", "t" ], i: "echo_gen3", n: "Echo (Gen3)" ],
        "A3RMGO6LYLH7YN" : [ c: [ "a", "t" ], i: "echo_gen4", n: "Echo (Gen4)" ],
        "A2M35JJZWCQOMZ" : [ c: [ "a", "t" ], i: "echo_plus_gen1", n: "Echo Plus (Gen1)" ],
        "A18O6U1UQFJ0XK" : [ c: [ "a", "t" ], i: "echo_plus_gen2", n: "Echo Plus (Gen2)" ],

        // Amazon Echo Dots
        "AKNO1N0KSFN8L"  : [ c: [ "a", "t" ], i: "echo_dot_gen1", n: "Echo Dot (Gen1)" ],
        "A3S5BH2HU6VAYF" : [ c: [ "a", "t" ], i: "echo_dot_gen2", n: "Echo Dot (Gen2)" ],
        "A32DDESGESSHZA" : [ c: [ "a", "t" ], i: "echo_dot_gen3",  n: "Echo Dot (Gen3)" ],
        "A32DOYMUN6DTXA" : [ c: [ "a", "t" ], i: "echo_dot_gen3",  n: "Echo Dot (Gen3)" ],
        "A1RABVCI4QCIKC" : [ c: [ "a", "t" ], i: "echo_dot_gen3", n: "Echo Dot (Gen3)" ],
        "A2U21SRK4QGSE1" : [ c: [ "a", "t" ], i: "echo_dot_gen4",  n: "Echo Dot (Gen4)" ],
        "A30YDR2MK8HMRV" : [ c: [ "a", "t" ], i: "echo_dot_clock", n: "Echo Dot Clock" ],
        "A2H4LV5GIZ1JFT" : [ c: [ "a", "t" ], i: "echo_dot_clock_gen4",  n: "Echo Dot Clock (Gen4)" ],
        
        // Amazon Echo Spot's
        "A10A33FOX2NUBK" : [ c: [ "a", "t" ], i: "echo_spot_gen1", n: "Echo Spot" ],

        // Amazon Echo Show's
        "A1NL4BVLQ4L3N3" : [ c: [ "a", "t" ], i: "echo_show_gen1", n: "Echo Show (Gen1)" ],
        "AWZZ5CVHX2CD"   : [ c: [ "a", "t" ], i: "echo_show_gen2", n: "Echo Show (Gen2)" ],
        "A4ZP7ZC4PI6TO"  : [ c: [ "a", "t" ], i: "echo_show_5", n: "Echo Show 5 (Gen1)" ],
        "A1XWJRHALS1REP" : [ c: [ "a", "t" ], i: "echo_show_5", n: "Echo Show 5 (Gen2)" ],
        "A1Z88NGR2BK6A2" : [ c: [ "a", "t" ], i: "echo_show_8", n: "Echo Show 8 (Gen1)" ],
        "A15996VY63BQ2D" : [ c: [ "a", "t" ], i: "echo_show_8", n: "Echo Show 8 (Gen2)" ],
        "AIPK7MM90V7TB"  : [ c: [ "a", "t" ], i: "echo_show_10_gen3", n: "Echo Show 10 (Gen3)" ],
        
        // Amazon Echo Auto's
        "A303PJF6ISQ7IC" : [ c: [ "a", "t" ], i: "echo_auto", n: "Echo Auto" ],
        "A195TXHV1M5D4A" : [ c: [ "a", "t" ], i: "echo_auto", n: "Echo Auto" ],
        "ALT9P69K6LORD"  : [ c: [ "a", "t" ], i: "echo_auto", n: "Echo Auto" ],

        // Other Amazon Devices
        "A38949IHXHRQ5P" : [ c: [ "a", "t" ], i: "echo_tap", n: "Echo Tap" ],
        "A1JJ0KFC4ZPNJ3" : [ c: [ "a", "t" ], i: "echo_input", n: "Echo Input" ],
        "A3IYPH06PH1HRA" : [ c: [ "a", "t" ], i: "echo_frames", n: "Echo Frames" ],
        "A3RBAYBE7VM004" : [ c: [ "a", "t" ], i: "echo_studio", n: "Echo Studio" ],
        "A2RU4B77X9R9NZ" : [ c: [ "a", "t" ], i: "echo_link_amp", n: "Echo Link Amp" ],
        "A3VRME03NAXFUB" : [ c: [ "a", "t" ], i: "echo_flex", n: "Echo Flex" ],
        "A3SSG6GR8UU7SN" : [ c: [ "a", "t" ], i: "echo_sub_gen1", n: "Echo Sub" ],
        "A27VEYGQBW3YR5" : [ c: [ "a", "t" ], i: "echo_link", n: "Echo Link" ],
        
        // Ignored Devices
        "A112LJ20W14H95" : [ ig: true ],
        "A1GC6GEE1XF1G9" : [ ig: true ],
        "A1MPSLFC7L5AFK" : [ ig: true ],
        "A1ORT4KZ23OY88" : [ ig: true ],
        "A1VS6XVTGTLC00" : [ ig: true ],
        "A1VZJGJYCRI78V" : [ ig: true ],
        "A1ZB65LA390I4K" : [ ig: true ],
        "A21X6I4DKINIZU" : [ ig: true ],
        "A21Z3CGI8UIP0F" : [ ig: true ],
        "A2825NDLA7WDZV" : [ ig: true ],
        "A29L394LN0I8HN" : [ ig: true ],
        "A2IVLV5VM2W81"  : [ ig: true ],
        "A2T0P32DY3F7VB" : [ ig: true ],
        "A2TF17PFR55MTB" : [ ig: true ],
        "A2TOXM6L8SFS8A" : [ ig: true ],
        "A2V3E2XUH5Z7M8" : [ ig: true ],
        "A1FWRGKHME4LXH" : [ ig: true ],
        "A26TRKU1GG059T" : [ ig: true ],
        "AU4IFDJDRSBC1"  : [ ig: true ],
        "A2ZOTUOF1IBEYI" : [ ig: true ],
        "ABP0V5EHO8A4U"  : [ ig: true ],
        "AD2YUJTRVBNOF"  : [ ig: true ],
        "ADQRVG6LYK4LQ"  : [ ig: true ],
        "A1GPVMRI4IOS0M" : [ ig: true ],
        "A2Z8O30CD35N8F" : [ ig: true ],
        "A1XN1MKELB7WUF" : [ ig: true ],
        "AINRG27IL8AS0"  : [ ig: true ],
        "A3NVKTZUPX1J3X" : [ ig: true, n: "Onkyp VC30" ],
        "A3NWHXTQ4EBCZS" : [ ig: true ],
        "A2RG3FY1YV97SS" : [ ig: true ],
        "A3H674413M2EKB" : [ ig: true ],
        "AGZWSPR7FLP9E"  : [ ig: true ],
        "AILBSA2LNTOYL"  : [ ig: true ],
        "A2RJLFEH0UEKI9" : [ ig: true ],
        "AKOAGQTKAS9YB"  : [ ig: true ],
        "ATH4K2BAIXVHQ"  : [ ig: true ],
        "A324YMIUSWQDGE" : [ ig: true, i: "unknown", n: "Samsung 8K TV" ],
        "A18X8OBWBCSLD8" : [ ig: true, i: "unknown", n: "Samsung Soundbar" ],
        "A1GA2W150VBSDI" : [ ig: true, i: "unknown", n: "Sony On-Hear Headphones" ],
        "A2S24G29BFP88"  : [ ig: true, i: "unknown", n: "Ford/Lincoln Alexa App" ],
        "A1NAFO69AAQ16Bk": [ ig: true, i: "unknown", n: "Wyze Band" ],
        "A1NAFO69AAQ16B" : [ ig: true, i: "unknown", n: "Wyze Band" ],
        "A3L2K717GERE73" : [ ig: true, i: "unknown", n: "Voice in a Can (iOS)" ],
        "A222D4HGE48EOR" : [ ig: true, i: "unknown", n: "Voice in a Can (Apple Watch)" ],
        "A19JK51Y4N50K5" : [ ig: true, i: "unknown", n: "Jabra(?)" ],

        // Unknown or Needs Image
        "A16MZVIFVHX6P6" : [ c: [ "a", "t" ], i: "unknown", n: "Generic Echo" ],
        "A2XPGY5LRKB9BE" : [ c: [ "a", "t" ], i: "unknown", n: "Fitbit Versa 2" ],
        "A2Y04QPFCANLPQ" : [ c: [ "a", "t" ], i: "unknown", n: "Bose QuietComfort 35 II" ],
        "A3B50IC5QPZPWP" : [ c: [ "a", "t" ], i: "unknown", n: "Polk Command Bar" ],
        "A3CY98NH016S5F" : [ c: [ "a", "t" ], i: "unknown", n: "Facebook Portal Mini" ],
        "AO6HHP9UE6EOF"  : [ c: [ "a", "t" ], i: "unknown", n: "Unknown Media Device" ],
        "A3KULB3NQN7Z1F" : [ c: [ "a", "t" ], i: "unknown", n: "Unknown TV" ],
        "A18TCD9FP10WJ9" : [ c: [ "a", "t" ], i: "unknown", n: "Orbi Voice" ],
        "AGHZIK8D6X7QR"  : [ c: [ "a", "t" ], i: "unknown", n: "Fire TV" ],
        "A1L4KDRIILU6N9" : [ c: [ "a", "t" ], i: "unknown", n: "Sony Speaker" ],
        "A1F1F76XIW4DHQ" : [ c: [ "a", "t" ], i: "unknown", n: "Unknown TV" ],
        "AE7X7Z227NFNS"  : [ c: [ "a", "t" ], i: "unknown", n: "HiMirror Mini" ],
        "AF473ZSOIRKFJ"  : [ c: [ "a", "t" ], i: "unknown", n: "Onkyo VC-PX30" ],
        "A2E5N6DMWCW8MZ" : [ c: [ "a", "t" ], i: "unknown", n: "Brilliant Smart Switch" ],
        "A2C8J6UHV0KFCV" : [ c: [ "a", "t" ], i: "unknown", n: "Alexa Gear" ],
        "AUPUQSVCVHXP0"  : [ c: [ "a", "t" ], i: "unknown", n: "Ecobee Switch+" ],
        "A37M7RU8Z6ZFB"  : [ c: [ "a", "t" ], i: "unknown", n: "Garmin Speak" ],
        "A2WN1FJ2HG09UN" : [ c: [ "a", "t" ], i: "unknown", n: "Ultimate Alexa App" ],
        "A2BRQDVMSZD13S" : [ c: [ "a", "t" ], i: "unknown", n: "SURE Universal Remote" ],
        "A3TCJ8RTT3NVI7" : [ c: [ "a", "t" ], i: "unknown", n: "Alexa Listens" ],
        "A2VAXZ7UNGY4ZH" : [ c: [ "a", "t" ], i: "unknown", n: "Wyze Headphones"],
        "AKO51L5QAQKL2"  : [ c: [ "a", "t" ], i: "unknown", n: "Alexa Jams" ],
        "A3K69RS3EIMXPI" : [ c: [ "a", "t" ], i: "unknown", n: "Hisense Smart TV" ],
        "A1QKZ9D0IJY332" : [ c: [ "a", "t" ], i: "unknown", n: "Samsung TV 2020-U" ],
        "A1SCI5MODUBAT1" : [ c: [ "a", "t"], i: "unknown", n: "Pioneer DMH-W466NEX" ],
        "A1ETW4IXK2PYBP" : [ c: [ "a", "t"], i: "unknown", n: "Talk to Alexa" ],
        "A81PNL0A63P93"  : [ c: [ "a", "t" ], i: "unknown", n: "Home Remote" ],
        
        
        // Blocked Devices
        "A1DL2DVDQVK3Q"  : [ b: true, ig: true, n: "Mobile App" ],
        "A1H0CMF1XM0ZP4" : [ b: true, n: "Bose SoundTouch 30" ],
        "A1X7HJX9QL16M5" : [ b: true, ig: true, n: "Bespoken.io" ],
        "A3SSWQ04XYPXBH" : [ b: true, i: "amazon_tablet", n: "Generic Tablet" ],
        "AKKLQD9FZWWQS"  : [ b: true, c: [ "a", "t" ], i: "unknown", n: "Jabra Elite" ],
        "AP1F6KUH00XPV"  : [ b: true, n: "Stereo/Subwoofer Pair" ],
        "A2WFDCBDEXOXR8" : [ b: true, i: "unknown", n: "Bose Soundbar 700" ],
        "AVN2TMX8MU2YM"  : [ b: true, n: "Bose Home Speaker 500" ],
        "A3F1S88NTZZXS9" : [ b: true, i: "dash_wand", n: "Dash Wand" ],
        "A25EC4GIHFOCSG" : [ b: true, n: "Unrecognized Media Player" ],

        // Other Devices
        "A1W2YILXTG9HA7" : [ c: [ "t", "a" ], i: "unknown", n: "Nextbase 522GW Dashcam" ],
        "A3NPD82ABCPIDP" : [ c: [ "t" ], i: "sonos_beam", n: "Sonos Beam" ],
        "AVD3HM0HOJAAL"  : [ c: [ "t", "a" ], i: "sonos_generic", n: "Sonos" ],
        "A14ZH95E6SE9Z1" : [ c: [ "t", "a" ], i: "unknown", n: "Bose Home Speaker 300" ], 
        "AVE5HX13UR5NO"  : [ c: [ "a", "t" ], i: "logitech_zero_touch", n: "Logitech Zero Touch" ],
        "A1M0A9L9HDBID3" : [ c: [ "t" ], i: "one-link", n: "One-Link Safe and Sound" ],
        "A1N9SW0I0LUX5Y" : [ c: [ "a", "t" ], i: "unknown", n: "Ford/Lincoln Alexa App" ],
        "A1P31Q3MOWSHOD" : [ c: [ "t", "a" ], i: "halo_speaker", n: "Zolo Halo Speaker" ],
        "A1RTAM01W29CUP" : [ c: [ "a", "t" ], i: "alexa_windows", n: "Windows App" ],
        "A2LH725P8DQR2A" : [ c: [ "a", "t" ], i: "fabriq_riff", n: "Fabriq Riff" ],
        "A15ERDAKK5HQQG" : [ i: "sonos_generic", n: "Sonos" ],
        "A10L5JEZTKKCZ8" : [ c: [ "a", "t" ], i: "vobot_bunny", n: "Vobot Bunny" ],
        "A17LGWINFBUTZZ" : [ c: [ "t", "a" ], i: "roav_viva", n: "Anker Roav Viva" ],
        "A18BI6KPKDOEI4" : [ c: [ "a", "t" ], i: "ecobee4", n: "Ecobee4" ],
        "A2R2GLZH1DFYQO" : [ c: [ "t", "a" ], i: "halo_speaker", n: "Zolo Halo Speaker" ],
        "A2J0R2SD7G9LPA" : [ c: [ "a", "t" ], i: "lenovo_smarttab_m10", n: "Lenovo SmartTab M10" ],
        "A2OSP3UA4VC85F" : [ i: "sonos_generic", n: "Sonos" ],
        "A2X8WT9JELC577" : [ c: [ "a", "t" ], i: "ecobee4", n: "Ecobee5" ],
        "A347G2JC8I4HC7" : [ c: [ "a", "t" ], i: "unknown", n: "Roav Car Charger Pro" ],        
        "A37CFAHI1O0CXT" : [ i: "logitech_blast", n: "Logitech Blast" ],
        "A37SHHQ3NUL7B5" : [ b: true, n: "Bose Home Speaker 500" ],
        "A38BPK7OW001EX" : [ b: true, n: "Raspberry Alexa" ],
        "A3B5K1G3EITBIF" : [ c: [ "a", "t" ], i: "facebook_portal", n: "Facebook Portal" ],
        "A3D4YURNTARP5K" : [ c: [ "a", "t" ], i: "facebook_portal", n: "Facebook Portal TV" ],
        "A3QPPX1R9W5RJV" : [ c: [ "a", "t" ], i: "fabriq_chorus", n: "Fabriq Chorus" ],
        "A3BW5ZVFHRCQPO" : [ c: [ "a", "t" ], i: "unknown", n: "BMW Alexa Integration" ],
        "A3BRT6REMPQWA8" : [ c: [ "a", "t" ], i: "sonos_generic", n: "Bose Home Speaker 450" ],
        "A2HZENIFNYTXZD" : [ c: [ "a", "t" ], i: "facebook_portal", n: "Facebook Portal" ],
        "A52ARKF0HM2T4"  : [ c: [ "a", "t" ], i: "facebook_portal", n: "Facebook Portal+" ],        
    ],
    families: [
        block: [ "AMAZONMOBILEMUSIC_ANDROID", "AMAZONMOBILEMUSIC_IOS", "TBIRD_IOS", "TBIRD_ANDROID", "VOX", "MSHOP" ],
        echo: [ "ROOK", "KNIGHT", "ECHO" ],
        other: [ "REAVER", "FIRE_TV", "FIRE_TV_CUBE", "ALEXA_AUTO", "MMSDK" ],
        tablet: [ "TABLET" ],
        wha: [ "WHA" ]
    ]
]
