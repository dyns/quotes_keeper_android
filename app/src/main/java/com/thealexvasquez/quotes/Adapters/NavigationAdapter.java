package com.thealexvasquez.quotes.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.thealexvasquez.quotes.R;

import java.util.List;

/**
 * Created by LACAJITA on 7/1/15
 */
/*
 mDrawerListView.setAdapter(new ArrayAdapter<>(
                getActionBar().getThemedContext(),
                R.layout.cell_navigation,
                R.id.cellTitle,
                sources));
 */

public class NavigationAdapter extends ArrayAdapter<String> {

    int selectedPosition = -1;

    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }

    public NavigationAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view =  super.getView(position, convertView, parent);

        if(position == selectedPosition){
            view.setBackgroundResource(R.drawable.selector_inverted_source_navigation_selection);
        } else {
            view.setBackgroundResource(R.drawable.selector_source_selection_cell);
        }

        return view;
    }

}
