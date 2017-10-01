package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.Tweet_Table;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements NewTweetFragment.onFragmentResult{

    final static int MAXCALL = 5;

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    User authUser;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

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
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        if (!isNetworkAvailable()) {
            readFromDB();
        }
        else {
            getDataFromApi();
        }
    }

    public void readFromDB() {
        Toast.makeText(this, "No Internet. Using offline tweets.", Toast.LENGTH_LONG).show();
        swipeContainer.setEnabled(false);
        List<Tweet> queryResults = SQLite.select().from(Tweet.class).orderBy(Tweet_Table.uid, false).queryList();
        tweets.clear();
        tweets.addAll(queryResults);
        tweetAdapter.notifyDataSetChanged();
    }

    public void getDataFromApi() {
        client = TwitterApp.getRestClient();
        getUser();
        populateTimeline(-1);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(-1);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Tweet tweet = tweets.get(position);
                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra("details", Parcels.wrap(tweet));
                startActivity(i);
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

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
