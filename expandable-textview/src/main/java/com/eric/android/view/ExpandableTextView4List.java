package com.eric.android.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * author : Eric
 * e-mail : yuanshuaiding@163.com
 * time   : 2017/10/26
 * desc   : 可展开折叠的文本控件（用于列表界面,如RecyclerView）
 * version: 1.0.2
 */
public class ExpandableTextView4List extends ExpandableTextView {

    public ExpandableTextView4List(Context context) {
        this(context, null, 0);
    }

    public ExpandableTextView4List(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView4List(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String text, boolean close) {
        mCancelAnim = true;
        super.setText(text, close);
        if (mMeasured) {
            toggleText();
        }
    }
}