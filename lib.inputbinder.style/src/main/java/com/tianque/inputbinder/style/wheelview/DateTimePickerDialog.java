package com.tianque.inputbinder.style.wheelview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.tianque.inputbinder.style.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DateTimePickerDialog {
    private static final int START_YEAR = 1900;
    private static final int END_YEAR = 2100;
    private final Context mContext;
    private final OnDataSetListener onDataSetListener;
    private WheelView wv_year, wv_month, wv_day, wv_hours, wv_mins;
    private boolean showYear = false;
    private boolean showMonth = false;
    private boolean showDay = false;

    private AlertDialog mDialog;
    private boolean isShowClear = false;

    public DateTimePickerDialog(Context context, OnDataSetListener onDataSetListener) {
        this.mContext = context;
        this.onDataSetListener = onDataSetListener;
    }

    public void setShowClear(boolean showClear) {
        isShowClear = showClear;
    }

    protected void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.date_time_picker_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(showYear ? "请选择年份" : showMonth ? "请选择月份" : "请选择时间")
                .setView(view)
//                .setWidth(showYear ? 0.5f : showMonth ? 0.7f : showDay ? 0.8f : 0.95f)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDataSetListener.OnDataSet(false, wv_year.getCurrentItem() + START_YEAR,
                                wv_month.getCurrentItem(), wv_day.getCurrentItem() + 1,
                                wv_hours.getCurrentItem(), wv_mins.getCurrentItem());
                    }
                });
//                .setPositiveButton(new MaterialDialog.OnClickListener() {
//                    @Override
//                    public boolean onClick(DialogInterface dialog, int which) {
//                        return !onDataSetListener.OnDataSet(false, wv_year.getCurrentItem() + START_YEAR,
//                                wv_month.getCurrentItem(), wv_day.getCurrentItem() + 1,
//                                wv_hours.getCurrentItem(), wv_mins.getCurrentItem());
//                    }
//                }).setNegativeButton(null);

        if (isShowClear) {
            builder.setNeutralButton("清除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDataSetListener.OnDataSet(true, -1, -1, -1, -1, -1);
                }
            });
        }
        mDialog = builder.create();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(true);// 可循环滚动
        wv_year.setLabel("年");// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel("月");
        wv_month.setCurrentItem(month);
        if (showYear)
            wv_month.setVisibility(View.GONE);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
            wv_day.setAdapter(new NumericWheelAdapter(1, isLeapYear ? 29 : 28));
        }
        wv_day.setLabel("日");
        wv_day.setCurrentItem(day - 1);
        if (showYear || showMonth)
            wv_day.setVisibility(View.GONE);

        // 时
        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCyclic(true);
        wv_hours.setLabel("时");
        wv_hours.setCurrentItem(hour);
        if (showYear || showMonth || showDay)
            wv_hours.setVisibility(View.GONE);

        // 分
        wv_mins = (WheelView) view.findViewById(R.id.mins);
        wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wv_mins.setCyclic(true);
        wv_mins.setLabel("分");
        wv_mins.setCurrentItem(minute);
        if (showYear || showMonth || showDay)
            wv_mins.setVisibility(View.GONE);

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);

        // 根据屏幕密度来指定选择器字体的大小
//        int textSize = (int) mContext.getResources().getDimension(R.dimen.text_xmedium);
//
//        wv_day.TEXT_SIZE = textSize;
//        wv_hours.TEXT_SIZE = textSize;
//        wv_mins.TEXT_SIZE = textSize;
//        wv_month.TEXT_SIZE = textSize;
//        wv_year.TEXT_SIZE = textSize;
    }

    public void onlyShowYear(boolean isOnlyShow) {
        showYear = isOnlyShow;
    }

    public void onlyShowMonth() {
        showYear = false;
        showMonth = true;
    }

    public void onlyShowDay() {
        showYear = false;
        showMonth = false;
        showDay = true;
    }


    public interface OnDataSetListener {
        boolean OnDataSet(boolean isClear, int year, int month, int day, int hours, int mins);
    }

    public void show() {
        initView();
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }
}
