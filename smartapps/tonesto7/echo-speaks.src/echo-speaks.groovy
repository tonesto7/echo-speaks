/**
 *  Echo Speaks SmartApp
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
String appVersion()   { return "3.0.0" }
String appModified()  { return "2019-08-27" }
String appAuthor()    { return "Anthony S." }
Boolean isBeta()      { return true }
Boolean isST()        { return (getPlatform() == "SmartThings") }
Map minVersions()     { return [echoDevice: 300, actionApp: 300, server: 222] } //These values define the minimum versions of code this app will work with.
// TODO: Change importURL back to master branch
// TODO: Change docs link to public docs for release
definition(
    name        : "Echo Speaks",
    namespace   : "tonesto7",
    author      : "Anthony Santilli",
    description : "Integrate your Amazon Echo devices into your Smart Home environment to create virtual Echo Devices. This allows you to speak text, make announcements, control media playback including volume, and many other Alexa features.",
    category    : "My Apps",
    iconUrl     : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks.1x${state?.updateAvailable ? "_update" : ""}.png",
    iconX2Url   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks.2x${state?.updateAvailable ? "_update" : ""}.png",
    iconX3Url   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/icons/echo_speaks.3x${state?.updateAvailable ? "_update" : ""}.png",
    importUrl   : "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/smartapps/tonesto7/echo-speaks.src/echo-speaks.groovy",
    oauth       : true,
    pausable    : true
)

preferences {
    page(name: "startPage")
    page(name: "mainPage")
    page(name: "settingsPage")
    page(name: "devicePrefsPage")
    page(name: "deviceManagePage")
    page(name: "newSetupPage")
    page(name: "groupsPage")
    page(name: "actionsPage")
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
    page(name: "broadcastPage")
    page(name: "announcePage")
    page(name: "sequencePage")
    page(name: "setNotificationTimePage")
    page(name: "uninstallPage")
}

def startPage() {
    state?.isParent = true
    checkVersionData(true)
    state?.childInstallOkFlag = false
    if(!state?.resumeConfig && state?.isInstalled) { checkGuardSupport() }
    if(state?.resumeConfig || (state?.isInstalled && !state?.serviceConfigured)) { return servPrefPage() }
    else if(isBeta() || showChgLogOk()) { return changeLogPage() }
    else if(showDonationOk()) { return donationPage() }
    else { return mainPage() }
}

def mainPage() {
    Boolean tokenOk = getAccessToken()
    Boolean newInstall = (state?.isInstalled != true)
    Boolean resumeConf = (state?.resumeConfig == true)
    if(state?.refreshDeviceData == true) { getEchoDevices() }
    return dynamicPage(name: "mainPage", uninstall: false, install: true) {
        appInfoSect()
        if(!tokenOk) {
            section() { paragraph title: "Uh OH!!!", "Oauth Has NOT BEEN ENABLED. Please Remove this app and try again after it after enabling OAUTH"; }; return;
        }
        if(newInstall) {
            deviceDetectOpts()
        } else {
            section(sTS("Alexa Guard:")) {
                if(state?.alexaGuardSupported) {
                    String gState = state?.alexaGuardState ? (state?.alexaGuardState =="ARMED_AWAY" ? "Away" : "Home") : "Unknown"
                    String gStateIcon = gState == "Unknown" ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
                    href "alexaGuardPage", title: inTS("Alexa Guard Control", getAppImg(gStateIcon, true)), image: getAppImg(gStateIcon), state: guardAutoConfigured() ? "complete" : null,
                            description: "Current Status: ${gState}${guardAutoConfigured() ? "\nAutomation: Enabled" : ""}\n\nTap to proceed..."
                } else { paragraph "Alexa Guard is not enabled or supported by any of your Echo Devices", image: getAppImg(gStateIcon) }
            }

            section(sTS("Alexa Devices:")) {
                if(!newInstall) {
                    List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : ""}${it?.value?.supported == false ? " \u2639" : ""}" }
                    Map skDevs = state?.skippedDevices?.findAll { (it?.value?.reason != "In Ignore Device Input") }
                    Map ignDevs = state?.skippedDevices?.findAll { (it?.value?.reason == "In Ignore Device Input") }
                    List remDevs = getRemovableDevs()
                    if(remDevs?.size()) { paragraph "Removable Devices:\n${remDevs?.sort()?.join("\n")}", required: true, state: null }
                    href "deviceManagePage", title: inTS("Manage Devices:", getAppImg("devices", true)), description: "(${devs?.size()}) Installed\n\nTap to manage...", state: "complete", image: getAppImg("devices")
                } else { paragraph "Device Management will be displayed after install is complete" }
            }

            def acts = getActionApps()
            section(sTS("Actions:")) {
                paragraph "Create automation triggers from device/location events and perform advanced functions using your Alexa device."
                href "actionsPage", title: inTS("Manage Actions", getAppImg("es_actions", true)), description: getActionsDesc(), state: (acts?.size() ? "complete" : null), image: getAppImg("es_actions")
            }

            section(sTS("Alexa Login Service:")) {
                def t0 = getServiceConfDesc()
                href "servPrefPage", title: inTS("Manage Login Service", getAppImg("settings", true)), description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("settings")
            }
            if(!state?.shownDevSharePage) { showDevSharePrefs() }
            section(sTS("Notifications:")) {
                def t0 = getAppNotifConfDesc()
                href "notifPrefPage", title: inTS("Manage Notifications", getAppImg("notification2", true)), description: (t0 ? "${t0}\n\nTap to modify" : "Tap to configure"), state: (t0 ? "complete" : null), image: getAppImg("notification2")
            }

            section(sTS("Experimental Functions:")) {
                href "deviceTestPage", title: inTS("Device Testing", getAppImg("testing", true)), description: "Test Speech, Announcements, and Sequences Builder\n\nTap to proceed...", image: getAppImg("testing")
                href "musicSearchTestPage", title: inTS("Music Search Tests", getAppImg("music", true)), description: "Test music queries\n\nTap to proceed...", image: getAppImg("music")
            }
        }
        section(sTS("Documentation & Settings:")) {
            href url: documentationLink(), style: "external", required: false, title: inTS("View Documentation", getAppImg("documentation", true)), description: "Tap to proceed", state: "complete", image: getAppImg("documentation")
            href "settingsPage", title: inTS("Manage Logging, and Metrics", getAppImg("settings", true)), description: "Tap to modify...", image: getAppImg("settings")
        }
        if(!newInstall) {
            section(sTS("Donations:")) {
                href url: textDonateLink(), style: "external", required: false, title: inTS("Donations", getAppImg("donate", true)), description: "Tap to open browser", image: getAppImg("donate")
            }
            section(sTS("Remove Everything:")) {
                href "uninstallPage", title: inTS("Uninstall this App", getAppImg("uninstall", true)), description: "Tap to Remove...", image: getAppImg("uninstall")
            }
        } else {
            showDevSharePrefs()
            section(sTS("Important Step:")) {
                paragraph title: "Notice:", pTS("Please complete the install and return to the Echo Speaks App to resume deployment and configuration of the server.", null, true, "red"), required: true, state: null
                state?.resumeConfig = true
            }
        }
        state.ok2InstallActionFlag = false
    }
}

def deviceManagePage() {
    return dynamicPage(name: "deviceManagePage", uninstall: false, install: false) {
        Boolean newInstall = (state?.isInstalled != true)
        section(sTS("Alexa Devices:")) {
            if(!newInstall) {
                List devs = getDeviceList()?.collect { "${it?.value?.name}${it?.value?.online ? " (Online)" : ""}${it?.value?.supported == false ? " \u2639" : ""}" }?.sort()
                Map skDevs = state?.skippedDevices?.findAll { (it?.value?.reason != "In Ignore Device Input") }
                Map ignDevs = state?.skippedDevices?.findAll { (it?.value?.reason == "In Ignore Device Input") }
                if(devs?.size()) {
                    href "deviceListPage", title: inTS("Installed Devices:"), description: "${devs?.join("\n")}\n\nTap to view details...", state: "complete"
                } else { paragraph title: "Discovered Devices:", "No Devices Available", state: "complete" }
                if(skDevs?.size()) {
                    String uDesc = "Unsupported: (${skDevs?.size()})"
                    uDesc += ignDevs?.size() ? "\nUser Ignored: (${ignDevs?.size()})" : ""
                    uDesc += settings?.bypassDeviceBlocks ? "\nBlock Bypass: (Active)" : ""
                    href "unrecogDevicesPage", title: inTS("Unused Devices:"), description: "${uDesc}\n\nTap to view details..."
                }
            }
            def devPrefDesc = devicePrefsDesc()
            href "devicePrefsPage", title: inTS("Device Detection\nPreferences", getAppImg("devices", true)), description: "${devPrefDesc ? "${devPrefDesc}\n\n" : ""}Tap to configure...", state: "complete", image: getAppImg("devices")
        }
    }
}

def alexaGuardPage() {
    return dynamicPage(name: "alexaGuardPage", uninstall: false, install: false) {
        String gState = state?.alexaGuardState ? (state?.alexaGuardState =="ARMED_AWAY" ? "Away" : "Home") : "Unknown"
        String gStateIcon = gState == "Unknown" ? "alarm_disarm" : (gState == "Away" ? "alarm_away" : "alarm_home")
        String gStateTitle = (gState == "Unknown" || gState == "Home") ? "Set Guard to Armed?" : "Set Guard to Home?"
        section(sTS("Alexa Guard Control")) {
            input "alexaGuardAwayToggle", "bool", title: inTS(gStateTitle, getAppImg(gStateIcon, true)), description: "Current Status: ${gState}", defaultValue: false, submitOnChange: true, image: getAppImg(gStateIcon)
        }
        if(settings?.alexaGuardAwayToggle != state?.alexaGuardAwayToggle) {
            setGuardState(settings?.alexaGuardAwayToggle == true ? "ARMED_AWAY" : "ARMED_STAY")
        }
        state?.alexaGuardAwayToggle = settings?.alexaGuardAwayToggle
        section(sTS("Automate Guard Control")) {
            href "alexaGuardAutoPage", title: inTS("Automate Guard Changes", getAppImg("alarm_disarm", true)), description: guardAutoDesc(), image: getAppImg("alarm_disarm"), state: (guardAutoDesc() =="Tap to configure..." ? null : "complete")
        }
    }
}

def alexaGuardAutoPage() {
    return dynamicPage(name: "alexaGuardAutoPage", uninstall: false, install: false) {
        section(sTS("Set Guard using ${getAlarmSystemName(true)}")) {
            input "guardHomeAlarm", "enum", title: inTS("Home in ${getAlarmSystemName(true)} modes.", getAppImg("alarm_home", true)), description: "Tap to select...", options: getAlarmModeOpts(), required: (settings?.guardAwayAlarm), multiple: true, submitOnChange: true, image: getAppImg("alarm_home")
            input "guardAwayAlarm", "enum", title: inTS("Away in ${getAlarmSystemName(true)} modes.", getAppImg("alarm_away", true)), description: "Tap to select...", options: getAlarmModeOpts(), required: (settings?.guardHomeAlarm), multiple: true, submitOnChange: true, image: getAppImg("alarm_away")
        }

        section(sTS("Set Guard using Modes")) {
            input "guardHomeModes", "mode", title: inTS("Home in these Modes?", getPublicImg("mode", true)), description: "Tap to select...", required: (settings?.guardAwayModes), multiple: true, submitOnChange: true, image: getAppImg("mode")
            input "guardAwayModes", "mode", title: inTS("Away in these Modes?", getPublicImg("mode", true)), description: "Tap to select...", required: (settings?.guardHomeModes), multiple: true, submitOnChange: true, image: getAppImg("mode")
        }
        section(sTS("Set Guard using Presence")) {
            input "guardAwayPresence", "capability.presenceSensor", title: inTS("Away when all of these Sensors are away?", getAppImg("presence", true)), description: "Tap to select...", multiple: true, required: false, submitOnChange: true, image: getAppImg("presence")
        }
        if(guardAutoConfigured()) {
            section(sTS("Delay:")) {
                input "guardAwayDelay", "number", title: inTS("Delay before activating Guard?", getAppImg("delay_time", true)), description: "Enter number in seconds", required: false, defaultValue: 30, submitOnChange: true, image: getAppImg("delay_time")
            }
        }
        section(sTS("Restrict Guard Changes (Optional):")) {
            input "guardRestrictOnSwitch", "capability.switch", title: inTS("Only when these switch(es) are On?", getAppImg("switch", true)), description: "Tap to select...", multiple: true, required: false, submitOnChange: true, image: getAppImg("switch")
            input "guardRestrictOffSwitch", "capability.switch", title: inTS("Only when these switch(es) are Off?", getAppImg("switch", true)), description: "Tap to select...", multiple: true, required: false, submitOnChange: true, image: getAppImg("switch")
        }
    }
}

Boolean guardAutoConfigured() {
    return ((settings?.guardAwayAlarm && settings?.guardHomeAlarm) || (settings?.guardAwayModes && settings?.guardHomeModes) || settings?.guardAwayPresence)
}

String guardAutoDesc() {
    String str = ""
    if(guardAutoConfigured()) {
        str += "Guard Triggers:"
        str += (settings?.guardAwayAlarm && settings?.guardHomeAlarm) ? bulletItem(str, "Using ${getAlarmSystemName()}\n") : ""
        str += settings?.guardHomeModes ? bulletItem(str, "Home Modes: (${settings?.guardHomeModes?.size()})\n") : ""
        str += settings?.guardAwayModes ? bulletItem(str, "Away Modes: (${settings?.guardAwayModes?.size()})\n") : ""
        str += settings?.guardAwayPresence ? bulletItem(str, "Presence Home: (${settings?.guardAwayPresence?.size()})") : ""
    }
    return str == "" ? "Tap to configure..." : "${str}\n\nTap to configure..."
}

def guardTriggerEvtHandler(evt) {
    def evtDelay = now() - evt?.date?.getTime()
	logDebug("${evt?.name.toUpperCase()} Event | Device: ${evt?.displayName} | Value: (${strCapitalize(evt?.value)}) with a delay of ${evtDelay}ms")
    if(!guardRestrictOk()) {
        log.debug "guardTriggerEvtHandler | Skipping Changes because restriction filter is active"
        return
    }
    String newState = null
    String curState = state?.alexaGuardState ?: null
    switch(evt?.name as String) {
        case "mode":
            Boolean inAwayMode = isInMode(settings?.guardAwayModes)
            Boolean inHomeMode = isInMode(settings?.guardHomeModes)
            if(inAwayMode && inHomeMode) { logError("Guard Control Trigger can't act because same mode is in both Home and Away input"); return; }
            if(inAwayMode && !inHomeMode) { newState = "ARMED_AWAY" }
            if(!inAwayMode && inHomeMode) { newState = "ARMED_STAY" }
            break
        case "presence":
            newState = isSomebodyHome(settings?.guardAwayPresence) ? "ARMED_STAY" : "ARMED_AWAY"
            break
        case "alarmSystemStatus":
        case "hsmStatus":
            Boolean inAlarmHome = isInAlarmMode(settings?.guardHomeAlarm)
            Boolean inAlarmAway = isInAlarmMode(settings?.guardAwayAlarm)
            if(inAlarmAway && !inAlarmHome) { newState = "ARMED_AWAY" }
            if(!inAlarmAway && inAlarmHome) { newState = "ARMED_STAY" }
            break
    }
    if(curState == newState) { logInfo("Skipping Guard Change... New Guard State is the same as current state: ($curState)") }
    if(newState && curState != newState) {
        if (newState == "ARMED_STAY") {
            unschedule("setGuardAway")
            logInfo("Setting Alexa Guard Mode to Home...")
            setGuardHome()
        }
        if(newState == "ARMED_AWAY") {
            if(settings?.guardAwayDelay) { logWarn("Setting Alexa Guard Mode to Away in (${settings?.guardAwayDelay} seconds)"); runIn(settings?.guardAwayDelay, "setGuardAway"); }
            else { setGuardAway(); logWarn("Setting Alexa Guard Mode to Away..."); }
        }
    }
}

Boolean guardRestrictOk() {
    Boolean onSwOk = settings?.guardRestrictOnSwitch ? isSwitchOn(settings?.guardRestrictOnSwitch) : true
    Boolean offSwOk = settings?.guardRestrictOffSwitch ? !isSwitchOn(settings?.guardRestrictOffSwitch) : true
    return (onSwOk && offSwOk)
}

def actionsPage() {
    return dynamicPage(name: "actionsPage", nextPage: "mainPage", uninstall: false, install: false) {
        def actionApp = getChildApps()?.findAll { it?.name == actChildName() }
        if(actionApp) { /*Nothing to add here yet*/ }
        else {
            section("") {
                paragraph pTS("You haven't created any Actions yet!\nTap Create New Action to get Started")
            }
        }
        section() {
            app(name: "actionApp", appName: actChildName(), namespace: "tonesto7", multiple: true, title: inTS("Create New Action", getAppImg("es_actions", true)), image: getAppImg("es_actions"))
        }

        if(actionApp) {
            section (sTS("Pause All Actions:"), hideable: true, hidden: true) {
                input "pauseChildActions", "bool", title: inTS("Pause All Actions?", getAppImg("pause_orange", true)), defaultValue: false, submitOnChange: true, image: getAppImg("pause_orange")
                // executeActionPause()
            }
        }
        state.childInstallOkFlag = true
    }
}

private executeActionPause() {
    getActionApps()?.each {
        if(it?.isPaused() != settings?.pauseChildActions) { it?.updatePauseState(settings?.pauseChildActions) }
    }
}

def devicePrefsPage() {
    Boolean newInstall = (state?.isInstalled != true)
    Boolean resumeConf = (state?.resumeConfig == true)
    return dynamicPage(name: "devicePrefsPage", uninstall: false, install: false) {
        deviceDetectOpts()
        section(sTS("Detection Override:")) {
            paragraph pTS("Device not detected?  Enabling this will allow you to override the developer block for unrecognized or uncontrollable devices.  This is useful for testing the device.", getAppImg("info", true), false)
            input "bypassDeviceBlocks", "bool", title: inTS("Override Blocks and Create Ignored Devices?"), description: "WARNING: This will create devices for all remaining ignored devices", required: false, defaultValue: false, submitOnChange: true
        }
        devCleanupSect()
        if(!newInstall && !resumeConf) { state?.refreshDeviceData = true }
    }
}

