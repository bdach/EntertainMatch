package io.github.entertainmatch.firebase.model;

import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
public class FirebaseCompletedPollTest {
    @Test
    public void votingComplete_goingNull() {
        // given
        FirebaseCompletedPoll completedPoll = new FirebaseCompletedPoll(
                "id",
                "category",
                Collections.emptyList(),
                null,
                null,
                null,
                null
        );
        // when
        boolean votingComplete = completedPoll.votingComplete("test");
        // then
        assertThat(votingComplete).isFalse();
    }

    @Test
    public void votingComplete_notVotedYet() {
        // given
        FirebaseCompletedPoll completedPoll = new FirebaseCompletedPoll(
                "id",
                "category",
                Collections.emptyList(),
                null,
                null,
                null,
                Collections.singletonMap("wrong_user", true)
        );
        // when
        boolean votingComplete = completedPoll.votingComplete("test");
        // then
        assertThat(votingComplete).isFalse();
    }

    @Test
    public void votingComplete() {
        // given
        FirebaseCompletedPoll completedPoll = new FirebaseCompletedPoll(
                "id",
                "category",
                Collections.emptyList(),
                null,
                null,
                null,
                Collections.singletonMap("test", true)
        );
        // when
        boolean votingComplete = completedPoll.votingComplete("test");
        // then
        assertThat(votingComplete).isTrue();
    }

    @Test
    public void goingList() {
        // given
        HashMap<String, Boolean> going = new HashMap<>();
        going.put("test1", true);
        going.put("test2", false);
        going.put("test3", true);
        FirebaseCompletedPoll completedPoll = new FirebaseCompletedPoll(
                "id",
                "category",
                Collections.emptyList(),
                null,
                null,
                null,
                going
        );
        // when
        List<String> list = completedPoll.goingList();
        // then
        assertThat(list).contains("test1", "test3");
        assertThat(list).doesNotContain("test2");
    }
}
