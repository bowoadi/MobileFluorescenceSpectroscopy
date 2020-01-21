package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;

/**
 * Created by ranuwp on 11/8/2017.
 */

/**
 * Kelas yang digunakan untuk menyimpan dataresult yang akan disimpan pada server
 */
public class SaveDataResult {

    @SerializedName("uid")
    public String uid;
    @SerializedName("data_result")
    public Map<String, Object> dataResult;

    /**
     * Merubah dataresult ke format json
     * @param dataResult
     */
    public void setDataResult(DataResult dataResult) {
        Map<String,Object> data_result = new HashMap<>();
        data_result.put("id", dataResult.getId());
        data_result.put("name", dataResult.getName());
        data_result.put("preview_picture_url",String.valueOf("http://172.104.188.209/f/img/"+uid+"/"+dataResult.getId()+".jpg"));
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
        this.dataResult = data_result;
//        JDataResult data_result = new JDataResult();
//        dataResult.setId(data_result.getId());
//        dataResult.setName(data_result.getName());
//        dataResult.setPreview_picture_url(data_result.getPreview_picture_url());
//        try {
//            dataResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(data_result.getDatetime()));
//        } catch (ParseException e) {
//            dataResult.setDatetime(new Date());
//        }
//        dataResult.setIs_clouded(true);
//        ArrayList<DetectionMethodColor> detectionMethodColors = new ArrayList<>();
//        for(JDetectionMethodColor jDetectionMethodColor : data_result.getDetection_method_color_id()){
//            DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
//            detectionMethodColor.setId(jDetectionMethodColor.getId());
//            detectionMethodColor.setColor_type(jDetectionMethodColor.getColor_type());
//            detectionMethodColor.setSelected(jDetectionMethodColor.selected);
//            detectionMethodColors.add(detectionMethodColor);
//        }
//        dataResult.setDetectionMethodColors(detectionMethodColors);
//        ArrayList graph_data_list = new ArrayList();
//        for(JGraphData jGraphData : data_result.getGraph_data()){
//            GraphData graphData = new GraphData();
//            graphData.setId(jGraphData.getId());
//            graphData.setMicroRNA(moflusSQLiteHelper.getMicroRNA(jGraphData.getMicrorna_id()));
//            ArrayList graph_data_value_list = new ArrayList();
//            for(JGraphDataValue jGraphDataValue : jGraphData.getGraph_data_value()){
//                GraphDataValue graphDataValue = new GraphDataValue(jGraphDataValue.getTime(),jGraphDataValue.getValue());
//                graphDataValue.setId(jGraphDataValue.getId());
//                graph_data_value_list.add(graphDataValue);
//            }
//            graphData.setGraphDataValues(graph_data_value_list);
//            graph_data_list.add(graphData);
//        }
//        dataResult.setGraphDatas(graph_data_list);
//        this.dataResult = data_result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getDataResult() {
        return dataResult;
    }
}
