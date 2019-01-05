package com.example.shakil.lasplatica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private EditText eTxtUserEmail;
    private EditText eTxtUserPassword;
    private EditText eTxtConfirmPassword;

    private Button btnRegister;

    private TextView txtLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    //Creating new account
    private void registerUser() {
        String userEmail =  eTxtUserEmail.getText().toString().trim();
        String userPassword = eTxtUserPassword.getText().toString();
        String confirmPassword = eTxtConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Please enter both email and password..",Toast.LENGTH_LONG).show();
        }
        else if(!userPassword.equals(confirmPassword)){
            Toast.makeText(this,"Password does not match..",Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Registering  new user");
            loadingBar.setMessage("Please Wait, user registration is on process");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                mDatabaseReference.child("Users").child(currentUserID).setValue("");
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }

                            else {
                                String errorMsg = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error"+errorMsg,Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    //
    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //Initializing fields
    private void initializeFields() {
        eTxtUserEmail = findViewById(R.id.etxt_reg_email);
        eTxtUserPassword = findViewById(R.id.etxt_reg_password);
        eTxtConfirmPassword = findViewById(R.id.etxt_reg_confirm_password);

        btnRegister = findViewById(R.id.btn_reg);
        txtLogin = findViewById(R.id.txt_login_link);

        loadingBar = new ProgressDialog(this);
    }
}
