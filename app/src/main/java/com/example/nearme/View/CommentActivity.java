package com.example.nearme.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearme.Controller.CommentController;
import com.example.nearme.Model.CommentModel;
import com.example.nearme.R;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtName, txtAddress,txtComment;
    Toolbar toolbar;
    EditText edtTitle,edtContent;
    String placeID;
    RatingBar ratingBar_Comment;
    SharedPreferences sharedPreferences;

    CommentController commentController;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        placeID = getIntent().getStringExtra("placeID");
        Log.d("placeid",placeID+"");
        String name = getIntent().getStringExtra("name");
        Log.d("placeid",name+"");
        String address = getIntent().getStringExtra("address");
        Log.d("placeid",address+"");
        sharedPreferences = getSharedPreferences("savelogin",MODE_PRIVATE);

        txtName = (TextView) findViewById(R.id.txt_Name);
        txtAddress = (TextView) findViewById(R.id.txt_Address);
        txtComment = (TextView) findViewById(R.id.txt_Comment);
        edtTitle = (EditText) findViewById(R.id.edt_Title);
        edtContent = (EditText) findViewById(R.id.edt_Content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ratingBar_Comment=findViewById(R.id.ratingbar_Comment);
        commentController = new CommentController();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        txtName.setText("Địa điểm: "+name);
        txtAddress.setText("Địa chỉ: "+address);

        txtComment.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txt_Comment:
                CommentModel commentModel = new CommentModel();
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();
                int rating=ratingBar_Comment.getNumStars();
                String userid = sharedPreferences.getString("userid","");
                Log.d("userid",userid);

                commentModel.setTitle(title);
                commentModel.setContent(content);
                commentModel.setRating(rating);
                commentModel.setLike(0);
                commentModel.setUserid(userid);
                commentController.AddComment(placeID,commentModel);
                Toast.makeText(getApplicationContext(),"Thêm bình luận thành công",Toast.LENGTH_SHORT).show();

                edtTitle.setText("");
                edtContent.setText("");
                ratingBar_Comment.setNumStars(0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }
}