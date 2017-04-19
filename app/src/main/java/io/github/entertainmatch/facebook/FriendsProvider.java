package io.github.entertainmatch.facebook;

import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import io.github.entertainmatch.model.Person;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Bartlomiej Dach
 * @since 17.04.17
 */
public class FriendsProvider {
    public static GraphRequest getFriendsList(GraphRequest.GraphJSONArrayCallback callback) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMyFriendsRequest(token, callback);
        Bundle params = new Bundle();
        params.putString("fields", "name,id,picture");
        return request;
    }

    public static ArrayList<Person> arrayFromJson(JSONArray friends) {
        ArrayList<Person> people = new ArrayList<>();
        try {
            people.ensureCapacity(friends.length());
            for (int i = 0; i < friends.length(); ++i) {
                JSONObject jsonObject = friends.getJSONObject(i);
                Person person = new Person(jsonObject);
                people.add(person);
            }
        } catch (Exception e) {
            Log.w("FriendsProvider", e);
        }
        return people;
    }
}
