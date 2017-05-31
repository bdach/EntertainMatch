package io.github.entertainmatch.facebook;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Adrian Bednarz on 5/30/17.
 */

@Module
public class FacebookModule {
    Application application;

    public FacebookModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public FacebookUsers provideFacebookUsers() {
        return new FacebookUsers();
    }

    @Provides
    public FacebookInitializer provideFacebookInitializer() {
        return new FacebookInitializer();
    }

    @Provides
    public FriendsProvider provideFriendsProvider() {
        return new FriendsProvider();
    }
}
