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

String appVersion()	 { return "2.0.6" }
String appModified() { return "2018-12-10" } 
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
    // Time
    buildItems["Date/Time"] = ["trig_datetime::": "Specific Date/Time"]

    buildItems["Location Modes"] = ["trig_mode::Active": "Mode(s) Active", "trig_mode::Inactive": "Not in Mode(s)"]
    
    buildItems["Smart Home Monitor (SHM)"] = ["trig_shm::ArmedHome": "Armed Home", "trig_shm::ArmedAway": "Armed Away", "trig_shm::Disarmed": "Disarmed", "trig_shm::Alerts": "Monitor Alerts"]

    buildItems["Contacts Sensors"] = ["trig_contact::Opened": "Opened", "trig_contact::Closed": "Closed"]

    buildItems["Garage Doors"] = ["trig_garage::Opened": "Opened", "trig_garage::Closed": "Closed"]

    buildItems["Locks"] = ["trig_lock::Locked": "Locked", "trig_lock::Unlocked": "Unlocked"]
    
    buildItems["Humidity"] = ["trig_humidity::EQ": "= Humidity (%)", "trig_humidity::LT": "< Humidity (%)", "trig_humidity::LTE": "<= Humidity (%)", "trig_humidity::GT": "> Humidity (%)", "trig_humidity::GTE": ">= Humidity (%)"]

    buildItems["Motion Sensors"] = ["trig_motion::Active": "Active", "trig_motion::Inactive": "Inactive"]

    buildItems["Power"] = ["trig_power::EQ": "= Power (W)", "trig_power::LT": "< Power (W)", "trig_power::LTE": "<= Power (W)", "trig_power::GT": "> Power (W)", "trig_power::GTE": ">= Power (W)"]

    buildItems["Presence Sensors"] = ["trig_presence::Present": "Presence", "trig_presence::Not present": "Not Present"]

    buildItems["Temperature"] = ["trig_temp::EQ": "= Temp (${unitStr("temp")})", "trig_temp::LT": "< Temp (${unitStr("temp")})", "trig_temp::LTE": "<= Temp (${unitStr("temp")})", "trig_temp::GT": "> Temp (${unitStr("temp")})", "trig_temp::GTE": ">= Temp (${unitStr("temp")})"]
    
    buildItems["Valves"] = ["trig_valve::Opened": "Opened", "trig_valve::Closed": "Closed"]

    buildItems["Water Sensors"] = ["trig_water::Dry": "Dry", "trig_water::Wet": "Wet"]

    buildItems["Window Shades"] = ["trig_shades::Opened": "Opened", "trig_shades::Closed": "Closed"]

    buildItems["Routines"] = ["trig_routine::Active": "Routine(s) Executed"]

    buildItems["Scenes"] = ["trig_scene::Active": "Scene(s) Executed"]

    buildItems?.each { key, val-> 
        addInputGrp(enumOpts, key, val) 
    }
	// log.debug "enumOpts: $enumOpts"
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
        
        section("Triggers:", hideable: true, hidden: (settings?.triggerTypes?.size())) {
            input "triggerTypes", "enum", title: "Select Triggers (Multiple Allowed)", description: "Tap to select", groupedOptions: buildTriggerEnum(), multiple: true, required: true, submitOnChange: true, image: getAppImg("trigger.png")
        }
        if(settings?.triggerTypes) {
            List trigItems = cleanedTriggerList()
            String curItemKey = ""
            if("trig_datetime" in trigItems) {
                section() {
                    href "dateTimePage", title: "Configure Date/Time Triggers", description: "tap to configure", image: getAppImg("restriction.png")
                }
            }
            if("trig_modes" in trigItems) {
                curItemKey = "trig_mode"
                section() {
                    String modeDesc = settings?."${curItemKey}"?.size() ? "Selected: (${settings?."${curItemKey}"?.size()})\n\n tap to modify" : "tap to select modes"
                    input name: "${curItemKey}", type: "enum", title: "Location Modes", description: modeDesc, options: location?.modes?.name?.sort(), multiple: true, required: true, submitOnChange: true, image: getAppImg("mode.png")
                }
            }
            if("trig_shm" in trigItems) {
                curItemKey = "trig_shm"
                section() {
                    def shmItems = selTriggerTypes("${curItemKey}") ?: null
                    paragraph title: "SHM Triggers", "${shmItems ?: "No Items Returned"}", state: (shmItems ? "complete": null), required: true, image: getAppImg("shm.png")
                }
            }
            if("trig_contact" in trigItems) {
                curItemKey = "trig_contact"
                section() {
                    def selItems = selTriggerTypes(curItemKey) ?: null
                    String selDesc = settings[curItemKey]?.size() ? "${selItems ? "Events: [${selItems}]\n" : ""}(${settings[curItemKey]?.size()}) Selected\n\ntap to modify" : "tap to select"
                    input name: "${curItemKey}", type: "capability.contactSensor", title: "Contact Sensors", description: selDesc, state: (settings[curItemKey]?.size() ? "complete" : null), multiple: true, required: true, submitOnChange: true, image: getAppImg("contact.png")
                }
            }
            if("trig_garage" in trigItems) {
                curItemKey = "trig_garage"
                section() {
                    def selItems = selTriggerTypes(curItemKey) ?: null
                    String selDesc = settings[curItemKey]?.size() ? "${selItems ? "Events: [${selItems}]\n" : ""}(${settings[curItemKey]?.size()}) Selected\n\ntap to modify" : "tap to select"
                    input name: "${curItemKey}", type: "capability.garageDoorControl", title: "Garage Doors", description: selDesc, state: (settings[curItemKey]?.size() ? "complete" : null), multiple: true, required: true, submitOnChange: true, image: getAppImg("garage.png")
                }
            }
            if("trig_lock" in trigItems) {
                curItemKey = "trig_lock"
                section() {
                    def selItems = selTriggerTypes(curItemKey) ?: null
                    String selDesc = settings[curItemKey]?.size() ? "${selItems ? "Events: [${selItems}]\n" : ""}(${settings[curItemKey]?.size()}) Selected\n\ntap to modify" : "tap to select"
                    input name: "${curItemKey}", type: "capability.lock", title: "Locks", description: selDesc, state: (settings[curItemKey]?.size() ? "complete" : null), multiple: true, required: true, submitOnChange: true, image: getAppImg("lock.png")
                }
            }

            if("trig_motion" in trigItems) {
                curItemKey = "trig_motion"
                section() {
                    def selItems = selTriggerTypes(curItemKey) ?: null
                    String selDesc = settings[curItemKey]?.size() ? "${selItems ? "Events: [${selItems}]\n" : ""}(${settings[curItemKey]?.size()}) Selected\n\ntap to modify" : "tap to select"
                    input name: "${curItemKey}", type: "capability.motionSensors", title: "Motion Sensors", description: selDesc, state: (settings[curItemKey]?.size() ? "complete" : null), multiple: true, required: true, submitOnChange: true, image: getAppImg("motion.png")
                }
            }
            
        } else { section() { paragraph "No Triggers Selected...", state: null, required: true } }

        if(state?.isInstalled) {
            section("Remove Broadcast Group:") {
                href "uninstallPage", title: "Remove this Group", description: "Tap to Remove...", image: getAppImg("uninstall.png")
            }
        }
    }
}

