package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ranuwp on 11/9/2017.
 */

/**
 * Kelas untuk menyimpan dataset
 */
public class DataSet {

    @SerializedName("uid")
    private String uid;

    @SerializedName("folders")
    private List<FolderDataSet> folders;

    @SerializedName("lrate")
    private double lrate;

    @SerializedName("epoch")
    private int epoch;

    @SerializedName("merror")
    private double merror;

    @SerializedName("hidden")
    private int hidden;

    @SerializedName("k")
    private int k;

    @SerializedName("method")
    private String method;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<FolderDataSet> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderDataSet> folders) {
        this.folders = folders;
    }

    public double getLrate() {
        return lrate;
    }

    public void setLrate(double lrate) {
        this.lrate = lrate;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public double getMerror() {
        return merror;
    }

    public void setMerror(double merror) {
        this.merror = merror;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
