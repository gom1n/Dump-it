package com.dumpit.ffff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class BuyList extends AppCompatActivity{


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private ListView listViews;
    BuyAdapter adapter;
    ArrayList<BuyItem> buylist = new ArrayList<BuyItem>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        String email = user.getEmail();
        int index = email.indexOf("@");
        String id = email.substring(0, index);
        String web = email.substring(index+1);
        int webidx = web.indexOf(".");
        String website = web.substring(0, webidx);


        DatabaseReference buy = databaseReference.child(id+"_"+website).child("marketHistory");

        adapter = new BuyAdapter(this, buylist);
        listViews = (ListView)findViewById(R.id.purchase_lists);
        listViews.setAdapter(adapter);


        buy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot snaps : snapshot.getChildren()) {
                    BuyItem buyitem = snaps.getValue(BuyItem.class);
                    buylist.add(buyitem);
                    adapter.notifyDataSetChanged();
                }
                listViews.setSelection(adapter.getCount()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 상품구매내역 클릭 시 물품 바코드 등장!
        listViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                BuyItem item = (BuyItem) a_parent.getAdapter().getItem(a_position);
                String url = item.getImageURI();
                Intent intent = new Intent(getApplicationContext(), seeBarcode.class);
                intent.putExtra("imageURI", url);
                System.out.println(url);
                startActivity(intent);
            }
        });

    }
}
// 구매아이템 데이터 담기
class BuyItem {
    String name;
    int price;
    int bmyPoint;
    String buyTime;
    String imageURI;

    // Generate > Constructor
    public BuyItem() {}
    public BuyItem(String name, int price, int BmyPoint, String buyTime, String imageURI) {
        this.name = name;
        this.price = price;
        this.bmyPoint = BmyPoint;
        this.buyTime = buyTime;
        this.imageURI = imageURI;
    }

    // Generate > Getter and Setter
    public String getName() {
        return this.name;
    }
    public int getPrice() { return this.price; }
    public int getBmyPoint() {
        return this.bmyPoint;
    }
    public String getBuyTime() {
        return this.buyTime;
    }
    public String getImageURI() {
        return this.imageURI;
    }


    // Generate > toString() : 아이템을 문자열로 출력
    @Override
    public String toString() {
        return "BuyItem{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", BmyPoint='" + bmyPoint + '\'' +
                ", BuyTime='" + buyTime + '\'' +
                ", imageURI='" + imageURI + '\'' +
                '}';
    }
}
class BuyAdapter extends BaseAdapter {
    private ArrayList<BuyItem> items = new ArrayList<BuyItem>();
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;

    TextView buyItemN;
    TextView buyItemP;
    TextView BmyPoint;
    TextView buyTime;

    public BuyAdapter() {}
    public BuyAdapter(Context context, ArrayList<BuyItem> dataArray) {
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
    public BuyItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.buylist_custom, null);

        buyItemN = (TextView) view.findViewById(R.id.buyitemN);
        buyItemN.setText(items.get(position).getName());
        buyItemP = (TextView) view.findViewById(R.id.buyitemP);
        buyItemP.setText(items.get(position).getPrice()+"");
        BmyPoint = (TextView) view.findViewById(R.id.BmyPoint);
        BmyPoint.setText(items.get(position).getBmyPoint()+"");
        buyTime = (TextView) view.findViewById(R.id.buytime);
        buyTime.setText(items.get(position).getBuyTime());

        return view;
    }

}
