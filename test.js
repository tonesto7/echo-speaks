/* jshint -W097 */
/* jshint -W030 */
/* jshint strict: false */
/* jslint node: true */
/* jslint esversion: 6 */
const WebSocket = require('ws');
const EventEmitter = require('events');

class AlexaWsMqtt extends EventEmitter {

    constructor(options, cookie) {
        super();

        this._options = options;
        this.stop = false;
        let serialArr = null;
        this.cookie = cookie;
        if (cookie) serialArr = cookie.match(/ubid-[a-z]+=([^;]+);/);
        if (!serialArr || !serialArr[1]) {
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Cookie incomplete : ' + JSON.stringify(serialArr));
            return undefined;
        }
        this.accountSerial = serialArr[1];
        this.websocket = null;
        this.pingPongInterval = null;
        this.errorRetryCounter = 0;
        this.reconnectTimeout = null;
        this.pongTimeout = null;
        this.connectionActive = false;

        this.messageId = Math.floor(1E9 * Math.random());
    }

    connect() {
        const urlTime = Date.now();
        let amazonPage = '.' + this._options.amazonPage;
        if (amazonPage === '.amazon.com') amazonPage = '-js.amazon.com'; // Special Handling for US!
        const url = `https://dp-gw-na${amazonPage}/?x-amz-device-type=ALEGCNGL9K0HM&x-amz-device-serial=${this.accountSerial}-${urlTime}`;
        try {
            this.websocket = new WebSocket(url, [],
                {
                    'perMessageDeflate': true,
                    'protocolVersion': 13,

                    'headers': {
                        'Connection': 'keep-alive, Upgrade',
                        'Upgrade': 'websocket',
                        'Host': 'dp-gw-na.' + this._options.amazonPage,
                        'Origin': 'https://alexa.' + this._options.amazonPage,
                        'Pragma': 'no-cache',
                        'Cache-Control': 'no-cache',
                        //'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
                        //'Accept-Language': 'de,en-US;q=0.7,en;q=0.3',
                        //'Sec-WebSocket-Key': 'aV/ud2q+G4pTtOhlt/Amww==',
                        //'Sec-WebSocket-Extensions': 'permessage-deflate', // 'x-webkit-deflate-frame',
                        //'User-Agent': 'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15G77 PitanguiBridge/2.2.219248.0-[HARDWARE=iPhone10_4][SOFTWARE=11.4.1]',
                        'Cookie': this.cookie,
                    }
                });
        }
        catch (err) {
            this.emit('error', err);
            return;
        }
        let msgCounter = 0;
        let initTimeout = null;

        this.websocket.on('open', () => {
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Open: ' + url);
            this.connectionActive = false;

            initTimeout = setTimeout(() => {
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Initialization not done within 30s');
                this.websocket.close();
            }, 30000);

            // tell Tuning Service that we support "A:H" protocol = AlphaPrococol
            const msg = new Buffer('0x99d4f71a 0x0000001d A:HTUNE');
            //console.log('SEND: ' + msg.toString('ascii'));
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Initialization Msg 1 sent');
            this.websocket.send(msg);
        });

        this.websocket.on('close', (code, reason) => {
            this.websocket = null;
            this.connectionActive = false;
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Close: ' + code + ': ' + reason);
            if (initTimeout) {
                clearTimeout(initTimeout);
                initTimeout = null;
            }
            if (this.pingPongInterval) {
                clearInterval(this.pingPongInterval);
                this.pingPongInterval = null;
            }
            if (this.pongTimeout) {
                clearTimeout(this.pongTimeout);
                this.pongTimeout = null;
            }
            if (code === 4001 && reason.startsWith('before - Could not find any')) { // code = 40001, reason = "before - Could not find any vali"
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Cookie invalid!');
                this.emit('disconnect', false, 'Cookie invalid');
                return;
            }
            if (this.stop) return;
            if (this.errorRetryCounter > 100) {
                this.emit('disconnect', false, 'Too many failed retries. Check cookie and data');
                return;
            }
            let retryDelay = this.errorRetryCounter * 5 + 5;
            if (retryDelay > 60) retryDelay = 60;
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Retry Connection in ' + retryDelay + 's');
            this.emit('disconnect', true, 'Retry Connection in ' + retryDelay + 's');
            this.reconnectTimeout = setTimeout(() => {
                this.reconnectTimeout = null;
                this.connect();
            }, retryDelay * 1000);
        });

        this.websocket.on('error', (error) => {
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Error: ' + error);
            this.emit('error', error);
            this.websocket.terminate();
        });

        this.websocket.on('unexpected-response', (request, response) => {
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Unexpected Response: ' + JSON.stringify(response));
        });

        this.websocket.on('message', (data) => {
            let message = this.parseIncomingMessage(data);
            if (msgCounter === 0) { // initialization
                if (message.content.protocolName) {
                    if (message.content.protocolName !== 'A:H') {
                        this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Server requests unknown protocol: ' + message.content.protocolName);
                    }
                }
                else {
                    this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Unexpected Response: ' + JSON.stringify(message));
                }
                let msg = new Buffer('0xa6f6a951 0x0000009c {"protocolName":"A:H","parameters":{"AlphaProtocolHandler.receiveWindowSize":"16","AlphaProtocolHandler.maxFragmentSize":"16000"}}TUNE');
                //console.log('SEND: ' + msg.toString('ascii'));
                this.websocket.send(msg);
                //msg = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
                msg = this.encodeGWHandshake();
                //console.log('SEND: ' + msg.toString('ascii'));
                this.websocket.send(msg);
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Initialization Msg 2+3 sent');
            }
            else if (msgCounter === 1) {
                //let msg = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
                let msg = this.encodeGWRegister();
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Initialization Msg 4 (Register Connection) sent');
                //console.log('SEND: ' + msg.toString('ascii'));
                this.websocket.send(msg);

                //msg = new Buffer('4D53472030783030303030303635203078306534313465343720662030783030303030303031203078626332666262356620307830303030303036322050494E00000000D1098D8CD1098D8C000000070052006500670075006C0061007246414245', 'hex'); // "MSG 0x00000065 0x0e414e47 f 0x00000001 0xbc2fbb5f 0x00000062 PIN" + 30 + "FABE"
                msg = this.encodePing();
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Send First Ping');
                //console.log('SEND: ' + msg.toString('hex'));
                this.websocket.send(msg);

                this.pingPongInterval = setInterval(() => {
                    //let msg = new Buffer('4D53472030783030303030303635203078306534313465343720662030783030303030303031203078626332666262356620307830303030303036322050494E00000000D1098D8CD1098D8C000000070052006500670075006C0061007246414245', 'hex'); // "MSG 0x00000065 0x0e414e47 f 0x00000001 0xbc2fbb5f 0x00000062 PIN" + 30 + "FABE"
                    let msg = this.encodePing();
                    this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Send Ping');
                    //console.log('SEND: ' + msg.toString('hex'));
                    this.websocket.send(msg);

                    this.pongTimeout = setTimeout(() => {
                        this.pongTimeout = null;
                        this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: No Pong received after 30s');
                        this.websocket.close();
                    }, 30000);
                }, 180000);
            }
            msgCounter++;
            if (msgCounter < 3) return;

            const incomingMsg = data.toString('ascii');
            //if (incomingMsg.includes('PON') && incomingMsg.includes('\u0000R\u0000e\u0000g\u0000u\u0000l\u0000a\u0000r')) {
            if (message.service === 'FABE' && message.content && message.content.messageType === 'PON' && message.content.payloadData && message.content.payloadData.includes('\u0000R\u0000e\u0000g\u0000u\u0000l\u0000a\u0000r')) {
                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Received Pong');
                if (initTimeout) {
                    clearTimeout(initTimeout);
                    initTimeout = null;
                    this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Initialization completed');
                    this.emit('connect');
                }
                if (this.pongTimeout) {
                    clearTimeout(this.pongTimeout);
                    this.pongTimeout = null;
                }
                this.connectionActive = true;
                return;
            }
            else if (message.content.payload) {
                let command = message.content.payload.command;
                let payload = message.content.payload.payload;

                this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Command ' + command + ': ' + JSON.stringify(payload, null, 4));
                this.emit('command', command, payload);
                return;
            }
            this._options.logger && this._options.logger('Alexa-Remote WS-MQTT: Unknown Data (' + msgCounter + '): ' + incomingMsg);
            this.emit('unknown', incomingMsg);
        });
    }


