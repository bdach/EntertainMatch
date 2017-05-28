package io.github.entertainmatch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
@RunWith(AndroidJUnit4.class)
public class UserPreferencesTest {
    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void getCity() {
        // given
        sharedPreferences.edit()
                .putString(UserPreferences.PREF_LOCATION_KEY, "city")
                .apply();
        // when
        String city = UserPreferences.getCity(context);
        // then
        assertThat(city).isEqualTo("city");
    }

    @Test
    public void getCity_noCitySet() {
        // given
        // when
        String city = UserPreferences.getCity(context);
        // then
        assertThat(city).isEmpty();
    }
}
