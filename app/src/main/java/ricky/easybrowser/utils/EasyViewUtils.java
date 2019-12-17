package ricky.easybrowser.utils;

import android.content.Context;
import android.util.TypedValue;

public class EasyViewUtils {

    public static float dp2px(Context context, int dpValue) {
        if (context == null) {
            return 0f;
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, int spValue) {
        if (context == null) {
            return 0f;
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
