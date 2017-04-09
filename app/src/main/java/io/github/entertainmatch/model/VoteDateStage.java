package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.date.VoteDateActivity;

/**
 * @author Bartlomiej Dach
 * @since 09.04.17
 */
public class VoteDateStage implements PollStage {
    @Override
    public Intent getViewStageIntent(Activity callingActivity) {
        return new Intent(callingActivity, VoteDateActivity.class);
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_date;
    }
}
