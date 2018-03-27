package com.tianque.inputbinder.style;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tianque.inputbinder.style.itembox.ItemBoxBase;
import com.tianque.inputbinder.style.wheelview.DateTimePickerDialog;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.TimeUtils;
import com.tianque.inputbinder.util.ToastUtils;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerWidget {
    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    private final Calendar mCalendar = Calendar.getInstance();
    private final int mThisYear = Calendar.getInstance().get(Calendar.YEAR);
    private final int mThisMonth = Calendar.getInstance().get(Calendar.MONTH);

    private boolean mAllowOnlyToday = false;//仅允许今天
    private boolean mNotAllowTodayAfter = false;//不允许今天之后
    private boolean mNotAllowTodayAndAfter = false;//不允许今天并且之后
    private boolean mNotAllowTodayBefore = false;//不允许今天之前
    private boolean mNotAllowTodayAndBefore = false;//不允许今天并且之前

    private boolean mNotAllowThisYearAfter = false;//不允许今年之后
    private boolean mNotAllowThisMonthAfter = false;//不允许当月之后
    private boolean mNotAllowThisMonthAndAfter = false;//不允许当月并且之后

    private boolean onlyShowYear = false;//仅仅显示年份
    private boolean onlyShowMonth = false;//仅仅显示月份(包括年份)
    private boolean onlyShowDay = false;//仅仅显示日期(包括年、月)
    private boolean ignoreTime = true;//进行时间段比较时是否忽略日期后面的时间 注意：紧在时间段比较时用到 时间段比较并不是时间比较
    private boolean isClearTime = true;//清除选择的时间
    private Date mStartDate = null, mEndDate = null;
    private String mTimeForamt;

    private DateTimePickerDialog mDateTimePickerDialog;

    private View mCaller = null;
    private Context mContext = null;

    public DatePickerWidget(Context c) {
        this(c, null);
    }

    public DatePickerWidget(Context context, View caller) {
        mCaller = caller;
        mContext = context;
        mDateTimePickerDialog = new DateTimePickerDialog(mContext, mOnDataSetListener);
    }

    public final DatePickerWidget setPickerCaller(View view) {
        mCaller = view;
        return this;
    }


    DateTimePickerDialog.OnDataSetListener mOnDataSetListener = new DateTimePickerDialog.OnDataSetListener() {
        @Override
        public boolean OnDataSet(boolean isClear, int year, int month, int day, int hours, int mins) {
            String specificTime = "";
            if (!isClear) {
                if (onlyShowYear) {
                    mCalendar.set(Calendar.YEAR, year);
                    if (mNotAllowThisYearAfter && year > mThisYear) {
                        ToastUtils.show(R.string.year_after_error);
                    } else {
                        specificTime = TimeUtils.getSimpleDateFormat(YYYY).format(mCalendar.getTime());
                        onDatePicked(specificTime, mCaller);
                        execSetTextMethod(mCaller, specificTime);
                        return true;
                    }
                } else if (onlyShowMonth) {
                    mCalendar.set(Calendar.YEAR, year);
                    mCalendar.set(Calendar.MONTH, month);
                    if (mNotAllowThisMonthAfter && month > mThisMonth) {
                        ToastUtils.show(R.string.month_after_error);
                    } else if (mNotAllowThisMonthAndAfter && month >= mThisMonth) {
                        ToastUtils.show("只能选择本月之前的月份");
                    } else {
                        specificTime = TimeUtils.getSimpleDateFormat(mTimeForamt != null ? mTimeForamt : YYYY_MM).format(mCalendar.getTime());
                        onDatePicked(specificTime, mCaller);
                        execSetTextMethod(mCaller, specificTime);
                        return true;
                    }
                } else {
                    Date today = new Date();
                    long thisDay = today.getTime() / (1000 * 60 * 60 * 24);
                    mCalendar.set(year, month, day);
                    long selectDay = mCalendar.getTimeInMillis() / (1000 * 60 * 60 * 24);
                    if (!onlyShowDay) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hours);
                        mCalendar.set(Calendar.MINUTE, mins);
                        mCalendar.set(Calendar.SECOND, today.getSeconds());
                    }
                    long startDay = 0, endDay = 0;
                    if (ignoreTime) {
                        if (mStartDate != null) {
                            startDay = mStartDate.getTime() / (1000 * 60 * 60 * 24);
                        }
                        if (mEndDate != null) {
                            endDay = mEndDate.getTime() / (1000 * 60 * 60 * 24);
                        }
                    } else {
                        selectDay = mCalendar.getTimeInMillis();
                        thisDay = today.getTime();
                        if (mStartDate != null) {
                            startDay = mStartDate.getTime();
                        }
                        if (mEndDate != null) {
                            endDay = mEndDate.getTime();
                        }
                    }

                    if (mNotAllowTodayBefore && selectDay < thisDay) {
                        ToastUtils.show(R.string.day_before_error);
                    } else if (mNotAllowTodayAfter && selectDay > thisDay) {
                        ToastUtils.show(R.string.day_after_error);
                    } else if (mNotAllowTodayAndAfter && selectDay >= thisDay) {
                        ToastUtils.show(R.string.before_today);
                    } else if (mNotAllowTodayAndBefore && selectDay <= thisDay) {
                        ToastUtils.show(R.string.after_today);
                    } else if (mAllowOnlyToday && selectDay == thisDay) {
                        ToastUtils.show(R.string.only_today);
                    } else if (startDay != 0 && selectDay <= startDay) {
                        String startTime = TimeUtils.getSimpleDateFormat(ignoreTime ? yyyy_MM_dd : yyyy_MM_dd_HH_mm_ss).format(mStartDate);
                        ToastUtils.show(mContext.getResources().getString(R.string.date_in_error) + startTime
                                + mContext.getResources().getString(R.string.after_error));
                    } else if (endDay != 0 && selectDay > endDay) {
                        String endTime = TimeUtils.getSimpleDateFormat(ignoreTime ? yyyy_MM_dd : yyyy_MM_dd_HH_mm_ss).format(mEndDate);
                        ToastUtils.show(mContext.getResources().getString(R.string.date_in_error) + endTime
                                + mContext.getResources().getString(R.string.before_error));
                    } else {
                        specificTime = TimeUtils.getSimpleDateFormat(mTimeForamt != null ? mTimeForamt : onlyShowDay ?
                                yyyy_MM_dd : yyyy_MM_dd_HH_mm_ss).format(mCalendar.getTimeInMillis());
                        onDatePicked(specificTime, mCaller);
                        execSetTextMethod(mCaller, specificTime);
                        return true;
                    }
                }
            } else {
                onDatePicked("", mCaller);
                execSetTextMethod(mCaller, "");
            }
            return false;
        }
    };

    public final void showDatePicker() {
        mDateTimePickerDialog.show();
    }

    public final DatePickerWidget onlyAllowToday() {
        mAllowOnlyToday = true;
        return this;
    }

    public final DatePickerWidget notAllowTodayBefore() {
        mNotAllowTodayBefore = true;
        mNotAllowTodayAfter = false;
        return this;
    }

    public final DatePickerWidget notAllowTodayAndBefore() {
        mNotAllowTodayAndBefore = true;
        mNotAllowTodayAfter = false;
        return this;
    }


    public final DatePickerWidget notAllowTodayAfter() {
        mNotAllowTodayAfter = true;
        mNotAllowTodayBefore = false;
        return this;
    }

    public final DatePickerWidget notAllowTodayAndAfter() {
        mNotAllowTodayAndAfter = true;
        mNotAllowTodayBefore = false;
        return this;
    }


    public final DatePickerWidget notAllowThisYearAfter() {
        mNotAllowThisYearAfter = true;
        return this;
    }

    public final DatePickerWidget notAllowThisMonthAfter() {
        mNotAllowThisMonthAfter = true;
        return this;
    }

    public final DatePickerWidget notAllowThisMonthAndAfter() {
        mNotAllowThisMonthAndAfter = true;
        return this;
    }

    public final DatePickerWidget noDateLimit() {
        mNotAllowTodayAfter = mNotAllowTodayBefore = false;
        return this;
    }

    public DatePickerWidget onlyShowYear(boolean isOnlyShow) {
        onlyShowYear = isOnlyShow;
        mDateTimePickerDialog.onlyShowYear(isOnlyShow);
        return this;
    }

    public DatePickerWidget onlyShowMonth() {
        onlyShowYear = false;
        onlyShowMonth = true;
        mDateTimePickerDialog.onlyShowMonth();
        return this;
    }

    public DatePickerWidget onlyShowDay() {
        onlyShowYear = false;
        onlyShowMonth = false;
        onlyShowDay = true;
        mDateTimePickerDialog.onlyShowDay();
        return this;
    }

    public DatePickerWidget setShowClear() {
        mDateTimePickerDialog.setShowClear(true);
        return this;
    }

    /**
     * 设置允许的时间段，默认忽略日期后面的时间进行比较
     *
     * @param startDate 开始时间
     * @param endData   结束时间
     */
    public final DatePickerWidget setAllowDate(Date startDate, Date endData) {
        return setAllowDate(startDate, endData, true);
    }

    /**
     * 设置允许的时间段
     *
     * @param startDate  开始时间
     * @param endData    结束时间
     * @param ignoreTime 是否忽略日期后面的时间
     */
    public final DatePickerWidget setAllowDate(Date startDate, Date endData, boolean ignoreTime) {
        mStartDate = startDate;
        mEndDate = endData;
        this.ignoreTime = ignoreTime;
        return this;

    }

    /**
     * Set the date format, the default is 'yyyy-MM-dd'
     *
     * @param format
     * @return
     */
    public final DatePickerWidget setTimeFormat(String format) {
        mTimeForamt = format;
        return this;
    }

    public void onDatePicked(String date, View caller) {

    }

    private void execSetTextMethod(View v, String displayName) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            ((TextView) v).setText(displayName);
        } else if (v instanceof ItemBoxBase) {
            ((ItemBoxBase) v).setContent(displayName);
        } else {
            try {
                Method m = v.getClass().getDeclaredMethod("setText", String.class);
                m.invoke(v, displayName);
            } catch (Exception e) {
                Logging.e(e);
            }
        }
    }

    public boolean vildTime(String firstTime, String secondTime, String thirdTime) {
        SimpleDateFormat formatter = TimeUtils.getSimpleDateFormat(yyyy_MM_dd);
        Date firstDate = null;
        Date secondDate;
        Date thirdDate;
        try {
            if (firstTime != null) {
                firstDate = formatter.parse(firstTime);
            }
            secondDate = formatter.parse(secondTime);
            thirdDate = formatter.parse(thirdTime);
            if (firstTime != null) {// 三个相比
                return thirdDate.after(firstDate) && thirdDate.before(secondDate);
            } else {// 两个相比
                return thirdDate.before(secondDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 还原默认状态
     */
    public final DatePickerWidget restore() {
        mNotAllowThisYearAfter = false;
        mNotAllowThisMonthAfter = false;
        mNotAllowThisMonthAndAfter = false;

        mAllowOnlyToday = false;
        mNotAllowTodayAfter = false;
        mNotAllowTodayAndAfter = false;
        mNotAllowTodayBefore = false;
        mNotAllowTodayAndBefore = false;

        onlyShowYear = false;
        onlyShowMonth = false;
        onlyShowDay = false;

        ignoreTime = true;
        mStartDate = null;
        mEndDate = null;
        return this;
    }
}
