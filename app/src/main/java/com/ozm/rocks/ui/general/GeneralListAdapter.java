package com.ozm.rocks.ui.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GeneralListAdapter extends ListBindableAdapter<ImageResponse> {

    public static final int FILTER_CLEAN_STATE = 0;
    private static final String FILTER_PREFIX = "filter_";

    private ActionListener actionListener;
    private List<PInfo> messengers = Collections.emptyList();
    private List<PInfo> gifMessengers = Collections.emptyList();

    private String filterText = Strings.EMPTY;

    public GeneralListAdapter(Context context, @NonNull ActionListener actionListener) {
        super(context);
        this.actionListener = actionListener;
    }

    public void updateAll(List<ImageResponse> list) {
        clear();
        addAll(list);
        setFilter();
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        setFilter();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.main_general_list_item_view;
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
        ((GeneralListItemView) view).bindTo(item, position, actionListener, messengers, gifMessengers);
    }

    @Override
    protected String itemToString(ImageResponse item) {
        return FILTER_PREFIX + item.categoryId;
    }

    public void setFilter(long categoryId) {
        filterText = categoryId == FILTER_CLEAN_STATE ? Strings.EMPTY : FILTER_PREFIX + categoryId;
        setFilter();
    }

    private void setFilter() {
        getFilter().filter(filterText);
    }

//    public void updateLikedItem(int positionInList, boolean b) {
//        getItem(positionInList).liked = b;
//        Toast.makeText(getContext(),
//                getContext().getString(b ? R.string.main_feed_like_format_string : R.string
//                                .main_feed_dislike_format_string,
//                        getItem(positionInList).categoryDescription), Toast.LENGTH_SHORT).show();
//        notifyDataSetChanged();
//    }

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

    public Subscription update(final LikeHideResult mLikeHideResult, EndlessObserver<Boolean> observer) {
        final Map<String, Boolean> likes = mLikeHideResult.getLikeItems();
        final List<String> hides = mLikeHideResult.getHideItems();
        return Observable.from(getList()).doOnEach(new EndlessObserver<ImageResponse>() {
            @Override
            public void onNext(ImageResponse imageResponse) {
                if (likes.containsKey(imageResponse.url)) {
                    imageResponse.liked = likes.get(imageResponse.url);
                }
                if (hides.contains(imageResponse.url)) {
                    deleteChild(imageResponse);
                }
            }
        })
                .toList()
                .flatMap(new Func1<List<ImageResponse>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<ImageResponse> imageResponses) {
                        return Observable.just(true);
                    }
                }).onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void setMessengers(ArrayList<PInfo> pInfoMessengers, ArrayList<PInfo> pInfoGifMessengers) {
        gifMessengers = pInfoGifMessengers;
        messengers = pInfoMessengers;
    }

    public interface ActionListener {
        void share(ImageResponse image, int position);

        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);

        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);

        void hide(int itemPosition, HideRequest hideRequest);

        void openCategory(long categoryId, String categoryName);

        void fastShare(PInfo pInfo, ImageResponse image);
    }
}
