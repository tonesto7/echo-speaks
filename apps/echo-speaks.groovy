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

import groovy.transform.Field
@Field static final String appVersionFLD  = "4.0.2.0"
@Field static final String appModifiedFLD = "2021-01-25"
@Field static final String branchFLD      = "master"
@Field static final String platformFLD    = "Hubitat"
@Field static final Boolean betaFLD       = true
@Field static final Boolean devModeFLD    = true
@Field static final Map minVersionsFLD    = [echoDevice: 4020, wsDevice: 4020, actionApp: 4020, zoneApp: 4020, server: 270]  //These values define the minimum versions of code this app will work with.

@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sBULLET        = '\u2022'
@Field static final String okSymFLD       = "\u2713"
@Field static final String notOkSymFLD    = "\u2715"
@Field static final String sFALSE         = 'false'
@Field static final String sTRUE          = 'true'
@Field static final String sBOOL          = 'bool'
@Field static final String sENUM          = 'enum'
@Field static final String sAPPJSON       = 'application/json'
@Field static final String sIN_IGNORE     = 'In Ignore Device Input'
@Field static final String sARM_AWAY      = 'ARMED_AWAY'
@Field static final String sARM_STAY      = 'ARMED_STAY'
@Field static final String sCOMPLT        = 'complete'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sTTM           = 'Tap to modify...'
@Field static final String sTTC           = 'Tap to configure...'
@Field static final String sTTP           = 'Tap to proceed...'
@Field static final String sTTS           = 'Tap to select...'
@Field static final String sSETTINGS      = 'settings'
@Field static final String sRESET         = 'reset'
@Field static final String sHEROKU        = 'heroku'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sAMAZONORNG    = 'amazon_orange'
@Field static final String sDEVICES       = 'devices'
@Field static final String sSWITCH        = 'switch'

// IN-MEMORY VARIABLES (Cleared only on HUB REBOOT or CODE UPDATES)
@Field volatile static Map<String, Map> historyMapFLD    = [:]
@Field volatile static Map<String, Map> cookieDataFLD    = [:]
@Field volatile static Map<String, Map> echoDeviceMapFLD = [:]
@Field volatile static Map<String, Map> childDupMapFLD   = [:]
//@Field static Map<String,          Map> guardDataFLD     = [:]
@Field volatile static Map<String, Map> zoneStatusMapFLD = [:]
@Field volatile static Map<String, Map> bluetoothDataFLD = [:]
@Field volatile static Map<String, Map> dndDataFLD       = [:]

definition(
    name        : "Echo Speaks",
    namespace   : "tonesto7",
    author      : "Anthony Santilli",
    description : "Integrate your Amazon Echo devices into your Smart Home environment to create virtual Echo Devices. This allows you to speak text, make announcements, control media playback including volume, and many other Alexa features.",
    category    : "My Apps",
    iconUrl     : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.1x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    iconX2Url   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.2x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    iconX3Url   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks_3.3x${(Boolean)state.updateAvailable ? "_update" : sBLANK}.png",
    importUrl   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/apps/echo-speaks.groovy",
    oauth       : true
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
//    Boolean resumeConf = ((Boolean)state.resumeConfig == true)
    //if((Boolean)state.refreshDeviceData) { getEchoDevices(true) }

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
            section(sTS("Alexa Guard:")) {
                if((Boolean)state.alexaGuardSupported) {
                    String gState = (String)state.alexaGuardState ? ((String)state.alexaGuardState == sARM_AWAY ? "Away" : "Home") : "Unknown"
                    String gStateIcon = gState == "Unknown" ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
                    href "alexaGuardPage", title: inTS1("Alexa Guard Control", gStateIcon), state: guardAutoConfigured() ? sCOMPLT : sNULL,
                            description: "Current Status: ${gState}${guardAutoConfigured() ? "\nAutomation: Enabled" : sBLANK}\n\n${sTTM}"
                } else { paragraph pTS("Alexa Guard is not enabled or supported by any of your Echo Devices", sNULL, false, sCLRGRY) }
            }

            section(sTS("Alexa Devices:")) {
                if(!newInstall) {
                    List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : sBLANK}${it?.value?.supported == false ? " \u2639" : sBLANK}" }
                    //Map skDevs = state.skippedDevices?.findAll { (it?.value?.reason != sIN_IGNORE) }
                    //Map ignDevs = state.skippedDevices?.findAll { (it?.value?.reason == sIN_IGNORE) }
                    List remDevs = getRemovableDevs()
                    if(remDevs?.size()) {
                        href "devCleanupPage", title: inTS("Removable Devices:"), description: "${remDevs?.sort()?.join("\n")}", required: true, state: sNULL
                    }
                    href "deviceManagePage", title: inTS1("Manage Devices:", sDEVICES), description: "(${devs?.size()}) Installed\n\n${sTTM}", state: sCOMPLT
                } else { paragraph "Device Management will be displayed after install is complete" }
            }

            section(sTS("Companion Apps:")) {
                List zones = getZoneApps()
                List acts = getActionApps()
                href "zonesPage", title: inTS1("Manage Zones${zones?.size() ? " (${zones?.size()} ${zones?.size() > 1 ? "Zones" : "Zone"})" : sBLANK}", "es_groups"), description: getZoneDesc(), state: (zones?.size() ? sCOMPLT : sNULL)
                href "actionsPage", title: inTS1("Manage Actions${acts?.size() ? " (${acts?.size()} ${acts?.size() > 1 ? "Actions" : "Action"})" : sBLANK}", "es_actions"), description: getActionsDesc(), state: (acts?.size() ? sCOMPLT : sNULL)
            }

            section(sTS("Alexa Login Service:")) {
                String ls = getLoginStatusDesc()
                href "authStatusPage", title: inTS1("Login Status | Cookie Service Management", sSETTINGS), description: (ls ? "${ls}\n\n${sTTM}" : sTTC), state: (ls ? sCOMPLT : sNULL)
            }
            if(!(Boolean)state.shownDevSharePage) { showDevSharePrefs() }
        }
        section(sTS("Notifications:")) {
            String t0 = getAppNotifConfDesc()
            href "notifPrefPage", title: inTS1("Manage Notifications", "notification2"), description: (t0 ? "${t0}\n\n${sTTM}" : sTTC), state: (t0 ? sCOMPLT : sNULL)
        }
        section(sTS("Documentation & Settings:")) {
            href url: documentationLink(), style: sEXTNRL, required: false, title: inTS1("View Documentation", "documentation"), description: sTTP
            href "settingsPage", title: inTS1("Manage Logging, and Metrics", sSETTINGS), description: "${sTTM}"
        }

//        if((Boolean)state.isInstalled) {
//        } else {
//            paragraph pTS("New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the Apps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", getHEAppImg("info"), false, sCLR4D9), state: sCOMPLT
//        }

        if(!newInstall) {
            section(sTS("Experimental Functions:")) {
                href "deviceTestPage", title: inTS1("Device Testing", "testing"), description: "Test Speech, Announcements, and Sequences Builder\n\n${sTTP}"
                href "musicSearchTestPage", title: inTS1("Music Search Tests", "music"), description: "Test music queries\n\n${sTTP}"
            }
            section(sTS("Donations:")) {
                href url: textDonateLink(), style: sEXTNRL, required: false, title: inTS1("Donations", "donate"), description: "Tap to open browser"
            }
            section(sTS("Remove Everything:")) {
                href "uninstallPage", title: inTS1("Uninstall this App", "uninstall"), description: "Tap to Remove..."
            }
            section(sTS("Feature Requests/Issue Reporting"), hideable: true, hidden: true) {
                def issueUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=bug&template=bug_report.md&title=%28BUG%29+&projects=echo-speaks%2F6"
                def featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?assignees=tonesto7&labels=enhancement&template=feature_request.md&title=%5BFeature+Request%5D&projects=echo-speaks%2F6"
                href url: featUrl, style: sEXTNRL, required: false, title: inTS1("New Feature Request", "www"), description: "Tap to open browser"
                href url: issueUrl, style: sEXTNRL, required: false, title: inTS1("Report an Issue", "www"), description: "Tap to open browser"
            }
        } else {
            showDevSharePrefs()
            section(sTS("Important Step:")) {
                paragraph title: "Notice:", pTS("Please complete the install (hit done below) and then return to the Echo Speaks App to resume deployment and configuration of the server.", sNULL, true, sCLRRED), required: true, state: sNULL
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
            section(sTS("Cookie Status:")) {
                Boolean cookieValid = validateCookie(true)
                Boolean chk1 = (state.cookieData && state.cookieData.localCookie)
                Boolean chk2 = (state.cookieData && state.cookieData.csrf  )
                Boolean chk3 = (lastChkSec < 432000)
                // Boolean chk4 = (cookieValid == true)
                // log.debug "cookieValid: ${cookieValid} | chk1: $chk1 | chk2: $chl2 | chk3: $chk3 | chk4: $chk4"
                String stat = "Auth Status: (${(chk1 && chk2 && cookieValid) ? "OK": "Invalid"})"
                stat += "\n ${sBULLET} Cookie: (${chk1 ? okSymFLD : notOkSymFLD})"
                stat += "\n \u2022 CSRF Value: (${chk2 ? okSymFLD : notOkSymFLD})"
                paragraph pTS(stat, sNULL, false, (chk1 && chk2) ? sCLR4D9 : sCLRRED), state: ((chk1 && chk2) ? sCOMPLT : sNULL), required: true
                paragraph pTS("Last Refresh: (${chk3 ? "OK" : "Issue"})\n(${seconds2Duration(getLastTsValSecs("lastCookieRrshDt"))})", sNULL, false, chk3 ? sCLR4D9 : sCLRRED), state: (chk3 ? sCOMPLT : sNULL), required: true
                paragraph pTS("Next Refresh:\n(${nextCookieRefreshDur()})", sNULL, false, sCLR4D9), state: sCOMPLT, required: true
            }

            section(sTS("Cookie Tools: (Tap to show)", getHEAppImg("cookie")), hideable: true, hidden: true) {
                String ckDesc = pastDayChkOk ? "This will Refresh your Amazon Cookie." : "It's too soon to refresh your cookie.\nMinimum wait is 24 hours!!"
                input "refreshCookieDays", "number", title: inTS1("Auto refresh cookie every?\n(in days)", "day_calendar"), description: "in Days (1-5 max)", required: true, range: '1..5', defaultValue: 5, submitOnChange: true
                if(refreshCookieDays != null && refreshCookieDays < 1) { settingUpdate("refreshCookieDays", 1, "number") }
                if(refreshCookieDays != null && refreshCookieDays > 5) { settingUpdate("refreshCookieDays", 5, "number") }
                // Refreshes the cookie
                input "refreshCookie", sBOOL, title: inTS1("Manually refresh cookie?", sRESET), description: ckDesc, required: true, defaultValue: false, submitOnChange: true, state: (pastDayChkOk ? sBLANK : sNULL)
                paragraph pTS(ckDesc, sNULL, false, pastDayChkOk ? sNULL : sCLRRED)
                paragraph pTS("Notice:\nAfter manually refreshing the cookie leave this page and come back before the date will change.", sNULL, false, sCLR4D9), state: sCOMPLT
                // Clears cookies for app and devices
                input "resetCookies", sBOOL, title: inTS1("Remove All Cookie Data?", sRESET), description: "Clear all stored cookie data from the app and devices.", required: false, defaultValue: false, submitOnChange: true
                paragraph pTS("Clear all stored cookie data from the app and devices.", sNULL, false, sCLRGRY)
                input "refreshDevCookies", sBOOL, title: inTS1("Resend Cookies to Devices?", sRESET), description: "Force devices to synchronize their stored cookies.", required: false, defaultValue: false, submitOnChange: true
                paragraph pTS("Force devices to synchronize their stored cookies.", sNULL, false, sCLRGRY)
                if((Boolean)settings.refreshCookie) { settingUpdate("refreshCookie", sFALSE, sBOOL); runIn(2, "runCookieRefresh") }
                if(settings.resetCookies) { clearCookieData("resetCookieToggle", false) }
                if((Boolean)settings.refreshDevCookies) { refreshDevCookies() }
            }
        }

        section(sTS("Cookie Service Management")) {
            String t0 = getServiceConfDesc()
            href "servPrefPage", title: inTS1("Manage Cookie Login Service", sSETTINGS), description: (t0 ? "${t0}\n\n${sTTM}" : sTTC), state: (t0 ? sCOMPLT : sNULL)
        }
    }
}

def servPrefPage() {
    Boolean newInstall = !(Boolean)state.isInstalled
    Boolean resumeConf = (Boolean)state.resumeConfig
    return dynamicPage(name: "servPrefPage", install: (newInstall || resumeConf), nextPage: (!(newInstall || resumeConf) ? "mainPage" : sBLANK), uninstall: !(Boolean)state.serviceConfigured) {
//        Boolean hasChild = (getChildDevices())?.size()
//        Boolean onHeroku = ((Boolean)settings.useHeroku != false)
        Boolean authValid = (Boolean)state.authValid

        if(settings.useHeroku == null) settingUpdate("useHeroku", sTRUE, sBOOL)
        if(settings.amazonDomain == null) settingUpdate("amazonDomain", "amazon.com", sENUM)
        if(settings.regionLocale == null) settingUpdate("regionLocale", "en-US", sENUM)

        if(!(Boolean)state.serviceConfigured) {
            section(sTS("Cookie Server Deployment Option:")) {
                input "useHeroku", sBOOL, title: inTS1("Deploy server to Heroku?", sHEROKU), description: "Turn Off to allow local server deployment", required: false, defaultValue: true, submitOnChange: true
                if(!(Boolean)settings.useHeroku) { paragraph """<p style="color: red;">Local Server deployments are only allowed on Hubitat and are something that can be very difficult for me to support.  I highly recommend Heroku deployments for most users.</p>""" }
            }
            section() { paragraph pTS("To proceed with the server setup.\nTap on 'Begin Server Setup' below", sNULL, true, sCLR4D9), state: sCOMPLT }
            srvcPrefOpts(true)
            section(sTS("Deploy the Server:")) {
                href (url: getAppEndpointUrl("config"), style: sEXTNRL, title: inTS1("Begin Server Setup", "upload"), description: sTTP, required: false, state: sCOMPLT)
            }
        } else {
            String myUrl = "${getServerHostURL()}/config"
            String t0 = getServiceConfDesc()
            if(!authValid) {
                section(sTS("Authentication:")) {
                    paragraph pTS("You still need to Login to Amazon to complete the setup", sNULL, true, sCLRRED), required: true, state: sNULL
                    href url: myUrl, style: sEXTNRL, required: false, title: inTS1("Amazon Login Page", sAMAZONORNG), description: t0+'\n\n'+sTTP
                }
            } else {
                Boolean oH = (Boolean)getServerItem("onHeroku")
                    section(sTS("Server Management:")) {
                        if(oH && (String)state.herokuName) { paragraph pTS("Heroku Name:\n \u2022 ${(String)state.herokuName}", sNULL, true, sCLR4D9), state: sCOMPLT }
                        href url: myUrl, style: sEXTNRL, required: false, title: inTS1("Amazon Login Page", sAMAZONORNG), description: t0+'\n\n'+sTTP
                        if(oH) href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/settings", style: sEXTNRL, required: false, title: inTS1("Heroku App Settings", sHEROKU), description: sTTP
                        if(oH) href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/logs", style: sEXTNRL, required: false, title: inTS1("Heroku App Logs", sHEROKU), description: sTTP
                    }
            }
            srvcPrefOpts()
        }
        section(sTS("Reset Options (Tap to show):"), hideable: true, hidden: true) {
            input "resetService", sBOOL, title: inTS1("Reset Service Data?", sRESET), description: "This will clear all references to the current server and allow you to redeploy a new instance.\nLeave the page and come back after toggling.",
                required: false, defaultValue: false, submitOnChange: true
            paragraph pTS("This will clear all references to the current server and allow you to redeploy a new instance.\nLeave the page and come back after toggling.", sNULL, false, sCLRGRY)
            if(settings?.resetService) { clearCloudConfig() }
        }
/*        section(sTS("Documentation & Settings:")) {
            href url: documentationLink(), style: sEXTNRL, required: false, title: inTS1("View Documentation", "documentation"), description: sTTP
            href "settingsPage", title: inTS1("Manage Logging, and Metrics", sSETTINGS), description: sTTM
        } */
        state.resumeConfig = false
    }
}

def srvcPrefOpts(Boolean req=false) {
    section(sTS("${req ? "Required " : sBLANK}Amazon Locale Settings"), hideable: false, hidden: false) {
        if(req) {
            input "amazonDomain", sENUM, title: inTS1("Select your Amazon Domain?", sAMAZONORNG), description: sBLANK, required: true, defaultValue: "amazon.com", options: amazonDomainOpts(), submitOnChange: true
            input "regionLocale", sENUM, title: inTS1("Select your Locale?", "www"), description: sBLANK, required: true, defaultValue: "en-US", options: localeOpts(), submitOnChange: true
        } else {
            String s = sBLANK
            s += settings.amazonDomain ? "Amazon Domain: (${settings.amazonDomain})" : sBLANK
            s += settings.regionLocale ? "\nLocale Region: (${settings.regionLocale})" : sBLANK
            paragraph pTS(s, getHEAppImg(sAMAZONORNG), false, sCLR4D9), state: sCOMPLT
        }
    }
}

def deviceManagePage() {
    return dynamicPage(name: "deviceManagePage", uninstall: false, install: false) {
        Boolean newInstall = !(Boolean)state.isInstalled
        section(sTS("Alexa Devices:")) {
            if(!newInstall) {
                List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : sBLANK}${it?.value?.supported == false ? " \u2639" : sBLANK}" }?.sort()
                Map skDevs = ((Map)state.skippedDevices)?.findAll { (it?.value?.reason != sIN_IGNORE) }
                Map ignDevs = ((Map)state.skippedDevices)?.findAll { (it?.value?.reason == sIN_IGNORE) }
                if(devs?.size()) {
                    href "deviceListPage", title: inTS("Installed Devices:"), description: "${devs?.join("\n")}\n\nTap to view details...", state: sCOMPLT
                } else { paragraph title: "Discovered Devices:", "No Devices Available", state: sCOMPLT }
                List remDevs = getRemovableDevs()
                if(remDevs?.size()) {
                    href "devCleanupPage", title: inTS("Removable Devices:"), description: "${remDevs?.sort()?.join("\n")}", required: true, state: sNULL
                }
                if(skDevs?.size()) {
                    String uDesc = "Unsupported: (${skDevs?.size()})"
                    uDesc += ignDevs?.size() ? "\nUser Ignored: (${ignDevs?.size()})" : sBLANK
                    uDesc += (Boolean)settings.bypassDeviceBlocks ? "\nBlock Bypass: (Active)" : sBLANK
                    href "unrecogDevicesPage", title: inTS("Unused Devices:"), description: "${uDesc}\n\nTap to view details...", state: sCOMPLT
                }
            }
            String devPrefDesc = devicePrefsDesc()
            href "devicePrefsPage", title: inTS1("Device Detection\nPreferences", sDEVICES), description: "${devPrefDesc ? "${devPrefDesc}\n\n${sTTM}" : sTTC}", state: sCOMPLT
        }
    }
}

def alexaGuardPage() {
    return dynamicPage(name: "alexaGuardPage", uninstall: false, install: false) {
        String gState = (String)state.alexaGuardState ? ((String)state.alexaGuardState == sARM_AWAY ? "Away" : "Home") : "Unknown"
        String gStateIcon = gState == "Unknown" ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
        String gStateTitle = (gState == "Unknown" || gState == "Home") ? "Set Guard to Armed?" : "Set Guard to Home?"
        section(sTS("Alexa Guard Control")) {
            input "alexaGuardAwayToggle", sBOOL, title: inTS1(gStateTitle, gStateIcon), description: "Current Status: ${gState}", defaultValue: false, submitOnChange: true
        }
        if(settings?.alexaGuardAwayToggle != state.alexaGuardAwayToggle) {
            setGuardState(settings.alexaGuardAwayToggle == true ? sARM_AWAY : sARM_STAY)
        }
        state.alexaGuardAwayToggle = settings?.alexaGuardAwayToggle
        section(sTS("Automate Guard Control")) {
            String t0 = guardAutoDesc()
            href "alexaGuardAutoPage", title: inTS1("Automate Guard Changes", "alarm_disarm"), description: t0, state: (t0==sTTC ? sNULL : sCOMPLT)
        }
    }
}

