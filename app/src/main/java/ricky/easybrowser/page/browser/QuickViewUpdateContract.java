package ricky.easybrowser.page.browser;

import java.util.List;

public interface QuickViewUpdateContract {

    interface Subject {
        void attach(Observer observer);
        List<TabCacheManager.TabInfo> provideInfoList();
    }

    interface Observer {
        void updateQuickView();
    }
}
