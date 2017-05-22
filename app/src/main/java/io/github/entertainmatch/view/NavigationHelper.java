package io.github.entertainmatch.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static android.app.Activity.RESULT_OK;

/**
 * @author Bartlomiej Dach
 * @since 22.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NavigationHelper {
    public static void back(AppCompatActivity context, String pollId) {
        if (context.getCallingActivity() != null) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.FINISHED_POLL_ID_KEY, pollId);
            context.setResult(RESULT_OK, intent);
        }
        context.finish();
    }
}
