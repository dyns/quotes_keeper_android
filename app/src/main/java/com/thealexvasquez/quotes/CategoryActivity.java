package com.thealexvasquez.quotes;

import android.app.Activity;
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

import com.thealexvasquez.quotes.MRealmModels.Category;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/2/15
 */

public class CategoryActivity extends AppCompatActivity {

    EditText nameEditText;

    boolean isCreated;
    String selectedCategory;
    String selectedCategoryDisplayName;

    Realm realm;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_category);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        nameEditText = (EditText) findViewById(R.id.nameEditText);

        realm = Realm.getInstance(this);

        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra(getResources().getString(R.string.category_extra));

        isCreated = (selectedCategory != null);

        if(isCreated)
            setUpCreated();

    }

    private String getCategoryNameInput(){
        return nameEditText.getText().toString().toUpperCase();
    }

    private void setUpCreated(){
        getSupportActionBar().setTitle(getResources().getString(R.string.category));

        Category category = realm.where(Category.class).equalTo("name", selectedCategory).findFirst();
        selectedCategoryDisplayName = category.getDisplayName();
        nameEditText.setText(category.getDisplayName());
    }

    private void saveCategory(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!isCreated) {
                    Category category = realm.createObject(Category.class);
                    category.setName(getCategoryNameInput());
                    category.setDisplayName(nameEditText.getText().toString());
                } else {
                    Category category = realm.where(Category.class).equalTo("name", selectedCategory).findFirst();
                    category.setName(getCategoryNameInput());
                    category.setDisplayName(nameEditText.getText().toString());
                }
            }
        });
    }

    private void deleteCategory(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Category category = realm.where(Category.class).equalTo("name", selectedCategory).findFirst();
                category.removeFromRealm();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isCreated)
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        else
            getMenuInflater().inflate(R.menu.menu_save_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finishWithTransition();
                return true;
            case R.id.action_save: {
                if(categoryIsValid()){
                    saveCategory();

                    if(isCreated)
                        setEditedResult();

                    finishWithTransition();
                }
                else
                    displayErrorMessage();
                return true;
            }

            case R.id.action_delete:
                displayDeleteAlert();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setEditedResult(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_NAME", selectedCategoryDisplayName);
        resultIntent.putExtra("EDITED_NAME", nameEditText.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private void setDeletedResult(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_NAME", selectedCategoryDisplayName);
        resultIntent.putExtra("DELETED", true);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private void displayDeleteAlert(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_category))
                .setMessage(getResources().getString(R.string.category_delete_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCategory();
                        setDeletedResult();
                        finishWithTransition();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(QuotesConstants.ICON_DELETE_ALERT)
                .show();
    }

    private boolean categoryIsValid(){
        if(!isCreated)
            return !getCategoryNameInput().isEmpty() && !categoryExists();
        else
            return !getCategoryNameInput().isEmpty() && isExistingCategoryValid();
    }

    private boolean isExistingCategoryValid() {
        return selectedCategory.equals(getCategoryNameInput()) || !categoryExists();
    }

    private boolean categoryExists(){
        RealmResults<Category> categoryRealmResults = realm.where(Category.class).equalTo("name", getCategoryNameInput()).findAll();
        return categoryRealmResults.size() > 0;
    }

    private void displayErrorMessage(){
        String message;

        if(getCategoryNameInput().isEmpty())
            message = "Please Enter A Name";
        else
            message = "Category Already Exists";

        CustomToast.showToast(this, message, Toast.LENGTH_SHORT, null);
    }

    private void finishWithTransition() {
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
