package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.max;

public class TimelineActivity extends AppCompatActivity implements NewTweetFragment.onFragmentResult{

    final static int MAXCALL = 5;

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    User authUser;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        tweets = new ArrayList<>();
        rvTweets = (RecyclerView)findViewById(R.id.rvTweets);
        tweetAdapter = new TweetAdapter(this, tweets);
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        client = TwitterApp.getRestClient();
        getUser();
        rvTweets.clearOnScrollListeners();
        populateTimeline(-1);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        });
    }
    private void populateTimeline(final long maxID) {

        client.getHomeTimeline(maxID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                int size = tweets.size();
                //   Log.d("TwitterClient", response.toString());
                if (maxID == -1) {
                    tweets.clear();
//                    tweetAdapter.notifyDataSetChanged();
//                    rvTweets.clearOnScrollListeners();
                }
//                int curSize = tweets.size();
                ArrayList<Tweet> newList = Tweet.fromJSONArray(response);
                tweets.addAll(newList);
//                tweetAdapter.notifyItemRangeInserted(curSize, newList.size());
                tweetAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    public void loadNextDataFromApi(int page) {
        if (page > MAXCALL - 1) {
            return;
        }
        long maxID = tweets.get(tweets.size() - 1).uid - 1;
        populateTimeline(maxID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweets, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {

            FragmentManager fm = getSupportFragmentManager();
            NewTweetFragment newTweetFragment = NewTweetFragment.newInstance(authUser);
            newTweetFragment.show(fm, "fragment_tweet");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void getUser() {
        client.getAuthUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                authUser = new User();
                try {
                    authUser= User.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.d("DEBUG", response.toString());
            }
        });
    }

    @Override
    public void returnData(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        //scroll to top after tweet post
        rvTweets.scrollToPosition(0);
    }
}
