package com.example.nearme.View;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nearme.Controller.RegisterController;
import com.example.nearme.Model.UserModel;
import com.example.nearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDangKy;
    EditText edEmailDK,edPasswordDK,edNhapLaiPasswordDK;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RegisterController registerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnDangKy = (Button) findViewById(R.id.btnDangKy);
        edEmailDK = (EditText) findViewById(R.id.edEmailDK);
        edPasswordDK = (EditText) findViewById(R.id.edPasswordDK);
        edNhapLaiPasswordDK = (EditText) findViewById(R.id.edNhapLaiPasswordDK);

        btnDangKy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        progressDialog.setMessage("LOading..");
        progressDialog.setIndeterminate(true);

        progressDialog.show();

        final String email = edEmailDK.getText().toString();
        String matkhau = edPasswordDK.getText().toString();
        String nhaplaimatkhau = edNhapLaiPasswordDK.getText().toString();
        String thongbaoloi = "Err";

        if(email.trim().length() == 0 ){
            thongbaoloi += "Loi email";
            Toast.makeText(this,thongbaoloi,Toast.LENGTH_SHORT).show();
        }else if(matkhau.trim().length() == 0){
            thongbaoloi += "Loi pass";
            Toast.makeText(this,thongbaoloi,Toast.LENGTH_SHORT).show();
        }else if(!nhaplaimatkhau.equals(matkhau)){
            Toast.makeText(this,"Nhap lai",Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        UserModel userModel = new UserModel();
                        userModel.setName(email);
                        userModel.setPhoto("user.png");
                        String uid = task.getResult().getUser().getUid();

                        registerController = new RegisterController();
                        registerController.addInfoUserModel(userModel,uid);
                        Toast.makeText(RegisterActivity.this,"DK ok",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}