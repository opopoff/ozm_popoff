package com.ozm.rocks.ui.main;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainMyCollectionView extends FrameLayout {
    @Inject
    MainActivity.Presenter presenter;

    @InjectView(R.id.my_collection_grid_view) StaggeredGridView mStaggeredGridView;

    private MyCollectionAdapter mMyCollectionAdapter;
    ArrayList<MyCollectionModel> mMyCollectionModels;

    public MainMyCollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        mMyCollectionAdapter = new MyCollectionAdapter(context);
        mMyCollectionModels = new ArrayList<>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        mStaggeredGridView.setAdapter(mMyCollectionAdapter);
        MyCollectionModel myCollectionModel = new MyCollectionModel();
        myCollectionModel.setImage(getResources().getDrawable(R.drawable.test_grid_image_1));
        myCollectionModel.setWidth(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_1))
                .getBitmap().getWidth());
        myCollectionModel.setHeight(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_1))
                .getBitmap().getHeight());
        mMyCollectionModels.add(myCollectionModel);
        myCollectionModel = new MyCollectionModel();
        myCollectionModel.setImage(getResources().getDrawable(R.drawable.test_grid_image_2));
        myCollectionModel.setWidth(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_2))
                .getBitmap().getWidth());
        myCollectionModel.setHeight(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_2))
                .getBitmap().getHeight());
        mMyCollectionModels.add(myCollectionModel);
        myCollectionModel = new MyCollectionModel();
        myCollectionModel.setImage(getResources().getDrawable(R.drawable.test_grid_image_3));
        myCollectionModel.setWidth(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_3))
                .getBitmap().getWidth());
        myCollectionModel.setHeight(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_3))
                .getBitmap().getHeight());
        mMyCollectionModels.add(myCollectionModel);
        myCollectionModel = new MyCollectionModel();
        myCollectionModel.setImage(getResources().getDrawable(R.drawable.test_grid_image_4));
        myCollectionModel.setWidth(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_4))
                .getBitmap().getWidth());
        myCollectionModel.setHeight(((BitmapDrawable) getResources().getDrawable(R.drawable.test_grid_image_4))
                .getBitmap().getHeight());
        mMyCollectionModels.add(myCollectionModel);

        ArrayList<PInfo> packages = presenter.getPackages();
        for (PInfo pack : packages) {
            myCollectionModel = new MyCollectionModel();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) pack.getIcon();
            myCollectionModel.setImage(pack.getIcon());
            myCollectionModel.setWidth(bitmapDrawable.getBitmap().getWidth());
            myCollectionModel.setHeight(bitmapDrawable.getBitmap().getHeight());
            mMyCollectionModels.add(myCollectionModel);
        }
        mMyCollectionAdapter.addAll(mMyCollectionModels);
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }
}
