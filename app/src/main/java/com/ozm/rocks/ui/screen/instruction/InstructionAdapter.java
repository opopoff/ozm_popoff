package com.ozm.rocks.ui.screen.instruction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.ozm.R;
import com.ozm.rocks.util.ViewPagerAdapter;

/**
 * Created by Danil on 11.06.2015.
 */
public class InstructionAdapter extends ViewPagerAdapter<Drawable> {

    public InstructionAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(Drawable item) {
        return R.layout.instruction_item;
    }

    @Override
    public void bindView(Drawable item, int position, View view) {
        ((ImageView) view.findViewById(R.id.instruction_item_image)).setImageDrawable(item);
    }
}
