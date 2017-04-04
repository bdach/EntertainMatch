package io.github.entertainmatch.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import io.github.entertainmatch.R;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 02.04.17
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class MovieEvent implements Parcelable {
    @Getter
    private String title;
    @Getter
    private Integer drawableId;
    @Getter
    private String synopsis;
    @Getter
    private String director;
    @Getter
    private String cast;
    @Getter
    private Uri youtubeTrailerUrl;
    @Getter
    private Integer rottenTomatoesScore;

    protected MovieEvent(Parcel in) {
        title = in.readString();
        drawableId = in.readInt();
        synopsis = in.readString();
        director = in.readString();
        cast = in.readString();
        youtubeTrailerUrl = Uri.parse(in.readString());
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
                        R.drawable.movie_example,
                        "In the near future, Major is the first of her kind: A human saved from a terrible crash, who is cyber-enhanced to be a perfect soldier devoted to stopping the world's most dangerous criminals.",
                        "Rupert Sanders",
                        "Scarlett Johansson, Pilou Asbaek, Takeshi Kitano",
                        Uri.parse("https://www.youtube.com/watch?v=G4VmJcZR0Yg"),
                        42
                ),
                new MovieEvent(
                        "Ghost in the Shell",
                        R.drawable.movie_example,
                        "In the near future, Major is the first of her kind: A human saved from a terrible crash, who is cyber-enhanced to be a perfect soldier devoted to stopping the world's most dangerous criminals.",
                        "Rupert Sanders",
                        "Scarlett Johansson, Pilou Asbaek, Takeshi Kitano",
                        Uri.parse("https://www.youtube.com/watch?v=G4VmJcZR0Yg"),
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
        dest.writeInt(drawableId);
        dest.writeString(synopsis);
        dest.writeString(director);
        dest.writeString(cast);
        dest.writeString(youtubeTrailerUrl.toString());
        dest.writeInt(rottenTomatoesScore);
    }
}
