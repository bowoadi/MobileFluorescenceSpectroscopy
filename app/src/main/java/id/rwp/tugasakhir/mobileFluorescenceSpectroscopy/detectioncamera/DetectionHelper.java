package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.RGBValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;

/**
 * Created by ranuwp on 3/14/2017.
 */

/**
 * Kelas yang digunakan untuk membantu proses pengambilan nilai gambar dari bitmap pada surfaceview
 */
public class DetectionHelper {
    private boolean isProcess;
    private boolean isOnCountdown;
    private byte[] preview;
    private long duration;
    private long interval;
    private int pixel_size;
    private long startTime;
    private long current_time;
    private long next_time;
    private long finish_time;
    private DataResult dataResult;
    private ArrayList<DotLocation> dotLocations;
    private DetectionListener detectionListener;

    /**
     * Konstruktor DetectionHelper
     * @param detectionListener listener untuk mengatur perubahan yang dilakukan pada surfaceview
     */
    public DetectionHelper(DetectionListener detectionListener) {
        this.isProcess = false;
        this.isOnCountdown = false;
        this.detectionListener = detectionListener;
    }

    /**
     * Memulai delay text
     * @param textView textview yang akan digunakan untuk delay text
     * @param startDelayTime waktu detik yang digunakan delay
     */
    public void startDelayText(TextView textView, int startDelayTime) {
        isOnCountdown = true;
        countDown(textView, startDelayTime);
    }

    /**
     * Fungsi rekrusive untuk menghitung mundur delay text
     * @param textView textview yang akan digunakan untuk delay text
     * @param startDelayTime waktu detik yang digunakan delay
     */
    private void countDown(final TextView textView, final int startDelayTime) {
        if (startDelayTime == 0 || !isOnCountdown) {
            if (isOnCountdown) {
                setProcess(true);
                startTime = System.currentTimeMillis();
                next_time = startTime;
                finish_time = startTime+duration;
                isOnCountdown = false;
            }
            textView.setText("");
            return;
        }
        textView.setText("" + startDelayTime);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                countDown(textView, startDelayTime - 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(alphaAnimation);
    }

    public void stopDelayText() {
        isOnCountdown = false;
    }

    /**
     * Menghentikan proses pengambilan nilai gambar
     */
    public void finishProcess() {
        try{
            dataResult.setPreview_picture(BitmapHelper.getBitmapFromNV21(preview));
            isProcess = false;
            detectionListener.finishProcess(dataResult);
        }catch (NullPointerException e){
            isProcess = false;
            detectionListener.finishProcess(null);
        }
    }

    /**
     * Melakukan proses pengambilan nilai gambar
     * @param bytes bytes dari gambar yang diambil
     */
    public void imageProcessing(byte[] bytes) {
        synchronized (this){
            Bitmap bitmap = BitmapHelper.getBitmap(bytes);
            Log.d("RWP", "Size : "+bitmap.getWidth()+" Height : "+bitmap.getHeight());
            int i = 0;
            for (GraphData graphData : dataResult.getGraphDatas()) {
                Bitmap live_preview = BitmapHelper.getBitmapLivePreview(bitmap,dotLocations.get(i++),pixel_size);
                graphData.setLive_preview(live_preview);
                detectionListener.dataChange(dataResult);
                ArrayList<RGBValue> rgbValues = ColorDetectionHelper.getRGBValueClassArray(live_preview);
                double value = 0;
                for(RGBValue rgbValue : rgbValues){
                    value += rgbValue.getColorValue(dataResult.getDetectionMethodColors());
                }
                GraphDataValue graphDataValue = new GraphDataValue((current_time - startTime), value);
                graphData.getLineGraphSeries().appendData(graphDataValue, false, ((int)(duration/interval))+1);
                graphData.getGraphDataValues().add(graphDataValue);
            }
//            int i = 0;
//            switch (dataResult.getDetectionMethodColors().getColor_type()) {
//                case "RGB":
//                    for (GraphData graphData : dataResult.getGraphDatas()) {
//                        Bitmap live_preview = BitmapHelper.getBitmapLivePreview(bitmap,dotLocations.get(i++),pixel_size);
//                        graphData.setLive_preview(live_preview);
//                        detectionListener.dataChange(dataResult);
//                        double value = ColorDetectionHelper.getRGBValue(live_preview);
//                        GraphDataValue graphDataValue = new GraphDataValue((current_time - startTime), value);
//                        graphData.getLineGraphSeries().appendData(graphDataValue, false, ((int)(duration/interval))+1);
//                        graphData.getGraphDataValues().add(graphDataValue);
//                    }
//                    break;
//                case "HSV":
//                    for (GraphData graphData : dataResult.getGraphDatas()) {
//                        Bitmap live_preview = BitmapHelper.getBitmapLivePreview(bitmap,dotLocations.get(i++),pixel_size);
//                        graphData.setLive_preview(live_preview);
//                        detectionListener.dataChange(dataResult);
//                        double value = ColorDetectionHelper.getHSVValue(live_preview);
//                        GraphDataValue graphDataValue = new GraphDataValue((current_time - startTime), value);
//                        graphData.getLineGraphSeries().appendData(graphDataValue, false, ((int)(duration/interval))+1);
//                        graphData.getGraphDataValues().add(graphDataValue);
//                    }
//                    break;
//                default :
//                    for (GraphData graphData : dataResult.getGraphDatas()) {
//                        Bitmap live_preview = BitmapHelper.getBitmapLivePreview(bitmap,dotLocations.get(i++),pixel_size);
//                        graphData.setLive_preview(live_preview);
//                        detectionListener.dataChange(dataResult);
//                        RGBValue rgbValue = ColorDetectionHelper.getRGBValueClass(live_preview);
//                        GraphDataValue graphDataValue = new GraphDataValue((current_time - startTime), rgbValue,dataResult.getDetectionMethodColors());
//                        graphData.getLineGraphSeries().appendData(graphDataValue, false, ((int)(duration/interval))+1);
//                        graphData.getGraphDataValues().add(graphDataValue);
//                    }
//            }
            bytes = null;
            bitmap.recycle();
        }
    }

    /**
     * Membuat DataResultGraphView tertanam pada DataResult
     * @param dataResult dataresult yang digunakan
     * @param context context dari activity yang menggunakan kelas ini
     */
    private void createDataResultGraphView(DataResult dataResult, final Context context) {
        for (GraphData graphData : dataResult.getGraphDatas()) {
            LineGraphSeries lineSeries = new LineGraphSeries<>();
            lineSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(context,"Time : "+dataPoint.getX()+"\nValue : "+dataPoint.getY(),Toast.LENGTH_SHORT).show();
                }
            });
            graphData.setLineGraphSeries(lineSeries);
        }
    }

    public void setDataResult(DataResult dataResult, Context context) {
        createDataResultGraphView(dataResult, context);
        this.dataResult = dataResult;
    }

    public ArrayList<DotLocation> getDotLocations() {
        return dotLocations;
    }

    public void setDotLocations(ArrayList<DotLocation> dotLocations) {
        this.dotLocations = dotLocations;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setPixel_size(int pixel_size) {
        this.pixel_size = pixel_size;
    }

    public long getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(long current_time) {
        this.current_time = current_time;
    }

    public long getNext_time() {
        return next_time;
    }

    public void setNext_time(long next_time) {
        this.next_time = next_time;
    }

    public long getFinish_time() {
        return finish_time;
    }

    public boolean isProcess() {
        return isProcess;
    }

    public void setProcess(boolean process) {
        isProcess = process;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }
}
