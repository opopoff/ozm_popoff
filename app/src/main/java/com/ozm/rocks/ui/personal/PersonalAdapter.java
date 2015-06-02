package com.ozm.rocks.ui.personal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.DimenTools;

public class PersonalAdapter extends ListBindableAdapter<ImageResponse> {
    private final Point mDisplaySize;
    private Callback callback;

    public PersonalAdapter(Context context) {
        super(context);
        mDisplaySize = DimenTools.displaySize(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.personal_grid_item;
    }

    @Override
    public void bindView(ImageResponse item, final int position, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.click(position);
                }
            }
        });
        ImageView like = ((ImageView) view.findViewById(R.id.my_collection_grid_view_like));
        ImageView share = ((ImageView) view.findViewById(R.id.my_collection_grid_view_share));
        if (item.liked) {
            like.setImageResource(R.drawable.ic_history_liked);
        } else {
            like.setVisibility(View.GONE);
        }
        if (item.shared) {
            share.setImageResource(R.drawable.ic_history_shared);
        } else {
            share.setVisibility(View.GONE);
        }
        final float halfXScreenSize = mDisplaySize.x;
        AspectRatioImageView mImageView = (AspectRatioImageView) view.findViewById(R.id.my_collection_grid_view_item);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mImageView.setAspectRatio(item.width / (float) item.height);
        Uri uri = Uri.parse(item.url);

        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }

        progressBar.setVisibility(View.VISIBLE);
        Ion.with(getContext()).load(item.url).intoImageView(mImageView).setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void click(int position);
    }
}
