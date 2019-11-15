package ricky.easybrowser.page.tabpreview;

import java.util.List;

import ricky.easybrowser.entity.bo.TabInfo;

public interface TabQuickViewContract {

    interface Subject {
        void attach(Observer observer);

        List<TabInfo> provideInfoList();

        void updateTabInfo(TabInfo info);
    }

    interface Observer {
        void updateQuickView();
    }
}
