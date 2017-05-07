package io.github.entertainmatch.model;

import android.os.Parcel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Bartlomiej Dach
 * @since 07.05.17
 */
@Getter
@AllArgsConstructor
public class ConcertEvent extends Event {

    protected ConcertEvent(Parcel src) {
        id = src.readString();
        title = src.readString();
        description = src.readString();
        drawableUri = src.readString();
    }

    public static final Creator<ConcertEvent> CREATOR = new Creator<ConcertEvent>() {
        @Override
        public ConcertEvent createFromParcel(Parcel source) {
            return new ConcertEvent(source);
        }

        @Override
        public ConcertEvent[] newArray(int size) {
            return new ConcertEvent[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(drawableUri);
    }
}
