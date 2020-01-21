package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ranuwp on 12/6/2016.
 */

/**
 * Kelas yang membantu dalam melakukan format tanggal
 */
public class DateHelper {
    public static final SimpleDateFormat SQLITE_FORMAT_ALL = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
    public static final SimpleDateFormat SQLITE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat SQLITE_FORMAT_TIME = new SimpleDateFormat("HH:mm:SS", Locale.getDefault());
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("HH:mm, dd MMM yyyy ", Locale.getDefault());
    public static final SimpleDateFormat FULLDATETIME_FORMAT = new SimpleDateFormat("HH:mm:ss, dd MMM yyyy ", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault());
}
