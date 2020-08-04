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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Class: Utility.java
 *  Description: Provides common methods that other class use a lot - thus a "utility" class
 */
public class Utility {
    private final static String PATTERN = "MM/dd HH:mm";
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);
    private final static Date DATE = new Date();

    // Empty constructor
    public Utility(){}

    // Validation for username, email and password
    static boolean checkUserName(String username){
        return username.length() >= 2;
    }

    static boolean checkPassword(String password){
        return password.length() > 5;
    }

    // checks if format matches
    static boolean checkEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Getters
    public static String getTimeStamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getTime(String timeStamp){
        return getTime(Long.parseLong(timeStamp));
    }

    public static String getTime(long milliseconds){

        DATE.setTime(milliseconds);

        return DATE_FORMAT.format(DATE);

    }
}