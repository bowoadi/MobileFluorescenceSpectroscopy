package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.MoFluSPermission;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.CameraSurfaceView;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionListener;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.CameraActivityBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.CameraContentBinding;

/**
 * Kelas yang mengontrol tampilan pada camera activity
 */
public class CameraController extends AppCompatActivity implements DetectionListener, View.OnLongClickListener, View.OnClickListener {

    private static final String FOLDER= "folder";
    private CameraActivityBinding binding;
    private CameraContentBinding contentBinding;
    private Folder folder;
    private CameraSurfaceView cameraSurfaceView;
    private DetectionHelper detectionHelper;

    /**
     * Fungsi yang pertama kali dijalankan saat camera view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!MoFluSPermission.requestCamera(this)){
            return;
        }
        viewSetup();
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada camera activity
     */
    private void viewSetup(){
        binding = DataBindingUtil.setContentView(this,R.layout.camera_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        contentBinding = binding.content;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        folder = getIntent().getParcelableExtra(FOLDER);
        detectionHelper = new DetectionHelper(this);
        cameraSurfaceView = new CameraSurfaceView(this, detectionHelper,folder);
        contentBinding.timeIntervalEdittext.setText(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getString(MoflusSharedPreferenceHelper.TIME_INTERVAL, "5000"));
        contentBinding.timeDurationEdittext.setText(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getString(MoflusSharedPreferenceHelper.TIME_DURATION, "360000"));
        contentBinding.cameraFramelayout.addView(cameraSurfaceView);
        contentBinding.detectionProcessButton.setOnLongClickListener(this);
        contentBinding.detectionProcessButton.setOnClickListener(this);
        contentBinding.cancelButton.setOnClickListener(this);
    }

    /**
     * Fungsi yang menangani reaksi user saat user memberikan Permission terhadap aplikasi
     * @param requestCode code request yang diminta
     * @param permissions permission yang diminta
     * @param grantResults hasil dari permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MoFluSPermission.CAMERA_REQUEST_CODE :
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewSetup();
                } else {
                    Toast.makeText(this, "Please Allow Camera Permission", Toast.LENGTH_SHORT).show();
                    finish();

                }
                break;
        }
    }

    /**
     * Fungsi yang menangani saat menu dipilih
     * @param item menu yang dipilih
     * @return true jika terdapat menu, false jika tidak
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fungsi agar activity lain dapat pindah ke halaman camera activity
     * @param context berisi context dari tampilan yang sedang muncul
     * @param folder folder untuk menyimpan gambar yang diambil melalui kamera
     */
    public static void toActivity(Context context, Folder folder){
        Intent intent = new Intent(context, CameraController.class);
        intent.putExtra(FOLDER,folder);
        context.startActivity(intent);
    }

    /**
     * Fungsi untuk membatalkan proses pengambilan gambar
     */
    private void cancelProcess() {
        disableProcessProgress();
        detectionHelper.stopDelayText();
        if (detectionHelper.isProcess()) {
            detectionHelper.finishProcess();
        }
    }

    /**
     * Fungsi untuk mengubah tampilan saat process dilakukan
     */
    private void enableProcessProgress() {
        contentBinding.timeIntervalEdittext.setEnabled(false);
        contentBinding.timeIntervalEdittext.setEnabled(false);
        contentBinding.detectionProgressProcessButtonParentview.setVisibility(View.VISIBLE);
        contentBinding.detectionProcessButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Fungsi untuk mengubah tampilan saat process tidak dilakukan
     */
    private void disableProcessProgress() {
        contentBinding.timeIntervalEdittext.setEnabled(true);
        contentBinding.timeIntervalEdittext.setEnabled(true);
        contentBinding.detectionProgressProcessButtonParentview.setVisibility(View.INVISIBLE);
        contentBinding.detectionProcessButton.setVisibility(View.VISIBLE);
    }

    /**
     * Fungsi saat dataresult diambil
     * @param dataResult dataresult yang ingin dimasukan
     */
    @Override
    public void dataChange(DataResult dataResult) {

    }

    /**
     * Fungsisaat process pengambilan gambar telah dilakukan
     * @param dataResult dataresult yang telah diambil
     */
    @Override
    public void finishProcess(DataResult dataResult) {
        disableProcessProgress();
    }

    /**
     * Fungsi yang dilakukan saat tampilan ditekan lama
     * @param view tampilan yang ditekan lama
     * @return true jika ada, false jika tidak ada
     */
    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.detection_process_button :
                cameraSurfaceView.startAutoFocus();
                break;
        }
        return true;
    }

    /**
     * Mengecek kondisi sebelum pengambilan gambar
     * @return true jika memenuhi syarat, false jika tidak
     */
    private boolean detectionFormCheck(){
        if(Integer.parseInt(contentBinding.timeIntervalEdittext.getText().toString()) > Integer.parseInt(contentBinding.timeDurationEdittext.getText().toString()) ){
            Toast.makeText(this,"Interval More Tha Duration",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Fungsi yang mengatur saat tampilan ditekan
     * @param view tampilan yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detection_process_button:
                takePictureProcess();
                break;
            case R.id.cancel_button:
                cancelProcess();
                break;
        }
    }

    /**
     * fungsi untuk melakukan pengambilan gambar
     */
    private void takePictureProcess(){
        if(!detectionFormCheck()){
            return;
        }
        int start_delay = getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE).getInt(MoflusSharedPreferenceHelper.START_DELAY, 5);
        long interval = Long.parseLong(contentBinding.timeIntervalEdittext.getText().toString());
        long duration = Long.parseLong(contentBinding.timeDurationEdittext.getText().toString());
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE)
                .edit()
                .putString(MoflusSharedPreferenceHelper.TIME_DURATION,String.valueOf(duration))
                .putString(MoflusSharedPreferenceHelper.TIME_INTERVAL,String.valueOf(interval))
                .apply();
        detectionHelper.setInterval(interval);
        detectionHelper.setDuration(duration);
        enableProcessProgress();
        cameraSurfaceView.startDetection(contentBinding.countdownTextview,start_delay);
    }

}
