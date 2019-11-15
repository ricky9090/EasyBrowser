package ricky.easybrowser.web.legacy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PlaceholderView extends View {

    public PlaceholderView(Context context) {
        super(context);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getPlaceholderHeight() {
        return getLayoutParams().height;
    }

    public void setPlaceholderHeight(int h) {
        getLayoutParams().height = h;
        requestLayout();
    }
}
