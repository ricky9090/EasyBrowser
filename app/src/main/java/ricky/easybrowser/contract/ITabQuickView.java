package ricky.easybrowser.contract;

import java.util.List;

import ricky.easybrowser.entity.bo.TabInfo;

public interface ITabQuickView {

    interface Subject {
        void attach(Observer observer);

        void detach();

        List<TabInfo> provideInfoList();

        void updateTabInfo(TabInfo info);
    }

    interface Observer {
        void updateQuickView();
    }
}
