package io.github.entertainmatch.view.dagger;

import android.app.Application;
import android.content.Context;
import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.facebook.FacebookModule;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.facebook.FriendsProvider;
import io.github.entertainmatch.model.Person;
import org.mockito.Mockito;

/**
 * @author Bartlomiej Dach
 * @since 31.05.17
 */
public class MockFacebookModule extends FacebookModule {
    public MockFacebookModule(Application application) {
        super(application);
    }

    @Override
    public FacebookInitializer provideFacebookInitializer() {
        return Mockito.mock(FacebookInitializer.class);
    }

    @Override
    public FacebookUsers provideFacebookUsers() {
        FacebookUsers facebookUsers = Mockito.mock(FacebookUsers.class);
        Mockito.when(facebookUsers.getCurrentUser(Mockito.any(Context.class)))
                .thenReturn(new Person(
                        "119661278582880",
                        "Open Graph Test User",
                        false,
                        ""
                ));
        return facebookUsers;
    }

    @Override
    public FriendsProvider provideFriendsProvider() {
        return Mockito.mock(FriendsProvider.class);
    }
}
