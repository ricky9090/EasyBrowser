package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.page.webpage.WebPageFragment;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener,
        WebPageFragment.OnWebInteractionListener {

    NoScrollViewPager viewPager;
    Button addTabButton;
    Button showTabsButton;
    List<Fragment> fragments = new ArrayList<>();
    BrowserTabAdapter adapter;

    RecyclerView tabRecyclerView;
    TabQuickViewAdapter tabQuickViewAdapter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        viewPager = findViewById(R.id.web_viewpager);
        adapter = new BrowserTabAdapter(getSupportFragmentManager());
        adapter.addTab("about:newTab");
        viewPager.setAdapter(adapter);
        viewPager.setCanScroll(false);

        tabRecyclerView = findViewById(R.id.tab_list_recyclerview);
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        tabQuickViewAdapter = new TabQuickViewAdapter(this);
        tabQuickViewAdapter.attachToBrwoserTabAdapter(adapter);
        tabQuickViewAdapter.setListener(new TabQuickViewAdapter.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        tabRecyclerView.setAdapter(tabQuickViewAdapter);


        addTabButton = findViewById(R.id.add_new_tab);
        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addTab("tabNo " + count);
                count++;
                tabQuickViewAdapter.notifyTabDataSetChanged();
                viewPager.setCurrentItem(adapter.getTabList().size() - 1);
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

    /*@Override
    public void onTabtInteraction(Uri uri) {
        if (uri == null) {
            return;
        }

        String url = uri.getScheme() + uri.getHost();
        Log.d("test", url);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_frame, WebPageFragment.newInstance(url, null))
                .commit();
    }*/

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
