package net.shironamhin.shironamhin.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.VerticalMarqueeTextView;
import net.shironamhin.shironamhin.adapter.CustomAdapter;
import net.shironamhin.shironamhin.adapter.DialogAdapter;
import net.shironamhin.shironamhin.database.DatabaseHelper;
import net.shironamhin.shironamhin.firebase.FirebaseHelper;
import net.shironamhin.shironamhin.model.Playlist;
import net.shironamhin.shironamhin.model.Song;
import net.shironamhin.shironamhin.model.Songlist;
import net.shironamhin.shironamhin.myPlayService;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String BROADCAST_SEEKBAR = "net.shironamhin.shironamhin.activity.sendseekbar";
    private static int songEnded = 0;
    public boolean switchOn = false;
    Intent serviceIntent;
    ArrayList<Songlist> songlists = new ArrayList<>();
    String currentPlayingSong;
    String currentPlayingName;
    String currentPlayingAlbum;
    ImageView fab_plus, fab_share, fab_volume, fab_karaokey, fab_refrsh, fab_playlist;
    Animation FadeOpen, FadeClose;
    boolean isOpen = false;
    String post_key;
    String album;
    String currentlyrics;
    SeekBar volume;
    boolean value = false;
    boolean internetOn = false;
    CustomAdapter adapter;
    ListView listView = null;
    ArrayList<Playlist> playlists;
    DialogAdapter dialogadapter;
    android.support.v7.app.AlertDialog dialog;
    boolean mBufferBroadcastIsRegistered;
    boolean mBroadcastIsRegistered;
    Intent intent;
    private ImageButton playprevious, plyabtn, playnext;
    private int position = 0;
    private DatabaseReference mDatabase;
    private Query query;
    private ProgressBar progressBar;
    private ListView mBlogList;
    private Switch aSwitch;
    private ImageView imageAlbum;
    private String image;
    private TextView currentDurationOfsong, durationOfsong, currentsong, currentalbum, dvd, text_view;
    private int maxVolume;
    private int curVolume;
    private AudioManager audioManager;
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;
    private TextView mtext;
    private boolean boolMusicPlaying = false;
    private boolean isOnline;
    private ProgressDialog pdBuff = null;
    private SeekBar seekBar;
    private int seekMax;
    private myPlayService mBoundService = new myPlayService();
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myPlayService.LocalBinder binder = (myPlayService.LocalBinder) service;
            mBoundService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        navigation();
        try {
            serviceIntent = new Intent(this, myPlayService.class);
            intent = new Intent(BROADCAST_SEEKBAR);

            initViews();
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        databaseHelper = new DatabaseHelper(PlayerActivity.this);
        Bundle bundle = getIntent().getExtras();
        internetOn = bundle.getBoolean("internetOn");
        post_key = bundle.getString("post_key");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("SongPlay");
        query = mDatabase.orderByChild("post_key").equalTo(post_key);
        query.keepSynced(true);

        firebaseHelper = new FirebaseHelper(query);
        songlists = firebaseHelper.retrieve();
        adapter = new CustomAdapter(this, songlists);
        mBlogList.setAdapter(adapter);
        if (internetOn == true) {
            currentPlayingSong = bundle.getString("url");
            image = bundle.getString("image");
            currentPlayingName = bundle.getString("firstsong");
            album = bundle.getString("album");
            currentlyrics = bundle.getString("currentlyrics");
            switchOn = bundle.getBoolean("switchbtn");
            currentsong.setText(currentPlayingName);
            currentalbum.setText(album);

            plyabtn.setImageResource(R.drawable.pausebtn);
            playAudio();
            boolMusicPlaying = true;
            seekBar.setOnSeekBarChangeListener(this);
        } else if (internetOn == false) {
            plyabtn.setImageResource(R.drawable.pausebtn);
            playAudioFromMini();
            boolMusicPlaying = true;
        }

        LinearLayout linearLayouthome = (LinearLayout) findViewById(R.id.homePage);
        linearLayouthome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, MiniPlayer.class);
                intent.putExtra("post_key", post_key);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView menuRight = (ImageView) findViewById(R.id.menuRight);
        menuRight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (drawer.isDrawerOpen(GravityCompat.END)) {
                            drawer.closeDrawer(GravityCompat.END);
                        } else {
                            drawer.openDrawer(GravityCompat.END);
                        }
                    }
                });
        TextView menu = (TextView) findViewById(R.id.textMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        volume = (SeekBar) findViewById(R.id.volume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume.setMax(maxVolume);
        volume.setProgress(curVolume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        arg1, 0);
            }
        });
        if (internetOn) {
            Picasso.with(PlayerActivity.this).load(image.toString()).networkPolicy(NetworkPolicy.OFFLINE).into(imageAlbum, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(PlayerActivity.this).load(image.toString()).into(imageAlbum);
                }
            });
        }
        if (isOnline == true) {
            listView = new ListView(this);
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            playlists = dbHelper.getAllplaylist();
            dialogadapter = new DialogAdapter(this, playlists);
            listView.setAdapter(dialogadapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        if (currentPlayingName != null && currentPlayingAlbum != null) {

                            currentPlayingSong.toString();
                            currentPlayingName.toString();
                            currentPlayingAlbum.toString();
                            currentlyrics.toString();

                            long second = (seekMax / 1000) % 60;
                            long minute = (seekMax / (1000 * 60)) % 60;
                            String totalDuration = String.format("%02d:%02d", minute, second);

                            Song song = new Song();
                            song.setListname(playlists.get(position).getName());
                            song.setSongalbumname(currentPlayingAlbum);
                            song.setDuration(totalDuration);
                            song.setAudiofile(currentPlayingSong);
                            song.setLyrics(currentlyrics);
                            song.setSongtitle(currentPlayingName);

                            databaseHelper.addSonglist(song);

                            int playListId = databaseHelper.getPlaylistIdByName(playlists.get(position).getName());
                            int songId = databaseHelper.getSongIdByName(currentPlayingName);
                            databaseHelper.insertIntoPlayListSong(playListId, songId);
                            dialog.dismiss();
                            Toast.makeText(PlayerActivity.this, "your song add to playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            currentPlayingSong.toString();
                            currentPlayingName = songlists.get(position).getSongtitle().toString();
                            currentPlayingAlbum = songlists.get(position).getSongalbumname().toString();
                            currentlyrics = songlists.get(position).getLyrics().toString();

                            long second = (seekMax / 1000) % 60;
                            long minute = (seekMax / (1000 * 60)) % 60;
                            String totalDuration = String.format("%02d:%02d", minute, second);

                            Song song = new Song();
                            song.setListname(playlists.get(position).getName());
                            song.setSongalbumname(currentPlayingAlbum);
                            song.setDuration(totalDuration);
                            song.setAudiofile(currentPlayingSong);
                            song.setLyrics(currentlyrics);
                            song.setSongtitle(currentPlayingName);

                            databaseHelper.addSonglist(song);

                            int playListId = databaseHelper.getPlaylistIdByName(playlists.get(position).getName());
                            int songId = databaseHelper.getSongIdByName(currentPlayingName);
                            databaseHelper.insertIntoPlayListSong(playListId, songId);
                            dialog.dismiss();
                            Toast.makeText(PlayerActivity.this, "your song add to playlist", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBoundService.playMedia();
                    }

                }
            });

            mBlogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    plyabtn.setImageResource(R.drawable.pausebtn);
                    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.test);
                    view.startAnimation(hyperspaceJumpAnimation);
                    if (position < songlists.size()) {
                        position = (position) % songlists.size();
                        currentPlayingSong = songlists.get(position).getAudiofile();
                        currentPlayingName = songlists.get(position).getSongtitle();
                        currentPlayingAlbum = songlists.get(position).getSongalbumname();
                        songlists.get(position).getPlayimage();
                        currentlyrics = songlists.get(position).getLyrics();
                        aSwitch.setChecked(switchOn);
                        if (aSwitch.isChecked()) {
                            mtext.setText(currentlyrics.toString());
                            mtext.setVisibility(View.VISIBLE);
                            text_view.setText("Lyric view");
                        }
                        currentsong.setText(songlists.get(position).getSongtitle().toString());
                        currentalbum.setText(songlists.get(position).getSongalbumname().toString());
                        playAudio();
                        boolMusicPlaying = true;
                        seekBar.setOnSeekBarChangeListener(PlayerActivity.this);
                        // mBoundService.playFromlist();
                    }
                }
            });
            playprevious.setOnClickListener(this);
            playnext.setOnClickListener(this);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Floating();
            switchOperation();

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setView(listView);
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("create playlist", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switchOn = true;
                    Intent intent = new Intent(PlayerActivity.this, OnlineActivity.class);
                    intent.putExtra("internetOn", internetOn);
                    intent.putExtra("enable", switchOn);
                    intent.putExtra("post_key", post_key);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });
            dialog = builder.create();
        }
    }

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");

        currentPlayingName = serviceIntent.getStringExtra("currentPlayingSongName");
        currentPlayingAlbum = serviceIntent.getStringExtra("currentPlayingAlbum");
        currentlyrics = serviceIntent.getStringExtra("currentPlayinglyris");
        currentPlayingSong = serviceIntent.getStringExtra("sntAudioLink");
        position = serviceIntent.getExtras().getInt("position");

        currentsong.setText(currentPlayingName);
        currentalbum.setText(currentPlayingAlbum);
        mtext.setText(currentlyrics);

        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);

        long second = (seekMax / 1000) % 60;
        long minute = (seekMax / (1000 * 60)) % 60;
        String totalDuration = String.format("%02d:%02d", minute, second);

        long second1 = (seekProgress / 1000) % 60;
        long minute1 = (seekProgress / (1000 * 60)) % 60;
        String currentDuration = String.format("%02d:%02d", minute1, second1);

        durationOfsong.setText(totalDuration);
        currentDurationOfsong.setText(currentDuration);

        seekBar.setProgress(seekProgress);
        if (songEnded == 1) {
            plyabtn.setImageResource(R.drawable.playbtn);
        }
    }

    private void initViews() {
        playprevious = (ImageButton) findViewById(R.id.previous);
        plyabtn = (ImageButton) findViewById(R.id.play);
        playnext = (ImageButton) findViewById(R.id.next);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        imageAlbum = (ImageView) findViewById(R.id.imageAlbum);
        currentDurationOfsong = (TextView) findViewById(R.id.currentdurationOfsong);
        durationOfsong = (TextView) findViewById(R.id.durationOfsong);
        currentsong = (TextView) findViewById(R.id.currentsong);
        currentalbum = (TextView) findViewById(R.id.currentalbum);
        mBlogList = (ListView) findViewById(R.id.songlist);
        text_view = (TextView) findViewById(R.id.text_view);
    }

    private void setListeners() {
        plyabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlayStopClick();
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void buttonPlayStopClick() {
        if (!boolMusicPlaying) {
            plyabtn.setImageResource(R.drawable.pausebtn);
            mBoundService.playMedia();
            boolMusicPlaying = true;
        } else {
            if (boolMusicPlaying) {
                plyabtn.setImageResource(R.drawable.playbtn);
                mBoundService.pauseMedia();
                boolMusicPlaying = false;
            }
        }
    }

    private void stopMyPlayService() {
        if (mBroadcastIsRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        getApplicationContext(),
                        e.getClass().getName() + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
        try {
            stopService(serviceIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        boolMusicPlaying = false;
    }

    private void playAudio() {
        checkConnectivity();
        if (isOnline) {
            stopMyPlayService();
            serviceIntent.putExtra("sentAudioLink", currentPlayingSong);
            serviceIntent.putExtra("currentPlayingName", currentPlayingName);
            serviceIntent.putExtra("currentlyrics", currentlyrics);
            serviceIntent.putExtra("currentPlayingAlbum", currentPlayingAlbum);
            serviceIntent.putExtra("position", position);
            serviceIntent.putExtra("post_key", post_key);
            try {
                startService(serviceIntent);
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        getApplicationContext(),
                        e.getClass().getName() + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
            registerReceiver(broadcastReceiver, new IntentFilter(
                    myPlayService.BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                    boolMusicPlaying = false;
                    mBoundService.stopMedia();
                }
            });
            alertDialog.setIcon(R.drawable.mcircle);
            plyabtn.setImageResource(R.drawable.playbtn);
            alertDialog.show();
        }

    }

    private void playAudioFromMini() {
        checkConnectivity();
        if (isOnline) {
            try {
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        getApplicationContext(),
                        e.getClass().getName() + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
            registerReceiver(broadcastReceiver, new IntentFilter(
                    myPlayService.BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.setIcon(R.drawable.mcircle);
            plyabtn.setImageResource(R.drawable.playbtn);
            alertDialog.show();
        }

    }

    private void showPD(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
        switch (bufferIntValue) {
            case 0:
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;

            case 1:
                BufferDialogue();
                break;
            case 2:
                plyabtn.setImageResource(R.drawable.playbtn);
                break;

        }
    }

    private void BufferDialogue() {

        pdBuff = ProgressDialog.show(PlayerActivity.this, "Buffering...",
                "Acquiring song...", true);
    }

    @Override
    protected void onPause() {
        // Unregister broadcast receiver
        if (mBufferBroadcastIsRegistered) {
            try {
                unregisterReceiver(broadcastBufferReceiver);
                mBufferBroadcastIsRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        getApplicationContext(),
                        e.getClass().getName() + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register broadcast receiver
        if (!mBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    myPlayService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
        if (internetOn) {
            progressBar.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())
            isOnline = true;
        else
            isOnline = false;
    }


    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        if (fromUser) {
            int seekPos = sb.getProgress();
            intent.putExtra("seekpos", seekPos);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void addPlaylist(View view) {
        dialog.setMessage("Add to a playlist");
        dialog.show();
    }

    public void Floating() {
        if (isOnline == true) {
            fab_plus = (ImageView) findViewById(R.id.first);
            fab_share = (ImageView) findViewById(R.id.six);
            fab_volume = (ImageView) findViewById(R.id.third);
            fab_karaokey = (ImageView) findViewById(R.id.second);
            fab_refrsh = (ImageView) findViewById(R.id.fourth);
            fab_playlist = (ImageView) findViewById(R.id.fifth);


            FadeOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floatingbutton_open);
            FadeClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floatingbutton_close);


            fab_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    volume.setVisibility(View.INVISIBLE);
                    if (isOpen) {

                        fab_share.startAnimation(FadeClose);
                        fab_volume.startAnimation(FadeClose);
                        fab_karaokey.startAnimation(FadeClose);
                        fab_refrsh.startAnimation(FadeClose);
                        fab_playlist.startAnimation(FadeClose);

                        fab_share.setClickable(false);
                        fab_volume.setClickable(false);
                        fab_karaokey.setClickable(false);
                        fab_refrsh.setClickable(false);
                        fab_playlist.setClickable(false);
                        isOpen = false;

                    } else {

                        fab_share.startAnimation(FadeOpen);
                        fab_volume.startAnimation(FadeOpen);
                        fab_karaokey.startAnimation(FadeOpen);
                        fab_refrsh.startAnimation(FadeOpen);
                        fab_playlist.startAnimation(FadeOpen);

                        fab_share.setClickable(true);
                        fab_volume.setClickable(true);
                        fab_karaokey.setClickable(true);
                        fab_refrsh.setClickable(true);
                        fab_playlist.setClickable(true);
                        isOpen = true;

                    }

                }
            });

            fab_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubText = "Let's enjoy music of Shironamhin";
                    String shareBodyText = "Let's enjoy music of Shironamhin." + currentPlayingName + currentPlayingAlbum + currentPlayingSong;
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(shareIntent, "Share With"));
                }
            });

            fab_volume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!value) {
                        volume.setVisibility(View.VISIBLE);
                        value = true;
                    } else {
                        if (value) {
                            volume.setVisibility(View.INVISIBLE);
                            value = false;
                        }
                    }

                }
            });
            fab_refrsh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBoundService.playFromlist();

                }
            });

            fab_karaokey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions
                        try {
                            mBoundService.pauseMedia();
                            plyabtn.setImageResource(R.drawable.playbtn);
                            Intent intent = new Intent(PlayerActivity.this, Karaokey.class);
                            intent.putExtra("post_key", post_key);
//                            intent.putExtra("currentlyrics", currentlyrics);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // do something for phones running an SDK before lollipop
                        Toast.makeText(PlayerActivity.this, "karaoke not supported", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void switchOperation() {
        if (isOnline == true) {
            aSwitch = (Switch) findViewById(R.id.switch_xml);
            mtext = (TextView) findViewById(R.id.mediatext);
            mtext.setMovementMethod(new ScrollingMovementMethod());
            dvd = (TextView) findViewById(R.id.dvd);
            aSwitch.setChecked(switchOn);
            if (aSwitch.isChecked()) {
                mtext.setText(currentlyrics.toString());
                mtext.setVisibility(View.VISIBLE);
                text_view.setText("Lyric view");
                volume.setVisibility(View.INVISIBLE);
                playprevious.setVisibility(View.INVISIBLE);
                plyabtn.setVisibility(View.INVISIBLE);
                playnext.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                currentDurationOfsong.setVisibility(View.INVISIBLE);
                durationOfsong.setVisibility(View.INVISIBLE);
                currentsong.setVisibility(View.INVISIBLE);
                currentalbum.setVisibility(View.INVISIBLE);
                dvd.setVisibility(View.INVISIBLE);
            }
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean lyrics) {
                    try{
                        if (lyrics) {
                            mtext.setText(currentlyrics.toString());
                            mtext.setVisibility(View.VISIBLE);
                            text_view.setText("Lyric view");
                            volume.setVisibility(View.INVISIBLE);
                            playprevious.setVisibility(View.INVISIBLE);
                            plyabtn.setVisibility(View.INVISIBLE);
                            playnext.setVisibility(View.INVISIBLE);
                            seekBar.setVisibility(View.INVISIBLE);
                            currentDurationOfsong.setVisibility(View.INVISIBLE);
                            durationOfsong.setVisibility(View.INVISIBLE);
                            currentsong.setVisibility(View.INVISIBLE);
                            currentalbum.setVisibility(View.INVISIBLE);
                            dvd.setVisibility(View.INVISIBLE);
                        } else {
                            mtext.setVisibility(View.INVISIBLE);
                            text_view.setText("Song view");
                            //volume.setVisibility(View.INVISIBLE);
                            playprevious.setVisibility(View.VISIBLE);
                            plyabtn.setVisibility(View.VISIBLE);
                            playnext.setVisibility(View.VISIBLE);
                            seekBar.setVisibility(View.VISIBLE);
                            currentDurationOfsong.setVisibility(View.VISIBLE);
                            durationOfsong.setVisibility(View.VISIBLE);
                            currentsong.setVisibility(View.VISIBLE);
                            currentalbum.setVisibility(View.VISIBLE);
                            dvd.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mBoundService.playMedia();
                    }

                }
            });
        }

    }

    public void navigation() {
        LinearLayout online = (LinearLayout) findViewById(R.id.online);
        LinearLayout share = (LinearLayout) findViewById(R.id.share);
        LinearLayout contact = (LinearLayout) findViewById(R.id.contact);

        online.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!boolMusicPlaying) {
                            switchOn = false;
                            Intent intent = new Intent(PlayerActivity.this, OnlineActivity.class);
                            intent.putExtra("internetOn", internetOn);
                            intent.putExtra("enable", switchOn);
                            startActivity(intent);
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.END);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            if (boolMusicPlaying) {
                                switchOn = true;
                                Intent intent = new Intent(PlayerActivity.this, OnlineActivity.class);
                                intent.putExtra("internetOn", internetOn);
                                intent.putExtra("enable", switchOn);
                                intent.putExtra("post_key", post_key);
                                intent.putExtra("position", position);
                                startActivity(intent);
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }

                    }
                });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareSubText = "Let's enjoy music of Shironamhin";
                String shareBodyText = "http://shironamhin.net/";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(shareIntent, "Share With"));

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://shironamhin.net/contact-page-2/")));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            Intent intent = new Intent(PlayerActivity.this, MiniPlayer.class);
            intent.putExtra("post_key", post_key);
            intent.putExtra("position", position);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
        }


    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.next:

                plyabtn.setImageResource(R.drawable.pausebtn);
                mBoundService.playNext();

                break;

            case R.id.previous:

                plyabtn.setImageResource(R.drawable.pausebtn);
                mBoundService.playPrv();

                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
