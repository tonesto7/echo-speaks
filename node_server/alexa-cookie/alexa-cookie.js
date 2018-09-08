/* jshint -W097 */
/* jshint -W030 */
/* jshint strict: false */
/* jslint node: true */
/* jslint esversion: 6 */
"use strict";

/**
 * partly based on Amazon Alexa Remote Control (PLAIN shell)
 * http://blog.loetzimmer.de/2017/10/amazon-alexa-hort-auf-die-shell-echo.html AND on
 * https://github.com/thorsten-gehrig/alexa-remote-control
 * and much enhanced ...
 */

const https = require('https');
const querystring = require('querystring');
const url = require('url');
// const logger2 = require('../logger');
const os = require('os');
const modifyResponse = require('http-proxy-response-rewrite');
const express = require('express');
const proxy = require('http-proxy-middleware');

const defaultAmazonPage = 'amazon.com';
const defaultAlexaServiceHost = 'pitangui.amazon.com';
const defaultUserAgent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0';
const defaultUserAgentLinux = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36';
//const defaultUserAgentMacOs = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36';
const defaultAcceptLanguage = 'en-US';

let proxyServer;

function customStringify(v, func, intent) {
    const cache = new Map();
    return JSON.stringify(v, function(key, value) {
        if (typeof value === 'object' && value !== null) {
            if (cache.get(value)) {
                // Circular reference found, discard key
                return;
            }
            // Store value in our map
            cache.set(value, true);
        }
        return value;
    }, intent);
}

function addCookies(Cookie, headers) {
    if (!headers || !headers['set-cookie']) return Cookie;
    for (let cookie of headers['set-cookie']) {
        cookie = cookie.replace(/(^[^;]+;).*/, '$1') + ' ';
        if (Cookie.indexOf(cookie) === -1 && cookie !== 'ap-fid=""; ') {
            if (Cookie && !Cookie.endsWith('; ')) Cookie += '; ';
            Cookie += cookie;
        }
    }
    Cookie = Cookie.replace(/[; ]*$/, '');
    return Cookie;
}

