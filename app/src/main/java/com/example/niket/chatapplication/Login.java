package com.example.niket.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.niket.chatapplication.databinding.ActivityLoginBinding;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReference2;
    SharedPreferences sharedPreferences;

    MyPojo myPojo;
    ProgressDialog dialog, dialog_integration;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (actionBar != null) {
            actionBar.setTitle("Chat Application");
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("data").child("users");
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait....");
        dialog.setMessage("verifying user");
        dialog.setCanceledOnTouchOutside(false);


        //integration dialog
        dialog_integration = new ProgressDialog(this);
        dialog_integration.setTitle("please wait....");
        dialog_integration.setMessage("verifying account");
        dialog_integration.setCanceledOnTouchOutside(false);


        sharedPreferences = getSharedPreferences("save", 0);
        final String name = sharedPreferences.getString("senderName", null);
        final String senderID = sharedPreferences.getString("senderID", null);
        databaseReference2 = firebaseDatabase.getReference("data").child("users");

        Log.e("Login", "name " + name);
        Log.e("Login", "status " + sharedPreferences.getBoolean("status", false));

        if (sharedPreferences.getBoolean("status", false)) {
            Log.e("Login", "inside status " + sharedPreferences.getBoolean("status", false));

            Intent intent = new Intent(Login.this, ChatPage.class);
            intent.putExtra("senderID", senderID);
            intent.putExtra("senderName", name);
            startActivity(intent);
            finish();
        }

        activityLoginBinding.Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        activityLoginBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (activityLoginBinding.Mobile.getText().toString().trim().length() == 0) {
                        activityLoginBinding.Mobile.setError("This field is required");
                    } else if (activityLoginBinding.editTextPassword.getText().toString().trim().length() == 0) {
                        activityLoginBinding.editTextPassword.setError("This field is required");
                    } else if (activityLoginBinding.Mobile.getText().toString().trim().length() != 10 &&
                            activityLoginBinding.Mobile.getText().toString().trim().length() > 0
                    ) {
                        activityLoginBinding.Mobile.setError("Invalid Mobile Number");
                    } else {
                        dialog.show();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                boolean userFound = false;

                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    myPojo = data.getValue(MyPojo.class);

                                    if (activityLoginBinding.Mobile.getText().toString().equals(myPojo.getMobile())
                                            && activityLoginBinding.editTextPassword.getText().toString().equals(myPojo.getPassword())) {

                                        dialog.cancel();

                                        userFound = true;

                                        break;
                                    }
                                }

                                if (userFound) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("status", true);
                                    editor.putString("senderID", myPojo.getID());
                                    editor.putString("senderName", myPojo.getName());
                                    Log.d("register", "register: " + myPojo.getName());
                                    editor.apply();

                                    Intent intent = new Intent(Login.this, ChatPage.class);
                                    intent.putExtra("senderID", myPojo.getID());
                                    intent.putExtra("senderName", myPojo.getName());
                                    startActivity(intent);
                                    finish();

                                } else {
                                    dialog.cancel();
                                    Toast.makeText(Login.this, "invalid Credentials", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(Login.this, "Database error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } catch (Exception e) {
                    dialog.cancel();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Login", "onResume");
    }
}
