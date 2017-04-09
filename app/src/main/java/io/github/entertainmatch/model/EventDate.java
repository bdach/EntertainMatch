package io.github.entertainmatch.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @author Bartlomiej Dach
 * @since 09.04.17
 */
@RequiredArgsConstructor
@Getter
public class EventDate implements Parcelable {
    private final String place;
    private final Double lat;
    private final Double lon;
    private final Date date;

    protected EventDate(Parcel in) {
        place = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        date = (Date) in.readSerializable();
    }

    public static final Creator<EventDate> CREATOR = new Creator<EventDate>() {
        @Override
        public EventDate createFromParcel(Parcel in) {
            return new EventDate(in);
        }

        @Override
        public EventDate[] newArray(int size) {
            return new EventDate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeSerializable(date);
    }

    public static List<EventDate> mockData() {
        return Arrays.asList(
                new EventDate(
                        "Cinema City Warszawa, Galeria Mokotów",
                        52.179182,
                        21.004422,
                        new Date(2017 - 1900, 4, 9, 19, 40, 0)
                ),
                new EventDate(
                        "Cinema City Warszawa, Sadyba",
                        52.187350,
                        21.061075,
                        new Date(2017 - 1900, 4, 9, 21, 40, 0)
                )
        );
    }

    public Uri getGoogleMapsUri() {
        String uriString = String.format(
                Locale.ENGLISH, "geo:%f,%f", lat, lon
        );
        return Uri.parse(uriString);
    }
}
