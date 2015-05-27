package com.ozm.rocks.ui.sharing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.PInfo;

/**
 * Created by Danil on 15.05.2015.
 */
public class SharingDialogAdapter extends ListBindableAdapter<PInfo> {
    protected SharingDialogAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.sharing_dialog_list_element;
    }

    @Override
    public void bindView(PInfo item, int position, View view) {
        ((ImageView) view.findViewById(R.id.sharing_dialog_list_element_image)).setImageDrawable(item.getIcon());
        ((TextView) view.findViewById(R.id.sharing_dialog_list_element_text)).setText(item.getApplicationName());
    }
}
