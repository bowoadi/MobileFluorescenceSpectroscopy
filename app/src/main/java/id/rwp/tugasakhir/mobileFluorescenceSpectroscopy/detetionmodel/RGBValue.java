package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by ranuwp on 8/14/2017.
 */

/**
 * Kelas untuk menyimpan RGBValue
 */
public class RGBValue {

    private int red;
    private int green;
    private int blue;
    private float hue;
    private float saturation;
    private float value;

    /**
     * Konstruktor RGBValue
     * @param red nilai merah
     * @param green nilai hijau
     * @param blue nilai biru
     */
    public RGBValue(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        float[] hsv = new float[3];
        Color.RGBToHSV(red,green,blue,hsv);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    /**
     * Mendapatkan total nilai dari warna berdasarkan detectionmethodcolor yang digunakan
     * @param detectionMethodColors
     * @return
     */
    public double getColorValue(ArrayList<DetectionMethodColor>detectionMethodColors){
        double value= 0;
        for(DetectionMethodColor detectionMethodColor :detectionMethodColors){
            String colorMethod = detectionMethodColor.getColor_type();
            if(colorMethod.equals("Red")){
                value +=red;
            }else if(colorMethod.equals("Green")){
                value += green;
            }else if(colorMethod.equals("Blue")){
                value += blue;
            }else if(colorMethod.equals("Hue")){
                value += hue;
            }else if(colorMethod.equals("Saturation")){
                value += saturation;
            }else if(colorMethod.equals("Value")){
                value += this.value;
            }else if(colorMethod.equals("Wavelength")){
                value += 0;
            }else{
                value += 0;
            }
        }
        return value;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getValue() {
        return value;
    }
}
