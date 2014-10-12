package com.hackzurich.wishlist;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.hackzurich.wishlist.model.Registration;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MainActivity extends CustomActivity {
    public static int FACEBOOK_AUTH = 1;
    public String userId;

    private UiLifecycleHelper uiHelper;
    private LoginButton loginBtn;
    private ProgressBar spinner;

    private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "user_friends", "user_birthday");

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                load(spinner);
                getUserId(session);
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.d("FacebookSampleActivity", "Facebook session closed");

            }
        }
    };

    private void getUserId(final Session session) {
        Request req = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(final GraphUser user, Response response) {
                if (user != null) {
                    final String userId = user.getId();
                    String token = session.getAccessToken();

                    final RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(getString(R.string.endpoint))
                            .build();
                    final WishlistBackend service = restAdapter.create(WishlistBackend.class);
                    service.register(new Registration(userId, token), new Callback<String>() {
                        @Override
                        public void success(String aVoid, retrofit.client.Response response) {
                            Intent myIntent = new Intent(MainActivity.this, TabbedActivity.class);
                            myIntent.putExtra(TabbedActivity.ARG_USER_ID, userId);
                            MainActivity.this.startActivity(myIntent);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG);
                            // TODO; // die instead
                            // rip
                            // Intent myIntent = new Intent(MainActivity.this, TabbedActivity.class);
                            // MainActivity.this.startActivity(myIntent);
                        }
                    });
                }
            }
        });
        req.executeAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        loginBtn = (LoginButton) findViewById(R.id.authButton);
        loginBtn.setPublishPermissions(PERMISSIONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

    public void requestPermissions() {
        Session s = Session.getActiveSession();
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                    this, PERMISSIONS));
    }

    public void load(View view){
        spinner.setVisibility(View.VISIBLE);
    }
}
