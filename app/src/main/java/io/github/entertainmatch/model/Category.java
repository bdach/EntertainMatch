package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.utils.ICloneable;
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
public class Category implements Parcelable, ICloneable<Category> {
    private final String name;
    @Setter
    private Integer voteCount = 0;
    @Setter
    private boolean votedFor = false;
    private final String imageUrl;
    @Getter
    private String id;

    protected Category(Parcel in) {
        name = in.readString();
        voteCount = in.readInt();
        imageUrl = in.readString();
        id = in.readString();
        votedFor = in.readInt() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(voteCount);
        dest.writeString(imageUrl);
        dest.writeString(id);
        dest.writeInt(votedFor ? 1 : 0);
    }

    @Override
    public Category clone() {
        return new Category(name, voteCount, votedFor, imageUrl, id);
    }
}
