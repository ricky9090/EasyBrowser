package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ricky.easybrowser.R;

public class NewTabView extends LinearLayout {

    private RecyclerView siteGird;
    private SiteAdapterV2 siteAdapter;

    private TextView title;

    public NewTabView(Context context) {
        this(context, null);
    }

    public NewTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.fragment_new_tab, this);

        title = findViewById(R.id.new_tab_tag);

        siteGird = findViewById(R.id.site_grid);
        siteGird.setLayoutManager(new GridLayoutManager(getContext(), 3));
        siteAdapter = new SiteAdapterV2(getContext());
        siteAdapter.appendDataList(SiteAdapterV2.getTestDataList(context));
        siteGird.setAdapter(siteAdapter);
        //PagerSnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(siteGird);
    }

    public void setSiteListener(SiteAdapterV2.OnSiteItemClickListener listener) {
        siteAdapter.setListener(listener);
    }

    public void setTabTitle(String name) {
        title.setText(name);
    }

}
