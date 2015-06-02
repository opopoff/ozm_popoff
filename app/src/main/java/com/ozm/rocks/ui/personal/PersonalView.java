package com.ozm.rocks.ui.personal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class PersonalView extends FrameLayout implements BaseView {
    public static final long DURATION_DELETE_ANIMATION = 300;

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    PersonalPresenter myPresenter;
    @Inject
    LikeHideResult mLikeHideResult;


    @InjectView(R.id.my_collection_grid_view)
    StaggeredGridView staggeredGridView;

    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();
    private PersonalAdapter personalAdapter;

    public PersonalView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        personalAdapter = new PersonalAdapter(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        staggeredGridView.setAdapter(personalAdapter);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                myPresenter.setSharingDialogHide(new SharingService.SharingDialogHide() {
                    @Override
                    public void hide() {
                        ArrayList<Action> actions = new ArrayList<>();
                        actions.add(Action.getLikeDislikeHideActionForPersonal(personalAdapter.getItem(position).id,
                                Timestamp.getUTC()));
                        postHide(new HideRequest(actions), position);
                        mLikeHideResult.hideItem(personalAdapter.getItem(position).url);
                    }
                });
                myPresenter.shareWithDialog(personalAdapter.getItem(position));
            }
        });
    }

    private void postHide(HideRequest hideRequest, final int positionInList) {
        animateRemoval(positionInList);
        myPresenter.hide(hideRequest);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        myPresenter.takeView(this);
    }


    private void animateRemoval(int position) {
        View viewToRemove = staggeredGridView.getChildAt(position);
        int firstVisiblePosition = staggeredGridView.getFirstVisiblePosition();
        for (int i = 0; i < staggeredGridView.getChildCount(); ++i) {
            View child = staggeredGridView.getChildAt(i);
            if (child != viewToRemove) {
                int positionView = firstVisiblePosition + i;
                long itemId = personalAdapter.getItemId(positionView);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        personalAdapter.deleteChild(position);

        final ViewTreeObserver observer = staggeredGridView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = staggeredGridView.getFirstVisiblePosition();
                for (int i = 0; i < staggeredGridView.getChildCount(); ++i) {
                    final View child = staggeredGridView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = personalAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
                            if (firstAnimation) {
                                firstAnimation = true;
                            }
                        }
                    } else {
                        int childHeight = child.getHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
                        if (firstAnimation) {
                            firstAnimation = false;
                        }
                    }

                }
                mItemIdTopMap.clear();
                return true;
            }
        });

    }

    public void loadFeed() {
        presenter.loadMyCollection(
                new EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.d("Error");
                    }


                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        if (imageList.size() > 0) {
                            preloadImages(imageList);
                            findViewById(R.id.my_collection_empty_view).setVisibility(GONE);
                            personalAdapter.clear();
                            personalAdapter.addAll(imageList);
                            personalAdapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.my_collection_empty_view).setVisibility(VISIBLE);
                        }
                    }
                });
    }

    private void preloadImages(List<ImageResponse> imageList) {
        for (ImageResponse imageResponse : imageList) {
            Ion.with(getContext()).load(imageResponse.url).withBitmap().asBitmap();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        myPresenter.dropView(this);
        super.onDetachedFromWindow();
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
