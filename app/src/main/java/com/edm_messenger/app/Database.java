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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 *  Class: Database.java
 *  Description: This class provides an easy way to access the fields in the database. Contains fields in database,
 *              fields in storage, and current auth.
 */
public class Database {

    // Get reference of database instance and storage instance
    final private static DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    final private static StorageReference STORAGE_REFERENCE = FirebaseStorage.getInstance().getReference();

    // Fields of Database
    final private static DatabaseReference usersRef = DATABASE_REFERENCE.child("USERS");
    final private static DatabaseReference groupsRef = DATABASE_REFERENCE.child("GROUPS");
    final private static DatabaseReference friendsRef = DATABASE_REFERENCE.child("FRIENDS");
    final private static DatabaseReference groupChatRef = DATABASE_REFERENCE.child("GROUP_CHATS");
    final private static DatabaseReference privateChatRef = DATABASE_REFERENCE.child("PRIVATE_CHATS");
    final private static DatabaseReference requestsRef = DATABASE_REFERENCE.child("REQUESTS");
    final private static DatabaseReference chatIdRef = DATABASE_REFERENCE.child("CHAT_ID");
    final private static DatabaseReference onlineStatusRef = DATABASE_REFERENCE.child("ONLINE_STATUS");
    final private static DatabaseReference sentRequestsRef = DATABASE_REFERENCE.child("SENT_REQUEST");
    final private static DatabaseReference callRequestRef = DATABASE_REFERENCE.child("CALL_REQUEST");

    // Fields of storage
    final private static StorageReference profileImagesRef = STORAGE_REFERENCE.child("PROFILE_IMAGES");
    final private static StorageReference imagesRef = STORAGE_REFERENCE.child("IMAGES");
    final private static StorageReference pdfsRef = STORAGE_REFERENCE.child("PDFS");

    // Current Auth
    final private static FirebaseAuth auth = FirebaseAuth.getInstance();

    // Getters and setters
    public static DatabaseReference getSentRequestsRef() {
        return sentRequestsRef;
    }

    public static DatabaseReference getOnlineStatusRef() {
        return onlineStatusRef;
    }

    public static DatabaseReference getRequestsRef() {
        return requestsRef;
    }

    public static DatabaseReference getUsersRef() {
        return usersRef;
    }

    public static DatabaseReference getGroupsRef() {
        return groupsRef;
    }

    public static DatabaseReference getFriendsRef() {
        return friendsRef;
    }

    public static DatabaseReference getGroupChatRef() {
        return groupChatRef;
    }

    public static DatabaseReference getPrivateChatRef() {
        return privateChatRef;
    }

    public static StorageReference getProfileImagesRef() {
        return profileImagesRef;
    }

    public static DatabaseReference getChatIdRef() {
        return chatIdRef;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static StorageReference getImagesRef() {
        return imagesRef;
    }

    public static StorageReference getPdfsRef() {
        return pdfsRef;
    }

    public static DatabaseReference getCallRequestRef() {
        return callRequestRef;
    }

}
