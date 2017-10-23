package net.shironamhin.shironamhin.model;

/**
 * Created by Jannat Mostafiz on 9/25/2017.
 */

public class Youtubelist {
    public String videoname;
    public String albumname;
    public String channelname;
    public String views;
    public String image;
    public String videoID;

    public Youtubelist(String videoname, String albumname, String channelname, String views, String image, String videoID) {
        this.videoname = videoname;
        this.albumname = albumname;
        this.channelname = channelname;
        this.views = views;
        this.image = image;
        this.videoID = videoID;
    }
    public Youtubelist(){

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
}
