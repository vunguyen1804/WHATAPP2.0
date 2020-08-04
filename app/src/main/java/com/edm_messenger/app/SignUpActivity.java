/*
 * Name: Zuhao Zhang
 *       Adrian Wong
 *       Yuxiang Liu
 *       Zhe Xu
 *       Vu Nguyen
 *       Boram Wong
 *       Srinivas Venkatraman
 *       Apurva Goenka
 *       Steven Kan
 *       Edgar Matias
 *
 * Project: EDM-Messenger
 * Date: 9/23/2019
 * Description: UCSD Fall 2019 Gary's CSE 110 Team EDM's messenger project
 */
package com.edm_messenger.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.edm_messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Class: SignUpActivity.java
 * Description: Contains all the functionality of the sign up page - handles all functions such as creating new user
 *              sign up button clicked etc
 *
 */
public class SignUpActivity extends AppCompatActivity {

    // All buttons, input fields etc displayed on the page
    private Button signUpButton;
    private EditText username_edit_text;
    private EditText password_edit_text;
    private EditText email_edit_text;
    private TextView warningTextView;
    private ProgressDialog diaglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_activity);

        init();

        addOnClick();

    }

    /**
     * connect all XML component to Java code
     */
    private void init(){

        signUpButton = (Button) findViewById(R.id.signup_button);

        username_edit_text = (EditText) findViewById(R.id.sign_up_username_edit_text);

        password_edit_text = (EditText) findViewById(R.id.sign_up_password_edit_text);

        email_edit_text = (EditText) findViewById(R.id.sign_up_email_edit_text);

        warningTextView = (TextView) findViewById(R.id.signup_warning_text_view);

        diaglog = new ProgressDialog(this);

    }

    /**
     * add onClickListener to XML components - attaches code to execute when clicked
     */
    private void addOnClick(){

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            // validate info, create new account when user clicks sign up
            public void onClick(View v) {
                validate();
            }

        });
    }

    /**
     * Transitioning to login activity after a user successfully created a new account
     */
    private void toLoginActivity(){

        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);

        startActivity(loginIntent);

    }

    /**
     * validate information that user enters and display error messages. If valid - create new account
     */

    private void validate(){

        String username =  username_edit_text.getText().toString();

        String password = password_edit_text.getText().toString();

        String email = email_edit_text.getText().toString();

        if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {

            warningTextView.setText("one or more fields are empty!");

            return;

        }

        diaglog.setTitle("Creating new account!");

        diaglog.setMessage("Please wait...");

        diaglog.show();

        if(!Utility.checkUserName(username)){

            diaglog.dismiss();

            Toast.makeText(this,"Name is invalid - should be greater than 2 characters." , Toast.LENGTH_SHORT).show();

        } else if (!Utility.checkPassword(password)){

            diaglog.dismiss();

            Toast.makeText(this,"Password is invalid - should be greater than 5 characters." , Toast.LENGTH_SHORT).show();

        } else if ( !Utility.checkEmail(email)){

            diaglog.dismiss();

            Toast.makeText(this,"Email is invalid - make sure it is in correct format!" , Toast.LENGTH_SHORT).show();

        } else{

            // All good - create the new account.
            Database.getAuth()
                    .createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            diaglog.dismiss();

                            if (task.isSuccessful()) {

                                createNewUser(username);

                                Database.getAuth().signOut();

                                diaglog.setMessage("Register successfully!");

                                toLoginActivity();

                            } else {

                                Toast.makeText(SignUpActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
        }

    }

    /**
     *
     * @param name
     * Save new user info to database
     */
    private void createNewUser(String name) {

        String id = Database.getAuth().getCurrentUser().getUid();

        User user = new User(name);

        Database.getUsersRef().child(id).setValue(user);

    }

}
