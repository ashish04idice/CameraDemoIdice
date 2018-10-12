package com.example.asus.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asus on 05-09-2017.
 */
public class PhotoHandler implements Camera.PictureCallback {
    private final Context context;
    private File mediaDirectory,fileName;
    private boolean isPictureTaken;
    private OnPhotoSavedListener listener;


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        mediaDirectory= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),CameraActivityWithCameraAPI.MEDIA_FOLDER_NAME);

        if(!mediaDirectory.exists() && !mediaDirectory.mkdir())
        {
            Toast.makeText(context, "Not able to create directory", Toast.LENGTH_SHORT).show();
        }

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyMMddHHmmssZ");
        Date date =new Date();

        fileName= new File(mediaDirectory.getPath()+File.separator+simpleDateFormat.format(date)+"_photo.jpg");

        try
        {
            isPictureTaken=true;
            FileOutputStream fileOutputStream= new FileOutputStream(fileName);
            fileOutputStream.write(data);
            fileOutputStream.close();

            if(listener!=null)
            {
                listener.getSavedPhoto(getPictureUri());
            }

            Toast.makeText(context, "Picture Saved successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e)
        {
            isPictureTaken=false;
            Log.d(CameraActivityWithCameraAPI.DEBUG_TAG, "Error Saving Picture " + e.getMessage());
        }

    }

    public PhotoHandler(Context context)
    {
        this.context=context;
    }


    public void setOnPhotoSavedListener(OnPhotoSavedListener listener)
    {
        this.listener=listener;
    }

    public Uri getPictureUri()
    {
        Uri fileUri=null;
        
        if (isPictureTaken) {
            //fileUri=FileProvider.getUriForFile(context, "com.example.asus.camerademo", fileName);
            fileUri=Uri.fromFile(fileName);
        }
        else
        {
            fileUri=null;
        }
        return fileUri;
    }


    public interface OnPhotoSavedListener
    {
        public void getSavedPhoto(Uri photoUri);
    }

}
