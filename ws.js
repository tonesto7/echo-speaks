var messageId = 377437188;
var now = 1569534310194;
var guid = "51ab9ef5-0d22-4bbf-3a07-e1872ebdceb9";


encodeNumber(val, byteLen) {
    if (!byteLen) byteLen = 8;
    let str = val.toString(16);
    while (str.length < byteLen) str = '0' + str;
    return '0x' + str;
}

generateUUID() {
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

encodeGWHandshake() {
    //pubrelBuf = new Buffer('MSG 0x00000361 0x0e414e45 f 0x00000001 0xd7c62f29 0x0000009b INI 0x00000003 1.0 0x00000024 ff1c4525-c036-4942-bf6c-a098755ac82f 0x00000164d106ce6b END FABE');
    // this.messageId++;
    let msg = 'MSG 0x00000361 '; // Message-type and Channel = GW_HANDSHAKE_CHANNEL;
    msg += this.encodeNumber(messageId) + ' f 0x00000001 ';
    let idx1 = msg.length;
    msg += '0x00000000 '; // Checksum!
    let idx2 = msg.length;
    msg += '0x0000009b '; // length content
    msg += 'INI 0x00000003 1.0 0x00000024 '; // content part 1
    msg += guid; //this.generateUUID();
    msg += ' ';
    msg += this.encodeNumber(1569534310194, 16);
    msg += ' END FABE';
    let completeBuffer = Buffer.from(msg, 'ascii');

    let checksum = this.computeChecksum(completeBuffer, idx1, idx2);
    let checksumBuf = Buffer.from(this.encodeNumber(checksum));
    console.log('checksumBuf: ', checksumBuf);
    checksumBuf.copy(completeBuffer, 39);
    let test = new String(completeBuffer);
    console.log('test: ', test);
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