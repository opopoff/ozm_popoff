package com.ozm.rocks.ui.main.emotions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.Promo;
import com.ozm.rocks.ui.misc.RecyclerBindableAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmotionsAdapter extends RecyclerBindableAdapter<Category, EmotionsAdapter.ViewHolder> {

    private final Picasso mPicassso;
    private ActionListener actionListener;

    public EmotionsAdapter(Context context,
                           RecyclerView.LayoutManager manager,
                           Picasso picasso,
                           @NonNull ActionListener actionListener) {
        super(context, manager);
        this.actionListener = actionListener;
        this.mPicassso = picasso;
    }

    public void addAll(List<Category> categories, List<Promo> promos) {
        addAll(categories);
        loadingImagesPreview();
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getItemCount(); i++) {
            mPicassso.load(getItem(i).backgroundImage).fetch();
        }
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), mPicassso, position, actionListener);
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.main_emotions_item_view;
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface ActionListener {
        void openGoldCategory(Category item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(Category item, Picasso picasso, int position, ActionListener callback) {
            ((EmotionsItemView) itemView).bindView(item, position, picasso, callback);
        }
    }
}
