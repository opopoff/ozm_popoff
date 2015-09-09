package com.umad.rly.ui.screen.sharing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozm.R;
import com.umad.rly.ui.misc.ListBindableAdapter;
import com.umad.rly.util.PInfo;

/**
 * Created by Danil on 15.05.2015.
 */
public class SharingViewAdapter extends ListBindableAdapter<PInfo> {
    protected SharingViewAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.sharing_view_item;
    }

    @Override
    public void bindView(PInfo item, int position, View view) {
        ((ImageView) view.findViewById(R.id.sharing_dialog_list_element_image)).setImageBitmap(item.getIcon());
        ((TextView) view.findViewById(R.id.sharing_dialog_list_element_text)).setText(item.getApplicationName());
    }
}
