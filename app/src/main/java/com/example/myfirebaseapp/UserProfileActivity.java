package com.example.myfirebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDob, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar= findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Firebase App");

        textViewWelcome= findViewById(R.id.textView_show_welcome);
        textViewFullName= findViewById(R.id.textView_show_full_name);
        textViewEmail= findViewById(R.id.textView_show_email);
        textViewDob= findViewById(R.id.textView_show_dob);
        textViewGender= findViewById(R.id.textView_show_gender);
        textViewMobile= findViewById(R.id.textView_show_mobile);

        progressBar= findViewById(R.id.progressBar);

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser == null)
        {
            Toast.makeText(UserProfileActivity.this,"Something went wrong!, User details are not available at this moment",
                    Toast.LENGTH_LONG).show();
        } else {
            //checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    //User coming from UserProfileActivity after the successful registration
    /*private void checkIfEmailVerified(FirebaseUser firebaseUser) {
            if (!firebaseUser.isEmailVerified()) {
                //showAlertDialog();
            }
    }*/

    /*private void showAlertDialog() {
        //Setup the alert builder
        AlertDialog.Builder builder= new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time.");

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
    }*/

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID= firebaseUser.getUid();

        //Extracting user reference from database for "Registered Users"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullName= firebaseUser.getDisplayName();
                    email=    firebaseUser.getEmail();
                    doB= readUserDetails.dob;
                    gender= readUserDetails.gender;
                    mobile= readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, "+ fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDob.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"Something went wrong !", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu items is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();

        //Refresh Activity
        if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } /*else if (id == R.id.menu_update_profile) {
            Intent intent= new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_update_email) {
            Intent intent= new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);

        }*/ else if (id == R.id.menu_settings) {
            Toast.makeText(UserProfileActivity.this,"Menu_Setting", Toast.LENGTH_LONG).show();
            
        } /*else if (id == R.id.menu_change_password) {
            Intent intent= new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_delete_profile){
            Intent intent= new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);

        }*/ else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged Out !", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(UserProfileActivity.this, MainActivity.class);

            //Clear stack to prevent user coming back to UserProfileActivity on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  //Close UserProfileActivity
        } else {
            Toast.makeText(UserProfileActivity.this, "Some went wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}