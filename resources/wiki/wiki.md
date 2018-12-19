<span style="font-size: 40px; text-align: center;"><img align="center" src="https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/EchoSpeaks.png" width="120"> **Welcome to Echo Speaks**</span>


**Echo Speaks** is a utilitarian SmartApp and Device handler for SmartThings that
allows you to discover, select, and use the Amazon Alexa Devices directly in your SmartThings Home
Environment. 
**Echo Speaks** gives you the ability to wake your Alexa devices on-demand to speak any text
that you wish for almost any given scenario. 
Gone are the days of needing to use expensive speakers connected to your hub. 
So without further ado, The day that you have all been waiting for has arrived.

**Category**: My Apps

**Author**: Anthony Santilli

**Documentation**: Jason Headley

Minimum Requirements
--------------------

The following are required for proper operation of **Echo Speaks**.

**Accounts**
- SmartThings IDE Account access
- SmartThings account with active ST Hub (V1, V2 or V3)
- SmartThings mobile app installed on your mobile device
- During the setup process, you will be required to create a free HerokuApp account (Existing accounts will work as well)
- Amazon 2 Factor Authentication. 
  - _Not required (Unless it was enabled in the past), It's HIGHLY recommend that you enable it._

------------------------------------------------------------------------

Which Echo Devices will work?
-----------------------------

| **Devices tested to work with Echo Speaks:**|
| :------------- |
| Echo Show (Gen 1,2) |
| Echo Dot (Gen 1,2,3) |
| Echo (Gen 1,2) |
| Echo Spot |
| Kindle Fire Tablets (HD Fire Models) |
| Ecobee 4 |


***Notice:*** 
There are a lot of devices out there that have Alexa integrated. They may or may not display in the app. We are attempting to make a way to better identify supported devices. In the mean time I will update this list as often as I can.

