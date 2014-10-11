package com.hackzurich.wishlist;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hackzurich.wishlist.rest.WishlistBackend;

import retrofit.RestAdapter;


public class LoginScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        final TextView tv = (TextView) findViewById(R.id.tv);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://cotizo.net:3000/")
                .build();
        final WishlistBackend service = restAdapter.create(WishlistBackend.class);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (final Integer x: service.getFriendList()) {
                    LoginScreen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(tv.getText() + " " + x.toString());
                        }
                    });
                }
                return null;

            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