private deviceDetectOpts() {
    Boolean newInstall = (state?.isInstalled != true)
    Boolean resumeConf = (state?.resumeConfig == true)
    section(sTS("Device Detection Preferences")) {
        input "autoCreateDevices", "bool", title: inTS("Auto Create New Devices?", getAppImg("devices", true)), description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("devices")
        input "createTablets", "bool", title: inTS("Create Devices for Tablets?", getAppImg("amazon_tablet", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("amazon_tablet")
        input "createWHA", "bool", title: inTS("Create Multiroom Devices?", getAppImg("echo_wha", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("echo_wha")
        input "createOtherDevices", "bool", title: inTS("Create Other Alexa Enabled Devices?", getAppImg("devices", true)), description: "FireTV (Cube, Stick), Sonos, etc.", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("devices")
        input "autoRenameDevices", "bool", title: inTS("Rename Devices to Match Amazon Echo Name?", getAppImg("name_tag", true)), description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("name_tag")
        input "addEchoNamePrefix", "bool", title: inTS("Add 'Echo - ' Prefix to label?", getAppImg("name_tag")), description: "", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("name_tag")
        Map devs = getAllDevices(true)
        if(devs?.size()) {
            input "echoDeviceFilter", "enum", title: inTS("Don't Use these Devices", getAppImg("exclude", true)), description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true, image: getAppImg("exclude")
            paragraph title:"Notice:", pTS("To prevent unwanted devices from reinstalling after removal make sure to add it to the Don't use these devices input above before removing.", getAppImg("info", true), false)
        }
    }
}

private devCleanupSect() {
    if(state?.isInstalled && !state?.resumeConfig) {
        section(sTS("Device Cleanup Options:")) {
            List remDevs = getRemovableDevs()
            if(remDevs?.size()) { paragraph "Removable Devices:\n${remDevs?.sort()?.join("\n")}", required: true, state: null }
            paragraph title:"Notice:", pTS("Remember to add device to filter above to prevent recreation.  Also the cleanup process will fail if the devices are used in external apps/automations", getAppImg("info", true), true, "#2784D9")
            input "cleanUpDevices", "bool", title: inTS("Cleanup Unused Devices?"), description: "", required: false, defaultValue: false, submitOnChange: true
            if(cleanUpDevices) { removeDevices() }
        }
    }
}

private List getRemovableDevs() {
    def childDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    Map eDevs = state?.echoDeviceMap ?: [:]
    List remDevs = []
    childDevs?.each { cDev->
        def dni = cDev?.deviceNetworkId?.tokenize("|")
        if(!eDevs?.containsKey(dni[2])) { remDevs?.push(cDev?.getLabel() as String) }
    }
    return remDevs ?: []
}

private String devicePrefsDesc() {
    String str = ""
    str += "Auto Create (${(settings?.autoCreateDevices == false) ? "Disabled" : "Enabled"})"
    if(settings?.autoCreateDevices) {
        str += (settings?.createTablets == true) ? bulletItem(str, "Tablets") : ""
        str += (settings?.createWHA == true) ? bulletItem(str, "WHA") : ""
        str += (settings?.createOtherDevices == true) ? bulletItem(str, "Other Devices") : ""
    }
    str += settings?.autoRenameDevices != false ? bulletItem(str, "Auto Rename") : ""
    str += settings?.bypassDeviceBlocks == true ? "\nBlock Bypass: (Active)" : ""
    def remDevsSz = getRemovableDevs()?.size() ?: 0
    str += remDevsSz > 0 ? "\n\nRemovable Devices: (${remDevsSz})" : ""
    return str != "" ? str : null
}

def settingsPage() {
    return dynamicPage(name: "settingsPage", uninstall: false, install: false) {
        section(sTS("Logging:")) {
            input "logInfo", "bool", title: inTS("Show Info Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logWarn", "bool", title: inTS("Show Warning Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logError", "bool", title: inTS("Show Error Logs?", getAppImg("debug", true)), required: false, defaultValue: true, submitOnChange: true, image: getAppImg("debug")
            input "logDebug", "bool", title: inTS("Show Debug Logs?", getAppImg("debug", true)), description: "Only leave on when required", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug")
            input "logTrace", "bool", title: inTS("Show Detailed Logs?", getAppImg("debug", true)), description: "Only Enabled when asked by the developer", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("debug")
        }
        showDevSharePrefs()
        section(sTS("App Change Details:")) {
            href "changeLogPage", title: inTS("View App Revision History", getAppImg("change_log", true)), description: "Tap to view", image: getAppImg("change_log")
        }
    }
}

def deviceListPage() {
    return dynamicPage(name: "deviceListPage", install: false) {
        Boolean onST = isST()
        section(sTS("Discovered Devices:")) {
            state?.echoDeviceMap?.sort { it?.value?.name }?.each { k,v->
                String str = "Status: (${v?.online ? "Online" : "Offline"})"
                str += "\nStyle: ${v?.style?.name}"
                str += "\nFamily: ${v?.family}"
                str += "\nType: ${v?.type}"
                str += "\nVolume Control: (${v?.volumeSupport?.toString()?.capitalize()})"
                str += "\nAnnouncements: (${v?.announceSupport?.toString()?.capitalize()})"
                str += "\nText-to-Speech: (${v?.ttsSupport?.toString()?.capitalize()})"
                str += "\nMusic Player: (${v?.mediaPlayer?.toString()?.capitalize()})"
                str += v?.supported != true ? "\nUnsupported Device: (True)" : ""
                str += (v?.mediaPlayer == true && v?.musicProviders) ? "\nMusic Providers: [${v?.musicProviders}]" : ""
                if(onST) {
                    paragraph title: pTS(v?.name, getAppImg(v?.style?.image, true), false, "#2784D9"), str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.style?.image)
                } else { href "deviceListPage", title: inTS(v?.name, getAppImg(v?.style?.image, true)), description: str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.style?.image) }
            }
        }
    }
}

def unrecogDevicesPage() {
    return dynamicPage(name: "unrecogDevicesPage", install: false) {
        Boolean onST = isST()
        Map skDevMap = state?.skippedDevices ?: [:]
        Map ignDevs = skDevMap?.findAll { (it?.value?.reason == "In Ignore Device Input") }
        Map unDevs = skDevMap?.findAll { (it?.value?.reason != "In Ignore Device Input") }
        section(sTS("Unrecognized/Unsupported Devices:")) {
            if(unDevs?.size()) {
                unDevs?.sort { it?.value?.name }?.each { k,v->
                    String str = "Status: (${v?.online ? "Online" : "Offline"})\nStyle: ${v?.desc}\nFamily: ${v?.family}\nType: ${v?.type}\nVolume Control: (${v?.volume?.toString()?.capitalize()})"
                    str += "\nText-to-Speech: (${v?.tts?.toString()?.capitalize()})\nMusic Player: (${v?.mediaPlayer?.toString()?.capitalize()})\nReason Ignored: (${v?.reason})"
                    if(onST) {
                        paragraph title: pTS(v?.name, getAppImg(v?.image, true), false), str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.image)
                    } else { href "unrecogDevicesPage", title: inTS(v?.name, getAppImg(v?.image, true)), description: str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.image) }
                }
                input "bypassDeviceBlocks", "bool", title: inTS("Override Blocks and Create Ignored Devices?"), description: "WARNING: This will create devices for all remaining ignored devices", required: false, defaultValue: false, submitOnChange: true
            } else {
                paragraph pTS("No Uncognized Devices", null, true)
            }
        }
        if(ignDevs?.size()) {
            section(sTS("User Ignored Devices:")) {
                ignDevs?.sort { it?.value?.name }?.each { k,v->
                    String str = "Status: (${v?.online ? "Online" : "Offline"})\nStyle: ${v?.desc}\nFamily: ${v?.family}\nType: ${v?.type}\nVolume Control: (${v?.volume?.toString()?.capitalize()})"
                    str += "\nText-to-Speech: (${v?.tts?.toString()?.capitalize()})\nMusic Player: (${v?.mediaPlayer?.toString()?.capitalize()})\nReason Ignored: (${v?.reason})"
                    if(onST) {
                        paragraph title: pTS(v?.name, getAppImg(v?.image, true), false, "#2784D9"), str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.image)
                    } else { href "unrecogDevicesPage", title: inTS(v?.name, getAppImg(v?.image, true)), description: str, required: true, state: (v?.online ? "complete" : null), image: getAppImg(v?.image) }
                }
            }
        }
    }
}

def showDevSharePrefs() {
    section(sTS("Share Data with Developer:")) {
        paragraph title: "What is this used for?", pTS("These options send non-user identifiable information and error data to diagnose catch trending issues.", null, false)
        input ("optOutMetrics", "bool", title: inTS("Do Not Share Data?", getAppImg("analytics", true)), required: false, defaultValue: false, submitOnChange: true, image: getAppImg("analytics"))
        if(settings?.optOutMetrics != true) {
            href url: getAppEndpointUrl("renderMetricData"), style: (isST() ? "embedded" : "external"), title: inTS("View the Data shared with Developer", getAppImg("view", true)), description: "Tap to view Data", required: false, image: getAppImg("view")
        }
    }
    if(optOutMetrics != true && state?.isInstalled && state?.serviceConfigured && !state?.resumeConfig) {
        section() { input "sendMetricsNow", "bool", title: inTS("Send Metrics Now?", getAppImg("reset", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("reset") }
        if(sendMetricsNow) { sendInstallData() }
    }
    state?.shownDevSharePage = true
}

Map getDeviceList(isInputEnum=false, filters=[]) {
    Map devMap = [:]
    Map availDevs = state?.echoDeviceMap ?: [:]
    availDevs?.each { key, val->
        if(filters?.size()) {
            if(filters?.contains('tts') && val?.ttsSupport != true) { return }
            if(filters?.contains('announce') && val?.ttsSupport != true && val?.announceSupport != true) { return }
        }
        devMap[key] = val
    }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

Map getAllDevices(isInputEnum=false) {
    Map devMap = [:]
    Map availDevs = state?.allEchoDevices ?: [:]
    availDevs?.each { key, val-> devMap[key] = val }
    return isInputEnum ? (devMap?.size() ? devMap?.collectEntries { [(it?.key):it?.value?.name] } : devMap) : devMap
}

def servPrefPage() {
    Boolean newInstall = (state?.isInstalled != true)
    Boolean resumeConf = (state?.resumeConfig == true)
    return dynamicPage(name: "servPrefPage", install: (newInstall || resumeConf), nextPage: (!(newInstall || resumeConf) ? "mainPage" : ""), uninstall: (state?.serviceConfigured != true)) {
        Boolean hasChild = ((isST() ? app?.getChildDevices(true) : getChildDevices())?.size())
        Boolean onHeroku = (isST() || settings?.useHeroku != false)

        if(state?.generatedHerokuName) { section() { paragraph title: "Heroku Name:", pTS("${!isST() ? "Heroku Name:\n" : ""}${state?.generatedHerokuName}", null, false, "orange"), state: "complete" }; }
        if(!isST() && settings?.useHeroku == null) settingUpdate("useHeroku", "true", "bool")
        if(settings?.amazonDomain == null) settingUpdate("amazonDomain", "amazon.com", "enum")
        if(settings?.regionLocale == null) settingUpdate("regionLocale", "en-US", "enum")

        if(!state?.serviceConfigured) {
            if(!isST()) {
                section(sTS("Server Deployment Option:")) {
                    input "useHeroku", "bool", title: inTS("Deploy server to Heroku?", getAppImg("heroku", true)), description: "Turn Off to allow local server deployment", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("heroku")
                    if(settings?.useHeroku == false) { paragraph """<p style="color: red;">Local Server deployments are only allowed on Hubitat and are something that can be very difficult for me to support.  I highly recommend Heroku deployments for most users.</p>""" }
                }
            }
            section("") { paragraph "Proceed with the server setup by tapping on Begin Server Setup", state: "complete" }
            srvcPrefOpts(true)
            section(sTS("Deploy the Server:")) {
                href (url: getAppEndpointUrl("config"), style: "external", title: inTS("Begin Server Setup", getAppImg("upload", true)), description: "Tap to proceed", required: false, state: "complete", image: getAppImg("upload"))
            }
        } else {
            if(state?.onHeroku) {
                section(sTS("Server Management:")) {
                    href url: "https://${getRandAppName()}.herokuapp.com/config", style: "external", required: false, title: inTS("Amazon Login Page", getAppImg("amazon_orange", true)), description: "Tap to proceed", image: getAppImg("amazon_orange")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/settings", style: "external", required: false, title: inTS("Heroku App Settings", getAppImg("heroku", true)), description: "Tap to proceed", image: getAppImg("heroku")
                    href url: "https://dashboard.heroku.com/apps/${getRandAppName()}/logs", style: "external", required: false, title: inTS("Heroku App Logs", getAppImg("heroku", true)), description: "Tap to proceed", image: getAppImg("heroku")
                }
            }
            if(state?.isLocal) {
                section(sTS("Local Server Management:")) {
                    href url: "${getServerHostURL()}/config", style: "external", required: false, title: inTS("Amazon Login Page", getAppImg("amazon_orange", true)), description: "Tap to proceed", image: getAppImg("amazon_orange")
                }
            }
            if(state?.authValid) {
                section(sTS("Cookie Management:")) {
                    if(state?.lastCookieRefresh) { paragraph pTS("Cookie Date:\n \u2022 (${parseFmtDt("E MMM dd HH:mm:ss z yyyy", "MM/dd/yyyy HH:mm a" ,state?.lastCookieRefresh)})", null, false, "#2784D9"), state: "complete" }
                    input "refreshCookie", "bool", title: inTS("Refresh Alexa Cookie?", getAppImg("reset", true)), description: "This will Refresh your Amazon Cookie.", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("reset")
                    paragraph pTS("Notice:\nRunning this too fast back to back will actually cause it to clear your cookie.  Run at a max of once every 24 hours.", null, false, "#2784D9")
                    if(refreshCookie) { runCookieRefresh() }
                }
            }
            srvcPrefOpts()
        }
        section(sTS("Reset Options (Tap to view):"), hideable:true, hidden: true) {
            input "resetService", "bool", title: inTS("Reset Service Data?", getAppImg("reset", true)), description: "This will clear all references to the current server and allow you to redeploy a new instance.\nLeave the page and come back after toggling.",
                required: false, defaultValue: false, submitOnChange: true, image: getAppImg("reset")
            input "resetCookies", "bool", title: inTS("Clear Stored Cookie Data?", getAppImg("reset", true)), description: "This will clear all stored cookie data.", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("reset")
            if(settings?.resetService) { clearCloudConfig() }
            if(settings?.resetCookies) { clearCookieData() }
        }
        state?.resumeConfig = false
    }
}

def srvcPrefOpts(pre=false) {
    section(sTS("${pre ? "Required " : ""}Amazon Region Settings${state?.serviceConfigured ? " (Tap to view)" : ""}"), hideable: state?.serviceConfigured, hidden: state?.serviceConfigured) {
        input "amazonDomain", "enum", title: inTS("Select your Amazon Domain?", getAppImg("amazon_orange", true)), description: "", required: true, defaultValue: "amazon.com", options: amazonDomainOpts(), submitOnChange: true, image: getAppImg("amazon_orange")
        input "regionLocale", "enum", title: inTS("Select your Locale?", getAppImg("www", true)), description: "", required: true, defaultValue: "en-US", options: localeOpts(), submitOnChange: true, image: getAppImg("www")
    }
}

def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
        Integer pollWait = 900
        Integer pollMsgWait = 3600
        Integer updNotifyWait = 7200
        section("") {
            paragraph title: "Notice:", pTS("The settings configure here are used by both the App and the Devices.", getAppImg("info", true), true, "#2784D9"), state: "complete"
        }
        section(sTS("Push Messages:")) {
            input "usePush", "bool", title: inTS("Send Push Notitifications\n(Optional)", getAppImg("notification", true)), required: false, submitOnChange: true, defaultValue: false, image: getAppImg("notification")
        }
        section(sTS("SMS Text Messaging:")) {
            paragraph "To send to multiple numbers separate the number by a comma\nE.g. 8045551122,8046663344"
            input "smsNumbers", "text", title: inTS("Send SMS to Text to...\n(Optional)", getAppImg("sms_phone", true)), required: false, submitOnChange: true, image: getAppImg("sms_phone")
        }
        section(sTS("Pushover Support:")) {
            input ("pushoverEnabled", "bool", title: inTS("Use Pushover Integration", getAppImg("pushover", true)), description: "requires Pushover Manager app.", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("pushover"))
            if(settings?.pushoverEnabled == true) {
                if(state?.isInstalled) {
                    if(!state?.pushoverManager) {
                        paragraph "If this is the first time enabling Pushover than leave this page and come back if the devices list is empty"
                        pushover_init()
                    } else {
                        input "pushoverDevices", "enum", title: inTS("Select Pushover Devices"), description: "Tap to select", groupedOptions: getPushoverDevices(), multiple: true, required: false, submitOnChange: true
                        if(settings?.pushoverDevices) {
                            def t0 = ["-2":"Lowest", "-1":"Low", "0":"Normal", "1":"High", "2":"Emergency"]
                            input "pushoverPriority", "enum", title: inTS("Notification Priority (Optional)"), description: "Tap to select", defaultValue: "0", required: false, multiple: false, submitOnChange: true, options: t0
                            input "pushoverSound", "enum", title: inTS("Notification Sound (Optional)"), description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: getPushoverSounds()
                        }
                    }
                } else { paragraph "New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the SmartApps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", state: "complete" }
            }
        }
        if(settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) {
            if((settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) && !state?.pushTested && state?.pushoverManager) {
                if(sendMsg("Info", "Push Notification Test Successful. Notifications Enabled for ${app?.label}", true)) {
                    state.pushTested = true
                }
            }
            section(sTS("Notification Restrictions:")) {
                def t1 = getNotifSchedDesc()
                href "setNotificationTimePage", title: inTS("Notification Restrictions", getAppImg("restriction", true)), description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("restriction")
            }
            section(sTS("Missed Poll Alerts:")) {
                input (name: "sendMissedPollMsg", type: "bool", title: inTS("Send Missed Checkin Alerts?", getAppImg("late", true)), defaultValue: true, submitOnChange: true, image: getAppImg("late"))
                if(settings?.sendMissedPollMsg) {
                    def misPollNotifyWaitValDesc = settings?.misPollNotifyWaitVal ?: "Default: 45 Minutes"
                    input (name: "misPollNotifyWaitVal", type: "enum", title: inTS("Time Past the Missed Checkin?", getAppImg("delay_time", true)), required: false, defaultValue: 2700, options: notifValEnum(), submitOnChange: true, image: getAppImg("delay_time"))
                    if(settings?.misPollNotifyWaitVal) { pollWait = settings?.misPollNotifyWaitVal as Integer }

                    def misPollNotifyMsgWaitValDesc = settings?.misPollNotifyMsgWaitVal ?: "Default: 1 Hour"
                    input (name: "misPollNotifyMsgWaitVal", type: "enum", title: inTS("Send Reminder After?", getAppImg("reminder", true)), required: false, defaultValue: 3600, options: notifValEnum(), submitOnChange: true, image: getAppImg("reminder"))
                    if(settings?.misPollNotifyMsgWaitVal) { pollMsgWait = settings?.misPollNotifyMsgWaitVal as Integer }
                }
            }
            section(sTS("Cookie Refresh Alert:")) {
                input (name: "sendCookieRefreshMsg", type: "bool", title: inTS("Send on Refreshed Cookie?", getAppImg("cookie", true)), defaultValue: false, submitOnChange: true, image: getAppImg("cookie"))
            }
            section(sTS("Code Update Alerts:")) {
                input "sendAppUpdateMsg", "bool", title: inTS("Send for Updates...", getAppImg("update", true)), defaultValue: true, submitOnChange: true, image: getAppImg("update")
                if(settings?.sendAppUpdateMsg) {
                    def updNotifyWaitValDesc = settings?.updNotifyWaitVal ?: "Default: 12 Hours"
                    input (name: "updNotifyWaitVal", type: "enum", title: inTS("Send Reminders After?", getAppImg("reminder", true)), required: false, defaultValue: 43200, options: notifValEnum(), submitOnChange: true, image: getAppImg("reminder"))
                    if(settings?.updNotifyWaitVal) { updNotifyWait = settings?.updNotifyWaitVal as Integer }
                }
            }
        } else { state.pushTested = false }
        state.misPollNotifyWaitVal = pollWait
        state.misPollNotifyMsgWaitVal = pollMsgWait
        state.updNotifyWaitVal = updNotifyWait
    }
}

def setNotificationTimePage() {
    dynamicPage(name: "setNotificationTimePage", title: "Prevent Notifications\nDuring these Days, Times or Modes", uninstall: false) {
        Boolean timeReq = (settings["qStartTime"] || settings["qStopTime"]) ? true : false
        section() {
            input "qStartInput", "enum", title: inTS("Starting at", getAppImg("start_time", true)), options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("start_time")
            if(settings["qStartInput"] == "A specific time") {
                input "qStartTime", "time", title: inTS("Start time", getAppImg("start_time", true)), required: timeReq, image: getAppImg("start_time")
            }
            input "qStopInput", "enum", title: inTS("Stopping at", getAppImg("stop_time", true)), options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false, image: getAppImg("stop_time")
            if(settings?."qStopInput" == "A specific time") {
                input "qStopTime", "time", title: inTS("Stop time", getAppImg("stop_time", true)), required: timeReq, image: getAppImg("stop_time")
            }
            input "quietDays", "enum", title: inTS("Only on these days of the week", getAppImg("day_calendar", true)), multiple: true, required: false, image: getAppImg("day_calendar"),
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            input "quietModes", "mode", title: inTS("When these Modes are Active", getAppImg("mode", true)), multiple: true, submitOnChange: true, required: false, image: getAppImg("mode")
        }
    }
}

def uninstallPage() {
    dynamicPage(name: "uninstallPage", title: "Uninstall", uninstall: true) {
        section("") { paragraph "This will uninstall the App and All Child Devices.\n\nPlease make sure that any devices created by this app are removed from any routines/rules/smartapps before tapping Remove." }
        if(isST()) { remove("Remove ${app?.label} and Devices!", "WARNING!!!", "Last Chance to Stop!\nThis action is not reversible\n\nThis App and Devices will be removed") }
    }
}

String bulletItem(String inStr, String strVal) { return "${inStr == "" ? "" : "\n"} \u2022 ${strVal}" }
String dashItem(String inStr, String strVal, newLine=false) { return "${(inStr == "" && !newLine) ? "" : "\n"} - ${strVal}" }

def deviceTestPage() {
    return dynamicPage(name: "deviceTestPage", uninstall: false, install: false) {
        section("") {
            href "speechPage", title: inTS("Speech Test", getAppImg("broadcast", true)), description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("broadcast")
            // href "broadcastPage", title: inTS("Broadcast Test", getAppImg("broadcast", true)), description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("broadcast")
            href "announcePage", title: inTS("Announcement Test", getAppImg("announcement", true)), description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("announcement")
            href "sequencePage", title: inTS("Sequence Creator Test", getAppImg("sequence", true)), description: (t1 ?: "Tap to configure"), state: (t1 ? "complete" : null), image: getAppImg("sequence")
        }
    }
}

def speechPage() {
    return dynamicPage(name: "speechPage", uninstall: false, install: false) {
        section("") {
            Map devs = getDeviceList(true, [tts])
            input "speechTestDevices", "enum", title: inTS("Select Devices to Test the Speech"), description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            if(speechDevices?.size() >= 3) { paragraph "Amazon will Rate Limit more than 3 device commands at a time.  There will be a delay in the other devices but they should play the test after a few seconds", state: null}
            input "speechTestVolume", "number", title: inTS("Speak at this volume"), description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
            input "speechTestRestVolume", "number", title: inTS("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
            input "speechTestMessage", "text", title: inTS("Message to Speak"), defaultValue: "This is a speach test for your Echo speaks device!!!", required: true, submitOnChange: true
        }
        if(settings?.speechTestDevices) {
            section() {
                input "speechTestRun", "bool", title: inTS("Perform the Speech Test?"), description: "", required: false, defaultValue: false, submitOnChange: true
                if(speechTestRun) { executeSpeechTest() }
            }
        }
    }
}

def broadcastPage() {
    return dynamicPage(name: "broadcastPage", uninstall: false, install: false) {
        section("") {
            paragraph "This feature is not supported by all Alexa devices so using unsupported device may cause it not work"
            Map devs = getDeviceList(true, [tts])
            input "broadcastDevices", "enum", title: inTS("Select Devices to Test the Broadcast"), description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            input "broadcastVolume", "number", title: inTS("Broadcast at this volume"), description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
            input "broadcastRestVolume", "number", title: inTS("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
            input "broadcastMessage", "text", title: inTS("Message to broadcast"), defaultValue: "This is a test of the Echo speaks broadcast system!!!", required: true, submitOnChange: true
            input "broadcastParallel", "bool", title: inTS("Execute commands in Parallel?"), description: "", required: false, defaultValue: true, submitOnChange: true
        }
        if(settings?.broadcastDevices) {
            section() {
                input "broadcastRun", "bool", title: inTS("Perform the Broadcast?"), description: "", required: false, defaultValue: false, submitOnChange: true
                if(broadcastRun) { executeBroadcast() }
            }
        }
    }
}

def announcePage() {
    return dynamicPage(name: "announcePage", uninstall: false, install: false) {
        section("") {
            paragraph "This feature is not supported by all Alexa devices so using unsupported device may cause it not work"
            Map devs = getDeviceList(true, [announce])
            if(!announceDevices) {
                input "announceAllDevices", "bool", title: inTS("Test Announcement using All Supported Devices"), defaultValue: false, required: false, submitOnChange: true
            }
            if(!announceAllDevices) {
                input "announceDevices", "enum", title: inTS("Select Devices to Test the Announcement"), description: "Tap to select", options: (devs ? devs?.sort{it?.value} : []), multiple: true, required: false, submitOnChange: true
            }
            if(announceAllDevices || announceDevices) {
                input "announceVolume", "number", title: inTS("Announce at this volume"), description: "Enter number", range: "0..100", defaultValue: 30, required: false, submitOnChange: true
                input "announceRestVolume", "number", title: inTS("Restore to this volume after"), description: "Enter number", range: "0..100", defaultValue: null, required: false, submitOnChange: true
                input "announceMessage", "text", title: inTS("Message to announce"), defaultValue: "This is a test of the Echo speaks announcement system!!!", required: true, submitOnChange: true
            }
        }
        if(settings?.announceDevices || settings?.announceAllDevices) {
            section() {
                input "announceRun", "bool", title: inTS("Perform the Announcement?"), description: "", required: false, defaultValue: false, submitOnChange: true
                if(announceRun) { executeAnnouncement() }
            }
        }
    }
}

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

def sequencePage() {
    return dynamicPage(name: "sequencePage", uninstall: false, install: false) {
        section(sTS("Command Legend:"), hideable: true, hidden: true) {
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
        section(sTS("Sequence Test Config:")) {
            input "sequenceDevice", "device.EchoSpeaksDevice", title: inTS("Select Devices to Test Sequence Command"), description: "Tap to select", multiple: false, required: false, submitOnChange: true
            input "sequenceString", "text", title: inTS("Sequence String to Use"), defaultValue: "", required: false, submitOnChange: true
        }
        if(settings?.sequenceDevice && settings?.sequenceString) {
            section() {
                input "sequenceRun", "bool", title: inTS("Perform the Sequence?"), description: "", required: false, defaultValue: false, submitOnChange: true
                if(sequenceRun) { executeSequence() }
            }
        }
    }
}

Integer getRecheckDelay(Integer msgLen=null, addRandom=false) {
    def random = new Random()
    Integer randomInt = random?.nextInt(5) //Was using 7
    if(!msgLen) { return 30 }
    def v = (msgLen <= 14 ? 1 : (msgLen / 14)) as Integer
    // logTrace("getRecheckDelay($msgLen) | delay: $v + $randomInt")
    return addRandom ? (v + randomInt) : (v < 5 ? 5 : v)
}

private executeSpeechTest() {
    settingUpdate("speechTestRun", "false", "bool")
    String testMsg = settings?.speechTestMessage
    List selectedDevs = settings?.speechTestDevices
    selectedDevs?.each { devSerial->
        def childDev = getChildDeviceBySerial(devSerial)
        if(childDev && childDev?.hasCommand('setVolumeSpeakAndRestore')) {
            childDev?.setVolumeSpeakAndRestore(settings?.speechTestVolume as Integer, testMsg, (settings?.speechTestRestVolume ?: 30))
        } else {
            logError("Speech Test device with serial# (${devSerial} was not located!!!")
        }
    }
}

private executeBroadcast() {
    settingUpdate("broadcastRun", "false", "bool")
    String testMsg = settings?.broadcastMessage
    Map eDevs = state?.echoDeviceMap
    List seqItems = []
    def selectedDevs = settings?.broadcastDevices
    selectedDevs?.each { dev->
        seqItems?.push([command: "volume", value: settings?.broadcastVolume as Integer, serial: dev, type: eDevs[dev]?.type])
        seqItems?.push([command: "speak", value: testMsg, serial: dev, type: eDevs[dev]?.type])
    }
    sendMultiSequenceCommand(seqItems, "broadcastTest", settings?.broadcastParallel)
    // schedules volume restore
    runIn(getRecheckDelay(testMsg?.length()), "broadcastVolumeRestore")
}

private broadcastVolumeRestore() {
    Map eDevs = state?.echoDeviceMap
    def selectedDevs = settings?.broadcastDevices
    List seqItems = []
    selectedDevs?.each { dev-> seqItems?.push([command: "volume", value: (settings?.broadcastRestVolume ?: 30), serial: dev, type: eDevs[dev]?.type]) }
    sendMultiSequenceCommand(seqItems, "broadcastVolumeRestore", settings?.broadcastParallel)
}

private announcementVolumeRestore() {
    Map eDevs = state?.echoDeviceMap
    def selectedDevs = settings?.announceDevices
    List seqItems = []
    selectedDevs?.each { dev-> seqItems?.push([command: "volume", value: (settings?.announceRestVolume ?: 30), serial: dev, type: eDevs[dev]?.type]) }
    sendMultiSequenceCommand(seqItems, "announcementVolumeRestore", settings?.broadcastParallel)
}

private executeAnnouncement() {
    settingUpdate("announceRun", "false", "bool")
    String testMsg = settings?.announceMessage
    List selectedDevs = settings?.announceDevices
    if(settings?.announceAllDevices) {
        def childDev = getChildDeviceByCap("announce")
        if(childDev && childDev?.hasCommand('playAnnouncementAll')) {
            childDev?.playAnnouncementAll(testMsg)
        } else {
            logError("Announcement Test All | A Device with was not found that supports announcements!!!")
        }
    } else {
        Map eDevs = state?.echoDeviceMap
        List seqItems = []
        selectedDevs?.each { dev->
            seqItems?.push([command: "volume", value: settings?.announceRestVolume as Integer, serial: dev, type: eDevs[dev]?.type])
        }
        seqItems?.push([command: "announcementTest", value: testMsg, serial: null, type: null])
        sendMultiSequenceCommand(seqItems, "announcementTest", settings?.broadcastParallel)
    }
    runIn(getRecheckDelay(testMsg?.length()), "announcementVolumeRestore")
}

private executeSequence() {
    settingUpdate("sequenceRun", "false", "bool")
    String seqStr = settings?.sequenceString
    if(settings?.sequenceDevice?.hasCommand("executeSequenceCommand")) {
        settings?.sequenceDevice?.executeSequenceCommand(seqStr as String)
    } else {
        logWarn("sequence test device doesn't support the executeSequenceCommand command...")
    }
}

private executeTuneInSearch() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/tunein/search",
        query: [ query: settings?.tuneinSearchQuery, mediaOwnerCustomerId: state?.deviceOwnerCustomerId ],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json"
    ]
    Map results = makeSyncHttpReq(params, "get", "tuneInSearch") ?: [:]
    return results
}

private executeMusicSearchTest() {
    settingUpdate("performMusicTest", "false", "bool")
    if(settings?.musicTestDevice && settings?.musicTestProvider && settings?.musicTestQuery) {
        if(settings?.musicTestDevice?.hasCommand("searchMusic")) {
            logDebug("Performing ${settings?.musicTestProvider} Search Test with Query: (${settings?.musicTestQuery}) on Device: (${settings?.musicTestDevice})")
            settings?.musicTestDevice?.searchMusic(settings?.musicTestQuery as String, settings?.musicTestProvider as String)
        } else { logError("The Device ${settings?.musicTestDevice} does NOT support the searchMusic() command...") }
    }
}

def musicSearchTestPage() {
    return dynamicPage(name: "musicSearchTestPage", uninstall: false, install: false) {
        section("Test a Music Search on Device:") {
            paragraph "Use this to test the search you discovered above directly on a device.", state: "complete"
            Map testEnum = ["CLOUDPLAYER": "My Library", "AMAZON_MUSIC": "Amazon Music", "I_HEART_RADIO": "iHeartRadio", "PANDORA": "Pandora", "APPLE_MUSIC": "Apple Music", "TUNEIN": "TuneIn", "SIRIUSXM": "siriusXm", "SPOTIFY": "Spotify"]
            input "musicTestProvider", "enum", title: inTS("Select Music Provider to perform test", getAppImg("music", true)), defaultValue: null, required: false, options: testEnum, submitOnChange: true, image: getAppImg("music")
            if(musicTestProvider) {
                input "musicTestQuery", "text", title: inTS("Music Search term to test on Device", getAppImg("search2", true)), defaultValue: null, required: false, submitOnChange: true, image: getAppImg("search2")
                if(settings?.musicTestQuery) {
                    input "musicTestDevice", "device.EchoSpeaksDevice", title: inTS("Select a Device to Test Music Search", getAppImg("echo_speaks.1x", true)), description: "Tap to select", multiple: false, required: false, submitOnChange: true, image: getAppImg("echo_speaks.1x")
                    if(musicTestDevice) {
                        input "performMusicTest", "bool", title: inTS("Perform the Music Search Test?", getAppImg("music", true)), description: "", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("music")
                        if(performMusicTest) { executeMusicSearchTest() }
                    }
                }
            }
        }
        section(sTS("TuneIn Search Results:")) {
            paragraph "Enter a search phrase to query TuneIn to help you find the right search term to use in searchTuneIn() command.", state: "complete"
            input "tuneinSearchQuery", "text", title: inTS("Enter search phrase for TuneIn", getAppImg("tunein", true)), defaultValue: null, required: false, submitOnChange: true, image: getAppImg("tunein")
            if(settings?.tuneinSearchQuery) {
                href "searchTuneInResultsPage", title: inTS("View search results!", getAppImg("search2", true)), description: "Tap to proceed...", image: getAppImg("search2")
            }
        }
    }
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
                                    paragraph title: pTS(item2?.name?.take(75), (onST ? null : item2?.image), false), str, required: true, state: (!item2?.name?.contains("Not Supported") ? "complete" : null), image: item2?.image ?: ""
                                } else { href "searchTuneInResultsPage", title: pTS(item2?.name?.take(75), (onST ? null : item2?.image), false), description: str, required: true, state: (!item2?.name?.contains("Not Supported") ? "complete" : null), image: onST && item2?.image ? item2?.image : null }
                            }
                        } else {
                            String str = ""
                            str += "ContentType: (${item?.contentType})"
                            str += "\nId: (${item?.id})"
                            str += "\nDescription: ${item?.description}"
                            if(onST) {
                                paragraph title: pTS(item?.name?.take(75), (onST ? null : item?.image), false), str, required: true, state: (!item?.name?.contains("Not Supported") ? "complete" : null), image: item?.image ?: ""
                            } else { href "searchTuneInResultsPage", title: pTS(item?.name?.take(75), (onST ? null : item?.image), false), description: str, required: true, state: (!item?.name?.contains("Not Supported") ? "complete" : null), image: onST && item?.image ? item?.image : null }
                        }
                    }
                }
            } else { paragraph "No Results found..." }
        }
    }
}

