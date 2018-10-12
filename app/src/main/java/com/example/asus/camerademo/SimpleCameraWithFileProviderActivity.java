package com.example.asus.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleCameraWithFileProviderActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView image_view_simple_camera_file_provider_id;
    private Button btn_simple_camera_file_provider_id;

    private String photoFileName = "photo.jpg";
    private final String APP_TAG = "MyCustomApp";

    private static final int REQUEST_CODE=1;
    private Uri uri=null;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_camera_with_file_provider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image_view_simple_camera_file_provider_id=(ImageView) findViewById(R.id.image_view_simple_camera_file_provider_id);
        btn_simple_camera_file_provider_id=(Button) findViewById(R.id.btn_simple_camera_file_provider_id);

        btn_simple_camera_file_provider_id.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_simple_camera_file_provider_id:
                launchCamera();
                break;
        }
    }


    private void launchCamera()
    {
        if(Utility.isExternalStorageAvailable()) {

            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileURI());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
        else
        {
            Toast.makeText(SimpleCameraWithFileProviderActivity.this, "Need Permission for External Directory", Toast.LENGTH_SHORT).show();
        }

    }


    private Uri getPhotoFileURI()
    {
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyMMddHHmmssZ");
        Date currentDate=new Date();
        String fileName=simpleDateFormat.format(currentDate)+"_"+photoFileName;


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            uri=Utility.getExternalFilesDirForVersion24Above(SimpleCameraWithFileProviderActivity.this,Environment.DIRECTORY_PICTURES,APP_TAG,fileName);
        }
        else
        {
            uri=Utility.getExternalFilesDirForVersion24Below(SimpleCameraWithFileProviderActivity.this, Environment.DIRECTORY_PICTURES, APP_TAG, fileName);
        }
        return uri;
    }


    private void setImage(Intent data)
    {
        InputStream inputStream=null;

        try{
            if(bitmap!=null)
            {
                bitmap.recycle();
            }

            //inputStream=getContentResolver().openInputStream(data.getData());
            //bitmap=BitmapFactory.decodeFile(uri.toString());
            //bitmap= BitmapFactory.decodeFile(getPhotoFileURI(photoFileName).getPath());
            image_view_simple_camera_file_provider_id.setImageURI(uri);

        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("debug","File not found");
        }
        finally {
            try
            {
                if(inputStream!=null)
                {
                    inputStream.close();
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    setImage(data);
                } else {
                    Toast.makeText(SimpleCameraWithFileProviderActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }
}

