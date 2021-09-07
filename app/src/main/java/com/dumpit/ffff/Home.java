package com.dumpit.ffff;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Home extends Fragment{
    ViewGroup viewGroup;
    Dialog dialog;
    TextView HowToUse;
    static final int SMS_RECEIVE_PERMISSON=1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewGroup = (ViewGroup) inflater.inflate(R.layout.home,container,false);

        dialog = new Dialog(this.getContext());       // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setContentView(R.layout.help_dialog);             // xml 레이아웃 파일과 연결

        HowToUse = (TextView) viewGroup.findViewById(R.id.howToUse);

        HowToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        // SMS 권한이 부여되어 있는지 확인
        int permissonCheck= ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.RECEIVE_SMS);
        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(getContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.RECEIVE_SMS)){
                Toast.makeText(getContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{ Manifest.permission.RECEIVE_SMS},       SMS_RECEIVE_PERMISSON);
            }else{
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{ Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }

        return viewGroup;
    }

}

