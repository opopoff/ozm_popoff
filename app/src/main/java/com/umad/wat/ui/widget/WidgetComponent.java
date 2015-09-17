package com.umad.wat.ui.widget;

import com.umad.wat.OzomeComponent;

import dagger.Component;

@WidgetScope
@Component(
        dependencies = OzomeComponent.class,
        modules = WidgetModule.class
)
public interface WidgetComponent {
    void inject(WidgetBootService service);
}
