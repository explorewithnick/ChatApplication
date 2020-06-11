package com.example.niket.chatapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.SelectImageHelper;
import com.example.niket.chatapplication.databinding.ActivityRegisterBinding;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Random;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding activityRegisterBinding;
    ProgressDialog dialog;
    SelectImageHelper helper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;

    Random random;

    MyPojo myPojo;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        actionBar = getSupportActionBar();
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        if (actionBar != null) {
            actionBar.setTitle("Chat Application");
        }
        //get FirebaseDatabase Instance
        firebaseDatabase = FirebaseDatabase.getInstance();

        //get selectHelper class instance
        helper = new SelectImageHelper(this, activityRegisterBinding.circular);

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait.....");
        dialog.setMessage("registering data to Database");
        dialog.setCanceledOnTouchOutside(false);

        random = new Random();
        final String s1 = String.valueOf(random.nextInt(263443));
        activityRegisterBinding.circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.selectImageOption();
            }
        });


        //making edittext cli
        activityRegisterBinding.editTextDOB.setFocusable(false);
        activityRegisterBinding.editTextDOB.setClickable(true);
        activityRegisterBinding.editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    datePickerDialog = new DatePickerDialog(Register.this, android.R.style.Theme_DeviceDefault_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String dob = day + "/" + (month + 1) + "/" + year;
                            activityRegisterBinding.editTextDOB.setText(dob);
                        }
                    }, year, month, day);
                } else {
                    datePickerDialog = new DatePickerDialog(Register.this, android.R.style.Theme_Material_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String dob = day + "/" + (month + 1) + "/" + year;
                            activityRegisterBinding.editTextDOB.setText(dob);
                        }
                    }, year, month, day);
                }
                datePickerDialog.show();
            }

        });

        activityRegisterBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //register new user
        activityRegisterBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Uri file = helper.getURI_FOR_SELECTED_IMAGE();

                    if (activityRegisterBinding.editTextName.getText().toString().trim().length() == 0) {
                        activityRegisterBinding.editTextName.setError("This field is required");
                    } else if (activityRegisterBinding.editTextEmail.getText().toString().trim().length() == 0) {
                        activityRegisterBinding.editTextEmail.setError("This field is required");
                    } else if (activityRegisterBinding.editTextMobile.getText().toString().trim().length() == 0) {
                        activityRegisterBinding.editTextMobile.setError("This field is required");
                    } else if (activityRegisterBinding.editTextMobile.getText().toString().trim().length() != 10 &&
                            activityRegisterBinding.editTextMobile.getText().toString().trim().length() > 0
                    ) {
                        activityRegisterBinding.editTextMobile.setError("Invalid Mobile Number");
                    } else if (activityRegisterBinding.editTextPassword.getText() != null && activityRegisterBinding.editTextPassword.getText().toString().trim().length() == 0) {
                        activityRegisterBinding.editTextPassword.setError("This field is required");
                    } else if (activityRegisterBinding.cpass.getText() != null && activityRegisterBinding.cpass.getText().toString().trim().length() == 0) {
                        activityRegisterBinding.cpass.setError("This field is required");
                    } else if (file == null) {
                        Toast.makeText(Register.this, "set profile image", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.show();

                        //get the DatabaseReference
                        databaseReference = firebaseDatabase.getReference("data").child("users");


                        StorageReference storageReference2 = storageReference.child(s1);

                        storageReference2.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();

                                                myPojo = new MyPojo();

                                                myPojo.setID(databaseReference.push().getKey());


                                                myPojo.setImage_URL(downloadUrl + "");
                                                myPojo.setOnline("no");
                                                myPojo.setTimeStamp(String.valueOf(System.currentTimeMillis()));
                                                myPojo.setRegisterTimeStamp(String.valueOf(System.currentTimeMillis()));
                                                myPojo.setName(activityRegisterBinding.editTextName.getText().toString());
                                                myPojo.setDOB(activityRegisterBinding.editTextDOB.getText().toString());
                                                myPojo.setMobile(activityRegisterBinding.editTextMobile.getText().toString());
                                                myPojo.setEmail(activityRegisterBinding.editTextEmail.getText().toString());
                                                myPojo.setPassword(activityRegisterBinding.editTextPassword.getText().toString());
                                                myPojo.setName(activityRegisterBinding.editTextName.getText().toString());


                                                databaseReference.child(activityRegisterBinding.editTextName.getText().toString()).setValue(myPojo);

                                                dialog.cancel();

                                                Intent intent = new Intent(Register.this, Login.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("Profile_image", myPojo.getImage_URL());
                                                //intent.putExtra("username",activityRegisterBinding.editTextName.getText().toString());
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Database Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Register.this, "set your profile image", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        helper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        helper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }

}
