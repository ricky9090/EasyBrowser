package ricky.easybrowser.page.browser.newtab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import ricky.easybrowser.R;
import ricky.easybrowser.page.webpage.WebPageView;
import ricky.easybrowser.utils.EasyLog;
import ricky.easybrowser.utils.OnBackInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTabFragmentV2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTabFragmentV2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTabFragmentV2 extends Fragment implements OnBackInteractionListener {

    public static final String ARG_TITLE = "param_title";
    public static final String ARG_TAG = "param_tag";

    private String mTitle;
    private String mTag;

    private FrameLayout frameLayout;
    private NewTabView newTabView;
    private WebPageView webPageView;

    private OnFragmentInteractionListener mListener;

    public NewTabFragmentV2() {
        // Required empty public constructor
    }

    public static NewTabFragmentV2 newInstance() {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        return fragment;
    }

    public static NewTabFragmentV2 newInstance(String title) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewTabFragmentV2 newInstance(String title, String tag) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mTag = getArguments().getString(ARG_TAG);
            EasyLog.i("test", "title: " + mTitle);
            EasyLog.i("test", "tag: " + mTag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_tab_fragment_v2, container, false);
        frameLayout = rootView.findViewById(R.id.new_tab_v2_frame);

        newTabView = new NewTabView(getContext());
        newTabView.setTabTitle(mTitle);

        newTabView.setSiteListener(new SiteAdapter.OnSiteItemClickListener() {
            @Override
            public void onSiteItemClick(SiteEntity siteEntity) {
                Uri uri = new Uri.Builder()
                        .scheme("http://")
                        .authority(siteEntity.getSiteUrl())
                        .build();

                frameLayout.removeAllViews();
                webPageView = new WebPageView(getContext());
                webPageView.setOnWebPageChangeListener(new WebPageView.OnWebPageChangeListener() {
                    @Override
                    public void onPageTitleChange(String newTitle) {
                        mTitle = newTitle;
                        getArguments().putString(ARG_TITLE, mTitle);
                        if (mListener != null) {
                            mListener.onTabTitleChanged(mTitle);
                        }
                    }
                });
                frameLayout.addView(webPageView);
                webPageView.loadUrl(uri.getScheme() + uri.getHost());
            }
        });
        frameLayout.addView(newTabView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        if (frameLayout.getChildCount() <= 0) {
            return false;
        }

        View view = frameLayout.getChildAt(0);
        if (view instanceof NewTabView) {
            return false;
        }

        if (!(view instanceof WebPageView)) {
            return false;
        }
        if (webPageView.canGoBack()) {
            webPageView.getWebView().goBack();
            return true;
        } else {
            frameLayout.removeAllViews();
            destroyWebView();
            frameLayout.addView(newTabView);
            return true;
        }
    }

    private void destroyWebView() {
        if (webPageView != null && webPageView.getWebView() != null) {
            WebView target = webPageView.getWebView();
            target.stopLoading();
            target.getSettings().setJavaScriptEnabled(false);
            target.clearHistory();
            target.clearCache(true);
            target.loadUrl("about:blank");  // replace target.clearView();
            target.pauseTimers();
            target.removeAllViews();
            target.destroy();
            target = null;
            webPageView = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyWebView();
        frameLayout.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onTabtInteraction(Uri uri);
        void onTabTitleChanged(String title);
    }
}
