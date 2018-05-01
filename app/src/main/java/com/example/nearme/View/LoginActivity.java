package com.example.nearme.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.example.nearme.R;
import com.facebook.FacebookSdk;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
   SignInButton btnLoginGoogle,btnLoginFB;
    GoogleApiClient mApiClient;

    final static int LOGIN_GOOGLE=1;
    final static int LOGIN_FB=2;
    final  static int CHECK_PROVIDER=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        createClientGoogle();
        intiViews();



    }

    private void AuthAccount(String tokenID) {


    }

    private void LoginGoogle(GoogleApiClient mApiClient) {
        Intent intent= Auth.GoogleSignInApi.getSignInIntent(mApiClient);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==LOGIN_GOOGLE){
            if (resultCode==RESULT_OK){
                GoogleSignInResult signInResult=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount Account=signInResult.getSignInAccount();
                String tokenID=Account.getIdToken();
            }
        }
    }

    private void intiViews() {
        btnLoginGoogle=findViewById(R.id.btn_login_google);
        btnLoginFB=findViewById(R.id.btn_login_fb);
    }

    private void createClientGoogle() {
        GoogleSignInOptions mSignInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,mSignInOptions)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        int id= v.getId();
        switch (id) {
            case R.id.btn_login_google:
                break;


        }
    }
}