private getChildDeviceBySerial(String serial) {
    def childDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    return childDevs?.find { it?.deviceNetworkId?.tokenize("|")?.contains(serial) } ?: null
}

public getChildDeviceByCap(String cap) {
    def childDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    return childDevs?.find { it?.currentValue("permissions") && it?.currentValue("permissions")?.toString()?.contains(cap) } ?: null
}

public getDevicesFromList(List ids) {
    def cDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    return cDevs?.findAll { it?.id in ids } ?: null
}

public getDeviceFromId(String id) {
    def cDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    return cDevs?.find { it?.id == id } ?: null
}

public getChildDevicesByCap(String cap) {
    def childDevs = isST() ? app?.getChildDevices(true) : app?.getChildDevices()
    return childDevs?.findAll { it?.currentValue("permissions") && it?.currentValue("permissions")?.toString()?.contains(cap) } ?: null
}

def donationPage() {
    return dynamicPage(name: "donationPage", title: "", nextPage: "mainPage", install: false, uninstall: false) {
        section("") {
            def str = ""
            str += "Hello User, \n\nPlease forgive the interuption but it's been 30 days since you installed/updated this SmartApp and I wanted to present you with this one time reminder that donations are accepted (We do not require them)."
            str += "\n\nIf you have been enjoying the software and devices please remember that we have spent thousand's of hours of our spare time working on features and stability for those applications and devices."
            str += "\n\nIf you have already donated, thank you very much for your support!"
            str += "\n\nIf you are just not interested in donating please ignore this message"

            str += "\n\nThanks again for using Echo Speaks"
            paragraph str, required: true, state: null
            href url: textDonateLink(), style: "external", required: false, title: "Donations", description: "Tap to open in browser", state: "complete", image: getAppImg("donate")
        }
        def iData = atomicState?.installData
        iData["shownDonation"] = true
        atomicState?.installData = iData
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    state?.installData = [initVer: appVersion(), dt: getDtNow().toString(), updatedDt: "Not Set", showDonation: false, sentMetrics: false, shownChgLog: true]
    state?.isInstalled = true
    sendInstallData()
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    if(!state?.isInstalled) { state?.isInstalled = true }
    if(!state?.installData) { state?.installData = [initVer: appVersion(), dt: getDtNow().toString(), updatedDt: getDtNow().toString(), shownDonation: false, sentMetrics: false] }
    unsubscribe()
    unschedule()
    initialize()
}

def initialize() {
    if(app?.getLabel() != "Echo Speaks") { app?.updateLabel("Echo Speaks") }
    if(settings?.optOutMetrics == true && state?.appGuid) { if(removeInstallData()) { state?.appGuid = null } }
    if((settings?.guardHomeAlarm && settings?.guardAwayAlarm) || settings?.guardHomeModes || settings?.guardAwayModes || settings?.guardAwayPresence) {
        if(settings?.guardAwayAlarm && settings?.guardHomeAlarm) {
            subscribe(location, "${!isST() ? "hsmStatus" : "alarmSystemStatus"}", guardTriggerEvtHandler)
        }
        if(settings?.guardAwayModes && settings?.guardHomeModes) {
            subscribe(location, "mode", guardTriggerEvtHandler)
        }
        if(settings?.guardAwayPresence) {
            subscribe(settings?.guardAwayPresence, "presence", guardTriggerEvtHandler)
        }
    }
    if(!state?.resumeConfig) {
        runEvery5Minutes("healthCheck") // This task checks for missed polls, app updates, code version changes, and cloud service health
        appCleanup()
        validateCookie(true)
        runEvery1Minute("getOtherData")
        runEvery10Minutes("getEchoDevices") //This will reload the device list from Amazon
        // runEvery1Minute("getEchoDevices") //This will reload the device list from Amazon
        runIn(15, "reInitChildren")
        getOtherData()
        getEchoDevices()
    }
}

def uninstalled() {
    log.warn "uninstalling app and devices"
    unschedule()
    if(settings?.optOutMetrics != true) { if(removeInstallData()) { state?.appGuid = null } }
    clearCloudConfig()
    clearCookieData()
    removeDevices(true)
}

def getActionApps() {
    return getAllChildApps()?.findAll { it?.name == actChildName() }
}

def onAppTouch(evt) {
    // logTrace("appTouch...")
    updated()
}

void settingUpdate(name, value, type=null) {
    if(name && type) {
        app?.updateSetting("$name", [type: "$type", value: value])
    }
    else if (name && type == null){ app?.updateSetting(name.toString(), value) }
}

void settingRemove(String name) {
    logTrace("settingRemove($name)...")
    if(name && settings?.containsKey(name as String)) { isST() ? app?.deleteSetting(name as String) : app?.removeSetting(name as String) }
}

mappings {
    path("/renderMetricData")           { action: [GET: "renderMetricData"] }
    path("/receiveData")                { action: [POST: "processData"] }
    path("/config")                     { action: [GET: "renderConfig"] }
    path("/textEditor/:cId/:inName")    { action: [GET: "renderTextEditPage", POST: "textEditProcessing"] }
    path("/cookie")                     { action: [GET: "getCookieData", POST: "storeCookieData", DELETE: "clearCookieData"] }
}

String getCookieVal() { return (state?.cookieData && state?.cookieData?.localCookie) ? state?.cookieData?.localCookie as String : null }
String getCsrfVal() { return (state?.cookieData && state?.cookieData?.csrf) ? state?.cookieData?.csrf as String : null }

def clearCloudConfig() {
    logTrace("clearCloudConfig called...")
    settingUpdate("resetService", "false", "bool")
    unschedule("cloudServiceHeartbeat")
    List remItems = ["generatedHerokuName", "useHeroku", "onHeroku", "nodeServiceInfo", "serverHost", "isLocal"]
    remItems?.each { rem->
        state?.remove(rem as String)
    }
    // (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { dev-> dev?.setAuthState(false) }
    state?.serviceConfigured = false
    state?.resumeConfig = true
}

String getEnvParamsStr() {
    Map envParams = [:]
    envParams["smartThingsUrl"] = "${getAppEndpointUrl("receiveData")}"
    envParams["appCallbackUrl"] = "${getAppEndpointUrl("receiveData")}"
    envParams["hubPlatform"] = "${getPlatform()}"
    envParams["useHeroku"] = (isST() || settings?.useHeroku != false)
    envParams["serviceDebug"] = (settings?.serviceDebug == true) ? "true" : "false"
    envParams["serviceTrace"] = (settings?.serviceTrace == true) ? "true" : "false"
    envParams["amazonDomain"] = settings?.amazonDomain as String ?: "amazon.com"
    envParams["regionLocale"] = settings?.regionLocale as String ?: "en-US"
    envParams["hostUrl"] = "${getRandAppName()}.herokuapp.com"
    String envs = ""
    envParams?.each { k, v-> envs += "&env[${k}]=${v}" }
    return envs
}

private checkIfCodeUpdated() {
    if(state?.codeVersions && state?.codeVersions?.mainApp != appVersion()) {
        checkVersionData(true)
        logInfo("Code Version Change! Re-Initializing SmartApp in 5 seconds...")
        state?.pollBlocked = true
        updCodeVerMap("mainApp", appVersion())
        Map iData = atomicState?.installData ?: [:]
        iData["updatedDt"] = getDtNow().toString()
        iData["shownChgLog"] = false
        if(iData?.shownDonation == null) {
            iData["shownDonation"] = false
        }
        atomicState?.installData = iData
        runIn(5, "postCodeUpdated", [overwrite: false])
        return true
    }
    state?.pollBlocked = false
    return false
}

private postCodeUpdated() {
    updated()
    runIn(10, "sendInstallData", [overwrite: false])
}

private appCleanup() {
    List items = ["availableDevices", "lastMsgDt", "consecutiveCmdCnt", "isRateLimiting", "versionData", "heartbeatScheduled", "serviceAuthenticated", "cookie"]
    items?.each { si-> if(state?.containsKey(si as String)) { state?.remove(si)} }
    state?.pollBlocked = false
    state?.resumeConfig = false
    state?.deviceRefreshInProgress = false
    // Settings Cleanup

    List setItems = ["tuneinSearchQuery", "performBroadcast", "performMusicTest", "stHub"]
    settings?.each { si-> if(si?.key?.startsWith("broadcast") || si?.key?.startsWith("musicTest") || si?.key?.startsWith("announce") || si?.key?.startsWith("sequence") || si?.key?.startsWith("speechTest")) { setItems?.push(si?.key as String) } }
    setItems?.each { sI->
        if(settings?.containsKey(sI as String)) { settingRemove(sI as String) }
    }
}

private resetQueues() {
    (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { it?.resetQueue() }
}

private reInitChildren() {
    (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { it?.triggerInitialize() }
    updChildAppVer()
}

private updCodeVerMap(key, val) {
    Map cv = atomicState?.codeVersions ?: [:]
    cv[key as String] = val
    atomicState?.codeVersions = cv
}

String getRandAppName() {
    if(!state?.generatedHerokuName && (!state?.isLocal && !state?.serverHost)) { state?.generatedHerokuName = "${app?.name?.toString().replaceAll(" ", "-")}-${randomString(8)}"?.toLowerCase() }
    return state?.generatedHerokuName as String
}

def processData() {
    // logTrace("processData() | Data: ${request.JSON}")
    Map data = request?.JSON as Map
    if(data) {
        if(data?.version) {
            state?.onHeroku = (isST() || data?.onHeroku == true || data?.onHeroku == null || (!data?.isLocal && settings?.useHeroku != false))
            state?.isLocal = (!isST() && data?.isLocal == true)
            state?.serverHost = (data?.serverUrl ?: null)
            logTrace("processData Received | Version: ${data?.version} | onHeroku: ${data?.onHeroku} | serverUrl: ${data?.serverUrl}")
            updCodeVerMap("server", data?.version)
        } else { log.debug "data: $data" }
    }
    def json = new groovy.json.JsonOutput().toJson([message: "success", version: appVersion()])
    render contentType: "application/json", data: json, status: 200
}

def getCookieData() {
    logTrace("getCookieData() Request Received...")
    Map resp = state?.cookieData ?: [:]
    resp["refreshDt"] = state?.lastCookieRefresh ?: null
    def json = new groovy.json.JsonOutput().toJson(resp)
    incrementCntByKey("getCookieCnt")
    render contentType: "application/json", data: json
}

def storeCookieData() {
    logTrace("storeCookieData Request Received...")
    if(request?.JSON && request?.JSON?.cookieData) {
        logTrace("cookieData Received: ${request?.JSON?.cookieData?.keySet()}")
        Map obj = [:]
        request?.JSON?.cookieData?.each { k,v->
            obj[k as String] = v as String
        }
        state?.cookieData = obj
        state?.onHeroku = (isST() || data?.onHeroku == true || data?.onHeroku == null || (!data?.isLocal && settings?.useHeroku != false))
        state?.isLocal = (!isST() && data?.isLocal == true)
        state?.serverHost = request?.JSON?.serverUrl ?: null
        updCodeVerMap("server", request?.JSON?.version)
    }
    // log.debug "csrf: ${state?.cookieData?.csrf}"
    if(state?.cookieData?.localCookie && state?.cookieData?.csrf != null) {
        logInfo("Cookie Data has been Updated... Re-Initializing SmartApp and to restart polling in 10 seconds...")
        validateCookie(true)
        state?.serviceConfigured = true
        state?.lastCookieRefresh = getDtNow()
        runIn(10, "initialize", [overwrite: true])
    }
}

def clearCookieData(src=null) {
    logTrace("clearCookieData(${src ?: ""})")
    settingUpdate("resetCookies", "false", "bool")
    state?.remove("cookie")
    state?.remove("cookieData")
    state?.remove("lastCookieRefresh")
    unschedule("getEchoDevices")
    unschedule("getOtherData")
    logWarn("Cookie Data has been cleared and Device Data Refreshes have been suspended...")
    updateChildAuth(false)
    state?.authValid = false
    // if(getServerHostURL()) { clearServerAuth() }
}

private updateChildAuth(Boolean isValid) {
    (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { it?.setAuthState(isValid, [cookie: getCookieVal(), csrf: getCsrfVal()]) }
}

private authEvtHandler(Boolean isAuth) {
    // log.debug "authEvtHandler(${isAuth})"
    state?.authValid = (isAuth == true)
    if(isAuth == false && !state?.noAuthActive) {
        clearCookieData()
        noAuthReminder()
        sendMsg("${app.name} Amazon Login Issue", "Amazon Cookie Has Expired or is Missing!!! Please login again using the Heroku Web Config page...")
        runEvery1Hour("noAuthReminder")
        state?.noAuthActive = true
        updateChildAuth(isAuth)
    } else {
        if(state?.noAuthActive) {
            unschedule("noAuthReminder")
            state?.noAuthActive = false
            runIn(10, "initialize", [overwrite: true])
        }
    }
}

Boolean isAuthValid(methodName) {
    if(state?.authValid == false) {
        logWarn("Echo Speaks Authentication is no longer valid... Please login again and commands will be allowed again!!! | Method: (${methodName})")
        return false
    }
    return true
}

String toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v?.toString(), "utf-8").replaceAll("\\+", "%20")}" }?.sort().join("&")
}

String getServerHostURL() {
    return (state?.isLocal && state?.serverHost) ? (state?.serverHost ? "${state?.serverHost}" : null) : "https://${getRandAppName()}.herokuapp.com"
}

Integer getLastCookieRefreshSec() { return !state?.lastCookieRefresh ? 100000 : GetTimeDiffSeconds(state?.lastCookieRefresh, "getLastCookieRrshSec").toInteger() }

def clearServerAuth() {
    logDebug("serverUrl: ${getServerHostURL()}")
    Map params = [ uri: getServerHostURL(), path: "/clearAuth" ]
    def execDt = now()
    httpGet(params) { resp->
        // log.debug "resp: ${resp.status} | data: ${resp?.data}"
        if (resp?.status == 200) {
            logInfo("Clear Server Auth Completed... | Process Time: (${execDt ? (now()-execDt) : 0}ms)")
        }
    }
}

private runCookieRefresh() {
    settingUpdate("refreshCookie", "false", "bool")
    if(getLastCookieRefreshSec() < 86400) { log.error "Cookie Refresh is blocked... | Last refresh was less than 24 hours ago."; return; }
    Map params = [
        uri: getServerHostURL(),
        path: "/config",
        contentType: "text/html",
        requestContentType: "text/html"
    ]
    execAsyncCmd("get", "wakeUpServerResp", params, [execDt: now()])
}

def wakeUpServerResp(response, data) {
    Boolean hasErr = (response?.hasError() == true)
    String errMsg = (hasErr && response?.getErrorMessage()) ? response?.getErrorMessage() : null
    if(!respIsValid(response?.status, hasErr, errMsg, "wakeUpServerResp")) {return}
    def rData = null
    try { rData = response?.data ?: null }
    catch(ex) { logError("wakeUpServerResp Exception: ${ex?.message}") }
    if (rData) {
        // log.debug "rData: $rData"
        logInfo("wakeUpServer Completed... | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms)")
        Map cookieData = state?.cookieData ?: [:]
        if (!cookieData || !cookieData?.loginCookie || !cookieData?.refreshToken) {
            logError("Required Registration data is missing for Cookie Refresh")
            return
        }
        Map params = [
            uri: getServerHostURL(),
            path: "/refreshCookie"
        ]
        execAsyncCmd("get", "cookieRefreshResp", params, [execDt: now()])
    }
}

def cookieRefreshResp(response, data) {
    Boolean hasErr = (response?.hasError() == true)
    String errMsg = (hasErr && response?.getErrorMessage()) ? response?.getErrorMessage() : null
    if(!respIsValid(response?.status, hasErr, errMsg, "cookieRefreshResp")) {return}
    Map rData = null
    try { rData = response?.data ? response?.json ?: [:] : [:] }
    catch(ex) { logError("cookieRefreshResp Exception: ${ex}") }
    // log.debug "rData: $rData"
    if (rData && rData?.result && rData?.result?.size()) {
        logInfo("Amazon Cookie Refresh Completed | Process Time: (${data?.execDt ? (now()-data?.execDt) : 0}ms)")
        if(settings?.sendCookieRefreshMsg == true) { sendMsg("${app.name} Cookie Refresh", "Amazon Cookie was Refreshed Successfully!!!") }
        // log.debug "refreshAlexaCookie Response: ${rData?.result}"
    }
}

private apiHealthCheck(frc=false) {
    try {
        Map params = [uri: getAmazonUrl(), path: "/api/ping", query: ["_": ""], headers: [cookie: getCookieVal(), csrf: getCsrfVal()], contentType: "plain/text"]
        httpGet(params) { resp->
            logDebug("API Health Check Resp: (${resp?.getData()})")
            return (resp?.getData().toString() == "healthy")
        }
    } catch(ex) {
        incrementCntByKey("err_app_apiHealthCnt")
        logError("apiHealthCheck() Exception: ${ex.message}")
    }
}

private validateCookie(frc=false) {
    if((!frc && getLastCookieChkSec() <= 1800) || !getCookieVal() || !getCsrfVal()) { return }
    try {
        def params = [uri: getAmazonUrl(), path: "/api/bootstrap", query: ["version": 0], headers: [cookie: getCookieVal(), csrf: getCsrfVal()], contentType: "application/json"]
        execAsyncCmd("get", "cookieValidResp", params, [execDt: now()])
    } catch(ex) {
        incrementCntByKey("err_app_cookieValidCnt")
        logError("validateCookie() Exception: ${ex.message}")
    }
}

def cookieValidResp(response, data) {
    // logTrace("cookieValidResp...")
    if(response?.status == 401) {
        logError("cookieValidResp Status: (${response.status})")
        authValidationEvent(false)
        state?.lastCookieChkDt = getDtNow()
        return
    }
    Map aData = response?.json?.authentication ?: [:]
    Boolean valid = false
    if (aData) {
        if(aData?.customerId) { state?.deviceOwnerCustomerId = aData?.customerId }
        if(aData?.customerName) { state?.customerName = aData?.customerName }
        valid = (resp?.data?.authentication?.authenticated != false)
    }
    state?.lastCookieChkDt = getDtNow()
    def execTime = data?.execDt ? (now()-data?.execDt) : 0
    logDebug("Cookie Validation: (${valid}) | Process Time: (${execTime}ms)")
    authValidationEvent(valid)
}

private authValidationEvent(valid) {
	Integer listSize = 3
    List eList = atomicState?.authValidHistory ?: [true, true, true]
    eList.push(valid)
	if(eList?.size() > listSize) { eList = eList?.drop( (eList?.size()-listSize)+1 ) }
	atomicState?.authValidHistory = eList
    if(eList?.every { it == false }) {
        logError("The last 3 Authentication Validations have failed | Clearing Stored Auth Data | Please login again using the Echo Speaks service...")
        authEvtHandler(false)
        return
    } else { authEvtHandler(true) }
}

private respIsValid(statusCode, Boolean hasErr, errMsg=null, String methodName, Boolean falseOnErr=false) {
    statusCode = statusCode as Integer
    if(!hasErr && statusCode == 200) {
        return true
    } else if(statusCode == 401) {
        authValidationEvent(false)
        return false
    } else {
        if(statusCode > 401 && statusCode < 500) {
            logError("${methodName} Error: ${errMsg ?: null}")
            if(errMsg == "Forbidden") {
                authValidationEvent(false)
                return false
            }
        }
    }
    if(hasErr && falseOnErr) { return false }
    return true
}

private noAuthReminder() { logWarn("Amazon Cookie Has Expired or is Missing!!! Please login again using the Heroku Web Config page...") }

def makeSyncHttpReq(Map params, String method="get", String src, Boolean strInJsonResp=false, Boolean showLogs=false) {
    try {
        "http${method?.toString()?.toLowerCase()?.capitalize()}"(params) { resp ->
            if(showLogs) { log.debug "makeSyncHttpReq(Src: $src) | Status: ${resp?.status}: ${resp?.data}" }
            return resp?.data ?: null
        }
    } catch (ex) {
        if(ex instanceof  groovyx.net.http.ResponseParseException) {
            logError("There was an error while parsing the response: ${ex.message}")
        } else { logError("makeSyncHttpReq(Method: ${method}, Src: ${src}) Exception: ${ex.message}") }
        return null
    }
}

public childInitiatedRefresh() {
    Integer lastRfsh = getLastChildInitRefreshSec()
    if(state?.deviceRefreshInProgress != true && lastRfsh > 120) {
        logDebug("A Child Device is requesting a Device List Refresh...")
        state?.lastChildInitRefreshDt = getDtNow()
        getOtherData()
        runIn(3, "getEchoDevices")
    } else {
        logWarn("childInitiatedRefresh request ignored... Refresh already in progress or it's too soon to refresh again | Last Refresh: (${lastRfsh} seconds)")
    }
}

public updChildAppVer() {
    def actApps = getActionApps()
    if(actApps?.size()) { updCodeVerMap("actionApp", actApps[0]?.appVersion()) }
}

private getEchoDevices() {
    if(!isAuthValid("getEchoDevices")) { return }
    def params = [
        uri: getAmazonUrl(),
        path: "/api/devices-v2/device",
        query: [ cached: true, _: new Date().getTime() ],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    state?.deviceRefreshInProgress = true
    state?.refreshDeviceData = false
    execAsyncCmd("get", "echoDevicesResponse", params, [execDt: now()])
}

private getMusicProviders() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/entities",
        query: [ skillId: "amzn1.ask.1p.music" ],
        headers: ["Routines-Version": "1.1.210292", cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json"
    ]
    Map items = [:]
    List musicResp = makeSyncHttpReq(params, "get", "getMusicProviders") ?: []
    if(musicResp?.size()) {
        musicResp?.findAll { it?.availability == "AVAILABLE" }?.each { item->
            items[item?.id] = item?.displayName
        }
    }
    // log.debug "Music Providers: ${items}"
    return items
}

private getOtherData() {
    getBluetoothDevices()
    getDoNotDisturb()
    checkGuardSupport()
}

private getBluetoothDevices() {
    // logTrace("getBluetoothDevices")
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/bluetooth",
        query: [cached: true, _: new Date().getTime()],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json"
    ]
    def btResp = makeSyncHttpReq(params, "get", "getBluetoothDevices") ?: null
    state?.bluetoothData = btResp ?: [:]
}

