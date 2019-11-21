package ricky.easybrowser.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    public static boolean isValidUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        if (url.startsWith("http://")
                || url.startsWith("https://")
                || url.startsWith("ftp://")
                || url.startsWith("file://")) {
            return true;
        }
        if ("about:blank".equals(url)) {
            return true;
        }
        return false;
    }
}
