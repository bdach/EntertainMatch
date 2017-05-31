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
    protected FacebookComponent facebookComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        initFacebookComponent();
        facebookComponent.inject(this);
    }

    protected void initFacebookComponent() {
        facebookComponent = DaggerFacebookComponent.builder().facebookModule(new FacebookModule(this)).build();
    }
}
