package com.example.asus.camerademo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by asus on 07-09-2017.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera mCamera;
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private static final String DEBUG_TAG="CameraPreview_D";
    private static boolean isPreviewRunning=false;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mContext=context;
        mCamera=camera;

        mSurfaceHolder=getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            isPreviewRunning=true;
            mCamera.setDisplayOrientation(90);
        }catch (IOException e)
        {
            Log.d(DEBUG_TAG, "Error Setting Camera Preview " + e.getMessage());
            isPreviewRunning=false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mCamera.stopPreview();
            mCamera.release();
            isPreviewRunning=false;
        }catch (Exception e)
        {
            Log.d(DEBUG_TAG,"Error in releasing Camera "+e.getMessage());
        }
    }


    public void destorySurface()
    {
        try {
            mCamera.stopPreview();
            mCamera.release();
            isPreviewRunning=false;
        }catch (Exception e)
        {
            Log.d(DEBUG_TAG,"Error in releasing Camera "+e.getMessage());
        }
    }
}
