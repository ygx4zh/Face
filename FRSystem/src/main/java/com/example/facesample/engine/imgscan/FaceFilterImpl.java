package com.example.facesample.engine.imgscan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;

import java.io.File;



public class FaceFilterImpl implements Filter<File, Boolean> {

    private FaceDetector.Face[] faces = new FaceDetector.Face[1];
    private FaceDetector detector;
    private final BitmapFactory.Options opts;

    public FaceFilterImpl(){
        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    private int mCacheWidth ;
    private int mCacheHeight;
    final FaceDetector getFaceDetector(int bmp_width, int bmp_height) {
       if(detector == null)
       {
           detector = new FaceDetector(bmp_width,bmp_height,faces.length);
           mCacheWidth = bmp_width;
           mCacheHeight = bmp_height;
       }

       if(bmp_height == mCacheHeight && bmp_width == mCacheWidth)
           return detector;

        detector = new FaceDetector(bmp_width,bmp_height,faces.length);
        mCacheWidth = bmp_width;
        mCacheHeight = bmp_height;
        return detector;
    }


    @Override
    public Boolean filte(File p) {
        Bitmap bitmap = BitmapFactory.decodeFile(p.getAbsolutePath(),opts);
        FaceDetector faceDetector = getFaceDetector(bitmap.getWidth(), bitmap.getHeight());
        int faces = faceDetector.findFaces(bitmap, this.faces);

        return faces > 0;
    }
}
