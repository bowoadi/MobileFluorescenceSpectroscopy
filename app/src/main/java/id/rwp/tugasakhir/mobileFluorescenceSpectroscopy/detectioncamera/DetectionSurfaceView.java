 package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by ranuwp on 3/14/2017.
 */

/**
 * Kelas SurfaceView untuk tampilan detection
 */
public class DetectionSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnTouchListener{

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private CameraSettings cameraSettings;
    private DetectionHelper detectionHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int selectedMicroRNAPosition=-1;
    private Paint textPaint;
    private Paint dotPaint;

    /**
     * Konstruktor DetectionSurfaceView
     * @param context context dari activity yang menggunakan surfaceview ini
     * @param detectionHelper detectionHelper yang digunakan untuk surfaceview
     */
    public DetectionSurfaceView(Context context, DetectionHelper detectionHelper) {
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
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        setWillNotDraw(false);
        setOnTouchListener(this);
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

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] bytes, Camera camera) {
            Log.d("RWP", "On Jpeg : " + bytes.length);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    detectionHelper.imageProcessing(bytes);
                }
            });
            camera.startPreview();
        }
    };

    public void setSelectedMicroRNAPosition(int position){
        this.selectedMicroRNAPosition = position;
    }

    /**
     * Fungsi untuk menggambar dots pada surfaceview
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        ArrayList<DotLocation> dotLocations = detectionHelper.getDotLocations();
        if (dotLocations != null) {
            if (dotLocations.size() >= 0) {
                for (DotLocation dotLocation : dotLocations) {
                    int xtemp = onScreenWidth-CameraSettings.PREVIEW_WIDTH;
                    double xChange = (double) xtemp/CameraSettings.PREVIEW_WIDTH;
                    int ytemp = onScreenHeight-CameraSettings.PREVIEW_HEIGHT;
                    double yChange = (double) ytemp/CameraSettings.PREVIEW_HEIGHT;
                    int x = dotLocation.getX()+(int)(dotLocation.getX()*xChange);
                    int y = dotLocation.getY()+(int)(dotLocation.getY()*yChange);
                    float textSize = onScreenHeight*4/100;
                    textPaint.setTextSize(textSize);
                    canvas.drawText(dotLocation.getMicroRNA().getName(), x, y, textPaint);
                    float radius = onScreenHeight/100;
                    canvas.drawCircle(x, y, radius, dotPaint);
                }
            }
        }
    }

    private int onScreenWidth;
    private int onScreenHeight;

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
     * Fungsi yang dilakukan saat surfaceview disentuh
     * @param view tampilan yang ditekan
     * @param motionEvent posisi tekanan yang dilakukan user
     * @return true jika tidak ada view lain yang dapat ditekan, false jika view lain dapat sekalian ditekan
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(detectionHelper.getDotLocations()== null)return false;
        if(detectionHelper.getDotLocations().size() > 0){
            if(motionEvent.getAction() == ACTION_DOWN && selectedMicroRNAPosition != -1){
                float[] eventXY = new float[]{motionEvent.getX(), motionEvent.getY()};
                int x = (int) eventXY[0];
                int y = (int) eventXY[1];
                if(x < 0){
                    x = 0;
                }
                if(y < 0){
                    y = 0;
                }
                if(x > onScreenWidth-1){
                    x = onScreenWidth;
                }
                if(y > onScreenHeight-1){
                    y = onScreenHeight;
                }
                changeDotPosition(selectedMicroRNAPosition,x,y);
                Log.d("RWP",x+" x:y "+y);
            }else if(motionEvent.getAction() == ACTION_UP){
                invalidate();
            }
        }
        return true;
    }

    /**
     * Merubah posisi dari dots
     * @param position posisi dots
     * @param xScreen ukuran lebar layar surfaceview
     * @param yScreen ukuran tinggi layar surfaceview
     */
    private void changeDotPosition(int position, int xScreen, int yScreen){
        int xtemp = CameraSettings.PREVIEW_WIDTH-onScreenWidth;
        double xChange = (double) xtemp/onScreenWidth;
        int ytemp = CameraSettings.PREVIEW_HEIGHT-onScreenHeight;
        double yChange = (double) ytemp/onScreenHeight;
        int x = xScreen+(int)(xScreen*xChange);
        int y = yScreen+(int)(yScreen*yChange);
        detectionHelper.getDotLocations().get(position).setX(x);
        detectionHelper.getDotLocations().get(position).setY(y);
    }

    /**
     * Memulai Deteksi
     * @param textView start duration interval textview
     * @param startDelaytime waktu delay awal yang digunakan
     */
    public void startDetection(TextView textView, int startDelaytime){
        selectedMicroRNAPosition = 0;
        invalidate();
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
