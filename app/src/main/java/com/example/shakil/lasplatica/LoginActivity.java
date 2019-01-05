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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnPhoneLogin;

    private EditText eTxtUserEmail;
    private EditText eTxtUserPassword;

    private TextView txtSignup;
    private TextView txtForgetPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        initializeFields();

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        String email = eTxtUserEmail.getText().toString().trim();
        String password = eTxtUserPassword.getText().toString();



        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter both email and password",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else {
            progressDialog.setTitle("User Sign In");
            progressDialog.setMessage("Please wait. Signing in process");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                             if(task.isSuccessful()){
                                 sendUserToMainActivity();
                                 progressDialog.dismiss();
                             }
                             else{
                                 String errorMsg = task.getException().toString();
                                 Toast.makeText(LoginActivity.this,errorMsg+" Try again or click forgot password to reset it.",Toast.LENGTH_LONG).show();
                                 progressDialog.dismiss();

                             }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void initializeFields() {

        btnLogin = findViewById(R.id.btn_login);
        btnPhoneLogin = findViewById(R.id.btn_mobile_login);

        eTxtUserEmail = findViewById(R.id.etxt_login_email);
        eTxtUserPassword = findViewById(R.id.etxt_login_password);

        txtSignup = findViewById(R.id.txt_signup_link);
        txtForgetPassword = findViewById(R.id.txt_forgot_password);

        progressDialog = new ProgressDialog(this);
    }

}
