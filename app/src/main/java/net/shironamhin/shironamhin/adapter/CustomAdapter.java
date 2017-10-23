package net.shironamhin.shironamhin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.model.Songlist;

import java.util.ArrayList;

/**
 * Created by Jannat Mostafiz on 8/5/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<Songlist> songlists;

    public CustomAdapter(Context c, ArrayList<Songlist> songlists) {
        this.c = c;
        this.songlists = songlists;
    }
    @Override
    public int getCount() {
        return songlists.size();
    }
    @Override
    public Object getItem(int position) {
        return songlists.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.songlist,parent,false);
        }
        TextView title= (TextView) convertView.findViewById(R.id.title);
        TextView bandName= (TextView) convertView.findViewById(R.id.bandName);
        TextView albumName= (TextView) convertView.findViewById(R.id.albumName);
        TextView songduration = (TextView) convertView.findViewById(R.id.songduration);

        final Songlist s= (Songlist) this.getItem(position);
        title.setText(s.getSongtitle());
        bandName.setText(s.getBandname());
        albumName.setText(s.getSongalbumname());
        songduration.setText(s.getDuration());
        return convertView;
    }
}
