package com.ozm.fun.ui.widget;

import com.ozm.fun.OzomeComponent;

import dagger.Component;

@WidgetScope
@Component(
        dependencies = OzomeComponent.class,
        modules = WidgetModule.class
)
public interface WidgetComponent {
    void inject(WidgetBootService service);
}
