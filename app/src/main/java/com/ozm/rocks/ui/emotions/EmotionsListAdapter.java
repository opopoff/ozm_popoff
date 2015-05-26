package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.Promo;
import com.ozm.rocks.ui.misc.BindableAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmotionsListAdapter extends BindableAdapter<Category> {
    private List<Category> list = Collections.emptyList();
    private ActionListener actionListener;

    public EmotionsListAdapter(Context context, @NonNull ActionListener actionListener) {
        super(context);
        this.actionListener = actionListener;
    }

    public void addAll(List<Category> categories, List<Promo> promos) {
        //TODO probably need move to background
        List<Category> emotionsList = new ArrayList<>(categories);
//        ArrayList<Category> threeItem = new ArrayList<>(3);
//        for (int i = 0; i < categories.size(); i++) {
//            if (threeItem.size() < 3) {
//                threeItem.add(categories.get(i));
//            } else {
//                emotionsList.add(EmotionsListItem.fromCategories(threeItem));
//                threeItem = new ArrayList<>(3);
//                threeItem.add(categories.get(i));
//            }
//            if (i == categories.size() - 1 && threeItem.size() > 0) {
//                emotionsList.add(EmotionsListItem.fromCategories(threeItem));
//            }
//        }

//        for (Promo promo : promos) {
//            emotionsList.add(promo.line, promo.categories.get(0));
//        }

        this.list = emotionsList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Category getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.simple_emotion_item_view, container, false);
    }

    @Override
    public void bindView(Category item, int position, View view) {
        ((SimpleEmotionItemView) view).bindTo(item);
    }

    public interface ActionListener {
//        void share(ImageResponse image, int position);
//
//        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);
//
//        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);
//
//        void hide(int itemPosition, HideRequest hideRequest);

        void openGoldCategory(long categoryId, String categoryName);
    }

    public static class EmotionsListItem {

        private final boolean isPromo;
        private final List<Category> categories;
        private final String description;

        private EmotionsListItem(boolean isPromo, List<Category> categories, String description) {
            this.isPromo = isPromo;
            this.categories = categories;
            this.description = description;
        }

        public static EmotionsListItem fromCategories(List<Category> list) {
            return new EmotionsListItem(false, list, null);
        }

        public static EmotionsListItem fromPromo(Promo promo) {
            return new EmotionsListItem(true, promo.categories, promo.description);

        }

        public boolean isPromo() {
            return isPromo;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public String getDescription() {
            return description;
        }
    }
}