***Incorrectly Identified?*** 
If you have a device that is not identified correctly please submit an issue here: [https://github.com/tonesto7/echo-speaks/issues](https://github.com/tonesto7/echo-speaks/issues)
  * Provide the following info in the issue:
    * DeviceType id
    * Model Name
    * Generation of the device

------------------------------------------------------------------------

Software - Current Release
--------------------------

The code for the SmartThings SmartApp is found on the GitHub site:

  **SmartApps:**     **Source Code URL:**
  ------------------ -------------------------------------------------------------------------------------------------------------------------------------------------------
  Echo Speaks:       [Parent App](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/smartapps/tonesto7/echo-speaks.src/echo-speaks.groovy)
  Echo Speaks DTH:   [Device Handler](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/devicetypes/tonesto7/echo-speaks-device.src/echo-speaks-device.groovy)

------------------------------------------------------------------------

Installation Instructions
=========================

Automated Installer (Highly Recommended)
----------------------------------------

*This is the simplest way to get Echo Speak Installed as well as other
community apps*

Please visit here for instructions: ***[SmartThings Community
Installer](http://thingsthataresmart.wiki/index.php?title=Community_Installer_(Free_Marketplace))***

------------------------------------------------------------------------

Manual Install of Code
----------------------

### SmartThings IDE Code Installation

#### SmartApp - Install

` 1. Log-in to the SmartThings IDE at `[`ST`` ``IDE`](https://account.smartthings.com/login)`  `\
` Once you log-in ensure you are able to see your hub and devices under the Locations tab.`\
` **It is assumed that the user has already registered their account on the SmartThings IDE website.  If you have not,`\
` please do so before proceeding any further.**`

![](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/IDE_Login.JPG | "wikilink" | width=800px)

` 2. Navigate to the SmartApps Section of the SmartThings IDE by clicking 'My SmartApps' tab at the top of your screen:`

[800px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/SA_Link.JPG "wikilink")

` 3. Create a new smartapp by clicking on the `[`https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/NewSA_button.JPG`](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/NewSA_button.JPG "wikilink")` button found in the top right corner of this page:`

[800px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/NewSA_Link.JPG "wikilink")

` 4. Click on the `[`https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/FromCode_button.JPG`](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/FromCode_button.JPG "wikilink")` button found at the top of the screen:`

[800px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/NewSA_FromCode.JPG "wikilink")

` 5. You will now be at this page:`

[800px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/NewSA_Blank.JPG "wikilink")

` 6. Follow this link to copy the Echo Speaks source code `[`ECHO`` ``SPEAKS`](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/smartapps/tonesto7/echo-speaks.src/echo-speaks.groovy)` Now paste it`\
` into the large white box on your screen. Scroll to the bottom of the screen`\
` and click on the 'Create' button.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-1.JPG>

` 7.  You will now see the screen below. Click on the 'Save' button. After a few`\
` seconds click on the 'Publish' button, this pops up a smaller 'For Me' button`\
` that you need to click.`

[800px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-2.JPG "wikilink")

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-3.JPG>

` 8.  You will now enable the smartapp oAuth configuration. Please click on the `\
` App Settings button and scroll to the bottom of the next screen.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-4.JPG>

` 9.  Click on the 'OAuth' tab and then click the 'Enable oAuth in Smart App' button`\
` as seen below:`

[500px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-5.JPG "wikilink")

` 10.  Finally, click on 'Update'. Once this is complete you are done with the IDE`\
` Smart app install. Continue on for the Device Handler install instructions.`

[500px](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-6.JPG "wikilink")

------------------------------------------------------------------------

#### Device Handler - Install

` 1. Log into the IDE and click on the 'My Device Handlers' tab followed by the 'Create`\
` New Device Handler' and finally the 'From Code' tab.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-7.JPG>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-8.JPG>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-9.JPG>

` 2. Click on this link --> `[`DEVICE`` ``HANDLER`](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/devicetypes/tonesto7/echo-speaks-device.src/echo-speaks-device.groovy)` to obtain the Device Handler code. Click on the page that opens,`\
` press ctrl-A followed by ctrl-C. Then paste the code into the large white box as seen below, and then`\
`click the 'Create' button at the bottom of the screen.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-10.JPG>

` 3. Scroll to the top of the screen and click the 'Save' button, followed by the 'Publish' and the`\
` 'For Me' buttons`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-11.JPG>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/ES-12.JPG>

Using Git Integration
---------------------

*Enabling the GitHub Integration in the IDE is by far the easiest way to
install and get the latest updates for Echo Speak and Echo Speaks
devices.*

To enable Git integration (one time configuration) under the IDE please
visit here for instructions: ***[IDE GitHub Integration
Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)***

**NOTE: Git Integration is not currently available outside of US and
UK**

\
\

------------------------------------------------------------------------

Echo Speaks Smartapp Install/Configuration
==========================================

` 1. On your mobile device, open the SmartThings app. This tutorial will use`\
` the SmartThings Classic App. Tap on the 'Automations' tab.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/SA-1.jpg>

` 2. Scroll to the bottom of the screen and tap on 'Add a SmartApp'`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/SA-2.jpg>

` 3. Scroll to the bottom of the screen again and tap on 'My Apps'`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/SA-3.jpg>

` 4. Scroll until you find 'Echo Speaks' and tap on it to start the install process.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/SA-4.jpg>

` 5. You will now be in the main screen of the Echo Speaks smartapp. If you have Fire`\
` Tablets that you wish to use please activate the 'Create Devices for Tablets' toggle.`\
` Next, tap on the 'Next' in the top right corner of the screen.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-1.jpg>

` 6. Tap on the 'Select Local Hub' at the bottom of the screen and select your SmartThings `\
` Hub that you will be using for this install.`\
` `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-2.jpg>

` 7. The screen will change to what is shown below. Activate the toggle at the top to signify `\
` that you will be Heroku server service. If you have not already created a free Heroku app, you`\
` will need to follow the prompts to create it. `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-3.jpg>

` 8. Now tap on 'Save' in the top right corner. This will close the app. You will need to go `\
` into the Automation tab of the SmartThings mobile app and select Echo Speaks to finish the`\
` install. Tap on the 'Login Service Settings' block.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-4.jpg>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-4-1.jpg>

` 9. Ensure your Amazon domain is correct (green box) and then tap on the 'Begin Heroku Setup'`\
` shown in the red box below.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-5.jpg>

` 10. A browser window should open that looks like the one belows. Highlight and copy the information`\
` in the grey box for later use. Now tap on the 'Deploy to Heroku' button.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-6.jpg>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-7.jpg>

` 11. You will paste the information from the previous step into the box named 'App Name' `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-8.jpg>

` See the green box below. Your information is going to show that the app name is not available.`\
` YOU ABSOLUTELY MUST FOLLOW THE NEXT STEP EXACTLY AS WRITTEN.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-9.jpg>

` 12. **THIS MUST BE DONE PRECISELY. FAILURE TO FOLLOW THIS STEP MAY RESULT IN YOUR APP NOT OPERATING PROPERLY**`\
` Please tap on the app name you pasted to open your keyboard on your screen, and then tap the space bar. `\
` Now tap the back button to delete that space that you just typed in. You MUST remove the space.`\
` It should now turn green as pictured below:`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-10.jpg>

` 13. Now, scroll to the bottom of the screen and tap on the 'Deploy App' button. You will now see the information`\
` below as the app is deployed. The information will change. Think of the below picture as a movie.... the words will change.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-11.jpg>

` 14. Once the  screen looks like the one below, normally a minute or two after starting the above, tap on the`\
` 'Manage App' button.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-12.jpg>

` Now tap on the 'Open app' button at the top of the page. That will open the 2nd page shown below.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-13.jpg>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-14.jpg>

` 15. At this point, you have to make a choice. Do you want to manually install the required cookie, or`\
` do you want to have it done automatically. If you choose the manual open you will see this picture.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-15.jpg>

` Good choice, automatically is the recommended way to go.`\
` Tap on the 'Goto Login Page' as shown below.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-14.jpg>

` 16. Input your CORRECT login information for the Amazon account that your devices are registered with,`\
` then tap on 'Sign In'`\
` `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-16.jpg>

` When the above part is done successfully, you will se the screen below.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-17.jpg>

` 17. At this point, close the browser window and go back into the SmartThings mobile app. Open the `\
` Echo Speaks smartapp and wait a couple of minutes. Your app needs to sync with the server and this`\
` can sometimes take a couple of minutes. You will eventually see your Discovered Devices section `\
` auto populate.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-18.jpg>

` 18. Once you see your devices scroll down and tap on the 'Broadcast Test Page' `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-19.jpg>

` 19. Select the devices to test and activate the 'Perform the Broadcast' toggle. This will send a test`\
` text to speech command to selected devices.  If you hear it, good. If not, call Houston cause you have a problem.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/Install-20.jpg>

` Once you have completed these steps you are done. You can now use the devices in any of`\
` your automations.`\
` Congratulations!!`

------------------------------------------------------------------------

FAQ
===

What if I have more than one Amazon Account?
--------------------------------------------

` If you have multiple Amazon accounts with devices on each one, maybe the one in your home and`\
` the one in your parents home, you can simply perform a second complete install of the app and`\
` Heroku server. This will allow you to find all of your devices and they can be used in your`\
` SmartThings automations.`

------------------------------------------------------------------------

What Can I Do With My New Devices?
----------------------------------

` In this section, we will go over the capabilities of the device handlers, available commands`\
` and attributes, as well as some basic examples.`

### Attributes

#### Custom Attributes and Example Values

**lastUpdated:** (String)

-   *Dec 3, 2018 - 12:47:43 PM*

**deviceStatus:** (String)

-   *playing\_echo\_spot\_gen1*

**deviceType:** (String)

-   *A10A33FOX2NUBK*

**deviceStyle:** (String)

-   *Echo Spot*

**doNotDisturb:** (String)

-   *false*

**firmwareVer:** (String)

-   *625533420*

**onlineStatus:** (String)

-   *online*

**currentStation:** (String)

-   *Holiday Favorites Station*

**currentAlbum:** (String)

-   *Dean Martin*

**lastSpeakCmd:** (String)

-   *The Front Door Lock is unlocked*

**lastCmdSentDt:** (String)

-   *Mon Dec 03 09:16:09 EST 2018*

**trackImage:** (String)

-   *<https://m.media-amazon.com/images/I/71mwv+MFxSL._UL600_.jpg>*

**alarmVolume:** (Number)

-   *47*

**alexaWakeWord:** (String)

-   *ALEXA*

**wakeWords:** (Enum)

-   *\[\"ALEXA\",\"AMAZON\",\"ECHO\",\"COMPUTER\"\]*

**alexaPlaylists:** (JSON\_Object)

-   *\[\]*

**alexaNotifications:** (JSON\_Object)

-   \'\'{

`   "Home Audio": [`\
`      {`\
`          "title": "Home Audio",`\
`          "playlistId": "a1553cd7-f732-4f7e-ac3c-995b94860e57",`\
`          "entryList": null,`\
`          "version": "1",`\
`          "trackCount": 0`\
`      }`\
`   ]`\
` }''`

**alexaMusicProviders:** (JSON\_Object)

-   *\[CLOUDPLAYER:My Library, AMAZON\_MUSIC:Amazon Music,
    I\_HEART\_RADIO:iHeartRadio, PANDORA:Pandora, TUNEIN:TuneIn\]*

**volumeSupported:** (Boolean)

-   *true*

**ttsSupported:** (Boolean)

-   *true*

**musicSupported:** (Boolean)

-   *true*

**alarmSupported:** (Boolean)

-   *true*

**reminderSupported:** (Boolean)

-   *true*

------------------------------------------------------------------------

### Commands

#### replayText()

-   Replays the last text sent

#### doNotDisturbOn()

-   Turns OFF Do Not Disturb

#### doNotDisturbOff()

-   Turns ON Do Not Disturb

#### setVolumeAndSpeak(volume, message)

-   This command is highly recommend in place of sending an individual
    commands for setVolume(47) and speak(\"The Front door is unlocked\")
-   When used it is set to Amazon as a single command and executed in a
    sequence
-   Accepted Parameters:

` * Volume (Integer): Between 0 - 100`\
` * Message (String): String between 1-400 characters in length`

-   Example Usage: setVolumeAndSpeak(47, \"The Front door is unlocked\")

#### setVolumeSpeakAndRestore(volume, message)

-   This command is highly recommended in place of sending individual
    commands for setVolume(47), speak(\"The Front door is unlocked\"),
    and setVolume(30)
-   When used it is set to Amazon as a single command and executed in a
    sequence
-   When the command is called it captures the current volume and
    restores immediately after the message is played
-   Accepted Parameters:

` * Volume (Integer): Between 0 - 100`\
` * Message (String): String between 1-400 characters in length`

-   Example Usage: setVolumeSpeakAndRestore(47, \"The Front door is
    unlocked\")

#### setAlarmVolume(value)

-   Only available on device that support alarms and reminders
-   *Sets the Echo Devices Alarm and Reminder Notification volume*
-   Accepted Parameters:

` * Volume (Integer): Between 0 - 100`

#### playWeather()

-   *Alexa will give the current weather conditions*

#### playSingASong()

-   *Alexa will sing a random song*

#### playFlashBrief()

-   *Alexa will play the your flash briefing (If device supports it)*

#### playGoodMorning()

-   *Alexa will play the your good morning run down*

#### playTraffic()

-   *Alexa will give the current traffic condition on your way to work*

#### playTellStory()

-   *Alexa will tell a random story*

#### searchMusic(String searchPhrase, String providerId)

-   Used to play music from the desired music provider
-   The available providerId\'s are \[CLOUDPLAYER, AMAZON\_MUSIC,
    I\_HEART\_RADIO, PANDORA, SPOTIFY, TUNEIN\]
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * ProviderId (String): "AMAZON_MUSIC"`\
` * Volume (Integer): 30`

-   Example Usage: searchMusic(\"thriller\", \"AMAZON\_MUSIC\", 30)

#### searchAmazonMusic(\"item\_to\_search\_for\")

-   Used to search and play music from Amazon Music
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchAmazonMusic(\"thriller\", 30)

#### searchAppleMusic(\"item\_to\_search\_for\")

-   Used to search and play music from Amazon Music
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchAppleMusic(\"thriller\", 30)

#### searchPandora(\"item\_to\_search\_for\")

-   Used to search and play music from Pandora Music
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchPandora(\"thriller\", 30)

#### searchIheart(\"item\_to\_search\_for\")

-   Used to search and play music from iHeartRadio
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchIheart(\"thriller\", 30)

#### searchSiriusXm(\"item\_to\_search\_for\")

-   Used to search and play from Sirius XM Radio
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchSiriusXm(\"thriller\", 30)

#### searchSpotify(\"item\_to\_search\_for\")

-   Used to search and play music from Spotify Music
-   Accepted Parameters:

` * SearchPhrase (String): "thriller"`\
` * Volume (Integer): 30`

-   Example Usage: searchSpotify(\"thriller\", 30)

#### searchTuneIn(\"item\_to\_search\_for\")

-   Used to search and play music from TuneIn Radio
-   Accepted Parameters:

` * SearchPhrase (String): "WNIC"`\
` * Volume (Integer): 30`

-   Example Usage: searchPandora(\"WNIC\")

#### createAlarm(\"Alarm Label\", \"2018-11-14\", \"15:10\")

-   This will create audible alarms for a specific device.
-   Required Parameters:

` * 1. Label (String): It's used to visually identify the alarm in the Alexa App`\
` * 2. Date (String) This must be in this format (Year-Mon-Day)`\
` * 2. Time (String) This must be in this 24-hour time format (HH:MM)`

-   Example Usage: createAlarm(\"Alarm Label\", \"2018-12-03\",
    \"15:10\")

#### createReminder(\"Reminder Message\", \"2018-11-14\", \"15:40\")

-   This will create audible Reminder and read back the label as the
    reminder message
-   Required Parameters:

` * 1. Label (String): It's used to visually identify the alarm in the Alexa App`\
` * 2. Date (String) This must be in this format (Year-Mon-Day)`\
` * 2. Time (String) This must be in this 24-hour time format (HH:MM)`

-   Example Usage: createReminder(\"Remember to feed the dogs\",
    \"2018-12-03\", \"15:10\")

#### setWakeWord(\"ECHO\")

-   You can change the Wake Word for the device
-   You can use the attribute \"wakeWords\" to get the list of available
    words but currently they will only be
    \[\"ALEXA\",\"AMAZON\",\"ECHO\",\"COMPUTER\"\]
-   Accepted Parameters:

` * wakeword (String): "ECHO"`

-   Example Usage: setWakeWord(\"ECHO\")

TroubleShooting
===============

Remove Echo Speaks Server from Heroku
-------------------------------------

` There may come a time when you need to reset your Heroku server service. Follow these steps precisely to do it:`

` 1. Logon to the Heroku.com site. You should see this screen that lists your Echo Speaks App, click on the app.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-1.JPG>

` 2. Click on the 'Settings' button you see in the green box below:`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-2.JPG>

` 3. Scroll to the very bottom of the page and click on 'Delete App'`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-3.JPG>

` 4. Copy and paste the name of the app into the block and click on Delete. Now close this screen and go back to`\
` your mobile app.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-4.JPG>

` 5. Open the 'Echo Speaks' smartapp and click on the 'Echo Service Settings'`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-5.jpg>

` 6. At the bottom, you will see the 'Reset Service Data'. Toggle that switch and wait until you see this screen:`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-6.jpg>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-7.jpg>

------------------------------------------------------------------------

Redeploy the Echo Speaks Server
-------------------------------

` Redeploying the Echo Speaks server is a very simple process. Just follow these steps.`\
` **hint** it will take you longer to read these than to actually do it!`

` 1. Follow this link and follow the directions to remove the Heroku app and reset the`\
` Echo Speaks service in the smartapp.`\
` `[`https://tonesto7.github.io/echo-speaks/`` ``Remove`` ``Echo`` ``Speaks`` ``Server`](https://tonesto7.github.io/echo-speaks/_Remove_Echo_Speaks_Server "wikilink")

` 2. Now that you have deleted the Echo Speaks server and reset the smartapp, just follow`\
` this link to redeploy your service. You should be able to start the process on Step 8.`\
` `[`http://thingsthataresmart.wiki/index.php?title=Echo_Speaks#Echo_Speaks_Smartapp_Install.2FConfiguration`` ``Redeploy`` ``Echo`` ``Speaks`` ``Server`](http://thingsthataresmart.wiki/index.php?title=Echo_Speaks#Echo_Speaks_Smartapp_Install.2FConfiguration_Redeploy_Echo_Speaks_Server "wikilink")\
` `

------------------------------------------------------------------------

Errors
------

` Sometimes you may receive an error message. Hopefully it is one of the below and we already have a fix for you!`

### Status Code 400/401

` This failure is almost always due to the expiration of your cookie.  Simply clear the log in information for the Heroku App and log`\
` back in to Amazon. This should fix your issue right away.`\
` 1. Log into the Heroku.com website and click on your app. Then click on 'Settings' and scroll down to the 'Domains and Certificates'`\
` section (pictured below). Click on the link listed next to 'Domain'. It will take you to the Amazon Cookie Retrieval and should look`\
` like the second picture below. Just click to clear the log in information. `

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-8.JPG>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-9.JPG>

### Status Code 404

` Failed with status code 404 @line 890 (asyncCommandHandler) - The Heroku service will put an app to sleep once it detects no activity`\
` for a certain amount of time. The Echo Speaks system has a "Heart Beat" that talks to Heroku to keep the app awake. We are dealing `\
` with the cloud here, so sometimes there may be a delay in the Heart Beat, resulting in a nap for your server. To remedy this it is`\
` helpful to open the Web Config page, which normally wakes the service up. Sometimes this may take a couple of minutes. You can `\
` navigate easily to this page by following these steps. We recommend that you create a link to the page in case of any future incidents,`\
` then it will be a simple one click fix.`

` 1. Log into the Heroku.com website and click on your app. Then click on 'Settings' and scroll down to the 'Domains and Certificates'`\
` section (pictured below). Click on the link listed next to 'Domain'. It will take you to the Amazon Cookie Retrieval and should look`\
` like the second picture below. You should then be good to go. If it wants you to log in again, then just do it and you should then be good.`

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-8.JPG>

<https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/TS-9.JPG>

------------------------------------------------------------------------

### Request Error

\_\_/ap/cvf/request error\_\_

`   If you see this error when you are attempting to do the Amazon Login part of the server deployment`\
`   We have found this to be due to having had the Amazon 2FA (2 Step Verfication) service activated, and then later turned off.`\
`   Unfortunately, once you have had this service turned on, it will have to be on for Echo Speaks to work properly. You may or`\
`   may not remember ever having it turned on in the past, but with the presence of this error, it is very possible. To remedy this`\
`   error you will need to turn the 2FA service back on and redeploy the server.`

------------------------------------------------------------------------

Donations
=========

-   While donations are not required they are very much appreciated.
-   Here is the ***[donation
    link](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HWBN4LB9NMHZ4)***

![](https://raw.githubusercontent.com/tonesto7/echo-speaks/master/resources/wiki_images/echospeaks_donation_qr.png "wikilink" | width=150px)
