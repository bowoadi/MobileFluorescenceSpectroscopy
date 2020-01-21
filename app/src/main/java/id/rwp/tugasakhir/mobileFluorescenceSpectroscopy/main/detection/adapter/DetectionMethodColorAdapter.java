package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 8/14/2017.
 */

/**
 * Kelas adapter yang menangani list detectionmethodcolor pada detection
 */
public class DetectionMethodColorAdapter extends ArrayAdapter<DetectionMethodColor> {

    private Context context;
    private List<DetectionMethodColor> detectionMethodColors;

    /**
     * Interface saat tampilan detectionmethodcolo ditekan
     */
    public interface OnColorDataListener{
        /**
         * Fungsi saat tampilan detectionmethodcolor ditekan
         * @param id id detectionmethodcolor yang dipilih
         */
        void onColorDataChange(int id);
    }

    private OnColorDataListener onColorDataListener;

    /**
     * Konstruktor DetectionMethodColorAdapter
     * @param context context dari activity yang menggunaan adapter ini
     * @param resource id view layout
     * @param detectionMethodColors list dari detectionmethodcolor yang akan ditampilkan
     * @param onColorDataListener listener yang mengatur saat view ditekan
     */
    public DetectionMethodColorAdapter(Context context, int resource, List<DetectionMethodColor> detectionMethodColors, OnColorDataListener onColorDataListener) {
        super(context, resource, detectionMethodColors);
        this.context = context;
        this.detectionMethodColors = detectionMethodColors;
        this.onColorDataListener = onColorDataListener;
    }

    /**
     * Fungsi yang dijalankan saat color tipe akan dipilih
     * @param position posisi detectionmethodcolor
     * @param convertView view yang ditampilkan
     * @param parent parent view dari detectionmethodcolor
     * @return View detectionmethodcolor
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * Fungsi untuk mendapatkan tampilan detectionmethodcolor
     * @param position posisi detectionmethodcolor yang ditampilkan
     * @param convertView view yang ditampilkan pada detectionmethodcolor
     * @param parent parentview pada detectionmethodcolor
     * @return View dari detectionmethodcolor
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * Fungsi untuk mendapatkan view secara custom
     * @param position posisi detectionmethodcolor
     * @param convertView view dari detectionmethodcolor
     * @param parent parent view dari detectionmethodcolor
     * @return View dari detectionmethodcolor
     */
    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        DetectionColorViewHolder holder;
        LayoutInflater layoutInflator = LayoutInflater.from(context);
        convertView = layoutInflator.inflate(R.layout.detection_method_color_item, null);
        holder = new DetectionColorViewHolder();
        holder.textView = (TextView) convertView
                .findViewById(R.id.text);
        holder.checkBox = (CheckBox) convertView
                .findViewById(R.id.checkbox);
        convertView.setTag(holder);
        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(detectionMethodColors.get(position).isSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) buttonView.getTag();
                detectionMethodColors.get(position).setSelected(isChecked);
                String id = "";
                for(DetectionMethodColor detectionMethodColor : detectionMethodColors){
                    if(detectionMethodColor.isSelected()){
                        id += String.valueOf(detectionMethodColor.getId());
                    }
                }
                if(id.equals("")) {
                    onColorDataListener.onColorDataChange(-1);
                }else{
                    onColorDataListener.onColorDataChange(Integer.parseInt(id));
                }
            }
        });
        holder.textView.setText(detectionMethodColors.get(position).getColor_type());
        if ((position == 0)) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * Kelas DetectionMethodColorViewHolder untuk tampilan detectionmethodcolor
     */
    class DetectionColorViewHolder {
        private TextView textView;
        private CheckBox checkBox;
    }
}
