package com.dumpit.ffff;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ConsentRegister extends AppCompatActivity {

    TextView consentContent;
    TextView goToBlog;
    Switch consentSwitch;
    Button checking;
    TextView When;

    ArrayList<String> contents = new ArrayList<>();
    String CC = "";
    String url = "https://dumpit2021.blogspot.com/2021/06/blog-post.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_register);

        consentContent = (TextView)findViewById(R.id.consentContent);
        consentSwitch = (Switch)findViewById(R.id.consentSwitch);
        checking = (Button)findViewById(R.id.checking);
        checking.setEnabled(false);
        checking.setBackgroundColor(Color.parseColor("#D5D5D5"));
        goToBlog = (TextView)findViewById(R.id.goToBlog);

        SpannableString content = new SpannableString("블로그에서의 확인이 필요하시다면 이곳을 클릭해주세요");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        goToBlog.setText(content);

        goToBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        consentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked == true) {
                    checking.setEnabled(true);
                    checking.setBackgroundColor(Color.parseColor("#FBE7FF"));
                }
                else {
                    checking.setEnabled(false);
                    checking.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
            }
        });

        checking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        //crawling
        final Bundle bundle = new Bundle();
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements e_contents = doc.select("#post-body-6897112915313532619 p");
                    for(Element buff : e_contents){
                        String save = buff.text();
                        contents.add(save);
                    }
                    for(String strBuff : contents){
                        CC += strBuff + "\n";
                    }
                    bundle.putString("content", CC);
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            consentContent.setText(bundle.getString("content"));
        }
    };
}