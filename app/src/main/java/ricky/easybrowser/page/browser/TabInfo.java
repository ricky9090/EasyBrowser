package ricky.easybrowser.page.browser;

import android.net.Uri;

import androidx.annotation.Nullable;

/**
 * 缓存中对应的页面信息
 */
public class TabInfo {
    private String tag;
    private String title;
    private Uri uri;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (tag == null || obj == null) {
            return false;
        }
        if (obj instanceof TabInfo) {
            TabInfo target = (TabInfo) obj;
            return tag.equals(target.tag);
        } else {
            return false;
        }
    }
}