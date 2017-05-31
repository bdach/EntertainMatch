package io.github.entertainmatch.facebook;

import android.content.Context;

import com.facebook.FacebookSdk;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to initialize the required Facebook SDKs.
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

    public void initNonStatic(Context context) {
        FacebookSdk.sdkInitialize(context);
    }
}
