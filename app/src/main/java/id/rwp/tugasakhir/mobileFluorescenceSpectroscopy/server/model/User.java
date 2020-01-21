package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ranuwp on 10/5/2017.
 */

/**
 * Kelas untuk menyimpan gson dari json user
 */
public class User {

    @SerializedName("uid")
    private String uid;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("success")
    private boolean success;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
