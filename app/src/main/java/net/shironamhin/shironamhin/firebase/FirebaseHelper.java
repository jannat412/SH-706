package net.shironamhin.shironamhin.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.shironamhin.shironamhin.model.Songlist;

import java.util.ArrayList;

/**
 * Created by Jannat Mostafiz on 8/5/2017.
 */

public class FirebaseHelper {
    private Query query;
    ArrayList<Songlist> songlists=new ArrayList<>();

    public FirebaseHelper(Query query) {
        this.query = query;
    }

    public ArrayList<Songlist> retrieve()
    {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               for (DataSnapshot dp: dataSnapshot.getChildren()){

                   Songlist songlist=dp.getValue(Songlist.class);
                   songlists.add(songlist);

               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return songlists;
    }
}
