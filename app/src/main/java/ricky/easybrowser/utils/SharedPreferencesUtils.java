package ricky.easybrowser.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    public static final String KEY_NO_PIC_MODE = "no_pic_mode";
    public static final String KEY_FIRST_BOOT = "first_boot";
    public static final String KEY_SITE_LIST_CREATED = "site_list_created";

    public static SharedPreferences getSettingSP(Context context) {
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences("setting-sp", Context.MODE_PRIVATE);
    }


}
