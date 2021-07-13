library (
    base: "app",
    author: "tonesto7",
    category: "timedate_utils",
    description: "Time/Date Helper Code",
    name: "timedate_utils",
    namespace: "echospeaks20210701",
    documentationLink: ""
)

String formatDt(Date dt, Boolean tzChg=true) {
    def tf = new java.text.SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(tzChg) { if(location.timeZone) { tf.setTimeZone(location?.timeZone) } }
    return (String)tf.format(dt)
}

String parseDt(String pFormat, String dt, Boolean tzFmt=true) {
    Date newDt = Date.parse(pFormat, dt)
    return formatDt(newDt, tzFmt)
}

String parseFmtDt(String parseFmt, String newFmt, dt) {
    Date newDt = Date.parse(parseFmt, dt?.toString())
    def tf = new java.text.SimpleDateFormat(newFmt)
    if(location.timeZone) { tf.setTimeZone(location?.timeZone) }
    return (String)tf.format(newDt)
}

String getDtNow() {
    Date now = new Date()
    return formatDt(now)
}

String epochToTime(Date tm) {
    def tf = new java.text.SimpleDateFormat("h:mm a")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    return (String)tf.format(tm)
}

String time2Str(time) {
    if(time) {
        Date t = timeToday(time as String, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f.setTimeZone(location?.timeZone ?: timeZone(time))
        return (String)f.format(t)
    }
    return sNULL
}

Long GetTimeDiffSeconds(String lastDate, String sender=sNULL) {
    try {
        if(lastDate?.contains("dtNow")) { return 10000 }
        Date lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
        Long start = lastDt.getTime()
        Long stop = now()
        Long diff = (stop - start) / 1000L
        return diff.abs()
    } catch (ex) {
        logError("GetTimeDiffSeconds Exception: (${sender ? "$sender | " : sBLANK}lastDate: $lastDate): ${ex}", false, ex)
        return 10000L
    }
}

static String seconds2Duration(Integer timeSec, Boolean postfix=true, Integer tk=2) {
    Integer years = Math.floor(timeSec / 31536000); timeSec -= years * 31536000
    Integer months = Math.floor(timeSec / 31536000); timeSec -= months * 2592000
    Integer days = Math.floor(timeSec / 86400); timeSec -= days * 86400
    Integer hours = Math.floor(timeSec / 3600); timeSec -= hours * 3600
    Integer minutes = Math.floor(timeSec / 60); timeSec -= minutes * 60
    Integer seconds = Integer.parseInt((timeSec % 60) as String, 10)
    Map d = [y: years, mn: months, d: days, h: hours, m: minutes, s: seconds]
    List l = []
    if(d.d > 0) { l.push("${d.d} ${pluralize(d.d, "day")}") }
    if(d.h > 0) { l.push("${d.h} ${pluralize(d.h, "hour")}") }
    if(d.m > 0) { l.push("${d.m} ${pluralize(d.m, "min")}") }
    if(d.s > 0) { l.push("${d.s} ${pluralize(d.s, "sec")}") }
    return l.size() ? "${l.take(tk ?: 2)?.join(", ")}${postfix ? " ago" : sBLANK}".toString() : "Not Sure"
}