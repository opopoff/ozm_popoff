package com.umad.wat.ui.screen.main;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.umad.R;
import com.umad.wat.base.ComponentFinder;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.ui.misc.CoordinatorPageAdapter;
import com.umad.wat.ui.misc.CoordinatorView;
import com.umad.wat.ui.view.OzomeToolbar;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainView extends FrameLayout implements BaseView {

    @InjectView(R.id.main_drawer_layout)
    protected DrawerLayout drawerLayout;
    @InjectView(R.id.coordinator_view)
    protected CoordinatorView coordinatorView;
    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;
    @Inject
    MainActivity.Presenter presenter;
    @Inject
    LocalyticsController localyticsController;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        toolbar.setTitleVisibility(true);
        toolbar.setTitle(R.string.main_screen_title);
        toolbar.setLogoVisibility(false);
        toolbar.setDrawerIconVisibility(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                // nothing;
            }

            @Override
            public void onDrawerOpened(View view) {
                localyticsController.openSettings();
            }

            @Override
            public void onDrawerClosed(View view) {
                // nothing;
            }

            @Override
            public void onDrawerStateChanged(int i) {
                // nothing;
            }
        });

        final List<CoordinatorPageAdapter.Item> pages = MainScreens.getList();
//        if (!BuildConfig.DEBUG) {
//            pages.remove(MainScreens.GENERAL_SCREEN);
//        }
        coordinatorView.addScreens(pages);
        coordinatorView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == .0f && positionOffsetPixels == 0) {
                    final MainScreens screen = (MainScreens) pages.get(position);
                    if (screen == MainScreens.EMOTIONS_SCREEN) {
                        localyticsController.openBest();
                    } /*else if (screen == MainScreens.FAVORITE_SCREEN) {
                        localyticsController.openHistory();
                    } else if (screen == MainScreens.GENERAL_SCREEN) {
                        localyticsController.openFeed();
                    }*/
                }
            }

            @Override
            public void onPageSelected(int position) {
                /*if (coordinatorView.getPageItem(position).getResId() == MainScreens.FAVORITE_SCREEN.getResId()) {
                    presenter.updateMyFeed();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void showLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showContent() {
    }

    @Override
    public void showError(Throwable throwable) {
    }

//    @OnClick(R.id.debug_pushwoosh_copy_pushtoken)
//    void onPushwooshCopyPushTokenButton() {
//        final Context context = getContext().getApplicationContext();
//        final String pushToken = PushManager.getPushToken(context);
//        Timber.d("PushToken: %s", pushToken);
//        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("label", pushToken);
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(context, "Copy PushToken to Buffer", Toast.LENGTH_SHORT).show();
//    }

    public void showMainContent() {
    }

    public void openFirstScreen() {
        showMainContent();
        coordinatorView.setCurrentPage(0);
    }

    public boolean onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        }
        if (coordinatorView.getCurrentPagePosition() != 0) {
            coordinatorView.setCurrentPage(0);
            return true;
        }
        return false;
    }
}
