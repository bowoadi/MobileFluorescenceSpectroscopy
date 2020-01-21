package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by ranuwp on 3/9/2017.
 */

/**
 * Kelas untuk menyimpan graphdatavalue
 */
public class GraphDataValue extends DataPoint {
    private int id;
    private double time;
    private double value;

    /**
     * Konstruktor graphdatavalue
     * @param time waktu pengambilan data
     * @param value nilai warna
     */
    public GraphDataValue(double time, double value) {
        super(time/1000, value);
        this.time = time;
        this.value = value;
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

}
