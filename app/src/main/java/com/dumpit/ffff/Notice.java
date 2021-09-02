package com.dumpit.ffff;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Notice extends AppCompatActivity {

    private String htmlPageUrl = "https://dumpit2021.blogspot.com/search/label/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD";
    private SwipeRefreshLayout refresh;

    private ArrayList<String> noticeCrawlingArrayList = new ArrayList<String>();
    private ArrayList<String> noticeUrlArrayList = new ArrayList<String>();

    private ListView noticeCrawlingList;

    private ArrayAdapter<String> noticeAdapter;
    private String noticeUrl;
    private String noticeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        refresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noticeCrawlingArrayList.clear();
                noticeUrlArrayList.clear();
                refresh.setRefreshing(false);
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        noticeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noticeCrawlingArrayList);

        noticeCrawlingList = (ListView)findViewById(R.id.noticeCrawlingList);
        noticeCrawlingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                noticeUrl = noticeUrlArrayList.get((int)id);
                noticeTitle = noticeCrawlingArrayList.get((int)id);

                Intent intent = new Intent(getApplicationContext(), NoticePage.class);
                intent.putExtra("url", noticeUrl);
                intent.putExtra("title", noticeTitle);
                startActivity(intent);
            }
        });
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

                Elements titles = doc.select("div.blog-posts.hfeed.container article.post-outer-container div.post-outer div.post h3.post-title.entry-title a");
                System.out.println("----------------------------------------");
                for(Element e : titles){
                    noticeCrawlingArrayList.add(e.text());
                    noticeUrlArrayList.add(e.attr("href"));
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Void result){
            noticeCrawlingList.setAdapter(noticeAdapter);
        }
    }
}