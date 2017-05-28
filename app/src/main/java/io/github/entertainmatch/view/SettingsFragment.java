package io.github.entertainmatch.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebaseCityController;

/**
 * Fragment containing the list of user preferences.
 *
 * @author Bartłomiej Dach
 * @since 25.05.17
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Called when the fragment is created.
     * @param savedInstanceState The saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen screen = getPreferenceScreen();
        Context context = screen.getContext();

        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(R.string.location_settings_category);
        screen.addPreference(category);

        ListPreference locationPreference = new ListPreference(context);
        locationPreference.setKey(UserPreferences.PREF_LOCATION_KEY);
        locationPreference.setTitle(R.string.pref_location_label);
        locationPreference.setSummary(R.string.pref_location_summary);
        FirebaseCityController.getCitiesOnce()
                .subscribe(cities -> {
                    String[] citiesArray = cities.toArray(new String[cities.size()]);
                    locationPreference.setEntries(citiesArray);
                    locationPreference.setEntryValues(citiesArray);
                });
        category.addPreference(locationPreference);
    }

    /**
     * Called when the preferences are being created.
     * @param savedInstanceState Saved instance state.
     * @param rootKey The root key under which to store preferences.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}
