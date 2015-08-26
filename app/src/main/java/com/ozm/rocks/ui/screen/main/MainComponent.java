package com.ozm.rocks.ui.screen.main;

import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.ui.screen.main.emotions.EmotionsView;
import com.ozm.rocks.ui.screen.main.general.GeneralView;
import com.ozm.rocks.ui.screen.main.personal.PersonalView;
import com.ozm.rocks.ui.screen.settings.SettingsView;

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
