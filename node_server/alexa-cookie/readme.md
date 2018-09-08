# alexa-cookie

Library to generate/retrieve a cookie including a csrf for alexa remote

<!--
[![NPM version](http://img.shields.io/npm/v/alexa-remote.svg)](https://www.npmjs.com/package/alexa-remote)
[![Tests](http://img.shields.io/travis/soef/alexa-remote/master.svg)](https://travis-ci.org/soef/alexa-remote)
-->
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/soef/alexa-remote/blob/master/LICENSE)

## Description
This library can be used to get the cookies needed to access Amazon Alexa services from outside. It authenticates with Amazon and gathers all needed details. These details are returned in the callback.
If the automatic authentication fails (because of security checks from amazon like a needed Captcha or because you enabled two factor authentication) the library can also setup a proxy server to allow the manual login and will catch the cookie by itself. Using this proxy you can enter needed 2FA codes or solve captchas and still do not need to trick around to get the cookie.

## Example:
```javascript 1.8
const alexaCookie = require('alexa-cookie2');

const options = { // options is optional at all
    logger: console.log,       // optional: Logger instance to get (debug) logs
    amazonPage: 'amazon.com',  // optional: possible to use with different countries, default is 'amazon.de'
    alexaServiceHost: 'pitangui.amazon.com',  // optional: possible to use with different countries, default is 'layla.amazon.de'
    acceptLanguage: 'en-US',   // optional: webpage language, should match to amazon-Page, default is 'de-DE'
    userAgent: '...',          // optional: own userAgent to use for all request, overwrites default one
    setupProxy: true,          // optional: should the library setup a proxy to get cookie when automatic way did not worked? Default false!
    proxyOwnIp: '...',         // required if proxy enabled: provide own IP or hostname to later access the proxy. needed to setup all rewriting and proxy stuff internally
    proxyPort: 3456,           // optional: use this port for the proxy, default is 0 means random port is selected
    proxyListenBind: '0.0.0.0',// optional: set this to bind the proxy to a special IP, default is '0.0.0.0'
    proxyLogLevel: 'info'      // optional: Loglevel of Proxy, default 'warn'
}

alexaCookie.generateAlexaCookie('amazon-email', 'password', options, function (err, result) {
    // IMPORTANT: can be called multiple times!! As soon as a new cookie is fetched or an error happened. Consider that!
    console.log('cookie: ' + result.cookie);
    console.log('csrf: '   + result.csrf);
    if (result && result.csrf) {
        alexaCookie.stopProxyServer();
    }
});

````

## Thanks:
A big thanks go to soef for the initial version of this library.

Partly based on [Amazon Alexa Remote Control](http://blog.loetzimmer.de/2017/10/amazon-alexa-hort-auf-die-shell-echo.html) (PLAIN shell) and [alexa-remote-control](https://github.com/thorsten-gehrig/alexa-remote-control) and the the Proxy idea from [OpenHab-Addon](https://github.com/openhab/openhab2-addons/blob/f54c9b85016758ff6d271b62d255bbe41a027928/addons/binding/org.openhab.binding.amazonechocontrol)
Thank you for that work.

## Changelog:

### 0.2.x
* (Apollon77) 0.2.2: fix encoding of special characters in email and password
* (Apollon77) 0.2.1: Cleanup to prepare release
* (Apollon77) 0.2.0: Add option to use a proxy to also retrieve the credentials if the automatic retrieval fails
* (Apollon77) 0.2.0: Optimize automatic cookie retrieval, remove MacOS user agent again because the Linux one seems to work better

### 0.1.x
* (Apollon77) 0.1.3: Use specific User-Agents for Win32, MacOS and linux based platforms
* (Apollon77) 0.1.2: Log the used user-Agent, Accept-Language and Login-URL
* (Apollon77) 0.1.1: update to get it working again and sync to [alexa-remote-control](https://github.com/thorsten-gehrig/alexa-remote-control)

### 0.0.x
* Versions by soef
