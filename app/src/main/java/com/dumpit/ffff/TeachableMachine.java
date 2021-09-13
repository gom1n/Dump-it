package com.dumpit.ffff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dumpit.ffff.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TeachableMachine extends AppCompatActivity {

    Button selectBtn;
    Button predictBtn;
    ImageView imgView;
    private Bitmap img;
    TextView tv;
    private ArrayList<String> result;
    TextView getResult;
    Button getPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachable_machine);

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
        getPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 포인트 적립 코드 (+)

                // 자동으로 돌아가기
                /*Fragment fragment = new MyPage();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view_tag, fragment, fragment.getClass().getSimpleName())
                        .commit();*/
                finish();
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