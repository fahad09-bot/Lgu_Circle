package com.codesses.lgucircle.fcm;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.activity.MainActivity;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.model.Service;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Developed By: Codesses
 * Developer Name: Saad Iftikhar
 *
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "Bestmarts";
    private static final String CHANNEL_NAME = "Bestmarts";
    Intent resultIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotification1(remoteMessage);
        } else {
            sendNotification(remoteMessage);
        }
    }

    @SuppressLint("LongLogTag")
    private void sendNotification(RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app
            Log.e("remoteMessage foreground", remoteMessage.getData().toString());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            if (title.equals("message")) {
                resultIntent = new Intent(getApplicationContext(), ServicesChatAC.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, remoteMessage.getData().get("c_id"));
                resultIntent.putExtras(bundle);
            }

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setTicker("Bestmarts")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");

            Glide.with(getApplicationContext()).asBitmap().load(remoteMessage.getData().get("user_image"))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            notificationBuilder.setLargeIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });
            notificationManager.notify(1, notificationBuilder.build());
        } else {
            Log.e("remoteMessage background", remoteMessage.getData().toString());
            Map data = remoteMessage.getData();
            String title = String.valueOf(data.get("title"));
            String body = String.valueOf(data.get("body"));
            if (title.equals("message")) {
                resultIntent = new Intent(getApplicationContext(), ServicesChatAC.class);
                resultIntent.putExtra(Constants.USER_ID, remoteMessage.getData().get("c_id"));
            }
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setTicker("Bestmarts")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");

            Glide.with(getApplicationContext()).asBitmap().load(remoteMessage.getData().get("user_image"))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            notificationBuilder.setLargeIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });

            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @SuppressLint("NewApi")
    private void sendNotification1(RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app
            Log.e("remoteMessage", remoteMessage.getData().toString());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            if (title.equals("message")) {
                resultIntent = new Intent(getApplicationContext(), ServicesChatAC.class);
                resultIntent.putExtra(Constants.USER_ID, remoteMessage.getData().get("c_id"));
            }
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultsound, String.valueOf(R.mipmap.ic_launcher_foreground));

            int i = 0;
            oreoNotification.getManager().notify(i, builder.build());
        } else {
            Log.e("remoteMessage", remoteMessage.getData().toString());
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            if (title.equals("message")) {
                resultIntent = new Intent(getApplicationContext(), ServicesChatAC.class);
                resultIntent.putExtra(Constants.USER_ID, remoteMessage.getData().get("c_id"));
            }
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultsound, String.valueOf(R.mipmap.ic_launcher_foreground));

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int i = 0;
            oreoNotification.getManager().notify(i, builder.build());
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPrefManager.getInstance(getApplicationContext()).storeSharedData(SharedPrefKey.TOKEN, s);
    }

}
