package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.entertainmatch.R;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@AllArgsConstructor
public class Category implements Parcelable {
    @Getter
    private final String name;
    @Getter
    private Integer voteCount = 0;
    @Getter
    private boolean votedFor = false;
    @Getter
    private final Integer imageId;

    protected Category(Parcel in) {
        name = in.readString();
        voteCount = in.readInt();
        imageId = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public void registerVote() {
        voteCount++;
        votedFor = true;
    }

    static ArrayList<Category> mockData() {
        List<Category> list = Arrays.asList(
                new Category("Movies", 5, false, R.drawable.cinema),
                new Category("Concerts", 3, false, R.drawable.concert),
                new Category("Plays", 2, false, R.drawable.play),
                new Category("Staff Picks", 1, false, R.drawable.staffpick)
        );
        return new ArrayList<>(list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(voteCount);
        dest.writeInt(imageId);
    }
}
