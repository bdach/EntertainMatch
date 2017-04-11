package io.github.entertainmatch.view.poll;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private final List<Person> people;
    private final PersonFragment.OnListFragmentInteractionListener listener;

    public PersonRecyclerViewAdapter(ArrayList<Person> people, PersonFragment.OnListFragmentInteractionListener listener) {
        this.people = people;
        this.listener = listener;
    }

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
        private TextView personName;
        private CheckBox personSelected;
        private ImageView avatarBackground;
        private TextView avatarLetter;
        private Person person;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.personName = (TextView) view.findViewById(R.id.person_name);
            this.personSelected = (CheckBox) view.findViewById(R.id.person_selected);
            this.avatarBackground = (ImageView) view.findViewById(R.id.person_avatar_background);
            this.avatarLetter = (TextView) view.findViewById(R.id.person_avatar_letter);
        }

        public void setItem(Person item) {
            this.person = item;
            personName.setText(item.getName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personSelected.toggle();
                }
            });
            setAvatar(item);
        }

        private void setAvatar(Person person) {
            if (person.getDrawableId() == null) {
                setLetterAvatar(person.getName());
            } else {
                setPictureAvatar(person.getDrawableId());
            }
        }

        private void setPictureAvatar(Integer drawableId) {
            avatarBackground.setImageResource(drawableId);
        }

        private void setLetterAvatar(String name) {
            avatarLetter.setText(name.substring(0, 1));
        }
    }
}
