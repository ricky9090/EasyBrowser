package ricky.easybrowser.page.browser;

import android.content.Context;
import android.util.LruCache;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;

/**
 * LRU实现的标签页缓存。负责标签页的缓存及切换显示逻辑。
 */
public class TabCacheManager implements QuickViewUpdateContract.Subject,
        IBrowser.TabController {

    private final Context mContext;
    private final FragmentManager fm;
    private int browserLayoutId;

    private QuickViewUpdateContract.Observer observer;

    private LruCache<TabInfo, Fragment> lruCache;
    private final List<TabInfo> infoList = new ArrayList<>();

    public TabCacheManager(Context context, FragmentManager manager, int maxSize, int layoutId) {
        this.mContext = context;
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
                    fm.beginTransaction().remove(oldValue).commitAllowingStateLoss();
                }
            }
        };
    }

    public void restoreTabCache(TabInfo info, Fragment fragment) {
        int prevIndex = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                prevIndex = i;
                break;
            }
        }
        if (prevIndex >= 0) {
            // 之前有缓存，直接put进cache，不更新列表
            lruCache.put(info, fragment);
        } else {
            infoList.add(info);
            lruCache.put(info, fragment);
        }
    }

    private void put(TabInfo info, Fragment fragment) {
        int prevIndex = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                prevIndex = i;
                break;
            }
        }
        if (prevIndex >= 0) {
            // 之前有缓存，直接put进cache，不更新列表
            lruCache.put(info, fragment);
        } else {
            infoList.add(info);
            lruCache.put(info, fragment);
        }
    }

    private Fragment get(TabInfo info) {
        return lruCache.get(info);
    }

    private void remove(TabInfo info) {
        lruCache.remove(info);  // throw error when tag is null

        // 只有用户主动操作，才从recyclerview使用的列表中移除tag
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                infoList.remove(i);
                break;
            }
        }
    }

    public void closeAllTabs() {
        lruCache.evictAll();
        infoList.clear();
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
            fm.beginTransaction().hide(current).add(browserLayoutId, fragmentToAdd, info.getTag()).commit();
            put(fragmentInfo, fragmentToAdd);
        }
    }

    /**
     * 新建标签页
     */
    private void addNewTab(String title) {
        TabInfo tabInfo = new TabInfo();
        tabInfo.setTitle(title);
        tabInfo.setTag(System.currentTimeMillis() + "");

        onAddNewTab(tabInfo, false);
    }

    private void addNewTab(TabInfo info, boolean backstage) {
        if (fm == null || info == null) {
            return;
        }
        Fragment current = findVisibleFragment(fm);
        NewTabFragmentV2 fragmentToAdd = NewTabFragmentV2.newInstance(info.getTitle(), info.getTag(), info.getUri());
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(browserLayoutId, fragmentToAdd);
        if (current != null && !backstage) {
            transaction.hide(current);
        } else if (current != null) {
            transaction.hide(fragmentToAdd);
        }
        transaction.commit();
        put(info, fragmentToAdd);
        if (observer != null) {
            observer.updateQuickView();
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
        if (observer != null) {
            observer.updateQuickView();
        }

        if (infoList.size() <= 0 && observer != null) {
            addNewTab(mContext.getString(R.string.new_tab_welcome));
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
            if (tag.equals(infoList.get(i).getTag())) {
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
            final Fragment tmp = fragments.get(i);
            if ((!tmp.isHidden()) && (tmp instanceof NewTabFragmentV2)) {
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
            infoList.get(i).setTitle(nTitle);
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

    @Override
    public List<TabInfo> provideInfoList() {
        return this.infoList;
    }

    @Override
    public void onTabSelected(TabInfo tabInfo) {
        switchToTab(tabInfo);
    }

    @Override
    public void onTabClose(TabInfo tabInfo) {
        closeTab(tabInfo);
    }

    @Override
    public void onAddNewTab(TabInfo tabInfo, boolean backstage) {
        addNewTab(tabInfo, backstage);
    }
}
