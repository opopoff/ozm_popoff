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
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.ui.screen.gold.SpecialProjectTimerTextView;
import com.umad.wat.util.FadeImageLoading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsHeaderView extends FrameLayout {

    @InjectView(R.id.main_emotions_header_image_1)
    protected ImageView imageView1;
    @InjectView(R.id.main_emotions_header_image_2)
    protected ImageView imageView2;
    @InjectView(R.id.main_emotions_header_image_3)
    protected ImageView imageView3;

    @InjectView(R.id.main_emotions_header_name)
    protected TextView textView;

    @InjectView(R.id.main_emotions_header_time)
    protected SpecialProjectTimerTextView timerView;

    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    private List<ImageView> images = new ArrayList<>(3);


    public EmotionsHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindData(Category category) {
        textView.setText(String.valueOf(category.description).toUpperCase());
        timerView.setPromoEnd(category.promoEnd);
        progressBar.setVisibility(VISIBLE);
    }

    //for compatibility
    public void bindData(List<ImageResponse> specialProjectImages, Picasso picasso) {
        images.add(imageView1);
        images.add(imageView2);
        images.add(imageView3);
        for (int i = 0; i < images.size(); i++) {
            final ImageView imageView = images.get(i);
            if (i < specialProjectImages.size()) {
                picasso.load(specialProjectImages.get(i).url).
                        noFade().into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                FadeImageLoading.animate(imageView);
                            }

                            @Override
                            public void onError() {

                            }
                        }
                );
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    public void bindData(String url, OzomeImageLoader ozomeImageLoader) {
        imageView2.setVisibility(GONE);
        imageView3.setVisibility(GONE);

        ozomeImageLoader.load(OzomeImageLoader.IMAGE, url, imageView1, new OzomeImageLoader.Listener() {
            @Override
            public void onSuccess() {
                FadeImageLoading.animate(imageView1);
            }

            @Override
            public void onError() {

            }
        });
        progressBar.setVisibility(View.GONE);
    }
}
