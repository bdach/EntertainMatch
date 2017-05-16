package io.github.entertainmatch.view.main;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.facebook.login.widget.ProfilePictureView;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.view.CircularProfilePictureView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 15.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AvatarHelper {
    public static void addPlus(int count, LinearLayout layout, Context context) {
        PlusView plusView = new PlusView(context, count);
        LinearLayout.LayoutParams params = getParamsWithMargin(context);
        layout.addView(plusView, params);
    }

    public static void addMemberAvatar(String personId, LinearLayout layout, Context context) {
        ProfilePictureView pictureView = new CircularProfilePictureView(context);
        pictureView.setProfileId(personId);
        pictureView.setPresetSize(ProfilePictureView.SMALL);
        LinearLayout.LayoutParams params = getParamsWithMargin(context);
        layout.addView(pictureView, params);
    }

    private static LinearLayout.LayoutParams getParamsWithMargin(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                r.getDisplayMetrics()
        );
        params.setMargins(px, px, px, px);
        return params;
    }
}
