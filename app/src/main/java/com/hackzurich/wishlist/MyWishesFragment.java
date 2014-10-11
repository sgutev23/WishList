package com.hackzurich.wishlist;
;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hackzurich.wishlist.model.Card.Card;
import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by cotizo on 10/11/2014.
 */
public class MyWishesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_USER_ID = "user_id";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyWishesFragment newInstance(int sectionNumber, String userId) {
        MyWishesFragment fragment = new MyWishesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public MyWishesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint))
                .build();
        final WishlistBackend service = restAdapter.create(WishlistBackend.class);

        final View rootView = inflater.inflate(R.layout.fragment_wishes, container, false);
        final ListView list = (ListView) rootView.findViewById(R.id.card_listView);

       // refreshAdapter(list, service, getArguments().getString(ARG_USER_ID));

        final Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String wishText = ((EditText) rootView.findViewById(R.id.text)).getText().toString();
                service.createWish(new Wish(wishText), new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        refreshAdapter(list, service, getArguments().getString(ARG_USER_ID));
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
        return rootView;
    }

    private void refreshAdapter(ListView list, final WishlistBackend service, final String userId) {
        try {
            List<Card> wishes = (new AsyncTask<Void, Void, List<Card>>() {

                @Override
                protected List<Card> doInBackground(Void... voids) {
                    List<Card> result = new ArrayList<Card>();
                    for (Wish w: service.getWishList(userId)) {
                        result.add(new Card(w.getContent(), "test"));
                    }
                    return result;
                }
            }).execute().get();

            ArrayAdapter<Card> listAdapter = new CardArrayAdapter(getActivity(), R.layout.list_item_card, wishes);
            list.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
