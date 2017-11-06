package com.thealexvasquez.quotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.thealexvasquez.quotes.Adapters.CategorySelectAdapter;
import com.thealexvasquez.quotes.MRealmModels.Category;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/3/15
 */

public class CategorySelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    private CategorySelectAdapter adapter;
    private CheckBox viewUncategorizedCheckbox;
    private Button selectButton;

    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_select_category);

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        viewUncategorizedCheckbox = (CheckBox) findViewById(R.id.viewUncategorizedCheckbox);

        selectButton = (Button) findViewById(R.id.selectButton);
        selectButton.setOnClickListener(this);

        Realm realm = Realm.getInstance(this);

        RealmResults<Category> realmResults = realm.where(Category.class).findAll();

        adapter = new CategorySelectAdapter(this, realmResults, true);
        adapter.notifyDataSetChanged();

        ListView listView = (ListView) findViewById(R.id.listView);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setEmptyView(findViewById(R.id.emptyView));

        getAndSetPreviouslySelectedCategories();
        showUncategorizedCheckBoxIfValid();
    }

    private void showUncategorizedCheckBoxIfValid(){
        Intent intent = getIntent();
        if(intent.getStringExtra(getResources().getString(R.string.MAIN_ACTIVITY)) != null){
            viewUncategorizedCheckbox.setChecked(intent.getBooleanExtra(getResources().getString(R.string.IS_UNCATEGORIZED_VISIBLE), false));
        } else {
            viewUncategorizedCheckbox.setVisibility(View.GONE);
        }
    }

    private void getAndSetPreviouslySelectedCategories(){
        Intent intent = getIntent();
        List<String> preSelectedCategories = intent.getStringArrayListExtra("ITEMS");
        for(String item : preSelectedCategories){
            adapter.addCategory(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finishWithSelections();
                return true;

            case R.id.action_new:
                startActivity(new Intent(this, CategoryActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = adapter.getItem(position).getDisplayName();
        if(adapter.isCategorySelected(name)){
            adapter.removeCategory(name);
        } else {
            adapter.addCategory(name);
        }

        adapter.notifyDataSetChanged();
    }

    private final int CATEGORY_EDIT = 548;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        startActivityForResult(new Intent(this, CategoryActivity.class)
                .putExtra(getResources().getString(R.string.category_extra), adapter.getItem(position).getName()), CATEGORY_EDIT);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == CATEGORY_EDIT) {

            String SELECTED_NAME = intent.getStringExtra("SELECTED_NAME");
            String EDITED_NAME = intent.getStringExtra("EDITED_NAME");

            boolean DELETED = intent.getBooleanExtra("DELETED", false);

            if(DELETED){
                adapter.removeCategory(SELECTED_NAME);
                adapter.notifyDataSetChanged();
            }
            else if(adapter.isCategorySelected(SELECTED_NAME)){
                adapter.removeCategory(SELECTED_NAME);
                adapter.addCategory(EDITED_NAME);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishWithSelections();
    }

    private void finishWithSelections(){

        String[] items = new String[adapter.getSelectedCategories().size()];

        for(int i = 0; i < adapter.getSelectedCategories().size(); i++){
            items[i] = adapter.getSelectedCategories().get(i);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("ITEMS", items);
        resultIntent.putExtra("SHOW_UNCATEGORIZED", viewUncategorizedCheckbox.isChecked());
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectButton:{
                if(isSelected){
                    selectButton.setText("Select All");
                    adapter.removeAllCategories();
                    isSelected = false;
                } else {
                    selectButton.setText("Deselect All");
                    adapter.selectAllCategories();
                    isSelected = true;
                }
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
