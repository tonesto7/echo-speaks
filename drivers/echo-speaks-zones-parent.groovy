/**
 *	Echo Speaks Zones Device (Hubitat ONLY)
 *
 *  Copyright 2021 Anthony Santilli
 *  Code Contributions by @nh.schottfam
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

 /* TODO: 
    Use a call to the parent to get the token and compare it with the current field variable and update the variable if they are different
*/

import groovy.transform.Field

// STATICALLY DEFINED VARIABLES
@Field static final String devVersionFLD  = "4.0.8.0"
@Field static final String appModifiedFLD = "2021-02-26"
@Field static final String branchFLD      = "master"
@Field static final String platformFLD    = "Hubitat"
@Field static final Boolean betaFLD       = false
@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sLINEBR        = '<br>'
@Field static final String sTRUE          = 'true'
@Field static final String sFALSE         = 'false'
@Field static final String sMEDIUM        = 'medium'
@Field static final String sSMALL         = 'small'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sAPPJSON       = 'application/json'

// IN-MEMORY VARIABLES (Cleared only on HUB REBOOT or CODE UPDATES)
@Field volatile static Map<String,Map> historyMapFLD = [:]
@Field volatile static Map<String,Map> cookieDataFLD = [:]

static String devVersion()  { return devVersionFLD }
static Boolean isWS()       { return false }
static Boolean isZone()     { return true }

metadata {
    definition (name: "Echo Speaks Zones Parent", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/beta/drivers/echo-speaks-zone-parent-device.groovy") {
        capability "Refresh"
        capability "Sensor"
        capability "Switch"
    }

    preferences {
        section("Preferences") {
            input "logInfo", "bool", title: "Show Info Logs?",  required: false, defaultValue: true
            input "logWarn", "bool", title: "Show Warning Logs?", required: false, defaultValue: true
            input "logError", "bool", title: "Show Error Logs?",  required: false, defaultValue: true
            input "logDebug", "bool", title: "Show Debug Logs?", description: "Only leave on when required", required: false, defaultValue: false
            input "logTrace", "bool", title: "Show Detailed Logs?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false
        }
    }
}

def installed() {
    logInfo("${device?.displayName} Executing Installed...")
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "status", value: "stopped")
    sendEvent(name: "lastSpeakCmd", value: "Nothing sent yet...")
    initialize()
}

def updated() {
    logInfo("${device?.displayName} Executing Updated()")
    initialize()
}

def initialize() {
    logInfo("${device?.displayName} Executing initialize()")
    unschedule()
    state.refreshScheduled = false
    stateCleanup()
    if(minVersionFailed()) { logError("CODE UPDATE required to RESUME operation.  No Device Events will updated."); return }
    // schedDataRefresh(true)
    if(advLogsActive()) { runIn(1800, "logsOff") }
    refreshData(true)
}

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace) }
public void logsOff() {
    device.updateSetting("logDebug",[value:sFALSE,type:"bool"])
    device.updateSetting("logTrace",[value:sFALSE,type:"bool"])
    log.debug "Disabling debug logs"
}

public Boolean childExists(ep) {
    List children = childDevices
    def childDevice = children.find{it.deviceNetworkId.endsWith(ep)}
    return (childDevice) ? true : false
}

private addChild(String id, String label, String namespace, String driver, Boolean isComponent){
    if((Boolean)!childExists(id)){
        try {
            def newChild = addChildDevice(namespace, driver, "${device.deviceNetworkId}-${id}", [completedSetup: true, label: "${device.displayName} (${label})", isComponent: isComponent, componentName: id, componentLabel: label])
            newChild.sendEvent(name:"switch", value:"off")
        } catch (e) {
            log.error "addChild Error, ${e}"
        }
    }
}

private deleteChild(String id){
    if((Boolean)childExists(id)){
        def childDevice = childDevices.find{it.deviceNetworkId.endsWith(id)}
        try {
            if(childDevice) deleteChildDevice(childDevice.deviceNetworkId)
        } catch (e) {
            if (infoEnable) log.info "Hubitat may have issues trying to delete the child device when it is in use. Need to manually delete them."
        }
    }
}

public createChildZone(String zoneId, String name) {
    addChild("es_zone_${zoneId}", name, "tonesto7", "Echo Speaks Zone Child", true)
}

public removeChildZone(String zoneId) {
    deleteChild(zoneId.toString())
}

public updateChildLabel(String id, String label) {
    List children = childDevices
    def childDevice = children.find{it.deviceNetworkId.endsWith(id)}
    if(childDevice) { childDevice.setLabel(label) }
}

public updateChildZoneState(String id, Boolean zoneActive) {
    def childZone = childDevices.find{it.deviceNetworkId.endsWith(id)}
} 

public triggerInitialize() { runIn(3, "initialize") }

String getHealthStatus(Boolean lower=false) {
    String res = device?.getStatus()
    if(lower) { return res?.toLowerCase() }
    return res
}

void updateDeviceStatus(Map devData) {
    Boolean isOnline = true
    setOnlineStatus(isOnline)
    sendEvent(name: "lastUpdated", value: formatDt(new Date()), display: false, displayed: false)
    schedDataRefresh()
}

