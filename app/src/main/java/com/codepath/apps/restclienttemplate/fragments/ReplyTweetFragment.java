package com.codepath.apps.restclienttemplate.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.TwitterApp;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.ivProfileImage;
import static com.codepath.apps.restclienttemplate.R.id.tvScreenName;
import static com.codepath.apps.restclienttemplate.R.id.tvUserName;

/**
 * Created by bear&bear on 10/7/2017.
 */

public class ReplyTweetFragment extends DialogFragment {

    onFragmentResult activityCommander;
    final static int CharLength = 140;

    private TwitterClient twitterClient;
    User user;
    Tweet tweet;

    ImageButton ibCancel;
    EditText etTweetBody;
    TextView tvReplyTo;
    Button btnTweet;
    TextView tvCharsLeft;

    public ReplyTweetFragment() {
        twitterClient = TwitterApp.getRestClient();
    }

    public static ReplyTweetFragment newInstance(Tweet tweet) {
        ReplyTweetFragment frag = new ReplyTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet", Parcels.wrap(tweet));
        frag.setArguments(args);
        return frag;
    }

    public interface onFragmentResult {
        void returnReplyTweet(Tweet tweet);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            activityCommander = (onFragmentResult) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply_tweet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tweet = Parcels.unwrap(getArguments().getParcelable("tweet"));
        user = tweet.getUser();

        initiateView(view);

        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvCharsLeft.setText(String.valueOf(CharLength));
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                int remainingChars = CharLength - s.length();
                tvCharsLeft.setText(String.valueOf(remainingChars));
                if (remainingChars < 0) {
                    tvCharsLeft.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    btnTweet.setEnabled(false);
                } else {
                    tvCharsLeft.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColor));
                    btnTweet.setEnabled(true);
                }
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                twitterClient.postStatus(etTweetBody.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        Tweet newTweet = Tweet.fromJSON(response);

                        activityCommander.returnReplyTweet(newTweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }, true, user.uid);
                dismiss();
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public void initiateView(View view) {
        ibCancel = (ImageButton) view.findViewById(R.id.ibCancel);
        etTweetBody = (EditText) view.findViewById(R.id.etTweetBody);
        tvReplyTo = (TextView) view.findViewById(R.id.tvReplyTo);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);
        tvReplyTo.setText("In reply to " +  user.name);
        etTweetBody.setText(user.screenName + " ");
        etTweetBody.requestFocus();
        tvCharsLeft.setText(String.valueOf(CharLength - 1 - user.screenName.length()));
    }


}
