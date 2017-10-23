package net.shironamhin.shironamhin.activity;

import android.app.DownloadManager;
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
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.adapter.DialogAdapter;
import net.shironamhin.shironamhin.database.DatabaseHelper;
import net.shironamhin.shironamhin.firebase.FirebaseHelper;
import net.shironamhin.shironamhin.model.Blog;
import net.shironamhin.shironamhin.model.Playlist;
import net.shironamhin.shironamhin.model.Song;
import net.shironamhin.shironamhin.model.Songlist;
import net.shironamhin.shironamhin.myPlayService;

import java.util.ArrayList;

public class MiniPlayer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private String VIDEO_ID;
    private String SONG_NAME;
    private RecyclerView mBlogList;
    private RecyclerView mBlogList2;
    private RecyclerView mBlogList3;
    private RecyclerView mBlogList4;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaselist;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceNews;
    private DatabaseReference databaseReferenceLyrics;
    private ProgressBar progressBar;

    ArrayList<Songlist> songlists = new ArrayList<>();
    ImageView fab_share, fab_karaokey, fab_refrsh, fab_playlist;
    boolean isOpen = false;

    public boolean switchOn = false;

    String currentPlayingName;
    String currentPlayingAlbum;
    String currentPlayingSong;
    ImageView upButton, btnPlay, btnPause, btnStop, albumImage;
    SeekBar volume;
    TextView songName, albumName, bandName, currenttime, sharesong, download;

    private AudioManager audioManager;
    private int maxVolume;
    private int curVolume;
    String album_key;
    private int position;
    String currentlyrics;
    private boolean internetOn = false;
    LinearLayout linearLayout;
    private DownloadManager downloadManager;
    private long Music_DownloadId;

    ListView listView = null;
    ArrayList<Playlist> playlists;
    DialogAdapter dialogadapter;
    AlertDialog dialog;
    private DatabaseHelper databaseHelper;
    private Query query;
    private FirebaseHelper firebaseHelper;

    Intent serviceIntent;
    private boolean boolMusicPlaying = false;
    private boolean isOnline;
    boolean mBufferBroadcastIsRegistered;
    private ProgressDialog pdBuff = null;
    private SeekBar seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;
    private myPlayService mBoundService = new myPlayService();

    public static final String BROADCAST_SEEKBARM = "net.shironamhin.shironamhin.activity.sendseekbar";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_player);
        try {
            serviceIntent = new Intent(this, myPlayService.class);
            intent = new Intent(BROADCAST_SEEKBARM);

            initViews();
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        databaseHelper = new DatabaseHelper(MiniPlayer.this);
        Bundle bundle = getIntent().getExtras();
        album_key = bundle.getString("post_key");
        position = bundle.getInt("position");

        mDatabaselist = FirebaseDatabase.getInstance().getReference().child("SongPlay");
        query = mDatabaselist.orderByChild("post_key").equalTo(album_key);
        query.keepSynced(true);

        firebaseHelper = new FirebaseHelper(query);
        songlists = firebaseHelper.retrieve();
        navigation();
        Floating();
        try{
            playAudio();
            boolMusicPlaying = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        listView = new ListView(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        playlists = dbHelper.getAllplaylist();
        dialogadapter = new DialogAdapter(this, playlists);
        listView.setAdapter(dialogadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
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
                    Toast.makeText(MiniPlayer.this, "your song add to playlist", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    mBoundService.playMedia();
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.playMedia();
                btnPause.setImageResource(R.drawable.mini_pause);
                btnPlay.setImageResource(R.drawable.mini_play);
                btnStop.setImageResource(R.drawable.mini_stop);

            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.pauseMedia();
                btnPause.setImageResource(R.drawable.minipause);
                btnPlay.setImageResource(R.drawable.miniplay);
                btnStop.setImageResource(R.drawable.mini_stop);

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.stopMedia();
                switchOn = false;
                boolMusicPlaying= false;
                linearLayout.setVisibility(View.INVISIBLE);
                fab_share.setVisibility(View.INVISIBLE);
                fab_karaokey.setVisibility(View.INVISIBLE);
                fab_refrsh.setVisibility(View.INVISIBLE);
                fab_playlist.setVisibility(View.INVISIBLE);
                btnPause.setImageResource(R.drawable.mini_pause);
                btnPlay.setImageResource(R.drawable.miniplay);
                btnStop.setImageResource(R.drawable.ministop);
            }
        });

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
            public void onProgressChanged(SeekBar arg0, int arg1,
                                          boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        arg1, 0);
            }
        });

        //***************************
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView menuRight = (ImageView) findViewById(R.id.menuRight);
        menuRight.setOnClickListener(new View.OnClickListener() {
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

        Button button = (Button) findViewById(R.id.seeMore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boolMusicPlaying) {
                    switchOn = false;
                    Intent intent = new Intent(MiniPlayer.this, NewsActivity.class);
                    intent.putExtra("enable", switchOn);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (boolMusicPlaying) {
                        switchOn = true;
                        Intent intent = new Intent(MiniPlayer.this, NewsActivity.class);
                        intent.putExtra("post_key", album_key);
                        intent.putExtra("position", position);
                        intent.putExtra("enable", switchOn);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Albums");
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("VideoSong");
        databaseReference.keepSynced(true);
        mBlogList2 = (RecyclerView) findViewById(R.id.blog_list2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        layoutManager2.setStackFromEnd(true);
        mBlogList2.setHasFixedSize(true);
        mBlogList2.setLayoutManager(layoutManager2);

        databaseReferenceNews = FirebaseDatabase.getInstance().getReference().child("News");
        databaseReferenceNews.keepSynced(true);
        mBlogList3 = (RecyclerView) findViewById(R.id.blog_list3);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        layoutManager3.setReverseLayout(true);
        layoutManager3.setStackFromEnd(true);
        mBlogList3.setHasFixedSize(true);
        mBlogList3.setLayoutManager(layoutManager3);
        mBlogList3.setNestedScrollingEnabled(false);

        databaseReferenceLyrics = FirebaseDatabase.getInstance().getReference().child("Lyrics");
        databaseReferenceLyrics.keepSynced(true);
        mBlogList4 = (RecyclerView) findViewById(R.id.blog_list4);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager4.setReverseLayout(true);
        layoutManager4.setStackFromEnd(true);
        mBlogList4.setHasFixedSize(true);
        mBlogList4.setLayoutManager(layoutManager4);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(listView);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("create playlist", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switchOn = true;
                Intent intent = new Intent(MiniPlayer.this, OnlineActivity.class);
                intent.putExtra("internetOn", internetOn);
                intent.putExtra("enable", switchOn);
                intent.putExtra("post_key", album_key);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        dialog = builder.create();
    }


    //*************************
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

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");

        currentPlayingName = serviceIntent.getStringExtra("currentPlayingSongName");
        currentPlayingAlbum = serviceIntent.getStringExtra("currentPlayingAlbum");
        currentlyrics = serviceIntent.getStringExtra("currentPlayinglyris");
        currentPlayingSong = serviceIntent.getStringExtra("sntAudioLink");
        position = serviceIntent.getExtras().getInt("position");

        songName.setText(currentPlayingName);
        albumName.setText(currentPlayingAlbum);

        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);

        long second1 = (seekProgress / 1000) % 60;
        long minute1 = (seekProgress / (1000 * 60)) % 60;
        String currentDuration = String.format("%02d:%02d", minute1, second1);

        currenttime.setText(currentDuration);

        seekBar.setProgress(seekProgress);
        if (songEnded == 1) {
        }
    }

    private void initViews() {
        btnPlay = (ImageView) findViewById(R.id.miniPlay);
        btnPause = (ImageView) findViewById(R.id.miniPause);
        btnStop = (ImageView) findViewById(R.id.miniStop);
        albumImage = (ImageView) findViewById(R.id.albumImage);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        volume = (SeekBar) findViewById(R.id.volume);
        songName = (TextView) findViewById(R.id.mtitle);
        albumName = (TextView) findViewById(R.id.malbumName);
        bandName = (TextView) findViewById(R.id.mbandName);
        currenttime = (TextView) findViewById(R.id.currentDuration);
        sharesong = (TextView) findViewById(R.id.shareSong);
        download = (TextView) findViewById(R.id.download);
        linearLayout = (LinearLayout) findViewById(R.id.miniplay);
        download.setOnClickListener(this);
        final IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    private void setListeners() {
        seekBar.setOnSeekBarChangeListener(this);
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
            linearLayout.setVisibility(View.INVISIBLE);
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                    boolMusicPlaying=false;
                    mBoundService.stopMedia();
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            });
            alertDialog.setIcon(R.drawable.mcircle);
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
                break;

        }
    }

    private void BufferDialogue() {

        pdBuff = ProgressDialog.show(MiniPlayer.this, "Buffering...",
                "Acquiring song...", true);
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };

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
        progressBar.setVisibility(View.GONE);
        super.onResume();
    }

    public void miniPlayer(View view) {
        Intent intent = new Intent(MiniPlayer.this, PlayerActivity.class);
        intent.putExtra("internetOn", internetOn);
        intent.putExtra("post_key", album_key);
        startActivity(intent);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                try{
                    Uri music_uri = Uri.parse(currentPlayingSong);
                    Music_DownloadId = DownloadData(music_uri, v);
                }catch (Exception e){
                    e.printStackTrace();
                    mBoundService.playMedia();
                }
                break;
        }
    }

    private long DownloadData(Uri uri, View v) {

        long downloadReference;

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(currentPlayingName);

        //Setting description of request
        request.setDescription(currentPlayingAlbum + " " + "Shironamhin");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        if (v.getId() == R.id.download)
            request.setDestinationInExternalFilesDir(MiniPlayer.this, Environment.DIRECTORY_DOWNLOADS, currentPlayingName + ".mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        request.allowScanningByMediaScanner();// if you want to be available from media players
        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (referenceId == Music_DownloadId) {

                Toast toast = Toast.makeText(MiniPlayer.this,
                        "Music Download Complete", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, MiniPlayer.AlbumviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog,
                MiniPlayer.AlbumviewHolder>(
                Blog.class, R.layout.albums, MiniPlayer.AlbumviewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(MiniPlayer.AlbumviewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setAlbumNames(model.getAlbumNames());
                viewHolder.setAlbumSongs(model.getAlbumSongs());
                viewHolder.setAlbumImage(getApplicationContext(), model.getAlbumImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                            try {
                                internetOn = true;
                                switchOn = false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("post_key", post_key);
                                intent.putExtra("internetOn", internetOn);
                                intent.putExtra("url", model.getAudiofile().toString());
                                intent.putExtra("image", model.getAlbumImage());
                                intent.putExtra("firstsong", model.getFirstsong());
                                intent.putExtra("album", model.getAlbumNames());
                                intent.putExtra("currentlyrics", model.getLyrics());
                                intent.putExtra("switchbtn", switchOn);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                            try {
                                internetOn = false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("internetOn", internetOn);
                                intent.putExtra("post_key", post_key);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);


        FirebaseRecyclerAdapter<Blog, MiniPlayer.VideolistHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<Blog,
                MiniPlayer.VideolistHolder>(Blog.class, R.layout.videos, MiniPlayer.VideolistHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(MiniPlayer.VideolistHolder viewHolder, final Blog model, int position) {

                viewHolder.setVideoID(model.getVideoID());
                viewHolder.setVideoname(model.getVideoname());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setViews(model.getViews());
                viewHolder.mView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBoundService.stopMedia();
                        VIDEO_ID = model.videoID;
                        SONG_NAME = model.getVideoname();
                        Intent intent = new Intent(getApplicationContext(), Youtube.class);
                        intent.putExtra("VIDEO_ID", VIDEO_ID);
                        intent.putExtra("SONG_NAME", SONG_NAME);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
        };
        mBlogList2.setAdapter(firebaseRecyclerAdapter1);

        FirebaseRecyclerAdapter<Blog, MiniPlayer.NewsviewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Blog,
                MiniPlayer.NewsviewHolder>(Blog.class, R.layout.news, MiniPlayer.NewsviewHolder.class, databaseReferenceNews) {
            @Override
            protected void populateViewHolder(MiniPlayer.NewsviewHolder viewHolder, final Blog model, int position) {

                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setPost_title(model.getPost_title());
                viewHolder.setPost_detail(model.getPost_detail());
            }
        };
        mBlogList3.setAdapter(firebaseRecyclerAdapter2);

        FirebaseRecyclerAdapter<Blog, MiniPlayer.LyricsviewHolder> firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<Blog,
                MiniPlayer.LyricsviewHolder>(
                Blog.class, R.layout.lyrics, MiniPlayer.LyricsviewHolder.class, databaseReferenceLyrics) {
            @Override
            protected void populateViewHolder(MiniPlayer.LyricsviewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setLyricsName(model.getLyricsName());
                viewHolder.setLyricsSong(model.getLyricsSong());
                viewHolder.setLyricsImage(getApplicationContext(), model.getLyricsImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                            try {
                                internetOn = true;
                                switchOn = true;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("post_key", post_key);
                                intent.putExtra("internetOn", internetOn);
                                intent.putExtra("url", model.getAudiofile().toString());
                                intent.putExtra("image", model.getLyricsImage());
                                intent.putExtra("firstsong", model.getFirstsong());
                                intent.putExtra("album", model.getLyricsName());
                                intent.putExtra("currentlyrics", model.getLyrics());
                                intent.putExtra("switchbtn", switchOn);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                            try {
                                internetOn = false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("internetOn", internetOn);
                                intent.putExtra("post_key", post_key);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        };
        mBlogList4.setAdapter(firebaseRecyclerAdapter3);

    }

    public void addPlaylist(View view) {
        dialog.setMessage("Add to a playlist");
        dialog.show();
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

    public static class AlbumviewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AlbumviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAlbumNames(String albumNames) {
            TextView album_name = (TextView) mView.findViewById(R.id.name);
            album_name.setText(albumNames);
        }

        public void setAlbumSongs(String albumSongs) {
            TextView album_songs = (TextView) mView.findViewById(R.id.songs);
            album_songs.setText(albumSongs);
        }

        public void setAlbumImage(final Context context, final String albumImage) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.album);

            Picasso.with(context).load(albumImage).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(albumImage).into(post_image);
                }
            });
        }
    }

    public static class VideolistHolder extends RecyclerView.ViewHolder {
        View mView2;

        public VideolistHolder(View itemView2) {
            super(itemView2);
            mView2 = itemView2;
        }

        public void setVideoID(String videoID) {

        }

        public void setVideoname(String videoname) {
            TextView video_name = (TextView) mView2.findViewById(R.id.vname);
            video_name.setText(videoname);
        }

        public void setViews(String views) {
            TextView viewer = (TextView) mView2.findViewById(R.id.viewer);
            viewer.setText(views);
        }

        public void setImage(final Context context, final String image) {
            final ImageView postimage = (ImageView) mView2.findViewById(R.id.video);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(postimage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(postimage);
                }
            });
        }
    }

    public static class NewsviewHolder extends RecyclerView.ViewHolder {
        View mView3;

        public NewsviewHolder(View itemView3) {
            super(itemView3);
            mView3 = itemView3;
        }

        public void setDate(String date) {
            TextView postDate = (TextView) mView3.findViewById(R.id.date);
            postDate.setText(date);
        }

        public void setTime(String time) {
            TextView postTime = (TextView) mView3.findViewById(R.id.time);
            postTime.setText(time);
        }

        public void setPost_title(String post_title) {
            TextView postTitle = (TextView) mView3.findViewById(R.id.post_title);
            postTitle.setText(post_title);
        }

        public void setPost_detail(String post_detail) {
            TextView postDetail = (TextView) mView3.findViewById(R.id.post_detail);
            postDetail.setText(post_detail);
        }
    }

    public static class LyricsviewHolder extends RecyclerView.ViewHolder {
        View mView;

        public LyricsviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setLyricsName(String lyricsName) {
            TextView lyrics_name = (TextView) mView.findViewById(R.id.name);
            lyrics_name.setText(lyricsName);
        }

        public void setLyricsSong(String lyricsSong) {
            TextView lyrics_songs = (TextView) mView.findViewById(R.id.songs);
            lyrics_songs.setText(lyricsSong);
        }

        public void setLyricsImage(final Context context, final String lyricsImage) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.album);

            Picasso.with(context).load(lyricsImage).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(lyricsImage).into(post_image);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            finish();
        }
    }

    public void Floating() {
        upButton = (ImageView) findViewById(R.id.upButtom);
        fab_share = (ImageView) findViewById(R.id.six);
        fab_karaokey = (ImageView) findViewById(R.id.second);
        fab_refrsh = (ImageView) findViewById(R.id.fourth);
        fab_playlist = (ImageView) findViewById(R.id.fifth);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.playMedia();
                if (isOpen) {
                    fab_share.setVisibility(View.VISIBLE);
                    fab_karaokey.setVisibility(View.VISIBLE);
                    fab_refrsh.setVisibility(View.VISIBLE);
                    fab_playlist.setVisibility(View.VISIBLE);
                    isOpen = false;

                } else {

                    fab_share.setVisibility(View.INVISIBLE);
                    fab_karaokey.setVisibility(View.INVISIBLE);
                    fab_refrsh.setVisibility(View.INVISIBLE);
                    fab_playlist.setVisibility(View.INVISIBLE);
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
                        btnPause.setImageResource(R.drawable.minipause);
                        btnPlay.setImageResource(R.drawable.miniplay);
                        btnStop.setImageResource(R.drawable.mini_stop);
                        Intent intent = new Intent(MiniPlayer.this, Karaokey.class);
                        intent.putExtra("post_key", album_key);
//                        intent.putExtra("currentlyrics", currentlyrics);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(MiniPlayer.this, "karaoke not supported", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void navigation() {
        LinearLayout online = (LinearLayout) findViewById(R.id.online);
        LinearLayout shareAPP = (LinearLayout) findViewById(R.id.share);
        LinearLayout contact = (LinearLayout) findViewById(R.id.contact);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boolMusicPlaying) {
                    switchOn = false;
                    Intent intent = new Intent(MiniPlayer.this, OnlineActivity.class);
                    intent.putExtra("internetOn", internetOn);
                    intent.putExtra("enable", switchOn);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (boolMusicPlaying) {
                        switchOn = true;
                        Intent intent = new Intent(MiniPlayer.this, OnlineActivity.class);
                        intent.putExtra("internetOn", internetOn);
                        intent.putExtra("enable", switchOn);
                        intent.putExtra("post_key", album_key);
                        intent.putExtra("position", position);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        shareAPP.setOnClickListener(new View.OnClickListener() {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
