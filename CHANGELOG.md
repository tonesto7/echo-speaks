# _**v4.0.1.0**_
## _**Echo Speaks App**_
- [UPDATE] Updated minimum code versions for Echo Device and WebSocket.
- [UPDATE] Lot's of code cleanups
- [FIX] Fixed guardData response error when guard is not enabled on Alexa account.

## _**Echo Speaks Actions**_
- [UPDATE] Lot's of code cleanups
- [FIX] Fixed issue with being able to unpause action.
- [FIX] Fixed issue with voicecmd actiontypes.

## _**Echo Speaks Zones**_
- [UPDATE] Lot's of code cleanups
- [FIX] Fixed issue with relaying voicecmds.

## _**Echo Speaks Device**_
- [FIX] VoiceActivity should now update the state correctly.
- [FIX] WebSocket fixes.
- [UPDATE] Rename voicecmd sequence string to voicecmdtxt.

## _**Echo Speaks WebSocket**_
- [FIX] Fixed Issue with WebSocket not working in v4.x.
  
# _**v4.0.0.1**_
## _**Echo Speaks App**_
- [FIX] Heroku app.json missing when using Beta branch.

# _**v4.0.0.0**_
## _**Echo Speaks App**_
- [UPDATE] Reduced overhead and resource usage
- [UPDATE] Add new echo devices
- [UPDATE] Optimizations
- [UPDATE] Reduced accesses to AWS Alexa APIs by caching results for re-use
- [UPDATE] Removed a lot of state and atomicState accesses
- [UPDATE] Many JVM optimizations to reduce memory use in HE
- [UPDATE] More use of asyncHttp calls
- [UPDATE] UI improvements
- [UPDATE] Improved status reporting and descriptions
- [UPDATE] Cleanup of use of local server deployments
- [UPDATE] Improved cookie refresh operations (in conjunction with server updates)
- [UPDATE] Fixes for conditions, restrictions handling, time based conditions and restrictions
- [UPDATE] Updates for operation with HSM,
- [ADDED] Restored Zone and Action duplications.
- [UPDATE] Added support for new Echo devices

## _**Echo Speaks Actions**_
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

## _**Echo Speaks Zones**_
- [UPDATE] Optimizations
- [UPDATE] Improved time handling and transitioning
- [UPDATE] Improved UI

## _**Echo Speaks Device**_
- [UPDATE] Improved labeling for command inputs
- [UPDATE] Less overhead
- [NEW] Added new voiceCmdAsText() command to execute commands as if they are spoken to the device

## _**Echo Speaks WebSocket**_
- [UPDATE] Less overhead
- [UPDATE] Improved operations with websockets
