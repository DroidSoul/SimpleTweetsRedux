package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bear&bear on 9/24/2017.
 */
@Table(database = MyDatabase.class)
//@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel implements Parcelable {

    public User getUser() {
        return user;
    }

    @Column
    public String body;

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String createdAt;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    public User user;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    public Entity entity;

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
        tweet.setBody(jsonObject.getString("text"));
        tweet.setUid(jsonObject.getLong("id"));
        tweet.setCreatedAt(jsonObject.getString("created_at"));
        tweet.setUser(User.fromJSON(jsonObject.getJSONObject("user")));
        tweet.setEntity(Entity.fromJON(jsonObject.getJSONObject("entities")));
            tweet.save();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                results.add(Tweet.fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    public Tweet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.entity, flags);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.entity = in.readParcelable(Entity.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
