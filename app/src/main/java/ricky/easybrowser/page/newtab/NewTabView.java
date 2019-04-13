package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import ricky.easybrowser.R;

public class NewTabView extends LinearLayout {

    private RecyclerView siteGird;
    private SiteAdapter siteAdapter;

    public NewTabView(Context context) {
        this(context, null);
    }

    public NewTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.fragment_new_tab, this);

        siteGird = findViewById(R.id.site_grid);
        siteGird.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        siteAdapter = new SiteAdapter(getContext(), SiteAdapter.DEFAULT_PAGE_SIZE);
        siteAdapter.appendDataList(SiteAdapter.getTestDataList());
        siteGird.setAdapter(siteAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(siteGird);
    }

    public void setSiteListener(SiteAdapter.OnSiteItemClickListener listener) {
        siteAdapter.setListener(listener);
    }


}
