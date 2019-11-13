package ricky.easybrowser.page.browser;

import java.util.List;

import ricky.easybrowser.entity.bo.TabInfo;

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
