package io.github.entertainmatch.view.event;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.ConcertEvent;
import io.github.entertainmatch.model.MovieEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
@NoArgsConstructor
public class ConcertEventDetailFragment extends EventDetailFragment {

    /**
     * The event this fragment is presenting.
     */
    @Getter
    private ConcertEvent event;
    /**
     * The toolbar of the view.
     */
    CollapsingToolbarLayout layout;
    /**
     * Title for the detail view.
     */
    @BindView(R.id.event_detail)
    TextView detailTitle;
    @BindView(R.id.concert_event_band_members)
    TextView bandMembers;
    @BindView(R.id.concert_event_last_album_title)
    TextView lastAlbumTitle;

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
        View rootView = inflater.inflate(R.layout.concert_event_detail, container, false);

        Activity activity = this.getActivity();
        layout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        ImageView imageView = (ImageView) activity.findViewById(R.id.toolbar_image);

        if (event != null) {
            setContent(rootView);
            applyImage(event, imageView, layout);
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
        bandMembers.setText(event.getBandMembers());
        lastAlbumTitle.setText(event.getLastAlbum());
    }

    @Override
    public View.OnClickListener getFabListener() {
        return v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getYoutubePlaylistUrl()));
            startActivity(intent);
        };
    }

    @Override
    public int getFabIconResource() {
        return R.drawable.ic_youtube_black_24dp;
    }
}