void refresh() {
    logTrace("refresh()")
    parent?.childInitiatedRefresh()
    triggerDataRrsh('refresh')
//    refreshData(true)
}

private void triggerDataRrsh(String src, Boolean parentRefresh=false) {
    logTrace("triggerDataRrsh $src $parentRefresh")
    runIn(6, parentRefresh ? "refresh" : "refreshData1")
}

void refreshData1() {
    refreshData()
}

private void triggerDataRrshF(String src) {
    logTrace("triggerDataRrsh $src $parentRefresh")
    runIn(6, "refreshData2")
}

void refreshData2() {
    refreshData(true)
}

public schedDataRefresh(Boolean frc=false) {
    if(frc || !(Boolean)state.refreshScheduled) {
        runEvery30Minutes("refreshData")
        state.refreshScheduled = true
    }
}

void refreshData(Boolean full=false) {
    logTrace("refreshData($full)...")

}

public setOnlineStatus(Boolean isOnline) {
    String onlStatus = (isOnline ? "online" : "offline")
    if(isStateChange(device, "onlineStatus", onlStatus?.toString())) {
        logDebug("OnlineStatus has changed to (${onlStatus})")
        sendEvent(name: "onlineStatus", value: onlStatus?.toString(), display: true, displayed: true)
    }
}


/*******************************************************************
            Device Command FUNCTIONS
*******************************************************************/
public relayMultiSeqCommand(List commands, String srcDesc, Boolean parallel=false) {
//  This is just a template for now this code does not work
//  void queueMultiSequenceCommand(List<Map> commands, String srcDesc, Boolean parallel=false, Map deviceData=[:], Map cmdMap=[:], String device=sNULL, String callback=sNULL) {
    parent.queueMultiSequenceCommand(commands, srcDesc, parallel, [deviceType: (String)state.deviceType, serialNumber: (String)state.serialNumber], null, device.deviceNetworkId)
}

public relaySeqCommand(String type, String command, value=null) {
//  This is just a template for now this code does not work
//  void queueSequenceCommand(String type, String command, value, Map deviceData=[:], String device=sNULL, String callback=sNULL)
    //parent.queueSequenceCommand(type, command, value, (Map) deviceData=[:], String device=sNULL, String callback=sNULL)
}

public relaySpeakCommand(Map cmdMap, Map devObj, String callback) {
//  This is just a template for now this code does not work
    parent.sendSpeak(cmdMap, device.deviceNetworkId, "finishSendSpeak")
}

/*******************************************************************
            Speech Queue Logic
*******************************************************************/

private void processLogItems(String t, List ll, Boolean es=false, Boolean ee=true) {
    if(t && ll?.size() && settings?.logDebug) {
        if(ee) { "log${t?.capitalize()}"(" ") }
        "log${t?.capitalize()}"("└─────────────────────────────")
        ll?.each { "log${t?.capitalize()}"(it) }
        if(es) { "log${t?.capitalize()}"(" ") }
    }
}

@Field static final List<String> clnItemsFLD = [
    "qBlocked", "qCmdCycleCnt", "useThisVolume", "lastVolume", "lastQueueCheckDt", "loopChkCnt", "speakingNow",
    "cmdQueueWorking", "firstCmdFlag", "recheckScheduled", "cmdQIndexNum", "curMsgLen", "lastTtsCmdDelay",
    "lastQueueMsg", "lastTtsMsg",
    "q_blocked",
    "q_cmdCycleCnt",
    "q_lastCheckDt",
    "q_loopChkCnt",
    "q_speakingNow",
    "q_cmdWorking",
    "q_firstCmdFlag",
    "q_recheckScheduled",
    "q_cmdIndexNum",
    "q_curMsgLen",
    "q_lastTtsCmdDelay",
    "q_lastTtsMsg",
    "q_lastMsg"
]

private void stateCleanup() {
    state.newVolume = null
    state.oldVolume = null
//    if(state.lastVolume) { state?.oldVolume = state?.lastVolume }
    clnItemsFLD.each { String si-> if(state.containsKey(si)) { state.remove(si)} }
}


/*****************************************************
                HELPER FUNCTIONS
******************************************************/
//static String getAppImg(String imgName) { return "https://raw.githubusercontent.com/tonesto7/echo-speaks/${betaFLD ? "beta" : "master"}/resources/icons/$imgName" }
static Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }
Boolean minVersionFailed() {
    try {
        Integer t0 = ((Map<String,Integer>)parent?.minVersions())["echoDevice"]
        Integer minDevVer = t0 ?: null
        return minDevVer != null && versionStr2Int(devVersionFLD) < minDevVer
    } catch (e) { 
        return false
    }
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now, false)
}

String getIsoDtNow() {
    def tf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(new Date())
}

String  formatDt(Date dt, Boolean mdy = true) {
    String formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
    def tf = new java.text.SimpleDateFormat(formatVal)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf.format(dt)
}

