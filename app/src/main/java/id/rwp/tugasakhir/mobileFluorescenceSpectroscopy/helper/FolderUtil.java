package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Image;

/**
 * Created by ranuwp on 9/27/2017.
 */

/**
 * Kelas yang membantu dalam pengelolaan folder
 */
public class FolderUtil {

    private static final String PARENT_DIRECTORY = "MoFluS/Extended/";
    private static final String CSV_DIRECTORY = "MoFluS/CSV/";
    private static final String UPLOAD_DIRECTORY = "MoFluS/Upload/";

    /**
     * Mendapatkan lokasi file utama
     * @return file yang dibuat
     */
    public static File getParentDirectrory(){
        return getDirectory(PARENT_DIRECTORY);
    }

    /**
     * Mendapatkan lokasi upload file
     * @return file yang dibuat
     */
    public static File getUploadDirectory(){
        return getDirectory(UPLOAD_DIRECTORY);
    }

    /**
     * MendapatkanLokasi file berdasarkan lokasi
     * @param path lokasi yang akan digunakan
     * @return file dengan lokasi yang dimasukan
     */
    private static File getDirectory(String path){
        File file = new File(Environment.getExternalStorageDirectory(),path);
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d("MoFluSDirectory","This Device Cannot Create External Directory");
                return null;
            }
        }
        return file;
    }

    /**
     * Mendapatkan semua folder pada bacth data
     * @return semua folder pada batch data
     */
    public static List<Folder> getAllFolder(){
        ArrayList<Folder> folders = new ArrayList<>();
        for(File file : getDirectory(PARENT_DIRECTORY).listFiles()){
            Folder folder = new Folder();
            folder.setPath(file.getPath());
            folders.add(folder);
        }
        return folders;
    }

    /**
     * Menambahkan folder pada batch data
     * @param name nama folder
     * @return folder yang telah dibuat
     */
    public static Folder addFolder(String name){
        Folder folder = new Folder();
        File file = new File(Environment.getExternalStorageDirectory(),"MoFluS/Extended/"+name);
        folder.setPath(file.getPath());
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d("RWP","Cannot Create Folder");
                return null;
            }else{
                return folder;
            }
        }else{
            return folder;
        }
    }

    /**
     * Menghapus folder
     * @param file file dari folder
     */
    public static void deleteFolder(File file){
        for(File content : file.listFiles()){
            content.delete();
        }
        file.delete();
    }

    /**
     * MEnghapus isi dari folder
     * @param file folder yang memiliki isi
     */
    public static void deleteFolderContent(File file){
        for(File content : file.listFiles()){
            content.delete();
        }
    }

    /**
     * Mendapatkan semua gambar yang ada pada folder
     * @param folder file dari folder
     * @return semua gambar pada folder
     */
    public static List<Image> getImages(File folder){
        List<Image> images = new ArrayList<>();
        for(File file : folder.listFiles()){
            Image image = new Image();
            image.setFile(file);
            images.add(image);
        }
        return images;
    }

    /**
     * Melakukan copy pada file
     * @param sourceFile file asli
     * @param destFile lokasi file saat dicopy
     * @throws IOException jika proses copy file gagal
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException{
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * Menghapus gambar
     * @param image gambar yang akan dihapus
     */
    public static void deleteImage(Image image){
        image.getFile().delete();
    }

}
