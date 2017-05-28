package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.MovieEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
public class FirebaseCompletedPollControllerTest extends AbstractFirebaseControllerTest {
    @Rule
    MockitoRule mockitoRule = MockitoJUnit.rule();

    @Captor
    ArgumentCaptor<Map<String, Object>> resultCaptor;

    @Test
    public void saveCompletedPoll() throws InterruptedException {
        // given
        FirebasePoll poll = setUpPoll();
        MovieEvent event = new MovieEvent();
        FirebaseEventDate eventDate = new FirebaseEventDate();
        FirebaseLocation location = new FirebaseLocation();
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("correctId")
                .finish();
        // when
        FirebaseCompletedPollController.saveCompletedPoll(
                poll,
                Observable.just(event),
                Observable.just(eventDate),
                Observable.just(location)
        );
        // then
        Mockito.verify(targetReference)
                .setValue(resultCaptor.capture());
        Map<String, Object> value = resultCaptor.getValue();
        assertThat(value).contains(
                new AbstractMap.SimpleEntry<String, Object>("id", "correctId"),
                new AbstractMap.SimpleEntry<String, Object>("category", "movies"),
                new AbstractMap.SimpleEntry<String, Object>("event", event),
                new AbstractMap.SimpleEntry<String, Object>("eventDate", eventDate),
                new AbstractMap.SimpleEntry<String, Object>("location", location)
        );
        assertThat(value).containsKey("participants");
        List<String> participants = (List<String>) value.get("participants");
        assertThat(participants.size()).isEqualTo(1);
    }

    private FirebasePoll setUpPoll() {
        FirebasePoll poll = Mockito.mock(FirebasePoll.class);
        Mockito.when(poll.getPollId()).thenReturn("correctId");
        Mockito.when(poll.getChosenCategory()).thenReturn("movies");
        Mockito.when(poll.getParticipants())
                .thenReturn(Collections.singletonList("participant"));
        return poll;
    }

    @Test
    public void getPollsForUser_userNull() {
        // given
        // when
        List<Observable<FirebaseCompletedPoll>> polls =
                FirebaseCompletedPollController.getPollsForUser(null);
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
        List<Observable<FirebaseCompletedPoll>> pollsObservables =
                FirebaseCompletedPollController.getPollsForUser(user);
        // then
        assertThat(pollsObservables.size()).isEqualTo(2);
    }

    @Test
    public void setIsGoing() {
        // given
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("pollId")
                .child("going")
                .child("111222333")
                .finish();
        // when
        FirebaseCompletedPollController.setIsGoing("pollId", "111222333", true);
        // then
        Mockito.verify(targetReference).setValue(true);
    }
}
