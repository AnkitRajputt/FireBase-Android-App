package com.example.myfirebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG= "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().setTitle("Login Page");

        //Show hide password using eye icon
        ImageView imageViewShowHidePwd= findViewById(R.id.imageview_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance()))
                {
                    // if pwd is visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        editTextLoginEmail= findViewById(R.id.edittext_login_email);
        editTextLoginPwd= findViewById(R.id.edittext_login_pwd);
        progressBar= findViewById(R.id.progressBar_login);

        authProfile= FirebaseAuth.getInstance();

        //Login User Button
        Button buttonLogin= findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String txtEmail= editTextLoginEmail.getText().toString();
                    String txtPwd= editTextLoginPwd.getText().toString();

                    if(TextUtils.isEmpty(txtEmail))
                    {
                        Toast.makeText(LoginActivity.this, "Enter your email id", Toast.LENGTH_LONG).show();
                        editTextLoginEmail.setError("Email is required");
                        editTextLoginEmail.requestFocus();

                    } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                        Toast.makeText(LoginActivity.this, "Re-enter your email id", Toast.LENGTH_LONG).show();
                        editTextLoginEmail.setError("Valid email is required");
                        editTextLoginEmail.requestFocus();

                    } else if (TextUtils.isEmpty(txtPwd)) {
                        Toast.makeText(LoginActivity.this, "Enter your password", Toast.LENGTH_LONG).show();
                        editTextLoginPwd.setError("Password is required");
                        editTextLoginPwd.requestFocus();

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        loginUser(txtEmail, txtPwd);
                    }
            }
        });

    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    //Get the current user
                    FirebaseUser firebaseUser= authProfile.getCurrentUser();

                    //Check the email is verified or not before access the user profile
                    if(firebaseUser.isEmailVerified())
                    {
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_LONG).show();
                        //Open User Profile
                        //Start the UserProfileActivity
                        Intent intent= new Intent(LoginActivity.this, UserProfileActivity.class);
                        startActivity(intent);

                        //startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                        //finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //Sign out user
                        //showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exist or a valid, please register again");
                        editTextLoginEmail.requestFocus();

                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginPwd.setError("Invalid user credential. Kindly check and re-enter");
                        editTextLoginPwd.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //Setup the alert builder
        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        //Open email apps when user clicks/taps to continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent= new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To open email app in new window not in out app
                startActivity(intent);
            }
        });

        //Create the alertdialog box
        AlertDialog alertDialog= builder.create();
        //Show the alertdialog box
        alertDialog.show();
    }

    //Check if user is already logged in. In such case, straightaway take the user to user profile's
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!= null){
            Toast.makeText(LoginActivity.this,"Already logged in!", Toast.LENGTH_LONG).show();

            //StartActivity to the userprofile
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        } else {
            Toast.makeText(LoginActivity.this,"Please logged In!", Toast.LENGTH_LONG).show();
        }

    }
}
