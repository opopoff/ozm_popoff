package com.ozm.rocks.ui.my;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.DimenTools;

public class MyCollectionAdapter extends ListBindableAdapter<ImageResponse> {
    private final Point mDisplaySize;

    public MyCollectionAdapter(Context context) {
        super(context);
        mDisplaySize = DimenTools.displaySize(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.my_collection_grid_item;
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
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
        mImageView.setAspectRatio(item.width / (float) item.height);
        Uri uri = Uri.parse(item.url);

        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        Ion.with(getContext()).load(item.url).intoImageView(mImageView);
    }
}
