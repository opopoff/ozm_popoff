package com.umad.wat;

import dagger.Component;

import com.umad.wat.data.ReleaseDataModule;
import com.umad.wat.ui.ReleaseUiModule;

/**
 * The core release component for u2020 applications
 */
@ApplicationScope
@Component(modules = {OzomeModule.class, ReleaseUiModule.class, ReleaseDataModule.class})
public interface OzomeComponent extends OzomeDependencies {
    /**
     * An initializer that creates the graph from an application.
     */
    static final class Initializer {
        static OzomeComponent init(OzomeApplication app) {
            return DaggerOzomeComponent.builder()
                    .ozomeModule(new OzomeModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }
}
