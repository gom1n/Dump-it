package com.dumpit.ffff;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NoticePage extends AppCompatActivity {

    private String htmlPageUrl;
    private String titleOfNotice;
    private TextView noticeTitle;
    private TextView noticeContent;
    private ArrayList<String> notices = new ArrayList<String>();
    private String showNotice = "";
    private TextView goToBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_page);

        noticeTitle = (TextView)findViewById(R.id.noticeTitle);
        noticeContent = (TextView)findViewById(R.id.noticeContent);

        Intent intent = getIntent();
        htmlPageUrl = intent.getStringExtra("url");
        titleOfNotice = intent.getStringExtra("title");

        goToBlog = (TextView)findViewById(R.id.noticeUrl);
        goToBlog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goBlogNotice = new Intent(Intent.ACTION_VIEW, Uri.parse(htmlPageUrl));
                startActivity(goBlogNotice);
            }
        });

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params){
            try{
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements contents = doc.select("div.post div.post-body.entry-content.float-container p");
                noticeTitle.setText(titleOfNotice);

                for(Element content : contents){
                    notices.add(content.text());
                    showNotice += content.text().trim() + "\n";
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result){
            noticeContent.setText(showNotice);
        }
    }


}