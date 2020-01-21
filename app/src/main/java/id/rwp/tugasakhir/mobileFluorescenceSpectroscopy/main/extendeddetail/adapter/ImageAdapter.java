package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.extendeddetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.FolderUtil;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Image;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.testing.TestingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ImageBinding;

/**
 * Created by ranuwp on 10/3/2017.
 */

/**
 * Kelas adapter yang menangani list image pada extendeddetail
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> images;
    private Context context;
    private Listener listener;
    private int imageShownIndex;
    private List<TestingResult> testingResults;

    /**
     * Interface saat tampilan image ditekan
     */
    public interface Listener{
        /**
         * Fungsi saat tampilan image ditekan
         * @param image image yang ditekan
         */
        void onImageClick(Image image);

        /**
         * Fungsi saat tampilan image ditekan lama
         * @param image image yang ditekan lama
         */
        void onImageLongClick(Image image);
    }

    /**
     * Kelas yang mengatur tampilan pada ImageViewHolder
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private ImageBinding binding;

        /**
         * Konstruktor ImageViewHolder
         * @param imageBinding view yang telah dibuat
         */
        public ImageViewHolder(ImageBinding imageBinding) {
            super(imageBinding.getRoot());
            this.binding = imageBinding;
            binding.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onImageLongClick((Image) view.getTag());
                    return true;
                }
            });
            binding.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Image image = (Image) view.getTag();
                    imageShownIndex = images.indexOf(image);
                    listener.onImageClick(image);
                }
            });
        }

        /**
         * Fungsi untuk menampilkan hasil testing
         * @param testingResult hasil test yang disimpan
         */
        public void bindTestingResult(TestingResult testingResult){
            if(testingResult == null){
                binding.testingResult.setText("");
                binding.testingResult.setVisibility(View.GONE);
            }else{
                binding.testingResult.setVisibility(View.VISIBLE);
                binding.testingResult.setText(testingResult.getResult());
            }

        }

        /**
         * Fungsi untuk mengikat image ke ImageViewHolder
         * @param image image yang akan diikat
         */
        public void bind(Image image){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.encodeQuality(50);
            requestOptions.override(301,401);
            requestOptions.dontAnimate();
            binding.parent.setTag(image);
            Glide.with(context).asBitmap().apply(requestOptions).load(image.getFile()).into(binding.imageImageview);
        }
    }

    /**
     * Konstruktor ImageAdapter
     * @param images list dari image yang akan ditampilkan
     * @param context context dari activity yang menggunakan adapter ini
     * @param listener listener yang mengatur saat view ditekan
     */
    public ImageAdapter(List<Image> images, Context context,Listener listener) {
        this.images = images;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Fungsi untuk membuat tampilan untuk masing-masing image
     * @param parent view parent dari image
     * @param viewType tipe view yang ditampilkan
     * @return ImageViewHolder yang akan ditampilkan
     */
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageBinding imageBinding = ImageBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ImageViewHolder(imageBinding);
    }

    /**
     * Fungsi untuk mengatur tampilan ImageViewHolder saat ditampilkan
     * @param holder ImageViewHolder yang ditampilkan
     * @param position posisi dari ImageViewHolder yang ditampilkan
     */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image image = images.get(position);
        TestingResult testingResult = checkImageTestingResult(image);
        holder.bindTestingResult(testingResult);
        holder.bind(image);
    }

    /**
     * Fungsi yang mengembalikan total dari image yang ditampilkan
     * @return jumlah image yang ditampilkan
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * Fungsi untuk menghapus image
     * @param image image yang akan dihapus
     */
    public void deleteImage(Image image){
        notifyItemRemoved(images.indexOf(image));
        images.remove(image);
        FolderUtil.deleteImage(image);
    }

    /**
     * Fungsi untuk mengubah semua image yang ada
     * @param images image yang akan mengubah image yan ada
     */
    public void swapImages(List<Image> images){
        this.images = images;
        notifyDataSetChanged();
    }

    /**
     * Fungsi untuk mendapatkan image selanjutnya
     * @return image selanjutnya
     */
    public Image nextImage(){
        if(imageShownIndex == images.size()-1){
            imageShownIndex = 0;
            return images.get(0);
        }else{
            return images.get(++imageShownIndex);
        }
    }

    /**
     * Fungsi untuk mendapatkan image sebelumnya
     * @return image sebelumnya
     */
    public Image prevImage(){
        if(imageShownIndex == 0){
            imageShownIndex = images.size()-1;
            return images.get(images.size()-1);
        }else{
            return images.get(--imageShownIndex);
        }
    }

    public List<TestingResult> getTestingResults() {
        return testingResults;
    }

    public void setTestingResults(List<TestingResult> testingResults) {
        this.testingResults = testingResults;
    }

    /**
     * Fungsi untuk mengecek apakah image telah ditesting atau belum
     * @param image image yang akan dicheck
     * @return true jika hasil testing pada image telah ada, false jika hasil testing pada image tidak ada
     */
    private TestingResult checkImageTestingResult(Image image){
        for(TestingResult testingResult : testingResults){
            if(testingResult.getPath().equals(image.getFile().getPath())){
                return testingResult;
            }
        }
        return null;
    }

}
