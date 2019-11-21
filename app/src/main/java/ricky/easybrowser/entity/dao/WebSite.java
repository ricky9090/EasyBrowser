package ricky.easybrowser.entity.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WebSite {

    @Id(autoincrement = true)
    private Long id;
    private String siteName;
    @NotNull
    private String siteUrl;

    private String favicoUrl;

    @Generated(hash = 283602442)
    public WebSite(Long id, String siteName, @NotNull String siteUrl,
            String favicoUrl) {
        this.id = id;
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.favicoUrl = favicoUrl;
    }

    @Generated(hash = 121794805)
    public WebSite() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getFavicoUrl() {
        return favicoUrl;
    }

    public void setFavicoUrl(String favicoUrl) {
        this.favicoUrl = favicoUrl;
    }
}
