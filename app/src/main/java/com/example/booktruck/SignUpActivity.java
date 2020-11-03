package com.example.booktruck;
/*
    SignUpActivity is the main activity for Sign Up page
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signUpEmailText;
    private EditText signInEmailText;
    private EditText signUpPasswordText;
    private EditText signInPasswordText;
    private Button signUpVisible;
    private Button signInVisible;
    FirebaseFirestore db;

    public void signUp(View view){
        final String email = signUpEmailText.getText().toString();
        final String password = signUpPasswordText.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // then save user information into User Database
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("email", email);
                            data.put("password", password);
                            db = FirebaseFirestore.getInstance();
                            final CollectionReference collectionReference = db.collection("Users");
                            collectionReference
                                    .document(user.getUid())    // using user id as the document id
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        // These are a method which gets executed when the task is succeeded
                                            Log.d("SaveUser", "Data has been added successfully!");
                                            NavUtils.navigateUpFromSameTask(SignUpActivity.this);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // These are a method which gets executed if there’s any problem
                                            Log.d("SaveUser", "Data could not be added!" + e.toString());
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void signIn(View view){
        final String email = signInEmailText.getText().toString();
        final String password = signInPasswordText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGNIN", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // check the current user, if not null, then navigate back to main page
                                NavUtils.navigateUpFromSameTask(SignUpActivity.this);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGNIN", "signInWithEmail:failure", task.getException());
                            // If password and email address don't match, alert user by AlertDialog
                            new AlertDialog.Builder(SignUpActivity.this)
                                    //.setIcon(android.R.drawable.notify)
                                    .setTitle("Email and password not match")
                                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            signInPasswordText.setText("");
                                        }
                                    }).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_view);
        signUpEmailText = findViewById(R.id.signup_email);
        signUpPasswordText = findViewById(R.id.signup_password);
        signInEmailText = findViewById(R.id.signin_email);
        signInPasswordText = findViewById(R.id.signin_password);
        mAuth = FirebaseAuth.getInstance();
        signUpVisible = findViewById(R.id.first_visible);
        signInVisible = findViewById(R.id.second_visible);

        //set both password invisible first
        signUpPasswordText.setInputType(129);
        signInPasswordText.setInputType(129);

        // give user option to either hide or show password
        signUpVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signUpPasswordText.getInputType() == 128){
                    signUpPasswordText.setInputType(129);
                    signUpVisible.setText("password hiden");
                }else {
                    signUpPasswordText.setInputType(128);
                    signUpVisible.setText("password shown");
                }
            }
        });
        signInVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signInPasswordText.getInputType() == 128){
                    signInPasswordText.setInputType(129);
                    signInVisible.setText("password hiden");
                }else {
                    signInPasswordText.setInputType(128);
                    signInVisible.setText("password shown");
                }
            }
        });

    }

}
