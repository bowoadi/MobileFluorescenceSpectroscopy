package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mikhaellopez.circularimageview.CircularImageView;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.FirebaseUserHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Kelas yang mengontrol tampilan untuk profile
 */
public class ProfileController extends AppCompatActivity implements View.OnClickListener{

    private CircularImageView profile_picture;
    private EditText name_form;
    private EditText email_form;
    private EditText clouded_form;
    private Button edit_save_button;
    private FirebaseUserHelper firebaseUserHelper;
    private MoflusSQLiteHelper moflusSQLiteHelper;

    /**
     * Fungsi yang pertama kali dijalankan saat profile view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUserHelper = new FirebaseUserHelper();
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        setContentView(R.layout.profile_activity);
        viewSetup();
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada profile
     */
    private void viewSetup(){
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Profile Form
        profile_picture = (CircularImageView) findViewById(R.id.profile_picture_form);
        //Picasso.with(this).load(firebaseUserHelper.getFirebaseUser().getPhotoUrl()).centerInside().error(R.drawable.preview_test);
        name_form = (EditText) findViewById(R.id.name_form);
//        String name = getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE).getString(MoflusSharedPreferenceHelper.USERNAME,"");
        name_form.setText("");
        email_form = (EditText) findViewById(R.id.email_form);
//        email_form.setText(firebaseUserHelper.getFirebaseUser().getEmail());
        email_form.setText(MoflusSharedPreferenceHelper.getMoFluSSharedPreference(this).getString(MoflusSharedPreferenceHelper.USERNAME,""));
        clouded_form = (EditText) findViewById(R.id.clouded_form);
        clouded_form.setText(String.valueOf(moflusSQLiteHelper.getDataResultCloudedCount()));
        //Edit Button
        edit_save_button = (Button) findViewById(R.id.edit_save_profile_button);
        edit_save_button.setOnClickListener(this);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke activity profile
     * @param activity berisi activity dari tampilan yang sedang muncul
     */
    public static void toProfileActivity(Activity activity){
        activity.startActivity(new Intent(activity,ProfileController.class));
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_save_profile_button :
                //editProfileForm();
                break;
        }
    }

//    private void editProfileForm(){
//        if(edit_save_button.getText().toString().equals("Edit")){
//            enableProfileForm();
//        }else{
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//            View view = LayoutInflater.from(this).inflate(R.layout.profile_update_loading_view,null,false);
//            final AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setView(view);
//            alertDialog.setCancelable(false);
//            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.show();
//            final String name = name_form.getText().toString();
//            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
//                    .Builder()
//                    .setDisplayName(name)
//                    .build();
//            firebaseUserHelper
//                    .getFirebaseUser()
//                    .updateProfile(userProfileChangeRequest)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE).edit().putString(MoflusSharedPreferenceHelper.USERNAME,name).apply();
//                                Toast.makeText(getApplicationContext(),"Save Successful",Toast.LENGTH_LONG).show();
//                                alertDialog.dismiss();
//                                disableProfileForm();
//                            }else{
//                                alertDialog.dismiss();
//                                Toast.makeText(getApplicationContext(),"Profile Not Saved",Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//            name_form.setEnabled(false);
//            edit_save_button.setText("Edit");
//        }
//    }

//    private void enableProfileForm(){
//        name_form.setEnabled(true);
//        edit_save_button.setText("Save");
//    }
//
//    private void disableProfileForm(){
//        name_form.setEnabled(false);
//        edit_save_button.setText("Edit");
//    }

}
