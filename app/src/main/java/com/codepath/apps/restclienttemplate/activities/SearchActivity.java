package com.codepath.apps.restclienttemplate.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.SearchListFragment;

public class SearchActivity extends AppCompatActivity {
    String query;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        query = getIntent().getStringExtra("q");
        getSupportActionBar().setTitle("popular tweets for " + query);
        fm = getSupportFragmentManager();
        loadFragment(query);

    }
    public void loadFragment(String query) {
        SearchListFragment frag = SearchListFragment.newInstance(query);
        //display user fragment dynamically within this activity
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flContainer, frag);
        ft.commit();
    }
}
