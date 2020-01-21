package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.extendeddetail;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.camera.CameraController;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.FolderUtil;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.extendeddetail.adapter.ImageAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Image;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.testing.TestingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ExtendedDetailActivityBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ExtendedDetailContentBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ImageDetailBinding;

/**
 * Kelas yang mengontrol tampilan pada extendeddetail
 */
public class ExtendedDetailController extends AppCompatActivity implements ImageAdapter.Listener, View.OnClickListener {

    private static final String FOLDER_TAG = "FOLDER";
    private static final int IMAGE_REQUEST = 101;
    private Folder folder;
    private List<Image> images;
    private ExtendedDetailActivityBinding binding;
    private ExtendedDetailContentBinding contentBinding;
    private ImageAdapter adapter;
    private List<TestingResult> testingResults;
    private MoflusSQLiteHelper moflusSQLiteHelper;

    /**
     * Fungsi yang pertama kali dijalankan saat extendeddetail view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.extended_detail_activity);
        contentBinding = binding.extendedDetailContent;
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        testingResults = moflusSQLiteHelper.getAllTestingResult();
        folder = getIntent().getParcelableExtra(FOLDER_TAG);
        if(folder == null){
            folder = savedInstanceState.getParcelable(FOLDER_TAG);
        }
        getSupportActionBar().setTitle(folder.getFolderName());
        images = new ArrayList<>(0);
        adapter = new ImageAdapter(images,this,this);
        adapter.setTestingResults(testingResults);
        contentBinding.imageRecyclerview.setAdapter(adapter);
        binding.fab.setOnClickListener(this);
    }

    /**
     * Fungsi untuk menyimpan bundle saat tampilan dibuat
     * @param outState bundle untuk menyimpan data-data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(FOLDER_TAG,folder);
        super.onSaveInstanceState(outState);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke activity extendeddetail
     * @param context berisi context dari tampilan yang sedang muncul
     * @param folder folder yang akan ditampilkan detailnya
     */
    public static void toActivity(Context context, Folder folder){
        Intent intent = new Intent(context,ExtendedDetailController.class);
        intent.putExtra(FOLDER_TAG,folder);
        context.startActivity(intent);
    }

    /**
     * Fungsi yang dilakukan saat tampilan direset
     */
    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapImages(FolderUtil.getImages(folder.getFile()));
    }

    /**
     * Fungsi yang dilakukan saat gambar ditekan
     * @param image gambar yang dipilih
     */
    @Override
    public void onImageClick(Image image) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final ImageDetailBinding imageDetailBinding = DataBindingUtil.inflate(getLayoutInflater(),R.layout.image_detail,null,true);
        imageDetailBinding.leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(imageDetailBinding.imageImageview.getDrawable());
                requestOptions.dontAnimate().encodeQuality(100).diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getApplicationContext()).asBitmap().apply(requestOptions).load(adapter.prevImage().getFile()).into(imageDetailBinding.imageImageview);
            }
        });
        imageDetailBinding.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(imageDetailBinding.imageImageview.getDrawable());
                requestOptions.dontAnimate().encodeQuality(100).diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getApplicationContext()).asBitmap().apply(requestOptions).load(adapter.nextImage().getFile()).into(imageDetailBinding.imageImageview);
            }
        });
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate().encodeQuality(100).diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).asBitmap().apply(requestOptions).load(image.getFile()).into(imageDetailBinding.imageImageview);
        dialog.setView(imageDetailBinding.getRoot());
        dialog.show();
    }

    /**
     * Fungsi saat gambar ditekan lama
     * @param image gambar yang ditekan
     */
    @Override
    public void onImageLongClick(final Image image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.deleteImage(image);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setTitle("Delete This Images ?");
        builder.show();
    }

    /**
     * Fungsi untuk menambah gambar
     */
    private void addImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Images"),IMAGE_REQUEST);
    }

    /**
     * Fungsi yang dipanggil saat activity selanjutnya di close dan kembali ke activity extendedetail
     * @param requestCode code dari permintaan hasil
     * @param resultCode kode hasil dari activity selanjutnya
     * @param data data yang dibalikan
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_CANCELED || data == null){
            return;
        }
        switch (requestCode){
            case IMAGE_REQUEST :
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                ClipData clipData = data.getClipData();
                String filePath = "";
                if(clipData == null){
                    Uri uri = data.getData();
                    File newFile = new File(folder.getFile(),String.valueOf(new Date().getTime())+".png");
                    try{
                        String wholeID = DocumentsContract.getDocumentId(uri);
                        // Split at colon, use second item in the array
                        String id = wholeID.split(":")[1];

                        String[] column = { MediaStore.Images.Media.DATA };

                        // where id is equal to
                        String sel = MediaStore.Images.Media._ID + "=?";

                        Cursor cursor = getContentResolver().
                                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        column, sel, new String[]{ id }, null);


                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    }catch (IllegalArgumentException e){

                        if(uri.getScheme().equals("file")){
                            filePath = uri.getPath();
                        }else{
                            try {
                                InputStream initialStream = getContentResolver().openInputStream(uri);
                                byte[] buffer = new byte[initialStream.available()];
                                initialStream.read(buffer);
                                OutputStream outStream = new FileOutputStream(newFile);
                                outStream.write(buffer);
                                initialStream.close();
                                outStream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    File imageFile = new File(filePath);
                    try {
                        FolderUtil.copyFile(imageFile,newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Image image = new Image();
                    image.setFile(newFile);
                    images.add(image);
                    adapter.notifyItemInserted(images.indexOf(image));
                }else{
                    for (int i = 0;i< clipData.getItemCount();i++){
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();

                        try{
                            String wholeID = DocumentsContract.getDocumentId(uri);
                            // Split at colon, use second item in the array
                            String id = wholeID.split(":")[1];

                            String[] column = { MediaStore.Images.Media.DATA };

                            // where id is equal to
                            String sel = MediaStore.Images.Media._ID + "=?";

                            Cursor cursor = getContentResolver().
                                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            column, sel, new String[]{ id }, null);

                            int columnIndex = cursor.getColumnIndex(column[0]);

                            if (cursor.moveToFirst()) {
                                filePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        }catch (IllegalArgumentException e){
                            filePath = uri.getPath();
                        }

                        File imageFile = new File(filePath);
                        File newFile = new File(folder.getFile(),String.valueOf(new Date().getTime())+".png");
                        try {
                            FolderUtil.copyFile(imageFile,newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Image image = new Image();
                        image.setFile(newFile);
                        images.add(image);
                        adapter.notifyItemInserted(images.indexOf(image));
                    }
                }
                break;
        }
    }

    /**
     * Fungsi yang mengatur fungsi saat user menekan pada tampilan
     * @param view tampilan yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab :
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Images From");
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CameraController.toActivity(ExtendedDetailController.this,folder);
                    }
                });
                builder.setNegativeButton("Library", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addImage();
                    }
                });
                builder.show();
                break;
        }
    }
}
