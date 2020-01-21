package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import java.util.List;

/**
 * Created by ranuwp on 3/14/2017.
 */

/**
 * Kelas settings untuk kamera yang digunakan pada surfaceview
 */
public class CameraSettings {
    public static int PREVIEW_WIDTH = 320;
    public static int PREVIEW_HEIGHT = 240;
    public static int PICTURE_WIDTH = 1280;
    public static int PICTURE_HEIGHT = 720;
    public static final int IMAGE_FORMAT = ImageFormat.NV21;

    /**
     * Mengatur parameter pada kamera yang digunakan
     * @param camera
     */
    public void setParameters(Camera camera){
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        List<Camera.Size> previews = parameters.getSupportedPreviewSizes();
        for(Camera.Size previewSize : previews){
            double v = (double) previewSize.width/previewSize.height;
            if(v==1.3333333333333333){
                PREVIEW_WIDTH = previewSize.width;
                PREVIEW_HEIGHT = previewSize.height;
                break;
            }
        }
        parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        parameters.setJpegQuality(100);
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        for(Camera.Size imageSize : pictureSizes){
            double v = (double) imageSize.width/imageSize.height;
            if(v==1.3333333333333333){
                PICTURE_WIDTH = imageSize.width;
                PICTURE_HEIGHT = imageSize.height;
                break;
            }
        }
        parameters.setPictureSize(PICTURE_WIDTH,PICTURE_HEIGHT);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();
    }
}