Long GetTimeDiffSeconds(String strtDate, String stpDate=sNULL) {
    if((strtDate && !stpDate) || (strtDate && stpDate)) {
        Date now = new Date()
        String stopVal = stpDate ? stpDate : formatDt(now, false)
        Long start = Date.parse("E MMM dd HH:mm:ss z yyyy", strtDate).getTime()
        Long stop = Date.parse("E MMM dd HH:mm:ss z yyyy", stopVal).getTime()
        Long diff =  ((stop - start) / 1000L)
        return diff
    } else { return null }
}

String parseFmtDt(String parseFmt, String newFmt, String dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location?.timeZone) { tf.setTimeZone(location?.timeZone) }
    return tf?.format(newDt)
}

Boolean ok2Notify() {
    return (Boolean)parent?.getOk2Notify()
}

private void logSpeech(String msg, Integer status, String error=sNULL) {
    Map o = [:]
    if(status) o.code = status
    if(error) o.error = error
    addToLogHistory("speechHistory", msg, o, 5)
}

private Integer stateSize() { String j = new groovy.json.JsonOutput().toJson(state); return j.length() }
private Integer stateSizePerc() { return (Integer) (((stateSize() / 100000)*100).toDouble().round(0)) }

private void addToLogHistory(String logKey, String msg, statusData, Integer max=10) {
    Boolean ssOk = true //(stateSizePerc() <= 70)
    String appId = device.getId()

    Map memStore = historyMapFLD[appId] ?: [:]
    List eData = (List)memStore[logKey] ?: []
    if(eData?.find { it?.message == msg }) { return }
    if(status) { eData.push([dt: getDtNow(), message: msg, status: statusData]) }
    else { eData.push([dt: getDtNow(), message: msg]) }
    Integer lsiz=eData.size()
    if(!ssOk || lsiz > max) { eData = eData.drop( (lsiz-max) ) }
    updMemStoreItem(logKey, eData)
}

public Map getLogConfigs() {
    return [
        info: (Boolean) settings.logInfo,
        warn: (Boolean) settings.logWarn,
        error: (Boolean) settings.logError,
        debug: (Boolean) settings.logDebug,
        trace: (Boolean) settings.logTrace,
    ]
}

public void enableDebugLog() { device.updateSetting("logDebug",[value:sTRUE,type:"bool"]); logInfo("Debug Logs Enabled From Main App..."); }
public void disableDebugLog() { device.updateSetting("logDebug",[value:sFALSE,type:"bool"]); logInfo("Debug Logs Disabled From Main App..."); }
public void enableTraceLog() { device.updateSetting("logTrace",[value:sTRUE,type:"bool"]); logInfo("Trace Logs Enabled From Main App..."); }
public void disableTraceLog() { device.updateSetting("logTrace",[value:sFALSE,type:"bool"]); logInfo("Trace Logs Disabled From Main App..."); }

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn logPrefix(sSPACE + msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15) } }
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return (String) str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error logPrefix(msg, sCLRRED)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (e) {
        }
        if(a) log.error logPrefix(a, sCLRRED)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, null, 15) }
}

static String logPrefix(String msg, String color = sNULL) {
    return span("Echo Zone (v" + devVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

Map getLogHistory() {
    return [ warnings: getMemStoreItem("warnHistory") ?: [], errors: getMemStoreItem("errorHistory") ?: [], speech: getMemStoreItem("speechHistory") ?: [] ]
}

public clearLogHistory() {
    updMemStoreItem("warnHistory", [])
    updMemStoreItem("errorHistory",[])
    updMemStoreItem("speechHistory", [])
    mb()
}

void incrementCntByKey(String key) {
    Long evtCnt = state?."${key}"
    evtCnt = evtCnt != null ? evtCnt : 0
    evtCnt++
    state."${key}" = evtCnt
}

static String getObjType(obj) {
    if(obj instanceof String) {return "String"}
    else if(obj instanceof GString) {return "GString"}
    else if(obj instanceof Map) {return "Map"}
    else if(obj instanceof LinkedHashMap) {return "LinkedHashMap"}
    else if(obj instanceof HashMap) {return "HashMap"}
    else if(obj instanceof List) {return "List"}
    else if(obj instanceof ArrayList) {return "ArrayList"}
    else if(obj instanceof Integer) {return "Integer"}
    else if(obj instanceof BigInteger) {return "BigInteger"}
    else if(obj instanceof Long) {return "Long"}
    else if(obj instanceof Boolean) {return "Boolean"}
    else if(obj instanceof BigDecimal) {return "BigDecimal"}
    else if(obj instanceof Float) {return "Float"}
    else if(obj instanceof Byte) {return "Byte"}
    else { return "unknown"}
}

// FIELD VARIABLE FUNCTIONS
private void updMemStoreItem(String key, val) {
    String appId = device.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    memStore[key] = val
    historyMapFLD[appId] = memStore
    historyMapFLD = historyMapFLD
    // log.debug("updMemStoreItem(${key}): ${memStore[key]}")
}

private List getMemStoreItem(String key){
    String appId = device.getId()
    Map memStore = historyMapFLD[appId] ?: [:]
    return (List)memStore[key] ?: []
}

// Memory Barrier
@Field static java.util.concurrent.Semaphore theMBLockFLD=new java.util.concurrent.Semaphore(0)

static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}
