/**
 *	Echo Speaks WebSocket
 *
 *  Copyright 2018, 2019 Anthony Santilli
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

import groovy.json.*
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
String devVersion()  { return "3.1.0.0"}
String devModified() { return "2019-09-25" }
Boolean isBeta()     { return false }
Boolean isST()       { return (getPlatform() == "SmartThings") }
Boolean isWS()       { return true }

metadata {
    definition (name: "Echo Speaks WS", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/devicetypes/tonesto7/echo-speaks-ws.src/echo-speaks-ws.groovy") {
        capability "Initialize"
        capability "Refresh"
        capability "Actuator"
        //command "sendMsg", ["String"]
        attribute "Activity","String"
    }
}

preferences {
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
}

void updateDeviceStatus(Map devData) {
    Boolean isOnline = false
    if(devData?.size()) {

    }
}

def logsOff(){
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def refresh() {
    log.info "refresh() called"
}

def triggerInitialize() {}
def resetQueue() {}

def installed() {
    log.info "installed() called"
    updated()
}

def updated() {
    log.info "updated() called"
    unschedule()
    initialize()
}

def initialize() {
    log.info "initialize() called"
    close()
    state?.amazonDomain = parent?.getAmazonDomain()
    state?.cookie = parent?.getCookieVal()
    def serArr = state?.cookie =~ /ubid-[a-z]+=([^;]+);/
    state?.wsSerial = serArr?.find() ? serArr[0..-1][0][1] : null
    state?.wsDomain = (state?.amazonDomain == "amazon.com") ? "-js.amazon.com" : ".${state?.amazonDomain}"
    state?.messageId = Math.floor(1E9 * Math.random());
    log.debug "messageId: ${state?.messageId}"
    connect()
}

def connect() {
    //Connect to Alexa API WebSocket
    try {
        def ts = now()
        String url = "https://dp-gw-na${state?.wsDomain}/?x-amz-device-type=ALEGCNGL9K0HM&x-amz-device-serial=${state?.wsSerial}-${ts}"
        log.debug "url: ${url}"
        Map headers = [
            Connection: "keep-alive, Upgrade",
            Upgrade: "websocket",
            Host: "dp-gw-na.${state?.amazonDomain}",
            Origin: "https://alexa.${state?.amazonDomain}",
            Pragma: "no-cache",
            "Cache-Control": "no-cache",
            Cookie: state?.cookie
        ]
        hubitat.helper.InterfaceUtils.webSocketConnect(device, url, perMessageDeflate: true, protocolVersion: 13, headers: headers)
    }
    catch(e) {
        log.error "WebSocket connect failed"
    }
}

def close() {
    state?.connectionActive = false;
    hubitat.helper.InterfaceUtils.webSocketClose()
}

def reconnectWebSocket() {
    // first delay is 2 seconds, doubles every time
    state.reconnectDelay = (state.reconnectDelay ?: 1) * 2
    // don't let the delay get too crazy, max it out at 10 minutes
    if(state.reconnectDelay > 600) state.reconnectDelay = 600

    runIn(state?.reconnectDelay, initialize)
}

def sendWsMsg(String s) {
    hubitat.helper.InterfaceUtils.sendWebSocketMessage(device, s)
}

def webSocketStatus(String status){
    log.debug "WS Status Event | ${status}"

    if(status.startsWith('failure: ')) {
        log.warn("failure message from web socket ${status}")
        reconnectWebSocket()
    } else if(status == 'status: open') {
        logInfo("Alexa WS Connection is Open")
        // success! reset reconnect delay
        pauseExecution(1000)
        state.reconnectDelay = 1
        runIn(2, "connectInit1")
    } else if(state == "status: message") {
        log.debug("Alexa WS Message Recieved...")
        // handleMsg()
    } else if (status == "status: closing"){
        log.warn "WebSocket connection closing."
    } else {
        log.warn "WebSocket error, reconnecting."
        reconnectWebSocket()
    }
}

def connectInit1() {
    log.trace("Connection Initiation (Step 1)")
    sendWsMsg(toHex("0x99d4f71a 0x0000001d A:HTUNE")?.toString())
}

def connectInit2() {
    log.trace("Connection Initiation (Step 2)")
    sendWsMsg(toHex("""0xa6f6a951 0x0000009c {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.receiveWindowSize":"16","AlphaProtocolHandler.maxFragmentSize":"16000"}}TUNE""")?.toString())
}

def connectInit3() {
    log.trace("Connection Initiation (Step 2)")
    // sendWsMsg(toHex("0x99d4f71a 0x0000001d A:HTUNE")?.toString())
}

def parse(String message) {
    log.debug "parsed ${message}"
    // def json = null;
    // try{
    //     json = new groovy.json.JsonSlurper().parseText(message)
    //     log.debug "${json}"
    //     if(json == null){
    //         log.warn "String description not parsed"
    //         return
    //     }
    // }  catch(e) {
    //     log.error("Failed to parse json e = ${e}")
    //     return
    // }
}

String toHex(String arg, charset="UTF-8") { return String.format("%x", new BigInteger(1, arg.getBytes(charset))); }
String getCookieVal() { return (state?.cookie && state?.cookie?.cookie) ? state?.cookie?.cookie as String : null }
String getCsrfVal() { return (state?.cookie && state?.cookie?.csrf) ? state?.cookie?.csrf as String : null }

Integer stateSize() { def j = new groovy.json.JsonOutput().toJson(state); return j?.toString().length(); }
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0); }
private addToLogHistory(String logKey, msg, statusData, Integer max=10) {
    Boolean ssOk = (stateSizePerc() > 70)
    List eData = state?.containsKey(logKey as String) ? state[logKey as String] : []
    if(eData?.find { it?.message == msg }) { return; }
    if(status) { eData.push([dt: getDtNow(), message: msg, status: statusData]) }
    else { eData.push([dt: getDtNow(), message: msg]) }
	if(!ssOK || eData?.size() > max) { eData = eData?.drop( (eData?.size()-max) ) }
	state[logKey as String] = eData
}
private logDebug(msg) { if(settings?.logDebug == true) { log.debug "Echo (v${devVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info " Echo (v${devVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace "Echo (v${devVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { log.warn " Echo (v${devVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15); } }
private logError(msg, noHist=false) { if(settings?.logError != false) { log.error "Echo (v${devVersion()}) | ${msg}"; }; if(noHist) { addToLogHistory("errorHistory", msg, null, 15); } }

Map getLogHistory() {
    return [ warnings: state?.warnHistory ?: [], errors: state?.errorHistory ?: [], speech: state?.speechHistory ?: [] ]
}
public clearLogHistory() {
    state?.warnHistory = []
    state?.errorHistory = []
    state?.speechHistory = []
}

private incrementCntByKey(String key) {
	long evtCnt = state?."${key}" ?: 0
	evtCnt++
	state?."${key}" = evtCnt?.toLong()
}

String getObjType(obj) {
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

public Map getDeviceMetrics() {
    Map out = [:]
    def cntItems = state?.findAll { it?.key?.startsWith("use_") }
    def errItems = state?.findAll { it?.key?.startsWith("err_") }
    if(cntItems?.size()) {
        out["usage"] = [:]
        cntItems?.each { k,v -> out?.usage[k?.toString()?.replace("use_", "") as String] = v as Integer ?: 0 }
    }
    if(errItems?.size()) {
        out["errors"] = [:]
        errItems?.each { k,v -> out?.errors[k?.toString()?.replace("err_", "") as String] = v as Integer ?: 0 }
    }
    return out
}

private getPlatform() {
    String p = "SmartThings"
    if(state?.hubPlatform == null) {
        try { [dummy: "dummyVal"]?.encodeAsJson(); } catch (e) { p = "Hubitat" }
        // if (location?.hubs[0]?.id?.toString()?.length() > 5) { p = "SmartThings" } else { p = "Hubitat" }
        state?.hubPlatform = p
        logDebug("hubPlatform: (${state?.hubPlatform})")
    }
    return state?.hubPlatform
}
