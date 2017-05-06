package io.github.entertainmatch.model;

import android.app.Activity;
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
    // TODO: Once loaded they doesn't change I guess, probably we need something more robust
    public static ArrayList<Category> categoriesTemplates = new ArrayList<>();

    @Getter
    private ArrayList<Category> categories = ListExt.clone(categoriesTemplates);

    @Getter
    private String pollId;

    public VoteCategoryStage(String pollId) {
        this.pollId = pollId;
        // update categories only if poll already exists
        FirebasePoll poll = FirebasePollController.polls.get(pollId);
        if (poll != null) {
            categories.forEach(poll::setValues);
        }
    }

    @Override
    public Intent getViewStageIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, VoteCategoryActivity.class);
        intent.putExtra(VoteCategoryActivity.CATEGORIES_KEY, new VoteCategoryData(pollId, categories));
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_category;
    }
}
