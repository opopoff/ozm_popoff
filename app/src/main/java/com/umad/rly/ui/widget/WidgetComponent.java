package com.umad.rly.ui.widget;

import com.umad.rly.OzomeComponent;

import dagger.Component;

@WidgetScope
@Component(
        dependencies = OzomeComponent.class,
        modules = WidgetModule.class
)
public interface WidgetComponent {
    void inject(WidgetBootService service);
}
