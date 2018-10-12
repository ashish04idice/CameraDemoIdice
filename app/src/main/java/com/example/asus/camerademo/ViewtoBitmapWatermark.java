package com.example.asus.camerademo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewtoBitmapWatermark extends AppCompatActivity {

    RelativeLayout relative_layout_for_watermark_id;
    ImageView image_view_watermark_id;
    TextView text_view_watermark_id;
    Button btn_watermark_id;

    private File file;

    private static final String APP_TAG="WatermarkDemo";
    private static final String WATERMARK_FILE_NAME="Watermark.jpeg";
    private static final String DEBUG_TAG="debug_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewto_bitmap_watermark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relative_layout_for_watermark_id=(RelativeLayout) findViewById(R.id.relative_layout_for_watermark_id);
        image_view_watermark_id=(ImageView)findViewById(R.id.image_view_watermark_id);
        text_view_watermark_id=(TextView) findViewById(R.id.text_view_watermark_id);
        btn_watermark_id=(Button) findViewById(R.id.btn_watermark_id);

        file=getBitmapFile();

        if(file!=null)
        {
            loadBitmapFromView(relative_layout_for_watermark_id,file);
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


}
