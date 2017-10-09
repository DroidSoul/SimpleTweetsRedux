package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.Tweet_Table;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bear&bear on 10/3/2017.
 */

public class MentionsTimeLineFragment extends TweetsListFragment {

    private TwitterClient client;

    public MentionsTimeLineFragment() {

    }

    public static MentionsTimeLineFragment newInstance(FragmentManager fragmentManager) {
        MentionsTimeLineFragment frag = new MentionsTimeLineFragment();
        frag.fragmentManager = fragmentManager;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }

    @Override
    void populateTimeline(final long maxID) {

        client.getMentionsTimeline(maxID, new JsonHttpResponseHandler() {

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
