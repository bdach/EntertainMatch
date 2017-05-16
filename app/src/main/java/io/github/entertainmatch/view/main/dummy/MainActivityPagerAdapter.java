package io.github.entertainmatch.view.main.dummy;

import android.hardware.camera2.params.Face;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.utils.PollStageFactory;
import io.github.entertainmatch.view.main.PollFragment;
import lombok.Getter;
import rx.Subscription;

/**
 * @author Bartlomiej Dach
 * @since 15.05.17
 */
public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    @Getter
    List<Fragment> fragments;

    List<Subscription> subscriptions;

    public MainActivityPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<Subscription> subscriptions) {
        super(fragmentManager);
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

        // cancer
        if (position == 0) {
            PollFragment pollFragment = (PollFragment) fragment;
            String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

            subscriptions.add(FirebaseUserController.getPollsForUser(facebookId)
                .subscribe(firebasePoll -> {
                    pollFragment.updatePoll(new Poll(
                        firebasePoll.getName(),
                        PollStageFactory.get(firebasePoll.getStage(), firebasePoll.getPollId()),
                        firebasePoll.getParticipants(),
                        firebasePoll.getPollId(),
                        firebasePoll.votingComplete(facebookId)));
                })
            );
        }

        fragments.set(position, fragment);
        return fragment;
    }
}
