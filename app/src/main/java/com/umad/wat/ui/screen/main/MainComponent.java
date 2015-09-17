package com.umad.wat.ui.screen.main;

import com.umad.wat.OzomeComponent;
import com.umad.wat.ui.screen.main.emotions.EmotionsView;
import com.umad.wat.ui.screen.main.general.GeneralView;
import com.umad.wat.ui.screen.main.personal.PersonalView;
import com.umad.wat.ui.screen.settings.SettingsView;

import dagger.Component;

@MainScope
@Component(dependencies = OzomeComponent.class, modules = MainModule.class)
public interface MainComponent extends MainDepencencies{
    void inject(MainActivity activity);

    void inject(MainView view);

    void inject(EmotionsView view);

    void inject(PersonalView view);

    void inject(SettingsView view);

    void inject(GeneralView view);
}
