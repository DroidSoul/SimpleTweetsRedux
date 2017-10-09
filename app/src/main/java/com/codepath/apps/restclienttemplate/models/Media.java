package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bear&bear on 9/30/2017.
 */
@Table(database = MyDatabase.class)
public class Media extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey
    public String mediaUrl;

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public static Media fromJSON(JSONObject jsonObject) {
        Media media = new Media();
        try {

//            media.mediaUrl = jsonObject.getString("media_url");
            media.setMediaUrl(jsonObject.getString("media_url"));
            media.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    public static Media fromJSONARRAY(JSONArray jsonArray) {
        Media media = new Media();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).has("media_url")) {
                    String mediaUrl = jsonArray.getJSONObject(i).get("media_url").toString();

                    if (mediaUrl.equals("")) {
                        continue;
                    } else {
                        media.setMediaUrl(mediaUrl);
                        media.save();
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaUrl);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.mediaUrl = in.readString();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
