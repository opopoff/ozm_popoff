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
public class ChooseDialogAdapter extends ListBindableAdapter<PInfo> {
    protected ChooseDialogAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.choose_dialog_item_view;
    }

    @Override
    public void bindView(PInfo item, int position, View view) {
        ((ImageView) view.findViewById(R.id.choose_dialog_grid_image)).setImageBitmap(item.getIcon());
        ((TextView) view.findViewById(R.id.choose_dialog_grid_text)).setText(item.getApplicationName());
    }
}
