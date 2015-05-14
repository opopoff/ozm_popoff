package com.ozm.rocks.ui.main;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.DimenTools;

public class MyCollectionAdapter extends ListBindableAdapter<MyCollectionModel> {
    private final Point mDisplaySize;

    public MyCollectionAdapter(Context context) {
        super(context);
        mDisplaySize = DimenTools.displaySize(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.my_collection_grid_item;
    }

    @Override
    public void bindView(MyCollectionModel item, int position, View view) {
        float ratio = mDisplaySize.x / 2f / (float) item.getWidth();
        ((ImageView) view.findViewById(R.id.my_collection_grid_view_item)).setImageDrawable(item.getImage());
        view.findViewById(R.id.my_collection_grid_view_item).getLayoutParams().height
                = (int) (item.getHeight() * ratio);
    }
}
