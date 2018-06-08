package com.example.nearme.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class CommentModel implements Parcelable {
    long like;
    double rating;
    UserModel userModel;
    String content, title, userid,commentID;

    public CommentModel() {
    }

    protected CommentModel(Parcel in) {
        like = in.readLong();
        commentID=in.readString();
        rating = in.readDouble();
        userModel = in.readParcelable(UserModel.class.getClassLoader());
        content = in.readString();
        title = in.readString();
        userid = in.readString();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(like);
        dest.writeString(commentID);
        dest.writeDouble(rating);
        dest.writeParcelable(userModel, flags);
        dest.writeString(content);
        dest.writeString(title);
        dest.writeString(userid);
    }

    public void AddComment(String placeID, CommentModel commentModel) {
        DatabaseReference nodeComment = FirebaseDatabase.getInstance().getReference().child("comments");
        String commentID = nodeComment.child(placeID).push().getKey();

        nodeComment.child(placeID).child(commentID).setValue(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                        }
                    }



        });

    }
}
