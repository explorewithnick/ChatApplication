package com.example.niket.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.niket.chatapplication.Adapter.MyCustomAdapter;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatPage extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyCustomAdapter customAdapter;
    ArrayList<MyPojo> arrayList = new ArrayList<>();

    String senderId;
    SharedPreferences sharedPreferences;
    MyPojo userPojo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, reference2;

    String message;
    ArrayList<String> arrayListForDelete = new ArrayList<>();


    String senderName;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);


        recyclerView = findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        sharedPreferences = getSharedPreferences("save", 0);

        senderId = getIntent().getStringExtra("senderID");
        senderName = getIntent().getStringExtra("senderName");

        firebaseDatabase = FirebaseDatabase.getInstance();

        reference = firebaseDatabase.getReference("data").child("users");

        if (senderName != null) {
            reference2 = firebaseDatabase.getReference("data").child("users").child(senderName);
        }
        getFirebaseData(reference);
    }


    private void getFirebaseData(final DatabaseReference reference) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("123", "datasnap: " + dataSnapshot);
                arrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Log.d("123", "data2: ");
                    message = data.getKey();
                    arrayListForDelete.add(message);

                    userPojo = data.getValue(MyPojo.class);
                    arrayList.add(userPojo);
                }


                customAdapter = new MyCustomAdapter(ChatPage.this, arrayList, senderId, arrayListForDelete);
                recyclerView.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatPage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_menu) {

            editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(ChatPage.this, Login.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference2.child("online").setValue("yes");
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference2.child("online").setValue("no");
        reference2.child("timeStamp").setValue(String.valueOf(System.currentTimeMillis()));
    }
}
