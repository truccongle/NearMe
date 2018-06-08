package com.example.nearme.View;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.nearme.R;


public class chat_rec extends RecyclerView.ViewHolder  {



    TextView leftText,rightText;

    public chat_rec(View itemView){
        super(itemView);

        leftText = (TextView)itemView.findViewById(R.id.leftText);
        rightText = (TextView)itemView.findViewById(R.id.rightText);


    }
}
