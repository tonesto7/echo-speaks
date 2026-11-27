### _**Release (v4.3.0.3) - [November 27th, 2026]**_

#### _***Echo Speaks App***_

- [NEW] The app now checks that the Echo Speaks Server has its current callback URL and sends the correct one when it is stale (for example after a hub replacement or backup restore, an app reinstall or an access token reset). Previously every server-to-hub call failed with a 404 until the URL was re-entered by hand. The check runs on initialize and every 6 hours from the health check.
- [NEW] The local hub endpoint URL is now shown as an alternative to the cloud URL on the diagnostics page and the server setup page, and the server can use either one. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [NEW] The app now appears under the Integrations menu. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [FIX] Fixed announcements failing with "SecurityException: Computed device command 'finishSendSpeak' is not allowed" on newer Hubitat firmware. Thanks @kahn-hubitat, @ndmaxwell and @mr_fy.
- [UPDATE] The automatic cookie refresh interval can now be set from 1 to 90 days (was 1 to 5). The default is still 5 days.
- [FIX] The cloud endpoint URL now always uses HTTPS (the hub could return an http:// address, which broke the Amazon login redirect). Thanks @howarddavidp.
- [FIX] Corrected the Echo Dot (Gen5) and Echo Spot 2 model names and added missing Echo Dot Gen4/Gen5 icons. Thanks @jshimota01.
- [UPDATE] Added device support for the Echo Show 8 (Gen4), Echo Show 11 (Gen4) and Echo Show 21 (Gen1), with new icons. Thanks @jshimota01.
- [NOTE] Echo Speaks Server v2.8.1 is recommended. It updates the Amazon login to support Amazon's newer sign-in pages and requires Node.js 20 or newer.

#### _***Echo Speaks Actions***_

- [FIX] Fixed the webCoRE piston list for the current webCoRE piston data format. Thanks @imnotbob.

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
  
