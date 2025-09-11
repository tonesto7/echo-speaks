library (
    base: "app",
    author: "tonesto7",
    category: "static_final_variables",
    description: "Static Final Field Variables",
    name: "static_final_variables",
    namespace: "echospeaks20210701",
    documentationLink: ""
)

// STRING VARIABLES
@Field static final String branchFLD      = 'master'
@Field static final String platformFLD    = 'Hubitat'

@Field static final String sNULL          = (String)null
@Field static final String sBLANK         = ''
@Field static final String sSPACE         = ' '
@Field static final String sBULLET        = '\u2022'
@Field static final String sBULLETINV     = '\u25E6'
@Field static final String sSQUARE        = '\u29C8'
@Field static final String sPLUS          = '\u002B'
@Field static final String sRIGHTARR      = '\u02C3'
@Field static final String sFRNFACE       = '\u2639'
@Field static final String okSymFLD       = "\u2713"
@Field static final String notOkSymFLD    = "\u2715"
@Field static final String sPAUSESymFLD   = "\u275A\u275A"
@Field static final String sLINEBR        = '<br>'
@Field static final String sFALSE         = 'false'
@Field static final String sTRUE          = 'true'
@Field static final String sBOOL          = 'bool'
@Field static final String sENUM          = 'enum'
@Field static final String sNUMBER        = 'number'
@Field static final String sTEXT          = 'text'
@Field static final String sTIME          = 'time'
@Field static final String sMODE          = 'mode'
@Field static final String sAPPJSON       = 'application/json'
@Field static final String sIN_IGNORE     = 'In Ignore Device Input'
// @Field static final String sARM_AWAY      = 'ARMED_AWAY' // DISABLED: Alexa Guard features disabled
// @Field static final String sARM_STAY      = 'ARMED_STAY' // DISABLED: Alexa Guard features disabled
@Field static final String sCOMPLT        = 'complete'
@Field static final String sMEDIUM        = 'medium'
@Field static final String sSMALL         = 'small'
@Field static final String sCLR4D9        = '#2784D9'
@Field static final String sCLRRED        = 'red'
@Field static final String sCLRRED2       = '#cc2d3b'
@Field static final String sCLRGRY        = 'gray'
@Field static final String sCLRGRN        = 'green'
@Field static final String sCLRGRN2       = '#43d843'
@Field static final String sCLRORG        = 'orange'
@Field static final String sTTM           = 'Tap to modify...'
@Field static final String sTTC           = 'Tap to configure...'
@Field static final String sTTP           = 'Tap to proceed...'
@Field static final String sTTVD          = 'Tap to view details...'
@Field static final String sTTS           = 'Tap to select...'
@Field static final String sSETTINGS      = 'settings'
@Field static final String sUnknown       = 'Unknown'
@Field static final String sUNKNOWN       = 'unknown'
@Field static final String sRESET         = 'reset'
@Field static final String sHEROKU        = 'heroku'
@Field static final String sEXTNRL        = 'external'
@Field static final String sDEBUG         = 'debug'
@Field static final String sAMAZONORNG    = 'amazon_orange'
@Field static final String sDEVICES       = 'devices'
@Field static final String sSWITCH        = 'switch'
@Field static final String sCHKBOX        = 'checkbox'
@Field static final String sCOMMAND       = 'command'
@Field static final String sANY           = 'any'
@Field static final String sARE           = 'are'
@Field static final String sBETWEEN       = 'between'
@Field static final String sNBETWEEN      = 'not_between'
@Field static final String sBELOW         = 'below'
@Field static final String sABOVE         = 'above'
@Field static final String sEQUALS        = 'equals'
@Field static final String sANN           = 'announcement'
@Field static final String sANNT          = 'announcement_tiered'
@Field static final String sSPEAK         = 'speak'
@Field static final String sSPEAKP        = 'speak_parallel'
@Field static final String sSPEAKPT       = 'speak_parallel_tiered'
@Field static final String sSPEAKT        = 'speak_tiered'
@Field static final String sWEATH         = 'weather'
@Field static final String sCSUNRISE      = 'Sunrise'
@Field static final String sCSUNSET       = 'Sunset'
@Field static final String sSTTIME        = 'start_time'
@Field static final String sSPDKNB        = 'speed_knob'
@Field static final String sTEMP          = 'temperature'
@Field static final String sTHERMTEMP     = 'thermostatTemperature'
@Field static final String sCOOLSP        = 'coolingSetpoint'
@Field static final String sHEATSP        = 'heatingSetpoint'
@Field static final String sMOTION        = 'motion'
@Field static final String sLEVEL         = 'level'
@Field static final String sBATT          = 'battery'
@Field static final String sTHERM         = 'thermostat'
@Field static final String sHUMID         = 'humidity'
@Field static final String sKEYPAD        = 'keypad'
@Field static final String sLOCK          = 'lock'
@Field static final String sCONTACT       = 'contact'
@Field static final String sWATER         = 'water'
@Field static final String sPOWER         = 'power'
@Field static final String sVALVE         = 'valve'
@Field static final String sCHGTO         = 'changes to'
@Field static final String sQUES          = 'question'
@Field static final String sDELAYT        = 'delay_time'
@Field static final String sEQ            = 'equal'
@Field static final String sPUSHED        = 'pushed'
@Field static final String sRELEASED      = 'released'
@Field static final String sHELD          = 'held'
@Field static final String sDBLTAP        = 'doubleTapped'
@Field static final String sALRMSYSST     = 'alarmSystemStatus'
@Field static final String sPISTNEXEC     = 'pistonExecuted'
@Field static final String sLRM           = "light_restore_map"
@Field static final String zoneHistFLD    = 'zoneHistory'
@Field static final String sASTR          = 'a'
@Field static final String sTSTR          = 't'
@Field static final String sLASTWU = 'lastwebCoREUpdDt'
@Field static final String sHMLF = 'theHistMapLockFLD'

