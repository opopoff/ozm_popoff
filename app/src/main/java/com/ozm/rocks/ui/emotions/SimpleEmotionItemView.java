package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.util.RoundImageTransform;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SimpleEmotionItemView extends LinearLayout {

    @InjectView(R.id.simple_emotion_name)
    TextView mCategoryName;
    @InjectView(R.id.simple_emotion_image)
    ImageView mCategoryImage;
    @InjectView(R.id.simple_emotion_promo_label)
    ImageView mPromoLabel;

    public SimpleEmotionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final Category category) {

        mCategoryName.setText(String.valueOf(category.description));
        Ion.with(getContext()).load(category.backgroundImage).withBitmap().transform(new RoundImageTransform())
                .intoImageView
                (mCategoryImage);
        mPromoLabel.setVisibility(category.isPromo ? VISIBLE : GONE);
    }

}
