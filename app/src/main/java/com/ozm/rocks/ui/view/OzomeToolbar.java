package com.ozm.rocks.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ozm.R;
import com.ozm.rocks.util.Strings;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OzomeToolbar extends Toolbar {

    @InjectView(R.id.groupon_logo)
    ImageView grouponLogo;

    private final int iconColor;

    private String title;

    public OzomeToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        iconColor = getResources().getColor(R.color.icons);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // some default configuration
        ButterKnife.inject(this);
        setTitle(R.string.application_name);
    }

    public void setLogoVisibility(boolean visibility) {
        grouponLogo.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setTitleVisibility(boolean visibility) {
        if (visibility) {
            if (Strings.isBlank(title)) {
                setTitle(R.string.application_name);
            } else {
                setTitle(title);
            }
        } else {
            title = getTitle().toString();
            setTitle(null);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title = title == null ? null : title.toString();
    }

    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
        title = getResources().getString(resId);
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
        // tint menu icons with theme color
        for (int i = 0; i < getMenu().size(); i++) {
            MenuItem menuItem = getMenu().getItem(i);
            final Drawable icon = menuItem.getIcon();
            if (icon != null) {
                icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }
}
