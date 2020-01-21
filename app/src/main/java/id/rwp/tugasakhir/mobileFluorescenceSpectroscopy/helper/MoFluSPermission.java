package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by ranuwp on 11/21/2017.
 */

/**
 * Kelas yang membantu dalam proses permission pada android
 */
public class MoFluSPermission {

    public static final int EXTERNAL_STORAGE_REQUEST_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    /**
     * Meminta permission untuk menggunakan external storage
     * @param activity activity yang menggunakan permission
     * @return true jika boleh, false jika tidak
     */
    public static boolean requestExternalStorage(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    /**
     * Meminta permission untuk menggunakan camera
     * @param activity activity yang menggunakan permission
     * @return true jika boleh, false jika tidak
     */
    public static boolean requestCamera(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

}
