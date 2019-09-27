var messageId = Math.floor(1E9 * Math.random());

function encodePing() {
    function encode(a, b, c, h) {
        a = new Uint8Array(a, c, h);
        for (c = 0; c < h; c++) {
            a[c] = b >> 8 * (h - 1 - c) & 255;
        }
    }

    messageId++;
    let msg = 'MSG 0x00000065 '; // Message-type and Channel = CHANNEL_FOR_HEARTBEAT;
    msg += encodeNumber(messageId) + ' f 0x00000001 ';
    let idx1 = msg.length;
    msg += '0x00000000 '; // Checksum!
    let idx2 = msg.length;
    msg += '0x00000062 '; // length content

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
    console.dir(n)
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

    let checksum = computeChecksum(completeBuffer, idx1, idx2);
    let checksumBuf = Buffer.from(encodeNumber(checksum));
    checksumBuf.copy(completeBuffer, 39);
    console.dir(completeBuffer)
    return completeBuffer;
}

function encodeNumber(val, byteLen) {
    if (!byteLen) byteLen = 8;
    let str = val.toString(16);
    while (str.length < byteLen) str = '0' + str;
    return '0x' + str;
}

function generateUUID() {
    for (var a = [], b = 0; 36 > b; b++) {
        var c = "rrrrrrrr-rrrr-4rrr-srrr-rrrrrrrrrrrr".charAt(b);
        if ("r" === c || "s" === c) {
            var d = Math.floor(16 * Math.random());
            "s" === c && (d = d & 3 | 8);
            a.push(d.toString(16));
        } else a.push(c);
    }
    return a.join("");
}

function computeChecksum(a, f, k) {
    function b(a, b) {
        for (a = c(a); 0 != b && 0 != a;) a = Math.floor(a / 2), b--;
        console.log(`b(${a});`, a);
        return a;
    }

    function c(a) {
        0 > a && (a = 4294967295 + a + 1);
        console.log(`c(${a});`, a);
        return a;
    }

    if (k < f) throw "Invalid checksum exclusion window!";
    a = new Uint8Array(a);
    for (var h = 0, l = 0, e = 0; e < a.length; e++) e != f ? (l += c(a[e] << ((e & 3 ^ 3) << 3)), h += b(l, 32), l = c(l & 4294967295)) : e = k - 1;
    for (; h;) l += h, h = b(l, 32), l &= 4294967295;
    return c(l);
}

function encodeGWHandshake() {
    //pubrelBuf = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
    // messageId++;
    let msg = 'MSG 0x00000361 '; // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
    msg += encodeNumber(messageId) + ' f 0x00000001 ';
    let idx1 = msg.length;
    msg += '0x00000000 '; // Checksum!
    let idx2 = msg.length;
    msg += '0x0000009b '; // length content
    msg += 'INI 0x00000003 1.0 0x00000024 '; // content part 1
    msg += generateUUID();
    msg += ' ';
    msg += encodeNumber(Date.now(), 16);
    msg += ' END FABE';
    let completeBuffer = Buffer.from(msg, 'ascii');

    let checksum = computeChecksum(completeBuffer, idx1, idx2);
    let checksumBuf = Buffer.from(encodeNumber(checksum));
    console.log('checksumBuf: ', checksumBuf);
    checksumBuf.copy(completeBuffer, 39);
    let test = new String(completeBuffer);
    console.log('test: ', test);
    return completeBuffer;
}

function encodeGWRegister() {
    //pubrelBuf = new Buffer('MSG 0x00000362 0x0e414e46 f 0x00000001 0xf904b9f5 0x00000109 GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE');
    messageId++;
    let msg = 'MSG 0x00000362 '; // Message-type and Channel = GW_CHANNEL;
    msg += encodeNumber(messageId) + ' f 0x00000001 ';
    let idx1 = msg.length;
    msg += '0x00000000 '; // Checksum!
    let idx2 = msg.length;
    msg += '0x00000109 '; // length content
    msg += 'GWM MSG 0x0000b479 0x0000003b urn:tcomm-endpoint:device:deviceType:0:deviceSerialNumber:0 0x00000041 urn:tcomm-endpoint:service:serviceName:DeeWebsiteMessagingService {"command":"REGISTER_CONNECTION"}FABE';
    let completeBuffer = Buffer.from(msg, 'ascii');

    let checksum = computeChecksum(completeBuffer, idx1, idx2);
    let checksumBuf = Buffer.from(encodeNumber(checksum));
    checksumBuf.copy(completeBuffer, 39);
    return completeBuffer;
}

encodePing();