package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

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
    Button addTabButton;
    Button showTabsButton;

    RecyclerView tabRecyclerView;
    TabQuickViewAdapter tabQuickViewAdapter;

    BrowserTabLruCache fragmentLruCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        fragmentLruCache = new BrowserTabLruCache(getSupportFragmentManager(), 3, R.id.web_content_frame);

        webContentFrame = findViewById(R.id.web_content_frame);

        tabRecyclerView = findViewById(R.id.tab_list_recyclerview);
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        tabQuickViewAdapter = new TabQuickViewAdapter(this);
        tabQuickViewAdapter.attachToBrwoserTabs(fragmentLruCache);
        tabQuickViewAdapter.setListener(new TabQuickViewAdapter.OnTabClickListener() {
            @Override
            public void onTabClick(String tag) {
                fragmentLruCache.switchToTab(tag);
            }

            @Override
            public void onTabClose(String tag) {
                fragmentLruCache.closeTab(tag);
            }
        });
        tabRecyclerView.setAdapter(tabQuickViewAdapter);


        addTabButton = findViewById(R.id.add_new_tab);
        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentLruCache.addNewTab();
                tabQuickViewAdapter.notifyDataSetChanged();
            }
        });

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
