package io.github.entertainmatch.facebook;

import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Class used to initialize the required Facebook SDKs.
 *
 * TODO: Possibly remove calls of {@link #init(Context)} with {@link FacebookSdk#sdkInitialize(Context)}?
 *
 * @author Adrian Bednarz
 * @since 5/22/17
 */
public class FacebookInitializer {
    /**
     * Initializes the SDK for the supplied {@link Context}.
     * @param context The {@link Context} within which to initialize the Facebook SDKs.
     */
    public static void init(Context context) {
        FacebookSdk.sdkInitialize(context);
    }
}
