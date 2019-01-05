package com.example.shakil.lasplatica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button btnUpdate;

    private EditText eTxtUsername;
    private EditText eTxtUserStatus;

    private CircleImageView iProfilePic;

    private String currentuserID;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        eTxtUsername.setVisibility(View.INVISIBLE);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        retrieveUserInfo();

    }

    private void retrieveUserInfo() {
        mDatabaseReference.child("Users").child(currentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.hasChild("username")&& dataSnapshot.hasChild("image")){
                            String retrievedUsername = dataSnapshot.child("username").getValue().toString();
                            String retrievedUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievedPorfilePic = dataSnapshot.child("image").getValue().toString();

                            eTxtUsername.setText(retrievedUsername);
                            eTxtUserStatus.setText(retrievedUserStatus);

                        }
                        else if(dataSnapshot.exists()&&dataSnapshot.hasChild("username")){
                            String retrievedUsername = dataSnapshot.child("username").getValue().toString();
                            String retrievedUserStatus = dataSnapshot.child("status").getValue().toString();

                            eTxtUsername.setText(retrievedUsername);
                            eTxtUserStatus.setText(retrievedUserStatus);
                        }
                        else {
                            eTxtUsername.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this,"set profile information.",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initializeFields() {

        btnUpdate = findViewById(R.id.btn_update_settings);

        eTxtUsername = findViewById(R.id.etxt_set_username);
        eTxtUserStatus = findViewById(R.id.etxt_set_status);

        iProfilePic = findViewById(R.id.set_profile_image);

        mProgressDialog = new ProgressDialog(this);
    }

    private void updateSettings() {
        String setUsername = eTxtUsername.getText().toString();
        String setUserStatus = eTxtUserStatus.getText().toString();

        if(TextUtils.isEmpty(setUsername)){
            Toast.makeText(this,"Please write your username!",Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String,String>profileMap = new HashMap<>();
            profileMap.put("userID", currentuserID);
            profileMap.put("username", setUsername);
            profileMap.put("status", setUserStatus);

            mProgressDialog.setTitle("Profile Update");
            mProgressDialog.setMessage("Profile update on process");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

            mDatabaseReference.child("Users").child(currentuserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mProgressDialog.dismiss();
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                            }
                            else {
                                String errorMsg = task.getException().toString();
                                mProgressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this,"Error: "+errorMsg,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
