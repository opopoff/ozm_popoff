package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.main.MainMenuItemView;
import com.ozm.rocks.ui.main.MainScreens;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainEmotionsView extends LinearLayout implements BaseView {
    @Inject
    MainActivity.Presenter mainPresenter;
    @Inject
    MainEmotionsPresenter emotionsPresenter;

    public MainEmotionsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

//    @InjectView(R.id.groupon_toolbar)
//    OzomeToolbar toolbar;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        emotionsPresenter.takeView(this);
//        toolbar.setTitleVisibility(false);
//        toolbar.setLogoVisibility(true);
//        toolbar.inflateMenu(R.menu.logout);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
//        {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem)
//            {
//                final int id = menuItem.getItemId();
//                if (id == R.id.menu_logout)
//                {
////                    showConfirmSignOutDialog();
//                    return true;
//                }
//                return false;
//            }
//        });
    }

//    void showConfirmSignOutDialog()
//    {
//        new MaterialDialog.Builder(getContext()).
//            title(R.string.confirm_logout_dialog_text).
//            positiveText(R.string.confirm_logout_dialog_positive_text).
//            negativeText(R.string.confirm_logout_dialog_negative_text).
//            backgroundColorRes(R.color.content_background).
//            contentColorRes(R.color.dialog_message_text_color).
//            negativeColorRes(R.color.dialog_negative_button_text_color).
//            positiveColorRes(R.color.dialog_positive_button_text_color).
//            callback(new MaterialDialog.ButtonCallback()
//            {
//                @Override
//                public void onPositive(MaterialDialog dialog)
//                {
//                    mainPresenter.signOut();
//                }
//            }).
//            build().show();
//    }

    @Override
    protected void onDetachedFromWindow() {
//        toolbar.setOnMenuItemClickListener(null);
        emotionsPresenter.dropView(this);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick({R.id.main_menu_coupons, R.id.main_menu_statistics, R.id.main_menu_support,
            R.id.main_menu_activation
    })
    void onMenuItemClick(MainMenuItemView menuItemView) {
        final int id = menuItemView.getId();
        if (id == R.id.main_menu_coupons) {
            mainPresenter.openScreen(MainScreens.GENERAL_SCREEN);
        } else if (id == R.id.main_menu_statistics) {
            mainPresenter.openScreen(MainScreens.EMOTIONS_SCREEN);
        }
//        else if (id == R.id.main_menu_support)
//        {
//            mainPresenter.openScreen(MainMenuScreen.FAVE_SCREEN);
//        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {

    }
}
