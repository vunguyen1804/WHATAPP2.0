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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.edm_messenger.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Class: MainActivity.java
 * Description: Handles all functionality of main page
 */
public class MainActivity extends AppCompatActivity {
    private static final String EMPTY_STRING = "";

    private CurrentUser currentUser;  // need to keep this
    private ImageView userProfPic;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        init();
    }

    @Override
    protected void onStart() {

        super.onStart();

        addOnClick();

        FirebaseUser current_user = Database.getAuth().getCurrentUser();

        if(current_user == null){

            toLoginActivity();

        }else{
            // get current user data from database
            currentUser = CurrentUser.getInstance();

            // Get all fields of current user
            Database.getUsersRef()
                    .child(CurrentUser.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            CurrentUser.setName(dataSnapshot.child(User.NAME_KEY).getValue().toString());

                            CurrentUser.setProfilePicDir(dataSnapshot.child(User.PROFILE_DIR_KEY).getValue().toString());

                            CurrentUser.setStatus(dataSnapshot.child(User.STATUS_KEY).getValue().toString());

                            Picasso.get().load(CurrentUser.getProfilePicDir())
                                    .placeholder(R.drawable.profile)
                                    .into(userProfPic);

                            userNameTextView.setText(CurrentUser.getName());

                            CurrentUser.setStatusToOnline();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });

        }

    }

    /**
     * Connect XML components to Java code
     */
    private void init() {

        //  Get all buttons/inputs/design elements from main activity page
        userNameTextView = (TextView) findViewById(R.id.user_name_text_view);

        TabController tabController = new TabController(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);

        Toolbar toolBar = (Toolbar)findViewById(R.id.tool_bar);

        setSupportActionBar(toolBar);

        getSupportActionBar().setTitle(EMPTY_STRING);

        viewPager.setAdapter(tabController);

        tabLayout.setupWithViewPager(viewPager);

        userProfPic = (CircleImageView)findViewById(R.id.main_profile_img);

    }

    // Add onClick listener when current user clicks at their profile picture
    private void addOnClick() {

        userProfPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                toProfileSettingActivity();
            }

        });

    }

    /**
     *
     * @param menu
     * @return
     * create menu items at main activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;

    }

    /**
     *
     * @param item each item in menu
     * @return
     * at functionality for each menu item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // what to do when each item is clicked
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.add_group_menu){

            joinOrCreateGroup();

        }else if(item.getItemId() == R.id.search_people_menu){

            toFriendSearchActivity();

        } else if(item.getItemId() == R.id.settings_menu){

            toProfileSettingActivity();

        } else if (item.getItemId() == R.id.search_group_menu){

            toGroupSearchActivity();

        } else if (item.getItemId() == R.id.logout_menu){

            logout();
        }

        return true;

    }

    /**
     * logging out: log the time that the current user logs out and transition to login activity.
     */
    private void logout() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(DialogInterface.BUTTON_POSITIVE == which){

                    Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                    CurrentUser.setStatusToOffline();

                    Database.getAuth().signOut();

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

                    startActivity(loginIntent);

                    finish();

                }
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setMessage("Do you want to log out?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }


    /**
     * if a group already exists -> join this group
     * otherwise create a new group
     */
    void joinOrCreateGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);

        editText.setHint("Enter a group name.");

        builder.setView(editText);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(DialogInterface.BUTTON_POSITIVE == which){

                    String groupName = editText.getText().toString();

                    if(groupName.isEmpty())
                        return;

                    saveGroup(groupName );

                    Toast.makeText(MainActivity.this, "Group " + groupName + " added", Toast.LENGTH_LONG).show();

                }
            }
        };

        builder.setTitle("Add Group")
                .setMessage("Join/Create a group")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }

    /**
     *
     * @param groupName
     * create group, add it, and save it to database.
     */
    private void saveGroup(String groupName) {

        DatabaseReference groupChatsRef = Database.getGroupChatRef().child(groupName);

        if(groupChatsRef.getKey().isEmpty())
            return;

        HashMap<String,Object> newValue = new HashMap<>();

        newValue.put(groupName, groupName);

        Database.getGroupsRef().child(CurrentUser.getId()).updateChildren(newValue);

    }

    /**
     * transiting to login activity
     */
    private void toLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

        startActivity(loginIntent);

    }

    /**
     * transitioning to profile setting activity
     */
    private void toProfileSettingActivity() {

        Intent profileSettingIntent = new Intent(MainActivity.this, ProfileSettings.class);

        startActivity(profileSettingIntent);

    }

    /**
     * transitioning to friend search activity
     */
    private void toFriendSearchActivity() {

        Intent searchIntent = new Intent(this, FriendSearchActivity.class );

        startActivity(searchIntent);

    }

    /**
     * transitioning to group search activity
     */
    private void toGroupSearchActivity() {

        Intent searchIntent = new Intent(this, GroupSearchActivity.class );

        startActivity(searchIntent);

    }

}