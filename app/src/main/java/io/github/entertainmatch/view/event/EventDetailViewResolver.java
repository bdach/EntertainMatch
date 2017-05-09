package io.github.entertainmatch.view.event;

import io.github.entertainmatch.model.*;

/**
 * @author Bartlomiej Dach
 * @since 07.05.17
 */
public class EventDetailViewResolver {
    private EventDetailViewResolver() {}

    public static EventDetailFragment createFragmentForEvent(Event event) {
        if (event.getClass().equals(MovieEvent.class)) {
            return new MovieEventDetailFragment();
        }
        if (event.getClass().equals(ConcertEvent.class)) {
            return new ConcertEventDetailFragment();
        }
        if (event.getClass().equals(PlayEvent.class)) {
            return new PlayEventDetailFragment();
        }
        if (event.getClass().equals(StaffPickEvent.class)) {
            return new StaffPickEventDetailFragment();
        }
        throw new IllegalArgumentException("An event of unknown type was supplied");
    }

}
