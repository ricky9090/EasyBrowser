package ricky.easybrowser.page.browser;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ricky.easybrowser.R;
import ricky.easybrowser.page.browser.newtab.NewTabFragmentV2;
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

        tabCacheManager = new TabCacheManager(getSupportFragmentManager(), 3, R.id.web_content_frame);

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
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onTabtInteraction(Uri uri) {

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
        // FIXME
    }


}
