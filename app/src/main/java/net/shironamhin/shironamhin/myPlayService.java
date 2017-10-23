package net.shironamhin.shironamhin;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import net.shironamhin.shironamhin.activity.MiniPlayer;
import net.shironamhin.shironamhin.activity.NewsActivity;
import net.shironamhin.shironamhin.activity.OnlineActivity;
import net.shironamhin.shironamhin.activity.PlayerActivity;
import net.shironamhin.shironamhin.firebase.FirebaseHelper;
import net.shironamhin.shironamhin.model.Songlist;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Jannat Mostafiz on 8/9/2017.
 */

public class myPlayService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String sntAudioLink;

    private static final int NOTIFICATION_ID = 1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    public static final String BROADCAST_BUFFER = "net.shironamhin.shironamhin.broadcastbuffer";
    Intent bufferIntent;
    private int headsetSwitch = 1;

    int mediaPosition;
    int mediaMax;
    Intent seekIntent;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "net.shironamhin.shironamhin.seekprogress";

    private DatabaseReference mDatabase;
    private Query query;
    private String post_key;
    ArrayList<Songlist> songlists = new ArrayList<>();
    private FirebaseHelper firebaseHelper;
    private int position = 0;
    private String currentPlayinglyris;
    private String currentPlayingAlbum;
    private String currentPlayingName;
    // OnCreate
    @Override
    public void onCreate() {

        bufferIntent = new Intent(BROADCAST_BUFFER);
        seekIntent = new Intent(BROADCAST_ACTION);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();

//        registerReceiver(headsetReceiver, new IntentFilter(
//                Intent.ACTION_HEADSET_PLUG));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(broadcastReceiver, new IntentFilter(
                PlayerActivity.BROADCAST_SEEKBAR));
        registerReceiver(broadcastReceiver, new IntentFilter(
                MiniPlayer.BROADCAST_SEEKBARM));
        registerReceiver(broadcastReceiver, new IntentFilter(
                NewsActivity.BROADCAST_SEEKBARMN));
        registerReceiver(broadcastReceiver, new IntentFilter(
                OnlineActivity.BROADCAST_SEEKBARMNO));

        Log.v(TAG, "Starting telephony");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Log.v(TAG, "Starting listener");
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.v(TAG, "Starting CallStateChange");
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }

            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        try{
            sntAudioLink = intent.getExtras().getString("sentAudioLink");
            currentPlayingName = intent.getExtras().getString("currentPlayingName");
            currentPlayinglyris = intent.getExtras().getString("currentlyrics");
            currentPlayingAlbum = intent.getExtras().getString("currentPlayingAlbum");
            position = intent.getExtras().getInt("position");
            post_key = intent.getExtras().getString("post_key");

            mDatabase = FirebaseDatabase.getInstance().getReference().child("SongPlay");
            query = mDatabase.orderByChild("post_key").equalTo(post_key);
            query.keepSynced(true);
            firebaseHelper = new FirebaseHelper(query);
            songlists = firebaseHelper.retrieve();
        }catch (Exception e){
            e.printStackTrace();
        }

        mediaPlayer.reset();
        if (!mediaPlayer.isPlaying()) {
            try {
                try{
                    mediaPlayer.setDataSource(sntAudioLink);
                }catch (IOException e){
                    e.printStackTrace();
                }
                sendBufferingBroadcast();
                mediaPlayer.prepareAsync();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setupHandler();
        return START_STICKY;
    }

    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000); // 2 seconds

        }
    };
    private void LogMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
            mediaMax = mediaPlayer.getDuration();
            currentPlayingAlbum = songlists.get(position).getSongalbumname().toString();

            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            seekIntent.putExtra("position", position);

            seekIntent.putExtra("currentPlayingSongName", String.valueOf(currentPlayingName));
            seekIntent.putExtra("currentPlayinglyris", String.valueOf(currentPlayinglyris));
            seekIntent.putExtra("currentPlayingAlbum", String.valueOf(currentPlayingAlbum));
            seekIntent.putExtra("sntAudioLink", sntAudioLink);
            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

    }

