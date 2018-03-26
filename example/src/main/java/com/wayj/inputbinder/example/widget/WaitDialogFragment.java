package com.wayj.inputbinder.example.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wayj.inputbinder.example.R;


/**
 * @author liujingxing  on 15/5/19.
 */
public class WaitDialogFragment extends DialogFragment {

    private View mView;
    private String mTip;
    private TextView mTipView;

    private boolean isShow;

    public WaitDialogFragment() {
        this(null);
    }

    @SuppressLint("ValidFragment")
    public WaitDialogFragment(String title) {
        mTip = TextUtils.isEmpty(title) ? "请稍后..." : title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mView = LayoutInflater.from(getActivity()).inflate(R.layout.wait_loading_layout, null);
        mTipView = (TextView) mView.findViewById(R.id.tip);
        mTipView.setText(mTip);

        Dialog dialog = new Dialog(getActivity(), R.style.wait_loading_dialog_style);
        dialog.setContentView(mView);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        return dialog;
    }

    public void setTip(String tip) {
        mTip = tip;
        if (mTipView != null)
            mTipView.setText(tip);
    }

    public void show(FragmentActivity activity) {
        isShow = true;
        super.show(activity.getSupportFragmentManager(), "");
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        isShow = true;
        super.show(manager, tag);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isShow = false;
        super.onDismiss(dialog);
    }

    public boolean isShow() {
        return isShow;
    }
}
