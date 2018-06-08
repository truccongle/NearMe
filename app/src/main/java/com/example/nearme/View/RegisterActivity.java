package com.example.nearme.View;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class  RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText edtEmail,edtPass,edtRePass;
    FirebaseAuth firebaseAuth;
    RegisterController registerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister =  findViewById(R.id.btn_Register);
        edtEmail =  findViewById(R.id.edt_Email);
        edtPass =  findViewById(R.id.edt_Pass);
        edtRePass = findViewById(R.id.edt_Re_Passs);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {

        final String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        String rePass = edtRePass.getText().toString();
        String message ="Bạn chưa nhập";

        if(email.trim().length() == 0 ){
            message += "Email";
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }else if(pass.trim().length() == 0){
            message += "Mật khẩu";
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }else if(!rePass.equals(pass)){
            Toast.makeText(this,"Mật khẩu không chính xác",Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        UserModel userModel = new UserModel();
                        userModel.setUsername(email);
                        userModel.setImg("user.png");
                        userModel.setAdmin(false);
                        String uid = task.getResult().getUser().getUid();

                        registerController = new RegisterController();
                        registerController.AddInfoUserController(userModel,uid);
                        Toast.makeText(RegisterActivity.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}
