### _**Release (v4.3.0.3) - [November 27th, 2026]**_

#### _***Echo Speaks App***_

- [NEW] The app now checks that the Echo Speaks Server has its current callback URL and sends the correct one when it is stale (for example after a hub replacement or backup restore, an app reinstall or an access token reset). Previously every server-to-hub call failed with a 404 until the URL was re-entered by hand. The check runs on initialize and every 6 hours from the health check.
- [NEW] The local hub endpoint URL is now shown as an alternative to the cloud URL on the server setup page and in the Server Callback URLs section, and the server can use either one. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [NEW] The app now appears under the Integrations menu. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [FIX] Fixed announcements failing with "SecurityException: Computed device command 'finishSendSpeak' is not allowed" on newer Hubitat firmware. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [UPDATE] The automatic cookie refresh interval can now be set from 1 to 90 days (was 1 to 5). The default is still 5 days.
- [FIX] The cloud endpoint URL now always uses HTTPS (the hub could return an http:// address, which broke the Amazon login redirect). Thanks @howarddavidp.
- [FIX] Corrected the Echo Dot (Gen5) and Echo Spot 2 model names and added missing Echo Dot Gen4/Gen5 icons. Thanks @jshimota01.
- [UPDATE] Added device support for the Echo Show 8 (Gen4), Echo Show 11 (Gen4) and Echo Show 21 (Gen1), with new icons. Thanks @jshimota01.
- [NOTE] Echo Speaks Server v2.8.1 is recommended. It updates the Amazon login to support Amazon's newer sign-in pages and requires Node.js 20 or newer.
- [SECURITY] Amazon cookies, CSRF tokens and request headers are no longer written to the logs.
- [SECURITY] The diagnostics page no longer offers a shareable link (it contained the app's access token, which also grants access to the Amazon login). Use the new "Copy Diagnostics" button and paste the text instead.
- [NEW] Manage Cookie Login Service now has a "Server Callback URLs" section (hidden until tapped) showing the cloud and local hub URLs the server can use, which kind the server currently uses, a button to check it now, and buttons to switch the server between the cloud and local URL (local is offered only for servers on your network). The callback URL in use is also shown in the Manage Cookie Login Service summary and under Server Management.
- [UPDATE] Server Host Override now sits below the Amazon Login Page link with a description of when to use it, and "(override)" is shown next to the server host when it is set.
- [NEW] Added a "Reset Access Token" button (Settings > Access Token). Previously shared app links stop working and the server is sent the new callback URL automatically.
- [FIX] The server callback URL check no longer replaces a working URL that uses a hostname, port 8443 or a reverse proxy, never sends a URL without an access token, and backs off after failed updates instead of retrying every 15 minutes. When the server uses the local URL and the hub's IP address changes, the check now sends the local URL with the new IP.
- [FIX] Automatic cookie refresh now runs when the refresh interval is set above 11 days.
- [FIX] One failed or deleted device no longer stalls the speech queue.
- [FIX] Speaking with a restore volume no longer reports the spoken text twice.
- [FIX] Fixed a lock override that could let two tasks run at the same time.
- [FIX] A local server that does not report "onHeroku" is no longer treated as a Heroku server, and the server URL it reports is preferred over the guessed herokuapp.com address.
- [FIX] Cookie validation now reports an unauthenticated cookie as invalid, and the Auth Status page no longer waits on Amazon each time it opens.
- [FIX] Echo Show 8 (Gen3) now uses the Echo Show 8 icon.
- [UPDATE] The in-memory caches in the app, Actions and Zones are now thread-safe (ConcurrentHashMap with copy-on-write updates), are cleared on uninstall, and server settings are cached instead of being re-read from the database on every check.
- [FIX] Actions now receive the zone list when the app rebuilds its zone cache (it passed an undefined value).
- [FIX] The local hub endpoint URL shown on the server setup page now has the correct format (http://<hub IP>/apps/api/<app id>/...).

#### _***Echo Speaks Actions***_

- [FIX] Fixed the webCoRE piston list for the current webCoRE piston data format. Thanks @imnotbob.
- [FIX] Fixed the Triggers page failing to open (leftover Alexa Guard code). The Alexa Guard trigger was removed.
- [FIX] Fixed device commands failing on newer Hubitat firmware ("Computed device command ... is not allowed").
- [FIX] "All devices" triggers now wait until every selected device matches.
- [FIX] Sunrise/sunset offsets are applied in minutes (they were applied as seconds).
- [FIX] Fixed-time conditions and quiet times now use today's date.
- [FIX] A threshold of 0 (for example "temperature below 0") now works.
- [FIX] Scheduled triggers: month filters work for January-September, Weekly/Monthly schedules are no longer limited to one run per week or month, and One-Time schedules run once (changing the trigger time re-arms them).
- [FIX] "Only once per day" and "wait" restrictions now apply to mode, alarm and webCoRE triggers, and only events that actually match the trigger count toward them.
- [FIX] Light flashing with two colors now alternates colors.
- [FIX] Music searches no longer stop after a number of seconds equal to the restore volume.
- [UPDATE] Locks are released even when an error occurs, and a failure in delayed ("after") trigger handling no longer leaves the action blocked.
- [UPDATE] After a code update, the actions send one webCoRE piston list request between them instead of one per action.

#### _***Echo Speaks Zones***_

- [FIX] Numeric conditions (battery, temperature, level, etc.) can be configured again.
- [FIX] Fixed zone commands failing on newer Hubitat firmware ("Computed device command ... is not allowed"), including music, "Say Goodbye" and the playback Volume action.
- [FIX] Zone volume up/down now changes the Echo volumes, and Play Sound / Canned TTS on a zone device now play.
- [FIX] Fixed-time conditions now schedule correctly, and sunrise/sunset offsets are applied in minutes.
- [FIX] A zone change is cancelled if the conditions flip back during the activation/deactivation delay, and frequent sensor events no longer keep restarting the delay.
- [FIX] Two events arriving at the same moment no longer send duplicate zone notifications or switch commands.
- [FIX] "Deactivate immediately" now works for Alarm (HSM) conditions, and a threshold of 0 now works.
- [FIX] Clearing one zone's history no longer clears the history of every zone.
- [FIX] Announcements that ignore Do Not Disturb (playAnnouncementIgnore) now also bypass DND when sent to a zone.

#### _***Echo Speaks Device***_

- [NEW] Declared the followUpModeOn/Off, repeat, shuffle and playCannedRandomTts commands; the followUpMode attribute now updates when the change is accepted.
- [FIX] Announcements to several devices now set the volume on each target device.
- [FIX] Built-in phrases and sounds (weather, jokes, good morning, etc.) now respect Do Not Disturb, authentication and offline status like speak() on devices that support text to speech.
- [FIX] Alarm volume, Do Not Disturb and wake word only change on the device after Amazon accepts the change.
- [FIX] Mute status is always reported, and an offline device no longer triggers a refresh on every status update.
- [FIX] Play/pause/next/previous refresh only the playback state instead of all device data.
- [FIX] Speech attributes are no longer reset to "NA" after every cookie refresh.
- [FIX] Fixed "every X days" reminders, the reminder time zone, alarm labels losing their spaces, removing the first created notification and the reminders permission check.

#### _***Build and Packaging***_

- [UPDATE] The Echo Speaks Zone Device driver is now generated from the Echo Speaks Device driver (scripts/gen-zone-driver.py), and the Hubitat bundle is rebuilt from the current code (scripts/build-bundle.py). A CI check keeps both in sync.
- [REMOVED] Removed the unused libraries folder.

### _**Release (v4.3.0.1) - [September 10th, 2025]**_

#### _***All Apps and Devices***_

- [FIX] Fixed 404 errors on devices with getPlaybackState command (Thanks @kahn-hubitat).
- [FIX] Fixed issue with code version checking when devices or apps are disabled in hubitat.
- [FIX] Merged in @imnotbobs pull request for cleanups post alexa guard removal.

### _**Release (v4.3.0.0) - [September 9th, 2025]**_

#### _***All Apps and Devices***_

- [FIX] Added server host override Thanks @jtp10181.
- [FIX] Added support for 5th gen echo dot and other devices. Thanks @jtp10181.
- [REMOVED] Removed Alexa Guard features as they are no longer supported by Amazon.

### _**Release (v4.2.4.0) - [March 7th, 2024]**_

#### _***All Apps and Devices***_

- [FIX] Removed getDeviceActivity errors @nh_shotfam.
- [NEW] Added support for newer devices.
- [FIX] Added this placeholder for testing

### _**Release (v4.2.3.0) - [August 21st, 2023]**_

#### _***All Apps and Devices***_

- [FIX] Fixes submitted by @nh_shotfam.
- [FIX] Fixed some ui issues with the Actions app.
- [FIX] Disabled the GetPlaylists command for now as it's not working.

### _**Release (v4.2.2.0) - [December 22nd, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Fixes submitted by @nh_shotfam.
- [FIX] Fixed some ui issues with the Actions app.

### _**Release (v4.2.1.2) - [December 20th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Fixes for cloneing zones and actions.
- [NEW] Support for 5th gen echo dot and other devices.
- [FIX] Cleanups and optimizations.
  
### _**Release (v4.2.0.8) - [November 28th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Tweaks for Local Server Installs.

### _**Release (v4.2.0.7) - [May 5th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Fix for new heroku deployments not working.

### _**Release (v4.2.0.6) - [May 5th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Fix for latest version not showing it's up to date.

### _**Release (v4.2.0.5) - [April 28th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] NoOp command description error

### _**Release (v4.2.0.4) - [April 21st, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Fixed version requirement from last update.

### _**Release (v4.2.0.3) - [April 21st, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Volume Restore issue resolved.
- [FIX] Bug fixes and optimizations.

### _**Release (v4.2.0.2) - [April 18th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Volume Restore issue resolved.
- [FIX] Bug fixes and optimizations.

### _**Release (v4.2.0.1) - [April 14th, 2022]**_

#### _***All Apps and Devices***_

- [FIX] Volume Restore issue resolved.
- [FIX] Bug fixes and optimizations.

### _**Release (v4.2.0.0) - [April 1st, 2022]**_

#### _***All Apps and Devices***_

- [NEW] Updated notification quiet time restrictions to match the same code structure as those in ES Actions.
- [NEW] Added new commands for speech and announcements to bypass alexa do not disturb restrictions to speak for emergency scenarios.
- [NEW] Added the ability to eliminate duplicate notifications for certain zigbee locks.  There is a toggle in the actions settings for locks only.
- [NEW] Updated device support list with latest devices reported.
- [FIX] Added new setting to device to ignore device online/offline status (On by default).
- [REMOVED] Removed WebSocket device until I get time to update it or move it to a local server.
  
