package io.github.entertainmatch.view.date;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.view.date.DateFragment.OnDateSelectedListener;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for {@link EventDate} objects.
 */
@RequiredArgsConstructor
public class DateRecyclerViewAdapter extends RecyclerView.Adapter<DateRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of all {@link EventDate} objects to be displayed.
     */
    private final List<EventDate> dates;
    /**
     * The {@link OnDateSelectedListener} to notify.
     */
    private final OnDateSelectedListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(dates.get(position));

        holder.mapButton.setOnClickListener(v -> {
            if (null != listener) {
                listener.onDateSelected(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    /**
     * A {@link RecyclerView.ViewHolder} for {@link EventDate} objects.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The root view of the holder.
         */
        public final RelativeLayout view;
        /**
         * Check box used for user voting.
         */
        @BindView(R.id.date_checked)
        CheckBox checkBox;
        /**
         * Text view containing the suggested date.
         */
        @BindView(R.id.date_text)
        TextView dateText;
        /**
         * Text view containing the suggested location.
         */
        @BindView(R.id.date_location)
        TextView locationText;
        /**
         * Icon button used to navigate to a map of the location.
         */
        @BindView(R.id.date_location_map)
        ImageButton mapButton;
        /**
         * The backing item.
         */
        public EventDate item;

        public ViewHolder(View view) {
            super(view);
            this.view = (RelativeLayout) view;
            ButterKnife.bind(this, view);
        }

        /**
         * Sets the view contents in accordance to the supplied item.
         * @param item The {@link EventDate} item to assign to this holder instance.
         */
        public void setItem(final EventDate item) {
            this.item = item;
            String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                    .format(item.getDate());
            dateText.setText(date);
            locationText.setText(item.getPlace());
            checkBox.setChecked(item.isSelected());

            view.setOnClickListener(v -> {
                checkBox.toggle();
                listener.onDateToggle(item, checkBox.isChecked());
            });
        }
    }
}
