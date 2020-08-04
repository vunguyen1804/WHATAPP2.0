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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.edm_messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 *  Class: AccountRecoveryActivity.java
 *  Description: Handle all account recovery activity by sending reset password email through
 *               Firebase
 */
public class AccountRecoveryActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button submitButton;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_recover_activity);

        init();

        addOnClick();

    }

    /**
     * connect xml components Java code
     */
    private void init() {

        firebaseAuth = FirebaseAuth.getInstance();

        submitButton = (Button) findViewById(R.id.submit_button);

        emailEditText = (EditText) findViewById(R.id.email_edit_text);

    }

    /**
     * Add onClick listener to all XML components.
     */
    private void addOnClick() {

        submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                validate();

            }
        });

    }

    /**
     * validate email when user clicks submit button
     */
    private void validate() {

        String email = emailEditText.getText().toString();

        if(email.isEmpty()){

            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();

        }else if(!Utility.checkEmail(email)){

            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();

        }else{

            sendPasswordRecovery(email.trim());

        }

    }

    /**
     *
     * @param email : a valid email sent from user
     *              send an reset instruction email to user.
     */
    private void sendPasswordRecovery(String email) {

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AccountRecoveryActivity.this, "A reset instruction has been sent to your email.", Toast.LENGTH_SHORT).show();

                        }else{

                            Toast.makeText(AccountRecoveryActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}