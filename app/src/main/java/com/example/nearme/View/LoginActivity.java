package com.example.nearme.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearme.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,FirebaseAuth.AuthStateListener{
    Button btnLogin,btnFacebook,btnGoogle;
    TextView txtRegister,txtForgot;
    EditText edtEmail,edtPass;
    LoginManager loginManager;
    CallbackManager callbackManager;
    GoogleApiClient apiClient;
    FirebaseAuth firebaseAuth;
    public static int REQUESTCODE_LOGIN_GOOGLE=99;
    public static int CHECK_PROVIER=0;
    List<String> permision= Arrays.asList("email","public_profile");
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();
        loginManager=LoginManager.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        init();
        sharedPreferences = getSharedPreferences("savelogin",MODE_PRIVATE);

        CreateClient();


    }

    private void LoginFacebook() {
        loginManager.logInWithReadPermissions(this,permision);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CHECK_PROVIER=2;
                String tokenID=loginResult.getAccessToken().getToken();
                AuthFirebase(tokenID);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }


    private void init() {
        btnGoogle=findViewById(R.id.btn_Google);
        btnFacebook=findViewById(R.id.btn_Facebook);
        btnLogin=findViewById(R.id.btn_Login);
        txtRegister=findViewById(R.id.txt_Register);
        txtForgot=findViewById(R.id.txt_Forgot);
        edtEmail=findViewById(R.id.edt_Email);
        edtPass=findViewById(R.id.edt_Pass);

        btnGoogle.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtForgot.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private  void CreateClient(){
        GoogleSignInOptions signInOptions= new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
         apiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener((FirebaseAuth.AuthStateListener) this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener((FirebaseAuth.AuthStateListener) this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void LoginGoogle(GoogleApiClient apiClient){
        CHECK_PROVIER=1;
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(intent,REQUESTCODE_LOGIN_GOOGLE);
    }
    private  void AuthFirebase(String tokenID){
        if (CHECK_PROVIER==1){
            AuthCredential  credential= GoogleAuthProvider.getCredential(tokenID,null);
            firebaseAuth.signInWithCredential(credential);
        } else  if (CHECK_PROVIER==2){
            AuthCredential   credential= FacebookAuthProvider.getCredential(tokenID);
            firebaseAuth.signInWithCredential(credential);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUESTCODE_LOGIN_GOOGLE){
            if (resultCode==RESULT_OK){
                GoogleSignInResult signInResult=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount  account=signInResult.getSignInAccount();
                String tokenID=account.getIdToken();
                AuthFirebase(tokenID);

            }

        }
        else {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_Google:
                LoginGoogle(apiClient);
                break;
            case R.id.btn_Facebook:
                LoginFacebook();
                break;
            case R.id.txt_Forgot:
                Intent intent= new Intent(getApplicationContext(),ForgotActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_Register:
                Intent intent1=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_Login:
                Login();
                break;

        }
    }
    private void Login(){
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        if(!email.equals("") && !pass.equals("")){
            firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Lỗi đăng nhập",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
            Toast.makeText(LoginActivity.this,"Vui lòng nhập Email,Password",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if (user!=null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userid",user.getUid());
            editor.commit();
            Toast.makeText(getApplicationContext(),"Login success!",Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        else {


        }
    }
}
