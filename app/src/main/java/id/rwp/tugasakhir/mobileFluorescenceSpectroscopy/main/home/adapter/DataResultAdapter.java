package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.FirebaseUserHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.CSVHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult.DetailedDataResultController;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.OnPopupClickListenner;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.viewholder.DataResultViewHolder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.MoFluSService;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.ImageUploader;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.DeleteDataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.SaveDataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.Status;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas adapter yang menangani list data result pada home
 */
public class DataResultAdapter extends RecyclerView.Adapter<DataResultViewHolder> implements View.OnClickListener {

    private Context context;
    private List<DataResult> listDataResult;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private OnPopupClickListenner onPopupClickListenner;
    private FirebaseUserHelper firebaseUserHelper;

    /**
     * Konstruktor DataResultAdapter
     * @param context context dari activity yang menggunakan adapter ini
     * @param listDataResult list dari dataresult yang akan ditampilkan
     * @param onPopupClickListenner listener yang mengatur fungsi saat tombol popup ditekan
     */
    public DataResultAdapter(Context context, List<DataResult> listDataResult, OnPopupClickListenner onPopupClickListenner) {
        this.context = context;
        this.moflusSQLiteHelper = new MoflusSQLiteHelper(context);
        this.listDataResult = listDataResult;
        this.onPopupClickListenner = onPopupClickListenner;
        this.firebaseUserHelper = new FirebaseUserHelper();
    }

