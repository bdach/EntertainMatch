package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.event.EventListActivity;

/**
 * @author Bartlomiej Dach
 * @since 02.04.17
 */
public class VoteEventStage implements PollStage {
    @Override
    public Intent getViewStageIntent(Activity callingActivity) {
        return new Intent(callingActivity, EventListActivity.class);
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_event;
    }
}
