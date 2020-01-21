package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan dataresult yang akan dihapus pada server
 */
public class DeleteDataResult {
    @SerializedName("uid")
    private String uid;

    @SerializedName("data_result_id")
    private int data_result_id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getData_result_id() {
        return data_result_id;
    }

    public void setData_result_id(int data_result_id) {
        this.data_result_id = data_result_id;
    }
}
