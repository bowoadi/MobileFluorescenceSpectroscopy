package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json status
 */
public class Status {

    public boolean success;
    public String imageLink;
    public String info;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
