package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Created by ranuwp on 12/27/2016.
 */

/**
 * Kelas yang mengatur tampilan pada CloudComputingResultViewHolder
 */
public class CloudComputingResultViewHolder extends RecyclerView.ViewHolder {

    private TextView date_time_text;
    private TextView result;
    private TextView classification_method_textview;
    private ImageView popup_button;
    private ImageView process_imageview;
    private CardView parent_card_view;

    public TextView getDate_time_text() {
        return date_time_text;
    }

    public TextView getResult() {
        return result;
    }

    public TextView getClassification_method_textview() {
        return classification_method_textview;
    }

    public ImageView getPopup_button() {
        return popup_button;
    }

    public ImageView getProcess_imageview() {
        return process_imageview;
    }

    public CardView getParent_card_view() {
        return parent_card_view;
    }

    /**
     * Konstruktor CloudComputingResultViewHolder
     * @param itemView
     */
    public CloudComputingResultViewHolder(View itemView) {
        super(itemView);
        date_time_text = (TextView) itemView.findViewById(R.id.date_time_text);
        result = (TextView) itemView.findViewById(R.id.result);
        classification_method_textview = (TextView) itemView.findViewById(R.id.classification_method_textview);
        popup_button = (ImageView) itemView.findViewById(R.id.popup_button);
        process_imageview = (ImageView) itemView.findViewById(R.id.process_imageview);
        parent_card_view = (CardView) itemView.findViewById(R.id.parent_card_view);
    }
}
