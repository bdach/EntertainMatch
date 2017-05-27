package io.github.entertainmatch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 25.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPreferences {
    public static final String PREF_LOCATION_KEY = "pref_location";

    public static String getCity(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_LOCATION_KEY, "");
    }
}
