package io.github.entertainmatch.firebase;

import io.github.entertainmatch.model.ConcertEvent;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.MovieEvent;
import io.github.entertainmatch.model.PlayEvent;
import io.github.entertainmatch.model.StaffPickEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
public class FirebaseEventControllerTest extends AbstractFirebaseControllerTest {
    @Rule
    private ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getClassForCategory() {
        Map<String, Class<? extends Event>> mappings = new HashMap<>();
        mappings.put("movies", MovieEvent.class);
        mappings.put("concerts", ConcertEvent.class);
        mappings.put("plays", PlayEvent.class);
        mappings.put("staffpicks", StaffPickEvent.class);
        for (Map.Entry<String, Class<? extends Event>> entry : mappings.entrySet()) {
            Class<? extends Event> classForCategory =
                    FirebaseEventController.getClassForCategory(entry.getKey());
            assertThat(classForCategory).isEqualTo(entry.getValue());
        }
    }

    public void getClassForCategory_failsForInvalidName() {
        // given
        String category = "invalid";
        // expect
        expectedException.expect(IllegalArgumentException.class);
        // when
        FirebaseEventController.getClassForCategory(category);
    }
}
