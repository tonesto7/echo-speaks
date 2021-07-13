### _**Release (v4.1.9.0) - [July 13th, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Bugfixes and Optimizations.
- [UPDATE] Modified the Actions UI to show multiple responses in a cleaner fashion.
- [NEW] Added Bundle zip file for importing App and Driver code via HE interface in Platform v2.2.8+.

### _**Release (v4.1.8.0) - [June 22st, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Bugfixes and Optimizations.
- [NEW] Added support for not_between for conditions and triggers in ES Actions.

### _**Release (v4.1.7.0) - [May 6th, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Bugfixes and Optimizations.
- [NEW] Added support for parallel speaks in ES Actions.

### _**Release (v4.1.6.0) - [April 20th, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Bugfixes and Optimizations.

### _**Release (v4.1.5.0) - [April 14th, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Icons are updated for keypad, buttons, and accelorometers.
- [FIX] Added some missing echo device icons for newer models
- [FIX] Significant Bugfixes and Optimizations for Actions and Zones.

### _**Release (v4.1.4.0) - [April 8th, 2021]**_

#### _***All Apps and Devices***_
- [FIX] Modified the changelog format to display correctly now.
- [FIX] Modified the speech string cleanup to resolve some issues with time and other values in speak commands.
- [FIX] Bugfixes and Optimizations

### _**Release (v4.1.3.0) - [April 5th, 2021]**_

#### _***All Apps and Devices***_

- [FIX] Bugfixes and Optimizations

### _**Release (v4.1.2.0) - [April 2nd, 2021]**_

#### _***All Apps and Devices***_

- [FIX] Bugfixes and Optimizations

### _**Release (v4.1.0.0) - [March 22nd, 2021]**_

#### _***All Apps and Devices***_

- [FIX] Bugfixes and Optimizations

### _**Release (v4.1.1.0) - [March 30th, 2021]**_

#### _***All Apps and Devices***_

- [FIX] Bugfixes and Optimizations
  
### _**Release (v4.1.0.1) - [March 25th, 2021]**_

#### _***All Apps and Devices***_

- [FIX] Bugfixes and Optimizations

### _**Release (v4.1.0.0) - [March 22nd, 2021]**_

#### _***Echo Speaks App***_

- [UPDATE] Reduced overhead and resource usage
- [UPDATE] Add new echo devices
- [UPDATE] Optimizations
- [UPDATE] Reduced accesses to AWS Alexa APIs by caching results for re-use
- [UPDATE] Removed a lot of state and atomicState accesses
- [UPDATE] Many JVM optimizations to reduce memory use in HE
- [UPDATE] More use of asyncHttp calls
- [NEW] Centralized speech queue to eliminate rate-limiting.
- [NEW] Speech command optimizations to try and group like commands
- [NEW] Restored Zone and Action duplications.
- [UPDATE] UI improvements
- [UPDATE] Improved status reporting and descriptions
- [UPDATE] Cleanup of use of local server deployments
- [UPDATE] Improved cookie refresh operations (in conjunction with server updates)
- [UPDATE] Fixes for conditions, restrictions handling, time based conditions and restrictions
- [UPDATE] Updates for operation with HSM,
- [UPDATE] Added support for new Echo devices
- [UPDATE] Significant reduction of platform resource usage.
  
#### _***Echo Speaks Actions***_

- [UPDATE] Integration with lock codes and security keypads, humidity sensors
- [UPDATE] Ability to filter actions on specific security code usage
- [UPDATE] Updates for operation with HSM
- [UPDATE] Integration with webCoRE
- [UPDATE] Ability to execute pistons
- [UPDATE] Ability to trigger based on piston executions
- [UPDATE] UI improvements
- [UPDATE] Improved status reporting and descriptions
- [UPDATE] Fixes for conditions, restrictions handling, time based conditions and restrictions
- [UPDATE] Optimizations
- [UPDATE] Removed a lot of state and atomicState accesses
- [UPDATE] Many JVM optimizations to reduce memory use in HE

#### _***Echo Speaks Zones***_

- [UPDATE] Optimizations
- [UPDATE] Improved time handling and transitioning
- [UPDATE] Improved UI
- [NEW] Create a virtual device for each zone to use in 3rd-party apps.

#### _***Echo Speaks Device***_

- [UPDATE] Improved labeling for command inputs
- [UPDATE] Significant reduction of platform resource usage.
- [NEW] Added new voiceCmdAsText() command to execute commands as if they are spoken to the device

#### _***Echo Speaks Zones Device***_

- [NEW] Device can be used in 3rd Party apps like WebCoRE or Rule Machine

#### _***Echo Speaks WebSocket***_

- [UPDATE] Significant reduction of platform resource usage.
- [UPDATE] Improved operations with websockets.
