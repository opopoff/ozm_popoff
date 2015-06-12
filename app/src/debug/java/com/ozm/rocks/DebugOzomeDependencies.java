package com.ozm.rocks;

import com.ozm.rocks.data.api.OzomeApiQualifier;
import com.ozm.rocks.ui.debug.DebugView;

import retrofit.MockRestAdapter;

public interface DebugOzomeDependencies extends OzomeDependencies {
    @OzomeApiQualifier
    MockRestAdapter mockRestAdapter();

    void inject(DebugView view);
}