//*************************???
//    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
//        private boolean headsetConnected = false;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.hasExtra("state")) {
//                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
//                    headsetConnected = false;
//                    headsetSwitch = 0;
//                } else if (!headsetConnected
//                        && intent.getIntExtra("state", 0) == 1) {
//                    headsetConnected = true;
//                    headsetSwitch = 1;
//                }
//
//            }
//            switch (headsetSwitch) {
//                case (0):
//                    headsetDisconnected();
//                    break;
//                case (1):
//                    break;
//            }
//        }
//
//    };
//
//    private void headsetDisconnected() {
//        playMedia();
//        stopSelf();
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }
        //unregisterReceiver(headsetReceiver);
        unregisterReceiver(broadcastReceiver);
        handler.removeCallbacks(sendUpdatesToUI);
        resetButtonPlayStopBroadcast();
    }
    public class LocalBinder extends Binder {
        public myPlayService getService() {
            return myPlayService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(position < songlists.size()){
            position = (position + 1) % songlists.size();
            sntAudioLink = songlists.get(position).getAudiofile().toString();
            currentPlayingName = songlists.get(position).getSongtitle().toString();
            currentPlayinglyris = songlists.get(position).getLyrics().toString();
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.setDataSource(sntAudioLink);
                    sendBufferingBroadcast();
                    mediaPlayer.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                }
            }
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this,
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }


    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        sendBufferCompleteBroadcast();
        playMedia();

//        Intent notIntent = new Intent(this, PlayerActivity.class);
//        notIntent.putExtra("internetOn", false);
//        notIntent.putExtra("post_key", post_key);
//        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
//                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification.Builder builder = new Notification.Builder(this);
//
//        builder.setContentIntent(pendInt)
//                .setSmallIcon(R.drawable.mcircle)
//                .setTicker(currentPlayingName)
//                .setOngoing(true)
//                .setContentTitle(currentPlayingName)
//                .setContentText("Shironamhin");
//        Notification not = builder.build();
//        startForeground(NOTIFICATION_ID, not);

    }

    private void sendBufferingBroadcast() {
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }

    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }

    private void resetButtonPlayStopBroadcast() {
        bufferIntent.putExtra("buffering", "2");
        sendBroadcast(bufferIntent);
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isPlaying()){
            playMedia();
            Toast.makeText(this,
                    "SeekComplete", Toast.LENGTH_SHORT).show();
        }
    }
    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    public void playNext(){
        if(position < songlists.size()){
            position = (position + 1) % songlists.size();
            sntAudioLink = songlists.get(position).getAudiofile().toString();
            currentPlayingName = songlists.get(position).getSongtitle().toString();
            currentPlayinglyris = songlists.get(position).getLyrics().toString();
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()) {
                try {
                    try{
                        mediaPlayer.setDataSource(sntAudioLink);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    sendBufferingBroadcast();
                    mediaPlayer.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void playPrv(){
        if(position < songlists.size()){
            position = (position - 1 < 0) ? songlists.size() - 1 : position - 1;
            sntAudioLink = songlists.get(position).getAudiofile().toString();
            currentPlayingName = songlists.get(position).getSongtitle().toString();
            currentPlayinglyris = songlists.get(position).getLyrics().toString();
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()) {
                try {
                    try{
                        mediaPlayer.setDataSource(sntAudioLink);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    sendBufferingBroadcast();
                    mediaPlayer.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void playFromlist(){
        if(position < songlists.size()){
            position = (position) % songlists.size();
            sntAudioLink = songlists.get(position).getAudiofile().toString();
            currentPlayingName = songlists.get(position).getSongtitle().toString();
            currentPlayinglyris = songlists.get(position).getLyrics().toString();
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.setDataSource(sntAudioLink);
                    sendBufferingBroadcast();
                    mediaPlayer.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
