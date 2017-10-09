package com.codepath.apps.restclienttemplate.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.UserTimeLineFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.codepath.apps.restclienttemplate.R.string.tweet;

public class ProfileActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        getSupportActionBar().setTitle(user.screenName);

        populateUserHeadLine(user);

        UserTimeLineFragment userTimeLineFragment = UserTimeLineFragment.newInstance(user.screenName);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimeLineFragment);
        ft.commit();

    }

    public void populateUserHeadLine(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);


        tvName.setText(user.name);
        tvTagLine.setText(user.tagline);
        tvFollowers.setText(user.followersCount + " followers");
        tvFollowing.setText(user.followingCount + " following");

//        Glide.with(this).load(user.profileImageUrl).into(ivProfileImage);
        Glide.with(this).load(user.profileImageUrl).apply(bitmapTransform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))).into(ivProfileImage);
    }
}
