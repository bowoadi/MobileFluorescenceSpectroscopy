package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter;

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

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.FirebaseUserHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.viewholder.DotsViewHolder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas adapter yang menangani list dots pada detection
 */
public class DotsAdapter extends RecyclerView.Adapter<DotsViewHolder> implements View.OnClickListener {

    /**
     * Interface saat tampilan dots ditekan
     */
    public interface OnDotsClickListener{
        /**
         * Fungsi saat tampilan dots ditekan
         * @param dots dots yang ditekan
         */
        void onClick(Dots dots);
    }

    private OnDotsClickListener onDotsClickListener;

    private Context context;
    private List<Dots> listDot;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private FirebaseUserHelper firebaseUserHelper;

    /**
     * Konstruktor DotsAdapter
     * @param context context dari activity yang menggunakan adapter ini
     * @param listDot list dari dots yang akan ditampilkan
     * @param onDotsClickListener listener yang mengatur saat view ditekan
     */
    public DotsAdapter(Context context, List<Dots> listDot, OnDotsClickListener onDotsClickListener) {
        this.context = context;
        this.moflusSQLiteHelper = new MoflusSQLiteHelper(context);
        this.listDot = listDot;
        this.firebaseUserHelper = new FirebaseUserHelper();
        this.onDotsClickListener = onDotsClickListener;
    }

    /**
     * Fungsi untuk membuat tampilan untuk masing-masing dots
     * @param parent view parent dari dots
     * @param viewType tipe view yang ditampilkan
     * @return DotsViewHolder yang akan ditampilkan
     */
    @Override
    public DotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dots, parent, false);
        return new DotsViewHolder(view);
    }

    /**
     * Fungsi untuk mengatur tampilan DotsViewHolder saat ditampilkan
     * @param holder DotsViewHolder yang ditampilkan
     * @param position posisi dari DotsViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(DotsViewHolder holder, int position) {
        Dots dots = listDot.get(position);
        holder.getParent_card_view().setOnClickListener(this);
        holder.getParent_card_view().setTag(dots);
        if (dots.getName().equals("") || dots.getName() == null) {
            holder.getName_text().setText("Anonymouse");
        } else {
            holder.getName_text().setText(dots.getName());
        }
        holder.getDot_total_text().setText(dots.getDotLocations().size() + " Dots");
        holder.getPopup_button().setTag(dots);
        holder.getPopup_button().setOnClickListener(this);
    }

    /**
     * Fungsi yang mengembalikan total dari dots yang ditampilkan
     * @return jumlah dots yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return listDot.size();
    }

    /**
     * Fungsi untuk dijalankan saat user menekan view
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
                Dots dots = (Dots) view.getTag();
                onDotsClickListener.onClick(dots);
                break;
        }
    }

    /**
     * Fungsi untuk menampilkan popup pada dots yang dipilih
     * @param view view dari dots yang dipilih
     */
    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        final Dots dots = (Dots) view.getTag();
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_dots, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_dots:
                        deleteDots(dots);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    /**
     * Fungsi untuk menghapus dots yang dipilih
     * @param dots dots yang akan dihapus
     */
    private void deleteDots(final Dots dots) {
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
            public void onClick(DialogInterface dialogInterface, int i) {

                moflusSQLiteHelper.deleteDots(dots);
                notifyItemRemoved(listDot.indexOf(dots));
                listDot.remove(dots);
                Toast.makeText(context, dots.getName() + " Deleted", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

}
