package io.github.entertainmatch.firebase.models;

import android.support.annotation.Nullable;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseCategoriesTemplatesController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.utils.ListExt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * The reason for this object is to provide Firebase-friendly implementation of Poll.
 * This is due to Firebase limitations that might occur in the future.
 * One of them is the serialization of Java Arrays. Firebase is not capable of doing that.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FirebasePoll {
    public static final String NO_USER_VOTE = "-1";
    /**
     * Users who participate in the poll
     */
    private List<String> participants;

    /**
     * Name of the poll.
     */
    private String name;

    /**
     * Identifier of the poll
     */
    private String pollId;

    /**
     * Current stage
     */
    private String stage;

    /**
     * Maps categoryId to number of votes
     */
    private HashMap<String, Integer> voteCounts = new HashMap<>();

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    private Map<String, String> votedFor = new HashMap<>();
    private Map<String, List<String>> remainingEventChoices = new HashMap<>();
    private Map<String, String> eventVotes = new HashMap<>();
    private String chosenCategory = "";

    private String victoriousEvent = "";
    /**
     * Maps location id to user selection status (facebookId to boolean mapping)
     * Additionally under votes key keeps information about users that already voted.
     */
    private Map<String, HashMap<String, Boolean>> eventDatesStatus;

    /**
     * Location that has been chosen in event date stage
     */
    private String chosenLocationId = "";

    /**
     * List of event ids to vote.
     * Reduced when tie occur.
     */
    private List<String> eventsToVote;
    @Nullable
    private String drawableUri = null;

    /**
     * Construct Firebase Poll from a Poll object that is used throughout the application.
     * @param pollStub Poll to convert
     * @return FirebasePoll used in the cloud
     */
    public static FirebasePoll fromPoll(String hostFacebookId, PollStub pollStub, String pollId) {
        return new FirebasePoll(hostFacebookId, pollStub, pollId);
    }

    public FirebasePoll(String hostFacebookId, PollStub pollStub, String pollId) {
        participants = ListExt.map(Arrays.asList(pollStub.getMembers()), Person::getFacebookId);
        participants.add(hostFacebookId);

        voteCounts = new HashMap<>();
        votedFor = new HashMap<>();
        eventVotes = new HashMap<>();
        eventDatesStatus = new HashMap<>();

        for (Category category : FirebaseCategoriesTemplatesController.getCached()) {
            voteCounts.put(category.getId(), 0);
        }

        for (String facebookId : participants)
        {
            votedFor.put(facebookId, NO_USER_VOTE);
            eventVotes.put(facebookId, NO_USER_VOTE);
        }
        name = pollStub.getName();
        this.pollId = pollId;
        stage = VoteCategoryStage.class.toString();
        eventsToVote = Collections.emptyList();
    }

    /**
     * Registers vote for category in firebase
     * @param category Category voted for by current user
     */
    public void voteCategory(Category category, String city) {
        String itemId = category.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        FirebasePollController.vote(pollId, facebookId, itemId, city);
    }

    /**
     * Updates vote values in given category object based on values cached in this poll object.
     * @param amendedCategory Category object to voteCategory votes for.
     */
    public void setValues(Category amendedCategory) {
        String itemId = amendedCategory.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        amendedCategory.setVotedFor(getVotedFor().get(facebookId).equals(itemId));
        amendedCategory.setVoteCount(getVoteCounts().get(itemId));
    }

    /**
     * Updates poll values based on poll retrieved from firebase
     * @param updatedPoll
     */
    public void update(FirebasePoll updatedPoll) {
        stage = updatedPoll.stage;
        voteCounts = updatedPoll.voteCounts;
        votedFor = updatedPoll.votedFor;
        remainingEventChoices = updatedPoll.remainingEventChoices;
        eventVotes = updatedPoll.eventVotes;
        chosenCategory = updatedPoll.chosenCategory;
        victoriousEvent = updatedPoll.victoriousEvent;
        eventDatesStatus = updatedPoll.eventDatesStatus;
        drawableUri = updatedPoll.drawableUri;
    }

    /**
     * Changes status of items selected by user in firebase
     * @param selections User selections - eventId to isRemaining mapping
     */
    public void updateRemainingEvents(Map<String, Boolean> selections) {
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        FirebasePollController.updateRemainingEvents(pollId, facebookId, selections);
    }

    /**
     * Registers vote for event in firebase
     * @param event Event voted for by current user
     */
    public void voteEvent(Event event) {
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        FirebasePollController.voteEvent(pollId, facebookId, event);
    }

    /**
     * Updates firebase with information about dates that suit user
     * @param locationIds Location identifiers
     * @param selections Corresponding to locations selection flags (isChosen)
     */
    public void chooseDate(List<String> locationIds, List<Boolean> selections) {
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        ListExt.zippedForeach(locationIds, selections, (l, s) -> FirebasePollController.chooseDate(pollId, l, facebookId, s));
        FirebasePollController.dateVotingFinished(pollId, facebookId);
    }
}
