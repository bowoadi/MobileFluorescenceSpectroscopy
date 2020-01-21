package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json dataresult
 */
public class JDataResult {

    @SerializedName("datetime")
    @Expose
    private String datetime;

    @SerializedName("id")
    @Expose
    private int id;

    private String name;

    private String preview_picture_url;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreview_picture_url() {
        return preview_picture_url;
    }

    public void setPreview_picture_url(String preview_picture_url) {
        this.preview_picture_url = preview_picture_url;
    }

    public List<JDetectionMethodColor> getDetection_method_color_id() {
        return detection_method_color_id;
    }

    public void setDetection_method_color_id(List<JDetectionMethodColor> detection_method_color_id) {
        this.detection_method_color_id = detection_method_color_id;
    }

    public List<JGraphData> getGraph_data() {
        return graph_data;
    }

    public void setGraph_data(List<JGraphData> graph_data) {
        this.graph_data = graph_data;
    }

    public List<JDetectionMethodColor> detection_method_color_id;

    public List<JGraphData> graph_data;


}
