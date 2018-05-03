package com.example.nearme.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserModel implements Parcelable {

    private DatabaseReference databaseReference;

    public UserModel(){
        databaseReference= FirebaseDatabase.getInstance().getReference("users");

    }

    protected UserModel(Parcel in) {
        name = in.readString();
        photo = in.readString();
        userid = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void addInfoUserModel(UserModel userModel,String uid){

        databaseReference.child(uid).setValue(userModel);
    }

    String name;
    String photo;
    String userid;

    public String getUserID() {
        return userid;
    }

    public void setUserID(String userid) {
        this.userid = userid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeString(userid);
    }
}
