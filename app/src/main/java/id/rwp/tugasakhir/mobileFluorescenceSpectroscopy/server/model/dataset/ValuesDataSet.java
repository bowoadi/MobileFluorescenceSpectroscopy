package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ranuwp on 11/9/2017.
 */

/**
 * Kelas untuk menyimpan valuesdataset
 */
class ValuesDataSet {

    @SerializedName("microrna_name")
    private String microRNAName;

    @SerializedName("value")
    private double value;

    public String getMicroRNAName() {
        return microRNAName;
    }

    public void setMicroRNAName(String microRNAName) {
        this.microRNAName = microRNAName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
