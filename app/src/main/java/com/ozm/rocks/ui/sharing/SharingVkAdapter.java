package com.ozm.rocks.ui.sharing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.RoundImageTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;

import java.util.List;

/**
 * Created by Danil on 25.06.2015.
 */
public class SharingVkAdapter extends ListBindableAdapter<VKApiUser> {
    private Picasso picasso;
    private Callback callback;

    protected SharingVkAdapter(Context context, Picasso picasso, Callback callback) {
        super(context);
        this.picasso = picasso;
        this.callback = callback;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.sharing_view_vk_item;
    }

    @Override
    public void bindView(final VKApiUser item, int position, View view) {
        if (item != null) {
            if (position == 0) {
                view.setPadding(view.getResources()
                        .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0, 0, 0);
            } else {
                view.setPadding(0, 0, 0, 0);
            }
            ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text)).setText(item.first_name);
            picasso.load(item.photo_100).noFade().transform(new RoundImageTransform())
                    .into(imageView, null);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.shareVk(item);
                    }
                }
            });
        } else {
            view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight() + view.getResources()
                    .getDimensionPixelOffset(R.dimen.sharing_view_vk_right_left_margin), 0);
            ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
            ((TextView) view.findViewById(R.id.sharing_view_vk_item_text)).setText("Все друзья");
            imageView.setImageResource(R.drawable.ic_vk_friends);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.shareVkAll();
                    }
                }
            });
        }
    }

    public interface Callback {
        void shareVk(VKApiUser user);

        void shareVkAll();
    }

    @Override
    public void addAll(List<? extends VKApiUser> items) {
        items.add(null);
        super.addAll(items);
    }
}
