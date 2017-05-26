package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/6/17.
 */

@Getter
@RequiredArgsConstructor
public class PollStub implements Parcelable {
    private final String name;
    private final Person[] members;
    private final String city;

    protected PollStub(Parcel in) {
        name = in.readString();
        members = in.createTypedArray(Person.CREATOR);
        city = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedArray(members, flags);
        dest.writeString(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PollStub> CREATOR = new Creator<PollStub>() {
        @Override
        public PollStub createFromParcel(Parcel in) {
            return new PollStub(in);
        }

        @Override
        public PollStub[] newArray(int size) {
            return new PollStub[size];
        }
    };
}
