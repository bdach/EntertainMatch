package io.github.entertainmatch.firebase.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
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
public class FirebasePoll {
    public static final String NO_USER_VOTE = "-1";
    /**
     * Users who participate in the poll
     */
    @Getter
    private List<String> participants;

    /**
     * Name of the poll.
     */
    @Getter
    private String name;

    /**
     * Identifier of the poll
     */
    @Getter
    private String pollId;

    /**
     * Current stage
     */
    @Getter
    private String stage;

    /**
     * Maps categoryId to number of votes
     */
    @Getter
    private Map<String, Integer> voteCounts = new HashMap<>();

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    @Getter
    private Map<String, String> votedFor = new HashMap<>();

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

        for (Category category : VoteCategoryStage.categoriesTemplates)
            voteCounts.put(category.getId(), 0);

        for (String facebookId : membersFacebookIds)
            votedFor.put(facebookId, NO_USER_VOTE);

        return new FirebasePoll(membersFacebookIds, pollStub.getName(), pollId,
                VoteCategoryStage.class.toString(), voteCounts, votedFor);
    }

    public void update(Category category) {
        String itemId = category.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        FirebasePollController.vote(pollId, facebookId, itemId);
    }

    public void setValues(Category amendedCategory) {
        String itemId = amendedCategory.getId();
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        amendedCategory.setVotedFor(getVotedFor().get(facebookId).equals(itemId));
        amendedCategory.setVoteCount(getVoteCounts().get(itemId));
    }

    public void update(FirebasePoll updatedPoll) {
        stage = updatedPoll.stage;
        voteCounts = updatedPoll.voteCounts;
        votedFor = updatedPoll.votedFor;
    }
}
