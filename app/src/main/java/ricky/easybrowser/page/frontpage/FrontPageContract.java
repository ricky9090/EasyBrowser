package ricky.easybrowser.page.frontpage;

import java.util.List;

import ricky.easybrowser.entity.dao.WebSite;

public interface FrontPageContract {

    interface View {
        void showWebSite(List<WebSite> webSiteList);
    }

    interface Presenter {
        void getWebSite();
    }
}
