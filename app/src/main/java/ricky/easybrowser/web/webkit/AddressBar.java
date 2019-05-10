package ricky.easybrowser.web.webkit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AddressBar extends RelativeLayout {

    public AddressBar(Context context) {
        super(context);
    }

    public AddressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getAddressBarHeight() {
        return getLayoutParams().height;
    }

    public void setAddressBarHeight(int h) {
        getLayoutParams().height = h;
        requestLayout();
    }
}
