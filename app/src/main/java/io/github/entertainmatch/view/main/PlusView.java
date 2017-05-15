package io.github.entertainmatch.view.main;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;

/**
 * @author Bartlomiej Dach
 * @since 15.05.17
 */
public class PlusView extends RelativeLayout {
    @BindView(R.id.plus_count)
    TextView plusText;

    public PlusView(Context context, Integer count) {
        super(context);
        init(count);
    }

    private void init(Integer count) {
        inflate(getContext(), R.layout.fragment_plus, this);
        ButterKnife.bind(this);
        plusText.setText("+" + count);
    }
}
