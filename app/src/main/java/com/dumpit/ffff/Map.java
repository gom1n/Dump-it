package com.dumpit.ffff;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.MapView;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;


public class Map extends Fragment {
    MapView sView = null;
    SQLiteDatabase db;
    DatabaseHelper dh;
    ArrayList<PlaceData> mapList = new ArrayList<PlaceData>();
    PlaceAdapter placeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map , container, false);

        ListView listView = (ListView) view.findViewById(R.id.mapList);
        placeAdapter = new PlaceAdapter(this.getContext(), mapList);
        listView.setAdapter(placeAdapter);

        readExcel();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                PlaceData place = (PlaceData) a_parent.getAdapter().getItem(a_position);
                Intent intent = new Intent(getContext(), seeMap.class);
                intent.putExtra("placeName", place.getPlaceName());
                intent.putExtra("placeAddress", place.getPlaceAddress());
                intent.putExtra("placeTel", place.getPlaceTel());
                startActivity(intent);

            }
        });


//        Button b1 = (Button) view.findViewById(R.id.m1);
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getChildFragmentManager().beginTransaction().replace(R.id.s_map, new Map1()).commit();
//            }
//        });
//        Button b2 = (Button) view.findViewById(R.id.m2);
//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getChildFragmentManager().beginTransaction().replace(R.id.s_map, new Map2()).commit();
//            }
//        });
//        Button b3 = (Button) view.findViewById(R.id.m3);
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getChildFragmentManager().beginTransaction().replace(R.id.s_map, new Map3()).commit();
//            }
//        });

        return view;
    }
    public void readExcel() {
        try {
            //파일읽기
            InputStream is = getActivity().getBaseContext().getResources().getAssets().open("incheon_medicine.xls");

            //엑셀파일
            Workbook wb = Workbook.getWorkbook(is);

            //엑셀파일이 있다면
            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    StringBuilder sb;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        sb = new StringBuilder();
                        String placeName="";
                        String placeAddress="";
                        String placeTel="";
                        //col: 컬럼순서, contents: 데이터값
                        for(int col=0; col < colTotal; col++) {
                            if(col==1) placeName = sheet.getCell(col, row).getContents();
                            if(col==3) placeAddress = sheet.getCell(col, row).getContents();
                            if(col==4) placeTel = sheet.getCell(col, row).getContents();
                        }
                        mapList.add(new PlaceData(placeName, placeAddress, placeTel));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

class PlaceData{
    private String placeName;
    private String placeAddress;
    private String placeTel;

    public PlaceData(){

    }

    public PlaceData(String placeName, String placeAddress, String placeTel){
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeTel = placeTel;
    }
    public String getPlaceName() {
        return this.placeName;
    }
    public String getPlaceAddress(){
        return this.placeAddress;
    }
    public String getPlaceTel(){
        return this.placeTel;
    }

}



class PlaceAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    private ArrayList<PlaceData> data;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private TextView placeTelTextView;


    public PlaceAdapter() {}
    public PlaceAdapter(Context context, ArrayList<PlaceData> dataArray) {
        mContext = context;
        data = dataArray;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public PlaceData getItem(int position) {
        return data.get(position);
    }


    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.map_listview_custom, null);

        placeNameTextView = (TextView) view.findViewById(R.id.place);
        placeNameTextView.setText(data.get(position).getPlaceName());
        placeAddressTextView = (TextView) view.findViewById(R.id.address);
        placeAddressTextView.setText(data.get(position).getPlaceAddress());
        placeTelTextView = (TextView) view.findViewById(R.id.tel);
        placeTelTextView.setText(data.get(position).getPlaceTel());

        return view;
    }

}