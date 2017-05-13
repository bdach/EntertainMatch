package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebaseCategory;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.utils.ListExt;
import io.github.entertainmatch.view.category.VoteCategoryActivity;
import io.github.entertainmatch.view.category.VoteCategoryData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@NoArgsConstructor
public class VoteCategoryStage implements PollStage {
    @Getter
    private String pollId;

    public VoteCategoryStage(String pollId) {
        this.pollId = pollId;
    }

    @Override
    public Intent getViewStageIntent(Context context) {
        Intent intent = new Intent(context, VoteCategoryActivity.class);
        intent.putExtra(VoteCategoryActivity.CATEGORIES_KEY, new VoteCategoryData(pollId));
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_category;
    }
}
