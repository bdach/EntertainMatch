package io.github.entertainmatch.view.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;

import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.model.MovieEvent;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity containing the list of available events.
 */
public class EventListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;

    /**
     * The toolbar displayed in the view.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * The {@link RecyclerView} containing the list of events.
     */
    @BindView(R.id.event_list)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView(recyclerView);

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(FirebaseController.getMovieEventsObservable());
        recyclerView.setAdapter(adapter);
    }

    /**
     * An {@link RecyclerView.Adapter} for the displayed events.
     */
    public class EventRecyclerViewAdapter
            extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

        private final List<MovieEvent> values = new ArrayList<>();

        public EventRecyclerViewAdapter(Observable<List<MovieEvent>> eventsObservable) {
            eventsObservable.subscribe(movieEvents -> {
                values.clear();
                values.addAll(movieEvents);
                notifyDataSetChanged();
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.setEvent(values.get(position));

            holder.eventDetailButton.setOnClickListener(v -> {
                if (twoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(EventDetailFragment.EVENTS_KEY, holder.event);
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EventDetailFragment.EVENTS_KEY, holder.event);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        /**
         * A {@link RecyclerView.ViewHolder} for the event.
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            /**
             * The root view of the holder.
             */
            public final View view;
            /**
             * The backing event model object.
             */
            public MovieEvent event;
            /**
             * View responsible for displaying the image associated with the event.
             */
            @BindView(R.id.event_image)
            ImageView eventImage;
            /**
             * Text view displaying the title of the event.
             */
            @BindView(R.id.event_title)
            TextView eventTitle;
            /**
             * Text view displaying the description of the event.
             */
            @BindView(R.id.event_description)
            TextView eventDescription;
            /**
             * Card button allowing the user to navigate to the details view.
             */
            @BindView(R.id.event_detail_button)
            Button eventDetailButton;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                ButterKnife.bind(this, view);
            }

            public void setEvent(MovieEvent movieEvent) {
                this.event = movieEvent;
                this.eventImage.setImageResource(movieEvent.getDrawableId());
                this.eventDescription.setText(movieEvent.getSynopsis());
                this.eventTitle.setText(movieEvent.getTitle());
            }
        }
    }
}
