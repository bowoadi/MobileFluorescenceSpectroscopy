package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.MoFluSService;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Kelas yang mengontrol tampilan untuk register
 */
public class RegisterController extends AppCompatActivity implements View.OnClickListener{

    private EditText email_form;
    private EditText password_form;
    private EditText confirm_password_form;
    private FirebaseAuth firebaseAuth;
    private Button submitButton;

    /**
     * Fungsi yang pertama kali dijalankan saat register view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        viewSetup();
    }

    /**
     * Mengatur tampilan pada view register
     */
    private void viewSetup(){
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Register Form
        email_form = (EditText) findViewById(R.id.email_form);
        password_form = (EditText) findViewById(R.id.password_form);
        confirm_password_form = (EditText) findViewById(R.id.confirm_password_form);
        //Submit Button
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
    }

    /**
     * Mengatur fungsi saat tombol pada menu di tekan
     * @param item menu yang dipilih
     * @return true jika terdapat menu yang dipilih, false jika tidak
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mengatur fungsi saat user menekan view
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_button :
                registerUser();
                break;
        }
    }

    /**
     * Fungsi saat tombol register ditekan ditekan
     */
    private void registerUser(){
        disableRegisterForm();
        String password = password_form.getText().toString();
        String email = email_form.getText().toString();
        if(email.equals("")){
            email_form.setError("Invalid Email");
            enableRegisterForm();
            return;
        }
        if(password.equals("")){
            password_form.setError("Invalid Password");
            enableRegisterForm();
            return;
        }
        String confirmPassword = confirm_password_form.getText().toString();
        if(password.equals(confirmPassword)){
//            firebaseAuth.createUserWithEmailAndPassword(email,password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            enableRegisterForm();
//                            if(!task.isSuccessful()){
//                                Toast.makeText(getApplicationContext(),"Registrasi Gagal",Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            firebaseAuth.getCurrentUser().sendEmailVerification();
//                            Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_LONG).show();
//                            finish();
//                        }
//                    });
            User user = new User();
            user.setUsername(email);
            user.setPassword(password);
            MoFluSService.getService().register(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if(user != null && user.isSuccess()){
                        Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();
                        enableRegisterForm();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Username Exists",Toast.LENGTH_SHORT).show();
                        enableRegisterForm();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Register Failed",Toast.LENGTH_SHORT).show();
                    enableRegisterForm();
                }
            });
        }else{
            confirm_password_form.setError("Password is not equal");
            enableRegisterForm();
        }
    }

    /**
     * Mengaktifkan form register agar dapat diubah-ubah
     */
    private void enableRegisterForm(){
        email_form.setEnabled(true);
        password_form.setEnabled(true);
        confirm_password_form.setEnabled(true);
        submitButton.setEnabled(true);
    }

    /**
     * Menonaktifkan form register agar tidak dapat diubah-ubah
     */
    private void disableRegisterForm(){
        email_form.setEnabled(false);
        password_form.setEnabled(false);
        confirm_password_form.setEnabled(false);
        submitButton.setEnabled(false);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke activity register
     * @param activity berisi activity dari tampilan yang sedang muncul
     */
    public static void toRegisterActivity(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(),RegisterController.class));
    }

}
