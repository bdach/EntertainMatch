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
    /**
     * Identifier of this event date in Firebase
     */
    private final String id;
    /**
     * Identifier of location
     */
    private final String locationId;
    /**
     * Name of the location where the event takes place.
     */
    private final String place;
    /**
     * Latitude of the place location.
     */
    private final Double lat;
    /**
     * Longtitude of the place location.
     */
    private final Double lon;
    /**
     * The date on which the event is to take place.
     */
    private final Date date;

    protected EventDate(Parcel in) {
        id = in.readString();
        locationId = in.readString();
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
        dest.writeString(id);
        dest.writeString(locationId);
        dest.writeString(place);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeSerializable(date);
    }

    public static List<EventDate> mockData() {
        return Arrays.asList(
                new EventDate(
                        "_0",
                        "_0",
                        "Cinema City Warszawa, Galeria Mokotów",
                        52.179182,
                        21.004422,
                        new Date(2017 - 1900, 4, 9, 19, 40, 0)
                ),
                new EventDate(
                        "_1",
                        "_1",
                        "Cinema City Warszawa, Sadyba",
                        52.187350,
                        21.061075,
                        new Date(2017 - 1900, 4, 9, 21, 40, 0)
                )
        );
    }

    /**
     * Returns a {@link Uri} that can be passed to an {@link android.content.Intent} to pull up the location
     * on the map.
     *
     * @return {@link Uri} to pass to the intent to display the map.
     */
    public Uri getGoogleMapsUri() {
        String uriString = String.format(
                Locale.ENGLISH, "geo:%f,%f", lat, lon
        );
        return Uri.parse(uriString);
    }
}
