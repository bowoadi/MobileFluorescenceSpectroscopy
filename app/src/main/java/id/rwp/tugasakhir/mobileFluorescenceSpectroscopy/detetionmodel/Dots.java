package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import java.util.ArrayList;

/**
 * Created by ranuwp on 8/18/2017.
 */

/**
 * Kelas untuk menyimpan dots
 */
public class Dots {
    private int id;
    private String name;
    private ArrayList<DotLocation> dotLocations;
    private int scale;

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

    public ArrayList<DotLocation> getDotLocations() {
        return dotLocations;
    }

    public void setDotLocations(ArrayList<DotLocation> dotLocations) {
        this.dotLocations = dotLocations;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
