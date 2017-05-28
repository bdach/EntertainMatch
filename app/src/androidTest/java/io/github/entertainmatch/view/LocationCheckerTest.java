package io.github.entertainmatch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import io.github.entertainmatch.R;
import io.github.entertainmatch.utils.Delegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
@RunWith(AndroidJUnit4.class)
public class LocationCheckerTest {
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences sharedPreferences;

    private Context context;
    private Delegate cityNotFoundDelegate;
    private Delegate settingsDelegate;
    private boolean[] delegateFired;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        context.setTheme(R.style.AppTheme); // required for coordinator layout
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
        coordinatorLayout = new CoordinatorLayout(context);
        delegateFired = new boolean[]{false};
        cityNotFoundDelegate = () -> delegateFired[0] = true;
        settingsDelegate = () -> {};
    }

    @Test
    public void checkCities_citiesNotSet() {
        // given
        Observable<List<String>> cities = Observable.just(Collections.singletonList("city"));
        // when
        LocationChecker locationChecker = new LocationChecker(
                context,
                coordinatorLayout,
                cityNotFoundDelegate,
                settingsDelegate,
                cities
        ); // implicit subscription to the observable
        // then
        assertThat(delegateFired[0]).isTrue();
    }

    @Test
    public void checkCities_citiesSet() {
        // given
        Observable<List<String>> cities = Observable.just(Collections.singletonList("city"));
        sharedPreferences.edit()
                .putString(UserPreferences.PREF_LOCATION_KEY, "city")
                .apply();
        // when
        LocationChecker locationChecker = new LocationChecker(
                context,
                coordinatorLayout,
                cityNotFoundDelegate,
                settingsDelegate,
                cities
        ); // implicit subscription to the observable
        // then
        assertThat(delegateFired[0]).isFalse();
    }

    @Test
    public void recheckCities() {
        // given
        Observable<List<String>> cities = Observable.just(Collections.singletonList("city"));
        LocationChecker locationChecker = new LocationChecker(
                context,
                coordinatorLayout,
                cityNotFoundDelegate,
                settingsDelegate,
                cities
        );
        // set up a snackbar here to not rely on observable subscription function
        locationChecker.snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);
        locationChecker.snackbar.show();
        // when
        sharedPreferences.edit()
                .putString(UserPreferences.PREF_LOCATION_KEY, "city")
                .apply();
        locationChecker.recheckCities();
        // then
        assertThat(locationChecker.snackbar).isNull();
    }
}
