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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *  Class: FriendChatActivity.java
 *  Description: Handles all chat activity between two users (friends).
 *               Handles sending and receiving messages.
 */
public class FriendChatActivity extends AppCompatActivity {
    private final static int PDF_REQUEST_CODE = 100;
    private final static int LOCATION_REQUEST_CODE = 1000;
    private final static String PDF_QUERY = "application/pdf";
    private final static int X_RATIO = 3, Y_RATIO = 2;
    private final static String EMPTY_STRING = "";
    private final static String LOCATION_PREFIX = "geo:";

    private ArrayList<Chat> chats;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private Button sendButton;
    private EditText chatEditText;
    private String friendId;
    private String chatId;
    private String friendName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_chat_activity);

        init();

        addOnClick();
    }

    /**
     * add listener to Firebase when other user wants to video/voice chat with current user.
     */
    @Override
    protected void onStart() {

        super.onStart();

        Database.getCallRequestRef().child(CurrentUser.getId()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                displayCallDialog(dataSnapshot);

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
     * Connect all XML components to Java code
     */
    private void init() {

        Intent intent = getIntent();

        friendId = intent.getStringExtra("FRIEND_ID");

        String friendProfileDir = intent.getStringExtra("PROFILE_DIR");

        friendName = intent.getStringExtra("NAME");

        CircleImageView friendImage = (CircleImageView) findViewById(R.id.friend_profile_image);

        Picasso.get().load(friendProfileDir).placeholder(R.drawable.profile).into(friendImage);

        Database.getChatIdRef()
                .child(CurrentUser.getId())
                .child(friendId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            chatId = dataSnapshot.getValue().toString();

                            displayChats();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        chatEditText = (EditText) findViewById(R.id.friend_input_edit_text);

        sendButton = (Button) findViewById(R.id.send_friend_message_button);

        chats = new ArrayList<>();

        chatAdapter = new ChatAdapter(chats, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.friend_chat_tool_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(EMPTY_STRING);

        recyclerView = (RecyclerView) findViewById(R.id.friend_message_recycle_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(chatAdapter);

    }

    /**
     * add onClickListener some XML components
     */
    private void addOnClick() {

        sendButton.setOnClickListener(new View.OnClickListener() {

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

    }

    /**
     * display all sent messages from current user and current user.
     */
    private void displayChats() {

        Database.getPrivateChatRef().child(chatId)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if (dataSnapshot.exists()) {
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
     * update view when either current user or other user send a message
     */
    private void updateView(DataSnapshot dataSnapshot) {

        String time = dataSnapshot.getKey();

        String content = (String) dataSnapshot.child(Chat.CONTENT_KEY).getValue();

        String sender = (String) dataSnapshot.child(Chat.SENDER_KEY).getValue();

        String type = (String) dataSnapshot.child(Chat.TYPE_KEY).getValue();

        chats.add(new Chat(sender, content, type, time));

        adjustView();

    }

    /**
     * adjust view (scroll down the chat list) when a message is sent from any user.
     */
    private void adjustView() {

        chatAdapter.notifyDataSetChanged();

        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

    }

    /**
     *
     * @param menu
     * @return
     * create friend menu items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.friend_chat_memu, menu);

        return true;

    }

    /**
     *
     * @param item
     * @return
     * add functionality for menu item when user click on one of those menu items
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == (R.id.video_icon)) {

            toVideoChatActivity();

        } else if (item.getItemId() == (R.id.pdf_icon)) {

            toSelectPdfActivity();

        } else if (item.getItemId() == (R.id.image_icon)) {

            toSelectImageActivity();

        } else if (item.getItemId() == (R.id.friend_location_icon)) {

            toShareLocationActivity();

        }

        return true;

    }

    /**
     * video/voice chat with a friend
     */
    private void toVideoChatActivity() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (DialogInterface.BUTTON_POSITIVE == which) {

                    sendCallRequest();

                    Intent videochatIntent = new Intent(FriendChatActivity.this, VideoChatViewActivity.class);

                    videochatIntent.putExtra("chatId", chatId);

                    startActivity(videochatIntent);
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to start video chat?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }

    /**
     * First ask for confirmation and then get the location from the user
     */
    private void toShareLocationActivity() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_POSITIVE == which) {

                    askGpsPermission();

                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to share your current location?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }

    /**
     * ask user for permission to use GPS
     */
    private void askGpsPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

        } else {

            getLocation();

        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * if user accepts, then get the current location
     * otherwise don't do anything
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {

            getLocation();

        } else {

            Toast.makeText(this, "Location service is disable!", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * get current location (lat, long) and send it to other user
     */
    private void getLocation() {

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if (location != null) {

                    String longitude = String.valueOf(location.getLongitude());

                    String latitude = String.valueOf(location.getLatitude());

                    String content = LOCATION_PREFIX + latitude + "," + longitude;

                    sendMsg(content, Chat.LOCATION_TYPE);

                }
            }
        });
    }

    /**
     * Let user select an image or take a picture and then crop the image to send to other user
     */
    private void toSelectImageActivity() {

        CropImage.activity().setAspectRatio(X_RATIO, Y_RATIO).start(this);

    }

    /**
     * Let user select an pdf file to send to other user
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
     * this method is invoked whenever a user finished selecting a file (image/pdf)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri pickedUri;

        if (data == null || resultCode != RESULT_OK) {

            return;

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            pickedUri = result.getUri();

            sendFile(Database.getImagesRef(), pickedUri, Chat.IMAGE_TYPE, Chat.IMAGE_EXTENSION);

        } else if (requestCode == PDF_REQUEST_CODE) {

            pickedUri = data.getData();

            sendFile(Database.getPdfsRef(), pickedUri, Chat.PDF_TYPE, Chat.PDF_EXTENSION);

        }

    }

    /**
     * send a text message to other user
     */
    private void sendTextMsg() {

        if (chatEditText.getText().length() == 0) {
            return;
        }

        String content = chatEditText.getText().toString();

        sendMsg(content, Chat.TEXT_TYPE);

        chatEditText.setText(EMPTY_STRING);

    }

    /**
     *
     * @param ref:  database reference
     * @param uri:  local directory of the file that user wants to send
     * @param type: could be "pdf", or "image"
     * @param extension: ".pdf", ".pdf"
     */
    private void sendFile(StorageReference ref, Uri uri, String type, String extension) {

        final ProgressDialog dialog = new ProgressDialog(FriendChatActivity.this);

        dialog.setMessage("Uploading...");

        dialog.show();

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

                        Toast.makeText(FriendChatActivity.this, "Uploading Successfully!", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }
                });

            }
        });

    }

    /**
     *
     * @param content: could be a text message or a URL link
     * @param type: could be "text", "pdf", "location", or "image"
     */
    private void sendMsg(String content, String type) {

        HashMap<String, Object> newMsg = new HashMap<>();

        String timeStamp = Utility.getTimeStamp();

        String sender = CurrentUser.getId();

        newMsg.put(Chat.CONTENT_KEY, content);

        newMsg.put(Chat.TYPE_KEY, type);

        newMsg.put(Chat.SENDER_KEY, sender);

        Database.getPrivateChatRef().child(chatId).child(timeStamp).setValue(newMsg);

    }

    /**
     * send a signal to database to let other user know that current user want to make a video sendCallRequest
     */
    private void sendCallRequest() {

        HashMap<String, Object> newValue = new HashMap<>();

        newValue.put(friendId, friendId);

        Database.getCallRequestRef()
                .child(friendId)
                .updateChildren(newValue);

    }

    /**
     *
     * @param dataSnapshot
     */
    private void displayCallDialog(DataSnapshot dataSnapshot) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dataSnapshot.getRef().removeValue();

                if (DialogInterface.BUTTON_POSITIVE == which) {

                    Intent videochatIntent = new Intent(FriendChatActivity.this, VideoChatViewActivity.class);

                    videochatIntent.putExtra("chatId", chatId);

                    startActivity(videochatIntent);

                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(FriendChatActivity.this);

        builder.setMessage(friendName + " is calling you!")
                .setPositiveButton( "Accept ",dialogClickListener)
                .setNegativeButton("Reject" , dialogClickListener)
                .show();

    }

}
