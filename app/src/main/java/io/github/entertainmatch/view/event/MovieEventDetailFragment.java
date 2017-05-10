package io.github.entertainmatch.view.event;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.MovieEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link MovieEventDetailActivity}
 * on handsets.
 */
@NoArgsConstructor
public class MovieEventDetailFragment extends Fragment {

    /**
     * The event this fragment is presenting.
     */
    @Getter
    private MovieEvent event;
    /**
     * The toolbar of the view.
     */
    CollapsingToolbarLayout layout;
    /**
     * Title for the detail view.
     */
    @BindView(R.id.event_detail)
    TextView detailTitle;
    /**
     * The view containing the name of the movie's director.
     */
    @BindView(R.id.movie_event_director)
    TextView directorText;
    /**
     * The view containing the movie cast.
     */
    @BindView(R.id.movie_event_cast)
    TextView castText;
    /**
     * View containing the movie score.
     */
    @BindView(R.id.movie_event_score)
    TextView scoreText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(EventListActivity.EVENTS_KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            event = getArguments().getParcelable(EventListActivity.EVENTS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        Activity activity = this.getActivity();
        layout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        ImageView imageView = (ImageView) activity.findViewById(R.id.toolbar_image);

        // Show the dummy content as text in a TextView.
        if (event != null) {
            setContent(rootView);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(bitmap);
                    setColors(Palette.from(bitmap).generate().getMutedSwatch());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            Picasso.with(getContext())
                    .load(event.getDrawableUri())
                    .into(target);
        }

        return rootView;
    }

    /**
     * Sets the content of this detail view.
     *
     * @param rootView The root view containing elements to fill.
     */
    private void setContent(View rootView) {
        ButterKnife.bind(this, rootView);
        layout.setTitle(event.getTitle());
        detailTitle.setText(event.getDescription());
        directorText.setText(event.getDirector());
        castText.setText(event.getCast());
        scoreText.setText(event.getRottenTomatoesScore().toString());
    }

    /**
     * Sets the scrim colors of the {@link CollapsingToolbarLayout} to suit the header image.
     *
     * @param detailSwatch The swatch containing the colors.
     */
    private final void setColors(Palette.Swatch detailSwatch) {
        layout.setContentScrimColor(detailSwatch.getRgb());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
    }

    /**
     * Creates a matching color palette from an image with the given drawable ID.
     *
     * @param drawableId The ID of the drawable.
     * @return A {@link Palette} with the matching colours.
     */
    private Palette getPalette(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), drawableId);
        return Palette.from(bitmap).generate();
    }
}