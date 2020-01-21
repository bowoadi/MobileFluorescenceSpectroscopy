package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ranuwp on 9/27/2017.
 */

/**
 * Kelas untuk menyimpan folder
 */
public class Folder implements Parcelable {

    private String path;
    private boolean selected;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName(){
        String[] a = path.split("/");
        return a[a.length-1];
    }

    public File getFile(){
        return new File(path);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
    }

    public Folder() {
    }

    protected Folder(Parcel in) {
        this.path = in.readString();
    }

    public static final Parcelable.Creator<Folder> CREATOR = new Parcelable.Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel source) {
            return new Folder(source);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
}
