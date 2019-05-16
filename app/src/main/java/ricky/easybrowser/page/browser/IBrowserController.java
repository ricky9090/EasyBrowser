package ricky.easybrowser.page.browser;

import ricky.easybrowser.entity.HistoryEntity;

public interface IBrowserController {
    void goBack();

    void goForward();

    void goHome();

    void showTabs();

    void showSetting();

    void addHistory(HistoryEntity entity);
}
