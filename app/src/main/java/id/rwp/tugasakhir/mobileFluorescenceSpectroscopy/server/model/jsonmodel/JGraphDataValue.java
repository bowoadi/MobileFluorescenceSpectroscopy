package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json graphdatavalue
 */
public class JGraphDataValue {

    private int graph_data_id;

    private int id;

    private double time;

    private double value;

    public int getGraph_data_id() {
        return graph_data_id;
    }

    public void setGraph_data_id(int graph_data_id) {
        this.graph_data_id = graph_data_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
