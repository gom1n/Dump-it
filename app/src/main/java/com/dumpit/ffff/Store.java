package com.dumpit.ffff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class Store extends Fragment{
    ViewGroup viewGroup;
    MarketAdapter adapter;
    GridView gridView;
    ArrayList<MarketItem> marketItemArrayList;
    private ArrayList<MarketItem> arraylist;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseUser user;
    String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.store,container,false);

        // firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        InitializeData();

        // 어댑터 안에 데이터 담기
        adapter = new MarketAdapter(getActivity(), marketItemArrayList);
        gridView = (GridView) viewGroup.findViewById(R.id.gridView);
        // 리스트 뷰에 어댑터 설정
        gridView.setAdapter(adapter);

        // 이벤트 처리 리스너 설정
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MarketItem item = (MarketItem) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), MarketItemClick.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("imageURI", item.getUri());
                startActivity(intent);
            }
        });

        arraylist = new ArrayList<MarketItem>();
        arraylist.addAll(marketItemArrayList);
        // 검색기능 (앱 내)
        EditText searchView = (EditText) viewGroup.findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }
            @Override
            public void afterTextChanged(Editable editable){
                String text = searchView.getText().toString();
                search(text);
            }
        });

        return viewGroup;
    }

    public void search(String charText){
        marketItemArrayList.clear();
        if(charText.length() == 0){
            marketItemArrayList.clear();
            marketItemArrayList.addAll(arraylist);
        }
        else{
            for(int i = 0; i < arraylist.size(); i++){
                if(arraylist.get(i).getName().toLowerCase().contains(charText)){
                    marketItemArrayList.add(arraylist.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void InitializeData() {
        marketItemArrayList = new ArrayList<MarketItem>();

        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();


        // 마켓 아이템 추가
        mReference.child("MarketItems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String itemName = snapshot.getKey();
                    int itemPrice = snapshot.child("price").getValue(Integer.class);
                    String itemUri = snapshot.child("imageURI").getValue(String.class);
                    marketItemArrayList.add(new MarketItem(itemName, itemPrice, itemUri));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

// 마켓아이템 데이터 담기
class MarketItem {
    String name;
    int price;
    String uri;

    // Generate > Constructor
    public MarketItem(String name, int price,  String uri) {
        this.name = name;
        this.price = price;
        this.uri = uri;
    }

    // Generate > Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    // Generate > toString() : 아이템을 문자열로 출력
    @Override
    public String toString() {
        return "MarketItem{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", imageURI='" + uri + '\'' +
                '}';
    }
}

class MarketAdapter extends BaseAdapter {
    private ArrayList<MarketItem> items;
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;

    public MarketAdapter(Context context, ArrayList<MarketItem> dataArray) {
        mContext = context;
        items = dataArray;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
    // Generate > implement methods
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 뷰 객체 재사용
        MarketItemView view = null;
        if (convertView == null) {
            view = new MarketItemView(parent.getContext());
            // 윗줄에서 자꾸 오류남.. 원래는 view = new SingerItemView(getApplicationContext()); 였음
        } else {
            view = (MarketItemView) convertView;
        }

        MarketItem item = items.get(position);

        view.setName(item.getName());
        view.setPrice(item.getPrice());
        view.setImage(item.getUri());


        return view;
    }
}
class MarketItemView extends LinearLayout {

    TextView textView;
    TextView textView2;
    ImageView imageView;

    // Generate > Constructor
    public MarketItemView(Context context) {
        super(context);

        init(context);
    }

    public MarketItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    // singer_item.xml을 inflation
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.market_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public void setPrice(int price) {
        textView2.setText(price+"");
    }

    public void setImage(String uri) {
//        imageView.setImageResource(Uri a);
        Glide.with(getContext()).load(uri)
                .error(R.drawable.loading)
                .into(imageView);
    }

}

