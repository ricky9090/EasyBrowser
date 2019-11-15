package ricky.easybrowser.page.tab;

import ricky.easybrowser.entity.bo.TabInfo;

public interface ITab {
    TabInfo provideTagInfo();

    boolean onBackPressed();

    void goForward();

    void gotoHomePage();
}
