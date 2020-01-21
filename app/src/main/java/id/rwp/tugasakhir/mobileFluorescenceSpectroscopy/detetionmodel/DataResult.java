package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas untuk menyimpan dataresult
 */
public class DataResult {
    private int id;
    private String name;
    private Bitmap preview_picture;
    private String preview_picture_url;
    private Date datetime;
    private boolean is_clouded;
    private ArrayList<DetectionMethodColor> detectionMethodColors;
    private ArrayList<GraphData> graphDatas;
    private ArrayList<CloudComputingResult> cloudComputingResults;

    /**
     * Konstruktor dataresult
     */
    public DataResult() {
        this.is_clouded = false;
        this.graphDatas = new ArrayList<>();
        this.cloudComputingResults = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DetectionMethodColor> getDetectionMethodColors() {
        return detectionMethodColors;
    }

    public int getDetectionMethodColorId(){
        String id = "";
        for(DetectionMethodColor detectionMethodColor : detectionMethodColors){
            id += String.valueOf(detectionMethodColor.getId());
        }
        if(id.equals("")) {
            return -1;
        }else{
            return Integer.parseInt(id);
        }
    }

    public String getDetectionColorMethodText(){
        String res = "";
        for(DetectionMethodColor detectionMethodColor : detectionMethodColors){
            res += detectionMethodColor.getColor_type().split("")[1];
        }
        return res;
    }

    public void setDetectionMethodColors(ArrayList<DetectionMethodColor> detectionMethodColors) {
        this.detectionMethodColors = detectionMethodColors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getPreview_picture() {
        return preview_picture;
    }

    public void setPreview_picture(Bitmap preview_picture) {
        this.preview_picture = preview_picture;
    }

    public String getPreview_picture_url() {
        return preview_picture_url;
    }

    public void setPreview_picture_url(String preview_picture_url) {
        this.preview_picture_url = preview_picture_url;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public boolean isIs_clouded() {
        return is_clouded;
    }

    public void setIs_clouded(boolean is_clouded) {
        this.is_clouded = is_clouded;
    }

    public ArrayList<GraphData> getGraphDatas() {
        return graphDatas;
    }

    public void setGraphDatas(ArrayList<GraphData> graphDatas) {
        this.graphDatas = graphDatas;
    }


}
