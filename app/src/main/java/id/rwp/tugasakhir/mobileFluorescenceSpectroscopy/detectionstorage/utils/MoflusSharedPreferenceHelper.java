package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils;

import android.content.Context;
import android.content.SharedPreferences;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;

/**
 * Created by ranuwp on 3/10/2017.
 */

/**
 * Kelas yang mengatur sharedpreference pada moflus
 */
public class MoflusSharedPreferenceHelper {
    public static String NAME = "MOFLUS_SHARED_PREFERENCE";
    public static String START_DELAY = "START_DELAY";
    public static String TIME_DURATION = "TIME_DURATION";
    public static String TIME_INTERVAL = "TIME_INTERVAL";
    public static String PIXEL_SIZE = "PIXEL_SIZE";
    public static String COLOR_TYPE = "COLORTYPE";
    public static String UID = "UID";
    public static String USERNAME = "USERNAME";
    public static String IS_LOGIN = "IS_LOGIN";

    /**
     * Mendapatkan sharepreference pada moflus
     * @param context context dari activity yang sedang berjalan
     * @return sharefpreference moflus
     */
    public static SharedPreferences getMoFluSSharedPreference(Context context){
        return context.getSharedPreferences(NAME,Context.MODE_PRIVATE);
    }

    /**
     * Mendapatkan detail pengguna yang terdapat pada sharedpreference
     * @param context context dari activity yang sedang berjalan
     * @return user yang sedang login
     */
    public static User getUser(Context context){
        User user = new User();
        SharedPreferences sp = MoflusSharedPreferenceHelper.getMoFluSSharedPreference(context);
        user.setUsername(sp.getString(USERNAME,""));
        user.setUid(sp.getString(UID,""));
        user.setPassword("");
        return user;
    }
}
