package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bear&bear on 10/3/2017.
 */

public abstract class TweetsListFragment extends Fragment{

    final static int MAXCALL = 5;

    private TwitterClient client;
    public TweetAdapter tweetAdapter;
    public ArrayList<Tweet> tweets;
    public RecyclerView rvTweets;
    User authUser;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    FragmentManager fragmentManager;

    abstract void populateTimeline(long maxID);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        fragmentManager = getActivity().getSupportFragmentManager();
        tweets = new ArrayList<>();
        rvTweets = (RecyclerView)view.findViewById(R.id.rvTweets);
        tweetAdapter = new TweetAdapter(getContext(), tweets, fragmentManager);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);
        if (!isNetworkAvailable()) {
            readFromDB();
        }
        else {
            getDataFromApi();
        }
    }

    public void readFromDB() {
        Toast.makeText(getContext(), "No Internet. Using offline tweets.", Toast.LENGTH_LONG).show();
        swipeContainer.setEnabled(false);
        // need to only get mentions
/*        List<Tweet> queryResults = SQLite.select().from(Tweet.class).orderBy(Tweet_Table.uid, false).queryList();
        tweets.clear();
        tweets.addAll(queryResults);
        tweetAdapter.notifyDataSetChanged();*/
    }

    public void getDataFromApi() {
        client = TwitterApp.getRestClient();

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
                Intent i = new Intent(getContext(), TweetDetailActivity.class);
                i.putExtra("details", Parcels.wrap(tweet));
                startActivity(i);
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

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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

}
