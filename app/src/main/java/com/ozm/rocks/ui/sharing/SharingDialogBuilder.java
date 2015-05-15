package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SharingDialogBuilder {

    @InjectView(R.id.sharing_dialog_header)
    TextView header;
    @InjectView(R.id.sharing_dialog_top)
    LinearLayout topContainer;
    @InjectView(R.id.sharing_dialog_list)
    ListView list;

    @Nullable
    private
    SharingDialogCallBack mCallBack;
    @Nullable
    private
    LayoutInflater mLayoutInflater;
    private AlertDialog mAlertDialog;
    private View mSharingPickDialog;
    private Activity activity;

    @Inject
    public SharingDialogBuilder() {

    }

    public void setCallback(SharingDialogCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void attach(Activity activity) {
        this.activity = activity;
        mLayoutInflater = activity.getLayoutInflater();
    }

    public void detach() {
        mLayoutInflater = null;
    }

    public void openDialog(final ArrayList<PInfo> pInfos) {
        if (mLayoutInflater != null) {
            SharingDialogAdapter sharingDialogAdapter = new SharingDialogAdapter(activity);
            mSharingPickDialog = mLayoutInflater.inflate(R.layout.main_sharing_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(mLayoutInflater.getContext());
            ButterKnife.inject(this, mSharingPickDialog);
            list.setAdapter(sharingDialogAdapter);
            for (int i = 0; i < pInfos.size(); i++) {
                if (i < 3) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setImageDrawable(pInfos.get(i).getIcon());
                    topContainer.addView(imageView);
                    int padding = topContainer.getResources().getDimensionPixelSize(
                            R.dimen.sharing_dialog_top_element_padding);
                    imageView.setPadding(padding, 0, padding, 0);
                    final int finalI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallBack != null) {
                                mCallBack.share(pInfos.get(finalI));
                            }
                        }
                    });
                } else {
                    sharingDialogAdapter.add(pInfos.get(i));
                }
            }
            builder.setView(mSharingPickDialog);
            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }

    public interface SharingDialogCallBack {
        public void share(PInfo pInfo);
    }

//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent share = new Intent(Intent.ACTION_SEND);
//            share.setType("image/*");
//            Uri uri = Uri.parse("android.resource://your.package.here/drawable/image_name");
//
//            // Add the URI and the caption to the Intent.
//            share.putExtra(Intent.EXTRA_STREAM, uri);
//            share.putExtra(Intent.EXTRA_TEXT, caption);
//
//            // Broadcast the Intent.
//            startActivity(Intent.createChooser(share, "Share to"));
//        }
//    };

}
