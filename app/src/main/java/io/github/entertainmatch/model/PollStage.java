package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
public interface PollStage {
    String POLL_ID_KEY = "poll_id";

    Intent getViewStageIntent(Context context);
    int getStageStringId();
}
