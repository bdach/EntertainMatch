package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.utils.ListExt;
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
@Getter
public class Category implements Parcelable {
    private final String name;
    private Integer voteCount = 0;
    private boolean votedFor = false;
    private final String imageUrl;

    protected Category(Parcel in) {
        name = in.readString();
        voteCount = in.readInt();
        imageUrl = in.readString();
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

    // TODO: fix me
    public static List<FirebaseCategoryTemplate> temporaryCache;
    public static List<Category> mockData() {
        if (temporaryCache == null)
            return new ArrayList<>();
        return ListExt.map(temporaryCache, FirebaseCategoryTemplate::toCategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(voteCount);
        dest.writeString(imageUrl);
    }
}
