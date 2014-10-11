package com.hackzurich.wishlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.RestAdapter;


public class FriendWishlistActivity extends CustomActivity {
    public final static String USER_ID = "user_id";
    public final static String  USER_NAME = "user_name";
    private String userId = null;
    private String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getStringExtra(USER_ID);
        userName = intent.getStringExtra(USER_NAME);

        setContentView(R.layout.activity_friend_wishlist);
        ListView list = (ListView) findViewById(R.id.list);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint))
                .build();
        final WishlistBackend service = restAdapter.create(WishlistBackend.class);

        refreshAdapter(list, service);
    }

    void refreshAdapter(ListView list, final WishlistBackend service) {
        try {
            List<String> wishes = new AsyncTask<Void, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(Void... voids) {
                    List<String> result = new LinkedList<String>();
                    for (Wish w: service.getWishList(userId)) {
                        result.add(w.getContent());
                    }
                    return result;
                }
            }.execute().get();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row, wishes);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_wishlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
