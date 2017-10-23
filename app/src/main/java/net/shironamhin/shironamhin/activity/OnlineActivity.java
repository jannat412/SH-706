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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.adapter.DialogAdapter;
import net.shironamhin.shironamhin.adapter.PlaylistAdapter;
import net.shironamhin.shironamhin.database.DatabaseHelper;
import net.shironamhin.shironamhin.firebase.FirebaseHelper;
import net.shironamhin.shironamhin.model.Playlist;
import net.shironamhin.shironamhin.model.Song;
import net.shironamhin.shironamhin.model.Songlist;
import net.shironamhin.shironamhin.myPlayService;

import java.util.ArrayList;

import static android.R.attr.handle;

public class OnlineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    PlaylistAdapter adapter;
    ArrayList<Playlist> playlists;
    DatabaseHelper mHelper;
    public int deleteItem;
    private ListView listView;

    public boolean switchOn = false;
    private boolean internetOn = false;
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
    private Query query;
    private DatabaseReference songData;
    private int position;
    ArrayList<Songlist> songlists;
    String currentlyrics;
    private boolean internetOff = false;
    private LinearLayout linearLayout;
    private DownloadManager downloadManager;
    private long Music_DownloadId;
    ProgressDialog progressDialog;

    ImageView fab_share, fab_karaokey, fab_refrsh;
    boolean isOpen = false;

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

    public static final String BROADCAST_SEEKBARMNO = "net.shironamhin.shironamhin.activity.sendseekbar";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        try {
            serviceIntent = new Intent(this, myPlayService.class);
            intent = new Intent(BROADCAST_SEEKBARMNO);

            initViews();
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        Bundle bundle = getIntent().getExtras();
        internetOff = bundle.getBoolean("enable");

        if (internetOff == true) {
            playAudio();
            boolMusicPlaying = true;
            album_key = bundle.getString("post_key");
            position = bundle.getInt("position");

            songName.setText(currentPlayingName);
            albumName.setText(currentPlayingAlbum);
            bandName.setText("SHIRONAMHIN");
            linearLayout.setVisibility(View.VISIBLE);

            songData = FirebaseDatabase.getInstance().getReference().child("SongPlay");
            query = songData.orderByChild("post_key").equalTo(album_key);
            query.keepSynced(true);

            FirebaseHelper db = new FirebaseHelper(query);
            songlists = db.retrieve();

        } else if (internetOff == false) {
            linearLayout.setVisibility(View.INVISIBLE);
        }


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
                boolMusicPlaying = false;
                linearLayout.setVisibility(View.INVISIBLE);
                fab_share.setVisibility(View.INVISIBLE);
                fab_karaokey.setVisibility(View.INVISIBLE);
                fab_refrsh.setVisibility(View.INVISIBLE);
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

        //*****************
        navigation();
        Floating();
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.homePage);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boolMusicPlaying) {
                    Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                } else {
                    if (boolMusicPlaying) {
                        Intent intent = new Intent(OnlineActivity.this, MiniPlayer.class);
                        intent.putExtra("post_key", album_key);
                        intent.putExtra("position", position);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                    }
                }
            }
        });

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
        listView = (ListView) findViewById(R.id.list);
        mHelper = new DatabaseHelper(this);
        loadlist();
        listView.setOnItemLongClickListener(myClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog = new ProgressDialog(OnlineActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setTitle("Please wait..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
                listClick();
                Intent intent = new Intent(OnlineActivity.this, OnlinePlayListSong.class);
                intent.putExtra("play_list_id", playlists.get(position).getId());
                intent.putExtra("switchbtn", switchOn);
                startActivity(intent);
            }
        });
    }

    public void listClick() {
        mBoundService.pauseMedia();
        linearLayout.setVisibility(View.INVISIBLE);
    }

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
        if (internetOff == true) {
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
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
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
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                    linearLayout.setVisibility(View.INVISIBLE);
                    boolMusicPlaying = false;
                    mBoundService.stopMedia();
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

        pdBuff = ProgressDialog.show(OnlineActivity.this, "Buffering...",
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
        super.onResume();
    }

    public void miniPlayer(View view) {
        Intent intent = new Intent(OnlineActivity.this, PlayerActivity.class);
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
                try {
                    Uri music_uri = Uri.parse(currentPlayingSong);
                    Music_DownloadId = DownloadData(music_uri, v);
                } catch (Exception e) {
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
            request.setDestinationInExternalFilesDir(OnlineActivity.this, Environment.DIRECTORY_DOWNLOADS, currentPlayingName + ".mp3");
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

                Toast toast = Toast.makeText(OnlineActivity.this,
                        "Music Download Complete", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };

    public AdapterView.OnItemLongClickListener myClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            deleteItem = arg2;
            android.app.AlertDialog alert = new android.app.AlertDialog.Builder(arg1.getContext())
                    .setTitle("Delete " + playlists.get(deleteItem).getName())
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Playlist playlist = playlists.get(deleteItem);
                                    playlists.remove(deleteItem);
                                    DatabaseHelper db = new DatabaseHelper(OnlineActivity.this);
                                    db.deleteplaylist(playlist.id);
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.cancel();
                                }
                            }).show();

            return true;
        }
    };

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
            finish();