function generateAlexaCookie(email, password, _options, callback) {

    function request(options, info, callback) {

        _options.logger && _options.logger('Alexa-Cookie: Sending Request with ' + JSON.stringify(options));
        if (typeof info === 'function') {
            callback = info;
            info = {
                requests: []
            };
        }

        let removeContentLength;
        if (options.headers && options.headers['Content-Length']) {
            if (!options.body) delete options.headers['Content-Length'];
        } else if (options.body) {
            if (!options.headers) options.headers = {};
            options.headers['Content-Length'] = options.body.length;
            removeContentLength = true;
        }

        let req = https.request(options, function(res) {
            let body  = "";
            let r = res;
            info.requests.push({ options: options, response: res });

            if (options.followRedirects !== false && res.statusCode >= 300 && res.statusCode < 400) {
                _options.logger && _options.logger('Alexa-Cookie: Response (' + res.statusCode + ')' + (res.headers.location ? ' - Redirect to ' + res.headers.location : ''));
                //options.url = res.headers.location;
                let u = url.parse(res.headers.location);
                if (u.host) options.host = u.host;
                options.path = u.path;
                options.method = 'GET';
                options.body = '';
                options.headers.Cookie = Cookie = addCookies(Cookie, res.headers);

                res.connection.end();
                return request(options, info, callback);
            } else {
                _options.logger && _options.logger('Alexa-Cookie: Response (' + res.statusCode + ')');
                res.on('data', function(chunk) {
                    body += chunk;
                });

                res.on('end', function() {
                    if (removeContentLength) delete options.headers['Content-Length'];
                    res.connection.end();
                    callback && callback(0, res, body, info);
                });
            }
        });

        req.on('error', function(e) {
            if (typeof callback === 'function' && callback.length >= 2) {
                return callback(e, null, null, info);
            }
        });
        if (options && options.body) {
            req.write(options.body);
        }
        req.end();
    }

    function getFields(body) {
        body = body.replace(/[\n\r]/g, ' ');
        let re = /^.*?("hidden"\s*name=".*$)/;
        let ar = re.exec(body);
        if (!ar || ar.length < 2) return {};
        let h;
        re = /.*?name="([^"]+)"[\s^\s]*value="([^"]+).*?"/g;
        let data = {};
        while ((h = re.exec(ar[1])) !== null) {
            if (h[1] !== 'rememberMe') {
                data[h[1]] = h[2];
            }
        }
        return data;
    }

    function getCSRFFromCookies(cookie, _options, callback) {
        // get CSRF
        let options = {
            'host': _options.alexaServiceHost,
            'path': '/api/language',
            'method': 'GET',
            'headers': {
                'DNT': '1',
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36',
                'Connection': 'keep-alive',
                'Referer': 'https://alexa.' + _options.amazonPage + '/spa/index.html',
                'Cookie': cookie,
                'Accept': '*/*',
                'Origin': 'https://alexa.' + _options.amazonPage
            }
        };

        _options.logger && _options.logger('Alexa-Cookie: Step 4: get CSRF');
        request(options, (error, response, body, info) => {
            cookie = addCookies(cookie, response.headers);
            let ar = /csrf=([^;]+)/.exec(cookie);
            let csrf = ar ? ar[1] : undefined;
            _options.logger && _options.logger('Alexa-Cookie: Result: csrf=' + csrf + ', Cookie=' + cookie);
            callback && callback(null, {
                cookie: cookie,
                csrf: csrf
            });
        });
    }

    function initConfig() {
        _options.amazonPage = _options.amazonPage || defaultAmazonPage;
        _options.logger && _options.logger('Alexa-Cookie: Use as Login-Amazon-URL: ' + _options.amazonPage);

        _options.alexaServiceHost = _options.alexaServiceHost || defaultAlexaServiceHost;
        _options.logger && _options.logger('Alexa-Cookie: Use as Alexa-Service-Host: ' + _options.alexaServiceHost);

        if (!_options.userAgent) {
            let platform = os.platform();
            if (platform === 'win32') {
                _options.userAgent = defaultUserAgent;
            }
            /*else if (platform === 'darwin') {
                _options.userAgent = defaultUserAgentMacOs;
            }*/
            else {
                _options.userAgent = defaultUserAgentLinux;
            }
        }
        _options.logger && _options.logger('Alexa-Cookie: Use as User-Agent: ' + _options.userAgent);

        _options.acceptLanguage = _options.acceptLanguage || defaultAcceptLanguage;
        _options.logger && _options.logger('Alexa-Cookie: Use as Accept-Language: ' + _options.acceptLanguage);

        if (_options.setupProxy && !_options.proxyOwnIp) {
            _options.logger && _options.logger('Alexa-Cookie: Own-IP Setting muissing for Proxy. Disabling!');
            _options.setupProxy = false;
        }
        if (_options.setupProxy) {
            _options.setupProxy = true;
            _options.proxyPort = _options.proxyPort || 0;
            _options.proxyListenBind = _options.proxyListenBind || '0.0.0.0';
            _options.logger && _options.logger('Alexa-Cookie: Proxy-Mode enabled if needed: ' + _options.proxyOwnIp + ':' + _options.proxyPort + ' to listen on ' + _options.proxyListenBind);
        } else {
            _options.setupProxy = false;
            _options.logger && _options.logger('Alexa-Cookie: Proxy mode disabled');
        }
        _options.proxyLogLevel = _options.proxyLogLevel || 'warn';
    }

    if (typeof _options === 'function') {
        callback = _options;
        _options = {};
    }

    let Cookie = '';

    initConfig();

    // get first cookie and write redirection target into referer
    let options = {
        host: 'alexa.' + _options.amazonPage,
        path: '',
        method: 'GET',
        headers: {
            'DNT': '1',
            'Upgrade-Insecure-Requests': '1',
            'User-Agent': _options.userAgent,
            'Accept-Language': _options.acceptLanguage,
            'Connection': 'keep-alive',
            'Accept': '*/*'
        },
    };
    _options.logger && _options.logger('Alexa-Cookie: Step 1: get first cookie and authentication redirect');
    request(options, (error, response, body, info) => {

        let lastRequestOptions = info.requests[info.requests.length - 1].options;
        // login empty to generate session
        Cookie = addCookies(Cookie, response.headers);
        let options = {
            host: 'www.' + _options.amazonPage,
            path: '/ap/signin',
            method: 'POST',
            headers: {
                'DNT': '1',
                'Upgrade-Insecure-Requests': '1',
                'User-Agent': _options.userAgent,
                'Accept-Language': _options.acceptLanguage,
                'Connection': 'keep-alive',
                'Content-Type': 'application/x-www-form-urlencoded',
                'Referer': 'https://' + lastRequestOptions.host + lastRequestOptions.path,
                'Cookie': Cookie,
                'Accept': '*/*'
            },
            gzip: true,
            body: querystring.stringify(getFields(body))
        };
        _options.logger && _options.logger('Alexa-Cookie: Step 2: login empty to generate session');
        request(options, (error, response, body, info) => {
            // login with filled out form
            //  !!! referer now contains session in URL
            options.host = 'www.' + _options.amazonPage;
            options.path = '/ap/signin';
            options.method = 'POST';
            options.headers.Cookie = Cookie = addCookies(Cookie, response.headers);
            let ar = options.headers.Cookie.match(/session-id=([^;]+)/);
            options.headers.Referer = `https://www.${_options.amazonPage}/ap/signin/${ar[1]}`;
            options.body = getFields(body);
            options.body.email = email || '';
            options.body.password = password || '';
            options.body = querystring.stringify(options.body, null, null, { encodeURIComponent: encodeURIComponent });

            _options.logger && _options.logger('Alexa-Cookie: Step 3: login with filled form, referer contains session id');
            request(options, (error, response, body, info) => {
                let lastRequestOptions = info.requests[info.requests.length - 1].options;

                // check whether the login has been successful or exit otherwise
                if (!lastRequestOptions.host.startsWith('alexa') || !lastRequestOptions.path.endsWith('.html')) {
                    let errMessage = 'Login unsuccessfull. Please check credentials.';
                    const amazonMessage = body.match(/auth-warning-message-box[\S\s]*"a-alert-heading">([^<]*)[\S\s]*<li><[^>]*>\s*([^<\n]*)\s*</);
                    if (amazonMessage && amazonMessage[1] && amazonMessage[2]) {
                        errMessage = `Amazon-Login-Error: ${amazonMessage[1]}: ${amazonMessage[2]}`;
                    }
                    if (_options.setupProxy) {
                        if (proxyServer) {
                            errMessage += ` You can try to get the cookie manually by opening http://${_options.proxyOwnIp}:${_options.proxyPort}/ with your browser.`;
                        } else {
                            initAmazonProxy(_options, email, password,
                                (err, cookie) => {
                                    if (err) {
                                        callback && callback(err, cookie);
                                        return;
                                    }
                                    getCSRFFromCookies(cookie, _options, callback);
                                },
                                (server) => {
                                    proxyServer = server;
                                    if (_options.proxyPort === 0) {
                                        _options.proxyPort = proxyServer.address().port;
                                    }
                                    errMessage += ` You can try to get the cookie manually by opening http://${_options.proxyOwnIp}:${_options.proxyPort}/ with your browser.`;
                                    callback && callback(new Error(errMessage), null);
                                }
                            );
                            return;
                        }
                    } else {}
                    callback && callback(new Error(errMessage), null);
                    return;
                }

                return getCSRFFromCookies(Cookie, _options, callback);
            });
        });
    });
}

function initAmazonProxy(_options, email, password, callbackCookie, callbackListening) {
    // proxy middleware options
    const optionsAlexa = {
        target: `https://alexa.${_options.amazonPage}`,
        changeOrigin: true,
        ws: false,
        pathRewrite: {}, // enhanced below
        router: router,
        hostRewrite: true,
        followRedirects: false,
        logLevel: _options.proxyLogLevel,
        onError: onError,
        onProxyRes: onProxyRes,
        onProxyReq: onProxyReq,
        headers: {
            'user-agent': _options.userAgent,
            'accept-language': _options.acceptLanguage
        },
        cookieDomainRewrite: { // enhanced below
            "*": ""
        }
    };
    optionsAlexa.pathRewrite[`^/www.${_options.amazonPage}`] = '';
    optionsAlexa.pathRewrite[`^/alexa.${_options.amazonPage}`] = '';
    optionsAlexa.cookieDomainRewrite[`.${_options.amazonPage}`] = _options.proxyOwnIp;
    optionsAlexa.cookieDomainRewrite[_options.amazonPage] = _options.proxyOwnIp;
    if (_options.logger) optionsAlexa.logProvider = function logProvider(provider) {
        return {
            log: _options.logger.log || _options.logger,
            debug: _options.logger.debug || _options.logger,
            info: _options.logger.info || _options.logger,
            warn: _options.logger.warn || _options.logger,
            error: _options.logger.error || _options.logger
        };
    };

    function router(req) {
        const url = (req.originalUrl || req.url);
        _options.logger && _options.logger(url + ' / ' + req.method + ' / ' + JSON.stringify(req.headers));
        if (req.headers.host === `${_options.proxyOwnIp}:${_options.proxyPort}`) {
            if (url.startsWith(`/www.${_options.amazonPage}/`)) {
                return `https://www.${_options.amazonPage}`;
            } else if (url.startsWith(`/alexa.${_options.amazonPage}/`)) {
                return `https://alexa.${_options.amazonPage}`;
            } else if (req.headers.referer) {
                if (req.headers.referer.startsWith(`http://${_options.proxyOwnIp}:${_options.proxyPort}/www.${_options.amazonPage}/`)) {
                    return `https://www.${_options.amazonPage}`;
                } else if (req.headers.referer.startsWith(`http://${_options.proxyOwnIp}:${_options.proxyPort}/alexa.${_options.amazonPage}/`)) {
                    return `https://alexa.${_options.amazonPage}`;
                }
            }
        }
        return `https://alexa.${_options.amazonPage}`;
    }

    function onError(err, req, res) {
        _options.logger && _options.logger('ERROR: ' + err);
        res.writeHead(500, {
            'Content-Type': 'text/plain'
        });
        res.end('Proxy-Error: ' + err);
    }

    function replaceHosts(data) {
        const amazonRegex = new RegExp(`https?://www.${_options.amazonPage}/`.replace(/\./g, "\\."), 'g');
        const alexaRegex = new RegExp(`https?://alexa.${_options.amazonPage}/`.replace(/\./g, "\\."), 'g');
        data = data.replace(amazonRegex, `http://${_options.proxyOwnIp}:${_options.proxyPort}/www.${_options.amazonPage}/`);
        data = data.replace(alexaRegex, `http://${_options.proxyOwnIp}:${_options.proxyPort}/alexa.${_options.amazonPage}/`);
        return data;
    }

    function replaceHostsBack(data) {
        const amazonRegex = new RegExp(`http://${_options.proxyOwnIp}:${_options.proxyPort}/www.${_options.amazonPage}/`.replace(/\./g, "\\."), 'g');
        const alexaRegex = new RegExp(`http://${_options.proxyOwnIp}:${_options.proxyPort}/alexa.${_options.amazonPage}/`.replace(/\./g, "\\."), 'g');
        data = data.replace(amazonRegex, `https://www.${_options.amazonPage}/`);
        data = data.replace(alexaRegex, `https://alexa.${_options.amazonPage}/`);
        return data;
    }

    function onProxyReq(proxyReq, req, res) {
        const url = req.originalUrl || req.url;
        if (url.endsWith('.ico') || url.endsWith('.js') || url.endsWith('.ttf') || url.endsWith('.svg') || url.endsWith('.png') || url.endsWith('.appcache')) return;
        if (url.startsWith('/ap/uedata')) return;

        _options.logger && _options.logger('Alexa-Cookie: Proxy-Request: ' + req.method + ' ' + url);
        //_options.logger && _options.logger('Alexa-Cookie: Proxy-Request-Data: ' + customStringify(proxyReq, null, 2));

        let modified = false;
        if (req.method === 'POST') {
            if (proxyReq._headers && proxyReq._headers.referer) {
                proxyReq._headers.referer = replaceHostsBack(proxyReq._headers.referer);
                _options.logger && _options.logger('Alexa-Cookie: Modify headers: Changed Referer');
                modified = true;
            }
            if (proxyReq._headers && proxyReq._headers.origin !== 'https://' + proxyReq._headers.host) {
                delete proxyReq._headers.origin;
                _options.logger && _options.logger('Alexa-Cookie: Modify headers: Delete Origin');
                modified = true;
            }

            let postBody = '';
            req.on('data', chunk => {
                postBody += chunk.toString(); // convert Buffer to string
            });
            req.on('end', () => {
                //_options.proxyLogLevel === 'debug' && _options.logger && _options.logger('Alexa-Cookie: Catched POST parameter: ' + postBody);
                const postParams = querystring.parse(postBody);
                if (email && email.length && postParams.email !== email) {
                    let errMessage = 'Alexa-Cookie: Email entered on Login Page via Proxy differs from set email! You should use the same email to allow automatic cookie retrieval.';
                    _options.logger && _options.logger(errMessage);
                    callbackCookie && callbackCookie(new Error(errMessage), null);
                }
                if (password && password.length && postParams.password !== password) {
                    let errMessage = 'Alexa-Cookie: Password entered on Login Page via Proxy differs from set email! You should use the same password to allow automatic cookie retrieval.';
                    _options.logger && _options.logger(errMessage);
                    callbackCookie && callbackCookie(new Error(errMessage), null);
                }
            });
        }
        _options.proxyLogLevel === 'debug' && _options.logger && _options.logger('Alexa-Cookie: Proxy-Request: (modified:' + modified + ')' + customStringify(proxyReq, null, 2));
    }

    function onProxyRes(proxyRes, req, res) {
        const url = req.originalUrl || req.url;
        if (url.endsWith('.ico') || url.endsWith('.js') || url.endsWith('.ttf') || url.endsWith('.svg') || url.endsWith('.png') || url.endsWith('.appcache')) return;
        if (url.startsWith('/ap/uedata')) return;
        //_options.logger && _options.logger('Proxy-Response: ' + customStringify(proxyRes, null, 2));
        _options.proxyLogLevel === 'debug' && _options.logger && _options.logger('Alexa-Cookie: Proxy-Response Headers: ' + customStringify(proxyRes._headers, null, 2));
        _options.proxyLogLevel === 'debug' && _options.logger && _options.logger('Alexa-Cookie: Proxy-Response Outgoing: ' + customStringify(proxyRes.socket.parser.outgoing, null, 2));
        //_options.logger && _options.logger('Proxy-Response RES!!: ' + customStringify(res, null, 2));

        if (proxyRes && proxyRes.headers && proxyRes.headers['set-cookie']) {
            // make sure cookies are also sent to http by remove secure flags
            for (let i = 0; i < proxyRes.headers['set-cookie'].length; i++) {
                proxyRes.headers['set-cookie'][i] = proxyRes.headers['set-cookie'][i].replace('Secure;', '');
            }
        }

        if (
            (proxyRes.socket && proxyRes.socket._host === `alexa.${_options.amazonPage}` && proxyRes.socket.parser.outgoing && proxyRes.socket.parser.outgoing.method === 'GET' && proxyRes.socket.parser.outgoing.path === '/spa/index.html') ||
            (proxyRes.socket && proxyRes.socket.parser.outgoing && proxyRes.socket.parser.outgoing._headers.location && proxyRes.socket.parser.outgoing._headers.location === `https://alexa.${_options.amazonPage}/spa/index.html`) ||
            (proxyRes.headers.location && proxyRes.headers.location === `https://alexa.${_options.amazonPage}/spa/index.html`)
        ) {
            _options.logger && _options.logger('Alexa-Cookie: Proxy detected SUCCESS!!');
            proxyRes.statusCode = 302;
            proxyRes.headers.location = `http://${_options.proxyOwnIp}:${_options.proxyPort}/cookie-success`;
            delete proxyRes.headers.referer;

            const finalCookie = proxyRes.headers.cookie || proxyRes.socket.parser.outgoing._headers.cookie;
            _options.logger && _options.logger('Alexa-Cookie: Proxy catched cookie: ' + finalCookie);

            callbackCookie && callbackCookie(null, finalCookie);
            return;
        }

        // If we detect a redirect, rewrite the location header
        if (proxyRes.headers.location) {
            proxyRes.headers.location = replaceHosts(proxyRes.headers.location);
            _options.logger && _options.logger('Redirect: Location ----> ' + proxyRes.headers.location);
            return;
        }
        if (!proxyRes || !proxyRes.headers || !proxyRes.headers['content-encoding']) return;

        modifyResponse(res, proxyRes.headers['content-encoding'], function(body) {
            if (body) {
                const bodyOrig = body;
                body = replaceHosts(body);
                if (body !== bodyOrig) _options.logger && _options.logger('Alexa-Cookie: MODIFIED Response Body to rewrite URLs');
            }
            return body;
        });
    }

    // create the proxy (without context)
    const myProxy = proxy('!/cookie-success', optionsAlexa);

    // mount `exampleProxy` in web server
    const app = express();

    app.use(myProxy);
    app.get('/cookie-success', function(req, res) {
        res.send('<b>Amazon Alexa Cookie successfully retrieved. You can close the browser.</b>');
    });
    let server = app.listen(_options.proxyPort, _options.proxyListenBind, function() {
        _options.logger && _options.logger('Alexa-Cookie: Proxy-Server listening on port ' + server.address().port);
        callbackListening(server);
    });
}

function stopProxyServer(callback) {
    if (proxyServer) {
        proxyServer.close(() => {
            callback && callback();
        });
    }
    proxyServer = null;
}

module.exports.generateAlexaCookie = generateAlexaCookie;
module.exports.stopProxyServer = stopProxyServer;