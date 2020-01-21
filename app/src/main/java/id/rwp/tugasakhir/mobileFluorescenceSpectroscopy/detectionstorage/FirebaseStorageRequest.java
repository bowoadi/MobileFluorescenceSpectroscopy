package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;

/**
 * Created by ranuwp on 8/9/2017.
 */

/**
 * Kelas tidak digunakan pada moflus yang baru
 */
public class FirebaseStorageRequest {

    private DataResult dataResult;
    private FirebaseUserHelper firebaseUserHelper;
    private StorageListener storageListener;

    public FirebaseStorageRequest(DataResult dataResult, StorageListener storageListener) {
        this.dataResult = dataResult;
        this.firebaseUserHelper = new FirebaseUserHelper();
        this.storageListener = storageListener;
    }

    public void saveToCloud(){
        UploadTask uploadTask = firebaseUserHelper.getStorageReference().child(String.valueOf(dataResult.getId())).putBytes(BitmapHelper.getBytes(dataResult.getPreview_picture()));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                dataResult.setPreview_picture_url(String.valueOf(url));
                //Update
                DatabaseReference databaseReference = firebaseUserHelper.getFirebaseDataResult().child(String.valueOf(dataResult.getId()));
                Map<String,Object> data_result = new HashMap<>();
                data_result.put("id", dataResult.getId());
                data_result.put("name", dataResult.getName());
                data_result.put("preview_picture_url",String.valueOf(url));
                data_result.put("datetime", DateHelper.SQLITE_FORMAT_ALL.format(dataResult.getDatetime()));
                data_result.put("detection_method_color_id",dataResult.getDetectionMethodColors());
                List graph_data_list = new ArrayList();
                for(GraphData graphData : dataResult.getGraphDatas()){
                    Map<String,Object> graph_data = new HashMap<>();
                    graph_data.put("id",graphData.getId());
                    graph_data.put("microrna_id",graphData.getMicroRNA().getId());
                    graph_data.put("data_result_id",dataResult.getId());
                    List graph_data_value_list = new ArrayList();
                    for(GraphDataValue graphDataValue : graphData.getGraphDataValues()){
                        Map<String,Object> graph_data_value = new HashMap<>();
                        graph_data_value.put("id",graphDataValue.getId());
                        graph_data_value.put("graph_data_id",graphData.getId());
                        graph_data_value.put("time",graphDataValue.getTime());
                        graph_data_value.put("value",graphDataValue.getValue());
                        graph_data_value_list.add(graph_data_value);
                    }
                    graph_data.put("graph_data_value",graph_data_value_list);
                    graph_data_list.add(graph_data);
                }
                data_result.put("graph_data",graph_data_list);
                databaseReference.setValue(data_result, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null){
                            dataResult.setIs_clouded(true);
                            storageListener.onSuccess(dataResult);
                        }else{
                            storageListener.onFailed(databaseError.getMessage());
                        }
                    }


                });
            }
        });
    }
}
