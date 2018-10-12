package com.example.asus.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SimpleCameraActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView image_view_simple_camera_id;
    Button btn_simple_camera_id;
    private static final int REQUEST_CODE=1;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image_view_simple_camera_id=(ImageView)findViewById(R.id.image_view_simple_camera_id);
        btn_simple_camera_id=(Button) findViewById(R.id.btn_simple_camera_id);
        btn_simple_camera_id.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_simple_camera_id:
                Intent intent=new Intent();

                /* Enable this if you don't want to click photo using camera, but instead want to pick already saved pics from Gallery or File Explorer
                //READ_EXTERNAL_STORAGE permission is required
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                */

                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, REQUEST_CODE);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE && resultCode== Activity.RESULT_OK)
        {
            setImage(data);
        }

    }

    private void setImage(Intent data)
    {
        InputStream inputStream=null;
        try {

            if(bitmap!=null)
            {
                bitmap.recycle();
            }

            inputStream = getContentResolver().openInputStream(data.getData());
            bitmap= BitmapFactory.decodeStream(inputStream);

            /* Enable this if you want to get Bitmap without accessing the URI as shown above. In this case READ_EXTERNAL_STORAGE permission is not required.
            bitmap=(Bitmap) data.getExtras().get("data");
            */

            image_view_simple_camera_id.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        };
    }

}
