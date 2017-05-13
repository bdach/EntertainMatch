package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import io.github.entertainmatch.R;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Person implements Parcelable {
    /**
     * The Facebook ID of the person.
     */
    public String facebookId = "";
    /**
     * Name of the person.
     */
    public String name;
    /**
     * Indicates whether a person has a custom profile picture.
     */
    public boolean profilePictureSet = false;
    /**
     * URL of the person's profile picture.
     */
    public String profilePictureUrl = "";

    public Person(JSONObject jsonObject) throws JSONException {
        this.facebookId = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        try {
            JSONObject picture = jsonObject
                    .getJSONObject("picture")
                    .getJSONObject("data");
            this.profilePictureSet = !picture.getBoolean("is_silhouette");
            this.profilePictureUrl = picture.getString("url");
        } catch (JSONException ex) {
            Log.d("Person", "No picture object received");
        }
    }

    protected Person(Parcel in) {
        facebookId = in.readString();
        name = in.readString();
        profilePictureSet = in.readInt() != 0;
        profilePictureUrl = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facebookId);
        dest.writeString(name);
        dest.writeInt(profilePictureSet ? 1 : 0);
        dest.writeString(profilePictureUrl);
    }
}
