package ricky.easybrowser.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class FragmentBackHandleHelper {

    public static boolean isFragmentBackHandled(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return false;
        }

        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (isFragmentBackable(fragment)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isFragmentBackable(Fragment fragment) {
        return fragment.isVisible()
                && fragment.getUserVisibleHint()
                && fragment instanceof OnBackInteractionListener
                && ((OnBackInteractionListener) fragment).onBackPressed();

    }
}
