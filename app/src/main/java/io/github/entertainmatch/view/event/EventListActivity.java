package io.github.entertainmatch.view.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;

import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseEventController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.utils.HashMapExt;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.NavigationHelper;
import io.github.entertainmatch.view.ParticipantList;
import rx.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity containing the list of available events.
 */
public class EventListActivity extends AppCompatActivity {
    /**
     * The fragment argument representing the event ID that this fragment
     * represents.
     */
    public static final String EVENTS_KEY = "event1";

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
    private EventRecyclerViewAdapter adapter;
    private Subscription subscription;
    private ParticipantList participantList;

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

        FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
            setupRecyclerView(recyclerView);
            FirebaseEventController.getEventsSingle(poll.getCity(), poll.getChosenCategory()).subscribe(events -> {
                adapter.updateData(poll, events);
            });
            subscription = FirebasePollController.getPoll(pollId).subscribe(this::stageFinishCallback);
        });


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

    private void stageFinishCallback(FirebasePoll firebasePoll) {
        String facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();

        participantList = new ParticipantList(this, firebasePoll);
        participantList.fetchNames();
        if (!firebasePoll.getStage().equals(VoteEventStage.class.toString())) {
            subscription.unsubscribe();
            Snackbar.make(coordinatorLayout, R.string.voting_finished, Snackbar.LENGTH_LONG)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            NavigationHelper.back(EventListActivity.this, firebasePoll.getPollId());
                        }
                    })
                    .show();
        } else if (firebasePoll.getAgain() != null && firebasePoll.getAgain().get(facebookId)) {
            FirebasePollController.removeVoteEventAgainFlag(pollId, facebookId);
            FirebaseEventController.getEventsSingle(
                    firebasePoll.getCity(),
                    firebasePoll.getChosenCategory()
            ).subscribe(events -> {
                adapter.updateData(firebasePoll, events);
                Snackbar.make(coordinatorLayout, R.string.message_tie, Snackbar.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backCleanup();
    }

    public void backCleanup() {
        if (getCallingActivity() != null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        // no actions

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FacebookInitializer.init(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.default_vote_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.show_participants && participantList != null) {
            AlertDialog dialog = participantList.getDialog();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new EventRecyclerViewAdapter();
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

        private final List<Event> values = new ArrayList<>();
        private Map<String, Boolean> visible = new HashMap<>();

        public void updateData(FirebasePoll poll, Map<String, ? extends Event> events) {
            values.clear();
            Map<String, Boolean> userChoices = getUserChoices(poll);

            setVisible(userChoices, new ArrayList<>(events.values()));
            notifyDataSetChanged();
        }

        private Map<String, Boolean> getUserChoices(FirebasePoll firebasePoll) {
            Map<String, List<String>> remainingChoices = firebasePoll.getRemainingEventChoices();
            String facebookId = FacebookUsers.getCurrentUser(EventListActivity.this).getFacebookId();
            List<String> choices = remainingChoices.get(facebookId);
            List<String> eventsToVote = firebasePoll.getEventsToVote();
            Map<String, Boolean> result = new HashMap<>();
            boolean noChoices = choices == null;
            for (String id : eventsToVote) {
                result.put(id, noChoices);
            }
            if (!noChoices) {
                for (String choice : choices) {
                    result.put(choice, true);
                }
            }

            return result;
        }

        public void removeItem(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            Event item = ((ViewHolder)viewHolder).event;
            values.remove(adapterPosition);
            visible.put(item.getId(), false);
            notifyItemRemoved(adapterPosition);

            Map<String, Boolean> visibleCopy = HashMapExt.shallowCopy(visible);
            Snackbar.make(coordinatorLayout,
                    String.format(getString(R.string.vote_event_item_discarded), item.getTitle()),
                    Snackbar.LENGTH_LONG)
                    .addCallback(snackbarCallback(visibleCopy))
                    .setAction("Undo", v -> undoRemoval(item, adapterPosition))
                    .show();
        }

        private Snackbar.Callback snackbarCallback(Map<String, Boolean> visibleCopy) {
            return new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == DISMISS_EVENT_ACTION) return;
                FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                    poll.updateRemainingEvents(visibleCopy);
                    checkOneChoiceLeft(poll);
                });
                }
            };
        }

        private void checkOneChoiceLeft(FirebasePoll poll) {
            if (values.size() == 1) {
                Snackbar.make(coordinatorLayout,
                    String.format("You've chosen %s!", values.get(0).getTitle()),
                    Snackbar.LENGTH_LONG)
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            poll.voteEvent(values.get(0));
                        }
                    })
                    .show();
            }
        }

        private void undoRemoval(Event item, int adapterPosition) {
            values.add(adapterPosition, item);
            visible.put(item.getId(), true);
            notifyItemInserted(adapterPosition);
        }

        private void setVisible(Map<String, Boolean> visible, List<? extends Event> movieEvents) {
            this.visible = visible;
            for (Event event : movieEvents) {
                if (!visible.containsKey(event.getId())) {
                    visible.put(event.getId(), false);
                }
                if (visible.get(event.getId())) {
                    values.add(event);
                }
            }
            notifyDataSetChanged();
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
                    arguments.putParcelable(EVENTS_KEY, holder.event);
                    Fragment fragment = EventDetailViewResolver.createFragmentForEvent(holder.event);
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EVENTS_KEY, holder.event);

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
            public Event event;
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

            public void setEvent(Event event) {
                this.event = event;
                Picasso.with(EventListActivity.this)
                        .load(event.getDrawableUri())
                        .into(eventImage);

                this.eventDescription.setText(event.getDescription());
                this.eventTitle.setText(event.getTitle());
            }
        }
    }
}
