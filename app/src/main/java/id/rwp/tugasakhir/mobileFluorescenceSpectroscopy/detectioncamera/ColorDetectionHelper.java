package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.RGBValue;

/**
 * Created by ranuwp on 3/15/2017.
 */

public class ColorDetectionHelper {

    /**
     * Mendapatkan nilai RGB dari Bitmap
     * @param bitmap bitmap yang akan diambil nilainya
     * @return nilai piksel bitmap
     */
    public static double getRGBValue(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        double value = 0;
        for(int pixel : pixel_array){
            int R = (pixel >> 16) & 0xff;
            int G = (pixel >> 8) & 0xff;
            int B = pixel & 0xff;
            value += (R+G+B);
        }
        return value/pixel_array.length;
    }

    /**
     * Mendapatkan RGBValueClass dari Bitmap
     * @param bitmap bitmap yang akan diambil nilainya
     * @return RGBValue dari Bitmap
     */
    public static RGBValue getRGBValueClass(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        int size = pixel_array.length;
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        int red = 0;
        int green = 0;
        int blue = 0;
        for(int pixel : pixel_array){
            red += (pixel >> 16) & 0xff;
            green += (pixel >> 8) & 0xff;
            blue += pixel & 0xff;
        }
        return new RGBValue(red/size,green/size,blue/size);
    }

    /**
     * Mendapatkan nilai RGBValue dari Bitmap
     * @param bitmap bitmap yang akan diambil nilainya
     * @return List RGBValue untuk nilai masing-masing piksel
     */
    public static ArrayList<RGBValue> getRGBValueClassArray(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        int size = pixel_array.length;
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        ArrayList<RGBValue> rgbValues = new ArrayList<>();
        for(int pixel : pixel_array){
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;
            RGBValue rgbValue = new RGBValue(red,green,blue);
            rgbValues.add(rgbValue);
        }
        return rgbValues;
    }

    /**
     * Mendapatkan Text nilai RGB dari Bitmap
     * @param bitmap bitmap yang akan digunakan
     * @return text dari nilai RGB bitmap
     */
    public static String getRGBValueTest(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        int R = 0;
        int G = 0;
        int B = 0;
        for(int pixel : pixel_array){
            R += (pixel >> 16) & 0xff;
            G += (pixel >> 8) & 0xff;
            B += pixel & 0xff;
        }
        R /= pixel_array.length;
        G /= pixel_array.length;
        B /= pixel_array.length;
        return "R:"+R+",G:"+G+",B:"+B;
    }

    /**
     * Mendapatkan nilai HSV dari Bitmap
     * @param bitmap bitmap yang akan diambil nilainya
     * @return nilai piksel bitmap
     */
    public static double getHSVValue(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        double value = 0;
        for(int pixel : pixel_array){
            int R = (pixel >> 16) & 0xff;
            int G = (pixel >> 8) & 0xff;
            int B = pixel & 0xff;
            float[] hsv = new float[3];
            Color.RGBToHSV(R,G,B,hsv);
            value += (hsv[0]+hsv[1]+hsv[2]);
        }
        return value/pixel_array.length;
    }

    /**
     * Mendapatkan Text nilai HSV dari Bitmap
     * @param bitmap bitmap yang akan digunakan
     * @return text dari nilai HSV bitmap
     */
    public static String getHSVValueTest(Bitmap bitmap){
        int pixel_size = bitmap.getWidth();
        int[] pixel_array = new int[pixel_size*pixel_size];
        bitmap.getPixels(pixel_array,0,pixel_size,0,0,pixel_size,pixel_size);
        float H = 0;
        float S = 0;
        float V = 0;
        for(int pixel : pixel_array){
            int R = (pixel >> 16) & 0xff;
            int G = (pixel >> 8) & 0xff;
            int B = pixel & 0xff;
            float[] hsv = new float[3];
            Color.RGBToHSV(R,G,B,hsv);
            H += hsv[0];
            S += hsv[1];
            V += hsv[2];
        }
        H /= pixel_array.length;
        S /= pixel_array.length;
        V /= pixel_array.length;
        return "H:"+H+",S:"+S+",V:"+V;
    }


}
