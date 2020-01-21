package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas yang mengatur tampilan pada GraphDataViewHolder
 */
public class GraphDataViewHolder extends RecyclerView.ViewHolder {

    private RadioButton selected_graph;
    private ImageView preview_graph_data_value;
    private TextView microrna_name;
    private GraphView graphview;

    /**
     * Konstruktor GraphDataViewHolder
     * @param itemView view yang telah dibuat
     */
    public GraphDataViewHolder(View itemView) {
        super(itemView);
        selected_graph = (RadioButton) itemView.findViewById(R.id.selected_graph);
        preview_graph_data_value = (ImageView) itemView.findViewById(R.id.preview_graph_data_value);
        microrna_name = (TextView) itemView.findViewById(R.id.microrna_name);
        graphview = (GraphView) itemView.findViewById(R.id.graphview);
    }

    public RadioButton getSelected_graph() {
        return selected_graph;
    }

    public ImageView getPreview_graph_data_value() {
        return preview_graph_data_value;
    }

    public TextView getMicrorna_name() {
        return microrna_name;
    }

    public GraphView getGraphview() {
        return graphview;
    }

    public void setGraphview(GraphView graphview) {
        this.graphview = graphview;
    }

}
