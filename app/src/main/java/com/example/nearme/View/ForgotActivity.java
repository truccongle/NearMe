package com.example.nearme.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtRegister,txtLogin;
    Button btnSentEmail;
    EditText edtEmailForgot;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        firebaseAuth = FirebaseAuth.getInstance();
        txtRegister=findViewById(R.id.txt_Register);
        txtLogin =  findViewById(R.id.txt_Login);
        btnSentEmail =  findViewById(R.id.btn_SentEmail);
        edtEmailForgot =  findViewById(R.id.edtEmail_Forgot);

        btnSentEmail.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_SentEmail:
                String email = edtEmailForgot.getText().toString();
                boolean check = CheckEmail(email);

                if(check){
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotActivity.this,"Gửi Email thành công!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    Toast.makeText(ForgotActivity.this,"Email không hợp lệ!",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_Register:
                Intent intent = new Intent(ForgotActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_Login:
                Intent intent1= new Intent(ForgotActivity.this,LoginActivity.class);
                startActivity(intent1);
        }
    }

    private boolean CheckEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