def alexaGuardAutoPage() {
    return dynamicPage(name: "alexaGuardAutoPage", uninstall: false, install: false) {
        String asn = getAlarmSystemName(true)
        List amo = getAlarmModes()
        Boolean alarmReq = (settings.guardAwayAlarm || settings.guardHomeAlarm)
        Boolean modeReq = (settings.guardAwayModes || settings.guardHomeModes)
        // Boolean swReq = (settings?.guardAwaySw || settings?.guardHomeSw)
        section(sTS("Set Guard Using ${asn}")) {
            input "guardHomeAlarm", sENUM, title: inTS1("Home in ${asn} modes.", "alarm_home"), description: sTTS, options: amo, required: alarmReq, multiple: true, submitOnChange: true
            input "guardAwayAlarm", sENUM, title: inTS1("Away in ${asn} modes.", "alarm_away"), description: sTTS, options: amo, required: alarmReq, multiple: true, submitOnChange: true
        }

        section(sTS("Set Guard Using Modes")) {
            input "guardHomeModes", "mode", title: inTS1("Home in these Modes?", "mode"), description: sTTS, required: modeReq, multiple: true, submitOnChange: true
            input "guardAwayModes", "mode", title: inTS1("Away in these Modes?", "mode"), description: sTTS, required: modeReq, multiple: true, submitOnChange: true
        }

        section(sTS("Set Guard Using Switches:")) {
            input "guardHomeSwitch", "capability.switch", title: inTS1("Home when any of these are On?", sSWITCH), description: sTTS, multiple: true, required: false, submitOnChange: true
            input "guardAwaySwitch", "capability.switch", title: inTS1("Away when any of these are On?", sSWITCH), description: sTTS, multiple: true, required: false, submitOnChange: true
        }

        section(sTS("Set Guard using Presence")) {
            input "guardAwayPresence", "capability.presenceSensor", title: inTS1("Away when these devices are All away?", "presence"), description: sTTS, multiple: true, required: false, submitOnChange: true
        }
        if(guardAutoConfigured()) {
            section(sTS("Delay:")) {
                input "guardAwayDelay", "number", title: inTS1("Delay before arming Away?\n(in seconds)", "delay_time"), description: "Enter number in seconds", required: false, defaultValue: 30, submitOnChange: true
            }
        }
        section(sTS("Restrict Guard Changes (Optional):")) {
            input "guardRestrictOnSwitch", "capability.switch", title: inTS1("Only when these are On?", sSWITCH), description: sTTS, multiple: true, required: false, submitOnChange: true
            input "guardRestrictOffSwitch", "capability.switch", title: inTS1("Only when these are Off?", sSWITCH), description: sTTS, multiple: true, required: false, submitOnChange: true
        }
    }
}

Boolean guardAutoConfigured() {
    return ((settings.guardAwayAlarm && settings.guardHomeAlarm) || (settings.guardAwayModes && settings.guardHomeModes) || (settings.guardAwaySwitch && settings.guardHomeSwitch) || settings.guardAwayPresence)
}

String guardAutoDesc() {
    String str = sBLANK
    if(guardAutoConfigured()) {
        str += "Guard Triggers:"
        str += (settings.guardAwayAlarm && settings.guardHomeAlarm) ? "\n \u2022 Using ${getAlarmSystemName()}" : sBLANK
        str += settings.guardHomeModes ? "\n \u2022 Home Modes: (${settings.guardHomeModes?.size()})" : sBLANK
        str += settings.guardAwayModes ? "\n \u2022 Away Modes: (${settings.guardAwayModes?.size()})" : sBLANK
        str += settings.guardHomeSwitch ? "\n \u2022 Home Switches: (${settings.guardHomeSwitch?.size()})" : sBLANK
        str += settings.guardAwaySwitch ? "\n \u2022 Away Switches: (${settings.guardAwaySwitch?.size()})" : sBLANK
        str += settings.guardAwayPresence ? "\n \u2022 Presence Home: (${settings.guardAwayPresence?.size()})" : sBLANK
    }
    return str == sBLANK ? sTTC : "${str}\n\n${sTTM}"
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
            Boolean inAwayMode = isInMode(settings.guardAwayModes)
            Boolean inHomeMode = isInMode(settings.guardHomeModes)
            if(inAwayMode && inHomeMode) { logError("Guard Control Trigger can't act because same mode is in both Home and Away input"); return }
            if(inAwayMode && !inHomeMode) { newState = sARM_AWAY }
            if(!inAwayMode && inHomeMode) { newState = sARM_STAY }
            break
        case "switch":
            Boolean inAwaySw = isSwitchOn(settings.guardAwaySwitch)
            Boolean inHomeSw = isSwitchOn(settings.guardHomeSwitch)
            if(inAwaySw && inHomeSw) { logError("Guard Control Trigger can't act because both switch groups are in both Home and Away input"); return }
            if(inAwaySw && !inHomeSw) { newState = sARM_AWAY }
            if(!inAwaySw && inHomeSw) { newState = sARM_STAY }
            break
        case "presence":
            newState = isSomebodyHome(settings.guardAwayPresence) ? sARM_STAY : sARM_AWAY
            break
        case "alarmSystemStatus":
        case "hsmStatus":
            Boolean inAlarmHome = isInAlarmMode(settings.guardHomeAlarm)
            Boolean inAlarmAway = isInAlarmMode(settings.guardAwayAlarm)
            if(inAlarmAway && !inAlarmHome) { newState = sARM_AWAY }
            if(!inAlarmAway && inAlarmHome) { newState = sARM_STAY }
            break
    }
    if(curState == newState) { logDebug("Skipping Guard Change... New Guard State is the same as current state: ($curState)") }
    if(newState && curState != newState) {
        if (newState == sARM_STAY) {
            unschedule("setGuardAway")
            logInfo("Setting Alexa Guard Mode to Home...")
            setGuardHome()
        }
        if(newState == sARM_AWAY) {
            if(settings?.guardAwayDelay) { logWarn("Setting Alexa Guard Mode to Away in (${settings?.guardAwayDelay} seconds)", true); runIn(settings?.guardAwayDelay, "setGuardAway") }
            else { setGuardAway(); logWarn("Setting Alexa Guard Mode to Away...", true) }
        }
    }
}

Boolean guardRestrictOk() {
    Boolean onSwOk = settings.guardRestrictOnSwitch ? isSwitchOn(settings.guardRestrictOnSwitch) : true
    Boolean offSwOk = settings.guardRestrictOffSwitch ? !isSwitchOn(settings.guardRestrictOffSwitch) : true
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
                    href "actionDuplicationPage", title: inTS1("Create Duplicate Action?", "question"), description: sTTP
                }
            }
        }
        if(actApps?.size()) {
            section (sTS("Action History:")) {
                href "viewActionHistory", title: inTS1("View Action History", "tasks"), description: "(Grouped by Action)", state: sCOMPLT
            }

            section (sTS("Global Actions Management:"), hideable: true, hidden: true) {
                if(activeActions?.size()) {
                    input "pauseChildActions", sBOOL, title: inTS1("Pause all actions?", "pause_orange"), description: "When pausing all Actions you can either restore all or open each action and manually unpause it.",
                            defaultValue: false, submitOnChange: true
                    if((Boolean)settings.pauseChildActions) { settingUpdate("pauseChildActions", sFALSE, sBOOL); runIn(3, "executeActionPause") }
                    paragraph pTS("When pausing all Actions you can either restore all or open each action and manually unpause it.", sNULL, false, sCLRGRY)
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
                paragraph pTS("Action already duplicated...\n\nReturn to action page and select it", sNULL, true, sCLRRED), required: true, state: sNULL
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
                    addChildApp("tonesto7", actChildName(), "${actData.label} (Dup)", [settings: actData.settings])
                    paragraph pTS("Action Duplicated...\n\nReturn to Action Page and look for the App with '(Dup)' in the name...", sNULL, true, sCLR4D9), state: sCOMPLT
                    state.actionDuplicated = true
                } else { paragraph pTS("Action not Found", sNULL, true, sCLRRED), required: true, state: sNULL }
            }
        }
    }
}

def zoneDuplicationPage() {
    return dynamicPage(name: "zoneDuplicationPage", nextPage: "zonesPage", uninstall: false, install: false) {
        section() {
            if((Boolean)state.zoneDuplicated) {
                paragraph pTS("Zone already duplicated...\n\nReturn to zone page and select it", sNULL, true, sCLRRED), required: true, state: sNULL
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
                    addChildApp("tonesto7", zoneChildName(), "${znData?.label} (Dup)", [settings: znData.settings])
                    paragraph pTS("Zone Duplicated...\n\nReturn to Zone Page and look for the App with '(Dup)' in the name...", sNULL, true, sCLR4D9), state: sCOMPLT
                    state.zoneDuplicated = true
                } else { paragraph pTS("Zone not Found", sNULL, true, sCLRRED), required: true, state: sNULL }
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
            section(sBLANK) { paragraph pTS("You haven't created any Zones yet!\nTap Create New Zone to get Started") }
        }
        section() {
            app(name: "zoneApp", appName: zoneChildName(), namespace: "tonesto7", multiple: true, title: inTS1("Create New Zone", "es_groups"))
            if(zApps?.size()) {
                input "zoneDuplicateSelect", sENUM, title: inTS1("Duplicate Existing Zone", "es_groups"), description: sTTS, options: zApps?.collectEntries { [(it?.id):it?.getLabel()] }, required: false, multiple: false, submitOnChange: true
                if(settings.zoneDuplicateSelect) {
                    href "zoneDuplicationPage", title: inTS1("Create Duplicate Zone?", "question"), description: sTTP
                }
            }
        }
        if(zApps?.size()) {
            section (sTS("Zone History:")) {
                href "viewZoneHistory", title: inTS1("View Zone History", "tasks"), description: "(Grouped by Zone)", state: sCOMPLT
            }
        }
        section (sTS("Zone Management:"), hideable: true, hidden: true) {
            if(activeZones?.size()) {
                input "pauseChildZones", sBOOL, title: inTS1("Pause all Zones?", "pause_orange"), description: "When pausing all Zones you can either restore all or open each zones and manually unpause it.",
                        defaultValue: false, submitOnChange: true
                if(settings.pauseChildZones) { settingUpdate("pauseChildZones", sFALSE, sBOOL); runIn(3, "executeZonePause") }
                paragraph pTS("When pausing all zones you can either restore all or open each zone and manually unpause it.", sNULL, false, sCLRGRY)
            }
            if(pausedZones?.size()) {
                input "unpauseChildZone", sBOOL, title: inTS1("Restore all actions?", "pause_orange"), defaultValue: false, submitOnChange: true
                if(settings?.unpauseChildZones) { settingUpdate("unpauseChildZones", sFALSE, sBOOL); runIn(3, "executeZoneUnpause") }
            }
            input "reinitChildZones", sBOOL, title: inTS1("Clear Zones Status and force a full status refresh for all zones?", sRESET), defaultValue: false, submitOnChange: true
            if(settings?.reinitChildZones) { settingUpdate("reinitChildZones", sFALSE, sBOOL); runIn(3, "executeZoneUpdate") }
        }
        state.childInstallOkFlag = true
        state.zoneDuplicated = false
        updateZoneSubscriptions()
    }
}

def viewZoneHistory() {
    return dynamicPage(name: "viewZoneHistory", uninstall: false, install: false) {
        List zApps = getZoneApps()
        zApps?.each { z->
            section((String)z.getLabel()) {
                List<String> items = z.getZoneHistory(true)
                items = items ?: []
                items.each { String v->
                    paragraph pTS(v)
                }
            }
        }
    }
}

