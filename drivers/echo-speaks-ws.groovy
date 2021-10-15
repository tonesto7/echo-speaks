/**
 *	Echo Speaks WebSocket  (Hubitat)
 *
 *  Copyright 2019, 2020, 2021 Anthony Santilli
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
 // This is based on the Amazon WebSocket used on Alexa.amazon.com and is ported from Javascript to Groovy and inspired from the work of @Apollon77 Alexa-Remote

// NOTICE: This device will not work on SmartThings
//file:noinspection GroovySillyAssignment

import groovy.transform.Field

import java.text.SimpleDateFormat
import java.util.concurrent.Semaphore

// import java.security.*;

//************************************************
//*               STATIC VARIABLES               *
//************************************************
@Field static final String devVersionFLD  = '4.2.0.0'
@Field static final String devModifiedFLD = '2021-10-14'
@Field static final String sNULL          = (String) null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sLINEBR        = '<br>'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRORG        = 'orange'

//************************************************
//*          IN-MEMORY ONLY VARIABLES            *
//* (Cleared only on HUB REBOOT or CODE UPDATES) *
//************************************************
@Field volatile static Map<String,Map> historyMapFLD = [:]
// @Field volatile static String gitBranchFLD = null

static String devVersion()   { return devVersionFLD }
static String devVersionDt() { return devModifiedFLD }
static Boolean isWS()        { return true }

metadata {
    definition (name: "Echo Speaks WS", namespace: "tonesto7", author: "Anthony Santilli", importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/drivers/echo-speaks-ws.groovy") {
        capability "Initialize"
        capability "Refresh"
        capability "Actuator"
        // command "refreshDomainState"
    }
}

preferences {
    input "logInfo", "bool", title: "Show Info Logs?",  required: false, defaultValue: true
    input "logWarn", "bool", title: "Show Warning Logs?", required: false, defaultValue: true
    input "logError", "bool", title: "Show Error Logs?",  required: false, defaultValue: true
    input "logDebug", "bool", title: "Show Debug Logs?", description: "Only leave on when required", required: false, defaultValue: false
    input "logTrace", "bool", title: "Show Detailed Logs?", description: "Only Enabled when asked by the developer", required: false, defaultValue: false
    input "autoConnectWs", "bool", required: false, title: "Auto Connect on Initialize?", defaultValue: true
}

Boolean isSocketActive() { return (Boolean)state.connectionActive }

public updateCookies(Map cookies, doInit=true) {
    String msg = "Cookies Update by Parent."
    msg += doInit ? "  Re-Initializing Device in 10 Seconds..." : ""
    logInfo(msg)
    state.cookie = cookies
    state.amazonDomain = sNULL
    if(doInit) runIn(10, "initialize")
}

public removeCookies(isParent=false) {
    logInfo("Cookie Authentication Cleared by ${isParent ? "Parent" : "Device"} | Scheduled Refreshes also cancelled!")
    close()
    state.amazonDomain = sNULL
    state.cookie = null
}

def refresh() {
    logInfo("refresh() called")
}

def triggerInitialize() { unschedule("initialize"); runIn(3, "updated") }
def resetQueue() {}

def installed() {
    logInfo("installed() called")
    updated()
}

def updated() {
    logInfo("updated() called")
    unschedule()
    state.remove('reconnectDelay') // state.reconnectDelay = 1
    if(advLogsActive()) { runIn(1800, "logsOff") }
    initialize()
}

def initialize() {
    logInfo("initialize() called")
    runIn(1,"close")
    if(minVersionFailed()) { logError("CODE UPDATE REQUIRED to RESUME operation. No WebSocket Connections will be made."); return }
    state.remove('warnHistory'); state.remove('errorHistory')

    if(settings.autoConnectWs != false) {
        if(!state.cookie || !(state.cookie instanceof Map)) state.cookie = (Map)parent?.getCookieMap()
        String cookS = getCookieVal() //state.cookie = parent?.getCookieVal()
        if(cookS) {
            if(!(String)state.amazonDomain) {
                state.amazonDomain = parent?.getAmazonDomain()
                state.wsDomain = ((String)state.amazonDomain == "amazon.com") ? "-js.amazon.com" : ".${(String)state.amazonDomain}"
                def serArr = cookS =~ /ubid-[a-z]+=([^;]+);/
                state.wsSerial = serArr?.find() ? serArr[0..-1][0][1] : null
            }
            state.messageId = state.messageId ?: Math.floor(1E9 * Math.random()) as BigInteger
            state.remove('messageInitCnt') // state.messageInitCnt = 0
            runIn(3,"connect")
        } else {
            logInfo("Skipping Socket Open... Cookie Data is Missing $cookS   $state.cookie")
        }
    } else {
        logInfo("Skipping Socket Open... autoconnect disabled")
    }
}

Boolean advLogsActive() { return ((Boolean)settings.logDebug || (Boolean)settings.logTrace) }

public void logsOff() {
    device.updateSetting("logDebug",[value:"false",type:"bool"])
    device.updateSetting("logTrace",[value:"false",type:"bool"])
    log.debug "Disabling debug logs"
}

def connect() {
    if(!state.cookie || !(String)state.amazonDomain || !(String)state.wsDomain || !state.wsSerial) { logError("connect: no cookie or domain"); return }
    try {
        Map macDms = getMacDmsVal()
        log.debug "macDms: ${macDms}"
        String rs = getRS(macDms)

        log.debug "RS: ${getObjType(rs)} ${rs}"
        Map<String,Object> headers = [
            "Connection": "keep-alive, Upgrade",
            "Upgrade": "websocket",
            "Host": "dp-gw-na.${(String)state.amazonDomain}",
            "Origin": "https://alexa.${(String)state.amazonDomain}",
            "Pragma": "no-cache",
            "Cache-Control": "no-cache",
            "Cookie": getCookieVal()
        ]
        logTrace("connect called")
        String url = "wss://dp-gw-na${(String)state.wsDomain}/?x-amz-device-type=ALEGCNGL9K0HM&x-amz-device-serial=${state.wsSerial}-${now()}"
        // log.info "Connect URL: $url"
        if(macDms) {
            headers["x-dp-comm-tuning"] = "A:F;A:H"
            headers["x-dp-reason"] = "ClientInitiated;1"
            headers["x-dp-tcomm-purpose"] = "Regular"
            headers["x-dp-obfuscatedBssid"] =  "-2019514039"
            headers["x-dp-tcomm-versionName"] = "2.2.443692.0"
            headers["x-adp-signature"] = rs
            headers["x-adp-token"] = macDms.adp_token
            headers["x-adp-alg"] = "SHA256WithRSA:1.0"
        }
        interfaces.webSocket.connect(url, byteInterface: "true", pingInterval: 45, headers: headers)
    } catch(ex) {
        logError("WebSocket connect failed | ${ex}", false, ex)
    }
}

@Field static final String sAPPJSON       = 'application/json'

String getRS(Map macDms) {
    String meth='getRS'
    String res=sNULL
    Map params = [
        uri: (String)parent.getServerHostURL(),
        path: "/createRS",
        headers: [devpk: macDms.device_private_key, adptkn: macDms.adp_token],
        contentType: sAPPJSON,
        timeout: 30
    ]
    try {
        httpPost(params) { resp->
            if(resp?.status != 200) logWarn("${resp?.status} "+meth)
            if(resp?.status == 200) {
                def t0 = resp?.data
                Map sData
                if( t0 instanceof String)  sData = parseJson(resp?.data)
                else sData =  resp?.data
                log.trace("getRS Data  ${sData}")
                log.trace("getRS Data (${getObjType(sData)}): ${sData?.rs}")
                res=sData.rs
            }
        }
    } catch (ex) {
        logError("getRS failed | ${ex}", false, ex)
    }
    return res
}

def close() {
    logInfo("close() called")
    updSocketStatus(false)
    interfaces.webSocket.close()
}

def reconnectWebSocket() {
    // first delay is 2 seconds, doubles every time
    Long d = state.reconnectDelay ?: 1
    d *= 2L
    // don't let the delay get too crazy, max it out at 10 minutes
    if(d > 600L) d = 600L
    state.reconnectDelay = d
    //close() // updSocketStatus(false)
    logInfo("reconnectWebSocket() called delay: $d")
    runIn(d, "initialize")
}

def sendWsMsg(String s) {
    interfaces?.webSocket?.sendMessage(s as String)
}

void updSocketStatus(Boolean active) {
    parent?.webSocketStatus(active)
    state.connectionActive = active
}

def webSocketStatus(String status) {
    logTrace("Websocket Status Event | ${status}")
    if(status.startsWith('failure: ')) {
        logWarn("Websocket Failure Message: ${status}")
    } else if(status == 'status: open') {
        logInfo("Alexa WS Connection is Open")
        // success! reset reconnect delay
        // pauseExecution(1000)
        // state.remove('reconnectDelay') // state.reconnectDelay = 1
        state.connectionActive = true
        // log.trace("Connection Initiation (Step 1)")
        runIn(1, "nextMsgSend")
        return
    } else if (status == "status: closing") {
        logWarn("WebSocket connection closing.")
        unschedule("nextMsgSend")
        unschedule("nextMsgSend1")
        unschedule("nextMsgSend2")
        unschedule("nextMsgSend3")
        unschedule("nextMsgSend4")
    } else if(status?.startsWith("send error: ")) {
        logError("Websocket Send Error: $status")
    } else logWarn("WebSocket error, reconnecting. $status", false)
    close() // updSocketStatus(false)
    reconnectWebSocket()
}

@SuppressWarnings('unused')
void nextMsgSend() {
    state.remove('reconnectDelay') // state.reconnectDelay = 1
    sendWsMsg(strToHex("0x99d4f71a 0x0000001d A:HTUNE"))
    logTrace("Gateway Handshake Message Sent (Step 1)")
}

@SuppressWarnings('unused')
void nextMsgSend1() {
    sendWsMsg( strToHex("""0xa6f6a951 0x0000009c {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.receiveWindowSize":"16","AlphaProtocolHandler.maxFragmentSize":"16000"}}TUNE""") )
    logTrace("Gateway Handshake Message Sent (Step 2A)")
}

@SuppressWarnings('unused')
void nextMsgSend2() {
    sendWsMsg( strToHex(encodeGWHandshake()) )
    logTrace("Gateway Handshake Message Sent (Step 2B)")
}

@SuppressWarnings('unused')
void nextMsgSend3() {
    sendWsMsg( strToHex(encodeGWRegister()) )
    logTrace("Gateway Registration Message Sent (Step 3)")
}

@SuppressWarnings('unused')
void nextMsgSend4() {
    sendWsMsg( strToHex(encodePing()) )
    logTrace("Encoded Ping Message Sent (Step 4)")
}

def parse(message) {
    // log.debug("parsed ${message}")
    String newMsg = strFromHex(message)
    logTrace("decodedMsg: ${newMsg}")
    if(newMsg) {
        if(newMsg == """0x37a3b607 0x0000009c {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.maxFragmentSize":"16000","AlphaProtocolHandler.receiveWindowSize":"16"}}TUNE""") {
        // if(newMsg == """0xbafef3f3 0x000000cd {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.supportedEncodings":"GZIP","AlphaProtocolHandler.maxFragmentSize":"16000","AlphaProtocolHandler.receiveWindowSize":"16"}}TUNE""") {
            runIn(4, "nextMsgSend1")
            runIn(6, "nextMsgSend2")
            return
        } else if (newMsg?.startsWith("MSG 0x00000361 ") && newMsg?.endsWith(" END FABE")) {
            runIn(2, "nextMsgSend3")
//            pauseExecution(1000)
            runIn(4, "nextMsgSend4")
        }
        parseIncomingMessage(newMsg)
    }
}

def readHex(String str, Integer ind, Integer len, Boolean logs=false) {
    str = str[ind..ind+len-1]
    if (str?.startsWith('0x')) str = str.substring(2)
    def res
    try {
        res = Integer.parseInt(str as String, 16)
    } catch(ignored) {
        res = new BigInteger(str as String, 16)
    }
    if(logs) log.debug "readHex(ind: $ind, len: $len): ${res}"
    return res
}

String readString(String str, Integer sind, Integer eind, Boolean logs=false) {
    String s = str[sind..eind]
    if(logs) log.debug "readString(ind: $sind, eind: $eind): ${s}"
    return s
}

String parseString(String str, Integer ind, Integer len, Boolean logs=false) {
    String s = str?.substring(ind, ind+len)
    if(logs) log.debug "parseString(ind: $ind, len: $len): ${s}"
    return s
}

void parseIncomingMessage(String data) {
    // try {
        Integer idx = 0
        Map message = [:]
        String dStr = data
        Integer dLen = dStr.length()
        message.service = readString(dStr, dLen-4, dLen-1)
        // log.debug "Message Service: ${message?.service}"

        if ((String) message.service == "TUNE") {
            message.checksum = readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            Integer contentLength = (Integer) readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            message.content = parseString(dStr, idx, contentLength - 4 - idx)
            if (message.content?.startsWith('{') && message.content?.endsWith('}')) {
                try {
                    message.content = parseJson(message.content?.toString())
                    // log.debug "TUNE: ${message?.content}"
                } catch (ignored) {}
            }
        } else if ((String)message.service == 'FABE') {
            message.messageType = readString(dStr, idx, 3)
            idx += 4
            message.channel = readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            message.messageId = readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            message.moreFlag = readString(dStr, idx, 1)
            idx += 2 // 1 + delimiter;
            message.seq = readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            message.checksum = readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;

            Integer contentLength = (Integer) readHex(dStr, idx, 10)
            idx += 11 // 10 + delimiter;
            message.content = [:]
            message.content.messageType = parseString(dStr, idx, 3)
            // dumpMsg(message)
            idx += 4

            if (message.channel == 865) { //0x361 GW_HANDSHAKE_CHANNEL
                if (message.content?.messageType == "ACK") {
                    Integer length = (Integer) readHex(dStr, idx, 10)
                    idx += 11 // 10 + delimiter;
                    message.content.protocolVersion = parseString(dStr, idx, length)
                    idx += length + 1
                    length = (Integer) readHex(dStr, idx, 10)
                    idx += 11 // 10 + delimiter;
                    message.content.connectionUUID = parseString(dStr, idx, length)
                    idx += length + 1
                    message.content.established = readHex(dStr, idx, 10)
                    idx += 11 // 10 + delimiter;
                    message.content.timestampINI = readHex(dStr, idx, 18)
                    idx += 19 // 18 + delimiter;
                    message.content.timestampACK = readHex(dStr, idx, 18)
                    //idx += 19 // 18 + delimiter;
                    // log.debug "message.content: ${message?.content}"
                    state.wsAckData = message.content
                    logInfo("WebSocket Connection Established...")
                    updSocketStatus(true)
                }
            } else if (message?.channel == 866) { // 0x362 GW_CHANNEL
                if (message.content?.messageType == 'GWM') {
                    message.content.subMessageType = readString(dStr, idx, 3)
                    idx += 4
                    message.content.channelStr = parseString(dStr, idx, 10)
                    message.content.channel = readHex(dStr, idx, 10)
                    // log.debug "Content Channel: ${message?.content?.channel} | (${message?.content?.channelStr})"
                    idx += 11 // 10 + delimiter;

                    if (message?.content?.channel == 46201) { // 0xb479 DEE_WEBSITE_MESSAGING
                        Integer length = (Integer) readHex(dStr, idx, 10)
                        idx += 11
                        message.content.destinationIdentityUrn = parseString(dStr, idx, length)
                        idx += length + 1
                        length = (Integer) readHex(dStr, idx, 10)
                        idx += 11
                        def idData = parseString(dStr, idx, length)
                        idx += length + 1
                        idData = idData?.tokenize(' ')
                        // log.debug "idData: $idData"
                        message.content.deviceIdentityUrn = idData[0]
                        message.content.payload = idData[1] ?: null
                        // log.debug "deviceUrn: ${message?.content?.deviceIdentityUrn}"
                        if (!message.content.payload) {
                            message.content.payload = parseString(dStr, idx, dStr?.length() - 4 - idx)
                        }
                        // log.debug "payload: ${message?.content?.payload}"
                        if (message.content.payload.startsWith('{') && message.content.payload.endsWith('}')) {
                            try {
                                message.content.payload = parseJson(message.content.payload.toString())
                                if (message.content.payload && message.content.payload.payload && message.content.payload.payload instanceof String) {
                                    message.content.payload.payload = parseJson(message.content.payload.payload?.toString())
                                    commandEvtHandler(message.content.payload)
                                }
                            } catch (e) {
                                logError("payload parse error: $ex", false, e)
                            }
                        } else {
                            dumpMsg(message)
                            // logWarn("UNKNOWN PAYLOAD FORMAT: ${message?.content?.payload}")
                        }
                    } else {
                        dumpMsg(message)
                        // logWarn("UNKNOWN CONTENT CHANNEL: ${message?.content?.channel}")
                    }
                } else {
                    dumpMsg(message)
                    // logWarn("UNKNOWN MESSAGETYPE: ${message?.content?.messageType}")
                }
            } else if (message?.channel == 101) { // 0x65 CHANNEL_FOR_HEARTBEAT
                idx -= 1 // no delimiter!
                dumpMsg(message)
                message.content.payloadData = readString(dStr, idx, dLen - 4)
            } else {
                dumpMsg(message)
                // logWarn("UNKNOWN CHANNEL: ${message?.channel}")
            }
        }
    // } catch (ex) {
    //     log.error "parseIncomingMessage Exception: ${ex.message}"
    // }
    // return message;
}

void dumpMsg(Map message) {
    logDebug("Service: (${message.service}) | Type: (${message.messageType}) | Channel: (${message.channel}) | contentMsgType: (${message.content?.messageType})")
}

private commandEvtHandler(mymsg) {
    Map msg = mymsg
    Boolean sendEvt = false
    String cmd = (String)msg?.command
    Map<String,Object> evt = [
        id: msg?.payload?.dopplerId?.deviceSerialNumber ?: null,
        all: false,
        type:  cmd,
        attributes: [:],
        triggers: []
    ]

    if(msg && cmd && msg.payload) {
        logTrace("Command: ${cmd} | Payload: ${msg.payload}")
        switch(cmd) {
            case "PUSH_EQUALIZER_STATE_CHANGE":
                // Black hole of unwanted events.
                break

            case "PUSH_VOLUME_CHANGE":
                sendEvt = true
                evt.attributes.volume = msg.payload.volumeSetting
                evt.attributes.level = msg.payload.volumeSetting
                evt.attributes.mute = msg.payload.isMuted == true ? "muted" : "unmuted"
                break
            case "PUSH_BLUETOOTH_STATE_CHANGE":
                switch((String)msg.payload.bluetoothEvent) {
                    case "DEVICE_DISCONNECTED":
                    case "DEVICE_CONNECTED":
                        if(msg.payload.bluetoothEventSuccess == true) {
                            sendEvt = true
                            if((String)msg.payload.bluetoothEvent == "DEVICE_DISCONNECTED") { evt.attributes?.btDeviceConnected = null }
                            evt.triggers.push("bluetooth")
                        }
                        break
                }
                break
            case "PUSH_AUDIO_PLAYER_STATE":
                sendEvt = true
                evt.attributes.status = msg.payload.audioPlayerState == "PLAYING" ? "playing" : "stopped"
                evt.triggers.push("media")
                break
            case "PUSH_MEDIA_QUEUE_CHANGE":
                sendEvt = true
                evt.triggers.push("queue")
                break
            case "PUSH_MEDIA_PROGRESS_CHANGE":
                sendEvt = true
                evt.triggers.push("media")
                evt.triggers.push("queue")
                break
            case "PUSH_DOPPLER_CONNECTION_CHANGE":
                sendEvt = true
                evt.attributes.onlineStatus = ((String)msg.payload.dopplerConnectionState == "ONLINE") ? "online" : "offline"
                evt.triggers.push(evt.attributes.onlineStatus)
                break
            case "PUSH_ACTIVITY":
                List keys = msg.payload?.key?.entryId?.tokenize("#")
                if(keys?.size() && keys[2]) {
                    sendEvt = true
                    evt.id = keys[2]
                    evt.triggers.push("activity")
                    evt.all = true
                }
                break
            case "PUSH_NOTIFICATION_CHANGE":
                sendEvt = true
                evt.triggers.push("notification")
                break
            // case 'PUSH_TODO_CHANGE':
            // case 'PUSH_LIST_ITEM_CHANGE':
            // case 'PUSH_LIST_CHANGE':

            // case 'PUSH_MICROPHONE_STATE':
            // case 'PUSH_DELETE_DOPPLER_ACTIVITIES':
            default:
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
        state.messageId++
        Long now = now()
        String msg = 'MSG 0x00000361 ' // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
        msg += encodeNumber(state.messageId) + ' f 0x00000001 '
        Integer idx1 = msg?.length()
        msg += '0x00000000 ' // Checksum!
        Integer idx2 = msg?.length()
        msg += '0x0000009b ' // length content
        msg += 'INI 0x00000003 1.0 0x00000024 ' // content part 1
        msg += generateUUID()
        msg += ' '
        msg += encodeNumber(now, 16)
        msg += ' END FABE'
        // log.debug "msg: ${msg}"
        byte[] buffer = msg?.getBytes("ASCII")
        Long checksum = rfc1071Checksum(msg, idx1, idx2)
        byte [] checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
        buffer = copyArrRange(buffer, 39, checksumBuf)
        return new String(buffer)
    } catch (ex) { log.error "encodeGWHandshake Exception: ${ex}" }
}

String encodeGWRegister() {
    //pubrelBuf = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
    try {
        state.messageId++
        String msg = 'MSG 0x00000362 ' // Message-type and Channel = GW_CHANNEL;
        msg += encodeNumber(state.messageId) + ' f 0x00000001 '
        Integer idx1 = msg?.length()
        msg += '0x00000000 ' // Checksum!
        Integer idx2 = msg?.length()
        msg += '0x00000109 ' // length content
        msg += 'GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE'
        byte[] buffer = msg?.getBytes("ASCII")
        Long checksum = rfc1071Checksum(msg, idx1, idx2)
        byte[] checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
        buffer = copyArrRange(buffer, 39, checksumBuf)
        String out = new String(buffer)
        return out
    } catch (ex) { log.error "encodeGWRegister Exception: ${ex}" }
}

String encodePing() {
    state.messageId++
    Long now = now()
    String msg = 'MSG 0x00000065 ' // Message-type and Channel = CHANNEL_FOR_HEARTBEAT;
    msg += encodeNumber(state.messageId) + ' f 0x00000001 '
    Integer idx1 = msg.length()
    msg += '0x00000000 ' // Checksum!
    Integer idx2 = msg.length()
    msg+= '0x00000062 ' // length content
    byte[] buffer = new byte[98]
    buffer = copyArrRange(buffer, 0, msg?.getBytes("ASCII"))
    String header = 'PIN'
    String payload = 'Regular'
    byte[] n = new byte[header?.length() + 4 + 8 + 4 + (2 * payload?.length())] // Creates empty byte array with size of 98
    //Integer idx = 0
    byte[] u = header?.getBytes("UTF-8")
    n = copyArrRange(n, 0, u)
    Integer l = 0
    idx = header?.length()
    n = encode(n, l, idx, 4)
    idx += 4
    n = encode(n, now, idx, 8)
    idx += 8
    n = encode(n, payload?.length(), idx, 4)
    idx += 4
    n = encodePayload(n, payload, idx, payload?.length())
    buffer = copyArrRange(buffer, msg?.length(), n)
    def buf2End = "FABE"?.getBytes("ASCII")
    Integer buf2EndPos = msg?.length() + n?.size()
    buffer = copyArrRange(buffer, buf2EndPos, buf2End)
    Long checksum = rfc1071Checksum(buffer, idx1, idx2)
    byte[] checksumBuf = encodeNumber(checksum)?.getBytes("UTF-8")
    buffer = copyArrRange(buffer, 39, checksumBuf)
    String out = new String(buffer)
    return out
    // "MSG 0x00000065 0x0e414e47 f 0x00000001 0xbc2fbb5f 0x00000062 PIN" + 30 + "FABE"
}


def encode(arr, b, Integer pos, Integer len) {
    try {
        byte[] u = new byte[len]
        for (def c = 0; c < len; c++) { u[c] = b >> ((8 * (len - 1 - c)) & 31) & 255 }
        return copyArrRange(arr, pos, u)
    } catch (ex) {
        log.error "encode: $ex"
        return arr
    }
}

byte[] encodePayload(arr, String pay, Integer pos, Integer len) {
    byte[] u = new byte[len*2]
    for (Integer q = 0; q < pay?.length(); q++) { u[q * 2] = 0; u[(q * 2) + 1] = pay?.charAt(q) }
    // log.debug "u: $u"
    return copyArrRange(arr, pos, u)
}

Long rfc1071Checksum(aa, Integer f, Integer k) {
    if (k < f) logError("Invalid checksum exclusion window!")
    byte[] a = aa instanceof String ? aa?.getBytes("UTF-8") : aa
    Integer h = 0
    Long l = 0
    Integer t
    for (Integer e = 0; e < a?.size(); e++) {
        if(e != f) { t = a[e] << ((e & 3 ^ 3) << 3); l += c(t.toLong()); h += b(l, 32); l = c(l & 4294967295L) }
        else { e = k - 1 }
    }
    for (; h>0;) { l += h; h = b(l, 32); l &= 4294967295L }
    return c(l)
}

static Integer b(Long a, Integer b) { Double aa = c(a).toDouble(); for (; 0 != b && 0.0D != a;) { aa = Math.floor(aa / 2.0D); b-- }; return aa?.toInteger() }
static Long c(Long a) { return (0L > a) ? (4294967295L + a + 1L) : a }

byte[] copyArrRange(arrSrc, Integer arrSrcStrt=0, arrIn) {
    if(arrSrc?.size() < arrSrcStrt) { log.error "Array Start Index is larger than Array Size..."; return arrSrc }
    Integer s = 0
    (arrSrcStrt..(arrSrcStrt+arrIn?.size()-1))?.each { arrSrc[it] = arrIn[s]; s++ }
    return arrSrc
}

static String encodeNumber(val, len=null) {
    if (!len) len = 8
    String str = new BigInteger(val?.toString())?.toString(16)
    while (str.length() < len) { str = "0"+str }
    return '0x' +str
}

String generateUUID() {
    List a = []
    for (Integer b = 0; 36 > b; b++) {
        String c = "rrrrrrrr-rrrr-4rrr-srrr-rrrrrrrrrrrr".charAt(b).toString()
        if ("r" == c || "s" == c) {
            def d = Math.floor(16 * Math.random())
            if("s" == c) d = d ? 3 : 8
            a.push(Integer.toString(d as Integer, 16))
        } else a.push(c)
    }
    String res = a.join(sBLANK)
    state.lastUsedGuid = res
    return res
}

static Integer toUInt(byte x) { return ((int) x) & 0xff }

static String strToHex(String arg, charset="UTF-8") { return String.format("%x", new BigInteger(1, arg.getBytes(charset))) }

static String strFromHex(String str, charset="UTF-8") { return new String(str?.decodeHex()) }

String getCookieVal() { return (state.cookie && state.cookie?.cookie) ? state.cookie?.cookie as String : sNULL }
Map getMacDmsVal() { return (state.cookie && state.cookie?.macDms) ? parseJson(state.cookie?.macDms?.toString()) : null }
//String getCsrfVal() { return (state.cookie && state.cookie?.csrf) ? state.cookie?.csrf as String : null }

Integer stateSize() { String j = new groovy.json.JsonOutput().toJson(state); return j.length() }
Integer stateSizePerc() { return (int) ((stateSize() / 100000)*100).toDouble().round(0) }

// public String gitBranch() { 
//     if(gitBranchFLD == sNULL) { gitBranchFLD = (String) parent.gitBranch() }
//     return (String)gitBranchFLD
// }

static Integer versionStr2Int(String str) { return str ? str.replaceAll("\\.", sBLANK)?.toInteger() : null }

Boolean minVersionFailed() {
    try {
        Integer minDevVer = (Integer)parent?.minVersions()["wsDevice"] ?: null
        return minDevVer != null && versionStr2Int(devVersion()) < minDevVer
    } catch (ignored) {
        return false
    }
}

String getDtNow() {
	Date now = new Date()
	return formatDt(now, false)
}

String formatDt(Date dt, Boolean mdy = true) {
	String formatVal = mdy ? "MMM d, yyyy - h:mm:ss a" : "E MMM dd HH:mm:ss z yyyy"
	def tf = new SimpleDateFormat(formatVal)
	if(location?.timeZone) { tf.setTimeZone((TimeZone)location?.timeZone) }
	return (String)tf.format(dt)
}

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

public void enableDebugLog() { device.updateSetting("logDebug",[value:sTRUE,type:"bool"]); logInfo("Debug Logs Enabled From Main App...") }
public void disableDebugLog() { device.updateSetting("logDebug",[value:sFALSE,type:"bool"]); logInfo("Debug Logs Disabled From Main App...") }
public void enableTraceLog() { device.updateSetting("logTrace",[value:sTRUE,type:"bool"]); logInfo("Trace Logs Enabled From Main App...") }
public void disableTraceLog() { device.updateSetting("logTrace",[value:sFALSE,type:"bool"]); logInfo("Trace Logs Disabled From Main App...") }

private void logDebug(String msg) { if((Boolean)settings.logDebug) { log.debug logPrefix(msg, "purple") } }
private void logInfo(String msg) { if((Boolean)settings.logInfo != false) { log.info sSPACE + logPrefix(msg, "#0299b1") } }
private void logTrace(String msg) { if((Boolean)settings.logTrace) { log.trace logPrefix(msg, sCLRGRY) } }
private void logWarn(String msg, Boolean noHist=false) { if((Boolean)settings.logWarn != false) { log.warn sSPACE + logPrefix(msg, sCLRORG) }; if(!noHist) { addToLogHistory("warnHistory", msg, null, 15) } }
static String span(String str, String clr=sNULL, String sz=sNULL, Boolean bld=false, Boolean br=false) { return (String) str ? "<span ${(clr || sz || bld) ? "style='${clr ? "color: ${clr};" : sBLANK}${sz ? "font-size: ${sz};" : sBLANK}${bld ? "font-weight: bold;" : sBLANK}'" : sBLANK}>${str}</span>${br ? sLINEBR : sBLANK}" : sBLANK }

void logError(String msg, Boolean noHist=false, ex=null) {
    if((Boolean)settings.logError != false) {
        log.error logPrefix(msg, sCLRRED)
        String a
        try {
            if (ex) a = getExceptionMessageWithLine(ex)
        } catch (ignored) {
        }
        if(a) log.error logPrefix(a, sCLRRED)
    }
    if(!noHist) { addToLogHistory("errorHistory", msg, null, 15) }
}

static String logPrefix(String msg, String color = sNULL) {
    return span("Socket (v" + devVersionFLD + ") | ", sCLRGRY) + span(msg, color)
}

Map getLogHistory() {
    return [ warnings: getMemStoreItem("warnHistory") ?: [], errors: getMemStoreItem("errorHistory") ?: [], speech: getMemStoreItem("speechHistory") ?: [] ]
}

public clearLogHistory() {
    updMemStoreItem("warnHistory", [])
    updMemStoreItem("errorHistory",[])
    mb()
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

public Map getDeviceMetrics() {
    Map out = [:]
    def cntItems = state?.findAll { it?.key?.startsWith("use_") }
    def errItems = state?.findAll { it?.key?.startsWith("err_") }
    if(cntItems?.size()) {
        out["usage"] = [:]
        cntItems?.each { k,v -> out.usage[k?.toString()?.replace("use_", sBLANK) as String] = v as Integer ?: 0 }
    }
    if(errItems?.size()) {
        out["errors"] = [:]
        errItems?.each { k,v -> out.errors[k?.toString()?.replace("err_", sBLANK) as String] = v as Integer ?: 0 }
    }
    return out
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
@Field static Semaphore theMBLockFLD=new Semaphore(0)

static void mb(String meth=sNULL){
    if((Boolean)theMBLockFLD.tryAcquire()){
        theMBLockFLD.release()
    }
}
