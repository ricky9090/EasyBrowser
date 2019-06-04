package ricky.easybrowser.page.browser;

import java.util.List;

public interface TabQuickViewContract {

    interface Subject {
        void attach(Observer observer);

        List<TabInfo> provideInfoList();

        void updateTabInfo();
    }

    interface Observer {
        void updateQuickView();
    }
}
