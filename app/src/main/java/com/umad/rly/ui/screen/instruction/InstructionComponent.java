package com.umad.rly.ui.screen.instruction;

import com.umad.rly.OzomeComponent;

import dagger.Component;

@InstructionScope
@Component(dependencies = OzomeComponent.class)
public interface InstructionComponent {
    void inject(InstructionActivity activity);

    void inject(InstructionView view);
}
