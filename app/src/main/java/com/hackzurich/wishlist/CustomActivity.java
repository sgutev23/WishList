package com.hackzurich.wishlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by heat on 10/11/14.
 */
public class CustomActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customizeTitleBar();
    }

    private void customizeTitleBar() {
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        TextView titleView = ((TextView)v.findViewById(R.id.title));
        titleView.setText(R.string.app_name);

        this.getActionBar().setCustomView(v);
    }
}
