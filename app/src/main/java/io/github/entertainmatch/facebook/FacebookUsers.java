package io.github.entertainmatch.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.utils.PreferencesHelper;

import javax.inject.Singleton;

/**
 * Class responsible for managing Facebook users.
 *
 * @author Adrian Bednarz
 * @since 4/5/17
 */
@Singleton
public class FacebookUsers {
    /**
     * Key for the {@link SharedPreferences} containing user data.
     */
    private final static String PREF_KEY = "facebook_users";
    /**
     * Key for the {@link Person} object stored in {@link SharedPreferences}.
     */
    private final static String USER_KEY = "user";

    /**
     * Contains the user currently logged into the application.
     */
    @VisibleForTesting
    Person currentUser = null;

    /**
     * Checks whether the user is logged in using Facebook.
     * @param ctx The {@link Context} from which the method is called.
     * @return True if user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn(Context ctx) {
        return currentUser != null || getCurrentUser(ctx) != null;
    }

    /**
     * Retrieves the current user from {@link SharedPreferences}.
     * @param ctx The context to fetch {@link SharedPreferences} from.
     * @return A {@link Person} object containing a logged-in user's data, or null
     * if the user is not logged in.
     */
    public Person getCurrentUser(Context ctx) {
        if (currentUser != null)
            return currentUser;

        SharedPreferences preferences = PreferencesHelper.getSharedPreferences(ctx, PREF_KEY);

        Person candidate = PreferencesHelper.getObject(preferences, USER_KEY, Person.class);
        currentUser = candidate == null || TextUtils.isEmpty(candidate.getFacebookId()) ? null : candidate;
        return currentUser;
    }

    /**
     * Sets the supplied user in {@link SharedPreferences} and in the {@link #currentUser}
     * field.
     * @param ctx Context to use when fetching {@link SharedPreferences}.
     * @param user The {@link Person} object to store, containing user data.
     */
    public void setCurrentUser(Context ctx, Person user) {
        SharedPreferences preferences = PreferencesHelper.getSharedPreferences(ctx, PREF_KEY);
        PreferencesHelper.putObject(preferences, USER_KEY, user);

        currentUser = user;
    }

    /**
     * Removes the current user from {@link SharedPreferences} and from the
     * {@link #currentUser} field.
     * @param ctx Context to use when fetching {@link SharedPreferences}.
     */
    public void removeCurrentUser(Context ctx) {
        SharedPreferences preferences = PreferencesHelper.getSharedPreferences(ctx, PREF_KEY);
        PreferencesHelper.removeObject(preferences, USER_KEY, Person.class);

        currentUser = null;
    }
}
