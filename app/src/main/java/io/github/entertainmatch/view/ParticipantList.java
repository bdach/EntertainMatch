package io.github.entertainmatch.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.facebook.GraphRequest;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FriendsProvider;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.utils.HashMapExt;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Bartlomiej Dach
 * @since 14.05.17
 */
public class ParticipantList {
    private final Context context;
    private final List<String> idList;
    private final List<String> nameList;

    public ParticipantList(Context context, FirebasePoll poll) {
        this.context = context;
        this.idList = poll.getParticipants();
        this.nameList = new ArrayList<>();
    }

    public ParticipantList(Context context, FirebaseCompletedPoll completedPoll) {
        this.context = context;
        this.idList = completedPoll.goingList();
        this.nameList = new ArrayList<>();
    }

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

    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.list_of_participants)
                .setItems(nameList.toArray(new String[nameList.size()]), null);
        return builder.create();
    }
}
