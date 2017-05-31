package io.github.entertainmatch.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.poll.CreatePollActivity;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 30.05.17
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    @Test
    public void clickFab() {
        // given
        MainActivity activity = activityTestRule.getActivity();
        Instrumentation.ActivityMonitor activityMonitor =
                instrumentation.addMonitor(CreatePollActivity.class.getName(), null, false);
        // when
        onView(withId(R.id.fab))
                .perform(click());
        Activity targetActivity = activityMonitor.waitForActivityWithTimeout(500);
        // then
        assertThat(targetActivity).isNotNull();
    }

    @Test
    public void clickToSettings() {
        // given
        MainActivity activity = activityTestRule.getActivity();
        // when
        onView(withClassName(Matchers.containsString("OverflowMenuButton")))
                .perform(click());
        onView(withText(R.string.action_settings))
                .perform(click());
        // then
        assertThat(activity.settingsFragment.isVisible()).isTrue();
    }

}
