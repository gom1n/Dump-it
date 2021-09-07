package com.dumpit.ffff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class seeBarcode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_barcode);

        ImageView seeBarcode = (ImageView) findViewById(R.id.seeBarcode);

        Intent intent = getIntent();
        String url = intent.getStringExtra("imageURI");
        Glide.with(seeBarcode.this).load(url)
                .error(R.drawable.loading)
                .into(seeBarcode);
    }
}
