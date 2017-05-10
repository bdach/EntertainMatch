package io.github.entertainmatch.model;

import android.os.Parcel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 09.05.17
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayEvent extends Event {
    private String director;
    private String cast;
    private String costumes;
    private String scenography;
    private Integer duration;
    private String youtubeTrailerUrl;

    public static final Creator<PlayEvent> CREATOR = new Creator<PlayEvent>() {
        @Override
        public PlayEvent createFromParcel(Parcel source) {
            return new PlayEvent(source);
        }

        @Override
        public PlayEvent[] newArray(int size) {
            return new PlayEvent[0];
        }
    };

    protected PlayEvent(Parcel source) {
        id = source.readString();
        title = source.readString();
        drawableUri = source.readString();
        description = source.readString();
        director = source.readString();
        cast = source.readString();
        costumes = source.readString();
        scenography = source.readString();
        duration = source.readInt();
        youtubeTrailerUrl = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(drawableUri);
        dest.writeString(description);
        dest.writeString(director);
        dest.writeString(cast);
        dest.writeString(costumes);
        dest.writeString(scenography);
        dest.writeInt(duration);
        dest.writeString(youtubeTrailerUrl);
    }
}
