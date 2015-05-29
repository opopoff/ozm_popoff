package com.ozm.rocks.ui.my;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
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
        Postprocessor redMeshPostprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "redMeshPostprocessor";
            }

            @Override
            public CloseableReference<Bitmap> process(
                    Bitmap sourceBitmap,
                    PlatformBitmapFactory bitmapFactory) {
                float ratio = (float) sourceBitmap.getWidth() / halfXScreenSize;
                CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                        (int) (sourceBitmap.getWidth() / ratio),
                        (int) (sourceBitmap.getHeight() / ratio));
                try {
                    Bitmap destBitmap = bitmapRef.get();
                    Bitmap tempBitmap = Bitmap.createScaledBitmap(sourceBitmap, (int) (sourceBitmap.getWidth() / ratio),
                            (int) (sourceBitmap.getHeight() / ratio), false);
                    for (int x = 0; x < destBitmap.getWidth(); x++) {
                        for (int y = 0; y < destBitmap.getHeight(); y++) {
                            destBitmap.setPixel(x, y, tempBitmap.getPixel(x, y));
                        }
                    }
                    return CloseableReference.cloneOrNull(bitmapRef);
                } finally {
                    CloseableReference.closeSafely(bitmapRef);
                }
            }
        };

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(redMeshPostprocessor)
                .build();

        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setImageRequest(request)
                .build();
//        mImageView.setController(controller);
        Ion.with(getContext())
                .load(item.url)
                .intoImageView(mImageView);
    }
}
