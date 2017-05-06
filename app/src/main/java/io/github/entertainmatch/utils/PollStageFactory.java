package io.github.entertainmatch.utils;

import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteDateStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.model.VoteResultStage;

/**
 * Created by Adrian Bednarz on 5/6/17.
 */

public class PollStageFactory {
    // TODO: quick hack
    public static PollStage get(String stage, String pollId) {
        if (stage.equals(VoteCategoryStage.class.toString()))
            return new VoteCategoryStage(pollId);
        else if (stage.equals(VoteEventStage.class.toString()))
            return new VoteEventStage(pollId);
        else if (stage.equals(VoteDateStage.class.toString()))
            return new VoteDateStage();
        else if (stage.equals(VoteResultStage.class.toString()))
            return new VoteResultStage();

        throw new UnsupportedOperationException("Unsupported stage");
    }
}
