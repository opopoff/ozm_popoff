package com.ozm.fun.ui.screen.sharing;

import com.ozm.fun.data.SharingService;
import com.ozm.fun.data.api.response.ImageResponse;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SharingModule {

    public static final String MP_IMAGE = "sharingImage";
    public static final String MP_FROM = "sharingFrom";

    private final ImageResponse imageResponse;
    private final int from;

    public SharingModule(ImageResponse imageResponse, @SharingService.From int from) {
        this.imageResponse = imageResponse;
        this.from = from;
    }

    @Provides
    @SharingScope
    @Named(MP_IMAGE)
    ImageResponse provideImage() {
        return imageResponse;
    }

    @Provides
    @SharingScope
    @Named(MP_FROM)
    int provideFrom() {
        return from;
    }

}
