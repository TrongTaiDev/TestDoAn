package com.dev.trongtai.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.dnn.Importer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.opencv.imgproc.Imgproc.findContours;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    private static final int SELECT_PHOTO = 100;
    private ImageView imageView;
    private Button btnChonAnh;
    private Button btnTest;
    private Button btnStep2;
    private Button btnStep3;
    Bitmap myImage;
    Mat img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) this.findViewById(R.id.imageView);
        btnChonAnh = (Button) this.findViewById(R.id.btnChonAnh);
        btnTest = (Button) this.findViewById(R.id.btnConvert);
        btnStep2 = (Button) this.findViewById(R.id.btnStep2);
        btnStep3 = (Button) this.findViewById(R.id.btnStep3);

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img = new Mat();
                Utils.bitmapToMat(myImage, img);
                Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
                Utils.matToBitmap(img, myImage);
                imageView.setImageBitmap(myImage);
            }
        });

        btnStep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Size (số hàng, số cột) bài trắc nghiệm
                Imgproc.GaussianBlur(img, img, new Size(5, 5), 0);
                Utils.matToBitmap(img, myImage);
                imageView.setImageBitmap(myImage);
            }
        });

        btnStep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Imgproc.Canny(img, img, 75, 200);
                Utils.matToBitmap(img, myImage);
                imageView.setImageBitmap(myImage);

                //Code
                ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                hierarchy.release();
                Utils.matToBitmap(hierarchy, myImage);
                imageView.setImageBitmap(myImage);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SELECT_PHOTO:{
                InputStream imageStream = null;
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    myImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(myImage);
                }
            }
        }
    }

    private void quetAnh(String duongDanhAnh){
        Mat img = Imgcodecs.imread(duongDanhAnh);
        if(img.empty()){
            Log.i("Đọc ảnh:", "Không thành công");
        }
        else {
            Log.i("Đọc ảnh:", "Thành công");
            Toast.makeText(getApplicationContext(), "Đọc thành công", Toast.LENGTH_SHORT).show();
        }
    }
}
