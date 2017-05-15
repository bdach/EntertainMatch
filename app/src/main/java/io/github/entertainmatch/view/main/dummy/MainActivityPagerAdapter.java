package io.github.entertainmatch.view.main.dummy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 15.05.17
 */
public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public MainActivityPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
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
}
