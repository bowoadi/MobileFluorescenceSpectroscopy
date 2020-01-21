package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json settingsdata
 */
public class SettingsData {

    @SerializedName("uid")
    public String uid;

    @SerializedName("samples")
    public List<MicroRNA> microRNAs;

    @SerializedName("dots")
    public List<Dots> dots;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<MicroRNA> getMicroRNAs() {
        return microRNAs;
    }

    public void setMicroRNAs(List<MicroRNA> microRNAs) {
        this.microRNAs = microRNAs;
    }

    public List<Dots> getDots() {
        return dots;
    }

    public void setDots(List<Dots> dots) {
        this.dots = dots;
    }
}
