package com.ozm.fun.ui.bugreport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.ozm.R;

public final class BugReportDialog extends AlertDialog implements BugReportView.ReportDetailsListener {
    public interface ReportListener {
        void onBugReportSubmit(BugReportView.Report report);
    }

    private ReportListener listener;

    @SuppressLint("InflateParams")
    public BugReportDialog(Context context) {
        super(context);

        final BugReportView view =
                (BugReportView) LayoutInflater.from(context).inflate(R.layout.bugreport_view, null);
        view.setBugReportListener(this);

        setTitle("Report a bug");
        setView(view);
        setButton(Dialog.BUTTON_NEGATIVE, "Cancel", (OnClickListener) null);
        setButton(Dialog.BUTTON_POSITIVE, "Submit", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onBugReportSubmit(view.getReport());
                }
            }
        });
    }

    public void setReportListener(ReportListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onStart() {
        getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onStateChanged(boolean valid) {
        getButton(Dialog.BUTTON_POSITIVE).setEnabled(valid);
    }
}
