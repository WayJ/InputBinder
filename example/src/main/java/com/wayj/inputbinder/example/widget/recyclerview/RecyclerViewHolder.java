package com.wayj.inputbinder.example.widget.recyclerview;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;//存储item中的子view

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }

    /**
     * 根据viewId获取item中对应的子view
     *
     * @param viewId item中子view的Id
     * @return 返回item中的子view
     */
    public View findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    public void setText(int viewId, CharSequence text) {
        View view = findViewById(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else if (view instanceof AppCompatTextView) {
            ((AppCompatTextView) view).setText(text);
        } else if (view instanceof Button) {
            ((Button) view).setText(text);
        } else if (view instanceof AppCompatButton) {
            ((AppCompatButton) view).setText(text);
        } else {
            try {
                Method m = view.getClass().getMethod("setText", CharSequence.class);
                m.invoke(view, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTextToButton(int viewId, CharSequence text) {
        Button textView = (Button) findViewById(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setToTextView(int viewId, CharSequence text) {
        TextView textView = (TextView) findViewById(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setTextColor(int viewId, int colorRes) {
        TextView textView = (TextView) findViewById(viewId);
        if (textView != null) {
            try {
                textView.setTextColor(colorRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTextCompoundDrawablesWithIntrinsicBounds(int viewId, Drawable left, Drawable top, Drawable right, Drawable bottom) {
        TextView textView = (TextView) findViewById(viewId);
        if (textView != null) {
            try {
                textView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