    /**
     * Fungsi untuk membuat tampilan untuk masing-masing dataresult
     * @param parent view parent dari dataresult
     * @param viewType tipe view yang ditampilkan
     * @return DataResultViewHolder yang akan ditampilkan
     */
    @Override
    public DataResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_result, parent, false);
        return new DataResultViewHolder(view);
    }

    /**
     * Fungsi yang mengatur tampilan DataResultViewHolder saat ditampilkan
     * @param holder DataResultViewHolder yang ditampilkan
     * @param position posisi dari DataResultViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(DataResultViewHolder holder, int position) {
        DataResult dataResult = listDataResult.get(position);
        if (dataResult.getPreview_picture() == null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.dontAnimate();
            requestOptions.encodeQuality(50);
            requestOptions.error(R.drawable.error_placeholder);
            requestOptions.placeholder(R.drawable.loading_placeholder);
            Glide.with(context).load(dataResult.getPreview_picture_url()).apply(requestOptions).into(holder.getPreview_image());
        } else {
            holder.getPreview_image().setImageBitmap(dataResult.getPreview_picture());
        }
        holder.getParent_card_view().setOnClickListener(this);
        holder.getParent_card_view().setTag(dataResult);
        if (dataResult.getName().equals("") || dataResult.getName() == null) {
            holder.getName_text().setText("Anonymouse");
        } else {
            holder.getName_text().setText(dataResult.getName());
        }
        holder.getDate_time_text().setText(DateHelper.DATETIME_FORMAT.format(dataResult.getDatetime()));
        holder.getPopup_button().setTag(dataResult);
        holder.getPopup_button().setOnClickListener(this);
        if (dataResult.isIs_clouded()) {
            holder.getCloud_saved_image().setImageResource(R.drawable.ic_cloud_done);
        } else {
            holder.getCloud_saved_image().setImageResource(R.drawable.ic_cloud_off);
        }
    }

    /**
     * Fungsi yang mengembalikan total dari dataresult yang ditampilkan
     * @return jumlah dataresult yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return listDataResult.size();
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.popup_button:
                showPopup(view);
                break;
            case R.id.parent_card_view:
                DataResult dataResult = (DataResult) view.getTag();
                DetailedDataResultController.toDetailedDataResultsActivity(context, dataResult);
                break;
        }
    }

    /**
     * Fungsi untuk menampilkan menu popup pada dataresultviewholder yang dipilih
     * @param view view dataresult yang dipilih
     */
    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        final DataResult dataResult = (DataResult) view.getTag();
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_data_result, popupMenu.getMenu());
        if (dataResult.isIs_clouded()) {
            popupMenu.getMenu().getItem(1).setEnabled(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open_csv_data_result:
                        openCSVDataResult(dataResult);
                        break;
                    case R.id.cloud_saved_data_result:
                        cloudSavedDataResult(dataResult);
                        break;
                    case R.id.delete_data_result:
                        deleteDataResult(dataResult);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    /**
     * fungsi yang dipanggil saat membuka file csv untuk dataresult
     * @param dataResult dataresult yang dipilih
     */
    private void openCSVDataResult(DataResult dataResult) {
        try {
            onPopupClickListenner.openCSVFile(CSVHelper.createFile(dataResult));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fungsi untuk menyimpan dataresult ke server
     * @param dataResult dataresult yang ingin disimpan ke server
     */
    private void cloudSavedDataResult(final DataResult dataResult) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cloud_saving_loading_view, null, false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
//        FirebaseStorageRequest firebaseStorageRequest = new FirebaseStorageRequest(dataResult, new StorageListener() {
//            @Override
//            public void onSuccess(DataResult dataResult) {
//                moflusSQLiteHelper.updateDataResult(dataResult);
//                notifyDataSetChanged();
//                alertDialog.dismiss();
//                Toast.makeText(context,"Data Result Saved to Cloud",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailed(String error) {
//                alertDialog.dismiss();
//                Toast.makeText(context,"Error While Saving To The Cloud",Toast.LENGTH_SHORT).show();
//            }
//        });
//        firebaseStorageRequest.saveToCloud();
        User user = MoflusSharedPreferenceHelper.getUser(context);
        SaveDataResult saveDataResult = new SaveDataResult();
        saveDataResult.setUid(user.getUid());
        saveDataResult.setDataResult(dataResult);
        MoFluSService.getService().saveDataResult(saveDataResult).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if (response.body()!= null && response.body().isSuccess()) {
                    ImageUploader imageUploader = new ImageUploader(context,MoflusSharedPreferenceHelper.getUser(context));

                    imageUploader.uploadDataResultPreview(BitmapHelper.createBitmapToFile(dataResult.getPreview_picture(),String.valueOf(dataResult.getId())));
                    dataResult.setIs_clouded(true);
                    moflusSQLiteHelper.updateDataResult(dataResult);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                    Toast.makeText(context, dataResult.getName()+" Saved to Cloud", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(context, "Error While Saving To The Cloud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(context, "Internet Problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fungsi untuk menghapus dataresult
     * @param dataResult dataresult yang akan dihapus
     */
    private void deleteDataResult(final DataResult dataResult) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Are You Sure ?");
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                if (dataResult.isIs_clouded()) {
//                        DatabaseReference databaseReference = firebaseUserHelper.getDatabaseReference().child("user").child(firebaseUserHelper.getFirebaseUser().getUid()).child("data_result").child(String.valueOf(dataResult.getId()));
//                        databaseReference.removeValue();
//                        firebaseUserHelper.getStorageReference().child(String.valueOf(dataResult.getId())).delete();
                    DeleteDataResult deleteDataResult = new DeleteDataResult();
                    deleteDataResult.setUid(MoflusSharedPreferenceHelper.getUser(context).getUid());
                    deleteDataResult.setData_result_id(dataResult.getId());
                    MoFluSService.getService().deleteDataResult(
                            deleteDataResult
                    ).enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            if (response.body()!= null &&response.body().isSuccess()) {
                                try {
                                    CSVHelper.deleteFile(dataResult);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                moflusSQLiteHelper.deleteDataResult(dataResult);
                                notifyItemRemoved(listDataResult.indexOf(dataResult));
                                listDataResult.remove(dataResult);
                                Toast.makeText(context, dataResult.getName() + " Deleted", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            } else {
                                Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable t) {
                            Toast.makeText(context, "No Internet Access", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                }else{
                    try {
                        CSVHelper.deleteFile(dataResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    moflusSQLiteHelper.deleteDataResult(dataResult);
                    notifyItemRemoved(listDataResult.indexOf(dataResult));
                    listDataResult.remove(dataResult);
                    Toast.makeText(context, dataResult.getName() + " Deleted", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertDialogBuilder.show();
    }

}
