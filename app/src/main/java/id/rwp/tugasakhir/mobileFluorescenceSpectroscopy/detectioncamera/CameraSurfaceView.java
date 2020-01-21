package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.CameraSettings;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;

/**
 * Created by ranuwp on 3/14/2017.
 */

/**
 * Kelas SurfaceView untuk tampilan camera
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private CameraSettings cameraSettings;
    private DetectionHelper detectionHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Folder folder;
    private int onScreenWidth;
    private int onScreenHeight;

    /**
     * Konstruktor DetectionSurfaceView
     * @param context context dari activity yang menggunakan surfaceview ini
     * @param detectionHelper detectionHelper yang digunakan untuk surfaceview
     * @param folder folder untuk menyimpan hasil gambar dari kamera
     */
    public CameraSurfaceView(Context context, DetectionHelper detectionHelper,Folder folder) {
        super(context);
        try{
            camera = Camera.open();
        }catch (RuntimeException e){
            Toast.makeText(context, "Camera Cannot be Used", Toast.LENGTH_SHORT).show();
            return;
        }
        if(camera == null){
            return;
        }
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraSettings = new CameraSettings();
        this.detectionHelper = detectionHelper;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
        this.folder = folder;
    }

    /**
     * Fungsi yang dijalankan saat surfaceview terbuat
     * @param surfaceHolder surfaceviewHolder yang digunakan untuk kamera
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(this);
        } catch (IOException e) {
            camera.release();
            camera = null;
        }
    }

    /**
     * Fungsi yang jalan saat pengaturan surfaceview berubah
     * @param surfaceHolder surfaceview yang digunakan
     * @param i -
     * @param i1 -
     * @param i2 -
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        cameraSettings.setParameters(camera);
    }

    /**
     * Fungsi yang jalan saat surfaceview dihancurkan
     * @param surfaceHolder surfaceview yang dihancurkan
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * Callback saat gambar berhasil diambil
     */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] bytes, Camera camera) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    File photo = new File(folder.getPath(),String.valueOf(new Date().getTime())+".png");
                    if (photo.exists()) {
                        photo.delete();
                    }
                    try {
                        FileOutputStream fos=new FileOutputStream(photo.getPath());
                        fos.write(bytes);
                        fos.close();
                    }
                    catch (java.io.IOException e) {
                        Log.e("PictureDemo", "Exception in photoCallback", e);
                    }
                }
            }
            );
            camera.startPreview();
        }
    };

    /**
     * Fungsi yang mengatur resolusi surfaceview terhadap layar pada smartphone
     * @param widthMeasureSpec lebar yang diukur
     * @param heightMeasureSpec tinggi yang diukur
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//        int width = ((View)getParent()).getMeasuredWidth();
//        int height = ((View)getParent()).getMeasuredHeight();
//        setMeasuredDimension(parentWidth,parentHeight);
//        setLayoutParams(new FrameLayout.LayoutParams(parentWidth,parentHeight));
        int height = ((View) getParent()).getMeasuredHeight();
        int dif = Math.abs(height - CameraSettings.PREVIEW_HEIGHT);
        int width = (int)(((double)4/3)*height);
        onScreenWidth = width;
        onScreenHeight = height;
        //height = CameraSettings.PREVIEW_HEIGHT - dif;
        setMeasuredDimension(width, height);
        Log.d("RWP1", width + " : " + height + " DIF :" + dif);
        //super.onMeasure(width,height);
    }

    /**
     * Fungsi yang selalu jalan saat surfaceview aktif
     * @param bytes preview image yang ditangkap
     * @param camera camera yang digunakan
     */
    @Override
    public void onPreviewFrame(byte[] bytes, final Camera camera) {
        if ((detectionHelper.getCurrent_time() > detectionHelper.getFinish_time())) {
            detectionHelper.finishProcess();
        }
        if (detectionHelper.isProcess()) {
            long expected_time = System.currentTimeMillis();
            if (expected_time >= detectionHelper.getNext_time()) {
                detectionHelper.setNext_time(detectionHelper.getNext_time() + detectionHelper.getInterval());
                detectionHelper.setCurrent_time(System.currentTimeMillis());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            camera.takePicture(null, null, jpegCallback);
                        } catch (RuntimeException e) {
                            Log.d("CameraError", e.getMessage());
                        }
                        //detectionHelper.imageProcessing(image);
                    }
                });
            }
        } else {
            detectionHelper.setPreview(bytes);
        }
    }

    /**
     * Memulai Deteksi
     * @param textView start duration interval textview
     * @param startDelaytime waktu delay awal yang digunakan
     */
    public void startDetection(TextView textView, int startDelaytime){
        detectionHelper.startDelayText(textView,startDelaytime);
    }

    /**
     * Melakukan autofokus pada camera
     */
    public void startAutoFocus(){
        if (camera != null){
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    if(b){
                        Toast.makeText(getContext(), "Focus Success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Focus Failed", Toast.LENGTH_SHORT).show();
                    }
                    camera.cancelAutoFocus();
                }
            });
        }
    }
}
