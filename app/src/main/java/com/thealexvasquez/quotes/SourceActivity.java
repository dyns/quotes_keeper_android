package com.thealexvasquez.quotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.MRealmModels.QuoteSource;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 5/29/15
 */

public class SourceActivity extends AppCompatActivity {

    private EditText sourceEditText, descriptionEditText, authorEditText;

    private boolean isCreated;

    private String selectedSourceName;

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_source);

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        sourceEditText = (EditText) findViewById(R.id.sourceEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        authorEditText = (EditText) findViewById(R.id.authorEditText);

        setIsCreated();

        if(isCreated)
            setUpCreated();
    }

    private void setIsCreated(){
        Intent intent = getIntent();
        selectedSourceName = intent.getStringExtra(QuotesConstants.SOURCE);
        isCreated = selectedSourceName != null;
    }

    private void setUpCreated(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Source");

        Realm realm = Realm.getInstance(this);
        QuoteSource quoteSource = realm.where(QuoteSource.class).equalTo("name", selectedSourceName).findFirst();

        sourceEditText.setText(quoteSource.getName());
        descriptionEditText.setText(quoteSource.getDescription());
        authorEditText.setText(quoteSource.getAuthorName());
    }

    private void saveSource(){
        Realm realm = Realm.getInstance(this);
        QuoteSource source;
        realm.beginTransaction();

        if(isCreated)
            source = realm.where(QuoteSource.class).equalTo("name", selectedSourceName).findFirst();
        else
            source = realm.createObject(QuoteSource.class);

        source.setName(sourceEditText.getText().toString());
        source.setDescription(descriptionEditText.getText().toString());
        source.setAuthorName(authorEditText.getText().toString());

        realm.commitTransaction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isCreated)
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        else
            getMenuInflater().inflate(R.menu.menu_save_new, menu);

        return true;
    }

    private boolean isInputValid(){
        return !sourceEditText.getText().toString().isEmpty() && isSourceNameValid();
    }

    private boolean isSourceNameValid(){
        if(isCreated)
            return !sourceEditText.getText().toString().equals(getResources().getString(R.string.view_all_sources)) && isNameEditValid();
        else
            return !sourceEditText.getText().toString().equals(getResources().getString(R.string.view_all_sources)) && !sourceExists();
    }

    private boolean isNameEditValid() {
        return sourceEditText.getText().toString().equals(selectedSourceName) || !sourceExists();
    }

    private boolean sourceExists(){
        Realm realm = Realm.getInstance(this);

        RealmResults<QuoteSource> realmResults = realm.where(QuoteSource.class).equalTo("name", sourceEditText.getText().toString()).findAll();

        return (realmResults.size() > 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.action_save: {

                if(isInputValid()){
                    saveSource();
                    finish();
                }
                else if(sourceEditText.getText().toString().isEmpty()){
                    CustomToast.showToast(this, "Please Enter A Title", Toast.LENGTH_SHORT, null);
                } else {
                    CustomToast.showToast(this, "This Source Already Exists", Toast.LENGTH_SHORT, null);
                }

                return true;
            }
            case R.id.action_delete: {
                displayDeleteAlert();
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void displayDeleteAlert(){

        new AlertDialog.Builder(this)
                .setTitle("Delete source")
                .setMessage(getResources().getString(R.string.delete_source_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSource();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(QuotesConstants.ICON_DELETE_ALERT)
                .show();
    }

    private void deleteSource(){
        Realm realm = Realm.getInstance(this);
        RealmResults<Quote> quoteRealmResults = realm.where(Quote.class).equalTo("source.name", selectedSourceName).findAll();

        realm.beginTransaction();
        quoteRealmResults.clear();
        realm.commitTransaction();

        RealmResults<QuoteSource> quoteSourceRealmResults = realm.where(QuoteSource.class).equalTo("name", selectedSourceName).findAll();
        realm.beginTransaction();
        quoteSourceRealmResults.clear();
        realm.commitTransaction();
    }

}
