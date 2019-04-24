package ricky.easybrowser.page.browser;

import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;

/**
 * LRU实现的标签页缓存。负责标签页的缓存及切换显示逻辑。
 */
public class TabCacheManager implements QuickViewUpdateContract.Subject {

    private final FragmentManager fm;
    private int browserLayoutId;

    private QuickViewUpdateContract.Observer observer;

    private LruCache<TabInfo, Fragment> lruCache;
    private final List<TabInfo> infoList = new ArrayList<>();

    public TabCacheManager(FragmentManager manager, int maxSize, int layoutId) {
        this.fm = manager;
        this.browserLayoutId = layoutId;
        lruCache = new LruCache<TabInfo, Fragment>(maxSize) {
            @Override
            protected void entryRemoved(boolean evicted, TabInfo key, Fragment oldValue, Fragment newValue) {
                /**
                 * Tab页面被移除或替换后，进行remove操作
                 */
                if (fm == null || key == null) {
                    return;
                }

                if (oldValue != null) {
                    fm.beginTransaction().remove(oldValue).commit();
                }
            }
        };
    }

    public List<TabInfo> getInfoList() {
        return infoList;
    }

    public void put(TabInfo info, Fragment fragment) {
        lruCache.put(info, fragment);  // throw error when tag is null

        boolean needAdd = true;
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                needAdd = false;
                break;
            }
        }
        if (needAdd) {
            infoList.add(info);
        }
    }

    public Fragment get(TabInfo info) {
        return lruCache.get(info);
    }

    public void remove(TabInfo info) {
        lruCache.remove(info);  // throw error when tag is null

        // 只有用户主动操作，才从recyclerview使用的列表中移除tag
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                infoList.remove(i);
                break;
            }
        }
    }

    /**
     * 用户点击标签页后，切换到目标页面
     *
     * @param info
     */
    public void switchToTab(TabInfo info) {
        Fragment current = findVisibleFragment(fm);
        Fragment target = get(info);

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
            TabInfo fragmentInfo = info;
            fm.beginTransaction().hide(current).add(browserLayoutId, fragmentToAdd, info.tag).commit();
            put(fragmentInfo, fragmentToAdd);
        }
    }

    /**
     * 新建标签页
     */
    public void addNewTab() {
        addNewTab("");
    }

    /**
     * 新建标签页
     * @param title 标题
     */
    public void addNewTab(String title) {
        if (fm == null) {
            return;
        }
        Fragment current = findVisibleFragment(fm);
        String fragmentTag = System.currentTimeMillis() + "";
        NewTabFragmentV2 fragmentToAdd = NewTabFragmentV2.newInstance(title, fragmentTag);
        TabInfo info = new TabInfo();
        info.tag = fragmentTag;
        info.title = title;
        if (current != null) {
            fm.beginTransaction().hide(current).add(browserLayoutId, fragmentToAdd).commit();
            put(info, fragmentToAdd);
        } else {
            fm.beginTransaction().add(browserLayoutId, fragmentToAdd).commit();
            put(info, fragmentToAdd);
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
     * @param info
     */
    public void closeTab(TabInfo info) {
        int orgIndex = findTabIndex(info);
        remove(info);

        if (infoList.size() <= 0 && observer != null) {
            observer.addNewTab();
            return;
        }

        if (orgIndex <= 0) {
            switchToTab(infoList.get(0));
        } else {
            switchToTab(infoList.get(orgIndex - 1));
        }
    }

    private int findTabIndex(TabInfo info) {
        int index = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int findTabByTag(String tag) {
        int index = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (tag.equals(infoList.get(i).tag)) {
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

    public void updateTabTitle() {
        Fragment target = findVisibleFragment(fm);
        if (!(target instanceof NewTabFragmentV2)) {
            return;
        }
        String nTag = target.getArguments().getString(NewTabFragmentV2.ARG_TAG);
        String nTitle = target.getArguments().getString(NewTabFragmentV2.ARG_TITLE);
        int i = findTabByTag(nTag);
        try {
            infoList.get(i).title = nTitle;
        } catch (Exception e) {

        }

        if (observer != null) {
            observer.updateQuickView();
        }
    }

    public void gotoHome() {
        Fragment target = findVisibleFragment(fm);
        if (target == null) {
            return;
        }

        if (target instanceof NewTabFragmentV2) {
            ((NewTabFragmentV2) target).gotoHomePage();
        }
    }

    @Override
    public void attach(QuickViewUpdateContract.Observer observer) {
        this.observer = observer;
    }

    /**
     * 缓存中对应的页面信息
     */
    public static class TabInfo {
        private String tag;
        private String title;

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
}
