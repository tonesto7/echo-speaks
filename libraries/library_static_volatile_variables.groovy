library (
    base: "app",
    author: "tonesto7",
    category: "static_volatile_variables",
    description: "Static Volatile Field Variables",
    name: "static_volatile_variables",
    namespace: "echospeaks20210701",
    documentationLink: ""
)

// IN-MEMORY VARIABLES (Cleared only on HUB REBOOT or CODE UPDATES)
@Field volatile static Map<String,Map> historyMapFLD = [:]
@Field volatile static Map<String,Map> cookieDataFLD = [:]
