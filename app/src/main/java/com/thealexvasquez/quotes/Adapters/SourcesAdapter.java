package com.thealexvasquez.quotes.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealexvasquez.quotes.MRealmModels.QuoteSource;
import com.thealexvasquez.quotes.R;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 5/29/15
 */

public class SourcesAdapter extends RealmBaseAdapter<QuoteSource> {

    public SourcesAdapter(Context context, RealmResults<QuoteSource> realmResults, boolean automaticUpdate) {
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
