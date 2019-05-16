package ricky.easybrowser.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class HistoryEntity {

    private String title;
    @NotNull
    private String url;

    // TODO 添加时间戳

    @Generated(hash = 1289018983)
    public HistoryEntity(String title, @NotNull String url) {
        this.title = title;
        this.url = url;
    }

    @Generated(hash = 1235354573)
    public HistoryEntity() {
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
