/* 
* EchoSistant Rooms Logic Blocks
*
*
*	12/09/2018		Version:2.0 R.0.5.1a	Bug fix in Humidity Outdoors variable for reports
*	12/07/2018		Version:2.0 R.0.5.1		Added new variables: &shm, &mode, &tempIn, &tempOut, &humIn, &humOut, &fans
*	12/05/2018		Version:2.0 R.0.5.0		Name change and redeployed as grandchild app to EchoSistant
*	09/13/2018		Version:2.0 R.0.4.3		Added Echo Devices as output devices
*	06/27/2017		Version:2.0 R.0.4.2		Bug fix in custom message variables
*	06/20/2018		Version:2.0 R.0.4.1		Added More Simple on/off/toggle switches to actions
*	06/10/2018		Version:2.0 R.0.4.0		Bug fix in delayed switches 
* 	06/08/2018		Version:2.0 R.0.3.9		Added delays to dimmers per mode
*	06/07/2018		Version:2.0 R.0.3.8		Added delays to dimmer actions and for color bulb actions
*	06/04/2018		Version:2.0 R.0.3.7		Bug fix in resetting variables for One-time actions selection
*	06/01/2018		Version:2.0 R.0.3.6		Added in "Custom Commands" section to Actions
*	06/01/2018		Version:2.0 R.0.3.5		Rework of the Routine actions with pending cancel and they routine custom messages
*	05/30/2018		Version:2.0 R.0.3.4		Included device selections for variables. Added ability to have Blocks executed by RemindR and the 
											ability for RemindR to execute Logic Blocks.
*	05/28/2018		Version:2.0 R.0.3.3		Bug Fix in garage door condition. Added variables to ST Routines custom message. Added pending cancel to 
											Executing other Logic Blocks. Made inprovements to Debug Logging information flow.
*	05/27/2018		Version:2.0 R.0.3.2		Bug fix in pending cancel switches.
*	05/27/2018		Version:2.0 R.0.3.1		Added custom messages to be sent when the block executes st routines.
*	05/27/2018		Version:2.0 R.0.3.0		Added delay w/pending cancellation to executing other logic blocks
*	05/27/2018		Version:2.0 R.0.2.9		Bug fix in ST Routine Events executing blocks.
*	05/26/2018		Version:2.0 R.0.2.8		Major bug fixes in delayed routines and a bunch of other stuff.
											Updated Temperature event to be 'Between', 'Below', & 'Above'. NOTE*** Between includes the low & high temps
*	05/24/2018		Version:2.0 R.0.2.7		UI Changes
*	05/23/2018		Version:2.0 R.0.2.6		Major changes to the logic and bug fixes for pending cancel
*	05/22/2018		Version:2.0 R.0.2.5		Added custom msg to Rain Event. Fixed bug in lights off delay.
*	05/21/2018		Version:2.0 R.0.2.4		Added &device, &action, &stRoutAction, &stRoutEvent variables to messaging
*	05/21/2018		Version:2.0 R.0.2.3		Bug fixes for Pending Cancel, ST routines restrictions, contact & switches events and other logic flow fixes.
*	05/18/2018		Version:2.0 R.0.2.2		Updated Rain, CO2, and Wind events to work properly
*	05/14/2018		Version:2.0 R.0.2.1(a)	Do not update. Testing Lux Features
*	05/13/2018		Version:2.0 R.0.2.0		Changed Temp Event to be "Between" two temps.
*	05/12/2018		Version:2.0 R.0.1.9		Found and fixed more bugs. Added "Run Once Daily" to Temp & Hum Events. Variables reset at midnight.
*	05/12/2018		Version:2.0 R.0.1.8		Found and fixed bug with messaging and variables
*	05/12/2018		Version:2.0 R.0.1.7		Added multiple variables for custom sms/push/audio messages
*	05/11/2018		Version:2.0 R.0.1.6		Added Temperature as an event. Added Humidity and Temperature as conditions.
*	05/08/2018		Version:2.0 R.0.1.5		Added Humidity, Water, and Smoke Detectors as events.
*	05/07/2018		Version:2.0 R.0.1.4		Added Keypads as an event. If SHM is set as an action, then the keypads will reflect proper status
*	05/06/2018		Version:2.0 R.0.1.3		Fixed dimmers not showing in UI. Fixed Lights turning on/off with contact. Fixed color lights not showing in UI.
											Continued work on Pending Cancel Events and Actions. Fixed delay on and delay off not working.
											Fixed multiple other bugs
*	05/05/2018		Version:2.0 R.0.1.2		Working copy.... DO NOT UPDATE
*	05/02/2018		Version:2.0 R.0.1.1		Added running ST Routines to delay pending cancel
*	05/01/2018		Version:2.0 R.0.1.0		Bug Fix in Pending Cancel and In Process Actions
*	04/30/2018		Version:2.0 R.0.0.9		Bug Fix in Pending Cancel actions
*	04/30/2018		Version:2.0 R.0.0.8		Bug Fix in ST routines trigging blocks when ANY routine ran.
*	04/30/2018		Version:2.0 R.0.0.7		Added switch, locks, contact Doors, & Contact Windows triggers to pending cancel off
*	04/29/2018		Version:2.0 R.0.0.6		Started addition of Pending cancel off. Contact & Motion triggers with Switch Actions
*	04/28/2018		Version:2.0 R.0.0.5		Bug fix in mode change trigger
*	04/28/2018		Version:2.0 R.0.0.4		Added ability to disable TTS speakers and messaging due to ST running out of TTS transactions
*	04/27/2018		Version:2.0 R.0.0.3		Code cleanup and changes with aSwitches
*	04/27/2018		Version:2.0 R.0.0.2h	UI cleanup
*	04/26/2018		Version:2.0 R.0.0.2g	Code Cleanup
*	04/25/2018		Version:2.0 R.0.0.2f	Bug fix in Garage Door subscribe
*	04/25/2018		Version:2.0 R.0.0.2e	RunIn Delay added to Garage Door, Locks, and Fans
*	04/25/2018		Version:2.0 R.0.0.2d	Bug fix in Cron scheduler
*	04/25/2018		Version:2.0 R.0.0.2c	Bug fix in thermostat temp settings
*	04/24/2018		Version:2.0 R.0.0.2b	Bug fix in mode condition
*	04/24/2018		Version:2.0 R.0.0.2a	Application name change
*	04/24/2018		Version:2.0 R.0.0.2		small bug fix and added ability to run actions of other Routines
*	04/24/2018		Version:2.0 R.0.0.1a	small bug fix
*	04/23/2018		Version:2.0 R.0.0.1		Total rewrite
* 
*  Copyright 2018 Jason Headley, Bobby Dobrescu, Jason Wise
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
import org.apache.log4j.Logger
import org.apache.log4j.Level
definition(
	name			: "EchoSistant Logic Blockz",
    namespace		: "Echo",
    author			: "JH/BD",
	description		: "Powerful and full featured Rules Engine to complement you EchoSistant Rooms",
	category		: "My Apps",
    parent			: "Echo:EchoSistant Rooms",
	iconUrl			: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/LogicRulz%20Icons/LogicRulz.png",
	iconX2Url		: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/LogicRulz%20Icons/LogicRulz2x.png",
	iconX3Url		: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/LogicRulz%20Icons/LogicRulz2x.png")
/**********************************************************************************************************************************************/
private def version() { 
    	def text = "Version 2.0, Revision 0.5.1a"
        //LogicBlocks Ver 2.0 / R.0.5.0, Release date: 12/05/2018, Initial App Release Date: 04/23/2018" 
	}

preferences {

    page name: "mainProfilePage"
    page name: "eventsPage"
    page name: "conditions"
    page name: "actions"
    page name: "restrictions"
    page name: "certainTime"
    page name: "renamePage"
   
}

/******************************************************************************
	MAIN PROFILE PAGE
******************************************************************************/
def mainProfilePage() {	
    dynamicPage(name: "mainProfilePage", title:"", install: true, uninstall: installed) {
        section ("Details and Status") {
            label title: "Name this Logic Block", required:true
            	input "routineDetails", "text", title: "Purpose: give a brief description", required: false
                href "renamePage", title: "Verbal Triggers and Alexa Responses..."
                }
        section ("Pause this Logic Block") {
           	input "rPause", "bool", title: "Logic Block Status: On = Running, Off = Paused", defaultValue: false, submitOnChange: true
            }
        if (rPause == null) {
        	section ("") {
            paragraph "This Logic Block has not been configured, please activate to continue."
           	}
        }    
        if (rPause == false) {
        section(""){
        	paragraph "This Logic Block has been paused and will not perform any actions. All scheduled actions have been cancelled."
            }
        }    
        else if(rPause == true) {    
    	    section ("Logic Block Configuration") {
                href "eventsPage", title: "Events", description: pTriggerComplete(), state: pTriggerSettings()
                href "conditions", title: "Conditions", description: pConditionComplete(), state: pConditionSettings()
                href "actions", title: "Actions", description: pDevicesComplete(), state: pDevicesSettings()
   		        href "pSend", title: "Audio, Push, SMS messages, and Reports", description: pMessageComplete(), state: pMessageSettings()   
            }
        }    
    }
}


page name: "renamePage"
def renamePage() {
	dynamicPage(name: "renamePage", title:"", install: false, uninstall: false) {
    section ("") {
        label title: "When you say...", required: true, submitOnChange: true
        input "scResponse", "text", title: "Alexa responds with...", required: false, submitOnChange: true	
    }
    section ("") {
        input "alias1", "text", title: "You can also say this...", required: false//, submitOnChange: true
        input "scResponse1", "text", title: "...and, Alexa responds with this", required: false//, submitOnChange: true
    }
    section ("") {  
        input "alias2", "text", title: "or, You can say this...", required: false//, submitOnChange: true
        input "scResponse2", "text", title: "...and, Alexa responds with this", required: false//, submitOnChange: true
    }
    section ("") {
        input "alias3", "text", title: "or, You can say this...", required: false//, submitOnChange: true
        input "scResponse3", "text", title: "...and, Alexa responds with this", required: false//, submitOnChange: true
    }
    section ("") {  
        input "alias4", "text", title: "or, You can say this...", required: false//, submitOnChange: true
        input "scResponse4", "text", title: "...and, Alexa responds with this", required: false//, submitOnChange: true
    }
    section ("") {
        input "alias5", "text", title: "or, You can say this...", required: false//, submitOnChange: true
        input "scResponse5", "text", title: "...and, Alexa responds with this", required: false//, submitOnChange: true
        }

//    section ("") {
//        paragraph ("Child App Version: ${textVersion()} | Release: ${release()} | Date: ${appVerDate()}")
    }
}


def appStatusTrue() {
	def details = "${routineDetails}"
    	if (routineDetails == null) { details = "No Details Entered" }
	def status = "${rPause}"
    if (status == "true") { status = "Running / $app.label - " + " $details" }
    return status
    }
def appStatusFalse() {
	def details = "${routineDetails}"
    	if (routineDetails == null) { details = "No Details Entered" }
	def status = "${rPause}"
    log.info "status = $status"
	if (status == null) { status = "All Logic Blockz are Running" }
    if (status == "false")  { status = "Paused / $app.label - " + " $details" }
    return status
    }

    
