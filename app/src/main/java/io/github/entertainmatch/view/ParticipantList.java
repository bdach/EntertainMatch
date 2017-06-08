package io.github.entertainmatch.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import com.facebook.GraphRequest;
import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FriendsProvider;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteEventStage;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final boolean[] votedList;

    @Inject
    FriendsProvider friendsProvider;

    /**
     * Constructor.
     * @param context Context to create the popup in.
     * @param poll A {@link FirebasePoll} object to get data from.
     */
    public ParticipantList(Context context, FirebasePoll poll) {
        this.context = context;
        this.idList = poll.getParticipants();
        this.nameList = new ArrayList<>();
        if (poll.getStage().equals(VoteCategoryStage.class.toString()) && poll.getVotedFor() != null) {
            votedList = generateVotedList(poll.getVotedFor());
        } else if (poll.getStage().equals(VoteEventStage.class.toString()) && poll.getEventVotes() != null) {
            votedList = generateVotedList(poll.getEventVotes());
        } else if (poll.getEventDatesStatus() != null) {
            votedList = generateVotedListForDates(poll.getEventDatesStatus());
        } else {
            votedList = new boolean[idList.size()];
        }
        DaggerApplication.getApp().getFacebookComponent().inject(this);
    }

    private boolean[] generateVotedListForDates(Map<String, HashMap<String, Boolean>> eventDatesStatus) {
        HashMap<String, Boolean> map = new HashMap<>();
        if (eventDatesStatus != null && eventDatesStatus.containsKey("voted")) {
            map = eventDatesStatus.get("voted");
        }
        return generateVotedListBoolean(map);
    }

    private boolean[] generateVotedListBoolean(Map<String, Boolean> map) {
        boolean[] list = new boolean[idList.size()];
        for (int i = 0; i < idList.size(); ++i) {
            if (map.containsKey(idList.get(i))) {
                list[i] = map.get(idList.get(i));
            }
        }
        return list;
    }

    private boolean[] generateVotedList(Map<String, String> votedFor) {
        boolean[] list = new boolean[idList.size()];
        if (votedFor.size() < idList.size()) return list;
        for (int i = 0; i < idList.size(); ++i) {
            if (votedFor.containsKey(idList.get(i))) {
                list[i] = !votedFor.get(idList.get(i)).equals(FirebasePoll.NO_USER_VOTE);
            }
        }
        return list;
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
        if (completedPoll.getGoing() != null) {
            this.votedList = generateVotedListBoolean(completedPoll.getGoing());
        } else {
            votedList = new boolean[idList.size()];
        }
        DaggerApplication.getApp().getFacebookComponent().inject(this);
    }

    /**
     * Fetches the names of users using the Facebook API.
     */
    public void fetchNames() {
        nameList.clear();
        if (idList.isEmpty()) return;
        List<GraphRequest> requests = friendsProvider.getFriendsById(idList, response -> {
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
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.list_of_participants)
                .setMultiChoiceItems(
                        nameList.toArray(new String[nameList.size()]),
                        votedList,
                        null
                );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ListView listView = alertDialog.getListView();
        listView.setEnabled(false);
    }
}
