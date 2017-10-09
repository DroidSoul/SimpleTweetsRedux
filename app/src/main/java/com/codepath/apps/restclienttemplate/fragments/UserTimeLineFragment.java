package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bear&bear on 10/5/2017.
 */

public class UserTimeLineFragment extends TweetsListFragment {
    private TwitterClient client;



    public UserTimeLineFragment() {

    }

    public static UserTimeLineFragment newInstance(String screenName) {
        UserTimeLineFragment frag = new UserTimeLineFragment();
//        frag.fragmentManager = fragmentManager;
        Bundle args = new Bundle();
        args.putString("screen_name",screenName);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }

    @Override
    void populateTimeline(final long maxID) {
        String screenName = getArguments().getString("screen_name");

        client.getUserTimeline(screenName, maxID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                int size = tweets.size();
                //   Log.d("TwitterClient", response.toString());
                if (maxID == -1) {
                    tweets.clear();
                    tweetAdapter.notifyDataSetChanged();
                    scrollListener.resetState();
//                    rvTweets.clearOnScrollListeners();
                }
//                int curSize = tweets.size();
                ArrayList<Tweet> newList = Tweet.fromJSONArray(response);
                tweets.addAll(newList);
//                tweetAdapter.notifyItemRangeInserted(curSize, newList.size());
                tweetAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

}
