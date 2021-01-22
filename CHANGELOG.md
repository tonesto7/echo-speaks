## v4.0.0
- [UPDATE] Reduced overhead and resource usage
- [UPDATE] Add new echo devices

### Echo Speaks App
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

### Echo Speaks Actions
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

### Echo Speaks Zones
- [UPDATE] Optimizations
- [UPDATE] Improved time handling and transitioning
- [UPDATE] Improved UI

### Echo Speaks Device
- [UPDATE] Improved labeling for command inputs
- [UPDATE] Less overhead
- [NEW] Added new voiceCmdAsText() command to execute commands as if they are spoken to the device

### Echo Speaks WebSocket
- [UPDATE] Less overhead
- [UPDATE] Improved operations with websockets