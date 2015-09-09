package com.umad.rly;

import com.umad.rly.data.DebugDataModule;
import com.umad.rly.ui.DebugUiModule;

import dagger.Component;

/**
 * The core debug component for u2020 applications
 */
@ApplicationScope
@Component(modules = {OzomeModule.class, DebugUiModule.class, DebugDataModule.class})
public interface OzomeComponent extends DebugOzomeDependencies {
    /**
     * An initializer that creates the graph from an application.
     */
    final class Initializer {
        static OzomeComponent init(OzomeApplication app) {
            return DaggerOzomeComponent.builder()
                    .ozomeModule(new OzomeModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }
}
