package io.github.entertainmatch.firebase.models;

import java.util.Arrays;
import java.util.List;

import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.utils.ListExt;
import lombok.Getter;

/**
 * Created by Adrian Bednarz on 4/30/17.
 * The reason for this object is to provide Firebase-friendly implementation of Poll.
 * This is due to Firebase limitations that might occur in the future.
 * One of them is the serialization of Java Arrays. Firebase is not capable of doing that.
 */
public class FirebasePoll {
    /**
     * Users who participate in the poll
     */
    @Getter
    private List<String> memberFacebookIds;
    /**
     * Name of the poll.
     */
    @Getter
    private String name;

    /**
     * Private constructor used to instantiate Firebase Poll object.
     * TODO: pass current poll stage
     * @param memberFacebookIds Identifiers of members connected with this poll. For now it is a facebook id.
     * @param name Name of the poll
     */
    private FirebasePoll(List<String> memberFacebookIds, String name) {
        this.memberFacebookIds = memberFacebookIds;
        this.name = name;
    }

    /**
     * Construct Firebase Poll from a Poll object that is used throughout the application.
     * @param poll Poll to convert
     * @return FirebasePoll used in the cloud
     */
    public static FirebasePoll fromPoll(String hostFacebookId, Poll poll) {
        List<String> membersFacebookIds = ListExt.map(Arrays.asList(poll.getMembers()), Person::getFacebookId);
        membersFacebookIds.add(hostFacebookId);
        return new FirebasePoll(
                membersFacebookIds,
                poll.getName()
        );
    }
}