// buildItems["Contacts Sensors"] = ["trig_contact::Opened": "Opened", "trig_contact::Closed": "Closed"]

//     buildItems["Garage Doors"] = ["trig_garage::Opened": "Opened", "trig_garage::Closed": "Closed"]

//     buildItems["Locks"] = ["trig_lock::Locked": "Locked", "trig_lock::Unlocked": "Unlocked"]
    
//     buildItems["Humidity"] = ["trig_humidity::EQ": "= Humidity (%)", "trig_humidity::LT": "< Humidity (%)", "trig_humidity::LTE": "<= Humidity (%)", "trig_humidity::GT": "> Humidity (%)", "trig_humidity::GTE": ">= Humidity (%)"]

//     buildItems["Motion Sensors"] = ["trig_motion::Active": "Active", "trig_motion::Inactive": "Inactive"]

//     buildItems["Power"] = ["trig_power::EQ": "= Power (W)", "trig_power::LT": "< Power (W)", "trig_power::LTE": "<= Power (W)", "trig_power::GT": "> Power (W)", "trig_power::GTE": ">= Power (W)"]

//     buildItems["Presence Sensors"] = ["trig_presence::Present": "Presence", "trig_presence::Not present": "Not Present"]

//     buildItems["Temperature"] = ["trig_temp::EQ": "= Temp (${unitStr("temp")})", "trig_temp::LT": "< Temp (${unitStr("temp")})", "trig_temp::LTE": "<= Temp (${unitStr("temp")})", "trig_temp::GT": "> Temp (${unitStr("temp")})", "trig_temp::GTE": ">= Temp (${unitStr("temp")})"]
    
//     buildItems["Valves"] = ["trig_valve::Opened": "Opened", "trig_valve::Closed": "Closed"]

//     buildItems["Water Sensors"] = ["trig_water::Dry": "Dry", "trig_water::Wet": "Wet"]

//     buildItems["Window Shades"] = ["trig_shades::Opened": "Opened", "trig_shades::Closed": "Closed"]

//     buildItems["Routines"] = ["trig_routine::Active": "Routine(s) Executed"]

//     buildItems["Scenes"] = ["trig_scene::Active": "Scene(s) Executed"]

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
	initialize()
}

def initialize() {
    state?.isInstalled = true
    // TODO: Cleanup unselected trigger types
}

void settingUpdate(name, value, type=null) {
    if(name && type) {
        app?.updateSetting("$name", [type: "$type", value: value])
    }
    else if (name && type == null){ app?.updateSetting(name.toString(), value) }
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