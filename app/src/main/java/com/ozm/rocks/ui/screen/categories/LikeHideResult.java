package com.ozm.rocks.ui.screen.categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry on 26/05/15.
 */
public class LikeHideResult {

    public static final int FULL = 1;
    public static final int EMPTY = 0;
    private List<String> hideItems = new ArrayList<>();
    private Map<String, Boolean> likeItems = new HashMap<>();

    public void hideItem(String url) {
        hideItems.add(url);
    }

    private void addLikeItem(String url, Boolean isLike) {
        likeItems.put(url, isLike);
    }

    public void likeItem(String url) {
        addLikeItem(url, true);
    }

    public void dislikeItem(String url) {
        addLikeItem(url, false);
    }

    public List<String> getHideItems() {
        return hideItems;
    }

    public Map<String, Boolean> getLikeItems() {
        return likeItems;
    }

    public void clearResult() {
        hideItems.clear();
        likeItems.clear();
    }

    public boolean isEmpty() {
        return (hideItems.isEmpty() && likeItems.isEmpty());
    }


}
