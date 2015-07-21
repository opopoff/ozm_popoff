package com.ozm.rocks.ui.screen.main.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.screen.gold.SpecialProjectTimerTextView;
import com.ozm.rocks.util.FadeImageLoading;
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
        images.add(imageView1);
        images.add(imageView2);
        images.add(imageView3);
    }

    public void bindData(Category category, Picasso picasso) {
        textView.setText(String.valueOf(category.description).toUpperCase());
        timerView.setPromoEnd(category.promoEnd);
        progressBar.setVisibility(VISIBLE);
//        picasso.load(category.backgroundImage).
//        noFade().into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        progressBar.setVisibility(View.GONE);
//                        FadeImageLoading.animate(imageView);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                }
//        );
    }

    public void bindData(List<ImageResponse> specialProjectImages, Picasso picasso) {
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
}
