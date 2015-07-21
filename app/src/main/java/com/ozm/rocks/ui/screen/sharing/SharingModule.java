package com.ozm.rocks.ui.screen.sharing;

import com.ozm.rocks.data.api.response.ImageResponse;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SharingModule {
    private final ImageResponse imageResponse;
    private final int from;

    public SharingModule(ImageResponse imageResponse, @SharingService.From int from) {
        this.imageResponse = imageResponse;
        this.from = from;
    }

    @Provides
    @SharingScope
    @Named("sharingImage")
    ImageResponse provideImage() {
        return imageResponse;
    }

    @Provides
    @SharingScope
    @Named("sharingFrom")
    int provideFrom() {
        return from;
    }

}
