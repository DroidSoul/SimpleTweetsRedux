package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.HomeTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimeLineFragment;

/**
 * Created by bear&bear on 10/5/2017.
 */

public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

    private static final int NUM_ITEMS = 2;
    private String[] tabTitles = new String[]{"home", "mentions"};
    private Context context;
    private FragmentManager fm;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return HomeTimeLineFragment.newInstance(fm);
        } else if (position == 1) {
            return MentionsTimeLineFragment.newInstance(fm);
        }
        else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
