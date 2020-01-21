package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.FirebaseUserHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.MoFluSPermission;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.HomeController;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.register.RegisterController;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.MoFluSService;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel.JDataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel.JDetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel.JGraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.jsonmodel.JGraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.PublicSettings;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Kelas yang mengontrol tampilan untuk login
 */
public class LoginController extends AppCompatActivity implements View.OnClickListener {

    private EditText email_form;
    private EditText password_form;
    private Button login_button;
    private Button register_button;
    private FirebaseUserHelper firebaseUserHelper;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private SharedPreferences sharedPreferences;

    /**
     * Fungsi yang pertama kali dijalankan saat login view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        sharedPreferences = MoflusSharedPreferenceHelper.getMoFluSSharedPreference(this);
        if(sharedPreferences.getBoolean(MoflusSharedPreferenceHelper.IS_LOGIN,false)){
            HomeController.toHomeActivity(this);
            finish();
            return;
        }
        if(!MoFluSPermission.requestExternalStorage(this)){
            return;
        }
//        if(firebaseUserHelper.isLogin()){
//            HomeController.toHomeActivity(this);
//            finish();
//        }
        viewSetup();
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
            case MoFluSPermission.EXTERNAL_STORAGE_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewSetup();
                } else {
                    Toast.makeText(this, "Please Allow External Storage Permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada login
     */
    private void viewSetup(){
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        firebaseUserHelper = new FirebaseUserHelper();
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Login Form
        email_form = (EditText) findViewById(R.id.email_form);
        password_form = (EditText) findViewById(R.id.password_form);
        //Login Button
        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(this);
        //Register Button
        register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button :
                login();
                break;
            case R.id.register_button :
                register();
                break;
        }
    }

    /**
     * Fungsi saat tombol login ditekan
     */
    private void login(){
        disableLoginForm();
        String email = email_form.getText().toString();
        String password = password_form.getText().toString();
        //final Map<String,String> map = new HashMap<>();
        if(!formCheck(email,password)){
            enableLoginForm();
            return;
        }
//        map.put("email",email);
//        map.put("password",password);
//        firebaseUserHelper.getFirebaseAuth().signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(!task.isSuccessful()){
//                            Toast.makeText(getApplicationContext(),"Wrong Email/Password",Toast.LENGTH_SHORT).show();
//                            enableLoginForm();
//                            return;
//                        }else if(!firebaseUserHelper.isEmailVerified()){
//                            Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_SHORT).show();
//                            enableLoginForm();
//                            return;
//                        }
//                        getCloudData();
//                    }
//                });
        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        MoFluSService.getService().login(user).enqueue(new Callback<PublicSettings>() {
            @Override
            public void onResponse(Call<PublicSettings> call, Response<PublicSettings> response) {
                PublicSettings publicSettings = response.body();
                if(publicSettings != null && publicSettings.isSuccess()){
                    Toast.makeText(LoginController.this,"Login Success",Toast.LENGTH_SHORT).show();
                    savePublicSettings(publicSettings);
                    HomeController.toHomeActivity(LoginController.this);
                    finish();
                }else{
                    Toast.makeText(LoginController.this,"Wrong Email/Password",Toast.LENGTH_SHORT).show();
                }
                enableLoginForm();
            }

            @Override
            public void onFailure(Call<PublicSettings> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();
                enableLoginForm();
            }
        });
    }

    /**
     * Fungsi untuk pindah ke halaman Register
     */
    private void register(){
        RegisterController.toRegisterActivity(this);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke activity login
     * @param activity berisi activity dari tampilan yang sedang muncul
     */
    public static void toLoginActivity(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(),LoginController.class));
    }

    /**
     * Menonaktifkan form login agar tidak dapat diubah-ubah
     */
    private void disableLoginForm(){
        email_form.setEnabled(false);
        password_form.setEnabled(false);
        login_button.setEnabled(false);
        register_button.setEnabled(false);
    }

    /**
     * Mengaktifkan form login agar dapat diubah
     */
    private void enableLoginForm(){
        email_form.setEnabled(true);
        password_form.setEnabled(true);
        login_button.setEnabled(true);
        register_button.setEnabled(true);
    }

    /**
     * Mengecek apakah setiap form telah memenuhi syarat sebelum diproses
     * @param email email atau username pengguna
     * @param password password dari pengguna
     * @return true jika form yang diisi telah sesuai, false jika tidak sesuai
     */
    private boolean formCheck(String email, String password){
        if(email.equals("")){
            email_form.setError("Empty Email");
            return false;
        }
        if(password.equals("")){
            password_form.setError("Empty Password");
            return false;
        }
        return true;
    }