def getBluetoothData(serialNumber) {
    // logTrace("getBluetoothData: ${serialNumber}")
    String curConnName = null
    Map btObjs = [:]
    Map btData = state?.bluetoothData ?: [:]
    Map bluData = btData && btData?.bluetoothStates?.size() ? btData?.bluetoothStates?.find { it?.deviceSerialNumber == serialNumber } : [:]
    if(bluData?.size() && bluData?.pairedDeviceList && bluData?.pairedDeviceList?.size()) {
        def bData = bluData?.pairedDeviceList?.findAll { (it?.deviceClass != "GADGET") }
        bData?.findAll { it?.address != null }?.each {
            btObjs[it?.address as String] = it
            if(it?.connected == true) { curConnName = it?.friendlyName as String }
        }
    }
    return [btObjs: btObjs, pairedNames: btObjs?.findAll { it?.value?.friendlyName != null }?.collect { it?.value?.friendlyName as String } ?: [], curConnName: curConnName]
}

private getDoNotDisturb() {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/dnd/device-status-list",
        query: [_: new Date().getTime()],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    def dndResp = makeSyncHttpReq(params, "get", "getDoNotDisturb") ?: null
    state?.dndData = dndResp ?: [:]
}

def getDndEnabled(serialNumber) {
    // logTrace("getBluetoothData: ${serialNumber}")
    Map sData = state?.dndData ?: [:]
    def dndData = sData?.doNotDisturbDeviceStatusList?.size() ? sData?.doNotDisturbDeviceStatusList?.find { it?.deviceSerialNumber == serialNumber } : [:]
    return (dndData && dndData?.enabled == true)
}

