package com.thealexvasquez.quotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.thealexvasquez.quotes.MRealmModels.Category;
import com.thealexvasquez.quotes.MRealmModels.Quote;
import com.thealexvasquez.quotes.helpers.ExportQuotesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LACAJITA on 6/18/15
 */

public class ExportActivity extends AppCompatActivity implements View.OnClickListener {

    Realm realm;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_export);

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        realm = Realm.getInstance(this);

        findViewById(R.id.jsonEmailExport).setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

//        String[] files = fileList();
//
//        new AlertDialog.Builder(this)
//                .setTitle("files")
//                .setMessage(Arrays.toString(files))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String emailSubject(String type){
        return "Quotes "+type+" Export " + (new SimpleDateFormat("LLL d, yyyy", Locale.getDefault()).format(new Date()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jsonEmailExport:
                showExportAlert("as JSON", new OnExport() {
                    @Override
                    public void onExport() {
                        createTextFile(getRealmJson());
                        sendEmail("ev117gm@gmail.com", emailSubject("JSON"), "Quotes JSON Export", Uri.fromFile(getFileStreamPath(TEMP_FILE)));
//                        deleteTempFileFile();
                    }
                });
                break;
        }
    }

    public void showExportAlert( String messageAddOn, final ExportActivity.OnExport onExportListener){
        new AlertDialog.Builder(this)
                .setTitle("Export")
                .setMessage("Are you sure you want to export all " + messageAddOn + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onExportListener.onExport();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private interface OnExport {
        void onExport();
    }

    private void sendEmail(String to, String subject, String body){
        sendEmail(new String[]{to}, subject, body, null);
    }

    private void sendEmail(String to, String subject, String body, Uri uri){
        sendEmail(new String[]{to}, subject, body, uri);
    }

    private void sendEmail(String[] to, String subject, String body, Uri uri){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_EMAIL, to);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);

        if(uri != null){
            i.putExtra(Intent.EXTRA_STREAM, uri);
        }

        try {
            startActivityForResult(createEmailOnlyChooserIntent(i, "Send", to[0]), 0);
        } catch (android.content.ActivityNotFoundException ex) {
            CustomToast.showToast(this, "There are no email clients installed.", Toast.LENGTH_SHORT);
        }
    }

    public Intent createEmailOnlyChooserIntent(Intent source,
                                               CharSequence chooserTitle, String email) {
        Stack<Intent> intents = new Stack<>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                email, null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));

            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    private String getRealmJson(){
        JSONObject container = new JSONObject();
        try {
            container.put("Categories", ExportQuotesHelper.exportCategoriesAsJSONArray(this));
            container.put("Sources", ExportQuotesHelper.exportSourcesAsJSONArray(this));
            container.put("Quotes", ExportQuotesHelper.exportQuotesAsJSONArray(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return container.toString();
    }

    private final String APP_NAME = "Quotes";
    private final String TEMP_FILE = APP_NAME + "_JSON_Export.json";
    private void createTextFile(String contents){

        try {
            FileOutputStream fos = openFileOutput(TEMP_FILE, Context.MODE_WORLD_READABLE);
            fos.write(contents.getBytes());
            fos.close();
        }
         catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void deleteTempFileFile(){
//
//        if(deleteFile(getFileStreamPath(TEMP_FILE).getName())){
//            CustomToast.showToast(this, "Deleted", Toast.LENGTH_SHORT);
//        } else {
//            CustomToast.showToast(this, "error deleting", Toast.LENGTH_SHORT);
//        }
//
//    }

    private String getRealmText(){

        String results = "";

        RealmResults<Quote> quoteRealmResults = realm.where(Quote.class).findAll();

        for(Quote quote : quoteRealmResults){

            String pageNumber = (quote.getPage() != -1 ? String.valueOf(quote.getPage() ): "n.p.");

            String quoteString = quote.getBody() + "\n" + quote.getSource().getName() + "\n" + pageNumber + "\n";

            String categories = "";
            for(Category category : quote.getCategories()){
                if(categories.isEmpty())
                    categories = category.getDisplayName();
                else
                    categories = categories + ", " + category.getDisplayName();
            }

            quoteString = quoteString + categories;

            if(results.isEmpty())
                results = quoteString;
            else
                results = results + "\n\n" + quoteString;

        }

        return results;
    }



}
