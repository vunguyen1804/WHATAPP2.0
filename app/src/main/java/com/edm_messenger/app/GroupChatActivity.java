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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *  Class: GroupChatActivity.java
 *  Description: Handle the chatting room between different user
 *               Handle the receiving massage from the all other users in the group.
 */

public class GroupChatActivity extends AppCompatActivity {
    private final static int PDF_REQUEST_CODE = 100;
    private final static int LOCATION_REQUEST_CODE = 1000;
    private final static int X_RATIO = 3;
    private final static int Y_RATIO = 2;
    private final static String PDF_QUERY = "application/pdf";
    private final static String EMPTY_STRING = "";
    private final static String LOCATION_PREFIX = "geo:";

    private DatabaseReference groupChatsRef;
    private Button sendButton;
    private EditText chatEditText;
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> chats;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_chat_activity);

        init();

        addOnClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * connect all XML components to Java code and access data sent from Group Fragment
     */
    private void init(){

        String groupName = getIntent().getStringExtra("GROUP_NAME");

        groupChatsRef = Database.getGroupChatRef().child(groupName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_chat_tool_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(groupName);

        sendButton = (Button)findViewById(R.id.send_group_message_button);

        chatEditText = (EditText) findViewById(R.id.group_edit_text);

        chats = new ArrayList<>();

        chatAdapter = new ChatAdapter(chats, this);

        recyclerView = (RecyclerView) findViewById(R.id.group_recycle_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(chatAdapter);

    }

    /**
     * add onClickListener to some XML components
     */
    private void addOnClick() {

        sendButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendTextMsg();
            }

        });

        chatEditText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                adjustView();
            }

        });

        //Database ListenerG
        groupChatsRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    updateView(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    /**
     *
     * @param dataSnapshot
     * update view when a message is sent by user
     */
    private void updateView ( DataSnapshot dataSnapshot){

        String time = (String) dataSnapshot.getKey();

        String content = (String) dataSnapshot.child("content").getValue();

        String sender = (String) dataSnapshot.child("sender").getValue();

        String type = (String) dataSnapshot.child("type").getValue();

        chats.add(new Chat(sender, content, type, time));

        adjustView();

    }

    /**
     * adjust view when a message a sent
     */
    private void adjustView() {

        chatAdapter.notifyDataSetChanged();

        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

    }

    /**
     *
     * @param menu
     * @return
     * create menu items for group activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.group_chat_menu, menu);

        return true;

    }

    /**
     *
     * @param item
     * @return
     * add functionality when user click at a particular menu item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.group_image_icon){

            toSelectImageActivity();

        }else if (item.getItemId() == R.id.group_pdf_icon){

            toSelectPdfActivity();

        }else if (item.getItemId() == R.id.group_location_icon){

            toShareLocationActivity();

        }

        return true;

    }

    /**
     * Allow user to select image from the phone or take a picture, and then crop it to send to other user.
     */
    private void toSelectImageActivity() {

        CropImage.activity().setAspectRatio(X_RATIO,Y_RATIO).start(this);

    }

    /**
     * Allow user to select pdf file from their phone
     */
    private void toSelectPdfActivity() {

        Intent pickPdfIntent = new Intent(Intent.ACTION_GET_CONTENT);

        pickPdfIntent.setType(PDF_QUERY);

        startActivityForResult(pickPdfIntent, PDF_REQUEST_CODE);

    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * send file (pdg/image) that current user selected
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri pickedUri;

        if(data == null || resultCode != RESULT_OK)
            return;

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            pickedUri = result.getUri();

            sendFile(Database.getImagesRef(), pickedUri, Chat.IMAGE_TYPE, Chat.IMAGE_EXTENSION);

        }else if (requestCode == PDF_REQUEST_CODE){

            pickedUri = data.getData();

            sendFile(Database.getPdfsRef(), pickedUri, Chat.PDF_TYPE, Chat.PDF_EXTENSION);

        }

    }

    /**
     * Ask for confirmation to send current location
     */
    private void toShareLocationActivity(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(DialogInterface.BUTTON_POSITIVE == which){
                    shareLocation();
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to share your location?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }

    /**
     * Ask for confirmation to user GPS
     */
    private void shareLocation (){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE
            );

        }else{

            getLocation();

        }

    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * if user select Ok and then get current location and send it out.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_REQUEST_CODE){

            getLocation();

        }else{

            Toast.makeText(this, "Location service is disable!", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * get current location
     */
    private void getLocation() {

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if(location != null) {

                    String longitude = String.valueOf(location.getLongitude());

                    String latitude = String.valueOf(location.getLatitude());

                    String content = LOCATION_PREFIX + latitude + "," + longitude;

                    sendMsg( content, Chat.LOCATION_TYPE);

                }
            }
        });
    }

    /**
     *
     * @param ref: firebase location where file will be stored
     * @param uri: local directory of the selected file
     * @param type: "pdf"/"image"
     * @param extension ".pdf"/".jpeg"
     * send files to database
     */
    private void sendFile(StorageReference ref, Uri uri, String type, String extension){

        final ProgressDialog dialog = new ProgressDialog(GroupChatActivity.this);

        dialog.setTitle("Uploading...");

        dialog.show();

        String chatId = groupChatsRef.getKey();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        StorageReference dir = ref.child(chatId).child(timeStamp + extension);

        dir.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                dir.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {

                        String content = uri.toString();

                        sendMsg(content, type);

                        Toast.makeText(GroupChatActivity.this, "Uploading successfully!",Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }

                });
            }
        });
    }

    /**
     * send text message to database
     */
    private void sendTextMsg(){

        if(chatEditText.getText().length() == 0) {
            return;
        }

        String content = chatEditText.getText().toString();

        sendMsg(content, Chat.TEXT_TYPE);

        chatEditText.setText(EMPTY_STRING);

    }

    /**
     *
     * @param content: could be text or URL of the file
     * @param type: could be "text", "location", "image", or "pdf"
     * send msg to other user
     */
    private void sendMsg(String content, String type){

        String sender = CurrentUser.getId();

        String timeStamp = Utility.getTimeStamp();

        HashMap<String, Object> newMsg = new HashMap<>();

        newMsg.put(Chat.CONTENT_KEY, content);

        newMsg.put(Chat.TYPE_KEY, type);

        newMsg.put(Chat.SENDER_KEY, sender);

        groupChatsRef.child(timeStamp).setValue(newMsg);

    }

}
