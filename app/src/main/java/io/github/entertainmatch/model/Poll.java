package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import io.github.entertainmatch.firebase.FirebaseController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    @Setter
    private PollStage pollStage;
    /**
     * Other users who are a part of the poll.
     */
    private final Person[] members;

    protected Poll(Parcel in) {
        name = in.readString();
        members = in.createTypedArray(Person.CREATOR);
        pollStage = new VoteCategoryStage();
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

    public static List<Poll> mockData() {
        return Arrays.asList(
                new Poll("Test poll", new VoteCategoryStage(), new Person[0]),
                new Poll("Another test poll", new VoteEventStage(), new Person[0]),
                new Poll("Yet another test poll", new VoteDateStage(), new Person[0]),
                new Poll("And yet another poll", new VoteResultStage(), new Person[0])
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedArray(members, 0);
    }

    public String stageName() {
        return pollStage.getClass().toString();
    }
}
