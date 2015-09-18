package com.umad.wat.ui.screen.main.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umad.R;
import com.umad.wat.data.api.response.Category;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.util.FadeImageLoading;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsItemView extends FrameLayout {

    @InjectView(R.id.simple_emotion_name)
    protected TextView mCategoryName;
    @InjectView(R.id.simple_emotion_image)
    protected ImageView mCategoryImage;
    @InjectView(R.id.progress)
    protected ProgressBar mProgress;
    @InjectView(R.id.simple_emotion_new)
    protected TextView newText;

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
                         final OzomeImageLoader ozomeImageLoader,
                         final EmotionsAdapter.ActionListener callback) {
        mCategoryName.setText(String.valueOf(category.description));
        mProgress.setVisibility(VISIBLE);
        newText.setVisibility(category.isNew ? VISIBLE : GONE);
        ozomeImageLoader.load(OzomeImageLoader.IMAGE, category.backgroundImage, mCategoryImage,
                new OzomeImageLoader.Listener() {
                    @Override
                    public void onSuccess() {
                        mProgress.setVisibility(GONE);
                        FadeImageLoading.animate(mCategoryImage);
                    }

                    @Override
                    public void onError() {
                        // nothing;
                    }
                });
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
