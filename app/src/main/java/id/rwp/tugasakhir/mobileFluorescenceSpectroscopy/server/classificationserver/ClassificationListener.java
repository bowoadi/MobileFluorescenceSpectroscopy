package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.classificationserver;

import org.json.JSONObject;

/**
 * Created by ranuwp on 8/10/2017.
 */

/**
 * Listener saat melakukan proses classification
 */
public interface ClassificationListener {
    /**
     * Saat proses classification berhasil
     * @param jsonObject balasan dari server
     */
    void onSuccess(JSONObject jsonObject);

    /**
     * Saat proses classification gagal
     * @param error error yang terjadi
     */
    void onFailed(String error);
}
