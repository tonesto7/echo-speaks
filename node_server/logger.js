'use: strict';
const homedir = require('os').homedir();
const logDir = homedir + '/.echo-speaks/logs';
const logNamePrefix = 'Echo-Speaks';
const winston = require('winston');
const moment = require('moment');
const tsFormat = () => getPrettyDt();

function getPrettyDt() {
    if (moment) {
        return moment().format('M-D-YYYY - h:mm:ssa');
    }
}

var options = {
    file: {
        level: 'info',
        filename: logDir + '/' + logNamePrefix + '-%DATE%.log',
        prettyPrint: true,
        timestamp: tsFormat,
        localTime: true,
        datePattern: 'MM-DD-YYYY',
        handleExceptions: true,
        json: true,
        maxsize: 5242880, // 5MB
        maxFiles: 5,
        colorize: false,
        prepend: true
    },
    console: {
        level: 'debug',
        handleExceptions: true,
        json: false,
        colorize: true,
        prepend: true,
        timestamp: tsFormat
    },
};

// var logger = new winston.Logger({
//     transports: [
//         new winston.transports.File(options.file),
//         new winston.transports.Console(options.console)
//     ],
//     exitOnError: false, // do not exit on handled exceptions
// });

let logger = winston.createLogger({
    level: 'info',
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.printf(info => {
            return `${info.timestamp} ${info.level}: ${info.message}`;
        })
    ),
    transports: [new winston.transports.Console()]
});

// This initializes the winston logging instance
// const logger = new(winston.Logger)({
//     levels: {
//         trace: 0,
//         input: 1,
//         verbose: 2,
//         debug: 3,
//         info: 4,
//         warn: 5,
//         error: 6
//     },
//     colors: {
//         trace: 'magenta',
//         input: 'grey',
//         verbose: 'cyan',
//         debug: 'green',
//         info: 'blue',
//         warn: 'orange',
//         error: 'red'
//     },
//     transports: [
//         new(winston.transports.Console)({
//             levels: 'error',
//             colorize: true,
//             prettyPrint: true,
//             timestamp: tsFormat
//         }),
//         new(require('winston-daily-rotate-file'))({
//             filename: logDir + '/' + logNamePrefix + '-%DATE%.log',
//             levels: 'error',
//             colorize: false,
//             prettyPrint: true,
//             timestamp: tsFormat,
//             json: false,
//             localTime: true,
//             datePattern: 'MM-DD-YYYY',
//             maxFiles: 20,
//             prepend: true
//         })
//     ],
//     exitOnError: false
// });

module.exports = logger;