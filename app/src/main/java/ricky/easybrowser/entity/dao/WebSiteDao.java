package ricky.easybrowser.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WebSiteDao {
    @Query("SELECT COUNT(*) FROM website")
    long count();

    @Query("SELECT * FROM website")
    List<WebSite> getAll();

    @Insert
    void insertWebSite(WebSite webSite);

    @Insert
    void insertAllWebSite(WebSite... webSites);
}
