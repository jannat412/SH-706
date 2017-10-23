package net.shironamhin.shironamhin.model;

/**
 * Created by Jannat Mostafiz on 7/16/2017.
 */

public class Blog {
    public String videoname;
    public String albumname;
    public String channelname;
    public String views;
    public String image;
    public String videoID;
    public String post_key;
    public String audiofile;
    public String firstsong;
    public String lyrics;

    public String albumImage;
    public String albumNames;
    public String albumSongs;

    public String date;
    public String time;
    public String post_title;
    public String post_detail;

    public String lyricsName;
    public String lyricsImage;
    public String lyricsSong;

    public String youtubeIcon;

    public String getAudiofile() {
        return audiofile;
    }

    public void setAudiofile(String audiofile) {
        this.audiofile = audiofile;
    }

    public String getLyricsName() {
        return lyricsName;
    }

    public void setLyricsName(String lyricsName) {
        this.lyricsName = lyricsName;
    }

    public String getLyricsImage() {
        return lyricsImage;
    }

    public void setLyricsImage(String lyricsImage) {
        this.lyricsImage = lyricsImage;
    }

    public String getLyricsSong() {
        return lyricsSong;
    }

    public void setLyricsSong(String lyricsSong) {
        this.lyricsSong = lyricsSong;
    }

    public String getYoutubeIcon() {
        return youtubeIcon;
    }

    public void setYoutubeIcon(String youtubeIcon) {
        this.youtubeIcon = youtubeIcon;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getFirstsong() {
        return firstsong;
    }

    public void setFirstsong(String firstsong) {
        this.firstsong = firstsong;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }



    public Blog(String videoname, String post_key, String audiofile, String albumname, String channelname, String views, String image, String videoID, String albumImage,
                String albumNames, String albumSongs, String date, String time, String post_title, String post_detail,
                String lyricsName, String lyricsImage, String lyricsSong, String youtubeIcon, String firstsong, String lyrics) {
        this.post_key = post_key;
        this.videoname = videoname;
        this.albumname = albumname;
        this.channelname = channelname;
        this.views = views;
        this.image = image;
        this.videoID = videoID;
        this.albumImage = albumImage;
        this.albumNames = albumNames;
        this.albumSongs = albumSongs;
        this.date = date;
        this.time = time;
        this.post_title = post_title;
        this.audiofile = audiofile;
        this.firstsong= firstsong;
        this.lyrics = lyrics;

        this.post_detail = post_detail;
        this.lyricsName = lyricsName;
        this.lyricsImage = lyricsImage;
        this.lyricsSong = lyricsSong;
        this.youtubeIcon = youtubeIcon;


    }
    public Blog(){

    }

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getAlbumNames() {
        return albumNames;
    }

    public void setAlbumNames(String albumNames) {
        this.albumNames = albumNames;
    }

    public String getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(String albumSongs) {
        this.albumSongs = albumSongs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_detail() {
        return post_detail;
    }

    public void setPost_detail(String post_detail) {
        this.post_detail = post_detail;
    }





}
