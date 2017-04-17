package io.github.entertainmatch.view.poll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to display {@link Person} lists.
 */
@RequiredArgsConstructor
public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    /**
     * List of people to display.
     */
    private final List<Person> people;
    /**
     * Listener to notify of selection events.
     */
    private final PersonFragment.OnPersonSelectedListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(people.get(position));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        /**
         * Name of the person.
         */
        @BindView(R.id.person_name)
        private TextView personName;
        /**
         * Indicates whether or not the person is selected.
         */
        @BindView(R.id.person_selected)
        private CheckBox personSelected;
        /**
         * Image view containing the person's avatar.
         */
        @BindView(R.id.person_avatar_background)
        private ImageView avatarBackground;
        /**
         * The view with the letter inside the avatar.
         */
        @BindView(R.id.person_avatar_letter)
        private TextView avatarLetter;
        /**
         * Backing {@link Person} model object.
         */
        private Person person;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        /**
         * Sets the view contents based on the supplied item.
         * @param item {@link Person} object to base contents upon.
         */
        public void setItem(Person item) {
            this.person = item;
            personName.setText(item.getName());
            view.setOnClickListener(v -> personSelected.toggle());
            setAvatar(item);
        }

        /**
         * Sets the person's avater.
         * @param person {@link Person} object to set avatar of.
         */
        private void setAvatar(Person person) {
            if (person.getDrawableId() == null) {
                setLetterAvatar(person.getName());
            } else {
                setPictureAvatar(person.getDrawableId());
            }
        }

        /**
         * Uses a user picture as an avatar.
         * @param drawableId ID of drawable to use as an avatar.
         */
        private void setPictureAvatar(Integer drawableId) {
            avatarBackground.setImageResource(drawableId);
        }

        /**
         * Uses a initial as an avatar.
         * @param name User's name.
         */
        private void setLetterAvatar(String name) {
            avatarLetter.setText(name.substring(0, 1));
        }
    }
}
