package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.fragment;

/**
 * Created by bear&bear on 10/8/2017.
 */

public class SearchListFragment extends TweetsListFragment {

    private TwitterClient client;
//    String query;



    public SearchListFragment() {

    }

/*    public static SearchListFragment newInstance(FragmentManager fragmentManager, String query) {
        SearchListFragment frag = new SearchListFragment();
        frag.fragmentManager = fragmentManager;
        frag.query = query;
        return frag;
    }*/

    public static SearchListFragment newInstance(String query) {
        SearchListFragment frag = new SearchListFragment();
        Bundle args = new Bundle();
        args.putString("q", query);
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
        String query = getArguments().getString("q");
        client.getSearchResults(maxID, query, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (maxID == -1) {
                    tweets.clear();
                    tweetAdapter.notifyDataSetChanged();
                    scrollListener.resetState();
//                    rvTweets.clearOnScrollListeners();
                }

                ArrayList<Tweet> newList = null;
                try {
                    newList = Tweet.fromJSONArray(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                };
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
