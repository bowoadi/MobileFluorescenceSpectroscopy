package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;

/**
 * Created by ranuwp on 8/10/2017.
 */

/**
 * Listener yang digunakan saat penyimpanan dilakukan
 */
public interface StorageListener {
    /**
     * Saat penyimpanan berhasil dilakukan
     * @param dataResult dataresult yang disimpan
     */
    void onSuccess(DataResult dataResult);

    /**
     * Saat penyimpanan gagal dilakukan
     * @param error pesan error
     */
    void onFailed(String error);
}
