package com.ozm.rocks.ui.main;

import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.ui.emotions.MainEmotionsView;
import com.ozm.rocks.ui.general.MainGeneralView;
import com.ozm.rocks.ui.personal.PersonalView;
import com.ozm.rocks.ui.settings.SettingsView;

import dagger.Component;

@MainScope
@Component(dependencies = OzomeComponent.class, modules = MainModule.class)
public interface MainComponent extends MainDepencencies{
    void inject(MainActivity activity);

    void inject(MainView view);

    void inject(MainEmotionsView view);

    void inject(MainGeneralView view);

    void inject(PersonalView view);

    void inject(SettingsView view);
}
