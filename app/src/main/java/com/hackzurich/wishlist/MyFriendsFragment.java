package com.hackzurich.wishlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hackzurich.wishlist.model.UserNameIdPic;
import com.hackzurich.wishlist.rest.WishlistBackend;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.RestAdapter;

/**
 * Created by cotizo on 10/11/2014.
 */
public class MyFriendsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_USER_ID = "user_id";

    private String userId = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyFriendsFragment newInstance(int sectionNumber, String userId) {
        MyFriendsFragment fragment = new MyFriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userId = getArguments().getString(ARG_USER_ID);
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint))
                .build();

        final WishlistBackend service = restAdapter.create(WishlistBackend.class);
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        final ListView list = (ListView) rootView.findViewById(R.id.list);
        try {
            list.setAdapter(new FriendListAdapter(inflater, service, userId));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    class FriendListAdapter extends BaseAdapter {

        private final List<UserNameIdPic> users;
        private final LayoutInflater inflater;

        public FriendListAdapter(final LayoutInflater inflater,List<UserNameIdPic> ids) {
            this.users = ids;
            this.inflater = inflater;
        }

        public FriendListAdapter(final LayoutInflater inflater, final WishlistBackend service, final String userId) throws ExecutionException, InterruptedException {
            this.inflater = inflater;
            this.users = new AsyncTask<Void, Void, List<UserNameIdPic>>() {

                @Override
                protected List<UserNameIdPic> doInBackground(Void... voids) {
                    return service.getFriendList(userId);
                }
            }.execute().get();
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int i) {
            return users.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View root = this.inflater.inflate(R.layout.row, viewGroup, false);
            TextView tv = (TextView) root.findViewById(R.id.rowTextView);
            tv.setText(users.get(i).getName());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toActivity = new Intent(MyFriendsFragment.this.getActivity(), FriendWishlistActivity.class);
                    toActivity.putExtra(FriendWishlistActivity.USER_ID, users.get(i).getId());
                    toActivity.putExtra(FriendWishlistActivity.USER_NAME, users.get(i).getName());
                    toActivity.putExtra(FriendWishlistActivity.MY_ID, userId);
                    toActivity.putExtra(FriendWishlistActivity.NAME_MAP, getNameMap());
                    startActivity(toActivity);
                }
            });
            return root;
        }

        Bundle getNameMap() {
            Bundle result = new Bundle();
            for (UserNameIdPic uni: this.users) {
                result.putString(uni.getId(), uni.getName());
            }
            result.putString(userId, "me");
            return result;
        }
    }
}
