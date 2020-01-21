package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings.adapter.MicroRNAAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

public class MicroRNAViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private CheckBox used_checkbox;
    private TextView name_textview;
    private View parent;
    private MicroRNAAdapter.OnMicroRNAChange onMicroRNAChange;

    public MicroRNAViewHolder(View itemView) {
        super(itemView);
        parent = itemView;
        parent.setOnLongClickListener(this);
        used_checkbox = (CheckBox) itemView.findViewById(R.id.name_checkbox);
        name_textview = (TextView) itemView.findViewById(R.id.name_textview);
    }

    public void setOnMicroRNAChange(MicroRNAAdapter.OnMicroRNAChange onMicroRNAChange) {
        this.onMicroRNAChange = onMicroRNAChange;
    }

    public TextView getName_textview() {
        return name_textview;
    }

    public CheckBox getUsed_checkbox() {
        return used_checkbox;
    }

    public View getParent() {
        return parent;
    }

    @Override
    public boolean onLongClick(View view) {
        final MicroRNA microRNA = (MicroRNA) view.getTag();
        final Context context = view.getContext();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
        alertDialog.setTitle("Update Sample");
        View microRNAView = LayoutInflater.from(context).inflate(R.layout.microrna_name_dialog,null,false);
        final EditText microRNAEditText = (EditText) microRNAView.findViewById(R.id.microrna_name);
        microRNAEditText.setText(microRNA.getName());
        alertDialog.setView(microRNAView);
        alertDialog.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onMicroRNAChange.onDelete(microRNA);
            }
        });
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                microRNA.setName(microRNAEditText.getText().toString());
                onMicroRNAChange.onUpdate(microRNA);
            }
        });
        alertDialog.show();
        return true;
    }
}
