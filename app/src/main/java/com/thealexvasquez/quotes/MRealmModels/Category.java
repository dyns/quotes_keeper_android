package com.thealexvasquez.quotes.MRealmModels;

import io.realm.RealmObject;

/**
 * Created by LACAJITA on 6/2/15
 */

public class Category extends RealmObject {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String name;
    private String displayName;
    private String description;

}
