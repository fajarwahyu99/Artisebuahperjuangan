package com.example.infolabsolution.thelastsubmission;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        NotificationTasks.executeTask(this, action);
    }
}
