package com.codepath.apps.restclienttemplate.fragments;

import android.preference.PreferenceActivity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.models.Tweet.fromJSON;

/**
 * Created by bear&bear on 9/27/2017.
 */

public class NewTweetFragment extends DialogFragment {

    onFragmentResult activityCommander;
    final static int CharLength = 140;

    private TwitterClient twitterClient;
    User authUser;

    ImageButton ibCancel;
    ImageView ivProfileImage;
    EditText etTweetBody;
    TextView tvUserName;
    TextView tvScreenName;
    Button btnTweet;
    TextView tvCharsLeft;

    public NewTweetFragment() {
//        twitterClient = TwitterApp.getRestClient();
    }
    public static NewTweetFragment newInstance(User user) {
        NewTweetFragment frag = new NewTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        frag.setArguments(args);
        return frag;
    }

    public interface onFragmentResult {
        void returnData(Tweet tweet);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authUser = Parcels.unwrap(getArguments().getParcelable("user"));
        twitterClient = TwitterApp.getRestClient();
//        getUser();
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
                tvCharsLeft.setText(Integer.toString(remainingChars));
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

                        activityCommander = (onFragmentResult) getActivity();
                        activityCommander.returnData(newTweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
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
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        etTweetBody = (EditText) view.findViewById(R.id.etTweetBody);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);
        tvUserName.setText(authUser.name);
        tvScreenName.setText(authUser.screenName);
        Glide.with(getActivity()).load(authUser.profileImageUrl)
                .fitCenter().centerCrop()
                .into(ivProfileImage);
    }
    public void getUser() {
        twitterClient.getAuthUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log("debug")
                authUser = new User();
                try {
                    authUser = User.fromJSON(response);
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
}
