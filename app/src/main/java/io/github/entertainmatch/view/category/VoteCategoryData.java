package io.github.entertainmatch.view.category;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import io.github.entertainmatch.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/5/17.
 */

@NoArgsConstructor
@AllArgsConstructor
public class VoteCategoryData implements Parcelable {
    @Getter
    private String pollId;

    @Getter
    private ArrayList<Category> categories;

    protected VoteCategoryData(Parcel in) {
        pollId = in.readString();
        categories = new ArrayList<>();
        in.readTypedList(categories, Category.CREATOR);
    }

    public static final Creator<VoteCategoryData> CREATOR = new Creator<VoteCategoryData>() {
        @Override
        public VoteCategoryData createFromParcel(Parcel in) {
            return new VoteCategoryData(in);
        }

        @Override
        public VoteCategoryData[] newArray(int size) {
            return new VoteCategoryData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pollId);
        dest.writeTypedList(categories);
    }
}
