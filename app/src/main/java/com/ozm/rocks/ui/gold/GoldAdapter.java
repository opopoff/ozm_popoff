package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;

public class GoldAdapter extends ListBindableAdapter<ImageResponse> {

    public GoldAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.gold_grid_item;
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
        AspectRatioImageView mImageView = (AspectRatioImageView) view.findViewById(R.id.gold_grid_view_item);
        mImageView.setAspectRatio(item.width / (float) item.height);
        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        Ion.with(getContext()).load(item.url).intoImageView(mImageView);
    }
}
