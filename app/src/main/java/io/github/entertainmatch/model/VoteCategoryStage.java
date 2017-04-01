package io.github.entertainmatch.model;

import android.app.Activity;
import android.content.Intent;
import io.github.entertainmatch.R;
import io.github.entertainmatch.view.category.VoteCategoryActivity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
public class VoteCategoryStage implements PollStage {
    @Getter
    private final ArrayList<Category> categories = Category.mockData();

    @Override
    public Intent getViewStageIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, VoteCategoryActivity.class);
        intent.putParcelableArrayListExtra(VoteCategoryActivity.CATEGORIES_KEY, categories);
        return intent;
    }

    @Override
    public int getStageStringId() {
        return R.string.poll_status_category;
    }
}
