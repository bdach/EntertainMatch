package io.github.entertainmatch.firebase.models;

import java.util.Arrays;
import java.util.List;

import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
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
     * Vote stage object - category
     */
    @Getter
    private FirebaseCategory category;

    /**
     * Construct Firebase Poll from a Poll object that is used throughout the application.
     * @param poll Poll to convert
     * @return FirebasePoll used in the cloud
     */
    public static FirebasePoll fromPoll(String hostFacebookId, Poll poll, String pollId, FirebaseCategory category) {
        List<String> membersFacebookIds = ListExt.map(Arrays.asList(poll.getMembers()), Person::getFacebookId);
        membersFacebookIds.add(hostFacebookId);

        return new FirebasePoll(membersFacebookIds, poll.getName(), pollId, poll.stageName(), category);
    }
}
