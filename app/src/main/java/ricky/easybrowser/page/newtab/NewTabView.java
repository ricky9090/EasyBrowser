package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ricky.easybrowser.R;
import ricky.easybrowser.page.browser.IBrowser;
import ricky.easybrowser.widget.BrowserNavBar;

public class NewTabView extends FrameLayout {

    private RecyclerView siteGird;
    private SiteAdapterV2 siteAdapter;
    private BrowserNavBar navBar;

    private TextView title;

    public NewTabView(Context context) {
        this(context, null);
    }

    public NewTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTabView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_new_tab, this);

        title = findViewById(R.id.new_tab_tag);

        siteGird = findViewById(R.id.site_grid);
        siteGird.setLayoutManager(new GridLayoutManager(getContext(), 3));
        siteAdapter = new SiteAdapterV2(getContext());
        siteAdapter.appendDataList(SiteAdapterV2.getTestDataList(context));
        siteGird.setAdapter(siteAdapter);
        //PagerSnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(siteGird);

        navBar = findViewById(R.id.new_tab_nav_bar);
        navBar.setNavListener(new BrowserNavBar.OnNavClickListener() {
            @Override
            public void onItemClick(View itemView) {
                boolean isBrowserController = getContext() instanceof IBrowser;
                if (!isBrowserController) {
                    return;
                }
                IBrowser browser = (IBrowser) getContext();
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
        });
    }

    public void setSiteListener(SiteAdapterV2.OnSiteItemClickListener listener) {
        siteAdapter.setListener(listener);
    }

    public void setTabTitle(String name) {
        title.setText(name);
    }

}
