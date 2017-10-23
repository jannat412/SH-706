package net.shironamhin.shironamhin.model;

/**
 * Created by Jannat Mostafiz on 8/5/2017.
 */

public class Songlist {
     String songimage;
     String songtitle;
     String songalbumname;
     String bandname;
     String duration;
     String playimage;
     String audiofile;
     String post_key;
     String lyrics;
    String karaokey;


    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }


    public String getKaraokey() {
        return karaokey;
    }

    public void setKaraokey(String karaokey) {
        this.karaokey = karaokey;
    }

    public Songlist(String songimage, String songtitle, String songalbumname, String bandname,
                    String duration, String playimage, String audiofile, String post_key, String lyrics, String karaokey) {
        this.songimage = songimage;
        this.songtitle = songtitle;
        this.songalbumname = songalbumname;
        this.bandname = bandname;
        this.duration = duration;
        this.playimage = playimage;
        this.audiofile = audiofile;
        this.post_key = post_key;
        this.lyrics = lyrics;
        this.karaokey = karaokey;


    }

    public Songlist(){

    }

    public String getSongimage() {
        return songimage;
    }

    public void setSongimage(String songimage) {
        this.songimage = songimage;
    }

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

    public String getBandname() {
        return bandname;
    }

    public void setBandname(String bandname) {
        this.bandname = bandname;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlayimage() {
        return playimage;
    }

    public void setPlayimage(String playimage) {
        this.playimage = playimage;
    }

    public String getAudiofile() {
        return audiofile;
    }

    public void setAudiofile(String audiofile) {
        this.audiofile = audiofile;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }
}
