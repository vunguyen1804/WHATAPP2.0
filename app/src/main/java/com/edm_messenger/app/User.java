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

/**
 *  Class: User.java
 *  Description: This is the model class used by Firebase for user object
 */
public class User {

    // Fields of USer model - also keys of database to access it
    public final static String NAME_KEY = "name";
    public final static String STATUS_KEY = "status";
    public final static String PROFILE_DIR_KEY = "profilePicDir";
    private final static String DEFAULT_IMAGE_DIR = "unknown";
    private final static String DEFAULT_STATUS = "I am a new user";
    private String name;
    private String status;
    private String profilePicDir;

    // Empty constructor
    public User() {}

    // Overloaded constructor with 1 String parameter
    public User(String name){

        this.name = name;

        status = DEFAULT_STATUS;

        profilePicDir = DEFAULT_IMAGE_DIR;

    }

    // Overloaded constructor with 3 String parameters
    public User(String name, String status, String profilePicDir) {

        this.name = name;

        this.status = status;

        this.profilePicDir = profilePicDir;

    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePicDir() {
        return profilePicDir;
    }

    public void setProfilePicDir(String profilePicDir) {
        this.profilePicDir = profilePicDir;
    }
}
