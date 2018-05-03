package com.example.nearme.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, FirebaseAuth.AuthStateListener {

    SignInButton btnLoginGG;
    LoginButton btnLoginFB;
    Button btnLogin;
    TextView txtRegister,txtForgotPass;
    EditText edtEmail,edtPassword;

    GoogleApiClient apiClient;
    public static int REQUESTCODE_LOGIN_GOOGLE = 1;
    public static int CHECK_PROVIDER_LOGIN = 1;
    FirebaseAuth firebaseAuth;
    CallbackManager mCallbackFacebook;
    LoginManager loginManager;
    List<String> permissionFacebook = Arrays.asList("user_link","user_gender","user_age_range","email","public_profile");

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mCallbackFacebook = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

     btnLoginGG= findViewById(R.id.btn_login_google);
     btnLoginFB=findViewById(R.id.btn_login_fb);
     btnLogin=findViewById(R.id.btn_login);
     txtRegister=findViewById(R.id.txt_register);
     txtForgotPass=findViewById(R.id.txt_forgot_pass);
     edtEmail=findViewById(R.id.edt_email);
     edtPassword=findViewById(R.id.edt_pass);

        btnLoginGG.setOnClickListener(this);
        btnLoginFB.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("saveLogin",MODE_PRIVATE);
        createClientGG();
    }

    private void loginFacebook(){
        loginManager.logInWithReadPermissions(this,permissionFacebook);
        loginManager.registerCallback(mCallbackFacebook, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CHECK_PROVIDER_LOGIN = 2;
                String tokenID = loginResult.getAccessToken().getToken();
                checkFirebaseAuth(tokenID);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    //Khởi tạo client cho đăng nhập google
    private void createClientGG(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

    }

    //end hởi tạo client cho đăng nhập google

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }

    //Mở form đăng nhập bằng google
    private void loginGoogle(GoogleApiClient apiClient){
        CHECK_PROVIDER_LOGIN = 1;
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(intent,REQUESTCODE_LOGIN_GOOGLE);
        Toast.makeText(getApplicationContext(),"DC MM",Toast.LENGTH_LONG).show();
    }

    //end mở form đăng nhập bằng google

    //Lấy stokenID đã đăng nhập bằng google để đăng nhập trên Firebase
    private void checkFirebaseAuth(String tokenID){
        if(CHECK_PROVIDER_LOGIN == 1){
            AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID,null);
            firebaseAuth.signInWithCredential(authCredential);
        }else if(CHECK_PROVIDER_LOGIN == 2){
            AuthCredential authCredential = FacebookAuthProvider.getCredential(tokenID);
            firebaseAuth.signInWithCredential(authCredential);
        }

    }
    //end Lấy stokenID đã đăng nhập bằng google để đăng nhập trên Firebase

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCODE_LOGIN_GOOGLE){
            if(resultCode == RESULT_OK){
                GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = signInResult.getSignInAccount();
                String tokenID = account.getIdToken();
                checkFirebaseAuth(tokenID);
            }
        }else{
            mCallbackFacebook.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Lắng nghe sự kiện user click vào button đăng nhập google, facebook và email account
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_login_google:
                loginGoogle(apiClient);
                break;

            case R.id.btn_login_fb:
                loginFacebook();
                break;

            case R.id.txt_register:
                Intent reg = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(reg);
                break;

            case R.id.btn_login:
                login();
                break;

            case R.id.txt_forgot_pass:
                Intent forgot = new Intent(LoginActivity.this,ForgotPassActivity.class);
                startActivity(forgot);
                break;
        }
    }
    //end

    private void login(){
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Login Fail!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Sự kiện kiểm tra xem người dùng đã nhập thành công hay đăng xuất
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID",user.getUid());
            editor.commit();

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{

        }
    }
    //End
}

