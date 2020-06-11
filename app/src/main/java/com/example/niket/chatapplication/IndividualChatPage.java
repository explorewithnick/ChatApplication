package com.example.niket.chatapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.Adapter.ChatAdapter;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class IndividualChatPage extends AppCompatActivity {

    CircleImageView circleImageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceToAddChat;
    String image;
    ImageView backButton;
    DatabaseReference databaseReferenceSender;
    TextView textView, textViewOnline;
    CircleImageView send;
    ImageView emoImageView;
    EmojiconEditText message;
    View rootView;
    EmojIconActions emojIcon;
    StorageReference storageReference;
    String receiverID, senderID, name, senderName;
    MessagePojo messagePojo;
    MyPojo myPojo;
    ArrayList<MessagePojo> messagePojoArrayList = new ArrayList<>();
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String messageKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_page);

        rootView = findViewById(R.id.linear);
        circleImageView = findViewById(R.id.profileimage);
        textView = findViewById(R.id.name);
        textViewOnline = findViewById(R.id.online_status);
        send = findViewById(R.id.send);
        message = findViewById(R.id.editMessage);
        recyclerView = findViewById(R.id.recyclerview_message);

        //set send button set as enabled=false
        send.setVisibility(View.INVISIBLE);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        SharedPreferences sharedPreferences = getSharedPreferences("save", 0);
        senderID = sharedPreferences.getString("senderID", null);
        senderName = sharedPreferences.getString("senderName", null);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        image = i.getStringExtra("image");
        receiverID = i.getStringExtra("ReceiverID");

        firebaseDatabase = FirebaseDatabase.getInstance();

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //Emoji keyboard
        emoImageView = findViewById(R.id.emo_image);
        emojIcon = new EmojIconActions(IndividualChatPage.this, rootView, message, emoImageView, "#F44336", "#e8e8e8", "#f4f4f4");
        emojIcon.ShowEmojIcon();

        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView profile_dialog;
                Dialog dialog = new Dialog(IndividualChatPage.this);
                dialog.setContentView(R.layout.main_profile_dialog);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                if (image != null)
                    Glide.with(IndividualChatPage.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                if (window != null)
                    window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            }
        });
        databaseReferenceToAddChat = firebaseDatabase.getReference("data").child("groupID");
        databaseReferenceSender = firebaseDatabase.getReference("data").child("users").child(name);

        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
        textView.setText(name);

        //check if editText is empty or not ,if empty keep send button as non clickable
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 0) {
                    send.setVisibility(View.INVISIBLE);
                } else {
                    send.setVisibility(View.VISIBLE);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking whether text send to firebase is not empty
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    messagePojo = new MessagePojo();
                    messagePojo.setSenderID(senderID);
                    messagePojo.setReceiverID(receiverID);
                    messagePojo.setTimeStamp(String.valueOf(System.currentTimeMillis()));
                    messagePojo.setMessageId(databaseReferenceToAddChat.push().getKey());
                    messagePojo.setMessage(message.getText().toString().trim());
                    messagePojo.setReceiverName(name);
                    messagePojo.setSenderName(senderName);
                    databaseReferenceToAddChat.child(messagePojo.getMessageId()).setValue(messagePojo);
                    message.setText(null);
                    send.setVisibility(View.INVISIBLE);
                }
            }
        });

        databaseReferenceToAddChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagePojoArrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    messagePojo = data.getValue(MessagePojo.class);
                    messageKey = data.getKey();
                    messagePojoArrayList.add(messagePojo);
                }

                chatAdapter = new ChatAdapter(messagePojoArrayList, senderID, receiverID, IndividualChatPage.this);
                recyclerView.setAdapter(chatAdapter);

                //to set the position of recyclerview to last
                if (messagePojoArrayList != null && !messagePojoArrayList.isEmpty()) {
                    recyclerView.scrollToPosition(messagePojoArrayList.size() - 1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPojo = dataSnapshot.getValue(MyPojo.class);

                ThreadManager.getInstance().doWork(new ThreadManager.CustomRunnable() {
                    @Override
                    void onBackground() {

                    }

                    @Override
                    void onUi() {
                        super.onUi();
                        if (myPojo != null) {
                            if (myPojo.getOnline().equalsIgnoreCase("yes")) {
                                bindDateTime(textViewOnline, null, null, "online", null);
                                Log.e("isOnline", "isOnline value : " + myPojo.getOnline());
                            } else {
                                bindDateTime(textViewOnline, new Date(Long.parseLong(myPojo.getTimeStamp())), getString(R.string.time_format), "online", myPojo.getTimeStamp());
                                Log.e("isOnline", "isOnline value : " + myPojo.getOnline());
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void bindDateTime(TextView textView, Date date, String format, String emptyTxt, String timeStamp) {
        try {
            if (date != null) {
                Calendar statusTime = Calendar.getInstance();
                statusTime.setTimeInMillis(Long.parseLong(timeStamp));

                Calendar now = Calendar.getInstance();

                if (now.get(Calendar.DATE) == statusTime.get(Calendar.DATE)) {
                    String currentDay = "last seen today at " + DateFormat.format(format, statusTime);
                    textView.setText(currentDay);
                } else if (now.get(Calendar.DATE) - statusTime.get(Calendar.DATE) == 1) {
                    String yesterday = "last seen yesterday at " + DateFormat.format(format, statusTime);
                    textView.setText(yesterday);
                } else if (now.get(Calendar.YEAR) == statusTime.get(Calendar.YEAR)) {
                    String pastYesterday = "last seen at " + DateFormat.format(getString(R.string.custom_date), statusTime);
                    textView.setText(pastYesterday);
                } else {
                    textView.setText(DateFormat.format("MMMM dd yyyy, h:mm aa", statusTime).toString());
                }

            } else {
                textView.setText(emptyTxt);
            }

        } catch (Exception e) {
            textView.setText(emptyTxt);
        }
    }
}
