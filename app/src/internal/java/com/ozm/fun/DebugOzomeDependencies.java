package com.ozm.fun;

import com.ozm.fun.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeDependencies extends OzomeDependencies {
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