/******************************************************************************
	EVENTS SELECTION PAGE
******************************************************************************/
page name: "eventsPage"
def eventsPage() {
    dynamicPage(name: "eventsPage", title: "Execute this Logic Block when...",install: false, uninstall: false) {
        def actions = location.helloHome?.getPhrases()*.label.sort()
    section ("Select Capabilities") {    
    		input "events", "enum", title: "Select Event Capabilities", options:["Buttons","Location & Schedules","Switches & Dimmers","Doors, Windows, & Contacts",
            "Motion","Locks & Keypads","Presence","Environmental Events"], multiple: true, required: true, submitOnChange: true
            }
    if (events != null) {
    if (events.contains("Location & Schedules")) {
    section ("Location Settings Events", hideable: true) {
            input "tMode", "mode", title: "Location Mode", multiple: true, required: false//, submitOnChange: true
        	input "tSHM", "enum", title: "Smart Home Monitor", options:["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed"], multiple: false, required: false, submitOnChange: true
			input "tRoutine", "enum", title: "SmartThings Routines", options: actions, multiple: true, required: false            
			input "mySunState", "enum", title: "Sunrise or Sunset...", options: ["Sunrise", "Sunset"], multiple: false, required: false, submitOnChange: true
			if(mySunState) input "offset", "number", range: "*..*", title: "Offset event this number of minutes (+/-)", required: false
			input "tSchedule", "enum", title: "Date/Time Schedule", submitOnChange: true, required: false, 
                options: ["One Time", "Recurring"]                    
        		
        }         
        if(tSchedule == "One Time"){
            section("At this future Time & Date", hideable:true) {        
                input "txFutureTime", "time", title: "At this time...",  required: true, submitOnChange: true
         		def todayYear = new Date(now()).format("yyyy")
                def todayMonth = new Date(now()).format("MMMM")
                def todayDay = new Date(now()).format("dd")
                input "txFutureDay", "number", title: "On this Day - maximum 31", range: "1..31", submitOnChange: true, description: "Example: ${todayDay}", required: false
                if(txFutureDay) input "txFutureMonth", "enum", title: "Of this Month", submitOnChange: true, required: false, multiple: false, description: "Example: ${todayMonth}",
                    options: ["1": "January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"]
                if(txFutureMonth) input "txFutureYear", "number", title: "Of this Year", range: "2017..2020", submitOnChange: true, description: "Example: ${todayYear}", required: false
            	
            }
        }
        if(tSchedule == "Recurring"){
            section("Recurring", hideable:true) {                 
                input "frequency", "enum", title: "Choose frequency", submitOnChange: true, required: false, 
                    options: ["Minutes", "Hourly", "Daily", "Weekly", "Monthly", "Yearly"]
                if(frequency == "Minutes"){
                    input "xMinutes", "number", title: "Every X minute(s) - maximum 60", range: "1..59", submitOnChange: true, required: false
                }
                if(frequency == "Hourly"){
                    input "xHours", "number", title: "Every X hour(s) - maximum 24", range: "1..23", submitOnChange: true, required: false
                }	
                if(frequency == "Daily"){
                    if (!xDaysWeekDay) input "xDays", "number", title: "Every X day(s) - maximum 31", range: "1..31", submitOnChange: true, required: false
                    input "xDaysWeekDay", "bool", title: "OR Every Week Day (MON-FRI)", required: false, defaultValue: false, submitOnChange: true
                    if(xDays || xDaysWeekDay){input "xDaysStarting", "time", title: "starting at time...", submitOnChange: true, required: true}
                }   
                if(frequency == "Weekly"){
                    input "xWeeks", "enum", title: "Every selected day(s) of the week", submitOnChange: true, required: false, multiple: true,
                        options: ["SUN": "Sunday", "MON": "Monday", "TUE": "Tuesday", "WED": "Wednesday", "THU": "Thursday", "FRI": "Friday", "SAT": "Saturday"]                        
                    if(xWeeks){input "xWeeksStarting", "time", title: "starting at time...", submitOnChange: true, required: true}
                }
                if(frequency == "Monthly"){
                    input "xMonths", "number", title: "Every X month(s) - maximum 12", range: "1..12", submitOnChange: true, required: false
                    if(xMonths){
                        input "xMonthsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
                        input "xMonthsStarting", "time", title: "starting at time...", submitOnChange: true, required: true
                    }
                }
                if(frequency == "Yearly"){
                    input "xYears", "enum", title: "Every selected month of the year", submitOnChange: true, required: false, multiple: false,
                        options: ["1": "January", "2":"February", "3":"March", "4":"April", "5":"May", "6":"June", "7":"July", "8":"August", "9":"September", "10":"October", "11":"November", "12":"December"]
                    if(xYears){
                        input "xYearsDay", "number", title: "...on this day of the month", range: "1..31", submitOnChange: true, required: true
                        input "xYearsStarting", "time", title: "starting at time...", submitOnChange: true, required: true                     
                    	}
                    }
                }        
            }
            
        }
        if (events.contains("Switches & Dimmers")) {
        section ("Switches", hideable: true) {
            input "tSwitch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false
            if (tSwitch) {
                input "tSwitchCmd", "enum", title: "are turned...", options:["on":"on","off":"off"], multiple: false, required: true, submitOnChange: true
                if (tSwitch.size() > 1) {
                    input "tSwitchAll", "bool", title: "ALL switches to be $tSwitchCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
            	}
            }
        section ("Dimmers", hideable: true) {
			input "tDim", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false
            if (tDim) {
                input "tDimCmd", "enum", title: "turn...", options:["on":"on","off":"off","greater":"to greater than","lessThan":"to less than","equal":"to being equal to"], multiple: false, required: false, submitOnChange: true
                if (tDimCmd == "greater" ||tDimCmd == "lessThan" || tDimCmd == "equal") {
                    input "tDimLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                	}
                }
            }
        }    
        if (events.contains("Motion")) {
        section ("Motion Sensors", hideable: true) {
            input "tMotion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true
            if (tMotion) {
                input "tMotionCmd", "enum", title: "become...", options: ["active":"active", "inactive":"inactive"], multiple: false, required: true, submitOnChange: true
                if (tMotion.size() > 1) {
                    input "tMotionAll", "bool", title: "ALL motion sensors to be $tMotionCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
            	}
            }
        }
        if (events.contains("Presence")) {
        section ("Presence Events", hideable: true) {
        	input "tPresence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true
            if (tPresence) {
                input "tPresenceCmd", "enum", title: "have...", options: ["present":"Arrived","not present":"Departed"], multiple: false, required: true, submitOnChange: true
                if (tPresence.size() > 1) {
                    input "tPresenceAll", "bool", title: "ALL presence sensors to be $tPresenceCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
                }
            }
        }
        if (events.contains("Doors, Windows, & Contacts")) {
        section ("Doors", hideable: true) {
            input "tGarage", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true
            if (tGarage) {
                input "tGarageCmd", "enum", title: "are...", options:["open":"opened", "close":"closed", "opening":"opening", "closing":"closing"], multiple: false, required: true, submitOnChange: true
        		}
            input "tContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true
            	if (tContactDoor) {
                input "tContactDoorCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (tContactDoor.size() > 1) {
                    input "tContactDoorAll", "bool", title: "ALLdoors to be $tContactDoorCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
            	}
            }    
        section ("Windows", hideable: true) {    
            input "tContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true
            	if (tContactWindow) {
                input "tContactWindowCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (tContactWindow.size() > 1) {
                    input "tContactWindowAll", "bool", title: "ALL windows to be $tContactWindowCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
            	}
            }    
        section ("Contact Sensors", hideable: true) {    
            input "tContact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: false, submitOnChange: true
            	if (tContact) {
                input "tContactCmd", "enum", title: "are...", options: ["open":"opened", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (tContact.size() > 1) {
                    input "tContactAll", "bool", title: "ALL contact sensors to be $tContactCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
            	}
            }
        }
        if (events.contains("Locks & Keypads")) {
		section ("Locks", hideable: true){
            input "tLocks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true
            if (tLocks) {
                input "tLocksCmd", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
                if (tLocks.size() > 1) {
                    input "tLocksAll", "bool", title: "ALL locks to be $tLocksCmd.", required: false, defaultValue: false, submitOnChange: true
                	}
                }
            }
        section ("Keypads", hideable: true) {    
            input "tKeypads", "capability.lockCodes", title: "Select Keypads", multiple: true, required: false, submitOnChange: true
            if (tKeypads) {
            	input "tKeyCode", "number", title: "Code (4 digits)", required: true, refreshAfterSelection: true
                input "tKeyButton", "enum", title: "Which button?", options: ["on":"On", "off":"Off", "partial":"Partial", "panic":"Panic"], multiple: false, required: true, submitOnChange: true           
            		}
                }
            }
        if (events.contains("Environmental Events")) {
        section ("Environmental Events", hideable: true) {
         	input "tTemperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true
         	input "tTempRead", "enum", title: "Temperature is...", options: ["between","below","above"], required: true, multiple: false, submitOnChange: true
            	if (tTempRead == "between") {
                	input "tempLow", "number", title: "a low Temperature of...", required: true, submitOnChange: true
                	input "tempHigh", "number", title: "and a high Temperature of...", required: true, submitOnChange: true
                	}
                if (tTempRead == "below") {
                	input "tempLow", "number", title: "a temperature of...", required: true, submitOnChange: true
                    }
                if (tTempRead == "above") {
                	input "tempHigh", "number", title: "a temperature of...", required: true, submitOnChange: true
                    }
                if (tTempRead) input "tempOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
        	input "tFeelsLike", "capability.relativeHumidityMeasurement", title: "How hot/cold it 'Feels'", required: false, multiple: true, submitOnChange: true
            input "tWind", "capability.sensor", title: "Wind Speed", multiple: true, required: false, submitOnChange: true
            	if (tWind) input "tWindLevel", "enum", title: "Activate when Wind Speed is...", options: ["above", "below"], required: false, submitOnChange: true            
            	if (tWind) input "tWindSpeed", "number", title: "Wind Speed Level...", required: true, description: "mph", submitOnChange: true            
            	if (tWind) input "windOnce", "bool", title: "Perform actions only once when true", required: false, defaultValue: false, submitOnChange: true
         	input "tRain", "capability.sensor", title: "Rain Accumulation", multiple: true, required: false, submitOnChange: true
            	if (tRain) input "tRainAction", "enum", title: "Activate when the Rain...", options: ["begins", "ends", "begins and ends"], required: false, defaultValue: true, submitOnChange: true            
            		if (tRainAction == "begins" || tRainAction == "begins and ends") { input "rainStartMsg", "text", title: "Send this message when it begins to rain" }
            		if (tRainAction == "ends" || tRainAction == "begins and ends") { input "rainStopMsg", "text", title: "Send this message when it stops raining" }
            input "tHumidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true
            	if (tHumidity) input "tHumidityLevel", "enum", title: "Activate when Relative Humidity is...", options: ["above", "below"], required: false, submitOnChange: true            
            	if (tHumidity) input "tHumidityPercent", "number", title: "Relative Humidity Level...", required: true, description: "percent", submitOnChange: true            
            	if (tHumidity) input "humOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
           	input "tWater", "capability.waterSensor", title: "Water/Moisture Sensors", required: false, multiple: true, submitOnChange: true
                if (tWater) input "tWaterStatus", "enum", title: "Activate when state changes to...", options: ["wet", "dry", "both"], required: false		
         	input "tSmoke", "capability.smokeDetector", title: "Smoke Detectors", required: false, multiple: true, submitOnChange: true
                if (tSmoke) input "tSmokeStatus", "enum", title: "Activate when smoke is...", options: ["detected", "clear", "both"], required: false
          	input "myCO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO2)", required: false, multiple: true, submitOnChange: true
                if (myCO2) input "myCO2S", "enum", title: "Activate when CO2 is...", options: ["above", "below"], required: false, submitOnChange: true            
                if (myCO2S) input "CO2", "number", title: "Carbon Dioxide Level...", required: true, description: "number", submitOnChange: true              
				if (myCO2S) input "CO2Once", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
         	input "tLux", "capability.illuminanceMeasurement", title: "Lux Level", required: false, submitOnChange: true
            	if (tLux) input "tLuxLow", "number", title: "A low lux level of...", required: true, submitOnChange: true
                if (tLux) input "tLuxHigh", "number", title: "and a high lux level of...", required: true, submitOnChange: true
				if (tLux) input "luxOnce", "bool", title: "Perform this check only once", required: false, defaultValue: false, submitOnChange: true
                }
            }
		}
    }
}    
/******************************************************************************
	CONDITIONS SELECTION PAGE
******************************************************************************/
page name: "conditions"
def conditions() {
    dynamicPage(name: "conditions", title: "Execute this routine when...",install: false, uninstall: false) {
        section ("Location Settings Conditions") {
            input "cMode", "mode", title: "Location Mode is...", multiple: true, required: false, submitOnChange: true
        	input "cSHM", "enum", title: "Smart Home Monitor is...", options:["away":"Armed (Away)","stay":"Armed (Home)","off":"Disarmed"], multiple: false, required: false, submitOnChange: true
            input "cDays", title: "Days of the week", multiple: true, required: false, submitOnChange: true,
                "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            href "certainTime", title: "Time Schedule", description: pTimeComplete(), state: pTimeSettings()
        }         
        section ("Switch and Dimmer Conditions") {
            input "cSwitch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false
            if (cSwitch) {
                input "cSwitchCmd", "enum", title: "are...", options:["on":"On","off":"Off"], multiple: false, required: true, submitOnChange: true
                if (cSwitch?.size() > 1) {
                    input "cSwitchAll", "bool", title: "Activate this toggle if you want ALL of the switches to be $tSwitchCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                	}
            }
            input "cDim", "capability.switchLevel", title: "Dimmers", multiple: true, submitOnChange: true, required: false
            if (cDim) {
                input "cDimCmd", "enum", title: "is...", options:["greater":"greater than","lessThan":"less than","equal":"equal to"], multiple: false, required: false, submitOnChange: true
                if (cDimCmd == "greater" ||cDimCmd == "lessThan" || cDimCmd == "equal") {
                    input "cDimLvl", "number", title: "...this level", range: "0..100", multiple: false, required: false, submitOnChange: true
                if (cDim.size() > 1) {
                    input "cDimAll", "bool", title: "Activate this toggle if you want ALL of the dimmers for this condition.", required: false, defaultValue: false, submitOnChange: true
                	}
                }
            }
        }
        section ("Motion and Presence Conditions") {
            input "cMotion", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false, submitOnChange: true
            if (cMotion) {
                input "cMotionCmd", "enum", title: "are...", options: ["active":"active", "inactive":"inactive"], multiple: false, required: true, submitOnChange: true
            	if (cMotion?.size() > 1) {
                	input "cMotionAll", "bool", title: "Activate this toggle if you want ALL of the Motion Sensors to be $cMotionCmd as a condition."
                    }
                }
        	input "cPresence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true
            if (cPresence) {
                input "cPresenceCmd", "enum", title: "are...", options: ["present":"Present","not present":"Not Present"], multiple: false, required: true, submitOnChange: true
                if (cPresence?.size() > 1) {
                    input "cPresenceAll", "bool", title: "Activate this toggle if you want ALL of the Presence Sensors to be $cPresenceCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                	}
                }
            }
        section ("Door, Window, and other Contact Sensor Conditions") {
            input "cContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true
            	if (cContactDoor) {
                input "cContactDoorCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (cContactDoor?.size() > 1) {
                	input "cContactDoorAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be $cContactDoorCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                    }
            	}
            input "cContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true
            	if (cContactWindow) {
                input "cContactWindowCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (cContactWindow?.size() > 1) {
                	input "cContactWindowAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be $cContactWindowCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                    }
            	}
            input "cContact", "capability.contactSensor", title: "All Other Contact Sensors", multiple: true, required: false, submitOnChange: true
            	if (cContact) {
                input "cContactCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
            	if (cContact?.size() > 1) {
                	input "cContactAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be $cContactCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                    }
            	}
            }
		section ("Garage Door and Lock Conditions"){
            input "cLocks", "capability.lock", title: "Smart Locks", multiple: true, required: false, submitOnChange: true
            if (cLocks) {
                input "cLocksCmd", "enum", title: "are...", options:["locked":"locked", "unlocked":"unlocked"], multiple: false, required: true, submitOnChange:true
            }
            input "cGarage", "capability.garageDoorControl", title: "Garage Doors", multiple: true, required: false, submitOnChange: true
            if (cGarage) {
                input "cGarageCmd", "enum", title: "are...", options:["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
        	}
        }
        section ("Environmental Conditions") {
        	input "cHumidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true
            	if (cHumidity) input "cHumidityLevel", "enum", title: "Only when the Humidity is...", options: ["above", "below"], required: false, submitOnChange: true            
            	if (cHumidityLevel) input "cHumidityPercent", "number", title: "this level...", required: true, description: "percent", submitOnChange: true            
                if (cHumidityPercent) input "cHumidityStop", "number", title: "...but not ${cHumidityLevel} this percentage", required: false, description: "humidity"
            input "cTemperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true
				if (cTemperature) input "cTemperatureLevel", "enum", title: "When the temperature is...", options: ["above", "below"], required: false, submitOnChange: true
                if (cTemperatureLevel) input "cTemperatureDegrees", "number", title: "Temperature...", required: true, description: "degrees", submitOnChange: true
                if (cTemperatureDegrees) input "cTemperatureStop", "number", title: "...but not ${cTemperatureLevel} this temperature", required: false, description: "degrees"
		}
    }
} 

/******************************************************************************
	ACTIONS
******************************************************************************/
page name: "actions"
def actions() {
    dynamicPage(name: "actions", title: "These Actions will occur...",install: false, uninstall: false) {
    	section ("Select Actions Capabilities") {    
    		input "actions", "enum", title: "Select Actions Capabilities", options:["Switches","Dimmers","Color Bulbs","Virtual Presence",
            "Locks & Doors","Fans & Ceiling Fans","Thermostats, Vents, & Shades","Custom Commands","Device Health","Mode, SHM, Routines, Logic Blocks & RemindR Profiles"], multiple: true, required: true, submitOnChange: true
            }
        if (actions.contains("Switches")) {    
            section("Simple On/Off/Toggle Switches", hideable: true, hidden: false) {    
                input "aOtherSwitches", "capability.switch", title: "On/Off/Toggle Lights & Switches", multiple: true, required: false, submitOnChange: true
                if (aOtherSwitches) {
                    input "aOtherSwitchesCmd", "enum", title: "...will turn...", options: ["on":"on","off":"off","toggle":"toggle"], multiple: false, required: false, submitOnChange: true
                }
            }    
            if (aOtherSwitchesCmd != null) {
            section("More Simple On/Off/Toggle Switches", hideable: true, hidden: false) {    
                input "aOtherSwitches2", "capability.switch", title: "On/Off/Toggle Lights & Switches", multiple: true, required: false, submitOnChange: true
                if (aOtherSwitches2) {
                    input "aOtherSwitchesCmd2", "enum", title: "...will turn...", options: ["on":"on","off":"off","toggle":"toggle"], multiple: false, required: false, submitOnChange: true
                	}
                }
            }
			if (events?.contains("Motion")) {
            section("Switches with both On & Off delay and both with Pending Cancellations (Motion Event Only)", hideable: true, hidden: false) {    
                input "aPendSwitches", "capability.switch", title: "Lights and Switches with a pending cancellation delay", multiple: true, required: false, submitOnChange: true
                if (aPendSwitches) {
                    input "aPendSwitchesCmd", "enum", title: "are turned...", options:["on":"On", "off":"Off"], multiple: false, required: true, submitOnChange: true
                }
                if (aPendSwitchesCmd && aPendSwitches) {
                    input "pendDelayOn", "number", title: "Delay turning on by this many seconds", required: false, submitOnChange: true
                }
                if (aPendSwitchesCmd && aPendSwitches) {
                    input "pendDelayOff", "number", title: "Delay turning off by this many seconds", required: false, submitOnChange: true
                }
                if (aPendSwitchesCmd && aPendSwitches) {
                    input "pendAll", "bool", title: "All contact/motion sensors inactive, and all switces off", required: false, defaultValue: true, submitOnChange: true
                	}
                }
            }
            section ("Delays On/Off (Optional Pending Cancellation)", hideable: true, hidden: false) {    
                input "aSwitchesOn", "capability.switch", title: "These switches will delay turning ON (with optional pending cancel)", multiple: true, required: false, submitOnChange: true
                if (aSwitchesOn) {
                    input "aSwitchesOnDelay", "number", title: "...by this many seconds", required: true, defaultValue: 30, submitOnChange: true
                    if (aSwitchesOn) input "aSwitchesOnPend", "bool", title: "activate for a pending state change cancel", defaultValue: false, required: false
                }
                input "aSwitchesOff", "capability.switch", title: "These switches will delay turning OFF (with optional pending cancel", multiple: true, required: false, submitOnChange: true
                if (aSwitchesOff) {
                    input "aSwitchesOffDelay", "number", title: "...by this many seconds", required: true, defaultValue: 30, submitOnChange: true
                    if (aSwitchesOff) input "aSwitchesOffPend", "bool", title: "activate for a pending state change cancel", defaultValue: false, required: false
                }
				if (aSwitchesOn && aSwitchesOff) paragraph "***Warning*** If using the same switches for both delay on and delay off, ensure that your " +
                "delay off time is the delay you want INCLUDING the delay on time. \n" +
                "Ex: delay on = 10 & delay off = 20 means the switch will turn on in 10 seconds and will turn off 10 seconds later " +
                "for a total time of 20 seconds."
            }
        }
        if (actions.contains("Switches")) {
        if (events?.contains("Switches & Dimmers") || events?.contains("Motion") || events?.contains("Doors, Windows, & Contacts")) {
            section ("Switches (Motion/Contact/Switches Events Only) with Pending Cancellation - Selection", hideable: true, hidden: false) {        
                input "aSwitches", "capability.switch", title: "Lights and Switches", multiple: true, required: false, submitOnChange: true 
                if (aSwitches) {
                    input "aSwitchesCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","toggle":"toggle"], multiple: false, required: false, submitOnChange:true
                    if (aSwitchesCmd=="on") {  
                        input "ContactOff", "bool", title: "Turn off when the selected event changes back?", defaultValue: false, submitOnChange: true
                    }
                    if (aSwitchesCmd=="off") { 
                        input "ContactOff", "bool", title: "Turn on when the selected event changes back?", defaultValue: false, submitOnChange: true
                    }
                    if (aSwitchesCmd && ContactOff) {
                        input "ContactOffDelay", "number", title: "Delay this action by how long?", defaultValue: 0, submitOnChange: true
                        if (ContactOffDelay) input "aSwitchesCancel", "bool", title: "Activate for a pending cancellation (Only if Motion is the Execution Event)", defaultValue: false, required: true
                    	}
                    }
                }
            }
        }    
    if (actions.contains("Dimmers")) {    
        section ("Dimmers - Selection", hideable: true, hidden: false) {    
            input "aDim", "capability.switchLevel", title: "Dimmable Lights and Switches", multiple: true, required: false , submitOnChange:true
            if (aDim) {
                input "aDimCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","set":"set the level","decrease":"decrease","increase":"increase"], multiple: false, required: false, submitOnChange: true
                if (aDimCmd=="decrease") {
                    input "aDimDecrease", "number", title: "the lights by this %", required: false, submitOnChange: true
                }
                if (aDimCmd == "increase") {
                    input "aDimIncrease", "number", title: "the lights by this %", required: false, submitOnChange: true
                }
                if (aDimCmd == "set") {
                    input "aDimLVL", "number", title: "...of the lights to...", description: "this percentage", range: "0..100", required: false, submitOnChange: true
                }
                input "aDimDelay", "number", title: "Delay this action by this many seconds.", required: false, defaultValue: 0, submitOnChange: true
            }
        }    
		section("More Dimmers - Selection", hideable: true, hidden: false) {
            input "aOtherDim", "capability.switchLevel", title: "More Dimmers", multiple: true, required: false , submitOnChange:true
                if (aOtherDim) {
                    input "aOtherDimCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","set":"set the level","decrease":"decrease","increase":"brighten"], multiple: false, required: false, submitOnChange:true
                    if (aOtherDimCmd=="decrease") {
                        input "aOtherDimDecrease", "number", title: "the lights by this %", required: false, submitOnChange: true
                    }
                    if (aOtherDimCmd == "increase") {
                        input "aOtherDimIncrease", "number", title: "the lights by this %", required: false, submitOnChange: true
                    }
                    if (aOtherDimCmd == "set") {
                        input "aOtherDimLVL", "number", title: "...of the lights to...", description: "this percentage", range: "0..100", required: false, submitOnChange: true
                    }
                    input "otherDimDelay", "number", title: "Delay this action by this many seconds.", required: false, defaultValue: 0, submitOnChange: true
                }
            }
        section ("Dimmer Levels set per Mode", hideable: true, hidden: false) {
        	input "modeDimmers", "capability.switchLevel", title: "Select Dimmers for Mode Level actions", required: false, multiple: true, submitOnChange: true
        		if (modeDimmers) {
                    def modeList = []
            		location.modes.each {modeList << "$it"}
                    input "dimmModes", "enum", title: "Select Modes", options:  modeList.sort(), required: false, multiple: true, submitOnChange: true
         				if (dimmModes) {
                    	def modeLevels = []
                  		dimmModes.each {getDimmModesLevels(it, "level" + "$it")}  
                    input "dimmerModesDelay", "number", title: "delay these actions by this many seconds", defaultValue: 0, required: false, submitOnChange: true
                    }
        		}
        	}
        }   

    if (actions.contains("Color Bulbs")) {
    	section ("Changing the Color", hideable: true, hidden: false){
            input "aHues", "capability.colorControl", title: "Colored Lights", multiple: true, required: false, submitOnChange:true
            if (aHues) {
                input "aHuesCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","setColor":"set the color"], multiple: false, required: false,
                    submitOnChange:true
                if(aHuesCmd == "setColor") {
                    input "aHuesColor", "enum", title: "...to...", required: false, multiple:false, options: fillColorSettings()?.name
                }
                input "aHuesColorDelay", "number", title: "delay this action by this many seconds", required: false, defaultValue: 0, submitOnChange: true
            }
        }
        if (aHuesCmd) {
            section("...and more color control", hideable: true, hidden: false) {
                input "aHuesOther", "capability.colorControl", title: "...and even more colored lights", multiple: true, required: false, submitOnChange:true
                if (aHuesOther) {
                    input "aHuesOtherCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","setColor":"set the color"], multiple: false, required: false,
                        submitOnChange:true
                    if(aHuesOtherCmd == "setColor") {
                        input "aHuesOtherColor", "enum", title: "...to...", required: false, multiple:false, options: fillColorSettings()?.name
                    	}
                    input "aHuesOtherColorDelay", "number", title: "delay this action by this many seconds", required: false, defaultValue: 0, submitOnChange: true    
                    }
                }
            }
        }

        
    if (actions.contains("Fans & Ceiling Fans")) {    
        section ("Fans connected to switches", hideable: true, hidden: false) {
            input "aFans", "capability.switch", title: "These Fans...", multiple: true, required: false, submitOnChange: true, image: "https://raw.githubusercontent.com/bamarayne/master/LogicRulz/icons/fan.png"
            if (aFans) {
                input "aFansCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off"], multiple: false, required: false, submitOnChange:true
                if (aFansCmd=="on") {
                    input "aFansDelayOn", "number", title: "Delay turning on by this many seconds", defaultValue: 0, submitOnChange:true
                	if (aFansDelayOn) input "aFansPendOn", "bool", title: "Activate for pending state change cancellation", required: false, defaultValue: false
                }
                if (aFansCmd=="off") {
                    input "aFansDelayOff", "number", title: "Delay turning off by this many seconds", defaultValue: 0, submitOnChange:true
                	if (aFansDelayOff) input "aFansPendOff", "bool", title: "Activate for pending state change cancellation", required: false, defaultValue: false
                    }
            }
        }
        section ("Fans and Ceiling Fan Settings (adjustable)", hideable: true, hidden: false) {
            input "aCeilingFans", "capability.switchLevel", title: "These ceiling fans...", multiple: true, required: false, submitOnChange: true
            if (aCeilingFans) {
                input "aCeilingFansCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","low":"set to low","med":"set to med","high":"set to high","incr":"speed up","decr":"slow down"], multiple: false, required: false, submitOnChange:true
                if (aCeilingFansCmd == "incr") {
                    input "aCeilingFansIncr", "number", title: "...by this percentage", required: true, submitOnChange: true
                }
                if (aCeilingFansCmd == "decr") {
                    input "aCeilingFansDecr", "number", title: "...by this percentage", required: true, submitOnChange: true
                	}
                }
            }
        }
    if (actions.contains("Locks & Doors")) {    
        section ("Locks", hideable: true, hidden: false){
            input "aLocks", "capability.lock", title: "These locks...", multiple: true, required: false, submitOnChange: true
            if (aLocks) {
                input "aLocksCmd", "enum", title: "...will...?", options:["lock":"lock","unlock":"unlock"], multiple: false, required: false, submitOnChange:true
                if (aLocksCmd=="lock") {
                    input "aLocksDelayLock", "number", title: "Delay locking by this many seconds", defaultValue: 0, submitOnChange:true
                }
                if (aLocksCmd=="unlock") {
                    input "aLocksDelayUnlock", "number", title: "Delay unlocking by this many seconds", defaultValue: 0, submitOnChange:true
                }
            }
        }
        section ("Garage Doors", hideable: true, hidden: false) {
            input "aDoor", "capability.garageDoorControl", title: "These garage doors...", multiple: true, required: false, submitOnChange: true
            if (aDoor) {
                input "aDoorCmd", "enum", title: "...will...", options:["open":"open","close":"close"], multiple: false, required: false, submitOnChange:true
                if (aDoorCmd=="open") {
                    input "aDoorDelayOpen", "number", title: "Delay opening by this many seconds", required: false, defaultValue: 0, submitOnChange: true
                }
                if (aDoorCmd=="close") {
                    input "aDoorDelayClose", "number", title: "Delay closing by this many seconds", required: false, defaultValue: 0, submitOnChange: true
                	}
                }
            }
        }
    if (actions.contains("Thermostats, Vents, & Shades")) {    
        section ("Thermostat", hideable: true, hidden: false) {
            input "cTstat", "capability.thermostat", title: "...and these thermostats will...", multiple: true, required: false, submitOnChange:true
            if (cTstat) {
                input "cTstatFan", "enum", title: "...set the fan mode to...", options:["auto":"auto","on":"on","off":"off","circ":"circulate"], multiple: false, required: false, submitOnChange:true
                input "cTstatMode", "enum", title: "...set the operating mode to...", options:["cool":"cooling","heat":"heating","auto":"auto","on":"on","off":"off","incr":"increase","decr":"decrease"], multiple: false, required: false, submitOnChange:true
                if (cTstatMode in ["cool","auto"]) { input "coolLvl", "number", title: "Cool Setpoint", required: true, submitOnChange: true}
                if (cTstatMode in ["heat","auto"]) { input "heatLvl", "number", title: "Heat Setpoint", required: true, submitOnChange: true}
                if (cTstatMode in ["incr","decr"]) {
                    if (cTstatMode == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                    if (cTstatMode == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                    input "tempChange", "number", title: "By this amount...", required: true, submitOnChange: true }
            }
        }
        if(cTstat) {
            section("Thermostats", hideable: true, hidden: false) {
                input "cTstat1", "capability.thermostat", title: "More Thermostat(s)...", multiple: true, required: false, submitOnChange:true
                if (cTstat1) {
                    input "cTstat1Fan", "enum", title: "Fan Mode", options:["auto":"Auto","on":"On","off":"Off","circ":"Circulate"],multiple: false, required: false, submitOnChange:true
                    input "cTstat1Mode", "enum", title: "Operating Mode", options:["cool":"Cool","heat":"Heat","auto":"Auto","on":"On","off":"Off","incr":"Increase","decr":"Decrease"],multiple: false, required: false, submitOnChange:true
                    if (cTstat1Mode in ["cool","auto"]) { input "coolLvl1", "number", title: "Cool Setpoint", required: true, submitOnChange: true }
                    if (cTstat1Mode in ["heat","auto"]) { input "heatLvl1", "number", title: "Heat Setpoint", required: true, submitOnChange: true }
                    if (cTstat1Mode in ["incr","decr"]) {
                        if (cTstat1Mode == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                        if (cTstat1Mode == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                        input "tempChange1", "number", title: "By this amount...", required: true, submitOnChange: true }
                }
            }
        }
        section ("Vents", hideable: true, hidden: false) {
            input "aVents", "capability.switchLevel", title: "These vents...", multiple: true, required: false, submitOnChange: true
            if (aVents) {
                input "aVentsCmd", "enum", title: "...will...",
                    options:["on":"open","off":"close","25":"change to 25% open","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
            }
        }
        section ("Shades", hideable: true, hidden: false){
            input "aShades", "capability.windowShade", title: "These window coverings...", multiple: true, required: false, submitOnChange: true
            if (aShades) {
                input "aShadesCmd", "enum", title: "...will...", options:["on":"open","off":"close","25":"change to 25% oetn","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
            	}
            }
        }
    if (actions.contains("Virtual Presence")) {    
        section ("Virtual Presence Sensors", hideable: true, hidden: false){
            input "aPresence", "capability.presenceSensor", title: "These presence sensors...", multiple: true, required: false, submitOnChange: true
            if (aPresence) {
                input "aPresenceCmd", "enum", title: "...will change to...", options:["arrived":"present","departed":"not present"], multiple: false, required: false, submitOnChange:true
            	}
            }
        }
    if (actions.contains("Mode, SHM, Routines, Logic Blocks & RemindR Profiles")) {
    	section("Mode, SHM, SmartThings Routines, & Logic Blockz", hideable: true, hidden: false){
            input "tSelf", "enum", title: "Perform the actions of these Logic Blokz", options: getRoutines(), multiple: true, required: false, submitOnChange: true
            	if (tSelf) input "tSelfDelay", "number", title: "Delay running by this number of seconds", defaultValue: 0, required: false, submitOnChange: true
            		if (tSelfDelay) input "tSelfPend", "bool", title: "Activate for pending cancellation", defaultValue: false, required: false, submitOnChange: true
            input "remindRProfile", "enum", title: "Execute this RemindR Profile...", options:  parent.listRemindRProfiles(), multiple: false, required: false
        	
            if(!tMode){
                def modes = location.modes.name.sort()
                input "aMode", "enum", title: "Change the Mode to", options: modes, multiple: false, required: false
            }
            if(!tSHM){
                input "aSHM", "enum", title: "Change Smart Home Monitor to", options:["away":"Armed (Away)","home":"Armed (Home)","disarmed":"Disarmed"], multiple: false, required: false, submitOnChange: true
            }
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
                actions.sort()
                if (parent.debug) log.trace actions
                input "aRout1", "enum", title: "Run these SmartThings Routines", options: actions, multiple: true, required: false, submitOnChange: true
            }
            // THESE RESTRICTIONS APPLY ONLY TO THE RUNNING OF THE 1ST SET OF ST ROUTINES. MAIN ROUTINE RESTRICTIONS AFFECT THESE AS WELL
            if (aRout1) {
                input "aRoutDelay1", "number", title: "...delay running these ST routines by this many seconds...", required: true, defaultValue: 0, submitOnChange: true
                if (aRoutDelay1 > 0) paragraph "When delayed, ST Routines automatically have a pending cancel feature."
                href "aRoutCertainTime1", title: "...only during this time schedule", description: strTimeComplete(), state: strTimeSettings()
                input "aRoutDays1", title: "...or, only on these days of the week", multiple: true, required: false, submitOnChange: true,
                    "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            	input "str1Msg", "text", title: "Send this message when this routine is executed", required: false, submitOnChange: true
            }
            if (aRout1) {
                actions.sort()
                input "aRout2", "enum", title: "...as well as running these SmartThings Routines", options: actions, multiple: true, required: false, submitOnChange: true
            }
            // THESE RESTRICTIONS APPLY ONLY TO THE RUNNING OF THE 2ND SET OF ST ROUTINES. MAIN ROUTINE RESTRICTIONS AFFECT THESE AS WELL
            if (aRout2) {
                input "aRoutDelay2", "number", title: "...delay running these ST routines by this many seconds...", required: true, defaultValue: 0, submitOnChange: true
                href "aRoutCertainTime2", title: "...only during this time schedule", description: strTimeComplete1(), state: strTimeSettings1()
                input "aRoutDays2", title: "...or, only on these days of the week", multiple: true, required: false, submitOnChange: true,
                    "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            	input "str2Msg", "text", title: "Send this message when this routine is executed", required: false, submitOnChange: true
                }
            }
        }
    }    
}
/*def eventHandler(evt) {
    def myState = app.currentState("someAttribute")
    log.debug "event was last updated: ${myState.lastUpdated}"
}*/


/******************************************************************************************************
	PUSH, SMS, AND AUDIO MESSAGES DEVICE SELECTIONS AND CONFIGURATION
******************************************************************************************************/
page name: "pSend"
def pSend(){
    dynamicPage(name: "pSend", title: "Audio and Text Message Settings", uninstall: false){
		section ("Custom Message"){
        	input "message", "text", title: "Send this message when this Logic Block is executed", required: false, submitOnChange: true, defaultValue: ""
            }
            if(message) {
				def report
               	section ("Preview Message", hideable: true, hidden: true) {
                	paragraph report = runProfile(message, evt) 
				}
            }
        section ("Tap here to see available &variables", hideable: true, hidden: true) {    
                    paragraph 	"CUSTOM MESSAGES: \n"+
                    				"Devices: \n"+
                                    "&device, &action \n"+
                                    "&winOpen, &winClosed \n"+
                                    "&doorOpen, &doorClosed \n"+
                                    "&lightsOn, &fans \n"+
                                    "\n"+
                                    "LOCATION: \n"+
                                    "&time, &date, &stRoutAction, &stRoutEvent \n"+ 
                                    "&shm, &mode \n"+
                                    "Temperature Variables: \n"+
                                    "&tempIn, &tempOut, &high, &low, &tempTrend, &feelsLike \n"+
                                    "\n"+
                                    "Sensors Variables: \n"+
                                    "&smoke, &CO2, &water \n"+
                                    "\n"+
                                    "Climate Variables: \n"+
                                    "&windSpeed, &windDir, &rain, &humIn, &humOut \n"+
                                    "\n"
					                }
		section ("Message Variables Devices") {
        	input "vWindows", "capability.contactSensor", title: "Windows", required: false, multiple: true, submitOnChange: true
            input "vDoors", "capability.contactSensor", title: "Doors", required: false, multiple: true, submitOnChange: true
            input "vLights", "capability.switch", title: "Lights", required: false, multiple: true, submitOnChange: true
            input "vFans", "capability.switch", title: "Fans", required: false, multiple: true, submitOnChange: true
            input "vTempIn", "capability.temperatureMeasurement", title: "Temperature Inside", required: false, multiple: true, submitOnChange: true
            input "vTempOut", "capability.temperatureMeasurement", title: "Temperature Outside", required: false, multiple: true, submitOnChange: true
        	input "vFeelsLike", "capability.relativeHumidityMeasurement", title: "How it feels", required: false, multiple: true, submitOnChange: true
            input "vWind", "capability.sensor", title: "Wind Speed, Direction, Gusts", multiple: true, required: false, submitOnChange: true
            input "vRain", "capability.sensor", title: "Rain Accumulation", multiple: true, required: false, submitOnChange: true
            input "vHumIn", "capability.relativeHumidityMeasurement", title: "Relative Humidity Inside", multiple: true, required: false, submitOnChange: true
            input "vHumOut", "capability.relativeHumidityMeasurement", title: "Relative Humidity Outside", multiple: true, required: false, submitOnChange: true
            input "vLux", "capability.illuminanceMeasurement", title: "Lux Level", required: false, submitOnChange: true
           	input "vWater", "capability.waterSensor", title: "Water/Moisture Sensors", required: false, multiple: true, submitOnChange: true
            input "vSmoke", "capability.smokeDetector", title: "Smoke Detectors", required: false, multiple: true, submitOnChange: true
            input "vCO2", "capability.carbonDioxideMeasurement", title: "Carbon Dioxide (CO2)", required: false, multiple: true, submitOnChange: true
			}        	
        section ("Audio Output Devices"){
        	input "report", "bool", title: "Activate to play the message above as the Alexa response to running this Logic Block", defaultValue: true, submitOnChange: true
        	input "speakDisable", "bool", title: "Activate this toggle to configure SMS/Audio/Push messages", defaultValue: false, submitOnChange: true
            if (speakDisable==true) {
            input "smc", "bool", title: "Send the message to Smart Message Control", defaultValue: false, submitOnChange: true
            input "synthDevice", "capability.speechSynthesis", title: "Speech Synthesis Devices", multiple: true, required: false
        	}    
        }    
        section ("") {
            if (speakDisable==true) {
        	input "echoDevice", "device.echoSpeaksDevice", title: "Amazon Alexa Devices", multiple: true, required: false
            input "sonosDevice", "capability.musicPlayer", title: "Music Player Devices", required: false, multiple: true, submitOnChange: true    
            if (sonosDevice) {
                input "volume", "number", title: "Temporarily change volume", description: "0-100% (default value = 30%)", required: false
            	}
            }
        }    
        section ("Text Messages" ) {
            if (speakDisable==true) {
            input "sendText", "bool", title: "Enable Text Notifications", required: false, submitOnChange: true     
            if (sendText){      
                paragraph "You may enter multiple phone numbers separated by comma to deliver the Alexa message. E.g. +18045551122,+18046663344"
                input name: "sms", title: "Send text notification to (optional):", type: "phone", required: false
            	}
            }
        }    
        section ("Push Messages") {
            if (speakDisable==true) {
            input "push", "bool", title: "Send Push Notification (optional)", required: false, defaultValue: false
        	}
        }    
        section ("Messaging Restrictions"){
            if (speakDisable==true) {
            href "smsCertainTime", title: "SMS/Push/Audio Message only during this time schedule", description: pmTimeComplete(), state: pmTimeSettings()
            input "smsDays", title: "...or, only on these days of the week", multiple: true, required: false, submitOnChange: true,
                "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        	}
        }   
    }                 
}

/******************************************************************************************************
	RESTRICTIONS TIME SELECTION PAGE FOR MESSAGING SECTION ONLY
******************************************************************************************************/
page name: "smsCertainTime"
def smsCertainTime() {
    dynamicPage(name:"smsCertainTime",title: "Only during this time schedule...", uninstall: false) {
        section("Beginning at....") {
            input "startingXm", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingXm in [null, "A specific time"]) input "startingm", "time", title: "Start time", required: false, submitOnChange: true
            else {
                if(startingXm == "Sunrise") input "startSunriseOffsetm", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(startingXm == "Sunset") input "startSunsetOffsetm", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
            }
        }
        section("Ending at....") {
            input "endingXm", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingXm in [null, "A specific time"]) input "endingm", "time", title: "End time", required: false, submitOnChange: true
            else {
                if(endingXm == "Sunrise") input "endSunriseOffsetm", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(endingXm == "Sunset") input "endSunsetOffsetm", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
        	}
        }
    }
}

/****************************************************************************************
	SMARTTHINGS ROUTINES RESTRICTION PAGE - TIME SELECTIONS / APPLY TO ST ROUTINES ONLY
*****************************************************************************************/
page name: "aRoutCertainTime1"
def aRoutCertainTime1() {
    dynamicPage(name:"aRoutCertainTime1",title: "Only during this time schedule...", uninstall: false) {
        section("Beginning at....") {
            input "startingXR1", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingXR1 in [null, "A specific time"]) input "startingR1", "time", title: "Start time", required: false, submitOnChange: true
            else {
                if(startingXR1 == "Sunrise") input "startSunriseOffsetR1", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(startingXR1 == "Sunset") input "startSunsetOffsetR1", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
            }
        }
        section("Ending at....") {
            input "endingXR1", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingXR1 in [null, "A specific time"]) input "endingR1", "time", title: "End time", required: false, submitOnChange: true
            else {
                if(endingXR1 == "Sunrise") input "endSunriseOffsetR1", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(endingXR1 == "Sunset") input "endSunsetOffsetR1", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
            }
        }
    }
}
/****************************************************************************************
SMARTTHINGS ROUTINES (2nd SET) RESTRICTION PAGE - TIME SELECTIONS / APPLY TO ST ROUTINES ONLY
*****************************************************************************************/
page name: "aRoutCertainTime2"
def aRoutCertainTime2() {
    dynamicPage(name:"aRoutCertainTime2",title: "Only during this time schedule...", uninstall: false) {
        section("Beginning at....") {
            input "startingXR2", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingXR2 in [null, "A specific time"]) input "startingR2", "time", title: "Start time", required: false, submitOnChange: true
            else {
                if(startingXR2 == "Sunrise") input "startSunriseOffsetR2", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(startingXR2 == "Sunset") input "startSunsetOffsetR2", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
            }
        }
        section("Ending at....") {
            input "endingXR2", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingXR2 in [null, "A specific time"]) input "endingR2", "time", title: "End time", required: false, submitOnChange: true
            else {
                if(endingXR2 == "Sunrise") input "endSunriseOffsetR2", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(endingXR2 == "Sunset") input "endSunsetOffsetR2", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
            }
        }
    }
}

/******************************************************************************************************
	RESTRICTIONS - MAIN APP TIME 
******************************************************************************************************/
page name: "certainTime"
def certainTime() {
    dynamicPage(name:"certainTime",title: "", uninstall: false) {
        section("") {
            input "startingX", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingX in [null, "A specific time"]) input "starting", "time", title: "Start time", required: false, submitOnChange: true
            else {
                if(startingX == "Sunrise") input "startSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(startingX == "Sunset") input "startSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    }
        }
        section("") {
            input "endingX", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingX in [null, "A specific time"]) input "ending", "time", title: "End time", required: false, submitOnChange: true
            else {
                if(endingX == "Sunrise") input "endSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(endingX == "Sunset") input "endSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    }
        }
    }
}

/******************************************************************************************************
	PAUSE ROUTINES FROM THE PARENT APP TOGGLE
******************************************************************************************************/
private routinePause() {
    if (parent.aPause == false) {
        log.warn "$app.label routine has been paused by the parent"
        state.allPause = false
        log.warn "state.allPause = $state.allPause"
    }
    if (parent.aPause == true) {
        log.warn "$app.label routine has been activated by the parent"
        state.allPause = true
        log.warn "state.allPause = $state.allPause"
    }
}
    
/************************************************************************************************************
	Base Process
************************************************************************************************************/
def installed() {
	log.debug "Installed with settings: ${settings}"
    state?.isInstalled = true
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
	log.info "The parent pause toggle is $parent.aPause and this routines pause toggle is $rPause"
//    if (rPause == false) { unschedule() }
//    if (rPause == true) {
    
    // Misc Variables
    state.tSelfHandlerEvt = null

	//ONCE A DAY CHECK VARIABLES
    state.cycleTh = false //temperature
    state.cycleTl = false
    state.cycleHh = false //humidity
    state.cycleHl = false    
    state.cycleWh = false //wind
    state.cycleWl = false    
    state.cycleLl = false //lux
    state.cycleLh = false
    state.cycleCO2h = false //CO2
    state.cycleCO2l = false    
	state.rainStart = true //rain
    state.rainStop = false
    
	//PENDING CANCEL VARIABLES
    state.tSelfOnFlag = true
	state.pendCancelFlag = true
    state.pendCancelOnFlag = true
    state.pendRout1CancelOnFlag = true
    state.pendRout2CancelOnFlag = true
    state.aSwitchesOnCancelFlag = true
    state.aSwitchesOffCancelFlag = true
    state.aSwitchesOnFlag = false
    state.aSwitchesOffFlag = false
    state.allDelayFlag = true
    
    //LOCATION & SCHEDULING
    subscribe(location, "alarmSystemStatus",shmModeChange)
    if (tMode) 									subscribe (location, processModeChange) 
    if (tRoutine)								subscribe(location, "routineExecuted", routineHandler)
    if (frequency) 								cronHandler(frequency)
    if (txFutureTime) 							oneTimeHandler()
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
    


//	else if (rPause == false) {
//    	log.info "This routine has been paused by the user"
//        }
//}

/************************************************************************************************************
	ROUTING METHOD
************************************************************************************************************/
def routingMethod(evt) {
	if (parent.debug) log.info "Routing Method has been activated"
	if (conditionHandler()==true){ // && getTimeOk()==true) {
	  state.eDev = "$evt.displayName"
    state.eAct = "$evt.value"
    
    if (parent.trace) log.trace "Routing Method Rcvd ==> evtValue == $evt.value && evt.displayName == $evt.displayName && state.stRoutEvent == $state.stRoutEvent"
	if ("${evt?.displayName}" == "${state.stRoutEvent}" || "${evt?.value}" == "${tContactCmd}" || "${evt?.value}" == "${tContactWindowCmd}" ||
        	"${evt?.value}" == "${tContactDoorCmd}" || "${evt?.value}" == "${tSwitchCmd}" || "${evt?.value}" == "${tContactCmd}" ||
        	"${evt?.value}" == "${tLocksCmd}" || "${evt?.value}" == "${tMotionCmd}" || "${evt?.value}" == "${tPresenceCmd}" ||
            "${evt?.value}" == "${tDimCmd}" || "${evt?.value}" == "${tGarageCmd}" || "${evt?.value}" == "${tKeyCode}") { 
		if (parent.trace) log.trace "The event is being routed to Devices Begin methods for actions processing"
        devicesBeginActions(evt)
        }

        if ("${evt?.displayName}" != "${state.stRoutEvent}" && "${evt?.value}" != "${tContactCmd}" && "${evt?.value}" != "${tContactWindowCmd}" &&
        	"${evt?.value}" != "${tContactDoorCmd}" && "${evt?.value}" != "${tSwitchCmd}" && "${evt?.value}" != "${tContactCmd}" &&
        	"${evt?.value}" != "${tLocksCmd}" && "${evt?.value}" != "${tMotionCmd}" && "${evt?.value}" != "${tPresenceCmd}" &&
            "${evt?.value}" != "${tDimCmd}" && "${evt?.value}" != "${tGarageCmd}" && "${evt?.value}" != "${tKeyCode}") { 
		if (parent.trace) log.trace "The event is being routed to Devices End method to cancel pending events"
		devicesEndActions(evt)
        }
    }
} 

/************************************************************************************************************
	DEVICES BEGINS METHOD
************************************************************************************************************/
def devicesBeginActions(evt) {
	if (parent.debug) log.info "Devices Begin Actions method activated"
    if (conditionHandler()==true){// && getTimeOk()==true) {
        def result 
        def devList = []
        def cDevList = []
        def sDevList = []
        def lDevList = []
        def cdDevList = []
        def cwDevList = []
        def pDevList = []
        def tMotionSize = tMotion?.size()
        def tContactSize = tContact?.size()
        def tSwitchSize = tSwitch?.size()
        def tLocksSize = tLocks?.size()
        def tContactDoorSize = tContactDoor?.size()
        def tContactWindowSize = tContactWindow?.size()
        def tPresenceSize = tPresence?.size()
		state.tSelfOnFlag = true
        
        if ("${evt?.value}" == "${state.stRoutEvent}" || "${evt?.value}" == "${tContactCmd}" || "${evt?.value}" == "${tContactWindowCmd}" ||
            "${evt?.value}" == "${tContactDoorCmd}" || "${evt?.value}" == "${tSwitchCmd}" || "${evt?.value}" == "${tPresenceCmd}" ||
            "${evt?.value}" == "${tLocksCmd}" || "${evt?.value}" == "${tMotionCmd}" || "${evt?.value}" == "${tDimCmd}" ||  
            "${evt?.value}" == "${tKeyCode}") { 
		
		// SWITCHES
            if(tSwitch) {
                if (parent.trace) log.trace "Devices Begin Actions: Switches events method activated"
                tSwitch.each { deviceName ->
                    def status = deviceName.currentValue("switch")
                    if (status == "${tSwitchCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tSwitchAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tSwitchAll) {
                    if ("${devListSize}" == "${tSwitchSize}") { 
                        processActions(evt) 
                    }
                }
            }

            // MOTION SENSORS
            if(tMotion) {
                if (parent.trace) log.trace "Devices Begin Actions: Motion events method activated"
                tMotion.each { deviceName ->
                    def status = deviceName.currentValue("motion")
                    if (status == "active"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                    if (status == "inactive"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }  
                def devListSize = devList?.size()
                if(!tMotionAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tMotionAll) {
                    if ("${devListSize}" == "${tMotionSize}") { 
                        processActions(evt)  
                    }
                }
            }    

            // PRESENCE SENSORS
            if(tPresence) {
                if (parent.trace) log.trace "Devices Begin Actions: Presence events method activated"
                tPresence.each { deviceName ->
                    def status = deviceName.currentValue("presence")
                    if (status == "${tPresenceCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tPresenceAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tPresenceAll) {
                    if ("${devListSize}" == "${tPresenceSize}") { 
                        processActions(evt)  
                    }
                }
            }

            // CONTACT SENSORS
            if(tContact) {
                if (parent.trace) log.trace "Devices Begin Actions: Contacts events method activated"
                tContact.each { deviceName ->
                    def status = deviceName.currentValue("contact")
                    if (status == "${tContactCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tContactAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }
                if(tContactAll) {
                    if ("${devListSize}" == "${tContactSize}") { 
                        processActions(evt)  
                    }
                }
            }

            // DOOR CONTACT SENSORS
            if(tContactDoor) {
                if (parent.trace) log.trace "Devices Begin Actions: Door Contacts events method activated"
                tContactDoor.each { deviceName ->
                    def status = deviceName.currentValue("contact")
                    if (status == "${tContactDoorCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tContactDoorAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tContactDoorAll) {
                    def tcontactSize = tcontact.size()
                    if ("${devListSize}" == "${tcontactSize}") { 
                        processActions(evt)  
                    }
                }
            }

            // WINDOW CONTACT SENSORS
            if(tContactWindow) {
                if (parent.trace) log.trace "Devices Begin Actions: Window Contacts events method activated"
                tContactWindow.each { deviceName ->
                    def status = deviceName.currentValue("contact")
                    if (status == "${tContactWindowCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tContactWindowAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tContactWindowAll) {
                    if ("${devListSize}" == "${tContactWindowSize}") { 
                        processActions(evt)  
                    }
                }
            }

            // LOCKS
            if(tLocks) {
                if (parent.trace) log.trace "Devices Begin Actions: Locks events method activated"
                tLocks.each { deviceName ->
                    def status = deviceName.currentValue("lock")
                    if (status == "${tLocksCmd}"){ 
                        String device  = (String) deviceName
                        devList += device
                    }
                }
                def devListSize = devList?.size()
                if(!tLocksAll) {
                    if (devList?.size() > 0) { 
                        processActions(evt)  
                    }
                }        
                if(tLocksAll) {
                    if ("${devListSize}" == "${tLocksSize}") { 
                        processActions(evt) 
                	}
                }
            }	
        }
    }
}

/************************************************************************************************************
	DEVICES ENDS ACTIONS METHOD
************************************************************************************************************/
def devicesEndActions(evt) {
    if (conditionHandler()==true){// && getTimeOk()==true) {

        if (parent.debug) log.info "Devices End Actions method activated." 

        if ("${evt?.value}" != "${tContactCmd}" && "${evt?.value}" != "${tContactWindowCmd}" &&
            "${evt?.value}" != "${tContactDoorCmd}" && "${evt?.value}" != "${tSwitchCmd}" && "${evt?.value}" != "${tContactCmd}" &&
            "${evt?.value}" != "${tLocksCmd}" && "${evt?.value}" != "${tMotionCmd}" && "${evt?.value}" != "${tPresenceCmd}") { 

            if (parent.trace) log.trace "The event device changed and any pending schedules will not execute."
			state.tSelfOnFlag = false
	// Pending On & Pending Off with Cancellation triggered from Motion ONLY
    if(tMotion) {
    	if (parent.trace) log.trace "Devices End Actions: Motion events method activated"
    	def devList = []
        if ("${evt.value}" == "inactive") {
		tMotion.each { deviceName ->
        def status = deviceName.currentValue("motion")
            if (status == "inactive"){ 
                String device  = (String) deviceName
                devList += device 
                }
            }
        def devListSize = devList?.size()
        def tMotionSize = tMotion.size()
            if ("${devListSize}" == "${tMotionSize}") { 
            	if (pendAll == true) {
                state.pendCancelOnFlag = false
                runIn(pendDelayOff, pendSwitchesOffHandler) 
                }
            }    
            if (!pendAll) {
            pendSwitchesOffHandler()
            }
        }
    }    

            if(ContactOff) {
            	aSwitchesContact(evt)
                }
            if (aSwitchesOn) {
                state.aSwitchesOnCancelFlag = false
                runIn(aSwitchesOnDelay, momentaryDeviceHandlerOn)
            }
            if (aSwitchesOff) {
                state.aSwitchesOffCancelFlag = false
                runIn(aSwitchesOffDelay, momentaryDeviceHandlerOff)
            }
            if (aRout1) {
                state.pendRout1CancelOnFlag = false
                unschedule(aRout1Handler) }

            if (aRout2) {
                state.pendRout2CancelOnFlag = false
                unschedule(aRout2Handler) }
        }
    }
}


/***********************************************************************************************************************
	PROCESS ACTIONS HANDLER - THIS MAKES THE STUFF HAPPEN
***********************************************************************************************************************/
def processActions(evt){
	if (!parent.debug) log.info "LogicRulz: Your User Friendly Rule Engine, The Developers of LogicRulz appreciate that you have chosen it to satisfy your SmartThings needs. \n" +
    "If you have any questions, concerns, or issues, please contact the Developers on the ST community forum at  https://community.smartthings.com"
    
    if (parent.aPause == false) {
        log.warn "All Logic Blocks have been paused by the parent app"
    }
    if (parent.debug) log.info "Process Actions Method activated."
    if (conditionHandler()==true){ // && getTimeOk()==true) {


//sendLocationEvent(name:"RemindR",value:app.label,isStateChange:true,displayed:false,data:data)

        if (remindRProfile) {
    		sendToRemindR(evt)
            }
            
		def result 
        def devList = []
        def aSwitchSize = aSwitch?.size()

        // SET DIMMERS BY LEVEL
        if (modeDimmers) { dimModes() }

		// OTHER SWITCHES
        if (aOtherSwitches) {
            if (aOtherSwitchesCmd == "on") {aOtherSwitches?.on()}
            if (aOtherSwitchesCmd == "off") {aOtherSwitches?.off()}
            if (aOtherSwitchesCmd == "toggle") {toggle2()}
        }
        if (aOtherSwitches2) {
            if (aOtherSwitchesCmd2 == "on") {aOtherSwitches2?.on()}
            if (aOtherSwitchesCmd2 == "off") {aOtherSwitches2?.off()}
            if (aOtherSwitchesCmd2 == "toggle") {toggle3()}
        }

        // DIMMERS
        if (aDim) {
        	runIn(aDimDelay, dimmersHandler)
            }
        if (aOtherDim) { 
        	runIn(otherDimDelay, otherDimmersHandler)
            }

		// COLOR LIGHTS
		if (aHues) {
        	runIn(aHuesColorDelay, HuesColorHandler) 
            }
        if (aHuesOther) {
        	runIn(aHuesOtherColorDelay, HuesOtherColorHandler)
            }
        
        // DELAY ON/OFF SWITCHES
        if (aSwitchesOn) { 
            state.aSwitchesOnFlag == true
            //evt?.value = "on"
            aSwitchesContact(evt) 
        }
        if (aSwitchesOff) {
            state.aSwitchesOffFlag == true
            //"${evt.value}" = "off"
            aSwitchesContact(evt) 
        }

        // CEILING FANS
        if (aCeilingFans) {
            if (aCeilingFansCmd == "on") {aCeilingFans.on()}
            else if (aCeilingFansCmd == "off") {aCeilingFans.off()}
            else if (aCeilingFansCmd == "low") {aCeilingFans.setLevel(33)}
            else if (aCeilingFansCmd == "med") {aCeilingFans.setLevel(66)}
            else if (aCeilingFansCmd == "high") {aCeilingFans.setLevel(99)}
            if (aCeilingFansCmd == "incr" && aCeilingFans) {
                def newLevel
                aCeilingFans?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aCeilingFansIncr
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aCeilingFansCmd == "decr" && aCeilingFans) {
                def newLevel
                aCeilingFans?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aCeilingFansDecr
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
        // FANS
        if (aFansCmd == "on") { 
            runIn(aFansDelayOn, aFansOn) }
        if (aFansCmd == "off") {
            runIn(aFansDelayOff, aFansOff) }

        // LOCKS
        if (aLocksCmd == "lock") {
            runIn(aLocksDelayLock, aLocksOpen) }  
        if (aLocksCmd == "unlock") { 
            runIn(aLocksDelayUnlock, aLocksClose) }

        // GARAGE DOORS
        if (aDoorCmd == "open") { 
            runIn(aDoorDelayOpen, aDoorOpen) }
        if (aDoorCmd == "close") { 
            runIn(aDoorDelayClose, aDoorClose) }

        // VIRTUAL PRESENCE
        if (aPresence) {
            if (aPresenceCmd == "arrived") { aPresence.arrived() }
            else if (aPresenceCmd == "departed") { aPresence.departed() }
        }

        // VENTS
        if (aVents) {
            if (sVentsCmd == "on") {aVents.setLevel(100)}
            else if (aVentsCmd == "off") {aVents.off()}
            else if (aVentsCmd == "25") {aVents.setLevel(25)}
            else if (aVentsCmd == "50") {aVents.setLevel(50)}
            else if (aVentsCmd == "75") {aVents.setLevel(75)}
        }

        // WINDOW COVERINGS
        if (aShades) {
            if (aShadesCmd == "open") {aShades.setLevel(100)}
            else if (aShadesCmd == "close") {aShades.setLevel(0)}
            else if (aShadesCmd == "25") {aShades.setLevel(25)}
            else if (aShadesCmd == "50") {aShades.setLevel(50)}
            else if (aShadesCmd == "75") {aShades.setLevel(75)}
        }

        // SMART HOME MONITOR
        if (aSHM == "away") { def data = 3 
                             sendSHMEvent("away", data) }
        if (aSHM == "home") { def data = 1 
                             sendSHMEvent("stay", data) }
        if (aSHM == "disarmed") { def data = 0 
                                 sendSHMEvent("off", data) }

        // THERMOSTATS
        if (cTstat) { thermostats() }
        if (cTstat1) { thermostats1() }

        // MODE
        if (aMode) { setLocationMode(aMode) }

        // EXECUTING ACTIONS FOR OTHER LOGIC BLOCKS
            if (tSelf) {
            	if (tSelfPend) {
                	runIn(tSelfDelay, tSelfHandler)
                    }
                if (!tSelfPend) {
                	runIn(tSelfDelay, tSelfHandler)
                    }
                }    
            

        // SMS/PUSH/AUDIO
		if (message != null) {
        	def Message = runProfile(message, evt)
            ttsActions(Message) }
            
        if (aSwitches) {
            aSwitchesContact(evt)
        }
        
        if (smc) {
        	sendToSMC(evt)
            }
        
		if (aPendSwitches) {
        	if (evt.value == "${tMotionCmd}") {
            state.pendCancelOnFlag = true
            if (pendDelayOn != null) { 
                runIn(pendDelayOn, pendSwitchesOnHandler) 
            } else { 
                runIn(pendDelayOn, pendSwitchesOnHandler) 
            	}
            }
        }    

        if (aSwitchesOn) {
            if (!aSwitchesOnPend) { 
                runIn(aSwitchesOnDelay, momentaryDeviceHandlerOn) 
            }
            if (aSwitchesOnPend) { 
                state.aSwitchesOnCancelFlag = true
                runIn(aSwitchesOnDelay, momentaryDeviceHandlerOn)
            }
        }
        if (aSwitchesOff) {
            if (!aSwitchesOffPend) { 
                runIn(aSwitchesOffDelay, momentaryDeviceHandlerOff) 
            }
            if (aSwitchesOffPend) { 
                state.aSwitchesOffCancelFlag = true
                runIn(aSwitchesOffDelay, momentaryDeviceHandlerOff)
            }
        }
        if (aRout1) {
        	state.pendRout1CancelOnFlag = true
            runIn(aRoutDelay1, aRout1Handler)
            }
        if (aRout2) {
        	state.pendRout2CancelOnFlag = true
            runIn(aRoutDelay2, aRout2Handler)
            }
        }    
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

// INDIVIDUAL DEVICE TYPE HANDLERS FOLLOW

/******************************************************************************************************
   SEND TO REMINDR
******************************************************************************************************/
def sendToRemindR(evt) {
	def execBlock = app.label
    def profile = remindRProfile
    def device = evt.displayName
    def action = evt.value
    def expire = expiration ? expiration * 60 : 0
	//def del= expire * 60
    def data = [:]
    log.debug "sending to RemindR: $profile"
    sendLocationEvent(name: "LogicRulz", value: runReport, isStateChange: true, data: [device: device, action: action], descriptionText: profile)
    log.warn "RemindR Profile event details: name = LogicRulz, device = $evt.displayName, value = $evt.value, descriptionText = $evt.descriptionText, " +
    "RemindR Profile to Execute = $profile"
}

/******************************************************************************************************
   SEND TO SMART MESSAGE CONTROL
******************************************************************************************************/
def sendToSMC(evt) {
	def message = runProfile(message, evt)
    def value = "${app.label}"
    log.debug "Sending this message to SMC: $message"
			sendLocationEvent(name: "SmartMessaging", value: "$value", isStateChange: true, descriptionText: "${message}")
			}
        
/***********************************************************************************************************************
    Logic Blocks being executed after a delay
***********************************************************************************************************************/
def tSelfHandler(evt) {
	if (parent.trace) log.trace "preparing to execute a logic block"
    if (tSelf) {
    	def children = parent.getChildApps()
        children.each {child ->
            if (tSelf.contains(child.label) && state.tSelfOnFlag == true) {
            if (conditionHandler()==true && getTimeOk()==true) {
            if (parent.trace) log.trace "executing logic block $child.label"
                child.processActions(evt)
            	}
            }
        }
    } 
}

/***********************************************************************************************************************
    ST ROUTINES TRIGGERS - ST ROUTINE EXECUTES, RUNNING THIS METHOD
***********************************************************************************************************************/
def routineHandler(evt) {
    def eDisplayN = evt.displayName
    def eName = evt.name
    if (parent.debug) log.info "The ST Routines Execution Events Handler has been activated"

    if(eName == "routineExecuted" && tRoutine) {
            tRoutine?.find {r -> 
                def rMatch = r.replaceAll("[^a-zA-Z0-9 ]", "")
                if (parent.trace) log.trace " r = $r && eDisplayN = $eDisplayN"
				if(r == eDisplayN){
                state.stRoutEvent = "${eDisplayN}"
                     processActions(evt)
            }
        }
    }
}

/***********************************************************************************************************************
    HUES COLOR BULB HANDLER
***********************************************************************************************************************/
def HuesColorHandler() {
	if (parent.debug) log.info "Hues Color Handler activated"
	if (aHuesCmd == "on") { aHues.on() }
    if (aHuesCmd == "off") { aHues.off() }
    if (aHuesCmd == "setColor") { processColor() }
}

def HuesOtherColorHandler() {
	if (parent.debug) log.info "Hues Other Color Handler activated"
    if (aHuesOtherCmd == "on") { aHuesOther?.on() }
    if (aHuesOtherCmd == "off") { aHuesOther?.off() }
	if (aHuesOtherCmd == "setColor") { processColor() }
}    

/***********************************************************************************************************************
    DIMMERS HANDLER
***********************************************************************************************************************/
def dimmersHandler() {
	if (parent.debug) log.info "Dimmers Handler activated"
		if (aDim) {
            if (aDimCmd == "on") {aDim.on()}
            else if (aDimCmd == "off") {aDim.off()}
            if (aDimCmd == "set" && aDim) {
                def level = aDimLVL < 0 || !aDimLVL ?  0 : aDimLVL >100 ? 100 : aDimLVL as int
                    aDim.setLevel(level)
            }
            if (aDimCmd == "increase" && aDim) {
                def newLevel
                aDim?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimIncrease
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aDimCmd == "decrease" && aDim) {
                def newLevel
                aDim?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimDecrease
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
    }

/***********************************************************************************************************************
    OTHER DIMMERS HANDLER
***********************************************************************************************************************/
def otherDimmersHandler() {
	if (parent.debug) log.info "Other Dimmers Handler activated"
        if (aOtherDim) {
            if (aOtherDimCmd == "on") {aOtherDim.on()}
            else if (aOtherDimCmd == "off") {aOtherDim.off()}
            if (aOtherDimCmd == "set" && aOtherDim) {
                def otherLevel = aOtherDimLVL < 0 || !aOtherDimLVL ?  0 : aOtherDimLVL >100 ? 100 : aOtherDimLVL as int
                    aOtherDim?.setLevel(otherLevel)
            }
            if (aOtherDimCmd == "increase" && aOtherDim) {
                def newLevel
                aOtherDim.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimIncrease
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aOtherDimCmd == "decrease" && aOtherDim) {
                def newLevel
                aOtherDimCmd?.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimDecrease
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
	}
    
/***********************************************************************************************************************
    CO2 HANDLER
***********************************************************************************************************************/
def CO2Handler(evt){
    if (parent.debug) log.info "CO2 Handler activated"
    def data = [:]
    def eVal = evt.value
    eVal = eVal as int
        def eName = evt.name
        def eDev = evt.device
        def eDisplayN = evt.displayName
        def CO2AVG = myCO2 ? getAverage(myCO2, "carbonDioxide") : "undefined device"
    if (parent.trace) log.trace "The CO2 average is: $CO2AVG"
    if(eDisplayN == null) eDisplayN = eDev 
    if(myCO2S == "above"){
        if (CO2AVG >= CO2) {
            if (CO2Once) {
                if (conditionHandler()==true && getTimeOk()==true) {
                    if (state.cycleCO2h == false){
                        state.cycleCO2h = true            
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetCO2Variables)
                    }
                }
            }    
            processActions(evt)    
        }
    }    
    if(myCO2S == "below"){
        if (CO2AVG <= CO2 || "${CO2AVG}" <= "${CO2}" ) {
            if (CO2Once) {
                if (conditionHandler()==true && getTimeOk()==true) {
                    if (state.cycleCO2l == false){
                        state.cycleCO2l = true                
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetCO2Variables)
                    }
                }
            }    
            else processActions(evt)    
        }
    }
}

/***********************************************************************************************************************
    WIND HANDLER 
***********************************************************************************************************************/
def windHandler(evt){
	if (parent.debug) log.info "Wind Handler activated"
    def data = [:]
    def eVal = evt.value
    def eName = evt.name
    def eDev = evt.device
    def eDisplayN = evt.displayName
    if(eDisplayN == null) eDisplayN = eDev 
    //if(parent) log.info "event received: event = $evt.descriptionText, eVal = $eVal, eName = $eName, eDev = $eDev, eDisplayN = $eDisplayN"
    if(tWindLevel == "above"){
        if ("${eVal}" >= 0 || "${eVal}" >= 0.00 || "${eVal}" >= 0.000 || "${eVal}" >= 0.0000) { // tWindSpeed) {
            if (windOnce) {
                if (conditionHandler()==true && getTimeOk()==true) {
                    if (state.cycleWh == false){
                        state.cycleWh = true            
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetWindVariables)
                    }
                }
            }    
            processActions(evt)    
        }
    }    
    else {
        if(tWindLevel == "below"){
            if ("${eVal}" <= tWindSpeed) {
                if (windOnce) {
                    if (conditionHandler()==true && getTimeOk()==true) {
                        if (state.cycleWl == false){
                            state.cycleWl = true                
                            processActions(evt)
                            schedule("0 0 0 * * ?",resetWindVariables)
                        }
                    }
                }    
                else processActions(evt)    
            }
        }
    }
}

/***********************************************************************************************************************
    HUMIDITY HANDLER - Code credit goes to Bobby Dobrescu and RemindR
***********************************************************************************************************************/
def humidityHandler(evt){
	if (parent.debug) log.info "Humidity Handler activated"
    def data = [:]
    def eVal = evt.value
    eVal = eVal as int
        def eName = evt.name
        def eDev = evt.device
        def eDisplayN = evt.displayName
        if(eDisplayN == null) eDisplayN = eDev 
        if(tHumidityLevel == "above"){
            if (eVal >= tHumidityPercent) {
                if (humOnce) {
                    if (conditionHandler()==true && getTimeOk()==true) {
                        if (state.cycleHh == false){
                            state.cycleHh = true            
                            processActions(evt)
                            schedule("0 0 0 * * ?",resetHumVariables)
                        }
                    }
                }    
                processActions(evt)    
            }
        }    
    else {
        if(tHumidityLevel == "below"){
            if (eVal <= tHumidityPercent) {
                if (humOnce) {
                    if (conditionHandler()==true && getTimeOk()==true) {
                        if (state.cycleHl == false){
                            state.cycleHl = true                
                            processActions(evt)
                            schedule("0 0 0 * * ?",resetHumVariables)
                        }
                    }
                }    
                else processActions(evt)    
            }
        }
    }
}

/***********************************************************************************************************************
    TEMPERATURE HANDLER
***********************************************************************************************************************/
def tempHandler(evt) {
	if (parent.debug) log.info "Temperature Handler activated"
    def data = [:]
    def eVal = evt.value
    def eName = evt.name
    def eDev = evt.device
    def eDisplayN = evt.displayName
    if(eDisplayN == null) eDisplayN = eDev 
    def tempAVG = tTemperature ? getAverage(tTemperature, "temperature") : "undefined device"          
    def cycleThigh = state.cycleTh
    def cycleTlow = state.cycleTl        
    def currentTemp = tempAVG

    if(tTemperature) {
 		if(tTempRead=="between") {
        if (currentTemp >= tempLow && currentTemp <= tempHigh) {
            if (tempOnce) {
                if (cycleThigh == false){
                    if (conditionHandler()==true && getTimeOk()==true) {
                        state.cycleTh = true
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetTempVariables)
                    }
                }
            }    
            else processActions(evt)
        	}
        }
 		if(tTempRead=="above") {
        if (currentTemp > tempHigh) {
            if (tempOnce) {
                if (cycleThigh == false){
                    if (conditionHandler()==true && getTimeOk()==true) {
                        state.cycleTh = true
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetTempVariables)
                    }
                }
            }    
            else processActions(evt)
        	}
        }
 		if(tTempRead=="below") {
        if (currentTemp < tempLow) {
            if (tempOnce) {
                if (cycleThigh == false){
                    if (conditionHandler()==true && getTimeOk()==true) {
                        state.cycleTh = true
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetTempVariables)
                    }
                }
            }    
            else processActions(evt)
        	}
        }
    }
}

/***********************************************************************************************************************
    RAIN HANDLER
***********************************************************************************************************************/
def rainHandler(evt) {
    if (parent.debug) log.info "Rain Handler activated"
    def data = [:]
    def eVal = evt.value
    def eName = evt.name
    def eDev = evt.device
    def eDisplayN = evt.displayName
    if(eDisplayN == null) eDisplayN = eDev 
    //if(parent.trace) log.trace "event received: event = $evt.descriptionText, eVal = $eVal, eName = $eName, eDev = $eDev, eDisplayN = $eDisplayN"
    if(tRain) {
        if (tRainAction == "begins") {
            if ("${eVal}" > 0) { 
                if (conditionHandler()==true && getTimeOk()==true) {
                    if (state.rainStart == true && state.rainStop == false) {
                        state.rainStart = false
                        state.rainStop = true
                        def Message = "${rainStartMsg}"
                        if (parent.trace ) log.trace "the rainStart message is: $Message"
                        processActions(evt)
                        ttsActions(Message)
                        schedule("0 0 0 * * ?",resetRainVariables)
                    }
                }
            }
        }
        if (tRainAction == "ends") {
            if ("${eVal}" == 00.00 || "${eVal}" == 0 || "${eVal}" == 0.0 || "${eVal}" == 0.000 || "${eVal}" == 0.0000 || "${eVal}" == 0.00000) {
                if (state.rainStart == false && state.rainStop == true) {
                    if(parent.trace) log.trace "sending notification (Rain Ended): as rain has ended" 
                    state.rainStart = true
                    state.rainStop = false
                    def Message = "${rainStopMsg}"
                    if (parent.trace ) log.trace "the rainstop message is: $Message"
                    processActions(evt)
                    ttsActions(Message)
                    schedule("0 0 0 * * ?",resetRainVariables)
                }
            }
        }
        if (tRainAction == "begins and ends") {
            if ("${eVal}" > 0) { 
                if (conditionHandler()==true && getTimeOk()==true) {
                    if (state.rainStart == true && state.rainStop == false) {
                        if(parent.trace) log.trace "sending notification (/both/Rain Started): as rain has begun" 
                        def Message = "${rainStartMsg}"
                        if (parent.trace ) log.trace "the rainStart message is: $Message"
                        processActions(evt)
                        ttsActions(Message)
                        schedule("0 0 0 * * ?",resetRainVariables)
                        state.rainStart = false
                        state.rainStop = true
                    }
                }
            }
            if ("${eVal}" == 0 || "${eVal}" == 0.0 || "${eVal}" == 0.000 || "${eVal}" == 0.0000 || "${eVal}" == 0.00000) {
                if (state.rainStart == false && state.rainStop == true) {
                    if(parent.trace) log.trace "sending notification (/both/Rain Ended): as rain has ended" 
                    def Message = "${rainStopMsg}"
                    if (parent.trace ) log.trace "the rainstop message is: $Message"
                    processActions(evt)
                    ttsActions(Message)
                    schedule("0 0 0 * * ?",resetRainVariables)
                    state.rainStart = true
                    state.rainStop = false
                }
            }
        }
    }
} 

/***********************************************************************************************************************
    LUX HANDLER
***********************************************************************************************************************/
def luxHandler(evt) {
    if (parent.debug) log.info "Lux Handler activated"
    def data = [:]
    def eVal = evt.value
    def eName = evt.name
    def eDev = evt.device
    def eDisplayN = evt.displayName
    if(eDisplayN == null) eDisplayN = eDev 
    def luxAVG = tLux ? getAverage(tLux, "illuminance") : "undefined device"          
    def cycleLhigh = state.cycleLh
    def cycleLlow = state.cycleLl        
    def currentLux = luxAVG
    
    if(tLux) {
        if (currentLux >= tLuxLow && currentLux <= tLuxHigh) { 
            if (luxOnce) {
                if (cycleLhigh == false){
                    if (conditionHandler()==true && getTimeOk()==true) {
                        state.cycleLh = true
                        if(parent.trace) log.trace "sending notification (above): as lux level $currentLux is between $luxLow and $luxHigh" 
                        processActions(evt)
                        schedule("0 0 0 * * ?",resetLuxVariables)
                    }
                }
            }    
            else processActions(evt)
        }
    }
}

/***********************************************************************************************************************
    DISCOVERS CHILD LIST
***********************************************************************************************************************/
def getRoutines(evt) {
    def childList = []
    parent.childApps.each {child ->
        def ch = child.label
        String children  = (String) ch
        childList += children
    }
    if (parent.trace) log.warn "finished looking and found: $childList"
    return childList
}

/*************************************************************************************************************
   DELAYED SWITCHES ON/OFF TRIGGERED BY CONTACT SENSORS FROM THE ASWITCHES SELECTIONS
************************************************************************************************************/
def aSwitchesContact(evt) {
    if (parent.debug) log.info "aSwitches Handler activated"
    if (aSwitches) { // Triggered only by motion, switches, or contacts
        if (aSwitchesCmd == "on" && ContactOff) {
            if (evt.value == "open" || evt.value == "active" || evt.value == "on") { 
                state.aSwitchesOnFlag = false
                aSwitches.on() 
            }
            if (evt.value == "closed" || evt.value == "inactive" || evt.value == "off") {
                state.aSwitchesOnFlag = true
                runIn(ContactOffDelay, delayedSwitches) 
            }
        }
        if (aSwitchesCmd == "on" && !ContactOff) {
            aSwitches.on()
        }

        if (aSwitchesCmd == "off" && ContactOff) {
            if (evt.value == "open" || evt.value == "active" || evt.value == "on") { aSwitches.off() }
            if (evt.value == "closed" || evt.value == "inactive" || evt.value == "off") { 
                state.aSwitchesOffFlag = false
                runIn(ContactOnDelay, delayedSwitches) 
            }
        }
        if (aSwitchesCmd == "off" && !ContactOff) {
            aSwitches.off()
        }
        if (aSwitchesCmd == "toggle") { toggle1() }
    }
}    

def delayedSwitches(evt) {
    if (parent.debug) log.info "Delayed Switches Handler activated"
    if (parent.debug) log.info "delayedSwitches w/contacts method activated"
    if (aSwitchesCancel) {
        if (aSwitchesCmd == "on" && state.aSwitchesOnFlag == true) {
            state.aSwitchesOnFlag = false
            aSwitches.off()
        }
        if (aSwitchesCmd == "on" && state.aSwitchesOnFlag == false) { 
            state.aSwitchesOnFlag = true
            unschedule(delayedSwitches)
        }
        if (aSwitchesCmd == "off") { 
            if (state.aSwitchesOffFlag == false) {
                state.aSwitchesOffFlag = true
                aSwitches.on() }
            if (state.aSwitchesOffFlag == true) {
                state.aSwitchesOffFlag == false
                unschedule(delayedSwitches) 
            }
        }
    }    
    if (!aSwitchesCancel) {
        if (aSwitchesCmd == "on") { 
            aSwitches.off() 
        }
        if (aSwitchesCmd == "off") {
            aSwitches.on()
        }
    } 
}    

/******************************************************************************
	 RESETS VARIABLES FOR ONE-TIME EXECUTIONS										
******************************************************************************/
def resetWindVariables() {
	state.cycleWl = false
    state.cycleWh = false
    }
def resetTempVariables() {    
    state.cycleTl = false
    state.cycleTh = false
    }
def resetHumVariables() {
	state.cycleHl = false
    state.cycleHh = false
    }
def resetLuxVariables() {
	state.cycleLl = false
    state.cycleLh = false
    }
def resetCO2Variables() {    
	state.cycleCO2l = false
    state.cycleCO2h = false
    }
def resetRainVariables() {
	state.rainStart = true
    state.rainStop = false
    }
    
/******************************************************************************
	 FEEDBACK SUPPORT - GET AVERAGE										
******************************************************************************/
def getAverage(device,type){
	if (parent.debug) log.info "Get Average readings handler activated"
    def result
    def total = 0
    device.each {total += it.latestValue(type)}
    result = (total/device?.size())
    return result
}
  
/******************************************************************************************************
ST ROUTINE EXECUTE - good
******************************************************************************************************/
def scheduledSTRoutine1(evt) {
    if (parent.debug) log.info "ST Routine1 is executing"
    state.stRoutAction = "${aRout1}" 
    def message = str1Msg
    def Message = runProfile(message, evt)
    if (str1Msg != null) {
    	ttsActions(Message)
    }
    location.helloHome?.execute(aRout1)
}
def scheduledSTRoutine2(evt) {
    if (parent.debug) log.info "ST Routine2 is executing"
    state.stRoutAction = "${aRout2}"
    def message = str2Msg
    def Message = runProfile(message, evt)
    if (str2Msg != null) {
    	ttsActions(Message)
    }
    location.helloHome?.execute(aRout2)
}

/******************************************************************************************************
SPEECH AND TEXT ACTION
******************************************************************************************************/
def ttsActions(Message) {
	if (parent.debug) log.info "TTS Actions Handler activated"
    if (parent.aPause == true) {
        if (parent.trace) log.trace "ttsactions have been called by: $Message"
        def tts = Message
        if (speakDisable == true) {
            if (echoDevice) {
            	settings.echoDevice.each { spk->
                		spk.speak(Message)
				}
            }     
           	if (synthDevice) {
            	settings?.synthDevice?.each { spk->
                	spk?.speak(Message)
                    }
            }
            if (tts) {
                state.sound = textToSpeech(tts instanceof List ? tts[9] : tts)
            }
            else {
                state.sound = textToSpeech("You selected the custom message option but did not enter a message in the $app.label Smart App")
            }
            if (sonosDevice){ 
                def currVolLevel = sonosDevice.latestValue("level")
                def currMuteOn = sonosDevice.latestValue("mute").contains("muted")
                if (currMuteOn) { 
                    sonosDevice.unmute()
                }
                def sVolume = settings.volume ?: 20
                sonosDevice?.playTrackAndResume(state.sound.uri, state.sound.duration, sVolume)
            }
        }
        if(recipients || sms){				
            sendtxt(tts)
        }
        if (push) {
            sendPushMessage(tts)
        }	
        state.lastMessage = tts
        return
    }
}

/***********************************************************************************************************************
	SMS HANDLER
***********************************************************************************************************************/
private void sendtxt(tts) {
    if (parent.debug) log.info "Send Text method activated."
    if (sendContactText) { 
        sendNotificationToContacts(tts, recipients)
        if (push || shmNotification) { 
            sendPushMessage
        }
    } 
    if (notify) {
        sendNotificationEvent(tts)
    }
    if (sms) {
        sendText(sms, tts)
    }
    if (psms) {
        processpsms(psms, tts)
    }
}

private void sendText(number, tts) {
    if (sms) {
        def phones = sms.split("\\,")
        for (phone in phones) {
            sendSms(phone, tts)
        }
    }
}
private void processpsms(psms, tts) {
    if (psms) {
        def phones = psms.split("\\,")
        for (phone in phones) {
            sendSms(phone, tts)
        }
    }
}

/******************************************************************************************************
MODE CHANGE HANDLER FOR TRIGGER 
******************************************************************************************************/
def processModeChange(evt){
    if (parent.debug) log.info "Mode Change Execution Event method is activated"
    def M = "${[evt.value]}"
    def S = "${tMode}"
    if (M == S) {
        processActions(evt)
    }
}  

/***********************************************************************************************************
   SMART HOME MONITOR SETTINGS HANDLER - good
************************************************************************************************************/
def shmModeChange(evt){
    if (parent.debug) log.info "shmModeChange method activated."
    if ("$evt.value" == tSHM) {
        processActions(evt) 
    }
}

private sendSHMEvent(shmState, data){
    def event = [name:"alarmSystemStatus", value: shmState, 
                 displayed: true, description: "System Status is ${shmState}"]
    sendLocationEvent(event)
    if (data == 0) {
        tKeypads?.each() { it.acknowledgeArmRequest(0) } 
        tKeypads?.each() { it.setDisarmed() }
        if (parent.trace) log.info "keypad updated to disarmed"
    }
    if (data == 1) {
        tKeypads?.each() { it.acknowledgeArmRequest(1) } 
        tKeypads?.each() { it.setArmedStay() } 
        if (parent.trace) log.info "keypad updated to Armed Stay"
    }
    if (data == 3) {
        tKeypads?.each() { it.acknowledgeArmRequest(3) } 
        tKeypads?.each() { it.setArmedAway() } 
        if (parent.trace) log.info "keypad updated to Armed Away"
    }
}    

/***********************************************************************************************************************
    CRON HANDLER - good
***********************************************************************************************************************/
def cronHandler(var) {
    if (parent.debug) log.info "cron handler method activated"
    def result
    if(var == "Minutes") {
        //	0 0/3 * 1/1 * ? *
        if(xMinutes) { result = "0 0/${xMinutes} * 1/1 * ? *"
                      schedule(result, "processActions")
                     }
        else log.error " unable to schedule your reminder due to missing required variables"
    }
    if(var == "Hourly") {
        //	0 0 0/6 1/1 * ? *
        if(xHours) { 
            result = "0 0 0/${xHours} 1/1 * ? *"
            schedule(result, "processActions")
        }
        else log.error " unable to schedule your reminder due to missing required variables"
    }
    if(var == "Daily") {
        // 0 0 1 1/7 * ? *
        def hrmn = hhmm(xDaysStarting, "HH:mm")
        def hr = hrmn[0..1] 
        def mn = hrmn[3..4]
        if(xDays && xDaysStarting) {
            result = "0 $mn $hr 1/${xDays} * ? *"
            schedule(result, "processActions")
        }
        else if(xDaysWeekDay && xDaysStarting) {
            //0 13 2 ? * MON-FRI *
            result = "0 $mn $hr ? * MON-FRI *"
            schedule(result, "processActions")
        }
        else log.error " unable to schedule your reminder due to missing required variables"
    }
    if(var == "Weekly") {
        // 	0 0 2 ? * TUE,SUN *
        def hrmn = hhmm(xWeeksStarting, "HH:mm")
        def hr = hrmn[0..1]
        def mn = hrmn[3..4]
        def weekDaysList = [] 
        xWeeks?.each {weekDaysList << it }
        def weekDays = weekDaysList.join(",")
        if(xWeeks && xWeeksStarting) { result = "0 $mn $hr ? * ${weekDays} *" }
        else log.error " unable to schedule your reminder due to missing required variables"
        schedule(result, "processActions")
    }
    if(var == "Monthly") { 
        // 0 30 5 6 1/2 ? *
        def hrmn = hhmm(xMonthsStarting, "HH:mm")
        def hr = hrmn[0..1]
        def mn = hrmn[3..4]
        if(xMonths && xMonthsDay) { result = "0 $mn $hr ${xMonthsDay} 1/${xMonths} ? *"}
        else log.error "unable to schedule your reminder due to missing required variables"
        schedule(result, "processActions")
    }
    if(var == "Yearly") {
        //0 0 4 1 4 ? *
        def hrmn = hhmm(xYearsStarting, "HH:mm")
        def hr = hrmn[0..1]
        def mn = hrmn[3..4]           
        if(xYears) {result = "0 $mn $hr ${xYearsDay} ${xYears} ? *"}
        else log.error "unable to schedule your reminder due to missing required variables"
        schedule(result, "processActions")
    }
    log.info "scheduled $var recurring event" 
}

/***********************************************************************************************************************
    ONE TIME SCHEDULING HANDLER - good
***********************************************************************************************************************/
def oneTimeHandler(var) {
	if (parent.debug) log.info "One Time Scheduling Handler activated"
    def result
    def todayYear = new Date(now()).format("yyyy")
    def todayMonth = new Date(now()).format("MM")
    def todayDay = new Date(now()).format("dd")
    def yyyy = txFutureYear ?: todayYear
    def MM = txFutureMonth ?: todayMonth
    def dd = txFutureDay ?: todayDay

    if(!txFutureDay) {
        runOnce(txFutureTime, processActions)
    }
    else{
        def timeSchedule = hhmmssZ(txFutureTime)
        result = "${yyyy}-${MM}-${dd}T${timeSchedule}" 
        Date date = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", result)
        runOnce(date, processActions)
    }
}
private hhmmssZ(time, fmt = "HH:mm:ss.SSSZ") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}

/************************************************************************************************************
THERMOSTATS HANDLERS
************************************************************************************************************/
private thermostats(evt) {
    if (parent.debug) log.info "thermostats handler method activated"
    cTstat.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstatMode == "off") { cTstat.off()
                                 }
        if (cTstatMode == "auto" || cTstatMode == "on") {
            cTstat.auto()
            cTstat.setCoolingSetpoint(coolLvl)
            cTstat.setHeatingSetpoint(heatLvl)
        }
        if (cTstatMode == "cool") {
            cTstat.cool()
            cTstat.setCoolingSetpoint(coolLvl)
        }
        if (cTstatMode == "heat") {
            cTstat.heat()
            cTstat.setHeatingSetpoint(heatLvl)
        }
        if (cTstatMode == "incr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = tempChange + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = tempChange + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatMode == "decr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = currentTMP - tempChange
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = currentTMP - tempChange
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatFan == "auto" || cTstatFan == "off") { cTstat.fanAuto() }
        if (cTstatFan == "on") { cTstat.fanOn() }
        if (cTstatFan == "circ") { cTstat.fanCirculate() }
    }
}
private thermostats1(evt) {
    cTstat1.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstat1Mode == "off") { cTstat1.off()
                                  }
        if (cTstat1Mode == "auto" || cTstat1Mode == "on") {
            cTstat1.auto()
            cTstat1.setCoolingSetpoint(coolLvl1)
            cTstat1.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1Mode == "auto" || cTstat1Mode == "on") {
            cTstat1.auto()
            cTstat1.setCoolingSetpoint(coolLvl1)
            cTstat1.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1Mode == "incr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = tempChange1 + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = tempChange1 + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTsta1tMode == "decr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = currentTMP - tempChange1
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = currentTMP - tempChange1
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstat1Fan == "auto" || cTstat1Fan == "off") { cTstat1.fanAuto() }
        if (cTstat1Fan == "on") { cTstat1.fanOn() }
        if (cTstat1Fan == "circ") { cTstat1.fanCirculate() }
    }
}

/************************************************************************************************************
TOGGLE SWITCHES HANDLER - good
************************************************************************************************************/
private toggle1(evt) {
    if (parent.debug) log.info "toggle1 method activated"
    aSwitches.each { deviceName ->
        def switchattr = deviceName.currentSwitch
        if (switchattr.contains("on")) {
            deviceName.off()
        }
        else {
            deviceName.on()
        }
    }		
}
private toggle2(evt) {
    if (parent.debug) log.info "toggle2 method activated"
    aOtherSwitches.each { deviceName ->
        def switchattr = deviceName.currentSwitch
        if (switchattr.contains('on')) {
            deviceName.off()
        }
        else {
            deviceName.on()
        }
    }		
}
private toggle3(evt) {
    if (parent.debug) log.info "toggle3 method activated"
    aOtherSwitches2.each { deviceName ->
        def switchattr = deviceName.currentSwitch
        if (switchattr.contains('on')) {
            deviceName.off()
        }
        else {
            deviceName.on()
        }
    }		
}

/************************************************************************************************************
Code Entry Handler 
************************************************************************************************************/
def codeEntryHandler(evt) {           //tKeyCode  tKeyButton  tKeypads  0=off 1=partial 3=on
    if (parent.debug) log.info "Keypad Code Entry Handler activated"
    def codeEntered = evt.value as String
    def data = evt.data as String
    def stamp = state.lastTime = new Date(now()).format("h:mm aa, dd-MMMM-yyyy", location.timeZone) 
    def exBtn

    if (data == '0') { exBtn = 'off' }
    if (data == '1') { exBtn = 'partial' }
    if (data == '3') { exBtn = 'on' }

    if ("${codeEntered}" == "${tKeyCode}") {
        if ("${tKeyButton}" == "${exBtn}") {
            pendSwitches(evt)
        }   	 
    }
}

/************************************************************************************************************
FANS Handler 
************************************************************************************************************/
def aFansOn(evt) {
    if (parent.debug) log.info "Fan device handler turn on activated"
    aFans?.on()
}
def aFansOff(evt) {
    if (parent.debug) log.info "Fan device handler turn off activated"
    aFans?.off()
}  

/************************************************************************************************************
LOCKS Handler 
************************************************************************************************************/
def aLocksOpen(evt) {
    if (parent.debug) log.info "lock device handler lock activated"
    aLocks?.lock()
}
def aLocksClose(evt) {
    if (parent.debug) log.info "lock device handler unlock activated"
    aLocks?.unlock()
}  

/************************************************************************************************************
Garage Door Handler 
************************************************************************************************************/
def aDoorOpen(evt) {
    if (parent.debug) log.info "garage door device handler open activated"
    aDoor?.open()
}
def aDoorClose(evt) {
    if (parent.debug) log.info "garage door device handler close activated"
    aDoor?.close()
}  

/************************************************************************************************************
Pending Switches On Handler
************************************************************************************************************/
def pendSwitchesOnHandler(evt) {
    if (parent.debug) log.info "pendingSwitchesOn method activated"	
    if (state.pendCancelOnFlag == true) {
        aPendSwitches?.on() 
    }
}

/************************************************************************************************************
Pending Switches Off Handler
************************************************************************************************************/
def pendSwitchesOffHandler(evt) {
    if (parent.debug) log.info "pendingSwitchesOff method activated"	
    if (state.pendCancelOnFlag == false) {
        aPendSwitches?.off()
    }
}

/************************************************************************************************************
Switches On with pending cancellation
************************************************************************************************************/
def momentaryDeviceHandlerOn(evt) {
    if (parent.debug) log.info "momentaryDeviceHandlerOn method activated"	
    if (aSwitchesOnPend) {
        if (state.aSwitchesOnCancelFlag == true) {
            aSwitchesOn?.on() }
        if (state.aSwitchesOnCancelFlag == false) { unschedule(momentaryDeviceHandlerOn) }
    }
    if (!aSwitchesOnPend) {
        aSwitchesOn?.on()
    }
}    
    
/************************************************************************************************************
Switches off with pending cancellation
************************************************************************************************************/
def momentaryDeviceHandlerOff(evt) {
    if (parent.debug) log.info "momentaryDeviceHandlerOff method activated"
    if (aSwitchesOffPend) {
        if (state.aSwitchesOffCancelFlag == true) {
            aSwitchesOff?.off() }
        if (state.aSwitchesOffCancelFlag == false) { unschedule(momentaryDeviceHandlerOff) }
    }
    if (!aSwitchesOffPend) {
        aSwitchesOff?.off()
    }
}

/************************************************************************************************************
Custom Commands with delays and schedules
************************************************************************************************************/
def custCmdDelayHandler(evt) {
	if (parent.debug) log.info "custom commands handler with delay called"
    custDevice."$custCommand"()
    }

def custCmdHandler(evt) {
	if (parent.debug) log.info "custom commands handler called"
	custDevice."$custCommand"()
    }

/************************************************************************************************************
ST ROUTINES #1 HANDLER
************************************************************************************************************/
def aRout1Handler(evt) {
    if (parent.debug) log.info "aRout1 Handler method activated"
    if (state.pendRout1CancelOnFlag == true) {
    	scheduledSTRoutine1(evt)
        }
    }

/************************************************************************************************************
ST ROUTINES #2 HANDLER
************************************************************************************************************/
def aRout2Handler(evt) {
    if (parent.debug) log.info "aRout2 Handler method activated"
    if (state.pendRout2CancelOnFlag == true) {
    	scheduledSTRoutine2(evt)
        }
    }

/************************************************************************************************************
Set Dimmer levels by Modes 
************************************************************************************************************/
def dimModes(evt) {
    if (parent.debug) log.info "Dimmers1 by mode Handler activated"
	if("$dimmModes".contains(location.mode)) {
        state.lev = settings.find{it.key == ("level" + "$location.mode")}.value   
    	log.info "setting the level to ($state.lev) for devices ($modeDimmers)"
        runIn(dimmerModesDelay, devLev)
    }
}
def devLev() {
	log.info "devLev activated"
	modeDimmers.setLevel(state.lev)
    }
def getDimmModesLevels(thisMode, dimModeLvl) {
    def result = input dimModeLvl, "number", range: "0..100", title: "Set Dimmers to this level for $thisMode", required: true, submitOnChange: true
}

/******************************************************************************
COLOR BULBS HANDLER 
******************************************************************************/
private processColor(evt) {
    if (parent.debug) log.info "Color Bulbs method activated"
    if (aHuesCmd == "on") { aHues?.on() }
    if (aHuesCmd == "off") { aHues?.off() }
    if (aHuesOtherCmd == "on") { aHuesOther?.on() }
    if (aHuesOtherCmd == "off") { aHuesOther?.off() }
    def hueSetVals = getColorName("${aHuesColor}",level)
    aHues?.setColor(hueSetVals)
    def    hueSetValsOther = getColorName("${aHuesOtherColor}",level)
    aHuesOther?.setColor(hueSetValsOther)
}

private getColorName(aHuesColor, level) {
    for (color in fillColorSettings()) {
        if (color.name.toLowerCase() == aHuesColor.toLowerCase()) {
            int hueVal = Math.round(color.h / 3.6)
            int hueLevel = !level ? color.l : level
            def hueSet = [hue: hueVal, saturation: color.s, level: hueLevel]
            return hueSet
        }
    }
}

def fillColorSettings() {
    return [
        [ name: "Soft White",                rgb: "#B6DA7C",        h: 83,        s: 44,        l: 67,    ],
        [ name: "Warm White",                rgb: "#DAF17E",        h: 51,        s: 20,        l: 100,    ],
        [ name: "Very Warm White",            rgb: "#DAF17E",        h: 51,        s: 60,        l: 51,    ],
        [ name: "Daylight White",            rgb: "#CEF4FD",        h: 191,        s: 9,        l: 90,    ],
        [ name: "Daylight",                    rgb: "#CEF4FD",        h: 191,        s: 9,        l: 90,    ],
        [ name: "Cool White",                rgb: "#F3F6F7",        h: 187,        s: 19,        l: 96,    ],
        [ name: "White",                    rgb: "#FFFFFF",        h: 0,        s: 0,        l: 100,    ],
        [ name: "Alice Blue",                rgb: "#F0F8FF",        h: 208,        s: 100,        l: 97,    ],
        [ name: "Antique White",            rgb: "#FAEBD7",        h: 34,        s: 78,        l: 91,    ],
        [ name: "Aqua",                        rgb: "#00FFFF",        h: 180,        s: 100,        l: 50,    ],
        [ name: "Aquamarine",                rgb: "#7FFFD4",        h: 160,        s: 100,        l: 75,    ],
        [ name: "Azure",                    rgb: "#F0FFFF",        h: 180,        s: 100,        l: 97,    ],
        [ name: "Beige",                    rgb: "#F5F5DC",        h: 60,        s: 56,        l: 91,    ],
        [ name: "Bisque",                    rgb: "#FFE4C4",        h: 33,        s: 100,        l: 88,    ],
        [ name: "Blanched Almond",            rgb: "#FFEBCD",        h: 36,        s: 100,        l: 90,    ],
        [ name: "Blue",                        rgb: "#0000FF",        h: 240,        s: 100,        l: 50,    ],
        [ name: "Blue Violet",                rgb: "#8A2BE2",        h: 271,        s: 76,        l: 53,    ],
        [ name: "Brown",                    rgb: "#A52A2A",        h: 0,        s: 59,        l: 41,    ],
        [ name: "Burly Wood",                rgb: "#DEB887",        h: 34,        s: 57,        l: 70,    ],
        [ name: "Cadet Blue",                rgb: "#5F9EA0",        h: 182,        s: 25,        l: 50,    ],
        [ name: "Chartreuse",                rgb: "#7FFF00",        h: 90,        s: 100,        l: 50,    ],
        [ name: "Chocolate",                rgb: "#D2691E",        h: 25,        s: 75,        l: 47,    ],
        [ name: "Coral",                    rgb: "#FF7F50",        h: 16,        s: 100,        l: 66,    ],
        [ name: "Corn Flower Blue",            rgb: "#6495ED",        h: 219,        s: 79,        l: 66,    ],
        [ name: "Corn Silk",                rgb: "#FFF8DC",        h: 48,        s: 100,        l: 93,    ],
        [ name: "Crimson",                    rgb: "#DC143C",        h: 348,        s: 83,        l: 58,    ],
        [ name: "Cyan",                        rgb: "#00FFFF",        h: 180,        s: 100,        l: 50,    ],
        [ name: "Dark Blue",                rgb: "#00008B",        h: 240,        s: 100,        l: 27,    ],
        [ name: "Dark Cyan",                rgb: "#008B8B",        h: 180,        s: 100,        l: 27,    ],
        [ name: "Dark Golden Rod",            rgb: "#B8860B",        h: 43,        s: 89,        l: 38,    ],
        [ name: "Dark Gray",                rgb: "#A9A9A9",        h: 0,        s: 0,        l: 66,    ],
        [ name: "Dark Green",                rgb: "#006400",        h: 120,        s: 100,        l: 20,    ],
        [ name: "Dark Khaki",                rgb: "#BDB76B",        h: 56,        s: 38,        l: 58,    ],
        [ name: "Dark Magenta",                rgb: "#8B008B",        h: 300,        s: 100,        l: 27,    ],
        [ name: "Dark Olive Green",            rgb: "#556B2F",        h: 82,        s: 39,        l: 30,    ],
        [ name: "Dark Orange",                rgb: "#FF8C00",        h: 33,        s: 100,        l: 50,    ],
        [ name: "Dark Orchid",                rgb: "#9932CC",        h: 280,        s: 61,        l: 50,    ],
        [ name: "Dark Red",                    rgb: "#8B0000",        h: 0,        s: 100,        l: 27,    ],
        [ name: "Dark Salmon",                rgb: "#E9967A",        h: 15,        s: 72,        l: 70,    ],
        [ name: "Dark Sea Green",            rgb: "#8FBC8F",        h: 120,        s: 25,        l: 65,    ],
        [ name: "Dark Slate Blue",            rgb: "#483D8B",        h: 248,        s: 39,        l: 39,    ],
        [ name: "Dark Slate Gray",            rgb: "#2F4F4F",        h: 180,        s: 25,        l: 25,    ],
        [ name: "Dark Turquoise",            rgb: "#00CED1",        h: 181,        s: 100,        l: 41,    ],
        [ name: "Dark Violet",                rgb: "#9400D3",        h: 282,        s: 100,        l: 41,    ],
        [ name: "Deep Pink",                rgb: "#FF1493",        h: 328,        s: 100,        l: 54,    ],
        [ name: "Deep Sky Blue",            rgb: "#00BFFF",        h: 195,        s: 100,        l: 50,    ],
        [ name: "Dim Gray",                    rgb: "#696969",        h: 0,        s: 0,        l: 41,    ],
        [ name: "Dodger Blue",                rgb: "#1E90FF",        h: 210,        s: 100,        l: 56,    ],
        [ name: "Fire Brick",                rgb: "#B22222",        h: 0,        s: 68,        l: 42,    ],
        [ name: "Floral White",                rgb: "#FFFAF0",        h: 40,        s: 100,        l: 97,    ],
        [ name: "Forest Green",                rgb: "#228B22",        h: 120,        s: 61,        l: 34,    ],
        [ name: "Fuchsia",                    rgb: "#FF00FF",        h: 300,        s: 100,        l: 50,    ],
        [ name: "Gainsboro",                rgb: "#DCDCDC",        h: 0,        s: 0,        l: 86,    ],
        [ name: "Ghost White",                rgb: "#F8F8FF",        h: 240,        s: 100,        l: 99,    ],
        [ name: "Gold",                        rgb: "#FFD700",        h: 51,        s: 100,        l: 50,    ],
        [ name: "Golden Rod",                rgb: "#DAA520",        h: 43,        s: 74,        l: 49,    ],
        [ name: "Gray",                        rgb: "#808080",        h: 0,        s: 0,        l: 50,    ],
        [ name: "Green",                    rgb: "#008000",        h: 120,        s: 100,        l: 25,    ],
        [ name: "Green Yellow",                rgb: "#ADFF2F",        h: 84,        s: 100,        l: 59,    ],
        [ name: "Honeydew",                    rgb: "#F0FFF0",        h: 120,        s: 100,        l: 97,    ],
        [ name: "Hot Pink",                    rgb: "#FF69B4",        h: 330,        s: 100,        l: 71,    ],
        [ name: "Indian Red",                rgb: "#CD5C5C",        h: 0,        s: 53,        l: 58,    ],
        [ name: "Indigo",                    rgb: "#4B0082",        h: 275,        s: 100,        l: 25,    ],
        [ name: "Ivory",                    rgb: "#FFFFF0",        h: 60,        s: 100,        l: 97,    ],
        [ name: "Khaki",                    rgb: "#F0E68C",        h: 54,        s: 77,        l: 75,    ],
        [ name: "Lavender",                    rgb: "#E6E6FA",        h: 240,        s: 67,        l: 94,    ],
        [ name: "Lavender Blush",            rgb: "#FFF0F5",        h: 340,        s: 100,        l: 97,    ],
        [ name: "Lawn Green",                rgb: "#7CFC00",        h: 90,        s: 100,        l: 49,    ],
        [ name: "Lemon Chiffon",            rgb: "#FFFACD",        h: 54,        s: 100,        l: 90,    ],
        [ name: "Light Blue",                rgb: "#ADD8E6",        h: 195,        s: 53,        l: 79,    ],
        [ name: "Light Coral",                rgb: "#F08080",        h: 0,        s: 79,        l: 72,    ],
        [ name: "Light Cyan",                rgb: "#E0FFFF",        h: 180,        s: 100,        l: 94,    ],
        [ name: "Light Golden Rod Yellow",    rgb: "#FAFAD2",        h: 60,        s: 80,        l: 90,    ],
        [ name: "Light Gray",                rgb: "#D3D3D3",        h: 0,        s: 0,        l: 83,    ],
        [ name: "Light Green",                rgb: "#90EE90",        h: 120,        s: 73,        l: 75,    ],
        [ name: "Light Pink",                rgb: "#FFB6C1",        h: 351,        s: 100,        l: 86,    ],
        [ name: "Light Salmon",                rgb: "#FFA07A",        h: 17,        s: 100,        l: 74,    ],
        [ name: "Light Sea Green",            rgb: "#20B2AA",        h: 177,        s: 70,        l: 41,    ],
        [ name: "Light Sky Blue",            rgb: "#87CEFA",        h: 203,        s: 92,        l: 75,    ],
        [ name: "Light Slate Gray",            rgb: "#778899",        h: 210,        s: 14,        l: 53,    ],
        [ name: "Light Steel Blue",            rgb: "#B0C4DE",        h: 214,        s: 41,        l: 78,    ],
        [ name: "Light Yellow",                rgb: "#FFFFE0",        h: 60,        s: 100,        l: 94,    ],
        [ name: "Lime",                        rgb: "#00FF00",        h: 120,        s: 100,        l: 50,    ],
        [ name: "Lime Green",                rgb: "#32CD32",        h: 120,        s: 61,        l: 50,    ],
        [ name: "Linen",                    rgb: "#FAF0E6",        h: 30,        s: 67,        l: 94,    ],
        [ name: "Maroon",                    rgb: "#800000",        h: 0,        s: 100,        l: 25,    ],
        [ name: "Medium Aquamarine",        rgb: "#66CDAA",        h: 160,        s: 51,        l: 60,    ],
        [ name: "Medium Blue",                rgb: "#0000CD",        h: 240,        s: 100,        l: 40,    ],
        [ name: "Medium Orchid",            rgb: "#BA55D3",        h: 288,        s: 59,        l: 58,    ],
        [ name: "Medium Purple",            rgb: "#9370DB",        h: 260,        s: 60,        l: 65,    ],
        [ name: "Medium Sea Green",            rgb: "#3CB371",        h: 147,        s: 50,        l: 47,    ],
        [ name: "Medium Slate Blue",        rgb: "#7B68EE",        h: 249,        s: 80,        l: 67,    ],
        [ name: "Medium Spring Green",        rgb: "#00FA9A",        h: 157,        s: 100,        l: 49,    ],
        [ name: "Medium Turquoise",            rgb: "#48D1CC",        h: 178,        s: 60,        l: 55,    ],
        [ name: "Medium Violet Red",        rgb: "#C71585",        h: 322,        s: 81,        l: 43,    ],
        [ name: "Midnight Blue",            rgb: "#191970",        h: 240,        s: 64,        l: 27,    ],
        [ name: "Mint Cream",                rgb: "#F5FFFA",        h: 150,        s: 100,        l: 98,    ],
        [ name: "Misty Rose",                rgb: "#FFE4E1",        h: 6,        s: 100,        l: 94,    ],
        [ name: "Moccasin",                    rgb: "#FFE4B5",        h: 38,        s: 100,        l: 85,    ],
        [ name: "Navajo White",                rgb: "#FFDEAD",        h: 36,        s: 100,        l: 84,    ],
        [ name: "Navy",                        rgb: "#000080",        h: 240,        s: 100,        l: 25,    ],
        [ name: "Old Lace",                    rgb: "#FDF5E6",        h: 39,        s: 85,        l: 95,    ],
        [ name: "Olive",                    rgb: "#808000",        h: 60,        s: 100,        l: 25,    ],
        [ name: "Olive Drab",                rgb: "#6B8E23",        h: 80,        s: 60,        l: 35,    ],
        [ name: "Orange",                    rgb: "#FFA500",        h: 39,        s: 100,        l: 50,    ],
        [ name: "Orange Red",                rgb: "#FF4500",        h: 16,        s: 100,        l: 50,    ],
        [ name: "Orchid",                    rgb: "#DA70D6",        h: 302,        s: 59,        l: 65,    ],
        [ name: "Pale Golden Rod",            rgb: "#EEE8AA",        h: 55,        s: 67,        l: 80,    ],
        [ name: "Pale Green",                rgb: "#98FB98",        h: 120,        s: 93,        l: 79,    ],
        [ name: "Pale Turquoise",            rgb: "#AFEEEE",        h: 180,        s: 65,        l: 81,    ],
        [ name: "Pale Violet Red",            rgb: "#DB7093",        h: 340,        s: 60,        l: 65,    ],
        [ name: "Papaya Whip",                rgb: "#FFEFD5",        h: 37,        s: 100,        l: 92,    ],
        [ name: "Peach Puff",                rgb: "#FFDAB9",        h: 28,        s: 100,        l: 86,    ],
        [ name: "Peru",                        rgb: "#CD853F",        h: 30,        s: 59,        l: 53,    ],
        [ name: "Pink",                        rgb: "#FFC0CB",        h: 350,        s: 100,        l: 88,    ],
        [ name: "Plum",                        rgb: "#DDA0DD",        h: 300,        s: 47,        l: 75,    ],
        [ name: "Powder Blue",                rgb: "#B0E0E6",        h: 187,        s: 52,        l: 80,    ],
        [ name: "Purple",                    rgb: "#800080",        h: 300,        s: 100,        l: 25,    ],
        [ name: "Red",                        rgb: "#FF0000",        h: 0,        s: 100,        l: 50,    ],
        [ name: "Rosy Brown",                rgb: "#BC8F8F",        h: 0,        s: 25,        l: 65,    ],
        [ name: "Royal Blue",                rgb: "#4169E1",        h: 225,        s: 73,        l: 57,    ],
        [ name: "Saddle Brown",                rgb: "#8B4513",        h: 25,        s: 76,        l: 31,    ],
        [ name: "Salmon",                    rgb: "#FA8072",        h: 6,        s: 93,        l: 71,    ],
        [ name: "Sandy Brown",                rgb: "#F4A460",        h: 28,        s: 87,        l: 67,    ],
        [ name: "Sea Green",                rgb: "#2E8B57",        h: 146,        s: 50,        l: 36,    ],
        [ name: "Sea Shell",                rgb: "#FFF5EE",        h: 25,        s: 100,        l: 97,    ],
        [ name: "Sienna",                    rgb: "#A0522D",        h: 19,        s: 56,        l: 40,    ],
        [ name: "Silver",                    rgb: "#C0C0C0",        h: 0,        s: 0,        l: 75,    ],
        [ name: "Sky Blue",                    rgb: "#87CEEB",        h: 197,        s: 71,        l: 73,    ],
        [ name: "Slate Blue",                rgb: "#6A5ACD",        h: 248,        s: 53,        l: 58,    ],
        [ name: "Slate Gray",                rgb: "#708090",        h: 210,        s: 13,        l: 50,    ],
        [ name: "Snow",                        rgb: "#FFFAFA",        h: 0,        s: 100,        l: 99,    ],
        [ name: "Spring Green",                rgb: "#00FF7F",        h: 150,        s: 100,        l: 50,    ],
        [ name: "Steel Blue",                rgb: "#4682B4",        h: 207,        s: 44,        l: 49,    ],
        [ name: "Tan",                        rgb: "#D2B48C",        h: 34,        s: 44,        l: 69,    ],
        [ name: "Teal",                        rgb: "#008080",        h: 180,        s: 100,        l: 25,    ],
        [ name: "Thistle",                    rgb: "#D8BFD8",        h: 300,        s: 24,        l: 80,    ],
        [ name: "Tomato",                    rgb: "#FF6347",        h: 9,        s: 100,        l: 64,    ],
        [ name: "Turquoise",                rgb: "#40E0D0",        h: 174,        s: 72,        l: 56,    ],
        [ name: "Violet",                    rgb: "#EE82EE",        h: 300,        s: 76,        l: 72,    ],
        [ name: "Wheat",                    rgb: "#F5DEB3",        h: 39,        s: 77,        l: 83,    ],
        [ name: "White Smoke",                rgb: "#F5F5F5",        h: 0,        s: 0,        l: 96,    ],
        [ name: "Yellow",                    rgb: "#FFFF00",        h: 60,        s: 100,        l: 50,    ],
        [ name: "Yellow Green",                rgb: "#9ACD32",        h: 80,        s: 61,        l: 50,    ],
    ]
}

/***********************************************************************************************************************
    SUN STATE HANDLER - good
***********************************************************************************************************************/
def sunsetTimeHandler(evt) {
if (parent.debug) log.info "Sunset Handler activated"
    def sunsetString = (String) evt.value
    def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: startSunriseOffset, sunsetOffset: startSunsetOffset)
    def sunsetTime = s.sunset.time 
    if(offset) {
        def offsetSunset = new Date(sunsetTime - (-offset * 60 * 1000))
        if (parent.trace) log.debug "Scheduling for: $offsetSunset (sunset is $sunsetTime)"
        runOnce(offsetSunset, "processActions")
    }
    else processActions("sunset")
}
def sunriseTimeHandler(evt) {
    def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: startSunriseOffset, sunsetOffset: startSunsetOffset)
    def sunriseTime = s.sunrise.time
    if(offset) {
        def offsetSunrise = new Date(sunriseTime -(-offset * 60 * 1000))
        if (parent.trace) log.debug "Scheduling for: $offsetSunrise (sunrise is $sunriseTime)"
        runOnce(offsetSunrise, "processActions")
    }
    else  processActions("sunrise")
}

def scheduledTimeHandler(state) {
    def data = [:]
    data = [value: "executed", name:"timer", device:"schedule"] 
    processActions(data)
}
     
/***********************************************************************************************************************
	TIME RESTRICTIONS FOR SMS/PUSH/AUDIO MESSAGES ONLY
***********************************************************************************************************************/
private getSMSDayOk(evt) {
	if (parent.debug) log.info "SMS custom restrictions Handler activated"
    def result = true
    if (smsDays) {
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        result = smsDays.contains(day)
    }
    if(parent.debug) log.warn "SMSDayOk = $result"
    return result
}
private getMTimeOk(evt) {
    def result = true
    if ((startingm && endingm) ||
        (startingm && endingXm in ["Sunrise", "Sunset"]) ||
        (startingXm in ["Sunrise", "Sunset"] && endingm) ||
        (startingXm in ["Sunrise", "Sunset"] && endingXm in ["Sunrise", "Sunset"])) {
        def currTimem = now()
        def startm = null
        def stopm = null
        def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetm: startSunriseOffsetm, sunsetOffsetm: startSunsetOffsetm)
        if(startingXm == "Sunrise") startm = s.sunrise.time
        else if(startingXm == "Sunset") startm = s.sunset.time
            else if(startingm) startm = timeToday(startingm,location.timeZone).time
                s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetm: endSunriseOffsetm, sunsetOffsetm: endSunsetOffsetm)
            if(endingXm == "Sunrise") stopm = s.sunrise.time
            else if(endingXm == "Sunset") stopm = s.sunset.time
                else if(endingm) stopm = timeToday(endingm,location.timeZone).time
                    result = startm < stopm ? currTimem >= startm && currTimem <= stopm : currTimem <= stopm || currTimem >= startm
            }
    if(parent.debug) log.warn "MTimeOk = $result"
    return result
}
private mhhmm(time, fmt = "h:mm a") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
private offsetm(value) {
    def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private mtimeIntervalLabel() {
    def result = "true"
    if      (startingXm == "Sunrise" && endingXm == "Sunrise")  result = "Sunrise" + offsetm(startSunriseOffsetm) + " to Sunrise" + offsetm(endSunriseOffsetm)
    else if (startingXm == "Sunrise" && endingXm == "Sunset")  result = "Sunrise" + offsetm(startSunriseOffsetm) + " to Sunset" + offsetm(endSunsetOffsetm)
        else if (startingXm == "Sunset" && endingXm == "Sunrise")  result = "Sunset" + offsetm(startSunsetOffsetm) + " to Sunrise" + offsetm(endSunriseOffsetm)
            else if (startingXm == "Sunset" && endingXm == "Sunset")  result = "Sunset" + offsetm(startSunsetOffsetm) + " to Sunset" + offsetm(endSunsetOffsetm)
                else if (startingXm == "Sunrise" && endingm)  result = "Sunrise" + offsetm(startSunriseOffsetm) + " to " + mhhmm(ending, "h:mm a z")
                    else if (startingXm == "Sunset" && endingm)  result = "Sunset" + offsetm(startSunsetOffsetm) + " to " + mhhmm(ending, "h:mm a z")
                        else if (startingm && endingXm == "Sunrise")  result = mhhmm(startingm) + " to Sunrise" + offsetm(endSunriseOffsetm)
                            else if (startingm && endingXm == "Sunset")  result = mhhmm(startingm) + " to Sunset" + offsetm(endSunsetOffsetm)
                                else if (startingm && endingm)  result = mhhmm(startingm) + " to " + mhhmm(endingm, "h:mm a z")
                                    }

/***********************************************************************************************************************
	SMARTTHING ROUTINES RESTRICTIONS HANDLER - 1ST SET
***********************************************************************************************************************/
private aRoutDays1Ok(evt) {
    def aRoutDays1Ok = false
    def result = true

    if (aRoutDays1 == null) { aRoutDays1Ok = true }

    if (aRoutDays1) {
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        result = aRoutDays1.contains(day)
    }
    if(parent.debug) log.warn "aRoutDays1Ok = $result"
    return result
}
private aRout1TimeOk(evt) {
    def aRout1TimeOk = false
    def result = true

    if (aRout1TimeOk == null) { aRout1TimeOk = true }
    if ((startingR1 && endingR1) ||
        (startingR1 && endingXR1 in ["Sunrise", "Sunset"]) ||
        (startingXR1 in ["Sunrise", "Sunset"] && endingR1) ||
        (startingXR1 in ["Sunrise", "Sunset"] && endingXR1 in ["Sunrise", "Sunset"])) {
        def currTimeR1 = now()
        def startR1 = null
        def stopR1 = null
        def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetR1: startSunriseOffsetR1, sunsetOffsetR1: startSunsetOffsetR1)
        if(startingXR1 == "Sunrise") startR1 = s.sunrise.time
        else if(startingXR1 == "Sunset") startR1 = s.sunset.time
            else if(startingR1) startR1 = timeToday(startingR1,location.timeZone).time
                s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetR1: endSunriseOffsetR1, sunsetOffsetR1: endSunsetOffsetR1)
            if(endingXR1 == "Sunrise") stopR1 = s.sunrise.time
            else if(endingXR1 == "Sunset") stopR1 = s.sunset.time
                else if(endingR1) stopR1 = timeToday(endingR1,location.timeZone).time
                    result = startR1 < stopR1 ? currTimeR1 >= startR1 && currTimeR1 <= stopR1 : currTimeR1 <= stopR1 || currTimeR1 >= startR1
            }
    if(parent.debug) log.warn "aRout1TimeOk = $result"
    return result
}
private R1hhmm(time, fmt = "h:mm a") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
private offsetR1(value) {
    def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private R1timeIntervalLabel() {
    def result = "true"
    if      (startingXR1 == "Sunrise" && endingXR1 == "Sunrise")  result = "Sunrise" + offsetR1(startSunriseOffsetR1) + " to Sunrise" + offsetR1(endSunriseOffsetR1)
    else if (startingXR1 == "Sunrise" && endingXR1 == "Sunset")  result = "Sunrise" + offsetR1(startSunriseOffsetR1) + " to Sunset" + offsetR1(endSunsetOffsetR1)
        else if (startingXR1 == "Sunset" && endingXR1 == "Sunrise")  result = "Sunset" + offsetR1(startSunsetOffsetR1) + " to Sunrise" + offsetR1(endSunriseOffsetR1)
            else if (startingXR1 == "Sunset" && endingXR1 == "Sunset")  result = "Sunset" + offsetR1(startSunsetOffsetR1) + " to Sunset" + offsetR1(endSunsetOffsetR1)
                else if (startingXR1 == "Sunrise" && endingR1)  result = "Sunrise" + offsetR1(startSunriseOffsetR1) + " to " + R1hhmm(endingR1, "h:mm a z")
                    else if (startingXR1 == "Sunset" && endingR1)  result = "Sunset" + offsetR1(startSunsetOffset) + " to " + R1hhmm(endingR1, "h:mm a z")
                        else if (startingR1 && endingXR1 == "Sunrise")  result = R1hhmm(startingR1) + " to Sunrise" + offsetR1(endSunriseOffsetR1)
                            else if (startingR1 && endingXR1 == "Sunset")  result = R1hhmm(startingR1) + " to Sunset" + offsetR1(endSunsetOffsetR1)
                                else if (startingR1 && endingR1)  result = R1hhmm(startingR1) + " to " + R1hhmm(endingR1, "h:mm a z")
                                    }

/***********************************************************************************************************************
	SMARTTHING ROUTINES RESTRICTIONS HANDLER - 2ND SET
***********************************************************************************************************************/
private aRoutDays2Ok(evt) {
    def aRoutDays2Ok = false
    def result = true

    if (aRoutDays2 == null) { aRoutDays2Ok = true }
    if (aRoutDays2) {
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        result = str2Days.contains(day)
    }
    if(parent.debug) log.warn "aRoutDays2Ok = $result"
    return result
}
private aRout2TimeOk(evt) {
    def aRout2TimeOk = false
    def result = true

    if (aRout2TimeOk == null) { aRout2TimeOk = true }
    if ((startingR2 && endingR2) ||
        (startingR2 && endingXR2 in ["Sunrise", "Sunset"]) ||
        (startingXR2 in ["Sunrise", "Sunset"] && endingR2) ||
        (startingXR2 in ["Sunrise", "Sunset"] && endingXR2 in ["Sunrise", "Sunset"])) {
        def currTimeR2 = now()
        def startR2 = null
        def stopR2 = null
        def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetR2: startSunriseOffsetR2, sunsetOffsetR2: startSunsetOffsetR2)
        if(startingXR2 == "Sunrise") startR2 = s.sunrise.time
        else if(startingXR2 == "Sunset") startR2 = s.sunset.time
            else if(startingR2) startR2 = timeToday(startingR2,location.timeZone).time
                s = getSunriseAndSunset(zipCode: zipCode, sunriseOffsetR2: endSunriseOffsetR2, sunsetOffsetR2: endSunsetOffsetR2)
            if(endingXR2 == "Sunrise") stopR2 = s.sunrise.time
            else if(endingXR2 == "Sunset") stopR2 = s.sunset.time
                else if(endingR2) stopR2 = timeToday(endingR2,location.timeZone).time
                    result = startR2 < stopR2 ? currTimeR2 >= startR2 && currTimeR2 <= stopR2 : currTimeR2 <= stopR2 || currTimeR2 >= startR2
            }
    if(parent.debug) log.warn "aRout2TimeOk = $result"
    return result
}
private R2hhmm(time, fmt = "h:mm a") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
private offsetR2(value) {
    def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private R2timeIntervalLabel() {
    def result = "true"
    if      (startingXR2 == "Sunrise" && endingXR2 == "Sunrise")  result = "Sunrise" + offsetR2(startSunriseOffsetR2) + " to Sunrise" + offsetR2(endSunriseOffsetR2)
    else if (startingXR2 == "Sunrise" && endingXR2 == "Sunset")  result = "Sunrise" + offsetR2(startSunriseOffsetR2) + " to Sunset" + offsetR2(endSunsetOffsetR2)
        else if (startingXR2 == "Sunset" && endingXR2 == "Sunrise")  result = "Sunset" + offsetR2(startSunsetOffsetR2) + " to Sunrise" + offsetR2(endSunriseOffsetR2)
            else if (startingXR2 == "Sunset" && endingXR2 == "Sunset")  result = "Sunset" + offsetR2(startSunsetOffsetR2) + " to Sunset" + offsetR2(endSunsetOffsetR2)
                else if (startingXR2 == "Sunrise" && endingR2)  result = "Sunrise" + offsetR2(startSunriseOffsetR2) + " to " + R2hhmm(endingR2, "h:mm a z")
                    else if (startingXR2 == "Sunset" && endingR2)  result = "Sunset" + offsetR2(startSunsetOffset) + " to " + R2hhmm(endingR2, "h:mm a z")
                        else if (startingR2 && endingXR2 == "Sunrise")  result = R2hhmm(startingR2) + " to Sunrise" + offsetR2(endSunriseOffsetR2)
                            else if (startingR2 && endingXR2 == "Sunset")  result = R2hhmm(startingR2) + " to Sunset" + offsetR2(endSunsetOffsetR2)
                                else if (startingR2 && endingR2)  result = R2hhmm(startingR2) + " to " + R2hhmm(endingR2, "h:mm a z")
                                    }

// TIME RESTRICTIONS - ENTIRE ROUTINE
private getTimeOk(evt) {
    if (parent.aPause == true) {
        def result = true
        if ((starting && ending) ||
            (starting && endingX in ["Sunrise", "Sunset"]) ||
            (startingX in ["Sunrise", "Sunset"] && ending) ||
            (startingX in ["Sunrise", "Sunset"] && endingX in ["Sunrise", "Sunset"])) {
            def currTime = now()
            def start = null
            def stop = null
            def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: startSunriseOffset, sunsetOffset: startSunsetOffset)
            if(startingX == "Sunrise") start = s.sunrise.time
            else if(startingX == "Sunset") start = s.sunset.time
                else if(starting) start = timeToday(starting,location.timeZone).time
                    s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: endSunriseOffset, sunsetOffset: endSunsetOffset)
                if(endingX == "Sunrise") stop = s.sunrise.time
                else if(endingX == "Sunset") stop = s.sunset.time
                    else if(ending) stop = timeToday(ending,location.timeZone).time
                        result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
                }
        if(parent.trace) log.trace "timeOk = $result"
        return result
    }
}
private hhmm(time, fmt = "h:mm a") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
private offset(value) {
    def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private timeIntervalLabel() {
    def result = "complete"
    if      (startingX == "Sunrise" && endingX == "Sunrise") result = "Sunrise" + offset(startSunriseOffset) + " to Sunrise" + offset(endSunriseOffset)
    else if (startingX == "Sunrise" && endingX == "Sunset") result = "Sunrise" + offset(startSunriseOffset) + " to Sunset" + offset(endSunsetOffset)
        else if (startingX == "Sunset" && endingX == "Sunrise") result = "Sunset" + offset(startSunsetOffset) + " to Sunrise" + offset(endSunriseOffset)
            else if (startingX == "Sunset" && endingX == "Sunset") result = "Sunset" + offset(startSunsetOffset) + " to Sunset" + offset(endSunsetOffset)
                else if (startingX == "Sunrise" && ending) result = "Sunrise" + offset(startSunriseOffset) + " to " + hhmm(ending, "h:mm a z")
                    else if (startingX == "Sunset" && ending) result = "Sunset" + offset(startSunsetOffset) + " to " + hhmm(ending, "h:mm a z")
                        else if (starting && endingX == "Sunrise") result = hhmm(starting) + " to Sunrise" + offset(endSunriseOffset)
                            else if (starting && endingX == "Sunset") result = hhmm(starting) + " to Sunset" + offset(endSunsetOffset)
                                else if (starting && ending) result = hhmm(starting) + " to " + hhmm(ending, "h:mm a z")
                                    }

/************************************************************************************************************
   CUSTOM MESSAGE VARIABLES 
************************************************************************************************************/
def runProfile(message, evt) {
	if (parent.debug) log.info "Message Variables Handler activated"
    def result 
    if(message) {
        result = message ? "$message".replace("&date", "${getVar("date")}").replace("&time", "${getVar("time")}").replace("&winOpen", "${getVar("winOpen")}") : null
        result = result ? "$result".replace("&winClosed", "${getVar("winClosed")}").replace("&doorOpen", "${getVar("doorOpen")}").replace("&doorClosed", "${getVar("doorClosed")}") : null
        result = result ? "$result".replace("&temperature", "${getVar("temperature")}").replace("&tempTrend", "${getVar("tempTrend")}").replace("&humidity", "${getVar("humidity")}") : null
        result = result ? "$result".replace("&windSpeed", "${getVar("windSpeed")}").replace("&windDir", "${getVar("windDir")}").replace("&lightsOn", "${getVar("lightsOn")}") : null
        result = result ? "$result".replace("&high", "${getVar("high")}").replace("&low", "${getVar("low")}").replace("&rain", "${getVar("rain")}") : null
        result = result ? "$result".replace("&smoke", "${getVar("smoke")}").replace("&CO2", "${getVar("CO2")}").replace("&water", "${getVar("water")}") : null
        result = result ? "$result".replace("&device", "${getVar("device")}").replace("&action", "${getVar("action")}").replace("&stRoutAction", "${getVar("stRoutAction")}") : null
        result = result ? "$result".replace("&stRoutEvent", "${getVar("stRoutEvent")}").replace("&feelsLike", "${getVar("feelsLike")}") : null
        result = result ? "$result".replace("&shm", "${getVar("shm")}").replace("&mode", "${getVar("mode")}").replace("&humIn", "${getVar("humIn")}").replace("&humOut", "${getVar("humOut")}") : null
        result = result ? "$result".replace("&fans", "${getVar("fans")}").replace("&tempIn", "${getVar("tempIn")}").replace("&tempOut", "${getVar("tempOut")}") : null
//        result = getWeatherVar(result) 
    }
    return stripBrackets(result ? " $result " : "")
}
/************************************************************************************************************
   REPORT VARIABLES   
************************************************************************************************************/
private getVar(var) {
    def devList = []
    def result
    
	if (var == "mode"){
        result = location.currentMode
        return stripBrackets(result ? " $result " : "")
    }
	if (var == "shm"){
        def currentSHM = location.currentState("alarmSystemStatus")?.value
        def shmStatus = currentSHM == "stay" ? "armed home" : currentSHM == "away" ? "armed away" : currentSHM == "off" ? "disarmed" : null
        result = shmStatus
        return stripBrackets(result ? " $result " : "")
    }
	if (var == "time"){
        result = new Date(now()).format("h:mm aa", location.timeZone) 
        return stripBrackets(result ? " $result " : "")
    }
    if (var == "date"){
        result = new Date(now()).format("EEEE, MMMM d, yyyy", location.timeZone)
        return stripBrackets(result ? " $result " : "")    
    }
    if (var == "tempIn"){    
        if(vTempIn){
            def total = 0
            vTempIn.each {total += it.currentValue("temperature")}
            int avgT = total as Integer
            result = Math.round(total/vTempIn?.size()) + " degrees"
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "tempOut"){    
        if(vTempOut){
            def total = 0
            vTempOut.each {total += it.currentValue("temperature")}
            int avgT = total as Integer
            result = Math.round(total/vTempOut?.size()) + " degrees"
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "high"){    
        if(vTemperature){
            result = vtempOut.latestValue("max_temp") + " degrees"             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "low"){    
        if(vTemperature){
            result = vtempOut.latestValue("min_temp") + " degrees"             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "tempTrend"){    
        if(vTemperature){
            result = "trending " + vtempOut.latestValue("temp_trend")             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "feelsLike"){
        if(vFeelsLike){
            result = "feel like " + vFeelsLike.latestValue("feelsLike") + " degrees"
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "rain"){    
        if(vRain){
            result = vRain.latestValue("rain") + " inches"             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "stRoutAction"){
        if(aRout1 || aRout2){
            result = "${state.stRoutAction}"   
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "stRoutEvent"){
        if(tRoutine){
            result = "${state.stRoutEvent}"
            return stripBrackets(result ? " $result " : "")
        }
    }            
    if (var == "device"){ 
        result = "${state.eDev}" 
        return stripBrackets(result ? " $result " : "")
    }
    if (var == "action"){ 
        result = "${state.eAct}"
        return stripBrackets(result ? " $result " : "")
    }
    if (var == "humIn"){    
        if(vHumIn){
            def total = 0
            vHumIn.each {total += it.currentValue("humidity")}
            int avgT = total as Integer
            result = Math.round(total/vHumIn?.size()) + " percent"
            return stripBrackets(result ? " $result " : "")
            }
        }
    if (var == "humOut"){    
        if(vHumOut){
            def total = 0
            vHumOut.each {total += it.currentValue("humidity")}
            int avgT = total as Integer
            result = Math.round(total/vHumIn?.size()) + " percent"
            return stripBrackets(result ? " $result " : "")
            }
        }
    if (var == "windSpeed"){    
        if(vWind){
            result = vWind.latestValue("WindStrength") + " miles per hour"             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "windDir"){    
        if(vWind){
            result = " blowing " + vWind.latestValue("WindDirection")             	
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "CO2"){    
        if(vCO2){
            result = vCO2.latestValue("carbonDioxide")             	
            log.info "The CO2 level is at $result"
            return stripBrackets(result ? " $result " : "")
        }
    }
    if (var == "noise"){    
        if(vSound){
            result = vSound.latestValue("soundPressureLevel") + " decibles"            	
            return stripBrackets(result ? " $result " : "")
        }
    }    
    if (var == "smoke"){
        if(vSmoke){
            if (vSmoke.latestValue("smoke")?.contains("detected")) {
                vSmoke.each { deviceName ->
                    if (deviceName.currentValue("smoke")=="${"detected"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " sensor detected smoke"
            else if (devList?.size() > 0) result = devList?.size() + " sensors detected smoke"
                else if (!devList) result = "no sensors detected smoke"
                    return stripBrackets(result ? " $result " : "")
                }
    }
    if (var == "water"){
        if(vWater){
            if (vWater.latestValue("water")?.contains("wet")) {
                vWater.each { deviceName ->
                    if (deviceName.currentValue("water")=="${"detected"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " sensor detected water"
            else if (devList?.size() > 0) result = devList?.size() + " sensors detected water"
                else if (!devList) result = "no sensors detected water"
                    return stripBrackets(result ? " $result " : "")
                }
    }
    if (var == "winOpen"){
        if(vWindows){
            if (vWindows.latestValue("contact")?.contains("open")) {
                vWindows.each { deviceName ->
                    if (deviceName.currentValue("contact")=="${"open"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " window is open: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " windows are open: $devList"
                else if (!devList) result = "There are no windows open"
                    return stripBrackets(result ? " $result " : "")
                }
    	}
    if (var == "winClosed"){
        if(vWindows){
            if (vWindows.latestValue("contact")?.contains("closed")) {
                vWindows.each { deviceName ->
                    if (deviceName.currentValue("contact")=="${"closed"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " window is open: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " windows are open: $devList"
                else if (!devList) result = "There are no windows open"
                    return stripBrackets(result ? " $result " : "")
                }
    	}
    if (var == "doorOpen"){
        if(vDoors){
            if (vDoors.latestValue("contact")?.contains("open")) {
                vDoors.each { deviceName ->
                    if (deviceName.currentValue("contact")=="${"open"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " door is open: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " doors are open: $devList"
                else if (!devList) result = "All of the doors are Closed"
                    return stripBrackets(result ? " $result " : "")
                }
    	}
    if (var == "doorClosed"){
        if(vDoors){
            if (vDoors.latestValue("contact")?.contains("closed")) {
                vDoors.each { deviceName ->
                    if (deviceName.currentValue("contact")=="${"closed"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " door is closed: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " doors are closed: $devList"
                else if (!devList) result = "All of the doors are Open"
                    return stripBrackets(result ? " $result " : "")
                }
    	}
    if (var == "lightsOn"){
        if(vLights){
            if (vLights.latestValue("switch")?.contains("on")) {
                vLights.each { deviceName ->
                    if (deviceName.currentValue("switch")=="${"on"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            }
            if (devList?.size() == 1)  result = devList?.size() + " light is on: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " lights are on: $devList"
                else if (!devList) result = "All of the lights are off"
                    return stripBrackets(result ? " $result " : "")
                }
    		}
    if (var == "fans"){
        if(vFans){
            if (vFans.latestValue("switch")?.contains("on")) {
                vFans.each { deviceName ->
                    if (deviceName.currentValue("switch")=="${"on"}") {
                        String device  = (String) deviceName
                        devList += device
                    }
                }
            if (devList?.size() == 1)  result = devList?.size() + " fan is on: $devList"
            else if (devList?.size() > 0) result = devList?.size() + " fans are on: $devList"
                else if (!devList) result = "All of the fans are off"
                    return stripBrackets(result ? " $result " : "")
                }
    		}
        }
	}
    
private stripBrackets(str) {
    str = str.replace("[", "")
    return str.replace("]", "")
}

/******************************************************************************************************
PARENT STATUS CHECKS
******************************************************************************************************/
// TRIGGER ACTIONS
def pTriggerSettings() {
    if (myCO2||tTemperature||tWind||tRain||tWater||tHumidity||tKeypads||tMode||tSHM||tRoutine||mySunstate||tSchedule||tSwitch||tDim||
    	tMotion||tPresence||tContactDoor||tContactWindow||tContact||tLocks||tGarage) {
        return "complete"
    }
    return ""
}
def pTriggerComplete() {
    if (myCO2||tTemperature||tWind||tRain||tWater||tHumidity||tKeypads||tMode||tSHM||tRoutine||mySunstate||tSchedule||tSwitch||tDim||
    	tMotion||tPresence||tContactDoor||tContactWindow||tContact||tLocks||tGarage) {
        return "Events have been Configured!"
    }
    return "Tap here to Configure Events"
}  
// CONDITIONS
def pConditionSettings() {
    if (cTemperature||cHumidity||cDays||cMode||cSHM||cSwitch||cDim||cMotion||cPresence||cContactDoor||cContactWindow||cContact||cLocks||
    	cGarage||starting||ending) {
        return "complete"
    }
    return ""
}
def pConditionComplete() {
    if (cTemperature||cHumidity||cDays||cMode||cSHM||cSwitch||cDim||cMotion||cPresence||cContactDoor||cContactWindow||cContact||cLocks||
    	cGarage||starting||ending) {
        return "Conditions have been Configured!"
    }
    return "Tap here to Configure Conditions"
}  
// ACTIONS
def pDevicesSettings() {
    if(aCeilingFans||aPendSwitches||tSelf||aSwitches||aOtherSwitches||aDim||aOtherDim||aHues||aHuesOther||aSwitchesOn||aSwitchesOff||
    	aFans||aLocks||aDoor||aTstat1||aTstat2||aVents||aShades||aPresence||aMode||aSHM||modeDimmers) { 
        return "complete"
    }
    return ""
}
def pDevicesComplete() {
    if( aCeilingFans||aPendSwitches||tSelf||aSwitches||aOtherSwitches||aDim||aOtherDim||aHues||aHuesOther||aSwitchesOn||aSwitchesOff||
    	aFans||aLocks||aDoor||aTstat1||aTstat2||aVents||aShades||aPresence||aMode||aSHM||modeDimmers) {
        return "Actions have been Configured!"
    }
    return "Tap here to Configure Actions"
}
// SMS/PUSH/AUDIO    
def pMessageSettings() {
    if(synthDevice||sonosDevice||push||sendText||smsDays) { 
        return "complete"
    }
    return ""
}
def pMessageComplete() {
    if (synthDevice||sonosDevice||push||sendText||smsDays) {
        return "Messaging has been Configured!"
    }
    return "Tap here to Configure Messaging"
}
// RESTRICTIONS
def pRestrictSettings(){ def result = "" 
                        if (rSHM || rMode || rDays || startingX || endingX || rSwitch || days || rPresence) { 
                            result = "complete"
                        }
                        return ""
                       }
def pRestrictComplete() {
    if (rSHM || rMode || rDays || startingX || endingX || rSwitch || days || rPresence) {
        return "Restrictions have been configured!"
    }
    return "Tap here to Configure Restrictions"
}
def pTimeSettings(){ def result = "" 
                    if (startingX || endingX) { 
                        result = "complete"}
                    result}
def pTimeComplete() {def text = "Tap here to Configure" 
                     if (startingX || endingX) {
                         text = "Configured"}
                     else text = "Tap here to Configure"
                     text}
def pmTimeSettings(){ def result = "" 
                     if (startingXm || endingXm) { 
                         result = "complete"}
                     result}
def pmTimeComplete() {def text = "Tap here to configure" 
                      if (startingXm || endingXm) {
                          text = "Configured"}
                      else text = "Tap here to Configure"
                      text}
def strTimeSettings(){ def result = "" 
                      if (startingXR || endingXR) { 
                          result = "complete"}
                      result}
def strTimeComplete() {def text = "Tap here to configure" 
                       if (startingXR || endingXR) {
                           text = "Configured"}
                       else text = "Tap here to Configure"
                       text}
def strTimeSettings1(){ def result = "" 
                       if (startingXR1 || endingXR1) { 
                           result = "complete"}
                       result}
def strTimeComplete1() {def text = "Tap here to configure" 
                        if (startingXR1 || endingXR1) {
                            text = "Configured"}
                        else text = "Tap here to Configure"
                        text}

////////////////////////////////////////////////////////////////////////////////////////////////
// DEVELOPMENT SECTION - WORK HERE AND MOVE IT WHEN YOU'RE DONE
////////////////////////////////////////////////////////////////////////////////////////////////