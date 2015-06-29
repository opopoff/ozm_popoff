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

public class EmotionsHeaderView extends FrameLayout {

    @InjectView(R.id.main_emotions_header_image)
    protected ImageView imageView;

    @InjectView(R.id.main_emotions_header_name)
    protected TextView textView;

    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    public EmotionsHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindData(Category category, Picasso picasso) {
        textView.setText(String.valueOf(category.description).toUpperCase());
        progressBar.setVisibility(VISIBLE);
        picasso.load(category.backgroundImage).
        noFade().into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        FadeImageLoading.animate(imageView);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }
}
