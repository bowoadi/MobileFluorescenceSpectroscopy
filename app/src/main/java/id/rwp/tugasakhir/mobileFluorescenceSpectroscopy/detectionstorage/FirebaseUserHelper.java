package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by ranuwp on 3/2/2017.
 */

public class FirebaseUserHelper {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public FirebaseUserHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public boolean isLogin(){
        return firebaseAuth.getCurrentUser() != null;
    }

    public void signOut(){
        firebaseAuth.signOut();
    }

    public boolean isEmailVerified(){
        return firebaseAuth.getCurrentUser().isEmailVerified();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public FirebaseUser getFirebaseUser(){
        return firebaseAuth.getCurrentUser();
    }

    public DatabaseReference getFirebaseDataResult(){
        return databaseReference.child("user").child(getFirebaseUser().getUid()).child("data_result");
    }

    public StorageReference getStorageReference() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return storageReference.child("user/"+firebaseUser.getUid()+"/data_result");
    }
}
