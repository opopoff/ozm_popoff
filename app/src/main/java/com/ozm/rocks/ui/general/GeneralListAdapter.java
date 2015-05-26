package com.ozm.rocks.ui.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ozm.R;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.misc.BindableAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GeneralListAdapter extends BindableAdapter<ImageResponse> {
    private List<ImageResponse> list = Collections.emptyList();
    private ActionListener actionListener;

    public GeneralListAdapter(Context context, @NonNull ActionListener actionListener) {
        super(context);
        this.actionListener = actionListener;
    }

    public void updateAll(List<ImageResponse> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addAll(List<ImageResponse> list) {
        List<ImageResponse> newList = new ArrayList<>(this.list);
        newList.addAll(list);
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ImageResponse getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.main_general_list_item_view, container, false);
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
        ((GeneralListItemView) view).bindTo(item, position, actionListener);
    }

    public void updateLikedItem(int positionInList, boolean b) {
        getItem(positionInList).liked = b;
        Toast.makeText(getContext(),
                getContext().getString(b ? R.string.main_feed_like_format_string : R.string
                                .main_feed_dislike_format_string,
                        getItem(positionInList).categoryDescription), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    public void deleteChild(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void deleteChild(ImageResponse image) {

        int order = list.indexOf(image);
        list.remove(order);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public Subscription update(final LikeHideResult mLikeHideResult, EndlessObserver<Boolean> observer) {
        final Map<String, Boolean> likes = mLikeHideResult.getLikeItems();
        final List<String> hides = mLikeHideResult.getHideItems();
        return Observable.from(list).doOnEach(new EndlessObserver<ImageResponse>() {
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


    public interface ActionListener {
        void share(ImageResponse image, int position);

        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);

        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);

        void hide(int itemPosition, HideRequest hideRequest);

        void openCategory(long categoryId, String categoryName);
    }
}
