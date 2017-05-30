package io.github.entertainmatch.model;

import android.os.Parcel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 09.05.17
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StaffPickEvent extends Event {

    private String detailsUrl;

    protected StaffPickEvent(Parcel source) {
        id = source.readString();
        title = source.readString();
        drawableUri = source.readString();
        description = source.readString();
        detailsUrl = source.readString();
        duration = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StaffPickEvent> CREATOR = new Creator<StaffPickEvent>() {
        @Override
        public StaffPickEvent createFromParcel(Parcel source) {
            return new StaffPickEvent(source);
        }

        @Override
        public StaffPickEvent[] newArray(int size) {
            return new StaffPickEvent[0];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(drawableUri);
        dest.writeString(description);
        dest.writeString(detailsUrl);
        dest.writeInt(duration);
    }
}
