package ricky.easybrowser.page.browser;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener {

    FrameLayout webContentFrame;
    BrowserNavBar navBar;
    RecyclerView tabRecyclerView;
    TabQuickViewAdapter tabQuickViewAdapter;

    TabCacheManager tabCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        if (tabCacheManager == null) {
            tabCacheManager = new TabCacheManager(getSupportFragmentManager(), 3, R.id.web_content_frame);
        }

        webContentFrame = findViewById(R.id.web_content_frame);
        tabRecyclerView = findViewById(R.id.tab_list_recyclerview);
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        tabQuickViewAdapter = new TabQuickViewAdapter(this);
        tabQuickViewAdapter.attachToBrwoserTabs(tabCacheManager);
        tabQuickViewAdapter.setListener(new TabQuickViewAdapter.OnTabClickListener() {
            @Override
            public void onTabClick(TabCacheManager.TabInfo info) {
                tabCacheManager.switchToTab(info);
            }

            @Override
            public void onTabClose(TabCacheManager.TabInfo info) {
                tabCacheManager.closeTab(info);
            }

            @Override
            public void onAddTab() {
                tabCacheManager.addNewTab(getString(R.string.new_tab_welcome));
                tabQuickViewAdapter.notifyDataSetChanged();
            }
        });
        tabRecyclerView.setAdapter(tabQuickViewAdapter);

        navBar = findViewById(R.id.nav_bar);
        navBar.setNavListener(new BrowserNavBar.OnNavClickListener() {
            @Override
            public void onItemClick(View itemView) {
                int id = itemView.getId();
                if (id == R.id.nav_show_tabs) {
                    if (tabRecyclerView.getVisibility() == View.VISIBLE) {
                        tabRecyclerView.setVisibility(View.GONE);
                    } else {
                        tabRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else if (id == R.id.nav_back) {
                    onBackPressed();
                } else if (id == R.id.nav_home) {
                    tabCacheManager.gotoHome();
                } else if (id == R.id.nav_setting) {
                    // TODO implement setting dialog
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ((TestDialog) prev).dismiss();
                        return;
                    }

                    TestDialog dialog = new TestDialog();
                    dialog.show(ft, "dialog");
                }
            }
        });


        if (savedInstanceState == null) {
            // 默认添加一个新标签页
            tabCacheManager.addNewTab(getString(R.string.new_tab_welcome));
        } else {
            // 当横竖屏切换后，将复原的Fragment重新推入cache
            Fragment restoredFragment = getSupportFragmentManager().findFragmentById(R.id.web_content_frame);
            if (restoredFragment != null && restoredFragment.getArguments() != null) {
                TabCacheManager.TabInfo info = new TabCacheManager.TabInfo();
                info.setTitle(restoredFragment.getArguments().getString(NewTabFragmentV2.ARG_TITLE));
                info.setTag(restoredFragment.getArguments().getString(NewTabFragmentV2.ARG_TAG));
                tabCacheManager.put(info, restoredFragment);
            }
        }
        tabQuickViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabCacheManager.clearCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTabTitleChanged(String title) {
        tabCacheManager.updateTabTitle();
    }

    @Override
    public void onBackPressed() {
        if (FragmentBackHandleHelper.isFragmentBackHandled(getSupportFragmentManager())) {
            return;
        }

        if (tabRecyclerView.getVisibility() == View.VISIBLE) {
            tabRecyclerView.setVisibility(View.GONE);
            return;
        }


        super.onBackPressed();
    }


    public static class TestDialog extends DialogFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.layout_setting_dialog, container, false);
            return dialogView;
        }

        @Override
        public void onResume() {
            super.onResume();
            WindowManager.LayoutParams param = getDialog().getWindow().getAttributes();
            param.gravity = Gravity.BOTTOM;
            getDialog().getWindow().setAttributes(param);
        }
    }

}
