package com.ozm.rocks;

import com.ozm.rocks.data.DebugDataModule;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.DebugUiModule;

import dagger.Component;

/**
 * The core debug component for u2020 applications
 */
@ApplicationScope
@Component(modules = {OzomeModule.class, DebugUiModule.class, DebugDataModule.class})
public interface OzomeComponent extends DebugOzomeGraph {
    /**
     * An initializer that creates the graph from an application.
     */
    final class Initializer {
        static OzomeComponent init(OzomeDependency app) {
            return DaggerOzomeComponent.builder()
                    .ozomeModule(new OzomeModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }
}
