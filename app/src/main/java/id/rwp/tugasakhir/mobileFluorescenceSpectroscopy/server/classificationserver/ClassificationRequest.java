package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.classificationserver;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;

/**
 * Created by ranuwp on 8/9/2017.
 */

/**
 * Kelas untuk melakukan request classification
 */
public class ClassificationRequest {

    private ClassificationListener classificationListener;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private double fromTime=-1;
    private double toTime=-1;

    /**
     * Konstruktor ClassificationRequest
     * @param context context dari activity yang berjalan
     * @param url url yang digunakan
     * @param dataResult dataresult yang akan diproses
     * @param classificationMethod classificationmethod yang digunakan
     * @param classificationListener listener dari classificationlistener
     */
    public ClassificationRequest(Context context, String url, DataResult dataResult, ClassificationMethod classificationMethod, final ClassificationListener classificationListener) {
        this.classificationListener = classificationListener;
        this.requestQueue = Volley.newRequestQueue(context);
        try {
            JSONObject params = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            ArrayList<GraphData> selectedGraphData = dataResult.getGraphDatas();
            for (GraphData graphData : selectedGraphData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("microrna_name", graphData.getMicroRNA().getName());
                double value = 0;
                if(fromTime != -1 || toTime!=-1){
                    for (GraphDataValue graphDataValue : graphData.getGraphDataValues(fromTime,toTime)) {
                        value += graphDataValue.getValue();
                    }
                }else{
                    for (GraphDataValue graphDataValue : graphData.getGraphDataValues()) {
                        value += graphDataValue.getValue();
                    }
                }
                value /= graphData.getGraphDataValues().size();
                jsonObject.put("value", value);
                jsonArray.put(jsonObject);
            }
            params.put("data", jsonArray);
            params.put("classification_method", classificationMethod.getName());
            Gson gson = new Gson();
            Log.d("RWPJSON", params.toString());
            //params.
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    classificationListener.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    classificationListener.onFailed(error.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menentukan waktu awal dan akhir dari data yang digunakan pada dataresult
     * @param from waktu awal
     * @param to waktu akhir
     */
    public void setDataResultFromToTime(double from, double to){
        this.fromTime = from;
        this.toTime = to;
    }

    /**
     * Memulai proses classification dengan mengirimkan ke url yang telah ditentukan
     */
    public void startClassification() {
        requestQueue.add(jsonObjectRequest);
    }
}
