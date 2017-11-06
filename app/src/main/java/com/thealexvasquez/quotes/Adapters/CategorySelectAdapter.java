package com.thealexvasquez.quotes.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/3/15
 */

public class CategorySelectAdapter extends RealmBaseAdapter<Category> {

    private List<String> selectedCategories;

    public CategorySelectAdapter(Context context, RealmResults<Category> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        selectedCategories = new ArrayList<>();
    }

    public void selectAllCategories(){
        selectedCategories.clear();
        for(Category category : realmResults){
            selectedCategories.add(category.getDisplayName());
        }
    }

    public void removeAllCategories(){
        selectedCategories.clear();
    }

    public void addCategory(String name){
        selectedCategories.add(name);
    }

    public void removeCategory(String name){
        selectedCategories.remove(name);
    }

    public List<String> getSelectedCategories(){
        return  selectedCategories;
    }

    public boolean isCategorySelected(String name){
        return  selectedCategories.contains(name);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getDisplayName();
//        String nameIdentifier = getItem(position).getName();

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.cell_source, parent, false);
        }

        ((TextView)view).setText(name);
        view.setBackgroundResource(R.drawable.selector_category_unselected);

        if(selectedCategories.contains(name)){
            view.setBackgroundResource(R.drawable.selector_cateogory_selected);
        }

        return view;
    }

}
