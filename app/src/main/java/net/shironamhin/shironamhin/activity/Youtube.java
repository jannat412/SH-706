package net.shironamhin.shironamhin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.shironamhin.shironamhin.EqualizerView;
import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.firebase.YoutubeFirebaseHelper;
import net.shironamhin.shironamhin.model.Blog;
import net.shironamhin.shironamhin.model.Youtubelist;

import java.util.ArrayList;


public class Youtube extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private TextView songName;
    private ProgressBar progressBar;
    private boolean mProcessYoutube = false;

    public static final String API_KEY = "AIzaSyC4R_tgQn5kk-o-ZIh3bwBHBa5b6BirKm0";
    String VIDEO_ID;
    String SONG_NAME;

    ArrayList<Youtubelist> list;
    private YoutubeFirebaseHelper youtubeFirebaseHelper;
    private int position = 0;

    EqualizerView equalizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        equalizer = (EqualizerView) findViewById(R.id.equalizer_view);

        Bundle bundle = getIntent().getExtras();
        VIDEO_ID = bundle.getString("VIDEO_ID");
        SONG_NAME = bundle.getString("SONG_NAME");

        songName = (TextView) findViewById(R.id.songName);
        songName.setText(SONG_NAME);
        /** Initializing YouTube Player View **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("VideoSong");
        mDatabase.keepSynced(true);

        //*************************
        youtubeFirebaseHelper = new YoutubeFirebaseHelper(mDatabase);
        list = new ArrayList<>();
        list = youtubeFirebaseHelper.getListData();
        for(int i = 0 ; i<list.size(); i++){
        }
        //***********************

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, VideoviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, VideoviewHolder>(
                Blog.class, R.layout.videolist, VideoviewHolder.class,mDatabase){
            @Override
            protected void populateViewHolder(VideoviewHolder viewHolder, final Blog model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setVideoID(model.getVideoID());
                viewHolder.setVideoname(model.getVideoname());
                viewHolder.setAlbumname(model.getAlbumname());
                viewHolder.setChannelname(model.getChannelname());
                viewHolder.setViews(model.getViews());
                //viewHolder.setYoutubeIcon(getApplicationContext(),model.getYoutubeIcon());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    public  static class VideoviewHolder extends RecyclerView.ViewHolder{
        View mView;


        public VideoviewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setVideoID(String videoID){

        }
        public void setYoutubeIcon(final Context context, final String youtubeIcon){
           ImageView imageViewYoutube = (ImageView) mView.findViewById(R.id.youtubeIcon);
            imageViewYoutube.setImageResource(R.drawable.song_running);

        }
        public void setVideoname(String videoname){
            TextView video_name = (TextView) mView.findViewById(R.id.vname);
            video_name.setText(videoname);
        }
        public void setAlbumname(String albumname){
            TextView album_name = (TextView) mView.findViewById(R.id.albumName);
            album_name.setText(albumname);
        }
        public void setViews(String views){
            TextView viewsall = (TextView) mView.findViewById(R.id.views);
            viewsall.setText(views);
        }
        public void setChannelname(String channelname){
            TextView channel_name = (TextView) mView.findViewById(R.id.channelName);
            channel_name.setText(channelname);
        }
        public void setImage(final Context context, final String image){
            final ImageView post_image = (ImageView) mView.findViewById(R.id.icon);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(post_image);
                }
            });
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
            //player.cueVideos();
        }
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
            if (equalizer.isAnimating()) {
                equalizer.stopBars();
            } else {
                equalizer.animateBars();
            }
        }
        @Override
        public void onPlaying() {
            equalizer.animateBars();
        }
        @Override
        public void onSeekTo(int arg0) {
        }
        @Override
        public void onStopped() {
            equalizer.stopBars();
        }
    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
        }
        @Override
        public void onVideoStarted() {
        }
    };

        @Override
        public void onBackPressed() {
            Intent intent = new Intent(Youtube.this, MainActivity.class);
            startActivity(intent);
            finish();
     }
}
