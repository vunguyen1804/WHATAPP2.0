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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edm_messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Class: LoginActivity.java
 * Description: Handles Login functionality for the user - provides functionality for reading user email
 *              and password and logs in with that
 */
public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView createNewAccountButton, forgetPasswordButton;
    private EditText emailEditText, passwordEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        init();

    }

    /**
     *  Initialization listener
     */
    @Override
    protected void onStart() {

        super.onStart();

        addOnClick();

    }

    /**
     * Connect XML components to Java code
     */
    private void init(){

        loginButton = (Button) findViewById(R.id.login_button);

        createNewAccountButton = (TextView) findViewById(R.id.create_new_account_label);

        forgetPasswordButton = (TextView) findViewById(R.id.forget_password_label);

        emailEditText = (EditText) findViewById(R.id.login_email_edit_text);

        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);

    }

    /**
     * Add onClickListener to some XML components - along with associated on methods
     */
    private void addOnClick(){

        createNewAccountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                toSignUpActivity();
            }

        });

        forgetPasswordButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                toAccountRecoveryActivity();
            }

        });


        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                validate();
            }

        });

    }

    /**
     * transiting to sign up activity page
     */
    private void toSignUpActivity(){

        Intent sign_up_intent = new Intent(LoginActivity.this, SignUpActivity.class);

        startActivity(sign_up_intent);

    }

    /**
     * transitioning to main activity page
     */
    private void toMainActivity(){

        Intent main_intent = new Intent(LoginActivity.this, MainActivity.class);

        startActivity(main_intent);

        Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show();

    }

    /**
     * transitioning to account recovery activity page
     */
    private void toAccountRecoveryActivity(){

        Intent password_recovery_intent = new Intent(LoginActivity.this, AccountRecoveryActivity.class);

        startActivity(password_recovery_intent);

    }

    /**
     * validate email and password that a user enters (just check if they are empty or not)
     */
    private void validate(){

        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        dialog.setMessage("Authenticating...");

        dialog.show();

        // Get email and password user enters
        String password = passwordEditText.getText().toString();

        String email = emailEditText.getText().toString();

        if(email.isEmpty() && password.isEmpty()){

            Toast.makeText(LoginActivity.this, "Email or password is invalid, please try again!", Toast.LENGTH_LONG).show();

            dialog.dismiss();

        }else{

            // Log in with credentials
            Database.getAuth()
                    .signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            dialog.dismiss();

                            if (task.isSuccessful()) {

                                toMainActivity();

                            } else {

                                Toast.makeText(LoginActivity.this, "Email or password is invalid, please try again!", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }

    }

}




