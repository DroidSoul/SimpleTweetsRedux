package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.HomeTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.ReplyTweetFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;

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

import static com.codepath.apps.restclienttemplate.R.id.rvTweets;
import static com.codepath.apps.restclienttemplate.R.id.swipeContainer;
import static com.codepath.apps.restclienttemplate.R.string.tweet;

public class TimelineActivity extends AppCompatActivity implements NewTweetFragment.onFragmentResult, ReplyTweetFragment.onFragmentResult{

    private TwitterClient client;
    User authUser;
    HomeTimeLineFragment homeTimeLineFragment;
    ViewPager vpPager;
    TweetsPagerAdapter tweetsPagerAdapter;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        vpPager = findViewById(R.id.viewpager);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(tweetsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        client = TwitterApp.getRestClient();
        getUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweets, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchView.clearFocus();
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("q", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
        if (id == R.id.miPorfile) {

            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("user", Parcels.wrap(authUser));
            startActivity(i);
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
        homeTimeLineFragment = (HomeTimeLineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
        homeTimeLineFragment.insertTweetAtTop(tweet);
        vpPager.setCurrentItem(0);
    }

    @Override
    public void returnReplyTweet(Tweet tweet) {
        homeTimeLineFragment = (HomeTimeLineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
        homeTimeLineFragment.insertTweetAtTop(tweet);
        vpPager.setCurrentItem(0);
    }
}
