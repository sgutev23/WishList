package com.hackzurich.wishlist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.hackzurich.wishlist.model.Registration;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MainActivity extends Activity implements ActionBar.TabListener {
    public static int FACEBOOK_AUTH = 1;

    private Session.StatusCallback statusCallback =
            new SessionStatusCallback();
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
                Request req = Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            String userId = user.getId();
                            String token = session.getAccessToken();

                            final RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setEndpoint("http://cotizo.net:3000/")
                                    .build();
                            final WishlistBackend service = restAdapter.create(WishlistBackend.class);
                            service.register(new Registration(userId, token), new Callback<Void>() {
                                @Override
                                public void success(Void aVoid, retrofit.client.Response response) {

                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });
                        }
                    }
                });
                req.executeAsync();
            }
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (Session.getActiveSession() == null) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, FACEBOOK_AUTH);
        } else {
            doLogin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        doLogin();
    }

    void doLogin() {
        Session session = Session.getActiveSession();
        if (session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("public_profile", "user_friends"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, false, statusCallback);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            session.closeAndClearTokenInformation();
        }
    }

    void getHashForFB () {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.hackzurich.wishlist",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new MyWishesFragment();
                case 1:
                default:
                    return new MyFriendsFragment();
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }
}
