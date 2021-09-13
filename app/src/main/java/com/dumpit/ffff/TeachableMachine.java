package com.dumpit.ffff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpit.ffff.ml.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TeachableMachine extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button selectBtn;
    Button predictBtn;
    ImageView imgView;
    private Bitmap img;
    TextView tv;
    int points = 0;
    private ArrayList<String> result;
    TextView getResult;
    Button getPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachable_machine);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        result = new ArrayList<String>();
        result.add("종류1");
        result.add("종류2");
        result.add("종류3");

        imgView = (ImageView)findViewById(R.id.imgView);
        tv = (TextView)findViewById(R.id.tv);
        tv.setText("이미지를 선택해주세요");
        selectBtn = (Button)findViewById(R.id.selectBtn);
        predictBtn = (Button)findViewById(R.id.predictBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv.setText("");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });
        getResult = (TextView)findViewById(R.id.getResult);
        getPoint = (Button)findViewById(R.id.getPoint);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                getPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File photoFile = null;
                        String email = user.getEmail();
                        int index = email.indexOf("@");
                        String id = email.substring(0, index);
                        String web = email.substring(index + 1);
                        int webidx = web.indexOf(".");
                        String website = web.substring(0, webidx);

                        if (photoFile != null) {
                            Toast.makeText(getApplicationContext(), "카메라 촬영을 원하시면 설정->어플리케이션->dumpit에 들어가서\n 카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = simpleDateFormat.format(System.currentTimeMillis());

                        points = snapshot.child("users").child(id + "_" + website).child("Totalpoint").getValue(Integer.class) + 50;
                        databaseReference.child("users").child(id + "_" + website).child("point").child(time).setValue("폐의약품 1p");
                        databaseReference.child("users").child(id + "_" + website).child("Totalpoint").setValue(points);
                        Toast.makeText(getApplicationContext(), "50p 적립!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img = Bitmap.createScaledBitmap(img, 224, 224, true);

                try {
                    Model model = Model.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                    System.out.println("result : " + inputFeature0.getShape());
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    //// The size of byte buffer and the shape do not match 에러 발생.
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    System.out.println();

                    //tv.setText("paper : " + outputFeature0.getFloatArray()[0] + "\nplastic : " + outputFeature0.getFloatArray()[1] + "\ntrash: " + outputFeature0.getFloatArray()[2]);

                    Float feat = new Float(0.0);
                    String resultFeat = result.get(0);
                    feat = outputFeature0.getFloatArray()[0];
                    for(int i = 0; i < 3; i++){
                        if(feat < outputFeature0.getFloatArray()[i]){
                            feat = outputFeature0.getFloatArray()[i];
                            resultFeat = result.get(i);
                            System.out.println("result : " + resultFeat);
                            getResult.setText(resultFeat + " (" + feat + ")");
                            if(resultFeat.equals("종류2")){
                                getPoint.setEnabled(true);
                            }
                        }
                    }

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            imgView.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}