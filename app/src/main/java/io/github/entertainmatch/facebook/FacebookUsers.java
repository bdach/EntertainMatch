package io.github.entertainmatch.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.util.Log;
import com.facebook.AccessToken;
import io.github.entertainmatch.model.Person;
import lombok.Getter;
import lombok.Setter;

/**
 * Class responsible for managing Facebook users.
 *
 * @author Adrian Bednarz
 * @since 4/5/17
 */
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
    static Person currentUser = null;

    /**
     * Checks whether the user is logged in using Facebook.
     * @param ctx The {@link Context} from which the method is called.
     * @return True if user is logged in, false otherwise.
     */
    public static boolean isUserLoggedIn(Context ctx) {
        return currentUser != null || getCurrentUser(ctx) != null;
    }

    /**
     * Retrieves the current user from {@link SharedPreferences}.
     * @param ctx The context to fetch {@link SharedPreferences} from.
     * @return A {@link Person} object containing a logged-in user's data, or null
     * if the user is not logged in.
     */
    public static Person getCurrentUser(Context ctx) {
        if (currentUser != null)
            return currentUser;

        SharedPreferences preferences = getSharedPreferences(ctx);

        Person candidate = getObject(preferences, USER_KEY, Person.class);
        currentUser = candidate == null || TextUtils.isEmpty(candidate.getFacebookId()) ? null : candidate;
        return currentUser;
    }

    /**
     * Retrieves {@link SharedPreferences} with the key of {@link #PREF_KEY}.
     * @param ctx The {@link Context} to use when fetching the preferences.
     * @return An instance of {@link SharedPreferences} with the key of {@link #PREF_KEY}
     */
    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Sets the supplied user in {@link SharedPreferences} and in the {@link #currentUser}
     * field.
     * @param ctx Context to use when fetching {@link SharedPreferences}.
     * @param user The {@link Person} object to store, containing user data.
     */
    public static void setCurrentUser(Context ctx, Person user) {
        SharedPreferences preferences = getSharedPreferences(ctx);
        putObject(preferences, USER_KEY, user);

        currentUser = user;
    }

    /**
     * Removes the current user from {@link SharedPreferences} and from the
     * {@link #currentUser} field.
     * @param ctx Context to use when fetching {@link SharedPreferences}.
     */
    public static void removeCurrentUser(Context ctx) {
        SharedPreferences preferences = getSharedPreferences(ctx);
        removeObject(preferences, USER_KEY, Person.class);

        currentUser = null;
    }

    /**
     * Helper method. Used to retrieve a Java object stored using the
     * {@link #putObject(SharedPreferences, String, Object)} method.
     * @param preferences The {@link SharedPreferences} instance to store the values into.
     * @param key The key to use when storing field values.
     * @param type The type of object to get.
     * @param <T> The type of object to retrieve.
     * @return An object of type {@link T} retrieved from {@link SharedPreferences}.
     */
    private static <T> T getObject(SharedPreferences preferences, String key, Class<T> type) {
        T o  = null;

        try {
            o = type.getConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                try {
                    if (field.getType().equals(String.class))
                        field.set(o, preferences.getString(getFieldKey(key, field.getName()), ""));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return o;
    }

    /**
     * Helper method. Used to store a Java object's field values into the supplied
     * {@link SharedPreferences} object.
     * @param preferences The {@link SharedPreferences} instance to store the values into.
     * @param key The key to use when storing field values.
     * @param o The object to store.
     */
    private static void putObject(SharedPreferences preferences, String key, Object o) {
        SharedPreferences.Editor editor = preferences.edit();
        for (Field field : o.getClass().getDeclaredFields()) {
            try {
                if (field.getType().equals(String.class))
                    editor.putString(getFieldKey(key, field.getName()), field.get(o).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    /**
     * Helper method. Used to remove an object stored using {@link SharedPreferences} from
     * the supplied {@link SharedPreferences} object.
     * @param preferences The {@link SharedPreferences} instance to remove the object from.
     * @param key The key under which the object was stored.
     * @param type The type of object stored.
     * @param <T> The type of object stored.
     */
    private static <T> void removeObject(SharedPreferences preferences, String key, Class<T> type) {
        SharedPreferences.Editor editor = preferences.edit();

        for (Field field : type.getDeclaredFields()) {
            if (field.getType().equals(String.class))
                editor.remove(getFieldKey(key, field.getName()));
        }

        editor.apply();
    }

    /**
     * Returns a key for the field with the given name.
     * @param objectKey The key used to store the whole object.
     * @param name The name of the field whose value is being stored.
     * @return A {@link String} to use when putting the value into {@link SharedPreferences}
     */
    private static String getFieldKey(String objectKey, String name) {
        return objectKey + "_" + name;
    }
}
