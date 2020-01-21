package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.ColorDetectionHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.viewholder.GraphDataViewHolder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas adapter yang menangani list graph data pada detection
 */
public class GraphDataAdapter extends RecyclerView.Adapter<GraphDataViewHolder> implements View.OnClickListener{

    /**
     * Interface saat tampilan graphdata ditekan
     */
    public interface Listener{
        /**
         * Fungsi saat tampilan graphdata dipilih
         * @param position
         */
        void onChangeSelected(int position);
    }

    private Context context;
    private List<GraphData> listGraphData;
    private int selected_graphview;
    private long duration;
    private Listener listener;

    /**
     * Konstruktor GraphDataAdapter
     * @param context context dari activity yang menggunakan adapter ini
     * @param listGraphData list dari GraphData yang akan ditampilkan
     * @param duration durasi yang digunakan
     * @param listener listener yang mengatur saat view ditekan
     */
    public GraphDataAdapter(Context context, List<GraphData> listGraphData, long duration, Listener listener) {
        this.context = context;
        selected_graphview = 0;
        this.duration = duration;
        this.listGraphData = listGraphData;
        this.listener = listener;
    }

    /**
     * Fungsi untuk membuat tampilan untuk masin-masing graphdata
     * @param parent view parent dari graphdata
     * @param viewType tipe view yang ditampilkan
     * @return GraphDataViewHolder yang akan ditampilkan
     */
    @Override
    public GraphDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.graph_data,parent,false);
        return new GraphDataViewHolder(view);
    }

    /**
     * Fungsi untuk mengatur tampilan GraphDataViewHolder saat ditampilkan
     * @param holder GraphDataViewHolder yang ditampilkan
     * @param position posisi dari GraphDataViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(GraphDataViewHolder holder, int position) {
        final GraphData graphData = listGraphData.get(position);
        holder.getSelected_graph().setChecked(graphData.isSelected_graph_data());
        holder.getSelected_graph().setOnClickListener(this);
        holder.getSelected_graph().setTag(position);
        holder.getGraphview().removeAllSeries();
        graphData.setGraphView(holder.getGraphview(),(int)((duration)/1000)+3);
        holder.getGraphview().addSeries(graphData.getLineGraphSeries());
        try{
            holder.getPreview_graph_data_value().setImageBitmap(graphData.getLive_preview());
            holder.getPreview_graph_data_value().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Bitmap bitmap = graphData.getLive_preview();
                    String RGBValue = ColorDetectionHelper.getRGBValueTest(bitmap);
                    String HSVValue = ColorDetectionHelper.getHSVValueTest(bitmap);
                    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MoFluS"+File.separator+"Image Testing";
                    String fileName = (new Date()).getTime()+" "+RGBValue+"|"+HSVValue+".png";
                    String filePath = baseDir+File.separator+fileName;
                    File file = new File(filePath);
                    if(file.exists()){
                        file.delete();
                    }
                    try{
                        file.getParentFile().mkdirs();
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                        out.flush();
                        out.close();
                    }catch (Exception e){
                        Log.d("Image Save Error", e.getMessage());
                    }
                    Toast.makeText(view.getContext(),"Image Test Saved",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }catch (NullPointerException e){
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            int scale = context.getSharedPreferences(MoflusSharedPreferenceHelper.NAME,Context.MODE_PRIVATE).getInt(MoflusSharedPreferenceHelper.PIXEL_SIZE,1);
            holder.getPreview_graph_data_value().setImageBitmap(Bitmap.createBitmap(scale,scale,config));
        }
        holder.getMicrorna_name().setText(graphData.getMicroRNA().getName());
    }

    /**
     * Fungsi yang mengembalikan total dari graphdata yang ditampilkan
     * @return jumlah graphdata yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return listGraphData.size();
    }

    /**
     * Fungsi untuk dijalankan saat user menekan view
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.selected_graph :
                selectGraph(view);
                break;
        }
    }

    /**
     * Mengubah graphdata yang dipilih
     * @param view view dari graphdata yang dipilih
     */
    private void selectGraph(View view){
        int new_selected_graphview = Integer.parseInt(view.getTag().toString());
        listener.onChangeSelected(new_selected_graphview);
        listGraphData.get(selected_graphview).setSelected_graph_data(false);
        listGraphData.get(new_selected_graphview).setSelected_graph_data(true);
        selected_graphview = new_selected_graphview;
        notifyDataSetChanged();
    }

    public int getSelected_graphview() {
        return selected_graphview;
    }

}
