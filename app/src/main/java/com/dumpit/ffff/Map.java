package com.dumpit.ffff;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
    ArrayList<PlaceData> mapList = new ArrayList<PlaceData>();
    private ArrayList<PlaceData> arraylist = new ArrayList<PlaceData>();
    PlaceAdapter placeAdapter;

    TextView placeNum;
    EditText placeSearch;
    Spinner spinner;
    Spinner spinner2;

    String what;
    String where;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map , container, false);

        ListView listView = (ListView) view.findViewById(R.id.mapList);
        placeAdapter = new PlaceAdapter(this.getContext(), mapList);
        listView.setAdapter(placeAdapter);

        placeNum = (TextView) view.findViewById(R.id.placeNum);
        placeSearch = (EditText) view.findViewById(R.id.placeSearch);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner2 = (Spinner) view.findViewById(R.id.spinner2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                what = parent.getItemAtPosition(position).toString();
                if(what.equals("선택")) {
                    placeNum.setText("0");
                    mapList.clear();
                    placeAdapter.notifyDataSetChanged();
                }
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        where = parent.getItemAtPosition(position).toString();
                        if(where.equals("선택")) {
                            placeNum.setText("0");
                            mapList.clear();
                            placeAdapter.notifyDataSetChanged();
                        }
                        if(what.equals("폐의약품")) {
                            if(where.equals("인천 강화군")) {
                                mapList.clear();
                                readExcel("incheon_medicine");
                            }else if(where.equals("광주광역시")) {
                                mapList.clear();
                                readExcel("kwangju_medicine");
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




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
        arraylist.addAll(mapList);
        placeSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }
            @Override
            public void afterTextChanged(Editable editable){
                String text = placeSearch.getText().toString();
                search(text);
            }
        });
        return view;
    }
    public void search(String charText){
        mapList.clear();
        if(charText.length() == 0){
            mapList.clear();
            mapList.addAll(arraylist);
        }
        else{
            for(int i = 0; i < arraylist.size(); i++){
                if(arraylist.get(i).getPlaceName().toLowerCase().contains(charText)){
                    mapList.add(arraylist.get(i));
                }
            }
        }
        placeAdapter.notifyDataSetChanged();
    }
    public void readExcel(String filename) {
        try {
            //파일읽기
            InputStream is = getActivity().getBaseContext().getResources().getAssets().open(filename+".xls");

            //엑셀파일
            Workbook wb = Workbook.getWorkbook(is);

            //엑셀파일이 있다면
            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    int count = 0;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        String placeName="";
                        String placeAddress="";
                        String placeTel="";
                        //col: 컬럼순서, contents: 데이터값
                        for(int col=0; col < colTotal; col++) {
                            if(col==0) placeName = sheet.getCell(col, row).getContents();
                            if(col==1) placeAddress = sheet.getCell(col, row).getContents();
                            if(col==2) placeTel = sheet.getCell(col, row).getContents();
                            if(sheet.getCell(col, row).getContents() == null) continue;
                        }
                        mapList.add(new PlaceData(placeName, placeAddress, placeTel));
                        count++;
                    }
                    placeAdapter.notifyDataSetChanged();
                    placeNum.setText(count+"");
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