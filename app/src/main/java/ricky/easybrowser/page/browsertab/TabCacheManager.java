package ricky.easybrowser.page.browsertab;

import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.utils.StringUtils;

/**
 * LRU实现的标签页缓存。负责标签页的缓存及切换显示逻辑。
 */
public class TabCacheManager {

    private final FragmentManager fm;
    private int browserLayoutId;

    private LruCache<String, Fragment> lruCache;
    private final List<String> tagList = new ArrayList<>();

    public TabCacheManager(FragmentManager manager, int maxSize, int layoutId) {
        this.fm = manager;
        this.browserLayoutId = layoutId;
        lruCache = new LruCache<String, Fragment>(maxSize) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Fragment oldValue, Fragment newValue) {
                /**
                 * Tab页面被移除或替换后，进行remove操作
                 */
                if (fm == null || StringUtils.isEmpty(key)) {
                    return;
                }

                if (oldValue != null) {
                    fm.beginTransaction().remove(oldValue).commit();
                }
            }
        };
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void put(String tag, Fragment fragment) {
        lruCache.put(tag, fragment);  // throw error when tag is null

        boolean needAdd = true;
        for (int i = 0; i < tagList.size(); i++) {
            if (tag.equals(tagList.get(i))) {
                needAdd = false;
                break;
            }
        }
        if (needAdd) {
            tagList.add(tag);
        }
    }

    public Fragment get(String tag) {
        return lruCache.get(tag);
    }

    public void remove(String tag) {
        lruCache.remove(tag);  // throw error when tag is null

        // 只有用户主动操作，才从recyclerview使用的列表中移除tag
        for (int i = 0; i < tagList.size(); i++) {
            if (tag.equals(tagList.get(i))) {
                tagList.remove(i);
                break;
            }
        }
    }

    /**
     * 用户点击标签页后，切换到目标页面
     *
     * @param tag
     */
    public void switchToTab(String tag) {
        Fragment current = findVisibleFragment(fm);
        Fragment target = get(tag);

        if (current == null) {
            // 当前没有显示任何Fragment，直接show，对应某一页面被关闭的情况
            fm.beginTransaction().show(target).commit();
            return;
        }

        if (target != null) {
            // 点击的是缓存过的页面，替换显示新的Fragment
            fm.beginTransaction().hide(current).show(target).commit();
        } else {
            // 没有缓存页，原页面被回收。重新创建Fragment，复用tag并放至缓存中
            NewTabFragmentV2 fragmentToAdd = NewTabFragmentV2.newInstance();
            String fragmentTag = tag;
            fm.beginTransaction().hide(current).add(browserLayoutId, fragmentToAdd, fragmentTag).commit();
            put(fragmentTag, fragmentToAdd);
        }
    }

    /**
     * 新建标签页
     */
    public void addNewTab() {
        Fragment current = findVisibleFragment(fm);
        NewTabFragmentV2 fragmentToAdd = NewTabFragmentV2.newInstance();
        String fragmentTag = System.currentTimeMillis() + "";
        if (current != null) {
            fm.beginTransaction().hide(current).add(browserLayoutId, fragmentToAdd, fragmentTag).commit();
            put(fragmentTag, fragmentToAdd);
        } else {
            fm.beginTransaction().add(browserLayoutId, fragmentToAdd, fragmentTag).commit();
            put(fragmentTag, fragmentToAdd);
        }
    }

    /**
     * 关闭标签页
     * <ul>
     * <li>如果所有标签页都被关闭，则新建一个标签页</li>
     * <li>如果关闭了第一个标签页，显示新的第一个标签页</li>
     * <li>如果关闭中间位置得标签页，显示前一位置的标签页</li>
     * </ul>
     *
     * @param tag
     */
    public void closeTab(String tag) {
        int orgIndex = findTabIndex(tag);
        remove(tag);

        if (tagList.size() <= 0) {
            addNewTab();
            return;
        }

        if (orgIndex <= 0) {
            switchToTab(tagList.get(0));
        } else {
            switchToTab(tagList.get(orgIndex - 1));
        }
    }

    private int findTabIndex(String tag) {
        int index = -1;
        for (int i = 0; i < tagList.size(); i++) {
            if (tag.equals(tagList.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 在当前的FragmentManager中寻找可见页面
     *
     * @param fm
     * @return
     */
    private Fragment findVisibleFragment(FragmentManager fm) {
        if (fm == null) {
            return null;
        }
        Fragment current = null;
        List<Fragment> fragments = fm.getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            if (!fragments.get(i).isHidden()) {
                current = fragments.get(i);
            }
        }
        return current;
    }
}
