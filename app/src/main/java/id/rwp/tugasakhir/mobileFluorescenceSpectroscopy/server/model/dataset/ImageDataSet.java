package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ranuwp on 11/9/2017.
 */

/**
 * Kelas untuk menyimpan imagedataset
 */
class ImageDataSet {

    @SerializedName("path")
    private String path;

    @SerializedName("values")
    private List<ValuesDataSet> valuesDataSets;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ValuesDataSet> getValuesDataSets() {
        return valuesDataSets;
    }

    public void setValuesDataSets(List<ValuesDataSet> valuesDataSets) {
        this.valuesDataSets = valuesDataSets;
    }
}
