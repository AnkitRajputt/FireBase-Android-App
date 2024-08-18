package com.example.myfirebaseapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDOB, editTextRegisterMobile,
                     editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG= "Register Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //Set the title
        //getSupportActionBar().setTitle("Registration");

        //Show toast
        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        //Get the id's
        editTextRegisterFullName= findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=    findViewById(R.id.editText_register_email);
        editTextRegisterDOB=      findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=   findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=      findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd= findViewById(R.id.editText_register_confirm_password);
        progressBar= findViewById(R.id.progressBar);

        //Get the id's of Radio Button
        radioGroupRegisterGender= findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting up Data Picker on edittext
        editTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month= calendar.get(Calendar.MONTH);
                int year= calendar.get(Calendar.YEAR);

                //Date picker dialog
                picker= new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                            editTextRegisterDOB.setText(dayOfMonth +"/" + (month + 1) + "/" + (year) ) ;

                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Get the id's Register Button
        Button buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId= radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected= findViewById(selectedGenderId);

                //obtain the entered data
                String textFullName= editTextRegisterFullName.getText().toString();
                String textEmail= editTextRegisterEmail.getText().toString();
                String textDoB= editTextRegisterDOB.getText().toString();
                String textMobile= editTextRegisterMobile.getText().toString();
                String textPwd= editTextRegisterPwd.getText().toString();
                String textConfirmPwd= editTextRegisterConfirmPwd.getText().toString();
                String textGender;

                //Validate mobile no using matcher and pattern (Regular expression)
                String mobileRegex= "[6-9][0-9]{9}"; //First no. can be 6,7,8,9 and other rest 9 nos. can be any no.s
                Matcher mobileMatcher;
                Pattern mobilePattern= Pattern.compile(mobileRegex);
                mobileMatcher= mobilePattern.matcher(textMobile);

                //Can't obtain the value before verifying if any button was selected or not
                if(TextUtils.isEmpty(textFullName))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();

                //Validation for a email-id
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this,"Please enter your Date Of Birth",Toast.LENGTH_LONG).show();
                    editTextRegisterDOB.setError("DOB is required");
                    editTextRegisterDOB.requestFocus();

                }else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();

                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Mobile no", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile No is required");
                    editTextRegisterMobile.requestFocus();

                } else if (textMobile.length()!= 10) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile No", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Mobile No. should be 10 digits");
                    editTextRegisterPwd.requestFocus();

                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile No", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Mobile no. is not valid");
                    editTextRegisterPwd.requestFocus();

                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();


                }else if (textPwd.length()< 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits only", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is weak");
                    editTextRegisterPwd.requestFocus();

                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Confirm Password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Confirm Password is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    
                } else if (!textConfirmPwd.equals(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Confirm password should be the same", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password Confirmation is required");
                    editTextRegisterPwd.requestFocus();

                    //Clear the both password
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    textGender= radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerMethod(textFullName,textEmail,textDoB, textMobile, textGender, textPwd, textConfirmPwd);
                }
            }
        });
    }

    //Register Users using the credential here
    //FireBase Authentication
    private void registerMethod(String textFullName, String textEmail, String textDoB, String textMobile, String textGender, String textPwd, String textConfirmPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Update display name of user
                            UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Enter the user data into the firebase realtime database
                            ReadWriteUserDetails readWriteUserDetails= new ReadWriteUserDetails(textDoB, textGender, textMobile);

                            //Extracting User references from the database for Registered Users
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        //Send Verification email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "User registered successfully, please verify your email",
                                                Toast.LENGTH_LONG).show();

                                        /*Open a user profile after successfully registration*/

                                        Intent intent= new Intent(RegisterActivity.this, UserProfileActivity.class);

                                        //To prevent user from returning back to register activity on pressing back button after registration
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();   //to close register activity

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "User registered failed, please verify your email",
                                                Toast.LENGTH_LONG).show();
                                    }

                                    //Hide progress whether user creation successfully or failed
                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                        }
                        else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e)
                                {
                                    editTextRegisterPwd.setError("Your password is too weak");
                                    editTextRegisterPwd.requestFocus();

                                } catch (FirebaseAuthInvalidCredentialsException e)
                                {
                                    editTextRegisterPwd.setError("Your email is invalid, please enter your right email");
                                    editTextRegisterPwd.requestFocus();

                                } catch (FirebaseAuthUserCollisionException e)
                                {
                                    editTextRegisterPwd.setError("This user email is already exists, please enter another email id");
                                    editTextRegisterPwd.requestFocus();

                                } catch (Exception e)
                                {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                //Hide progress whether user creation successfully or failed
                                progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
