library (
        base: "app",
        author: "tonesto7",
        category: "duplicationtypes",
        description: "App Duplication Types Map",
        name: "duplicationtypes",
        namespace: "echospeaks20210701",
        documentationLink: ""
)

@Field static final Map appDuplicationTypesMapFLD = [
    stat: [
        bool: ["notif_pushover", "notif_alexa_mobile", "logInfo", "logWarn", "logError", "logDebug", "logTrace", "enableWebCoRE"],
        enum: ["triggerEvents", "act_EchoZones", "actionType", "cond_alarm", "cond_months", /* "notif_pushover_devices", "notif_pushover_priority", "notif_pushover_sound", */ "trig_alarm", "trig_guard"],
        mode: ["cond_mode", "trig_mode"],
        number: [],
        text: ["appLbl"]
    ],
//
    ends: [
        bool: ["_all", "_avg", "_once", "_send_push", "_use_custom", "_stop_on_clear", "_db", "Pause", "_vol_per_zone"],
        enum: ["_cmd", "_type", "_routineExecuted",
               "_EchoDevices",
               "_scheduled_sunState", "_scheduled_recurrence", "_scheduled_days", "_scheduled_weeks", "_scheduled_weekdays", "_scheduled_months", "_scheduled_daynums", "_scheduled_type",
               "_routine_run", "_mode_run", "_piston_run", "_alarm_run", "_rt", "_rt_wd", "_nums", "_Codes", "_pistonExecuted", "_days", "_months", "_alarm_events"],
        number: ["_wait", "_low", "_high", "_equal", "_delay", "_cnt", "_volume", "_offset", "_after", "_after_repeat", "_rt_ed", "_volume_change", "_volume_restore"],
        text: ["_txt", "_sms_numbers", "_label", "_date", "_message"],
        mode: ["_modes"],
        time: ["_time_start", "_time_stop", "_time", "_scheduled_time"]
    ],
    caps: [
        _devs: "notification",
        _acceleration: "accelerationSensor",
        _battery: "battery",
        _contact: "contactSensor",
        _door: "garageDoorControl",
        _doors_open: "garageDoorControl",
        _doors_close: "garageDoorControl",
        _temperature: "temperatureMeasurement",
        _illuminance: "illuminanceMeasurement",
        _humidity: "relativeHumidityMeasurement",
        _motion: "motionSensor",
        _level: "switchLevel",
        _button: "button",
        _pushed: "pushableButton",
        _held: "holdableButton",
        _released: "releasableButton",
        _doubleTapped: "doubleTapableButton",
        _presence: "presenceSensor",
        _sirens: "alarm",
        _switch: "switch",
        _power: "powerMeter",
        _windowShade: "windowShades",
        _water: "waterSensor",
        _valve: "valve",
        _thermostatOperatingState: "thermostat",
        _thermostatMode: "thermostat",
        _thermostatFanMode: "thermostat",
        _thermostatTemperature: "thermostat",
        _thermostatHeatingSetpoint: "thermostat",
        _thermostatCoolingSetpoint: "thermostat",
        _carbonMonoxide: "carbonMonoxideDetector",
        _smoke: "smokeDetector",
        _lock: "lock",
        _unlock: "lock",
        _securityKeypad: "securityKeypad",
        _disarm: "securityKeypad",
        _armHome: "securityKeypad",
        _armAway: "securityKeypad",
        _switches_off: "switch",
        _switches_on: "switch",
        _lights: "level",
        _color: "colorControl",
        _EchoDeviceList: ""
    ],
    dev: [
        _scene: "sceneActivator",
//        _EchoDevices: "EchoSpeaksDevice",
//        _EchoDeviceList: "EchoSpeaksDevice"
    ]
]