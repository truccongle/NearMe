package com.example.nearme.Controller;

import com.example.nearme.Model.UserModel;

public class RegisterController {
    UserModel userModel;
    public RegisterController(){
        userModel=new UserModel();
    }
    public  void AddInfoUserController(UserModel userModel,String uid){
        userModel.AddInfoModel(userModel,uid);
    }
}
