package ricky.easybrowser.web.gecko;

import android.content.Context;
import android.util.AttributeSet;

import org.mozilla.geckoview.GeckoView;

public class EasyGeckoView extends GeckoView {

    public EasyGeckoView(Context context) {
        super(context);
    }

    public EasyGeckoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}