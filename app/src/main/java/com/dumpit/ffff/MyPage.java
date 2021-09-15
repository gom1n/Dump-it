package com.dumpit.ffff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

public class MyPage extends Fragment {
    ViewGroup viewGroup;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView editInfo;
    TextView logout;
    TextView name;
    TextView point;
    TextView pointtxt;
    TextView storetxt;

    Button selectBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.mypage, container, false);

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        name = (TextView)viewGroup.findViewById(R.id.name);
        point = (TextView)viewGroup.findViewById(R.id.point);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = user.getEmail();
                int index = email.indexOf("@");
                String id = email.substring(0, index);
                String web = email.substring(index+1);
                int webidx = web.indexOf(".");
                String website = web.substring(0, webidx);
                String getname = snapshot.child("users").child(id+"_"+website).child("nickname").getValue(String.class);
                name.setText(getname);
                Integer getpoint = snapshot.child("users").child(id+"_"+website).child("Totalpoint").getValue(Integer.class);
                point.setText(String.valueOf(getpoint));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editInfo = (TextView)viewGroup.findViewById(R.id.editInfo);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserInfo.class);
                startActivity(intent);
            }
        });
        logout = (TextView)viewGroup.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        pointtxt = (TextView)viewGroup.findViewById(R.id.pointButton);
        pointtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PointList.class);
                startActivity(intent);
            }
        });

        storetxt = (TextView)viewGroup.findViewById(R.id.storeButton);
        storetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BuyList.class);
                startActivity(intent);
            }
        });


        LinearLayout layout01 = (LinearLayout) viewGroup.findViewById(R.id.ask);
        layout01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:01077777777"));
                startActivity(tt);

            }
        });
        LinearLayout layout02 = (LinearLayout) viewGroup.findViewById(R.id.notice);
        layout02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(getContext(), Notice.class);
                startActivity(t);

            }
        });
        LinearLayout layout03 = (LinearLayout) viewGroup.findViewById(R.id.personal);
        layout03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(Intent.ACTION_VIEW,Uri.parse("https://dumpit2021.blogspot.com/2021/06/blog-post.html"));
                startActivity(t);

            }
        });
        LinearLayout layout04 = (LinearLayout) viewGroup.findViewById(R.id.service);
        layout04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(Intent.ACTION_VIEW,Uri.parse("https://dumpit2021.blogspot.com/2021/06/blog-post_42.html"));
                startActivity(t);

            }
        });
        return viewGroup;
    }

}