//            if (!boolMusicPlaying) {
//                Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                if (boolMusicPlaying) {
//                    Intent intent = new Intent(OnlineActivity.this, MiniPlayer.class);
//                    intent.putExtra("post_key", album_key);
//                    intent.putExtra("position", position);
//                    startActivity(intent);
//                    finish();
//                }
//            }
        }

    }

    public void navigation() {
        LinearLayout online = (LinearLayout) findViewById(R.id.online);
        LinearLayout share = (LinearLayout) findViewById(R.id.share);
        LinearLayout contact = (LinearLayout) findViewById(R.id.contact);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
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

    public void Floating() {

        upButton = (ImageView) findViewById(R.id.upButtom);
        fab_share = (ImageView) findViewById(R.id.six);
        fab_karaokey = (ImageView) findViewById(R.id.second);
        fab_refrsh = (ImageView) findViewById(R.id.fourth);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.playMedia();
                if (isOpen) {
                    fab_share.setVisibility(View.VISIBLE);
                    fab_karaokey.setVisibility(View.VISIBLE);
                    fab_refrsh.setVisibility(View.VISIBLE);
                    isOpen = false;

                } else {

                    fab_share.setVisibility(View.INVISIBLE);
                    fab_karaokey.setVisibility(View.INVISIBLE);
                    fab_refrsh.setVisibility(View.INVISIBLE);
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
                        Intent intent = new Intent(OnlineActivity.this, Karaokey.class);
                        intent.putExtra("post_key", album_key);
//                        intent.putExtra("currentlyrics", currentlyrics);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(OnlineActivity.this, "karaoke not supported", Toast.LENGTH_SHORT).show();
                }
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

    public void addplaylist(View view) {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(OnlineActivity.this);

        LayoutInflater inflater = LayoutInflater.from(OnlineActivity.this);

        View info = inflater.inflate(R.layout.customdialog, null);

        mydialog.setView(info);

        final AlertDialog builder = mydialog.create();

        builder.setCancelable(true);

        final EditText createlist = (EditText) info.findViewById(R.id.playlist_edit);

        Button btn = (Button) info.findViewById(R.id.button_add_xml);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createlist.getText().toString().length() > 0) {
                    String name = createlist.getText().toString();

                    DatabaseHelper db = new DatabaseHelper(OnlineActivity.this);
                    Playlist playlist = new Playlist(name);
                    db.addPlaylist(playlist);
                    loadlist();
                    Toast.makeText(OnlineActivity.this, "New playlist created ", Toast.LENGTH_SHORT).show();
                }
                builder.dismiss();

            }
        });
        builder.show();
    }

    public void loadlist() {
        playlists = mHelper.getAllplaylist();
        adapter = new PlaylistAdapter(this, playlists);
        listView.setAdapter(adapter);
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
}
