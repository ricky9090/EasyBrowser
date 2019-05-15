package ricky.easybrowser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import ricky.easybrowser.R;

public class BrowserNavBar extends FrameLayout {

    ImageView navBack;
    ImageView navForward;
    ImageView navHome;
    ImageView navTab;
    ImageView navSetting;

    private OnNavClickListener navListener;

    public BrowserNavBar(Context context) {
        this(context, null);
    }

    public BrowserNavBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        LayoutInflater.from(context).inflate(R.layout.layout_bottom_navbar, this);


        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navListener != null) {
                    navListener.onItemClick(v);
                }
            }
        };
        navBack = findViewById(R.id.nav_back);
        navBack.setOnClickListener(clickListener);
        navForward = findViewById(R.id.nav_forward);
        navForward.setOnClickListener(clickListener);
        navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(clickListener);
        navTab = findViewById(R.id.nav_show_tabs);
        navTab.setOnClickListener(clickListener);
        navSetting = findViewById(R.id.nav_setting);
        navSetting.setOnClickListener(clickListener);

    }

    public BrowserNavBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnNavClickListener getNavListener() {
        return navListener;
    }

    public void setNavListener(OnNavClickListener navListener) {
        this.navListener = navListener;
    }

    public interface OnNavClickListener {
        void onItemClick(View itemView);
    }
}
