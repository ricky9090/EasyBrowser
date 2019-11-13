package ricky.easybrowser.page.browser;

import androidx.annotation.NonNull;

import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.entity.dao.History;

/**
 * 抽象的浏览器接口。负责提供导航，历史记录，下载，书签，标签页控制等管理对象
 */
public interface IBrowser {

    @NonNull
    NavController provideNavController();

    @NonNull
    HistoryController provideHistoryController();

    @NonNull
    DownloadController provideDownloadController();

    @NonNull
    BookmarkController provideBookmarkController();

    @NonNull
    TabController provideTabController();


    interface NavController {
        void goBack();

        void goForward();

        void goHome();

        void showTabs();

        void showSetting();

        void showHistory();
    }

    interface HistoryController {
        void addHistory(History entity);
    }

    interface DownloadController {

    }

    interface BookmarkController {

    }

    interface TabController {
        void onTabSelected(TabInfo tabInfo);

        void onTabClose(TabInfo tabInfo);

        void onTabCreate(TabInfo tabInfo, boolean backstage);

        void onTabGoHome();

        void onTabGoForward();
    }
}
