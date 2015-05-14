package com.ozm.rocks.ui.main;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.tools.KeyboardPresenter;

import java.util.ArrayList;

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
    @InjectView(R.id.my_image_view)
    SimpleDraweeView imageView;
    @InjectView(R.id.general_list_view)
    ListView generalListView;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
//        ArrayList<PInfo> packages = presenter.getPackages();
//        toolbar.setTitleVisibility(false);
//        toolbar.setLogoVisibility(true);
//        emailView.addValidator(new EmailValidator(getResources()));
        // TODO remove debug login data
//        if (BuildConfig.DEBUG) {
//            emailView.setText("lzharova+99@groupon.com");
//            emailView.setSelection(emailView.getText().length());
//            passwordView.setText("123456");
//        }
//        keyboardPresenter.show(emailView);

        Uri uri = Uri.parse("http://frescolib.org/static/fresco-logo.png");
//        imageView.setImageURI(uri);
        uri = Uri.parse("https://media1.giphy.com/media/pwtpgXyiKBYLS/200.gif");
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        imageView.setController(controller);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("https://media1.giphy.com/media/pwtpgXyiKBYLS/200.gif");
        strings.add("https://s-media-cache-ak0." +
                "pinimg.com/originals/92/6f/40/926f4078d335a5d9b9592dbe9791a2df.jpg");
        strings.add("https://lh4.googleusercontent.com/-" +
                "Xn49uuNKY7s/UzzZWEMTGsI/AAAAAAAATcE/Ik7rcShBW" +
                "0Q/w800-h800/tumblr_mipzbzlB911qfjvexo2_r1_500.gif");
        strings.add("http://1.bp.blogspot.com/-580zc2Ezj3c/VO88rehoec" +
                "I/AAAAAAAAA8Q/YEA-KF-bAhw/s1600/Gif-1.gif");
        strings.add("https://33.media.tumblr.com/50973210f3d538645976979084" +
                "a6681d/tumblr_mt9r1kx0iq1si6qheo1_400.gif");
        strings.add("https://lh5.googleusercontent.com/-6yuqvrqLYNE/VGG-q3ggs8I" +
                "/AAAAAAAAI00/2vzJY--euxI/w800-h800/dancetoon.gif");
        strings.add("http://cdn.rsvlts.com/wp-content/uploads/2014/0" +
                "1/Emily-Ratajkowski-GQ-GIF-02.gif");
        strings.add("http://www.medem.kiev.ua/files/images/mult06.gif");
        strings.add("http://i.imgur.com/zF5N4hs.gif");
        strings.add("http://cdn.smosh.com/sites/default/files" +
                "/bloguploads/mario-gif-trippy.gif");
        strings.add("https://s-media-cache-ak0.pinimg.com/originals/92/6f/40/926f4078d335a5d9b9592dbe9791a2df.jpg");
        strings.add("https://lh4.googleusercontent.com/-Xn49uuNKY7s/UzzZWEMTGsI/AAAAAAAATcE/Ik7rcShBW0Q" +
                "/w800-h800/tumblr_mipzbzlB911qfjvexo2_r1_500.gif");
        strings.add("http://1.bp.blogspot.com/-580zc2Ezj3c/VO88rehoecI/AAAAAAAAA8Q/YEA-KF-bAhw/s1600/Gif-1.gif");
        strings.add("https://33.media.tumblr.com/50973210f3d538645976979084a6681d/tumblr_mt9r1kx0iq1si6qheo1_400.gif");
        strings.add("https://lh5.googleusercontent.com/-6yuqvrqLYNE/VGG-q3ggs8I/AAAAAAAAI00" +
                "/2vzJY--euxI/w800-h800/dancetoon.gif");
        strings.add("http://cdn.rsvlts.com/wp-content/uploads/2014/01/Emily-Ratajkowski-GQ-GIF-02.gif");
        strings.add("http://www.medem.kiev.ua/files/images/mult06.gif");
        strings.add("http://i.imgur.com/zF5N4hs.gif");
        strings.add("http://cdn.smosh.com/sites/default/files/bloguploads/mario-gif-trippy.gif");

        generalListView.setAdapter(listAdapter);
        listAdapter.updateAll(strings);
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
