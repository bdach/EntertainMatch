package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.result.VoteResultActivity;
import lombok.AllArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 10.04.17
 */
@AllArgsConstructor
public class VoteResultStage implements PollStage {
    private String pollId;
    @Override
    public Intent getViewStageIntent(Context context) {
        Intent intent = new Intent(context, VoteResultActivity.class);
        intent.putExtra(VoteResultStage.POLL_ID_KEY, pollId);
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_completed;
    }
}
