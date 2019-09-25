// def encodeNumber(val, byteLen) {
//     if (!byteLen) byteLen = 8;
//     def str = val.toString(16);
//     while (str.length < byteLen) str = '0' + str;
//     return '0x' +str;
// }

// def generateUUID() {
//     def a = []
//     for (def b = 0; 36 > b; b++) {
//         def c = "rrrrrrrr-rrrr-4rrr-srrr-rrrrrrrrrrrr".charAt(b);
//         if ("r" == c || "s" == c) {
//             def d = Math.floor(16 * Math.random());
//             "s" == c && (d = d & 3 | 8);
//             a.push(d.toString(16));
//         }
//         else a.push(c);
//     }
//     return a.join("");
// }

// def encodeGWHandshake() {
//     //pubrelBuf = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
//     state?.messageId++;
//     def msg = 'MSG 0x00000361 '; // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
//     msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
//     def idx1 = msg.length;
//     msg += '0x00000000 '; // Checksum!
//     def idx2 = msg.length;
//     msg += '0x0000009b '; // length content
//     msg += 'INI 0x00000003 1.0 0x00000024 '; // content part 1
//     msg += generateUUID();
//     msg += ' ';
//     msg += encodeNumber(now(), 16);
//     msg += ' END FABE';
//     def compdefeBuffer = Buffer.from(msg, 'ascii');

//     def checksum = computeChecksum(compdefeBuffer, idx1, idx2);
//     def checksumBuf = Buffer.from(encodeNumber(checksum));
//     checksumBuf.copy(compdefeBuffer, 39);
//     return compdefeBuffer;
// }

// def encodeGWRegister() {
//     //pubrelBuf = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
//     state?.messageId++;
//     def msg = 'MSG 0x00000362 '; // Message-type and Channel = GW_CHANNEL;
//     msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
//     def idx1 = msg.length;
//     msg += '0x00000000 '; // Checksum!
//     def idx2 = msg.length;
//     msg += '0x00000109 '; // length content
//     msg += 'GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE';
//     def compdefeBuffer = Buffer.from(msg, 'ascii');

//     def checksum = computeChecksum(compdefeBuffer, idx1, idx2);
//     def checksumBuf = Buffer.from(encodeNumber(checksum));
//     checksumBuf.copy(compdefeBuffer, 39);
//     return compdefeBuffer;
// }

// def encodePing() {
//     state.messageId++;
//     def msg = 'MSG 0x00000065 '; // Message-type and Channel = CHANNEL_FOR_HEARTBEAT;
//     msg += encodeNumber(state?.messageId) + ' f 0x00000001 ';
//     def idx1 = msg.length;
//     msg += '0x00000000 '; // Checksum!
//     def idx2 = msg.length;
//     msg+= '0x00000062 '; // length content

//     def compdefeBuffer = Buffer.alloc(0x62, 0);
//     def startBuffer = Buffer.from(msg, 'ascii');
//     startBuffer.copy(compdefeBuffer);

//     def header = 'PIN';
//     def payload = 'Regular'; // g = h.length
//     def n = new ArrayBuffer(header.length + 4 + 8 + 4 + 2 * payload.length);
//     def idx = 0;
//     def u = new Uint8Array(n, idx, header.length);
//     def l = 0;
//     def e = Date.now();

//     for (def q = 0; q < header.length; q++) u[q] = header.charCodeAt(q);
//     idx += header.length;
//     encode(n, l, idx, 4);
//     idx += 4;
//     encode(n, e, idx, 8);
//     idx += 8;
//     encode(n, payload.length, idx, 4);
//     idx += 4;
//     u = new Uint8Array(n, idx, payload.length * 2);
//     def q;
//     for (q = 0; q < payload.length; q++) {
//         u[q * 2] = 0;
//         u[q * 2 + 1] = payload.charCodeAt(q);
//     }
//     def buf = Buffer.from(n);
//     buf.copy(compdefeBuffer, msg.length);

//     def buf2End = Buffer.from('FABE', 'ascii');
//     buf2End.copy(compdefeBuffer, msg.length + buf.length);

//     def checksum = computeChecksum(compdefeBuffer, idx1, idx2);
//     def checksumBuf = Buffer.from(encodeNumber(checksum));
//     checksumBuf.copy(compdefeBuffer, 39);
//     return compdefeBuffer;
// }

// def encode(a, b, c, h) {
//     a = new Uint8Array(a, c, h);
//     for (c = 0; c < h; c++) a[c] = b >> 8 * (h - 1 - c) & 255;
// }

// def readHex(str, index, length) {
//     def s = str?.toString('ascii', index, index + length);
//     if (s?.startsWith('0x')) s = s?.substr(2);
//     return parseInt(s, 16);
// }

