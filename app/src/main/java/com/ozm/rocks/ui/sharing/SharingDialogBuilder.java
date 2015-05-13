package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.ozm.R;

import javax.inject.Inject;

public class SharingDialogBuilder {
    @Nullable private
    SharingDialogCallBack mCallBack;
    @Nullable private
    LayoutInflater mLayoutInflater;
    private AlertDialog mAlertDialog;
    private View mSharingPickDialog;

    @Inject
    public SharingDialogBuilder() {

    }

    public void setCallback(SharingDialogCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void attach(Activity activity) {
        mLayoutInflater = activity.getLayoutInflater();
    }

    public void detach() {
        mLayoutInflater = null;
    }

    public void openDialog() {
        mSharingPickDialog = mLayoutInflater.inflate(R.layout.main_sharing_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mLayoutInflater.getContext());
        builder.setView(mSharingPickDialog);
        mAlertDialog = builder.create();

        mAlertDialog.show();
    }

    public interface SharingDialogCallBack {
        public void pick();
    }

}
