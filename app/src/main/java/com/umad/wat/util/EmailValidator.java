package com.umad.wat.util;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.umad.R;


public class EmailValidator extends EditTextValidator {
    public EmailValidator(Resources resources) {
        super(R.string.wrong_email_error_text, resources);
    }

    public EmailValidator(int errorMessageResId, Resources resources) {
        super(errorMessageResId, resources);
    }

    @Override
    public boolean isValid(@NonNull CharSequence charSequence, boolean b) {
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }
}
