package io.github.entertainmatch.view.main;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.login.widget.ProfilePictureView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.view.CircularProfilePictureView;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} for {@link Poll} objects.
 */
@RequiredArgsConstructor
public class PollRecyclerViewAdapter extends RecyclerView.Adapter<PollRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of polls to be displayed in the list.
     */
    private final ArrayList<Poll> polls;

    /**
     * A {@link PollFragment.OnPollSelectedListener} to be notified of item selections.
     */
    private final PollFragment.OnPollSelectedListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Poll poll = polls.get(position);
        holder.setPoll(poll);

        holder.viewProgressButton.setOnClickListener(v -> {
            if (null != listener) {
                listener.viewPollProgress(holder.poll);
            }
        });
        holder.deleteCompletedButton.setOnClickListener(v -> {
            if (null != listener) {
                listener.deletePoll(holder.poll);
                int adapterPosition = holder.getAdapterPosition();
                polls.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    /**
     * The {@link RecyclerView.ViewHolder} for {@link Poll} items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final int MAX_AVATARS = 3;
        /**
         * The root view of the item.
         */
        private final View view;
        /**
         * Text label containing the poll name.
         */
        @BindView(R.id.poll_name)
        TextView nameView;
        /**
         * Text label containing the poll status.
         */
        @BindView(R.id.poll_status)
        TextView statusView;
        @BindView(R.id.member_avatars)
        LinearLayout memberAvatarLayout;
        @BindView(R.id.poll_view_progress)
        Button viewProgressButton;
        @BindView(R.id.poll_delete_completed)
        Button deleteCompletedButton;
        /**
         * The backing {@link Poll} item.
         */
        private Poll poll;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        public void setPoll(Poll poll) {
            this.poll = poll;
            nameView.setText(poll.getName());
            statusView.setText(poll.getPollStage().getStageStringId());
            memberAvatarLayout.removeAllViews();
            deleteCompletedButton.setVisibility(poll.getVotingComplete() ? View.VISIBLE : View.INVISIBLE);
            Integer counter = 0;
            Person[] members = poll.getMembers();
            for (Person person : members) {
                if (counter >= MAX_AVATARS) {
                    PlusFragment plusFragment = PlusFragment.newInstance(members.length - MAX_AVATARS);
                    listener.getContext()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.member_avatars, plusFragment)
                            .commit();
                    break;
                }
                addMemberAvatar(person);
                counter++;
            }
        }

        public void addMemberAvatar(Person personId) {
            ProfilePictureView pictureView = new CircularProfilePictureView(listener.getContext());
            pictureView.setProfileId(personId.getFacebookId());
            pictureView.setPresetSize(ProfilePictureView.SMALL);
            LinearLayout.LayoutParams params = getParamsWithMargin();
            memberAvatarLayout.addView(pictureView, params);
        }

        private LinearLayout.LayoutParams getParamsWithMargin() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            Resources r = listener.getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8,
                    r.getDisplayMetrics()
            );
            params.setMargins(px, px, px, px);
            return params;
        }
    }
}
