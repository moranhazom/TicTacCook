package com.example.moran.tictaccook;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.moran.tictaccook.model.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends Activity
                           implements EmailPasswordFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Log.d("TAG", "AuthActivity: onCreate");
        EmailPasswordFragment emailPasswordFragment = EmailPasswordFragment.newInstance();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.auth_container, emailPasswordFragment);
        tran.commit();

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }

    }

    static final int REQUEST_WRITE_STORAGE = 11;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_WRITE_STORAGE){
            Log.d("TAG", "REQUEST_WRITE_STORAGE");
        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = Model.instance.getCurrentUser();
//        Log.d("TAG", "user is signed in");
//        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    /**
     * callback from EmailPasswordFragment.OnFragmentInteractionListener
     */
    @Override
    public void onRegisterSuccess(String uId) {
        Log.d("TAG", "AuthActivity : onRegister uid: " + uId);
        Toast.makeText(AuthActivity.this, "Authentication success",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * callback from EmailPasswordFragment.OnFragmentInteractionListener
     */
    @Override
    public void onSignInSuccess(String uId) {
        Log.d("TAG", "AuthActivity : onSignIn uId:" + uId);

        Toast.makeText(AuthActivity.this, "SignIn success",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterFailed(String error) {
        Toast.makeText(AuthActivity.this, error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInFailed(String error) {
        if(error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
            Toast.makeText(AuthActivity.this, "You need to register first",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(AuthActivity.this, "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
