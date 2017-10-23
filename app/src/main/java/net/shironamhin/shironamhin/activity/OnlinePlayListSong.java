package net.shironamhin.shironamhin.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.VerticalMarqueeTextView;
import net.shironamhin.shironamhin.adapter.SonglistAdapter;
import net.shironamhin.shironamhin.database.DatabaseHelper;
import net.shironamhin.shironamhin.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class OnlinePlayListSong extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    ImageButton playprevious,plyabtn,playnext;
    private MediaPlayer mp = new MediaPlayer();
    private Uri u;
    private SeekBar seekBar;

    // ArrayList<Songlist> songlists=new ArrayList<Songlist>();

    String currentPlayingSong;
    String currentPlayingName;
    String currentPlayingAlbum;
    String currentlyrics;
    String playlistName;
    String listName;

    private int position=0;
    ImageView fab_plus,fab_share,fab_volume,fab_karaokey,fab_refrsh;
    Animation FadeOpen,FadeClose;
    boolean isOpen=false;
    private Switch aSwitch;
    private TextView mtext;
    //String post_key;
    private ImageView imageAlbum;
    private String image;
    TextView currentDurationOfsong, durationOfsong, currentsong, currentalbum, dvd, text_view;
    SeekBar volume;
    private int maxVolume;
    private int curVolume;
    private AudioManager audioManager;
    boolean value = false;
    private SonglistAdapter adapter ;
    private ListView listView;

    boolean switchOn;
    public int deleteItem;

    private ArrayList<Song> songArrayList ;
    private DatabaseHelper databaseHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_play_list_song);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.homePage);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mp.stop();
                    Intent intent = new Intent(OnlinePlayListSong.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                }catch (Exception e){
                    e.printStackTrace();
                    Intent intent = new Intent(OnlinePlayListSong.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);

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
        TextView menu = (TextView) findViewById(R.id.textMenu) ;
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

        playprevious=(ImageButton)findViewById(R.id.previous);
        plyabtn=(ImageButton)findViewById(R.id.play);
        playnext=(ImageButton)findViewById(R.id.next);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        imageAlbum = (ImageView) findViewById(R.id.imageAlbum);
        currentDurationOfsong = (TextView) findViewById(R.id.currentdurationOfsong);
        durationOfsong = (TextView) findViewById(R.id.durationOfsong);
        currentsong = (TextView) findViewById(R.id.currentsong);
        currentalbum = (TextView) findViewById(R.id.currentalbum);
        aSwitch = (Switch) findViewById(R.id.switch_xml);
        mtext = (TextView) findViewById(R.id.mediatext);
        mtext.setMovementMethod(new ScrollingMovementMethod());
        dvd = (TextView) findViewById(R.id.dvd);
        text_view = (TextView) findViewById(R.id.text_view);

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
            public void onProgressChanged(SeekBar arg0, int arg1,
                                          boolean arg2) {
                value=true;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        arg1, 0);
            }
        });

        Log.d("OnlinePlayListSong","OnCreate()");
        databaseHelper = new DatabaseHelper(OnlinePlayListSong.this);
        // new code

        Intent intent =getIntent();

        if(intent!=null){
            Log.d("OnlinePlayListSong","Intent Not Null");
            int playListId = intent.getIntExtra("play_list_id",-1);
            switchOn = intent.getBooleanExtra("switchbtn", false);
            songArrayList = new ArrayList<>();
            songArrayList =  databaseHelper.getAllSongByPlaylistId(playListId);

            for(int i = 0 ; i<songArrayList.size(); i++){
                Log.d("OnlinePlayListSong", "URL  "+songArrayList.get(i).getAudiofile() +" Name "+songArrayList.get(i).getSongtitle()
                        +"LYrice "+songArrayList.get(i).getLyrics());
            }
        }
        adapter = new SonglistAdapter(this, songArrayList);

        listView=(ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(myClickListener);

        navigation();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                plyabtn.setImageResource(R.drawable.pausebtn);
                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(OnlinePlayListSong.this, R.anim.test);
                view.startAnimation(hyperspaceJumpAnimation);
                if(position< songArrayList.size()){
                    try {
                        mp.reset();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        position=(position)% songArrayList.size();
                        currentPlayingSong = songArrayList.get(position).getAudiofile();
                        currentPlayingName = songArrayList.get(position).getSongtitle();
                        currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                        currentlyrics = songArrayList.get(position).getLyrics();
                        mtext.setText(currentlyrics.toString());
                       // mtext.setVisibility(View.INVISIBLE);
                        text_view.setText("Song view");
                        playlistName = songArrayList.get(position).getListname();

                        currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                        currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                        mp.setDataSource(currentPlayingSong.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
                else
                {
                    mp.release();
                }
            }
        });
        playprevious.setOnClickListener(this);
        plyabtn.setOnClickListener(this);
        playnext.setOnClickListener(this);
        switchOperation();

        if (mp != null) {
            plyabtn.setImageResource(R.drawable.pausebtn);
            mp.stop();
            mp.release();
        }
        try{
            position=(position)% songArrayList.size();
            currentPlayingName = songArrayList.get(position).getSongtitle();
            currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
            currentlyrics = songArrayList.get(position).getLyrics();
            mtext.setText(currentlyrics.toString());
           // mtext.setVisibility(View.INVISIBLE);
            text_view.setText("Song view");

            playlistName = songArrayList.get(position).getListname();
            currentsong.setText(songArrayList.get(position).getSongtitle().toString());
            currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());

            u = Uri.parse(songArrayList.get(position).getAudiofile().toString());
            mp = MediaPlayer.create(getApplicationContext(), u);
            mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }


        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if(mp.isPlaying()){
                        mp.pause();
                        plyabtn.setImageResource(R.drawable.playbtn);
                    }else {}
                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position< songArrayList.size())
                {
                    try {
                        mp.reset();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        position=(position+1)% songArrayList.size();
                        currentPlayingSong = songArrayList.get(position).getAudiofile();
                        currentPlayingName = songArrayList.get(position).getSongtitle();
                        currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                        currentlyrics = songArrayList.get(position).getLyrics();
                        mtext.setText(currentlyrics.toString());
                       // mtext.setVisibility(View.INVISIBLE);
                        text_view.setText("Song view");

                        playlistName = songArrayList.get(position).getListname();

                        currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                        currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                        mp.setDataSource(currentPlayingSong.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
                else
                {
                    try {
                    mp.release();
                }catch (Exception e){
                    e.printStackTrace();
                }

                }
            }
        });

        try{
            seekBar.setMax(mp.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try{
                        if(mp.isPlaying()){
                            try {
                                mp.seekTo(seekBar.getProgress());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Thread t = new OnlinePlayListSong.runThread();
            t.start();
            Floating();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public class runThread extends Thread {
        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Runwa", "run: " + 1);
                if (mp != null) {
                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                long second = (mp.getDuration() / 1000) % 60;
                                long minute = (mp.getDuration() / (1000 * 60)) % 60;
                                String totalDuration = String.format("%02d:%02d", minute, second);

                                long second1 = (mp.getCurrentPosition() / 1000) % 60;
                                long minute1 = (mp.getCurrentPosition() / (1000 * 60)) % 60;
                                String currentDuration = String.format("%02d:%02d", minute1, second1);

                                durationOfsong.setText(""+totalDuration);
                                currentDurationOfsong.setText(""+currentDuration);
                                seekBar.setProgress(mp.getCurrentPosition());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });

//                    Log.d("Runwa", "run: " + mp.getCurrentPosition());
                }
            }
        }

    }
    @Override
    public void onBackPressed() {
        if(mp == null){
            finish();
        }else {
            try{
                mp.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
            finish();
        }

    }
    @Override
    public void onClick(View view) {
     int id=view.getId();
     switch (id) {

        case R.id.play:
            try {
                if (mp.isPlaying()) {
                    plyabtn.setImageResource(R.drawable.playbtn);
                    mp.pause();
                } else {
                    plyabtn.setImageResource(R.drawable.pausebtn);
                    mp.start();
                }
            }catch (Exception e){
                e.printStackTrace();
                plyabtn.setImageResource(R.drawable.playbtn);
            }

            break;

        case R.id.next:
            plyabtn.setImageResource(R.drawable.pausebtn);
            if (position < songArrayList.size()) {
                try {
                    mp.reset();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    position = (position + 1) % songArrayList.size();
                    currentPlayingSong = songArrayList.get(position).getAudiofile();
                    currentPlayingName = songArrayList.get(position).getSongtitle();
                    currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                    currentlyrics = songArrayList.get(position).getLyrics();
                    mtext.setText(currentlyrics.toString());
                    mtext.setVisibility(View.INVISIBLE);
                    text_view.setText("Song view");

                    playlistName = songArrayList.get(position).getListname();

                    currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                    currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                    mp.setDataSource(currentPlayingSong.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            } else {
                mp.release();
            }
            break;

        case R.id.previous:
            plyabtn.setImageResource(R.drawable.pausebtn);
            if (position < songArrayList.size()) {
                try {
                    mp.reset();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    position = (position - 1 < 0) ? songArrayList.size() - 1 : position - 1;
                    currentPlayingSong = songArrayList.get(position).getAudiofile();
                    currentPlayingName = songArrayList.get(position).getSongtitle();
                    currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                    currentlyrics = songArrayList.get(position).getLyrics();
                    mtext.setText(currentlyrics.toString());
                    mtext.setVisibility(View.INVISIBLE);
                    text_view.setText("Song view");

                    playlistName = songArrayList.get(position).getListname();

                    currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                    currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                    mp.setDataSource(currentPlayingSong.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            } else {
                mp.release();
            }
            break;
    }
}
    public AdapterView.OnItemLongClickListener myClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            try {
                deleteItem = arg2;
                AlertDialog alert = new AlertDialog.Builder(arg1.getContext())
                        .setTitle("Delete " + songArrayList.get(deleteItem).getSongtitle())
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        try {
                                            Song song = songArrayList.get(deleteItem);
                                            songArrayList.remove(deleteItem);
                                            DatabaseHelper db = new DatabaseHelper(OnlinePlayListSong.this);
                                            db.deleteSonglist(song.id);
                                            adapter.notifyDataSetChanged();
                                            if (mp.isPlaying()){
                                                if (position < songArrayList.size()) {
                                                    try {
                                                        mp.reset();
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        position = (position + 1) % songArrayList.size();
                                                        currentPlayingSong = songArrayList.get(position).getAudiofile();
                                                        currentPlayingName = songArrayList.get(position).getSongtitle();
                                                        currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                                                        currentlyrics = songArrayList.get(position).getLyrics();
                                                        mtext.setText(currentlyrics.toString());
                                                        //mtext.setVisibility(View.INVISIBLE);
                                                        text_view.setText("Song view");

                                                        playlistName = songArrayList.get(position).getListname();
                                                        currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                                                        currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                                                        mp.setDataSource(currentPlayingSong.toString());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        mp.prepare();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    mp.start();
                                                } else {
                                                    mp.release();
                                                }
                                            }
                                            dialog.dismiss();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.cancel();
                                    }
                                }).show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
    };
    public void Floating(){


        fab_plus=(ImageView)findViewById(R.id.first);
        fab_share=(ImageView)findViewById(R.id.fourth);
        fab_volume=(ImageView)findViewById(R.id.second);
        fab_refrsh=(ImageView)findViewById(R.id.third);

        FadeOpen= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floatingbutton_open);
        FadeClose=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floatingbutton_close);


        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume.setVisibility(View.INVISIBLE);
                if (isOpen){

                    fab_share.startAnimation(FadeClose);
                    fab_volume.startAnimation(FadeClose);
                    fab_refrsh.startAnimation(FadeClose);

                    fab_share.setClickable(false);
                    fab_volume.setClickable(false);
                    fab_refrsh.setClickable(false);
                    isOpen=false;

                }

                else {

                    fab_share.startAnimation(FadeOpen);
                    fab_volume.startAnimation(FadeOpen);
                    fab_refrsh.startAnimation(FadeOpen);

                    fab_share.setClickable(true);
                    fab_volume.setClickable(true);
                    fab_refrsh.setClickable(true);
                    isOpen=true;

                }

            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareSubText = "Let's enjoy music of Shironamhin";
                String shareBodyText = "Let's enjoy music of Shironamhin."+ currentPlayingName+ currentPlayingAlbum+ currentPlayingSong;
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
                if(position< songArrayList.size())
                {
                    mp.reset();
                    try {
                        position=(position)% songArrayList.size();
                        currentPlayingSong = songArrayList.get(position).getAudiofile();
                        currentPlayingName = songArrayList.get(position).getSongtitle();
                        currentPlayingAlbum = songArrayList.get(position).getSongalbumname();
                        currentlyrics = songArrayList.get(position).getLyrics();
                        mtext.setText(currentlyrics.toString());
                        //mtext.setVisibility(View.INVISIBLE);
                        text_view.setText("Song view");

                        playlistName = songArrayList.get(position).getListname();

                        currentsong.setText(songArrayList.get(position).getSongtitle().toString());
                        currentalbum.setText(songArrayList.get(position).getSongalbumname().toString());
                        mp.setDataSource(currentPlayingSong.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
                else
                {
                    mp.release();
                }
            }
        });



    }
    private void switchOperation(){
        //aSwitch.setChecked(switchOn);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean lyrics) {

                    if (lyrics) {
                        mtext.setVisibility(View.VISIBLE);
                        volume.setVisibility(View.INVISIBLE);
                        playprevious.setVisibility(View.INVISIBLE);
                        plyabtn.setVisibility(View.INVISIBLE);
                        playnext.setVisibility(View.INVISIBLE);
                        seekBar.setVisibility(View.INVISIBLE);
                        text_view.setText("Lyric view");
                        currentDurationOfsong.setVisibility(View.INVISIBLE);
                        durationOfsong.setVisibility(View.INVISIBLE);
                        currentsong.setVisibility(View.INVISIBLE);
                        currentalbum.setVisibility(View.INVISIBLE);
                        dvd.setVisibility(View.INVISIBLE);

                    } else {
                        mtext.setVisibility(View.INVISIBLE);
                        volume.setVisibility(View.INVISIBLE);
                        playprevious.setVisibility(View.VISIBLE);
                        plyabtn.setVisibility(View.VISIBLE);
                        playnext.setVisibility(View.VISIBLE);
                        seekBar.setVisibility(View.VISIBLE);
                        text_view.setText("Song view");
                        currentDurationOfsong.setVisibility(View.VISIBLE);
                        durationOfsong.setVisibility(View.VISIBLE);
                        currentsong.setVisibility(View.VISIBLE);
                        currentalbum.setVisibility(View.VISIBLE);
                        dvd.setVisibility(View.VISIBLE);
                    }

                }
            });
    }
    public void navigation(){
        LinearLayout online= (LinearLayout)findViewById(R.id.online);
        LinearLayout share= (LinearLayout)findViewById(R.id.share);
        LinearLayout contact= (LinearLayout)findViewById(R.id.contact);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOn = false;
                if(mp == null){
                    Intent intent = new Intent(OnlinePlayListSong.this, OnlineActivity.class);
                    intent.putExtra("enable", switchOn);
                    startActivity(intent);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.END);
                }else {
                    try{
                        mp.stop();
                        Intent intent = new Intent(OnlinePlayListSong.this, OnlineActivity.class);
                        intent.putExtra("enable", switchOn);
                        startActivity(intent);
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(OnlinePlayListSong.this, OnlineActivity.class);
                    intent.putExtra("enable", switchOn);
                    startActivity(intent);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.END);
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
