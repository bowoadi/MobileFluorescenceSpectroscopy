package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

/**
 * Created by ranuwp on 3/4/2017.
 */

public class MicroRNA {
    private int id;
    private String name;
    private boolean used;

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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
