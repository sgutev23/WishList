package com.hackzurich.wishlist;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.model.WishAndId;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

;

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
        list.setEmptyView(rootView.findViewById(R.id.emptyListOfWishes));

       // refreshAdapter(list, service, getArguments().getString(ARG_USER_ID));
        final String userId = getArguments().getString(ARG_USER_ID);
        refreshAdapter(list, service, userId);

        final Button button = (Button) rootView.findViewById(R.id.button);
        final InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                final EditText text = (EditText) rootView.findViewById(R.id.text);
                final String wishText = text.getText().toString();
                if (wishText == null || wishText.isEmpty()) {
                    return;
                }
                service.createWish(new WishAndId(new Wish(wishText), userId), new Callback<String>() {
                    @Override
                    public void success(String aVoid, Response response) {
                        refreshAdapter(list, service, userId);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                text.setText("");
            }
        });
        return rootView;
    }

    private void refreshAdapter(ListView list, final WishlistBackend service, final String userId) {
        try {
            List<Wish> wishes = (new AsyncTask<Void, Void, List<Wish>>() {

                @Override
                protected List<Wish> doInBackground(Void... voids) {
                    return service.getWishList(userId);
                }
            }).execute().get();

            CardArrayAdapter listAdapter = new CardArrayAdapter(getActivity(), R.layout.list_item_card, wishes, userId);
            list.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
