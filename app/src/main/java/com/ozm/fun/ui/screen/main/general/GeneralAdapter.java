package com.ozm.fun.ui.screen.main.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.R;
import com.ozm.fun.data.api.request.DislikeRequest;
import com.ozm.fun.data.api.request.LikeRequest;
import com.ozm.fun.data.api.response.ImageResponse;
import com.ozm.fun.ui.misc.ListBindableAdapter;
import com.ozm.fun.util.PInfo;
import com.ozm.fun.util.Strings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralAdapter extends ListBindableAdapter<ImageResponse> {

    public static final int FILTER_CLEAN_STATE = 0;
    private static final String FILTER_PREFIX = "prefix_";
    private static final String FILTER_SUFFIX = "_suffix";

    private ActionListener actionListener;
    private List<PInfo> messengers = Collections.emptyList();
    private List<PInfo> gifMessengers = Collections.emptyList();
    private Picasso picasso;

    private String filterText = Strings.EMPTY;

    private boolean isShowEmotion = true;

    private int maximumDecide;

    public GeneralAdapter(Context context, @NonNull ActionListener actionListener, Picasso picasso) {
        super(context);
        this.actionListener = actionListener;
        this.picasso = picasso;
    }

    public void updateAll(List<ImageResponse> list) {
        clear();
        addAll(list);
        setFilter();
        loadingImagesPreview();
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        setFilter();
        loadingImagesPreview();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.main_general_item;
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
        if (position == 6 && actionListener != null) {
            actionListener.onBoarding();
        }
        final GeneralItemView itemView = (GeneralItemView) view;
        itemView.bindTo(item, position, isShowEmotion, actionListener, messengers, gifMessengers, picasso);

        int decide = position / 10;
        if (decide > maximumDecide) {
            maximumDecide = decide;
            actionListener.newMaximumShowedDecide(maximumDecide * 10);
        }
    }

    @Override
    protected String itemToString(ImageResponse item) {
        return FILTER_PREFIX + item.categoryId + FILTER_SUFFIX;
    }

    public void setFilter(long categoryId) {
        final boolean useFilter = categoryId == FILTER_CLEAN_STATE;
        filterText = useFilter ? Strings.EMPTY : FILTER_PREFIX + categoryId + FILTER_SUFFIX;
        isShowEmotion = useFilter;
        setFilter();
    }

    private void setFilter() {
        getFilter().filter(filterText);
    }

    public void deleteChild(int position) {
        removeItemByPosition(position);
        notifyDataSetChanged();
    }

    public void deleteChild(ImageResponse image) {
        removeItem(image);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

//    public Subscription update(final LikeHideResult mLikeHideResult, EndlessObserver<Boolean> observer) {
//        final Map<String, Boolean> likes = mLikeHideResult.getLikeItems();
//        final List<String> hides = mLikeHideResult.getHideItems();
//        return Observable.from(getList()).doOnEach(new EndlessObserver<ImageResponse>() {
//            @Override
//            public void onNext(ImageResponse imageResponse) {
//                if (likes.containsKey(imageResponse.url)) {
//                    imageResponse.liked = likes.get(imageResponse.url);
//                }
//                if (hides.contains(imageResponse.url)) {
//                    deleteChild(imageResponse);
//                }
//            }
//        })
//                .toList()
//                .flatMap(new Func1<List<ImageResponse>, Observable<Boolean>>() {
//                    @Override
//                    public Observable<Boolean> call(List<ImageResponse> imageResponses) {
//                        return Observable.just(true);
//                    }
//                }).onErrorReturn(new Func1<Throwable, Boolean>() {
//                    @Override
//                    public Boolean call(Throwable throwable) {
//                        return false;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//    }

    public void setMessengers(ArrayList<PInfo> pInfoMessengers, ArrayList<PInfo> pInfoGifMessengers) {
        gifMessengers = pInfoGifMessengers;
        messengers = pInfoMessengers;
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getList().size(); i++) {
            ImageResponse image = this.getItem(i);
            if (!image.isGIF) {
                picasso.load(image.url).fetch();
            }
        }
    }

    public interface ActionListener {
        void share(ImageResponse image, int position);

        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);

        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);

        void clickByCategory(long categoryId, String categoryName);

        void fastShare(PInfo pInfo, ImageResponse image);

        void onBoarding();

        void newMaximumShowedDecide(int decide);
    }
}
