package ricky.easybrowser;

import android.app.Application;


import org.greenrobot.greendao.database.Database;

import ricky.easybrowser.entity.dao.DaoMaster;
import ricky.easybrowser.entity.dao.DaoSession;
import ricky.easybrowser.entity.dao.WebSite;

public class EasyApplication extends Application {

    DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initDB();
    }

    private void initDB() {
        // FIXME replace DevOpenHelper
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "browser-db", null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        long defCount = daoSession.getWebSiteDao().count();
        if (defCount != 0) {
            return;
        }
        try {
            WebSite baidu = new WebSite(null, "Baidu", "www.baidu.com");
            WebSite bing = new WebSite(null, "Bing", "bing.com");
            WebSite qq = new WebSite(null, "QQ", "www.qq.com");
            WebSite netEasy = new WebSite(null, "网易", "www.163.com");
            WebSite myDrivers = new WebSite(null, "快科技", "mydrivers.com");
            WebSite v2ex = new WebSite(null, "V2ex", "v2ex.com");
            WebSite juejin = new WebSite(null, "掘金", "juejin.im");
            WebSite w36kr = new WebSite(null, "36Kr", "36kr.com");
            daoSession.getWebSiteDao().insert(baidu);
            daoSession.getWebSiteDao().insert(bing);
            daoSession.getWebSiteDao().insert(qq);
            daoSession.getWebSiteDao().insert(netEasy);
            daoSession.getWebSiteDao().insert(myDrivers);
            daoSession.getWebSiteDao().insert(v2ex);
            daoSession.getWebSiteDao().insert(juejin);
            daoSession.getWebSiteDao().insert(w36kr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
