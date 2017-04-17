package io.github.entertainmatch.facebook.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 4/5/17.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FacebookUser {
    public String facebookId;
    public String name;
    public String gender;

    public static FacebookUser fromJSON(JSONObject object) throws JSONException {
        return new FacebookUser(
            object.getString("id"),
            object.getString("name"),
            object.getString("gender")
        );
    }
}
