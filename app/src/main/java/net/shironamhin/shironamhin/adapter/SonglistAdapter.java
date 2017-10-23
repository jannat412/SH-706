package net.shironamhin.shironamhin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.shironamhin.shironamhin.R;
import net.shironamhin.shironamhin.model.Song;

import java.util.ArrayList;

/**
 * Created by Jannat Mostafiz on 8/17/2017.
 */

public class SonglistAdapter extends ArrayAdapter<Song> {
    private final Context context;
    private final ArrayList<Song> values;

    public SonglistAdapter(Context context, ArrayList<Song> values) {
        super(context, R.layout.songlist, values);

        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.songlist, parent, false);

        TextView songurl = (TextView) view.findViewById(R.id.songurl);
        TextView lyricsa = (TextView) view.findViewById(R.id.lyricsa);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView albumName = (TextView) view.findViewById(R.id.albumName);
        TextView songduration = (TextView) view.findViewById(R.id.songduration);
        ImageView imageView = (ImageView) view.findViewById(R.id.playIcon);

        // Setting the text to display
        songurl.setText(values.get(position).getAudiofile());
        lyricsa.setText(values.get(position).getLyrics());
        title.setText(values.get(position).getSongtitle());
        albumName.setText(values.get(position).getSongalbumname());
        songduration.setText(values.get(position).getDuration());
        imageView.setImageResource(R.drawable.mcircle);


        return view;
    }
}
