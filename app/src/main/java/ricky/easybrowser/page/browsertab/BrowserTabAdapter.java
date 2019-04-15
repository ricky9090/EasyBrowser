package ricky.easybrowser.page.browsertab;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.utils.EasyLog;

public class BrowserTabAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TabAdapter";

    private final List<String> fragmentList = new ArrayList<>();

    private int firstRemoedItem;

    public BrowserTabAdapter(FragmentManager fm) {
        super(fm);
        firstRemoedItem = -1;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO 分析从列表中获取Fragment引起内存泄漏原因
        if (position >= fragmentList.size()) {
            return null;
        }
        NewTabFragmentV2 fragmentV2 = NewTabFragmentV2.newInstance(fragmentList.get(position), null);
        fragmentV2.setIndexInViewPager(position);
        return fragmentV2;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * 当有Tab页面移除后，需要根据页面原Index值，判断位置是否发生改变，触发刷新
     *
     * @see <a href="https://stackoverflow.com/questions/10849552/update-viewpager-dynamically">
     *     update-viewpager-dynamically</a>
     * @see <a href="https://www.jianshu.com/p/266861496508">ViewPager刷新问题详解</a>
     */
    @Override
    public int getItemPosition(@NonNull Object object) {

        EasyLog.d(TAG, "getItemPosition called");
        if (firstRemoedItem != -1 && object instanceof NewTabFragmentV2) {
            NewTabFragmentV2 tab = (NewTabFragmentV2) object;
            EasyLog.d(TAG, "getItemPosition: " + tab.getIndexInViewPager());
            if (tab.getIndexInViewPager() >= firstRemoedItem) {
                tab.setIndexInViewPager(firstRemoedItem - 1);
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }
        return POSITION_UNCHANGED;
    }

    public List<String> getTabList() {
        return fragmentList;
    }

    public void addTab(String url) {
        fragmentList.add(url);
    }

    public void clearTabs() {
        fragmentList.clear();
    }

    public void removeTabAt(int position) {
        if (fragmentList.size() <= position) {
            return;
        }
        fragmentList.remove(position);
        firstRemoedItem = position;

    }
}
