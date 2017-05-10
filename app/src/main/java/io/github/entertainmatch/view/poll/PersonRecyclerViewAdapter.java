package io.github.entertainmatch.view.poll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.login.widget.ProfilePictureView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Adapter used to display {@link Person} lists.
 */
@AllArgsConstructor
public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    /**
     * List of people to display.
     */
    @Setter
    private List<Person> people;
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
        TextView personName;
        /**
         * Indicates whether or not the person is selected.
         */
        @BindView(R.id.person_selected)
        CheckBox personSelected;
        /**
         * The person's avatar.
         */
        @BindView(R.id.person_avatar)
        ProfilePictureView personAvatar;
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
            view.setOnClickListener(v -> {
                personSelected.toggle();
                if (listener != null) {
                    listener.onPersonToggled(item, personSelected.isChecked());
                }
            });
            personAvatar.setProfileId(person.getFacebookId());
        }

    }
}
