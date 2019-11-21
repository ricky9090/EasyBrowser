package ricky.easybrowser.page.frontpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ricky.easybrowser.R;
import ricky.easybrowser.page.browser.IBrowser;
import ricky.easybrowser.widget.BrowserNavBar;

public class FrontPageView extends FrameLayout {

    private RecyclerView siteGird;
    private SiteAdapterV2 siteAdapter;
    private BrowserNavBar navBar;

    public FrontPageView(Context context) {
        this(context, null);
    }

    public FrontPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrontPageView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_new_tab, this);

        siteGird = findViewById(R.id.site_grid);
        siteGird.setLayoutManager(new GridLayoutManager(getContext(), 4));
        siteAdapter = new SiteAdapterV2(getContext());
        siteAdapter.appendDataList(SiteAdapterV2.getTestDataList(context));
        siteGird.setAdapter(siteAdapter);
        //PagerSnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(siteGird);

        navBar = findViewById(R.id.new_tab_nav_bar);
        navBar.setNavListener(new FrontPageNavListener(getContext()));

        ImageView gotoButton = findViewById(R.id.goto_button);
        gotoButton.setImageResource(R.mipmap.ic_arrow_forward_black_36dp);
        TextView addressBar = findViewById(R.id.address_url);
        addressBar.setText(R.string.search_or_type_url);
        addressBar.setTextColor(getResources().getColor(R.color.gray_600, null));
        addressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof IBrowser) {
                    ((IBrowser) context).provideNavController().showAddress("about:blank");
                }
            }
        });
    }

    public void setSiteListener(SiteAdapterV2.OnSiteItemClickListener listener) {
        siteAdapter.setListener(listener);
    }

    public void setTabTitle(String name) {
        // FIXME
    }

    static class FrontPageNavListener implements BrowserNavBar.OnNavClickListener {
        private Context _context;

        public FrontPageNavListener(Context _context) {
            this._context = _context;
        }

        @Override
        public void onItemClick(View itemView) {
            boolean isBrowserController = _context instanceof IBrowser;
            if (!isBrowserController) {
                return;
            }
            IBrowser browser = (IBrowser) _context;
            int id = itemView.getId();
            switch (id) {
                case R.id.nav_back:
                    break;
                case R.id.nav_forward:
                    break;
                case R.id.nav_home:
                    break;
                case R.id.nav_show_tabs:
                    browser.provideNavController().showTabs();
                    break;
                case R.id.nav_setting:
                    browser.provideNavController().showSetting();
                    break;
            }
        }
    }

}
