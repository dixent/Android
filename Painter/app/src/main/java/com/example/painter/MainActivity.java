package com.example.painter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private PaintView paintView;
    private static final Map<String, Integer> requestCodes = new HashMap<String, Integer>()
    {
        {
            put("open_image_error", 0);
            put("open_image", 1);
            put("camera_error", 2);
            put("make_photo", 3);
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = findViewById(R.id.paint_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                break;
            case R.id.blur:
                paintView.blur();
                break;
  //          case R.id.fill:
    //            paintView.setDrawer("fill");
      //          return true;
            case R.id.fill_figure:
                 paintView.fillFigure = !paintView.fillFigure;
                 break;
            case R.id.line:
                paintView.setDrawer("line");
                break;
            case R.id.straight_line:
                paintView.setDrawer("line");
                paintView.setStraight();
                break;
            case R.id.rectangle:
                paintView.setDrawer("rectangle");
                break;
            case R.id.square:
                paintView.setDrawer("rectangle");
                paintView.setStraight();
                break;
            case R.id.oval:
                paintView.setDrawer("oval");
                break;
            case R.id.circle:
                paintView.setDrawer("oval");
                paintView.setStraight();
                break;
            case R.id.open_image:
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            requestCodes.get("open_image_error"));
                } else {
                    openImage();
                }
                return true;
            case R.id.make_photo:
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CAMERA},
                            requestCodes.get("camera_error"));
                } else {
                    makePhoto();
                }
                break;
            case R.id.clear:
                paintView.clear();
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.permission_storage_failure),
                            Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            case 2: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoto();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.permission_camera_failure),
                            Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        paintView.setBitmapBackground(getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case 3:
                if (resultCode == RESULT_OK) {
                    paintView.setBitmapBackground((Bitmap) data.getExtras().get("data"));
                }
        }
    }

    private void openImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestCodes.get("open_image"));
    }

    private void makePhoto() {
        Intent takePictureIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, requestCodes.get("make_photo"));
        } catch (ActivityNotFoundException e) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    requestCodes.get("camera_error"));
        }
    }
}