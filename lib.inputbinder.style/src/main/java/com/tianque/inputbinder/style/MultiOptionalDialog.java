package com.tianque.inputbinder.style;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiOptionalDialog implements AdapterView.OnItemClickListener {
    public static final String SEPARATOR = ",";
    private Context mContext;
    //    private ViewBehavioralController tvf;
    private onOptionalItemSelect monOptionalItemSelect;
    private ListView lv;
    private AlertDialog.Builder builder;
    private View showView;

    private boolean[] selecteds;
//    private List<String> selectPosition;
//    private HashMap<String, String> mMultiText = null, mMultiId = null;
    private MultiOptionalInputItem multiOptionalInputItem;

    public MultiOptionalDialog(Context context, MultiOptionalInputItem inputItem, View showView,
                               InputItemProfile attrs) {
        mContext = context;
        this.multiOptionalInputItem = inputItem;
        this.showView = showView;
        lv = new ListView(mContext);
        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lv.setBackgroundColor(Color.TRANSPARENT);
        lv.setCacheColorHint(Color.TRANSPARENT);
        selecteds = inputItem.getMultiOptionalData().getSelectedIndexs();
        SelectorAdapter sAdapter = new SelectorAdapter(inputItem.getMultiOptionalData().getOptionalTexts(), showView);
        lv.setAdapter(sAdapter);
        lv.setOnItemClickListener(this);
        initDialog();
    }

    private void initDialog() {
        builder = new AlertDialog.Builder(mContext);

        builder.setTitle("类型选择")
                .setView(lv)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        multiOptionalInputItem.setSelectedIndexes(selecteds);
                        return;
                    }
                }).setNegativeButton("取消", null);
    }

    public void show() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(showView.getWindowToken(), 0); //强制隐藏键盘
        if (builder == null) {
            initDialog();
        }
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (monOptionalItemSelect != null)
            monOptionalItemSelect.getPosition(position);
    }

    public interface onOptionalItemSelect {
        void getPosition(int position);
    }

    public void setMonOptionalItemSelect(onOptionalItemSelect monOptionalItemSelect) {
        if (monOptionalItemSelect != null) {
            this.monOptionalItemSelect = monOptionalItemSelect;
        }
    }

    private class SelectorAdapter extends BaseAdapter {
        private String[] showTexts;
        private View view;

        public SelectorAdapter(String[] showTexts, View view) {
            this.showTexts =showTexts;

            this.view = view;
//            mMultiId = new HashMap<String, String>();
//            if (view instanceof Button)
//                mMultiText = formatStringToMap(((Button) view).getText().toString());
//            else if (view instanceof EditText)
//                mMultiText = formatStringToMap(((EditText) view).getText().toString());
//            else if (view instanceof ItemBoxBase)
//                mMultiText = formatStringToMap(((ItemBoxBase) view).getContent().toString());
        }

        @Override
        public int getCount() {
            return showTexts.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.sw_type_selector_item_layout, null);
            }
//            String bttext = "";
//            if (view instanceof Button) {
//                Button bt = (Button) view;
//                bttext = bt.getText().toString();
//            } else if (view instanceof EditText) {
//                EditText ib = (EditText) view;
//                bttext = ib.getText().toString();
//            } else if (view instanceof ItemBoxBase) {
//                ItemBoxBase ib = (ItemBoxBase) view;
//                bttext = ib.getContent().toString();
//            }
            CheckBox ck = (CheckBox) convertView.findViewById(R.id.sw_ck_selector_item);
            ck.setChecked(false);
            ck.setOnClickListener(onClick);
            if (selecteds[position]) {
                ck.setChecked(true);
//                mMultiId.put(String.valueOf(ckList.get(position).getId()),
//                        String.valueOf(ckList.get(position).getId()));
//                mMultiText.put(ckList.get(position), ckList.get(position));
            }
            TextView tv = (TextView) convertView.findViewById(R.id.sw_tv_selector_item);
            String text = showTexts[position];
            ck.setTag(position);
            tv.setText(text);

            return convertView;
        }

        private View.OnClickListener onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int index = Integer.valueOf(v.getTag().toString());
                selecteds[index] = cb.isChecked();
//                if (cb.isChecked()) {
////                    mMultiId.put(String.valueOf(ckList.get(index).getId()),
////                            String.valueOf(ckList.get(index).getId()));
//                } else {
//                    if (mMultiText.containsKey(ckList.get(index))) {
//                        mMultiText.remove(ckList.get(index));
//                    }
//                    if (selectPosition.contains(index + "")) {
//                        selectPosition.remove(index + "");
//                    }
////                    mMultiId.remove(String.valueOf(ckList.get(index).getId()));
//                }
//                if (view instanceof Button) {
//                    Button bt = (Button) view;
//                    bt.setText(formatHashMapToString(mMultiText));
//                } else if (view instanceof EditText) {
//                    EditText ib = (EditText) view;
//                    ib.setText(formatHashMapToString(mMultiText));
//                } else if (view instanceof ItemBoxBase) {
//                    ItemBoxBase ib = (ItemBoxBase) view;
//                    ib.setContent(formatHashMapToString(mMultiText));
//                }
            }
        };
    }


    private HashMap<String, String> formatStringToMap(String string) {
        int i;
        String[] strings;
        HashMap<String, String> m = new HashMap<String, String>();
        strings = string.trim().split(SEPARATOR);

        if (!strings[0].equals("")) {
            for (i = 0; i < strings.length; i++) {
                m.put(strings[i], strings[i]);
            }
        }
        return m;
    }

    private String formatHashMapToString(HashMap<String, String> hashMap) {
        Iterator<Map.Entry<String, String>> entry = hashMap.entrySet().iterator();
        StringBuffer buffer = new StringBuffer();
        while (entry.hasNext()) {
            Map.Entry<String, String> i = entry.next();
            buffer.append(i.getKey() + SEPARATOR);
        }

        if (buffer.toString().length() > 0)

            return buffer.toString().substring(0, buffer.toString().length() - 1);
        return "";
    }
}
