package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

/**
 * Created by ranuwp on 3/9/2017.
 */

/**
 * Kelas untuk menyimpan detectionmethodcolor
 */
public class DetectionMethodColor {
    private int id;
    private String color_type;
    private boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor_type() {
        return color_type;
    }

    public void setColor_type(String color_type) {
        this.color_type = color_type;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }
}
