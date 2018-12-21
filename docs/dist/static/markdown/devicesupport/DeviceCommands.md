### Commands

#### replayText()

-   Replays the last text sent

#### doNotDisturbOn()

-   Turns OFF Do Not Disturb

#### doNotDisturbOff()

-   Turns ON Do Not Disturb

#### setVolumeAndSpeak(volume, message)

-   This command is highly recommend in place of sending an individual commands for setVolume(47) and speak(\"The Front door is unlocked\")
-   When used it is set to Amazon as a single command and executed in a sequence
-   Accepted Parameters:

` * Volume (Integer): Between 0 - 100`\
` * Message (String): String between 1-400 characters in length`

-   Example Usage: setVolumeAndSpeak(47, \"The Front door is unlocked\")

#### setVolumeSpeakAndRestore(volume, message)

-   This command is highly recommended in place of sending individual commands for setVolume(47), speak(\"The Front door is unlocked\"), and setVolume(30)
-   When used it is set to Amazon as a single command and executed in a sequence
-   When the command is called it captures the current volume and restores immediately after the message is played
-   Accepted Parameters:

` * Volume (Integer): Between 0 - 100`\
` * Message (String): String between 1-400 characters in length`

-   Example Usage: setVolumeSpeakAndRestore(47, \"The Front door is unlocked\")

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
-   The available providerId\'s are \[CLOUDPLAYER, AMAZON\_MUSIC, I\_HEART\_RADIO, PANDORA, SPOTIFY, TUNEIN\]
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

-   Example Usage: createAlarm(\"Alarm Label\", \"2018-12-03\", \"15:10\")

#### createReminder(\"Reminder Message\", \"2018-11-14\", \"15:40\")

-   This will create audible Reminder and read back the label as the reminder message
-   Required Parameters:

` * 1. Label (String): It's used to visually identify the alarm in the Alexa App`\
` * 2. Date (String) This must be in this format (Year-Mon-Day)`\
` * 2. Time (String) This must be in this 24-hour time format (HH:MM)`

-   Example Usage: createReminder(\"Remember to feed the dogs\", \"2018-12-03\", \"15:10\")

#### setWakeWord(\"ECHO\")

-   You can change the Wake Word for the device
-   You can use the attribute \"wakeWords\" to get the list of available words but currently they will only be \[\"ALEXA\",\"AMAZON\",\"ECHO\",\"COMPUTER\"\]
-   Accepted Parameters:

` * wakeword (String): "ECHO"`

-   Example Usage: setWakeWord(\"ECHO\")
