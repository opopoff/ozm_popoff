package com.ozm.rocks.ui.main.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.util.FadeImageLoading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsItemView extends FrameLayout {

    @InjectView(R.id.simple_emotion_name)
    protected TextView mCategoryName;
    @InjectView(R.id.simple_emotion_image)
    protected ImageView mCategoryImage;
    @InjectView(R.id.simple_emotion_promo_label)
    protected ImageView mPromoLabel;
    @InjectView(R.id.progress)
    protected ProgressBar mProgress;

    public EmotionsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindView(final Category category,
                         final int position,
                         final Picasso picasso,
                         final EmotionsAdapter.ActionListener callback) {
        mCategoryName.setText(String.valueOf(category.description).toUpperCase());
        mProgress.setVisibility(VISIBLE);
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
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.openGoldCategory(category);
                }
            }
        });

    }
}
