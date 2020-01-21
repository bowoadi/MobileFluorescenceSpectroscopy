package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;

import android.content.Context;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;

/**
 * Created by ranuwp on 8/15/2017.
 */

/**
 * Kelas yang membantu dalam proses upload gambar
 */
public class ImageUploader {

    private Context context;
    private MultipartUploadRequest request;
    private String url_dataresult = "http://172.104.188.209/upload_gambar_data_result/";
    private String url_dataset = "http://172.104.188.209/upload_gambar_dataset/";
    private User user;

    /**
     * Konstruktor ImageUpload
     * @param context context dari activity yang menggunakan imageuploader
     * @param user user yang login
     */
    public ImageUploader(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    /**
     * Mengupload dataresult preview ke server
     * @param file lokasi preview dari dataresult
     */
    public void uploadDataResultPreview(File file) {
        String url = url_dataresult;
        try {
            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
            MultipartUploadRequest request = new MultipartUploadRequest(context, url + user.getUid())
                    .setMethod("POST")
                    .setUtf8Charset()
                    .setNotificationConfig(uploadNotificationConfig)
                    .setMaxRetries(3)
                    .setUsesFixedLengthStreamingMode(true);
            request.addFileToUpload(file.getPath(), "image");
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengupload semua gambar dataset
     * @param files semua file gambar dataset
     */
    public void uploadDatasetImage(ArrayList<File> files) {
        String url = url_dataset;
        try {
            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
            MultipartUploadRequest request = new MultipartUploadRequest(context, url + user.getUid())
                    .setMethod("POST")
                    .setUtf8Charset()
                    .setNotificationConfig(uploadNotificationConfig)
                    .setMaxRetries(1)
                    .setUsesFixedLengthStreamingMode(true);
            for (File file : files) {
                String[] param = file.getPath().split("/");
                String parameter = param[param.length - 2] + ";" + file.getName();
                request.addFileToUpload(file.getPath(), parameter);
            }
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
