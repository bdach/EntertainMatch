package io.github.entertainmatch.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.facebook.GraphRequest;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FriendsProvider;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class used to display popups with lists of participating
 * users in all vote stage views.
 *
 * @author Bartlomiej Dach
 * @since 14.05.17
 */
public class ParticipantList {
    /**
     * {@link Context} to use when creating the popup.
     */
    private final Context context;

    /**
     * List of Facebook IDs of the users participating in a poll.
     */
    private final List<String> idList;

    /**
     * List of names of users fetched from the Facebook API.
     */
    private final List<String> nameList;

    /**
     * Constructor.
     * @param context Context to create the popup in.
     * @param poll A {@link FirebasePoll} object to get data from.
     */
    public ParticipantList(Context context, FirebasePoll poll) {
        this.context = context;
        this.idList = poll.getParticipants();
        this.nameList = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param context Context to create the popup in.
     * @param completedPoll A {@link FirebaseCompletedPoll} to get data from.
     */
    public ParticipantList(Context context, FirebaseCompletedPoll completedPoll) {
        this.context = context;
        this.idList = completedPoll.goingList();
        this.nameList = new ArrayList<>();
    }

    /**
     * Fetches the names of users using the Facebook API.
     */
    public void fetchNames() {
        nameList.clear();
        if (idList.isEmpty()) return;
        List<GraphRequest> requests = FriendsProvider.getFriendsById(idList, response -> {
            try {
                JSONObject responseObject = response.getJSONObject();
                String name = responseObject.getString("name");
                nameList.add(name);
            } catch (JSONException e) {
                Log.e("ParticipantList", "Displaying list failed", e);
            }
        });
        GraphRequest.executeBatchAsync(requests);
    }

    /**
     * Gets an {@link AlertDialog} that can be displayed in an activity.
     * @return An instance of {@link AlertDialog} containing names of participants
     * fetched from the database.
     */
    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.list_of_participants)
                .setItems(nameList.toArray(new String[nameList.size()]), null);
        return builder.create();
    }
}
