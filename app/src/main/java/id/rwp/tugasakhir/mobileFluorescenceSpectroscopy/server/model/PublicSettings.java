package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel.JDataResult;

/**
 * Created by ranuwp on 10/5/2017.
 */

/**
 * Kelas yang menyimpan publicsettings yang dapat digunakan pada server
 */
public class PublicSettings {

    @SerializedName("uid")
    private String uid;

    @SerializedName("username")
    private String username;

    @SerializedName("success")
    private boolean success;

    @SerializedName("classification_method")
    private List<ClassificationMethod> classificationMethods;

    @SerializedName("detection_method_color")
    private List<DetectionMethodColor> detectionMethodColors;

    @SerializedName("microrna")
    private List<MicroRNA> microRNAs;

    @SerializedName("dots")
    private List<Dots> dots;

    @SerializedName("data_result")
    private List<JDataResult> dataResults;

    public List<ClassificationMethod> getClassificationMethods() {
        return classificationMethods;
    }

    public void setClassificationMethods(List<ClassificationMethod> classificationMethods) {
        this.classificationMethods = classificationMethods;
    }

    public List<DetectionMethodColor> getDetectionMethodColors() {
        return detectionMethodColors;
    }

    public void setDetectionMethodColors(List<DetectionMethodColor> detectionMethodColors) {
        this.detectionMethodColors = detectionMethodColors;
    }

    public List<MicroRNA> getMicroRNAs() {
        return microRNAs;
    }

    public void setMicroRNAs(List<MicroRNA> microRNAs) {
        this.microRNAs = microRNAs;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<JDataResult> getDataResults() {
        return dataResults;
    }

    public void setDataResults(List<JDataResult> dataResults) {
        this.dataResults = dataResults;
    }

    public List<Dots> getDots() {
        return dots;
    }

    public void setDots(List<Dots> dots) {
        this.dots = dots;
    }
}
