package net.shironamhin.shironamhin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.model.Blog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private String VIDEO_ID;
    private String SONG_NAME;
    private RecyclerView mBlogList;
    private RecyclerView mBlogList2;
    private RecyclerView mBlogList3;
    private RecyclerView mBlogList4;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceNews;
    private DatabaseReference databaseReferenceLyrics;
    private ProgressBar progressBar;
    public boolean switchOn= false;
    private boolean internetOn = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation();
        //**********************

        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Opps!..No internet connection.");
            alertDialogBuilder
                    .setMessage("Please Connect to Internet!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try{
                                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }

                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
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

        Button button = (Button) findViewById(R.id.seeMore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOn = false;
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("enable", switchOn);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Albums");
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, MainActivity.AlbumviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog,
                MainActivity.AlbumviewHolder>(
                Blog.class, R.layout.albums, MainActivity.AlbumviewHolder.class,mDatabase){
            @Override
            protected void populateViewHolder(MainActivity.AlbumviewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setAlbumNames(model.getAlbumNames());
                viewHolder.setAlbumSongs(model.getAlbumSongs());
                viewHolder.setAlbumImage(getApplicationContext(),model.getAlbumImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                            try{
                                internetOn=true;
                                switchOn= false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("post_key",post_key);
                                intent.putExtra("internetOn",internetOn);
                                intent.putExtra("url", model.getAudiofile().toString());
                                intent.putExtra("image", model.getAlbumImage());
                                intent.putExtra("firstsong", model.getFirstsong());
                                intent.putExtra("album", model.getAlbumNames());
                                intent.putExtra("currentlyrics", model.getLyrics());
                                intent.putExtra("switchbtn", switchOn);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        } else if (
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                            try{
                                internetOn=false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("internetOn",internetOn);
                                intent.putExtra("post_key", post_key);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);



        FirebaseRecyclerAdapter<Blog, MainActivity.VideolistHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<Blog,
                VideolistHolder>(Blog.class, R.layout.videos, MainActivity.VideolistHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(VideolistHolder viewHolder, final Blog model, int position) {

                viewHolder.setVideoID(model.getVideoID());
                viewHolder.setVideoname(model.getVideoname());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setViews(model.getViews());
                viewHolder.mView2.setOnClickListener(new View.OnClickListener() {
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
        mBlogList2.setAdapter(firebaseRecyclerAdapter1);

        FirebaseRecyclerAdapter<Blog, MainActivity.NewsviewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Blog,
                NewsviewHolder>(Blog.class, R.layout.news, MainActivity.NewsviewHolder.class, databaseReferenceNews) {
            @Override
            protected void populateViewHolder(NewsviewHolder viewHolder, final Blog model, int position) {

                    viewHolder.setDate(model.getDate());
                    viewHolder.setTime(model.getTime());
                    viewHolder.setPost_title(model.getPost_title());
                    viewHolder.setPost_detail(model.getPost_detail());
            }
        };
        mBlogList3.setAdapter(firebaseRecyclerAdapter2);

        FirebaseRecyclerAdapter<Blog, MainActivity.LyricsviewHolder> firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<Blog,
                MainActivity.LyricsviewHolder>(
                Blog.class, R.layout.lyrics, MainActivity.LyricsviewHolder.class,databaseReferenceLyrics){
            @Override
            protected void populateViewHolder(MainActivity.LyricsviewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setLyricsName(model.getLyricsName());
                viewHolder.setLyricsSong(model.getLyricsSong());
                viewHolder.setLyricsImage(getApplicationContext(),model.getLyricsImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                            try {
                                internetOn=true;
                                switchOn = true;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("post_key",post_key);
                                intent.putExtra("internetOn",internetOn);
                                intent.putExtra("url", model.getAudiofile().toString());
                                intent.putExtra("image", model.getLyricsImage());
                                intent.putExtra("firstsong", model.getFirstsong());
                                intent.putExtra("album", model.getLyricsName());
                                intent.putExtra("currentlyrics", model.getLyrics());
                                intent.putExtra("switchbtn",switchOn);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        } else if (
                                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                            try{
                                internetOn=false;
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                intent.putExtra("internetOn",internetOn);
                                intent.putExtra("post_key", post_key);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }

        };
        mBlogList4.setAdapter(firebaseRecyclerAdapter3);

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public  static class AlbumviewHolder extends RecyclerView.ViewHolder{
        View mView;
        public AlbumviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setAlbumNames(String albumNames){
            TextView album_name = (TextView) mView.findViewById(R.id.name);
            album_name.setText(albumNames);
        }
        public void setAlbumSongs(String albumSongs){
            TextView album_songs = (TextView) mView.findViewById(R.id.songs);
            album_songs.setText(albumSongs);
        }
        public void setAlbumImage(final Context context, final String albumImage){
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
    public  static class VideolistHolder extends RecyclerView.ViewHolder{
        View mView2;
        public VideolistHolder(View itemView2) {
            super(itemView2);
            mView2 = itemView2;
        }
        public void setVideoID(String videoID){

        }
        public void setVideoname(String videoname){
            TextView video_name = (TextView) mView2.findViewById(R.id.vname);
            video_name.setText(videoname);
        }
        public void setViews(String views){
            TextView viewer = (TextView) mView2.findViewById(R.id.viewer);
            viewer.setText(views);
        }
        public void setImage(final Context context, final String image){
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
    public  static class NewsviewHolder extends RecyclerView.ViewHolder{
        View mView3;
        public NewsviewHolder(View itemView3) {
            super(itemView3);
            mView3 = itemView3;
        }
        public void setDate(String date){
            TextView postDate = (TextView) mView3.findViewById(R.id.date);
            postDate.setText(date);
        }
        public void setTime(String time){
            TextView postTime = (TextView) mView3.findViewById(R.id.time);
            postTime.setText(time);
        }
        public void setPost_title(String post_title){
            TextView postTitle = (TextView) mView3.findViewById(R.id.post_title);
            postTitle.setText(post_title);
        }
        public void setPost_detail(String post_detail){
            TextView postDetail = (TextView) mView3.findViewById(R.id.post_detail);
            postDetail.setText(post_detail);
        }
    }
    public  static class LyricsviewHolder extends RecyclerView.ViewHolder{
        View mView;
        public LyricsviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setLyricsName(String lyricsName){
            TextView lyrics_name = (TextView) mView.findViewById(R.id.name);
            lyrics_name.setText(lyricsName);
        }
        public void setLyricsSong(String lyricsSong){
            TextView lyrics_songs = (TextView) mView.findViewById(R.id.songs);
            lyrics_songs.setText(lyricsSong);
        }
        public void setLyricsImage(final Context context, final String lyricsImage){
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
    public void navigation(){
        LinearLayout online= (LinearLayout)findViewById(R.id.online);
        LinearLayout share= (LinearLayout)findViewById(R.id.share);
        LinearLayout contact= (LinearLayout)findViewById(R.id.contact);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOn = false;
                Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                intent.putExtra("internetOn", internetOn);
                intent.putExtra("enable", switchOn);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
