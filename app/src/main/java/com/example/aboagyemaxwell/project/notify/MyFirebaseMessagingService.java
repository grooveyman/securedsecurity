package com.example.aboagyemaxwell.project.notify;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.aboagyemaxwell.project.R;
import com.example.aboagyemaxwell.project.SuspectFragment;
import com.example.aboagyemaxwell.project.ViewBroadcast;
import com.example.aboagyemaxwell.project.broadcastActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String ADMIN_CHANNEL_ID ="admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> extraData = remoteMessage.getData();
        String category = extraData.get("category");
         Intent intent = null;
        assert category != null;
        if(category.equals("broadcast")){
             intent = new Intent(this, ViewBroadcast.class);
        } else if(category.equals("suspect")){
            intent = new Intent(this, SuspectFragment.class);
        }

//        = new Intent(this, ViewBroadcast.class);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

      /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        assert intent != null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.apblogo);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.apblogo)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

//    private final String ADMIN_CHANNEL_ID ="admin_channel";
//
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//            super.onMessageReceived(remoteMessage);
//
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            Map<String, String> extraData = remoteMessage.getData();
//
//            String brandId = extraData.get("brandId");
//            String category = extraData.get("category");
//
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, "apb")
//                            .setContentTitle(title)
//                            .setContentText(body)
//                            .setSmallIcon(R.drawable.apblogo);
//
//            Intent intent;
//        assert category != null;
//        if (category.equals("broadcast")) {
//                intent = new Intent(this, broadcastActivity.class);
//
//            } else if(category.equals("suspect")) {
//            intent = new Intent(this, SuspectFragment.class);
//        }else {
//            intent = new Intent(this, SuspectFragment.class);
//
//            }
//            intent.putExtra("brandId", brandId);
//            intent.putExtra("category", category);
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            notificationBuilder.setContentIntent(pendingIntent);
//
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                notificationBuilder.setSmallIcon(R.drawable.apblogo);
//                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.apblogo));
//                NotificationChannel channel = new NotificationChannel("Security","demo",NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//
//            int id =  (int) System.currentTimeMillis();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                NotificationChannel channel = new NotificationChannel("Security","demo",NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//            notificationManager.notify(id,notificationBuilder.build());
//
//        }
    }
