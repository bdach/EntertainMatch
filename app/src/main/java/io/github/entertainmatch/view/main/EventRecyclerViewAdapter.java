package io.github.entertainmatch.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebaseEventController;
import io.github.entertainmatch.firebase.FirebaseEventDateController;
import io.github.entertainmatch.firebase.FirebaseLocationsController;
import io.github.entertainmatch.firebase.models.FirebasePoll;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private static final int MAX_AVATARS = 3;
    private final List<FirebasePoll> values;
    private final EventFragment.OnEventSelectedListener listener;

    public EventRecyclerViewAdapter(List<FirebasePoll> items, EventFragment.OnEventSelectedListener listener) {
        values = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FirebasePoll poll = values.get(position);
        holder.setItem(poll);
        FirebaseEventController.getEventSingle(
                poll.getChosenCategory(),
                poll.getVictoriousEvent().substring(poll.getChosenCategory().length())
        )
                .subscribe(event -> {
                    Picasso.with(listener.getContext())
                            .load(event.getDrawableUri())
                            .into(holder.eventImage);
                    holder.eventName.setText(event.getTitle());
                });
        FirebaseLocationsController.getLocationOnce(poll.getChosenLocationId()).subscribe(location -> {
            holder.eventPlace.setText(location.getPlace());
        });
        FirebaseEventDateController.getEventSingle(poll.getChosenCategory(), poll.getVictoriousEvent(), poll.getChosenLocationId()).subscribe(eventDate -> {
            String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                    .format(eventDate.getDate());
            holder.eventDate.setText(date);
        });

        holder.detailButton.setOnClickListener(v -> {
            if (null != listener) {
                listener.onEventClicked(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public FirebasePoll item;

        @BindView(R.id.event_image)
        ImageView eventImage;
        @BindView(R.id.member_avatars)
        LinearLayout avatarLayout;
        @BindView(R.id.event_name)
        TextView eventName;
        @BindView(R.id.event_date)
        TextView eventDate;
        @BindView(R.id.event_place)
        TextView eventPlace;
        @BindView(R.id.event_detail_button)
        Button detailButton;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        public void setItem(FirebasePoll item) {
            this.item = item;
            Integer counter = 0;
            for (Map.Entry<String, Boolean> goingEntry : item.getGoing().entrySet()) {
                if (!goingEntry.getValue()) continue;
                counter++;
                if (counter >= MAX_AVATARS) continue;
                AvatarHelper.addMemberAvatar(goingEntry.getKey(), avatarLayout, listener.getContext());
            }
            if (counter >= MAX_AVATARS) {
                AvatarHelper.addPlus(counter - MAX_AVATARS, avatarLayout, listener.getContext());
            }
        }
    }
}
