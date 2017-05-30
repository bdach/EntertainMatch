package io.github.entertainmatch.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseUserEventController;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.model.Poll;
import lombok.Getter;
import rx.Subscription;

/**
 * @author Bartlomiej Dach
 * @since 15.05.17
 */
public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    @Inject
    FacebookUsers FacebookUsers;

    @Getter
    List<Fragment> fragments;

    List<Subscription> subscriptions;

    public MainActivityPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<Subscription> subscriptions) {
        super(fragmentManager);
        DaggerApplication.getApp().getFacebookComponent().inject(this);

        this.fragments = fragments;
        this.subscriptions = subscriptions;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Ongoing polls";
            case 1:
                return "Upcoming events";
        }
        throw new IllegalArgumentException("Page index out of range");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        // cancer
        if (position == 0) {
            PollFragment pollFragment = (PollFragment) fragment;

            subscriptions.add(FirebaseUserController.getPollsForUser(facebookId)
                .subscribe(firebasePoll -> pollFragment.updatePoll(new Poll(firebasePoll, facebookId))));
        } else {
            EventFragment eventFragment = (EventFragment) fragment;

            subscriptions.add(FirebaseUserEventController.getEventsForUser(facebookId)
                .subscribe(eventFragment::updatePoll));
        }

        fragments.set(position, fragment);
        return fragment;
    }
}
