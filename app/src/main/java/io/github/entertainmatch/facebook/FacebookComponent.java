package io.github.entertainmatch.facebook;

import javax.inject.Singleton;

import dagger.Component;
import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.notifications.NotificationService;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.MainActivity;
import io.github.entertainmatch.view.category.CategoryFragment;
import io.github.entertainmatch.view.category.VoteCategoryActivity;
import io.github.entertainmatch.view.date.DateFragment;
import io.github.entertainmatch.view.date.VoteDateActivity;
import io.github.entertainmatch.view.event.EventListActivity;
import io.github.entertainmatch.view.main.EventFragment;
import io.github.entertainmatch.view.main.MainActivityPagerAdapter;
import io.github.entertainmatch.view.result.VoteResultActivity;

/**
 * Created by Adrian Bednarz on 5/30/17.
 */

@Singleton
@Component(modules = { FacebookModule.class })
public interface FacebookComponent {
    void inject(DaggerApplication application);
    void inject(NotificationService service);
    void inject(LoginActivity activity);
    void inject(MainActivity activity);
    void inject(EventListActivity activity);
    void inject(VoteResultActivity activity);
    void inject(VoteDateActivity activity);
    void inject(VoteCategoryActivity activity);
    void inject(EventFragment fragment);
    void inject(CategoryFragment fragment);
    void inject(DateFragment fragment);
    void inject(FirebasePoll poll);
    void inject(MainActivityPagerAdapter adapter);
}
