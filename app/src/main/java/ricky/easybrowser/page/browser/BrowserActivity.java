package ricky.easybrowser.page.browser;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.page.setting.SettingDialogKt;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener {

    private static final String SETTING_DIALOG_TAG = "setting_dialog";
    private static final String TAB_DIALOG_TAG = "tab_dialog";

    FrameLayout webContentFrame;
    BrowserNavBar navBar;

    TabCacheManager tabCacheManager;
    TabDialogKt tabDialog;
    SettingDialogKt settingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        if (tabCacheManager == null) {
            tabCacheManager = new TabCacheManager(this, getSupportFragmentManager(), 3, R.id.web_content_frame);
        }

        webContentFrame = findViewById(R.id.web_content_frame);

        navBar = findViewById(R.id.nav_bar);
        navBar.setNavListener(new BrowserNavBar.OnNavClickListener() {
            @Override
            public void onItemClick(View itemView) {
                int id = itemView.getId();
                if (id == R.id.nav_show_tabs) {
                    showTabDialog();
                } else if (id == R.id.nav_back) {
                    onBackPressed();
                } else if (id == R.id.nav_home) {
                    tabCacheManager.gotoHome();
                } else if (id == R.id.nav_setting) {
                    showSettingDialog();
                }
            }
        });


        if (savedInstanceState == null) {
            // 默认添加一个新标签页
            tabCacheManager.addNewTab(getString(R.string.new_tab_welcome));
        } else {
            // 当横竖屏切换后，将复原的Fragment重新推入cache
            List<Fragment> restoredFragmentList = getSupportFragmentManager().getFragments();
            if (restoredFragmentList.size() > 0) {
                for (Fragment target : restoredFragmentList) {
                    if (target instanceof NewTabFragmentV2 && target.getArguments() != null) {
                        TabCacheManager.TabInfo info = new TabCacheManager.TabInfo();
                        info.setTitle(target.getArguments().getString(NewTabFragmentV2.ARG_TITLE));
                        info.setTag(target.getArguments().getString(NewTabFragmentV2.ARG_TAG));
                        // TODO 检查去掉clearCache后，是否重复put，TabInfo已经重写equals方法
                        tabCacheManager.restoreTabCache(info, target);
                    }
                }
            }
            Fragment prev = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
            if (prev instanceof TabDialogKt) {
                ((TabDialogKt) prev).setTabCacheManager(tabCacheManager);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabCacheManager.closeAllTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTabTitleChanged(String title) {
        tabCacheManager.updateTabTitle();
    }

    @Override
    public void onBackPressed() {
        if (FragmentBackHandleHelper.isFragmentBackHandled(getSupportFragmentManager())) {
            return;
        }

        super.onBackPressed();
    }

    public void addNewTab(String uriStr, boolean backstage) {
        Uri uri = null;
        try {
            uri = Uri.parse(uriStr);
        } catch (Exception e) {

        }
        if (uri == null) {
            return;
        }
        tabCacheManager.addNewTab(uri, backstage);
    }

    private void showTabDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
        if (prev != null) {
            ((TabDialogKt) prev).dismiss();
            return;
        }
        if (tabDialog == null) {
            tabDialog = new TabDialogKt();
            tabDialog.setTabCacheManager(tabCacheManager);
            tabDialog.setCancelable(false);
        }
        tabDialog.show(getSupportFragmentManager(), TAB_DIALOG_TAG);
    }

    private void showSettingDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(SETTING_DIALOG_TAG);
        if (prev != null) {
            ((SettingDialogKt) prev).dismiss();
            return;
        }

        if (settingDialog == null) {
            settingDialog = new SettingDialogKt();
            settingDialog.setCancelable(false);
        }

        settingDialog.show(getSupportFragmentManager(), SETTING_DIALOG_TAG);
    }
}
