package io.github.entertainmatch.view.dagger;

import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.facebook.DaggerFacebookComponent;

/**
 * @author Bartlomiej Dach
 * @since 31.05.17
 */
public class MockApplication extends DaggerApplication {
    @Override
    protected void initFacebookComponent() {
        facebookComponent = DaggerFacebookComponent
                .builder()
                .facebookModule(new MockFacebookModule(this))
                .build();
    }
}
