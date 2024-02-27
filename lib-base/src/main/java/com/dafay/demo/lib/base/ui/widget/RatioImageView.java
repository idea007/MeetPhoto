package com.dafay.demo.lib.base.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 宽度是设置宽度的一定比例
 */
public class RatioImageView extends AppCompatImageView {

    // width/height
    private float ratio = -1f;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio == -1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int newHeight = (int) (originalWidth / ratio);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
    }

}
