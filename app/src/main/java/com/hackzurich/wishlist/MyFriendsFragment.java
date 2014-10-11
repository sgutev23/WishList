package com.hackzurich.wishlist;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.RestAdapter;

/**
 * Created by cotizo on 10/11/2014.
 */
public class MyFriendsFragment extends Fragment{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyFriendsFragment newInstance(int sectionNumber) {
        MyFriendsFragment fragment = new MyFriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://cotizo.net:3000/")
                .build();
        final WishlistBackend service = restAdapter.create(WishlistBackend.class);
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        final ListView list = (ListView) rootView.findViewById(R.id.list);
        try {
            list.setAdapter(new FriendListAdapter(inflater, service));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    class FriendListAdapter extends BaseAdapter {

        private final List<String> ids;
        private final LayoutInflater inflater;

        public FriendListAdapter(final LayoutInflater inflater,List<String> ids) {
            this.ids = ids;
            this.inflater = inflater;
        }

        public FriendListAdapter(final LayoutInflater inflater, final WishlistBackend service) throws ExecutionException, InterruptedException {
            this.inflater = inflater;
            this.ids = new AsyncTask<Void, Void, List<String>>() {

                @Override
                protected List<String> doInBackground(Void... voids) {
                    List<String> converted = new LinkedList<String>();
                    for (Integer f: service.getFriendList()) {
                        converted.add(f.toString());
                    }
                    return converted;
                }
            }.execute().get();
        }

        @Override
        public int getCount() {
            return ids.size();
        }

        @Override
        public Object getItem(int i) {
            return ids.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View root = this.inflater.inflate(R.layout.row, viewGroup, false);
            TextView tv = (TextView) root.findViewById(R.id.rowTextView);
            tv.setText(ids.get(i));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toActivity = new Intent(MyFriendsFragment.this.getActivity(), FriendWishlistActivity.class);
                    startActivity(toActivity);
                }
            });
            return root;
        }
    }
}
