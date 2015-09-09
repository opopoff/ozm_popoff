package com.umad.rly.util;

import android.content.res.Resources;

import com.rengwuxian.materialedittext.validation.METValidator;

public abstract class EditTextValidator extends METValidator {
    public EditTextValidator(int errorMessageResId, Resources resources) {
        super(resources.getString(errorMessageResId));
    }
}
