package io.github.entertainmatch.view.dagger;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * @author Bartlomiej Dach
 * @since 31.05.17
 */
public class MockTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl,
                                      String className,
                                      Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(
                cl, MockApplication.class.getName(), context
        );
    }
}
