package io.github.entertainmatch.model;

import io.github.entertainmatch.firebase.FirebaseController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Poll {
    /**
     * Name of the poll.
     */
    private final String name;
    /**
     * The stage of the poll.
     */
    @Setter
    private PollStage pollStage;
    /**
     * Other users who are a part of the poll.
     */
    private final Iterable<Person> members;

    public static List<Poll> mockData() {
        return Arrays.asList(
                new Poll("Test poll", new VoteCategoryStage(), Collections.<Person>emptyList()),
                new Poll("Another test poll", new VoteEventStage(), Collections.<Person>emptyList()),
                new Poll("Yet another test poll", new VoteDateStage(), Collections.<Person>emptyList()),
                new Poll("And yet another poll", new VoteResultStage(), Collections.<Person>emptyList())
        );
    }
}
