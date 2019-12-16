package ricky.easybrowser.entity.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {History.class, WebSite.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();
    public abstract WebSiteDao webSiteDao();
}
