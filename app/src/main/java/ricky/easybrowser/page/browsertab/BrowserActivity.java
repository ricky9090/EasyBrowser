package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragment;
import ricky.easybrowser.page.webpage.WebPageFragment;
import ricky.easybrowser.utils.FragmentBackHandleHelper;

public class BrowserActivity extends AppCompatActivity implements NewTabFragment.OnTabInteractionListener,
        WebPageFragment.OnWebInteractionListener {

    String newTabFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        if (savedInstanceState == null) {
            newTabFragmentTag = "newtab" + System.currentTimeMillis();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.page_frame, NewTabFragment.newInstance(), newTabFragmentTag)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newTabFragmentTag = null;
    }

    @Override
    public void onTabtInteraction(Uri uri) {
        if (uri == null) {
            return;
        }

        String url = uri.getScheme() + uri.getHost();
        Log.d("test", url);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_frame, WebPageFragment.newInstance(url, null))
                .commit();
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
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.page_frame);
        if (current != null && current instanceof WebPageFragment) {
            Fragment cacheFragment = getSupportFragmentManager().findFragmentByTag(newTabFragmentTag);
            if (cacheFragment != null) {
                Log.d("test", "restore cached fragment !!!");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_frame, cacheFragment, newTabFragmentTag)
                        .commit();
            } else {
                newTabFragmentTag = "newtab" + System.currentTimeMillis();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_frame, NewTabFragment.newInstance(), newTabFragmentTag)
                        .commit();
            }
            return;
        }
        super.onBackPressed();
    }


}
