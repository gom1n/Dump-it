package com.dumpit.ffff;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;


public class seeMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mgoogleMap;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seemap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.see_map);
        mapFragment.getMapAsync(this);

        dialog = new Dialog(seeMap.this);       // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setContentView(R.layout.custom_map_dialog);             // xml 레이아웃 파일과 연결
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        Context context = this.getApplicationContext();

        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        String placeAddress = intent.getStringExtra("placeAddress");
        String placeTel = intent.getStringExtra("placeTel");

        Location place = addrToPoint(context, placeName);
        LatLng PLACE = new LatLng(place.getLatitude(), place.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(PLACE);
        markerOptions.title(placeName);
        markerOptions.snippet(placeAddress);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.pill);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
                mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        }); // 구글맵 로딩이 완료되면 카메라 위치 조정
        googleMap.addMarker(markerOptions).showInfoWindow();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //정보창 클릭 리스너
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);
        //마커 클릭 리스너
        googleMap.setOnMarkerClickListener(markerClickListener);

    }
    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            showDialog();
        }
    };
    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            showDialog();
            return false;
        }
    };
    // dialog01을 디자인하는 함수
    public void showDialog(){
        dialog.show(); // 다이얼로그 띄우기

        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        String placeAddress = intent.getStringExtra("placeAddress");
        String placeTel = intent.getStringExtra("placeTel");

        TextView place_dialog = dialog.findViewById(R.id.place_dialog);
        place_dialog.setText(placeName);
        TextView address_dialog = dialog.findViewById(R.id.address_dialog);
        address_dialog.setText(placeAddress);
        TextView tel_dialog = dialog.findViewById(R.id.tel_dialog);
        tel_dialog.setText(placeTel);

        // 전화걸기 버튼
        TextView call = dialog.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+placeTel));
                startActivity(tt);
            }
        });
        // 엑스 버튼
        dialog.findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }
    // geocoding 주소 변환
    public static Location addrToPoint(Context context, String place) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(place,10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null) {
            for(int i = 0 ; i < addresses.size() ; i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }
    //이 메서드가 없으면 지도가 보이지 않음
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop () {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState (@Nullable Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}