package net.shironamhin.shironamhin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.shironamhin.shironamhin.model.Playlist;
import net.shironamhin.shironamhin.R;

import java.util.ArrayList;

/**
 * Created by Jannat Mostafiz on 8/21/2017.
 */

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private final Context mContext;
    private final ArrayList<Playlist> playlists;

    public PlaylistAdapter(Context mContext, ArrayList<Playlist> playlists){
        super(mContext, R.layout.online_playlist, playlists);
        this.mContext = mContext;
        this.playlists = playlists;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.online_playlist, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.mcreate_list_title);
        name.setText(playlists.get(position).getName());
        return rowView;
    }
}
