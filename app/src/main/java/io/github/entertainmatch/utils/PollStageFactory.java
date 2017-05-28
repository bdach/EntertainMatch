package io.github.entertainmatch.utils;

import android.content.Context;
import android.content.Intent;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteDateStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.model.VoteResultStage;

/**
 * Factory for subclasses of {@link PollStage}.
 *
 * @author Adrian Bednarz
 * @since 5/6/17
 */
public class PollStageFactory {
    /**
     * Gets a {@link PollStage} instance for a poll at the given stage.
     * @param stage The identifier of the stage.
     * @param pollId The ID string of the poll to navigate to.
     * @return An instance of a subclass of {@link PollStage}.
     */
    public static PollStage get(String stage, String pollId) {
        if (stage.equals(VoteCategoryStage.class.toString()))
            return new VoteCategoryStage(pollId);
        else if (stage.equals(VoteEventStage.class.toString()))
            return new VoteEventStage(pollId);
        else if (stage.equals(VoteDateStage.class.toString()))
            return new VoteDateStage(pollId);
        else if (stage.equals(VoteResultStage.class.toString()))
            return new VoteResultStage(pollId);

        throw new UnsupportedOperationException("Unsupported stage");
    }

    /**
     * Gets an {@link Intent} to navigate to a poll with the supplied class.
     * @param stage Subclass of {@link PollStage} indicating the stage the
     *              poll is currently at.
     * @param pollId ID string of the poll.
     * @param context The context to use when creating the {@link Intent}.
     * @return A ready {@link Intent} object enabling navigation to the given
     * poll.
     */
    public static Intent getIntentForStage(Class<? extends PollStage> stage,
                                           String pollId,
                                           Context context) {
        if (stage.equals(VoteCategoryStage.class))
            return new VoteCategoryStage(pollId).getViewStageIntent(context);
        else if (stage.equals(VoteEventStage.class))
            return new VoteEventStage(pollId).getViewStageIntent(context);
        else if (stage.equals(VoteDateStage.class))
            return new VoteDateStage(pollId).getViewStageIntent(context);
        else if (stage.equals(VoteResultStage.class))
            return new VoteResultStage(pollId).getViewStageIntent(context);
        throw new UnsupportedOperationException("Unsupported stage");
    }
}