//    private void getCloudData(){
//        firebaseUserHelper.getDatabaseReference()
//                .child("public_settings")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        //MiroRNA
//                        //if(!dataSnapshot.child("sample").child(firebaseUserHelper.getFirebaseUser().getUid()).exists()){
//                            for(DataSnapshot postSnapshot : dataSnapshot.child("microrna").getChildren()){
//                                MicroRNA microRNA = new MicroRNA();
//                                microRNA.setId(Integer.parseInt(postSnapshot.child("id").getValue().toString()));
//                                microRNA.setName(postSnapshot.child("name").getValue().toString());
//                                moflusSQLiteHelper.addMicroRNA(microRNA);
//                            }
////                        }else{
////                            for(DataSnapshot sampleSnap : dataSnapshot.child("sample").child(firebaseUserHelper.getFirebaseUser().getUid()).getChildren()){
////                                MicroRNA microRNA = sampleSnap.getValue(MicroRNA.class);
////                                moflusSQLiteHelper.addMicroRNA(microRNA);
////                            }
////                        }
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
//                        //Data Result
//                        Query query = firebaseUserHelper.getFirebaseDataResult();
//                        query.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for(DataSnapshot data_result : dataSnapshot.getChildren()){
//                                    final DataResult dataResult = new DataResult();
//                                    dataResult.setId(Integer.parseInt(data_result.child("id").getValue().toString()));
//                                    dataResult.setName(data_result.child("name").getValue().toString());
//                                    dataResult.setPreview_picture_url(data_result.child("preview_picture_url").getValue().toString());
//                                    try {
//                                        dataResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(data_result.child("datetime").getValue().toString()));
//                                    } catch (ParseException e) {
//                                        dataResult.setDatetime(new Date());
//                                    }
//                                    dataResult.setIs_clouded(true);
//                                    ArrayList<DetectionMethodColor> detectionMethodColors = new ArrayList<>();
//                                    String[] ids = data_result.child("detection_method_color_id").getValue().toString().split("");
//                                    try{
//                                        for(String id : ids){
//                                            if(!id.equals("")){
//                                                detectionMethodColors.add(moflusSQLiteHelper.getDetectionMethodColor(Integer.parseInt(id)));
//                                            }
//                                        }
//                                    }catch (Exception e){
//                                        for(DataSnapshot detectionMethod : data_result.child("detection_method_color_id").getChildren()){
//                                            detectionMethodColors.add(detectionMethod.getValue(DetectionMethodColor.class));
//                                        }
//                                    }
//                                    dataResult.setDetectionMethodColors(detectionMethodColors);
//                                    ArrayList graph_data_list = new ArrayList();
//                                    for(DataSnapshot graph_data : data_result.child("graph_data").getChildren()){
//                                        GraphData graphData = new GraphData();
//                                        graphData.setId(Integer.parseInt(graph_data.child("id").getValue().toString()));
//                                        graphData.setMicroRNA(moflusSQLiteHelper.getMicroRNA(Integer.parseInt(graph_data.child("microrna_id").getValue().toString())));
//                                        ArrayList graph_data_value_list = new ArrayList();
//                                        for(DataSnapshot graph_data_value: graph_data.child("graph_data_value").getChildren()){
//                                            GraphDataValue graphDataValue = new GraphDataValue(Double.parseDouble(graph_data_value.child("time").getValue().toString()),Double.parseDouble(graph_data_value.child("value").getValue().toString()));
//                                            graphDataValue.setId(Integer.parseInt(graph_data_value.child("id").getValue().toString()));
//                                            graph_data_value_list.add(graphDataValue);
//                                        }
//                                        graphData.setGraphDataValues(graph_data_value_list);
//                                        graph_data_list.add(graphData);
//                                    }
//                                    dataResult.setGraphDatas(graph_data_list);
//                                    moflusSQLiteHelper.addDataResult(dataResult);
//                                }
//                                HomeController.toHomeActivity(LoginController.this);
//                                finish();
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                enableLoginForm();
//                                Toast.makeText(getApplicationContext(),"Failed To Login",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        enableLoginForm();
//                    }
//
//
//                });
//    }

    /**
     * Menyimpan public settings dan data-data yang diperlukan setelah login
     * @param publicSettings berisi seluruh data publik settings yang diberikan dari server
     */
    private void savePublicSettings(PublicSettings publicSettings){
        sharedPreferences.edit()
                .putString(MoflusSharedPreferenceHelper.UID,publicSettings.getUid())
                .putString(MoflusSharedPreferenceHelper.USERNAME,publicSettings.getUsername())
                .putBoolean(MoflusSharedPreferenceHelper.IS_LOGIN,true)
                .apply();
        if(publicSettings.getClassificationMethods() != null){
            for(ClassificationMethod classificationMethod : publicSettings.getClassificationMethods()){
                moflusSQLiteHelper.addClassificationMethod(classificationMethod);
            }
        }
        if(publicSettings.getDetectionMethodColors() != null){
            for(DetectionMethodColor detectionMethodColor : publicSettings.getDetectionMethodColors()){
                moflusSQLiteHelper.addDetectionMethodColor(detectionMethodColor);
            }
        }
        if(publicSettings.getMicroRNAs() != null){
            for(MicroRNA microRNA : publicSettings.getMicroRNAs()){
                moflusSQLiteHelper.addMicroRNA(microRNA);
            }
        }
        if(publicSettings.getDots() != null){
            for(Dots dot : publicSettings.getDots() ){
                moflusSQLiteHelper.addDots(dot);
            }
        }
        if(publicSettings.getDataResults() != null){
            for(JDataResult data_result : publicSettings.getDataResults()){
                final DataResult dataResult = new DataResult();
                dataResult.setId(data_result.getId());
                dataResult.setName(data_result.getName());
                dataResult.setPreview_picture_url(data_result.getPreview_picture_url());
                try {
                    dataResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(data_result.getDatetime()));
                } catch (ParseException e) {
                    dataResult.setDatetime(new Date());
                }
                dataResult.setIs_clouded(true);
                ArrayList<DetectionMethodColor> detectionMethodColors = new ArrayList<>();
                for(JDetectionMethodColor jDetectionMethodColor : data_result.getDetection_method_color_id()){
                    DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
                    detectionMethodColor.setId(jDetectionMethodColor.getId());
                    detectionMethodColor.setColor_type(jDetectionMethodColor.getColor_type());
                    detectionMethodColor.setSelected(jDetectionMethodColor.isSelected());
                    detectionMethodColors.add(detectionMethodColor);
                }
                dataResult.setDetectionMethodColors(detectionMethodColors);
                ArrayList graph_data_list = new ArrayList();
                for(JGraphData jGraphData : data_result.getGraph_data()){
                    GraphData graphData = new GraphData();
                    graphData.setId(jGraphData.getId());
                    graphData.setMicroRNA(moflusSQLiteHelper.getMicroRNA(jGraphData.getMicrorna_id()));
                    ArrayList graph_data_value_list = new ArrayList();
                    for(JGraphDataValue jGraphDataValue : jGraphData.getGraph_data_value()){
                        GraphDataValue graphDataValue = new GraphDataValue(jGraphDataValue.getTime(),jGraphDataValue.getValue());
                        graphDataValue.setId(jGraphDataValue.getId());
                        graph_data_value_list.add(graphDataValue);
                    }
                    graphData.setGraphDataValues(graph_data_value_list);
                    graph_data_list.add(graphData);
                }
                dataResult.setGraphDatas(graph_data_list);
                moflusSQLiteHelper.addDataResult(dataResult);
            }
        }
    }

}
