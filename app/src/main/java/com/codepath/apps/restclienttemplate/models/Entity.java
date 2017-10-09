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

/**
 * Created by bear&bear on 9/30/2017.
 */
@Table(database = MyDatabase.class)
public class Entity extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey
//    @ForeignKey(saveForeignKeyModel = false)
    public String mediaUrl;

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public static Entity fromJON(JSONObject jsonObject) {
        Entity entity = new Entity();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("media");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).has("media_url")) {
                        String url = jsonArray.getJSONObject(i).get("media_url").toString();

                        if (url.equals("")) {
                            continue;
                        } else {
                            entity.setMediaUrl(url);
                            entity.save();
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public Entity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaUrl);
    }

    protected Entity(Parcel in) {
        this.mediaUrl = in.readString();
    }

    public static final Creator<Entity> CREATOR = new Creator<Entity>() {
        @Override
        public Entity createFromParcel(Parcel source) {
            return new Entity(source);
        }

        @Override
        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
}
