package com.ozm.rocks.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainGeneralView extends LinearLayout {
    private final GeneralListAdapter listAdapter;
    @Inject
    MainActivity.Presenter presenter;

    @Inject
    KeyboardPresenter keyboardPresenter;

    public MainGeneralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        listAdapter = new GeneralListAdapter(context);
    }

    //    @InjectView(R.id.groupon_toolbar)
//    OzomeToolbar toolbar;
//    @InjectView(R.id.main_login_input_email)
//    MaterialEditText emailView;
//    @InjectView(R.id.main_login_input_password)
//    MaterialEditText passwordView;
    @InjectView(R.id.general_list_view)
    ListView generalListView;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        ArrayList<PInfo> packages = presenter.getPackages();
//        toolbar.setTitleVisibility(false);
//        toolbar.setLogoVisibility(true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        generalListView.setAdapter(listAdapter);
        presenter.loadGeneralFeed(new EndlessObserver<List<ImageResponse>>() {
            @Override
            public void onNext(List<ImageResponse> imageList) {
                listAdapter.updateAll(imageList);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public GeneralListAdapter getListAdapter() {
        return listAdapter;
    }

//    @OnTextChanged(value = R.id.main_login_input_email, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    public void onEmailInputChanged(CharSequence text) {
//        emailView.validate();
//    }

//    @OnClick({R.id.main_login_continue_button, R.id.main_login_forgot_password_button})
//    public void onButtonClick(View view) {
//        final int id = view.getId();
//        if (id == R.id.main_login_continue_button) {
//            if (!isInputsValid())
//                return;
//            presenter.signIn(emailView.getText().toString(), passwordView.getText().toString());
//        } else if (id == R.id.main_login_forgot_password_button) {
//            presenter.forgotPassword();
//        }
//    }

//    public boolean isInputsValid() {
//        final boolean emailValid = emailView.validate();
//        final boolean passwordValid = passwordView.validate();
//        return emailValid && passwordValid;
//    }
}
