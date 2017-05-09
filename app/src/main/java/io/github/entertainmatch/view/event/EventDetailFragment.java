package io.github.entertainmatch.view.event;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import io.github.entertainmatch.model.Event;

/**
 * @author Bartlomiej Dach
 * @since 09.05.17
 */
public abstract class EventDetailFragment extends Fragment {
    public abstract View.OnClickListener getFabListener();
    public abstract int getFabIconResource();

    protected void applyImage(Event event, ImageView imageView, CollapsingToolbarLayout layout) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                setColors(Palette.from(bitmap).generate().getMutedSwatch(), layout);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(getContext())
                .load(event.getDrawableUri())
                .into(target);
    }


    private void setColors(Palette.Swatch detailSwatch, CollapsingToolbarLayout layout) {
        layout.setContentScrimColor(detailSwatch.getRgb());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
        layout.setCollapsedTitleTextColor(detailSwatch.getTitleTextColor());
    }
}
