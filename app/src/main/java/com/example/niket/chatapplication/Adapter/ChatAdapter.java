package com.example.niket.chatapplication.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by Niket on 2/2/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ContactViewHolder> {
    private ArrayList<MessagePojo> pojoList;
    private MessagePojo messagePojo;
    private String receiverID, senderID;
    private String deleteMessageKey;
    private Activity activity;

    public ChatAdapter(ArrayList<MessagePojo> pojoList, String senderID, String receiverID, Activity activity) {
        this.pojoList = pojoList;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_message, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ContactViewHolder holder, int position) {
        messagePojo = pojoList.get(position);
        Log.e("message id is : ", messagePojo.getMessageId());
        if (senderID.equals(messagePojo.getSenderID()) && receiverID.equals(messagePojo.getReceiverID())
                || senderID.equals(messagePojo.getReceiverID()) && receiverID.equals(messagePojo.getSenderID())) {

            if (senderID.equals(messagePojo.getSenderID())) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.setMargins(140, 8, 20, 8);
                holder.cardView.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.setMargins(20, 8, 140, 8);
                holder.cardView.setLayoutParams(layoutParams);
                holder.cardView.setCardBackgroundColor(ResourcesCompat.getColor(activity.getResources(), R.color.blue, null));
                holder.textMessage.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.white, null));
            }

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("delete sender id", pojoList.get(holder.getAdapterPosition()).getMessageId());
                    if (senderID.equals(pojoList.get(holder.getAdapterPosition()).getSenderID())) {
                        Log.e("delete sender id inside", messagePojo.getSenderID());
                        deleteMessageKey = pojoList.get(holder.getAdapterPosition()).getMessageId();
                        Log.e("delete", FirebaseDatabase.getInstance().getReference("data").child("groupID").child(deleteMessageKey).toString());
                        messagePojo = pojoList.get(holder.getAdapterPosition());
                        showAlertDialogButtonClicked(view, deleteMessageKey, holder.getAdapterPosition());
                        return true;
                    }
                    return false;

                }
            });
            holder.textMessage.setText(messagePojo.getMessage().trim());

        } else {

            holder.cardView.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.cardView.setLayoutParams(layoutParams);
        }
    }


    @Override
    public int getItemCount() {
        return pojoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView textMessage;
        CardView cardView;
        RelativeLayout messageLayout;

        ContactViewHolder(View itemView) {
            super(itemView);
            messageLayout = itemView.findViewById(R.id.message_layout);
            cardView = itemView.findViewById(R.id.cv_message);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }

    private void showAlertDialogButtonClicked(View view, final String deleteId, final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete!");
        builder.setMessage("Are you sure, you want to delete?");


        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference delete = FirebaseDatabase.getInstance().getReference("data").child("groupID").child(deleteId);
                        delete.removeValue();
                        dialog.dismiss();
                    }
                }
        );

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}


