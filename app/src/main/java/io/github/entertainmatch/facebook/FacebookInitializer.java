package io.github.entertainmatch.facebook;

import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by Adrian Bednarz on 5/22/17.
 */

public class FacebookInitializer {
    public static void init(Context context) {
        FacebookSdk.sdkInitialize(context);
    }
}
