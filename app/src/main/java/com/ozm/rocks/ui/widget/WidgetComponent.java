package com.ozm.rocks.ui.widget;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@WidgetScope
@Component(
        dependencies = OzomeComponent.class,
        modules = WidgetModule.class
)
public interface WidgetComponent {
    void inject(WidgetBootService service);
}
