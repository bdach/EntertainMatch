package io.github.entertainmatch.view;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import io.github.entertainmatch.R;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 31.05.17
 */
@RunWith(AndroidJUnit4.class)
public class SettingsFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private SettingsFragment settingsFragment;

    @Before
    public void setUp() {
        MainActivity activity = activityTestRule.getActivity();
        activity.settingsFragment.setCityListObservable(
                Observable.just(Arrays.asList(
                        "Warsaw", "Poznań"
                ))
        );
    }

    @Test
    public void settings_setCity() throws InterruptedException {
        // given
        MainActivity activity = activityTestRule.getActivity();
        onView(withClassName(Matchers.containsString("OverflowMenuButton")))
                .perform(click());
        onView(withText(R.string.action_settings))
                .perform(click());
        // when
        onView(withText(R.string.pref_location_label))
                .perform(click());
        Thread.sleep(100);
        onView(withText("Poznań"))
                .perform(click());
        // then
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        String location = sharedPreferences.getString(UserPreferences.PREF_LOCATION_KEY, "");
        assertThat(location).isEqualTo("Poznań");
    }
}
