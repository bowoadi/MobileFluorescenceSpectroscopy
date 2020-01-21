package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.CloudComputingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult.viewholder.CloudComputingResultViewHolder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas adapter yang menangani list CloudComputing pada detaildataresult
 */
public class CloudComputingResultAdapter extends RecyclerView.Adapter<CloudComputingResultViewHolder> implements View.OnClickListener{

    private Context context;
    private List<CloudComputingResult> listCloudComputingResult;
    private MoflusSQLiteHelper moflusSQLiteHelper;

    /**
     * Konstruktor CloudComputingResultAdapter
     * @param context context dari activity yang menggunaan adapter ini
     * @param listCloudComputingResult list dari cloudcomputingresult yang akan ditampilkan
     */
    public CloudComputingResultAdapter(Context context, List<CloudComputingResult> listCloudComputingResult) {
        this.context = context;
        this.listCloudComputingResult = listCloudComputingResult;
        this.moflusSQLiteHelper = new MoflusSQLiteHelper(context);
    }

    /**
     * Fungsi untuk membuat tampilan untuk masing-masing cloudcomputingresult
     * @param parent view parent dari cloudcomputinggresult
     * @param viewType tipe view yang ditampilkan
     * @return CloudComputingResultViewHolder yang akan ditampilkan
     */
    @Override
    public CloudComputingResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cloud_computing_result,parent,false);
        return new CloudComputingResultViewHolder(view);
    }

    /**
     * Fungsi untuk mengatur tampilan CloudComputingResultViewHolder saat ditampilkan
     * @param holder CloudComputingResultViewHolder yang ditampilkan
     * @param position posisi dari CloudComputingResultViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(CloudComputingResultViewHolder holder, int position) {
        CloudComputingResult cloudComputingResult = listCloudComputingResult.get(position);
        holder.getDate_time_text().setText(DateHelper.FULLDATETIME_FORMAT.format(cloudComputingResult.getDatetime()));
        holder.getResult().setText(cloudComputingResult.getResult());
        holder.getClassification_method_textview().setText(cloudComputingResult.getClassificationMethod().getName());
        switch (cloudComputingResult.getProcess()){
            case -1 :
                holder.getProcess_imageview().setImageResource(R.drawable.ic_sync_disabled);
                break;
            case 0 :
                holder.getProcess_imageview().setImageResource(R.drawable.ic_hourglass_empty);
                break;
            case 1 :
                holder.getProcess_imageview().setImageResource(R.drawable.ic_done);
                break;
        }
        holder.getPopup_button().setOnClickListener(this);
        holder.getPopup_button().setTag(cloudComputingResult);
    }

    /**
     * Fungsi yang mengembalikan total dari cloudcomputingresult yang ditampilkan
     * @return jumlah cloudcomputingresult yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return listCloudComputingResult.size();
    }

    /**
     * Fungsi yang dipanggil saat user menekan view
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.popup_button:
                showPopup(view);
                break;
        }
    }

    /**
     * Menampilkan popup pada tampilan cloudcomputingresult
     * @param view view dari cloudcomputingresult
     */
    private void showPopup(View view){
        PopupMenu popupMenu = new PopupMenu(context,view);
        final CloudComputingResult cloudComputingResult = (CloudComputingResult) view.getTag();
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_cloud_computing_result, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_cloud_computing_result :
                        deleteCloudComputingResult(cloudComputingResult);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    /**
     * Menghapus cloudcomputingresult
     * @param cloudComputingResult cloudcomputingresult yang akan dihapus
     */
    private void deleteCloudComputingResult(CloudComputingResult cloudComputingResult){
        moflusSQLiteHelper.deleteCloudComputing(cloudComputingResult);
        listCloudComputingResult.remove(cloudComputingResult);
        notifyDataSetChanged();
    }

}
