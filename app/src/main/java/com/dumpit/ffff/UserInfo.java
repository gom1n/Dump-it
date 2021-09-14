package com.dumpit.ffff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseAuth firebaseAuth;
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
        firebaseAuth = FirebaseAuth.getInstance();

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
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String email = user.getEmail();

            @Override
            public void onClick(View view) {
                firebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                        dlg.setTitle("Checking");
                        dlg.setMessage("비밀번호 재설정 메일을 전송하였습니다.");
                        dlg.setPositiveButton("확인", null);
                        dlg.show();
                    } else {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                        dlg.setTitle("Checking");
                        dlg.setMessage("비밀번호 재설정 메일 전송을 실패하였습니다.");
                        dlg.setPositiveButton("확인", null);
                        dlg.show();
                    }
                });
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
                // 회원 탈퇴 코드 (+)

                AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                dlg.setTitle("!!주의!!");
                dlg.setMessage("덤프잇(Dump it) 회원 탈퇴를 진행하시겠습니까?\n\n탈퇴를 진행하는 경우, \n같은 이메일로 가입이 어려울 수 있습니다.");
                dlg.setIcon(R.drawable.dust);
                dlg.setPositiveButton("탈퇴", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                        firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                                dlg.setTitle("Dump it!");
                                dlg.setMessage("Thank you for using our app:)\nSee you soon!");
                                dlg.setIcon(R.drawable.dust);
                                dlg.setPositiveButton("Bye", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        FirebaseAuth.getInstance().signOut();

                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                    }
                                });
                                dlg.show();
//                                Intent intent = new Intent(getApplicationContext(), Login.class);
//                                startActivity(intent);
                            }
                        });

                        /*user.delete();
                        AlertDialog.Builder dlg = new AlertDialog.Builder(UserInfo.this);
                        dlg.setTitle("Dump it!");
                        dlg.setMessage("Thank you for using our app:)\nSee you soon!");
                        dlg.setIcon(R.drawable.dust);
                        dlg.setPositiveButton("Bye", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }
                        });
                        dlg.show();*/
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });
    }
}
