package com.thealexvasquez.quotes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.MRealmModels.QuoteSource;
import com.thealexvasquez.quotes.MUI.DeleteAlert;
import com.thealexvasquez.quotes.QuotesActivites.QuoteBaseActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 5/23/15
 */

public class QuoteActivity extends QuoteBaseActivity {

    /*
    start:
    overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);



    end:

    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
     */
    Quote quote;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        //opening transition animations
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Realm realm = Realm.getInstance(this);

        RealmResults<QuoteSource> realmResults = realm.where(QuoteSource.class).findAll();

        List<String> sourceNames = new ArrayList<>();

        for(QuoteSource quoteSource : realmResults){
            sourceNames.add(quoteSource.getName());
        }

        Intent intent = getIntent();

        quote = realm.where(Quote.class).endsWith("uniqueID",intent.getStringExtra("uniqueID")).findFirst();

        bodyEditText.setText(quote.getBody());
        sourceSpinner.setSelection(sourceNames.indexOf(quote.getSource().getName()));
        pageEditText.setText(getPageText(quote.getPage()));

        String someText = "";

        for(Category category : quote.getCategories()){

            String name = category.getDisplayName();
            selectedCategories.add(name);

            if(someText.equals("")){
                someText = name;
            } else {
                someText = someText + ", " + name;
            }
        }

        categoriesTextView.setText(someText);

        //////////////

//
//
    }

//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        //closing transition animations
//        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
//    }

    private String getPageText(int page){

        String pageText = String.valueOf(page);

        if(page == -1){
            pageText = "";
        }

        return pageText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_quote, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        String categories = "";

        for(Category category : quote.getCategories()){
            if(categories.isEmpty())
                categories = category.getDisplayName();
            else
                categories = categories + ", " + category.getDisplayName();
        }

        String intentContent =
                quote.getBody() + "\n" +
                        "From: " + quote.getSource().getName() + "\n" +
                        (quote.getPage() != -1 ? "Page " + String.valueOf(quote.getPage()) : "") +
                        (categories.isEmpty() ? "" : "\nCategories: " + categories);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, intentContent);
        sendIntent.setType("text/plain");

        setShareIntent(sendIntent);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:{
                finishWithAnimation();
                return true;
            }
            case R.id.action_save: {

                if(isInputValid()){
                    saveQuote();
                    finishWithAnimation();
                } else {
                    CustomToast.showToast(this, getInvalidQuoteMessage(), Toast.LENGTH_SHORT, null);
                }

                return true;
            }
            case R.id.action_delete: {
                displayDeleteAlert();
            }
            case R.id.action_share:{
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ShareActionProvider mShareActionProvider;
    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void displayDeleteAlert(){
        DeleteAlert.showDeleteAlert(this, "Delete entry", "Are you sure you want to delete?", new DeleteAlert.OnDeleteListener() {
            @Override
            public void onDelete() {
                deleteQuote();
                finishWithAnimation();
            }
        });
    }

    private void deleteQuote(){
        Realm realm = Realm.getInstance(this);

        Intent intent = getIntent();
        Quote quote = realm.where(Quote.class).endsWith("uniqueID", intent.getStringExtra("uniqueID")).findFirst();

        realm.beginTransaction();
        quote.removeFromRealm();
        realm.commitTransaction();
    }

    private void saveQuote(){

        String NAME_FIELD = "displayName";

        int page = getPageNumber();

        Intent intent = getIntent();

        Realm realm = Realm.getInstance(this);

        Quote quote = realm.where(Quote.class).endsWith("uniqueID",intent.getStringExtra("uniqueID")).findFirst();

        realm.beginTransaction();

        quote.setSource(adapter.getItem(sourceSpinner.getSelectedItemPosition()));
        quote.setBody(bodyEditText.getText().toString());
        quote.setPage(page);

        quote.getCategories().clear();

        for(String item : selectedCategories){
            Category category = realm.where(Category.class).equalTo(NAME_FIELD, item).findFirst();
            if(category != null)
                quote.getCategories().add(category);
        }

        if(selectedCategories.size() == 0)
            quote.setIsUncategorized(true);
        else
            quote.setIsUncategorized(false);

        realm.commitTransaction();
    }

    private int getPageNumber(){
        int page = -1;

        String pageText = pageEditText.getText().toString();
        if(!pageText.isEmpty()){
            page = Integer.valueOf(pageText);
        }

        return page;
    }

    private boolean isInputValid(){
        return !bodyEditText.getText().toString().isEmpty();
    }

    private String getInvalidQuoteMessage(){
        String message = "";

        if(bodyEditText.getText().toString().isEmpty()){
            message = "Please Enter A Quote";
        }

        return message;
    }

    private void finishWithAnimation(){
        finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }
}