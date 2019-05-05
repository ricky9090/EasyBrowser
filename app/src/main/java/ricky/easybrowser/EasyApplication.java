package ricky.easybrowser;

import android.app.Application;


import org.greenrobot.greendao.database.Database;
import org.mozilla.geckoview.GeckoRuntime;

import ricky.easybrowser.entity.DaoMaster;
import ricky.easybrowser.entity.DaoSession;
import ricky.easybrowser.entity.SiteEntity;

public class EasyApplication extends Application {

    DaoSession daoSession;
    GeckoRuntime geckoRuntime;

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

        long defCount = daoSession.getSiteEntityDao().count();
        if (defCount != 0) {
            return;
        }
        try {
            SiteEntity baidu = new SiteEntity(null, "Baidu", "www.baidu.com");
            SiteEntity bing = new SiteEntity(null, "Bing", "bing.com");
            SiteEntity qq = new SiteEntity(null, "QQ", "www.qq.com");
            SiteEntity netEasy = new SiteEntity(null, "网易", "www.163.com");
            SiteEntity myDrivers = new SiteEntity(null, "快科技", "mydrivers.com");
            SiteEntity v2ex = new SiteEntity(null, "V2ex", "v2ex.com");
            SiteEntity juejin = new SiteEntity(null, "掘金", "juejin.im");
            SiteEntity w36kr = new SiteEntity(null, "36Kr", "36kr.com");
            daoSession.getSiteEntityDao().insert(baidu);
            daoSession.getSiteEntityDao().insert(bing);
            daoSession.getSiteEntityDao().insert(qq);
            daoSession.getSiteEntityDao().insert(netEasy);
            daoSession.getSiteEntityDao().insert(myDrivers);
            daoSession.getSiteEntityDao().insert(v2ex);
            daoSession.getSiteEntityDao().insert(juejin);
            daoSession.getSiteEntityDao().insert(w36kr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGeckoRuntime() {
        geckoRuntime = GeckoRuntime.create(getApplicationContext());
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public GeckoRuntime getGeckoRuntime() {
        if (geckoRuntime == null) {
            initGeckoRuntime();
        }
        return geckoRuntime;
    }
}
