package com.example.nearme.Controller;

import com.example.nearme.Model.CommentModel;

public class CommentController {
    CommentModel commentModel;

    public CommentController() {
        commentModel = new CommentModel();
    }
    public void AddComment(String placeID,CommentModel commentModel){
        commentModel.AddComment(placeID,commentModel);
    }
}
