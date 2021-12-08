package com.vovmusic.uit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
/*        switch (action)
        {
            case MiniPlayerOnLockScreenService.ACTION_PLAY:
                onSongPlay();

                Log.d("BroadcastReceiver","Pause");
                break;
            case MiniPlayerOnLockScreenService.ACTION_PAUSE:
                Log.d("BroadcastReceiver","Pause");
                onSongPlay();
                break;
            case MiniPlayerOnLockScreenService.ACTION_PRE:
                onSongPrev();
                break;

        }*/
        context.sendBroadcast(new Intent("TRACKS_TRACkS").putExtra("actionname", action));
    }
}
