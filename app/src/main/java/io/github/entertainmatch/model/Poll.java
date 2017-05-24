package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.utils.ListExt;
import io.github.entertainmatch.utils.PollStageFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Poll implements Parcelable {
    /**
     * Name of the poll.
     */
    private final String name;
    /**
     * The stage of the poll.
     */
    private PollStage pollStage;
    /**
     * Other users who are a part of the poll.
     */
    private final Person[] members;
    /**
     * Poll identifier
     */
    @Setter
    private String pollId;
    @Nullable
    private String drawableUri = null;

    protected Poll(Parcel in) {
        name = in.readString();
        pollId = in.readString();
        members = in.createTypedArray(Person.CREATOR);
        String stageName = in.readString();
        pollStage = PollStageFactory.get(stageName, pollId);
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel in) {
            return new Poll(in);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };

    // TODO: Makeshift constructor. Here until we decide how to fetch participants.
    public Poll(FirebasePoll firebasePoll, String userId) {
        this.name = firebasePoll.getName();
        this.pollStage = PollStageFactory.get(firebasePoll.getStage(), firebasePoll.getPollId());
        this.members = ListExt.map(firebasePoll.getParticipants(), (s) -> {
            Person person = new Person();
            person.facebookId = s;
            return person;
        }).toArray(new Person[firebasePoll.getParticipants().size()]);
        this.pollId = firebasePoll.getPollId();
        this.drawableUri = firebasePoll.getDrawableUri();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pollId);
        dest.writeTypedArray(members, 0);
        dest.writeString(stageName());
    }

    public String stageName() {
        return pollStage.getClass().toString();
    }

    public void update(Poll that) {
        this.pollStage = that.pollStage;
    }
}
