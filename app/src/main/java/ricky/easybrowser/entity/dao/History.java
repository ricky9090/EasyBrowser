package ricky.easybrowser.entity.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String title;
    public String url;
    public long time;
}
