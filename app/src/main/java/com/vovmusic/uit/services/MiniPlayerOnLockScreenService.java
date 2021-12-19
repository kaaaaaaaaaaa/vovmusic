package com.vovmusic.uit.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vovmusic.uit.R;

public class MiniPlayerOnLockScreenService extends Service {
    private MediaSession mediaSession;
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PRE = "action_pre";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_REWIND = "action_rewind";
    private static final String ACTION_FAST_FORWARD = "action_fast_forward";

    public static final String CHANNEL_ID = "CHANNEL iMusic MEDIA";

    private MediaPlayer mediaPlayer;
    private MediaSessionManager mediaSessionManager;
    private MediaController mediaController;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            buildNotification(initAction(R.drawable.ic_pause, "Pause", ACTION_PLAY));
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            buildNotification(initAction(R.drawable.ic_play_2, "Pause", ACTION_PAUSE));
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mediaController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_PRE)) {
            buildNotification(initAction(R.drawable.ic_prev, "Prev", ACTION_PLAY));
        } else if (action.equalsIgnoreCase(ACTION_REWIND)) {
            mediaController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            mediaController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            Intent intent1 = new Intent(getApplicationContext(), MiniPlayerOnLockScreenService.class);
            stopService(intent1);
        }
    }

    private NotificationCompat.Action initAction(int icon, String title, String intentAction) {
        //Intent intent = new Intent(getApplicationContext(),MiniPlayerOnLockScreenService.class);
        Intent intent = new Intent(getApplicationContext(), NotificationActionService.class);
        intent.setAction(intentAction);
        //PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,intent,0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    public void buildNotification(NotificationCompat.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), MiniPlayerOnLockScreenService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Bitmap[] bitmap = new Bitmap[1];
        Picasso.get()
                .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap resourse, Picasso.LoadedFrom from) {
                        bitmap[0] = resourse;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setSound(null, null);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_small)
                .setContentTitle(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName().trim())
                .setContentText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger().trim())
                .setDeleteIntent(pendingIntent)
                .setLargeIcon(bitmap[0])
                .addAction(initAction(R.drawable.ic_prev, "Prev", ACTION_PRE))
                .addAction(action)
                .addAction(initAction(R.drawable.ic_next, "Next", ACTION_NEXT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2));
/*
       builder.addAction(action);
       builder.addAction(initAction(R.drawable.ic_repeat,"REPEAT",ACTION_REWIND));*/

        notificationManager.notify(1, builder.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            initMediaSession();
        }
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSession() {
        mediaPlayer = new MediaPlayer();
        //mediaPlayer = FullActivity.mediaPlayerCurrentSong;
        mediaSession = new MediaSession(getApplicationContext(), "example player session");
        //mediaController = new MediaController(getApplicationContext(),mediaSession.getSessionToken());
/*        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                buildNotification(initAction(R.drawable.ic_pause,"Pause",ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                buildNotification(initAction(R.drawable.ic_play_1,"Play",ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                buildNotification(initAction(R.drawable.ic_next,"Next",ACTION_NEXT));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                buildNotification(initAction(R.drawable.ic_prev,"Prev",ACTION_PRE));
            }

            @Override
            public void onRewind() {
                super.onRewind();
                buildNotification(initAction(R.drawable.ic_repeat,"Repeat",ACTION_REWIND));
            }

            @Override
            public void onStop() {
                super.onStop();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(),MiniPlayerOnLockScreenService.class);
                stopService(intent);
            }
        });*/
    }
}
