package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static com.codepath.apps.restclienttemplate.models.User_Table.screenName;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
//	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(TwitterApi.FlickrPerm.WRITE); // Change this
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "QrPbj1UIjKOFoWsy7AHOXgjLr";       // Change this
	public static final String REST_CONSUMER_SECRET = "hFLCJUSpjPgUemNMArUMpJdHgjJFUBgcGk0uIrYmvd5Qwf3WU6"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(long maxID, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
        if (maxID == -1) {
			params.put("since_id", 1);
		}
		else {
			params.put("max_id", maxID);
		}
		client.get(apiUrl, params, handler);
	}

	public void postStatus(String tweetBody, AsyncHttpResponseHandler handler, boolean isReply, long id) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
		if (isReply) {
			params.put("in_reply_to_status_id", id);
		}
		getClient().post(apiUrl, params, handler);
	}

	public void getAuthUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, handler);
	}

	public void getMentionsTimeline(long maxID, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (maxID == -1) {
			params.put("since_id", 1);
		}
		else {
			params.put("max_id", maxID);
		}
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, long maxID, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		if (maxID == -1) {
			params.put("since_id", 1);
		}
		else {
			params.put("max_id", maxID);
		}
		client.get(apiUrl, params, handler);
	}

	public void getSearchResults(long maxID, String query, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("search/tweets.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("q", query);
		params.put("result_type", "popular");
		if (maxID == -1) {
			params.put("since_id", 1);
		}
		else {
			params.put("max_id", maxID);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void favorTweet(AsyncHttpResponseHandler repsonseHandler, Boolean favourite, long id) {

		String apiUrl;

		if(favourite){
			apiUrl = getApiUrl("favorites/destroy.json");
		}else{
			apiUrl = getApiUrl("favorites/create.json");
		}
		//specify the params
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, repsonseHandler);
	}

	public void reTweet(AsyncHttpResponseHandler repsonseHandler, Boolean reTweet, long id) {
		String apiUrl;
		if(reTweet) {
			apiUrl = getApiUrl("statuses/unretweet/:id.json");
		}else{
			apiUrl = getApiUrl("statuses/retweet/:id.json");
		}
		//specify the params
		apiUrl = apiUrl.replace(":id", String.valueOf(id));
		getClient().post(apiUrl, null, repsonseHandler);
	}


	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
