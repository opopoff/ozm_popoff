package com.ozm.rocks.ui;

import com.ozm.rocks.ApplicationScope;

import javax.inject.Inject;

/**
 * Created by Danil on 10.06.2015.
 */
@ApplicationScope
public class OnGoBackPresenter {
    public OnGoBackInterface getOnGoBackInterface() {
        return onGoBackInterface;
    }

    public void setOnGoBackInterface(OnGoBackInterface onGoBackInterface) {
        this.onGoBackInterface = onGoBackInterface;
    }

    private OnGoBackInterface onGoBackInterface;

    @Inject
    public OnGoBackPresenter() {
    }
}
