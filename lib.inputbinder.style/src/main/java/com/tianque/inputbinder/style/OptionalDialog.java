package com.tianque.inputbinder.style;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class OptionalDialog implements OnItemClickListener {
    int high = 0;
    private View vPopWindow;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private ListView op_list;
    private onOptionalItemSelect monOptionalItemSelect;

    public OptionalDialog(Context context) {
        mContext = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        high = wm.getDefaultDisplay().getHeight();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vPopWindow = inflater.inflate(R.layout.optionaldialog_list, null, false);
        op_list = (ListView) vPopWindow.findViewById(R.id.op_list);
        op_list.setOnItemClickListener(this);

    }

    public void showPopWindow(View showView, List<String> list, onOptionalItemSelect onOptionalItemSelect) {
        onDismiss();
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(showView.getWindowToken(), 0); //强制隐藏键盘
        if (list != null) {
            this.monOptionalItemSelect = onOptionalItemSelect;
            SelectAdapter selectAdapter = new SelectAdapter(list);
            op_list.setAdapter(selectAdapter);

            int[] location = new int[2];
            showView.getLocationOnScreen(location);

            ViewParent viewParent = showView.getParent();
            //循环遍历父view，判断当前是否在弹出框中弹出下拉选择菜单
            while (viewParent instanceof View) {
                final View view = (View) viewParent;
                //R.id.dialog_base_layout是BaseDialog类的布局文件的根id
                if ("root".equals(view.getContentDescription())) {
                    high = view.getHeight();//弹出框的高度
                    location[0] = showView.getLeft();//showView在弹出框中的左边位置
                    location[1] = showView.getTop();//showView在弹出框中的顶部位置
                    break;
                }
                if (view.getId() == android.R.id.content) {
                    break;
                }
                viewParent = view.getParent();
            }

            int x = location[0];
            int y = location[1];
            int[] itemWh = getWidth(list, showView);

            if ((high - y - itemWh[1]) >= y) {
                if ((high - y - itemWh[1]) > itemWh[1] * list.size()) {
                    mPopupWindow = new PopupWindow(vPopWindow, itemWh[0], itemWh[1] * list.size() + 10, true);
                } else {
                    mPopupWindow = new PopupWindow(vPopWindow, itemWh[0], LayoutParams.WRAP_CONTENT, true);
                }
                mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setAnimationStyle(R.style.AnimTop);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));

                mPopupWindow.showAsDropDown(showView, showView.getWidth() - itemWh[0], 0);

            } else {
                if (itemWh[1] * list.size() <= y) {
                    mPopupWindow = new PopupWindow(vPopWindow, itemWh[0], itemWh[1] * list.size() + 10, true);
                    mPopupWindow.setTouchable(true);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setAnimationStyle(R.style.AnimBottom);
                    mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
                    mPopupWindow.showAtLocation(showView, Gravity.NO_GRAVITY, showView.getWidth()
                            - itemWh[0] + x, y - itemWh[1] * list.size() - 10);
                } else {
                    mPopupWindow = new PopupWindow(vPopWindow, itemWh[0], y
                            - getStatusBarHeight(mContext), true);
                    mPopupWindow.setTouchable(true);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setAnimationStyle(R.style.AnimBottom);
                    mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
                    mPopupWindow.showAtLocation(showView, Gravity.NO_GRAVITY, showView.getWidth()
                            - itemWh[0] + x, 0);
                }

            }
        }
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void onDismiss() {
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (monOptionalItemSelect != null)
            monOptionalItemSelect.getPosition(arg2);
        onDismiss();
    }

    private int[] getWidth(List<String> list, View showView) {
        int[] wl = new int[2];
        int length = 0;
        String slength = null;
        for (String s : list) {
            if (length < s.length()) {
                length = s.length();
                slength = s;
            }
        }
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_optional, null);
        TextView titleTv = (TextView) convertView.findViewById(R.id.textView1);
        titleTv.setText(slength);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        titleTv.measure(w, h);
        int height = titleTv.getMeasuredHeight();
        int width = titleTv.getMeasuredWidth();

        if (width > showView.getWidth()) {
            width = showView.getWidth();
        }

        wl[0] = width;
        wl[1] = height;

        return wl;
    }

    public interface onOptionalItemSelect {
        void getPosition(int position);
    }

    public class SelectAdapter extends BaseAdapter {
        List<String> list = new ArrayList<String>();

        public SelectAdapter(List<String> list) {
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_optional, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTv = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String item = (String) getItem(position);
            if (item != null) {

                viewHolder.titleTv.setText(item);
            }

            return convertView;
        }
    }

    class ViewHolder {
        TextView titleTv;
    }
}
