package net.shironamhin.shironamhin.model;

/**
 * Created by Jannat Mostafiz on 8/22/2017.
 */

public class Song {
    String songtitle;
    String songalbumname;
    String duration;
    String audiofile;
    String lyrics;
    String listname;
    public int  id;

    public Song(String songtitle, String songalbumname, String duration, String audiofile, String lyrics, String listname, int id) {
        this.songtitle = songtitle;
        this.songalbumname = songalbumname;
        this.duration = duration;
        this.audiofile = audiofile;
        this.lyrics = lyrics;
        this.listname=listname;
        this.id = id;
    }
    public Song(String songtitle, String songalbumname, String duration, String audiofile, String lyrics, String listname) {
        this.songtitle = songtitle;
        this.songalbumname = songalbumname;
        this.duration = duration;
        this.audiofile = audiofile;
        this.lyrics = lyrics;
        this.listname=listname;
    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public Song(){}

    public String getSongtitle() {
        return songtitle;
    }

    public void setSongtitle(String songtitle) {
        this.songtitle = songtitle;
    }

    public String getSongalbumname() {
        return songalbumname;
    }

    public void setSongalbumname(String songalbumname) {
        this.songalbumname = songalbumname;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudiofile() {
        return audiofile;
    }

    public void setAudiofile(String audiofile) {
        this.audiofile = audiofile;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
