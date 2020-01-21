package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.viewholder;

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
 * Kelas yang mengatur tampilan pada DataResultViewHolder
 */
public class DataResultViewHolder extends RecyclerView.ViewHolder {

    private ImageView preview_image;
    private TextView name_text;
    private TextView date_time_text;
    private ImageView popup_button;
    private ImageView cloud_saved_image;
    private CardView parent_card_view;

    /**
     * Konstruktor DataResultViewHolder
     * @param itemView view yang telah dibuat
     */
    public DataResultViewHolder(View itemView) {
        super(itemView);
        preview_image = (ImageView) itemView.findViewById(R.id.preview_image);
        name_text = (TextView) itemView.findViewById(R.id.name_text);
        date_time_text = (TextView) itemView.findViewById(R.id.date_time_text);
        popup_button = (ImageView) itemView.findViewById(R.id.popup_button);
        cloud_saved_image = (ImageView) itemView.findViewById(R.id.cloud_saved_image);
        parent_card_view = (CardView) itemView.findViewById(R.id.parent_card_view);
    }

    public ImageView getPreview_image() {
        return preview_image;
    }

    public TextView getName_text() {
        return name_text;
    }

    public TextView getDate_time_text() {
        return date_time_text;
    }

    public ImageView getPopup_button() {
        return popup_button;
    }

    public ImageView getCloud_saved_image() {
        return cloud_saved_image;
    }

    public View getParent_card_view() {
        return parent_card_view;
    }
}
