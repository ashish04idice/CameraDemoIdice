package com.example.asus.camerademo;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.hardware.Camera.CameraInfo;

public class CameraActivityWithCameraAPI extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG=CameraActivityWithCameraAPI.class.getSimpleName();
    public static final String MEDIA_FOLDER_NAME="CameraAPIDemo";
    public static final String DEBUG_TAG="debug_TAG";


    private PhotoHandler photoHandler;
    ImageView image_view_camera_api_id;
    Button btn_camera_api_id;

    private Camera camera;
    private int camera_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity_with_camera_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image_view_camera_api_id = (ImageView) findViewById(R.id.image_view_camera_api_id);
        btn_camera_api_id = (Button) findViewById(R.id.btn_camera_api_id);

        btn_camera_api_id.setOnClickListener(CameraActivityWithCameraAPI.this);

        camera_id = findFrontFacingCamera();

        if (camera_id < 0) {
            Toast.makeText(CameraActivityWithCameraAPI.this, "No Front Facing Camera Found", Toast.LENGTH_SHORT).show();
        } else {
         camera=Camera.open(camera_id);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_camera_api_id:
                setImage();
                break;
        }
    }


    private void setImage()
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
                try
                {
                    camera.startPreview();

                    photoHandler= new PhotoHandler(CameraActivityWithCameraAPI.this);

                    camera.takePicture(null, null, photoHandler);
                    photoHandler.setOnPhotoSavedListener(new PhotoHandler.OnPhotoSavedListener() {
                        @Override
                        public void getSavedPhoto(Uri photoUri) {
                            image_view_camera_api_id.setImageURI(photoUri);
                        }
                    });

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

        }
        else
        {
            Toast.makeText(CameraActivityWithCameraAPI.this, "External Card is not Mounted", Toast.LENGTH_SHORT).show();
        }
    }

    private int findFrontFacingCamera()
    {
        boolean done=true;
        int cameraID=-1;

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            done=false;
        }

        if(done)
        {

            int noOfCameras=Camera.getNumberOfCameras();

            for(int i=0;i<noOfCameras;i++)
            {
                CameraInfo cameraInfo= new CameraInfo();
                Camera.getCameraInfo(i,cameraInfo);

                if(cameraInfo.facing==CameraInfo.CAMERA_FACING_FRONT)
                {
                    cameraID=i;
                    break;
                }

            }
        }

        return cameraID;
    }

    @Override
    protected void onPause() {
        if(camera!=null)
        {
            camera.release();
            camera=null;
        }
        super.onPause();
    }

}
