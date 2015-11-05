package com.umad.wat.ui.screen.main.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.umad.R;
import com.umad.wat.base.ActivityConnector;
import com.umad.wat.ApplicationScope;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

@ApplicationScope
public class OnBoardingDialogBuilder extends ActivityConnector<Activity> {

    @OnClick(R.id.on_boarding_dialog_cancel)
    protected void back() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        if (callBack != null) {
            callBack.cancel();
        }
    }

    @OnClick(R.id.on_boarding_dialog_ok)
    protected void ok() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        if (callBack != null) {
            callBack.apply();
        }
    }

    @Nullable
    private
    ChooseDialogCallBack callBack;

    @Nullable
    private AlertDialog mAlertDialog;

    @Inject
    public OnBoardingDialogBuilder() {

    }

    @SuppressWarnings("PMD.UselessParentheses")
    public void openDialog() {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        if (mAlertDialog == null || (!mAlertDialog.isShowing())) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            final View onBoardingDialog = layoutInflater.inflate(R.layout.on_boarding_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            ButterKnife.inject(this, onBoardingDialog);
            builder.setView(onBoardingDialog);
            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }

    public void setCallBack(@Nullable ChooseDialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ChooseDialogCallBack {
        void apply();
        void cancel();
    }
}
