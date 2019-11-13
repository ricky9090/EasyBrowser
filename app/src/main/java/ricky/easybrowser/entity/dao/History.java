package ricky.easybrowser.entity.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class History {

    @Id(autoincrement = true)
    private Long id;

    private String title;
    @NotNull
    private String url;

    @Generated(hash = 1554579216)
    public History(Long id, String title, @NotNull String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    @Generated(hash = 869423138)
    public History() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    // TODO 添加时间戳
}
