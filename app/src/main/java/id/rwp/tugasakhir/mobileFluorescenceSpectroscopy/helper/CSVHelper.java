package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;

/**
 * Created by ranuwp on 9/25/2016.
 */

/**
 * Kelas yang membantu dalam pengolahan file csv
 */
public class CSVHelper {

    /**
     * membuat file csv berdasarkan dataresult
     * @param dataResult dataresult yang akan dibuat file csv
     * @return alamat file yang telah dibuat
     * @throws IOException jika file tidak dapat dibuat
     */
    public static String createFile(DataResult dataResult) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MoFluS";
        String fileName = dataResult.getId()+".csv";
        String filePath = baseDir+File.separator+fileName;
        File file = new File(filePath);
        if(file.exists()){
            return filePath;
        }
        List<String[]> text = new ArrayList<>();
        String[] header = new String[dataResult.getGraphDatas().size()+1];
        header[0] = "time 'ms'";
        for(int i = 1; i <= dataResult.getGraphDatas().size(); i++ ){
            header[i] = dataResult.getGraphDatas().get(i-1).getMicroRNA().getName();
        }
        text.add(header);
        for(int i = 0; i< dataResult.getGraphDatas().get(0).getGraphDataValues().size();i++){
            String[] row = new String[dataResult.getGraphDatas().size()+1];
            row[0] = String.valueOf(dataResult.getGraphDatas().get(0).getGraphDataValues().get(i).getTime());
            for(int j =1; j<= dataResult.getGraphDatas().size();j++){
                row[j] = String.valueOf(dataResult.getGraphDatas().get(j-1).getGraphDataValues().get(i).getY());
            }
            text.add(row);
        }
        file.getParentFile().mkdirs();
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        writer.writeAll(text);
        writer.close();
        return filePath;
    }

    /**
     * Menghapus file csv berdasarkan dataresult yang digunakan
     * @param dataResult dataresult yang akan dihapus file csvnya
     * @return true jika berhasil, false jika gagal
     * @throws IOException jika file tidak dapat dihapus
     */
    public static boolean deleteFile(DataResult dataResult) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MoFluS";
        String fileName = dataResult.getId()+".csv";
        String filePath = baseDir+File.separator+fileName;
        File file = new File(filePath);
        return file.delete();
    }
}
