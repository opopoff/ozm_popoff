package com.ozm.fun.ui.screen.instruction;

import com.ozm.fun.OzomeComponent;

import dagger.Component;

@InstructionScope
@Component(dependencies = OzomeComponent.class)
public interface InstructionComponent {
    void inject(InstructionActivity activity);

    void inject(InstructionView view);
}
