package io.github.entertainmatch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import com.facebook.login.widget.ProfilePictureView;

/**
 * @author Bartlomiej Dach
 * @since 11.05.17
 */
public class CircularProfilePictureView extends ProfilePictureView {

    public CircularProfilePictureView(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), getWidth() / 2, getHeight() / 2, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }
}
