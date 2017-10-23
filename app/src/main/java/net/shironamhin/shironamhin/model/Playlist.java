package net.shironamhin.shironamhin.model;

/**
 * Created by Jannat Mostafiz on 8/13/2017.
 */

public class Playlist {
    public int id;
    public String name;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Playlist(String name){
        this.name = name;
    }
    public Playlist(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
