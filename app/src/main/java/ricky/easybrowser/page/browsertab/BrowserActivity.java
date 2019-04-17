package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.page.webpage.WebPageFragment;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener,
        WebPageFragment.OnWebInteractionListener {

    FrameLayout webContentFrame;
    ImageView showTabsButton;

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
            public void onTabClick(String tag) {
                tabCacheManager.switchToTab(tag);
            }

            @Override
            public void onTabClose(String tag) {
                tabCacheManager.closeTab(tag);
            }

            @Override
            public void onAddTab() {
                tabCacheManager.addNewTab();
                tabQuickViewAdapter.notifyDataSetChanged();
            }
        });
        tabRecyclerView.setAdapter(tabQuickViewAdapter);

        showTabsButton = findViewById(R.id.show_all_tabs);
        showTabsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabRecyclerView.getVisibility() == View.VISIBLE) {
                    tabRecyclerView.setVisibility(View.GONE);
                } else {
                    tabRecyclerView.setVisibility(View.VISIBLE);
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
    public void onWebInteraction(WebView webview) {

    }

    @Override
    public void onBackPressed() {
        if (FragmentBackHandleHelper.isFragmentBackHandled(getSupportFragmentManager())) {
            return;
        }
        // FIXME
    }


}
