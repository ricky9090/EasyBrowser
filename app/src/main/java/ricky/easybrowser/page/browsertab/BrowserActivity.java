package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragmentV2;
import ricky.easybrowser.page.webpage.WebPageFragment;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragmentV2.OnFragmentInteractionListener,
        WebPageFragment.OnWebInteractionListener {

    ViewPager viewPager;
    Button addTabButton;
    List<Fragment> fragments = new ArrayList<>();
    BrowserTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        viewPager = findViewById(R.id.web_viewpager);
        addTabButton = findViewById(R.id.add_new_tab);

        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addTab("about:newTab");
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new BrowserTabAdapter(getSupportFragmentManager());
        adapter.addTab("about:newTab");
        viewPager.setAdapter(adapter);
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
