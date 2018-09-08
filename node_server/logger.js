'use: strict';
const winston = require('winston');
const {
    createLogger,
    format,
    transports
} = winston;
const DailyRotateFile = require('winston-daily-rotate-file');
const fs = require('fs');
const os = require('os');
const logNamePrefix = 'Echo-Speaks';
const dataFolder = os.homedir() + '/.echo-speaks';

function initLogFolder() {
    // Create the log directory if it does not exist
    if (!fs.existsSync(dataFolder)) {
        fs.mkdirSync(dataFolder);
    }
    if (!fs.existsSync(dataFolder + '/logs')) {
        fs.mkdirSync(dataFolder + '/logs');
    }
}

winston.addColors({
    error: 'red',
    warn: 'orange',
    info: 'blue',
    verbose: 'gray',
    debug: 'green',
    silly: 'yellow'
});

let logger = createLogger({
    level: 'silly',
    levels: {
        error: 0,
        warn: 1,
        info: 2,
        verbose: 3,
        debug: 4,
        silly: 5
    },
    format: format.combine(
        format.timestamp({
            format: 'M-D-YYYY - h:mm:ssa'
        }),
        format.colorize({ all: true }),
        winston.format.simple(),
        format.align(),
        format.prettyPrint(),
        format.printf(info => {
            return `${info.timestamp} ${info.level}: ${info.message}`;
        })
    ),
    transports: [
        new transports.Console(),
        new DailyRotateFile({
            filename: dataFolder + '/logs/' + logNamePrefix + '-%DATE%.log',
            maxsize: 5242880, // 5MB
            maxFiles: 5,
        })
    ],
    exitOnError: false
});

initLogFolder();
module.exports = logger;