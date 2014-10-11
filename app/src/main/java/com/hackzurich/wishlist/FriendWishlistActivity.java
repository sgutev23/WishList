package com.hackzurich.wishlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FriendWishlistActivity extends CustomActivity {
    public final static String MY_ID = "my_user_id";
    public final static String USER_ID = "user_id";
    public final static String USER_NAME = "user_name";
    public final static String NAME_MAP = "name_map";
    private String userId = null;
    private String userName = null;
    WishlistBackend service;
    Bundle nameMap;
    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getStringExtra(USER_ID);
        userName = intent.getStringExtra(USER_NAME);
        myId = intent.getStringExtra(MY_ID);
        nameMap = intent.getBundleExtra(NAME_MAP);

        setContentView(R.layout.activity_friend_wishlist);
        ListView list = (ListView) findViewById(R.id.list);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint))
                .build();
        service = restAdapter.create(WishlistBackend.class);

        refreshAdapter(list, service);
    }

    void refreshAdapter(ListView list, final WishlistBackend service) {
        try {
            List<Wish> wishes = new AsyncTask<Void, Void, List<Wish>>() {
                @Override
                protected List<Wish> doInBackground(Void... voids) {
                    return service.getWishList(userId);
                }
            }.execute().get();

            WishListAdapter adapter = new WishListAdapter(getLayoutInflater(), wishes);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class WishListAdapter extends BaseAdapter {

        private final List<Wish> wishes;
        private final LayoutInflater inflater;

        public WishListAdapter(final LayoutInflater inflater, List<Wish> wishes) {
            this.inflater = inflater;
            this.wishes = wishes;
        }

        @Override
        public int getCount() {
            return wishes.size();
        }

        @Override
        public Object getItem(int i) {
            return wishes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View root = inflater.inflate(R.layout.list_item_card, viewGroup, false);
            final TextView line1 = (TextView) root.findViewById(R.id.line1);
            final TextView line2 = (TextView) root.findViewById(R.id.line2);
            line1.setText(wishes.get(i).getContent());
            String bought = wishes.get(i).getBought();
            if (! (bought == null || bought.isEmpty())) {
                line1.setPaintFlags(line1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                line2.setText(String.format(" - %s", nameMap.getString(bought)));
            } else {
                line2.setText("");
            }
            final String wishId = wishes.get(i).getId();
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(FriendWishlistActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to buy this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    line1.setPaintFlags(line1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    service.changeWishState(myId, userId, wishId, new Callback<String>() {
                                        @Override
                                        public void success(String s, Response response) {
                                            line2.setText(String.format("- %s", nameMap.getString(myId)));
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(FriendWishlistActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("No", null)
                            .show();
                }
            });
            return root;
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
