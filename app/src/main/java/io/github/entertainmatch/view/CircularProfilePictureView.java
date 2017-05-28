package io.github.entertainmatch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Custom extension of {@link ProfilePictureView}, in which the image
 * is clipped by a circle.
 *
 * @author Bartlomiej Dach
 * @since 11.05.17
 */
public class CircularProfilePictureView extends ProfilePictureView {

    /**
     * Constructor.
     * @param context The {@link Context} in which to create the picture.
     */
    public CircularProfilePictureView(Context context) {
        super(context);
    }

    /**
     * Called when the view is drawn.
     * @param canvas The {@link Canvas} upon which the view is to be drawn.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = new Path();
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        path.addRoundRect(rect, getWidth() / 2, getHeight() / 2, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }
}
