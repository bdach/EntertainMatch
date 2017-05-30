package io.github.entertainmatch;

import android.app.Application;

import io.github.entertainmatch.facebook.DaggerFacebookComponent;
import io.github.entertainmatch.facebook.FacebookComponent;
import io.github.entertainmatch.facebook.FacebookModule;
import lombok.Getter;

/**
 * Created by Adrian Bednarz on 5/30/17.
 */

public class DaggerApplication extends Application {
    @Getter
    private static DaggerApplication app;

    @Getter
    FacebookComponent facebookComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        initFacebookComponent();
        facebookComponent.inject(this);
    }

    private void initFacebookComponent() {
        facebookComponent = DaggerFacebookComponent.builder().facebookModule(new FacebookModule(this)).build();
    }
}
