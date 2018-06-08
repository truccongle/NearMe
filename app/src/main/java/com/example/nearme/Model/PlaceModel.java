package com.example.nearme.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.nearme.Model.Interfaces.NearmeInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaceModel implements Parcelable {
    private String placeID,close,open,type,pirce,phone,name,address,photo;
    private long like;
    private  Double latitude,longitude,distance;
    private boolean verified;
    List<CommentModel> commentModelList;
    DatabaseReference nodeRoot;
    public PlaceModel() {
        nodeRoot= FirebaseDatabase.getInstance().getReference();
    }

    protected PlaceModel(Parcel in) {
        placeID = in.readString();
        close = in.readString();
        photo=in.readString();
        open = in.readString();
        type = in.readString();
        pirce = in.readString();
        phone = in.readString();
        name = in.readString();
        address = in.readString();
        like = in.readLong();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            distance = null;
        } else {
            distance = in.readDouble();
        }
        verified = in.readByte() != 0;
        commentModelList = in.createTypedArrayList(CommentModel.CREATOR);
    }

    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public List<CommentModel> getCommentModelList() {
        return commentModelList;
    }

    public void setCommentModelList(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPirce() {
        return pirce;
    }

    public void setPirce(String pirce) {
        this.pirce = pirce;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }


    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }


    public  void getListPlace(final NearmeInterface nearmeInterface, final Location curLocation){
        final ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot snapshotPlace=dataSnapshot.child("places");
                Log.d("kiemtra",snapshotPlace+"");

                for(DataSnapshot valuePlace:snapshotPlace.getChildren()){
                    PlaceModel placeModel=valuePlace.getValue(PlaceModel.class);
                    String strplaceID=valuePlace.getKey();
                    placeModel.setPlaceID(strplaceID);
//
//                    DataSnapshot snapshotPhoto=dataSnapshot.child("images").child(valuePlace.getKey());
//                    List<String> photoList=new ArrayList<>();
//                    for(DataSnapshot valuePhoto:snapshotPhoto.getChildren() ){
//                        photoList.add(valuePhoto.getValue(String.class));
//                    }
//
//                    placeModel.setImages(photoList);
//                    Log.d("key",valuePlace.getKey()+""+placeModel.getPlaceID());

                    //get list commnet
                    DataSnapshot snapshotComment=dataSnapshot.child("comments").child(valuePlace.getKey());
                    List<CommentModel> commentModels=new ArrayList<>();
                    Log.d("comment",snapshotComment+"");


                    for (DataSnapshot valueComment:snapshotComment.getChildren()){
                        CommentModel commentModel=valueComment.getValue(CommentModel.class);
                        Log.d("user",commentModel.getUserid()+"");
                        UserModel userModel=dataSnapshot.child("users").child(commentModel.getUserid()).getValue(UserModel.class);
                    commentModel.setUserModel(userModel);
                        commentModels.add(commentModel);
                    }
                    placeModel.setCommentModelList(commentModels);


                    Location location=new Location("");
                    location.setLatitude(placeModel.getLatitude());
                    location.setLongitude(placeModel.getLongitude());

                    double distance= curLocation.distanceTo(location)/1000;
                    Log.d("dis",distance+"");
                    placeModel.setDistance(distance);


                    nearmeInterface.getListPlaceModel(placeModel);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        nodeRoot.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeID);
        dest.writeString(close);
        dest.writeString(photo);
        dest.writeString(open);
        dest.writeString(type);
        dest.writeString(pirce);
        dest.writeString(phone);
        dest.writeString(name);

        dest.writeString(address);
        dest.writeLong(like);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (distance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(distance);
        }
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeTypedList(commentModelList);
    }
}
