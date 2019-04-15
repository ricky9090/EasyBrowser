package ricky.easybrowser.page.browsertab;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;

public class BrowserTabAdapter extends FragmentStatePagerAdapter {

    private final List<String> fragmentList = new ArrayList<>();

    public BrowserTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO 分析从列表中获取Fragment引起内存泄漏原因
        if (position >= fragmentList.size()) {
            return null;
        }
        return NewTabFragmentV2.newInstance();
    }

    @Override
    public int getCount() {
        return fragmentList.size();
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
}
