package com.example.asus.camerademo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;


/**
 * Created by asus on 02-09-2017.
 */
public class Utility {

    public static final String TAG=Utility.class.getSimpleName();

    public static boolean isExternalStorageAvailable()
    {
        return(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static boolean isExternalStorageReadable()
    {
        return
                (
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)
        );
    }


    public static Uri getExternalFilesDirForVersion24Above(@NonNull Context context,@NonNull String directoryPath,@NonNull String folderName,@NonNull String fileName)
    {
        Uri uri=null;
        File mediaStorageDir=null;
        File file=null;

        if(directoryPath.equals(Environment.DIRECTORY_PICTURES))
        {

           mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),folderName);

            if(!mediaStorageDir.exists() && !mediaStorageDir.mkdir())
           {
               Log.d(TAG, "Not able to create directory");
           }

        }

        if(mediaStorageDir!=null)
        {

            file= new File(mediaStorageDir.getPath()+File.separator+fileName);
            Log.d("fileName",file+"");

            uri=FileProvider.getUriForFile(context,"com.example.asus.camerademo",file);
            Log.d("uriName",uri+"");
        }
        return uri;
    }


    public static Uri getExternalFilesDirForVersion24Below(@NonNull Context context,@NonNull String directoryPath,@NonNull String folderName,@NonNull String fileName)
    {
        Uri uri=null;
        File mediaStorageDir=null;
        File file=null;

        if(directoryPath.equals(Environment.DIRECTORY_PICTURES))
        {
            mediaStorageDir=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),folderName);

            if(!mediaStorageDir.exists() && !mediaStorageDir.mkdir())
            {
                Log.d(TAG,"Not able to create directory");
            }
        }

        if(mediaStorageDir!=null)
        {
            file=new File(mediaStorageDir.getPath()+File.separator+fileName);
            uri=Uri.fromFile(file);
        }

        return uri;
    }

}
