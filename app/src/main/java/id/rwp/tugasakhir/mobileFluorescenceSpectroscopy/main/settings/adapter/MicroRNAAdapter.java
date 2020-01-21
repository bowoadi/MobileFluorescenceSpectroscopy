package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings.viewholder.MicroRNAViewHolder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

public class MicroRNAAdapter extends RecyclerView.Adapter<MicroRNAViewHolder> implements CompoundButton.OnCheckedChangeListener{

    private Context context;
    private List<MicroRNA> listMicroRNA;
    private MoflusSQLiteHelper moflusSQLiteHelper;

    public interface OnMicroRNAChange{
        void onDelete(MicroRNA microRNA);
        void onUpdate(MicroRNA microRNA);
    }

    private OnMicroRNAChange onMicroRNAChange = new OnMicroRNAChange() {
        @Override
        public void onDelete(MicroRNA microRNA) {
            moflusSQLiteHelper.deleteMicroRNA(microRNA);
            listMicroRNA.remove(microRNA);
            Toast.makeText(context,  microRNA.getName()+" Deleted", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }

        @Override
        public void onUpdate(MicroRNA microRNA) {
            moflusSQLiteHelper.updateMicroRNA(microRNA);
            Toast.makeText(context, microRNA.getName()+" Updated", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    };

    public MicroRNAAdapter(Context context, List<MicroRNA> listMicroRNA) {
        this.context = context;
        this.listMicroRNA = listMicroRNA;
        moflusSQLiteHelper = new MoflusSQLiteHelper(context);
    }

    @Override
    public MicroRNAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.microrna,parent,false);
        return new MicroRNAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MicroRNAViewHolder holder, int position) {
        holder.setOnMicroRNAChange(onMicroRNAChange);
        holder.getParent().setTag(listMicroRNA.get(position));
        holder.getUsed_checkbox().setTag(listMicroRNA.get(position));
        holder.getName_textview().setText(listMicroRNA.get(position).getName());
        holder.getUsed_checkbox().setChecked(listMicroRNA.get(position).isUsed());
        holder.getUsed_checkbox().setOnCheckedChangeListener(this);
    }

    @Override
    public int getItemCount() {
        return listMicroRNA.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        MicroRNA microRNA = (MicroRNA) compoundButton.getTag();
        microRNA.setUsed(isChecked);
        moflusSQLiteHelper.updateMicroRNA(microRNA);
    }
}
