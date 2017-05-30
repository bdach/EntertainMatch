package io.github.entertainmatch.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebaseCityController;
import io.github.entertainmatch.utils.Delegate;
import rx.Observable;
import rx.Subscription;

import java.util.List;

/**
 * Helper class.
 * Used in views to check whether the user's current location
 * exists in Firebase, therefore ensuring he doesn't break stuff.
 *
 * @author Bartlomiej Dach
 * @since 26.05.17
 */
public class LocationChecker {
    /**
     * The {@link Context} to use when fetching user preferences
     * with {@link Context#getSharedPreferences(String, int)}.
     */
    private Context context;

    /**
     * The {@link CoordinatorLayout} used to display snackbars onto.
     */
    private CoordinatorLayout coordinatorLayout;

    /**
     * A {@link Delegate} to call, representing the action to take
     * when the city is not found in the database.
     */
    private Delegate cityNotFoundDelegate;

    /**
     * A {@link Delegate} to call to navigate to the settings view.
     */
    private Delegate settingsDelegate;

    /**
     * An {@link Observable}, yielding the list of cities available
     * to the user.
     */
    private Observable<List<String>> citiesObservable;

    /**
     * The current {@link Subscription} to the {@link #citiesObservable}.
     */
    private Subscription citiesSubscription;

    /**
     * Contains the last displayed {@link Snackbar}, if there is one.
     */
    @Nullable
    @VisibleForTesting
    Snackbar snackbar;

    /**
     * The last list of cities received from the {@link #citiesObservable},
     * if there is one.
     */
    @Nullable
    private List<String> lastValue;

    /**
     * Constructor.
     * @param context Context to use when fetching preferences.
     * @param coordinatorLayout {@link CoordinatorLayout} to display snackbars in.
     * @param cityNotFoundDelegate {@link Delegate} to execute when a city isn't found.
     * @param settingsDelegate {@link Delegate} forwarding the user to a settings menu.
     */
    public LocationChecker(Context context,
                           CoordinatorLayout coordinatorLayout,
                           Delegate cityNotFoundDelegate,
                           Delegate settingsDelegate) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.cityNotFoundDelegate = cityNotFoundDelegate;
        this.settingsDelegate = settingsDelegate;
        this.citiesObservable = FirebaseCityController.getCities();
        this.citiesSubscription = citiesObservable.subscribe(this::checkCities);
    }

    @VisibleForTesting
    LocationChecker(Context context,
                    CoordinatorLayout coordinatorLayout,
                    Delegate cityNotFoundDelegate,
                    Delegate settingsDelegate,
                    Observable<List<String>> citiesObservable) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.cityNotFoundDelegate = cityNotFoundDelegate;
        this.settingsDelegate = settingsDelegate;
        this.citiesObservable = citiesObservable;
        this.citiesSubscription = citiesObservable.subscribe(this::checkCities);
    }

    /**
     * Checks whether the city in user preferences is still valid.
     * @param cities Newly updated list of cities.
     */
    private void checkCities(List<String> cities) {
        lastValue = cities;
        String city = UserPreferences.getCity(context);
        if (cities.contains(city)) {
            if (snackbar != null) {
                snackbar.dismiss();
                snackbar = null;
            }
            return;
        }
        cityNotFoundDelegate.execute();
        snackbar = Snackbar.make(
                coordinatorLayout,
                R.string.no_city_set_snackbar,
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                R.string.no_city_set_snackbar_action,
                v -> settingsDelegate.execute()
        );
        snackbar.show();
    }

    /**
     * Performs another check of the city list, using the
     * last cached value.
     */
    public void recheckCities() {
        checkCities(lastValue);
    }

    /**
     * Unsubscribes from the {@link #citiesObservable} and
     * removes all displayed snackbars.
     */
    public void unsubscribe() {
        if (citiesSubscription != null)
            citiesSubscription.unsubscribe();
        if (snackbar != null)
            snackbar.dismiss();
    }
}
