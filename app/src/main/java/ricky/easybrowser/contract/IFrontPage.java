package ricky.easybrowser.contract;

import java.util.List;

import ricky.easybrowser.entity.dao.WebSite;

public interface IFrontPage {

    interface View {
        void showWebSite(List<WebSite> webSiteList);
    }

    interface Presenter {
        void getWebSite();
    }
}
