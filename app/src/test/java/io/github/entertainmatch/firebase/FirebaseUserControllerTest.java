package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
public class FirebaseUserControllerTest extends AbstractFirebaseControllerTest {
    @Test
    public void addPoll() {
        // given
        List<String> members = Arrays.asList("member1", "member2");
        DatabaseReference member1Reference = FirebaseReferenceScaffold.from(getReference())
                .child("member1")
                .child("polls")
                .child("firstPoll")
                .finish();
        DatabaseReference member2Reference = FirebaseReferenceScaffold.from(getReference())
                .child("member2")
                .child("polls")
                .child("firstPoll")
                .finish();
        DatabaseReference leaderReference = FirebaseReferenceScaffold.from(getReference())
                .child("leader")
                .child("polls")
                .child("firstPoll")
                .finish();
        // when
        FirebaseUserController.addPoll(
                "firstPoll",
                members,
                "leader"
        );
        // then
        Mockito.verify(member1Reference).setValue(true);
        Mockito.verify(member2Reference).setValue(true);
    }

    @Test
    public void removePollForUser() {
        // given
        DatabaseReference targetReference =
                FirebaseReferenceScaffold.from(getReference())
                    .child("remove")
                    .child("polls")
                    .child("oldPoll")
                .finish();
        // when
        FirebaseUserController.removePollForUser("oldPoll", "remove");
        // then
        Mockito.verify(targetReference).setValue(null);
    }

    @Test
    public void setUpEventStage() {
        // given
        HashSet<String> members = new HashSet<>();
        members.add("member3");
        members.add("member4");
        DatabaseReference member1Reference = FirebaseReferenceScaffold.from(getReference())
                .child("member3")
                .child("events")
                .child("secondPoll")
                .finish();
        DatabaseReference member2Reference = FirebaseReferenceScaffold.from(getReference())
                .child("member4")
                .child("events")
                .child("secondPoll")
                .finish();
        // when
        FirebaseUserController.setupEventStage(
                "secondPoll",
                members
        );
        // then
        Mockito.verify(member1Reference).setValue(true);
        Mockito.verify(member2Reference).setValue(true);
    }

    @Test
    public void setUpDateStage() {
        // given
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("member5")
                .child("dates")
                .child("thirdPoll")
                .finish();
        // when
        FirebaseUserController.setupDateStage(
                "thirdPoll",
                "member5"
        );
        // then
        Mockito.verify(targetReference).setValue(true);
    }

    @Test
    public void setUpResultStage() {
        // given
        DatabaseReference targetReference = FirebaseReferenceScaffold.from(getReference())
                .child("member6")
                .child("finished")
                .child("fourthPoll")
                .finish();
        // when
        FirebaseUserController.setupResultStage(
                "fourthPoll",
                "member6"
        );
        // then
        Mockito.verify(targetReference).setValue(true);
    }

    @Test
    public void notificationResetHandler() {
        // given
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.getPolls().put("aPoll", true);
        firebaseUser.getDates().put("aDate", false);
        firebaseUser.getEvents().put("anEvent", true);
        firebaseUser.getFinished().put("somethingFinished", false);
        MutableData mutableData = Mockito.mock(MutableData.class);
        Mockito.when(mutableData.child(Mockito.anyString())).thenReturn(mutableData);
        FirebaseUserController.NotificationResetHandler handler =
                new FirebaseUserController.NotificationResetHandler(firebaseUser);
        // when
        handler.doTransaction(mutableData);
        // then
        Mockito.verify(mutableData, Mockito.times(4)).setValue(false);
    }
}
