package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;

/**
 * Created by ranuwp on 3/17/2017.
 */

/**
 * Listener saat proses deteksi dimulai
 */
public interface DetectionListener {
    /**
     * Saat nilai pada dataresult berubah
     * @param dataResult dataresult yang berubah
     */
    void dataChange(DataResult dataResult);

    /**
     * Saat Proses deteksi selesai
     * @param dataResult hasil akhir dataresult
     */
    void finishProcess(DataResult dataResult);
}
