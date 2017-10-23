package net.shironamhin.shironamhin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.shironamhin.shironamhin.model.Song;
import net.shironamhin.shironamhin.model.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jannat Mostafiz on 8/21/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Dataplaylist";
    private static final String TABLE_SONGLIST = "songlist";
    private static final String TABLE_PLAYLIST = "playlist";
    private static final String TABLE_PLAY_LIST_SONG = "playlistsong";
    private static final String COLUMN_SONG_ID = "song_id";
    private static final String COLUMN_SONG_TITLE = "song_title";
    private static final String COLUMN_SONG_ALBUM_NAME = "album_name";
    private static final String COLUMN_SONG_DURATION = "duration";
    private static final String COLUMN_SONG_AUDIOFILE = "audiofile";
    private static final String COLUMN_SONG_LYRICS = "lyrics";
    private static final String COLUMN_SONG_LIST_NAME = "listname";

    private static final String COLUMN_PLAYLIST_NAME = "playlist_name ";
    private static final String COLUMN_PLAYLIST_ID = "play_list_id";

    private static final String COLUMN_PLAY_LIST_SONG_ID = "play_list_song_id";


    private static final String CREATE_TABLE_SONGLIST = "create table if not exists "
            + TABLE_SONGLIST
            + " ( "
            + COLUMN_SONG_ID + " integer primary key autoincrement,"
            + COLUMN_SONG_TITLE + " varchar(30),"
            + COLUMN_SONG_ALBUM_NAME + " varchar(30), "
            + COLUMN_SONG_DURATION + " varchar(30),"
            + COLUMN_SONG_AUDIOFILE + " varchar(1000000),"
            + COLUMN_SONG_LYRICS + " varchar(1000000),"
            + COLUMN_SONG_LIST_NAME + " varchar(50) )";


    private static final String CREATE_TABLE_PLAYLIST = "create table if not exists "
            + TABLE_PLAYLIST
            + " ( "+
            COLUMN_PLAYLIST_ID+" integer primary key autoincrement,"
            + COLUMN_PLAYLIST_NAME+" varchar(50) )";
    private static final String CREATE_TABLE_PLAYLIST_SONG = "create table if not exists "
            + TABLE_PLAY_LIST_SONG
            + " ( " +
            COLUMN_PLAY_LIST_SONG_ID+ " integer primary key autoincrement,"
            +COLUMN_PLAYLIST_ID+" integer,"
            +COLUMN_SONG_ID+" integer)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TABLE_SONGLIST);
        db.execSQL(CREATE_TABLE_PLAYLIST);
        db.execSQL(CREATE_TABLE_PLAYLIST_SONG);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_LIST_SONG);
        onCreate(db);

    }


    public int getSongIdByName(String name ){
        int id =-1;
        String query = "SELECT "+COLUMN_SONG_ID+" FROM " + TABLE_SONGLIST+" WHERE "+COLUMN_SONG_TITLE+"="+ '"'+name+'"';
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("Database","Song ID By Name "+id+" Name: "+name);
        return id;

    }
    public int getPlaylistIdByName(String playlistName){
        int id =-1;
        String query = "SELECT "+COLUMN_PLAYLIST_ID+" FROM " + TABLE_PLAYLIST+" WHERE "+COLUMN_PLAYLIST_NAME+"="+ '"'+playlistName+'"';


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_PLAYLIST_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("Database","Playlist ID By Name "+id+" Name: "+playlistName);
        return id;
    }


    public void insertIntoPlayListSong(int playlistId , int songId){

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_ID,songId);
        values.put(COLUMN_PLAYLIST_ID,playlistId);
        db.insert(TABLE_PLAY_LIST_SONG, null, values);
        Log.d("Database","Song added into playlist By playListId "+playlistId+" Songid: "+songId);
        db.close();
    }

    public ArrayList<Song> getAllSongByPlaylistId(int id){
        ArrayList<Song> songList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PLAY_LIST_SONG+" WHERE "+COLUMN_PLAYLIST_ID+"="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
               int songId =  cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
               Song song = getSongById(songId);
                songList.add(song);
            }
            while (cursor.moveToNext());
        }

        cursor.close();


        return songList ;
    }

    private Song getSongById(int id) {
        Song song = new Song();
        String query = "SELECT * FROM " +TABLE_SONGLIST +" WHERE "+COLUMN_SONG_ID+"="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
                song.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID)));
                song.setSongtitle(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE)));
                song.setAudiofile(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_AUDIOFILE)));
                song.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_DURATION)));
                song.setLyrics(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_LYRICS)));
                song.setSongalbumname(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ALBUM_NAME)));
                song.setListname(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_LIST_NAME)));
        }

        cursor.close();

        Log.d("Database","Song by id "+ song.getSongtitle());
        return song ;
    }


    public void addSonglist(Song song) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_TITLE, song.getSongtitle());
        values.put(COLUMN_SONG_ALBUM_NAME, song.getSongalbumname());
        values.put(COLUMN_SONG_DURATION, song.getDuration());
        values.put(COLUMN_SONG_AUDIOFILE, song.getAudiofile());
        values.put(COLUMN_SONG_LYRICS, song.getLyrics());
        values.put(COLUMN_SONG_LIST_NAME, song.getListname());
        db.insert(TABLE_SONGLIST, null, values);
        db.close();
    }

    public void addPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYLIST_NAME, playlist.getName());
        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }


    public ArrayList<Song> getAllsonglist() {
        ArrayList<Song> songList = new ArrayList<Song>();
        String selectQuery = "SELECT  * FROM " + TABLE_SONGLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(Integer.parseInt(cursor.getString(0)));
                song.setSongtitle(cursor.getString(1));
                song.setSongalbumname(cursor.getString(2));
                song.setDuration(cursor.getString(3));
                song.setAudiofile(cursor.getString(4));
                song.setLyrics(cursor.getString(5));
                song.setListname(cursor.getString(6));

                // Adding contact to list
                songList.add(song);
            } while (cursor.moveToNext());
        }

        // return contact list
        return songList;

    }

    public ArrayList<Playlist> getAllplaylist() {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Playlist playlist = new Playlist();
                playlist.setId(Integer.parseInt(cursor.getString(0)));
                playlist.setName(cursor.getString(1));

                // Adding contact to list
                playlists.add(playlist);
            } while (cursor.moveToNext());
        }

        // return contact list
        return playlists;

    }

    public void deleteSonglist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TABLE_PLAY_LIST_SONG + " WHERE " + COLUMN_SONG_ID
                + "=" + id + "");
    }

    public void deleteplaylist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST + " WHERE " + COLUMN_PLAYLIST_ID
                + "=" + id + "");
    }

}
