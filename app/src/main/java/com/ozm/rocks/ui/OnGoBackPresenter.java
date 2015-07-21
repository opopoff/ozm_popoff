package com.ozm.rocks.ui;

import com.ozm.rocks.ApplicationScope;

import javax.inject.Inject;

/**
 * Created by Danil on 10.06.2015.
 */
@ApplicationScope
public class OnGoBackPresenter {
    public OnBackInterface getOnBackInterface() {
        return onBackInterface;
    }

    public void setOnBackInterface(OnBackInterface onBackInterface) {
        this.onBackInterface = onBackInterface;
    }

    private OnBackInterface onBackInterface;

    @Inject
    public OnGoBackPresenter() {
    }
}
