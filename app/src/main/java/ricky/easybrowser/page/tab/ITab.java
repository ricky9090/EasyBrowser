package ricky.easybrowser.page.tab;

import ricky.easybrowser.entity.bo.TabInfo;

public interface ITab {
    TabInfo provideTabInfo();

    boolean onBackPressed();

    void goForward();

    void gotoHomePage();
}
