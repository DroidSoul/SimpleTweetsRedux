package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by bear&bear on 9/24/2017.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel implements Parcelable {


    @Column
    public String name;

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String screenName;

    @Column
    public String profileImageUrl;

    public String tagline;
    public int followersCount;
    public int followingCount;

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException{
        User user = new User();
//        user.name = jsonObject.getString("name");
        user.setName(jsonObject.getString("name"));
//        user.uid = jsonObject.getLong("id");
        user.setUid(jsonObject.getLong("id"));
//        user.screenName = "@" + jsonObject.getString("screen_name");
        user.setScreenName("@" + jsonObject.getString("screen_name"));
 //       user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.setProfileImageUrl(jsonObject.getString("profile_image_url"));

        user.tagline = jsonObject.getString("description");
        user.followersCount = jsonObject.getInt("followers_count");
        user.followingCount = jsonObject.getInt("friends_count");
        user.save();
        return user;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.tagline);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingCount);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.tagline = in.readString();
        this.followersCount = in.readInt();
        this.followingCount = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
