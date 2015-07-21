package com.ozm.rocks.ui.screen.instruction;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@InstructionScope
@Component(dependencies = OzomeComponent.class)
public interface InstructionComponent {
    void inject(InstructionActivity activity);

    void inject(InstructionView view);
}
