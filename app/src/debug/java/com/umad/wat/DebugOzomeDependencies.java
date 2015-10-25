package com.umad.wat;

import com.umad.wat.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeDependencies extends OzomeDependencies {
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
