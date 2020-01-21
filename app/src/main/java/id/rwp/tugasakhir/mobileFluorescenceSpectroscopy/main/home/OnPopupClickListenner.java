package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home;

/**
 * Created by ranuwp on 3/19/2017.
 */

/**
 * Interface untuk menangani fungsi popup pada view home realtime
 */
public interface OnPopupClickListenner {
    /**
     * fungsi saat file csv akan dipilih
     * @param path lokasi file csv
     */
    void openCSVFile(String path);
}
