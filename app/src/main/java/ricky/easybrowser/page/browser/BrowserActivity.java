package ricky.easybrowser.page.browser;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.R;
import ricky.easybrowser.entity.DaoSession;
import ricky.easybrowser.entity.HistoryEntity;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.page.setting.SettingDialogKt;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener,
        IBrowser {

    private static final String SETTING_DIALOG_TAG = "setting_dialog";
    private static final String TAB_DIALOG_TAG = "tab_dialog";

    FrameLayout webContentFrame;

    TabCacheManager tabCacheManager;
    TabDialogKt tabDialog;
    SettingDialogKt settingDialog;

    IBrowser.NavController navController;
    IBrowser.HistoryController historyController;
    IBrowser.TabController tabController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        if (tabCacheManager == null) {
            tabCacheManager = new TabCacheManager(this, getSupportFragmentManager(), 3, R.id.web_content_frame);
        }

        webContentFrame = findViewById(R.id.web_content_frame);

        if (savedInstanceState == null) {
            // 默认添加一个新标签页
            TabInfo tabInfo = new TabInfo();
            tabInfo.setTag(System.currentTimeMillis() + "");
            tabInfo.setTitle(getString(R.string.new_tab_welcome));
            ((IBrowser.TabController) tabCacheManager).onAddNewTab(tabInfo, false);
        } else {
            // 当横竖屏切换后，将复原的Fragment重新推入cache
            // FIXME 原tab列表信息未能还原，复原的tab数量为cache数量
            // 序列化tablist用于还原操作
            List<Fragment> restoredFragmentList = getSupportFragmentManager().getFragments();
            if (restoredFragmentList.size() > 0) {
                for (Fragment target : restoredFragmentList) {
                    if (target instanceof NewTabFragmentV2 && target.getArguments() != null) {
                        TabInfo info = new TabInfo();
                        info.setTitle(target.getArguments().getString(NewTabFragmentV2.ARG_TITLE));
                        info.setTag(target.getArguments().getString(NewTabFragmentV2.ARG_TAG));
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

    @NonNull
    @Override
    public NavController provideNavController() {
        if (navController == null) {
            navController = new EasyNavController();
        }
        return navController;
    }

    @NonNull
    @Override
    public HistoryController provideHistoryController() {
        if (historyController == null) {
            historyController = new EasyHistoryController();
        }
        return historyController;
    }

    @NonNull
    @Override
    public DownloadController provideDownloadController() {
        return new StubDownloadController();
    }

    @NonNull
    @Override
    public BookmarkController provideBookmarkController() {
        return new StubBookmarkController();
    }

    @NonNull
    @Override
    public TabController provideTabController() {
        if (tabController == null) {
            tabController = new EasyTabController(tabCacheManager);
        }
        return tabController;
    }

    class EasyNavController implements IBrowser.NavController {
        @Override
        public void goBack() {
            onBackPressed();
        }

        @Override
        public void goForward() {

        }

        @Override
        public void goHome() {
            tabCacheManager.gotoHome();
        }

        @Override
        public void showTabs() {
            showTabDialog();
        }

        @Override
        public void showSetting() {
            showSettingDialog();
        }
    }

    class EasyHistoryController implements IBrowser.HistoryController {
        @Override
        public void addHistory(final HistoryEntity entity) {
            Disposable dps = Observable.create(new ObservableOnSubscribe<Long>() {

                @Override
                public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                    final EasyApplication application = (EasyApplication) getApplicationContext();
                    DaoSession daoSession = application.getDaoSession();
                    long rowId = daoSession.getHistoryEntityDao().insertOrReplace(entity);
                    emitter.onNext(rowId);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    class EasyTabController implements IBrowser.TabController {

        private TabController next = null;

        public EasyTabController(TabController next) {
            this.next = next;
        }

        @Override
        public void onTabSelected(TabInfo tabInfo) {
            if (next != null) {
                next.onTabSelected(tabInfo);
            }
        }

        @Override
        public void onTabClose(TabInfo tabInfo) {
            if (next != null) {
                next.onTabClose(tabInfo);
            }
        }

        @Override
        public void onAddNewTab(TabInfo tabInfo, boolean backstage) {
            if (next != null) {
                next.onAddNewTab(tabInfo, backstage);
            }
        }
    }

    class StubDownloadController implements IBrowser.DownloadController {
    }

    class StubBookmarkController implements IBrowser.BookmarkController {
    }
}