def viewActionHistory() {
    return dynamicPage(name: "viewActionHistory", uninstall: false, install: false) {
        List actApps = getActionApps()
        actApps?.each { a->
            section(a.getLabel()) {
                List<String> items = (List<String>)a.getActionHistory(true)
                items.each { String v->
                    paragraph pTS(v)
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
    zoneStatusMapFLD = [:]
    getZoneApps()?.each { it?.updated() }
}

def devicePrefsPage() {
//    Boolean newInstall = !(Boolean)state.isInstalled
//    Boolean resumeConf = (Boolean)state.resumeConfig
    return dynamicPage(name: "devicePrefsPage", uninstall: false, install: false) {
        deviceDetectOpts()
        section(sTS("Detection Override:")) {
            paragraph pTS("Device not detected?  Enabling this will allow you to override the developer block for unrecognized or uncontrollable devices.  This is useful for testing the device.", getAppImg("info", true), false)
            input "bypassDeviceBlocks", sBOOL, title: inTS("Override Blocks and Create Ignored Devices?"), description: "WARNING: This will create devices for all remaining ignored devices", required: false, defaultValue: false, submitOnChange: true
        }
        devCleanupSect()
//        if(!newInstall && !resumeConf) { state.refreshDeviceData = true }
    }
}

private deviceDetectOpts() {
//    Boolean newInstall = !(Boolean) state.isInstalled
//    Boolean resumeConf = (Boolean) state.resumeConfig
    section(sTS("Device Detection Preferences")) {
        input "autoCreateDevices", sBOOL, title: inTS1("Auto Create New Devices?", sDEVICES), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        input "createTablets", sBOOL, title: inTS1("Create Devices for Tablets?", "amazon_tablet"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        input "createWHA", sBOOL, title: inTS1("Create Multiroom Devices?", "echo_wha"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
        input "createOtherDevices", sBOOL, title: inTS1("Create Other Alexa Enabled Devices?", sDEVICES), description: "FireTV (Cube, Stick), Sonos, etc.", required: false, defaultValue: false, submitOnChange: true
        input "autoRenameDevices", sBOOL, title: inTS1("Rename Devices to Match Amazon Echo Name?", "name_tag"), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        input "addEchoNamePrefix", sBOOL, title: inTS1("Add 'Echo - ' Prefix to label?", "name_tag"), description: sBLANK, required: false, defaultValue: true, submitOnChange: true
        Map devs = getAllDevices(true)
        if(devs?.size()) {
            input "echoDeviceFilter", sENUM, title: inTS1("Don't Use these Devices", "exclude"), description: sTTS, options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            paragraph title:"Notice:", pTS("To prevent unwanted devices from reinstalling after removal make sure to add it to the Don't use these devices input above before removing.", getAppImg("info", true), false)
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
        section(sTS("Device Cleanup Options:")) {
            List remDevs = getRemovableDevs()
            if(remDevs.size()) { paragraph "Removable Devices:\n${remDevs.sort()?.join("\n")}", required: true, state: sNULL }
            paragraph title:"Notice:", pTS("Remember to add device to filter above to prevent recreation.  Also the cleanup process will fail if the devices are used in external apps/automations", getAppImg("info", true), true, sCLR4D9)
            input "cleanUpDevices", sBOOL, title: inTS("Cleanup Unused Devices?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
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
        str += (Boolean) settings.createTablets ? bulletItem(str, "Tablets") : sBLANK
        str += (Boolean) settings.createWHA ? bulletItem(str, "WHA") : sBLANK
        str += (Boolean) settings.createOtherDevices ? bulletItem(str, "Other Devices") : sBLANK
    }
    str += (Boolean) settings.autoRenameDevices ? bulletItem(str, "Auto Rename") : sBLANK
    str += (Boolean) settings.bypassDeviceBlocks ? "\nBlock Bypass: (Active)" : sBLANK
    return str != sBLANK ? str : sNULL
}

def settingsPage() {
    return dynamicPage(name: "settingsPage", uninstall: false, install: false) {
        section(sTS("Logging:")) {
            input "logInfo", sBOOL, title: inTS1("Show Info Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logWarn", sBOOL, title: inTS1("Show Warning Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logError", sBOOL, title: inTS1("Show Error Logs?", sDEBUG), required: false, defaultValue: true, submitOnChange: true
            input "logDebug", sBOOL, title: inTS1("Show Debug Logs?", sDEBUG), description: "Auto disables after 6 hours", required: false, defaultValue: false, submitOnChange: true
            input "logTrace", sBOOL, title: inTS1("Show Detailed Logs?", sDEBUG), description: "Only enabled when asked to.\n(Auto disables after 6 hours)", required: false, defaultValue: false, submitOnChange: true
        }
        if(advLogsActive()) { logsEnabled() }
        showDevSharePrefs()
        section(sTS("Diagnostic Data:")) {
            paragraph pTS("If you are having trouble send a private message to the developer with a link to this page that is shown below.", sNULL, false, sCLRGRY)
            input "diagShareSensitveData", sBOOL, title: inTS1("Share Cookie Data?", "question"), required: false, defaultValue: false, submitOnChange: true
            href url: getAppEndpointUrl("diagData"), style: sEXTNRL, title: inTS("Diagnostic Data"), description: "Tap to view"
        }
    }
}

def deviceListPage() {
    return dynamicPage(name: "deviceListPage", install: false) {
        section(sTS("Discovered Devices:")) {
            getEchoDeviceMap()?.sort { it?.value?.name }?.each { String k,Map v->
                String str = "Status: (${v.online ? "Online" : "Offline"})"
                str += "\nStyle: ${v.style?.name}"
                str += "\nFamily: ${v.family}"
                str += "\nType: ${v.type}"
                str += "\nVolume Control: (${v.volumeSupport?.toString()?.capitalize()})"
                str += "\nAnnouncements: (${v.announceSupport?.toString()?.capitalize()})"
                str += "\nText-to-Speech: (${v.ttsSupport?.toString()?.capitalize()})"
                str += "\nMusic Player: (${v.mediaPlayer?.toString()?.capitalize()})"
                str += v.supported != true ? "\nUnsupported Device: (True)" : sBLANK
                str += (v.mediaPlayer == true && v.musicProviders) ? "\nMusic Providers: [${v.musicProviders}]" : sBLANK
                String a = (String)v.style?.image
                href "deviceListPage", title: inTS1((String)v.name, a), description: str, required: true, state: (v.online ? sCOMPLT : sNULL)
            }
        }
    }
}

def unrecogDevicesPage() {
    return dynamicPage(name: "unrecogDevicesPage", install: false) {
        Map<String, Map> skDevMap = (Map<String, Map>)state.skippedDevices ?: [:]
        Map<String, Map> ignDevs = skDevMap?.findAll { (it?.value?.reason == sIN_IGNORE) }
        Map<String, Map> unDevs = skDevMap?.findAll { (it?.value?.reason != sIN_IGNORE) }
        section(sTS("Unrecognized/Unsupported Devices:")) {
            if(unDevs?.size()) {
                unDevs.sort { it?.value?.name }?.each { String k,Map v->
                    String str = "Status: (${v.online ? "Online" : "Offline"})\nStyle: ${(String)v.desc}\nFamily: ${(String)v.family}\nType: ${(String)v.type}\nVolume Control: (${v?.volume?.toString()?.capitalize()})"
                    str += "\nText-to-Speech: (${v?.tts?.toString()?.capitalize()})\nMusic Player: (${v?.mediaPlayer?.toString()?.capitalize()})\nReason Ignored: (${v?.reason})"
                    String a = (String)v.image
                    href "unrecogDevicesPage", title: inTS1((String)v.name, a), description: str, required: true, state: (v.online ? sCOMPLT : sNULL)
                }
                input "bypassDeviceBlocks", sBOOL, title: inTS("Override Blocks and Create Ignored Devices?"), description: "WARNING: This will create devices for all remaining ignored devices", required: false, defaultValue: false, submitOnChange: true
            } else {
                paragraph pTS("No Uncognized Devices", sNULL, true)
            }
        }
        if(ignDevs?.size()) {
            section(sTS("User Ignored Devices:")) {
                ignDevs.sort { it?.value?.name }?.each { k,v->
                    String str = "Status: (${v.online ? "Online" : "Offline"})\nStyle: ${(String)v.desc}\nFamily: ${(String)v.family}\nType: ${(String)v.type}\nVolume Control: (${v.volume?.toString()?.capitalize()})"
                    str += "\nText-to-Speech: (${v?.tts?.toString()?.capitalize()})\nMusic Player: (${v?.mediaPlayer?.toString()?.capitalize()})\nReason Ignored: (${v?.reason})"
                    String a = (String)v.image
                    href "unrecogDevicesPage", title: inTS1((String)v.name, a), description: str, required: true, state: (v?.online ? sCOMPLT : sNULL)
                }
            }
        }
    }
}

def showDevSharePrefs() {
    section(sTS("Share Data with Developer:")) {
        paragraph title: "What is this used for?", pTS("These options send non-user identifiable information and error data to diagnose catch trending issues.", sNULL, false)
        input ("optOutMetrics", sBOOL, title: inTS1("Do Not Share Data?", "analytics"), required: false, defaultValue: false, submitOnChange: true)
//        if(!(Boolean)settings.optOutMetrics) {
            href url: getAppEndpointUrl("renderMetricData"), style: sEXTNRL, title: inTS1("View the Data shared with Developer", "view"), description: "Tap to view Data", required: false
 //       }
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
            paragraph title: "Notice:", pTS("The settings configure here are used by both the App and the Devices.", getAppImg("info", true), true, sCLR4D9), state: sCOMPLT
        }
        section (sTS("Notification Devices:")) {
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

        //if(settings?.smsNumbers?.toString()?.length()>=10 || settings.notif_devs || (Boolean)settings.usePush || ((Boolean)settings.pushoverEnabled && settings.pushoverDevices)) {
        if(settings.notif_devs) {
            //if(((Boolean)settings.usePush || settings.notif_devs || ((Boolean)settings.pushoverEnabled && settings.pushoverDevices)) && !state.pushTested && state.pushoverManager) {
            if((settings.notif_devs) && !state.pushTested) {
                if(sendMsg("Info", "Notification Test Successful. Notifications Enabled for ${app?.label}", true)) {
                    state.pushTested = true
                }
            }
            section(sTS("Notification Restrictions:")) {
                String t1 = getNotifSchedDesc()
                href "setNotificationTimePage", title: inTS1("Quiet Restrictions", "restriction"), description: (t1 ? "${t1}\n\n${sTTM}" : sTTC), state: (t1 ? sCOMPLT : sNULL)
            }
            section(sTS("Missed Poll Alerts:")) {
                input (name: "sendMissedPollMsg", type: sBOOL, title: inTS1("Send Missed Checkin Alerts?", "late"), defaultValue: true, submitOnChange: true)
                if((Boolean)settings.sendMissedPollMsg) {
                    input (name: "misPollNotifyWaitVal", type: sENUM, title: inTS1("Time Past the Missed Checkin?", "delay_time"), description: "Default: 45 Minutes", required: false, defaultValue: 2700, options: notifValEnum(), submitOnChange: true)
                    input (name: "misPollNotifyMsgWaitVal", type: sENUM, title: inTS1("Send Reminder After?", "reminder"), description: "Default: 1 Hour", required: false, defaultValue: 3600, options: notifValEnum(), submitOnChange: true)
                }
            }
            section(sTS("Cookie Alerts:")) {
                input (name: "sendCookieRefreshMsg", type: sBOOL, title: inTS1("Send on Refreshed Cookie?", "cookie"), defaultValue: false, submitOnChange: true)
                input (name: "sendCookieInvalidMsg", type: sBOOL, title: inTS1("Send on Invalid Cookie?", "cookie"), defaultValue: true, submitOnChange: true)
            }
            section(sTS("Code Update Alerts:")) {
                input "sendAppUpdateMsg", sBOOL, title: inTS1("Send for Updates...", "update"), defaultValue: true, submitOnChange: true
                if((Boolean)settings.sendAppUpdateMsg) {
                    input (name: "updNotifyWaitVal", type: sENUM, title: inTS1("Send Reminders After?", "reminder"), description: "Default: 12 Hours", required: false, defaultValue: 43200, options: notifValEnum(), submitOnChange: true)
                }
            }
        } else { state.pushTested = false }
    }
}

def setNotificationTimePage() {
    dynamicPage(name: "setNotificationTimePage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
        String a = getNotifSchedDesc()
         if(a) {
             section() {
                 paragraph pTS("Restrictions Status:\n"+a, sNULL, false, sCLR4D9), state: sCOMPLT
                 paragraph pTS("Notice:\nAll selected restrictions  must be inactive for notifications to be sent.", sNULL, false, sCLR4D9), state: sCOMPLT
             }
         }
        Boolean timeReq = settings["qStartTime"] || settings["qStopTime"]
        section() {
            input "qStartInput", sENUM, title: inTS1("Starting at", "start_time"), options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false
            if(settings["qStartInput"] == "A specific time") {
                input "qStartTime", "time", title: inTS1("Start time", "start_time"), required: timeReq
            }
            input "qStopInput", sENUM, title: inTS1("Stopping at", "stop_time"), options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false
            if(settings?."qStopInput" == "A specific time") {
                input "qStopTime", "time", title: inTS1("Stop time", "stop_time"), required: timeReq
            }
            input "quietDays", sENUM, title: inTS1("Only on these week days", "day_calendar"), multiple: true, required: false, options: weekDaysEnum()
            input "quietModes", "mode", title: inTS1("When these modes are Active", "mode"), multiple: true, submitOnChange: true, required: false
        }
    }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section(sBLANK) { paragraph "This will remove the app, all devices, all actions, all zones.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove." }
    }
}

static String bulletItem(String inStr, String strVal) { return "${inStr == sBLANK ? sBLANK : "\n"} "+ sBULLET + " " + strVal }
static String dashItem(String inStr, String strVal, Boolean newLine=false) { return "${(inStr == sBLANK && !newLine) ? sBLANK : "\n"} - " + strVal }

def deviceTestPage() {
    return dynamicPage(name: "deviceTestPage", uninstall: false, install: false) {
        String t1 = sNULL
        section(sBLANK) {
            href "speechPage", title: inTS1("Speech Test", "broadcast"), description: (t1 ? "${t1}\n\n${sTTM}": sTTC), state: (t1 ? sCOMPLT : sNULL)
            href "announcePage", title: inTS1("Announcement Test","announcement"), description: (t1 ? "${t1}\n\n${sTTM}": sTTC), state: (t1 ? sCOMPLT : sNULL)
            href "sequencePage", title: inTS1("Sequence Creator Test", "sequence"), description: (t1 ? "${t1}\n\n${sTTM}": sTTC), state: (t1 ? sCOMPLT : sNULL)
        }
    }
}

def speechPage() {
    return dynamicPage(name: "speechPage", uninstall: false, install: false) {
        section(sBLANK) {
            paragraph pTS("This feature has been known to have issues and may not work because it's not supported by all Alexa devices.  To test each device individually I suggest using the device interface and press Test Speech or Test Announcement")
            Map<String,String> devs = getDeviceList(true, [tts])
            input "test_speechDevices", sENUM, title: inTS("Select Devices to Test the Speech"), description: sTTS, options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            if(((List) settings.test_speechDevices)?.size() >= 3) { 
                paragraph pTS("<b>NOTICE</b>:<br>Amazon will Rate Limit more than 3 device commands at a time.<br>There will be a delay in the other devices but they should play the test after a few seconds", null, false, "red"), state: sNULL
            }
            input "test_speechVolume", "number", title: inTS("Speak at this volume"), description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
            input "test_speechRestVolume", "number", title: inTS("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
            input "test_speechMessage", "text", title: inTS("Message to Speak"), defaultValue: "This is a speech test for your Echo speaks device!!!", required: true, submitOnChange: true
        }
        if((List)settings.test_speechDevices) {
            section() {
                input "test_speechRun", sBOOL, title: inTS("Perform the Speech Test?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_speechRun) { executeSpeechTest() }
            }
        }
    }
}

def announcePage() {
    return dynamicPage(name: "announcePage", uninstall: false, install: false) {
        section(sBLANK) {
            paragraph pTS("This feature has known to have issues and may not work because it's not supported by all Alexa devices.  To test each device individually I suggest using the device interface and press Test Speech or Test Announcement")
            if(!settings.test_announceDevices) {
                input "test_announceAllDevices", sBOOL, title: inTS("Test Announcement using All Supported Devices"), defaultValue: false, required: false, submitOnChange: true
            }
            if(!(Boolean)settings.test_announceAllDevices) {
                def devs = getChildDevicesByCap("announce") ?: []
                input "test_announceDevices", sENUM, title: inTS("Select Devices to Test the Announcement"), description: sTTS, options: (devs?.collectEntries { [(it?.getId()): it?.getLabel() as String] }), multiple: true, required: false, submitOnChange: true
            }
            if((Boolean)settings.test_announceAllDevices || settings.test_announceDevices) {
                input "test_announceVolume", "number", title: inTS("Announce at this volume"), description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
                input "test_announceRestVolume", "number", title: inTS("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
                input "test_announceMessage", "text", title: inTS("Message to announce"), defaultValue: "This is a test of the Echo speaks announcement system!!!", required: true, submitOnChange: true
            }
        }
        if(settings.test_announceDevices || (Boolean)settings.test_announceAllDevices) {
            section() {
                input "test_announceRun", sBOOL, title: inTS("Perform the Announcement?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_announceRun) { executeAnnouncement() }
            }
        }
    }
}

@Field final Map seqItemsAvailFLD = [
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

public Map seqItemsAvail() {
    return seqItemsAvailFLD
}

def sequencePage() {
    return dynamicPage(name: "sequencePage", uninstall: false, install: false) {
        section(sTS("Command Legend:"), hideable: true, hidden: true) {
            String str1 = "Sequence Options:"
            seqItemsAvailFLD.other?.sort()?.each { String k, String v->
                str1 += "${bulletItem(str1, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            }
            String str4 = "DoNotDisturb Options:"
            seqItemsAvailFLD.dnd?.sort()?.each { String k, String v->
                str4 += "${bulletItem(str4, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            }
            String str2 = "Music Options:"
            seqItemsAvailFLD.music?.sort()?.each { String k, String v->
                str2 += "${bulletItem(str2, "${k}${v != sNULL ? "::${v}" : sBLANK}")}"
            }
            String str3 = "Canned TTS Options:"
            seqItemsAvailFLD.speech?.sort()?.each { String k, v->
                String newV
                if(v instanceof List) { newV = sBLANK; v?.sort()?.each { newV += "     ${dashItem(newV, "${it}", true)}" } }
                else newV=v
                str3 += "${bulletItem(str3, "${k}${newV != sNULL ? "::${newV}" : sBLANK}")}"
            }
            paragraph str1, state: sCOMPLT
            // paragraph str4, state: sCOMPLT
            paragraph str2, state: sCOMPLT
            paragraph str3, state: sCOMPLT
            paragraph "Enter the command in a format exactly like this:\nvolume::40,, speak::this is so silly,, wait::60,, weather,, cannedtts_random::goodbye,, traffic,, amazonmusic::green day,, volume::30\n\nEach command needs to be separated by a double comma `,,` and the separator between the command and value must be command::value.", state: sCOMPLT
        }
        section(sTS("Sequence Test Config:")) {
            input "test_sequenceDevice", "device.EchoSpeaksDevice", title: inTS("Select Devices to Test Sequence Command"), description: sTTS, multiple: false, required: ((String)settings.test_sequenceString != sNULL), submitOnChange: true
            input "test_sequenceString", "text", title: inTS("Sequence String to Use"), required: ((String)settings?.test_sequenceDevice != sNULL), submitOnChange: true
        }
        if(settings?.test_sequenceDevice && settings?.test_sequenceString) {
            section() {
                input "test_sequenceRun", sBOOL, title: inTS("Perform the Sequence?"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                if((Boolean)settings.test_sequenceRun) { executeSequence() }
            }
        }
    }
}

static Integer getRecheckDelay(Integer msgLen=null, Boolean addRandom=false) {
    def random = new Random()
    Integer randomInt = random?.nextInt(5) //Was using 7
    if(!msgLen) { return 30 }
    def v = (msgLen <= 14 ? 1 : (msgLen / 14)) as Integer
    // logTrace("getRecheckDelay($msgLen) | delay: $v + $randomInt")
    return addRandom ? (v + randomInt) : (v < 5 ? 5 : v)
}

void executeSpeechTest() {
    settingUpdate("test_speechRun", sFALSE, sBOOL)
    String testMsg = (String)settings.test_speechMessage
    List<String> selectedDevs = (List<String>)settings.test_speechDevices
    selectedDevs?.each { String devSerial->
        def childDev = getChildDeviceBySerial(devSerial)
        if(childDev && childDev?.hasCommand('setVolumeSpeakAndRestore')) {
            childDev?.setVolumeSpeakAndRestore(settings.test_speechVolume as Integer, testMsg, (settings.test_speechRestVolume ?: 30))
        } else {
            logError("Speech Test device with serial# (${devSerial} was not located!!!")
        }
    }
}

void executeAnnouncement() {
    settingUpdate("test_announceRun", sFALSE, sBOOL)
    String testMsg = (String)settings.test_announceMessage
    List sDevs = (Boolean)settings.test_announceAllDevices ? getChildDevicesByCap("announce") : getDevicesFromList((List)settings.test_announceDevices)
    if(sDevs?.size()) {
        if(sDevs.size() > 1) {
            List devObj = []
            sDevs.each { devObj.push([deviceTypeId: it?.getEchoDeviceType() as String, deviceSerialNumber: it?.getEchoSerial() as String]) }
//            String devJson = new groovy.json.JsonOutput().toJson(devObj)
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
        headers: getCookieMap(),
        requestContentType: sAPPJSON,
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map results = [:]
    try {
        httpGet(params) { resp ->
            results = resp?.data ?: [:]
            if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
            if(resp?.status != 200) logWarn("${resp?.status} $params")
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
        section("Test a Music Search on Device:") {
            paragraph "Use this to test the search you discovered above directly on a device.", state: sCOMPLT
            Map testEnum = ["CLOUDPLAYER": "My Library", "AMAZON_MUSIC": "Amazon Music", "I_HEART_RADIO": "iHeartRadio", "PANDORA": "Pandora", "APPLE_MUSIC": "Apple Music", "TUNEIN": "TuneIn", "SIRIUSXM": "siriusXm", "SPOTIFY": "Spotify"]
            input "test_musicProvider", sENUM, title: inTS1("Select Music Provider to perform test", "music"), defaultValue: null, required: false, options: testEnum, multiple: false, submitOnChange: true
            if((String)settings.test_musicProvider) {
                input "test_musicQuery", "text", title: inTS1("Music Search term to test on Device", "search2"), defaultValue: null, required: false, submitOnChange: true
                if((String)settings.test_musicQuery) {
                    input "test_musicDevice", "device.EchoSpeaksDevice", title: inTS1("Select a Device to Test Music Search", "echo_speaks_3.1x"), description: sTTS, multiple: false, required: false, submitOnChange: true
                    if(settings.test_musicDevice) {
                        input "test_musicSearchRun", sBOOL, title: inTS1("Perform the Music Search Test?", "music"), description: sBLANK, required: false, defaultValue: false, submitOnChange: true
                        if((Boolean)settings.test_musicSearchRun) { executeMusicSearchTest() }
                    }
                }
            }
        }
        section(sTS("TuneIn Search Results:")) {
            paragraph "Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", state: sCOMPLT
            input "test_tuneinSearchQuery", "text", title: inTS1("Enter search phrase for TuneIn", "tunein"), defaultValue: sNULL, required: false, submitOnChange: true
            if((String)settings.test_tuneinSearchQuery) {
                href "searchTuneInResultsPage", title: inTS1("View search results!", "search2"), description: sTTP
            }
        }
    }
}

def searchTuneInResultsPage() {
    return dynamicPage(name: "searchTuneInResultsPage", uninstall: false, install: false) {
        Map results = executeTuneInSearch((String)settings.test_tuneinSearchQuery)
        section(sTS("Search Results: (Query: ${(String)settings.test_tuneinSearchQuery})")) {
            if(results?.browseList && results?.browseList?.size()) {
                results?.browseList?.eachWithIndex { item, Integer i->
                    if(i < 25) {
                        if(item?.browseList != null && item?.browseList?.size()) {
                            item?.browseList?.eachWithIndex { item2, i2->
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
    href "searchTuneInResultsPage", title: pTS(b, a, false), description: str, required: true, state: (c ? sCOMPLT : sNULL)
}

private getChildDeviceBySerial(String serial) {
    List childDevs = app?.getChildDevices()
    def a = childDevs?.find { it?.deviceNetworkId?.tokenize("|")?.contains(serial) }
    return a ?: null
}

public getChildDeviceByCap(String cap) {
    List childDevs = app?.getChildDevices()
    def a= childDevs?.find { it?.currentValue("permissions") && it?.currentValue("permissions")?.toString()?.contains(cap) }
    return a ?: null
}

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
            def str = sBLANK
            str += "Hello User, \n\nPlease forgive the interuption but it's been 30 days since you installed/updated this App and I wanted to present you with this one time reminder that donations are accepted (We do not require them)."
            str += "\n\nIf you have been enjoying the software and devices please remember that we have spent thousand's of hours of our spare time working on features and stability for those applications and devices."
            str += "\n\nIf you have already donated, thank you very much for your support!"
            str += "\n\nIf you are just not interested in donating please ignore this message"

            str += "\n\nThanks again for using Echo Speaks"
            paragraph str, required: true, state: sNULL
            href url: textDonateLink(), style: sEXTNRL, required: false, title: "Donations", description: "Tap to open in browser", state: sCOMPLT
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
    //if(app?.getLabel() != "Echo Speaks") { app?.updateLabel("Echo Speaks") }
    if((Boolean)settings.optOutMetrics && (String)state.appGuid) { if(removeInstallData()) { state.appGuid = sNULL } }
    subscribe(location, "systemStart", startHandler)
    if(guardAutoConfigured()) {
        if(settings.guardAwayAlarm && settings.guardHomeAlarm) {
            subscribe(location, "hsmStatus", guardTriggerEvtHandler)
        }
        if(settings.guardAwayModes && settings.guardHomeModes) {
            subscribe(location, "mode", guardTriggerEvtHandler)
        }
        if(settings.guardAwaySwitch && settings.guardHomeSwitch) {
            if(settings.guardHomeSwitch) subscribe(settings.guardHomeSwitch, sSWITCH, guardTriggerEvtHandler)
            if(settings.guardAwaySwitch) subscribe(settings.guardAwaySwitch, sSWITCH, guardTriggerEvtHandler)
        }
        if(settings?.guardAwayPresence) {
            subscribe(settings.guardAwayPresence, "presence", guardTriggerEvtHandler)
        }
    }
    if(!(Boolean)state.resumeConfig) {
        updChildVers()
        updateZoneSubscriptions()
        Boolean a=validateCookie(true)
        if(!(Boolean)state.noAuthActive) {
            runEvery15Minutes("getOtherData")
            runEvery3Hours("getEchoDevices") //This will reload the device list from Amazon
            runIn(11, "postInitialize")
            getOtherData()
            remTsVal("lastDevDataUpdDt") // will force next one to gather EchoDevices
            getEchoDevices()
            if(advLogsActive()) { logsEnabled() }
        } else { unschedule("getEchoDevices"); unschedule("getOtherData") }
    }
}

void startHandler(evt){
    logDebug('startHandler called')
    runIn(6, "restartSocket")
}

void restartSocket(){
    def dev= getSocketDevice()
    if(!(Boolean) dev?.isSocketActive()) { dev?.triggerInitialize() }
}

void stateMigrationChk() {
    if(!getAppFlag("stateMapConverted")) { stateMapMigration() }
}

void updateZoneSubscriptions() {
    if(state.zoneEvtsActive != true) {
        subscribe(location, "es3ZoneState", zoneStateHandler)
        subscribe(location, "es3ZoneRemoved", zoneRemovedHandler)
        state.zoneEvtsActive = true
        runIn(6, "requestZoneRefresh")
    }
}

void postInitialize() {
    logTrace("postInitialize")
    runEvery15Minutes("healthCheck") // This task checks for missed polls, app updates, code version changes, and cloud service health
    appCleanup()
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
        "warnHistory", "errorHistory", "bluetoothData", "dndData", "zoneStatusMap"
    ]
// pushTested
    items?.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
    state.pollBlocked = false
    state.resumeConfig = false
    state.missPollRepair = false
    state.deviceRefreshInProgress = false

    // Settings Cleanup
    List setItems = ["performBroadcast", "stHub", "cookieRefreshDays"]
    settings?.each { si-> ["music", "tunein", "announce", "perform", "broadcast", "sequence", "speech", "test_"]?.each { swi-> if(si?.key?.startsWith(swi as String)) { setItems?.push(si?.key as String) } } }
    setItems?.unique()?.sort()?.each { sI-> if(settings?.containsKey(sI as String)) { settingRemove(sI as String) } }
    cleanUpdVerMap()
}

void wsEvtHandler(evt) {
    // log.trace("wsEvtHandler evt: ${evt}")
    if(evt && evt.id && (evt.attributes?.size() || evt.triggers?.size())) {
        if("bluetooth" in evt.triggers) { runIn(2, "getBluetoothRunIn") } // getBluetoothDevices(true)
        if("activity" in evt.triggers) { runIn(1, "getDeviceActivityRunIn") } // Map a=getDeviceActivity(sNULL, true)
        if(evt.all == true) {
            getEsDevices()?.each { eDev->
                if(evt.attributes?.size()) { evt.attributes?.each { String k,v-> eDev?.sendEvent(name: k, value: v) } }
                if(evt.triggers?.size()) { eDev.websocketUpdEvt(evt.triggers) }
            }
        } else {
            def eDev = findEchoDevice((String)evt.id)
            if(eDev) {
                evt.attributes?.each { String k,v-> eDev?.sendEvent(name: k, value: v) }
                if(evt.triggers?.size()) { eDev?.websocketUpdEvt(evt.triggers) }
            }
        }
    }
}

private findEchoDevice(String serial) {
    def a = getEsDevices()?.find { it?.getEchoSerial()?.toString() == serial }
    return a ?: null
}

void webSocketStatus(Boolean active) {
    logTrace "webSocketStatus... | Active: ${active}"
    state.websocketActive = active
    if(active) remTsVal('bluetoothUpdDt') // healthcheck will re-read
    runIn(3, "updChildSocketStatus")
}

void updChildSocketStatus() {
    Boolean active = (Boolean)state.websocketActive
    logTrace "updChildSocketStatus... | Active: ${active}"
    getEsDevices()?.each { it?.updSocketStatus(active) }
    updTsVal("lastWebsocketUpdDt")
}

def zoneStateHandler(evt) {
    String id = evt?.value?.toString()
    Map data = evt?.jsonData
    // log.trace "zone: ${id} | Data: $data"
    if(data && id) {
        Boolean aa = getTheLock(sHMLF, "zoneStateHandler")
        Map t0 = zoneStatusMapFLD
        Map zoneMap = t0 ?: [:]
        zoneMap[id] = [name: data?.name, active: data?.active, paused: data?.paused]
        zoneStatusMapFLD = zoneMap
        zoneStatusMapFLD = zoneStatusMapFLD
        releaseTheLock(sHMLF)
        List cApps = getActionApps()
        if(cApps?.size()) cApps[0].updZones(zoneMap)
    }
}

def zoneRemovedHandler(evt) {
    String id = evt?.value?.toString()
    Map data = evt?.jsonData
    log.trace "zone removed: ${id} | Data: $data"
    if(data && id) {
        Boolean aa = getTheLock(sHMLF, "zoneRemoveHandler")
        Map t0 = zoneStatusMapFLD
        Map zoneMap = t0 ?: [:]
        zoneMap = zoneMap ?: [:]
        if(zoneMap.containsKey(id)) { zoneMap.remove(id) }
        zoneStatusMapFLD = zoneMap
        zoneStatusMapFLD = zoneStatusMapFLD
        releaseTheLock(sHMLF)
        List cApps = getActionApps()
        if(cApps?.size()) cApps[0].updZones(zoneMap)
    }
}

private requestZoneRefresh() {
    zoneStatusMapFLD =  [:]
    sendLocationEvent(name: "es3ZoneRefresh", value: "sendStatus", data: [sendStatus: true], isStateChange: true, display: false, displayed: false)
}

void checkZoneData() {
    if(!zoneStatusMapFLD) {
        Boolean aa = getTheLock(sHMLF, "getZones")
        zoneStatusMapFLD.initialized = [a:true]
        zoneStatusMapFLD = zoneStatusMapFLD
        releaseTheLock(sHMLF)
        requestZoneRefresh()
    }
}

public Map getZones() {
    checkZoneData()
    Map a = zoneStatusMapFLD
    return a
}

Map getActiveZones() {
    Map zones = getZones()
    return zones.size() ? zones.findAll { it?.value?.active == true && !it?.value?.paused } : [:]
}

Map getInActiveZones() {
    Map zones = getZones()
    return zones.size() ? zones.findAll { it?.value?.active != true  || it?.value?.paused } : [:]
}

static List getMyZNames(Map zones) {
    zones = zones ?: [:]
    return zones.size() ? zones?.collect { (String)it?.value?.name } : []
}

List getActiveZoneNames() {
    return getMyZNames(getActiveZones())
}

List getInActiveZoneNames() {
    return getMyZNames(getInActiveZones())
}

List getZoneApps() {
    return getAllChildApps()?.findAll { (String)it?.name == zoneChildName() }
}

def getZoneById(String id) {
    return getZoneApps()?.find { it?.id?.toString() == id }
}

List getActiveApps() {
    List acts = getActionApps()
    return acts.size() ? acts.findAll { it?.isPaused() != true } : []
}

List getInActiveApps() {
    List acts = getActionApps()
    return acts.size() ? acts.findAll { it?.isPaused() == true } : []
}

List getMyANames(List acts) {
    acts = acts ?: []
    return acts.size() ? acts?.findAll { it }.collect { (String)it?.getLabel() } : []
}

public List getActiveActionNames() {
    return getMyANames(getActiveApps())
}

public List getInActiveActionNames() {
    return getMyANames(getInActiveApps())
}

List getActionApps() {
    return getAllChildApps()?.findAll { it?.name == actChildName() }
}

List getEsDevices() {
    return getChildDevices()?.findAll { it?.isWS() == false }
}

def getSocketDevice() {
    String myId=app.getId()
    String nmS = 'echoSpeaks_websocket'
    nmS = myId+'|'+nmS
    return getChildDevice(nmS)
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
    Map codeVer = (Map)state.codeVersions ?: [:]
    logTrace("Code versions: ${codeVer}")
    if(codeVer.mainApp != appVersionFLD) {
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
        if((String)codeVer.echoDevice != ver) {
            chgs.push("echoDevice")
            state.pollBlocked = true
            updCodeVerMap("echoDevice", ver)
            codeUpdated = true
        }
    }
    def wsDev = getSocketDevice()
    if(wsDev) {
        String ver = (String)wsDev?.devVersion()
        if((String)codeVer.wsDevice != ver) {
            chgs.push("wsDevice")
            updCodeVerMap("wsDevice", ver)
            codeUpdated = true
        }
    }
    List cApps = getActionApps()
    if(cApps?.size()) {
        String ver = (String)cApps[0]?.appVersion()
        if((String)codeVer.actionApp != ver) {
            chgs.push("actionApp")
            state.pollBlocked = true
            updCodeVerMap("actionApp", ver)
            codeUpdated = true
        }
    }
    List zApps = getZoneApps()
    if(zApps?.size()) {
        String ver = (String)zApps[0]?.appVersion()
        if((String)codeVer.zoneApp != ver) {
            chgs.push("zoneApp")
            state.pollBlocked = true
            // log.debug "zoneVer: ver"
            updCodeVerMap("zoneApp", ver)
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
    String json = new groovy.json.JsonOutput().toJson([message: "success", version: appVersionFLD])
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
    String json = new groovy.json.JsonOutput().toJson(resp)
    incrementCntByKey("getCookieCnt")
    render contentType: sAPPJSON, data: json, status: 200
}

Map getCookieMap() {
    return [cookie: getCookieVal(), csrf: getCsrfVal()]
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

    if(data && data.cookieData) {
        logTrace("cookieData Received: ${request?.JSON?.cookieData?.keySet()}")

        data.cookieData.each { String k,v-> cookieItems[k] = v as String }
        state.cookieData = cookieItems
        String myId=app.getId()
        cookieDataFLD[myId] = cookieItems
        cookieDataFLD = cookieDataFLD
        state.clearCnt = 0
    }

    if(data) {
        updServerItem("isLocal", (data.isLocal == true))
        updServerItem("onHeroku", (data.onHeroku != false || (!data?.isLocal && (Boolean)settings.useHeroku)))
        updServerItem("serverHost", ((String)data.serverUrl ?: sNULL))
        updCodeVerMap("server", (String)data.version)
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
    String json = new groovy.json.JsonOutput().toJson([message: "success", version: appVersionFLD])
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
    String json = new groovy.json.JsonOutput().toJson([message: "success", version: appVersionFLD])
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
    unschedule("getOtherData")
    logWarn("Cookie Data has been cleared and Device Data Refreshes have been suspended...")
    updateChildAuth(false)
}

Boolean refreshDevCookies() {
    logTrace("refreshDevCookies()")
    settingUpdate("refreshDevCookies", sFALSE, sBOOL)
    logDebug("Re-Syncing Cookie Data with Devices")
    Boolean isValid = ((Boolean)state.authValid && getCookieVal() && getCsrfVal())
    updateChildAuth(isValid)
    return isValid
}

void updateChildAuth(Boolean isValid) {
    Map cook = getCookieMap()
    getChildDevices()?.each { (isValid) ? it?.updateCookies(cook) : it?.removeCookies(true) }
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
//    } else if (isAuth && (Boolean)state.noAuthActive) {
        // waiting for initialize to run
//        logWarn("OOPS Somehow your Auth is Valid but the NoAuthActive State is true.  Clearing noAuthActive flag to allow device refresh")
//        unschedule("noAuthReminder")
//        state.noAuthActive = false
        // runIn(10, "initialize", [overwrite: true])
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
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v?.toString(), "utf-8").replaceAll("\\+", "%20")}" }?.sort().join("&")
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
        def rData = response?.data ?: null
        if(response?.status != 200) { logWarn("wakeupServerResp: ${response?.status} $data"); return }
        updTsVal("lastServerWakeDt")
        if (rData && rData == "OK") {
            logDebug("$rData wakeupServer Completed... | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms) | Source: (${data?.wakesrc}) ${data}")
            if(data?.refreshCookie == true) { runIn(2, "cookieRefresh") }
            if(data?.updateGuard == true) { runIn(2, "checkGuardSupportFromServer") }
        } else {
            logWarn("wakeupServerResp: noData ${rData} ${data}")
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
            cMsg = "Amazon Cookie Refresh FAILED ${response?.status} $data"
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
    if(!frc && valid && lastChk <= 900 && cookieOk) { return valid }
    if(!frc && valid && lastSpoke <= 900 && cookieOk) { return valid }
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
            headers: getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20,
        ]
        logTrace(meth)
        if(!frc) execAsyncCmd("get", "validateCookieResp", params, [dt:execDt])
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

def validateCookieResp(resp, data){
    try {
        String meth = 'validCookieResp'
        if(resp?.status != 200) logWarn("${resp?.status} $meth")
        if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
        def t0 = resp?.data
        Map aData
        if( t0 instanceof String)  aData = parseJson(resp?.data)
        else aData =  resp?.data
        aData = aData ?: null
        aData = (Map)aData?.authentication ?: null
//        Map aData = resp?.data?.authentication ?: null
        if (aData) {
//            log.debug "aData: $aData"
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

private getCustomerData(Boolean frc=false) {
    try {
        if(!frc && state.amazonCustomerData && getLastTsValSecs("lastCustDataUpdDt") < 3600) { return state.amazonCustomerData }
        if(!isAuthValid("getCustomerData")) { return null }
        Long execDt = now()
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/get-customer-pfm",
            query: ["_": execDt],
            headers: getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20,
        ]
        logTrace("getCustomerData")
        httpGet(params) { resp->
            if(resp?.status != 200) logWarn("${resp?.status} $params")
            if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
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
    } catch(ex) {
        respExceptionHandler(ex, "getCustomerData", true)
        updTsVal("lastCustDataUpdDt")
    }
}
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
            if(response?.status == 200) updTsVal("lastSpokeToAmazon")
            List resp = response?.data ?: []
            Map accItems = (resp?.size()) ? resp?.findAll { it?.signedInUser?.toString() == sTRUE }?.collectEntries { [(it?.commsId as String): [firstName: it?.firstName as String, signedInUser: it?.signedInUser, isChild: it?.isChild]]} : [:]
            state.accountCommIds = accItems
            logDebug("Amazon User CommId's: (${accItems})")
        }
    } catch(ex) {
        respExceptionHandler(ex, "userCommIds")
    }
}
*/
public void childInitiatedRefresh() {
    Integer lastRfsh = getLastTsValSecs("lastChildInitRefreshDt", 3600)?.abs()
    if(!(Boolean)state.deviceRefreshInProgress && lastRfsh > 120) {
        logDebug("A Child Device is requesting a Device List Refresh...")
        updTsVal("lastChildInitRefreshDt")
        getOtherData()
        runIn(3, "getEchoDevices1")
    } else {
        logWarn("childInitiatedRefresh request ignored... Refresh already in progress or it's too soon to refresh again | Last Refresh: (${lastRfsh} seconds)")
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
    if(!frc && (Map)state.musicProviders && getLastTsValSecs("musicProviderUpdDt") < 3600) { return (Map)state.musicProviders }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/entities",
        query: [ skillId: "amzn1.ask.1p.music" ],
        headers: [Connection: "keep-alive", DNT: "1", "Routines-Version": "1.1.210292" ] + getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20
    ]
    Map items = [:]
    try {
        logTrace("getMusicProviders")
        httpGet(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) updTsVal("lastSpokeToAmazon")
            List rData = response?.data ?: []
            if(rData.size()) {
                rData.findAll { it?.availability == "AVAILABLE" }?.each { Map item->
                    items[item.id] = (String)item.displayName
                }
            }
            // log.debug "Music Providers: ${items}"
            state.musicProviders = items
            updTsVal("musicProviderUpdDt")
        }
    } catch (ex) {
        respExceptionHandler(ex, "getMusicProviders", true)
    }
    return items
}

private getOtherData() {
    stateMigrationChk()
    getDoNotDisturb()
    getBluetoothDevices()
    Map aa = getMusicProviders()
    // getCustomerData()
    // getAlexaSkills()
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
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20
    ]
//    Map btResp = [:]
    try {
        logTrace("getBluetoothDevices")
        if(!frc) execAsyncCmd("get", "getBluetoothResp", params, [:])
        else {
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
        if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
        def t0 = resp?.data
        Map btResp
        if (t0 instanceof String) { btResp = parseJson(resp?.data) }
        else { btResp = resp?.data }
        // log.debug "Bluetooth Items: ${btResp}"
        String myId=app.getId()
        bluetoothDataFLD[myId] = btResp
        bluetoothDataFLD=bluetoothDataFLD
        updTsVal("bluetoothUpdDt")
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
    btData = bluetoothDataFLD[myId]
    if(btData == null) {
        bluetoothDataFLD[myId] = [:]
        bluetoothDataFLD=bluetoothDataFLD
        btData = [:]
    }
    Map bluData = btData && btData.bluetoothStates?.size() ? btData.bluetoothStates?.find { it?.deviceSerialNumber == serialNumber } : [:]
    if(bluData && bluData.size() && bluData.pairedDeviceList && bluData.pairedDeviceList?.size()) {
        def bData = bluData.pairedDeviceList.findAll { (it?.deviceClass != "GADGET") }
        bData?.findAll { it?.address != null }?.each {
            btObjs[it?.address as String] = it
            if(it?.connected == true) { curConnName = it?.friendlyName as String }
        }
    }
    List tob = btObjs?.findAll { it?.value?.friendlyName != null }?.collect { it?.value?.friendlyName?.toString()?.replaceAll("\ufffd", sBLANK) }
    return [btObjs: btObjs, pairedNames: tob ?: [], curConnName: curConnName?.replaceAll("\ufffd", "")]
}

@Field volatile static Map<String,Map> devActivityMapFLD = [:]

void getDeviceActivityRunIn() {
     Map a = getDeviceActivity(sNULL, true)
}

Map getDeviceActivity(String serialNum, Boolean frc=false) {
    if(!isAuthValid("getDeviceActivity")) { return null }
    try {
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/activities",
            query: [ size: 5, offset: 1 ],
            headers: getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20
        ]
        String appId=app.getId()
        Map lastActData = devActivityMapFLD[appId]
        lastActData = lastActData ?: null
        // log.debug "activityData(IN): $lastActData"
        Integer lastUpdSec = getLastTsValSecs("lastDevActChk")
        // log.debug "lastUpdSec: $lastUpdSec"

        if((frc && lastUpdSec > 3) || lastUpdSec >= 360) {
            logTrace("getDeviceActivity($serialNum, $frc)")
            updTsVal("lastDevActChk")
            httpGet(params) { response->
                if(response?.status != 200) logWarn("${response?.status} $params")
                if(response?.status == 200) updTsVal("lastSpokeToAmazon")
                if (response?.data && response?.data?.activities != null) {
                    Map lastCommand = response?.data?.activities?.find {
                        (it?.domainAttributes == null || it?.domainAttributes?.startsWith("{")) &&
                        it?.activityStatus?.equals("SUCCESS") &&
                        (it?.utteranceId?.startsWith(it?.sourceDeviceIds?.deviceType) || it?.utteranceId?.startsWith("Vox:")) &&
                        it?.utteranceId?.contains(it?.sourceDeviceIds?.serialNumber)
                    }
                    if (lastCommand) {
                        Map lastDescription = new groovy.json.JsonSlurper().parseText((String)lastCommand.description)
                        def lastDevice = lastCommand.sourceDeviceIds?.get(0)
                        lastActData = [ serialNumber: lastDevice?.serialNumber, spokenText: lastDescription?.summary, lastSpokenDt: lastCommand?.creationTimestamp ]

                        devActivityMapFLD[appId] = lastActData
                        devActivityMapFLD = devActivityMapFLD
                    }
                }
            }
        }
        if(serialNum && lastActData && lastActData.size() && lastActData.serialNumber == serialNum) {
            // log.debug "activityData(OUT): $lastActData"
            return lastActData
        }
    } catch (ex) {
        if(ex?.message != "Bad Request") {
            respExceptionHandler(ex, "getDeviceActivity")
        }
        // log.error "getDeviceActivity error: ${ex.message}"
    }
    return null
}

void getDoNotDisturb() {
    if(!isAuthValid("getDoNotDisturb")) { return }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/dnd/device-status-list",
        query: [_: now()],
        headers: getCookieMap(),
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
        String myId=app.getId()
        if(!dndDataFLD[myId]) { dndDataFLD[myId] = [:] }
    }
}

void DnDResp(resp, data){
    try {
        String meth = 'DnDResp'
        if(resp?.status != 200) logWarn("${resp?.status} $meth")
        if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
        def t0 = resp?.data
        def dndResp
        if( t0 instanceof String)  dndResp = parseJson(resp?.data)
        else dndResp = resp?.data
//            log.debug "DoNotDisturb Data: ${dndResp}"
            String myId=app.getId()
            dndDataFLD[myId] = dndResp
            dndDataFLD=dndDataFLD
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
    Map dndData = sData && sData.doNotDisturbDeviceStatusList?.size() ? sData.doNotDisturbDeviceStatusList?.find { it?.deviceSerialNumber == serialNumber } : [:]
    return (dndData && dndData.enabled == true)
}

public Map getAlexaRoutines(String autoId=sNULL, Boolean utterOnly=false) {
    if(!isAuthValid("getAlexaRoutines")) { return [:]}
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/automations${autoId ? "/${autoId}" : sBLANK}",
        query: [ limit: 100 ],
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20
    ]

    Map rtResp = [:]
    try {
        logTrace("getAlexaRoutines($autoId, $utterOnly)")
        httpGet(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) updTsVal("lastSpokeToAmazon")
            rtResp = response?.data ?: [:]
            // log.debug "alexaRoutines: $rtResp"
            if(rtResp) {
                if(autoId) {
                    return rtResp
                } else {
                    Map items = [:]
                    Integer cnt = 1
                    if(rtResp.size()) {
                        rtResp.findAll { it?.status == "ENABLED" }?.each { item->
                            String myK = item?.automationId?.toString()
                            if(item?.name != null) {
                                items[myK] = item?.name
                            } else {
                                if(item?.triggers?.size()) {
                                    item?.triggers?.each { trg->
                                        if(trg?.payload?.containsKey("utterance") && trg?.payload?.utterance != null) {
                                            items[myK] = trg?.payload?.utterance as String
                                        } else {
                                            items[myK] = "Unlabeled Routine ($cnt)"
                                            cnt++
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // log.debug "routine items: $items"
                    return items
                }
            }
        }
    } catch (ex) {
        respExceptionHandler(ex, "getAlexaRoutines", true)
    }
    return rtResp
}

Boolean executeRoutineById(String routineId) {
    Long execDt = now()
    Map routineData = getAlexaRoutines(routineId)
    if(routineData && routineData?.sequence) {
        sendSequenceCommand("ExecuteRoutine", routineData, null)
        logDebug("Executed Alexa Routine | Process Time: (${(now()-execDt)}ms) | RoutineId: ${routineId}")
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
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    logTrace("checkGuardSupport")
    execAsyncCmd("get", "checkGuardSupportResponse", params, [execDt: now(), aws: true])
}

void checkGuardSupportResponse(response, data) {
    // log.debug "checkGuardSupportResponse Resp Size(${response?.data?.toString()?.size()})"
    Boolean guardSupported = false
    try {
        if(response?.status != 200) logWarn("${response?.status} $data")
        if(response?.status == 200 && data?.aws) updTsVal("lastSpokeToAmazon")
        Integer respLen = response?.data?.toString()?.length() ?: null
        // log.trace("GuardSupport Response Length: ${respLen}")
        if(response?.data && respLen && respLen > 485000) {
            Map minUpdMap = getMinVerUpdsRequired()
            if(!minUpdMap?.updRequired || (minUpdMap?.updItems && !minUpdMap?.updItems?.contains("Echo Speaks Server"))) {
                wakeupServer(false, false, "checkGuardSupport")
                logDebug("Guard Support Check Response is too large for ST... Checking for Guard Support using the Server")
            } else {
                logWarn("Can't check for Guard Support because server version is out of date...  Please update to the latest version...")
            }
            state.guardDataOverMaxSize = true
            return
        }
        Map resp = response?.data ? parseJson(response?.data?.toString()) : null
        if(resp && resp.networkDetail) {
            Map details = parseJson(resp.networkDetail as String)
            Map locDetails = details?.locationDetails?.locationDetails?.Default_Location?.amazonBridgeDetails?.amazonBridgeDetails["LambdaBridge_AAA/OnGuardSmartHomeBridgeService"] ?: null
            if(locDetails && locDetails?.applianceDetails && locDetails?.applianceDetails?.applianceDetails) {
                def guardKey = locDetails?.applianceDetails?.applianceDetails?.find { it?.key?.startsWith("AAA_OnGuardSmartHomeBridgeService_") }
                def guardData = locDetails?.applianceDetails?.applianceDetails[guardKey?.key]
                // log.debug "Guard: ${guardData}"
                if(guardData?.modelName == "REDROCK_GUARD_PANEL") {
                    state.guardData = [
                        entityId: guardData?.entityId,
                        applianceId: guardData?.applianceId,
                        friendlyName: guardData?.friendlyName,
                    ]
                    guardSupported = true
                } // else { logDebug("checkGuardSupportResponse | No Guard Data Received Must Not Be Enabled...") }
            }
        } else { logError("checkGuardSupportResponse Error | No data received...") }
    } catch (ex) {
        respExceptionHandler(ex, 'checkGuardSupportResponse', true)
    }
    state.alexaGuardSupported = guardSupported
    updTsVal("lastGuardSupChkDt")
    state.guardDataSrc = "app"
    if(guardSupported) getGuardState()
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
            logWarn("checkGuardSupportServerResp: ${response?.status} $data")
            return
        } else {
            Map resp = response?.data ? parseJson(response?.data?.toString()) : null
            // log.debug "GuardSupport Server Response: ${resp}"
            if(resp) {
                if(resp.guardData) {
                    log.debug "AGS Server Resp: ${resp?.guardData}"
                    state.guardData = resp.guardData
                    guardSupported = true
                }
            } else { logError("checkGuardSupportServerResponse Error | No data received..."); return }
        }
    } catch (ex) {
        respExceptionHandler(ex, "checkGuardSupportServerResponse", false, false)
        return
    }
    state.alexaGuardSupported = guardSupported
    state.guardDataOverMaxSize = guardSupported
    state.guardDataSrc = "server"
    updTsVal("lastGuardSupChkDt")
    if(guardSupported) getGuardState()
}

void getGuardState() {
    if(!isAuthValid("getGuardState")) { return }
    if(!(Boolean)state.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix/state",
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
        body: [ stateRequests: [ [entityId: state.guardData?.applianceId, entityType: "APPLIANCE" ] ] ]
    ]
    try {
        logTrace("getGuardState")
        httpPostJson(params) { resp ->
            if(resp?.status != 200) logWarn("${resp?.status} $params")
            if(resp?.status == 200) updTsVal("lastSpokeToAmazon")
            Map respData = resp?.data ?: null
            if(respData && respData?.deviceStates && respData?.deviceStates[0] && respData?.deviceStates[0]?.capabilityStates) {
                def guardStateData = parseJson(respData?.deviceStates[0]?.capabilityStates as String)
                String curState = (String)state.alexaGuardState ?: sNULL
                state.alexaGuardState = guardStateData?.value[0] ? (String)guardStateData?.value[0] : (String)guardStateData?.value
                settingUpdate("alexaGuardAwayToggle", (((String)state.alexaGuardState == sARM_AWAY) ? sTRUE : sFALSE), sBOOL)
                logDebug("Alexa Guard State: (${(String)state.alexaGuardState})")
                if(curState != (String)state.alexaGuardState) updGuardActionTrig()
                updTsVal("lastGuardStateChkDt")
            }
            // log.debug "GuardState resp: ${respData}"
        }
    } catch (ex) {
        respExceptionHandler(ex, "getGuardState", true)
    }
}

void setGuardState(String guardState) {
    Long execTime = now()
    if(!isAuthValid("setGuardState")) { return }
    if(!(Boolean)state.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return }
    guardState = guardStateConv(guardState)
    logDebug("setAlexaGuard($guardState)")
    try {
        String body = new groovy.json.JsonOutput()?.toJson([ controlRequests: [ [ entityId: state.guardData?.applianceId as String, entityType: "APPLIANCE", parameters: [action: "controlSecurityPanel", armState: guardState ] ] ] ])
        Map params = [
            uri: getAmazonUrl(),
            path: "/api/phoenix/state",
            headers: getCookieMap(),
            contentType: sAPPJSON,
            timeout: 20,
            body: body
        ]
        logTrace("setGuardState")
        httpPutJson(params) { response ->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) updTsVal("lastSpokeToAmazon")
            def resp = response?.data ?: null
            if(resp && !resp.errors?.size() && resp.controlResponses && resp.controlResponses[0] && resp.controlResponses[0].code && (String)resp.controlResponses[0].code == "SUCCESS") {
                logInfo("Alexa Guard set to (${guardState}) Successfully | (${(now()-execTime)}ms)")
                state.alexaGuardState = guardState
                updTsVal("lastGuardStateUpdDt")
                updGuardActionTrig()
            } else { logError("Failed to set Alexa Guard to (${guardState}) | Reason: ${resp?.errors ?: null}") }
        }
    } catch (ex) {
        respExceptionHandler(ex, "setGuardState", true)
    }
}

private getAlexaSkills() {
    Long execDt = now()
    if(!isAuthValid("getAlexaSkills") || !getCustomerData() /* state.amazonCustomerData */) { return }
    if(state.skillDataMap && getLastTsValSecs("skillDataUpdDt") < 3600) { return }
    Map params = [
        uri: "https://skills-store.${getAmazonDomain()}",
        path: "/app/secure/your-skills-page?deviceType=app&ref-suffix=evt_sv_ub&pfm=${state.amazonCustomerData?.marketPlaceId}&cor=US&lang=en-us&_=${now()}",
        headers: [
            Accept: "application/vnd+amazon.uitoolkit+json;ns=1;fl=0",
            Origin: getAmazonUrl()] + getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    try {
        logTrace("getAlexaSkills")
        httpGet(params) { response->
            if(response?.status != 200) logWarn("${response?.status} $params")
            if(response?.status == 200) updTsVal("lastSpokeToAmazon")
            def respData = response?.data ?: null
            log.debug "respData: $respData"
            // log.debug respData[3]?.contents[3]?.contents?.products

            // updTsVal("skillDataUpdDt")
        }
    } catch (ex) {
        log.error "getAlexaSkills Exception: ${ex}"
        // respExceptionHandler(ex, "getAlexaSkills", true)
    }
}

void respExceptionHandler(ex, String mName, Boolean ignOn401=false, Boolean toAmazon=true, Boolean ignNullMsg=false) {
    String toMsg = "Amazon"
    if(!toAmazon) { toMsg = "Echo Speaks Server" }
    String stackTr
    if(ex) {
        try {
            stackTr = getStackTrace(ex)
        } catch (e) {
        }
        if(stackTr) logError("${mName} | Stack Trace: "+stackTr)
    }
    if(ex instanceof groovyx.net.http.HttpResponseException ) {
        Integer sCode = ex?.getResponse()?.getStatus()
//        def rData = ex?.getResponse()?.getData()
        def errMsg = ex?.getMessage()
        if(sCode == 401) {
            if(ignOn401) authValidationEvent(false, "${mName}_${sCode}")
        } else if (sCode == 400) {
            switch(errMsg) {
                case "Bad Request":
                    logError("${mName} | Improperly formatted request sent to ${toMsg} | Msg: ${errMsg}")
                    break
                case "Rate Exceeded":
                    logError("${mName} | ${toMsg} is currently rate-limiting your requests | Msg: ${errMsg}")
                    break
                default:
                    logError("${mName} | 400 Error | Msg: ${errMsg}")
                    break
            }
        } else if(sCode == 429) {
            logWarn("${mName} | Too Many Requests Made to ${toMsg} | Msg: ${errMsg}")
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
    def acts = getActionApps()
    if(acts?.size()) { acts?.each { aa-> aa?.guardEventHandler((String)state.alexaGuardState) } }
}

public setGuardHome() {
    setGuardState(sARM_STAY)
}

public setGuardAway() {
    setGuardState(sARM_AWAY)
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
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
    ]
    state.deviceRefreshInProgress = true
//    state.refreshDeviceData = false
    logTrace("getEchoDevices")
    execAsyncCmd("get", "echoDevicesResponse", params, [execDt: now(), aws: true])
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
        if(response?.status == 200 && data?.aws) updTsVal("lastSpokeToAmazon")
        // log.debug "json response is: ${response.json}"
        state.deviceRefreshInProgress=false
        List eDevData = response?.json?.devices ?: []
        Map echoDevices = [:]
        if(eDevData.size()) {
            eDevData.each { eDevice->
                if (!((String)eDevice.deviceType in ignoreTypes) && !((String)eDevice.accountName)?.startsWith("This Device")) {
                    removeKeys.each { rk-> eDevice.remove(rk) }
                    eDevice.capabilities = eDevice.capabilities?.findAll { !(it in removeCaps) }?.collect { it as String }
                    if (eDevice.deviceOwnerCustomerId != null) { state.deviceOwnerCustomerId = eDevice.deviceOwnerCustomerId }
                    echoDevices[(String)eDevice.serialNumber] = eDevice
                }
            }
        }
        // log.debug "echoDevices: ${echoDevices}"
        Map musicProvs = (Map)state.musicProviders ?: getMusicProviders(true)
        receiveEventData([echoDevices: echoDevices, musicProviders: musicProvs, execDt: data?.execDt], "Groovy")
    } catch (ex) {
        respExceptionHandler(ex, "echoDevicesResponse")
    }
}

private static List<String> getDeviceIgnoreData() {
    Map<String,Map> dData = (Map<String,Map>)deviceSupportMapFLD.types
    if(dData.size()) {
        List o = dData.findAll { it.value.ignore == true }?.collect { it.key }
        // log.debug "devTypeIgnoreData: ${o}"
        return o
    }
    return []
}

List getUnknownDevices() {
    List items = []
    ((List<Map>)state.unknownDevices)?.each {
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
            if(!wsDevice) { addChildDevice("tonesto7", wsChildHandlerName, nmS, null, [name: wsChildHandlerName, label: "Echo Speaks - WebSocket", completedSetup: true]) }
            updCodeVerMap("echoDeviceWs", (String)wsDevice?.devVersion())

            if (evtData?.echoDevices?.size()) {
                Long execTime = evtData?.execDt ? (now()-(Long)evtData.execDt) : 0L
                Map<String, Map> echoDeviceMap = [:]
                Map<String, Map> allEchoDevices = [:]
                Map<String, Map> skippedDevices = [:]
                List unknownDevices = []
                List curDevFamily = []
//                Integer cnt = 0
                String devAcctId = (String)null
                evtData?.echoDevices?.each { String echoKey, echoValue->
                    devAcctId = echoValue?.deviceAccountId
                    logTrace("echoDevice | $echoKey | ${echoValue}")
                    // logDebug("echoDevice | ${echoValue?.accountName}", false)
                    allEchoDevices[echoKey] = [name: echoValue?.accountName]
                    // log.debug "name: ${echoValue?.accountName}"
                    Map familyAllowed = isFamilyAllowed(echoValue?.deviceFamily as String)
                    Map deviceStyleData = getDeviceStyle(echoValue?.deviceFamily as String, echoValue?.deviceType as String)
                    // log.debug "deviceStyle: ${deviceStyleData}"
                    Boolean isBlocked = (deviceStyleData?.blocked || familyAllowed?.reason == "Family Blocked")
                    Boolean isInIgnoreInput = (echoValue?.serialNumber in settings.echoDeviceFilter)
                    Boolean allowTTS = (deviceStyleData?.caps && deviceStyleData?.caps?.contains("t"))
                    Boolean isMediaPlayer = (echoValue?.capabilities?.contains("AUDIO_PLAYER") || echoValue?.capabilities?.contains("AMAZON_MUSIC") || echoValue?.capabilities?.contains("TUNE_IN") || echoValue?.capabilities?.contains("PANDORA") || echoValue?.capabilities?.contains("I_HEART_RADIO") || echoValue?.capabilities?.contains("SPOTIFY"))
                    Boolean volumeSupport = (echoValue?.capabilities?.contains("VOLUME_SETTING"))
                    Boolean unsupportedDevice = ((!familyAllowed?.ok && familyAllowed?.reason == "Unknown Reason") || isBlocked)
                    Boolean bypassBlock = ((Boolean)settings.bypassDeviceBlocks && !isInIgnoreInput)

                    if(!bypassBlock && (!familyAllowed?.ok || isBlocked || (!allowTTS && !isMediaPlayer) || isInIgnoreInput)) {
                        logTrace("familyAllowed(${echoValue?.deviceFamily}): ${familyAllowed?.ok} | Reason: ${familyAllowed?.reason} | isBlocked: ${isBlocked} | deviceType: ${echoValue?.deviceType} | tts: ${allowTTS} | volume: ${volumeSupport} | mediaPlayer: ${isMediaPlayer}")
                        if(!skippedDevices.containsKey(echoValue?.serialNumber as String)) {
                            List reasons = []
                            if(deviceStyleData?.blocked) {
                                reasons.push("Device Blocked by App Config")
                            } else if(familyAllowed?.reason == "Family Blocked") {
                                reasons.push("Family Blocked by App Config")
                            } else if (!familyAllowed?.ok) {
                                reasons.push(familyAllowed?.reason)
                            } else if(isInIgnoreInput) {
                                reasons.push(sIN_IGNORE)
                                logDebug("skipping ${echoValue?.accountName} because it is in the do not use list...")
                            } else {
                                if(!allowTTS) { reasons.push("No TTS") }
                                if(!isMediaPlayer) { reasons.push("No Media Controls") }
                            }
                            skippedDevices[echoValue?.serialNumber as String] = [
                                name: echoValue?.accountName, desc: deviceStyleData?.name, image: deviceStyleData?.image, family: echoValue?.deviceFamily,
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
                    echoValue["deviceAccountId"] = echoValue?.deviceAccountId as String ?: (String)null
                    echoValue["deviceStyle"] = deviceStyleData
                    // log.debug "deviceStyle: ${echoValue?.deviceStyle}"

                    Map<String, Object> permissions = [:]
                    permissions["TTS"] = allowTTS
                    permissions["announce"] = (deviceStyleData?.caps && deviceStyleData?.caps?.contains("a"))
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
                    echoValue["guardStatus"] = ((Boolean)state.alexaGuardSupported && (String)state.alexaGuardState) ? (String)state.alexaGuardState : ((Boolean)permissions.guardSupported ? "Unknown" : "Not Supported")
                    echoValue["musicProviders"] = (Map)evtData.musicProviders
                    echoValue["permissionMap"] = permissions
                    echoValue["hasClusterMembers"] = (echoValue.clusterMembers && echoValue.clusterMembers?.size() > 0) ?: false

                    if(deviceStyleData?.name?.toString()?.toLowerCase()?.contains("unknown")) {
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
                    curDevFamily.push(echoValue.deviceStyle?.name)
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
    Map codeItems = [server: "Echo Speaks Server", echoDevice: "Echo Speaks Device", wsDevice: "Echo Speaks Websocket", actionApp: "Echo Speaks Actions", zoneApp: "Echo Speaks Zones"]
    Map codeVers = (Map)state.codeVersions ?: [:]
    codeVers.each { String k,String v->
        if(codeItems.containsKey(k) && v != sNULL && (versionStr2Int(v) < minVersionsFLD[k])) { updRequired = true; updItems.push(codeItems[k]) }
    }
    return [updRequired: updRequired, updItems: updItems]
}

static Map getDeviceStyle(String family, String type) {
    Map typeData = deviceSupportMapFLD.types[type] ?: [:]
    if(typeData) {
        return typeData
    } else { return [name: "Echo Unknown $type", image: "unknown", allowTTS: false] }
}

public Map getDeviceFamilyMap() {
    if(!state.appData || !state.appData.deviceFamilies) { checkVersionData(true) }
    return state.appData?.deviceFamilies ?: deviceSupportMapFLD.families
}

List getDevicesFromSerialList(List serialList) {
    //logTrace("getDevicesFromSerialList called with: ${serialList}")
    if (serialList == null) {
       logDebug("SerialNumberList is null")
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

// This is called by the device handler to send playback data to cluster members
public void sendPlaybackStateToClusterMembers(String whaKey, data) {
    //logTrace("sendPlaybackStateToClusterMembers: key: ${ whaKey}")
    try {
        Map echoDeviceMap = getEchoDeviceMap() //state.echoDeviceMap
        Map whaMap = echoDeviceMap[whaKey]
        def clusterMembers = whaMap?.clusterMembers

        if (clusterMembers) {
            def clusterMemberDevices = getDevicesFromSerialList(clusterMembers)
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
        List<String> items = app.getChildDevices()?.findResults { (all || (!all && !devList?.contains(it?.deviceNetworkId as String))) ? it?.deviceNetworkId as String : sNULL }
        logWarn("removeDevices(${all ? "all" : sBLANK}) | In Use: (${all ? 0 : devList.size()}) | Removing: (${items.size()})", true)
        if(items.size() > 0) {
            items.each {  String it -> deleteChildDevice(it) }
        }
    } catch (ex) { logError("Device Removal Failed: ${ex}", false, ex) }
}

Map sequenceBuilder(cmd, val) {
    Map seqJson
    if (cmd instanceof Map) {
        seqJson = cmd?.sequence ?: cmd
    } else { seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": createSequenceNode(cmd, val)] }
    Map seqObj = [behaviorId: (seqJson?.sequenceId ? cmd?.automationId : "PREVIEW"), sequenceJson: new groovy.json.JsonOutput().toJson(seqJson), status: "ENABLED"]
    return seqObj
}

Map multiSequenceBuilder(commands, Boolean parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList.push(createSequenceNode((String)cmdItem?.command, cmdItem?.value, [serialNumber: cmdItem?.serial, deviceType:cmdItem?.type])) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    Map seqObj = sequenceBuilder(seqJson, null)
    return seqObj
}

Map createSequenceNode(String command, value, Map deviceData = [:]) {
    try {
        Boolean remDevSpecifics = false
        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            operationPayload: [
                deviceType: deviceData?.deviceType ?: null,
                deviceSerialNumber: deviceData?.serialNumber ?: null,
                locale: (settings.regionLocale ?: "en-US"),
                customerId: state.deviceOwnerCustomerId
            ]
        ]
        switch (command) {
            case "volume":
                seqNode.type = "Alexa.DeviceControls.Volume"
                seqNode.operationPayload.value = value
                break
            case "speak":
                seqNode.type = "Alexa.Speak"
                seqNode.operationPayload.textToSpeak = value as String
                break
            case "announcementTest":
                seqNode.type = "AlexaAnnouncement"
                seqNode.operationPayload.remove('deviceType')
                seqNode.operationPayload.remove('deviceSerialNumber')
                seqNode.operationPayload.remove('locale')
                seqNode.operationPayload.expireAfter = "PT5S"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : ["Echo Speaks", value as String]
                seqNode.operationPayload.content = [[
                    locale: (state.regionLocale ?: "en-US"),
                    display: [ title: valObj[0], body: valObj[1] as String ],
                    speak: [ type: "text", value: valObj[1] as String ],
                ]]
                List announceDevs = []
                if(settings.test_announceDevices) {
                    Map eDevs = getEchoDeviceMap() //state.echoDeviceMap
                    settings.test_announceDevices.each { String dev->
                        announceDevs.push([deviceTypeId: eDevs[dev]?.type, deviceSerialNumber: dev])
                    }
                }
                seqNode.operationPayload.target = [ customerId : state.deviceOwnerCustomerId, devices: announceDevs ]
                break
            default:
                return
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

void execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
    if(method && callbackHandler && params) {
        String m = method?.toString()?.toLowerCase()
        "asynchttp${m?.capitalize()}"("${callbackHandler}", params, otherData)
    } else { logError("execAsyncCmd Error | Missing a required parameter") }
}

void sendAmazonCommand(String method, Map params, Map otherData=null) {
    try {
        def rData = null
        def rStatus = null
        switch(method) {
            case "POST":
                httpPostJson(params) { response->
                    if(response?.status != 200) logWarn("${response?.status} $params")
                    if(response?.status == 200) updTsVal("lastSpokeToAmazon")
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
            case "PUT":
                if(params?.body) { params?.body = new groovy.json.JsonOutput().toJson(params?.body) }
                httpPutJson(params) { response->
                    if(response?.status != 200) logWarn("${response?.status} $params")
                    if(response?.status == 200) updTsVal("lastSpokeToAmazon")
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
            case "DELETE":
                httpDelete(params) { response->
                    if(response?.status != 200) logWarn("${response?.status} $params")
                    if(response?.status == 200) updTsVal("lastSpokeToAmazon")
                    rData = response?.data ?: null
                    rStatus = response?.status
                }
                break
        }
        logDebug("sendAmazonCommand | Status: (${rStatus})${rData != null ? " | Response: ${rData}" : sBLANK} | ${otherData?.cmdDesc} was Successfully Sent!!!")
    } catch (ex) {
        respExceptionHandler(ex, "${otherData?.cmdDesc}", true)
    }
}

void sendSequenceCommand(String type, Map command, value) {
    // logTrace("sendSequenceCommand($type) | command: $command | value: $value", true)
    Map seqObj = sequenceBuilder(command, value)
    sendAmazonCommand("POST", [
        uri: getAmazonUrl(),
        path: "/api/behaviors/preview",
        headers: getCookieMap(),
        contentType: sAPPJSON,
        timeout: 20,
        body: new groovy.json.JsonOutput().toJson(seqObj)
    ], [cmdDesc: "SequenceCommand (${type})"])
}

private sendMultiSequenceCommand(commands, String srcDesc, Boolean parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList.push(createSequenceNode(cmdItem?.command, cmdItem?.value, [serialNumber: cmdItem?.serial, deviceType: cmdItem?.type])) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    sendSequenceCommand("${srcDesc} | MultiSequence: ${parallel ? "Parallel" : "Sequential"}", seqJson, null)
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

    restartSocket()

    if((Boolean)state.isInstalled && getLastTsValSecs("lastMetricUpdDt") > (3600*24)) { runIn(30, "sendInstallData", [overwrite: true]) }
    if(advLogsActive()) { logsDisable() }
    appUpdateNotify()

    //if(!getOk2Notify()) { return }
    missPollNotify((Boolean)settings.sendMissedPollMsg, (settings.misPollNotifyMsgWaitVal as Integer ?: 3600))
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

void missPollNotify(Boolean on, Integer wait) {
    Integer lastDataUpd = getLastTsValSecs("lastDevDataUpdDt")
    Integer lastMissPollM = getLastTsValSecs("lastMissedPollMsgDt")
    logTrace("missPollNotify() | on: ($on) | wait: ($wait) | getLastDevicePollSec: (${lastDataUpd}) | misPollNotifyWaitVal: (${settings.misPollNotifyWaitVal}) | getLastMisPollMsgSec: (${lastMissPollM})")
    if(lastDataUpd <= ((settings.misPollNotifyWaitVal as Integer ?: 2700)+10800)) {
        state.missPollRepair = false
        return
    } else {
        if(!(Boolean)state.missPollRepair) {
            state.missPollRepair = true
            initialize()
            return
        }
        if(!(lastMissPollM > wait?.toInteger())) { on = false }
        String msg = sBLANK
        if((Boolean)state.authValid) {
            msg = "\nThe Echo Speaks app has NOT received any device data from Amazon in the last (${getLastTsValSecs("lastDevDataUpdDt")}) seconds.\nThere maybe an issue with the scheduling.  Please open the app and press Done/Save."
        } else { msg = "\nThe Amazon login info has expired!\nPlease open the heroku amazon authentication page and login again to restore normal operation." }
        logWarn("${msg.toString().replaceAll("\n", " ")}")
        if(on && sendMsg("${app.name} ${(Boolean)state.authValid ? "Data Refresh Issue" : "Amazon Login Issue"}", msg)) {
            updTsVal("lastMissedPollMsgDt")
        }
/*        if((Boolean)state.authValid) {
            (getChildDevices())?.each { cd-> cd?.sendEvent(name: "DeviceWatch-DeviceStatus", value: "offline", displayed: true, isStateChange: true) }
        } */
    }
}

void appUpdateNotify() {
    Boolean appUpd = appUpdAvail()
    Boolean actUpd = actionUpdAvail()
    Boolean zoneUpd = zoneUpdAvail()
    Boolean echoDevUpd = echoDevUpdAvail()
    Boolean socketUpd = socketUpdAvail()
    Boolean servUpd = serverUpdAvail()
    Boolean res=false
    if(appUpd || actUpd || zoneUpd || echoDevUpd || socketUpd || servUpd) res=true

    Integer secs
    Integer updW
    Boolean on=false
    if(res) {
        on = ((Boolean)settings.sendAppUpdateMsg)
        updW = settings.updNotifyWaitVal
        if(updW == null) { updW = 43200; settingUpdate("updNotifyWaitVal", 43200) }
        secs=getLastTsValSecs("lastUpdMsgDt")
        if(secs > updW && on) {
            String str = sBLANK
            str += !appUpd ? "" : "\nEcho Speaks App: v${state.appData?.versions?.mainApp?.ver?.toString()}"
            str += !actUpd ? "" : "\nEcho Speaks Actions: v${state.appData?.versions?.actionApp?.ver?.toString()}"
            str += !zoneUpd ? "" : "\nEcho Speaks Zones: v${state.appData?.versions?.zoneApp?.ver?.toString()}"
            str += !echoDevUpd ? "" : "\nEcho Speaks Device: v${state.appData?.versions?.echoDevice?.ver?.toString()}"
            str += !socketUpd ? "" : "\nEcho Speaks Socket: v${state.appData?.versions?.wsDevice?.ver?.toString()}"
            str += !servUpd ? "" : "\n${((Boolean)getServerItem("onHeroku") == true) ? "Heroku Service" : "Node Service"}: v${state.appData?.versions?.server?.ver?.toString()}"
            sendMsg("Info", "Echo Speaks Update(s) are Available:${str}...\n\nPlease visit the IDE to Update your code...")
            updTsVal("lastUpdMsgDt")
        }
    }
    state.updateAvailable = res
    String msg="appUpdateNotify() RESULT: ${res} | on: (${on}) | appUpd: (${appUpd}) | actUpd: (${appUpd}) | zoneUpd: (${zoneUpd}) | echoDevUpd: (${echoDevUpd}) | servUpd: (${servUpd}) | getLastUpdMsgSec: ${secs} | updNotifyWaitVal: ${updW}"
    if(res) logDebug(msg)
    else logTrace(msg)
}

private List codeUpdateItems(Boolean shrt=false) {
    Boolean appUpd = appUpdAvail()
    Boolean actUpd = actionUpdAvail()
    Boolean zoneUpd = zoneUpdAvail()
    Boolean devUpd = echoDevUpdAvail()
    Boolean socketUpd = socketUpdAvail()
    Boolean servUpd = serverUpdAvail()
    List updItems = []
    if(appUpd || actUpd || zoneUpd || devUpd || socketUpd || servUpd) {
        if(appUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}App: (v${state.appData?.versions?.mainApp?.ver?.toString()})")
        if(actUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Actions: (v${state.appData?.versions?.actionApp?.ver?.toString()})")
        if(zoneUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Zones: (v${state.appData?.versions?.zoneApp?.ver?.toString()})")
        if(devUpd) updItems.push("${!shrt ? "\nEcho Speaks " : "ES "}Device: (v${state.appData?.versions?.echoDevice?.ver?.toString()})")
        if(socketUpd) updItems.push("${!shrt ? "\nEcho Speaks " : sBLANK}Websocket: (v${state.appData?.versions?.wsDevice?.ver?.toString()})")
        if(servUpd) updItems.push("${!shrt ? "\n" : sBLANK}Server: (v${state.appData?.versions?.server?.ver?.toString()})")
    }
    return updItems
}

//TODO REMOVE
//Boolean pushStatus() { return (settings.smsNumbers?.toString()?.length()>=10 || (Boolean)settings.usePush || (Boolean)settings.pushoverEnabled) ? (((Boolean)settings.usePush || ((Boolean)settings.pushoverEnabled && settings.pushoverDevices)) ? "Push Enabled" : "Enabled") : null }
//Boolean pushStatus() { return (settings.smsNumbers?.toString()?.length()>=10 || (Boolean)settings.usePush || (Boolean)settings.pushoverEnabled) ? (((Boolean)settings.usePush || ((Boolean)settings.pushoverEnabled && settings.pushoverDevices)) ? true : true ) : false }

Boolean getOk2Notify() {
    Boolean smsOk // (settings.smsNumbers?.toString()?.length()>=10)
    Boolean pushOk // (Boolean)settings.usePush
    Boolean notifDevs = (settings.notif_devs?.size())
    Boolean pushOver // ((Boolean)settings.pushoverEnabled && settings.pushoverDevices)
    Boolean daysOk = quietDaysOk(settings.quietDays)
    Boolean timeOk = quietTimeOk()
    Boolean modesOk = quietModesOk(settings.quietModes)
    Boolean result = true
    if(!(smsOk || pushOk || notifDevs || pushOver)) { result= false }
    if(!(daysOk && modesOk && timeOk)) { result= false }
    logDebug("getOk2Notify() RESULT: $result | notifDevs: $notifDevs | smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
    return result
}

Boolean quietModesOk(List modes) { return !(modes && location?.mode?.toString() in modes) }

Boolean quietTimeOk() {
    Date startTime = null
    Date stopTime = null
//    Date now = new Date()
    def sun = getSunriseAndSunset() // current based on geofence, previously was: def sun = getSunriseAndSunset(zipCode: zipCode)
    if(settings.qStartTime && settings.qStopTime) {
        if(settings.qStartInput == "Sunset") { startTime = sun?.sunset }
        else if(settings.qStartInput == "Sunrise") { startTime = sun?.sunrise }
        else if(settings.qStartInput == "A specific time" && settings.qStartTime) { startTime = toDateTime(settings.qStartTime) }

        if(settings.qStopInput == "Sunset") { stopTime = sun?.sunset }
        else if(settings.qStopInput == "Sunrise") { stopTime = sun?.sunrise }
        else if(settings.qStopInput == "A specific time" && settings.qStopTime) { stopTime = toDateTime(settings.qStopTime) }
    } else { return true }
    if(startTime && stopTime) {
        // log.debug "quietTimeOk | Start: ${startTime} | Stop: ${stopTime}"
        Date now = new Date()
        Boolean not = startTime.getTime() > stopTime.getTime() 
        Boolean isBtwn = timeOfDayIsBetween((not ? stopTime : startTime), (not ? startTime : stopTime), now, location?.timeZone) ? false : true
        isBtwn = not ? !isBtwn : isBtwn
        logTrace("QuietTimeOk ${isBtwn} | CurTime: (${now}) is${!isBtwn ? " NOT" : sBLANK} between (${not ? stopTime:startTime} and ${not ? startTime:stopTime})")
        return isBtwn
    } else { return true }
}

Boolean quietDaysOk(List days) {
    if(days) {
        def dayFmt = new java.text.SimpleDateFormat("EEEE")
        if(location?.timeZone) { dayFmt?.setTimeZone(location?.timeZone) }
        return !days.contains(dayFmt?.format(new Date()))
    }
    return true
}

// Sends the notifications based on app settings
public Boolean sendMsg(String msgTitle, String msg, Boolean showEvt=true, Map pushoverMap=null, sms=null, push=null) {
    logTrace("sendMsg() | msgTitle: ${msgTitle}, msg: ${msg}, showEvt: ${showEvt}")
    String sentstr = sBLANK
    Boolean sent = false
    try {
        String newMsg = msgTitle+": "+msg
        String flatMsg = newMsg.replaceAll("\n", " ")
        if(!getOk2Notify()) {
            logInfo("sendMsg: Message Skipped Notification not configured or During Quiet Time ($flatMsg)")
//            if(showEvt) { sendNotificationEvent(newMsg) }
        } else {
            // if(push || (Boolean)settings.usePush) {
            //     sentstr = "Push Message"
            //     if(showEvt) {
            //         sendPush(newMsg)	// sends push and notification feed
            //     } else {
            //         sendPushMessage(newMsg)	// sends push
            //     }
            //     sent = true
            // }
            if(settings.notif_devs) {
                sentstr = "Notification Devices"
                settings.notif_devs?.each { it?.deviceNotification(newMsg) }
                sent = true
            }
/*
            String smsPhones = sms ? sms.toString() : (settings.smsNumbers?.toString() ?: null)
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
            } */
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

static String getHEAppImg(String imgName) { return getAppImg(imgName, true) }
static String getAppImg(String imgName, Boolean frc=false) { return (frc) ? "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/icons/${imgName}.png" : sBLANK}

static String getHEPublicImg(String imgName) { return getPublicImg(imgName, true) }
static String getPublicImg(String imgName, Boolean frc=false) { return (frc) ? "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" : sBLANK}

String sTS(String t, String i = sNULL, Boolean bold=false) { return """<h3>${i ? """<img src="${i}" width="42"> """ : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>""" }
/* """ */

String s3TS(String t, String st, String i = sNULL, String c="#1A77C9") { return """<h3 style="color:${c};font-weight: bold">${i ? """<img src="${i}" width="42"> """ : sBLANK} ${t?.replaceAll("\\n", "<br>")}</h3>${st ? "${st}" : sBLANK}""" }
/* """ */

static String sectTS(String t, String i = sNULL, Boolean bold=false) { return """<h3>${i ? """<img src="${i}" width="48"> """ : sBLANK} ${bold ? "<b>" : sBLANK}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : sBLANK}</h3>""" }

static String sectH3TS(String t, String st, String i = sNULL, String c="#1A77C9") { return """<h3 style="color:${c};font-weight: bold">${i ? """<img src="${i}" width="48"> """ : sBLANK} ${t?.replaceAll("\\n", "<br>")}</h3>${st ?: sBLANK}""" }

static String pTS(String t, String i = sNULL, Boolean bold=true, String color=sNULL) { return "${color ? """<div style="color: $color;">""" : sBLANK}${bold ? "<b>" : sBLANK}${i ? """<img src="${i}" width="42"> """ : sBLANK}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : sBLANK}${color ? "</div>" : sBLANK}" }
/* """ */

static String inTS1(String t, String i = sNULL, String color=sNULL, Boolean under=true) { return inTS(t, getHEAppImg(i), color, under) }
static String inTS(String t, String i = sNULL, String color=sNULL, Boolean under=true) { return """${color ? """<div style="color: $color;">""" : sBLANK}${i ? """<img src="${i}" width="42"> """ : sBLANK} ${under ? "<u>" : sBLANK}${t?.replaceAll("\\n", " ")}${under ? "</u>" : sBLANK}${color ? "</div>" : sBLANK}""" }
/* """ */

static String htmlLine(String color="#1A77C9") { return "<hr style='background-color:${color}; height: 1px; border: 0;'>" }

def appFooter() {
    section() {
        paragraph htmlLine()
        paragraph """<div style='color:#1A77C9;text-align:center'>Echo Speaks<br><a href='${textDonateLink()}' target="_blank"><img width="120" height="120" src="https://raw.githubusercontent.com/tonesto7/homebridge-hubitat-tonesto7/master/images/donation_qr.png"></a><br><br>Please consider donating if you find this integration useful.</div>"""
    }       
}

static String actChildName(){ return "Echo Speaks - Actions" }
static String zoneChildName(){ return "Echo Speaks - Zones" }
static String documentationLink() { return "https://tonesto7.github.io/echo-speaks-docs" }
static String textDonateLink() { return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HWBN4LB9NMHZ4" }
def updateDocsInput() { href url: documentationLink(), style: sEXTNRL, required: false, title: inTS1("View Documentation", "documentation"), description: sTTP, state: sCOMPLT}

String getAppEndpointUrl(subPath)   { return "${getApiServerUrl()}/${getHubUID()}/apps/${app?.id}${subPath ? "/${subPath}" : sBLANK}?access_token=${state.accessToken}".toString() }

String getLocalEndpointUrl(subPath) { return "${getLocalApiServerUrl()}/apps/${app?.id}${subPath ? "/${subPath}" : sBLANK}?access_token=${state.accessToken}" }

/******************************************
|       Changelog Logic
******************************************/
Boolean showDonationOk() { return ((Boolean)state.isInstalled && !(Boolean)getInstData('shownDonation') && getDaysSinceUpdated() >= 30)
}

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
    String txt = (String) getWebData([uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/CHANGELOG.md", contentType: "text/plain; charset=UTF-8", timeout: 20], "changelog", true)
    txt = txt?.toString()?.replaceAll(/(\#\#\#\s)/, sBLANK)?.replaceAll(/(_\*\*)/, '<h5 style="font-size: 1.0em; font-weight: bold;">')?.replaceAll(/(\*\*\_)/, "</h5>") // Replaces header format
    txt = txt?.toString()?.replaceAll(/(\#\#\s)/, sBLANK)?.replaceAll(/(_\*\*)/, '<h3 style="color: red; font-size: 1.3em; font-weight: bolder;">')?.replaceAll(/(\*\*\_)/, "</h3>") // Replaces header format
    // txt = txt?.toString()?.replaceAll("#", sBLANK)?.replaceAll(/(_\*\*)/, "<p style='font-size: 1.5em; font-weight: bolder; color:#1A77C9;'>")?.replaceAll(/(\*\*\_)/, "</p>") // Replaces header format
    txt = txt?.toString()?.replaceAll(/(- )/, "   ${sBULLET} ")
    txt = txt?.toString()?.replaceAll(/(\[NEW\])/, "<u>[NEW]</u>")
    txt = txt?.toString()?.replaceAll(/(\[UPDATE\])/, "<u>[FIX]</u>")
    txt = txt?.toString()?.replaceAll(/(\[FIX\])/, "<u>[FIX]</u>")
    txt += "<hr>"
    // log.debug "txt: $txt"
    return txt?.toString() // Replaces ## then **_ and _** in changelog data
}
Boolean showChgLogOk() { return ((Boolean) state.isInstalled && !((String) state.curAppVer == appVersionFLD && (Boolean) getInstData('shownChgLog')) ) }

def changeLogPage() {
    return dynamicPage(name: "changeLogPage", title: sBLANK, nextPage: "mainPage", install: false) {
        section(sectTS("Release Notes:", getAppImg("change_log", true), true)) { paragraph changeLogData() }
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
private generateGuid() { if(!(String)state.appGuid) { state.appGuid = UUID?.randomUUID().toString() } }
void sendInstallData() { settingUpdate("sendMetricsNow", sFALSE, sBOOL); if(metricsOk()) { Boolean aa=sendFirebaseData(getFbMetricsUrl(), "/clients/${(String)state.appGuid}.json", createMetricsDataJson(), "put", "heartbeat") } }
Boolean removeInstallData() { return removeFirebaseData("/clients/${(String)state.appGuid}.json") }
Boolean sendFirebaseData(String url, String path, String data, String cmdType=null, String type=null) { logTrace("sendFirebaseData(${path}, ${data}, $cmdType, $type"); return queueFirebaseData(url, path, data, cmdType, type) }

Boolean queueFirebaseData(String url, String path, String data, String cmdType=sNULL, String type=sNULL) {
    logTrace("queueFirebaseData(${path}, ${data}, $cmdType, $type")
    Boolean result = false
    String json = new groovy.json.JsonOutput().prettyPrint(data)
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
        String json = new groovy.json.JsonOutput().prettyPrint(createMetricsDataJson())
        render contentType: sAPPJSON, data: json, status: 200
    } catch (ex) { logError("renderMetricData Exception: ${ex}", false, ex) }
}

private Map getSkippedDevsAnon() {
    Map res = [:]
    Map sDevs = state.skippedDevices ?: [:]
    sDevs?.each { k, v-> if(!res?.containsKey(v?.type)) { res[v?.type] = v } }
    return res
}

String createMetricsDataJson() {
    try {
        generateGuid()
        Map swVer = (Map)state.codeVersions
        Map deviceUsageMap = [:]
        Map deviceErrorMap = [:]
        getChildDevices()?.each { d->
            Map obj = d?.getDeviceMetrics()
            if(obj?.usage?.size()) { obj?.usage?.each { k,v-> deviceUsageMap[k as String] = (deviceUsageMap[k as String] ? deviceUsageMap[k as String] + v : v) } }
            if(obj?.errors?.size()) { obj?.errors?.each { k,v-> deviceErrorMap[k as String] = (deviceErrorMap[k as String] ? deviceErrorMap[k as String] + v : v) } }
        }
        Map actData = [:]
        def actCnt = 0
        getActionApps()?.each { a-> actData[actCnt] = a?.getActionMetrics(); actCnt++ }
        Map zoneData = [:]
        def zoneCnt = 0
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
            versions: [app: appVersionFLD, server: swVer?.server ?: "N/A", actions: swVer?.actionApp ?: "N/A", zones: swVer?.zoneApp ?: "N/A", device: swVer?.echoDevice ?: "N/A", socket: swVer?.wsDevice ?: "N/A"],
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
        String json = new groovy.json.JsonOutput().toJson(dataObj)
        return json
    } catch (ex) {
        logError("createMetricsDataJson: Exception: ${ex}", false, ex)
    }
}

void incrementCntByKey(String key) {
    Long evtCnt = (Long)state."${key}"
    evtCnt = evtCnt != null ? evtCnt : 0L
    // evtCnt = evtCnt?.toLong()+1
    evtCnt++
    // logTrace("${key?.toString()?.capitalize()}: $evtCnt", true)
    state."${key}" = evtCnt
}

// ******************************************
//      APP/DEVICE Version Functions
// ******************************************
Boolean codeUpdIsAvail(String newVer, String curVer, String type) {
    Boolean result = false
    def latestVer
    if(newVer && curVer) {
        List versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b ->
                List verA = a?.tokenize('.'); List verB = b?.tokenize('.'); Integer commonIndices = Math.min(verA?.size(), verB?.size())
                for (int i = 0; i < commonIndices; ++i) { if(verA[i]?.toInteger() != verB[i]?.toInteger()) { return verA[i]?.toInteger() <=> verB[i]?.toInteger() } }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer)
        }
    }
    return result
}

Boolean appUpdAvail()       { return (state.appData?.versions && state.codeVersions?.mainApp && codeUpdIsAvail(state.appData?.versions?.mainApp?.ver, state.codeVersions?.mainApp, "main_app")) }
Boolean actionUpdAvail()    { return (state.appData?.versions && state.codeVersions?.actionApp && codeUpdIsAvail(state.appData?.versions?.actionApp?.ver, state.codeVersions?.actionApp, "action_app")) }
Boolean zoneUpdAvail()      { return (state.appData?.versions && state.codeVersions?.zoneApp && codeUpdIsAvail(state.appData?.versions?.zoneApp?.ver, state.codeVersions?.zoneApp, "zone_app")) }
Boolean echoDevUpdAvail()   { return (state.appData?.versions && state.codeVersions?.echoDevice && codeUpdIsAvail(state.appData?.versions?.echoDevice?.ver, state.codeVersions?.echoDevice, "dev")) }
Boolean socketUpdAvail()    { return (state.appData?.versions && state.codeVersions?.wsDevice && codeUpdIsAvail(state.appData?.versions?.wsDevice?.ver, state.codeVersions?.wsDevice, "socket")) }
Boolean serverUpdAvail()    { return (state.appData?.versions && state.codeVersions?.server && codeUpdIsAvail(state.appData?.versions?.server?.ver, state.codeVersions?.server, "server")) }
Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }

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
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/appData.json",
        contentType: sAPPJSON,
        timeout: 20,
    ]
    Map data = getWebData(params, "appData", false)
    if(data) {
        state.appData = data
        updTsVal("lastAppDataUpdDt")
        logDebug("Successfully Retrieved (v${data?.appDataVer}) of AppData Content from GitHub Repo...")
    }
}

void getNoticeData() {
    Map params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/notices.json",
        contentType: sAPPJSON,
        timeout: 20,
    ]
    Map data = getWebData(params, "noticeData", false)
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
                if(text) { return resp.data?.text.toString() }
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


Map getAvailableSounds() {
    return getAvailableSoundsFLD
}

// TODO: https://m.media-amazon.com/images/G/01/mobile-apps/dex/ask-tech-docs/ask-soundlibrary._TTH_.json
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

//ERS
//@Field volatile static Map<String,Map> echoDeviceMapFLD       = [:]
//@Field volatile static Map<String,Map> devActivityMapFLD = [:]

private getDiagDataJson(Boolean asObj = false) {
    try {
        String myId=app.getId()
        updChildVers()
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
                count: echoDevs?.size() ?: 0,
                lastDataUpdDt: getTsVal("lastDevDataUpdDt"),
                models: (Map)state.deviceStyleCnts ?: [:],
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
                firmware: location?.hubs[0]?.getFirmwareVersionString() ?: null,
                type: location?.hubs[0]?.getType() ?: null
            ],
            authStatus: [
                cookieValidationState: (Boolean)state.authValid,
                cookieValidDate: getTsVal("lastCookieChkDt") ?: null,
                cookieValidDur: getTsVal("lastCookieChkDt") ? seconds2Duration(getLastTsValSecs("lastCookieChkDt")) : null,
                cookieValidHistory: (List)state.authValidHistory,
                cookieLastRefreshDate: getTsVal("lastCookieRrshDt") ?: null,
                cookieLastRefreshDur: getTsVal("lastCookieRrshDt") ? seconds2Duration(getLastTsValSecs("lastCookieRrshDt")) : null,
                cookieInvalidReason: (!(Boolean)state.authValid && state.authEvtClearReason) ? state.authEvtClearReason : (String)null,
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
                dataSrc: (String)state.guardDataSrc,
                lastSupportCheck: getTsVal("lastGuardSupChkDt"),
                lastStateCheck: getTsVal("lastGuardStateChkDt"),
                lastStateUpd: getTsVal("lastGuardStateUpdDt"),
                stRespLimit: (Boolean)state.guardDataOverMaxSize
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
        String json = new groovy.json.JsonOutput().toJson(output)
        if(asObj) {
            return json
        }
        render contentType: sAPPJSON, data: json, status: 200
    } catch (ex) {
        logError("getDiagData: Exception: ${ex}", false, ex)
        if(asObj) { return null }
        render contentType: sAPPJSON, data: [status: "failed", error: ex], status: 500
    }
}

private getDiagDataText() {
    String jsonIn = getDiagDataJson(true)
    if(jsonIn) {
        String o = new groovy.json.JsonOutput().prettyPrint(createMetricsDataJson())
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
    String json = new groovy.json.JsonOutput().toJson([message: (status ? "ok" : "failed"), command: dcmd, version: appVersionFLD])
    render contentType: sAPPJSON, data: json, status: 200
}


/******************************************
|    Time and Date Conversion Functions
*******************************************/
String formatDt(Date dt, Boolean tzChg=true) {
    def tf = new java.text.SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone(location?.timeZone) } }
    return (String)tf.format(dt)
}

String strCapitalize(String str) { return str ? str?.toString().capitalize() : sNULL }
String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }
String pluralize(Integer itemVal, String str) { return (itemVal > 1) ? str+"s" : str }

String parseDt(String pFormat, String dt, Boolean tzFmt=true) {
    Date newDt = Date.parse(pFormat, dt)
    return formatDt(newDt, tzFmt)
}

String parseFmtDt(String parseFmt, String newFmt, dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location.timeZone) { tf.setTimeZone(location?.timeZone) }
    return (String)tf.format(newDt)
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now)
}

String epochToTime(Date tm) {
    def tf = new java.text.SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    return (String)tf.format(tm)
}

String time2Str(time) {
    if(time) {
        Date t = timeToday(time as String, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f.setTimeZone(location?.timeZone ?: timeZone(time))
        return (String)f.format(t)
    }
}

Long GetTimeDiffSeconds(String lastDate, String sender=sNULL) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        Date lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        Long start = lastDt.getTime()
        Long stop = now()
        Long diff = (stop - start) / 1000L
        return diff.abs()
    } catch (ex) {
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : sBLANK}lastDate: $lastDate): ${ex}", false, ex)
        return 10000L
    }
}

String seconds2Duration(Integer timeSec, Boolean postfix=true, Integer tk=2 /*, Boolean asMap=false */) {
    Integer years = Math.floor(timeSec / 31536000); timeSec -= years * 31536000;
    Integer months = Math.floor(timeSec / 31536000); timeSec -= months * 2592000;
    Integer days = Math.floor(timeSec / 86400); timeSec -= days * 86400;
    Integer hours = Math.floor(timeSec / 3600); timeSec -= hours * 3600;
    Integer minutes = Math.floor(timeSec / 60); timeSec -= minutes * 60;
    Integer seconds = Integer.parseInt((timeSec % 60) as String, 10);
    Map d = [y: years, mn: months, d: days, h: hours, m: minutes, s: seconds]
//    if(asMap) { return d }
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
//    Date now = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(Date.parse("E MMM dd HH:mm:ss z yyyy", getDtNow())))
    Date lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(Date.parse("E MMM dd HH:mm:ss z yyyy", lastCookieRfsh)))
    Date nextDt = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt + days))
    Integer diff = ( ((Long)nextDt.getTime() - now()) / 1000) as Integer
    String dur = seconds2Duration(diff, false, 3)
    // log.debug "now: ${now} | lastDt: ${lastDt} | nextDt: ${nextDt} | Days: $days | Wait: $diff | Dur: ${dur}"
    return dur
}
List weekDaysEnum() {
    return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
}

List monthEnum() {
    return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
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

@Field volatile static Map<String,Map> tsDtMapFLD=[:]

private void updTsVal(String key, String dt=sNULL) {
    String val = dt ?: getDtNow()
    if(key == "lastCookieRrshDt") { updServerItem(key, val); return }

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
                key.each { String k->
                    if(data?.containsKey(k)) { data?.remove(k) }
                    if(k == "lastCookieRrshDt") { remServerItem(k) }
                }
        } else {
            if(data?.containsKey((String)key)) { data?.remove((String)key) }
            if((String)key == "lastCookieRrshDt") { remServerItem((String)key) }
        }
        tsDtMapFLD[appId]=data
        tsDtMapFLD=tsDtMapFLD
    }
}

private String getTsVal(String key) {
    if(key == "lastCookieRrshDt") {
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
            key?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
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
            key?.each { String k-> if(data.containsKey(k)) { data.remove(k) } }
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
    tsItems?.each { String k, String v-> if(state.containsKey(k)) { updTsVal(v, state[k]); state.remove(k); } }

    //App Flag Migrations
    Map flagItems = [:]
    flagItems?.each { String k, String v-> if(state.containsKey(k)) { updAppFlag(v, state[k]); state.remove(k); } }

    //Server Data Migrations
    Map servItems = ["onHeroku":"onHeroku", "serverHost":"serverHost", "isLocal":"isLocal", "lastCookieRefresh":"lastCookieRrshDt" ]
    servItems?.each { String k, String v-> if(state.containsKey(k)) { updServerItem(v, state[k]); state.remove(k); } }
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
    Map cv = state.codeVersions
    cv = cv ?: [:]
    List ri = ["groupApp"]
    cv.each { String k, String v-> if(v == null) ri.push(k) }
    ri.each { cv.remove(it) }
    state.codeVersions = cv
}

String getRandAppName() {
    if(!(String)state.herokuName && (!(Boolean)getServerItem("isLocal") && !(String)getServerItem("serverHost"))) { state.herokuName = "${app?.name?.toString().replaceAll(" ", "-")}-${randomString(8)}"?.toLowerCase() }
    return (String)state.herokuName
}

/******************************************
|   App Input Description Functions
*******************************************/
String getAppNotifConfDesc() {
    String str = sBLANK
    Integer notifDevs = settings.notif_devs?.size()
    if(notifDevs) { //pushStatus()) {
        Boolean ok = getOk2Notify()
        str += "Send Notifications Allowed: (${ok ? okSymFLD : notOkSymFLD})"
        String ap = getAppNotifDesc()
        String nd = getNotifSchedDesc(true)
        str += notifDevs ? bulletItem(str, "Sending via: Notification Device${pluralizeStr(settings.notif_devs)} (${notifDevs})") : sBLANK
        str += (ap) ? "${str != sBLANK ? "\n\n" : sBLANK}Enabled Alerts:\n${ap}" : sBLANK
        str += (ap && nd) ? "${str != sBLANK ? "\n" : sBLANK}\n${nd}" : sBLANK
    }
    return str != sBLANK ? str : sNULL
}

List getQuietDays() {
    List allDays = weekDaysEnum()
    List curDays = settings.quietDays ?: []
    return allDays?.findAll { (!curDays?.contains(it as String)) }
}

String getNotifSchedDesc(Boolean min=false) {
    String startType = settings.qStartInput
    Date startTime
    String stopType = settings.qStopInput
    Date stopTime
    List dayInput = settings.quietDays
    List modeInput = settings.quietModes
    String str = sBLANK

    if(startType && stopType) {
        startTime = startType == 'A specific time' && settings.qStartTime ? toDateTime(settings.qStartTime) : null
        stopTime = stopType == 'A specific time' && settings.qStopTime ? toDateTime(settings.qStopTime) : null
    }
    if(startType in ["Sunrise","Sunset"] || stopType in ["Sunrise","Sunset"]) {
        def sun = getSunriseAndSunset()
        Long lsunset = sun.sunset.time
        Long lsunrise = sun.sunrise.time
        Long startoffset = settings.notif_time_start_offset ? settings.notif_time_start_offset*1000L : 0L
        Long stopoffset = settings.notif_time_stop_offset ? settings.notif_time_stop_offset*1000L : 0L
        if(startType in ["Sunrise","Sunset"]) {
            Long startl = (startType == 'Sunrise' ? lsunrise : lsunset) + startoffset
            startTime = new Date(startl)
        }
        if(stopType in ["Sunrise","Sunset"]) {
            Long stopl = (stopType == 'Sunrise' ? lsunrise : lsunset) + stopoffset
            stopTime = new Date(stopl)
        }
    }
    Boolean timeOk = quietTimeOk()
    Boolean daysOk = quietDaysOk(dayInput)
    Boolean modesOk = quietModesOk(modeInput)
    Boolean rest = !(daysOk && modesOk && timeOk)
    String startLbl = startTime ? epochToTime(startTime) : sBLANK
    String stopLbl = stopTime ? epochToTime(stopTime) : sBLANK
    str += (startLbl && stopLbl) ? "   \u2022 Restricted Times: ${startLbl} - ${stopLbl} (${!timeOk ? okSymFLD : notOkSymFLD})" : sBLANK
    List qDays = getQuietDays()
    String a = " (${!daysOk ? okSymFLD : notOkSymFLD})"
    str += dayInput && qDays ? "${(startLbl || stopLbl) ? "\n" : sBLANK}   \u2022 Restricted Day${pluralizeStr(qDays, false)}:${min ? " (${qDays?.size()} selected)" : " ${qDays?.join(", ")}"}${a}" : sBLANK
    a = " (${!modesOk ? okSymFLD : notOkSymFLD})"
    str += modeInput ? "${(startLbl || stopLbl || qDays) ? "\n" : sBLANK}   \u2022 Allowed Mode${pluralizeStr(modeInput, false)}:${min ? " (${modeInput?.size()} selected)" : " ${modeInput?.join(",")}"}${a}" : sBLANK
    str = str ? " \u2022 Restrictions: (${rest ? okSymFLD : notOkSymFLD})\n"+str : sBLANK
    return (str != sBLANK) ? str : sNULL
}

String getServiceConfDesc() {
    String str = sBLANK
    str += ((String)state.herokuName && (Boolean)getServerItem("onHeroku")) ? "Heroku: (Configured)\n" : sBLANK
    str += ((Boolean)state.serviceConfigured && (Boolean)getServerItem("isLocal")) ? "Local Server: (Configured)\n" : sBLANK
    str += "Server: (${getServerHostURL()})\n"
    str += (settings.amazonDomain) ? "Domain: (${settings?.amazonDomain})" : sBLANK
    return str != sBLANK ? str : sNULL
}

String getLoginStatusDesc() {
    String s = """<span style="color: gray;">Login Status:</b> ${(Boolean) state.authValid ? """<div style="color: green;">(Valid)</div>""" : """<div style="color: red;">(Invalid)</div>"""}</span>"""
    
    s += (getTsVal("lastCookieRrshDt")) ? "\nCookie Updated:\n(${seconds2Duration(getLastTsValSecs("lastCookieRrshDt"))})" : sBLANK
    return s
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
    List<String> actActs = getActiveActionNames()?.sort()?.collect { bulletItem(sBLANK, it) }
    List<String> inactActs = getInActiveActionNames()?.sort()?.collect { bulletItem(sBLANK, it) }
    Integer a = actActs?.size()
    Integer b = inactActs?.size()
    String str = sBLANK
    str += a ? "Active Actions:\n${actActs?.join("\n")}\n" : sBLANK
    str += a && b ? "\n" : sBLANK
    str += b ? "Paused Actions:\n${inactActs?.join("\n")}\n" : sBLANK
    str += a || b ? "\n${sTTM}" : "Tap to create actions using device/location events to perform advanced actions using your Alexa devices."
    return str
}

String getZoneDesc() {
    List<String> actZones = getActiveZoneNames()?.sort()?.collect { bulletItem(sBLANK, it) }
    List<String> inactZones = getInActiveZoneNames()?.sort()?.collect { bulletItem(sBLANK, it) }
    String str = sBLANK
    Integer a = actZones?.size()
    Integer b = inactZones?.size()
    str += a ? "Active Zones:\n${actZones?.join("\n")}\n" : sBLANK
    str += a && b ? "\n" : sBLANK
    str += b ? "\nInActive or Paused Zones:\n${inactZones?.join("\n")}\n" : sBLANK
    str += a || b ? "\n${sTTM}" : "Tap to create alexa device zones based on motion, presence, and other criteria."
    return str
}

String getInputToStringDesc(List inpt, Boolean addSpace=false) {
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
        tStr += """<small style="color: gray;"><b>App:</b> v${appVersionFLD}</small>"""
        tStr += (codeVer.echoDevice) ? """<br><small style="color: gray;"><b>Device:</b> v${codeVer.echoDevice}</small>""" : sBLANK
        tStr += (codeVer.actionApp) ? """<br><small style="color: gray;"><b>Action:</b> v${codeVer.actionApp}</small>""" : sBLANK
        tStr += (codeVer.zoneApp) ? """<br><small style="color: gray;"><b>Zone:</b> v${codeVer.zoneApp}</small>""" : sBLANK
        tStr += (codeVer.wsDevice) ? """<br><small style="color: gray;"><b>Socket:</b> v${codeVer.wsDevice}</small>""" : sBLANK
        tStr += (codeVer.server) ? """<br><small style="color: gray;"><b>Server:</b> v${codeVer.server}</small>""" : sBLANK
    }

    section (sectH3TS(app?.name, tStr, getAppImg("echo_speaks_3.2x", true), "#1A77C9")) {
        if(!(Boolean)state.isInstalled) {
            paragraph pTS("--NEW Install--", sNULL, true, sCLR4D9), state: sCOMPLT
        } else {
            if(!state.noticeData) { getNoticeData() }
            Boolean showDocs = false
            Map minUpdMap = getMinVerUpdsRequired()
            List codeUpdItems = codeUpdateItems(true)
            List remDevs = getRemovableDevs()
            if((Boolean)minUpdMap?.updRequired && ((List)minUpdMap.updItems).size()>0) {
                isNote=true
                String str3 = """<small style="color: red;"><b>Updates Required:</b></small>"""
                ((List) minUpdMap.updItems).each { item-> str3 += """<br><small style="color: red;">  ${sBULLET} ${item}</small>""" }
                str3 += """<br><br><small style="color: red; font-weight: bold;">If you just updated the code please press Done/Next to let the app process the changes.</small>"""
                paragraph str3
                showDocs = true
            } else if(codeUpdItems?.size()) {
                isNote=true
                String str2 = """<small style="color: red;"><b>Code Updates Available:</b></small>"""
                codeUpdItems?.each { item-> str2 += """<br><small style="color: red;">  ${sBULLET} ${item}</small>""" }
                paragraph str2
                showDocs = true
            }
            if(showDocs) { updateDocsInput() }
            if(!(Boolean) state.authValid && !(Boolean) state.resumeConfig) { 
                isNote = true; 
                String str4 = """<small style="color: orange;"><b>Login Issue:</b></small>"""
                str4 += """<br><br><small style="color: orange;">You are no longer logged in to Amazon.  Please complete the Authentication Process on the Server Login Page!</small>"""
                paragraph str4 
            }
            if(state.noticeData && state.noticeData.notices && state.noticeData.notices?.size()) {
                isNote = true; 
                String str1 = ""
                state.noticeData.notices.each { String item-> str1 += """<br><small style="color: red;">  ${sBULLET} ${item}</small>""" }
                paragraph str1
            }
            if(remDevs?.size()) {
                isNote = true
                paragraph """<small style="color: red;"><b>Device Removal:</b>\n(${remDevs?.size()}) devices can be removed</small>"""
            }
            if(!isNote) { paragraph """<small style="color: gray;">No Issues to Report</small>""" }
        }
        paragraph htmlLine()
    }
    List unkDevs = getUnknownDevices()
    if(unkDevs?.size()) {
        section() {
            Map params = [ assignees: "tonesto7", labels: "add_device_support", title: "[ADD DEVICE SUPPORT] (${unkDevs?.size()}) Devices", body: "Requesting device support from the following device(s):\n" + unkDevs?.collect { d-> d?.collect { k,v-> "${k}: ${v}" }?.join("\n") }?.join("\n\n")?.toString() ]
            def featUrl = "https://github.com/tonesto7/echo-speaks/issues/new?${UrlParamBuilder(params)}"
            href url: featUrl, style: sEXTNRL, required: false, title: inTS1("Unknown Devices Found\n\nSend device info to the Developer on GitHub?", "info"), description: "Tap to open browser"
        }
    }
}

String UrlParamBuilder(Map items) {
    return items?.collect { k,v -> "${k}=${URLEncoder.encode(v?.toString())}" }?.join("&") as String
}

def getRandomItem(items) {
    def list = new ArrayList<String>()
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()))
}

String randomString(Integer len) {
    def pool = ["a".."z",0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    logDebug("randomString: ${randChars?.join()}")
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
                <a href="https://heroku.com/deploy?template=https://github.com/tonesto7/echo-speaks-server/tree/${betaFLD ? "develop" : "master"}${getEnvParamsStr()}">
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
                <div class="my-3 text-center"><span><img src="${getAppImg("echo_speaks_3.1x", true)}"/><p class="h4 text-center">Echo Speaks</p></span></div>
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
    String json = new groovy.json.JsonOutput().toJson([message: (status ? "success" : "failed"), version: appVersionFLD])
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

private List amazonDomainOpts() { return (state.appData && state.appData?.amazonDomains?.size()) ? state.appData?.amazonDomains : amazonDomainsFLD }
private List localeOpts() { return (state.appData && state.appData?.locales?.size()) ? state.appData?.locales : localesFLD }

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

Boolean isContactOpen(sensors) {
    if(sensors) { sensors.each { if(sensors?.currentSwitch == "open") { return true } } }
    return false
}

Boolean isSwitchOn(devs) {
    if(devs) { devs.each { if(it?.currentSwitch == "on") { return true } } }
    return false
}

Boolean isSensorPresent(sensors) {
    if(sensors) { sensors.each { if(it?.currentPresence == "present") { return true } } }
    return false
}

Boolean isSomebodyHome(sensors) {
    if(sensors) { return (sensors.findAll { it?.currentPresence == "present" }.size() > 0) }
    return false
}

Boolean isInMode(modes) {
    return (location?.mode?.toString() in modes)
}

Boolean isInAlarmMode(modes) {
    if(!modes) return false
    return (getAlarmSystemStatus() in modes)
}

String getAlarmSystemName(Boolean abbr=false) {
    return (abbr ? "HSM" : "Hubitat Safety Monitor")
}

List getAlarmModes() {
    return ["armedAway", "armingAway", "armedHome", "armingHome", "armedNight", "armingNight", "disarmed", "allDisarmed"]
}

String getAlarmSystemStatus() {
    return location?.hsmStatus ?: "disarmed"
}

def getShmIncidents() {
    def incidentThreshold = now() - 604800000
    return location?.activeIncidents?.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}

// This is incomplete (and currently unused)
public setAlarmSystemMode(mode) {
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

Integer stateSize() { String j = new groovy.json.JsonOutput().toJson(state); return j.length() }
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

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug addHead(msg) } }
private void logInfo(String msg) { if((Boolean)settings.logInfo) { log.info " "+addHead(msg) } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace addHead(msg) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn) { log.warn " "+addHead(msg) }; if(!noHist) { addToLogHistory("warnHistory", msg, 15); } }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError) {
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

String addHead(String msg) {
    return "EchoApp (v"+appVersionFLD+") | "+msg
}

void clearDiagLogs(String type="all") {
    // log.debug "clearDiagLogs($type)"
    if(type=="all") {
        clearHistory()
        getActionApps()?.each { ca-> ca?.clearLogHistory() }
        getChildDevices()?.each { cd-> cd?.clearLogHistory() }
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
@Field static java.util.concurrent.Semaphore theMBLockFLD=new java.util.concurrent.Semaphore(0)

static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}

@Field static final String sHMLF = 'theHistMapLockFLD'
@Field static java.util.concurrent.Semaphore histMapLockFLD = new java.util.concurrent.Semaphore(1)

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

java.util.concurrent.Semaphore getSema(Integer snum) {
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

public Map getAppDuplTypes() { return appDuplicationTypesMapFLD }

@Field static final Map appDuplicationTypesMapFLD = [
    stat: [
        bool: ["notif_pushover", "notif_alexa_mobile", "logInfo", "logWarn", "logError", "logDebug", "logTrace", "enableWebCoRE"],
        enum: ["triggerEvents", "act_EchoZones", "actionType", "cond_alarm", "cond_months", /* "notif_pushover_devices", "notif_pushover_priority", "notif_pushover_sound", */ "trig_alarm", "trig_guard"],
        mode: ["cond_mode", "trig_mode"],
        number: [],
        text: ["appLbl"]
    ],
//
    ends: [
        bool: ["_all", "_avg", "_once", "_send_push", "_use_custom", "_stop_on_clear", "_db", "Pause"],
        enum: ["_cmd", "_type", "_routineExecuted",
               "_EchoDevices",
               "_scheduled_sunState", "_scheduled_recurrence", "_scheduled_days", "_scheduled_weeks", "_scheduled_weekdays", "_scheduled_months", "_scheduled_daynums", "_scheduled_type",
               "_routine_run", "_mode_run", "_piston_run", "_alarm_run", "_rt", "_rt_wd", "_nums", "_Codes", "_pistonExecuted", "_days", "_months", "_alarm_events"],
        number: ["_wait", "_low", "_high", "_equal", "_delay", "_cnt", "_volume", "_offset", "_after", "_after_repeat", "_rt_ed", "_volume_change", "_volume_restore"],
        text: ["_txt", "_sms_numbers", "_label", "_date", "_message"],
        mode: ["_modes"],
        time: ["_time_start", "_time_stop", "_time", "_scheduled_time"]
    ],
    caps: [
        _devs: "notification",
        _acceleration: "accelerationSensor",
        _battery: "battery",
        _contact: "contactSensor",
        _door: "garageDoorControl",
        _doors_open: "garageDoorControl",
        _doors_close: "garageDoorControl",
        _temperature: "temperatureMeasurement",
        _illuminance: "illuminanceMeasurement",
        _humidity: "relativeHumidityMeasurement",
        _motion: "motionSensor",
        _level: "switchLevel",
        _button: "button",
        _pushed: "pushableButton",
        _held: "holdableButton",
        _released: "releasableButton",
        _doubleTapped: "doubleTapableButton",
        _presence: "presenceSensor",
        _sirens: "alarm",
        _switch: "switch",
        _power: "powerMeter",
        _shade: "windowShades",
        _water: "waterSensor",
        _valve: "valve",
        _thermostat: "thermostat",
        _carbonMonoxide: "carbonMonoxideDetector",
        _smoke: "smokeDetector",
        _lock: "lock",
        _unlock: "lock",
        _securityKeypad: "securityKeypad",
        _disarm: "securityKeypad",
        _armHome: "securityKeypad",
        _armAway: "securityKeypad",
        _switches_off: "switch",
        _switches_on: "switch",
        _lights: "level",
        _color: "colorControl",
        _EchoDeviceList: ""
    ],
    dev: [
        _scene: "sceneActivator",
//        _EchoDevices: "EchoSpeaksDevice",
//        _EchoDeviceList: "EchoSpeaksDevice"
    ]
]

@Field static final Map deviceSupportMapFLD = [
    types: [
        "A10A33FOX2NUBK" : [ caps: [ "a", "t" ], image: "echo_spot_gen1", name: "Echo Spot" ],
        "A10L5JEZTKKCZ8" : [ caps: [ "a", "t" ], image: "vobot_bunny", name: "Vobot Bunny" ],
        "A112LJ20W14H95" : [ ignore: true ],
        "A12GXV8XMS007S" : [ caps: [ "a", "t" ], image: "firetv_gen1", name: "Fire TV (Gen1)" ],
        "A15ERDAKK5HQQG" : [ image: "sonos_generic", name: "Sonos" ],
        "A16MZVIFVHX6P6" : [ caps: [ "a", "t" ], image: "unknown", name: "Generic Echo" ],
        "A17LGWINFBUTZZ" : [ caps: [ "t", "a" ], image: "roav_viva", name: "Anker Roav Viva" ],
        "A18BI6KPKDOEI4" : [ caps: [ "a", "t" ], image: "ecobee4", name: "Ecobee4" ],
        "A18O6U1UQFJ0XK" : [ caps: [ "a", "t" ], image: "echo_plus_gen2", name: "Echo Plus (Gen2)" ],
        "A1C66CX2XD756O" : [ caps: [ "a", "t" ], image: "amazon_tablet", name: "Fire Tablet HD" ],
        "A1DL2DVDQVK3Q"  : [ blocked: true, ignore: true, name: "Mobile App" ],
        "A1F8D55J0FWDTN" : [ caps: [ "a", "t" ], image: "toshiba_firetv", name: "Fire TV (Toshiba)" ],
        "A1GC6GEE1XF1G9" : [ ignore: true ],
        "A1H0CMF1XM0ZP4" : [ blocked: true, name: "Bose SoundTouch 30" ],
        "A1J16TEDOYCZTN" : [ caps: [ "a", "t" ], image: "amazon_tablet", name: "Fire Tablet" ],
        "A1JJ0KFC4ZPNJ3" : [ caps: [ "a", "t" ], image: "echo_input", name: "Echo Input" ],
        "A1M0A9L9HDBID3" : [ caps: [ "t" ], image: "one-link", name: "One-Link Safe and Sound" ],
        "A1MPSLFC7L5AFK" : [ ignore: true ],
        "A1N9SW0I0LUX5Y" : [ blocked: false, caps: [ "a", "t" ], image: "unknown", name: "Ford/Lincoln Alexa App" ],
        "A1NL4BVLQ4L3N3" : [ caps: [ "a", "t" ], image: "echo_show_gen1", name: "Echo Show (Gen1)" ],
        "A1ORT4KZ23OY88" : [ ignore: true ],
        "A1P31Q3MOWSHOD" : [ caps: [ "t", "a" ], image: "halo_speaker", name: "Zolo Halo Speaker" ],
        "A1Q7QCGNMXAKYW" : [ blocked: true, image: "amazon_tablet", name: "Generic Tablet" ],
        "A1RABVCI4QCIKC" : [ caps: [ "a", "t" ], image: "echo_dot_gen3", name: "Echo Dot (Gen3)" ],
        "A1RTAM01W29CUP" : [ caps: [ "a", "t" ], image: "alexa_windows", name: "Windows App" ],
        "A1VS6XVTGTLC00" : [ ignore: true ],
        "A1VZJGJYCRI78V" : [ ignore: true ],
        "A1W2YILXTG9HA7" : [ caps: [ "t", "a" ], image: "unknown", name: "Nextbase 522GW Dashcam" ],
        "A1X7HJX9QL16M5" : [ blocked: true, ignore: true, name: "Bespoken.io" ],
        "A1Z88NGR2BK6A2" : [ caps: [ "a", "t" ], image: "echo_show_gen2", name: "Echo Show 8" ],
        "A1ZB65LA390I4K" : [ ignore: true ],
        "A21X6I4DKINIZU" : [ ignore: true ],
        "A21Z3CGI8UIP0F" : [ ignore: true ],
        "A25EC4GIHFOCSG" : [ blocked: true, name: "Unrecognized Media Player" ],
        "A27VEYGQBW3YR5" : [ caps: [ "a", "t" ], image: "echo_link", name: "Echo Link" ],
        "A2825NDLA7WDZV" : [ ignore: true ],
        "A29L394LN0I8HN" : [ ignore: true ],
        "A2E0SNTXJVT7WK" : [ caps: [ "a", "t" ], image: "firetv_gen1", name: "Fire TV (Gen2)" ],
        "A2GFL5ZMWNE0PX" : [ caps: [ "a", "t" ], image: "firetv_gen1", name: "Fire TV (Gen3)" ],
        "A2HZENIFNYTXZD" : [ caps: [ "a", "t" ], image: "facebook_portal", name: "Facebook Portal" ],
        "A52ARKF0HM2T4"  : [ caps: [ "a", "t" ], image: "facebook_portal", name: "Facebook Portal+" ],
        "A2IVLV5VM2W81"  : [  ignore: true ],
        "A2J0R2SD7G9LPA" : [ caps: [ "a", "t" ], image: "lenovo_smarttab_m10", name: "Lenovo SmartTab M10" ],
        "A2JKHJ0PX4J3L3" : [ caps: [ "a", "t" ], image: "firetv_cube", name: "Fire TV Cube (Gen2)" ],
        "A2LH725P8DQR2A" : [ caps: [ "a", "t" ], image: "fabriq_riff", name: "Fabriq Riff" ],
        "A2LWARUGJLBYEW" : [ caps: [ "a", "t" ], image: "firetv_stick_gen1", name: "Fire TV Stick (Gen2)" ],
        "A2M35JJZWCQOMZ" : [ caps: [ "a", "t" ], image: "echo_plus_gen1", name: "Echo Plus (Gen1)" ],
        "A2M4YX06LWP8WI" : [ caps: [ "a", "t" ], image: "amazon_tablet", name: "Fire Tablet" ],
        "A2OSP3UA4VC85F" : [ image: "sonos_generic", name: "Sonos" ],
        "A2R2GLZH1DFYQO" : [ caps: [ "t", "a" ], image: "halo_speaker", name: "Zolo Halo Speaker" ],
        "A2T0P32DY3F7VB" : [ ignore: true ],
        "A2TF17PFR55MTB" : [ ignore: true ],
        "A2TOXM6L8SFS8A" : [ ignore: true ],
        "A2V3E2XUH5Z7M8" : [ ignore: true ],
        "A1FWRGKHME4LXH" : [ ignore: true ],
        "A26TRKU1GG059T" : [ ignore: true ],
        "A2S24G29BFP88"  : [ ignore: true, image: "unknown", name: "Ford/Lincoln Alexa App" ],
        "A1NAFO69AAQ16Bk": [ ignore: true, image: "unknown", name: "Wyze Band" ],
        "A1NAFO69AAQ16B" : [ ignore: true, image: "unknown", name: "Wyze Band" ],
        "A3L2K717GERE73" : [ ignore: true, image: "unknown", name: "Voice in a Can (iOS)" ],
        "A222D4HGE48EOR" : [ ignore: true, image: "unknown", name: "Voice in a Can (Apple Watch)" ],
        "A19JK51Y4N50K5" : [ ignore: true, image: "unknown", name: "Jabra(?)" ],
        "A2X8WT9JELC577" : [ caps: [ "a", "t" ], image: "ecobee4", name: "Ecobee5" ],
        "A2XPGY5LRKB9BE" : [ caps: [ "a", "t" ], image: "unknown", name: "Fitbit Versa 2" ],
        "A2Y04QPFCANLPQ" : [ caps: [ "a", "t" ], image: "unknown", name: "Bose QuietComfort 35 II" ],
        "A2ZOTUOF1IBEYI" : [ ignore: true ],
        "A303PJF6ISQ7IC" : [ caps: [ "a", "t" ], image: "echo_auto", name: "Echo Auto" ],
        "A195TXHV1M5D4A" : [ caps: [ "a", "t" ], image: "echo_auto", name: "Echo Auto" ],
        "A30YDR2MK8HMRV" : [ caps: [ "a", "t" ], image: "echo_dot_clock", name: "Echo Dot Clock" ],
        "A32DDESGESSHZA" : [ caps: [ "a", "t" ], image: "echo_dot_gen3",  name : "Echo Dot (Gen3)" ],
        "A32DOYMUN6DTXA" : [ caps: [ "a", "t" ], image: "echo_dot_gen4",  name : "Echo Dot (Gen4)" ],
        "A2H4LV5GIZ1JFT" : [ caps: [ "a", "t" ], image: "echo_dot_clock_gen4",  name : "Echo Dot Clock (Gen4)" ],
        "A2U21SRK4QGSE1" : [ caps: [ "a", "t" ], image: "echo_dot_gen4",  name : "Echo Dot (Gen4)" ],
        "A347G2JC8I4HC7" : [ caps: [ "a", "t" ], image: "unknown", name: "Roav Car Charger Pro" ],
        "A37CFAHI1O0CXT" : [ image: "logitech_blast", name: "Logitech Blast" ],
        "A37SHHQ3NUL7B5" : [ blocked: true, name: "Bose Home Speaker 500" ],
        "A38949IHXHRQ5P" : [ caps: [ "a", "t" ], image: "echo_tap", name: "Echo Tap" ],
        "A38BPK7OW001EX" : [ blocked: true, name: "Raspberry Alexa" ],
        "A38EHHIB10L47V" : [ caps: [ "a", "t" ], image: "tablet_hd10", name: "Fire Tablet HD 8" ],
        "A3B50IC5QPZPWP" : [ caps: [ "a", "t" ], image: "unknown", name: "Polk Command Bar" ],
        "A3B5K1G3EITBIF" : [ caps: [ "a", "t" ], image: "facebook_portal", name: "Facebook Portal" ],
        "A3D4YURNTARP5K" : [ caps: [ "a", "t" ], image: "facebook_portal", name: "Facebook Portal TV" ],
        "A3CY98NH016S5F" : [ caps: [ "a", "t" ], image: "unknown", name: "Facebook Portal Mini" ],
        "A3BRT6REMPQWA8" : [ caps: [ "a", "t" ], image: "sonos_generic", name: "Bose Home Speaker 450" ],
        "A3C9PE6TNYLTCH" : [ image: "echo_wha", name: "Multiroom" ],
        "A3F1S88NTZZXS9" : [ blocked: true, image: "dash_wand", name: "Dash Wand" ],
        "A2WFDCBDEXOXR8" : [ blocked: true, image: "unknown", name: "Bose Soundbar 700" ],
        "A3FX4UWTP28V1P" : [ caps: [ "a", "t" ], image: "echo_plus_gen2", name: "Echo (Gen3)" ],
        "A3H674413M2EKB" : [ ignore: true ],
        "A3KULB3NQN7Z1F" : [ caps: [ "a", "t" ], image: "unknown", name: "Unknown TV" ],
        "A18TCD9FP10WJ9" : [ caps: [ "a", "t" ], image: "unknown", name: "Orbi Voice" ],
        "AGHZIK8D6X7QR"  : [ caps: [ "a", "t" ], image: "unknown", name: "Fire TV" ],
        "A3HF4YRA2L7XGC" : [ caps: [ "a", "t" ], image: "firetv_cube", name: "Fire TV Cube" ],
        "A3L0T0VL9A921N" : [ caps: [ "a", "t" ], image: "tablet_hd10", name: "Fire Tablet HD 8" ],
        "AVU7CPPF2ZRAS"  : [ caps: [ "a", "t" ], image: "tablet_hd10", name: "Fire Tablet HD 8" ],
        "A3NPD82ABCPIDP" : [ caps: [ "t" ], image: "sonos_beam", name: "Sonos Beam" ],
        "A3NVKTZUPX1J3X" : [ ignore: true, name: "Onkyp VC30" ],
        "A3NWHXTQ4EBCZS" : [ ignore: true ],
        "A2RG3FY1YV97SS" : [ ignore: true ],
        "A3IYPH06PH1HRA" : [ caps: [ "a", "t" ], image: "echo_frames", name: "Echo Frames" ],
        "AKO51L5QAQKL2"  : [ caps: [ "a", "t" ], image: "unknown", name: "Alexa Jams" ],
        "A3K69RS3EIMXPI" : [ caps: [ "a", "t" ], image: "unknown", name: "Hisense Smart TV" ],
        "A1QKZ9D0IJY332" : [ caps: [ "a", "t" ], image: "unknown", name: "Samsung TV 2020-U" ],
        "A3QPPX1R9W5RJV" : [ caps: [ "a", "t" ], image: "fabriq_chorus", name: "Fabriq Chorus" ],
        "A3R9S4ZZECZ6YL" : [ caps: [ "a", "t" ], image: "tablet_hd10", name: "Fire Tablet HD 10" ],
        "A3RBAYBE7VM004" : [ caps: [ "a", "t" ], image: "echo_studio", name: "Echo Studio" ],
        "A2RU4B77X9R9NZ" : [ caps: [ "a", "t" ], image: "echo_link_amp", name: "Echo Link Amp" ],
        "A3S5BH2HU6VAYF" : [ caps: [ "a", "t" ], image: "echo_dot_gen2", name: "Echo Dot (Gen2)" ],
        "A3SSG6GR8UU7SN" : [ caps: [ "a", "t" ], image: "echo_sub_gen1", name: "Echo Sub" ],
        "A3BW5ZVFHRCQPO" : [ caps: [ "a", "t" ], image: "unknown", name: "BMW Alexa Integration" ],
        "A3SSWQ04XYPXBH" : [ blocked: true, image: "amazon_tablet", name: "Generic Tablet" ],
        "A3VRME03NAXFUB" : [ caps: [ "a", "t" ], image: "echo_flex", name: "Echo Flex" ],
        "A4ZP7ZC4PI6TO"  : [ caps: [ "a", "t" ], image: "echo_show_5", name: "Echo Show 5 (Gen1)" ],
        "A3RMGO6LYLH7YN" : [ caps: [ "a", "t" ], image: "echo_gen4", name: "Echo (Gen4)" ],
        "A7WXQPH584YP"   : [ caps: [ "a", "t" ], image: "echo_gen2", name: "Echo (Gen2)" ],
        "A81PNL0A63P93"  : [ caps: [ "a", "t" ], image: "unknown", name: "Home Remote" ],
        "AB72C64C86AW2"  : [ caps: [ "a", "t" ], image: "echo_gen1", name: "Echo (Gen1)" ],
        "A1SCI5MODUBAT1" : [ caps: [ "a", "t"], image: "unknown", name: "Pioneer DMH-W466NEX" ],
        "A1ETW4IXK2PYBP" : [ caps: [ "a", "t"], image: "unknown", name: "Talk to Alexa" ],
        "ABP0V5EHO8A4U"  : [ ignore: true ],
        "AD2YUJTRVBNOF"  : [ ignore: true ],
        "ADQRVG6LYK4LQ"  : [ ignore: true ],
        "A1GPVMRI4IOS0M" : [ ignore: true ],
        "A2Z8O30CD35N8F" : [ ignore: true ],
        "A1XN1MKELB7WUF" : [ ignore: true ],
        "ADVBD696BHNV5"  : [ caps: [ "a", "t" ], image: "firetv_stick_gen1", name: "Fire TV Stick (Gen1)" ],
        "AE7X7Z227NFNS"  : [ caps: [ "a", "t" ], image: "unknown", name: "HiMirror Mini" ],
        "AF473ZSOIRKFJ"  : [ caps: [ "a", "t" ], image: "unknown", name: "Onkyo VC-PX30" ],
        "A2E5N6DMWCW8MZ" : [ caps: [ "a", "t" ], image: "unknown", name: "Brilliant Smart Switch" ],
        "AFF50AL5E3DIU"  : [ caps: [ "a", "t" ], image: "insignia_firetv",  "name" : "Fire TV (Insignia)" ],
        "AGZWSPR7FLP9E"  : [ ignore: true ],
        "AILBSA2LNTOYL"  : [ ignore: true ],
        "AKKLQD9FZWWQS"  : [ blocked: true, caps: [ "a", "t" ], image: "unknown", name: "Jabra Elite" ],
        "AKNO1N0KSFN8L"  : [ caps: [ "a", "t" ], image: "echo_dot_gen1", name: "Echo Dot (Gen1)" ],
        "AKPGW064GI9HE"  : [ caps: [ "a", "t" ], image: "firetv_stick_gen1", name: "Fire TV Stick 4K (Gen3)" ],
        "AO6HHP9UE6EOF"  : [ caps: [ "a", "t" ], image: "unknown", name: "Unknown Media Device" ],
        "AP1F6KUH00XPV"  : [ blocked: true, name: "Stereo/Subwoofer Pair" ],
        "AP4RS91ZQ0OOI"  : [ caps: [ "a", "t" ], image: "toshiba_firetv", name: "Fire TV (Toshiba)" ],
        "AFF5OAL5E3DIU"  : [ caps: [ "a", "t" ], image: "toshiba_firetv", name: "Fire TV" ],
        "ATH4K2BAIXVHQ"  : [ ignore: true ],
        "AVD3HM0HOJAAL"  : [ image: "sonos_generic", name: "Sonos" ],
        "AVE5HX13UR5NO"  : [ caps: [ "a", "t" ], image: "logitech_zero_touch", name: "Logitech Zero Touch" ],
        "AVN2TMX8MU2YM"  : [ blocked: true, name: "Bose Home Speaker 500" ],
        "AWZZ5CVHX2CD"   : [ caps: [ "a", "t" ], image: "echo_show_gen2", name: "Echo Show (Gen2)" ],
        "A2C8J6UHV0KFCV" : [ caps: [ "a", "t" ], image: "unknown", name: "Alexa Gear" ],
        "AUPUQSVCVHXP0"  : [ caps: [ "a", "t" ], image: "unknown", name: "Ecobee Switch+" ],
        "A2RJLFEH0UEKI9" : [ ignore: true ],
        "AKOAGQTKAS9YB"  : [ ignore: true ],
        "A37M7RU8Z6ZFB"  : [ caps: [ "a", "t" ], image: "unknown", name: "Garmin Speak" ],
        "A2WN1FJ2HG09UN" : [ caps: [ "a", "t" ], image: "unknown", name: "Ultimate Alexa App" ],
        "A2BRQDVMSZD13S" : [ caps: [ "a", "t" ], image: "unknown", name: "SURE Universal Remote" ],
        "A3TCJ8RTT3NVI7" : [ caps: [ "a", "t" ], image: "unknown", name: "Alexa Listens" ],
    ],
    families: [
        block: [ "AMAZONMOBILEMUSIC_ANDROID", "AMAZONMOBILEMUSIC_IOS", "TBIRD_IOS", "TBIRD_ANDROID", "VOX", "MSHOP" ],
        echo: [ "ROOK", "KNIGHT", "ECHO" ],
        other: [ "REAVER", "FIRE_TV", "FIRE_TV_CUBE", "ALEXA_AUTO", "MMSDK" ],
        tablet: [ "TABLET" ],
        wha: [ "WHA" ]
    ]
]
