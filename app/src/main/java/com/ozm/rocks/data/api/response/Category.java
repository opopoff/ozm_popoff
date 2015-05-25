package com.ozm.rocks.data.api.response;

public final class Category {
    public final long id;
    public final String backgroundImage;
    public final String description;
    public final boolean isPinned;

    public Category(long id, String backgroundImage, String description, boolean isPinned) {
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.description = description;
        this.isPinned = isPinned;
    }
}
