package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.date.VoteDateActivity;
import lombok.RequiredArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 09.04.17
 */
@RequiredArgsConstructor
public class VoteDateStage implements PollStage {
    private final String pollId;

    @Override
    public Intent getViewStageIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, VoteDateActivity.class);
        intent.putExtra(PollStage.POLL_ID_KEY, pollId);
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_date;
    }
}