public def getAlexaRoutines(autoId=null, utterOnly=false) {
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/behaviors/automations${autoId ? "/${autoId}" : ""}",
        query: [ limit: limit ],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json"
    ]

    def routineResp = makeSyncHttpReq(params, "get", "getAlexaRoutines") ?: [:]
    // log.debug "routineResp: $routineResp"
    if(routineResp) {
        if(autoId) {
            return routineResp
        } else {
            Map items = [:]
            Integer cnt = 1
            if(routineResp?.size()) {
                routineResp?.findAll { it?.status == "ENABLED" }?.each { item->
                    if(utterOnly) {
                        if(item?.triggers?.size()) {
                            item?.triggers?.each { trg->
                                if(trg?.payload?.containsKey("utterance") && trg?.payload?.utterance != null) {
                                    items[item?.automationId] = trg?.payload?.utterance as String
                                } else {
                                    items[item?.automationId] = "Unlabeled Routine ($cnt)"
                                    cnt++
                                }
                            }
                        }
                    } else {
                        items[item?.automationId] = item?.name
                    }
                }
            }
            // log.debug "routine items: $items"
            return items
        }
    }
}

def executeRoutineById(String routineId) {
    def execDt = now()
    Map routineData = getAlexaRoutines(routineId)
    if(routineData && routineData?.sequence) {
        sendSequenceCommand("ExecuteRoutine", routineData, null)
        // log.debug "Executed Alexa Routine | Process Time: (${(now()-execDt)}ms) | RoutineId: ${routineId}"
        return true
    } else {
        logError("No Routine Data Returned for ID: (${routineId})")
        return false
    }
}

def checkGuardSupport() {
    if(!isAuthValid("checkGuardSupport")) { return }
    def params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix",
        query: [ cached: true, _: new Date().getTime() ],
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
    ]
    execAsyncCmd("get", "checkGuardSupportResponse", params, [execDt: now()])
}

def checkGuardSupportResponse(response, data) {
    // log.debug "checkGuardSupportResponse Resp Size(${response?.data?.toString()?.size()})"
    //TODO: This will fail on ST platform if the json file size returned is greater than 500Kb
    //TODO: Maybe we can use the server to get the required ID needed to make guard requests
    def resp = parseJson(response?.data?.toString())
    Boolean guardSupported = false
    if(resp && resp?.networkDetail) {
        def details = parseJson(resp?.networkDetail as String)
        def locDetails = details?.locationDetails?.locationDetails?.Default_Location?.amazonBridgeDetails?.amazonBridgeDetails["LambdaBridge_AAA/OnGuardSmartHomeBridgeService"] ?: null
        if(locDetails && locDetails?.applianceDetails && locDetails?.applianceDetails?.applianceDetails) {
            def guardKey = locDetails?.applianceDetails?.applianceDetails?.find { it?.key?.startsWith("AAA_OnGuardSmartHomeBridgeService_") }
            def guardData = locDetails?.applianceDetails?.applianceDetails[guardKey?.key]
            // log.debug "Guard: ${guardData}"
            if(guardData?.modelName == "REDROCK_GUARD_PANEL") {
                state?.guardData = [
                    entityId: guardData?.entityId,
                    applianceId: guardData?.applianceId,
                    friendlyName: guardData?.friendlyName,
                ]
                guardSupported = true
            } else { logError("checkGuardSupportResponse Error | No data received...") }
        }
    } else { logError("checkGuardSupportResponse Error | No data received...") }
    state?.alexaGuardSupported = guardSupported
    if(guardSupported) getGuardState()
}

private getGuardState() {
    if(!isAuthValid("getGuardState")) { return }
    if(!state?.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return; }
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix/state",
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
        body: [ stateRequests: [ [entityId: state?.guardData?.applianceId, entityType: "APPLIANCE" ] ] ]
    ]
    try {
        httpPost(params) { resp ->
            Map respData = resp?.data ?: null
            if(respData && respData?.deviceStates && respData?.deviceStates[0] && respData?.deviceStates[0]?.capabilityStates) {
                def guardStateData = parseJson(respData?.deviceStates[0]?.capabilityStates as String)
                state?.alexaGuardState = guardStateData?.value[0] ? guardStateData?.value[0] : guardStateData?.value
                settingUpdate("alexaGuardAwayToggle", ((state?.alexaGuardState == "ARMED_AWAY") ? "true" : "false"), "bool")
                logDebug("Alexa Guard State: (${state?.alexaGuardState})")
            }
            // log.debug "GuardState resp: ${respData}"
        }
    } catch (ex) {
        if(ex instanceof  groovyx.net.http.ResponseParseException) {
            logError("There was an error while parsing the response: ${ex.message}")
        } else { logError("makeSyncHttpReq(Method: ${method}, Src: ${src}) Exception: ${ex.message}") }
    }
}

private setGuardState(guardState) {
    if(!isAuthValid("setGuardState")) { return }
    if(!state?.alexaGuardSupported) { logError("Alexa Guard is either not enabled. or not supported by any of your devices"); return; }
    guardState = guardStateConv(guardState)
    logDebug("setAlexaGuard($guardState)")
    Map params = [
        uri: getAmazonUrl(),
        path: "/api/phoenix/state",
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
        body: [ controlRequests: [ [ entityId: state?.guardData?.applianceId, entityType: "APPLIANCE", parameters: [action: "controlSecurityPanel", armState: guardState ] ] ] ]
    ]
    execAsyncCmd("put", "setGuardStateResponse", params, [execDt: now(), requestedState: guardState ])
}

private guardStateConv(gState) {
    switch(gState) {
        case "disarm":
        case "off":
        case "stay":
        case "home":
        case "ARMED_STAY":
            return "ARMED_STAY"
        case "away":
        case "ARMED_AWAY":
            return "ARMED_AWAY"
        default:
            return "ARMED_STAY"
    }
}

def setGuardStateResponse(response, data) {
    def resp = response?.json
    // log.debug "resp: ${resp}"
    if(resp && !resp?.errors?.size() && resp?.controlResponses && resp?.controlResponses[0] && resp?.controlResponses[0]?.code && resp?.controlResponses[0]?.code == "SUCCESS") {
        logInfo("Alexa Guard set to (${data?.requestedState}) Successfully!!!")
        state?.alexaGuardState = data?.requestedState
    } else { logError("Failed to set Alexa Guard to (${data?.requestedState}) | Reason: ${resp?.errors ?: null}") }
}

String getAlexaGuardStatus() {
    return state?.alexaGuardState ?: null
}

Boolean getAlexaGuardSupported() {
    return (state?.alexaGuardSupported == true) ? true : false
}

public setGuardHome() {
    setGuardState("ARMED_STAY")
}

public setGuardAway() {
    setGuardState("ARMED_AWAY")
}

Map isFamilyAllowed(String family) {
    Map famMap = getDeviceFamilyMap()
    if(family in famMap?.block) { return [ok: false, reason: "Family Blocked"] }
    if(family in famMap?.echo) { return [ok: true, reason: "Amazon Echos Allowed"] }
    if(family in famMap?.tablet) {
        if(settings?.createTablets == true) { return [ok: true, reason: "Tablets Enabled"] }
        return [ok: false, reason: "Tablets Not Enabled"]
    }
    if(family in famMap?.wha) {
        if(settings?.createWHA == true) { return [ok: true, reason: "WHA Enabled"] }
        return [ok: false, reason: "WHA Devices Not Enabled"]
    }
    if(settings?.createOtherDevices == true) {
        return [ok: true, reason: "Other Devices Enabled"]
    } else { return [ok: false, reason: "Other Devices Not Enabled"] }
    return [ok: false, reason: "Unknown Reason"]
}

def echoDevicesResponse(response, data) {
    List ignoreTypes = getDeviceTypesMap()?.ignore ?: ["A1DL2DVDQVK3Q", "A21Z3CGI8UIP0F", "A2825NDLA7WDZV", "A2IVLV5VM2W81", "A2TF17PFR55MTB", "A1X7HJX9QL16M5", "A2T0P32DY3F7VB", "A3H674413M2EKB", "AILBSA2LNTOYL"]
    List removeKeys = ["appDeviceList", "charging", "macAddress", "deviceTypeFriendlyName", "registrationId", "remainingBatteryLevel", "postalCode", "language"]
    if(response?.status == 401) {
        authValidationEvent(false)
        return
    }
    try {
        // log.debug "json response is: ${response.json}"
        state?.deviceRefreshInProgress=false
        List eDevData = response?.json?.devices ?: []
        Map echoDevices = [:]
        if(eDevData?.size()) {
            eDevData?.each { eDevice->
                String serialNumber = eDevice?.serialNumber;
                // if (!(eDevice?.deviceType in ignoreTypes) && !eDevice?.accountName?.contains("Alexa App") && !eDevice?.accountName?.startsWith("This Device")) {
                if (!(eDevice?.deviceType in ignoreTypes) && !eDevice?.accountName?.startsWith("This Device")) {
                    removeKeys?.each { rk-> eDevice?.remove(rk as String) }
                    if (eDevice?.deviceOwnerCustomerId != null) { state?.deviceOwnerCustomerId = eDevice?.deviceOwnerCustomerId }
                    echoDevices[serialNumber] = eDevice
                }
            }
        }
        // log.debug "echoDevices: ${echoDevices}"
        receiveEventData([echoDevices: echoDevices, musicProviders: getMusicProviders(), execDt: data?.execDt], "Groovy")
    } catch (ex) {
        logError("echoDevicesResponse Exception: ${ex.message}")
    }
}

