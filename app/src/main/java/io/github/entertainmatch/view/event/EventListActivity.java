package io.github.entertainmatch.view.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;

import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.MovieEvent;
import io.github.entertainmatch.model.PollStage;
import rx.Observable;

import java.util.ArrayList;
import java.util.HashMap;
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
    private String pollId;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

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
        pollId = getIntent().getStringExtra(PollStage.POLL_ID_KEY);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerView(recyclerView);

        Snackbar.make(coordinatorLayout,
                R.string.vote_event_start_tip,
                Snackbar.LENGTH_LONG)
                .show();

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

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.removeItem(viewHolder);
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragDirs = 0;
                int swipeDirs = adapter.getItemCount() > 1 ? ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT : 0;
                return makeMovementFlags(dragDirs, swipeDirs);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * An {@link RecyclerView.Adapter} for the displayed events.
     */
    public class EventRecyclerViewAdapter
            extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

        private final List<MovieEvent> values = new ArrayList<>();
        private final HashMap<String, Boolean> visible = new HashMap<>();

        public EventRecyclerViewAdapter(Observable<List<MovieEvent>> eventsObservable) {
            eventsObservable.subscribe(movieEvents -> {
                values.clear();
                values.addAll(movieEvents);
                for (MovieEvent event : values) {
                    visible.put(event.getId(), true);
                }
                notifyDataSetChanged();
            });
        }

        public void removeItem(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            MovieEvent item = ((ViewHolder)viewHolder).event;
            values.remove(adapterPosition);
            visible.put(item.getId(), false);
            notifyItemRemoved(adapterPosition);
            Snackbar.make(coordinatorLayout,
                    String.format(getString(R.string.vote_event_item_discarded), item.getTitle()),
                    Snackbar.LENGTH_LONG)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event == DISMISS_EVENT_ACTION) return;
                            FirebasePoll poll = FirebasePollController.polls.get(pollId);
                            poll.updateRemainingEvents(visible);
                            if (getItemCount() == 1) {
                                Snackbar.make(coordinatorLayout,
                                        String.format("You've chosen %s!", values.get(0).getTitle()),
                                        Snackbar.LENGTH_INDEFINITE)
                                        .show();
                            }
                        }
                    })
                    .setAction("Undo", v -> undoRemoval(item, adapterPosition))
                    .show();
        }

        private void undoRemoval(MovieEvent item, int adapterPosition) {
            values.add(adapterPosition, item);
            visible.put(item.getId(), true);
            notifyItemInserted(adapterPosition);
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
                Picasso.with(EventListActivity.this)
                        .load(movieEvent.getDrawableUri())
                        .into(eventImage);

                this.eventDescription.setText(movieEvent.getSynopsis());
                this.eventTitle.setText(movieEvent.getTitle());
            }
        }
    }
}
