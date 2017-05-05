package io.github.entertainmatch.view;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import io.github.entertainmatch.R;

/**
 * Fragment containing the list of user preferences.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
