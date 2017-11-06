package com.thealexvasquez.quotes.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.R;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/2/15
 */

public class CategoryAdapter extends RealmBaseAdapter<Category> {
    public CategoryAdapter(Context context, RealmResults<Category> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.cell_source, parent, false);
        }

        ((TextView)view).setText(getItem(position).getName());

        return view;
    }
}
