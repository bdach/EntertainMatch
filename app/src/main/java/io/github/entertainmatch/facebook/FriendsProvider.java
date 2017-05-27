package io.github.entertainmatch.facebook;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import io.github.entertainmatch.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class used for fetching information about friends using the Android Facebook API.
 *
 * @author Bartlomiej Dach
 * @since 17.04.17
 */
public class FriendsProvider {
    /**
     * Returns an executable {@link GraphRequest} fetching a user's friends list.
     * @param callback The callback function to execute upon request completion.
     * @return An instance of {@link GraphRequest} ready to execute.
     */
    public static GraphRequest getFriendsList(GraphRequest.GraphJSONArrayCallback callback) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return getFriendsList(callback, token);
    }

    /**
     * Returns an executable {@link GraphRequest} fetching a user's friends list.
     * @param callback The callback function to execute upon request completion.
     * @param token Access token to use when authenticating with the Facebook API.
     * @return An instance of {@link GraphRequest} ready to execute.
     */
    @VisibleForTesting
    static GraphRequest getFriendsList(GraphRequest.GraphJSONArrayCallback callback, AccessToken token) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(token, callback);
        Bundle params = new Bundle();
        params.putString("fields", "name,id,picture");
        request.setParameters(params);
        return request;
    }

    /**
     * Returns a list of {@link GraphRequest}s returning information about the
     * friends with the supplied IDs.
     * @param ids A {@link List} of Facebook IDs as strings.
     * @param callback The callback to execute upon each of the requests' completion.
     * @return A {@link List} of {@link GraphRequest}s for user data.
     */
    public static List<GraphRequest> getFriendsById(List<String> ids, GraphRequest.Callback callback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return getFriendsById(ids, callback, accessToken);
    }

    /**
     * Returns a list of {@link GraphRequest}s returning information about the
     * friends with the supplied IDs.
     * @param ids A {@link List} of Facebook IDs as strings.
     * @param callback The callback to execute upon each of the requests' completion.
     * @param token Access token to use when authenticating with the Facebook API.
     * @return A {@link List} of {@link GraphRequest}s for user data.
     */
    @VisibleForTesting
    static List<GraphRequest> getFriendsById(List<String> ids,
                                             GraphRequest.Callback callback,
                                             AccessToken token) {
        List<GraphRequest> requests = new ArrayList<>();
        for (String id : ids) {
            GraphRequest request = new GraphRequest(
                    token,
                    "/" + id,
                    null,
                    HttpMethod.GET,
                    callback
            );
            requests.add(request);
        }
        return requests;
    }

    /**
     * Transforms a {@link JSONArray} returned by
     * {@link #getFriendsList(GraphRequest.GraphJSONArrayCallback)}
     * to an {@link ArrayList} of {@link Person} objects.
     * @param friends The incoming {@link JSONArray}.
     * @return A list of {@link Person} object with friend data.
     */
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
