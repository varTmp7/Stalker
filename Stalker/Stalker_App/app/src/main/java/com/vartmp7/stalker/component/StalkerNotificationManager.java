/*
 * MIT License
 *
 * Copyright (c) 2020 VarTmp7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vartmp7.stalker.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.vartmp7.stalker.MainActivity;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.Tools;

import org.jetbrains.annotations.NotNull;


public class StalkerNotificationManager {
    private static  final String PACKAGE_NAME="com.vartmp7.stalker.component.StalkerTrackingService";
    private static final String NOTIFICATION_CHANNEL_ID = "channel_01";
    static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    static final int NOTIFICATION_ID = 12345678;
    public static final String GO_TO_TRACKINGFRAGMENT = "go_to_tracking_fragment";
    private Context context;
    private NotificationManager manager;
    StalkerNotificationManager(@NotNull Context context, NotificationManager manager) {
        this.context =context;
        this.manager = manager;
        CharSequence name = context.getString(R.string.app_name);
        NotificationChannel mChannel =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

        // Set the Notification Channel for the Notification Manager.
        this.manager.createNotificationChannel(mChannel);

    }
    void notify(@NotNull Notification notification){

        manager.notify(NOTIFICATION_ID,notification);
    }

    Notification getNotification(String text) {
        Intent intent = new Intent(context, StalkerTrackingService.class);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(GO_TO_TRACKINGFRAGMENT,true), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "")
                .addAction(R.drawable.icon_stalker, context.getString(R.string.apri_app),
                        activityPendingIntent)
                .setContentTitle(Tools.getLocationTitle(context))
                .setOngoing(true)
                .setPriority(Notification.BADGE_ICON_LARGE)
                .setSmallIcon(R.drawable.people)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        builder.setChannelId(NOTIFICATION_CHANNEL_ID); // Channel ID

        return builder.build();
    }
}
