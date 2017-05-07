package io.github.entertainmatch.model;

import android.os.Parcelable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 07.05.17
 */
@Getter
@NoArgsConstructor
public abstract class Event implements Parcelable {
    protected String id;
    protected String title;
    protected String drawableUri;
    protected String description;
}
