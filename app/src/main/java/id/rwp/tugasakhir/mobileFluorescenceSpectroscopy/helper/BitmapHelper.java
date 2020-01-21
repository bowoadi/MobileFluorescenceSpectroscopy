package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.CameraSettings;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;

/**
 * Kelas yang membantu dalam pengelolaan bitmap
 */
public class BitmapHelper {

    /**
     * Mendapatkan byte[] dari bitmap
     * @param bitmap bitmap yang digunakan
     * @return byte[] dari bitmap yang digunakan
     */
    public static byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        return stream.toByteArray();
    }

    /**
     * Mendapatkan bitmap dari byte[]
     * @param bytes byte[] dari bitmap
     * @return bitmap dari byte[] yang digunakan
     */
    public static Bitmap getBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    /**
     * Mendapatkan bitmap dari byte[] dengan format NV21
     * @param bytes byte[] dari bitmap dengan format nv21
     * @return
     */
    public static Bitmap getBitmapFromNV21(byte[] bytes){
        YuvImage yuvImage = new YuvImage(bytes, CameraSettings.IMAGE_FORMAT, CameraSettings.PREVIEW_WIDTH, CameraSettings.PREVIEW_HEIGHT, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, CameraSettings.PREVIEW_WIDTH, CameraSettings.PREVIEW_HEIGHT), 100, os);
        byte[] jpgByteArray = os.toByteArray();
        return BitmapFactory.decodeByteArray(jpgByteArray, 0, jpgByteArray.length);
    }

    /**
     * Mendapatkan gambar titik-titik pada bitmap berdasarkan dotlocations
     * @param dotLocations dotlocation yang memiliki titik-titik yang akan dibuat
     * @return gambar dengan titik-titik
     */
    public static  Bitmap getBitmapPixelLocation(ArrayList<DotLocation> dotLocations){
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(CameraSettings.PREVIEW_WIDTH, CameraSettings.PREVIEW_HEIGHT, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        Paint paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setTextSize(32f);
        Paint paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        for (DotLocation dotLocation : dotLocations) {
            int x = dotLocation.getX();
            int y = dotLocation.getY();
            canvas.drawText(dotLocation.getMicroRNA().getName(), x, y, paint1);
            canvas.drawCircle(x, y, 5, paint2);
        }
        return bitmap;
    }

    /**
     * Mendapatkan live preview dari bitmap berdasarkan lokasi dan ukuran piksel
     * @param bitmap bitmap yang diproses
     * @param dotLocation lokasi dari titik
     * @param pixel_size ukuran piksel yang digunakan
     * @return bitmap live preview
     */
    public static Bitmap getBitmapLivePreview(Bitmap bitmap,DotLocation dotLocation, int pixel_size){
        Bitmap sentBitmap;
        try{
            int xtemp = CameraSettings.PICTURE_WIDTH-CameraSettings.PREVIEW_WIDTH;
            double xChange = (double) xtemp/CameraSettings.PREVIEW_WIDTH;
            int ytemp = CameraSettings.PICTURE_HEIGHT-CameraSettings.PREVIEW_HEIGHT;
            double yChange = (double) ytemp/CameraSettings.PREVIEW_HEIGHT;
            int x = dotLocation.getX()+(int)(dotLocation.getX()*xChange);
            int y = dotLocation.getY()+(int)(dotLocation.getY()*yChange);
            sentBitmap = Bitmap.createBitmap(bitmap,x-(pixel_size/2),y-(pixel_size/2),pixel_size,pixel_size);
        }catch (IllegalArgumentException e){
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            sentBitmap = Bitmap.createBitmap(pixel_size,pixel_size,config);
        }
        return sentBitmap;
    }

    /**
     * Mendapatkan bitmap live preview dengan ukuran asli berdasarkan lokasi dan ukuran piksel
     * @param bitmap bitmap yang digunakan
     * @param dotLocation lokasi titik yang dideteksi
     * @param pixel_size ukuran piksel
     * @return gambar live preview dengan ukuran asli
     */
    public static Bitmap getRealBitmapLivePreview(Bitmap bitmap,DotLocation dotLocation, int pixel_size){
        Bitmap sentBitmap;
        try{
            int x = dotLocation.getX();
            int y = dotLocation.getY();
            sentBitmap = Bitmap.createBitmap(bitmap,x-(pixel_size/2),y-(pixel_size/2),pixel_size,pixel_size);
        }catch (IllegalArgumentException e){
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            sentBitmap = Bitmap.createBitmap(pixel_size,pixel_size,config);
        }
        return sentBitmap;
    }

    /**
     * Membuat bitmap kedalam file
     * @param bitmap bitmap yang akan diproses
     * @param fileName nama file dari gambar yang akan dibuat
     * @return file yang telah dibuat
     */
    public static File createBitmapToFile(Bitmap bitmap, String fileName){
        FileOutputStream out = null;
        File uploadedFile = new File(FolderUtil.getUploadDirectory()+"/"+fileName+".jpg");
        try {
            out = new FileOutputStream(uploadedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uploadedFile;
    }

    /**
     * Mendapatkan bitmap dari file
     * @param file file yang berisi alamat gambar
     * @return bitmap dari gambar berdasarkan file
     */
    public static Bitmap getBitmapFromFile(File file){
        String filePath = file.getPath();
        return BitmapFactory.decodeFile(filePath);
    }
}
