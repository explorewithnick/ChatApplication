package com.example.niket.chatapplication.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.IndividualChatPage;
import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ContactViewHolder> {

    private ArrayList<MyPojo> pojos;
    private Context context;
    private MyPojo myPojo;
    private String senderId;
    private ArrayList<String> arrayListForDelete;
    private String messageKey;


    public MyCustomAdapter(Context context, ArrayList<MyPojo> pojos, String senderId, ArrayList<String> arrayListForDelete) {
        this.context = context;
        this.pojos = pojos;
        this.senderId = senderId;
        this.arrayListForDelete = arrayListForDelete;


    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position) {
        myPojo = pojos.get(position);


        if (senderId.equals(myPojo.getID())) {
            holder.cardView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.cardView.setLayoutParams(layoutParams);

        }


        holder.textName.setText(myPojo.getName());
        //holder.textName.setTextSize(20);

        //holder.textNumber.setText(myPojo.getMobile());
        holder.textNumber.setText(myPojo.getEmail());
        Glide.with(context).load(myPojo.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profileImage);


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPojo = pojos.get(position);

                ImageView profile_dialog;
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.main_profile_dialog);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                Glide.with(context).load(myPojo.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                if(window!=null)
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPojo = pojos.get(position);

                Intent i = new Intent(context, IndividualChatPage.class);
                i.putExtra("name", myPojo.getName());
                i.putExtra("number", myPojo.getMobile());
                i.putExtra("image", myPojo.getImage_URL());
                i.putExtra("ReceiverID", myPojo.getID());

                context.startActivity(i);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myPojo = pojos.get(position);

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.delete);
                Button b = dialog.findViewById(R.id.delete);
                dialog.show();

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        messageKey = arrayListForDelete.get(position);
                        Log.d("12345", "onClick: " + messageKey);
                        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("data").child("users").child(messageKey);
                        DatabaseReference databaseReferenceUserMessage = FirebaseDatabase.getInstance().getReference("data").child("chats").child(myPojo.getName());

                        databaseReferenceUser.removeValue();
                        databaseReferenceUserMessage.removeValue();

                        arrayListForDelete.clear();
                        dialog.dismiss();
                    }
                });


                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojos.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textNumber;
        CircleImageView profileImage;
        CardView cardView;

        ContactViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv);
            profileImage = itemView.findViewById(R.id.image);

            textName = itemView.findViewById(R.id.textViewUserName);
            textNumber = itemView.findViewById(R.id.textViewMessage);
        }
    }
}

