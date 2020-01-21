package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.viewholder;

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
 * Kelas yang mengatur tampilan pada DotsViewHolder
 */
public class DotsViewHolder extends RecyclerView.ViewHolder {

    private TextView name_text;
    private TextView dot_total_text;
    private ImageView popup_button;
    private CardView parent_card_view;

    /**
     * Konstruktor DotsViewHolder
     * @param itemView view yang telah dibuat
     */
    public DotsViewHolder(View itemView) {
        super(itemView);
        name_text = (TextView) itemView.findViewById(R.id.name_text);
        dot_total_text = (TextView) itemView.findViewById(R.id.dot_total_text);
        popup_button = (ImageView) itemView.findViewById(R.id.popup_button);
        parent_card_view = (CardView) itemView.findViewById(R.id.parent_card_view);
    }

    public TextView getName_text() {
        return name_text;
    }

    public TextView getDot_total_text() {
        return dot_total_text;
    }

    public ImageView getPopup_button() {
        return popup_button;
    }


    public View getParent_card_view() {
        return parent_card_view;
    }
}
