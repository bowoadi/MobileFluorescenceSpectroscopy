package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import java.util.Date;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;

/**
 * Created by ranuwp on 3/9/2017.
 */

/**
 * Kelas yang menyimpan cloudcomputingresult
 */
public class CloudComputingResult {
    private int id;
    private ClassificationMethod classificationMethod;
    private Date datetime;
    private String result;
    private int process;

    /**
     * Konstruktor cloudomputingresult
     */
    public CloudComputingResult() {
        process = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClassificationMethod getClassificationMethod() {
        return classificationMethod;
    }

    public void setClassificationMethod(ClassificationMethod classificationMethod) {
        this.classificationMethod = classificationMethod;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
