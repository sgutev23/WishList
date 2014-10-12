package com.hackzurich.wishlist;

/**
 * Created by heat on 10/11/14.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardArrayAdapter  extends ArrayAdapter<Wish> {
    private static final String TAG = "CardArrayAdapter";
    private final String userId;
    private List<Wish> cardList = new ArrayList<Wish>();
    private final WishlistBackend service;

    static class CardViewHolder {
        TextView line1;
        TextView line2;
    }

    public CardArrayAdapter(Context context, int textViewResourceId, List<Wish> cardList, String userId) {
        super(context, textViewResourceId);
        this.cardList = cardList;
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(context.getString(R.string.endpoint))
                .build();

        service = restAdapter.create(WishlistBackend.class);
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Wish getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_card, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.line1 = (TextView) row.findViewById(R.id.line1);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        final Wish card = getItem(position);
        viewHolder.line1.setText(card.getContent());
        viewHolder.line1.setLongClickable(true);
        viewHolder.line1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(CardArrayAdapter.this.getContext())
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // call callback
                                service.deleteWish(userId, card.getId(), new Callback<String>() {
                                    @Override
                                    public void success(String s, Response response) {
                                        cardList.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
