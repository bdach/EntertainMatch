package io.github.entertainmatch.view.event;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.MovieEvent;
import io.github.entertainmatch.view.event.dummy.DummyContent;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the event ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "event";

    /**
     * The dummy content this fragment is presenting.
     */
    private MovieEvent event;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            event = getArguments().getParcelable(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        ImageView view = (ImageView) activity.findViewById(R.id.toolbar_image);

        // Show the dummy content as text in a TextView.
        if (event != null) {
            ((TextView) rootView.findViewById(R.id.event_detail)).setText(event.getSynopsis());
            view.setImageResource(event.getDrawableId());

            Palette.Swatch swatch = getPrimaryColor(event.getDrawableId());
            appBarLayout.setContentScrimColor(swatch.getRgb());
            appBarLayout.setStatusBarScrimColor(swatch.getRgb());
            appBarLayout.setTitle(event.getTitle());
            appBarLayout.setCollapsedTitleTextColor(swatch.getTitleTextColor());
        }

        return rootView;
    }

    private Palette.Swatch getPrimaryColor(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), drawableId);
        Palette palette = Palette.from(bitmap).generate();
        return palette.getVibrantSwatch();
    }
}
