package com.ozm.rocks.ui.main;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.util.UrlFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dmitry on 12/05/15.
 */
public class GeneralListItemView extends FrameLayout {

    @InjectView(R.id.image_view)
    SimpleDraweeView mImageView;

    public GeneralListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(ImageResponse image) {
        Uri uri = UrlFormat.getImageUri(image.url);
        if (image.isGIF) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true)
                    .build();
            mImageView.setController(controller);
        } else {
            mImageView.setImageURI(uri);
        }
    }
}
