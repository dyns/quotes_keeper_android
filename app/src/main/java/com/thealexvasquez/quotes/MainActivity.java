package com.thealexvasquez.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.thealexvasquez.quotes.Adapters.QuotesAdapter;
import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.MRealmModels.QuoteSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    private QuotesAdapter quotesAdapter;
    private ListView listView;
    private TextView categoriesTextView;

    private final String VIEW_ALL_CATEGORIES = "All Categories";
    private final String NAME_FIELD = "displayName";

    private List<String> visibleCategories;
    private String selectedSource;

    private boolean isUncategorizedVisible = true;

    View overlayView;

    FloatingActionButton newQuoteButton, newSourceButton, newCategoryButton;
    FloatingActionsMenu floatingMenu;

    ActionBarDrawerToggle mDrawerToggle;

    private View.OnClickListener someOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.newQuoteButton:
                    if(isNewQuoteAllowed())
                        startActivity(new Intent(MainActivity.this, NewQuoteActivity.class));
                    else
                        CustomToast.showToast(MainActivity.this, getResources().getString(R.string.source_required), Toast.LENGTH_SHORT, null);
                    break;
                case R.id.newSourceButton:
                    startActivity(new Intent(MainActivity.this, SourceActivity.class));
                    break;
                case R.id.newCategoryButton:
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    break;
            }

            floatingMenu.collapse();
        }
    };

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_slider);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overlayView = findViewById(R.id.overlayView);
        overlayView.setOnClickListener(this);
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        floatingMenu.collapse();
                        return true;
                    }
                }
                return false;
            }
        });

        View selectCategoriesView = findViewById(R.id.selectCategoriesView);
        selectCategoriesView.setOnClickListener(this);

        floatingMenu = (FloatingActionsMenu) findViewById(R.id.floatingMenu);
        floatingMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                overlayView.setVisibility(View.VISIBLE);
                overlayView.setClickable(true);
            }

            @Override
            public void onMenuCollapsed() {
                overlayView.setVisibility(View.GONE);
                overlayView.setClickable(false);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // Drawer object Assigned to the view

        DrawerFragment drawerFragment = new DrawerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer_frame, drawerFragment).commit();

        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_Drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        drawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
                @Override
                public void onMenuVisibilityChanged(boolean isVisible) {
                    if (isVisible && floatingMenu.isExpanded()){
                        floatingMenu.collapse();
                    }
                }
            });
        }

        newQuoteButton = (FloatingActionButton) findViewById(R.id.newQuoteButton);
        newSourceButton = (FloatingActionButton) findViewById(R.id.newSourceButton);
        newCategoryButton = (FloatingActionButton) findViewById(R.id.newCategoryButton);


        newQuoteButton.setOnClickListener(someOnclick);
        newSourceButton.setOnClickListener(someOnclick);
        newCategoryButton.setOnClickListener(someOnclick);

        categoriesTextView = (TextView) findViewById(R.id.categoriesTextView);
        categoriesTextView.setText(VIEW_ALL_CATEGORIES);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(findViewById(R.id.emptyView));

        visibleCategories = new ArrayList<>();
        Realm realm = Realm.getInstance(this);
        RealmResults<Category> allCategories = realm.where(Category.class).findAll();
        for(Category category : allCategories){
            visibleCategories.add(category.getDisplayName());
        }
        setVisibleCategoriesToCategoriesTextView();

        selectedSource = "";

        setSourcesAdapter();


    }

    @Override
    protected void onResume(){
        super.onResume();
        checkIfSelectedCategoriesAreValid();
        setSourcesAdapter();
        setVisibleCategoriesToCategoriesTextView();
    }

    private void checkIfSelectedCategoriesAreValid(){
        Realm realm = Realm.getInstance(this);

        List<String> categoriesToRemove = new ArrayList<>();

        for(final String categoryDisplayName : visibleCategories){
            RealmResults<Category> category = realm.where(Category.class).equalTo("displayName", categoryDisplayName).findAll();

            if(category.size() == 0)
                categoriesToRemove.add(categoryDisplayName);

        }

        for(String categoryToRemove : categoriesToRemove)
            visibleCategories.remove(categoryToRemove);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_export:
                startActivity(new Intent(this, ExportActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent= new Intent(this, QuoteActivity.class);
        intent.putExtra("uniqueID", quotesAdapter.getItem(position).getUniqueID());
        startActivity(intent);
    }

    private boolean isNewQuoteAllowed(){
        Realm realm = Realm.getInstance(this);
        RealmResults<QuoteSource> sources = realm.where(QuoteSource.class).findAll();
        return (sources.size() > 0);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String source) {
        if(listView != null && drawerLayout != null){
            filterQuotesForSource(source);
            drawerLayout.closeDrawers();
        }

    }

    @Override
    public void onNavigationDrawerItemLongSelected(int position, String source) {
        if(!source.equals(getResources().getString(R.string.view_all_sources)))
            startActivity(new Intent(this, SourceActivity.class).putExtra(QuotesConstants.SOURCE, source));
    }

    private void filterQuotesForSource(String source){
        if(source.equals(getResources().getString(R.string.view_all_sources)))
            selectedSource = "";
        else
            selectedSource = source;

        getSupportActionBar().setTitle(source);
        setSourcesAdapter();
    }

    private final int CATEGORY_SELECT = 52;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectCategoriesView:{
                String MAIN_ACTIVITY = getResources().getString(R.string.MAIN_ACTIVITY);
                String IS_UNCATEGORIZED_VISIBLE = getResources().getString(R.string.IS_UNCATEGORIZED_VISIBLE);
                startActivityForResult(new Intent(this, CategorySelectActivity.class)
                        .putStringArrayListExtra("ITEMS", (ArrayList<String>)visibleCategories)
                        .putExtra(MAIN_ACTIVITY, MAIN_ACTIVITY)
                        .putExtra(IS_UNCATEGORIZED_VISIBLE, isUncategorizedVisible), CATEGORY_SELECT);
                break;
            }
            case R.id.overlayView:{
                floatingMenu.collapse();
                break;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == CATEGORY_SELECT) {

            String [] selectedCategoriesResponse = intent.getStringArrayExtra("ITEMS");
            isUncategorizedVisible = intent.getBooleanExtra("SHOW_UNCATEGORIZED", false);

            visibleCategories.clear();

            if(selectedCategoriesResponse.length > 0)
                Collections.addAll(visibleCategories, selectedCategoriesResponse);

            setVisibleCategoriesToCategoriesTextView();

            setSourcesAdapter();
        }
    }

    private void setSourcesAdapter(){

        Realm realm = Realm.getInstance(this);
        RealmQuery<Quote> query = realm.where(Quote.class);
        RealmResults<Quote> realmResults;

        if(!selectedSource.isEmpty()){
            query.equalTo("source.name", selectedSource);
        }

        if(!visibleCategories.isEmpty()){
            query.beginGroup();
            for(int i = 0; i < visibleCategories.size(); i++){
                String category = visibleCategories.get(i);
                if(i == (visibleCategories.size() - 1)){
                    query = query.equalTo("categories." + NAME_FIELD, category);
                } else {
                    query = query.equalTo("categories." + NAME_FIELD, category).or();
                }
            }
            query.endGroup();
        }

        if(isUncategorizedVisible){
            if(!visibleCategories.isEmpty()){
                query.or();
            }

            query.equalTo("isUncategorized", true);
        }

        realmResults = query.findAll();

        if(isUncategorizedVisible)
            realmResults.sort("isUncategorized", RealmResults.SORT_ORDER_ASCENDING);

        if(!isUncategorizedVisible && visibleCategories.isEmpty()){
            realmResults = realm.where(Quote.class).equalTo("categories.name", "").findAll();
        }

        quotesAdapter = new QuotesAdapter(this, realmResults, true);
        listView.setAdapter(quotesAdapter);
    }

    private void setVisibleCategoriesToCategoriesTextView(){
        String builder = "";

        for (String selectedCategory : visibleCategories) {
            if(builder.equals(""))
                builder = builder + selectedCategory;
            else
                builder = builder + ", " + selectedCategory;
        }

        if(isUncategorizedVisible){
            if(builder.isEmpty())
                builder = "Uncategorized";
            else
                builder = builder + ", Uncategorized";
        }

        if(builder.isEmpty()){
            builder = getResources().getString(R.string.click_to_select_visible_categories);
        }

        categoriesTextView.setText(builder);
    }

    @Override
    public void onBackPressed() {
        if(floatingMenu.isExpanded()){
            floatingMenu.collapse();
        } else if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        } else{
            super.onBackPressed();
        }
    }

    // If drawer is open when FAB is open, close drawer and then open menu
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if ( keyCode == KeyEvent.KEYCODE_MENU  && floatingMenu.isExpanded()) {
//            floatingMenu.collapse();
//        }
//
//        // let the system handle all other key events
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }
}