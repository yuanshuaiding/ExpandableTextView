package com.eric.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * author : Eric
 * e-mail : yuanshuaiding@163.com
 * time   : 2017/10/26
 * desc   : 可展开折叠的文本控件（使用LinearLayout封装）
 * version: 1.0.2
 */
public class ExpandableTextView extends LinearLayout {
    private String TAG = this.getClass().getSimpleName();
    private final static String imgTag = "[img]";
    private String TIP_COLLAPSE = "收起";
    private String TIP_EXPAND = "展开";
    private static final String ELLIPSE = "...";
    private static final int ALIGN_RIGHT = 0;//控制按钮在文本右下角，与文本底部基线齐平
    private static final int BOTTOM_START = 1;//控制按钮在文本下方左侧
    private static final int BOTTOM_CENTER = 2;//控制按钮在文本下方中间
    private static final int BOTTOM_END = 3;//控制按钮在文本下方右侧侧
    private final TextView mTvContent;//内容
    private final TextView mTvContentTemp;//收起后的内容
    private final TextView mTvExpand;//折叠控件
    protected boolean mIsExpand;//是否折叠的标记
    private String mOriginText = "";//展示的文本
    private int mContentTextSize;
    private int mContentColor;
    private int mTipsColor;
    private int mLines = 3;//折叠后显示的行数，默认为3行
    private int mPosition = BOTTOM_CENTER;
    private Drawable mCollapseDrawable;
    private Drawable mExpandDrawable;
    private int collapseHeight;
    private boolean performedByUser;
    protected boolean mCancelAnim;
    private int mTextTotalWidth;
    /**
     * 提示文本的点击事件
     */
    private ExpandedClickableSpan mClickableSpan = new ExpandedClickableSpan();
    /**
     * 展开折叠监听器
     */
    private OnToggleListener toggleListener;
    protected boolean mMeasured;

