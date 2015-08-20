package com.ozm.rocks.ui.screen.sharing;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.ui.misc.Misc;
import com.ozm.rocks.ui.misc.OnEndAnimationListener;
import com.ozm.rocks.util.AnimationTools;
import com.ozm.rocks.util.RoundImageTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 25.06.2015.
 */
public class SharingVkAdapter extends ListBindableAdapter<VKApiUser> {
    private Context context;
    private Picasso picasso;
    private ArrayList<Integer> sends = new ArrayList<>();
    private OnItemClick onItemClick = new OnItemClick() {
        @Override
        public boolean onItemClick(final View view, final int position) {
            final ImageView fg = (ImageView) view.findViewById(R.id.sharing_view_vk_item_image_send);
            if (sends.indexOf(position) == -1) {
                sends.add(position);
                AnimationTools.vkItemAnimation(fg, new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fg.setImageResource(R.drawable.vk_anim_circle);
                        ProgressBar progressBar = (ProgressBar) view.findViewById(
                                R.id.sharing_view_vk_item_image_progress);
                        progressBar.getIndeterminateDrawable().setColorFilter(view.getResources()
                                .getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                return true;
            }
            setForeground(fg, position);
            return false;
        }
    };

    protected SharingVkAdapter(Context context, Picasso picasso) {
        super(context);
        this.context = context;
        this.picasso = picasso;
    }

    @Override
    public void clear() {
        sends.clear();
        super.clear();
    }

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.sharing_view_vk_item;
    }

    @Override
    public void bindView(final VKApiUser item, final int position, final View view) {
        final ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.sharing_view_vk_item_image_progress);
        if (item != null) {
            if (position == 0) {
                view.setPadding(view.getResources()
                        .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0, 0, 0);
            } else {
                view.setPadding(0, 0, 0, 0);
            }
            final ImageView fg = (ImageView) view.findViewById(R.id.sharing_view_vk_item_image_send);
            setForeground(fg, position);
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text)).setText(item.first_name);
            picasso.load(item.photo_100).noFade().transform(new RoundImageTransform())
                    .into(imageView, null);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight() + view.getResources()
                    .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0);
            view.findViewById(R.id.sharing_view_vk_item_image_send).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text))
                    .setText(view.getResources().getString(R.string.sharing_view_all_friends));
            imageView.setImageResource(R.drawable.ic_vk_friends);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setForeground(ImageView fg, int position) {
        if (sends.indexOf(position) == -1) {
            fg.setImageDrawable(null);
            fg.setBackgroundResource(0);
        } else {
            fg.setBackgroundResource(R.drawable.vk_anim_circle);
            Drawable drawable = Misc.getDrawable(fg.getResources(), R.drawable.ic_done);
            if (drawable != null) {
                drawable.setColorFilter(context.getResources().getColor(
                        R.color.sharing_view_icon_color), PorterDuff.Mode.SRC_ATOP);
            }
            fg.setImageDrawable(drawable);
        }
    }

    @Override
    public void addAll(List<? extends VKApiUser> items) {
        if (items.size() != 0 && items.get(items.size() - 1) != null) {
            items.add(null);
        }
        super.clear();
        super.addAll(items);
    }

    public interface OnItemClick {
        boolean onItemClick(View view, int position);
    }
}
