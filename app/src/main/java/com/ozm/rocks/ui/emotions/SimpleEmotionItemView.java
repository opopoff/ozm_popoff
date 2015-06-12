package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.util.FadeImageLoading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SimpleEmotionItemView extends LinearLayout {

    @InjectView(R.id.simple_emotion_name)
    TextView mCategoryName;
    @InjectView(R.id.simple_emotion_image)
    ImageView mCategoryImage;
    @InjectView(R.id.simple_emotion_promo_label)
    ImageView mPromoLabel;
    @InjectView(R.id.progress)
    ProgressBar mProgress;
    @InjectView(R.id.simple_emotion_image_frame)
    FrameLayout mImageFrame;

    public SimpleEmotionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final Category category, Picasso picasso) {
        mCategoryName.setText(String.valueOf(category.description));
        mProgress.setVisibility(VISIBLE);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.rounded_shape_foreground, getContext().getTheme());
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(R.color.content_background), PorterDuff.Mode.SRC_ATOP);
        }
        mImageFrame.setForeground(drawable);
        picasso.load(category.backgroundImage).
//                        transform(new RoundImageTransform()).
        noFade().into(mCategoryImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgress.setVisibility(GONE);
                        FadeImageLoading.animate(mCategoryImage);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
        mPromoLabel.setVisibility(category.isPromo ? VISIBLE : GONE);
    }

}