    encodeNumber(val, byteLen) {
        if (!byteLen) byteLen = 8;
        let str = val.toString(16);
        while (str.length < byteLen) str = '0' + str;
        return '0x' +str;
    }

    generateUUID() {
        for (var a = [], b = 0; 36 > b; b++) {
            var c = "rrrrrrrr-rrrr-4rrr-srrr-rrrrrrrrrrrr".charAt(b);
            if ("r" === c || "s" === c) {
                var d = Math.floor(16 * Math.random());
                "s" === c && (d = d & 3 | 8);
                a.push(d.toString(16));
            }
            else a.push(c);
        }
        return a.join("");
    }

    encodeGWHandshake() {
        //pubrelBuf = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
        this.messageId++;
        let msg = 'MSG 0x00000361 '; // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
        msg += this.encodeNumber(this.messageId) + ' f 0x00000001 ';
        let idx1 = msg.length;
        msg += '0x00000000 '; // Checksum!
        let idx2 = msg.length;
        msg += '0x0000009b '; // length content
        msg += 'INI 0x00000003 1.0 0x00000024 '; // content part 1
        msg += this.generateUUID();
        msg += ' ';
        msg += this.encodeNumber(Date.now(), 16);
        msg += ' END FABE';
        let completeBuffer = Buffer.from(msg, 'ascii');

        let checksum = this.computeChecksum(completeBuffer, idx1, idx2);
        let checksumBuf = Buffer.from(this.encodeNumber(checksum));
        checksumBuf.copy(completeBuffer, 39);
        return completeBuffer;
    }

