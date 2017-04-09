package io.github.entertainmatch.view.date;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.view.date.DateFragment.OnListFragmentInteractionListener;
import io.github.entertainmatch.view.date.dummy.DummyContent.DummyItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class DateRecyclerViewAdapter extends RecyclerView.Adapter<DateRecyclerViewAdapter.ViewHolder> {

    private final List<EventDate> dates;
    private final OnListFragmentInteractionListener listener;
    private Context context;

    public DateRecyclerViewAdapter(ArrayList<EventDate> dates, OnListFragmentInteractionListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(dates.get(position));

        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout view;
        public final CheckBox checkBox;
        public final TextView dateText;
        public final TextView locationText;
        public final ImageButton mapButton;
        public EventDate item;

        public ViewHolder(View view) {
            super(view);
            this.view = (RelativeLayout) view;
            this.checkBox = (CheckBox) view.findViewById(R.id.date_checked);
            this.dateText = (TextView) view.findViewById(R.id.date_text);
            this.locationText = (TextView) view.findViewById(R.id.date_location);
            this.mapButton = (ImageButton) view.findViewById(R.id.date_location_map);
        }

        public void setItem(final EventDate item) {
            this.item = item;
            String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                    .format(item.getDate());
            dateText.setText(date);
            locationText.setText(item.getPlace());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.toggle();
                }
            });
        }
    }
}
