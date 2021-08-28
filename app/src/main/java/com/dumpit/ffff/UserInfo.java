package com.dumpit.ffff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView userid;
    EditText usernick;
    Button changepw;
    Button cancel;
    Button changeinfo;
    Button logout;
    Button withdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userid = (TextView) findViewById(R.id.input_edit_id);
        usernick = (EditText) findViewById(R.id.input_edit_nickname);
        changepw = (Button) findViewById(R.id.changepw);
        cancel = (Button)findViewById(R.id.cancel);
        changeinfo = (Button)findViewById(R.id.changeInfo);
        logout = (Button)findViewById(R.id.logout);
        withdraw = (Button)findViewById(R.id.withdraw);

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = user.getEmail();
                int index = email.indexOf("@");
                String id = email.substring(0, index);
                String web = email.substring(index+1);
                int webidx = web.indexOf(".");
                String website = web.substring(0, webidx);
                String getid = snapshot.child("users").child(id+"_"+website).child("email").getValue(String.class);
                userid.setText(getid);
                String getnick = snapshot.child("users").child(id+"_"+website).child("nickname").getValue(String.class);
                usernick.setText(getnick);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changeinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                dlg.setTitle("정보 변경 확인");
                dlg.setMessage("변경하시겠습니까?");
                dlg.setIcon(R.drawable.dust);
                dlg.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = user.getEmail();
                        int index = email.indexOf("@");
                        String id = email.substring(0, index);
                        String web = email.substring(index+1);
                        int webidx = web.indexOf(".");
                        String website = web.substring(0, webidx);
                        String newusernick = usernick.getText().toString().trim();
                        databaseReference.child("users").child(id+"_"+website).child("nickname").setValue(newusernick);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}