    encodeGWRegister() {
        //pubrelBuf = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
        this.messageId++;
        let msg = 'MSG 0x00000362 '; // Message-type and Channel = GW_CHANNEL;
        msg += this.encodeNumber(this.messageId) + ' f 0x00000001 ';
        let idx1 = msg.length;
        msg += '0x00000000 '; // Checksum!
        let idx2 = msg.length;
        msg += '0x00000109 '; // length content
        msg += 'GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE';
        let completeBuffer = Buffer.from(msg, 'ascii');

        let checksum = this.computeChecksum(completeBuffer, idx1, idx2);
        let checksumBuf = Buffer.from(this.encodeNumber(checksum));
        checksumBuf.copy(completeBuffer, 39);
        return completeBuffer;
    }

    encodePing() {
        function encode(a, b, c, h) {
            a = new Uint8Array(a, c, h);
            for (c = 0; c < h; c++) a[c] = b >> 8 * (h - 1 - c) & 255;
        }

        this.messageId++;
        let msg = 'MSG 0x00000065 '; // Message-type and Channel = CHANNEL_FOR_HEARTBEAT;
        msg += this.encodeNumber(this.messageId) + ' f 0x00000001 ';
        let idx1 = msg.length;
        msg += '0x00000000 '; // Checksum!
        let idx2 = msg.length;
        msg+= '0x00000062 '; // length content

        let completeBuffer = Buffer.alloc(0x62, 0);
        let startBuffer = Buffer.from(msg, 'ascii');
        startBuffer.copy(completeBuffer);

        const header = 'PIN';
        const payload = 'Regular'; // g = h.length
        let n = new ArrayBuffer(header.length + 4 + 8 + 4 + 2 * payload.length);
        let idx = 0;
        let u = new Uint8Array(n, idx, header.length);
        let l = 0;
        let e = Date.now();

        for (let q = 0; q < header.length; q++) u[q] = header.charCodeAt(q);
        idx += header.length;
        encode(n, l, idx, 4);
        idx += 4;
        encode(n, e, idx, 8);
        idx += 8;
        encode(n, payload.length, idx, 4);
        idx += 4;
        u = new Uint8Array(n, idx, payload.length * 2);
        let q;
        for (q = 0; q < payload.length; q++) {
            u[q * 2] = 0;
            u[q * 2 + 1] = payload.charCodeAt(q);
        }
        let buf = Buffer.from(n);
        buf.copy(completeBuffer, msg.length);

        let buf2End = Buffer.from('FABE', 'ascii');
        buf2End.copy(completeBuffer, msg.length + buf.length);

        let checksum = this.computeChecksum(completeBuffer, idx1, idx2);
        let checksumBuf = Buffer.from(this.encodeNumber(checksum));
        checksumBuf.copy(completeBuffer, 39);
        return completeBuffer;
    }

    computeChecksum(a, f, k) {
        function b(a, b) {
            for (a = c(a); 0 != b && 0 != a;) a = Math.floor(a / 2), b--;
            return a;
        }

        function c(a) {
            0 > a && (a = 4294967295 + a + 1);
            return a;
        }

        if (k < f) throw "Invalid checksum exclusion window!";
        a = new Uint8Array(a);
        for (var h = 0, l = 0, e = 0; e < a.length; e++) e != f ? (l += c(a[e] << ((e & 3 ^ 3) << 3)), h += b(l, 32), l = c(l & 4294967295)) : e = k - 1;
        for (; h;) l += h, h = b(l, 32), l &= 4294967295;
        return c(l);
    }

