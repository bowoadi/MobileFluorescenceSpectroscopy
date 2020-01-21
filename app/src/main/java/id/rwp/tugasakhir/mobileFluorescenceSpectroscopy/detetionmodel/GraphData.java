package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranuwp on 3/9/2017.
 */

/**
 * Kelas untuk menyimpan graphdata
 */
public class GraphData {
    private int id;
    private MicroRNA microRNA;
    private ArrayList<GraphDataValue> graphDataValues;
    private boolean selected_graph_data;
    private GraphView graphView;
    private LineGraphSeries lineGraphSeries;
    private Bitmap live_preview;

    public GraphData(){
        graphDataValues = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MicroRNA getMicroRNA() {
        return microRNA;
    }

    public void setMicroRNA(MicroRNA microRNA) {
        this.microRNA = microRNA;
    }

    public ArrayList<GraphDataValue> getGraphDataValues() {
        return graphDataValues;
    }

    public List<GraphDataValue> getGraphDataValues(double from, double to) {
        if(graphDataValues.size() <= 1){
            return graphDataValues;
        }
        int fromIndex = 0;
        int toIndex = getGraphDataValues().size()-1;
        for(int i = 0; i < graphDataValues.size();i++){
            if(graphDataValues.get(i).getX() == from){
                fromIndex=i;
            }
            if(graphDataValues.get(i).getX() == to){
                toIndex=i;
            }
        }
        return graphDataValues.subList(fromIndex,toIndex);
    }

    public void setGraphDataValues(ArrayList<GraphDataValue> graphDataValues) {
        this.graphDataValues = graphDataValues;
    }

    public boolean isSelected_graph_data() {
        return selected_graph_data;
    }

    public void setSelected_graph_data(boolean selected_graph_data) {
        this.selected_graph_data = selected_graph_data;
    }

    public LineGraphSeries getLineGraphSeries() {
        return lineGraphSeries;
    }

    public void setLineGraphSeries(LineGraphSeries lineGraphSeries) {
        lineGraphSeries.setTitle("Value");
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(5);
        lineGraphSeries.setThickness(4);
        lineGraphSeries.setColor(Color.RED);
        this.lineGraphSeries = lineGraphSeries;
    }

    public void setGraphView(GraphView graphView, int maxX) {
        this.graphView = graphView;
        graphView.addSeries(lineGraphSeries);
        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(maxX+1);
        graphView.getViewport().setMaxY(800);
    }

    public Bitmap getLive_preview() {
        return live_preview;
    }

    public void setLive_preview(Bitmap live_preview) {
        this.live_preview = live_preview;
    }
}
