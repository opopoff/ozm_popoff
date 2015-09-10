package com.umad.rly;

import com.umad.rly.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeDependencies extends OzomeDependencies {
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
