package com.ozm.rocks.ui.categories;

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
import com.ozm.rocks.ui.misc.BindableAdapter;
import com.ozm.rocks.util.PInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryListAdapter extends BindableAdapter<ImageResponse> {
    private List<ImageResponse> list = Collections.emptyList();
    private List<PInfo> messengers = Collections.emptyList();
    private List<PInfo> gifMessengers = Collections.emptyList();
    private ActionListener actionListener;
    private Picasso picasso;

    public CategoryListAdapter(Context context, @NonNull ActionListener actionListener, Picasso picasso) {
        super(context);
        this.actionListener = actionListener;
        this.picasso = picasso;
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
        return inflater.inflate(R.layout.category_list_item_view, container, false);
    }

    @Override
    public void bindView(ImageResponse item, int position, View view) {
        ((CategoryListItemView) view).bindTo(item, position, actionListener, gifMessengers, messengers,
                picasso);
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

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setMessengers(ArrayList<PInfo> pInfoMessengers, ArrayList<PInfo> pInfoGifMessengers) {
        gifMessengers = pInfoGifMessengers;
        messengers = pInfoMessengers;
    }

    public interface ActionListener {
        void share(ImageResponse image, int position);

        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);

        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);

        void hide(int itemPosition, HideRequest hideRequest, ImageResponse image);

        void fastShare(PInfo pInfo, ImageResponse image);
    }
}
