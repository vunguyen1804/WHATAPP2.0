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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edm_messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
/**
 *  Class: ProfileSettings.java
 *  Description: View for the profile setting, and it will upload the information of the current user
 *               to the Database.
 */
public class ProfileSettings extends AppCompatActivity {
    private final static String IMAGE_EXTENSION = ".png";

    private Button save;
    private Button logoutAcc;
    private EditText statusEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private ImageView profileCirImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.profile_activity);

    }

    @Override
    protected void onStart() {

        super.onStart();

        setContentView(R.layout.profile_activity);

        init();

        addOnClick();

        getData();

    }

    /**
     * connect XML components to Java code
     */
    private void init() {

        save = findViewById(R.id.save_button);

        logoutAcc = findViewById(R.id.logout_button);

        statusEditText = findViewById(R.id.status_edit_text);

        userNameEditText =  findViewById(R.id.user_name_edit_text);

        passwordEditText = findViewById(R.id.password_edit_text);

        userNameTextView =  findViewById(R.id.user_name_text_view);

        userEmailTextView =  findViewById(R.id.user_email_text_view);

        profileCirImageView =  findViewById(R.id.user_circle_image_view);

    }

    /**
     * add onClickListener to some XML components
     */
    private void addOnClick() {

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                validateUserInfo();
            }

        });

        logoutAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logout();
            }

        });

        profileCirImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                changeProfilePic();
            }

        });
    }

    /**
     * log the time that the current user logs out and transition to log in activity
     *
     */
    private void logout() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(DialogInterface.BUTTON_POSITIVE == which){

                    Toast.makeText(ProfileSettings.this, "Logged out", Toast.LENGTH_SHORT).show();

                    CurrentUser.setStatusToOffline();

                    Database.getAuth().signOut();

                    Intent loginIntent = new Intent(ProfileSettings.this, LoginActivity.class);

                    startActivity(loginIntent);

                    finish();

                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to log out?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    /**
     * update profile and upload new profile to database
     */
    private void validateUserInfo() {

        String name = userNameEditText.getText().toString();

        String status = statusEditText.getText().toString();

        String newPassword = passwordEditText.getText().toString();

        if(validatePassword(newPassword)){
            updateProfileInfo(name,status);
            Toast.makeText(ProfileSettings.this,"Profile successfully updated", Toast.LENGTH_LONG).show();
            return;
        }

        EditText confirmPasswordEditText;

        AlertDialog.Builder confirmPasswordAlert;

        confirmPasswordEditText = new EditText(this);

        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPasswordAlert = new AlertDialog.Builder(this).setView(confirmPasswordEditText);

        confirmPasswordAlert.setTitle("Enter your current password to change your password");

        confirmPasswordAlert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FirebaseUser currentUser = Database.getAuth().getCurrentUser();

                String enteredPassword = confirmPasswordEditText.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), enteredPassword);

                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        currentUser.updatePassword(newPassword);

                        Toast.makeText(ProfileSettings.this, "Password successfully updated", Toast.LENGTH_LONG).show();

                        passwordEditText.setText("", TextView.BufferType.EDITABLE);

                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ProfileSettings.this,"Password update failed" , Toast.LENGTH_LONG).show();

                        passwordEditText.setText("", TextView.BufferType.EDITABLE);
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(ProfileSettings.this,"Password update cancelled" , Toast.LENGTH_LONG).show();

                passwordEditText.setText("", TextView.BufferType.EDITABLE);

            }
        }).show();

        updateProfileInfo(name, status);

    }

    /**
     *
     * @param name
     * @param status
     * upload new profileInfo to databse
     */
    private void updateProfileInfo(String name, String status) {
        DatabaseReference usersRef = Database.getUsersRef().child(CurrentUser.getId());

        HashMap<String, Object> updatedUser = new HashMap<>();

        updatedUser.put(User.NAME_KEY, name);

        updatedUser.put(User.STATUS_KEY, status);

        userNameTextView.setText(name);

        userNameEditText.setText(name);

        statusEditText.setText(status);

        updatedUser.put(User.PROFILE_DIR_KEY, CurrentUser.getProfilePicDir());

        usersRef.updateChildren(updatedUser);
    }


    private Boolean validatePassword(String newPassword) {

        if(!newPassword.isEmpty()) {

            if (!Utility.checkPassword(newPassword)) {

                Toast.makeText(this, "Invalid password - length should be greater than 5 characters", Toast.LENGTH_LONG).show();

                return true;

            } else{

                return false;

            }

        }

        return true;

    }

    /**
     * get Data of the current user from Firebase
     */
    private void getData(){

        userNameTextView.setText(CurrentUser.getName());

        userNameEditText.setText(CurrentUser.getName());

        statusEditText.setText(CurrentUser.getStatus());

        Picasso.get().load(CurrentUser.getProfilePicDir())
                .placeholder(R.drawable.profile)
                .into(profileCirImageView);

        userEmailTextView.setText(Database.getAuth().getCurrentUser().getEmail());

    }

    /**
     * Allow user to select an existing picture on their phone or take a new picture and then crop
     * it to user as profile image.
     */
    private void changeProfilePic() {

        CropImage.activity().setAspectRatio(1,1).start(this);

    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * get location of the selected image to upload to database
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                uploadImg(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(ProfileSettings.this,"Please try again!",Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     *
     * @param uri
     * upload image to database
     */
    private void uploadImg(Uri uri) {

        final ProgressDialog dialog = new ProgressDialog(ProfileSettings.this);

        dialog.setMessage("Updating profile image");

        dialog.show();

        StorageReference ref = Database.getProfileImagesRef().child(CurrentUser.getId() + IMAGE_EXTENSION);

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {

                        String link = uri.toString();

                        Picasso.get().load(link).into(profileCirImageView);

                        Toast.makeText(ProfileSettings.this, "Profile image successfully updated", Toast.LENGTH_SHORT).show();

                        DatabaseReference userRef = Database.getUsersRef().child(CurrentUser.getId()).child(User.PROFILE_DIR_KEY).getRef();

                        userRef.setValue(link);

                        dialog.dismiss();
                    }

                });

            }

        });
    }
}