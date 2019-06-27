package com.tianque.inputbinder.style.itembox;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianque.inputbinder.viewer.InputViewer;
import com.tianque.inputbinder.style.R;


/**
 * Version 1.0
 *
 * @author Ray
 */
public abstract class ItemBoxBase<T> extends LinearLayout implements InputViewer<T> {
    protected final static int MARGIN = 5;
    protected final String nameSpace = "http://schemas.android.com/apk/res/android";
    protected Context mContext = null;

    protected TextView mTitleView = null;
    protected String mTitle = null;
    protected int mTitleSize = 10;
    protected int mEditLength = 0;
    protected boolean mRequire = false;
    protected boolean mHaveMoreLine = false;
    protected boolean mIsVisibleColon;//是否显示冒号，默认显示
    protected boolean isVisibleRightArrow;
    protected boolean isVisibleDrownArrow;//是否显示向下的箭头
    protected int rightArrow;
    protected int editType;
    protected String mIconSrc = null;
    protected String mBoxHint = null;
    protected View mExpansionView = null;
    protected View mLineView = null;
    private int iconSize = 40;// 40*40

    public ItemBoxBase(Context context) {
        this(context, null);
    }

    public ItemBoxBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            String text = attrs.getAttributeValue(nameSpace, "text");
            int textResId = attrs.getAttributeResourceValue(nameSpace, "text", -1);
            if(textResId<=0&&!TextUtils.isEmpty(text)){
                mTitle = text;
            }else
                mTitle = textResId > 0 ? context.getString(textResId) : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.ItemBox);
        editType = tArray.getInteger(R.styleable.ItemBox_editType, 0);
        mEditLength = tArray.getInteger(R.styleable.ItemBox_editLength, 0);

        mIconSrc = tArray.getString(R.styleable.ItemBox_iconSrc);
        mBoxHint = tArray.getString(R.styleable.ItemBox_boxHint);

        int defaultTitleSize = 16;
        try {
            defaultTitleSize = context.getResources().getDimensionPixelSize(R.dimen.material_dialog_textSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTitleSize = tArray.getDimensionPixelSize(R.styleable.ItemBox_titleSize, defaultTitleSize);

        mRequire = tArray.getBoolean(R.styleable.ItemBox_require, false);
        mHaveMoreLine = tArray.getBoolean(R.styleable.ItemBox_haveMoreLine, false);
        mIsVisibleColon = tArray.getBoolean(R.styleable.ItemBox_isVisibleColon, true);
        isVisibleRightArrow = tArray.getBoolean(R.styleable.ItemBox_isVisibleRightArrow, false);
        isVisibleDrownArrow = tArray.getBoolean(R.styleable.ItemBox_isVisibleDrownArrow, false);
        rightArrow = tArray.getResourceId(R.styleable.ItemBox_rightArrow, R.drawable.tree_ex);
        tArray.recycle();
        init(context, attrs);
    }

    public static BitmapDrawable getBtnRightIcon(int h, String count, int bgColor, int textColor) {

        int textSize = h - 15;

        Paint p = new Paint();
        p.setColor(textColor);
        p.setTextSize(textSize);
        p.setTextAlign(Align.CENTER);

        FontMetrics metrics = p.getFontMetrics();
        float fontHeight = metrics.bottom - metrics.top;
        float textBaseY = h - (h - fontHeight) / 2 - metrics.bottom;

        Bitmap bmp = Bitmap.createBitmap(h * 2, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint pnew = new Paint();
        // Color
        pnew.setColor(bgColor);// Color.rgb(31, 130, 198));
        RectF r = new RectF(0, 0, h * 2, h);
        canvas.drawRect(r, pnew);
        canvas.drawText(count, h, textBaseY, p);

        return new BitmapDrawable(bmp);
    }

    public static Drawable getRequiredIcon(Context context, int h, int w, int id) throws Exception {

        // 加载需要操作的图片，这里是一张图片
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), id);
//        Log.i("33333",System.currentTimeMillis()+"&*&&*&*&*&*&*");
//        String resName = context.getResources().getResourceName(id);
//        InputStream resourceAsStream = context.getClass().getClassLoader().getResourceAsStream(resName);
//        Bitmap bitmapOrg = BitmapFactory.decodeResourceStream(context.getResources(),null,
//                resourceAsStream,null,new BitmapFactory.Options());
//        Log.i("888888",System.currentTimeMillis()+"&*&&*&*&*&*&*");
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(context.getResources(), resizedBitmap);

    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.TOP);

        LinearLayout mainLinearLayout = new LinearLayout(context);
        mainLinearLayout.setPadding(2, dip2px(mContext, 3.0f),
                2, dip2px(mContext, 3.0f));
        mainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        initTextView();
        mainLinearLayout.addView(mTitleView);
        try {
            mExpansionView = initExpansionView(context, attrs);
            mainLinearLayout.addView(mExpansionView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayoutParams mainLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mainLinearLayout.setMinimumHeight(dip2px(mContext, 55.0f));
        addView(mainLinearLayout, mainLp);
        mLineView = new View(context);
        setOnSelectLineColor(Color.LTGRAY, false);
        addView(mLineView);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
    }

    private void initTextView() {
        //默认为7个字的宽度 最大宽度为屏幕的三分之一
        int width = mTitleSize * 7 + 5;
        int windowWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        if (width > windowWidth / 3) {
            width = windowWidth / 3;
        }
        LayoutParams lp = new LayoutParams(width, LayoutParams.MATCH_PARENT);
        mTitleView = new TextView(mContext);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        mTitleView.setTextColor(Color.GRAY);
        mTitleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        mTitleView.setLayoutParams(lp);
        lp.rightMargin = MARGIN;
        if (mTitle != null) {
            StringBuffer tempTitle = new StringBuffer();
            tempTitle.append(" ").append(mTitle).append(mIsVisibleColon ? ":" : "");
            mTitleView.setText(tempTitle);
            if (mRequire) {
                mTitleView.append(Html.fromHtml("<font color=red>＊</font>"));
            }
        }

        if (mIconSrc != null) {
            Resources resources = mContext.getResources();
            mIconSrc = getExtensionName(mIconSrc);
            mIconSrc = getFileNameNoEx(mIconSrc);
            int identify = resources.getIdentifier(mContext.getPackageName() + ":drawable/" + mIconSrc, null, null);
            try {
                Drawable drawable_left = getRequiredIcon(mContext, iconSize, iconSize, identify);
                mTitleView.setCompoundDrawablesWithIntrinsicBounds(drawable_left, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void setOnSelectLineColor(int color, boolean select) {
        if (mLineView != null) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,dip2px(mContext, select ? 0.8f : 0.5f));
            mLineView.setLayoutParams(lp);
            mLineView.setBackgroundColor(color);
        }
    }


    /**
     * 初始化扩展视图
     */
    public abstract View initExpansionView(Context context, AttributeSet attrs) throws Exception;

    /**
     * 获得扩展视图
     */
    public View getExpansionView() {
        return mExpansionView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        StringBuffer tempTitle = new StringBuffer();
        tempTitle.append(" ").append(title).append(mIsVisibleColon ? ":" : "");
        mTitleView.setText(tempTitle);
        if (mRequire) {
            mTitleView.append(Html.fromHtml("<font color=red>＊</font>"));
        }
    }

    public boolean isRequired() {
        return mRequire;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mTitleView.setEnabled(enabled);
        getExpansionView().setEnabled(enabled);
        getExpansionView().setFocusable(enabled);
        getExpansionView().setClickable(enabled);
    }

//    public abstract String getContent();
//
//    public abstract void setContent(String content);

    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public boolean isRequire() {
        return mRequire;
    }

    public void setRequire(boolean require) {
        if (mRequire != require) {
            this.mRequire = require;
            StringBuffer tempTitle = new StringBuffer();
            tempTitle.append(" ").append(mTitle).append(mIsVisibleColon ? ":" : "");
            mTitleView.setText(tempTitle);
            if (mRequire) {
                mTitleView.append(Html.fromHtml("<font color=red>＊</font>"));
            }
            //			Drawable drawable_right = null;
            //			if (mRequire) {
            //				drawable_right = getResources().getDrawable(R.drawable.icon_require);
            //			}
            //			Drawable[] drawables = mTitleView.getCompoundDrawables();
            //			mTitleView.setCompoundDrawables(drawables[0], drawables[1], drawable_right, drawables[3]);
        }

    }

//    protected void setBoxVisible() {
//        if (getExpansionView() == null || (!getExpansionView().isEnabled() && TextUtils.isEmpty(getContent()))) {
//            setVisibility(GONE);
//        } else {
//            //可能存在问题
//            setVisibility(getVisibility());
//        }
//    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
//        setBoxVisible();;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