// ARRAY LIST VARIABLES
@Field static final List<String> lONOFF        = ['on', 'off']
@Field static final List<String> lANY          = ['any']
@Field static final List<String> lOPNCLS       = ['open', 'closed']
@Field static final List<String> lACTINACT     = ['active', 'inactive']
@Field static final List<String> lSUNRISESET   = ['sunrise', 'sunset']
@Field static final List<String> lWETDRY       = ['wet', 'dry']
@Field static final List<String> lLOCKUNL      = ['locked', 'unlocked']
@Field static final List<String> lDETECTCLR    = ['detected', 'clear', 'tested']
@Field static final List<String> lPRES         = ['present', 'not present']
@Field static final List<String> lSEC          = ['disarmed', 'armed home', 'armed away', 'unknown']

// MAP VARIABLES
// @Field static final Map seqItemsAvailFLD = [
//     other: [
//         "weather":null, "traffic":null, "flashbriefing":null, "goodnews":null, "goodmorning":null, "goodnight":null, "cleanup":null,
//         "singasong":null, "tellstory":null, "funfact":null, "joke":null, "playsearch":null, "calendartoday":null,
//         "calendartomorrow":null, "calendarnext":null, "stop":null, "stopalldevices":null,
//         "dnd_duration": "2H30M", "dnd_time": "00:30", "dnd_all_duration": "2H30M", "dnd_all_time": "00:30",
//         "cannedtts_random": ["goodbye", "confirmations", "goodmorning", "compliments", "birthday", "goodnight", "iamhome"],
//         "sound": "message",
//         "date": null, "time": null,
//         "wait": "value (seconds)", "volume": "value (0-100)", "speak": "message", "announcement": "message",
//         "announcementall": "message", "pushnotification": "message", "email": null, "voicecmdtxt": "voice command as text"
//     ],
//     music: [
//         "amazonmusic": "AMAZON_MUSIC", "applemusic": "APPLE_MUSIC", "iheartradio": "I_HEART_RADIO", "pandora": "PANDORA",
//         "spotify": "SPOTIFY", "tunein": "TUNEIN", "cloudplayer": "CLOUDPLAYER"
//     ],
//     musicAlt: [
//         "amazonmusic": "amazonMusic", "applemusic": "appleMusic", "iheartradio": "iHeartRadio", "pandora": "pandoraRadio",
//         "spotify": "spotify", "tunein": "tuneInRadio", "cloudplayer": "cloudPlayer"
//     ]
// ]