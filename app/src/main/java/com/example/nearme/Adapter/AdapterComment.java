package com.example.nearme.Adapter;
 /*
 * @name AdapterComment.
 * @author FPT.
 * @license Copyright (C) 2015 Pioneer Corporation. All Rights Reserved.
 */

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nearme.Model.CommentModel;
import com.example.nearme.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {

    Context context;
    int layout;
    List<CommentModel> commentModelList;


    public AdapterComment(Context context, int layout, List<CommentModel> commentModelList) {
        this.context = context;
        this.layout = layout;
        this.commentModelList = commentModelList;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtTitle, txtContent, txtPoint;

        public ViewHolder(View itemView) {
            super(itemView);

            circleImageView = (CircleImageView) itemView.findViewById(R.id.cicle_ImageUser);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_Title);
            txtContent = (TextView) itemView.findViewById(R.id.txt_Content);
            txtPoint = (TextView) itemView.findViewById(R.id.txt_Point);
        }
    }

    @Override
    public AdapterComment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterComment.ViewHolder holder, int position) {
        final CommentModel commentModel = commentModelList.get(position);
        holder.txtTitle.setText(commentModel.getTitle());
        holder.txtContent.setText(commentModel.getContent());
        holder.txtPoint.setText(commentModel.getRating() + "");
        setImgUser(holder.circleImageView, commentModel.getUserModel().getImg());


    }

    @Override
    public int getItemCount() {
        int soBinhLuan = commentModelList.size();
        if (soBinhLuan > 10) {
            return 10;
        } else {
            return commentModelList.size();
        }

    }
    private void setImgUser(final CircleImageView circleImageView, String linkimg){
        StorageReference storageHinhUser = FirebaseStorage.getInstance().getReference().child("user").child(linkimg);
        long ONE_MEGABYTE = 1024 * 1024;
        storageHinhUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                circleImageView.setImageBitmap(bitmap);
            }
        });
    }
}