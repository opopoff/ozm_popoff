package com.umad.wat.ui.screen.instruction;

import com.umad.wat.OzomeComponent;

import dagger.Component;

@InstructionScope
@Component(dependencies = OzomeComponent.class)
public interface InstructionComponent {
    void inject(InstructionActivity activity);

    void inject(InstructionView view);
}
