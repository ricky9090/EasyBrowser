package ricky.easybrowser.page.browser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.R;
import ricky.easybrowser.common.Const;
import ricky.easybrowser.common.TabConst;
import ricky.easybrowser.entity.bo.ClickInfo;
import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.entity.dao.DaoSession;
import ricky.easybrowser.entity.dao.History;
import ricky.easybrowser.page.history.HistoryActivity;
import ricky.easybrowser.page.setting.SettingDialogKt;
import ricky.easybrowser.page.tab.ITab;
import ricky.easybrowser.page.tabpreview.TabDialogKt;
import ricky.easybrowser.page.tabpreview.TabQuickViewContract;
import ricky.easybrowser.utils.FragmentBackHandleHelper;
import ricky.easybrowser.utils.TabHelper;
import ricky.easybrowser.web.IWebView;

public class BrowserActivity extends AppCompatActivity implements IWebView.OnWebInteractListener,
        IBrowser {

    private static final String TAG = "BrowserActivity";

    private static final String SETTING_DIALOG_TAG = "setting_dialog";
    private static final String TAB_DIALOG_TAG = "tab_dialog";

    FrameLayout webContentFrame;

    TabDialogKt tabDialog;
    SettingDialogKt settingDialog;

    IBrowser.NavController navController;
    IBrowser.HistoryController historyController;
    IBrowser.TabController tabController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        provideTabController();

        webContentFrame = findViewById(R.id.web_content_frame);

        if (savedInstanceState == null) {
            // 默认添加一个新标签页
            TabInfo tabInfo = TabInfo.create(
                    System.currentTimeMillis() + "",
                    getString(R.string.new_tab_welcome));
            provideTabController().onTabCreate(tabInfo, false);
        } else {
            Fragment prevDialog = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
            if (prevDialog instanceof TabDialogKt) {
                ((TabDialogKt) prevDialog).setTabViewSubject(provideTabController());
                ((TabDialogKt) prevDialog).dismiss();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        ArrayList<TabInfo> restoreList = savedInstanceState.getParcelableArrayList("tablist");
        if (restoreList == null) {
            return;
        }

        // 当横竖屏切换后，将复原的Fragment重新推入cache
        // 由于tablist可能超出cache的大小(即Activity销毁前Fragment数量)，这里首先还原tablist信息
        provideTabController().provideInfoList().addAll(restoreList);
        List<Fragment> restoredFragmentList = getSupportFragmentManager().getFragments();
        if (restoredFragmentList.size() > 0) {
            for (Fragment target : restoredFragmentList) {
                if (target instanceof ITab && target.getArguments() != null) {
                    // 根据Fragment参数，还原TabInfo信息用于列表中查找
                    TabInfo info = TabInfo.create(
                            target.getArguments().getString(TabConst.ARG_TAG),
                            target.getArguments().getString(TabConst.ARG_TITLE));
                    provideTabController().onRestoreTabCache(info, target);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TabInfo> storeList = new ArrayList<>();
        storeList.addAll(provideTabController().provideInfoList());
        outState.putParcelableArrayList("tablist", storeList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provideTabController().onCloseAllTabs();
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
    public void onPageTitleChange(TabInfo tabInfo) {
        provideTabController().updateTabInfo(tabInfo);
    }

    @Override
    public void onLongClick(ClickInfo clickInfo) {
        if (clickInfo == null) {
            return;
        }

        switch (clickInfo.type) {
            case WebView.HitTestResult.IMAGE_TYPE:
                showImageActionDialog(clickInfo);
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                showUrlActionDialog(clickInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (FragmentBackHandleHelper.isFragmentBackHandled(getSupportFragmentManager())) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.RequestCode.SHOW_HISTORY) {
            showHistoryResult(resultCode, data);
        }
    }

    private void showHistoryResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        TabInfo info = data.getParcelableExtra(Const.Key.TAB_INFO);
        if (info == null) {
            return;
        }

        provideTabController().onTabCreate(info, false);
    }

    private void showTabDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
        if (prev != null) {
            ((TabDialogKt) prev).dismiss();
            return;
        }
        if (tabDialog == null) {
            tabDialog = new TabDialogKt();
            tabDialog.setCancelable(false);
        }
        tabDialog.setTabViewSubject(provideTabController());
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

    private void showImageActionDialog(@NonNull final ClickInfo clickInfo) {
        AlertDialog.Builder imageDialogbuilder = new AlertDialog.Builder(this);
        imageDialogbuilder.setItems(R.array.image_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            false);
                }
            }
        });
        imageDialogbuilder.show();
    }

    private void showUrlActionDialog(@NonNull final ClickInfo clickInfo) {
        AlertDialog.Builder urlDialogbuilder = new AlertDialog.Builder(this);
        urlDialogbuilder.setItems(R.array.url_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            false);
                }
            }
        });
        urlDialogbuilder.show();
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
            IBrowser.TabController next = new TabCacheManager(this, getSupportFragmentManager(), 3, R.id.web_content_frame);
            tabController = new EasyTabController(next);
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
            provideTabController().onTabGoForward();
        }

        @Override
        public void goHome() {
            provideTabController().onTabGoHome();
        }

        @Override
        public void showTabs() {
            showTabDialog();
        }

        @Override
        public void showSetting() {
            showSettingDialog();
        }

        @Override
        public void showHistory() {
            Intent intent = new Intent();
            intent.setClass(BrowserActivity.this, HistoryActivity.class);
            startActivityForResult(intent, Const.RequestCode.SHOW_HISTORY);
        }
    }

    class EasyHistoryController implements IBrowser.HistoryController {
        @Override
        public void addHistory(final History entity) {
            Disposable dps = Observable.create(new ObservableOnSubscribe<Long>() {

                @Override
                public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                    final EasyApplication application = (EasyApplication) getApplicationContext();
                    DaoSession daoSession = application.getDaoSession();
                    long rowId = daoSession.getHistoryDao().insertOrReplace(entity);
                    Log.i(TAG, "inserted id    is : " + rowId);
                    Log.i(TAG, "inserted title is : " + entity.getTitle());
                    Log.i(TAG, "inserted url   is : " + entity.getUrl());
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
        public void onTabCreate(TabInfo tabInfo, boolean backstage) {
            if (next != null) {
                next.onTabCreate(tabInfo, backstage);
            }
        }

        @Override
        public void onTabGoHome() {
            if (next != null) {
                next.onTabGoHome();
            }
        }

        @Override
        public void onTabGoForward() {
            if (next != null) {
                next.onTabGoForward();
            }
        }

        @Override
        public void onRestoreTabCache(TabInfo infoCopy, @Nullable Fragment fragment) {
            if (next != null) {
                next.onRestoreTabCache(infoCopy, fragment);
            }
        }

        @Override
        public void onCloseAllTabs() {
            if (next != null) {
                next.onCloseAllTabs();
            }
        }

        @Override
        public void attach(TabQuickViewContract.Observer observer) {
            if (next != null) {
                next.attach(observer);
            }
        }

        @Override
        public List<TabInfo> provideInfoList() {
            if (next != null) {
                return next.provideInfoList();
            }
            return null;
        }

        @Override
        public void updateTabInfo(TabInfo info) {
            if (next != null) {
                next.updateTabInfo(info);
            }
        }
    }

    private class StubDownloadController implements IBrowser.DownloadController {
    }

    private class StubBookmarkController implements IBrowser.BookmarkController {
    }
}
