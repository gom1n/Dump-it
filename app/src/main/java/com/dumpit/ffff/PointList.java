package com.dumpit.ffff;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PointList extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private TextView totalp;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    List<Object> Array = new ArrayList<Object>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        listView = (ListView)findViewById(R.id.point_lists);
        totalp = findViewById(R.id.totalp);

        String email = user.getEmail();
        int index = email.indexOf("@");
        String id = email.substring(0, index);
        String web = email.substring(index+1);
        int webidx = web.indexOf(".");
        String website = web.substring(0, webidx);

        DatabaseReference points = databaseReference.child(id+"_"+website).child("Totalpoint");
        DatabaseReference data = databaseReference.child(id+"_"+website).child("point");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);


        points.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String point = snapshot.getValue().toString();
                totalp.setText(point + "p");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){


               for(DataSnapshot snaps : snapshot.getChildren()) {
                   String date = snaps.getKey().toString();
                   String point = snaps.getValue().toString();

                   Array.add(date + " " + point);
                   adapter.add(date + " " +point);
               }

                adapter.notifyDataSetChanged();
               listView.setSelection(adapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }



}
