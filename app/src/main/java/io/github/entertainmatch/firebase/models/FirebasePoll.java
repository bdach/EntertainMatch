package io.github.entertainmatch.firebase.models;

import java.util.*;

import android.media.FaceDetector;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebaseEventDateController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.model.*;
import io.github.entertainmatch.utils.ListExt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Map<String, Integer> voteCounts = new HashMap<>();

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    private Map<String, String> votedFor = new HashMap<>();
    private Map<String, Map<String, Boolean>> remainingEventChoices = new HashMap<>();
    private Map<String, String> eventVotes = new HashMap<>();
    private String chosenCategory;

    private String victoriousEvent;
    /**
     * Maps location id to user selection status (facebookId to boolean mapping)
     * Additionally under votes key keeps information about users that already voted.
     */
    private Map<String, HashMap<String, Boolean>> eventDatesStatus;
    /**
     * Construct Firebase Poll from a Poll object that is used throughout the application.
     * @param pollStub Poll to convert
     * @return FirebasePoll used in the cloud
     */
    public static FirebasePoll fromPoll(String hostFacebookId, PollStub pollStub, String pollId) {
        List<String> membersFacebookIds = ListExt.map(Arrays.asList(pollStub.getMembers()), Person::getFacebookId);
        membersFacebookIds.add(hostFacebookId);

        HashMap<String, Integer> voteCounts = new HashMap<>();
        HashMap<String, String> votedFor = new HashMap<>();
        HashMap<String, String> eventVotes = new HashMap<>();
        HashMap<String, HashMap<String, Boolean>> eventDatesStatus = new HashMap<>();

        for (Category category : VoteCategoryStage.categoriesTemplates)
            voteCounts.put(category.getId(), 0);

        for (String facebookId : membersFacebookIds)
        {
            votedFor.put(facebookId, NO_USER_VOTE);
            eventVotes.put(facebookId, NO_USER_VOTE);
        }

        return new FirebasePoll(membersFacebookIds, pollStub.getName(), pollId,
                VoteCategoryStage.class.toString(), voteCounts, votedFor, null,
                eventVotes, "", "", eventDatesStatus);
    }

    /**
     * Registers vote for category in firebase
     * @param category Category voted for by current user
     */
    public void voteCategory(Category category) {
        String itemId = category.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        FirebasePollController.vote(pollId, facebookId, itemId);
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
        String eventId = event.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        FirebasePollController.voteEvent(pollId, facebookId, eventId);
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
