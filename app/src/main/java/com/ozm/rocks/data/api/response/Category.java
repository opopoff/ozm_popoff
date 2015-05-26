package com.ozm.rocks.data.api.response;

public final class Category {
    public final long id;
    public final String backgroundImage;
    public final String description;
    public final boolean isPinned;
    public final boolean isPromo;

    public Category(long id, String backgroundImage, String description, boolean isPinned, boolean isPromo) {
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.description = description;
        this.isPinned = isPinned;
        this.isPromo = isPromo;
    }
}
