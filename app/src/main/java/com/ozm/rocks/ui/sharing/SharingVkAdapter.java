package com.ozm.rocks.ui.sharing;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.ui.misc.Misc;
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
        public boolean onItemClick(View view, int position) {
            if (sends.indexOf(position) == -1) {
                final ImageView fg = (ImageView) view.findViewById(R.id.sharing_view_vk_item_image_send);
                sends.add(position);
                setForeground(fg, position);
                return true;
            }
            return false;
        }
    };

    protected SharingVkAdapter(Context context, Picasso picasso) {
        super(context);
        this.context = context;
        this.picasso = picasso;
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
        if (item != null) {
            if (position == 0) {
                view.setPadding(view.getResources()
                        .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0, 0, 0);
            } else {
                view.setPadding(0, 0, 0, 0);
            }
            final ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
            final ImageView fg = (ImageView) view.findViewById(R.id.sharing_view_vk_item_image_send);
            setForeground(fg, position);
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text)).setText(item.first_name);
            picasso.load(item.photo_100).noFade().transform(new RoundImageTransform())
                    .into(imageView, null);
        } else {
            view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight() + view.getResources()
                    .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0);
            ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
            view.findViewById(R.id.sharing_view_vk_item_image_send).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text))
                    .setText(view.getResources().getString(R.string.sharing_view_all_friends));
            imageView.setImageResource(R.drawable.ic_vk_friends);
        }
    }

    private void setForeground(ImageView fg, int position) {
        if (sends.indexOf(position) == -1) {
            fg.setImageDrawable(null);
            fg.setBackgroundResource(0);
        } else {
            fg.setBackgroundColor(fg.getResources().getColor(R.color.sharing_view_vk_image_send_bg));
            Drawable drawable = Misc.getDrawable(R.drawable.ic_done, fg.getResources());
            if (drawable != null) {
                drawable.setColorFilter(context.getResources().getColor(
                        R.color.sharing_view_icon_color), PorterDuff.Mode.SRC_ATOP);
            }
            fg.setImageDrawable(drawable);
        }
    }

    @Override
    public void addAll(List<? extends VKApiUser> items) {
        items.add(null);
        super.addAll(items);
    }

    public interface OnItemClick {
        boolean onItemClick(View view, int position);
    }
}
