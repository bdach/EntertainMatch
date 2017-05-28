package io.github.entertainmatch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper class used to fetch user city from {@link SharedPreferences}.
 *
 * @author Bartlomiej Dach
 * @since 25.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPreferences {
    /**
     * The key under which the city location is stored.
     */
    static final String PREF_LOCATION_KEY = "pref_location";

    /**
     * Retrieves the city name from {@link SharedPreferences} associated with
     * the provide {@link Context}.
     * @param context The {@link Context} to use when fetching preferences.
     * @return The user's chosen city name as string, or an empty string if
     * the city was not set.
     */
    public static String getCity(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_LOCATION_KEY, "");
    }
}
