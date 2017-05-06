package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/6/17.
 */

@RequiredArgsConstructor
public class PollStub implements Parcelable {
    @Getter
    private final String name;
    @Getter
    private final Person[] members;

    protected PollStub(Parcel in) {
        name = in.readString();
        members = in.createTypedArray(Person.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedArray(members, flags);
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
