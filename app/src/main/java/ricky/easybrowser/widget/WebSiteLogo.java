package ricky.easybrowser.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import ricky.easybrowser.R;

public class WebSiteLogo extends View {

    private Paint mPaint = new Paint();
    private Rect bounds = new Rect();

    private String name = "E";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WebSiteLogo(Context context) {
        super(context);
    }

    public WebSiteLogo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WebSiteLogo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();

        mPaint.setAntiAlias(true);
        mPaint.setColor(getContext().getResources().getColor(R.color.blue_gray_600));
        canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.WHITE);

        mPaint.getTextBounds(name, 0, name.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();

        int x = width / 2;  // Align.CENTER
        int y = height / 2 + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        canvas.drawText(name, x, y, mPaint);

    }
}
