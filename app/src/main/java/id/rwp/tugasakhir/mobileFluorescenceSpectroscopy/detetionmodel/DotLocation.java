package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;

/**
 * Created by ranuwp on 3/16/2017.
 */

/**
 * Kelas untuk menyimpan dotlocation
 */
public class DotLocation {
    private int id;
    private MicroRNA microRNA;
    private int x;
    private int y;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MicroRNA getMicroRNA() {
        return microRNA;
    }

    public void setMicroRNA(MicroRNA microRNA) {
        this.microRNA = microRNA;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
