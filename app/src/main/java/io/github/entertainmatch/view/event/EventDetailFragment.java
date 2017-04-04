package io.github.entertainmatch.view.event;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.MovieEvent;
import lombok.Getter;
import org.w3c.dom.Text;

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
    @Getter
    private MovieEvent event;
    private CollapsingToolbarLayout layout;

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
        layout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        ImageView view = (ImageView) activity.findViewById(R.id.toolbar_image);

        // Show the dummy content as text in a TextView.
        if (event != null) {
            setContent(rootView);
            view.setImageResource(event.getDrawableId());

            Palette palette = getPalette(event.getDrawableId());
            setColors(palette.getMutedSwatch());
        }

        return rootView;
    }

    private void setContent(View rootView) {
        layout.setTitle(event.getTitle());
        ((TextView) rootView.findViewById(R.id.event_detail)).setText(event.getSynopsis());
        TextView director = (TextView) rootView.findViewById(R.id.movie_event_director);
        director.setText(event.getDirector());
        TextView cast = (TextView) rootView.findViewById(R.id.movie_event_cast);
        cast.setText(event.getCast());
        TextView score = (TextView) rootView.findViewById(R.id.movie_event_score);
        score.setText(event.getRottenTomatoesScore().toString());
    }

    private final void setColors(Palette.Swatch detailSwatch) {
        layout.setContentScrimColor(detailSwatch.getRgb());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
    }

    private Palette getPalette(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), drawableId);
        Palette palette = Palette.from(bitmap).generate();
        return palette;
    }
}
