library (
    base: "app",
    author: "tonesto7",
    category: "string_utils",
    description: "String Helper Code",
    name: "string_utils",
    namespace: "echospeaks20210701",
    documentationLink: ""
)

static String strCapitalize(String str) { return str ? str.toString().capitalize() : sNULL }
static String pluralizeStr(List obj, Boolean para=true) { return (obj?.size() > 1) ? "${para ? "(s)": "s"}" : sBLANK }
static String pluralize(Integer itemVal, String str) { return (itemVal > 1) ? str+"s" : str }


String UrlParamBuilder(Map items) {
    return items.collect { String k,String v -> "${k}=${URLEncoder.encode(v.toString())}" }?.join("&").toString()
}

static def getRandomItem(items) {
    def list = new ArrayList<String>()
    items?.each { list?.add(it) }
    return list?.get(new Random().nextInt(list?.size()))
}

static String randomString(Integer len) {
    def pool = ["a".."z",0..9].flatten()
    Random rand = new Random(new Date().getTime())
    def randChars = (0..len).collect { pool[rand.nextInt(pool.size())] }
    // logDebug("randomString: ${randChars?.join()}")
    return randChars.join()
}