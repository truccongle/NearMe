package com.example.nearme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nearme.Model.CommentModel;
import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;
import com.example.nearme.View.PlaceDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerNearme extends RecyclerView.Adapter<AdapterRecyclerNearme.ViewHolder> {
    List<PlaceModel> placeModelList;
    int resouce;

    Context context;
    public AdapterRecyclerNearme(Context context,List<PlaceModel> placeModelList,int resouce){
        this.placeModelList=placeModelList;
        this.resouce=resouce;
        this.context=context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNamePlace,txtTitle,txtTitle2,txtContent,txtContent2,txtPoint,txtPoint2
                ,txtTotalComment,txtTotalPoint,txtType,txtAddress,txtDistance;
        ImageView imgVerified;
        ImageView imgPhoto;
        CardView cardViewNearme;
        CircleImageView cirUser,cirUser2;
        LinearLayout container,container2;
        public ViewHolder(View itemView) {
            super(itemView);
            cardViewNearme=itemView.findViewById(R.id.cardView_Nearme);
            txtNamePlace=itemView.findViewById(R.id.txt_Name_Place);
            imgVerified=itemView.findViewById(R.id.img_Verified);
            imgPhoto=itemView.findViewById(R.id.img_Photo_Place);
            txtTitle=itemView.findViewById(R.id.txt_Title);
            txtContent=itemView.findViewById(R.id.txt_Content);
            txtTitle2=itemView.findViewById(R.id.txt_Tilte2);
            txtContent2=itemView.findViewById(R.id.txt_Content2);
            cirUser=itemView.findViewById(R.id.cicle_ImageUser);
            cirUser2=itemView.findViewById(R.id.cicle_ImageUser2);
            container=itemView.findViewById(R.id.container_Comment);
            container2=itemView.findViewById(R.id.container_Comment2);
            txtTotalComment=itemView.findViewById(R.id.txt_Total_Comment);
            txtType=itemView.findViewById(R.id.txt_Type);
            txtPoint=itemView.findViewById(R.id.txt_Point);
            txtPoint2=itemView.findViewById(R.id.txt_Point2);
            txtTotalPoint=itemView.findViewById(R.id.txt_Total_Point);
            txtAddress=itemView.findViewById(R.id.txt_Address);
            txtDistance=itemView.findViewById(R.id.txt_Distance);



        }
    }
    @NonNull
    @Override
    public AdapterRecyclerNearme.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(resouce,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRecyclerNearme.ViewHolder holder, int position) {
        final PlaceModel placeModel= placeModelList.get(position);
        holder.txtNamePlace.setText(placeModel.getName());
        holder.txtType.setText(placeModel.getType());
        if (placeModel.isVerified()){
            holder.imgVerified.setVisibility(View.VISIBLE);
        }
     if ( placeModel.getPhoto().length()>0){
         StorageReference storagePhoto= FirebaseStorage.getInstance().getReference().child("photo").child(placeModel.getPhoto());
         long ONE_MEGABYTE=1024*1024;
         storagePhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
             @Override
             public void onSuccess(byte[] bytes) {
                 Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                 holder.imgPhoto.setImageBitmap(bitmap);
             }
         });

     }
     holder.txtAddress.setText(placeModel.getAddress());
        holder.txtDistance.setText(String.format("%.1f",placeModel.getDistance())+" km");
     if (placeModel.getCommentModelList().size()>0) {
         CommentModel commentModel = placeModel.getCommentModelList().get(0);
         holder.txtTitle.setText(commentModel.getTitle());
         holder.txtContent.setText(commentModel.getContent());
         holder.txtPoint.setText(commentModel.getRating()+"");
         setImageUser(holder.cirUser,commentModel.getUserModel().getImg());
         if (placeModel.getCommentModelList().size() > 1) {
             CommentModel commentModel2 = placeModel.getCommentModelList().get(1);
             holder.txtTitle2.setText(commentModel2.getTitle());
             holder.txtContent2.setText(commentModel2.getContent());
             holder.txtPoint2.setText(commentModel2.getRating()+"");
             setImageUser(holder.cirUser2,commentModel.getUserModel().getImg());
            Log.d("img",commentModel.getUserModel().getImg()+""+commentModel.getUserModel());


         }
         holder.txtTotalComment.setText(placeModel.getCommentModelList().size()+"");
         double totalPoint=0;
         for (CommentModel commentModel1:placeModel.getCommentModelList()){
             totalPoint+=commentModel1.getRating();
         }
         double avg=totalPoint/(placeModel.getCommentModelList().size());
        holder.txtTotalPoint.setText(String.format("%.1f",avg));

     }
     else {
            holder.container.setVisibility(View.GONE);
         holder.container2.setVisibility(View.GONE);
            holder.txtTotalComment.setText("0");



     }
       holder.cardViewNearme.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(context, PlaceDetailActivity.class);
               intent.putExtra("place",placeModel);
               context.startActivity(intent);

           }
       });
    }


    private void setImageUser(final  CircleImageView circleImageView,String link){
        StorageReference storagePhoto= FirebaseStorage.getInstance().getReference().child("user").child(link);
        long ONE_MEGABYTE=1024*1024;
        storagePhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                circleImageView.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }


}
