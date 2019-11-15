package ricky.easybrowser.utils;

import android.content.Context;
import android.net.Uri;

import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.page.browser.IBrowser;

public class TabHelper {

    public static void createTab(Context context, int titleResId, String uriStr, boolean backStage) {
        if (context == null) {
            return;
        }

        String title = context.getResources().getString(titleResId);
        createTab(context, title, uriStr, backStage);
    }

    public static void createTab(Context context, String title, String uriStr, boolean backStage) {
        IBrowser browser = null;
        if (context instanceof IBrowser) {
            browser = (IBrowser) context;
        }
        if (browser == null) {
            return;
        }
        if (StringUtils.isEmpty(uriStr)) {
            return;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(uriStr);
        } catch (Exception e) {
            uri = null;
        }
        if (uri == null) {
            return;
        }
        TabInfo tabInfo = TabInfo.create(
                System.currentTimeMillis() + "",
                title,
                uri);
        browser.provideTabController().onTabCreate(tabInfo, backStage);
    }
}
