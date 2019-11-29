package ricky.easybrowser.entity.dao;

public class DaoManager {
    public static void createDefaultSiteList(AppDatabase db) {
        try {
            WebSite baidu = new WebSite(null, "Baidu", "https://www.baidu.com");
            WebSite bing = new WebSite(null, "Bing", "https://bing.com");
            WebSite qq = new WebSite(null, "QQ", "https://www.qq.com");
            WebSite w163net = new WebSite(null, "网易", "https://www.163.com");
            WebSite juejin = new WebSite(null, "掘金", "https://juejin.im");
            WebSite myDrivers = new WebSite(null, "快科技", "https://mydrivers.com");
            WebSite v2ex = new WebSite(null, "V2ex", "https://v2ex.com");
            WebSite w36kr = new WebSite(null, "36Kr", "https://36kr.com");

            db.webSiteDao().insertAllWebSite(baidu, bing, qq, w163net, juejin, myDrivers, v2ex, w36kr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
