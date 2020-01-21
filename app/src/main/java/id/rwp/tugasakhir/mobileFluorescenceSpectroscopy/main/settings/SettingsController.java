package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.FirebaseUserHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.settings.adapter.MicroRNAAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.MoFluSService;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.SettingsData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Kelas yang mengontrol tampilan untuk settings
 */
public class SettingsController extends AppCompatActivity implements View.OnClickListener, RangeSeekBar.OnRangeSeekBarChangeListener {

    private RecyclerView recycleview;
    private MicroRNAAdapter microRNAAdapter;
    private List microRNAList;
    private Button microrna_update_button;
    private RangeSeekBar start_delay_seekbar;
    private ImageView add_microrna_button;
    private FirebaseUserHelper firebaseUserHelper;
    private MoflusSQLiteHelper moflusSQLiteHelper;

    /**
     * Fungsi yang pertama kali dijalankan saat settings view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUserHelper = new FirebaseUserHelper();
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        setContentView(R.layout.settings_activity);
        viewSetup();
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada settings
     */
    private void viewSetup(){
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //MicroRNA Update Button
        microrna_update_button = (Button) findViewById(R.id.microrna_update_button);
        microrna_update_button.setOnClickListener(this);
        //Start Delay Seekbar
        start_delay_seekbar = (RangeSeekBar) findViewById(R.id.cloud_computing_dataset_seekbar);
        int start_delay = getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE)
                .getInt(MoflusSharedPreferenceHelper.START_DELAY,5);
        start_delay_seekbar.setSelectedMaxValue(start_delay);
        start_delay_seekbar.setOnRangeSeekBarChangeListener(this);
        //Add MicroRNA Button
        add_microrna_button = (ImageView) findViewById(R.id.add_microrna_button);
        add_microrna_button.setOnClickListener(this);
        //RecycleView
        recycleview = (RecyclerView) findViewById(R.id.settings_recycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        recycleview.setLayoutManager(gridLayoutManager);
        microRNAList = moflusSQLiteHelper.getAllMicroRNA();
        microRNAAdapter = new MicroRNAAdapter(this, microRNAList);
        recycleview.setAdapter(microRNAAdapter);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke activity settings
     * @param activity berisi activity dari tampilan yang sedang muncul
     */
    public static void toSettingsActivity(Activity activity){
        activity.startActivity(new Intent(activity, SettingsController.class));
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.microrna_update_button :
                //updateMicroRNA();
                saveSamples();
                break;
            case R.id.add_microrna_button :
                addMicroRNA();
                break;
        }
    }

    /**
     * Fungsi untuk menambahkan microRNAs atau Samples
     */
    private void addMicroRNA() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add Sample");
        final View view = LayoutInflater.from(this).inflate(R.layout.microrna_name_dialog,null,false);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText nameEditText = (EditText) view.findViewById(R.id.microrna_name);
                String res;
                if(nameEditText.getText().equals("")){
                    res = "No Name";
                }else{
                    res = nameEditText.getText().toString();
                }
                MicroRNA microRNA = new MicroRNA();
                microRNA.setName(res);
                moflusSQLiteHelper.addMicroRNA(microRNA);
                microRNAList.add(microRNA);
                microRNAAdapter.notifyDataSetChanged();
                Toast.makeText(SettingsController.this, microRNA.getName()+" Added", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

//    private void updateMicroRNA(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.microrna_update_loading_view,null,false);
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.setView(view);
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//        firebaseUserHelper.getDatabaseReference()
//                .child("public_settings")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        //MiroRNA
//                        for(DataSnapshot postSnapshot : dataSnapshot.child("microrna").getChildren()){
//                            MicroRNA microRNA = new MicroRNA();
//                            microRNA.setId(Integer.parseInt(postSnapshot.child("id").getValue().toString()));
//                            microRNA.setName(postSnapshot.child("name").getValue().toString());
//                            moflusSQLiteHelper.addMicroRNA(microRNA);
//                        }
//                        //Classification Method
//                        for(DataSnapshot postSnapshot : dataSnapshot.child("classification_method").getChildren()){
//                            ClassificationMethod classificationMethod = new ClassificationMethod();
//                            classificationMethod.setId(Integer.parseInt(postSnapshot.child("id").getValue().toString()));
//                            classificationMethod.setName(postSnapshot.child("name").getValue().toString());
//                            moflusSQLiteHelper.addClassificationMethod(classificationMethod);
//                        }
//                        //Detection Method Color
//                        for(DataSnapshot postSnapshot : dataSnapshot.child("detection_method_color").getChildren()){
//                            DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
//                            detectionMethodColor.setId(Integer.parseInt(postSnapshot.child("id").getValue().toString()));
//                            detectionMethodColor.setColor_type(postSnapshot.child("color_type").getValue().toString());
//                            moflusSQLiteHelper.addDetectionMethodColor(detectionMethodColor);
//                        }
//
//                        microRNAList.clear();
//                        microRNAList.addAll(moflusSQLiteHelper.getAllMicroRNA());
//                        microRNAAdapter.notifyDataSetChanged();
//                        alertDialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();
//                        alertDialog.dismiss();
//                    }
//
//
//                });
//
//    }

    /**
     * Fungsi yang dijalankan untuk menyimpan samples atau microRNA ke server
     */
    public void saveSamples(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.microrna_update_loading_view,null,false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        List<MicroRNA> microRNAs = moflusSQLiteHelper.getAllMicroRNA();
        List<Dots> dotses = moflusSQLiteHelper.getFullDots();
        SettingsData settingsData = new SettingsData();
        settingsData.setUid(MoflusSharedPreferenceHelper.getUser(this).getUid());
        settingsData.setDots(dotses);
        settingsData.setMicroRNAs(microRNAs);
        MoFluSService.getService().saveSettings(settingsData).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                if(response.body()!=null && response.body().isSuccess()){
                    alertDialog.dismiss();
                    Toast.makeText(SettingsController.this, "Save Success", Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog.dismiss();
                    Toast.makeText(SettingsController.this, "Save Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                alertDialog.dismiss();
                Toast.makeText(SettingsController.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
//        firebaseUserHelper.getDatabaseReference()
//                .child("public_settings")
//                .child("sample")
//                .child(firebaseUserHelper.getFirebaseUser().getUid())
//                .setValue(microRNAs).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    alertDialog.dismiss();
//                    Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_LONG).show();
//                }else{
//                    alertDialog.dismiss();
//                    Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    /**
     * Fungsi yang dijalankan saat user merubah interval start
     * @param bar view yang ditekan
     * @param minValue nilai minimal
     * @param maxValue nilai maksimal
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
        switch (bar.getId()){
            case R.id.cloud_computing_dataset_seekbar:
                getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE)
                        .edit()
                        .putInt(MoflusSharedPreferenceHelper.START_DELAY,(int)maxValue)
                        .apply();
                break;
        }
    }
}
