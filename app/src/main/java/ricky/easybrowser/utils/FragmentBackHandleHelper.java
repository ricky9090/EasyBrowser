package ricky.easybrowser.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import ricky.easybrowser.contract.ITab;

public class FragmentBackHandleHelper {

    public static boolean isFragmentBackHandled(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (CollectionUtils.isEmpty(fragments)) {
            return false;
        }

        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (isFragmentBackable(fragment)) {
                return true;
            }
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                return true;
            }
        }

        return false;
    }

    public static boolean isFragmentBackable(Fragment fragment) {
        return fragment.isVisible()
                && fragment.getUserVisibleHint()
                && fragment instanceof ITab
                && ((ITab) fragment).onBackPressed();

    }
}
