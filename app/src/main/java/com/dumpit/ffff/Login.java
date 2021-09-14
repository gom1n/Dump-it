package com.dumpit.ffff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    EditText loginID;
    EditText loginPW;
    Button loginbtn;
    Button signUp;

    ProgressDialog dialog;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();
        if(ninfo == null){
            AlertDialog.Builder dlg = new AlertDialog.Builder(Login.this);
            dlg.setTitle("네트워크 오류");
            dlg.setMessage("네트워크 연결을 확인해주세요");
            dlg.setIcon(R.drawable.dust);
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return;
                }
            });
            dlg.show();
        }

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // 이미 로그인 되어 있을 때
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // xml
        loginID = (EditText) findViewById(R.id.login_id);
        loginPW = (EditText) findViewById(R.id.login_password);
        loginbtn = (Button) findViewById(R.id.login_button);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signUp = (Button) findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
                //회원가입 페이지로 이동.
            }
        });

        dialog = new ProgressDialog(this);




    }

    private void login(){
        String email = loginID.getText().toString().trim();
        String pw = loginPW.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage("로그인 중입니다");
        dialog.setCancelable(false);
        dialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().getEmail() == null){
                                AlertDialog.Builder dlg = new AlertDialog.Builder(Login.this);
                                dlg.setTitle("로그인 실패");
                                dlg.setMessage("다시 시도해주세요");
                                dlg.setIcon(R.drawable.dust);
                                dlg.setPositiveButton("확인", null);
                                dlg.setCancelable(false);
                                dlg.show();
                            }
                            else{
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            AlertDialog.Builder dlg = new AlertDialog.Builder(Login.this);
                            dlg.setTitle("로그인 실패");
                            dlg.setMessage("다시 시도해주세요");
                            dlg.setIcon(R.drawable.dust);
                            dlg.setPositiveButton("확인", null);
                            dlg.setCancelable(false);
                            dlg.show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed(){
        Toast finishToast = null;
        if(System.currentTimeMillis() - time > 2000){
            time = System.currentTimeMillis();
            finishToast = Toast.makeText(this, "한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
            finishToast.show();
            return;
        }
        if(System.currentTimeMillis() - time <= 2000){
            finishAffinity();
            finishToast.cancel();
        }
    }

}