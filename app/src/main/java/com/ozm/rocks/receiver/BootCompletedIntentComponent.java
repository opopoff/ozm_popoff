package com.ozm.rocks.receiver;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@BootCompletedIntentScope
@Component(
        dependencies = OzomeComponent.class,
        modules = BootCompletedIntentModule.class
)
public interface BootCompletedIntentComponent {
    void inject(BootCompletedIntentService service);
}
