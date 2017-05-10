package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.event.EventListActivity;
import lombok.RequiredArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 02.04.17
 */
@RequiredArgsConstructor
public class VoteEventStage implements PollStage {
    private final String pollId;

    @Override
    public Intent getViewStageIntent(Context context) {
        Intent intent = new Intent(context, EventListActivity.class);
        intent.putExtra(PollStage.POLL_ID_KEY, pollId);
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_event;
    }
}
