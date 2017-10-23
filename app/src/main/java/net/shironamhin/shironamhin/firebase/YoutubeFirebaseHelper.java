package net.shironamhin.shironamhin.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.shironamhin.shironamhin.model.Songlist;
import net.shironamhin.shironamhin.model.Youtubelist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jannat Mostafiz on 9/25/2017.
 */

public class YoutubeFirebaseHelper {
    private DatabaseReference mDatabase;
    ArrayList<Youtubelist> list=new ArrayList<>();

    public YoutubeFirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }
    public ArrayList<Youtubelist> getListData(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dp: dataSnapshot.getChildren()){

                    Youtubelist youtubelist=dp.getValue(Youtubelist.class);
                    list.add(youtubelist);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return list;
    }

}
