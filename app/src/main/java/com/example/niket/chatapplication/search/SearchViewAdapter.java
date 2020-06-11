package com.example.niket.chatapplication.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.IndividualChatPage;
import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.pojoClass.SignupModelClass;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.MyViewHolder> {

    private ArrayList<SignupModelClass> signUpModelClassArrayList;
    private Context mContext;
    private String senderId;


    SearchViewAdapter(Context mContext, ArrayList<SignupModelClass> signUpModelClassArrayList, String senderId) {
        this.mContext = mContext;
        this.signUpModelClassArrayList = signUpModelClassArrayList;
        this.senderId = senderId;
    }


    @NonNull
    @Override
    public SearchViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchview_item, parent, false);
        return new SearchViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewAdapter.MyViewHolder holder, int position) {

        SignupModelClass signupModelClass = signUpModelClassArrayList.get(position);


        if (senderId.equals(signupModelClass.getUserID())) {
            holder.cv.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.cv.setLayoutParams(layoutParams);
            //return;
        }


        holder.usernameTv.setText(signupModelClass.getUsername());


        Glide.with(mContext).load(signupModelClass.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignupModelClass signupModelClass = signUpModelClassArrayList.get(holder.getAdapterPosition());

                ImageView profile_dialog;
                Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.main_profile_dialog);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                Glide.with(mContext).load(signupModelClass.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                if(window!=null)
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupModelClass signupModelClass = signUpModelClassArrayList.get(holder.getAdapterPosition());

                Intent i = new Intent(mContext, IndividualChatPage.class);
                i.putExtra("name", signupModelClass.getUsername());
                i.putExtra("image", signupModelClass.getUserImage());
                i.putExtra("ReceiverID", signupModelClass.getUserID());

                mContext.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return signUpModelClassArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView usernameTv, messageTv;
        CardView cv;
        MyViewHolder(View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.textViewUserName);
            messageTv = itemView.findViewById(R.id.textViewMessage);
            image = itemView.findViewById(R.id.image);
            cv = itemView.findViewById(R.id.cv);
        }
    }
}