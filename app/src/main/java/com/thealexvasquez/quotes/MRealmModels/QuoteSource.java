package com.thealexvasquez.quotes.MRealmModels;

import io.realm.RealmObject;

/**
 * Created by LACAJITA on 5/29/15
 */

public class QuoteSource extends RealmObject {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    private String name;
    private String displayName;
    private String description;
    private String authorName;

}
