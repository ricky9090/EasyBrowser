package ricky.easybrowser.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }
}
