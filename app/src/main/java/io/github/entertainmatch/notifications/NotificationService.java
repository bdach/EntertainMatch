package io.github.entertainmatch.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.FirebaseUserController;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Adrian Bednarz on 5/10/17.
 */

public class NotificationService extends IntentService {
    @Inject
    FacebookUsers FacebookUsers;

    public NotificationService() {
        super("NotificationService");
        DaggerApplication.getApp().getFacebookComponent().inject(this);

        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        FirebaseUserController.getUser(facebookId).subscribe(user -> {
            Log.d("NotificationsService", "User data changed");
            if (user == null)
                return;

            // category
            for (Map.Entry<String, Boolean> entry : user.getPolls().entrySet()) {
                if (entry.getValue()) {
                    Log.d("NotificationsServiceC", entry.getKey());
                    Notifications.notifyPollStarted(this, entry.getKey());
                }
            }

            // event
            for (Map.Entry<String, Boolean> entry : user.getEvents().entrySet()) {
                if (entry.getValue()) {
                    Log.d("NotificationsServiceE", entry.getKey());
                    Notifications.notifyEventStageStarted(this, entry.getKey());
                }
            }

            // date
            for (Map.Entry<String, Boolean> entry : user.getDates().entrySet()) {
                if (entry.getValue()) {
                    Log.d("NotificationsServiceD", entry.getKey());
                    Notifications.notifyDateStageStarted(this, entry.getKey());
                }
            }

            // finish
            for (Map.Entry<String, Boolean> entry : user.getFinished().entrySet()) {
                if (entry.getValue()) {
                    Log.d("NotificationsServiceF", entry.getKey());
                    Notifications.notifyFinished(this, entry.getKey());
                }
            }

            FirebaseUserController.makePollsOldForUser(facebookId, user);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NotificationsService", "onDestroy");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
