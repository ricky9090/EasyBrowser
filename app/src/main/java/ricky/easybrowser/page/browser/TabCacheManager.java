package ricky.easybrowser.page.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.contract.IBrowser;
import ricky.easybrowser.contract.ITab;
import ricky.easybrowser.contract.ITabQuickView;
import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.page.tab.NewTabFragmentV2;

/**
 * LRU实现的标签页缓存。负责标签页的缓存及切换显示逻辑。
 */
public class TabCacheManager implements IBrowser.ITabController {

    private final Context mContext;
    private final FragmentManager fm;
    private int browserLayoutId;

    private ITabQuickView.Observer observer;

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

    /**
     * 还原Tab页缓存，使用从Fragment中还原的参数生成TabInfo对象
     * <p>
     * 此方法仅还原一个标签页，上层可能需要在循环中调用
     *
     * @param infoCopy 由Fragment中的参数还原的TabInfo对象，与复原列表里的hash值不同，put时需判断
     * @param fragment 目标Fragment
     */
    private void restoreTabCache(TabInfo infoCopy, @Nullable Fragment fragment) {
        int prevIndex = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (infoCopy.equals(infoList.get(i))) {
                prevIndex = i;
                break;
            }
        }
        if (prevIndex >= 0) {
            // 之前有缓存，直接put进cache，不更新列表，需要从infoList中拿到真正的TabInfo
            TabInfo info = infoList.get(prevIndex);
            lruCache.put(info, fragment);
        } else {
            // 缓存列表里不存在此项
            infoList.add(infoCopy);
            lruCache.put(infoCopy, fragment);
        }
    }

    private void addToCache(TabInfo info, Fragment fragment) {
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

    private Fragment getFromCache(TabInfo info) {
        return lruCache.get(info);
    }

    private void removeFromCache(TabInfo info) {
        lruCache.remove(info);  // throw error when tag is null

        // 只有用户主动操作，才从recyclerview使用的列表中移除tag
        for (int i = 0; i < infoList.size(); i++) {
            if (info.equals(infoList.get(i))) {
                infoList.remove(i);
                break;
            }
        }
    }

    private void closeAllTabs() {
        lruCache.evictAll();
        infoList.clear();
    }

    /**
     * 用户点击标签页后，切换到目标页面
     *
     * @param info
     */
    private void switchToTab(TabInfo info) {
        Fragment current = findVisibleFragment(fm);
        Fragment target = getFromCache(info);

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
            addToCache(fragmentInfo, fragmentToAdd);
        }
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
        addToCache(info, fragmentToAdd);
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
    private void closeTab(TabInfo info) {
        int orgIndex = findTabIndex(info);
        removeFromCache(info);
        if (observer != null) {
            observer.updateQuickView();
        }

        if (infoList.size() <= 0 && observer != null) {
            TabInfo tabInfo = TabInfo.create(
                    System.currentTimeMillis() + "",
                    mContext.getString(R.string.new_tab_welcome));
            onTabCreate(tabInfo, false);
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
            if ((!tmp.isHidden()) && (tmp instanceof ITab)) {
                current = fragments.get(i);
            }
        }
        return current;
    }

    @Override
    public void attach(ITabQuickView.Observer observer) {
        this.observer = observer;
    }

    @Override
    public void detach() {
        this.observer = null;
    }

    @Override
    public List<TabInfo> provideInfoList() {
        return this.infoList;
    }

    @Override
    public void updateTabInfo(TabInfo tabInfo) {
        try {
            int i = findTabIndex(tabInfo);
            if (i < 0) {
                return;
            }
            String nTitle = tabInfo.getTitle();

            infoList.get(i).setTitle(nTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (observer != null) {
            observer.updateQuickView();
        }
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
    public void onTabCreate(TabInfo tabInfo, boolean backstage) {
        addNewTab(tabInfo, backstage);
    }

    @Override
    public void onTabGoHome() {
        Fragment target = findVisibleFragment(fm);
        if (target == null) {
            return;
        }

        if (target instanceof ITab) {
            ((ITab) target).gotoHomePage();
        }
    }

    @Override
    public void onTabGoForward() {
        Fragment target = findVisibleFragment(fm);
        if (target == null) {
            return;
        }

        if (target instanceof ITab) {
            ((ITab) target).goForward();
        }
    }

    @Override
    public void onTabLoadUrl(String url) {
        Fragment target = findVisibleFragment(fm);
        if (target == null) {
            return;
        }

        if (target instanceof ITab) {
            ((ITab) target).loadUrl(url);
        }
    }

    @Override
    public void onRestoreTabCache(TabInfo infoCopy, @Nullable Fragment fragment) {
        restoreTabCache(infoCopy, fragment);
    }

    @Override
    public void onCloseAllTabs() {
        closeAllTabs();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public TabInfo getCurrentTab() {
        if (this.fm == null) {
            return null;
        }

        Fragment fragment = findVisibleFragment(this.fm);
        if (fragment instanceof ITab) {
            TabInfo info = ((ITab) fragment).provideTabInfo();
            return info;
        }
        return null;
    }

    @Override
    public Bitmap getPreviewForTab(TabInfo tabInfo) {
        Fragment fragment = getFromCache(tabInfo);
        if (fragment instanceof ITab) {
            return ((ITab) fragment).getTabPreview();
        }
        return null;
    }
}
