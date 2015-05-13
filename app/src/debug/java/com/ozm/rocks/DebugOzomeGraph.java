package com.ozm.rocks;

import com.ozm.rocks.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeGraph extends OzomeGraph {
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
