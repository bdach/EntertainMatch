package io.github.entertainmatch.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.utils.PollStageFactory;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 5/10/17.
 */

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationServiceXDDD");

        String facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
        FirebaseUserController.getUser(facebookId).subscribe(user -> {
            user.getPolls().forEach((pollId, isNew) -> {
                if (isNew) Notifications.notifyPollStarted(this, pollId);
            });

            FirebaseUserController.makePollsOldForUser(facebookId, user);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Notification", "BOOM");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
