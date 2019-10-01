/**
 *	Echo Speaks WebSocket
 *
 *  Copyright 2019 Anthony Santilli
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
 // This is based on the Amazon WebSocket used on Alexa.amazon.com and is ported from Javascript to Groovy and inspired from the work of @Apollon77 Alexa-Remote

import groovy.json.*
import java.util.*
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.security.MessageDigest
String devVersion()  { return "3.1.0.2"}
String devModified() { return "2019-10-01" }
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
    def msgId = Math.floor(1E9 * Math.random()) as BigInteger;
    // log.debug "messageId: ${msgId}"
    state?.messageId = state?.messageId ?: msgId
    state?.messageInitCnt = 0
    connect()
}

def connect() {
    //Connect to Alexa API WebSocket
    try {
        def ts = now()
        String url = "https://dp-gw-na${state?.wsDomain}/?x-amz-device-type=ALEGCNGL9K0HM&x-amz-device-serial=${state?.wsSerial}-${ts}"
        log.debug "url: ${url}"
        Map headers = [
            "Connection": "keep-alive, Upgrade",
            "Upgrade": "websocket",
            "Host": "dp-gw-na.${state?.amazonDomain}",
            "Origin": "https://alexa.${state?.amazonDomain}",
            "Pragma": "no-cache",
            "Cache-Control": "no-cache",
            "Cookie": state?.cookie
        ]
        interfaces.webSocket.connect(url, byteInterface: "true", pingInterval: 45, headers: headers)
    }
    catch(e) {
        log.error "WebSocket connect failed"
    }
}

def close() {
    state?.connectionActive = false;
    interfaces.webSocket.close()
}

def reconnectWebSocket() {
    // first delay is 2 seconds, doubles every time
    state.reconnectDelay = (state.reconnectDelay ?: 1) * 2
    // don't def the delay get too crazy, max it out at 10 minutes
    if(state.reconnectDelay > 600) state.reconnectDelay = 600

    runIn(state?.reconnectDelay, initialize)
}

def sendWsMsg(String s) {
    interfaces.webSocket.sendMessage(s)
}

def webSocketStatus(String status){
    logDebug("Websocket Status Event | ${status}")
    if(status.startsWith('failure: ')) {
        logWarn("Websocket Failure Message: ${status}")
        parent?.webSocketStatus(false)
        reconnectWebSocket()
    } else if(status == 'status: open') {
        logInfo("Alexa WS Connection is Open")
        // success! reset reconnect delay
        pauseExecution(1000)
        state.reconnectDelay = 1
        // log.trace("Connection Initiation (Step 1)")
        sendWsMsg(strToHex("0x99d4f71a 0x0000001d A:HTUNE")?.toString())
    } else if (status == "status: closing"){
        logWarn("WebSocket connection closing.")
        parent?.webSocketStatus(false)
    } else if(status?.startsWith("send error: ")) {
        logError("Websocket Send Error: $status")
    } else {
        logWarn("WebSocket error, reconnecting.", false)
        reconnectWebSocket()
    }
}

def parse(message) {
    // log.debug "parsed ${message}"
    def newMsg = strFromHex(message)
    // log.debug "decodedMsg: ${newMsg}"
    if(newMsg) {
        if(newMsg == """0xbafef3f3 0x000000cd {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.supportedEncodings":"GZIP","AlphaProtocolHandler.maxFragmentSize":"16000","AlphaProtocolHandler.receiveWindowSize":"16"}}TUNE""") {
            sendWsMsg(strToHex("""0xa6f6a951 0x0000009c {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.receiveWindowSize":"16","AlphaProtocolHandler.maxFragmentSize":"16000"}}TUNE""")?.toString())
            pauseExecution(1000)
            sendWsMsg(strToHex(encodeGWHandshake()))
            // log.trace("Gateway Handshake Message Sent (Step 2)")
        } else if (newMsg?.startsWith("MSG 0x00000361 ") && newMsg?.endsWith(" END FABE")) {
            sendWsMsg(strToHex(encodeGWRegister()))
            // log.trace("Gateway Registration Message Sent (Step 3)")
            pauseExecution(1000)
            sendWsMsg(strToHex(encodePing()))
            // log.trace("Encoded Ping Message Sent (Step 4)")
        }
        parseIncomingMessage(newMsg as String)
    }
}

def readHex(str, ind, len, logs=false) {
    str = str[ind..ind+len-1]
    if (str?.startsWith('0x')) str = str?.substring(2);
    def res = null
    try {
        res = Integer.parseInt(str as String, 16)
    } catch(ex) {
        res = new BigInteger(str as String, 16);
    }
    if(logs) log.debug "readHex(ind: $ind, len: $len): ${res}"
    return res
}

def readString(str, ind, len, logs=false) {
    def s = str[ind..len]
    if(logs) log.debug "readString(ind: $ind, len: $len): ${s}"
    return s
}

def parseString(String str, Integer ind, Integer len, Boolean logs=false) {
    def s = str?.substring(ind, ind+len)
    if(logs) log.debug "parseString(ind: $ind, len: $len): ${s}"
    return s
}

def parseIncomingMessage(data) {
    // try {
        Integer idx = 0;
        Map message = [:]
        String dStr = data?.toString()
        Integer dLen = dStr?.length()
        message?.service = readString(dStr, dLen-4, dLen-1)
        // log.debug "Message Service: ${message?.service}"

        if (message?.service == "TUNE") {
            message?.checksum = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            def contentLength = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            message?.content = parseString(dStr, idx, contentLength - 4 - idx);
            if (message?.content?.startsWith('{') && message?.content?.endsWith('}')) {
                try {
                    message?.content = parseJson(message?.content?.toString());
                    log.debug "TUNE: ${message?.content}"
                } catch (e) {}
            }
        } else if (message?.service == 'FABE') {
            message?.messageType = readString(dStr, idx, 3);
            idx += 4;
            message?.channel = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            message?.messageId = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            message?.moreFlag = readString(dStr, idx, 1);
            idx += 2; // 1 + delimiter;
            message?.seq = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            message?.checksum = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;

            def contentLength = readHex(dStr, idx, 10);
            idx += 11; // 10 + delimiter;
            message?.content = [:]
            message?.content?.messageType = parseString(dStr, idx, 3);
            // log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
            idx += 4;

            if (message?.channel == 865) { //0x361 GW_HANDSHAKE_CHANNEL
                if (message?.content?.messageType == "ACK") {
                    def length = readHex(dStr, idx, 10);
                    idx += 11; // 10 + delimiter;
                    message?.content?.protocolVersion = parseString(dStr, idx, length);
                    idx += length + 1;
                    length = readHex(dStr, idx, 10);
                    idx += 11; // 10 + delimiter;
                    message?.content?.connectionUUID = parseString(dStr, idx, length);
                    idx += length + 1;
                    message?.content?.established = readHex(dStr, idx, 10);
                    idx += 11; // 10 + delimiter;
                    message?.content?.timestampINI = readHex(dStr, idx, 18);
                    idx += 19; // 18 + delimiter;
                    message?.content?.timestampACK = readHex(dStr, idx, 18);
                    idx += 19; // 18 + delimiter;
                    // log.debug "message.content: ${message?.content}"
                    state?.wsAckData = message?.content
                    logInfo("WebSocket Connection Established...")
                    parent?.webSocketStatus(true)
                }
            } else if (message?.channel == 866) { // 0x362 GW_CHANNEL
                if (message?.content?.messageType == 'GWM') {
                    message?.content?.subMessageType = readString(dStr, idx, 3);
                    idx += 4;
                    message?.content?.channelStr = parseString(dStr, idx, 10)
                    message?.content?.channel = readHex(dStr, idx, 10);
                    // log.debug "Content Channel: ${message?.content?.channel} | (${message?.content?.channelStr})"
                    idx += 11; // 10 + delimiter;

                    if (message?.content?.channel == 46201) { // 0xb479 DEE_WEBSITE_MESSAGING
                        def length = readHex(dStr, idx, 10);
                        idx += 11;
                        message?.content?.destinationIdentityUrn = parseString(dStr, idx, length);
                        idx += length + 1;
                        length = readHex(dStr, idx, 10);
                        idx += 11;
                        def idData = parseString(dStr, idx, length);
                        idx += length + 1;
                        idData = idData?.tokenize(' ');
                        // log.debug "idData: $idData"
                        message?.content?.deviceIdentityUrn = idData[0];
                        message?.content?.payload = idData[1] ?: null;
                        // log.debug "deviceUrn: ${message?.content?.deviceIdentityUrn}"
                        if (!message?.content?.payload) {
                            message?.content?.payload = parseString(dStr, idx, dStr?.length() - 4 - idx);
                        }
                        // log.debug "payload: ${message?.content?.payload}"
                        if (message?.content?.payload?.startsWith('{') && message?.content?.payload?.endsWith('}')) {
                            try {
                                message?.content?.payload = parseJson(message?.content?.payload?.toString());
                                if (message?.content?.payload && message?.content?.payload?.payload && message?.content?.payload?.payload instanceof String) {
                                    message?.content?.payload?.payload = parseJson(message?.content?.payload?.payload?.toString());
                                    commandEvtHandler(message?.content?.payload)
                                }
                            } catch (e) {
                                logError("payload parse error: $ex")
                            }
                        } else {
                            log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
                            logWarn("UNKNOWN PAYLOAD FORMAT: ${message?.content?.payload}")
                        }
                    } else {
                        log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
                        logWarn("UNKNOWN CONTENT CHANNEL: ${message?.content?.channel}")
                    }
                } else {
                    log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
                    logWarn("UNKNOWN MESSAGETYPE: ${message?.content?.messageType}")
                }
            } else if (message?.channel == 101) { // 0x65 CHANNEL_FOR_HEARTBEAT
                idx -= 1; // no delimiter!
                log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
                message?.content?.payloadData = dStr.slice(idx, dLen - 4);
            } else {
                log.debug "Service: (${message?.service}) | Type: (${message?.messageType}) | Channel: (${message?.channel}) | contentMsgType: (${message?.content?.messageType})"
                logWarn("UNKNOWN CHANNEL: ${message?.channel}")
            }
        }
    // } catch (ex) {
    //     log.error "parseIncomingMessage Exception: ${ex.message}"
    // }
    // return message;
}

private commandEvtHandler(msg) {
    Boolean sendEvt = false
    Map evt = [:]
    evt?.id = msg?.payload?.dopplerId?.deviceSerialNumber ?: null
    evt?.all = false
    evt?.type = msg?.command
    evt?.attributes = [:]
    evt?.triggers = []

    if(msg && msg?.command && msg?.payload) {
        switch(msg?.command as String) {
            case "PUSH_EQUALIZER_STATE_CHANGE":
                // Black hole of unwanted events.
                break

            case "PUSH_VOLUME_CHANGE":
                // log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.attributes?.volume = msg?.payload?.volumeSetting
                evt?.attributes?.level = msg?.payload?.volumeSetting
                evt?.attributes?.mute = (isMuted == true) ? "muted" : "unmuted"
                break
            case "PUSH_BLUETOOTH_STATE_CHANGE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                switch(msg?.payload?.bluetoothEvent) {
                    case "DEVICE_DISCONNECTED":
                    case "DEVICE_CONNECTED":
                        if(msg?.payload?.bluetoothEventSuccess == true) {
                            sendEvt = true
                            if(msg?.payload?.bluetoothEvent == "DEVICE_DISCONNECTED") { evt?.attributes?.btDeviceConnected = null }
                            evt?.triggers?.push("bluetooth")
                        }
                        break
                }
                break
            case "PUSH_AUDIO_PLAYER_STATE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.attributes?.status = msg?.payload?.audioPlayerState == "PLAYING" ? "playing" : "stopped"
                evt?.triggers?.push("media")
                break
            case "PUSH_MEDIA_QUEUE_CHANGE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.triggers?.push("queue")
                break
            case "PUSH_MEDIA_PROGRESS_CHANGE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.triggers?.push("media")
                evt?.triggers?.push("queue")
                break
            case "PUSH_DOPPLER_CONNECTION_CHANGE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.attributes?.onlineStatus = (msg?.payload?.dopplerConnectionState == "ONLINE") ? "online" : "offline"
                evt?.triggers?.push(evt?.attributes?.onlineStatus)
                break
            case "PUSH_ACTIVITY":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                def keys = msg?.payload?.key?.entityId?.tokenize("#")
                if(keys?.size() && keys[2]) {
                    sendEvt = true
                    evt?.id = keys[2]
                    evt?.triggers?.push("activity")
                    evt?.all = true
                }
                break
            case "PUSH_NOTIFICATION_CHANGE":
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                sendEvt = true
                evt?.triggers?.push("notification")
                break
            default:
                log.debug "Command: ${msg?.command} | Payload: ${msg?.payload}"
                break
        }
    }
    if(sendEvt) {
        parent?.wsEvtHandler(evt)
    }
}


String encodeGWHandshake() {
    //pubrelBuf = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
    try {
        state?.messageId++;
        def now = now()
        def msg = 'MSG 0x00000361 '; // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
        msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
        def idx1 = msg?.length();
        msg += '0x00000000 '; // Checksum!
        def idx2 = msg?.length();
        msg += '0x0000009b '; // length content
        msg += 'INI 0x00000003 1.0 0x00000024 '; // content part 1
        msg += generateUUID();
        msg += ' ';
        msg += encodeNumber(now, 16);
        msg += ' END FABE';
        // log.debug "msg: ${msg}"
        byte[] buffer = msg?.getBytes("ASCII")
        def checksum = rfc1071Checksum(msg, idx1, idx2);
        def checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
        buffer = copyArrRange(buffer, 39, checksumBuf)
        return new String(buffer)
    } catch (ex) { log.error "encodeGWHandshake Exception: ${ex}" }
}

def encodeGWRegister() {
    //pubrelBuf = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
    try {
        state?.messageId++;
        def msg = 'MSG 0x00000362 '; // Message-type and Channel = GW_CHANNEL;
        msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
        def idx1 = msg?.length();
        msg += '0x00000000 '; // Checksum!
        def idx2 = msg?.length();
        msg += '0x00000109 '; // length content
        msg += 'GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE';
        byte[] buffer = msg?.getBytes("ASCII")
        def checksum = rfc1071Checksum(msg, idx1, idx2);
        def checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
        buffer = copyArrRange(buffer, 39, checksumBuf)
        def out = new String(buffer)
        return out
    } catch (ex) { log.error "encodeGWRegister Exception: ${ex}" }
}

def encodePing() {
    state.messageId++;
    def now = now()
    String msg = 'MSG 0x00000065 '; // Message-type and Channel = CHANNEL_FOR_HEARTBEAT;
    msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
    Integer idx1 = msg.length();
    msg += '0x00000000 '; // Checksum!
    Integer idx2 = msg.length();
    msg+= '0x00000062 '; // length content
    byte[] buffer = new byte[98]
    buffer = copyArrRange(buffer, 0, msg?.getBytes("ASCII"));
    String header = 'PIN';
    String payload = 'Regular';
    byte[] n = new byte[header?.length() + 4 + 8 + 4 + (2 * payload?.length())] // Creates empty byte array with size of 98
    Integer idx = 0;
    byte[] u = header?.getBytes("UTF-8");
    n = copyArrRange(n, 0, u)
    Integer l = 0;
    idx = header?.length();
    n = encode(n, l, idx, 4);
    idx += 4;
    n = encode(n, now, idx, 8);
    idx += 8;
    n = encode(n, payload?.length(), idx, 4);
    idx += 4;
    n = encodePayload(n, payload, idx, payload?.length())
    buffer = copyArrRange(buffer, msg?.length(), n)
    def buf2End = "FABE"?.getBytes("ASCII")
    def buf2EndPos = msg?.length() + n?.size()
    buffer = copyArrRange(buffer, buf2EndPos, buf2End)
    def checksum = rfc1071Checksum(buffer, idx1, idx2);
    def checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
    buffer = copyArrRange(buffer, 39, checksumBuf)
    def out = new String(buffer)
    return out
    // "MSG 0x00000065 0x0e414e47 f 0x00000001 0xbc2fbb5f 0x00000062 PIN" + 30 + "FABE"
}


def encode(arr, b, Integer pos, Integer len) {
    try {
        def u = new byte[len]
        for (def c = 0; c < len; c++) { u[c] = b >> ((8 * (len - 1 - c)) & 31) & 255; }
        return copyArrRange(arr, pos, u)
    } catch (ex) {
        log.error "encode: $ex"
        return arr
    }
}

def encodePayload(arr, pay, pos, len) {
    byte[] u = new byte[len*2]
    for (def q = 0; q < pay?.length(); q++) { u[q * 2] = 0; u[(q * 2) + 1] = pay?.charAt(q); }
    // log.debug "u: $u"
    return copyArrRange(arr, pos, u)
}

def rfc1071Checksum(a, f, k) {
    if (k < f) throw "Invalid checksum exclusion window!";
    if(a instanceof String) { a = a?.getBytes("UTF-8"); }
    def h = 0
    def l = 0
    def t = 0
    for (def e = 0; e < a?.size(); e++) {
        if(e != f) { t = a[e] << ((e & 3 ^ 3) << 3); l += c(t); h += b(l, 32); l = c(l & 4294967295); }
        else { e = k - 1; }
    }
    for (; h>0;) { l += h; h = b(l, 32); l &= 4294967295; }
    return c(l);
}

def copyArrRange(arrSrc, Integer arrSrcStrt=0, arrIn) {
    if(arrSrc?.size() < arrSrcStrt) { log.error "Array Start Index is larger than Array Size..."; return arrSrc; }
    Integer s = 0
    (arrSrcStrt..(arrSrcStrt+arrIn?.size()-1))?.each { arrSrc[it] = arrIn[s]; s++; }
    return arrSrc
}

String encodeNumber(val, len=null) {
    if (!len) len = 8;
    def str = new BigInteger(val?.toString())?.toString(16);
    while (str?.length() < len) { str = "0${str}"; }
    return '0x' +str;
}

String generateUUID() {
    def a = []
    for (def b = 0; 36 > b; b++) {
        def c = "rrrrrrrr-rrrr-4rrr-srrr-rrrrrrrrrrrr"?.charAt(b);
        if ("r" == c || "s" == c) {
            def d = Math.floor(16 * Math.random());
            if("s" == c) d = d ? 3 : 8;
            a?.push(Integer.toString(d as Integer, 16));
        } else a?.push(c);
    }
    state?.lastUsedGuid = a?.join("")
    return a?.join("");
}
def b(a, b) { for (a = c(a); 0 != b && 0 != a;) { a = Math.floor(a / 2); b--; }; return (a instanceof Double) ? a?.toInteger() : a; }
def c(a) { return (0 > a) ? (4294967295 + a + 1) : a; }
Integer toUInt(byte x) { return ((int) x) & 0xff; }
String strToHex(String arg, charset="UTF-8") { return String.format("%x", new BigInteger(1, arg.getBytes(charset))); }
String strFromHex(str, charset="UTF-8") { return new String(str?.decodeHex()) }
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
private logDebug(msg) { if(settings?.logDebug == true) { log.debug "Socket (v${devVersion()}) | ${msg}" } }
private logInfo(msg) { if(settings?.logInfo != false) { log.info " Socket (v${devVersion()}) | ${msg}" } }
private logTrace(msg) { if(settings?.logTrace == true) { log.trace "Socket (v${devVersion()}) | ${msg}" } }
private logWarn(msg, noHist=false) { if(settings?.logWarn != false) { log.warn " Socket (v${devVersion()}) | ${msg}"; }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15); } }
private logError(msg, noHist=false) { if(settings?.logError != false) { log.error "Socket (v${devVersion()}) | ${msg}"; }; if(noHist) { addToLogHistory("errorHistory", msg, null, 15); } }

Map getLogHistory() {
    return [ warnings: state?.warnHistory ?: [], errors: state?.errorHistory ?: [], speech: state?.speechHistory ?: [] ]
}
public clearLogHistory() {
    state?.warnHistory = []
    state?.errorHistory = []
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
