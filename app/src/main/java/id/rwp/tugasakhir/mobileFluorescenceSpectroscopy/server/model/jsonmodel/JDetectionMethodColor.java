package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json detectionmethodcolor
 */
public class JDetectionMethodColor {

    private String color_type;
    private int id;
    private boolean selected;

    public String getColor_type() {
        return color_type;
    }

    public void setColor_type(String color_type) {
        this.color_type = color_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
