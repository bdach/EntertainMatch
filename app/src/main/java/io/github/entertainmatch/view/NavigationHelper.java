package io.github.entertainmatch.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static android.app.Activity.RESULT_OK;

/**
 * Helper class, used to simplify navigation over multiple views.
 *
 * @author Bartlomiej Dach
 * @since 22.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NavigationHelper {
    /**
     * Handles a user's back click.
     * @param context {@link AppCompatActivity} the back press came from.
     * @param pollId ID of the poll that was navigated back from.
     */
    public static void back(AppCompatActivity context, String pollId) {
        if (context.getCallingActivity() != null) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.STAGE_FINISHED_POLL_ID_KEY, pollId);
            context.setResult(RESULT_OK, intent);
        } else {
            // ??
            context.startActivity(new Intent(context, MainActivity.class));
        }
        context.finish();
    }
}
