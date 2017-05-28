package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStub;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
@PrepareForTest(FirebaseUserController.class)
public class FirebasePollControllerTest extends AbstractFirebaseControllerTest {
    @Rule
    MockitoRule mockitoRule = MockitoJUnit.rule();

    @Captor
    ArgumentCaptor<List<String>> resultCaptor;

    @Mock
    private DatabaseReference targetReference;

    @Test
    public void getPollsForUser_userNull() {
        // given
        // when
        List<Observable<FirebasePoll>> polls = FirebasePollController.getPollsForUser(null);
        // then
        assertThat(polls).isEmpty();
    }

    @Test
    public void getPollsForUser() {
        // given
        FirebaseUser user = new FirebaseUser();
        Map<String, Boolean> polls = user.getPolls();
        polls.put("poll1", true);
        polls.put("poll2", false);
        // when
        List<Observable<FirebasePoll>> observables = FirebasePollController.getPollsForUser(user);
        // then
        assertThat(observables).hasSize(2);
    }

    @Test
    public void updateRemainingEvents() {
        // given
        Map<String, Boolean> selections = new HashMap<>();
        selections.put("event1", true);
        selections.put("event2", false);
        selections.put("event3", true);
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("pollId")
                .child("remainingEventChoices")
                .child("userId")
                .finish();
        // when
        FirebasePollController.updateRemainingEvents(
                "pollId",
                "userId",
                selections
        );
        // then
        Mockito.verify(targetReference).setValue(resultCaptor.capture());
        List<String> value = resultCaptor.getValue();
        assertThat(value).contains("event1", "event3");
        assertThat(value).doesNotContain("event2");
    }

    @Test
    public void chooseDate() {
        // given
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("otherPollId")
                .child("eventDatesStatus")
                .child("locationId")
                .child("facebookId")
                .finish();
        // when
        FirebasePollController.chooseDate(
                "otherPollId",
                "locationId",
                "facebookId",
                true
        );
        // then
        Mockito.verify(targetReference).setValue(true);
    }

    @Test
    public void dateVotingFinished() {
        // given
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("andAnotherPollId")
                .child("eventDatesStatus")
                .child("voted")
                .child("facebookId")
                .finish();
        // when
        FirebasePollController.dateVotingFinished(
                "andAnotherPollId",
                "facebookId"
        );
        // then
        Mockito.verify(targetReference).setValue(true);
    }
}
