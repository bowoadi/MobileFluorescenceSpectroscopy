package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.DeleteDataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.PublicSettings;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.SaveDataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.SettingsData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.Status;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset.DataSet;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.testing.TestingResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ranuwp on 10/5/2017.
 */

/**
 * Interface untuk proses service ke server
 */
public interface IMoFluS {

    /**
     * fungsi untuk login
     * @param user user yang akan login
     * @return publicsettings yang dimiliki user
     */
    @POST("service/login")
    Call<PublicSettings> login(
            @Body User user
    );

    /**
     * fungsi untuk register
     * @param user user yang akan register
     * @return user yang telah terregister
     */
    @POST("register")
    Call<User> register(
            @Body User user
    );

    /**
     * fungsi untuk menyimpan dataresult ke server
     * @param saveDataResult dataresult yang disimpan ke server
     * @return status berhasil atau tidak
     */
    @POST("service/tambah_data_result")
    Call<Status> saveDataResult(
            @Body SaveDataResult saveDataResult
    );

    /**
     * Fungsi untuk menghapus dataresult pada server
     * @param deleteDataResult dataresult yang dihapus pada server
     * @return status berhasil atau tidak
     */
    @POST("service/delete_data_result")
    Call<Status> deleteDataResult(
            @Body DeleteDataResult deleteDataResult
    );

    /**
     * fungsi untuk menyimpan samples dan dots
     * @param settingsData settingsdata yang dimiliki user
     * @return status berhasil atau tidak
     */
    @POST("service/simpan_samples_dots")
    Call<Status> saveSettings(
            @Body SettingsData settingsData
    );

    /**
     * Fungsi untuk melakukan training pada server
     * @param dataSet dataset yang digunakan
     * @return status berhasil atau tidak
     */
    @POST("upload_dataset_moflus")
    Call<Status> dataSetSend(
            @Body DataSet dataSet
    );

    /**
     * fungsi untuk melakukan pengecekan status training
     * @param user user yang login
     * @return status berhasil atau tidak
     */
    @POST("cekmodel")
    Call<Status> checkTraining(
        @Body User user
    );

    /**
     * fungsi untuk melakukan classification pada batch data
     * @param dataSet dataset yang digunakan
     * @return Semua testingresult yang telah di testing
     */
    @POST("classify_extended")
    Call<List<TestingResult>> testingDataSet(
            @Body DataSet dataSet
    );

}
