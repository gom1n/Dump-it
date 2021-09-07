package com.dumpit.ffff;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
    PointAdapter adapter;
    ArrayList<PointData> pointList = new ArrayList<PointData>();

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

        adapter = new PointAdapter(this, pointList);
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

                   pointList.add(new PointData(date, point));
               }
               adapter.notifyDataSetChanged();
//               listView.setSelection(adapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}

// 포인트적립 데이터 담기
class PointData {
    String time;
    String pointData;

    // Generate > Constructor
    public PointData() {}
    public PointData(String time, String pointData) {
        this.time = time;
        this.pointData = pointData;
    }

    // Generate > Getter and Setter
    public String getTime() {
        return this.time;
    }
    public String getPointData() { return this.pointData; }


    // Generate > toString() : 아이템을 문자열로 출력
    @Override
    public String toString() {
        return "PointData{" +
                "time='" + time + '\'' +
                ", pointData='" + pointData + '\'' +
                '}';
    }
}
class PointAdapter extends BaseAdapter {
    private ArrayList<PointData> items = new ArrayList<PointData>();
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;

    TextView pointTime;
    TextView pointD;

    public PointAdapter() {}
    public PointAdapter(Context context, ArrayList<PointData> dataArray) {
        mContext = context;
        items = dataArray;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public PointData getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.point_list_custom, null);

        pointTime = (TextView) view.findViewById(R.id.PointTime);
        pointTime.setText(items.get(position).getTime());
        pointD = (TextView) view.findViewById(R.id.pointD);
        pointD.setText(items.get(position).getPointData());

        return view;
    }

}
