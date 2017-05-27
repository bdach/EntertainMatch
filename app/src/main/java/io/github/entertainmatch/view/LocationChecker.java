package io.github.entertainmatch.view;

import android.content.Context;
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
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private Delegate cityNotFoundDelegate;
    private Delegate settingsDelegate;
    private Observable<List<String>> citiesObservable;
    private Subscription citiesSubscription;
    private Snackbar snackbar;
    private List<String> lastValue;

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

    private void checkCities(List<String> cities) {
        lastValue = cities;
        String city = UserPreferences.getCity(context);
        if (cities.contains(city)) {
            if (snackbar != null) {
                snackbar.dismiss();
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

    public void recheckCities() {
        checkCities(lastValue);
    }

    public void unsubscribe() {
        citiesSubscription.unsubscribe();
        snackbar.dismiss();
    }
}
