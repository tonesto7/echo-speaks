/*
 * RemindR Profile V2

 *	7/19/2018 Version: 2.0 (R.0.0.13) Started rewrite of v2
 *
 *  Copyright 2016, 2017, 2018 Jason Headley & Bobby Dobrescu & Anthony Santilli
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
/**********************************************************************************************************************************************/

definition(
	name: "RemindRProfiles2",
	namespace: "Echo",
	author: "JH/BD",
	description: "ReminRProfiles",
	category: "My Apps",
	parent: "Echo:RemindR2",
	iconUrl: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-RemindR.png",
	iconX2Url: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-RemindR@2x.png",
	iconX3Url: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-RemindR@2x.png")
/**********************************************************************************************************************************************/
private appVersion() { return "2.0.0l" }
private appDate() { return "08/23/2018" }
private platform() { return "smartthings" }

preferences {
	page name: "startPage"
	page name: "typeConfigPage"
	page name: "mainPage"
	page name: "reviewPage"
	page name: "tSchedulePage"
	page name: "restrictionConfigPage"
	page name: "notifyPage"
	page name: "certainTimePage"
	page name: "customSounds"
	page name: "triggersPage"
	page name: "actionDevicesPage"
	page name: "sensorDevicesPage"
	page name: "variablePage"
	page name: "pNotifyScene"
}

def appInfoSect(showType=false)	{
	section() {
		def str = "Released: (${appDate()})"
		paragraph title: "${app?.name} (V${appVersion()})", str, image: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-RemindR@2x.png"
		if(showType) {
			def i0 = actionTypesMap()[state?.actionType]?.image
			paragraph title: "Message Configuration", "Selected Type: ${state?.actionType?.toString()?.capitalize()}", image: getAppImg(i0)
		}
	}
}

Map actionTypesMap() {
	return [
		"Ad-Hoc Report":[desc:"Select this option to create an ad-hoc report that can later be triggered", image:"adhoc.png"],
		"Default":[desc:"Select this option to define quick notifications based on various events (quick settings)", image:"report.png"],
		"Custom Sound":[desc:"Select this option to define sound notifications (no text)", image:"sound.png"],
		"Custom Text":[desc:"Select this option to customize text and sound (advanced settings)", image:"text_letter.png"],
		"Custom Text with Weather":[desc:"Select this option if you'd like to use weather elements with your custom text", image:"weather.png"],
		"Triggered Report":[desc:"Select this option to trigger Ad-Hoc reports", image:"trigger.png"]
	]
}

def startPage() {
	appMigration()
	if(state?.actionType) {
		return mainPage()
	} else { return typeConfigPage() }
}

Boolean isAdHoc() {(state?.actionType == "Ad-Hoc Report")}
Boolean isDefault() {(state?.actionType == "Default")}
Boolean isTrigger() {(state?.actionType == "Triggered Report")}
Boolean isCustText() {(state?.actionType == "Custom Text")}
Boolean isCustTextWeather() {(state?.actionType == "Custom Text with Weather")}
Boolean isCustSound() {(state?.actionType == "Custom Sound")}

def typeConfigPage() {
 	dynamicPage (name: "typeConfigPage", nextPage: "Configure Report Type", install: false, uninstall: false) {
		section() { paragraph title: "NOTICE:", "Once type is set it can not be changed!!!", required: true, state: null}
		Map items = actionTypesMap()
		if(parent?.getAdHocReports()?.size() <= 0) { items.remove("Triggered Report") }
		section("Create an Ad-HOC Report") {
			def t0 = items["Ad-Hoc Report"]
			href "mainPage", title: "Ad-Hoc Report", description: t0?.desc, params:[type:"Ad-Hoc Report"], required: false, submitOnChange: true, image: getAppImg(t0?.image)
			items.remove("Ad-Hoc Report")
		}
		section("Create a Reminder Type:") {
			items?.each { k,v ->
				href "mainPage", title: "${k}", description: "${v?.desc}", params:[type: k], required: false, submitOnChange: true, image: getAppImg(v?.image)
			}
		}
	}
}

private appMigration() {
	def typeMap = ["sunOffset":"number", "myNotifyDevice":"capability.notification", "reportMessage":"text", "myWeatherCheckSched":"enum", "smsNumbers":"text",
		"usePush":"bool", "pushTimeStamp":"bool", "askAlexaMQs":"enum", "askAlexaMsgExpiration":"number", "mySonosDevices":"capability.musicPlayer", "mySonosVolume":"number",
		"mySonosResume":"bool", "mySonosDelay1":"number", "mySonosDelay2":"number", "mySpeechDevices":"capability.speechSynthesis", "mySpeechVolume":"number", "mySpeechDelay1":"number",
		"ttsVoiceStyle":"enum", "retriggerSched":"enum", "retriggerCount":"number", "retriggerCancelOnChange":"bool", "custSoundUrl":"text", "custSoundDuration":"text",
		"playCustIntroSound":"bool", "custIntroSoundUrl":"text", "custIntroSoundDuration":"text", "webCorePistons":"enum", "myPowerThresholdStart":"number", "myPowerThresholdStop":"number",
		"myButtonNum":"number"
	]
	def itemMap = ["offset":"sunOffset", "tv":"myNotifyDevice", "myWeatherCheck":"myWeatherCheckSched", "message":"reportMessage", "sms":"smsNumbers",
		"push":"usePush", "timeStamp":"pushTimeStamp", "listOfMQs":"askAlexaMQs", "expiration":"askAlexaMsgExpiration", "sonos":"mySonosDevices", "sonosVolume":"mySonosVolume",
		"resumePlaying":"mySonosResume", "sonosDelayFirst":"mySonosDelay1", "sonosDelay":"mySonosDelay2", "speechSynth":"mySpeechDevices", "speechVolume":"mySpeechVolume",
		"delayFirst":"mySpeechDelay1", "stVoice":"ttsVoiceStyle", "retrigger":"retriggerSched", "howManyTimes":"retriggerCount", "continueOnChange":"retriggerCancelOnChange",
		"cSound":"custSoundUrl", "cDuration":"custSoundDuration", "introSound":"playCustIntroSound", "iSound":"custIntroSoundUrl", "iDuration":"custIntroSoundDuration", "myPiston":"webCorePistons",
		"threshold":"myPowerThresholdStart", "thresholdStop":"myPowerThresholdStop", "buttonNum":"myButtonNum"
	]
	if(settings?.actionType) {
		state?.actionType = settings?.actionType
		settingRemove("actionType");
	}
	itemMap?.each { k, v->
		if(settings?.containsKey(k as String) && !settings?.containsKey(v as String)) {
			def item = (typeMap[v] && typeMap[v]?.toString()?.startsWith("capability")) ? settings?."${k}"?.collect {it?.getId()} : (typeMap[v] == "bool" ? settings[k] as String : settings[k])
			settingUpdate("${v}", item, typeMap[v as String])
			if(settings?.containsKey(v as String)) {
				settingRemove("${k}")
			}
		} else if (settings?.containsKey(v as String) && settings?.containsKey(k as String)) { settingRemove("${k}") }
	}
}

//dynamic page methods
def mainPage(params) {
	if(params?.type) { state?.actionType = params?.type }
 	dynamicPage (name: "mainPage", nextPage: "reviewPage", install: false, uninstall: false) {
		appInfoSect(true)
		if(state?.actionType) {
			Map appData = parent?.getAppFileData()
			section ("Using These ${!isAdHoc() ? "Trigger(s)" : "Device(s)"}") {
				def t0 = triggersDesc()
				href "triggersPage", title: "Select ${!isAdHoc() ? "Trigger(s):" : "Device(s) Events:"}", description: (t0 ? t0 : "Tap to configure"), state: (t0 ? "complete" : ""), image: getAppImg((!isAdHoc() ? "trigger.png" : "devices.png"))
			}
			if (isCustSound()) {
				section ("Play this sound...") {
					List sndItems = appData?.customSounds?.collect { it?.key }?.sort()
					input "custSound", "enum", title: "Choose a Sound", required: false, defaultValue: "Bell 1", submitOnChange: true, options: parent?.custSoundList(), image: getAppImg("sound.png")
					// if(settings?.custSound && settings?.custSound != "Custom URI") {
					// 	state?.customSoundData = appData?.customSounds[settings?.custSound]
					// } else { state?.customSoundData = null }
					if (settings?.custSound == "Custom URI") {
						input "custSoundUrl", "text", title: "Use this URL", required: true, multiple: false, defaultValue: "", submitOnChange: true, image: "blank.png"
						if (settings?.custSoundUrl) {
							input "custSoundDuration", "text", title: "Track Duration", required: true, multiple: false, defaultValue: "10", submitOnChange: true, image: "blank.png"
						}
					}
				}
			}
			if (isCustText() || isCustTextWeather() || isAdHoc() || isTrigger()) {
				section ("Tap here to see available variables:", hideable: true, hidden: true) {
					variableLists()
					href "variablePage", title: "View Available Variables", description: "Tap to view", image: getAppImg("variables.png")
				}
				section ("Play this...") {
					if (!isTrigger()) {
						input "reportMessage", "text", title: "Compose Message\n(tip: include &variables here)", required: false, multiple: false, defaultValue: "", image: getAppImg("text_letter.png")
					}
					if (!isAdHoc()) {
						input "playCustIntroSound", "bool", title: "Play Intro Sound", defaultValue: false, submitOnChange: true, image: getAppImg("sound.png")
						if (settings?.playCustIntroSound) {
							List introItems = appData?.customIntos?.collect { it?.key }?.sort()
							input "custIntroSound", "enum", title: "Choose a Sound", required: false, defaultValue: "Soft Chime", submitOnChange: true, options: ["Custom URI", "Soft Chime", "Text Message Alert","Water Droplet"], image: "blank.png"
							// if(settings?.custIntroSound && settings?.custIntroSound != "Custom URI") {
							// 	state?.customIntroData = appData?.customIntros[settings?.custIntroSound]
							// } else { state?.customIntroData = null }
						}
						if (settings?.custIntroSound == "Custom URI") {
							input "custIntroSoundUrl", "text", title: "Use this URI", required: false, multiple: false, defaultValue: "", submitOnChange: true, image: "blank.png"
							if (settings?.custIntroSoundUrl) {
								input "custIntroSoundDuration", "text", title: "Track Duration", required: true, multiple: false, defaultValue: "10", submitOnChange: true, image: "blank.png"
							}
						}
					}
				}
				if (settings?.reportMessage) {
					section ("Generate Preview Report:", hideable: true, hidden: true, submitOnChange: true) {
						paragraph "${runProfile("test")}"
					}
				}
			}
			if (!isAdHoc()) {
				if (state?.actionType && !isDefault()) {
					section ("Post Event Retrigger" ) {
						input "retriggerSched", "enum", title: "Schedule Post Event Reminder", multiple: false, required: false, submitOnChange: true, image: getAppImg("day_calendar2.png"),
							options: [
								"runEvery1Minute": "Every Minute",
								"runEvery2Minutes": "Every 2 Minutes",
								"runEvery3Minutes": "Every 3 Minutes",
								"runEvery5Minutes": "Every 5 Minutes",
								"runEvery10Minutes": "Every 10 Minutes",
								"runEvery15Minutes": "Every 15 Minutes",
								"runEvery30Minutes": "Every 30 Minutes",
								"runEvery1Hour": "Every Hour",
								"runEvery3Hours": "Every 3 Hours",
								"runEvery6Hours": "Every 6 Hours",
								"runEvery12Hours": "Every 12 Hours",
								"runEvery1Day": "Every Day"
							]
						if (settings?.retriggerSched) {
							input "retriggerCount", "number", title: "...remind you how many times", required: true, description: "number of reminders", defaultValue: 3, submitOnChange: true, image: getAppImg("reminder.png")
							input "retriggerCancelOnChange", "bool", title: "Cancel reminders after condition changes?", required: true, defaultValue: false, submitOnChange: true, image: getAppImg("cancel.png")
						}
					}
				}

				section ("Output to Devices:", hideWhenEmpty: true) {
					input "myNotifyDevice", "capability.notification", title: "Display on this Notification Capable Device(s)", required: false, multiple: true, submitOnChange: true, image: getAppImg("notification_device.png")
					input "mySonosDevices", "capability.musicPlayer", title: "Select Music Player(s) to Play to", required: false, multiple: true, submitOnChange: true, image: getAppImg("media_player.png")
					if (settings?.mySonosDevices) {
						input "mySonosVolume", "number", title: "Temporarily change volume", description: "0-100%", required: false, image: getAppImg("speed_knob.png"), submitOnChange: true
						input "mySonosResume", "bool", title: "Resume currently playing music after notification", required: false, defaultValue: false, submitOnChange: true, image: "blank.png"
						if (!isDefault()) {
							input "mySonosDelay1", "number", title: "(Optional) Delay delivery of first message by...", description: "seconds", required: false, image: getAppImg("delay_time.png"), submitOnChange: true
							input "mySonosDelay2", "number", title: "(Optional) Delay delivery of second message by...", description: "seconds", required: false, image: getAppImg("delay_time.png"), submitOnChange: true
						}
					}
					input "mySpeechDevices", "capability.speechSynthesis", title: "Select Speech Device(s) to Play to", required: false, multiple: true, submitOnChange: true, image: getAppImg("speech.png")
					if (settings?.mySpeechDevices) {
						input "mySpeechVolume", "number", title: "Temporarily change volume", description: "0-100%", required: false, image: getAppImg("speed_knob.png"), submitOnChange: true
						input "mySpeechDelay1", "number", title: "(Optional) Delay delivery of first message by...", description: "seconds", required: false, submitOnChange: true, image: getAppImg("delay_time.png")
					}
					if(settings?.mySonosDevices || settings?.mySpeechDevices) {
						input "ttsVoiceStyle", "enum", title: "SmartThings Voice Style", required: true, defaultValue: "en-US Salli", options: parent?.stVoicesList(), submitOnChange: true, image: getAppImg("voice_feedback.png")
					}
				}

				section ("Send Notifications:", hideWhenEmpty: true) {
					href "notifyPage", title: "Send messages using:\nAsk Alexa, Push,\nPushover, or SMS Text", description: pushSendDesc(), state: pushSendSettings(), image: getAppImg("messages.png")
				}

				if (state?.actionType && !isDefault()) {
					if (isTrigger()) {
						section ("Run actions for this Ad-Hoc Report") {
							input "myAdHocReport", "enum", title: "Choose Ad-Hoc Report...", options: parent?.getAdHocReports(), multiple: false, required: false, submitOnChange: true, image: getAppImg("adhoc.png")
						}
					} else {
						section ("Run actions for this webCoRE Piston") {
							input "enableWebCoRE", "bool", title: "Enable webCoRE Integration", required: false, defaultValue: false, submitOnChange: true, image: webCore_icon()
							if(settings?.enableWebCoRE) {
								input "webCorePistons", "enum", title: "Choose Piston...", options: parent?.webCoRE_list('name'), multiple: false, required: false, submitOnChange: true, image: webCore_icon()
							}
						}
					}
					section ("Using these Restrictions") {
						href "restrictionConfigPage", title: "Use these restrictions...", description: restrictPageDesc(), state: restrictPageSettings(), image: getAppImg("restriction.png")
					}
				}
			}
		} else {
			section() { paragraph title: "Somethings Wrong", "ActionType is missing... Please return to previous page", required: true, state: null, image: getAppImg("error.png") }
		}
 	}
}

def variableLists() {
	List vars = []
	if (!isAdHoc() && !isCustTextWeather()) {
		vars = ["&device", "&action", "&event","&time", "&date","&last","&profile"]
		paragraph title: "Custom Variables:", "${vars}"
	}
	if(!isAdHoc()) {
		vars = ["&mode","&shm"]
		paragraph title: "Location Variables:", "${vars}"
	}
	if (isAdHoc()) {
		vars = getDeviceVarMap()?.collect { "&${it?.key}" }
		paragraph title: "AD-Hoc Variables:", "${vars}"
	}
	if (isCustTextWeather() || isAdHoc() ) {
		vars = ["&today","&tonight","&tomorrow"]
		paragraph title: "Weather Variables (Forcast):", "${vars}"
		vars = ["&current","&high","&low"]
		paragraph title: "Weather Variables (Temperatures):", "${vars}"
		vars = ["&set","&rise"]
		paragraph title: "Weather Variables (Sun State)", "${vars}"
		vars = ["&wind","&precipitation","&humidity","&uv"]
		paragraph title: "Weather Variables (Other):", "${vars}"
	}
}

def variablePage() {
	dynamicPage (name: "variablePage", nextPage: "reviewPage", install: false, uninstall: false) {
		def stamp = state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)
		def today = new Date(now()).format("EEEE, MMMM d, yyyy", location.timeZone)
		if (!isAdHoc() && !isCustTextWeather()) {
			section("Custom Variables:") {
				Map vars = [:]
				vars["&device"] = "<<Label of Device>>"
				vars["&action"] = "<<Actual event: on/off, open/closed, locked/unlocked>>"
				vars["&event"] = "<<Capability Type: switch, contact, motion, lock, etc>>"
				vars["&time"] = "${getVariable("time")}"
				vars["&date"] = "${getVariable("date")}"
				vars["&last"] = "${state.lastEvent}"
				vars["&profile"] = "${app?.getLabel()}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
		}
		if(!isAdHoc()) {
			section("Location Variables:") {
				Map vars = [:]
				vars["&mode"] = "${getVariable("mode")}"
				vars["&shm"] = "${getVariable("shm")}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
		}
		if (isAdHoc()) {
			section("AD-Hoc Variables:") {

				getDeviceVarMap()?.each { k, v->
					paragraph title: "&${k}", (settings?."${v}" ? getVariable(k as String)?.toString() : "No ${k} Devices Selected!"), state: (settings?."${v}" ? "complete" : "")
				}
			}
		}
		if (isCustTextWeather() || isAdHoc() ) {
			section("Weather Variables (Forcast):") {
				Map vars = [:]
				vars["&today"] = "${mGetWeatherVar("today")}"
				vars["&tonight"] = "${mGetWeatherVar("tonight")}"
				vars["&tomorrow"] = "${mGetWeatherVar("tomorrow")}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
			section("Weather Variables (Temperatures):") {
				Map vars = [:]
				vars["&current"] = "${mGetWeatherElements("current")}"
				vars["&high"] = "${mGetWeatherVar("high")}"
				vars["&low"] = "${mGetWeatherVar("low")}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
			section("Weather Variables (Sun State)") {
				Map vars = [:]
				vars["&set"] = "${mGetWeatherElements("set")}"
				vars["&rise"] = "${mGetWeatherElements("rise")}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
			section("Weather Variables (Other):") {
				Map vars = [:]
				vars["&wind"] = "${mGetWeatherElements("wind")}"
				vars["&precipitation"] = "${mGetWeatherElements("precip")}"
				vars["&humidity"] = "${mGetWeatherElements("hum")}"
				vars["&uv"] = "${mGetWeatherElements("uv")}"
				vars?.each { k, v->
					paragraph title: "${k}", "${v}"
				}
			}
		}
	}
}

Map getDeviceVarMap() {
	return ["power":"myPower", "lights":"mySwitch", "unlocked":"myLocks", "open":"myContact", "doors":"myDoor", "windows":"myWindow", "garage":"myGarage", "valves":"myValve", "present":"myPresence", "shades":"myShades", "smoke":"mySmoke", "water":"myWater", "CO2":"myCO2", "indoorhum":"myHumidity", "noise":"mySound", "temperature":"myTemperature", "running":"myTstat", "thermostat":"myTstat", "cooling":"myTstat", "heating":"myTstat"]
}

def triggersPage() {
	dynamicPage(name: "triggersPage", title: "", uninstall: false) {
		def actions = location.helloHome?.getPhrases()*.label.sort()
		if (!isDefault() && !isAdHoc() ) {
			section("Date & Time: ") {
				input "tSchedule", "enum", title: "How Often?", submitOnChange: true, required: fale, options: ["One Time", "Recurring"], image: getAppImg("day_calendar2.png")
				if(settings?.tSchedule) {
					href "tSchedulePage", title: "Schedule Settings...", description: "Tap to Configure", state: ""
				}
			}
		}

		if (!isDefault() && !isAdHoc()) {
			section ("Location Events: ", hideWhenEmpty: true) {
				input "myMode", "enum", title: "Modes", options: location.modes.name.sort(), multiple: true, required: false, submitOnChange: true, image: getAppImg("mode.png")
				input "mySHM", "enum", title: "SHM status", options: ["Armed (Away)","Armed (Home)","Disarmed"], multiple: true, required: false, submitOnChange: true, image: getAppImg("alarm_disarm.png")
				input "myRoutine", "enum", title: "Routines", options: actions, multiple: true, required: false, submitOnChange: true, image: getAppImg("routine.png")
				input "mySunState", "enum", title: "Sunrise or Sunset...", options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true, image: getAppImg("sun.png")
				if (settings?.mySunState) {
					input "sunOffset", "number", range: "*..*", title: "Offset trigger this number of minutes (+/-)", required: false, submitOnChange: true, image: getAppImg("offset_icon.png")
				}
			}
		}

		section("Controllable Devices:") {
			href "actionDevicesPage", title: "Controllable Devices...", description: (deviceTriggers() ? "Configured..." : "Tap to configure"), state: (deviceTriggers() ? "Complete" : null), image: getAppImg("devices2.png")
		}

		section("Sensor Devices:") {
			href "sensorDevicesPage", title: "Sensor Devices...", description: (sensorTriggers() ? "Configured..." : "Tap to configure"), state: (sensorTriggers() ? "Complete" : null), image: getAppImg("sensors.png")
		}

		if (!isDefault() && !isAdHoc()) {
			section ("Weather Events") {
				input "myWeatherAlert", "enum", title: "Weather Alerts", required: false, multiple: true, submitOnChange: true, image: getAppImg("weather.png"),
						options: [
							"TOR":	"Tornado Warning",
							"TOW":	"Tornado Watch",
							"WRN":	"Severe Thunderstorm Warning",
							"SEW":	"Severe Thunderstorm Watch",
							"WIN":	"Winter Weather Advisory",
							"FLO":	"Flood Warning",
							"WND":	"High Wind Advisoryt",
							"HEA":	"Heat Advisory",
							"FOG":	"Dense Fog Advisory",
							"FIR":	"Fire Weather Advisory",
							"VOL":	"Volcanic Activity Statement",
							"HWW":	"Hurricane Wind Warning"
						]
				input "myWeather", "enum", title: "Hourly Weather Forecast Updates", required: false, multiple: false, submitOnChange: true, options: ["Weather Condition Changes", "Chance of Precipitation Changes", "Wind Speed Changes", "Humidity Changes", "Any Weather Updates"], image: "blank.png"
				input "myWeatherTriggers", "enum", title: "Weather Elements", required: false, multiple: false, submitOnChange: true, options: ["Chance of Precipitation (in/mm)", "Wind Gust (MPH/kPH)", "Humidity (%)", "Temperature (F/C)"], image: "blank.png"
				if (settings?.myWeatherTriggers) {
					input "myWeatherTriggersS", "enum", title: "Notify when Weather Element changes...", options: ["above", "below"], required: false, submitOnChange: true, image: getAppImg("trigger.png")
				}
				if (settings?.myWeatherTriggersS) {
					input "myWeatherThreshold", "decimal", title: "Weather Variable Threshold...", required: false, submitOnChange: true, image: getAppImg("trigger.png")
				}
				if (settings?.myWeatherThreshold) {
					input "myWeatherCheckSched", "enum", title: "How Often to Check for Weather Changes...", required: true, multiple: false, submitOnChange: true, image: getAppImg("day_calendar2.png"),
							options: [
								"runEvery1Minute": "Every Minute",
								"runEvery5Minutes": "Every 5 Minutes",
								"runEvery10Minutes": "Every 10 Minutes",
								"runEvery15Minutes": "Every 15 Minutes",
								"runEvery30Minutes": "Every 30 Minutes",
								"runEvery1Hour": "Every Hour",
								"runEvery3Hours": "Every 3 Hours"
							]
				}
			}
		}
	}
}

def actionDevicesPage() {
	dynamicPage(name: "actionDevicesPage", title: "", uninstall: false) {
		section ("Switches:", hideWhenEmpty: true) {
			input "mySwitch", "capability.switch", title: "Switches", required: false, multiple: true, submitOnChange: true, image: getAppImg("switch.png")
			if (settings?.mySwitch && !isAdHoc()) {
				input "mySwitchS", "enum", title: "Notify when state changes to...", options: ["on", "off", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.mySwitchS != "both" && !isDefault()) {
					input "mySwitchMinutes", "number", title: "... and continues to be ${settings?.mySwitchS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}

		if (!isAdHoc()) {
            section ("Buttons:", hideWhenEmpty: true) {
                input "myButton", "capability.button", title: "Button", required: false, multiple: true, submitOnChange: true, image: getAppImg("push_button.png")
                if (settings?.myButton && !isAdHoc()) {
                    input "myButtonS", "enum", title: "Notify when button is...", options: ["pushed", "held"], required: true, submitOnChange: true, image: "blank.png"
                    if (settings?.myButtonS) {
                        input "myButtonNum", "number", title: "Button Number", range: "1..20", required: false, description: "number (optional - max 20)", submitOnChange: true, image: "blank.png"
                    }
                }
            }
		}
        
		section ("Locks:", hideWhenEmpty: true) {
			input "myLocks", "capability.lock", title: "Locks", required: false, multiple: true, submitOnChange: true, image: getAppImg("lock.png")
			if (settings?.myLocks && !isAdHoc()) {
				input "myLocksS", "enum", title: "Notify when state changes to...", options: ["locked", "unlocked", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myLocksS != "both" && !isDefault()) {
					input "myLocksMinutes", "number", title: "... and continues to be ${settings?.myLocksS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
				if (settings?.myLocksS == "unlocked") {
					input "myLocksSCode", "number", title: "With this user code...", required: false, description: "user code number (optional)", submitOnChange: true, image: "blank.png"
				}
			}
		}

		if (!isDefault()) {
			section ("Thermostats:", hideWhenEmpty: true) {
				input "myTstat", "capability.thermostat", title: "Thermostats", required: false, multiple: true, submitOnChange: true, image: getAppImg("thermostat.png")
				if (settings?.myTstat && !isAdHoc()) {
					input "myTstatS", "enum", title: "Notify when set point changes for...", options: ["cooling", "heating", "both"], required: false, submitOnChange: true, image: "blank.png"
					input "myTstatM", "enum", title: "Notify when mode changes to...", options: ["auto", "cool", " heat", "emergency heat", "off", "every mode"], required: false, submitOnChange: true, image: "blank.png"
					input "myTstatOS", "enum", title: "Notify when Operating State changes to...", options: ["cooling", "heating", "idle", "every state"], required: false, submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Window Shades:", hideWhenEmpty: true) {
			input "myShades", "capability.windowShade", title: "Window Covering Devices", multiple: true, required: false, submitOnChange: true, image: getAppImg("window_shade.png")
			if (settings?.myShades && !isAdHoc()) {
				input "myShadesS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: false, submitOnChange: true, image: "blank.png"
			}
		}

		section ("Garage Doors:", hideWhenEmpty: true) {
			input "myGarage", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true, image: getAppImg("garage_door.png")
			if (settings?.myGarage && !isAdHoc()) {
				input "myGarageS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myGarageS != "both" && !isDefault()) {
					input "myGarageMinutes", "number", title: "... and continues to be ${settings?.myGarageS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}
/*
		section ("Garage Doors (Relay):", hideWhenEmpty: true) {
			input "myRelay", "capability.switch", title: "Relay used as Garage Doors", multiple: true, required: false, submitOnChange: true, image: getAppImg("garage_door.png")
			if (settings?.myRelay) {
				input "myRelayContact", "capability.contactSensor", title: "Select a Contact Sensor that monitors the relay", multiple: false, required: false, submitOnChange: true, image: "blank.png"
			}
			if (settings?.myRelayContact && !isAdHoc()) {
				input "myRelayContactS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myRelayContactS != "both" && !isDefault()) {
					input "myRelayMinutes", "number", title: "... and continues to be ${settings?.myRelayContactS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}
*/
		section ("Valve Devices:", hideWhenEmpty: true) {
			input "myValve", "capability.valve", title: "Water Valves", required: false, multiple: true, submitOnChange: true, image: getAppImg("valve.png")
			if (settings?.myValve && !isAdHoc()) {
				input "myValveS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myValveS != "both" && !isDefault()) {
					input "myValveMinutes", "number", title: "... and continues to be ${settings?.myValveS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}
	}
}

def sensorDevicesPage() {
	dynamicPage(name: "sensorDevicesPage", title: "", uninstall: false) {
		if (!isDefault()) {
			section ("Power Meters:", hideWhenEmpty: true) {
				input "myPower", "capability.powerMeter", title: "Power Meters", required: false, multiple: false, submitOnChange: true, image: getAppImg("power.png")
				if (settings?.myPower && !isAdHoc()) {
					input "myPowerS", "enum", title: "Notify when power is...", options: ["above threshold", "below threshold"], required: false, submitOnChange: true, image: "blank.png"
				}
				if (settings?.myPowerS) {
					input "myPowerThresholdStart", "number", title: "Wattage Threshold...", required: false, description: "in watts", submitOnChange: true, image: "blank.png"
				}
				if (settings?.myPowerThresholdStart) {
					input "myPowerMinutes", "number", title: "Threshold Delay", required: false, description: "in minutes (optional)", submitOnChange: true, image: "blank.png"
					input "myPowerThresholdStop", "number", title: "...but not ${settings?.myPowerS} this value", required: false, description: "in watts", submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Contact Sensors:", hideWhenEmpty: true) {
			input "myContact", "capability.contactSensor", title: "Contact", required: false, multiple: true, submitOnChange: true, image: getAppImg("contact.png")
			if (settings?.myContact && !isAdHoc()) {
				input "myContactS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myContactS != "both" && !isDefault()) {
					input "myContactMinutes", "number", title: "... and continues to be ${settings?.myContactS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}

			if (isAdHoc()) {
				input "myDoor", "capability.contactSensor", title: "Contact Sensors used on doors", required: false, multiple: true, submitOnChange: true, image: getAppImg("door_close.png")
				if (settings?.myDoor && !isAdHoc()) {
					input "myDoorS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: true, submitOnChange: true, image: "blank.png"
				}
				input "myWindow", "capability.contactSensor", title: "Contact Sensors used on windows", required: false, multiple: true, submitOnChange: true, image: getAppImg("window.png")
				if (settings?.myWindow && !isAdHoc()) {
					input "myWindowS", "enum", title: "Notify when state changes to...", options: ["open", "closed", "both"], required: false, submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Acceleration Sensors:", hideWhenEmpty: true) {
			input "myAcceleration", "capability.accelerationSensor", title: "Acceleration", required: false, multiple: true, submitOnChange: true, image: getAppImg("acceleration.png")
			if (settings?.myAcceleration && !isAdHoc()) {
				input "myAccelerationS", "enum", title: "Notify when state changes to...", options: ["active", "inactive", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myAccelerationS != "both" && !isDefault()) {
					input "myAccelerationMinutes", "number", title: "... and continues to be ${settings?.myAcceleration} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Motion Sensors:", hideWhenEmpty: true) {
			input "myMotion", "capability.motionSensor", title: "Motion", required: false, multiple: true, submitOnChange: true, image: getAppImg("motion.png")
			if (settings?.myMotion && !isAdHoc()) {
				input "myMotionS", "enum", title: "Notify when state changes to...", options: ["active", "inactive", "both"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myMotionS != "both" && !isDefault()) {
					input "myMotionMinutes", "number", title: "... and continues to be ${settings?.myMotionS} for (minutes) - OPTIONAL", required: false, description: "minutes", submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Presence Sensors:", hideWhenEmpty: true) {
			input "myPresence", "capability.presenceSensor", title: "Presence", required: false, multiple: true, submitOnChange: true, image: getAppImg("recipient.png")
			if (settings?.myPresence && !isAdHoc()) {
				input "myPresenceS", "enum", title: "Notify when state changes to...", options: ["present", "not present", "both"], required: true, submitOnChange: true, image: "blank.png"
			}
		}

		section ("Carbon Dioxide Sensors:", hideWhenEmpty: true) {
			input "myCO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO2)", multiple:true, required: false, submitOnChange: true, image: getAppImg("co2_warn_status.png")
			if (settings?.myCO2 && !isAdHoc()) {
				input "myCO2S", "enum", title: "Notify when CO2 is...", options: ["above", "below"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myCO2S) {
					input "CO2", "number", title: "this Carbon Dioxide Level...", required: true, description: "number", submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Carbon Monoxide Detectors:", hideWhenEmpty: true) {
			input "myCO", "capability.carbonMonoxideDetector", title: "Carbon Monoxide (CO)", required: false, submitOnChange: true, image: getAppImg("co2_warn_status.png")
			if (settings?.myCO && !isAdHoc()) {
				input "myCOS", "enum", title: "Notify when ...", options: ["detected", "tested", "both"], required: true, submitOnChange: true, image: "blank.png"
			}
		}

		section ("Smoke Detectors:", hideWhenEmpty: true) {
			input "mySmoke", "capability.smokeDetector", title: "Smoke", required: false, multiple: true, submitOnChange: true, image: getAppImg("smoke_emergency_status.png")
			if (settings?.mySmoke && !isAdHoc()) {
				input "mySmokeS", "enum", title: "Notify when state changes to...", options: ["detected", "clear", "both"], required: true, submitOnChange: true, image: "blank.png"
			}
		}

		section ("Water Sensors:", hideWhenEmpty: true) {
			input "myWater", "capability.waterSensor", title: "Water", required: false, multiple: true, submitOnChange: true, image: getAppImg("water.png")
			if (settings?.myWater && !isAdHoc()) {
				input "myWaterS", "enum", title: "Notify when state changes to...", options: ["wet", "dry", "both"], required: true, submitOnChange: true, image: "blank.png"
			}
		}

		section ("Temperature Sensors:", hideWhenEmpty: true) {
			input "myTemperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true, image: getAppImg("temp.png")
			if (settings?.myTemperature && !isAdHoc()) {
				input "myTemperatureS", "enum", title: "Notify when temperature is...", options: ["above", "below"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myTemperatureS) {
					input "temperature", "number", title: "this Temperature...", required: true, description: "degrees", submitOnChange: true, image: "blank.png"
					if (settings?.temperature) {
						input "temperatureStop", "number", title: "...but not ${settings?.myTemperatureS} this temperature", required: false, description: "degrees", submitOnChange: true, image: "blank.png"
					}
				}
			}
		}

		section ("Relative Humidity:", hideWhenEmpty: true) {
			input "myHumidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", multiple:true, required: false, submitOnChange: true, image: getAppImg("humidity.png")
			if (settings?.myHumidity && !isAdHoc()) {
				input "myHumidityS", "enum", title: "Notify when Relative Humidity is...", options: ["above", "below"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.myHumidityS) {
					input "humidity", "number", title: "Relative Humidity Level...", required: true, description: "percent", submitOnChange: true, image: "blank.png"
				}
			}
		}

		section ("Sound Pressure:", hideWhenEmpty: true) {
			input "mySound", "capability.soundPressureLevel", title: "Sound Pressure (noise level)", multiple:true, required: false, submitOnChange: true, image: getAppImg("sound_sensor.png")
			if (settings?.mySound && !isAdHoc()) {
				input "mySoundS", "enum", title: "Notify when Noise is...", options: ["above", "below"], required: true, submitOnChange: true, image: "blank.png"
				if (settings?.mySoundS) {
					input "noise", "number", title: "Noise Level...", required: true, description: "number", submitOnChange: true, image: "blank.png"
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

def tSchedulePage() {
	dynamicPage(name: "tSchedulePage", title: "", uninstall: false) {
		if (settings?.tSchedule == "One Time") {
			section("Time:") {
				input "xFutureTime", "time", title: "At this time...",  required: true, submitOnChange: true
			}
			section("Date (optional):") {
				def todayYear = new Date(now()).format("yyyy")
				def todayMonth = new Date(now()).format("MMMM")
				def todayDay = new Date(now()).format("dd")
				input "xFutureDay", "number", title: "On this Day - maximum 31", range: "1..31", submitOnChange: true, description: "Example: ${todayDay}", required: false
				if (settings?.xFutureDay) {
					input "xFutureMonth", "enum", title: "Of this Month", submitOnChange: true, required: false, multiple: false, description: "Example: ${todayMonth}", options: monthMap()
				}
				if (settings?.xFutureMonth) {
					input "xFutureYear", "number", title: "Of this Year", range: "2017..2020", submitOnChange: true, description: "Example: ${todayYear}", required: false
				}
			}
		}
		if (settings?.tSchedule == "Recurring") {
			section("Recurring:") {
				input "frequency", "enum", title: "Choose frequency", submitOnChange: true, required: fale, options: ["Minutes", "Hourly", "Daily", "Weekly", "Monthly", "Yearly"]
				if (settings?.frequency == "Minutes") {
					input "xMinutes", "number", title: "Every X minute(s) - maximum 60", range: "1..59", submitOnChange: true, required: false
				}
				if (settings?.frequency == "Hourly") {
					input "xHours", "number", title: "Every X hour(s) - maximum 24", range: "1..23", submitOnChange: true, required: false
				}
				if (settings?.frequency == "Daily") {
					if (!settings?.xDaysWeekDay) {
						input "xDays", "number", title: "Every X day(s) - maximum 31", range: "1..31", submitOnChange: true, required: false
					}
					input "xDaysWeekDay", "bool", title: "OR Every Week Day (MON-FRI)", required: false, defaultValue: false, submitOnChange: true
					if (settings?.xDays || settings?.xDaysWeekDay) {
						input "xDaysStarting", "time", title: "starting at time...", submitOnChange: true, required: true
					}
				}
				if (settings?.frequency == "Weekly") {
					input "xWeeks", "enum", title: "Every selected day(s) of the week", submitOnChange: true, required: false, multiple: true,
						options: ["SUN": "Sunday", "MON": "Monday", "TUE": "Tuesday", "WED": "Wednesday", "THU": "Thursday", "FRI": "Friday", "SAT": "Saturday"]
					if (settings?.xWeeks) {
						input "xWeeksStarting", "time", title: "starting at time...", submitOnChange: true, required: true
					}
				}
				if (settings?.frequency == "Monthly") {
					//TO DO add every (First-Fourth), (Mon-Fri) of every (X) month
					input "xMonths", "number", title: "Every X month(s) - maximum 12", range: "1..12", submitOnChange: true, required: false
					if (settings?.xMonths) {
						input "xMonthsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
						input "xMonthsStarting", "time", title: "starting at time...", submitOnChange: true, required: true
					}
				}
				if (settings?.frequency == "Yearly") {
					//TO DO add the (First-Fourth), (Mon-Fri) of (Jan-Dec)
					input "xYears", "enum", title: "Every selected month of the year", submitOnChange: true, required: false, multiple: false, options: monthMap()
					if (settings?.xYears) {
						input "xYearsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
						input "xYearsStarting", "time", title: "starting at time...", submitOnChange: true, required: true
					}
				}
			}
		}
	}
}

def notifyPage() {
	dynamicPage(name: "notifyPage", title: "Configure Notifications", uninstall: false) {
		section ("Push Messages:") {
			input "usePush", "bool", title: "Send Push Notifications...", required: false, defaultValue: false, submitOnChange: true
			input "pushTimeStamp", "bool", title: "Add timestamp to Push Messages...", required: false, defaultValue: false, submitOnChange: true
		}
		section ("Text Messages:", hideWhenEmpty: true) {
			paragraph "To send to multiple numbers separate the number by a comma\nE.g. 8045551122,8046663344"
			input "smsNumbers", "text", title: "Send SMS Text to...", required: false, submitOnChange: true, image: getAppImg("sms_phone.png")
		}
		section("Pushover Support:") {
			input ("pushoverEnabled", "bool", title: "Use Pushover Integration", required: false, submitOnChange: true, image: getAppImg("pushover_icon.png"))
			if(settings?.pushoverEnabled == true) {
				def poDevices = parent?.getPushoverDevices()
				if(!poDevices) {
					parent?.pushover_init()
					paragraph "If this is the first time enabling Pushover than leave this page and come back if the devices list is empty"
				} else {
					input "pushoverDevices", "enum", title: "Select Pushover Devices", description: "Tap to select", groupedOptions: poDevices, multiple: true, required: false, submitOnChange: true, image: getAppImg("select_icon.png")
					if(settings?.pushoverDevices) {
						def t0 = [(-2):"Lowest", (-1):"Low", 0:"Normal", 1:"High", 2:"Emergency"]
						input "pushoverPriority", "enum", title: "Notification Priority (Optional)", description: "Tap to select", defaultValue: 0, required: false, multiple: false, submitOnChange: true, options: t0, image: getAppImg("priority.png")
						input "pushoverSound", "enum", title: "Notification Sound (Optional)", description: "Tap to select", defaultValue: "pushover", required: false, multiple: false, submitOnChange: true, options: parent?.getPushoverSounds(), image: getAppImg("sound.png")
					}
				}
				// } else { paragraph "New Install Detected!!!\n\n1. Press Done to Finish the Install.\n2. Goto the Automations Tab at the Bottom\n3. Tap on the SmartApps Tab above\n4. Select ${app?.getLabel()} and Resume configuration", state: "complete" }
			}
		}
		section("Ask Alexa Support:") {
			input "askAlexa", "bool", title: "Send to Ask Alexa", defaultValue: false, submitOnChange: true, image: askAlexaImgUrl()
			if (settings?.askAlexa) {
				input "askAlexaMQs", "enum", title: "Choose Ask Alexa Message Queue(s)", options: parent?.listaskAlexaMQHandler(), multiple: true, required: false, submitOnChange: true, image: getAppImg("check_list.png")
				if (settings?.askAlexaMQs) {
					input "askAlexaMsgExpiration", "number", title: "Remove message from Ask Alexa Message Queue in...", description: "minutes", required: false, submitOnChange: true, image: getAppImg("expire_time.png")
				}
			}
		}
	}
}

def restrictionConfigPage() {
	dynamicPage(name: "restrictionConfigPage", title: "", uninstall: false) {
		section ("Location Mode:") {
			input "modes", "mode", title: "Only when mode is", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode.png")
		}
		section ("Certain Days:") {
			input "days","enum", title: "Only on certain days of the week", multiple: true, required: false, submitOnChange: true, image: getAppImg("day_calendar.png"),
				options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
		}
		section ("Certain Time:") {
			href "certainTimePage", title: "Only during a certain time", description: pTimeComplete(), state: pTimeSettings(), image: getAppImg("time_past.png")
		}
		section ("Device Status:") {
			input "rSwitch", "capability.switch", title: "Only when these Switch(es)", required: false, multiple: true, submitOnChange: true, image: getAppImg("switch.png")
				if (settings?.rSwitch) {
					input "rSwitchS", "enum", title: "state is...", options: ["on", "off"], required: false
				}
			input "rContact", "capability.contactSensor", title: "Only when these Contact Sensor(s)", required: false, multiple: true, submitOnChange: true, image: getAppImg("door_close.png")
				if (settings?.rContact) {
					input "rContactS", "enum", title: "state is...", options: ["open", "closed"], required: false
				}
			input "rMotion", "capability.motionSensor", title: "Only when these Motion Sensor(s)..", required: false, multiple: true, submitOnChange: true, image: getAppImg("motion.png")
				if (settings?.rMotion) {
					input "rMotionS", "enum", title: "state is...", options: ["active", "inactive"], required: false
				}
			input "rPresence", "capability.presenceSensor", title: "Only when these Presence Sensor(s)...", required: false, multiple: true, submitOnChange: true, image: getAppImg("recipient.png")
				if (settings?.rPresence) {
					input "rPresenceS", "enum", title: "state is...", options: ["present", "not present"], required: false, submitOnChange: true
				}
		}
		section ("Frequency (audio only):") {
			input "onceDaily", "bool", title: "Only notify once a day", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("info.png")
			input "everyXmin", "number", title: "Delay between notifications", description: "Minutes", required: false, submitOnChange: true, image: getAppImg("delay_time.png")
		}
	}
}

def certainTimePage() {
	dynamicPage(name:"certainTimePage",title: "Only during a certain time", uninstall: false) {
		section("Beginning at....") {
			input "startingX", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true, image: getAppImg("start_time.png")
			if (settings?.startingX == "A specific time") {
				input "starting", "time", title: "Start time", required: true, submitOnChange: true, image: getAppImg("start_time.png")
			} else if(settings?.startingX in ["Sunrise", "Sunset"]) {
				input "start${settings?.startingX}Offset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true, image: getAppImg("offset_icon.png")
			}
		}
		section("Ending at....") {
			input "endingX", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true, image: getAppImg("stop_time.png")
			if (settings?.endingX == "A specific time") {
				input "ending", "time", title: "End time", required: true, submitOnChange: true, image: getAppImg("stop_time.png")
			} else if(settings?.endingX in ["Sunrise", "Sunset"]) {
				input "end${settings?.startingX}Offset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true, image: getAppImg("offset_icon.png")
			}
		}
	}
}

def reviewPage() {
	dynamicPage(name: "reviewPage", install: true, uninstall: true) {
		section ("Give this Reminder a Name") {
 			label title: "Reminder Name", required: true, defaultValue: "", submitOnChange: true, image: getAppImg("es5_prf_name.png")
		}
	}
}

def getAppImg(imgName)	{ return "https://echosistant.com/es5_content/images/$imgName" }

/************************************************************************************************************

************************************************************************************************************/
def installed() {
	atomicState?.isInstalled = true
	log.debug "Installed with settings: ${settings}, current app version: ${appVersion()}"
	state.NotificationRelease = "Notification: " + appVersion()
	state.sound
	if (settings?.myWeatherAlert) {
		runEvery5Minutes("mGetWeatherAlerts")
		mGetWeatherAlerts()
	}
	if (settings?.myWeather) {
		runEvery1Hour("mGetCurrentWeather")
		mGetCurrentWeather()
	}
}

def updated() {
	log.debug "Updated with settings: ${settings}, current app version: ${appVersion()}"
	state.NotificationRelease = "Notification: " + appVersion()
	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
	stateCleanup()
	clearRetrigger()
	subscriber()
}

public setDebugVal(val) {
	state?.showDebug = (val == true)
}

public setZipCode(val) {
	state?.wZipCode = val
}

private clearRetrigger() {
	parent?.updActiveRetrigger(app?.getId(), null)
}

private subscriber() {
	if (settings?.mySunState == "Sunset") {
		subscribe(location, "sunsetTime", sunsetTimeHandler)
		sunsetTimeHandler(location.currentValue("sunsetTime"))
	}
	if (settings?.mySunState == "Sunrise") {
		subscribe(location, "sunriseTime", sunriseTimeHandler)
		sunriseTimeHandler(location.currentValue("sunriseTime"))
	}
	if (settings?.frequency) {
		cronHandler(settings?.frequency)
	}
	if (settings?.xFutureTime) {
		oneTimeHandler()
	}
	if (settings?.myWeatherAlert) {
		runEvery5Minutes("mGetWeatherAlerts")
		state.weatherAlert
		mGetWeatherAlerts()
	}
	if (settings?.myWeatherTriggers) {
		"${settings?.myWeatherCheckSched}"("mGetWeatherTrigger")
		mGetWeatherTrigger()
	}
	if (settings?.myWeather) {
		if (state?.showDebug) { log.debug "refreshing hourly weather" }
		runEvery1Hour("mGetCurrentWeather")
		state.lastWeather = null
		state.lastWeatherCheck = null
	   	mGetCurrentWeather()
	}
	if (state?.actionType && !isAdHoc()) {
		if (settings?.myPower) 	{ subscribe(settings?.myPower, "power", meterHandler) }
		if (settings?.myRoutine){ subscribe(location, "routineExecuted", alertsHandler) }
		if (settings?.myMode) 	{ subscribe(location, "mode", alertsHandler) }
		if (settings?.mySHM) 	{ subscribe(location, "alarmSystemStatus", alertsHandler) }
		if (settings?.mySwitch) {
			// if (settings?.mySwitchS == "on") {
			// 	subscribe(settings?.mySwitch, "switch.on", alertsHandler)
			// } else if (settings?.mySwitchS == "off") {
			// 	subscribe(settings?.mySwitch, "switch.off", alertsHandler)
			// } else { subscribe(settings?.mySwitch, "switch", alertsHandler) }
			subscribe(settings?.mySwitch, "switch", alertsHandler)
		}
		if (settings?.myButton) {
			if (settings?.myButtonS == "held") {
				subscribe(settings?.myButton, "button.held", buttonNumHandler)
			} else if (settings?.myButtonS == "pushed") { 
				subscribe(settings?.myButton, "button.pushed", buttonNumHandler) 
			}
		}
		if (settings?.myContact) {
			// if (settings?.myContactS == "open") {
			// 	subscribe(settings?.myContact, "contact.open", alertsHandler)
			// } else if (settings?.myContactS == "closed")	{
			// 	subscribe(settings?.myContact, "contact.closed", alertsHandler)
			// } else { subscribe(settings?.myContact, "contact", alertsHandler) }
			subscribe(settings?.myContact, "contact", alertsHandler)
		}
		if (settings?.myGarage) {
			// if (settings?.myGarageS == "open") {
			// 	subscribe(settings?.myGarage, "contact.open", alertsHandler)
			// } else if (settings?.myGarageS == "closed") {
			// 	subscribe(settings?.myGarage, "contact.closed", alertsHandler)
			// } else { subscribe(settings?.myGarage, "contact", alertsHandler) }
			subscribe(settings?.myGarage, "contact", alertsHandler)
		}
		/* Bobby 8/20/18
		if (settings?.myRelayContact) {
			if (settings?.myRelayContactS == "open") {
				subscribe(settings?.myRelayContact, "contact.open", alertsHandler)
			} else if (settings?.myRelayContactS == "closed") {
				subscribe(settings?.myRelayContact, "contact.closed", alertsHandler)
			} else { subscribe(settings?.myRelayContact, "contact", alertsHandler) }
		}
		*/
		if (settings?.myDoor) {
			// if (settings?.myDoorS == "open") {
			// 	subscribe(settings?.myDoor, "contact.open", alertsHandler)
			// } else if (settings?.myDoorS == "closed") {
			// 	subscribe(settings?.myDoor, "contact.closed", alertsHandler)
			// } else { subscribe(settings?.myDoor, "contact", alertsHandler) }
			subscribe(settings?.myDoor, "contact", alertsHandler)
		}
		if (settings?.myWindow) {
			// if (settings?.myWindowS == "open") {
			// 	subscribe(settings?.myWindow, "contact.open", alertsHandler)
			// } else if (settings?.myWindowS == "closed") {
			// 	subscribe(settings?.myWindow, "contact.closed", alertsHandler)
			// } else { subscribe(settings?.myWindow, "contact", alertsHandler) }
			subscribe(settings?.myWindow, "contact", alertsHandler)
		}
		if (settings?.myValve) {
			// if (settings?.myValveS == "open") {
			// 	subscribe(settings?.myValve, "valve.open", alertsHandler)
			// } else if (settings?.myValveS == "closed") {
			// 	subscribe(settings?.myValve, "valve.closed", alertsHandler)
			// } else { subscribe(settings?.myValve, "valve", alertsHandler) }
			subscribe(settings?.myValve, "valve", alertsHandler)
		}
		if (settings?.myShades) {
			// if (settings?.myShadesS == "open") {
			// 	subscribe(settings?.myShades, "contact.open", alertsHandler)
			// } else if (settings?.myShadesS == "closed") {
			// 	subscribe(settings?.myShades, "contact.closed", alertsHandler)
			// } else { subscribe(settings?.myShades, "contact", alertsHandler) }
			subscribe(settings?.myShades, "contact", alertsHandler)
		}
		if (settings?.myMotion) {
			// if (settings?.myMotionS == "active") {
			// 	subscribe(settings?.myMotion, "motion.active", alertsHandler)
			// } else if (settings?.myMotionS == "inactive") {
			// 	subscribe(settings?.myMotion, "motion.inactive", alertsHandler)
			// } else { subscribe(settings?.myMotion, "motion", alertsHandler) }
			subscribe(settings?.myMotion, "motion", alertsHandler)
		}
		if (settings?.myLocks) {
			// if (settings?.myLocksS == "locked") {
			// 	subscribe(settings?.myLocks, "lock.locked", alertsHandler)
			// } else if (settings?.myLocksS == "unlocked") {
			// 	if (settings?.myLocksSCode) {
			// 		subscribe(settings?.myLocks, "lock", unlockedWithCodeHandler)
			// 	} else { subscribe(settings?.myLocks, "lock.unlocked", alertsHandler) }
			// } else { subscribe(settings?.myLocks, "lock", alertsHandler) }
			subscribe(settings?.myLocks, "lock", alertsHandler)
		}
		if (settings?.myPresence) {
			// if (settings?.myPresenceS == "present") {
			// 	subscribe(settings?.myPresence, "presence.present", alertsHandler) }
			// else if (settings?.myPresenceS == "not present"){
			// 	subscribe(settings?.myPresence, "presence.not present", alertsHandler)
			// } else { subscribe(settings?.myPresence, "presence", alertsHandler)	}
			subscribe(settings?.myPresence, "presence", alertsHandler)
		}
		if (settings?.myTstat) {
			if (settings?.myTstatS == "cooling") {
				subscribe(settings?.myTstat, "coolingSetpoint", alertsHandler)
			} else if (settings?.myTstatS == "heating") {
				subscribe(settings?.myTstat, "heatingSetpoint", alertsHandler)
			} else {
				subscribe(settings?.myTstat, "coolingSetpoint", alertsHandler)
				subscribe(settings?.myTstat, "heatingSetpoint", alertsHandler)
			}
			
			// if (settings?.myTstatM == "auto") {
			// 	subscribe(settings?.myTstat, "thermostatMode.auto", alertsHandler)
			// } else if (settings?.myTstatM == "cool") {
			// 	subscribe(settings?.myTstat, "thermostatMode.cool", alertsHandler)
			// } else if (settings?.myTstatM == "heat") {
			// 	subscribe(settings?.myTstat, "thermostatMode.heat", alertsHandler)
			// } else if (settings?.myTstatM == "off") {
			// 	subscribe(settings?.myTstat, "thermostatMode.off", alertsHandler)
			// } else { subscribe(settings?.myTstat, "thermostatMode", alertsHandler) }
			subscribe(settings?.myTstat, "thermostatMode", alertsHandler)

			// if (settings?.myTstatOS == "cooling") {
			// 	subscribe(settings?.myTstat, "thermostatOperatingState.cooling", alertsHandler)
			// } else if (settings?.myTstatOS == "heating") {
			// 	subscribe(settings?.myTstat, "thermostatOperatingState.heating", alertsHandler)
			// } else if (settings?.myTstatOS == "idle") {
			// 	subscribe(settings?.myTstat, "thermostatOperatingState.idle", alertsHandler)
			// } else { subscribe(settings?.myTstat, "thermostatOperatingState", alertsHandler) }
			subscribe(settings?.myTstat, "thermostatOperatingState", alertsHandler)
		}
		if (settings?.mySmoke) {
			// if (settings?.mySmokeS == "detected") {
			// 	subscribe(settings?.mySmoke, "smoke.detected", alertsHandler)
			// } else if (settings?.mySmokeS == "clear") {
			// 	subscribe(settings?.mySmoke, "smoke.clear", alertsHandler)
			// } else { subscribe(settings?.mySmoke, "smoke", alertsHandler) }
			subscribe(settings?.mySmoke, "smoke", alertsHandler)
		}
		if (settings?.myWater) {
			// if (settings?.myWaterS == "wet") {
			// 	subscribe(settings?.myWater, "water.wet", alertsHandler)
			// } else if (settings?.myWaterS == "dry") {
			// 	subscribe(settings?.myWater, "water.dry", alertsHandler)
			// } else {  }
			subscribe(settings?.myWater, "water", alertsHandler)
		}
		if (settings?.myTemperature) { subscribe(settings?.myTemperature, "temperature", tempHandler) }
		if (settings?.myCO2) { subscribe(settings?.myCO2, "carbonDioxide", carbonDioxideHandler) }
		if (settings?.myCO) {
			if (settings?.myCOS== "detected") {
				subscribe(settings?.myCO, "carbonMonoxide.detected", alertsHandler)
			} else if (settings?.myCOS== "tested") {
				subscribe(settings?.myCO, "carbonMonoxide.tested", alertsHandler)
			} else { subscribe(settings?.myCO, "carbonMonoxide", alertsHandler) }
		}
		if (settings?.myHumidity) { subscribe(settings?.myHumidity, "humidity", humidityHandler) }
		if (settings?.mySound) { subscribe(settings?.mySound, "soundPressureLevel", soundHandler) }
		if (settings?.myAcceleration) {
			if (settings?.myAccelerationS == "active") {
				subscribe(settings?.myAcceleration, "acceleration.active", alertsHandler)
			} else if (settings?.myAccelerationS == "inactive") {
				subscribe(settings?.myAcceleration, "acceleration.inactive", alertsHandler)
			} else { subscribe(settings?.myAcceleration, "acceleration", alertsHandler) }
		}
	}
}

void stateCleanup() {
	["lastPlayed","lastEvent","sound", "soundIntro","speechSound","lastTime","lastWeather","lastWeatherCheck","lastAlert","message","speechVolume", "mySpeechSound", "retriggerSchedActive", "lastEventData"]?.each { if(state?.containsKey(it as String)) { state?.remove(it) } }
	["cycleOnH","cycleOnL","cycleOnA","cycleOnB","savedOffset","cycleTh","cycleTl","cycleSh","cycleSl","cycleHh","cycleHl","cycleCO2h","cycleCO2l"]?.each { state[it as String] = false	}
	state?.occurrences = 0
}

/************************************************************************************************************
   RUNNING ADD-HOC REPORT
************************************************************************************************************/
def runProfile(profile) {
	def result
	if (settings?.reportMessage && isAdHoc()) {
		// date, time and profile variables
		result = settings?.reportMessage ? "${settings?.reportMessage}".replace("&date", "${getVariable("date")}")?.replace("&time", "${getVariable("time")}")?.replace("&profile", "${getVariable("profile")}") : null
		// power variables
		result = result ? "$result"?.replace("&power", "${getVariable("power")}")?.replace("&lights", "${getVariable("lights")}") : null
		// garage doors, locks and precence variables
		result = result ? "$result"?.replace("&garage", "${getVariable("garage")}")?.replace("&unlocked", "${getVariable("unlocked")}")?.replace("&present", "${getVariable("present")}") : null
		// shades, valves, contacts, motion variables
		result = result ? "$result"?.replace("&shades", "${getVariable("shades")}")?.replace("&open", "${getVariable("open")}")?.replace("&motion", "${getVariable("motion")}") : null
		result = result ? "$result"?.replace("&valves", "${getVariable("valves")}")?.replace("&windows", "${getVariable("windows")}")?.replace("&doors", "${getVariable("doors")}") : null
		// location variables
		result = result ? "$result"?.replace("&mode", "${getVariable("mode")}")?.replace("&shm", "${getVariable("shm")}") : null
		//climate variables
		result = result ? "$result"?.replace("&temperature", "${getVariable("temperature")}")?.replace("&indoorhum", "${getVariable("indoorhum")}")  : null
		//thermostat
		result = result ? "$result"?.replace("&heating", "${getVariable("heating")}")?.replace("&cooling", "${getVariable("cooling")}")?.replace("&thermostat", "${getVariable("thermostat")}")?.replace("&running", "${getVariable("running")}") : null
		// water, smoke, COs and noise and contacts variables
		result = result ? "$result"?.replace("&smoke", "${getVariable("smoke")}")?.replace("&CO2", "${getVariable("CO2")}")?.replace("&water", "${getVariable("water")}") : null
		//weather variables
		result = getWeatherVar(result)
	}
	if (settings?.reportMessage && isCustText()) {
		def device = "<< Device Label >>"
		def action = " << Action (on/off, open/closed, locked/unlocked) >>"
		def event =  " << Capability (switch, contact, motion, lock, etc) >>"
		def last =  " << Time of last event (only available when used with pending actions) >>"
		result = settings?.reportMessage ? "${settings?.reportMessage}".replace("&date", "${getVariable("date")}")?.replace("&time", "${getVariable("time")}")?.replace("&profile", "${getVariable("profile")}") : null
		result = result ? "$result".replace("&device", "${device}")?.replace("&action", "${action}")?.replace("&event", "${event}")?.replace("&last", "${last}") : null
	}
	if (settings?.reportMessage && isCustTextWeather()) {
		def device = "<< Device Label >>"
		def action = " << Action (on/off, open/closed, locked/unlocked) >>"
		def event =  " << Capability (switch, contact, motion, lock, etc) >>"
		def last =  " << Time of last event (only available when used with pending actions) >>"
		result = settings?.reportMessage ? "${settings?.reportMessage}".replace("&date", "${getVariable("date")}")?.replace("&time", "${getVariable("time")}")?.replace("&profile", "${getVariable("profile")}") : null
		result = result ? "$result".replace("&device", "${device}")?.replace("&action", "${action}")?.replace("&event", "${event}")?.replace("&last", "${last}")  : null
	   	result = getWeatherVar(result)
	}
	if (settings?.reportMessage && profile == "test") {
		result = "This is your Sample Report with current data:  \n\n"+ result
   		return result
	}
	else {
		if (isAdHoc() && settings?.reportMessage) {
			return result
	 	} else {
			 result = "Sorry you can only generate an ad-hoc report that has a custom message"
		}
		log.warn "sending Report to Main App: $result"
	}

}

/************************************************************************************************************
   REPORT VARIABLES
************************************************************************************************************/
//getVariable(var)
String getVariable(var) {
	List devList = []
	String result = null
	switch(var) {
		case "time":
			result = new Date(now()).format("h:mm aa", location?.timeZone)
			break
		case "date":
			result = new Date(now()).format("EEEE, MMMM d, yyyy", location?.timeZone)
			break

		case "profile":
			result = app.getLabel()
			break

		case "mode":
			result = location?.currentMode
			break

		case "shm":
			def sSHM = location?.currentState("alarmSystemStatus")?.value
			result = sSHM == "off" ? "disabled" : sSHM == "away" ? "Armed Away" : sSHM == "stay" ? "Armed Home" : "unknown"
			break

		case "power":
			if (settings?.myPower) {
				def meterValueRaw = settings?.myPower?.currentValue("power") as double
				result = meterValueRaw ?: 0
			}
			break

		case "open":
			if (settings?.myContact) {
				devList = getDevsByAttrState(settings?.myContact, "contact", "open")
				result = varResultBuilder(devList, " sensor", " sensors", "no sensors")
			}
			break

		case "doors":
			if (settings?.myDoor) {
				devList = getDevsByAttrState(settings?.myDoor, "contact", "open")
				result = varResultBuilder(devList, " door", " doors", "no doors")
			}
			break

		case "windows":
			if (settings?.myWindow) {
				devList = getDevsByAttrState(settings?.myWindow, "contact", "open")
				result = varResultBuilder(devList, " window", " windows", "no windows")
			}
			break

		case "valves":
			if (settings?.myValve) {
				devList = getDevsByAttrState(settings?.myValve, "valve", "open")
				result = varResultBuilder(devList, " valve", " valves", "no valves")
			}
			break

		case "unlocked":
			if (settings?.myLocks) {
				devList = getDevsByAttrState(settings?.myLocks, "lock", "unlocked")
				result = varResultBuilder(devList, " door", " doors", "no doors")
			}
			break

		case "present":
			if (settings?.myPresence) {
				devList = getDevsByAttrState(settings?.myPresence, "presence", "present")
				result = varResultBuilder(devList, " person", " people", "no people")
			}
			break

		case "lights":
			if (settings?.mySwitch) {
				devList = getDevsByAttrState(settings?.mySwitch, "switch", "on")
				result = varResultBuilder(devList, " switch", " switches", "no switches")
			}
			break

		case "thermostat":
			if (settings?.myTstat) {
				def currentMode
				settings?.myTstat.each { dev ->
					devList += dev?.displayName + " is set to "+ dev.currentValue("thermostatMode")
				}
				if (devList) { result = devList } else { result = "unknown" }
			}
			break

		case "heating":
			if (settings?.myTstat) {
				settings?.myTstat.each { dev ->
					devList += dev?.displayName + " heating set point is "+ dev?.currentValue("heatingSetpoint")
				}
				if (devList) { result = devList } else { result = "unknown" }
			}
			break

		case "cooling":
			if (settings?.myTstat) {
				settings?.myTstat.each { dev ->
					devList += dev?.displayName + " cooling set point is "+ dev?.currentValue("coolingSetpoint")
				}
				if (devList) { result = devList } else { result = "unknown" }
			}
			break

		case "running":
			if (settings?.myTstat) {
				settings?.myTstat.each { dev -> devList += dev.currentState("thermostatOperatingState")?.stringValue }
				if (state?.showDebug) { log.warn "size = ${devList?.size()}, $devList " }
				if (devList && devList.contains("idle")) { result = "not running" }
				else if (devList && (devList.contains("cooling") || devList.contains("heating"))) { result = "running" }
				else if (devList && devList.contains("fan only")) { result = "running the fan only" }
				else { result = "unknown" }
			}
			break

		case "temperature":
			if (settings?.myTemperature) { result = getDeviceVarAvg(settings?.myTemperature, "temperature") }
			break

		case "indoorhum":
			if (settings?.myHumidity) { result = getDeviceVarAvg(settings?.myHumidity, "humidity") }
			break

		case "CO2":
			if (settings?.myCO2) { result = getDeviceVarAvg(settings?.myCO2, "carbonDioxide") }
			break

		case "noise":
			if (settings?.mySound) { result = getDeviceVarAvg(settings?.mySound, "soundPressureLevel") }
			break

		case "motion":
			if (settings?.myMotion) {
				devList = getDevsByAttrState(settings?.myMotion, "motion", "active")
				result = varResultBuilder(devList, " motion sensor", " motion sensors", "no motion sensors")
			}
			break

		case "garage":
			Integer devCnt = 0
			if (settings?.myGarage) {
				devCnt = devCnt + settings?.myGarage?.size()
				devList = devList + getDevsByAttrState(settings?.myGarage, "contact", "open")
			}
			/* Bobby 8/20/18
			if (settings?.myRelayContact) {
				devCnt = devCnt + settings?.myRelayContact?.size()
				devList = devList + getDevsByAttrState(settings?.myRelayContact, "contact", "open")
			}
            */
			def closedDoors = devCnt - devList?.size()
			result = varResultBuilder(devList, " garage door is open", " of the Garage doors are open and ${closedDoor} closed", "no garage doors are open")
			break

		case "shades":
			if (settings?.myShades) {
				devList = getDevsByAttrState(settings?.myShades, "contact", "open")
				def closedShades = settings?.myShades?.size() - devList?.size()
				result = varResultBuilder(devList, " of the shades is open and ${closedShades} closed", " of the shades are open and ${closedShades} closed", "no shades are open")
			}
			break

		case "smoke":
			if (settings?.mySmoke) {
				devList = getDevsByAttrState(settings?.mySmoke, "smoke", "detected")
				result = varResultBuilder(devList, " sensor detected smoke", " sensors detected smoke", "no sensors detected smoke")
			}
			break
	}
	return result
}

String varResultBuilder(List devList, oneStr, multiStr, noStr) {
	if (devList?.size() == 1) {
		return "${devList?.size()} ${oneStr}"
	} else if (devList?.size() > 0) {
		return "${devList?.size()} ${multiStr}"
	} else {
		return "${noStr}"
	}
}

def isPluralString(obj) {
	return (obj?.size() > 1) ? "(s)" : ""
}

/***********************************************************************************************************************
	POWER HANDLER
***********************************************************************************************************************/
def meterHandler(evt) {
	def evtValue = evt.value
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName
	int delay = settings?.myPowerMinutes ?: 0
	int meterValueRaw = evt.value as double
	int meterValue = meterValueRaw ?: 0 as int
	int thresholdValue = settings?.myPowerThresholdStart == null ? 0 : settings?.myPowerThresholdStart as int
	int thresholdStopValue = settings?.myPowerThresholdStop == null ? 0 : settings?.myPowerThresholdStop as int
	def cycleOnHigh = state.cycleOnH
	def cycleOnLow = state.cycleOnL
	if (settings?.myPowerS == "above threshold") {
		thresholdStopValue = thresholdStopValue == 0 ? 9999 :  thresholdStopValue as int
		if (meterValue >= thresholdValue && meterValue <= thresholdStopValue ) {
			if (cycleOnHigh == false) {
				state.cycleOnH = true
				if (state?.showDebug) { log.debug "Power meter $meterValue is above threshold $thresholdValue with threshold stop $thresholdStopValue" }
				if (delay) {
					log.warn "scheduling delay ${delay}, ${60*delay}"
					runIn(60*delay, bufferPendingH)
				} else {
					if (state?.showDebug) { log.debug "sending notification (above)" }
					alertsHandler([value:"above threshold", name:"power", device:"power meter"])
				}
			}
		} else {
			state.cycleOnH = false
			unschedule("bufferPendingH")
			//log.debug "Power exception (above) meterValue ${meterValue}, thresholdValue ${thresholdValue}, stop ${thresholdStopValue} "
		}
	}
	if (settings?.myPowerS == "below threshold") {
		if (meterValue <= thresholdValue && meterValue >= thresholdStopValue) {
			if (cycleOnLow == false) {
				state.cycleOnL = true
				if (state?.showDebug) { log.debug "Power meter $meterValue is below threshold $thresholdValue with threshold stop $thresholdStopValue" }
				if (delay) {
					if (state?.showDebug) { log.warn "scheduling delay ${delay}, ${60*delay}" }
					runIn(60*delay, bufferPendingL)
				} else {
					if (state?.showDebug) { log.debug "sending notification (below)" }
					alertsHandler([value:"below threshold", name:"power", device:"power meter"])
				}
			}
		} else {
			state.cycleOnL = false
			unschedule("bufferPendingL")
			//log.debug "Power exception (below) meterValue ${meterValue}, thresholdValue ${thresholdValue}, stop ${thresholdStopValue}"
		}
	}
}

def bufferPendingH() {
	def meterValueRaw = settings?.myPower.currentValue("power") as double
	int meterValue = meterValueRaw ?: 0 as int
	def thresholdValue = settings?.myPowerThresholdStart == null ? 0 : settings?.myPowerThresholdStart as int
	if (meterValue >= thresholdValue) {
		if (state?.showDebug) { log.debug "sending notification (above)" }
		alertsHandler([value:"above threshold", name:"power", device:"power meter"])
	}
}

def bufferPendingL() {
	def meterValueRaw = settings?.myPower.currentValue("power") as double
	int meterValue = meterValueRaw ?: 0 as int
	def thresholdValue = settings?.myPowerThresholdStart == null ? 0 : settings?.myPowerThresholdStart as int
	if (meterValue <= thresholdValue) {
		if (state?.showDebug) { log.debug "sending notification (below)" }
		alertsHandler([value:"below threshold", name:"power", device:"power meter"])
  	}
}
/************************************************************************************************************
   UNLOCKED WITH USER CODE
************************************************************************************************************/
def unlockedWithCodeHandler(evt) {
	def event = evt.data
	def evtValue = evt.value
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	def evtDescText = evt.descriptionText
	def eData = parseJson(evt.data)
    def data = [:]
	def eTxt = evtDispName + " was " + evtValue //evt.descriptionText
	if (state?.showDebug) { log.info "unlocked event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName, evtDescText = $evtDescText, eTxt = $eTxt" }
	if (evtValue == "unlocked" && myLocksSCode && event) {
		//this is no longer valid!!!!!!!!!!!!!!! Bobby 8/19/18
        //def userCode = evt.data.replaceAll("\\D+","")
		def userCode = eData?.usedCode
        userCode = userCode?.isNumber() ? userCode as Integer : null //userCode.toInteger()
		int usedCode = userCode
    if (settings?.myLocksSCode == usedCode) {
			eTxt = settings?.reportMessage ? settings?.reportMessage.replace("&device", "${evtDispName}")?.replace("&event", "time")?.replace("&action", "executed")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}") : evtDescText
			data = [value:eTxt, name:"unlocked with code", device:"lock"]
			alertsHandler(data)
		}
	}
}
/************************************************************************************************************
   Button NUMBER
************************************************************************************************************/
def buttonNumHandler(evt) {
	def event = evt.data
	def evtValue = evt.value
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
		if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName
	def evtDescText = evt.descriptionText
	def bTN
	def eTxt = evtDispName + " is " + evtValue //evt.descriptionText
	if (state?.showDebug) { log.info "button event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName, evtDescText = $evtDescText, eTxt = $eTxt" }
	if (evtValue == "pushed" || evtValue == "held") {
		def buttonNumUsed = evt?.data?.replaceAll("\\D+","")
		buttonNumUsed = buttonNumUsed.toInteger()
	   	int butNum = buttonNumUsed
		log.warn "button num = $butNum, value = $evtValue"
		bTN = settings.myButtonNum ?: 1
		if (bTN == butNum) {
			eTxt = settings?.reportMessage ? settings?.reportMessage?.replace("&device", "${evtDispName}")?.replace("&event", "time")?.replace("&action", "executed")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}") : evtDescText
			alertsHandler([value:evtValue, name:evtName, device:evtDispName])
		}
	}
}
/***********************************************************************************************************************
	TEMPERATURE HANDLER
***********************************************************************************************************************/
def tempHandler(evt) {
	def evtValue = evt.value
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName
	if (state?.showDebug) { log.info "event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName" }
	def tempAVG = settings?.myTemperature ? getAverage(settings?.myTemperature, "temperature") : "undefined device"
	def cycleThigh = state.cycleTh
	def cycleTlow = state.cycleTl
	def currentTemp = tempAVG
	int temperatureStopVal = settings?.temperatureStop == null ? 0 : settings?.temperatureStop as int
	if (state?.showDebug) { log.warn "currentTemp = $currentTemp" }
	if (settings?.myTemperatureS == "above") {
		temperatureStopVal = temperatureStopVal == 0 ? 999 : temperatureStopVal as int
		if (currentTemp >= settings?.temperature && currentTemp <= temperatureStopVal) {
			if (cycleThigh == false) {
				state.cycleTh = true
				if (state?.showDebug) { log.debug "sending notification (above): as temperature $currentTemp is above threshold ${settings?.temperature}" }
				alertsHandler([value:"above ${settings?.temperature} degrees", name:"temperature", device:"temperature sensor"])
			}
		} else { state.cycleTh = false }
	}
	if (myTemperatureS == "below") {
		if (currentTemp <= settings?.temperature && currentTemp >= temperatureStopVal) {
			if (cycleTlow == false) {
				state.cycleTl = true
				if (state?.showDebug) { log.debug "sending notification (below): as temperature $currentTemp is below threshold ${settings?.temperature}" }
				alertsHandler([value:"below ${settings?.temperature} degrees", name:"temperature", device:"temperature sensor"])
			}
		} else { state.cycleTl = false }
	}
}

/******************************************************************************
	 FEEDBACK SUPPORT - GET AVERAGE
******************************************************************************/
def getAverage(device,type) {
	def total = 0
	if (state?.showDebug) { log.debug "calculating average temperature" }
	device.each { total += it.latestValue(type) }
	return Math.round(total/device?.size())
}

/***********************************************************************************************************************
	HUMIDITY HANDLER
***********************************************************************************************************************/
def humidityHandler(evt) {
	def evtValue = evt.value
		evtValue = evtValue as int
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName
	if (state?.showDebug) { log.info "event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName" }
	if (settings?.myHumidityS == "above") {
		if (evtValue >= settings?.humidity) {
			if (state.cycleHh == false) {
				state.cycleHh = true
				if (state?.showDebug) { log.debug "sending notification (above): as humidity $evtValue is above threshold ${settings?.humidity}" }
				alertsHandler([value:"above ${settings?.humidity}", name:"humidity", device:"humidity sensor"])
			}
		} else { state.cycleHh = false }
	} else {
		if (settings?.myHumidityS == "below") {
			if (evtValue <= settings?.humidity) {
				if (state.cycleHl == false) {
					state.cycleHl = true
					if (state?.showDebug) { log.debug "sending notification (below): as humidity $evtValue is below threshold ${settings?.humidity}" }
					alertsHandler([value:"below ${settings?.humidity}", name:"humidity", device:"humidity sensor"])
				}
			} else { state.cycleHl = false }
		}
	}
}


/***********************************************************************************************************************
	SOUND HANDLER
***********************************************************************************************************************/
def soundHandler(evt) {
	def evtValue = evt.value
		evtValue = evtValue as int
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName

	if (state?.showDebug) { log.info "event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName" }
	if (settings?.mySoundS == "above") {
		if (evtValue >= settings?.noise) {
			if (state.cycleSh == false) {
				state.cycleSh = true
				if (state?.showDebug) { log.debug "sending notification (above): as noise $evtValue is above threshold ${settings?.noise}" }
				alertsHandler([value:"above ${settings?.noise}", name:"noise", device:"sound sensor"])
			}
		} else { state.cycleSh = false }
	} else {
		if (settings?.mySoundS == "below") {
			if (evtValue <= settings?.noise) {
				if (state.cycleSl == false) {
					state.cycleSl = true
					if (state?.showDebug) { log.debug "sending notification (below): as noise $evtValue is below threshold ${settings?.noise}" }
					alertsHandler([value:"below ${settings?.noise}", name:"noise", device:"sound sensor"])
				}
			} else { state.cycleSl = false }
		}
	}
}


/***********************************************************************************************************************
	CO2 HANDLER
***********************************************************************************************************************/
def carbonDioxideHandler(evt) {
	def evtValue = evt.value ? (evt?.value?.isNumber() ? evt?.value as Integer : evt?.value) : null
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDispName = evt.displayName
	if (evtDispName == null) { evtDispName = evtDevice } // 5/28/2017 evtName
	if (state?.showDebug) { log.info "event received: event = $event, evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName" }
	if (settings?.myCO2S == "above") {
		if (evtValue >= settings?.CO2) {
			if (state.cycleCO2h == false) {
				state.cycleCO2h = true
				if (state?.showDebug) { log.debug "sending notification (above): as CO2 $evtValue is above threshold ${settings?.CO2}" }
				alertsHandler([value:"${evtValue}", name:"CO2", device:"CO2 sensor"])
			}
		} else { state.cycleCO2h = false }
	} else {
		if (settings?.myCO2S == "below") {
			if (evtValue <= settings?.CO2) {
				if (state.cycleCO2l == false) {
					state.cycleCO2l = true
					if (state?.showDebug) { log.debug "sending notification (below): as CO2 $evtValue is below threshold ${settings?.CO2}" }
					alertsHandler([value:"${evtValue}", name:"CO2", device:"CO2 sensor"])
				}
			} else { state.cycleCO2l = false }
		}
	}
}


/************************************************************************************************************
   EVENTS HANDLER
************************************************************************************************************/
def getDeviceCapName(evtName) {
	if(!evtName) { return null }
	switch(evtName) {
		case "switch":
			return "mySwitch"
		case "motion":
			return "myMotion"
		case "contact":
			return "myContact"
		case "acceleration":
			return "myAcceleration"
		case "valve":
			return "myValve"
		case "lock":
			return "myLocks"
		case "garageDoorControl":
			return "myGarage"
		default:
			return null
	}
}

def alertsHandler(evt) {
	def event = evt.data
	def evtValue = evt.value
	def evtName = evt.name
	def evtDevice = evt.device
	def evtDeviceId = evt.deviceId
	def evtDispName = evt.displayName
	def evtDescText = evt.descriptionText
	if (evtDispName == null) { evtDispName = evtDevice }
	log.warn "retrigger occurrences = ${state.occurrences}"
	String eTxt = "$evtDispName is $evtValue"
	if (state?.showDebug) { 
		log.info "event received: evtValue = $evtValue, evtName = $evtName, evtDevice = $evtDevice, evtDispName = $evtDispName, evtDescText = $evtDescText, eTxt = $eTxt"
		log.warn "version number = ${appVersion()}" 
	}
	String dCapability = getDeviceCapName(evtName)
	Integer delayMinutes = (dCapability && settings["${dCapability}Minutes"]) ? settings["${dCapability}Minutes"] : null
	Map data = [deviceName: evtDevice.label, attributeName: evtValue, capabilityName: "${evtName}", inputName: dCapability]
	state?.lastEventData = data
	if(ok2Proceed()) {
		if (dCapability && delayMinutes && evtName != "delay") {	
			log.warn "scheduling delay with data: $data"
			state.lastEvent = new Date(now()).format("h:mm aa", location.timeZone)
			runIn(delayMinutes * 60, checkDevices)
		} else {
			//FAST LANE AUDIO DELIVERY METHOD
			if (isDefault()) {
				if (settings?.mySpeechDevices) {
					settings?.mySpeechDevices?.each { d->  if(d?.hasCommand("playTextAndResume")) { d?.playTextAndResume(eTxt) } }
				} else {
					if (settings?.mySonosDevices) {
						def sCommand = settings?.mySonosResume == true ? "playTrackAndResume" : "playTrackAndRestore"
						def sTxt = textToSpeech(eTxt instanceof List ? eTxt[0] : eTxt, settings?.ttsVoiceStyle.substring(6))
						def sVolume = settings.mySonosVolume ?: 20
						settings?.mySonosDevices?.each { d-> if(d?.hasCommand("${sCommand}")) { d?."${sCommand}"(sTxt?.uri, sTxt?.duration, sVolume) } }
					}
				}
				if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings.pushoverEnabled && settings?.pushoverDevices)) { sendtxt(eTxt) }
				if (settings?.myNotifyDevice) { settings?.myNotifyDevice.deviceNotification(settings?.reportMessage) }
				if (settings?.askAlexa && settings?.askAlexaMQs) { sendToAskAlexa(eTxt) }
			} else {
				if (isTrigger() && settings?.myAdHocReport) {
					eTxt = null
					if (evtName == "routineExecuted" || evtName == "mode") {
						if (evtName == "routineExecuted" && settings?.myRoutine) {
							def rtMatch = settings?.myRoutine?.find {r -> r == evtDispName}
							if (rtMatch != null) { eTxt = parent.runReport(settings?.myAdHocReport) }
						}
						if (evtName == "mode" && settings?.myMode) {
							def mdMatch = settings?.myMode?.find {m -> m == evtValue}
							if (mdMatch) { eTxt = parent.runReport(settings?.myAdHocReport) }
						}
					} else { eTxt = parent.runReport(settings?.myAdHocReport) }
				}
				def eProfile = app?.getLabel()
				def nRoutine = false
				def stamp = state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)
				def today = new Date(now()).format("EEEE, MMMM d, yyyy", location.timeZone)
				def last = state.lastEvent
				if (settings?.playCustIntroSound) {
					playIntroSound()
				}
				if (evtName == "time of day" && settings?.reportMessage && !isTrigger()) {
					eTxt = settings?.reportMessage ? settings?.reportMessage.replace("&device", "${evtDispName}")?.replace("&event", "time")?.replace("&action", "executed")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}") : null
					if (isCustTextWeather()) { eTxt = getWeatherVar(eTxt) }
				}
				if (evtName == "coolingSetpoint" || evtName == "heatingSetpoint") {
					evtValue = evt.value.toFloat()
					evtValue = Math.round(evtValue)
				}
				if (evtName == "routineExecuted" && settings?.myRoutine && !isTrigger()) {
					def deviceMatch = settings?.myRoutine?.find {r -> r == evtDispName}
					if (deviceMatch) {
						eTxt = settings?.reportMessage ? settings?.reportMessage.replace("&device", "${evtDispName}")?.replace("&event", "routine")?.replace("&action", "executed")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}") : null
						if (isCustTextWeather()) { eTxt = getWeatherVar(eTxt) }
						if (settings?.reportMessage) {
							if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings.pushoverEnabled && settings?.pushoverDevices)) {
								sendtxt(eTxt)
							}
							if (settings?.myNotifyDevice) { settings?.myNotifyDevice.deviceNotification(message) }
							takeAction(eTxt)
						} else {
							eTxt = "routine was executed"
							takeAction(eTxt)
						}
					}
				} else {
					if (evtName == "mode" && settings?.myMode && !isTrigger()) {
						def deviceMatch = settings?.myMode?.find {m -> m == evtValue}
						if (deviceMatch) {
							eTxt = settings?.reportMessage ? settings?.reportMessage.replace("&device", "${evtValue}")?.replace("&event", "${evtName}")?.replace("&action", "changed")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}") : null
							if (isCustTextWeather()) { eTxt = getWeatherVar(eTxt) }
							if (settings?.reportMessage) {
								if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings.pushoverEnabled && settings?.pushoverDevices)) {
									sendtxt(eTxt)
								}
								if (settings?.myNotifyDevice) { settings?.myNotifyDevice.deviceNotification(settings?.reportMessage) }
								takeAction(eTxt)
							} else {
								eTxt = "location mode has changed"
								takeAction(eTxt)
							}
						}
					} else {
						if (settings?.reportMessage || isTrigger()) {
							if (settings?.reportMessage) {
								eTxt = settings?.reportMessage ? settings?.reportMessage.replace("&device", "${evtDevice}")?.replace("&event", "${evtName}")?.replace("&action", "${evtValue}")?.replace("&date", "${today}")?.replace("&time", "${stamp}")?.replace("&profile", "${eProfile}")?.replace("&last", "${last}") : null
								if (isCustTextWeather()) { eTxt = getWeatherVar(eTxt) }
							}
							if (eTxt) {
								if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings.pushoverEnabled && settings?.pushoverDevices)) { sendtxt(eTxt) }
								if (settings?.myNotifyDevice) { settings?.myNotifyDevice.deviceNotification(settings?.reportMessage) }
								takeAction(eTxt)
							}
						} else {
							if (evtDevice == "weather") {
								if (evtDispName == "weather alert") {
									eTxt = evtValue
								} else { eTxt = evtDispName + " is " + evtValue }
							}
							if (eTxt) {
								if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings.pushoverEnabled && settings?.pushoverDevices)) {
									// if (state?.showDebug) { log.info "sending sms" }
									sendtxt(eTxt)
								}
								if (settings?.myNotifyDevice) { settings?.myNotifyDevice.deviceNotification(settings?.reportMessage) }
								if (state?.showDebug) { log.info "processing eTxt = $eTxt" }
								takeAction(eTxt)
							}
						}
					}
				}
			}
		}
	} else { 
		unscheduleRetrigger() 
	}
}

private checkDevices() {
	Map data = state?.lastEventData
	log.warn "received runIn data: device = ${data?.deviceName}, attribute = ${data?.attributeName}, capability = ${data?.capabilityName}, type = ${data?.inputName}"
	if (data?.size() && getDevicesOk(data)) {
		if (state?.showDebug) { log.debug "pushing notification after delay" }
		alertsHandler([value: data?.attributeName, name: "delay", device: data?.deviceName])
	}
}

Boolean getDevicesOk(Map data) {
	Boolean devOk = true
	if(data?.size() && data?.inputName) {
		def dev = settings?."${data?.inputName}"?.find {d -> d?.displayName == data?.deviceName}
		if(dev && dev?.hasAttribute(data?.capabilityName)) {
			String compVal = settings?."${data?.inputName}S" ?: data?.attributeName
			devOk = (dev?.currentState(data?.capabilityName as String)?.stringValue == compVal)
		}
	}
	return devOk
}

Boolean getConditionsOk() {
	List devList = []
	Boolean devcheck = ((settings?.rSwitch && settings?.rSwitchS) || (settings?.rMotion && settings?.rMotionS) || (settings?.rContact && settings?.rContactS) || (settings?.rPresence && settings?.rPresenceS))
	Integer devCnts = ((settings?.rSwitch?.size()?:0) + (settings?.rMotion?.size()?:0) + (settings?.rContact?.size()?:0) + (settings?.rPresence?.size()?:0))
	if (settings?.rSwitch && settings?.rSwitchS) {
		List devs = getDevsByAttrState(settings?.rSwitch, "switch", settings?.rSwitchS)
		devList = devList + devs
		log.warn "rSwitch list is ${devs} for state ${settings?.rSwitchS}"
	}
	if (settings?.rMotion && settings?.rMotionS) {
		List devs = getDevsByAttrState(settings?.rMotion, "motion", settings?.rMotionS)
		devList = devList + devs
		log.warn "rMotion list is ${devs} for state ${settings?.rMotionS}"
	}
	if (settings?.rContact && settings?.rContactS) {
		List devs = getDevsByAttrState(settings?.rContact, "contact", settings?.rContactS)
		devList = devList + devs
		log.warn "rContact list is ${devs} for state ${settings?.rContactS}"
	}
	if (settings?.rPresence && settings?.rPresenceS) {
		List devs = getDevsByAttrState(settings?.rPresence, "presence", settings?.rPresenceS)
		devList = devList + devs
		log.warn "rPresence list is ${devs} for state ${settings?.rPresenceS}"
	}
	Boolean result = devcheck ? (devList?.size() == devCnts) : true
	// log.debug "getConditionsOk = ${result} | devcheck: ${devcheck} | devList: ${devList}"
	return result
}

/***********************************************************************************************************************
	TAKE ACTIONS HANDLER
***********************************************************************************************************************/
private takeAction(eTxt) {
	//Sending Data to 3rd parties
	def data = [args: eTxt]
	sendLocationEvent(name:"echoSistantProfile",value: app.getLabel(), isStateChange: true, displayed: false, data: data)
	//sendLocationEvent([name:i,value:app.label,isStateChange:true,displayed:false,data:data])
	// if (state?.showDebug) { log.debug "sendNotificationEvent sent to 3rd party as ${app.label} was active" }
	state.savedOffset = false
	def sVolume
	def sTxt
	//int prevDuration
	Double prevDuration
	if (state.sound) { prevDuration = state?.sound?.duration as Double }
	if (settings?.mySonosDelay2 && prevDuration)	{ prevDuration = prevDuration + settings?.mySonosDelay2 }
	//if (settings?.myProfile && !isTrigger()) { sendEvent(eTxt) } //retired mailbox 2/25/18
	if (settings?.enableWebCoRE != false && settings?.webCorePistons && !isTrigger() ) {
		log.warn "executing piston name = ${settings?.webCorePistons}"
		webCoRE_execute(settings?.webCorePistons)
	}
	if (settings?.askAlexa && settings?.askAlexaMQs ) sendToAskAlexa(eTxt)
	if (isCustText() || isCustTextWeather() || isTrigger()) {
		if (settings?.mySpeechDevices || settings?.mySonosDevices) {
			sTxt = textToSpeech(eTxt instanceof List ? eTxt[0] : eTxt, settings?.ttsVoiceStyle.substring(6))
		}
		state.sound = sTxt
	} else {
		loadSoundState()
		state?.lastPlayed = now()
		sTxt = state?.sound
	}
	//Playing Audio Message
	if (settings?.mySpeechDevices) {
		def currVolLevel = settings?.mySpeechDevices.latestValue("level")
		def currMute = settings?.mySpeechDevices.latestValue("mute")
		if (state?.showDebug) { log.debug "vol switch = ${currVolSwitch}, vol level = ${currVolLevel}, currMute = ${currMute} " }
		sVolume = settings.mySpeechVolume ?: 30
		if (!settings?.mySpeechDelay1) {
			unmuteDevices(settings?.mySpeechDevices)
			settings?.mySpeechDevices?.each {d-> if(d?.hasCommand("playTextAndResume")) { d?.playTextAndResume(eTxt, sVolume) } }
			state?.lastPlayed = now()
			if (state?.showDebug) { log.info "Playing Message on Speech Synthesizer(s) '${settings?.mySpeechDevices}' at volume (${sVolume})" }
		} else {
			state.mySpeechSound = eTxt
			state.mySpeechVolume = sVolume
			runIn(settings?.mySpeechDelay1, delayedFirstMessage)
		}
	}
	if (settings?.mySonosDevices) {
		def currVolLevel = settings?.mySonosDevices.latestValue("level") //as Integer
		currVolLevel = currVolLevel[0]
		unmuteDevices(settings?.mySonosDevices)
		sVolume = settings.mySonosVolume ?: 20
		sVolume = (sVolume == 20 && currVolLevel == 0) ? sVolume : (sVolume !=20 ? sVolume : currVolLevel)
		def sCommand = settings?.mySonosResume == true ? "playTrackAndResume" : "playTrackAndRestore"
		if (!state?.lastPlayed) {
			if (!settings?.mySonosDelay1) {
				playSonosIntro(sCommand, sTxt, sVolume)
			} else {
				if (state?.showDebug) { log.info "delaying first message by ${settings?.mySonosDelay1}" }
				runIn(settings?.mySonosDelay1, sonosFirstDelayedMessage)
			}
		} else {
			def elapsed = now() - state?.lastPlayed
			def elapsedSec = elapsed/1000
			def timeCheck = prevDuration * 1000
			if (elapsed < timeCheck) {
				def delayNeeded = prevDuration - elapsedSec
				if (delayNeeded > 0 ) { delayNeeded = delayNeeded + 2 }
				log.error "message is already playing, delaying new message by $delayNeeded seconds (raw delay = $prevDuration, elapsed time = $elapsedSec)"
				runIn(delayNeeded, delayedMessage)
			} else { playSonosIntro(sCommand, sTxt, sVolume) }
		}
		state?.sound?.command = sCommand
		state?.sound?.volume = sVolume
		state?.lastPlayed = now()
	}
	retriggerSchedule(eTxt)
}

private playSonosIntro(cmd, msg, vol) {
	if(cmd && msg && vol) {
		if (settings?.playCustIntroSound) {
			int sDelayFirst = 2
			if (state?.showDebug) { log.info "delaying first message to play intro by $sDelayFirst" }
			playIntroSound()
			runIn(sDelayFirst, sonosFirstDelayedMessage)
		} else {
			if (state?.showDebug) { log.info "playing first message" }
			settings?.mySonosDevices?.each { d-> 
				if(d?.hasCommand("${cmd}")) { d?."${cmd}"(msg?.uri, Math.max((msg?.duration as Integer),2), vol) } 
			}
		}
	}
}

private unmuteDevices(devs) {
	if(!devs) { return }
	devs?.each { dev->
		def currMuteOn = (dev?.latestValue("mute") == "muted")
		if (currMuteOn) {
			if(dev?.hasCommand("unmute")) {
				log.warn "speaker (${dev?.displayName}) is muted, sending unmute command..."
				dev?.unmute()
			}
		}
	}
}

def delayedMessage() {
	def sTxt = state.sound
	settings?.mySonosDevices?."${sTxt.command}"(sTxt.uri, Math.max((sTxt.duration as Integer),2), sTxt.volume)
	if (state?.showDebug) { log.info "delayed message is now playing" }
}

def delayedFirstMessage() {
	def eTxt = state?.mySpeechSound
	def sVolume = state.mySpeechVolume
	settings?.mySpeechDevices?.playTextAndResume(eTxt, sVolume)
	state?.lastPlayed = now()
	if (state?.showDebug) { log.info "Playing first message delayed" }
}

def sonosFirstDelayedMessage() {
	if(state?.sound && state?.sound?.command && state?.sound?.duration && state?.sound?.volume) {
		settings?.mySonosDevices?.each { d->
			if(d?.hasCommand("${state?.sound?.command}")) {
				d?."${state?.sound?.command}"(state?.sound?.uri, Math.max((state?.sound?.duration as Integer),2), state?.sound?.volume)
				if (state?.showDebug) { log.info "Playing first message delayed on (${d?.displayName})" }
			} else { if(state?.showDebug) {log.error "sonosFirstDelayedMessage() Skipping... Device ${d?.displayName} does not support ${state?.sound?.command} command!!!"} }
		}
	} else { if(state?.showDebug) {log.warn "sonosFirstDelayedMessage() Skipping... Sound state not available!!!"} }
}
/***********************************************************************************************************************
	RETRIGGER
***********************************************************************************************************************/

Integer retriggerConvMap(String val) {
	Map m = [
		"runEvery1Minute": 60,
		"runEvery2Minutes": 60,
		"runEvery3Minutes": 60,
		"runEvery5Minutes": 300,
		"runEvery10Minutes": 600,
		"runEvery15Minutes": 900,
		"runEvery30Minutes": 1800,
		"runEvery1Hour": 3600,
		"runEvery3Hours": (3600*3),
		"runEvery6Hours": (3600*6),
		"runEvery12Hours": (3600*12),
		"runEvery1Day": (3600*24)
	]
	return m[val] ?: null
}

void retriggerSchedule(eTxt) { //Called by takeAction()
	if (settings?.retriggerSched != null) {
		Integer seconds = retriggerConvMap(settings?.retriggerSched)
		Integer curCnt = state?.occurrences ?: 1
		Integer stopCnt = (settings?.retriggerCount ?: 3)
		if (state?.showDebug) { log.info "curCnt = $curCnt; stopCnt = $stopCnt" }
		if (curCnt == 1) {
			if (state?.showDebug) { log.warn "saving message" }
			state?.message = eTxt
			state?.originalMessage = eTxt
		}
		if (curCnt < stopCnt && seconds) {
			if (state?.showDebug) { log.warn "scheduling reminders" }
			runIn(seconds, retriggerHandler)
			state?.retriggerSchedActive = true
			//state?.message = eTxt
			parent.updActiveRetrigger(app?.getId(), eTxt)
		} else if(curCnt && (curCnt >= stopCnt)) {
			unscheduleRetrigger()
		}
	}
}

def retriggerHandler() {
	def message = "In case you misssed it: ${state?.message}"
	Integer curCnt = state?.occurrences
	Boolean send = false
	log.debug "retriggerHandler: | retriggerCancelOnChange: ${settings?.retriggerCancelOnChange}"
	if (settings?.retriggerCancelOnChange == false) {
		send = true
	} else {
		if(ok2Proceed()) {
			send = true
		}
	}
	if(send) {
		state?.occurrences = curCnt ? curCnt+1 : 1
		if (state?.showDebug) { log.info "processing retrigger with message = $message" }
		if (settings?.smsNumbers?.toString()?.length()>=10 || settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices)) { sendtxt(message) }
		if (settings?.myNotifyDevice) { settings?.myNotifyDevice?.deviceNotification(message) }
		takeAction(message)
	} else { 
		if(state?.showDebug) { log.info "retriggerHandler | Retrigger Conditions now invalid.  Unscheduling Retrigger..."}
		unscheduleRetrigger()
	}
}

void unscheduleRetrigger() {
	unschedule("retriggerHandler")
	state?.retriggerSchedActive = false
	state?.message = null
	state?.originalMessage = null
	state?.lastEventData = null
	state?.occurrences = 0
	parent?.updActiveRetrigger(app?.getId(), null)
	if (state?.showDebug) { log.warn "canceling reminders" }
}
/***********************************************************************************************************************
	CANCEL RETRIGGER
***********************************************************************************************************************/
String retriveMessage() { 
	if (state?.showDebug) { log.trace "retrieveMessage..." }
	return state?.originalMessage
}

def cancelRetrigger() {
	unscheduleRetrigger()
	if (state?.showDebug) { log.warn "canceling retrigger as requested by other app" }
	return "successful"
}
/***********************************************************************************************************************
	CUSTOM WEATHER VARIABLES
***********************************************************************************************************************/
private getWeatherVar(eTxt) {
	def result
	// weather variables
	def weatherToday = mGetWeatherVar("today")
	def weatherTonight = mGetWeatherVar("tonight")
	def weatherTomorrow = mGetWeatherVar("tomorrow")
	def tHigh = mGetWeatherVar("high")
	def tLow = mGetWeatherVar("low")
	def tUV = mGetWeatherElements("uv")
	def tPrecip = mGetWeatherElements("precip")
	def tHum = mGetWeatherElements("hum")
	def tCond = mGetWeatherElements("cond")
	def tWind = mGetWeatherElements("wind")
	def tSunset = mGetWeatherElements("set")
	def tSunrise = mGetWeatherElements("rise")
	def tTemp = mGetWeatherElements("current")
	//def tWind = mGetWeatherElements("moonphase")

	result = eTxt.replace("&today", "${weatherToday}")?.replace("&tonight", "${weatherTonight}")?.replace("&tomorrow", "${weatherTomorrow}")
	if (result) { result = result.replace("&high", "${tHigh}")?.replace("&low", "${tLow}")?.replace("&wind", "${tWind}")?.replace("&uv", "${tUV}")?.replace("&precipitation", "${tPrecip}") }
	if (result) { result = result.replace("&humidity", "${tHum}")?.replace("&conditions", "${tCond}")?.replace("&set", "${tSunset}")?.replace("&rise", "${tSunrise}")?.replace("&current", "${tTemp}") }

	return result
}
/***********************************************************************************************************************
	WEATHER TRIGGERS
***********************************************************************************************************************/
def mGetWeatherTrigger() {
	def data = [:]
	def myTrigger
	def process = false
	try {
		if (getMetric() == false) {
			def cWeather = getWeatherFeature("conditions", state?.wZipCode)
			def cTempF = cWeather?.current_observation?.temp_f.toDouble()
			int tempF = cTempF as Integer
			def cRelativeHum = cWeather?.current_observation?.relative_humidity
			cRelativeHum = cRelativeHum?.replaceAll("%", "")
			int humid = cRelativeHum as Integer
			def cWindGustM = cWeather?.current_observation?.wind_gust_mph?.toDouble()
			int wind = cWindGustM as Integer
			def cPrecipIn = cWeather?.current_observation?.precip_1hr_in?.toDouble()
			double precip = cPrecipIn //as double
				precip = 1 + precip //precip
			if (state?.showDebug) { log.debug "current triggers: precipitation = $precip, humidity = $humid, wind = $wind, temp = $tempF" }
			myTrigger = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? precip : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? wind : settings?.myWeatherTriggers == "Humidity (%)" ? humid : settings?.myWeatherTriggers == "Temperature (F/C)" ? tempF : null
		} else {
			def cWeather = getWeatherFeature("conditions", state?.wZipCode)
			def cTempC = cWeather?.current_observation?.temp_c?.toDouble()
				int tempC = cTempC as Integer
			def cRelativeHum = cWeather?.current_observation?.relative_humidity
				cRelativeHum = cRelativeHum?.replaceAll("%", "")
				int humid = cRelativeHum as Integer
			def cWindGustK = cWeather?.current_observation?.wind_gust_kph?.toDouble()
				int windC = cWindGustK as Integer
			def cPrecipM = cWeather?.current_observation?.precip_1hr_metric?.toDouble()
				double  precipC = cPrecipM as double

			myTrigger = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? precipC : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? windC : settings?.myWeatherTriggers == "Humidity (%)" ? humid : settings?.myWeatherTriggers == "Temperature (F/C)" ? tempC : null
		}
		def myTriggerName = settings?.myWeatherTriggers == "Chance of Precipitation (in/mm)" ? "Precipitation" : settings?.myWeatherTriggers == "Wind Gust (MPH/kPH)" ? "Wind Gusts" : settings?.myWeatherTriggers == "Humidity (%)" ? "Humidity" : settings?.myWeatherTriggers == "Temperature (F/C)" ? "Temperature" : null
		if (settings?.myWeatherTriggersS == "above" && state.cycleOnA == false) {
			def var = myTrigger > settings?.myWeatherThreshold
			if (state?.showDebug) { log.debug  " myTrigger = $myTrigger, myWeatherThreshold = ${settings?.myWeatherThreshold}, myWeatherTriggersS = ${settings?.myWeatherTriggersS}, var = $var" }
			if (myTrigger > settings?.myWeatherThreshold) {
				process = true
				state.cycleOnA = process
				state.cycleOnB = false
			}
		}
		if (settings?.myWeatherTriggersS == "below" && state.cycleOnB == false) {
			def var = myTrigger <= settings?.myWeatherThreshold
			if (state?.showDebug) { log.debug  " myTrigger = $myTrigger, myWeatherThreshold = ${settings?.myWeatherThreshold} myWeatherTriggersS = ${settings?.myWeatherTriggersS}, var = $var" }
			if (myTrigger <= settings?.myWeatherThreshold) {
				process = true
				state.cycleOnA = false
				state.cycleOnB = process
			}
		}
		if (process == true) {
			//data = [value:"${myTrigger}", name:"${settings?.myWeatherTriggers}", device:"${settings?.myWeatherTriggers}"] 4/5/17 Bobby
			data = [value:"${myTrigger}", name:"weather", device:"${myTriggerName}"]
			alertsHandler(data)
		}
	} catch (Throwable t) {
		log.error t
		return result
	}
}

/***********************************************************************************************************************
	WEATHER ALERTS
***********************************************************************************************************************/
def mGetWeatherAlerts() {
	def result
	def firstTime = false
	if (state.weatherAlert == null) {
   		result = "You are now subscribed to selected weather alerts for your area"
		firstTime = true
		state.weatherAlert = "There are no weather alerts for your area"
		state.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
	} else { result = "There are no weather alerts for your area" }
	
	def data = [:]
	try {
		def weather = getWeatherFeature("alerts", state?.wZipCode)
		def type = weather?.alerts?.type[0]
		def alert = weather?.alerts?.description[0]
		def expire = weather?.alerts?.expires[0]
		def typeOk = settings?.myWeatherAlert?.find {a -> a == type}
		if (typeOk) {
			if (expire != null) { expire = expire?.replaceAll(~/ EST /, " ")?.replaceAll(~/ CST /, " ")?.replaceAll(~/ MST /, " ")?.replaceAll(~/ PST /, " ") }
			if (alert != null) {
				result = alert  + " is in effect for your area, that expires at " + expire
				if (state?.weatherAlert == null) {
					state?.weatherAlert = result
					state?.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
					data = [value: result, name: "weather alert", device:"weather"]
					alertsHandler(data)
				} else {
					if (state?.showDebug) { log.debug "new weather alert = ${alert}, expire = ${expire}" }
					def newAlert = result != state?.weatherAlert ? true : false
					if (newAlert == true) {
						state?.weatherAlert = result
						state?.lastAlert = new Date(now()).format("h:mm aa", location.timeZone)
						data = [value: result, name: "weather alert", device:"weather"]
						alertsHandler(data)
					}
				}
			}
		} else if (firstTime == true) {
			data = [value: result, name: "weather alert", device:"weather"]
			alertsHandler(data)
		}
	} catch (Throwable t) {
		log.error t
		return result
	}
}
/***********************************************************************************************************************
	HOURLY FORECAST
***********************************************************************************************************************/
def mGetCurrentWeather() {
	def weatherData = [:]
	def data = [:]
   	def result
	try {
		//hourly updates
		def cWeather = getWeatherFeature("hourly", state?.wZipCode)
		def cWeatherCondition = cWeather?.hourly_forecast[0]?.condition
		def cWeatherPrecipitation = cWeather?.hourly_forecast[0]?.pop + " percent"
		def cWeatherWind = cWeather?.hourly_forecast[0]?.wspd?.english + " miles per hour"
		def cWeatherWindC = cWeather?.hourly_forecast[0]?.wspd?.metric + " kilometers per hour"
		if (getMetric() == true) { cWeatherWind = cWeatherWindC }
		def cWeatherHum = cWeather?.hourly_forecast[0]?.humidity + " percent"
		def cWeatherUpdate = cWeather?.hourly_forecast[0]?.FCTTIME?.civil
		//past hour's data
		def pastWeather = state.lastWeather
		//current forecast
			weatherData.wCond = cWeatherCondition
			weatherData.wWind = cWeatherWind
			weatherData.wHum = cWeatherHum
			weatherData.wPrecip = cWeatherPrecipitation
		//last weather update
		def lastUpdated = new Date(now()).format("h:mm aa", location.timeZone)
		if (settings?.myWeather) {
			if (pastWeather == null) {
				state.lastWeather = weatherData
				state.lastWeatherCheck = lastUpdated
				result = "hourly weather forcast notification has been activated at " + lastUpdated + " You will now receive hourly weather updates, only if the forecast data changes"
				data = [value: result, name: "weather alert", device: "weather"]
				alertsHandler(data)
			} else {
				def wUpdate = pastWeather.wCond != cWeatherCondition ? "current weather condition" : pastWeather.wWind != cWeatherWind ? "wind intensity" : pastWeather.wHum != cWeatherHum ? "humidity" : pastWeather.wPrecip != cWeatherPrecipitation ? "chance of precipitation" : null
				def wChange = wUpdate == "current weather condition" ? cWeatherCondition : wUpdate == "wind intensity" ? cWeatherWind  : wUpdate == "humidity" ? cWeatherHum : wUpdate == "chance of precipitation" ? cWeatherPrecipitation : null
				//something has changed
				if (wUpdate != null) {
					// saving update to state
					state.lastWeather = weatherData
					state.lastWeatherCheck = lastUpdated
					if (settings?.myWeather == "Any Weather Updates") {
						def condChanged = pastWeather.wCond != cWeatherCondition
						def windChanged = pastWeather.wWind != cWeatherWind
						def humChanged = pastWeather.wHum != cWeatherHum
						def precChanged = pastWeather.wPrecip != cWeatherPrecipitation
						if (condChanged) {
							result = "The hourly weather forecast has been updated. The weather condition has been changed to "  + cWeatherCondition
						}
						if (windChanged) {
							if (result) { result = result +  ", the wind intensity to "  + cWeatherWind }
							else { result = "The hourly weather forecast has been updated. The wind intensity has been changed to "  + cWeatherWind }
						}
						if (humChanged) {
							if (result) {result = result +  ", the humidity to "  + cWeatherHum }
							else { result = "The hourly weather forecast has been updated. The humidity has been changed to "  + cWeatherHum }
						}
						if (precChanged) {
							if (result) {result = result + ", the chance of rain to "  + cWeatherPrecipitation }
							else { result = "The hourly weather forecast has been updated. The chance of rain has been changed to "  + cWeatherPrecipitation }
						}
						data = [value: result, name: "weather alert", device: "weather"]
						alertsHandler(data)
					}
					else {
						if (settings?.myWeather == "Weather Condition Changes" && wUpdate ==  "current weather condition") {
							result = "The " + wUpdate + " has been updated to " + wChange
							data = [value: result, name: "weather alert", device: "weather"]
							alertsHandler(data)
						}
						else if (settings?.myWeather == "Chance of Precipitation Changes" && wUpdate ==  "chance of precipitation") {
							result = "The " + wUpdate + " has been updated to " + wChange
							data = [value: result, name: "weather alert", device: "weather"]
							alertsHandler(data)
						}
						else if (settings?.myWeather == "Wind Speed Changes" && wUpdate == "wind intensity") {
							result = "The " + wUpdate + " has been updated to " + wChange
							data = [value: result, name: "weather alert", device: "weather"]
							alertsHandler(data)
						}
						else if (settings?.myWeather == "Humidity Changes" && wUpdate == "humidity") {
							result = "The " + wUpdate + " has been updated to " + wChange
							data = [value: result, name: "weather alert", device: "weather"]
							alertsHandler(data)
						}
					}
				}
			}
		}
	} catch (Throwable t) {
		log.error t
		return result
	}
}


/***********************************************************************************************************************
	WEATHER ELEMENTS
***********************************************************************************************************************/
def mGetWeatherElements(element) {
	state.pTryAgain = false
	def result ="Current weather is not available at the moment, please try again later"
   	try {
		//hourly updates
		def cWeather = getWeatherFeature("hourly", state?.wZipCode)
		def cWeatherCondition = cWeather?.hourly_forecast[0]?.condition
		def cWeatherPrecipitation = cWeather?.hourly_forecast[0]?.pop + " percent"
		def cWeatherWind = cWeather?.hourly_forecast[0]?.wspd?.english + " miles per hour"
		def cWeatherHum = cWeather?.hourly_forecast[0]?.humidity + " percent"
		def cWeatherUpdate = cWeather?.hourly_forecast[0]?.FCTTIME?.civil //forecast last updated time E.G "11:00 AM",
		//current conditions
		def condWeather = getWeatherFeature("conditions", state?.wZipCode)
		def condTodayUV = condWeather?.current_observation?.UV
  		def currentT = condWeather?.current_observation?.temp_f
			int currentNow = currentT
		//forecast
		def forecastT = getWeatherFeature("forecast", state?.wZipCode)
		def fToday = forecastT?.forecast?.simpleforecast?.forecastday[0]
		def high = fToday?.high?.fahrenheit?.toInteger()
	   		int highNow = high
		def low = fToday?.low?.fahrenheit?.toInteger()
			int lowNow = low
		//sunset, sunrise, moon, tide
		def s = getWeatherFeature("astronomy", state?.wZipCode)
		def sunriseHour = s?.moon_phase?.sunrise?.hour
		def sunriseTime = s?.moon_phase?.sunrise?.minute
		def sunrise = sunriseHour + ":" + sunriseTime
			Date date = Date.parse("HH:mm",sunrise)
			def sunriseNow = date.format( "h:mm aa" )
		def sunsetHour = s?.moon_phase?.sunset?.hour
		def sunsetTime = s?.moon_phase?.sunset?.minute
		def sunset = sunsetHour + ":" + sunsetTime
			date = Date.parse("HH:mm", sunset)
			def sunsetNow = date.format( "h:mm aa" )
			if (getMetric() == true) {
				def cWeatherWindC = cWeather?.hourly_forecast[0]?.wspd?.metric + " kilometers per hour"
					cWeatherWind = cWeatherWindC
				def currentTc = condWeather?.current_observation?.temp_c
					currentNow = currentTc
				def highC = fToday?.high?.celsius
					highNow = currentTc
				def lowC = fToday?.low?.celsius
					lowNow = currentTc
			}
		if (state?.showDebug) { log.debug "cWeatherUpdate = ${cWeatherUpdate}, cWeatherCondition = ${cWeatherCondition}, cWeatherPrecipitation = ${cWeatherPrecipitation}, cWeatherWind = ${cWeatherWind},  cWeatherHum = ${cWeatherHum}, cWeatherHum = ${condTodayUV}" }
			if		(element == "precip" ) { result = cWeatherPrecipitation }
			else if	(element == "wind") { result = cWeatherWind }
			else if	(element == "uv") { result = condTodayUV }
			else if	(element == "hum") { result = cWeatherHum }
			else if	(element == "cond") { result = cWeatherCondition }
			else if	(element == "current") { result = currentNow }
			else if	(element == "rise") { result = sunriseNow }
			else if	(element == "set") { result = sunsetNow }
 			else if	(element == "high") { result = highNow }
			else if	(element == "low") { result = lowNow }

			return result
	} catch (Throwable t) {
		log.error t
		state.pTryAgain = true
		return result
	}
}
/***********************************************************************************************************************
	WEATHER TEMPS
***********************************************************************************************************************/
def private mGetWeatherVar(var) {
	state.pTryAgain = false
	def result
	try {
		def weather = getWeatherFeature("forecast", state?.wZipCode)
		def sTodayWeather = weather?.forecast?.simpleforecast?.forecastday[0]
		if (var =="high") { result = sTodayWeather?.high?.fahrenheit }
		if (var == "low") { result = sTodayWeather?.low?.fahrenheit }
		if (var =="today") { result = 	weather?.forecast?.txt_forecast?.forecastday[0]?.fcttext }
		if (var =="tonight") { result = weather?.forecast?.txt_forecast?.forecastday[1]?.fcttext }
		if (var =="tomorrow") { result = weather?.forecast?.txt_forecast?.forecastday[2]?.fcttext }

		if (getMetric() == true) {
			if (var =="high") { result = weather?.forecast?.simpleforecast?.forecastday[0]?.high?.celsius }
			if (var == "low") { result = weather?.forecast?.simpleforecast?.forecastday[0]?.low?.celsius }
			if (var =="today") { result = 	weather?.forecast?.txt_forecast?.forecastday[0]?.fcttext_metric }
			if (var =="tonight") { result = weather?.forecast?.txt_forecast?.forecastday[1]?.fcttext_metric }
			if (var =="tomorrow") { result = weather?.forecast?.txt_forecast?.forecastday[2]?.fcttext_metric }
			result = result?.toString()
			result = result?.replaceAll(/([0-9]+)C/,'$1 degrees')
		}
		result = result?.toString()
		result = result?.replaceAll(/([0-9]+)F/,'$1 degrees')?.replaceAll(~/mph/, " miles per hour")
		// clean up wind direction (South)
		result = result?.replaceAll(~/ SSW /, " South-southwest ")?.replaceAll(~/ SSE /, " South-southeast ")?.replaceAll(~/ SE /, " Southeast ")?.replaceAll(~/ SW /, " Southwest ")
		// clean up wind direction (North)
		result = result?.replaceAll(~/ NNW /, " North-northwest ")?.replaceAll(~/ NNE /, " North-northeast ")?.replaceAll(~/ NE /, " Northeast ")?.replaceAll(~/ NW /, " Northwest ")
		// clean up wind direction (West)
		result = result?.replaceAll(~/ WNW /, " West-northwest ")?.replaceAll(~/ WSW /, " West-southwest ")
		// clean up wind direction (East)
		result = result?.replaceAll(~/ ENE /, " East-northeast ")?.replaceAll(~/ ESE /, " East-southeast ")

		return result
	} catch (Throwable t) {
		log.error t
		state.pTryAgain = true
		return result
	}
}

/***********************************************************************************************************************
	CRON HANDLER
***********************************************************************************************************************/
def cronHandler(var) {
	if (state?.showDebug) { log.debug " con var is $var" }
	String cron = null
	switch(var) {
		case "Minutes":
			// 0 0/3 * 1/1 * ? *
			if (settings?.xMinutes) {
				cron = "0 0/${settings?.xMinutes} * 1/1 * ? *"
			} else { log.error " unable to schedule your reminder due to missing required variables" }
			break
		case "Hourly":
			//	0 0 0/6 1/1 * ? *
			if (settings?.xHours) {
				cron = "0 0 0/${settings?.xHours} 1/1 * ? *"
			} else { log.error " unable to schedule your reminder due to missing required variables" }
			break
		case "Daily":
			// 0 0 1 1/7 * ? *
			def hrmn = hhmm(settings?.xDaysStarting, "HH:mm")
			def hr = hrmn[0..1]
			def mn = hrmn[3..4]
			if (settings?.xDays && settings?.xDaysStarting) {
				cron = "0 $mn $hr 1/${settings?.xDays} * ? *"
			} else if (settings?.xDaysWeekDay && settings?.xDaysStarting) {
				//0 13 2 ? * MON-FRI *
				cron = "0 $mn $hr ? * MON-FRI *"
			} else { log.error " unable to schedule your reminder due to missing required variables" }
			break
		case "Weekly":
			// 	0 0 2 ? * TUE,SUN *
			def hrmn = hhmm(settings?.xWeeksStarting, "HH:mm")
			def hr = hrmn[0..1]
			def mn = hrmn[3..4]
			def weekDays = settings?.xWeeks?.collect { it as String }?.join(",")
			if (settings?.xWeeks && settings?.xWeeksStarting) {
				cron = "0 $mn $hr ? * ${weekDays} *"
			} else { log.error " unable to schedule your reminder due to missing required variables" }
			break
		case "Monthly":
			// 0 30 5 6 1/2 ? *
			def hrmn = hhmm(settings?.xMonthsStarting, "HH:mm")
			def hr = hrmn[0..1]
			def mn = hrmn[3..4]
			if (settings?.xMonths && settings?.xMonthsDay) {
				cron = "0 $mn $hr ${settings?.xMonthsDay} 1/${settings?.xMonths} ? *"
			} else { log.error "unable to schedule your reminder due to missing required variables" }
			break
		case "Yearly":
			//0 0 4 1 4 ? *
			def hrmn = hhmm(settings?.xYearsStarting, "HH:mm")
			def hr = hrmn[0..1]
			def mn = hrmn[3..4]
			if (settings?.xYears) {
				cron = "0 $mn $hr ${settings?.xYearsDay} ${settings?.xYears} ? *"
			} else { log.error "unable to schedule your reminder due to missing required variables" }
			break
	}
	if(cron) { schedule(cron, "scheduledTimeHandler") }
	log.info "scheduled $var recurring event" //time period with expression: $result"
}
/***********************************************************************************************************************
	ONE TIME SCHEDULING HANDLER
***********************************************************************************************************************/
def oneTimeHandler(var) {
	def result
	def todayYear = new Date(now()).format("yyyy")
	def todayMonth = new Date(now()).format("MM")
	def todayDay = new Date(now()).format("dd")
	def yyyy = settings?.xFutureYear ?: todayYear
	def MM = settings?.xFutureMonth ?: todayMonth
	def dd = settings?.xFutureDay ?: todayDay

	if (!settings?.xFutureDay) {
	 	runOnce(settings?.xFutureTime, scheduledTimeHandler)
		//if (pretrigger) { runOnce(settings?.xFutureTime-15, scheduledpretrigger) }
		//options:["15":"15 minutes","30":"30 minutes","60":"One hour", "540":"One day"]
	} else{
		def timeSchedule = hhmmssZ(settings?.xFutureTime)
		result = "${yyyy}-${MM}-${dd}T${timeSchedule}"
		//result = "${settings?.xFutureYear}-${MM}-${dd}T${timeSchedule}"
	   	Date date = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", result)
		runOnce(date, scheduledTimeHandler)
		//if (pretrigger) { runOnce(settings?.xFutureTime-30, scheduledpretrigger) }
	}
}
private hhmmssZ(time, fmt = "HH:mm:ss.SSSZ") {
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}
/***********************************************************************************************************************
	SUN STATE HANDLER
***********************************************************************************************************************/
def sunsetTimeHandler(evt) {
	def sunsetString = (String) evt?.value
	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: settings?.startSunriseOffset, sunsetOffset: settings?.startSunsetOffset)
	def sunsetTime = s?.sunset?.time // Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
	if (settings?.sunOffset) {
		def offsetSunset = new Date(sunsetTime - (-settings?.sunOffset * 60 * 1000))
		log.debug "Scheduling for: $offsetSunset (sunset is $sunsetTime)"
		runOnce(offsetSunset, "scheduledTimeHandler")
	} else { scheduledTimeHandler("sunset") }
}
def sunriseTimeHandler(evt) {
	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: settings?.startSunriseOffset, sunsetOffset: settings?.startSunsetOffset)
	def sunriseTime = s.sunrise.time
	if (settings?.sunOffset) {
		def offsetSunrise = new Date(sunriseTime -(-settings?.sunOffset * 60 * 1000))
		log.debug "Scheduling for: $offsetSunrise (sunrise is $sunriseTime)"
		runOnce(offsetSunrise, "scheduledTimeHandler")
	} else { scheduledTimeHandler("sunrise") }
}

def scheduledTimeHandler(state) {
	alertsHandler([value: "executed", name:"timer", device:"schedule"])
}
/***********************************************************************************************************************
	RESTRICTIONS HANDLER
***********************************************************************************************************************/
Boolean ok2Proceed() {
	log.trace "ok2Proceed | getDayOk(): ${getDayOk()} | getModeOk(): ${getModeOk()} | getTimeOk(): ${getTimeOk()} | getFrequencyOk(): ${getFrequencyOk()} | getConditionsOk(): ${getConditionsOk()} | getDevicesOk(): ${getDevicesOk(state?.lastEventData)}"
	return (getDayOk() && getModeOk() && getTimeOk() && getFrequencyOk() && getConditionsOk() && getDevicesOk(state?.lastEventData))
}


Boolean getFrequencyOk() {
	def lastPlayed = state?.lastPlayed
	Boolean result = false
	if (onceDailyOk(lastPlayed)) {
		if (settings?.everyXmin) {
			if (state?.lastPlayed == null) {
				result = true
			} else {
				if (now() - state?.lastPlayed >= settings?.everyXmin * 60000) {
					result = true
				} else {
					log.debug "Not taking action because ${settings?.everyXmin} minutes have not passed since last notification"
				}
			}
		} else {
			result = true
		}
	} else {
		log.debug "Not taking action because the notification was already played once today"
	}
	log.debug "frequencyOk = $result"
	return result
}

Boolean onceDailyOk(Long lastPlayed) {
	Boolean result = true
	if (settings?.onceDaily) {
		def today = new Date(now()).format("EEEE, MMMM d, yyyy", location.timeZone)
		def lastTime = new Date(lastPlayed).format("EEEE, MMMM d, yyyy", location.timeZone)
		result = (lastPlayed ? (today != lastTime) : true)
		log.trace "oncePerDayOk = $result"
	}
	return result
}

Boolean getMetric() { return (location.temperatureScale == "C") }

Boolean getModeOk() {
	Boolean result = (!modes || modes?.contains(location?.mode))
	log.debug "modeOk = $result"
	return result
}

Boolean getDayOk() {
	Boolean result = true
	if (settings?.days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		} else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def today = df.format(new Date())
		result = (settings?.days?.contains(today))
	}
	log.debug "daysOk = $result"
	return result
}

Boolean getTimeOk() {
	Boolean result = true
	if ((settings?.starting && settings?.ending) || (settings?.starting && settings?.endingX in ["Sunrise", "Sunset"]) || (settings?.startingX in ["Sunrise", "Sunset"] && settings?.ending) || (settings?.startingX in ["Sunrise", "Sunset"] && settings?.endingX in ["Sunrise", "Sunset"])) {
		def currTime = now()
		def start = null
		def stop = null
		def s = getSunriseAndSunset(zipCode: settings?.zipCode, sunriseOffset: settings?.startSunriseOffset, sunsetOffset: settings?.startSunsetOffset)
		if (settings?.startingX == "Sunrise") { start = s.sunrise.time }
		else if (settings?.startingX == "Sunset") { start = s?.sunset?.time }
		else if (settings?.starting) { start = timeToday(settings?.starting, location.timeZone)?.time }
		s = getSunriseAndSunset(zipCode: settings?.zipCode, sunriseOffset: settings?.endSunriseOffset, sunsetOffset: settings?.endSunsetOffset)
		if (settings?.endingX == "Sunrise") { stop = s.sunrise.time }
		else if (settings?.endingX == "Sunset") { stop = s.sunset.time }
		else if (settings?.ending) { stop = timeToday(settings?.ending, location.timeZone).time }
		result = (start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start)
		if (state?.showDebug) { log.trace "getTimeOk = $result." }
	}
	log.debug "timeOk = $result"
	return result
}

private hhmm(time, fmt = "h:mm a") {
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	return f.format(t)
}

private offset(value) {
	log.warn "offset is $offset"
	return value ? ((value > 0 ? "+" : "") + value + " min") : ""
}

private timeIntervalLabel() {
	def result = ""
	if      (settings?.startingX == "Sunrise" &&settings?. endingX == "Sunrise") { result = "Sunrise" + offset(settings?.startSunriseOffset) + " to Sunrise" + offset(settings?.endSunriseOffset) }
	else if (settings?.startingX == "Sunrise" && settings?.endingX == "Sunset") { result = "Sunrise" + offset(settings?.startSunriseOffset) + " to Sunset" + offset(settings?.endSunsetOffset) }
	else if (settings?.startingX == "Sunset" && settings?.endingX == "Sunrise") { result = "Sunset" + offset(settings?.startSunsetOffset) + " to Sunrise" + offset(settings?.endSunriseOffset) }
	else if (settings?.startingX == "Sunset" && settings?.endingX == "Sunset") { result = "Sunset" + offset(settings?.startSunsetOffset) + " to Sunset" + offset(settings?.endSunsetOffset) }
	else if (settings?.startingX == "Sunrise" && settings?.ending) { result = "Sunrise" + offset(settings?.startSunriseOffset) + " to " + hhmm(settings?.ending, "h:mm a z") }
	else if (settings?.startingX == "Sunset" && settings?.ending) { result = "Sunset" + offset(settings?.startSunsetOffset) + " to " + hhmm(settings?.ending, "h:mm a z") }
	else if (settings?.starting && settings?.endingX == "Sunrise") { result = hhmm(settings?.starting) + " to Sunrise" + offset(settings?.endSunriseOffset) }
	else if (settings?.starting && settings?.endingX == "Sunset") { result = hhmm(settings?.starting) + " to Sunset" + offset(settings?.endSunsetOffset) }
	else if (settings?.starting && settings?.ending) { result = hhmm(settings?.starting) + " to " + hhmm(settings?.ending, "h:mm a z") }
	return result
}

void settingUpdate(name, value, type=null) {
	log.trace "settingUpdate($name, $value, $type)..."
	if(name && type) {
		app?.updateSetting("$name", [type: "$type", value: value])
	}
	else if (name && type == null){ app?.updateSetting(name.toString(), value) }
}

void settingRemove(name) {
	log.trace "settingRemove($name)..."
	if(name) { app?.deleteSetting("$name") }
}

/***********************************************************************************************************************
	SMS HANDLER
***********************************************************************************************************************/
private void sendtxt(message) {
	def stamp = state.lastTime = new Date(now()).format("h:mm aa", location.timeZone)
	if (state?.showDebug) { log.debug "Request to send sms received with message: '${message}'" }
	if (settings?.usePush) {
		message = settings?.pushTimeStamp == true ? "${message} at ${stamp}" : message
		sendPush(message)
		if (state?.showDebug) { log.debug "Sending push message" }
	}
	if(settings?.pushoverEnabled && settings?.pushoverDevices) {
		Map msgObj = [:]
		msgObj = [title: "${app?.getLabel()}", message: message, priority: (settings?.pushoverPriority?:0)]
		if(settings?.pushoverSound) { msgObj?.sound = settings?.pushoverSound }
		parent?.buildPushMessage(settings?.pushoverDevices, msgObj, true)
		if (state?.showDebug) { log.debug "Sending pushover message to selected ${settings?.pushoverDevices}" }
	}
	if (settings?.smsNumbers) { sendText(settings?.smsNumbers, message) }
	// if (settings?.notify) { //Note: Not sure where this was defined in V1?
	// 	sendNotificationEvent(message)
	// 	if (state?.showDebug) { log.debug "Sending notification to mobile app" }
	// }
}

private void sendText(number, message) {
	if (settings?.smsNumbers) {
		def phones = settings?.smsNumbers.split("\\,")
		for (phone in phones) {
			sendSms(phone?.trim(), message)
			if (state?.showDebug) { log.debug "Sending sms to selected phones ${phones}" }
		}
	}
}
/***********************************************************************************************************************
	CUSTOM SOUNDS HANDLER
***********************************************************************************************************************/
private loadSoundState() {
	switch (settings?.custSound) {
		case "Bell 2":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"]
			break
		case "Dogs Barking":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"]
			break
		case "Fire Alarm":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"]
			break
		case "The mail has arrived":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/the+mail+has+arrived.mp3", duration: "1"]
			break
		case "A door opened":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/a+door+opened.mp3", duration: "1"]
			break
		case "There is motion":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/there+is+motion.mp3", duration: "1"]
			break
		case "Smartthings detected a flood":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+a+flood.mp3", duration: "2"]
			break
		case "Smartthings detected smoke":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+smoke.mp3", duration: "1"]
			break
		case "Someone is arriving":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/someone+is+arriving.mp3", duration: "1"]
			break
		case "Piano":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"]
			break
		case "Lightsaber":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"]
			break
		case "Alexa: Beep Beep":
			state.sound = [uri: "https://images-na.ssl-images-amazon.com/images/G/01/mobile-apps/dex/ask-customskills/audio/speechcons/beep_beep._TTH_.mp3", duration: "10"]
			break
		case "Alexa: Bada Bing Bada Boom":
			state.sound = [uri: "https://images-na.ssl-images-amazon.com/images/G/01/mobile-apps/dex/ask-customskills/audio/speechcons/bada_bing_bada_boom._TTH_.mp3", duration: "10"]
			break
		case "Alexa: Boing":
			state.sound = [uri: "https://images-na.ssl-images-amazon.com/images/G/01/mobile-apps/dex/ask-customskills/audio/speechcons/boing._TTH_.mp3", duration: "10"]
			break
		case "Alexa: Open Sesame":
			state.sound = [uri: "https://images-na.ssl-images-amazon.com/images/G/01/mobile-apps/dex/ask-customskills/audio/speechcons/open_sesame._TTH_.mp3", duration: "10"]
			break
		case "Soft Chime":
			state.sound = [uri: "http://soundbible.com/mp3/Electronic_Chime-KevanGC-495939803.mp3", duration: "10"]
			break
		case "Water Droplet":
			state.sound = [uri: "http://soundbible.com/mp3/Single Water Droplet-SoundBible.com-425249738.mp3", duration: "5"]
			break
 		case "Text Message Alert":
			state.sound = [uri: "http://soundbible.com/mp3/sms-alert-5-daniel_simon.mp3", duration: "5"]
			break	           
        case "Custom URI":
			def fDuration = settings?.custSoundDuration ?: "10"
			state.sound = [uri: "${settings?.custSoundUrl}", duration: "${fDuration}"]
			break
		default:
			state?.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break
	}
}

private playIntroSound() {
	log.info "loading intro ${settings?.custIntroSound}"
	def lastPlay = state?.lastPlayed ?: now()
	def elapsed = now() - lastPlay
	log.warn "last play elapsed = $elapsed"
	def sVolume = settings?.mySonosVolume ?: 20
	switch (settings?.custIntroSound) {
		case "Water Droplet":
			state.soundIntro = [uri: "https://cdn.rawgit.com/BamaRayne/EchoSistantApps/master/Content/Audio/Single_Water_Droplet.mp3", duration: "5", volume: sVolume]
			break
		case "Text Message Alert":
			state.soundIntro = [uri: "https://cdn.rawgit.com/BamaRayne/EchoSistantApps/master/Content/Audio/sms_alert_5.mp3", duration: "5", volume: sVolume]
			break
        case "Custom URI":
			def fDuration = settings?.custIntroSoundDuration ?: "10"
			state.soundIntro = [uri: "${settings?.custIntroSoundUrl}", duration: "${settings?.fDuration}", volume: sVolume]
			break
		default:
			state.soundIntro = [uri: "https://cdn.rawgit.com/BamaRayne/EchoSistantApps/master/Content/Audio/Electronic_Chime.mp3", duration: "4", volume: sVolume]
			break
	}
	settings?.mySonosDevices?.each { d->
		log.info "playing intro on ${d?.displayName}"
		d?.playTrackAndRestore(state?.soundIntro?.uri, state?.soundIntro?.duration, state?.soundIntro?.volume)
	}
}

/******************************************************************************************************
   SEND TO ASK ALEXA
******************************************************************************************************/
void sendToAskAlexa(message) {
	def profile = app.label
	def expire = (settings?.askAlexaMsgExpiration && settings?.askAlexaMsgExpiration?.isNumber()) ? settings?.askAlexaMsgExpiration*60 : 0
	def data = [:]
	log.debug "sending to Ask Alexa: $message"
	sendLocationEvent(
		name: "AskAlexaMsgQueue",
		value: profile,
		isStateChange: true,
		descriptionText: message,
		data:[
			queues: settings?.askAlexaMQs,
			//overwrite: false,
			expires: expire,
			//notifyOnly: false,
			//suppressTimeDate: false,
			//trackDelete: false
		]
	)
	log.warn "Ask Alexa event details: name = AskAlexaMsgQueue, value = $profile, isStateChange = true, descriptionText = $message, queues: ${settings?.askAlexaMQs}"
}

def askAlexaImgUrl() { return "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/smartapps/michaelstruck/ask-alexa.src/AskAlexa512.png" }
/************************************************************************************************************
CoRE Integration
************************************************************************************************************/
public webCore_icon(){return "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE.png"}
/************************************************************************************************************
   Page status and descriptions
************************************************************************************************************/
def pushSendSettings() {
	return (settings?.smsNumbers || settings?.usePush || (settings?.pushoverEnabled && settings?.pushoverDevices) || (settings?.askAlexa && settings?.askAlexaMQs)) ? "complete" : ""
}

def triggersDesc() {
	def str = ""
	str += triggersConfigured() == "Configured" ? "Triggers:" : ""
	str += scheduleTriggers() ? "${str != "" ? "\n" : ""} • Scheduled" : ""
	str += locationTriggers() ? "${str != "" ? "\n" : ""} • Mode/Routine/SHM" : ""
	str += deviceTriggers() ? "${str != "" ? "\n" : ""} • Controllable Devices" : ""
	str += sensorTriggers() ? "${str != "" ? "\n" : ""} • Sensor Devices" : ""
	str += weatherTriggers() ? "${str != "" ? "\n" : ""} • Weather" : ""
	return str == "" ? null : str
}

def pushSendDesc() {
	def str = ""
	str += pushSendSettings() == "complete" ? "Configured:" : ""
	str += settings?.smsNumbers ? "${str != "" ? "\n" : ""} • SMS Text (${settings?.smsNumbers?.toString()?.split(",")?.size()} Devices)" : ""
	str += settings?.usePush ? "${str != "" ? "\n" : ""} • Push Message" : ""
	str += (settings?.pushoverEnabled && settings?.pushoverDevices) ? "${str != "" ? "\n" : ""} • Pushover (${settings?.pushoverDevices?.size()} Devices)" : ""
	str += (settings?.askAlexa && settings?.askAlexaMQs) ? "${str != "" ? "\n" : ""} • Ask Alexa (${settings?.askAlexaMQs?.size()} Queues)" : ""
	return str == "" ? "Tap to Configure" : str
}

def getStateVal(var) {
	return state[var] ?: null
}

def restrictPageSettings() {
	return (settings?.modes || settings?.days || pTimeSettings() || settings?.onceDaily || settings?.everyXmin || settings?.rSwitch || settings?.rContact || settings?.rMotion || settings?.rPresence) ? "complete" : ""
}

def restrictPageDesc() {
	return (restrictPageSettings() == "complete") ? "Configured" : "Tap to Configure"
}

def pTimeSettings() {
	return (settings?.startingX || settings?.endingX) ? "complete" : ""
}

def pTimeComplete() {
	return (settings?.startingX || settings?.endingX) ? "Configured" : "Tap to Configure"
}

public static Map monthMap() {
	return ["1": "January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"]
}

def getDeviceVarAvg(List items, String attrVal, dbl=false) {
	List itemList = []
	def avgVal = 0
	if(items && attrVal) {
		if(items?.size()) {
			itemList = items*."current${attrVal?.capitalize()}"
			if(itemList && itemList?.size() > 1) {
				avgVal = (itemList?.sum().toDouble() / itemList?.size().toDouble()).round(1)
				avgVal = dbl ? avgVal : avgVal?.toDouble()?.round(0)
			}
		}
	}
	return dbl==true ? avgVal?.toDouble() : avgVal?.toInteger()
}

def getDevsByAttrState(devs, String attrType, String stateVal) {
	List list = []
	if(!(devs && attrType && stateVal)) { return list }
	if(devs instanceof List) {
		list = devs?.findAll { it?.currentState("${attrType}")?.value == "${stateVal}" }
	} else { if(devs?.currentState("${attrType}")?.value == "${stateVal}") { list?.push(devs)	} }
	return list
}
