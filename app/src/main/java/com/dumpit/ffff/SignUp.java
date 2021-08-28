package com.dumpit.ffff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.SimpleDateFormat;

import static android.widget.Toast.LENGTH_SHORT;

public class SignUp extends AppCompatActivity {

    EditText signup_id;
    EditText signup_password;
    EditText signup_nickname;
    Button signup_button;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    int point = 0;
    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        signup_id = (EditText)findViewById(R.id.signup_id);
        signup_password = (EditText)findViewById(R.id.signup_password);
        signup_nickname = (EditText)findViewById(R.id.signup_nickname);
        signup_button = (Button)findViewById(R.id.signUp);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        progressDialog = new ProgressDialog(this);

    }

    private void reload(){

    }

    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    //이메일 , 이름, 포인트, useruid,
    private void createAccount(String email, String pw){
        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String email = signup_id.getText().toString().trim();
                    int index = email.indexOf("@");
                    String id = email.substring(0, index);
                    String web = email.substring(index+1);
                    int webidx = web.indexOf(".");
                    String website = web.substring(0, webidx);
                    Log.i("web : ", web);
                    String nickname = signup_nickname.getText().toString().trim();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = simpleDateFormat.format(System.currentTimeMillis());

                    Toast.makeText(SignUp.this, "가입 완료", LENGTH_SHORT).show();

                    FirebaseUser user = mAuth.getCurrentUser();
                    String useruid = user.getUid();

                    updateUI(user);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(SignUp.this);
                    dlg.setTitle("환영합니다");
                    dlg.setMessage("회원가입 완료");
                    dlg.setIcon(R.drawable.dust);
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference.child("users").child(id+"_"+website).child("email").setValue(email);
                            databaseReference.child("users").child(id+"_"+website).child("nickname").setValue(nickname);
                            databaseReference.child("users").child(id+"_"+website).child("useruid").setValue(useruid);
                            databaseReference.child("users").child(id+"_"+website).child("Totalpoint").setValue(point);
                            // 마켓상품 추가 (임시)
                            String[] list = {"BHC 치즈볼", "가나 초콜릿", "문화상품권 5000원", "바세린 50ml", "빼빼로 오리지널",
                                    "스타벅스 아이스 아메리카노 tall", "양키 캔들", "하늘보리"};
                            for(int j=0; j<list.length; j++)
                                databaseReference.child("users").child(id+"_"+website).child("zzim").child(list[j]).setValue(false);

                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                        }
                    });
                    dlg.setCancelable(false);
                    dlg.show();
                }
                else{
                    progressDialog.cancel();
                    Toast.makeText(SignUp.this, "회원등록 실패", LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void registerUser(){
        String email = signup_id.getText().toString().trim();
        String pw = signup_password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "이메일을 입력해주세요", LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "비밀번호를 입력해주세요", LENGTH_SHORT).show();
            return;
        }

        int check = -1;
        check = email.indexOf("@");
        if(check == -1){
            Toast.makeText(this, "이메일 형식으로 입력해주세요", LENGTH_SHORT).show();
            return;
        }
        else{
            progressDialog.setMessage("등록중입니다 조금만 기다려주세요");
            progressDialog.setCancelable(false);
            progressDialog.show();

            createAccount(email, pw);
        }
    }

    private void updateUI(FirebaseUser user){

    }
}