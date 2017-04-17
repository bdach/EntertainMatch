package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.entertainmatch.R;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Person implements Parcelable {
    /**
     * Name of the person.
     */
    private final String name;
    /**
     * ID of the drawable to use as the person's avatar.
     */
    private Integer drawableId;

    protected Person(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public static List<Person> mockData() {
        return Arrays.asList(
                new Person("John Angle"),
                new Person("Katie Bell"),
                new Person("Leon Carson", R.drawable.person)
        );
    }
}
