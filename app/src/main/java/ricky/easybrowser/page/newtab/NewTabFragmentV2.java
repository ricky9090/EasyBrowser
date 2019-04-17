package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import ricky.easybrowser.R;
import ricky.easybrowser.page.webpage.WebPageView;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int indexInViewPager;

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

    public static NewTabFragmentV2 newInstance(String param1, String param2) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public int getIndexInViewPager() {
        return indexInViewPager;
    }

    public void setIndexInViewPager(int indexInViewPager) {
        this.indexInViewPager = indexInViewPager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_tab_fragment_v2, container, false);
        frameLayout = rootView.findViewById(R.id.new_tab_v2_frame);

        newTabView = new NewTabView(getContext());
        newTabView.setTabTitle(mParam1);

        newTabView.setSiteListener(new SiteAdapter.OnSiteItemClickListener() {
            @Override
            public void onSiteItemClick(SiteEntity siteEntity) {
                Uri uri = new Uri.Builder()
                        .scheme("http://")
                        .authority(siteEntity.getSiteUrl())
                        .build();

                frameLayout.removeAllViews();
                webPageView = new WebPageView(getContext());
                frameLayout.addView(webPageView);
                webPageView.loadUrl(uri.getScheme() + uri.getHost());
            }
        });
        frameLayout.addView(newTabView);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTabtInteraction(uri);
        }
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
            destroyWebView();
            frameLayout.removeAllViews();
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

    public interface OnFragmentInteractionListener {
        void onTabtInteraction(Uri uri);
    }
}