def receiveEventData(Map evtData, String src) {
    try {
        if(checkIfCodeUpdated()) {
            logWarn("Possible Code Version Change Detected... Device Updates will occur on next cycle.")
            return
        }
        // log.debug "musicProviders: ${evtData?.musicProviders}"
        logTrace("evtData(Keys): ${evtData?.keySet()}")
        if (evtData?.keySet()?.size()) {
            List ignoreTheseDevs = settings?.echoDeviceFilter ?: []
            Boolean onHeroku = (state?.onHeroku == true && state?.isLocal == true)

            //Check for minimum versions before processing
            Map updReqMap = getMinVerUpdsRequired()
            Boolean updRequired = updReqMap?.updRequired
            List updRequiredItems = updReqMap?.updItems

            if (evtData?.echoDevices?.size()) {
                def execTime = evtData?.execDt ? (now()-evtData?.execDt) : 0
                Map echoDeviceMap = [:]
                Map allEchoDevices = [:]
                Map skippedDevices = [:]
                List curDevFamily = []
                Integer cnt = 0
                evtData?.echoDevices?.each { echoKey, echoValue->
                    logTrace("echoDevice | $echoKey | ${echoValue}")
                    // logDebug("echoDevice | ${echoValue?.accountName}", false)
                    allEchoDevices[echoKey] = [name: echoValue?.accountName]
                    // log.debug "name: ${echoValue?.accountName}"
                    Map familyAllowed = isFamilyAllowed(echoValue?.deviceFamily as String)
                    Map deviceStyleData = getDeviceStyle(echoValue?.deviceFamily as String, echoValue?.deviceType as String)
                    // log.debug "deviceStyle: ${deviceStyleData}"
                    Boolean isBlocked = (deviceStyleData?.blocked || familyAllowed?.reason == "Family Blocked")
                    Boolean isInIgnoreInput = (echoValue?.serialNumber in settings?.echoDeviceFilter)
                    Boolean allowTTS = (deviceStyleData?.caps && deviceStyleData?.caps?.contains("t"))
                    Boolean isMediaPlayer = (echoValue?.capabilities?.contains("AUDIO_PLAYER") || echoValue?.capabilities?.contains("AMAZON_MUSIC") || echoValue?.capabilities?.contains("TUNE_IN") || echoValue?.capabilities?.contains("PANDORA") || echoValue?.capabilities?.contains("I_HEART_RADIO") || echoValue?.capabilities?.contains("SPOTIFY"))
                    Boolean volumeSupport = (echoValue?.capabilities.contains("VOLUME_SETTING"))
                    Boolean unsupportedDevice = ((familyAllowed?.ok == false && familyAllowed?.reason == "Unknown Reason") || isBlocked == true)
                    Boolean bypassBlock = (settings?.bypassDeviceBlocks == true && !isInIgnoreInput)

                    if(!bypassBlock && (familyAllowed?.ok == false || isBlocked == true || (!allowTTS && !isMediaPlayer) || isInIgnoreInput)) {
                        logDebug("familyAllowed(${echoValue?.deviceFamily}): ${familyAllowed?.ok} | Reason: ${familyAllowed?.reason} | isBlocked: ${isBlocked} | deviceType: ${echoValue?.deviceType} | tts: ${allowTTS} | volume: ${volumeSupport} | mediaPlayer: ${isMediaPlayer}")
                        if(!skippedDevices?.containsKey(echoValue?.serialNumber as String)) {
                            List reasons = []
                            if(deviceStyleData?.blocked) {
                                reasons?.push("Device Blocked by App Config")
                            } else if(familyAllowed?.reason == "Family Blocked") {
                                reasons?.push("Family Blocked by App Config")
                            } else if (!familyAllowed?.ok) {
                                reasons?.push(familyAllowed?.reason)
                            } else if(isInIgnoreInput) {
                                reasons?.push("In Ignore Device Input")
                                logDebug("skipping ${echoValue?.accountName} because it is in the do not use list...")
                            } else {
                                if(!allowTTS) { reasons?.push("No TTS") }
                                if(!isMediaPlayer) { reasons?.push("No Media Controls") }
                            }
                            skippedDevices[echoValue?.serialNumber as String] = [
                                name: echoValue?.accountName, desc: deviceStyleData?.name, image: deviceStyleData?.image, family: echoValue?.deviceFamily,
                                type: echoValue?.deviceType, tts: allowTTS, volume: volumeSupport, mediaPlayer: isMediaPlayer, reason: reasons?.join(", "),
                                online: echoValue?.online
                            ]
                        }
                        return
                    }

                    echoValue["unsupported"] = (unsupportedDevice == true)
                    echoValue["authValid"] = (state?.authValid == true)
                    echoValue["amazonDomain"] = (settings?.amazonDomain ?: "amazon.com")
                    echoValue["regionLocale"] = (settings?.regionLocale ?: "en-US")
                    echoValue["cookie"] = [cookie: getCookieVal(), csrf: getCsrfVal()]
                    echoValue["deviceAccountId"] = echoValue?.deviceAccountId as String ?: null
                    echoValue["deviceStyle"] = deviceStyleData
                    // log.debug "deviceStyle: ${echoValue?.deviceStyle}"

                    Map permissions = [:]
                    permissions["TTS"] = allowTTS
                    permissions["announce"] = (deviceStyleData?.caps && deviceStyleData?.caps?.contains("a"))
                    permissions["volumeControl"] = volumeSupport
                    permissions["mediaPlayer"] = isMediaPlayer
                    permissions["amazonMusic"] = (echoValue?.capabilities.contains("AMAZON_MUSIC"))
                    permissions["tuneInRadio"] = (echoValue?.capabilities.contains("TUNE_IN"))
                    permissions["iHeartRadio"] = (echoValue?.capabilities.contains("I_HEART_RADIO"))
                    permissions["pandoraRadio"] = (echoValue?.capabilities.contains("PANDORA"))
                    permissions["appleMusic"] = (evtData?.musicProviders.containsKey("APPLE_MUSIC"))
                    permissions["siriusXm"] = (evtData?.musicProviders?.containsKey("SIRIUSXM"))
                    // permissions["tidal"] = true
                    permissions["spotify"] = true //(echoValue?.capabilities.contains("SPOTIFY")) // Temporarily removed restriction check
                    permissions["isMultiroomDevice"] = (echoValue?.clusterMembers && echoValue?.clusterMembers?.size() > 0) ?: false;
                    permissions["isMultiroomMember"] = (echoValue?.parentClusters && echoValue?.parentClusters?.size() > 0) ?: false;
                    permissions["alarms"] = (echoValue?.capabilities.contains("TIMERS_AND_ALARMS"))
                    permissions["reminders"] = (echoValue?.capabilities.contains("REMINDERS"))
                    permissions["doNotDisturb"] = (echoValue?.capabilities?.contains("SLEEP"))
                    permissions["wakeWord"] = (echoValue?.capabilities?.contains("FAR_FIELD_WAKE_WORD"))
                    permissions["flashBriefing"] = (echoValue?.capabilities?.contains("FLASH_BRIEFING"))
                    permissions["microphone"] = (echoValue?.capabilities?.contains("MICROPHONE"))
                    permissions["followUpMode"] = (echoValue?.capabilities?.contains("GOLDFISH"))
                    permissions["connectedHome"] = (echoValue?.capabilities?.contains("SUPPORTS_CONNECTED_HOME"))
                    permissions["bluetoothControl"] = (echoValue?.capabilities.contains("PAIR_BT_SOURCE") || echoValue?.capabilities.contains("PAIR_BT_SINK"))
                    permissions["guardSupported"] = (echoValue?.capabilities?.contains("TUPLE"))
                    echoValue["guardStatus"] = (state?.alexaGuardSupported && state?.alexaGuardState) ? state?.alexaGuardState as String : (permissions?.guardSupported ? "Unknown" : "Not Supported")
                    echoValue["musicProviders"] = evtData?.musicProviders
                    echoValue["permissionMap"] = permissions
                    echoValue["hasClusterMembers"] = (echoValue?.clusterMembers && echoValue?.clusterMembers?.size() > 0) ?: false
                    // logWarn("Device Permisions | Name: ${echoValue?.accountName} | $permissions")

                    echoDeviceMap[echoKey] = [
                        name: echoValue?.accountName, online: echoValue?.online, family: echoValue?.deviceFamily, serialNumber: echoKey,
                        style: echoValue?.deviceStyle, type: echoValue?.deviceType, mediaPlayer: isMediaPlayer, announceSupport: permissions?.announce,
                        ttsSupport: allowTTS, volumeSupport: volumeSupport, clusterMembers: echoValue?.clusterMembers, broadcastSupport: permissions?.broadcast,
                        musicProviders: evtData?.musicProviders?.collect{ it?.value }?.sort()?.join(", "), supported: (unsupportedDevice != true)
                    ]

                    String dni = [app?.id, "echoSpeaks", echoKey].join("|")
                    def childDevice = getChildDevice(dni)
                    String devLabel = "${settings?.addEchoNamePrefix != false ? "Echo - " : ""}${echoValue?.accountName}${echoValue?.deviceFamily == "WHA" ? " (WHA)" : ""}"
                    String childHandlerName = "Echo Speaks Device"
                    Boolean autoRename = (settings?.autoRenameDevices != false)
                    if (!childDevice) {
                        // log.debug "childDevice not found | autoCreateDevices: ${settings?.autoCreateDevices}"
                        if(settings?.autoCreateDevices != false) {
                            try{
                                logInfo("Creating NEW Echo Speaks Device!!! | Device Label: ($devLabel)${(settings?.bypassDeviceBlocks && unsupportedDevice) ? " | (UNSUPPORTED DEVICE)" : "" }")
                                childDevice = addChildDevice("tonesto7", childHandlerName, dni, null, [name: childHandlerName, label: devLabel, completedSetup: true])
                            } catch(ex) {
                                logError("AddDevice Error! | ${ex.message}")
                            }
                        }
                    } else {
                        //Check and see if name needs a refresh
                        String curLbl = childDevice?.getLabel()
                        if(autoRename && childDevice?.name as String != childHandlerName) { childDevice?.name = childHandlerName as String }
                        // log.debug "curLbl: ${curLbl} | newLbl: ${devLabel} | autoRename: ${autoRename}"
                        if(autoRename && (curLbl != devLabel)) {
                            logDebug("Amazon Device Name Change Detected... Updating Device Name to (${devLabel}) | Old Name: (${curLbl})")
                            childDevice?.setLabel(devLabel as String)
                        }
                        // logInfo("Sending Device Data Update to ${devLabel} | Last Updated (${getLastDevicePollSec()}sec ago)")
                        childDevice?.updateDeviceStatus(echoValue)
                        updCodeVerMap("echoDevice", childDevice?.devVersion()) // Update device versions in codeVersions state Map
                    }
                    curDevFamily.push(echoValue?.deviceStyle?.name)
                }
                logDebug("Device Data Received and Updated for (${echoDeviceMap?.size()}) Alexa Devices | Took: (${execTime}ms) | Last Refreshed: (${(getLastDevicePollSec()/60).toFloat()?.round(1)} minutes)")
                state?.lastDevDataUpd = getDtNow()
                state?.echoDeviceMap = echoDeviceMap
                state?.allEchoDevices = allEchoDevices
                state?.skippedDevices = skippedDevices
                state?.deviceStyleCnts = curDevFamily?.countBy { it }
            } else {
                log.warn "No Echo Device Data Sent... This may be the first transmission from the service after it started up!"
            }
            if(updRequired) {
                logWarn("CODE UPDATES REQUIRED: Echo Speaks Integration may not function until the following items are ALL Updated ${updRequiredItems}...")
                appUpdateNotify()
            }
            if(state?.installData?.sentMetrics != true) { runIn(900, "sendInstallData", [overwrite: false]) }
        }
    } catch(ex) {
        logError("receiveEventData Error: ${ex.message}")
        incrementCntByKey("appErrorCnt")
    }
}

private Map getMinVerUpdsRequired() {
    Boolean updRequired = false
    List updItems = []
    ["server":"Echo Speaks Server", "echoDevice":"Echo Speaks Device", "actionApp":"Echo Speaks Actions"]?.each { k,v->
        Map codeVers = state?.codeVersions
        if(codeVers && codeVers[k as String] && (versionStr2Int(codeVers[k as String]) < minVersions()[k as String])) {
            updRequired = true
            updItems?.push("$v")
        }
    }
    return [updRequired: updRequired, updItems: updItems]
}

public getDeviceStyle(String family, String type) {
    if(!state?.appData || !state?.appData?.deviceSupport) { checkVersionData(true) }
    Map typeData = state?.appData?.deviceSupport ?: [:]
    if(typeData[type]) {
        return typeData[type]
    } else { return [name: "Echo Unknown $type", image: "unknown", allowTTS: false] }
}

public Map getDeviceFamilyMap() {
    if(!state?.appData || !state?.appData?.deviceFamilies) { checkVersionData(true) }
    return state?.appData?.deviceFamilies ?: [:]
}

public Map getDeviceTypesMap() {
    if(!state?.appData || !state?.appData?.deviceTypes) { checkVersionData(true) }
    return state?.appData?.deviceTypes ?: [:]
}

private getDevicesFromSerialList(serialNumberList) {
    //logTrace("getDevicesFromSerialList called with: ${ serialNumberList}")
    if (serialNumberList == null) {
       logDebug("SerialNumberList is null")
       return;
    }
    def devicesList = serialNumberList.findResults { echoKey ->
        String dni = [app?.id, "echoSpeaks", echoKey].join("|")
        getChildDevice(dni)
    }
    //log.debug "Device list: ${ devicesList}"
    return devicesList
}

// This is called by the device handler to send playback data to cluster members
public sendPlaybackStateToClusterMembers(whaKey, response, data) {
    //logTrace("sendPlaybackStateToClusterMembers: key: ${ whaKey}")
    def echoDeviceMap = state?.echoDeviceMap
    def whaMap = echoDeviceMap[whaKey]
    def clusterMembers = whaMap?.clusterMembers

    if (clusterMembers) {
        def clusterMemberDevices = getDevicesFromSerialList(clusterMembers)
        clusterMemberDevices?.each { it?.getPlaybackStateHandler(response, data, true) }
    } else {
        // The lookup will fail during initial refresh because echoDeviceMap isn't available yet
        //log.debug "sendPlaybackStateToClusterMembers: no data found for ${whaKey} (first refresh?)"
    }
}

public getServiceHostInfo() {
    return (state?.isLocal && state?.serverHost) ? state?.serverHost : null
}

private removeDevices(all=false) {
    try {
        settingUpdate("cleanUpDevices", "false", "bool")
        List devList = getDeviceList(true)?.collect { String dni = [app?.id, "echoSpeaks", it?.key].join("|") }
        def items = app.getChildDevices()?.findResults { (all || (!all && !devList?.contains(it?.deviceNetworkId as String))) ? it?.deviceNetworkId as String : null }
        logWarn("removeDevices(${all ? "all" : ""}) | In Use: (${all ? 0 : devList?.size()}) | Removing: (${items?.size()})")
        if(items?.size() > 0) {
            Boolean isST = isST()
            items?.each {  isST ? deleteChildDevice(it as String, true) : deleteChildDevice(it as String) }
        }
    } catch (ex) { logError("Device Removal Failed: ${ex.message}") }
}

Map sequenceBuilder(cmd, val) {
    def seqJson = null
    if (cmd instanceof Map) {
        seqJson = cmd?.sequence ?: cmd
    } else { seqJson = ["@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": createSequenceNode(cmd, val)] }
    Map seqObj = [behaviorId: (seqJson?.sequenceId ? cmd?.automationId : "PREVIEW"), sequenceJson: new JsonOutput().toJson(seqJson) as String, status: "ENABLED"]
    return seqObj
}

Map multiSequenceBuilder(commands, parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList?.push(createSequenceNode(cmdItem?.command, cmdItem?.value, [serialNumber: cmdItem?.serial, deviceType:cmdItem?.type])) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    Map seqObj = sequenceBuilder(seqJson, null)
    return seqObj
}

Map createSequenceNode(command, value, Map deviceData = [:]) {
    try {
        Map seqNode = [
            "@type": "com.amazon.alexa.behaviors.model.OpaquePayloadOperationNode",
            "operationPayload": [
                "deviceType": deviceData?.deviceType,
                "deviceSerialNumber": deviceData?.serialNumber,
                "locale": (settings?.regionLocale ?: "en-US"),
                "customerId": state?.deviceOwnerCustomerId
            ]
        ]
        switch (command) {
            case "volume":
                seqNode?.type = "Alexa.DeviceControls.Volume"
                seqNode?.operationPayload?.value = value;
                break
            case "speak":
                seqNode?.type = "Alexa.Speak"
                seqNode?.operationPayload?.textToSpeak = value as String
                break
            case "announcementTest":
                seqNode?.type = "AlexaAnnouncement"
                seqNode?.operationPayload?.remove('deviceType')
                seqNode?.operationPayload?.remove('deviceSerialNumber')
                seqNode?.operationPayload?.remove('locale')
                seqNode?.operationPayload?.expireAfter = "PT5S"
                List valObj = (value?.toString()?.contains("::")) ? value?.split("::") : ["Echo Speaks", value as String]
                seqNode?.operationPayload?.content = [[
                    locale: (state?.regionLocale ?: "en-US"),
                    display: [ title: valObj[0], body: valObj[1] as String ],
                    speak: [ type: "text", value: valObj[1] as String ],
                ]]
                List announceDevs = []
                if(settings?.announceDevices) {
                    Map eDevs = state?.echoDeviceMap
                    settings?.announceDevices?.each { dev->
                        announceDevs?.push([deviceTypeId: eDevs[dev]?.type, deviceSerialNumber: dev])
                    }
                }
                seqNode?.operationPayload?.target = [ customerId : state?.deviceOwnerCustomerId, devices: announceDevs ]
                break
            default:
                return
        }
        // log.debug "seqNode: $seqNode"
        return seqNode
    } catch (ex) {
        logError("createSequenceNode Exception: ${ex?.message}")
        return [:]
    }
}

private execAsyncCmd(String method, String callbackHandler, Map params, Map otherData = null) {
    if(method && callbackHandler && params) {
        String m = method?.toString()?.toLowerCase()
        if(isST()) {
            include 'asynchttp_v1'
            asynchttp_v1."${m}"(callbackHandler, params, otherData)
        } else { "asynchttp${m?.capitalize()}"("${callbackHandler}", params, otherData) }
    } else { logError("execAsyncCmd Error | Missing a required parameter") }
}

private sendAmazonCommand(String method, Map params, Map otherData) {
    execAsyncCmd(method, "amazonCommandResp", params, otherData)
}

def amazonCommandResp(response, data) {
    Boolean hasErr = (response?.hasError() == true)
    String errMsg = (hasErr && response?.getErrorMessage()) ? response?.getErrorMessage() : null
    if(!respIsValid(response?.status, hasErr, errMsg, "amazonCommandResp", true)) {return}
    try {} catch (ex) { }
    def resp = response?.data ? response?.getJson() : null
    // logDebug("amazonCommandResp | Status: (${response?.status}) | Response: ${resp} | PassThru-Data: ${data}")
    if(response?.status == 200) {
        logDebug("amazonCommandResp | Status: (${response?.status})${resp != null ? " | Response: ${resp}" : ""} | ${data?.cmdDesc} was Successfully Sent!!!")
    }
}

private sendSequenceCommand(type, command, value) {
    // logTrace("sendSequenceCommand($type) | command: $command | value: $value", true)
    Map seqObj = sequenceBuilder(command, value)
    sendAmazonCommand("post", [
        uri: getAmazonUrl(),
        path: "/api/behaviors/preview",
        headers: [cookie: getCookieVal(), csrf: getCsrfVal()],
        requestContentType: "application/json",
        contentType: "application/json",
        body: seqObj
    ], [cmdDesc: "SequenceCommand (${type})"])
}

private sendMultiSequenceCommand(commands, String srcDesc, Boolean parallel=false) {
    String seqType = parallel ? "ParallelNode" : "SerialNode"
    List nodeList = []
    commands?.each { cmdItem-> nodeList?.push(createSequenceNode(cmdItem?.command, cmdItem?.value, [serialNumber: cmdItem?.serial, deviceType: cmdItem?.type])) }
    Map seqJson = [ "sequence": [ "@type": "com.amazon.alexa.behaviors.model.Sequence", "startNode": [ "@type": "com.amazon.alexa.behaviors.model.${seqType}", "name": null, "nodesToExecute": nodeList ] ] ]
    sendSequenceCommand("${srcDesc} | MultiSequence: ${parallel ? "Parallel" : "Sequential"}", seqJson, null)
}

/******************************************
|    Notification Functions
*******************************************/
String getAmazonDomain() { return settings?.amazonDomain as String }
String getAmazonUrl() {return "https://alexa.${settings?.amazonDomain as String}"}

