package ricky.easybrowser.entity.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WebSite {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String siteName;
    public String siteUrl;

    public WebSite(Long id, String siteName, String siteUrl) {
        this.id = id;
        this.siteName = siteName;
        this.siteUrl = siteUrl;
    }
}
