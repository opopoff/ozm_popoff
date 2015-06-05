package com.ozm.rocks.ui.personal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.squareup.picasso.Picasso;

public class PersonalAdapter extends ListBindableAdapter<ImageResponse> {
    private Callback callback;
    private Picasso picasso;

    public PersonalAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
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
        AspectRatioImageView mImageView = (AspectRatioImageView) view.findViewById(R.id.my_collection_grid_view_item);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mImageView.setAspectRatio(item.width / (float) item.height);

        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        progressBar.setVisibility(View.VISIBLE);
        if (item.isGIF) {
            Ion.with(getContext()).load(item.url).withBitmap().intoImageView(mImageView).setCallback(
                    new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            picasso.load(item.url).
                    noFade().into(
                    mImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    }
            );
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void click(int position);
    }
}
