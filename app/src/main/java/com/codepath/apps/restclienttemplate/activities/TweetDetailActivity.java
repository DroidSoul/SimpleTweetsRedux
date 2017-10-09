package com.codepath.apps.restclienttemplate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TweetDetailActivity extends AppCompatActivity {
    Tweet tweet;
    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvScreenName;
    TextView tvBody;
    ImageView ivImageMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        ivImageMedia = findViewById(R.id.ivImageMedia);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("details"));
        setupView();
    }

    void setupView() {
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText(tweet.user.screenName);
        tvBody.setText(tweet.body);
//        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        Glide.with(this).load(tweet.user.profileImageUrl).apply(bitmapTransform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))).into(ivProfileImage);
        if (tweet.entity != null && tweet.entity.mediaUrl != null) {
        Glide.with(this).load(tweet.entity.mediaUrl).into(ivImageMedia);}
        else {
            ivImageMedia.setImageResource(0);
        }
    }
}
