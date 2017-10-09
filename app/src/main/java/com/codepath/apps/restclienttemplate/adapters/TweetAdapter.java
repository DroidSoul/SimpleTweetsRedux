package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.fragments.ReplyTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.R.attr.bitmap;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.codepath.apps.restclienttemplate.R.id.ivReply;
import static com.codepath.apps.restclienttemplate.R.id.tvLikeCount;


/**
 * Created by bear&bear on 9/25/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;
    TwitterClient client;
    FragmentManager mFragmentManager;

    public TweetAdapter(Context context, List<Tweet> tweets, FragmentManager fragmentManager) {
        this.mTweets = tweets;
        mContext = context;
        client = TwitterApp.getRestClient();
        this.mFragmentManager = fragmentManager;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvRelativeTime;
        TextView tvBody;
        ImageView ivImageMedia;
        ImageView  ivReply;
        ImageView ivIsRetweet;
        TextView tvRetweetCount;
        ImageView ivIsLike;
        TextView tvLikeCount;


        ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            ivImageMedia = itemView.findViewById(R.id.ivImageMedia);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivIsRetweet = itemView.findViewById(R.id.ivIsRetweet);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            ivIsLike = itemView.findViewById(R.id.ivIsLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Tweet tweet = mTweets.get(position);
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvScreenName.setText(tweet.user.screenName);
        Glide.with(mContext).load(tweet.user.profileImageUrl).apply(bitmapTransform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))).into(holder.ivProfileImage);
        if (tweet.entity != null && tweet.entity.mediaUrl != null) {
            Glide.with(mContext).load(tweet.entity.mediaUrl).into(holder.ivImageMedia);
//            Glide.with(mContext).load(tweet.entity.mediaUrl).apply(bitmapTransform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.ALL))).into(holder.ivImageMedia);
        }
        else {
            holder.ivImageMedia.setImageResource(0);
        }

        holder.tvLikeCount.setText("");
        holder.tvRetweetCount.setText("");

        if (tweet.retweetCount > 0) {
            holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        }

        if (tweet.likeCount > 0) {
            holder.tvLikeCount.setText(String.valueOf(tweet.likeCount));

        }

        holder.ivIsLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_gray_24dp));
        holder.ivIsRetweet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_repeat_gray_24dp));

        if(tweet.isLike){
            holder.ivIsLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }
        if(tweet.isRetweet){
            holder.ivIsRetweet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
        }

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("user", Parcels.wrap(tweet.getUser()));
                mContext.startActivity(intent);
            }
        });
        holder.ivIsLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorTweet(tweet, holder.tvLikeCount, holder.ivIsLike);

            }
        });
        holder.ivIsRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reTweet(tweet, holder.tvRetweetCount, holder.ivIsRetweet);

            }
        });
        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyTweetFragment myDialog = ReplyTweetFragment.newInstance(tweet);
                myDialog.show(mFragmentManager, "reply tweet");
            }
        });
    }

    private void favorTweet(final Tweet tweet, final TextView tvFavorCount, final ImageView ivFavor) {

        client.favorTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getBoolean("favorited")){
                        tweet.setLikeCount(Integer.parseInt(response.getString("favorite_count")));
                        ivFavor.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                    }else{
                        ivFavor.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_gray_24dp));
                    }
                    tweet.setIsLike(response.getBoolean("favorited"));
                    if(response.getLong("favorite_count") > 0) {
                        tvFavorCount.setText(String.valueOf(response.getLong("favorite_count")));
                    }else{
                        tvFavorCount.setText("");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        }, tweet.isLike, tweet.uid);

    }

    private void reTweet(final Tweet tweet, final TextView tvRetweetCount, final ImageView ivRetweetCount) {

        client.reTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "retweeted" + response.toString());
                try {
                    if(response.getBoolean("retweeted")){
                        tweet.setRetweetCount(Integer.parseInt(response.getString("retweet_count")));
                        ivRetweetCount.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                    }else{
                        ivRetweetCount.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_repeat_gray_24dp));
                    }
                    tweet.setIsRetweet(response.getBoolean("retweeted"));
                    if(response.getLong("retweet_count") > 0) {
                        tvRetweetCount.setText(String.valueOf(response.getLong("retweet_count")));
                    }else{
                        tvRetweetCount.setText("");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", responseString);

            }
        }, tweet.isRetweet, tweet.uid);}

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
