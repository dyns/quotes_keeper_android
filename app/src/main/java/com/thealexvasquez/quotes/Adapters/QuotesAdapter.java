package com.thealexvasquez.quotes.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.R;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 5/23/15
 */

public class QuotesAdapter extends RealmBaseAdapter<Quote> {

    public QuotesAdapter(Context context,
                     RealmResults<Quote> realmResults,
                     boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.cell_quote, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.cellTitle);
        TextView source = (TextView) view.findViewById(R.id.cellSource);
        TextView page= (TextView) view.findViewById(R.id.cellPage);

        title.setText(getItem(position).getBody());
        source.setText(getItem(position).getSource().getName());

        int pageNumber = getItem(position).getPage();

        if(pageNumber == -1)
            page.setText("n.p.");
        else
            page.setText(String.valueOf(pageNumber));

        TextView quoteCategoriesTextView = (TextView) view.findViewById(R.id.quoteCategoriesTextView);
        String categories = "";
        for(Category category : getItem(position).getCategories()){
            if(categories.isEmpty())
                categories = category.getDisplayName();
            else
                categories = categories + ", " + category.getDisplayName();
        }

        if(categories.isEmpty())
            categories = "Uncategorized";

        quoteCategoriesTextView.setText(categories);

        return view;
    }
}