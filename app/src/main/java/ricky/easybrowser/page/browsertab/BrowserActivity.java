package ricky.easybrowser.page.browsertab;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ricky.easybrowser.R;
import ricky.easybrowser.page.newtab.NewTabFragment;
import ricky.easybrowser.page.webpage.WebPageFragment;

public class BrowserActivity extends AppCompatActivity implements NewTabFragment.OnTabInteractionListener,
        WebPageFragment.OnWebInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.page_frame, NewTabFragment.newInstance())
                    .commitNow();
        }
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
                .commitNow();
    }

    @Override
    public void onWebInteraction(Uri uri) {

    }
}