Map notifValEnum(allowCust = true) {
    Map items = [
        300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes",
        1800:"30 Minutes", 2700:"45 Minutes", 3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    if(allowCust) { items[100000] = "Custom" }
    return items
}

private healthCheck() {
    // logTrace("healthCheck", true)
    checkVersionData()
    if(checkIfCodeUpdated()) {
        logWarn("Code Version Change Detected... Health Check will occur on next cycle.")
        return
    }
    validateCookie()
    if(getLastCookieRefreshSec() > 432000) { runCookieRefresh() }
    if(!getOk2Notify()) { return }
    missPollNotify((settings?.sendMissedPollMsg == true), (state?.misPollNotifyMsgWaitVal ?: 3600))
    appUpdateNotify()
    if(state?.isInstalled && getLastMetricUpdSec() > (3600*24)) { runIn(30, "sendInstallData", [overwrite: true]) }
}

private missPollNotify(Boolean on, Integer wait) {
    logTrace("missPollNotify() | on: ($on) | wait: ($wait) | getLastDevicePollSec: (${getLastDevicePollSec()}) | misPollNotifyWaitVal: (${state?.misPollNotifyWaitVal}) | getLastMisPollMsgSec: (${getLastMisPollMsgSec()})")
    if(!on || !wait || !(getLastDevicePollSec() > (state?.misPollNotifyWaitVal ?: 2700))) { return }
    if(!(getLastMisPollMsgSec() > wait.toInteger())) {
        state?.missPollRepair = false
        return
    } else {
        if(!state?.missPollRepair) {
            state?.missPollRepair = true
            initialize()
            return
        }
        state?.missPollRepair = true
        String msg = ""
        if(state?.authValid) {
            msg = "\nThe Echo Speaks app has NOT received any device data from Amazon in the last (${getLastDevicePollSec()}) seconds.\nThere maybe an issue with the scheduling.  Please open the app and press Done/Save."
        } else { msg = "\nThe Amazon login info has expired!\nPlease open the heroku amazon authentication page and login again to restore normal operation." }
        logWarn("${msg.toString().replaceAll("\n", " ")}")
        if(sendMsg("${app.name} ${state?.authValid ? "Data Refresh Issue" : "Amazon Login Issue"}", msg)) {
            state?.lastMisPollMsgDt = getDtNow()
        }
        if(state?.authValid) {
            (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { cd-> cd?.sendEvent(name: "DeviceWatch-DeviceStatus", value: "offline", displayed: true, isStateChange: true) }
        }
    }
}

private appUpdateNotify() {
    Boolean on = (settings?.sendAppUpdateMsg != false)
    Boolean appUpd = isAppUpdateAvail()
    Boolean actUpd = isActionAppUpdateAvail()
    Boolean echoDevUpd = isEchoDevUpdateAvail()
    Boolean servUpd = isServerUpdateAvail()
    logDebug("appUpdateNotify() | on: (${on}) | appUpd: (${appUpd}) | actUpd: (${appUpd}) | echoDevUpd: (${echoDevUpd}) | servUpd: (${servUpd}) | getLastUpdMsgSec: ${getLastUpdMsgSec()} | state?.updNotifyWaitVal: ${state?.updNotifyWaitVal}")
    if(getLastUpdMsgSec() > state?.updNotifyWaitVal.toInteger()) {
        if(on && (appUpd || actUpd || echoDevUpd || servUpd)) {
            state?.updateAvailable = true
            def str = ""
            str += !appUpd ? "" : "\nEcho Speaks App: v${state?.appData?.versions?.mainApp?.ver?.toString()}"
            str += !actUpd ? "" : "\nEcho Speaks - Actions: v${state?.appData?.versions?.actionApp?.ver?.toString()}"
            str += !echoDevUpd ? "" : "\nEcho Speaks Device: v${state?.appData?.versions?.echoDevice?.ver?.toString()}"
            str += !servUpd ? "" : "\n${state?.onHeroku ? "Heroku Service" : "Node Service"}: v${state?.appData?.versions?.server?.ver?.toString()}"
            sendMsg("Info", "Echo Speaks Update(s) are Available:${str}...\n\nPlease visit the IDE to Update your code...")
            state?.lastUpdMsgDt = getDtNow()
            return
        }
        state?.updateAvailable = false
    }
}

private List codeUpdateItems() {
    Boolean appUpd = isAppUpdateAvail()
    Boolean actUpd = isActionAppUpdateAvail()
    Boolean devUpd = isEchoDevUpdateAvail()
    Boolean servUpd = isServerUpdateAvail()
    List updItems = []
    if(appUpd || actUpd || devUpd || servUpd) {
        if(appUpd) updItems.push("\nEcho Speaks App: (v${state?.appData?.versions?.mainApp?.ver?.toString()})")
        if(actUpd) updItems.push("\nEcho Speaks - Actions: (v${state?.appData?.versions?.actionApp?.ver?.toString()})")
        if(devUpd) updItems.push("\nEcho Speaks Device: (v${state?.appData?.versions?.echoDevice?.ver?.toString()})")
        if(servUpd) updItems.push("\nServer: (v${state?.appData?.versions?.server?.ver?.toString()})")
    }
    return updItems
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
    logDebug("getOk2Notify() | smsOk: $smsOk | pushOk: $pushOk | pushOver: $pushOver || daysOk: $daysOk | timeOk: $timeOk | modesOk: $modesOk")
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
        // log.debug "quietTimeOk | Start: ${strtTime} | Stop: ${stopTime}"
        if(!isST()) {
            strtTime = toDateTime(strtTime)
            stopTime = toDateTime(stopTime)
        }
        return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
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
    logTrace("sendMsg() | msgTitle: ${msgTitle}, msg: ${msg}, showEvt: ${showEvt}")
    String sentstr = "Push"
    Boolean sent = false
    try {
        String newMsg = "${msgTitle}: ${msg}"
        String flatMsg = newMsg.toString().replaceAll("\n", " ")
        if(!getOk2Notify()) {
            logInfo("sendMsg: Message Skipped During Quiet Time ($flatMsg)")
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
                logDebug("sendMsg: Sent ${sentstr} (${flatMsg})")
            }
        }
    } catch (ex) {
        incrementCntByKey("appErrorCnt")
        logError("sendMsg $sentstr Exception: ${ex.message}")
    }
    return sent
}

Boolean childInstallOk() { return (state?.childInstallOkFlag == true) }
String getAppImg(String imgName, frc=false) { return (frc || isST()) ? "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/icons/${imgName}.png" : "" }
String getPublicImg(String imgName, frc=false) { return (frc || isST()) ? "https://raw.githubusercontent.com/tonesto7/SmartThings-tonesto7-public/master/resources/icons/${imgName}.png" : "" }
String sTS(String t, String i = null) { return isST() ? t : """<h3>${i ? """<img src="${i}" width="42"> """ : ""} ${t?.replaceAll("\\n", "<br>")}</h3>""" }
String pTS(String t, String i = null, bold=true, color=null) { return isST() ? t : "${color ? """<div style="color: $color;">""" : ""}${bold ? "<b>" : ""}${i ? """<img src="${i}" width="42"> """ : ""}${t?.replaceAll("\\n", "<br>")}${bold ? "</b>" : ""}${color ? "</div>" : ""}" }
String inTS(String t, String i = null, color=null) { return isST() ? t : """${color ? """<div style="color: $color;">""" : ""}${i ? """<img src="${i}" width="42"> """ : ""} <u>${t?.replaceAll("\\n", " ")}</u>${color ? "</div>" : ""}""" }

String actChildName(){ return "Echo Speaks - Actions" }
String documentationLink() { return "https://tonesto7.github.io/echo-speaks-docs2" }
String textDonateLink() { return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HWBN4LB9NMHZ4" }
String getAppEndpointUrl(subPath)   { return isST() ? "${apiServerUrl("/api/smartapps/installations/${app.id}${subPath ? "/${subPath}" : ""}?access_token=${state.accessToken}")}" : "${getApiServerUrl()}/${getHubUID()}/apps/${app?.id}${subPath ? "/${subPath}" : ""}?access_token=${state?.accessToken}" }
String getLocalEndpointUrl(subPath) { return "${getLocalApiServerUrl()}/apps/${app?.id}${subPath ? "/${subPath}" : ""}?access_token=${state?.accessToken}" }
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
|       Changelog Logic
******************************************/
Boolean showDonationOk() { return (state?.isInstalled && !atomicState?.installData?.shownDonation && getDaysSinceUpdated() >= 30) ? true : false }

Integer getDaysSinceUpdated() {
	def updDt = atomicState?.installData?.updatedDt ?: null
	if(updDt == null || updDt == "Not Set") {
		def iData = atomicState?.installData
		iData["updatedDt"] = getDtNow().toString()
		atomicState?.installData = iData
		return 0
	} else {
        def start = Date.parse("E MMM dd HH:mm:ss z yyyy", updDt)
        def stop = new Date()
        if(start && stop) {	return (stop - start) }
        return 0
    }
}

String changeLogData() { return getWebData([uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/changelog.txt", contentType: "text/plain; charset=UTF-8"], "changelog") }
Boolean showChgLogOk() { return (state?.isInstalled && state?.installData?.shownChgLog != true) }
def changeLogPage() {
    def execTime = now()
    return dynamicPage(name: "changeLogPage", title: "", nextPage: "mainPage", install: false) {
        section() {
            paragraph title: "Release Notes for (v${appVersion()}${isBeta() ? " Beta" : ""}): ", pTS(isST() ? "" : "Release Notes for (v${appVersion()}${isBeta() ? " Beta" : ""}): ", getAppImg("whats_new", true), true), state: "complete", image: getAppImg("whats_new")
            paragraph pTS(changeLogData(), null, false, "gray")
        }
        Map iData = atomicState?.installData ?: [:]
        iData["shownChgLog"] = true
        atomicState?.installData = iData
    }
}

/******************************************
|    METRIC Logic
******************************************/
String getFbMetricsUrl() { return state?.appData?.settings?.database?.metricsUrl ?: "https://echo-speaks-metrics.firebaseio.com" }
Integer getLastMetricUpdSec() { return !state?.lastMetricUpdDt ? 100000 : GetTimeDiffSeconds(state?.lastMetricUpdDt, "getLastMetricUpdSec").toInteger() }
Boolean metricsOk() { (settings?.optOutMetrics != true && state?.appData?.settings?.sendMetrics != false) }
private generateGuid() { if(!state?.appGuid) { state?.appGuid = UUID?.randomUUID().toString() } }
private sendInstallData() { settingUpdate("sendMetricsNow", "false", "bool"); if(metricsOk()) { sendFirebaseData(getFbMetricsUrl(), "/clients/${state?.appGuid}.json", createMetricsDataJson(), "put", "heartbeat"); } }
private removeInstallData() { return removeFirebaseData("/clients/${state?.appGuid}.json") }
private sendFirebaseData(url, path, data, cmdType=null, type=null) {
    logTrace("sendFirebaseData(${path}, ${data}, $cmdType, $type")
    return queueFirebaseData(url, path, data, cmdType, type)
}
def queueFirebaseData(url, path, data, cmdType=null, type=null) {
    logTrace("queueFirebaseData(${path}, ${data}, $cmdType, $type")
    Boolean result = false
    def json = new groovy.json.JsonOutput().prettyPrint(data)
    Map params = [uri: url as String, path: path as String, requestContentType: "application/json", contentType: "application/json", body: json.toString()]
    String typeDesc = type ? type as String : "Data"
    try {
        if(!cmdType || cmdType == "put") {
            execAsyncCmd(cmdType, "processFirebaseResponse", params, [type: typeDesc])
            result = true
        } else if (cmdType == "post") {
            execAsyncCmd(cmdType, "processFirebaseResponse", params, [type: typeDesc])
            result = true
        } else { logWarn("queueFirebaseData UNKNOWN cmdType: ${cmdType}") }

    } catch(ex) { logError("queueFirebaseData (type: $typeDesc) Exception: ${ex.message}") }
    return result
}

def removeFirebaseData(pathVal) {
    logTrace("removeFirebaseData(${pathVal})")
    Boolean result = true
    try {
        httpDelete(uri: getFbMetricsUrl(), path: pathVal as String) { resp ->
            logDebug("Remove Firebase | resp: ${resp?.status}")
        }
    } catch (ex) {
        if(ex instanceof groovyx.net.http.ResponseParseException) {
            logError("removeFirebaseData: Response: ${ex?.message}")
        } else {
            logError("removeFirebaseData: Response: ${ex?.message}")
            result = false
        }
    }
    return result
}

def processFirebaseResponse(resp, data) {
    logTrace("processFirebaseResponse(${data?.type})")
    Boolean result = false
    String typeDesc = data?.type as String
    try {
        if(resp?.status == 200) {
            logDebug("processFirebaseResponse: ${typeDesc} Data Sent SUCCESSFULLY")
            if(typeDesc?.toString() == "heartbeat") { state?.lastMetricUpdDt = getDtNow() }
            def iData = atomicState?.installData ?: [:]
            iData["sentMetrics"] = true
            atomicState?.installData = iData
            result = true
        } else if(resp?.status == 400) {
            logError("processFirebaseResponse: 'Bad Request': ${resp?.status}")
        } else { logWarn("processFirebaseResponse: 'Unexpected' Response: ${resp?.status}") }
        if (isST() && resp?.hasError()) { logError("processFirebaseResponse: errorData: ${resp?.errorData} | errorMessage: ${resp?.errorMessage}") }
    } catch(ex) {
        logError("processFirebaseResponse (type: $typeDesc) Exception: ${ex.message}")
    }
}

def renderMetricData() {
    try {
        def json = new groovy.json.JsonOutput().prettyPrint(createMetricsDataJson())
        render contentType: "application/json", data: json
    } catch (ex) { logError("renderMetricData Exception: ${ex.message}") }
}

private Map getSkippedDevsAnon() {
    Map res = [:]
    Map sDevs = state?.skippedDevices ?: [:]
    sDevs?.each { k, v-> if(!res?.containsKey(v?.type)) { res[v?.type] = v } }
    return res
}

private createMetricsDataJson(rendAsMap=false) {
    try {
        generateGuid()
        Map swVer = state?.codeVersions
        Map deviceUsageMap = [:]
        Map deviceErrorMap = [:]
        (isST() ? app?.getChildDevices(true) : getChildDevices())?.each { d->
            Map obj = d?.getDeviceMetrics()
            if(obj?.usage?.size()) { obj?.usage?.each { k,v-> deviceUsageMap[k as String] = (deviceUsageMap[k as String] ? deviceUsageMap[k as String] + v : v) } }
            if(obj?.errors?.size()) { obj?.errors?.each { k,v-> deviceErrorMap[k as String] = (deviceErrorMap[k as String] ? deviceErrorMap[k as String] + v : v) } }
        }
        def dataObj = [
            guid: state?.appGuid,
            datetime: getDtNow()?.toString(),
            installDt: state?.installData?.dt,
            updatedDt: state?.installData?.updatedDt,
            timeZone: location?.timeZone?.ID?.toString(),
            hubPlatform: getPlatform(),
            authValid: (state?.authValid == true),
            stateUsage: "${stateSizePerc()}%",
            amazonDomain: settings?.amazonDomain,
            serverPlatform: state?.onHeroku ? "Cloud" : "Local",
            versions: [app: appVersion(), server: swVer?.server ?: "N/A", device: swVer?.echoDevice ?: "N/A"],
            detections: [skippedDevices: getSkippedDevsAnon()],
            counts: [
                deviceStyleCnts: state?.deviceStyleCnts ?: [:],
                appHeartbeatCnt: state?.appHeartbeatCnt ?: 0,
                getCookieCnt: state?.getCookieCnt ?: 0,
                appErrorCnt: state?.appErrorCnt ?: 0,
                deviceErrors: deviceErrorMap ?: [:],
                deviceUsage: deviceUsageMap ?: [:]
            ]
        ]
        def json = new groovy.json.JsonOutput().toJson(dataObj)
        return json
    } catch (ex) {
        logError("createMetricsDataJson: Exception: ${ex.message}")
    }
}

private incrementCntByKey(String key) {
    long evtCnt = state?."${key}" ?: 0
    // evtCnt = evtCnt?.toLong()+1
    evtCnt++
    // logTrace("${key?.toString()?.capitalize()}: $evtCnt", true)
    state?."${key}" = evtCnt?.toLong()
}

/******************************************
|    APP/DEVICE Version Functions
*******************************************/
Boolean isCodeUpdateAvailable(String newVer, String curVer, String type) {
    Boolean result = false
    def latestVer
    if(newVer && curVer) {
        List versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b ->
                List verA = a?.tokenize('.')
                List verB = b?.tokenize('.')
                Integer commonIndices = Math.min(verA?.size(), verB?.size())
                for (int i = 0; i < commonIndices; ++i) {
                    //log.debug "comparing $numA and $numB"
                    if(verA[i]?.toInteger() != verB[i]?.toInteger()) {
                        return verA[i]?.toInteger() <=> verB[i]?.toInteger()
                    }
                }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer) ? true : false
        }
    }
    // logDebug("isCodeUpdateAvailable | type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result")
    return result
}

Boolean isAppUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.mainApp && isCodeUpdateAvailable(state?.appData?.versions?.mainApp?.ver, state?.codeVersions?.mainApp, "main_app")) { return true }
    return false
}

Boolean isActionAppUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.actionApp && isCodeUpdateAvailable(state?.appData?.versions?.actionApp?.ver, state?.codeVersions?.actionApp, "action_app")) { return true }
    return false
}

Boolean isEchoDevUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.echoDevice && isCodeUpdateAvailable(state?.appData?.versions?.echoDevice?.ver, state?.codeVersions?.echoDevice, "dev")) { return true }
    return false
}

Boolean isServerUpdateAvail() {
    if(state?.appData?.versions && state?.codeVersions?.server && isCodeUpdateAvailable(state?.appData?.versions?.server?.ver, state?.codeVersions?.server, "server")) { return true }
    return false
}

Integer versionStr2Int(str) { return str ? str.toString()?.replaceAll("\\.", "")?.toInteger() : null }

private checkVersionData(now = false) { //This reads a JSON file from GitHub with version numbers
    if (now || !state?.appData || (getLastVerUpdSec() > (3600*6))) {
        if(now && (getLastVerUpdSec() < 300)) { return }
        getConfigData()
        getNoticeData()
    }
}

private getConfigData() {
    def params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/${isBeta() ? "beta" : "master"}/resources/appData2.json",
        contentType: "application/json"
    ]
    def data = getWebData(params, "appData", false)
    if(data) {
        state?.appData = data
        state?.lastVerUpdDt = getDtNow()
        logInfo("Successfully Retrieved (v${data?.appDataVer}) of AppData Content from GitHub Repo...")
    }
}

private getNoticeData() {
    def params = [
        uri: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/notices.json",
        contentType: "application/json"
    ]
    def data = getWebData(params, "noticeData", false)
    if(data) {
        state?.noticeData = data
        logInfo("Successfully Retrieved Developer Notices from GitHub Repo...")
    }
}

private getWebData(params, desc, text=true) {
    try {
        // log.trace("getWebData: ${desc} data")
        httpGet(params) { resp ->
            if(resp?.data) {
                if(text) { return resp?.data?.text.toString() }
                return resp?.data
            }
        }
    } catch (ex) {
        incrementCntByKey("appErrorCnt")
        if(ex instanceof groovyx.net.http.HttpResponseException) {
            logWarn("${desc} file not found")
        } else { logError("getWebData(params: $params, desc: $desc, text: $text) Exception: ${ex.message}") }
        return "${label} info not found"
    }
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

def parseFmtDt(parseFmt, newFmt, dt) {
    def newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new SimpleDateFormat(newFmt)
    if(location.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(newDt)
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
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : ""}lastDate: $lastDate): ${ex.message}")
        return 10000
    }
}

/******************************************
|   App Input Description Functions
*******************************************/
String getAppNotifConfDesc() {
    String str = ""
    if(pushStatus()) {
        def ap = getAppNotifDesc()
        def nd = getNotifSchedDesc()
        str += (settings?.usePush) ? bulletItem(str, "Sending via: (Push)") : ""
        str += (settings?.pushoverEnabled) ? bulletItem(str, "Pushover: (Enabled)") : ""
        // str += (settings?.pushoverEnabled && settings?.pushoverPriority) ? bulletItem(str, "Priority: (${settings?.pushoverPriority})") : ""
        // str += (settings?.pushoverEnabled && settings?.pushoverSound) ? bulletItem(str, "Sound: (${settings?.pushoverSound})") : ""
        str += (settings?.phone) ? bulletItem(str, "Sending via: (SMS)") : ""
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
    str += (state?.generatedHerokuName && state?.onHeroku) ? "Heroku: (Configured)\n" : ""
    str += (state?.serviceConfigured && state?.isLocal) ? "Local Server: (Configured)\n" : ""
    str += (settings?.amazonDomain) ? "Domain: (${settings?.amazonDomain})\n" : ""
    str += (state?.lastCookieRefresh) ? "Cookie Date:\n \u2022 (${parseFmtDt("E MMM dd HH:mm:ss z yyyy", "MM/dd/yyyy HH:mm a" ,state?.lastCookieRefresh)})\n" : ""
    return str != "" ? str : null
}

String getAppNotifDesc() {
    def str = ""
    str += settings?.sendMissedPollMsg != false ? bulletItem(str, "Missed Poll Alerts") : ""
    str += settings?.sendAppUpdateMsg != false ? bulletItem(str, "Code Updates") : ""
    str += settings?.sendCookieRefreshMsg == true ? bulletItem(str, "Cookie Refresh") : ""
    return str != "" ? str : null
}

String getActionsDesc() {
    def acts = getActionApps()
    def paused = acts?.findAll { it?.isPaused() == true }
    def active = acts?.findAll { it?.isPaused() != true }
    String str = ""
    str += active?.size() ? "(${active?.size()}) Active\n" : ""
    str += paused?.size() ? "(${paused?.size()}) Paused\n" : ""
    str += active?.size() || paused?.size() ? "\nTap to modify" : "Tap to configure"
    return str
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
    if(state?.onHeroku) {
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

def appInfoSect()	{
    Map codeVer = state?.codeVersions ?: null
    String str = ""
    Boolean isNote = false
    if(codeVer && (codeVer?.server || codeVer?.echoDevice)) {
        str += bulletItem(str, "App: (v${appVersion()})")
        str += (codeVer && codeVer?.echoDevice) ? bulletItem(str, "Device: (v${codeVer?.echoDevice})") : ""
        str += (codeVer && codeVer?.server) ? bulletItem(str, "Server: (v${codeVer?.server})") : ""
        str += (state?.appData && state?.appData?.appDataVer) ? bulletItem(str, "Config: (v${state?.appData?.appDataVer})") : ""
    } else { str += "App: v${appVersion()}" }
    section() {
        href "changeLogPage", title: inTS("${app?.name}", getAppImg("echo_speaks.2x", true)), description: str, image: getAppImg("echo_speaks.2x")
        if(!state?.isInstalled) {
            paragraph pTS("--NEW Install--", null, true, "#2784D9"), state: "complete"
        } else {
            if(!state?.noticeData) { getNoticeData() }
            Map minUpdMap = getMinVerUpdsRequired()
            List codeUpdItems = codeUpdateItems()
            List remDevs = getRemovableDevs()
            if(codeUpdItems?.size()) {
                isNote=true
                String str2 = "Code Updates Available for:"
                codeUpdItems?.each { item-> str2 += bulletItem(str2, item) }
                paragraph pTS(str2, null, false, "#2784D9"), required: true, state: null
            }
            if(minUpdMap?.updRequired) {
                isNote=true
                String str3 = "Updates Required for:"
                minUpdMap?.updItems?.each { item-> str3 += bulletItem(str3, item)  }
                paragraph pTS(str3, null, false, "red"), required: true, state: null
            }
            if(!state?.authValid && !state?.resumeConfig) { isNote = true; paragraph pTS("You are no longer logged in to Amazon.  Please complete the Authentication Process on the Server Login Page!", null, false, "red"), required: true, state: null }
            if(state?.noticeData && state?.noticeData?.notices && state?.noticeData?.notices?.size()) {
                isNote = true
                state?.noticeData?.notices?.each { item-> paragraph pTS(bulletItem(str, item), null, false, "red"), required: true, state: null }
            }
            if(remDevs?.size()) {
                isNote = true
                paragraph pTS("Devices to Remove:\n(${remDevs?.size()}) Devices to be Removed", null, false), required: true, state: null
            }
            if(!isNote) { paragraph pTS("No Issues to Report", null, true) }
        }
    }
}

def getRandomItem(items) {
    def list = new ArrayList<String>();
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()));
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
        if(!state?.accessToken) { state?.accessToken = createAccessToken() }
        else { return true }
    } catch (ex) {
        logError("getAccessToken Exception: ${ex.message}")
        return false
    }
}

def renderConfig() {
    String title = "Echo Speaks"
    Boolean heroku = (isST() || (settings?.useHeroku == null || settings?.useHeroku != false))
    String oStr = !heroku ? """<div id="localServerDiv" class="w-100 mb-3">
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
                    <p id="copyHeroku" class="m-0 p-0">${getRandAppName()?.toString().trim()}</p>
                </div>
            </div>
            <div class="my-2 text-center">
                <h5>2. Tap Button to deploy to Heroku</h5>
                <a href="https://heroku.com/deploy?template=https://github.com/tonesto7/echo-speaks-server/tree/${isBeta() ? "dev" : "master"}${getEnvParamsStr()}">
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
            .btn-rounded {
                border-radius: 50px!important;
            }
            span img {
                width: 48px;
                height: auto;
            }
            span p {
                display: block;
            }
            .all-copy p {
                -webkit-user-select: all;
                -moz-user-select: all;
                -ms-user-select: all;
                user-select: all;
            }
            .nameContainer {
                border-radius: 18px;
                color: rgba(255,255,255,1);
                font-size: 1.5rem;
                background: #666;
                -webkit-box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3) ;
                box-shadow: 1px 1px 1px 0 rgba(0,0,0,0.3) ;
                text-shadow: 1px 1px 1px rgba(0,0,0,0.2) ;
            }
        </style>
    <head>
    <body>
        <div style="margin: 0 auto; max-width: 600px;">
            <form class="p-1">
                <div class="my-3 text-center">
                    <span>
                        <img src="${getAppImg("echo_speaks.1x", true)}"/>
                        <p class="h4 text-center">Echo Speaks</p>
                    </span>
                </div>
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

def renderTextEditPage() {
    String actId = params?.cId
    String inName = params?.inName
    Map inData = [:]
    // log.debug "actId: $actId | inName: $inName"
    if(actId && inName) {
        def actApp = getActionApps()?.find { it?.id == actId }
        if(actApp) { inData = actApp?.getInputData(inName) }
        // inData = getInputData(inName, actId)
    }
    String html = """
        <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta http-equiv="x-ua-compatible" content="ie=edge">
                <title>Echo Speak Text Entry</title>
                <!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"> -->
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.9.0/css/all.min.css">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.8.8/css/mdb.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/codemirror.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/theme/material.min.css" rel="stylesheet">
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vanillatoasts@1.3.0/vanillatoasts.css" integrity="sha256-U06o/6s4HELYo2A3Gd7KPGQMojQiAxY9B8oE/hnM3KU=" crossorigin="anonymous">
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.4/umd/popper.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/js/bootstrap.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/codemirror.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/addon/mode/simple.min.js"></script>
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.48.4/mode/xml/xml.min.js"></script>
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
                                    <button id="newLineBtn" style="border-radius: 50px !important;" class="btn btn-sm btn-outline-info px-1 my-2 mx-3" type="button"><i class="fas fa-plus mr-1"></i>Add New Response Line</button>
                                    <button style="border-radius: 50px !important;" class="btn btn-sm btn-info my-2" type="submit"><i class="fa fa-save mr-1"></i>Submit Responses</button>
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
                                                                <input class="ssml-button" type="button" unselectable="on" value="Date" data-ssml="evtdate">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Time" data-ssml="evttime">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Date/Time" data-ssml="evtdatetime">
                                                                <input class="ssml-button" type="button" unselectable="on" value="Duration" data-ssml="evtduration">
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
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.8.8/js/mdb.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/vanillatoasts@1.3.0/vanillatoasts.min.js"></script>
                <script>
                    let inName = '${inName}';
                    let actId = '${actId}'
                    let rootUrl = '${getAppEndpointUrl("textEditor/${actId}/${inName}")}';
                    let curText = '${inData?.val}';
                    curText = curText == null || curText == 'null' || curText == '' ? '${inData?.template}' : curText
                    let curTitle = '${inData?.title}';
                    let curDesc = '${inData?.desc}';
                    let selectedLineNum = undefined;

                    function cleanTxt(txt) {
                        txt = txt.split(';').map(t => t.trim()).join(';')
                        return txt.endsWith(';') ? txt.replace(/;([^;]*)\$/, '\$1') : txt
                    }

                    \$(document).ready(function() {
                        \$('#inputTitle').text(curTitle);
                        \$('#inputDesc').html(curDesc);
                        \$('#editor').val(cleanTxt(curText));
                        CodeMirror.defineSimpleMode("simplemode", {
                            start: [{
                                regex: /%[a-z]+%/,
                                token: "variable"
                            }, {
                                regex: /<[^>]+>/,
                                token: 'variable-3'
                            }]
                        });

                        let editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
                            theme: 'material',
                            mode: 'simplemode',
                            lineNumbers: true,
                            lineSeparator: ';',
                            showCursorWhenSelecting: true,
                            autoCloseTags: true,
                            lineWrapping: false,
                            autocorrect: false,
                            autocapitalize: false,
                            spellcheck: true
                        });

                        \$('#newLineBtn').click((e) => {
                            let doc = editor.getDoc();
                            let cursor = doc.getCursor();
                            let lineCnt = editor.lineCount();
                            doc.replaceRange(';', CodeMirror.Pos(lineCnt - 1));
                        });

                        function updateInfo(instance, changeObj) {
                            selectedLineNum = changeObj.to.line;
                            // \$('#lineCnt').text('Response Cnt: ' + changeObj.to.line);
                        }

                        editor.on("change", updateInfo);

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
                                        icon: 'https://github.com/tonesto7/echo-speaks/raw/master/resources/icons/echo_speaks.1x.png',
                                        timeout: 4500,
                                        callback: function() { this.hide() }
                                    });
                                }
                            }
                            xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                            console.log(\$('#editor').val());
                            xmlhttp.send(JSON.stringify({
                                val: \$('#editor').val(),
                                name: inName,
                                type: 'text'
                            }));
                        });

                        function insertSsml(editor, str, sTags = true) {
                            let doc = editor.getDoc();
                            let cursor = doc.getCursor();
                            let line = cursor.line;
                            console.log(`lineTxt (\${editor.getLine(line).length}): `, editor.getLine(line))
                            if (editor.getSelection().length > 0) {
                                editor.replaceSelection(` \${str}`);
                            } else {
                                doc.replaceRange(` \${str}`, {
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
                                case 'evttime':
                                    insertSsml(editor, '%time%', false);
                                    break;
                                case 'evtdatetime':
                                    insertSsml(editor, '%datetime%', false);
                                    break;
                                case 'evtduration':
                                    insertSsml(editor, '%duration%', false);
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
    render contentType: "text/html", data: html
}

def textEditProcessing() {
    String actId = params?.cId
    String inName = params?.inName
    // log.debug "POST | actId: $actId | inName: $inName"
    def resp = request?.JSON ?: null
    // log.debug "textEntryProcessing | Resp: $resp"
    def actApp = getActionApps()?.find { it?.id == actId }
    Boolean status = (actApp && actApp?.updateTxtEntry(resp))
    def json = new JsonOutput().toJson([message: (status ? "success" : "failed"), version: appVersion()])
    render contentType: "application/json", data: json, status: 200
}

def getSettingVal(inName) {
    String actionId = params?.cId
    // log.debug "GetSettingVals | actionId: $actionId"
    def actApp = getActionApps()?.find { it?.id == actionId }
    def value = null
    if(actApp) { value = actApp?.getSettingInputVal(inName) }
    return value
}

String getTextEditorPath(cId, inName) {
    return getAppEndpointUrl("textEditor/${cId}/${inName}") as String
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

private Map amazonDomainOpts() {
    return [
        "amazon.com":"Amazon.com",
        "amazon.ca":"Amazon.ca",
        "amazon.co.uk":"amazon.co.uk",
        "amazon.com.au":"amazon.com.au",
        "amazon.de":"Amazon.de",
        "amazon.it":"Amazon.it"
    ]
}
private List localeOpts() { return ["en-US", "en-CA", "de-DE", "en-GB", "it-IT", "en-AU"] }

private getPlatform() {
    def p = "SmartThings"
    if(state?.hubPlatform == null) {
        try { [dummy: "dummyVal"]?.encodeAsJson(); } catch (e) { p = "Hubitat" }
        // p = (location?.hubs[0]?.id?.toString()?.length() > 5) ? "SmartThings" : "Hubitat"
        state?.hubPlatform = p
    }
    // log.debug "hubPlatform: (${state?.hubPlatform})"
    return state?.hubPlatform
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
    return (location?.mode?.toString() in modes)
}

Boolean isInAlarmMode(modes) {
    if(!modes) return false
    return (getAlarmSystemStatus() in modes)
}

String getAlarmSystemName(abbr=false) {
    return isST() ? (abbr ? "SHM" : "Smart Home Monitor") : (abbr ? "HSM" : "Hubitat Safety Monitor")
}

List getAlarmModeOpts() {
    return isST() ? ["off", "stay", "away"] : ["disarm", "armNight", "armHome", "armAway"]
}

String getAlarmSystemStatus() {
    if(isST()) {
        def cur = location.currentState("alarmSystemStatus")?.value
        def inc = getShmIncidents()
        if(inc != null && inc?.size()) { cur = 'alarm_active' }
        return cur ?: "disarmed"
    } else { return location?.hsmStatus ?: "disarmed" }
}

def getShmIncidents() {
    def incidentThreshold = now() - 604800000
    return location.activeIncidents.collect{[date: it?.date?.time, title: it?.getTitle(), message: it?.getMessage(), args: it?.getMessageArgs(), sourceType: it?.getSourceType()]}.findAll{ it?.date >= incidentThreshold } ?: null
}
public setAlarmSystemMode(mode) {
    if(!isST()) {
        switch(mode) {
            case "armAway":
            case "away":
                mode = "armAway"
                break
            case "armHome":
            case "night":
            case "stay":
                mode = "armHome"
                break
            case "disarm":
            case "off":
                mode = "disarm"
                break
        }
    }
    logInfo("Setting the ${getAlarmSystemName()} Mode to (${mode})...")
    sendLocationEvent(name: (isST() ? 'alarmSystemStatus' : 'hsmSetArm'), value: mode.toString())
}

public JsonElementsParser(root) {
    if (root instanceof List) {
        root.collect {
            if (it instanceof Map) { JsonElementsParser(it) }
            else if (it instanceof List) { JsonElementsParser(it) }
            else if (it == null) { null }
            else {
                if(it?.toString()?.startsWith("{") && it?.toString()?.endsWith("}")) { it = JsonElementsParser(parseJson(it?.toString())) }
                else { it }
            }
        }
    } else if (root instanceof Map) {
        root.each {
            if (it.value instanceof Map) { JsonElementsParser(it.value) }
            else if (it.value instanceof List) { it.value = JsonElementsParser(it.value) }
            else if (it.value == null) { it.value }
        }
    }
}

Integer stateSize() { def j = new groovy.json.JsonOutput().toJson(state); return j?.toString().length(); }
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0); }
// String debugStatus() { return (!settings?.appDebug) ? "Off" : "On" }
String deviceDebugStatus() { return !settings?.childDebug ? "Off" : "On" }
Boolean isChildDebug() { return (settings?.childDebug == true) }

List logLevels() {
    List lItems = ["logInfo", "logWarn", "logDebug", "logError", "logTrace"]
    return settings?.findAll { it?.key in lItems && it?.value == true }?.collect { it?.key }
}

String getAppDebugDesc() {
    def ll = logLevels()
    def str = ""
    str += ll?.size() ? "App Log Levels: (${ll?.join(", ")})" : ""
    str += isChildDebug() && str != "" ? "\n" : ""
    str += isChildDebug() ? "Device Debug: (${deviceDebugStatus()})" : ""
    return (str != "") ? "${str}" : null
}

private logDebug(msg) { if(settings?.logDebug == true) { log.debug msg } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info msg } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace msg } }
private logWarn(msg) { if(settings?.logWarn != false) { log.warn msg } }
private logError(msg) { if(settings?.logError != false) { log.error msg } }
