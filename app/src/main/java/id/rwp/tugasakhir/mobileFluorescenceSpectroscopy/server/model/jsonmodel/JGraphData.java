package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel;

import java.util.List;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * kelas untuk menyimpan gson dari json graphdata
 */
public class JGraphData {

    private int data_result_id;

    private int id;

    private int microrna_id;

    public int getData_result_id() {
        return data_result_id;
    }

    public void setData_result_id(int data_result_id) {
        this.data_result_id = data_result_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMicrorna_id() {
        return microrna_id;
    }

    public void setMicrorna_id(int microrna_id) {
        this.microrna_id = microrna_id;
    }

    public List<JGraphDataValue> getGraph_data_value() {
        return graph_data_value;
    }

    public void setGraph_data_value(List<JGraphDataValue> graph_data_value) {
        this.graph_data_value = graph_data_value;
    }

    public List<JGraphDataValue> graph_data_value;

}
