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
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String PREF_LOCATION_KEY = "pref_location";

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
        locationPreference.setKey(PREF_LOCATION_KEY);
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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}
