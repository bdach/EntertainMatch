package io.github.entertainmatch.firebase;

import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
@PrepareForTest(FirebasePollController.class)
public class FirebaseEventDateControllerTest extends AbstractFirebaseControllerTest {
    @Before
    public void testSetUp() {
        PowerMockito.mockStatic(FirebasePollController.class);
    }

    @Test
    public void setUpDateStageForUsers() {
        // given
        FirebasePoll poll = Mockito.mock(FirebasePoll.class);
        Mockito.when(poll.getPollId()).thenReturn("test");
        Mockito.when(poll.getParticipants())
                .thenReturn(Arrays.asList("participant1", "participant2"));
        HashMap<String, FirebaseEventDate> datesMap = new HashMap<>();
        datesMap.put("date1", new FirebaseEventDate());
        datesMap.put("date2", new FirebaseEventDate());
        // when
        FirebaseEventDateController.setupDateStageForUsers(poll, datesMap);
        // then
        PowerMockito.verifyStatic(Mockito.times(4));
        FirebasePollController.setupDateStageForUser(
                Mockito.eq("test"),
                Mockito.anyString(),
                Mockito.anyString()
        );
    }
}
