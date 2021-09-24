package com.dumpit.ffff;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpit.ffff.ml.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;

public class CameraShot extends Fragment {
    ViewGroup viewGroup;

    Button selectBtn;
    Button predictBtn;
    ImageView imgView;
    private Bitmap img;
    TextView tv;
    int points = 0;
    private ArrayList<String> result;
    TextView getResult;
    Button getPoint;
    String section = "";
    int trashpoint = 0;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private static final int REQUEST_IMAGE_CAPTURE= 672;
    private String imageFilePath;
    private Uri photoUri;
    private AdView mAdview; //애드뷰 변수 선언
    private MediaScanner scanner; //사진 저장 후 갤러리에 변경사항 업데이트



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.camera, container, false);
        scanner = MediaScanner.getInstance(getContext());

      /**  public static Home newInstance() {
            return new Home();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_layout, home.newInstance()).commit();

       **/
        MobileAds.initialize(this.getContext(), new OnInitializationCompleteListener() { //광고 초기화
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdview = viewGroup.findViewById(R.id.adView); //배너광고 레이아웃 가져오기
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        AdView adView = new AdView(this.getContext());
        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
        adView.setAdUnitId("\n" + "ca-app-pub-5154428061719123/7769030105");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        result = new ArrayList<String>();
        result.add("일반");
        result.add("폐건전지");
        result.add("폐의약품");
        result.add("폐형광등");
        result.add("etc");


        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA);
        TedPermission.with(getContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage(" 카메라 촬영을 원하시면\n 설정->어플리케이션->dumpit에 들어가서\n 카메라 권한을 허용해주세요")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        viewGroup.findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {

                    }

                    if (photoFile != null) {
                        
                        photoUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                    }

                }
            }
        });


        imgView = viewGroup.findViewById(R.id.imgView);
        tv = viewGroup.findViewById(R.id.tv);
        tv.setText("이미지를 선택해주세요");
        selectBtn = viewGroup.findViewById(R.id.selectBtn);
        predictBtn = viewGroup.findViewById(R.id.predictBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        getResult = viewGroup.findViewById(R.id.getResult);
        getPoint = viewGroup.findViewById(R.id.getPoint);
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

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = simpleDateFormat.format(System.currentTimeMillis());

                        points = snapshot.child("users").child(id + "_" + website).child("Totalpoint").getValue(Integer.class) + trashpoint;
                        databaseReference.child("users").child(id + "_" + website).child("point").child(time).setValue(section + " " + trashpoint+ "p");
                        databaseReference.child("users").child(id + "_" + website).child("Totalpoint").setValue(points);
                        Toast.makeText(getContext().getApplicationContext(), trashpoint + "적립!", Toast.LENGTH_SHORT).show();
                        getPoint.setEnabled(false);

                        CameraShot camera;
                        camera = new CameraShot();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.main_layout, camera).commitAllowingStateLoss();
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
                    Model model = Model.newInstance(getContext().getApplicationContext());

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

                    //tv.setText("paper : " + outputFeature0.getFloatArray()[0] + "\nplastic : " + outputFeature0.getFloatArray()[1] + "\ntrash: " + outputFeature0.getFloatArray()[2]);

                    Float feat = new Float(0.0);
                    String resultFeat = result.get(0);
                    feat = outputFeature0.getFloatArray()[0];
                    for(int i = 0; i < 5; i++){
                        if(feat < outputFeature0.getFloatArray()[i]){
                            feat = outputFeature0.getFloatArray()[i];
                            resultFeat = result.get(i);
                            System.out.println("result : " + resultFeat);
                            getResult.setText(resultFeat);
                            //getResult.setText(resultFeat + " (" + feat + ")");
                            if(resultFeat.equals("폐건전지")){
                                getPoint.setEnabled(true);
                                section = "폐건전지";
                                trashpoint = 1;
                                break;
                            }
                            else if(resultFeat.equals("폐의약품")){
                                getPoint.setEnabled(true);
                                section = "폐의약품";
                                trashpoint = 3;
                                break;
                            }
                            else if(resultFeat.equals("폐형광등")){
                                getPoint.setEnabled(true);
                                section = "폐형광등";
                                trashpoint = 2;
                                break;
                            }
                        }
                        else{
                            getResult.setText("다른 이미지를 인식해주세요.");
                            getPoint.setEnabled(false);
                        }
                    }

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });


        return viewGroup;
    }


    private File createImageFile() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + time + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){


            if(data == null)  return;
            Uri uri = data.getData();
            imgView.setImageURI(uri);
            try {
                img = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);

            } else {
                exifDegree = 0;
            }

            String result = "";
            SimpleDateFormat dateformats = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            Date dates = new Date(System.currentTimeMillis());
            String filenames= dateformats.format(dates);

            String foldername = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "dumpit" + File.separator;

            File file = new File(foldername);
            if(!file.exists())
                file.mkdirs();

            File files = new File(foldername + "/" + filenames + ".png");
            result = files.getPath();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(files);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
                result = "Save Error fOut";
            }

            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                fOut.close();
                scanner.mediaScanning(foldername + "/" + filenames +".png");
            }catch (IOException e) {
                e.printStackTrace();
                result = "File close Error";
            }
        }
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "카메라 촬영을 원하시면 설정->어플리케이션->dumpit에 들어가서\n 카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
        }
    };



}