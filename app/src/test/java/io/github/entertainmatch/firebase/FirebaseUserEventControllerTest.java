package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
public class FirebaseUserEventControllerTest extends AbstractFirebaseControllerTest {
    @Test
    public void addEventForUser() {
        // given
        DatabaseReference reference = FirebaseReferenceScaffold.from(getReference())
                .child("testUser")
                .child("polls")
                .child("aPoll")
                .finish();
        // when
        FirebaseUserEventController.addEventForUser("aPoll", "testUser");
        // then
        Mockito.verify(reference).setValue(true);
    }

    @Test
    public void removeEventForUser() {
        // given
        DatabaseReference reference = FirebaseReferenceScaffold.from(getReference())
                .child("testUser")
                .child("polls")
                .child("aPoll")
                .finish();
        // when
        FirebaseUserEventController.removeEventForUser("aPoll", "testUser");
        // then
        Mockito.verify(reference).setValue(null);
    }
}
