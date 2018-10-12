package com.example.asus.camerademo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.media.MediaRecorder.VideoSource.CAMERA;


public class CameraActivityWithSurfaceViewAndCameraAPI extends AppCompatActivity implements View.OnClickListener{

    Button btn_surface_view_id,btn_restart_surface_view_id,btn_galley;
    ImageView image_view_surface_view_id;
    TextView txt_view_surface_view_id,cross;
    RelativeLayout relative_layout_for_surface_view_id;
    FrameLayout camera_preview_layout_id;

    private Uri uri;
    private File file;
    private Camera mCamera;
    private static int mCameraID;
    private CameraPreview cameraPreview;

    private static final String APP_TAG="MyCustomApp";
    private static final String WATERMARK_FILE_NAME="Watermark.jpeg";
    private static final String DEBUG_TAG="debug_tag";
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_activity_with_surface_view_and_camera_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        relative_layout_for_surface_view_id=(RelativeLayout) findViewById(R.id.relative_layout_for_surface_view_id);
        camera_preview_layout_id=(FrameLayout) findViewById(R.id.camera_preview_layout_id);
        btn_surface_view_id=(Button) findViewById(R.id.btn_surface_view_id);
        btn_restart_surface_view_id=(Button) findViewById(R.id.btn_restart_surface_view_id);
        txt_view_surface_view_id=(TextView) findViewById(R.id.txt_view_surface_view_id);

        cross=(TextView) findViewById(R.id.cross);

        btn_galley=(Button) findViewById(R.id.btn_galley);
        btn_surface_view_id.setOnClickListener(this);
        btn_restart_surface_view_id.setOnClickListener(this);
        image_view_surface_view_id=(ImageView) findViewById(R.id.image_view_surface_view_id);


        btn_galley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallary();

            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                finishAffinity();
            }
        });


        mCameraID = findFrontFacingCamera();

        if(mCameraID!=-1)
        {
            mCamera=getCameraInstance(mCameraID);
            if(mCamera!=null) {
                cameraPreview = new CameraPreview(CameraActivityWithSurfaceViewAndCameraAPI.this, mCamera);
                camera_preview_layout_id.addView(cameraPreview);
            }
        }
    }

    private static Camera getCameraInstance(int cameraID)
    {
        Camera camera=null;
        try
        {
            camera=Camera.open(cameraID);
        }catch (Exception e)
        {
            Log.d(DEBUG_TAG,"Camera Not Opened");
        }
        return camera;
    }


    private int findFrontFacingCamera()
    {
        int cameraID=-1;
        int noOfCameras=0;

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            noOfCameras= Camera.getNumberOfCameras();
            for(int i=0;i<noOfCameras;i++)
            {
                CameraInfo cameraInfo=new CameraInfo();
                Camera.getCameraInfo(i,cameraInfo);

                if(cameraInfo.facing==CameraInfo.CAMERA_FACING_BACK)
                {
                    cameraID=i;
                    break;
                }
            }
        }
        else
        {
            Log.d(DEBUG_TAG,"Camera Feature not available");
        }

        return cameraID;
    }


    private PictureCallback mPictureCallBack= new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) {
                Toast.makeText(CameraActivityWithSurfaceViewAndCameraAPI.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }

            image_view_surface_view_id.setImageBitmap(scaleDownBitmapImage(bitmap, 300, 200));
            txt_view_surface_view_id.setVisibility(View.VISIBLE);

            File f=getBitmapFile();
            loadBitmapFromView(relative_layout_for_surface_view_id, f);
            btn_restart_surface_view_id.setVisibility(View.VISIBLE);
        }

    };


    private void restartSurfaceView()
    {
        mCamera=getCameraInstance(mCameraID);
        if(mCamera!=null) {
            camera_preview_layout_id.removeAllViews();
            image_view_surface_view_id.setImageBitmap(null);
            cameraPreview = new CameraPreview(CameraActivityWithSurfaceViewAndCameraAPI.this, mCamera);
            camera_preview_layout_id.addView(cameraPreview);
        }
    }


    private void loadBitmapFromView(final View v,final File file)
    {
        if(file!=null)
        {
            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                                  @Override
                                                                  public void onGlobalLayout() {
                                                                      Bitmap bitmap=null;
                                                                      FileOutputStream fout=null;
                                                                      try {
                                                                          v.setDrawingCacheEnabled(true);
                                                                          v.buildDrawingCache();
                                                                          v.setBackgroundColor(Color.WHITE);
                                                                          bitmap = Bitmap.createBitmap(v.getDrawingCache());
                                                                          fout = new FileOutputStream(file);
                                                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);

                                                                      } catch (FileNotFoundException e) {
                                                                          e.printStackTrace();
                                                                      } catch (Exception e)
                                                                      {
                                                                          e.printStackTrace();
                                                                      }finally {
                                                                          if(fout!=null)
                                                                          {
                                                                              try {
                                                                                  fout.close();
                                                                                  bitmap.recycle();
                                                                              }catch (IOException e)
                                                                              {
                                                                                  e.printStackTrace();
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                              }
            );
        }
        else
        {
            Log.d(DEBUG_TAG, "No File Passed. Cannot Save Image");
        }
    }


    private File getBitmapFile()
    {
        boolean done=false;
        File file=null;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            done=true;
        }

        if(done)
        {
            try
            {
                File mediaStorageDir= new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),APP_TAG);

                if(!mediaStorageDir.exists() && !mediaStorageDir.mkdir())
                {
                    Log.d(DEBUG_TAG,"Not able to create directory");
                }

                if(mediaStorageDir!=null)
                {
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyMMddHHmmssZ");
                    Date date= new Date();
                    file= new File(mediaStorageDir.getPath()+File.separator+simpleDateFormat.format(date)+"_"+WATERMARK_FILE_NAME);
                    Log.d(DEBUG_TAG,file.getAbsolutePath());
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return file;
    }

    private void setImage(Uri uri)
    {
        txt_view_surface_view_id.setVisibility(View.VISIBLE);
        image_view_surface_view_id.setImageURI(uri);
    }

    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_surface_view_id:


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Handler hadHandler=new Handler();
                    hadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(cameraPreview!=null)
                            {
                                mCamera.takePicture(null,null,mPictureCallBack);
                                btn_surface_view_id.setVisibility(View.GONE);
                            }
                        }
                    },3000);
                }else  {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                }


                break;

            case R.id.btn_restart_surface_view_id:

                restartSurfaceView();
                btn_surface_view_id.setVisibility(View.VISIBLE);
                btn_restart_surface_view_id.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onPause() {
        if(mCamera!=null)
        {
            mCamera.release();
            mCamera=null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mCamera==null)
        {
            if(mCameraID!=-1)
            {
                mCamera=getCameraInstance(mCameraID);
                if(mCamera!=null) {
                    camera_preview_layout_id.removeAllViews();
                    cameraPreview = new CameraPreview(CameraActivityWithSurfaceViewAndCameraAPI.this, mCamera);
                    camera_preview_layout_id.addView(cameraPreview);
                }
            }
        }

    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                   // Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();*/
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
