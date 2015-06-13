package com.ozm.rocks;

import com.ozm.rocks.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeDependencies extends OzomeDependencies {
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
