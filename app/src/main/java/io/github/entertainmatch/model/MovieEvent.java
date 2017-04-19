package io.github.entertainmatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.entertainmatch.R;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 02.04.17
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieEvent implements Parcelable {
    /**
     * Title of the event.
     */
    private String title;
    /**
     * Uri of the drawable to use as event image.
     */
    private String drawableUri;
    /**
     * Movie synopsis.
     */
    private String synopsis;
    /**
     * Movie director.
     */
    private String director;
    /**
     * Movie cast.
     */
    private String cast;
    /**
     * YouTube URL to the movie trailer.
     */
    private String youtubeTrailerUrl;
    /**
     * Movie score on Rotten Tomatoes.
     */
    private Integer rottenTomatoesScore;

    protected MovieEvent(Parcel in) {
        title = in.readString();
        drawableUri = in.readString();
        synopsis = in.readString();
        director = in.readString();
        cast = in.readString();
        youtubeTrailerUrl = in.readString();
        rottenTomatoesScore = in.readInt();
    }

    public static final Creator<MovieEvent> CREATOR = new Creator<MovieEvent>() {
        @Override
        public MovieEvent createFromParcel(Parcel in) {
            return new MovieEvent(in);
        }

        @Override
        public MovieEvent[] newArray(int size) {
            return new MovieEvent[size];
        }
    };

    public static List<MovieEvent> mockData() {
        return Arrays.asList(
                new MovieEvent(
                        "Ghost in the Shell",
                        "http://i.imgur.com/PLFkStW.jpg",
                        "In the near future, Major is the first of her kind: A human saved from a terrible crash, who is cyber-enhanced to be a perfect soldier devoted to stopping the world's most dangerous criminals.",
                        "Rupert Sanders",
                        "Scarlett Johansson, Pilou Asbaek, Takeshi Kitano",
                        "https://www.youtube.com/watch?v=G4VmJcZR0Yg",
                        42
                ),
                new MovieEvent(
                        "Ghost in the Shell",
                        "http://i.imgur.com/PLFkStW.jpg",
                        "In the near future, Major is the first of her kind: A human saved from a terrible crash, who is cyber-enhanced to be a perfect soldier devoted to stopping the world's most dangerous criminals.",
                        "Rupert Sanders",
                        "Scarlett Johansson, Pilou Asbaek, Takeshi Kitano",
                        "https://www.youtube.com/watch?v=G4VmJcZR0Yg",
                        42
                )
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(drawableUri);
        dest.writeString(synopsis);
        dest.writeString(director);
        dest.writeString(cast);
        dest.writeString(youtubeTrailerUrl);
        dest.writeInt(rottenTomatoesScore);
    }
}
