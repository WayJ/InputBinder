package com.wayj.inputbinder.example.widget.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 加载动画
 */
public class SimpleRoundLoadingView extends View {
    private Context context;
    private float mDensity;
    private String defaultColor = "#666666";

    private int widthHalf, heightHalf;
    private float drawL, drawR, drawT, drawB;


    // 创建画笔
    Paint paint = new Paint();

    public SimpleRoundLoadingView(Context context) {
        this(context, null);
    }

    public SimpleRoundLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SimpleRoundLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (height < width) {
            int temp = height;
            height = width;
            width = temp;
        }
        widthHalf = width / 2;
        heightHalf = height / 2;
        drawL = width / 2;
        drawR = height / 2 + width / 4;
        drawT = width / 2 + width / 20;
        drawB = height / 2 + width / 2;
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        mDensity = getResources().getDisplayMetrics().density;
        paint.setColor(Color.parseColor(defaultColor));
        //设置是否使用抗锯齿功能，比较耗资源，减慢绘制速度
        paint.setAntiAlias(false);
        //设定是否使用图像抖动，如true，绘制出来的图片颜色更饱满、清晰
        paint.setDither(true);
        //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式  Cap.ROUND,或方形样式Cap.SQUARE
        paint.setStrokeCap(Paint.Cap.ROUND);
        //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
        paint.setStrokeWidth(mDensity * 1);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
    }

    private int animValue = 0;
    private ValueAnimator viewAnimator;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(animValue, widthHalf, heightHalf);
        for (int i = 14; i <= 194; ) {
            paint.setAlpha(i);
            canvas.rotate(15, widthHalf, heightHalf);
            if (i % 14 == 0)
                canvas.drawRect(drawL, drawR, drawT, drawB, paint);
            i += 7;
        }
    }

    private void initAnimator() {
        if (viewAnimator == null) {
            viewAnimator = ValueAnimator.ofInt(0, 360);
            viewAnimator.setDuration(50);
            viewAnimator.setRepeatMode(ValueAnimator.RESTART);
            viewAnimator.setRepeatCount(ValueAnimator.INFINITE);
            viewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animValue += 4;
                    invalidate();
                }
            });
        }
    }

    public void startAnimator() {
        initAnimator();
        viewAnimator.start();
    }

    public void cancelAnimator() {
        initAnimator();
        viewAnimator.cancel();
    }

    public void setAngle(int angle) {
        animValue = angle;
        invalidate();
    }

    public void setColor(String colorString) {
        paint.setColor(Color.parseColor(colorString));
    }

}
