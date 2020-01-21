package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.FolderUtil;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.FolderBinding;

/**
 * Created by ranuwp on 9/27/2017.
 */

/**
 * Kelas adapter yang mengangani list folder pada home
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    /**
     * Interface saat tampilan folder ditekan
     */
    public interface Listener {
        /**
         * Fungsi saat tampilan folder ditekan
         * @param folder folder yang ditekan
         */
        void onClick(Folder folder);

        /**
         * Fungsi saat menekan tombol popup
         * @param view tampilan view dari folder yang dipilih
         * @param folder folder yang dipilih
         */
        void onPopup(View view, Folder folder);

        /**
         * Fungsi yang dipanggil saat folder dicheck
         * @param folder folder yang dicheck
         */
        void onSelected(Folder folder);
    }

    /**
     * Kelas yang mengatur tampilan pada FolderViewHolder
     */
    public class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        FolderBinding binding;

        /**
         * Konstruktor FolderViewHolder
         * @param binding view yang telah dibuat
         */
        public FolderViewHolder(FolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.popupButton.setOnClickListener(this);
            binding.parentCardView.setOnClickListener(this);
            binding.selectedCheckbox.setOnCheckedChangeListener(this);
        }

        /**
         * Fungsi yang dijalankan saat tampilan folder ditekan
         * @param view tampilan yang ditekan
         */
        @Override
        public void onClick(View view) {
            Folder folder = (Folder) view.getTag();
            switch (view.getId()){
                case R.id.parent_card_view :
                    listener.onClick(folder);
                    break;
                case R.id.popup_button :
                    listener.onPopup(view, folder);
                    break;
            }
        }

        /**
         * Fungsi yang dijalankan saat saat folder dicheck
         * @param compoundButton view dari tombol yang dicheck
         * @param b true jika dicheck, false jika di uncheck
         */
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Folder folder = (Folder) compoundButton.getTag();
            folder.setSelected(b);
            listener.onSelected(folder);
        }
    }

    private Context context;

    private Listener listener;

    private List<Folder> folders;

    /**
     * Konstruktor FolderAdapter
     * @param context context dari activity yang menggunakan adapter ini
     * @param listener listener yang mengatur saat view ditekan
     * @param folders list dari folder yang akan ditampilkan
     */
    public FolderAdapter(Context context, Listener listener, List<Folder> folders) {
        this.context = context;
        this.listener = listener;
        this.folders = folders;
    }

    /**
     * Fungsi untuk membuat tampilan untuk masing-masing folder
     * @param parent view parent dari folder
     * @param viewType tipe view yang ditampilkan
     * @return FolderViewHolder yang akan ditampilkan
     */
    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FolderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.folder,parent,false);
        return new FolderViewHolder(binding);
    }

    /**
     * Fungsi yang mengatur tampilan FolderViewHolder saat ditampilkan
     * @param holder FolderViewHolder yang ditampilkan
     * @param position posisi dari FolderViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.binding.popupButton.setTag(folder);
        holder.binding.parentCardView.setTag(folder);
        holder.binding.selectedCheckbox.setTag(folder);
        holder.binding.nameText.setText(folder.getFolderName());
    }

    /**
     * Fungsi yang mengembalikan total dari folder yang ditampilkan
     * @return jumlah dataresult yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return folders.size();
    }

    /**
     * fungsi untuk mengubah semua folder yang ada
     * @param folders folder yang akan mengubah folder yang ada
     */
    public void swapFolder(List<Folder> folders){
        if(folders != null){
            this.folders = folders;
            notifyDataSetChanged();
        }
    }

    /**
     * Fungsi untuk mengecek apakah folder telah ada
     * @param name nama folder
     * @return true jika ada, false jika tidak ada
     */
    public boolean isExistFolder(String name){
        for(Folder folder : folders){
            if(folder.getFolderName().toUpperCase().equals(name.toUpperCase())){
                return true;
            }
        }
        return false;
    }

    /**
     * Menampilkan popup pada FolderViewHolder yang ditekan
     * @param view view yang dipilih
     * @param folder folder yang dipilih
     */
    public void showPopup(View view, final Folder folder){
        PopupMenu popupMenu = new PopupMenu(context,view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_folder, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_folder :
                        deleteFolder(folder);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    /**
     * Fungsi untuk menambahkan Folder
     * @param folder folder yang akan ditambah
     */
    public void addFolder(Folder folder){
        folders.add(folder);
        notifyItemInserted(folders.indexOf(folder));
    }

    /**
     * Fungsi untuk menghapus folder
     * @param folder folder yang akan dihapus
     */
    private void deleteFolder(Folder folder){
        FolderUtil.deleteFolder(folder.getFile());
        notifyItemRemoved(folders.indexOf(folder));
        folders.remove(folder);
    }

    public List<Folder> getFolders() {
        return folders;
    }

    /**
     * Fungsi untuk mendapatkan list dari folder yang dicheck
     * @return list folder yang di check
     */
    public List<Folder> getSelectedFolders(){
        ArrayList<Folder> selectedFolder = new ArrayList<>();
        for(Folder folder : folders){
            if(folder.isSelected()){
                selectedFolder.add(folder);
            }
        }
        return selectedFolder;
    }
}
