package com.ozm.rocks.ui.gold.novel;

import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.ui.gold.GoldScope;

import javax.inject.Inject;
import javax.inject.Named;

import rx.subscriptions.CompositeSubscription;

@GoldScope
public class GoldNovelPresenter extends BasePresenter<GoldNovelView> {

    private final DataService dataService;
    private final LocalyticsController localyticsController;
    private final Category category;

    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public GoldNovelPresenter(DataService dataService,
                              LocalyticsController localyticsController,
                              @Named("category") Category category) {
        this.dataService = dataService;
        this.localyticsController = localyticsController;
        this.category = category;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

}
