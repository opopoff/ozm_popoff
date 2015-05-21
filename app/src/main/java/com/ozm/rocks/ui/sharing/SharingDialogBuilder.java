package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
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
    private Activity activity;
    private AlertDialog mAlertDialog;

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

    public void openDialog(final ArrayList<PInfo> pInfos, final ImageResponse image) {
        if (mLayoutInflater != null) {
            SharingDialogAdapter sharingDialogAdapter = new SharingDialogAdapter(activity);
            View mSharingPickDialog = mLayoutInflater.inflate(R.layout.main_sharing_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(mLayoutInflater.getContext());
            ButterKnife.inject(this, mSharingPickDialog);
            list.setAdapter(sharingDialogAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == list.getAdapter().getCount() - 3) {
                        Toast.makeText(activity.getApplicationContext(), "hide", Toast.LENGTH_SHORT).show();
                    } else if (position == list.getAdapter().getCount() - 2) {
                        Toast.makeText(activity.getApplicationContext(), "copy", Toast.LENGTH_SHORT).show();
                    } else if (position == list.getAdapter().getCount() - 1) {
                        Toast.makeText(activity.getApplicationContext(), "other", Toast.LENGTH_SHORT).show();
                    } else if (mCallBack != null) {
                        mCallBack.share(pInfos.get(position + 3), image);
                    }
                }
            });
            PInfo pInfo = new PInfo("Hide", null);
            pInfos.add(pInfo);
            pInfo = new PInfo("Скопировать ссылку", null);
            pInfos.add(pInfo);
            pInfo = new PInfo("Другое", null);
            pInfos.add(pInfo);

            for (int i = 0; i < pInfos.size(); i++) {
                if (i < 3 && i < pInfos.size() - 3) {
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
                                mCallBack.share(pInfos.get(finalI), image);
                                mAlertDialog.dismiss();
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
        void share(PInfo pInfo, ImageResponse imageResponse);
    }
}
