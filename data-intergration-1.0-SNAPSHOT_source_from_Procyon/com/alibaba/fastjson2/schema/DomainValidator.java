// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.schema;

import java.net.IDN;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DomainValidator
{
    static final Pattern domainRegex;
    private static final String[] INFRASTRUCTURE_TLDS;
    private static final String[] GENERIC_TLDS;
    private static final String[] COUNTRY_CODE_TLDS;
    
    public static boolean isValid(String domain) {
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        if (domain.length() > 253) {
            return false;
        }
        final Matcher matcher = DomainValidator.domainRegex.matcher(domain);
        if (matcher.matches()) {
            final int count = matcher.groupCount();
            final String[] groups = new String[count];
            for (int j = 0; j < count; ++j) {
                groups[j] = matcher.group(j + 1);
            }
            return isValidTld(groups[0]);
        }
        return false;
    }
    
    public static boolean isValidTld(String tld) {
        tld = unicodeToASCII(tld);
        String key = unicodeToASCII(tld).toLowerCase(Locale.ENGLISH);
        if (key.startsWith(".")) {
            key = key.substring(1);
        }
        return Arrays.binarySearch(DomainValidator.INFRASTRUCTURE_TLDS, key) >= 0 || Arrays.binarySearch(DomainValidator.GENERIC_TLDS, key) >= 0 || Arrays.binarySearch(DomainValidator.COUNTRY_CODE_TLDS, key) >= 0;
    }
    
    static String unicodeToASCII(final String input) {
        boolean ascii = true;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) > '\u007f') {
                ascii = false;
                break;
            }
        }
        if (ascii) {
            return input;
        }
        return IDN.toASCII(input);
    }
    
    static {
        domainRegex = Pattern.compile("^(?:\\p{Alnum}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?\\.)+(\\p{Alpha}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?)\\.?$");
        INFRASTRUCTURE_TLDS = new String[] { "arpa" };
        GENERIC_TLDS = new String[] { "aaa", "aarp", "abb", "abbott", "abogado", "academy", "accenture", "accountant", "accountants", "aco", "active", "actor", "ads", "adult", "aeg", "aero", "afl", "agency", "aig", "airforce", "airtel", "allfinanz", "alsace", "amica", "amsterdam", "android", "apartments", "app", "apple", "aquarelle", "aramco", "archi", "army", "arte", "asia", "associates", "attorney", "auction", "audio", "auto", "autos", "axa", "azure", "band", "bank", "bar", "barcelona", "barclaycard", "barclays", "bargains", "bauhaus", "bayern", "bbc", "bbva", "bcn", "beats", "beer", "bentley", "berlin", "best", "bet", "bharti", "bible", "bid", "bike", "bing", "bingo", "bio", "biz", "black", "blackfriday", "bloomberg", "blue", "bms", "bmw", "bnl", "bnpparibas", "boats", "bom", "bond", "boo", "boots", "boutique", "bradesco", "bridgestone", "broker", "brother", "brussels", "budapest", "build", "builders", "business", "buzz", "bzh", "cab", "cafe", "cal", "camera", "camp", "cancerresearch", "canon", "capetown", "capital", "car", "caravan", "cards", "care", "career", "careers", "cars", "cartier", "casa", "cash", "casino", "cat", "catering", "cba", "cbn", "ceb", "center", "ceo", "cern", "cfa", "cfd", "chanel", "channel", "chat", "cheap", "chloe", "christmas", "chrome", "church", "cipriani", "cisco", "citic", "city", "claims", "cleaning", "click", "clinic", "clothing", "cloud", "club", "clubmed", "coach", "codes", "coffee", "college", "cologne", "com", "commbank", "community", "company", "computer", "condos", "construction", "consulting", "contractors", "cooking", "cool", "coop", "corsica", "country", "coupons", "courses", "credit", "creditcard", "cricket", "crown", "crs", "cruises", "csc", "cuisinella", "cymru", "cyou", "dabur", "dad", "dance", "date", "dating", "datsun", "day", "dclk", "deals", "degree", "delivery", "dell", "delta", "democrat", "dental", "dentist", "desi", "design", "dev", "diamonds", "diet", "digital", "direct", "directory", "discount", "dnp", "docs", "dog", "doha", "domains", "doosan", "download", "drive", "durban", "dvag", "earth", "eat", "edu", "education", "email", "emerck", "energy", "engineer", "engineering", "enterprises", "epson", "equipment", "erni", "esq", "estate", "eurovision", "eus", "events", "everbank", "exchange", "expert", "exposed", "express", "fage", "fail", "faith", "family", "fan", "fans", "farm", "fashion", "feedback", "ferrero", "film", "final", "finance", "financial", "firmdale", "fish", "fishing", "fit", "fitness", "flights", "florist", "flowers", "flsmidth", "fly", "foo", "football", "forex", "forsale", "forum", "foundation", "frl", "frogans", "fund", "furniture", "futbol", "fyi", "gal", "gallery", "game", "garden", "gbiz", "gdn", "gea", "gent", "genting", "ggee", "gift", "gifts", "gives", "giving", "glass", "gle", "global", "globo", "gmail", "gmo", "gmx", "gold", "goldpoint", "golf", "goo", "goog", "google", "gop", "gov", "graphics", "gratis", "green", "gripe", "group", "gucci", "guge", "guide", "guitars", "guru", "hamburg", "hangout", "haus", "healthcare", "help", "here", "hermes", "hiphop", "hitachi", "hiv", "hockey", "holdings", "holiday", "homedepot", "homes", "honda", "horse", "host", "hosting", "hoteles", "hotmail", "house", "how", "hsbc", "hyundai", "ibm", "icbc", "ice", "icu", "ifm", "iinet", "immo", "immobilien", "industries", "infiniti", "info", "ing", "ink", "institute", "insure", "int", "international", "investments", "ipiranga", "irish", "ist", "istanbul", "itau", "iwc", "jaguar", "java", "jcb", "jetzt", "jewelry", "jlc", "jll", "jobs", "joburg", "jprs", "juegos", "kaufen", "kddi", "kia", "kim", "kinder", "kitchen", "kiwi", "koeln", "komatsu", "krd", "kred", "kyoto", "lacaixa", "lancaster", "land", "landrover", "lasalle", "lat", "latrobe", "law", "lawyer", "lds", "lease", "leclerc", "legal", "lexus", "lgbt", "liaison", "lidl", "life", "lighting", "limited", "limo", "linde", "link", "live", "lixil", "loan", "loans", "lol", "london", "lotte", "lotto", "love", "ltd", "ltda", "lupin", "luxe", "luxury", "madrid", "maif", "maison", "man", "management", "mango", "market", "marketing", "markets", "marriott", "mba", "media", "meet", "melbourne", "meme", "memorial", "men", "menu", "meo", "miami", "microsoft", "mil", "mini", "mma", "mobi", "moda", "moe", "moi", "mom", "monash", "money", "montblanc", "mormon", "mortgage", "moscow", "motorcycles", "mov", "movie", "movistar", "mtn", "mtpc", "mtr", "museum", "mutuelle", "nadex", "nagoya", "name", "navy", "nec", "net", "netbank", "network", "neustar", "new", "news", "nexus", "ngo", "nhk", "nico", "ninja", "nissan", "nokia", "nra", "nrw", "ntt", "nyc", "obi", "office", "okinawa", "omega", "one", "ong", "onl", "online", "ooo", "oracle", "orange", "org", "organic", "osaka", "otsuka", "ovh", "page", "panerai", "paris", "partners", "parts", "party", "pet", "pharmacy", "philips", "photo", "photography", "photos", "physio", "piaget", "pics", "pictet", "pictures", "ping", "pink", "pizza", "place", "play", "playstation", "plumbing", "plus", "pohl", "poker", "porn", "post", "praxi", "press", "pro", "prod", "productions", "prof", "properties", "property", "protection", "pub", "qpon", "quebec", "racing", "realtor", "realty", "recipes", "red", "redstone", "rehab", "reise", "reisen", "reit", "ren", "rent", "rentals", "repair", "report", "republican", "rest", "restaurant", "review", "reviews", "rich", "ricoh", "rio", "rip", "rocher", "rocks", "rodeo", "rsvp", "ruhr", "run", "rwe", "ryukyu", "saarland", "sakura", "sale", "samsung", "sandvik", "sandvikcoromant", "sanofi", "sap", "sapo", "sarl", "saxo", "sbs", "sca", "scb", "schmidt", "scholarships", "school", "schule", "schwarz", "science", "scor", "scot", "seat", "security", "seek", "sener", "services", "seven", "sew", "sex", "sexy", "shiksha", "shoes", "show", "shriram", "singles", "site", "ski", "sky", "skype", "sncf", "soccer", "social", "software", "sohu", "solar", "solutions", "sony", "soy", "space", "spiegel", "spreadbetting", "srl", "stada", "starhub", "statoil", "stc", "stcgroup", "stockholm", "studio", "study", "style", "sucks", "supplies", "supply", "support", "surf", "surgery", "suzuki", "swatch", "swiss", "sydney", "systems", "taipei", "tatamotors", "tatar", "tattoo", "tax", "taxi", "team", "tech", "technology", "tel", "telefonica", "temasek", "tennis", "thd", "theater", "theatre", "tickets", "tienda", "tips", "tires", "tirol", "today", "tokyo", "tools", "top", "toray", "toshiba", "tours", "town", "toyota", "toys", "trade", "trading", "training", "travel", "trust", "tui", "ubs", "university", "uno", "uol", "vacations", "vegas", "ventures", "versicherung", "vet", "viajes", "video", "villas", "vin", "virgin", "vision", "vista", "vistaprint", "viva", "vlaanderen", "vodka", "vote", "voting", "voto", "voyage", "wales", "walter", "wang", "watch", "webcam", "website", "wed", "wedding", "weir", "whoswho", "wien", "wiki", "williamhill", "win", "windows", "wine", "wme", "work", "works", "world", "wtc", "wtf", "xbox", "xerox", "xin", "xn--11b4c3d", "xn--1qqw23a", "xn--30rr7y", "xn--3bst00m", "xn--3ds443g", "xn--3pxu8k", "xn--42c2d9a", "xn--45q11c", "xn--4gbrim", "xn--55qw42g", "xn--55qx5d", "xn--6frz82g", "xn--6qq986b3xl", "xn--80adxhks", "xn--80asehdb", "xn--80aswg", "xn--9dbq2a", "xn--9et52u", "xn--b4w605ferd", "xn--c1avg", "xn--c2br7g", "xn--cg4bki", "xn--czr694b", "xn--czrs0t", "xn--czru2d", "xn--d1acj3b", "xn--efvy88h", "xn--estv75g", "xn--fhbei", "xn--fiq228c5hs", "xn--fiq64b", "xn--fjq720a", "xn--flw351e", "xn--hxt814e", "xn--i1b6b1a6a2e", "xn--imr513n", "xn--io0a7i", "xn--j1aef", "xn--kcrx77d1x4a", "xn--kput3i", "xn--mgba3a3ejt", "xn--mgbab2bd", "xn--mk1bu44c", "xn--mxtq1m", "xn--ngbc5azd", "xn--nqv7f", "xn--nqv7fs00ema", "xn--nyqy26a", "xn--p1acf", "xn--pssy2u", "xn--q9jyb4c", "xn--qcka1pmc", "xn--rhqv96g", "xn--ses554g", "xn--t60b56a", "xn--tckwe", "xn--unup4y", "xn--vermgensberater-ctb", "xn--vermgensberatung-pwb", "xn--vhquv", "xn--vuq861b", "xn--xhq521b", "xn--zfr164b", "xperia", "xxx", "xyz", "yachts", "yamaxun", "yandex", "yodobashi", "yoga", "yokohama", "youtube", "zara", "zip", "zone", "zuerich" };
        COUNTRY_CODE_TLDS = new String[] { "ac", "ad", "ae", "af", "ag", "ai", "al", "am", "ao", "aq", "ar", "as", "at", "au", "aw", "ax", "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cr", "cu", "cv", "cw", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "er", "es", "et", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "me", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "rs", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st", "su", "sv", "sx", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "xn--3e0b707e", "xn--45brj9c", "xn--80ao21a", "xn--90a3ac", "xn--90ais", "xn--clchc0ea0b2g2a9gcd", "xn--d1alf", "xn--fiqs8s", "xn--fiqz9s", "xn--fpcrj9c3d", "xn--fzc2c9e2c", "xn--gecrj9c", "xn--h2brj9c", "xn--j1amh", "xn--j6w193g", "xn--kprw13d", "xn--kpry57d", "xn--l1acc", "xn--lgbbat1ad8j", "xn--mgb9awbf", "xn--mgba3a4f16a", "xn--mgbaam7a8h", "xn--mgbayh7gpa", "xn--mgbbh1a71e", "xn--mgbc0a9azcg", "xn--mgberp4a5d4ar", "xn--mgbpl2fh", "xn--mgbtx2b", "xn--mgbx4cd0ab", "xn--node", "xn--o3cw4h", "xn--ogbpf8fl", "xn--p1ai", "xn--pgbs0dh", "xn--qxam", "xn--s9brj9c", "xn--wgbh1c", "xn--wgbl6a", "xn--xkc2al3hye2a", "xn--xkc2dl3a5ee0h", "xn--y9a3aq", "xn--yfro4i67o", "xn--ygbi2ammx", "ye", "yt", "za", "zm", "zw" };
    }
}
