package com.thealexvasquez.quotes.MRealmModels;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by LACAJITA on 5/23/15
 */

public class Quote extends RealmObject {

    private String body;
    private String notes;
    private QuoteSource source;
    private String uniqueID;
    private int page;
    private RealmList<Category> categories;
    private boolean isUncategorized;

    public boolean isUncategorized() {
        return isUncategorized;
    }

    public void setIsUncategorized(boolean isUncategorized) {
        this.isUncategorized = isUncategorized;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public QuoteSource getSource() {
        return source;
    }

    public void setSource(QuoteSource source) {
        this.source = source;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public RealmList<Category> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
    }

}
