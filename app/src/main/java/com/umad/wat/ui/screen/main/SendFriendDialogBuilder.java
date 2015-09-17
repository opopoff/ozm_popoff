package com.umad.wat.ui.screen.main;

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
public class SendFriendDialogBuilder extends ActivityConnector<Activity> {

    @OnClick(R.id.send_friend_send)
    protected void send() {
        if (callBack != null) {
            callBack.share();
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        }
    }

    @OnClick(R.id.send_friend_after)
    protected void cancel() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Nullable
    private
    ChooseDialogCallBack callBack;
    @Nullable
    private AlertDialog alertDialog;

    @Inject
    public SendFriendDialogBuilder() {

    }

    public void setCallback(ChooseDialogCallBack callBack) {
        this.callBack = callBack;
    }

    public void openDialog() {
        if (alertDialog == null || (!alertDialog.isShowing())) {
            final Activity activity = getAttachedObject();
            if (activity == null) return;
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            final View chooseDialog = layoutInflater.inflate(R.layout.send_friend_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            ButterKnife.inject(this, chooseDialog);
            builder.setView(chooseDialog);
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public interface ChooseDialogCallBack {
        void share();
    }
}
