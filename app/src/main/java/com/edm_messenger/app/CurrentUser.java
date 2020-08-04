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

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/*
 * Class: CurrentUser.java
 * Description: Singleton class, provides info about current user of app, so that any other class can use it.
 */
public class CurrentUser {
    private static String name;
    private static String id;
    private static String status;
    private static String profilePicDir;
    private static CurrentUser user = null;
    private final static String STATUS_ONLINE = "online";

    public static CurrentUser getInstance(){
        return user == null ? new CurrentUser() : user;
    }

    // Constructor
    private CurrentUser(){

        // Get id - add listener to listen for changes in database and update fields accordingly.
        id = Database.getAuth().getCurrentUser().getUid();

        Database.getUsersRef().child(id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    name = dataSnapshot.child(User.NAME_KEY).getValue().toString();

                    profilePicDir = dataSnapshot.child(User.PROFILE_DIR_KEY).getValue().toString();

                    status = dataSnapshot.child(User.STATUS_KEY).getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
    }

    /**
     * set status to online when user logging in
     */
    public static void setStatusToOnline() {
        Database.getOnlineStatusRef().child(CurrentUser.getId()).setValue(STATUS_ONLINE);
    }

    /**
     * log the time when user logs out
     */
    public static void setStatusToOffline() {

        String currentTime = Utility.getTime(System.currentTimeMillis());

        Database.getOnlineStatusRef().child(CurrentUser.getId()).setValue(currentTime);

    }

    public static User toPerson (){
        return new User(name, status, profilePicDir);
    }

    // Getters and setters

    public static String getName() {
        return name;
    }

    public static String getId() {
        return id;
    }

    public static String getStatus() {
        return status;
    }

    public static String getProfilePicDir() {
        return profilePicDir;
    }

    public static void setName(String name) {
        CurrentUser.name = name;
    }

    public static void setStatus(String status) {
        CurrentUser.status = status;
    }

    public static void setProfilePicDir(String profilePicDir) {
        CurrentUser.profilePicDir = profilePicDir;
    }
}