    parseIncomingMessage(data) {
        function readHex(index, length) {
            let str = data.toString('ascii', index, index + length);
            if (str.startsWith('0x')) str = str.substr(2);
            return parseInt(str, 16);
        }

        function readString(index, length) {
            return data.toString('ascii', index, index + length);
        }

        let idx = 0;
        const message = {};
        message.service = readString(data.length - 4, 4);

        if (message.service === 'TUNE') {
            message.checksum = readHex(idx, 10);
            idx += 11; // 10 + delimiter;
            let contentLength = readHex(idx, 10);
            idx += 11; // 10 + delimiter;
            message.content = readString(idx, contentLength - 4 - idx);
            if (message.content.startsWith('{') && message.content.endsWith('}')) {
                try {
                    message.content = JSON.parse(message.content);
                }
                catch (e) {}
            }
        }
        else if (message.service === 'FABE') {
            message.messageType = readString(idx, 3);
            idx += 4;
            message.channel = readHex(idx, 10);
            idx += 11; // 10 + delimiter;
            message.messageId = readHex(idx, 10);
            idx += 11; // 10 + delimiter;
            message.moreFlag = readString(idx, 1);
            idx += 2; // 1 + delimiter;
            message.seq = readHex(idx, 10);
            idx += 11; // 10 + delimiter;
            message.checksum = readHex(idx, 10);
            idx += 11; // 10 + delimiter;

            let contentLength = readHex(idx, 10);
            idx += 11; // 10 + delimiter;

            message.content = {};
            message.content.messageType = readString(idx, 3);
            idx += 4;

            if (message.channel === 0x361) { // GW_HANDSHAKE_CHANNEL
                if (message.content.messageType === 'ACK') {
                    let length = readHex(idx, 10);
                    idx += 11; // 10 + delimiter;
                    message.content.protocolVersion = readString(idx, length);
                    idx += length + 1;
                    length = readHex(idx, 10);
                    idx += 11; // 10 + delimiter;
                    message.content.connectionUUID = readString(idx, length);
                    idx += length + 1;
                    message.content.established = readHex(idx, 10);
                    idx += 11; // 10 + delimiter;
                    message.content.timestampINI = readHex(idx, 18);
                    idx += 19; // 18 + delimiter;
                    message.content.timestampACK = readHex(idx, 18);
                    idx += 19; // 18 + delimiter;
                }
            }
            else if (message.channel === 0x362) { // GW_CHANNEL
                if (message.content.messageType === 'GWM') {
                    message.content.subMessageType = readString(idx, 3);
                    idx += 4;
                    message.content.channel = readHex(idx, 10);
                    idx += 11; // 10 + delimiter;

                    if (message.content.channel === 0xb479) { // DEE_WEBSITE_MESSAGING
                        let length = readHex(idx, 10);
                        idx += 11; // 10 + delimiter;
                        message.content.destinationIdentityUrn = readString(idx, length);
                        idx += length + 1;

                        length = readHex(idx, 10);
                        idx += 11; // 10 + delimiter;
                        let idData = readString(idx, length);
                        idx += length + 1;

                        idData = idData.split(' ');
                        message.content.deviceIdentityUrn = idData[0];
                        message.content.payload = idData[1];
                        if (!message.content.payload) {
                            message.content.payload = readString(idx, data.length - 4 - idx);
                        }
                        if (message.content.payload.startsWith('{') && message.content.payload.endsWith('}')) {
                            try {
                                message.content.payload = JSON.parse(message.content.payload);
                                if (message.content.payload && message.content.payload.payload && typeof message.content.payload.payload === 'string') {
                                    message.content.payload.payload = JSON.parse(message.content.payload.payload);
                                }
                            }
                            catch (e) {}
                        }
                    }
                }
            }
            else if (message.channel === 0x65) { // CHANNEL_FOR_HEARTBEAT
                idx -= 1; // no delimiter!
                message.content.payloadData = data.slice(idx, data.length - 4);
            }
        }
        //console.log(JSON.stringify(message, null, 4));
        return message;
    }

    disconnect() {
        if (!this.websocket) return;
        this.stop = true;
        this.websocket.close();
    }
}


module.exports = AlexaWsMqtt;