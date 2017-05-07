package io.github.entertainmatch.view.event;

import android.app.Activity;
import android.support.v4.app.Fragment;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.MovieEvent;

/**
 * @author Bartlomiej Dach
 * @since 07.05.17
 */
public class EventDetailViewResolver {
    private EventDetailViewResolver() {}

    public static Fragment createFragmentForEvent(Event event) {
        if (event.getClass().equals(MovieEvent.class)) {
            return new MovieEventDetailFragment();
        }
        throw new IllegalArgumentException("An event of unknown type was supplied");
    }

    public static Class<? extends Activity> getActivityForEvent(Event event) {
        if (event.getClass().equals(MovieEvent.class)) {
            return MovieEventDetailActivity.class;
        }
        throw new IllegalArgumentException("An event of unknown type was supplied");
    }
}
