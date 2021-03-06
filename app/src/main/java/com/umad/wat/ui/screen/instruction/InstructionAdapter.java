package com.umad.wat.ui.screen.instruction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.umad.R;
import com.umad.wat.data.rx.RequestFunction;
import com.umad.wat.ui.misc.Misc;
import com.umad.wat.util.DimenTools;
import com.umad.wat.util.ViewPagerAdapter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Danil on 11.06.2015.
 */
public class InstructionAdapter extends ViewPagerAdapter<InstructionAdapter.InstructionAdapterItem> {

    private Context context;

    public InstructionAdapter(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getItemLayoutId(InstructionAdapter.InstructionAdapterItem item) {
        return R.layout.instruction_item;
    }

    @Override
    public void bindView(final InstructionAdapter.InstructionAdapterItem item, int position, View view) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.instruction_item_image);
        Observable.create(new RequestFunction<Bitmap>() {
            @Override
            protected Bitmap request() {
                final Resources resources = context.getResources();
                final Point point = DimenTools.displaySize(getContext());
                int screenWidth = point.x;
                int screenHeight = point.y;
//                return Misc.decodeSampledBitmapFromResourceByMinSide(
                return Misc.decodeSampledBitmapFromResource(
                        resources, item.getDrawableId(), (int) (screenWidth * 0.8f), (int) (screenHeight * 0.8f));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
    }

    public static class InstructionAdapterItem {

        private final int drawableId;

        public InstructionAdapterItem(int drawableId) {
            this.drawableId = drawableId;
        }

        public int getDrawableId() {
            return drawableId;
        }
    }
}
