package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Intent;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
public interface PollStage {
    Intent getViewStageIntent(Activity callingActivity);
    int getStageStringId();
}
