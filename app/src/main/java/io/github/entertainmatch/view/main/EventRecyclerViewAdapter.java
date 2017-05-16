package io.github.entertainmatch.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.main.dummy.DummyContent.DummyItem;

import java.util.Arrays;
import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private static final int MAX_AVATARS = 3;
    private final List<DummyItem> mValues;
    private final EventFragment.OnEventSelectedListener listener;

    public EventRecyclerViewAdapter(List<DummyItem> items, EventFragment.OnEventSelectedListener listener) {
        mValues = items;
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
        holder.item = mValues.get(position);

        holder.view.setOnClickListener(v -> {
            if (null != listener) {
                listener.onListFragmentInteraction(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public DummyItem item;

        @BindView(R.id.event_image)
        ImageView eventImage;
        @BindView(R.id.member_avatars)
        LinearLayout avatarLayout;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            List<String> ids = Arrays.asList("111840479374488", "115131705719584");
            for (String id : ids) {
                AvatarHelper.addMemberAvatar(id, avatarLayout, listener.getContext());
            }
            Picasso.with(listener.getContext())
                    .load("http://i.imgur.com/PLFkStW.jpg")
                    .into(eventImage);
        }
    }
}