// def readString(str, index, length) {
//     return str?.toString('ascii', index, index + length);
// }

// def parseIncomingMessage(data) {
//     log.debug "inMsg: ${data}"
//     Integer idx = 0;
//     Map message = [:];
//     message?.service = readString(data, data?.toString()?.length() - 4, 4);

//     if (message?.service == 'TUNE') {
//         message?.checksum = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;
//         def contentLength = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;
//         message?.content = readString(data, idx, contentLength - 4 - idx);
//         log.debug "msgCont: ${message?.content}"
//         if (message?.content?.startsWith('{') && message.content.endsWith('}')) {
//             try {
//                 message.content = JSON.parse(message.content);
//             }
//             catch (e) {}
//         }
//     }
//     else if (message.service == 'FABE') {
//         message.messageType = readString(data, idx, 3);
//         idx += 4;
//         message.channel = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;
//         message.messageId = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;
//         message.moreFlag = readString(data, idx, 1);
//         idx += 2; // 1 + delimiter;
//         message.seq = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;
//         message.checksum = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;

//         def contentLength = readHex(data, idx, 10);
//         idx += 11; // 10 + delimiter;

//         message.content = [:];
//         message.content.messageType = readString(data, idx, 3);
//         idx += 4;

//         if (message.channel == 0x361) { // GW_HANDSHAKE_CHANNEL
//             if (message.content.messageType == 'ACK') {
//                 def length = readHex(data, idx, 10);
//                 idx += 11; // 10 + delimiter;
//                 message.content.protocolVersion = readString(data, idx, length);
//                 idx += length + 1;
//                 length = readHex(data, idx, 10);
//                 idx += 11; // 10 + delimiter;
//                 message.content.connectionUUID = readString(data, idx, length);
//                 idx += length + 1;
//                 message.content.established = readHex(data, idx, 10);
//                 idx += 11; // 10 + delimiter;
//                 message.content.timestampINI = readHex(data, idx, 18);
//                 idx += 19; // 18 + delimiter;
//                 message.content.timestampACK = readHex(data, idx, 18);
//                 idx += 19; // 18 + delimiter;
//             }
//         }
//         else if (message.channel == 0x362) { // GW_CHANNEL
//             if (message.content.messageType == 'GWM') {
//                 message.content.subMessageType = readString(data, idx, 3);
//                 idx += 4;
//                 message.content.channel = readHex(data, idx, 10);
//                 idx += 11; // 10 + delimiter;

//                 if (message.content.channel == 0xb479) { // DEE_WEBSITE_MESSAGING
//                     def length = readHex(data, idx, 10);
//                     idx += 11; // 10 + delimiter;
//                     message.content.destinationIdentityUrn = readString(data, idx, length);
//                     idx += length + 1;

//                     length = readHex(data, idx, 10);
//                     idx += 11; // 10 + delimiter;
//                     def idData = readString(data, idx, length);
//                     idx += length + 1;

//                     idData = idData.split(' ');
//                     message.content.deviceIdentityUrn = idData[0];
//                     message.content.payload = idData[1];
//                     if (!message.content.payload) {
//                         message.content.payload = readString(data, idx, data.length - 4 - idx);
//                     }
//                     if (message.content.payload.startsWith('{') && message.content.payload.endsWith('}')) {
//                         try {
//                             message.content.payload = JSON.parse(message.content.payload);
//                             if (message.content.payload && message.content.payload.payload && message.content.payload.payload instanceof String) {
//                                 message.content.payload.payload = JSON.parse(message.content.payload.payload);
//                             }
//                         }
//                         catch (e) {}
//                     }
//                 }
//             }
//         }
//         else if (message.channel == 0x65) { // CHANNEL_FOR_HEARTBEAT
//             idx -= 1; // no delimiter!
//             message.content.payloadData = data.slice(idx, data.length - 4);
//         }
//     }
//     //console.log(JSON.stringify(message, null, 4));
//     return message;
// }

// def computeChecksum(a, f, k) {
//     if (k < f) throw "Invalid checksum exclusion window!";
//     a = a?.toByteArray();
//     def h = 0
//     def l = 0
//     for (def e = 0; e < a?.toString()?.length(); e++) {
//         if(e != f) {
//             l += c(a[e] << ((e & 3 ^ 3) << 3))
//             h += b(l, 32)
//             l = c(l & 4294967295)
//         } else { e = k - 1 }
//     }
//     for (; h;) l += h; h = b(l, 32); l &= 4294967295;
//     return c(l);
// }

// def b(a, b) {
//     for (a = c(a); 0 != b && 0 != a;) a = Math.floor(a / 2); b--;
//     return a;
// }

// def c(a) {
//     0 > a && (a = 4294967295 + a + 1);
//     return a;
// }
