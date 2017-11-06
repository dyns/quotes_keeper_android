package com.thealexvasquez.quotes;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by LACAJITA on 6/20/15
 */

public class CustomToast {

    public static void showToast (Context context, String text, int length) {
        showToast(context, text, length, null);
    }

    public static void showToast (Context context, String text, int length, Integer imageResources){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_custom, null, false);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(text);

        if(imageResources != null){
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(imageResources);
            imageView.setVisibility(View.VISIBLE);
        }

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(length);
        toast.setView(view);
        toast.show();
    }

    private static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    /*



View layout = inflater.inflate(R.layout.toast_layout,
                               (ViewGroup) findViewById(R.id.toast_layout_root));

ImageView image = (ImageView) layout.findViewById(R.id.image);
image.setImageResource(R.drawable.android);
TextView text = (TextView) layout.findViewById(R.id.text);
text.setText("Hello! This is a custom toast!");

Toast toast = new Toast(getApplicationContext());
toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
toast.setDuration(Toast.LENGTH_LONG);
toast.setView(layout);
toast.show();



     */
}
