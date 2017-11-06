package com.thealexvasquez.quotes.QuotesActivites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thealexvasquez.quotes.Adapters.SourcesAdapter;
import com.thealexvasquez.quotes.CategorySelectActivity;
import com.thealexvasquez.quotes.CustomToast;
import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.MRealmModels.QuoteSource;
import com.thealexvasquez.quotes.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/7/15
 */

public class QuoteBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected EditText bodyEditText, pageEditText;
    protected Spinner sourceSpinner;
    protected SourcesAdapter adapter;
    protected TextView categoriesTextView;

    protected List<String> selectedCategories;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_quote);

        selectedCategories = new ArrayList<>();

        bodyEditText = (EditText) findViewById(R.id.quoteEditText);
        pageEditText = (EditText) findViewById(R.id.pageEditText);
        sourceSpinner = (Spinner) findViewById(R.id.sourceSpinner);
        categoriesTextView = (TextView) findViewById(R.id.categoriesTextView);

        Realm realm = Realm.getInstance(this);

        RealmResults<QuoteSource> realmResults = realm.where(QuoteSource.class).findAll();

        adapter = new SourcesAdapter(this, realmResults, true);
        sourceSpinner.setAdapter(adapter);

        View categoryView = findViewById(R.id.categoryView);
        categoryView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_save: {

                if(isInputValid()){
                    saveQuote();
                    finish();
                }
                else{
                    CustomToast.showToast(this, getInvalidQuoteMessage(), Toast.LENGTH_SHORT, null);
                }

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
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

    private void saveQuote(){

        String id = getUniqueID();
        int page = getPageNumber();

        Realm realm = Realm.getInstance(this);

        realm.beginTransaction();

        Quote quote = realm.createObject(Quote.class);

        quote.setBody(bodyEditText.getText().toString());
        quote.setSource(adapter.getItem(sourceSpinner.getSelectedItemPosition()));
        quote.setPage(page);
        quote.setUniqueID(id);

        for(String item : selectedCategories){
            Category category = realm.where(Category.class).equalTo("name", item).findFirst();
            quote.getCategories().add(category);
        }

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

    private String getUniqueID(){
        String id = generateID();
        if(checkIfUnique(String.valueOf(id))){
            return String.valueOf(id);
        }
        else {
            return getUniqueID();
        }
    }

    private boolean checkIfUnique(String id){

        Realm realm = Realm.getInstance(this);

        String QUOTE_ID = "uniqueID";
        RealmResults<Quote> results = realm.where(Quote.class).equalTo(QUOTE_ID, id).findAll();

        return results.size() == 0;
    }

    private String generateID(){
        UUID id = UUID.randomUUID();
        return String.valueOf(id);
    }

    private final int CATEGORY_SELECT = 52;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.categoryView:
                startActivityForResult(new Intent(this, CategorySelectActivity.class).putStringArrayListExtra("ITEMS", (ArrayList<String>)selectedCategories), CATEGORY_SELECT);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CATEGORY_SELECT) {
                String [] selectedCategoriesResponse = intent.getStringArrayExtra("ITEMS");
                if(selectedCategoriesResponse != null ){
                    if(selectedCategoriesResponse.length > 0){
                        selectedCategories.clear();
                        Collections.addAll(selectedCategories, selectedCategoriesResponse);

                        String builder = "";

                        for (String selectedCategory : selectedCategories) {
                            if(builder.equals(""))
                                builder = builder + selectedCategory;
                            else
                                builder = builder + ", " + selectedCategory;
                        }

                        categoriesTextView.setText(builder);
                    } else {
                        selectedCategories.clear();
                        categoriesTextView.setText("");
                    }
                }
            }
        }
    }
}