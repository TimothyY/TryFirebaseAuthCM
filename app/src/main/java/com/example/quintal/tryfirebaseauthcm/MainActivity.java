package com.example.quintal.tryfirebaseauthcm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TryFirebaseAuthCM";
    ImageView ivProfpic;
    TextView tvCurrentUser;

    //firebase auth ui
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivProfpic = findViewById(R.id.ivProfPic);
        tvCurrentUser = findViewById(R.id.tvCurrentUser);

        printHashKey(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void loginButtonClicked(View view) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)      // Set logo drawable
                        .setTheme(R.style.CustomTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }


    public void logoutButtonClicked(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            ivProfpic.setVisibility(View.VISIBLE);
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(ivProfpic);
            Log.v("path",FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            tvCurrentUser.setVisibility(View.VISIBLE);
            tvCurrentUser.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+" is online.");
        }else{
            ivProfpic.setVisibility(View.GONE);
            tvCurrentUser.setVisibility(View.GONE);
        }

    }

    public void printHashKey(Context pContext){
        try{
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md= MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey=new String(Base64.encode(md.digest(),0));
                Log.i(TAG,"printHashKey()HashKey:"+hashKey);
            }
        }catch(NoSuchAlgorithmException e){
            Log.e(TAG,"printHashKey()",e);
        }catch(Exception e){
            Log.e(TAG,"printHashKey()",e);
        }
    }
}