    public ExpandableTextView(Context context) {
        this(context, null, 0);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        if (typedArray != null) {
            mOriginText = typedArray.getString(R.styleable.ExpandableTextView_android_text);
            mLines = typedArray.getInteger(R.styleable.ExpandableTextView_collapseLines, mLines);
            mContentColor = typedArray.getColor(R.styleable.ExpandableTextView_android_textColor, Color.BLACK);
            mTipsColor = typedArray.getColor(R.styleable.ExpandableTextView_tipsColor, Color.RED);
            mContentTextSize = typedArray.getDimensionPixelSize(R.styleable.ExpandableTextView_android_textSize, 14);
            mPosition = typedArray.getInteger(R.styleable.ExpandableTextView_tipPosition, mPosition);
            mExpandDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_expandDrawable);
            if (mExpandDrawable != null) {
                mExpandDrawable.setBounds(0, 0, mContentTextSize, mContentTextSize);
            }
            mCollapseDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_collapseDrawable);
            if (mCollapseDrawable != null) {
                mCollapseDrawable.setBounds(0, 0, mContentTextSize, mContentTextSize);
            }
            String expandLabel = typedArray.getString(R.styleable.ExpandableTextView_expandTipLabel);
            if (!TextUtils.isEmpty(expandLabel)) {
                TIP_EXPAND = expandLabel;
            }
            String collapseLabel = typedArray.getString(R.styleable.ExpandableTextView_collapseTipLabel);
            if (!TextUtils.isEmpty(collapseLabel)) {
                TIP_COLLAPSE = collapseLabel;
            }
            typedArray.recycle();
        }
        setOrientation(VERTICAL);
        //加载布局
        LayoutInflater.from(context).inflate(R.layout.layout_expandable_view, this, true);
        //获取文本控件
        mTvContent = findViewById(R.id.tv_content);
        mTvContent.setHeight(0);
        mTvContentTemp = findViewById(R.id.tv_content_temp);
        mTvExpand = findViewById(R.id.tv_arrow);
        mTvExpand.setTextColor(mTipsColor);
        mTvExpand.setTextSize(mContentTextSize);
        mTvExpand.setCompoundDrawablePadding(5);
        if (mExpandDrawable != null) {
            mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, mExpandDrawable, null);
        }
        mTvContent.setTextColor(mContentColor);
        mTvContentTemp.setTextColor(mContentColor);
        mTvContent.setTextSize(mContentTextSize);
        mTvContentTemp.setTextSize(mContentTextSize);
        mTvContentTemp.setEllipsize(TextUtils.TruncateAt.END);
        mTvContentTemp.setMaxLines(mLines);
        mTvExpand.setText(TIP_EXPAND);
        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                performedByUser = true;
                mCancelAnim = false;
                toggleText();
            }
        };
        mTvContent.setOnClickListener(clickListener);
        mTvContentTemp.setOnClickListener(clickListener);
        mTvExpand.setOnClickListener(clickListener);
        if (!TextUtils.isEmpty(mOriginText))
            setText(mOriginText);
        //设置折叠展开标识控件的位置
        updateExpandArrowAndPosition(mPosition);
    }

    private void updateExpandArrowAndPosition(int position) {
        LayoutParams params = new LayoutParams(mTvExpand.getLayoutParams());
        switch (position) {
            case BOTTOM_START:
                params.gravity = Gravity.LEFT;
                break;
            case BOTTOM_CENTER:
                params.gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case BOTTOM_END:
                params.gravity = Gravity.RIGHT;
                break;
            case ALIGN_RIGHT:
                params.gravity = Gravity.NO_GRAVITY;
                mTvExpand.setVisibility(GONE);
                break;
            default:
                params.gravity = Gravity.CENTER_HORIZONTAL;
                break;
        }
        mTvExpand.setLayoutParams(params);
    }

    private void initText(final String text) {
        //根据指定的折叠行数获取折叠文本
        mOriginText = text;
        mTvContent.setText(mOriginText);
        mTvContentTemp.setText(mOriginText);
        if (getViewTreeObserver() != null) {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!mMeasured) {
                        //获取控件尺寸
                        if (getWidth() != 0) {
                            mTextTotalWidth = getWidth() - getPaddingLeft() - getPaddingRight();
                            Log.d(TAG, "控件宽度：" + mTextTotalWidth);
                            mMeasured = true;
                            toggleText();
                        }
                    }
                    return true;
                }
            });
        }
    }

    public void setText(String text) {
        //if (TextUtils.isEmpty(text)) return;
        mIsExpand = !mIsExpand;
        performedByUser = false;
        initText(text);
    }

    /**
     * 指定显示文本,并指定赋值后展开还是折叠的状态
     *
     * @param text  显示文本
     * @param close true代表默认收起,false代表默认展开
     */
    public void setText(String text, boolean close) {
        mIsExpand = !close;
        performedByUser = true;
        initText(text);
    }

    /**
     * 设置展开提示语，在setText方法之前调用
     *
     * @param label 展开提示语，如“展开”
     */
    public void setExpandLabel(String label) {
        if (!TextUtils.isEmpty(label)) {
            TIP_EXPAND = label;
        }
    }

    /**
     * 设置折叠提示语，在setText方法之前调用
     *
     * @param label 折叠提示语，如“收起”
     */
    public void seCollapseLabel(String label) {
        if (!TextUtils.isEmpty(label)) {
            TIP_COLLAPSE = label;
        }
    }

    /**
     * 设置展开提示图标，在setText方法之前调用
     *
     * @param drawable 展开图标
     */
    public void setExpandDrawable(Drawable drawable) {
        if (drawable != null) {
            mExpandDrawable = drawable;
            mExpandDrawable.setBounds(0, 0, mContentTextSize, mContentTextSize);
        }
    }

    /**
     * 设置折叠提示图标，在setText方法之前调用
     *
     * @param drawable 折叠图标
     */
    public void setCollapseDrawable(Drawable drawable) {
        if (drawable != null) {
            mCollapseDrawable = drawable;
            mCollapseDrawable.setBounds(0, 0, mContentTextSize, mContentTextSize);
        }
    }

    public String getText() {
        return mOriginText;
    }

    /**
     * 展开或收起文本
     */
    public void toggleText() {
        //修改展开折叠标志
        mIsExpand = !mIsExpand;
        if (mPosition == ALIGN_RIGHT) {
            mTvExpand.setVisibility(GONE);
            mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
            mTvContentTemp.setMovementMethod(LinkMovementMethod.getInstance());
            formatExpandText(mOriginText);
            formatCollapseText(mOriginText);
        }
        //计算控件高度
        int expandHeight = getTextViewHeight(mTvContent) + (mPosition == ALIGN_RIGHT ? 2 : 0);//稍微修正一下高度
        collapseHeight = getTextViewHeight(mTvContentTemp);
        Log.d(TAG, "展开高度" + expandHeight);
        Log.d(TAG, "收起高度" + collapseHeight);
        //此处使用LinearLayout.LayoutParams控制辅助文本控件mTvContentTemp的高度
        final LinearLayout.LayoutParams layoutParams = (LayoutParams) mTvContentTemp.getLayoutParams();
        if (expandHeight <= collapseHeight) {
            //说明无需折叠
            layoutParams.height = 0;
            mTvContentTemp.setLayoutParams(layoutParams);
            mTvContent.setHeight(expandHeight);
            mTvExpand.setVisibility(GONE);
            return;
        } else {
            if (mPosition != ALIGN_RIGHT) {
                mTvExpand.setVisibility(VISIBLE);
            }
        }
        if (!performedByUser) {
            layoutParams.height = collapseHeight;
            mTvContentTemp.setLayoutParams(layoutParams);
            mTvContent.setHeight(0);
            mIsExpand = false;
            if (mExpandDrawable != null) {
                mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, mExpandDrawable, null);
            }
            mTvExpand.setText(TIP_EXPAND);
            return;
        }
        if (mIsExpand) {
            if (mCancelAnim) {
                mTvContent.setHeight(expandHeight);
                layoutParams.height = 0;
                mTvContentTemp.setLayoutParams(layoutParams);
            } else {
                //展开动画
                ValueAnimator anim = ValueAnimator.ofInt(collapseHeight, expandHeight).setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mTvContent.setHeight((Integer) animation.getAnimatedValue());
                        layoutParams.height = 0;
                        mTvContentTemp.setLayoutParams(layoutParams);
                    }
                });
                anim.start();
            }
            if (mCollapseDrawable != null) {
                mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, mCollapseDrawable, null);
            } else {
                mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            mTvExpand.setText(TIP_COLLAPSE);
            if (toggleListener != null) {
                toggleListener.onToggle(true);
            }
        } else {
            if (mCancelAnim) {
                mTvContent.setHeight(0);
                layoutParams.height = collapseHeight;
                mTvContentTemp.setLayoutParams(layoutParams);
            } else {
                //收起动画
                ValueAnimator anim = ValueAnimator.ofInt(expandHeight, collapseHeight).setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int h = (Integer) animation.getAnimatedValue();
                        mTvContent.setHeight(h);
                        if (h == collapseHeight) {
                            mTvContent.setHeight(0);
                            layoutParams.height = collapseHeight;
                            mTvContentTemp.setLayoutParams(layoutParams);
                        }
                    }
                });
                anim.start();
            }
            if (mExpandDrawable != null) {
                mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, mExpandDrawable, null);
            } else {
                mTvExpand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            mTvExpand.setText(TIP_EXPAND);
            if (toggleListener != null) {
                toggleListener.onToggle(false);
            }
        }
        invalidate();
    }

    /**
     * 格式化折叠时的文本
     */
    public void formatCollapseText(CharSequence text) {
        // 获取 layout，用于计算行数
        Layout layout = mTvContentTemp.getLayout();
        // 调用 setText 用于重置 Layout
        if (layout == null || !layout.getText().equals(text)) {
            mTvContentTemp.setText(text);
            layout = mTvContentTemp.getLayout();
        }
        if (layout == null) return;
        // 获取 paint，用于计算文字宽度
        TextPaint paint = mTvContentTemp.getPaint();
        int line = layout.getLineCount();
        if (line <= mLines) {
            mTvContentTemp.setText(text);
        } else {
            // 最后一行的开始字符位置
            int lastLineStartIndex = layout.getLineStart(mLines - 1);
            // 最后一行的结束字符位置
            int lastLineEndIndex = layout.getLineVisibleEnd(mLines - 1);
            // 计算后缀的宽度
            String spaceImageTag = " " + imgTag;
            if (mExpandDrawable == null) {
                spaceImageTag = "";
            }
            //这里使用4个空格是为了确保最终拼接的长度不会超过整行宽度
            int expandedTextWidth = (int) paint.measureText(ELLIPSE + "  " + TIP_EXPAND + spaceImageTag);
            // 获取最后一行的宽
            float lastLineWidth = layout.getLineWidth(mLines - 1);
            // 如果大于屏幕宽度则需要减去部分字符
            if (lastLineWidth + expandedTextWidth >= mTextTotalWidth) {
                int cutCount = paint.breakText(mOriginText, lastLineStartIndex, lastLineEndIndex, false, expandedTextWidth, null);
                lastLineEndIndex -= cutCount;
            }
            StringBuilder appd = new StringBuilder(ELLIPSE);

            //再测量一下,有可能放置不下
            lastLineEndIndex = ensureLastLineEndIndex(paint, lastLineStartIndex, lastLineEndIndex, spaceImageTag, appd);
            // 因设置的文本可能是带有样式的文本，如SpannableStringBuilder，所以根据计算的字符数从原始文本中截取
            SpannableStringBuilder spannable = new SpannableStringBuilder();
            // 截取文本，还是因为原始文本的样式原因不能直接使用paragraphs中的文本
            CharSequence ellipsizeText = mOriginText.subSequence(0, lastLineEndIndex);
            spannable.append(ellipsizeText);
            spannable.append(appd.toString());
            // 设置样式
            setSpan(spannable, false);
            Log.d("截取后的字符串:", spannable.toString());
            mTvContentTemp.setText(spannable);
        }
    }

    protected int ensureLastLineEndIndex(TextPaint paint, int lastLineStartIndex, int lastLineEndIndex, String spaceImageTag, StringBuilder appd) {
        float lastLineWidth;
        lastLineWidth = paint.measureText(mOriginText.substring(lastLineStartIndex, lastLineEndIndex) + "  " + ELLIPSE + "  " + TIP_EXPAND + spaceImageTag);
        if (lastLineWidth > mTextTotalWidth) {
            //再减掉一个字
            lastLineEndIndex--;
            //添加点占位
            int spaceWidth = (int) paint.measureText(".");
            int spaceCount = (int) ((mTextTotalWidth - paint.measureText(mOriginText.substring(lastLineStartIndex, lastLineEndIndex) + "  " + ELLIPSE + "  " + TIP_EXPAND + spaceImageTag)) / spaceWidth);
            for (int i = 0; i < spaceCount; i++) {
                appd.append(".");
            }
        }
        return lastLineEndIndex;
    }

    /**
     * 格式化展开式的文本，直接在后面拼接即可
     */
    private void formatExpandText(String text) {
        // 获取 layout，用于计算行数
        Layout layout = mTvContent.getLayout();
        // 调用 setText 用于重置 Layout
        if (layout == null || !layout.getText().equals(text)) {
            mTvContent.setText(text);
            layout = mTvContent.getLayout();
        }
        if (layout == null) return;
        // 获取 paint，用于计算文字宽度
        TextPaint paint = mTvContent.getPaint();
        int line = layout.getLineCount();
        if (line <= mLines) {
            mTvContent.setText(text);
        } else {
            String space = "  ";
            // 计算后缀的宽度
            int expandedTextWidth = (int) paint.measureText(space + TIP_COLLAPSE + " " + imgTag) + 1;
            // 获取最后一行的宽
            float lastLineWidth = layout.getLineWidth(line - 1);
            // 如果大于屏幕宽度则需要使用空白填充，让控件自动换行
            if (lastLineWidth + expandedTextWidth > mTextTotalWidth) {
                float spaceWidth = mTextTotalWidth - lastLineWidth;
                int spaceCount = (int) (spaceWidth / mTvExpand.getTextSize());
                for (int i = 0; i <= spaceCount; i++) {
                    space += space;
                }
                text += space;
            }
            SpannableStringBuilder spannable = new SpannableStringBuilder(text);
            setSpan(spannable, true);
            mTvContent.setText(spannable);
        }
    }

    /**
     * 设置提示的样式
     *
     * @param spannable 需修改样式的文本
     */
    private void setSpan(SpannableStringBuilder spannable, boolean isExpand) {
        Drawable drawable = null;
        // 添加一点空白用于分隔
        spannable.append("  ");
        int tipsLen;
        // 判断是展开还是收起
        if (isExpand) {
            spannable.append(TIP_COLLAPSE);
            if (mCollapseDrawable != null) {
                // 插入图片
                drawable = mCollapseDrawable;
                drawable.setBounds(0, 0, (int) mTvContent.getTextSize(), (int) mTvContent.getTextSize());
            }
            tipsLen = TIP_COLLAPSE.length();
        } else {
            spannable.append(TIP_EXPAND);
            if (mExpandDrawable != null) {
                drawable = mExpandDrawable;
                drawable.setBounds(0, 0, (int) mTvContent.getTextSize(), (int) mTvContent.getTextSize());
            }
            tipsLen = TIP_EXPAND.length();
        }
        // 设置点击事件
        spannable.setSpan(mClickableSpan, spannable.length() - tipsLen, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        // 如果提示的图片资源不为空，则添加图片
        if (drawable != null) {
            spannable.append(" ").append(imgTag);
            int start = spannable.length() - imgTag.length();
            int end = spannable.length();
            spannable.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.append(" ");
        }
    }

    private int getTextViewHeight(TextView textView) {
        int height;
        int lines = textView.getLineCount();
        int lineHight = textView.getLineHeight();
        if (textView == mTvContentTemp) {
            if (lines > mLines)
                lines = mLines;
        }
        height = lines * lineHight;
        int pt = textView.getPaddingTop();
        int pb = textView.getPaddingBottom();
        height = height + pt + pb;
        return height;
    }

    /**
     * 提示的点击事件
     */
    private class ExpandedClickableSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            //do nothing,因为整个文本控件已设置了点击事件
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置展开折叠颜色
            ds.setColor(mTipsColor);
            //去掉下划线
            ds.setUnderlineText(false);
        }
    }

    public void setToggleListener(OnToggleListener listener) {
        this.toggleListener = listener;
    }

    public interface OnToggleListener {
        void onToggle(boolean expanded);
    }
}