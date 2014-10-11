package com.hackzurich.wishlist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.hackzurich.wishlist.model.Card.Card;
import com.hackzurich.wishlist.model.Registration;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MainActivity extends CustomActivity implements ActionBar.TabListener {
    public static int FACEBOOK_AUTH = 1;
    public String userId;
    public final Object lock = new Object();

    private Session.StatusCallback statusCallback =
            new SessionStatusCallback();
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
                getUserId(session);
            }
        }
    }

    private void getUserId(final Session session) {
        Request req = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    String userId = user.getId();
                    MainActivity.this.userId = userId;
                    String token = session.getAccessToken();

                    final RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(getString(R.string.endpoint))
                            .build();
                    final WishlistBackend service = restAdapter.create(WishlistBackend.class);
                    service.register(new Registration(userId, token), new Callback<Void>() {
                        @Override
                        public void success(Void aVoid, retrofit.client.Response response) {
                            resumeSetup();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            resumeSetup(); // die
                        }
                    });
                }
            }
        });
        req.executeAsync();
    }

    private void resumeSetup() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
        } else if (session.isOpened()) {
            if (session.getAccessToken().isEmpty()) {
                Session.openActiveSession(this, false, statusCallback);
            } else {
                getUserId(session);
            }
        } else {
            throw new Error("WTF");
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
                    return MyWishesFragment.newInstance(1, userId);
                case 1:
                default:
                    return MyFriendsFragment.newInstance(1, userId);
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

    public static class CardArrayAdapter  extends ArrayAdapter<Card> {
        private static final String TAG = "CardArrayAdapter";
        private List<Card> cardList = new ArrayList<Card>();

        static class CardViewHolder {
            TextView line1;
            TextView line2;
        }

        public CardArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public void add(Card object) {
            cardList.add(object);
            super.add(object);
        }

        @Override
        public int getCount() {
            return this.cardList.size();
        }

        @Override
        public Card getItem(int index) {
            return this.cardList.get(index);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CardViewHolder viewHolder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_card, parent, false);
                viewHolder = new CardViewHolder();
                viewHolder.line1 = (TextView) row.findViewById(R.id.line1);
                viewHolder.line2 = (TextView) row.findViewById(R.id.line2);
                row.setTag(viewHolder);
            } else {
                viewHolder = (CardViewHolder)row.getTag();
            }
            Card card = getItem(position);
            viewHolder.line1.setText(card.getLine1());
            viewHolder.line2.setText(card.getLine2());
            return row;
        }

        public Bitmap decodeToBitmap(byte[] decodedByte) {
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }
    }
}
