3package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.ColorDetectionHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.RGBValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.FolderUtil;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Image;

/**
 * Created by ranuwp on 11/9/2017.
 */

/**
 * Le;as untuk menyimpan folderdataset
 */
public class FolderDataSet {

    @SerializedName("images")
    private List<ImageDataSet> imageDataSets;

    @SerializedName("folder_name")
    private String folderName;

    /**
     * Konstruktor yang sekaligus untuk mengambil nilai dari masing-masing gambar
     * @param folder folder yang digunakan
     * @param dotLocations lokasi dots yang digunakan
     * @param detectionMethodColors detectionmethodcolor yang digunakan
     * @param pixelSize ukuran piksel yang digunakan
     */
    public FolderDataSet(Folder folder, List<DotLocation> dotLocations, ArrayList<DetectionMethodColor> detectionMethodColors, int pixelSize) {
        folderName = folder.getFolderName();
        imageDataSets = new ArrayList<>();
        List<Image> images = FolderUtil.getImages(folder.getFile());
        for(Image image : images){
            Bitmap bitmap = BitmapHelper.getBitmapFromFile(image.getFile());
            ImageDataSet imageDataSet = new ImageDataSet();
            ArrayList<ValuesDataSet> valuesDataSets = new ArrayList<>();
            for(DotLocation dotLocation : dotLocations){
                Bitmap bittemp = BitmapHelper.getRealBitmapLivePreview(bitmap,dotLocation,pixelSize);
                ArrayList<RGBValue> rgbValues = ColorDetectionHelper.getRGBValueClassArray(bittemp);
                double value = 0;
                for(RGBValue rgbValue : rgbValues){
                    value = rgbValue.getColorValue(detectionMethodColors);
                }
                ValuesDataSet valuesDataSet = new ValuesDataSet();
                valuesDataSet.setMicroRNAName(dotLocation.getMicroRNA().getName());
                valuesDataSet.setValue(value);
                valuesDataSets.add(valuesDataSet);
                bittemp.recycle();
            }
            imageDataSet.setValuesDataSets(valuesDataSets);
            imageDataSet.setPath(image.getFile().getPath());
            imageDataSets.add(imageDataSet);
            bitmap.recycle();
        }
    }

    public List<ImageDataSet> getImageDataSets() {
        return imageDataSets;
    }

    public void setImageDataSets(List<ImageDataSet> imageDataSets) {
        this.imageDataSets = imageDataSets;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
