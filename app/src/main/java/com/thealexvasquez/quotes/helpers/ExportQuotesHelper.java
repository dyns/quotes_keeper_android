package com.thealexvasquez.quotes.helpers;

import android.content.Context;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.MRealmModels.QuoteSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/28/15
 */

public class ExportQuotesHelper {

    public static JSONArray exportCategoriesAsJSONArray(Context context){
        Realm realm = Realm.getInstance(context);

        RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();

        JSONArray categoriesJsonArray = new JSONArray();
        for(Category category : categoryRealmResults){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("displayName", category.getDisplayName());
                jsonObject.put("name", category.getName());
                categoriesJsonArray.put(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return categoriesJsonArray;
    }

    public static JSONArray exportSourcesAsJSONArray(Context context){
        Realm realm = Realm.getInstance(context);

        RealmResults<QuoteSource> quoteSourceRealmResults = realm.where(QuoteSource.class).findAll();

        JSONArray quoteSourcesJsonArray = new JSONArray();
        for(QuoteSource quoteSource : quoteSourceRealmResults){
            try {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("displayName", quoteSource.getDisplayName());
                jsonObject.put("name", quoteSource.getName());
                jsonObject.put("description", quoteSource.getDescription());
                jsonObject.put("authorName", quoteSource.getAuthorName());
                quoteSourcesJsonArray.put(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return quoteSourcesJsonArray;
    }

    public static JSONArray exportQuotesAsJSONArray(Context context){
        Realm realm = Realm.getInstance(context);

        RealmResults<Quote> quoteRealmResults = realm.where(Quote.class).findAll();

        JSONArray quotesJsonArray = new JSONArray();
        for(Quote quote : quoteRealmResults){
            try {
                JSONObject jsonObject = new JSONObject();

                String page = quote.getPage() != -1 ? String.valueOf(quote.getPage()) : "n.p.";
                jsonObject.put("page", page);


                jsonObject.put("body", quote.getBody());
                jsonObject.put("sourceName", quote.getSource().getName());


                JSONArray categories = new JSONArray();
                for(Category category : quote.getCategories()){
                    categories.put(category.getDisplayName());
                }
                jsonObject.put("Categories", categories);

                quotesJsonArray.put(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return quotesJsonArray;
    }

}
