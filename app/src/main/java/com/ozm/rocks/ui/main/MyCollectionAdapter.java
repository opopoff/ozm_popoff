package com.ozm.rocks.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.DimenTools;
import com.ozm.rocks.util.UrlFormat;

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
//        float ratio = mDisplaySize.x / 2f / (float) item.width;
        SimpleDraweeView mImageView = (SimpleDraweeView) view.findViewById(R.id.my_collection_grid_view_item);
        mImageView.setAspectRatio(item.width / (float) item.height);
        Uri uri = UrlFormat.getImageUri(item.url);
        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        if (item.isGIF) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true)
                    .build();
            mImageView.setController(controller);
        } else {
            mImageView.setImageURI(uri);
        }
//        view.findViewById(R.id.my_collection_grid_view_item).getLayoutParams().height
//                = (int) (item.height * ratio);
    }
}
