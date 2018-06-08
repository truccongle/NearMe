package com.example.nearme.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserModel implements Parcelable {

    private DatabaseReference dataNodeUser;

    public UserModel(){
        dataNodeUser = FirebaseDatabase.getInstance().getReference().child("users");
    }

    protected UserModel(Parcel in) {
        username = in.readString();
        img = in.readString();
        userID = in.readString();
        isAdmin = in.readByte() != 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void AddInfoModel(UserModel thanhVienModel,String uid){

        dataNodeUser.child(uid).setValue(thanhVienModel);
    }

    String username;
    String img;
    String userID;
    boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(img);
        dest.writeString(userID);
        dest.writeByte((byte) (isAdmin ? 1 : 0));

    }
}